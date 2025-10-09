package wily.factoryapi.base.client;

import com.mojang.serialization.Codec;
import net.minecraft.client.Minecraft;
import wily.factoryapi.base.ArbitrarySupplier;
import wily.factoryapi.base.config.FactoryConfig;
import wily.factoryapi.base.config.FactoryConfigControl;
import wily.factoryapi.base.config.FactoryConfigDisplay;
import wily.factoryapi.util.FactoryComponents;

public class FactoryOptions {
    public static final FactoryConfig.StorageHandler CLIENT_STORAGE = new FactoryConfig.StorageHandler().withFile("factory_api/client_options.json").withDefaultFile("factory_api/default_client_options.json");
    public static final FactoryConfig<Boolean> UI_DEFINITION_LOGGING = CLIENT_STORAGE.register(FactoryConfig.createBoolean("uiDefinitionLogging", FactoryConfigDisplay.createToggle(FactoryComponents.optionsName("uiDefinitionLogging")), false, b-> {}, CLIENT_STORAGE));
    public static final FactoryConfig<Boolean> NEAREST_MIPMAP_SCALING = CLIENT_STORAGE.register(FactoryConfig.createBoolean("nearestMipmapScaling", FactoryConfigDisplay.createToggle(FactoryComponents.optionsName("nearestMipmapScaling")), false, b-> Minecraft.getInstance().execute(MinecraftAccessor::reloadResourcePacksIfLoaded), CLIENT_STORAGE));
    public static final FactoryConfig<Boolean> RANDOM_BLOCK_ROTATIONS = CLIENT_STORAGE.register(FactoryConfig.createBoolean("randomBlockRotations", FactoryConfigDisplay.createToggle(FactoryComponents.optionsName("randomBlockRotations")), true, b-> Minecraft.getInstance().execute(MinecraftAccessor::reloadResourcePacksIfLoaded), CLIENT_STORAGE));
    public static final FactoryConfig<String> MANUAL_MIPMAP_PATH = CLIENT_STORAGE.register(FactoryConfig.create("manualMipmapPath", new FactoryConfigDisplay.Instance<>(FactoryComponents.optionsName("manualMipmapPath")), new FactoryConfigControl.TextEdit<>(Codec.STRING),"textures/mipmap", b-> {}, CLIENT_STORAGE));
    public static final FactoryConfig<Boolean> MANUAL_MIPMAP = CLIENT_STORAGE.register(FactoryConfig.createBoolean("manualMipmap", FactoryConfigDisplay.createToggle(FactoryComponents.optionsName("manualMipmap")), true, b-> Minecraft.getInstance().execute(MinecraftAccessor::reloadResourcePacksIfLoaded), CLIENT_STORAGE));
}
