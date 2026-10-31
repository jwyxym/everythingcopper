package cy.jdkdigital.everythingcopper.util;

import cy.jdkdigital.everythingcopper.common.item.ICopperItem;
import cy.jdkdigital.everythingcopper.common.item.ICopperBucket;
import cy.jdkdigital.everythingcopper.init.ModDataComponents;
import cy.jdkdigital.everythingcopper.init.ModItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.equipment.Equippable;
import net.minecraft.world.level.Level;

public final class CopperWeathering {
	public static final int CHECK_INTERVAL = 20;
	public static final int TOOL_USES_PER_STAGE = 32;
	public static final int BUCKET_USES_PER_STAGE = 16;
	private static final int PROGRESS_PER_USE = 5;

	private CopperWeathering() {
	}

	public static float chancePerCheck(Level level) {
		double ticks = CHECK_INTERVAL * (level.isRaining() ? 1.2D : 1.0D);
		return (float) -Math.expm1(ticks * Math.log1p(-ICopperItem.WEATHERING_CHANCE_PER_TICK));
	}

	public static boolean isWornArmor(ItemStack stack, EquipmentSlot slot) {
		boolean armor = stack.is(Items.COPPER_HELMET) || stack.is(Items.COPPER_CHESTPLATE)
			|| stack.is(Items.COPPER_LEGGINGS) || stack.is(Items.COPPER_BOOTS) || stack.is(Items.COPPER_HORSE_ARMOR);
		Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
		return armor && equippable != null && equippable.slot() == slot;
	}

	public static boolean isTool(ItemStack stack) {
		return stack.is(Items.COPPER_SWORD) || stack.is(Items.COPPER_SPEAR) || stack.is(Items.COPPER_SHOVEL)
			|| stack.is(Items.COPPER_PICKAXE) || stack.is(Items.COPPER_AXE) || stack.is(Items.COPPER_HOE)
			|| stack.is(ModItems.COPPER_SHEARS.get());
	}

	public static void recordToolUse(ItemStack stack, Level level) {
		if (isTool(stack)) recordUse(stack, level, TOOL_USES_PER_STAGE);
	}

	public static void recordBucketUse(ItemStack stack, Level level) {
		if (stack.getItem() instanceof ICopperBucket) {
			recordUse(stack, level, BUCKET_USES_PER_STAGE);
		}
	}

	public static ItemStack bucketInteraction(ItemStack source, ItemStack target, Level level) {
		CopperItemVariants.copyState(source, target);
		recordBucketUse(target, level);
		return target;
	}

	private static void recordUse(ItemStack stack, Level level, int usesPerStage) {
		if (level.isClientSide() || !ICopperItem.canAge(stack)) return;
		int threshold = usesPerStage * PROGRESS_PER_USE;
		int previous = stack.getOrDefault(ModDataComponents.WEATHERING_PROGRESS.get(), 0);
		int progress = Math.max(0, Math.min(previous, threshold - 1)) + (level.isRaining() ? 6 : 5);
		if (progress >= threshold) {
			ICopperItem.setAge(stack, WeatheringUtils.nextState(ICopperItem.getAge(stack)));
			progress -= threshold;
		}
		stack.set(ModDataComponents.WEATHERING_PROGRESS.get(), progress);
	}
}
