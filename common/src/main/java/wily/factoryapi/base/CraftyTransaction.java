package wily.factoryapi.base;

import dev.architectury.fluid.FluidStack;
import org.jetbrains.annotations.Nullable;

public class CraftyTransaction {
    public final int energy;
    public @Nullable FactoryCapacityTiers tier;
    public CraftyTransaction(int energyTransferred, @Nullable FactoryCapacityTiers energyTier){
        energy = energyTransferred;
        tier = energyTier;
    }
    public int convertEnergyTo(FactoryCapacityTiers tier){
        return tier == null ? 0 : this.tier.convertEnergyTo(energy,tier);
    }
    public CraftyTransaction reduce(float reduction ){
        return new CraftyTransaction(Math.round(energy/ reduction),tier);
    }

    public boolean isEmpty() {return energy== 0 || tier == null;}
    public static final CraftyTransaction EMPTY = new CraftyTransaction(0, null);

}
