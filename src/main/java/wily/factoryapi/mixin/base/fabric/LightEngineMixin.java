//? if fabric {
package wily.factoryapi.mixin.base.fabric;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.lighting.LightEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wily.factoryapi.base.IFactoryBlock;

@Mixin(LightEngine.class)
public class LightEngineMixin {
    //? if <1.21.2 {
    /*@Inject(method = ("hasDifferentLightProperties"), at = @At("RETURN"), cancellable = true)
    private static void injectLuminance(BlockGetter blockGetter, BlockPos blockPos, BlockState blockState, BlockState blockState2, CallbackInfoReturnable<Boolean> info){
        if (blockState.getBlock() instanceof IFactoryBlock || blockState2.getBlock() instanceof IFactoryBlock){
            info.setReturnValue(info.getReturnValue() || IFactoryBlock.getBlockLuminance(blockState2,blockGetter,blockPos) != IFactoryBlock.getBlockLuminance(blockState,blockGetter,blockPos));
        }
    }
    *///?}
}
//?}