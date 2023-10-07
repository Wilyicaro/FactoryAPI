package wily.factoryapi.fabric.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.lighting.BlockLightEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wily.factoryapi.base.IFactoryBlock;

@Mixin(BlockLightEngine.class)
public class BlockLightEngineInjector {

    @Inject(method = ("getLightEmission"), at = @At("HEAD"), cancellable = true)
    private void injectLuminance(long l, CallbackInfoReturnable<Integer> info){
        BlockLightEngine engine = ((BlockLightEngine)(Object)this);
        int i = BlockPos.getX(l);
        int j = BlockPos.getY(l);
        int k = BlockPos.getZ(l);
        BlockGetter blockGetter = engine.chunkSource.getChunkForLighting(i >> 4, k >> 4);
        BlockPos pos = engine.pos.set(i, j, k);
        info.setReturnValue(blockGetter != null ? getBlockLuminance(blockGetter.getBlockState(pos),blockGetter,pos)  : 0);
    }
    @Unique
    private int getBlockLuminance(BlockState state, BlockGetter level, BlockPos pos){
        return state.getBlock() instanceof IFactoryBlock ? ((IFactoryBlock)state.getBlock()).getLuminance(state,level,pos): state.getLightEmission();
    }

}
