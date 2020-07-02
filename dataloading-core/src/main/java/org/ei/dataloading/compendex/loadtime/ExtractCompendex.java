package org.ei.dataloading.compendex.loadtime;

import java.io.ByteArrayInputStream;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.oro.text.perl.Perl5Util;
import org.ei.common.AuthorStream;
import org.ei.common.Constants;
import org.ei.util.StringUtil;

public class ExtractCompendex
{
    // from shell or bat scritp pass param = "all" to load all data_set
    // pass load_number to load specific load_number
    Perl5Util perl = new Perl5Util();
	public static void main(String[] args) throws Exception
	{

        String load_number = null;

        if (args != null && args.length > 0)
        {
              load_number = args[0];
        }
		//String[] m_ids = null;//{"pch_B9CB8C03148510C6E03408002081DCA4", "pch_B9CB8C03147410C6E03408002081DCA4", "pch_B9CB8C03144810C6E03408002081DCA4"};
		String[] m_ids = null;//{"cpx_18a992f10a185ae64fM80002061377553","cpx_18a992f10a185ae64fM7f602061377553","cpx_6966265","cpx_6840258","cpx_2549648","cpx_3060720","cpx_30c221115ec27f59fM7e922061377553","cpx_18a992f108afbc18f6M6cf22061377553","cpx_30c221110dfec28c6M7e6e2061377553","cpx_30c221110dfec28c6M7e682061377553","cpx_10385c11116ae314caM7e992061377553","cpx_30c221117c13ee92bM7f3a2061377553","cpx_1e5e2c311a79bdcfe6M7fd42061377553"};
		Connection con = getDbCoonection("jdbc:oracle:thin:@jupiter.elsevier.com:1521:EIDB1", "AP_PRO1", "", "oracle.jdbc.driver.OracleDriver");
		ExtractCompendex epc = new ExtractCompendex();

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
			String filename = "compendex_extract1.out";
			System.out.println("filename= "+filename);
			writerPub   = new PrintWriter(new FileWriter(filename));
			String sqlQuery = null;
			if (m_ids != null  && m_ids.length > 1)
            {
				String midsList = "(";
				for(int i = 0; i < m_ids.length; i++)
				{
					midsList += "'" + m_ids[i] + "'";
					if(i != m_ids.length-1)
						midsList += ",";
				}

				midsList += ")";
			    sqlQuery = " select * from cpx_master where m_id in "+midsList+" and mh is null and cvs is null";
            }
            else if(m_ids != null)
            {
                String load_number = m_ids[0];
                sqlQuery = "select * from cpx_master where load_number in ('"+load_number+"') and mh is null and cvs is null";
            }
            else // select complete data_set
            {
                sqlQuery = "select * from cpx_master where mh is null and cvs is null";
                //for 161 and 162 sqlQuery = "select * from cpx_master where m_id in(select M_id from cpx_master where  mh is null and cvs is null minus select m_id from bd_master where tocflag='toc')";
                 //182,183 sqlQuery = "select * from cpx_master where m_id in(select M_id from no_loadnumber_bd_master)";
            }

            pstmt1  = con.prepareStatement(sqlQuery);
            System.out.println("\n\nQuery: "+sqlQuery);

            rs1     = pstmt1.executeQuery();
			int i=0;
			int j=1;
            while(rs1.next())
            {
				writeColumn(rs1, "m_id", writerPub);//M_ID	1
				writeColumn(rs1, "ex", writerPub);//ACCESSNUMBER	2
				writeColumn(rs1, "ab", writerPub);//ABSTRACT	3
				writeColumn(rs1, "aus", writerPub);//AUTHOR	4
				writeColumn(rs1, "cp", writerPub);//CORRESPONDENCENAME	5
				writeColumn(rs1, "em", writerPub);//CORRESPONDENCEEADDRESS	6
				writeColumn(rs1, "af", writerPub);//CORRESPONDENCEAFFILIATION	7
				writeColumn(rs1, "mt", writerPub);//ISSUETITLE	8
				writeColumn(rs1, "st", writerPub);//SOURCETITLE	9
				writeColumn(rs1, "se", writerPub);//SOURCETITLEABBREV	10
				writeColumn(rs1, "cf", writerPub);//CONFNAME	11
				writeColumn(rs1, "bn", writerPub);//ISBN	12
				writeColumn(rs1, "sn", writerPub);//ISSN	13
				writeColumn(rs1, "cn", writerPub);//CODEN	14
				writeColumn(rs1, "vo", writerPub);//VOLUME	15
				writeColumn(rs1, "iss", writerPub);//ISSUE	16
				writeColumn(rs1, "sd", writerPub);//PUBLICATIONDATE	17
				writeColumn(rs1, "yr", writerPub);//PUBLICATIONYEAR	18
				writeColumn(rs1, "md", writerPub);//CONFDATE	19
				writeColumn(rs1, "ml", writerPub);//CONFLOCATION	20
				writeColumn(rs1, "sp", writerPub);//CONFSPONSORS	21
				writeColumn(rs1, "dt", writerPub);//CITTYPE	22
				writeColumn(rs1, "pa", writerPub);//REPORTNUMBER	23
				writeColumn(rs1, "do", writerPub);//DOI	24
				writeColumn(rs1, "pi", writerPub);//PII	25
				writeColumn(rs1, "cc", writerPub);//CONFCODE	26
				writeColumn(rs1, "at", writerPub);//ABSTRACTORIGINAL	27
				//writeColumn(rs1, "cvs", writerPub);//CONTROLLEDTERM
				writeColumn(rs1, "pn", writerPub);//PUBLISHERNAME	28
				writeColumn(rs1, "load_number", writerPub);//LOADNUMBER	29
				writeColumn(rs1, "xp", writerPub);//PAGE	30
				writeColumn(rs1, "tr", writerPub);//TREATMENTCODE	31
				writeColumn(rs1, "pp", writerPub);//PAGECOUNT	32
				writeColumn(rs1, "cls", writerPub);//CLASSIFICATIONCODE	33
				writeColumn(rs1, "ti", writerPub);//CITATIONTITLE	34
				writeColumn(rs1, "nr", writerPub);//REFCOUNT	35
				writeColumn(rs1, "vx", writerPub);//CONFCATNUMBER	36
				writeColumn(rs1, "pc", writerPub);//PUBLISHERADDRESS	37
				writeColumn(rs1, "fls", writerPub);//UNCONTROLLEDTERM	38
				writeColumn(rs1, "ed", writerPub);//EDITOR	39
				writeColumn(rs1, "ced", writerPub);//CONFERENCEEDITOR	40
				writeColumn(rs1, "ef", writerPub);//CONFERENCEORGANIZATION	41
				writeColumn(rs1, "ec", writerPub);//CONFERENCEEDITORADDRESS	42
				writeColumn(rs1, "la", writerPub);//CITATIONLANGUAGE	43
				writeColumn(rs1, "db", writerPub);//DATABASE	44
				//writeColumn(rs1, "mh", writerPub);//MAINHEADING
				writeColumn(rs1, "en", writerPub);//EISSN	45
				writeColumn(rs1, "ty", writerPub);//SOURCETYPE	46
				writeColumn(rs1, "vt", writerPub);//VOLUMETITLE	47
				writeColumn(rs1, "up", writerPub);//UPDATECODESTAMP	48
				writeColumn(rs1, "ur", writerPub);//UPDATERESOURCE	49
				writeColumn(rs1, "UPDATE_NUMBER", writerPub);//UPDATENUMBER	50
				writeColumn(rs1, "ar", writerPub);//ARTICLENUMBER	51
				writeColumn(rs1, "od", writerPub);//UpdateTimeStamp	52
				writeColumn(rs1, "toc", writerPub);//TocFlag	53
				writeColumn(rs1, "tg", writerPub);//TgFlag	54
                writerPub.println();
                if(i>100000)
                {
					j++;
					writerPub.close();
					filename = "compendex_extract"+j+".out";
					System.out.println("filename= "+filename);
					writerPub = new PrintWriter(new FileWriter(filename));
					i=0;
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
			column = formatAffiliation(rs1.getString("af"),rs1.getString("ac"),rs1.getString("ass"),rs1.getString("av"),rs1.getString("ay"));
		}
		else if(columnName.equals("aus"))
		{
			column = formatAuthor(rs1.getString("aus"));
		}
		else if(columnName.equals("cp"))
		{
			column = formatPersonalName(rs1.getString("cp"));
		}
		else if(columnName.equals("em"))
		{
			if(rs1.getString("em")!=null && (rs1.getString("em")).length()>0)
			{
				column = "email"+Constants.IDDELIMITER+(rs1.getString("em"));
			}
		}
		else if(columnName.equals("bn"))
		{
			column = formatISBN(rs1.getString("be"),rs1.getString("bn"),rs1.getString("bv"),rs1.getString("bx"));
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
			column = formatConferenceLocation(rs1.getString("ml"),rs1.getString("mc"),rs1.getString("ms"),rs1.getString("mv"),rs1.getString("my"));
		}
		else if(columnName.equals("at"))
		{
			column = rs1.getString("at");
			if(column == null || column.indexOf("Author abstract") != -1)
				column = "y";
			else
				column = "n";
		}
		else if(columnName.equals("cvs"))
		{
			column = formatControlledTerms(rs1.getString("cvs"));
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
			column = formatPubAddress(rs1.getString("pc"),rs1.getString("ps"),rs1.getString("pv"),rs1.getString("py"));
		}
		else if(columnName.equals("fls"))
		{
			column = formatControlledTerms(rs1.getString("fls"));
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
		else if(columnName.equals("pn"))
		{
			column = formatPubName(rs1.getString("pn"));
		}
		else if(columnName.equals("db"))
		{
			column = "cpx";
		}
		else if(columnName.equals("toc"))
		{
			column = "toc";
		}
		else if(columnName.equals("dt"))
		{
			column = formatDocType(rs1.getString("it"),rs1.getString("dt"));
		}
		else if(columnName.equals("ex"))
		{
			String accessNumber = rs1.getString("ex");
			if(accessNumber.charAt(0)=='0')
			{
				accessNumber = "20"+accessNumber;
			}
			else
			{
				accessNumber = "19"+accessNumber;
			}
			column = accessNumber;
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

	private String formatDocType(String it,String dt)
	{
		if(it == null)
		{
			it = dt;
		}
		return it;
	}

	public String formatPubName(String line)
	{

		if(line != null && !line.trim().equals(""))
		{
			line = line.trim();

			if(perl.match("/[\t\n\r\f\b]+/", line))
			{
			   line = perl.substitute("s/[\t\n\r\f\b]+//gi", line);
			}
		}
		return line;
	}

    public String formatPage(String xp)
    {
        StringBuffer result = new StringBuffer();
        if(xp != null && !xp.trim().equals(""))
        {
            xp = xp.trim();
			// if len. is longer than 50 - truncate
			int len = xp.length();
			if (len > 50)
			{
				xp = xp.substring(0, 50);
				// and up to the last delim =";"
				if(xp.lastIndexOf(";") > -1)
				{
				   xp = xp.substring(0, xp.lastIndexOf(";"));
				}
			}
            if(xp.indexOf("p ") == 0)
            {
                xp = xp.substring(2);
            }
            result.append(xp);
            result.append(Constants.AUDELIMITER).append(Constants.AUDELIMITER);
        }


        return result.toString();
    }

    public String formatPageCount(String pp)
    {
        StringBuffer result= new StringBuffer();

        if(pp != null && !pp.trim().equals(""))
        {
			result.append(Constants.AUDELIMITER);
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

	public String formatConfOrganization(ResultSet rs) throws SQLException
	{

	    StringBuffer buf = new StringBuffer();
	    String state = null;
	    if(rs.getString("es") != null)
	    {
			state = rs.getString("es");
		}
		else
		{
			state = rs.getString("ev");
		}

	    if(rs.getString("ec") != null)
	    {
	        buf.append(rs.getString("ec"));
	        if(state != null ||rs.getString("ey") != null)
	        {
	            buf.append(",");
	        }
	    }
	    if(state != null && !perl.match("/[0-9]+/", state))
	    {
	        buf.append(state).append(",");
            if(rs.getString("ey") != null)
	        {
	            buf.append(",");
	        }
	    }
	    else
	    {
			 buf.append(" ");
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
	    else if(clanguage!=null)
	    {
	        lan = clanguage;
	    }
	    else
	    {
			lan = "";
		}

	    if(citTranslatedTitle != null)
		{
			cittext.append(index);
			cittext.append(Constants.IDDELIMITER);
			cittext.append(citTranslatedTitle);
			cittext.append(Constants.IDDELIMITER);
			cittext.append("n");
			cittext.append(Constants.IDDELIMITER);
			cittext.append("eng");
			cittext.append(Constants.AUDELIMITER);
			index++;
		}

		if(citTitle != null)
		{
		    cittext.append(index);
		    cittext.append(Constants.IDDELIMITER);
		    cittext.append(citTitle);
		    cittext.append(Constants.IDDELIMITER);
		    cittext.append("y");
		    cittext.append(Constants.IDDELIMITER);
		    cittext.append(lan);
		    cittext.append(Constants.AUDELIMITER);
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
                tr.append(Constants.AUDELIMITER);
			}
		}
        //trim out last delim
        int lastdelim = tr.lastIndexOf(Constants.AUDELIMITER);

        if(lastdelim > 0)
        {
            return  tr.toString().substring(0,lastdelim);
        }
		return tr.toString();
	}


	public String formatConferenceLocation(String location,
												String city,
												String state,
												String province,
												String country)
	{
		StringBuffer affBuffer = new StringBuffer();
		if(state == null)
		{
			state = province;
		}
		if(location != null || city != null || state != null || country!= null)
		{
			affBuffer.append(Constants.IDDELIMITER);//affid
			affBuffer.append(Constants.IDDELIMITER);//venue
			affBuffer.append(Constants.IDDELIMITER);//organization
			affBuffer.append(Constants.IDDELIMITER);//address_part
			affBuffer.append(Constants.IDDELIMITER);//citygroup
			if(city == null && state == null && country == null)
			{
				affBuffer.append(location);
			}
			else
			{
				location=null;
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
			affBuffer.append(Constants.IDDELIMITER);//country

			if(country!= null || (location!=null && location.indexOf(country)<0))
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
	public String formatISBN(String be,String bn,String bv,String bx)
	{
		StringBuffer isbnBuffer = new StringBuffer();

		if(be!=null)
		{
			if(be.indexOf("-")>-1)
			{
				be = be.replaceAll("-","");
			}

			if(be.indexOf(")")>-1)
			{
				be = be.replaceAll(")","");
			}

			isbnBuffer.append(Constants.IDDELIMITER);//isbnType
			isbnBuffer.append("13");
			isbnBuffer.append(Constants.IDDELIMITER);//isbnLength
			isbnBuffer.append("volume");
			isbnBuffer.append(Constants.IDDELIMITER);//isbnVolume
			isbnBuffer.append(be);
			//isbnBuffer.append(Constants.IDDELIMITER);//isbnValue
		}

		if(bn != null || bv != null || bx!=null)
		{
			isbnBuffer.append(Constants.AUDELIMITER);
		}

		if(bn!=null)
		{
			if(bn.indexOf("-")>-1)
			{
				bn = bn.replaceAll("-","");
			}

			if(bn.indexOf(")")>-1)
			{
				bn = bn.replaceAll(")","");
			}

			isbnBuffer.append(Constants.IDDELIMITER);//isbnType
			isbnBuffer.append("10");
			isbnBuffer.append(Constants.IDDELIMITER);//isbnLength
			isbnBuffer.append("volume");
			isbnBuffer.append(Constants.IDDELIMITER);//isbnVolume
			isbnBuffer.append(bn);
			//isbnBuffer.append(Constants.IDDELIMITER);//isbnValue
		}

		if(bv != null || bx!=null)
		{
			isbnBuffer.append(Constants.AUDELIMITER);
		}

		if(bx!=null)
		{
			if(bx.indexOf("-")>-1)
			{
				bx = bx.replaceAll("-","");
			}

			if(bx.indexOf(")")>-1)
			{
				bx = bx.replaceAll(")","");
			}

			isbnBuffer.append(Constants.IDDELIMITER);//isbnType
			isbnBuffer.append("10");
			isbnBuffer.append(Constants.IDDELIMITER);//isbnLength
			isbnBuffer.append("set");
			isbnBuffer.append(Constants.IDDELIMITER);//isbnVolume
			isbnBuffer.append(bx);
			//isbnBuffer.append(Constants.IDDELIMITER);//isbnValue
		}

		if(bx!=null)
		{
			isbnBuffer.append(Constants.AUDELIMITER);
		}

		if(bv!=null)
		{
			if(bv.indexOf("-")>-1)
			{
				bv = bv.replaceAll("-","");
			}

			if(bv.indexOf(")")>-1)
			{
				bv = bv.replaceAll(")","");
			}

			isbnBuffer.append(Constants.IDDELIMITER);//isbnType
			isbnBuffer.append("13");
			isbnBuffer.append(Constants.IDDELIMITER);//isbnLength
			isbnBuffer.append("set");
			isbnBuffer.append(Constants.IDDELIMITER);//isbnVolume
			isbnBuffer.append(bv);
			//isbnBuffer.append(Constants.IDDELIMITER);//isbnValue
		}

		return isbnBuffer.toString();
	}

	public String formatAffiliation(String affiliation,String city,String state,String province,String country)throws Exception
	{
		StringBuffer affBuffer = new StringBuffer();
		if(affiliation!=null || city!=null || state!=null || province!=null ||country!=null)
		{
			affBuffer.append(Constants.IDDELIMITER);//affid
			affBuffer.append(Constants.IDDELIMITER);//text
			if(affiliation!=null)
			{
				affBuffer.append(affiliation);
			}
			affBuffer.append(Constants.GROUPDELIMITER);//afforganization

			affBuffer.append(Constants.GROUPDELIMITER);//address-part
			if(city!=null)
			{
				affBuffer.append(city);
			}

			if(state==null)
			{
				state = province;
			}
			if(state!=null)
			{
				if(city!=null)
				{
					affBuffer.append(", ");//city
				}
				affBuffer.append(state);
			}
			affBuffer.append(Constants.GROUPDELIMITER);//country
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
				nameBuffer.append(Constants.IDDELIMITER);//id
				nameBuffer.append(Constants.IDDELIMITER);//initials
				nameBuffer.append(Constants.IDDELIMITER);//indexname
				nameBuffer.append(Constants.IDDELIMITER);//Degrees
				nameBuffer.append(lastName.trim());
				nameBuffer.append(Constants.IDDELIMITER);//Surname
				nameBuffer.append(givenName.trim());
				nameBuffer.append(Constants.IDDELIMITER);//givenName
				nameBuffer.append(Constants.IDDELIMITER);//Suffix
				nameBuffer.append(Constants.IDDELIMITER);//Nametext
				nameBuffer.append(Constants.IDDELIMITER);//text
				if(i<namesArray.length-1)
				{
					nameBuffer.append(Constants.AUDELIMITER);
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

				if(author.indexOf(" ")>-1)
				{
					lastName  = author.substring(0,author.indexOf(" "));
					givenName = author.substring(author.indexOf(" ")+1);
				}
				else
				{
					lastName = author;
				}

				if(lastName.indexOf(",")>-1)
				{
					lastName = lastName.replaceAll(",", "");
				}

                nameBuffer.append(i);    //starts from 1
				nameBuffer.append(Constants.IDDELIMITER);//sec
				nameBuffer.append(Constants.IDDELIMITER);//auid
				nameBuffer.append(affId);
				nameBuffer.append(Constants.IDDELIMITER);//afid
				nameBuffer.append(Constants.IDDELIMITER);//indexname
				nameBuffer.append(Constants.IDDELIMITER);//initials
				nameBuffer.append(lastName.trim());
				nameBuffer.append(Constants.IDDELIMITER);//Surname
				nameBuffer.append(Constants.IDDELIMITER);//Degrees
				nameBuffer.append(givenName.trim());
				nameBuffer.append(Constants.IDDELIMITER);//givenName
				nameBuffer.append(Constants.IDDELIMITER);//Suffix
				nameBuffer.append(Constants.IDDELIMITER);//Nametext
				nameBuffer.append(Constants.IDDELIMITER);//PrefnameInitials
				nameBuffer.append(Constants.IDDELIMITER);//PrefnameIndexedname
				nameBuffer.append(Constants.IDDELIMITER);//PrefnameDegrees
				nameBuffer.append(Constants.IDDELIMITER);//PrefnameSurname
				nameBuffer.append(Constants.IDDELIMITER);//PrefnameGivenname
				nameBuffer.append(Constants.IDDELIMITER);//Eaddress
				nameBuffer.append(Constants.AUDELIMITER);
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
				termsBuffer.append(Constants.AUDELIMITER);
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
				codesBuffer.append(Constants.AUDELIMITER);
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
				sponsorsBuffer.append(Constants.AUDELIMITER);
				sponsorsBuffer.append(sponsorsArray[i]);
			}
		}

		return sponsorsBuffer.toString();
	}


	public String formatPubAddress(String city, String state, String province, String country)
	{
		StringBuffer affBuffer = new StringBuffer();

		if(state == null)
		{
			state = province;
		}

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

