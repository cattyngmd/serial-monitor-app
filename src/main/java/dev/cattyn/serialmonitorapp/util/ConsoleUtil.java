package dev.cattyn.serialmonitorapp.util;

import com.profesorfalken.jsensors.util.OSDetector;

import java.io.IOException;
import java.util.Date;
import java.util.logging.ConsoleHandler;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public final class ConsoleUtil {
    private static final Formatter FORMATTER = new LogFormatter();

    private ConsoleUtil() {
        throw new AssertionError();
    }

    public static void prepare(Logger logger) {
        logger.setUseParentHandlers(false);
        ConsoleHandler handler = new ConsoleHandler();
        handler.setFormatter(ConsoleUtil.FORMATTER);
        logger.addHandler(handler);
    }

    public static void cleanup() {
        try {
            new ProcessBuilder(getClearCommand())
                    .inheritIO()
                    .start()
                    .waitFor();
        } catch (InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String[] getClearCommand() {
        if (OSDetector.isWindows()) {
            return new String[]{"cmd", "/c", "cls"};
        } else if (OSDetector.isUnix()) {
            return new String[]{"clear"};
        }
        return new String[0];
    }

    public static final class LogFormatter extends Formatter {
        private static final String FORMAT = "[%1$tT] [%2$s] %3$s %n";

        private LogFormatter() {}

        @Override
        public String format(LogRecord record) {
            return String.format(FORMAT,
                    new Date(record.getMillis()),
                    record.getLevel().getLocalizedName(),
                    formatMessage(record));
        }

    }
}
