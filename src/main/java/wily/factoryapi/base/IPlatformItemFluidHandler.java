package wily.factoryapi.base;

//? if forge {
/*import net.minecraftforge.fluids.capability.IFluidHandlerItem;
*///?} else if neoforge && <1.21.9 {
/*import net.neoforged.neoforge.fluids.capability.IFluidHandlerItem;
*///?}

public interface IPlatformItemFluidHandler extends IPlatformFluidHandler, IHasItemContainer /*? if forge || (neoforge && <1.21.9) {*//*, IFluidHandlerItem*//*?}*/ {
}
