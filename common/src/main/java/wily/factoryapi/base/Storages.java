package wily.factoryapi.base;

import net.minecraft.resources.ResourceLocation;

public class Storages {
public static class Storage<T>{
    public final ResourceLocation id;
    public Storage(ResourceLocation nameId){
        this.id = nameId;
    }
    public Storage(String name){
        this.id = new ResourceLocation(name);
    }
}

    public static Storage<IPlatformFluidHandler> FLUID = new Storage<>("fluid_storage");
    public static Storage<IPlatformFluidHandler> FLUID_ITEM = new Storage<>("fluid_item_storage");
    public static Storage<IPlatformEnergyStorage> ENERGY = new Storage<>("energy_storage");
    public static Storage<IPlatformEnergyStorage> ENERGY_ITEM = new Storage<>("energy_item_storage");
    public static Storage<ICraftyEnergyStorage> CRAFTY_ENERGY = new Storage<>("crafty_energy_storage");
    public static Storage<ICraftyEnergyStorage> CRAFTY_ENERGY_ITEM = new Storage<>("crafty_energy_item_storage");
    public static Storage<IPlatformItemHandler> ITEM = new Storage<>("item_storage");




}