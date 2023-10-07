package wily.factoryapi.fabric.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.factoryapi.base.IFactoryItem;

@Mixin({ItemRenderer.class})
public abstract class ItemRendererInjector {
    @Inject(method = ("render"), at = @At("HEAD"), cancellable = true)
    private void injectRender(ItemStack itemStack, ItemTransforms.TransformType transformType, boolean bl, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, BakedModel bakedModel, CallbackInfo info){
        if (!itemStack.isEmpty() && bakedModel.isCustomRenderer()) {
                if (itemStack.getItem() instanceof IFactoryItem) {
                    ((IFactoryItem)itemStack.getItem()).clientExtension(c-> {
                        BlockEntityWithoutLevelRenderer beWLR = c.getCustomRenderer();
                        if (beWLR != null){
                            poseStack.pushPose();
                            bakedModel.getTransforms().getTransform(transformType).apply(bl, poseStack);
                            poseStack.translate(-0.5, -0.5, -0.5);
                            beWLR.renderByItem(itemStack, transformType, poseStack, multiBufferSource, i, j);
                            poseStack.popPose();
                            info.cancel();
                        }
                    });
                }
        }
    }
}
