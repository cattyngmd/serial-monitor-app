package dev.cattyn.serialmonitorapp;

import com.fazecast.jSerialComm.SerialPort;
import dev.cattyn.serialmonitorapp.manager.Managers;
import dev.cattyn.serialmonitorapp.manager.configs.ConfigManager;
import dev.cattyn.serialmonitorapp.util.ConsoleUtil;
import dev.cattyn.serialmonitorapp.util.SerialOutputStream;
import lombok.Cleanup;
import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public final class Main implements Globals {
    @SneakyThrows
    public void launch() {
        ConsoleUtil.prepare(LOGGER);
        Managers.init();
        prepareConfig();

        SerialPort devicePort = SerialPort.getCommPorts()[Managers.getConfig().getPort().get()];
        while (!Thread.interrupted()) {
            try {
                startSerialPort(devicePort);
            } catch (Exception e) {
                LOGGER.severe("Error occurred, restarting service in 5 seconds.");
                Thread.sleep(5000);
            }
        }
    }

    private void prepareConfig() {
        if (ConfigManager.CONFIG_PATH.toFile().exists()) {
            Managers.getConfig().load();
            LOGGER.info("Found an existing config, load it? [Y/n]");

            if (ConsoleUtil.scanYes())
                return;
        }
        prepareNewConfig();
    }

    private void prepareNewConfig() {
        LOGGER.info("Baud rate (default - 38400):");
        String baud = ConsoleUtil.scan();
        if (!baud.isBlank()) Managers.getConfig().getBaud().fromString(baud);

        SerialPort[] ports = SerialPort.getCommPorts();
        StringBuilder suggestions = new StringBuilder();
        for (int i = 0; i < ports.length; i++) {
            suggestions.append(i + 1);
            suggestions.append(" - ");
            suggestions.append(ports[i].getDescriptivePortName());
            if (i != ports.length - 1) suggestions.append(", ");
        }

        int devicePort = -1;
        while (devicePort == -1) {
            LOGGER.info("Device port [%s]".formatted(suggestions));
            String port = ConsoleUtil.scan();
            try {
                int index = Integer.parseInt(port) - 1;
                if (index < 0 || index >= ports.length)
                    throw new RuntimeException();
                devicePort = index;
            } catch (Exception e) {
                LOGGER.warning("Invalid device port try again!");
            }
        }
        Managers.getConfig().getPort().set(devicePort);
    }

    private static void startSerialPort(SerialPort devicePort) throws Exception {
        devicePort.openPort();
        devicePort.setBaudRate(38400);
        devicePort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 5000, 0);

        try {
            handleSerialConnection(devicePort);
        } finally {
            devicePort.closePort();
        }
    }

    private static void handleSerialConnection(SerialPort devicePort) throws Exception {
        @Cleanup SerialOutputStream out = new SerialOutputStream(devicePort.getOutputStream());
        @Cleanup BufferedReader reader = new BufferedReader(new InputStreamReader(devicePort.getInputStream(), StandardCharsets.UTF_8));

        for (;;) {
            Thread.sleep(3500);
            Managers.getSystem().forEach((v, f) -> {
                try {
                    f.serialize(v, out);
                } catch (IOException e) {
                    LOGGER.severe(e.toString());
                }
            });
            out.flush();

            String line = reader.readLine();
            LOGGER.info("Response: " + (line != null ? line : "(null - timeout)"));
        }
    }

    private static boolean isDevicePort(SerialPort port) {
        return port.getDescriptivePortName().toLowerCase(Locale.ROOT).contains("serial");
    }
}