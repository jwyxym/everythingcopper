package cy.jdkdigital.everythingcopper.common.entity;

import cy.jdkdigital.everythingcopper.util.WeatheringUtils;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import cy.jdkdigital.everythingcopper.init.ModEntities;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.minecart.MinecartFurnace;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.WeatheringCopper;

public class CopperMinecartFurnace extends MinecartFurnace implements IWeatheringEntity
{
	private static final EntityDataAccessor<String> DATA_WEATHER_STATE = SynchedEntityData.defineId(CopperMinecartFurnace.class, EntityDataSerializers.STRING);
	private static final EntityDataAccessor<Boolean> DATA_WAXED = SynchedEntityData.defineId(CopperMinecartFurnace.class, EntityDataSerializers.BOOLEAN);

	@Override
	public EntityDataAccessor<String> weatherStateAccessor() { return DATA_WEATHER_STATE; }

	@Override
	public EntityDataAccessor<Boolean> waxedAccessor() { return DATA_WAXED; }

    public CopperMinecartFurnace(EntityType<? extends MinecartFurnace> type, Level level) {
        super(type, level);
    }

    public CopperMinecartFurnace(Level level, double x, double y, double z) {
		this(ModEntities.FURNACE_COPPER_MINECART.get(), level);
		setPos(x, y, z);
    }

    @Override
    public SynchedEntityData getSyncData() {
        return super.getEntityData();
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder pBuilder) {
        super.defineSynchedData(pBuilder);
        pBuilder.define(DATA_WEATHER_STATE, WeatheringCopper.WeatherState.UNAFFECTED.name());
        pBuilder.define(DATA_WAXED, false);
    }

    @Override
    protected Component getTypeName() {
        String id = (isWaxed() ? "waxed_" : "") + getAge().name().toLowerCase() + "_furnace_copper_minecart";
        return Component.translatable(this.getType().getDescriptionId().replace("furnace_copper_minecart", id));
    }

    @Override
	public InteractionResult interact(Player player, InteractionHand hand, net.minecraft.world.phys.Vec3 location) {
        InteractionResult res = itemUse(this.level(), player.getItemInHand(hand), player, blockPosition());
        if (res.equals(InteractionResult.PASS)) {
			return super.interact(player, hand, location);
        }
        return res;
    }

    @Override
    public void tick() {
		if (!level().isClientSide() && tickCount%369 == 0 && canAge() && this.level().getRandom().nextFloat() < 0.005688889F) {
            setAge(WeatheringUtils.nextState(getAge()));
        }
        super.tick();
    }

    @Override
    protected float getBlockSpeedFactor() {
        return super.getBlockSpeedFactor() * speedModifier();
    }

    @Override
	public void addAdditionalSaveData(ValueOutput tag) {
        super.addAdditionalSaveData(tag);

        saveWeatheredState(tag);
    }

    @Override
	public void readAdditionalSaveData(ValueInput tag) {
        super.readAdditionalSaveData(tag);

        loadWeatheredState(tag);
    }
	@Override
	protected net.minecraft.world.item.Item getDropItem() {
		return cy.jdkdigital.everythingcopper.init.ModItems.COPPER_FURNACE_MINECART.get();
	}

	@Override
	public net.minecraft.world.item.ItemStack getPickResult() {
		return new net.minecraft.world.item.ItemStack(getDropItem());
	}
}
