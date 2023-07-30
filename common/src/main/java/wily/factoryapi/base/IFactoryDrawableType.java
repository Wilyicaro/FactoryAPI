package wily.factoryapi.base;


import dev.architectury.fluid.FluidStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import wily.factoryapi.util.ProgressElementRenderUtil;
@Environment(EnvType.CLIENT)
public interface IFactoryDrawableType {

    ResourceLocation texture();

    class  DrawableStatic<T extends IFactoryDrawableType> implements IFactoryDrawableType{
        public  T drawable;
        public int posX;
        public int posY;

        public DrawableStatic(T drawable, int posX, int posY){
            this.drawable = drawable;
            this.posX = posX;
            this.posY = posY;
        }
        public void draw(GuiGraphics graphics) {
            drawable.draw(graphics, posX, posY);
        }
        public void drawAsFluidTank(GuiGraphics graphics, FluidStack stack, long capacity, boolean hasColor) {
            drawable.drawAsFluidTank(graphics, posX, posY, stack, capacity, hasColor);
        }
        public boolean inMouseLimit(double mouseX, double mouseY) {
            return drawable.inMouseLimit(mouseX,mouseY,posX,posY);
        }

        public ResourceLocation texture() {return drawable.texture();}
        public int width() {return drawable.width();}
        public int height() {return drawable.height();}
        public int uvX() {return drawable.uvX();}
        public int uvY() {return drawable.uvY();}
    }

    record DrawableImage(ResourceLocation texture, int uvX, int uvY, int width, int height) implements IFactoryDrawableType {
        @Deprecated
        public DrawableProgress asProgress(Progress.Identifier identifier, boolean reverse, Direction plane){
            return asProgress(reverse,plane);
        }
        public DrawableProgress asProgress(boolean reverse, Direction plane){
            return new DrawableProgress(this,reverse,plane);
        }
        public DrawableStatic<DrawableImage> createStatic(int posX, int posY){
            return  new DrawableStatic<>(this,posX,posY);
        }
    }
    static DrawableImage create(ResourceLocation texture, int uvX, int uvY, int width, int height){
        return new DrawableImage(texture, uvX, uvY, width, height);
    }
    record DrawableProgress(DrawableImage drawable, boolean reverse, Direction plane) implements IFactoryDrawableType {
        public void drawProgress(GuiGraphics graphics,int x, int y, float percentage){
            ProgressElementRenderUtil.renderDefaultProgress(graphics,x,y,percentage,this);
        }
        public void drawProgress(GuiGraphics graphics,int x, int y, int progress, int max){
            ProgressElementRenderUtil.renderDefaultProgress(graphics,x,y, max <= 0 ? 0 : (float) progress / max,this);
        }
        public void drawProgress(GuiGraphics graphics, int relativeX, int relativeY, Progress progress){
            progress.getEntries().forEach(p->drawProgress(graphics,relativeX + p.x, relativeY + p.y, p.get(), p.maxProgress));
        }
        public DrawableStaticProgress createStatic(int posX, int posY){
            return  new DrawableStaticProgress(this,posX,posY);
        }

        public ResourceLocation texture() {return drawable.texture;}
        public int width() {return drawable.width;}
        public int height() {return drawable.height;}
        public int uvX() {return drawable.uvX;}
        public int uvY() {return drawable.uvY;}
    }
    class DrawableStaticProgress extends  DrawableStatic<DrawableProgress>{

        public DrawableStaticProgress(DrawableProgress drawable, int posX, int posY) {
            super(drawable, posX, posY);
        }
        public void drawProgress(GuiGraphics graphics, float percentage){
            drawable.drawProgress(graphics,posX,posY, percentage);
        }
        public void drawProgress(GuiGraphics graphics, int progress, int max){
            drawable.drawProgress(graphics,posX,posY, progress, max);
        }
        public void drawProgress(GuiGraphics graphics, Progress progress){
            drawable.drawProgress(graphics,posX,posY,progress);
        }
    }
    int width();
    int height();


    default void drawAsFluidTank(GuiGraphics graphics, int x, int y, FluidStack stack,long capacity, boolean hasColor){
        ProgressElementRenderUtil.renderFluidTank(graphics,x,y,this,stack,capacity, hasColor);
    }
    @Deprecated
    default void drawAsFluidTank(GuiGraphics graphics, int x, int y, int progress, FluidStack stack, boolean hasColor){
        drawAsFluidTank(graphics,x,y,stack, progress == 0 ? 0 : (int)  (stack.getAmount() / (progress /height())),hasColor);
    }
    default boolean inMouseLimit(double mouseX, double mouseY, int posX, int posY){
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