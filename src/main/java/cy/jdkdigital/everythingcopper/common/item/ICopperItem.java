package cy.jdkdigital.everythingcopper.common.item;

import cy.jdkdigital.everythingcopper.init.ModDataComponents;
import cy.jdkdigital.everythingcopper.init.ModItems;
import cy.jdkdigital.everythingcopper.util.WeatheringUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

public interface ICopperItem
{
	float WEATHERING_CHANCE_PER_TICK = 0.00018962963F;
	float FILLED_BUCKET_WEATHERING_MULTIPLIER = 2.0F;

    default void weatheringTick(ItemStack stack, Level level) {
		if (!level.isClientSide() && canAge(stack) && level.getRandom().nextFloat() < getWeatheringChance(stack)) {
            setAge(stack, WeatheringUtils.nextState(getAge(stack)));
        }
    }

	static float getWeatheringChance(ItemStack stack) {
		// Include every filled bucket, even dry mob buckets whose fluid is EMPTY.
		boolean filledBucket = stack.getItem() instanceof ICopperBucket && !stack.is(ModItems.COPPER_BUCKET.get());
		return WEATHERING_CHANCE_PER_TICK * (filledBucket ? FILLED_BUCKET_WEATHERING_MULTIPLIER : 1.0F);
	}

    static boolean isWaxed(ItemStack stack) {
		if (stack.has(ModDataComponents.WAXED.get())) {
			return Boolean.TRUE.equals(stack.get(ModDataComponents.WAXED.get()));
        }
        return false;
    }

    static void setWaxed(ItemStack stack, boolean waxed) {
		stack.set(ModDataComponents.WAXED.get(), waxed);
		cy.jdkdigital.everythingcopper.event.CopperEquipmentEvents.updateAppearance(stack);
    }

    static boolean canAge(ItemStack stack) {
		return isCopperItem(stack) && !isWaxed(stack) && !getAge(stack).equals("oxidized");
    }

    static String getAge(ItemStack stack) {
		if (stack.has(ModDataComponents.OXIDATION_STATE.get())) {
			return stack.get(ModDataComponents.OXIDATION_STATE.get());
        }
        return "unaffected";
    }

    static void setAge(ItemStack stack, String age) {
		stack.set(ModDataComponents.OXIDATION_STATE.get(), age);
		cy.jdkdigital.everythingcopper.event.CopperEquipmentEvents.updateAppearance(stack);
    }

    static Float getStateAsFloat(ItemStack stack) {
		if (stack.has(ModDataComponents.OXIDATION_STATE.get())) {
			var state = stack.get(ModDataComponents.OXIDATION_STATE.get());
            return switch (state) {
                case "exposed" -> 1.0F;
                case "weathered" -> 2.0F;
                case "oxidized" -> 3.0F;
                default -> 0.0F;
            };
        }
        return 0.0F;
    }
	static boolean isCopperItem(ItemStack stack) {
		return stack.getItem() instanceof ICopperItem || stack.is(Items.COPPER_SWORD) || stack.is(Items.COPPER_SHOVEL)
			|| stack.is(Items.COPPER_PICKAXE) || stack.is(Items.COPPER_AXE) || stack.is(Items.COPPER_HOE) || stack.is(Items.COPPER_SPEAR)
			|| stack.is(Items.COPPER_HELMET) || stack.is(Items.COPPER_CHESTPLATE)
			|| stack.is(Items.COPPER_LEGGINGS) || stack.is(Items.COPPER_BOOTS) || stack.is(Items.COPPER_HORSE_ARMOR);
	}
}
