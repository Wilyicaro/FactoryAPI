package wily.factoryapi.mixin.common;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.factoryapi.base.client.FactoryGuiGraphics;
import wily.factoryapi.base.client.WidgetAccessor;

@Mixin(AbstractWidget.class)
public abstract class AbstractWidgetMixin implements WidgetAccessor, LayoutElement {
    @Shadow protected int height;

    @Shadow public abstract boolean isHoveredOrFocused();

    @Shadow protected float alpha;
    @Unique ResourceLocation sprite = null;
    @Unique ResourceLocation highlightedSprite = null;

    @Override
    public void setSpriteOverlay(ResourceLocation sprite) {
        this.sprite = sprite;
    }

    @Override
    public void setHighlightedSpriteOverlay(ResourceLocation sprite) {
        this.highlightedSprite = sprite;
    }

    @Override
    public ResourceLocation getSpriteOverlay() {
        return isHoveredOrFocused() ? highlightedSprite : sprite;
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/AbstractWidget;renderWidget(Lnet/minecraft/client/gui/GuiGraphics;IIF)V", shift = At.Shift.AFTER))
    public void renderOverlay(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci){
        ResourceLocation sprite = getSpriteOverlay();
        if (sprite != null) {
            RenderSystem.enableBlend();
            FactoryGuiGraphics.of(guiGraphics).setColor(1.0f,1.0f,1.0f,alpha);
            FactoryGuiGraphics.of(guiGraphics).blitSprite(sprite, getX(), getY(), getWidth(), getHeight());
            FactoryGuiGraphics.of(guiGraphics).setColor(1.0f,1.0f,1.0f,1.0f);
            RenderSystem.disableBlend();
        }
    }

    //? <=1.20.1 {
    /*@Override
    public void setHeight(int height) {
        this.height = height;
    }
    *///?}
}
