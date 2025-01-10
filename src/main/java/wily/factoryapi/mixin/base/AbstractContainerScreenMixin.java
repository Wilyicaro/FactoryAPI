package wily.factoryapi.mixin.base;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.factoryapi.base.Bearer;
import wily.factoryapi.base.client.UIAccessor;
import wily.factoryapi.base.client.UIDefinition;

@Mixin(AbstractContainerScreen.class)
public abstract class AbstractContainerScreenMixin extends Screen {
    @Shadow protected int imageWidth;

    @Shadow protected int imageHeight;

    @Shadow protected int topPos;

    @Shadow protected int leftPos;

    @Shadow protected int inventoryLabelX;

    @Shadow protected int inventoryLabelY;

    @Shadow protected int titleLabelX;

    @Shadow protected int titleLabelY;

    @Shadow protected abstract void renderBg(GuiGraphics guiGraphics, float f, int i, int j);

    protected AbstractContainerScreenMixin(Component component) {
        super(component);
    }

    @Inject(method = "init", at = @At("HEAD"))
    protected void init(CallbackInfo ci) {
        UIAccessor.of(this).putIntegerBearer("imageWidth", Bearer.of(()-> imageWidth, i-> imageWidth = i));
        UIAccessor.of(this).putIntegerBearer("imageHeight", Bearer.of(()-> imageHeight, i-> imageHeight = i));
    }
    @Inject(method = "init", at = @At("RETURN"))
    protected void initReturn(CallbackInfo ci) {
        UIAccessor.of(this).putIntegerBearer("leftPos", Bearer.of(()-> leftPos, i-> leftPos = i));
        UIAccessor.of(this).putIntegerBearer("topPos", Bearer.of(()-> topPos, i-> topPos = i));
        UIAccessor.of(this).getDefinitions().add(new UIDefinition() {
            @Override
            public void afterInit(UIAccessor accessor) {
                UIDefinition.super.afterInit(accessor);
                accessor.putIntegerBearer("titleLabelX", Bearer.of(()-> titleLabelX, i-> titleLabelX = i));
                accessor.putIntegerBearer("titleLabelY", Bearer.of(()-> titleLabelY, i-> titleLabelY = i));
                accessor.putIntegerBearer("inventoryLabelX", Bearer.of(()-> inventoryLabelX, i-> inventoryLabelX = i));
                accessor.putIntegerBearer("inventoryLabelY", Bearer.of(()-> inventoryLabelY, i-> inventoryLabelY = i));
            }
        });
    }
    //? if >1.20.1 {
    @Inject(method = "renderBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;renderBg(Lnet/minecraft/client/gui/GuiGraphics;FII)V"), cancellable = true)
    protected void renderBackground(CallbackInfo ci) {
        if (!UIAccessor.of(this).getBoolean("hasContainerBackground",true)) ci.cancel();
    }
    //?} else {
    /*@Redirect(method = "render", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/inventory/AbstractContainerScreen;renderBg(Lnet/minecraft/client/gui/GuiGraphics;FII)V"))
    protected void render(AbstractContainerScreen instance, GuiGraphics guiGraphics, float v, int i, int j) {
        if (UIAccessor.of(this).getBoolean("hasContainerBackground",true)) renderBg(guiGraphics,v,i,j);
    }
    *///?}

}
