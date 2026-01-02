package wily.factoryapi.base.client;

import com.google.gson.JsonElement;
import com.mojang.realmsclient.RealmsMainScreen;
//? if >=1.21.6 {
import com.mojang.realmsclient.gui.screens.configuration.RealmsBackupScreen;
import com.mojang.realmsclient.gui.screens.configuration.RealmsInviteScreen;
//?} else {
/*import com.mojang.realmsclient.gui.screens.RealmsBackupScreen;
import com.mojang.realmsclient.gui.screens.RealmsInviteScreen;
*///?}
import com.mojang.realmsclient.gui.screens.RealmsConfirmScreen;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.minecraft.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.screens.*;
//? if <1.20.5 {
/*import net.minecraft.client.gui.screens.controls.ControlsScreen;
import net.minecraft.client.gui.screens.controls.KeyBindsScreen;
*///?} else {
import net.minecraft.client.gui.screens.options.*;
import net.minecraft.client.gui.screens.options.controls.*;
//?}
import net.minecraft.client.gui.screens.achievement.StatsScreen;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
import net.minecraft.client.gui.screens.inventory.*;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.client.gui.screens.telemetry.TelemetryInfoScreen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.EditWorldScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.repository.PackRepository;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.*;
import net.minecraft.world.item.ItemStack;
import wily.factoryapi.FactoryAPI;
import wily.factoryapi.FactoryAPIClient;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.base.ArbitrarySupplier;
import wily.factoryapi.base.Bearer;
import wily.factoryapi.util.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;
import java.util.function.*;
import java.util.stream.IntStream;

public class UIDefinitionManager implements ResourceManagerReloadListener {
    public static final String UI_DEFINITIONS = "ui_definitions";
    public static final ListMap<Identifier,Class<?>> NAMED_UI_TARGETS = new ListMap.Builder<String,Class<?>>().put("screen", Screen.class).put("accessibility_onboarding_screen", AccessibilityOnboardingScreen.class).put("title_screen",TitleScreen.class).put("options_screen", OptionsScreen.class).put("skin_customization_screen", SkinCustomizationScreen.class).put("video_settings_screen", VideoSettingsScreen.class).put("language_select_screen", LanguageSelectScreen.class).put("pack_selection_screen", PackSelectionScreen.class).put("telemetry_info_screen", TelemetryInfoScreen.class).put("online_options_screen", OnlineOptionsScreen.class).put("sound_options_screen", SoundOptionsScreen.class).put("controls_screen", ControlsScreen.class).put("mouse_settings_screen", MouseSettingsScreen.class).put("key_binds_screen", KeyBindsScreen.class).put("chat_options_screen", ChatOptionsScreen.class).put("accessibility_options_screen", AccessibilityOptionsScreen.class).put("credits_and_attribution_screen", CreditsAndAttributionScreen.class).put("win_screen", WinScreen.class).put("confirm_link_screen", ConfirmLinkScreen.class).put("select_world_screen", SelectWorldScreen.class).put("create_world_screen", CreateWorldScreen.class).put("edit_world_screen", EditWorldScreen.class).put("join_multiplayer_screen", JoinMultiplayerScreen.class)./*? if <1.21.9 {*//*put("edit_server_screen", EditServerScreen.class).*//*?}*/put("direct_join_server_screen", DirectJoinServerScreen.class).put("realms_main_screen", RealmsMainScreen.class).put("realms_screen", RealmsScreen.class).put("realms_confirm_screen", RealmsConfirmScreen.class).put("realms_backup_screen", RealmsBackupScreen.class).put("realms_invite_screen", RealmsInviteScreen.class).put("share_to_lan_screen", ShareToLanScreen.class).put("advancements_screen", AdvancementsScreen.class).put("stats_screen", StatsScreen.class).put("confirm_screen", ConfirmScreen.class).put("level_loading_screen", LevelLoadingScreen.class).put("progress_screen", ProgressScreen.class).put("generic_message_screen",/*? if <1.20.5 {*//*GenericDirtMessageScreen*//*?} else {*/GenericMessageScreen/*?}*/.class)./*? if <1.21.9 {*//*put("receiving_level_screen", ReceivingLevelScreen.class).*//*?}*/put("connect_screen", ConnectScreen.class).put("pause_screen", PauseScreen.class).put("inventory_screen", InventoryScreen.class).put("crafting_screen", CraftingScreen.class).put("container_screen", ContainerScreen.class).put("abstract_furnace_screen", AbstractFurnaceScreen.class).put("furnace_screen", FurnaceScreen.class).put("smoker_screen", SmokerScreen.class).put("blast_furnace_screen", BlastFurnaceScreen.class).put("loom_screen", LoomScreen.class).put("stonecutter_screen", StonecutterScreen.class).put("grindstone_screen", GrindstoneScreen.class).put("enchantment_screen", EnchantmentScreen.class).put("hopper_screen", HopperScreen.class).put("dispenser_screen", DispenserScreen.class).put("shulker_box_screen", ShulkerBoxScreen.class).put("anvil_screen", AnvilScreen.class).put("smithing_screen", SmithingScreen.class).put("brewing_stand_screen", BrewingStandScreen.class).put("beacon_screen", BeaconScreen.class).put("chat_screen", ChatScreen.class).put("in_bed_chat_screen", InBedChatScreen.class).put("gui", Gui.class).mapKeys(FactoryAPI::createVanillaLocation).build();
    public static final ListMap<Identifier, Function<Screen, Screen>> DEFAULT_SCREENS_MAP = new ListMap.Builder<String, Function<Screen, Screen>>().put("title", s -> new TitleScreen()).put("options", s -> new OptionsScreen(s, Minecraft.getInstance().options)).put("language_select", s -> new LanguageSelectScreen(s, Minecraft.getInstance().options, Minecraft.getInstance().getLanguageManager())).put("video_settings", s -> new VideoSettingsScreen(s,/*? if >=1.21 {*/Minecraft.getInstance() ,/*?}*/ Minecraft.getInstance().options)).put("skin_customization", s -> new SkinCustomizationScreen(s, Minecraft.getInstance().options)).put("online_options", s -> /*? if <1.21 {*//*OnlineOptionsScreen.createOnlineOptionsScreen(Minecraft.getInstance(), s, Minecraft.getInstance().options)*//*?} else {*/new OnlineOptionsScreen(s, Minecraft.getInstance().options)/*?}*/).put("controls", s -> new ControlsScreen(s, Minecraft.getInstance().options)).put("mouse_settings", s -> new MouseSettingsScreen(s, Minecraft.getInstance().options)).put("key_binds", s -> new KeyBindsScreen(s, Minecraft.getInstance().options)).put("chat_options", s -> new ChatOptionsScreen(s, Minecraft.getInstance().options)).put("accessibility_options", s -> new AccessibilityOptionsScreen(s, Minecraft.getInstance().options)).put("credits_and_attribution", CreditsAndAttributionScreen::new).put("select_world", SelectWorldScreen::new).mapKeys(FactoryAPI::createVanillaLocation).build();


