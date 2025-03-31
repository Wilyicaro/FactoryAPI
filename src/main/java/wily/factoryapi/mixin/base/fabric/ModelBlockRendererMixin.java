//? if fabric {
package wily.factoryapi.mixin.base.fabric;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import wily.factoryapi.base.IFactoryBlock;

@Mixin(ModelBlockRenderer.class )
public class ModelBlockRendererMixin {
    @ModifyExpressionValue(method = "tesselateBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/state/BlockState;getLightEmission()I"))
    private int render(int original, @Local(argsOnly = true) BlockAndTintGetter level, @Local(argsOnly = true) BlockState state, @Local(argsOnly = true) BlockPos pos) {
        return state.getBlock() instanceof IFactoryBlock block ? block.getLightEmission(state,level,pos) : original;
    }
}
//?}