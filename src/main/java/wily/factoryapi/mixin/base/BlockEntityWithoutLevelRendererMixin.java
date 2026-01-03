//? if <1.21.4 {
/*package wily.factoryapi.mixin.base;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.factoryapi.base.client.IFactoryBlockEntityWLRenderer;
import wily.factoryapi.base.client.IFactoryItemClientExtension;

@Mixin(BlockEntityWithoutLevelRenderer.class)
public abstract class BlockEntityWithoutLevelRendererMixin {
    @Inject(method = "renderByItem", at = @At("HEAD"), cancellable = true)
    private void injectRender(ItemStack itemStack, ItemDisplayContext itemDisplayContext, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, CallbackInfo info){
        if (!itemStack.isEmpty()) {
            IFactoryItemClientExtension e;
            IFactoryBlockEntityWLRenderer beWLR;
            if ((e = IFactoryItemClientExtension.map.get(itemStack.getItem())) == null || (beWLR = e.getCustomRenderer()) == null) return;
            beWLR.renderByItem(itemStack, itemDisplayContext, poseStack, multiBufferSource, i, j);
            info.cancel();
        }
    }
}
*///?}