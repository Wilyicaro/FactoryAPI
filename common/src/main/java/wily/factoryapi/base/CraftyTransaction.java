package wily.factoryapi.base;

import org.jetbrains.annotations.Nullable;

public class CraftyTransaction {
    public int energy;
    public @Nullable FactoryCapacityTiers tier;
    public CraftyTransaction(int energyTransferred, @Nullable FactoryCapacityTiers energyTier){
        energy = energyTransferred;
        tier = energyTier;
    }
    public int convertEnergyTo(FactoryCapacityTiers tier){
        return tier == null ? 0 : this.tier.convertEnergyTo(energy,tier);
    }
    public CraftyTransaction reduce(double reduction ){
        energy /= reduction;
        return this;
    }
    public boolean isEmpty() {return energy== 0 || tier == null;}
    public static CraftyTransaction EMPTY = new CraftyTransaction(0, null);

}
