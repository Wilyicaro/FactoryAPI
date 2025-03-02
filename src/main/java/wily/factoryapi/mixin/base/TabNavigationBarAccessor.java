package wily.factoryapi.mixin.base;

import net.minecraft.client.gui.components.tabs.TabNavigationBar;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(TabNavigationBar.class)
public interface TabNavigationBarAccessor {
    @Invoker("currentTabIndex")
    int getCurrentTabIndex();
}
