package wily.factoryapi.fabric.mixin;

import net.fabricmc.fabric.api.transfer.v1.item.InventoryStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StoragePreconditions;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import org.jetbrains.annotations.UnmodifiableView;
import org.spongepowered.asm.mixin.Mixin;
import wily.factoryapi.base.IPlatformHandler;
import wily.factoryapi.base.IPlatformItemHandler;
import wily.factoryapi.fabric.FactoryAPIPlatformImpl;

import java.util.Iterator;
import java.util.List;

@Mixin(IPlatformItemHandler.class)
public interface IPlatformItemHandlerMixin extends InventoryStorage,IPlatformItemHandler {

    @Override
    default @UnmodifiableView List<SingleSlotStorage<ItemVariant>> getSlots() {
        FactoryAPIPlatformImpl.ITEM_SLOTS_CACHE.asMap().keySet().removeIf(IPlatformHandler::isRemoved);
        List<SingleSlotStorage<ItemVariant>> slots;
        while ((slots = FactoryAPIPlatformImpl.ITEM_SLOTS_CACHE.getUnchecked(this)).size() != getContainerSize())
            FactoryAPIPlatformImpl.ITEM_SLOTS_CACHE.invalidate(this);
        return slots;
    }

    @Override
    default long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notNegative(maxAmount);
        long amount = 0;
        for (SingleSlotStorage<ItemVariant> part : getSlots()) {
            amount += part.insert(resource, maxAmount - amount, transaction);
            if (amount == maxAmount) break;
        }
        return amount;
    }

    @Override
    default long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        StoragePreconditions.notNegative(maxAmount);
        long amount = 0;

        for (SingleSlotStorage<ItemVariant> part : getSlots()) {
            amount += part.extract(resource, maxAmount - amount, transaction);
            if (amount == maxAmount) break;
        }

        return amount;
    }

    @Override
    default boolean supportsInsertion() {
        return getTransport().canInsert();
    }

    @Override
    default boolean supportsExtraction() {
        return getTransport().canExtract();
    }

    @Override
    default Iterator<StorageView<ItemVariant>> iterator() {
        return getSlots().stream().map(s-> (StorageView<ItemVariant>) s).iterator();
    }
}
