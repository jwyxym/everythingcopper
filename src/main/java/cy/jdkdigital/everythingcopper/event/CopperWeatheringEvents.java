package cy.jdkdigital.everythingcopper.event;

import cy.jdkdigital.everythingcopper.EverythingCopper;
import cy.jdkdigital.everythingcopper.common.entity.ICopperMinecart;
import cy.jdkdigital.everythingcopper.common.item.ICopperItem;
import cy.jdkdigital.everythingcopper.util.CopperWeathering;
import cy.jdkdigital.everythingcopper.util.WeatheringUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Container;
import net.minecraft.world.RandomizableContainer;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.decoration.ItemFrame;
import net.minecraft.world.entity.npc.InventoryCarrier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.ContainerEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;

@EventBusSubscriber(modid = EverythingCopper.MODID)
public final class CopperWeatheringEvents {
	private CopperWeatheringEvents() {
	}

	@SubscribeEvent
	public static void tick(LevelTickEvent.Post event) {
		if (!(event.getLevel() instanceof ServerLevel level) || level.getGameTime() % CopperWeathering.CHECK_INTERVAL != 0) {
			return;
		}
		// Inventory and equipment/menu views can refer to the same stack; check it only once.
		Set<ItemStack> checked = Collections.newSetFromMap(new IdentityHashMap<>());
		for (Entity entity : level.getAllEntities()) {
			if (entity.isRemoved()) continue;
			if (entity instanceof Player player) {
				// Inventory includes the selected slot, all other hotbar slots, armor and offhand.
				tickContainer(player.getInventory(), level, checked);
				tickContainer(player.getEnderChestInventory(), level, checked);
				ItemStack carried = player.containerMenu.getCarried();
				ItemStack updated = weatherStack(carried, level, checked);
				if (updated != carried) player.containerMenu.setCarried(updated);
			} else if (entity instanceof LivingEntity living) {
				for (EquipmentSlot slot : EquipmentSlot.values()) {
					ItemStack original = living.getItemBySlot(slot);
					ItemStack updated = weatherStack(original, level, checked);
					if (updated != original) living.setItemSlot(slot, updated);
				}
			}
			if (entity instanceof Container container) tickContainer(container, level, checked);
			if (entity instanceof InventoryCarrier carrier) tickContainer(carrier.getInventory(), level, checked);
			if (entity instanceof AbstractHorse horse) {
				for (int slot = 0; slot < horse.getInventorySize(); slot++) {
					tickSlot(horse.getSlot(AbstractHorse.INVENTORY_SLOT_OFFSET + slot), level, checked);
				}
			}
			if (entity instanceof ItemFrame frame) {
				ItemStack original = frame.getItem();
				ItemStack updated = weatherStack(original, level, checked);
				if (updated != original) frame.setItem(updated, false);
			}
			if (entity instanceof Display.ItemDisplay display) tickSlot(display.getSlot(0), level, checked);
			if (entity instanceof ICopperMinecart cart && !cart.isCopperWaxed()
				&& cart.getCopperAge() != WeatheringCopper.WeatherState.OXIDIZED
				&& level.getRandom().nextFloat() < CopperWeathering.chancePerCheck(ICopperItem.WEATHERING_CHANCE_PER_TICK)) {
				cart.setCopperAge(WeatheringUtils.nextState(cart.getCopperAge()));
			}
			// Dropped ItemEntity stacks deliberately do not participate in weathering.
		}
		// Visit loaded, ticking chunks only, without loading chunks elsewhere in the world.
		level.getChunkSource().chunkMap.forEachBlockTickingChunk(chunk -> {
			for (BlockPos pos : new ArrayList<>(chunk.getBlockEntitiesPos())) {
				BlockEntity blockEntity = chunk.getBlockEntity(pos);
				if (blockEntity instanceof Container container && !blockEntity.isRemoved()) {
					if (tickContainer(container, level, checked)) blockEntity.setChanged();
				}
			}
		});
	}

	private static boolean tickContainer(Container container, ServerLevel level, Set<ItemStack> checked) {
		// Do not generate loot in unopened structure containers just to look for copper.
		if (container instanceof RandomizableContainer randomizable && randomizable.getLootTable() != null) return false;
		if (container instanceof ContainerEntity vehicle && vehicle.getContainerLootTable() != null) return false;
		boolean changed = false;
		for (int slot = 0; slot < container.getContainerSize(); slot++) {
			ItemStack original = container.getItem(slot);
			ItemStack updated = weatherStack(original, level, checked);
			if (updated != original) {
				container.setItem(slot, updated);
				changed = true;
			}
		}
		if (changed) container.setChanged();
		return changed;
	}

	private static void tickSlot(SlotAccess slot, ServerLevel level, Set<ItemStack> checked) {
		ItemStack original = slot.get();
		ItemStack updated = weatherStack(original, level, checked);
		if (updated != original) slot.set(updated);
	}

	private static ItemStack weatherStack(ItemStack stack, ServerLevel level, Set<ItemStack> checked) {
		if (stack.isEmpty() || !checked.add(stack)) return stack;
		ItemStack result = CopperWeathering.weatherStack(stack, level);
		checked.add(result);
		return result;
	}
}
