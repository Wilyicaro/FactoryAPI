package wily.factoryapi.base.client;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.gui.screens.RealmsBackupScreen;
import com.mojang.realmsclient.gui.screens.RealmsConfirmScreen;
import com.mojang.realmsclient.gui.screens.RealmsInviteScreen;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.JsonOps;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Checkbox;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.screens.*;
import net.minecraft.client.gui.screens.achievement.StatsScreen;
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen;
//? if <1.20.5 {
import net.minecraft.client.gui.screens.controls.ControlsScreen;
import net.minecraft.client.gui.screens.controls.KeyBindsScreen;
//?} else {
/*import net.minecraft.client.gui.screens.options.*;
import net.minecraft.client.gui.screens.options.controls.*;
*///?}
import net.minecraft.client.gui.screens.inventory.*;
import net.minecraft.client.gui.screens.multiplayer.JoinMultiplayerScreen;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.client.gui.screens.telemetry.TelemetryInfoScreen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.EditWorldScreen;
import net.minecraft.client.gui.screens.worldselection.SelectWorldScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
import net.minecraft.util.Unit;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.util.TriConsumer;
import org.jetbrains.annotations.Nullable;
import wily.factoryapi.FactoryAPI;
import wily.factoryapi.FactoryAPIClient;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.base.ArbitrarySupplier;
import wily.factoryapi.base.Bearer;
import wily.factoryapi.base.network.FactoryAPICommand;
import wily.factoryapi.util.DynamicUtil;
import wily.factoryapi.util.ListMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.function.*;
import java.util.stream.IntStream;

public interface UIDefinition extends Predicate<UIDefinition.Accessor> {
    String UI_DEFINITIONS = "ui_definitions";

    ListMap<ResourceLocation,Class<?>> NAMED_UI_DEFINITIONS = new ListMap.Builder<String,Class<?>>().put("accessibility_onboarding_screen", AccessibilityOnboardingScreen.class).put("title_screen",TitleScreen.class).put("options_screen", OptionsScreen.class).put("skin_customization_screen", SkinCustomizationScreen.class).put("video_settings_screen", VideoSettingsScreen.class).put("language_select_screen", LanguageSelectScreen.class).put("pack_selection_screen", PackSelectionScreen.class).put("telemetry_info_screen", TelemetryInfoScreen.class).put("online_options_screen", OnlineOptionsScreen.class).put("sound_options_screen", SoundOptionsScreen.class).put("controls_screen", ControlsScreen.class).put("mouse_settings_screen", MouseSettingsScreen.class).put("key_binds_screen", KeyBindsScreen.class).put("chat_options_screen", ChatOptionsScreen.class).put("accessibility_options_screen", AccessibilityOptionsScreen.class).put("credits_and_attribution_screen", CreditsAndAttributionScreen.class).put("win_screen", WinScreen.class).put("confirm_link_screen", ConfirmLinkScreen.class).put("select_world_screen", SelectWorldScreen.class).put("create_world_screen", CreateWorldScreen.class).put("edit_world_screen", EditWorldScreen.class).put("join_multiplayer_screen", JoinMultiplayerScreen.class).put("edit_server_screen", EditServerScreen.class).put("direct_join_server_screen", DirectJoinServerScreen.class).put("realms_main_screen", RealmsMainScreen.class).put("realms_screen", RealmsScreen.class).put("realms_confirm_screen", RealmsConfirmScreen.class).put("realms_backup_screen", RealmsBackupScreen.class).put("realms_invite_screen", RealmsInviteScreen.class).put("share_to_lan_screen", ShareToLanScreen.class).put("advancements_screen", AdvancementsScreen.class).put("stats_screen", StatsScreen.class).put("confirm_screen", ConfirmScreen.class).put("level_loading_screen", LevelLoadingScreen.class).put("progress_screen", ProgressScreen.class).put("generic_message_screen",/*? if <1.20.5 {*/GenericDirtMessageScreen/*?} else {*//*GenericMessageScreen*//*?}*/.class).put("receiving_level_screen", ReceivingLevelScreen.class).put("connect_screen", ConnectScreen.class).put("pause_screen", PauseScreen.class).put("inventory_screen", InventoryScreen.class).put("crafting_screen", CraftingScreen.class).put("container_screen", ContainerScreen.class).put("abstract_furnace_screen", AbstractFurnaceScreen.class).put("furnace_screen", FurnaceScreen.class).put("smoker_screen", SmokerScreen.class).put("blast_furnace_screen", BlastFurnaceScreen.class).put("loom_screen", LoomScreen.class).put("stonecutter_screen", StonecutterScreen.class).put("grindstone_screen", GrindstoneScreen.class).put("enchantment_screen", EnchantmentScreen.class).put("hopper_screen", HopperScreen.class).put("dispenser_screen", DispenserScreen.class).put("shulker_box_screen", ShulkerBoxScreen.class).put("anvil_screen", AnvilScreen.class).put("smithing_screen", SmithingScreen.class).put("brewing_stand_screen", BrewingStandScreen.class).put("beacon_screen", BeaconScreen.class).put("chat_screen", ChatScreen.class).put("in_bed_chat_screen", InBedChatScreen.class).put("gui",Gui.class).mapKeys(FactoryAPI::createVanillaLocation).build();

    static void registerNamedUIDefinition(ResourceLocation id, Class<?> uiClass){
        NAMED_UI_DEFINITIONS.put(id,uiClass);
    }
    static void registerNamedUIDefinition(String path, Class<?> uiClass){
        registerNamedUIDefinition(FactoryAPI.createVanillaLocation(path),uiClass);
    }

