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

@Mixin(BlockLightEngine.class)
public class BlockLightEngineInjector {

    @Inject(method = ("getEmission"), at = @At("HEAD"), cancellable = true)
    private void injectLuminance(long l, BlockState blockState, CallbackInfoReturnable<Integer> info){
        BlockLightEngine engine = ((BlockLightEngine)(Object)this);
        if (blockState.getBlock() instanceof IFactoryBlock b && engine.storage.lightOnInSection(SectionPos.blockToSection(l))){
            int i = b.getLuminance(blockState,engine.chunkSource.getLevel(),BlockPos.of(l));
            if (i> 0)info.setReturnValue(i);
        }
    }
    @Unique
    private int getBlockLuminance(BlockState state, BlockGetter level, BlockPos pos){
        return state.getBlock() instanceof IFactoryBlock b ? b.getLuminance(state,level,pos): state.getLightEmission();
    }
    @Inject(method = ("propagateLightSources"), at = @At("HEAD"), cancellable = true)
    private void injectLightSources(ChunkPos pos, CallbackInfo info){
        BlockLightEngine engine = ((BlockLightEngine)(Object)this);
        engine.setLightEnabled(pos, true);
        LightChunk lightChunk = engine.chunkSource.getChunkForLighting(pos.x, pos.z);
        if (lightChunk != null) {
            lightChunk.findBlockLightSources((blockPos, blockState) -> {
                int i = getBlockLuminance(blockState,engine.chunkSource.getLevel(),blockPos);
                engine.enqueueIncrease(blockPos.asLong(), LightEngine.QueueEntry.increaseLightFromEmission(i, !blockState.canOcclude() || !blockState.useShapeForLightOcclusion()));
            });
        }
        info.cancel();
    }
}
