package cy.jdkdigital.everythingcopper.event;

import cy.jdkdigital.everythingcopper.EverythingCopper;
import cy.jdkdigital.everythingcopper.init.ModItems;
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
			player.setItemInHand(event.getHand(), ItemUtils.createFilledResult(event.getItemStack(), player, new ItemStack(ModItems.COPPER_MILK_BUCKET.get())));
		}
		event.setCancellationResult(InteractionResult.SUCCESS);
		event.setCanceled(true);
	}
}
