package org.ei.data.paper.loadtime;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.DriverManager;
import java.sql.Clob;
import java.sql.SQLException;

import org.ei.util.*;
import org.ei.data.*;
import org.ei.data.bd.loadtime.*;
import org.jdom.Element;

public class ExtractPaperChem
{
    // from shell or bat scritp pass param = "all" to load all data_set
    // pass load_number to load specific load_number
	public static void main(String[] args) throws Exception
	{

        String load_number = null;

        if (args != null && args.length > 0)
        {
              load_number = args[0];
        }
		String[] m_ids = new String[] {"pch_B9CB8C03148510C6E03408002081DCA4", "pch_B9CB8C03147410C6E03408002081DCA4", "pch_B9CB8C03144810C6E03408002081DCA4"};

                //{"pch_34f213f85aae815aM6fa219817173212","pch_11ffb3af85ab48bb2M7ff119817173212","pch_34f213f85aae815aM7bc019817173212","pch_34f213f85aae815aM672219817173212","pch_B9CB8C08410F10C6E03408002081DCA4","pch_34f213f85aae815aM7e2b19817173212","pch_115f0a9f85ab60809M7ea819817173212","pch_B9CB8C083E7510C6E03408002081DCA4","pch_B9CB8C03873410C6E03408002081DCA4","pch_B9CB8C08184610C6E03408002081DCA4","pch_B9CB8C07B53010C6E03408002081DCA4","pch_B9CB8C0806B010C6E03408002081DCA4","pch_B9CB8C0806B110C6E03408002081DCA4","pch_34f213f85aae815aM672219817173212","pch_34f213f85aae815aM7e1a19817173212","pch_6d2baff85ab4375bM7fc519817173212"};
		//Connection con = getDbCoonection("jdbc:oracle:thin:@jupiter.elsevier.com:1521:EIDB1", "AP_PRO1", "ei3it", "oracle.jdbc.driver.OracleDriver");
		Connection con = getDbCoonection("jdbc:oracle:thin:@neptune.elsevier.com:1521:EI", "AP_PRO1", "ei3it", "oracle.jdbc.driver.OracleDriver");
		ExtractPaperChem epc = new ExtractPaperChem();

        // set array for select stmt.
        if(load_number != null)
        {
            String whatToLoad = load_number;
            if (whatToLoad.equalsIgnoreCase("all"))
            {
                m_ids = null;
            }
            else
            {
                m_ids = new String[1];
                m_ids[0] = load_number;
            }
        }

		epc.extract(m_ids,con);
	}

