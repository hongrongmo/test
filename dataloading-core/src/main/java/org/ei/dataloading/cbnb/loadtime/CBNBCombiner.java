package org.ei.dataloading.cbnb.loadtime;

import java.io.ByteArrayInputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.MatchResult;
import org.ei.common.*;
import org.ei.util.GUID;
import org.ei.dataloading.*;
import org.ei.util.kafka.*;
import org.ei.dataloading.lookup.LookupEntry;

public class CBNBCombiner extends Combiner
{

    Perl5Util perl = new Perl5Util();

    private int exitNumber;

    private static String tablename;
    private static int loadNumber =0;
    private static String propertyFileName;
    
    /*HT added 09/21/2020 for lookup extraction to ES*/
    private String action = null;
    private LookupEntry lookupObj = null;
    
    public static void main(String args[]) throws Exception
    {

        String url = args[0];
        String driver = args[1];
        String username = args[2];
        String password = args[3];
        loadNumber = Integer.parseInt(args[4]);
        int recsPerbatch = Integer.parseInt(args[5]);
        String operation = args[6];
        tablename = args[7];
        String environment = args[8].toLowerCase();
        Combiner.TABLENAME = tablename;       
        System.out.println(Combiner.TABLENAME);
        
        if(args.length>9)
        {
        	propertyFileName=args[9];
        }

        CombinedWriter writer = new CombinedXMLWriter(recsPerbatch, loadNumber, "cbn");
        writer.setOperation(operation);

        CBNBCombiner c = new CBNBCombiner(writer);
        
        /*TH added 09/21/2020 for ES lookup generation*/
        for(String str: args)
        {
        	if(str.equalsIgnoreCase("lookup"))
        		c.setAction("lookup");
        	System.out.println("Action: lookup");
        }

        /*HT added 09/21/2020 to support ES lookup*/
        if(c.getAction() != null && c.getAction().equalsIgnoreCase("lookup"))
     	   c.writeLookupByWeekHook(loadNumber);
        {
        	 if(loadNumber > 3000)
             {
                     c.writeCombinedByWeekNumber(url,
                                                 driver,
                                                 username,
                                                 password,
                                                 loadNumber);
             }
             // extract the whole thing
             else if(loadNumber == 0)
             {
                 for(int yearIndex = 1980; yearIndex <= 2012; yearIndex++)
                 {
                     System.out.println("Processing year " + yearIndex + "...");
                     // create  a new writer so we can see the loadNumber/yearNumber in the filename
                     c = new CBNBCombiner(new CombinedXMLWriter(recsPerbatch, yearIndex,"cbn", environment));
                     c.writeCombinedByYear(url,
                                         driver,
                                         username,
                                         password,
                                         yearIndex);
                 }
             }
             else if(loadNumber == 1)
             {          
                     c.writeCombinedByTable(url,
                                         driver,
                                         username,
                                         password);
                
             }
             else
             {
                     c.writeCombinedByYear(url,
                                           driver,
                                           username,
                                           password,
                                           loadNumber);
             }
         
         System.out.println("write year" + loadNumber);

         //c.writeCombinedByYear(url, driver, username, password, loadNumber);

         System.out.println("completed  " + loadNumber);
        }       
    }
    public void setAction(String str)
    {
    	action = str;
    }
    public String getAction() {
    	return action;
    }

