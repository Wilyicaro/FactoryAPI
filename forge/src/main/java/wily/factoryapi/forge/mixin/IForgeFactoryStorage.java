package wily.factoryapi.forge.mixin;

import net.minecraft.core.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import wily.factoryapi.base.IFactoryStorage;
import wily.factoryapi.base.IPlatformHandlerApi;
import wily.factoryapi.base.Storages;
import wily.factoryapi.forge.base.CapabilityUtil;

import java.util.Optional;

@Mixin(IFactoryStorage.class)
public interface IForgeFactoryStorage extends ICapabilityProvider {
    @Override
    default @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction arg) {
        IFactoryStorage be = ((IFactoryStorage) this);
        Storages.Storage<?> storage = CapabilityUtil.capabilityToStorage(capability);
        Optional<? extends IPlatformHandlerApi<?>> handler =  be.getStorage(storage,arg);
        if (storage != null && handler.isPresent())
            return LazyOptional.of(()->handler.get().getHandler()).cast();
        return LazyOptional.empty();
    }
}
