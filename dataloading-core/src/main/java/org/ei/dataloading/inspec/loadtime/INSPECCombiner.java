package org.ei.dataloading.inspec.loadtime;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.ArrayList;
import java.util.regex.*;
import java.util.Arrays;

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
import org.ei.dataloading.bd.loadtime.BdNumericalIndexMapping;
import org.ei.dataloading.lookup.LookupEntry;
import org.ei.dataloading.MessageSender;
import org.ei.util.kafka.*;


public class INSPECCombiner
    extends Combiner
{

    Perl5Util perl = new Perl5Util();
    private static String tablename;
	private static final String databaseIndexName = "ins";
	private static String propertyFileName;
	private static int loadNumber = 0;
	
    /*HT added 09/21/2020 for lookup extraction to ES*/
    private String action = null;
    private LookupEntry lookupObj = null;
	
    public INSPECCombiner(CombinedWriter writer)
    {
        super(writer);
    }
    
    public INSPECCombiner(CombinedWriter writer, String propertyFileName, int loadNumber)  throws Exception
    {
        super(writer);
        this.propertyFileName=propertyFileName;
        this.loadNumber=loadNumber;
    }
    /*HT added 09/21/2020 to support ES Lookup*/
	public INSPECCombiner(CombinedWriter writer, String propertyFileName, String database) {
		super(writer);
		this.propertyFileName = propertyFileName;
		Combiner.CURRENTDB = database;
	}

    public static void main(String args[])
            throws Exception
    {
        String url = args[0];
        String driver = args[1];
        String username = args[2];
        String password = args[3];
        int recsPerbatch = Integer.parseInt(args[5]);
        String operation = args[6];
        tablename = args[7];
       
        String environment = args[8].toLowerCase();
        if(args.length>9)
        {
        	propertyFileName=args[9];
        }

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

        /*TH added 09/21/2020 for ES lookup generation*/
        Combiner.CURRENTDB = databaseIndexName;
        for(String str: args)
        {
        	if(str.equalsIgnoreCase("lookup"))
        		c.setAction("lookup");
        }
        /*HT added 09/21/2020 to support ES lookup, will need to run lookup anyway even if action not lookup*/
   	// if(c.getAction() != null && c.getAction().equalsIgnoreCase("lookup"))
     	   c.writeLookupByWeekHook(loadNumber);
			if (loadNumber > 3000) {
				c.writeCombinedByWeekNumber(url, driver, username, password, loadNumber);
			} else if (loadNumber == 2999) {
				int yearIndex = loadNumber;
				System.out.println("Processing MISC records as loadnumber " + yearIndex + "...");
				c = new INSPECCombiner(new CombinedXMLWriter(recsPerbatch, yearIndex, databaseIndexName, environment));
				c.writeCombinedByYear(url, driver, username, password, yearIndex);
			}
			// extract the whole thing
			else if (loadNumber == 0) {
				for (int yearIndex = 2005; yearIndex <= 2012; yearIndex++) {
					System.out.println("Processing year " + yearIndex + "...");
					// create a new writer so we can see the loadNumber/yearNumber in the filename
					c = new INSPECCombiner(
							new CombinedXMLWriter(recsPerbatch, yearIndex, databaseIndexName, environment));
					c.writeCombinedByYear(url, driver, username, password, yearIndex);
				}
			} else if (loadNumber == 1) {
				c.writeCombinedByTable(url, driver, username, password);

			} else {
				c.writeCombinedByYear(url, driver, username, password, loadNumber);
			}
    }
    public void setAction(String str)
    {
    	action = str;
    }
    public String getAction() {
    	return action;
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
    				writeRecs(rs,con);
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

        	stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            System.out.println("Doing year:"+year);

            if(year == 2999)
            {
                rs = stmt.executeQuery("select m_id, fdate, opan, copa, ppdate, sspdate, aaff, afc, ab, anum, pubti, su, pyr, nrtype, pdoi, cdate, cedate, aoi, aus, aus2, rnum, pnum, cpat, ciorg, iorg, pas, chi, pvoliss, pvol, piss, pipn, cloc, cls, pcdn, scdn, cvs, eaff, eds, pfjt, sfjt, fls, pajt, sajt, la, matid, ndi, pspdate, pepdate, popdate, sopdate, ppub, rtype, sbn, sorg, psn, ssn, tc, pubti, ti, trs, trmc, aaffmulti1, aaffmulti2, eaffmulti1, eaffmulti2, nssn, npsn, LOAD_NUMBER, seq_num, ipc, updatenumber from "+Combiner.TABLENAME+" where pyr='0294' or pyr='0994' or pyr='1101' or pyr='20007' or pyr='Dec.' or pyr='July' or pyr is null");
            }
            else
            {
                rs = stmt.executeQuery("select m_id, fdate, opan, copa, ppdate, sspdate, aaff, afc, ab, anum, pubti, su, pyr, nrtype, pdoi, cdate, cedate, aoi, aus, aus2, rnum, pnum, cpat, ciorg, iorg, pas, chi, pvoliss, pvol, piss, pipn, cloc, cls, pcdn, scdn, cvs, eaff, eds, pfjt, sfjt, fls, pajt, sajt, la, matid, ndi, pspdate, pepdate, popdate, sopdate, ppub, rtype, sbn, sorg, psn, ssn, tc, pubti, ti, trs, trmc, aaffmulti1, aaffmulti2, eaffmulti1, eaffmulti2, nssn, npsn, LOAD_NUMBER, seq_num, ipc, updatenumber from "+Combiner.TABLENAME+" where pyr ='"+ year +"'");
            }
            writeRecs(rs,con);
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

    void writeRecs(ResultSet rs,Connection con)
            throws Exception
    {
        
        KafkaService kafka=null;
        Map<String,String> batchData = new ConcurrentHashMap<String,String>();   
        Map<String,String> missedData = new ConcurrentHashMap<String,String>();
        int counter=0;
        int batchSize = 0;
        MessageSender sendMessage=null;
        Thread thread =null;
        int i = 0;
        int totalCount =0;
        long processTime = System.currentTimeMillis();
        //int MAX_THREAD = 110; 
        //ExecutorService pool = Executors.newFixedThreadPool(MAX_THREAD);  
        
        try
        {
    	    totalCount = getResultSetSize(rs);   		// HT 09/21/2020 ONLY COMMENT OUT WHEN GENERATING WHOLE TABLE LOOKUP, UNCOMMENT IN PROD
        	if (this.propertyFileName != null && (getAction() == null || !(getAction().equalsIgnoreCase("lookup")))) // HT only create Kafka instance when it is // not lookup extraction
    	    {
    	    	System.out.println("propertyFileName="+this.propertyFileName);
    	    	kafka = new KafkaService(processTime+"_ins_"+loadNumber, this.propertyFileName);
    	    }
    	    System.out.println("epoch="+processTime+" database=INS totalCount="+totalCount);
	        while(rs.next())
	        {
	            EVCombinedRec rec = new EVCombinedRec();
	            ++i;
	            String mid = rs.getString("M_ID");
	            String abString = getStringFromClob(rs.getClob("ab"));		            	           
	            
	            String accessnumber = rs.getString("anum");
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
	            	String processInfo = processTime+"-"+totalCount+'-'+i+"-ins-"+rs.getString("LOAD_NUMBER")+'-'+rs.getString("UPDATENUMBER");
                	rec.put(EVCombinedRec.PROCESS_INFO, processInfo);
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
	                    	aaff.append(Constants.AUDELIMITER);
	                        aaff.append(rs.getString("aaffmulti1"));
	
	                        if (rs.getString("aaffmulti2") != null)
	                        {
	                            aaff.append(rs.getString("aaffmulti2"));
	                        }
	                    }
	
	                    //System.out.println(aaff.toString());
						//System.out.println(Arrays.toString(prepareAffiliation(aaff.toString())));
	                    rec.put(EVCombinedRec.AUTHOR_AFFILIATION, prepareAffiliation(aaff.toString()));
	                  //added by hmo on 2019/09/11
	                   // rec.put(EVCombinedRec.AFFILIATIONID, prepareAffiliationID(aaff.toString()));
	                   // rec.put(EVCombinedRec.ORG_ID, prepareAffiliationORGID(aaff.toString()));
	                    rec.put(EVCombinedRec.AFFILIATIONID, prepareAffiliationORGID(aaff.toString()));
	                    rec.put(EVCombinedRec.ORG_ID, prepareAffiliationID(aaff.toString()));
	                    rec.put(EVCombinedRec.AFFILIATION_LOCATION, prepareAffiliationLocation(aaff.toString()));
	                    
	                }
	
	                if(rs.getString("afc") != null)
	                {
	                    String countryFormatted = Country.formatCountry(rs.getString("afc"));
	                    if (countryFormatted != null)
	                    {
	                        rec.put(EVCombinedRec.COUNTRY, countryFormatted);
	                        if(rec.get(EVCombinedRec.AFFILIATION_LOCATION)==null)
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
	
	                    //rec.put(EVCombinedRec.EDITOR_AFFILIATION, prepareAuthor(eaff.toString()));
	                    rec.put(EVCombinedRec.EDITOR_AFFILIATION, prepareAffiliation(eaff.toString()));
	                    
	                    //added by hmo on 2019/09/11
	                    rec.put(EVCombinedRec.AFFILIATIONID, prepareAffiliationID(eaff.toString()));
	                    rec.put(EVCombinedRec.ORG_ID, prepareAffiliationORGID(eaff.toString()));
	                    
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
	                    processNumericalIndex(rec,mid,accessnumber,rs.getString("ndi"),con);
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
	                        if(rec.get(EVCombinedRec.AFFILIATION_LOCATION)==null)
	                        	rec.put(EVCombinedRec.AFFILIATION_LOCATION, countryFormatted);
	                    }
	                }
	
	                if(rs.getString("ipc")!=null)
	                {
	                    String ipcString = getIpcCode(rs.getString("ipc"));
	                    ipcString = perl.substitute("s/\\//SLASH/g", ipcString);
	                    rec.put(EVCombinedRec.INT_PATENT_CLASSIFICATION, prepareMulti(ipcString));
	                }
	
	                rec.put(EVCombinedRec.DOCID, rs.getString("M_ID"));
	                rec.put(EVCombinedRec.PARENT_ID, rs.getString("seq_num"));
	
	                rec.put(EVCombinedRec.DATABASE, "ins");
	                rec.put(EVCombinedRec.LOAD_NUMBER, rs.getString("LOAD_NUMBER"));
	                if(rs.getString("UPDATENUMBER")!=null)
	                {
	                	rec.put(EVCombinedRec.UPDATE_NUMBER, rs.getString("UPDATENUMBER"));
	                }
	
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
	
	                if (this.propertyFileName == null && (getAction() != null && !(getAction().equalsIgnoreCase("lookup"))))
	                {
	                	writer.writeRec(rec);//Use this line for FAST extraction
	                }
	                /*HT added 09/21/2020 for ES lookup*/
	                else if (getAction() != null && getAction().equalsIgnoreCase("lookup"))
	                {
	                	this.lookupObj.writeLookupRec(rec);
	                }
	                else
	                {
		                /**********************************************************/
		                //following code used to test kafka by hmo@2020/01/30
		                //this.writer.writeRec(recArray,kafka);
		                /**********************************************************/
		                
		                //writer.writeRec(rec,kafka);
		                
	                	this.writer.writeRec(rec,kafka, batchData, missedData);
	                	if(this.lookupObj!=null)
	                		this.lookupObj.writeLookupRec(rec);						//HT added later for weekly lookup extraction for ES
			            if(counter<batchSize)
			            {            	
			            	counter++;
			            }
			            else
			            { 
			            	/*
			            	 thread = new Thread(sendMessage);
			            	 sendMessage= new MessageSender(kafka,batchData,missedData);		            	 
			            	 thread.start(); 
			            	 */
			            	 kafka.runBatch(batchData,missedData);
			            	 batchData = new ConcurrentHashMap<String,String>();
			            	 counter=0;			            	 
			            }
	                }
		            
	            }
	
	        }
       }
       finally
       {
    	    System.out.println("Total "+i+" records");
    	    if(i!=totalCount)
     	    {
     	    	System.out.println("**Got "+i+" records instead of "+totalCount );
     	    }
    	    
    	    if (this.propertyFileName != null && (getAction() == null || !(getAction().equalsIgnoreCase("lookup"))))
        	{
	        	try
	        	{
	        		 kafka.runBatch(batchData,missedData);
	        		 /*
	        		 thread = new Thread(sendMessage);
	            	 sendMessage= new MessageSender(kafka,batchData,missedData);            	 
	            	 thread.start(); 
	            	 */
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
       }
    }
    
    private String getIpcCode(String ipc) throws Exception
    {
    	StringBuffer ipcCodes=new StringBuffer();
    	if(ipc !=null)
    	{
    		String[] ipcs=ipc.split(Constants.AUDELIMITER,-1);
    		for(int i=0;i<ipcs.length;i++)
    		{
    			if(ipcs[i]!=null)
    			{
    				String[] ipcD= ipcs[i].split(Constants.IDDELIMITER,-1);
    				ipcCodes.append(ipcD[0]);
    				if(i<ipcs.length-1)
    				{
    					ipcCodes.append(Constants.AUDELIMITER);
    				}
    				
    			}
    			
    		}
    	}
    	//System.out.println(ipc);
    	//System.out.println(ipcCodes.toString());
    	return ipcCodes.toString();
    		
    }
    
    private void processNumericalIndex(EVCombinedRec rec, String mid, String accessnumber, String niString,Connection con) throws Exception
    {
    	Statement stmt = null;
    	ResultSet rs = null;
    	InsNumericalIndexMapping bdni = new InsNumericalIndexMapping();
    	NumberFormat formatter = new DecimalFormat();
    	HashSet<String> unitSet = new HashSet<String>();
    	
    	try
    	{
    		if(accessnumber!=null && accessnumber.length()>0)
    		{
    			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	    		//System.out.println("Running the Numerical query...");
	    		String sqlQuery = "select * from INS_MASTER_NUMERICAL where ACCESSNUMBER='"+accessnumber+"' order by unit";
	    		//System.out.println(sqlQuery);
	    		rs = stmt.executeQuery(sqlQuery);
	    		//System.out.println("Got records ...from table INS_MASTER_NUMERICAL");
	    		while (rs.next())
	    	    {	
	    	        String unit = null;
    				String maximum = null;
    				String minimum = null;
    				StringBuffer niBuffer = new StringBuffer();
    				StringBuffer rangesBuffer = new StringBuffer();
    				if(rs.getString("UNIT") != null)
					{
						unit = rs.getString("UNIT").toLowerCase();
						unit = unit.replaceAll(" ", "_");
						//System.out.println("UNITS+"+unit);
						unitSet.add(unit);
					}
					else
					{
						System.out.println("no unit found for accessnumber "+accessnumber);
						return;
					}
					//System.out.println("PUI="+pui+" UNIT="+unit);
	    	        if(rs.getString("MAXIMUM") != null && rs.getString("MINIMUM") != null)
	    	        {
		    	          maximum = rs.getString("MAXIMUM");
		    	          minimum = rs.getString("MINIMUM");
		    	          String niKey = bdni.get(unit+"_ranges");
		    	          niBuffer.append(maximum+Constants.AUDELIMITER);	
		    	          niBuffer.append(minimum+Constants.AUDELIMITER);
		    	          String ranges = minimum+" "+maximum;
		    	          if(niKey!=null)
		    	          {
		    	       			String oldRanges = rec.getString(niKey);
								if(oldRanges!=null)
								{								
									rec.put(niKey,oldRanges+ranges+" | ");								
								}
								else
								{
									rec.put(niKey," | "+ranges+" | ");									
								}
		    	          }
		    	         
		    	          
		    	          niKey = bdni.get(unit+"_text");
		    	          if(niKey!=null)
		    	          {	    	          	
								String[] oldText = rec.getStrings(niKey);
								if(oldText==null)
								{
									oldText = new String[0];
								}
								
								String[] temp = prepareMulti(niBuffer.toString());
								rec.put(niKey,uniqueConcat(oldText,temp));
								/*
								{
									String[] temp = prepareMulti(niBuffer.toString());
									rec.put(niKey,uniqueConcat(oldText,temp));
									//System.out.println("KeyText1="+niKey+" TEXT="+Arrays.toString(uniqueConcat(oldText,temp)));
								}
								else
								{
									rec.put(niKey,prepareMulti(niBuffer.toString()));
									//System.out.println("KeyText2="+niKey+" TEXT="+niBuffer.toString().replace(Constants.AUDELIMITER, " | "));
								}
								*/
		    	          	
		    	          }
		    	          else
		    	          {
		    	        	  //System.out.println("no mapping found for "+unit+"_text");
		    	          }
	    	          
	    	          }
	    	       
	    	    }
	    		rec.put(EVCombinedRec.NUMERICALUNITS, unitSet.toArray(new String[unitSet.size()]));
	    		  
    		}
    		
	    	
    
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
    
    public String[] uniqueConcat(String[] a, String[] b) 
    {
    	   HashSet<String> tSet = new HashSet<String>();
    	   String temp = null;
    	   for(int i=0;i<a.length;i++)
    	   {
    		   temp = a[i];
    		   if(temp != null && temp.length()>0)
    		   {
    			   temp = temp.replace("-", "minus").replace("+", "plus" );
    			   tSet.add(temp);
    		   }
    		   //System.out.println("NUM_TEXT1= "+a[i]+" ==> "+temp);
    	   }
    	   
    	   for(int i=0;i<b.length;i++)
    	   {
    		   temp = b[i];
    		   if(temp != null && temp.length()>0)
    		   {
    			   temp = temp.replace("-", "minus").replace("+", "plus");
    			   tSet.add(temp);
    		   }
    		   //System.out.println("NUM_TEXT2= "+b[i]+" ==> "+temp);
    	   }
    	   
    	   return tSet.toArray(new String[tSet.size()]);
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
    
    public  ArrayList prepareAllAffiliation(String aString)
            throws Exception
    {

        ArrayList list = new ArrayList();
        
        //added by hmo at 11/17/2020 for removing comma and period 
        //do this in CombinedXMLWriter for all database based on T.M. on EV-10082 @11/30/2020
        aString=aString.replaceAll("\\.|,", " ");

        
        String[] st = aString.split(Constants.AUDELIMITER,-1);
        String s;
        HashMap  afMap = new HashMap();
        
        for (int i=0;i<st.length;i++)
        {
            s = st[i];
            if(s!=null && s.length() > 0)
            {
            	String[] a;
                if(s.indexOf(Constants.IDDELIMITER) > -1)
                {
                	a = s.split(Constants.IDDELIMITER,-1); 
                	
                } 
                else
                {
                	 a = new String[1];
                	 a[0]=s;
                }
               
                list.add(a);           
            }
            
        }
        return list;
    }
    
    public  String[] prepareAffiliation(String aString)
            throws Exception
    {

        ArrayList list = new ArrayList();
        ArrayList allAff = prepareAllAffiliation(aString);
        String[] sAff;
        String s;

        for (int i=0;i<allAff.size();i++)
        {
            sAff = (String[])allAff.get(i);
            if(sAff!=null && sAff.length>0)
            {
            	String aff=null;
          	   	String org=null;
          	   	String dept=null;
          	   	StringBuffer affBuffer = new StringBuffer();
          	   	for(int j=0;j<sAff.length;j++)
          	   	{
          	   		String ss = sAff[j];
          	   		if(j==0 && !list.contains(ss))
          	   		{
          	   			list.add(ss);          	   			
          	   		}
          	   		
          	   		if(j==3)
          	   		{
          	   			org = sAff[j];
          	   		}
          	   		
	          	   	if(j==4)
	      	   		{
	      	   			dept = sAff[j];
	      	   		}
          	   	
          	   	
	          	   	if(org!=null && dept!=null && dept.length()>0)
	          	   	{
	          	   		ss = dept+", "+org;
	          	   		if(!list.contains(ss))
	          	   			list.add(ss);
	          	   	}
	          	   	else if(org!=null && org.length()>0)
	          	   	{
	          	   		ss = org;
	          	   		if(!list.contains(ss))
	          	   			list.add(ss);
	          	   	}  
	          	   	else if(dept!=null && dept.length()>0)
	          	   	{
	          	   		ss = dept;
	          	   		if(!list.contains(ss))
	          	   			list.add(ss);
	          	   	}  
          	   	}
          	                
            }
        }
        
      
        return (String[])list.toArray(new String[1]);
    }
        
    private	String[] prepareAffiliationLocation(String aString)
            throws Exception
    {

        ArrayList list = new ArrayList();
        ArrayList allAff = prepareAllAffiliation(aString);
        String[] sAff;
        String s;

        for (int i=0;i<allAff.size();i++)
        {
            sAff = (String[])allAff.get(i);
            if(sAff!=null && sAff.length>0)
            {
               for(int j=0;j<sAff.length;j++)
               {
            	   if(j==2 || j==5 || j==6 || j==7 || j==8)
            	   {
	            	   list.add(sAff[j]);
            	   }
               }

               
            }

        }

        return (String[])list.toArray(new String[1]);

    }
    
    private	String[] prepareAffiliationID(String aString)
            throws Exception
    {

        ArrayList list = new ArrayList();
        ArrayList allAff = prepareAllAffiliation(aString);
        String[] sAff;
        String s;

        for (int i=0;i<allAff.size();i++)
        {
            sAff = (String[])allAff.get(i);
            if(sAff!=null && sAff.length>0)
            {
               for(int j=0;j<sAff.length;j++)
               {
            	   if(j==1)
            	   {
	            	   
            		   String afid = sAff[j];
            		   if(!list.contains(afid)) {
            			   list.add(afid);
            		   }
	            	   if(afid.indexOf(".")>-1)
	            	   {
	            		   afid = afid.replaceAll("\\.", "DQD");
	            		   if(!list.contains(afid)) {
	            			   list.add(afid);
	            		   }
	            	   }
            	   }
               }
               
            }

        }

        return (String[])list.toArray(new String[1]);

    }
        

        
    public  String[] prepareAffiliationORGID(String aString)
            throws Exception
    {

        ArrayList list = new ArrayList();
        ArrayList allAff = prepareAllAffiliation(aString);
        String[] sAff;
        String s;

        for (int i=0;i<allAff.size();i++)
        {
            sAff = (String[])allAff.get(i);
            if(sAff!=null && sAff.length>0)
            {
               for(int j=0;j<sAff.length;j++)
               {
            	   if(j==9)
            	   {
	            	   list.add(sAff[j]);
            	   }
               }

               
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
            //list.add("MC");
        	//change for book project by hmo at2017/05/16
        	list.add("CH");
        }
        else if(newdocType.equals("30"))
        {
            //list.add("MR");
        	//change for book project by hmo at2017/05/16
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

        	stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String sqlQuery = "";
        //  System.out.println("Running the query...");
        //  rs = stmt.executeQuery("select m_id, aaff, su, ab, anum, aoi, aus, aus2,pyr, rnum, pnum, cpat, ciorg, iorg, pas, cdate, cedate, doi, nrtype, doit, chi, voliss, ipn, cloc, cls, cn, cnt, cvs, eaff, eds, fjt, fls, fttj, la, matid, ndi, pdate, pub, rtype, sbn, sorg, sn, snt, tc, tdate, thlp, ti, trs, trmc, LOAD_NUMBER from "+Combiner.TABLENAME+ " where LOAD_NUMBER = "+loadN);
            if (loadN == 3001)
            {
                sqlQuery="select m_id, fdate, opan, copa, ppdate,sspdate, aaff, afc, su, pubti, pfjt, pajt, sfjt, sajt, ab, anum, aoi, aus, aus2, pyr, rnum, pnum, cpat, ciorg, iorg, pas, pcdn, scdn, cdate, cedate, pdoi, nrtype, chi, pvoliss, pvol, piss, pipn, cloc, cls, cvs, eaff, eds, fls, la, matid, ndi, pspdate, ppub, rtype, sbn, sorg, psn, ssn, tc, sspdate, ti, trs, trmc, aaffmulti1, aaffmulti2, eaffmulti1, eaffmulti2, nssn, npsn, LOAD_NUMBER, seq_num, ipc, updatenumber from "+Combiner.TABLENAME+ " where pyr is null and load_number < 200537";
            }
            else if (loadN == 3002)
            {
                sqlQuery="select m_id, fdate, opan, copa, ppdate,sspdate, aaff, afc, su, pubti, pfjt, pajt, sfjt, sajt, ab, anum, aoi, aus, aus2, pyr, rnum, pnum, cpat, ciorg, iorg, pas, pcdn, scdn, cdate, cedate, pdoi, nrtype, chi, pvoliss, pvol, piss, pipn, cloc, cls, cvs, eaff, eds, fls, la, matid, ndi, pspdate, ppub, rtype, sbn, sorg, psn, ssn, tc, sspdate, ti, trs, trmc,aaffmulti1, aaffmulti2, eaffmulti1, eaffmulti2, nssn, npsn,  LOAD_NUMBER, seq_num, ipc, updatenumber from "+Combiner.TABLENAME+ " where pyr like '194%'or pyr like '195%' or (pyr like '196%' and pyr != '1969')";
            }
            else if(loadN ==8413583)
            {
                sqlQuery="select m_id, fdate, opan, copa, ppdate,sspdate, aaff, afc, su, pubti, pfjt, pajt, sfjt, sajt, ab, anum, aoi, aus, aus2, pyr, rnum, pnum, cpat, ciorg, iorg, pas, pcdn, scdn, cdate, cedate, pdoi, nrtype, chi, pvoliss, pvol, piss, pipn, cloc, cls, cvs, eaff, eds, fls, la, matid, ndi, pspdate, ppub, rtype, sbn, sorg, psn, ssn, tc, sspdate, ti, trs, trmc,aaffmulti1, aaffmulti2, eaffmulti1, eaffmulti2, nssn, npsn, LOAD_NUMBER, seq_num, ipc, updatenumber from "+Combiner.TABLENAME+ " where Anum = '"+loadN+"'";
            }
            else
            {
                sqlQuery="select m_id, fdate, opan, copa, ppdate,sspdate, aaff, afc, su, pubti, pfjt, pajt, sfjt, sajt, ab, anum, aoi, aus, aus2, pyr, rnum, pnum, cpat, ciorg, iorg, pas, pcdn, scdn, cdate, cedate, pdoi, nrtype, chi, pvoliss, pvol, piss, pipn, cloc, cls, cvs, eaff, eds, fls, la, matid, ndi, pspdate, ppub, rtype, sbn, sorg, psn, ssn, tc, sspdate, ti, trs, trmc,aaffmulti1, aaffmulti2, eaffmulti1, eaffmulti2, nssn, npsn, LOAD_NUMBER, seq_num, ipc, updatenumber from "+Combiner.TABLENAME+ " where LOAD_NUMBER = "+loadN;
            }
            System.out.println("Inspect sqlQuery= "+sqlQuery);
            rs = stmt.executeQuery(sqlQuery);
            writeRecs(rs,con);
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
    
    @Override
	/*HT added 09/21/2020 wk: [202040] for Lookup extraction for ES*/
	public void writeLookupByWeekHook(int weekNumber) throws Exception {
		System.out.println("Extract Lookup");
		String database =  Combiner.CURRENTDB;
    	lookupObj = new LookupEntry(database, weekNumber);
    	lookupObj.init();
	}
    
    /*HT added 09/21/2020, Get Lookup list from temp tables based on correction action, this method to support bdCorrectio, not for ES lookup extraction*/
	  public Map<String,List<String>> getESLookupData(int weekNumber, String actionType, String tableName, Connection sqlcon, String database) throws Exception {
		  
		  Statement stmt = null; 
		  ResultSet rs = null; 
		  String sqlQuery = null, cpxSqlQuery = null;
		  Map<String,List<String>> recs = null;
		  
		  try { 
				
		  Connection con = sqlcon;
		  stmt = con.createStatement();
		  System.out.println("database: " + database);

		  Combiner.CURRENTDB = database;
		  

			
		  if(actionType.equalsIgnoreCase("update")||actionType.equalsIgnoreCase("ins")||actionType.equalsIgnoreCase("lookupindex") || actionType.equalsIgnoreCase("insBackup"))
				 sqlQuery = "select m_id, fdate, opan, copa, ppdate,sspdate, aaff, afc, su, pubti, pfjt, pajt, sfjt, sajt, ab, anum, aoi, aus, aus2, pyr, "
			 		+ "rnum, pnum, cpat, ciorg, iorg, pas, pcdn, scdn, cdate, cedate, pdoi, nrtype, chi, pvoliss, pvol, piss, pipn, cloc, cls, cvs, eaff, "
			 		+ "eds, fls, la, matid, ndi, pspdate, ppub, rtype, sbn, sorg, psn, ssn, tc, sspdate, ti, trs, trmc,aaffmulti1, aaffmulti2, eaffmulti1, "
			 		+ "eaffmulti2, nssn, npsn, LOAD_NUMBER, ipc, updatenumber from "+tableName+ " where LOAD_NUMBER = "+weekNumber;
			 else
				 sqlQuery = "select m_id, fdate, opan, copa, ppdate,sspdate, aaff, afc, su, pubti, pfjt, pajt, sfjt, sajt, ab, anum, aoi, aus, aus2, pyr, "
					 		+ "rnum, pnum, cpat, ciorg, iorg, pas, pcdn, scdn, cdate, cedate, pdoi, nrtype, chi, pvoliss, pvol, piss, pipn, cloc, cls, cvs, eaff, "
					 		+ "eds, fls, la, matid, ndi, pspdate, ppub, rtype, sbn, sorg, psn, ssn, tc, sspdate, ti, trs, trmc,aaffmulti1, aaffmulti2, eaffmulti1, "
					 		+ "eaffmulti2, nssn, npsn, LOAD_NUMBER, ipc, updatenumber from "+tableName;

			System.out.println(sqlQuery);
			rs = stmt.executeQuery(sqlQuery);

			recs = prepareLookupRecs(rs, weekNumber);
				 
		  }
		  finally 
		  {

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
		  return recs;
	    
	  }
	  //HT added 09/21/2020 for ES lookup 
	public Map<String, List<String>> prepareLookupRecs(ResultSet rs, int weekNumber) throws Exception {
		int i = 0;
		EVCombinedRec[] recArray = null;
		boolean isChimica = false;
		boolean isCpx = false;
		String accessNumber = "";
		String pui = "";

		Map<String, List<String>> recs = new HashMap<>();
		List<String> authorList = new ArrayList<>();
		List<String> affiliationList = new ArrayList<>();
		List<String> serialTitleList = new ArrayList<>();
		List<String> controltermList = new ArrayList<>();
		List<String> publishernameList = new ArrayList<>();
		List<String> ipcList = new ArrayList<>();

		try {
			while (rs.next()) {
				EVCombinedRec rec = new EVCombinedRec();
				++i;
				String mid = rs.getString("M_ID");
				String abString = getStringFromClob(rs.getClob("ab"));

				String accessnumber = rs.getString("anum");
				String strYear = "";
				if (rs.getString("pyr") != null && validYear(getPubYear(rs.getString("pyr")))) {
					strYear = getPubYear(rs.getString("pyr"));
				} else if (rs.getString("sspdate") != null && validYear(getPubYear(rs.getString("sspdate")))) {
					strYear = getPubYear(rs.getString("sspdate"));
				} else if (rs.getString("fdate") != null && validYear(getPubYear(rs.getString("fdate")))) {
					strYear = getPubYear(rs.getString("fdate"));
				} else if (rs.getString("cdate") != null && validYear(getPubYear(rs.getString("cdate")))) {
					strYear = getPubYear(rs.getString("cdate"));
				} else if (rs.getString("su") != null && validYear(getPubYear(rs.getString("su")))) {

					strYear = getPubYear(rs.getString("su"));
				}

				try {
					if (validYear(strYear)) {

						// AUTHOR
						if ((rs.getString("aus") != null) || (rs.getString("aus2") != null)) {
							StringBuffer aus = new StringBuffer();
							if (rs.getString("aus") != null) {
								aus.append(rs.getString("aus"));
							}
							if (rs.getString("aus2") != null) {
								aus.append(rs.getString("aus2"));
							}

							rec.put(EVCombinedRec.AUTHOR, prepareAuthor(aus.toString()));
						}

						// AFFILIATION
						if (rs.getString("aaff") != null) {
							StringBuffer aaff = new StringBuffer(rs.getString("aaff"));

							if (rs.getString("aaffmulti1") != null) {
								aaff.append(Constants.AUDELIMITER);
								aaff.append(rs.getString("aaffmulti1"));

								if (rs.getString("aaffmulti2") != null) {
									aaff.append(rs.getString("aaffmulti2"));
								}
							}
							
							rec.put(EVCombinedRec.AUTHOR_AFFILIATION, prepareAffiliation(aaff.toString()));
							rec.put(EVCombinedRec.AFFILIATION_LOCATION, prepareAffiliationLocation(aaff.toString()));

						}

						if (rs.getString("afc") != null) {
							String countryFormatted = Country.formatCountry(rs.getString("afc"));
							if (countryFormatted != null) {
								if (rec.get(EVCombinedRec.AFFILIATION_LOCATION) == null)
									rec.put(EVCombinedRec.AFFILIATION_LOCATION, countryFormatted);
							}
						}

						// CONTROLLED_TERMS

						if (rs.getString("cvs") != null) {
							rec.put(EVCombinedRec.CONTROLLED_TERMS, prepareMulti(rs.getString("cvs")));
						}

						// SERIAL_TITLE

						if (rs.getString("pubti") != null) {
							rec.put(rec.MONOGRAPH_TITLE, rs.getString("pubti"));
							rec.put(EVCombinedRec.SERIAL_TITLE, rs.getString("pubti"));
						}

						if (rs.getString("pfjt") != null) {
							rec.put(EVCombinedRec.SERIAL_TITLE, rs.getString("pfjt"));
						}

						// PUBLISHERNAME
						if (rs.getString("ppub") != null) {
							rec.put(EVCombinedRec.PUBLISHER_NAME, rs.getString("ppub"));
						}
						// replacing AUTHOR_AFFILIATION for Patent Assignee
						if (rs.getString("pas") != null) {
							rec.put(EVCombinedRec.AUTHOR_AFFILIATION, prepareMulti(rs.getString("pas")));
						}

						// AFFILIATION_LOCATION
						if (rs.getString("iorg") != null) {
							rec.put(rec.NOTES, prepareMulti(rs.getString("iorg")));

							String countryFormatted = Country.formatCountry(rs.getString("ciorg"));
							if (countryFormatted != null && !rec.containsKey(EVCombinedRec.COUNTRY)) {
								rec.put(EVCombinedRec.COUNTRY, countryFormatted);
								if (rec.get(EVCombinedRec.AFFILIATION_LOCATION) == null)
									rec.put(EVCombinedRec.AFFILIATION_LOCATION, countryFormatted);
							}
						}

						// IPC
						if (rs.getString("ipc") != null) {
							 String ipcString = getIpcCode(rs.getString("ipc"));
			                    ipcString = perl.substitute("s/\\//SLASH/g", ipcString);
			                    rec.put(EVCombinedRec.INT_PATENT_CLASSIFICATION, prepareMulti(ipcString));
						}

						rec.put(EVCombinedRec.DOCID, rs.getString("M_ID"));

						rec.put(EVCombinedRec.DATABASE, "ins");
						rec.put(EVCombinedRec.LOAD_NUMBER, rs.getString("LOAD_NUMBER"));
						if (rs.getString("UPDATENUMBER") != null) {
							rec.put(EVCombinedRec.UPDATE_NUMBER, rs.getString("UPDATENUMBER"));
						}

						rec.put(EVCombinedRec.ACCESSION_NUMBER, rs.getString("ANUM"));
						if(recArray!=null)
						{
							this.lookupObj.setLookupRecs(recArray, authorList, affiliationList, serialTitleList,
								controltermList, publishernameList, ipcList);
						}

					}
				} catch (Exception e) {
					System.out.println(
							"**** ERROR Found on preparinglookuprec for access number " + accessNumber + " *****");
					//System.out.println(e.getMessage());
					e.printStackTrace();
				}

			}
		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}
		return recs;

	}
}
