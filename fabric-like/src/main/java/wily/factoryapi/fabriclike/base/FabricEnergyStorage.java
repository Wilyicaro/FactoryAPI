package wily.factoryapi.fabriclike.base;

import dev.architectury.fluid.FluidStack;
import dev.architectury.hooks.fluid.fabric.FluidStackHooksFabric;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import team.reborn.energy.api.base.SimpleEnergyStorage;
import wily.factoryapi.base.IPlatformEnergyStorage;
import wily.factoryapi.base.TransportState;

public class FabricEnergyStorage extends SimpleEnergyStorage implements IPlatformEnergyStorage {

    BlockEntity be;
    public FabricEnergyStorage(long capacity, BlockEntity be) {
        super(capacity, capacity, capacity);
        this.be = be;
    }
    public static final String KEY = "energy";

    @Override
    public int receiveEnergy(int energy, boolean simulate) {
        try (Transaction transaction = Transaction.openOuter()) {
            int i;
            try (Transaction nested= transaction.openNested()) {
                i = (int) insert(energy, nested);
                if (!simulate) {
                    nested.commit();
                }
            }
            transaction.commit();
            return i;

        }
    }

    @Override
    public int consumeEnergy(int energy, boolean simulate) {
        try (Transaction transaction = Transaction.openOuter()) {
            int i;
            try (Transaction nested= transaction.openNested()) {
                i = (int) extract(energy, nested);
                if (!simulate) {
                    nested.commit();
                }
            }
            transaction.commit();
            return i;

        }
    }

    @Override
    public int getEnergyStored() {
        return (int) getAmount();
    }

    @Override
    public int getMaxEnergyStored() {
        return (int) getCapacity();
    }

    @Override
    public void setEnergyStored(int energy) {
        this.amount = energy;
    }

    @Override
    public int getMaxConsume() {
        return (int) maxExtract;
    }

    @Override
    public Object getHandler() {
        return this;
    }

    @Override
    public TransportState getTransport() {
        return TransportState.INSERT;
    }

    @Override
    public CompoundTag serializeTag() {
        CompoundTag tag = new CompoundTag();
        tag.putInt(KEY,getEnergyStored());
        return tag;
    }

    @Override
    public void deserializeTag(CompoundTag nbt) {
        setEnergyStored(nbt.getInt(KEY));
    }

    @Override
    protected void onFinalCommit() {
        be.setChanged();
        super.onFinalCommit();
    }
}
