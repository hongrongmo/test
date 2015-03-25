package org.ei.dataloading;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.naming.NamingException;

import org.ei.dataloading.ntis.loadtime.ExtractAuAffNtis;
import org.ei.dataloading.ntis.loadtime.ExtractAuthorsNtis;
import org.ei.dataloading.ntis.loadtime.ExtractCvsNtis;

public class LoadLookupNtis
{


  public static void main(String args[])
    throws Exception
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
      if(args.length < 6)
      {
        System.out.println("\n\nVerify input parameters...");
      }
      else if(args.length >= 6)
      {
        url     = args[0];
        username  = args[1];
        password  = args[2];
        driver    = args[3];
        lookup    = args[4];

        if(!args[5].equalsIgnoreCase("all"))
        {
          load_number_begin = new Integer(args[5]).intValue();
        }
        con = getDbCoonection(url, username, password, driver);
        if(args.length == 7)
        {
          load_number_end   = new Integer(args[6]).intValue();
        }
        if(!(lookup.equalsIgnoreCase("all")))
        {
          if(lookup.equalsIgnoreCase("AUS"))
          {
            ExtractAuthorsNtis extractAuthorsNtis = new ExtractAuthorsNtis();
            extractAuthorsNtis.extract(load_number_begin, load_number_end, con);
          }
          else if(lookup.equalsIgnoreCase("AF"))
          {
            ExtractAuAffNtis extractAuAffNtis = new ExtractAuAffNtis();
            extractAuAffNtis.extract(load_number_begin, load_number_end, con);
          }
          else if(lookup.equalsIgnoreCase("CVS"))
          {
            ExtractCvsNtis extractCvsNtis = new ExtractCvsNtis();
            extractCvsNtis.extract(load_number_begin, load_number_end, con);
          }
          else
          {
            System.out.println("\n\nVerify input parameters...");
          }
        }
        else if(lookup.equalsIgnoreCase("all"))
        {
          ExtractAuthorsNtis extractAuthorsNtis = new ExtractAuthorsNtis();
          extractAuthorsNtis.extract(load_number_begin, load_number_end, con);

          ExtractAuAffNtis extractAuAffNtis = new ExtractAuAffNtis();
          extractAuAffNtis.extract(load_number_begin, load_number_end, con);

          ExtractCvsNtis extractCvsNtis = new ExtractCvsNtis();
          extractCvsNtis.extract(load_number_begin, load_number_end, con);
        }
      }
    }
    catch(NamingException e)
    {
      e.printStackTrace();
    }
    catch(SQLException e)
    {
      e.printStackTrace();
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

  public static Connection getDbCoonection(String url,String username,String password,
  String driver)
    throws Exception
  {
    Class.forName(driver).newInstance();
    Connection con  = DriverManager.getConnection(url, username, password);
    return con;
  }
}