    public static void registerNamedUITarget(Identifier id, Class<?> uiClass) {
        NAMED_UI_TARGETS.put(id, uiClass);
    }

    public static void registerNamedUITarget(String path, Class<?> uiClass) {
        registerNamedUITarget(FactoryAPI.createVanillaLocation(path), uiClass);
    }

    public static void registerDefaultScreen(Identifier id, Function<Screen, Screen> defaultScreen) {
        DEFAULT_SCREENS_MAP.put(id, defaultScreen);
    }

    public static void registerDefaultScreen(String path, Function<Screen, Screen> defaultScreen) {
        registerDefaultScreen(FactoryAPI.createVanillaLocation(path), defaultScreen);
    }

    @Override
    public String getName() {
        return "factoryapi:ui_definition_manager";
    }


    public record ElementValue<T>(String name, Function<UIAccessor, T> value) implements UIDefinition {
        @Override
        public void beforeInit(UIAccessor accessor) {
            accessor.getElements().put(name, () -> value.apply(accessor));
        }
    }

    public interface WidgetAction<P, W extends AbstractWidget> {
        ListMap<Identifier, WidgetAction<?, AbstractWidget>> map = new ListMap.Builder<String, WidgetAction<?, AbstractWidget>>().put("open_default_screen", create(Identifier.CODEC, (s) -> (a, w, t) -> Minecraft.getInstance().setScreen(DEFAULT_SCREENS_MAP.getOrDefault(s, s1 -> null).apply(a.getScreen())))).put("open_config_screen", create(Codec.STRING, (s) -> (a, w, t) -> Minecraft.getInstance().setScreen(FactoryAPIClient.getConfigScreen(FactoryAPIPlatform.getModInfo(s), a.getScreen())))).put("reload_ui", create(Codec.unit(Unit.INSTANCE), (s) -> (a, w, t) -> a.reloadUI())).put("run_command", createRunCommand(s -> true)).put("run_windows_command", createRunCommand(s -> Util.getPlatform() == Util.OS.WINDOWS)).put("run_linux_command", createRunCommand(s -> Util.getPlatform() == Util.OS.LINUX)).put("run_osx_command", createRunCommand(s -> Util.getPlatform() == Util.OS.OSX)).put("toggle_datapacks", createToggleDatapacks()).mapKeys(FactoryAPI::createVanillaLocation).build();
        Codec<WidgetAction<?, AbstractWidget>> CODEC = map.createCodec(Identifier.CODEC);

        Codec<P> getCodec();

        enum Type {
            ENABLE,DISABLE
        }

        interface PressSupplier<W extends AbstractWidget> {
            void press(UIAccessor accessor, W widget, Type type);
        }

        PressSupplier<W> press(P result);

        record CycleEntry(Component message, List<PressSupplier<AbstractWidget>> actions) {

        }

        default Optional<PressSupplier<W>> press(Predicate<UIAccessor> canApply, Dynamic<?> dynamic) {
            return getCodec().parse(dynamic).result().or(() -> dynamic.get("value").get().result().flatMap(d -> getCodec().parse(d).result())).map(p -> (a, w, type) -> {
                if (canApply.test(a)) press(p).press(a, w, type);
            });
        }

        static WidgetAction<String, AbstractWidget> createRunCommand(Predicate<String> shouldRun) {
            return create(Codec.STRING, (s) -> (a, w, t) -> {
                if (shouldRun.test(s)) {
                    try {
                        new ProcessBuilder(s.split(" ")).start();
                    } catch (IOException e) {
                        FactoryAPI.LOGGER.warn(e.getMessage());
                    }
                }
            });
        }



        static WidgetAction<List<String>, AbstractWidget> createToggleDatapacks() {
            return create(Codec.STRING.listOf(), (s) -> (a, w, t) -> {
                if (a instanceof DatapackRepositoryAccessor datapackAccessor) {
                    PackRepository repo = datapackAccessor.getDatapackRepository();
                    switch (t) {
                        case ENABLE -> {
                            List<String> datapacks = new ArrayList<>(s);
                            datapacks.removeIf(pack-> !repo.isAvailable(pack));
                            Collections.reverse(datapacks);
                            datapacks.addAll(0, repo.getSelectedIds());
                            repo.setSelected(datapacks);
                            datapackAccessor.tryApplyNewDataPacks(repo);
                        }
                        case DISABLE -> {
                            List<String> datapacks = new ArrayList<>(repo.getSelectedIds());
                            datapacks.removeIf(s::contains);
                            repo.setSelected(datapacks);
                            datapackAccessor.tryApplyNewDataPacks(repo);
                        }
                    }
                }
            });
        }

