package org.ei.dataloading.knovel.loadtime;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Calendar;
import java.util.Set;
import java.util.HashSet;

import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.MatchResult;
import org.ei.dataloading.*;
import org.ei.dataloading.georef.loadtime.*;
import org.ei.dataloading.lookup.LookupEntry;
import org.ei.common.bd.*;
import org.ei.common.georef.CitationView;
import org.ei.common.georef.DocumentView;
import org.ei.common.georef.DocumentView.DocumentTypeDecorator;
import org.ei.common.*;
import org.ei.util.GUID;
import org.ei.util.StringUtil;
import org.ei.util.kafka.*;


	public class KnovelCombiner 
	    extends CombinerTimestamp
	{

	    public String[] EVCombinedRecKeys = {EVCombinedRec.AUTHOR, EVCombinedRec.AUTHOR_AFFILIATION, EVCombinedRec.AFFILIATION_LOCATION, EVCombinedRec.COUNTRY, EVCombinedRec.EDITOR, EVCombinedRec.TITLE, EVCombinedRec.TRANSLATED_TITLE, EVCombinedRec.VOLUME_TITLE, EVCombinedRec.ABSTRACT, EVCombinedRec.CONTROLLED_TERMS, EVCombinedRec.UNCONTROLLED_TERMS, EVCombinedRec.CHEMICALTERMS, EVCombinedRec.INT_PATENT_CLASSIFICATION, EVCombinedRec.ISSN, EVCombinedRec.CODEN, EVCombinedRec.ISBN, EVCombinedRec.SERIAL_TITLE, EVCombinedRec.MAIN_HEADING, EVCombinedRec.PUBLISHER_NAME, EVCombinedRec.TREATMENT_CODE, EVCombinedRec.LANGUAGE, EVCombinedRec.DOCTYPE, EVCombinedRec.CLASSIFICATION_CODE, EVCombinedRec.CONFERENCE_CODE, EVCombinedRec.CONFERENCE_NAME, EVCombinedRec.CONFERENCE_LOCATION, EVCombinedRec.MEETING_DATE, EVCombinedRec.SPONSOR_NAME, EVCombinedRec.CONFERENCEAFFILIATIONS, EVCombinedRec.CONFERENCEEDITORS, EVCombinedRec.CONFERENCEPARTNUMBER, EVCombinedRec.CONFERENCEPAGERANGE, EVCombinedRec.CONFERENCENUMBERPAGES, EVCombinedRec.MONOGRAPH_TITLE, EVCombinedRec.DATABASE, EVCombinedRec.LOAD_NUMBER, EVCombinedRec.PUB_YEAR, EVCombinedRec.DEDUPKEY, EVCombinedRec.VOLUME, EVCombinedRec.ISSUE, EVCombinedRec.STARTPAGE, EVCombinedRec.ACCESSION_NUMBER, EVCombinedRec.REPORTNUMBER, EVCombinedRec.DOI, EVCombinedRec.COPYRIGHT, EVCombinedRec.PII, EVCombinedRec.PUI, EVCombinedRec.COMPANIES, EVCombinedRec.CASREGISTRYNUMBER, EVCombinedRec.PUB_SORT};

	    Perl5Util perl = new Perl5Util();
	    private static String tablename;
	    private static String currentDb;
	    private static HashMap issnARFix = new HashMap();
	    private List auid;
	    private static String propertyFileName;
	    private static int loadNumber=0;
	    
	    /*HT added 09/21/2020 for lookup extraction to ES*/
	    private String action = null;
	    private LookupEntry lookupObj = null;
	    

	    public static void main(String args[])
	        throws Exception
	    {
	        String url = args[0];
	        String driver = args[1];
	        String username = args[2];
	        String password = args[3];
	        loadNumber = 0;
	        int recsPerbatch = 0;
	        try {
	            loadNumber = Integer.parseInt(args[4]);
	        }
	        catch(NumberFormatException e) {
	            loadNumber = 0;
	        }
	        try {
	            recsPerbatch = Integer.parseInt(args[5]);
	        }
	        catch(NumberFormatException e) {
	            recsPerbatch = 50000;
	        }
	        String operation = args[6];
	        tablename = args[7];
	        String currentDb = args[8].toLowerCase();
	        if(args.length>9)
	        {
	        	propertyFileName=args[9];
	        	System.out.println("propertyFileName="+propertyFileName);
	        }
	        long timestamp=0;

	        Combiner.TABLENAME = tablename;
	        Combiner.CURRENTDB = currentDb;
	        System.out.println(Combiner.TABLENAME);

	        String dbname = currentDb;
	        Calendar now = Calendar.getInstance();
	        int year = now.get(Calendar.YEAR);

	        if (timestamp > 0)
	            dbname=dbname+"cor";
	        CombinedWriter writer = new CombinedXMLWriter(recsPerbatch,
	                                                      loadNumber,
	                                                      dbname, "dev");

	        writer.setOperation(operation);

	        KnovelCombiner c = new KnovelCombiner(writer);
	        
	        /*TH added 09/21/2020 for ES lookup generation*/
	        Combiner.CURRENTDB = dbname;
	        for(String str: args)
	        {
	        	if(str.equalsIgnoreCase("lookup"))
	        		c.setAction("lookup");
	        }
	        
	        /*HT added 09/21/2020 to support ES lookup, will need to run lookup anyway even if action not lookup*/
       	// if(c.getAction() != null && c.getAction().equalsIgnoreCase("lookup"))
         	   c.writeLookupByWeekHook(loadNumber);
       	 
	        	 if (timestamp==0 && (loadNumber > 3000 || loadNumber < 1000) && (loadNumber >1))
	 	        {
	 	           c.writeCombinedByWeekNumber(url, driver, username, password, loadNumber);
	 	        }
	 	        else if(timestamp > 0)
	 	        {
	 	           c.writeCombinedByTimestamp(url, driver, username, password, timestamp);
	 	        }
	 	        else if(loadNumber == 1)
	 	        {
	 	        	c.writeCombinedByTable(url, driver, username, password);
	 	        }
	 	        else if(loadNumber == 0)
	 	        {
	 	            for(int yearIndex = 1900; yearIndex <= year+1; yearIndex++)
	 	            {
	 	              c = new KnovelCombiner(new CombinedXMLWriter(recsPerbatch, yearIndex,dbname, "dev"));
	 	              c.writeCombinedByYear(url, driver, username, password, yearIndex);
	 	            }
	 	        }
	 	        else
	 	        {
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

	    public KnovelCombiner(CombinedWriter writer)
	    {
	        super(writer);
	    }
	    
	    public KnovelCombiner(CombinedWriter writer, String propertyFileName)
	    {
	        super(writer);
	        this.propertyFileName=propertyFileName;
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
	            System.out.println("Running the query...");
	            String sqlQuery ="";
	            if(Combiner.CURRENTDB.equalsIgnoreCase("kno")&&(year==9999))
	            {
	            	sqlQuery = "select ACCESSNUMBER,DOC_TYPE,DOI,PII,OAI,ISBN,EISBN,LANGUAGE,TITLE,AUTHOR,AFFILIATION,M_ID,PUBLISHER,DOC_FORMAT,substr(PUBLISH_DATE,1,4) PUBLISH_DATE,PARENTID,JOURNAL_NAME,JOURNAL_SUBNAME,VOLUME,ISSUE,ABSTRACT,SUBJECT,TABLE_OF_CONTENT,LOADNUMBER,COPYRIGHT,DATABASE from "+tablename+" where (PUBLISH_DATE is not null and length(PUBLISH_DATE)>3) and loadnumber < 100000000 and doc_type!='journal-article' and database='" + Combiner.CURRENTDB + "'";
	                //rs = stmt.executeQuery("select ACCESSNUMBER,DOC_TYPE,DOI,PII,OAI,ISBN,EISBN,LANGUAGE,TITLE,AUTHOR,AFFILIATION,M_ID,PUBLISHER,DOC_FORMAT,PUBLISH_DATE,PARENTID,JOURNAL_NAME,JOURNAL_SUBNAME,VOLUME,ISSUE,ABSTRACT,SUBJECT,TABLE_OF_CONTENT,LOADNUMBER,COPYRIGHT where (PUBLISH_DATE is not null and length(PUBLISH_DATE)>3) and loadnumber < 100000000 and database='" + Combiner.CURRENTDB + "'");
	            }
	            else
	            {
	            	sqlQuery = "select ACCESSNUMBER,DOC_TYPE,DOI,PII,OAI,ISBN,EISBN,LANGUAGE,TITLE,AUTHOR,AFFILIATION,M_ID,PUBLISHER,DOC_FORMAT,substr(PUBLISH_DATE,1,4) PUBLISH_DATE,PARENTID,JOURNAL_NAME,JOURNAL_SUBNAME,VOLUME,ISSUE,ABSTRACT,SUBJECT,TABLE_OF_CONTENT,LOADNUMBER,COPYRIGHT,DATABASE from "+tablename+" where  substr(PUBLISH_DATE,1,4)='" + year + "' AND loadnumber != 0 and loadnumber < 100000000 and doc_type!='journal-article' and database='" + Combiner.CURRENTDB + "'";
	            	//rs = stmt.executeQuery("select ACCESSNUMBER,DOC_TYPE,DOI,PII,OAI,ISBN,EISBN,LANGUAGE,TITLE,AUTHOR,AFFILIATION,M_ID,PUBLISHER,DOC_FORMAT,PUBLISH_DATE,PARENTID,JOURNAL_NAME,JOURNAL_SUBNAME,VOLUME,ISSUE,ABSTRACT,SUBJECT,TABLE_OF_CONTENT,LOADNUMBER,COPYRIGHT where  substr(PUBLISH_DATE,1,4)='" + year + "' AND loadnumber != 0 and loadnumber < 100000000 and database='" + Combiner.CURRENTDB + "'");
	            }
	            System.out.println("SQL_QUERY= "+sqlQuery);
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
    
	public void writeCombinedByTableHook(Connection con)
	throws Exception
	{
		Statement stmt = null;
		ResultSet rs = null;
		try
		{
		
			stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			System.out.println("Running the query...");
			String sqlQuery = "select ACCESSNUMBER,DOC_TYPE,DOI,PII,OAI,ISBN,EISBN,LANGUAGE,TITLE,AUTHOR,AFFILIATION,M_ID,PUBLISHER,DOC_FORMAT,substr(PUBLISH_DATE,1,4) PUBLISH_DATE,PARENTID,JOURNAL_NAME,JOURNAL_SUBNAME,VOLUME,ISSUE,ABSTRACT,SUBJECT,TABLE_OF_CONTENT,LOADNUMBER,COPYRIGHT,DATABASE from "+tablename+" where doc_type!='journal-article' and database='" + Combiner.CURRENTDB + "'";
							   
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


	    public void writeRecs(ResultSet rs)
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
	        EVCombinedRec recSecondBox = null;
	        EVCombinedRec[] recArray = null;	      
	        String accessNumber = "";
	        long processTime = System.currentTimeMillis();
	    	int totalCount = getResultSetSize(rs);  
	    	System.out.println("epoch="+processTime+" database="+Combiner.CURRENTDB+" totalCount="+totalCount);
	    	//int MAX_THREAD = 110; 
	        //ExecutorService pool = Executors.newFixedThreadPool(MAX_THREAD); //use this line for ES extraction
	    	
	        try
	        {
	        	  if (this.propertyFileName != null && (getAction() == null || !(getAction().equalsIgnoreCase("lookup")))) // HT only create Kafka instance when it is // not lookup extraction
	        	{
	        		kafka = new KafkaService(processTime+"_"+Combiner.CURRENTDB+"_"+loadNumber, this.propertyFileName);//use this line for ES extraction
	        	}
	        	
		        while (rs.next())
		        {
		          ++i;
		          String firstGUID = "";	      
	
		          Vector recVector = new Vector();
		          
	              
		          try
		          {
		        	  EVCombinedRec rec = new EVCombinedRec();
		        	  String processInfo = processTime+"-"+totalCount+'-'+i+"-"+rs.getString("DATABASE")+"-"+rs.getString("LOADNUMBER")+"-NO_UPDATENUMBER";
		              rec.put(EVCombinedRec.PROCESS_INFO, processInfo);
		                
		                accessNumber = rs.getString("ACCESSNUMBER");
		                String parentID = rs.getString("PARENTID");
		                if (rs.getString("PUBLISH_DATE")!=null)
		                {		                		                    
		                    //M_ID
		                    rec.put(EVCombinedRec.DOCID, rs.getString("M_ID"));
		                    
		                    //DATABASE
		                    rec.put(EVCombinedRec.DATABASE, rs.getString("DATABASE"));
		                    
		                    //ACCESSNUMBER
		                    
		                    rec.put(EVCombinedRec.ACCESSION_NUMBER, rs.getString("ACCESSNUMBER"));
		                    
		                    
		                    //DOC_TYPE
		                    if(rs.getString("DOC_TYPE")!=null)
		                    {
		                    	//added for book project by hmo at 5/17/2017
		                    	String docType = rs.getString("DOC_TYPE");
		                    	if(docType.equalsIgnoreCase("book"))
		                    	{
		                    		docType = "bk";
		                    	}
		                    	else if(docType.equalsIgnoreCase("chapter"))
		                    	{
		                    		docType = "ch";
		                    	}
		                    	rec.put(EVCombinedRec.DOCTYPE,docType);
		                    }
		                    
		                    //DOI
		                    if(rs.getString("DOI")!=null)
		                    {
		                        rec.put(EVCombinedRec.DOI,rs.getString("DOI"));
		                    }
		                    
		                    //PII
		                    if(rs.getString("PII") != null)
		                    {
		                        rec.put(EVCombinedRec.PII,rs.getString("PII"));
		                    }
	
		                    //PUI
		                    if(rs.getString("OAI") != null)
		                    {
		                        rec.put(EVCombinedRec.PUI,rs.getString("OAI"));
		                    }
		                    
		                    //ISBN	
		                    String[] isbn = new String[2];
		                    isbn[0] = rs.getString("ISBN");
		                    isbn[1] = rs.getString("EISBN");
		                    if (rs.getString("ISBN")!= null)
		                    {
		                        rec.put(EVCombinedRec.ISBN, isbn);
		                    }
		                    
		                    //LANGUAGE
		                    String la = rs.getString("LANGUAGE");
		                    if (la != null)
		                    {
		                        rec.put(EVCombinedRec.LANGUAGE,Language.getIso639Language(la));
		                    }
		                    
		                    //TITLE
		                    if (rs.getString("TITLE") != null)
		                    {
		                       rec.put(EVCombinedRec.TITLE, rs.getString("TITLE"));
		                    }
		                    
		                    //AUTHOR
		                    if(rs.getString("AUTHOR") != null && rs.getString("AUTHOR").trim().length()>0)
		                    {
		                        String authorString = rs.getString("AUTHOR");	             	                       
		                        rec.put(EVCombinedRec.AUTHOR, prepareAuthor(authorString));
		                    }
		                    
		                    //AFFILIATION
	                        String affiliation = null;
	                        if (rs.getString("AFFILIATION") != null && rs.getString("AFFILIATION").trim().length()>0)
	                        {
	                            affiliation = rs.getString("AFFILIATION");                            
	                            rec.put(EVCombinedRec.AUTHOR_AFFILIATION, prepareAuthor(affiliation));
	                        }	                   
		                  
		                    //PUBLISHER
	                        if (rs.getString("PUBLISHER") != null)
		                    {
		                        rec.put(EVCombinedRec.PUBLISHER_NAME, rs.getString("PUBLISHER"));
		                    }
	                        
	                        //PARENTID
	                        if (rs.getString("PARENTID") != null)
		                    {
	                        	parentID = rs.getString("PARENTID");
	                        	//parentID = parentID.substring(parentID.lastIndexOf(":")+1);
	                        	//System.out.println("parentID="+parentID);
		                        rec.put(EVCombinedRec.PARENT_ID, parentID);
		                    }
	                        
		                    //JOURNAL_NAME
		                    if (rs.getString("JOURNAL_NAME") != null)
		                    {
		                    	//System.out.println("JOURNAL_NAME="+rs.getString("JOURNAL_NAME"));
		                        rec.put(EVCombinedRec.SERIAL_TITLE, rs.getString("JOURNAL_NAME"));
		                    }
	
		                    //JOURNAL_SUBNAME
		                    if (rs.getString("JOURNAL_SUBNAME") != null)
		                    {
		                    	//System.out.println("JOURNAL_SUBNAME="+rs.getString("JOURNAL_SUBNAME"));
		                        rec.put(EVCombinedRec.SECONDARY_SRC_TITLE, rs.getString("JOURNAL_SUBNAME"));
		                    }
		                    
		                    //VOLUME
		                    rec.put(EVCombinedRec.VOLUME, rs.getString("VOLUME"));
		                    //System.out.println("VOLUME= "+rs.getString("VOLUME"));
		                 
		                    //ISSUE
		                    rec.put(EVCombinedRec.ISSUE, rs.getString("ISSUE"));
		                    //System.out.println("ISSUE= "+rs.getString("ISSUE"));
		                    
		                    //ABSTRACT
		                    String abString = getStringFromClob(rs.getClob("ABSTRACT"));
		                    if (abString != null)
		                    {
		                        rec.put(EVCombinedRec.ABSTRACT, abString);
		                    }
	
		                    //SUBJECT
		                    if (rs.getString("SUBJECT") != null)
		                    {
		                        rec.put(EVCombinedRec.CONTROLLED_TERMS, prepareSubject(rs.getString("SUBJECT")));
		                    }
		                    
		                    //TABLE_OF_CONTENT
		                    if (rs.getString("TABLE_OF_CONTENT") != null)
		                    {
		                        rec.put(EVCombinedRec.TABLE_OF_CONTENT, prepareTableOfContent(getStringFromClob(rs.getClob("TABLE_OF_CONTENT"))));
		                    }
		                   
		                    rec.put(EVCombinedRec.LOAD_NUMBER, rs.getString("LOADNUMBER"));
	
		                    if (rs.getString("PUBLISH_DATE") != null)
		                    {
		                        rec.put(EVCombinedRec.PUB_YEAR, rs.getString("PUBLISH_DATE"));
		                    }
		                   	                  
		                    if(rs.getString("COPYRIGHT") != null)
		                    {
		                        rec.put(EVCombinedRec.COPYRIGHT,rs.getString("COPYRIGHT"));
		                    }
		                    recVector.add(rec);	                  	                 	                     	                	                
		            }
		            else
		            {
		            	System.out.println("Problem with publication_date, accessnumber="+accessNumber);
		            }
		               
		            
		                
		            recArray = (EVCombinedRec[])recVector.toArray(new EVCombinedRec[0]);
		            
		            if (this.propertyFileName == null && (getAction() != null && !(getAction().equalsIgnoreCase("lookup"))))
		            {
		            	this.writer.writeRec(recArray);//use this line for fast extraction
		            }
		            /*HT added 09/21/2020 for ES lookup*/
		            else if (getAction() != null && getAction().equalsIgnoreCase("lookup"))
		            {
		            	this.lookupObj.writeLookupRecs(recArray);
		            }
		            else
		            {
			            /**********************************************************/
			            //following code used to test kafka by hmo@2020/01/30
			            //this.writer.writeRec(recArray,kafka);
			            /*********************************************************/
			            
			            //use this block of code for sending data to kafka
			            
			            this.writer.writeRec(recArray,kafka, batchData, missedData);
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
		         catch(Exception e)
		         {
		            System.out.println("**** ERROR Found on access number "+accessNumber+" *****");
		            e.printStackTrace();
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
	        	
	        	if (this.propertyFileName != null && (getAction() != null && !(getAction().equalsIgnoreCase("lookup")))) 
	        	{
		        	try
		        	{
		        		 kafka.runBatch(batchData,missedData);        		
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
	    
	    public String[] prepareAuthor(String bdAuthor)
	    {
	        if(bdAuthor != null && !bdAuthor.trim().equals(""))
	        {	     
	            String[] ausArray = bdAuthor.split(Constants.AUDELIMITER, -1);    
	            return ausArray;
	        }
	        return null;
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
	    
	    public String[] prepareSubject(String multiString)
	            throws Exception
	    {
	        String[] multiStringArray = multiString.split(Constants.AUDELIMITER,-1);
	        HashSet<String> subjectSet = new HashSet<String>();
	        for(int i=0;i<multiStringArray.length;i++)
	        {
	        	String[] subArray = multiStringArray[i].split("/");
	        	for(int j=0;j<subArray.length;j++ )
	        	{
	        		subjectSet.add(subArray[j]);
	        	}
	        }

	        return subjectSet.toArray(new String[0]);

	    }

	   
	    public String[]  prepareTableOfContent(String multiString) throws Exception
	    {
	    	String[] multiStringArray = multiString.split(Constants.AUDELIMITER,-1);
	    	String[] titles = new String[multiStringArray.length];
	    	for(int i=0;i<multiStringArray.length;i++)
	    	{
	    		String singleTOC = multiStringArray[i];
	    		String[] singleTOCArray = singleTOC.split(Constants.IDDELIMITER,-1);
	    		titles[i] = singleTOCArray[0];
	    		//System.out.println("TITLE="+singleTOCArray[0]);
	    		
	    	}
	    	
	    	return titles;
	    }

	    public String replaceNull(String sVal) {

	        if (sVal == null)
	            sVal = "";

	        return sVal;
	    }


	    public String[] getCvs(List list)
	    {

	        if(list != null && list.size() > 0)
	        {
	            return (String[]) list.toArray(new String[0]);

	        }
	        return null;
	    }

	    private String stripAsterics(String line)
	    {
	        line = perl.substitute("s/\\*+//gi", line);
	        return line;
	    }


	 public void writeCombinedByTimestampHook(Connection con, long timestamp) throws Exception
	 {
	            Statement stmt = null;
	            ResultSet rs = null;

	            try
	            {

	            	stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	                rs = stmt.executeQuery("select ACCESSNUMBER,DOC_TYPE,DOI,PII,OAI,ISBN,EISBN,LANGUAGE,TITLE,AUTHOR,AFFILIATION,M_ID,PUBLISHER,DOC_FORMAT,substr(PUBLISH_DATE,1,4) PUBLISH_DATE,PARENTID,JOURNAL_NAME,JOURNAL_SUBNAME,VOLUME,ISSUE,ABSTRACT,SUBJECT,TABLE_OF_CONTENT,LOADNUMBER,COPYRIGHT from "+tablename+" where loadnumber != 0 and loadnumber < 100000000 and doc_type!='journal-article' and database='" + Combiner.CURRENTDB + "'");
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
	    public void writeCombinedByWeekHook(Connection con,
	                                        int weekNumber)
	        throws Exception
	    {
	        Statement stmt = null;
	        ResultSet rs = null;

	        try
	        {
	        	stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	            String sqlString = "select ACCESSNUMBER,DOC_TYPE,DOI,PII,OAI,ISBN,EISBN,LANGUAGE,TITLE,AUTHOR,AFFILIATION,M_ID,PUBLISHER,DOC_FORMAT,substr(PUBLISH_DATE,1,4) PUBLISH_DATE,PARENTID,JOURNAL_NAME,JOURNAL_SUBNAME,VOLUME,ISSUE,ABSTRACT,SUBJECT,TABLE_OF_CONTENT,LOADNUMBER,COPYRIGHT,DATABASE from "+tablename+"  where  LOADNUMBER='" + weekNumber + "' AND loadnumber != 0 and loadnumber < 100000000 and doc_type!='journal-article' and database='" + Combiner.CURRENTDB + "'";
	            System.out.println("SQL="+sqlString);
	            rs = stmt.executeQuery(sqlString);
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
	    
	    /*HT added 09/21/2020, Get Lookup list from temp tables based on correction action, this method to support bdCorrectio, not for ES lookup extraction*/
	    public Map<String, List<String>> getESLookupData(int weekNumber, String actionType, String tableName,
				Connection sqlcon, String database) throws Exception {
		Statement stmt = null;
		ResultSet rs = null;
		Map<String, List<String>> results = null;
		  
		  try { 
				
		  Connection con = sqlcon;
		  stmt = con.createStatement();
			System.out.println("Running the query...");
			String sqlString=null;
			if(database.equalsIgnoreCase("knc") ||database.equalsIgnoreCase("kna")  && (actionType.equalsIgnoreCase("update")
					|| actionType.equalsIgnoreCase("backup")))
			{
				if(tableName !=null)
					sqlString = "select * from " + tableName;
				
				System.out.println("Processing "+sqlString);
				rs = stmt.executeQuery(sqlString);
				results = prepareLookupRecs(rs);
			}

		}
		catch(Exception e)
		{
			System.out.println("Exception on GeoRefCorrection.getLookupData "+e.getMessage());
			e.printStackTrace();
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

      return results;
		  }

		// HT added 09/21/2020 for ES lookup
		public Map<String, List<String>> prepareLookupRecs(ResultSet rs) throws Exception {

			EVCombinedRec[] recArray = null;
			String accessNumber = "";

			Map<String, List<String>> recs = new HashMap<>();
			List<String> authorList = new ArrayList<>();
			List<String> affiliationList = new ArrayList<>();
			List<String> serialTitleList = new ArrayList<>();
			List<String> controltermList = new ArrayList<>();
			List<String> publishernameList = new ArrayList<>();
			List<String> ipcList = new ArrayList<>();

			try {
				while (rs.next()) {
					try {

						Vector<EVCombinedRec> recVector = new Vector<>();

				        	  EVCombinedRec rec = new EVCombinedRec();

				                
				                accessNumber = rs.getString("ACCESSNUMBER");
				                if (rs.getString("PUBLISH_DATE")!=null)
				                {		                		                    
				                    //M_ID
				                    rec.put(EVCombinedRec.DOCID, rs.getString("M_ID"));
				                    
				                    //DATABASE
				                    rec.put(EVCombinedRec.DATABASE, rs.getString("DATABASE"));
				                    
				                    //ACCESSNUMBER
				                    
				                    rec.put(EVCombinedRec.ACCESSION_NUMBER, rs.getString("ACCESSNUMBER"));

				                    //PUI
				                    if(rs.getString("OAI") != null)
				                    {
				                        rec.put(EVCombinedRec.PUI,rs.getString("OAI"));
				                    }                 
				                    //AUTHOR
				                    if(rs.getString("AUTHOR") != null && rs.getString("AUTHOR").trim().length()>0)
				                    {
				                        String authorString = rs.getString("AUTHOR");	             	                       
				                        rec.put(EVCombinedRec.AUTHOR, prepareAuthor(authorString));
				                    }
				                    
				                    //AFFILIATION
			                        String affiliation = null;
			                        if (rs.getString("AFFILIATION") != null && rs.getString("AFFILIATION").trim().length()>0)
			                        {
			                            affiliation = rs.getString("AFFILIATION");                            
			                            rec.put(EVCombinedRec.AUTHOR_AFFILIATION, prepareAuthor(affiliation));
			                        }	                   
				                  
				                    //PUBLISHER
			                        if (rs.getString("PUBLISHER") != null)
				                    {
				                        rec.put(EVCombinedRec.PUBLISHER_NAME, rs.getString("PUBLISHER"));
				                    }
 
			                        //JOURNAL_NAME
				                    if (rs.getString("JOURNAL_NAME") != null)
				                    {
				                    	//System.out.println("JOURNAL_NAME="+rs.getString("JOURNAL_NAME"));
				                        rec.put(EVCombinedRec.SERIAL_TITLE, rs.getString("JOURNAL_NAME"));
				                    }							                  
				                    rec.put(EVCombinedRec.LOAD_NUMBER, rs.getString("LOADNUMBER"));
				                    recVector.add(rec);	                  	                 	                     	                	                
				            }
				            else
				            {
				            	System.out.println("Problem with publication_date, accessnumber="+accessNumber);
				            }				               				            				                
				            recArray = (EVCombinedRec[])recVector.toArray(new EVCombinedRec[0]);
				            this.lookupObj.setLookupRecs(recArray, authorList, affiliationList, serialTitleList, controltermList, publishernameList, ipcList);
					}
					catch (Exception e) {
						System.out.println("**** ERROR Found on preparinglookuprec for access number " + accessNumber + " *****");
						System.out.println(e.getMessage());
						e.printStackTrace();
					}

				           
				} // while

				recs.put("AUTHOR", authorList);
				recs.put("AFFILIATION", affiliationList);
				recs.put("CONTROLLEDTERM", controltermList);
				recs.put("PUBLISHERNAME", publishernameList);
				recs.put("SERIALTITLE", serialTitleList);
			} catch (Exception e) {
				System.out.println("Exception on GeoRefCorrection.setGRFRecs " + e.getMessage());
				e.printStackTrace();
			}

			return recs;
		}
	}
