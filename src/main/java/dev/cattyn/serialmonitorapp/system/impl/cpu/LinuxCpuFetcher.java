package dev.cattyn.serialmonitorapp.system.impl.cpu;

import com.profesorfalken.jsensors.model.components.Components;
import dev.cattyn.serialmonitorapp.system.impl.AbstractCpuLoadFetcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class LinuxCpuFetcher extends AbstractCpuLoadFetcher {
    private final List<CpuStat> lastStats = new ArrayList<>();

    @Override
    public Data fetch(Components components) {
        try {
            return fileFetch();
        } catch (RuntimeException e) {
            return new Data(Collections.emptyList(), 0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Data fileFetch() throws IOException {
        List<CpuStat> current = readStats();
        if (lastStats.isEmpty()) {
            lastStats.addAll(current);
            throw new RuntimeException();
        }

        float sum = 0;
        List<Float> loadList = new ArrayList<>();
        for (int i = 0; i < lastStats.size(); i++) {
            CpuStat p = lastStats.get(i);
            CpuStat c = current.get(i);

            long idleDiff = c.idle - p.idle;
            long totalDiff = c.total() - p.total();
            float cpuLoad = (totalDiff - idleDiff) / (float) totalDiff * 100f;
            loadList.add(cpuLoad);
            sum += cpuLoad;
        }

        return new Data(List.copyOf(loadList), sum / loadList.size());
    }

    private List<CpuStat> readStats() throws IOException {
        List<String> lines = Files.readAllLines(Paths.get("/proc/stat"));
        List<CpuStat> stats = new ArrayList<>();

        for (String line : lines) {
            if (line.startsWith("cpu") && !line.startsWith("cpu ")) {
                stats.add(CpuStat.fromLine(line));
            }
        }
        return stats;
    }

    private record CpuStat(
            long user,
            long nice,
            long system,
            long idle,
            long iowait,
            long irq,
            long softirq,
            long steal
    ) {
        long total() {
            return user + nice + system + idle + iowait + irq + softirq + steal;
        }

        static CpuStat fromLine(String line) {
            String[] parts = line.trim().split("\\s+");
            return new CpuStat(
                    Long.parseLong(parts[1]),
                    Long.parseLong(parts[2]),
                    Long.parseLong(parts[3]),
                    Long.parseLong(parts[4]),
                    Long.parseLong(parts[5]),
                    Long.parseLong(parts[6]),
                    Long.parseLong(parts[7]),
                    parts.length > 8 ? Long.parseLong(parts[8]) : 0
            );
        }
    }

}
