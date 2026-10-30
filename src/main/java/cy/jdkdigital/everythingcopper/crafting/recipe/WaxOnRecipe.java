package cy.jdkdigital.everythingcopper.crafting.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.everythingcopper.common.item.ICopperItem;
import cy.jdkdigital.everythingcopper.init.ModRecipeTypes;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class WaxOnRecipe extends CustomRecipe {
	public static final MapCodec<WaxOnRecipe> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
		Ingredient.CODEC.fieldOf("item").forGetter(recipe -> recipe.input),
		Ingredient.CODEC.fieldOf("wax").forGetter(recipe -> recipe.wax)
	).apply(builder, WaxOnRecipe::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, WaxOnRecipe> STREAM_CODEC = StreamCodec.composite(
		Ingredient.CONTENTS_STREAM_CODEC, recipe -> recipe.input,
		Ingredient.CONTENTS_STREAM_CODEC, recipe -> recipe.wax, WaxOnRecipe::new);
	public final Ingredient input;
	public final Ingredient wax;

	public WaxOnRecipe(Ingredient input, Ingredient wax) {
		this.input = input;
		this.wax = wax;
	}

	@Override
	public boolean matches(CraftingInput input, Level level) {
		int items = 0;
		int waxes = 0;
		for (int index = 0; index < input.size(); index++) {
			ItemStack stack = input.getItem(index);
			if (!stack.isEmpty()) {
				if (wax.test(stack)) {
					waxes++;
				} else if (this.input.test(stack) && ICopperItem.isCopperItem(stack) && !ICopperItem.isWaxed(stack)) {
					items++;
				} else {
					return false;
				}
			}
		}
		return items == 1 && waxes == 1;
	}

	@Override
	public ItemStack assemble(CraftingInput input) {
		for (int index = 0; index < input.size(); index++) {
			ItemStack stack = input.getItem(index);
			if (this.input.test(stack)) {
				ItemStack result = stack.copyWithCount(1);
				ICopperItem.setWaxed(result, true);
				return result;
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public RecipeSerializer<WaxOnRecipe> getSerializer() {
		return ModRecipeTypes.WAX_ON.get();
	}
}
