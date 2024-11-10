//? if neoforge {
/*package wily.factoryapi.mixin.neoforge;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.BlockCapability;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.base.ArbitrarySupplier;
import wily.factoryapi.base.FactoryStorage;
import wily.factoryapi.base.IFactoryStorage;
import wily.factoryapi.base.IPlatformHandler;

@Mixin(BlockCapability.class)
public class BlockCapabilityMixin {
    @Inject(method = "getCapability", at = @At("RETURN"), cancellable = true)
    public void getCapability(Level level, BlockPos pos, @Nullable BlockState state, @Nullable BlockEntity blockEntity, Object context, CallbackInfoReturnable<Object> cir) {
        if (cir.getReturnValue() == null && blockEntity instanceof IFactoryStorage be && context instanceof Direction dir){
            FactoryStorage<?> storage = FactoryAPIPlatform.BLOCK_CAPABILITY_MAP.get(this);
            ArbitrarySupplier<? extends IPlatformHandler> handler = be.getStorage(storage,dir);
            if (storage != null && handler.isPresent())
                cir.setReturnValue(handler.get());
        }
    }
}
*///?}
