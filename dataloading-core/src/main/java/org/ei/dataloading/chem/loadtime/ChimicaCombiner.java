package org.ei.dataloading.chem.loadtime;

import java.io.ByteArrayInputStream;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Random;

import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.MatchResult;
import org.ei.common.AuthorStream;
import org.ei.common.CountryFormatter;
import org.ei.dataloading.CombinedWriter;
import org.ei.dataloading.CombinedXMLWriter;
import org.ei.dataloading.Combiner;
import org.ei.dataloading.DataValidator;
import org.ei.dataloading.EVCombinedRec;
import org.ei.dataloading.XMLWriterCommon;
import org.ei.dataloading.lookup.LookupEntry;
import org.ei.util.GUID;

public class ChimicaCombiner extends Combiner {

    Perl5Util perl = new Perl5Util();

    private Random rNumbers = new Random();

    private static String tablename;
    /*HT added 09/21/2020 for lookup extraction to ES*/
    private String action = null;
    private LookupEntry lookupObj = null;
    

    public static void main(String args[]) throws Exception {
        if (args[0].equalsIgnoreCase("-v")) {
            DataValidator d = new DataValidator();
            d.validateFile(args[1]);
        } else {
            String url = args[0];
            String driver = args[1];
            String username = args[2];
            String password = args[3];
            int loadNumber = Integer.parseInt(args[4]);
            int recsPerfile = Integer.parseInt(args[5]);
            int exitAt = Integer.parseInt(args[6]);
            tablename = args[7];

            Combiner.TABLENAME = tablename;
            Combiner.EXITNUMBER = exitAt;
            System.out.println(Combiner.TABLENAME);

            CombinedWriter writer = new CombinedXMLWriter(recsPerfile, loadNumber, "chm");

            ChimicaCombiner c = new ChimicaCombiner(writer);
            
            /*TH added 09/21/2020 for ES lookup generation*/
            for(String str: args)
            {
            	if(str.equalsIgnoreCase("lookup"))
            		c.setAction("lookup");
            	System.out.println("Action: lookup");
            }
            
            if(c.getAction() == null || c.getAction().isEmpty())
            {
            	if (loadNumber > 3000 || loadNumber < 1000) {
                    c.writeCombinedByWeekNumber(url, driver, username, password, loadNumber);
                } else {
                    c.writeCombinedByYear(url, driver, username, password, loadNumber);
                }

                System.out.println("completed " + loadNumber);
            }
            else
            {
            	System.out.println("Extracting Lookups");
            	c.writeLookupByWeekNumber(loadNumber);
            }
            
        }

    }
    
    public void setAction(String str)
    {
    	action = str;
    }
    public String getAction() {
    	return action;
    }

