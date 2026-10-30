package cy.jdkdigital.everythingcopper.util;

import cy.jdkdigital.everythingcopper.common.item.ICopperItem;
import cy.jdkdigital.everythingcopper.init.ModItems;
import cy.jdkdigital.everythingcopper.init.ModDataComponents;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.WeatheringCopper;

import java.util.ArrayList;
import java.util.List;

public final class CopperItemVariants {
	private CopperItemVariants() {
	}

	public static List<Item> equipment() {
		return List.of(ModItems.COPPER_SWORD.get(), ModItems.COPPER_SPEAR.get(), ModItems.COPPER_SHOVEL.get(), ModItems.COPPER_PICKAXE.get(),
			ModItems.COPPER_AXE.get(), ModItems.COPPER_HOE.get(), ModItems.COPPER_SHEARS.get(),
			ModItems.COPPER_HELMET.get(), ModItems.COPPER_CHESTPLATE.get(), ModItems.COPPER_LEGGINGS.get(),
			ModItems.COPPER_BOOTS.get(), ModItems.COPPER_HORSE_ARMOR.get());
	}

	public static ItemStack create(Item item, WeatheringCopper.WeatherState age, boolean waxed) {
		ItemStack stack = item.getDefaultInstance();
		if (age != WeatheringCopper.WeatherState.UNAFFECTED) {
			ICopperItem.setAge(stack, age.getSerializedName());
		}
		if (waxed) {
			ICopperItem.setWaxed(stack, true);
		}
		return stack;
	}

	public static List<Item> minecarts() {
		return List.of(ModItems.COPPER_MINECART.get(), ModItems.COPPER_CHEST_MINECART.get(), ModItems.COPPER_FURNACE_MINECART.get(),
			ModItems.COPPER_HOPPER_MINECART.get(), ModItems.COPPER_TNT_MINECART.get());
	}

	public static List<Item> buckets() {
		return List.of(ModItems.COPPER_BUCKET.get(), ModItems.COPPER_WATER_BUCKET.get(), ModItems.COPPER_LAVA_BUCKET.get(),
			ModItems.COPPER_POWDER_SNOW_BUCKET.get(), ModItems.COPPER_MILK_BUCKET.get(), ModItems.COPPER_AXOLOTL_BUCKET.get(),
			ModItems.COPPER_TADPOLE_BUCKET.get(), ModItems.COPPER_COD_BUCKET.get(), ModItems.COPPER_SALMON_BUCKET.get(),
			ModItems.COPPER_PUFFERFISH_BUCKET.get(), ModItems.COPPER_TROPICAL_FISH_BUCKET.get(), ModItems.COPPER_SULFUR_CUBE_BUCKET.get());
	}

	public static List<Item> weatheringItems() {
		List<Item> items = new ArrayList<>(equipment());
		items.addAll(minecarts());
		items.addAll(buckets());
		return items;
	}

	public static ItemStack copyState(ItemInstance from, ItemStack target) {
		target.set(ModDataComponents.WEATHERING_PROGRESS.get(), from.getOrDefault(ModDataComponents.WEATHERING_PROGRESS.get(), 0));
		String age = from.get(ModDataComponents.OXIDATION_STATE.get());
		ICopperItem.setAge(target, age == null ? "unaffected" : age);
		ICopperItem.setWaxed(target, Boolean.TRUE.equals(from.get(ModDataComponents.WAXED.get())));
		return target;
	}

	public static List<ItemStack> all(Item item) {
		List<ItemStack> variants = new ArrayList<>();
		for (WeatheringCopper.WeatherState age : WeatheringCopper.WeatherState.values()) {
			variants.add(create(item, age, false));
			variants.add(create(item, age, true));
		}
		return variants;
	}
}
