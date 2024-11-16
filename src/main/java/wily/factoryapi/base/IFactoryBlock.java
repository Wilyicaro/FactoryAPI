package wily.factoryapi.base;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;

public interface IFactoryBlock {
    default int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getLightEmission();
    }
    static int getBlockLuminance(BlockState state, BlockGetter level, BlockPos pos){
        return state.getBlock() instanceof IFactoryBlock b ? b.getLightEmission(state,level,pos): state.getLightEmission();
    }
}
