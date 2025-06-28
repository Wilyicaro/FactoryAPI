package wily.factoryapi.base;

//? if fabric {
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
//?} else if forge {
/*import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.IFluidHandler;
*///?} else if neoforge {
/*import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.IFluidTank;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
*///?}
import net.minecraft.nbt.*;
import org.jetbrains.annotations.NotNull;
import wily.factoryapi.util.FluidInstance;


public interface IPlatformFluidHandler extends ITagSerializable<CompoundTag>, IPlatformHandler, IHasIdentifier/*? if forge || neoforge {*//*, IFluidHandler, IFluidTank*//*?} else if fabric {*/, SingleSlotStorage<FluidVariant>/*?}*/ {


    /**
     * Returns the FluidInstance of the handler.
     *
     * <p>
     * <strong>IMPORTANT:</strong> This FluidInstance <em>MUST NOT</em> be modified. This method is not for
     * altering internal contents. Any implementers who are able to detect modification via this method
     * should throw an exception. It is ENTIRELY reasonable and likely that the stack returned here will be a copy.
     * </p>
     *
     * <p>
     * <strong><em>SERIOUSLY: DO NOT MODIFY THE RETURNED FLUIDSTACK</em></strong>
     * </p>
     *
     * @return FluidInstance of the fluid handler. FluidInstance.empty() if the tank is empty.
     */
    @NotNull
    FluidInstance getFluidInstance();

    /**
     * Retrieves the maximum fluid amount of the fluid handler.
     *
     * @return The maximum fluid amount held by the tank.
     */
    int getMaxFluid();

    /**
     * This function is a way to determine which fluids can exist inside a given handler. General purpose tanks will
     * basically always return TRUE for this.
     *
     * @param instance Instance to test with for validity
     * @return TRUE if the tank can hold the FluidInstance, not considering current state.
     * (Basically, is a given fluid EVER allowed in this tank?) Return FALSE if the answer to that question is 'no.'
     */
    boolean isFluidValid(@NotNull FluidInstance instance);

    /**
     * Basically, fills FluidInstance into the fluid handler.
     *
     * @param resource FluidInstance representing the Fluid and maximum amount of fluid to be filled.
     * @param simulate If true, fill will obviously be simulated (will not modify the actual content).
     * @return Amount of resource that was (or would have been, if simulated) filled.
     */
    int fill(FluidInstance resource, boolean simulate);

    /**
     * Basically, drains the desired FluidInstance of the fluid handler.
     *
     * @param resource FluidInstance representing the Fluid and maximum amount of fluid to be drained.
     * @param simulate If true, drain will obviously be simulated (will not modify the actual content).
     * @return FluidInstance representing the Fluid and amount that was (or would have been, if
     * simulated) drained.
     */
    @NotNull
    FluidInstance drain(FluidInstance resource, boolean simulate);

    /**
     * Basically, drains the FluidInstance of the fluid handler.
     * <p>
     *
     * @param maxDrain Maximum amount of fluid to drain.
     * @param simulate If true, drain will obviously be simulated (will not modify the actual content).
     * @return FluidInstance representing the Fluid and amount that was (or would have been, if
     * simulated) drained.
     */
    @NotNull
    FluidInstance drain(int maxDrain, boolean simulate);

    default int getTotalSpace(){
        return Math.max(0, getMaxFluid() - getFluidInstance().getAmount());
    }

    /**
     * Defines the FluidInstance of the fluid handler.
     * <p>
     *
     * @param fluidStack The FluidInstance to replace the actual stored FluidInstance.
     */
    void setFluid(FluidInstance fluidStack);

    /**
     * Defines the capacity of the fluid handler, if supported.
     *
     * @param capacity The capacity to replace the actual maximum fluid amount.
     * @throws UnsupportedOperationException if it isn't implemented in the handler
     */
    default void setCapacity(int capacity){
        throw new UnsupportedOperationException("This Platform Fluid Handler capacity can't be modified!");
    }

