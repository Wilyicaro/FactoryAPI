package wily.factoryapi.util;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import wily.factoryapi.base.IFactoryItem;
import wily.factoryapi.base.client.IFactoryBlockEntityWLRenderer;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class ScreenUtil {

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

    private static final Minecraft mc = Minecraft.getInstance();




    public static void drawString(PoseStack stack, String text, int x, int y, int color, boolean shadow) {
        Font font = mc.font;
        MultiBufferSource.BufferSource source = mc.renderBuffers().bufferSource();
        font.drawInBatch(text, (float)x, (float)y, color, shadow, stack.last().pose(), source, Font.DisplayMode.NORMAL, 0, 15728880, font.isBidirectional());
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

    public static void drawGUIBackground(PoseStack stack, int x, int y, int width, int height, int outlineColor, int mainColor, int shadowColor, int lightColor){
        GuiComponent.fill(stack,x + 2, y + 2, x + width - 2, y + height - 2, mainColor);


        GuiComponent.fill(stack, x + 2, y + 1, x + width - 3, y + 3, lightColor);
        GuiComponent.fill(stack,x + 3, y + 3, x + 4, y + 4, lightColor);
        GuiComponent.fill(stack,x + 1, y + 2, x + 3, y + height - 3, lightColor);
        GuiComponent.fill(stack,x + 3, y + height - 1, x + width - 3, y + height - 3, shadowColor);
        GuiComponent.fill(stack,x + width - 1, y + 3, x + width - 3, y + height - 3, shadowColor);
        GuiComponent.fill(stack,x + width - 4, y + height - 4, x + width - 2, y + height - 2, shadowColor);

        GuiComponent.fill(stack,x + 2, y, x + width - 3, y + 1, outlineColor);
        GuiComponent.fill(stack,x + 3, y + height - 1, x + width - 3, y + height, outlineColor);
        GuiComponent.fill(stack,x, y + 2, x + 1, y + height - 3, outlineColor);
        GuiComponent.fill(stack,x + width - 1, y + 3, x + width, y + height - 3, outlineColor);
        BiConsumer<Integer, Integer> outlinePixel = (posx, posy)->GuiComponent.fill(stack,posx, posy, posx + 1, posy + 1, outlineColor);
        outlinePixel.accept(x + 1, y + 1);
        outlinePixel.accept(x + width - 2, y + 1);
        outlinePixel.accept(x + width - 1, y + 2);
        outlinePixel.accept(x + 1, y + height - 3);
        outlinePixel.accept(x + 2, y + height - 2);
        outlinePixel.accept(x+ width -2, y + height - 3);
        outlinePixel.accept(x+ width -3, y + height - 2);

    }
    public static void drawGUIBackground(PoseStack stack, int x, int y, int width, int height) {
        drawGUIBackground(stack, x,y,width,height,-16777216, -3750202, -11184811, -1);
    }
    public static void drawGUISlot(PoseStack poseStack, int x, int y, int width, int height, int shadowColor, int lightColor, int cornerColor, Integer backGroundColor) {
        GuiComponent.fill(poseStack,x, y, x + width - 1, y + 1, shadowColor);
        GuiComponent.fill(poseStack,x, y + 1, x + 1, y + height - 1, shadowColor);
        GuiComponent.fill(poseStack,x + 1, y + height - 1, x + width, y + height, lightColor);
        GuiComponent.fill(poseStack,x + width - 1, y + 1, x + width, y + height - 1, lightColor);
        GuiComponent.fill(poseStack,x + width - 1, y + 1, x + width, y + height - 1, lightColor);
        GuiComponent.fill(poseStack,x + width - 1, y, x + width, y + 1, cornerColor);
        GuiComponent.fill(poseStack,x , y + height - 1, x + 1, y + height, cornerColor);
        if (backGroundColor != null)
            GuiComponent.fill(poseStack,x + 1, y + 1, x + width - 1, y + height - 1, backGroundColor);
    }
    public static void drawGUISlot(PoseStack stack, int x, int y, int width, int height) {
        drawGUISlot(stack,x,y,width,height, -13158601, -1, -7631989, -7631989);
    }
    public static void drawGUISubSlot(PoseStack stack, int x, int y, int width, int height) {
        drawGUISlot(stack,x,y,width,height, -16777216, -1, -13158601, -14342875);
    }
    public static void drawGUIFluidSlot(PoseStack stack, int x, int y, int width, int height) {
        drawGUISlot(stack,x,y,width,height, -13158601, -1, -7631989, -8947849);
    }
    public static void drawGUISlotOutline(PoseStack stack, int x, int y, int width, int height) {
        drawGUISlot(stack,x,y,width,height, -10263709, -5066062, -8158333, null);
    }

    public static void renderScaled(PoseStack stack, String text, int x, int y, float scale, int color, boolean shadow) {
        prepTextScale(stack, m -> drawString(stack, text, 0, 0, color, shadow), x, y, scale);
    }
    public static BakedModel getItemStackModel(ItemRenderer itemRenderer,ItemStack stack){
        return itemRenderer.getModel(stack,null,null,0);
    }
    public static ScreenRectangle rect2iToRectangle(Rect2i rect){
        return new ScreenRectangle(new ScreenPosition(rect.getX(),rect.getY()),rect.getWidth(),rect.getHeight());
    }
    public static void renderGuiBlock(PoseStack poseStack, @Nullable BlockEntity be, BlockState state, int i, int j, float scaleX, float scaleY, float rotateX, float rotateY) {
        ItemRenderer itemRenderer = mc.getItemRenderer();
        ItemStack stack = state.getBlock().asItem().getDefaultInstance();
        BakedModel bakedModel = getItemStackModel(itemRenderer,stack);
        poseStack.pushPose();
        poseStack.translate(i + 8F, j + 8F, 250F);
        poseStack.scale(1.0F, -1.0F, 1.0F);
        poseStack.scale(16.0F, 16.0F, 16.0F);
        poseStack.scale(scaleX, scaleY, 0.5F);
        poseStack.mulPose(Axis.XP.rotationDegrees(rotateX));
        poseStack.mulPose(Axis.YP.rotationDegrees(rotateY));
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        Consumer<BakedModel> defaultRender = (b)->{
            itemRenderer.render(stack, ItemDisplayContext.NONE, false, poseStack, bufferSource, 15728880, OverlayTexture.NO_OVERLAY,b);
            bufferSource.endBatch();
            RenderSystem.enableDepthTest();
            Lighting.setupFor3DItems();
        };
        if (bakedModel.isCustomRenderer()){
            stack.getOrCreateTag().put("BlockEntityTag" ,be.getUpdateTag());
            if (stack.getItem() instanceof IFactoryItem f) {
                bakedModel.getTransforms().getTransform(ItemDisplayContext.NONE).apply(false, poseStack);
                poseStack.translate(-0.5f, -0.5f, -0.5f);
                f.clientExtension(c->{
                    if (c.getCustomRenderer(mc.getBlockEntityRenderDispatcher(), mc.getEntityModels()) instanceof IFactoryBlockEntityWLRenderer r) r.renderByItemBlockState(state, stack, ItemDisplayContext.NONE, poseStack, mc.renderBuffers().bufferSource(),15728880, OverlayTexture.NO_OVERLAY);
                });
            }else defaultRender.accept(bakedModel);
        }else defaultRender.accept(mc.getBlockRenderer().getBlockModel(state));

        poseStack.popPose();
    }
    public static void renderOutline(PoseStack poseStack, int i, int j, int k, int l, int m) {
        GuiComponent.fill(poseStack, i, j, i + k, j + 1, m);
        GuiComponent.fill(poseStack,i, j + l - 1, i + k, j + l, m);
        GuiComponent.fill(poseStack,i, j + 1, i + 1, j + l - 1, m);
        GuiComponent.fill(poseStack,i + k - 1, j + 1, i + k, j + l - 1, m);
    }
    public static void playButtonDownSound(float grave) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, grave));
    }

}
