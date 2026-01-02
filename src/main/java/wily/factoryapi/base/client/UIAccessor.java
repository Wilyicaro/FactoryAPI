package wily.factoryapi.base.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import wily.factoryapi.FactoryAPI;
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

public interface UIAccessor extends UIDefinition, VariableResolver {
    static UIAccessor of(Screen screen) {
        return (UIAccessor) screen;
    }

    static UIAccessor of(Gui gui) {
        return (UIAccessor) gui;
    }

    @Nullable
    Screen getScreen();

    default void reloadUI() {
        beforeInit();
        getChildrenRenderables().clear();
        afterInit();
    }

    boolean initialized();

    @Override
    default void beforeInit(UIAccessor accessor) {
        getElements().clear();
        putStaticElement("windowWidth", Minecraft.getInstance().getWindow().getWidth());
        putStaticElement("windowHeight", Minecraft.getInstance().getWindow().getHeight());
        putStaticElement("width", Minecraft.getInstance().getWindow().getGuiScaledWidth());
        putStaticElement("height", Minecraft.getInstance().getWindow().getGuiScaledHeight());
        getElements().put("hasScreen", ()-> Minecraft.getInstance().screen != null);
        if (getChildrenRenderables() != null)
            getElements().put("renderablesCount", getChildrenRenderables()::size);
        FactoryAPIPlatform.getMods().forEach(i -> putStaticElement("loadedMods." + i.getId(), true));
        ServerData serverData = Minecraft.getInstance().getCurrentServer();
        if (serverData != null)
            putStaticElement("serverIp."+serverData.ip,true);
        Inventory inventory = Minecraft.getInstance().player == null ? null : Minecraft.getInstance().player.getInventory();
        if (inventory != null) {
            List<ItemStack> items = inventory./*? if >1.21.4 {*/getNonEquipmentItems()/*?} else {*//*items*//*?}*/;
            for (int i = 0; i < items.size(); i++) {
                int index = i;
                getElements().put("inventory." + index, () -> items.get(index));
            }
            //? if >1.21.4 {
            Inventory.EQUIPMENT_SLOT_MAPPING.forEach((i, equipmentSlot)->{
                if (equipmentSlot == EquipmentSlot.OFFHAND) return;
                getElements().put("inventory.armor." + equipmentSlot.getIndex(), () -> inventory.getItem(i));
            });
            //?} else {
            /*for (int i = 0; i < inventory.armor.size(); i++) {
                int index = i;
                getElements().put("inventory.armor." + index, () -> inventory.armor.get(index));
            }
            *///?}
            getElements().put("inventory.offhand", () -> /*? if >1.21.4 {*/inventory.getItem(Inventory.SLOT_OFFHAND)/*?} else {*//*inventory.offhand.get(0)*//*?}*/);
        }
        putSupplierComponent("username", () -> Component.literal(Minecraft.getInstance().getUser().getName()));
        if (getScreen() instanceof MenuAccess<?> access) {
            getElements().put("slotsCount", access.getMenu().slots::size);
            for (Slot slot : access.getMenu().slots) {
                getElements().put("menu.slot." + slot.getContainerSlot(), slot::getItem);
            }
        }
        getDefinitions().clear();
        FactoryAPIClient.uiDefinitionManager.applyStatic(accessor);
        getDefinitions().addAll(getStaticDefinitions());
        FactoryAPIClient.uiDefinitionManager.apply(accessor);
        UIDefinition.super.beforeInit(accessor);
    }

    default void beforeInit() {
        beforeInit(this);
    }

    default void afterInit() {
        afterInit(this);
        if (FactoryOptions.UI_DEFINITION_LOGGING.get()) {
            FactoryAPI.LOGGER.warn(getElements());
        }
    }

    default void beforeTick() {
        beforeTick(this);
    }

    default void afterTick() {
        afterTick(this);
    }

    List<GuiEventListener> getChildren();

    List<Renderable> getChildrenRenderables();

    <T extends GuiEventListener> T removeChild(T widget);

    <T extends GuiEventListener> T addChild(int renderableIndex, T listener, boolean isRenderable, boolean isNarratable);

    default <T extends GuiEventListener> T addChild(int renderableIndex, T listener) {
        return addChild(renderableIndex, listener, true, true);
    }

    default <T extends GuiEventListener> T addChild(String name, T listener) {
        return addChild(getInteger(name + ".order", getChildrenRenderables().size()), listener);
    }

    default <T extends Renderable> T addRenderable(T renderable) {
        return addRenderable(getChildrenRenderables().size(), renderable);
    }

    default <T extends Renderable> T addRenderable(int index, T renderable) {
        getChildrenRenderables().add(Math.min(Math.max(0, index), getChildrenRenderables().size()), renderable);
        return renderable;
    }

