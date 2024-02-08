package wily.factoryapi.base;

import dev.architectury.fluid.FluidStack;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;


public interface IPlatformFluidHandler extends ITagSerializable<CompoundTag>, IPlatformHandler, IHasIdentifier
{


    /**
     * Returns the FluidStack of the handler.
     *
     * <p>
     * <strong>IMPORTANT:</strong> This FluidStack <em>MUST NOT</em> be modified. This method is not for
     * altering internal contents. Any implementers who are able to detect modification via this method
     * should throw an exception. It is ENTIRELY reasonable and likely that the stack returned here will be a copy.
     * </p>
     *
     * <p>
     * <strong><em>SERIOUSLY: DO NOT MODIFY THE RETURNED FLUIDSTACK</em></strong>
     * </p>
     *
     * @return FluidStack of the fluid handler. FluidStack.empty() if the tank is empty.
     */
    @NotNull
    FluidStack getFluidStack();

    /**
     * Retrieves the maximum fluid amount of the fluid handler.
     *
     * @return The maximum fluid amount held by the tank.
     */
    long getMaxFluid();

    /**
     * This function is a way to determine which fluids can exist inside a given handler. General purpose tanks will
     * basically always return TRUE for this.
     *
     * @param stack Stack to test with for validity
     * @return TRUE if the tank can hold the FluidStack, not considering current state.
     * (Basically, is a given fluid EVER allowed in this tank?) Return FALSE if the answer to that question is 'no.'
     */
    boolean isFluidValid(@NotNull FluidStack stack);

    /**
     * Basically, fills FluidStack into the fluid handler.
     *
     * @param resource FluidStack representing the Fluid and maximum amount of fluid to be filled.
     * @param simulate If true, fill will obviously be simulated (will not modify the actual content).
     * @return Amount of resource that was (or would have been, if simulated) filled.
     */
    long fill(FluidStack resource, boolean simulate);

    /**
     * Basically, drains the desired FluidStack of the fluid handler.
     *
     * @param resource FluidStack representing the Fluid and maximum amount of fluid to be drained.
     * @param simulate If true, drain will obviously be simulated (will not modify the actual content).
     * @return FluidStack representing the Fluid and amount that was (or would have been, if
     * simulated) drained.
     */
    @NotNull
    FluidStack drain(FluidStack resource, boolean simulate);

    /**
     * Basically, drains the FluidStack of the fluid handler.
     * <p>
     *
     * @param maxDrain Maximum amount of fluid to drain.
     * @param simulate If true, drain will obviously be simulated (will not modify the actual content).
     * @return FluidStack representing the Fluid and amount that was (or would have been, if
     * simulated) drained.
     */
    @NotNull
    FluidStack drain(long maxDrain, boolean simulate);

    default long getTotalSpace(){
        return Math.max(0, getMaxFluid() - getFluidStack().getAmount());
    }

    /**
     * Defines the FluidStack of the fluid handler.
     * <p>
     *
     * @param fluidStack The FluidStack to replace the actual stored FluidStack.
     */
    void setFluid(FluidStack fluidStack);

    /**
     * Defines the capacity of the fluid handler, if supported.
     * <p>
     * This method is not Fluid-sensitive.
     *
     * @param capacity The capacity to replace the actual maximum fluid amount.
     * @throws UnsupportedOperationException if it isn't implemented in the handler
     */
    default void setCapacity(long capacity){
        throw new UnsupportedOperationException("This Platform Fluid Handler capacity can't be modified!");
    }

    /**
     * Basically, returns the name used for internal operations, as serializing CompoundTag.
     * <p>
     *
     * @return the name of the used identifier with "Tank" suffix.
     */
    default String getName(){
        return identifier().getName() + "Tank";
    }
}