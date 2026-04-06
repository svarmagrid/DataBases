package org.example.service;

import java.io.*;
import java.net.*;

public class LocationService {
    public static String getLocation() {
        try {
            URL url = new URL("http://ip-api.com/line/?fields=city");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String city = reader.readLine();
            reader.close();
            return city;
        } catch(Exception e) {
            return "Unknown";
        }
    }
}