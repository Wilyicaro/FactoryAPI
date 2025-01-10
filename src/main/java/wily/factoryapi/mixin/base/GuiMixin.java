package wily.factoryapi.mixin.base;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.factoryapi.base.ArbitrarySupplier;
import wily.factoryapi.base.client.UIAccessor;
import wily.factoryapi.base.client.UIDefinition;
import wily.factoryapi.util.VariablesMap;

import java.util.*;
import java.util.regex.Pattern;

@Mixin(Gui.class)
public class GuiMixin implements UIAccessor {
    @Unique private final List<Renderable> renderables = new ArrayList<>();
    @Unique private final VariablesMap<String, ArbitrarySupplier<?>> elements = new VariablesMap<>();
    @Unique private final List<UIDefinition> definitions = new ArrayList<>();
    @Unique private final List<UIDefinition> staticDefinitions = new ArrayList<>();
    @Override
    public @Nullable Screen getScreen() {
        return null;
    }

    @Override
    public void reloadUI() {
    }

    @Override
    public boolean initialized() {
        return true;
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
        return Collections.emptyList();
    }

    @Override
    public List<Renderable> getRenderables() {
        return renderables;
    }

    @Override
    public <T extends GuiEventListener> T removeChild(T listener) {
        if (listener instanceof Renderable r) renderables.remove(r);
        return listener;
    }

    @Override
    public <T extends GuiEventListener> T addChild(int renderableIndex, T listener, boolean isRenderable, boolean isNarratable) {
        if (listener instanceof Renderable r) addRenderable(renderableIndex,r);
        return listener;
    }

    @Override
    public VariablesMap<String, ArbitrarySupplier<?>> getElements() {
        return elements;
    }

    @Inject(method = "tick()V", at = @At("HEAD"))
    public void beforeTick(CallbackInfo ci) {
        beforeTick();
    }

    @Inject(method = "tick()V", at = @At("RETURN"))
    public void afterTick(CallbackInfo ci) {
        afterTick();
    }
}
