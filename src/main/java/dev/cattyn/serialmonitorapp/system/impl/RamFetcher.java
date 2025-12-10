package dev.cattyn.serialmonitorapp.system.impl;

import com.profesorfalken.jsensors.model.components.Components;
import com.sun.management.OperatingSystemMXBean;
import dev.cattyn.serialmonitorapp.system.SimpleSystemFetcher;
import dev.cattyn.serialmonitorapp.util.SerialOutputStream;

import java.io.IOException;
import java.lang.management.ManagementFactory;

public final class RamFetcher extends SimpleSystemFetcher<RamFetcher.Data> {
    @Override
    public Data fetch(Components components) {
        OperatingSystemMXBean bean =
                (OperatingSystemMXBean)
                        ManagementFactory.getOperatingSystemMXBean();

        return Data.fromByes(bean.getTotalMemorySize() - bean.getFreeMemorySize(), bean.getTotalMemorySize());
    }

    @Override
    public void serialize(Data object, SerialOutputStream dos) throws IOException {
        dos.writeTinyFloat((float) (object.usedMemory / object.totalMemory));
    }

    private static double bytesToMegabytes(long bytes) {
        return bytes / 1024d / 1024d;
    }

    public record Data(double usedMemory, double totalMemory) {
        static Data fromByes(long usedMemory, long totalMemory) {
            return new Data(bytesToMegabytes(usedMemory), bytesToMegabytes(totalMemory));
        }
    }
}
