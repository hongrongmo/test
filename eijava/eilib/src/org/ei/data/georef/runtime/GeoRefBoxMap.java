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

public class GeoRefBoxMap
{
  protected static Log log = LogFactory.getLog(GeoRefBoxMap.class);
  private static GeoRefBoxMap instance = null;
  private Map georefMapCoordinates = null;

  private GeoRefBoxMap()
  {
  }

  public static GeoRefBoxMap getInstance() {
    if(instance == null)
    {
      synchronized (GeoRefBoxMap.class) {
        instance = new GeoRefBoxMap();
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
      return (String) georefMapCoordinates.get(geoterm);
    }
    else
    {
      return null;
    }
  }

  private void populateMap() {
    georefMapCoordinates = new HashMap();

    BufferedReader rdr = null;
    try {
      rdr = new BufferedReader(new FileReader("/export/home/hestevez/GeoRefCoodinates.txt"));

      if(rdr != null)
      {
        while(rdr.ready())
        {
          String aline = rdr.readLine();

          String[] coords = aline.split(",");
          georefMapCoordinates.put(coords[0].trim().toLowerCase(),coords[1].replaceAll("[- ]",""));

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
}