    public CBNBCombiner(CombinedWriter writer)
    {
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

        try
        {

        	stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            System.out.println("Running the query...");
            rs = stmt.executeQuery("select m_id, abn, doc, sco, fjl, isn, cdn, lan, ibn, src, scc,sct, ebt, cin, vol, iss, pag, reg, cym, sic, gic, gid, atl, otl, abs, edn, SUBSTR(pbn,1,4) pyr1, pyr,pbn,avl, pbr, load_number, seq_num from " + Combiner.TABLENAME +" where substr(pbn,1,4) ='"+ year +"'");

            System.out.println("Got records ...");
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
        KafkaService kafka=null;
        Map<String,String> batchData = new ConcurrentHashMap<String,String>();   
        Map<String,String> missedData = new ConcurrentHashMap<String,String>();
        int counter=0;
        int batchSize = 0;
        MessageSender sendMessage=null;
        Thread thread =null;
        long processTime = System.currentTimeMillis();
    	
    	
        try
        {
        	int totalCount = getResultSetSize(rs);
        	/*HT added 09/21/2020 for lookup extraction for lookup*/
        	if(this.propertyFileName!=null && !(getAction().equalsIgnoreCase("lookup")))
        	{
        		kafka = new KafkaService(processTime+"_CBNB_"+this.loadNumber, this.propertyFileName);
        	}
        	
        	System.out.println("epoch="+processTime+" database=CBNB totalCount="+totalCount);
        	
	        while (rs.next())
	        {
	
	            EVCombinedRec rec = new EVCombinedRec();
	            ++i;
	
	
	            String abString = getStringFromClob(rs.getClob("abs"));
	            try 
	            {
	            	String processInfo = processTime+"-"+totalCount+'-'+i+"-cbnb-"+rs.getString("LOAD_NUMBER")+"-NO_UPDATENUMBER";
		            rec.put(EVCombinedRec.PROCESS_INFO, processInfo);
		            if (validYear(rs.getString("pyr")) || validYear(rs.getString("pyr1")))
		            {            	
		            	
		            	//added for book project by hmo at 5/17/2017
		                String docType = rs.getString("doc");
		                if (docType == null)
		                {
		                    docType = "";
		                }
		                else if(docType!=null && docType.equalsIgnoreCase("Book"))
		                {
		                	docType = "bk";
		                }
		
		                rec.put(EVCombinedRec.DOCTYPE, docType);
		
		                if (rs.getString("sco") != null)
		                {
		                    rec.put(EVCombinedRec.SCOPE, rs.getString("sco"));
		                }
		
		                if (rs.getString("fjl") != null)
		                {
		                    rec.put(EVCombinedRec.SERIAL_TITLE, rs.getString("fjl"));
		                }
		
		                if (rs.getString("isn") != null)
		                {
		                    rec.put(EVCombinedRec.ISSN, rs.getString("isn"));
		                }
		
		                if (rs.getString("cdn") != null)
		                {
		                    rec.put(EVCombinedRec.CODEN, rs.getString("cdn"));
		                }
		
		                if (abString != null && !abString.equals("No abstract available"))
		                {
		                    rec.put(EVCombinedRec.ABSTRACT, abString);
		                }
		
		                if (rs.getString("lan") != null)
		                {
		                    rec.put(EVCombinedRec.LANGUAGE, prepareMulti(rs.getString("lan")));
		                }
		
		                if (rs.getString("ibn") != null)
		                {
		                    rec.put(EVCombinedRec.ISBN, rs.getString("ibn"));
		                }
		
		                if (rs.getString("pyr") != null && validYear(rs.getString("pyr")) )
		                {
		                    rec.put(EVCombinedRec.PUB_YEAR, rs.getString("pyr"));
		                }
		                else if(rs.getString("pyr1") != null && validYear(rs.getString("pyr1")) )
		                {
		                    rec.put(EVCombinedRec.PUB_YEAR, rs.getString("pyr1"));
		                }
		
		                // add companies to INT_PATENT_CLASSIFICATION , facet
		                if (rs.getString("src") != null)
		                {
		                    rec.put(EVCombinedRec.COMPANIES, prepareMulti(rs.getString("src")));
		                    rec.put(EVCombinedRec.INT_PATENT_CLASSIFICATION, prepareMulti(rs.getString("src")));
		                }
		
		                if(rs.getString("sct") != null)
		                {
		                    rec.put(EVCombinedRec.COUNTRY, prepareMulti(rs.getString("sct"),Constants.CO));
		                }
		
		
		                if(rs.getString("scc") != null)
		                {
		                    rec.put(EVCombinedRec.DESIGNATED_STATES, prepareMulti(rs.getString("scc")));
		                }
		
		                if (rs.getString("ebt") != null)
		                {
		                    rec.put(EVCombinedRec.BUSINESSTERMS, prepareMulti(rs.getString("ebt")));
		                    rec.put(EVCombinedRec.CONTROLLED_TERMS, prepareMulti(rs.getString("ebt")));
		                }
		
		                // Standard Industrial Code added to class codes field and facet
		                if (rs.getString("cin") != null)
		                {
		                    rec.put(EVCombinedRec.CHEMICALTERMS, prepareMulti(rs.getString("cin")));
		                    rec.put(EVCombinedRec.ECLA_CODES, prepareMulti(rs.getString("cin")));
		                }
		
		                if (rs.getString("reg") != null)
		                {
		                    rec.put(EVCombinedRec.CASREGISTRYNUMBER, prepareMulti(rs.getString("reg")));
		                }
		
		                if (rs.getString("cym") != null)
		                {
		                    rec.put(EVCombinedRec.CHEMICALACRONYMS, prepareMulti(rs.getString("cym")));
		                }
		
		                // Standard Industrial Code added to patent kind field and facet
		                if (rs.getString("sic") != null)
		                {
		                    rec.put(EVCombinedRec.PATENT_KIND, prepareMulti(rs.getString("sic")));
		                }
		
		                // Industrial Sector Code added to patent kind field and facet
		                if (rs.getString("gic") != null)
		                {
		                    rec.put(EVCombinedRec.CLASSIFICATION_CODE, prepareMulti(rs.getString("gic")));
		                }
		
		                // add gid to facets
		                if (rs.getString("gid") != null)
		                {
		                    rec.put(EVCombinedRec.INDUSTRIALSECTORS, prepareMulti(rs.getString("gid")));
		                    rec.put(EVCombinedRec.AUTHOR_AFFILIATION, prepareMulti(rs.getString("gid")));
		                }
		
		
		                if (rs.getString("atl") != null)
		                {
		                    rec.put(EVCombinedRec.TITLE, rs.getString("atl"));
		                }
		
		                if (rs.getString("otl") != null)
		                {
		                    rec.put(EVCombinedRec.TRANSLATED_TITLE, rs.getString("otl"));
		                }
		
		                if (rs.getString("avl") != null)
		                {
		                    rec.put(EVCombinedRec.AVAILABILITY, rs.getString("avl"));
		                }
		
		                if (rs.getString("seq_num") != null)
		                {
		                    rec.put(EVCombinedRec.PARENT_ID, rs.getString("seq_num"));
		                }
		
		                rec.put(EVCombinedRec.DOCID, rs.getString("M_ID"));
		                rec.put(EVCombinedRec.DATABASE, "cbn");
		                rec.put(EVCombinedRec.LOAD_NUMBER, rs.getString("LOAD_NUMBER"));
		                rec.put(EVCombinedRec.DEDUPKEY,
		                        getDedupKey(rec.get(EVCombinedRec.ISSN),
		                                    rec.get(EVCombinedRec.CODEN),
		                                    rs.getString("vol"),
		                                    rs.getString("iss"),
		                                    rs.getString("pag")));
		
		                rec.put(EVCombinedRec.VOLUME, getFirstNumber(rs.getString("vol")));
		                rec.put(EVCombinedRec.ISSUE, getFirstNumber(rs.getString("iss")));
		                rec.put(EVCombinedRec.STARTPAGE, getFirstNumber(rs.getString("pag")));
		                rec.put(EVCombinedRec.ACCESSION_NUMBER, rs.getString("abn"));
		
		                if(rs.getString("pbr") != null)
		                {
		                    rec.put(EVCombinedRec.PUBLISHER_NAME, prepareMulti(rs.getString("pbr")));
		                }		      
		                
		                /*HT 09/21/2020 added action check to support lookup extraction*/
		                if(this.propertyFileName==null && !(getAction().equalsIgnoreCase("lookup")))
		                {
		                	this.writer.writeRec(rec); //this is used for FAST extraction
		                }
		                else if (getAction() != null && getAction().equalsIgnoreCase("lookup"))
		                {
		                	this.lookupObj.writeLookupRec(rec);
		                }
		                else
		                {
			                /**********************************************************/
			    	        //following code used to test kafka by hmo@2020/02/3
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
		            else
		            {
		            	System.out.println(rs.getString("pyr")+" YEAR for record "+rs.getString("abn")+" is not good");
		            }
	            }           
	            catch(Exception e)
	            {
	            	System.out.println("why am I here");
	            	e.printStackTrace();
	            }
	 
	        }
        }
        finally
        { 
        	if(this.propertyFileName!=null)
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
	        System.out.println("Total "+i+" records");
        }
    }

    private String[] prepareAuthor(String aString) throws Exception
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

    private String[] prepareMulti(String multiString ,
                                    Constants constant)
                throws Exception
   {
        if (multiString != null) {

            AuthorStream astream = new AuthorStream(new ByteArrayInputStream(multiString.getBytes()));
            String s = null;
            ArrayList list = new ArrayList();
            while ((s = astream.readAuthor()) != null)
            {
                s = s.trim();
                if(constant == null)
                {
                    list.add(s);
                }
                else if(constant.equals(Constants.CO))
                {
                    list.add(CountryFormatter.formatCountry(s));
                }
            }
            return (String[]) list.toArray(new String[1]);
        }
        else
        {
            String[] str = new String[] { "" };
            return str;

        }
   }

    public String[] prepareMulti(String multiString) throws Exception
    {
        return prepareMulti(multiString, null);
    }


    private String stripAnon(String line)
    {
        line = perl.substitute("s/\\banon\\b/ /gi", line);
        line = perl.substitute("s/\\(ed\\.\\)/ /gi", line);
        return line;
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

    private String getDedupKey(String issn, String coden, String volume, String issue, String page) throws Exception
    {

        String firstVolume = getFirstNumber(volume);
        String firstIssue = getFirstNumber(issue);
        String firstPage = getFirstNumber(page);

        if ((issn == null &&
             coden == null) ||
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

    private boolean validYear(String year)
    {
        if (year == null)
        {
            return false;
        }

        if (year.trim().length() != 4)
        {
            return false;
        }
        
        return perl.match("/[1-9][0-9][0-9][0-9]/", year);
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

    public void writeCombinedByWeekHook(Connection con, int weekNumber) throws Exception
    {
        Statement stmt = null;
        ResultSet rs = null;

        try
        {

        	stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            rs = stmt.executeQuery("select m_id, abn, doc, sco, fjl, isn, cdn, lan, ibn, src, scc,sct, ebt, cin, vol, iss, pag, reg, cym, sic, gic, gid, atl, otl, abs, edn, SUBSTR(pbn,1,4) pyr1, pyr,pbn,avl,pbr,seq_num,load_number from " + Combiner.TABLENAME + " where load_number ='" + weekNumber + "'");
            writeRecs(rs);
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
    /*HT added 09/21/2020 to support ES lookup*/
    @Override
   	public void writeLookupByWeekHook(int weekNumber) throws Exception {
   		System.out.println("Extract Lookup");
   		String database =  Combiner.CURRENTDB;
   		lookupObj = new LookupEntry(database, weekNumber);
       	lookupObj.init();
   		
   	}
}
