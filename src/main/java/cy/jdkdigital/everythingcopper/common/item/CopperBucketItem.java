package cy.jdkdigital.everythingcopper.common.item;

import cy.jdkdigital.everythingcopper.init.ModItems;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class CopperBucketItem extends BucketItem {
	public CopperBucketItem(Fluid content, Properties properties) {
		super(content, properties);
	}

	public static ItemStack copperContents(ItemStack vanilla) {
		if (vanilla.is(Items.WATER_BUCKET)) {
			return new ItemStack(ModItems.COPPER_WATER_BUCKET.get());
		}
		if (vanilla.is(Items.LAVA_BUCKET)) {
			return new ItemStack(ModItems.COPPER_LAVA_BUCKET.get());
		}
		if (vanilla.is(Items.POWDER_SNOW_BUCKET)) {
			return new ItemStack(ModItems.COPPER_POWDER_SNOW_BUCKET.get());
		}
		return vanilla;
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		ItemStack stack = player.getItemInHand(hand);
		BlockHitResult hit = getPlayerPOVHitResult(level, player, getFluidContext());
		if (hit.getType() != HitResult.Type.BLOCK) {
			return InteractionResult.PASS;
		}
		BlockPos pos = hit.getBlockPos();
		BlockPos adjacent = pos.relative(hit.getDirection());
		if (!level.mayInteract(player, pos) || !player.mayUseItemAt(adjacent, hit.getDirection(), stack)) {
			return InteractionResult.FAIL;
		}
		BlockState state = level.getBlockState(pos);
		if (content == Fluids.EMPTY) {
			if (!(state.getBlock() instanceof BucketPickup pickup)) {
				return InteractionResult.FAIL;
			}
			ItemStack filled = copperContents(pickup.pickupBlock(player, level, pos, state));
			if (filled.isEmpty()) {
				return InteractionResult.FAIL;
			}
			player.awardStat(Stats.ITEM_USED.get(this));
			pickup.getPickupSound(state).ifPresent(sound -> player.playSound(sound, 1.0F, 1.0F));
			level.gameEvent(player, GameEvent.FLUID_PICKUP, pos);
			if (player instanceof ServerPlayer serverPlayer) {
				CriteriaTriggers.FILLED_BUCKET.trigger(serverPlayer, filled);
			}
			return InteractionResult.SUCCESS.heldItemTransformedTo(ItemUtils.createFilledResult(stack, player, filled));
		}
		BlockPos target = state.getBlock() instanceof LiquidBlockContainer && content == Fluids.WATER ? pos : adjacent;
		if (!emptyContents(player, level, target, hit)) {
			return InteractionResult.FAIL;
		}
		checkExtraContent(player, level, stack, target);
		if (player instanceof ServerPlayer serverPlayer) {
			CriteriaTriggers.PLACED_BLOCK.trigger(serverPlayer, target, stack);
		}
		player.awardStat(Stats.ITEM_USED.get(this));
		ItemStack empty = player.hasInfiniteMaterials() ? stack : new ItemStack(ModItems.COPPER_BUCKET.get());
		return InteractionResult.SUCCESS.heldItemTransformedTo(ItemUtils.createFilledResult(stack, player, empty));
	}
}
