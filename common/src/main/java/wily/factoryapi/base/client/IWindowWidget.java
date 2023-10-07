package wily.factoryapi.base.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.Rect2i;
import wily.factoryapi.base.ArbitrarySupplier;
import wily.factoryapi.base.client.drawable.AbstractDrawableStatic;
import wily.factoryapi.base.client.drawable.DrawableStatic;
import wily.factoryapi.base.client.drawable.IFactoryDrawableType;
import wily.factoryapi.util.ScreenUtil;

import java.util.List;

public interface IWindowWidget extends GuiEventListener,Renderable {
    default boolean mouseClicked(double d, double e, int i) {
        for (Renderable nestedRenderable : getNestedRenderables())
            if (nestedRenderable instanceof GuiEventListener listener && listener.mouseClicked(d,e,i)) return true;
        return false;
    }

    default boolean mouseReleased(double d, double e, int i) {
        for (Renderable nestedRenderable : getNestedRenderables())
            if (nestedRenderable instanceof GuiEventListener listener && listener.mouseReleased(d,e,i)) return true;
        return false;
    }

    default boolean mouseDragged(double d, double e, int i, double f, double g) {
        for (Renderable nestedRenderable : getNestedRenderables())
            if (nestedRenderable instanceof GuiEventListener listener && listener.mouseDragged(d,e,i,f,g)) return true;
        return false;
    }

    default void render(PoseStack poseStack, int i, int j, float f) {
        List<? extends Renderable> renderables = getNestedRenderables();
        for (Renderable nestedRenderable : renderables)
            nestedRenderable.render(poseStack,i,j,f);
        renderables.stream().filter(r-> r instanceof AbstractDrawableStatic<?,?>).forEach(d-> ((AbstractDrawableStatic<?,?>) d).renderTooltip(poseStack,i,j,f));
    }

    default void playDownSound(float grave) {
        ScreenUtil.playButtonDownSound(grave);
    }
    Rect2i getBounds();

    boolean isVisible();

    @Override
    default ScreenRectangle getRectangle() {
        return ScreenUtil.rect2iToRectangle(getBounds());
    }

    <R extends Renderable> R addNestedRenderable(R drawable);

    List<? extends Renderable> getNestedRenderables();

    default ArbitrarySupplier<Renderable> getNestedAt(int x, int y){
        for (Renderable nested : getNestedRenderables()) {
            if (nested instanceof LayoutElement layout && IFactoryDrawableType.getMouseLimit(x,y, layout.getX(), layout.getY(), layout.getHeight(), layout.getWidth()))
                return ()->nested;
            else if (nested instanceof DrawableStatic drawable && drawable.inMouseLimit(x,y)) return ()->nested;
        }
        return ArbitrarySupplier.empty();
    }
}
