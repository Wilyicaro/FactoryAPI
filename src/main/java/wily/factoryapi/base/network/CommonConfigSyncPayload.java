package wily.factoryapi.base.network;

import com.mojang.serialization.Dynamic;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import wily.factoryapi.FactoryAPI;
import wily.factoryapi.base.config.FactoryConfig;

import java.util.Map;
import java.util.function.Supplier;

public record CommonConfigSyncPayload(CommonNetwork.Identifier<CommonConfigSyncPayload> identifier, int commonConfigStorage, CompoundTag configTag) implements CommonNetwork.Payload {
    public static final CommonNetwork.Identifier<CommonConfigSyncPayload> ID_S2C = CommonNetwork.Identifier.create(FactoryAPI.createModLocation("common_config_sync_s2c"),CommonConfigSyncPayload::createS2C);
    public static final CommonNetwork.Identifier<CommonConfigSyncPayload> ID_C2S = CommonNetwork.Identifier.create(FactoryAPI.createModLocation("common_config_sync_c2s"),CommonConfigSyncPayload::createC2S);

    public static CommonConfigSyncPayload of(CommonNetwork.Identifier<CommonConfigSyncPayload> identifier, FactoryConfig.StorageHandler handler){
        return new CommonConfigSyncPayload(identifier, FactoryConfig.COMMON_STORAGES.indexOf(handler), (CompoundTag)handler.encodeConfigs(NbtOps.INSTANCE));
    }
    public static CommonConfigSyncPayload of(CommonNetwork.Identifier<CommonConfigSyncPayload> identifier, FactoryConfig.StorageHandler handler, FactoryConfig<?> config){
        return new CommonConfigSyncPayload(identifier, FactoryConfig.COMMON_STORAGES.indexOf(handler), (CompoundTag)FactoryConfig.encodeConfigs(Map.of(config.getKey(), config), NbtOps.INSTANCE));
    }

    public CommonConfigSyncPayload(CommonNetwork.Identifier<CommonConfigSyncPayload> identifier, CommonNetwork.PlayBuf playBuf){
        this(identifier, playBuf.get().readVarInt(), playBuf.get().readNbt());
    }

    public static CommonConfigSyncPayload createS2C(CommonNetwork.PlayBuf playBuf){
        return new CommonConfigSyncPayload(ID_S2C, playBuf);
    }

    public static CommonConfigSyncPayload createC2S(CommonNetwork.PlayBuf playBuf){
        return new CommonConfigSyncPayload(ID_C2S, playBuf);
    }

    @Override
    public void apply(CommonNetwork.SecureExecutor secureExecutor, Supplier<Player> supplier) {
        FactoryConfig.COMMON_STORAGES.getByIndex(commonConfigStorage).decodeConfigs(new Dynamic<>(NbtOps.INSTANCE, configTag));
    }

    @Override
    public void applyServer(Supplier<Player> player) {
        if (player.get() instanceof ServerPlayer sp && sp.hasPermissions(2)) {
            CommonNetwork.Payload.super.applyServer(player);
            FactoryConfig.StorageHandler handler = FactoryConfig.COMMON_STORAGES.getByIndex(commonConfigStorage);
            CommonNetwork.sendToPlayers(sp.server.getPlayerList().getPlayers(), new CommonConfigSyncPayload(ID_S2C, commonConfigStorage, configTag));
            handler.save();
        }
    }

    @Override
    public void encode(CommonNetwork.PlayBuf playBuf) {
        playBuf.get().writeVarInt(commonConfigStorage);
        playBuf.get().writeNbt(configTag);
    }

}
