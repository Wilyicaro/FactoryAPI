package wily.factoryapi.base.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import wily.factoryapi.base.client.drawable.AbstractDrawableButton;
import wily.factoryapi.base.client.drawable.DrawableStatic;
import wily.factoryapi.base.client.drawable.IFactoryDrawableType;
import wily.factoryapi.util.ScreenUtil;

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

    protected final List<Widget> nestedRenderables = new ArrayList<>();

    protected final DrawableStatic drawable;
    public T parent;
    public FactoryScreenWindow(AbstractDrawableButton<?> config, DrawableStatic drawable, T parent){
        super(drawable.getX(),drawable.getY(),drawable.getWidth(), drawable.getHeight(), TextComponent.EMPTY);
        this.config = config;
        this.lastX = x;
        this.lastY = y;
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
        parent.children().forEach(l-> {if (l instanceof FactoryScreenWindow<?>) onClick.accept((FactoryScreenWindow<?>)l);});
        if (parent instanceof IWindowWidget) ((IWindowWidget)parent).getNestedWidgets().forEach(l-> {if (l instanceof FactoryScreenWindow<?>) onClick.accept((FactoryScreenWindow<?>)l);});
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

    protected void renderBg(PoseStack stack, int i, int j, float f) {
        stack.pushPose();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        RenderSystem.setShaderColor(1,1,1,alpha);
        if (useGeneratedBackground) ScreenUtil.drawGUIBackground(stack, x, y, width, height);
        else drawable.draw(stack,x,y);
        IWindowWidget.super.render(stack,i,j,f);
        RenderSystem.setShaderColor(1,1,1,1);
        RenderSystem.disableBlend();
        RenderSystem.disableDepthTest();
        stack.popPose();
    }

    @Override
    public List<? extends Widget> getNestedWidgets() {
        return nestedRenderables;
    }

    @Override
    public <R extends Widget> R addNestedWidget(R drawable) {
        nestedRenderables.add(drawable);
        return drawable;
    }

    public void render(PoseStack stack, int i, int j, float f) {
        if (!isVisible()) return;
        stack.pushPose();
        stack.translate(0D,0D,  getBlitOffset());
        renderBg(stack,i,j,f);
        renderToolTip(stack,i,j);
        stack.popPose();

    }
    public int getBlitOffset(){
        return 450;
    }

    public void renderToolTip(PoseStack poseStack, int i, int j) {

    }
    @Override
    public boolean isMouseOver(double d, double e) {
        return isVisible() && IFactoryDrawableType.getMouseLimit(d,e,x,y,width,height);
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
            updateLastMouse(x,y);
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
                x= (int) newX;
            if (newY + height < parent.height && newY > 0)
                y =(int)newY;
            parent.setDragging(dragging = true);
            return true;
        }
        return false;
    }
    public Rect2i getBounds() {
        return new Rect2i(x,y,width,height);
    }

}
