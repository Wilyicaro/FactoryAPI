package wily.factoryapi.base;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import wily.factoryapi.base.client.IFactoryItemClientExtension;

import java.util.function.Consumer;

public interface IFactoryItem {
    default <T extends IPlatformHandler> ArbitrarySupplier<T> getStorage(FactoryStorage<T> storage, ItemStack stack){
        return ()->null;
    }

    default @Nullable String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        ResourceLocation texture = getArmorLocation(stack,entity,slot,type);
        return texture == null ? null : texture.toString();
    }
    default @Nullable ResourceLocation getArmorLocation(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        return null;
    }

    default void clientExtension(Consumer<IFactoryItemClientExtension> clientExtensionConsumer){
    }
}
