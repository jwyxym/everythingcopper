package cy.jdkdigital.everythingcopper.event;

import cy.jdkdigital.everythingcopper.EverythingCopper;

import net.minecraft.client.renderer.entity.MinecartRenderer;
import net.minecraft.client.renderer.entity.TntMinecartRenderer;
import cy.jdkdigital.everythingcopper.init.ModEntities;
import net.minecraft.client.model.geom.ModelLayers;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = EverythingCopper.MODID, value = Dist.CLIENT)
public class ClientSetup
{
    @SubscribeEvent
    public static void registerEntityRendering(EntityRenderersEvent.RegisterRenderers event) {
		event.registerEntityRenderer(ModEntities.COPPER_MINECART.get(), (context) -> new MinecartRenderer(context, ModelLayers.MINECART));
		event.registerEntityRenderer(ModEntities.CHEST_COPPER_MINECART.get(), (context) -> new MinecartRenderer(context, ModelLayers.CHEST_MINECART));
		event.registerEntityRenderer(ModEntities.COMMAND_BLOCK_COPPER_MINECART.get(), (context) -> new MinecartRenderer(context, ModelLayers.COMMAND_BLOCK_MINECART));
		event.registerEntityRenderer(ModEntities.FURNACE_COPPER_MINECART.get(), (context) -> new MinecartRenderer(context, ModelLayers.FURNACE_MINECART));
		event.registerEntityRenderer(ModEntities.HOPPER_COPPER_MINECART.get(), (context) -> new MinecartRenderer(context, ModelLayers.HOPPER_MINECART));
		event.registerEntityRenderer(ModEntities.SPAWNER_COPPER_MINECART.get(), (context) -> new MinecartRenderer(context, ModelLayers.SPAWNER_MINECART));
		event.registerEntityRenderer(ModEntities.TNT_COPPER_MINECART.get(), TntMinecartRenderer::new);
    }
}
