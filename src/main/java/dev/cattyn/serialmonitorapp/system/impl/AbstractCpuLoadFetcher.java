package dev.cattyn.serialmonitorapp.system.impl;

import com.profesorfalken.jsensors.util.OSDetector;
import dev.cattyn.serialmonitorapp.system.SimpleSystemFetcher;
import dev.cattyn.serialmonitorapp.system.impl.cpu.LinuxCpuFetcher;
import dev.cattyn.serialmonitorapp.system.impl.cpu.WindowsCpuFetcher;
import dev.cattyn.serialmonitorapp.util.SerialOutputStream;

import java.io.IOException;
import java.util.List;

public abstract class AbstractCpuLoadFetcher extends SimpleSystemFetcher<AbstractCpuLoadFetcher.Data> {
    @Override
    public void serialize(Data object, SerialOutputStream dos) throws IOException {
        int size = object.cpuLoadList.size();
        dos.writeByte(size);
        for (Float v : object.cpuLoadList) {
            dos.writeTinyFloat(v);
        }
        dos.writeTinyFloat(object.avgLoad);
    }

    public static AbstractCpuLoadFetcher create() {
        return OSDetector.isWindows() ? new WindowsCpuFetcher() : new LinuxCpuFetcher();
    }

    public record Data(List<Float> cpuLoadList, float avgLoad) { }
}
