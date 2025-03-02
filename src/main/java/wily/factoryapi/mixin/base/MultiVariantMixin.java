package wily.factoryapi.mixin.base;

import net.minecraft.client.renderer.block.model.MultiVariant;
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wily.factoryapi.base.client.FactoryOptions;

import java.util.List;
import java.util.function.Function;

@Mixin(MultiVariant.class)
public class MultiVariantMixin {
    @Shadow @Final private List<Variant> variants;

    @Inject(method = "bake", at = @At("HEAD"), cancellable = true)
    public void bake(ModelBaker modelBaker,/*? if <1.21.2 {*/ Function<Material, TextureAtlasSprite> function, ModelState modelState,/*? if <1.20.5 {*/ ResourceLocation resourceLocation,/*?}*//*?}*/ CallbackInfoReturnable<BakedModel> cir) {
        if (variants.size() > 1 && !FactoryOptions.RANDOM_BLOCK_ROTATIONS.get()){
            Variant variant = this.variants.get(0);
            cir.setReturnValue(modelBaker.bake(variant./*? if >1.21.3 {*//*modelLocation*//*?} else {*/getModelLocation/*?}*/(), variant));
        }
    }
}
