package wily.factoryapi.base;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import wily.factoryapi.base.client.IFactoryItemClientExtension;

import java.util.function.Consumer;

public interface IFactoryItem {
    default <T extends IPlatformHandler> ArbitrarySupplier<T> getStorage(Storages.Storage<T> storage, ItemStack stack){
        return ()->null;
    }
    @Environment(EnvType.CLIENT)
    default ResourceLocation getArmorTexture(){
        return null;
    }
    @Environment(EnvType.CLIENT)
    default void clientExtension(Consumer<IFactoryItemClientExtension> clientExtensionConsumer){
    }
}
