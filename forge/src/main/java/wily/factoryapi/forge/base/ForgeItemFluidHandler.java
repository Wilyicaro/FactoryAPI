package wily.factoryapi.forge.base;

import dev.architectury.fluid.FluidStack;
import dev.architectury.hooks.fluid.forge.FluidStackHooksForge;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidHandlerItemStack;
import org.jetbrains.annotations.NotNull;
import wily.factoryapi.ItemContainerUtil;
import wily.factoryapi.base.IPlatformFluidHandler;
import wily.factoryapi.base.SlotsIdentifier;
import wily.factoryapi.base.TransportState;

import java.util.function.Predicate;

import static wily.factoryapi.base.SimpleItemCraftyStorage.BLOCK_ENTITY_TAG;


public class ForgeItemFluidHandler extends FluidHandlerItemStack implements IPlatformFluidHandler<IFluidHandler> {
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
        return ItemContainerUtil.isBlockItem(stack) ?  stack.getOrCreateTag().getCompound(BLOCK_ENTITY_TAG).getCompound("singleTank") :stack.getOrCreateTag().getCompound("Fluid");
    }
    protected void setFluid(net.minecraftforge.fluids.FluidStack fluid) {
        boolean b = (ItemContainerUtil.isBlockItem(container));
        CompoundTag tag = container.getOrCreateTag();
        if (b) tag = tag.getCompound(BLOCK_ENTITY_TAG);
        CompoundTag newTag = new CompoundTag();
        fluid.writeToNBT(newTag);
        tag.put( b ? "singleTank" : FLUID_NBT_KEY, newTag);
        if (b) container.getTag().put(BLOCK_ENTITY_TAG, tag);
    }

    @Override
    protected void setContainerToEmpty() {
        getFluidCompound(container).getAllKeys().clear();
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
        setFluid(net.minecraftforge.fluids.FluidStack.loadFluidStackFromNBT(tag));
        setCapacity(tag.getLong("capacity"));
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
    public boolean isFluidValid(int tank, net.minecraftforge.fluids.@NotNull FluidStack stack) {
        return isFluidValid(FluidStackHooksForge.fromForge(stack));
    }

    @Override
    public long fill(FluidStack resource, boolean simulate) {
        if (!getTransport().canInsert()) return 0;
        return fill(FluidStackHooksForge.toForge(resource), FluidMultiUtil.FluidActionof(simulate));
    }

    @Override
    public int fill(net.minecraftforge.fluids.FluidStack resource, FluidAction doFill) {
        return isFluidValid(0,resource) ? super.fill(resource, doFill) : 0;
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, boolean simulate) {
        if (!getTransport().canExtract()) return FluidStack.empty();
        return FluidStackHooksForge.fromForge(drain(FluidStackHooksForge.toForge(resource), (FluidMultiUtil.FluidActionof(simulate))));
    }

    @Override
    public @NotNull FluidStack drain(long maxDrain, boolean simulate) {
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

    @Override
    public void setCapacity(long capacity) {
        getFluidCompound(container).putInt("capacity", (int) capacity);
    }
}
