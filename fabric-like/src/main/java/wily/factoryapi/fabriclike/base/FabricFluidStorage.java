package wily.factoryapi.fabriclike.base;

import dev.architectury.fluid.FluidStack;
import dev.architectury.hooks.fluid.fabric.FluidStackHooksFabric;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import wily.factoryapi.base.IPlatformFluidHandler;
import wily.factoryapi.base.SlotsIdentifier;
import wily.factoryapi.base.TransportState;

import java.util.function.Predicate;

public class FabricFluidStorage  extends SingleVariantStorage<FluidVariant> implements IPlatformFluidHandler<SingleVariantStorage<FluidVariant> > {


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

    public static FabricFluidStorage filtered(IPlatformFluidHandler<SingleVariantStorage<FluidVariant>> fluidHandler, TransportState transportState){
        FabricFluidStorage newFluidHandler = new FabricFluidStorage(fluidHandler.getMaxFluid(), ((FabricFluidStorage)fluidHandler).be, (f) -> fluidHandler.isFluidValid(0,f), fluidHandler.identifier(), transportState){
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
    public @NotNull FluidStack getFluidStack() {
        return FluidStack.create(variant.getFluid(), getAmount());
    }

    @Override
    public void deserializeTag(CompoundTag tag) {
        variant = FluidVariant.fromNbt(tag.getCompound("fluidVariant"));
        amount = tag.getLong("amount");
    }

    @Override
    public CompoundTag serializeTag() {
        CompoundTag tag = new CompoundTag();
        tag.put("fluidVariant", variant.toNbt());
        tag.putLong("amount", amount);
        return tag;
    }

    @Override
    public long getMaxFluid() {
        return  getCapacity();
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return validator.test(stack);
    }



    @Override
    public long fill(FluidStack resource, boolean simulate) {

        try (Transaction transaction = Transaction.openOuter()) {
            long i;
            if (!simulate) {
                i = (int) insert(FluidStackHooksFabric.toFabric(resource), resource.getAmount(), transaction);
            }else i = (int) simulateInsert(FluidStackHooksFabric.toFabric(resource), resource.getAmount(), transaction);
            transaction.commit();
            return i;

        }
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, boolean simulate) {
        try (Transaction transaction = Transaction.openOuter()) {
            long i;
            if (!simulate) {
                i = (int) extract(FluidStackHooksFabric.toFabric(resource), resource.getAmount(), transaction);
            }else i = (int) simulateExtract(FluidStackHooksFabric.toFabric(resource), resource.getAmount(), transaction);
            transaction.commit();
            return FluidStack.create(resource.getFluid(), i);

        }
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, boolean simulate) {
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
        return isFluidValid(0, FluidStackHooksFabric.fromFabric(variant, FluidStack.bucketAmount()));
    }

    @Override
    protected boolean canExtract(FluidVariant variant) {
        return isFluidValid(0, FluidStackHooksFabric.fromFabric(variant, FluidStack.bucketAmount()));
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
