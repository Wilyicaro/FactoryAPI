package wily.factoryapi.base.client.drawable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import wily.factoryapi.base.client.FactoryGuiMatrixStack;
import wily.factoryapi.util.FluidInstance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public abstract class AbstractDrawableStatic<D extends AbstractDrawableStatic<D,T>, T extends IFactoryDrawableType> extends Rect2i implements IFactoryDrawableType, Renderable{
    public T drawable;
    protected final List<Component> tooltips = new ArrayList<>();
    public boolean hovered = false;
    protected Minecraft mc = Minecraft.getInstance();
    public Supplier<Boolean> visible = ()-> true;
    protected IFactoryDrawableType overlay;

    public AbstractDrawableStatic(T drawable, int posX, int posY){
        super(posX,posY,drawable.width(),drawable.height());
        this.drawable = drawable;
    }
    public D overlay(IFactoryDrawableType overlay){
        this.overlay = overlay;
        return (D) this;
    }
    public D visible(Supplier<Boolean> visible){
        this.visible = visible;
        return (D) this;
    }
    public D tooltip(Component component){
        tooltips.add(component);
        return (D) this;
    }
    public D tooltips(List<Component> components){
        tooltips.addAll(components);
        return (D) this;
    }
    public D clearTooltips(){
        tooltips.clear();
        return (D) this;
    }
    public void draw(GuiGraphics graphics) {
        draw(graphics, getX(), getY());
    }

    @Override
    public void draw(GuiGraphics graphics, int x, int y) {
        IFactoryDrawableType.super.draw(graphics, x, y);
        FactoryGuiMatrixStack.of(graphics.pose()).pushPose();
        FactoryGuiMatrixStack.of(graphics.pose()).translate(0F,0F,1F);
        if (overlay != null) {
            int dX = overlay instanceof AbstractDrawableStatic<?,?> d ? d.getX() : 0;
            int dY = overlay instanceof AbstractDrawableStatic<?,?> d ? d.getY() : 0;
            overlay.draw(graphics, x + dX + (width() - overlay.width()) / 2, y + dY + (height() - overlay.height()) / 2);
        }
        FactoryGuiMatrixStack.of(graphics.pose()).popPose();
    }

    @Override
    public boolean isSprite() {
        return drawable.isSprite();
    }

    public void drawAsFluidTank(GuiGraphics graphics, FluidInstance instance, int capacity, boolean hasColor) {
        drawable.drawAsFluidTank(graphics, getX(), getY(), instance, capacity, hasColor);
    }
    public boolean inMouseLimit(double mouseX, double mouseY) {
        return drawable.inMouseLimit(mouseX,mouseY,getX(),getY());
    }

    public Identifier texture() {return drawable.texture();}
    public int width() {return getWidth();}
    public int height() {return getHeight();}
    public int uvX() {return drawable.uvX();}
    public int uvY() {return drawable.uvY();}

    @Override
    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        if (!visible.get()) return;
        hovered = inMouseLimit(i,j);
        draw(guiGraphics);
        //? if >=1.21.6 {
        if (hovered && !tooltips.isEmpty()) guiGraphics.setTooltipForNextFrame(mc.font, tooltips.stream().map(Component::getVisualOrderText).toList(), i, j);
        //?} else {
        /*if (hovered && !tooltips.isEmpty()) guiGraphics.renderComponentTooltip(mc.font, tooltips,i,j);
        *///?}
    }
}
