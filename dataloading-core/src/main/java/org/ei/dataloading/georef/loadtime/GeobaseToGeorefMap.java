package org.ei.dataloading.georef.loadtime;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GeobaseToGeorefMap {
    private Map<String, String> geobaseToGeorefMap = null;
    private static GeobaseToGeorefMap instance = null;
    private static Pattern csvRE;
    public static final String CSV_PATTERN = "\"([^\"]+?)\",?|([^,]+),?|,";

    private GeobaseToGeorefMap() {
        csvRE = Pattern.compile(CSV_PATTERN);
        populateMap();
    }

    public static GeobaseToGeorefMap getInstance() {
        if (instance == null) {
            synchronized (GeobaseToGeorefMap.class) {
                instance = new GeobaseToGeorefMap();
            }
        }
        return instance;
    }

    public String lookupGeobaseTerm(String geobaseterm) {
        if (geobaseToGeorefMap.containsKey(geobaseterm)) {
            return (String) geobaseToGeorefMap.get(geobaseterm);
        } else {
            return null;
        }
    }

    private void populateMap() {
        geobaseToGeorefMap = new HashMap<String, String>();

        InputStream in = this.getClass().getClassLoader().getResourceAsStream("org/ei/data/georef/loadtime/GeobaseToGeorefTerms.txt");
        BufferedReader rdr = null;
        try {
            rdr = new BufferedReader(new InputStreamReader(in));
            // rdr = new BufferedReader(new FileReader("geodata/GeobaseToGeorefTerms.txt"));

            if (rdr != null) {
                while (rdr.ready()) {
                    String aline = rdr.readLine();
                    String[] terms = aline.split(",");
                    Matcher m = csvRE.matcher(aline);
                    // For each field
                    int termindex = 0;
                    while (m.find()) {
                        String match = m.group();
                        if (match.endsWith(",")) {  // trim trailing ,
                            match = match.substring(0, match.length() - 1);
                        }
                        if (match.startsWith("\"")) { // assume also ends with
                            match = match.substring(1, match.length() - 1);
                        }
                        if (termindex < 2) {
                            // System.out.println(termindex + ". Match... " + match);
                            terms[termindex++] = match;
                        }
                    }
                    geobaseToGeorefMap.put(terms[0], terms[1]);
                    // System.out.println("Loading... " + terms[0] + ", " + terms[1]);
                }
            }
           else
           {
              System.out.println("Reader is null");
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
