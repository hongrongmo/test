package org.ei.dataloading.inspec.loadtime;

import java.sql.*;
import java.io.*;
import java.text.*;
import java.util.*;

import org.apache.oro.text.perl.*;
import org.apache.oro.text.regex.*;
import org.ei.util.GUID;
import org.ei.dataloading.*;
import org.ei.xml.*;
import org.ei.dataloading.*;
import org.ei.common.*;

public class IBFCombiner
    extends Combiner
{

    Perl5Util perl = new Perl5Util();

    private static final String QUERY_FIELDS = "m_id, anum, rtype, su, aus, eds, ti, ab,oinfo,  pyr, cls, cvs, ocvs, ocls, fls, fjt, ojt, fttj,ottj, source, tsource, thlp,voliss, vol, iss, ipn, la, pub,  rnum, tc, cdate, cedate, cloc, ccnf, sorg, pnum, pas, cpat,iorg,ciorg, fdate, LOAD_NUMBER, SEQ_NUM";

    public IBFCombiner(CombinedWriter writer)
    {
        super(writer);
    }

    public static void main(String args[])
            throws Exception
    {
        String url = args[0];
        String driver = args[1];
        String username = args[2];
        String password = args[3];
        int loadNumber = Integer.parseInt(args[4]);
        int recsPerbatch = Integer.parseInt(args[5]);
        String operation = args[6];
        Combiner.TABLENAME = args[7];
        String environment = args[8].toLowerCase();

        CombinedWriter writer = new CombinedXMLWriter(recsPerbatch,
                                                      loadNumber,
                                                      "ibf");
        writer.setOperation(operation);
        IBFCombiner c = new IBFCombiner(writer);
        // extract the whole thing
        if(loadNumber == 0)
        {
            for(int yearIndex = 1898; yearIndex <= 1968; yearIndex++)
            {
                System.out.println("Processing year " + yearIndex + "...");
                c = new IBFCombiner(new CombinedXMLWriter(recsPerbatch, yearIndex,"ibf", environment));
                c.writeCombinedByYear(url,
                                driver,
                                username,
                                password,
                                yearIndex);
            }
        }
        else if (loadNumber ==1)
        {
            c.writeCombinedByTable(url,
                                   driver,
                                   username,
                                   password);                              
        }
        else if (loadNumber > 3000 || loadNumber < 1000)
        {
            c.writeCombinedByWeekNumber(url,
                                        driver,
                                        username,
                                        password,
                                        loadNumber);
        }     
        else
        {
            c.writeCombinedByYear(url,
                                driver,
                                username,
                                password,
                                loadNumber);
        }
    }

    public void writeCombinedByYearHook(Connection con,
                                        int year)
            throws Exception
    {
        Statement stmt = null;
        ResultSet rs = null;

        try
        {

            stmt = con.createStatement();
            System.out.println("Running the YEAR query...");
            rs = stmt.executeQuery("select " + QUERY_FIELDS + " from " + Combiner.TABLENAME + " where seq_num is not null and nvl(pyr,substr(su,1,4)) ='" + year + "'");
            writeRecs(rs);
            System.out.println("Wrote records.");
            this.writer.end();
            this.writer.flush();
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
    
    public void writeCombinedByTableHook(Connection con)
    		throws Exception
    		{
    			Statement stmt = null;
    			ResultSet rs = null;
    			try
    			{
    			
    				stmt = con.createStatement();
    				System.out.println("Running the query...");
    				String sqlQuery = "select * from " + Combiner.TABLENAME;
    				System.out.println(sqlQuery);
    				rs = stmt.executeQuery(sqlQuery);
    				
    				System.out.println("Got records ...from table::"+Combiner.TABLENAME);
    				writeRecs(rs);
    				System.out.println("Wrote records.");
    				this.writer.end();
    				this.writer.flush();
    			
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
                                        int loadN)
            throws Exception
    {
        Statement stmt = null;
        ResultSet rs = null;

        try
        {

            stmt = con.createStatement();
            System.out.println("Running the LOAD NUMBER query...");
            rs = stmt.executeQuery("select " + QUERY_FIELDS + " from " + Combiner.TABLENAME + " where seq_num is not null and LOAD_NUMBER = " + loadN);
            writeRecs(rs);
            System.out.println("Wrote records.");
            this.writer.end();
            this.writer.flush();
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

            rec.put(rec.DOCID, rs.getString("M_ID"));
            rec.put(rec.DATABASE, "ibf");
            rec.put(rec.LOAD_NUMBER, rs.getString("LOAD_NUMBER"));

            rec.put(rec.ACCESSION_NUMBER, rs.getString("ANUM"));

            String abString = getStringFromClob(rs.getClob("ab"));

            if (rs.getString("rtype") != null)
            {
                rec.put(rec.DOCTYPE,
                        getDocType(rs.getString("rtype")));
            }

            if (rs.getString("aus") != null)
            {
                rec.put(rec.AUTHOR, prepareAuthor(rs.getString("aus")));
            }
            else if (rs.getString("eds") != null)
            {
                rec.put(rec.EDITOR, prepareAuthor(rs.getString("eds")));
            }

            if (rs.getString("ti") != null)
            {
                rec.put(rec.TITLE, rs.getString("ti"));
            }

            if (rs.getString("oinfo") != null)
            {

                abString = abString + rs.getString("oinfo").substring(rs.getString("oinfo").indexOf("<ABS>"));

            }

            if (abString != null && abString.length() > 10)
            {
                rec.put(rec.ABSTRACT, abString);
            }

            if (rs.getString("pyr") != null)
            {
                rec.put(rec.PUB_YEAR, getPubYear(rs.getString("pyr")));
            }
            else
            {
               rec.put(rec.PUB_YEAR, getPubYear(rs.getString("su").substring(0, 4)));
            }

            //Indexing Terms
            if (rs.getString("cvs") != null || rs.getString("ocvs") != null)
            {
                StringBuffer cvs = new StringBuffer();
                if (rs.getString("cvs") != null)
                {
                    cvs.append(rs.getString("cvs"));
                }
                if (rs.getString("ocvs") != null)
                {
                    cvs.append("; ").append(getOrigCVS(rs.getString("ocvs")));
                }

                rec.put(rec.CONTROLLED_TERMS,
                        prepareMulti(cvs.toString()));
            }

            if (rs.getString("fls") != null)
            {
                rec.put(rec.UNCONTROLLED_TERMS,
                        prepareMulti(rs.getString("fls")));
            }

            if (rs.getString("cls") != null)
            {
                rec.put(rec.CLASSIFICATION_CODE,
                        prepareMulti(XMLWriterCommon.formatClassCodes(rs.getString("cls"))));
            }

            if (rs.getString("ocls") != null)
            {
                rec.put(rec.INDUSTRIALCODES,
                        prepareMulti(getOrigCLS(rs.getString("ocls"))));
            }

            String dis = getDiscipline(rs.getString("cls"));

            if (dis != null)
            {
                rec.put(rec.DISCIPLINE, prepareMulti(dis));
            }

            //Source Info
            if (rs.getString("fjt") != null)
            {
                rec.put(rec.SERIAL_TITLE,
                        rs.getString("fjt"));
            }
            else if (rs.getString("ojt") != null) rec.put(rec.SERIAL_TITLE,
                    rs.getString("ojt"));

            if (rs.getString("fttj") != null)
            {
                rec.put(rec.SERIAL_TITLE_TRANSLATION,
                        rs.getString("fttj"));

            }
            else if (rs.getString("ottj") != null)
            {
                rec.put(rec.SERIAL_TITLE_TRANSLATION,
                        rs.getString("ottj"));

            }

            rec.put(rec.VOLUME, rs.getString("vol"));
            rec.put(rec.ISSUE, rs.getString("iss"));
            rec.put(rec.STARTPAGE, getFirstPage(rs.getString("ipn")));

            if (rs.getString("source") != null || rs.getString("tsource") != null)
            {
                StringBuffer src = new StringBuffer();
                if (rs.getString("source") != null)
                {
                    src.append(getAdditionalSources(rs.getString("source")));
                }
                if (rs.getString("tsource") != null)
                {
                    src.append("; ").append(getAdditionalSources(rs.getString("tsource")));
                }
                rec.put(rec.SOURCE,
                        prepareMulti(src.toString()));

            }

            if (rs.getString("la") != null)
            {
                rec.put(rec.LANGUAGE,
                        prepareMulti(rs.getString("la")));
            }
            if (rs.getString("thlp") != null)
            {
                rec.put(rec.MONOGRAPH_TITLE,
                        rs.getString("thlp"));
            }

            if (rs.getString("pub") != null)
            {
                rec.put(rec.PUBLISHER_NAME,
                        rs.getString("pub"));
            }

            //Conference Info
            if (rs.getString("tc") != null)
            {
                rec.put(rec.CONFERENCE_NAME,
                        rs.getString("tc"));
            }

            //Put ccnf in country also ??
            if (rs.getString("cloc") != null || rs.getString("ccnf") != null)
            {
                StringBuffer buf = new StringBuffer();
                if (rs.getString("cloc") != null)
                    buf.append(rs.getString("cloc"));
                if (rs.getString("ccnf") != null)
                    buf.append(" ").append(rs.getString("ccnf"));

                rec.put(rec.CONFERENCE_LOCATION,
                            buf.toString());
            }

            if (rs.getString("cdate") != null || rs.getString("cedate") != null)
            {
                StringBuffer buf = new StringBuffer();
                if (rs.getString("cdate") != null)
                {
                    buf.append(rs.getString("cdate"));
                }
                if (rs.getString("cedate") != null)
                {
                    buf.append("; ").append(rs.getString("cedate"));
                }
                rec.put(rec.MEETING_DATE, buf.toString());

            }
            if (rs.getString("sorg") != null)
            {
                rec.put(rec.SPONSOR_NAME,
                        rs.getString("sorg"));
            }

            if (rs.getString("rnum") != null)
            {
                rec.put(rec.REPORTNUMBER,
                        rs.getString("rnum"));
            }

            //Patent Info
            if (rs.getString("pnum") != null)
            {
                rec.put(rec.PATENT_NUMBER,
                        rs.getString("pnum"));
            }

            // COMPANIES ?
            if(rs.getString("pas") != null)
            {
                rec.put(rec.AUTHOR_AFFILIATION, prepareMulti(rs.getString("pas")));
            }

            // new added ?
            if(rs.getString("fdate") != null)
            {
                rec.put(rec.PATENT_FILING_DATE,
                        rs.getString("fdate"));
            }

            if(rs.getString("cpat") != null)
            {
                String countryFormatted = Country.formatCountry(rs.getString("cpat"));
                if (countryFormatted != null)
                {
                    rec.put(EVCombinedRec.COUNTRY, countryFormatted);
                }
            }

            // AFF_LOC ?
            // use cpub for country ??
            if(rs.getString("iorg") != null)
            {
                rec.put(rec.NOTES,
                        prepareMulti(rs.getString("iorg")));

                String countryFormatted = Country.formatCountry(rs.getString("ciorg"));
                if (countryFormatted != null &&
                    !rec.containsKey(EVCombinedRec.COUNTRY))
                {
                    rec.put(EVCombinedRec.COUNTRY, countryFormatted);
                    rec.put(EVCombinedRec.AFFILIATION_LOCATION, countryFormatted);
                }
            }

            rec.put(rec.DEDUPKEY,
                    getDedupKey(rec.get(rec.ISSN),
                            rec.get(rec.CODEN),
                            rs.getString("vol"),
                            rs.getString("iss"),
                            rs.getString("ipn")));

            if(rs.getString("seq_num") != null)
            {
                rec.put(EVCombinedRec.PARENT_ID, rs.getString("seq_num"));
            }

            writer.writeRec(rec);

        }
    }

    private String[] prepareMulti(String multiString)
            throws Exception
    {
        AuthorStream astream = new AuthorStream(new ByteArrayInputStream(multiString.getBytes()));
        String s = null;
        ArrayList list = new ArrayList();
        while ((s = astream.readAuthor()) != null)
        {
            s = s.trim();
            if (s.length() > 0)
            {
                list.add(s);
            }
        }

        return (String[]) list.toArray(new String[1]);

    }

    private String[] prepareAuthor(String aString)
            throws Exception
    {
        StringBuffer buf = new StringBuffer();
        AuthorStream astream = new AuthorStream(new ByteArrayInputStream(aString.getBytes()));
        String s = null;
        ArrayList list = new ArrayList();

        while ((s = astream.readAuthor()) != null)
        {
            s = s.trim();
            if (s.length() > 0)
            {
                s = stripAnon(s);
                s = s.trim();

                list.add(s);
            }
        }

        return (String[]) list.toArray(new String[1]);

    }

    private String stripAnon(String line)
    {
        line = perl.substitute("s/\\banon\\b/ /gi", line);
        line = perl.substitute("s/\\(ed\\.\\)/ /gi", line);
        return line;
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



    private String getFirstPage(String v)
    {

        MatchResult mResult = null;
        if (v == null)
        {
            return null;
        }

        if (perl.match("/[A-Z]?[0-9][0-9]*/", v))
        {
            mResult = perl.getMatch();
        }
        else
        {
            return null;
        }

        return mResult.toString();
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

    private String getPubYear(String y)
    {
        String i = null;

        if (perl.match("/[1-9][0-9][0-9][0-9]/", y))
        {
            MatchResult mResult = perl.getMatch();
            i = mResult.toString();
        }
        else
        {
            System.err.println("Bad Pub Year:" + y + " Stopping the extract");
            System.exit(1);
        }
        return i;
    }

    private String getAdditionalSources(String s)
    {
        StringTokenizer st = new StringTokenizer(s, "~");
        String strToken;
        StringBuffer strResult = new StringBuffer();
        while (st.hasMoreTokens())
        {
            strToken = st.nextToken();
            ArrayList l = getPipeFormat(strToken);
            strResult.append((String) l.get(0)).append(";");
        }
        return strResult.toString();
    }

    private String getOrigCVS(String s)
    {
        StringTokenizer st = new StringTokenizer(s, "~");
        String strToken;
        StringBuffer strResult = new StringBuffer();
        while (st.hasMoreTokens())
        {
            strToken = st.nextToken();
            ArrayList l = getPipeFormat(strToken);
            for (int i = 0; i < 3 && i < l.size(); i++)
                strResult.append((String) l.get(i)).append(";");
        }
        return strResult.toString();
    }

    private String getOrigCLS(String str)
    {

        StringBuffer buf = new StringBuffer();
        String[] cls = str.split("~ ");
        for (int i = 0; i < cls.length; i++)
        {

            String[] term = cls[i].split("\\|");
            for (int j = 1; j < term.length; j++)
            {
                if (!term[j].equals("")) buf.append(term[j]).append(";");
            }

        }

        return buf.toString();
    }

    private String[] getDocType(String newdocType)
    {
        StringBuffer newtype = new StringBuffer();
        ArrayList list = new ArrayList();

        if(newdocType.equals("21")||newdocType.equals("22")||newdocType.equals("23"))
        {
            list.add("JA");
            list.add("IJ");
            if(newdocType.equals("22"))
            {
                list.add("OA");
            }
            else if(newdocType.equals("23"))
            {
                list.add("TA");
            }

        }
        else if(newdocType.equals("60")||newdocType.equals("61")||newdocType.equals("62")||newdocType.equals("63"))
        {
            list.add("CA");
            if(newdocType.equals("61"))
            {
                list.add("IJ");
            }
            else if(newdocType.equals("62"))
            {
                list.add("IJ");
                list.add("OA");
            }
            else if(newdocType.equals("63"))
            {
                list.add("IJ");
                list.add("TA");
            }
        }
        else if(newdocType.equals("50")||newdocType.equals("51")||newdocType.equals("52")||newdocType.equals("53"))
        {
            list.add("CP");
            if(newdocType.equals("51"))
            {
                list.add("IJ");
            }
            else if(newdocType.equals("52"))
            {
                list.add("IJ");
                list.add("OA");
            }

            else if(newdocType.equals("53"))
            {
                list.add("IJ");
                list.add("TA");
            }
        }
        else if(newdocType.equals("40"))
        {
        	//change for book project by hmo at 5/18/2017
            //list.add("MC");
        	list.add("CH");
        }
        else if(newdocType.equals("30"))
        {
        	//change for book project by hmo at 5/18/2017
            //list.add("MR");
        	list.add("BK");
        }
        else if(newdocType.equals("12"))
        {
            list.add("RC");
        }
        else if(newdocType.equals("11"))
        {
            list.add("RR");
        }
        else if(newdocType.equals("10"))
        {
            list.add("DS");
        }
        else if(newdocType.equals("80"))
        {
            list.add("PA");
        }
        else
        {
            list.add("NA");
        }

        return (String[])list.toArray(new String[1]);

    }

    private String getDiscipline(String dis)
    {
        StringBuffer buf = new StringBuffer();

        if (dis == null)
        {
            return null;
        }

        StringTokenizer t = new StringTokenizer(dis, ";");
        char[] carray = new char[1];
        while (t.hasMoreTokens())
        {

            String token = t.nextToken().trim();
            if (token.length() > 0 && token != null)
            {
                carray[0] = (token.trim()).charAt(0);
                String s = new String(carray);
                buf.append(s + "DCLS; ");
            }
        }

        return buf.toString();
    }


    private String getStringFromClob(Clob clob)
                throws Exception
    {
        String temp = null;
        if (clob != null)
        {
            temp = clob.getSubString(1, (int) clob.length());
            if (temp != null && temp.indexOf("<ABS>") > -1)
            {
                temp = temp.substring(temp.indexOf("<ABS>"));
            }

        }

        return temp;
    }

    private ArrayList getPipeFormat(String strSource)
    {
        int start = 0;
        int end = 0;
        ArrayList al = new ArrayList();

        while (strSource.indexOf("|", start) > -1)
        {
            end = strSource.indexOf("|", start);
            al.add(strSource.substring(start, end).trim());
            start = end + 1;
        }

        al.add(strSource.substring(start).trim());

        return al;
    }

}
