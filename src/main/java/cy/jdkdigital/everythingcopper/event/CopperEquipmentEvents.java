package cy.jdkdigital.everythingcopper.event;

import cy.jdkdigital.everythingcopper.EverythingCopper;
import cy.jdkdigital.everythingcopper.common.item.ICopperItem;
import cy.jdkdigital.everythingcopper.util.WeatheringUtils;
import cy.jdkdigital.everythingcopper.util.CopperItemVariants;
import cy.jdkdigital.everythingcopper.init.ModItems;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.component.UseRemainder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.EquipmentAssets;
import net.minecraft.world.item.equipment.Equippable;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.EntityTickEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.Optional;

@EventBusSubscriber(modid = EverythingCopper.MODID)
public class CopperEquipmentEvents {
	@SubscribeEvent
	public static void playerTick(PlayerTickEvent.Post event) {
		tickEquipment(event.getEntity());
	}

	@SubscribeEvent
	public static void entityTick(EntityTickEvent.Post event) {
		if (event.getEntity() instanceof LivingEntity entity && !(entity instanceof Player)) {
			tickEquipment(entity);
		}
	}

	private static void tickEquipment(LivingEntity entity) {
		if (entity.level().isClientSide()) {
			return;
		}
		for (EquipmentSlot slot : EquipmentSlot.values()) {
			ItemStack stack = entity.getItemBySlot(slot);
			if (!ICopperItem.isCopperItem(stack)) {
				continue;
			}
			if (ICopperItem.canAge(stack) && entity.getRandom().nextFloat() < 0.00018962963F) {
				ICopperItem.setAge(stack, WeatheringUtils.nextState(ICopperItem.getAge(stack)));
			}
			updateAppearance(stack);
		}
	}

	public static void updateAppearance(ItemStack stack) {
		if (!ICopperItem.isCopperItem(stack)) {
			return;
		}
		String age = ICopperItem.getAge(stack);
		String path = net.minecraft.core.registries.BuiltInRegistries.ITEM.getKey(stack.getItem()).getPath();
		if (age.equals("unaffected") && !ICopperItem.isWaxed(stack)) {
			stack.set(DataComponents.ITEM_NAME, stack.getItem().components().get(DataComponents.ITEM_NAME));
		} else {
			String prefix = ICopperItem.isWaxed(stack) ? "waxed_" : "";
			stack.set(DataComponents.ITEM_NAME, Component.translatable("item.everythingcopper." + prefix + age + "_" + path));
		}
		Equippable equippable = stack.get(DataComponents.EQUIPPABLE);
		if (stack.is(ModItems.COPPER_MILK_BUCKET.get())) {
			ItemStack empty = CopperItemVariants.copyState(stack, new ItemStack(ModItems.COPPER_BUCKET.get()));
			stack.set(DataComponents.USE_REMAINDER, new UseRemainder(ItemStackTemplate.fromNonEmptyStack(empty)));
		}
		if (equippable != null) {
			ResourceKey<EquipmentAsset> asset = age.equals("unaffected") ? EquipmentAssets.COPPER
				: ResourceKey.create(EquipmentAssets.ROOT_ID, Identifier.fromNamespaceAndPath(EverythingCopper.MODID, age + "_copper"));
			if (!equippable.assetId().equals(Optional.of(asset))) {
				stack.set(DataComponents.EQUIPPABLE, new Equippable(equippable.slot(), equippable.equipSound(), Optional.of(asset),
					equippable.cameraOverlay(), equippable.allowedEntities(), equippable.dispensable(), equippable.swappable(),
					equippable.damageOnHurt(), equippable.equipOnInteract(), equippable.canBeSheared(), equippable.shearingSound()));
			}
		}
	}
}
