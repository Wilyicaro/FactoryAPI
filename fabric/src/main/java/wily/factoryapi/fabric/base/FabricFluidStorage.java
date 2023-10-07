package wily.factoryapi.fabric.base;

import me.shedaniel.architectury.fluid.FluidStack;
import me.shedaniel.architectury.utils.Fraction;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.base.IPlatformFluidHandler;
import wily.factoryapi.base.SlotsIdentifier;
import wily.factoryapi.base.TransportState;
import wily.factoryapi.fabric.util.FluidStackUtil;

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
        FabricFluidStorage newFluidHandler = new FabricFluidStorage(fluidHandler.getMaxFluid(), ((FabricFluidStorage)fluidHandler).be, fluidHandler::isFluidValid, fluidHandler.identifier(), transportState){
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
        return FluidStack.create(variant.getFluid(), Fraction.ofWhole(getAmount()));
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
    public boolean isFluidValid(@NotNull FluidStack stack) {
        return validator.test(stack);
    }



    @Override
    public long fill(FluidStack resource, boolean simulate) {

        try (Transaction transaction = Transaction.openOuter()) {
            long i;
            if (!simulate) {
                i = (int) insert(FluidStackUtil.toFabric(resource), resource.getAmount().longValue(), transaction);
            }else i = (int) simulateInsert(FluidStackUtil.toFabric(resource), resource.getAmount().longValue(), transaction);
            transaction.commit();
            return i;

        }
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, boolean simulate) {
        try (Transaction transaction = Transaction.openOuter()) {
            long i;
            if (!simulate) {
                i = (int) extract(FluidStackUtil.toFabric(resource), resource.getAmount().longValue(), transaction);
            }else i = (int) simulateExtract(FluidStackUtil.toFabric(resource), resource.getAmount().longValue(), transaction);
            transaction.commit();
            return FluidStack.create(resource.getFluid(), Fraction.ofWhole(i));

        }
    }

    @Override
    public @NotNull FluidStack drain(long maxDrain, boolean simulate) {
        return drain(FluidStackUtil.fromFabric(variant,maxDrain), simulate);
    }

    @Override
    public void setFluid(FluidStack fluidStack) {
        amount = fluidStack.getAmount().longValue();
        variant = FluidStackUtil.toFabric(fluidStack);
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
        return isFluidValid(FluidStackUtil.fromFabric(variant, FactoryAPIPlatform.getBucketAmount()));
    }

    @Override
    protected boolean canExtract(FluidVariant variant) {
        return isFluidValid(FluidStackUtil.fromFabric(variant, FactoryAPIPlatform.getBucketAmount()));
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
