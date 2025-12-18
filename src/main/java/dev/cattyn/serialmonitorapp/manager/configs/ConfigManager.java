package dev.cattyn.serialmonitorapp.manager.configs;

import dev.cattyn.serialmonitorapp.config.Config;
import dev.cattyn.serialmonitorapp.config.ConfigFactory;
import dev.cattyn.serialmonitorapp.util.FileUtil;
import lombok.Getter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Getter
public final class ConfigManager implements ConfigFactory {
    public static final Path CONFIG_PATH = FileUtil.MAIN.resolve("config.ini");

    private final List<Config<?>> configurations = new ArrayList<>();
    private final ConfigIniParser parser = new ConfigIniParser(configurations);

    private final Config<Integer> baud = integer("baud", 38400);
    private final Config<Integer> port = integer("port", 0);

    public ConfigManager() {
        configurations.sort(Comparator.comparing(Config::getName));
    }

    public void save() {
        if (!FileUtil.MAIN.toFile().exists()) {
            FileUtil.MAIN.toFile().mkdir();
        }

        try {
            Files.writeString(CONFIG_PATH, parser.serialize());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void load() {
        try {
            parser.deserialize(Files.readString(CONFIG_PATH));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public <T> Config<T> register(Config<T> config) {
        configurations.add(config);
        return config;
    }
}
