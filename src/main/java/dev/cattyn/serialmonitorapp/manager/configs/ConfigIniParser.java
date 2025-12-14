package dev.cattyn.serialmonitorapp.manager.configs;

import dev.cattyn.serialmonitorapp.config.Config;

import java.util.List;

public record ConfigIniParser(List<Config<?>> configs) {
    public String serialize() {
        StringBuilder sb = new StringBuilder();
        configs.forEach(k -> {
            sb.append(k.getName());
            sb.append("=");
            sb.append(k.asString());
            sb.append("\n");
        });
        return sb.toString();
    }

    public void deserialize(String content) {
        for (String string : content.split("\n")) {
            string = string.trim();
            String[] split = string.split("=");
            if (split.length != 2) continue;

            try {
                Config<?> config = find(split[0]);
                config.fromString(split[1]);
            } catch (Exception e) {

            }
        }
    }

    private Config<?> find(String name) {
        return configs.stream()
                .filter(config -> config.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElseThrow();
    }
}
