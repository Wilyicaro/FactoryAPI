package wily.factoryapi.base.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.components.spectator.SpectatorGui;
import net.minecraft.world.item.ItemStack;

public interface GuiAccessor {
    ItemStack getLastToolHighlight();

    int getToolHighlightTimer();

    SpectatorGui getSpectatorGui();

    static GuiAccessor of(Gui gui){
        return (GuiAccessor) gui;
    }

    static GuiAccessor getInstance(){
        return of(Minecraft.getInstance().gui);
    }
}
