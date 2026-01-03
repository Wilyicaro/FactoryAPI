package wily.factoryapi.init;

import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.SingletonArgumentInfo;
//? if >=1.20.5 {
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.util.ExtraCodecs;
//?}
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import wily.factoryapi.FactoryAPI;
import wily.factoryapi.FactoryAPIPlatform;
import wily.factoryapi.base.FactoryCapacityTier;
import wily.factoryapi.base.RegisterListing;
import wily.factoryapi.base.network.FactoryAPICommand;
import wily.factoryapi.util.FactoryItemUtil;
import wily.factoryapi.util.FluidInstance;

import java.util.List;

public class FactoryRegistries {
    public static final RegisterListing<ArgumentTypeInfo<?,?>> ARGUMENT_TYPE_INFOS = FactoryAPIPlatform.createRegister(FactoryAPI.MOD_ID, BuiltInRegistries.COMMAND_ARGUMENT_TYPE);
    public static final RegisterListing.Holder<ArgumentTypeInfo<FactoryAPICommand.JsonArgument,?>> JSON_ARGUMENT_TYPE = ARGUMENT_TYPE_INFOS.add("json_argument_type",()-> SingletonArgumentInfo.contextFree(FactoryAPICommand.JsonArgument::json));

    //? if >=1.20.5 {
    public static final RegisterListing<DataComponentType<?>> DATA_COMPONENT_TYPES = FactoryAPIPlatform.createRegister(FactoryAPI.MOD_ID, BuiltInRegistries.DATA_COMPONENT_TYPE);
    public static final RegisterListing.Holder<DataComponentType<FluidInstance>> FLUID_INSTANCE_COMPONENT  = DATA_COMPONENT_TYPES.add("fluid_instance",()-> DataComponentType.<FluidInstance>builder().persistent(FluidInstance.CODEC).networkSynchronized(FluidInstance.STREAM_CODEC).build());
    public static final RegisterListing.Holder<DataComponentType<Integer>> FLUID_CAPACITY_COMPONENT = DATA_COMPONENT_TYPES.add("fluid_capacity",()-> DataComponentType.<Integer>builder().persistent(ExtraCodecs.POSITIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT).build());
    public static final RegisterListing.Holder<DataComponentType<Integer>> ENERGY_COMPONENT = DATA_COMPONENT_TYPES.add("energy",()-> DataComponentType.<Integer>builder().persistent(ExtraCodecs.POSITIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT).build());
    //public static final RegisterListing.Holder<DataComponentType<Integer>> ENERGY_CAPACITY_COMPONENT = DATA_COMPONENT_TYPES.add("energy_capacity",()-> DataComponentType.<Integer>builder().persistent(ExtraCodecs.POSITIVE_INT).networkSynchronized(ByteBufCodecs.VAR_INT).build());
    public static final RegisterListing.Holder<DataComponentType<FactoryCapacityTier>> ENERGY_TIER_COMPONENT = DATA_COMPONENT_TYPES.add("energy_tier",()-> DataComponentType.<FactoryCapacityTier>builder().persistent(FactoryCapacityTier.CODEC).networkSynchronized(FactoryCapacityTier.STREAM_CODEC).build());
    public static final RegisterListing.Holder<DataComponentType<FactoryCapacityTier>> STORED_ENERGY_TIER_COMPONENT = DATA_COMPONENT_TYPES.add("stored_energy_tier",()-> DataComponentType.<FactoryCapacityTier>builder().persistent(FactoryCapacityTier.CODEC).networkSynchronized(FactoryCapacityTier.STREAM_CODEC).build());

    public static final RegisterListing.Holder<DataComponentType<List<Item>>> ITEM_COMPONENTS_COMPONENT = DATA_COMPONENT_TYPES.add("item_components",()-> DataComponentType.<List<Item>>builder().persistent(FactoryItemUtil.ITEM_COMPONENTS_CODEC).networkSynchronized(FactoryItemUtil.ITEM_COMPONENTS_STREAM_CODEC).build());

    //?}

    public static void init() {
        ARGUMENT_TYPE_INFOS.register();
        //? if >=1.20.5 {
        DATA_COMPONENT_TYPES.register();
        //?}
    }
}
