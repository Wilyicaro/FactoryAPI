package wily.factoryapi.base;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;

import java.util.List;
import java.util.Map;

public class FluidSide implements ISideType<FluidSide,IPlatformFluidHandler<?>> {
    public TransportState transportState;
    public IPlatformFluidHandler<?> fluidHandler;
    public FluidSide(IPlatformFluidHandler<?> fluidHandler, TransportState transportState){
        this.fluidHandler = fluidHandler;
        this.transportState = transportState;
    }
    public FluidSide(){
        this(null,TransportState.NONE);
    }
    public void setFluidHandler(IPlatformFluidHandler<?> newFluidHandler){
        this.fluidHandler = newFluidHandler;
    }
    public static CompoundTag serializeTag(SideList<FluidSide> sided, List<IPlatformFluidHandler<?>> tanks) {
        CompoundTag sides = new CompoundTag();
        for (Direction direction : Direction.values())
            sides.putIntArray(direction.getName(), new int[]{Math.max(0,tanks.indexOf(sided.get(direction).fluidHandler)),sided.get(direction).transportState.ordinal()});
        return sides;
    }

    public static void deserializeNBT(CompoundTag nbt, SideList<FluidSide> sided, List<IPlatformFluidHandler<?>> tanks) {
        if (!nbt.isEmpty())
            for (Direction direction : Direction.values())
                sided.put(direction,  new FluidSide(tanks.get(nbt.getIntArray(direction.getName())[0]), TransportState.byOrdinal(nbt.getIntArray(direction.getName())[1])));
    }

    @Override
    public SlotsIdentifier identifier() {
        return fluidHandler.identifier();
    }

    @Override
    public int nextSlotIndex(List<IPlatformFluidHandler<?>> tanks) {
        int i = getSlotIndex(tanks) + 1;
        int b = i < tanks.size() ? i : 0;
        setFluidHandler(tanks.get(b));
        return b;
    }

    @Override
    public int getSlotIndex(List<IPlatformFluidHandler<?>> identifierList) {
        return identifierList.indexOf(fluidHandler);
    }

    @Override
    public FluidSide ofTransport(TransportState transport) {
        return new FluidSide(fluidHandler,transport);
    }

    @Override
    public TransportState getTransport() {
        return transportState;
    }

    @Override
    public FluidSide withTransport(TransportState transportState) {
        this.transportState = transportState;
        return this;
    }
}
