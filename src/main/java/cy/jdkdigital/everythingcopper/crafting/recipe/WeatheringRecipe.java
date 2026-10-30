package cy.jdkdigital.everythingcopper.crafting.recipe;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

// JEI display data; the weathering station does not use the crafting recipe registry.
public record WeatheringRecipe(Identifier id, ItemStack input, ItemStack output) {
}
