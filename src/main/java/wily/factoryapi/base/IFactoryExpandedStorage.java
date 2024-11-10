package wily.factoryapi.base;

import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public interface IFactoryExpandedStorage extends IFactoryStorage {
    default void replaceSidedStorage(BlockSide blockSide,SideList<TransportSide> side, TransportSide replacement){
        if (this instanceof BlockEntity be) side.put(blockSide.blockStateToFacing(be.getBlockState()), replacement);
    }

    default NonNullList<FactoryItemSlot> getSlots(@Nullable Player player){
        return NonNullList.create();
    }

    default List<IPlatformFluidHandler> getTanks(){
        return NonNullList.create();
    }

    default Map<SlotsIdentifier, int[]> itemSlotsIdentifiers() {
        Map<SlotsIdentifier, int[]> map = new LinkedHashMap<>();
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
    default List<SlotsIdentifier> getItemSlotsIdentifiers(){
        return List.copyOf(itemSlotsIdentifiers().keySet());
    }
    default List<SlotsIdentifier> getFluidSlotsIdentifiers() { return IHasIdentifier.getSlotsIdentifiers(getTanks()); }

    default void loadTag(CompoundTag compoundTag) {
        getStorage(FactoryStorage.CRAFTY_ENERGY).ifPresent((e)->e.deserializeTag(compoundTag.getCompound("CYEnergy")));
        getStorage(FactoryStorage.ENERGY).ifPresent((e)->e.deserializeTag(compoundTag.getCompound("Energy")));
        if (!getTanks().isEmpty()) getTanks().forEach((tank)->  tank.deserializeTag(compoundTag.getCompound(tank.getName())));
        getStorage(FactoryStorage.ITEM).ifPresent((e)-> e.deserializeTag(compoundTag.getCompound("inventory")));
        getStorageSides(FactoryStorage.FLUID).ifPresent((f)-> TransportSide.deserializeTag(compoundTag.getCompound("fluidSides"), f, getTanks()));
        getStorageSides(FactoryStorage.ITEM).ifPresent((i)-> TransportSide.deserializeTag(compoundTag.getCompound("itemSides"), i, getItemSlotsIdentifiers()));
        getStorageSides(FactoryStorage.CRAFTY_ENERGY).ifPresent((e)-> TransportSide.deserializeTag(compoundTag.getCompound("energySides"), e));
    }

    default void saveTag(CompoundTag compoundTag) {
        getStorage(FactoryStorage.CRAFTY_ENERGY).ifPresent((e)-> compoundTag.put("CYEnergy", e.serializeTag()));
        getStorage(FactoryStorage.ENERGY).ifPresent((e)-> compoundTag.put("Energy", e.serializeTag()));
        if (!getTanks().isEmpty()) getTanks().forEach((tank)-> compoundTag.put(tank.getName(), tank.serializeTag()));
        getStorage(FactoryStorage.ITEM).ifPresent((i)-> compoundTag.put("inventory", i.serializeTag()));
        getStorageSides(FactoryStorage.FLUID).ifPresent((f)-> compoundTag.put("fluidSides",TransportSide.serializeTag(f, getTanks())));
        getStorageSides(FactoryStorage.ITEM).ifPresent((i)-> compoundTag.put("itemSides", TransportSide.serializeTag(i, getItemSlotsIdentifiers())));
        getStorageSides(FactoryStorage.CRAFTY_ENERGY).ifPresent((e)-> compoundTag.put("energySides", TransportSide.serializeTag(e)));
    }

}
