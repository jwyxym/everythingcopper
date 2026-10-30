package cy.jdkdigital.everythingcopper.common.item;

import cy.jdkdigital.everythingcopper.init.ModItems;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;

public class CopperMobBucketItem extends MobBucketItem implements ICopperBucket {
	public CopperMobBucketItem(EntityType<? extends Mob> type, Fluid content, SoundEvent emptySound, Properties properties) {
		super(type, content, emptySound, properties);
	}

	@Override
	public InteractionResult use(Level level, Player player, InteractionHand hand) {
		return CopperBucketItem.useContents(this, level, player, hand);
	}

	public static Item copperVariant(Item vanilla) {
		if (vanilla == Items.AXOLOTL_BUCKET) return ModItems.COPPER_AXOLOTL_BUCKET.get();
		if (vanilla == Items.TADPOLE_BUCKET) return ModItems.COPPER_TADPOLE_BUCKET.get();
		if (vanilla == Items.COD_BUCKET) return ModItems.COPPER_COD_BUCKET.get();
		if (vanilla == Items.SALMON_BUCKET) return ModItems.COPPER_SALMON_BUCKET.get();
		if (vanilla == Items.PUFFERFISH_BUCKET) return ModItems.COPPER_PUFFERFISH_BUCKET.get();
		if (vanilla == Items.TROPICAL_FISH_BUCKET) return ModItems.COPPER_TROPICAL_FISH_BUCKET.get();
		if (vanilla == Items.SULFUR_CUBE_BUCKET) return ModItems.COPPER_SULFUR_CUBE_BUCKET.get();
		return vanilla;
	}
}
