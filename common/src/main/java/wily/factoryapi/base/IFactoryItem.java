package wily.factoryapi.base;

import java.util.function.Consumer;

public interface IFactoryItem {
    default void clientExtension(Consumer<IFactoryItemClientExtension> clientExtensionConsumer){
    }
}
