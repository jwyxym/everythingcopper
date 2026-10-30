package cy.jdkdigital.everythingcopper.datagen.recipes;

import cy.jdkdigital.everythingcopper.EverythingCopper;
import cy.jdkdigital.everythingcopper.init.ModBlocks;
import cy.jdkdigital.everythingcopper.init.ModItems;
import cy.jdkdigital.everythingcopper.init.ModTags;
import net.minecraft.advancements.triggers.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

public class CopperRecipeProvider extends RecipeProvider
{
	public CopperRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
		super(registries, output);
	}

    @Override
	protected void buildRecipes() {
        // Blocks
		shaped(RecipeCategory.MISC, ModBlocks.COPPER_ANVIL.get(), 1)
                .pattern("BBB")
                .pattern(" i ")
                .pattern("iii")
                .define('B', Tags.Items.STORAGE_BLOCKS_COPPER)
                .define('i', Tags.Items.INGOTS_COPPER)
                .group(EverythingCopper.MODID)
				.unlockedBy("copper", InventoryChangeTrigger.TriggerInstance.hasItems(Items.COPPER_BLOCK.weathering().unaffected(), Items.COPPER_INGOT))
				.save(output);

		shaped(RecipeCategory.MISC, ModBlocks.COPPER_BUTTON.get(), 1)
                .pattern("nnn")
                .pattern("nnn")
                .define('n', ModTags.COPPER_NUGGET)
                .group(EverythingCopper.MODID)
                .unlockedBy("copper", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.COPPER_NUGGET.get()))
				.save(output);

		shaped(RecipeCategory.MISC, ModBlocks.COPPER_HOPPER.get(), 1)
                .pattern("i i")
                .pattern("iCi")
                .pattern(" i ")
                .define('i', Tags.Items.INGOTS_COPPER)
                .define('C', Tags.Items.CHESTS)
                .group(EverythingCopper.MODID)
                .unlockedBy("copper", InventoryChangeTrigger.TriggerInstance.hasItems(Items.COPPER_INGOT))
				.save(output);

		shaped(RecipeCategory.MISC, ModBlocks.COPPER_LADDER.get(), 16)
                .pattern("i i")
                .pattern("iii")
                .pattern("i i")
                .define('i', Tags.Items.INGOTS_COPPER)
                .group(EverythingCopper.MODID)
                .unlockedBy("copper", InventoryChangeTrigger.TriggerInstance.hasItems(Items.COPPER_INGOT))
				.save(output);

		shaped(RecipeCategory.MISC, ModBlocks.COPPER_PRESSURE_PLATE.get())
                .pattern("ii")
                .define('i', Tags.Items.INGOTS_COPPER)
                .group(EverythingCopper.MODID)
                .unlockedBy("copper", InventoryChangeTrigger.TriggerInstance.hasItems(Items.COPPER_INGOT))
				.save(output);

		shaped(RecipeCategory.MISC, ModBlocks.COPPER_RAIL.get(), 16)
                .pattern("i i")
                .pattern("isi")
                .pattern("i i")
                .define('s', Tags.Items.RODS_WOODEN)
                .define('i', Tags.Items.INGOTS_COPPER)
                .group(EverythingCopper.MODID)
                .unlockedBy("copper", InventoryChangeTrigger.TriggerInstance.hasItems(Items.COPPER_INGOT))
				.save(output);

		shaped(RecipeCategory.MISC, ModBlocks.COPPER_SOUL_LANTERN.get(), 1)
                .pattern("nnn")
                .pattern("ntn")
                .pattern("nnn")
                .define('t', Items.SOUL_TORCH)
                .define('n', ModTags.COPPER_NUGGET)
                .group(EverythingCopper.MODID)
                .unlockedBy("copper", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.COPPER_NUGGET.get(), Items.SOUL_TORCH))
				.save(output);

        // Items

		shaped(RecipeCategory.MISC, ModItems.COPPER_SHEARS.get())
                .pattern("i ")
                .pattern(" i")
                .define('i', Tags.Items.INGOTS_COPPER)
                .group(EverythingCopper.MODID)
                .unlockedBy("copper", InventoryChangeTrigger.TriggerInstance.hasItems(Items.COPPER_INGOT))
				.save(output);

		shaped(RecipeCategory.MISC, ModItems.COPPER_MINECART.get())
                .pattern("i i")
                .pattern("iii")
                .define('i', Tags.Items.INGOTS_COPPER)
                .group(EverythingCopper.MODID)
                .unlockedBy("copper", InventoryChangeTrigger.TriggerInstance.hasItems(Items.COPPER_INGOT))
				.save(output);

        // Waxing
        /// I'm too bored for that
    }
	public static class Runner extends RecipeProvider.Runner {
		public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) { super(output, registries); }
		@Override
		protected RecipeProvider createRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
			return new CopperRecipeProvider(registries, output);
		}
		@Override
		public String getName() { return "Everything is Copper recipes"; }
	}
}