        static <P, W extends AbstractWidget> WidgetAction<P, W> create(Codec<P> codec, Function<P,PressSupplier<W>> onPress) {
            return new WidgetAction<>() {
                @Override
                public Codec<P> getCodec() {
                    return codec;
                }

                @Override
                public PressSupplier<W> press(P result) {
                    return onPress.apply(result);
                }
            };
        }
    }

    public static Checkbox createCheckbox(boolean selected, BiConsumer<Checkbox, Boolean> onPress) {
        //? if >1.20.1 {
        return Checkbox.builder(Component.empty(), Minecraft.getInstance().font).selected(selected).onValueChange(onPress::accept).build();
        //?} else {
        /*return new Checkbox(0,0,20,20,Component.empty(), selected) {
            @Override
            public void onPress() {
                super.onPress();
                onPress.accept(this,selected());
            }
        };
        *///?}
    }

    public static List<Integer> parseIntRange(String s) {
        if (!s.contains(",")) return Collections.singletonList(Integer.parseInt(s));
        String[] numbers = s.split(",");
        List<Integer> range = new ArrayList<>();
        for (int i = 0; i < numbers.length; i++) {
            boolean closed;
            if ((closed = numbers[i].startsWith("[")) || numbers[i].startsWith("]")) {
                int start = Integer.parseInt(numbers[i].substring(1));
                i++;
                boolean endClosed;
                if (!(endClosed = numbers[i].endsWith("]")) && !numbers[i].endsWith("[")) {
                    FactoryAPI.LOGGER.warn("Incorrect integer interval syntax at ordinal {}, skipping this. \nInteger Range: {}",i-1,s);
                }else {
                    int end = Integer.parseInt(numbers[i].replace(endClosed ? "]" : "[",""));
                    IntStream.rangeClosed(start + (closed ? 0: -Mth.sign(start)),end + (endClosed ? 0: -Mth.sign(end))).forEach(range::add);
                }
            } else range.add(Integer.parseInt(numbers[i]));
        }
        return range;
    }

    public interface ElementType {
        ListMap<Identifier, ElementType> map = new ListMap<>();

