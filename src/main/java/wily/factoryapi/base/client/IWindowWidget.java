package wily.factoryapi.base.client;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.navigation.ScreenRectangle;
//? if >=1.21.9 {
import net.minecraft.client.input.MouseButtonEvent;
//?}
import net.minecraft.client.renderer.Rect2i;
import wily.factoryapi.base.ArbitrarySupplier;
import wily.factoryapi.base.client.drawable.DrawableStatic;
import wily.factoryapi.util.FactoryScreenUtil;

import java.util.List;

public interface IWindowWidget extends GuiEventListener, Renderable {

    //? if >=1.21.9 {
    @Override
    default boolean mouseClicked(MouseButtonEvent mouseButtonEvent, boolean bl) {
        for (Renderable nestedRenderable : getNestedRenderables())
            if (nestedRenderable instanceof GuiEventListener listener && listener.mouseClicked(mouseButtonEvent, bl)) return true;
        return false;
    }
    @Override
    default boolean mouseReleased(MouseButtonEvent mouseButtonEvent) {
        for (Renderable nestedRenderable : getNestedRenderables())
            if (nestedRenderable instanceof GuiEventListener listener && listener.mouseReleased(mouseButtonEvent)) return true;
        return false;
    }
    @Override
    default boolean mouseDragged(MouseButtonEvent mouseButtonEvent, double f, double g) {
        for (Renderable nestedRenderable : getNestedRenderables())
            if (nestedRenderable instanceof GuiEventListener listener && listener.mouseDragged(mouseButtonEvent, f, g)) return true;
        return false;
    }
    //?} else {
    /*@Override
    default boolean mouseClicked(double d, double e, int i) {
        for (Renderable nestedRenderable : getNestedRenderables())
            if (nestedRenderable instanceof GuiEventListener listener && listener.mouseClicked(d,e,i)) return true;
        return false;
    }
    @Override
    default boolean mouseReleased(double d, double e, int i) {
        for (Renderable nestedRenderable : getNestedRenderables())
            if (nestedRenderable instanceof GuiEventListener listener && listener.mouseReleased(d,e,i)) return true;
        return false;
    }
    @Override
    default boolean mouseDragged(double d, double e, int i, double f, double g) {
        for (Renderable nestedRenderable : getNestedRenderables())
            if (nestedRenderable instanceof GuiEventListener listener && listener.mouseDragged(d,e,i,f,g)) return true;
        return false;
    }
    *///?}

    default void render(GuiGraphics guiGraphics, int i, int j, float f) {
        for (Renderable nestedRenderable : getNestedRenderables()) {
            nestedRenderable.render(guiGraphics,i,j,f);
        }
    }

    default void playDownSound(float grave) {
        FactoryScreenUtil.playButtonDownSound(grave);
    }
    Rect2i getBounds();

    boolean isVisible();

    @Override
    default ScreenRectangle getRectangle() {
        return FactoryScreenUtil.rect2iToRectangle(getBounds());
    }

    <R extends Renderable> R addNestedRenderable(R drawable);

    List<? extends Renderable> getNestedRenderables();

    default ArbitrarySupplier<Renderable> getNestedAt(int x, int y){
        for (Renderable nested : getNestedRenderables()) {
            if (nested instanceof LayoutElement layout && FactoryScreenUtil.isMouseOver(x,y, layout.getX(), layout.getY(), layout.getHeight(), layout.getWidth()))
                return ()->nested;
            else if (nested instanceof DrawableStatic drawable && drawable.inMouseLimit(x,y)) return ()->nested;
        }
        return ArbitrarySupplier.empty();
    }
}
