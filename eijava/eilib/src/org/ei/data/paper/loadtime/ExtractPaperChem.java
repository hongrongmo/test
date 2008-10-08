package org.ei.data.paper.loadtime;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Clob;
import org.ei.util.*;
import org.ei.data.bd.loadtime.*;

public class ExtractPaperChem
{
	public static void main(String[] args) throws Exception
	{
		String[] m_ids = new String[]{"pch_34f213f85aae815aM672219817173212","pch_B9CB8C08410F10C6E03408002081DCA4","pch_34f213f85aae815aM7e2b19817173212","pch_115f0a9f85ab60809M7ea819817173212","pch_B9CB8C083E7510C6E03408002081DCA4","pch_B9CB8C03873410C6E03408002081DCA4","pch_B9CB8C08184610C6E03408002081DCA4","pch_B9CB8C07B53010C6E03408002081DCA4","pch_B9CB8C0806B010C6E03408002081DCA4","pch_B9CB8C0806B110C6E03408002081DCA4","pch_34f213f85aae815aM672219817173212","pch_34f213f85aae815aM7e1a19817173212"};
		Connection con = getDbCoonection("jdbc:oracle:thin:@neptune.elsevier.com:1521:EI", "AP_PRO1", "ei3it", "oracle.jdbc.driver.OracleDriver");
		ExtractPaperChem epc = new ExtractPaperChem();
		epc.extract(m_ids,con);
	}

