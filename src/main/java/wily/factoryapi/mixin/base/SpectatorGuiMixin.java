package wily.factoryapi.mixin.base;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.spectator.SpectatorGui;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.factoryapi.base.client.SpectatorGuiAccessor;
import wily.factoryapi.util.FactoryGuiElement;

@Mixin(SpectatorGui.class)
public abstract class SpectatorGuiMixin implements SpectatorGuiAccessor {
    @Inject(method = "renderHotbar", at = @At("HEAD"), cancellable = true)
    public void renderHotbar(GuiGraphics guiGraphics, CallbackInfo ci) {
        FactoryGuiElement.SPECTATOR_HOTBAR.prepareMixin(guiGraphics, ci);
    }
    @Inject(method = "renderHotbar", at = @At("RETURN"))
    public void renderHotbarReturn(GuiGraphics guiGraphics, CallbackInfo ci) {
        FactoryGuiElement.SPECTATOR_HOTBAR.finalizeMixin(guiGraphics);
    }
    @Inject(method = "renderTooltip", at = @At("HEAD"), cancellable = true)
    public void renderTooltip(GuiGraphics guiGraphics, CallbackInfo ci) {
        FactoryGuiElement.SPECTATOR_TOOLTIP.prepareMixin(guiGraphics, ci);
    }
    @Inject(method = "renderTooltip", at = @At("RETURN"))
    public void renderTooltipReturn(GuiGraphics guiGraphics, CallbackInfo ci) {
        FactoryGuiElement.SPECTATOR_TOOLTIP.finalizeMixin(guiGraphics);
    }

    @Invoker("getHotbarAlpha")
    public abstract float getVisibility();

    @Accessor
    public abstract SpectatorMenu getMenu();
}
