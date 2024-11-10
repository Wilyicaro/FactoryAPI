package wily.factoryapi.base;

public interface ITransportHandler {
    /**
     * Used to determine if this handler can insert or/and extract
     */
    TransportState getTransport();
}
