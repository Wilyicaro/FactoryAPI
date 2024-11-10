package wily.factoryapi.init;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
import net.minecraft.core.registries.BuiltInRegistries;
import wily.factoryapi.FactoryAPI;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.base.RegisterListing;
import wily.factoryapi.base.network.FactoryAPICommand;

public class FactoryRegistries {
    public static final RegisterListing<ArgumentTypeInfo<?,?>> ARGUMENT_TYPE_INFOS = FactoryAPIPlatform.createRegister(FactoryAPI.MOD_ID, BuiltInRegistries.COMMAND_ARGUMENT_TYPE);
    public static final RegisterListing.Holder<ArgumentTypeInfo<FactoryAPICommand.JsonArgument,?>> jsonArgumentType = ARGUMENT_TYPE_INFOS.add("json_argument_type",()-> SingletonArgumentInfo.contextFree(FactoryAPICommand.JsonArgument::json));

    public static void init() {
        ARGUMENT_TYPE_INFOS.register();
    }
}
