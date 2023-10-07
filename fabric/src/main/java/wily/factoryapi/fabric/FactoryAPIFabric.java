package wily.factoryapi.fabric;


import net.fabricmc.api.ModInitializer;
import team.reborn.energy.Energy;
import team.reborn.energy.EnergyStorage;
import wily.factoryapi.FactoryAPI;
import wily.factoryapi.fabric.base.FabricEnergyStorage;
import wily.factoryapi.fabric.base.FabricStorages;

public class FactoryAPIFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        FactoryAPI.init();
        FabricStorages.registerDefaultStorages();
    }
}
