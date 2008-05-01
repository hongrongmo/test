package org.ei.data;

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
import org.ei.data.ntis.loadtime.ExtractAuAffNtis;
import org.ei.data.ntis.loadtime.ExtractAuthorsNtis;
import org.ei.data.ntis.loadtime.ExtractCvsNtis;
import org.ei.data.upt.loadtime.ExtractInventorsUpt;
import org.ei.data.upt.loadtime.ExtractAssigneeUpt;
import org.ei.data.geobase.loadtime.ExtractAuAffGeo;
import org.ei.data.geobase.loadtime.ExtractAuthorsGeo;
import org.ei.data.geobase.loadtime.ExtractCvsGeo;
import org.ei.data.geobase.loadtime.ExtractStGeo;
import org.ei.data.geobase.loadtime.ExtractPnGeo;
import org.ei.data.chem.loadtime.ExtractAuAffChm;
import org.ei.data.chem.loadtime.ExtractAuthorsChm;
import org.ei.data.chem.loadtime.ExtractPubChm;
import org.ei.data.chem.loadtime.ExtractStChm;
import org.ei.data.cbnb.loadtime.ExtractPubCbn;
import org.ei.data.cbnb.loadtime.ExtractStCbn;
import org.ei.data.encompasslit.loadtime.ExtractAuAffElt;
import org.ei.data.encompasslit.loadtime.ExtractAuthorsElt;
import org.ei.data.encompasslit.loadtime.ExtractStElt;
import org.ei.data.encompasslit.loadtime.ExtractPubElt;
import org.ei.data.encompasspat.loadtime.ExtractPatAssigneeEpt;
import org.ei.data.encompasspat.loadtime.ExtractAuthorsEpt;
import org.ei.data.paper.loadtime.ExtractAuAffPch;
import org.ei.data.paper.loadtime.ExtractAuthorsPch;
import org.ei.data.paper.loadtime.ExtractPubPch;
import org.ei.data.paper.loadtime.ExtractStPch;
import org.ei.data.geobase.loadtime.*;
import org.ei.data.georef.loadtime.*;
import org.ei.data.compendex.loadtime.*;

