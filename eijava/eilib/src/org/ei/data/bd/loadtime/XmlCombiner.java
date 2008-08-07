package org.ei.data.bd.loadtime;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.MatchResult;
import org.ei.data.*;
import org.ei.data.bd.*;
import org.ei.data.bd.loadtime.*;


import java.io.*;
import org.ei.util.GUID;

public class XmlCombiner
    extends CombinerTimestamp
{

    Perl5Util perl = new Perl5Util();

    private int exitNumber;

    private static String tablename;

    private static HashMap issnARFix = new HashMap();

    static
    {  //ISSNs with AR field problem
        issnARFix.put("00913286", "");
        issnARFix.put("10833668", "");
        issnARFix.put("10179909", "");
        issnARFix.put("15393755", "");
        issnARFix.put("00319007", "");
        issnARFix.put("10502947", "");
        issnARFix.put("00036951", "");
        issnARFix.put("00218979", "");
        issnARFix.put("00219606", "");
        issnARFix.put("00346748", "");
        issnARFix.put("1070664X", "");
        issnARFix.put("10706631", "");
        issnARFix.put("00948276", "");
        issnARFix.put("00431397", "");
    }

    public static void main(String args[])
        throws Exception
    {
        String url = args[0];
        String driver = args[1];
        String username = args[2];
        String password = args[3];
        int loadNumber = Integer.parseInt(args[4]);
        int recsPerfile = Integer.parseInt(args[5]);
        int exitAt = Integer.parseInt(args[6]);
        tablename = args[7];
        long timestamp=0l;
        if(args.length==9)
            timestamp = Long.parseLong(args[8]);

        Combiner.TABLENAME = tablename;
        Combiner.EXITNUMBER = exitAt;
        System.out.println(Combiner.TABLENAME);
        System.out.println("TEST");

        String dbname = "cpx";
        if (timestamp > 0)
            dbname=dbname+"cor";
        CombinedWriter writer = new CombinedXMLWriter(recsPerfile,
                                                      loadNumber,
                                                      dbname);

        XmlCombiner c = new XmlCombiner(writer);
        if (timestamp==0 && (loadNumber > 3000 || loadNumber < 1000))
        {

           c.writeCombinedByWeekNumber(url, driver, username, password, loadNumber);
        }
        else if(timestamp > 0)
        {

           c.writeCombinedByTimestamp(url, driver, username, password, timestamp);
        }
        else
        {

           c.writeCombinedByYear(url, driver, username, password, loadNumber);
        }
    }

    public XmlCombiner(CombinedWriter writer)
    {
        super(writer);
    }

    public void writeCombinedByYearHook(Connection con,
                                        int year)
        throws Exception
    {

        Statement stmt = null;
        ResultSet rs = null;
        try
        {

            this.writer.begin();
            stmt = con.createStatement();
            System.out.println("Running the query...");
            rs = stmt.executeQuery("select SPECIESTERM,REGIONALTERM,DATABASE,CITATIONLANGUAGE,CITATIONTITLE,CITTYPE,ABSTRACTDATA,PII,PUI,COPYRIGHT,M_ID,accessnumber,datesort,author,author_1,AFFILIATION,AFFILIATION_1,CODEN,ISSUE,CLASSIFICATIONCODE,CONTROLLEDTERM,UNCONTROLLEDTERM,MAINHEADING,TREATMENTCODE,LOADNUMBER,SOURCETYPE,SOURCECOUNTRY,SOURCEID,SOURCETITLE,SOURCETITLEABBREV,ISSUETITLE,ISSN,EISSN,ISBN,VOLUME,PAGE,PAGECOUNT,ARTICLENUMBER,PUBLICATIONYEAR,PUBLICATIONDATE,EDITORS,PUBLISHERNAME,PUBLISHERADDRESS,PUBLISHERELECTRONICADDRESS,REPORTNUMBER,CONFNAME, CONFCATNUMBER,CONFCODE,CONFLOCATION,CONFDATE,CONFSPONSORS,CONFERENCEPARTNUMBER, CONFERENCEPAGERANGE, CONFERENCEPAGECOUNT, CONFERENCEEDITOR, CONFERENCEORGANIZATION,CONFERENCEEDITORADDRESS,TRANSLATEDSOURCETITLE,VOLUMETITLE,DOI  from " + Combiner.TABLENAME + " where PUBLICATIONYEAR ='" + year + "' AND loadnumber != 0 and loadnumber < 1000000");
            System.out.println("Got records ...");
            writeRecs(rs);
            System.out.println("Wrote records.");
            this.writer.end();

        }
        finally
        {

            if (rs != null)
            {
                try
                {
                    rs.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            if (stmt != null)
            {
                try
                {
                    stmt.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    private void writeRecs(ResultSet rs)
        throws Exception
    {
        int i = 0;

        while (rs.next())
        {
            EVCombinedRec rec = new EVCombinedRec();
            ++i;

			//System.out.println("Combiner.EXITNUMBER= "+Combiner.EXITNUMBER);
            if (Combiner.EXITNUMBER != 0 && i > Combiner.EXITNUMBER)
            {
                break;
            }

            if (validYear(rs.getString("PUBLICATIONYEAR")))
            {
                if(rs.getString("AUTHOR") != null)
                {
					String authorString = rs.getString("AUTHOR");
					if(rs.getString("AUTHOR_1") !=null)
					{
						authorString=authorString+rs.getString("AUTHOR_1");
					}
                                        rec.put(EVCombinedRec.AUTHOR, prepareBdAuthor(authorString));

                    if (rs.getString("AFFILIATION") != null)
                    {
						String affiliation = rs.getString("AFFILIATION");
						if(rs.getString("AFFILIATION_1")!=null)
						{
							affiliation = affiliation+rs.getString("AFFILIATION_1");
						}
                   		BdAffiliations aff = new BdAffiliations(affiliation);

				        rec.put(EVCombinedRec.AUTHOR_AFFILIATION,  aff.getSearchValue());
				        rec.put(EVCombinedRec.AFFILIATION_LOCATION,  aff.getLocationsSearchValue());
                        rec.put(EVCombinedRec.COUNTRY,  aff.getCountriesSearchValue());
					}
				}

                if (rs.getString("EDITORS") != null)
                {
                    rec.put(EVCombinedRec.EDITOR, prepareEditor(rs.getString("EDITORS")));
                }

                if (rs.getString("CITATIONTITLE") != null)
                {
                   rec.put(EVCombinedRec.TITLE, prepareCitationTitle(rs.getString("CITATIONTITLE")));
                }

                if (rs.getString("CITATIONTITLE") != null)
				{
				   rec.put(EVCombinedRec.TRANSLATED_TITLE, prepareTranslatedCitationTitle(rs.getString("CITATIONTITLE")));
                }

                if (rs.getString("TRANSLATEDSOURCETITLE") != null)
                {
                    rec.put(EVCombinedRec.TRANSLATED_TITLE, prepareMulti(rs.getString("TRANSLATEDSOURCETITLE")));
                }

                if (rs.getString("VOLUMETITLE") != null)
                {
                    rec.put(EVCombinedRec.VOLUME_TITLE, rs.getString("VOLUMETITLE"));
                }

				String abString = getStringFromClob(rs.getClob("ABSTRACTDATA"));
                if (abString != null)
                {
                    rec.put(EVCombinedRec.ABSTRACT, abString);
                }

                if (rs.getString("CONTROLLEDTERM") != null)
                {
                    rec.put(EVCombinedRec.CONTROLLED_TERMS, prepareMulti(rs.getString("CONTROLLEDTERM")));
                }

                if (rs.getString("UNCONTROLLEDTERM") != null)
                {
                    rec.put(EVCombinedRec.UNCONTROLLED_TERMS, prepareMulti(rs.getString("UNCONTROLLEDTERM")));
                }
                else if(rs.getString("SPECIESTERM") != null)
                {
					 rec.put(EVCombinedRec.UNCONTROLLED_TERMS, prepareMulti(rs.getString("SPECIESTERM")));
				}

				if (rs.getString("REGIONALTERM") != null)
				{
				     rec.put(EVCombinedRec.CHEMICALTERMS, prepareMulti(rs.getString("REGIONALTERM")));
                }

				String[] issnArray = null;
                if (rs.getString("ISSN") != null && rs.getString("EISSN") != null)
                {
					issnArray = new String[2];
					issnArray[0] = rs.getString("ISSN");
					issnArray[1] = rs.getString("EISSN");
                }
                else if (rs.getString("EISSN") != null)
				{
					issnArray = new String[1];
					issnArray[0] = rs.getString("EISSN");
                }
                else if(rs.getString("ISSN") != null)
				{
					issnArray = new String[1];
					issnArray[0] = rs.getString("ISSN");
				}

				if(issnArray != null)
				{
                	rec.put(EVCombinedRec.ISSN, issnArray);
				}

                if (rs.getString("CODEN") != null)
                {
                    rec.put(EVCombinedRec.CODEN, rs.getString("CODEN"));
                }

				String isbnString = rs.getString("ISBN");
                if (isbnString!= null)
                {
                    rec.put(EVCombinedRec.ISBN, prepareISBN(isbnString));
                }

                String st = rs.getString("SOURCETITLE");
                String sta = rs.getString("SOURCETITLEABBREV");
                if (st == null)
                {
                    st = sta;
                }

                if (st != null)
                {
                    rec.put(EVCombinedRec.SERIAL_TITLE, st);
                }

                if(sta != null)
                {
					 rec.put(EVCombinedRec.ABBRV_SRC_TITLE, sta);
				}

                if (rs.getString("MAINHEADING") != null)
                {
                    rec.put(EVCombinedRec.MAIN_HEADING, prepareMulti(rs.getString("MAINHEADING")));
                }

                if (rs.getString("PUBLISHERNAME") != null)
                {
                    rec.put(EVCombinedRec.PUBLISHER_NAME, rs.getString("PUBLISHERNAME"));
                }

                if (rs.getString("TREATMENTCODE") != null)
                {
                    rec.put(EVCombinedRec.TREATMENT_CODE,
                            prepareMulti(getTreatmentCode(rs.getString("TREATMENTCODE"))));
                }

                String la = rs.getString("CITATIONLANGUAGE");

                if (la != null)
                {
                 	rec.put(EVCombinedRec.LANGUAGE,prepareLanguage(la));
                }

				String docType = rs.getString("CITTYPE");
				if(docType != null)
				{
					boolean confCodeFlag = false;
					if(rs.getString("CONFCODE") != null)
					{
						confCodeFlag = true;
					}

					String ct = null;
					if((ct = getCitationType(docType,confCodeFlag)) != null)
					{
						rec.put(EVCombinedRec.DOCTYPE,ct);
					}
				}

                if (rs.getString("CLASSIFICATIONCODE") != null)
                {
                    rec.put(EVCombinedRec.CLASSIFICATION_CODE,
                            prepareMulti(formatClassCodes(rs.getString("CLASSIFICATIONCODE"))));
                }

                if (rs.getString("CONFCODE") != null)
                {
                    rec.put(EVCombinedRec.CONFERENCE_CODE, rs.getString("CONFCODE"));
                }

                if (rs.getString("CONFNAME") != null)
                {
                    rec.put(EVCombinedRec.CONFERENCE_NAME, rs.getString("CONFNAME"));
                }

                String cl = rs.getString("CONFLOCATION");

                if (cl !=null && cl.length() > 2)
                {
                    rec.put(EVCombinedRec.CONFERENCE_LOCATION,prepareConfLocation(cl) );
                }

                if (rs.getString("CONFDATE") != null)
                {
                    rec.put(EVCombinedRec.MEETING_DATE, rs.getString("CONFDATE"));
                }

                if (rs.getString("CONFSPONSORS") != null)
                {
                    rec.put(EVCombinedRec.SPONSOR_NAME, prepareMulti(rs.getString("CONFSPONSORS")));
                }

                if (rs.getString("CONFERENCEORGANIZATION") != null)
				{
				    rec.put(EVCombinedRec.CONFERENCEAFFILIATIONS, prepareMulti(rs.getString("CONFERENCEORGANIZATION")));
                }

                if (rs.getString("CONFERENCEEDITOR") != null)
				{
					rec.put(EVCombinedRec.CONFERENCEEDITORS, prepareEditor(rs.getString("CONFERENCEEDITOR")));
                }

				if (rs.getString("CONFERENCEPARTNUMBER") != null)
				{
					rec.put(EVCombinedRec.CONFERENCEPARTNUMBER,rs.getString("CONFERENCEPARTNUMBER"));
				}

				if (rs.getString("CONFERENCEPAGERANGE") != null)
				{
					rec.put(EVCombinedRec.CONFERENCEPAGERANGE,rs.getString("CONFERENCEPAGERANGE"));
				}

				if (rs.getString("CONFERENCEPAGECOUNT") != null)
				{
					rec.put(EVCombinedRec.CONFERENCENUMBERPAGES,rs.getString("CONFERENCEPAGECOUNT"));
				}

                if (rs.getString("ISSUETITLE") != null)
                {
                    rec.put(EVCombinedRec.MONOGRAPH_TITLE, rs.getString("ISSUETITLE"));
                }

                rec.put(EVCombinedRec.DOCID, rs.getString("M_ID"));

                rec.put(EVCombinedRec.DATABASE, rs.getString("DATABASE"));

                rec.put(EVCombinedRec.LOAD_NUMBER, rs.getString("LOADNUMBER"));

                if (rs.getString("PUBLICATIONYEAR") != null)
                {
                    rec.put(EVCombinedRec.PUB_YEAR, rs.getString("PUBLICATIONYEAR"));
                }

                rec.put(EVCombinedRec.DEDUPKEY,
                        getDedupKey(rec.getString("ISSN"),
                                    rec.getString("CODEN"),
                                    rs.getString("VOLUME"),
                                    rs.getString("ISSUE"),
                                    getPage(rs.getString("PAGE"), rs.getString("ARTICLENUMBER"), issnArray)));

                rec.put(EVCombinedRec.VOLUME, rs.getString("VOLUME"));
                rec.put(EVCombinedRec.ISSUE, rs.getString("ISSUE"));
                //rec.put(EVCombinedRec.STARTPAGE, getFirstPage(getPage(rs.getString("PAGE"), rs.getString("ARTICLENUMBER"), rs.getString("ISSN"))));
				rec.put(EVCombinedRec.STARTPAGE, getPage(getFirstPage(rs.getString("PAGE")),rs.getString("ARTICLENUMBER"), issnArray));

                rec.put(EVCombinedRec.ACCESSION_NUMBER,
                        rs.getString("ACCESSNUMBER"));

                if (rs.getString("REPORTNUMBER") != null)
                {
                 	rec.put(EVCombinedRec.REPORTNUMBER,rs.getString("REPORTNUMBER"));
				}

				if(rs.getString("DOI")!=null)
				{
                	rec.put(EVCombinedRec.DOI,rs.getString("DOI"));
				}

				if(rs.getString("COPYRIGHT") != null)
				{
					rec.put(EVCombinedRec.COPYRIGHT,rs.getString("COPYRIGHT"));
				}

				if(rs.getString("PII") != null)
				{
					rec.put(EVCombinedRec.PII,rs.getString("PII"));
				}

				if(rs.getString("PUI") != null)
				{
                	rec.put(EVCombinedRec.PUI,rs.getString("PUI"));
				}

                rec.put(EVCombinedRec.PUB_SORT, Integer.toString(i));

                this.writer.writeRec(rec);
            }

        }
    }



	private String formatClassCodes(String c)
	{
	    if (c == null)
	    {
	    	return null;
	    }

	  	return c.replaceAll("\\.","DQD");

    }


	private String[] prepareBdAuthor(String bdAuthor)
	{
		if(bdAuthor != null && !bdAuthor.trim().equals(""))
		{
			BdAuthors aus = new BdAuthors(bdAuthor);
			return aus.getSearchValue();
		}
		return null;
	}
	private String prepareConfLocation(String coAff)
	{
		if(coAff != null && !coAff.trim().equals(""))
		{
			BdConfLocations aff = new BdConfLocations(coAff);
			return aff.getSearchValue();
		}
		return null;

	}
	private String[] prepareISBN(String isbnString) throws Exception
	{
		List list = new ArrayList();
		if(isbnString != null)
		{
			BdIsbn isbn = new BdIsbn(isbnString);
			List isbnList = isbn.getISBN();
			for(int i=0;i<isbnList.size();i++)
			{
				BdIsbn isbnObject = (BdIsbn)isbnList.get(i);

				if(isbnObject.getValue() !=null)
				{
					list.add(isbnObject.getValue());
				}
			}
		}

		return (String[])list.toArray(new String[1]);
	}

	private String[] prepareCitationTitle(String citationTitle) throws Exception
	{
		List list = new ArrayList();
		if(citationTitle != null)
		{
			BdCitationTitle ct = new BdCitationTitle(citationTitle);
			List ctList = ct.getCitationTitle();

			for(int i=0;i<ctList.size();i++)
			{
				BdCitationTitle ctObject = (BdCitationTitle)ctList.get(i);

				if(ctObject.getTitle() !=null)
				{
					list.add(ctObject.getTitle());
				}

			}
		}


		return (String[])list.toArray(new String[1]);
	}

	private String[] prepareTranslatedCitationTitle(String citationTitle) throws Exception
	{
		List list = new ArrayList();
		if(citationTitle != null)
		{
			BdCitationTitle ct = new BdCitationTitle(citationTitle);
			List ctList = ct.getTranslatedCitationTitle();

			for(int i=0;i<ctList.size();i++)
			{
				BdCitationTitle ctObject = (BdCitationTitle)ctList.get(i);

				if(ctObject.getTitle() !=null)
				{
					list.add(ctObject.getTitle());
				}

			}
		}
			return (String[])list.toArray(new String[1]);
	}

	private String[] prepareEditor(String bdEditor)
	{
		if(bdEditor != null && !bdEditor.trim().equals(""))
		{
			BdEditors ed = new BdEditors(bdEditor);
			return ed.getSearchValue();
		}
		return null;
	}


	private String[] prepareMulti(String multiString)
	        throws Exception
    {
		String[] multiStringArray = multiString.split(BdParser.AUDELIMITER,-1);
		return multiStringArray;
	}

	private String[] prepareLanguage(String multiString)
		throws Exception
	{
		String[] languages = multiString.split(BdParser.IDDELIMITER);
		for(int i = 0; i < languages.length; i++)
		{
			languages[i]=Language.getIso639Language(languages[i]);
		}
		return languages;
    }


    private String getFirstNumber(String v)
    {

        MatchResult mResult = null;
        if (v == null)
        {
            return null;
        }

        if (perl.match("/[1-9][0-9]*/", v))
        {
            mResult = perl.getMatch();
        }
        else
        {
            return null;
        }

        return mResult.toString();
    }

    public String getPage(String xp,
                          String ar,
                          String[] issn)
    {
        String strPage = null;

        if(xp != null)
        {
            strPage = xp;
        }


        if(ar != null && issn != null) // Records with AR field Fix
        {
			for(int i=0;i<issn.length;i++)
			{
				if(issn[i] != null && issnARFix.containsKey(issn[i].toUpperCase().replaceAll("-",""))) // Check ISSN for AR problem
				{
					strPage=ar;
					break;
				}
			}
        }

        return strPage;
    }


    private String getFirstPage(String v)
    {
		BdPage pages = new BdPage(v);
		return pages.getStartPage();
	}


    private String getDedupKey(String issn,
                               String coden,
                               String volume,
                               String issue,
                               String page)
        throws Exception
    {

        String firstVolume = getFirstNumber(volume);
        String firstIssue = getFirstNumber(issue);
        String firstPage = getFirstPage(page);

        if ((issn == null && coden == null) ||
                firstVolume == null ||
                firstIssue == null ||
                firstPage == null)
        {
            return (new GUID()).toString();
        }

        StringBuffer buf = new StringBuffer();

        if (issn != null)
        {
            buf.append(perl.substitute("s/-//g", issn));
        }
        else
        {
            buf.append(coden);
        }

        buf.append("vol" + firstVolume);
        buf.append("is" + firstIssue);
        buf.append("pa" + firstPage);

        return buf.toString().toLowerCase();

    }

    private String getTreatmentCode(String tc)
    {
        StringBuffer tbuff = new StringBuffer();

        for (int i = 0; i < tc.length(); ++i)
        {
            char c = tc.charAt(i);

            if (c == 'A')
            {
                tbuff.append("APP");
            }
            else if (c == 'B')
            {
                tbuff.append("BIO");
            }
            else if (c == 'E')
            {
                tbuff.append("ECO");
            }
            else if (c == 'X')
            {
                tbuff.append("EXP");
            }
            else if (c == 'G')
            {
                tbuff.append("GEN");
            }
            else if (c == 'H')
            {
                tbuff.append("HIS");
            }
            else if (c == 'L')
            {
                tbuff.append("LIT");
            }
            else if (c == 'M')
            {
                tbuff.append("MAN");
            }
            else if (c == 'N')
            {
                tbuff.append("NUM");
            }
            else if (c == 'T')
            {
                tbuff.append("THR");
            }
            else if(c < 32)
            {
				tbuff.append(c);
			}
            else
            {
                tbuff.append("NA");
            }
        }

        return tbuff.toString();
    }

    private String getCitationType(String ct,boolean flag)
	{
		return BdDocumentType.getDocType(ct,flag);
    }



    private boolean validYear(String year)
    {
        if (year == null)
        {
            return false;
        }

        if (year.length() != 4)
        {
            return false;
        }

        return perl.match("/[1-9][0-9][0-9][0-9]/", year);
    }

    private String formatConferenceLoc(String ms,
                                       String mc,
                                       String mv,
                                       String my)
    {
        StringBuffer b = new StringBuffer(" ");
        if (ms != null)
        {
            b.append(ms + ", ");
        }

        if (mc != null)
        {
            b.append(mc + ", ");
        }

        if (mv != null)
        {
            b.append(mv + ", ");
        }

        if (my != null)
        {
            b.append(my);
        }

        return b.toString();

    }

    private String getStringFromClob(Clob clob) throws Exception
    {
        String temp = null;
        if (clob != null)
        {
            temp = clob.getSubString(1, (int) clob.length());
        }

        return temp;
    }
    public void writeCombinedByTimestampHook(Connection con, long timestamp) throws Exception
    {
            Statement stmt = null;
            ResultSet rs = null;

            try
            {

                this.writer.begin();
                stmt = con.createStatement();
                rs = stmt.executeQuery("select SPECIESTERM,REGIONALTERM,DATABASE,CITATIONLANGUAGE,CITATIONTITLE,CITTYPE,ABSTRACTDATA,PII,PUI,COPYRIGHT,M_ID,accessnumber,datesort,author,author_1,AFFILIATION,AFFILIATION_1,CODEN,ISSUE,CLASSIFICATIONCODE,CONTROLLEDTERM,UNCONTROLLEDTERM,MAINHEADING,TREATMENTCODE,LOADNUMBER,SOURCETYPE,SOURCECOUNTRY,SOURCEID,SOURCETITLE,SOURCETITLEABBREV,ISSUETITLE,ISSN,EISSN,ISBN,VOLUME,PAGE,PAGECOUNT,ARTICLENUMBER,PUBLICATIONYEAR,PUBLICATIONDATE,EDITORS,PUBLISHERNAME,PUBLISHERADDRESS,PUBLISHERELECTRONICADDRESS,REPORTNUMBER,CONFNAME, CONFCATNUMBER,CONFCODE,CONFLOCATION,CONFDATE,CONFSPONSORS,CONFERENCEPARTNUMBER, CONFERENCEPAGERANGE, CONFERENCEPAGECOUNT, CONFERENCEEDITOR, CONFERENCEORGANIZATION,CONFERENCEEDITORADDRESS,TRANSLATEDSOURCETITLE,VOLUMETITLE,DOI from " + Combiner.TABLENAME + " where loadnumber != 0 and loadnumber < 1000000");
				System.out.println("Got records1 ...");
                writeRecs(rs);
                this.writer.end();

            }
            finally
            {

                if (rs != null)
                {
                    try
                    {
                        rs.close();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }

                if (stmt != null)
                {
                    try
                    {
                        stmt.close();
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
            }

    }
    public void writeCombinedByWeekHook(Connection con,
                                        int weekNumber)
        throws Exception
    {
        Statement stmt = null;
        ResultSet rs = null;

        try
        {
            this.writer.begin();
            stmt = con.createStatement();
            rs = stmt.executeQuery("select SPECIESTERM,REGIONALTERM,DATABASE,CITATIONLANGUAGE, CITATIONTITLE,CITTYPE,ABSTRACTDATA,PII,PUI,COPYRIGHT,M_ID,accessnumber,datesort,author,author_1,AFFILIATION,AFFILIATION_1,CODEN,ISSUE,CLASSIFICATIONCODE,CONTROLLEDTERM,UNCONTROLLEDTERM,MAINHEADING,TREATMENTCODE,LOADNUMBER,SOURCETYPE,SOURCECOUNTRY,SOURCEID,SOURCETITLE,SOURCETITLEABBREV,ISSUETITLE,ISSN,EISSN,ISBN,VOLUME,PAGE,PAGECOUNT,ARTICLENUMBER,PUBLICATIONYEAR,PUBLICATIONDATE,EDITORS,PUBLISHERNAME,PUBLISHERADDRESS,PUBLISHERELECTRONICADDRESS,REPORTNUMBER,CONFNAME, CONFCATNUMBER,CONFCODE,CONFLOCATION,CONFDATE,CONFSPONSORS,CONFERENCEPARTNUMBER, CONFERENCEPAGERANGE, CONFERENCEPAGECOUNT, CONFERENCEEDITOR, CONFERENCEORGANIZATION,CONFERENCEEDITORADDRESS,TRANSLATEDSOURCETITLE,VOLUMETITLE,DOI  from " + Combiner.TABLENAME + " where loadnumber =" + weekNumber +" AND loadnumber != 0 and loadnumber < 1000000");
			System.out.println("Got records2 ...");

            writeRecs(rs);
            this.writer.end();

        }
        finally
        {

            if (rs != null)
            {
                try
                {
                    rs.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

            if (stmt != null)
            {
                try
                {
                    stmt.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }

    }

}