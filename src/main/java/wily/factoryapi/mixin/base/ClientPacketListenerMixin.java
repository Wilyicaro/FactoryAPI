package wily.factoryapi.mixin.base;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import wily.factoryapi.FactoryAPIClient;

@Mixin(ClientPacketListener.class)
public class ClientPacketListenerMixin {
    @Inject(method = "handleLogin", at = @At("RETURN"))
    private void handleLogin(ClientboundLoginPacket clientboundLoginPacket, CallbackInfo ci){
        FactoryAPIClient.PlayerEvent.JOIN_EVENT.invoker.accept(Minecraft.getInstance().player);
    }
    @Inject(method = "close", at = @At("RETURN"))
    private void close(CallbackInfo ci){
        FactoryAPIClient.PlayerEvent.DISCONNECTED_EVENT.invoker.accept(Minecraft.getInstance().player);
    }
}
