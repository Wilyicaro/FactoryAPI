package wily.factoryapi.base;

import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.architectury.fluid.FluidStack;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.resources.ResourceLocation;
import wily.factoryapi.util.ProgressElementRenderUtil;

import static wily.factoryapi.util.ProgressElementRenderUtil.minecraft;

public abstract class FactoryDrawableType {

    public final ResourceLocation texture;
    public final int uvX;
    public final int uvY;
    public final int width;
    public final int height;

    public FactoryDrawableType(ResourceLocation texture, int uvX, int uvY, int width, int height){
        this.texture = texture;
        this.uvX = uvX;
        this.uvY = uvY;
        this.width = width;
        this.height = height;
    }

    public static class DrawableImage extends FactoryDrawableType {
        public DrawableImage(ResourceLocation texture, int uvX, int uvY, int width, int height) {
            super(texture, uvX, uvY, width, height);
        }

        public DrawableProgress asProgress(Progress.Identifier identifier, boolean reverse, Direction plane){
            return new DrawableProgress(this,identifier,reverse,plane);
        }
    }
    public static DrawableImage create(ResourceLocation texture, int uvX, int uvY, int width, int height){
        return new DrawableImage(texture, uvX, uvY, width, height);
    }
    public static class DrawableProgress extends FactoryDrawableType {
        public final DrawableImage drawable;
        public final Progress.Identifier identifier;
        public final boolean reverse;
        public final Direction plane;

        public DrawableProgress(DrawableImage drawable, Progress.Identifier identifier, boolean reverse, Direction plane){
            super(drawable.texture, drawable.uvX, drawable.uvY, drawable.width, drawable.height);
            this.drawable = drawable;
            this.identifier = identifier;
            this.reverse = reverse;
            this.plane = plane;
        }
        public void drawProgress(PoseStack poseStack,int x, int y, int progress){
            ProgressElementRenderUtil.renderDefaultProgress(poseStack,x,y,progress,this);
        }
    }



    public void drawAsFluidTank(PoseStack poseStack, int x, int y, int progress, FluidStack stack, boolean hasColor){
        ProgressElementRenderUtil.renderFluidTank(poseStack,x,y,progress,this,stack,hasColor);
    }
    public boolean inMouseLimit(int mouseX, int mouseY, int posX, int posY){
        return getMouseLimit(mouseX,mouseY,posX,posY,width,height);
    }
    public void draw(PoseStack poseStack, int x, int y) {
        minecraft.getTextureManager().bind(texture);
        GuiComponent.blit(poseStack,x,y,uvX,uvY,width,height,256,256);
    }

    static boolean getMouseLimit(double mouseX, double mouseY, int posX, int posY, int sizeX, int sizeY){
        return (mouseX >= posX && mouseX <= posX + sizeX && mouseY >= posY && mouseY <= posY + sizeY);
    }
    public enum Direction {
        VERTICAL,HORIZONTAL;
        public boolean isVertical(){return  this == VERTICAL;}
        public boolean isHorizontal(){return  this == HORIZONTAL;}
    }
}