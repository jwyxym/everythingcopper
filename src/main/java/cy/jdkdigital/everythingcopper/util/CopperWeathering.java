package cy.jdkdigital.everythingcopper.util;

import cy.jdkdigital.everythingcopper.common.item.ICopperItem;
import net.minecraft.core.component.DataComponents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.WeatheringCopper;

import java.util.ArrayList;
import java.util.List;

public final class CopperWeathering {
	public static final int CHECK_INTERVAL = 20;
	private static final int MAX_CONTAINER_DEPTH = 64;

	private CopperWeathering() {
	}

	public static float chancePerCheck(float chancePerTick) {
		return (float) -Math.expm1(CHECK_INTERVAL * Math.log1p(-chancePerTick));
	}

	// Return the original reference when nothing changes, so callers only save or sync actual changes.
	public static ItemStack weatherStack(ItemStack stack, ServerLevel level) {
		return weatherStack(stack, level, 0);
	}

	private static ItemStack weatherStack(ItemStack stack, ServerLevel level, int depth) {
		if (stack.isEmpty() || depth >= MAX_CONTAINER_DEPTH) {
			return stack;
		}
		ItemStack result = stack;
		if (ICopperItem.isCopperItem(stack)) {
			if (ICopperItem.canAge(stack) && level.getRandom().nextFloat() < chancePerCheck(ICopperItem.getWeatheringChance(stack))) {
				result = stack.copy();
				ICopperItem.setAge(result, WeatheringUtils.nextState(ICopperItem.getAge(stack)));
			}
		} else if (stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof WeatheringCopper) {
			// Block items use separate vanilla/mod IDs for each stage, rather than equipment components.
			var next = WeatheringCopper.getNext(blockItem.getBlock());
			if (next.isPresent() && next.get().asItem() != Items.AIR
				&& level.getRandom().nextFloat() < chancePerCheck(ICopperItem.WEATHERING_CHANCE_PER_TICK)) {
				result = stack.transmuteCopy(next.get());
			}
		}

		ItemContainerContents contents = stack.get(DataComponents.CONTAINER);
		if (contents != null) {
			List<ItemStack> items = new ArrayList<>(contents.allItemsCopyStream().toList());
			if (weatherContents(items, level, depth + 1)) {
				if (result == stack) result = stack.copy();
				result.set(DataComponents.CONTAINER, ItemContainerContents.fromItems(items));
			}
		}
		BundleContents bundle = stack.get(DataComponents.BUNDLE_CONTENTS);
		if (bundle != null && !bundle.isEmpty()) {
			List<ItemStack> items = new ArrayList<>(bundle.itemCopyStream().toList());
			if (weatherContents(items, level, depth + 1)) {
				List<ItemStackTemplate> templates = items.stream().map(ItemStackTemplate::fromNonEmptyStack).toList();
				BundleContents.Mutable updated = new BundleContents.Mutable(new BundleContents(templates));
				if (bundle.getSelectedItemIndex() >= 0) updated.toggleSelectedItem(bundle.getSelectedItemIndex());
				if (result == stack) result = stack.copy();
				result.set(DataComponents.BUNDLE_CONTENTS, updated.toImmutable());
			}
		}
		return result;
	}

	private static boolean weatherContents(List<ItemStack> items, ServerLevel level, int depth) {
		boolean changed = false;
		for (int slot = 0; slot < items.size(); slot++) {
			ItemStack original = items.get(slot);
			ItemStack updated = weatherStack(original, level, depth);
			if (updated != original) {
				items.set(slot, updated);
				changed = true;
			}
		}
		return changed;
	}
}
