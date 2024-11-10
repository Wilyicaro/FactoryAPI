//? if neoforge {
/*package wily.factoryapi.mixin.neoforge;

import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.ItemCapability;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.base.*;

@Mixin(ItemCapability.class)
public class ItemCapabilityMixin {
    @Inject(method = "getCapability", at = @At("RETURN"), cancellable = true)
    public void getCapability(ItemStack stack, Object context, CallbackInfoReturnable<Object> cir) {
        if (cir.getReturnValue() == null && stack.getItem() instanceof IFactoryItem item){
            FactoryStorage<?> storage = FactoryAPIPlatform.ITEM_CAPABILITY_MAP.get(this);
            ArbitrarySupplier<? extends IPlatformHandler> handler = item.getStorage(storage,stack);
            if (storage != null && handler.isPresent())
                cir.setReturnValue(handler.get());
        }
    }
}
*///?}
