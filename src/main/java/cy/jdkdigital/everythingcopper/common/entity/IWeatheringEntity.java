package cy.jdkdigital.everythingcopper.common.entity;

import cy.jdkdigital.everythingcopper.util.WeatheringUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.HoneycombItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WeatheringCopper;

public interface IWeatheringEntity
{
	EntityDataAccessor<String> weatherStateAccessor();
	EntityDataAccessor<Boolean> waxedAccessor();

    SynchedEntityData getSyncData();

    default WeatheringCopper.WeatherState getAge() {
		return WeatheringCopper.WeatherState.valueOf(getSyncData().get(weatherStateAccessor()));
    }

    default void setAge(WeatheringCopper.WeatherState age) {
		getSyncData().set(weatherStateAccessor(), age.name());
    }

    default boolean isWaxed() {
		return getSyncData().get(waxedAccessor());
    }

    default void setWaxed(boolean waxed) {
		getSyncData().set(waxedAccessor(), waxed);
    }

	default void saveWeatheredState(ValueOutput tag) {
		tag.putString("weathering", getSyncData().get(weatherStateAccessor()));
		tag.putBoolean("waxed", getSyncData().get(waxedAccessor()));
    }

	default void loadWeatheredState(ValueInput tag) {
		String state = tag.getStringOr("weathering", "UNAFFECTED");
		try {
			setAge(WeatheringCopper.WeatherState.valueOf(state));
		} catch (IllegalArgumentException ignored) {
			setAge(WeatheringCopper.WeatherState.UNAFFECTED);
		}
		setWaxed(tag.getBooleanOr("waxed", false));
	}

    default InteractionResult itemUse(Level level, ItemStack itemStack, Player player, BlockPos pos) {
        if (!isWaxed() && itemStack.getItem() instanceof HoneycombItem) {
            if (!level.isClientSide()) {
                setWaxed(true);
            }
            if (!player.getAbilities().instabuild) {
                itemStack.shrink(1);
            }
            level.levelEvent(player, 3003, pos.above(), 0);
			return InteractionResult.SUCCESS;
        } else if (itemStack.getItem() instanceof AxeItem) {
            if (isWaxed()) {
                if (!level.isClientSide()) {
                    setWaxed(false);
                }
                level.playSound(player, pos.above(), SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.levelEvent(player, 3004, pos.above(), 0);
				return InteractionResult.SUCCESS;
            } else if (!getAge().equals(WeatheringCopper.WeatherState.UNAFFECTED)) {
                if (!level.isClientSide()) {
                    setAge(WeatheringUtils.prevState(getAge()));
                }
                level.playSound(player, pos.above(), SoundEvents.AXE_SCRAPE, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.levelEvent(player, 3005, pos.above(), 0);
				return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    default float speedModifier() {
        float speedModifier = 1.0F;
        switch (getAge()) {
            case UNAFFECTED -> speedModifier = 1.1F;
            case WEATHERED -> speedModifier = 0.9F;
            case OXIDIZED -> speedModifier = 0.8F;
        }
        return speedModifier;
    }

    default boolean canAge() {
        return !isWaxed() && !getAge().equals(WeatheringCopper.WeatherState.OXIDIZED);
    }
}