    public void extract(String[] m_ids, Connection con)
        throws Exception
    {
        PrintWriter writerPub   = null;

        PreparedStatement pstmt1    = null;
        ResultSet rs1               = null;

        long begin          = System.currentTimeMillis();

        try
        {
			String midsList = "(";
			for(int i = 0; i < m_ids.length; i++)
			{
				midsList += "'" + m_ids[i] + "'";
				if(i != m_ids.length-1)
					midsList += ",";
			}

			midsList += ")";

            writerPub   = new PrintWriter(new FileWriter("paperchem_extract.sql"));

			String sqlQuery = " select * from paper_master where m_id in "+midsList;
            pstmt1  = con.prepareStatement(sqlQuery);
            System.out.println("\n\nQuery: "+sqlQuery);


            rs1     = pstmt1.executeQuery();

            while(rs1.next())
            {
				writeColumn(rs1, "m_id", writerPub);
				writeColumn(rs1, "an", writerPub);
				writeColumn(rs1, "ab", writerPub);
				writeColumn(rs1, "au", writerPub);
				writeColumn(rs1, "cp", writerPub);
				writeColumn(rs1, "em", writerPub);
				writeColumn(rs1, "af", writerPub);
				writeColumn(rs1, "ed", writerPub);
				writeColumn(rs1, "mt", writerPub);
				writeColumn(rs1, "st", writerPub);
				writeColumn(rs1, "se", writerPub);
				writeColumn(rs1, "cf", writerPub);
				writeColumn(rs1, "bn", writerPub);
				writeColumn(rs1, "sn", writerPub);
				writeColumn(rs1, "cn", writerPub);
				writeColumn(rs1, "vo", writerPub);
				writeColumn(rs1, "iss", writerPub);
				writeColumn(rs1, "sd", writerPub);
				writeColumn(rs1, "yr", writerPub);
				writeColumn(rs1, "md", writerPub);
				writeColumn(rs1, "ml", writerPub);
				writeColumn(rs1, "sp", writerPub);
				writeColumn(rs1, "media", writerPub);
				writeColumn(rs1, "csess", writerPub);
				writeColumn(rs1, "patno", writerPub);
				writeColumn(rs1, "pling", writerPub);
				writeColumn(rs1, "appln", writerPub);
				writeColumn(rs1, "prior_num", writerPub);
				writeColumn(rs1, "dt", writerPub);
				writeColumn(rs1, "assig", writerPub);
				writeColumn(rs1, "pcode", writerPub);
				writeColumn(rs1, "claim", writerPub);
				writeColumn(rs1, "sourc", writerPub);
				writeColumn(rs1, "nofig", writerPub);
				writeColumn(rs1, "notab", writerPub);
				writeColumn(rs1, "sub_index", writerPub);
				writeColumn(rs1, "specn", writerPub);
				writeColumn(rs1, "suppl", writerPub);
				writeColumn(rs1, "pdfix", writerPub);
				writeColumn(rs1, "rn", writerPub);
				writeColumn(rs1, "do", writerPub);
				writeColumn(rs1, "pi", writerPub);
				writeColumn(rs1, "cc", writerPub);
				writeColumn(rs1, "at", writerPub);
				writeColumn(rs1, "pt", writerPub);
                writerPub.println();
            }

        }
        finally
        {
            if(rs1 != null)
            {
                try
                {
                    rs1.close();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
            if(pstmt1 != null)
            {
                try
                {
                    pstmt1.close();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
            if(writerPub != null)
            {
                try
                {
                    writerPub.close();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

    }


	public void writeColumn(ResultSet rs1, String columnName, PrintWriter writerPub) throws Exception
	{
		String column = null;
		if(columnName.equals("ab"))
		{
			Clob clob = rs1.getClob("AB");
			if(clob != null)
			{
				column = StringUtil.getStringFromClob(clob);
			}
		}
		else if(columnName.equals("af"))
		{
			column = formatAffiliation(rs1.getString("af"),rs1.getString("ac"),rs1.getString("asp"),rs1.getString("ay"));
		}
		else if(columnName.equals("au"))
		{
			column = formatAuthor(rs1.getString("au"),rs1.getString("af"));
		}
		else if(columnName.equals("cp"))
		{
			column = formatPersonalName(rs1.getString("cp"));
		}
		else if(columnName.equals("ed"))
		{
			column = formatPersonalName(rs1.getString("ed"));
		}
		else if(columnName.equals("bn"))
		{
			column = formatISBN(rs1.getString("bn"));
		}
		else if(columnName.equals("vo"))
		{
			column = formatVOLUME(rs1.getString("vo"));
		}
		else if(columnName.equals("iss"))
		{
			column = formatISSUE(rs1.getString("iss"));
		}
		else if(columnName.equals("md"))
		{
			column = formatDate(rs1.getString("md"),rs1.getString("m1"),rs1.getString("m2"));
		}
		else if(columnName.equals("ml"))
		{
			column = formatConferenceLocation(rs1.getString("ml"),rs1.getString("mc"),rs1.getString("ms"),rs1.getString("my"));
		}
		else if(columnName.equals("at"))
		{
			column = rs1.getString("at");
			if(column == null || column.indexOf("Author abstract") != -1)
				column = "y";
			else
				column = "n";
		}
		else if(columnName.equals("pt"))
		{
			column = formatControlledTerms(rs1.getString("pt"));
		}
		else
		{
			column   = rs1.getString(columnName);
		}

		if(column != null)
		{
			writerPub.print(column + "\t");
		}
		else
		{
			writerPub.print("\t");
		}
	}

	public String formatConferenceLocation(String location, String city,String state, String country)
	{
		StringBuffer affBuffer = new StringBuffer();

		affBuffer.append(BdParser.IDDELIMITER);//affid
		affBuffer.append(BdParser.IDDELIMITER);//venue
		affBuffer.append(BdParser.IDDELIMITER);//organization
		affBuffer.append(BdParser.IDDELIMITER);//address_part
		affBuffer.append(BdParser.IDDELIMITER);//citygroup
		if(location != null)
		{
			affBuffer.append(location);
		}
		else
		{
			if(city !=null)
			{
				affBuffer.append(city);
			}

			if(state !=null)
			{
				if(city!= null)
				{
					affBuffer.append(", ");
				}
				affBuffer.append(state);
			}
		}
		affBuffer.append(BdParser.IDDELIMITER);//country

		if(country!= null && location!=null && location.indexOf(country)<0)
		{
			affBuffer.append(country);
		}


		return affBuffer.toString();

	}

	public String formatDate(String md,String m1,String m2)
	{
		String dateString = null;
		if(m2 != null)
		{
			dateString = m2;
		}
		else if(m1 !=null)
		{
			dateString = m1;
		}
		else if(md !=null)
		{
			dateString = md;
		}
		return dateString;
	}

	public String formatVOLUME(String volume)
	{
		if(volume != null)
		{
			volume = volume.replaceAll("v","").trim();
		}
		return volume;
	}

	public String formatISSUE(String issue)
	{
		if(issue != null)
		{
			issue = issue.replaceAll("n","").trim();
		}
		return issue;
	}

	public String formatISBN(String isbn)
	{
		StringBuffer isbnBuffer = new StringBuffer();
		String[] isbnArray = null;
		if(isbn!=null)
		{
			if(isbn.indexOf("-")>-1)
			{
				isbn = isbn.replaceAll("-","");
			}
			if(isbn.indexOf(" ")>-1)
			{
				isbnArray = isbn.split(" ",-1);
			}
			else
			{
				isbnArray = new String[1];
				isbnArray[0] = isbn;
			}
			for(int i=0;i<isbnArray.length;i++)
			{
				isbnBuffer.append(BdParser.IDDELIMITER);//isbnType
				isbnBuffer.append(isbnArray[i].length());
				isbnBuffer.append(BdParser.IDDELIMITER);//isbnLength
				isbnBuffer.append(BdParser.IDDELIMITER);//isbnVolume
				isbnBuffer.append(isbnArray[i]);
				isbnBuffer.append(BdParser.IDDELIMITER);//isbnValue
				if(i<isbnArray.length-1)
				{
					isbnBuffer.append(BdParser.AUDELIMITER);
				}
			}
		}
		return isbnBuffer.toString();
	}

	public String formatAffiliation(String affiliation,String city,String state,String country)throws Exception
	{
		StringBuffer affBuffer = new StringBuffer();
		if(affiliation!=null || city!=null || state!=null || country!=null)
		{
			affBuffer.append("1");
			affBuffer.append(BdParser.IDDELIMITER);//affid
			if(affiliation!=null)
			{
				affBuffer.append(affiliation);
			}
			affBuffer.append(BdParser.IDDELIMITER);//afforganization
			affBuffer.append(BdParser.IDDELIMITER);//affcityGroup
			if(country!=null)
			{
				affBuffer.append(country);
			}
			affBuffer.append(BdParser.IDDELIMITER);//affCountry
			affBuffer.append(BdParser.IDDELIMITER);//affAddressPart
			if(city!=null)
			{
				affBuffer.append(city);
			}
			affBuffer.append(BdParser.IDDELIMITER);//affCity
			if(state!=null)
			{
				affBuffer.append(state);
			}
			affBuffer.append(BdParser.IDDELIMITER);//affState
			affBuffer.append(BdParser.IDDELIMITER);//affPostalCode
			affBuffer.append(BdParser.IDDELIMITER);//affText
		}
		return affBuffer.toString();
	}

	public String formatPersonalName(String names)
	{
		String lastName     = null;
		String givenName    = null;
		String fullname     = null;
		String[] namesArray = null;
		StringBuffer nameBuffer = new StringBuffer();
		if(names != null)
		{
			if(names.indexOf(";")>-1)
			{
				namesArray = names.split(";",-1);
			}
			else
			{
				namesArray = new String[1];
				namesArray[0] = names;
			}

			for(int i=0;i<namesArray.length;i++)
			{
				fullname = namesArray[i];
				if(fullname.indexOf(",")>-1)
				{
					lastName  = fullname.substring(0,fullname.indexOf(",")-1);
					givenName = fullname.substring(fullname.indexOf(",")+1);
				}
				else
				{
					lastName = fullname;
					givenName = "";
				}

				nameBuffer.append(i);
				nameBuffer.append(BdParser.IDDELIMITER);//id
				nameBuffer.append(BdParser.IDDELIMITER);//initials
				nameBuffer.append(BdParser.IDDELIMITER);//indexname
				nameBuffer.append(BdParser.IDDELIMITER);//Degrees
				nameBuffer.append(lastName);
				nameBuffer.append(BdParser.IDDELIMITER);//Surname
				nameBuffer.append(givenName);
				nameBuffer.append(BdParser.IDDELIMITER);//givenName
				nameBuffer.append(BdParser.IDDELIMITER);//Suffix
				nameBuffer.append(BdParser.IDDELIMITER);//Nametext
				nameBuffer.append(BdParser.IDDELIMITER);//text
				if(i<namesArray.length-1)
				{
					nameBuffer.append(BdParser.AUDELIMITER);
				}
			}
		}
		return nameBuffer.toString();
	}


	public String formatAuthor(String author,String affiliation)
	{
		String lastName = null;
		String fullname = null;
		String givenName = null;
		String[] authorArray = null;
		String affId = "0";
		StringBuffer nameBuffer = new StringBuffer();
		if(author != null)
		{
			if(author.indexOf(";")>-1)
			{
				authorArray = author.split(";",-1);
			}
			else
			{
				authorArray = new String[1];
				authorArray[0] = author;
			}

			if(affiliation!=null&&affiliation.length()>0)
			{
				affId="1";
			}

			for(int i=0;i<authorArray.length;i++)
			{
				fullname = authorArray[i];
				if(fullname.indexOf(",")>-1)
				{
					lastName  = fullname.substring(0,fullname.indexOf(",")-1);
					givenName = fullname.substring(fullname.indexOf(",")+1);
				}
				nameBuffer.append(BdParser.IDDELIMITER);//sec
				nameBuffer.append(i);
				nameBuffer.append(BdParser.IDDELIMITER);//auid
				nameBuffer.append(affId);
				nameBuffer.append(BdParser.IDDELIMITER);//afid
				nameBuffer.append(BdParser.IDDELIMITER);//indexname
				nameBuffer.append(BdParser.IDDELIMITER);//initials
				nameBuffer.append(lastName);
				nameBuffer.append(BdParser.IDDELIMITER);//Surname
				nameBuffer.append(BdParser.IDDELIMITER);//Degrees
				nameBuffer.append(givenName);
				nameBuffer.append(BdParser.IDDELIMITER);//givenName
				nameBuffer.append(BdParser.IDDELIMITER);//Suffix
				nameBuffer.append(BdParser.IDDELIMITER);//Nametext
				nameBuffer.append(BdParser.IDDELIMITER);//PrefnameInitials
				nameBuffer.append(BdParser.IDDELIMITER);//PrefnameIndexedname
				nameBuffer.append(BdParser.IDDELIMITER);//PrefnameDegrees
				nameBuffer.append(BdParser.IDDELIMITER);//PrefnameSurname
				nameBuffer.append(BdParser.IDDELIMITER);//PrefnameGivenname
				nameBuffer.append(BdParser.IDDELIMITER);//Eaddress
				nameBuffer.append(BdParser.AUDELIMITER);
			}

		}
		return nameBuffer.toString();
	}


	public String formatControlledTerms(String terms)
	{
		String[] termsArray = null;
		StringBuffer termsBuffer = new StringBuffer();
		if(terms != null)
		{
			if(terms.indexOf(";")>-1)
			{
				termsArray = terms.split(";",-1);
			}
			else
			{
				termsArray = new String[1];
				termsArray[0] = terms;
			}

			termsBuffer.append(termsArray[0]);

			for(int i=1;i<termsArray.length;i++)
			{
				termsBuffer.append(BdParser.AUDELIMITER);
				termsBuffer.append(termsArray[i]);
			}
		}

		return termsBuffer.toString();
	}
  public static Connection getDbCoonection(String url,String username,String password, String driver)
    throws Exception
  {
    Class.forName(driver).newInstance();
    Connection con  = DriverManager.getConnection(url, username, password);
    return con;
  }

}

