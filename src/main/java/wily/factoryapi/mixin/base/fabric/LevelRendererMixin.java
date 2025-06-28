//? if fabric {
package wily.factoryapi.mixin.base.fabric;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
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
    //? if <1.21.5 {
    /*@ModifyExpressionValue(method = "getLightColor(Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getLightEmission()I"))
    private static int getLightColors(int original, @Local(argsOnly = true) BlockAndTintGetter level, @Local(argsOnly = true) BlockState state, @Local(argsOnly = true) BlockPos pos) {
        return state.getBlock() instanceof IFactoryBlock b ? b.getLightEmission(state, level, pos) : original;
    }
    *///?} else {
    @ModifyExpressionValue(method = "getLightColor(Lnet/minecraft/client/renderer/LevelRenderer$BrightnessGetter;Lnet/minecraft/world/level/BlockAndTintGetter;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;)I", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getLightEmission()I"))
    private static int getLightColors(int original, @Local(argsOnly = true) BlockAndTintGetter level, @Local(argsOnly = true) BlockState state, @Local(argsOnly = true) BlockPos pos) {
        return state.getBlock() instanceof IFactoryBlock b ? b.getLightEmission(state, level, pos) : original;
    }
    //?}
}
//?}