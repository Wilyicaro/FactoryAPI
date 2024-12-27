package wily.factoryapi.util;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import wily.factoryapi.FactoryAPIClient;
import wily.factoryapi.base.client.FactoryGuiGraphics;
import wily.factoryapi.base.IFactoryItem;
import wily.factoryapi.base.client.IFactoryBlockEntityWLRenderer;
import wily.factoryapi.base.client.IFactoryItemClientExtension;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class FactoryScreenUtil {
    private static final Minecraft mc = Minecraft.getInstance();


    public static float getRed(int color) {
        return (color >> 16 & 0xFF) / 255.0F;
    }

    public static float getGreen(int color) {
        return (color >> 8 & 0xFF) / 255.0F;
    }

    public static float getBlue(int color) {
        return (color & 0xFF) / 255.0F;
    }

    public static float getAlpha(int color) {
        return (color >> 24 & 0xFF) / 255.0F;
    }

    public static int colorFromFloat(float r, float g, float b, float a) {
        return (int)(a * 255f) << 24 | (int)(r * 255f) << 16 | (int)(g * 255f) << 8 | (int)(b * 255f);
    }

    public static int colorFromFloat(float[] rgba) {
        return colorFromFloat(rgba.length == 0 ? 0 : rgba[0], rgba.length <= 1 ? 0 : rgba[1], rgba.length <= 2 ? 0 : rgba[2], rgba.length <= 3 ? 0 : rgba[3]);
    }

    public static float[] rgbaToFloat(int rgba) {
        return new float[]{getRed(rgba),getGreen(rgba),getBlue(rgba),getAlpha(rgba)};
    }

    public static void drawString(PoseStack stack, String text, int x, int y, int color, boolean shadow) {
        Font font = mc.font;
        MultiBufferSource.BufferSource source = mc.renderBuffers().bufferSource();
        font.drawInBatch(/*? if >=1.21.2 {*//*Component.literal(text)*//*?} else {*/ text/*?}*/, (float)x, (float)y, color, shadow, stack.last().pose(), source, Font.DisplayMode.NORMAL, 0, 15728880, font.isBidirectional());
        RenderSystem.disableDepthTest();
        source.endBatch();
        RenderSystem.enableDepthTest();
    }

    public static void prepTextScale(PoseStack poseStack, Consumer<PoseStack> runnable, float x, float y, float scale) {
        float yAdd = 4 - (scale * 8) / 2F;
        poseStack.pushPose();
        poseStack.translate(x, y + yAdd, 0);
        poseStack.scale(scale, scale, scale);
        runnable.accept(poseStack);
        poseStack.popPose();
        RenderSystem.setShaderColor(1, 1, 1, 1);
    }

    public static void drawGUIBackground(GuiGraphics graphics, int x, int y, int width, int height, int outlineColor, int mainColor, int shadowColor, int lightColor){
        graphics.fill(x + 2, y + 2, x + width - 2, y + height - 2, mainColor);


        graphics.fill(x + 2, y + 1, x + width - 3, y + 3, lightColor);
        graphics.fill(x + 3, y + 3, x + 4, y + 4, lightColor);
        graphics.fill(x + 1, y + 2, x + 3, y + height - 3, lightColor);
        graphics.fill(x + 3, y + height - 1, x + width - 3, y + height - 3, shadowColor);
        graphics.fill(x + width - 1, y + 3, x + width - 3, y + height - 3, shadowColor);
        graphics.fill(x + width - 4, y + height - 4, x + width - 2, y + height - 2, shadowColor);

        graphics.fill(x + 2, y, x + width - 3, y + 1, outlineColor);
        graphics.fill(x + 3, y + height - 1, x + width - 3, y + height, outlineColor);
        graphics.fill(x, y + 2, x + 1, y + height - 3, outlineColor);
        graphics.fill(x + width - 1, y + 3, x + width, y + height - 3, outlineColor);
        BiConsumer<Integer, Integer> outlinePixel = (posx, posy)-> graphics.fill(posx, posy, posx + 1, posy + 1, outlineColor);
        outlinePixel.accept(x + 1, y + 1);
        outlinePixel.accept(x + width - 3, y + 1);
        outlinePixel.accept(x + width - 2, y + 2);
        outlinePixel.accept(x + 1, y + height - 3);
        outlinePixel.accept(x + 2, y + height - 2);
        outlinePixel.accept(x+ width -2, y + height - 3);
        outlinePixel.accept(x+ width -3, y + height - 2);

    }
    public static void drawGUIBackground(GuiGraphics graphics, int x, int y, int width, int height) {
        drawGUIBackground(graphics, x,y,width,height,-16777216, -3750202, -11184811, -1);
    }
    public static void drawGUISlot(GuiGraphics graphics, int x, int y, int width, int height, int shadowColor, int lightColor, int cornerColor, Integer backGroundColor) {
        graphics.fill(x, y, x + width - 1, y + 1, shadowColor);
        graphics.fill(x, y + 1, x + 1, y + height - 1, shadowColor);
        graphics.fill(x + 1, y + height - 1, x + width, y + height, lightColor);
        graphics.fill(x + width - 1, y + 1, x + width, y + height - 1, lightColor);
        graphics.fill(x + width - 1, y + 1, x + width, y + height - 1, lightColor);
        graphics.fill(x + width - 1, y, x + width, y + 1, cornerColor);
        graphics.fill(x , y + height - 1, x + 1, y + height, cornerColor);
        if (backGroundColor != null)
            graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, backGroundColor);
    }
    public static void drawGUISlot(GuiGraphics graphics, int x, int y, int width, int height) {
        drawGUISlot(graphics,x,y,width,height, -13158601, -1, -7631989, -7631989);
    }
    public static void drawGUISubSlot(GuiGraphics graphics, int x, int y, int width, int height) {
        drawGUISlot(graphics,x,y,width,height, -16777216, -1, -13158601, -14342875);
    }
    public static void drawGUIFluidSlot(GuiGraphics graphics, int x, int y, int width, int height) {
        drawGUISlot(graphics,x,y,width,height, -13158601, -1, -7631989, -8947849);
    }
    public static void drawGUISlotOutline(GuiGraphics graphics, int x, int y, int width, int height) {
        drawGUISlot(graphics,x,y,width,height, -10263709, -5066062, -8158333, null);
    }

    public static void renderScaled(PoseStack stack, String text, int x, int y, float scale, int color, boolean shadow) {
        prepTextScale(stack, m -> drawString(stack, text, 0, 0, color, shadow), x, y, scale);
    }

    public static ScreenRectangle rect2iToRectangle(Rect2i rect){
        return new ScreenRectangle(new ScreenPosition(rect.getX(),rect.getY()),rect.getWidth(),rect.getHeight());
    }

    public static void renderGuiBlock(GuiGraphics graphics, @Nullable BlockEntity be, BlockState state, int i, int j, float scaleX, float scaleY, float rotateX, float rotateY) {
        ItemStack stack = state.getBlock().asItem().getDefaultInstance();
        BakedModel bakedModel = mc.getBlockRenderer().getBlockModel(state);
        graphics.pose().pushPose();
        graphics.pose().translate(i + 8F, j + 8F, 250F);
        graphics.pose().scale(1.0F, -1.0F, 1.0F);
        graphics.pose().scale(16.0F, 16.0F, 16.0F);
        graphics.pose().scale(scaleX, scaleY, 0.5F);
        graphics.pose().mulPose(Axis.XP.rotationDegrees(rotateX));
        graphics.pose().mulPose(Axis.YP.rotationDegrees(rotateY));
        graphics.pose().translate(-0.5f, -0.5f, -0.5f);
        Lighting.setupForFlatItems();
        IFactoryItemClientExtension e;
        bakedModel.getTransforms().getTransform(ItemDisplayContext.NONE).apply(false, graphics.pose());
        BlockEntityRenderer<BlockEntity> blockEntityRenderer;
        if (be == null || (blockEntityRenderer = mc.getBlockEntityRenderDispatcher().getRenderer(be)) == null) {
            if ((e = IFactoryItemClientExtension.map.get(state.getBlock().asItem())) != null && e.getCustomRenderer() != null) {
                e.getCustomRenderer().renderByItemBlockState(state, stack, ItemDisplayContext.NONE, graphics.pose(), FactoryGuiGraphics.of(graphics).getBufferSource(), 15728880, OverlayTexture.NO_OVERLAY);
            } else {
                mc.getBlockRenderer().renderSingleBlock(state, graphics.pose(), FactoryGuiGraphics.of(graphics).getBufferSource(), 15728880, OverlayTexture.NO_OVERLAY);
            }
        } else {
            blockEntityRenderer.render(be, FactoryAPIClient.getGamePartialTick(true),graphics.pose(),FactoryGuiGraphics.of(graphics).getBufferSource(),15728880, OverlayTexture.NO_OVERLAY);
        }
        graphics.flush();
        graphics.pose().popPose();
    }

    public static void playButtonDownSound(float grave) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, grave));
    }

}
