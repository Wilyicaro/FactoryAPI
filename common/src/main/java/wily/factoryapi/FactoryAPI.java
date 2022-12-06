package wily.factoryapi;

import com.google.common.base.Suppliers;
import dev.architectury.registry.CreativeTabRegistry;
import dev.architectury.registry.registries.DeferredRegister;
import dev.architectury.registry.registries.Registries;
import dev.architectury.registry.registries.RegistrySupplier;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;
import java.util.logging.Logger;

public class FactoryAPI {
    public static final String MOD_ID = "factory_api";
    public static final Supplier<Registries> REGISTRIES = Suppliers.memoize(() -> Registries.get(MOD_ID));


    public static final Logger LOGGER = Logger.getLogger(MOD_ID);
    
    public static void init() {
        LOGGER.info("Initializing FactoryAPI!");

    }
}
