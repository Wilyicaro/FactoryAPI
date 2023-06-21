package wily.factoryapi.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.fluid.FluidStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiComponent;
import wily.factoryapi.base.IFactoryDrawableType;


@Environment(value = EnvType.CLIENT)
public class ProgressElementRenderUtil {


    public static void renderDefaultProgress(PoseStack matrix, int x, int y, int progress, IFactoryDrawableType.DrawableProgress type){
        if (type.reverse()) {
            if (type.plane().isHorizontal()) x+= type.width() - progress;
            else y+= type.height() - progress;
        }
        RenderSystem.setShaderTexture(0, type.texture());
        if(progress > 0) {
            if (type.plane().isHorizontal())
                GuiComponent.blit(matrix,  x,  y, type.uvX(), type.uvY(), progress, type.height());
            else
                GuiComponent.blit(matrix,  x,  y + type.height() - progress, type.uvX(), type.uvY() + (type.height() - progress), type.width(), progress);
        }
    }

    public static void renderFluidTank(PoseStack poseStack, int x, int y, int progress, IFactoryDrawableType type, FluidStack stack, boolean hasColor){

        if (progress > 0) {
            RenderSystem.enableBlend();
            progress /= 1.3;
            int fluidWidth = type.width();
            int fluidHeight = (type.height() );
            int posY = y + type.height() - progress;

            for (int i = 0; i < fluidWidth; i += 16) {
                for (int j = 0; j < progress; j += 16) {
                   FluidRenderUtil.renderTiledFluid(x, posY, i, j, progress, fluidWidth, fluidHeight, stack, hasColor);
                }
            }

            RenderSystem.disableBlend();
            RenderSystem.setShaderColor(1, 1, 1, 1);
        }
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0,type.texture());
        type.draw(poseStack,x,y);
    }

}
