package wily.factoryapi.mixin.base;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.ClickEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Screen.class)
public interface ScreenAccessor {
	//? if >=1.21.6 {
	/*@Invoker
	static void callDefaultHandleClickEvent(ClickEvent clickEvent, Minecraft minecraft, Screen screen) {
		throw new UnsupportedOperationException();
	}
	*///?}
}
