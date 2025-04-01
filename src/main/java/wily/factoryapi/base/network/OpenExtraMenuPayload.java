package wily.factoryapi.base.network;

import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import wily.factoryapi.FactoryAPI;
import wily.factoryapi.FactoryAPIClient;
import wily.factoryapi.base.FactoryExtraMenuSupplier;

import java.util.function.Consumer;
import java.util.function.Supplier;

public record OpenExtraMenuPayload(int menuId, MenuType<?> menuType, Component component, CommonNetwork.PlayBuf extra) implements CommonNetwork.Payload {
    public static final CommonNetwork.Identifier<OpenExtraMenuPayload> ID = CommonNetwork.Identifier.create(FactoryAPI.createModLocation("open_extra_menu"), OpenExtraMenuPayload::new);

    public OpenExtraMenuPayload(CommonNetwork.PlayBuf buf){
        this(buf.get().readVarInt(), BuiltInRegistries.MENU.byId(buf.get().readVarInt()), CommonNetwork.decodeComponent(buf), CommonNetwork.decodeBuf(buf));
    }

    public static void openMenuWithPos(ServerPlayer player, MenuProvider provider, BlockPos pos){
        openMenuWithExtra(player, provider, buf-> buf.get().writeBlockPos(pos));
    }

    public static void openMenuWithExtra(ServerPlayer player, MenuProvider provider, Consumer<CommonNetwork.PlayBuf> extraConsumer){
        ((FactoryExtraMenuSupplier.PrepareMenu)player).prepareMenu(provider, menu-> {
            CommonNetwork.PlayBuf playBuf = CommonNetwork.PlayBuf.create();
            extraConsumer.accept(playBuf);
            CommonNetwork.sendToPlayer(player, new OpenExtraMenuPayload(menu.containerId, menu.getType(), provider.getDisplayName(), playBuf));
        });
    }

    @Override
    public void apply(Context context) {
        if (FactoryAPI.isClient()) FactoryAPIClient.handleExtraMenu(context.executor(), context.player(), menuType, this);
    }

    @Override
    public CommonNetwork.Identifier<? extends CommonNetwork.Payload> identifier() {
        return ID;
    }

    @Override
    public void encode(CommonNetwork.PlayBuf buf) {
        buf.get().writeVarInt(menuId);
        buf.get().writeVarInt(BuiltInRegistries.MENU.getId(menuType));
        CommonNetwork.encodeComponent(buf, component);
        extra.get().readerIndex(0);
        CommonNetwork.encodeBuf(buf, extra);
    }
}
