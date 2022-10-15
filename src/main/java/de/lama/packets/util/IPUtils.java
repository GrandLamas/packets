package de.lama.packets.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public final class IPUtils {

    private static final String IP_CHECK = "http://checkip.amazonaws.com/";

    private IPUtils() {
    }

    public static String getSystemPublicIp() throws IOException {
        URL url = new URL(IP_CHECK);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
            return br.readLine();
        }
    }
}
