package cy.jdkdigital.everythingcopper.common.entity;

import cy.jdkdigital.everythingcopper.init.ModEntities;
import cy.jdkdigital.everythingcopper.init.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.minecart.MinecartFurnace;
import net.minecraft.world.level.Level;

public class CopperMinecartFurnace extends MinecartFurnace {
	public CopperMinecartFurnace(EntityType<? extends MinecartFurnace> type, Level level) {
		super(type, level);
	}

	public CopperMinecartFurnace(Level level, double x, double y, double z) {
		this(ModEntities.FURNACE_COPPER_MINECART.get(), level);
		setPos(x, y, z);
	}

	@Override
	protected float getBlockSpeedFactor() {
		return super.getBlockSpeedFactor() * 1.1F;
	}

	@Override
	protected Item getDropItem() {
		return ModItems.COPPER_FURNACE_MINECART.get();
	}

	@Override
	public ItemStack getPickResult() {
		return new ItemStack(getDropItem());
	}
}
