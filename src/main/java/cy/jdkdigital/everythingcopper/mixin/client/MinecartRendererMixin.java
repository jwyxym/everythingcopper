package cy.jdkdigital.everythingcopper.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import cy.jdkdigital.everythingcopper.EverythingCopper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.AbstractMinecartRenderer;
import net.minecraft.client.renderer.entity.state.MinecartRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractMinecartRenderer.class)
public class MinecartRendererMixin {
	@Unique
	private static final ContextKey<Identifier> EVERYTHINGCOPPER_TEXTURE = new ContextKey<>(Identifier.fromNamespaceAndPath(EverythingCopper.MODID, "minecart_texture"));

	@Inject(method = "extractRenderState(Lnet/minecraft/world/entity/vehicle/minecart/AbstractMinecart;Lnet/minecraft/client/renderer/entity/state/MinecartRenderState;F)V", at = @At("TAIL"))
	private void everythingcopper$extract(AbstractMinecart entity, MinecartRenderState state, float partialTick, CallbackInfo callback) {
		Identifier texture = BuiltInRegistries.ENTITY_TYPE.getKey(entity.getType()).getNamespace().equals(EverythingCopper.MODID)
			? Identifier.fromNamespaceAndPath(EverythingCopper.MODID, "textures/entity/copper_minecart/unaffected.png") : null;
		state.setRenderData(EVERYTHINGCOPPER_TEXTURE, texture);
	}

	@Redirect(method = "submit(Lnet/minecraft/client/renderer/entity/state/MinecartRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/SubmitNodeCollector;submitModel(Lnet/minecraft/client/model/Model;Ljava/lang/Object;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/resources/Identifier;IIILnet/minecraft/client/renderer/feature/ModelFeatureRenderer$CrumblingOverlay;)V"))
	private <S> void everythingcopper$submit(SubmitNodeCollector collector, Model<? super S> model, S state, PoseStack poses,
		Identifier texture, int light, int overlay, int outline, ModelFeatureRenderer.CrumblingOverlay crumbling) {
		Identifier selected = state instanceof MinecartRenderState minecart ? minecart.getRenderDataOrDefault(EVERYTHINGCOPPER_TEXTURE, texture) : texture;
		collector.submitModel(model, state, poses, selected, light, overlay, outline, crumbling);
	}
}
