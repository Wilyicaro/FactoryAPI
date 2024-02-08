package wily.factoryapi.forge.mixin;

import net.minecraftforge.energy.IEnergyStorage;
import org.spongepowered.asm.mixin.Mixin;
import wily.factoryapi.base.IPlatformEnergyStorage;

@Mixin(IPlatformEnergyStorage.class)
public interface IPlatformEnergyStorageMixin extends IEnergyStorage, IPlatformEnergyStorage {


    @Override
    default int extractEnergy(int i, boolean bl) {
        return consumeEnergy(i,bl);
    }

    @Override
    default boolean canExtract() {
        return getTransport().canExtract();
    }

    @Override
    default boolean canReceive() {
        return getTransport().canInsert();
    }
}
