package wily.factoryapi.base.client;

import net.minecraft.client.gui.components.spectator.SpectatorGui;
import net.minecraft.client.gui.spectator.SpectatorMenu;

public interface SpectatorGuiAccessor {
    float getVisibility();

    SpectatorMenu getMenu();

    static SpectatorGuiAccessor of(SpectatorGui gui){
        return (SpectatorGuiAccessor) gui;
    }
    static SpectatorGuiAccessor getInstance(){
        return of(GuiAccessor.getInstance().getSpectatorGui());
    }
}
