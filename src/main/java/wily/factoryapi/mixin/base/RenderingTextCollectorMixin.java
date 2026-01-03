//? if >=1.21.11 {
/*package wily.factoryapi.mixin.base;

import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import wily.factoryapi.base.FactoryRenderingTextCollector;

@Mixin(targets = "net/minecraft/client/gui/GuiGraphics$RenderingTextCollector")
public class RenderingTextCollectorMixin implements FactoryRenderingTextCollector {
	@Shadow
	private GuiGraphics field_63856;
	@Override
	public GuiGraphics getGuiGraphics() {
		return field_63856;
	}
}
*///?}