package wily.factoryapi.mixin.base;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
//? if >=1.20.5 {
/*import net.minecraft.client.gui.components.WidgetTooltipHolder;
*///?} else {
import net.minecraft.client.gui.components.Tooltip;
//?}
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.navigation.CommonInputs;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.factoryapi.base.client.FactoryGuiGraphics;
import wily.factoryapi.base.client.WidgetAccessor;

import java.util.function.Consumer;

@Mixin(AbstractWidget.class)
public abstract class AbstractWidgetMixin implements WidgetAccessor, GuiEventListener {
    @Shadow protected int height;

    @Shadow public abstract boolean isHoveredOrFocused();

    @Shadow protected float alpha;

    @Shadow public abstract void playDownSound(SoundManager arg);

    @Shadow public boolean active;
    @Shadow public boolean visible;
    //? if >1.20.1 {
    @Shadow private /*? if >=1.20.5 {*//*WidgetTooltipHolder*//*?} else {*/Tooltip/*?}*/ tooltip;
    //?} else {
    /*@Shadow protected abstract void updateTooltip();
    *///?}

    @Shadow public abstract boolean isHovered();

    @Shadow protected abstract void renderScrollingString(GuiGraphics arg, Font arg2, int k, int l);

    @Unique ResourceLocation overlaySprite = null;
    @Unique ResourceLocation highlightedOverlaySprite = null;
    @Unique Consumer<AbstractWidget> onPressOverride = null;

    @Override
    public void setSpriteOverride(ResourceLocation sprite) {
        this.overlaySprite = sprite;
    }

    @Override
    public void setHighlightedSpriteOverride(ResourceLocation sprite) {
        this.highlightedOverlaySprite = sprite;
    }

    @Override
    public ResourceLocation getSpriteOverride() {
        return isHoveredOrFocused() ? highlightedOverlaySprite : overlaySprite;
    }
    @Override
    public Consumer<AbstractWidget> getOnPressOverride() {
        return onPressOverride;
    }

    @Override
    public void setOnPressOverride(Consumer<AbstractWidget> onPressOverride) {
        this.onPressOverride = onPressOverride;
    }

    @Unique
    private void onPress(){
        if (getOnPressOverride() != null) getOnPressOverride().accept((AbstractWidget) (Object) this);
    }

    @Inject(method = "onClick", at = @At("HEAD"))
    public void onClick(double d, double e, CallbackInfo ci) {
        onPress();
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (this.active && this.visible) {
            if (CommonInputs.selected(i)) {
                this.playDownSound(Minecraft.getInstance().getSoundManager());
                this.onPress();
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Inject(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/AbstractWidget;renderWidget(Lnet/minecraft/client/gui/GuiGraphics;IIF)V", shift = At.Shift.BEFORE), cancellable = true)
    public void renderOverlay(GuiGraphics guiGraphics, int i, int j, float f, CallbackInfo ci){
        ResourceLocation sprite = getSpriteOverride();
        if (sprite != null) {
            LayoutElement element = (LayoutElement) this;
            RenderSystem.enableBlend();
            FactoryGuiGraphics.of(guiGraphics).setColor(1.0f,1.0f,1.0f,alpha);
            FactoryGuiGraphics.of(guiGraphics).blitSprite(sprite, element.getX(), element.getY(), element.getWidth(), element.getHeight());
            FactoryGuiGraphics.of(guiGraphics).setColor(1.0f,1.0f,1.0f,1.0f);
            RenderSystem.disableBlend();
            this.renderScrollingString(guiGraphics, Minecraft.getInstance().font, 2, i);
            //? if >1.20.1 {
            if (this.tooltip != null) {
                this.tooltip.refreshTooltipForNextRenderPass(this.isHovered(), this.isFocused(), this.getRectangle());
            }
            //?} else {
            /*this.updateTooltip();
            *///?}
            ci.cancel();
        }
    }

    //? <=1.20.1 {
    /*@Override
    public void setHeight(int height) {
        this.height = height;
    }
    *///?}
}
