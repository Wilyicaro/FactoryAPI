package wily.factoryapi.base.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.User;

public interface MinecraftAccessor {
    static MinecraftAccessor getInstance(){
        return (MinecraftAccessor) Minecraft.getInstance();
    }
    //? if <1.20.5 {
    /*float getPausePartialTick();
    *///?}
    boolean setUser(User user);

    boolean hasGameLoaded();

    static void reloadResourcePacksIfLoaded(){
        if (MinecraftAccessor.getInstance().hasGameLoaded()) Minecraft.getInstance().reloadResourcePacks();
    }
}