        ElementType CHILDREN = registerConditional("children", (definition, accessorFunction, name, e) -> parseAllElements(definition, accessorFunction, e, s -> e.get("applyPrefix").asBoolean(true) ? (name+"."+s) : s));
        ElementType ADD_BUTTON = registerConditional("add_button", (definition, accessorFunction, name, e) -> {
            List<WidgetAction.PressSupplier<AbstractWidget>> actions = parseActionsElement(definition, name, e);
            parseWidgetElements(definition, name, e);
            definition.addStatic(UIDefinition.createAfterInit(a -> a.putWidget(name, accessorFunction.apply(a).addChild(name, Button.builder(Component.empty(), b -> actions.forEach(c -> c.press(a, b, WidgetAction.Type.ENABLE))).build()))));
        });
        ElementType ADD_CHECKBOX = registerConditional("add_checkbox", (definition, accessorFunction, name, e) -> {
            List<WidgetAction.PressSupplier<AbstractWidget>> actions = parseActionsElement(definition, name, e);
            parseWidgetElements(definition, name, e);
            parseElement(definition, name, e, "selected", ElementType::parseBoolean);
            definition.addStatic(UIDefinition.createAfterInit(a -> a.putWidget(name, accessorFunction.apply(a).addChild(name, createCheckbox(a.getBoolean(name + ".selected"), (c, b) -> {
                actions.forEach(c1 -> c1.press(a, c, c.selected() ? WidgetAction.Type.ENABLE : WidgetAction.Type.DISABLE));
            })))));
        });
        ElementType ADD_SLIDER = registerConditional("add_slider", (definition, accessorFunction, name, e) -> {
            List<WidgetAction.CycleEntry> entries = e.get("entries").asList(d-> new WidgetAction.CycleEntry(d.get("message").flatMap(DynamicUtil.getComponentCodec()::parse).result().orElse(Component.empty()), parseActionsElement(definition, name, d)));
            parseElement(definition, name, e, "initialIndex", ElementType::parseNumber);
            parseWidgetElements(definition, name, e);
            definition.addStatic(UIDefinition.createAfterInit(a -> {
                a.putWidget(name, accessorFunction.apply(a).addChild(name, new AbstractSliderButton(0, 0, 200, 20, Component.empty(), Math.min(entries.size() - 1, a.getInteger(name + "initialIndex", 0) / (entries.size() - 1d))) {
                    WidgetAction.CycleEntry entry = getActualEntry();

                    @Override
                    protected void updateMessage() {
                        setMessage(entry.message());
                    }

                    private WidgetAction.CycleEntry getActualEntry() {
                        return entries.get((int) ((entries.size() - 1 ) * value));
                    }

                    @Override
                    protected void applyValue() {
                        WidgetAction.CycleEntry oldEntry = entry;
                        entry = getActualEntry();
                        if (!entry.equals(oldEntry)) {
                            oldEntry.actions().forEach(action-> action.press(a, this, WidgetAction.Type.DISABLE));
                            entry.actions().forEach(action -> action.press(a, this, WidgetAction.Type.ENABLE));
                        }
                    }
                })).updateMessage();
            }));
        });
        ElementType MODIFY_WIDGET = registerConditional("modify_widget", createIndexable(i -> (definition, accessorFunction, name, e) -> {
            List<WidgetAction.PressSupplier<AbstractWidget>> actions = parseActionsElement(definition, name, e);
            i.forEach(index -> parseWidgetElements(definition, name + (i.size() == 1 ? "" : "_" + index), e));
            definition.addStatic(UIDefinition.createAfterInit(a -> {
                Consumer<AbstractWidget> onPressOverride = actions.isEmpty() ? null : w-> actions.forEach(action-> action.press(a, w, WidgetAction.Type.ENABLE));
                Bearer<Integer> count = Bearer.of(0);
                a.getElements().put(name + ".index", count);
                for (Integer index : i)
                    if (a.getChildren().size() > index && a.getChildren().get(index) instanceof AbstractWidget w) {
                        String suffixedName = name + (i.size() == 1 ? "" : "_" + index);
                        if (onPressOverride != null) a.putStaticElement(suffixedName+".onPressOverride", onPressOverride);
                        accessorFunction.apply(a).putWidget(suffixedName, w);
                        count.set(count.get() + 1);
                    }
            }));
        }));
        ElementType REMOVE_WIDGET = registerConditional("remove_widget", createIndexable(i -> (definition, accessorFunction, name, e) -> definition.addStatic(UIDefinition.createAfterInit(a -> {
            for (Integer index : i)
                if (a.getChildren().size() > index)
                    accessorFunction.apply(a).removeChild(a.getChildren().get(index));
        }))));
        ElementType PUT_NUMBER = registerConditional("put_number", (definition, accessorFunction, name, e) -> parseElement(definition, name, e, "value", (s, d) -> parseNumber(name, d)));
        ElementType PUT_COMPONENT = registerConditional("put_component", (definition, accessorFunction, name, e) -> parseElement(definition, name, e, "value", (s, d) -> parseComponentElement(name, d)));
        ElementType PUT_STRING = registerCodec("put_string", Codec.STRING);
        ElementType PUT_BOOLEAN = registerConditional("put_boolean", (definition, accessorFunction, name, e) -> parseElement(definition, name, e, "value", (s, d) -> parseBoolean(name, d)));
        ElementType PUT_RESOURCE_LOCATION = registerCodec("put_resource_location", Identifier.CODEC);
        ElementType PUT_VEC3 = registerCodec("put_vec3", DynamicUtil.VEC3_OBJECT_CODEC);
        ElementType PUT_VEC2 = registerCodec("put_vec2", DynamicUtil.VEC2_CODEC);
        ElementType BLIT = registerConditional("blit", ElementType::parseBlitElements);
        ElementType BLIT_SPRITE = registerConditional("blit_sprite", ElementType::parseBlitSpriteElements);
        ElementType FILL = registerConditional("fill", ElementType::parseFillElements);
        ElementType FILL_GRADIENT = registerConditional("fill_gradient", ElementType::parseFillGradientElements);
        ElementType DRAW_STRING = registerConditional("draw_string", ElementType::parseDrawStringElements);
        ElementType DRAW_MULTILINE_STRING = registerConditional("draw_multiline_string", ElementType::parseDrawMultilineStringElements);
        ElementType RENDER_ITEM = registerConditional("render_item", ElementType::parseRenderItemElements);
        ElementType RENDER_ITEMS = registerConditional("render_items", ElementType::parseRenderItemsElements);
        ElementType COMPARE_ITEMS = registerConditional("compare_items", ((uiDefinition, accessorFunction, elementName, element) -> {
            parseElements(uiDefinition, elementName, element, ElementType::parseItemStackElement, "firstItem", "secondItem");
            parseElements(uiDefinition, elementName, element, ElementType::parseBoolean, "checkCount", "strict");
            uiDefinition.addStatic(UIDefinition.createBeforeInit(a -> a.getElements().put(elementName, () -> FactoryItemUtil.compareItems(a.getItemStack(elementName+".firstItem"), a.getItemStack(elementName+".secondItem"), a.getBoolean(elementName+".checkCount",true), a.getBoolean(elementName+".strict",true)))));
        }));
        ElementType CHANCE = registerConditional("chance", ((uiDefinition, accessorFunction, elementName, element) -> {
            RandomSource rand = RandomSource.create();
            parseElements(uiDefinition, elementName, element, ElementType::parseNumber, "min", "max");
            uiDefinition.addStatic(UIDefinition.createBeforeInit(a -> a.getElements().put(elementName, () -> rand.nextInt(a.getInteger(elementName+".min", 0), a.getInteger(elementName+".max", 0)))));
        }));

        static void parseWidgetElements(UIDefinition uiDefinition, String elementName, Dynamic<?> element) {
            parseElements(uiDefinition, elementName, element, ElementType::parseNumber, "x", "y", "width", "height", "order");
            parseElements(uiDefinition, elementName, element, (s, d) -> parseComponentElement(elementName, s, d), "message", "tooltip");
            parseElement(uiDefinition, elementName, element, "spriteOverride", Identifier.CODEC);
            parseElement(uiDefinition, elementName, element, "highlightedSpriteOverride", Identifier.CODEC);
            parseElement(uiDefinition, elementName, element, "isVisible", ElementType::parseBoolean);
        }

        static void parseFillElements(UIDefinition uiDefinition, Function<UIAccessor, UIAccessor> accessorFunction, String elementName, Dynamic<?> element) {
            parseElements(uiDefinition, elementName, element, ElementType::parseNumber, "x", "y", "width", "height", "color", "order");
            parseTranslationElements(uiDefinition, elementName, element);
            uiDefinition.addStatic(UIDefinition.createAfterInit(a -> accessorFunction.apply(a).addRenderable(elementName, (a.createModifiableRenderable(elementName, (guiGraphics, i, j, f) -> guiGraphics.fill(a.getInteger(elementName + ".x", 0), a.getInteger(elementName + ".y", 0), a.getInteger(elementName + ".x", 0) + a.getInteger(elementName + ".width", 0), a.getInteger(elementName + ".y", 0) + a.getInteger(elementName + ".height", 0), a.getInteger(elementName + ".color", 0xFFFFFFFF)))))));
        }

