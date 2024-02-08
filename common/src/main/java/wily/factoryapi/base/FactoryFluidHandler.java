package wily.factoryapi.base;

import dev.architectury.fluid.FluidStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import wily.factoryapi.util.FluidStackUtil;

import java.util.function.Predicate;

public class FactoryFluidHandler implements IPlatformFluidHandler{
    long capacity;

    private final BlockEntity be;
    public FluidStack fluid = FluidStack.empty();
    private final Predicate<FluidStack> validator;
    private final SlotsIdentifier differential;

    protected TransportState transportState;

    public FactoryFluidHandler(long capacity, BlockEntity be) {
        this(capacity, be, f -> true, SlotsIdentifier.GENERIC, TransportState.EXTRACT_INSERT);

    }
    public FactoryFluidHandler(long capacity, @Nullable BlockEntity be, Predicate<FluidStack> validator, SlotsIdentifier differential, TransportState transport) {
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
        public @NotNull FluidStack drain(long maxDrain, boolean simulate) {
            return fluidHandler.drain(maxDrain,simulate);
        }

        @Override
        public long fill(FluidStack fluid, boolean simulate) {
            return fluidHandler.fill(fluid,simulate);
        }

        @Override
        public FluidStack getFluidStack() {
            return fluidHandler.getFluidStack();
        }

        @Override
        public void setFluid(FluidStack stack) {
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
    public @NotNull FluidStack getFluidStack() {
        return fluid;
    }

    @Override
    public long getMaxFluid() {
        return capacity;
    }

    @Override
    public void deserializeTag(CompoundTag tag) {
        setFluid(FluidStackUtil.fromTag(tag));
        if (tag.contains("capacity")) setCapacity(tag.getLong("capacity"));
    }

    @Override
    public CompoundTag serializeTag() {
        CompoundTag tag = FluidStackUtil.toTag(getFluidStack());
        tag.putLong("capacity",getMaxFluid());
        return tag;
    }

    @Override
    public boolean isFluidValid(@NotNull FluidStack stack) {
        return validator.test(stack);
    }

    @Override
    public long fill(FluidStack resource, boolean simulate)
    {
        if (resource.isEmpty() || !isFluidValid(resource))
        {
            return 0;
        }
        if (simulate)
        {
            if (fluid.isEmpty())
            {
                return Math.min(capacity, resource.getAmount());
            }
            if (!fluid.isFluidEqual(resource))
            {
                return 0;
            }
            return Math.min(capacity - fluid.getAmount(), resource.getAmount());
        }
        if (fluid.isEmpty())
        {
            fluid = FluidStack.create(resource, Math.min(capacity, resource.getAmount()));
            setChanged();
            return fluid.getAmount();
        }
        if (!fluid.isFluidEqual(resource))
        {
            return 0;
        }
        long filled = capacity - fluid.getAmount();

        if (resource.getAmount() < filled)
        {
            fluid.grow(resource.getAmount());
            filled = resource.getAmount();
        }
        else
        {
            fluid.setAmount(capacity);
        }
        if (filled > 0)
            setChanged();
        return filled;
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, boolean simulate) {
        if (!resource.isFluidEqual(getFluidStack())) return FluidStack.empty();
        return drain(resource.getAmount(),simulate);
    }

    @Override
    public @NotNull FluidStack drain(long maxDrain, boolean simulate) {
        if (!getTransport().canExtract()) return FluidStack.empty();
        long drained = maxDrain;
        if (fluid.getAmount() < drained)
        {
            drained = fluid.getAmount();
        }
        FluidStack stack = FluidStack.create(fluid, drained);
        if (!simulate && drained > 0)
        {
            fluid.shrink(drained);
            setChanged();
        }
        return stack;
    }


    @Override
    public void setFluid(FluidStack fluidStack) {
        this.fluid = fluidStack;
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
    public void setCapacity(long capacity) {
        this.capacity = capacity;
    }
}
