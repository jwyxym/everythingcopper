package cy.jdkdigital.everythingcopper.datagen;

import cy.jdkdigital.everythingcopper.init.ModBlocks;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;
import net.neoforged.neoforge.registries.datamaps.builtin.Oxidizable;
import net.neoforged.neoforge.registries.datamaps.builtin.Waxable;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class DataMapProvider extends net.neoforged.neoforge.common.data.DataMapProvider {
	protected DataMapProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
		super(output, registries);
	}

	@Override
	protected void gather(HolderLookup.Provider provider) {
		var oxidizables = builder(NeoForgeDataMaps.OXIDIZABLES);
		var waxables = builder(NeoForgeDataMaps.WAXABLES);
		Set<Identifier> registered = ModBlocks.BLOCKS.getEntries().stream().map(holder -> holder.getId()).collect(Collectors.toSet());
		for (Identifier id : registered) {
			String name = id.getPath();
			if (name.startsWith("waxed_") || !name.contains("copper_")) {
				continue;
			}
			String next = name.contains("oxidized_") ? null : name.contains("weathered_") ? name.replace("weathered_", "oxidized_")
				: name.contains("exposed_") ? name.replace("exposed_", "weathered_") : name.replace("copper_", "exposed_copper_");
			if (next != null && registered.contains(id.withPath(next))) {
				oxidizables.add(id, new Oxidizable(BuiltInRegistries.BLOCK.getValue(id.withPath(next))), false);
			}
			Identifier waxed = id.withPath("waxed_" + name);
			if (registered.contains(waxed)) {
				waxables.add(id, new Waxable(BuiltInRegistries.BLOCK.getValue(waxed)), false);
			}
		}
	}
}
