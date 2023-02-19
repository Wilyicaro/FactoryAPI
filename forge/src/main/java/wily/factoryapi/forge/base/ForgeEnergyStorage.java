package wily.factoryapi.forge.base;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.energy.EnergyStorage;
import org.checkerframework.checker.units.qual.K;
import wily.factoryapi.base.IPlatformEnergyStorage;
import wily.factoryapi.base.TransportState;

public class ForgeEnergyStorage extends EnergyStorage implements IPlatformEnergyStorage<ForgeEnergyStorage> {
    public static final String KEY = "energy";

    BlockEntity be;

    protected TransportState transportState;
    public ForgeEnergyStorage(int capacity, BlockEntity be, TransportState transportState) {
        super(capacity);
        this.be = be;
        this.transportState = transportState;
    }
    public ForgeEnergyStorage(int capacity, BlockEntity be) {
        this(capacity,be,TransportState.EXTRACT_INSERT);
    }
    public void onChanged(){
        be.setChanged();
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) {
        onChanged();
        return super.receiveEnergy(maxReceive, simulate);
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) {
        onChanged();
        return super.extractEnergy(maxExtract, simulate);
    }

    @Override
    public int consumeEnergy(int energy, boolean simulate) {
        return extractEnergy(energy,simulate);
    }

    @Override
    public void setEnergyStored(int energy) {
        this.energy = energy;
    }

    @Override
    public int getMaxConsume() {
        return Math.min(getEnergyStored(),maxExtract);
    }

    @Override
    public ForgeEnergyStorage getHandler() {
        return this;
    }

    @Override
    public TransportState getTransport() {
        return transportState;
    }

    @Override
    public CompoundTag serializeTag() {
        CompoundTag tag = new CompoundTag();
        tag.put(KEY, serializeNBT());
        return tag;
    }

    @Override
    public void deserializeTag(CompoundTag nbt) {
        deserializeNBT(nbt.get(KEY) instanceof IntTag ? nbt.get(KEY) : IntTag.valueOf(0));
    }
    public static ForgeEnergyStorage filtered(IPlatformEnergyStorage<ForgeEnergyStorage> energyStorage, TransportState transportState){
        return new ForgeEnergyStorage(energyStorage.getMaxEnergyStored(), energyStorage.getHandler().be, transportState){
            @Override
            public int getEnergyStored() {
                return energyStorage.getEnergyStored();
            }

            @Override
            public void setEnergyStored(int energy) {
                energyStorage.setEnergyStored(energy);
            }

            @Override
            public Tag serializeNBT() {
                return energyStorage.getHandler().serializeNBT();
            }

            @Override
            public void deserializeNBT(Tag nbt) {
                energyStorage.getHandler().deserializeNBT(nbt);
            }

            @Override
            public int receiveEnergy(int maxReceive, boolean simulate) {
                if (!transportState.canInsert()) return 0;
                return energyStorage.receiveEnergy(maxReceive, simulate);
            }

            @Override
            public int extractEnergy(int maxExtract, boolean simulate) {
                if (!transportState.canExtract()) return 0;
                return energyStorage.consumeEnergy(maxExtract, simulate);
            }

        };
    }
}
