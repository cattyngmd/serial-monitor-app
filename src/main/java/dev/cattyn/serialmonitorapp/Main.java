package dev.cattyn.serialmonitorapp;

import com.fazecast.jSerialComm.SerialPort;
import dev.cattyn.serialmonitorapp.manager.Managers;
import dev.cattyn.serialmonitorapp.util.ConsoleUtil;
import dev.cattyn.serialmonitorapp.util.SerialOutputStream;
import lombok.Cleanup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public final class Main implements Globals {
    public void launch() {
        ConsoleUtil.prepare(LOGGER);
        Managers.init();
        SerialPort devicePort = null;
        for (SerialPort port : SerialPort.getCommPorts()) {
            if (isDevicePort(port)) {
                devicePort = port;
                break;
            }
        }
        if (devicePort == null) {
            throw new RuntimeException();
        }

        startSerialPort(devicePort);
    }

    private static void startSerialPort(SerialPort devicePort) {
        devicePort.openPort();
        devicePort.setBaudRate(38400);
        devicePort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 5000, 0);

        try {
            handleSerialConnection(devicePort);
        } catch (Exception e) {
            e.printStackTrace();
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