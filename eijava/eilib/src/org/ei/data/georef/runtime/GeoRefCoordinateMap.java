package org.ei.data.georef.runtime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GeoRefCoordinateMap
{
  protected static Log log = LogFactory.getLog(GeoRefCoordinateMap.class);

  private Map georefMapCoordinates = null;
  private static GeoRefCoordinateMap instance = null;
  private static Pattern csvRE;
  public static final String CSV_PATTERN = "\"([^\"]+?)\",?|([^,]+),?|,";

  private static Pattern coordsRE;
  public static final String COORDS_PATTERN = "(N|S)(\\d+)\\-(N|S)(\\d+)\\s+(E|W)(\\d+)\\-(E|W)(\\d+)";

  private GeoRefCoordinateMap()
  {
    GeoRefCoordinateMap.csvRE = Pattern.compile(GeoRefCoordinateMap.CSV_PATTERN);
    populateMap();
  }

  public static GeoRefCoordinateMap getInstance() {
    if(instance == null)
    {
      synchronized (GeoRefCoordinateMap.class) {
        instance = new GeoRefCoordinateMap();
      }
    }
    return instance;
  }

  public String lookupGeoRefTermCoordinates(String geoterm)
  {
    geoterm = geoterm.toLowerCase();

    if(georefMapCoordinates.containsKey(geoterm))
    {
      return (String) ((Rectangle) georefMapCoordinates.get(geoterm)).toXML();
    }
    else
    {
      return null;
    }
  }

  private void populateMap() {
    georefMapCoordinates = new HashMap();

    InputStream in = this.getClass().getClassLoader().getResourceAsStream("org/ei/data/georef/runtime/GeoRefCoodinates.txt");
    BufferedReader rdr = new BufferedReader(new InputStreamReader(in));
    try {
      if(rdr != null)
      {
        while(rdr.ready())
        {
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
            if(termindex < 2)
            {
              log.debug(termindex + ". Match... " + match);
              terms[termindex++] = match;
            }
          }
          Rectangle r = (new Rectangle(terms[1]));
          georefMapCoordinates.put(terms[0].toLowerCase(),r);
          log.info("Loading... " + terms[0] + ", " + r);
        }
      }
      else
      {
        log.error("Reader is null");
        log.error("Reader is null");
        System.out.println("Reader is null");
      }
    }
    catch(IOException e) {
      e.printStackTrace();
    }
    finally {
      try {
        rdr.close();
      }
      catch(IOException e) {
        e.printStackTrace();
      }
    }

    return;
  }


  public class LatLng implements Comparable {
    private int lat = 0;
    private int lng = 0;

    public int hashCode() {
      // TODO Auto-generated method stub
      return lat * lng;
    }

    public boolean equals(Object obj) {
      // TODO Auto-generated method stub
      return (this.compareTo(obj)) == 0;
    }
    public LatLng(int lat, int lng)
    {
      this.lat = lat;
      this.lng = lng;
    }
    public String toString()
    {
      return "(" + lat + ", " + lng + ")";
    }
    public String toXML()
    {
      return "<Point><coordinates>" +  lng + ","+ lat  + ",0</coordinates></Point>";
    }
    public int getLat()
    {
      return this.lat;
    }
    public int getLng()
    {
      return this.lng;
    }
    public int compareTo(Object o) {
      LatLng apoint = (LatLng) o;
      if(apoint.getLat() == this.getLat())
      {
        if(apoint.getLng() == this.getLng())
        {
          return 0;
        }
      }
      return 1;
    }
  }

  public class Rectangle {
    private LatLng sw;
    private LatLng ne;

    public Rectangle(String coords)
    {
      coordsRE = Pattern.compile(COORDS_PATTERN);
      Matcher m = coordsRE.matcher(coords);
      if(m.find())
      {
        //(N)(5000)-(N)(5030) (E)(00720)-(E)(00620)
        // Leg 115,$CO S131002-N050454 E0734953-E05901

        int lat1 = Integer.parseInt(m.group(2))/100;
        int lat2 = Integer.parseInt(m.group(4))/100;
        int lng1 = Integer.parseInt(m.group(6))/100;
        int lng2 = Integer.parseInt(m.group(8))/100;
        lat1 = (lat1 > 90) ? (lat1/100) : lat1;
        lat2 = (lat2 > 90) ? (lat2/100) : lat2;
        lng1 = (lng1 > 180) ? (lng1/100) : lng1;
        lng1 = (lng1 > 180) ? (lng1/100) : lng1;

        lat1 = (m.group(1).equalsIgnoreCase("S")) ? lat1 * -1 : (lat1);
        lat2 = (m.group(3).equalsIgnoreCase("S")) ? lat2 * -1 : (lat2);
        lng1 = (m.group(5).equalsIgnoreCase("W")) ? lng1 * -1 : (lng1);
        lng2 = (m.group(7).equalsIgnoreCase("W")) ? lng2 * -1 : (lng2);
        sw = new LatLng(lat1,lng2);
        ne = new LatLng(lat2,lng1);
      }
      else
      {
        log.error("Error on:" + coords);
      }
    }
    public boolean containsLatLng(LatLng pt)
    {
      boolean result = false;

      if((sw.getLat() <= pt.getLat()) && (ne.getLat() >= pt.getLat())) {
        if((sw.getLng() <= pt.getLng()) && (ne.getLng() >= pt.getLng())) {
          result = true;
        }
      }
      return result;
    }
    public String toString()
    {
      return "SW " + ((sw != null) ? sw.toString() : "null") + " NE " + ((ne != null) ? ne.toString() : "null")  + "] ";
    }
    public String toXML()
    {
      return "<RECT>" + sw.toXML() + ne.toXML() + "</RECT>";
    }
  }
}