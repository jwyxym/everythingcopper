package cy.jdkdigital.everythingcopper.util;

import cy.jdkdigital.everythingcopper.common.item.ICopperItem;
import cy.jdkdigital.everythingcopper.init.ModItems;
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

	public static List<ItemStack> all(Item item) {
		List<ItemStack> variants = new ArrayList<>();
		for (WeatheringCopper.WeatherState age : WeatheringCopper.WeatherState.values()) {
			variants.add(create(item, age, false));
			variants.add(create(item, age, true));
		}
		return variants;
	}
}
