package wily.factoryapi.base;


import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import wily.factoryapi.FactoryAPI;
import wily.factoryapi.ItemContainerPlatform;
import wily.factoryapi.util.FluidInstance;

import java.util.function.Predicate;

import static net.minecraft.world.item.BlockItem.BLOCK_ENTITY_TAG;

public class FactoryItemFluidHandler extends FactoryFluidHandler {
    private final ItemStack container;
    public FactoryItemFluidHandler(int capacity, ItemStack container) {
        this(capacity, container, f -> true, TransportState.EXTRACT_INSERT);

    }
    public FactoryItemFluidHandler(int capacity, ItemStack stack, Predicate<FluidInstance> validator, TransportState transportState) {
        super(capacity, null, validator, SlotsIdentifier.GENERIC,transportState);
        this.container = stack;
    }
    private CompoundTag getFluidCompound(ItemStack stack){
        return ItemContainerPlatform.isBlockItem(stack) ?  stack.getOrCreateTag().getCompound(BLOCK_ENTITY_TAG).getCompound("singleTank") : stack.getOrCreateTag().getCompound(FactoryAPI.getLoader().isFabric() ? "fluidStorage" : "Fluid");
    }
    public String getStorageKey(){
        return ItemContainerPlatform.isBlockItem(container) ?  "singleTank" : FactoryAPI.getLoader().isFabric() ? "fluidStorage" : "Fluid";
    }
    @Override
    public void setFluid(FluidInstance fluid) {
        boolean b = (ItemContainerPlatform.isBlockItem(container));
        CompoundTag tag = container.getOrCreateTag();
        if (b) tag = tag.getCompound(BLOCK_ENTITY_TAG);
        CompoundTag newTag = FluidInstance.toTag(fluid);
        tag.put(getStorageKey(), newTag);
        if (b) container.getTag().put(BLOCK_ENTITY_TAG, tag);
    }

    @Override
    public @NotNull FluidInstance getFluidInstance() {
        return FluidInstance.fromTag(getFluidCompound(container));
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
    public int fill(FluidInstance resource, boolean simulate) {
        if (container.getCount() != 1 || resource.isEmpty() || !isFluidValid(resource)){
            return 0;
        }

        FluidInstance contained = this.getFluidInstance();
        if (contained.isEmpty())
        {
            int fillAmount = Math.min(capacity, resource.getAmount());

            if (!simulate)
            {
                FluidInstance filled = resource.copy();
                filled.setAmount(fillAmount);
                setFluid(filled);
            }

            return fillAmount;
        }
        else
        {
            if (contained.isFluidEqual(resource))
            {
                int fillAmount = Math.min(capacity - contained.getAmount(), resource.getAmount());

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
    public @NotNull FluidInstance drain(int maxDrain, boolean simulate) {
        if (container.getCount() != 1 || maxDrain <= 0 || !getTransport().canExtract()) return FluidInstance.empty();

        FluidInstance contained = this.getFluidInstance();
        if (contained.isEmpty())
            return FluidInstance.empty();

        final int drainAmount = Math.min(contained.getAmount(), maxDrain);

        FluidInstance drained = contained.copy();
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
    public void setCapacity(int capacity) {
        getFluidCompound(container).putInt("capacity", capacity);
    }
}
