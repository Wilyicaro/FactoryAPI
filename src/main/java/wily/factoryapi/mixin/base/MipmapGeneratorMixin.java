package wily.factoryapi.mixin.base;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import net.minecraft.client.renderer.texture.MipmapGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wily.factoryapi.base.client.FactoryOptions;

@Mixin(MipmapGenerator.class)
public class MipmapGeneratorMixin {
    //? if <1.21.11 {
    @Inject(method = "alphaBlend", at = @At("HEAD"), cancellable = true)
    private static void alphaBlend(int i, int j, int k, int l, boolean bl, CallbackInfoReturnable<Integer> cir) {
        if (FactoryOptions.NEAREST_MIPMAP_SCALING.get()) cir.setReturnValue(l);
    }
    //?} else {
    /*@WrapOperation(method = "generateMipLevels", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/ARGB;meanLinear(IIII)I"))
    private static int alphaBlend(int i, int j, int k, int l, Operation<Integer> original) {
        if (FactoryOptions.NEAREST_MIPMAP_SCALING.get()) return l;
        return original.call(i, j, k, l);
    }
    @Inject(method = "darkenedAlphaBlend", at = @At("HEAD"), cancellable = true)
    private static void darkenedAlphaBlend(int i, int j, int k, int l, CallbackInfoReturnable<Integer> cir) {
        if (FactoryOptions.NEAREST_MIPMAP_SCALING.get()) cir.setReturnValue(l);
    }
    *///?}
}
