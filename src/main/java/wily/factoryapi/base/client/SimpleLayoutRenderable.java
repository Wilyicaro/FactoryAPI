package wily.factoryapi.base.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Renderable;
import net.minecraft.client.gui.layouts.LayoutElement;
import net.minecraft.network.chat.Component;
import wily.factoryapi.util.FactoryScreenUtil;

import java.util.function.Consumer;
import java.util.function.Function;

public abstract class SimpleLayoutRenderable implements Renderable, LayoutElement {
    public int width;
    public int height;
    public int x;
    public int y;

    public SimpleLayoutRenderable(){
    }

    public SimpleLayoutRenderable(int width, int height){
        this.width = width;
        this.height = height;
    }

    public static SimpleLayoutRenderable create(Function<SimpleLayoutRenderable,Renderable> simpleRender){
        return new SimpleLayoutRenderable() {
            public void render(GuiGraphics guiGraphics, int i, int j, float f) {
                simpleRender.apply(this).render(guiGraphics,i,j,f);
            }
        };
    }

    public static SimpleLayoutRenderable create(int width, int height,Function<SimpleLayoutRenderable,Renderable> simpleRender){
        return new SimpleLayoutRenderable(width,height) {
            public void render(GuiGraphics guiGraphics, int i, int j, float f) {
                simpleRender.apply(this).render(guiGraphics,i,j,f);
            }
        };
    }

    public static SimpleLayoutRenderable createDrawString(Component message, int xOffset, int yOffset, int width, int height, int color, boolean shadow){
        return new SimpleLayoutRenderable(width,height) {
            public void render(GuiGraphics guiGraphics, int i, int j, float f) {
                guiGraphics.drawString(Minecraft.getInstance().font, message, getX() + xOffset, getY() + yOffset, color, shadow);
            }
        };
    }

    public void bounds(int x, int y, int width, int height){
        setPosition(x,y);
        size(width,height);
    }

    public void size(int width, int height){
        this.width = width;
        this.height = height;
    }

    public void init(){
    }

    public boolean isHovered(double mouseX, double mouseY){
        return FactoryScreenUtil.isMouseOver(mouseX,mouseY,getX(),getY(),getWidth(),getHeight());
    }

    public void setX(int i) {
        x = i;
    }

    public void setY(int i) {
        y = i;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void visitWidgets(Consumer<AbstractWidget> consumer) {

    }
}
