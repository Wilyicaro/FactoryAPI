package wily.factoryapi.mixin.base;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.color.block.BlockColors;
import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wily.factoryapi.FactoryAPIClient;

import java.util.*;
import java.util.function.Function;

@Mixin(/*? if <1.21 {*//*ModelBakery*//*?} else if <1.21.5 {*//*BlockStateModelLoader*//*?} else {*/BlockStateDefinitions/*?}*/.class)
public abstract class AddExtraModelsMixin {
    //? if <1.21 {
    /*@Shadow protected abstract void loadTopLevel(ModelResourceLocation par1);

    @WrapWithCondition(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V", ordinal = 0))
    private boolean init(ProfilerFiller instance, String string){
        FactoryAPIClient.extraModels.forEach((r, id)->loadTopLevel(BlockModelShaper.stateToModelLocation(r, id.blockState())));
        return true;
    }
    *///?} else if <1.21.2 {
    /*@Shadow protected abstract void loadBlockStateDefinitions(ResourceLocation par1, StateDefinition<Block, BlockState> par2);

    @Inject(method = "loadAllBlockStates", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/profiling/ProfilerFiller;popPush(Ljava/lang/String;)V"))
    private void definitionLocationToBlockMapper(CallbackInfo ci){
        FactoryAPIClient.extraModels.forEach((r, id)-> loadBlockStateDefinitions(r, id.stateDefinition()));
    }
    *///?} else if <1.21.5 {
    /*@Inject(method = "definitionLocationToBlockMapper", at = @At("RETURN"))
    private static void definitionLocationToBlockMapper(CallbackInfoReturnable<Function<ResourceLocation, StateDefinition<Block, BlockState>>> cir, @Local Map<ResourceLocation, StateDefinition<Block, BlockState>> map){
        FactoryAPIClient.extraModels.forEach((r, id)-> map.put(r, id.stateDefinition()));
    }
    *///?} else {
    @Inject(method = "definitionLocationToBlockStateMapper", at = @At("RETURN"))
    private static void loadBlockStates(CallbackInfoReturnable<Function<ResourceLocation, StateDefinition<Block, BlockState>>> cir, @Local Map<ResourceLocation, StateDefinition<Block, BlockState>> map){
        FactoryAPIClient.extraModels.forEach((r, id)-> map.put(r, id.stateDefinition()));
    }
    //?}
}