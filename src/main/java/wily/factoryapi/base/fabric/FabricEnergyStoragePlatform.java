//? if fabric {
package wily.factoryapi.base.fabric;

import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.api.base.SimpleEnergyStorage;
import wily.factoryapi.base.IPlatformEnergyStorage;
import wily.factoryapi.base.IPlatformHandlerApi;
import wily.factoryapi.base.TransportState;
import wily.factoryapi.util.CompoundTagUtil;

public interface FabricEnergyStoragePlatform extends IPlatformEnergyStorage, IPlatformHandlerApi<EnergyStorage> {

    String KEY = "energy";

    @Override
    default int receiveEnergy(int energy, boolean simulate) {
        try (Transaction transaction = Transaction.openOuter()) {
            int i;
            try (Transaction nested= transaction.openNested()) {
                i = (int) getHandler().insert(energy, nested);
                if (!simulate) {
                    nested.commit();
                }
            }
            transaction.commit();
            return i;

        }
    }

    @Override
    default int consumeEnergy(int energy, boolean simulate) {
        try (Transaction transaction = Transaction.openOuter()) {
            int i;
            try (Transaction nested= transaction.openNested()) {
                i = (int) getHandler().extract(energy, nested);
                if (!simulate) {
                    nested.commit();
                }
            }
            transaction.commit();
            return i;

        }
    }

    @Override
    default int getEnergyStored() {
        return (int) getHandler().getAmount();
    }

    @Override
    default int getMaxEnergyStored() {
        return (int) getHandler().getCapacity();
    }

    @Override
    default void setEnergyStored(int energy) {
        this.consumeEnergy(getEnergyStored(),false);
        this.receiveEnergy(energy,false);
    }

    @Override
    default int getMaxConsume() {
        return (int) getHandler().getCapacity();
    }


    @Override
    default TransportState getTransport() {
        return TransportState.ofBoolean(getHandler().supportsExtraction(),getHandler().supportsInsertion());
    }

    @Override
    default CompoundTag serializeTag() {
        CompoundTag tag = new CompoundTag();
        tag.putInt(KEY,getEnergyStored());
        return tag;
    }

    @Override
    default void deserializeTag(CompoundTag tag) {
        setEnergyStored(CompoundTagUtil.getInt(tag, KEY).orElse(0));
    }

}
//?}