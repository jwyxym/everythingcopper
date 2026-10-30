package cy.jdkdigital.everythingcopper.crafting.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.everythingcopper.common.item.CopperMinecartItem;
import cy.jdkdigital.everythingcopper.init.ModRecipeTypes;
import cy.jdkdigital.everythingcopper.util.CopperItemVariants;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class CopperMinecartUpgradeRecipe extends CustomRecipe {
	public static final MapCodec<CopperMinecartUpgradeRecipe> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
		Ingredient.CODEC.fieldOf("item").forGetter(recipe -> recipe.input),
		Ingredient.CODEC.fieldOf("addition").forGetter(recipe -> recipe.addition),
		ItemStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.result)
	).apply(builder, CopperMinecartUpgradeRecipe::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, CopperMinecartUpgradeRecipe> STREAM_CODEC = StreamCodec.composite(
		Ingredient.CONTENTS_STREAM_CODEC, recipe -> recipe.input,
		Ingredient.CONTENTS_STREAM_CODEC, recipe -> recipe.addition,
		ItemStack.STREAM_CODEC, recipe -> recipe.result, CopperMinecartUpgradeRecipe::new);
	private final Ingredient input;
	private final Ingredient addition;
	private final ItemStack result;

	public CopperMinecartUpgradeRecipe(Ingredient input, Ingredient addition, ItemStack result) {
		this.input = input;
		this.addition = addition;
		this.result = result.copy();
	}

	@Override
	public boolean matches(CraftingInput input, Level level) {
		int carts = 0;
		int materials = 0;
		for (int index = 0; index < input.size(); index++) {
			ItemStack stack = input.getItem(index);
			if (stack.isEmpty()) continue;
			if (this.input.test(stack) && stack.getItem() instanceof CopperMinecartItem) carts++;
			else if (addition.test(stack)) materials++;
			else return false;
		}
		return carts == 1 && materials == 1;
	}

	@Override
	public ItemStack assemble(CraftingInput input) {
		for (int index = 0; index < input.size(); index++) {
			ItemStack stack = input.getItem(index);
			if (this.input.test(stack) && stack.getItem() instanceof CopperMinecartItem) {
				ItemStack output = CopperItemVariants.copyState(stack, result.copyWithCount(1));
				if (stack.has(DataComponents.CUSTOM_NAME)) output.set(DataComponents.CUSTOM_NAME, stack.get(DataComponents.CUSTOM_NAME));
				return output;
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public RecipeSerializer<CopperMinecartUpgradeRecipe> getSerializer() {
		return ModRecipeTypes.MINECART_UPGRADE.get();
	}
}
