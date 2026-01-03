package wily.factoryapi.util;

//? if >1.21.4 {
import com.mojang.blaze3d.opengl.GlStateManager;
 //?} else {
/*import com.mojang.blaze3d.platform.GlStateManager;
*///?}
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.navigation.ScreenPosition;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Display;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix3x2fStack;
import wily.factoryapi.FactoryAPIClient;
import wily.factoryapi.base.client.FactoryGuiGraphics;
import wily.factoryapi.base.client.FactoryGuiMatrixStack;
import wily.factoryapi.base.client.IFactoryItemClientExtension;
import wily.factoryapi.base.client.UIAccessor;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class FactoryScreenUtil {
    private static final Minecraft mc = Minecraft.getInstance();

    public static void drawString(FactoryGuiMatrixStack stack, String text, int x, int y, int color, boolean shadow) {
        Font font = mc.font;
        //? if >=1.21.6 {
        switch (stack) {
            case GuiGraphics graphics -> graphics.drawString(font, text, x, y, color, shadow);
            case PoseStack poseStack -> {
                MultiBufferSource.BufferSource source = mc.renderBuffers().bufferSource();
				font.drawInBatch(/*? if >=1.21.2 {*/Component.literal(text)/*?} else {*/ /*text*//*?}*/, (float) x, (float) y, color, shadow, poseStack.last().pose(), source, Font.DisplayMode.NORMAL, 0, 15728880/*? if <1.21.6 {*//*, font.isBidirectional()*//*?}*/);
                disableDepthTest();
                source.endBatch();
                enableDepthTest();
			}
			default -> throw new IllegalStateException("Unexpected value: " + stack);
		}
        //?} else {
        /*MultiBufferSource.BufferSource source = mc.renderBuffers().bufferSource();
        font.drawInBatch(/^? if >=1.21.2 {^/Component.literal(text)/^?} else {^/ /^text^//^?}^/, (float) x, (float) y, color, shadow, stack.<PoseStack>getNative().last().pose(), source, Font.DisplayMode.NORMAL, 0, 15728880/^? if <1.21.6 {^//^, font.isBidirectional()^//^?}^/);
        disableDepthTest();
        source.endBatch();
        enableDepthTest();
        *///?}
    }

    public static void disableDepthTest(){
        GlStateManager._disableDepthTest();
    }

    public static void enableDepthTest(){
        GlStateManager._enableDepthTest();
    }

    public static void disableBlend(){
        GlStateManager._disableBlend();
    }

    public static void enableBlend(){
        GlStateManager._enableBlend();
    }

    public static void setShaderColor(float r, float g, float b, float a) {

    }

    public static void prepTextScale(FactoryGuiMatrixStack poseStack, Consumer<FactoryGuiMatrixStack> runnable, float x, float y, float scale) {
        float yAdd = 4 - (scale * 8) / 2F;
        poseStack.pushPose();
        poseStack.translate(x, y + yAdd, 0);
        poseStack.scale(scale, scale, scale);
        runnable.accept(poseStack);
        poseStack.popPose();
        setShaderColor(1, 1, 1, 1);
    }

    public static ScreenRectangle rect2iToRectangle(Rect2i rect){
        return new ScreenRectangle(new ScreenPosition(rect.getX(),rect.getY()),rect.getWidth(),rect.getHeight());
    }

    //? if <1.21.6 {
    /*public static void renderGuiBlock(GuiGraphics graphics, @Nullable BlockEntity be, BlockState state, int i, int j, float scaleX, float scaleY, float rotateX, float rotateY) {
        Item item = state.getBlock().asItem();
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
        BlockEntityRenderer<BlockEntity> blockEntityRenderer;
        if (be == null || (blockEntityRenderer = mc.getBlockEntityRenderDispatcher().getRenderer(be)) == null) {
            if ((e = IFactoryItemClientExtension.map.get(item)) != null && e.getCustomRenderer() != null) {
                e.getCustomRenderer().renderByItemBlockState(state, item.getDefaultInstance(), ItemDisplayContext.NONE, graphics.pose(), FactoryGuiGraphics.of(graphics).getBufferSource(), 15728880, OverlayTexture.NO_OVERLAY);
            } else {
                mc.getBlockRenderer().renderSingleBlock(state, graphics.pose(), FactoryGuiGraphics.of(graphics).getBufferSource(), 15728880, OverlayTexture.NO_OVERLAY);
            }
        } else {
            blockEntityRenderer.render(be, FactoryAPIClient.getGamePartialTick(true),graphics.pose(),FactoryGuiGraphics.of(graphics).getBufferSource(),15728880, OverlayTexture.NO_OVERLAY /^? if >1.21.4 {^/, Vec3.ZERO/^?}^/);
        }
        graphics.flush();
        graphics.pose().popPose();
    }
    *///?} else {
    public static void renderGuiBlock(GuiGraphics graphics, @Nullable BlockEntity be, BlockState state, int i, int j, float scaleX, float scaleY, float rotateX, float rotateY) {
        throw new IllegalStateException("Not implemented in 1.21.6+!");
    }
    //?}

    public static void playButtonDownSound(float grave) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, grave));
    }

    public static boolean isMouseOver(double mouseX, double mouseY, int posX, int posY, int sizeX, int sizeY){
        return (mouseX >= posX && mouseX < posX + sizeX && mouseY >= posY && mouseY < posY + sizeY);
    }

    public static void applyOffset(GuiGraphics graphics, float x, float y, float z) {
        if (x != 0 || y != 0 | z != 0) FactoryGuiMatrixStack.of(graphics.pose()).translate(x, y, z);
    }

    public static void applyScale(GuiGraphics graphics, float x, float y, float z) {
        if (x != 1 || y != 1 || z != 1) FactoryGuiMatrixStack.of(graphics.pose()).scale(x, y, z);
    }

    public static void applyColor(GuiGraphics graphics, int color) {
        //? if >=1.21.6 {
        if (color != -1) FactoryGuiGraphics.of(graphics).setBlitColor(color);
        //?} else
        //if (color != -1) FactoryGuiGraphics.of(graphics).setColor(color, true);
    }

    public static UIAccessor getScreenAccessor(){
        return UIAccessor.of(mc.screen);
    }

    public static UIAccessor getGuiAccessor(){
        return UIAccessor.of(mc.gui);
    }
}
