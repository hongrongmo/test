package org.ei.data.geobase.loadtime;

import java.sql.Connection;
import java.sql.DriverManager;
import org.ei.data.compendex.loadtime.ExtractAuAffCpx;
import org.ei.data.compendex.loadtime.ExtractAuthorsCpx;
import org.ei.data.compendex.loadtime.ExtractCvsCpx;
import org.ei.data.compendex.loadtime.ExtractPubCpx;
import org.ei.data.compendex.loadtime.ExtractStCpx;
import org.ei.data.inspec.loadtime.ExtractAuAffIns;
import org.ei.data.inspec.loadtime.ExtractAuthorsIns;
import org.ei.data.inspec.loadtime.ExtractPubIns;
import org.ei.data.inspec.loadtime.ExtractStIns;
import org.ei.data.ntis.loadtime.ExtractAuthorsNtis;
import org.ei.data.ntis.loadtime.ExtractCvsNtis;

public class LoadClassification
{
  public static void main(String[] args) throws Exception
  {

    String url    = null;
    String username = null;
    String password = null;
    String driver = null;
    String database = null;
    String lookup = null;
    Connection con  = null;

    int load_number_begin = 0;
    int load_number_end   = 0;

    try
    {

        url       = "jdbc:oracle:thin:@206.137.75.51:1521:EI";
        username  = "ap_ev_search";
        password  = "ei3it";
        driver    = "oracle.jdbc.driver.OracleDriver";
        database  = "geo";
        lookup    = "CLS";

        con = getDbCoonection(url, username, password, driver);
		testExtractCLSCbn classification = new testExtractCLSCbn();
		classification.extract(con);

    }
    finally
    {
      if(con != null)
      {
        try
        {
          con.close();
        }
        catch(Exception e)
        {
          e.printStackTrace();
        }
      }
    }

  }

  public static Connection getDbCoonection(String url,String username,String password, String driver)
    throws Exception
  {
    Class.forName(driver).newInstance();
    Connection con  = DriverManager.getConnection(url, username, password);
    return con;
  }
}