        static void parseFillGradientElements(UIDefinition uiDefinition, Function<UIAccessor, UIAccessor> accessorFunction, String elementName, Dynamic<?> element) {
            parseElements(uiDefinition, elementName, element, ElementType::parseNumber, "x", "y", "width", "height", "color", "secondColor", "order");
            parseTranslationElements(uiDefinition, elementName, element);
            uiDefinition.addStatic(UIDefinition.createAfterInit(a -> accessorFunction.apply(a).addRenderable(elementName, (a.createModifiableRenderable(elementName, (guiGraphics, i, j, f) -> guiGraphics.fillGradient(a.getInteger(elementName + ".x", 0), a.getInteger(elementName + ".y", 0), a.getInteger(elementName + ".x", 0) + a.getInteger(elementName + ".width", 0), a.getInteger(elementName + ".y", 0) + a.getInteger(elementName + ".height", 0), a.getInteger(elementName + ".color", 0xFFFFFFFF), a.getInteger(elementName + ".secondColor", 0xFFFFFFFF)))))));
        }

        static void parseBlitElements(UIDefinition uiDefinition, Function<UIAccessor, UIAccessor> accessorFunction, String elementName, Dynamic<?> element) {
            parseElement(uiDefinition, elementName, element, "texture", Identifier.CODEC);
            parseElements(uiDefinition, elementName, element, ElementType::parseNumber, "x", "y", "uvX", "uvY", "width", "height", "imageWidth", "imageHeight", "renderColor", "order", "amount");
            parseTranslationElements(uiDefinition, elementName, element);
            uiDefinition.addStatic(UIDefinition.createAfterInitWithAmount(elementName, a -> accessorFunction.apply(a).addRenderable(elementName, (a.createModifiableRenderable(elementName, (guiGraphics, i, j, f) -> a.getElement(elementName + ".texture", Identifier.class).ifPresent(t -> FactoryGuiGraphics.of(guiGraphics).blit(t, a.getInteger(elementName + ".x", 0), a.getInteger(elementName + ".y", 0), a.getInteger(elementName + ".uvX", 0), a.getInteger(elementName + ".uvY", 0), a.getInteger(elementName + ".width", 0), a.getInteger(elementName + ".height", 0), a.getInteger(elementName + ".imageWidth", 256), a.getInteger(elementName + ".imageHeight", 256))))))));
        }

        static void parseTranslationElements(UIDefinition uiDefinition, String elementName, Dynamic<?> element) {
            parseElements(uiDefinition, elementName, element, ElementType::parseNumber,"translateX", "translateY", "translateZ", "scaleX", "scaleY", "scaleZ");
        }

        static void parseBlitSpriteElements(UIDefinition uiDefinition, Function<UIAccessor, UIAccessor> accessorFunction, String elementName, Dynamic<?> element) {
            parseElement(uiDefinition, elementName, element, "sprite", Identifier.CODEC);
            parseElements(uiDefinition, elementName, element, ElementType::parseNumber, "x", "y", "width", "height", "renderColor", "order", "amount");
            parseTranslationElements(uiDefinition, elementName, element);
            uiDefinition.addStatic(UIDefinition.createAfterInitWithAmount(elementName, a -> accessorFunction.apply(a).addRenderable(elementName, (a.createModifiableRenderable(elementName, (guiGraphics, i, j, f) -> a.getElement(elementName + ".sprite", Identifier.class).ifPresent(t -> FactoryGuiGraphics.of(guiGraphics).blitSprite(t, a.getInteger(elementName + ".x", 0), a.getInteger(elementName + ".y", 0), a.getInteger(elementName + ".width", 0), a.getInteger(elementName + ".height", 0))))))));
        }

        static void parseTextElements(UIDefinition uiDefinition, String elementName, Dynamic<?> element) {
            parseElement(uiDefinition, elementName, element, "component", (s, d) -> parseComponentElement(elementName, s, d));
            parseElement(uiDefinition, elementName, element, "shadow", ElementType::parseBoolean);
            parseElements(uiDefinition, elementName, element, ElementType::parseNumber, "x", "y", "color", "order");
        }

        static void parseDrawStringElements(UIDefinition uiDefinition, Function<UIAccessor, UIAccessor> accessorFunction, String elementName, Dynamic<?> element) {
            parseTextElements(uiDefinition, elementName, element);
            parseTranslationElements(uiDefinition, elementName, element);
            uiDefinition.addStatic(UIDefinition.createAfterInit(a -> accessorFunction.apply(a).addRenderable(elementName, (a.createModifiableRenderable(elementName, (guiGraphics, i, j, f) -> a.getElement(elementName + ".component", Component.class).ifPresent(c -> guiGraphics.drawString(Minecraft.getInstance().font, c, a.getInteger(elementName + ".x", 0), a.getInteger(elementName + ".y", 0), a.getInteger(elementName + ".color", 0xFFFFFFFF), a.getBoolean(elementName + ".shadow", true))))))));
        }

        static void parseDrawMultilineStringElements(UIDefinition uiDefinition, Function<UIAccessor, UIAccessor> accessorFunction, String elementName, Dynamic<?> element) {
            parseTextElements(uiDefinition, elementName, element);
            parseElements(uiDefinition, elementName, element, ElementType::parseNumber, "lineSpacing", "width");
            parseElement(uiDefinition, elementName, element, "centered", ElementType::parseBoolean);
            uiDefinition.addStatic(UIDefinition.createAfterInit(a -> {
                a.getElement(elementName+".component", Component.class).ifPresent(c-> {
                    int lineSpacing = a.getInteger(elementName+".lineSpacing", 12);
                    int width = a.getInteger(elementName+".width", 0);
                    AdvancedTextWidget advancedTextWidget = a.putLayoutElement(elementName, accessorFunction.apply(a).addChild(elementName, new AdvancedTextWidget(a).lineSpacing(lineSpacing).withLines(c,width).withColor(a.getInteger(elementName + ".color", 0xFFFFFFFF)).withShadow(a.getBoolean(elementName + ".shadow", true)).centered(a.getBoolean(elementName + ".centered", false))), i-> {}, i->{});
                    a.putStaticElement(elementName+".linesCount", advancedTextWidget.getLines().size());
                });
            }));
        }

