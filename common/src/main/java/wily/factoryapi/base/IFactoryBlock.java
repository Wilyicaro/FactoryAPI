package wily.factoryapi.base;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public interface IFactoryBlock {
    default int getLuminance(BlockState state, BlockGetter level, BlockPos pos)
    {
        return state.getLightEmission();
    }
}
