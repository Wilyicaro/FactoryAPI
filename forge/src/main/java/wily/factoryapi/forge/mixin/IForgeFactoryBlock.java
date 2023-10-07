package wily.factoryapi.forge.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.extensions.IForgeBlock;
import org.spongepowered.asm.mixin.Mixin;
import wily.factoryapi.base.IFactoryBlock;

@Mixin(IFactoryBlock.class)
public interface IForgeFactoryBlock extends IForgeBlock {
    @Override
    default int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
        return ((IFactoryBlock)this).getLuminance(state,level,pos);
    }
}
