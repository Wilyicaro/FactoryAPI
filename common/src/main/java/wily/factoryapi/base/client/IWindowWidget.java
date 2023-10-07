package wily.factoryapi.base.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.renderer.Rect2i;
import wily.factoryapi.base.ArbitrarySupplier;
import wily.factoryapi.base.client.drawable.AbstractDrawableStatic;
import wily.factoryapi.util.ScreenUtil;

import java.util.List;

public interface IWindowWidget extends GuiEventListener, Widget {
    default boolean mouseClicked(double d, double e, int i) {
        for (Widget nestedWidgets : getNestedWidgets())
            if (nestedWidgets instanceof GuiEventListener && ((GuiEventListener) nestedWidgets).mouseClicked(d,e,i)) {return true;}
        return false;
    }

    default boolean mouseReleased(double d, double e, int i) {
        for (Widget nestedWidget : getNestedWidgets())
            if (nestedWidget instanceof GuiEventListener && ((GuiEventListener) nestedWidget).mouseReleased(d,e,i)) {
                return true;
            }
        return false;
    }

    default boolean mouseDragged(double d, double e, int i, double f, double g) {
        for (Widget nestedWidget : getNestedWidgets())
            if (nestedWidget instanceof GuiEventListener && ((GuiEventListener) nestedWidget).mouseDragged(d,e,i,f,g)) {
                return true;
            }
        return false;
    }

    default void render(PoseStack poseStack, int i, int j, float f) {
        List<? extends Widget> widgets = getNestedWidgets();
        for (Widget nestedWidget : widgets) {
            nestedWidget.render(poseStack,i,j,f);
        }
        widgets.stream().filter(w-> w instanceof AbstractDrawableStatic<?,?>).forEach(w-> ((AbstractDrawableStatic<?,?>)w).renderTooltip(poseStack,i,j,f));
    }

    default void playDownSound(float grave) {
        ScreenUtil.playButtonDownSound(grave);
    }
    Rect2i getBounds();

    boolean isVisible();

    <R extends Widget> R addNestedWidget(R drawable);

    List<? extends Widget> getNestedWidgets();

    default ArbitrarySupplier<Widget> getNestedAt(int x, int y){
        for (Widget nested : getNestedWidgets()) {
            if (nested instanceof GuiEventListener && ((GuiEventListener) nested).isMouseOver(x,y))
                return ()->nested;
        }
        return ArbitrarySupplier.empty();
    }
}
