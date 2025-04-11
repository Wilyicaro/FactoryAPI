package wily.factoryapi.base;

import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public class FactoryItemSlot extends Slot implements IHasIdentifier {
    SlotsIdentifier identifier;

    public TransportState transportState;
    private Type type = Type.DEFAULT;
    protected Predicate<FactoryItemSlot> active = (s)-> true;


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
    public FactoryItemSlot(IFactoryStorage storage, SlotsIdentifier identifier, TransportState transport, int i, int j, int k) {
        this((Container) storage.getStorage(FactoryStorage.ITEM).orObject(new SimpleContainer()), identifier, transport, i, j, k);
    }

    public FactoryItemSlot withType(Type type){
        this.type = type;
        return this;
    }

    public int getCustomX(){
        return x;
    }

    public int getCustomY(){
        return y;
    }

    public Type getType() {
        return type;
    }

    @Override
    public boolean mayPlace(ItemStack itemStack) {
        return isActive();
    }
    @Override
    public boolean isActive() {
        return active.test(this);
    }

    @Override
    public SlotsIdentifier identifier() {
        return identifier;
    }
}
