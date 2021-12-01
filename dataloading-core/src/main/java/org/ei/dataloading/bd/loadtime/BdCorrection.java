package org.ei.dataloading.bd.loadtime;

import java.sql.*;
import java.util.*;
import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.MatchResult;
import org.dataloading.sharedsearch.SharedSearchSearchEntry;
import org.ei.common.CVSTermBuilder;
import org.ei.common.Constants;
import org.ei.common.upt.AssigneeFilter;
import org.ei.dataloading.*;
import org.ei.dataloading.upt.loadtime.UPTCombiner;
import org.ei.dataloading.ntis.loadtime.*;
import org.ei.dataloading.inspec.loadtime.*;
import org.ei.dataloading.encompasspat.loadtime.*;
import org.ei.dataloading.cbnb.loadtime.*;
import org.ei.common.*;
import org.ei.common.bd.*;
import org.ei.common.ntis.*;
import org.ei.domain.*;
import org.ei.query.base.*;
import org.ei.util.*;
import org.ei.connectionpool.*;

import java.text.*;
import java.io.*;
import java.lang.Process;
import java.util.regex.*;
import org.ei.util.kafka.*;
import org.ei.util.GUID;
import org.ei.xml.Entity;

public class BdCorrection
{
    Perl5Util perl = new Perl5Util();

    private static String tablename;

    private static String currentDb;

    private static HashMap issnARFix = new HashMap();

    private int intDbMask = 1;
    private static Connection con = null;
    static String url="jdbc:oracle:thin:@jupiter:1521:eidb1";
    static String driver="oracle.jdbc.driver.OracleDriver";
    static String username="ap_ev_search";
    static String password="";
    static String database;
    static String action;
    static String m_id;
    static int updateNumber=0;
    static boolean test = false;
    static String tempTable="bd_correction_temp";
    static String lookupTable="deleted_lookupIndex";
    static String backupTable="bd_temp_backup";
	static String referenceTable="bd_reference_temp";
	static String cafePuiListTempTable;				//HH added 06/09/2020
	static String authorLookupTempTable;
	static String affiliationLookupTempTable;
    static String sqlldrFileName="correctionFileLoader.sh";
    static String authorLookupIndexSqlldrFileName = "cafe_au_lookupindex.sh";
    static String AffLookupIndexSqlldrFileName = "cafe_af_lookupindex.sh";
    static String cafeFlag = null;
    
    //HH added 11/15/2021 to support adhoc deletion
    static String adhocDelTableName;
    
    public static final String AUDELIMITER = new String(new char[] {30});
    public static final String IDDELIMITER = new String(new char[] {31});
    public static final String GROUPDELIMITER = new String(new char[] {29});
    private static long startTime = System.currentTimeMillis();
    private static long endTime = System.currentTimeMillis();
    private static long midTime = System.currentTimeMillis();
    private static List<String> blockedIssnList;
    private static List<String> cittypeList;
    private static String fileName; 		//HH added 06/09/2020 for showing in exception message of temp tables cleanup
    private static String propertyFileName;
    
   
    /*HT added 09/21/2020 for ES lookup, add BdCorrection constructor to initiat XmlCombiner once instead of being initialized in many individual methods in this class*/
    private static String tableToBeTruncated = null;
    private static String fileToBeLoaded   = null;
    public BdCorrection()
    {
    }
    public static void main(String args[])
        throws Exception
    {
        
       
        String input;
        //String tableToBeTruncated = "bd_correction_temp,deleted_lookupIndex,bd_temp_backup";
        int iThisChar; // To read individual chars with System.in.read()

        try
        {
            do
            {
                System.out.println("do you want to run test mode");
                iThisChar = System.in.read();
                if(iThisChar!=121 && iThisChar!=110)
                {
                    System.out.println("please enter y or n");
                }
                else if(iThisChar==121)
                {
                    test = true;
                    Thread.currentThread().sleep(1000);
                }
                else if(iThisChar==110)
                {
                    test = false;
                    Thread.currentThread().sleep(1000);
                }
            }
            while(iThisChar!=121 && iThisChar!=110);

        }
        catch (IOException ioe)
        {
            System.out.println("IO error trying to read your input!");
            System.exit(1);
        }
       
        
        //change this parameter to get kafka properties file
        if(args.length>11)
        {           	
        	propertyFileName = args[11];
            System.out.println("using propertyFileName "+propertyFileName);
         }
         else
         {
             System.out.println("Does not have propertyFileName file");              
         }
        
      
        
        if(args.length>10)
        {
            if(args[6]!=null)
            {
                url = args[6];
            }
            if(args[7]!=null)
            {
                driver = args[7];
            }
            if(args[8]!=null)
            {
                username = args[8];
                System.out.println("username1= "+username);
            }
            if(args[9]!=null)
            {
                password = args[9];
                System.out.println("password1= "+password);
            }
            if(args[10]!=null)
            {
                sqlldrFileName = args[10];
                System.out.println("using sqlloaderfile "+sqlldrFileName);
            }
            else
            {
                System.out.println("Does not have sqlldr file");
                System.exit(1);
            }
            
           
                                 
        }

        if(args.length>6)
        {
            if(args[0]!=null)
            {
                fileToBeLoaded = args[0];
                System.out.println("file to be processed= "+fileToBeLoaded);
            }

            if(args[1]!=null)
            {
                tableToBeTruncated = args[1];
                String[] tableName = null;
                if(tableToBeTruncated.indexOf(",")>-1)
                {
                    tableName = tableToBeTruncated.split(",",-1);
                }
                else
                {
                    tableName = new String[1];
                    tableName[0] = tableToBeTruncated;
                }
                if(tableName.length>0)
                {
                        tempTable=tableName[0];
                }
                if(tableName.length>1)
                {
                        lookupTable=tableName[1];
                }
                if(tableName.length>2)
                {
                        backupTable=tableName[2];
                }
				if(tableName.length>3)
				{
				        referenceTable=tableName[3];
                }
				//HH 06/09/2020
				if(tableName.length >4)
				{
					cafePuiListTempTable = tableName[4];
				}
				if(tableName.length >5)
				{
					authorLookupTempTable = tableName[5];
				}
				if(tableName.length >6)
				{
					affiliationLookupTempTable = tableName[6];
				}
            }

            if(args[2]!=null)
            {
                database = args[2];
                System.out.println("database= "+database);
            }

            if(args[3]!=null && args[3].length()>0)
            {
                Pattern pattern = Pattern.compile("^\\d*$");
                Matcher matcher = pattern.matcher(args[3]);
                if (matcher.find())
                {
                    updateNumber = Integer.parseInt(args[3]);
                    System.out.println("updateNumber= "+updateNumber);
                }
                else
                {
                    System.out.println("did not find updateNumber or updateNumber has wrong format");
                    System.exit(1);
                }
            }

            if(args[4]!=null)
            {
                action = args[4];
                System.out.println("action= "+action);
            }
            else
            {
                System.out.println("Are we doing 'update' or 'delete'");
                System.exit(1);
            }

            if(args[5]!=null)
            {
                FastSearchControl.BASE_URL = args[5];
                System.out.println("FAST URL= "+FastSearchControl.BASE_URL);
            }
            else
            {
                System.out.println("Does not have FastSearch URL");
                System.exit(1);
            }


        }
        
        else
        {
            System.out.println("not enough parameters");
            System.exit(1);
        }
        
        if(action != null && action.equalsIgnoreCase("adhocextractdelete") && args.length >12)
        {
        	adhocDelTableName = args[12];
        	System.out.println("action: " + args[12]);
        	System.out.println("Delete records from ES matching M_Id in table " + adhocDelTableName);
        }
        else
        {
        	System.out.println("table name for adhocextractdelete is not provided, exit");
        	//System.exit(1);
        }
        
        /*HT Added 09/21/2020 Move all work below to startCorrection instead*/
        //tableToBeTruncated = "bd_correction_temp,deleted_lookupIndex,bd_temp_backup";
        BdCorrection bdc = new BdCorrection();
        bdc.startCorrection();
    }
    
    /*HT 09/21/2020 moved from main to here for readability*/
    public void startCorrection()
    {
    	CombinedXMLWriter writer = new CombinedXMLWriter(50000,
                updateNumber,
                database,
                "dev");

    	XmlCombiner xmlcomb = new XmlCombiner(writer,propertyFileName,database);
    	
    	 midTime = System.currentTimeMillis();
         endTime = System.currentTimeMillis();
         System.out.println("Time for finish reading input parameter"+(endTime-startTime)/1000.0+" seconds");
         System.out.println("total Time used "+(endTime-startTime)/1000.0+" seconds");
         try
         {

        	String[] tableNames = new String[3];
          	tableNames[0] = tempTable;
          	tableNames[1] = "BD_MASTER_ORIG";
          	tableNames[2] = backupTable;   
          	

        	/*HT added 09/21/2020 initialize lookup entry*/
    		xmlcomb.writeLookupByWeekHook(updateNumber);

          	
             con = getConnection(url,driver,username,password);
             if(action!=null && !(action.equalsIgnoreCase("extractupdate")||action.equals("extractdelete") ||
            		 action.equalsIgnoreCase("lookupIndex")||action.equalsIgnoreCase("extractnumerical")||
            		 action.equalsIgnoreCase("extractauthorlookupindex")||action.equalsIgnoreCase("extractcafe")||
            		 action.equalsIgnoreCase("extractcafeloadnumber")||action.equalsIgnoreCase("extractcafemapping")||
            		 action.equalsIgnoreCase("extractcafedelete")||action.equalsIgnoreCase("extractallupdate") || action.equalsIgnoreCase("adhocextractdelete")))
             {
                 /**********delete all data from temp table *************/

                 if(test)
                 {
                     System.out.println("about to truncate table "+tableToBeTruncated);
                     System.out.println("press enter to continue");
                     Thread.currentThread().sleep(500);
                     System.in.read();
                     Thread.currentThread().sleep(1000);
                 }


                 cleanUp(tableToBeTruncated);							

                 midTime = endTime;
                 endTime = System.currentTimeMillis();
                 System.out.println("time for truncate table "+(endTime-midTime)/1000.0+" seconds");
                 System.out.println("total time used "+(endTime-startTime)/1000.0+" seconds");
                 
                 /************** load data into temp table ****************/

                 if(test)
                 {
                     System.out.println("about to parse data file "+fileToBeLoaded);
                     System.out.println("press enter to continue");
                     System.in.read();
                     Thread.currentThread().sleep(1000);
                 }

                 BaseTableDriver c = new BaseTableDriver(updateNumber,database);
                 if(!action.equalsIgnoreCase("delete"))
                 {
                 	c.setBlockedIssnList(con);
                 	this.cittypeList = getCittypeList(con, this.database);
                 	c.setCittypeList(this.cittypeList);
                 }
                 c.writeBaseTableFile(fileToBeLoaded,con);
                 String dataFile=fileToBeLoaded+"."+updateNumber+".out";
                 File f = new File(dataFile);
                 if(!f.exists())
                 {
                     System.out.println("datafile: "+dataFile+" does not exists");
                     System.exit(1);
                 }

                 if(test)
                 {
                     System.out.println("sql loader file "+dataFile+" created;");
                     System.out.println("about to load data file "+dataFile);
                     System.out.println("press enter to continue");
                     System.in.read();
                     Thread.currentThread().sleep(1000);
                 }
				
				  Runtime r = Runtime.getRuntime();
				  
				  Process p = r.exec("./"+sqlldrFileName+" "+dataFile); 
				  int t = p.waitFor();
				

                 int tempTableCount = getTempTableCount();
                 int tempReferenceTableCount = getTempReferenceTableCount();
                 System.out.println(tempTableCount+" records was loaded into the temp table");
                 System.out.println(tempReferenceTableCount+" records was loaded into the reference table");
                 
                 midTime = endTime;
                 endTime = System.currentTimeMillis();
                 System.out.println("time for loading temp table "+(endTime-midTime)/1000.0+" seconds");
                 System.out.println("total time used "+(endTime-startTime)/1000.0+" seconds");
                 
                 if(tempTableCount>0)
                 {                 
                     if(test)
                     {
                         System.out.println("begin to update tables");
                         System.out.println("press enter to continue");
                         System.in.read();
                         Thread.currentThread().sleep(1000);
                     }
                     runCorrection(dataFile,updateNumber,database,action);
                 }
                 else
                 {
                     System.out.println("no record was loaded into the temp table");
                     System.exit(1);
                 }
   
                /* Block out all lookup index processing, Hanan will do from different place@10/02/2020
                 
                
                if(action.equalsIgnoreCase("update"))
                {
                    bdc.processLookupIndex(bdc.getLookupData("update"),bdc.getLookupData("backup"));
                }
                else if(action.equalsIgnoreCase("delete"))
                {
                    bdc.processLookupIndex(new HashMap(),bdc.getLookupData("backup"));
                }
                else if(action.equalsIgnoreCase("aip"))
                {
                    bdc.processLookupIndex(bdc.getLookupData("aip"),bdc.getLookupData("aipBackup"));
                }
               // else if(action.equalsIgnoreCase("cafedelete"))
               //{
               //    bdc.processLookupIndex(new HashMap(),bdc.getLookupData("backup"));
               // }
                
                midTime = endTime;
                endTime = System.currentTimeMillis();
                System.out.println("time for run lookupIndex table "+(endTime-midTime)/1000.0+" seconds");
                */

                 midTime = endTime;
                 endTime = System.currentTimeMillis();
                 System.out.println("time for run correction table "+(endTime-midTime)/1000.0+" seconds");
                 System.out.println("total time used "+(endTime-startTime)/1000.0+" seconds");
                 
                 if(test)
                 {
                     System.out.println("finished updating tables");
                     System.out.println("begin to process lookup index");
                     System.out.println("press enter to continue");
                     System.in.read();
                     Thread.currentThread().sleep(1000);
                 }
                 if(action.equalsIgnoreCase("update"))
                 {
                 	if(propertyFileName == null)
                 		processLookupIndex(getLookupData("update"),getLookupData("backup"));			// fast
                 	
                 	/*//HT added 09/21/2020 for es*/
                 	else
                 	{
                 		
                		processESLookupIndex(xmlcomb.getESLookupData(updateNumber,"update", tableNames,con, database),xmlcomb.getESLookupData(updateNumber, "backup", tableNames, con, database));			
                 		
                 	}
                 		
                 }
                 else if(action.equalsIgnoreCase("delete"))
                 {
                	 if(propertyFileName == null)
                		 processLookupIndex(new HashMap<String, List<String>>(),getLookupData("backup"));					// fast
                	 /*//HT added 09/21/2020 for es*/
                  	else
                  	{
                  		
            			
                  		processESLookupIndex(new HashMap<String, List<String>>(),xmlcomb.getESLookupData(updateNumber, "backup", tableNames, con, database));			
                  		
                  	}
                 }
                 else if(action.equalsIgnoreCase("aip"))
                 {
                	 if(propertyFileName == null)
                		 processLookupIndex(getLookupData("aip"),getLookupData("aipBackup"));							//fast
                	 /*//HT added 09/21/2020 for es*/
                   	else
                   	{
                   		processESLookupIndex(xmlcomb.getESLookupData(updateNumber,"aip", tableNames,con, database),xmlcomb.getESLookupData(updateNumber, "aipBackup", tableNames, con, database));			
                   		
                   	}
                 }
               
                 
                 midTime = endTime;
                 endTime = System.currentTimeMillis();
                 System.out.println("time for run lookupIndex table "+(endTime-midTime)/1000.0+" seconds");
                 System.out.println("total time used "+(endTime-startTime)/1000.0+" seconds");

             }

             if(action.equalsIgnoreCase("lookupIndex"))
             {            	              
                 if(test)
                 {
                     System.out.println("press enter to continue");
                     System.in.read();
                     Thread.currentThread().sleep(1000);
                 }
               
                 if(propertyFileName == null)
                	 writeIndexOnly(updateNumber,database);							//fast
                 else
                	 /*HT added 09/21/2020 for ES lookup*/
                	 writeESIndexOnly(updateNumber,database,xmlcomb);					
                 
                 
                 System.out.println("************************ "+database+" "+updateNumber+" lookup index is done. **********************");
                 midTime = endTime;
                 endTime = System.currentTimeMillis();
                 System.out.println("time for run lookup index along "+(endTime-midTime)/1000.0+" seconds");
                 System.out.println("total time used "+(endTime-startTime)/1000.0+" seconds");
             }
             else if(action.equalsIgnoreCase("extractupdate")||action.equalsIgnoreCase("extractdelete")||action.equalsIgnoreCase("extractnumerical")||
            		 action.equalsIgnoreCase("extractauthorlookupindex")||action.equalsIgnoreCase("extractcafe")||action.equalsIgnoreCase("extractcafeloadnumber")||
            		 action.equalsIgnoreCase("extractcafemapping")||action.equalsIgnoreCase("extractcafedelete")||action.equalsIgnoreCase("extractallupdate") ||action.equalsIgnoreCase("adhocextractdelete"))
             {

                 doFastExtract(updateNumber,database,action, xmlcomb);
                 System.out.println(database+" "+updateNumber+" fast extract is done.");
                 midTime = endTime;
                 endTime = System.currentTimeMillis();
                 System.out.println("time for run fast extract along "+(endTime-midTime)/1000.0+" seconds");
                 System.out.println("total time used "+(endTime-startTime)/1000.0+" seconds");
             }
             else
             {
                 System.out.println(database+" "+updateNumber+" correction is done.");
                 System.out.println("Please run this program again with parameter \"extractupdate\" or \"extractdelete\" to get fast extract file");
             }

         }
         catch(Exception e)
         {
        	 System.out.println("Exception starting BD correction?!");
        	 System.out.println("Reason:- " + e.getMessage());
        	 e.printStackTrace();
         }
         finally
         {
             if (con != null)
             {
                 try
                 {
                     con.close();
                 }
                 catch (Exception e)
                 {
                     e.printStackTrace();
                 }
             }
             System.out.println("total process time "+(System.currentTimeMillis()-startTime)/1000.0+" seconds");
         }

         System.exit(1);
    }
    
