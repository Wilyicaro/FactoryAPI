package wily.factoryapi.fabric.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wily.factoryapi.base.IFactoryBlock;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Mixin(LevelChunk.class)
public class LevelChunkInjector {

    @Inject( at = @At( value = "HEAD"), method = "getLights", cancellable = true)
    private void getLightSourcesStream(CallbackInfoReturnable<Stream<BlockPos>> ci ) {
        LevelChunk chunk = (LevelChunk) (Object) this;
        ci.setReturnValue(StreamSupport.stream(BlockPos.betweenClosed(chunk.chunkPos.getMinBlockX(), 0, chunk.chunkPos.getMinBlockZ(), chunk.chunkPos.getMaxBlockX(), 255, chunk.chunkPos.getMaxBlockZ()).spliterator(), false).filter((blockPos) -> {
            BlockState state = chunk.getBlockState(blockPos);
            return chunk.getBlockState(blockPos).getLightEmission() != 0 ||  state.getBlock() instanceof IFactoryBlock b && b.getLuminance(state,chunk,blockPos) != 0 ;
        }));
    }
}
