//? if fabric {
package wily.factoryapi.mixin.fabric;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.factoryapi.base.IFactoryBlock;

import java.util.function.BiConsumer;

@Mixin(ChunkAccess.class)
public class LevelChunkMixin {

    @Inject( at = @At( value = "HEAD"), method = "findBlockLightSources", cancellable = true)
    private void getLightSourcesStream(BiConsumer<BlockPos, BlockState> biConsumer, CallbackInfo ci ) {
        ChunkAccess chunk = (ChunkAccess)(Object)(this);
        chunk.findBlocks(blockState -> blockState.getBlock() instanceof IFactoryBlock || blockState.getLightEmission() != 0,((blockPos, state) -> {if (!(state.getBlock() instanceof IFactoryBlock b) || b.getLightEmission(state,chunk,blockPos) != 0) biConsumer.accept(blockPos,state);}));
        ci.cancel();
    }
}
//?}
