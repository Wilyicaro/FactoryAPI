package wily.factoryapi.fabric;


import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderingRegistry;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import wily.factoryapi.FactoryAPI;
import wily.factoryapi.base.IFactoryItem;

public class FactoryAPIFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        Registry.ITEM.stream().filter(i-> i instanceof IFactoryItem).forEach(i-> ((IFactoryItem) i).clientExtension((c)-> {
            if (c.getArmorTexture() != null)
                ArmorRenderingRegistry.registerTexture((entity,  stack, slot, secondLayer, suffix, defaultTexture)->c.getArmorTexture(),i);
            ArmorRenderingRegistry.registerModel((entity,  stack, slot, defaultModel)-> (HumanoidModel<LivingEntity>) c.getHumanoidArmorModel(entity,stack,slot,defaultModel),i);
        }));
    }
}
