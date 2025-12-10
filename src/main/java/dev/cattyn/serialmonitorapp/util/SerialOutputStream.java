package dev.cattyn.serialmonitorapp.util;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class SerialOutputStream extends DataOutputStream {
    public SerialOutputStream(OutputStream out) {
        super(out);
    }

    public void writeTinyFloat(float value) throws IOException {
        short tiny = (short) Math.round(value * 10);
        writeShort(tiny);
    }
}
