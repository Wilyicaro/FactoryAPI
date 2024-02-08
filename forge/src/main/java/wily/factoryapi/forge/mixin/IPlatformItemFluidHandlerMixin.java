package wily.factoryapi.forge.mixin;

import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import org.spongepowered.asm.mixin.Mixin;
import wily.factoryapi.base.IPlatformItemFluidHandler;

@Mixin(IPlatformItemFluidHandler.class)
public interface IPlatformItemFluidHandlerMixin extends IFluidHandlerItem {

}
