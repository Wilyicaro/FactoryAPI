package wily.factoryapi.base;

import java.util.List;

public interface ISideType<T extends ISideType<?,?>,B> extends IHasIdentifier {
    int nextSlotIndex(List<B> identifierList);

    int getSlotIndex(List<B> identifierList);
    T ofTransport(TransportState transport);
    TransportState getTransport();

    T withTransport(TransportState transportState);
}
