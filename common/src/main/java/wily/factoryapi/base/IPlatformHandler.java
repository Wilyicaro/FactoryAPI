package wily.factoryapi.base;

public interface IPlatformHandler extends ITransportHandler {
    default boolean isRemoved(){
        return false;
    }
    default void setChanged(){
    }
}
