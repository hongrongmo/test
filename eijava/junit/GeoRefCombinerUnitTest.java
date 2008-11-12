package org.ei.junit;

import junit.framework.TestCase;
import java.io.*;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;
import java.util.regex.*;

import org.ei.data.*;
import org.ei.data.georef.runtime.*;
import org.ei.domain.*;
import org.ei.util.StringUtil;
import org.ei.util.GUID;
import org.ei.data.georef.loadtime.*;
import org.ei.util.Base64Coder;

import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.MatchResult;

import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.GZIPInputStream;


public class GeoRefCombinerUnitTest extends TestCase {

	private static final Database GRF_DATABASE = new GRFDatabase();

    public static void main(String args[])
    {
    	org.junit.runner.JUnitCore.main("org.ei.junit.GeoRefCombinerUnitTest");
    }

	protected void setUp() {

		try
		{
			/*
			String driver = null;
			String url  = null;
			String username = null;
			String password = null;
			url = "jdbc:oracle:thin:@206.137.75.53:1521:eidb1";
			driver  = "oracle.jdbc.driver.OracleDriver";
			username  = "ap_ev_search";
			password  = "ei3it";
			int loadNumber = 200845;
			int recsPerbatch = 100;
			int exitAt = 9150;
			String tablename = "georef_master";

			Combiner.TABLENAME = tablename;
			Combiner.EXITNUMBER = exitAt;

			CombinedWriter writer = new CombinedXMLWriter(recsPerbatch,
														  loadNumber,
														  GRF_DATABASE.getIndexName());

			GeoRefCombiner c = new GeoRefCombiner(writer);

			  c.writeCombinedByWeekNumber(url,
										  driver,
										  username,
										  password,
										  loadNumber);
										  */
		}

		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	protected void tearDown() {

		try
		{
			//broker.closeConnections();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public void testGeorefCombiner() throws Exception
	{
		unzipXMLFile();
		BufferedReader in = new BufferedReader(new FileReader("C:\\baja\\eijava\\junit\\fast\\batch_200845_0001\\EIDATA\\grf_1975b5911d6357b468M7d3c2061377553_1.xml.gz.xml"));
        String str;
        while ((str = in.readLine()) != null)
        {
            if(str.indexOf("PARENTID") != -1)
            {
            	assertTrue(str.trim().equals("<PARENTID>441134</PARENTID>"));
			}
            else if(str.indexOf("INTPATENTCLASSIFICATION") != -1)
            {
            	assertTrue(str.trim().equals("<INTPATENTCLASSIFICATION><![CDATA[QQDelQQ Greenland QQDelQQ Siberia QQDelQQ Sweden QQDelQQ Taymyr Peninsula QQDelQQ Yakutia Russian Federation QQDelQQ Yamal-Nenets Russian Federation QQDelQQ]]></INTPATENTCLASSIFICATION>"));
			}
            else if(str.indexOf("LAT_NW") != -1)
            {
            	assertTrue(str.trim().equals("<LAT_NW><![CDATA[84]]></LAT_NW>"));
			}
            else if(str.indexOf("LNG_NW") != -1)
            {
            	assertTrue(str.trim().equals("<LNG_NW><![CDATA[-70]]></LNG_NW>"));
			}
            else if(str.indexOf("LAT_NE") != -1)
            {
            	assertTrue(str.trim().equals("<LAT_NE><![CDATA[84]]></LAT_NE>"));
			}
            else if(str.indexOf("LNG_NE") != -1)
            {
            	assertTrue(str.trim().equals("<LNG_NE><![CDATA[-20]]></LNG_NE>"));
			}
            else if(str.indexOf("LAT_SW") != -1)
            {
            	assertTrue(str.trim().equals("<LAT_SW><![CDATA[60]]></LAT_SW>"));
			}
            else if(str.indexOf("LNG_SW") != -1)
            {
            	assertTrue(str.trim().equals("<LNG_SW><![CDATA[-70]]></LNG_SW>"));
			}
            else if(str.indexOf("LAT_SE") != -1)
            {
            	assertTrue(str.trim().equals("<LAT_SE><![CDATA[60]]></LAT_SE>"));
			}
            else if(str.indexOf("LNG_SE") != -1)
            {
            	assertTrue(str.trim().equals("<LNG_SE><![CDATA[-20]]></LNG_SE>"));
			}
        }
        in.close();

	}

	private void unzipXMLFile() throws Exception
	{
		Enumeration zipEntries = null;
		File dir = new File("C:\\baja\\eijava\\junit\\fast\\batch_200845_0001\\EIDATA");
		assertTrue(dir.exists());
		File[] children = dir.listFiles();
		String zipFileName = children[1].getAbsolutePath();
		ZipFile zipFile = new ZipFile(zipFileName);
		zipEntries = zipFile.entries();
		int zipCount = 0;

		if(zipEntries.hasMoreElements())
		{
			ZipEntry entry = (ZipEntry)zipEntries.nextElement();
			copyInputStream(zipFile.getInputStream(entry),
			new BufferedOutputStream(new FileOutputStream(dir.getAbsolutePath() + "\\" + entry.getName())));
			GZIPInputStream in = null;
			in = new GZIPInputStream(new FileInputStream(dir.getAbsolutePath() + "\\" + entry.getName()));
			FileOutputStream out = null;
			out = new FileOutputStream(dir.getAbsolutePath() + "\\" + entry.getName() + ".xml");
 			byte[] buf = new byte[1024];
            int len;
            while((len = in.read(buf)) > 0)
            {
                out.write(buf, 0, len);
            }

			in.close();
            out.close();
		}

		zipFile.close();
	}
  public static final void copyInputStream(InputStream in, OutputStream out)
  throws IOException
  {
    byte[] buffer = new byte[1024];
    int len;

    while((len = in.read(buffer)) >= 0)
      out.write(buffer, 0, len);

    in.close();
    out.close();
  }

}