    UIDefinition EMPTY = new UIDefinition(){};

    LoadingCache<String,Double> EXPRESSION_CACHE = CacheBuilder.newBuilder().expireAfterWrite(Duration.ofMinutes(12)).build(CacheLoader.from(UIDefinition::evaluateExpression));
    LoadingCache<String,Boolean> BOOLEAN_EXPRESSION_CACHE = CacheBuilder.newBuilder().expireAfterWrite(Duration.ofMinutes(15)).build(CacheLoader.from(UIDefinition::evaluateBooleanExpression));

    default void beforeInit(Accessor accessor){
        getDefinitions().forEach(d->d.beforeInit(accessor));
    }
    default void afterInit(Accessor accessor){
        getDefinitions().forEach(d->d.afterInit(accessor));
    }

    default List<UIDefinition> getDefinitions(){
        return Collections.emptyList();
    }

    static UIDefinition createBeforeInit(Consumer<Accessor> beforeInit){
        return new UIDefinition() {
            @Override
            public void beforeInit(Accessor accessor) {
                UIDefinition.super.beforeInit(accessor);
                beforeInit.accept(accessor);
            }
        };
    }
    static UIDefinition createBeforeInit(String name, Consumer<Accessor> beforeInit){
        return createBeforeInit(a->{
            if (a.getBoolean(name+".applyCondition",true)) beforeInit.accept(a);
        });
    }
    static UIDefinition createAfterInit(Consumer<Accessor> afterInit){
        return new UIDefinition() {
            @Override
            public void afterInit(Accessor accessor) {
                UIDefinition.super.afterInit(accessor);
                afterInit.accept(accessor);
            }
        };
    }
    static UIDefinition createAfterInit(String name, Consumer<Accessor> afterInit){
        return createAfterInit(a->{
            if (a.getBoolean(name+".applyCondition",true)) afterInit.accept(a);
        });
    }

    @Override
    default boolean test(Accessor accessor){
        return true;
    }

    static List<Integer> parseIntRange(String s){
        if (!s.contains(",")) return Collections.singletonList(Integer.parseInt(s));
        String[] numbers = s.split(",");
        List<Integer> range = new ArrayList<>();
        for (int i = 0; i < numbers.length; i++) {
            boolean closed;
            if ((closed = numbers[i].startsWith("[")) || numbers[i].startsWith("]")){
                int start = Integer.parseInt(numbers[i].substring(1));
                i++;
                boolean endClosed;
                if (!(endClosed = numbers[i].endsWith("]")) && !numbers[i].endsWith("[")){
                    FactoryAPI.LOGGER.warn("Incorrect integer interval syntax at ordinal {}, skipping this. \nInteger Range: {}",i-1,s);
                }else {
                    int end = Integer.parseInt(numbers[i].replace(endClosed ? "]" : "[",""));
                    IntStream.rangeClosed(start + (closed ? 0: -Mth.sign(start)),end + (endClosed ? 0: -Mth.sign(end))).forEach(range::add);
                }
            } else range.add(Integer.parseInt(numbers[i]));
        }
        return range;
    }

    static double evaluateExpression(String expression) {
        char[] chars = expression.toCharArray();

        Stack<Double> values = new Stack<>();
        Stack<Character> operators = new Stack<>();

        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == ' ')
                continue;

