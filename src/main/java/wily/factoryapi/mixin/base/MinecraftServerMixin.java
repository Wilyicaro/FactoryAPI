package wily.factoryapi.mixin.base;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wily.factoryapi.FactoryEvent;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {
    @Inject(method = "saveEverything", at = @At("RETURN"))
    private void saveEverything(boolean bl, boolean bl2, boolean bl3, CallbackInfoReturnable<Boolean> cir) {
        FactoryEvent.ServerSave.EVENT.invoker.run((MinecraftServer) (Object)this,bl,bl2,bl3);
    }
}
