//? if forge && >=1.21.2 {
/*package wily.factoryapi.mixin.base.forge;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wily.factoryapi.FactoryAPIClient;

import java.util.*;
import java.util.function.Function;

@Mixin(BlockStateModelLoader.class)
public abstract class ModelDiscoveryMixin {

    @Inject(method = "definitionLocationToBlockMapper", at = @At("RETURN"))
    private static void loadBlockStates(CallbackInfoReturnable<Function<ResourceLocation, StateDefinition<Block, BlockState>>> cir, @Local Map<ResourceLocation, StateDefinition<Block, BlockState>> map){
        FactoryAPIClient.extraModels.forEach(r-> map.put(r.id(), Blocks.AIR.getStateDefinition()));
    }
}
*///?}
