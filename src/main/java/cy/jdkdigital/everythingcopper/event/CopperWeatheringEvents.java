package cy.jdkdigital.everythingcopper.event;

import cy.jdkdigital.everythingcopper.EverythingCopper;
import cy.jdkdigital.everythingcopper.common.entity.ICopperMinecart;
import cy.jdkdigital.everythingcopper.common.item.ICopperItem;
import cy.jdkdigital.everythingcopper.util.CopperWeathering;
import cy.jdkdigital.everythingcopper.util.WeatheringUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.WeatheringCopper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.LevelTickEvent;

@EventBusSubscriber(modid = EverythingCopper.MODID)
public final class CopperWeatheringEvents {
	private CopperWeatheringEvents() {
	}

	@SubscribeEvent
	public static void tick(LevelTickEvent.Post event) {
		if (!(event.getLevel() instanceof ServerLevel level) || level.getGameTime() % CopperWeathering.CHECK_INTERVAL != 0) return;
		for (Entity entity : level.getAllEntities()) {
			if (entity.isRemoved()) continue;
			if (entity instanceof LivingEntity living) {
				for (EquipmentSlot slot : EquipmentSlot.values()) {
					ItemStack stack = living.getItemBySlot(slot);
					// Refresh stored tool components on existing pickaxes when they are equipped.
					if (stack.is(Items.COPPER_PICKAXE)) CopperEquipmentEvents.updateAppearance(stack);
					if (CopperWeathering.isWornArmor(stack, slot) && ICopperItem.canAge(stack)
						&& level.getRandom().nextFloat() < CopperWeathering.chancePerCheck(level, ICopperItem.getAge(stack))) {
						ItemStack updated = stack.copy();
						ICopperItem.setAge(updated, WeatheringUtils.nextState(ICopperItem.getAge(stack)));
						living.setItemSlot(slot, updated);
					}
				}
			}
			if (entity instanceof ICopperMinecart cart && !cart.isCopperWaxed()
				&& cart.getCopperAge() != WeatheringCopper.WeatherState.OXIDIZED
				&& level.getRandom().nextFloat() < CopperWeathering.chancePerCheck(level, cart.getCopperAge().getSerializedName())) {
				cart.setCopperAge(WeatheringUtils.nextState(cart.getCopperAge()));
			}
		}
	}
}
