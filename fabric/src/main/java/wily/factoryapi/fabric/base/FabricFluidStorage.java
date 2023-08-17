package wily.factoryapi.fabric.base;

import dev.architectury.fluid.FluidStack;
import dev.architectury.hooks.fluid.fabric.FluidStackHooksFabric;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import wily.factoryapi.base.IPlatformFluidHandler;
import wily.factoryapi.base.SlotsIdentifier;
import wily.factoryapi.base.Storages;
import wily.factoryapi.base.TransportState;

import java.util.function.Predicate;

public class FabricFluidStorage  extends SingleVariantStorage<FluidVariant> implements FabricFluidStoragePlatform {


    private final BlockEntity be;
    protected Predicate<FluidStack> validator;
    private final SlotsIdentifier differential;
    protected final TransportState transportState;
    protected final long capacity;

    public FabricFluidStorage(long Capacity, BlockEntity be, TransportState transportState){
        this(Capacity, be, f -> true, SlotsIdentifier.GENERIC, transportState);
    }

    public FabricFluidStorage(long capacity, BlockEntity be, Predicate<FluidStack> validator, SlotsIdentifier differential, TransportState transportState) {
        this.be = be;
        this.validator = validator;
        this.differential = differential;

        this.transportState = transportState;
        this.capacity = capacity;
    }

    public static FabricFluidStorage filtered(IPlatformFluidHandler<FabricFluidStorage> fluidHandler, TransportState transportState){
        FabricFluidStorage newFluidHandler = new FabricFluidStorage(fluidHandler.getMaxFluid(), fluidHandler.getHandler().be, fluidHandler::isFluidValid, fluidHandler.identifier(), transportState){
            @Override
            public @NotNull FluidStack getFluidStack() {
                return fluidHandler.getFluidStack();
            }

            @Override
            public long insert(FluidVariant insertedVariant, long maxAmount, TransactionContext transaction) {
                if (!canInsert(insertedVariant)) return 0;
                return(fluidHandler.getHandler().insert(insertedVariant, maxAmount, transaction));
            }

            @Override
            public long extract(FluidVariant extractedVariant, long maxAmount, TransactionContext transaction) {
                if (!canExtract(extractedVariant)) return 0;
                return(fluidHandler.getHandler().extract(extractedVariant, maxAmount, transaction));
            }

            @Override
            protected boolean canExtract(FluidVariant variant) {
                return super.canExtract(variant) && transportState.canExtract();
            }

            @Override
            protected boolean canInsert(FluidVariant variant) {
                return super.canInsert(variant) && transportState.canInsert();
            }

            @Override
            public void setFluid(FluidStack fluidStack) {
                fluidHandler.setFluid(fluidStack);
            }

        };
        return newFluidHandler;
    }

    @Override
    public long getMaxFluid() {
        return getCapacity();
    }

    @Override
    public boolean isFluidValid(@NotNull FluidStack stack) {
        return validator.test(stack);
    }

    @Override
    public @NotNull FluidStack drain(long maxDrain, boolean simulate) {
        return drain(FluidStackHooksFabric.fromFabric(variant,maxDrain), simulate);
    }

    @Override
    public void setFluid(FluidStack fluidStack) {
        amount = fluidStack .getAmount();
        variant = FluidStackHooksFabric.toFabric(fluidStack);
    }

    @Override
    public SlotsIdentifier identifier() {
        return differential;
    }
    @Override
    protected FluidVariant getBlankVariant() {
        return FluidVariant.blank();
    }

    @Override
    protected long getCapacity(FluidVariant variant) {
        return capacity;
    }

    @Override
    protected void onFinalCommit() {
        be.setChanged();
        super.onFinalCommit();
    }

    @Override
    protected boolean canInsert(FluidVariant variant) {
        return isFluidValid(FluidStackHooksFabric.fromFabric(variant, FluidStack.bucketAmount()));
    }

    @Override
    public SingleVariantStorage<FluidVariant> getHandler() {
        return this;
    }

    @Override
    public TransportState getTransport() {
        return transportState;
    }
}
