package dev.cattyn.serialmonitorapp;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public interface Globals {
    ExecutorService EXECUTOR = Executors.newFixedThreadPool(4);
    Logger LOGGER = Logger.getLogger("[serial-monitor-app]");
}
