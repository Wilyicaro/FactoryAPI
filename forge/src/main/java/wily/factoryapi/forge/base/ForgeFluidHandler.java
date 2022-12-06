package wily.factoryapi.forge.base;

import dev.architectury.fluid.FluidStack;
import dev.architectury.hooks.fluid.forge.FluidStackHooksForge;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import org.jetbrains.annotations.NotNull;
import wily.factoryapi.base.IPlatformFluidHandler;
import wily.factoryapi.base.SlotsIdentifier;
import wily.factoryapi.base.TransportState;

import java.util.function.Predicate;

public class ForgeFluidHandler extends FluidTank implements IPlatformFluidHandler<IFluidHandler> {
    private final BlockEntity be;
    private final Predicate<FluidStack> validator;
    private final SlotsIdentifier differential;

    protected TransportState transportState;

    public ForgeFluidHandler(long capacity, BlockEntity be) {
        this(capacity, be, f -> true, SlotsIdentifier.GENERIC, TransportState.EXTRACT_INSERT);

    }
    public ForgeFluidHandler(long capacity, BlockEntity be, Predicate<FluidStack> validator, SlotsIdentifier differential, TransportState transport) {
        super((int) capacity);
        this.be = be;
        this.validator = validator;
        this.differential = differential;
        this.transportState = transport;
    }
    public static ForgeFluidHandler filtered(IPlatformFluidHandler<IFluidHandler> fluidHandler, TransportState transport){
        ForgeFluidHandler newFluidHandler = new ForgeFluidHandler(fluidHandler.getMaxFluid(), ((ForgeFluidHandler)fluidHandler).be, (f) -> fluidHandler.isFluidValid(0,f), fluidHandler.identifier(), transport){
            @Override
            public net.minecraftforge.fluids.@NotNull FluidStack drain(int maxDrain, FluidAction action) {
                if (!transport.canExtract()) return net.minecraftforge.fluids.FluidStack.EMPTY;
                return ((ForgeFluidHandler) fluidHandler).drain(maxDrain, action);
            }

            @Override
            public net.minecraftforge.fluids.@NotNull FluidStack drain(net.minecraftforge.fluids.FluidStack resource, FluidAction action) {
                if (!transport.canExtract()) return net.minecraftforge.fluids.FluidStack.EMPTY;
                return ((ForgeFluidHandler) fluidHandler).drain(resource, action);
            }

            @Override
            public int fill(net.minecraftforge.fluids.FluidStack resource, FluidAction action) {
                if (!transport.canInsert()) return 0;
                return ((ForgeFluidHandler) fluidHandler).fill(resource, action);
            }

            @Override
            public FluidTank readFromNBT(CompoundTag nbt) {
                return ((ForgeFluidHandler) fluidHandler).readFromNBT(nbt);
            }

            @Override
            public CompoundTag writeToNBT(CompoundTag nbt) {
                return ((ForgeFluidHandler) fluidHandler).writeToNBT(nbt);
            }

            @Override
            public net.minecraftforge.fluids.@NotNull FluidStack getFluid() {
                return ((ForgeFluidHandler) fluidHandler).getFluid();
            }

            @Override
            public void setFluid(net.minecraftforge.fluids.FluidStack stack) {
                ((ForgeFluidHandler) fluidHandler).setFluid(stack);
            }
        };

        return newFluidHandler;
    }


    @Override
    public @NotNull FluidStack getFluidStack() {
        return FluidStackHooksForge.fromForge(getFluid());
    }

    @Override
    public long getMaxFluid() {
        return getCapacity();
    }

    @Override
    public void deserializeTag(CompoundTag tag) {
        readFromNBT(tag);
    }

    @Override
    public CompoundTag serializeTag() {
        CompoundTag tag = new CompoundTag();
        return writeToNBT(tag);
    }



    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return validator.test(stack);
    }



    @Override
    public long fill(FluidStack resource, boolean simulate) {
        return fill(FluidStackHooksForge.toForge(resource), FluidMultiUtil.FluidActionof(simulate));
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, boolean simulate) {
        return FluidStackHooksForge.fromForge(drain(FluidStackHooksForge.toForge(resource), (FluidMultiUtil.FluidActionof(simulate))));
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, boolean simulate) {
        return drain(FluidStack.create(getFluid().getFluid(), maxDrain), simulate);
    }

    @Override
    public net.minecraftforge.fluids.@NotNull FluidStack drain(int maxDrain, FluidAction action) {
        if (!transportState.canExtract()) return net.minecraftforge.fluids.FluidStack.EMPTY;
        return super.drain(maxDrain, action);
    }


    @Override
    public int fill(net.minecraftforge.fluids.FluidStack resource, FluidAction action) {
        if (!transportState.canInsert()) return 0;
        return super.fill(resource, action);
    }

    @Override
    public void setFluid(FluidStack fluidStack) {
        setFluid(FluidStackHooksForge.toForge(fluidStack));
    }


    @Override
    public SlotsIdentifier identifier() {
        return differential;
    }

    @Override
    public FluidTank getHandler() {
        return this;
    }

    @Override
    public TransportState getTransport() {
        return transportState;
    }
}
