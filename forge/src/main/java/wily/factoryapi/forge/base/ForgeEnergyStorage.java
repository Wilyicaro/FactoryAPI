package wily.factoryapi.forge.base;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.energy.EnergyStorage;
import org.checkerframework.checker.units.qual.K;
import wily.factoryapi.base.IPlatformEnergyStorage;
import wily.factoryapi.base.TransportState;

public class ForgeEnergyStorage extends EnergyStorage implements IPlatformEnergyStorage {
    public static final String KEY = "energy";

    BlockEntity be;
    public ForgeEnergyStorage(int capacity, BlockEntity be) {
        super(capacity);
        this.be = be;
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
    public Object getHandler() {
        return this;
    }

    @Override
    public TransportState getTransport() {
        return TransportState.EXTRACT_INSERT;
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

}
