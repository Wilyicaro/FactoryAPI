//? if fabric {
package wily.factoryapi.base.compat.client;

import com.terraformersmc.modmenu.ModMenu;
import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.terraformersmc.modmenu.gui.ModsScreen;
import net.minecraft.client.gui.screens.Screen;
import org.apache.logging.log4j.core.util.ReflectionUtil;
import wily.factoryapi.FactoryAPI;
import wily.factoryapi.base.client.UIDefinitionManager;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.function.Function;

public class FactoryAPIModMenuCompat implements ModMenuApi {
    public static final Field CONFIG_SCREEN_FACTORIES = FactoryAPI.getAccessibleField(ModMenu.class,"configScreenFactories");
    public static Screen getConfigScreen(String modid, Screen parent){
        return ModMenu.getConfigScreen(modid,parent);
    }

    public static void registerConfigScreen(String modId, Function<Screen,Screen> function){
        Map<String, ConfigScreenFactory<?>> map = (Map<String, ConfigScreenFactory<?>>) ReflectionUtil.getStaticFieldValue(CONFIG_SCREEN_FACTORIES);
        map.put(modId, function::apply);
    }

    public static void init(){
        UIDefinitionManager.registerDefaultScreen(FactoryAPI.createLocation(ModMenu.MOD_ID, "mods"), ModsScreen::new);
    }
}
//?}
