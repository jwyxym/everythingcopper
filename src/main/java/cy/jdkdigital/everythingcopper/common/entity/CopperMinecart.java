package cy.jdkdigital.everythingcopper.common.entity;

import cy.jdkdigital.everythingcopper.init.ModEntities;
import cy.jdkdigital.everythingcopper.init.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.entity.vehicle.minecart.Minecart;
import net.minecraft.world.level.Level;

public class CopperMinecart extends Minecart {
	public CopperMinecart(EntityType<?> type, Level level) {
		super(type, level);
	}

	public CopperMinecart(Level level, double x, double y, double z) {
		this(ModEntities.COPPER_MINECART.get(), level);
		setPos(x, y, z);
	}

	@Override
	protected float getBlockSpeedFactor() {
		return super.getBlockSpeedFactor() * 1.1F;
	}

	public static AbstractMinecart createMinecart(Level level, double x, double y, double z, Type type) {
		if (type.equals(Type.CHEST)) {
            return new CopperMinecartChest(level, x, y, z);
		} else if (type.equals(Type.FURNACE)) {
            return new CopperMinecartFurnace(level, x, y, z);
		} else if (type.equals(Type.TNT)) {
            return new CopperMinecartTNT(level, x, y, z);
		} else if (type.equals(Type.SPAWNER)) {
            return new CopperMinecartSpawner(level, x, y, z);
		} else if (type.equals(Type.HOPPER)) {
            return new CopperMinecartHopper(level, x, y, z);
        } else {
            return type == Type.COMMAND_BLOCK ? new CopperMinecartCommandBlock(level, x, y, z) : new CopperMinecart(level, x, y, z);
        }
    }
	public enum Type { RIDEABLE, CHEST, FURNACE, TNT, SPAWNER, HOPPER, COMMAND_BLOCK }

	@Override
	protected Item getDropItem() {
		return ModItems.COPPER_MINECART.get();
	}

	@Override
	public ItemStack getPickResult() {
		return new ItemStack(getDropItem());
	}
}
