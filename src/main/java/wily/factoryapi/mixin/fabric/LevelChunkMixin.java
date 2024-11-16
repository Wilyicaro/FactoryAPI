package wily.factoryapi.mixin.fabric;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.*;
import net.minecraft.world.level.lighting.LightEngine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import wily.factoryapi.base.IFactoryBlock;

@Mixin(value = {LevelChunk.class, ProtoChunk.class} )
public class LevelChunkMixin {
    //? if >=1.21.2 {
    /*@Redirect(method = "setBlockState", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/lighting/LightEngine;hasDifferentLightProperties(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/world/level/block/state/BlockState;)Z"))
    public boolean setBlockState(BlockState blockState, BlockState blockState1, BlockPos blockPos){
        return LightEngine.hasDifferentLightProperties(blockState,blockState1) || IFactoryBlock.getBlockLuminance(blockState, (BlockGetter) this,blockPos) != IFactoryBlock.getBlockLuminance(blockState1, (BlockGetter) this,blockPos);
    }
    *///?}
}
