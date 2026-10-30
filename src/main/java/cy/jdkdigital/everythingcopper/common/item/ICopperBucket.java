package cy.jdkdigital.everythingcopper.common.item;

import cy.jdkdigital.everythingcopper.init.ModItems;
import cy.jdkdigital.everythingcopper.util.CopperItemVariants;
import net.minecraft.world.item.ItemInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.neoforged.neoforge.common.extensions.IItemExtension;

public interface ICopperBucket extends ICopperItem, IItemExtension {
	@Override
	default ItemStackTemplate getCraftingRemainder(ItemInstance instance) {
		if (instance.typeHolder().value() == ModItems.COPPER_BUCKET.get()) {
			return null;
		}
		return ItemStackTemplate.fromNonEmptyStack(CopperItemVariants.copyState(instance, new ItemStack(ModItems.COPPER_BUCKET.get())));
	}
}
