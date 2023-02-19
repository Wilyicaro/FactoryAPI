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
import team.reborn.energy.api.base.SimpleEnergyStorage;
import wily.factoryapi.base.IPlatformEnergyStorage;
import wily.factoryapi.base.IPlatformFluidHandler;
import wily.factoryapi.base.TransportState;

public class FabricEnergyStorage extends SimpleEnergyStorage implements IPlatformEnergyStorage<FabricEnergyStorage> {

    BlockEntity be;

    public TransportState transportState;
    public FabricEnergyStorage(long capacity, BlockEntity be, TransportState transportState) {
        super(capacity, capacity, capacity);
        this.be = be;
        this.transportState = transportState;
    }
    public FabricEnergyStorage(long capacity, BlockEntity be) {
        this(capacity, be, TransportState.EXTRACT_INSERT);
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
    public FabricEnergyStorage getHandler() {
        return this;
    }

    @Override
    public TransportState getTransport() {
        return transportState;
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

    public static FabricEnergyStorage filtered(IPlatformEnergyStorage<FabricEnergyStorage> energyStorage, TransportState transportState){
        return new FabricEnergyStorage(energyStorage.getMaxEnergyStored(), energyStorage.getHandler().be, transportState){
            @Override
            public int getEnergyStored() {
                return energyStorage.getEnergyStored();
            }

            @Override
            public void setEnergyStored(int energy) {
                energyStorage.setEnergyStored(energy);
            }

            @Override
            public void deserializeTag(CompoundTag nbt) {
                energyStorage.deserializeTag(nbt);
            }

            @Override
            public CompoundTag serializeTag() {
                return energyStorage.serializeTag();
            }

            @Override
            public long insert( long maxAmount, TransactionContext transaction) {
                if (!transportState.canInsert()) return 0;
                return(energyStorage.getHandler().insert( maxAmount, transaction));
            }

            @Override
            public long extract( long maxAmount, TransactionContext transaction) {
                if (!transportState.canExtract()) return 0;
                return(energyStorage.getHandler().extract( maxAmount, transaction));
            }

        };
    }
}
