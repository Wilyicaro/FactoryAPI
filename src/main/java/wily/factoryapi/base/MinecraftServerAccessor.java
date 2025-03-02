package wily.factoryapi.base;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.storage.LevelStorageSource;

public interface MinecraftServerAccessor {
    LevelStorageSource.LevelStorageAccess getStorageSource();
    static MinecraftServerAccessor of(MinecraftServer server){
        return (MinecraftServerAccessor) server;
    }
}
