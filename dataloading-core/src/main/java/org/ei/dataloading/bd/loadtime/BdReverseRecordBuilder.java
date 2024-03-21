package org.ei.dataloading.bd.loadtime;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.StringReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.util.*;

import org.ei.common.bd.*;
import org.apache.commons.text.StringEscapeUtils;
import org.ei.common.Constants;

//import org.ei.data.LoadNumber;
//import org.ei.dataloading.bd.*;
import java.sql.*;

import org.ei.common.Constants;
import org.ei.dataloading.DataLoadDictionary;
import org.ei.dataloading.EVCombinedRec;
import org.ei.dataloading.cafe.GetANIFileFromCafeS3Bucket;
import org.ei.xml.Entity;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.ei.data.compendex.runtime.*;

public class BdReverseRecordBuilder
{
    private static BaseTableWriter baseWriter;
    private String accessnumber;
    private int loadNumber;
    private String databaseName;
    private String tableName = "bd_master";
    private static String startRootElement ="<?xml version=\"1.0\" encoding=\"UTF-8\" ?><bibdataset xsi:schemaLocation=\"http://www.elsevier.com/xml/ani/ani http://www.elsevier.com/xml/ani/ani512.xsd\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:ait=\"http://www.elsevier.com/xml/ani/ait\" xmlns:ce=\"http://www.elsevier.com/xml/ani/common\" xmlns=\"http://www.elsevier.com/xml/ani/ani\">";        																								
    private static String endRootElement   ="</bibdataset>";
    private static Connection con;
    private static String infile;
    private static String url = "jdbc:oracle:thin:@eid.cmdvszxph9cf.us-east-1.rds.amazonaws.com:1521:eid";
    private static String driver = "oracle.jdbc.driver.OracleDriver";
    private static String username = "ba_loading";
    private static String password = "";
    private DataLoadDictionary dictionary = new DataLoadDictionary();

    private static Hashtable contributorRole = new Hashtable();
	{
		contributorRole.put("author","auth");
		contributorRole.put("compiler","comp");
		contributorRole.put("editor","edit");
		contributorRole.put("illustrator","illu");
		contributorRole.put("photographer","phot");
		contributorRole.put("publisher","publ");
		contributorRole.put("reviewer","revi");
		contributorRole.put("translator","tran");
	}
   
    public static void main(String args[])
        throws Exception

