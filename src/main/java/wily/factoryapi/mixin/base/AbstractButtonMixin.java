package wily.factoryapi.mixin.base;

//? if >=1.21.11 {
/*import net.minecraft.client.gui.ActiveTextCollector;
import wily.factoryapi.base.FactoryRenderingTextCollector;
*///?} else {
import net.minecraft.client.gui.Font;
//?}
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.inventory.LoomScreen;
//? if >=1.21.9 {
/*import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
*///?}
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wily.factoryapi.base.client.FactoryGuiGraphics;
import wily.factoryapi.base.client.WidgetAccessor;
import wily.factoryapi.util.FactoryScreenUtil;

@Mixin(AbstractButton.class)
public abstract class AbstractButtonMixin extends AbstractWidget implements WidgetAccessor {

    public AbstractButtonMixin(int i, int j, int k, int l, Component component) {
        super(i, j, k, l, component);
    }

    //? if >=1.21.9 {
    /*@Inject(method = "onClick", at = @At("HEAD"), cancellable = true)
    public void onClick(MouseButtonEvent mouseButtonEvent, boolean bl, CallbackInfo ci) {
        if (getOnPressOverride() != null) {
            super.onClick(mouseButtonEvent, bl);
            ci.cancel();
        }
    }

    @Inject(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/AbstractButton;onPress(Lnet/minecraft/client/input/InputWithModifiers;)V"), cancellable = true)
    public void keyPressed(KeyEvent keyEvent, CallbackInfoReturnable<Boolean> cir) {
        if (getOnPressOverride() != null) {
            getOnPressOverride().accept(this);
            cir.setReturnValue(true);
        }
    }
    *///?} else {
    @Inject(method = "onClick", at = @At("HEAD"), cancellable = true)
    public void onClick(double d, double e, CallbackInfo ci) {
        if (getOnPressOverride() != null) {
            super.onClick(d,e);
            ci.cancel();
        }
    }

    @Inject(method = "keyPressed", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/components/AbstractButton;onPress()V"), cancellable = true)
    public void keyPressed(int i, int j, int k, CallbackInfoReturnable<Boolean> cir) {
        if (getOnPressOverride() != null) {
            getOnPressOverride().accept(this);
            cir.setReturnValue(true);
        }
    }
    //?}

    // TODO 1.21.11
    @Inject(method = /*? if <1.21.11 {*/"renderString"/*?} else {*//*"renderDefaultLabel"*//*?}*/, at = @At("HEAD"))
    public void renderString(/*? if <1.21.11 {*/GuiGraphics guiGraphics, Font font, int i, /*?} else {*//*ActiveTextCollector activeTextCollector, *//*?}*/ CallbackInfo ci) {
        //? if >=1.21.11 {
        /*GuiGraphics guiGraphics;
        if (activeTextCollector instanceof FactoryRenderingTextCollector collector) guiGraphics = collector.getGuiGraphics(); else return;
        *///?}
        ResourceLocation sprite = getSpriteOverride();
        if (sprite != null) {
            FactoryScreenUtil.enableBlend();
            //? if <1.21.6 {
            FactoryGuiGraphics.of(guiGraphics).setColor(1.0f, 1.0f, 1.0f, alpha);
            //?} else
            //FactoryGuiGraphics.of(guiGraphics).setBlitColor(1.0f, 1.0f, 1.0f, alpha);
            FactoryGuiGraphics.of(guiGraphics).blitSprite(sprite, getX(), getY(), getWidth(), getHeight());
            //? if <1.21.6 {
            FactoryGuiGraphics.of(guiGraphics).clearColor();
            //?} else
            //FactoryGuiGraphics.of(guiGraphics).clearBlitColor();
            FactoryScreenUtil.disableBlend();
        }
    }
}