    /**
     * Basically, returns the name used for internal operations, as serializing CompoundTag in IFactoryExpandedStorage.
     * <p>
     *
     * @return the name of the used identifier with "Tank" suffix.
     */
    default String getName(){
        return identifier().getName() + "Tank";
    }

    @Override
    default void deserializeTag(CompoundTag tag) {
        setFluid(FluidInstance.fromTag(tag));
        Tag t = tag.tags.get("capacity");
        if (t instanceof NumericTag) {
            if (t instanceof LongTag l) setCapacity(FluidInstance.getMilliBucketsFluidAmount(l./*? if >1.21.4 {*/value/*?} else {*//*getAsLong*//*?}*/()));
            else if (t instanceof IntTag i) setCapacity(i./*? if >1.21.4 {*/value/*?} else {*//*getAsInt*//*?}*/());
        }
    }

    @Override
    default CompoundTag serializeTag() {
        CompoundTag tag = FluidInstance.toTag(getFluidInstance());
        tag.putInt("capacity", getMaxFluid());
        return tag;
    }

    //? if fabric {
    @Override
    default long insert(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        FluidInstance instance = FluidInstance.create(resource.getFluid(),maxAmount);
        transaction.addCloseCallback((t,r)->{
            if (r.wasCommitted())
                fill(instance,false);
        });
        return FluidInstance.getPlatformFluidAmount(fill(instance,true));
    }

    @Override
    default long extract(FluidVariant resource, long maxAmount, TransactionContext transaction) {
        FluidInstance instance = FluidInstance.create(resource.getFluid(),maxAmount);
        transaction.addCloseCallback((t,r)->{
            if (r.wasCommitted())
                drain(instance,false);
        });
        return drain(instance,true).getPlatformAmount();
    }

    @Override
    default boolean isResourceBlank() {
        return getResource().isBlank();
    }

    @Override
    default FluidVariant getResource() {
        return getFluidInstance().toVariant();
    }

    @Override
    default long getAmount() {
        return getFluidInstance().getPlatformAmount();
    }

    @Override
    default long getCapacity() {
        return FluidInstance.getPlatformFluidAmount(getMaxFluid());
    }

    //?} else if forge || neoforge {
    /*@Override
    default @NotNull FluidStack getFluid() {
        return new FluidStack(getFluidInstance().getFluid(),getFluidInstance().getAmount());
    }

    @Override
    default int getFluidAmount() {
        return getFluid().getAmount();
    }

    @Override
    default int getCapacity() {
        return getMaxFluid();
    }

    @Override
    default boolean isFluidValid(FluidStack fluidStack) {
        return isFluidValid(FluidInstance.create(fluidStack.getFluid(),fluidStack.getAmount()));
    }

    @Override
    default int getTanks() {
        return 1;
    }

    @Override
    default @NotNull FluidStack getFluidInTank(int i) {
        return getFluid();
    }

    @Override
    default int getTankCapacity(int i) {
        return getCapacity();
    }

    @Override
    default boolean isFluidValid(int i, @NotNull FluidStack fluidStack) {
        return isFluidValid(fluidStack);
    }

    @Override
    default int fill(FluidStack fluidStack, FluidAction fluidAction) {
        return fill(FluidInstance.create(fluidStack.getFluid(),fluidStack.getAmount()), fluidAction.simulate());
    }

    @Override
    default @NotNull FluidStack drain(FluidStack fluidStack, FluidAction fluidAction) {
        FluidInstance drained = drain(FluidInstance.create(fluidStack.getFluid(),fluidStack.getAmount()),fluidAction.simulate());
        return new FluidStack(drained.getFluid(),drained.getAmount());
    }

    @Override
    default @NotNull FluidStack drain(int i, FluidAction fluidAction) {
        FluidInstance drained = drain(i,fluidAction.simulate());
        return new FluidStack(drained.getFluid(),drained.getAmount());
    }
    *///?}
}