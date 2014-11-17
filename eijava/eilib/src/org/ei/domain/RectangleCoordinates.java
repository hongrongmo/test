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
    this.key = Keys.COORDINATES;
    this.coordinates = s;
  }

	public void setKey(Key akey)
	{
	  this.key = akey;
	}
	public Key getKey() { return this.key; }

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

  private static final String LAT = "(N|S)(\\d+)";
  private static final String LONG = "(E|W)(\\d+)";
  private static final Pattern RECTANGLEPATTERN = Pattern.compile(RectangleCoordinates.LAT + RectangleCoordinates.LAT + RectangleCoordinates.LONG + RectangleCoordinates.LONG);

  public void toXML(Writer out)
    throws IOException
  {
// Latitude. Latitude indicates the number of degrees north or south of the equator (0 degrees). Latitude is indicated by a character string of seven characters: a letter N or S to indicate direction (i.e., north or south of the equator) and a six-digit number indicating the number of degrees, minutes, and seconds. Latitude coordinates are also cascaded to the full degree (i.e., first two digits): N451430 is North, 45 degrees, 14 minutes, 30 seconds; it is also cascaded to N45.
// Longitude. Longitude indicates the number of degrees east or west of the prime meridian (0 degrees). Longitude is indicated by a character string of eight characters: a letter E or W to indicate direction (i.e., east or west of the prime meridian) and a seven-digit number indicating the number of degrees, minutes, and seconds. Longitude coordinates are also cascaded to the full degree (i.e., first three digits): W1211752 is West, 121 degrees, 17 minutes, 52 seconds; it is also cascaded to W121
// Coordinates are assigned as follows: Starting from the lower right-hand corner, a latitude is assigned, followed by the latitude of the upper right-hand corner (counterclockwise), the longitude of that point, and finally the longitude of the upper left-hand corner.
// i.e. N174800N180000W0661000W0664000
// S230000S100000E1530000E1430000
// DEC = DEG + (MIN * 1/60) + (SEC * 1/60 * 1/60)

    long xsmall = 0;
    long xlarge = 0;
    long ysmall  = 0;
    long ylarge = 0;

    out.write("<LOCS label=\"Coordinates\">");

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
          out.write("<LOC " + ((term != null) ?  " ID=\"" + term + "\" " : "") + ">");
          // LAT_LOW_RIGHT LAT_UPPER_RIGHT LONG_UPPER_RIGHT LONG_UPPER_LEFT
          out.write("<![CDATA[");
          for(int x = 0; x < 4; x++)
          {
            int base = x * 2;
            // degress and N/S/E/W
            out.write(rectangle.group(base + 2) + rectangle.group(base + 1) + " ");
          }
          out.write("]]>");
          out.write("</LOC>");
        }
      }
    }
    out.write("</LOCS>");
  }
}

