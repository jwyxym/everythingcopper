package cy.jdkdigital.everythingcopper.common.entity;

import cy.jdkdigital.everythingcopper.init.ModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.minecart.MinecartSpawner;
import net.minecraft.world.level.Level;

public class CopperMinecartSpawner extends MinecartSpawner {
	public CopperMinecartSpawner(EntityType<? extends MinecartSpawner> type, Level level) {
		super(type, level);
	}

	public CopperMinecartSpawner(Level level, double x, double y, double z) {
		this(ModEntities.SPAWNER_COPPER_MINECART.get(), level);
		setPos(x, y, z);
	}

	@Override
	protected float getBlockSpeedFactor() {
		return super.getBlockSpeedFactor() * 1.1F;
	}

}
