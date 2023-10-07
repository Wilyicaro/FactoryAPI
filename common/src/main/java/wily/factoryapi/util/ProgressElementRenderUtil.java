package wily.factoryapi.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.architectury.fluid.FluidStack;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import wily.factoryapi.base.client.drawable.IFactoryDrawableType;


@Environment(value = EnvType.CLIENT)
public class ProgressElementRenderUtil {
    public static Minecraft minecraft = Minecraft.getInstance();


    public static void renderDefaultProgress(PoseStack poseStack, int x, int y, float percentage, IFactoryDrawableType.DrawableProgress type){

        int progress = Math.round(percentage*(type.plane().isVertical() ? type.height() : type.width()));
        if (type.reverse()) {
            if (type.plane().isHorizontal()) x+= type.width() - progress;
            else y+= type.height() - progress;
        }
        if(progress > 0) {
            RenderSystem.setShaderTexture(0,type.texture());
            if (type.plane().isHorizontal())
                GuiComponent.blit(poseStack,  x,  y, type.uvX(), type.uvY(), progress, type.height(),256,256);
            else
                GuiComponent.blit(poseStack, x,  y + type.height() - progress, type.uvX(), type.uvY() + (type.height() - progress), type.width(), progress,256,256);
        }
    }

    public static void renderFluidTank(PoseStack poseStack, int x, int y, IFactoryDrawableType type, FluidStack stack, long capacity, boolean hasColor){
        int progress = capacity <= 0 ? 0 : Math.round(((float)stack.getAmount() /capacity) * type.height());
        if (progress > 0) {
            RenderSystem.enableBlend();
            progress /= 1.3;
            int fluidWidth = type.width();
            int posY = y + type.height() - progress;

            TextureAtlasSprite fluidSprite = FluidRenderUtil.fluidSprite(stack, hasColor);
            RenderSystem.setShaderTexture(0,fluidSprite.atlas().location());
            for (int i = 0; i < fluidWidth; i += 16) {
                for (int j = 0; j < progress; j += 16) {
                    FluidRenderUtil.renderTiledFluid(poseStack ,x, posY, i, j, progress, fluidWidth, fluidSprite);
                }
            }

            RenderSystem.disableBlend();
        }
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0,type.texture());
        type.draw(poseStack,x,y);
    }

}
