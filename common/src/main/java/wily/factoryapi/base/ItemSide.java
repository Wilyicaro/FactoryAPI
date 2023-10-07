package wily.factoryapi.base;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

import java.util.List;

public class ItemSide implements ISideType<ItemSide, SlotsIdentifier>{
    public TransportState transportState;

    public SlotsIdentifier identifier;


    public ItemSide(SlotsIdentifier identifier, TransportState transportState){
        this.transportState = transportState;
        this.identifier = identifier;
    }

    public ItemSide(){
        this(SlotsIdentifier.GENERIC,TransportState.NONE);
    }

    public static CompoundTag serializeTag(SideList<ItemSide> sided, List<SlotsIdentifier> list) {
        CompoundTag sides = new CompoundTag();
        for (Direction direction : Direction.values())
            sides.putIntArray(direction.getName(), new int[]{list.contains(sided.get(direction).identifier) ? list.indexOf(sided.get(direction).identifier) : 0, sided.get(direction).transportState.ordinal()});
        return sides;
    }

    public static void deserializeNBT(CompoundTag nbt, SideList<ItemSide> sided, List<SlotsIdentifier> list) {
        if (!nbt.isEmpty())
            for (Direction direction : Direction.values()) {
                int[] slotsState = nbt.getIntArray(direction.getName());
                sided.put(direction, new ItemSide(!list.isEmpty() ?  list.get(slotsState[0]) : SlotsIdentifier.GENERIC , TransportState.byOrdinal(slotsState[slotsState.length - 1])));
            }
    }

    @Override
    public SlotsIdentifier identifier() {
        return identifier;
    }

    @Override
    public ItemSide ofTransport(TransportState transport) {
        return new ItemSide(identifier,transport);
    }

    @Override
    public TransportState getTransport() {
        return transportState;
    }

    @Override
    public ItemSide withTransport(TransportState transportState) {
        this.transportState = transportState;
        return this;
    }

    @Override
    public int nextSlotIndex(List<SlotsIdentifier> identifiers) {
        int i = getSlotIndex(identifiers) + 1;
        int b = i < identifiers.size() ? i : 0;
        identifier = (identifiers.get(b));
        return b;

    }

    @Override
    public int getSlotIndex(List<SlotsIdentifier> identifierList) {
        return Math.max(identifierList.indexOf(identifier()), 0);
    }
}
