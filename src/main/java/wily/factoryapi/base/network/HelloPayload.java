package wily.factoryapi.base.network;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import wily.factoryapi.FactoryAPI;
import wily.factoryapi.FactoryAPIClient;

import java.util.function.Supplier;

public class HelloPayload extends CommonNetwork.EmptyPayload {
    public static final CommonNetwork.Identifier<HelloPayload> ID_S2C = CommonNetwork.Identifier.create(FactoryAPI.createModLocation("hello_s2c"),HelloPayload::createS2C);
    public static final CommonNetwork.Identifier<HelloPayload> ID_C2S = CommonNetwork.Identifier.create(FactoryAPI.createModLocation("hello_c2s"),HelloPayload::createC2S);

    public HelloPayload(CommonNetwork.Identifier<HelloPayload> identifier) {
        super(identifier);
    }
    public static HelloPayload createS2C(){
        return new HelloPayload(ID_S2C);
    }
    public static HelloPayload createC2S(){
        return new HelloPayload(ID_C2S);
    }

    @Override
    public void apply(Context context) {
        if (context.player() instanceof ServerPlayer sp){
            CommonNetwork.ENABLED_PLAYERS.add(sp.getUUID());
        } else {
            context.executor().execute(()->{
                FactoryAPIClient.hasModOnServer = true;
                CommonNetwork.sendToServer(createC2S());
            });
        }
    }

}
