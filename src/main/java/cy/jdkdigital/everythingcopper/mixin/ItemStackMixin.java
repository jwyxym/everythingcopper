package cy.jdkdigital.everythingcopper.mixin;

import cy.jdkdigital.everythingcopper.util.CopperWeathering;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
	@Unique
	private ServerLevel everythingcopper$durabilityLevel;
	@Unique
	private int everythingcopper$previousDamage;

	// NeoForge's ServerPlayer overloads delegate to these LivingEntity implementations.
	@Inject(method = "processDurabilityChange(ILnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;)I", at = @At("RETURN"))
	private void everythingcopper$damageContext(int amount, ServerLevel level, LivingEntity player, CallbackInfoReturnable<Integer> callback) {
		everythingcopper$durabilityLevel = callback.getReturnValue() > 0 ? level : null;
	}

	@Inject(method = "applyDamage(ILnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)V", at = @At("HEAD"))
	private void everythingcopper$captureDamage(int damage, LivingEntity player, Consumer<Item> onBreak, CallbackInfo callback) {
		everythingcopper$previousDamage = ((ItemStack) (Object) this).getDamageValue();
	}

	@Inject(method = "applyDamage(ILnet/minecraft/world/entity/LivingEntity;Ljava/util/function/Consumer;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;setDamageValue(I)V", shift = At.Shift.AFTER))
	private void everythingcopper$toolDamage(int damage, LivingEntity player, Consumer<Item> onBreak, CallbackInfo callback) {
		ServerLevel level = everythingcopper$durabilityLevel;
		everythingcopper$durabilityLevel = null;
		ItemStack stack = (ItemStack) (Object) this;
		int lostDurability = stack.getDamageValue() - everythingcopper$previousDamage;
		if (level != null && lostDurability > 0) {
			CopperWeathering.recordToolDamage(stack, level, lostDurability);
		}
	}
}
