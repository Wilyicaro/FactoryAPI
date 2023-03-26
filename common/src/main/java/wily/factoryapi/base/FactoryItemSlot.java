package wily.factoryapi.base;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

public class FactoryItemSlot extends Slot implements IHasIdentifier {

    SlotsIdentifier identifier;

    public TransportState transportState;

    public FactoryItemSlot(Container container, SlotsIdentifier identifier, TransportState transport, int i, int j, int k) {
        super(container, i, j, k);
        this.identifier = identifier;
        this.transportState = transport;
    }



    @Override
    public SlotsIdentifier identifier() {
        return identifier;
    }
}
