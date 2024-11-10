//? if fabric {
package wily.factoryapi.base.fabric;

import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.fabricmc.fabric.api.lookup.v1.item.ItemApiLookup;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import wily.factoryapi.FactoryAPI;
import wily.factoryapi.base.ICraftyEnergyStorage;

public final class CraftyEnergyStorage {
    public static final BlockApiLookup<ICraftyEnergyStorage, Direction> SIDED = BlockApiLookup.get(new ResourceLocation(FactoryAPI.MOD_ID,"side_crafty_energy"), ICraftyEnergyStorage.class, Direction.class);

    public static final ItemApiLookup<ICraftyEnergyStorage, ContainerItemContext> ITEM = ItemApiLookup.get(new ResourceLocation(FactoryAPI.MOD_ID,"item_crafty_energy"), ICraftyEnergyStorage.class, ContainerItemContext.class);

}
//?}
