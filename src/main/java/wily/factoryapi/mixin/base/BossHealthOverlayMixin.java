package wily.factoryapi.mixin.base;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.BossHealthOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.factoryapi.util.FactoryGuiElement;

@Mixin(BossHealthOverlay.class)
public class BossHealthOverlayMixin {
    //? if >=26.1 {
    /*@Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, CallbackInfo ci) {
        FactoryGuiElement.BOSSHEALTH.prepareMixin(guiGraphics, ci);

    }
    @Inject(method = "extractRenderState", at = @At("RETURN"))
    public void renderReturn(GuiGraphicsExtractor guiGraphics, CallbackInfo ci) {
        FactoryGuiElement.BOSSHEALTH.finalizeMixin(guiGraphics);
    }
    *///?} else {
    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    public void render(GuiGraphics guiGraphics, CallbackInfo ci) {
        FactoryGuiElement.BOSSHEALTH.prepareMixin(guiGraphics, ci);

    }
    @Inject(method = "render", at = @At("RETURN"))
    public void renderReturn(GuiGraphics guiGraphics, CallbackInfo ci) {
        FactoryGuiElement.BOSSHEALTH.finalizeMixin(guiGraphics);
    }
    //?}
}
