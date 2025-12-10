package dev.cattyn.serialmonitorapp.system;

import com.profesorfalken.jsensors.model.components.Components;

public interface SystemFetcher<T> extends Serializer<T> {
    T fetch(Components components);
}
