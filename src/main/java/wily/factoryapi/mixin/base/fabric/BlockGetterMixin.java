//? if fabric {
package wily.factoryapi.mixin.base.fabric;

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
    private void injectLuminance(BlockPos pos, CallbackInfoReturnable<Integer> info){
        BlockGetter level = ((BlockGetter)this);
        if (level.getBlockState(pos).getBlock() instanceof IFactoryBlock b) info.setReturnValue(b.getLightEmission(level.getBlockState(pos),level,pos));
    }
}
//?}
