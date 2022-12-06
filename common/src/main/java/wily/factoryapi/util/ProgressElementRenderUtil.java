package wily.factoryapi.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.fluid.FluidStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import org.jetbrains.annotations.Nullable;
import wily.factoryapi.base.ProgressType;


@Environment(value = EnvType.CLIENT)
public class ProgressElementRenderUtil {


    public static void renderDefaultProgress(PoseStack matrix, @Nullable AbstractContainerScreen<?> abstractContainerScreen, int x, int y, int progress, ProgressType type){
        Screen screen = Minecraft.getInstance().screen;
        boolean bl = type.Plane == ProgressType.Direction.HORIZONTAL;
        boolean bl2 = type.Plane == ProgressType.Direction.VERTICAL;
        if (abstractContainerScreen !=null){
            screen = abstractContainerScreen;
        }
        if (type.isReverse) {
            if (bl) x+= type.sizeX - progress;
            if (bl2) y+= type.sizeY - progress;
        }
        RenderSystem.setShaderTexture(0, type.texture);
        if(progress > 0) {
            if (bl)
                screen.blit(matrix,  x,  y, type.uvX, type.uvY, progress, type.sizeY);
            if (bl2)
                screen.blit(matrix,  x,  y + type.sizeY - progress, type.uvX, type.uvY + (type.sizeY - progress), type.sizeX,  progress );
        }
    }

    public static void renderFluidTank(PoseStack matrix, @Nullable AbstractContainerScreen<?> abstractContainerScreen, int x, int y, int progress, ProgressType type, FluidStack stack, boolean hasColor){
        Screen screen = Minecraft.getInstance().screen;
        if (abstractContainerScreen !=null){
            screen = abstractContainerScreen;
        }
        if (progress > 0) {
            RenderSystem.enableBlend();
            progress /= 1.3;
            int fluidWidth = type.sizeX;
            int fluidHeight = (type.sizeY );
            int posY = y + type.sizeY - progress;

            for (int i = 0; i < fluidWidth; i += 16) {
                for (int j = 0; j < progress; j += 16) {
                   FluidRenderUtil.renderTiledFluid(x, posY, i, j, progress, fluidWidth, fluidHeight, stack, hasColor);
                }
            }

            RenderSystem.disableBlend();
            RenderSystem.setShaderColor(1, 1, 1, 1);
        }
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0,type.texture);
        screen.blit(matrix,x,y, type.uvX, type.uvY, type.sizeX,type.sizeY);
    }

}
