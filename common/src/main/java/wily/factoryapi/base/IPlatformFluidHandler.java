package wily.factoryapi.base;

import dev.architectury.fluid.FluidStack;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;


public interface IPlatformFluidHandler<T> extends ITagSerializable<CompoundTag>,IPlatformHandlerApi<T>, IHasIdentifier
{


    /**
     * Returns the FluidStack in a given tank.
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
     * @return FluidStack in a given tank. FluidStack.EMPTY if the tank is empty.
     */
    @NotNull
    FluidStack getFluidStack();

    /**
     * Retrieves the maximum fluid amount for a given tank.
     *
     * @return     The maximum fluid amount held by the tank.
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
    @Deprecated
    default boolean isFluidValid(int tank, @NotNull FluidStack stack){
     return isFluidValid(stack);
    }

    /**
     * Fills fluid into internal tanks, distribution is left entirely to the IFluidHandler.
     *
     * @param resource FluidStack representing the Fluid and maximum amount of fluid to be filled.
     * @param simulate   If SIMULATE, fill will only be simulated.
     * @return Amount of resource that was (or would have been, if simulated) filled.
     */
    long fill(FluidStack resource, boolean simulate);

    /**
     * Drains fluid out of internal tanks, distribution is left entirely to the IFluidHandler.
     *
     * @param resource FluidStack representing the Fluid and maximum amount of fluid to be drained.
     * @param simulate   If SIMULATE, drain will only be simulated.
     * @return FluidStack representing the Fluid and amount that was (or would have been, if
     * simulated) drained.
     */
    @NotNull
    FluidStack drain(FluidStack resource, boolean simulate);

    /**
     * Drains fluid out of internal tanks, distribution is left entirely to the IFluidHandler.
     * <p>
     * This method is not Fluid-sensitive.
     *
     * @param maxDrain Maximum amount of fluid to drain.
     * @param simulate   If SIMULATE, drain will only be simulated.
     * @return FluidStack representing the Fluid and amount that was (or would have been, if
     * simulated) drained.
     */
    @NotNull
    FluidStack drain(int maxDrain, boolean simulate);

    default long getTotalSpace(){
        return Math.max(0, getMaxFluid() - getFluidStack().getAmount());
    }


    void setFluid(FluidStack fluidStack);

    default String getName(){
        return identifier().getName() + "Tank";
    }
}