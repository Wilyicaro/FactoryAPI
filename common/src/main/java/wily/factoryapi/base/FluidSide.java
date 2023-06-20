package wily.factoryapi.base;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

import java.util.List;
import java.util.Map;

public class FluidSide implements ISideType<FluidSide,IPlatformFluidHandler> {
    public final TransportState transportState;
    public IPlatformFluidHandler fluidHandler;
    public FluidSide(IPlatformFluidHandler fluidHandler, TransportState transportState){
        this.fluidHandler = fluidHandler;
        this.transportState = transportState;
    }
    public void setFluidHandler(IPlatformFluidHandler newFluidHandler){
        this.fluidHandler = newFluidHandler;
    }
    public static CompoundTag serializeTag(Map<Direction,FluidSide> sided) {
        CompoundTag sides = new CompoundTag();
        for (Direction direction : Direction.values())
            sides.putIntArray(direction.getName(), new int[]{sided.get(direction).identifier().differential,sided.get(direction).transportState.ordinal()});
        return sides;
    }

    public static void deserializeNBT(CompoundTag nbt, Map<Direction,FluidSide> sided, List<IPlatformFluidHandler> tanks) {
        for (Direction direction : Direction.values())
            sided.put(direction,  new FluidSide(tanks.get(nbt.getIntArray(direction.getName())[0]), TransportState.byOrdinal(nbt.getIntArray(direction.getName())[1])));
    }

    @Override
    public SlotsIdentifier identifier() {
        return fluidHandler.identifier();
    }

    @Override
    public int nextSlotIndex(List<IPlatformFluidHandler> tanks) {
        int i = identifier().differential + 1;
        int b = i < tanks.size() ? i : 0;
        setFluidHandler(tanks.get(b));
        return b;
    }

    @Override
    public int getSlotIndex(List<IPlatformFluidHandler> identifierList) {
        return identifierList.indexOf(fluidHandler);
    }

    @Override
    public FluidSide ofTransport(TransportState transport) {
        return new FluidSide(fluidHandler,transport);
    }
}
