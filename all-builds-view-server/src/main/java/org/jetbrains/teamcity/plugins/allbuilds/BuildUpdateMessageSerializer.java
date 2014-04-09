package org.jetbrains.teamcity.plugins.allbuilds;

import org.atmosphere.cpr.Serializer;

import java.io.IOException;
import java.io.OutputStream;

public class BuildUpdateMessageSerializer implements Serializer {

    @Override
    public void write(OutputStream os, Object o) throws IOException {
        String message = ((BuildUpdateMessage) o).asJson();
        os.write(message.getBytes("UTF-8"));
        os.flush();
    }
}
