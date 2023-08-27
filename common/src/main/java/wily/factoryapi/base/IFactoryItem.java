package wily.factoryapi.base;

import wily.factoryapi.base.client.IFactoryItemClientExtension;

import java.util.function.Consumer;

public interface IFactoryItem {
    default void clientExtension(Consumer<IFactoryItemClientExtension> clientExtensionConsumer){
    }
}
