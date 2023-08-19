package wily.factoryapi.fabric.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LightChunk;
import net.minecraft.world.level.lighting.BlockLightEngine;
import net.minecraft.world.level.lighting.BlockLightSectionStorage;
import net.minecraft.world.level.lighting.LightEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wily.factoryapi.base.IFactoryBlock;

@Mixin(LightEngine.class)
public class LightEngineInjector {
    @Unique
    private static int getBlockLuminance(BlockState state, BlockGetter level, BlockPos pos){
        return state.getBlock() instanceof IFactoryBlock b ? b.getLuminance(state,level,pos): state.getLightEmission();
    }
    @Inject(method = ("hasDifferentLightProperties"), at = @At("HEAD"), cancellable = true)
    private static void injectLuminance(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, BlockState blockState2, CallbackInfoReturnable<Boolean> info){
        if (blockState.getBlock() instanceof IFactoryBlock || blockState2.getBlock() instanceof IFactoryBlock){
            info.setReturnValue(blockState2.getLightBlock(blockGetter, blockPos) != blockState.getLightBlock(blockGetter, blockPos) || getBlockLuminance(blockState2,blockGetter,blockPos) != getBlockLuminance(blockState,blockGetter,blockPos) || blockState2.useShapeForLightOcclusion() || blockState.useShapeForLightOcclusion());
        }
    }
}
