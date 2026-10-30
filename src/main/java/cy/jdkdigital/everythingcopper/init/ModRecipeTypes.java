package cy.jdkdigital.everythingcopper.init;

import cy.jdkdigital.everythingcopper.EverythingCopper;
import cy.jdkdigital.everythingcopper.crafting.recipe.ScrapeRecipe;
import cy.jdkdigital.everythingcopper.crafting.recipe.WaxOnRecipe;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModRecipeTypes
{
    public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, EverythingCopper.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(BuiltInRegistries.RECIPE_SERIALIZER, EverythingCopper.MODID);

	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<WaxOnRecipe>> WAX_ON = RECIPE_SERIALIZERS.register("wax_on", () -> new RecipeSerializer<>(WaxOnRecipe.CODEC, WaxOnRecipe.STREAM_CODEC));
	public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<ScrapeRecipe>> SCRAPE = RECIPE_SERIALIZERS.register("scrape", () -> new RecipeSerializer<>(ScrapeRecipe.CODEC, ScrapeRecipe.STREAM_CODEC));
}
