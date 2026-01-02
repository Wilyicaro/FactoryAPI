package wily.factoryapi.mixin.base;

//? if <1.21.5 {
import net.minecraft.client.renderer.block.model.Variant;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.renderer.block.model.MultiVariant;
//?} else {
/*import net.minecraft.client.resources.model.WeightedVariants;
import net.minecraft.util.random.WeightedList;
import net.minecraft.client.renderer.block.model.BlockModelPart;
import net.minecraft.client.renderer.block.model.BlockStateModel;
*///?}
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wily.factoryapi.base.client.FactoryOptions;

import java.util.List;
import java.util.function.Function;

@Mixin(/*? if >1.21.4 {*//*WeightedVariants*//*?} else {*/MultiVariant/*?}*/.class)
public class MultiVariantMixin {
    //? if <1.21.5 {
    @Mutable
    @Shadow @Final private List<Variant> variants;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(List list, CallbackInfo ci) {
        if (!FactoryOptions.RANDOM_BLOCK_ROTATIONS.get()) {
            this.variants = List.of(variants.get(0));
        }
    }
    //?} else {
    /*@Mutable
    @Shadow @Final private WeightedList<BlockStateModel> list;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(WeightedList<BlockStateModel> weightedList, CallbackInfo ci) {
        if (!FactoryOptions.RANDOM_BLOCK_ROTATIONS.get()) {
            this.list = WeightedList.of(list.unwrap().getFirst().value());
        }
    }
    *///?}
}
