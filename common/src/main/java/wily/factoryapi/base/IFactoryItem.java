package wily.factoryapi.base;

import net.minecraft.world.item.ItemStack;
import wily.factoryapi.base.client.IFactoryItemClientExtension;

import java.util.function.Consumer;

public interface IFactoryItem {
    default <T extends IPlatformHandler> ArbitrarySupplier<T> getStorage(Storages.Storage<T> storage, ItemStack stack){
        return ()->null;
    }
    default void clientExtension(Consumer<IFactoryItemClientExtension> clientExtensionConsumer){
    }
}
