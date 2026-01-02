package wily.factoryapi.mixin.base;

import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wily.factoryapi.base.ArbitrarySupplier;
import wily.factoryapi.base.client.UIAccessor;
import wily.factoryapi.base.client.UIDefinition;
import wily.factoryapi.util.VariablesMap;

import java.util.ArrayList;
import java.util.List;

@Mixin(Screen.class)
public abstract class ScreenMixin implements UIAccessor {
    @Shadow @Final private List<GuiEventListener> children;
    @Shadow @Final public List<Renderable> renderables;
    @Shadow @Final private List<NarratableEntry> narratables;

    @Shadow protected abstract void repositionElements();

    @Shadow private boolean initialized;
    @Unique private final VariablesMap<String, ArbitrarySupplier<?>> elements = new VariablesMap<>();
    @Unique private final List<UIDefinition> definitions = new ArrayList<>();
    @Unique private final List<UIDefinition> staticDefinitions = new ArrayList<>();

    public Screen getScreen(){
        return (Screen) (Object) this;
    }

    @Override
    public void reloadUI() {
        repositionElements();
    }

    @Override
    public VariablesMap<String, ArbitrarySupplier<?>> getElements() {
        return elements;
    }

    private static final @Unique String INIT_METHOD = /*? >=1.21.11 {*//*"init(II)V"*//*?} else {*/"init(Lnet/minecraft/client/Minecraft;II)V"/*?}*/;

    @Inject(method = INIT_METHOD, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;init()V"))
    public void beforeInit(CallbackInfo ci) {
        beforeInit();
    }
    @Inject(method = INIT_METHOD, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/screens/Screen;init()V",shift = At.Shift.AFTER))
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

    @Inject(method = "isPauseScreen", at = @At("HEAD"), cancellable = true)
    public void isPauseScreen(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(this.getBoolean("isPauseScreen", true));
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

    @Accessor("renderables")
    public abstract List<Renderable> getChildrenRenderables();

    @Override
    public <T extends GuiEventListener> T addChild(int renderableIndex, T listener, boolean isRenderable, boolean isNarratable) {
        children.add(listener);
        if (isRenderable && listener instanceof Renderable r) addRenderable(renderableIndex, r);
        if (isNarratable && listener instanceof NarratableEntry e) narratables.add(e);
        return listener;
    }

    @Override
    public <T extends GuiEventListener> T removeChild(T listener) {
        children.remove(listener);
        renderables.remove(listener);
        narratables.remove(listener);
        return listener;
    }

    @Inject(method = "renderBackground", at = @At(value = "HEAD"), cancellable = true)
    protected void renderBackground(CallbackInfo ci) {
        if (!getBoolean("hasBackground",true)) ci.cancel();
    }

    @Override
    public boolean initialized() {
        return initialized;
    }
}
