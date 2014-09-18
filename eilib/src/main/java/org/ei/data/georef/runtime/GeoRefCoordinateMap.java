package org.ei.data.georef.runtime;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GeoRefCoordinateMap {
    protected static Log log4j = LogFactory.getLog(GeoRefCoordinateMap.class);
    private static GeoRefCoordinateMap instance = null;
    private Map<String, String> georefMapCoordinates = null;

    private GeoRefCoordinateMap() {
    }

    public static GeoRefCoordinateMap getInstance() {
        if (instance == null) {
            synchronized (GeoRefCoordinateMap.class) {
                instance = new GeoRefCoordinateMap();
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

        InputStream geofile = null;
        BufferedReader reader = null;

        try {
            geofile = this.getClass().getResourceAsStream("/GeoRefJSONCoordinates.txt");
            reader = new BufferedReader(new InputStreamReader(geofile, "UTF-8"));
            for (String aline = reader.readLine(); aline != null; aline = reader.readLine()) {
                // Load georefMapCoordinates with coordinates for
                // SearchResults Google Mapping
                String[] coords = aline.split("QQDELQQ");
                if (coords != null && coords.length == 2) {
                    georefMapCoordinates.put(coords[0], coords[1]);
                } else {
                    log4j.warn("Unable to get coords from line '" + aline + "'");
                }
            }
            log4j.info("Done loading terms...");

        } catch (FileNotFoundException e) {
            log4j.error("'GeoRefJSONCoordinates.txt' not found.  Please ensure it is in the root of the classpath!");
        } catch (Exception e) {
            log4j.error("Unable to read from 'GeoRefJSONCoordinates.txt'", e);
        } finally {
            try {
                if (reader != null)
                    reader.close();
                if (geofile != null)
                    geofile.close();
            } catch (IOException e) {
                // Ignore - nothing we can do!
            }
        }

        return;
    }
}
