package wily.factoryapi.mixin.base;

import net.minecraft.network.Connection;
import net.minecraft.server.level.ServerPlayer;
//? if >1.20.2 {
import net.minecraft.server.network.CommonListenerCookie;
//?}
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.factoryapi.FactoryEvent;

@Mixin(PlayerList.class)
public class PlayerListMixin {
    @Inject(method = "placeNewPlayer", at = @At("RETURN"))
    public void placeNewPlayer(Connection connection, ServerPlayer serverPlayer,/*? if >1.20.2 {*/ CommonListenerCookie commonListenerCookie, /*?}*/CallbackInfo ci) {
        FactoryEvent.PlayerEvent.JOIN_EVENT.invoker.accept(serverPlayer);
    }
    @Inject(method = "remove", at = @At("RETURN"))
    public void remove(ServerPlayer serverPlayer, CallbackInfo ci) {
        FactoryEvent.PlayerEvent.REMOVED_EVENT.invoker.accept(serverPlayer);
    }
}
