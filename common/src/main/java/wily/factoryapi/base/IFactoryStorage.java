package wily.factoryapi.base;

import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public interface IFactoryStorage {







    default <T> void replaceSidedStorage(BlockSide blockSide,Map<Direction, T> side, T replacement){
        if (this instanceof BlockEntity be) side.replace(blockSide.blockStateToFacing(be.getBlockState()), replacement);
    }

    default List<IPlatformFluidHandler> getTanks(){
        return NonNullList.create();
    }

    default <T extends IPlatformHandlerApi> Optional<T> getStorage(Storages.Storage<T> storage){return getStorage(storage, null);}


    <T extends IPlatformHandlerApi> Optional<T> getStorage(Storages.Storage<T> storage, Direction direction);


    default Optional<Map<Direction, FluidSide>> fluidSides(){return Optional.empty();}


    default Optional<Map<Direction, ItemSide>> itemSides(){return Optional.empty();}

    default Optional<Map<Direction, TransportState>> energySides(){return Optional.empty();}

    default NonNullList<FactoryItemSlot> getSlots(@Nullable Player player){
        return NonNullList.create();
    }


    default void transferEnergyTo(Direction d, ICraftyEnergyStorage cy){
        getStorage(Storages.CRAFTY_ENERGY,d).ifPresent((e)-> e.consumeEnergy(cy.receiveEnergy(new CraftyTransaction(e.getMaxConsume(), e.getStoredTier()), false), false));
    }
    default void transferEnergyFrom(Direction d, ICraftyEnergyStorage cy){
        getStorage(Storages.CRAFTY_ENERGY,d).ifPresent((e)-> cy.consumeEnergy(e.receiveEnergy(new CraftyTransaction(cy.getMaxConsume(), cy.getStoredTier()),false), false));
    }

    default Map<SlotsIdentifier, int[]> itemSlotsIdentifiers() {
        Map<SlotsIdentifier, int[]> map = new TreeMap<>(Comparator.comparingInt(SlotsIdentifier::differential));
        for( FactoryItemSlot slot : getSlots(null)){
            int[] list =  map.getOrDefault(slot.identifier(), new int[]{});
            if (ArrayUtils.contains(list,slot.getContainerSlot())) continue;
            list = ArrayUtils.add(list,slot.getContainerSlot());
            map.put(slot.identifier(),list);
        }
        return map;
    }

    default List<Direction> getBlockedSides(){
        return Collections.emptyList();
    }
    default List<SlotsIdentifier> getSlotsIdentifiers(){
        return List.copyOf(itemSlotsIdentifiers().keySet());
    }
    default void loadTag(CompoundTag compoundTag) {
        getStorage(Storages.CRAFTY_ENERGY).ifPresent((e)->e.deserializeTag(compoundTag.getCompound("CYEnergy")));
        if (!getTanks().isEmpty()) getTanks().forEach((tank) ->  tank.deserializeTag(compoundTag.getCompound(tank.getName())));
        getStorage(Storages.ITEM).ifPresent((e) -> e.deserializeTag(compoundTag.getCompound("inventory")));
        fluidSides().ifPresent((f)-> FluidSide.deserializeNBT(compoundTag.getCompound("fluidSides"), f, getTanks()));
        itemSides().ifPresent((i)->  ItemSide.deserializeNBT(compoundTag.getCompound("itemSides"), i,getSlotsIdentifiers()));
        energySides().ifPresent((e)->  TransportState.deserializeNBT(compoundTag.getCompound("energySides"), e));
    }

    default void saveTag(CompoundTag compoundTag) {
        getStorage(Storages.CRAFTY_ENERGY).ifPresent((e)-> compoundTag.put("CYEnergy", e.serializeTag()));
        if (!getTanks().isEmpty()) getTanks().forEach((tank) -> compoundTag.put(tank.getName(), tank.serializeTag()));
        getStorage(Storages.ITEM).ifPresent((i)-> compoundTag.put("inventory", i.serializeTag()));
        fluidSides().ifPresent((f)-> compoundTag.put("fluidSides",FluidSide.serializeTag(f)));
        itemSides().ifPresent((i)-> compoundTag.put("itemSides",ItemSide.serializeTag(i,getSlotsIdentifiers())));
        energySides().ifPresent((e)-> compoundTag.put("energySides", TransportState.serializeTag(e)));
    }

}
