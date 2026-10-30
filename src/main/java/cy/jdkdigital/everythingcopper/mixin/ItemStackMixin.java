package cy.jdkdigital.everythingcopper.mixin;

import cy.jdkdigital.everythingcopper.util.CopperWeathering;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
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

	@Inject(method = "processDurabilityChange", at = @At("RETURN"))
	private void everythingcopper$damageContext(int amount, ServerLevel level, ServerPlayer player, CallbackInfoReturnable<Integer> callback) {
		everythingcopper$durabilityLevel = callback.getReturnValue() > 0 ? level : null;
	}

	@Inject(method = "applyDamage", at = @At("HEAD"))
	private void everythingcopper$toolUse(int damage, ServerPlayer player, Consumer<Item> onBreak, CallbackInfo callback) {
		ServerLevel level = everythingcopper$durabilityLevel;
		everythingcopper$durabilityLevel = null;
		ItemStack stack = (ItemStack) (Object) this;
		if (level != null && damage > stack.getDamageValue()) {
			CopperWeathering.recordToolUse(stack, level);
		}
	}
}
