package cy.jdkdigital.everythingcopper.common.entity;

import cy.jdkdigital.everythingcopper.init.ModTags;
import cy.jdkdigital.everythingcopper.util.CopperItemVariants;
import cy.jdkdigital.everythingcopper.util.WeatheringUtils;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.gamerules.GameRules;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public interface ICopperMinecart {
	EntityDataAccessor<String> ageAccessor();
	EntityDataAccessor<Boolean> waxAccessor();
	SynchedEntityData getEntityData();

	default WeatheringCopper.WeatherState getCopperAge() {
		return WeatheringCopper.WeatherState.valueOf(getEntityData().get(ageAccessor()));
	}

	default void setCopperAge(WeatheringCopper.WeatherState age) {
		getEntityData().set(ageAccessor(), age.name());
	}

	default boolean isCopperWaxed() { return getEntityData().get(waxAccessor()); }
	default void setCopperWaxed(boolean waxed) { getEntityData().set(waxAccessor(), waxed); }

	default void saveCopperState(ValueOutput output) {
		output.putString("weathering", getCopperAge().name());
		output.putBoolean("waxed", isCopperWaxed());
	}

	default void loadCopperState(ValueInput input) {
		try {
			setCopperAge(WeatheringCopper.WeatherState.valueOf(input.getStringOr("weathering", "UNAFFECTED")));
		} catch (IllegalArgumentException ignored) {
			setCopperAge(WeatheringCopper.WeatherState.UNAFFECTED);
		}
		setCopperWaxed(input.getBooleanOr("waxed", false));
	}

	default ItemStack copperItem(Item item) {
		ItemStack stack = CopperItemVariants.create(item, getCopperAge(), isCopperWaxed());
		AbstractMinecart entity = (AbstractMinecart) this;
		if (entity.hasCustomName()) stack.set(DataComponents.CUSTOM_NAME, entity.getCustomName());
		return stack;
	}

	default void dropCopperItem(ServerLevel level, Item item) {
		AbstractMinecart entity = (AbstractMinecart) this;
		entity.kill(level);
		if (level.getGameRules().get(GameRules.ENTITY_DROPS)) entity.spawnAtLocation(level, copperItem(item));
	}

	default float copperSpeedModifier() {
		return switch (getCopperAge()) {
			case UNAFFECTED -> 1.1F;
			case EXPOSED -> 1.0F;
			case WEATHERED -> 0.9F;
			case OXIDIZED -> 0.8F;
		};
	}

	default InteractionResult interactCopper(Player player, InteractionHand hand) {
		AbstractMinecart entity = (AbstractMinecart) this;
		ItemStack held = player.getItemInHand(hand);
		boolean wax = !isCopperWaxed() && held.is(ModTags.WAXING_ITEMS);
		boolean scrape = held.getItem() instanceof AxeItem && (isCopperWaxed() || getCopperAge() != WeatheringCopper.WeatherState.UNAFFECTED);
		if (!wax && !scrape) return InteractionResult.PASS;
		if (!entity.level().isClientSide()) {
			if (wax) {
				setCopperWaxed(true);
				if (!player.hasInfiniteMaterials()) held.shrink(1);
				entity.level().levelEvent(player, 3003, entity.blockPosition(), 0);
			} else {
				boolean waxed = isCopperWaxed();
				if (waxed) setCopperWaxed(false);
				else setCopperAge(WeatheringUtils.prevState(getCopperAge()));
				entity.level().playSound(null, entity.blockPosition(), waxed ? SoundEvents.AXE_WAX_OFF : SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
				entity.level().levelEvent(player, waxed ? 3004 : 3005, entity.blockPosition(), 0);
			}
		}
		return InteractionResult.SUCCESS;
	}
}
