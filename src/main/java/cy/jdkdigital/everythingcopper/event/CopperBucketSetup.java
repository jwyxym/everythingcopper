package cy.jdkdigital.everythingcopper.event;

import cy.jdkdigital.everythingcopper.EverythingCopper;
import cy.jdkdigital.everythingcopper.common.item.CopperBucketItem;
import cy.jdkdigital.everythingcopper.init.ModItems;
import cy.jdkdigital.everythingcopper.util.CopperItemVariants;
import cy.jdkdigital.everythingcopper.common.item.CopperMobBucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteractions;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.event.RegisterCauldronInteractionEvent;

@EventBusSubscriber(modid = EverythingCopper.MODID)
public class CopperBucketSetup {
	@SubscribeEvent
	public static void registerCauldrons(RegisterCauldronInteractionEvent.Interaction event) {
		for (String dispatcher : new String[] {"empty", "water", "lava", "powder_snow"}) {
			Identifier id = Identifier.withDefaultNamespace(dispatcher);
			event.register(id, ModItems.COPPER_WATER_BUCKET.get(), (state, level, pos, player, hand, stack) ->
				emptyCauldronBucket(level, pos, player, hand, stack, Blocks.WATER_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3), SoundEvents.BUCKET_EMPTY));
			event.register(id, ModItems.COPPER_LAVA_BUCKET.get(), (state, level, pos, player, hand, stack) ->
				emptyCauldronBucket(level, pos, player, hand, stack, Blocks.LAVA_CAULDRON.defaultBlockState(), SoundEvents.BUCKET_EMPTY_LAVA));
			event.register(id, ModItems.COPPER_POWDER_SNOW_BUCKET.get(), (state, level, pos, player, hand, stack) ->
				emptyCauldronBucket(level, pos, player, hand, stack, Blocks.POWDER_SNOW_CAULDRON.defaultBlockState().setValue(LayeredCauldronBlock.LEVEL, 3), SoundEvents.BUCKET_EMPTY_POWDER_SNOW));
		}
		event.register(Identifier.withDefaultNamespace("water"), ModItems.COPPER_BUCKET.get(), (state, level, pos, player, hand, stack) ->
			CauldronInteractions.fillBucket(state, level, pos, player, hand, stack, CopperItemVariants.copyState(stack, new ItemStack(ModItems.COPPER_WATER_BUCKET.get())),
				block -> block.getValue(LayeredCauldronBlock.LEVEL) == 3, SoundEvents.BUCKET_FILL));
		event.register(Identifier.withDefaultNamespace("lava"), ModItems.COPPER_BUCKET.get(), (state, level, pos, player, hand, stack) ->
			CauldronInteractions.fillBucket(state, level, pos, player, hand, stack, CopperItemVariants.copyState(stack, new ItemStack(ModItems.COPPER_LAVA_BUCKET.get())),
				block -> true, SoundEvents.BUCKET_FILL_LAVA));
		event.register(Identifier.withDefaultNamespace("powder_snow"), ModItems.COPPER_BUCKET.get(), (state, level, pos, player, hand, stack) ->
			CauldronInteractions.fillBucket(state, level, pos, player, hand, stack, CopperItemVariants.copyState(stack, new ItemStack(ModItems.COPPER_POWDER_SNOW_BUCKET.get())),
				block -> block.getValue(LayeredCauldronBlock.LEVEL) == 3, SoundEvents.BUCKET_FILL_POWDER_SNOW));
	}

	private static InteractionResult emptyCauldronBucket(Level level, BlockPos pos, Player player, InteractionHand hand, ItemStack stack, BlockState filled, SoundEvent sound) {
		if (!level.isClientSide()) {
			player.awardStat(Stats.FILL_CAULDRON);
			player.awardStat(Stats.ITEM_USED.get(stack.getItem()));
			player.setItemInHand(hand, ItemUtils.createFilledResult(stack, player, CopperItemVariants.copyState(stack, new ItemStack(ModItems.COPPER_BUCKET.get()))));
			level.setBlockAndUpdate(pos, filled);
			level.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, 1.0F);
			level.gameEvent(player, GameEvent.FLUID_PLACE, pos);
		}
		return InteractionResult.SUCCESS;
	}

	@SubscribeEvent
	public static void registerDispensers(FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			DefaultDispenseItemBehavior filled = new DefaultDispenseItemBehavior() {
				@Override
				protected ItemStack execute(BlockSource source, ItemStack stack) {
					BlockPos pos = source.pos().relative(source.state().getValue(DispenserBlock.FACING));
					DispensibleContainerItem container = (DispensibleContainerItem) stack.getItem();
					if (!container.emptyContents(null, source.level(), pos, null)) {
						return super.execute(source, stack);
					}
					container.checkExtraContent(null, source.level(), stack, pos);
					return consumeWithRemainder(source, stack, CopperItemVariants.copyState(stack, new ItemStack(ModItems.COPPER_BUCKET.get())));
				}
			};
			DispenserBlock.registerBehavior(ModItems.COPPER_WATER_BUCKET.get(), filled);
			DispenserBlock.registerBehavior(ModItems.COPPER_LAVA_BUCKET.get(), filled);
			DispenserBlock.registerBehavior(ModItems.COPPER_POWDER_SNOW_BUCKET.get(), filled);
			for (Item bucket : CopperItemVariants.buckets()) {
				if (bucket instanceof CopperMobBucketItem) {
					DispenserBlock.registerBehavior(bucket, filled);
				}
			}
			DispenserBlock.registerBehavior(ModItems.COPPER_BUCKET.get(), new DefaultDispenseItemBehavior() {
				@Override
				protected ItemStack execute(BlockSource source, ItemStack stack) {
					BlockPos pos = source.pos().relative(source.state().getValue(DispenserBlock.FACING));
					BlockState state = source.level().getBlockState(pos);
					if (state.getBlock() instanceof BucketPickup pickup) {
						ItemStack contents = CopperBucketItem.copperContents(pickup.pickupBlock(null, source.level(), pos, state));
						if (!contents.isEmpty()) {
							source.level().gameEvent(null, GameEvent.FLUID_PICKUP, pos);
							return consumeWithRemainder(source, stack, CopperItemVariants.copyState(stack, contents));
						}
					}
					return super.execute(source, stack);
				}
			});
		});
	}
}
