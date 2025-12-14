package dev.cattyn.serialmonitorapp.manager;

import dev.cattyn.serialmonitorapp.Globals;
import dev.cattyn.serialmonitorapp.manager.impl.SystemManager;
import lombok.Getter;

public class Managers {
    @Getter
    private static SystemManager system;

    public static void init() {
        Globals.LOGGER.info("Preparing managers.");
        system = new SystemManager();

        Globals.LOGGER.info("Initialization complete!");
    }
}
