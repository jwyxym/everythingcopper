package cy.jdkdigital.everythingcopper.init;

import cy.jdkdigital.everythingcopper.EverythingCopper;
import cy.jdkdigital.everythingcopper.util.CopperItemVariants;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
		Map<String, List<Block>> blockFamilies = new LinkedHashMap<>();
		ModBlocks.BLOCKS.getEntries().forEach(holder -> {
			Block block = holder.get();
			String family = holder.getId().getPath()
				.replaceFirst("^waxed_", "")
				.replaceFirst("(?:exposed_|weathered_|oxidized_)(?=copper_)", "");
			blockFamilies.computeIfAbsent(family, key -> new ArrayList<>()).add(block);
			variantItems.add(block.asItem());
		});
		blockFamilies.values().forEach(blocks -> addBlockVariants(output, blocks));
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
		addBlockVariants(output, Blocks.COPPER_BARS.asList());
		addBlockVariants(output, Blocks.COPPER_CHAIN.asList());
		addBlockVariants(output, Blocks.COPPER_LANTERN.asList());
	}

	private static void addBlockVariants(CreativeModeTab.Output output, List<Block> blocks) {
		blocks.stream().sorted(Comparator.comparingInt(ModCreativeTabs::blockVariantOrder)).forEach(output::accept);
	}

	private static int blockVariantOrder(Block block) {
		String name = BuiltInRegistries.BLOCK.getKey(block).getPath();
		int age = name.contains("oxidized_copper_") ? 3
			: name.contains("weathered_copper_") ? 2
			: name.contains("exposed_copper_") ? 1 : 0;
		return age * 2 + (name.startsWith("waxed_") ? 1 : 0);
	}
}
