package wily.factoryapi.base.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import wily.factoryapi.base.client.drawable.AbstractDrawableButton;
import wily.factoryapi.base.client.drawable.DrawableStatic;
import wily.factoryapi.util.FactoryScreenUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public abstract class FactoryScreenWindow<T extends AbstractContainerScreen<?>> extends AbstractWidget implements IWindowWidget {
    public final AbstractDrawableButton<?> config;

    private double actualMouseX;

    private double actualMouseY;

    public boolean dragging = false;

    public boolean useGeneratedBackground;
    protected final ItemRenderer itemRenderer;

    protected final Font font = Minecraft.getInstance().font;

    protected final List<Renderable> nestedRenderables = new ArrayList<>();

    protected final DrawableStatic drawable;
    public T parent;
    public FactoryScreenWindow(AbstractDrawableButton<?> config, DrawableStatic drawable, T parent){
        super(drawable.getX(),drawable.getY(),drawable.width(), drawable.height(), Component.empty());
        this.config = config;
        this.lastX = getX();
        this.lastY = getY();
        this.parent = parent;
        this.drawable = drawable;

        itemRenderer = Minecraft.getInstance().getItemRenderer();
    }

    @Override
    public boolean isVisible() {
        return config.selected == Boolean.TRUE && config.visible.get();
    }

    public void onClose(){
        this.config.selected = false;
        Consumer<FactoryScreenWindow<?>> onClick = (w)->{ if (w.isVisible()) w.onClickWidget();};
        parent.children().forEach(l-> {if (l instanceof FactoryScreenWindow<?> s) onClick.accept(s);});
        if (parent instanceof IWindowWidget w) w.getNestedRenderables().forEach(l-> {if (l instanceof FactoryScreenWindow<?> s) onClick.accept(s);});
    }
    public void onClickWidget(){
        alpha = 1.0F;
        parent.setFocused(this);
    }
    public void onClickOutside(double mouseX, double mouseY){
        setFocused(false);
        alpha = 0.88F;
    }

    @Override
    public boolean keyPressed(int i, int j, int k) {
        if (i == 256 && isVisible()) {
            onClose();
            return true;
        }

        return false;
    }

    protected void renderBg(GuiGraphics graphics, int i, int j, float f) {
        FactoryGuiMatrixStack.of(graphics.pose()).pushPose();
        FactoryScreenUtil.enableBlend();
        FactoryScreenUtil.enableDepthTest();
        //? if <1.21.6
        RenderSystem.setShaderColor(1,1,1,alpha);
        if (useGeneratedBackground) FactoryScreenUtil.drawGUIBackground(graphics, getX(), getY(), width, height);
        else drawable.draw(graphics,getX(),getY());
        IWindowWidget.super.render(graphics,i,j,f);
        //? if <1.21.6
        RenderSystem.setShaderColor(1,1,1,1);
        FactoryScreenUtil.disableBlend();
        FactoryScreenUtil.disableDepthTest();
        FactoryGuiMatrixStack.of(graphics.pose()).popPose();
    }

    @Override
    public List<? extends Renderable> getNestedRenderables() {
        return nestedRenderables;
    }

    @Override
    public <R extends Renderable> R addNestedRenderable(R drawable) {
        nestedRenderables.add(drawable);
        return drawable;
    }

    @Override
    protected void renderWidget(GuiGraphics graphics, int i, int j, float f) {
        if (!isVisible()) return;
        FactoryGuiMatrixStack.of(graphics.pose()).pushPose();
        FactoryGuiMatrixStack.of(graphics.pose()).translate(0D,0D,  getBlitOffset());
        renderBg(graphics,i,j,f);
        renderToolTip(graphics,i,j);
        FactoryGuiMatrixStack.of(graphics.pose()).popPose();
    }

    public float getBlitOffset(){
        return 450F;
    }

    public void renderToolTip(GuiGraphics graphics, int i, int j) {

    }
    @Override
    public boolean isMouseOver(double d, double e) {
        return isVisible() && FactoryScreenUtil.isMouseOver(d,e,getX(),getY(),width,height);
    }

    public void updateActualMouse(double mouseX, double mouseY){
        actualMouseX = mouseX;
        actualMouseY = mouseY;
    }
    public void updateLastMouse(int mouseX, int mouseY){
        lastX = mouseX;
        lastY = mouseY;
    }



    @Override
    public boolean mouseClicked(double d, double e, int i) {
        if (!isVisible()) return false;
        if (i == 0) {
            if (isMouseOver(d,e) || config.isMouseOver(d,e)) {
                onClickWidget();
                if (isMouseOver(d,e))updateActualMouse(d, e);
            }else  onClickOutside(d,e);
        }
        return IWindowWidget.super.mouseClicked(d, e, i);
    }

    @Override
    public boolean mouseReleased(double d, double e, int i) {
        if (dragging) {
            parent.setDragging(dragging = false);;
            updateLastMouse(getX(),getY());
            return true;
        }
        return IWindowWidget.super.mouseReleased(d, e, i);
    }
    int lastX;
    int lastY;
    @Override
    public boolean mouseDragged(double d, double e, int i, double f, double g) {
        if (!isVisible() || ((parent.isDragging() && !dragging))) return false;
        if (IWindowWidget.super.mouseDragged(d, e, i,f,g)) return true;
        if (i == 0 && ((isMouseOver(d,e) || dragging))) {
            double newX =  (lastX + d - actualMouseX);
            double newY = (lastY + e - actualMouseY);
            if (newX + width < parent.width && newX > 0 )
                setX((int) newX);
            if (newY + height < parent.height && newY > 0)
                setY((int)newY);
            parent.setDragging(dragging = true);
            return true;
        }
        return false;
    }
    public Rect2i getBounds() {
        return new Rect2i(getX(),getY(),width,height);
    }

}
