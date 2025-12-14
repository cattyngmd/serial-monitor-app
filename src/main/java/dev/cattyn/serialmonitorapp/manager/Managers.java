package dev.cattyn.serialmonitorapp.manager;

import dev.cattyn.serialmonitorapp.Globals;
import dev.cattyn.serialmonitorapp.manager.configs.ConfigManager;
import dev.cattyn.serialmonitorapp.manager.system.SystemManager;
import lombok.Getter;

public class Managers {
    @Getter
    private static SystemManager system;
    @Getter
    private static ConfigManager config;

    public static void init() {
        Globals.LOGGER.info("Preparing managers.");
        config = new ConfigManager();
        config.save();
        system = new SystemManager();
        Globals.LOGGER.info("Initialization complete!");
    }
}