    public void extract(String[] m_ids, Connection con)
        throws Exception
    {
        PrintWriter writerPub   = null;

        PreparedStatement pstmt1    = null;
        ResultSet rs1               = null;

        long begin = System.currentTimeMillis();

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

			String filename = "paperchem_extract1.out";
			System.out.println("filename= "+filename);
            writerPub   = new PrintWriter(new FileWriter(filename));
            String sqlQuery = null;
            if (m_ids != null  && m_ids.length > 1)
            {
			    sqlQuery = " select * from paper_master where m_id in "+midsList;
            }
            else if(m_ids != null)
            {
                String load_number = m_ids[0];
                sqlQuery = "select * from paper_master where load_number ="+load_number;
            }
            else // select complete data_set
            {
                sqlQuery = "select * from paper_master";
            }
			//String sqlQuery = "select * from paper_master_test";
            pstmt1  = con.prepareStatement(sqlQuery);
            System.out.println("\n\nQuery: "+sqlQuery);

            rs1     = pstmt1.executeQuery();
			int i=0;
			int j=1;
            while(rs1.next())
            {
				writeColumn(rs1, "m_id", writerPub);
				writeColumn(rs1, "an", writerPub);
				writeColumn(rs1, "ab", writerPub);
				writeColumn(rs1, "au", writerPub);
				writeColumn(rs1, "cp", writerPub);
				writeColumn(rs1, "em", writerPub);
				writeColumn(rs1, "af", writerPub);
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
				writeColumn(rs1, "pa", writerPub);
				writeColumn(rs1, "do", writerPub);
				writeColumn(rs1, "pi", writerPub);
				writeColumn(rs1, "cc", writerPub);
				writeColumn(rs1, "at", writerPub);
				writeColumn(rs1, "pt", writerPub);
				writeColumn(rs1, "pn", writerPub);
				writeColumn(rs1, "load_number", writerPub);
				writeColumn(rs1, "xp", writerPub); //PAGE
				writeColumn(rs1, "tr", writerPub); //TREATMENTCODE
				writeColumn(rs1, "pp", writerPub);  //PAGECOUNT
				writeColumn(rs1, "cls", writerPub);
				writeColumn(rs1, "ti", writerPub);
				writeColumn(rs1, "nr", writerPub);   //REFCOUNT
				writeColumn(rs1, "vx", writerPub);
				writeColumn(rs1, "pc", writerPub);
				writeColumn(rs1, "fl", writerPub);
				writeColumn(rs1, "ed", writerPub);//EDITOR
				writeColumn(rs1, "ced", writerPub);//CONFERENCEEDITOR
				writeColumn(rs1, "ec", writerPub);//CONFERENCEORGANIZATION
				writeColumn(rs1, "es", writerPub);//CONFERENCEEDITORADDRESS
				writeColumn(rs1, "la", writerPub);
				writeColumn(rs1, "db", writerPub);
                writerPub.println();
                if(i>100000)
                {
					writerPub.close();
					filename = "paperchem_extract"+j+".out";
					System.out.println("filename= "+filename);
					writerPub = new PrintWriter(new FileWriter(filename));
					i=0;
					j++;
				}
				i++;
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
			column = formatAuthor(rs1.getString("au"));
		}
		else if(columnName.equals("cp"))
		{
			column = formatPersonalName(rs1.getString("cp"));
		}
		else if(columnName.equals("em"))
		{
			if(rs1.getString("em")!=null && (rs1.getString("em")).length()>0)
			{
				column = "email"+BdParser.IDDELIMITER+(rs1.getString("em"));
			}
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
		else if(columnName.equals("pp"))
		{
			column = formatPageCount(rs1.getString("pp"));
		}
        else if(columnName.equals("xp"))
        {
            column = formatPage(rs1.getString("xp"));
        }
		else if(columnName.equals("cls"))
		{
			column = formatClassificationCode(rs1.getString("cls"));
		}
		else if(columnName.equals("tr"))
		{
			column = formatTreatmentType(rs1.getString("tr"));
		}
		else if(columnName.equals("ti"))
		{
			column = formatCitationTitle(rs1.getString("ti"), rs1.getString("tt"), rs1.getString("la"));
		}
		else if(columnName.equals("sp"))
		{
			column = formatSponsors(rs1.getString("sp"));
		}
		else if(columnName.equals("pc"))
		{
			column = formatPubAddress(rs1.getString("pc"),rs1.getString("ps"),rs1.getString("py"));
		}
		else if(columnName.equals("fl"))
		{
			column = formatControlledTerms(rs1.getString("fl"));
		}
        else if(columnName.equals("nr"))
		{
			column = formatRefCount(rs1.getString("nr"));
		}
		else if(columnName.equals("ed") )
		{
		    if(rs1.getString("cc")== null)
		    {
		        column = formatPersonalName(rs1.getString("ed"));
		    }
		}
		else if(columnName.equals("ced") )
		{
		    if(rs1.getString("cc")!= null)
		    {
		        column = formatPersonalName(rs1.getString("ed"));
		    }
		}
		else if(columnName.equals("ef"))
		{
		    column = rs1.getString("ef");	//confed organization
		}
		else if(columnName.equals("ec"))
		{
		    column = formatConfOrganization(rs1);
		}
		else if(columnName.equals("sn"))
		{
		    column = formatISSN(rs1.getString("sn"));
		}
		else if(columnName.equals("db"))
		{
			column = "pch";
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

    public String formatPage(String xp)
    {
        StringBuffer result = new StringBuffer();
        if(xp != null && !xp.trim().equals(""))
        {
            xp = xp.trim();
            if(xp.indexOf("p ") == 0)
            {
                xp = xp.substring(2);
            }
            result.append(xp);
            result.append(BdParser.AUDELIMITER).append(BdParser.AUDELIMITER);
        }


        return result.toString();
    }

    public String formatPageCount(String pp)
    {
        StringBuffer result= new StringBuffer();

        if(pp != null && !pp.trim().equals(""))
        {
			result.append(BdParser.AUDELIMITER);
            pp = pp.trim();
            int len = pp.length();
            if(pp.lastIndexOf("p") == (len-1))
            {
               pp = pp.substring(0, (len-1));
            }
            result.append(pp);
        }
        return result.toString();
    }

	public String formatConfOrganization(ResultSet rs)
											throws SQLException
	{

	    StringBuffer buf = new StringBuffer();
	    if(rs.getString("ec") != null)
	    {
	        buf.append(rs.getString("ec"));
	        if(rs.getString("es") != null ||rs.getString("ey") != null)
	        {
	            buf.append(",");
	        }
	    }
	    if(rs.getString("es") != null)
	    {
	        buf.append(rs.getString("es")).append(",");
            if(rs.getString("ey") != null)
	        {
	            buf.append(",");
	        }
	    }
	    if(rs.getString("ey") != null)
	    {
	        buf.append(rs.getString("ey"));
	    }

	    return buf.toString();
	}

	public String formatCitationTitle(String citTitle,
										String citTranslatedTitle,
										String clanguage)throws Exception
	{
		//cit title is
		// 0 - id, transl title if it is there , lan -> "eng"
		// 1 - id, title , lan -> from pch master table

		StringBuffer cittext = new StringBuffer();
		int index =0;
		String lan = null;
			    if (clanguage!= null && clanguage.equalsIgnoreCase("English"))
	    {
	        lan = "eng";
	    }
	    else
	    {
	        lan = clanguage;
	    }
	    if(citTranslatedTitle != null)
		{
			cittext.append(index);
			cittext.append(BdParser.IDDELIMITER);
			cittext.append(citTranslatedTitle);
			cittext.append(BdParser.IDDELIMITER);
			cittext.append("n");
			cittext.append(BdParser.IDDELIMITER);
			cittext.append("eng");
			cittext.append(BdParser.AUDELIMITER);
			index++;
		}

		if(citTitle != null)
		{
		    cittext.append(index);
		    cittext.append(BdParser.IDDELIMITER);
		    cittext.append(citTitle);
		    cittext.append(BdParser.IDDELIMITER);
		    cittext.append("y");
		    cittext.append(BdParser.IDDELIMITER);
		    cittext.append(lan);
		    cittext.append(BdParser.AUDELIMITER);
		}

		return cittext.toString();
	}

	public String formatTreatmentType(String treatments)throws Exception
	{

		StringBuffer tr = new StringBuffer();
		if(treatments != null)
		{
            treatments = treatments.trim();
			int len = treatments.length();

			for (int i= 0; i < len; i++)
			{
				tr.append(treatments.charAt(i));
                tr.append(BdParser.AUDELIMITER);
			}
		}
        //trim out last delim
        int lastdelim = tr.lastIndexOf(BdParser.AUDELIMITER);

        if(lastdelim > 0)
        {
            return  tr.toString().substring(0,lastdelim);
        }
		return tr.toString();
	}


	public String formatConferenceLocation(String location,
												String city,
												String state,
												String country)
	{
		StringBuffer affBuffer = new StringBuffer();
		if(location != null || city != null || state != null || country!= null)
		{
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

	public String formatISSN(String issn)
	{
		if(issn!=null)
		{
		    issn = issn.replaceAll("-","");
		}
	    if(issn!=null && issn.equals("028068000"))// fix for 028068000 typo
	    {
	        return "02806800";
	    }
	    else if(issn!=null && issn.indexOf(";") >-1) // fix for redundant issns 0029-3156;0029-3156 and 0040-5752;0040-5752
		{
		    String [] issnArray = new String[1];
		    issnArray = issn.split(";");
		    return issnArray[0];
		}
		return issn;
	}

    public String formatRefCount(String refcount)
    {
        if(refcount != null && !refcount.trim().equals(""))
        {
            int beginRefs = refcount.indexOf("Refs");
            int beginN = refcount.indexOf("N");
            if(beginRefs > 0)
            {
                refcount  = refcount.substring(0, beginRefs);
                return refcount.trim();
            }
            else if(beginN > 0 )
            {
                return null;
            }
        }
        return null;
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
			affBuffer.append(BdParser.IDDELIMITER);//affid
			affBuffer.append(BdParser.IDDELIMITER);//text
			if(affiliation!=null)
			{
				affBuffer.append(affiliation);
			}
			affBuffer.append(BdParser.GROUPDELIMITER);//afforganization
			affBuffer.append(BdParser.GROUPDELIMITER);//address-part
			if(city!=null)
			{
				affBuffer.append(city);
			}

			if(state!=null)
			{
				if(city!=null)
				{
					affBuffer.append(", ");//city
				}
				affBuffer.append(state);
			}
			affBuffer.append(BdParser.GROUPDELIMITER);//country
			if(country!=null)
			{
				affBuffer.append(country);
			}
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
					lastName  = fullname.substring(0,fullname.indexOf(","));
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
				nameBuffer.append(lastName.trim());
				nameBuffer.append(BdParser.IDDELIMITER);//Surname
				nameBuffer.append(givenName.trim());
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


	public String formatAuthor(String authors) throws Exception
	{
		String lastName = "";
		String givenName = "";
		String affId = "0";
		StringBuffer nameBuffer = new StringBuffer();
		int i=1;
		if(authors != null)
		{
			AuthorStream aStream = new AuthorStream(new ByteArrayInputStream(authors.getBytes()));
			String author = null;
			while((author = aStream.readAuthor()) != null)
			{

				if(author.indexOf(",")>-1)
				{
					lastName  = author.substring(0,author.indexOf(","));
					givenName = author.substring(author.indexOf(",")+1);
				}
				else
				{
					lastName = author;
				}

                nameBuffer.append(i);    //starts from 1
				nameBuffer.append(BdParser.IDDELIMITER);//sec
				nameBuffer.append(BdParser.IDDELIMITER);//auid
				nameBuffer.append(affId);
				nameBuffer.append(BdParser.IDDELIMITER);//afid
				nameBuffer.append(BdParser.IDDELIMITER);//indexname
				nameBuffer.append(BdParser.IDDELIMITER);//initials
				nameBuffer.append(lastName.trim());
				nameBuffer.append(BdParser.IDDELIMITER);//Surname
				nameBuffer.append(BdParser.IDDELIMITER);//Degrees
				nameBuffer.append(givenName.trim());
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
				i++;
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
	public String formatClassificationCode(String codes)
	{
		String[] codesArray = null;
		StringBuffer codesBuffer = new StringBuffer();
		if(codes != null)
		{
			if(codes.indexOf(";")>-1)
			{
				codesArray = codes.split(";",-1);
			}
			else
			{
				codesArray = new String[1];
				codesArray[0] = codes;
			}

			codesBuffer.append(codesArray[0]);

			for(int i=1;i<codesArray.length;i++)
			{
				codesBuffer.append(BdParser.AUDELIMITER);
				codesBuffer.append(codesArray[i]);
			}
		}

		return codesBuffer.toString();
	}

	public String formatSponsors(String sponsors)
	{
		String[] sponsorsArray = null;
		StringBuffer sponsorsBuffer = new StringBuffer();
		if(sponsors != null)
		{
			if(sponsors.indexOf(";")>-1)
			{
				sponsorsArray = sponsors.split(";",-1);
			}
			else
			{
				sponsorsArray = new String[1];
				sponsorsArray[0] = sponsors;
			}

			sponsorsBuffer.append(sponsorsArray[0]);

			for(int i=1;i<sponsorsArray.length;i++)
			{
				sponsorsBuffer.append(BdParser.AUDELIMITER);
				sponsorsBuffer.append(sponsorsArray[i]);
			}
		}

		return sponsorsBuffer.toString();
	}


	public String formatPubAddress(String city, String state, String country)
	{
		StringBuffer affBuffer = new StringBuffer();

		if(city != null)
		{
			affBuffer.append(city.trim());
		}
		if(state != null)
		{
			if(city != null)
			{
				affBuffer.append(", ");
			}

			affBuffer.append(state.trim());
		}
		if(country != null)
		{
			if(city != null || state != null)
			{
				affBuffer.append(", ");
			}

			affBuffer.append(country.trim());
		}

		return affBuffer.toString();

	}

  public static Connection getDbCoonection(String url,String username,String password, String driver)
    throws Exception
  {
    Class.forName(driver).newInstance();
    Connection con  = DriverManager.getConnection(url, username, password);
    return con;
  }

}

