package wily.factoryapi.base.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
//? if >=1.21.4 {
/*import net.minecraft.client.renderer.special.SpecialModelRenderer;
*///?}
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

public interface IFactoryBlockEntityWLRenderer /*? if >=1.21.4 {*//*extends SpecialModelRenderer<ItemStack>*//*?}*/ {
    void renderByItemBlockState(BlockState state, ItemStack itemStack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j);
    default void renderByItem(ItemStack itemStack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j){
        renderByItemBlockState(((BlockItem)(itemStack.getItem())).getBlock().defaultBlockState(), itemStack, displayContext, poseStack, multiBufferSource, i, j);
    }
    //? if >=1.21.4 {

    /*@Override
    default ItemStack extractArgument(ItemStack itemStack) {
        return itemStack;
    }

    default MapCodec<SpecialModelRenderer.Unbaked> createUnbakedCodec(){
        return MapCodec.unit(new SpecialModelRenderer.Unbaked(){

            @Override
            public @Nullable SpecialModelRenderer<?> bake(EntityModelSet entityModelSet) {
                return IFactoryBlockEntityWLRenderer.this;
            }

            @Override
            public MapCodec<? extends Unbaked> type() {
                return createUnbakedCodec();
            }
        });
    }

    @Override
    default void render(@Nullable ItemStack object, ItemDisplayContext itemDisplayContext, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, int j, boolean bl) {
        renderByItem(object,itemDisplayContext,poseStack,multiBufferSource,i,j);
    }
    *///?}
}
