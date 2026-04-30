package wily.factoryapi.base.forge;
//? if forge {
/*import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
//? if <1.21
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
//? if <1.21.6 {
import net.minecraftforge.eventbus.api.SubscribeEvent;
//?}
import net.minecraftforge.fml.common.Mod;
import wily.factoryapi.FactoryAPI;
import wily.factoryapi.base.ICraftyEnergyStorage;

//? if <1.21
@Mod.EventBusSubscriber(modid = FactoryAPI.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class FactoryCapabilities {

    public static Capability<ICraftyEnergyStorage> CRAFTY_ENERGY = CapabilityManager.get(new CapabilityToken<>() {});
    //? if <1.21 {
    @SubscribeEvent
    public static void register(RegisterCapabilitiesEvent event) {
        event.register(ICraftyEnergyStorage.class);
    }
    //?}
}
*///?} else if neoforge {

/*import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;
//? if >=1.21.9 {
/^import net.neoforged.neoforge.transfer.access.ItemAccess;
^///?}
import wily.factoryapi.FactoryAPI;
import wily.factoryapi.base.ICraftyEnergyStorage;

public class FactoryCapabilities {
    public static BlockCapability<ICraftyEnergyStorage, Direction> CRAFTY_ENERGY = BlockCapability.createSided(FactoryAPI.createModLocation("crafty_energy"), ICraftyEnergyStorage.class);
    //? if >=1.21.9 {
    /^public static ItemCapability<ICraftyEnergyStorage, ItemAccess> CRAFTY_ENERGY_ITEM = ItemCapability.create(FactoryAPI.createModLocation("crafty_energy_item"), ICraftyEnergyStorage.class, ItemAccess.class);
    ^///?} else {
    public static ItemCapability<ICraftyEnergyStorage,Void> CRAFTY_ENERGY_ITEM = ItemCapability.createVoid(FactoryAPI.createModLocation("crafty_energy_item"), ICraftyEnergyStorage.class);
    //?}

}
*///?}

