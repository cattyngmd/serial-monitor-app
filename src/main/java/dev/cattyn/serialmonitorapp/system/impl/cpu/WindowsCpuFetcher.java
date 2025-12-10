package dev.cattyn.serialmonitorapp.system.impl.cpu;

import com.profesorfalken.jsensors.model.components.Components;
import com.profesorfalken.jsensors.model.components.Cpu;
import com.profesorfalken.jsensors.model.sensors.Load;
import dev.cattyn.serialmonitorapp.system.impl.AbstractCpuLoadFetcher;

import java.util.ArrayList;
import java.util.List;

public final class WindowsCpuFetcher extends AbstractCpuLoadFetcher {
    @Override
    public Data fetch(Components components) {
        Cpu cpu = cpuOrThrow(components);

        List<Float> cpuLoadList = new ArrayList<>();
        int sum = 0;
        float loadCombined = 0f;

        for (Load load : cpu.sensors.loads) {
            if (load.name.contains("Memory") || load.name.contains("Total"))
                continue;

            cpuLoadList.add(load.value.floatValue());
            loadCombined += load.value.floatValue();
            sum++;
        }

        return new Data(List.copyOf(cpuLoadList), loadCombined / sum);
    }
}
