package wily.factoryapi.fabric.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import wily.factoryapi.base.IFactoryBlock;

import java.util.Random;

@Mixin(ModelBlockRenderer.class )
public class ModelBlockRendererMixin {

    @ModifyVariable(method = ("tesselateBlock"), at = @At("STORE"), ordinal = 1)
    private boolean render(boolean b, BlockAndTintGetter level, BakedModel model, BlockState state, BlockPos pos, PoseStack matrices, VertexConsumer vertexConsumer, boolean cull, RandomSource random, long seed, int overlay) {
        return state.getBlock() instanceof IFactoryBlock?  Minecraft.useAmbientOcclusion() && ((IFactoryBlock)state.getBlock()).getLuminance(state,level,pos) == 0  && model.useAmbientOcclusion() : b;
    }
}
