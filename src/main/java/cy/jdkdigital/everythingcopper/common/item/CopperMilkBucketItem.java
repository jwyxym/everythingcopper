package cy.jdkdigital.everythingcopper.common.item;

import cy.jdkdigital.everythingcopper.event.CopperEquipmentEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class CopperMilkBucketItem extends Item implements ICopperBucket {
	public CopperMilkBucketItem(Properties properties) {
		super(properties);
	}

	@Override
	public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
		CopperEquipmentEvents.updateAppearance(stack);
		return super.finishUsingItem(stack, level, entity);
	}
}
