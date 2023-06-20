package wily.factoryapi.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import me.shedaniel.architectury.fluid.FluidStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import wily.factoryapi.base.FactoryDrawableType;


@Environment(value = EnvType.CLIENT)
public class ProgressElementRenderUtil {
    public static Minecraft minecraft = Minecraft.getInstance();


    public static void renderDefaultProgress(PoseStack matrix, int x, int y, int progress, FactoryDrawableType.DrawableProgress type){
        if (type.reverse) {
            if (type.plane.isHorizontal()) x+= type.width - progress;
            else y+= type.height - progress;
        }
        minecraft.getTextureManager().bind( type.texture);
        if(progress > 0) {
            if (type.plane.isHorizontal())
                GuiComponent.blit(matrix,  x,  y, type.uvX, type.uvY, progress, type.height,256,256);
            else
                GuiComponent.blit(matrix,  x,  y + type.height - progress, type.uvX, type.uvY + (type.height - progress), type.width, progress,256,256);
        }
    }

    public static void renderFluidTank(PoseStack poseStack, int x, int y, int progress, FactoryDrawableType type, FluidStack stack, boolean hasColor){

        if (progress > 0) {
            RenderSystem.enableBlend();
            progress /= 1.3;
            int fluidWidth = type.width;
            int fluidHeight = (type.height );
            int posY = y + type.height - progress;

            for (int i = 0; i < fluidWidth; i += 16) {
                for (int j = 0; j < progress; j += 16) {
                   FluidRenderUtil.renderTiledFluid(x, posY, i, j, progress, fluidWidth, fluidHeight, stack, hasColor);
                }
            }

            RenderSystem.disableBlend();
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        }
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        minecraft.getTextureManager().bind(type.texture);
        type.draw(poseStack,x,y);
    }

}
