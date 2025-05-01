package wily.factoryapi.base;

import net.minecraft.nbt.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wily.factoryapi.util.FluidInstance;

import java.util.function.Predicate;

public class FactoryFluidHandler implements IPlatformFluidHandler{
    private int capacity;

    private final BlockEntity be;
    public FluidInstance fluid = FluidInstance.empty();
    private final Predicate<FluidInstance> validator;
    private final SlotsIdentifier differential;

    protected TransportState transportState;

    public FactoryFluidHandler(int capacity, BlockEntity be) {
        this(capacity, be, f -> true, SlotsIdentifier.GENERIC, TransportState.EXTRACT_INSERT);

    }
    public FactoryFluidHandler(int capacity, @Nullable BlockEntity be, Predicate<FluidInstance> validator, SlotsIdentifier differential, TransportState transport) {
        this.capacity = capacity;
        this.be = be;
        this.validator = validator;
        this.differential = differential;
        this.transportState = transport;
    }

    @Override
    public boolean isRemoved() {
        return be != null && be.isRemoved();
    }

    public static class SidedWrapper extends FactoryFluidHandler implements IModifiableTransportHandler{
        private final IPlatformFluidHandler fluidHandler;
        public SidedWrapper(IPlatformFluidHandler fluidHandler) {
            super(fluidHandler.getMaxFluid(), ((FactoryFluidHandler)fluidHandler).be, fluidHandler::isFluidValid, fluidHandler.identifier(), fluidHandler.getTransport());
            this.fluidHandler = fluidHandler;
        }

        @Override
        public @NotNull FluidInstance drain(int maxDrain, boolean simulate) {
            return fluidHandler.drain(maxDrain,simulate);
        }

        @Override
        public int fill(FluidInstance fluid, boolean simulate) {
            return fluidHandler.fill(fluid,simulate);
        }

        @Override
        public FluidInstance getFluidInstance() {
            return fluidHandler.getFluidInstance();
        }

        @Override
        public void setFluid(FluidInstance stack) {
            fluidHandler.setFluid(stack);
        }

        @Override
        public void setTransport(TransportState state) {
            transportState = state;
        }
    }

    @Override
    public void setChanged() {
        if (be != null)
            be.setChanged();
    }

    @Override
    public @NotNull FluidInstance getFluidInstance() {
        return fluid;
    }

    @Override
    public int getMaxFluid() {
        return capacity;
    }

    @Override
    public boolean isFluidValid(@NotNull FluidInstance instance) {
        return validator.test(instance);
    }

    @Override
    public int fill(FluidInstance resource, boolean simulate)
    {
        if (resource.isEmpty() || !isFluidValid(resource))
        {
            return 0;
        }
        if (simulate)
        {
            if (fluid.isEmpty())
            {
                return Math.min(getMaxFluid(), resource.getAmount());
            }
            if (!fluid.isFluidEqual(resource))
            {
                return 0;
            }
            return Math.min(getMaxFluid() - fluid.getAmount(), resource.getAmount());
        }
        if (fluid.isEmpty())
        {
            fluid = FluidInstance.create(resource, Math.min(getMaxFluid(), resource.getAmount()));
            setChanged();
            return fluid.getAmount();
        }
        if (!fluid.isFluidEqual(resource))
        {
            return 0;
        }
        int filled = getMaxFluid() - fluid.getAmount();

        if (resource.getAmount() < filled)
        {
            fluid.grow(resource.getAmount());
            filled = resource.getAmount();
        }
        else
        {
            fluid.setAmount(getMaxFluid());
        }
        if (filled > 0)
            setChanged();
        return filled;
    }

    @Override
    public @NotNull FluidInstance drain(FluidInstance resource, boolean simulate) {
        if (!resource.isFluidEqual(getFluidInstance())) return FluidInstance.empty();
        return drain(resource.getAmount(),simulate);
    }

    @Override
    public @NotNull FluidInstance drain(int maxDrain, boolean simulate) {
        if (!getTransport().canExtract()) return FluidInstance.empty();
        int drained = maxDrain;
        if (fluid.getAmount() < drained)
        {
            drained = fluid.getAmount();
        }
        FluidInstance stack = FluidInstance.create(fluid, drained);
        if (!simulate && drained > 0)
        {
            fluid.shrink(drained);
            setChanged();
        }
        return stack;
    }


    @Override
    public void setFluid(FluidInstance FluidInstance) {
        this.fluid = FluidInstance;
    }

    @Override
    public SlotsIdentifier identifier() {
        return differential;
    }

    @Override
    public TransportState getTransport() {
        return transportState;
    }

    @Override
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }
}
