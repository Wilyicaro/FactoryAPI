package wily.factoryapi.base.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface IWindowWidget {

    default List<FactoryDrawableButton> configButtons() {
        List<FactoryDrawableButton> list = new ArrayList<>();
        addButtons(list);
        list.addAll(configSliders());
        return list;
    }
    default List<FactoryDrawableButton> addButtons(List<FactoryDrawableButton> list) {
        return list;
    }

    default List<FactoryDrawableSlider> configSliders() {
        return Collections.emptyList();
    }
    default void renderButtons(GuiGraphics graphics, int i, int j) {
        for (FactoryDrawableButton button : configButtons()) {
            button.selected = button.inMouseLimit(i, j);
            button.draw(graphics);
        }

    }
    default void renderButtonsTooltip(Font font, GuiGraphics graphics, int i, int j){
        for (FactoryDrawableButton button : configButtons()) {
            if (button.inMouseLimit(i, j) && !button.tooltip.getString().isEmpty()) graphics.renderTooltip(font,button.tooltip, i,j);
        }
    }
    default boolean mouseDraggedSliders(double d, double e, int i){
        for (FactoryDrawableSlider slider : configSliders()) {
            if (slider.inMouseLimit(d, e) || slider.dragging) {
                return slider.mouseDragging(d,e,i);
            }
        }
        return false;
    }
    default boolean mouseReleasedSliders(double d, double e, int i){
        for (FactoryDrawableSlider slider : configSliders()) {
            if (slider.inMouseLimit(d, e) || slider.dragging) {
                slider.onRelease(d,e,i);
                return true;
            }
        }
        return false;
    }
    default boolean mouseClickedButtons(double d, double e, int i) {
        for (FactoryDrawableButton button : configButtons()) {
            if (button.inMouseLimit(d, e)) {
                playDownSound(1.5F);
                button.onClick(d,e,i);
                return true;
            }
        }
        return  false;
    }
    default void playDownSound(float grave) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, grave));
    }
    Rect2i getBounds();

    boolean isVisible();
}