            if (Character.isDigit(chars[i]) || chars[i] == '.') {
                StringBuilder sb = new StringBuilder();

                while (i < chars.length && (Character.isDigit(chars[i]) || chars[i] == '.')) {
                    sb.append(chars[i]);
                    i++;
                }
                values.push(Double.parseDouble(sb.toString()));
                i--;
            }
            if (chars[i] == '#') {
                StringBuilder sb = new StringBuilder();

                while (i < chars.length && (Character.isLetterOrDigit(chars[i]) || chars[i] == '#')) {
                    sb.append(chars[i]);
                    i++;
                }
                long colorValue = Long.parseLong(sb.substring(1),16);
                if (colorValue > Integer.MAX_VALUE) {
                    colorValue -= (1L << 32);
                }
                values.push((double) colorValue);
                i--;
            }
            else if (chars[i] == '(') {
                operators.push(chars[i]);
            }
            else if (chars[i] == ')') {
                while (operators.peek() != '(') {
                    values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
                }
                operators.pop();
            }
            else if ("+-*/%&|".contains(String.valueOf(chars[i]))) {

                while (!operators.isEmpty() && hasPrecedence(chars[i], operators.peek())) {
                    values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
                }

                operators.push(chars[i]);
            }
        }

        while (!operators.isEmpty()) {
            values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
        }

        return values.pop();
    }

    static boolean hasPrecedence(char operator1, char operator2) {
        if (operator2 == '(' || operator2 == ')')
            return false;
        return (operator1 != '*' && operator1 != '/') || (operator2 != '+' && operator2 != '-');
    }

    static double applyOperator(char operator, double b, double a) {
        return switch (operator) {
            case '+' -> a + b;
            case '-' -> a - b;
            case '*' -> a * b;
            case '/' -> {
                if (b == 0)
                    throw new ArithmeticException("Cannot divide by zero");
                yield a / b;
            }
            case '%' -> a % b;
            case '&' -> (int)a & (int)b;
            case '|' -> (int)a | (int)b;
            default -> 0;
        };
    }
    static boolean applyLogicalOperator(String operator, boolean b, boolean a) {
        return switch (operator) {
            case "&" -> a & b;
            case "|" -> a | b;
            case "&&" -> a && b;
            case "||" -> a || b;
            default -> false;
        };
    }
    static boolean applyEquality(String equality, double b, double a) {
        return switch (equality) {
            case "==" -> a == b;
            case "!=" -> a != b;
            case ">=" -> a >= b;
            case "<=" -> a <= b;
            case ">" -> a > b;
            case "<" -> a < b;
            default -> false;
        };
    }

    static boolean evaluateBooleanExpression(String expression) {
        char[] chars = expression.toCharArray();

        Stack<Boolean> values = new Stack<>();
        Stack<String> operators = new Stack<>();

        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == ' ')
                continue;

            if (Character.isDigit(chars[i]) || chars[i] == '.') {

                StringBuilder a = new StringBuilder();

                while (i < chars.length && (Character.isDigit(chars[i]) || chars[i] == '.')) {
                    a.append(chars[i]);
                    i++;
                }

                StringBuilder equality = new StringBuilder();
                while (i < chars.length && "!=><".contains(String.valueOf(chars[i])) && equality.length() <= 1) {
                    equality.append(chars[i]);
                    i++;
                }

                StringBuilder b = new StringBuilder();
                while (i < chars.length && (Character.isDigit(chars[i]) || chars[i] == '.')) {
                    b.append(chars[i]);
                    i++;
                }

                values.push(applyEquality(equality.toString(),Double.parseDouble(b.toString()),Double.parseDouble(a.toString())));
                i--;
            }
            else if ("!tf".contains(String.valueOf(chars[i]))){
                StringBuilder sb = new StringBuilder();
                boolean invert = chars[i] == '!';
                if (invert) i++;
                while (i < chars.length && ("true".contains(sb.toString()) || "false".contains(sb.toString()))) {
                    sb.append(chars[i]);
                    i++;
                }
                i--;
                if (sb.isEmpty() && invert) operators.push(String.valueOf(chars[i]));
                else values.push(invert != Boolean.parseBoolean(sb.toString()));
            }
            else if (chars[i] == '(') {
                operators.push(String.valueOf(chars[i]));
            }
            else if (chars[i] == ')') {
                while (!"(".equals(operators.peek())) {
                    values.push(applyLogicalOperator(operators.pop(), values.pop(), values.pop()));
                }
                operators.pop();
                if ("!".equals(operators.peek())) {
                    values.push(!values.pop());
                    operators.pop();
                }
            }
            else if ("&|".contains(String.valueOf(chars[i]))) {
                StringBuilder sb = new StringBuilder();
                while (i < chars.length && sb.length() <= 1 && "&|".contains(String.valueOf(chars[i]))) {
                    sb.append(chars[i]);
                    i++;
                }
                while (!operators.isEmpty() && !operators.peek().equals("(")) {
                    values.push(applyLogicalOperator(operators.pop(), values.pop(), values.pop()));
                }
                operators.push(sb.toString());
                i--;
            }
        }

        while (!operators.isEmpty()) {
            values.push(applyLogicalOperator(operators.pop(), values.pop(), values.pop()));
        }

        return values.pop();
    }


    interface Accessor extends UIDefinition {
        static Accessor of(Screen screen){
            return (Accessor) screen;
        }
        static Accessor of(Gui gui){
            return (Accessor) gui;
        }

        @Nullable
        Screen getScreen();

        void reloadUI();

        @Override
        default void beforeInit(Accessor accessor) {
            getElements().clear();
            putStaticElement("windowWidth", Minecraft.getInstance().getWindow().getWidth());
            putStaticElement("windowHeight", Minecraft.getInstance().getWindow().getHeight());
            putStaticElement("width", Minecraft.getInstance().getWindow().getGuiScaledWidth());
            putStaticElement("height", Minecraft.getInstance().getWindow().getGuiScaledHeight());
            getDefinitions().clear();
            FactoryAPIClient.uiDefinitionManager.list.stream().filter(d->d.test(this)).forEach(getDefinitions()::add);
            getStaticDefinitions().stream().filter(d->d.test(this)).forEach(getDefinitions()::add);
            UIDefinition.super.beforeInit(accessor);
        }

        List<UIDefinition> getStaticDefinitions();

        default void beforeInit(){
            beforeInit(this);
        }

        default void afterInit(){
            afterInit(this);
        }

        List<GuiEventListener> getChildren();

        List<Renderable> getRenderables();

        <T extends GuiEventListener> T removeChildren(T widget);

        <T extends GuiEventListener> T addChidren(T listener, boolean isRenderable, boolean isNarratable);

        default <T extends GuiEventListener> T addChidren(T listener){
            return addChidren(listener,true,true);
        }

        <T extends Renderable> T addRenderable(T renderable);

        Map<String, ArbitrarySupplier<?>> getElements();

        default <E> E putStaticElement(String name, E e){
            getElements().put(name, ArbitrarySupplier.of(e));
            return e;
        }

        default <E> E putBearer(String name, Bearer<E> e, Function<Object,E> convertOldValue){
            ArbitrarySupplier<?> oldElement = getElements().put(name, e);
            if (oldElement != null) oldElement.map(convertOldValue::apply).ifPresent(e::set);
            return e.get();
        }
        default <E> E putBearer(String name, Bearer<E> e){
            return putBearer(name,e,o-> (E)o);
        }
        default Integer putIntegerBearer(String name, Bearer<Integer> e){
            return putBearer(name,e,o-> o instanceof Number n ? Integer.valueOf(n.intValue()) : o instanceof String s ? Integer.parseInt(s) : null);
        }

        default <E extends AbstractWidget> E putWidget(String name, E e){
            putBearer(name+".message",Bearer.of(e::getMessage,e::setMessage));
            putBearer(name+".spriteOverlay",Bearer.of(WidgetAccessor.of(e)::getSpriteOverlay,WidgetAccessor.of(e)::setSpriteOverlay));
            putBearer(name+".highlightedSpriteOverlay",Bearer.of(WidgetAccessor.of(e)::getSpriteOverlay,WidgetAccessor.of(e)::setHighlightedSpriteOverlay));
            return putLayoutElement(name,e,e::setWidth, /*? if <=1.20.1 {*//*WidgetAccessor.of(e)*//*?} else {*/e/*?}*/::setHeight);
        }

        default Component putComponent(String name, Component component){
            putStaticElement(name, component);
            putStaticElement(name+".width", Minecraft.getInstance().font.width(component));
            return component;
        }

        default Renderable putTranslatableRenderable(String name, Renderable renderable){
            return (guiGraphics, i, j, f) -> {
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(getDouble(name+".translateX",0),getDouble(name+".translateY",0),getDouble(name+".translateZ",0));
                guiGraphics.pose().scale(getFloat(name+".scaleX",1),getFloat(name+".scaleY",1),getFloat(name+".scaleZ",1));
                renderable.render(guiGraphics,i,j,f);
                guiGraphics.pose().popPose();
            };
        }

        default <E extends LayoutElement> E putLayoutElement(String name, E e, Consumer<Integer> setWidth, Consumer<Integer> setHeight){
            putIntegerBearer(name+".x",Bearer.of(e::getX,e::setX));
            putIntegerBearer(name+".y",Bearer.of(e::getY,e::setY));
            putIntegerBearer(name+".width",Bearer.of(e::getWidth,setWidth));
            putIntegerBearer(name+".height",Bearer.of(e::getHeight,setHeight));
            return putStaticElement(name,e);
        }
        default String replaceValidElementValues(String s){
            for (Map.Entry<String, ArbitrarySupplier<?>> e : getElements().entrySet()) {
                String elementString = "${%s}".formatted(e.getKey());
                if (s.contains(elementString)) s = s.replace(elementString, String.valueOf(e.getValue().get()));
            }
            return s;
        }
        default <T> ArbitrarySupplier<Double> getNumberFromDynamic(Dynamic<T> data){
            return data.asString().result().map(s-> (ArbitrarySupplier<Double>)()->getExpressionResult(s)).orElse(data.asNumber().result().map(n-> ArbitrarySupplier.of(n.doubleValue())).orElse(ArbitrarySupplier.empty()));
        }
        default <T> ArbitrarySupplier<Boolean> getBooleanFromDynamic(Dynamic<T> data){
            return data.asString().result().map(s-> (ArbitrarySupplier<Boolean>) ()-> getBooleanExpressionResult(s)).orElse(data.getOps().getBooleanValue(data.getValue()).result().map(ArbitrarySupplier::of).orElse(ArbitrarySupplier.empty()));
        }
        default Double getExpressionResult(String s) {
            return getExpressionResult(s, EXPRESSION_CACHE::getUnchecked);
        }
        default <T> T getExpressionResult(String s, Function<String,T> expressionEvaluator) {
            String processed = replaceValidElementValues(s);
            try {
                return expressionEvaluator.apply(processed);
            } catch (Exception e) {
                FactoryAPI.LOGGER.warn("Incorrect expression syntax: {} \nUI Definition: {} \nExpression: {}  \nProcessed Expression: {}",e.getMessage(),toString(),s,processed);
                return null;
            }
        }
        default Boolean getBooleanExpressionResult(String s) {
            return getExpressionResult(s, BOOLEAN_EXPRESSION_CACHE::getUnchecked);
        }
        default <V> ArbitrarySupplier<V> getElement(String name, Class<V> valueClass){
            return getElements().getOrDefault(name,ArbitrarySupplier.empty()).secureCast(valueClass);
        }
        default ArbitrarySupplier<?> getElement(String name){
            return getElements().getOrDefault(name,ArbitrarySupplier.empty());
        }
        default ArbitrarySupplier<Boolean> getBooleanElement(String name){
            return getElement(name, Boolean.class);
        }
        default ArbitrarySupplier<Integer> getIntegerElement(String name){
            return getElement(name, Number.class).map(Number::intValue);
        }
        default <V> V getElementValue(String name, V defaultValue, Class<V> valueClass){
            return getElements().containsKey(name) && valueClass.isInstance(getElements().get(name).get()) ? valueClass.cast(getElements().get(name).get()) : defaultValue;
        }
        default int getInteger(String name, int defaultValue){
            return getElementValue(name,defaultValue,Number.class).intValue();
        }
        default double getDouble(String name, double defaultValue){
            return getElementValue(name,defaultValue,Number.class).doubleValue();
        }
        default float getFloat(String name, float defaultValue){
            return getElementValue(name,defaultValue,Number.class).floatValue();
        }
        default boolean getBoolean(String name, boolean defaultValue){
            return getElementValue(name,defaultValue,Boolean.class);
        }
        default boolean getBoolean(String name){
            return getBoolean(name,false);
        }
    }

    static Checkbox createCheckbox(boolean selected, BiConsumer<Checkbox,Boolean> onPress){
        //? if >1.20.1 {
        return Checkbox.builder(Component.empty(), Minecraft.getInstance().font).selected(selected).onValueChange(onPress::accept).build();
        //?} else {
        /*return new Checkbox(0,0,20,20,Component.empty(), selected){
            @Override
            public void onPress() {
                super.onPress();
                onPress.accept(this,selected());
            }
        };
        *///?}
    }

    class Manager implements ResourceManagerReloadListener {

        @Override
        public String getName() {
            return "factoryapi:ui_definition_manager";
        }


        public interface WidgetAction<P, W extends AbstractWidget> {
            ListMap<ResourceLocation,Supplier<Screen>> defaultScreensMap = new ListMap.Builder<String,Supplier<Screen>>().put("title",TitleScreen::new).put("options",()-> new OptionsScreen(Minecraft.getInstance().screen, Minecraft.getInstance().options)).put("language_select",()->new LanguageSelectScreen(Minecraft.getInstance().screen, Minecraft.getInstance().options,Minecraft.getInstance().getLanguageManager())).put("video_settings",()-> new VideoSettingsScreen(Minecraft.getInstance().screen,/*? if >=1.21 {*//*Minecraft.getInstance() ,*//*?}*/ Minecraft.getInstance().options)).put("skin_customization",()-> new SkinCustomizationScreen(Minecraft.getInstance().screen, Minecraft.getInstance().options)).mapKeys(FactoryAPI::createVanillaLocation).build();
            ListMap<ResourceLocation,WidgetAction<?,AbstractWidget>> map = new ListMap.Builder<String,WidgetAction<?,AbstractWidget>>().put("open_default_screen", create(ResourceLocation.CODEC,(s,a,w)->Minecraft.getInstance().setScreen(defaultScreensMap.getOrDefault(s,()->null).get()))).put("reload_ui",create(Codec.unit(Unit.INSTANCE), (s,a,w)->a.reloadUI())).put("run_command",createRunCommand(s->true)).put("run_windows_command",createRunCommand(s->Util.getPlatform() == Util.OS.WINDOWS)).put("run_linux_command",createRunCommand(s->Util.getPlatform() == Util.OS.LINUX)).put("run_osx_command",createRunCommand(s->Util.getPlatform() == Util.OS.OSX)).mapKeys(FactoryAPI::createVanillaLocation).build();
            Codec<WidgetAction<?,AbstractWidget>> CODEC = map.createCodec(ResourceLocation.CODEC);
            Codec<P> getCodec();

            void press(P result, Accessor accessor, W widget);
            default Optional<BiConsumer<Accessor,W>> pressSupplier(Predicate<Accessor> canApply, Dynamic<?> dynamic){
                return getCodec().parse(dynamic).result().or(()->dynamic.get("value").get().result().flatMap(d->getCodec().parse(d).result())).map(p->(a,w)-> {
                    if (canApply.test(a)) press(p, a, w);
                });
            }
            static WidgetAction<String,AbstractWidget> createRunCommand(Predicate<String> shouldRun){
                return create(Codec.STRING,(s,d,w)->{
                    if (shouldRun.test(s)) {
                        try {
                            new ProcessBuilder(s.split(" ")).start();
                        } catch (IOException e) {
                            FactoryAPI.LOGGER.warn(e.getMessage());
                        }
                    }
                });
            }
            static <P, W extends AbstractWidget> WidgetAction<P,W> create(Codec<P> codec, TriConsumer<P,Accessor,W> onPress){
                 return new WidgetAction<>() {
                     @Override
                     public Codec<P> getCodec() {
                         return codec;
                     }

                     @Override
                     public void press(P result, Accessor accessor, W widget) {
                         onPress.accept(result, accessor, widget);
                     }
                 };
            }
        }

        public interface ElementType {
            ListMap<ResourceLocation,ElementType> map = new ListMap<>();

            ElementType ADD_BUTTON = registerConditional("add_button",(definition, name, e)-> {
                List<BiConsumer<Accessor,AbstractWidget>> actions = parseActionsElement(definition, name, e);
                parseWidgetElements(definition,name,e);
                definition.getDefinitions().add(createAfterInit(name,a -> a.putWidget(name, a.addChidren(Button.builder(Component.empty(), b -> actions.forEach(c->c.accept(a,b))).build()))));
            });
            ElementType ADD_CHECKBOX = registerConditional("add_checkbox",(definition, name, e)-> {
                List<BiConsumer<Accessor,AbstractWidget>> actions = parseActionsElement(definition, name, e);
                parseWidgetElements(definition,name,e);
                Bearer<Boolean> selected = Bearer.of(null);
                parseElement(definition,name,e,"selected",(s,d)->createBeforeInit(name,a->{
                    if (selected.isEmpty()) selected.set(a.getBooleanFromDynamic(d).get());
                    a.getElements().put(s,selected);
                }));
                definition.getDefinitions().add(createAfterInit(name,a-> a.putWidget(name, a.addChidren(createCheckbox(a.getBoolean(name+".selected"),(c,b)-> {
                    selected.set(b);
                    actions.forEach(c1->c1.accept(a,c));
                })))));
            });
            ElementType MODIFY_WIDGET = registerConditional("modify_widget",createIndexable(i->(definition,name,e)-> {
                i.forEach(index-> parseWidgetElements(definition,name + (i.size() == 1 ? "" : "_" + index),e));
                definition.getDefinitions().add(createAfterInit(a-> {
                    Bearer<Integer> count = Bearer.of(0);
                    a.getElements().put(name+".index",count);
                    for (Integer index : i)
                        if (a.getChildren().size() > index && a.getChildren().get(index) instanceof AbstractWidget w){
                            a.putWidget(name + (i.size() == 1 ? "" : "_" + index),w);
                            count.set(count.get()+1);
                        }
                }));
            }));
            ElementType REMOVE_CHILDREN = registerConditional("remove_children",createIndexable(i->(definition,name,e)-> definition.getDefinitions().add(createAfterInit(name,a -> {
                for (Integer index : i)
                    if (a.getChildren().size() > index)
                        a.removeChildren(a.getChildren().get(index));
            }))));
            ElementType PUT_INTEGER = registerConditional("put_integer",(definition, name, e)-> e.asMapOpt().result().ifPresent(m-> m.forEach(o->o.getFirst().asString().result().ifPresent(s-> definition.getDefinitions().add(createBeforeInit(name,a-> a.getElements().put(name+"."+s,a.getNumberFromDynamic(e))))))));
            ElementType PUT_COMPONENT = registerConditional("put_component",(definition, name, e)->parseElement(definition,name,e,"component", ElementType::parseComponentElement));
            ElementType PUT_STRING = registerConditional("put_string",(definition, name, e)-> e.get("string").asString().result().ifPresent(s->definition.getDefinitions().add(createBeforeInit(name,a-> a.putStaticElement(name,s)))));
            ElementType PUT_BOOLEAN = registerConditional("put_boolean",(definition, name, e)-> e.get("boolean").result().ifPresent(d->definition.getDefinitions().add(createBeforeInit(name,a-> a.getElements().put(name,a.getBooleanFromDynamic(d))))));
            ElementType BLIT = registerConditional("blit", ElementType::parseBlitElements);
            ElementType BLIT_SPRITE = registerConditional("blit_sprite", ElementType::parseBlitSpriteElements);
            ElementType FILL = registerConditional("fill", ElementType::parseFillElements);
            ElementType FILL_GRADIENT = registerConditional("fill_gradient", ElementType::parseFillGradientElements);
            ElementType DRAW_STRING = registerConditional("draw_string", ElementType::parseDrawStringElements);
            ElementType RENDER_ITEM = registerConditional("render_item", ElementType::parseRenderItemElements);

            static void parseWidgetElements(UIDefinition uiDefinition, String elementName, Dynamic<?> element){
                parseElements(uiDefinition,elementName,element, (s,d)->createBeforeInit(elementName,a-> a.getElements().put(s,a.getNumberFromDynamic(d))),"x","y","width","height");
                parseElement(uiDefinition,elementName,element,"message", ElementType::parseComponentElement);
                parseElement(uiDefinition,elementName,element,"spriteOverlay",ResourceLocation.CODEC);
                parseElement(uiDefinition,elementName,element,"highlightedSpriteOverlay",ResourceLocation.CODEC);
            }
            static void parseFillElements(UIDefinition uiDefinition, String elementName, Dynamic<?> element){
                uiDefinition.getDefinitions().add(createAfterInit(elementName,a-> a.addRenderable((a.putTranslatableRenderable(elementName, (guiGraphics, i, j, f) -> guiGraphics.fill(a.getInteger(elementName+".x",0),a.getInteger(elementName+".y",0),a.getInteger(elementName+".x",0) + a.getInteger(elementName+".width",0), a.getInteger(elementName+".y",0) + a.getInteger(elementName+".height",0),a.getInteger(elementName+".color",0xFFFFFFFF)))))));
                parseElements(uiDefinition,elementName,element, (s,d)->createBeforeInit(elementName,a-> a.getElements().put(s,a.getNumberFromDynamic(d))),"x","y","width","height","translateX","translateY","translateZ","scaleX","scaleY","scaleZ","color");
            }
            static void parseFillGradientElements(UIDefinition uiDefinition, String elementName, Dynamic<?> element){
                uiDefinition.getDefinitions().add(createAfterInit(elementName,a-> a.addRenderable((a.putTranslatableRenderable(elementName, (guiGraphics, i, j, f) -> guiGraphics.fillGradient(a.getInteger(elementName+".x",0),a.getInteger(elementName+".y",0),a.getInteger(elementName+".x",0) + a.getInteger(elementName+".width",0), a.getInteger(elementName+".y",0) + a.getInteger(elementName+".height",0),a.getInteger(elementName+".color",0xFFFFFFFF),a.getInteger(elementName+".secondColor",0xFFFFFFFF)))))));
                parseElements(uiDefinition,elementName,element, (s,d)->createBeforeInit(elementName,a-> a.getElements().put(s,a.getNumberFromDynamic(d))),"x","y","width","height","translateX","translateY","translateZ","scaleX","scaleY","scaleZ","color","secondColor");
            }
            static void parseBlitElements(UIDefinition uiDefinition, String elementName, Dynamic<?> element){
                uiDefinition.getDefinitions().add(createAfterInit(elementName,a-> a.addRenderable((a.putTranslatableRenderable(elementName, (guiGraphics, i, j, f) -> a.getElement(elementName+".texture",ResourceLocation.class).ifPresent(t-> FactoryGuiGraphics.of(guiGraphics).blit(t,a.getInteger(elementName+".x",0),a.getInteger(elementName+".y",0),a.getInteger(elementName+".uvX",0),a.getInteger(elementName+".uvY",0),a.getInteger(elementName+".width",0),a.getInteger(elementName+".height",0),a.getInteger(elementName+".imageWidth",256),a.getInteger(elementName+".imageHeight",256))))))));
                parseElement(uiDefinition,elementName,element,"texture",ResourceLocation.CODEC);
                parseElements(uiDefinition,elementName,element, (s,d)->createBeforeInit(elementName,a-> a.getElements().put(s,a.getNumberFromDynamic(d))),"x","y","uvX","uvY","width","height","imageWidth","imageHeight","translateX","translateY","translateZ","scaleX","scaleY","scaleZ");
            }
            static void parseBlitSpriteElements(UIDefinition uiDefinition, String elementName, Dynamic<?> element){
                uiDefinition.getDefinitions().add(createAfterInit(elementName,a-> a.addRenderable((a.putTranslatableRenderable(elementName,(guiGraphics, i, j, f) -> a.getElement(elementName+".sprite",ResourceLocation.class).ifPresent(t-> FactoryGuiGraphics.of(guiGraphics).blitSprite(t,a.getInteger(elementName+".x",0),a.getInteger(elementName+".y",0),a.getInteger(elementName+".width",0),a.getInteger(elementName+".height",0))))))));
                parseElement(uiDefinition,elementName,element,"sprite",ResourceLocation.CODEC);
                parseElements(uiDefinition,elementName,element, (s,d)->createBeforeInit(elementName,a-> a.getElements().put(s,a.getNumberFromDynamic(d))),"x","y","width","height","translateX","translateY","translateZ","scaleX","scaleY","scaleZ");
            }
            static void parseDrawStringElements(UIDefinition uiDefinition, String elementName, Dynamic<?> element){
                uiDefinition.getDefinitions().add(createAfterInit(elementName,a-> a.addRenderable((a.putTranslatableRenderable(elementName,(guiGraphics, i, j, f) -> a.getElement(elementName+".component",Component.class).ifPresent(c-> guiGraphics.drawString(Minecraft.getInstance().font, c,a.getInteger(elementName+".x",0),a.getInteger(elementName+".y",0),a.getInteger(elementName+".color",0xFFFFFF),a.getBoolean(elementName+".shadow",true))))))));
                parseElement(uiDefinition,elementName,element,"component", ElementType::parseComponentElement);
                parseElements(uiDefinition,elementName,element, (s,d)->createBeforeInit(elementName,a-> a.getElements().put(s,a.getNumberFromDynamic(d))),"x","y","translateX","translateY","translateZ","scaleX","scaleY","scaleZ","color");
                parseElement(uiDefinition,elementName,element, "shadow",(s,d)->createBeforeInit(elementName,a-> a.getElements().put(s,a.getNumberFromDynamic(d))));
            }
            static void parseRenderItemElements(UIDefinition uiDefinition, String elementName, Dynamic<?> element){
                uiDefinition.getDefinitions().add(createAfterInit(elementName,a-> a.addRenderable((a.putTranslatableRenderable(elementName,(guiGraphics, i, j, f) -> a.getElement(elementName+".item", ItemStack.class).ifPresent(s-> guiGraphics.renderItem(s,a.getInteger(elementName+".x",0),a.getInteger(elementName+".y",0))))))));
                parseElement(uiDefinition,elementName,element,"item",(s,d)-> createBeforeInit(elementName,a-> a.getElements().put(s,DynamicUtil.getItemFromDynamic(d,true))));
                parseElements(uiDefinition,elementName,element, (s,d)->createBeforeInit(elementName,a-> a.getElements().put(s,a.getNumberFromDynamic(d))),"x","y","translateX","translateY","translateZ","scaleX","scaleY","scaleZ");
            }
            static List<BiConsumer<Accessor,AbstractWidget>> parseActionsElement(UIDefinition uiDefinition, String elementName, Dynamic<?> element){
                return element.get("actions").asMapOpt().result().map(m->m.map(p-> {
                    String actionName = elementName+".actions."+p.getFirst().asString("");
                    parseElement(uiDefinition,actionName,p.getSecond(),"applyCondition",(s,d)->createBeforeInit(a-> a.getElements().put(s,a.getBooleanFromDynamic(d))));
                    return WidgetAction.CODEC.parse(p.getFirst()).result().flatMap(c-> c.pressSupplier(a->a.getBoolean(actionName+".applyCondition",true),p.getSecond()));
                }).filter(Optional::isPresent).map(Optional::get).toList()).orElse(Collections.emptyList());
            }
            static UIDefinition parseComponentElement(String name, Dynamic<?> element){
                return DynamicUtil.getComponentCodec().parse(element).result().map(c-> createBeforeInit(name,a-> a.putComponent(name,c))).orElse(null);
            }
            static void parseElements(UIDefinition uiDefinition, String elementName, Dynamic<?> element, BiFunction<String,Dynamic<?>,UIDefinition> dynamicToDefinition, String... fields){
                for (String field : fields) {
                    parseElement(uiDefinition,elementName,element,field,dynamicToDefinition);
                }
            }
            static void parseElement(UIDefinition uiDefinition, String elementName, Dynamic<?> element, String field, Codec<?> codec){
                parseElement(uiDefinition,elementName,element,field,(s,d)->codec.parse(d).result().map(c-> createBeforeInit(elementName,a-> a.putStaticElement(s,c))).orElse(null));
            }
            static void parseElement(UIDefinition uiDefinition, String elementName, Dynamic<?> element, String field, BiFunction<String,Dynamic<?>,UIDefinition> dynamicToDefinition){
                element.get(field).result().map(d-> dynamicToDefinition.apply(elementName + "." + field, d)).ifPresent(d->uiDefinition.getDefinitions().add(d));
            }
            static ElementType get(String id){
                return get(ResourceLocation.tryParse(id));
            }
            static ElementType get(ResourceLocation id){
                return map.getOrDefault(id,PUT_INTEGER);
            }
            static ResourceLocation getId(ElementType type){
                return map.getKeyOrDefault(type,null);
            }

            void parse(UIDefinition uiDefinition, String elementName, Dynamic<?> element);

            static ElementType createIndexable(Function<List<Integer>, ElementType> parser){
                return ((uiDefinition, elementName, element)-> parser.apply(element.get("range").asString().result().map(UIDefinition::parseIntRange).orElse(List.of(element.get("index").asInt(0)))).parse(uiDefinition,elementName,element));
            }
            static ElementType createConditional(ElementType type){
                return ((uiDefinition, elementName, element) -> {
                    parseElement(uiDefinition,elementName,element,"applyCondition",(s,d)->createBeforeInit(a-> a.getElements().put(s,a.getBooleanFromDynamic(d))));
                    type.parse(uiDefinition,elementName,element);
                });
            }
            static ElementType registerConditional(String path, ElementType type){
                return register(FactoryAPI.createVanillaLocation(path),createConditional(type));
            }

            static ElementType register(String path, ElementType type){
                return register(FactoryAPI.createVanillaLocation(path),type);
            }

            static ElementType register(ResourceLocation id, ElementType type){
                map.put(id,type);
                return type;
            }

        }

        public final List<UIDefinition> list = new ArrayList<>();

        @Override
        public void onResourceManagerReload(ResourceManager resourceManager) {
            list.clear();
            resourceManager.listResources(UI_DEFINITIONS, r-> r.getPath().endsWith(".json")).forEach((l, r)->{
                try (BufferedReader bufferedReader = r.openAsReader()) {
                    list.add(fromDynamic(l.toString(), new Dynamic<>(JsonOps.INSTANCE,GsonHelper.parse(bufferedReader))));
                } catch (IOException exception) {
                    FactoryAPI.LOGGER.warn(exception.getMessage());
                }
            });
        }
        public static Class<?> getClassFromString(String uiDefinitionName, String s){
            try {
                return Class.forName(FactoryAPIPlatform.getCurrentClassName(s));
            } catch (ClassNotFoundException e) {
                FactoryAPI.LOGGER.warn("Incorrect Class Name {} from UI Definition {}: {}",s,uiDefinitionName,e.getMessage());
                return null;
            }
        }
        public static <T> UIDefinition fromDynamic(String name, Dynamic<T> dynamic){
            String targetMod = dynamic.get("targetMod").asString(null);
            if (targetMod != null && !FactoryAPIPlatform.isModLoaded(targetMod)) return EMPTY;
            String targetType = dynamic.get("targetType").asString("id");

            Class<?> targetClass = dynamic.get("targetUI").asString().map(s->targetType.equals("id") ? NAMED_UI_DEFINITIONS.get(ResourceLocation.tryParse(s)) : getClassFromString(name,s)).result().orElse(null);
            Component targetTitle = targetType.equals("screenTitle") ? dynamic.get("targetUI").flatMap(DynamicUtil.getComponentCodec()::parse).result().orElse(null) : null;

            String targetRange = dynamic.get("targetRange").asString("instance");

            Function<Accessor, Boolean> applyCondition = dynamic.get("applyCondition").map(d-> (Function<Accessor,Boolean>)(a-> a.getBooleanFromDynamic(d).or(true))).result().orElse(a->true);
            UIDefinition uiDefinition = new UIDefinition() {
                final List<UIDefinition> definitions = new ArrayList<>();

                @Override
                public List<UIDefinition> getDefinitions() {
                    return definitions;
                }

                @Override
                public boolean test(Accessor accessor) {
                    return applyCondition.apply(accessor) && (accessor.toString().equals(name) || targetClass != null && (targetRange.equals("instance") && targetClass.isInstance(accessor) || targetRange.equals("class") && targetClass == accessor.getClass())) || targetTitle != null && accessor.getScreen() != null && accessor.getScreen().getTitle().equals(targetTitle);
                }

                @Override
                public String toString() {
                    return name;
                }
            };
            dynamic.get("elements").asMap(Dynamic::asString, d->d).forEach((ds,e)-> ds.result().ifPresent(s-> e.get("type").asString().map(ElementType::get).result().ifPresentOrElse(p -> p.parse(uiDefinition,s,e),()->uiDefinition.getDefinitions().add(createBeforeInit(a->a.getElements().put(s,a.getNumberFromDynamic(e)))))));
            return uiDefinition;
        }
        public void openScreenAndAddDefinition(UIDefinition uiDefinition){
            Screen s = new Screen(Component.empty()) {
                @Override
                public Component getTitle() {
                    return super.getTitle();
                }

                @Override
                public boolean isPauseScreen() {
                    return Accessor.of(this).getBoolean("isPauseScreen",false);
                }

                @Override
                public String toString() {
                    return FactoryAPICommand.UIDefinitionPayload.ID.toString();
                }
            };
            Accessor.of(s).getStaticDefinitions().add(uiDefinition);
            Minecraft.getInstance().setScreen(s);
        }
    }
}