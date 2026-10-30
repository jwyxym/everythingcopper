package cy.jdkdigital.everythingcopper.common.item;

import cy.jdkdigital.everythingcopper.init.ModItems;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SolidBucketItem;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Blocks;

public class CopperSolidBucketItem extends SolidBucketItem {
	public CopperSolidBucketItem(Properties properties) {
		super(Blocks.POWDER_SNOW, SoundEvents.BUCKET_EMPTY_POWDER_SNOW, properties);
	}

	@Override
	public InteractionResult useOn(UseOnContext context) {
		InteractionResult result = super.useOn(context);
		Player player = context.getPlayer();
		if (result.consumesAction() && player != null && !player.hasInfiniteMaterials()) {
			player.setItemInHand(context.getHand(), new ItemStack(ModItems.COPPER_BUCKET.get()));
		}
		return result;
	}

	@Override
	public void registerBlocks(java.util.Map<net.minecraft.world.level.block.Block, net.minecraft.world.item.Item> blocks, net.minecraft.world.item.Item item) {
		// The vanilla powder-snow bucket remains the default item for the powder-snow block.
	}
}
