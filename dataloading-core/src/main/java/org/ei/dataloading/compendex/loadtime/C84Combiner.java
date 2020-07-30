package org.ei.dataloading.compendex.loadtime;

import java.io.ByteArrayInputStream;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.MatchResult;
import org.ei.common.Country;
import org.ei.common.AuthorStream;
import org.ei.dataloading.CombinedWriter;
import org.ei.dataloading.CombinedXMLWriter;
import org.ei.dataloading.Combiner;
import org.ei.dataloading.EVCombinedRec;
import org.ei.dataloading.XMLWriterCommon;
import org.ei.util.GUID;
import org.ei.util.kafka.*;
import org.ei.dataloading.MessageSender;

public class C84Combiner extends Combiner {

    Perl5Util perl = new Perl5Util();
    private static String propertyFileName;
    private static String tablename;
    private static int loadNumber;
    
    public static void main(String args[]) throws Exception {

        String url = args[0];
        String driver = args[1];
        String username = args[2];
        String password = args[3];
        loadNumber = Integer.parseInt(args[4]);
        int recsPerbatch = Integer.parseInt(args[5]);
        String operation = args[6];
        tablename = args[7];
        String environment = args[8].toLowerCase();
        if (args.length>9)
        {
        	propertyFileName=args[9];
        	System.out.println("propertyFileName="+propertyFileName);
        }
        
        Combiner.TABLENAME = tablename;
        System.out.println(Combiner.TABLENAME);

        CombinedWriter writer = new CombinedXMLWriter(recsPerbatch, loadNumber, "c84");
        writer.setOperation(operation);
        C84Combiner c = new C84Combiner(writer);

        // extract the whole thing
        if (loadNumber == 0) {
            for (int yearIndex = 1869; yearIndex <= 1969; yearIndex++) {
                System.out.println("Processing year " + yearIndex + "...");
                c = new C84Combiner(new CombinedXMLWriter(recsPerbatch, yearIndex, "c84", environment));
                c.writeCombinedByYear(url, driver, username, password, yearIndex);
            }
            int yearIndex = 9999;
            System.out.println("Processing year " + yearIndex + "...");
            c = new C84Combiner(new CombinedXMLWriter(recsPerbatch, yearIndex, "c84", environment));
            c.writeCombinedByYear(url, driver, username, password, yearIndex);
        } else if (loadNumber == 1) {
            c.writeCombinedByTable(url, driver, username, password);
	    } else {
	        c.writeCombinedByWeekNumber(url, driver, username, password, loadNumber);
	    }

    }

    public C84Combiner(CombinedWriter writer) {
        super(writer);
    }
    
