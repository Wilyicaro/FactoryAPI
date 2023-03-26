package wily.factoryapi;

import wily.factoryapi.base.IFluidItem;

import java.util.logging.Logger;

public class FactoryAPI {
    public static final String MOD_ID = "factory_api";


    public static final Logger LOGGER = Logger.getLogger(MOD_ID);
    
    public static void init() {
        LOGGER.info("Initializing FactoryAPI!");
    }
}
