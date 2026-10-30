package cy.jdkdigital.everythingcopper.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.LadderBlock;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;

public class CopperLadder extends LadderBlock implements IWeatheringBlock
{
    private final WeatherState weatherState;

    public CopperLadder(WeatherState state, Properties properties) {
        super(properties);
        weatherState = state;
    }

    @Override
    public void randomTick(BlockState blockState, ServerLevel level, BlockPos blockPos, RandomSource random) {
        this.changeOverTime(blockState, level, blockPos, random);
    }

    @Override
    public boolean isRandomlyTicking(BlockState blockState) {
        return WeatheringCopper.getNext(blockState.getBlock()).isPresent();
    }

    @Override
    public WeatherState getAge() {
        return this.weatherState;
    }

    private boolean isSupportSolid(BlockGetter level, BlockPos pos) {
        BlockState blockstate = level.getBlockState(pos);
        return blockstate.isFaceSturdy(level, pos, Direction.UP);
    }

    private boolean isCopperLadder(BlockGetter level, BlockPos pos) {
        return level.getBlockState(pos).getBlock() instanceof CopperLadder;
    }

    @Override
    public boolean canSurvive(BlockState blockState, LevelReader level, BlockPos pos) {
        // valid if below or above are copper ladders or if opposite face is solid
        return super.canSurvive(blockState, level, pos) || isSupportSolid(level, pos.above()) || isCopperLadder(level, pos.below());
    }

    @Override
	public BlockState updateShape(BlockState blockState, LevelReader level, net.minecraft.world.level.ScheduledTickAccess ticks, BlockPos blockPos, Direction direction, BlockPos blockPos1, BlockState blockState1, RandomSource random) {
        if (!blockState.canSurvive(level, blockPos)) {
			ticks.scheduleTick(blockPos, this, 1);
        }

		return super.updateShape(blockState, level, ticks, blockPos, direction, blockPos1, blockState1, random);
    }

    @Override
    public void tick(BlockState blockState, ServerLevel level, BlockPos pos, RandomSource rand) {
        if (!blockState.canSurvive(level, pos)) {
            level.destroyBlock(pos, true);
        }
    }
}
