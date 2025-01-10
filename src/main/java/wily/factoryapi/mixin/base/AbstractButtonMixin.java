package wily.factoryapi.mixin.base;

import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wily.factoryapi.base.client.WidgetAccessor;

@Mixin(AbstractButton.class)
public abstract class AbstractButtonMixin extends AbstractWidget implements WidgetAccessor {

    public AbstractButtonMixin(int i, int j, int k, int l, Component component) {
        super(i, j, k, l, component);
    }

    @Inject(method = "onClick", at = @At("HEAD"), cancellable = true)
    public void onClick(double d, double e, CallbackInfo ci) {
        if (getOnPressOverride() != null) {
            super.onClick(d,e);
            ci.cancel();
        }
    }

    @Inject(method = "keyPressed", at = @At("HEAD"), cancellable = true)
    public void keyPressed(int i, int j, int k, CallbackInfoReturnable<Boolean> cir) {
        if (getOnPressOverride() != null) {
            cir.setReturnValue(super.keyPressed(i, j, k));
        }
    }
}