    public List getCittypeList(Connection con,String database)
    {
    	Statement stmt = null;
        ResultSet rs = null;
        List<String> cittypeList = new ArrayList<>();
        String cittype = null;
       
        try
        {
            stmt = con.createStatement();

            rs = stmt.executeQuery("select cittype,database from bd_master_cittype where database='"+database+"'");
            while (rs.next())
            {
                cittype = rs.getString("cittype");
                database = rs.getString("database");
                if(cittype != null)
                {
                	cittypeList.add(cittype);
                }
            }

        }
        catch(Exception e)
        {
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
       return cittypeList;
       
    }
    /**
     * 
     * @author TELEBH
     * @Date: 09/21/2020 
     * @Description: Generate Lookups for ES
     * @throws Exception
     */
    
	public void writeESIndexOnly(int updatenumber, String database, XmlCombiner c) throws Exception 
	{
		/* TH added 09/21/2020 for ES lookup generation */
		//CombinedXMLWriter writer = new CombinedXMLWriter(50000, updateNumber, database, "dev");
		c.setAction("lookup");
		c.writeLookupByWeekHook(updatenumber);
		
		if ((updatenumber > 3000 || updatenumber < 1000) && (updatenumber > 1)) {
			Combiner.TABLENAME = "BD_MASTER_ORIG";
			c.writeCombinedByWeekNumber(url, driver, username, password, updatenumber);
		} 
	
	}
    	
    public void writeIndexOnly(int updatenumber,String database)throws Exception
    {
    	CombinedXMLWriter writer = new CombinedXMLWriter(50000,updatenumber,database);
    	XmlCombiner c = new XmlCombiner(writer);
    	EVCombinedRec rec = new EVCombinedRec();
    	writer.init();
    	int curRecNum = 0;
    	Statement stmt = null;
        ResultSet rs = null;
        String sqlQuery = null;
        
    	System.out.println("Running the query...");
        
    	if(updatenumber==0)
    	{
    		sqlQuery ="select a.UPDATENUMBER,a.CHEMICALTERM,a.SPECIESTERM,a.REGIONALTERM,a.DATABASE,a.CITATIONLANGUAGE,a.CITATIONTITLE,a.CITTYPE,a.ABSTRACTDATA,a.PII,a.PUI,a.COPYRIGHT,a.M_ID,a.accessnumber,a.datesort,a.author,a.author_1,a.AFFILIATION,a.AFFILIATION_1,a.CORRESPONDENCEAFFILIATION,a.CODEN,a.ISSUE,a.CLASSIFICATIONCODE,a.CLASSIFICATIONDESC,a.CONTROLLEDTERM,a.UNCONTROLLEDTERM,a.MAINHEADING,a.TREATMENTCODE,a.LOADNUMBER,a.SOURCETYPE,a.SOURCECOUNTRY,a.SOURCEID,a.SOURCETITLE,a.SOURCETITLEABBREV,a.ISSUETITLE,a.ISSN,a.EISSN,a.ISBN,a.VOLUME,a.PAGE,a.PAGECOUNT,a.ARTICLENUMBER, substr(a.PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,a.PUBLICATIONDATE,a.EDITORS,a.PUBLISHERNAME,a.PUBLISHERADDRESS,a.PUBLISHERELECTRONICADDRESS,a.REPORTNUMBER,a.CONFNAME, a.CONFCATNUMBER,a.CONFCODE,a.CONFLOCATION,a.CONFDATE,a.CONFSPONSORS,a.CONFERENCEPARTNUMBER, a.CONFERENCEPAGERANGE, a.CONFERENCEPAGECOUNT, a.CONFERENCEEDITOR, a.CONFERENCEORGANIZATION,a.CONFERENCEEDITORADDRESS,a.TRANSLATEDSOURCETITLE,a.VOLUMETITLE,a.DOI,a.ASSIG,a.CASREGISTRYNUMBER,a.APILT,a.APILT1,a.APICT,a.APICT1,a.APIAMS,a.SEQ_NUM,a.GRANTLIST,b.author as cafe_author,b.author_1 as cafe_author1,b.affiliation as cafe_affiliation,b.affiliation_1 as cafe_affiliation1,b.CORRESPONDENCEAFFILIATION as CAFE_CORRESPONDENCEAFFILIATION,null as authorid,null as affid,a.SOURCEBIBTEXT,a.STANDARDID,a.STANDARDDESIGNATION,a.NORMSTANDARDID,a.GRANTTEXT,a.UPDATENUMBER, count(*) over() totalCount from bd_master_orig a left join cafe_master b on a.cafe_pui=b.pui where a.database='cpx'";
    	}
    	else
    	{
    		sqlQuery ="select a.UPDATENUMBER,a.CHEMICALTERM,a.SPECIESTERM,a.REGIONALTERM,a.DATABASE,a.CITATIONLANGUAGE,a.CITATIONTITLE,a.CITTYPE,a.ABSTRACTDATA,a.PII,a.PUI,a.COPYRIGHT,a.M_ID,a.accessnumber,a.datesort,a.author,a.author_1,a.AFFILIATION,a.AFFILIATION_1,a.CORRESPONDENCEAFFILIATION,a.CODEN,a.ISSUE,a.CLASSIFICATIONCODE,a.CLASSIFICATIONDESC,a.CONTROLLEDTERM,a.UNCONTROLLEDTERM,a.MAINHEADING,a.TREATMENTCODE,a.LOADNUMBER,a.SOURCETYPE,a.SOURCECOUNTRY,a.SOURCEID,a.SOURCETITLE,a.SOURCETITLEABBREV,a.ISSUETITLE,a.ISSN,a.EISSN,a.ISBN,a.VOLUME,a.PAGE,a.PAGECOUNT,a.ARTICLENUMBER, substr(a.PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,a.PUBLICATIONDATE,a.EDITORS,a.PUBLISHERNAME,a.PUBLISHERADDRESS,a.PUBLISHERELECTRONICADDRESS,a.REPORTNUMBER,a.CONFNAME, a.CONFCATNUMBER,a.CONFCODE,a.CONFLOCATION,a.CONFDATE,a.CONFSPONSORS,a.CONFERENCEPARTNUMBER, a.CONFERENCEPAGERANGE, a.CONFERENCEPAGECOUNT, a.CONFERENCEEDITOR, a.CONFERENCEORGANIZATION,a.CONFERENCEEDITORADDRESS,a.TRANSLATEDSOURCETITLE,a.VOLUMETITLE,a.DOI,a.ASSIG,a.CASREGISTRYNUMBER,a.APILT,a.APILT1,a.APICT,a.APICT1,a.APIAMS,a.SEQ_NUM,a.GRANTLIST,b.author as cafe_author,b.author_1 as cafe_author1,b.affiliation as cafe_affiliation,b.affiliation_1 as cafe_affiliation1,b.CORRESPONDENCEAFFILIATION as CAFE_CORRESPONDENCEAFFILIATION,null as authorid,null as affid,a.SOURCEBIBTEXT,a.STANDARDID,a.STANDARDDESIGNATION,a.NORMSTANDARDID,a.GRANTTEXT,a.UPDATENUMBER, count(*) over() totalCount from bd_master_orig a left join cafe_master b on a.cafe_pui=b.pui where a.database='cpx' and a.updatenumber='"+updateNumber+"'";
    	}
       
        System.out.println("sqlQuery="+sqlQuery);
        stmt = con.createStatement();
        rs = stmt.executeQuery(sqlQuery);
        while (rs.next())
        {
        	String affiliation = null;
        	rec.put(EVCombinedRec.DATABASE, rs.getString("database"));
        	rec.put(EVCombinedRec.LOAD_NUMBER, rs.getString("loadnumber"));
        	rec.put(EVCombinedRec.PUI, rs.getString("pui"));
        	rec.put(EVCombinedRec.ACCESSION_NUMBER, rs.getString("accessnumber"));
        	String authorString = rs.getString("AUTHOR");
            if(rs.getString("AUTHOR_1") !=null)
            {
                authorString=authorString+rs.getString("AUTHOR_1");
            }
            
            if(authorString!=null)
            {
            	rec.put(EVCombinedRec.AUTHOR, c.prepareBdAuthor(authorString));
            }
            
            if(rs.getString("authorid")==null && rs.getString("cafe_author")!=null)
            {
            	String cafeAuthorString = rs.getString("cafe_author");
            	if(rs.getString("cafe_author1")!=null)
            	{
            		cafeAuthorString = cafeAuthorString+rs.getString("cafe_author1");
            	}
            	String[] cafeAuthor = c.prepareBdAuthor(cafeAuthorString);
                String[] cafe_aid = c.getAuID();	                        
                rec.put(EVCombinedRec.AUTHORID, cafe_aid);
            }
            
        	 if (rs.getString("AFFILIATION") != null)
             {
                 affiliation = rs.getString("AFFILIATION");
                 if(rs.getString("AFFILIATION_1")!=null)
                 {
                     affiliation = affiliation+rs.getString("AFFILIATION_1");
                 }
                              
                 BdAffiliations aff = new BdAffiliations(affiliation);
                 rec.put(EVCombinedRec.AUTHOR_AFFILIATION,  aff.getSearchValue());
                 rec.put(EVCombinedRec.AFFILIATION_LOCATION,  aff.getLocationsSearchValue());
                 rec.put(EVCombinedRec.COUNTRY,  aff.getCountriesSearchValue());
               
             }
             else if(rs.getString("AFFILIATION_1")!=null)
             {
                 affiliation = rs.getString("AFFILIATION_1");
                 BdAffiliations aff = new BdAffiliations(affiliation);
                 rec.put(EVCombinedRec.AUTHOR_AFFILIATION,  aff.getSearchValue());
                 rec.put(EVCombinedRec.AFFILIATION_LOCATION,  aff.getLocationsSearchValue());
                 rec.put(EVCombinedRec.COUNTRY,  aff.getCountriesSearchValue());
                 rec.put(EVCombinedRec.AFFILIATIONID,  aff.getAffiliationId());
                 rec.put(EVCombinedRec.DEPARTMENTID,  aff.getDepartmentId());
                 if(rs.getString("affid")==null && rs.getString("CAFE_AFFILIATION1")!=null)
                 {
                 	String cafeAffString = rs.getString("CAFE_AFFILIATION1");                           	
                 	BdAffiliations caff = new BdAffiliations(cafeAffString);
                 	caff.getSearchValue();   
                 	rec.put(EVCombinedRec.AFFILIATIONID,  caff.getAffiliationId());
                 	rec.put(EVCombinedRec.DEPARTMENTID,  caff.getDepartmentId());
                 }

             }
             else if(rs.getString("CORRESPONDENCEAFFILIATION") != null)
             {
                 affiliation = rs.getString("CORRESPONDENCEAFFILIATION");
                 BdCorrespAffiliations aff = new BdCorrespAffiliations(affiliation);
                 rec.put(EVCombinedRec.AUTHOR_AFFILIATION,  aff.getSearchValue());
                 rec.put(EVCombinedRec.AFFILIATION_LOCATION,  aff.getLocationsSearchValue());
                 rec.put(EVCombinedRec.COUNTRY,  aff.getCountriesSearchValue());
                 rec.put(EVCombinedRec.AFFILIATIONID,  aff.getAffiliationId());
                 rec.put(EVCombinedRec.DEPARTMENTID,  aff.getDepartmentId());
                 if(rs.getString("CAFE_CORRESPONDENCEAFFILIATION")!=null)
                 {
                 	String cafeAffString = rs.getString("CAFE_CORRESPONDENCEAFFILIATION");                           	
                 	BdAffiliations caff = new BdAffiliations(cafeAffString);
                 	caff.getSearchValue();   
                 	rec.put(EVCombinedRec.AFFILIATIONID,  caff.getAffiliationId());
                 	rec.put(EVCombinedRec.DEPARTMENTID,  caff.getDepartmentId());
                 }
             }
        	 
        	 if (rs.getString("CONTROLLEDTERM") != null)
             {
                 rec.put(EVCombinedRec.CONTROLLED_TERMS, c.prepareMulti(rs.getString("CONTROLLEDTERM")));
             }
        	 
        	 if(rs.getString("CHEMICALTERM") != null)
             {
                 rec.put(EVCombinedRec.CHEMICALTERMS, c.prepareMulti(rs.getString("CHEMICALTERM")));
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
             
             if (rs.getString("PUBLISHERNAME") != null)
             {
                 rec.put(EVCombinedRec.PUBLISHER_NAME, c.preparePublisherName(rs.getString("PUBLISHERNAME")));
             }

        	writer.setDatabase(rec.getString(EVCombinedRec.DATABASE));
        	writer.setPui(rec.getString(EVCombinedRec.PUI));
        	writer.setAccessnumber(rec.getString(EVCombinedRec.ACCESSION_NUMBER));
        	writer.setLoadnumber(rec.getString(EVCombinedRec.LOAD_NUMBER));
        	if(rec.getStrings(EVCombinedRec.AUTHOR)!=null && (rec.getStrings(EVCombinedRec.AUTHOR))[0]!=null)
	        {
        		writer.addIndex(rec.getStrings(EVCombinedRec.AUTHOR),"AUTHOR"); //AUTHOR
	        }
	        
	        if(rec.getString(EVCombinedRec.AUTHOR_AFFILIATION)!=null && (rec.getStrings(EVCombinedRec.AUTHOR_AFFILIATION))[0]!=null)
	        {
	        	writer.addIndex(rec.getStrings(EVCombinedRec.AUTHOR_AFFILIATION),"AUTHORAFFILIATION");//AUTHOR_AFFILIATION
	        }
	        else if(rec.getString(EVCombinedRec.AFFILIATION_LOCATION)!=null && (rec.getStrings(EVCombinedRec.AFFILIATION_LOCATION))[0]!=null)
	        {
	        	writer.addIndex(rec.getStrings(EVCombinedRec.AFFILIATION_LOCATION),"AUTHORAFFILIATION");//AUTHOR_AFFILIATION
	        }
	
	        if(rec.getString(EVCombinedRec.CONTROLLED_TERMS)!=null && (rec.getStrings(EVCombinedRec.CONTROLLED_TERMS))[0]!=null)
	        {
	        	writer.addIndex(rec.getString(EVCombinedRec.CONTROLLED_TERMS),"CONTROLLEDTERMS");//CONTROLLEDTERMS
	        }
	        else if(rec.getString(EVCombinedRec.CHEMICALTERMS)!=null && (rec.getStrings(EVCombinedRec.CHEMICALTERMS))[0]!=null)
	        {
	        	writer.addIndex(rec.getString(EVCombinedRec.CHEMICALTERMS),"CONTROLLEDTERMS");//CHEMICALTERMS
	        }
	        
	        if(rec.getString(EVCombinedRec.SERIAL_TITLE)!=null && (rec.getStrings(EVCombinedRec.SERIAL_TITLE))[0]!=null)
	        {
	        	writer.addIndex(rec.getString(EVCombinedRec.SERIAL_TITLE),"SERIALTITLE");//SERIALTITLE
	        }
	        
	        if(rec.getString(EVCombinedRec.PUBLISHER_NAME)!=null && (rec.getStrings(EVCombinedRec.PUBLISHER_NAME))[0]!=null)
	        {
	        	writer.addIndex(rec.getString(EVCombinedRec.PUBLISHER_NAME),"PUBLISHERNAME");//PUBLISHERNAME
	        }
	        
	       // writer.addIndex(rec.getString(EVCombinedRec.INT_PATENT_CLASSIFICATION),"INTERNATONALPATENTCLASSIFICATION");//IPC CODE
	
	        ++curRecNum;
        }
        writer.end();

    }
    
    private void updateAuthorAffiliationProfile(Connection con1) throws Exception
    {
    	  Statement stmt = null;
          ResultSet rs = null;
          PreparedStatement pstmt=null;
          try
          {
        	  //set as cpx record 
	          stmt = con.createStatement();                         
	          stmt.executeUpdate("update author_profile set database='cpx' where authorid in (select author_id from cafe_au_lookup_temp)");
	          
	          //do update when the author id is in the cafe_au_lookup
	          rs = stmt.executeQuery("select * from cafe_au_lookup_temp where author_id in(select author_id from cafe_au_lookup)");
			  String sql = "update cafe_au_lookup set display_name=?,au_index_name=?,id=?,dbase=? where author_id=?";
			  con.setAutoCommit(false);
			  pstmt = con.prepareStatement(sql);
			  int batchSize = 1000;
			  int count = 0;
			  
			  while (rs.next())
	          {				 
				  pstmt.setString(1,rs.getString("display_name"));
				  pstmt.setString(2,rs.getString("au_index_name"));
				  pstmt.setString(3,rs.getString("id"));
				  pstmt.setString(4,rs.getString("dbase"));				
				  pstmt.setString(5,rs.getString("author_id"));
				  pstmt.addBatch();
				  if(++count % batchSize == 0) 
				  {
					  pstmt.executeBatch();
				  }
	          }
			  pstmt.executeBatch();
			  con.commit();
			  con.setAutoCommit(true);
			  //insert as new if author id is not in the cafe_au_lookup table.
			  stmt.executeUpdate("insert into cafe_au_lookup select * from cafe_au_lookup_temp where author_id not in(select author_id from cafe_au_lookup)");
			  
			  //update affiliation profile
			  stmt.executeUpdate("update institute_profile set database='cpx' where affid in (select INSTITUTE_ID from cafe_af_lookup_temp)");
	          
	          //do update when the affiliation id is in the cafe_af_lookup
	          rs = stmt.executeQuery("select * from cafe_af_lookup_temp where institute_id in(select institute_id from cafe_af_lookup)");
			  sql = "update cafe_af_lookup set institute_name=?,id=?,dbase=? where institute_id=?";
			  con.setAutoCommit(false);
			  pstmt = con.prepareStatement(sql);			  
			  count = 0;
			  
			  while (rs.next())
	          {
				  
				  pstmt.setString(1,rs.getString("institute_name"));
				  pstmt.setString(2,rs.getString("id"));
				  pstmt.setString(3,rs.getString("dbase"));				
				  pstmt.setString(4,rs.getString("institute_id"));
				  pstmt.addBatch();
				  if(++count % batchSize == 0) 
				  {
					  pstmt.executeBatch();
				  }
	          }
			  pstmt.executeBatch();
			  con.commit();
			  con.setAutoCommit(true);
			 
			  //insert as new if author id is not in the cafe_au_lookup table.
			  stmt.executeUpdate("insert into cafe_af_lookup select * from cafe_af_lookup_temp where institute_id not in(select institute_id from cafe_af_lookup)");			  			
			  
          }
          catch(Exception e)
          {
        	  e.printStackTrace();
          }            
          finally
          {
              if(pstmt != null)
              {
                  try
                  {
                      pstmt.close();
                  }
                  catch(Exception se)
                  {
                  }
              }
              if(stmt != null)
              {
                  try
                  {
                      stmt.close();
                  }
                  catch(Exception se)
                  {
                  }
              }
          }          
    }
    
    private void loadLookupIndexFile(String sqlldrFileName, String dataFileName) throws Exception
    {
    	
         Runtime r = Runtime.getRuntime();

         Process p = r.exec("./"+sqlldrFileName+" "+dataFileName);
         int t = p.waitFor();
    }

    private void outputLookupIndex(HashMap lookupData, int updateNumber)
    {
    	String pui = null;
    	if(lookupData.get("PUI")!=null)
        {
             pui = (String)lookupData.get("PUI");
        }
    	
        if(lookupData.get("AUTHOR")!=null)
        {       	
            writeToFile((ArrayList)lookupData.get("AUTHOR"),"AUTHOR",updateNumber,pui);
        }

        if(lookupData.get("AFFILIATION")!=null)
        {        	
            writeToFile((ArrayList)lookupData.get("AFFILIATION"),"AFFILIATION",updateNumber,pui);
        }

        if(lookupData.get("CONTROLLEDTERM")!=null)
        {       
            writeToFile((ArrayList)lookupData.get("CONTROLLEDTERM"),"CONTROLLEDTERM",updateNumber,pui);
        }

        if(lookupData.get("PUBLISHERNAME")!=null)
        {        	
            writeToFile((ArrayList)lookupData.get("PUBLISHERNAME"),"PUBLISHERNAME",updateNumber,pui);
        }

        if(lookupData.get("SERIALTITLE")!=null)
        {
            writeToFile((ArrayList)lookupData.get("SERIALTITLE"),"SERIALTITLE",updateNumber,pui);
        }

    }



    private void writeToFile(List data, String field, int updateNumber,String pui)
    {
    	String fileName = "lookupIndexfile.txt";
    	if("AUTHOR".equals(field))
    	{
    		fileName = "./ei/index_au/"+field.toLowerCase()+"-"+updateNumber+"."+database;
    	}
    	else if("AFFILIATION".equals(field))
    	{
    		fileName = "./ei/index_af/"+field.toLowerCase()+"-"+updateNumber+"."+database;
    	}
    	else if("CONTROLLEDTERM".equals(field))
    	{
    		fileName = "./ei/index_cv/"+field.toLowerCase()+"-"+updateNumber+"."+database;
    	}
    	else if("PUBLISHERNAME".equals(field))
    	{
    		fileName = "./ei/index_pn/"+field.toLowerCase()+"-"+updateNumber+"."+database;
    	}
    	else if("SERIALTITLE".equals(field))
    	{
    		fileName = "./ei/index_st/"+field.toLowerCase()+"-"+updateNumber+"."+database;
    	}	
         		
        FileWriter out;

        File file=new File("lookupindex/"+database);


        try
        {
            if(!file.exists())
            {
                file.mkdir();
            }
            String aid = "";
            String oo = null;


            out = new FileWriter(fileName);
            //System.out.println("field==> "+field);
            if(data != null)
            {
                       	
                for(int i=0;i<data.size();i++)
                {
                	String ss = (String)data.get(i);
                	if(ss!=null && ss.indexOf(Constants.GROUPDELIMITER)>-1)
                	{
                		int dd = ss.indexOf(Constants.GROUPDELIMITER);
                		aid = ss.substring(0,dd);
                		oo = ss.substring(dd+1);
                		
                	}
                	else
                	{
                		oo = ss;               		
                	}
                    
                    if(oo!=null && !oo.equals("null") && database!=null)
                    {
                    	out.write(Entity.prepareString(oo).toUpperCase().trim() + "\t" + database + "\t" + aid+"\n");
                    }
                   
                }
            }
            out.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

    }

    private void doFastExtract(int updateNumber,String dbname,String action, XmlCombiner c) throws Exception
    {
        CombinedXMLWriter writer = new CombinedXMLWriter(50000,
                                                      updateNumber,
                                                      dbname,
                                                      "dev");

        Statement stmt = null;
        ResultSet rs = null;
        //XmlCombiner c = new XmlCombiner(writer,propertyFileName, database);					//HT commented 9/21/2020 since XmlCombiner initialized in startCorrection already
        try
        {
        	/*HT added 09/21/2020 initialize lookup entry*/
			//c.writeLookupByWeekHook(updateNumber);		
			
			c.setCittypeList(this.cittypeList);
        	stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            if(action.equalsIgnoreCase("update") || action.equalsIgnoreCase("extractupdate") || action.equalsIgnoreCase("aip") )
            {
                System.out.println("Running the query...");
                writer.setOperation("add");
                
                if(dbname.equals("cpx"))
                {
                	if(updateNumber==1)
                	{
                		System.out.println("extracting entire bd_master_orig table");
                		//blocked ISOPENACESS
                		//String sqlQuery = "select a.EID,a.CHEMICALTERM,a.SPECIESTERM,a.REGIONALTERM,a.DATABASE,a.CITATIONLANGUAGE,a.CITATIONTITLE,a.CITTYPE,a.ABSTRACTDATA,a.PII,a.PUI,a.COPYRIGHT,a.M_ID,a.accessnumber,a.datesort,a.author,a.author_1,a.AFFILIATION,a.AFFILIATION_1,a.CORRESPONDENCEAFFILIATION,a.CODEN,a.ISSUE,a.CLASSIFICATIONCODE,a.CLASSIFICATIONDESC,a.CONTROLLEDTERM,a.UNCONTROLLEDTERM,a.MAINHEADING,a.TREATMENTCODE,a.LOADNUMBER,a.SOURCETYPE,a.SOURCECOUNTRY,a.SOURCEID,a.SOURCETITLE,a.SOURCETITLEABBREV,a.ISSUETITLE,a.ISSN,a.EISSN,a.ISBN,a.VOLUME,a.PAGE,a.PAGECOUNT,a.ARTICLENUMBER, substr(a.PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,a.PUBLICATIONDATE,a.EDITORS,a.PUBLISHERNAME,a.PUBLISHERADDRESS,a.PUBLISHERELECTRONICADDRESS,a.REPORTNUMBER,a.CONFNAME, a.CONFCATNUMBER,a.CONFCODE,a.CONFLOCATION,a.CONFDATE,a.CONFSPONSORS,a.CONFERENCEPARTNUMBER, a.CONFERENCEPAGERANGE, a.CONFERENCEPAGECOUNT, a.CONFERENCEEDITOR, a.CONFERENCEORGANIZATION,a.CONFERENCEEDITORADDRESS,a.TRANSLATEDSOURCETITLE,a.VOLUMETITLE,a.DOI,a.ASSIG,a.CASREGISTRYNUMBER,a.APILT,a.APILT1,a.APICT,a.APICT1,a.APIAMS,a.SEQ_NUM,a.GRANTLIST,b.author as cafe_author,b.author_1 as cafe_author1,b.affiliation as cafe_affiliation,b.affiliation_1 as cafe_affiliation1,b.CORRESPONDENCEAFFILIATION as CAFE_CORRESPONDENCEAFFILIATION,null as authorid,null as affid,a.SOURCEBIBTEXT,a.STANDARDID,a.STANDARDDESIGNATION,a.NORMSTANDARDID,a.GRANTTEXT,a.ISOPENACESS from bd_master_orig a left outer join cafe_master b on a.cafe_pui = b.pui where a.database='cpx'";
                		String sqlQuery = "select a.UPDATENUMBER,a.EID,a.CHEMICALTERM,a.SPECIESTERM,a.REGIONALTERM,a.DATABASE,a.CITATIONLANGUAGE,a.CITATIONTITLE,a.CITTYPE,a.ABSTRACTDATA,a.PII,a.PUI,a.COPYRIGHT,a.M_ID,a.accessnumber,a.datesort,a.author,a.author_1,a.AFFILIATION,a.AFFILIATION_1,a.CORRESPONDENCEAFFILIATION,a.CODEN,a.ISSUE,a.CLASSIFICATIONCODE,a.CLASSIFICATIONDESC,a.CONTROLLEDTERM,a.UNCONTROLLEDTERM,a.MAINHEADING,a.TREATMENTCODE,a.LOADNUMBER,a.SOURCETYPE,a.SOURCECOUNTRY,a.SOURCEID,a.SOURCETITLE,a.SOURCETITLEABBREV,a.ISSUETITLE,a.ISSN,a.EISSN,a.ISBN,a.VOLUME,a.PAGE,a.PAGECOUNT,a.ARTICLENUMBER, substr(a.PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,a.PUBLICATIONDATE,a.EDITORS,a.PUBLISHERNAME,a.PUBLISHERADDRESS,a.PUBLISHERELECTRONICADDRESS,a.REPORTNUMBER,a.CONFNAME, a.CONFCATNUMBER,a.CONFCODE,a.CONFLOCATION,a.CONFDATE,a.CONFSPONSORS,a.CONFERENCEPARTNUMBER, a.CONFERENCEPAGERANGE, a.CONFERENCEPAGECOUNT, a.CONFERENCEEDITOR, a.CONFERENCEORGANIZATION,a.CONFERENCEEDITORADDRESS,a.TRANSLATEDSOURCETITLE,a.VOLUMETITLE,a.DOI,a.ASSIG,a.CASREGISTRYNUMBER,a.APILT,a.APILT1,a.APICT,a.APICT1,a.APIAMS,a.SEQ_NUM,a.GRANTLIST,b.author as cafe_author,b.author_1 as cafe_author1,b.affiliation as cafe_affiliation,b.affiliation_1 as cafe_affiliation1,b.CORRESPONDENCEAFFILIATION as CAFE_CORRESPONDENCEAFFILIATION,null as authorid,null as affid,a.SOURCEBIBTEXT,a.STANDARDID,a.STANDARDDESIGNATION,a.NORMSTANDARDID,a.GRANTTEXT,a.ISOPENACESS, count(*) over() totalCount from bd_master_orig a left outer join CAFE_PUI_LIST_MASTER c on a.pui= c.puisecondary left outer join cafe_master b on b.pui=c.pui where  a.database='cpx' and a.publicationyear is not null"; 
                		//String sqlQuery = "select a.EID,a.CHEMICALTERM,a.SPECIESTERM,a.REGIONALTERM,a.DATABASE,a.CITATIONLANGUAGE,a.CITATIONTITLE,a.CITTYPE,a.ABSTRACTDATA,a.PII,a.PUI,a.COPYRIGHT,a.M_ID,a.accessnumber,a.datesort,a.author,a.author_1,a.AFFILIATION,a.AFFILIATION_1,a.CORRESPONDENCEAFFILIATION,a.CODEN,a.ISSUE,a.CLASSIFICATIONCODE,a.CLASSIFICATIONDESC,a.CONTROLLEDTERM,a.UNCONTROLLEDTERM,a.MAINHEADING,a.TREATMENTCODE,a.LOADNUMBER,a.SOURCETYPE,a.SOURCECOUNTRY,a.SOURCEID,a.SOURCETITLE,a.SOURCETITLEABBREV,a.ISSUETITLE,a.ISSN,a.EISSN,a.ISBN,a.VOLUME,a.PAGE,a.PAGECOUNT,a.ARTICLENUMBER, substr(a.PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,a.PUBLICATIONDATE,a.EDITORS,a.PUBLISHERNAME,a.PUBLISHERADDRESS,a.PUBLISHERELECTRONICADDRESS,a.REPORTNUMBER,a.CONFNAME, a.CONFCATNUMBER,a.CONFCODE,a.CONFLOCATION,a.CONFDATE,a.CONFSPONSORS,a.CONFERENCEPARTNUMBER, a.CONFERENCEPAGERANGE, a.CONFERENCEPAGECOUNT, a.CONFERENCEEDITOR, a.CONFERENCEORGANIZATION,a.CONFERENCEEDITORADDRESS,a.TRANSLATEDSOURCETITLE,a.VOLUMETITLE,a.DOI,a.ASSIG,a.CASREGISTRYNUMBER,a.APILT,a.APILT1,a.APICT,a.APICT1,a.APIAMS,a.SEQ_NUM,a.GRANTLIST,b.author as cafe_author,b.author_1 as cafe_author1,b.affiliation as cafe_affiliation,b.affiliation_1 as cafe_affiliation1,b.CORRESPONDENCEAFFILIATION as CAFE_CORRESPONDENCEAFFILIATION,null as authorid,null as affid,a.SOURCEBIBTEXT,a.STANDARDID,a.STANDARDDESIGNATION,a.NORMSTANDARDID,a.GRANTTEXT from bd_master_orig a left outer join cafe_master b on a.cafe_pui = b.pui where a.database='cpx'";
                		//use accessnumber to join cafe_master with bd_master 11/20/2018
                		//String sqlQuery = "select a.EID,a.CHEMICALTERM,a.SPECIESTERM,a.REGIONALTERM,a.DATABASE,a.CITATIONLANGUAGE,a.CITATIONTITLE,a.CITTYPE,a.ABSTRACTDATA,a.PII,a.PUI,a.COPYRIGHT,a.M_ID,a.accessnumber,a.datesort,a.author,a.author_1,a.AFFILIATION,a.AFFILIATION_1,a.CORRESPONDENCEAFFILIATION,a.CODEN,a.ISSUE,a.CLASSIFICATIONCODE,a.CLASSIFICATIONDESC,a.CONTROLLEDTERM,a.UNCONTROLLEDTERM,a.MAINHEADING,a.TREATMENTCODE,a.LOADNUMBER,a.SOURCETYPE,a.SOURCECOUNTRY,a.SOURCEID,a.SOURCETITLE,a.SOURCETITLEABBREV,a.ISSUETITLE,a.ISSN,a.EISSN,a.ISBN,a.VOLUME,a.PAGE,a.PAGECOUNT,a.ARTICLENUMBER, substr(a.PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,a.PUBLICATIONDATE,a.EDITORS,a.PUBLISHERNAME,a.PUBLISHERADDRESS,a.PUBLISHERELECTRONICADDRESS,a.REPORTNUMBER,a.CONFNAME, a.CONFCATNUMBER,a.CONFCODE,a.CONFLOCATION,a.CONFDATE,a.CONFSPONSORS,a.CONFERENCEPARTNUMBER, a.CONFERENCEPAGERANGE, a.CONFERENCEPAGECOUNT, a.CONFERENCEEDITOR, a.CONFERENCEORGANIZATION,a.CONFERENCEEDITORADDRESS,a.TRANSLATEDSOURCETITLE,a.VOLUMETITLE,a.DOI,a.ASSIG,a.CASREGISTRYNUMBER,a.APILT,a.APILT1,a.APICT,a.APICT1,a.APIAMS,a.SEQ_NUM,a.GRANTLIST,b.author as cafe_author,b.author_1 as cafe_author1,b.affiliation as cafe_affiliation,b.affiliation_1 as cafe_affiliation1,b.CORRESPONDENCEAFFILIATION as CAFE_CORRESPONDENCEAFFILIATION,null as authorid,null as affid,a.SOURCEBIBTEXT,a.STANDARDID,a.STANDARDDESIGNATION,a.NORMSTANDARDID,a.GRANTTEXT from bd_master_orig a left outer join cafe_master b on a.accessnumber = b.accessnumber where a.database='cpx'";
                		System.out.println("sqlQuery="+sqlQuery);
                		rs = stmt.executeQuery(sqlQuery);
                	}
                	else
                	{
                		//blocked ISOPENACESS
                		
                		String sqlQuery = "select a.UPDATENUMBER,a.EID,a.CHEMICALTERM,a.SPECIESTERM,a.REGIONALTERM,a.DATABASE,a.CITATIONLANGUAGE,a.CITATIONTITLE,a.CITTYPE,a.ABSTRACTDATA,a.PII,a.PUI,a.COPYRIGHT,a.M_ID,a.accessnumber,a.datesort,a.author,a.author_1,a.AFFILIATION,a.AFFILIATION_1,a.CORRESPONDENCEAFFILIATION,a.CODEN,a.ISSUE,a.CLASSIFICATIONCODE,a.CLASSIFICATIONDESC,a.CONTROLLEDTERM,a.UNCONTROLLEDTERM,a.MAINHEADING,a.TREATMENTCODE,a.LOADNUMBER,a.SOURCETYPE,a.SOURCECOUNTRY,a.SOURCEID,a.SOURCETITLE,a.SOURCETITLEABBREV,a.ISSUETITLE,a.ISSN,a.EISSN,a.ISBN,a.VOLUME,a.PAGE,a.PAGECOUNT,a.ARTICLENUMBER, substr(a.PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,a.PUBLICATIONDATE,a.EDITORS,a.PUBLISHERNAME,a.PUBLISHERADDRESS,a.PUBLISHERELECTRONICADDRESS,a.REPORTNUMBER,a.CONFNAME, a.CONFCATNUMBER,a.CONFCODE,a.CONFLOCATION,a.CONFDATE,a.CONFSPONSORS,a.CONFERENCEPARTNUMBER, a.CONFERENCEPAGERANGE, a.CONFERENCEPAGECOUNT, a.CONFERENCEEDITOR, a.CONFERENCEORGANIZATION,a.CONFERENCEEDITORADDRESS,a.TRANSLATEDSOURCETITLE,a.VOLUMETITLE,a.DOI,a.ASSIG,a.CASREGISTRYNUMBER,a.APILT,a.APILT1,a.APICT,a.APICT1,a.APIAMS,a.SEQ_NUM,a.GRANTLIST,b.author as cafe_author,b.author_1 as cafe_author1,b.affiliation as cafe_affiliation,b.affiliation_1 as cafe_affiliation1,b.CORRESPONDENCEAFFILIATION as CAFE_CORRESPONDENCEAFFILIATION,null as authorid,null as affid,a.SOURCEBIBTEXT,a.STANDARDID,a.STANDARDDESIGNATION,a.NORMSTANDARDID,a.GRANTTEXT,a.ISOPENACESS, count(*) over() totalCount from bd_master_orig a left outer join CAFE_PUI_LIST_MASTER c on a.pui= c.puisecondary left outer join cafe_master b on b.pui=c.pui where a.database='cpx' and a.updateNumber='"+updateNumber+"' and a.publicationyear is not null";
                		//String sqlQuery = "select a.EID,a.CHEMICALTERM,a.SPECIESTERM,a.REGIONALTERM,a.DATABASE,a.CITATIONLANGUAGE,a.CITATIONTITLE,a.CITTYPE,a.ABSTRACTDATA,a.PII,a.PUI,a.COPYRIGHT,a.M_ID,a.accessnumber,a.datesort,a.author,a.author_1,a.AFFILIATION,a.AFFILIATION_1,a.CORRESPONDENCEAFFILIATION,a.CODEN,a.ISSUE,a.CLASSIFICATIONCODE,a.CLASSIFICATIONDESC,a.CONTROLLEDTERM,a.UNCONTROLLEDTERM,a.MAINHEADING,a.TREATMENTCODE,a.LOADNUMBER,a.SOURCETYPE,a.SOURCECOUNTRY,a.SOURCEID,a.SOURCETITLE,a.SOURCETITLEABBREV,a.ISSUETITLE,a.ISSN,a.EISSN,a.ISBN,a.VOLUME,a.PAGE,a.PAGECOUNT,a.ARTICLENUMBER, substr(a.PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,a.PUBLICATIONDATE,a.EDITORS,a.PUBLISHERNAME,a.PUBLISHERADDRESS,a.PUBLISHERELECTRONICADDRESS,a.REPORTNUMBER,a.CONFNAME, a.CONFCATNUMBER,a.CONFCODE,a.CONFLOCATION,a.CONFDATE,a.CONFSPONSORS,a.CONFERENCEPARTNUMBER, a.CONFERENCEPAGERANGE, a.CONFERENCEPAGECOUNT, a.CONFERENCEEDITOR, a.CONFERENCEORGANIZATION,a.CONFERENCEEDITORADDRESS,a.TRANSLATEDSOURCETITLE,a.VOLUMETITLE,a.DOI,a.ASSIG,a.CASREGISTRYNUMBER,a.APILT,a.APILT1,a.APICT,a.APICT1,a.APIAMS,a.SEQ_NUM,a.GRANTLIST,b.author as cafe_author,b.author_1 as cafe_author1,b.affiliation as cafe_affiliation,b.affiliation_1 as cafe_affiliation1,b.CORRESPONDENCEAFFILIATION as CAFE_CORRESPONDENCEAFFILIATION,null as authorid,null as affid,a.SOURCEBIBTEXT,a.STANDARDID,a.STANDARDDESIGNATION,a.NORMSTANDARDID,a.GRANTTEXT,a.ISOPENACESS from bd_master_orig a left outer join cafe_master b on a.cafe_pui = b.pui where a.database='cpx' and a.updateNumber='"+updateNumber+"'";
                		//String sqlQuery = "select a.EID,a.CHEMICALTERM,a.SPECIESTERM,a.REGIONALTERM,a.DATABASE,a.CITATIONLANGUAGE,a.CITATIONTITLE,a.CITTYPE,a.ABSTRACTDATA,a.PII,a.PUI,a.COPYRIGHT,a.M_ID,a.accessnumber,a.datesort,a.author,a.author_1,a.AFFILIATION,a.AFFILIATION_1,a.CORRESPONDENCEAFFILIATION,a.CODEN,a.ISSUE,a.CLASSIFICATIONCODE,a.CLASSIFICATIONDESC,a.CONTROLLEDTERM,a.UNCONTROLLEDTERM,a.MAINHEADING,a.TREATMENTCODE,a.LOADNUMBER,a.SOURCETYPE,a.SOURCECOUNTRY,a.SOURCEID,a.SOURCETITLE,a.SOURCETITLEABBREV,a.ISSUETITLE,a.ISSN,a.EISSN,a.ISBN,a.VOLUME,a.PAGE,a.PAGECOUNT,a.ARTICLENUMBER, substr(a.PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,a.PUBLICATIONDATE,a.EDITORS,a.PUBLISHERNAME,a.PUBLISHERADDRESS,a.PUBLISHERELECTRONICADDRESS,a.REPORTNUMBER,a.CONFNAME, a.CONFCATNUMBER,a.CONFCODE,a.CONFLOCATION,a.CONFDATE,a.CONFSPONSORS,a.CONFERENCEPARTNUMBER, a.CONFERENCEPAGERANGE, a.CONFERENCEPAGECOUNT, a.CONFERENCEEDITOR, a.CONFERENCEORGANIZATION,a.CONFERENCEEDITORADDRESS,a.TRANSLATEDSOURCETITLE,a.VOLUMETITLE,a.DOI,a.ASSIG,a.CASREGISTRYNUMBER,a.APILT,a.APILT1,a.APICT,a.APICT1,a.APIAMS,a.SEQ_NUM,a.GRANTLIST,b.author as cafe_author,b.author_1 as cafe_author1,b.affiliation as cafe_affiliation,b.affiliation_1 as cafe_affiliation1,b.CORRESPONDENCEAFFILIATION as CAFE_CORRESPONDENCEAFFILIATION,null as authorid,null as affid,a.SOURCEBIBTEXT,a.STANDARDID,a.STANDARDDESIGNATION,a.NORMSTANDARDID,a.GRANTTEXT from bd_master_orig a left outer join cafe_master b on a.cafe_pui = b.pui where a.database='cpx' and a.updateNumber='"+updateNumber+"'";
                		//use accessnumber to join cafe_master with bd_master 11/20/2018
                		//String sqlQuery = "select a.EID,a.CHEMICALTERM,a.SPECIESTERM,a.REGIONALTERM,a.DATABASE,a.CITATIONLANGUAGE,a.CITATIONTITLE,a.CITTYPE,a.ABSTRACTDATA,a.PII,a.PUI,a.COPYRIGHT,a.M_ID,a.accessnumber,a.datesort,a.author,a.author_1,a.AFFILIATION,a.AFFILIATION_1,a.CORRESPONDENCEAFFILIATION,a.CODEN,a.ISSUE,a.CLASSIFICATIONCODE,a.CLASSIFICATIONDESC,a.CONTROLLEDTERM,a.UNCONTROLLEDTERM,a.MAINHEADING,a.TREATMENTCODE,a.LOADNUMBER,a.SOURCETYPE,a.SOURCECOUNTRY,a.SOURCEID,a.SOURCETITLE,a.SOURCETITLEABBREV,a.ISSUETITLE,a.ISSN,a.EISSN,a.ISBN,a.VOLUME,a.PAGE,a.PAGECOUNT,a.ARTICLENUMBER, substr(a.PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,a.PUBLICATIONDATE,a.EDITORS,a.PUBLISHERNAME,a.PUBLISHERADDRESS,a.PUBLISHERELECTRONICADDRESS,a.REPORTNUMBER,a.CONFNAME, a.CONFCATNUMBER,a.CONFCODE,a.CONFLOCATION,a.CONFDATE,a.CONFSPONSORS,a.CONFERENCEPARTNUMBER, a.CONFERENCEPAGERANGE, a.CONFERENCEPAGECOUNT, a.CONFERENCEEDITOR, a.CONFERENCEORGANIZATION,a.CONFERENCEEDITORADDRESS,a.TRANSLATEDSOURCETITLE,a.VOLUMETITLE,a.DOI,a.ASSIG,a.CASREGISTRYNUMBER,a.APILT,a.APILT1,a.APICT,a.APICT1,a.APIAMS,a.SEQ_NUM,a.GRANTLIST,b.author as cafe_author,b.author_1 as cafe_author1,b.affiliation as cafe_affiliation,b.affiliation_1 as cafe_affiliation1,b.CORRESPONDENCEAFFILIATION as CAFE_CORRESPONDENCEAFFILIATION,null as authorid,null as affid,a.SOURCEBIBTEXT,a.STANDARDID,a.STANDARDDESIGNATION,a.NORMSTANDARDID,a.GRANTTEXT from bd_master_orig a left outer join cafe_master b on a.accessnumber = b.accessnumber where a.database='cpx' and a.updateNumber='"+updateNumber+"'";
                		System.out.println("sqlQuery="+sqlQuery);
                		rs = stmt.executeQuery(sqlQuery);
                	}
                }
                else
                {
                	String sqlQuery = "select UPDATENUMBER,CHEMICALTERM,SPECIESTERM,REGIONALTERM,DATABASE,CITATIONLANGUAGE,CITATIONTITLE,CITTYPE,ABSTRACTDATA,PII,PUI,COPYRIGHT,M_ID,accessnumber,datesort,author,author_1,AFFILIATION,AFFILIATION_1,CORRESPONDENCEAFFILIATION,CODEN,ISSUE,CLASSIFICATIONCODE,CLASSIFICATIONDESC,CONTROLLEDTERM,UNCONTROLLEDTERM,MAINHEADING,TREATMENTCODE,LOADNUMBER,SOURCETYPE,SOURCECOUNTRY,SOURCEID,SOURCETITLE,SOURCETITLEABBREV,ISSUETITLE,ISSN,EISSN,ISBN,VOLUME,PAGE,PAGECOUNT,ARTICLENUMBER, substr(PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,PUBLICATIONDATE,EDITORS,PUBLISHERNAME,PUBLISHERADDRESS,PUBLISHERELECTRONICADDRESS,REPORTNUMBER,CONFNAME, CONFCATNUMBER,CONFCODE,CONFLOCATION,CONFDATE,CONFSPONSORS,CONFERENCEPARTNUMBER, CONFERENCEPAGERANGE, CONFERENCEPAGECOUNT, CONFERENCEEDITOR, CONFERENCEORGANIZATION,CONFERENCEEDITORADDRESS,TRANSLATEDSOURCETITLE,VOLUMETITLE,DOI,ASSIG,CASREGISTRYNUMBER,APILT,APILT1,APICT,APICT1,APIAMS,SEQ_NUM,GRANTLIST,null as cafe_author,null as cafe_author1,null as cafe_affiliation,null as cafe_affiliation1,null as CAFE_CORRESPONDENCEAFFILIATION,null as authorid,null as affid,SOURCEBIBTEXT,STANDARDID,STANDARDDESIGNATION,NORMSTANDARDID,GRANTTEXT,ISOPENACESS, count(*) over() totalCount from bd_master_orig where database='"+dbname+"' and updateNumber='"+updateNumber+"' and publicationyear is not null";
                    System.out.println("sqlQuery="+sqlQuery);
                	rs = stmt.executeQuery(sqlQuery);
                }
                //rs = stmt.executeQuery("select CHEMICALTERM,SPECIESTERM,REGIONALTERM,DATABASE,CITATIONLANGUAGE,CITATIONTITLE,CITTYPE,ABSTRACTDATA,PII,PUI,COPYRIGHT,M_ID,accessnumber,datesort,author,author_1,AFFILIATION,AFFILIATION_1,CORRESPONDENCEAFFILIATION,CODEN,ISSUE,CLASSIFICATIONCODE,CLASSIFICATIONDESC,CONTROLLEDTERM,UNCONTROLLEDTERM,MAINHEADING,TREATMENTCODE,LOADNUMBER,SOURCETYPE,SOURCECOUNTRY,SOURCEID,SOURCETITLE,SOURCETITLEABBREV,ISSUETITLE,ISSN,EISSN,ISBN,VOLUME,PAGE,PAGECOUNT,ARTICLENUMBER, substr(PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,PUBLICATIONDATE,EDITORS,PUBLISHERNAME,PUBLISHERADDRESS,PUBLISHERELECTRONICADDRESS,REPORTNUMBER,CONFNAME, CONFCATNUMBER,CONFCODE,CONFLOCATION,CONFDATE,CONFSPONSORS,CONFERENCEPARTNUMBER, CONFERENCEPAGERANGE, CONFERENCEPAGECOUNT, CONFERENCEEDITOR, CONFERENCEORGANIZATION,CONFERENCEEDITORADDRESS,TRANSLATEDSOURCETITLE,VOLUMETITLE,DOI,ASSIG,CASREGISTRYNUMBER,APILT,APILT1,APICT,APICT1,APIAMS,SEQ_NUM from bd_master_orig where database='"+dbname+"' and updateNumber='"+updateNumber+"'");
               c.writeRecs(rs,con,updateNumber,"extractupdate",dbname);
            }
            else if(dbname.equals("cpx") && action.equalsIgnoreCase("extractcafe"))
            {
                System.out.println("Running the query...");
                writer.setOperation("add");
                //XmlCombiner c = new XmlCombiner(writer);
                //blocked ISOPENACESS
                String sqlQuery = "select a.UPDATENUMBER,a.EID,a.CHEMICALTERM,a.SPECIESTERM,a.REGIONALTERM,a.DATABASE,a.CITATIONLANGUAGE,a.CITATIONTITLE,a.CITTYPE,a.ABSTRACTDATA,a.PII,a.PUI,a.COPYRIGHT,a.M_ID,a.accessnumber,a.datesort,a.author,a.author_1,a.AFFILIATION,a.AFFILIATION_1,a.CORRESPONDENCEAFFILIATION,a.CODEN,a.ISSUE,a.CLASSIFICATIONCODE,a.CLASSIFICATIONDESC,a.CONTROLLEDTERM,a.UNCONTROLLEDTERM,a.MAINHEADING,a.TREATMENTCODE,a.LOADNUMBER,a.SOURCETYPE,a.SOURCECOUNTRY,a.SOURCEID,a.SOURCETITLE,a.SOURCETITLEABBREV,a.ISSUETITLE,a.ISSN,a.EISSN,a.ISBN,a.VOLUME,a.PAGE,a.PAGECOUNT,a.ARTICLENUMBER, substr(a.PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,a.PUBLICATIONDATE,a.EDITORS,a.PUBLISHERNAME,a.PUBLISHERADDRESS,a.PUBLISHERELECTRONICADDRESS,a.REPORTNUMBER,a.CONFNAME, a.CONFCATNUMBER,a.CONFCODE,a.CONFLOCATION,a.CONFDATE,a.CONFSPONSORS,a.CONFERENCEPARTNUMBER, a.CONFERENCEPAGERANGE, a.CONFERENCEPAGECOUNT, a.CONFERENCEEDITOR, a.CONFERENCEORGANIZATION,a.CONFERENCEEDITORADDRESS,a.TRANSLATEDSOURCETITLE,a.VOLUMETITLE,a.DOI,a.ASSIG,a.CASREGISTRYNUMBER,a.APILT,a.APILT1,a.APICT,a.APICT1,a.APIAMS,a.SEQ_NUM,a.GRANTLIST,b.author as cafe_author,b.author_1 as cafe_author1,b.affiliation as cafe_affiliation,b.affiliation_1 as cafe_affiliation1,b.CORRESPONDENCEAFFILIATION as CAFE_CORRESPONDENCEAFFILIATION,null as authorid,null as affid,a.SOURCEBIBTEXT,a.STANDARDID,a.STANDARDDESIGNATION,a.NORMSTANDARDID,a.GRANTTEXT,a.ISOPENACESS, count(*) over() totalCount from bd_master_orig a left outer join CAFE_PUI_LIST_MASTER c on a.pui= c.puisecondary left outer join cafe_master b on b.pui=c.pui where a.database='cpx' and b.updateNumber='"+updateNumber+"' and a.publicationyear is not null";
                //use accessnumber to join cafe_master with bd_master 11/20/2018
                //String sqlQuery = "select a.CHEMICALTERM,a.SPECIESTERM,a.REGIONALTERM,a.DATABASE,a.CITATIONLANGUAGE,a.CITATIONTITLE,a.CITTYPE,a.ABSTRACTDATA,a.PII,a.PUI,a.COPYRIGHT,a.M_ID,a.accessnumber,a.datesort,a.author,a.author_1,a.AFFILIATION,a.AFFILIATION_1,a.CORRESPONDENCEAFFILIATION,a.CODEN,a.ISSUE,a.CLASSIFICATIONCODE,a.CLASSIFICATIONDESC,a.CONTROLLEDTERM,a.UNCONTROLLEDTERM,a.MAINHEADING,a.TREATMENTCODE,a.LOADNUMBER,a.SOURCETYPE,a.SOURCECOUNTRY,a.SOURCEID,a.SOURCETITLE,a.SOURCETITLEABBREV,a.ISSUETITLE,a.ISSN,a.EISSN,a.ISBN,a.VOLUME,a.PAGE,a.PAGECOUNT,a.ARTICLENUMBER, substr(a.PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,a.PUBLICATIONDATE,a.EDITORS,a.PUBLISHERNAME,a.PUBLISHERADDRESS,a.PUBLISHERELECTRONICADDRESS,a.REPORTNUMBER,a.CONFNAME, a.CONFCATNUMBER,a.CONFCODE,a.CONFLOCATION,a.CONFDATE,a.CONFSPONSORS,a.CONFERENCEPARTNUMBER, a.CONFERENCEPAGERANGE, a.CONFERENCEPAGECOUNT, a.CONFERENCEEDITOR, a.CONFERENCEORGANIZATION,a.CONFERENCEEDITORADDRESS,a.TRANSLATEDSOURCETITLE,a.VOLUMETITLE,a.DOI,a.ASSIG,a.CASREGISTRYNUMBER,a.APILT,a.APILT1,a.APICT,a.APICT1,a.APIAMS,a.SEQ_NUM,a.GRANTLIST,b.author as cafe_author,b.author_1 as cafe_author1,b.affiliation as cafe_affiliation,b.affiliation_1 as cafe_affiliation1,b.CORRESPONDENCEAFFILIATION as CAFE_CORRESPONDENCEAFFILIATION,null as authorid,null as affid,a.SOURCEBIBTEXT,a.STANDARDID,a.STANDARDDESIGNATION,a.NORMSTANDARDID,a.GRANTTEXT from bd_master_orig a left join cafe_master b on a.accessnumber=b.accessnumber where a.database='cpx' and b.updateNumber='"+updateNumber+"'";
                System.out.println("sqlQuery="+sqlQuery);
                rs = stmt.executeQuery(sqlQuery);
                //rs = stmt.executeQuery("select CHEMICALTERM,SPECIESTERM,REGIONALTERM,DATABASE,CITATIONLANGUAGE,CITATIONTITLE,CITTYPE,ABSTRACTDATA,PII,PUI,COPYRIGHT,M_ID,accessnumber,datesort,author,author_1,AFFILIATION,AFFILIATION_1,CORRESPONDENCEAFFILIATION,CODEN,ISSUE,CLASSIFICATIONCODE,CLASSIFICATIONDESC,CONTROLLEDTERM,UNCONTROLLEDTERM,MAINHEADING,TREATMENTCODE,LOADNUMBER,SOURCETYPE,SOURCECOUNTRY,SOURCEID,SOURCETITLE,SOURCETITLEABBREV,ISSUETITLE,ISSN,EISSN,ISBN,VOLUME,PAGE,PAGECOUNT,ARTICLENUMBER, substr(PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,PUBLICATIONDATE,EDITORS,PUBLISHERNAME,PUBLISHERADDRESS,PUBLISHERELECTRONICADDRESS,REPORTNUMBER,CONFNAME, CONFCATNUMBER,CONFCODE,CONFLOCATION,CONFDATE,CONFSPONSORS,CONFERENCEPARTNUMBER, CONFERENCEPAGERANGE, CONFERENCEPAGECOUNT, CONFERENCEEDITOR, CONFERENCEORGANIZATION,CONFERENCEEDITORADDRESS,TRANSLATEDSOURCETITLE,VOLUMETITLE,DOI,ASSIG,CASREGISTRYNUMBER,APILT,APILT1,APICT,APICT1,APIAMS,SEQ_NUM from bd_master_orig where database='"+dbname+"' and m_id in (select mid from bd_master_numerical where mid is not null and loadnumber='"+updateNumber+"')");
                c.writeRecs(rs,con,updateNumber,"extractcafe",dbname);
            }
            else if(dbname.equals("cpx") && action.equalsIgnoreCase("extractcafeloadnumber"))
            {
                System.out.println("Running the query...");
                writer.setOperation("add");
                //XmlCombiner c = new XmlCombiner(writer);
                //blocked ISOPENACESS
                String sqlQuery = "select a.EID,a.CHEMICALTERM,a.SPECIESTERM,a.REGIONALTERM,a.DATABASE,a.CITATIONLANGUAGE,a.CITATIONTITLE,a.CITTYPE,a.ABSTRACTDATA,a.PII,a.PUI,a.COPYRIGHT,a.M_ID,a.accessnumber,a.datesort,a.author,a.author_1,a.AFFILIATION,a.AFFILIATION_1,a.CORRESPONDENCEAFFILIATION,a.CODEN,a.ISSUE,a.CLASSIFICATIONCODE,a.CLASSIFICATIONDESC,a.CONTROLLEDTERM,a.UNCONTROLLEDTERM,a.MAINHEADING,a.TREATMENTCODE,a.LOADNUMBER,a.SOURCETYPE,a.SOURCECOUNTRY,a.SOURCEID,a.SOURCETITLE,a.SOURCETITLEABBREV,a.ISSUETITLE,a.ISSN,a.EISSN,a.ISBN,a.VOLUME,a.PAGE,a.PAGECOUNT,a.ARTICLENUMBER, substr(a.PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,a.PUBLICATIONDATE,a.EDITORS,a.PUBLISHERNAME,a.PUBLISHERADDRESS,a.PUBLISHERELECTRONICADDRESS,a.REPORTNUMBER,a.CONFNAME, a.CONFCATNUMBER,a.CONFCODE,a.CONFLOCATION,a.CONFDATE,a.CONFSPONSORS,a.CONFERENCEPARTNUMBER, a.CONFERENCEPAGERANGE, a.CONFERENCEPAGECOUNT, a.CONFERENCEEDITOR, a.CONFERENCEORGANIZATION,a.CONFERENCEEDITORADDRESS,a.TRANSLATEDSOURCETITLE,a.VOLUMETITLE,a.DOI,a.ASSIG,a.CASREGISTRYNUMBER,a.APILT,a.APILT1,a.APICT,a.APICT1,a.APIAMS,a.SEQ_NUM,a.GRANTLIST,b.author as cafe_author,b.author_1 as cafe_author1,b.affiliation as cafe_affiliation,b.affiliation_1 as cafe_affiliation1,b.CORRESPONDENCEAFFILIATION as CAFE_CORRESPONDENCEAFFILIATION,null as authorid,null as affid,a.SOURCEBIBTEXT,a.STANDARDID,a.STANDARDDESIGNATION,a.NORMSTANDARDID,a.GRANTTEXT,a.ISOPENACESS, count(*) over() totalCount from bd_master_orig a left outer join CAFE_PUI_LIST_MASTER c on a.pui= c.puisecondary left outer join cafe_master b on b.pui=c.pui where a.database='cpx' and b.loadnumber='"+updateNumber+"' and a.publicationyear is not null";
                //String sqlQuery ="select a.CHEMICALTERM,a.SPECIESTERM,a.REGIONALTERM,a.DATABASE,a.CITATIONLANGUAGE,a.CITATIONTITLE,a.CITTYPE,a.ABSTRACTDATA,a.PII,a.PUI,a.COPYRIGHT,a.M_ID,a.accessnumber,a.datesort,a.author,a.author_1,a.AFFILIATION,a.AFFILIATION_1,a.CORRESPONDENCEAFFILIATION,a.CODEN,a.ISSUE,a.CLASSIFICATIONCODE,a.CLASSIFICATIONDESC,a.CONTROLLEDTERM,a.UNCONTROLLEDTERM,a.MAINHEADING,a.TREATMENTCODE,a.LOADNUMBER,a.SOURCETYPE,a.SOURCECOUNTRY,a.SOURCEID,a.SOURCETITLE,a.SOURCETITLEABBREV,a.ISSUETITLE,a.ISSN,a.EISSN,a.ISBN,a.VOLUME,a.PAGE,a.PAGECOUNT,a.ARTICLENUMBER, substr(a.PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,a.PUBLICATIONDATE,a.EDITORS,a.PUBLISHERNAME,a.PUBLISHERADDRESS,a.PUBLISHERELECTRONICADDRESS,a.REPORTNUMBER,a.CONFNAME, a.CONFCATNUMBER,a.CONFCODE,a.CONFLOCATION,a.CONFDATE,a.CONFSPONSORS,a.CONFERENCEPARTNUMBER, a.CONFERENCEPAGERANGE, a.CONFERENCEPAGECOUNT, a.CONFERENCEEDITOR, a.CONFERENCEORGANIZATION,a.CONFERENCEEDITORADDRESS,a.TRANSLATEDSOURCETITLE,a.VOLUMETITLE,a.DOI,a.ASSIG,a.CASREGISTRYNUMBER,a.APILT,a.APILT1,a.APICT,a.APICT1,a.APIAMS,a.SEQ_NUM,a.GRANTLIST,b.author as cafe_author,b.author_1 as cafe_author1,b.affiliation as cafe_affiliation,b.affiliation_1 as cafe_affiliation1,b.CORRESPONDENCEAFFILIATION as CAFE_CORRESPONDENCEAFFILIATION,null as authorid,null as affid,a.SOURCEBIBTEXT,a.STANDARDID,a.STANDARDDESIGNATION,a.NORMSTANDARDID,a.GRANTTEXT,a.ISOPENACESS from bd_master_orig a left join cafe_master b on a.cafe_pui=b.pui where a.database='cpx' and b.loadnumber="+updateNumber;
                //String sqlQuery ="select a.CHEMICALTERM,a.SPECIESTERM,a.REGIONALTERM,a.DATABASE,a.CITATIONLANGUAGE,a.CITATIONTITLE,a.CITTYPE,a.ABSTRACTDATA,a.PII,a.PUI,a.COPYRIGHT,a.M_ID,a.accessnumber,a.datesort,a.author,a.author_1,a.AFFILIATION,a.AFFILIATION_1,a.CORRESPONDENCEAFFILIATION,a.CODEN,a.ISSUE,a.CLASSIFICATIONCODE,a.CLASSIFICATIONDESC,a.CONTROLLEDTERM,a.UNCONTROLLEDTERM,a.MAINHEADING,a.TREATMENTCODE,a.LOADNUMBER,a.SOURCETYPE,a.SOURCECOUNTRY,a.SOURCEID,a.SOURCETITLE,a.SOURCETITLEABBREV,a.ISSUETITLE,a.ISSN,a.EISSN,a.ISBN,a.VOLUME,a.PAGE,a.PAGECOUNT,a.ARTICLENUMBER, substr(a.PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,a.PUBLICATIONDATE,a.EDITORS,a.PUBLISHERNAME,a.PUBLISHERADDRESS,a.PUBLISHERELECTRONICADDRESS,a.REPORTNUMBER,a.CONFNAME, a.CONFCATNUMBER,a.CONFCODE,a.CONFLOCATION,a.CONFDATE,a.CONFSPONSORS,a.CONFERENCEPARTNUMBER, a.CONFERENCEPAGERANGE, a.CONFERENCEPAGECOUNT, a.CONFERENCEEDITOR, a.CONFERENCEORGANIZATION,a.CONFERENCEEDITORADDRESS,a.TRANSLATEDSOURCETITLE,a.VOLUMETITLE,a.DOI,a.ASSIG,a.CASREGISTRYNUMBER,a.APILT,a.APILT1,a.APICT,a.APICT1,a.APIAMS,a.SEQ_NUM,a.GRANTLIST,b.author as cafe_author,b.author_1 as cafe_author1,b.affiliation as cafe_affiliation,b.affiliation_1 as cafe_affiliation1,b.CORRESPONDENCEAFFILIATION as CAFE_CORRESPONDENCEAFFILIATION,null as authorid,null as affid,a.SOURCEBIBTEXT,a.STANDARDID,a.STANDARDDESIGNATION,a.NORMSTANDARDID,a.GRANTTEXT from bd_master_orig a left join cafe_master b on a.cafe_pui=b.pui where a.database='cpx' and b.loadnumber="+updateNumber;
                //use accessnumber to join cafe_master with bd_master 11/20/2018
                //String sqlQuery ="select a.CHEMICALTERM,a.SPECIESTERM,a.REGIONALTERM,a.DATABASE,a.CITATIONLANGUAGE,a.CITATIONTITLE,a.CITTYPE,a.ABSTRACTDATA,a.PII,a.PUI,a.COPYRIGHT,a.M_ID,a.accessnumber,a.datesort,a.author,a.author_1,a.AFFILIATION,a.AFFILIATION_1,a.CORRESPONDENCEAFFILIATION,a.CODEN,a.ISSUE,a.CLASSIFICATIONCODE,a.CLASSIFICATIONDESC,a.CONTROLLEDTERM,a.UNCONTROLLEDTERM,a.MAINHEADING,a.TREATMENTCODE,a.LOADNUMBER,a.SOURCETYPE,a.SOURCECOUNTRY,a.SOURCEID,a.SOURCETITLE,a.SOURCETITLEABBREV,a.ISSUETITLE,a.ISSN,a.EISSN,a.ISBN,a.VOLUME,a.PAGE,a.PAGECOUNT,a.ARTICLENUMBER, substr(a.PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,a.PUBLICATIONDATE,a.EDITORS,a.PUBLISHERNAME,a.PUBLISHERADDRESS,a.PUBLISHERELECTRONICADDRESS,a.REPORTNUMBER,a.CONFNAME, a.CONFCATNUMBER,a.CONFCODE,a.CONFLOCATION,a.CONFDATE,a.CONFSPONSORS,a.CONFERENCEPARTNUMBER, a.CONFERENCEPAGERANGE, a.CONFERENCEPAGECOUNT, a.CONFERENCEEDITOR, a.CONFERENCEORGANIZATION,a.CONFERENCEEDITORADDRESS,a.TRANSLATEDSOURCETITLE,a.VOLUMETITLE,a.DOI,a.ASSIG,a.CASREGISTRYNUMBER,a.APILT,a.APILT1,a.APICT,a.APICT1,a.APIAMS,a.SEQ_NUM,a.GRANTLIST,b.author as cafe_author,b.author_1 as cafe_author1,b.affiliation as cafe_affiliation,b.affiliation_1 as cafe_affiliation1,b.CORRESPONDENCEAFFILIATION as CAFE_CORRESPONDENCEAFFILIATION,null as authorid,null as affid,a.SOURCEBIBTEXT,a.STANDARDID,a.STANDARDDESIGNATION,a.NORMSTANDARDID,a.GRANTTEXT from bd_master_orig a left join cafe_master b on a.accessnumber=b.accessnumber where a.database='cpx' and b.loadnumber="+updateNumber;
                System.out.println("sqlQuery="+sqlQuery);
                rs = stmt.executeQuery(sqlQuery);
                //rs = stmt.executeQuery("select CHEMICALTERM,SPECIESTERM,REGIONALTERM,DATABASE,CITATIONLANGUAGE,CITATIONTITLE,CITTYPE,ABSTRACTDATA,PII,PUI,COPYRIGHT,M_ID,accessnumber,datesort,author,author_1,AFFILIATION,AFFILIATION_1,CORRESPONDENCEAFFILIATION,CODEN,ISSUE,CLASSIFICATIONCODE,CLASSIFICATIONDESC,CONTROLLEDTERM,UNCONTROLLEDTERM,MAINHEADING,TREATMENTCODE,LOADNUMBER,SOURCETYPE,SOURCECOUNTRY,SOURCEID,SOURCETITLE,SOURCETITLEABBREV,ISSUETITLE,ISSN,EISSN,ISBN,VOLUME,PAGE,PAGECOUNT,ARTICLENUMBER, substr(PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,PUBLICATIONDATE,EDITORS,PUBLISHERNAME,PUBLISHERADDRESS,PUBLISHERELECTRONICADDRESS,REPORTNUMBER,CONFNAME, CONFCATNUMBER,CONFCODE,CONFLOCATION,CONFDATE,CONFSPONSORS,CONFERENCEPARTNUMBER, CONFERENCEPAGERANGE, CONFERENCEPAGECOUNT, CONFERENCEEDITOR, CONFERENCEORGANIZATION,CONFERENCEEDITORADDRESS,TRANSLATEDSOURCETITLE,VOLUMETITLE,DOI,ASSIG,CASREGISTRYNUMBER,APILT,APILT1,APICT,APICT1,APIAMS,SEQ_NUM from bd_master_orig where database='"+dbname+"' and m_id in (select mid from bd_master_numerical where mid is not null and loadnumber='"+updateNumber+"')");
                c.writeRecs(rs,con,updateNumber,"extractcafeloadnumber",dbname);
            }
            else if(dbname.equals("cpx") && action.equalsIgnoreCase("extractallupdate"))
            {
                System.out.println("Running the query...");
                writer.setOperation("add");
                //XmlCombiner c = new XmlCombiner(writer);
                //blocked ISOPENACESS
                String allUpdateString = "select a.UPDATENUMBER,a.EID,a.CHEMICALTERM,a.SPECIESTERM,a.REGIONALTERM,a.DATABASE,a.CITATIONLANGUAGE,a.CITATIONTITLE,a.CITTYPE,a.ABSTRACTDATA,a.PII,a.PUI,a.COPYRIGHT,a.M_ID,a.accessnumber,a.datesort,a.author,a.author_1,a.AFFILIATION,a.AFFILIATION_1,a.CORRESPONDENCEAFFILIATION,a.CODEN,a.ISSUE,a.CLASSIFICATIONCODE,a.CLASSIFICATIONDESC,a.CONTROLLEDTERM,a.UNCONTROLLEDTERM,a.MAINHEADING,a.TREATMENTCODE,a.LOADNUMBER,a.SOURCETYPE,a.SOURCECOUNTRY,a.SOURCEID,a.SOURCETITLE,a.SOURCETITLEABBREV,a.ISSUETITLE,a.ISSN,a.EISSN,a.ISBN,a.VOLUME,a.PAGE,a.PAGECOUNT,a.ARTICLENUMBER, substr(a.PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,a.PUBLICATIONDATE,a.EDITORS,a.PUBLISHERNAME,a.PUBLISHERADDRESS,a.PUBLISHERELECTRONICADDRESS,a.REPORTNUMBER,a.CONFNAME, a.CONFCATNUMBER,a.CONFCODE,a.CONFLOCATION,a.CONFDATE,a.CONFSPONSORS,a.CONFERENCEPARTNUMBER, a.CONFERENCEPAGERANGE, a.CONFERENCEPAGECOUNT, a.CONFERENCEEDITOR, a.CONFERENCEORGANIZATION,a.CONFERENCEEDITORADDRESS,a.TRANSLATEDSOURCETITLE,a.VOLUMETITLE,a.DOI,a.ASSIG,a.CASREGISTRYNUMBER,a.APILT,a.APILT1,a.APICT,a.APICT1,a.APIAMS,a.SEQ_NUM,a.GRANTLIST,b.author as cafe_author,b.author_1 as cafe_author1,b.affiliation as cafe_affiliation,b.affiliation_1 as cafe_affiliation1,b.CORRESPONDENCEAFFILIATION as CAFE_CORRESPONDENCEAFFILIATION,null as authorid,null as affid,a.SOURCEBIBTEXT,a.STANDARDID,a.STANDARDDESIGNATION,a.NORMSTANDARDID,a.GRANTTEXT,a.ISOPENACESS, count(*) over() totalCount from bd_master_orig a left outer join CAFE_PUI_LIST_MASTER c on a.pui= c.puisecondary left outer join cafe_master b on b.pui=c.pui where a.database='cpx' and a.publicationyear is not null and a.updateNumber in ('"+updateNumber+"','"+updateNumber+"01','"+updateNumber+"02','"+updateNumber+"05','"+updateNumber+"07','"+updateNumber+"20')";
                //String allUpdateString = "select a.CHEMICALTERM,a.SPECIESTERM,a.REGIONALTERM,a.DATABASE,a.CITATIONLANGUAGE,a.CITATIONTITLE,a.CITTYPE,a.ABSTRACTDATA,a.PII,a.PUI,a.COPYRIGHT,a.M_ID,a.accessnumber,a.datesort,a.author,a.author_1,a.AFFILIATION,a.AFFILIATION_1,a.CORRESPONDENCEAFFILIATION,a.CODEN,a.ISSUE,a.CLASSIFICATIONCODE,a.CLASSIFICATIONDESC,a.CONTROLLEDTERM,a.UNCONTROLLEDTERM,a.MAINHEADING,a.TREATMENTCODE,a.LOADNUMBER,a.SOURCETYPE,a.SOURCECOUNTRY,a.SOURCEID,a.SOURCETITLE,a.SOURCETITLEABBREV,a.ISSUETITLE,a.ISSN,a.EISSN,a.ISBN,a.VOLUME,a.PAGE,a.PAGECOUNT,a.ARTICLENUMBER, substr(a.PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,a.PUBLICATIONDATE,a.EDITORS,a.PUBLISHERNAME,a.PUBLISHERADDRESS,a.PUBLISHERELECTRONICADDRESS,a.REPORTNUMBER,a.CONFNAME, a.CONFCATNUMBER,a.CONFCODE,a.CONFLOCATION,a.CONFDATE,a.CONFSPONSORS,a.CONFERENCEPARTNUMBER, a.CONFERENCEPAGERANGE, a.CONFERENCEPAGECOUNT, a.CONFERENCEEDITOR, a.CONFERENCEORGANIZATION,a.CONFERENCEEDITORADDRESS,a.TRANSLATEDSOURCETITLE,a.VOLUMETITLE,a.DOI,a.ASSIG,a.CASREGISTRYNUMBER,a.APILT,a.APILT1,a.APICT,a.APICT1,a.APIAMS,a.SEQ_NUM,a.GRANTLIST,b.author as cafe_author,b.author_1 as cafe_author1,b.affiliation as cafe_affiliation,b.affiliation_1 as cafe_affiliation1,b.CORRESPONDENCEAFFILIATION as CAFE_CORRESPONDENCEAFFILIATION,null as authorid,null as affid,a.SOURCEBIBTEXT,a.STANDARDID,a.STANDARDDESIGNATION,a.NORMSTANDARDID,a.GRANTTEXT,a.ISOPENACESS from bd_master_orig a left outer join cafe_master b on a.cafe_pui = b.pui where a.database='cpx' and a.updateNumber in ('"+updateNumber+"','"+updateNumber+"01','"+updateNumber+"02','"+updateNumber+"05','"+updateNumber+"07','"+updateNumber+"20')";
                //String allUpdateString = "select a.CHEMICALTERM,a.SPECIESTERM,a.REGIONALTERM,a.DATABASE,a.CITATIONLANGUAGE,a.CITATIONTITLE,a.CITTYPE,a.ABSTRACTDATA,a.PII,a.PUI,a.COPYRIGHT,a.M_ID,a.accessnumber,a.datesort,a.author,a.author_1,a.AFFILIATION,a.AFFILIATION_1,a.CORRESPONDENCEAFFILIATION,a.CODEN,a.ISSUE,a.CLASSIFICATIONCODE,a.CLASSIFICATIONDESC,a.CONTROLLEDTERM,a.UNCONTROLLEDTERM,a.MAINHEADING,a.TREATMENTCODE,a.LOADNUMBER,a.SOURCETYPE,a.SOURCECOUNTRY,a.SOURCEID,a.SOURCETITLE,a.SOURCETITLEABBREV,a.ISSUETITLE,a.ISSN,a.EISSN,a.ISBN,a.VOLUME,a.PAGE,a.PAGECOUNT,a.ARTICLENUMBER, substr(a.PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,a.PUBLICATIONDATE,a.EDITORS,a.PUBLISHERNAME,a.PUBLISHERADDRESS,a.PUBLISHERELECTRONICADDRESS,a.REPORTNUMBER,a.CONFNAME, a.CONFCATNUMBER,a.CONFCODE,a.CONFLOCATION,a.CONFDATE,a.CONFSPONSORS,a.CONFERENCEPARTNUMBER, a.CONFERENCEPAGERANGE, a.CONFERENCEPAGECOUNT, a.CONFERENCEEDITOR, a.CONFERENCEORGANIZATION,a.CONFERENCEEDITORADDRESS,a.TRANSLATEDSOURCETITLE,a.VOLUMETITLE,a.DOI,a.ASSIG,a.CASREGISTRYNUMBER,a.APILT,a.APILT1,a.APICT,a.APICT1,a.APIAMS,a.SEQ_NUM,a.GRANTLIST,b.author as cafe_author,b.author_1 as cafe_author1,b.affiliation as cafe_affiliation,b.affiliation_1 as cafe_affiliation1,b.CORRESPONDENCEAFFILIATION as CAFE_CORRESPONDENCEAFFILIATION,null as authorid,null as affid,a.SOURCEBIBTEXT,a.STANDARDID,a.STANDARDDESIGNATION,a.NORMSTANDARDID,a.GRANTTEXT from bd_master_orig a left outer join cafe_master b on a.cafe_pui = b.pui where a.database='cpx' and a.updateNumber in ('"+updateNumber+"','"+updateNumber+"01','"+updateNumber+"02','"+updateNumber+"05','"+updateNumber+"07','"+updateNumber+"20')";
                //use accessnumber to join cafe_master with bd_master 11/20/2018
                //String allUpdateString = "select a.CHEMICALTERM,a.SPECIESTERM,a.REGIONALTERM,a.DATABASE,a.CITATIONLANGUAGE,a.CITATIONTITLE,a.CITTYPE,a.ABSTRACTDATA,a.PII,a.PUI,a.COPYRIGHT,a.M_ID,a.accessnumber,a.datesort,a.author,a.author_1,a.AFFILIATION,a.AFFILIATION_1,a.CORRESPONDENCEAFFILIATION,a.CODEN,a.ISSUE,a.CLASSIFICATIONCODE,a.CLASSIFICATIONDESC,a.CONTROLLEDTERM,a.UNCONTROLLEDTERM,a.MAINHEADING,a.TREATMENTCODE,a.LOADNUMBER,a.SOURCETYPE,a.SOURCECOUNTRY,a.SOURCEID,a.SOURCETITLE,a.SOURCETITLEABBREV,a.ISSUETITLE,a.ISSN,a.EISSN,a.ISBN,a.VOLUME,a.PAGE,a.PAGECOUNT,a.ARTICLENUMBER, substr(a.PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,a.PUBLICATIONDATE,a.EDITORS,a.PUBLISHERNAME,a.PUBLISHERADDRESS,a.PUBLISHERELECTRONICADDRESS,a.REPORTNUMBER,a.CONFNAME, a.CONFCATNUMBER,a.CONFCODE,a.CONFLOCATION,a.CONFDATE,a.CONFSPONSORS,a.CONFERENCEPARTNUMBER, a.CONFERENCEPAGERANGE, a.CONFERENCEPAGECOUNT, a.CONFERENCEEDITOR, a.CONFERENCEORGANIZATION,a.CONFERENCEEDITORADDRESS,a.TRANSLATEDSOURCETITLE,a.VOLUMETITLE,a.DOI,a.ASSIG,a.CASREGISTRYNUMBER,a.APILT,a.APILT1,a.APICT,a.APICT1,a.APIAMS,a.SEQ_NUM,a.GRANTLIST,b.author as cafe_author,b.author_1 as cafe_author1,b.affiliation as cafe_affiliation,b.affiliation_1 as cafe_affiliation1,b.CORRESPONDENCEAFFILIATION as CAFE_CORRESPONDENCEAFFILIATION,null as authorid,null as affid,a.SOURCEBIBTEXT,a.STANDARDID,a.STANDARDDESIGNATION,a.NORMSTANDARDID,a.GRANTTEXT from bd_master_orig a left outer join cafe_master b on a.accessnumber = b.accessnumber where a.database='cpx' and a.updateNumber in ('"+updateNumber+"','"+updateNumber+"01','"+updateNumber+"02','"+updateNumber+"05','"+updateNumber+"07','"+updateNumber+"20')";
                System.out.println("allUpdateString="+allUpdateString);
                rs = stmt.executeQuery(allUpdateString);                
                c.writeRecs(rs,con,updateNumber,"extractallupdate",dbname);
            }
            else if(dbname.equals("cpx") && action.equalsIgnoreCase("extractcafedelete"))
            {
                System.out.println("Running the query...");
                writer.setOperation("add");
                //XmlCombiner c = new XmlCombiner(writer);
                //blocked ISOPENACESS
                String sqlQuery ="select a.UPDATENUMBER,a.CHEMICALTERM,a.SPECIESTERM,a.REGIONALTERM,a.DATABASE,a.CITATIONLANGUAGE,a.CITATIONTITLE,a.CITTYPE,a.ABSTRACTDATA,a.PII,a.PUI,a.COPYRIGHT,a.M_ID,a.accessnumber,a.datesort,a.author,a.author_1,a.AFFILIATION,a.AFFILIATION_1,a.CORRESPONDENCEAFFILIATION,a.CODEN,a.ISSUE,a.CLASSIFICATIONCODE,a.CLASSIFICATIONDESC,a.CONTROLLEDTERM,a.UNCONTROLLEDTERM,a.MAINHEADING,a.TREATMENTCODE,a.LOADNUMBER,a.SOURCETYPE,a.SOURCECOUNTRY,a.SOURCEID,a.SOURCETITLE,a.SOURCETITLEABBREV,a.ISSUETITLE,a.ISSN,a.EISSN,a.ISBN,a.VOLUME,a.PAGE,a.PAGECOUNT,a.ARTICLENUMBER, substr(a.PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,a.PUBLICATIONDATE,a.EDITORS,a.PUBLISHERNAME,a.PUBLISHERADDRESS,a.PUBLISHERELECTRONICADDRESS,a.REPORTNUMBER,a.CONFNAME, a.CONFCATNUMBER,a.CONFCODE,a.CONFLOCATION,a.CONFDATE,a.CONFSPONSORS,a.CONFERENCEPARTNUMBER, a.CONFERENCEPAGERANGE, a.CONFERENCEPAGECOUNT, a.CONFERENCEEDITOR, a.CONFERENCEORGANIZATION,a.CONFERENCEEDITORADDRESS,a.TRANSLATEDSOURCETITLE,a.VOLUMETITLE,a.DOI,a.ASSIG,a.CASREGISTRYNUMBER,a.APILT,a.APILT1,a.APICT,a.APICT1,a.APIAMS,a.SEQ_NUM,a.GRANTLIST,null as cafe_author,null as cafe_author1,null as cafe_affiliation,null as cafe_affiliation1,null as CAFE_CORRESPONDENCEAFFILIATION,null as authorid,null as affid,a.SOURCEBIBTEXT,a.STANDARDID,a.STANDARDDESIGNATION,a.NORMSTANDARDID,a.GRANTTEXT,a.ISOPENACESS from bd_master_orig a where a.database='cpx' and a.cafe_pui in(select b.pui, count(*) over() totalCount from CAFE_PUI_LIST_MASTER b, CAFE_weekly_DELETION c where b.puisecondary=c.pui)";
                //String sqlQuery ="select a.CHEMICALTERM,a.SPECIESTERM,a.REGIONALTERM,a.DATABASE,a.CITATIONLANGUAGE,a.CITATIONTITLE,a.CITTYPE,a.ABSTRACTDATA,a.PII,a.PUI,a.COPYRIGHT,a.M_ID,a.accessnumber,a.datesort,a.author,a.author_1,a.AFFILIATION,a.AFFILIATION_1,a.CORRESPONDENCEAFFILIATION,a.CODEN,a.ISSUE,a.CLASSIFICATIONCODE,a.CLASSIFICATIONDESC,a.CONTROLLEDTERM,a.UNCONTROLLEDTERM,a.MAINHEADING,a.TREATMENTCODE,a.LOADNUMBER,a.SOURCETYPE,a.SOURCECOUNTRY,a.SOURCEID,a.SOURCETITLE,a.SOURCETITLEABBREV,a.ISSUETITLE,a.ISSN,a.EISSN,a.ISBN,a.VOLUME,a.PAGE,a.PAGECOUNT,a.ARTICLENUMBER, substr(a.PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,a.PUBLICATIONDATE,a.EDITORS,a.PUBLISHERNAME,a.PUBLISHERADDRESS,a.PUBLISHERELECTRONICADDRESS,a.REPORTNUMBER,a.CONFNAME, a.CONFCATNUMBER,a.CONFCODE,a.CONFLOCATION,a.CONFDATE,a.CONFSPONSORS,a.CONFERENCEPARTNUMBER, a.CONFERENCEPAGERANGE, a.CONFERENCEPAGECOUNT, a.CONFERENCEEDITOR, a.CONFERENCEORGANIZATION,a.CONFERENCEEDITORADDRESS,a.TRANSLATEDSOURCETITLE,a.VOLUMETITLE,a.DOI,a.ASSIG,a.CASREGISTRYNUMBER,a.APILT,a.APILT1,a.APICT,a.APICT1,a.APIAMS,a.SEQ_NUM,a.GRANTLIST,null as cafe_author,null as cafe_author1,null as cafe_affiliation,null as cafe_affiliation1,null as CAFE_CORRESPONDENCEAFFILIATION,null as authorid,null as affid,a.SOURCEBIBTEXT,a.STANDARDID,a.STANDARDDESIGNATION,a.NORMSTANDARDID,a.GRANTTEXT,a.ISOPENACESS from bd_master_orig a where a.database='cpx' and a.cafe_pui in(select pui from CAFE_weekly_DELETION)";
                //String sqlQuery ="select a.CHEMICALTERM,a.SPECIESTERM,a.REGIONALTERM,a.DATABASE,a.CITATIONLANGUAGE,a.CITATIONTITLE,a.CITTYPE,a.ABSTRACTDATA,a.PII,a.PUI,a.COPYRIGHT,a.M_ID,a.accessnumber,a.datesort,a.author,a.author_1,a.AFFILIATION,a.AFFILIATION_1,a.CORRESPONDENCEAFFILIATION,a.CODEN,a.ISSUE,a.CLASSIFICATIONCODE,a.CLASSIFICATIONDESC,a.CONTROLLEDTERM,a.UNCONTROLLEDTERM,a.MAINHEADING,a.TREATMENTCODE,a.LOADNUMBER,a.SOURCETYPE,a.SOURCECOUNTRY,a.SOURCEID,a.SOURCETITLE,a.SOURCETITLEABBREV,a.ISSUETITLE,a.ISSN,a.EISSN,a.ISBN,a.VOLUME,a.PAGE,a.PAGECOUNT,a.ARTICLENUMBER, substr(a.PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,a.PUBLICATIONDATE,a.EDITORS,a.PUBLISHERNAME,a.PUBLISHERADDRESS,a.PUBLISHERELECTRONICADDRESS,a.REPORTNUMBER,a.CONFNAME, a.CONFCATNUMBER,a.CONFCODE,a.CONFLOCATION,a.CONFDATE,a.CONFSPONSORS,a.CONFERENCEPARTNUMBER, a.CONFERENCEPAGERANGE, a.CONFERENCEPAGECOUNT, a.CONFERENCEEDITOR, a.CONFERENCEORGANIZATION,a.CONFERENCEEDITORADDRESS,a.TRANSLATEDSOURCETITLE,a.VOLUMETITLE,a.DOI,a.ASSIG,a.CASREGISTRYNUMBER,a.APILT,a.APILT1,a.APICT,a.APICT1,a.APIAMS,a.SEQ_NUM,a.GRANTLIST,null as cafe_author,null as cafe_author1,null as cafe_affiliation,null as cafe_affiliation1,null as CAFE_CORRESPONDENCEAFFILIATION,null as authorid,null as affid,a.SOURCEBIBTEXT,a.STANDARDID,a.STANDARDDESIGNATION,a.NORMSTANDARDID,a.GRANTTEXT from bd_master_orig a where a.database='cpx' and a.cafe_pui in(select pui from CAFE_weekly_DELETION)";
                System.out.println("sqlQuery="+sqlQuery);
                rs = stmt.executeQuery(sqlQuery);
                //rs = stmt.executeQuery("select CHEMICALTERM,SPECIESTERM,REGIONALTERM,DATABASE,CITATIONLANGUAGE,CITATIONTITLE,CITTYPE,ABSTRACTDATA,PII,PUI,COPYRIGHT,M_ID,accessnumber,datesort,author,author_1,AFFILIATION,AFFILIATION_1,CORRESPONDENCEAFFILIATION,CODEN,ISSUE,CLASSIFICATIONCODE,CLASSIFICATIONDESC,CONTROLLEDTERM,UNCONTROLLEDTERM,MAINHEADING,TREATMENTCODE,LOADNUMBER,SOURCETYPE,SOURCECOUNTRY,SOURCEID,SOURCETITLE,SOURCETITLEABBREV,ISSUETITLE,ISSN,EISSN,ISBN,VOLUME,PAGE,PAGECOUNT,ARTICLENUMBER, substr(PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,PUBLICATIONDATE,EDITORS,PUBLISHERNAME,PUBLISHERADDRESS,PUBLISHERELECTRONICADDRESS,REPORTNUMBER,CONFNAME, CONFCATNUMBER,CONFCODE,CONFLOCATION,CONFDATE,CONFSPONSORS,CONFERENCEPARTNUMBER, CONFERENCEPAGERANGE, CONFERENCEPAGECOUNT, CONFERENCEEDITOR, CONFERENCEORGANIZATION,CONFERENCEEDITORADDRESS,TRANSLATEDSOURCETITLE,VOLUMETITLE,DOI,ASSIG,CASREGISTRYNUMBER,APILT,APILT1,APICT,APICT1,APIAMS,SEQ_NUM from bd_master_orig where database='"+dbname+"' and m_id in (select mid from bd_master_numerical where mid is not null and loadnumber='"+updateNumber+"')");
                c.writeRecs(rs,con,updateNumber,"extractcafedelete",dbname);
            }
            else if(dbname.equals("cpx") && action.equalsIgnoreCase("extractcafemapping"))
            {
                System.out.println("Running the query...");
                writer.setOperation("add");
                //XmlCombiner c = new XmlCombiner(writer);
                //blocked ISOPENACESS at 10/24/2018
                String sqlQuery = "select a.UPDATENUMBER,a.CHEMICALTERM,a.SPECIESTERM,a.REGIONALTERM,a.DATABASE,a.CITATIONLANGUAGE,a.CITATIONTITLE,a.CITTYPE,a.ABSTRACTDATA,a.PII,a.PUI,a.COPYRIGHT,a.M_ID,a.accessnumber,a.datesort,a.author,a.author_1,a.AFFILIATION,a.AFFILIATION_1,a.CORRESPONDENCEAFFILIATION,a.CODEN,a.ISSUE,a.CLASSIFICATIONCODE,a.CLASSIFICATIONDESC,a.CONTROLLEDTERM,a.UNCONTROLLEDTERM,a.MAINHEADING,a.TREATMENTCODE,a.LOADNUMBER,a.SOURCETYPE,a.SOURCECOUNTRY,a.SOURCEID,a.SOURCETITLE,a.SOURCETITLEABBREV,a.ISSUETITLE,a.ISSN,a.EISSN,a.ISBN,a.VOLUME,a.PAGE,a.PAGECOUNT,a.ARTICLENUMBER, substr(a.PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,a.PUBLICATIONDATE,a.EDITORS,a.PUBLISHERNAME,a.PUBLISHERADDRESS,a.PUBLISHERELECTRONICADDRESS,a.REPORTNUMBER,a.CONFNAME, a.CONFCATNUMBER,a.CONFCODE,a.CONFLOCATION,a.CONFDATE,a.CONFSPONSORS,a.CONFERENCEPARTNUMBER, a.CONFERENCEPAGERANGE, a.CONFERENCEPAGECOUNT, a.CONFERENCEEDITOR, a.CONFERENCEORGANIZATION,a.CONFERENCEEDITORADDRESS,a.TRANSLATEDSOURCETITLE,a.VOLUMETITLE,a.DOI,a.ASSIG,a.CASREGISTRYNUMBER,a.APILT,a.APILT1,a.APICT,a.APICT1,a.APIAMS,a.SEQ_NUM,a.GRANTLIST,b.author as cafe_author,b.author_1 as cafe_author1,b.affiliation as cafe_affiliation,b.affiliation_1 as cafe_affiliation1,b.CORRESPONDENCEAFFILIATION as CAFE_CORRESPONDENCEAFFILIATION,null as authorid,null as affid,a.SOURCEBIBTEXT,a.STANDARDID,a.STANDARDDESIGNATION,a.NORMSTANDARDID,a.GRANTTEXT,a.ISOPENACESS, count(*) over() totalCount from bd_master_orig a,cafe_master b,bd_cafe_mapping c where a.database='cpx' and a.updateNumber='"+updateNumber+"' and a.pui=c.bd_pui and b.pui=c.cafe_pui and a.publicationyear is not null";
                //String sqlQuery = "select a.CHEMICALTERM,a.SPECIESTERM,a.REGIONALTERM,a.DATABASE,a.CITATIONLANGUAGE,a.CITATIONTITLE,a.CITTYPE,a.ABSTRACTDATA,a.PII,a.PUI,a.COPYRIGHT,a.M_ID,a.accessnumber,a.datesort,a.author,a.author_1,a.AFFILIATION,a.AFFILIATION_1,a.CORRESPONDENCEAFFILIATION,a.CODEN,a.ISSUE,a.CLASSIFICATIONCODE,a.CLASSIFICATIONDESC,a.CONTROLLEDTERM,a.UNCONTROLLEDTERM,a.MAINHEADING,a.TREATMENTCODE,a.LOADNUMBER,a.SOURCETYPE,a.SOURCECOUNTRY,a.SOURCEID,a.SOURCETITLE,a.SOURCETITLEABBREV,a.ISSUETITLE,a.ISSN,a.EISSN,a.ISBN,a.VOLUME,a.PAGE,a.PAGECOUNT,a.ARTICLENUMBER, substr(a.PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,a.PUBLICATIONDATE,a.EDITORS,a.PUBLISHERNAME,a.PUBLISHERADDRESS,a.PUBLISHERELECTRONICADDRESS,a.REPORTNUMBER,a.CONFNAME, a.CONFCATNUMBER,a.CONFCODE,a.CONFLOCATION,a.CONFDATE,a.CONFSPONSORS,a.CONFERENCEPARTNUMBER, a.CONFERENCEPAGERANGE, a.CONFERENCEPAGECOUNT, a.CONFERENCEEDITOR, a.CONFERENCEORGANIZATION,a.CONFERENCEEDITORADDRESS,a.TRANSLATEDSOURCETITLE,a.VOLUMETITLE,a.DOI,a.ASSIG,a.CASREGISTRYNUMBER,a.APILT,a.APILT1,a.APICT,a.APICT1,a.APIAMS,a.SEQ_NUM,a.GRANTLIST,b.author as cafe_author,b.author_1 as cafe_author1,b.affiliation as cafe_affiliation,b.affiliation_1 as cafe_affiliation1,b.CORRESPONDENCEAFFILIATION as CAFE_CORRESPONDENCEAFFILIATION,null as authorid,null as affid,a.SOURCEBIBTEXT,a.STANDARDID,a.STANDARDDESIGNATION,a.NORMSTANDARDID,a.GRANTTEXT from bd_master_orig a,cafe_master b,bd_cafe_mapping c where a.database='cpx' and a.updateNumber='"+updateNumber+"' and a.pui=c.bd_pui and b.pui=c.cafe_pui";
                System.out.println("sqlQuery="+sqlQuery);
                rs = stmt.executeQuery(sqlQuery);
                //rs = stmt.executeQuery("select CHEMICALTERM,SPECIESTERM,REGIONALTERM,DATABASE,CITATIONLANGUAGE,CITATIONTITLE,CITTYPE,ABSTRACTDATA,PII,PUI,COPYRIGHT,M_ID,accessnumber,datesort,author,author_1,AFFILIATION,AFFILIATION_1,CORRESPONDENCEAFFILIATION,CODEN,ISSUE,CLASSIFICATIONCODE,CLASSIFICATIONDESC,CONTROLLEDTERM,UNCONTROLLEDTERM,MAINHEADING,TREATMENTCODE,LOADNUMBER,SOURCETYPE,SOURCECOUNTRY,SOURCEID,SOURCETITLE,SOURCETITLEABBREV,ISSUETITLE,ISSN,EISSN,ISBN,VOLUME,PAGE,PAGECOUNT,ARTICLENUMBER, substr(PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,PUBLICATIONDATE,EDITORS,PUBLISHERNAME,PUBLISHERADDRESS,PUBLISHERELECTRONICADDRESS,REPORTNUMBER,CONFNAME, CONFCATNUMBER,CONFCODE,CONFLOCATION,CONFDATE,CONFSPONSORS,CONFERENCEPARTNUMBER, CONFERENCEPAGERANGE, CONFERENCEPAGECOUNT, CONFERENCEEDITOR, CONFERENCEORGANIZATION,CONFERENCEEDITORADDRESS,TRANSLATEDSOURCETITLE,VOLUMETITLE,DOI,ASSIG,CASREGISTRYNUMBER,APILT,APILT1,APICT,APICT1,APIAMS,SEQ_NUM from bd_master_orig where database='"+dbname+"' and m_id in (select mid from bd_master_numerical where mid is not null and loadnumber='"+updateNumber+"')");
                c.writeRecs(rs,con,updateNumber,"extractcafemapping",dbname);
            }
            else if(action.equalsIgnoreCase("extractnumerical"))
            {
                System.out.println("Running the query...");
                writer.setOperation("add");
                //XmlCombiner c = new XmlCombiner(writer);
                //blocked ISOPENACESS at 10/24/2018
                String sqlQuery = "select a.UPDATENUMBER,a.CHEMICALTERM,a.SPECIESTERM,a.REGIONALTERM,a.DATABASE,a.CITATIONLANGUAGE,a.CITATIONTITLE,a.CITTYPE,a.ABSTRACTDATA,a.PII,a.PUI,a.COPYRIGHT,a.M_ID,a.accessnumber,a.datesort,a.author,a.author_1,a.AFFILIATION,a.AFFILIATION_1,a.CORRESPONDENCEAFFILIATION,a.CODEN,a.ISSUE,a.CLASSIFICATIONCODE,a.CLASSIFICATIONDESC,a.CONTROLLEDTERM,a.UNCONTROLLEDTERM,a.MAINHEADING,a.TREATMENTCODE,a.LOADNUMBER,a.SOURCETYPE,a.SOURCECOUNTRY,a.SOURCEID,a.SOURCETITLE,a.SOURCETITLEABBREV,a.ISSUETITLE,a.ISSN,a.EISSN,a.ISBN,a.VOLUME,a.PAGE,a.PAGECOUNT,a.ARTICLENUMBER, substr(a.PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,a.PUBLICATIONDATE,a.EDITORS,a.PUBLISHERNAME,a.PUBLISHERADDRESS,a.PUBLISHERELECTRONICADDRESS,a.REPORTNUMBER,a.CONFNAME, a.CONFCATNUMBER,a.CONFCODE,a.CONFLOCATION,a.CONFDATE,a.CONFSPONSORS,a.CONFERENCEPARTNUMBER, a.CONFERENCEPAGERANGE, a.CONFERENCEPAGECOUNT, a.CONFERENCEEDITOR, a.CONFERENCEORGANIZATION,a.CONFERENCEEDITORADDRESS,a.TRANSLATEDSOURCETITLE,a.VOLUMETITLE,a.DOI,a.ASSIG,a.CASREGISTRYNUMBER,a.APILT,a.APILT1,a.APICT,a.APICT1,a.APIAMS,a.SEQ_NUM,a.GRANTLIST,b.author as cafe_author,b.author_1 as cafe_author1,b.affiliation as cafe_affiliation,b.affiliation_1 as cafe_affiliation1,b.CORRESPONDENCEAFFILIATION as CAFE_CORRESPONDENCEAFFILIATION,null as authorid,null as affid,a.SOURCEBIBTEXT,a.STANDARDID,a.STANDARDDESIGNATION,a.NORMSTANDARDID,a.GRANTTEXT,a.ISOPENACESS, count(*) over() totalCount from bd_master_orig a left outer join CAFE_PUI_LIST_MASTER c on a.pui= c.puisecondary left outer join cafe_master b on b.pui=c.pui where a.database='"+dbname+"' and a.publicationyear is not null and a.m_id in (select distinct mid from bd_master_numerical where mid is not null and loadnumber='"+updateNumber+"')";
                //String sqlQuery = "select a.CHEMICALTERM,a.SPECIESTERM,a.REGIONALTERM,a.DATABASE,a.CITATIONLANGUAGE,a.CITATIONTITLE,a.CITTYPE,a.ABSTRACTDATA,a.PII,a.PUI,a.COPYRIGHT,a.M_ID,a.accessnumber,a.datesort,a.author,a.author_1,a.AFFILIATION,a.AFFILIATION_1,a.CORRESPONDENCEAFFILIATION,a.CODEN,a.ISSUE,a.CLASSIFICATIONCODE,a.CLASSIFICATIONDESC,a.CONTROLLEDTERM,a.UNCONTROLLEDTERM,a.MAINHEADING,a.TREATMENTCODE,a.LOADNUMBER,a.SOURCETYPE,a.SOURCECOUNTRY,a.SOURCEID,a.SOURCETITLE,a.SOURCETITLEABBREV,a.ISSUETITLE,a.ISSN,a.EISSN,a.ISBN,a.VOLUME,a.PAGE,a.PAGECOUNT,a.ARTICLENUMBER, substr(a.PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,a.PUBLICATIONDATE,a.EDITORS,a.PUBLISHERNAME,a.PUBLISHERADDRESS,a.PUBLISHERELECTRONICADDRESS,a.REPORTNUMBER,a.CONFNAME, a.CONFCATNUMBER,a.CONFCODE,a.CONFLOCATION,a.CONFDATE,a.CONFSPONSORS,a.CONFERENCEPARTNUMBER, a.CONFERENCEPAGERANGE, a.CONFERENCEPAGECOUNT, a.CONFERENCEEDITOR, a.CONFERENCEORGANIZATION,a.CONFERENCEEDITORADDRESS,a.TRANSLATEDSOURCETITLE,a.VOLUMETITLE,a.DOI,a.ASSIG,a.CASREGISTRYNUMBER,a.APILT,a.APILT1,a.APICT,a.APICT1,a.APIAMS,a.SEQ_NUM,a.GRANTLIST,b.author as cafe_author,b.author_1 as cafe_author1,b.affiliation as cafe_affiliation,b.affiliation_1 as cafe_affiliation1,b.CORRESPONDENCEAFFILIATION as CAFE_CORRESPONDENCEAFFILIATION,null as authorid,null as affid,a.SOURCEBIBTEXT,a.STANDARDID,a.STANDARDDESIGNATION,a.NORMSTANDARDID,a.GRANTTEXT,a.ISOPENACESS from bd_master_orig a left outer join cafe_master b on a.cafe_pui = b.pui where a.database='"+dbname+"' and a.m_id in (select distinct mid from bd_master_numerical where mid is not null and loadnumber='"+updateNumber+"')";
                //String sqlQuery = "select a.CHEMICALTERM,a.SPECIESTERM,a.REGIONALTERM,a.DATABASE,a.CITATIONLANGUAGE,a.CITATIONTITLE,a.CITTYPE,a.ABSTRACTDATA,a.PII,a.PUI,a.COPYRIGHT,a.M_ID,a.accessnumber,a.datesort,a.author,a.author_1,a.AFFILIATION,a.AFFILIATION_1,a.CORRESPONDENCEAFFILIATION,a.CODEN,a.ISSUE,a.CLASSIFICATIONCODE,a.CLASSIFICATIONDESC,a.CONTROLLEDTERM,a.UNCONTROLLEDTERM,a.MAINHEADING,a.TREATMENTCODE,a.LOADNUMBER,a.SOURCETYPE,a.SOURCECOUNTRY,a.SOURCEID,a.SOURCETITLE,a.SOURCETITLEABBREV,a.ISSUETITLE,a.ISSN,a.EISSN,a.ISBN,a.VOLUME,a.PAGE,a.PAGECOUNT,a.ARTICLENUMBER, substr(a.PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,a.PUBLICATIONDATE,a.EDITORS,a.PUBLISHERNAME,a.PUBLISHERADDRESS,a.PUBLISHERELECTRONICADDRESS,a.REPORTNUMBER,a.CONFNAME, a.CONFCATNUMBER,a.CONFCODE,a.CONFLOCATION,a.CONFDATE,a.CONFSPONSORS,a.CONFERENCEPARTNUMBER, a.CONFERENCEPAGERANGE, a.CONFERENCEPAGECOUNT, a.CONFERENCEEDITOR, a.CONFERENCEORGANIZATION,a.CONFERENCEEDITORADDRESS,a.TRANSLATEDSOURCETITLE,a.VOLUMETITLE,a.DOI,a.ASSIG,a.CASREGISTRYNUMBER,a.APILT,a.APILT1,a.APICT,a.APICT1,a.APIAMS,a.SEQ_NUM,a.GRANTLIST,b.author as cafe_author,b.author_1 as cafe_author1,b.affiliation as cafe_affiliation,b.affiliation_1 as cafe_affiliation1,b.CORRESPONDENCEAFFILIATION as CAFE_CORRESPONDENCEAFFILIATION,null as authorid,null as affid,a.SOURCEBIBTEXT,a.STANDARDID,a.STANDARDDESIGNATION,a.NORMSTANDARDID,a.GRANTTEXT from bd_master_orig a left outer join cafe_master b on a.cafe_pui = b.pui where a.database='"+dbname+"' and a.m_id in (select distinct mid from bd_master_numerical where mid is not null and loadnumber='"+updateNumber+"')";
                //use accessnumber to join cafe_master with bd_master 11/20/2018
                //String sqlQuery = "select a.CHEMICALTERM,a.SPECIESTERM,a.REGIONALTERM,a.DATABASE,a.CITATIONLANGUAGE,a.CITATIONTITLE,a.CITTYPE,a.ABSTRACTDATA,a.PII,a.PUI,a.COPYRIGHT,a.M_ID,a.accessnumber,a.datesort,a.author,a.author_1,a.AFFILIATION,a.AFFILIATION_1,a.CORRESPONDENCEAFFILIATION,a.CODEN,a.ISSUE,a.CLASSIFICATIONCODE,a.CLASSIFICATIONDESC,a.CONTROLLEDTERM,a.UNCONTROLLEDTERM,a.MAINHEADING,a.TREATMENTCODE,a.LOADNUMBER,a.SOURCETYPE,a.SOURCECOUNTRY,a.SOURCEID,a.SOURCETITLE,a.SOURCETITLEABBREV,a.ISSUETITLE,a.ISSN,a.EISSN,a.ISBN,a.VOLUME,a.PAGE,a.PAGECOUNT,a.ARTICLENUMBER, substr(a.PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,a.PUBLICATIONDATE,a.EDITORS,a.PUBLISHERNAME,a.PUBLISHERADDRESS,a.PUBLISHERELECTRONICADDRESS,a.REPORTNUMBER,a.CONFNAME, a.CONFCATNUMBER,a.CONFCODE,a.CONFLOCATION,a.CONFDATE,a.CONFSPONSORS,a.CONFERENCEPARTNUMBER, a.CONFERENCEPAGERANGE, a.CONFERENCEPAGECOUNT, a.CONFERENCEEDITOR, a.CONFERENCEORGANIZATION,a.CONFERENCEEDITORADDRESS,a.TRANSLATEDSOURCETITLE,a.VOLUMETITLE,a.DOI,a.ASSIG,a.CASREGISTRYNUMBER,a.APILT,a.APILT1,a.APICT,a.APICT1,a.APIAMS,a.SEQ_NUM,a.GRANTLIST,b.author as cafe_author,b.author_1 as cafe_author1,b.affiliation as cafe_affiliation,b.affiliation_1 as cafe_affiliation1,b.CORRESPONDENCEAFFILIATION as CAFE_CORRESPONDENCEAFFILIATION,null as authorid,null as affid,a.SOURCEBIBTEXT,a.STANDARDID,a.STANDARDDESIGNATION,a.NORMSTANDARDID,a.GRANTTEXT from bd_master_orig a left outer join cafe_master b on a.accessnumber = b.accessnumber where a.database='"+dbname+"' and a.m_id in (select distinct mid from bd_master_numerical where mid is not null and loadnumber='"+updateNumber+"')";
                System.out.println("sqlQuery="+sqlQuery);
                rs = stmt.executeQuery(sqlQuery);
                //rs = stmt.executeQuery("select CHEMICALTERM,SPECIESTERM,REGIONALTERM,DATABASE,CITATIONLANGUAGE,CITATIONTITLE,CITTYPE,ABSTRACTDATA,PII,PUI,COPYRIGHT,M_ID,accessnumber,datesort,author,author_1,AFFILIATION,AFFILIATION_1,CORRESPONDENCEAFFILIATION,CODEN,ISSUE,CLASSIFICATIONCODE,CLASSIFICATIONDESC,CONTROLLEDTERM,UNCONTROLLEDTERM,MAINHEADING,TREATMENTCODE,LOADNUMBER,SOURCETYPE,SOURCECOUNTRY,SOURCEID,SOURCETITLE,SOURCETITLEABBREV,ISSUETITLE,ISSN,EISSN,ISBN,VOLUME,PAGE,PAGECOUNT,ARTICLENUMBER, substr(PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,PUBLICATIONDATE,EDITORS,PUBLISHERNAME,PUBLISHERADDRESS,PUBLISHERELECTRONICADDRESS,REPORTNUMBER,CONFNAME, CONFCATNUMBER,CONFCODE,CONFLOCATION,CONFDATE,CONFSPONSORS,CONFERENCEPARTNUMBER, CONFERENCEPAGERANGE, CONFERENCEPAGECOUNT, CONFERENCEEDITOR, CONFERENCEORGANIZATION,CONFERENCEEDITORADDRESS,TRANSLATEDSOURCETITLE,VOLUMETITLE,DOI,ASSIG,CASREGISTRYNUMBER,APILT,APILT1,APICT,APICT1,APIAMS,SEQ_NUM from bd_master_orig where database='"+dbname+"' and m_id in (select mid from bd_master_numerical where mid is not null and loadnumber='"+updateNumber+"')");
                c.writeRecs(rs,con,updateNumber,"extractnumerical",dbname);
            }
            else if(dbname.equals("cpx") && action.equalsIgnoreCase("extractauthorlookupindex"))
            {
                System.out.println("Running the query...");
                writer.setOperation("add");
                //XmlCombiner c = new XmlCombiner(writer);
                //blocked ISOPENACESS at 10/24/2018
                String sqlQuery = "select a.UPDATENUMBER,a.CHEMICALTERM,a.SPECIESTERM,a.REGIONALTERM,a.DATABASE,a.CITATIONLANGUAGE,a.CITATIONTITLE,a.CITTYPE,a.ABSTRACTDATA,a.PII,a.PUI,a.COPYRIGHT,a.M_ID,a.accessnumber,a.datesort,a.author,a.author_1,a.AFFILIATION,a.AFFILIATION_1,a.CORRESPONDENCEAFFILIATION,a.CODEN,a.ISSUE,a.CLASSIFICATIONCODE,a.CLASSIFICATIONDESC,a.CONTROLLEDTERM,a.UNCONTROLLEDTERM,a.MAINHEADING,a.TREATMENTCODE,a.LOADNUMBER,a.SOURCETYPE,a.SOURCECOUNTRY,a.SOURCEID,a.SOURCETITLE,a.SOURCETITLEABBREV,a.ISSUETITLE,a.ISSN,a.EISSN,a.ISBN,a.VOLUME,a.PAGE,a.PAGECOUNT,a.ARTICLENUMBER, substr(a.PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,a.PUBLICATIONDATE,a.EDITORS,a.PUBLISHERNAME,a.PUBLISHERADDRESS,a.PUBLISHERELECTRONICADDRESS,a.REPORTNUMBER,a.CONFNAME, a.CONFCATNUMBER,a.CONFCODE,a.CONFLOCATION,a.CONFDATE,a.CONFSPONSORS,a.CONFERENCEPARTNUMBER, a.CONFERENCEPAGERANGE, a.CONFERENCEPAGECOUNT, a.CONFERENCEEDITOR, a.CONFERENCEORGANIZATION,a.CONFERENCEEDITORADDRESS,a.TRANSLATEDSOURCETITLE,a.VOLUMETITLE,a.DOI,a.ASSIG,a.CASREGISTRYNUMBER,a.APILT,a.APILT1,a.APICT,a.APICT1,a.APIAMS,a.SEQ_NUM,a.GRANTLIST,b.authorid,b.affid,'cafeauthor','cafeaffiliation' ,a.SOURCEBIBTEXT,a.STANDARDID,a.STANDARDDESIGNATION,a.NORMSTANDARDID,a.GRANTTEXT,a.ISOPENACESS from bd_master_orig a,(select a.pui pui, rtrim(xmlagg(XMLELEMENT(e,b.author_id,',').EXTRACT('//text()')).GetClobVal(),',') authorid,rtrim(xmlagg(XMLELEMENT(e,c.institute_id,',').EXTRACT('//text()')).GetClobVal(),',') affid, count(*) over() totalCount from bd_master_orig a,cafe_au_lookup b,cafe_af_lookup c where (b.loadnumber="+updateNumber+" or c.loadnumber="+updateNumber+") and a.pui=b.pui and a.pui=c.pui group by a.pui) b where a.pui=b.pui";
                //String sqlQuery = "select a.CHEMICALTERM,a.SPECIESTERM,a.REGIONALTERM,a.DATABASE,a.CITATIONLANGUAGE,a.CITATIONTITLE,a.CITTYPE,a.ABSTRACTDATA,a.PII,a.PUI,a.COPYRIGHT,a.M_ID,a.accessnumber,a.datesort,a.author,a.author_1,a.AFFILIATION,a.AFFILIATION_1,a.CORRESPONDENCEAFFILIATION,a.CODEN,a.ISSUE,a.CLASSIFICATIONCODE,a.CLASSIFICATIONDESC,a.CONTROLLEDTERM,a.UNCONTROLLEDTERM,a.MAINHEADING,a.TREATMENTCODE,a.LOADNUMBER,a.SOURCETYPE,a.SOURCECOUNTRY,a.SOURCEID,a.SOURCETITLE,a.SOURCETITLEABBREV,a.ISSUETITLE,a.ISSN,a.EISSN,a.ISBN,a.VOLUME,a.PAGE,a.PAGECOUNT,a.ARTICLENUMBER, substr(a.PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,a.PUBLICATIONDATE,a.EDITORS,a.PUBLISHERNAME,a.PUBLISHERADDRESS,a.PUBLISHERELECTRONICADDRESS,a.REPORTNUMBER,a.CONFNAME, a.CONFCATNUMBER,a.CONFCODE,a.CONFLOCATION,a.CONFDATE,a.CONFSPONSORS,a.CONFERENCEPARTNUMBER, a.CONFERENCEPAGERANGE, a.CONFERENCEPAGECOUNT, a.CONFERENCEEDITOR, a.CONFERENCEORGANIZATION,a.CONFERENCEEDITORADDRESS,a.TRANSLATEDSOURCETITLE,a.VOLUMETITLE,a.DOI,a.ASSIG,a.CASREGISTRYNUMBER,a.APILT,a.APILT1,a.APICT,a.APICT1,a.APIAMS,a.SEQ_NUM,a.GRANTLIST,b.authorid,b.affid,'cafeauthor','cafeaffiliation' ,a.SOURCEBIBTEXT,a.STANDARDID,a.STANDARDDESIGNATION,a.NORMSTANDARDID,a.GRANTTEXT from bd_master_orig a,(select a.pui pui, rtrim(xmlagg(XMLELEMENT(e,b.author_id,',').EXTRACT('//text()')).GetClobVal(),',') authorid,rtrim(xmlagg(XMLELEMENT(e,c.institute_id,',').EXTRACT('//text()')).GetClobVal(),',') affid from bd_master_orig a,cafe_au_lookup b,cafe_af_lookup c where (b.loadnumber="+updateNumber+" or c.loadnumber="+updateNumber+") and a.pui=b.pui and a.pui=c.pui group by a.pui) b where a.pui=b.pui";
                System.out.println("sqlQuery="+sqlQuery);
                //rs = stmt.executeQuery("select CHEMICALTERM,SPECIESTERM,REGIONALTERM,DATABASE,CITATIONLANGUAGE,CITATIONTITLE,CITTYPE,ABSTRACTDATA,PII,PUI,COPYRIGHT,M_ID,accessnumber,datesort,author,author_1,AFFILIATION,AFFILIATION_1,CORRESPONDENCEAFFILIATION,CODEN,ISSUE,CLASSIFICATIONCODE,CLASSIFICATIONDESC,CONTROLLEDTERM,UNCONTROLLEDTERM,MAINHEADING,TREATMENTCODE,LOADNUMBER,SOURCETYPE,SOURCECOUNTRY,SOURCEID,SOURCETITLE,SOURCETITLEABBREV,ISSUETITLE,ISSN,EISSN,ISBN,VOLUME,PAGE,PAGECOUNT,ARTICLENUMBER, substr(PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,PUBLICATIONDATE,EDITORS,PUBLISHERNAME,PUBLISHERADDRESS,PUBLISHERELECTRONICADDRESS,REPORTNUMBER,CONFNAME, CONFCATNUMBER,CONFCODE,CONFLOCATION,CONFDATE,CONFSPONSORS,CONFERENCEPARTNUMBER, CONFERENCEPAGERANGE, CONFERENCEPAGECOUNT, CONFERENCEEDITOR, CONFERENCEORGANIZATION,CONFERENCEEDITORADDRESS,TRANSLATEDSOURCETITLE,VOLUMETITLE,DOI,ASSIG,CASREGISTRYNUMBER,APILT,APILT1,APICT,APICT1,APIAMS,SEQ_NUM,GRANTLIST from bd_master_orig where database='"+dbname+"' and pui in (select distinct pui from cafe_au_lookup where loadnumber="+updateNumber+")");
                //rs = stmt.executeQuery("select CHEMICALTERM,SPECIESTERM,REGIONALTERM,DATABASE,CITATIONLANGUAGE,CITATIONTITLE,CITTYPE,ABSTRACTDATA,PII,PUI,COPYRIGHT,M_ID,accessnumber,datesort,author,author_1,AFFILIATION,AFFILIATION_1,CORRESPONDENCEAFFILIATION,CODEN,ISSUE,CLASSIFICATIONCODE,CLASSIFICATIONDESC,CONTROLLEDTERM,UNCONTROLLEDTERM,MAINHEADING,TREATMENTCODE,LOADNUMBER,SOURCETYPE,SOURCECOUNTRY,SOURCEID,SOURCETITLE,SOURCETITLEABBREV,ISSUETITLE,ISSN,EISSN,ISBN,VOLUME,PAGE,PAGECOUNT,ARTICLENUMBER, substr(PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,PUBLICATIONDATE,EDITORS,PUBLISHERNAME,PUBLISHERADDRESS,PUBLISHERELECTRONICADDRESS,REPORTNUMBER,CONFNAME, CONFCATNUMBER,CONFCODE,CONFLOCATION,CONFDATE,CONFSPONSORS,CONFERENCEPARTNUMBER, CONFERENCEPAGERANGE, CONFERENCEPAGECOUNT, CONFERENCEEDITOR, CONFERENCEORGANIZATION,CONFERENCEEDITORADDRESS,TRANSLATEDSOURCETITLE,VOLUMETITLE,DOI,ASSIG,CASREGISTRYNUMBER,APILT,APILT1,APICT,APICT1,APIAMS,SEQ_NUM from bd_master_orig where database='"+dbname+"' and m_id in (select mid from bd_master_numerical where mid is not null and loadnumber='"+updateNumber+"')");                         
                rs = stmt.executeQuery(sqlQuery);
                c.writeRecs(rs,con,updateNumber,"extractauthorlookupindex",dbname);
            }
            else if(action.equalsIgnoreCase("delete") || action.equalsIgnoreCase("extractdelete"))
            {
                writer.setOperation("delete");
                String deleteString = "select m_id from bd_master_orig where database='"+dbname+"' and updateNumber='"+updateNumber+"' and accessnumber in (select 'D'||accessnumber from "+tempTable+")";
                System.out.println("Deleting SQL String="+deleteString);
                String countString = "select count(*) count from bd_master_orig where database='"+dbname+"' and updateNumber='"+updateNumber+"' and accessnumber in (select 'D'||accessnumber from "+tempTable+")";
                rs = stmt.executeQuery(countString);
                int deleteSize=0;
                while (rs.next())
                {
                    if(rs.getInt("count") >0)
                    {
                        deleteSize = rs.getInt("count");
                    }
                }
                
                //HH added 11/15/2021 to handle resource leake issue
                try
           	 	{
           		 	rs.close();
           	 	}
           	 	catch(SQLException  ex)
           	 	{
           	 		System.out.println("Failed to close RS for delete/extractdelete!");
           	 		ex.printStackTrace();
           	 	}
                
                if(deleteSize>0)
                {                		               
                	rs = stmt.executeQuery(deleteString);
	                List deleteRecords = creatDeleteFile(rs,dbname,updateNumber);
	                if(this.propertyFileName!=null)
	                {
	                	sendDeleteToKafka(deleteRecords);
	                }
	                //writer.zipBatch(); //no need to zip a delete file after switch to ES modified by hmo @10/29/2020
	                
                }
                else
                {
                	System.out.println("the records needed to delete is not in the database");
                }
            }
            //HH added 11/15/2021 for adhoc deletion requests (only for OBII/ not cafe)
            else if(action.equalsIgnoreCase("adhocextractdelete"))
            {
            	 writer.setOperation("delete");
            	 // check count first
            	 String countQuery = "select count(*) as count from " + adhocDelTableName;
            	 rs = stmt.executeQuery(countQuery);
            	 int recCount = 0;
            	 
            	 //Fetch ID of records to be deleted from ES
            	 String records_id = "select m_id from " + adhocDelTableName;
            	 
            	 while(rs.next())
            	 {
            		 if(rs.getInt("count") >0)
            		 {
            			recCount = rs.getInt("COUNT"); 
            		 }
            	 }
            	 try
            	 {
            		 rs.close();
            	 }
            	 catch(SQLException  ex)
            	 {
            		 System.out.println("Failed to close RS for adhocdeletion!");
            		 ex.printStackTrace();
            	 }
            	 if(recCount >0)
            	 {
            		 rs = stmt.executeQuery(records_id);
            		 ArrayList<String> ids = new ArrayList<>();
            		 while(rs.next())
            			 ids.add(rs.getString("M_ID"));
            		 if(propertyFileName!=null)
 	                {
 	                	sendDeleteToKafka(ids);
 	                }
            		 
            	 }
            }
            //c.writeRecs(rs,con,updateNumber,"bd_master_orig",dbname);
            writer.end();
            writer.flush();
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
    
    private void sendDeleteToKafka(List rs)
    {
    	//KafkaService kafka = new KafkaService();
    	long processTime = System.currentTimeMillis();
    	String processInfo=processTime+"_"+this.database+"_"+updateNumber;
    	KafkaService kafka = new KafkaService(processInfo, this.propertyFileName); //use it for ES extraction
    	try
    	{
	    	String eid="";
	    	for (int i=0;i<rs.size();i++)
            {
	    		eid=(String)rs.get(i);
                if(eid != null)
                {                   
                    kafka.runProducer("{}",eid,0,new HashMap());
                    System.out.println("EID="+eid);
                }
            }
	    	
    	}
    	catch(Exception e)
        {
        	e.printStackTrace();
        }
        finally
        {
        	try
        	{
		        if(kafka!=null)
		        { 	        		        
		        	kafka.close();        
		        }
        	}
        	catch(Exception e)
            {
            	e.printStackTrace();
            }
        }      
    }

    private List creatDeleteFile(ResultSet rs,String database,int updateNumber)
    {
    	List outputList=new ArrayList();
        String batchidFormat = "0000";
        String batchID = "0001";
        String numberID = "0000";
        File file=new File("fast");
        FileWriter out= null;
        CombinedWriter writer = new CombinedXMLWriter(10000,10000,database);

        try
        {
            if(!file.exists())
            {
                file.mkdir();
            }

            long starttime = System.currentTimeMillis();
            String batchPath = "fast/batch_" + updateNumber+"_"+batchID;

            file=new File(batchPath);
            if(!file.exists())
            {
                file.mkdir();
            }
            String root = batchPath +"/EIDATA";
            file=new File(root);

            if(!file.exists())
            {
                file.mkdir();
            }
            
            String tmp = root +"/tmp";
            file=new File(tmp);

            if(!file.exists())
            {
                file.mkdir();
            }

            file = new File(tmp+"/delete.txt");
            System.out.println("FILE="+file);

            if(!file.exists())
            {
                file.createNewFile();
            }
            out = new FileWriter(file);
            while (rs.next())
            {
            	String mid=rs.getString("M_ID");
                if(mid != null)
                {
                	outputList.add(mid);
                    out.write(mid+"\n");
                }
            }
            out.flush();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(rs !=null)
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

            if(out !=null)
            {
                try
                {
                    out.close();
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }

        }
        return outputList;

    }

    private void runCorrection(String fileName,int updateNumber,String database,String action)
    {
    	long midTime = System.currentTimeMillis();
        CallableStatement pstmt = null;
        boolean blnResult = false;
        try
        {

            if(test)
            {
                System.out.println("begin to execute stored procedure update_bd_backup_table");
                System.out.println("press enter to continue");
                System.in.read();
                Thread.currentThread().sleep(1000);
                
            }

            if(action != null && action.equalsIgnoreCase("aip") && database.equalsIgnoreCase("cpx"))
            {
                pstmt = con.prepareCall("{ call update_aip_backup_table(?,?)}");
                pstmt.setInt(1,updateNumber);
                pstmt.setString(2,database);
                pstmt.executeUpdate();
            }
            else
            {
                pstmt = con.prepareCall("{ call update_bd_backup_table(?,?)}");
                pstmt.setInt(1,updateNumber);
                pstmt.setString(2,database);
                pstmt.executeUpdate();
            }

            midTime = endTime;
            endTime = System.currentTimeMillis();
            System.out.println("time for update_bd_backup_table "+(endTime-midTime)/1000.0+" seconds");
            System.out.println("total time used "+(endTime-startTime)/1000.0+" seconds");
            
            if(action != null && action.equalsIgnoreCase("update"))
            {
                //System.out.println("updateNumber= "+updateNumber+" fileName= "+fileName+" database= "+database);
                if(test)
                {
                    System.out.println("begin to execute stored procedure update_bd_temp_table");
                    System.out.println("press enter to continue");
                    System.in.read();
                    Thread.currentThread().sleep(1000);
                }
                pstmt = con.prepareCall("{ call update_bd_temp_table(?,?,?)}");
                pstmt.setInt(1,updateNumber);
                pstmt.setString(2,fileName);
                pstmt.setString(3,database);
                pstmt.executeUpdate();

                midTime = endTime;
                endTime = System.currentTimeMillis();
                System.out.println("time for update_bd_temp_table "+(endTime-midTime)/1000.0+" seconds");
                System.out.println("total time used "+(endTime-startTime)/1000.0+" seconds");
                
                if(test)
                {
                    System.out.println("begin to execute stored procedure update_bd_master_table");
                    System.out.println("press enter to continue");
                    System.in.read();
                    Thread.currentThread().sleep(1000);
                }
                pstmt = con.prepareCall("{ call update_bd_master_table(?,?)}");
                pstmt.setInt(1,updateNumber);
                pstmt.setString(2,database);
                pstmt.executeUpdate();
                
                midTime = endTime;
                endTime = System.currentTimeMillis();
                System.out.println("time for update_bd_master_table "+(endTime-midTime)/1000.0+" seconds");
                System.out.println("total time used "+(endTime-startTime)/1000.0+" seconds");

				if(test)
				{
					System.out.println("begin to execute stored procedure update_bd_reference_table");
					System.out.println("press enter to continue");
					System.in.read();
					Thread.currentThread().sleep(1000);
				}
				
				if(database.equals("cpx"))
				{
					pstmt = con.prepareCall("{ call update_bd_reference_table(?,?)}");
					pstmt.setInt(1,updateNumber);
					pstmt.setString(2,database);
	                pstmt.executeUpdate();
				}
				
				 midTime = endTime;
	             endTime = System.currentTimeMillis();
	             System.out.println("time for update_bd_reference_table "+(endTime-midTime)/1000.0+" seconds");
	             System.out.println("total time used "+(endTime-startTime)/1000.0+" seconds");

            }
            else if(action != null && action.equalsIgnoreCase("aip"))
            {
                if(test)
                {
                    System.out.println("begin to execute stored procedure update_aip_temp_table");
                    System.out.println("press enter to continue");
                    System.in.read();
                    Thread.currentThread().sleep(1000);
                }
                pstmt = con.prepareCall("{ call update_aip_temp_table(?,?,?)}");
                pstmt.setInt(1,updateNumber);
                pstmt.setString(2,fileName);
                pstmt.setString(3,database);
                pstmt.executeUpdate();
                
                midTime = endTime;
	            endTime = System.currentTimeMillis();
	            System.out.println("time for update_aip_temp_table "+(endTime-midTime)/1000.0+" seconds");
	            System.out.println("total time used "+(endTime-startTime)/1000.0+" seconds");

                if(test)
                {
                    System.out.println("begin to execute stored procedure update_aip_master_table");
                    System.out.println("press enter to continue");
                    System.in.read();
                    Thread.currentThread().sleep(1000);
                }
                pstmt = con.prepareCall("{ call update_aip_master_table(?,?)}");
                pstmt.setInt(1,updateNumber);
                pstmt.setString(2,database);
                pstmt.executeUpdate();

                midTime = endTime;
                endTime = System.currentTimeMillis();
                System.out.println("time for update_aip_master_table "+(endTime-midTime)/1000.0+" seconds");
                System.out.println("total time used "+(endTime-startTime)/1000.0+" seconds");
                
				if(test)
				{
					System.out.println("begin to execute stored procedure update_bd_reference_table");
					System.out.println("press enter to continue");
					System.in.read();
					Thread.currentThread().sleep(1000);
				}
				if(database.equals("cpx"))
				{
					pstmt = con.prepareCall("{ call update_aip_reference_table(?,?)}");
					pstmt.setInt(1,updateNumber);
					pstmt.setString(2,database);
	                pstmt.executeUpdate();
				}
				
				midTime = endTime;
                endTime = System.currentTimeMillis();
                System.out.println("time for update_aip_reference_table "+(endTime-midTime)/1000.0+" seconds");
                System.out.println("total time used "+(endTime-startTime)/1000.0+" seconds");
            }
            else if(action != null && action.equalsIgnoreCase("delete"))
            {
                if(test)
                {
                    System.out.println("begin to execute stored procedure delete_bd_master_table");
                    System.out.println("press enter to continue");
                    System.in.read();
                    Thread.currentThread().sleep(1000);
                }
                pstmt = con.prepareCall("{ call delete_bd_master_table(?,?,?)}");
                pstmt.setInt(1,updateNumber);
                pstmt.setString(2,fileName);
                pstmt.setString(3,database);
                pstmt.executeUpdate();
                
                midTime = endTime;
                endTime = System.currentTimeMillis();
                System.out.println("time for delete_bd_master_table "+(endTime-midTime)/1000.0+" seconds");
                System.out.println("total time used "+(endTime-startTime)/1000.0+" seconds");

                if(database.equalsIgnoreCase("cpx"))
                {
					if(test)
					{
						System.out.println("begin to execute stored procedure delete_bd_reference_table");
						System.out.println("press enter to continue");
						System.in.read();
						Thread.currentThread().sleep(1000);
					}
					pstmt = con.prepareCall("{ call delete_bd_reference_table(?,?)}");
					pstmt.setInt(1,updateNumber);
					pstmt.setString(2,database);
	                pstmt.executeUpdate();
	                
	                midTime = endTime;
	                endTime = System.currentTimeMillis();
	                System.out.println("time for delete_bd_reference_table "+(endTime-midTime)/1000.0+" seconds");
	                System.out.println("total time used "+(endTime-startTime)/1000.0+" seconds");
                }
            }
            else if(action != null && action.equalsIgnoreCase("cafedelete"))
            {
                if(test)
                {
                    System.out.println("begin to execute stored procedure delete_cafe_master_table");
                    System.out.println("press enter to continue");
                    System.in.read();
                    Thread.currentThread().sleep(1000);
                }
                pstmt = con.prepareCall("{ call delete_cafe_master_table(?)}");
                pstmt.setInt(1,updateNumber);              
                pstmt.executeUpdate();
                
                midTime = endTime;
                endTime = System.currentTimeMillis();
                System.out.println("time for delete_cafe_master_table "+(endTime-midTime)/1000.0+" seconds");
                System.out.println("total time used "+(endTime-startTime)/1000.0+" seconds");
              
            }
            else
            {
                System.out.println("What do you want me to do? action "+action+" not known");
                System.exit(1);
            }
            System.out.println("updateNumber= "+updateNumber+" fileName= "+fileName+" database= "+database);

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(pstmt != null)
            {
                try
                {
                    pstmt.close();
                }
                catch(Exception se)
                {
                }
            }
        }

    }

    private int getTempTableCount()
    {
        Statement stmt = null;
        String[] tableName = null;
        int count = 0;
        ResultSet rs = null;

        try
        {
            stmt = con.createStatement();

            rs = stmt.executeQuery("select count(*) count from "+tempTable+"");
            if(rs.next())
            {
                count = rs.getInt("count");
            }

        }
        catch(Exception e)
        {
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

        return count;
    }
    
    private int getTempReferenceTableCount()
    {
        Statement stmt = null;
        String[] tableName = null;
        int count = 0;
        ResultSet rs = null;

        try
        {
            stmt = con.createStatement();

            rs = stmt.executeQuery("select count(*) count from "+referenceTable+"");
            if(rs.next())
            {
                count = rs.getInt("count");
            }

        }
        catch(Exception e)
        {
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

        return count;
    }


    private void cleanUp(String tableToBeTruncate)
    {
    	boolean aipTemp = false;
    	boolean cafePuiListTemp = false;
    			
        Statement stmt = null;
        String[] tableName = null;
        
        if(tableToBeTruncate.indexOf(",")>-1)
        {
            tableName = tableToBeTruncate.split(",",-1);
        }
        else
        {
            tableName = new String[1];
            tableName[0] = tableToBeTruncate;
        }

        try
        {
            stmt = con.createStatement();

            //HH added Tuesday 06/09/2020, Week# [202025] 
            /*
             * Temp tables provided in this order from shell script  [bd_aip_temp,deleted_lookupIndex,bd_aip_temp_backup,bd_reference_temp,CAFE_PUI_LIST_TEMP]
             * to tackle "issue java.sql.SQLException: ORA-00054: resource busy and acquire with NOWAIT specified or timeout expired" happened at the time of cafe correction of week 2020172
             * causing many of cafe abstract records have been deleted from cafe_master because truncating temp table "bd_reference_temp" failed due to other unknown process was running on same table
             * that caused table lock and so raised java exception, and so exited cleanup process for rest of tables (i.e. bd_reference_temp, CAFE_PUI_LIST_TEMP), but the rest of parsing process 
             * still continued
             * As per Frank recommendation, make bd_reference_temp low priority, if truncation exception happened for bd_aip_temp, CAFE_PUI_LIST_TEMP these will have priority to exit whole process
             * not even continue parsing, if exception happened for references still can continue rest of correction, in this case have to sort temp tables based on some sort of priority
             *  In order to keep refrence temp table the last, has to check by table name instead of loop
             */
            
            if(this.tempTable != null && !(this.tempTable.isEmpty()))
            {
            	System.out.println("About to truncate: " + this.tempTable);
            	stmt.executeUpdate("truncate table "+this.tempTable);
                System.out.println(this.tempTable + " truncated");
                aipTemp = true;
            }
            if(this.lookupTable != null && !(this.lookupTable.isEmpty()))
            {
            	System.out.println("About to truncate: " + this.lookupTable);
                stmt.executeUpdate("truncate table "+this.lookupTable);
                System.out.println(this.lookupTable + " truncated");
            }
            if(this.backupTable != null && !(this.backupTable.isEmpty()))
            {
            	System.out.println("About to truncate: " + this.backupTable);
                stmt.executeUpdate("truncate table "+this.backupTable);
                System.out.println(this.backupTable + " truncated");
            }
            if(this.cafePuiListTempTable != null && !(this.cafePuiListTempTable.isEmpty()) && database.equals("cpx"))
            {
            	System.out.println("About to truncate: " + this.cafePuiListTempTable);
                stmt.executeUpdate("truncate table "+this.cafePuiListTempTable);
                System.out.println(this.cafePuiListTempTable + " truncated");
                cafePuiListTemp = true;
            }
            if(this.referenceTable != null && !(this.referenceTable.isEmpty()) && database.equals("cpx"))
            {
            	System.out.println("About to truncate: " + this.referenceTable);
 			    stmt.executeUpdate("truncate table "+this.referenceTable);
 			    System.out.println(this.referenceTable + " truncated");
            }
           if(this.authorLookupTempTable != null && !(this.authorLookupTempTable.isEmpty()) && database.equals("cpx"))
           {
        	   System.out.println("About to truncate: " + this.authorLookupTempTable);
			   stmt.executeUpdate("truncate table "+this.authorLookupTempTable);
			   System.out.println(this.authorLookupTempTable + " truncated");
           }
           if(this.affiliationLookupTempTable != null && !(affiliationLookupTempTable.isEmpty()) && database.equals("cpx"))
           {
        	   System.out.println("About to truncate: " + this.affiliationLookupTempTable);
			   stmt.executeUpdate("truncate table "+this.affiliationLookupTempTable);
			   System.out.println(this.affiliationLookupTempTable + " truncated");
           }
           

        }
        catch(Exception e)
        {
        	System.out.println("Exception occurred to truncate a temp table!");
            e.printStackTrace();
            
          //HH added 06/09/2020, if exception occured during cleaning-up bd_temp or cafe_pui_list_temp, exit correction process to avoid records deletion as happened for cafe_master
            if((!aipTemp && this.tempTable != null && !(this.tempTable.isEmpty())) || (!cafePuiListTemp && this.cafePuiListTempTable != null && !(this.cafePuiListTempTable.isEmpty())))
            {
            	StringBuffer tempTableName = new StringBuffer();
            	if(!aipTemp && this.tempTable != null)
            		tempTableName.append(this.tempTable);
            	if(!cafePuiListTemp && this.cafePuiListTempTable != null)
            		tempTableName.append(this.cafePuiListTempTable);
            	System.out.println("major temp table " + tempTableName.toString()+ " failed to be truncated, exit correction process for this file: " + fileName);
            	System.exit(1);
            }
            
        }
        finally
        {
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


    private void saveDeletedData(String field,List data,String database)
    {
        PreparedStatement stmt = null;
        try
        {
            if(data!=null)
            {
                for(int i=0;i<data.size();i++)
                {
                    String term = (String)data.get(i);
                    if(term != null && field != null && database != null)
                    {
                        stmt = con.prepareStatement("insert into "+lookupTable+" (field,term,database) values(?,?,?)");
                        stmt.setString(1,field);
                        stmt.setString(2,term);
                        stmt.setString(3,database);
                        stmt.executeUpdate();

                        con.commit();
                        if(stmt != null)
                        {
                            stmt.close();
                        }
                    }
                }
            }
            con.commit();
            if(stmt != null)
            {
                stmt.close();
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
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
    
    /*HT added 09/21/2020 for ES Lookup*/
	private void processESLookupIndex(Map<String, List<String>> update,Map<String, List<String>> backup)
			throws Exception {

		database = this.database;

		HashMap outputMap = new HashMap();

		Map deletedAuthorLookupIndex = getDeleteData(update, backup, "AUTHOR");
		Map deletedAffiliationLookupIndex = getDeleteData(update, backup, "AFFILIATION");
		Map deletedControlltermLookupIndex = getDeleteData(update, backup, "CONTROLLEDTERM");
		Map deletedPublisherNameLookupIndex = getDeleteData(update, backup, "PUBLISHERNAME");
		Map deletedSerialtitleLookupIndex = getDeleteData(update, backup, "SERIALTITLE");
		
		/*ONLY FOR DEBUGGING, UNCOMMENT IN PROD*/
		System.out.println("AUTHORTOBEDELETEDLIST: " + deletedAuthorLookupIndex.size());
		System.out.println("AFFTOBEDELETEDLIST: " + deletedAffiliationLookupIndex.size());
		System.out.println("CVTOBEDELETEDLIST: " + deletedControlltermLookupIndex.size());
		System.out.println("PNTOBEDELETEDLIST: " + deletedPublisherNameLookupIndex.size());
		System.out.println("STTOBEDELETEDLIST: " + deletedSerialtitleLookupIndex.size());
		

		saveDeletedData("AU", checkES(deletedAuthorLookupIndex, "AU", database), database);
		saveDeletedData("AF", checkES(deletedAffiliationLookupIndex, "AF", database), database);
		saveDeletedData("CV", checkES(deletedControlltermLookupIndex, "CV", database), database);
		saveDeletedData("PN", checkES(deletedPublisherNameLookupIndex, "PN", database), database);
		saveDeletedData("ST", checkES(deletedSerialtitleLookupIndex, "ST", database), database);

	}

    
    private void processLookupIndex(HashMap update,HashMap backup) throws Exception
    {

        database = this.database;

        HashMap outputMap = new HashMap();
        //System.out.println("****Doing Amazon testing, do not process lookup index*****");
        //command out for amazon cloud testing

        HashMap deletedAuthorLookupIndex            = getDeleteData(update,backup,"AUTHOR");
        HashMap deletedAffiliationLookupIndex       = getDeleteData(update,backup,"AFFILIATION");
        HashMap deletedControlltermLookupIndex  = getDeleteData(update,backup,"CONTROLLEDTERM");
        HashMap deletedPublisherNameLookupIndex     = getDeleteData(update,backup,"PUBLISHERNAME");
        HashMap deletedSerialtitleLookupIndex   = getDeleteData(update,backup,"SERIALTITLE");
        
        /*
        if(cafeFlag!=null)
        {
        	System.out.println("doing cafe loading");
        }
        else
        {
        */
        	//System.out.println("doing non-cafe loading");
	        saveDeletedData("AU",checkFast(deletedAuthorLookupIndex,"AU",database),database);        
	        saveDeletedData("AF",checkFast(deletedAffiliationLookupIndex,"AF",database),database);
	        saveDeletedData("CV",checkFast(deletedControlltermLookupIndex,"CV",database),database);
	        saveDeletedData("PN",checkFast(deletedPublisherNameLookupIndex,"PN",database),database);
	        saveDeletedData("ST",checkFast(deletedSerialtitleLookupIndex,"ST",database),database);
        //}

    }

    /*ES added 09/21/2020 for ES Lookup*/
    private List<String> checkES(Map inputMap, String searchField, String database) throws Exception
    {
        List<String> outputList = new ArrayList();
        SharedSearchSearchEntry entry = new SharedSearchSearchEntry("https://shared-search-service-api.prod.scopussearch.net/sharedsearch/document/result");
        outputList = entry.runESLookupCheck(inputMap,searchField,database);
        return outputList;

    }
    
    private List checkFast(Map inputMap, String searchField, String database) throws Exception
    {
    	
    	//HH 02/23/2015 set DataBaseConf.DbCorrFlag for local db connection othertan connectionBroker
    	DatabaseConfig.DbCorrFlag = 1;

        List outputList = new ArrayList();
        
        DatabaseConfig databaseConfig = DatabaseConfig.getInstance(DriverConfig.getDriverTable());
        String[] credentials = new String[]{"CPX","PCH","CHM","GEO","ELT"};
        String[] dbName = {database};

        int intDbMask = databaseConfig.getMask(dbName);

        Iterator searchTerms = inputMap.keySet().iterator();
        //System.out.println("total Fast search size for "+ searchField+" is "+  inputMap.size());

        while (searchTerms.hasNext())
        {
            String term1=null;
            try
            {
                SearchControl sc = new FastSearchControl();
                term1 = (String) searchTerms.next();

                int oc = Integer.parseInt((String)inputMap.get(term1));
                Query queryObject = new Query(databaseConfig, credentials);
                queryObject.setDataBase(intDbMask);

                String searchID = (new GUID()).toString();
                queryObject.setID(searchID);
                queryObject.setSearchType(Query.TYPE_QUICK);

                queryObject.setSearchPhrase("{"+term1+"}",searchField,"","","","","","");
                queryObject.setSearchQueryWriter(new FastQueryWriter());
                queryObject.compile();
                String sessionId = null;
                int pagesize = 2;
                SearchResult result = sc.openSearch(queryObject,sessionId,pagesize,false);
                int c = result.getHitCount();
                String indexCount = (String)inputMap.get(term1);
                if(indexCount!=null && indexCount!="" && Integer.parseInt(indexCount) >= c)
                {
                    outputList.add(term1);
                }

            }
            catch(Exception e)
            {
            	outputList.add(term1);
                System.out.println("Problem with lookup index= "+term1);
                System.out.println("This term will be deleted from lookupindex");
                e.printStackTrace();
            }
            Thread.currentThread().sleep(100);

        }
        
      //HH 02/23/2015 Reset DataBaseConf.DbCorrFlag back to default
      	DatabaseConfig.DbCorrFlag = 0;
        return outputList;

    }

    private HashMap getDeleteData(Map update,Map backup,String field)
    {
        List backupList = null;
        List updateList = null;
        HashMap deleteLookupIndex = new HashMap();
        if(update !=null && backup != null)
        {
            backupList = (ArrayList)backup.get(field);
            updateList = (ArrayList)update.get(field);

            if(backupList!=null)
            {
                String dData = null;

                for(int i=0;i<backupList.size();i++)
                {
                    dData = (String)backupList.get(i);
                    if(dData != null)
                    {
                        if(updateList==null ||(updateList!=null && !updateList.contains(dData)))
                        {
                            if(deleteLookupIndex.containsKey(dData.toUpperCase()))
                            {
                                deleteLookupIndex.put(dData.toUpperCase(),Integer.toString(Integer.parseInt((String)deleteLookupIndex.get(dData.toUpperCase()))+1));
                            }
                            else
                            {
                                deleteLookupIndex.put(dData.toUpperCase(),"1");
                            }

                        }
                    }
                }
            }
        }

        return deleteLookupIndex;
    }

    private boolean checkUpdate(List update,String term)
    {
        if(update != null)
        {
            for(int i=0;i<update.size();i++)
            {
                String updateData = (String)update.get(i);

                if(term.equalsIgnoreCase(updateData))
                {
                    return true;
                }
            }
        }
        return false;

    }


    public HashMap getLookupData(String action) throws Exception
    {
        Statement stmt = null;
        ResultSet rs = null;
        HashMap results = null;
        try
        {
            stmt = con.createStatement();
            System.out.println("Running the query...");
            String sqlString=null;
            if(database.equals("cpx"))          
            {
                if(action.equals("update")||action.equals("aip"))
                {
                    sqlString = "select ACCESSNUMBER,AUTHOR,AUTHOR_1,AFFILIATION,AFFILIATION_1,CONTROLLEDTERM,CHEMICALTERM,SOURCETITLE,PUBLISHERNAME,DATABASE,PUI FROM "+tempTable;
                }
                else if(action.equals("lookupIndex") && updateNumber != 0 && database != null)
                {
                    sqlString = "select ACCESSNUMBER,PUI,AUTHOR,AUTHOR_1,AFFILIATION,AFFILIATION_1,CONTROLLEDTERM,CHEMICALTERM,SOURCETITLE,PUBLISHERNAME,DATABASE,PUI FROM BD_MASTER_ORIG where updateNumber="+updateNumber+" and database='"+database+"'";
                }
                else
                {
                    sqlString = "select ACCESSNUMBER,AUTHOR,AUTHOR_1,AFFILIATION,AFFILIATION_1,CONTROLLEDTERM,CHEMICALTERM,SOURCETITLE,PUBLISHERNAME,DATABASE,PUI FROM "+backupTable;
                }

                System.out.println("Processing "+sqlString);
                rs = stmt.executeQuery(sqlString);

                System.out.println("Got records ...");
                results = setRecs(rs);
            }

            //System.out.println("Wrote records.");


        }
        catch(Exception e)
        {
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

    public HashMap setInspecRecs(ResultSet rs)
                throws Exception
        {
            int i = 0;
            CombinedWriter writer = new CombinedXMLWriter(10000,10000,"ins","dev");
            XmlCombiner xml = new XmlCombiner(writer);
            HashMap recs = new HashMap();
            List authorList = new ArrayList();
            List affiliationList = new ArrayList();
            List serialTitleList = new ArrayList();
            List controltermList = new ArrayList();
            List publishernameList = new ArrayList();
            String database = null;
            String accessNumber = null;
            try
            {
                INSPECCombiner c = new INSPECCombiner(writer);
                while (rs.next())
                {
                    ++i;
                    EVCombinedRec rec = new EVCombinedRec();

                    accessNumber = rs.getString("anum");

                    if(accessNumber !=null && accessNumber.length()>5)
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
                            authorList.addAll(Arrays.asList(c.prepareAuthor(aus.toString().toUpperCase())));
                        }
                        else if(rs.getString("eds") != null)
                        {
                            rec.put(EVCombinedRec.EDITOR, c.prepareAuthor(rs.getString("eds")));
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
                            affiliationList.addAll(Arrays.asList(c.prepareAuthor(aaff.toString())));
                        }

                        if(rs.getString("cvs") != null)
                        {
                             controltermList.addAll(Arrays.asList(c.prepareMulti(rs.getString("cvs").toUpperCase())));
                        }

                        if(rs.getString("ppub") != null)
                        {
                             publishernameList.add(xml.preparePublisherName(rs.getString("ppub").toUpperCase()));
                        }

                        if(rs.getString("chi") != null)
                        {
                              controltermList.addAll(Arrays.asList(c.prepareMulti(rs.getString("chi").toUpperCase())));
                        }

                        if(rs.getString("pubti") != null)
                        {
                            serialTitleList.add(rs.getString("pubti").toUpperCase());
                        }

                        if(rs.getString("pfjt") != null)
                        {
                            serialTitleList.add(rs.getString("pfjt").toUpperCase());
                        }


                        if(rs.getString("ipc")!=null)
                        {
                            String ipcString = rs.getString("ipc");
                            ipcString = perl.substitute("s/\\//SLASH/g", ipcString);
                            rec.put(EVCombinedRec.INT_PATENT_CLASSIFICATION, ipcString);
                        }

                    }
                }

                recs.put("AUTHOR",authorList);
                recs.put("AFFILIATION",affiliationList);
                recs.put("CONTROLLEDTERM",controltermList);
                recs.put("PUBLISHERNAME",publishernameList);
                recs.put("SERIALTITLE",serialTitleList);
                //recs.put("DATABASE",database);
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

            return recs;
    }

    public HashMap setGRFRecs(ResultSet rs)
                    throws Exception
    {
        int i = 0;
        CombinedWriter writer = new CombinedXMLWriter(10000,10000,"grf","dev");
        XmlCombiner xml = new XmlCombiner(writer);
        HashMap recs = new HashMap();
        List authorList = new ArrayList();
        List affiliationList = new ArrayList();
        List serialTitleList = new ArrayList();
        List controltermList = new ArrayList();
        List publishernameList = new ArrayList();
        String database = null;
        String accessNumber = null;
        try
        {
            while (rs.next())
            {

                accessNumber = rs.getString("ID_NUMBER");

                if(accessNumber !=null && accessNumber.length()>5)
                {
                    //rec.put(EVCombinedRec.ACCESSION_NUMBER, accessNumber);


                    //AUTHOR************
                    String aString = rs.getString("PERSON_ANALYTIC");
                    if(aString != null)
                    {
                        authorList.addAll(Arrays.asList(aString.split(AUDELIMITER)));
                    }
                    else if(rs.getString("PERSON_MONOGRAPH") != null)
                    {
                        String eString = rs.getString("PERSON_MONOGRAPH");

                        String otherEditors = rs.getString("PERSON_COLLECTION");
                        if(otherEditors != null)
                        {
                            eString = eString.concat(AUDELIMITER).concat(otherEditors);
                        }
                        //rec.put(EVCombinedRec.EDITOR, eString.split(AUDELIMITER));

                    }

                    // AUTHOR AFFLICATION *********

                    String affilitation = rs.getString("AUTHOR_AFFILIATION");
                    if(affilitation != null)
                    {
                      List affilations= new ArrayList();
                      String[] affilvalues = null;
                      String[] values = null;
                      affilvalues = affilitation.split(AUDELIMITER);
                      for(int x = 0 ; x < affilvalues.length; x++)
                      {
                          affilations.add(affilvalues[x]);
                      }
                     if(rs.getString("AFFILIATION_SECONDARY") != null)
                      {
                        String secondaffiliations = rs.getString("AFFILIATION_SECONDARY");
                        affilvalues = secondaffiliations.split(AUDELIMITER);
                        for(int x = 0 ; x < affilvalues.length; x++)
                        {
                          values = affilvalues[x].split(IDDELIMITER);
                          affilations.add(values[0]);
                        }
                      }
                      if(!affilations.isEmpty())
                      {
                        //rec.putIfNotNull(EVCombinedRec.AUTHOR_AFFILIATION, (String[]) affilations.toArray(new String[]{}));
                        affiliationList.addAll(affilations);
                      }
                    }

                    // CONTROLL_TERMS (CVS)
                    if(rs.getString("INDEX_TERMS") != null)
                    {
                        String[] idxterms = rs.getString("INDEX_TERMS").split(AUDELIMITER);
                        for(int z = 0; z < idxterms.length; z++)
                        {
                            idxterms[z] = idxterms[z].replaceAll("[A-Z]*" + IDDELIMITER,"");
                        }
                        controltermList.addAll(Arrays.asList(idxterms));
                    }

                    if(rs.getString("PUBLISHER") != null)
                    {
                        publishernameList.addAll(Arrays.asList(rs.getString("PUBLISHER").split(AUDELIMITER)));
                    }

                    if(rs.getString("TITLE_OF_SERIAL") != null)
                    {
                        serialTitleList.add(rs.getString("TITLE_OF_SERIAL"));
                    }
                }
            }

            recs.put("AUTHOR",authorList);
            recs.put("AFFILIATION",affiliationList);
            recs.put("CONTROLLEDTERM",controltermList);
            recs.put("PUBLISHERNAME",publishernameList);
            recs.put("SERIALTITLE",serialTitleList);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return recs;
    }

    public HashMap setEPTRecs(ResultSet rs)
                        throws Exception
    {
        int i = 0;
        CombinedWriter writer = new CombinedXMLWriter(10000,10000,"ept","dev");
        EptCombiner c = new EptCombiner(writer);
        HashMap recs = new HashMap();
        List authorList = new ArrayList();
        List affiliationList = new ArrayList();
        List serialTitleList = new ArrayList();
        List controltermList = new ArrayList();
        List publishernameList = new ArrayList();
        String database = null;
        String accessNumber = null;
        try
        {
            while (rs.next())
            {
                ++i;
                EVCombinedRec rec = new EVCombinedRec();

                accessNumber = rs.getString("dn");

                if(accessNumber !=null && accessNumber.length()>5)
                {
                    rec.put(EVCombinedRec.ACCESSION_NUMBER, accessNumber);


                    //AUTHOR************
                    String aString = rs.getString("pat_in");
                    if(aString != null)
                    {
                        authorList.addAll(Arrays.asList(c.prepareMulti(StringUtil.replaceNonAscii(c.replaceNull(aString)))));
                    }


                    // AUTHOR AFFLICATION *********

                    String affilitation = rs.getString("cs");
                    if(affilitation != null)
                    {
                        affiliationList.addAll(Arrays.asList(c.prepareMulti(StringUtil.replaceNonAscii(c.replaceNull(affilitation)))));

                    }



                    // CONTROLL_TERMS (CVS)
                    String ct = c.replaceNull(rs.getString("ct"));
                    if(ct != null)
                    {
                        CVSTermBuilder termBuilder = new CVSTermBuilder();
                        String cv = termBuilder.getNonMajorTerms(ct);
                        String mh = termBuilder.getMajorTerms(ct);
                        StringBuffer cvsBuffer = new StringBuffer();

                        String expandedMajorTerms = termBuilder.expandMajorTerms(mh);
                        String expandedMH = termBuilder.getMajorTerms(expandedMajorTerms);
                        String expandedCV1 = termBuilder.expandNonMajorTerms(cv);
                        String expandedCV2 = termBuilder.getNonMajorTerms(expandedMajorTerms);

                        if (!expandedCV2.equals(""))
                            cvsBuffer.append(expandedCV1).append(";").append(expandedCV2);
                        else
                            cvsBuffer.append(expandedCV1);

                        String parsedCV = termBuilder.formatCT(cvsBuffer.toString());

                        String parsedMH = termBuilder.formatCT(expandedMH);
                        controltermList.addAll(Arrays.asList(c.prepareMulti(termBuilder.getStandardTerms(parsedCV), Constants.CVS)));
                        controltermList.addAll(Arrays.asList(c.prepareMulti(StringUtil.replaceNonAscii(termBuilder.getStandardTerms(parsedMH)), Constants.CVS)));
                    }

                }
            }

            recs.put("AUTHOR",authorList);
            recs.put("AFFILIATION",affiliationList);
            recs.put("CONTROLLEDTERM",controltermList);

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return recs;
    }

    public HashMap setUPARecs(ResultSet rs)
                            throws Exception
    {
        int i = 0;
        CombinedWriter writer = new CombinedXMLWriter(10000,10000,"upa","dev");
        UPTCombiner c = new UPTCombiner("upa",writer);
        HashMap recs = new HashMap();
        List authorList = new ArrayList();
        List affiliationList = new ArrayList();
        List serialTitleList = new ArrayList();
        List controltermList = new ArrayList();
        List publishernameList = new ArrayList();
        String database = null;
        String accessNumber = null;
        try
        {
            while (rs.next())
            {
                String patentNumber = Entity.replaceUTFString(Entity.prepareString(c.replaceAmpersand(rs.getString("pn"))));
                String kindCode = Entity.replaceUTFString(Entity.prepareString(c.replaceAmpersand(rs.getString("kc"))));
                String authCode = Entity.replaceUTFString(Entity.prepareString(c.replaceAmpersand(rs.getString("ac"))));

                if (patentNumber != null)
                {
                    //rec.put(EVCombinedRec.PATENT_NUMBER, c.formatPN(patentNumber, kindCode, authCode));

                    //AUTHOR************

                    if (rs.getString("inv") != null)
                    {
                        if (rs.getString("asg") != null)
                        {
                            List lstAsg = c.convertString2List(rs.getString("asg"));
                            List lstInv = c.convertString2List(rs.getString("inv"));

                            if (authCode.equals(c.EP_CY) && lstInv.size() > 0 && lstAsg.size() > 0)
                            {
                                lstInv = AssigneeFilter.filterInventors(lstAsg, lstInv, false);
                            }

                            String[] arrVals = (String[]) lstInv.toArray(new String[1]);
                            arrVals[0] = c.replaceNull(arrVals[0]);

                            for (int j = 0; j < arrVals.length; j++) {
                                arrVals[j] = c.formatAuthor(Entity.replaceUTFString(Entity.prepareString(c.replaceAmpersand(arrVals[j]))));
                            }

                            if (arrVals != null)
                            {
                                authorList.addAll(Arrays.asList(arrVals));
                            }

                        }
                        else
                        {
                            authorList.addAll(Arrays.asList(c.convert2Array(c.formatAuthor(Entity.replaceUTFString(Entity.prepareString(c.replaceAmpersand(rs.getString("inv"))))))));
                        }
                    }


                    // AUTHOR AFFLICATION *********

                     if (rs.getString("asg") != null)
                     {
                        if (rs.getString("inv") != null)
                        {
                            List lstAsg = c.convertString2List(rs.getString("asg"));
                            List lstInv = c.convertString2List(rs.getString("inv"));

                            if (authCode.equals(c.US_CY) && lstInv.size() > 0 && lstAsg.size() > 0) {
                                lstAsg = AssigneeFilter.filterInventors(lstInv, lstAsg, true);
                            }

                            String[] arrVals = (String[]) lstAsg.toArray(new String[1]);

                            arrVals[0] = c.replaceNull(arrVals[0]);

                            for (int j = 0; j < arrVals.length; j++) {
                                arrVals[j] = Entity.replaceUTFString(Entity.prepareString(c.replaceAmpersand(arrVals[j])));

                            }

                            if (arrVals != null)
                            {
                                affiliationList.addAll(Arrays.asList(arrVals));
                            }
                        }
                        else
                        {
                            affiliationList.addAll(Arrays.asList(c.convert2Array(Entity.replaceUTFString(Entity.prepareString(c.replaceAmpersand(rs.getString("asg")))))));
                        }

                    }
                }
            }

            recs.put("AUTHOR",authorList);
            recs.put("AFFILIATION",affiliationList);

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return recs;
    }

    public HashMap setNTISRecs(ResultSet rs)
                        throws Exception
    {
        int i = 0;
        CombinedWriter writer = new CombinedXMLWriter(10000,10000,"nti","dev");
        NTISCombiner ntis = new NTISCombiner(writer);
        HashMap recs = new HashMap();
        List authorList = new ArrayList();
        List affiliationList = new ArrayList();
        List serialTitleList = new ArrayList();
        List controltermList = new ArrayList();
        List publishernameList = new ArrayList();
        String database = null;
        String accessNumber = null;
        try
        {
            while (rs.next())
            {

                accessNumber = rs.getString("AN");

                if(accessNumber !=null)
                {


                    //AUTHOR************
                     String aut = NTISAuthor.formatAuthors(rs.getString("PA1"),
                                                           rs.getString("PA2"),
                                                           rs.getString("PA3"),
                                                           rs.getString("PA4"),
                                                           rs.getString("PA5"),
                                                           rs.getString("HN"));

                    if (aut != null)
                    {
                        authorList.addAll(Arrays.asList(ntis.prepareAuthor(aut)));
                    }


                    // AUTHOR AFFLICATION *********

                    String affil = ntis.formatAffil(rs.getString("SO"));
                    Map pAndS = NTISData.authorAffiliationAndSponsor(affil);
                    if(pAndS.containsKey(Keys.PERFORMER))
                    {
                        affiliationList.add(pAndS.get(Keys.PERFORMER));
                    }

                    if(pAndS.containsKey(Keys.RSRCH_SPONSOR))
                    {
                        affiliationList.add(pAndS.get(Keys.RSRCH_SPONSOR));
                    }


                    // CONTROLL_TERMS (CVS)
                    String cv = ntis.formatDelimiter(ntis.formatCV(rs.getString("DES")));
                    if (cv != null)
                    {
                        controltermList.addAll(Arrays.asList(ntis.prepareMulti(cv)));
                    }


                }
            }

            recs.put("AUTHOR",authorList);
            recs.put("AFFILIATION",affiliationList);
            recs.put("CONTROLLEDTERM",controltermList);

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return recs;
    }

    public HashMap setCBNRecs(ResultSet rs)
                            throws Exception
        {

            CombinedWriter writer = new CombinedXMLWriter(10000,10000,"cbn","dev");
            CBNBCombiner cbn = new CBNBCombiner(writer);
            HashMap recs = new HashMap();
            List serialTitleList = new ArrayList();
            List controltermList = new ArrayList();


            try
            {
                while (rs.next())
                {


                    // CONTROLL_TERMS (CVS)

                    if (rs.getString("ebt") != null)
                    {

                        controltermList.addAll(Arrays.asList(cbn.prepareMulti(rs.getString("ebt"))));
                    }

                    // SERIAL_TITLE

                    if (rs.getString("fjl") != null)
                    {
                        serialTitleList.add(rs.getString("fjl"));
                    }




                }

                recs.put("CONTROLLEDTERM",controltermList);
                recs.put("SERIALTITLE",serialTitleList);

            }
            catch(Exception e)
            {
                e.printStackTrace();
            }

            return recs;
    }


    private HashMap setRecs(ResultSet rs)
            throws Exception
    {
        int i = 0;
        CombinedWriter writer = null;
        if(this.database!=null)
        {
        	writer = new CombinedXMLWriter(10000,10000,this.database,"dev");
        }
        else
        {
        	System.out.println("missing database");
        	return null;
        }
        XmlCombiner xml = new XmlCombiner(writer);
        HashMap recs = new HashMap();
        List authorList = new ArrayList();
        List affiliationList = new ArrayList();
        List serialTitleList = new ArrayList();
        List controltermList = new ArrayList();
        List publishernameList = new ArrayList();
        String database = null;
        String accessNumber = null;
        String pui = null;
        try
        {
            while (rs.next())
            {
                ++i;
                //EVCombinedRec rec = new EVCombinedRec();

                accessNumber = rs.getString("ACCESSNUMBER");
                pui = rs.getString("PUI");
                //System.out.println("PUI="+pui);
                if(accessNumber !=null && accessNumber.length()>5 && !(accessNumber.substring(0,6).equals("200138")))
                {
                    recs.put("ACCESSION_NUMBER", accessNumber);
                    recs.put("PUI", pui);
                    if(rs.getString("AUTHOR") != null)
                    {
                        String authorString = rs.getString("AUTHOR");
                        if(rs.getString("AUTHOR_1") !=null)
                        {
                            authorString=authorString+rs.getString("AUTHOR_1");
                        }
                        authorList.addAll(Arrays.asList(xml.prepareBdAuthor(authorString.toUpperCase())));
                    }

                    if (rs.getString("AFFILIATION") != null)
                    {
                        String affiliation = rs.getString("AFFILIATION");
                        if(rs.getString("AFFILIATION_1")!=null)
                        {
                            affiliation = affiliation+rs.getString("AFFILIATION_1");
                        }
                        BdAffiliations aff = new BdAffiliations(affiliation.toUpperCase());
                        affiliationList.addAll(Arrays.asList(aff.getSearchValue()));

                    }

                    if (rs.getString("CHEMICALTERM") != null)
                    {
                        controltermList.addAll(Arrays.asList(xml.prepareMulti(rs.getString("CHEMICALTERM").toUpperCase())));
                    }

                    if (rs.getString("CONTROLLEDTERM") != null)
                    {
                        controltermList.addAll(Arrays.asList(xml.prepareMulti(rs.getString("CONTROLLEDTERM").toUpperCase())));
                    }

                    if (rs.getString("PUBLISHERNAME") != null)
                    {
                        publishernameList.add(xml.preparePublisherName(rs.getString("PUBLISHERNAME").toUpperCase()));
                    }

                    if (rs.getString("SOURCETITLE") != null)
                    {
                        serialTitleList.add(rs.getString("SOURCETITLE").toUpperCase());
                    }

                    if(rs.getString("DATABASE") != null)
                    {
                        database = rs.getString("DATABASE");
                    }
                }
            }

            recs.put("AUTHOR",authorList);
            recs.put("AFFILIATION",affiliationList);
            recs.put("CONTROLLEDTERM",controltermList);
            recs.put("PUBLISHERNAME",publishernameList);
            recs.put("SERIALTITLE",serialTitleList);
            recs.put("DATABASE",database);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return recs;
    }

     protected Connection getConnection(String connectionURL,
                                             String driver,
                                             String username,
                                             String password)
                throws Exception
     {
                System.out.println("connectionURL= "+connectionURL);
                System.out.println("driver= "+driver);
                System.out.println("username= "+username);
                System.out.println("password= "+password);

                Class.forName(driver);
                Connection con = DriverManager.getConnection(connectionURL,
                                                  username,
                                                  password);
                return con;
     }

}
