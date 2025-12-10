package dev.cattyn.serialmonitorapp.system;

import dev.cattyn.serialmonitorapp.util.SerialOutputStream;

import java.io.IOException;

public interface Serializer<T> {
    void serialize(T object, SerialOutputStream dos) throws IOException;
}
