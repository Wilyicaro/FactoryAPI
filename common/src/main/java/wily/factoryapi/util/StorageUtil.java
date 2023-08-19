package wily.factoryapi.util;

import dev.architectury.fluid.FluidStack;
import net.minecraft.core.Direction;
import wily.factoryapi.base.*;

import java.util.function.Function;

public class StorageUtil {
    public static void transferEnergyTo(IFactoryStorage fromStorage, Direction d, ICraftyEnergyStorage energyReceiver){
        transferEnergyTo(fromStorage,c->c,d,energyReceiver);
    }
    public static void transferEnergyFrom(IFactoryStorage fromStorage, Direction d, ICraftyEnergyStorage energyConsumer){
       transferEnergyFrom(fromStorage,c->c,d,energyConsumer);
    }
    public static void transferEnergyTo(IFactoryStorage fromStorage, Function<CraftyTransaction,CraftyTransaction> transaction, Direction d, ICraftyEnergyStorage energyReceiver){
        fromStorage.getStorage(Storages.CRAFTY_ENERGY,d).ifPresent((e)-> e.consumeEnergy(energyReceiver.receiveEnergy(transaction.apply(new CraftyTransaction(e.getMaxConsume(), e.getStoredTier())), false), false));
    }
    public static void transferEnergyFrom(IFactoryStorage fromStorage, Function<CraftyTransaction,CraftyTransaction> transaction, Direction d, ICraftyEnergyStorage energyConsumer){
        fromStorage.getStorage(Storages.CRAFTY_ENERGY,d).ifPresent((e)-> energyConsumer.consumeEnergy(e.receiveEnergy(transaction.apply(new CraftyTransaction(energyConsumer.getMaxConsume(), energyConsumer.getStoredTier())),false), false));
    }
    public static void transferFluidTo(IFactoryStorage fromStorage, Direction d, IPlatformFluidHandler<?> fluidFiller){
        transferFluidTo(fromStorage,c->c,d,fluidFiller);
    }
    public static void transferFluidFrom(IFactoryStorage toStorage, Direction d, IPlatformFluidHandler<?> fluidFiller){
        transferFluidFrom(toStorage,c->c,d,fluidFiller);
    }
    public static void transferFluidTo(IFactoryStorage fromStorage, Function<FluidStack,FluidStack> transaction, Direction d, IPlatformFluidHandler<?> fluidFiller){
        fromStorage.getStorage(Storages.FLUID,d).ifPresent((e)-> e.drain(fluidFiller.fill(transaction.apply(e.getFluidStack()), false), false));
    }
    public static void transferFluidFrom(IFactoryStorage toStorage, Function<FluidStack,FluidStack> transaction, Direction d, IPlatformFluidHandler<?> fluidDrainer){
        toStorage.getStorage(Storages.FLUID,d).ifPresent((e)-> fluidDrainer.drain(e.fill(transaction.apply(fluidDrainer.getFluidStack()),false), false));
    }
}
