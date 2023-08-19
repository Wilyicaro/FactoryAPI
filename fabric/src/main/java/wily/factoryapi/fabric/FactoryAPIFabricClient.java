package wily.factoryapi.fabric;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import wily.factoryapi.base.IFactoryItem;

public class FactoryAPIFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        BuiltInRegistries.ITEM.stream().filter(i-> i instanceof IFactoryItem).forEach(i-> ((IFactoryItem) i).clientExtension((c)-> {
            if (c.getArmorTexture() != null)
                ArmorRenderer.register((matrices,vertexConsumers,stack,entity,slot,light,contextModel)-> c.getHumanoidArmorModel(entity,stack,slot,contextModel).renderToBuffer(matrices,vertexConsumers.getBuffer(RenderType.entityCutout(c.getArmorTexture())), light, OverlayTexture.NO_OVERLAY,1.0F,1.0F,1.0F, 1.0F),i);
        }));
    }
}