    public ChimicaCombiner(CombinedWriter writer) {
        super(writer);
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


    public void writeCombinedByYearHook(Connection con, int year) throws Exception {

        Statement stmt = null;
        ResultSet rs = null;
        try {
            this.writer.begin();
            stmt = con.createStatement();
            System.out.println("Running the query...");
            rs = stmt
                .executeQuery("select abs, m_id, cna, chn, tie, aut, adr, sti, SUBSTR(pyr,1,4) pyr, cod, isn, lna, ctm, crd, trc, vol,iss,pag, std, tif, cfl,to_char(to_date(spd),'yyyymmdd') pub_sort, pub, doi , load_number from "
                    + Combiner.TABLENAME + " where SUBSTR(pyr, 1, 4) ='" + year + "'");
            System.out.println("Got records ...");
            writeRecs(rs);
            System.out.println("Wrote records.");
            this.writer.end();

        } finally {

            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void writeRecs(ResultSet rs) throws Exception {
        int i = 0;
        while (rs.next()) {
            EVCombinedRec rec = new EVCombinedRec();
            ++i;
            if (Combiner.EXITNUMBER != 0 && i > Combiner.EXITNUMBER) {
                break;
            }
            String abString = getStringFromClob(rs.getClob("abs"));

            if (validYear(rs.getString("pyr"))) {
                if (rs.getString("aut") != null) {
                    rec.put(EVCombinedRec.AUTHOR, prepareAuthor(rs.getString("aut")));
                }

                if (rs.getString("adr") != null) {
                    rec.put(EVCombinedRec.AUTHOR_AFFILIATION, removeAuNames(rs.getString("adr")));
                }

                if (rs.getString("tie") != null) {
                    rec.put(EVCombinedRec.TITLE, rs.getString("tie"));
                }

                if (rs.getString("tif") != null) {
                    rec.put(EVCombinedRec.TRANSLATED_TITLE, rs.getString("tif"));
                }

                if (abString != null && abString.length() > 99) {
                    rec.put(EVCombinedRec.ABSTRACT, abString);
                }

                if (rs.getString("cod") != null) {
                    rec.put(EVCombinedRec.CODEN, rs.getString("cod"));
                }

                if (rs.getString("sti") != null) {
                    rec.put(EVCombinedRec.SERIAL_TITLE, rs.getString("sti"));
                }

                if (rs.getString("isn") != null) {
                    rec.put(EVCombinedRec.ISSN, rs.getString("isn"));
                }

                if (rs.getString("lna") != null) {
                    rec.put(EVCombinedRec.LANGUAGE, prepareMulti(rs.getString("lna")));
                }

                if (rs.getString("ctm") != null) {
                    rec.put(EVCombinedRec.CONTROLLED_TERMS, prepareMulti(rs.getString("ctm")));
                }

                if (rs.getString("crd") != null) {
                    rec.put(EVCombinedRec.CASREGISTRYNUMBER, prepareMulti(rs.getString("crd")));
                }

                if (rs.getString("trc") != null) {
                    rec.put(EVCombinedRec.CLASSIFICATION_CODE, prepareMulti(XMLWriterCommon.formatClassCodes(rs.getString("trc"))));
                }

                String docType = rs.getString("std");
                if (docType == null) {
                    docType = "";
                }

                rec.put(EVCombinedRec.DOCTYPE, docType);

                if (rs.getString("cfl") != null) {
                    rec.put(EVCombinedRec.UNCONTROLLED_TERMS, prepareMulti(rs.getString("cfl")));

                }

                rec.put(EVCombinedRec.DOCID, rs.getString("M_ID"));

                rec.put(EVCombinedRec.DATABASE, "chm");
                rec.put(EVCombinedRec.LOAD_NUMBER, rs.getString("LOAD_NUMBER"));

                if (rs.getString("pyr") != null) {
                    rec.put(EVCombinedRec.PUB_YEAR, rs.getString("pyr"));
                }

                rec.put(EVCombinedRec.DEDUPKEY,
                    getDedupKey(rec.get(EVCombinedRec.ISSN), rec.get(EVCombinedRec.CODEN), rs.getString("vol"), rs.getString("iss"), rs.getString("pag")));

                rec.put(EVCombinedRec.VOLUME, getFirstNumber(rs.getString("vol")));
                rec.put(EVCombinedRec.ISSUE, getFirstNumber(rs.getString("iss")));
                rec.put(EVCombinedRec.STARTPAGE, getFirstNumber(rs.getString("pag")));
                rec.put(EVCombinedRec.ACCESSION_NUMBER, rs.getString("chn"));

                if (rs.getString("pub") != null) {
                    rec.put(EVCombinedRec.PUBLISHER_NAME, rs.getString("pub"));
                }

                if (rs.getString("cna") != null) {
                    rec.put(EVCombinedRec.COUNTRY, prepareMulti(CountryFormatter.formatCountry(rs.getString("cna"))));
                }

                if (rs.getString("doi") != null) {
                    rec.put(EVCombinedRec.DOI, rs.getString("doi"));
                }

                if(!(getAction().equalsIgnoreCase("lookup")))
                {
                	this.writer.writeRec(rec);
                }
                /*HT added 09/21/2020 for ES lookup*/
                else if (getAction() != null && getAction().equalsIgnoreCase("lookup"))
                {
                	this.lookupObj.writeLookupRec(rec);
                }
                
            }

        }
    }

    private String getFirstNumber(String v) {

        MatchResult mResult = null;
        if (v == null) {
            return null;
        }

        if (perl.match("/[1-9][0-9]*/", v)) {
            mResult = perl.getMatch();
        } else {
            return null;
        }

        return mResult.toString();
    }

    private String getDedupKey(String issn, String coden, String volume, String issue, String page) throws Exception {

        String firstVolume = getFirstNumber(volume);
        String firstIssue = getFirstNumber(issue);
        String firstPage = getFirstNumber(page);

        if ((issn == null && coden == null) || firstVolume == null || firstIssue == null || firstPage == null) {
            return (new GUID()).toString();
        }

        StringBuffer buf = new StringBuffer();

        if (issn != null) {
            buf.append(perl.substitute("s/-//g", issn));
        } else {
            buf.append(coden);
        }

        buf.append("vol" + firstVolume);
        buf.append("is" + firstIssue);
        buf.append("pa" + firstPage);

        return buf.toString().toLowerCase();

    }

    private String[] prepareMulti(String multiString) throws Exception {
        AuthorStream astream = new AuthorStream(new ByteArrayInputStream(multiString.getBytes()));
        String s = null;
        ArrayList<String> list = new ArrayList<String>();

        while ((s = astream.readAuthor()) != null) {
            s = s.trim();
            if (s.length() > 0) {
                list.add(s);
            }
        }

        return (String[]) list.toArray(new String[1]);

    }

    // 12/04/2007 TS based on the final specs this method is parsing out the occurrences of author name in followed by comma
    // from adr author aff field
    // 12/04/2007 TS added spec on it : if there is no comma in the adr field - this is a redundant au name - remove it

    private String removeAuNames(String adr) {
        int m = adr.indexOf(", ");
        if (m > 0) {
            adr = adr.substring(m + 1);
        } else if (m == -1) {
            return "";
        }
        return adr;
    }

    private String[] prepareAuthor(String aString) throws Exception {
        new StringBuffer();
        AuthorStream astream = new AuthorStream(new ByteArrayInputStream(aString.getBytes()));
        String s = null;
        ArrayList<String> list = new ArrayList<String>();

        while ((s = astream.readAuthor()) != null) {
            s = s.trim();
            if (s.length() > 0) {
                s = stripAnon(s);
                s = s.trim();

                list.add(s);
            }
        }

        return (String[]) list.toArray(new String[1]);

    }

    private String stripAnon(String line) {
        line = perl.substitute("s/\\banon\\b/ /gi", line);
        line = perl.substitute("s/\\(ed\\.\\)/ /gi", line);
        return line;
    }

    private boolean validYear(String year) {
        if (year == null) {
            return false;
        }

        if (year.length() != 4) {
            return false;
        }

        return perl.match("/[1-9][0-9][0-9][0-9]/", year);
    }

    private String getStringFromClob(Clob clob) throws Exception {
        String temp = null;
        if (clob != null) {
            temp = clob.getSubString(1, (int) clob.length());
        }

        return temp;
    }

    private String getRandomNumber() {
        this.rNumbers.setSeed(System.currentTimeMillis());
        int i = rNumbers.nextInt(1000);
        String is = Integer.toString(i);
        while (is.length() < 4) {
            is = is + "0";
        }

        return is;
    }

    public void writeCombinedByWeekHook(Connection con, int weekNumber) throws Exception {
        Statement stmt = null;
        ResultSet rs = null;

        try {

            this.writer.begin();
            stmt = con.createStatement();

            rs = stmt
                .executeQuery("select abs, m_id, cna, chn, tie, aut, adr, sti, SUBSTR(pyr,1,4) pyr, cod, isn, lna, ctm, crd, trc, vol,iss,pag, std, tif, cfl,to_char(to_date(spd),'yyyymmdd') pub_sort, pub, doi, load_number from "
                    + Combiner.TABLENAME + " where load_number =" + weekNumber);

            writeRecs(rs);
            this.writer.end();

        } finally {

            if (rs != null) {
                try {
                    rs.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (stmt != null) {
                try {
                    stmt.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

	@Override
	public void writeLookupByWeekHook(int weekNumber) throws Exception {
		System.out.println("Extract Lookup");
		
	}

}
