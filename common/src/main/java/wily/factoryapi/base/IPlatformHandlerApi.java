package wily.factoryapi.base;

public interface IPlatformHandlerApi<T> {

    /**
     /
     /* *
     *
     * @return must be an instance of respective platform storage handler
     */
    T getHandler();

    /**
     * Used to determine if this handler can insert or/and extract
     */
    TransportState getTransport();
}
