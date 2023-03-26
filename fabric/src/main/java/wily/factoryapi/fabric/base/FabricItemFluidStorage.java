package wily.factoryapi.fabric.base;

import dev.architectury.fluid.FluidStack;
import dev.architectury.hooks.fluid.fabric.FluidStackHooksFabric;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantItemStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import wily.factoryapi.base.*;


import java.util.function.Predicate;

public class FabricItemFluidStorage extends SingleVariantItemStorage<FluidVariant> implements IPlatformFluidHandler<Storage<FluidVariant>>, IStorageItem {



    private final long Capacity;
    ContainerItemContext context;
    protected Predicate<FluidStack> validator;

    protected TransportState transportState;
    public FabricItemFluidStorage(ContainerItemContext c, long Capacity){
        this(c, Capacity, f -> true, TransportState.EXTRACT_INSERT);
    }
    public FabricItemFluidStorage(ContainerItemContext c, IFluidItem.FluidStorageBuilder builder){
        this(c,builder.Capacity(), builder.validator(),builder.transportState());
    }
public FabricItemFluidStorage(ContainerItemContext c,long Capacity, Predicate<FluidStack> validator, TransportState transportState){
    super(c);
    context = c;
    this.Capacity = Capacity;
    this.validator = validator;
    this.transportState = transportState;
}


    @Override
    protected FluidVariant getBlankResource() {
        return FluidVariant.blank();
    }

    @Override
    protected FluidVariant getResource(ItemVariant currentVariant) {
        return FluidVariant.fromNbt(getFluidCompound(currentVariant.toStack()).getCompound("fluidVariant"));
    }

    @Override
    protected long getAmount(ItemVariant currentVariant) {
        return getFluidCompound(currentVariant.toStack()).getInt("amount");
    }

    @Override
    protected long getCapacity(FluidVariant variant) {
        return getMaxFluid();
    }

    @Override
    protected ItemVariant getUpdatedVariant(ItemVariant currentVariant, FluidVariant newResource, long newAmount) {
        ItemStack stack = currentVariant.toStack();
        stack.setTag(getUpdatedTag(FluidStack.create(newResource.getFluid(),newAmount)));

        return ItemVariant.of(stack);
    }

    private CompoundTag getUpdatedTag(FluidStack newStack){
        ItemStack stack = context.getItemVariant().toStack();
        CompoundTag tag = stack.getOrCreateTag();
        CompoundTag newTag = new CompoundTag();
        newTag.put("fluidVariant",FluidStackHooksFabric.toFabric(newStack).toNbt());
        newTag.putLong("amount", newStack.getAmount());
        tag.put("fluidStorage", newTag);
        return tag;
    }


    private CompoundTag getFluidCompound(ItemStack stack){
        return stack.getOrCreateTag().getCompound("fluidStorage");
    }



    @Override
    public @NotNull FluidStack getFluidStack() {
        return FluidStack.create(getResource(context.getItemVariant()).getFluid(), getAmount());
    }

    @Override
    public void deserializeTag(CompoundTag tag) {
    }

    @Override
    public CompoundTag serializeTag() {
        CompoundTag tag = new CompoundTag();
        return tag;
    }

    @Override
    public long getMaxFluid() {
        return Capacity;
    }

    @Override
    public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
        return validator.test(stack);
    }

    @Override
    protected boolean canInsert(FluidVariant variant) {
        return isFluidValid(0, FluidStackHooksFabric.fromFabric(variant, FluidStack.bucketAmount())) && getTransport().canInsert();
    }

    @Override
    protected boolean canExtract(FluidVariant variant) {
        return isFluidValid(0, FluidStackHooksFabric.fromFabric(variant, FluidStack.bucketAmount())) && getTransport().canExtract();
    }

    @Override
    public long fill(FluidStack resource, boolean simulate) {
        try (Transaction transaction = Transaction.openOuter()) {
            long i;
            if (!simulate) {
                i = insert(FluidStackHooksFabric.toFabric(resource), resource.getAmount(), transaction);
            }else i =  simulateInsert(FluidStackHooksFabric.toFabric(resource), resource.getAmount(), transaction);
            transaction.commit();
            return i;

        }
    }

    @Override
    public @NotNull FluidStack drain(FluidStack resource, boolean simulate) {
        try (Transaction transaction = Transaction.openOuter()) {
            long i;
            if (!simulate) {
                i =  extract(FluidStackHooksFabric.toFabric(resource), resource.getAmount(), transaction);
            }else i = simulateInsert(FluidStackHooksFabric.toFabric(resource), resource.getAmount(), transaction);
            transaction.commit();
            return FluidStack.create(resource.getFluid(), i);

        }
    }

    @Override
    public long insert(FluidVariant insertedResource, long maxAmount, TransactionContext transaction) {
        return super.insert(insertedResource, maxAmount, transaction);
    }

    @Override
    public @NotNull FluidStack drain(int maxDrain, boolean simulate) {
        return drain(FluidStackHooksFabric.fromFabric(getResource(),maxDrain), simulate);
    }

    @Override
    public void setFluid(FluidStack fluidStack) {
        try (Transaction transaction = Transaction.openOuter()) {
            context.exchange(getUpdatedVariant(context.getItemVariant(), FluidStackHooksFabric.toFabric(fluidStack), fluidStack.getAmount()), 1, transaction);
        }
    }


    @Override
    public SlotsIdentifier identifier() {
        return SlotsIdentifier.GENERIC;
    }


    @Override
    public ItemStack getContainer() {
        return context.getItemVariant().toStack();
    }

    @Override
    public Storage<FluidVariant> getHandler() {
        return this;
    }

    @Override
    public TransportState getTransport() {
        return transportState;
    }
}
