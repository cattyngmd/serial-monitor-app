package dev.cattyn.serialmonitorapp.config;

import lombok.Getter;

public abstract class SimpleConfig<T> implements Config<T> {
    @Getter
    private final String name;
    private T value;

    public SimpleConfig(String name, T defaultValue) {
        this.name = name;
        this.value = defaultValue;
    }

    @Override
    public T get() {
        return value;
    }

    @Override
    public void set(T value) {
        this.value = value;
    }

    public static class Int extends SimpleConfig<Integer> {
        public Int(String name, Integer defaultValue) {
            super(name, defaultValue);
        }

        @Override
        public void fromString(String string) {
            set(Integer.parseInt(string));
        }

        @Override
        public String asString() {
            return String.valueOf(get());
        }
    }

    public static class Str extends SimpleConfig<String> {
        public Str(String name, String defaultValue) {
            super(name, defaultValue);
        }

        @Override
        public void fromString(String string) {
            set(string);
        }

        @Override
        public String asString() {
            return get();
        }
    }
}
