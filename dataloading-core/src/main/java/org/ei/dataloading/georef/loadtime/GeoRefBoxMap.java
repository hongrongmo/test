package org.ei.dataloading.georef.loadtime;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GeoRefBoxMap {
    protected static Log log = LogFactory.getLog(GeoRefBoxMap.class);
    private static GeoRefBoxMap instance = null;
    private Map<String, String> georefMapCoordinates = null;

    private GeoRefBoxMap() {
    }

    public static GeoRefBoxMap getInstance() {
        if (instance == null) {
            synchronized (GeoRefBoxMap.class) {
                instance = new GeoRefBoxMap();
                instance.populateMap();
            }
        }
        return instance;
    }

    public String lookupGeoRefTermCoordinates(String geoterm) {
        geoterm = geoterm.toLowerCase();

        if (georefMapCoordinates.containsKey(geoterm)) {
            return (String) georefMapCoordinates.get(geoterm);
        } else {
            return null;
        }
    }

    private void populateMap() {
        georefMapCoordinates = new HashMap<String, String>();

        BufferedReader rdr = null;
        //HH
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("org/ei/data/georef/runtime/GeoRefCoodinates.txt");
        try {
           // rdr = new BufferedReader(new FileReader("GeoRefCoodinates.txt"));   //original
        	rdr = new BufferedReader(new InputStreamReader(in));
            if (rdr != null) {
                while (rdr.ready()) {
                    String aline = rdr.readLine();

                    String[] coords = aline.split(",");
                    georefMapCoordinates.put(coords[0].trim().toLowerCase(), coords[1].replaceAll("[- ]", ""));

                } // while
                System.out.println("Done loading terms...");
            }
        } catch (FileNotFoundException e) {
            System.out.println("File Not found.  Look for geodata subdirectory.");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                rdr.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return;
    }
}
