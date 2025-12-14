package dev.cattyn.serialmonitorapp.manager.system;

import com.profesorfalken.jsensors.JSensors;
import com.profesorfalken.jsensors.model.components.Components;
import dev.cattyn.serialmonitorapp.Globals;
import dev.cattyn.serialmonitorapp.system.SystemFetcher;
import dev.cattyn.serialmonitorapp.system.impl.AbstractCpuLoadFetcher;
import dev.cattyn.serialmonitorapp.system.impl.RamFetcher;
import dev.cattyn.serialmonitorapp.system.impl.TemperatureFetcher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

public final class SystemManager {
    private final Map<Class<?>, Object> cache = new HashMap<>();

    private final List<SystemFetcher<?>> fetchers = List.of(
            AbstractCpuLoadFetcher.create(),
            new RamFetcher(),
            new TemperatureFetcher()
    );

    public SystemManager() {
        fetchAll(JSensors.get.components());
        Globals.EXECUTOR.submit(this::asyncTask);
    }

    public synchronized <V, T extends SystemFetcher<V>> V get(Class<T> exact) {
        return (V) cache.get(exact);
    }

    public synchronized <T> void forEach(BiConsumer<T, SystemFetcher<T>> consumer) {
        for (SystemFetcher<?> fetcher : fetchers) {
            Object o = cache.get(fetcher.getClass());
            consumer.accept((T) o, (SystemFetcher<T>) fetcher);
        }
    }

    private void asyncTask() {
        while (!Thread.interrupted()) {
            try {
                Components components = JSensors.get.components();
                fetchAll(components);
                Thread.sleep(200);
            } catch (Throwable t) {
                Globals.LOGGER.warning(t.getMessage());
            }
        }
    }

    private void fetch(SystemFetcher<?> fetcher, Components components) {
        Object data = fetcher.fetch(components);
        synchronized (this) {
            cache.put(fetcher.getClass(), data);
        }
    }

    private void fetchAll(Components components) {
        for (SystemFetcher<?> fetcher : fetchers) fetch(fetcher, components);
    }
}
