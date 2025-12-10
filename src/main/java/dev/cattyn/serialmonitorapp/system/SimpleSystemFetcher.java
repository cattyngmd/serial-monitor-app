package dev.cattyn.serialmonitorapp.system;

import com.profesorfalken.jsensors.model.components.Components;
import com.profesorfalken.jsensors.model.components.Cpu;

public abstract class SimpleSystemFetcher<T> implements SystemFetcher<T> {
    protected Cpu cpuOrThrow(Components components) {
        if (components.cpus.isEmpty())
            throw new RuntimeException("Couldn't identify the CPU.");
        return components.cpus.getFirst();
    }
}
