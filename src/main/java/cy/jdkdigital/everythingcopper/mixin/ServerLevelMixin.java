package cy.jdkdigital.everythingcopper.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin {
	@Redirect(method = "tickChunk", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;randomTick(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/util/RandomSource;)V"))
	private void everythingcopper$rainWeathering(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
		state.randomTick(level, pos, random);
		if (level.isRaining() && state.getBlock() instanceof WeatheringCopper && random.nextFloat() < 0.2F) {
			BlockState current = level.getBlockState(pos);
			if (current.getBlock() instanceof WeatheringCopper && current.isRandomlyTicking()) {
				current.randomTick(level, pos, random);
			}
		}
	}
}
