package cy.jdkdigital.everythingcopper.common.item;

import cy.jdkdigital.everythingcopper.common.entity.CopperMinecart;
import cy.jdkdigital.everythingcopper.init.ModDataComponents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.BlockTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseRailBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RailShape;
import net.minecraft.world.level.gameevent.GameEvent;

public class CopperMinecartItem extends Item
{
	final CopperMinecart.Type type;
	public CopperMinecartItem(CopperMinecart.Type type, Properties properties) {
        super(properties);
        this.type = type;
    }

	@Override
	public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
		super.inventoryTick(stack, level, entity, slot);
		// Convert stacks from saves that still contain the removed minecart variants.
		if (stack.has(ModDataComponents.OXIDATION_STATE.get()) || stack.has(ModDataComponents.WAXED.get())) {
			stack.remove(ModDataComponents.OXIDATION_STATE.get());
			stack.remove(ModDataComponents.WAXED.get());
			stack.set(DataComponents.ITEM_NAME, components().get(DataComponents.ITEM_NAME));
		}
	}

    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockPos blockpos = context.getClickedPos();
        BlockState blockstate = level.getBlockState(blockpos);
        if (!blockstate.is(BlockTags.RAILS)) {
            return InteractionResult.FAIL;
        } else {
            ItemStack itemstack = context.getItemInHand();
			if (!level.isClientSide()) {
                RailShape railshape = blockstate.getBlock() instanceof BaseRailBlock ? ((BaseRailBlock)blockstate.getBlock()).getRailDirection(blockstate, level, blockpos, null) : RailShape.NORTH_SOUTH;
                double d0 = 0.0D;
				if (railshape.isSlope()) {
                    d0 = 0.5D;
                }

                AbstractMinecart abstractminecart = CopperMinecart.createMinecart(level, (double)blockpos.getX() + 0.5D, (double)blockpos.getY() + 0.0625D + d0, (double)blockpos.getZ() + 0.5D, this.type);
                if (itemstack.has(DataComponents.CUSTOM_NAME)) {
                    abstractminecart.setCustomName(itemstack.getHoverName());
                }

                level.addFreshEntity(abstractminecart);
                level.gameEvent(context.getPlayer(), GameEvent.ENTITY_PLACE, blockpos);
            }

			if (context.getPlayer() == null || !context.getPlayer().getAbilities().instabuild) {
				itemstack.shrink(1);
			}
			return InteractionResult.SUCCESS;
        }
    }
}
