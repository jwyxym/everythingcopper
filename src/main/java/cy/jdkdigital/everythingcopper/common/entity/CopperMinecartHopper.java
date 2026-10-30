package cy.jdkdigital.everythingcopper.common.entity;

import cy.jdkdigital.everythingcopper.init.ModEntities;
import cy.jdkdigital.everythingcopper.init.ModItems;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.vehicle.minecart.MinecartHopper;
import net.minecraft.world.level.Level;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.phys.Vec3;

public class CopperMinecartHopper extends MinecartHopper implements ICopperMinecart {
	private static final EntityDataAccessor<String> COPPER_AGE = SynchedEntityData.defineId(CopperMinecartHopper.class, EntityDataSerializers.STRING);
	private static final EntityDataAccessor<Boolean> COPPER_WAXED = SynchedEntityData.defineId(CopperMinecartHopper.class, EntityDataSerializers.BOOLEAN);
	@Override public EntityDataAccessor<String> ageAccessor() { return COPPER_AGE; }
	@Override public EntityDataAccessor<Boolean> waxAccessor() { return COPPER_WAXED; }

	@Override
	protected void defineSynchedData(SynchedEntityData.Builder builder) {
		super.defineSynchedData(builder);
		builder.define(COPPER_AGE, "UNAFFECTED");
		builder.define(COPPER_WAXED, false);
	}

	@Override
	public void addAdditionalSaveData(ValueOutput output) {
		super.addAdditionalSaveData(output);
		saveCopperState(output);
	}

	@Override
	public void readAdditionalSaveData(ValueInput input) {
		super.readAdditionalSaveData(input);
		loadCopperState(input);
	}

	@Override
	public InteractionResult interact(Player player, InteractionHand hand, Vec3 location) {
		InteractionResult result = interactCopper(player, hand);
		return result.consumesAction() ? result : super.interact(player, hand, location);
	}

	public CopperMinecartHopper(EntityType<? extends MinecartHopper> type, Level level) {
		super(type, level);
	}

	public CopperMinecartHopper(Level level, double x, double y, double z) {
		this(ModEntities.HOPPER_COPPER_MINECART.get(), level);
		setPos(x, y, z);
	}

	@Override
	protected net.minecraft.network.chat.Component getTypeName() {
		String id = (isCopperWaxed() ? "waxed_" : "") + getCopperAge().getSerializedName() + "_"
			+ net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(getType()).getPath();
		return net.minecraft.network.chat.Component.translatable("entity.everythingcopper." + id);
	}

	@Override
	protected float getBlockSpeedFactor() {
		return super.getBlockSpeedFactor() * copperSpeedModifier();
	}

	@Override
	protected Item getDropItem() {
		return ModItems.COPPER_HOPPER_MINECART.get();
	}

	@Override
	public ItemStack getPickResult() {
		return copperItem(getDropItem());
	}
	@Override
	public void destroy(net.minecraft.server.level.ServerLevel level, Item item) {
		dropCopperItem(level, item);
	}
}
