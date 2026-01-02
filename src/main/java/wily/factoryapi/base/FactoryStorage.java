package wily.factoryapi.base;

import net.minecraft.resources.ResourceLocation;
import wily.factoryapi.FactoryAPI;
import wily.factoryapi.util.ListMap;

public record FactoryStorage<T extends IPlatformHandler>(Class<T> type) {

    public static final ListMap<ResourceLocation, FactoryStorage<?>> STORAGES = new ListMap<>();

    public static <T extends FactoryStorage<?>> T register(ResourceLocation location, T storage){
        STORAGES.put(location,storage);
        return storage;
    }
    public static <T extends FactoryStorage<?>> T registerDefault(String name, T storage){
        return register(FactoryAPI.createModLocation(name),storage);
    }

    public static final FactoryStorage<IPlatformFluidHandler> FLUID = registerDefault("fluid_storage", new FactoryStorage<>(IPlatformFluidHandler.class));
    public static final FactoryStorage<IPlatformItemHandler> ITEM = registerDefault("item_storage",new FactoryStorage<>(IPlatformItemHandler.class));
    public static final FactoryStorage<IPlatformEnergyStorage> ENERGY = registerDefault("energy_storage",new FactoryStorage<>(IPlatformEnergyStorage.class));
    public static final FactoryStorage<ICraftyEnergyStorage> CRAFTY_ENERGY = registerDefault("crafty_energy_storage",new FactoryStorage<>(ICraftyEnergyStorage.class));

}