public class LoadLookup
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
      if(args.length < 7)
      {
        System.out.println("\n\nVerify Your Parameters...For Example... "+
          "\nParm1: url - jdbc:oracle:thin:@stage.ei.org:1521:apl2 "+
          "\nParm2: username - e_java /i_java"+
          "\nParm3: password - team "+
          "\nParm4: Driver - oracle.jdbc.driver.OracleDriver "+
          "\nParm5: Database - cpx / ins "+
          "\nParm6: Lookup  - ALL // AUS/AF/PUB/ST "+
          "\nParm7: Load Number Begin - 200310"+
          "\nParm8: Load Number End - 200320");
      }

        url     = args[0];
        username  = args[1];
        password  = args[2];
        driver    = args[3];
        database  = args[4];
        lookup    = args[5];

        load_number_begin = new Integer(args[6]).intValue();
        con = getDbCoonection(url, username, password, driver);
        boolean all = false;

        if(lookup.equalsIgnoreCase("all")){
            all=true;
        }


      if(args.length == 8)
      {
        load_number_end   = new Integer(args[7]).intValue();
      }


        if(database.equalsIgnoreCase("geo"))
        {
          if(lookup.equalsIgnoreCase("AUS")|| all)
          {
            ExtractAuthorsGeo extractAuthorsGeo = new ExtractAuthorsGeo();
            extractAuthorsGeo.extract(load_number_begin, load_number_end, con,database);
          }
          if(lookup.equalsIgnoreCase("AF")|| all)
          {
            ExtractAuAffGeo extractAuAffGeo = new ExtractAuAffGeo();
            extractAuAffGeo.extract(load_number_begin, load_number_end, con,database);
          }
          if(lookup.equalsIgnoreCase("CVS")|| all)
          {
            ExtractCvsGeo extractCvsGeo = new ExtractCvsGeo();
            extractCvsGeo.extract(load_number_begin, load_number_end, con,database);
          }
          if(lookup.equalsIgnoreCase("ST")|| all)
          {
            ExtractStGeo extractStGeo = new ExtractStGeo();
            extractStGeo.extract(load_number_begin, load_number_end, con,database);
          }
          if(lookup.equalsIgnoreCase("PN")|| all)
          {
            ExtractPnGeo extractPnGeo = new ExtractPnGeo();
            extractPnGeo.extract(load_number_begin, load_number_end, con,database);
          }

        }
        else if(database.equalsIgnoreCase("cpx"))
        {
          if(lookup.equalsIgnoreCase("AUS")|| all)
          {
            ExtractAuthorsCpx extractAuthorsCpx = new ExtractAuthorsCpx();
            extractAuthorsCpx.extract(load_number_begin, load_number_end, con,database);
          }
          if(lookup.equalsIgnoreCase("AF")|| all)
          {
            ExtractAuAffCpx extractAuAffCpx = new ExtractAuAffCpx();
            extractAuAffCpx.extract(load_number_begin, load_number_end, con,database);
          }
          if(lookup.equalsIgnoreCase("PUB")|| all)
          {
            ExtractPubCpx extractPubCpx = new ExtractPubCpx();
            extractPubCpx.extract(load_number_begin, load_number_end, con,database);
          }
          if(lookup.equalsIgnoreCase("ST")|| all)
          {
            ExtractStCpx extractStCpx = new ExtractStCpx();
            extractStCpx.extract(load_number_begin, load_number_end, con,database);
          }

        }

        else if(database.equalsIgnoreCase("ins"))
        {
          if(lookup.equalsIgnoreCase("AUS")|| all)
          {
            ExtractAuthorsIns extractAuthorsIns = new ExtractAuthorsIns();
            extractAuthorsIns.extract(load_number_begin, load_number_end, con);
          }
          if(lookup.equalsIgnoreCase("AF")|| all)
          {
            ExtractAuAffIns extractAuAffIns = new ExtractAuAffIns();
            extractAuAffIns.extract(load_number_begin, load_number_end, con);
          }
          if(lookup.equalsIgnoreCase("PUB")|| all)
          {
            ExtractPubIns extractPubIns = new ExtractPubIns();
            extractPubIns.extract(load_number_begin, load_number_end, con);
          }
          if(lookup.equalsIgnoreCase("ST")|| all)
          {
            ExtractStIns extractStIns = new ExtractStIns();
            extractStIns.extract(load_number_begin, load_number_end, con);
          }

        }

        else if(database.equalsIgnoreCase("ntis"))
        {
          if(lookup.equalsIgnoreCase("AUS")|| all)
          {
            ExtractAuthorsNtis extractAuthorsNtis = new ExtractAuthorsNtis();
            extractAuthorsNtis.extract(load_number_begin, load_number_end, con);
          }
          if(lookup.equalsIgnoreCase("AF")|| all)
          {
            ExtractAuAffNtis extractAuAffNtis = new ExtractAuAffNtis();
            extractAuAffNtis.extract(load_number_begin, load_number_end, con);
          }
          if(lookup.equalsIgnoreCase("CVS")|| all)
          {
            ExtractCvsNtis extractCvsNtis = new ExtractCvsNtis();
            extractCvsNtis.extract(load_number_begin, load_number_end, con);
          }

        }
        else if(database.equalsIgnoreCase("upt"))
        {
          if(lookup.equalsIgnoreCase("INV")|| all)
          {
            ExtractInventorsUpt extractinventorsUpt = new ExtractInventorsUpt();
            extractinventorsUpt.extract(load_number_begin, load_number_end, con);
          }
          if(lookup.equalsIgnoreCase("ASG")|| all)
          {
            ExtractAssigneeUpt extractassigneeUpt = new ExtractAssigneeUpt();
            extractassigneeUpt.extract(load_number_begin, load_number_end, con);
          }

        }
        else if(database.equalsIgnoreCase("chm"))
        {
          if(lookup.equalsIgnoreCase("AUS")|| all)
          {
            ExtractAuthorsChm extractAuthorsChm = new ExtractAuthorsChm();
            extractAuthorsChm.extract(load_number_begin, load_number_end, con);
          }
          if(lookup.equalsIgnoreCase("AF")|| all)
          {
            ExtractAuAffChm extractAuAffChm = new ExtractAuAffChm();
            extractAuAffChm.extract(load_number_begin, load_number_end, con);
          }
          if(lookup.equalsIgnoreCase("PUB")|| all)
          {
            ExtractPubChm extractPubChm = new ExtractPubChm();
            extractPubChm.extract(load_number_begin, load_number_end, con);
          }
          if(lookup.equalsIgnoreCase("ST")|| all)
          {
            ExtractStChm extractStChm = new ExtractStChm();
            extractStChm.extract(load_number_begin, load_number_end, con);
          }

        }
        else if(database.equalsIgnoreCase("cbn"))
        {
          if(lookup.equalsIgnoreCase("PUB")|| all)
          {
            ExtractPubCbn extractPubCbn = new ExtractPubCbn();
            extractPubCbn.extract(load_number_begin, load_number_end, con);
          }
          if(lookup.equalsIgnoreCase("ST")|| all)
          {
            ExtractStCbn extractStCbn = new ExtractStCbn();
            extractStCbn.extract(load_number_begin, load_number_end, con);
          }

        }
        else if(database.equalsIgnoreCase("elt"))
        {
              if(lookup.equalsIgnoreCase("AUS") || all)
              {
                ExtractAuthorsElt extractAuthorsElt = new ExtractAuthorsElt();
                extractAuthorsElt.extract(load_number_begin, load_number_end, con);
              }
              if(lookup.equalsIgnoreCase("AF")|| all)
              {
                ExtractAuAffElt extractAuAffElt = new ExtractAuAffElt();
                extractAuAffElt.extract(load_number_begin, load_number_end, con);
              }
              if(lookup.equalsIgnoreCase("ST")|| all)
              {
                ExtractStElt extractStElt = new ExtractStElt();
                extractStElt.extract(load_number_begin, load_number_end, con);
              }
              if(lookup.equalsIgnoreCase("PN")|| all)
              {
                ExtractPubElt extractPubElt = new ExtractPubElt();
                extractPubElt.extract(load_number_begin, load_number_end, con);
              }
        }
        else if(database.equalsIgnoreCase("ept"))
        {
          if(lookup.equalsIgnoreCase("AUS") || all)
          {
            ExtractAuthorsEpt extractAuthorsEpt = new ExtractAuthorsEpt();
            extractAuthorsEpt.extract(load_number_begin, load_number_end, con);
          }
          if(lookup.equalsIgnoreCase("AF")|| all)
          {
            ExtractPatAssigneeEpt extractAuAffEpt = new ExtractPatAssigneeEpt();
            extractAuAffEpt.extract(load_number_begin, load_number_end, con);
          }
        }
        else if(database.equalsIgnoreCase("pch"))
        {
          if(lookup.equalsIgnoreCase("AUS")|| all)
          {
            ExtractAuthorsPch extractAuthorsPch = new ExtractAuthorsPch();
            extractAuthorsPch.extract(load_number_begin, load_number_end, con);
          }
          if(lookup.equalsIgnoreCase("AF")|| all)
          {
            ExtractAuAffPch extractAuAffPch = new ExtractAuAffPch();
            extractAuAffPch.extract(load_number_begin, load_number_end, con);
          }
          if(lookup.equalsIgnoreCase("PUB")|| all)
          {
            ExtractPubPch extractPubPch = new ExtractPubPch();
            extractPubPch.extract(load_number_begin, load_number_end, con);
          }
          if(lookup.equalsIgnoreCase("ST")|| all)
          {
            ExtractStPch extractStPch = new ExtractStPch();
            extractStPch.extract(load_number_begin, load_number_end, con);
          }

        }
		else if(database.equalsIgnoreCase("georef"))
		{
		  if(lookup.equalsIgnoreCase("AUS")|| all)
		  {
			ExtractAuthorsGrf extractAuthorsGrf = new ExtractAuthorsGrf();
			extractAuthorsGrf.extract(load_number_begin, load_number_end, con,database);
		  }
		  else if(lookup.equalsIgnoreCase("AF")|| all)
		  {
			ExtractAuAffGrf extractAuAffGrf = new ExtractAuAffGrf();
			extractAuAffGrf.extract(load_number_begin, load_number_end, con,database);
		  }
		  else if(lookup.equalsIgnoreCase("CVS")|| all)
		  {
			ExtractIndexTermsGrf extractIndexTermsGrf = new ExtractIndexTermsGrf();
			extractIndexTermsGrf.extract(load_number_begin, load_number_end, con,database);
		  }
		  else if(lookup.equalsIgnoreCase("ST")|| all)
		  {
			ExtractSerialTitleGrf extractSerialTitleGrf = new ExtractSerialTitleGrf();
			extractSerialTitleGrf.extract(load_number_begin, load_number_end, con,database);
		  }
		  else if(lookup.equalsIgnoreCase("PN")|| all)
		  {
		  	ExtractPnGrf extractPnGrf = new ExtractPnGrf();
		  	extractPnGrf.extract(load_number_begin, load_number_end, con,database);
		  }
		  else if(lookup.equalsIgnoreCase("LA")|| all)
		  {
		  	ExtractLangGrf extractLangGrf = new ExtractLangGrf();
		  	extractLangGrf.extract(load_number_begin, load_number_end, con,database);
		  }
        }
        else
        {
          System.out.println("\n\nVerify Parm1:Database is cpx / ins / ntis / chm / cbn / geo / elt / ept / georef");
        }



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

  public static String removeSpecialCharacter(String input)
  {
        StringBuffer outputStringbuffer = new StringBuffer();
        String outputString = null;
        if(input !=null && input.length()>0 && input.charAt(0)=='(')
        {
            char[] ch = input.toCharArray();
            int  charFlag = 0;
            for(int i=1;i<ch.length;i++)
            {
                if(ch[i]=='(')
                {
                    charFlag++;
                }
                if(ch[i] == ')')
                {
                    charFlag--;
                }

                if(charFlag >= 0)
                {
                    outputStringbuffer.append(ch[i]);
                }
            }
            outputString = outputStringbuffer.toString();
        }
        else
        {
            outputString = input;
        }
        return outputString;
    }

  public static Connection getDbCoonection(String url,String username,String password, String driver)
    throws Exception
  {
    Class.forName(driver).newInstance();
    Connection con  = DriverManager.getConnection(url, username, password);
    return con;
  }
}