package wily.factoryapi.base.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import wily.factoryapi.FactoryAPIClient;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.base.ArbitrarySupplier;
import wily.factoryapi.base.Bearer;
import wily.factoryapi.util.FactoryScreenUtil;
import wily.factoryapi.util.VariableResolver;
import wily.factoryapi.util.VariablesMap;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface UIAccessor extends UIDefinition, VariableResolver {
    static UIAccessor of(Screen screen) {
        return (UIAccessor) screen;
    }

    static UIAccessor of(Gui gui) {
        return (UIAccessor) gui;
    }

    @Nullable
    Screen getScreen();

    void reloadUI();

    boolean initialized();

    @Override
    default void beforeInit(UIAccessor accessor) {
        getElements().clear();
        putStaticElement("windowWidth", Minecraft.getInstance().getWindow().getWidth());
        putStaticElement("windowHeight", Minecraft.getInstance().getWindow().getHeight());
        putStaticElement("width", Minecraft.getInstance().getWindow().getGuiScaledWidth());
        putStaticElement("height", Minecraft.getInstance().getWindow().getGuiScaledHeight());
        getElements().put("renderablesCount", getRenderables()::size);
        FactoryAPIPlatform.getMods().forEach(i -> putStaticElement("loadedMods." + i.getId(), true));
        getDefinitions().clear();
        FactoryAPIClient.uiDefinitionManager.applyStatic(accessor);
        getStaticDefinitions().stream().filter(d -> d.test(this)).forEach(getDefinitions()::add);
        FactoryAPIClient.uiDefinitionManager.apply(accessor);
        UIDefinition.super.beforeInit(accessor);
    }

    List<UIDefinition> getStaticDefinitions();

    default void staticInit() {
        beforeInit();
        getRenderables().clear();
        afterInit();
    }

    default void beforeInit() {
        beforeInit(this);
    }

    default void afterInit() {
        afterInit(this);
    }

    default void beforeTick() {
        beforeTick(this);
    }

    default void afterTick() {
        afterTick(this);
    }

    List<GuiEventListener> getChildren();

    List<Renderable> getRenderables();

    <T extends GuiEventListener> T removeChild(T widget);

    <T extends GuiEventListener> T addChild(int renderableIndex, T listener, boolean isRenderable, boolean isNarratable);

    default <T extends GuiEventListener> T addChild(int renderableIndex, T listener) {
        return addChild(renderableIndex, listener, true, true);
    }

    default <T extends GuiEventListener> T addChild(String name, T listener) {
        return addChild(getInteger(name + ".order", getRenderables().size()), listener);
    }

    default <T extends Renderable> T addRenderable(T renderable) {
        return addRenderable(getRenderables().size(), renderable);
    }

    default <T extends Renderable> T addRenderable(int index, T renderable) {
        getRenderables().add(Math.min(Math.max(0, index), getRenderables().size()), renderable);
        return renderable;
    }

    default <T extends Renderable> T addRenderable(String name, T renderable) {
        return addRenderable(getInteger(name + ".order", getRenderables().size()), renderable);
    }

    VariablesMap<String, ArbitrarySupplier<?>> getElements();

    @Override
    default VariablesMap.View getView() {
        return getElements().getView();
    }

    default <E> E putStaticElement(String name, E e) {
        getElements().put(name, ArbitrarySupplier.of(e));
        return e;
    }

    default <E> E putBearer(String name, Bearer<E> e, Function<Object, E> convertOldValue) {
        ArbitrarySupplier<?> oldElement = getElements().put(name, e);
        if (oldElement != null) oldElement.map(convertOldValue::apply).ifPresent(e::set);
        return e.get();
    }

    default <E> E putBearer(String name, Bearer<E> e) {
        return putBearer(name, e, o -> (E) o);
    }

    default Integer putIntegerBearer(String name, Bearer<Integer> e) {
        return putBearer(name, e, o -> o instanceof Number n ? Integer.valueOf(n.intValue()) : o instanceof String s ? Integer.parseInt(s) : null);
    }

    default <E extends AbstractWidget> E putWidget(String name, E e) {
        putBearer(name + ".message", Bearer.of(e::getMessage, e::setMessage));
        putBearer(name + ".spriteOverride", Bearer.of(WidgetAccessor.of(e)::getSpriteOverride, WidgetAccessor.of(e)::setSpriteOverride));
        putBearer(name + ".highlightedSpriteOverride", Bearer.of(WidgetAccessor.of(e)::getSpriteOverride, WidgetAccessor.of(e)::setHighlightedSpriteOverride));
        putBearer(name + ".onPressOverride", Bearer.of(WidgetAccessor.of(e)::getOnPressOverride, WidgetAccessor.of(e)::setOnPressOverride));
        return putLayoutElement(name, e, e::setWidth, /*? if <=1.20.1 {*//*WidgetAccessor.of(e)*//*?} else {*/e/*?}*/::setHeight);
    }

    default Component putComponent(String name, Component component) {
        putStaticElement(name, component);
        putStaticElement(name + ".width", Minecraft.getInstance().font.width(component));
        return component;
    }

    default Vec3 putVec3(String name, Vec3 offset) {
        putStaticElement(name, offset);
        putStaticElement(name + ".x", offset.x());
        putStaticElement(name + ".y", offset.y());
        putStaticElement(name + ".z", offset.z());
        return offset;
    }

    default Renderable createModifiableRenderable(String name, Renderable renderable) {
        return (guiGraphics, i, j, f) -> {
            int amount = getInteger(name+".amount", 1);
            for (int i1 = 0; i1 < amount; i1++) {
                if (getElements().get(name+".index") instanceof Bearer<?> b) b.secureCast(Integer.class).set(i1);
                guiGraphics.pose().pushPose();
                int color = getInteger(name+".renderColor", 0xFFFFFFFF);
                RenderSystem.enableBlend();
                FactoryGuiGraphics.of(guiGraphics).setColor(FactoryScreenUtil.getRed(color), FactoryScreenUtil.getGreen(color), FactoryScreenUtil.getBlue(color), FactoryScreenUtil.getAlpha(color));
                guiGraphics.pose().translate(getDouble(name + ".translateX", 0), getDouble(name + ".translateY", 0), getDouble(name + ".translateZ", 0));
                guiGraphics.pose().scale(getFloat(name + ".scaleX", 1), getFloat(name + ".scaleY", 1), getFloat(name + ".scaleZ", 1));
                renderable.render(guiGraphics, i, j, f);
                guiGraphics.pose().popPose();
                RenderSystem.disableBlend();
                FactoryGuiGraphics.of(guiGraphics).clearColor();
            }
        };
    }

    default <E extends LayoutElement> E putLayoutElement(String name, E e, Consumer<Integer> setWidth, Consumer<Integer> setHeight) {
        putIntegerBearer(name + ".x", Bearer.of(e::getX, e::setX));
        putIntegerBearer(name + ".y", Bearer.of(e::getY, e::setY));
        putIntegerBearer(name + ".width", Bearer.of(e::getWidth, setWidth));
        putIntegerBearer(name + ".height", Bearer.of(e::getHeight, setHeight));
        return putStaticElement(name, e);
    }


    default String replaceValidElementValues(String s) {
        getElements().updatePattern();
        Matcher matcher = getElements().getPattern().matcher(s);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String key = matcher.group(1);
            matcher.appendReplacement(result, String.valueOf(getElements().get(key).get()));
        }
        matcher.appendTail(result);
        return result.toString();
    }


    default <V> ArbitrarySupplier<V> getElement(String name, Class<V> valueClass) {
        return getElements().getOrDefault(name, ArbitrarySupplier.empty()).secureCast(valueClass);
    }

    default ArbitrarySupplier<?> getElement(String name) {
        return getElements().getOrDefault(name, ArbitrarySupplier.empty());
    }

    default ArbitrarySupplier<Boolean> getBooleanElement(String name) {
        return getElement(name, Boolean.class);
    }

    default ArbitrarySupplier<Integer> getIntegerElement(String name) {
        return getElement(name, Number.class).map(Number::intValue);
    }

    default <V> V getElementValue(String name, V defaultValue, Class<V> valueClass) {
        ArbitrarySupplier<?> element = getElements().get(name);
        Object value;
        return element != null && valueClass.isInstance(value = element.get()) ? valueClass.cast(value) : defaultValue;
    }

    default int getInteger(String name, int defaultValue) {
        return getElementValue(name, defaultValue, Number.class).intValue();
    }

    default double getDouble(String name, double defaultValue) {
        return getElementValue(name, defaultValue, Number.class).doubleValue();
    }

    default float getFloat(String name, float defaultValue) {
        return getElementValue(name, defaultValue, Number.class).floatValue();
    }

    default Boolean getBoolean(String name, Boolean defaultValue) {
        return getElementValue(name, defaultValue, Boolean.class);
    }

    default Boolean getBoolean(String name) {
        return getBoolean(name, false);
    }

    default ResourceLocation getResourceLocation(String name, ResourceLocation defaultValue) {
        return getElementValue(name, defaultValue, ResourceLocation.class);
    }

    default ResourceLocation getResourceLocation(String name) {
        return getResourceLocation(name, null);
    }

    default Component getComponent(String name, Component defaultValue) {
        return getElementValue(name, defaultValue, Component.class);
    }

    default Component getComponent(String name) {
        return getComponent(name, null);
    }

    @Override
    default Number getNumber(String name, Number defaultValue) {
        return getElementValue(name, defaultValue, Number.class);
    }

    static UIAccessor createRenderablesWrapper(UIAccessor accessor, List<Renderable> renderables){
        return new UIAccessor() {
            @Override
            public @Nullable Screen getScreen() {
                return accessor.getScreen();
            }

            @Override
            public List<UIDefinition> getDefinitions() {
                return accessor.getDefinitions();
            }

            @Override
            public void reloadUI() {
                accessor.reloadUI();
            }

            @Override
            public boolean initialized() {
                return accessor.initialized();
            }

            @Override
            public List<UIDefinition> getStaticDefinitions() {
                return accessor.getStaticDefinitions();
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
            public <T extends GuiEventListener> T removeChild(T widget) {
                if (widget instanceof Renderable) renderables.remove(widget);
                return widget;
            }

            @Override
            public <T extends GuiEventListener> T addChild(int renderableIndex, T listener, boolean isRenderable, boolean isNarratable) {
                if (isRenderable && listener instanceof Renderable r) renderables.add(renderableIndex, r);
                return listener;
            }

            @Override
            public VariablesMap<String, ArbitrarySupplier<?>> getElements() {
                return accessor.getElements();
            }
        };
    }
}
