package wily.factoryapi.base;

//? if >=1.21.2
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceKey;
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

    //? if <1.21.2 {
    /*default @Nullable String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot) {
        ResourceLocation texture = getArmorLocation(stack,entity,slot);
        return texture == null ? null : texture.toString();
    }
    *///?}

    default @Nullable ResourceLocation getArmorLocation(ItemStack stack, /*? if <1.21.2 {*//*Entity entity, *//*?}*/ EquipmentSlot slot) {
        return /*? if <1.21.2 {*//*null *//*?} else if <1.21.4 {*/ /*stack.get(DataComponents.EQUIPPABLE).model().orElse(null)*//*?} else {*/stack.get(DataComponents.EQUIPPABLE).assetId().map(ResourceKey::location).orElse(null)/*?}*/;
    }

    default void clientExtension(Consumer<IFactoryItemClientExtension> clientExtensionConsumer){
    }
}
