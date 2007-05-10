package org.ei.data.geobase;

import java.sql.Connection;
import java.sql.DriverManager;
import org.ei.data.compendex.ExtractAuAffCpx;
import org.ei.data.compendex.ExtractAuthorsCpx;
import org.ei.data.compendex.ExtractCvsCpx;
import org.ei.data.compendex.ExtractPubCpx;
import org.ei.data.compendex.ExtractStCpx;
import org.ei.data.inspec.ExtractAuAffIns;
import org.ei.data.inspec.ExtractAuthorsIns;
import org.ei.data.inspec.ExtractPubIns;
import org.ei.data.inspec.ExtractStIns;
import org.ei.data.ntis.ExtractAuAffNtis;
import org.ei.data.ntis.ExtractAuthorsNtis;
import org.ei.data.ntis.ExtractCvsNtis;

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