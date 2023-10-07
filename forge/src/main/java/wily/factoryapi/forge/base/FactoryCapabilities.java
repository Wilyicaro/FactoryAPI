package wily.factoryapi.forge.base;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.jetbrains.annotations.Nullable;
import wily.factoryapi.base.CYEnergyStorage;
import wily.factoryapi.base.FactoryCapacityTiers;
import wily.factoryapi.base.ICraftyEnergyStorage;


public class FactoryCapabilities {
    @CapabilityInject(ICraftyEnergyStorage.class)
    public static Capability<ICraftyEnergyStorage> CRAFTY_ENERGY = null;

    @SubscribeEvent
    public static void registerCapabilities(FMLCommonSetupEvent event) {
        CapabilityManager.INSTANCE.register(ICraftyEnergyStorage.class, new Capability.IStorage<ICraftyEnergyStorage>() {
            @Nullable
            @Override
            public Tag writeNBT(Capability<ICraftyEnergyStorage> capability, ICraftyEnergyStorage object, Direction arg) {
                return object.serializeTag();
            }
            @Override
            public void readNBT(Capability<ICraftyEnergyStorage> capability, ICraftyEnergyStorage object, Direction arg, Tag arg2) {
                if (arg2 instanceof CompoundTag)
                    object.deserializeTag((CompoundTag) arg2);
            }
        },()-> new CYEnergyStorage(null,1000, FactoryCapacityTiers.BASIC));
    }
}
