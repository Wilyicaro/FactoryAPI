package wily.factoryapi.fabric;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import team.reborn.energy.api.EnergyStorage;
import team.reborn.energy.impl.SimpleItemEnergyStorageImpl;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.ItemContainerUtil;
import wily.factoryapi.base.*;
import wily.factoryapi.fabric.base.CraftyEnergyStorage;
import wily.factoryapi.fabric.base.FabricEnergyStoragePlatform;
import wily.factoryapi.fabric.base.FabricFluidStoragePlatform;
import wily.factoryapi.fabric.base.FabricItemStoragePlatform;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FactoryAPIPlatformImpl {
    public static final LoadingCache<IPlatformItemHandler, List<SingleSlotStorage<ItemVariant>>> ITEM_SLOTS_CACHE = CacheBuilder.newBuilder().build(new CacheLoader<>() {
        @Override
        public List<SingleSlotStorage<ItemVariant>> load(IPlatformItemHandler key) {
            List<SingleSlotStorage<ItemVariant>> slots = new ArrayList<>();
            for (int i = 0; i < key.getContainerSize(); i++) {
                int index = i;
                slots.add(new SingleSlotStorage<>() {
                    @Override
                    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
                        ItemStack insertion = resource.toStack((int) maxAmount);
                        transaction.addCloseCallback((t, r) -> {
                            if (r.wasCommitted()) key.insertItem(index, insertion, false);
                        });
                        return insertion.getCount() - key.insertItem(index, insertion, true).getCount();
                    }

                    @Override
                    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
                        if (!resource.matches(key.getItem(index))) return 0;
                        transaction.addCloseCallback((t, r) -> {
                            if (r.wasCommitted()) key.extractItem(index, (int) maxAmount, false);
                        });
                        return key.extractItem(index, (int) maxAmount, true).getCount();
                    }

                    @Override
                    public boolean isResourceBlank() {
                        return getResource().isBlank();
                    }

                    @Override
                    public ItemVariant getResource() {
                        return ItemVariant.of(key.getItem(index));
                    }

                    @Override
                    public long getAmount() {
                        return key.getItem(index).getCount();
                    }

                    @Override
                    public long getCapacity() {
                        return key.getItem(index).getMaxStackSize();
                    }
                });
            }
            return slots;
        }
    });
    public static Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }
    public static IPlatformFluidHandler getItemFluidHandler(ItemStack container) {
        ContainerItemContext context = ItemContainerUtilImpl.modifiableStackContext(container);
        Storage<FluidVariant> handStorage = FluidStorage.ITEM.find(container,context);
        if (handStorage instanceof  IPlatformFluidHandler p) return p;
        if (container.getItem() instanceof IFluidHandlerItem<?> f) return createItemFluidHandler(f,container);
        return handStorage != null ? (FabricFluidStoragePlatform)()-> handStorage : null;
    }

    public static IPlatformFluidHandler createItemFluidHandler(IFluidHandlerItem<?> f, ItemStack container) {
        return new FactoryItemFluidHandler(f.getCapacity(),container,f::isFluidValid,f.getTransport());
    }

    public static IPlatformEnergyStorage getItemEnergyStorage(ItemStack container) {
        ContainerItemContext context = ItemContainerUtilImpl.modifiableStackContext(container);
        EnergyStorage handStorage = EnergyStorage.ITEM.find(container,context);
        if (handStorage instanceof  IPlatformEnergyStorage p) return p;
        if (container.getItem() instanceof IEnergyStorageItem<?>) return getItemEnergyStorage(container,context);
        return handStorage != null ? (FabricEnergyStoragePlatform)()-> handStorage : null;
    }
    public static IPlatformEnergyStorage getItemEnergyStorage(ItemStack container, ContainerItemContext context) {
        return  container.getItem() instanceof IEnergyStorageItem<?> f  ? (FabricEnergyStoragePlatform) ()-> SimpleItemEnergyStorageImpl.createSimpleStorage(context,f.getCapacity(),f.getTransport().canInsert() ? f.getMaxReceive() : 0,f.getTransport().canExtract() ? f.getMaxConsume() : 0) : null;
    }

    public static Component getPlatformEnergyComponent() {
        return Component.literal("Energy (E)").withStyle(ChatFormatting.GOLD);
    }


    public static IFactoryStorage getPlatformFactoryStorage(BlockEntity be) {
        if (be instanceof IFactoryStorage s) return s;
        FactoryAPIPlatform.platformStorageWrappersCache.entrySet().removeIf(e->e.getKey().isRemoved());
        return FactoryAPIPlatform.platformStorageWrappersCache.computeIfAbsent(be, (be1)-> new IFactoryStorage() {
            @Override
            public <T extends IPlatformHandler> ArbitrarySupplier<T> getStorage(Storages.Storage<T> storage, Direction direction) {
                if (be.hasLevel())
                    if (storage == Storages.ITEM) {
                        Storage<ItemVariant> variantStorage = ItemStorage.SIDED.find(be.getLevel(),be.getBlockPos(),be.getBlockState(),be, direction);
                        if (variantStorage instanceof IPlatformItemHandler) return ()->((T) variantStorage);
                        if (variantStorage!= null)
                            return ()->((T)(FabricItemStoragePlatform)()-> variantStorage);
                    } else if (storage == Storages.FLUID) {
                        Storage<FluidVariant> variantStorage = FluidStorage.SIDED.find(be.getLevel(),be.getBlockPos(),be.getBlockState(),be, direction);
                        if (variantStorage instanceof IPlatformFluidHandler) return ()->(T) variantStorage;
                        if (variantStorage!= null)
                            return ()->((T)(FabricFluidStoragePlatform) ()-> variantStorage);
                    }else if (storage == Storages.ENERGY) {
                        EnergyStorage energyStorage = EnergyStorage.SIDED.find(be.getLevel(),be.getBlockPos(),be.getBlockState(),be, direction);
                        if (energyStorage instanceof IPlatformEnergyStorage) return ()->(T) energyStorage;
                        if (energyStorage!= null)
                            return ()->((T)(FabricEnergyStoragePlatform)()-> energyStorage);
                    }
                    else if (storage == Storages.CRAFTY_ENERGY) {
                        ICraftyEnergyStorage energyStorage = CraftyEnergyStorage.SIDED.find(be.getLevel(),be.getBlockPos(),be.getBlockState(),be, direction);
                        if (energyStorage!= null) return ()->((T)energyStorage);
                    }
                return ()-> null;
            }
        });
    }


    public static ICraftyEnergyStorage getItemCraftyEnergyStorage(ItemStack container) {
        ICraftyEnergyStorage craftyStorage = CraftyEnergyStorage.ITEM.find(container,ItemContainerUtilImpl.modifiableStackContext(container));
        if (craftyStorage == null) return getItemCraftyEnergyStorageApi(container);
        return craftyStorage;
    }
    public static ICraftyEnergyStorage getItemCraftyEnergyStorageApi(ItemStack container) {
        return container.getItem() instanceof ICraftyStorageItem f ? new SimpleItemCraftyStorage(container,0,f.getCapacity(), f.getMaxConsume(), f.getMaxReceive(),f.getTransport(),f.getSupportedEnergyTier(), ItemContainerUtil.isBlockItem(container)) : null;
    }
}
