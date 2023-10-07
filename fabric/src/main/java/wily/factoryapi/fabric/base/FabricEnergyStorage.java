package wily.factoryapi.fabric.base;

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import team.reborn.energy.api.base.SimpleEnergyStorage;
import wily.factoryapi.base.IPlatformEnergyStorage;
import wily.factoryapi.base.TransportState;

public class FabricEnergyStorage extends SimpleEnergyStorage implements FabricEnergyStoragePlatform {

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
            public boolean supportsInsertion() {
                return energyStorage.getTransport().canInsert();
            }

            @Override
            public boolean supportsExtraction() {
                return energyStorage.getTransport().canExtract();
            }

            @Override
            public long insert(long maxAmount, TransactionContext transaction) {
                if (!transportState.canInsert()) return 0;
                return(energyStorage.getHandler().insert( maxAmount, transaction));
            }

            @Override
            public long extract(long maxAmount, TransactionContext transaction) {
                if (!transportState.canExtract()) return 0;
                return(energyStorage.getHandler().extract( maxAmount, transaction));
            }

        };
    }
}
