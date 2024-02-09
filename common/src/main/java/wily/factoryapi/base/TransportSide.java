package wily.factoryapi.base;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

import java.util.Collections;
import java.util.List;

public class TransportSide implements IHasIdentifier,IModifiableTransportHandler{
    public TransportState transportState;

    public SlotsIdentifier identifier;
    public TransportSide(SlotsIdentifier identifier, TransportState transportState){
        this.transportState = transportState;
        this.identifier = identifier;
    }
    public TransportSide(TransportState transportState){
        this(SlotsIdentifier.GENERIC,transportState);
    }
    public TransportSide(){
        this(TransportState.NONE);
    }
    public TransportSide withTransport(TransportState state) {
        this.transportState = state;
        return this;
    }
    public static CompoundTag serializeTag(SideList<TransportSide> sided, List<? extends IHasIdentifier> list) {
        CompoundTag sides = new CompoundTag();
        List<SlotsIdentifier> identifiers = IHasIdentifier.getSlotsIdentifiers(list);
        for (Direction direction : Direction.values())
            sides.putIntArray(direction.getName(), new int[]{identifiers.contains(sided.get(direction).identifier()) ? identifiers.indexOf(sided.get(direction).identifier()) : 0, sided.get(direction).getTransport().ordinal()});
        return sides;
    }

    public static void deserializeTag(CompoundTag nbt, SideList<TransportSide> sided, List<? extends IHasIdentifier> list) {
        if (!nbt.isEmpty())
            for (Direction direction : Direction.values()) {
                int[] slotsState = nbt.getIntArray(direction.getName());
                sided.put(direction, new TransportSide(!list.isEmpty() ?  list.get(slotsState[0]).identifier() : sided.get(direction).identifier(), TransportState.byOrdinal(slotsState[slotsState.length - 1])));
            }
    }

    public static CompoundTag serializeTag(SideList<TransportSide> sided) {
        return serializeTag(sided, Collections.emptyList());
    }

    public static void deserializeTag(CompoundTag nbt, SideList<TransportSide> sided) {
       deserializeTag(nbt,sided,Collections.emptyList());
    }
    @Override
    public SlotsIdentifier identifier() {
        return identifier;
    }

    public TransportSide ofTransport(TransportState transport) {
        return new TransportSide(identifier,transport);
    }

    @Override
    public TransportState getTransport() {
        return transportState;
    }

    @Override
    public void setTransport(TransportState state) {
        transportState = state;
    }
    public TransportSide withSlotIdentifier(SlotsIdentifier identifier) {
        this.identifier = identifier;
        return this;
    }
    public int nextSlotIndex(List<? extends IHasIdentifier> identifiers) {
        int i = getSlotIndex(identifiers) + 1;
        int b = i < identifiers.size() ? i : 0;
        identifier = identifiers.get(b).identifier();
        return b;
    }
    public int getSlotIndex(List<? extends IHasIdentifier> identifierList) {
        return Math.max(IHasIdentifier.getSlotsIdentifiers(identifierList).indexOf(identifier()), 0);
    }
}
