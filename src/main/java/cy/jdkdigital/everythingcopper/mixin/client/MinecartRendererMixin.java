package cy.jdkdigital.everythingcopper.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import cy.jdkdigital.everythingcopper.EverythingCopper;
import cy.jdkdigital.everythingcopper.client.render.CopperMinecartRenderData;
import cy.jdkdigital.everythingcopper.common.entity.ICopperMinecart;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.AbstractMinecartRenderer;
import net.minecraft.client.renderer.entity.state.MinecartRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractMinecartRenderer.class)
public class MinecartRendererMixin {
	@Shadow @Final
	private static Identifier MINECART_LOCATION;

	@Inject(method = "extractRenderState(Lnet/minecraft/world/entity/vehicle/minecart/AbstractMinecart;Lnet/minecraft/client/renderer/entity/state/MinecartRenderState;F)V", at = @At("TAIL"))
	private void everythingcopper$extract(AbstractMinecart entity, MinecartRenderState state, float partialTick, CallbackInfo callback) {
		Identifier texture = entity instanceof ICopperMinecart copper
			? Identifier.fromNamespaceAndPath(EverythingCopper.MODID, "textures/entity/copper_minecart/" + copper.getCopperAge().getSerializedName() + ".png") : null;
		((CopperMinecartRenderData) state).everythingcopper$setTexture(texture);
	}

	@Redirect(method = "submit(Lnet/minecraft/client/renderer/entity/state/MinecartRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
		at = @At(value = "FIELD", target = "Lnet/minecraft/client/renderer/entity/AbstractMinecartRenderer;MINECART_LOCATION:Lnet/minecraft/resources/Identifier;"))
	private static Identifier everythingcopper$texture(MinecartRenderState state, PoseStack poses, SubmitNodeCollector collector, CameraRenderState camera) {
		Identifier texture = ((CopperMinecartRenderData) state).everythingcopper$getTexture();
		return texture == null ? MINECART_LOCATION : texture;
	}
}
