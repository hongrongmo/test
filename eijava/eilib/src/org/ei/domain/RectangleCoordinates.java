package org.ei.domain;

import java.io.*;
import java.util.regex.*;

import org.ei.data.georef.runtime.GRFDocBuilder;

public class RectangleCoordinates
  implements ElementData
{
  protected String[] coordinates;
  protected Key key;
  protected boolean labels = true;

  public RectangleCoordinates(String[] s)
  {
    this.key = GRFDocBuilder.COORDINATES;
    this.coordinates = s;
  }

  public RectangleCoordinates(Key key, String[] s)
  {
    this.key = key;
    this.coordinates = s;
  }

  public String[] getElementData()
  {
    return this.coordinates;
  }

  public void setElementData(String[] s)
  {
    this.coordinates = s;
  }

  public void exportLabels(boolean labels)
  {
    this.labels = labels;
  }


  private static final String LAT = "(N|S)(\\d{2})(\\d{2})(\\d{2})";
  private static final String LONG = "(E|W)(\\d{3})(\\d{2})(\\d{2})";
  private static final Pattern RECTANGLEPATTERN = Pattern.compile(RectangleCoordinates.LAT + RectangleCoordinates.LAT + RectangleCoordinates.LONG + RectangleCoordinates.LONG);

  public void toXML(Writer out)
    throws IOException
  {
    out.write("<");
    out.write(this.key.getKey());
    out.write(" label=\"");
    out.write(this.key.getLabel());
    out.write("\"");
    out.write(">");

// Latitude. Latitude indicates the number of degrees north or south of the equator (0 degrees). Latitude is indicated by a character string of seven characters: a letter N or S to indicate direction (i.e., north or south of the equator) and a six-digit number indicating the number of degrees, minutes, and seconds. Latitude coordinates are also cascaded to the full degree (i.e., first two digits): N451430 is North, 45 degrees, 14 minutes, 30 seconds; it is also cascaded to N45.
// Longitude. Longitude indicates the number of degrees east or west of the prime meridian (0 degrees). Longitude is indicated by a character string of eight characters: a letter E or W to indicate direction (i.e., east or west of the prime meridian) and a seven-digit number indicating the number of degrees, minutes, and seconds. Longitude coordinates are also cascaded to the full degree (i.e., first three digits): W1211752 is West, 121 degrees, 17 minutes, 52 seconds; it is also cascaded to W121
// Coordinates are assigned as follows: Starting from the lower right-hand corner, a latitude is assigned, followed by the latitude of the upper right-hand corner (counterclockwise), the longitude of that point, and finally the longitude of the upper left-hand corner.
// i.e. N174800N180000W0661000W0664000
// S230000S100000E1530000E1430000
// DEC = DEG + (MIN * 1/60) + (SEC * 1/60 * 1/60)

    StringBuffer locations = new StringBuffer();
    locations.append("<LOCS label=\"Locations\">");

    long xsmall = 0;
    long xlarge = 0;
    long ysmall  = 0;
    long ylarge = 0;

    for(int i = 0; i < coordinates.length; i++)
    {
      String[] termcoordinate = coordinates[i].split(GRFDocBuilder.IDDELIMITER);
      String term = null;
      String coordinate = null;
      if(termcoordinate.length == 1)
      {
        coordinate = termcoordinate[0];
      }
      else
      {
        term = termcoordinate[0];
        coordinate = termcoordinate[1];
      }

      if(coordinate != null)
      {
        Matcher rectangle = RectangleCoordinates.RECTANGLEPATTERN.matcher(coordinate);
        if(rectangle.find())
        {
          String lat1 = String.valueOf(Long.parseLong(rectangle.group(2)) * (rectangle.group(1).equals("S") ? -1 : 1) + (Long.parseLong(rectangle.group(3)) * 1/60) + (Long.parseLong(rectangle.group(4)) * 1/360));
          String lat2 = String.valueOf(Long.parseLong(rectangle.group(6)) * (rectangle.group(5).equals("S") ? -1 : 1) + (Long.parseLong(rectangle.group(7)) * 1/60) + (Long.parseLong(rectangle.group(8)) * 1/360));
          String lng1 = String.valueOf(Long.parseLong(rectangle.group(10)) * (rectangle.group(9).equals("W") ? -1 : 1) + (Long.parseLong(rectangle.group(11)) * 1/60) + (Long.parseLong(rectangle.group(12)) * 1/360));
          String lng2 = String.valueOf(Long.parseLong(rectangle.group(14)) * (rectangle.group(13).equals("W") ? -1 : 1) + (Long.parseLong(rectangle.group(15)) * 1/60) + (Long.parseLong(rectangle.group(16)) * 1/360));

          out.write("<RECT ");
          out.write((term != null) ? (" ID=\"" + term + "\" ") : "");
          out.write(">");
          out.write("<POINT>");
          out.write("<LAT>");out.write(lat1);out.write("</LAT>");out.write("<LONG>");out.write(lng1);out.write("</LONG>");
          out.write("</POINT>");
          out.write("<POINT>");
          out.write("<LAT>");out.write(lat2);out.write("</LAT>");out.write("<LONG>");out.write(lng1);out.write("</LONG>");
          out.write("</POINT>");
          out.write("<POINT>");
          out.write("<LAT>");out.write(lat2);out.write("</LAT>");out.write("<LONG>");out.write(lng2);out.write("</LONG>");
          out.write("</POINT>");
          out.write("<POINT>");
          out.write("<LAT>");out.write(lat1);out.write("</LAT>");out.write("<LONG>");out.write(lng2);out.write("</LONG>");
          out.write("</POINT>");
          out.write("</RECT>");

          locations.append("<LOC ID=\"" + i + "\">");
          // LAT_LOW_RIGHT LAT_UPPER_RIGHT LONG_UPPER_RIGHT LONG_UPPER_LEFT
          locations.append("<![CDATA[");
          for(int x = 0; x < 4; x++)
          {
            int base = x * 4;
            // degress and N/S/E/W
            locations.append(rectangle.group(base + 2) + "&#176;" + rectangle.group(base + 1) + " ");
            // Minutes
            if(!rectangle.group(base + 3).equals("00"))
            {
              locations.append(rectangle.group(base + 3) + "\" ");
              // Seconds
              if(!rectangle.group(base + 4).equals("00"))
              {
                locations.append(rectangle.group(base + 4) + "' ");
              }
            }
          }
          locations.append("]]>");
          locations.append("</LOC>");
        }
      }
    }
    locations.append("</LOCS>");

    out.write("</");
    out.write(key.getKey());
    out.write(">");

    out.write(locations.toString());
  }

}

