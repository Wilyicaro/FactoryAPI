package wily.factoryapi.base.client.screen;

import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import wily.factoryapi.base.client.FactoryOptions;
import wily.factoryapi.base.client.FactoryConfigWidgets;
import wily.factoryapi.base.config.FactoryCommonOptions;
import wily.factoryapi.base.config.FactoryConfig;

import java.util.ArrayList;
import java.util.List;

public class FactoryConfigScreen extends Screen {
    public static final Component TITLE = Component.translatable("options.factory_api.title");
    protected final List<AbstractWidget> optionWidgets = new ArrayList<>();

    public static final List<FactoryConfig<?>> CONFIGS = new ArrayList<>(List.of(FactoryOptions.NEAREST_MIPMAP_SCALING, FactoryOptions.RANDOM_BLOCK_ROTATIONS, FactoryOptions.UI_DEFINITION_LOGGING, FactoryCommonOptions.EXPRESSION_FAIL_LOGGING));
    public final Screen parent;
    private ConfigList list;

    public FactoryConfigScreen(Screen screen, List<FactoryConfig<?>> configs, Component title) {
        super(title);
        parent = screen;
        for (FactoryConfig<?>  config : configs) {
            if (config.getStorageAccess().allowSync() && !config.getStorageAccess().allowClientSync(Minecraft.getInstance().player)) continue;
            AbstractWidget widget = FactoryConfigWidgets.createWidget( config, 0, 0, 150, b-> config.sync());
            if (widget != null) optionWidgets.add(widget);
        }
    }

    public static FactoryConfigScreen createFactoryAPIConfigScreen(Screen parent){
        return new FactoryConfigScreen(parent, CONFIGS, TITLE);
    }

    protected void init() {
        this.list = this.addRenderableWidget(new ConfigList(this.minecraft, this.width, this.height - 64, 32, 25, this));
        list.addSmall(optionWidgets);
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (button) -> onClose()).bounds(this.width / 2 - 100, this.height - 27, 200, 20).build());
        super.init();
    }

    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        //? if <=1.20.1
        /*renderBackground(guiGraphics);*/
        super.render(guiGraphics, i, j, f);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 16777215);
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }

    public static class ConfigList extends ContainerObjectSelectionList<ConfigList.Entry>{
        private final Screen screen;

        public ConfigList(Minecraft minecraft, int screenWidth, int listHeight, int k, int l, Screen screen) {
            super(minecraft, screenWidth,  /*? if <=1.20.2 {*//*listHeight + k * 2, k, listHeight + k, l*//*?} else {*/listHeight, k, l/*?}*/);
            this.screen = screen;
        }

        public void addSmall(List<AbstractWidget> list) {
            for (int i = 0; i < list.size(); i += 2) {
                this.addSmall(list.get(i), i < list.size() - 1 ? list.get(i + 1) : null);
            }
        }

        public void addSmall(AbstractWidget arg, @Nullable AbstractWidget arg2) {
            this.addEntry(Entry.small(arg, arg2, this.screen));
        }

        @Override
        public int getRowWidth() {
            return 310;
        }

        public static class Entry extends ContainerObjectSelectionList.Entry<Entry> {
            private final List<AbstractWidget> children;
            private final Screen screen;

            Entry(List<AbstractWidget> list, Screen arg) {
                this.children = ImmutableList.copyOf(list);
                this.screen = arg;
            }

            public static Entry big(List<AbstractWidget> list, Screen arg) {
                return new Entry(list, arg);
            }

            public static Entry small(AbstractWidget arg, @Nullable AbstractWidget arg2, Screen arg3) {
                return arg2 == null ? new Entry(ImmutableList.of(arg), arg3) : new Entry(ImmutableList.of(arg, arg2), arg3);
            }

            @Override
            public void render(GuiGraphics arg, int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
                int p = 0;
                int q = this.screen.width / 2 - 155;

                for (AbstractWidget abstractWidget : this.children) {
                    abstractWidget.setPosition(q + p, j);
                    abstractWidget.render(arg, n, o, f);
                    p += 160;
                }
            }

            @Override
            public List<? extends GuiEventListener> children() {
                return this.children;
            }

            @Override
            public List<? extends NarratableEntry> narratables() {
                return this.children;
            }
        }
    }
}
