package wily.factoryapi.mixin.base;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wily.factoryapi.FactoryAPI;
import wily.factoryapi.FactoryEvent;
import wily.factoryapi.base.MinecraftServerAccessor;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements MinecraftServerAccessor {
    @Inject(method = "saveEverything", at = @At("RETURN"))
    private void saveEverything(boolean bl, boolean bl2, boolean bl3, CallbackInfoReturnable<Boolean> cir) {
        FactoryEvent.ServerSave.EVENT.invoker.run((MinecraftServer) (Object)this,bl,bl2,bl3);
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void init(CallbackInfo ci){
        FactoryAPI.currentServer = (MinecraftServer) (Object) this;
    }

    @Accessor
    public abstract LevelStorageSource.LevelStorageAccess getStorageSource();
}
