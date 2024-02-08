package wily.factoryapi.base;

public interface IModifiableTransportHandler extends ITransportHandler {
    /**
     * Used to set if this handler can insert or/and extract
     */
    void setTransport(TransportState state);
}
