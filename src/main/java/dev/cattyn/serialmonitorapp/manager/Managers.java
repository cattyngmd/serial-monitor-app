package dev.cattyn.serialmonitorapp.manager;

import dev.cattyn.serialmonitorapp.Globals;
import dev.cattyn.serialmonitorapp.manager.impl.SystemManager;

public class Managers {
    private static SystemManager SYSTEM;

    public static SystemManager getSystem() {
        return SYSTEM;
    }

    public static void init() {
        Globals.LOGGER.info("Preparing managers.");
        SYSTEM = new SystemManager();

        Globals.LOGGER.info("Initialization complete!");
    }
}
