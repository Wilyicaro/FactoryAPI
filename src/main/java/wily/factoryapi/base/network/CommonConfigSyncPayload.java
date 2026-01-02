package wily.factoryapi.base.network;

import com.mojang.serialization.Dynamic;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
//? if >=1.21.11 {
import net.minecraft.server.permissions.Permission;
import net.minecraft.server.permissions.PermissionLevel;
//?}
import net.minecraft.world.entity.player.Player;
import wily.factoryapi.FactoryAPI;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.base.config.FactoryConfig;

import java.util.Map;
import java.util.function.Supplier;

public record CommonConfigSyncPayload(CommonNetwork.Identifier<CommonConfigSyncPayload> identifier, Identifier commonConfigStorage, CompoundTag configTag) implements CommonNetwork.Payload {
    public static final CommonNetwork.Identifier<CommonConfigSyncPayload> ID_S2C = CommonNetwork.Identifier.create(FactoryAPI.createModLocation("common_config_sync_s2c"),CommonConfigSyncPayload::createS2C);
    public static final CommonNetwork.Identifier<CommonConfigSyncPayload> ID_C2S = CommonNetwork.Identifier.create(FactoryAPI.createModLocation("common_config_sync_c2s"),CommonConfigSyncPayload::createC2S);

    public static CommonConfigSyncPayload of(CommonNetwork.Identifier<CommonConfigSyncPayload> identifier, FactoryConfig.StorageHandler handler){
        return new CommonConfigSyncPayload(identifier, FactoryConfig.COMMON_STORAGES.getKey(handler), (CompoundTag)handler.encodeConfigs(NbtOps.INSTANCE));
    }
    public static CommonConfigSyncPayload of(CommonNetwork.Identifier<CommonConfigSyncPayload> identifier, FactoryConfig.StorageHandler handler, FactoryConfig<?> config){
        return new CommonConfigSyncPayload(identifier, FactoryConfig.COMMON_STORAGES.getKey(handler), (CompoundTag)FactoryConfig.encodeConfigs(Map.of(config.getKey(), config), NbtOps.INSTANCE));
    }

    public CommonConfigSyncPayload(CommonNetwork.Identifier<CommonConfigSyncPayload> identifier, CommonNetwork.PlayBuf playBuf){
        this(identifier, playBuf.get().readIdentifier(), playBuf.get().readNbt());
    }

    public static CommonConfigSyncPayload createS2C(CommonNetwork.PlayBuf playBuf){
        return new CommonConfigSyncPayload(ID_S2C, playBuf);
    }

    public static CommonConfigSyncPayload createC2S(CommonNetwork.PlayBuf playBuf){
        return new CommonConfigSyncPayload(ID_C2S, playBuf);
    }

    @Override
    public void apply(Context context) {
        FactoryConfig.COMMON_STORAGES.get(commonConfigStorage).decodeConfigs(new Dynamic<>(NbtOps.INSTANCE, configTag));
        if (!context.isClient() && context.player() instanceof ServerPlayer sp && sp/*? if >=1.21.11 {*/.permissions()/*?}*/.hasPermission(/*? if >=1.21.11 {*/new Permission.HasCommandLevel(PermissionLevel.GAMEMASTERS)/*?} else {*//*2*//*?}*/)){
            FactoryConfig.StorageHandler handler = FactoryConfig.COMMON_STORAGES.get(commonConfigStorage);
            CommonNetwork.sendToPlayers(FactoryAPIPlatform.getEntityServer(sp).getPlayerList().getPlayers(), new CommonConfigSyncPayload(ID_S2C, commonConfigStorage, configTag));
            handler.save();
        }
    }

    @Override
    public void encode(CommonNetwork.PlayBuf playBuf) {
        playBuf.get().writeIdentifier(commonConfigStorage);
        playBuf.get().writeNbt(configTag);
    }

}
