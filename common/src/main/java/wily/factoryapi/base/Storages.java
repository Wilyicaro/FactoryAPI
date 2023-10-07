package wily.factoryapi.base;

import net.minecraft.resources.ResourceLocation;
import wily.factoryapi.FactoryAPI;

import java.util.ArrayList;
import java.util.List;

public class Storages {
    public static final List<Storage<?>> STORAGES = new ArrayList<>();
    public static class Storage<T extends IPlatformHandlerApi<?>>{
        public final ResourceLocation id;
        public Storage(ResourceLocation nameId){
            this.id = nameId;
        }
        public Storage(String name){
            this(new ResourceLocation(FactoryAPI.MOD_ID,name));
            STORAGES.add(this);
        }
    }

    public static final Storage<IPlatformFluidHandler<?>> FLUID = new Storage<>("fluid_storage");
    public static final Storage<IPlatformFluidHandler<?>> FLUID_ITEM = new Storage<>("fluid_item_storage");
    public static final Storage<IPlatformItemHandler<?>> ITEM = new Storage<>("item_storage");
    public static final Storage<IPlatformEnergyStorage<?>> ENERGY = new Storage<>("energy_storage");
    public static final Storage<ICraftyEnergyStorage> CRAFTY_ENERGY = new Storage<>("crafty_energy_storage");


}