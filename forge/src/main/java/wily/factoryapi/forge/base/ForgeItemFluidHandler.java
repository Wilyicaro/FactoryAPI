package wily.factoryapi.forge.base;

import dev.architectury.fluid.FluidStack;
import dev.architectury.hooks.fluid.forge.FluidStackHooksForge;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import org.jetbrains.annotations.NotNull;
import wily.factoryapi.base.*;

import java.util.function.Predicate;

public class ForgeItemFluidHandler extends FluidHandlerItemStack implements IPlatformFluidHandler<IFluidHandler>, IStorageItem {
    private final ItemStack container;
    private final Predicate<FluidStack> validator;

    private final TransportState transportState;
    public ForgeItemFluidHandler(long capacity, ItemStack container) {
        this(capacity, container, f -> true, TransportState.EXTRACT_INSERT);

    }
    public ForgeItemFluidHandler(long capacity, ItemStack stack, Predicate<FluidStack> validator, TransportState transportState) {
        super(stack, (int) capacity);
        this.container = stack;
        this.validator = validator;
        this.transportState = transportState;
    }
    public ForgeItemFluidHandler(ItemStack stack, IFluidItem.FluidStorageBuilder builder) {
        this(builder.Capacity(), stack, builder.validator(), builder.transportState());
    }


    @Override
    public @NotNull FluidStack getFluidStack() {
        return FluidStackHooksForge.fromForge(getFluid());
    }

    @Override
    public long getMaxFluid() {
        return capacity;
    }

    @Override
    public void deserializeTag(CompoundTag tag) {
        container.getTag().put("Fluid",tag);
    }

    @Override
    public CompoundTag serializeTag() {
        return container.getTag().getCompound("Fluid");
    }


    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return validator.test(stack);
    }


    @Override
    public long fill(FluidStack resource, boolean simulate) {
        if (!getTransport().canInsert()) return resource.getAmount();
        return fill(FluidStackHooksForge.toForge(resource), FluidMultiUtil.FluidActionof(simulate));
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, boolean simulate) {
        if (!getTransport().canExtract()) return FluidStack.empty();
        return FluidStackHooksForge.fromForge(drain(FluidStackHooksForge.toForge(resource), (FluidMultiUtil.FluidActionof(simulate))));
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, boolean simulate) {
        return drain(FluidStack.create(getFluid().getFluid(), maxDrain), simulate);
    }

    @Override
    public void setFluid(FluidStack fluidStack) {
        setFluid(FluidStackHooksForge.toForge(fluidStack));
    }


    @Override
    public SlotsIdentifier identifier() {
        return SlotsIdentifier.GENERIC;
    }

    @Override
    public IFluidHandler getHandler() {
        return this;
    }

    @Override
    public TransportState getTransport() {
        return transportState;
    }
}
