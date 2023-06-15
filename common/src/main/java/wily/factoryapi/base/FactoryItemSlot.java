package wily.factoryapi.base;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.Slot;

public class FactoryItemSlot extends Slot implements IHasIdentifier {

    SlotsIdentifier identifier;

    public TransportState transportState;
    private Type type = Type.DEFAULT;

    public enum Type{
        DEFAULT,BIG;
        public int getSizedPos(int xy){
            return this == DEFAULT ? xy : xy - 4;
        }
        public int getOutPos(int xy){
            return getSizedPos(xy) -1;
        }
    }


    public FactoryItemSlot(Container container, SlotsIdentifier identifier, TransportState transport, int i, int j, int k) {
        super(container, i, j, k);
        this.identifier = identifier;
        this.transportState = transport;
    }

    public FactoryItemSlot withType(Type type){
        this.type = type;
        return this;
    }
    public Type getType() {
        return type;
    }

    @Override
    public SlotsIdentifier identifier() {
        return identifier;
    }
}
