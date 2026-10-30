package cy.jdkdigital.everythingcopper.crafting.recipe;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import cy.jdkdigital.everythingcopper.common.item.ICopperItem;
import cy.jdkdigital.everythingcopper.init.ModRecipeTypes;
import cy.jdkdigital.everythingcopper.util.WeatheringUtils;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class ScrapeRecipe extends CustomRecipe {
	public static final MapCodec<ScrapeRecipe> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
		Ingredient.CODEC.fieldOf("item").forGetter(recipe -> recipe.input)
	).apply(builder, ScrapeRecipe::new));
	public static final StreamCodec<RegistryFriendlyByteBuf, ScrapeRecipe> STREAM_CODEC = StreamCodec.composite(
		Ingredient.CONTENTS_STREAM_CODEC, recipe -> recipe.input, ScrapeRecipe::new);
	public final Ingredient input;

	public ScrapeRecipe(Ingredient input) {
		this.input = input;
	}

	@Override
	public boolean matches(CraftingInput input, Level level) {
		int count = 0;
		for (int index = 0; index < input.size(); index++) {
			ItemStack stack = input.getItem(index);
			if (!stack.isEmpty()) {
				if (!this.input.test(stack) || !ICopperItem.isCopperItem(stack)
					|| (!ICopperItem.isWaxed(stack) && ICopperItem.getAge(stack).equals("unaffected"))) {
					return false;
				}
				count++;
			}
		}
		return count == 1;
	}

	@Override
	public ItemStack assemble(CraftingInput input) {
		for (int index = 0; index < input.size(); index++) {
			ItemStack stack = input.getItem(index);
			if (this.input.test(stack)) {
				ItemStack result = stack.copyWithCount(1);
				if (ICopperItem.isWaxed(result)) {
					ICopperItem.setWaxed(result, false);
				} else {
					ICopperItem.setAge(result, WeatheringUtils.prevState(ICopperItem.getAge(result)));
				}
				return result;
			}
		}
		return ItemStack.EMPTY;
	}

	@Override
	public RecipeSerializer<ScrapeRecipe> getSerializer() {
		return ModRecipeTypes.SCRAPE.get();
	}
}
