package dev.cattyn.serialmonitorapp.system.impl;

import com.profesorfalken.jsensors.model.components.Components;
import com.profesorfalken.jsensors.model.components.Cpu;
import com.profesorfalken.jsensors.model.sensors.Temperature;
import dev.cattyn.serialmonitorapp.system.SimpleSystemFetcher;
import dev.cattyn.serialmonitorapp.util.SerialOutputStream;

import java.io.IOException;

public final class TemperatureFetcher extends SimpleSystemFetcher<Float> {
    @Override
    public Float fetch(Components components) {
        Cpu cpu = cpuOrThrow(components);
        int sum = 0;
        float temp = 0;

        for (Temperature temperature : cpu.sensors.temperatures) {
            temp += temperature.value.floatValue();
            sum++;
        }

        return temp / sum;
    }

    @Override
    public void serialize(Float object, SerialOutputStream dos) throws IOException {
        dos.writeTinyFloat(object);
    }
}
