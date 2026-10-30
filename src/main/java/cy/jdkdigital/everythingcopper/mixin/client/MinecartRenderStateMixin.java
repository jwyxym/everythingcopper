package cy.jdkdigital.everythingcopper.mixin.client;

import cy.jdkdigital.everythingcopper.client.render.CopperMinecartRenderData;
import net.minecraft.client.renderer.entity.state.MinecartRenderState;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(MinecartRenderState.class)
public class MinecartRenderStateMixin implements CopperMinecartRenderData {
	@Unique
	private Identifier everythingcopper$texture;

	@Override
	public Identifier everythingcopper$getTexture() { return everythingcopper$texture; }

	@Override
	public void everythingcopper$setTexture(Identifier texture) { everythingcopper$texture = texture; }
}
