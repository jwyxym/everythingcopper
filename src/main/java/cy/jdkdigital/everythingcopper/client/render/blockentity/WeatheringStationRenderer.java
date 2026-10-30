package cy.jdkdigital.everythingcopper.client.render.blockentity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import cy.jdkdigital.everythingcopper.common.block.entity.WeatheringStationBlockEntity;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.feature.ModelFeatureRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.phys.Vec3;

public class WeatheringStationRenderer implements BlockEntityRenderer<WeatheringStationBlockEntity, WeatheringStationRenderer.State> {
	private final ItemModelResolver items;

	public WeatheringStationRenderer(BlockEntityRendererProvider.Context context) {
		items = context.itemModelResolver();
	}

	@Override
	public State createRenderState() {
		return new State();
	}

	@Override
	public void extractRenderState(WeatheringStationBlockEntity entity, State state, float partialTick, Vec3 cameraPosition, ModelFeatureRenderer.CrumblingOverlay crumbling) {
		BlockEntityRenderer.super.extractRenderState(entity, state, partialTick, cameraPosition, crumbling);
		items.updateForTopItem(state.input, entity.inventoryHandler.getStackInSlot(WeatheringStationBlockEntity.SLOT_INPUT), ItemDisplayContext.FIXED, entity.getLevel(), null, 0);
		items.updateForTopItem(state.output, entity.inventoryHandler.getStackInSlot(WeatheringStationBlockEntity.SLOT_OUTPUT), ItemDisplayContext.FIXED, entity.getLevel(), null, 0);
	}

	@Override
	public void submit(State state, PoseStack poses, SubmitNodeCollector collector, CameraRenderState camera) {
		boolean multiple = !state.input.isEmpty() && !state.output.isEmpty();
		submitItem(state.input, state.lightCoords, poses, collector, multiple ? 90.0F : 0.0F);
		submitItem(state.output, state.lightCoords, poses, collector, 0.0F);
	}

	private void submitItem(ItemStackRenderState item, int light, PoseStack poses, SubmitNodeCollector collector, float rotation) {
		if (item.isEmpty()) {
			return;
		}
		poses.pushPose();
		poses.translate(0.5F, 0.6375F, 0.5F);
		poses.mulPose(Axis.YP.rotationDegrees(rotation));
		poses.scale(0.7F, 0.7F, 0.7F);
		item.submit(poses, collector, light, OverlayTexture.NO_OVERLAY, 0);
		poses.popPose();
	}

	public static class State extends BlockEntityRenderState {
		public final ItemStackRenderState input = new ItemStackRenderState();
		public final ItemStackRenderState output = new ItemStackRenderState();
	}
}