    {
        int loadN=0;
        long startTime = System.currentTimeMillis();
        /*
        if(args.length<7)
        {
            System.out.println("please enter three parameters as \" weeknumber filename databaseName action url driver username password\"");
            System.exit(1);
        }
         */
       
        
        if(args.length>3)
        {
            url = args[3];
            driver = args[4];
            username = args[5];
            password = args[6];          
        }
        else
        {
        	
        	infile = args[0];
        	String xdsName = args[1];
        	System.out.println("Validating XML file "+infile);
        	BdReverseRecordBuilder b = new BdReverseRecordBuilder();
        	if(b.validatedXml(infile,xdsName))
            {
            	 System.out.println("File "+infile+" is valid");
            }
        	else
        	{
        		System.out.println("File "+infile+" is NOT valid");
        	}
        	System.exit(1);
        }
        loadN = Integer.parseInt(args[0]);
        String databaseName = args[1];
        String tableName = args[2];
        BdReverseRecordBuilder c;

        try
        {

          
            c = new BdReverseRecordBuilder(loadN,databaseName);            
            con = c.getConnection(url,driver,username,password);
            c.writeFile(con,loadN,databaseName,tableName);
            
        }
        catch(Exception e)
        {
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
    }
   
    public BdReverseRecordBuilder()
    {
    	
    }
    public BdReverseRecordBuilder(int loadN,String databaseName)
    {
        this.loadNumber = loadN;
        this.databaseName = databaseName;
   
    }
    
    /* this one is used for generate a file per loadnumber 
    public void writeFile(Connection con,int loadN,String databaseName, String tableName)
            throws Exception
    {
    	StringBuffer initialString = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
    	initialString.append("<bibdataset xsi:schemaLocation=\"http://www.elsevier.com/xml/ani/ani http://www.elsevier.com/xml/ani/ani512.xsd\" ");
    	initialString.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
    	initialString.append("xmlns:ait=\"http://www.elsevier.com/xml/ani/ait\" ");
    	initialString.append("xmlns:ce=\"http://www.elsevier.com/xml/ani/common\" ");
    	initialString.append("xmlns=\"http://www.elsevier.com/xml/ani/ani\">\n");
    	 Statement stmt = null;
         ResultSet rs = null;
         int count = 0;
         boolean checkResult = false;
         FileWriter file = null;
         String xsdFileName="ani515/ani515.xsd";
         try
         {
             stmt = con.createStatement();
             String filename = databaseName+"_"+loadN+".xml";
             file = new FileWriter(filename);
             file.write(initialString.toString());
             rs = stmt.executeQuery("select *  from bd_master where loadnumber="+loadN+" and database='"+databaseName+"'");
             while (rs.next())
             {
                 writeRecord(rs,file);
             }
             file.write("</bibdataset>");
             file.close();
             if(validatedXml(filename,xsdFileName))
            	 zipBatchFile(filename,databaseName);
         }
         catch (Exception e)
         {
        	 System.out.println("problem with accessnumber="+this.accessnumber);
        	 e.printStackTrace();
             System.exit(1);
         }
         finally
         {
             if(rs != null)
             {
                 rs.close();
             }
             
             if(file != null)
             {
            	 file.close();
             }
         }
    	
    }
    */
    
    //this one is used for a record for each xml file
    public void writeFile(Connection con,int loadN,String databaseName, String tableName)
            throws Exception
    {
    	StringBuffer initialString = new StringBuffer("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
    	initialString.append("<bibdataset xsi:schemaLocation=\"http://www.elsevier.com/xml/ani/ani http://www.elsevier.com/xml/ani/ani512.xsd\" ");
    	initialString.append("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" ");
    	initialString.append("xmlns:ait=\"http://www.elsevier.com/xml/ani/ait\" ");
    	initialString.append("xmlns:ce=\"http://www.elsevier.com/xml/ani/common\" ");
    	initialString.append("xmlns=\"http://www.elsevier.com/xml/ani/ani\">\n");
    	 Statement stmt = null;
         ResultSet rs = null;
         int count = 0;
         boolean checkResult = false;
         FileWriter file = null;
         String xsdFileName="ani515/ani515.xsd";
         File path = null;
         Hashtable recordData = new Hashtable();     	
         try
         {
             stmt = con.createStatement();
             if(databaseName.equalsIgnoreCase("cbn"))
             {
            	 rs = stmt.executeQuery("select *  from cbn_master where load_number="+loadN);
             }
             else
             {
            	 rs = stmt.executeQuery("select *  from bd_master where loadnumber="+loadN+" and database='"+databaseName+"'");
             }
             
             path=new File(databaseName);
         	 if(!path.exists())
             {
                 path.mkdir();
             }
         	 path=new File(databaseName+"/xml");
         	 if(!path.exists())
             {
                 path.mkdir();
             }
         	path=new File(databaseName+"/xml/"+loadN);
        	if(!path.exists())
            {
                path.mkdir();
            }
         	 
             while (rs.next())
             {
            	 String m_id=rs.getString("M_ID");
            	 String filename = path+"/"+m_id+".xml";
                 file = new FileWriter(filename);
                 file.write(initialString.toString());
                 if(databaseName.equalsIgnoreCase("cbn"))
                 {
                	 recordData=getCBNBDataFromDatabase(rs);
                 }
                 else
                 {
                	 recordData=getBDDataFromDatabase(rs);
                 }
                 writeRecord(recordData,file);
                 file.write("</bibdataset>");
                 file.close();
                 
                 /*
                 if(!validatedXml(filename,xsdFileName))
                 {
                	 continue;
                	 //break;
                 }
                 */
                 
             }
             
            
         }
         catch (Exception e)
         {
        	 System.out.println("problem with accessnumber="+this.accessnumber);
        	 e.printStackTrace();
             System.exit(1);
         }
         finally
         {
             if(rs != null)
             {
                 rs.close();
             }
             
             if(file != null)
             {
            	 file.close();
             }
         }
    	
    }
    
    private boolean validatedXml(String filename,String xsdFileName) throws Exception
	{
		// parse an XML document into a DOM tree
		 javax.xml.parsers.DocumentBuilder parser =  javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder();
		 Document document = parser.parse(new File(filename));

		// create a SchemaFactory capable of understanding WXS schemas
		//SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		 SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);

		// load a WXS schema, represented by a Schema instance
		//Source schemaFile = new StreamSource(new File("ani512.xsd"));
		Source schemaFile = new StreamSource(new File(xsdFileName));
		Schema schema = factory.newSchema(schemaFile);

		// create a Validator instance, which can be used to validate an instance document
		Validator validator = schema.newValidator();

		// validate the DOM tree
		try {
		   //validator.validate(new DOMSource(document));
			validator.validate(new StreamSource(new File(filename)));
			
		    //System.out.println(filename+" is valid!");
		    return true;
		} catch (SAXException e) {
			System.out.println(filename+" is invalid!");
			e.printStackTrace();
			return false;
		    // instance document is invalid!
		}
		
	}
    
    public Hashtable getCBNBDataFromDatabase(ResultSet rs)
    {
    	Hashtable singleRecordData = new Hashtable();
    	try 
    	{
    		singleRecordData.put("ACCESSNUMBER",rs.getString("ABN"));
    		singleRecordData.put("DATABASE","CBNB");
    		
	    	if(rs.getString("CDT")!=null)
	    	{
	    		singleRecordData.put("DATESORT", rs.getString("CDT"));
	    	}
	    	
	    	/*
	    	if(rs.getString("COPYRIGHT")!=null)
	    	{
	    		singleRecordData.put("COPYRIGHT", dictionary.AlphaEntitysToNumericEntitys(rs.getString("COPYRIGHT")));
	    	}
	    	
	    	
	    	if(rs.getString("DOI")!=null)
	    	{
	    		singleRecordData.put("DOI",rs.getString("DOI"));
	    	}
	    	
	    	if(rs.getString("PUI")!=null)
	    	{
	    		singleRecordData.put("PUI", rs.getString("PUI"));
	    	}
	    	
	    	if(rs.getString("DATABASE")!=null)
	    	{
	    		singleRecordData.put("DATABASE",rs.getString("DATABASE"));
	    	}
	    	*/
	    	
	    	if(rs.getString("DOC")!=null)
	    	{
	    		String cittype=rs.getString("DOC");
	    		if(cittype.equalsIgnoreCase("book"))
	    		{
	    			cittype="br";
	    		}
	    		else if(cittype.equalsIgnoreCase("press release"))
	    		{
	    			cittype="pr";
	    		}
	    		else if(cittype.equalsIgnoreCase("journal"))
	    		{
	    			cittype="ar";
	    		}
	    		singleRecordData.put("CITTYPE",cittype);
	    	}
	    	
	    	if(rs.getString("LAN")!=null)
	    	{
	    		singleRecordData.put("CITATIONLANGUAGE",rs.getString("LAN"));
	    	}
	    	
	    	if(rs.getString("ATL")!=null)
	    	{
	    		singleRecordData.put("CITATIONTITLE",dictionary.AlphaEntitysToNumericEntitys(rs.getString("ATL")));
	    	}
	    	
	    	/*
	    	if(rs.getString("AUTHORKEYWORDS")!=null)
	    	{
	    		singleRecordData.put("AUTHORKEYWORDS",dictionary.AlphaEntitysToNumericEntitys(rs.getString("AUTHORKEYWORDS")));
	    	}
	    	
	    	if(rs.getString("AUTHOR")!=null)
	    	{
	    		singleRecordData.put("AUTHOR",dictionary.AlphaEntitysToNumericEntitys(rs.getString("AUTHOR")));
	    	}
	    	
	    	if(rs.getString("AUTHOR_1")!=null)
	    	{
	    		singleRecordData.put("AUTHOR_1",dictionary.AlphaEntitysToNumericEntitys(rs.getString("AUTHOR_1")));
	    	}
	    	
	    	if(rs.getString("AFFILIATION")!=null)
	    	{
	    		singleRecordData.put("AFFILIATION",dictionary.AlphaEntitysToNumericEntitys(rs.getString("AFFILIATION")));
	    	}
	    	
	    	if(rs.getString("AFFILIATION_1")!=null)
	    	{
	    		singleRecordData.put("AFFILIATION_1",dictionary.AlphaEntitysToNumericEntitys(rs.getString("AFFILIATION_1")));
	    	}
	    	
	    	if(rs.getString("CORRESPONDENCENAME")!=null)
	    	{
	    		singleRecordData.put("CORRESPONDENCENAME",dictionary.AlphaEntitysToNumericEntitys(rs.getString("CORRESPONDENCENAME")));
	    	}
	    	
	    	if(rs.getString("correspondenceaffiliation")!=null)
	    	{
	    		singleRecordData.put("CORRESPONDENCEAFFILIATION",dictionary.AlphaEntitysToNumericEntitys(rs.getString("correspondenceaffiliation")));
	    	}
	    	
	    	if(rs.getString("CORRESPONDENCEEADDRESS")!=null)
	    	{
	    		singleRecordData.put("CORRESPONDENCEEADDRESS",dictionary.AlphaEntitysToNumericEntitys(rs.getString("CORRESPONDENCEEADDRESS")));
	    	}
	    	
	    	if(rs.getString("GRANTLIST")!=null)
	    	{
	    		singleRecordData.put("GRANTLIST",dictionary.AlphaEntitysToNumericEntitys(rs.getString("GRANTLIST")));
	    	}
	    	*/
	    	if(rs.getString("ABS")!=null)
	    	{
	    		singleRecordData.put("ABSTRACTDATA",dictionary.AlphaEntitysToNumericEntitys(rs.getString("ABS")));
	    	}
	    	
	    	/*
	    	if(rs.getString("ABSTRACTORIGINAL")!=null)
	    	{
	    		singleRecordData.put("ABSTRACTORIGINAL",dictionary.AlphaEntitysToNumericEntitys(rs.getString("ABSTRACTORIGINAL")));
	    	}
	    	
	    	if(rs.getString("ABSTRACTPERSPECTIVE")!=null)
	    	{
	    		singleRecordData.put("ABSTRACTPERSPECTIVE",dictionary.AlphaEntitysToNumericEntitys(rs.getString("ABSTRACTPERSPECTIVE")));
	    	}
	    	*/
	    	
	    	if(rs.getString("SOURCE_TYPE")!=null)
	    	{
	    		singleRecordData.put("SOURCETYPE",rs.getString("SOURCE_TYPE"));
	    	}
	    	
	    	/*
	    	if(rs.getString("SOURCECOUNTRY")!=null)
	    	{
	    		singleRecordData.put("SOURCECOUNTRY",dictionary.AlphaEntitysToNumericEntitys(rs.getString("SOURCECOUNTRY")));
	    	}
	    	
	    	if(rs.getString("SOURCEID")!=null)
	    	{
	    		singleRecordData.put("SOURCEID",rs.getString("SOURCEID"));
	    	}
	    	*/
	    	
	    	if(rs.getString("FJL")!=null)
	    	{
	    		singleRecordData.put("SOURCETITLE",dictionary.AlphaEntitysToNumericEntitys(rs.getString("FJL")));
	    	}
	    	
	    	/*
	    	if(rs.getString("SOURCETITLEABBREV")!=null)
	    	{
	    		singleRecordData.put("SOURCETITLEABBREV",dictionary.AlphaEntitysToNumericEntitys(rs.getString("SOURCETITLEABBREV")));
	    	}
	    	
	    	if(rs.getString("TRANSLATEDSOURCETITLE")!=null)
	    	{
	    		singleRecordData.put("TRANSLATEDSOURCETITLE",dictionary.AlphaEntitysToNumericEntitys(rs.getString("TRANSLATEDSOURCETITLE")));
	    	}
	    	
	    	if(rs.getString("VOLUMETITLE")!=null)
	    	{
	    		singleRecordData.put("VOLUMETITLE",dictionary.AlphaEntitysToNumericEntitys(rs.getString("VOLUMETITLE")));
	    	}
	    	
	    	if(rs.getString("ISSUETITLE")!=null)
	    	{
	    		singleRecordData.put("ISSUETITLE",dictionary.AlphaEntitysToNumericEntitys(rs.getString("ISSUETITLE")));
	    	}
	    	*/
	    	
	    	if( rs.getString("ISN")!=null)
	    	{
	    		singleRecordData.put("ISSN",rs.getString("ISN"));
	    	}
	    	
	    	/*
	    	if(rs.getString("EISSN")!=null)
	    	{
	    		singleRecordData.put("EISSN",rs.getString("EISSN"));
	    	}
	    	*/
	    	if(rs.getString("IBN")!=null)
	    	{
	    		singleRecordData.put("ISBN",rs.getString("IBN"));
	    	}
	    	
	    	
	    	if(rs.getString("CDN")!=null)
	    	{
	    		singleRecordData.put("CODEN",rs.getString("CDN"));
	    	}
	    	
	    	if(rs.getString("VOL")!=null)
	    	{
	    		singleRecordData.put("VOLUME",rs.getString("VOL"));
	    	}
	    	
	    	if(rs.getString("ISS")!=null)
	    	{
	    		singleRecordData.put("ISSUE",rs.getString("ISS"));
	    	}
	    	
	    	if(rs.getString("PAG")!=null)
	    	{
	    		singleRecordData.put("PAGE",rs.getString("PAG"));
	    	}
	    	/*
	    	if(rs.getString("PAGECOUNT")!=null)
	    	{
	    		singleRecordData.put("PAGECOUNT",rs.getString("PAGECOUNT"));
	    	}
	    	
	    	if(rs.getString("ARTICLENUMBER")!=null)
	    	{
	    		singleRecordData.put("ARTICLENUMBER",rs.getString("ARTICLENUMBER"));
	    	}
	    	*/
	    	
	    	if(rs.getString("PBD")!=null)
	    	{
	    		singleRecordData.put("PUBLICATIONDATEDATETEXT",rs.getString("PBD"));
	    	}
	    	
	    	
	    	if(rs.getString("PBN")!=null)
	    	{
	    		singleRecordData.put("PUBLICATIONDATE",rs.getString("PBN"));
	    	}
	    	
	    	if(rs.getString("AVL")!=null)
	    	{
	    		singleRecordData.put("SOURCEWEBSITE",dictionary.AlphaEntitysToNumericEntitys(rs.getString("AVL")));
	    	}
	    	
	    	/*
	    	if(rs.getString("CONTRIBUTOR")!=null)
	    	{
	    		singleRecordData.put("CONTRIBUTOR",dictionary.AlphaEntitysToNumericEntitys(rs.getString("CONTRIBUTOR")));
	    	}
	    	
	    	if(rs.getString("CONTRIBUTORAFFILIATION")!=null)
	    	{
	    		singleRecordData.put("CONTRIBUTORAFFILIATION",dictionary.AlphaEntitysToNumericEntitys(rs.getString("CONTRIBUTORAFFILIATION")));
	    	}
	    	
	    	if(rs.getString("EDITORS")!=null)
	    	{
	    		singleRecordData.put("EDITORS",dictionary.AlphaEntitysToNumericEntitys(rs.getString("EDITORS")));
	    	}
	    	
	    	
	    	if(rs.getString("PUBLISHERNAME")!=null)
	    	{
	    		singleRecordData.put("PUBLISHERNAME",dictionary.AlphaEntitysToNumericEntitys(rs.getString("PUBLISHERNAME")));
	    	}
	    	
	    	if(rs.getString("PUBLISHERADDRESS")!=null)
	    	{
	    		singleRecordData.put("PUBLISHERADDRESS",dictionary.AlphaEntitysToNumericEntitys(rs.getString("PUBLISHERADDRESS")));
	    	}
	    	
	    	if(rs.getString("PUBLISHERELECTRONICADDRESS")!=null)
	    	{
	    		singleRecordData.put("PUBLISHERELECTRONICADDRESS",dictionary.AlphaEntitysToNumericEntitys(rs.getString("PUBLISHERELECTRONICADDRESS")));
	    	}
	    	
	    	if(rs.getString("REPORTNUMBER")!=null)
	    	{
	    		singleRecordData.put("REPORTNUMBER",dictionary.AlphaEntitysToNumericEntitys(rs.getString("REPORTNUMBER")));
	    	}
	    	
	    	if(rs.getString("CONFNAME")!=null)
	    	{
	    		singleRecordData.put("CONFNAME",dictionary.AlphaEntitysToNumericEntitys(rs.getString("CONFNAME")));
	    	}
	    	
	    	if(rs.getString("CONFCATNUMBER")!=null)
	    	{
	    		singleRecordData.put("CONFCATNUMBER",rs.getString("CONFCATNUMBER"));
	    	}
	    	
	    	if(rs.getString("CONFCODE")!=null)
	    	{
	    		singleRecordData.put("CONFCODE",rs.getString("CONFCODE"));
	    	}
	    	
	    	if(rs.getString("CONFLOCATION")!=null)
	    	{
	    		singleRecordData.put("CONFLOCATION",dictionary.AlphaEntitysToNumericEntitys(rs.getString("CONFLOCATION")));
	    	}
	    	
	    	if(rs.getString("CONFDATE")!=null)
	    	{
	    		singleRecordData.put("CONFDATE",rs.getString("CONFDATE"));
	    	}
	    	
	    	if(rs.getString("CONFSPONSORS")!=null)
	    	{
	    		singleRecordData.put("CONFSPONSORS",dictionary.AlphaEntitysToNumericEntitys(rs.getString("CONFSPONSORS")));
	    	}
	    	
	    	if(rs.getString("CONFERENCEPARTNUMBER")!=null)
	    	{
	    		singleRecordData.put("CONFERENCEPARTNUMBER",rs.getString("CONFERENCEPARTNUMBER"));
	    	}
	    	
	    	if(rs.getString("CONFERENCEPAGERANGE")!=null)
	    	{
	    		singleRecordData.put("CONFERENCEPAGERANGE",rs.getString("CONFERENCEPAGERANGE"));
	    	}
	    	
	    	if(rs.getString("CONFERENCEPAGECOUNT")!=null)
	    	{
	    		singleRecordData.put("CONFERENCEPAGECOUNT",rs.getString("CONFERENCEPAGECOUNT"));
	    	}
	    	
	    	if(rs.getString("CONFERENCEEDITOR")!=null)
	    	{
	    		singleRecordData.put("CONFERENCEEDITOR",dictionary.AlphaEntitysToNumericEntitys(rs.getString("CONFERENCEEDITOR")));
	    	}
	    	
	    	if(rs.getString("CONFERENCEORGANIZATION")!=null)
	    	{
	    		singleRecordData.put("CONFERENCEORGANIZATION",dictionary.AlphaEntitysToNumericEntitys(rs.getString("CONFERENCEORGANIZATION")));
	    	}
	    	
	    	if(rs.getString("CONFERENCEEDITORADDRESS")!=null)
	    	{
	    		singleRecordData.put("CONFERENCEEDITORADDRESS",dictionary.AlphaEntitysToNumericEntitys(rs.getString("CONFERENCEEDITORADDRESS"))); 
	    	}
	    	
	    	if(rs.getString("CONTROLLEDTERM")!=null)
	    	{
	    		singleRecordData.put("CONTROLLEDTERM",dictionary.AlphaEntitysToNumericEntitys(rs.getString("CONTROLLEDTERM")));   
	    	}
	    	
	    	if(rs.getString("UNCONTROLLEDTERM")!=null)
	    	{
	    		singleRecordData.put("UNCONTROLLEDTERM",dictionary.AlphaEntitysToNumericEntitys(rs.getString("UNCONTROLLEDTERM"))); 
	    	}
	    	
	    	if(rs.getString("MAINHEADING")!=null)
	    	{
	    		singleRecordData.put("MAINHEADING",dictionary.AlphaEntitysToNumericEntitys(rs.getString("MAINHEADING")));      
	    	}

	    	if(rs.getString("SPECIESTERM")!=null)
	    	{
	    		singleRecordData.put("SPECIESTERM",dictionary.AlphaEntitysToNumericEntitys(rs.getString("SPECIESTERM"))); 
	    	}
	    	
	    	if(rs.getString("REGIONALTERM")!=null)
	    	{	    		
	    		singleRecordData.put("REGIONALTERM",dictionary.AlphaEntitysToNumericEntitys(rs.getString("REGIONALTERM")));    
	    	}
	    	
	    	if(rs.getString("TREATMENTCODE")!=null)
	    	{
	    		singleRecordData.put("TREATMENTCODE",rs.getString("TREATMENTCODE")); 
	    	}
	    	*/
	    	
	    	if(rs.getString("SCC")!=null)
	    	{
	    		singleRecordData.put("CBNBGEOCLASSIFICATIONCODE",rs.getString("SCC")); 
	    	}
	    	
	    	
	    	if(rs.getString("SCT")!=null)
	    	{
	    		singleRecordData.put("CBNBGEOCLASSIFICATIONDESC",rs.getString("SCT"));    
	    	}
	    	
	    	if(rs.getString("SIC")!=null)
	    	{
	    		singleRecordData.put("CBNBSICCLASSIFICATIONDESC",dictionary.AlphaEntitysToNumericEntitys(rs.getString("SIC")));
	    	}
	    	
	    	if(rs.getString("GIC")!=null)
	    	{
	    		singleRecordData.put("CBNBSECTORCLASSIFICATIONCODE",dictionary.AlphaEntitysToNumericEntitys(rs.getString("GIC")));
	    	}
	    	
	    	if(rs.getString("GID")!=null)
	    	{
	    		singleRecordData.put("CBNBSECTORCLASSIFICATIONDESC",dictionary.AlphaEntitysToNumericEntitys(rs.getString("GID")));
	    	}
	    	
	    	if(rs.getString("SRC")!=null)
	    	{
	    		singleRecordData.put("CBETERM",rs.getString("SRC"));
	    	}
	    	
	    	if(rs.getString("EBT")!=null)
	    	{
	    		singleRecordData.put("CBBTERM",dictionary.AlphaEntitysToNumericEntitys(rs.getString("EBT")));
	    	}
	    	
	    	if(rs.getString("CIN")!=null)
	    	{
	    		singleRecordData.put("CBCTERM",rs.getString("CIN"));
	    	}
	    	
	    	if(rs.getString("REG")!=null)
	    	{
	    		singleRecordData.put("CNCTERM",rs.getString("REG"));
	    	}
	    	
	    	if(rs.getString("CYM")!=null)
	    	{
	    		singleRecordData.put("CBATERM",rs.getString("CYM"));
	    	}
	    	
	    	if(rs.getString("OTL")!=null)
	    	{
	    		singleRecordData.put("CBNBFOREIGNTITLE",rs.getString("OTL"));
	    	}
	    	/*
	    	if(rs.getString("ISOPENACESS")!=null)
	    	{
	    		singleRecordData.put("ISOPENACESS",rs.getString("ISOPENACESS"));
	    	}
	    	
	    	if(rs.getString("SOURCEBIBTEXT")!=null)
	    	{
	    		singleRecordData.put("SOURCEBIBTEXT",rs.getString("SOURCEBIBTEXT"));
	    	}
	    	
	    	if(rs.getString("GRANTTEXT")!=null)
	    	{
	    		singleRecordData.put("GRANTTEXT",rs.getString("GRANTTEXT"));
	    	}
	    	*/
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    
		return singleRecordData;
	}
    
    public String replaceNull(String sVal) {

		if (sVal == null)
			sVal = "";

		return sVal;
	}

	public String[] getCvs(List list) {

		if (list != null && list.size() > 0) {
			return (String[]) list.toArray(new String[0]);

		}
		return null;
	}
    
    public Hashtable getBDDataFromDatabase(ResultSet rs)
    {

    	Hashtable singleRecordData = new Hashtable();
    	try 
    	{
    		singleRecordData.put("ACCESSNUMBER",rs.getString("ACCESSNUMBER"));
	    	if(rs.getString("DATESORT")!=null)
	    	{
	    		singleRecordData.put("DATESORT", rs.getString("DATESORT"));
	    	}
	    	
	    	if(rs.getString("COPYRIGHT")!=null)
	    	{
	    		singleRecordData.put("COPYRIGHT", dictionary.AlphaEntitysToNumericEntitys(rs.getString("COPYRIGHT")));
	    	}
	    	
	    	if(rs.getString("DOI")!=null)
	    	{
	    		singleRecordData.put("DOI",rs.getString("DOI"));
	    	}
	    	
	    	if(rs.getString("PUI")!=null)
	    	{
	    		singleRecordData.put("PUI", rs.getString("PUI"));
	    	}
	    	
	    	if(rs.getString("DATABASE")!=null)
	    	{
	    		singleRecordData.put("DATABASE",rs.getString("DATABASE"));
	    	}
	    	
	    	if(rs.getString("CITTYPE")!=null)
	    	{
	    		singleRecordData.put("CITTYPE",rs.getString("CITTYPE"));
	    	}
	    	
	    	if(rs.getString("CITATIONLANGUAGE")!=null)
	    	{
	    		singleRecordData.put("CITATIONLANGUAGE",rs.getString("CITATIONLANGUAGE"));
	    	}
	    	
	    	if(rs.getString("CITATIONTITLE")!=null)
	    	{
	    		singleRecordData.put("CITATIONTITLE",dictionary.AlphaEntitysToNumericEntitys(rs.getString("CITATIONTITLE")));
	    	}
	    	
	    	if(rs.getString("AUTHORKEYWORDS")!=null)
	    	{
	    		singleRecordData.put("AUTHORKEYWORDS",dictionary.AlphaEntitysToNumericEntitys(rs.getString("AUTHORKEYWORDS")));
	    	}
	    	
	    	if(rs.getString("AUTHOR")!=null)
	    	{
	    		singleRecordData.put("AUTHOR",dictionary.AlphaEntitysToNumericEntitys(rs.getString("AUTHOR")));
	    	}
	    	
	    	if(rs.getString("AUTHOR_1")!=null)
	    	{
	    		singleRecordData.put("AUTHOR_1",dictionary.AlphaEntitysToNumericEntitys(rs.getString("AUTHOR_1")));
	    	}
	    	
	    	if(rs.getString("AFFILIATION")!=null)
	    	{
	    		singleRecordData.put("AFFILIATION",dictionary.AlphaEntitysToNumericEntitys(rs.getString("AFFILIATION")));
	    	}
	    	
	    	if(rs.getString("AFFILIATION_1")!=null)
	    	{
	    		singleRecordData.put("AFFILIATION_1",dictionary.AlphaEntitysToNumericEntitys(rs.getString("AFFILIATION_1")));
	    	}
	    	
	    	if(rs.getString("CORRESPONDENCENAME")!=null)
	    	{
	    		singleRecordData.put("CORRESPONDENCENAME",dictionary.AlphaEntitysToNumericEntitys(rs.getString("CORRESPONDENCENAME")));
	    	}
	    	
	    	if(rs.getString("CORRESPONDENCEAFFILIATION")!=null)
	    	{
	    		singleRecordData.put("CORRESPONDENCEAFFILIATION",dictionary.AlphaEntitysToNumericEntitys(rs.getString("CORRESPONDENCEAFFILIATION")));
	    	}
	    	
	    	if(rs.getString("CORRESPONDENCEEADDRESS")!=null)
	    	{
	    		singleRecordData.put("CORRESPONDENCEEADDRESS",dictionary.AlphaEntitysToNumericEntitys(rs.getString("CORRESPONDENCEEADDRESS")));
	    	}
	    	
	    	if(rs.getString("GRANTLIST")!=null)
	    	{
	    		singleRecordData.put("GRANTLIST",dictionary.AlphaEntitysToNumericEntitys(rs.getString("GRANTLIST")));
	    	}
	    	
	    	if(rs.getString("ABSTRACTDATA")!=null)
	    	{
	    		singleRecordData.put("ABSTRACTDATA",dictionary.AlphaEntitysToNumericEntitys(rs.getString("ABSTRACTDATA")));
	    	}
	    	
	    	if(rs.getString("ABSTRACTORIGINAL")!=null)
	    	{
	    		singleRecordData.put("ABSTRACTORIGINAL",dictionary.AlphaEntitysToNumericEntitys(rs.getString("ABSTRACTORIGINAL")));
	    	}
	    	
	    	if(rs.getString("ABSTRACTPERSPECTIVE")!=null)
	    	{
	    		singleRecordData.put("ABSTRACTPERSPECTIVE",dictionary.AlphaEntitysToNumericEntitys(rs.getString("ABSTRACTPERSPECTIVE")));
	    	}
	    	
	    	if(rs.getString("SOURCETYPE")!=null)
	    	{
	    		singleRecordData.put("SOURCETYPE",rs.getString("SOURCETYPE"));
	    	}
	    	
	    	if(rs.getString("SOURCECOUNTRY")!=null)
	    	{
	    		singleRecordData.put("SOURCECOUNTRY",dictionary.AlphaEntitysToNumericEntitys(rs.getString("SOURCECOUNTRY")));
	    	}
	    	
	    	if(rs.getString("SOURCEID")!=null)
	    	{
	    		singleRecordData.put("SOURCEID",rs.getString("SOURCEID"));
	    	}
	    	
	    	if(rs.getString("SOURCETITLE")!=null)
	    	{
	    		singleRecordData.put("SOURCETITLE",dictionary.AlphaEntitysToNumericEntitys(rs.getString("SOURCETITLE")));
	    	}
	    	
	    	if(rs.getString("SOURCETITLEABBREV")!=null)
	    	{
	    		singleRecordData.put("SOURCETITLEABBREV",dictionary.AlphaEntitysToNumericEntitys(rs.getString("SOURCETITLEABBREV")));
	    	}
	    	
	    	if(rs.getString("TRANSLATEDSOURCETITLE")!=null)
	    	{
	    		singleRecordData.put("TRANSLATEDSOURCETITLE",dictionary.AlphaEntitysToNumericEntitys(rs.getString("TRANSLATEDSOURCETITLE")));
	    	}
	    	
	    	if(rs.getString("VOLUMETITLE")!=null)
	    	{
	    		singleRecordData.put("VOLUMETITLE",dictionary.AlphaEntitysToNumericEntitys(rs.getString("VOLUMETITLE")));
	    	}
	    	
	    	if(rs.getString("ISSUETITLE")!=null)
	    	{
	    		singleRecordData.put("ISSUETITLE",dictionary.AlphaEntitysToNumericEntitys(rs.getString("ISSUETITLE")));
	    	}
	    	
	    	if( rs.getString("ISSN")!=null)
	    	{
	    		singleRecordData.put("ISSN",rs.getString("ISSN"));
	    	}
	    	
	    	if(rs.getString("EISSN")!=null)
	    	{
	    		singleRecordData.put("EISSN",rs.getString("EISSN"));
	    	}
	    	
	    	if(rs.getString("ISBN")!=null)
	    	{
	    		singleRecordData.put("ISBN",rs.getString("ISBN"));
	    	}
	    	
	    	if(rs.getString("CODEN")!=null)
	    	{
	    		singleRecordData.put("CODEN",rs.getString("CODEN"));
	    	}
	    	
	    	if(rs.getString("VOLUME")!=null)
	    	{
	    		singleRecordData.put("VOLUME",rs.getString("VOLUME"));
	    	}
	    	
	    	if(rs.getString("ISSUE")!=null)
	    	{
	    		singleRecordData.put("ISSUE",rs.getString("ISSUE"));
	    	}
	    	
	    	if(rs.getString("PAGE")!=null)
	    	{
	    		singleRecordData.put("PAGE",rs.getString("PAGE"));
	    	}
	    	
	    	if(rs.getString("PAGECOUNT")!=null)
	    	{
	    		singleRecordData.put("PAGECOUNT",rs.getString("PAGECOUNT"));
	    	}
	    	
	    	if(rs.getString("ARTICLENUMBER")!=null)
	    	{
	    		singleRecordData.put("ARTICLENUMBER",rs.getString("ARTICLENUMBER"));
	    	}
	    	
	    	if(rs.getString("PUBLICATIONYEAR")!=null)
	    	{
	    		singleRecordData.put("PUBLICATIONYEAR",rs.getString("PUBLICATIONYEAR"));
	    	}
	    	
	    	if(rs.getString("PUBLICATIONDATE")!=null)
	    	{
	    		singleRecordData.put("PUBLICATIONDATE",rs.getString("PUBLICATIONDATE"));
	    	}
	    	
	    	if(rs.getString("SOURCEWEBSITE")!=null)
	    	{
	    		singleRecordData.put("SOURCEWEBSITE",dictionary.AlphaEntitysToNumericEntitys(rs.getString("SOURCEWEBSITE")));
	    	}
	    	
	    	if(rs.getString("CONTRIBUTOR")!=null)
	    	{
	    		singleRecordData.put("CONTRIBUTOR",dictionary.AlphaEntitysToNumericEntitys(rs.getString("CONTRIBUTOR")));
	    	}
	    	
	    	if(rs.getString("CONTRIBUTORAFFILIATION")!=null)
	    	{
	    		singleRecordData.put("CONTRIBUTORAFFILIATION",dictionary.AlphaEntitysToNumericEntitys(rs.getString("CONTRIBUTORAFFILIATION")));
	    	}
	    	
	    	if(rs.getString("EDITORS")!=null)
	    	{
	    		singleRecordData.put("EDITORS",dictionary.AlphaEntitysToNumericEntitys(rs.getString("EDITORS")));
	    	}
	    	
	    	if(rs.getString("PUBLISHERNAME")!=null)
	    	{
	    		singleRecordData.put("PUBLISHERNAME",dictionary.AlphaEntitysToNumericEntitys(rs.getString("PUBLISHERNAME")));
	    	}
	    	
	    	if(rs.getString("PUBLISHERADDRESS")!=null)
	    	{
	    		singleRecordData.put("PUBLISHERADDRESS",dictionary.AlphaEntitysToNumericEntitys(rs.getString("PUBLISHERADDRESS")));
	    	}
	    	
	    	if(rs.getString("PUBLISHERELECTRONICADDRESS")!=null)
	    	{
	    		singleRecordData.put("PUBLISHERELECTRONICADDRESS",dictionary.AlphaEntitysToNumericEntitys(rs.getString("PUBLISHERELECTRONICADDRESS")));
	    	}
	    	
	    	if(rs.getString("REPORTNUMBER")!=null)
	    	{
	    		singleRecordData.put("REPORTNUMBER",dictionary.AlphaEntitysToNumericEntitys(rs.getString("REPORTNUMBER")));
	    	}
	    	
	    	if(rs.getString("CONFNAME")!=null)
	    	{
	    		singleRecordData.put("CONFNAME",dictionary.AlphaEntitysToNumericEntitys(rs.getString("CONFNAME")));
	    	}
	    	
	    	if(rs.getString("CONFCATNUMBER")!=null)
	    	{
	    		singleRecordData.put("CONFCATNUMBER",rs.getString("CONFCATNUMBER"));
	    	}
	    	
	    	if(rs.getString("CONFCODE")!=null)
	    	{
	    		singleRecordData.put("CONFCODE",rs.getString("CONFCODE"));
	    	}
	    	
	    	if(rs.getString("CONFLOCATION")!=null)
	    	{
	    		singleRecordData.put("CONFLOCATION",dictionary.AlphaEntitysToNumericEntitys(rs.getString("CONFLOCATION")));
	    	}
	    	
	    	if(rs.getString("CONFDATE")!=null)
	    	{
	    		singleRecordData.put("CONFDATE",rs.getString("CONFDATE"));
	    	}
	    	
	    	if(rs.getString("CONFSPONSORS")!=null)
	    	{
	    		singleRecordData.put("CONFSPONSORS",dictionary.AlphaEntitysToNumericEntitys(rs.getString("CONFSPONSORS")));
	    	}
	    	
	    	if(rs.getString("CONFERENCEPARTNUMBER")!=null)
	    	{
	    		singleRecordData.put("CONFERENCEPARTNUMBER",rs.getString("CONFERENCEPARTNUMBER"));
	    	}
	    	
	    	if(rs.getString("CONFERENCEPAGERANGE")!=null)
	    	{
	    		singleRecordData.put("CONFERENCEPAGERANGE",rs.getString("CONFERENCEPAGERANGE"));
	    	}
	    	
	    	if(rs.getString("CONFERENCEPAGECOUNT")!=null)
	    	{
	    		singleRecordData.put("CONFERENCEPAGECOUNT",rs.getString("CONFERENCEPAGECOUNT"));
	    	}
	    	
	    	if(rs.getString("CONFERENCEEDITOR")!=null)
	    	{
	    		singleRecordData.put("CONFERENCEEDITOR",dictionary.AlphaEntitysToNumericEntitys(rs.getString("CONFERENCEEDITOR")));
	    	}
	    	
	    	if(rs.getString("CONFERENCEORGANIZATION")!=null)
	    	{
	    		singleRecordData.put("CONFERENCEORGANIZATION",dictionary.AlphaEntitysToNumericEntitys(rs.getString("CONFERENCEORGANIZATION")));
	    	}
	    	
	    	if(rs.getString("CONFERENCEEDITORADDRESS")!=null)
	    	{
	    		singleRecordData.put("CONFERENCEEDITORADDRESS",dictionary.AlphaEntitysToNumericEntitys(rs.getString("CONFERENCEEDITORADDRESS"))); 
	    	}
	    	
	    	if(rs.getString("CONTROLLEDTERM")!=null)
	    	{
	    		singleRecordData.put("CONTROLLEDTERM",dictionary.AlphaEntitysToNumericEntitys(rs.getString("CONTROLLEDTERM")));   
	    	}
	    	
	    	//added by hmo for elt at 2/28/2024
	    	//controlledterm
	    	if(rs.getString("APICT")!=null || rs.getString("APICT1")!=null)
	    	{		    	
				String apict = replaceNull(rs.getString("APICT"));
				String apict1 = replaceNull(rs.getString("APICT1"));
	
				if (apict != null && !apict.trim().equals("")) {
					if (apict1 != null && !apict1.trim().equals("")) {
						apict = apict+Constants.AUDELIMITER+apict1;
					}				
				}
				singleRecordData.put("APICT",dictionary.AlphaEntitysToNumericEntitys(apict)); 									
	    	}
	    	
	    	//Linked terms  added by hmo for elt at 2/28/2024
	    	if(rs.getString("APILT")!=null)
	    	{
	    		String apilt=rs.getString("APILT");
	    		singleRecordData.put("APILT",dictionary.AlphaEntitysToNumericEntitys(apilt));
	    	}
	    	
	    	if(rs.getString("APIAMS")!=null)
	    	{
	    		String apiams=rs.getString("APIAMS");
	    		singleRecordData.put("APIAMS",dictionary.AlphaEntitysToNumericEntitys(apiams));
	    	}
	    	
	    	if(rs.getString("APILTM")!=null)
	    	{
	    		String apiltm=rs.getString("APILTM");
	    		singleRecordData.put("APILTM",dictionary.AlphaEntitysToNumericEntitys(apiltm));
	    	}
	    	
	    	if(rs.getString("APIAPC")!=null)
	    	{
	    		String apiapc=rs.getString("APIAPC");
	    		singleRecordData.put("APIAPC",dictionary.AlphaEntitysToNumericEntitys(apiapc));
	    	}
	    	
	    	if(rs.getString("APIAT")!=null)
	    	{
	    		String apiat=rs.getString("APIAT");
	    		singleRecordData.put("APIAT",dictionary.AlphaEntitysToNumericEntitys(apiat));
	    	}
	    	
	    	/*
	    	//CAS Registry Numbers
	    	if(rs.getString("CASREGISTRYNUMBER")!=null)
	    	{
	    		String casNumber=rs.getString("CASREGISTRYNUMBER");
	    		singleRecordData.put("CASREGISTRYNUMBER",dictionary.AlphaEntitysToNumericEntitys(casNumber));
	    	}
	    	*/
	    	
	    	
	    	//classification
	    	if(rs.getString("CLASSIFICATIONDESC")!=null)
	    	{
	    		String classification=rs.getString("CLASSIFICATIONDESC");
	    		singleRecordData.put("CLASSIFICATION",dictionary.AlphaEntitysToNumericEntitys(classification));
	    	}
	    	
	    	if(rs.getString("UNCONTROLLEDTERM")!=null)
	    	{
	    		singleRecordData.put("UNCONTROLLEDTERM",dictionary.AlphaEntitysToNumericEntitys(rs.getString("UNCONTROLLEDTERM"))); 
	    	}
	    	
	    	if(rs.getString("MAINHEADING")!=null)
	    	{
	    		singleRecordData.put("MAINHEADING",dictionary.AlphaEntitysToNumericEntitys(rs.getString("MAINHEADING")));      
	    	}
	    	
	    	if(rs.getString("SPECIESTERM")!=null)
	    	{
	    		singleRecordData.put("SPECIESTERM",dictionary.AlphaEntitysToNumericEntitys(rs.getString("SPECIESTERM"))); 
	    	}
	    	
	    	if(rs.getString("REGIONALTERM")!=null)
	    	{	    		
	    		singleRecordData.put("REGIONALTERM",dictionary.AlphaEntitysToNumericEntitys(rs.getString("REGIONALTERM")));    
	    	}
	    	
	    	if(rs.getString("TREATMENTCODE")!=null)
	    	{
	    		singleRecordData.put("TREATMENTCODE",rs.getString("TREATMENTCODE")); 
	    	}
	    	
	    	if(rs.getString("CLASSIFICATIONCODE")!=null)
	    	{
	    		singleRecordData.put("CLASSIFICATIONCODE",rs.getString("CLASSIFICATIONCODE")); 
	    	}
	    	
	    	if(rs.getString("REFCOUNT")!=null)
	    	{
	    		singleRecordData.put("REFCOUNT",rs.getString("REFCOUNT"));    
	    	}
	    	
	    	if(rs.getString("MANUFACTURER")!=null)
	    	{
	    		singleRecordData.put("MANUFACTURER",dictionary.AlphaEntitysToNumericEntitys(rs.getString("MANUFACTURER")));
	    	}
	    	
	    	if(rs.getString("TRADENAME")!=null)
	    	{
	    		singleRecordData.put("TRADENAME",dictionary.AlphaEntitysToNumericEntitys(rs.getString("TRADENAME")));
	    	}
	    	
	    	if(rs.getString("SEQUENCEBANKS")!=null)
	    	{
	    		singleRecordData.put("SEQUENCEBANKS",dictionary.AlphaEntitysToNumericEntitys(rs.getString("SEQUENCEBANKS")));
	    	}
	    	
	    	if(rs.getString("CASREGISTRYNUMBER")!=null)
	    	{
	    		singleRecordData.put("CASREGISTRYNUMBER",rs.getString("CASREGISTRYNUMBER"));
	    	}
	    	
	    	if(rs.getString("CHEMICALTERM")!=null)
	    	{
	    		singleRecordData.put("CHEMICALTERM",dictionary.AlphaEntitysToNumericEntitys(rs.getString("CHEMICALTERM")));
	    	}
	    	
	    	if(rs.getString("REFERENCE_FLAG")!=null)
	    	{
	    		singleRecordData.put("REFERENCE_FLAG",rs.getString("REFERENCE_FLAG"));
	    	}
	    	
	    	if(rs.getString("STANDARDID")!=null)
	    	{
	    		singleRecordData.put("STANDARDID",rs.getString("STANDARDID"));
	    	}
	    	
	    	if(rs.getString("STANDARDDESIGNATION")!=null)
	    	{
	    		singleRecordData.put("STANDARDDESIGNATION",rs.getString("STANDARDDESIGNATION"));
	    	}
	    	
	    	if(rs.getString("NORMSTANDARDID")!=null)
	    	{
	    		singleRecordData.put("NORMSTANDARDID",rs.getString("NORMSTANDARDID"));
	    	}
	    	
	    	if(rs.getString("ISOPENACESS")!=null)
	    	{
	    		singleRecordData.put("ISOPENACESS",rs.getString("ISOPENACESS"));
	    	}
	    	
	    	if(rs.getString("SOURCEBIBTEXT")!=null)
	    	{
	    		singleRecordData.put("SOURCEBIBTEXT",rs.getString("SOURCEBIBTEXT"));
	    	}
	    	
	    	if(rs.getString("GRANTTEXT")!=null)
	    	{
	    		singleRecordData.put("GRANTTEXT",rs.getString("GRANTTEXT"));
	    	}
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	return singleRecordData;
    }
    
  
    public void writeRecord(Hashtable rs, FileWriter file) throws Exception
    {
    	
    	Calendar calendar = Calendar.getInstance();
    	int year= calendar.get(Calendar.YEAR);
    	int month = calendar.get(Calendar.MONTH);
    	int day = calendar.get(Calendar.DATE);    	
    	String time = calendar.getTime().toString();
    	String dateSort = (String)rs.get("DATESORT");
    	String accessnumber = (String)rs.get("ACCESSNUMBER");
    	this.accessnumber=accessnumber;
    	String copyright = dictionary.AlphaEntitysToNumericEntitys((String)rs.get("COPYRIGHT"));
    	String doi = (String)rs.get("DOI");
    	String pui = (String)rs.get("PUI");
    	String database = (String)rs.get("DATABASE");
    	String documentType = (String)rs.get("CITTYPE");
    	String citationLanguage = (String)rs.get("CITATIONLANGUAGE");
    	String citationTitle = dictionary.AlphaEntitysToNumericEntitys((String)rs.get("CITATIONTITLE"));
    	//System.out.println("AUTHORKEYWORD1 "+rs.getString("AUTHORKEYWORDS"));
    	String authorKeyword = dictionary.AlphaEntitysToNumericEntitys((String)rs.get("AUTHORKEYWORDS"));
    	//System.out.println("AUTHORKEYWORD1 "+authorKeyword);
    	String authorString = dictionary.AlphaEntitysToNumericEntitys((String)rs.get("AUTHOR"));
    	String author1String = dictionary.AlphaEntitysToNumericEntitys((String)rs.get("AUTHOR_1"));
    	String affiliationString = dictionary.AlphaEntitysToNumericEntitys((String)rs.get("AFFILIATION"));
    	String affiliation1String = dictionary.AlphaEntitysToNumericEntitys((String)rs.get("AFFILIATION_1"));
    	String correspondencename = dictionary.AlphaEntitysToNumericEntitys((String)rs.get("CORRESPONDENCENAME"));
    	String correspondenceaffiliation = dictionary.AlphaEntitysToNumericEntitys((String)rs.get("CORRESPONDENCEAFFILIATION"));
    	String correspondenceeaddress = dictionary.AlphaEntitysToNumericEntitys((String)rs.get("CORRESPONDENCEEADDRESS"));
    	String grantlist = dictionary.AlphaEntitysToNumericEntitys((String)rs.get("GRANTLIST"));
    	String abstractdata = dictionary.AlphaEntitysToNumericEntitys((String)rs.get("ABSTRACTDATA"));
    	String abstract_original = dictionary.AlphaEntitysToNumericEntitys((String)rs.get("ABSTRACTORIGINAL"));
		if(abstract_original==null)
		{
		   abstract_original="y";
		}
		
    	String abstract_perspective = dictionary.AlphaEntitysToNumericEntitys((String)rs.get("ABSTRACTPERSPECTIVE"));
    	String sourcetype = (String)rs.get("SOURCETYPE");
    	String sourcecountry = dictionary.AlphaEntitysToNumericEntitys((String)rs.get("SOURCECOUNTRY"));
    	String sourceid = (String)rs.get("SOURCEID");
    	String sourcetitle = dictionary.AlphaEntitysToNumericEntitys((String)rs.get("SOURCETITLE"));
    	String sourcetitleabbrev = dictionary.AlphaEntitysToNumericEntitys((String)rs.get("SOURCETITLEABBREV"));
    	String translatedsourcetitle = dictionary.AlphaEntitysToNumericEntitys((String)rs.get("TRANSLATEDSOURCETITLE"));
    	String volumetitle = dictionary.AlphaEntitysToNumericEntitys((String)rs.get("VOLUMETITLE"));
    	String issuetitle = dictionary.AlphaEntitysToNumericEntitys((String)rs.get("ISSUETITLE"));
    	String issn = (String)rs.get("ISSN");
    	String eissn = (String)rs.get("EISSN");
    	String isbnString = (String)rs.get("ISBN");
    	String coden = (String)rs.get("CODEN");
    	String volume = (String)rs.get("VOLUME");
    	String issue = (String)rs.get("ISSUE");
    	String page = (String)rs.get("PAGE");
    	String pagecount = (String)rs.get("PAGECOUNT");
    	String articlenumber = (String)rs.get("ARTICLENUMBER");
    	String publicationyear = (String)rs.get("PUBLICATIONYEAR");
    	String publicationdate = (String)rs.get("PUBLICATIONDATE");
    	String sourcewebsite = dictionary.AlphaEntitysToNumericEntitys((String)rs.get("SOURCEWEBSITE"));
    	String contributor = dictionary.AlphaEntitysToNumericEntitys((String)rs.get("CONTRIBUTOR"));
    	String contributoraffiliation = dictionary.AlphaEntitysToNumericEntitys((String)rs.get("CONTRIBUTORAFFILIATION"));
    	String editors = dictionary.AlphaEntitysToNumericEntitys((String)rs.get("EDITORS"));
    	String publishername = dictionary.AlphaEntitysToNumericEntitys((String)rs.get("PUBLISHERNAME"));
    	String publisheraddress = dictionary.AlphaEntitysToNumericEntitys((String)rs.get("PUBLISHERADDRESS"));
    	String publisherelectronicaddress = dictionary.AlphaEntitysToNumericEntitys((String)rs.get("PUBLISHERELECTRONICADDRESS"));
    	String reportnumber = dictionary.AlphaEntitysToNumericEntitys((String)rs.get("REPORTNUMBER"));
    	String confname =  dictionary.AlphaEntitysToNumericEntitys((String)rs.get("CONFNAME"));
    	String confcatnumber =  (String)rs.get("CONFCATNUMBER");
    	String confcode =  (String)rs.get("CONFCODE");
    	String conflocation =  dictionary.AlphaEntitysToNumericEntitys((String)rs.get("CONFLOCATION"));
    	String confdate =  (String)rs.get("CONFDATE");
    	String confsponsors =  dictionary.AlphaEntitysToNumericEntitys((String)rs.get("CONFSPONSORS"));
    	String confencepartnumber = (String)rs.get("CONFERENCEPARTNUMBER");
    	String confercepagerange = (String)rs.get("CONFERENCEPAGERANGE");
    	String confencepagecount = (String)rs.get("CONFERENCEPAGECOUNT");
    	String confenceeditor = dictionary.AlphaEntitysToNumericEntitys((String)rs.get("CONFERENCEEDITOR"));
    	String conferenceorganization = dictionary.AlphaEntitysToNumericEntitys((String)rs.get("CONFERENCEORGANIZATION"));
    	String conferenceeditoraddress = dictionary.AlphaEntitysToNumericEntitys((String)rs.get("CONFERENCEEDITORADDRESS")); 	
    	String controlledterm = dictionary.AlphaEntitysToNumericEntitys((String)rs.get("CONTROLLEDTERM"));                  
    	String uncontrolledterm = dictionary.AlphaEntitysToNumericEntitys((String)rs.get("UNCONTROLLEDTERM"));               
    	String mainheading = dictionary.AlphaEntitysToNumericEntitys((String)rs.get("MAINHEADING")); 
    	String apict=dictionary.AlphaEntitysToNumericEntitys((String)rs.get("APICT")); 
    	String apilt=dictionary.AlphaEntitysToNumericEntitys((String)rs.get("APILT")); 
    	String apiat=dictionary.AlphaEntitysToNumericEntitys((String)rs.get("APIAT"));
    	String apiams=dictionary.AlphaEntitysToNumericEntitys((String)rs.get("APIAMS"));
    	String apiltm=dictionary.AlphaEntitysToNumericEntitys((String)rs.get("APILTM"));
    	String apiapc=dictionary.AlphaEntitysToNumericEntitys((String)rs.get("APIAPC"));
    	String classification = (String)rs.get("CLASSIFICATION");
    	//String controlledterm =null;//block out mainheading for databrick @08/13/2021
    	//String uncontrolledterm =null;//block out mainheading for databrick @08/13/2021
    	//String mainheading =null;//block out mainheading for databrick @08/13/2021
    	String speciesterm = dictionary.AlphaEntitysToNumericEntitys((String)rs.get("SPECIESTERM"));                     
    	String regionalterm = dictionary.AlphaEntitysToNumericEntitys((String)rs.get("REGIONALTERM"));                    
    	String treatmentcode = (String)rs.get("TREATMENTCODE");                   
    	String classificationcode = (String)rs.get("CLASSIFICATIONCODE");              
    	String refcount = (String)rs.get("REFCOUNT");                        
    	String manufacturer = dictionary.AlphaEntitysToNumericEntitys((String)rs.get("MANUFACTURER"));
    	String tradename = dictionary.AlphaEntitysToNumericEntitys((String)rs.get("TRADENAME"));
    	String sequencebank = dictionary.AlphaEntitysToNumericEntitys((String)rs.get("SEQUENCEBANKS"));
    	String casregistrynumber = (String)rs.get("CASREGISTRYNUMBER");
    	String chemicalterm = dictionary.AlphaEntitysToNumericEntitys((String)rs.get("CHEMICALTERM"));
    	String referenceflag = (String)rs.get("REFERENCE_FLAG");
    	String standardid = (String)rs.get("STANDARDID");
    	String standarddesignation = (String)rs.get("STANDARDDESIGNATION");
    	String normstandardid = (String)rs.get("NORMSTANDARDID");
    	String isopenaccess = (String)rs.get("ISOPENACESS");
    	String sourcebibtext = (String)rs.get("SOURCEBIBTEXT");
    	String granttext = (String)rs.get("GRANTTEXT");
    	
 	
    	file.write("<item>\n");
    	file.write("<ait:process-info>\n");
    	file.write("<ait:date-delivered year=\""+year+"\" month=\""+month+"\" day=\""+day+"\"/>\n");
    	if(dateSort!=null)
    	{
    		String[] date = dateSort.split(Constants.IDDELIMITER);
    		if(date.length==3)
    		{
    			file.write("<ait:date-sort year=\""+date[0] +"\" month=\""+date[1]+ "\" day=\""+date[2]+"\"/>\n");
    		}
    		else
    		{
    			file.write("<ait:date-sort year=\""+year+"\" month=\""+month+"\" day=\""+day+"\"/>\n");
    		}
    	}
    	else
    	{
    		file.write("<ait:date-sort year=\""+year+"\" month=\""+month+"\" day=\""+day+"\"/>\n");
    	}
    	
    	file.write("<ait:status type=\"core\" state=\"new\" stage=\"S300\"/>\n");
    	file.write("</ait:process-info>\n");
    	file.write("<bibrecord>\n");
    	file.write("<item-info>\n");
    	
    	//COPYRIGHT
    	if(copyright!=null && copyright.length()>0)
    	{
    		file.write("<copyright type=\"Elsevier\">"+copyright.trim()+"</copyright>\n");
    	}
    	else
    	{
    		file.write("<copyright type=\"Elsevier\">Copyright 2016 Elsevier B.V., All rights reserved.</copyright>\n");
    	}
    	
    	file.write("<itemidlist>\n");
    	
    	//DOI
    	if(doi!=null && doi.length()>0)
    	{
    		//doi = doi.replaceAll("<","&#60;").replaceAll(">", "&#62;").trim();
    		file.write("<ce:doi>"+cleanBadCharacters(doi)+"</ce:doi>\n");
    		//file.write("<ce:doi><![CDATA["+doi+"]]></ce:doi>\n");
    	}

    	//PUI
    	if(pui!=null && pui.length()>0)
    	{
    		file.write("<itemid idtype=\"PUI\">"+pui.trim()+"</itemid>\n");
    	}
    	
    	//ACCESSNUMBER
    	if(accessnumber!=null && accessnumber.length()>0)
    	{
    		System.out.println("ACCESSNUMBER="+accessnumber);
    		file.write("<itemid idtype=\""+database.toUpperCase()+"\">"+accessnumber+"</itemid>\n");
    	}
    	
    	//NORMSTANDARDID
    	if(normstandardid!=null && normstandardid.length()>0)
    	{
    		file.write("<itemid idtype=\"NORMSTANDARDID\">"+cleanBadCharacters(normstandardid)+"</itemid>\n");
    	}
    	
    	//"STANDARDDESIGNATION"
    	if(standarddesignation!=null && standarddesignation.length()>0)
    	{
    		file.write("<itemid idtype=\"STANDARDDESIGNATION\">"+cleanBadCharacters(standarddesignation)+"</itemid>\n");
    	}
    	
    	//STANDARDID
    	if(standardid!=null && standardid.length()>0)
    	{
    		file.write("<itemid idtype=\"STANDARDID\">"+cleanBadCharacters(standardid)+"</itemid>\n");
    	}
    	
    	file.write("</itemidlist>\n");
    	
    	//HISTORY  	   
    	//System.out.println("The current date is : " + calendar.getTime());  
        
    	 file.write("<history>\n<date-created timestamp=\""+time+"\" year=\""+year+"\" month=\""+month+"\" day=\""+day+"\"/>\n</history>\n");

    	
    	//DATABASE
    	if(database.equals("chem"))
    	{
    		file.write("<dbcollection>CHEM</dbcollection>\n");
    	}
    	else if(database.equals("cpx"))
    	{
    		file.write("<dbcollection>CPX</dbcollection>\n");
    	}
    	else if(database.equals("pch"))
    	{
    		file.write("<dbcollection>PCH</dbcollection>\n");
    	}
    	else if(database.equals("elt"))
    	{
    		file.write("<dbcollection>APILIT</dbcollection>\n");
    	}
    	else if(database.equals("geo"))
    	{
    		file.write("<dbcollection>GEO</dbcollection>\n");
    	}
    	
    	
    	file.write("</item-info>\n");
    	file.write("<head>\n");
    	file.write("<citation-info>\n");
    	
    	//CITTYPE
    	if(documentType!=null && documentType.trim().length()>0)
    	{
    		//System.out.println(documentType.trim()+" --- "+bdDocType.get(documentType.trim().toLowerCase()));
    		if(bdDocType.get(documentType.trim().toLowerCase())!=null)
    		{
    			file.write("<citation-type code=\""+bdDocType.get(documentType.trim().toLowerCase())+"\"/>\n");
    		}
    		else
    		{
    			file.write("<citation-type code=\""+documentType.trim().toLowerCase()+"\"/>\n");
    		}
    	}

    	//CITATION-LANGUAGE
    	if(citationLanguage!=null && citationLanguage.length()>0)
    	{
    		file.write("<citation-language xml:lang=\""+citationLanguage+"\"/>\n");
    		file.write("<abstract-language xml:lang=\""+citationLanguage+"\"/>\n");//not capture by us
    	}
    	
    	//AUTHOR-KEYWORD
    	if(authorKeyword!=null && authorKeyword.length()>0)
    	{
    		String[] authorKeywords = authorKeyword.split(Constants.IDDELIMITER);
    		file.write("<author-keywords>\n");
    		for(int i=0; i<authorKeywords.length;i++)
    		{
    			String keyword = authorKeywords[i];
	    		//System.out.println("keyword "+keyword);	
    			if(keyword!=null && keyword.length()>0)
    			{
    				file.write("<author-keyword>"+cleanBadCharacters(keyword)+"</author-keyword>\n");
    			}
    		}
    		file.write("</author-keywords>\n");
    		
    	}
    	
    	file.write("</citation-info>\n");
    	    	
    	//CITATIONTITLE
    	if(citationTitle!=null )
    	{
    		outputCitationTitle(file,citationTitle);    		
    	}
    	       	
		//AUTHOR and AFFILIATION
		if(author1String!=null)
		{
			authorString = authorString+author1String;
		}
		
		if(affiliation1String!=null)
		{
			affiliationString = affiliationString+affiliation1String;
		}
		outputAuthorGroup(file,authorString,affiliationString);
    	
    
    	
		//CORRESPONDENCEAFFILIATION
		if(correspondencename!=null || correspondenceaffiliation!=null || correspondenceeaddress!=null)
		{
			System.out.println("CORRESPONDENCEAFFILIATION");
			outputCorrespondence(file,correspondencename,correspondenceaffiliation,correspondenceeaddress,accessnumber);			
		}//correspondence
			
		    		
		//GRANTLIST
		if(grantlist!=null && grantlist.length()>0)
		{
			outputGrantlist(file,grantlist);	
		}//grantlist
		    		
		//ABSTRACT
		if(abstractdata!=null && abstractdata.length()>0)
		{
			outputAbstract(file,abstractdata,abstract_original, abstract_perspective);	
		}//abstract
	    		
		//SOURCE
		file.write("<source");
		if(sourcetype!=null && sourcetype.length()>0)
		{
			file.write(" type=\""+sourcetype.toLowerCase()+"\""); 
		}
		
		if(sourcecountry!=null && sourcecountry.length()>0)
		{
			file.write(" country=\""+sourcecountry+"\"");
		}
		
		if(sourceid!=null && sourceid.length()>0)
		{
			file.write(" srcid=\""+sourceid+"\"");
		}
		file.write(">\n");
		
		if(sourcetitle!=null && sourcetitle.length()>0)
		{
			file.write("<sourcetitle>"+cleanBadCharacters(sourcetitle)+"</sourcetitle>\n");
		}
		
		if(sourcetitleabbrev!=null && sourcetitleabbrev.length()>0)
		{
			file.write("<sourcetitle-abbrev>"+cleanBadCharacters(sourcetitleabbrev)+"</sourcetitle-abbrev>\n");
		}
		
		if(translatedsourcetitle!=null && translatedsourcetitle.length()>0)
		{
			file.write("<translated-sourcetitle>"+cleanBadCharacters(translatedsourcetitle)+"</translated-sourcetitle>\n");
		}
		
		if(issuetitle!=null && issuetitle.length()>0)
		{
			file.write("<issuetitle>"+cleanBadCharacters(issuetitle)+"</issuetitle>\n");
		}
		
		if(issn!=null && issn.length()>0)
		{
			file.write("<issn type=\"print\">"+issn+"</issn>\n");
		}
		
		if(eissn!=null && eissn.length()>0)
		{
			file.write("<issn type=\"electronic\">"+eissn+"</issn>\n");
		}
				
		if(isbnString!=null && isbnString.length()>0)
		{
			outputISBN(file,isbnString);					
		}
		
		if(coden!=null && coden.length()>0)
		{
			file.write("<codencode>"+coden+"</codencode>\n");
		}
		
		if((volume!=null && volume.length()>0) || (issue!=null && issue.length()>0) || (page!=null && page.length()>0) || (pagecount!=null && pagecount.length()>0))
		{	
			outputVolisspag(file,volume,issue,page,pagecount,accessnumber);	
		}
		
		if(articlenumber!=null && articlenumber.length()>0)
		{
			file.write("<article-number>"+articlenumber+"</article-number>\n");
		}

		//PUBLICATIONYEAR
		if(publicationyear!=null && publicationyear.length()>0)
		{
			outputYear(file,publicationyear);	
		}
		
		//PUBLICATIONDATE
		if(publicationdate!=null && publicationdate.length()>0)
		{
			file.write("<publicationdate>\n<date-text>"+cleanBadCharacters(publicationdate)+"</date-text>\n</publicationdate>\n");
		}
		
		//SOURCEWEBSITE
		if(sourcewebsite!=null && sourcewebsite.length()>0)
		{
			outputWebsite(file,cleanBadCharacters(sourcewebsite));			
		}
		
		//Contributor
		outputContributor(file,contributoraffiliation,contributor);
				
		if(editors!=null && editors.length()>0)
		{
			outputEditors(file,editors);		
		}
		
		if(publishername!=null || publisheraddress!=null || publisherelectronicaddress!=null)
		{
			outputPublisher(file,publishername,publisheraddress,publisherelectronicaddress);
		}
			
		if((reportnumber!=null && reportnumber.length()>0) || (confname!=null && confname.length()>0) || 
			(confcatnumber!=null && confcatnumber.length()>0) || (confcode !=null && confcode.length()>0) || 
			(conflocation!=null && conflocation.length()>0) || (confdate!=null && confdate.length()>0) ||
			(confsponsors!=null && confsponsors.length()>0))
		{
			file.write("<additional-srcinfo>\n");
			if((confname!=null && confname.length()>0) || (confcatnumber!=null && confcatnumber.length()>0) || 
    			(confcode !=null && confcode.length()>0) || (conflocation!=null && conflocation.length()>0) || 
    			(confdate!=null && confdate.length()>0) || (confsponsors!=null && confsponsors.length()>0))
			{
				file.write("<conferenceinfo>\n");
				file.write("<confevent>\n");
				if(confname!=null && confname.length()>0)
				{
					file.write("<confname>"+cleanBadCharacters(confname)+"</confname>\n");
					//System.out.println(accessnumber+" confname="+confname);
				}
				
				
				if(conflocation!=null && conflocation.length()>0)
				{
					outputConflocation(file,conflocation);
				}
				
				if(confdate!=null && confdate.length()>0)
				{
					confdate = confdate.trim();
					String[] confdateArr = confdate.split("-",-1);
					String[] dateString = null;
					file.write("<confdate>\n");
					file.write("<date-text>"+confdate+"</date-text>");
					/*
					for(int j=0;j<confdateArr.length;j++)
					{
						if(j==0)
						{
							file.write("<startdate");
						}
						else
						{
							file.write("<enddate");
						}
						
						if(confdateArr[j].indexOf(",")>-1)
						{
							String cYear=confdateArr[j].substring(confdateArr[j].indexOf(",")+1);
							file.write(" year=\""+cYear.trim()+"\"");	
							if(confdateArr[j].trim().indexOf(" ")>-1) 
							{
								String confdateA = confdateArr[j].trim();
								String cMonth = confdateA.substring(0,confdateA.indexOf(" "));
								String cDay =null;
								if(confdateA.indexOf(",")>0)
								{
									cDay = confdateA.substring(confdateA.indexOf(" "),confdateA.indexOf(","));
								}
								file.write(" month=\""+cMonth.trim()+"\"");
								if(cDay!=null)
									file.write(" day=\""+cDay.replaceAll(",", "").trim()+"\"");
							}
						}
						else
						{
							dateString = confdateArr[j].split("\\s",-1);
							System.out.println("DATESTRING LENGTH="+dateString.length);
							if(dateString.length>2)
							{
								String cMonth=dateString[0];
								String cDay=dateString[1];
								String cYear=dateString[2];
								file.write(" year=\""+cYear.trim()+"\"");	
								file.write(" month=\""+cMonth.trim()+"\"");
								file.write(" day=\""+cDay.replaceAll(",", "").trim()+"\"");
							}
							else if (dateString.length>1)  
							{
								String cMonth=dateString[0];								
								String cYear=dateString[1];
								file.write(" year=\""+cYear.trim()+"\"");	
								file.write(" month=\""+cMonth.trim()+"\"");							
							}
							else if (dateString.length>0)  
							{														
								String cYear=dateString[0];
								file.write(" year=\""+cYear.trim()+"\"");												
							}
						}
						
						
						file.write(" />\n");
					}
					*/
					file.write("</confdate>\n");
					//file.write("<confdate>\n<date-text>"+confdate+"</date-text>\n</confdate>\n");
					//System.out.println("c-confdate="+confdate);
				}
				
				if(confcatnumber!=null && confcatnumber.length()>0)
				{
					file.write("<confcatnumber>"+confcatnumber+"</confcatnumber>\n");
					//System.out.println(accessnumber+" confcatnumber="+confcatnumber);
				}
				
				if(confcode!=null && confcode.length()>0)
				{
					file.write("<confcode>"+confcode+"</confcode>\n");
					//System.out.println(accessnumber+" confcode="+confcode);
				}
				
				if(confsponsors!=null && confsponsors.length()>0)
				{
					file.write("<confsponsors>\n");
					String[] confsponsorArray = confsponsors.split(Constants.AUDELIMITER,-1);
					for(int i=0;i<confsponsorArray.length;i++)
					{
						String confsponsor = confsponsorArray[i];
						file.write("<confsponsor>"+cleanBadCharacters(confsponsor)+"</confsponsor>\n");					
					}
					file.write("</confsponsors>\n");
				}
				file.write("</confevent>\n");
				if((confencepartnumber!=null && confencepartnumber.length()>0) || (confercepagerange!=null && confercepagerange.length()>0) || 
	    			(confencepagecount !=null && confencepagecount.length()>0) || (confenceeditor!=null && confenceeditor.length()>0) || 
	    			(conferenceorganization!=null && conferenceorganization.length()>0) || (conferenceeditoraddress!=null && conferenceeditoraddress.length()>0))
    			{
    				file.write("<confpublication>\n");
    				
    				if((confenceeditor!=null && confenceeditor.length()>0) || (conferenceorganization!=null && conferenceorganization.length()>0) ||
    	    				(conferenceeditoraddress!=null && conferenceeditoraddress.length()>0))
    				{
    					outputConfeditors(file,conferenceeditoraddress,conferenceorganization,confenceeditor);	
    				}
    				
    				if(confencepartnumber!=null && confencepartnumber.length()>0)
    				{
    					file.write("<procpartno>"+confencepartnumber+"</procpartno>\n");
    				}
    				
    				if(confercepagerange!=null && confercepagerange.length()>0)
    				{
    					file.write("<procpagerange>"+confercepagerange+"</procpagerange>\n");
    				}
    				
    				if(confencepagecount!=null && confencepagecount.length()>0)
    				{
    					file.write("<procpagecount>"+confencepagecount+"</procpagecount>\n");
    				}
  				
    				file.write("</confpublication>\n");
    			}
				file.write("</conferenceinfo>\n");				    				
			}
			
			if(reportnumber!=null && reportnumber.length()>0)
			{
				file.write("<reportinfo>\n");
				file.write("<reportnumber>"+reportnumber+"</reportnumber>\n");
				file.write("</reportinfo>\n");
			}
			
			file.write("</additional-srcinfo>\n");
			
			
		}
		file.write("</source>\n");
	    		
	    //ENHANCEMENT		
		if((controlledterm!=null && controlledterm.length()>0) || (uncontrolledterm!=null && uncontrolledterm.length()>0) || 
			(mainheading!= null && mainheading.length()>0) || (speciesterm != null && speciesterm.length()>0) || 
			(regionalterm != null && regionalterm.length()>0) || (treatmentcode != null && treatmentcode.length()>0) || 
			(classificationcode != null && classificationcode.length()>0) || (manufacturer != null && manufacturer.length()>0) || 
			(tradename != null && tradename.length()>0) || (sequencebank != null && sequencebank.length()>0 ) || 
			(chemicalterm != null && chemicalterm.length()>0) || (casregistrynumber != null && casregistrynumber.length()>0))
		{
			file.write("<enhancement>\n");
			if((controlledterm!=null && controlledterm.length()>0) || (uncontrolledterm!=null && uncontrolledterm.length()>0) || 
				(mainheading!= null && mainheading.length()>0) || (speciesterm != null && speciesterm.length()>0) || 
				(regionalterm != null && regionalterm.length()>0) || (treatmentcode != null && treatmentcode.length()>0) || 
				(chemicalterm != null && chemicalterm.length()>0))		    				    				
				{
					if(database.equalsIgnoreCase("elt"))
					{
						file.write("<API-descriptorgroup>\n");
					}
					else
					{				
						file.write("<descriptorgroup>\n");
					}
					if(controlledterm!=null && controlledterm.length()>0)
					{
						outputControlledterm(file,controlledterm);							
					}//if controlledterm
					
					if(uncontrolledterm!=null && uncontrolledterm.length()>0)
					{
						outputUncontrolledterm(file,uncontrolledterm);
					}//if uncontrolledterm
					
					if(mainheading!=null && mainheading.length()>0)
					{
						outputMainHeading(file,mainheading);
					}//if mainheading
					
					if(speciesterm!=null && speciesterm.length()>0)
					{
						outputSpeciesterm(file,speciesterm);
					}//if speciesterm
					
					if(regionalterm!=null && regionalterm.length()>0)
					{
						outputRegionalterm(file,regionalterm);						
					}//if regionalterm
						    						
					if(treatmentcode!=null && treatmentcode.length()>0)
					{
						outputTreaTmentCode(file,treatmentcode);
					}//if treatmentcode
					
					if(chemicalterm!=null && chemicalterm.length()>0)
					{
						outputChemicalterm(file,chemicalterm);
					}//ifchemicalterm				
					if(database.equalsIgnoreCase("elt"))
					{
						file.write("</API-descriptorgroup>\n");
					}
					else
					{				
						file.write("</descriptorgroup>\n");
					}
				} //if descriptorgroup
			
				if(database.equalsIgnoreCase("elt") && ((classification != null && classification.length()>0) || (apict !=null && apict.length()>0) || 
					(apilt !=null && apilt.length()>0) || (apiat !=null && apiat.length()>0) ||(casregistrynumber!=null && casregistrynumber.length()>0) ||
					(apiams !=null && apiams.length()>0) || (apiapc !=null && apiapc.length()>0) || (apiltm !=null && apiltm.length()>0)))
				{
					file.write("<API-descriptorgroup>\n");
					file.write("<autoposting>\n");
					if(apilt !=null && apilt.length()>0)
					{
						outputELTTerms("API-LT",file,apilt);
					}
					if(apict !=null && apict.length()>0)
					{
						outputELTTerms("API-CT",file,apict);
					}
					if(apiat !=null && apiat.length()>0)
					{
						outputELTTerms("API-AT",file,apiat);
					}
					if(classification != null && classification.length()>0)
					{
						outputELTClassification(file,classification);
					}
					
					if(casregistrynumber!=null && casregistrynumber.length()>0)
					{
						outputELTTerms("API-CRN",file,apiat);
					}
					
					if(apiams!=null && apiams.length()>0)
					{
						outputELTMAINTerms("API-AMS",file,apiams);
					}
					
					if(apiapc!=null && apiapc.length()>0)
					{
						outputELTMAINTerms("API-APC",file,apiapc);
					}
					
					if(apiltm!=null && apiltm.length()>0)
					{
						outputELTtermgroup(file,apiltm);
					}
					
					file.write("</autoposting>\n");
					file.write("</API-descriptorgroup>\n");
				}
			
				
				if(classificationcode!=null && classificationcode.length()>0)
				{
					outputClassificationgroup(file,classificationcode);
				}//if							
				
				if(manufacturer!=null && manufacturer.length()>0)
				{
					outputManufacturergroups(file,manufacturer);
				}//if
				
				
				if(tradename!=null && tradename.length()>0)
				{
					outputTradenameGroup(file,tradename);
				}//if
				
				if(sequencebank!=null && sequencebank.length()>0)
				{
					outputSequencebanks(file,sequencebank);
				}//if
				
				if(!database.equalsIgnoreCase("elt"))
				{				
					if(casregistrynumber!=null && casregistrynumber.length()>0)
					{
						outputChemicalgroup(file, casregistrynumber);
					}//if
				}
				file.write("</enhancement>\n");
			}    							
			file.write("</head>\n");
	    	
			//REFERENCE COUNT
			if(refcount !=null && refcount.length()>0)
			{
				file.write("<tail>\n");
				if(referenceflag!=null && referenceflag.equals("YES"))
				{
					file.write("<bibliography refcount=\""+refcount+"\">\n");	
					//skip reference for now @12/2/2020 by HMO
					//file.write("<reference>SKIP REFERENCE</reference>");
					//getReference(file,accessnumber);//skip reference for databrick 
					file.write("</bibliography>\n");
							
				}
				else
				{
					file.write("<bibliography refcount=\""+refcount+"\"/>\n");
				}
				file.write("</tail>\n");
			}	    			
		
		file.write("</bibrecord>\n");
		file.write("</item>\n");    	
    }

	private void outputCitationTitle(FileWriter file,String citationTitle) throws Exception
	{
		file.write("<citation-title>\n");
		String[] ctitles = citationTitle.split(Constants.AUDELIMITER,-1);
		for(int i=0;i<ctitles.length;i++)
		{
			String ctitle = ctitles[i];
			String[] ct = ctitle.split(Constants.IDDELIMITER,-1);
			if(ct.length==4)
			{
				String title = ct[1];
				String original = ct[2];
				String lang = ct[3];
				file.write("<titletext xml:lang=\""+lang+"\" original=\""+original+"\">"+cleanBadCharacters(title)+"</titletext>\n");
			}
			else
			{
				file.write("<titletext>"+cleanBadCharacters(ctitle)+"</titletext>\n");
				//System.out.println("record "+accessnumber+" citation-title format is wrong");
			}
			
		}
		file.write("</citation-title>\n");
	}
	
	private void outputWebsite(FileWriter file,String sourcewebsite) throws Exception
	{
		file.write("<website>\n");
		String[] websiteArray = sourcewebsite.split(Constants.IDDELIMITER,-1);
		if(websiteArray.length>0)
		{
			String eaddress = null;
			String websitename = websiteArray[0];
			if(websiteArray.length>1)
			{
				eaddress = websiteArray[1];
			}
			if(websitename!=null && websitename.length()>0)
			{
				file.write("<websitename>"+cleanBadCharacters(websitename)+"</websitename>\n");
			}
			
			if(eaddress!=null && eaddress.length()>0)
			{
				file.write("<ce:e-address>"+cleanBadCharacters(eaddress)+"</ce:e-address>\n");
			}
		}
		file.write("</website>\n");
	}
    
	private void outputContributor(FileWriter file,String contributoraffiliation,String contributor) throws Exception
	{
		HashMap contributoraffiliationMap = new HashMap();
		if(contributoraffiliation!=null && contributoraffiliation.length()>0)
		{
			String[] caffiliations = contributoraffiliation.split(Constants.AUDELIMITER,-1);
			for(int i=0;i<caffiliations.length;i++)
			{
				String caffiliation = caffiliations[i];
				if(caffiliation.indexOf(Constants.IDDELIMITER)>-1)
				{
    				String caffid = caffiliation.substring(0,caffiliation.indexOf(Constants.IDDELIMITER));
    				contributoraffiliationMap.put(caffid, caffiliation);
				}
				else
				{
					//System.out.println("no contributor affid for record "+accessnumber+", caffiliation="+caffiliation);
				}
			}
		}
		
		if(contributor!=null && contributor.length()>0)
		{
			String[] contributorGroups = contributor.split(Constants.AUDELIMITER,-1);
			for(int i=0;i<contributorGroups.length;i++)
			{
				String contributorGroup = contributorGroups[i];
				file.write("<contributor-group>\n");
				if(contributorGroup!=null && contributorGroup.length()>0)
				{
					String[] contributors = contributorGroup.split(Constants.IDDELIMITER,-1);
					//System.out.println("CONTRIBUTOR SIZE="+contributors.length);
					
					
					if(contributors.length>0)
					{
						String initials = null;
						String indexed_name = null;
						String degrees = null;
						String surname = null;
						String given_name = null;
						String seq = null;
						String auid = null;
						String type = null;
						String role = null;
						String id = null;
						String contributorid = contributors[0];
						if(contributors.length>1)
						{
							seq = contributors[1];
						}
						if(contributors.length>2)
						{
							auid = contributors[2];
						}
						if(contributors.length>3)
						{
							type = contributors[3];
						}
						if(contributors.length>4)
						{
							role = (String)contributorRole.get(contributors[4]);
						}
						if(contributors.length>5)
						{
							id = contributors[5];
						}
						if(contributors.length>6)
						{
							initials = contributors[6];
						}
						if(contributors.length>7)
						{
							indexed_name = contributors[7];
						}
						if(contributors.length>8)
						{
							degrees = contributors[8];
						}
						if(contributors.length>9)
						{
							surname = contributors[9];
						}
						if(contributors.length>10)
						{
							given_name = contributors[10];
						}
						//String suffix = contributors[11];
						//String nametext = contributors[12];
						//String text = contributors[13];
						//String eaddress = contributors[14];
						file.write("<contributor");
						if(seq!=null && seq.length()>0)
						{
							file.write(" seq=\""+seq+"\"");	    							
						}
						else
						{
							file.write(" seq=\""+i+"\"");	    	
						}
						
						if(auid!=null && auid.length()>0)
						{
							file.write(" auid=\""+auid+"\"");	    							
						}
						
						if(type!=null && type.length()>0)
						{
							file.write(" type=\""+type+"\"");	    							
						}
						
						if(role!=null && role.length()>0)
						{
							file.write(" role=\""+role+"\"");	    							
						}
						file.write(">\n");
						
						if(initials!=null && initials.length()>0)
						{
							file.write("<ce:initials>"+cleanBadCharacters(initials)+"</ce:initials>\n");
						}
						
						if(indexed_name!=null && indexed_name.length()>0)
						{
							file.write("<ce:indexed-name>"+cleanBadCharacters(indexed_name)+"</ce:indexed-name>\n");
						}
						else
			    		{
			    			if(surname!=null && initials!=null)
			    			{
			    				file.write("<ce:indexed-name>"+cleanBadCharacters(surname)+" "+cleanBadCharacters(initials)+"</ce:indexed-name>\n");
			    			}
			    			else if(surname!=null && given_name!=null)
			    			{
			    				file.write("<ce:indexed-name>"+cleanBadCharacters(surname)+" "+cleanBadCharacters(given_name)+"</ce:indexed-name>\n");
			    			}
			    			else if(surname!=null)
			    			{
			    				file.write("<ce:indexed-name>"+cleanBadCharacters(surname)+"</ce:indexed-name>\n");
			    			}
			    			else if(given_name!=null)
			    			{
			    				file.write("<ce:indexed-name>"+cleanBadCharacters(given_name)+"</ce:indexed-name>\n");
			    			}
			    			else
			    			{
			    				file.write("<ce:indexed-name/>\n");
			    			}
			    		}
						
						if(degrees!=null && degrees.length()>0)
						{
							file.write("<ce:degrees>"+degrees+"</ce:degrees>\n");
						}
						
						if(surname!=null && surname.length()>0)
						{
							file.write("<ce:surname>"+cleanBadCharacters(surname)+"</ce:surname>\n");
						}
						
						if(given_name!=null && given_name.length()>0)
						{
							file.write("<ce:given-name>"+cleanBadCharacters(given_name)+"</ce:given-name>\n");
						}
						file.write("</contributor>\n");
						
						if(contributoraffiliationMap.get(contributorid)!=null)
						{
							String caffiliation = (String)contributoraffiliationMap.get(contributorid);
							String[] caffiliationElements = caffiliation.split(Constants.IDDELIMITER,-1);
							file.write("<affiliation");
							if(caffiliationElements.length>0)
							{
								String affVenue = null;
								String affText = null;
								String affID = caffiliationElements[0];
								if(caffiliationElements.length>1)
								{
									affVenue = caffiliationElements[1];
								}
								
								if(caffiliationElements.length>2)
								{	
									affText = caffiliationElements[2];
								}

								if(caffiliationElements.length>3)
								{
									String affOrg = caffiliationElements[3];
									String[] affOrgs = affOrg.split(Constants.GROUPDELIMITER);
									
									if(affOrgs.length>0)
									{
										String address_part = null;
										String city_group = null;
										String country = null;
										String affOrganization = affOrgs[0];
										if(affOrgs.length>1)
										{
											address_part = affOrgs[1];
										}
										
										if(affOrgs.length>2)
										{
											city_group = affOrgs[2];
										}
										
										if(affOrgs.length>3)
										{
											country = affOrgs[3];
											file.write(" country=\""+country+"\">\n");
										}
										else
										{
											file.write(">");
										}
										
										if(affOrganization!=null && affOrganization.length()>0)
										{
											file.write("<organization>"+cleanBadCharacters(affOrganization)+"</organization>\n");
										}
										if(address_part!=null && address_part.length()>0)
										{
											file.write("<address-part>"+cleanBadCharacters(address_part)+"</address-part>\n");
										}
										if(city_group!=null && city_group.length()>0)
										{
											file.write("<city-group>"+cleanBadCharacters(city_group)+"</city-group>\n");
										}
										/*
										if(country!=null && country.length()>0)
										{
											file.write("<country>"+cleanBadCharacters(country)+"</country>\n");
										}	
										*/    								
									}
									else
									{
										file.write(">\n");	
										System.out.println("affOrgs has wrong format size="+affOrgs.length);
									}
									
									if(affText!=null && affText.length()>0)
									{
										file.write("<ce:text>"+cleanBadCharacters(affText)+"</ce:text>\n");
									}
									
									
								}
								else
								{
									file.write(">\n");
									
								}
								
								
								if(affID!=null && !affID.equals("0"))
								{
									file.write("<affiliation-id afid=\""+affID+"\"/>\n");
									//System.out.println("affiliation-id="+affID);
								}
								
								
							}
							else
							{
								//file.write("<affiliation-id/>\n");
								System.out.println("affiliationElements has wrong format size="+caffiliationElements.length);
							}
							file.write("</affiliation>\n");
						}
						
					}
				}
				file.write("</contributor-group>\n");
			}
		}
	}

	private void outputAuthorGroup(FileWriter file, String authorString,String affiliationString) throws Exception
	{
		Map affsMap = new LinkedHashMap();
    	Map authorGroupMap = new LinkedHashMap();
    	
    	
    	if(affiliationString!=null)
    	{
	    	String[] BdAffiliationsArray = affiliationString.split(Constants.AUDELIMITER, -1);
	    	for(int i=0;i<BdAffiliationsArray.length;i++)
	    	{
	    		String BdAffiliation = BdAffiliationsArray[i];	    		
	    		String[] BdAffiliationArray = BdAffiliation.split(Constants.IDDELIMITER, -1);	    		
	    		
	    		if(BdAffiliationArray.length>0)
	    		{
	    			String affid=BdAffiliationArray[0];
	    			
	    			affsMap.put(affid, BdAffiliation);
	    		}
	    		else
	    		{
	    			//System.out.println("record "+accessnumber+" aff wrong format "+BdAffiliationArray.length);
	    		}
	    		
	    	}
    	}
    	
    	BdAuthors aus = new BdAuthors(authorString);
    	List authors = aus.getAuthors();
    	
    	
    	for(int i=0;i<authors.size();i++)
    	{
    		BdAuthor author = (BdAuthor)authors.get(i);
    		String affidStr = author.getAffidString();
    		if(affidStr!=null)
    		{
    			String[] affids = affidStr.split(Constants.GROUPDELIMITER,-1);
    			for(int k=0;k<affids.length;k++)
    			{
    				add(authorGroupMap,affids[k],author);
    			}
    		}
    	}
    	   	
    	Iterator authorGroupkey = authorGroupMap.keySet().iterator();
    	
    	while(authorGroupkey.hasNext())
    	{
    		String authorGroupId = (String)authorGroupkey.next();
    		List authorGroup = (List)authorGroupMap.get(authorGroupId);   
    		String affidStr = null;
    		file.write("<author-group>\n");
    		for(int i=0;i<authorGroup.size();i++)
    		{
    			BdAuthor author = (BdAuthor)authorGroup.get(i);
	    		String authorID = author.getAuid();
	    		String seq = author.getSec();
	    		String surname = author.getSurname();
	    		String givenname = author.getGivenName();
	    		String indexname = author.getIndexedName();
	    		String initials = author.getInitials();
	    		String suffix = author.getSuffix();
	    		String eaddress = author.getEaddress();
	    		String degree = author.getDegrees();
	    		 	   affidStr = author.getAffidString();	    			    			    		
	    		
	    		file.write("<author");
	    		
	    		if(seq!=null)
	    		{
	    			file.write(" seq=\""+seq+"\"");
	    		}
	    		
	    		if(authorID!=null)
	    		{
	    			if(authorID.indexOf(",")>-1)
	    			{
	    				String[] auids = authorID.split(",");
	    				file.write(" orcid=\""+auids[0]+"\" auid=\""+auids[1]+"\"");   				
	    			}
	    			else
	    			{
	    				file.write(" orcid=\""+authorID+"\"");
	    			}
	    		}
	    		
	    		file.write(" type=\"auth\">\n");
	    		
	    		
	    		if(initials!=null)
	    		{
	    			file.write("<ce:initials>"+cleanBadCharacters(initials)+"</ce:initials>\n");
	    		}
	    		
	    		if(indexname!=null)
	    		{
	    			file.write("<ce:indexed-name>"+cleanBadCharacters(indexname)+"</ce:indexed-name>\n");
	    		}
	    		else
	    		{
	    			if(surname!=null && initials!=null)
	    			{
	    				file.write("<ce:indexed-name>"+cleanBadCharacters(surname)+" "+cleanBadCharacters(initials)+"</ce:indexed-name>\n");
	    			}
	    			else if(surname!=null && givenname!=null)
	    			{
	    				file.write("<ce:indexed-name>"+cleanBadCharacters(surname)+" "+cleanBadCharacters(givenname)+"</ce:indexed-name>\n");
	    			}
	    			else if(surname!=null)
	    			{
	    				file.write("<ce:indexed-name>"+cleanBadCharacters(surname)+"</ce:indexed-name>\n");
	    			}
	    			else if(givenname!=null)
	    			{
	    				file.write("<ce:indexed-name>"+cleanBadCharacters(givenname)+"</ce:indexed-name>\n");
	    			}
	    		}
	    		
	    		if(surname!=null)
	    		{
	    			file.write("<ce:surname>"+cleanBadCharacters(surname)+"</ce:surname>\n");
	    		}
	    		else if(givenname!=null)
	    		{
	    			file.write("<ce:surname>"+cleanBadCharacters(givenname)+"</ce:surname>\n");
	    		}

	    		if(surname!=null && givenname!=null)
	    		{
	    			file.write("<ce:given-name>"+cleanBadCharacters(givenname)+"</ce:given-name>\n");
	    		}
	    		
	    		if(eaddress!=null)
	    		{
	    			file.write("<ce:e-address>"+cleanBadCharacters(eaddress)+"</ce:e-address>\n");
	    		}
	    		
	    		if(degree!=null)
	    		{
	    			file.write("<ce:degrees>"+degree+"</ce:degrees>\n");
	    		}
	    		
	    		if(suffix!=null)
	    		{
	    			file.write("<ce:suffix>"+cleanBadCharacters(suffix)+"</ce:suffix>\n");
	    		}
	    		file.write("</author>\n");
    		}// for author
    		    		
    		String affiliation = (String)affsMap.get(authorGroupId);
    		if(affiliation!=null)
    		{
    			
    			String[] BdAffiliationArray = affiliation.split(Constants.IDDELIMITER, -1);
	    		
	    		if(BdAffiliationArray.length>0)
	    		{
	    			String affDeptId=null;
	    			String affiliationId=null;
	    			String affText=null;
	    			String affPostalCode=null;
	    			String affState= null;
	    			String affCity= null;
	    			String affAddressPart=null;
	    			String affCountry=null;
	    			String affOrganization=null;
	    			String affCityGroup=null;
	    			String affid=BdAffiliationArray[0];
	    			if(BdAffiliationArray.length>1)
		    		{
	    				affOrganization=BdAffiliationArray[1];
		    		}
	    			if(BdAffiliationArray.length>2)
		    		{
	    				affCityGroup=BdAffiliationArray[2];
		    		}
	    			if(BdAffiliationArray.length>3)
		    		{
	    				affCountry=BdAffiliationArray[3];
		    		}
	    			if(BdAffiliationArray.length>4)
		    		{
	    				affAddressPart=BdAffiliationArray[4];
		    		}
	    			if(BdAffiliationArray.length>5)
		    		{
	    				affCity=BdAffiliationArray[5];
		    		}
	    			if(BdAffiliationArray.length>6)
		    		{
	    				affState=BdAffiliationArray[6];
		    		}
	    			if(BdAffiliationArray.length>7)
		    		{
	    				affPostalCode=BdAffiliationArray[7];
		    		}
	    			if(BdAffiliationArray.length>8)
		    		{
	    				affText=BdAffiliationArray[8];
		    		}
	    			if(BdAffiliationArray.length>9)
		    		{
	    				affiliationId=BdAffiliationArray[9];
		    		}
	    			if(BdAffiliationArray.length>10)
		    		{
	    				affDeptId=BdAffiliationArray[10];
		    		}
    		
	    		file.write("<affiliation");
	    		if(affCountry!=null && affCountry.length()>0)
	    		{
	    			file.write(" country=\""+cleanBadCharacters(affCountry)+"\"");
	    		}
	    		
	    		if(affiliationId!=null && affiliationId.length()>0)
	    		{
	    			file.write(" afid=\""+affiliationId+"\"");
	    		}
	    		
	    		if(affDeptId!=null && affDeptId.length()>0)
	    		{
	    			file.write(" dptid=\""+affDeptId+"\"");
	    		}
	    		
	    		file.write(">\n");	    		
	    		
	    		if(affText!=null && affText.length()>0)
	    		{
	    			file.write("<ce:text>"+cleanBadCharacters(affText)+"</ce:text>\n");
	    		}
	    		else
	    		{
	    			if(affOrganization!=null && affOrganization.length()>0)
	    			{
	    				file.write("<organization>"+cleanBadCharacters(affOrganization)+"</organization>\n");
	    			}
	    			
	    			if(affAddressPart!=null && affAddressPart.length()>0)
	    			{
	    				file.write("<address-part>"+cleanBadCharacters(affAddressPart)+"</address-part>\n");
	    			}
	    			
	    			if(affCityGroup!=null && affCityGroup.length()>0)
	    			{
	    				file.write("<city-group>"+cleanBadCharacters(affCityGroup)+"</city-group>\n"); 
	    				/*
	    				String[] cityGroupArr = cleanBadCharacters(affCityGroup).split(";");
	    				if(cityGroupArr.length==1)
	    				{
	    					file.write("<city>"+cityGroupArr[0]+"</city>\n");
	    				}
	    				else if(cityGroupArr.length==2)
	    				{
	    					file.write("<city>"+cityGroupArr[0]+"</city>\n");
	    					file.write("<postal-code>"+cityGroupArr[1].trim()+"</postal-code>\n");
	    				}
	    				else
	    				{
	    					System.out.println("affiliation city-group has wrong format");
	    				}
	    				*/
	    			}
	    			else
	    			{
	    				if(affCity!=null && affCity.length()>0)
	    				{
	    					file.write("<city>"+cleanBadCharacters(affCity)+"</city>\n");
	    				}
	    				
	    				if(affState!=null && affState.length()>0)
	    				{
	    					file.write("<state>"+cleanBadCharacters(affState)+"</state>\n");
	    				}
	    				
	    				if(affPostalCode!=null && affPostalCode.length()>0)
	    				{
	    					file.write("<postal-code>"+cleanBadCharacters(affPostalCode).trim()+"</postal-code>\n");
	    				}
	    				
	    			}//if affCityGroup
	    			
	    			if((affiliationId!=null && affiliationId.length()>0 && !affiliationId.equals("0")) || (affDeptId!=null && affDeptId.length()>0 && !affDeptId.equals("0")))
					{						
						file.write("<affiliation-id ");
						if(affiliationId!=null && affiliationId.length()>0)
						{
							file.write("afid=\""+affiliationId+"\"");
						}
						if(affDeptId!=null && affDeptId.length()>0)
						{
							file.write(" dptid=\""+affDeptId+"\"");
						}						
						file.write(" />");
					}
	    			
	    			
					
	    			
	    		}//if affText
	    		file.write("</affiliation>\n");
    		}//if affiliation
    	}    		
    	file.write("</author-group>\n");
    }//while
}

	private void outputCorrespondence(FileWriter file, String correspondencename,String correspondenceaffiliation,String correspondenceeaddress,String accessnumber) throws Exception
	{
		//System.out.println("correspondencename = "+correspondencename);
		//System.out.println("correspondenceaffiliation = "+correspondenceaffiliation);
		//System.out.println("correspondenceeaddress = "+correspondenceeaddress);
		String[] correspondencenames=null;
		String[] correspondenceAffArr=null;
		String[] correspondenceeaddressArr=null;
		
		if(correspondenceaffiliation!=null)
		{
			correspondenceAffArr=correspondenceaffiliation.split(Constants.AUDELIMITER,-1);
		}
		
		if(correspondenceeaddress!=null)
		{
			correspondenceeaddressArr=correspondenceeaddress.split(Constants.AUDELIMITER,-1);
		}
		
		if(correspondencename!=null && correspondencename.length()>0)
		{
			
			correspondencenames = correspondencename.split(Constants.AUDELIMITER,-1);
			//System.out.println("correspondencename size = "+correspondencenames.length);
			for(int i=0;i<correspondencenames.length;i++)
			{
				file.write("<correspondence>\n");
				String cperson = correspondencenames[i];
				String[] cnames = cperson.split(Constants.IDDELIMITER,-1);
				if(cnames.length>0)
				{
					String given_name = null;
					String suffix = null;
					String nametext = null;
					String text = null;
					String initials = null;
					String indexed_name = null;
					String degrees = null;
					String surname = null;
					String id = cnames[0];
					if(cnames.length>1)
					{
						initials = cnames[1];
					}
					if(cnames.length>2)
					{
						indexed_name = cnames[2];
					}
					if(cnames.length>3)
					{
						degrees = cnames[3];
					}
					if(cnames.length>4)
					{
						surname = cnames[4];
					}
					if(cnames.length>5)
					{
						given_name = cnames[5];
					}
					if(cnames.length>6)
					{
						suffix = cnames[6];
					}
					if(cnames.length>7)
					{
						nametext = cnames[7];
					}
					if(cnames.length>8)
					{
						text = cnames[8];
					}
					if(indexed_name!=null && indexed_name.length()>0)
					{
						file.write("<person>\n");
						if(initials!=null && initials.length()>0)
						{
							file.write("<ce:initials>"+cleanBadCharacters(initials)+"</ce:initials>\n");
						}
						
						if(indexed_name!=null && indexed_name.length()>0)
						{
							file.write("<ce:indexed-name>"+cleanBadCharacters(indexed_name)+"</ce:indexed-name>\n");
						}
						else
			    		{
			    			if(surname!=null && initials!=null)
			    			{
			    				file.write("<ce:indexed-name>"+cleanBadCharacters(surname)+" "+cleanBadCharacters(initials)+"</ce:indexed-name>\n");
			    			}
			    			else if(surname!=null && given_name!=null)
			    			{
			    				file.write("<ce:indexed-name>"+cleanBadCharacters(surname)+" "+cleanBadCharacters(given_name)+"</ce:indexed-name>\n");
			    			}
			    			else if(surname!=null)
			    			{
			    				file.write("<ce:indexed-name>"+cleanBadCharacters(surname)+"</ce:indexed-name>\n");
			    			}
			    			else if(given_name!=null)
			    			{
			    				file.write("<ce:indexed-name>"+cleanBadCharacters(given_name)+"</ce:indexed-name>\n");
			    			}
			    		}
						
						if(degrees!=null && degrees.length()>0)
						{
							file.write("<ce:degrees>"+degrees+"</ce:degrees>\n");
						}
						
						if(surname!=null && surname.length()>0)
						{
							file.write("<ce:surname>"+cleanBadCharacters(surname)+"</ce:surname>\n");
						}
						
						if(given_name!=null && given_name.length()>0)
						{
							file.write("<ce:given-name>"+cleanBadCharacters(given_name)+"</ce:given-name>\n");
						}
						
						if(suffix!=null && suffix.length()>0)
						{
							file.write("<ce:suffix>"+cleanBadCharacters(suffix)+"</ce:suffix>\n");
						}
						
						if(nametext!=null && nametext.length()>0)
						{
							file.write("<ce:nametext>"+cleanBadCharacters(nametext)+"</ce:nametext>\n");
						}
						
						if(text!=null && text.length()>0)
						{
							file.write("<ce:text>"+cleanBadCharacters(text)+"</ce:text>\n");
						}
						
						file.write("</person>\n");
					}
					
					if(correspondenceAffArr!=null && correspondenceAffArr.length>i)
					{
						//System.out.println("correspondenceAffArr size = "+correspondenceAffArr.length);
						String[] affs = correspondenceAffArr[i].split(Constants.IDDELIMITER,-1);
						
						//System.out.println("correspondenceaffiliation size = "+affs.length);
						if(affs.length>0)
						{
							
							
							String afid = affs[0];
							System.out.println("afid="+affs[0]);
							String venue = null;
							String content =null;
							String dptid = null;
							if(affs.length>1)
							{
								venue = affs[1];
							}
							//System.out.println("venue="+affs[1]);
							String country = null;
							if(affs.length>2)
							{
								content = affs[2];	
								System.out.println("content="+affs[2]);
								if(content.indexOf(Constants.GROUPDELIMITER)>-1)
								{
									String[] organizations = content.split(Constants.GROUPDELIMITER);																					
									if(organizations.length>3)
									{
										country = organizations[3];
									}
								}
							}
							
							if(affs.length>3)
							{
								dptid = affs[3];
								System.out.println("dptid="+affs[3]);
							}
							
							if((afid!=null && afid.length()>0 ) || (dptid!=null && dptid.length()>0 ) || 
									(country!=null && country.length()>0 ) || (content!=null))
							{
								file.write("<affiliation");
							
								if(afid!=null && afid.length()>0 && !afid.equals("0"))
								{
									file.write(" afid=\""+afid+"\"");
								}
								
								if(dptid!=null && dptid.length()>0 && !dptid.equals("0"))
								{
									file.write(" dptid=\""+dptid+"\"");
								}
								
								if(country!=null && country.length()>0 )
								{
									file.write(" country=\""+country+"\"");
								}								
								file.write(">\n");
							
							
							
								if(content!=null)
								{
									if(content.indexOf(Constants.GROUPDELIMITER)>-1)
									{
										String[] organizations = content.split(Constants.GROUPDELIMITER);
										
										String organization = null;
										String address_part = null;
										String city_group = null;
										
										if(organizations.length>0)
										{
											organization = organizations[0];
											System.out.println("organization="+organizations[0]);
										}
										if(organizations.length>1)
										{
											address_part = organizations[1];
											System.out.println("address_part="+organizations[1]);
										}
										if(organizations.length>2)
										{
											city_group = organizations[2];
											System.out.println("city_group="+organizations[2]);
										}
										if(organizations.length>3)
										{
											country = organizations[3];
											System.out.println("country="+organizations[3]);
										}
										
										if(organization!=null && organization.length()>0)
										{
											file.write("<organization>"+cleanBadCharacters(organization)+"</organization>\n");
										}
										
										if(address_part!=null && address_part.length()>0)
										{
											file.write("<address-part>"+cleanBadCharacters(address_part)+"</address-part>\n");
										}
										
										if(city_group!=null && city_group.length()>0)
										{
											file.write("<city-group>"+cleanBadCharacters(city_group)+"</city-group>\n");
											/*
											if(city_group.indexOf(",")>-1) 
											{
												String[] cityArr=city_group.split(",");
												if(cityArr.length==1)
												{
													file.write("<city>"+cityArr[0]+"</city>\n");
												}
												else if(cityArr.length==2)
												{
													file.write("<city>"+cityArr[0].trim()+"</city>\n");
													file.write("<postal-code>"+cityArr[1].trim()+"</postal-code>\n");
												}
												else
												{
													System.out.println("correspondence city-group has wrong format");
												}
											}
											else
											{
												file.write("<city-group>"+cleanBadCharacters(city_group)+"</city-group>\n");
											}
											*/
										}
				   				
									}
									else
									{
										file.write("<ce:text>"+cleanBadCharacters(content)+"</ce:text>\n");
									}//content
									
									if(afid!=null && afid.length()>0 && !afid.equals("0"))
									{
										//System.out.println("affiliation-id="+afid);
										file.write("<affiliation-id>"+afid+"</affiliation-id>\n");
									}
									file.write("</affiliation>\n");		
								}
							}
							//else
							//{
							//	System.out.println("record "+accessnumber+" has no correspondence affiliation");
								
							//}//content			
								
						}
						else
						{
							System.out.println("2 correspondence affiliation format is wrong ");
							System.out.println("else affs[0]="+affs[0]);
							System.out.println("else affs[1]="+affs[1]);
							System.out.println("else affs[2]="+affs[2]);
						}//content				
				}
					
				if(correspondenceeaddressArr!=null && correspondenceeaddressArr.length>i)
				{
					String[] cEaddress = correspondenceeaddressArr[i].split(Constants.IDDELIMITER);
					if(cEaddress.length==2)
					{
						//file.write("<ce:e-address type=\""+cleanBadCharacters(cEaddress[0])+"\">"+cEaddress[1]+"</ce:e-address>\n");
						file.write("<ce:e-address>"+cEaddress[1]+"</ce:e-address>\n");
					}
					else
					{
						//System.out.println("record "+accessnumber+" correspondenceeaddress has wrong format");
					}
				}
				file.write("</correspondence>\n");
				
			}
			else
			{
				System.out.println("correspondencename has wrong format, size is "+cnames.length);
			}//cnames
		}
		    			
	}//correspondencename
		
		
			
			
		
		
	}
	
	private void outputGrantlist(FileWriter file, String grantlist) throws Exception
	{
		file.write("<grantlist>\n"); //our database table didn't capture the attribute "complete", so we ignore it here.
		String[] grants = grantlist.split(Constants.AUDELIMITER,-1);
		for(int i=0;i<grants.length;i++)
		{
			String grant = grants[i];
			String[] grantElements = grant.split(Constants.IDDELIMITER,-1);
			if(grantElements.length==3){
				file.write("<grant>\n");
				String grantId = grantElements[0];
				String grantAcronym = grantElements[1];
				String grantAgency = grantElements[2];
				if(grantId!=null && grantId.length()>0){
					file.write("<grant-id>"+cleanBadCharacters(grantId)+"</grant-id>\n");
				}
				
				if(grantAcronym!=null && grantAcronym.length()>0){
					file.write("<grant-acronym>"+cleanBadCharacters(grantAcronym)+"</grant-acronym>\n");
				}
				
				if(grantAgency!=null && grantAgency.length()>0){
					file.write("<grant-agency>"+cleanBadCharacters(grantAgency)+"</grant-agency>\n");
				}
				file.write("</grant>\n");
				
			}
			else
			{
				//System.out.println("record "+accessnumber+"'s grantlist has a wrong format");
			}
		}
		file.write("</grantlist>\n");
	}

	private void outputAbstract(FileWriter file, String abstractdata, String abstract_original, String abstract_perspective) throws Exception
	{
		file.write("<abstracts>\n");
		file.write("<abstract ");
		if(abstract_original!=null && abstract_original.length()>0 && abstract_perspective!=null && abstract_perspective.length()>0)
		{
			file.write(" original=\""+abstract_original+"\" perspective=\""+abstract_perspective+"\">\n");
		}
		else if(abstract_original!=null && abstract_original.length()>0)
		{
			file.write(" original=\""+abstract_original+"\">\n");
		}
		else if(abstract_perspective!=null && abstract_perspective.length()>0)
		{
			file.write(" perspective=\""+abstract_perspective+"\">\n");
		}
		else
		{
			file.write(">\n");
		}
		if(abstractdata.indexOf("&copy;")>-1)
		{
			String abstractcopyright = abstractdata.substring(abstractdata.indexOf("&copy;"));
			file.write("<publishercopyright>"+abstractcopyright+"</publishercopyright>\n");
			abstractdata = abstractdata.substring(0,abstractdata.indexOf("&copy;")-1);
			//System.out.println("copyright="+abstractcopyright);
			//System.out.println("abstract="+abstractdata);
		}
		file.write("<ce:para>"+cleanBadCharacters(abstractdata)+"</ce:para>\n");
		file.write("</abstract>\n");
		file.write("</abstracts>\n");
	}

    private void outputISBN(FileWriter file, String isbnString) throws Exception
    {
    	String[] isbns = isbnString.split(Constants.AUDELIMITER,-1);
    	
		for(int i=0;i<isbns.length;i++)
		{
			String isbn = isbns[i];
			if(isbn!=null && isbn.length()>0)
			{
				String[] isbnElements = isbn.split(Constants.IDDELIMITER,-1);
				if(isbnElements.length>0 && isbnElements.length==4 )
				{
					String isbnType = isbnElements[0];
					String isbnLength = isbnElements[1];
					String isbnLevel = isbnElements[2];
					String isbnValue = isbnElements[3];
					file.write("<isbn");
					if(isbnType!=null && isbnType.length()>0)
					{
						file.write(" type=\""+isbnType+"\"");
					}
					
					if(isbnLevel!=null && isbnLevel.length()>0)
					{
						file.write(" level=\""+isbnLevel+"\"");
					}
					
					if(isbnLength!=null && isbnLength.length()>0)
					{
						file.write(" length=\""+isbnLength+"\"");
					}
					file.write(">"+isbnValue+"</isbn>\n");												
				}
				else if(isbnElements.length>0 && isbnElements.length==2 )
				{
					String isbnType = isbnElements[0];					
					String isbnValue = isbnElements[1];
					file.write("<isbn");
					if(isbnType!=null && isbnType.length()>0)
					{
						file.write(" type=\""+isbnType+"\"");
					}										
					file.write(">"+isbnValue+"</isbn>\n");												
				}
				else
				{
					System.out.println("invalid isbn length for record="+this.accessnumber);
				}
			}
		}
    }
    
    private void outputYear(FileWriter file, String publicationyear) throws Exception
    {
    	file.write("<publicationyear");
		String first = null;
		String last = null;
		if(publicationyear.indexOf("-")>0)
		{
			String[] yearArray = publicationyear.split("-");
			first = yearArray[0];
			last  = yearArray[1];
		}
		else
		{
			first = publicationyear;
		}
		file.write(" first=\""+first+"\"");
		if(last != null && last.length()>0)
		{
			file.write(" last=\""+last+"\"");
		}
		file.write(" />\n");
    }
    
    private void outputEditors(FileWriter file, String editors) throws Exception
    {
    	String[] editorArray = editors.split(Constants.AUDELIMITER,-1);
		file.write("<editors>\n");
		for(int i=0;i<editorArray.length;i++)
		{
			file.write("<editor>\n");
			String editor = editorArray[i];
			String[] editorElements = editor.split(Constants.IDDELIMITER,-1);
			if(editorElements.length>0)
			{
				String given_name = null;
				String suffix = null;
				String nametext = null;
				String text = null;
				String initials = null;
				String indexed_name = null;
				String degrees = null;
				String surname = null;
				String id = editorElements[0];
				if(editorElements.length>1)
				{
					initials = editorElements[1];
				}
				if(editorElements.length>2)
				{
					indexed_name = editorElements[2];
				}
				if(editorElements.length>3)
				{
					degrees = editorElements[3];
				}
				if(editorElements.length>4)
				{
					surname = editorElements[4];
				}
				
				if(editorElements.length>5)
				{
					given_name = editorElements[5];
				}
				if(editorElements.length>6)
				{
					suffix = editorElements[6];
				}
				if(editorElements.length>7)
				{
					nametext = editorElements[7];
				}
				if(editorElements.length>8)
				{
					text = editorElements[8];
				}
				//file.write("<person>\n");
				if(initials!=null && initials.length()>0)
				{
					file.write("<ce:initials>"+cleanBadCharacters(initials)+"</ce:initials>\n");
				}
				
				if(indexed_name!=null && indexed_name.length()>0)
				{
					file.write("<ce:indexed-name>"+cleanBadCharacters(indexed_name)+"</ce:indexed-name>\n");
				}
				else
	    		{
	    			if(surname!=null && initials!=null)
	    			{
	    				file.write("<ce:indexed-name>"+cleanBadCharacters(surname)+" "+cleanBadCharacters(initials)+"</ce:indexed-name>\n");
	    			}
	    			else if(surname!=null && given_name!=null)
	    			{
	    				file.write("<ce:indexed-name>"+cleanBadCharacters(surname)+" "+cleanBadCharacters(given_name)+"</ce:indexed-name>\n");
	    			}
	    			else if(surname!=null)
	    			{
	    				file.write("<ce:indexed-name>"+cleanBadCharacters(surname)+"</ce:indexed-name>\n");
	    			}
	    			else if(given_name!=null)
	    			{
	    				file.write("<ce:indexed-name>"+cleanBadCharacters(given_name)+"</ce:indexed-name>\n");
	    			}
	    			else
	    			{
	    				file.write("<ce:indexed-name/>");
	    			}
	    		}
				
				if(degrees!=null && degrees.length()>0)
				{
					file.write("<ce:degrees>"+degrees+"</ce:degrees>\n");
				}
				
				if(surname!=null && surname.length()>0)
				{
					file.write("<ce:surname>"+cleanBadCharacters(surname)+"</ce:surname>\n");
				}
				
				if(given_name!=null && given_name.length()>0)
				{
					file.write("<ce:given-name>"+cleanBadCharacters(given_name)+"</ce:given-name>\n");
				}
				
				if(suffix!=null && suffix.length()>0)
				{
					file.write("<ce:suffix>"+cleanBadCharacters(suffix)+"</ce:suffix>\n");
				}
				
				if(nametext!=null && nametext.length()>0)
				{
					file.write("<ce:nametext>"+cleanBadCharacters(nametext)+"</ce:nametext>\n");
				}
				
				if(text!=null && text.length()>0)
				{
					file.write("<ce:text>"+cleanBadCharacters(text)+"</ce:text>\n");
				}
				
				//file.write("</person>\n");
				
			}
			else
			{
				System.out.println("editors has wrong format, size is "+editorElements.length);
			}//editorElements
			file.write("</editor>\n");
		}
		file.write("</editors>\n");
    }
    
    private void outputPublisher(FileWriter file, String publishername,String publisheraddress,String publisherelectronicaddress) throws Exception
    {
    	String[] publishernameArray = null;
		String[] publisheraddressArray = null;
		String[] publisherelectronicaddressArray = null;
		if(publishername!=null && publishername.length()>0)
		{
			publishernameArray = publishername.split(Constants.AUDELIMITER,-1);
		}
		
		if(publisheraddress!=null && publisheraddress.length()>0)
		{
			publisheraddressArray = publisheraddress.split(Constants.AUDELIMITER,-1);
		}
		
		if(publisherelectronicaddress!=null && publisherelectronicaddress.length()>0)
		{
			publisherelectronicaddressArray = publisherelectronicaddress.split(Constants.AUDELIMITER,-1);
		}
		
		if(publishernameArray!=null && publishernameArray.length>0)
		{
			for(int i=0;i<publishernameArray.length;i++)
			{
				file.write("<publisher>\n");
				file.write("<publishername>"+cleanBadCharacters(publishernameArray[i])+"</publishername>\n");
				if(publisheraddressArray!=null && publisheraddressArray.length>i)
				{
					//file.write("<publisheraddress>"+publisheraddressArray[i]+"</publisheraddress>\n");
					String paddress = publisheraddressArray[i];
					if(paddress!=null && paddress.length()>0)
					{	
						if(paddress.indexOf(Constants.IDDELIMITER)<0)
						{
							file.write("<publisheraddress>"+cleanBadCharacters(paddress)+"</publisheraddress>\n");
						}
						else
						{
							String[] publisheraddressElements = paddress.split(Constants.IDDELIMITER);
							file.write("<affiliation");
							if(publisheraddressElements.length>2)
							{
								String affaddress_part = null;
								String affcity_group = null;
								String affcountry = null;
								String afforganization = null;
								String afftext = null;
								String venue = null;
								String afid = publisheraddressElements[0];
								if(publisheraddressElements.length>3)
								{
									venue = publisheraddressElements[1];
								}
								if(publisheraddressElements.length>3)
								{
									afftext = publisheraddressElements[2];
								}
								
								if(publisheraddressElements.length>3)
								{
									afforganization = publisheraddressElements[3];
								}
								if(publisheraddressElements.length>4)
								{
									affaddress_part = publisheraddressElements[4];
								}
								if(publisheraddressElements.length>5)
								{
									affcity_group = publisheraddressElements[5];
								}
								if(publisheraddressElements.length>6)
								{
									affcountry = publisheraddressElements[6];
								}
								if(affcountry!=null && affcountry.length()>0)
								{
									file.write(" country=\""+cleanBadCharacters(affcountry)+"\">\n");
								}
								else
								{
									file.write(">");
								}
								
								if(venue!=null && venue.length()>0)
								{
									file.write("<venue>"+cleanBadCharacters(venue)+"</venue>\n");
								}
								if(affaddress_part!=null && affaddress_part.length()>0)
								{
									file.write("<address-part>"+cleanBadCharacters(affaddress_part)+"</address-part>\n");
								}
								if(affcity_group!=null && affcity_group.length()>0)
								{
									if(affcity_group.indexOf(",")>-1)
									{
										String[] affCitys = affcity_group.split(",");
										if(affCitys.length>1)
										{
											file.write("<city>"+cleanBadCharacters(affCitys[0])+"</city>\n");
											file.write("<postal-code>"+cleanBadCharacters(affCitys[1])+"</postal-code>\n");
										}
									}
									else
									{
										file.write("<city-group>"+cleanBadCharacters(affcity_group)+"</city-group>\n");
									}
								}//if								
								file.write("</affiliation>\n");	
							}															
							else
							{
								System.out.println("format is wrong publisheraddressElements size"+publisheraddressElements.length);
							}//else
						}//else
					}//if
					
				}//if
				
				if(publisherelectronicaddressArray!=null && publisherelectronicaddressArray.length>i)
				{
					String[] pEaddress = publisherelectronicaddressArray[i].split(Constants.IDDELIMITER);
					if(pEaddress.length==2)
					{
						file.write("<ce:e-address");
						if(pEaddress[0]!=null && pEaddress[0].length()>0)
						{
							file.write(" type=\""+pEaddress[0]+"\"");
						}
						file.write(">"+cleanBadCharacters(pEaddress[1])+"</ce:e-address>\n");
						//System.out.println("PUBLISHER0="+pEaddress[0]);
						//System.out.println("PUBLISHER1="+pEaddress[1]);
					}
					else
					{
						System.out.println("PUBLISHER="+publisherelectronicaddressArray[i]+" size="+pEaddress.length);
					}
					
				}//if
				file.write("</publisher>\n");
			}//for
			
		}
		else if(publisheraddressArray!=null && publisheraddressArray.length>0)
		{
			for(int i=0;i<publisheraddressArray.length;i++)
			{
				file.write("<publisher>\n");
				String paddress = publisheraddressArray[i];
				if(paddress!=null && paddress.length()>0)
				{	
					if(paddress.indexOf(Constants.IDDELIMITER)<0)
					{
						file.write("<publisheraddress>"+cleanBadCharacters(paddress)+"</publisheraddress>\n");
					}
					else
					{
						String[] publisheraddressElements = paddress.split(Constants.IDDELIMITER);
						file.write("<affiliation");
						if(publisheraddressElements.length>2)
						{
							String affaddress_part = null;
							String affcity_group = null;
							String affcountry = null;
							String afforganization = null;
							String afftext = null;
							String venue = null;
							String afid = publisheraddressElements[0];
							if(publisheraddressElements.length>3)
							{
								venue = publisheraddressElements[1];
							}
							if(publisheraddressElements.length>3)
							{
								afftext = publisheraddressElements[2];
							}
							
							if(publisheraddressElements.length>3)
							{
								afforganization = publisheraddressElements[3];
							}
							if(publisheraddressElements.length>4)
							{
								affaddress_part = publisheraddressElements[4];
							}
							if(publisheraddressElements.length>5)
							{
								affcity_group = publisheraddressElements[5];
							}
							if(publisheraddressElements.length>6)
							{
								affcountry = publisheraddressElements[6];
							}
							if(affcountry!=null && affcountry.length()>0)
							{
								file.write(" country=\""+cleanBadCharacters(affcountry)+"\">\n");
							}
							else
							{
								file.write(">");
							}
							
							if(venue!=null && venue.length()>0)
							{
								file.write("<venue>"+cleanBadCharacters(venue)+"</venue>\n");
							}
							if(affaddress_part!=null && affaddress_part.length()>0)
							{
								file.write("<address-part>"+cleanBadCharacters(affaddress_part)+"</address-part>\n");
							}
							if(affcity_group!=null && affcity_group.length()>0)
							{
								if(affcity_group.indexOf(",")>-1)
								{
									String[] affCitys = affcity_group.split(",");
									if(affCitys.length>1)
									{
										file.write("<city>"+cleanBadCharacters(affCitys[0])+"</city>\n");
										file.write("<postal-code>"+cleanBadCharacters(affCitys[1])+"</postal-code>\n");
									}
								}
								else
								{
									file.write("<city-group>"+cleanBadCharacters(affcity_group)+"</city-group>\n");
								}
							}
							
							file.write("</affiliation>\n");	
						}//else
									    							
						if(publisherelectronicaddressArray!=null && publisherelectronicaddressArray.length>i)
						{
							String[] pEaddress = publisherelectronicaddressArray[i].split(Constants.IDDELIMITER);
							String emailAddress = null;
							if(pEaddress.length>1)
							{
								emailAddress=pEaddress[1];
							}
							else
							{
								emailAddress=pEaddress[0];
							}
							file.write("<ce:e-address>"+cleanBadCharacters(emailAddress)+"</ce:e-address>\n");
						}
						file.write("</publisher>\n");	
					}//for
			}
			else if(publisherelectronicaddressArray!=null && publisherelectronicaddressArray.length>0)
			{
				for(int j=0;j<publisherelectronicaddressArray.length;j++)
				{
					file.write("<publisher>\n");
					String[] pEaddress = publisherelectronicaddressArray[j].split(Constants.IDDELIMITER);
					String emailAddress = null;
					if(pEaddress.length>1)
					{
						emailAddress=pEaddress[1];
					}
					else
					{
						emailAddress=pEaddress[0];
					}
					file.write("<ce:e-address>"+cleanBadCharacters(emailAddress)+"</ce:e-address>\n");
					
					file.write("</publisher>\n");
				}
			}
		}}
    }
    
    private void outputConflocation(FileWriter file, String conflocation) throws Exception
    {
    	String[] conflocationArray = conflocation.split(Constants.AUDELIMITER,-1);
		for(int i=0;i<conflocationArray.length;i++)
		{
			String cflocation = conflocationArray[i];
			if(cflocation!=null && cflocation.length()>0)
			{
				String[] cflocationElements = cflocation.split(Constants.IDDELIMITER,-1);
				
				if(cflocationElements.length>0)
				{
					file.write("<conflocation");
					String affcountry = null;
					String affcity_group = null;
					String affaddress_part = null;
					String venue = null;
					String afftext = null;
					String afforganization = null;
					String afid = cflocationElements[0];
					if(cflocationElements.length>1)
					{
						venue = cflocationElements[1];
					}
					if(cflocationElements.length>2)
					{
						afftext = cflocationElements[2];
					}
					if(cflocationElements.length>3)
					{
						afforganization = cflocationElements[3];
					}
					if(cflocationElements.length>4)
					{
						affaddress_part = cflocationElements[4];
					}
					if(cflocationElements.length>5)
					{
						affcity_group = cflocationElements[5];
					}
					
					if(cflocationElements.length>6)
					{
						affcountry = cflocationElements[6];
					}
					
					if(affcountry!=null && affcountry.length()>0)
					{
						file.write(" country=\""+cleanBadCharacters(affcountry)+"\">\n");
					}
					else
					{
						file.write(">");
					}
					
					if(venue!=null && venue.length()>0)
					{
						file.write("<venue>"+cleanBadCharacters(venue)+"</venue>\n");
					}
					if(affaddress_part!=null && affaddress_part.length()>0)
					{
						file.write("<address-part>"+cleanBadCharacters(affaddress_part)+"</address-part>\n");
					}
					if(affcity_group!=null && affcity_group.length()>0)
					{
						if(affcity_group.indexOf(",")>-1)
						{
							String[] affCitys = affcity_group.split(",");
							if(affCitys.length>1)
							{
								file.write("<city>"+cleanBadCharacters(affCitys[0])+"</city>\n");
								file.write("<postal-code>"+cleanBadCharacters(affCitys[1])+"</postal-code>\n");
							}
						}
						else
						{
							//file.write("<city-group>"+cleanBadCharacters(affcity_group)+"</city-group>\n");
							file.write("<city>"+cleanBadCharacters(affcity_group)+"</city>\n");
						}
					}					
					file.write("</conflocation>\n");	    												
				}
				else
				{									
					System.out.println("afid ="+ cflocationElements[0]);
					System.out.println("venue ="+ cflocationElements[1]);
					//System.out.println("afftext ="+ cflocationElements[2]);
					//System.out.println("afforganization ="+ cflocationElements[3]);									
					System.out.println("format is wrong cflocationElements size"+cflocationElements.length);
				}
			}
		}//for
    }
   
    
    
    private void outputVolisspag(FileWriter file, String volume,String issue,String page, String pagecount, String accessnumber) throws Exception
    {
    	file.write("<volisspag>\n");
		if((volume!=null && volume.length()>0) || (issue!=null && issue.length()>0))
		{	    			
			file.write("<voliss");
    		if(volume!=null && volume.length()>0 )
    		{
    			file.write(" volume=\""+dictionary.AlphaEntitysToNumericEntitys(volume)+"\"");
    		}
    		
    		if(issue!=null && issue.length()>0)
    		{
    			file.write(" issue=\""+dictionary.AlphaEntitysToNumericEntitys(issue)+"\"");
    		}
    		file.write("/>\n");    	          	 
		}
		
		if(page!=null && page.length()>0)
		{
			String[] pageArray = page.split(Constants.AUDELIMITER,-1);
		    if(pageArray.length==3)
		    {
		    	String pages = pageArray[0];
		    	String firstPage = pageArray[1];
		    	String lastPage = pageArray[2];
		    	if(pages!=null && pages.length()>0)
		    	{
		    		file.write("<pages>"+cleanBadCharacters(pages)+"</pages>\n");
		    	}
		    	else if((firstPage!=null && firstPage.length()>0) || (lastPage!=null && lastPage.length()>0))
		    	{
		    		file.write("<pagerange");	    		    			    		    	
		    		if(firstPage!=null && firstPage.length()>0)
		    		{
		    			file.write(" first=\""+firstPage+"\"");
		    		}
		    		
		    		if(lastPage!=null && lastPage.length()>0)
	    		    {
	    		    	file.write(" last=\""+lastPage+"\""); 
	    		    }
		    		file.write(" />\n");	
		    	}
		    		
		    }//if
		   	  
		}//if
		
		if(pagecount!=null && pagecount.length()>0)
		{
			String[] pagecounts = pagecount.split(Constants.AUDELIMITER,-1);
			for(int i=0;i<pagecounts.length;i++)
			{
				String pagecountElement = pagecounts[i];
				//System.out.println("pagecounts"+pagecounts.length);
				if(pagecountElement!=null && pagecountElement.length()>0)
				{	
					String[] pCount = pagecountElement.split(Constants.IDDELIMITER);
					if(pCount.length>1)
					{
						file.write("<pagecount>"+cleanBadCharacters(pCount[1])+"</pagecount>\n");						
					}
					else
					{
						file.write("<pagecount>"+cleanBadCharacters(pCount[0])+"</pagecount>\n");	
					}										
				}//if
			}//for
		}
		file.write("</volisspag>\n");
    }
    
    private void outputConfeditors(FileWriter file, String conferenceeditoraddress,String conferenceorganization,String confenceeditor) throws Exception
    {   	
		if(confenceeditor!=null && confenceeditor.length()>0)
		{
			file.write("<confeditors>\n");
			file.write("<editors>\n");
			String[] confeditorArray = confenceeditor.split(Constants.AUDELIMITER,-1);
			for(int i=0;i<confeditorArray.length;i++)
			{
				String confeditor = confeditorArray[i];
				if(confeditor!=null && confeditor.length()>0)
				{
					file.write("<editor>\n");
					String[] confeditorElement = confeditor.split(Constants.IDDELIMITER,-1);
					//System.out.println("confeditorElement SIZE="+confeditorElement.length);
					String editorID = null;
					String initials = null;
					String indexed_name = null;
					String degrees = null;
					String surname = null;
					String given_name = null;
					String suffix = null;
					String nametext = null;
					String text = null;
					if(confeditorElement.length>0)
					{
						editorID = confeditorElement[0];
					}
					if(confeditorElement.length>1)
					{
						initials = confeditorElement[1];
					}
					if(confeditorElement.length>2)
					{
						indexed_name = confeditorElement[2];
					}
					if(confeditorElement.length>3)
					{
						degrees = confeditorElement[3];
					}
					if(confeditorElement.length>4)
					{
						surname = confeditorElement[4];
					}
					if(confeditorElement.length>5)
					{
						given_name = confeditorElement[5];
					}
					if(confeditorElement.length>6)
					{
						suffix = confeditorElement[6];
					}
					if(confeditorElement.length>7)
					{
						nametext = confeditorElement[7];
					}
					if(confeditorElement.length>8)
					{
						text = confeditorElement[8];
					}
					
					if(initials!=null && initials.length()>0)
					{
						file.write("<ce:initials>"+cleanBadCharacters(initials)+"</ce:initials>\n");
					}
					
					
					
					if(indexed_name!=null && indexed_name.length()>0)
					{
						file.write("<ce:indexed-name>"+cleanBadCharacters(indexed_name)+"</ce:indexed-name>\n");
					}
					else
		    		{
		    			if(surname!=null && initials!=null)
		    			{
		    				file.write("<ce:indexed-name>"+cleanBadCharacters(surname)+" "+cleanBadCharacters(initials)+"</ce:indexed-name>\n");
		    			}
		    			else if(surname!=null && given_name!=null)
		    			{
		    				file.write("<ce:indexed-name>"+cleanBadCharacters(surname)+" "+cleanBadCharacters(given_name)+"</ce:indexed-name>\n");
		    			}
		    			else if(surname!=null)
		    			{
		    				file.write("<ce:indexed-name>"+cleanBadCharacters(surname)+"</ce:indexed-name>\n");
		    			}
		    			else if(given_name!=null)
		    			{
		    				file.write("<ce:indexed-name>"+cleanBadCharacters(given_name)+"</ce:indexed-name>\n");
		    			}
		    			else
		    			{
		    				file.write("<ce:indexed-name/>");
		    			}
		    		}
					
					if(degrees!=null && degrees.length()>0)
					{
						file.write("<ce:degrees>"+degrees+"</ce:degrees>\n");
					}
					
					if(surname!=null && surname.length()>0)
					{
						file.write("<ce:surname>"+cleanBadCharacters(surname)+"</ce:surname>\n");
					}
										
					if(given_name!=null && given_name.length()>0)
					{
						file.write("<ce:given-name>"+cleanBadCharacters(given_name)+"</ce:given-name>\n");
					}
					
					if(suffix!=null && suffix.length()>0)
					{
						file.write("<ce:suffix>"+suffix+"</ce:suffix>\n");
					}
					if(nametext!=null && nametext.length()>0)
					{
						file.write("<ce:nametext>"+cleanBadCharacters(nametext)+"</ce:nametext>\n");
					}
					
					if(text!=null && text.length()>0)
					{
						file.write("<nametext>"+cleanBadCharacters(text)+"</nametext>\n");
					}
					    												
					file.write("</editor>\n");
				}
			}
			file.write("</editors>\n");
			if(conferenceorganization!=null && conferenceorganization.length()>0)
			{
				file.write("<editororganization>"+cleanBadCharacters(conferenceorganization)+"</editororganization>\n");
			}
			
			if(conferenceeditoraddress!=null && conferenceeditoraddress.trim().length()>0)
			{
				file.write("<editoraddress>"+cleanBadCharacters(conferenceeditoraddress)+"</editoraddress>\n");
			}
			file.write("</confeditors>\n");
		}
		
	}
	
    
    private void outputControlledterm(FileWriter file, String controlledterm) throws Exception
    {
    	file.write("<descriptors");
		String descriptorsType = null;
		if(databaseName.equals("cpx")  || databaseName.equals("chm"))
		{
			descriptorsType="CCV";
		}
		else if(databaseName.equals("pch")) 
		{
			descriptorsType="PCV";
		}
		else if(databaseName.equals("geo")) 
		{
			descriptorsType="GDE";
		}
		
		file.write(" controlled=\"y\" type=\""+descriptorsType+"\">\n");
		
		String[] controlledtermElements = controlledterm.split(Constants.AUDELIMITER,-1);
		for(int i=0;i<controlledtermElements.length;i++)
		{
			String controlledtermElement = controlledtermElements[i];
			if(controlledtermElement!=null && controlledtermElement.length()>0)
			{
				file.write("<descriptor>\n");
				file.write("<mainterm>"+cleanBadCharacters(controlledtermElement)+"</mainterm>\n");
				file.write("</descriptor>\n");
			}
		}
		file.write("</descriptors>\n");
    }
    
    private void outputELTMAINTerms(String term, FileWriter file, String controlledterm) throws Exception
    {
    	file.write("<"+term+">\n");					
		
		String[] controlledtermElements = controlledterm.split(Constants.IDDELIMITER,-1);
		for(int i=0;i<controlledtermElements.length;i++)
		{
			String controlledtermElement = controlledtermElements[i];
			if(controlledtermElement!=null && controlledtermElement.length()>0)
			{
				file.write("<API-term major-term-indicator=");
				if(term.equalsIgnoreCase("API-AMS"))
				{
					file.write("\"M\">");
				}
				else if (term.equalsIgnoreCase("API-APC"))
				{
					file.write("\"P\">");
				}
				else
				{
					file.write(">");
				}
				file.write(cleanBadCharacters(controlledtermElement)+"</API-term>\n");				
			}
		}
		file.write("</"+term+">\n");
    }
    
    private void outputELTTerms(String term, FileWriter file, String controlledterm) throws Exception
    {
    	file.write("<"+term+">\n");
		String descriptorsType = null;				
		
		String[] controlledtermElements = controlledterm.split(Constants.IDDELIMITER,-1);
		for(int i=0;i<controlledtermElements.length;i++)
		{
			String controlledtermElement = controlledtermElements[i];
			if(controlledtermElement!=null && controlledtermElement.length()>0)
			{
				file.write("<autoposting-term>"+cleanBadCharacters(controlledtermElement)+"</autoposting-term>\n");				
			}
		}
		file.write("</"+term+">\n");
    }
    
    private void outputAPILT(FileWriter file, String controlledterm) throws Exception
    {
    	file.write("<API-LT>\n");
		String descriptorsType = null;				
		
		String[] controlledtermElements = controlledterm.split(Constants.AUDELIMITER,-1);
		for(int i=0;i<controlledtermElements.length;i++)
		{
			String controlledtermElement = controlledtermElements[i];
			if(controlledtermElement!=null && controlledtermElement.length()>0)
			{
				file.write("<autoposting-term>"+cleanBadCharacters(controlledtermElement)+"</autoposting-term>\n");				
			}
		}
		file.write("</API-LT>\n");
    }
    
    private void outputELTtermgroup(FileWriter file, String controlledterm) throws Exception
    {
    	file.write("<API-LTM>\n");
		String descriptorsType = null;				
		
		String[] controlledtermElements = controlledterm.split(Constants.GROUPDELIMITER,-1);
		for(int i=0;i<controlledtermElements.length;i++)
		{
			String controlledtermElement = controlledtermElements[i];
			file.write("<API-LTM-group>\n");
			if(controlledtermElement!=null && controlledtermElement.length()>0)
			{
				String[] controllSubterms = controlledtermElement.split(Constants.IDDELIMITER,-1);
				for(int j=0;j<controllSubterms.length;j++)
				{
					String controllSubtermElement = controllSubterms[j];
					file.write("<autoposting-term>"+cleanBadCharacters(controllSubtermElement)+"</autoposting-term>\n");	
				}
			}
			file.write("</API-LTM-group>\n");
		}
		file.write("</API-LTM>\n");
    }
    
    
    private void outputUncontrolledterm(FileWriter file, String uncontrolledterm) throws Exception
    {
    	file.write("<descriptors controlled=\"n\" type=\"CFL\">\n");	    								    							
		
		String[] uncontrolledtermElements = uncontrolledterm.split(Constants.AUDELIMITER,-1);
		for(int i=0;i<uncontrolledtermElements.length;i++)
		{
			String uncontrolledtermElement = uncontrolledtermElements[i];
			if(uncontrolledtermElement!=null && uncontrolledtermElement.length()>0)
			{
				file.write("<descriptor>\n");
				file.write("<mainterm>"+cleanBadCharacters(uncontrolledtermElement)+"</mainterm>\n");
				file.write("</descriptor>\n");
			}
		}
		file.write("</descriptors>\n");
    }
    
    private void outputMainHeading(FileWriter file, String mainheading) throws Exception
    {
    	file.write("<descriptors controlled=\"y\" type=\"CMH\">\n");	    								    							
		
		String[] mainheadingElements = mainheading.split(Constants.AUDELIMITER,-1);
		for(int i=0;i<mainheadingElements.length;i++)
		{
			String mainheadingElement = mainheadingElements[i];
			if(mainheadingElement!=null && mainheadingElement.length()>0)
			{
				file.write("<descriptor>\n");
				file.write("<mainterm>"+cleanBadCharacters(mainheadingElement)+"</mainterm>\n");
				file.write("</descriptor>\n");
			}
		}
		file.write("</descriptors>\n");
    }
    
    private void outputSpeciesterm(FileWriter file, String speciesterm) throws Exception
    {
    	file.write("<descriptors controlled=\"y\" type=\"SPC\">\n");	    								    							
		
		String[] speciestermElements = speciesterm.split(Constants.AUDELIMITER,-1);
		for(int i=0;i<speciestermElements.length;i++)
		{
			String speciestermElement = speciestermElements[i];
			if(speciestermElement!=null && speciestermElement.length()>0)
			{
				file.write("<descriptor>\n");
				file.write("<mainterm>"+cleanBadCharacters(speciestermElement)+"</mainterm>\n");
				file.write("</descriptor>\n");
			}
		}
		file.write("</descriptors>\n");
    }
    
    private void outputRegionalterm(FileWriter file, String regionalterm) throws Exception
    {
    	file.write("<descriptors controlled=\"y\" type=\"RGI\">\n");	    								    							
		
		String[] regionaltermElements = regionalterm.split(Constants.AUDELIMITER,-1);
		for(int i=0;i<regionaltermElements.length;i++)
		{
			String regionaltermElement = regionaltermElements[i];
			if(regionaltermElement!=null && regionaltermElement.length()>0)
			{
				file.write("<descriptor>\n");
				file.write("<mainterm>"+cleanBadCharacters(regionaltermElement)+"</mainterm>\n");
				file.write("</descriptor>");
			}
		}
		file.write("</descriptors>\n");
    }
    
    private void outputTreaTmentCode(FileWriter file,String treatmentcode) throws Exception
    {
    	file.write("<descriptors controlled=\"y\" type=\"CTC\">\n");	    								    							
		
		String[] treatmentcodeElements = treatmentcode.split(Constants.AUDELIMITER,-1);
		for(int i=0;i<treatmentcodeElements.length;i++)
		{
			String treatmentcodeElement = treatmentcodeElements[i];
			if(treatmentcodeElement!=null && treatmentcodeElement.length()>0)
			{
				file.write("<descriptor>\n");
				file.write("<mainterm>"+cleanBadCharacters(treatmentcodeElement)+"</mainterm>\n");
				file.write("</descriptor>\n");
			}
		}
		file.write("</descriptors>\n");
    }
    
    private void outputChemicalterm(FileWriter file,String chemicalterm) throws Exception
    {
    	file.write("<descriptors controlled=\"y\" type=\"MED\">\n");	    								    							
		
		String[] chemicaltermElements = chemicalterm.split(Constants.AUDELIMITER,-1);
		for(int i=0;i<chemicaltermElements.length;i++)
		{
			String chemicaltermElement = chemicaltermElements[i];
			if(chemicaltermElement!=null && chemicaltermElement.length()>0)
			{
				file.write("<descriptor>\n");
				file.write("<mainterm>"+cleanBadCharacters(chemicaltermElement)+"</mainterm>\n");
				file.write("</descriptor>\n");
			}
		}
		file.write("</descriptors>\n");
    }
    
    private void outputClassificationgroup(FileWriter file,String classificationcode) throws Exception
    {
    	CPXDataDictionary classificationDesc = new CPXDataDictionary();
    	file.write("<classificationgroup>\n");
		file.write("<classifications");
		if(databaseName.equals("geo"))
		{
			file.write(" type=\"GEOCLASS\">\n");	
		}
		else
		{
			file.write(" type=\"CPXCLASS\">\n");	
		}
		
		String[] classificationcodeElements = classificationcode.split(Constants.AUDELIMITER,-1);
		for(int i=0;i<classificationcodeElements.length;i++)
		{
			String  classificationcodeElement = classificationcodeElements[i];	    					
			file.write("<classification>\n");
			file.write("<classification-code>"+classificationcodeElement+"</classification-code>\n");
			if(classificationDesc.getClassCodeTitle(classificationcodeElement)!=null)
			{
				file.write("<classification-description>"+classificationDesc.getClassCodeTitle(classificationcodeElement)+"</classification-description>\n");
				//System.out.println("CLASSCODE="+classificationcodeElement+" Desc="+classificationDesc.getClassCodeTitle(classificationcodeElement));
			}
			file.write("</classification>\n");
		}
		file.write("</classifications>\n");
		file.write("</classificationgroup>\n");
    }
    
    private void outputELTClassification(FileWriter file, String classificationcode) throws Exception
    {
    	CPXDataDictionary classificationDesc = new CPXDataDictionary();
    	file.write("<API-CC>\n");
					
		String[] classificationcodeElements = classificationcode.split(Constants.AUDELIMITER,-1);
		for(int i=0;i<classificationcodeElements.length;i++)
		{
			String  classificationcodeElement = classificationcodeElements[i];	    					
			file.write("<classification>\n");
			file.write("<classification-description>"+classificationcodeElement+"</classification-description>\n");			
			file.write("</classification>\n");
		}
		file.write("</API-CC>\n");		
    }
    
    private void outputManufacturergroups(FileWriter file,String manufacturer) throws Exception
    {
    	file.write("<manufacturergroup>\n");
		String[] manufacturers = manufacturer.split(Constants.AUDELIMITER,-1);
		for(int i=0;i<manufacturers.length;i++)
		{
			String manufacturersElement = manufacturers[i];
			if(manufacturersElement!=null && manufacturersElement.length()>0)
			{
				file.write("<manufacturers>\n");
				String[] manufacturerArray = manufacturersElement.split(Constants.IDDELIMITER,-1);
				for(int j=0;j<manufacturerArray.length;j++)
				{
					String manufacturerElement = manufacturerArray[j];
					if(manufacturerElement!=null && manufacturerElement.length()>0)
					{
						String[] manufacturerDetail = manufacturerElement.split(Constants.GROUPDELIMITER,-1);
						if(manufacturerDetail.length==2)
						{
							file.write("<manufacturer");
							String manufacturerCountry = manufacturerDetail[0];
							String manufacturerContent = manufacturerDetail[1];
							if(manufacturerCountry!=null && manufacturerCountry.length()>0)
							{
								file.write(" country=\""+manufacturerDetail[0]+"\">\n");
							}
							else
							{
								file.write(">\n");
							}
							
							if(manufacturerContent!=null && manufacturerContent.length()>0) 
							{
								file.write(cleanBadCharacters(manufacturerContent));
							}
							
							file.write("</manufacturer>\n");
						}//if
					}//if
				}//for
				file.write("</manufacturers>\n");
			}//if
		}//for
		file.write("</manufacturergroup>\n");
    }
    
    private void outputSequencebanks(FileWriter file,String sequencebank) throws Exception
    {
    	//System.out.println("sequencebank="+sequencebank);
		file.write("<sequencebanks>\n");
		String[] sequencebanks = sequencebank.split(Constants.AUDELIMITER,-1);
		for(int i=0;i<sequencebanks.length;i++)
		{
			String sequencebankElements = sequencebanks[i];
			//System.out.println("sequencebankElements="+sequencebankElements);
			if(sequencebankElements!=null && sequencebankElements.length()>0)
			{
				file.write("<sequencebank");
				String[] sequencebankArray = sequencebankElements.split(Constants.IDDELIMITER,-1);
				String sequencebankName = sequencebankArray[0];
				if(sequencebankName!=null && sequencebankName.length()>0)
				{
					file.write(" name=\""+cleanBadCharacters(sequencebankName)+"\">\n");
				}
				else
				{
					file.write(">\n");
				}
				
				for(int j=1;j<sequencebankArray.length;j++)
				{
					file.write("<sequence-number>"+sequencebankArray[j]+"</sequence-number>\n");
					//System.out.println("sequence-number="+sequencebankArray[j]);
				}
				file.write("</sequencebank>\n");
				
			}//if
		}//for
		file.write("</sequencebanks>\n");
    }
    
    private void outputTradenameGroup(FileWriter file,String tradename) throws Exception 
    {
    	file.write("<tradenamegroup>\n");
		String[] tradenamesArray = tradename.split(Constants.AUDELIMITER,-1);
		for(int i=0;i<tradenamesArray.length;i++)
		{
			file.write("<tradenames");
			String tradenames = tradenamesArray[i];
			if(tradenames!=null && tradenames.length()>0)
			{
				String[] tradenameElements = tradenames.split(Constants.IDDELIMITER,-1);
				//System.out.println("tradenames SIZE="+tradenameElements.length);
				String tradenameType = tradenameElements[0];
				if(tradenameType!=null && tradenameType.length()>0)
				{
					file.write(" type=\""+tradenameType+"\">\n");
				}
				else
				{
					file.write(">\n");
				}
				for(int j=1;j<tradenameElements.length;j++)
				{
					String tradenameContent = tradenameElements[j];
					file.write("<trademanuitem>\n<tradename>"+cleanBadCharacters(tradenameContent)+"</tradename>\n</trademanuitem>\n");
				}  								 								
			}//if
			else
			{
				file.write(">\n");
			}
			file.write("</tradenames>\n");
		}//for 						
		file.write("</tradenamegroup>\n");
    }
    
    private void outputChemicalgroup(FileWriter file, String casregistrynumber) throws Exception 
    {
    	file.write("<chemicalgroup>\n");
		String[] chemicalgroup = casregistrynumber.split(Constants.AUDELIMITER,-1);	
		for(int i=0;i<chemicalgroup.length;i++)
		{
			file.write("<chemicals>\n");
			String chemicals=chemicalgroup[i];
			if(chemicals!=null && chemicals.length()>0)
			{
				String[] chemicalsElement = chemicals.split(Constants.IDDELIMITER,-1);
				for(int j=0;j<chemicalsElement.length;j++)
				{
					
					String chemical = chemicalsElement[j];
					if(chemical!=null && chemical.length()>0)
					{
						file.write("<chemical>\n");
						String[] chemicalElement = chemical.split(Constants.GROUPDELIMITER,-1);
						String chemical_name = chemicalElement[0];
						
						if(chemical_name!=null && chemical_name.length()>0)
						{
							file.write("<chemical-name>"+cleanBadCharacters(chemical_name)+"</chemical-name>\n");
						}
						
						for(int k=1; k<chemicalElement.length;k++)
						{    										
							String cas_registry_number = chemicalElement[k];
							if(cas_registry_number != null && cas_registry_number.length()>0)
							{
								file.write("<cas-registry-number>"+cas_registry_number+"</cas-registry-number>\n");
							}
						}
						file.write("</chemical>\n");
					}//if
				}//for						
			}//if
			file.write("</chemicals>\n");
		}//for
		file.write("</chemicalgroup>\n");
    }
    
    public void getReference(FileWriter file, String accessnumber)
    {
    	 Statement stmt = null;
         ResultSet rs = null;        
         
    	 try
         {
             stmt = con.createStatement();           
             rs = stmt.executeQuery("select *  from bd_master_reference where accessnumber='"+accessnumber+"'");
             while (rs.next())
             {
            	                    
            	 String referenceid = rs.getString("REFERENCEID");                     
            	 String referencetitle = rs.getString("REFERENCETITLE");                  
            	 String referenceauthor = rs.getString("REFERENCEAUTHOR");                 
            	 String referencesourcetitle = rs.getString("REFERENCESOURCETITLE");            
            	 String referencepublicationyear = rs.getString("REFERENCEPUBLICATIONYEAR");        
            	 String referencevolume = rs.getString("REFERENCEVOLUME");                
            	 String referenceissue = rs.getString("REFERENCEISSUE");                  
            	 String referencepages = rs.getString("REFERENCEPAGES");                  
            	 String referencefulltext = rs.getString("REFERENCEFULLTEXT");              
            	 String referencetext = rs.getString("REFERENCETEXT");                   
            	 String referencewebsite = rs.getString("REFERENCEWEBSITE");                
            	 String referenceitemid = rs.getString("REFERENCEITEMID");                 
            	 String referenceitemcitationpii = rs.getString("REFERENCEITEMCITATIONPII");        
            	 String referenceitemcitationdoi = rs.getString("REFERENCEITEMCITATIONDOI");        
            	 String referenceitemcitationauthor = rs.getString("REFERENCEITEMCITATIONAUTHOR");     
            	 String refitemcitationsourcetitle = rs.getString("REFITEMCITATIONSOURCETITLE");      
            	 String referenceitemcitationissn = rs.getString("REFERENCEITEMCITATIONISSN");       
            	 String referenceitemcitationisbn = rs.getString("REFERENCEITEMCITATIONISBN");       
            	 String referenceitemcitationpart = rs.getString("REFERENCEITEMCITATIONPART");       
            	 String refitemcitationpublicationyear = rs.getString("REFITEMCITATIONPUBLICATIONYEAR");  
            	 String referenceitemcitationvolume = rs.getString("REFERENCEITEMCITATIONVOLUME");     
            	 String referenceitemcitationissue = rs.getString("REFERENCEITEMCITATIONISSUE");      
            	 String referencecitationpage = rs.getString("REFERENCEITEMCITATIONPAGE");       
            	 String refitemcitationarticlenumber = rs.getString("REFITEMCITATIONARTICLENUMBER");    
            	 String referenceitemcitationwebsite = rs.getString("REFERENCEITEMCITATIONWEBSITE");    
            	 String referenceitemcitationeaddress = rs.getString("REFERENCEITEMCITATIONEADDRESS");   
            	 String referenceitemcitationreftext = rs.getString("REFERENCEITEMCITATIONREFTEXT");    
            	 String referenceitemcitationtitle = rs.getString("REFERENCEITEMCITATIONTITLE");      
            	 String referenceitemcitationcoden = rs.getString("REFERENCEITEMCITATIONCODEN");      
            	 String refcitationsourcetitleabbrev = rs.getString("REFCITATIONSOURCETITLEABBREV");  
            	 String pui = rs.getString("PUI");
      
            	 
            	 file.write("<reference");
            	 //REFERENCEID
            	 if(referenceid!=null && referenceid.length()>0)
            	 {
            		 file.write(" id=\""+referenceid+"\">\n");
            	 }
            	 else
            	 {
            		 file.write(">\n");
            	 }
            	 file.write("<ref-info>\n");
            	 //REFERENCETITLE
            	 if(referencetitle!=null && referencetitle.trim().length()>1)
            	 {           	
            		 file.write("<ref-title>\n");
            		 String[] titles = referencetitle.split(",");
            		 for(int i=0;i<titles.length;i++)
            		 {
            			 file.write("<ref-titletext>"+cleanBadCharacters(referencetitle)+"</ref-titletext>\n");
            		 } 
            		 file.write("</ref-title>\n");
            	 }
            	 
            	 if((pui!=null && pui.length()>0) || (accessnumber!=null && accessnumber.length()>0))
     	 		{
     	 			file.write("<refd-itemidlist>\n");
     	 			//file.write("<itemid>");
     	 			file.write("<itemid  idtype=\"CPX\">"+accessnumber+"</itemid>\n"); 
     	 			if(pui!=null && pui.length()>0)
     	 			{
     	 				file.write("<itemid  idtype=\"PUI\">"+pui+"</itemid>\n");
     	 			}
     	 			//file.write("</itemid>");
     	 			file.write("</refd-itemidlist>\n");
     	 		}
            	 
            	if(referenceauthor!=null && referenceauthor.length()>0)
         		{
         			String[] referenceauthors = referenceauthor.split(Constants.AUDELIMITER,-1);
         			file.write("<ref-authors>\n");
         			for(int i=0;i<referenceauthors.length;i++)
         			{
         				String rauthors = referenceauthors[i];       				
         				if(rauthors!=null && rauthors.length()>0)
         				{
         					String[] referenceauthorElements = rauthors.split(Constants.IDDELIMITER,-1);
         					//System.out.println("REFERENCE AUTHOR SIZE="+referenceauthorElements.length);
         					String rid = null;
         					String seq = null;
         					String auid = null;
         					String indexed_name = null;
         					String initials = null;
         					String surname = null;
         					String suffix = null;
         					String nametext = null;
         					
         					if(referenceauthorElements.length<2)
         					{
         						nametext = referenceauthorElements[0];
         						//System.out.println("nametext="+nametext);
         					}
         					
         					if(referenceauthorElements.length>=4)
         					{
         						 seq = referenceauthorElements[0];
         						 rid = referenceauthorElements[1];
         						 auid = referenceauthorElements[2];
         						 indexed_name = referenceauthorElements[3];
         					}
         					
     						if(referenceauthorElements.length>=6)
     						{
     							 initials = referenceauthorElements[4];
     							surname = referenceauthorElements[5];
     						    //System.out.println("initials="+initials);
     						    //System.out.println("surname="+surname);
         					}
     						
     						if(referenceauthorElements.length>=9)
     						{
     							String ELEMENT6 = referenceauthorElements[6];
     							String ELEMENT7 = referenceauthorElements[7];
     							 suffix = referenceauthorElements[8];
     							 /*
     							if(ELEMENT6!=null && ELEMENT6.length()>0)
     								System.out.println("ELEMENT6="+ELEMENT6);
     							if(ELEMENT7!=null && ELEMENT7.length()>0)
     								System.out.println("ELEMENT7="+ELEMENT7);
     						    	System.out.println("suffix="+suffix);
     						    */
         					}
         						
     						//System.out.println("ref ID="+rid);
     						//System.out.println("seq="+seq);
     						//System.out.println("auid="+auid);
     						//System.out.println("indexed_name="+indexed_name);
     						file.write("<author");
     						if(seq!=null && seq.length()>0)
     						{
     							file.write(" seq=\""+seq+"\"");	    							
     						}
     						else
     						{
     							file.write(" seq=\""+i+"\"");
     						}
     						file.write(">\n");
     						
     						if(indexed_name==null || indexed_name.trim().length()<1)
     						{						
     			    			if(surname!=null && initials!=null)
     			    			{
     			    				file.write("<ce:indexed-name>"+cleanBadCharacters(surname)+" "+cleanBadCharacters(initials)+"</ce:indexed-name>\n");
     			    			}
     			    			else if(surname!=null)
     			    			{
     			    				file.write("<ce:indexed-name>"+cleanBadCharacters(surname)+"</ce:indexed-name>\n");
     			    			}
     			    			else if(surname!=null)
     			    			{
     			    				file.write("<ce:indexed-name>"+cleanBadCharacters(surname)+"</ce:indexed-name>\n");
     			    			}    			    		
     			    			else
     			    			{
     			    				file.write("<ce:indexed-name/>");
     			    			}     			    		
	     						
     						}
     						
     						if(initials!=null && initials.length()>0)
     						{
     							//System.out.println(initials+" --- "+cleanBadCharacters(initials));
     							file.write("<ce:initials>"+cleanBadCharacters(initials)+"</ce:initials>\n");
     						}
     						if(indexed_name!=null && indexed_name.length()>0)
     						{
     							file.write("<ce:indexed-name>"+cleanBadCharacters(indexed_name)+"</ce:indexed-name>\n");	    							
     						}
     						if(surname!=null && surname.length()>0)
     						{
     							file.write("<ce:surname>"+cleanBadCharacters(surname)+"</ce:surname>\n");
     						}
     						
     						if(suffix!=null && suffix.length()>0)
     						{
     							file.write("<ce:suffix>"+cleanBadCharacters(suffix)+"</ce:suffix>\n");    							
     						}   						
     						file.write("</author>\n");
         						
         				}
         			}//for
         			file.write("</ref-authors>\n");      				
         		}
            	 	
            	 	
        	 	//REFERENCESOURCETITLE
        	 	if(referencesourcetitle!=null && referencesourcetitle.length()>0)
        	 	{
        	 		file.write("<ref-sourcetitle>"+cleanBadCharacters(referencesourcetitle)+"</ref-sourcetitle>\n");
        	 	}
            	 	
        	 	//REFERENCEPUBLICATIONYEAR
        	 	if(referencepublicationyear!=null && referencepublicationyear.length()>0)
        	 	{
        	 		file.write("<ref-publicationyear");
        	 		if(referencepublicationyear.indexOf("-")<0)
        	 		{
        	 			file.write(" first=\""+referencepublicationyear+"\"");
        	 		}
        	 		else
        	 		{
        	 			String[] publicationYears = referencepublicationyear.split("-");
        	 			if(publicationYears[0]!=null && publicationYears[0].length()>0)
        	 			{
        	 				file.write(" first=\""+publicationYears[0]+"\"");
        	 			}
        	 			
        	 			if(publicationYears[1]!=null && publicationYears[1].length()>0)
        	 			{
        	 				file.write(" last=\""+publicationYears[1]+"\"");
        	 			}
        	 			
        	 		}
        	 		file.write("/>\n");
        	 	}
            	 	
            	 	
        	 	//REFERENCEVOLUME
        	 	if((referencevolume!=null && referencevolume.length()>0) || (referenceissue!=null && referenceissue.length()>0) || (referencepages!=null && referencepages.length()>0))            	 	
        	 	{
        	 		file.write("<ref-volisspag>\n");
        	 		if((referencevolume!=null && referencevolume.length()>0) || (referenceissue!=null && referenceissue.length()>0))
        	 		{
	        	 		file.write("<voliss");
	        	 		if(referencevolume!=null && referencevolume.length()>0)
	        	 		{
	        	 			file.write(" volume=\""+cleanBadCharacters(referencevolume)+"\"");
	        	 		}
	        	 		
	        	 		if(referenceissue!=null && referenceissue.length()>0)
	        	 		{
	        	 			file.write(" issue=\""+cleanBadCharacters(referenceissue)+"\"");
	        	 		}
	        	 		
	        	 		file.write("/>\n");
        	 		}
            	 		
        	 		if(referencepages!=null && referencepages.length()>0)
        	 		{
        	 			if(referencepages.indexOf("PAGES:")>-1)
        	 			{
        	 				file.write("<pages>"+cleanBadCharacters(referencepages.substring(6))+"\"</pages>\n");
        	 			}
        	 			else if(referencepages.indexOf("PAGERANGE:")>-1)
        	 			{
        	 				file.write("<pagerange");
        	 				referencepages = cleanBadCharacters(referencepages.substring(9));
        	 				if(referencepages.indexOf("-")>-1)
        	 				{
        	 					String[] referenceP = referencepages.split("-");
        	 					if(referenceP.length>0 && referenceP[0]!=null && referenceP[0].length()>0)
        	 					{
        	 						file.write(" first=\""+referenceP[0].replace(":", "")+"\"");           	 						
        	 					}
        	 					if(referenceP.length>1 && referenceP[1]!=null && referenceP[1].length()>0)
        	 					{
        	 						file.write(" last=\""+referenceP[1]+"\"");           	 						
        	 					}
        	 					
        	 				}
        	 				else
        	 				{
        	 					file.write(" first=\""+referencepages+"\"");
        	 				}
        	 				file.write("/>\n");
        	 			}
        	 			else if(referencepages.indexOf("PAGECOUNT:")>-1)
        	 			{
        	 				file.write("<pagecount");
        	 				
        	 				String[] referenceP = referencepages.split(Constants.IDDELIMITER);
        	 				if(referenceP.length==1)
        	 				{
        	 					String pagecount = referenceP[0];
        	 					if(pagecount.indexOf(":")>0)
        	 					{
        	 						pagecount=pagecount.substring(pagecount.indexOf(":")+1);
        	 						//System.out.println("pagecount="+pagecount);
        	 					}
        	 					file.write(">"+pagecount+"</pagecount>\n");
        	 				}
        	 				else if(referenceP.length>1)
        	 				{
        	 					String pagecountType = referenceP[0];
        	 					if(pagecountType.indexOf(":")>0)
        	 					{
        	 						pagecountType=pagecountType.substring(pagecountType.indexOf(":")+1);
        	 						//System.out.println("pagecountType="+pagecountType);
        	 					}
        	 					String pagecount = referenceP[1];
        	 					if(pagecount.indexOf(":")>0)
        	 					{
        	 						pagecount=pagecount.substring(pagecount.indexOf(":")+1);
        	 						//System.out.println("pagecount="+pagecount);
        	 					}
        	 					file.write(" type=\""+pagecountType+"\">"+pagecount+"</pagecount>\n");
        	 				}
        	 			
        	 			}//if
        	 		
        	 		}//REFERENCEVOLUME
        	 		
        	 		file.write("</ref-volisspag>\n");
        	 	}
        	 	
        	 	//REFERENCEWEBSITE
    	 		if(referencewebsite!=null && referencewebsite.length()>0)
    	 		{
    	 			file.write("<ref-website>\n");
    	 			String[] referencewebsites = referencewebsite.split(Constants.IDDELIMITER);
    	 			if(referencewebsites[0]!=null && referencewebsites[0].length()>0)
    	 			{
    	 				file.write("<websitename>"+cleanBadCharacters(referencewebsites[0])+"</websitename>\n");
    	 			}
    	 			
    	 			if(referencewebsites.length>1 && referencewebsites[1]!=null && referencewebsites[1].length()>0)
    	 			{
    	 				file.write("<ce:e-address type=\"email\">"+cleanBadCharacters(referencewebsites[1])+"</ce:e-address>\n");
    	 			}
    	 			        	 			
    	 			file.write("</ref-website>\n");
    	 		}
            	 		
    	 		
        	 	
        	 	//REFERENCETEXT
    	 		if(referencetext!=null && referencetext.length()>0)
    	 		{
    	 			file.write("<ref-text>"+cleanBadCharacters(referencetext)+"</ref-text>\n");
    	 		}
        	 		
            	file.write("</ref-info>\n");       	
        	 	
    	 		//REFERENCEFULLTEXT
    	 		if(referencefulltext!=null && referencefulltext.length()>0)
    	 		{
    	 			file.write("<ref-fulltext>"+cleanBadCharacters(referencefulltext)+"</ref-fulltext>\n");
    	 		}
    	 			
            	 		
    	 		if((referenceitemcitationpii!=null && referenceitemcitationpii.length()>0) || 
    	 				(referenceitemcitationdoi!=null && referenceitemcitationdoi.length()>0) ||
    	 				(referenceitemcitationauthor!=null && referenceitemcitationauthor.length()>0) || 
    	 				(refitemcitationsourcetitle!=null && refitemcitationsourcetitle.length()>0) ||
    	 				(refcitationsourcetitleabbrev!=null && refcitationsourcetitleabbrev.length()>0) ||
    	 				(referenceitemcitationissn!=null && referenceitemcitationissn.length()>0) ||
    	 				(referenceitemcitationisbn!=null && referenceitemcitationisbn.length()>0) || 
    	 				(referenceitemcitationpart!=null && referenceitemcitationpart.length()>0) ||           	 				
    	 				(refitemcitationpublicationyear!=null && refitemcitationpublicationyear.length()>0) ||
    	 				(referenceitemcitationvolume!=null && referenceitemcitationvolume.length()>0) || 
    	 				(referenceitemcitationissue!=null && referenceitemcitationissue.length()>0) ||
    	 				(referencecitationpage!=null && referencecitationpage.length()>0) ||
    	 				(referenceitemcitationisbn!=null && referenceitemcitationisbn.length()>0) || 
    	 				(refitemcitationarticlenumber!=null && refitemcitationarticlenumber.length()>0) ||          	 				
    	 				(referenceitemcitationwebsite!=null && referenceitemcitationwebsite.length()>0) ||
    	 				(referenceitemcitationeaddress!=null && referenceitemcitationeaddress.length()>0) || 
    	 				(referenceitemcitationreftext!=null && referenceitemcitationreftext.length()>0) ||
    	 				(referenceitemcitationtitle!=null && referenceitemcitationtitle.length()>0) ||
    	 				(referenceitemcitationcoden!=null && referenceitemcitationcoden.length()>0))
    	 		{
    	 			file.write("<refd-itemcitation  type=\"core\">\n");  
    	 			if(referenceitemcitationpii!=null && referenceitemcitationpii.length()>0)
    	 			{
    	 				file.write("<ce:pii>"+referenceitemcitationpii+"</ce:pii>\n");
    	 			}
    	 			
    	 			if(referenceitemcitationdoi!=null && referenceitemcitationdoi.length()>0)
    	 			{
    	 				file.write("<ce:doi>"+cleanBadCharacters(referenceitemcitationdoi)+"</ce:doi>\n");
    	 			}
    	 			
    	 			if(refitemcitationsourcetitle!=null && refitemcitationsourcetitle.length()>0)
    	 			{
    	 				file.write("<sourcetitle>"+cleanBadCharacters(refitemcitationsourcetitle)+"</sourcetitle>\n");
    	 			}
    	 			
    	 			if(refcitationsourcetitleabbrev!=null && refcitationsourcetitleabbrev.length()>0)
    	 			{
    	 				file.write("<sourcetitle-abbrev>"+cleanBadCharacters(refcitationsourcetitleabbrev)+"</sourcetitle-abbrev>\n");
    	 			}
			         	 			
    	 			if(referenceitemcitationissn!=null && referenceitemcitationissn.length()>0)
    	 			{
    	 				if(referenceitemcitationissn.indexOf(Constants.IDDELIMITER)>-1)
    	 				{
        	 				String[] issns = referenceitemcitationissn.split(Constants.IDDELIMITER);
        	 				for(int i=0;i<issns.length;i++)
        	 				{
        	 					String issn = issns[i];
        	 					if(issn!=null && issn.length()>0)
        	 					{
        	 						if(issn.indexOf(":")>-1)
        	 						{
        	 							String[] issnElements = issn.split(":");
        	 							file.write("<issn type=\""+issnElements[0]+"\">"+issnElements[1]+"</issn>\n");
        	 						}
        	 						else
        	 						{
        	 							file.write("<issn type=\"print\">"+issn+"</issn>\n");
        	 						}
        	 						
        	 					}
        	 				}
    	 				}
    	 				else
    	 				{
    	 					if(referenceitemcitationissn.indexOf(":")>-1)
    	 					{
    	 						String[] issnElements = referenceitemcitationissn.split(":");
	 							file.write("<issn type=\""+issnElements[0]+"\">"+issnElements[1]+"</issn>\n");           	 						
    	 					}
    	 					else
    	 					{
    	 						file.write("<issn type=\"print\">"+referenceitemcitationissn+"</issn>\n");
    	 					}           	 					
    	 				}
    	 			}
    	 			
    	 			if(referenceitemcitationisbn!=null && referenceitemcitationisbn.length()>0)
    	 			{
    	 				String[] isbns = referenceitemcitationisbn.split(Constants.AUDELIMITER,-1);
    	 				for(int i=0;i<isbns.length;i++)
    	 				{
    	 					String isbnArray = isbns[i];
    	 					if(isbnArray!=null && isbnArray.length()>0)
    	 					{
    	 						String[] isbnElements = isbnArray.split(Constants.IDDELIMITER,-1);
    	 						if(isbnElements.length==4)
    	 						{
    	 							String type = isbnElements[0];
    	 							String length = isbnElements[1];
    	 							String level = isbnElements[2];
    	 							String value = isbnElements[3];
    	 							file.write("<isbn");
    	 							if(type!=null && type.length()>0)
    	 							{
    	 								file.write(" type=\""+type+"\"");
    	 							}
    	 							
    	 							if(length!=null && length.length()>0)
    	 							{
    	 								file.write(" length=\""+length+"\"");
    	 							}
    	 							
    	 							if(level!=null && level.length()>0)
    	 							{
    	 								file.write(" level=\""+level+"\"");
    	 							}
    	 							
    	 							file.write(">"+value+"</isbn>\n");
    	 						}
    	 						else
    	 						{
    	 							System.out.println("records "+accessnumber+" has wrong format in referenceitemcitationisbn");
    	 						}//if
    	 					}//if
    	 				}//for
    	 			}//if
            	 			
    	 			//REFERENCEITEMCITATIONPART
    	 			if(referenceitemcitationpart!=null && referenceitemcitationpart.length()>0)
    	 			{
    	 				file.write("<part>"+cleanBadCharacters(referenceitemcitationpart)+"</part>\n");
    	 			}
    	 			
    	 			//REFITEMCITATIONPUBLICATIONYEAR
    	 			if(refitemcitationpublicationyear!=null && refitemcitationpublicationyear.length()>0)
    	 			{
    	 				file.write("<publicationyear");
    	 				if(refitemcitationpublicationyear.indexOf("-")<0)
            	 		{
            	 			file.write(" first=\""+refitemcitationpublicationyear+"\"");
            	 		}
            	 		else
            	 		{
            	 			String[] publicationYears = refitemcitationpublicationyear.split("-");
            	 			if(publicationYears[0]!=null && publicationYears[0].length()>0)
            	 			{
            	 				file.write(" first=\""+publicationYears[0]+"\"");
            	 			}
            	 			
            	 			if(publicationYears.length>1 && publicationYears[1]!=null && publicationYears[1].length()>0)
            	 			{
            	 				file.write(" last=\""+publicationYears[1]+"\"");
            	 			}                  	 			
            	 		}
            	 		file.write("/>\n");            	 				
    	 			}//REFITEMCITATIONPUBLICATIONYEAR
            	 			
    	 			//REFERENCEITEMCITATIONVOLUME
    	 			if((referenceitemcitationvolume!=null && referenceitemcitationvolume.length()>0) ||
    	 					(referenceitemcitationissue!=null && referenceitemcitationissue.length()>0) ||
    	 					(referencecitationpage!=null && referencecitationpage.length()>0))
    	 			{
    	 				file.write("<volisspag>\n");
            	 		file.write("<voliss");
            	 		if(referenceitemcitationvolume!=null && referenceitemcitationvolume.length()>0)
            	 		{
            	 			file.write(" volume=\""+referenceitemcitationvolume+"\"");
            	 		}
            	 		
            	 		if(referenceitemcitationissue!=null && referenceitemcitationissue.length()>0)
            	 		{
            	 			file.write(" issue=\""+referenceitemcitationissue+"\"");
            	 		}
            	 		
            	 		file.write("/>\n");
            	 		
            	 		if(referencecitationpage!=null && referencecitationpage.length()>0)
            	 		{
            	 			if(referencecitationpage.indexOf("PAGES:")>-1)
            	 			{
            	 				file.write("<pages>"+cleanBadCharacters(referencecitationpage.substring(6))+"\"</pages>\n");
            	 			}
            	 			else if(referencecitationpage.indexOf("PAGERANGE:")>-1)
            	 			{
            	 				file.write("<pagerange");
            	 				referencecitationpage = referencecitationpage.substring(9);
            	 				if(referencecitationpage.indexOf("-")>-1)
            	 				{
            	 					String[] referenceP = referencecitationpage.split("-");
            	 					if(referenceP[0]!=null && referenceP[0].length()>0)
            	 					{
            	 						file.write(" first=\""+referenceP[0]+"\"");           	 						
            	 					}
            	 					if(referenceP.length>1 && referenceP[1]!=null && referenceP[1].length()>0)
            	 					{
            	 						file.write(" last=\""+referenceP[1]+"\"");           	 						
            	 					}
            	 					
            	 				}
            	 				else
            	 				{
            	 					file.write(" first=\""+referencecitationpage+"\"");
            	 				}
            	 				file.write("/>\n");
            	 			}
            	 			else if(referencecitationpage.indexOf("PAGECOUNT:")>-1)
            	 			{
            	 				file.write("<pagecount");
            	 				
            	 				String[] referenceP = referencecitationpage.split(Constants.IDDELIMITER);
            	 				if(referenceP.length==1)
            	 				{
            	 					file.write(">"+referenceP[0]+"</pagecount>\n");
            	 				}
            	 				else
            	 				{
            	 					file.write(" type=\""+referenceP[0]+"\">"+referenceP[1]+"</pagecount>\n");
            	 				}
            	 			
            	 			}
            	 		
            	 		}           	 		            	 		
            	 		file.write("</volisspag>\n");
    	 				
    	 			}//REFERENCEITEMCITATIONVOLUME
            	 			
    	 			//REF-ARTICLE-NUMBER
    	 			if(refitemcitationarticlenumber!=null && refitemcitationarticlenumber.length()>0)
    	 			{
    	 				file.write("<article-number>"+refitemcitationarticlenumber+"</article-number>\n");
    	 			}
            	 			
            	 			
    	 			if((referenceitemcitationwebsite!=null && referenceitemcitationwebsite.length()>0) ||
    	 					(referenceitemcitationeaddress!=null && referenceitemcitationeaddress.length()>0) )	
    	 			{
    	 				file.write("<website>\n");
    	 				 
    	 				//REFERENCEITEMCITATIONWEBSITE
        	 			if(referenceitemcitationwebsite!=null && referenceitemcitationwebsite.length()>0)
        	 			{
        	 				file.write("<websitename>"+cleanBadCharacters(referenceitemcitationwebsite)+"</websitename>\n");
        	 			}
        	 			
        	 			//REFERENCEITEMCITATIONEADDRESS
        	 			if(referenceitemcitationeaddress!=null && referenceitemcitationeaddress.length()>0)
        	 			{
        	 				file.write("<ce:e-address");
        	 				String[] referenceeaddress = referenceitemcitationeaddress.split(Constants.IDDELIMITER);
        	 				if(referenceeaddress[0]!=null && referenceeaddress[0].length()>0)
        	 				{
        	 					file.write(" type=\""+referenceeaddress[0]+"\"");
        	 				}
        	 				file.write(">");
        	 				if(referenceeaddress.length>1 && referenceeaddress[1]!=null && referenceeaddress[1].length()>0)
        	 				{
        	 					file.write(cleanBadCharacters(referenceeaddress[1]));
        	 				}
        	 				file.write("</ce:e-address>\n");
        	 			}
    	 				
    	 				file.write("</website>\n");
    	 			}
            	 			
    	 			if(referenceitemcitationreftext!=null && referenceitemcitationreftext.length()>0)
    	 			{
    	 				file.write("<ref-text>"+cleanBadCharacters(referenceitemcitationreftext)+"</ref-text>\n");
    	 			}
    	 			 
    	 			if(referenceitemcitationtitle!=null && referenceitemcitationtitle.length()>0)
    	 			{
    	 				file.write("<ce:citation-title>\n");
    	 				String[] refTitles = referenceitemcitationtitle.split(Constants.AUDELIMITER,-1);
    	 				for(int i=0;i<refTitles.length;i++)
    	 				{
    	 					String refTitle = refTitles[i];
    	 					if(refTitle!=null && refTitle.length()>0)
    	 					{
    	 						file.write("<titletext");
    	 						String[] refTitleElements = refTitle.split(Constants.IDDELIMITER,-1);
    	 						if(refTitleElements.length==4)
    	 						{
    	 							String id = refTitleElements[0];
    	 							String original = refTitleElements[1];
    	 							String lang = refTitleElements[2];
    	 							String titlecontent = refTitleElements[3];
    	 							if(original!=null && original.length()>0)
    	 							{
    	 								file.write(" original=\""+original+"\"");
    	 							}
    	 							
    	 							if(lang!=null && lang.length()>0)
    	 							{
    	 								file.write(" lang=\""+lang+"\"");
    	 							}
    	 							
    	 							file.write(">"+cleanBadCharacters(titlecontent)+"</titletext>\n");
    	 							
    	 						}
    	 						else
    	 						{
    	 							System.out.println("record "+accessnumber+" has wrong referenceitemcitationtitle format");
    	 						}
    	 					}
    	 					 
    	 				}
    	 				
    	 				file.write("</ce:citation-title>\n");
    	 			}
            	 			
    	 			//CODEN
    	 			if(referenceitemcitationcoden!=null && referenceitemcitationcoden.length()>0)
    	 			{
    	 				file.write("<codencode>"+referenceitemcitationcoden+"</codencode>\n");
    	 			}
    	 			
        	 		//if(referenceitemcitationauthor!=null && referenceitemcitationauthor.length()>0)
        	 		//{
        	 			
        	 		//}
    	 		
        	 		file.write("</refd-itemcitation>\n");
    	 		}
    	 		file.write("</reference>\n");
    	 	}            	           	 	
 		}         
    	catch(ArrayIndexOutOfBoundsException ea)
		{
			 //ea.printStackTrace();
		System.err.println(ea);
		System.out.println("EXCEPTION IN "+accessnumber);
		// System.exit(1);
		}
		catch(Exception e)
		{
			 e.printStackTrace();
		    //System.err.println(e);
		// System.exit(1);
		}
		finally
		{
		     if(rs != null)
		     {
		    	 try{
		    		 rs.close();
		    	 }
		    	 catch(Exception er)
		    	 {
		    		 er.printStackTrace();
		    	 }
		     }
		     
		     if(stmt != null)
		     {
		    	 try{
		    		 stmt.close();
		    	 }
		    	 catch(Exception es)
		    	 {
		    		 es.printStackTrace();
		    	 }
		     }
		}
    }
    
    public void add(Map authorGroup, String key, BdAuthor newValue) {
        List<BdAuthor> currentValue = (List)authorGroup.get(key);
        if (currentValue == null) {
            currentValue = new ArrayList<BdAuthor>();          
        }
        currentValue.add(newValue);
        authorGroup.put(key, currentValue);
    }

     public Connection getConnection(String connectionURL,
                                                 String driver,
                                                 String username,
                                                 String password)
                    throws Exception
    {
        Class.forName(driver);
        Connection con = DriverManager.getConnection(connectionURL,
                                          username,
                                          password);
        return con;
     }
    
     public void zipBatchFile(String filename,String database)
    		    throws Exception
    {
    	long starttime = System.currentTimeMillis();

    	if(database.equalsIgnoreCase("upa") || database.equalsIgnoreCase("eup") || database.equalsIgnoreCase("wop"))
    	{
    		database="upt";
    	}

    	String path="json/"+database+"/";
        
        byte[] buf = new byte[1024];
        
        //create zip file name
        String ZipFilename =  filename.replace("xml", "zip");
        File file = new File(filename);
        
        //long timediff = time - this.starttime;
       
        ZipOutputStream outZIP = new ZipOutputStream(new FileOutputStream(path+ZipFilename));
      
        FileInputStream in = new FileInputStream(filename);
        outZIP.putNextEntry(new ZipEntry(filename));
        int len;
        while ((len = in.read(buf)) > 0) {
            outZIP.write(buf, 0, len);
        }
        outZIP.closeEntry();
        in.close();
        file.delete();
                   
        outZIP.close();
     
        long endtime = System.currentTimeMillis();
        System.out.println("Time for Zipping="+(endtime-starttime));
    }
     
    public static String cleanBadCharacters(String input)
 	{
     	//System.out.println("INPUT3"+input);
     	Set<String> keys = badCharacterMap.keySet();
         for(String key: keys){
             String value = badCharacterMap.get(key);
             input = input.replaceAll(key, value);
         }
         //System.out.println("INPUT4"+input);
         input=Entity.unescapeHtml(input);
         input=input.replaceAll("&", "&amp;");
         input=input.replaceAll(">", "&gt;");
         input=input.replaceAll("<", "&lt;");
         input=input.replaceAll("<br/>"," ");

         return input;
     	
 	}
    
    private static Map<String,String> bdDocType = new HashMap<String, String>();
    static {
	    bdDocType.put("cp","cp");
	    bdDocType.put("ab","ab");
	    bdDocType.put("ar","ar");
	    bdDocType.put("bk","bk");
	    bdDocType.put("br","br");
	    bdDocType.put("bz","bz");
	    bdDocType.put("ca","cp");
	    bdDocType.put("ch","ch");  
	    bdDocType.put("cr","cr");
	    bdDocType.put("di","di");
	    bdDocType.put("ds","di");
	    bdDocType.put("ed","ed");
	    bdDocType.put("er","er");      
	    bdDocType.put("le","le");
	    bdDocType.put("mc","ch");
	    bdDocType.put("mr","br");
	    bdDocType.put("no","no");
	    bdDocType.put("pa","pa");
	    bdDocType.put("pr","pr");
	    bdDocType.put("re","re");
	    bdDocType.put("rp","rp");
	    bdDocType.put("sh","sh");
	    bdDocType.put("wp","wp");
	    bdDocType.put("ja","ar");
	    bdDocType.put("pa","pa");
	    bdDocType.put("ip","ip");
	    bdDocType.put("gi","ip");
	    bdDocType.put("st","st");
    }
     
     private static Map<String, String> badCharacterMap = new HashMap<String, String>();
     static {
         // &die is the same as an &uml
         badCharacterMap.put("a&die;", "&#228;");
         badCharacterMap.put("e&die;", "&#235;");
         badCharacterMap.put("i&die;", "&#239;");
         badCharacterMap.put("o&die;", "&#246;");
         badCharacterMap.put("u&die;", "&#252;");
         badCharacterMap.put("A&die;", "&#196;");
         badCharacterMap.put("E&die;", "&#203;");
         badCharacterMap.put("I&die;", "&#207;");
         badCharacterMap.put("O&die;", "&#214;");
         badCharacterMap.put("U&die;", "&#220;");
         badCharacterMap.put("A&grave;", "&#192;");
         badCharacterMap.put("A&acute;", "&#193;");
         badCharacterMap.put("A&circ;", "&#194;");
         badCharacterMap.put("A&tilde;", "&#195;");
         badCharacterMap.put("A&uml;", "&#196;");
         badCharacterMap.put("A&ring;", "&#197;");
         badCharacterMap.put("C&cedil;", "&#199;");
         badCharacterMap.put("E&grave;", "&#200;");
         badCharacterMap.put("E&acute;", "&#201;");
         badCharacterMap.put("E&circ;", "&#202;");
         badCharacterMap.put("E&uml;", "&#203;");
         badCharacterMap.put("I&grave;", "&#204;");
         badCharacterMap.put("I&acute;", "&#205;");
         badCharacterMap.put("I&circ;", "&#206;");
         badCharacterMap.put("I&uml;", "&#207;");
         badCharacterMap.put("N&tilde;", "&#209;");
         badCharacterMap.put("O&grave;", "&#210;");
         badCharacterMap.put("O&acute;", "&#211;");
         badCharacterMap.put("O&circ;", "&#212;");
         badCharacterMap.put("O&tilde;", "&#213;");
         badCharacterMap.put("O&uml;", "&#214;");
         badCharacterMap.put("O&slash;", "&#216;");
         badCharacterMap.put("S&caron;", "&#352;");
         badCharacterMap.put("U&grave;", "&#217;");
         badCharacterMap.put("U&acute;", "&#218;");
         badCharacterMap.put("U&circ;", "&#219;");
         badCharacterMap.put("U&uml;", "&#220;");
         badCharacterMap.put("Y&acute;", "&#221;");
         badCharacterMap.put("Y&uml;", "&#376;");
         badCharacterMap.put("a&grave;", "&#224;");
         badCharacterMap.put("a&acute;", "&#225;");
         badCharacterMap.put("a&circ;", "&#226;");
         badCharacterMap.put("a&tilde;", "&#227;");
         badCharacterMap.put("a&uml;", "&#228;");
         badCharacterMap.put("a&ring;", "&#229;");
         badCharacterMap.put("c&cedil;", "&#231;");
         badCharacterMap.put("e&grave;", "&#232;");
         badCharacterMap.put("e&acute;", "&#233;");
         badCharacterMap.put("e&circ;", "&#234;");
         badCharacterMap.put("e&uml;", "&#235;");
         badCharacterMap.put("i&grave;", "&#236;");
         badCharacterMap.put("i&acute;", "&#237;");
         badCharacterMap.put("i&circ;", "&#238;");
         badCharacterMap.put("i&uml;", "&#239;");
         badCharacterMap.put("n&tilde;", "&#241;");
         badCharacterMap.put("o&grave;", "&#242;");
         badCharacterMap.put("o&acute;", "&#243;");
         badCharacterMap.put("o&circ;", "&#244;");
         badCharacterMap.put("o&tilde;", "&#245;");
         badCharacterMap.put("o&uml;", "&#246;");
         badCharacterMap.put("o&slash;", "&#248;");
         badCharacterMap.put("s&caron;", "&#353;");
         badCharacterMap.put("u&grave;", "&#249;");
         badCharacterMap.put("u&acute;", "&#250;");
         badCharacterMap.put("u&circ;", "&#251;");
         badCharacterMap.put("u&uml;", "&#252;");
         badCharacterMap.put("y&acute;", "&#253;");
         badCharacterMap.put("y&uml;", "&#255;");
         badCharacterMap.put("&nbsp;","&#160;");
         badCharacterMap.put("&iexcl;","&#161;");
         badCharacterMap.put("&cent;","&#162;");
         badCharacterMap.put("&pound;","&#163;");
         badCharacterMap.put("&curren;","&#164;");
         badCharacterMap.put("&yen;","&#165;");
         badCharacterMap.put("&brvbar;","&#166;");
         badCharacterMap.put("&sect;","&#167;");
         badCharacterMap.put("&uml;","&#168;");
         badCharacterMap.put("&copy;","&#169;");
         badCharacterMap.put("&ordf;","&#170;");
         badCharacterMap.put("&laquo;","&#171;");
         badCharacterMap.put("&not;","&#172;");
         badCharacterMap.put("&shy;","&#173;");
         badCharacterMap.put("&reg;","&#174;");
         badCharacterMap.put("&macr;","&#175;");
         badCharacterMap.put("&deg;","&#176;");
         badCharacterMap.put("&plusmn;","&#177;");
         badCharacterMap.put("&sup2;","&#178;");
         badCharacterMap.put("&sup3;","&#179;");
         badCharacterMap.put("&acute;","&#180;");
         badCharacterMap.put("&micro;","&#181;");
         badCharacterMap.put("&para;","&#182;");
         badCharacterMap.put("&middot;","&#183;");
         badCharacterMap.put("&cedil;","&#184;");
         badCharacterMap.put("&sup1;","&#185;");
         badCharacterMap.put("&ordm;","&#186;");
         badCharacterMap.put("&raquo;","&#187;");
         badCharacterMap.put("&frac14;","&#188;");
         badCharacterMap.put("&frac12;","&#189;");
         badCharacterMap.put("&frac34;","&#190;");
         badCharacterMap.put("&iquest;","&#191;");
         badCharacterMap.put("&Agrave;","&#192;");
         badCharacterMap.put("&Aacute;","&#193;");
         badCharacterMap.put("&Acirc;","&#194;");
         badCharacterMap.put("&Atilde;","&#195;");
         badCharacterMap.put("&Auml;","&#196;");
         badCharacterMap.put("&Aring;","&#197;");
         badCharacterMap.put("&AElig;","&#198;");
         badCharacterMap.put("&Ccedil;","&#199;");
         badCharacterMap.put("&Egrave;","&#200;");
         badCharacterMap.put("&Eacute;","&#201;");
         badCharacterMap.put("&Ecirc;","&#202;");
         badCharacterMap.put("&Euml;","&#203;");
         badCharacterMap.put("&Igrave;","&#204;");
         badCharacterMap.put("&Iacute;","&#205;");
         badCharacterMap.put("&Icirc;","&#206;");
         badCharacterMap.put("&Iuml;","&#207;");
         badCharacterMap.put("&ETH;","&#208;");
         badCharacterMap.put("&Ntilde;","&#209;");
         badCharacterMap.put("&Ograve;","&#210;");
         badCharacterMap.put("&Oacute;","&#211;");
         badCharacterMap.put("&Ocirc;","&#212;");
         badCharacterMap.put("&Otilde;","&#213;");
         badCharacterMap.put("&Ouml;","&#214;");
         badCharacterMap.put("&times;","&#215;");
         badCharacterMap.put("&Oslash;","&#216;");
         badCharacterMap.put("&Ugrave;","&#217;");
         badCharacterMap.put("&Uacute;","&#218;");
         badCharacterMap.put("&Ucirc;","&#219;");
         badCharacterMap.put("&Uuml;","&#220;");
         badCharacterMap.put("&Yacute;","&#221;");
         badCharacterMap.put("&THORN;","&#222;");
         badCharacterMap.put("&szlig;","&#223;");
         badCharacterMap.put("&agrave;","&#224;");
         badCharacterMap.put("&aacute;","&#225;");
         badCharacterMap.put("&acirc;","&#226;");
         badCharacterMap.put("&atilde;","&#227;");
         badCharacterMap.put("&auml;","&#228;");
         badCharacterMap.put("&aring;","&#229;");
         badCharacterMap.put("&aelig;","&#230;");
         badCharacterMap.put("&ccedil;","&#231;");
         badCharacterMap.put("&egrave;","&#232;");
         badCharacterMap.put("&eacute;","&#233;");
         badCharacterMap.put("&ecirc;","&#234;");
         badCharacterMap.put("&euml;","&#235;");
         badCharacterMap.put("&igrave;","&#236;");
         badCharacterMap.put("&iacute;","&#237;");
         badCharacterMap.put("&icirc;","&#238;");
         badCharacterMap.put("&iuml;","&#239;");
         badCharacterMap.put("&eth;","&#240;");
         badCharacterMap.put("&ntilde;","&#241;");
         badCharacterMap.put("&ograve;","&#242;");
         badCharacterMap.put("&oacute;","&#243;");
         badCharacterMap.put("&ocirc;","&#244;");
         badCharacterMap.put("&otilde;","&#245;");
         badCharacterMap.put("&ouml;","&#246;");
         badCharacterMap.put("&divide;","&#247;");
         badCharacterMap.put("&oslash;","&#248;");
         badCharacterMap.put("&ugrave;","&#249;");
         badCharacterMap.put("&uacute;","&#250;");
         badCharacterMap.put("&ucirc;","&#251;");
         badCharacterMap.put("&uuml;","&#252;");
         badCharacterMap.put("&yacute;","&#253;");
         badCharacterMap.put("&thorn;","&#254;");
         badCharacterMap.put("&yuml;","&#255;");
         badCharacterMap.put("&ndash;","&#8211;");
         badCharacterMap.put("&rsquo;","&#146;");
         badCharacterMap.put("&lsquo;","&#145;");
         badCharacterMap.put("&mdash;","&#151;");
         badCharacterMap.put("&mellip;","&#8943;");
         badCharacterMap.put("&ldquo;","&#8220;");
         badCharacterMap.put("&rdquo;","&#8221;");
         
   
         
       
        
         
     }

	    
}
