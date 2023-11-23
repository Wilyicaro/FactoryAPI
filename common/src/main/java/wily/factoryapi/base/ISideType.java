package wily.factoryapi.base;

import java.util.List;

public interface ISideType<T extends ISideType<T>> extends IHasIdentifier {
    int nextSlotIndex(List<? extends IHasIdentifier> identifierList);

    int getSlotIndex(List<? extends IHasIdentifier> identifierList);
    T ofTransport(TransportState transport);
    TransportState getTransport();

    T withTransport(TransportState transportState);

    T withSlotIdentifier(SlotsIdentifier identifier);
}
