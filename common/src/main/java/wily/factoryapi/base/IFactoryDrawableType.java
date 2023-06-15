package wily.factoryapi.base;


import dev.architectury.fluid.FluidStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import wily.factoryapi.util.ProgressElementRenderUtil;

public interface IFactoryDrawableType {

    ResourceLocation texture();
    record DrawableImage(ResourceLocation texture, int uvX, int uvY, int width, int height) implements IFactoryDrawableType {
        public DrawableProgress asProgress(Progress.Identifier identifier, boolean reverse, Direction plane){
            return new DrawableProgress(this,identifier,reverse,plane);
        }
    }
    static DrawableImage create(ResourceLocation texture, int uvX, int uvY, int width, int height){
        return new DrawableImage(texture, uvX, uvY, width, height);
    }
    record DrawableProgress(DrawableImage drawable,Progress.Identifier identifier, boolean reverse, Direction plane) implements IFactoryDrawableType {
        public void drawProgress(GuiGraphics graphics,int x, int y, int progress){
            ProgressElementRenderUtil.renderDefaultProgress(graphics,x,y,progress,this);
        }
        public ResourceLocation texture() {return drawable.texture;}
        public int width() {return drawable.width;}
        public int height() {return drawable.height;}
        public int uvX() {return drawable.uvX;}
        public int uvY() {return drawable.uvY;}
    }
    int width();
    int height();

    default void drawAsFluidTank(GuiGraphics graphics, int x, int y, int progress, FluidStack stack, boolean hasColor){
        ProgressElementRenderUtil.renderFluidTank(graphics,x,y,progress,this,stack,hasColor);
    }
    default boolean inMouseLimit(int mouseX, int mouseY, int posX, int posY){
        return getMouseLimit(mouseX,mouseY,posX,posY,width(),height());
    }
    default void draw(GuiGraphics graphics, int x, int y) {
        graphics.blit(texture(),x,y,uvX(),uvY(),width(),height());
    }
    int uvX();
    int uvY();
    static boolean getMouseLimit(double mouseX, double mouseY, int posX, int posY, int sizeX, int sizeY){
        return (mouseX >= posX && mouseX <= posX + sizeX && mouseY >= posY && mouseY <= posY + sizeY);
    }
    enum Direction {
        VERTICAL,HORIZONTAL;
        public boolean isVertical(){return  this == VERTICAL;}
        public boolean isHorizontal(){return  this == HORIZONTAL;}
    }
}