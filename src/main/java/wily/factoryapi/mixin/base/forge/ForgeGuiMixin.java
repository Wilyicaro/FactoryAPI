//? if <1.20.5 && (forge || neoforge) {
/*package wily.factoryapi.mixin.base.forge;

import net.minecraft.client.gui.GuiGraphics;
//? forge {
/^import net.minecraftforge.client.gui.overlay.ForgeGui;
^///?} else {
import net.neoforged.neoforge.client.gui.overlay.ExtendedGui;
//?}
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.factoryapi.util.FactoryGuiElement;

@Mixin(/^? forge {^//^ForgeGui^//^?} else {^/ExtendedGui/^?}^/.class)
public class ForgeGuiMixin {

    @Inject(method = "renderRecordOverlay", at = @At(value = "HEAD"), remap = false, cancellable = true)
    public void renderOverlayMessage(int width, int height, float partialTick, GuiGraphics guiGraphics, CallbackInfo ci) {
        FactoryGuiElement.OVERLAY_MESSAGE.prepareMixin(guiGraphics, ci);
    }
    @Inject(method = "renderRecordOverlay", at = @At(value = "RETURN"), remap = false)
    public void renderOverlayMessageReturn(int width, int height, float partialTick, GuiGraphics guiGraphics, CallbackInfo ci) {
        FactoryGuiElement.OVERLAY_MESSAGE.finalizeMixin(guiGraphics);
    }
    @Inject(method = {"renderHealth","renderFood","renderAir","renderHealthMount"}, at = @At("HEAD"), cancellable = true,remap = false)
    public void renderHealth(int width, int height, GuiGraphics guiGraphics, CallbackInfo ci) {
        FactoryGuiElement.PLAYER_HEALTH.prepareMixin(guiGraphics, ci);
    }
    @Inject(method = {"renderHealth","renderFood","renderAir","renderHealthMount"}, at = @At("RETURN"),remap = false)
    public void renderHealthReturn(int width, int height, GuiGraphics guiGraphics, CallbackInfo ci) {
        FactoryGuiElement.PLAYER_HEALTH.finalizeMixin(guiGraphics);
    }
    @Inject(method = "renderArmor", at = @At("HEAD"), remap = false, cancellable = true)
    public void renderArmor(GuiGraphics guiGraphics, int width, int height, CallbackInfo ci) {
        FactoryGuiElement.PLAYER_HEALTH.prepareMixin(guiGraphics, ci);
    }
    @Inject(method = "renderArmor", at = @At("RETURN"), remap = false)
    public void renderArmorReturn(GuiGraphics guiGraphics, int width, int height, CallbackInfo ci) {
        FactoryGuiElement.PLAYER_HEALTH.finalizeMixin(guiGraphics);
    }
}
*///?}