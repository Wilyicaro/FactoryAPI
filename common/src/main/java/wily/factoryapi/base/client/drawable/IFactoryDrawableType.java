package wily.factoryapi.base.client.drawable;


import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.architectury.fluid.FluidStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;
import wily.factoryapi.base.Progress;
import wily.factoryapi.util.ProgressElementRenderUtil;

import static wily.factoryapi.util.ProgressElementRenderUtil.minecraft;

@Environment(EnvType.CLIENT)
public interface IFactoryDrawableType {
    IFactoryDrawableType EMPTY = new IFactoryDrawableType() {
        public void draw(PoseStack poseStack, int x, int y) {}
        public ResourceLocation texture() {return null;}
        public int width() {return 0;}

        public int height() {return 0;}

        public int uvX() {return 0;}

        public int uvY() {return 0;}
    };

    static IFactoryDrawableType empty(){return EMPTY;}

    ResourceLocation texture();

    class DrawableImage implements IFactoryDrawableType {
        private final ResourceLocation texture;
        private final int uvX;
        private final int uvY;
        private final int width;
        private final int height;

        public DrawableImage(ResourceLocation texture, int uvX, int uvY, int width, int height){
            this.texture = texture;
            this.uvX = uvX;
            this.uvY = uvY;
            this.width = width;
            this.height = height;
        }
        @Deprecated
        public DrawableProgress asProgress(Progress.Identifier identifier, boolean reverse, Direction plane){
            return asProgress(reverse,plane);
        }
        public DrawableProgress asProgress(boolean reverse, Direction plane){
            return new DrawableProgress(this,reverse,plane);
        }
        public DrawableStatic createStatic(int posX, int posY){
            return new DrawableStatic(this,posX,posY);
        }
        public ResourceLocation texture() {return texture;}
        public int width() {return width;}
        public int height() {return height;}
        public int uvX() {return uvX;}
        public int uvY() {return uvY;}
    }
    static DrawableImage create(ResourceLocation texture, int uvX, int uvY, int width, int height){
        return new DrawableImage(texture, uvX, uvY, width, height);
    }
    static DrawableImage create(ResourceLocation texture,  int width, int height){
        return new DrawableImage(texture, 0, 0, width, height);
    }
    class DrawableProgress extends DrawableImage {
        public final Direction plane;
        public final boolean reverse;

        public DrawableProgress(ResourceLocation texture, int uvX, int uvY, int width, int height, boolean reverse, Direction plane) {
            super(texture, uvX, uvY, width, height);
            this.plane = plane;
            this.reverse = reverse;
        }
        public DrawableProgress(IFactoryDrawableType drawableType,boolean reverse, Direction plane) {
            this(drawableType.texture(), drawableType.uvX(), drawableType.uvY(), drawableType.width(), drawableType.height(),reverse,plane);
        }

        public void drawProgress(PoseStack poseStack, int x, int y, float percentage){
            ProgressElementRenderUtil.renderDefaultProgress(poseStack,x,y,percentage,this);
        }
        public void drawProgress(PoseStack poseStack,int x, int y, int progress, int max){
            ProgressElementRenderUtil.renderDefaultProgress(poseStack,x,y, max <= 0 ? 0 : (float) progress / max,this);
        }
        public void drawProgress(PoseStack poseStack, int relativeX, int relativeY, Progress progress){
            progress.forEach(p->drawProgress(poseStack,relativeX + p.x, relativeY + p.y, p.get(), p.maxProgress));
        }

        public DrawableStaticProgress createStaticProgress(int posX, int posY){
            return new DrawableStaticProgress(this,posX,posY);
        }
    }

    int width();
    int height();

    default DrawableImage adjacentImage(Direction direction){
        return create(texture(),uvX() + (direction.isHorizontal() ? width(): 0),uvY() + (direction.isVertical() ? height(): 0), width(), height());
    }
    default void drawAsFluidTank(PoseStack poseStack, int x, int y, FluidStack stack, long capacity, boolean hasColor){
        ProgressElementRenderUtil.renderFluidTank(poseStack,x,y,this,stack,capacity, hasColor);
    }
    @Deprecated
    default void drawAsFluidTank(PoseStack poseStack, int x, int y, int progress, FluidStack stack, boolean hasColor){
        drawAsFluidTank(poseStack,x,y,stack, progress == 0 ? 0 : (int)  (stack.getAmount().longValue() / (progress / height())),hasColor);
    }
    default boolean inMouseLimit(double mouseX, double mouseY, int posX, int posY){
        return getMouseLimit(mouseX,mouseY,posX,posY, width(), height());
    }
    default void draw(PoseStack poseStack, int x, int y) {
        minecraft.getTextureManager().bind(texture());
        GuiComponent.blit(poseStack,x,y,uvX(),uvY(), width(), height(),256,256);
    }
    int uvX();
    int uvY();
    static boolean getMouseLimit(double mouseX, double mouseY, int posX, int posY, int sizeX, int sizeY){
        return (mouseX >= posX && mouseX < posX + sizeX && mouseY >= posY && mouseY < posY + sizeY);
    }
    enum Direction {
        VERTICAL,HORIZONTAL;
        public boolean isVertical(){return  this == VERTICAL;}
        public boolean isHorizontal(){return  this == HORIZONTAL;}
    }
}