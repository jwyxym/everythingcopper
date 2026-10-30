package cy.jdkdigital.everythingcopper.event;

import cy.jdkdigital.everythingcopper.EverythingCopper;
import cy.jdkdigital.everythingcopper.init.ModBlocks;
import cy.jdkdigital.everythingcopper.init.ModItems;
import cy.jdkdigital.everythingcopper.common.item.CopperMobBucketItem;
import cy.jdkdigital.everythingcopper.util.CopperItemVariants;
import net.minecraft.world.item.CreativeModeTabs;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

public class EventHandler
{
    public static void buildContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().equals(CreativeModeTabs.COMBAT)) {
        }
        if (event.getTabKey().equals(CreativeModeTabs.TOOLS_AND_UTILITIES)) {
            event.accept(ModItems.COPPER_SHEARS.get());
			event.accept(ModItems.COPPER_BUCKET.get());
			event.accept(ModItems.COPPER_WATER_BUCKET.get());
			event.accept(ModItems.COPPER_LAVA_BUCKET.get());
			event.accept(ModItems.COPPER_POWDER_SNOW_BUCKET.get());
			event.accept(ModItems.COPPER_MILK_BUCKET.get());
			for (var bucket : CopperItemVariants.buckets()) {
				if (bucket instanceof CopperMobBucketItem) event.accept(bucket);
			}
        }
        if (event.getTabKey().equals(CreativeModeTabs.INGREDIENTS)) {
        }
        if (event.getTabKey().equals(CreativeModeTabs.BUILDING_BLOCKS)) {
            event.accept(ModBlocks.COPPER_PRESSURE_PLATE.get());
            event.accept(ModBlocks.EXPOSED_COPPER_PRESSURE_PLATE.get());
            event.accept(ModBlocks.WEATHERED_COPPER_PRESSURE_PLATE.get());
            event.accept(ModBlocks.OXIDIZED_COPPER_PRESSURE_PLATE.get());
            event.accept(ModBlocks.COPPER_BUTTON.get());
            event.accept(ModBlocks.EXPOSED_COPPER_BUTTON.get());
            event.accept(ModBlocks.WEATHERED_COPPER_BUTTON.get());
            event.accept(ModBlocks.OXIDIZED_COPPER_BUTTON.get());

            event.accept(ModBlocks.WAXED_COPPER_PRESSURE_PLATE.get());
            event.accept(ModBlocks.WAXED_EXPOSED_COPPER_PRESSURE_PLATE.get());
            event.accept(ModBlocks.WAXED_WEATHERED_COPPER_PRESSURE_PLATE.get());
            event.accept(ModBlocks.WAXED_OXIDIZED_COPPER_PRESSURE_PLATE.get());
            event.accept(ModBlocks.WAXED_COPPER_BUTTON.get());
            event.accept(ModBlocks.WAXED_EXPOSED_COPPER_BUTTON.get());
            event.accept(ModBlocks.WAXED_WEATHERED_COPPER_BUTTON.get());
            event.accept(ModBlocks.WAXED_OXIDIZED_COPPER_BUTTON.get());
        }
        if (event.getTabKey().equals(CreativeModeTabs.REDSTONE_BLOCKS)) {
            event.accept(ModBlocks.COPPER_HOPPER.get());
            event.accept(ModBlocks.EXPOSED_COPPER_HOPPER.get());
            event.accept(ModBlocks.WEATHERED_COPPER_HOPPER.get());
            event.accept(ModBlocks.OXIDIZED_COPPER_HOPPER.get());
            event.accept(ModBlocks.COPPER_RAIL.get());
            event.accept(ModBlocks.EXPOSED_COPPER_RAIL.get());
            event.accept(ModBlocks.WEATHERED_COPPER_RAIL.get());
            event.accept(ModBlocks.OXIDIZED_COPPER_RAIL.get());

            event.accept(ModBlocks.WAXED_COPPER_HOPPER.get());
            event.accept(ModBlocks.WAXED_EXPOSED_COPPER_HOPPER.get());
            event.accept(ModBlocks.WAXED_WEATHERED_COPPER_HOPPER.get());
            event.accept(ModBlocks.WAXED_OXIDIZED_COPPER_HOPPER.get());
            event.accept(ModBlocks.WAXED_COPPER_RAIL.get());
            event.accept(ModBlocks.WAXED_EXPOSED_COPPER_RAIL.get());
            event.accept(ModBlocks.WAXED_WEATHERED_COPPER_RAIL.get());
            event.accept(ModBlocks.WAXED_OXIDIZED_COPPER_RAIL.get());
        }
        if (event.getTabKey().equals(CreativeModeTabs.FUNCTIONAL_BLOCKS)) {
            event.accept(ModBlocks.COPPER_SOUL_LANTERN.get());
            event.accept(ModBlocks.EXPOSED_COPPER_SOUL_LANTERN.get());
            event.accept(ModBlocks.WEATHERED_COPPER_SOUL_LANTERN.get());
            event.accept(ModBlocks.OXIDIZED_COPPER_SOUL_LANTERN.get());
            event.accept(ModBlocks.COPPER_ANVIL.get());
            event.accept(ModBlocks.EXPOSED_COPPER_ANVIL.get());
            event.accept(ModBlocks.WEATHERED_COPPER_ANVIL.get());
            event.accept(ModBlocks.OXIDIZED_COPPER_ANVIL.get());
            event.accept(ModBlocks.CHIPPED_COPPER_ANVIL.get());
            event.accept(ModBlocks.CHIPPED_EXPOSED_COPPER_ANVIL.get());
            event.accept(ModBlocks.CHIPPED_WEATHERED_COPPER_ANVIL.get());
            event.accept(ModBlocks.CHIPPED_OXIDIZED_COPPER_ANVIL.get());
            event.accept(ModBlocks.DAMAGED_COPPER_ANVIL.get());
            event.accept(ModBlocks.DAMAGED_EXPOSED_COPPER_ANVIL.get());
            event.accept(ModBlocks.DAMAGED_WEATHERED_COPPER_ANVIL.get());
            event.accept(ModBlocks.DAMAGED_OXIDIZED_COPPER_ANVIL.get());
            event.accept(ModBlocks.COPPER_LADDER.get());
            event.accept(ModBlocks.EXPOSED_COPPER_LADDER.get());
            event.accept(ModBlocks.WEATHERED_COPPER_LADDER.get());
            event.accept(ModBlocks.OXIDIZED_COPPER_LADDER.get());

            event.accept(ModBlocks.WAXED_COPPER_SOUL_LANTERN.get());
            event.accept(ModBlocks.WAXED_EXPOSED_COPPER_SOUL_LANTERN.get());
            event.accept(ModBlocks.WAXED_WEATHERED_COPPER_SOUL_LANTERN.get());
            event.accept(ModBlocks.WAXED_OXIDIZED_COPPER_SOUL_LANTERN.get());
            event.accept(ModBlocks.WAXED_COPPER_ANVIL.get());
            event.accept(ModBlocks.WAXED_EXPOSED_COPPER_ANVIL.get());
            event.accept(ModBlocks.WAXED_WEATHERED_COPPER_ANVIL.get());
            event.accept(ModBlocks.WAXED_OXIDIZED_COPPER_ANVIL.get());
            event.accept(ModBlocks.WAXED_CHIPPED_COPPER_ANVIL.get());
            event.accept(ModBlocks.WAXED_CHIPPED_EXPOSED_COPPER_ANVIL.get());
            event.accept(ModBlocks.WAXED_CHIPPED_WEATHERED_COPPER_ANVIL.get());
            event.accept(ModBlocks.WAXED_CHIPPED_OXIDIZED_COPPER_ANVIL.get());
            event.accept(ModBlocks.WAXED_DAMAGED_COPPER_ANVIL.get());
            event.accept(ModBlocks.WAXED_DAMAGED_EXPOSED_COPPER_ANVIL.get());
            event.accept(ModBlocks.WAXED_DAMAGED_WEATHERED_COPPER_ANVIL.get());
            event.accept(ModBlocks.WAXED_DAMAGED_OXIDIZED_COPPER_ANVIL.get());
            event.accept(ModBlocks.WAXED_COPPER_LADDER.get());
            event.accept(ModBlocks.WAXED_EXPOSED_COPPER_LADDER.get());
            event.accept(ModBlocks.WAXED_WEATHERED_COPPER_LADDER.get());
            event.accept(ModBlocks.WAXED_OXIDIZED_COPPER_LADDER.get());
        }
    }
}
