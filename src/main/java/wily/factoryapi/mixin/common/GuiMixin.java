package wily.factoryapi.mixin.common;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import wily.factoryapi.base.ArbitrarySupplier;
import wily.factoryapi.base.client.UIDefinition;

import java.util.*;

@Mixin(Gui.class)
public class GuiMixin implements UIDefinition.Accessor {
    @Unique private final List<Renderable> renderables = new ArrayList<>();
    @Unique private final Map<String, ArbitrarySupplier<?>> elements = new HashMap<>();
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
    public <T extends GuiEventListener> T removeChildren(T listener) {
        if (listener instanceof Renderable r) renderables.remove(r);
        return listener;
    }

    @Override
    public <T extends GuiEventListener> T addChidren(T listener, boolean isRenderable, boolean isNarratable) {
        if (listener instanceof Renderable r) renderables.add(r);
        return listener;
    }

    @Override
    public <T extends Renderable> T addRenderable(T renderable) {
        renderables.add(renderable);
        return renderable;
    }

    @Override
    public Map<String, ArbitrarySupplier<?>> getElements() {
        return elements;
    }
}