        static void parseRenderItemsElements(UIDefinition uiDefinition, Function<UIAccessor, UIAccessor> accessorFunction, String elementName, Dynamic<?> element) {
            parseElements(uiDefinition, elementName, element, ElementType::parseBoolean,"isFake","allowDecorations");
            parseElement(uiDefinition, elementName, element, "items", (s, d) ->d.asListOpt(d1->DynamicUtil.getItemFromDynamic(d1, true)).result().map(l-> UIDefinition.createBeforeInit((a) -> a.putStaticElement(s,l.stream().map(ArbitrarySupplier::get).toArray(ItemStack[]::new)))).orElse(null));
            parseElements(uiDefinition, elementName, element, ElementType::parseNumber, "x", "y", "order");
            parseTranslationElements(uiDefinition, elementName, element);
            uiDefinition.addStatic(UIDefinition.createAfterInit(a -> {
                UIAccessor accessor = accessorFunction.apply(a);
                Bearer<Integer> index = Bearer.of(0);
                a.putBearer( elementName + ".index",index);
                a.getElement(elementName + ".items", ItemStack[].class).ifPresent(stacks-> {
                    accessor.putStaticElement(elementName + ".amount",stacks.length);
                    accessor.addRenderable(elementName, (a.createModifiableRenderable(elementName, (guiGraphics, i, j, f) -> {
                                ItemStack s = stacks[a.getInteger(elementName+".index",0)];
                                int x = a.getInteger(elementName + ".x", 0);
                                int y = a.getInteger(elementName + ".y", 0);
                                if (a.getBoolean(elementName + ".isFake", false))
                                    guiGraphics.renderFakeItem(s, x, y);
                                else guiGraphics.renderItem(s, x, y);
                                if (a.getBoolean(elementName + ".allowDecorations", true))
                                    guiGraphics.renderItemDecorations(Minecraft.getInstance().font, s, x, y);
                            }
                    )));
                });
            }));
        }

        static void parseRenderItemElements(UIDefinition uiDefinition, Function<UIAccessor, UIAccessor> accessorFunction, String elementName, Dynamic<?> element) {
            parseElements(uiDefinition, elementName, element, ElementType::parseBoolean,"isFake","allowDecorations");
            parseElement(uiDefinition, elementName, element, "item", (s,d) -> parseItemStackElement(s, d));
            parseElements(uiDefinition, elementName, element, ElementType::parseNumber, "x", "y", "order", "amount");
            parseTranslationElements(uiDefinition, elementName, element);
            uiDefinition.addStatic(UIDefinition.createAfterInit(a -> accessorFunction.apply(a).addRenderable(elementName, (a.createModifiableRenderable(elementName, (guiGraphics, i, j, f) -> a.getElement(elementName + ".item", ItemStack.class).ifPresent(s-> {
                int x = a.getInteger(elementName + ".x", 0);
                int y = a.getInteger(elementName + ".y", 0);
                if (a.getBoolean(elementName + ".isFake", false))
                    guiGraphics.renderFakeItem(s, x, y);
                else guiGraphics.renderItem(s, x ,y);
                if (a.getBoolean(elementName+".allowDecorations", true)) guiGraphics.renderItemDecorations(Minecraft.getInstance().font, s, x, y);
            }))))));
        }

        static List<WidgetAction.PressSupplier<AbstractWidget>> parseActionsElement(UIDefinition uiDefinition, String elementName, Dynamic<?> element) {
            return element.get("actions").asMapOpt().result().map(m -> m.map(p -> {
                String actionName = elementName + ".actions." + p.getFirst().asString("");
                parseElement(uiDefinition, actionName, p.getSecond(), "applyCondition", ElementType::parseBoolean);
                return WidgetAction.CODEC.parse(p.getFirst()).result().flatMap(c -> c.press(a -> a.getBoolean(actionName + ".applyCondition", true), p.getSecond()));
            }).filter(Optional::isPresent).map(Optional::get).toList()).orElse(Collections.emptyList());
        }

        static ElementValue<ItemStack> parseItemStackElement(String field, Dynamic<?> element) {
            ArbitrarySupplier<ItemStack> stackSupplier = DynamicUtil.getItemFromDynamic(element, true);
            return new ElementValue<>(field, a -> stackSupplier.get());
        }

        @Deprecated
        static UIDefinition parseItemStackElement(String elementName, String field, Dynamic<?> element) {
            return parseItemStackElement(field, element);
        }

        static ElementValue<Number> parseNumber(String field, Dynamic<?> element) {
            Optional<Number> numberResult = element.asNumber().result();
            if (numberResult.isPresent()) return numberResult.map(n-> new ElementValue<>(field, a -> n)).orElse(null);
            else return element.asString().map(ExpressionEvaluator::of).map(e -> new ElementValue<>(field, e::evaluate)).result().orElse(null);
        }

        @Deprecated
        static UIDefinition parseNumberElement(String elementName, String field, Dynamic<?> element) {
            return parseNumber(field, element);
        }

        static ElementValue<Boolean> parseBooleanElementOrReference(String elementName, Dynamic<?> element) {
            return UIDefinitionManager.ElementType.<Boolean>parseElementReference(elementName, element).orElseGet(() -> parseBoolean(elementName, element));
        }

        static <T> ElementValue<Boolean> parseBoolean(String field, Dynamic<T> element) {
            Optional<Boolean> booleanResult = element.getOps().getBooleanValue(element.getValue()).result();
            if (booleanResult.isPresent()) return booleanResult.map(n -> new ElementValue<>(field, a -> n)).orElse(null);
            else return element.asString().map(BooleanExpressionEvaluator::of).map(e -> new ElementValue<>(field, e::evaluate)).result().orElse(null);
        }

