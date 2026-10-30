package cy.jdkdigital.everythingcopper.event;

import cy.jdkdigital.everythingcopper.util.CopperWeathering;
import cy.jdkdigital.everythingcopper.EverythingCopper;
import cy.jdkdigital.everythingcopper.init.ModItems;
import cy.jdkdigital.everythingcopper.common.item.CopperMobBucketItem;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Bucketable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Leashable;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.cow.AbstractCow;
import net.minecraft.world.entity.animal.goat.Goat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUtils;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;

@EventBusSubscriber(modid = EverythingCopper.MODID)
public class CopperBucketEvents {
	@SubscribeEvent
	public static void captureMob(PlayerInteractEvent.EntityInteract event) {
		ItemStack container = event.getItemStack();
		if ((!container.is(ModItems.COPPER_WATER_BUCKET.get()) && !container.is(ModItems.COPPER_BUCKET.get()))
			|| !(event.getTarget() instanceof LivingEntity entity) || !entity.isAlive()
			|| !(entity instanceof Bucketable bucketable)) {
			return;
		}
		ItemStack vanillaContainer = new ItemStack(container.is(ModItems.COPPER_WATER_BUCKET.get()) ? Items.WATER_BUCKET : Items.BUCKET);
		if (!bucketable.canBePickedUpWithBucket(vanillaContainer)) {
			return;
		}
		ItemStack nativeContents = bucketable.getBucketItemStack();
		Item copper = CopperMobBucketItem.copperVariant(nativeContents.getItem());
		if (copper == nativeContents.getItem()) {
			return;
		}
		Player player = event.getEntity();
		if (!player.level().isClientSide()) {
			ItemStack filled = CopperWeathering.bucketInteraction(container, nativeContents.transmuteCopy(copper, 1), player.level());
			bucketable.saveToBucketTag(filled);
			entity.playSound(bucketable.getPickupSound(), 1.0F, 1.0F);
			player.setItemInHand(event.getHand(), ItemUtils.createFilledResult(container, player, filled, false));
			if (player instanceof ServerPlayer serverPlayer) {
				CriteriaTriggers.FILLED_BUCKET.trigger(serverPlayer, filled);
			}
			if (entity instanceof Leashable leashable) {
				leashable.dropLeash();
			}
			entity.discard();
		}
		event.setCancellationResult(InteractionResult.SUCCESS);
		event.setCanceled(true);
	}

	@SubscribeEvent
	public static void milkAnimal(PlayerInteractEvent.EntityInteract event) {
		if (!event.getItemStack().is(ModItems.COPPER_BUCKET.get())
			|| !(event.getTarget() instanceof Animal animal) || animal.isBaby()
			|| !(animal instanceof AbstractCow || animal instanceof Goat)) {
			return;
		}
		Player player = event.getEntity();
		if (!player.level().isClientSide()) {
			player.level().playSound(null, animal.blockPosition(), animal instanceof Goat ? SoundEvents.GOAT_MILK : SoundEvents.COW_MILK,
				SoundSource.NEUTRAL, 1.0F, 1.0F);
			player.awardStat(Stats.ITEM_USED.get(ModItems.COPPER_BUCKET.get()));
			ItemStack milk = CopperWeathering.bucketInteraction(event.getItemStack(), new ItemStack(ModItems.COPPER_MILK_BUCKET.get()), player.level());
			player.setItemInHand(event.getHand(), ItemUtils.createFilledResult(event.getItemStack(), player, milk));
		}
		event.setCancellationResult(InteractionResult.SUCCESS);
		event.setCanceled(true);
	}
}
