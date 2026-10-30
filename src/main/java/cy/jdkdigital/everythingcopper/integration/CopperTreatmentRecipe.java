package cy.jdkdigital.everythingcopper.integration;

import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;

public record CopperTreatmentRecipe(Identifier id, ItemStack input, ItemStack output, Operation operation) {
	public enum Operation {
		WAX_ON("wax_on"),
		WAX_OFF("wax_off"),
		SCRAPE("scrape");

		private final String path;

		Operation(String path) {
			this.path = path;
		}

		public String path() {
			return path;
		}
	}
}
