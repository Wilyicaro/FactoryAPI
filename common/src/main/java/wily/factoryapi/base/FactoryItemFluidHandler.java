package wily.factoryapi.base;

import dev.architectury.fluid.FluidStack;
import dev.architectury.platform.Platform;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import wily.factoryapi.ItemContainerUtil;
import wily.factoryapi.util.FluidStackUtil;

import java.util.function.Predicate;

import static net.minecraft.world.item.BlockItem.BLOCK_ENTITY_TAG;

public class FactoryItemFluidHandler extends FactoryFluidHandler {
    private final ItemStack container;
    public FactoryItemFluidHandler(long capacity, ItemStack container) {
        this(capacity, container, f -> true, TransportState.EXTRACT_INSERT);

    }
    public FactoryItemFluidHandler(long capacity, ItemStack stack, Predicate<FluidStack> validator, TransportState transportState) {
        super(capacity, null, validator, SlotsIdentifier.GENERIC,transportState);
        this.container = stack;
    }
    private CompoundTag getFluidCompound(ItemStack stack){
        return ItemContainerUtil.isBlockItem(stack) ?  stack.getOrCreateTag().getCompound(BLOCK_ENTITY_TAG).getCompound("singleTank") : stack.getOrCreateTag().getCompound(Platform.isFabric() ? "fluidStorage" : "Fluid");
    }
    public String getStorageKey(){
        return ItemContainerUtil.isBlockItem(container) ?  "singleTank" : Platform.isFabric() ? "fluidStorage" : "Fluid";
    }
    @Override
    public void setFluid(FluidStack fluid) {
        boolean b = (ItemContainerUtil.isBlockItem(container));
        CompoundTag tag = container.getOrCreateTag();
        if (b) tag = tag.getCompound(BLOCK_ENTITY_TAG);
        CompoundTag newTag = FluidStackUtil.toTag(fluid);
        tag.put(getStorageKey(), newTag);
        if (b) container.getTag().put(BLOCK_ENTITY_TAG, tag);
    }

    @Override
    public @NotNull FluidStack getFluidStack() {
        return FluidStackUtil.fromTag(getFluidCompound(container));
    }

    @Override
    public void deserializeTag(CompoundTag tag) {
        getFluidCompound(container).put(getStorageKey(),tag);
    }

    @Override
    public CompoundTag serializeTag() {
        return getFluidCompound(container);
    }

    @Override
    public long fill(FluidStack resource, boolean simulate) {
        if (container.getCount() != 1 || resource.isEmpty() || !isFluidValid(resource)){
            return 0;
        }

        FluidStack contained = getFluidStack();
        if (contained.isEmpty())
        {
            long fillAmount = Math.min(capacity, resource.getAmount());

            if (!simulate)
            {
                FluidStack filled = resource.copy();
                filled.setAmount(fillAmount);
                setFluid(filled);
            }

            return fillAmount;
        }
        else
        {
            if (contained.isFluidEqual(resource))
            {
                long fillAmount = Math.min(capacity - contained.getAmount(), resource.getAmount());

                if (!simulate && fillAmount > 0) {
                    contained.grow(fillAmount);
                    setFluid(contained);
                }

                return fillAmount;
            }

            return 0;
        }
    }

    @Override
    public @NotNull FluidStack drain(long maxDrain, boolean simulate) {
        if (container.getCount() != 1 || maxDrain <= 0 || !getTransport().canExtract()) return FluidStack.empty();

        FluidStack contained = getFluidStack();
        if (contained.isEmpty())
            return FluidStack.empty();

        final long drainAmount = Math.min(contained.getAmount(), maxDrain);

        FluidStack drained = contained.copy();
        drained.setAmount(drainAmount);

        if (!simulate) {
            contained.shrink(drainAmount);
            if (contained.isEmpty())
                setContainerToEmpty();
            else setFluid(contained);
        }

        return drained;
    }
    protected void setContainerToEmpty() {
        container.removeTagKey(getStorageKey());
    }


    @Override
    public void setCapacity(long capacity) {
        getFluidCompound(container).putInt("capacity", (int) capacity);
    }
}
