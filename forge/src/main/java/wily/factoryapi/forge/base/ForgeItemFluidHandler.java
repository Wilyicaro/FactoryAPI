package wily.factoryapi.forge.base;


import me.shedaniel.architectury.fluid.FluidStack;
import me.shedaniel.architectury.utils.Fraction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import org.jetbrains.annotations.NotNull;
import wily.factoryapi.ItemContainerUtil;
import wily.factoryapi.base.*;
import wily.factoryapi.forge.utils.FluidStackUtil;

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

    public @NotNull net.minecraftforge.fluids.FluidStack getFluid() {
        return net.minecraftforge.fluids.FluidStack.loadFluidStackFromNBT(getFluidCompound(container));
    }
    private CompoundTag getFluidCompound(ItemStack stack){
        return ItemContainerUtil.isBlockItem(stack) ?  stack.getOrCreateTag().getCompound("BlockEntityTag").getCompound("singleTank") :stack.getOrCreateTag().getCompound("Fluid");
    }
    protected void setFluid(net.minecraftforge.fluids.FluidStack fluid) {
        boolean b = (ItemContainerUtil.isBlockItem(container));
        CompoundTag tag = container.getOrCreateTag();
        if (b) tag = tag.getCompound("BlockEntityTag");
        CompoundTag newTag = new CompoundTag();
        fluid.writeToNBT(newTag);
        tag.put( b ? "singleTank" : FLUID_NBT_KEY, newTag);
        if (b) container.getTag().put("BlockEntityTag", tag);
    }

    @Override
    protected void setContainerToEmpty() {
        getFluidCompound(container).getAllKeys().clear();
    }

    @Override
    public @NotNull FluidStack getFluidStack() {
        return FluidStackUtil.fromForge(getFluid());
    }

    @Override
    public long getMaxFluid() {
        return capacity;
    }

    @Override
    public void deserializeTag(CompoundTag tag) {
        setFluid(net.minecraftforge.fluids.FluidStack.loadFluidStackFromNBT(tag));
    }

    @Override
    public CompoundTag serializeTag() {
        return getFluidCompound(container);
    }


    @Override
    public boolean isFluidValid(@NotNull FluidStack stack) {
        return validator.test(stack);
    }


    @Override
    public long fill(FluidStack resource, boolean simulate) {
        if (!getTransport().canInsert()) return resource.getAmount().longValue();
        return fill(FluidStackUtil.toForge(resource), FluidStackUtil.FluidActionof(simulate));
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, boolean simulate) {
        if (!getTransport().canExtract()) return FluidStack.empty();
        return FluidStackUtil.fromForge(drain(FluidStackUtil.toForge(resource), (FluidStackUtil.FluidActionof(simulate))));
    }

    @Override
    public @NotNull FluidStack drain(long maxDrain, boolean simulate) {
        return drain(FluidStack.create(getFluid().getFluid(), Fraction.ofWhole(maxDrain)), simulate);
    }

    @Override
    public void setFluid(FluidStack fluidStack) {
        setFluid(FluidStackUtil.toForge(fluidStack));
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
