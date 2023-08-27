package wily.factoryapi.util;

import dev.architectury.fluid.FluidStack;
import net.minecraft.core.Direction;
import wily.factoryapi.base.*;

import java.util.function.Function;

public class StorageUtil {
    public static void transferEnergyTo(IFactoryStorage fromStorage, Direction d, ICraftyEnergyStorage energyReceiver){
        transferEnergyTo(fromStorage,c->c,c->c,d,energyReceiver);
    }
    public static void transferEnergyFrom(IFactoryStorage fromStorage, Direction d, ICraftyEnergyStorage energyConsumer){
       transferEnergyFrom(fromStorage,c->c,c->c,d,energyConsumer);
    }
    public static void transferEnergyTo(IFactoryStorage fromStorage, Function<CraftyTransaction,CraftyTransaction> transferFunction,Function<CraftyTransaction,CraftyTransaction> consumeFunction, Direction d, ICraftyEnergyStorage energyReceiver){
        fromStorage.getStorage(Storages.CRAFTY_ENERGY,d).ifPresent((e)-> {
            CraftyTransaction transaction1 = e.consumeEnergy(transferFunction.apply(new CraftyTransaction(e.getMaxConsume(), e.getStoredTier())), true);
            e.consumeEnergy(consumeFunction.apply(energyReceiver.receiveEnergy(transaction1, false)),false);
        });
    }
    public static void transferEnergyFrom(IFactoryStorage fromStorage, Function<CraftyTransaction,CraftyTransaction> transferFunction,Function<CraftyTransaction,CraftyTransaction> consumeFunction, Direction d, ICraftyEnergyStorage energyConsumer){
        fromStorage.getStorage(Storages.CRAFTY_ENERGY,d).ifPresent((e)-> {
            CraftyTransaction transaction = energyConsumer.consumeEnergy(transferFunction.apply(new CraftyTransaction(energyConsumer.getMaxConsume(), energyConsumer.getStoredTier())), true);
            energyConsumer.consumeEnergy(consumeFunction.apply(e.receiveEnergy(transaction,false)),false);
        });
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
