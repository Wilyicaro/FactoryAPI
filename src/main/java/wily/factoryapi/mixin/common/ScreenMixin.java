package wily.factoryapi.mixin.common;

import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.factoryapi.base.ArbitrarySupplier;
import wily.factoryapi.base.client.UIDefinition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(Screen.class)
public class ScreenMixin implements UIDefinition.Accessor {
    @Shadow @Final private List<GuiEventListener> children;
    @Shadow @Final public List<Renderable> renderables;
    @Shadow @Final private List<NarratableEntry> narratables;
    @Unique private final Map<String, ArbitrarySupplier<?>> elements = new HashMap<>();
    @Unique private final List<UIDefinition> definitions = new ArrayList<>();
    @Unique private final List<UIDefinition> staticDefinitions = new ArrayList<>();

    public Screen getScreen(){
        return (Screen) (Object) this;
    }

    @Override
    public Map<String, ArbitrarySupplier<?>> getElements() {
        return elements;
    }

    @Inject(method = "init(Lnet/minecraft/client/Minecraft;II)V",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;init()V"))
    public void beforeInit(CallbackInfo ci) {
        beforeInit();
    }
    @Inject(method = "init(Lnet/minecraft/client/Minecraft;II)V",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;init()V",shift = At.Shift.AFTER))
    public void afterInit(CallbackInfo ci) {
        afterInit();
    }
    @Inject(method = "rebuildWidgets",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;init()V"))
    public void rebuildWidgetsBefore(CallbackInfo ci) {
        beforeInit();
    }
    @Inject(method = "rebuildWidgets",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;init()V",shift = At.Shift.AFTER))
    public void rebuildWidgetsAfter(CallbackInfo ci) {
        afterInit();
    }

    @Override
    public List<UIDefinition> getDefinitions() {
        return definitions;
    }
    @Override
    public List<UIDefinition> getStaticDefinitions() {
        return staticDefinitions;
    }

    @Override
    public List<GuiEventListener> getChildren() {
        return children;
    }

    @Override
    public List<Renderable> getRenderables() {
        return renderables;
    }

    @Override
    public <T extends GuiEventListener> T addChidren(T listener, boolean isRenderable, boolean isNarratable) {
        children.add(listener);
        if (isRenderable && listener instanceof Renderable r) addRenderable(r);
        if (isNarratable && listener instanceof NarratableEntry e) narratables.add(e);
        return listener;
    }

    @Override
    public <T extends Renderable> T addRenderable(T renderable) {
        renderables.add(renderable);
        return renderable;
    }

    @Override
    public <T extends GuiEventListener> T removeChildren(T listener) {
        children.remove(listener);
        renderables.remove(listener);
        narratables.remove(listener);
        return listener;
    }
    @Inject(method = "renderBackground", at = @At(value = "HEAD"), cancellable = true)
    protected void renderBackground(CallbackInfo ci) {
        if (!getBoolean("hasBackground",true)) ci.cancel();
    }
}
