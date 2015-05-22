package org.ei.dataloading.inspec.loadtime;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.regex.*;

import org.ei.domain.*;
import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.MatchResult;
import org.ei.dataloading.CombinedWriter;
import org.ei.dataloading.CombinedXMLWriter;
import org.ei.dataloading.Combiner;
import org.ei.util.GUID;
import org.ei.common.*;
import org.ei.common.inspec.*;
import org.ei.dataloading.EVCombinedRec;
import org.ei.dataloading.XMLWriterCommon;


public class INSPECCombiner
    extends Combiner
{

    Perl5Util perl = new Perl5Util();
    private static String tablename;
    //private static final Database INS_DATABASE = new InspecDatabase();
	private static final String databaseIndexName = "ins";
    public INSPECCombiner(CombinedWriter writer)
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
        int loadNumber = 0;
        int recsPerbatch = Integer.parseInt(args[5]);
        String operation = args[6];
        tablename = args[7];
        String environment = args[8].toLowerCase();

        try {
            loadNumber = Integer.parseInt(args[4]);
         }
         catch(NumberFormatException e) {
            loadNumber = 0;
         }

         Combiner.TABLENAME = tablename;

         CombinedWriter writer = new CombinedXMLWriter(recsPerbatch,
                                                      loadNumber,
                                                      databaseIndexName, environment);


         writer.setOperation(operation);
        INSPECCombiner c = new INSPECCombiner(writer);

        if(loadNumber > 3000)
        {
            c.writeCombinedByWeekNumber(url,
                                        driver,
                                        username,
                                        password,
                                        loadNumber);
        }
        else if(loadNumber == 2999)
        {
            int yearIndex = loadNumber;
            System.out.println("Processing MISC records as loadnumber " + yearIndex + "...");
            c = new INSPECCombiner(new CombinedXMLWriter(recsPerbatch, yearIndex,databaseIndexName, environment));
            c.writeCombinedByYear(url,
                                  driver,
                                  username,
                                  password,
                                  yearIndex);
        }
        // extract the whole thing
        else if(loadNumber == 0)
        {
            for(int yearIndex = 2005; yearIndex <= 2012; yearIndex++)
            {
                System.out.println("Processing year " + yearIndex + "...");
                //create  a new writer so we can see the loadNumber/yearNumber in the filename
                c = new INSPECCombiner(new CombinedXMLWriter(recsPerbatch, yearIndex,databaseIndexName, environment));
                c.writeCombinedByYear(url,
                                      driver,
                                      username,
                                      password,
                                      yearIndex);
            }
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


    public void writeCombinedByTableHook(Connection con)
    		throws Exception
    		{
    			Statement stmt = null;
    			ResultSet rs = null;
    			try
    			{
    			
    				stmt = con.createStatement();
    				System.out.println("Running the query...");
    				String sqlQuery = "select * from " + Combiner.TABLENAME +" where database='" + Combiner.CURRENTDB + "'";
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


    public void writeCombinedByYearHook(Connection con,
                                        int year)
        throws Exception
    {
        Statement stmt = null;
        ResultSet rs = null;

        try
        {

            stmt = con.createStatement();
            System.out.println("Doing year:"+year);

            if(year == 2999)
            {
                rs = stmt.executeQuery("select m_id, fdate, opan, copa, ppdate, sspdate, aaff, afc, ab, anum, pubti, su, pyr, nrtype, pdoi, cdate, cedate, aoi, aus, aus2, rnum, pnum, cpat, ciorg, iorg, pas, chi, pvoliss, pvol, piss, pipn, cloc, cls, pcdn, scdn, cvs, eaff, eds, pfjt, sfjt, fls, pajt, sajt, la, matid, ndi, pspdate, pepdate, popdate, sopdate, ppub, rtype, sbn, sorg, psn, ssn, tc, pubti, ti, trs, trmc, aaffmulti1, aaffmulti2, eaffmulti1, eaffmulti2, nssn, npsn, LOAD_NUMBER, seq_num, ipc from "+Combiner.TABLENAME+" where pyr='0294' or pyr='0994' or pyr='1101' or pyr='20007' or pyr='Dec.' or pyr='July' or pyr is null");
            }
            else
            {
                rs = stmt.executeQuery("select m_id, fdate, opan, copa, ppdate, sspdate, aaff, afc, ab, anum, pubti, su, pyr, nrtype, pdoi, cdate, cedate, aoi, aus, aus2, rnum, pnum, cpat, ciorg, iorg, pas, chi, pvoliss, pvol, piss, pipn, cloc, cls, pcdn, scdn, cvs, eaff, eds, pfjt, sfjt, fls, pajt, sajt, la, matid, ndi, pspdate, pepdate, popdate, sopdate, ppub, rtype, sbn, sorg, psn, ssn, tc, pubti, ti, trs, trmc, aaffmulti1, aaffmulti2, eaffmulti1, eaffmulti2, nssn, npsn, LOAD_NUMBER, seq_num, ipc from "+Combiner.TABLENAME+" where pyr ='"+ year +"'");
            }
            writeRecs(rs);
            System.out.println("Wrote records.");
            this.writer.flush();
            this.writer.end();
        }
        finally
        {

            if(rs != null)
            {
                try
                {
                    rs.close();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }


            if(stmt != null)
            {
                try
                {
                    stmt.close();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
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


    void writeRecs(ResultSet rs)
            throws Exception
    {
        int i = 0;

        while(rs.next())
        {
            EVCombinedRec rec = new EVCombinedRec();
            ++i;

            String abString = getStringFromClob(rs.getClob("ab"));
            String strYear ="";
            if(rs.getString("pyr") != null && validYear(getPubYear(rs.getString("pyr"))))
            {
                strYear=getPubYear(rs.getString("pyr"));
            }
            else if (rs.getString("sspdate") != null && validYear(getPubYear(rs.getString("sspdate"))))
            {
                strYear=getPubYear(rs.getString("sspdate"));
            }
            else if (rs.getString("fdate") != null && validYear(getPubYear(rs.getString("fdate"))))
            {
                strYear=getPubYear(rs.getString("fdate"));
            }
            else if (rs.getString("cdate") != null && validYear(getPubYear(rs.getString("cdate"))))
            {
                strYear=getPubYear(rs.getString("cdate"));
            }
            else if (rs.getString("su") != null && validYear(getPubYear(rs.getString("su"))))
            {

                strYear=getPubYear(rs.getString("su"));
            }

            if (validYear(strYear))
            {
                if((rs.getString("aus") != null) || (rs.getString("aus2") != null))
                {
                    StringBuffer aus = new StringBuffer();
                    if(rs.getString("aus") != null)
                    {
                        aus.append(rs.getString("aus"));
                    }
                    if(rs.getString("aus2") != null)
                    {
                        aus.append(rs.getString("aus2"));
                    }

                    rec.put(EVCombinedRec.AUTHOR,prepareAuthor(aus.toString()));
                }
                else if(rs.getString("eds") != null)
                {
                    rec.put(EVCombinedRec.EDITOR, prepareAuthor(rs.getString("eds")));
                }


                if(rs.getString("aaff") != null)
                {
                    StringBuffer aaff = new StringBuffer(rs.getString("aaff"));

                    if(rs.getString("aaffmulti1") != null)
                    {
                        aaff = new StringBuffer(rs.getString("aaffmulti1"));

                        if (rs.getString("aaffmulti2") != null)
                        {
                            aaff.append(rs.getString("aaffmulti2"));
                        }
                    }

                    rec.put(EVCombinedRec.AUTHOR_AFFILIATION, prepareAuthor(aaff.toString()));
                }

                if(rs.getString("afc") != null)
                {
                    String countryFormatted = Country.formatCountry(rs.getString("afc"));
                    if (countryFormatted != null)
                    {
                        rec.put(EVCombinedRec.COUNTRY, countryFormatted);
                        rec.put(EVCombinedRec.AFFILIATION_LOCATION, countryFormatted);
                    }
                }

                if(rs.getString("pdoi") != null)
                {
                    rec.put(EVCombinedRec.DOI, rs.getString("pdoi"));
                }


                if(rs.getString("ti") != null)
                {
                    rec.put(EVCombinedRec.TITLE, rs.getString("ti"));
                }

                if(abString != null && abString.length() > 10)
                {
                    rec.put(EVCombinedRec.ABSTRACT, abString);
                }

                if(rs.getString("eaff") != null)
                {
                    StringBuffer eaff = new StringBuffer(rs.getString("eaff"));

                    if(rs.getString("eaffmulti1") != null)
                    {
                        eaff = new StringBuffer(rs.getString("eaffmulti1"));

                        if (rs.getString("eaffmulti2") != null)
                        {
                            eaff.append(rs.getString("eaffmulti2"));
                        }
                    }

                    rec.put(EVCombinedRec.EDITOR_AFFILIATION, prepareAuthor(eaff.toString()));
                }

                if(rs.getString("trs") != null)
                {
                    rec.put(EVCombinedRec.TRANSLATOR, prepareMulti(rs.getString("trs")));
                }

                if(rs.getString("cvs") != null)
                {
                    rec.put(EVCombinedRec.CONTROLLED_TERMS, prepareMulti(rs.getString("cvs")));
                }

                if(rs.getString("fls") != null)
                {
                    rec.put(EVCombinedRec.UNCONTROLLED_TERMS, prepareMulti(rs.getString("fls")));
                }

                if(rs.getString("psn") != null)
                {
                    //06/07 if npsn is not null, it contains print and online issn,
                    // if npsn is not available, use "psn"
                    if (rs.getString("npsn") != null)
                    {
                        rec.put(EVCombinedRec.ISSN, getISSN(rs.getString("npsn")));
                    }
                    else
                    {
                        rec.put(EVCombinedRec.ISSN, rs.getString("psn"));
                    }
                }

                if(rs.getString("ssn") != null)
                {
                    //06/07 if npsn is not null, it contains print and online issn,
                    // if npsn is not available, use "psn"
                    if (rs.getString("nssn") != null)
                    {
                        rec.put(EVCombinedRec.ISSN_OF_TRANSLATION,getISSN(rs.getString("nssn")));
                    }
                    else
                    {
                        rec.put(EVCombinedRec.ISSN_OF_TRANSLATION, rs.getString("ssn"));
                    }
                }


                if(rs.getString("sfjt") != null)
                {
                    rec.put(EVCombinedRec.SERIAL_TITLE_TRANSLATION, rs.getString("sfjt"));
                }

                if(rs.getString("pcdn") != null)
                {
                    rec.put(EVCombinedRec.CODEN, rs.getString("pcdn"));
                }

                if(rs.getString("scdn") != null)
                {
                    rec.put(EVCombinedRec.CODEN_OF_TRANSLATION, rs.getString("scdn"));
                }

                if(rs.getString("sbn") != null)
                {
                    rec.put(EVCombinedRec.ISBN, rs.getString("sbn"));
                }

                if(rs.getString("pubti") != null)
                {
                    rec.put(rec.MONOGRAPH_TITLE,
                            rs.getString("pubti"));
                    rec.put(EVCombinedRec.SERIAL_TITLE, rs.getString("pubti"));
                }

                if(rs.getString("pfjt") != null)
                {
                    rec.put(EVCombinedRec.SERIAL_TITLE, rs.getString("pfjt"));
                }

                if(rs.getString("pajt") != null|| rs.getString("sajt") != null)
                {
                    StringBuffer buf = new StringBuffer();
                    if(rs.getString("pajt") != null)
                    {
                        buf.append(rs.getString("pajt"));
                    }
                    if(rs.getString("sajt") != null)
                    {
                        buf.append("; ").append(rs.getString("sajt"));
                    }
                    rec.put(EVCombinedRec.SOURCE,buf.toString());
                }


                if(rs.getString("ppub") != null)
                {
                    rec.put(EVCombinedRec.PUBLISHER_NAME, rs.getString("ppub"));
                }

                if(rs.getString("trmc") != null)
                {
                    rec.put(EVCombinedRec.TREATMENT_CODE,
                            prepareMulti(getTreatmentCode(rs.getString("trmc"))));
                }

                if(rs.getString("la") != null)
                {
                    rec.put(EVCombinedRec.LANGUAGE,
                            prepareMulti(rs.getString("la")));
                }

                if(rs.getString("nrtype") != null)
                {
                    rec.put(EVCombinedRec.DOCTYPE, getDocType(rs.getString("nrtype")));
                }

                if(rs.getString("cls") != null)
                {
                    rec.put(EVCombinedRec.CLASSIFICATION_CODE, prepareMulti(XMLWriterCommon.formatClassCodes(rs.getString("cls"))));

                }

                if(rs.getString("tc") != null)
                {
                        rec.put(EVCombinedRec.CONFERENCE_NAME,
                                rs.getString("tc"));
                }
                else
                {
                    String thlp = null;
                    if(rs.getString("pfjt") != null)
                    {
                         thlp = rs.getString("pfjt");
                    }
                    else if(rs.getString("pubti") != null)
                    {
                        thlp = rs.getString("pubti");

                    }

                    String rt1 = rec.get(EVCombinedRec.DOCTYPE);

                    if(rt1 != null)
                    {
                        if(rt1.startsWith("CA") &&
                           thlp != null)
                        {
                            rec.put(EVCombinedRec.CONFERENCE_NAME,
                                    thlp);
                        }
                        else if(rt1.startsWith("CP") &&
                                rec.get(EVCombinedRec.TITLE) != null)
                        {
                            rec.put(EVCombinedRec.CONFERENCE_NAME,
                                    rec.get(EVCombinedRec.TITLE));
                        }
                    }
                }


                if(rs.getString("cloc") != null)
                {
                    rec.put(EVCombinedRec.CONFERENCE_LOCATION,
                            rs.getString("cloc"));
                }

                if(rs.getString("cdate") != null|| rs.getString("cedate") != null)
                {
                    StringBuffer buf = new StringBuffer();
                    if(rs.getString("cdate") != null)
                    {
                        buf.append(rs.getString("cdate"));
                    }
                    if(rs.getString("cedate") != null)
                    {
                        buf.append("; ").append(rs.getString("cedate"));
                    }
                    rec.put(EVCombinedRec.MEETING_DATE,buf.toString());
                }

                if(rs.getString("sorg") != null)
                {
                    rec.put(EVCombinedRec.SPONSOR_NAME,
                            prepareMulti(rs.getString("sorg")));
                }

                if(rs.getString("cls") != null)
                {
                    rec.put(EVCombinedRec.DISCIPLINE, prepareMulti(getDiscipline(rs.getString("cls"))));
                }

                if(rs.getString("matid") != null)
                {
                    rec.put(EVCombinedRec.MATERIAL_NUMBER,prepareMulti(rs.getString("matid")));
                }

                if(rs.getString("ndi") != null)
                {
                    rec.put(EVCombinedRec.NUMERICAL_INDEXING,prepareIndexterms(rs.getString("ndi")));
                }

                if(rs.getString("chi") != null)
                {
                    rec.put(EVCombinedRec.CHEMICAL_INDEXING,prepareIndexterms(rs.getString("chi")));
                }

                if(rs.getString("aoi") != null)
                {
                    rec.put(EVCombinedRec.ASTRONOMICAL_INDEXING,prepareMulti(rs.getString("aoi")));
                }

                if(rs.getString("rnum") !=null)
                {
                    rec.put(rec.REPORTNUMBER,
                    rs.getString("rnum"));
                }

                //Patent Info
                if(rs.getString("pnum") !=null)
                {
                    rec.put(rec.PATENT_NUMBER,
                    rs.getString("pnum"));
                }

                //replacing AUTHOR_AFFILIATION for Patent Assignee
                if(rs.getString("pas") != null)
                {
                    rec.put(EVCombinedRec.AUTHOR_AFFILIATION, prepareMulti(rs.getString("pas")));
                }

                if(rs.getString("fdate") != null)
                {
                    rec.put(rec.PATENT_FILING_DATE,
                            rs.getString("fdate"));
                }

                if(rs.getString("opan") != null)
                {
                    rec.put(rec.APPLICATION_NUMBER,
                            rs.getString("opan"));
                }

                if(rs.getString("copa") != null)
                {
                    String countryFormatted = Country.formatCountry(rs.getString("copa"));
                    if (countryFormatted != null)
                    {
                        rec.put(EVCombinedRec.APPLICATION_COUNTRY, countryFormatted);
                    }
                }

                if(rs.getString("ppdate") != null)
                {
                    rec.put(rec.PRIORITY_DATE,
                            rs.getString("ppdate"));
                }

                // replacing COUNTRY with Country of Patent
                if(rs.getString("cpat") != null)
                {
                    String countryFormatted = Country.formatCountry(rs.getString("cpat"));
                    if (countryFormatted != null)
                    {
                        rec.put(EVCombinedRec.COUNTRY, countryFormatted);
                    }
                }

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

                if(rs.getString("ipc")!=null)
                {
                    String ipcString = rs.getString("ipc");
                    ipcString = perl.substitute("s/\\//SLASH/g", ipcString);
                    rec.put(EVCombinedRec.INT_PATENT_CLASSIFICATION, ipcString);
                }

                rec.put(EVCombinedRec.DOCID, rs.getString("M_ID"));
                rec.put(EVCombinedRec.PARENT_ID, rs.getString("seq_num"));

                rec.put(EVCombinedRec.DATABASE, "ins");
                rec.put(EVCombinedRec.LOAD_NUMBER, rs.getString("LOAD_NUMBER"));

                if(validYear(strYear))
                {
                    rec.put(EVCombinedRec.PUB_YEAR,strYear);
                }

                rec.put(EVCombinedRec.DEDUPKEY,
                        getDedupKey(rec.get(EVCombinedRec.ISSN),
                                    rec.get(EVCombinedRec.CODEN),
                                    rs.getString("pvol"),
                                    rs.getString("piss"),
                                    rs.getString("pipn")));

                rec.put(EVCombinedRec.VOLUME, rs.getString("pvol"));
                rec.put(EVCombinedRec.ISSUE, rs.getString("piss"));
                rec.put(EVCombinedRec.STARTPAGE, getFirstPage(rs.getString("pipn")));
                rec.put(EVCombinedRec.ACCESSION_NUMBER, rs.getString("ANUM"));

                writer.writeRec(rec);
            }

        }
    }

    public String[] prepareMulti(String multiString)
        throws Exception
    {
        ArrayList list = new ArrayList();
        StringTokenizer st = new StringTokenizer(multiString, Constants.AUDELIMITER);
        String s;
        while (st.hasMoreTokens())
        {
            s = st.nextToken().trim();
            if(s.length() > 0)
            {
                list.add(s);
            }

        }

        return (String[])list.toArray(new String[1]);

    }

   public  String[] prepareAuthor(String aString)
        throws Exception
    {

        ArrayList list = new ArrayList();
        StringTokenizer st = new StringTokenizer(aString, Constants.AUDELIMITER);
        String s;

        while (st.hasMoreTokens())
        {
            s = st.nextToken().trim();
            if(s.length() > 0)
            {
                if(s.indexOf(Constants.IDDELIMITER) > -1)
                {
                     int i = s.indexOf(Constants.IDDELIMITER);
                      s = s.substring(0,i);
                }
                s = s.trim();

                list.add(s);
            }

        }

        return (String[])list.toArray(new String[1]);

    }

    public String[] prepareIndexterms(String aString)
        throws Exception
    {

        ArrayList list = new ArrayList();
        aString = perl.substitute("s/"+Constants.IDDELIMITER+"/ /g", aString);

        StringTokenizer st = new StringTokenizer(aString, Constants.AUDELIMITER);
        String s;
        while (st.hasMoreTokens())
        {
            s = st.nextToken().trim();
            if(s.length() > 0)
            {
                list.add(s);
            }

        }

        return (String[])list.toArray(new String[1]);
    }

    private String getPubYear(String y)
    {

        String year = "";
        String regularExpression = "((19\\d|20\\d)\\d)\\w?";
        Pattern p = Pattern.compile(regularExpression);
        Matcher m = p.matcher(y);
        if (m.find())
        {
            year = m.group(1).trim();
        }

        return year;
    }

   private boolean validYear(String year)
    {

        return year.matches("[1-2][0-9][0-9][0-9]");
    }

   // 06/07 TS - method added to extract print and online issn from new fields naaff , neaff
   private String[] getISSN(String nsn)
   {
       StringTokenizer toks = new StringTokenizer(nsn, Constants.AUDELIMITER);
       ArrayList list = new ArrayList();
       String s;
       while(toks.hasMoreTokens())
       {
           s = toks.nextToken().trim();

           s = perl.substitute("s/print_//g", s);
           s = perl.substitute("s/online_//g", s);

           if(s.length() > 0)
           {
               list.add(s);
           }
       }

       return (String[])list.toArray(new String[list.size()]);
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
            list.add("MC");
        }
        else if(newdocType.equals("30"))
        {
            list.add("MR");
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
        else if(newdocType.equals("70"))
        {
            list.add("ST");
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

    private String getTreatmentCode(String tc)
    {
        StringBuffer buf = new StringBuffer();
        int i = 0;
        StringTokenizer toks = new StringTokenizer(tc, Constants.AUDELIMITER);

        while(toks.hasMoreTokens())
        {
            String tok = toks.nextToken();
            if(i != 0)
            {
                buf.append(Constants.AUDELIMITER);
            }

            ++i;

            if(tok.equals("A"))
            {
                buf.append("APP");
            }
            else if(tok.equals("B"))
            {
                buf.append("BIB");
            }
            else if(tok.equals("E"))
            {
                buf.append("ECO");
            }
            else if(tok.equals("X"))
            {
                buf.append("EXP");
            }
            else if(tok.equals("G"))
            {
                buf.append("GEN");
            }
            else if(tok.equals("N"))
            {
                buf.append("NEW");
            }
            else if(tok.equals("P"))
            {
                buf.append("PRA");
            }
            else if(tok.equals("R"))
            {
                buf.append("PRO");
            }
            else if(tok.equals("T"))
            {
                buf.append("THR");
            }
            else
            {
                buf.append("NA");
            }
        }
        return buf.toString();
    }

    private String getDiscipline(String dis)
    {
        StringBuffer buf = new StringBuffer();
        if(dis == null)
        {
            return null;
        }
        StringTokenizer t = new StringTokenizer(dis, Constants.AUDELIMITER);
        char[] carray = new char[1];
        while(t.hasMoreTokens())
        {
                if(buf.length() > 0)
                {
                    buf.append(Constants.AUDELIMITER);
                }
                String token = t.nextToken();
                carray[0] = (token.trim()).charAt(0);
                String s = new String(carray);
                buf.append(s).append("DCLS");
        }
        return buf.toString();
    }

    private String getStringFromClob(Clob clob) throws Exception
    {
        String temp= null;
        if(clob!=null)
        {
            temp=clob.getSubString(1,(int)clob.length());
        }

        return temp;
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
            String sqlQuery = "";
        //  System.out.println("Running the query...");
        //  rs = stmt.executeQuery("select m_id, aaff, su, ab, anum, aoi, aus, aus2,pyr, rnum, pnum, cpat, ciorg, iorg, pas, cdate, cedate, doi, nrtype, doit, chi, voliss, ipn, cloc, cls, cn, cnt, cvs, eaff, eds, fjt, fls, fttj, la, matid, ndi, pdate, pub, rtype, sbn, sorg, sn, snt, tc, tdate, thlp, ti, trs, trmc, LOAD_NUMBER from "+Combiner.TABLENAME+ " where LOAD_NUMBER = "+loadN);
            if (loadN == 3001)
            {
                sqlQuery="select m_id, fdate, opan, copa, ppdate,sspdate, aaff, afc, su, pubti, pfjt, pajt, sfjt, sajt, ab, anum, aoi, aus, aus2, pyr, rnum, pnum, cpat, ciorg, iorg, pas, pcdn, scdn, cdate, cedate, pdoi, nrtype, chi, pvoliss, pvol, piss, pipn, cloc, cls, cvs, eaff, eds, fls, la, matid, ndi, pspdate, ppub, rtype, sbn, sorg, psn, ssn, tc, sspdate, ti, trs, trmc, aaffmulti1, aaffmulti2, eaffmulti1, eaffmulti2, nssn, npsn, LOAD_NUMBER, seq_num, ipc from "+Combiner.TABLENAME+ " where pyr is null and load_number < 200537";
            }
            else if (loadN == 3002)
            {
                sqlQuery="select m_id, fdate, opan, copa, ppdate,sspdate, aaff, afc, su, pubti, pfjt, pajt, sfjt, sajt, ab, anum, aoi, aus, aus2, pyr, rnum, pnum, cpat, ciorg, iorg, pas, pcdn, scdn, cdate, cedate, pdoi, nrtype, chi, pvoliss, pvol, piss, pipn, cloc, cls, cvs, eaff, eds, fls, la, matid, ndi, pspdate, ppub, rtype, sbn, sorg, psn, ssn, tc, sspdate, ti, trs, trmc,aaffmulti1, aaffmulti2, eaffmulti1, eaffmulti2, nssn, npsn,  LOAD_NUMBER, seq_num, ipc from "+Combiner.TABLENAME+ " where pyr like '194%'or pyr like '195%' or (pyr like '196%' and pyr != '1969')";
            }
            else if(loadN ==8413583)
            {
                sqlQuery="select m_id, fdate, opan, copa, ppdate,sspdate, aaff, afc, su, pubti, pfjt, pajt, sfjt, sajt, ab, anum, aoi, aus, aus2, pyr, rnum, pnum, cpat, ciorg, iorg, pas, pcdn, scdn, cdate, cedate, pdoi, nrtype, chi, pvoliss, pvol, piss, pipn, cloc, cls, cvs, eaff, eds, fls, la, matid, ndi, pspdate, ppub, rtype, sbn, sorg, psn, ssn, tc, sspdate, ti, trs, trmc,aaffmulti1, aaffmulti2, eaffmulti1, eaffmulti2, nssn, npsn, LOAD_NUMBER, seq_num, ipc from "+Combiner.TABLENAME+ " where Anum = '"+loadN+"'";
            }
            else
            {
                sqlQuery="select m_id, fdate, opan, copa, ppdate,sspdate, aaff, afc, su, pubti, pfjt, pajt, sfjt, sajt, ab, anum, aoi, aus, aus2, pyr, rnum, pnum, cpat, ciorg, iorg, pas, pcdn, scdn, cdate, cedate, pdoi, nrtype, chi, pvoliss, pvol, piss, pipn, cloc, cls, cvs, eaff, eds, fls, la, matid, ndi, pspdate, ppub, rtype, sbn, sorg, psn, ssn, tc, sspdate, ti, trs, trmc,aaffmulti1, aaffmulti2, eaffmulti1, eaffmulti2, nssn, npsn, LOAD_NUMBER, seq_num, ipc from "+Combiner.TABLENAME+ " where LOAD_NUMBER = "+loadN;
            }
            //System.out.println("Inspect sqlQuery= "+sqlQuery);
            rs = stmt.executeQuery(sqlQuery);
            writeRecs(rs);
            System.out.println("Wrote records.");
            this.writer.flush();
            this.writer.end();

        }
        finally
        {

            if(rs != null)
            {
                try
                {
                    rs.close();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }


            if(stmt != null)
            {
                try
                {
                    stmt.close();
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }
}
