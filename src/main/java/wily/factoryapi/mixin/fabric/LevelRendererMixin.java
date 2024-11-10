//? if fabric {
package wily.factoryapi.mixin.fabric;

import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;
import wily.factoryapi.base.IFactoryBlock;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {
    @Inject( at = @At( value = "TAIL" ), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true, method = "getLightColor(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)I")
    private static void getLightmapCoordinates(BlockAndTintGetter world, BlockState state, BlockPos pos, CallbackInfoReturnable<Integer> cir, int i, int j) {
        if (state.getBlock() instanceof IFactoryBlock b){
            int k = b.getLightEmission(state,world, pos);
            if ( j < k ) j = k;
            cir.setReturnValue(i << 20 | j << 4);
        }
    }
}
//?}