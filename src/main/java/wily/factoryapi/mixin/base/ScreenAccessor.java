//? if >=1.21.11 {
/*package wily.factoryapi.mixin.base;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.ClickEvent;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Screen.class)
public interface ScreenAccessor {
	@Invoker
	static void callDefaultHandleGameClickEvent(ClickEvent clickEvent, Minecraft minecraft, @Nullable Screen screen) {
		throw new UnsupportedOperationException();
	}
}
*///?}