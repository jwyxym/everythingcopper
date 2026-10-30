package cy.jdkdigital.everythingcopper.event;

import cy.jdkdigital.everythingcopper.EverythingCopper;
import cy.jdkdigital.everythingcopper.common.entity.ICopperMinecart;
import cy.jdkdigital.everythingcopper.util.WeatheringUtils;
import net.minecraft.world.level.block.WeatheringCopper;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

@EventBusSubscriber(modid = EverythingCopper.MODID)
public class CopperMinecartEvents {
	@SubscribeEvent
	public static void tick(EntityTickEvent.Post event) {
		var entity = event.getEntity();
		if (!entity.level().isClientSide() && entity instanceof ICopperMinecart copper
			&& entity.tickCount % 369 == 0 && !copper.isCopperWaxed()
			&& copper.getCopperAge() != WeatheringCopper.WeatherState.OXIDIZED
			&& entity.level().getRandom().nextFloat() < 0.005688889F) {
			copper.setCopperAge(WeatheringUtils.nextState(copper.getCopperAge()));
		}
	}
}
