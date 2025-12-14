package dev.cattyn.serialmonitorapp.util;

import com.profesorfalken.jsensors.util.OSDetector;
import lombok.experimental.UtilityClass;

import java.nio.file.Path;

@UtilityClass
public class FileUtil {
    public final Path LOCALAPPDATA = detectLocalAppdata();
    public final Path MAIN = LOCALAPPDATA.resolve("serial-monitor");

    private Path detectLocalAppdata() {
        if (OSDetector.isWindows()) {
            return Path.of(System.getenv("LOCALAPPDATA"));
        } else if (OSDetector.isUnix()) {
            String xdgPath = System.getenv("XDG_DATA_HOME");
            if (xdgPath != null && !xdgPath.isEmpty()) {
                return Path.of(xdgPath);
            } else {
                return Path.of(System.getProperty("user.home"), ".local", "share");
            }
        } else {
            return Path.of(System.getProperty("user.home")); // idc
        }
    }
}
