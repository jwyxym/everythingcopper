package cy.jdkdigital.everythingcopper.init;

import cy.jdkdigital.everythingcopper.EverythingCopper;
import cy.jdkdigital.everythingcopper.util.CopperItemVariants;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.HashSet;
import java.util.Set;

public final class ModCreativeTabs {
	public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, EverythingCopper.MODID);
	public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EVERYTHING_COPPER = TABS.register("everythingcopper", () ->
		CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
			.title(Component.translatable("itemGroup.everythingcopper"))
			.icon(() -> new ItemStack(ModItems.COPPER_SWORD.get()))
			.displayItems((parameters, output) -> buildContents(output))
			.build());

	private ModCreativeTabs() {
	}

	private static void buildContents(CreativeModeTab.Output output) {
		Set<Item> variantItems = new HashSet<>(CopperItemVariants.weatheringItems());
		ModItems.ITEMS.getEntries().forEach(holder -> {
			if (!variantItems.contains(holder.get())) {
				output.accept(holder.get());
			}
		});
		output.accept(ModItems.COPPER_NUGGET.get());
		for (Item item : CopperItemVariants.weatheringItems()) {
			output.acceptAll(CopperItemVariants.all(item));
		}
		// These sets replace former mod registrations, so include their vanilla IDs as well.
		Blocks.COPPER_BARS.forEach(block -> output.accept(block));
		Blocks.COPPER_CHAIN.forEach(block -> output.accept(block));
		Blocks.COPPER_LANTERN.forEach(block -> output.accept(block));
		output.accept(Items.COPPER_GOLEM_SPAWN_EGG);
	}
}
