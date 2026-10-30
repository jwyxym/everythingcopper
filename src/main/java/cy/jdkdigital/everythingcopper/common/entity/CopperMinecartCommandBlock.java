package cy.jdkdigital.everythingcopper.common.entity;

import cy.jdkdigital.everythingcopper.init.ModEntities;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.minecart.MinecartCommandBlock;
import net.minecraft.world.level.Level;

public class CopperMinecartCommandBlock extends MinecartCommandBlock {
	public CopperMinecartCommandBlock(EntityType<? extends MinecartCommandBlock> type, Level level) {
		super(type, level);
	}

	public CopperMinecartCommandBlock(Level level, double x, double y, double z) {
		this(ModEntities.COMMAND_BLOCK_COPPER_MINECART.get(), level);
		setPos(x, y, z);
	}

	@Override
	protected float getBlockSpeedFactor() {
		return super.getBlockSpeedFactor() * 1.1F;
	}

}
