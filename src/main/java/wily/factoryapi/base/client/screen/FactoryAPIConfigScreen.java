package wily.factoryapi.base.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
//? if <1.21.1 {
import net.minecraft.client.gui.screens.OptionsSubScreen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.GuiGraphics;
//?} else {
/*import net.minecraft.client.gui.screens.options.OptionsSubScreen;
*///?}
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import wily.factoryapi.base.client.FactoryOptions;
import wily.factoryapi.base.client.FactoryConfigWidgets;
import wily.factoryapi.base.config.FactoryCommonOptions;
import wily.factoryapi.base.config.FactoryConfig;

import java.util.ArrayList;
import java.util.List;

public class FactoryAPIConfigScreen extends OptionsSubScreen {
    public static final Component TITLE = Component.translatable("options.factory_api.title");
    protected final List<AbstractWidget> optionWidgets = new ArrayList<>();

    public static final List<FactoryConfig<?>> CONFIGS = new ArrayList<>(List.of(FactoryOptions.NEAREST_MIPMAP_SCALING, FactoryOptions.RANDOM_BLOCK_ROTATIONS, FactoryOptions.UI_DEFINITION_LOGGING, FactoryCommonOptions.EXPRESSION_FAIL_LOGGING));

    public FactoryAPIConfigScreen(Screen screen) {
        super(screen, Minecraft.getInstance().options, TITLE);

        for (FactoryConfig<?> value : CONFIGS) {
            optionWidgets.add(FactoryConfigWidgets.createWidget(value, 0, 0, 150, b->{}));
        }
    }

    //? if >=1.21.1 {
    /*@Override
    protected void addOptions() {
        list.addSmall(optionWidgets);
    }
    *///?} else {
    protected void init() {
        int i;
        for (i = 0; i < optionWidgets.size(); i++) {
            addRenderableWidget(optionWidgets.get(i)).setPosition(this.width / 2 - 155 + i % 2 * 160, this.height / 6 + 24 * (i >> 1));
        }

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (button) -> {
            this.minecraft.setScreen(this.lastScreen);
        }).bounds(this.width / 2 - 100, this.height / 6 + 24 * (i >> 1), 200, 20).build());
        super.init();
    }
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        //? if <=1.20.1
        /*renderBackground(guiGraphics);*/
        super.render(guiGraphics, i, j, f);
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 16777215);
    }
    //?}


    @Override
    public void removed() {
    }
}