        @Deprecated
        static <T> UIDefinition parseBooleanElement(String elementName, String field, Dynamic<T> element) {
            return parseBoolean(field, element);
        }

        static UIDefinition parseExternalComponentElement(String elementName, String field, Dynamic<?> element) {
            return element.get("baseDir").flatMap(Identifier.CODEC::parse).result().map(r-> {
                Map<String,Component> componentByLang = new HashMap<>();
                for (String s : Minecraft.getInstance().getLanguageManager().getLanguages().keySet()) {
                    Identifier location = r.withSuffix("/" + s + ".txt");
                    Optional<Resource> externalComponent = Minecraft.getInstance().getResourceManager().getResource(location);
                    externalComponent.ifPresent(resource -> {
                        MutableComponent c = Component.empty();
                        try (BufferedReader reader = resource.openAsReader()) {
                            reader.lines().forEach(l -> {
                                c.append(l);
                                c.append("\n");
                            });
                            componentByLang.put(s, c);
                        } catch (IOException e) {
                            FactoryAPI.LOGGER.warn("Failed to parse {}, this external component won't be loaded.", location, e);
                        }
                    });
                }
                return UIDefinition.createBeforeInit(a -> {
                    String lang;
                    if (componentByLang.containsKey(lang = Minecraft.getInstance().getLanguageManager().getSelected()) || componentByLang.containsKey(lang = "en_us"))
                        a.putComponent(field, componentByLang.get(lang));
                });
            }).orElse(null);
        }

        static UIDefinition parseComponentElement(String elementName, String field, Dynamic<?> element) {
            UIDefinition externalComponentUIDefinition = parseExternalComponentElement(elementName, field, element);
            if (externalComponentUIDefinition != null) return externalComponentUIDefinition;
            return (element.get("allowVariables").asBoolean(false) ? element.get("translate").flatMap(d-> d.asString().map(s-> UIDefinition.createBeforeInit(a -> a.putComponent(field,Component.translatable(s, element.get("args").asStream().map(d1->d1.asString().result().map(k-> a.getElementValue(k, null, Object.class))).filter(Optional::isPresent).map(Optional::get).toArray(Object[]::new)))))) : DynamicUtil.getComponentCodec().parse(element).map(c -> UIDefinition.createBeforeInit(a -> a.putComponent(field, c)))).result().orElse(null);
        }

        static UIDefinition parseComponentElement(String elementName, Dynamic<?> element) {
            return parseComponentElement(elementName, elementName, element);
        }

        static void parseElements(UIDefinition uiDefinition, String elementName, Dynamic<?> element, BiFunction<String, Dynamic<?>, UIDefinition> dynamicToDefinition, String... fields) {
            for (String field : fields) {
                parseElement(uiDefinition, elementName, element, field, dynamicToDefinition);
            }
        }

        static void parseElement(UIDefinition uiDefinition, String elementName, Dynamic<?> element, String field, Codec<?> codec) {
            parseElement(uiDefinition, elementName, element, field, (s, d) -> codec.parse(d).result().map(c -> UIDefinition.createBeforeInit(a -> a.putStaticElement(s, c))).orElse(null));
        }

        static void parseElement(UIDefinition uiDefinition, String elementName, Dynamic<?> element, String field, BiFunction<String, Dynamic<?>, UIDefinition> dynamicToDefinition) {
            element.get(field).result().map(d -> parseElementReference(elementName + "." + field, d).map(value -> (UIDefinition) value).orElseGet(() -> dynamicToDefinition.apply(elementName + "." + field, d))).ifPresent(uiDefinition::addStatic);
        }

        static <T> Optional<ElementValue<T>> parseElementReference(String field, Dynamic<?> element) {
            return element.get("reference").asString().result().map(s -> new ElementValue<>(field, a -> (T) a.getElement(s).get()));
        }

        static ElementType get(String id) {
            return get(Identifier.tryParse(id));
        }

        static ElementType get(Identifier id) {
            return map.getOrDefault(id, PUT_NUMBER);
        }

        static Identifier getId(ElementType type) {
            return map.getKeyOrDefault(type, null);
        }

        void parse(UIDefinition uiDefinition, Function<UIAccessor, UIAccessor> accessorFunction, String elementName, Dynamic<?> element);

        default void parse(UIDefinition uiDefinition, String elementName, Dynamic<?> element) {
            parse(uiDefinition, a -> a, elementName, element);
        }

        static ElementType createIndexable(Function<List<Integer>, ElementType> parser) {
            return ((uiDefinition, accessorFunction, elementName, element) -> parser.apply(element.get("range").asString().result().map(UIDefinitionManager::parseIntRange).orElse(List.of(element.get("index").asInt(0)))).parse(uiDefinition, accessorFunction, elementName, element));
        }

        static ElementType createConditional(ElementType type) {
            return ((uiDefinition, accessorFunction, elementName, element) -> {
                Optional<ElementValue<Boolean>> applyCondition = element.get("applyCondition").map(e -> UIDefinitionManager.ElementType.parseBooleanElementOrReference(elementName+".applyCondition", e)).result();
                UIDefinition.Instance condDefinition = new UIDefinition.Instance(elementName, a -> applyCondition.isEmpty() || applyCondition.get().value().apply(a));
                type.parse(condDefinition, accessorFunction, elementName, element);
                handleDeprecatedDefinitionsAddition(condDefinition);
                uiDefinition.addStatic(condDefinition);
            });
        }

        static ElementType registerConditional(String path, ElementType type) {
            return register(FactoryAPI.createVanillaLocation(path), createConditional(type));
        }

        static <T> ElementType registerCodec(String path, Codec<T> codec) {
            return registerConditional(path, (definition, accessorFunction, name, e) -> parseElement(definition, name, e, "value", (s, d) -> codec.parse(d).result().map(r -> UIDefinition.createBeforeInit(a -> a.putStaticElement(name, r))).orElse(null)));
        }

