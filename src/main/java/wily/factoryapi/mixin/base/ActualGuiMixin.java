//? if >=26.2 {
package wily.factoryapi.mixin.base;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.Hud;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.factoryapi.util.FactoryScreenUtil;
import wily.factoryapi.base.client.UIAccessor;

@Mixin(Gui.class)
public class ActualGuiMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    @Final
    public Hud hud;

    @Inject(method = "setScreen",at = @At("RETURN"))
    public void setScreen(Screen screen, CallbackInfo ci) {
        if (minecraft.level != null) {
            UIAccessor.of(hud).reloadUI();
        }
    }

    @Inject(method = "tick",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;tick()V"))
    public void beforeScreenTick(CallbackInfo ci) {
        if (FactoryScreenUtil.getScreen() != null) UIAccessor.of(FactoryScreenUtil.getScreen()).beforeTick();
    }

    @Inject(method = "tick",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;tick()V", shift = At.Shift.AFTER))
    public void afterScreenTick(CallbackInfo ci) {
        if (FactoryScreenUtil.getScreen() != null) UIAccessor.of(FactoryScreenUtil.getScreen()).afterTick();
    }
}
//?}