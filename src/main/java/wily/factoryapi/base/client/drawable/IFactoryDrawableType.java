package wily.factoryapi.base.client.drawable;


import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import wily.factoryapi.base.client.FactoryGuiGraphics;
import wily.factoryapi.base.Progress;
import wily.factoryapi.util.FactoryScreenUtil;
import wily.factoryapi.util.FluidInstance;
import wily.factoryapi.util.ProgressElementRenderUtil;

public interface IFactoryDrawableType {
    IFactoryDrawableType EMPTY = new IFactoryDrawableType() {
        public void draw(GuiGraphics graphics, int x, int y) {}
        public ResourceLocation texture() {return null;}
        public int width() {return 0;}

        public int height() {return 0;}

        public int uvX() {return 0;}

        public int uvY() {return 0;}

        public boolean isSprite() {return false;}
    };

    static IFactoryDrawableType empty(){return EMPTY;}

    ResourceLocation texture();

    record DrawableImage(ResourceLocation texture, int uvX, int uvY, int width, int height, boolean isSprite) implements IFactoryDrawableType {
        public DrawableImage(ResourceLocation texture, int uvX, int uvY, int width, int height){
            this(texture,uvX,uvY,width,height,false);
        }

        public DrawableProgress asProgress(boolean reverse, Direction plane){
            return new DrawableProgress(this,reverse,plane);
        }

        public DrawableStatic createStatic(int posX, int posY){
            return new DrawableStatic(this,posX,posY);
        }
    }
    static DrawableImage create(ResourceLocation texture, int uvX, int uvY, int width, int height){
        return new DrawableImage(texture, uvX, uvY, width, height);
    }
    static DrawableImage create(ResourceLocation texture,  int width, int height){
        return new DrawableImage(texture, 0, 0, width, height);
    }
    static DrawableImage create(ResourceLocation texture,  int width, int height, boolean isSprite){
        return new DrawableImage(texture, 0, 0, width, height, isSprite);
    }
    record DrawableProgress(DrawableImage drawable,boolean reverse, Direction plane) implements IFactoryDrawableType {
        public void drawProgress(GuiGraphics graphics,int x, int y, float percentage){
            ProgressElementRenderUtil.renderDefaultProgress(graphics,x,y,percentage,this);
        }
        public void drawProgress(GuiGraphics graphics,int x, int y, int progress, int max){
            ProgressElementRenderUtil.renderDefaultProgress(graphics,x,y, max <= 0 ? 0 : (float) progress / max,this);
        }
        public void drawProgress(GuiGraphics graphics, int relativeX, int relativeY, Progress progress){
            progress.forEach(p->drawProgress(graphics,relativeX + p.x, relativeY + p.y, p.get(), p.maxProgress));
        }

        public DrawableStaticProgress createStatic(int posX, int posY){
            return new DrawableStaticProgress(this,posX,posY);
        }

        public ResourceLocation texture() {return drawable.texture;}
        public int width() {return drawable.width;}
        public int height() {return drawable.height;}
        public int uvX() {return drawable.uvX;}
        public int uvY() {return drawable.uvY;}
        public boolean isSprite() {return drawable.isSprite;}
    }

    int width();
    int height();

    default DrawableImage adjacentImage(Direction direction){
        return create(texture(),uvX() + (direction.isHorizontal() ? width(): 0),uvY() + (direction.isVertical() ? height(): 0),width(),height());
    }

    default void drawAsFluidTank(GuiGraphics graphics, int x, int y, FluidInstance instance, int capacity, boolean hasColor){
        ProgressElementRenderUtil.renderFluidTank(graphics,x,y,this,instance,capacity, hasColor);
    }

    @Deprecated
    default void drawAsFluidTank(GuiGraphics graphics, int x, int y, int progress, FluidInstance instance, boolean hasColor){
        drawAsFluidTank(graphics,x,y,instance, progress == 0 ? 0 : (instance.getAmount() / (progress /height())),hasColor);
    }

    default boolean inMouseLimit(double mouseX, double mouseY, int posX, int posY){
        return FactoryScreenUtil.isMouseOver(mouseX,mouseY,posX,posY,width(),height());
    }

    default void draw(GuiGraphics graphics, int x, int y) {
        if (isSprite()) FactoryGuiGraphics.of(graphics).blitSprite(texture(),x,y,width(),height());
            else FactoryGuiGraphics.of(graphics).blit(texture(),x,y,uvX(),uvY(),width(),height());
    }

    int uvX();
    int uvY();

    boolean isSprite();

    enum Direction {
        VERTICAL,HORIZONTAL;
        public boolean isVertical(){return  this == VERTICAL;}
        public boolean isHorizontal(){return  this == HORIZONTAL;}
    }
}