        static ElementType register(String path, ElementType type) {
            return register(FactoryAPI.createVanillaLocation(path), type);
        }

        static ElementType register(Identifier id, ElementType type) {
            map.put(id, type);
            return type;
        }
    }

    public final ListMap<Identifier,UIDefinition> map = new ListMap<>();
    public final List<UIDefinition> staticList = new ArrayList<>();

    public final void applyStatic(UIAccessor accessor) {
        accessor.getDefinitions().addAll(staticList);
    }

    public final void apply(UIAccessor accessor) {
        accessor.getDefinitions().addAll(map.values());
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        map.clear();
        resourceManager.listResources(UI_DEFINITIONS, r -> r.getPath().endsWith(".json")).forEach((l, r) -> {
            try (BufferedReader bufferedReader = r.openAsReader()) {
                Dynamic<JsonElement> dynamic = new Dynamic<>(JsonOps.INSTANCE, GsonHelper.parse(bufferedReader));
                if (map.containsKey(l) && !dynamic.get("replace").asBoolean(false)) {
                    map.get(l).getStaticDefinitions().addAll(fromDynamic(l.toString(), dynamic).getStaticDefinitions());
                } else map.put(l, fromDynamicWithTarget(l.toString(), dynamic));
            } catch (IOException exception) {
                FactoryAPI.LOGGER.warn(exception.getMessage());
            }
        });
    }

    public static Class<?> getClassFromString(String uiDefinitionName, String s) {
        try {
            return Class.forName(FactoryAPIPlatform.getCurrentClassName(s));
        } catch (ClassNotFoundException e) {
            FactoryAPI.LOGGER.warn("Incorrect Class Name {} from UI Definition {}: {}", s, uiDefinitionName, e.getMessage());
            return null;
        }
    }

    public static <T> UIDefinition.Instance fromDynamic(String name, Dynamic<T> dynamic, Predicate<UIAccessor> extraCondition) {
        Optional<ElementValue<Boolean>> applyConditionDefinition = dynamic.get("applyCondition").map(d -> ElementType.parseBooleanElementOrReference("applyCondition", d)).result();
        UIDefinition.Instance uiDefinition = new UIDefinition.Instance(name, accessor -> {
            if (!extraCondition.test(accessor)) return false;
            return applyConditionDefinition.isEmpty() || applyConditionDefinition.get().value().apply(accessor);
        });
        parseAllElements(uiDefinition,a -> a, dynamic, s -> s);
        return uiDefinition;
    }

    public static <T> UIDefinition.Instance fromDynamic(String name, Dynamic<T> dynamic) {
        return fromDynamic(name, dynamic, a -> true);
    }

    public static <T> UIDefinition.Instance fromDynamicWithTarget(String name, Dynamic<T> dynamic) {
        String targetType = dynamic.get("targetType").asString("id");

        Class<?> targetClass = dynamic.get("targetUI").asString().map(s -> targetType.equals("id") ? NAMED_UI_TARGETS.get(Identifier.tryParse(s)) : targetType.equals("class") ? getClassFromString(name, s) : null).result().orElse(null);
        Component targetTitle = targetType.equals("screenTitle") ? dynamic.get("targetUI").flatMap(DynamicUtil.getComponentCodec()::parse).result().orElse(null) : null;

        String targetRange = dynamic.get("targetRange").asString("instance");
        return fromDynamic(name, dynamic, accessor -> (accessor.toString().equals(name) || targetClass != null && (targetRange.equals("instance") && targetClass.isInstance(accessor) || targetRange.equals("class") && targetClass == accessor.getClass())) || targetTitle != null && accessor.getScreen() != null && accessor.getScreen().getTitle().equals(targetTitle));
    }

    public static void parseAllElements(UIDefinition uiDefinition, Function<UIAccessor, UIAccessor> accessorFunction, Dynamic<?> dynamic, Function<String, String> nameModifier) {
        dynamic.get("elements").asMapOpt(Dynamic::asString, d -> d).result().ifPresentOrElse(m -> m.forEach((s,d) -> tryParseElement(uiDefinition, accessorFunction, s, d, nameModifier)), () -> dynamic.get("elements").asStream().forEach(d -> tryParseElement(uiDefinition, accessorFunction, d.get("name").asString(), d, nameModifier)));
        handleDeprecatedDefinitionsAddition(uiDefinition);
    }

    public static void tryParseElement(UIDefinition uiDefinition, Function<UIAccessor, UIAccessor> accessorFunction, DataResult<String> name, Dynamic<?> dynamic, Function<String, String> nameModifier) {
        name.result().ifPresent(s -> dynamic.get("type").asString().map(ElementType::get).result().ifPresentOrElse(p -> p.parse(uiDefinition, accessorFunction, nameModifier.apply(s), dynamic), () -> ArbitrarySupplier.of(ElementType.parseNumber(nameModifier.apply(s), dynamic)).ifPresent(uiDefinition::addStatic)));
    }

    @Deprecated
    private static void handleDeprecatedDefinitionsAddition(UIDefinition uiDefinition) {
        if (uiDefinition.getDefinitions().isEmpty()) return;
        uiDefinition.getStaticDefinitions().addAll(uiDefinition.getDefinitions());
        uiDefinition.getDefinitions().clear();
    }

    public void openDefaultScreenAndAddDefinition(Optional<Identifier> defaultScreen, UIDefinition uiDefinition) {
        Screen s = defaultScreen.map(DEFAULT_SCREENS_MAP::get).orElse(parent-> new Screen(Component.empty()) {}).apply(Minecraft.getInstance().screen);
        UIAccessor.of(s).addStatic(uiDefinition);
        Minecraft.getInstance().setScreen(s);
    }
}
