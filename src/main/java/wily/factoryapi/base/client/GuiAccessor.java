package wily.factoryapi.base.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
//? if >=26.2 {
import net.minecraft.client.gui.Hud;
//?}
import net.minecraft.client.gui.components.spectator.SpectatorGui;
import net.minecraft.world.item.ItemStack;
import wily.factoryapi.util.FactoryScreenUtil;

public interface GuiAccessor {
    ItemStack getLastToolHighlight();

    int getToolHighlightTimer();

    SpectatorGui getSpectatorGui();

    //~ if >=26.2 'Gui gui' -> 'Hud gui'
    static GuiAccessor of(Hud gui){
        return (GuiAccessor) gui;
    }

    static GuiAccessor getInstance(){
        return of(FactoryScreenUtil.getGuiOrHud(Minecraft.getInstance()));
    }
}
