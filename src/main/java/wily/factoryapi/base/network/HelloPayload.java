package wily.factoryapi.base.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import wily.factoryapi.FactoryAPI;
import wily.factoryapi.FactoryAPIClient;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.base.config.FactoryConfig;
import wily.factoryapi.util.ModInfo;

import java.util.Collection;
import java.util.stream.Collectors;

public record HelloPayload(Collection<String> modIds, CommonNetwork.Identifier<HelloPayload> identifier) implements CommonNetwork.Payload {
    public static final CommonNetwork.Identifier<HelloPayload> ID_S2C = CommonNetwork.Identifier.create(FactoryAPI.createModLocation("hello_s2c"),HelloPayload::createS2C);
    public static final CommonNetwork.Identifier<HelloPayload> ID_C2S = CommonNetwork.Identifier.create(FactoryAPI.createModLocation("hello_c2s"),HelloPayload::createC2S);

    public HelloPayload(CommonNetwork.PlayBuf playBuf, CommonNetwork.Identifier<HelloPayload> identifier) {
        this(playBuf.get().readList(FriendlyByteBuf::readUtf), identifier);
    }

    public static HelloPayload createS2C(CommonNetwork.PlayBuf playBuf){
        return new HelloPayload(playBuf, ID_S2C);
    }

    public static HelloPayload createC2S(CommonNetwork.PlayBuf playBuf){
        return new HelloPayload(playBuf, ID_C2S);
    }

    @Override
    public void apply(Context context) {
        if (context.player() instanceof ServerPlayer sp){
            CommonNetwork.ENABLED_PLAYERS.putAll(sp.getUUID(), modIds);
        } else if (context.isClient()) {
            context.executor().execute(()->{
                FactoryAPIClient.handleHelloPayload(this);
                CommonNetwork.sendToServer(new HelloPayload(modIds.stream().filter(FactoryAPI::isModLoaded).collect(Collectors.toSet()), ID_C2S));
            });
        }
    }

    public static void sendInitialPayloads(ServerPlayer serverPlayer){
        CommonNetwork.sendToPlayer(serverPlayer, new HelloPayload(FactoryAPIPlatform.getVisibleModsStream().map(ModInfo::getId).collect(Collectors.toSet()), HelloPayload.ID_S2C), true);
        FactoryConfig.COMMON_STORAGES.values().forEach(handler -> CommonNetwork.sendToPlayer(serverPlayer, CommonConfigSyncPayload.of(CommonConfigSyncPayload.ID_S2C, handler)));
        //? if >=1.21.2 {
        /*CommonNetwork.sendToPlayer(serverPlayer, CommonRecipeManager.ClientPayload.getInstance(), true);
         *///?}
    }

    @Override
    public void encode(CommonNetwork.PlayBuf buf) {
        buf.get().writeCollection(modIds, FriendlyByteBuf::writeUtf);
    }
}