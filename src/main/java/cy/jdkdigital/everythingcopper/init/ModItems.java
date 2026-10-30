package cy.jdkdigital.everythingcopper.init;

import cy.jdkdigital.everythingcopper.EverythingCopper;
import cy.jdkdigital.everythingcopper.common.item.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import cy.jdkdigital.everythingcopper.common.entity.CopperMinecart;
import net.minecraft.world.item.*;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.Consumables;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.sounds.SoundEvents;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems
{
	public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(EverythingCopper.MODID);

	public static final DeferredHolder<Item, ? extends Item> COPPER_SWORD = vanilla("copper_sword");
	public static final DeferredHolder<Item, ? extends Item> COPPER_SPEAR = vanilla("copper_spear");
	public static final DeferredHolder<Item, ? extends Item> COPPER_SHOVEL = vanilla("copper_shovel");
	public static final DeferredHolder<Item, ? extends Item> COPPER_PICKAXE = vanilla("copper_pickaxe");
	public static final DeferredHolder<Item, ? extends Item> COPPER_AXE = vanilla("copper_axe");
	public static final DeferredHolder<Item, ? extends Item> COPPER_HOE = vanilla("copper_hoe");

	public static final DeferredHolder<Item, ? extends Item> COPPER_HELMET = vanilla("copper_helmet");
	public static final DeferredHolder<Item, ? extends Item> COPPER_CHESTPLATE = vanilla("copper_chestplate");
	public static final DeferredHolder<Item, ? extends Item> COPPER_LEGGINGS = vanilla("copper_leggings");
	public static final DeferredHolder<Item, ? extends Item> COPPER_BOOTS = vanilla("copper_boots");

	public static final DeferredHolder<Item, ? extends Item> COPPER_NUGGET = vanilla("copper_nugget");
	public static final DeferredHolder<Item, ? extends Item> COPPER_SHEARS = ITEMS.registerItem("copper_shears", properties -> new CopperShearsItem(properties.durability(200).component(DataComponents.TOOL, ShearsItem.createToolProperties())));
	public static final DeferredHolder<Item, ? extends Item> COPPER_HORSE_ARMOR = vanilla("copper_horse_armor");

	public static final DeferredHolder<Item, ? extends Item> COPPER_BUCKET = ITEMS.registerItem("copper_bucket", properties -> new CopperBucketItem(Fluids.EMPTY, properties.stacksTo(16)));
	public static final DeferredHolder<Item, ? extends Item> COPPER_WATER_BUCKET = ITEMS.registerItem("copper_water_bucket", properties -> new CopperBucketItem(Fluids.WATER, properties.stacksTo(1).craftRemainder(COPPER_BUCKET.get())));
	public static final DeferredHolder<Item, ? extends Item> COPPER_LAVA_BUCKET = ITEMS.registerItem("copper_lava_bucket", properties -> new CopperBucketItem(Fluids.LAVA, properties.stacksTo(1).craftRemainder(COPPER_BUCKET.get())));
	public static final DeferredHolder<Item, ? extends Item> COPPER_POWDER_SNOW_BUCKET = ITEMS.registerItem("copper_powder_snow_bucket", properties -> new CopperSolidBucketItem(properties.stacksTo(1).craftRemainder(COPPER_BUCKET.get())));
	public static final DeferredHolder<Item, ? extends Item> COPPER_MILK_BUCKET = ITEMS.registerItem("copper_milk_bucket", properties -> new CopperMilkBucketItem(properties.stacksTo(1).craftRemainder(COPPER_BUCKET.get())
		.component(DataComponents.CONSUMABLE, Consumables.MILK_BUCKET).usingConvertsTo(COPPER_BUCKET.get())));

	public static final DeferredHolder<Item, ? extends Item> COPPER_AXOLOTL_BUCKET = ITEMS.registerItem("copper_axolotl_bucket", properties -> new CopperMobBucketItem(EntityTypes.AXOLOTL, Fluids.WATER, SoundEvents.BUCKET_EMPTY_AXOLOTL, properties.stacksTo(1)));
	public static final DeferredHolder<Item, ? extends Item> COPPER_TADPOLE_BUCKET = ITEMS.registerItem("copper_tadpole_bucket", properties -> new CopperMobBucketItem(EntityTypes.TADPOLE, Fluids.WATER, SoundEvents.BUCKET_EMPTY_TADPOLE, properties.stacksTo(1)));
	public static final DeferredHolder<Item, ? extends Item> COPPER_COD_BUCKET = ITEMS.registerItem("copper_cod_bucket", properties -> new CopperMobBucketItem(EntityTypes.COD, Fluids.WATER, SoundEvents.BUCKET_EMPTY_FISH, properties.stacksTo(1)));
	public static final DeferredHolder<Item, ? extends Item> COPPER_SALMON_BUCKET = ITEMS.registerItem("copper_salmon_bucket", properties -> new CopperMobBucketItem(EntityTypes.SALMON, Fluids.WATER, SoundEvents.BUCKET_EMPTY_FISH, properties.stacksTo(1)));
	public static final DeferredHolder<Item, ? extends Item> COPPER_PUFFERFISH_BUCKET = ITEMS.registerItem("copper_pufferfish_bucket", properties -> new CopperMobBucketItem(EntityTypes.PUFFERFISH, Fluids.WATER, SoundEvents.BUCKET_EMPTY_FISH, properties.stacksTo(1)));
	public static final DeferredHolder<Item, ? extends Item> COPPER_TROPICAL_FISH_BUCKET = ITEMS.registerItem("copper_tropical_fish_bucket", properties -> new CopperMobBucketItem(EntityTypes.TROPICAL_FISH, Fluids.WATER, SoundEvents.BUCKET_EMPTY_FISH, properties.stacksTo(1)));
	public static final DeferredHolder<Item, ? extends Item> COPPER_SULFUR_CUBE_BUCKET = ITEMS.registerItem("copper_sulfur_cube_bucket", properties -> new CopperMobBucketItem(EntityTypes.SULFUR_CUBE, Fluids.EMPTY, SoundEvents.BUCKET_EMPTY_SULFUR_CUBE, properties.stacksTo(1)));

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
