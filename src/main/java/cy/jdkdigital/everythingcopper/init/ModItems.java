package cy.jdkdigital.everythingcopper.init;

import cy.jdkdigital.everythingcopper.EverythingCopper;
import cy.jdkdigital.everythingcopper.common.item.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import cy.jdkdigital.everythingcopper.common.entity.CopperMinecart;
import net.minecraft.world.item.*;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems
{
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(EverythingCopper.MODID);

	public static final DeferredHolder<Item, ? extends Item> COPPER_SWORD = vanilla("copper_sword");
	public static final DeferredHolder<Item, ? extends Item> COPPER_SHOVEL = vanilla("copper_shovel");
	public static final DeferredHolder<Item, ? extends Item> COPPER_PICKAXE = vanilla("copper_pickaxe");
	public static final DeferredHolder<Item, ? extends Item> COPPER_AXE = vanilla("copper_axe");
	public static final DeferredHolder<Item, ? extends Item> COPPER_HOE = vanilla("copper_hoe");

	public static final DeferredHolder<Item, ? extends Item> COPPER_HELMET = vanilla("copper_helmet");
	public static final DeferredHolder<Item, ? extends Item> COPPER_CHESTPLATE = vanilla("copper_chestplate");
	public static final DeferredHolder<Item, ? extends Item> COPPER_LEGGINGS = vanilla("copper_leggings");
	public static final DeferredHolder<Item, ? extends Item> COPPER_BOOTS = vanilla("copper_boots");

	public static final DeferredHolder<Item, ? extends Item> COPPER_NUGGET = vanilla("copper_nugget");
	public static final DeferredHolder<Item, ? extends Item> COPPER_SHEARS = ITEMS.registerItem("copper_shears", properties -> new CopperShearsItem(properties.durability(200)));
	public static final DeferredHolder<Item, ? extends Item> COPPER_HORSE_ARMOR = vanilla("copper_horse_armor");

	public static final DeferredHolder<Item, ? extends Item> COPPER_MINECART = ITEMS.registerItem("copper_minecart", properties -> new CopperMinecartItem(CopperMinecart.Type.RIDEABLE, properties.stacksTo(1)));
	public static final DeferredHolder<Item, ? extends Item> COPPER_CHEST_MINECART = ITEMS.registerItem("chest_copper_minecart", properties -> new CopperMinecartItem(CopperMinecart.Type.CHEST, properties.stacksTo(1)));
	public static final DeferredHolder<Item, ? extends Item> COPPER_FURNACE_MINECART = ITEMS.registerItem("furnace_copper_minecart", properties -> new CopperMinecartItem(CopperMinecart.Type.FURNACE, properties.stacksTo(1)));
	public static final DeferredHolder<Item, ? extends Item> COPPER_TNT_MINECART = ITEMS.registerItem("tnt_copper_minecart", properties -> new CopperMinecartItem(CopperMinecart.Type.TNT, properties.stacksTo(1)));
	public static final DeferredHolder<Item, ? extends Item> COPPER_HOPPER_MINECART = ITEMS.registerItem("hopper_copper_minecart", properties -> new CopperMinecartItem(CopperMinecart.Type.HOPPER, properties.stacksTo(1)));
	private static DeferredHolder<Item, Item> vanilla(String path) {
		Identifier target = Identifier.withDefaultNamespace(path);
		ITEMS.addAlias(Identifier.fromNamespaceAndPath(EverythingCopper.MODID, path), target);
		return DeferredHolder.create(Registries.ITEM, target);
	}
}
