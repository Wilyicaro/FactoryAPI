package wily.factoryapi.fabric.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import wily.factoryapi.base.IFactoryBlock;

@Mixin(BlockGetter.class)
public interface BlockGetterMixin {
    @Inject(method = ("getLightEmission"), at = @At("HEAD"), cancellable = true)
    default void injectLuminance(BlockPos pos, CallbackInfoReturnable<Integer> info){
        BlockGetter level = ((BlockGetter)this);
        if (level.getBlockState(pos).getBlock() instanceof IFactoryBlock) info.setReturnValue(((IFactoryBlock)level.getBlockState(pos).getBlock()).getLuminance(level.getBlockState(pos),level,pos));
    }
}
