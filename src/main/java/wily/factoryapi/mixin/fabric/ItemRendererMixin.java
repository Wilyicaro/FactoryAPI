//? if fabric {
package wily.factoryapi.mixin.fabric;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.factoryapi.base.IFactoryItem;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin {

    @Inject(method = ("render"), at = @At("HEAD"), cancellable = true)
    private void injectRender(ItemStack itemStack, ItemDisplayContext transformType, boolean bl, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, BakedModel bakedModel, CallbackInfo info){
        if (!itemStack.isEmpty() && bakedModel.isCustomRenderer()) {
                if (itemStack.getItem() instanceof IFactoryItem factoryItem) {
                    factoryItem.clientExtension(c-> {
                        BlockEntityWithoutLevelRenderer beWLR = c.getCustomRenderer(Minecraft.getInstance().getBlockEntityRenderDispatcher(), Minecraft.getInstance().getEntityModels());
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
//?}