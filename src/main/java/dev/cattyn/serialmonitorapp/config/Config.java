package dev.cattyn.serialmonitorapp.config;

public interface Config<T> {
    String getName();
    T get();
    void set(T value);

    void fromString(String string);
    String asString();
}
