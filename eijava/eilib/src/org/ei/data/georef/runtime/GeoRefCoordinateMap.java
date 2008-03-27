package org.ei.data.georef.runtime;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ei.data.georef.loadtime.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class GeoRefCoordinateMap
{
  protected static Log log = LogFactory.getLog(GeoRefCoordinateMap.class);

  private Map georefMapCoordinates = null;
  private Map georefRawCoordinates = null;

  private static GeoRefCoordinateMap instance = null;

  private static Pattern csvRE;
  public static final String CSV_PATTERN = "\"([^\"]+?)\",?|([^,]+),?|,";

  private static Pattern coordsRE;
  public static final String COORDS_PATTERN = "(N|S)(\\d+)\\-(N|S)(\\d+)\\s+(E|W)(\\d+)\\-(E|W)(\\d+)";

  private GeoRefCoordinateMap()
  {
    //GeoRefCoordinateMap.csvRE = Pattern.compile(GeoRefCoordinateMap.CSV_PATTERN);
  }

  public static GeoRefCoordinateMap getInstance() {
    if(instance == null)
    {
      synchronized (GeoRefCoordinateMap.class) {
        instance = new GeoRefCoordinateMap();
        instance.populateMap();
      }
    }
    return instance;
  }

  public String lookupGeoRefTermCoordinates(String geoterm)
  {
    geoterm = geoterm.toLowerCase();

    if(georefMapCoordinates.containsKey(geoterm))
    {
      log.debug("lookup found " + geoterm);
      return (String) ((Rectangle) georefMapCoordinates.get(geoterm)).toXML();
    }
    else
    {
      log.debug("lookup failed " + geoterm);
      return null;
    }
  }

  public String lookupGeoBaseTermRawCoordinates(String geoterm)
  {
    // look for term in GeoBase to GeoRef
    GeobaseToGeorefMap georefLookup = GeobaseToGeorefMap.getInstance();
    String georefterm = georefLookup.lookupGeobaseTerm(geoterm);
    if(georefterm != null)
    {
      log.debug(geoterm + " GEOBASE term translated to GeoRef term " + georefterm);
      geoterm = georefterm;
    }

    geoterm = geoterm.toLowerCase();

    if(georefRawCoordinates.containsKey(geoterm))
    {
      log.debug(" RawCoordinates lookup found " + geoterm + " == " + georefRawCoordinates.get(geoterm));
      return (String) georefRawCoordinates.get(geoterm);
    }
    else
    {
      log.debug(" RawCoordinates lookup failed " + geoterm);
      return null;
    }
  }

  private void populateMap() {
    georefMapCoordinates = new HashMap();
    georefRawCoordinates = new HashMap();

    //InputStream in = this.getClass().getClassLoader().getResourceAsStream("org/ei/data/georef/runtime/GeoRefCoodinates.txt");
    BufferedReader rdr = null;
    try {
      rdr = new BufferedReader(new FileReader("geodata/GeoRefCoodinates.txt"));

      if(rdr != null)
      {
        while(rdr.ready())
        {
          String aline = rdr.readLine();

          int coordstart = aline.lastIndexOf(",");
          String term = aline.substring(0,coordstart);
          String coords = aline.substring(coordstart+1);
          // Load georefMapCoordinates with coordinates for SearchResults Google Mapping
          Rectangle r = (new Rectangle(coords));
          georefMapCoordinates.put(term.trim().toLowerCase(),r);
          // Load georefRawCoordinates with coordinates for Abstarct/Detailed Mapping
          georefRawCoordinates.put(term.trim().toLowerCase(),coords.replaceAll("\\W",""));
/*
          String[] terms = new String[2];// aline.split(",");
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
*/
          log.debug("Loading... " + term.trim().toLowerCase() + ", " + r);

        } // while
        System.out.println("Done loading terms...");
      }
      else
      {
        log.error("Reader is null");
        System.out.println("Reader is null");
      }
    }
    catch(FileNotFoundException e) {
      System.out.println("File Not found.  Look for geodata subdirectory.");
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
      //log.debug("Rectange Coords = " + coords);

      coords = coords.trim();
      int middlespace = coords.indexOf(" ");
      String strlat = coords.substring(0,middlespace);
      String strlng = coords.substring(middlespace+1);

      int latsplit = strlat.indexOf("-");
      int lngsplit = strlng.indexOf("-");

      String strlat1 = strlat.substring(0,latsplit);
      String strlat2 = strlat.substring(latsplit+1);
      String strlng1 = strlng.substring(0,lngsplit);
      String strlng2 = strlng.substring(lngsplit+1);

      int lat1 = Integer.parseInt(strlat1.substring(1,3));
      int lat2 = Integer.parseInt(strlat2.substring(1,3));
      int lng1 = Integer.parseInt(strlng1.substring(1,4));
      int lng2 = Integer.parseInt(strlng2.substring(1,4));
/*      lat1 = (lat1 > 90) ? (lat1/100) : lat1;
      lat2 = (lat2 > 90) ? (lat2/100) : lat2;
      lng1 = (lng1 > 180) ? (lng1/100) : lng1;
      lng2 = (lng2 > 180) ? (lng2/100) : lng2;*/
      lat1 = (strlat1.charAt(0) == 83) ? lat1 * -1 : (lat1);
      lat2 = (strlat2.charAt(0) == 83) ? lat2 * -1 : (lat2);
      lng1 = (strlng1.charAt(0) == 87) ? lng1 * -1 : (lng1);
      lng2 = (strlng2.charAt(0) == 87) ? lng2 * -1 : (lng2);
      sw = new LatLng(lat1,lng2);
      ne = new LatLng(lat2,lng1);

      //log.debug(this.toString());
/*
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
*/
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