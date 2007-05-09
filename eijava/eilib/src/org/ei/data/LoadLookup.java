package org.ei.data;

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
import org.ei.data.upt.ExtractInventorsUpt;
import org.ei.data.upt.ExtractAssigneeUpt;
import org.ei.data.geobase.ExtractAuAffGeo;
import org.ei.data.geobase.ExtractAuthorsGeo;
import org.ei.data.geobase.ExtractCvsGeo;
import org.ei.data.geobase.ExtractStGeo;
import org.ei.data.geobase.ExtractPnGeo;

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
      else if(args.length >= 7)
      {
        url     = args[0];
        username  = args[1];
        password  = args[2];
        driver    = args[3];
        database  = args[4];
        lookup    = args[5];

        load_number_begin = new Integer(args[6]).intValue();
        con = getDbCoonection(url, username, password, driver);
      }

      if(args.length == 8)
      {
        load_number_end   = new Integer(args[7]).intValue();
      }

      if(!(lookup.equalsIgnoreCase("all")))
      {
		if(database.equalsIgnoreCase("geo"))
		{
		  if(lookup.equalsIgnoreCase("AUS"))
		  {
			ExtractAuthorsGeo extractAuthorsGeo = new ExtractAuthorsGeo();
			extractAuthorsGeo.extract(load_number_begin, load_number_end, con,database);
		  }
		  else if(lookup.equalsIgnoreCase("AF"))
		  {
			ExtractAuAffGeo extractAuAffGeo = new ExtractAuAffGeo();
			extractAuAffGeo.extract(load_number_begin, load_number_end, con,database);
		  }
		  else if(lookup.equalsIgnoreCase("CVS"))
		  {
			ExtractCvsGeo extractCvsGeo = new ExtractCvsGeo();
			extractCvsGeo.extract(load_number_begin, load_number_end, con,database);
		  }
		  else if(lookup.equalsIgnoreCase("ST"))
		  {
			ExtractStGeo extractStGeo = new ExtractStGeo();
			extractStGeo.extract(load_number_begin, load_number_end, con,database);
		  }
		  else if(lookup.equalsIgnoreCase("PN"))
		  {
		  	ExtractPnGeo extractPnGeo = new ExtractPnGeo();
		  	extractPnGeo.extract(load_number_begin, load_number_end, con,database);
		  }
		  else
		  {
			System.out.println("\n\nVerify Parm2:Lookup should be AUS / AF / CVS / ST........");
		  }
        }
        else if(database.equalsIgnoreCase("cpx"))
        {
          if(lookup.equalsIgnoreCase("AUS"))
          {
            ExtractAuthorsCpx extractAuthorsCpx = new ExtractAuthorsCpx();
            extractAuthorsCpx.extract(load_number_begin, load_number_end, con,database);
          }
          else if(lookup.equalsIgnoreCase("AF"))
          {
            ExtractAuAffCpx extractAuAffCpx = new ExtractAuAffCpx();
            extractAuAffCpx.extract(load_number_begin, load_number_end, con,database);
          }
          else if(lookup.equalsIgnoreCase("PUB"))
          {
            ExtractPubCpx extractPubCpx = new ExtractPubCpx();
            extractPubCpx.extract(load_number_begin, load_number_end, con,database);
          }
          else if(lookup.equalsIgnoreCase("ST"))
          {
            ExtractStCpx extractStCpx = new ExtractStCpx();
            extractStCpx.extract(load_number_begin, load_number_end, con,database);
          }
          else
          {
            System.out.println("\n\nVerify Parm2:Lookup should be AUS / AF / PUB / ST........");
          }
        }
        else if(database.equalsIgnoreCase("c84"))
		{
		  if(lookup.equalsIgnoreCase("AUS"))
		  {
			ExtractAuthorsCpx extractAuthorsCpx = new ExtractAuthorsCpx();
			extractAuthorsCpx.extract(load_number_begin, load_number_end, con,database);
		  }
		  else if(lookup.equalsIgnoreCase("AF"))
		  {
			ExtractAuAffCpx extractAuAffCpx = new ExtractAuAffCpx();
			extractAuAffCpx.extract(load_number_begin, load_number_end, con,database);
		  }
		  else if(lookup.equalsIgnoreCase("PUB"))
		  {
			ExtractPubCpx extractPubCpx = new ExtractPubCpx();
			extractPubCpx.extract(load_number_begin, load_number_end, con,database);
		  }
		  else if(lookup.equalsIgnoreCase("ST"))
		  {
			ExtractStCpx extractStCpx = new ExtractStCpx();
			extractStCpx.extract(load_number_begin, load_number_end, con,database);
		  }
		  else if(lookup.equalsIgnoreCase("CVS"))
		  		  {
		  			ExtractCvsCpx extractCvsCpx = new ExtractCvsCpx();
		  			extractCvsCpx.extract(load_number_begin, load_number_end, con,database);
		  }
		  else
		  {
			System.out.println("\n\nVerify Parm2:Lookup should be AUS / AF / PUB / ST........");
		  }
        }

        else if(database.equalsIgnoreCase("ins"))
        {
          if(lookup.equalsIgnoreCase("AUS"))
          {
            ExtractAuthorsIns extractAuthorsIns = new ExtractAuthorsIns();
            extractAuthorsIns.extract(load_number_begin, load_number_end, con);
          }
          else if(lookup.equalsIgnoreCase("AF"))
          {
            ExtractAuAffIns extractAuAffIns = new ExtractAuAffIns();
            extractAuAffIns.extract(load_number_begin, load_number_end, con);
          }
          else if(lookup.equalsIgnoreCase("PUB"))
          {
            ExtractPubIns extractPubIns = new ExtractPubIns();
            extractPubIns.extract(load_number_begin, load_number_end, con);
          }
          else if(lookup.equalsIgnoreCase("ST"))
          {
            ExtractStIns extractStIns = new ExtractStIns();
            extractStIns.extract(load_number_begin, load_number_end, con);
          }
          else
          {
            System.out.println("\n\nVerify Parm2:Lookup should be AUS / AF / PUB / ST........");
          }
        }

        else if(database.equalsIgnoreCase("ntis"))
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
            System.out.println("\n\nVerify Parm2:Lookup should be AUS / AF / PUB / ST........");
          }
        }
        else if(database.equalsIgnoreCase("upt"))
        {
          if(lookup.equalsIgnoreCase("INV"))
          {
            ExtractInventorsUpt extractinventorsUpt = new ExtractInventorsUpt();
            extractinventorsUpt.extract(load_number_begin, load_number_end, con);
          }
          else if(lookup.equalsIgnoreCase("ASG"))
          {
            ExtractAssigneeUpt extractassigneeUpt = new ExtractAssigneeUpt();
            extractassigneeUpt.extract(load_number_begin, load_number_end, con);
          }
          else
          {
            System.out.println("\n\nVerify Parm2:Lookup should be INV / ASG......");
          }
        }
        else
        {
          System.out.println("\n\nVerify Parm1:Database is cpx / ins........");
        }
      }
      else if(lookup.equalsIgnoreCase("all"))
      {
        if(database.equalsIgnoreCase("cpx"))
        {
          ExtractAuthorsCpx extractAuthorsCpx = new ExtractAuthorsCpx();
          extractAuthorsCpx.extract(load_number_begin, load_number_end, con,database);

          ExtractAuAffCpx extractAuAffCpx = new ExtractAuAffCpx();
          extractAuAffCpx.extract(load_number_begin, load_number_end, con,database);

          ExtractPubCpx extractPubCpx = new ExtractPubCpx();
          extractPubCpx.extract(load_number_begin, load_number_end, con,database);

          ExtractStCpx extractStCpx = new ExtractStCpx();
          extractStCpx.extract(load_number_begin, load_number_end, con,database);
        }
        else if(database.equalsIgnoreCase("c84"))
		{
			ExtractAuthorsCpx extractAuthorsCpx = new ExtractAuthorsCpx();
			extractAuthorsCpx.extract(load_number_begin, load_number_end, con,database);

			ExtractAuAffCpx extractAuAffCpx = new ExtractAuAffCpx();
			extractAuAffCpx.extract(load_number_begin, load_number_end, con,database);

			ExtractPubCpx extractPubCpx = new ExtractPubCpx();
			extractPubCpx.extract(load_number_begin, load_number_end, con,database);

			ExtractStCpx extractStCpx = new ExtractStCpx();
			extractStCpx.extract(load_number_begin, load_number_end, con,database);

			ExtractCvsCpx extractCvsCpx = new ExtractCvsCpx();
            extractCvsCpx.extract(load_number_begin, load_number_end, con,database);
        }
        else if(database.equalsIgnoreCase("ins"))
        {
          ExtractAuthorsIns extractAuthorsIns = new ExtractAuthorsIns();
          extractAuthorsIns.extract(load_number_begin, load_number_end, con);

          ExtractAuAffIns extractAuAffIns = new ExtractAuAffIns();
          extractAuAffIns.extract(load_number_begin, load_number_end, con);

          ExtractPubIns extractPubIns = new ExtractPubIns();
          extractPubIns.extract(load_number_begin, load_number_end, con);

          ExtractStIns extractStIns = new ExtractStIns();
          extractStIns.extract(load_number_begin, load_number_end, con);
        }
        else if(database.equalsIgnoreCase("ntis"))
        {
          ExtractAuthorsNtis extractAuthorsNtis = new ExtractAuthorsNtis();
          extractAuthorsNtis.extract(load_number_begin, load_number_end, con);

          ExtractAuAffNtis extractAuAffNtis = new ExtractAuAffNtis();
          extractAuAffNtis.extract(load_number_begin, load_number_end, con);

          ExtractCvsNtis extractCvsNtis = new ExtractCvsNtis();
          extractCvsNtis.extract(load_number_begin, load_number_end, con);

        }
        else if(database.equalsIgnoreCase("upt"))
        {
            ExtractInventorsUpt extractinventorsUpt = new ExtractInventorsUpt();
            extractinventorsUpt.extract(load_number_begin, load_number_end, con);

            ExtractAssigneeUpt extractassigneeUpt = new ExtractAssigneeUpt();
            extractassigneeUpt.extract(load_number_begin, load_number_end, con);

        }
        else if(database.equalsIgnoreCase("geo"))
		{
		  if(lookup.equalsIgnoreCase("AUS"))
		  {
			ExtractAuthorsGeo extractAuthorsGeo = new ExtractAuthorsGeo();
			extractAuthorsGeo.extract(load_number_begin, load_number_end, con,database);
		  }
		  else if(lookup.equalsIgnoreCase("AF"))
		  {
			ExtractAuAffGeo extractAuAffGeo = new ExtractAuAffGeo();
			extractAuAffGeo.extract(load_number_begin, load_number_end, con,database);
		  }
		  else if(lookup.equalsIgnoreCase("CVS"))
		  {
			ExtractCvsGeo extractCvsGeo = new ExtractCvsGeo();
			extractCvsGeo.extract(load_number_begin, load_number_end, con,database);
		  }
		  else if(lookup.equalsIgnoreCase("ST"))
		  {
			ExtractStGeo extractStGeo = new ExtractStGeo();
			extractStGeo.extract(load_number_begin, load_number_end, con,database);
		  }
		  else if(lookup.equalsIgnoreCase("PN"))
		  {
		  	ExtractPnGeo extractPnGeo = new ExtractPnGeo();
		  	extractPnGeo.extract(load_number_begin, load_number_end, con,database);
		  }
		  else
		  {
			System.out.println("\n\nVerify Parm2:Lookup should be AUS / AF / CVS / ST........");
		  }
		}
        else
        {
          System.out.println("\n\nVerify Parm1:Database is cpx / ins........");
        }
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
  				else if(ch[i] == ')')
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