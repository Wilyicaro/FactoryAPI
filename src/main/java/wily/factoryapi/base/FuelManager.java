package wily.factoryapi.base;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
//? fabric {
//? if <1.21.2 {
import net.fabricmc.fabric.api.registry.FuelRegistry;
//?} else {
/*import net.fabricmc.fabric.api.registry.FuelRegistryEvents;
*///?}
//?} else if forge {
/*import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.furnace.FurnaceFuelBurnTimeEvent;
//? if <1.21.6 {
import net.minecraftforge.eventbus.api.EventPriority;
//?}
*///?} else if neoforge {
/*import net.neoforged.neoforge.event.furnace.FurnaceFuelBurnTimeEvent;
import net.neoforged.bus.api.EventPriority;
*///?}
//? if >=1.21.2 {
/*import wily.factoryapi.mixin.base.FuelValuesAccessor;
import net.minecraft.world.level.block.entity.FuelValues;
*///?}
import wily.factoryapi.FactoryAPI;
import wily.factoryapi.FactoryAPIClient;
import wily.factoryapi.FactoryAPIPlatform;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

public class FuelManager {

    public static int getBurnTime(Item item){
        return getBurnTime(item.getDefaultInstance());
    }

    /**
     * The vanilla-registered fuels are used by Fabric, but not by Forge/NeoForge, as it allows itemstack-based fuels.
     * @return Map of the vanilla-registered fuels
     * */
    public static Map<Item, Integer> getMap(){
        //? if <1.21.2 {
        return AbstractFurnaceBlockEntity.getFuel();
        //?} else {
        /*FuelValues fuelValues = getFuelValues();
        return fuelValues == null ? Collections.emptyMap() : ((FuelValuesAccessor)fuelValues).getValues();
        *///?}
    }

    //? if >=1.21.2 {
    /*public static FuelValues getFuelValues(){
        return FactoryAPI.currentServer == null ? FactoryAPIClient.hasLevel() ? FactoryAPIClient.getLevel().fuelValues() : null : FactoryAPI.currentServer.fuelValues();
    }
    *///?}

    public static int getBurnTime(ItemStack stack){
        if (stack.isEmpty()) return 0;
        //? if forge {
        /*//? if <1.21.2 {
        return ForgeHooks.getBurnTime(stack, null);
        //?} else {
        /^FuelValues fuelValues = getFuelValues();
        int ret = stack.getBurnTime(null);
        return ForgeEventFactory.getItemBurnTime(stack, ret == -1 ? fuelValues == null ? 0 : fuelValues.burnDuration(stack) : ret, null);
        ^///?}
        *///?} else if neoforge {
        /*return stack.getBurnTime(null/^? if >1.21.2 {^//^, getFuelValues()^//^?}^/);
        *///?} else {
        //? if <1.21.2 {
        return Objects.requireNonNullElse(FuelRegistry.INSTANCE.get(stack.getItem()), 0);
        //?} else {
        /*FuelValues fuelValues = getFuelValues();
        return fuelValues == null ? 0 : fuelValues.burnDuration(stack);
        *///?}
        //?}
    }

    public static boolean isFuel(Item item){
        return getBurnTime(item) > 0;
    }

    public static boolean isFuel(ItemStack stack){
        return isFuel(stack.getItem());
    }

    public static void add(Item item, int burnTime){
        //? if fabric {
        //? if <1.21.2 {
        FuelRegistry.INSTANCE.add(item, burnTime);
        //?} else {
        /*FuelRegistryEvents.BUILD.register((call, c)->call.add(item, burnTime));
        *///?}
        //?} elif (forge && <1.21.6) || neoforge {
        /*FactoryAPIPlatform.getForgeEventBus().addListener(EventPriority.NORMAL,false, FurnaceFuelBurnTimeEvent.class, e-> {
            if (e.getItemStack().is(item)) e.setBurnTime(burnTime);
        });
        *///?} elif forge {
        /*FurnaceFuelBurnTimeEvent.BUS.addListener(e-> {
            if (e.getItemStack().is(item)) e.setBurnTime(burnTime);
        });
        *///?} else
        /*throw new AssertionError();*/
    }
}