    default <T extends Renderable> T addRenderable(String name, T renderable) {
        return addRenderable(getInteger(name + ".order", getChildrenRenderables().size()), renderable);
    }

    VariablesMap<String, ArbitrarySupplier<?>> getElements();

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
        getElement(name + ".tooltip", Component.class).ifPresent(c-> e.setTooltip(Tooltip.create(c)));
        putBearer(name + ".spriteOverride", Bearer.of(WidgetAccessor.of(e)::getSpriteOverride, WidgetAccessor.of(e)::setSpriteOverride));
        putBearer(name + ".highlightedSpriteOverride", Bearer.of(WidgetAccessor.of(e)::getSpriteOverride, WidgetAccessor.of(e)::setHighlightedSpriteOverride));
        putBearer(name + ".onPressOverride", Bearer.of(WidgetAccessor.of(e)::getOnPressOverride, WidgetAccessor.of(e)::setOnPressOverride));
        WidgetAccessor.of(e).setVisibility(getElement(name + ".isVisible", Boolean.class));
        return putLayoutElement(name, e, e::setWidth, /*? if <=1.20.1 {*//*WidgetAccessor.of(e)*//*?} else {*/e/*?}*/::setHeight);
    }

    default Component putComponent(String name, Component component) {
        putStaticElement(name, component);
        putStaticElement(name + ".width", Minecraft.getInstance().font.width(component));
        return component;
    }

    default void putSupplierComponent(String name, ArbitrarySupplier<Component> component) {
        getElements().put(name, component);
        getElements().put(name+".width", component.map(c->Minecraft.getInstance().font.width(c)));
    }

    default Vec3 putVec3(String name, Vec3 vec3) {
        putStaticElement(name, vec3);
        putStaticElement(name + ".x", vec3.x());
        putStaticElement(name + ".y", vec3.y());
        putStaticElement(name + ".z", vec3.z());
        return vec3;
    }

    default Vec2 putVec2(String name, Vec2 offset) {
        putStaticElement(name, offset);
        putStaticElement(name + ".x", offset.x);
        putStaticElement(name + ".y", offset.y);
        return offset;
    }

    default Renderable createModifiableRenderable(String name, Renderable renderable) {
        return (guiGraphics, i, j, f) -> {
            int amount = getInteger(name+".amount", 1);
            for (int i1 = 0; i1 < amount; i1++) {
                if (getElements().get(name+".index") instanceof Bearer<?> b) b.secureCast(Integer.class).set(i1);
                FactoryGuiMatrixStack.of(guiGraphics.pose()).pushPose();
                int color = getInteger(name+".renderColor", 0xFFFFFFFF);
                //? if <1.21.6 {
                /*FactoryScreenUtil.enableBlend();
                FactoryGuiGraphics.of(guiGraphics).setColor(color);
                *///?} else
                FactoryGuiGraphics.of(guiGraphics).setBlitColor(color);
                FactoryGuiMatrixStack.of(guiGraphics.pose()).translate(getDouble(name + ".translateX", 0), getDouble(name + ".translateY", 0), getDouble(name + ".translateZ", 0));
                FactoryGuiMatrixStack.of(guiGraphics.pose()).scale(getFloat(name + ".scaleX", 1), getFloat(name + ".scaleY", 1), getFloat(name + ".scaleZ", 1));
                renderable.render(guiGraphics, i, j, f);
                FactoryGuiMatrixStack.of(guiGraphics.pose()).popPose();
                FactoryScreenUtil.disableBlend();
                //? if <1.21.6 {
                /*FactoryGuiGraphics.of(guiGraphics).clearColor();
                *///?} else
                FactoryGuiGraphics.of(guiGraphics).clearBlitColor();

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

    default ItemStack getItemStack(String name) {
        return getElementValue(name, ItemStack.EMPTY, ItemStack.class);
    }

    default ItemStack getItemStack(String name, ItemStack defaultValue) {
        return getElementValue(name, defaultValue, ItemStack.class);
    }

    default Component getComponent(String name, Component defaultValue) {
        return getElementValue(name, defaultValue, Component.class);
    }

    default Component getComponent(String name) {
        return getComponent(name, null);
    }

    default Vec3 getVec3(String name, Vec3 defaultValue) {
        return getElementValue(name, defaultValue, Vec3.class);
    }

    default Vec3 getVec3(String name) {
        return getVec3(name, null);
    }

    @Override
    default Number getNumber(String name, Number defaultValue) {
        return getElementValue(name, defaultValue, Number.class);
    }

    static UIAccessor createRenderablesWrapper(UIAccessor accessor, List<Renderable> renderables) {
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
            public List<Renderable> getChildrenRenderables() {
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
