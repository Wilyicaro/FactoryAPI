package wily.factoryapi.mixin.base;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.factoryapi.FactoryAPIClient;
import wily.factoryapi.base.client.UIDefinition;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Shadow @Nullable public ClientLevel level;

    @Shadow @Final public Gui gui;

    @Inject(method = "resizeDisplay",at = @At("RETURN"))
    public void resizeDisplay(CallbackInfo ci) {
        if (this.level != null) {
            UIDefinition.Accessor.of(gui).beforeInit();
            UIDefinition.Accessor.of(gui).getRenderables().clear();
            UIDefinition.Accessor.of(gui).afterInit();
        }
    }
    @Inject(method = "setScreen",at = @At("RETURN"))
    public void setScreen(Screen screen, CallbackInfo ci) {
        if (screen == null && this.level != null) {
            UIDefinition.Accessor.of(gui).beforeInit();
            UIDefinition.Accessor.of(gui).getRenderables().clear();
            UIDefinition.Accessor.of(gui).afterInit();
        }
    }
    @Inject(method = "stop",at = @At("RETURN"))
    public void stop(CallbackInfo ci) {
        FactoryAPIClient.STOPPING.invoker.accept(Minecraft.getInstance());
    }
}
