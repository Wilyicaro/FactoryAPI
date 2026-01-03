package wily.factoryapi.mixin.base;

import net.minecraft.client.renderer.texture.MipmapGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wily.factoryapi.base.client.FactoryOptions;

@Mixin(MipmapGenerator.class)
public class MipmapGeneratorMixin {
    // TODO 1.21.11
    //? if <1.21.11 {
    @Inject(method = "alphaBlend", at = @At("HEAD"), cancellable = true)
    private static void alphaBlend(int i, int j, int k, int l, boolean bl, CallbackInfoReturnable<Integer> cir) {
        if (FactoryOptions.NEAREST_MIPMAP_SCALING.get()) cir.setReturnValue(l);
    }
    //?}
}
