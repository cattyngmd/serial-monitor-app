package dev.cattyn.serialmonitorapp.config;

public interface ConfigFactory {
    <T> Config<T> register(Config<T> config);

    default Config<String> string(String name, String value) {
        return register(new SimpleConfig.Str(name, value));
    }

    default Config<Integer> integer(String name, int value) {
        return register(new SimpleConfig.Int(name, value));
    }
}