    public void writeCombinedByTableHook(Connection con)
    		throws Exception
	{
		Statement stmt = null;
		ResultSet rs = null;
		try
		{
		
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
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


    public void writeCombinedByYearHook(Connection con, int year) throws Exception 
    {

        Statement stmt = null;
        ResultSet rs = null;
        try {

        	stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            System.out.println("Running the query...");
            if (year == 9999) {
                rs = stmt
                    .executeQuery("select * from "
                        + Combiner.TABLENAME
                        + " where seq_num is not null and yr in ('1000','1003','1018','1039','1042','1043','1047','1051','1052','1059','1065','1153','1494','1495','1590','1592','1593','1597','1643','1659','1800','1802','1804','1805','1806','1807','1808','1809','1811','1813','1820','1830','1831','1838','1855','1856','1857','1858','1860','1863','1864','1865','1867','1868','189','19\\ill\\','192','193\\ill\\','1972','1975','1978','1981','1983','1984','1985','1987','1989','1990','1992','1993','1994','1995','7953') or yr is null");
            } else {
                rs = stmt.executeQuery("select * from " + Combiner.TABLENAME + " where seq_num is not null and substr(yr,1,4) ='" + year + "'");
            }

            //
            System.out.println("Got records ...");
            writeRecs(rs);
            System.out.println("Wrote records.");
            this.writer.end();
            this.writer.flush();

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

    public static int getResultSetSize(ResultSet resultSet)
    {
    	    int size = -1;
    	    try
    	    {
    	        resultSet.last();
    	        size = resultSet.getRow();
    	        resultSet.beforeFirst();
    	    }
    	    catch(SQLException e)
    	    {
    	        return size;
    	    }

    	    return size;
    }
    
    private void writeRecs(ResultSet rs) throws Exception 
    {     
        int i = 0;
        KafkaService kafka=null;;
       
        ///* use for ES only
        Thread thread =null;
        Map<String,String> batchData = new ConcurrentHashMap<String,String>();   
        Map<String,String> missedData = new ConcurrentHashMap<String,String>();
        int counter=0;
        int batchSize = 0;
        MessageSender sendMessage=null;
        //*/        
        long processTime = System.currentTimeMillis();   	 
    	
        try
        {
        	if(this.propertyFileName!=null)
        	{
        		kafka = new KafkaService(processTime+"_ibf_"+this.loadNumber, this.propertyFileName); //use it for ES extraction
        	}
        	
        	int totalCount = getResultSetSize(rs);
        	  
	        while (rs.next()) 
	        {
	            EVCombinedRec rec = new EVCombinedRec();
	            ++i;
	
	            if (rs.getString("seq_num") != null) {
	                rec.put(EVCombinedRec.PARENT_ID, rs.getString("seq_num"));
	            }
	
	            String abString = getStringFromClob(rs.getClob("ab"));
	            String processInfo = processTime+"-"+totalCount+'-'+i+"-C84-"+rs.getString("LOAD_NUMBER")+'-'+"";
	        	rec.put(EVCombinedRec.PROCESS_INFO, processInfo);	 
	        	
	            if (validYear(rs.getString("yr"), rs.getString("LOAD_NUMBER"))) {
	
	                if (rs.getString("aus") != null) {
	                    rec.put(EVCombinedRec.AUTHOR, prepareAuthor(rs.getString("aus")));
	
	                    if (rs.getString("af") != null) {
	                        rec.put(EVCombinedRec.AUTHOR_AFFILIATION, rs.getString("af"));
	
	                        StringBuffer affilLoc = new StringBuffer();
	                        if (rs.getString("ay") != null) {
	
	                            String countryFormatted = Country.formatCountry(rs.getString("ay"));
	                            if (countryFormatted != null) {
	                                affilLoc.append(countryFormatted);
	                                rec.put(EVCombinedRec.COUNTRY, prepareMulti(countryFormatted));
	                            }
	                        } else {
	                            String countryFormatted = Country.getCountry(rs.getString("af"));
	                            if (countryFormatted != null) {
	                                affilLoc.append(countryFormatted);
	                                rec.put(EVCombinedRec.COUNTRY, prepareMulti(countryFormatted));
	                            }
	                        }
	
	                        if (rs.getString("ac") != null) {
	                            affilLoc.append(" ");
	                            affilLoc.append(rs.getString("ac"));
	                        }
	
	                        if (affilLoc.length() > 0) {
	                            rec.put(EVCombinedRec.AFFILIATION_LOCATION, affilLoc.toString());
	                        }
	                    }
	                } else if (rs.getString("ed") != null) {
	                    rec.put(EVCombinedRec.EDITOR, prepareAuthor(rs.getString("ed")));
	
	                    if (rs.getString("ef") != null) {
	                        rec.put(EVCombinedRec.EDITOR_AFFILIATION, rs.getString("ef"));
	                        if (rs.getString("ey") != null) {
	                            String countryFormatted = Country.formatCountry(rs.getString("ey"));
	
	                            if (countryFormatted != null) {
	                                rec.put(EVCombinedRec.COUNTRY, prepareMulti(countryFormatted));
	                            }
	
	                        }
	                    }
	                }
	
	                if (rs.getString("ti") != null) {
	                    rec.put(EVCombinedRec.TITLE, rs.getString("ti"));
	                }
	
	                if (rs.getString("tt") != null) {
	                    rec.put(EVCombinedRec.TRANSLATED_TITLE, rs.getString("tt"));
	                }
	
	                if (rs.getString("vt") != null) {
	                    rec.put(EVCombinedRec.VOLUME_TITLE, rs.getString("vt"));
	                }
	
	                if (abString != null && abString.length() > 99) {
	                    rec.put(EVCombinedRec.ABSTRACT, abString);
	                }
	
	                if (rs.getString("cvs") != null) {
	                    rec.put(EVCombinedRec.CONTROLLED_TERMS, prepareMulti(rs.getString("cvs")));
	                }
	
	                if (rs.getString("fls") != null) {
	                    rec.put(EVCombinedRec.UNCONTROLLED_TERMS, prepareMulti(rs.getString("fls")));
	                }
	
	                if (rs.getString("sn") != null)
	
	                {
	                    rec.put(EVCombinedRec.ISSN, prepareMulti(rs.getString("sn")));
	                }
	
	                if (rs.getString("cn") != null) {
	                    rec.put(EVCombinedRec.CODEN, prepareMulti(rs.getString("cn")));
	                }
	
	                if (rs.getString("bn") != null) {
	                    rec.put(EVCombinedRec.ISBN, prepareMulti(rs.getString("bn")));
	                }
	
	                String st = rs.getString("st");
	                if (st == null) {
	                    st = rs.getString("se");
	                }
	
	                if (st != null) {
	                    rec.put(EVCombinedRec.SERIAL_TITLE, st);
	                }
	
	                String mh = rs.getString("mh");
	                String sh = rs.getString("sh");
	                String mhsh = null;
	                if (mh != null && sh != null) {
	                    mhsh = mh + " " + sh;
	                } else if (mh != null && sh == null) {
	                    mhsh = mh;
	                }
	
	                if (mhsh != null) {
	                    rec.put(EVCombinedRec.MAIN_HEADING, mhsh);
	                }
	
	                if (rs.getString("pn") != null) {
	                    rec.put(EVCombinedRec.PUBLISHER_NAME, rs.getString("pn"));
	                }
	
	                if (rs.getString("tr") != null) {
	                    rec.put(EVCombinedRec.TREATMENT_CODE, prepareMulti(getTreatmentCode(rs.getString("tr"))));
	                }
	
	                String la = rs.getString("la");
	                if (la == null) {
	                    la = rs.getString("lf");
	                }
	
	                if (la != null) {
	                    rec.put(EVCombinedRec.LANGUAGE, prepareMulti(la));
	                }
	
	                //change for book project by hmo at 5/23/2017
	                String docType = rs.getString("dt");
	                if (docType == null) {
	                    docType = "";
	                }
	                else if (docType.equalsIgnoreCase("mr"))
	                {
	                	docType = "bk";
	                }
	                else if (docType.equalsIgnoreCase("mc"))
	                {
	                	docType = "ch";
	                }
	                
	                /*
	                remove core from document type pre T.M. request at 4/20/2020 by hmo
	                 
	                if (mhsh != null || rec.containsKey(EVCombinedRec.CONTROLLED_TERMS)) {
	                    docType = docType + " CORE";
	                }
	                 */
	                rec.put(EVCombinedRec.DOCTYPE, docType);
	
	                if (rs.getString("cls") != null) {
	                    rec.put(EVCombinedRec.CLASSIFICATION_CODE, prepareMulti(XMLWriterCommon.formatClassCodes(rs.getString("cls"))));
	                }
	
	                if (rs.getString("cc") != null)
	
	                {
	                    rec.put(EVCombinedRec.CONFERENCE_CODE, rs.getString("cc"));
	                }
	
	                if (rs.getString("cf") != null) {
	                    rec.put(EVCombinedRec.CONFERENCE_NAME, rs.getString("cf"));
	                }
	
	                String cl = formatConferenceLoc(rs.getString("ms"), rs.getString("mc"), rs.getString("mv"), rs.getString("my"));
	
	                if (cl.length() > 2) {
	                    rec.put(EVCombinedRec.CONFERENCE_LOCATION, cl);
	                }
	
	                if (rs.getString("m2") != null) {
	                    rec.put(EVCombinedRec.MEETING_DATE, rs.getString("m2"));
	                }
	
	                if (rs.getString("sp") != null) {
	                    rec.put(EVCombinedRec.SPONSOR_NAME, prepareMulti(rs.getString("sp")));
	                }
	
	                if (rs.getString("mt") != null) {
	                    rec.put(EVCombinedRec.MONOGRAPH_TITLE, rs.getString("mt"));
	                }
	
	                if (rs.getString("pe") != null) {
	                    rec.put(EVCombinedRec.AUTHOR_AFFILIATION, rs.getString("pe"));
	                }
	
	                if (rs.getString("pu") != null) {
	                    String countryFormatted = Country.formatCountry(rs.getString("pu"));
	                    rec.put(EVCombinedRec.COUNTRY, prepareMulti(countryFormatted));
	                    rec.put(EVCombinedRec.AFFILIATION_LOCATION, prepareMulti(countryFormatted));
	                }
	
	                if (rs.getString("pm") != null)
	
	                {
	                    rec.put(EVCombinedRec.PATENT_NUMBER, prepareMulti(formatSic(rs.getString("pm"))));
	                }
	
	                if (rs.getString("ad") != null) {
	                    rec.put(EVCombinedRec.PATENTAPPDATE, rs.getString("ad"));
	                }
	
	                if (rs.getString("pd") != null) {
	                    rec.put(EVCombinedRec.PATENTISSUEDATE, rs.getString("pd"));
	                }
	
	                rec.put(EVCombinedRec.DOCID, rs.getString("M_ID"));
	                rec.put(EVCombinedRec.DATABASE, "c84");
	                rec.put(EVCombinedRec.LOAD_NUMBER, rs.getString("LOAD_NUMBER"));
	
	                if ((rs.getString("yr") != null) && validpubYear(rs.getString("yr"))) {
	                    rec.put(EVCombinedRec.PUB_YEAR, rs.getString("yr"));
	                    rec.put(EVCombinedRec.DATESORT, rs.getString("yr") + "0000");
	                } else {
	                    rec.put(EVCombinedRec.PUB_YEAR, rs.getString("LOAD_NUMBER"));
	                    rec.put(EVCombinedRec.DATESORT, rs.getString("LOAD_NUMBER") + "0000");
	                }
	
	                rec.put(EVCombinedRec.DEDUPKEY,
	                    getDedupKey(rec.get(EVCombinedRec.ISSN), rec.get(EVCombinedRec.CODEN), rs.getString("vo"), rs.getString("iss"), rs.getString("xp")));
	
	                rec.put(EVCombinedRec.VOLUME, getFirstNumber(rs.getString("vo")));
	                rec.put(EVCombinedRec.ISSUE, getFirstNumber(rs.getString("iss")));
	                rec.put(EVCombinedRec.STARTPAGE, getFirstPage(rs.getString("xp")));
	                rec.put(EVCombinedRec.ACCESSION_NUMBER, rs.getString("ex"));
	                rec.put(EVCombinedRec.PUB_SORT, Integer.toString(i));
	
	                if (rs.getString("do") != null) {
	                    rec.put(EVCombinedRec.DOI, rs.getString("do"));
	                }
	
	                if(this.propertyFileName==null)
	                {
	                	this.writer.writeRec(rec); //use this line for FAST extraction only;
	                }
	                else
	                {
		                /**********************************************************/
		    	        //following code used to test kafka by hmo@2020/03/24
		    	        //this.writer.writeRec(recArray,kafka);
		    	        /**********************************************************/
		                
		    	        //this.writer.writeRec(rec,kafka);		    	        
		    	        this.writer.writeRec(rec,kafka, batchData, missedData);
			            if(counter<batchSize)
			            {            	
			            	counter++;
			            }
			            else
			            {        
			            	 thread = new Thread(sendMessage);
			            	 sendMessage= new MessageSender(kafka,batchData,missedData);		            	 
			            	 thread.start(); 
			            	 batchData = new ConcurrentHashMap();
			            	 counter=0;
			            }
	                }
	
	            }
	        }
        }
        finally
        { 
        	if(this.propertyFileName!=null)
        	{
	        	try
	        	{
	        		 thread = new Thread(sendMessage);
	            	 sendMessage= new MessageSender(kafka,batchData,missedData);            	 
	            	 thread.start(); 
	        	}
	        	catch(Exception ex) 
	        	{
	        		ex.printStackTrace();
	        	}    
        	}
        	
        	if(kafka!=null)
 	        {
        		try 
            	{
        			kafka.close(missedData);
            	}
            	catch(Exception ex) 
            	{
            		ex.printStackTrace();
            	}
	        	
	        
	        }
	        System.out.println("Total "+i+" records");
        }
    }

    private String formatSic(String sic) {
        if (sic == null) {
            return null;
        }
        StringBuffer buf = new StringBuffer();
        String clean = perl.substitute("s#,##g", sic);
        buf.append(sic).append(";").append(clean);
        return buf.toString();
    }

    private String getFirstPage(String v) {

        MatchResult mResult = null;
        if (v == null) {
            return null;
        }

        if (perl.match("/[A-Z]?[0-9][0-9]*/", v)) {
            mResult = perl.getMatch();
        } else {
            return null;
        }

        return mResult.toString();
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
        String firstPage = getFirstPage(page);

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

    private String getTreatmentCode(String tc) {
        StringBuffer tbuff = new StringBuffer();

        for (int i = 0; i < tc.length(); ++i) {
            char c = tc.charAt(i);
            if (i > 0) {
                tbuff.append(";");
            }

            if (c == 'A') {
                tbuff.append("APP");
            } else if (c == 'B') {
                tbuff.append("BIO");
            } else if (c == 'E') {
                tbuff.append("ECO");
            } else if (c == 'X') {
                tbuff.append("EXP");
            } else if (c == 'G') {
                tbuff.append("GEN");
            } else if (c == 'H') {
                tbuff.append("HIS");
            } else if (c == 'L') {
                tbuff.append("LIT");
            } else if (c == 'M') {
                tbuff.append("MAN");
            } else if (c == 'N') {
                tbuff.append("NUM");
            } else if (c == 'T') {
                tbuff.append("THR");
            } else {
                tbuff.append("NA");
            }
        }

        return tbuff.toString();
    }

    private boolean validYear(String year, String loadnum) {
        if (loadnum != null) {
            return true;
        } else {
            if ((year == null) || (year.length() != 4)) {
                return false;
            } else {
                return perl.match("/[1-9][0-9][0-9][0-9]/", year);
            }
        }
    }

    private boolean validpubYear(String year) {
        if ((year == null) || (year.length() != 4)) {
            return false;
        } else {
            return perl.match("/[1-9][0-9][0-9][0-9]/", year);
        }
    }

    private String formatConferenceLoc(String ms, String mc, String mv, String my) {
        StringBuffer b = new StringBuffer(" ");
        if (ms != null) {
            b.append(ms + ", ");
        }

        if (mc != null) {
            b.append(mc + ", ");
        }

        if (mv != null) {
            b.append(mv + ", ");
        }

        if (my != null) {
            b.append(my);
        }

        return b.toString();

    }

    private String getStringFromClob(Clob clob) throws Exception {
        String temp = null;
        if (clob != null) {
            temp = clob.getSubString(1, (int) clob.length());
        }

        return temp;
    }

    public void writeCombinedByWeekHook(Connection con, int weekNumber) throws Exception {
        Statement stmt = null;
        ResultSet rs = null;

        try {

        	stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            rs = stmt
                .executeQuery("select ay, ac , ey, pe, pm, ad, pd, pu, ab, m_id, vo, iss, xp,af, ex, an, aus, bn, cal, cc, cf, cls, cn, cvs, dt, ed, ef, fls, id, la, lf, mc, me, mh, ms, mt, mv, my, m2, pn, se, sh, sn, sp, st, ti, tt, tr, vt, do, SUBSTR(yr,1,4) yr, load_number, seq_num from "
                    + tablename + " where seq_num is not null and load_number =" + weekNumber);
            writeRecs(rs);
            this.writer.end();
            this.writer.flush();

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

    private String[] prepareMulti(String multiString) throws Exception {
        AuthorStream astream = null;
        try {
            astream = new AuthorStream(new ByteArrayInputStream(multiString.getBytes()));
            String s = null;
            ArrayList<String> list = new ArrayList<String>();

            while ((s = astream.readAuthor()) != null) {
                s = s.trim();
                if (s.length() > 0) {
                    list.add(s);
                }
            }

            return (String[]) list.toArray(new String[1]);
        } catch (Exception e) {
            e.fillInStackTrace();
            throw e;
        } finally {
            if (astream != null) {
                astream.close();
            }
        }

    }

    private String[] prepareAuthor(String aString) throws Exception {
        AuthorStream astream = null;
        try {
            astream = new AuthorStream(new ByteArrayInputStream(aString.getBytes()));
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
        } catch (Exception e) {
            e.fillInStackTrace();
            throw e;
        } finally {
            if (astream != null) {
                astream.close();
            }
        }

    }

    private String stripAnon(String line) {
        line = perl.substitute("s/\\banon\\b/ /gi", line);
        line = perl.substitute("s/\\(ed\\.\\)/ /gi", line);
        return line;
    }

}
