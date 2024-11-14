package wily.factoryapi.base;

//? if >=1.20.5 {
/*import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
*///?}
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import wily.factoryapi.FactoryAPI;
import wily.factoryapi.ItemContainerPlatform;
import wily.factoryapi.init.FactoryRegistries;
import wily.factoryapi.util.FluidInstance;

import java.util.function.Function;
import java.util.function.Predicate;
//? if <1.20.5
import static net.minecraft.world.item.BlockItem.BLOCK_ENTITY_TAG;

public class FactoryItemFluidHandler extends FactoryFluidHandler {
    private final ItemStack container;

    public FactoryItemFluidHandler(int capacity, ItemStack container) {
        this(capacity, container, f -> true, TransportState.EXTRACT_INSERT);

    }

    public FactoryItemFluidHandler(int capacity, ItemStack stack, Predicate<FluidInstance> validator, TransportState transportState) {
        super(capacity, null, validator, SlotsIdentifier.GENERIC, transportState);
        this.container = stack;
    }

    //? if <1.20.5 {
    
    private CompoundTag getFluidCompound(ItemStack stack){
        return isBlockItem() ? stack.getOrCreateTag().getCompound(BLOCK_ENTITY_TAG).getCompound("singleTank") : stack.getOrCreateTag().getCompound(FactoryAPI.getLoader().isFabric() ? "fluidStorage" : "Fluid");
    }
    //?}
    public String getStorageKey() {
        return isBlockItem() ? "singleTank" : "Fluid";
    }

    public boolean isBlockItem() {
        return ItemContainerPlatform.isBlockItem(container);
    }

    @Override
    public void setFluid(FluidInstance fluid) {
        boolean b = isBlockItem();
        //? if <1.20.5 {
        CompoundTag tag = container.getOrCreateTag();
        if (b) tag = tag.getCompound(BLOCK_ENTITY_TAG);
        int capacity = getMaxFluid();
        CompoundTag newTag = FluidInstance.toTag(fluid);
        newTag.putInt("capacity",capacity);
        tag.put(getStorageKey(), newTag);
        if (b) container.getTag().put(BLOCK_ENTITY_TAG, tag);
        //?} else {
        /*if (b) {
            int capacity = getMaxFluid();
            CompoundTag beTag = container.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY).copyTag();
            CompoundTag tag = FluidInstance.toTag(fluid);
            tag.putInt("capacity", capacity);
            beTag.put(getStorageKey(), tag);
            container.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(beTag));
        } else {
            container.set(FactoryRegistries.FLUID_INSTANCE_COMPONENT.get(), fluid);
        }
        *///?}
    }

    @Override
    public @NotNull FluidInstance getFluidInstance() {
        return /*? if <1.20.5 {*/FluidInstance.fromTag(getFluidCompound(container).getCompound("fluid"))/*?} else {*/ /*isBlockItem() ? getFromComponentOrDefault(container,DataComponents.BLOCK_ENTITY_DATA, CustomData::copyTag, c-> FluidInstance.fromTag(c.getCompound(getStorageKey())),c-> c.contains(getStorageKey()), FluidInstance.empty()) : container.getOrDefault(FactoryRegistries.FLUID_INSTANCE_COMPONENT.get(), FluidInstance.empty())*//*?}*/;
    }

    //? if <1.20.5 {
    @Override
    public void deserializeTag(CompoundTag tag) {
        getFluidCompound(container).put(getStorageKey(),tag);
    }

    @Override
    public CompoundTag serializeTag() {
        return getFluidCompound(container);
    }
    //?} else {
    /*public static <C,D,T> T getFromComponentOrDefault(ItemStack stack, DataComponentType<C> type, Function<C,D> getter, Function<D,T> mapper, Predicate<D> allow, T obj){
        D d;
        return !stack.has(type) || !allow.test(d=getter.apply(stack.get(type))) ? obj : mapper.apply(d);
    }
    *///?}
    @Override
    public int fill(FluidInstance resource, boolean simulate) {
        if (container.getCount() != 1 || resource.isEmpty() || !isFluidValid(resource)){
            return 0;
        }

        FluidInstance contained = this.getFluidInstance();
        if (contained.isEmpty())
        {
            int fillAmount = Math.min(getMaxFluid(), resource.getAmount());

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
                int fillAmount = Math.min(getMaxFluid() - contained.getAmount(), resource.getAmount());

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
        //? if <1.20.5 {
        container.removeTagKey(getStorageKey());
        //?} else {
        /*container.remove(FactoryRegistries.FLUID_INSTANCE_COMPONENT.get());
        container.remove(FactoryRegistries.FLUID_CAPACITY_COMPONENT.get());
        *///?}
    }

    @Override
    public void setCapacity(int capacity) {
        boolean b = isBlockItem();
        //? if <1.20.5 {
        CompoundTag tag = container.getOrCreateTag();
        if (b) tag = tag.getCompound(BLOCK_ENTITY_TAG);
        CompoundTag newTag = getFluidCompound(container);
        newTag.putInt("capacity",capacity);
        tag.put(getStorageKey(), newTag);
        if (b) container.getTag().put(BLOCK_ENTITY_TAG, tag);
        //?} else {
        /*if (b){
            CompoundTag beTag = container.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY).copyTag();
            CompoundTag tag = beTag.getCompound(getStorageKey());
            tag.putInt("capacity",capacity);
            beTag.put(getStorageKey(),tag);
            container.set(DataComponents.BLOCK_ENTITY_DATA, CustomData.of(beTag));
        } else {
            container.set(FactoryRegistries.FLUID_CAPACITY_COMPONENT.get(),capacity);
        }
        *///?}
    }

    @Override
    public int getMaxFluid() {
        return /*? if <1.20.5 {*/ getFluidCompound(container).contains("capacity") ? getFluidCompound(container).getInt("capacity") : super.getMaxFluid() /*?} else {*/ /*isBlockItem() ? getFromComponentOrDefault(container,DataComponents.BLOCK_ENTITY_DATA, CustomData::copyTag, c-> c.getCompound(getStorageKey()).getInt("capacity"),c-> c.contains(getStorageKey()), super.getMaxFluid()) : container.getOrDefault(FactoryRegistries.FLUID_CAPACITY_COMPONENT.get(),super.getMaxFluid()) *//*?}*/;
    }
}
