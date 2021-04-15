package org.ei.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.io.*;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSetMetaData;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.net.*;
import javax.net.ssl.HttpsURLConnection;
import java.util.regex.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.Map.Entry;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder.*;
import javax.xml.parsers.*;
import javax.xml.validation.SchemaFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.transform.*;
import javax.xml.validation.*;
import javax.xml.transform.dom.*;

import org.ei.dataloading.inspec.loadtime.*;
import org.ei.dataloading.cafe.*;
import org.ei.domain.*;
import org.ei.query.base.*;
import org.ei.dataloading.cafe.*;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.MatchResult;
import org.ei.xml.Entity;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
//import io.searchbox.core.QueryBuilder;
import io.searchbox.core.SearchResult.Hit; 
import com.google.gson.JsonParser;
//import com.google.gson.JsonObject;
import com.google.gson.JsonElement;
import com.fasterxml.jackson.databind.JsonNode;

import org.ei.dataloading.DataLoadDictionary;
import org.ei.dataloading.bd.loadtime.*;
import org.ei.dataloading.CombinedXMLWriter;
import org.ei.common.bd.*;
import org.ei.common.*;
import org.ei.dataloading.awss3.AmazonS3Service;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.event.ProgressEvent;
import com.amazonaws.event.ProgressListener;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.transfer.MultipleFileUpload;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.AmazonSQSException;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.amazonaws.services.sqs.model.ListQueuesResult;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.DeleteQueueRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import org.apache.commons.text.StringEscapeUtils;

import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryBuilders.*;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.CloseableHttpClient;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.XML;
//import org.json.JSONArray;
//import org.json.XML;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.json.*;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import org.ei.util.kafka.*;
import org.ei.dataloading.awss3.AmazonS3Service;
import org.ei.dataloading.awss3.UploadFileToS3;
import org.ei.dataloading.awss3.UploadFileToS3Thread;


public class NewDataTesting
{
	
	//public static String URL="jdbc:oracle:thin:@127.0.0.1:5523:EIA";
	public static String URL="jdbc:oracle:thin:@eid.cmdvszxph9cf.us-east-1.rds.amazonaws.com:1521:eid";
	public static String driver="oracle.jdbc.driver.OracleDriver";
	public static String username="ap_correction";
	public static String username1="ap_ev_search";
	public static String password="ei3it";
	public static String password1="";
    public static String tableName="bd_master";
    public static String tableName1="bd_master_jupiter";
    public static String database="cpx";
    public static String updateNumber="0";
    public static String action="bd_loading";
    public static String server="prod";

	public static void main(String args[]) throws Exception
	{
		NewDataTesting test = new NewDataTesting();
		long startTime = System.currentTimeMillis();
	    long endTime = System.currentTimeMillis();
		if(args.length>8)
		{
			test.database = args[0];
			System.out.println("DATABASE:: "+test.database);
			test.username = args[1];
			System.out.println("USERNAME:: "+test.username);
			test.username1 = args[2];
			System.out.println("USERNAME1:: "+test.username1);
			test.password = args[3];
			System.out.println("PASSWORD:: "+test.password);
			test.password1 = args[4];
			System.out.println("PASSWORD1:: "+test.password1);
			test.tableName = args[5];
			System.out.println("TABLENAME:: "+test.tableName);
			test.tableName1 = args[6];
			System.out.println("TABLENAME1:: "+test.tableName1);
			test.URL=args[7];
			System.out.println("URL:: "+test.URL);
			test.updateNumber = args[8];
			System.out.println("UPDATENUMBER:: "+test.updateNumber);
			test.action = args[9];
			System.out.println("ACTION:: "+test.action);

		}
	    else if(args.length>5)
	    {
			test.database = args[0];
			System.out.println("DATABASE:: "+test.database);
			test.username = test.username1 = args[1];
			System.out.println("USERNAME:: "+test.username);
			test.password = test.password1 = args[2];
			System.out.println("PASSWORD:: "+test.password);
			test.tableName  =args[3];
			System.out.println("TABLENAME:: "+test.tableName);
			test.tableName1 =args[4];
			System.out.println("TABLENAME:: "+test.tableName1);
			test.updateNumber = args[5];
			System.out.println("UPDATENUMBER:: "+test.updateNumber);
			if(args.length>6)
			{
				test.action = args[6];
				System.out.println("ACTION:: "+test.action);
			}
			if(args.length>7)
			{
				test.server = args[7];
				System.out.println("ACTION:: "+test.server);
			}

		}
		else if(args.length>2)
		{
			test.database = args[0];
			System.out.println("DATABASE:: "+test.database);
			test.action = args[1];
			System.out.println("ACTION:: "+test.action);
			test.updateNumber = args[2];
			System.out.println("LoadNumber:: "+test.updateNumber);
		}
		else if(args.length>1)
		{
			test.database = args[0];
			System.out.println("DATABASE:: "+test.database);
			test.action = args[1];
			System.out.println("ACTION:: "+test.action);
		}
		else
		{
			System.out.println("not enough parameters");
			System.out.println("please enter org.ei.util.NewDataTesting databse username password tablename updatenumber");
		}

		if(action.contentEquals("checkSize"))
		{
			test.checkColumnSize();
		}
		else if(action.equals("testES"))
		{
			System.out.println("doing testing Elastic Search Index");
			test.testURL(updateNumber);
		}		
		else if(action.equals("deleteElasticRecord"))
		{
			System.out.println("sending records to Kafka for deleting");
			test.deleteESRecord(database);
		}
		else if(action.equals("testKafkaConsumer"))
		{
			test.testKafkaConsumer(test.tableName);
		}
		else if(action.equals("fast"))
		{
			test.checkFast(test.tableName,test.tableName1);
		}
		else if(action.equals("loadnumber"))
		{
			test.checkLoadNumber(database);
		}
		else if(action.equals("detail"))
		{
			test.checkDetailRecord(database,updateNumber);
		}
		else if(action.equals("invalidYearData"))
		{
			test.getInvalidYearData(database);
		}
		else if(action.equals("yearCount"))
		{
			test.getYearCountFromFast(updateNumber,database);
		}
		else if(action.equals("weeklyCount"))
		{
			test.getWeeklyCount(updateNumber);
		}
		else  if(action.equals("checkFastCount"))
		{
			test.getMIDFromFast(updateNumber);
		}
		else  if(action.equals("cpc"))
		{
			test.getCPCDescription(updateNumber);
		}
		else  if(action.equals("testElasticSearch"))
		{
			test.testBuildElasticSearch();
		}
		else  if(action.equals("testSearchElasticSearch"))
		{
			test.testSearchElasticSearch();
		}
		else  if(action.equals("inspecNIParsing"))
		{
			test.convertInspecNI(updateNumber);
		}
		else  if(action.equals("xml"))
		{
			test.validatedXml(updateNumber);
		}
		else  if(action.equals("buildxml"))
		{
			test.buildXMLFromDATABASE(updateNumber);
		}
		else  if(action.equals("readFromUrl"))
		{
			test.urlReader();
		}
		else  if(action.equals("dedup"))
		{
			test.buildDedupkeyTable(updateNumber);
		}
		else  if(action.equals("bdmapping"))
		{
			test.buildMappingFromBD(updateNumber);
		}
		else  if(action.equals("testmapping"))
		{
			HashMap map = test.testMapping();
		}
		else  if(action.equals("allfastmid"))
		{
			test.getALLCPXMIDFromFast(database);
		}
		else  if(action.equals("cpxMID"))
		{
			test.checkFASTFORMID(updateNumber);
		}
		else  if(action.equals("remove"))
		{
			test.removeInvalidChar(updateNumber);
		}
		else  if(action.equals("fastQueryDR"))
		{
			test.getMIDFromFastQuery_DR(updateNumber);
		}
		else  if(action.equals("fastQueryDEV"))
		{
			test.getMIDFromFastQuery_DEV(updateNumber);
		}
		else  if(action.equals("fastQuery"))
		{
			test.getMIDFromFastQuery(updateNumber);
		}
		else  if(action.equals("checkCoor"))
		{
			test.checkGrfCoordinates();
		}
		else  if(action.equals("allFastCount"))
		{
			test.getAllFastCount(updateNumber,database);
		}
		else  if(action.equals("checkFastDr_PROD"))
		{
			test.checkMidDR_PROD(updateNumber,database);
		}
		else  if(action.equals("doFastExtract"))
		{
			test.doFastExtract(updateNumber,database);
		}
		else  if(action.equals("compareDRwithPROD"))
		{
			test.compareMidDRPROD(updateNumber,database);
		}
		else  if(action.equals("allCpxIP_DR"))
		{
			test.getAllMIDFromFastCPXIP_DR();
		}
		else  if(action.equals("allCpxIP_PROD"))
		{
			test.getAllMIDFromFastCPXIP_PROD();
		}
		else  if(action.equals("getS3"))
		{
			test.testGetAWSS3(updateNumber);
		}
		else  if(action.equals("putS3"))
		{
			test.testPutAWSS3(updateNumber);
		}
		else  if(action.equals("addSqs"))
		{
			test.testAddSqs();
		}
		else  if(action.equals("listSqs"))
		{
			test.testListSqs();
		}		
		else  if(action.equals("getSqs"))
		{
			test.testGetSqs(updateNumber);
		}
		else  if(action.equals("runSqs"))
		{
			test.fullTestRunSQS();
		}		
		else  if(action.equals("getSqsMessage"))
		{
			test.getSqsMessage(updateNumber);
		}
		else  if(action.equals("testBuildAuthorESDoc"))
		{
			test.testBuildAuthorESDoc(updateNumber);
		}
		else  if(action.equals("readJson"))
		{
			test.parseJsonObject(updateNumber);
		}
		else  if(action.equals("getCITCOUNT"))
		{
			test.getCITCountFromFast(updateNumber);
		}
		else  if(action.equals("getAUTHORCOUNT"))
		{
			test.getAuthorCountFromFast(updateNumber);
		}
		else  if(action.equals("getAFCOUNT"))
		{
			test.getAFCountFromFast(updateNumber);
		}
		else  if(action.equals("getallcodes"))
		{
			test.getAllCodes();
		}
		else  if(action.equals("getallIndexes"))
		{
			test.getAllIndexes(updateNumber);
		}
		else  if(action.equals("checkChar"))
		{
			test.checkCharacter();
		}
		else  if(action.equals("displayStringCharValue"))
		{
			test.checkStringCharacter(updateNumber);
		}		
		else  if(action.equals("xmltojson"))
		{
			test.xmlToJson(updateNumber);
		}
		else if(action.contentEquals("checkes"))
		{
			test.checkES(updateNumber, tableName1, database, server);
		}
		else if(action.contentEquals("getesmid"))
		{
			test.getESMID(updateNumber, tableName1, database, server);
		}		
		else if(action.contentEquals("comparedatabasees"))
		{
			test.runDatabaseTOES(updateNumber, tableName1, database, server, tableName);
		}
		else
		{
			System.out.println("we dont know your input "+action);
			System.exit(1);
		}
		endTime = System.currentTimeMillis();
		System.out.println("total Time used "+(endTime-startTime)/1000.0+" seconds");

	}
	
	
	public void xmlToJson(String filename) {
		
		//String xml_data = "<student><name>Neeraj Mishra</name><age>22</age></student>";
		BufferedReader in = null;
		String line=null;
		try{
			String database="cpx";
			in = new BufferedReader(new FileReader(new File(filename)));
			StringBuffer xmlbuffer = new StringBuffer();
						         
			while((line=in.readLine())!=null)
			{
				if(line.indexOf("QQDelQQ")>0)
				{
					line = line.replaceAll("\\[QQDelQQ ", "\\[");
					line = line.replaceAll(" QQDelQQ", ";");									
				}
				
				if(line.indexOf("QstemQ")>0)
				{
					//System.out.println("line1="+line);
					//line = line.replaceAll("QstemQ\\w.\\]","");
						
					line = line.substring(0,line.indexOf("QstemQ"))+line.substring(line.indexOf("]]><"));
					line = line.replaceAll(" QstemQ ", "");
					//System.out.println("line2="+line);
				}
				xmlbuffer.append(line);
				
			}
			//converting xml to json
			org.json.JSONObject obj = XML.toJSONObject(xmlbuffer.toString());
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
	        JsonParser jp = new JsonParser();
	        	//JsonElement je = jp.parse(evo.build().toString());
	        JsonElement je = jp.parse(obj.toString());
	        String prettyJsonString = gson.toJson(je);
			System.out.println(prettyJsonString);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (in != null) {
				try {
					in.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
		
	}
	
	 private void doFastExtract(String updateNumber,String dbname) throws Exception
	 {
	        CombinedXMLWriter writer = new CombinedXMLWriter(50000,
	                                                      Integer.parseInt(updateNumber),
	                                                      dbname,
	                                                      "dev");
	        Connection con = null;
	        Statement stmt = null;
	        ResultSet rs = null;
	        try
	        {
	        	con = getConnection(this.URL,this.driver,this.username,this.password);
	            stmt = con.createStatement();
	            System.out.println("updatenumber= "+updateNumber+" dbname= "+dbname);
                System.out.println("Running the query...");
                writer.setOperation("add");
                XmlCombiner c = new XmlCombiner(writer);
                String sqlQuery = null;
                if(dbname.equals("cpx"))
                {
                	if(!updateNumber.equals("1"))
                	{
                		sqlQuery="select a.CHEMICALTERM,a.SPECIESTERM,a.REGIONALTERM,a.DATABASE,a.CITATIONLANGUAGE,a.CITATIONTITLE,a.CITTYPE,a.ABSTRACTDATA,a.PII,a.PUI,a.COPYRIGHT,a.M_ID,a.accessnumber,a.datesort,a.author,a.author_1,a.AFFILIATION,a.AFFILIATION_1,a.CORRESPONDENCEAFFILIATION,a.CODEN,a.ISSUE,a.CLASSIFICATIONCODE,a.CLASSIFICATIONDESC,a.CONTROLLEDTERM,a.UNCONTROLLEDTERM,a.MAINHEADING,a.TREATMENTCODE,a.LOADNUMBER,a.SOURCETYPE,a.SOURCECOUNTRY,a.SOURCEID,a.SOURCETITLE,a.SOURCETITLEABBREV,a.ISSUETITLE,a.ISSN,a.EISSN,a.ISBN,a.VOLUME,a.PAGE,a.PAGECOUNT,a.ARTICLENUMBER, substr(a.PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,a.PUBLICATIONDATE,a.EDITORS,a.PUBLISHERNAME,a.PUBLISHERADDRESS,a.PUBLISHERELECTRONICADDRESS,a.REPORTNUMBER,a.CONFNAME, a.CONFCATNUMBER,a.CONFCODE,a.CONFLOCATION,a.CONFDATE,a.CONFSPONSORS,a.CONFERENCEPARTNUMBER, a.CONFERENCEPAGERANGE, a.CONFERENCEPAGECOUNT, a.CONFERENCEEDITOR, a.CONFERENCEORGANIZATION,a.CONFERENCEEDITORADDRESS,a.TRANSLATEDSOURCETITLE,a.VOLUMETITLE,a.DOI,a.ASSIG,a.CASREGISTRYNUMBER,a.APILT,a.APILT1,a.APICT,a.APICT1,a.APIAMS,a.SEQ_NUM,a.GRANTLIST,b.author as cafe_author,b.author_1 as cafe_author1,b.affiliation as cafe_affiliation,b.affiliation_1 as cafe_affiliation1,b.CORRESPONDENCEAFFILIATION as CAFE_CORRESPONDENCEAFFILIATION,null as authorid,null as affid,a.SOURCEBIBTEXT,a.STANDARDID,a.STANDARDDESIGNATION,a.NORMSTANDARDID,a.GRANTTEXT from bd_master_orig a left outer join cafe_master b on a.pui = b.pui where a.database='cpx' and a.updateNumber='"+updateNumber+"'";
                	}
                	else
                	{
                		sqlQuery="select a.CHEMICALTERM,a.SPECIESTERM,a.REGIONALTERM,a.DATABASE,a.CITATIONLANGUAGE,a.CITATIONTITLE,a.CITTYPE,a.ABSTRACTDATA,a.PII,a.PUI,a.COPYRIGHT,a.M_ID,a.accessnumber,a.datesort,a.author,a.author_1,a.AFFILIATION,a.AFFILIATION_1,a.CORRESPONDENCEAFFILIATION,a.CODEN,a.ISSUE,a.CLASSIFICATIONCODE,a.CLASSIFICATIONDESC,a.CONTROLLEDTERM,a.UNCONTROLLEDTERM,a.MAINHEADING,a.TREATMENTCODE,a.LOADNUMBER,a.SOURCETYPE,a.SOURCECOUNTRY,a.SOURCEID,a.SOURCETITLE,a.SOURCETITLEABBREV,a.ISSUETITLE,a.ISSN,a.EISSN,a.ISBN,a.VOLUME,a.PAGE,a.PAGECOUNT,a.ARTICLENUMBER, substr(a.PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,a.PUBLICATIONDATE,a.EDITORS,a.PUBLISHERNAME,a.PUBLISHERADDRESS,a.PUBLISHERELECTRONICADDRESS,a.REPORTNUMBER,a.CONFNAME, a.CONFCATNUMBER,a.CONFCODE,a.CONFLOCATION,a.CONFDATE,a.CONFSPONSORS,a.CONFERENCEPARTNUMBER, a.CONFERENCEPAGERANGE, a.CONFERENCEPAGECOUNT, a.CONFERENCEEDITOR, a.CONFERENCEORGANIZATION,a.CONFERENCEEDITORADDRESS,a.TRANSLATEDSOURCETITLE,a.VOLUMETITLE,a.DOI,a.ASSIG,a.CASREGISTRYNUMBER,a.APILT,a.APILT1,a.APICT,a.APICT1,a.APIAMS,a.SEQ_NUM,a.GRANTLIST,b.author as cafe_author,b.author_1 as cafe_author1,b.affiliation as cafe_affiliation,b.affiliation_1 as cafe_affiliation1,b.CORRESPONDENCEAFFILIATION as CAFE_CORRESPONDENCEAFFILIATION,null as authorid,null as affid,a.SOURCEBIBTEXT,a.STANDARDID,a.STANDARDDESIGNATION,a.NORMSTANDARDID,a.GRANTTEXT from bd_master_orig a left outer join cafe_master b on a.pui = b.pui where a.database='cpx'";
                		//sqlQuery="select CHEMICALTERM,SPECIESTERM,REGIONALTERM,DATABASE,CITATIONLANGUAGE,CITATIONTITLE,CITTYPE,ABSTRACTDATA,PII,PUI,COPYRIGHT,M_ID,accessnumber,datesort,author,author_1,AFFILIATION,AFFILIATION_1,CORRESPONDENCEAFFILIATION,CODEN,ISSUE,CLASSIFICATIONCODE,CLASSIFICATIONDESC,CONTROLLEDTERM,UNCONTROLLEDTERM,MAINHEADING,TREATMENTCODE,LOADNUMBER,SOURCETYPE,SOURCECOUNTRY,SOURCEID,SOURCETITLE,SOURCETITLEABBREV,ISSUETITLE,ISSN,EISSN,ISBN,VOLUME,PAGE,PAGECOUNT,ARTICLENUMBER, substr(PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,PUBLICATIONDATE,EDITORS,PUBLISHERNAME,PUBLISHERADDRESS,PUBLISHERELECTRONICADDRESS,REPORTNUMBER,CONFNAME, CONFCATNUMBER,CONFCODE,CONFLOCATION,CONFDATE,CONFSPONSORS,CONFERENCEPARTNUMBER, CONFERENCEPAGERANGE, CONFERENCEPAGECOUNT, CONFERENCEEDITOR, CONFERENCEORGANIZATION,CONFERENCEEDITORADDRESS,TRANSLATEDSOURCETITLE,VOLUMETITLE,DOI,ASSIG,CASREGISTRYNUMBER,APILT,APILT1,APICT,APICT1,APIAMS,SEQ_NUM,GRANTLIST,null as cafe_author,null as cafe_author1,null as cafe_affiliation,null as cafe_affiliation1,null as CAFE_CORRESPONDENCEAFFILIATION,null as authorid,null as affid ,SOURCEBIBTEXT,STANDARDID,STANDARDDESIGNATION,NORMSTANDARDID,GRANTTEXT from cafe_master where pui in (select pui from hmo_cafemaster_minus_aulookup)";
                	}
                }
                else
                {
                	sqlQuery = "select CHEMICALTERM,SPECIESTERM,REGIONALTERM,DATABASE,CITATIONLANGUAGE,CITATIONTITLE,CITTYPE,ABSTRACTDATA,PII,PUI,COPYRIGHT,M_ID,accessnumber,datesort,author,author_1,AFFILIATION,AFFILIATION_1,CORRESPONDENCEAFFILIATION,CODEN,ISSUE,CLASSIFICATIONCODE,CLASSIFICATIONDESC,CONTROLLEDTERM,UNCONTROLLEDTERM,MAINHEADING,TREATMENTCODE,LOADNUMBER,SOURCETYPE,SOURCECOUNTRY,SOURCEID,SOURCETITLE,SOURCETITLEABBREV,ISSUETITLE,ISSN,EISSN,ISBN,VOLUME,PAGE,PAGECOUNT,ARTICLENUMBER, substr(PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,PUBLICATIONDATE,EDITORS,PUBLISHERNAME,PUBLISHERADDRESS,PUBLISHERELECTRONICADDRESS,REPORTNUMBER,CONFNAME, CONFCATNUMBER,CONFCODE,CONFLOCATION,CONFDATE,CONFSPONSORS,CONFERENCEPARTNUMBER, CONFERENCEPAGERANGE, CONFERENCEPAGECOUNT, CONFERENCEEDITOR, CONFERENCEORGANIZATION,CONFERENCEEDITORADDRESS,TRANSLATEDSOURCETITLE,VOLUMETITLE,DOI,ASSIG,CASREGISTRYNUMBER,APILT,APILT1,APICT,APICT1,APIAMS,SEQ_NUM,GRANTLIST,null as cafe_author,b.null as cafe_author1,null as cafe_affiliation,null as cafe_affiliation1,null as CAFE_CORRESPONDENCEAFFILIATION,null as authorid,null as affid,SOURCEBIBTEXT,STANDARDID,STANDARDDESIGNATION,NORMSTANDARDID,GRANTTEXT from bd_master_orig where database='"+dbname+"' and updateNumber='"+updateNumber+"'";
                }
                System.out.println("SQLQUERY= "+sqlQuery);
                rs = stmt.executeQuery(sqlQuery);                
                c.writeRecs(rs,con, Integer.parseInt(updateNumber),"bd_master","cpx");
	            
	           
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

	 private void deleteESRecord(String infile) throws Exception
	 {
		//KafkaService kafka = new KafkaService();
		long processTime = System.currentTimeMillis();
		String propertyFileName="lib/config.properties";
		KafkaService kafka = new KafkaService(processTime+"delete"+infile, propertyFileName);
		BufferedReader deleteFileStream;
		List deleteList = new ArrayList();
		List databaseList =  new ArrayList<String>();
		databaseList.add("cpx_");
		databaseList.add("ins_");
		databaseList.add("upt_");
		databaseList.add("grf_");
		databaseList.add("pch_");
		databaseList.add("geo_");
		databaseList.add("chm_");
		databaseList.add("elt_");
		databaseList.add("cbn_");
		databaseList.add("ntis");
		databaseList.add("knc_");
		databaseList.add("kna_");
		databaseList.add("ept_");
		databaseList.add("c84_");
		databaseList.add("ibf_");
		
		String eid=null;
        try
        {
        	 if(infile.toLowerCase().endsWith(".zip"))
             {
                 System.out.println("IS ZIP FILE");
                 ZipFile zipFile = new ZipFile(infile);
                 Enumeration entries = zipFile.entries();
                 while (entries.hasMoreElements())
                 {
                     ZipEntry entry = (ZipEntry)entries.nextElement();
                     deleteFileStream = new BufferedReader(new InputStreamReader(zipFile.getInputStream(entry), "UTF-8"));
                     while((eid=deleteFileStream.readLine())!=null)
                     {                   	 
                         if(eid != null && eid.length()>4 && databaseList.contains(eid.substring(0,4)))
                         {
                        	 System.out.println("EID="+eid);
                            // kafka.runProducer("{}","\""+eid.trim()+"\"",0,new HashMap());
                        	 kafka.runProducer("{}",eid.trim(),0,new HashMap());
                            
                         }
                         else
                         {
                        	 System.out.println("m_id "+eid+" is invalid");
                         }
                     }
                 }
             }
             else if(infile.toLowerCase().endsWith(".txt"))
             {
                 System.out.println("IS TEXT FILE");
                 //in = new BufferedReader(new FileReader(infile));//new InputStreamReader(is, "UTF-8"));
                 File file = new File(infile);
                 deleteFileStream = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-8"));
                 while((eid=deleteFileStream.readLine())!=null)
                 {                   	 
                	 if(eid != null && eid.length()>4 && databaseList.contains(eid.substring(0,4)))
                     {
                    	 System.out.println("EID="+eid);
                         //kafka.runProducer("{}","\""+eid+"\"",0,new HashMap());
                         kafka.runProducer("{}",eid,0,new HashMap());
                     }
                     else
                     {
                    	 System.out.println("m_id "+eid+" is invalid");
                     }
                 }
             }        
           
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
	 
	 private void testKafkaConsumer(String infile) throws Exception
	 {
		//KafkaService kafka = new KafkaService();
		long processTime = System.currentTimeMillis();
		String propertyFileName="lib/config.properties";
		KafkaService kafka = new KafkaService(processTime+"delete"+infile, propertyFileName);
		BufferedReader deleteFileStream;
		
		
		String eid=null;
        try
        {
        	
           
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
	
	public void getAllFastCount(String filename,String database)
	{
		FileWriter out = null;
		BufferedReader in = null;
		try{
			//String database="ins";
			in = new BufferedReader(new FileReader(new File(filename)));
			String weekNumber=null;
		
			out = new FileWriter("FastCountBYLoadnumber.out");
						         
			while((weekNumber=in.readLine())!=null)
			{
				String fastCount = getFastCount(database,weekNumber);
				Thread.sleep(1000);
				out.write(weekNumber+"\t"+fastCount+"\n");
				out.flush();
			}
			out.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (in != null) {
				try {
					in.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (out != null) {
				try {
					out.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}
		
	}
	
	 private void sendDeleteToKafka(List rs)
    {
    	KafkaService kafka = new KafkaService();
    	try
    	{
	    	String eid="";
	    	for (int i=0;i<rs.size();i++)
            {
	    		eid=(String)rs.get(i);
                if(eid != null)
                {                   
                    kafka.runProducer("{}","\""+eid+"\"",0,new HashMap());
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
	
	private void compareMidDRPROD(String query,String database)
	{
		FileWriter out = null;
		try
		{
			String searchQuery = query.replaceAll("_", " ");
			String dr_url = "http://evdr09.cloudapp.net:15100"; //DR
			String prod_url = "http://evprod14.cloudapp.net:15100"; //Production
			//out = new FileWriter("DR_"+loadnumber+".txt");
			List listFronDR = getALLCPXMIDFromFastBYSeachQuery(query,dr_url);
			out = new FileWriter("PROD_"+query+".txt");
			//List listFronDR = getALLCPXMIDFromFastBYSeachQuery(query,prod_url);
			long startTime = System.currentTimeMillis();
			if(listFronDR!=null && listFronDR.size()>0)
			{
				for(int i=0;i<listFronDR.size();i++)
				{
					System.out.print(i+",");
					if(i%1000==0)
					{
						long midTime = System.currentTimeMillis();
						System.out.println("time used "+(midTime-startTime));
						startTime = midTime;
					}
					String mid = (String)listFronDR.get(i);
					if(!checkMIDFromFast(mid,prod_url))
					{
						System.out.println(mid);
						out.write(mid+"\n");
					}
				}
			}
			out.flush();
			out.close();
			
		}
		catch(Exception e)
		{
			try{
			if(out!=null)
				out.close();
			}
			catch(Exception e1)
			{
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}
	
	private void checkMidDR_PROD(String loadnumber,String database)
	{
		FileWriter out = null;
		try
		{
			
			String dr_url = "http://evdr09.cloudapp.net:15100"; //DR
			String prod_url = "http://evazure.trafficmanager.net:15100"; //Production
			//out = new FileWriter("DR_"+loadnumber+".txt");
			//List listFronDR = getALLCPXMIDFromFastBYLOADNUMBER(loadnumber,dr_url);
			out = new FileWriter("PROD_"+loadnumber+".txt");
			List listFronDR = getALLCPXMIDFromFastBYLOADNUMBER(loadnumber,prod_url);
			
			if(listFronDR!=null && listFronDR.size()>0)
			{
				for(int i=0;i<listFronDR.size();i++)
				{
					String mid = (String)listFronDR.get(i);
					//if(!checkMIDFromFast(mid,loadnumber,prod_url))
					{
						out.write(mid+"\n");
					}
				}
			}
			out.flush();
			out.close();
			
		}
		catch(Exception e)
		{
			try{
			if(out!=null)
				out.close();
			}
			catch(Exception e1)
			{
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}
	
	private boolean checkMIDFromFast(String mid, String URL)
	{
		int count= 0;
		try
		{						
			FastClient client = new FastClient();
			//client.setBaseURL("http://evprod08.cloudapp.net:15100");
			client.setBaseURL(URL);
			client.setResultView("ei");
			client.setOffSet(0);
			client.setPageSize(500000);
			client.setQueryString(mid);
			client.setDoCatCount(true);
			client.setDoNavigators(true);
			client.setPrimarySort("ausort");
			client.setPrimarySortDirection("+");
			client.search();
			List l = client.getDocIDs();
			count =client.getHitCount();
			//System.out.println(mid+"\t"+count);
			

		}
		catch(Exception e)
		{		
			e.printStackTrace();
		}
		if(count<1)
		{
			return false;
	    }
		else
		{
			return true;
		}
		

	}
	
	private List getALLCPXMIDFromFastBYSeachQuery(String query, String URL)
	{

		FileWriter out = null;
		List<String> resultList = new ArrayList<String>();
		try
		{
			//out = new FileWriter(loadnumber+".txt");
							
			FastClient client = new FastClient();
			//client.setBaseURL("http://evprod08.cloudapp.net:15100");
			client.setBaseURL(URL);
			client.setResultView("ei");
			client.setOffSet(0);
			client.setPageSize(500000);
			client.setQueryString(query.replaceAll("_", " and "));
			client.setDoCatCount(true);
			client.setDoNavigators(true);
			client.setPrimarySort("ausort");
			client.setPrimarySortDirection("+");
			client.search();

			List l = client.getDocIDs();
			int count =client.getHitCount();

			if(count<1)
			{
			  System.out.println("No records found in fast");
			  //System.out.println("DATABASE="+database+" LOADNUMBER= "+loadnumber);
		    }
			else
			{
				System.out.println("COUNT="+count);
			}

			StringBuffer sb=new StringBuffer();
			
			for(int i=0;i<l.size();i++)
			{
				String[] docID = (String[])l.get(i);				
				
				//System.out.println("docID="+docID[0]);
				resultList.add(docID[0]);
				//out.write(docID[0]+"\n");
				//Thread.sleep(5);
				//out.flush();
				
			}		
			//out.flush();
			//out.close();

		}
		catch(Exception e)
		{
			try{
			if(out!=null)
				out.close();
			}
			catch(Exception e1)
			{
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		return resultList;

	}
	
	private List getALLCPXMIDFromFastBYLOADNUMBER(String loadnumber, String URL)
	{

		FileWriter out = null;
		List<String> resultList = new ArrayList<String>();
		try
		{
			//out = new FileWriter(loadnumber+".txt");
							
			FastClient client = new FastClient();
			//client.setBaseURL("http://evprod08.cloudapp.net:15100");
			client.setBaseURL(URL);
			client.setResultView("ei");
			client.setOffSet(0);
			client.setPageSize(500000);
			client.setQueryString("db:"+database+" and wk:"+loadnumber);
			client.setDoCatCount(true);
			client.setDoNavigators(true);
			client.setPrimarySort("ausort");
			client.setPrimarySortDirection("+");
			client.search();

			List l = client.getDocIDs();
			int count =client.getHitCount();

			if(count<1)
			{
			  System.out.println("No records found in fast");
			  System.out.println("DATABASE="+database+" LOADNUMBER= "+loadnumber);
		    }
			else
			{
				System.out.println("COUNT="+count);
			}

			StringBuffer sb=new StringBuffer();
			
			for(int i=0;i<l.size();i++)
			{
				String[] docID = (String[])l.get(i);				
				
				//System.out.println("docID="+docID[0]);
				resultList.add(docID[0]);
				//out.write(docID[0]+"\n");
				//Thread.sleep(5);
				//out.flush();
				
			}		
			//out.flush();
			//out.close();

		}
		catch(Exception e)
		{
			try{
			if(out!=null)
				out.close();
			}
			catch(Exception e1)
			{
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		return resultList;

	}
	
	
	private void runDatabaseTOES(String value, String searchfield,String database, String server,String tablename)
    {
		Statement stmt = null;
		ResultSet rs = null;
		Connection con = null;
		String sqlQuery = null;
		
		HashMap mappingMap = new HashMap();
    	long midTime = System.currentTimeMillis();       
        boolean blnResult = false;
        try
        {
        	con = getConnection(this.URL,this.driver,this.username,this.password);
			stmt = con.createStatement(); 
			if(value.equalsIgnoreCase("1"))
			{
				sqlQuery = "select m_id from "+tablename;
			}
			else
			{
				sqlQuery = "select m_id from "+tablename +" where load_number="+value;
			}
			rs = stmt.executeQuery(sqlQuery);
			while (rs.next())
			{					
				String m_id = rs.getString("m_id");
				checkES(m_id, searchfield,database, server);
			}
            
        }
        catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (rs != null) {
				try {
					rs.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (stmt != null) {
				try {
					stmt.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			if (con != null) {
				try {
					con.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
    }
	
	
	public HashMap testMapping()
	{
		Statement stmt = null;
		ResultSet rs = null;
		Connection con = null;
		String sqlQuery = null;
		
		HashMap mappingMap = new HashMap();
		try
		{
			con = getConnection(this.URL,this.driver,this.username,this.password);
			stmt = con.createStatement();
			
			sqlQuery = "select bd_accessnumber,cafe_accessnumber,bd_pui,cafe_pui from bd_cafe_mapping";
	        rs = stmt.executeQuery(sqlQuery);
			int i=0;
			while (rs.next())
			{
		
				MappingObj mapping = new MappingObj();
				String pui = rs.getString("bd_pui");
				mapping.setBdAccessnumber(rs.getString("bd_accessnumber"));
				mapping.setCafeAccessnumber(rs.getString("cafe_accessnumber"));
				mapping.setBdPui(pui);
				mapping.setCafePui(rs.getString("cafe_pui"));
				mappingMap.put(pui, mapping);
				//System.out.print(i+",");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (rs != null) {
				try {
					rs.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (stmt != null) {
				try {
					stmt.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			if (con != null) {
				try {
					con.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return mappingMap;
		
	}
	
	public HashMap pickFirstMapping()
	{
		Statement stmt = null;
		ResultSet rs = null;
		Connection con = null;
		String sqlQuery = null;
		
		HashMap mappingMap = new HashMap();
		try
		{
			con = getConnection(this.URL,this.driver,this.username,this.password);
			stmt = con.createStatement();
			
			sqlQuery = "select pui,doi,authorselect bd_accessnumber,cafe_accessnumber,bd_pui,cafe_pui from hmo_cafe_doi";
	        rs = stmt.executeQuery(sqlQuery);
			int i=0;
			while (rs.next())
			{
		
				MappingObj mapping = new MappingObj();
				String pui = rs.getString("bd_pui");
				mapping.setBdAccessnumber(rs.getString("bd_accessnumber"));
				mapping.setCafeAccessnumber(rs.getString("cafe_accessnumber"));
				mapping.setBdPui(pui);
				mapping.setCafePui(rs.getString("cafe_pui"));
				mappingMap.put(pui, mapping);
				//System.out.print(i+",");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (rs != null) {
				try {
					rs.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (stmt != null) {
				try {
					stmt.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			
			if (con != null) {
				try {
					con.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return mappingMap;
		
	}
	
	public List getMID(Connection con) throws Exception
	{
		Statement stmt = null;
		ResultSet rs = null;
		String sqlQuery = null;
		String m_id = null;
		List midList = new ArrayList();
		
		try
		{
			
			stmt = con.createStatement();			
			sqlQuery = "select M_id from bd_master_dedupkey";
	        rs = stmt.executeQuery(sqlQuery);			
			while (rs.next())
			{
				m_id = rs.getString("M_ID");
				midList.add(m_id);
			}
					
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (rs != null) {
				try {
					rs.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (stmt != null) {
				try {
					stmt.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return midList;
	    
	}
					
	
	public void buildDedupkeyTable(String loadnumber) throws Exception
	{
		Statement stmt = null;
		Statement addStmt = null;
		Statement deleteStmt = null;
		ResultSet rs = null;
		Connection con = null;
		String sqlQuery = null;
		BufferedReader in = null;
		CombinedXMLWriter writer = new CombinedXMLWriter(1,1,"cpx");
		XmlCombiner c = new XmlCombiner(writer);
		String m_id = null;
		String accessnumber = null;
		String doi = null;
		String pui = null;
		String issn = null;
		String coden = null;
		String volume = null;
		String issue = null;
		String page = null;
		String articlenumber = null;
		//String loadnumber = null;
		String updatenumber = null;
		String pii = null;
		String citationtitle = null;
		String dedupkey = null;
		
		try
		{
			con = getConnection(this.URL,this.driver,this.username,this.password);
			stmt = con.createStatement();
			addStmt = con.createStatement();
			deleteStmt = con.createStatement();
			List midList = getMID(con);
			sqlQuery = "select M_id,accessnumber,doi,pui,issn,coden,volume,issue,page,articlenumber,loadnumber,updatenumber,pii,citationtitle from bd_master where loadnumber="+loadnumber;
	        rs = stmt.executeQuery(sqlQuery);
			int i=0;
			while (rs.next())
			{
				i++;
				try
				{
					m_id = rs.getString("M_ID");
					if(midList.contains(m_id))
					{
						deleteStmt.executeQuery("delete from bd_master_dedupkey where m_id='"+m_id+"'");
						System.out.println("delete m_id "+m_id);
					}
					accessnumber = rs.getString("ACCESSNUMBER");
					doi = rs.getString("DOI");
					if(doi==null)
					{
						doi="";
					}
					pui = rs.getString("PUI");
					issn = rs.getString("ISSN");
					if(issn==null)
					{
						issn="";
					}
					coden = rs.getString("CODEN");
					if(coden==null)
					{
						coden="";
					}
					volume = rs.getString("VOLUME");
					if(volume==null)
					{
						volume="";
					}
					issue = rs.getString("ISSUE");
					if(issue==null)
					{
						issue="";
					}
					page = rs.getString("PAGE");
					if(page==null)
					{
						page="";
					}
					articlenumber = rs.getString("ARTICLENUMBER");
					if(articlenumber==null)
					{
						articlenumber="";
					}
					loadnumber = rs.getString("LOADNUMBER");
					updatenumber = rs.getString("UPDATENUMBER");
					pii = rs.getString("PII");
					citationtitle = rs.getString("CITATIONTITLE");				
					dedupkey = c.getDedupKey(issn,coden,volume,issue,c.getPage(page,articlenumber));  
					String insertQuery = "insert into bd_master_dedupkey(M_ID,ACCESSNUMBER,DOI,PUI,ISSN,CODEN,VOLUME,ISSUE,PAGE,ARTICLENUMBER,"
							+ "LOADNUMBER,UPDATENUMBER,DEDUPKEY,PII,CITATIONTITLE) "
							+ "values ('"+m_id+"','"+accessnumber+"','"+doi+"','"+pui+"','"+issn+"','"+coden+"','"+volume+"','"+issue+"','"+page+"','"
							+ articlenumber+"','"+loadnumber+"','"+updatenumber+"','"+dedupkey+"','"+pii+"','"+citationtitle+"')";
					System.out.println("INSERT QUERY "+insertQuery);
					addStmt.addBatch(insertQuery);
					if(i==100)
					{
						addStmt.executeBatch();
						i=0;
					}      
					
				}
				catch(Exception e1)
				{
					e1.printStackTrace();
				}
	            
	        }
			addStmt.executeBatch();
	        
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (rs != null) {
				try {
					rs.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (stmt != null) {
				try {
					stmt.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (addStmt != null) {
				try {
					addStmt.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	    
	}
	
	public void buildMappingFromBD(String loadnumber) throws Exception
	{
		Statement stmt = null;
		Statement addStmt = null;
		Statement checkStmt = null;
		ResultSet rs = null;
		ResultSet checkRs = null;
		Connection con = null;
		String sqlQuery = null;
		
		String mid = null;
		String accessnumber = null;
		String doi = null;
		String pui = null;
		String issn = null;
		String coden = null;
		String volume = null;
		String issue = null;
		String page = null;
		String articlenumber = null;
		//String loadnumber = null;
		String updatenumber = null;
		String pii = null;
		String citationtitle = null;
		String dedupkey = null;
		
		try
		{
			con = getConnection(this.URL,this.driver,this.username,this.password);
			stmt = con.createStatement();
			addStmt = con.createStatement();
			checkStmt = con.createStatement();
			List midList = getMID(con);
			sqlQuery = "select M_id,accessnumber,doi,pui,issn,coden,volume,issue,page,articlenumber,loadnumber,updatenumber,pii,citationtitle from bd_master where database='cpx' and loadnumber="+loadnumber;
	        rs = stmt.executeQuery(sqlQuery);
			int i=0;
			while (rs.next())
			{
				i++;
				try
				{
					mid = rs.getString("M_ID");
					accessnumber = rs.getString("ACCESSNUMBER");
					doi = rs.getString("DOI");
					if(doi==null)
					{
						doi="";
					}
					pui = rs.getString("PUI");
					issn = rs.getString("ISSN");
					if(issn==null)
					{
						issn="";
					}
					coden = rs.getString("CODEN");
					if(coden==null)
					{
						coden="";
					}
					volume = rs.getString("VOLUME");
					if(volume==null)
					{
						volume="";
					}
					issue = rs.getString("ISSUE");
					if(issue==null)
					{
						issue="";
					}
					page = rs.getString("PAGE");
					if(page==null)
					{
						page="";
					}
					articlenumber = rs.getString("ARTICLENUMBER");
					if(articlenumber==null)
					{
						articlenumber="";
					}
					loadnumber = rs.getString("LOADNUMBER");
					updatenumber = rs.getString("UPDATENUMBER");
					pii = rs.getString("PII");
					citationtitle = rs.getString("CITATIONTITLE");				
					dedupkey = getDedupKey(issn,coden,volume,issue,getPage(page,articlenumber));  
					String[] mapping = checkCafe(checkStmt,accessnumber,doi,pui,pii,citationtitle,dedupkey);
					if(mapping!=null && mapping.length==5)
					{
						String insertQuery = "insert into HMO_BD_CAFE_MAPPING(BD_ACCESSNUMBER,CAFE_ACCESSNUMBER,BD_PUI,CAFE_PUI,MATCHED_CRITERION,LOADNUMBER)"
								+ "values("+mapping[0]+","+mapping[1]+","+mapping[2]+","+mapping[3]+",'"+mapping[4]+"',"+Integer.parseInt(loadnumber)+")";
	 
						System.out.println("INSERT QUERY "+insertQuery);
						addStmt.addBatch(insertQuery);
						if(i==50)
						{
							addStmt.executeBatch();
							i=0;
						}  
					
					
					}
				}
				catch(Exception e1)
				{
					e1.printStackTrace();
				}
	            
	        }
			addStmt.executeBatch();
	        
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (rs != null) {
				try {
					rs.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (stmt != null) {
				try {
					stmt.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (addStmt != null) {
				try {
					addStmt.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (checkStmt != null) {
				try {
					checkStmt.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	    
	}
	
	private String[] checkCafe(Statement stmt,String accessnumber,String doi,String pui,String pii,String citationtitle,String dedupkey)
	{
		
		ResultSet rs = null;		
		String[] result = null;
		try
		{			
			String sqlQuery = "select M_id,accessnumber,doi,pui,issn,coden,volume,issue,page,articlenumber,loadnumber,updatenumber,pii,citationtitle from cafe_master where pui='"+pui+"'";
			String sqlQuery1 = "select M_id,accessnumber,doi,pui,issn,coden,volume,issue,page,articlenumber,loadnumber,updatenumber,pii,citationtitle from cafe_master where accessnumber='"+accessnumber+"'";
			String sqlQuery2 = "select M_id,accessnumber,doi,pui,issn,coden,volume,issue,page,articlenumber,loadnumber,updatenumber,pii,citationtitle from cafe_master where doi='"+doi+"'";
			String sqlQuery3 = "select M_id,accessnumber,doi,pui,issn,coden,volume,issue,page,articlenumber,loadnumber,updatenumber,pii,citationtitle from cafe_master where pii='"+pii+"'";
			String sqlQuery4 = "select M_id,accessnumber,doi,pui,issn,coden,volume,issue,page,articlenumber,loadnumber,updatenumber,pii,citationtitle from cafe_master where citationtitle='"+citationtitle+"'";
			rs = stmt.executeQuery(sqlQuery);			
			result=checkResult(rs,"pui",accessnumber,doi,pui,pii,citationtitle,dedupkey);
			if(result==null)
			{
				rs = stmt.executeQuery(sqlQuery1);			
				result=checkResult(rs,"accessnumber",accessnumber,doi,pui,pii,citationtitle,dedupkey);
				if(result==null)
				{
					rs = stmt.executeQuery(sqlQuery2);			
					result=checkResult(rs,"doi",accessnumber,doi,pui,pii,citationtitle,dedupkey);
					if(result==null)
					{
						rs = stmt.executeQuery(sqlQuery3);			
						result=checkResult(rs,"pii",accessnumber,doi,pui,pii,citationtitle,dedupkey);
						if(result==null)
						{
							rs = stmt.executeQuery(sqlQuery4);			
							result=checkResult(rs,"citationtitle",accessnumber,doi,pui,pii,citationtitle,dedupkey);
						}
					}
				}
			}
		}
		catch (Exception e)
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
		}
		return result;
	}
			
	private String[] checkResult(ResultSet rs,String checkParameter,String accessnumber,String doi,String pui,String pii,String citationtitle,String dedupkey)
	{
		String cafe_mid = null;
		String cafe_accessnumber = null;
		String cafe_doi = null;
		String cafe_pui = null;
		String cafe_issn = null;
		String cafe_coden = null;
		String cafe_volume = null;
		String cafe_issue = null;
		String cafe_page = null;
		String cafe_articlenumber = null;
		//String loadnumber = null;
		String cafe_updatenumber = null;
		String cafe_pii = null;
		String cafe_citationtitle = null;
		String cafe_dedupkey = null;
		boolean mappingFlag = false;
		String mappingMethod = null;
		String[] result = null;
		
		try
		{
			while (rs.next())
			{			
				cafe_mid = rs.getString("M_ID");
				cafe_accessnumber = rs.getString("ACCESSNUMBER");
				cafe_doi = rs.getString("DOI");
				if(cafe_doi==null)
				{
					cafe_doi="";
				}
				cafe_pui = rs.getString("PUI");
				cafe_issn = rs.getString("ISSN");
				if(cafe_issn==null)
				{
					cafe_issn="";
				}
				cafe_coden = rs.getString("CODEN");
				if(cafe_coden==null)
				{
					cafe_coden="";
				}
				cafe_volume = rs.getString("VOLUME");
				if(cafe_volume==null)
				{
					cafe_volume="";
				}
				cafe_issue = rs.getString("ISSUE");
				if(cafe_issue==null)
				{
					cafe_issue="";
				}
				cafe_page = rs.getString("PAGE");
				if(cafe_page==null)
				{
					cafe_page="";
				}
				cafe_articlenumber = rs.getString("ARTICLENUMBER");
				if(cafe_articlenumber==null)
				{
					cafe_articlenumber="";
				}
				//cafe_loadnumber = rs.getString("LOADNUMBER");
				//cafe_updatenumber = rs.getString("UPDATENUMBER");
				cafe_pii = rs.getString("PII");
				cafe_citationtitle = rs.getString("CITATIONTITLE");				
				cafe_dedupkey = getDedupKey(cafe_issn,cafe_coden,cafe_volume,cafe_issue,getPage(cafe_page,cafe_articlenumber));  
				if(!checkParameter.equals("pui") && (cafe_pui.equals(pui)))
				{
					mappingMethod=checkParameter+",pui";
				}
				else if(!checkParameter.equals("accessnumber") && (cafe_accessnumber.equals(accessnumber)))
				{
					mappingMethod=checkParameter+",accessnumber";
				}
				else if	(!checkParameter.equals("doi") && cafe_doi!=null && doi!=null && cafe_doi.equals(doi))
				{
					mappingMethod=checkParameter+",doi";
				}
				else if(!checkParameter.equals("pii") && cafe_pii != null && pii != null && cafe_pii.equals(pii))
				{
					mappingMethod=checkParameter+",pii";
				}
				else if(!checkParameter.equals("citationtitle") && cafe_citationtitle!= null && citationtitle!= null && cafe_citationtitle.equals(citationtitle))
				{
					mappingMethod=checkParameter+",citationtitle";
				}
				else if(!checkParameter.equals("dedupkey") && cafe_dedupkey!=null && dedupkey!=null && cafe_dedupkey.equals(dedupkey))
				{
					mappingMethod=checkParameter+",dedupkey";
				}
				if(mappingMethod!=null)	
				{
					result = new String[5];
					result[0]=accessnumber;
					result[1]=cafe_accessnumber;
					result[2]=pui;
					result[3]=cafe_pui;
					result[4]=mappingMethod;
					break;
				}
	
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (rs != null) {
				try {
					rs.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}
		
		return result;
		
	}
	
	public void urlReader() throws Exception
	{
		Statement stmt = null;
		ResultSet rs = null;
		Connection con = null;
		String sqlQuery = null;
		FileWriter out = null;
		BufferedReader in = null;
		
		try
		{
			out = new FileWriter("outputFromURL.out");
			con = getConnection(this.URL,this.driver,this.username,this.password);
			stmt = con.createStatement();
			List loadNumberList = new ArrayList();
	        URL chinese = new URL("http://www.wenxuecity.com/");
	        in = new BufferedReader(new InputStreamReader(chinese.openStream(), "UTF-8"));
	
	        String inputLine;
	        int i=0;
	        while ((inputLine = in.readLine()) != null)
	        {
	        	i++;
	            System.out.println(inputLine);
	            inputLine = inputLine.replaceAll("'", "''''");
	            out.write(i+"\t"+inputLine+"\n");
	            //stmt.executeQuery("insert into readFromURL values("+i+",'"+inputLine+"')");
	            
	        }
	        
	        in.close();
	        out.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (rs != null) {
				try {
					rs.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (stmt != null) {
				try {
					stmt.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	    
	}
	
	private void buildXMLFromDATABASE(String weekNumber)
	{
		Statement stmt = null;
		ResultSet rs = null;
		Connection con = null;
		String sqlQuery = null;
		String m_id = null;
		String accessnumber = null;
		String load_number = null;
		String ndi = null;
		FileWriter out = null;
		
		try
		{

			con = getConnection(this.URL,this.driver,this.username,this.password);
			stmt = con.createStatement();
			List loadNumberList = new ArrayList();
			if(weekNumber.equals("0"))
			{
				sqlQuery="select distinct load_number from c84_master";
				rs = stmt.executeQuery(sqlQuery);
				while (rs.next())
				{	
					if(rs.getString("load_number")!=null)				
					loadNumberList.add(rs.getString("load_number"));
				}
			}
			else
			{
				loadNumberList.add(weekNumber);
			}
			int k = 0;
			for(int i=0;i<loadNumberList.size();i++)
			{
				out = new FileWriter("C84_"+(String)loadNumberList.get(i)+".xml");
			
				sqlQuery="select * from C84_master where load_number='"+(String)loadNumberList.get(i)+"'";															
				System.out.println("QUERY= "+sqlQuery);
				stmt = con.createStatement();
				rs = stmt.executeQuery(sqlQuery);
				ResultSetMetaData rsmd = rs.getMetaData();
				out.write("<?xml version='1.0'  encoding='UTF-8'?>\n");
				out.write("<RESULTS>\n");
				
				while (rs.next())
				{
					out.write("<ROW>\n");
					for (int j = 1; j <= rsmd.getColumnCount(); j++) 
					{
						String name = rsmd.getColumnName(j);
						m_id = rs.getString("m_id");
						String value = rs.getString(name);
						if(value!=null && !(name.equals("SU") || name.equals("PT") || name.equals("JJ") || name.equals("OA") || name.equals("PG")))
						{					
							out.write("<COLUMN NAME=\""+name+"\"><![CDATA["+value+"]]></COLUMN>\n");
						}
						
					}
					
					//out.write("<COLUMN NAME=\"EVLINK\"><![CDATA[https://www.engineeringvillage.com/share/document.url?mid="+m_id+"&database=cpx&view=detailed]]></COLUMN>\n");
					out.write("</ROW>\n");
				}
				out.write("</RESULTS>\n");
				if(stmt!=null)
				{
					stmt.close();
				}
				out.flush();
				out.close();
			}
			System.out.println("total count= "+k);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (rs != null) {
				try {
					rs.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (stmt != null) {
				try {
					stmt.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	private void validatedXml(String filename) throws Exception
	{
		// parse an XML document into a DOM tree
		 javax.xml.parsers.DocumentBuilder parser =  javax.xml.parsers.DocumentBuilderFactory.newInstance().newDocumentBuilder();
		 Document document = parser.parse(new File(filename));

		// create a SchemaFactory capable of understanding WXS schemas
		//SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		 SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.XML_DTD_NS_URI);

		// load a WXS schema, represented by a Schema instance
		//Source schemaFile = new StreamSource(new File("ani512.xsd"));
		 Source schemaFile = new StreamSource(new File("ani512.dtd"));
		Schema schema = factory.newSchema(schemaFile);

		// create a Validator instance, which can be used to validate an instance document
		Validator validator = schema.newValidator();

		// validate the DOM tree
		try {
		   //validator.validate(new DOMSource(document));
			validator.validate(new StreamSource(new File(filename)));
		    System.out.println(filename+" is valid!");
		} catch (SAXException e) {
			System.out.println(filename+" is invalid!");
			e.printStackTrace();
		    // instance document is invalid!
		}
		
	}
	
	private void testBuildAuthorESDoc(String loadnumber) throws Exception
	{
		// doc_type should be "apr"
		Connection con = null;
		String doc_type="apr";
		String esIndexType = "file";
		int ESdirSeq_ID = 1;
		int loadNumber = Integer.parseInt(loadnumber);
		int fileSize =500;
		WriteEsDocToFile outputFile = new WriteEsDocToFile(fileSize);
		CombinedAuAfJSON writer = new CombinedAuAfJSON(doc_type,loadNumber,outputFile,esIndexType);
		writer.init(ESdirSeq_ID);
		
		con = getConnection(this.URL,this.driver,this.username,this.password);

		String esDir = writer.getEsDirName();
		if(loadNumber ==1)
		{
			writeCombinedByTable(con,writer);
		}
		else
		{
			writeCombinedByWeekNumber(con,writer,loadNumber);
		}
		
	}
	
	public void parseJsonObject(String filename)
	{
		BufferedReader in = null;
		try{
			in = new BufferedReader(new FileReader(new File(filename)));
		    JsonReader rdr = Json.createReader(in); 
		 
		    //JsonObject obj = rdr.readObject();
		    //JsonArray results = obj.getJsonArray("evrecords");
		    JsonArray results = rdr.readArray();
		    for (JsonObject result : results.getValuesAs(JsonObject.class)) {
		    	System.out.print(result.getString("pui")+"\t");		          
		    	System.out.println(result.getString("doi", ""));		          
		    }
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public void writeCombinedByTable(Connection con, CombinedAuAfJSON writer) throws Exception
	{
		Statement stmt = null;
		ResultSet rs = null;
		String query=null;
	}
	
	
	public void writeCombinedByWeekNumber(Connection con, CombinedAuAfJSON writer, int loadNumber) throws Exception
	{
		action = "new";
		Statement stmt = null;
		ResultSet rs = null;

		String query=null;

		try
		{
			stmt = con.createStatement();
			System.out.println("Running the query...");
			
			if(!(action.isEmpty()) && action.equalsIgnoreCase("new"))
			{
				/*query = "select * from " +  tableName + " where loadnumber=" + loadNumber + " and authorid in (select AUTHOR_ID from " + metadataTableName + 
						" where STATUS='matched' and dbase='cpx')";*/   // used for intial/pre-release ES index, till loadnumber: 2017261
				
				query = "select * from db_cafe.AUTHOR_PROFILE where ES_STATUS is null and authorid in (select AUTHOR_ID from db_cafe.cmb_au_lookup where STATUS='matched' and dbase='cpx' and loadnumber="+loadNumber+")";


				System.out.println(query);
				
				stmt.setFetchSize(200);
				rs = stmt.executeQuery(query);			
				writeRecs(rs,writer,con);

				//esIndex.ProcessBulk();
				//esIndex.end();
				
				System.out.println("Wrote records.");
			}
			else if(!(action.isEmpty()) && action.equalsIgnoreCase("update"))
			{
				updateNumber=String.valueOf(loadNumber);
				/*query = "select * from " +  tableName + " where updatenumber=" + updateNumber + " and authorid in (select AUTHOR_ID from " + metadataTableName + 
						" where STATUS='matched' and dbase='cpx')";*/      // used for intial/pre-release ES index, till updatenumber: 2017366
				
				query = "select * from db_cafe." +  tableName + " where ES_STATUS is null and authorid in (select AUTHOR_ID from cmb_au_lookup  where STATUS='matched' and dbase='cpx')";
				

				System.out.println(query);

				stmt.setFetchSize(200);
				rs = stmt.executeQuery(query);

				System.out.println("Got records... from table: " + tableName);

				//writeRecs(rs,writer);
		
				//esIndex.ProcessBulk();
				//esIndex.end();

				System.out.println("Wrote records.");

			}

			else if(!(action.isEmpty()) && action.equalsIgnoreCase("delete"))
			{
				// need to check with Hongrong

				updateNumber=String.valueOf(loadNumber);
				query = "select M_ID from db_cafe." +  tableName + " where updatenumber=" + updateNumber; 

				System.out.println(query);

				stmt.setFetchSize(200);
				rs = stmt.executeQuery(query);

				System.out.println("Got records... from table: " + tableName);
				//getDeletionList(rs,writer);

				//esIndex.createBulkDelete(doc_type, auId_deletion_list);

			}


		}

		finally
		{
			if(rs !=null)
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

			if(stmt !=null)
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

			if(con !=null)
			{
				try
				{
					con.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}

	}
	
	public void writeRecs(ResultSet rs, CombinedAuAfJSON writer, Connection con) throws Exception
	{
		int count =0, rec_count = 1;
		String currentdept_affid= null;
		String email="";
		String doc_type="apr";
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		while (rs.next())
		{
			try
			{

				AuAfCombinedRec rec = new AuAfCombinedRec();
				AuAffiliation auaf = new AuAffiliation();

				String date= dateFormat.format(new Date());

				if(doc_type !=null && doc_type.equalsIgnoreCase("apr"))
				{


					//M_ID
					rec.put(AuAfCombinedRec.DOCID, rs.getString("M_ID"));

					// UPDATEEPOCH (place holder for future filling with SQS epoch)
					rec.put(AuAfCombinedRec.UPDATEEPOCH, "");
					
					//LOADNUMBER
					if(rs.getString("LOADNUMBER") !=null)
					{
						rec.put(AuAfCombinedRec.LOAD_NUMBER, Integer.toString(rs.getInt("LOADNUMBER")));
					}

					//UPDATENUMBER
					if(rs.getString("UPDATENUMBER") !=null)
					{
						rec.put(AuAfCombinedRec.UPDATE_NUMBER, Integer.toString(rs.getInt("UPDATENUMBER")));
					}


					//EID
					if(rs.getString("EID") !=null)
					{
						rec.put(AuAfCombinedRec.EID, rs.getString("EID"));
					}

					//DOC_TYPE
					if(doc_type !=null)
					{
						rec.put(AuAfCombinedRec.DOC_TYPE, doc_type);
					}

					//STATUS
					if(rs.getString("STATUS") !=null)
					{
						rec.put(AuAfCombinedRec.STATUS, rs.getString("STATUS"));
					}

					//TIMESTAMP in DB, in ES called "LOADDATE"
					if(rs.getString("TIMESTAMP") !=null)
					{
						//rec.put(AuAfCombinedRec.TIMESTAMP, timeStampFormat(rs.getString("TIMESTAMP")));
						rec.put(AuAfCombinedRec.LOADDATE, rs.getString("TIMESTAMP"));
					}

					//INDEXDATE in DB, in ES called "ITEMTRANSACTIONID"
					if(rs.getString("INDEXED_DATE") !=null)
					{
						rec.put(AuAfCombinedRec.ITEMTRANSACTIONID, rs.getString("INDEXED_DATE"));
					}

					// EPOCH in DB, in ES called "INDEXEDDATE"
					if(rs.getString("EPOCH") !=null)
					{
						rec.put(AuAfCombinedRec.INDEXEDDATE, rs.getString("EPOCH"));
					}

					// ES Index Data (Current DateTime)
					rec.put(AuAfCombinedRec.ESINDEXTIME, date);

					//AUTHORID
					if(rs.getString("AUTHORID") !=null)
					{
						rec.put(AuAfCombinedRec.AUID, rs.getString("AUTHORID"));
					}

					//ORCID, comment for now bc it is not in author_profile table yet
					if(rs.getString("ORCID") !=null)
					{
						rec.put(AuAfCombinedRec.ORCID, rs.getString("ORCID"));
					}

					//PREFERRED_INI
					if(rs.getString("INITIALS") !=null)
					{
						rec.put(AuAfCombinedRec.PREFERRED_INI, DataLoadDictionary.mapEntity(rs.getString("INITIALS")));
					}

					//PREFERRED_FIRST
					if(rs.getString("GIVENNAME") !=null)
					{
						rec.put(AuAfCombinedRec.PREFERRED_FIRST, DataLoadDictionary.mapEntity(rs.getString("GIVENNAME")));
					}

					//PREFERRED_LAST
					if(rs.getString("SURENAME") !=null)
					{
						rec.put(AuAfCombinedRec.PREFERRED_LAST, DataLoadDictionary.mapEntity(rs.getString("SURENAME")));
					}

					//NAME_VARAINT (INITIALS, First, Last)
					if(rs.getString("NAME_VARIANT") !=null)
					{
						prepareNameVariant(rs.getString("NAME_VARIANT"),rec);
					}


					//SUBJABBR
					if(rs.getString("CLASSIFICATION_SUBJABBR") !=null)
					{
						rec.put(AuAfCombinedRec.CLASSIFICATION_SUBJABBR, rs.getString("CLASSIFICATION_SUBJABBR"));

						//SUBJECT_CLUSETR
						prepareSubjabbr(rs.getString("CLASSIFICATION_SUBJABBR"),rec);
					}

					//PUBLICATION_RANGE
					if(rs.getString("PUBLICATION_RANGE") !=null)
					{
						String[] ranges = rs.getString("PUBLICATION_RANGE").split("-");
						if(ranges[0] !=null)
						{
							rec.put(AuAfCombinedRec.PUBLICATION_RANGE_FIRST, ranges[0]);
						}
						if(ranges[1] !=null)
						{
							rec.put(AuAfCombinedRec.PUBLICATION_RANGE_LAST, ranges[1]);
						}
					}

					//SOURCE_TITLE

					String sourceTitles = getStringFromClob(rs.getClob("SOURCE_TITLE"));

					if(sourceTitles !=null)
					{
						rec.put(AuAfCombinedRec.SOURCE_TITLE, sourceTitles);
					}

					//ISSN
					String journals = getStringFromClob(rs.getClob("JOURNALS"));	
					if(journals !=null)
					{
						prepareISSN(journals,rec);
					}

					if(rs.getString("E_ADDRESS") !=null)
					{
						String []e_mail = rs.getString("E_ADDRESS").split(Constants.IDDELIMITER);
						if(e_mail.length >1)
						{
							email = e_mail[1];
						}

						rec.put(AuAfCombinedRec.EMAIL_ADDRESS, email);
					}

					//CURRENT_AFFILIATION_ID
					if(rs.getString("CURRENT_AFF_ID") !=null)
					{
						if(rs.getString("CURRENT_AFF_TYPE") !=null && rs.getString("CURRENT_AFF_TYPE").equalsIgnoreCase("parent"))
						{
							auaf.setAffiliationId(rs.getString("CURRENT_AFF_ID"));
						}
						else
						{
							currentdept_affid = rs.getString("CURRENT_AFF_ID");
						}

					}
					//CURRENT_PARENT_AFFILIATION_ID
					if(rs.getString("PARENT_AFF_TYPE") !=null && rs.getString("PARENT_AFF_ID") !=null)
					{
						if(rs.getString("PARENT_AFF_TYPE").trim().equalsIgnoreCase("parent"))
						{
							auaf.setAffiliationId(rs.getString("PARENT_AFF_ID"));
						}
					}

					//Current PARENT AFFILIATION INFO
					if(!(auaf.getAffiliationId().isEmpty()))
					{
						prepareCurrentAffiliation(auaf.getAffiliationId(), currentdept_affid,auaf,con);
					}

					String history_affiliationIds = getStringFromClob(rs.getClob("HISTORY_AFFILIATIONID"));
					/*
					if(history_affiliationIds !=null)
					{					
						prepareHistoryAffiliationIds(history_affiliationIds);

						if(affiliation_historyIds_List.size() >0)
						{	
							prepareHistoryAffiliation();						
						}
					}
					*/

					//CITY
					auaf.setAffiliationCity();

					//COUNTRY
					auaf.setAffiliationCountry();

					//Parents AFFILIATION ID
					auaf.setParentAffiliationsId();


					//CURRENT PARENT AFFILIATION_ID
					rec.put(AuAfCombinedRec.AFID, auaf.getAffiliationId());

					//CURRENT PARENT DISPLAY_NAME
					rec.put(AuAfCombinedRec.DISPLAY_NAME, auaf.getAffiliationDisplayName());

					//CURRENT PARENT DISPLAY_CITY
					rec.put(AuAfCombinedRec.DISPLAY_CITY, auaf.getAffiliationDisplayCity());

					//CURRENT PARENT DISPLAY_COUNTRY
					rec.put(AuAfCombinedRec.DISPLAY_COUNTRY, auaf.getAffiliationDisplayCountry());

					//CURRENT PARENT SORT_NAME
					rec.put(AuAfCombinedRec.AFFILIATION_SORT_NAME, auaf.getAffiliationSortName());

					//PARENT AFFILIATION HISTORY_ID
					rec.put(AuAfCombinedRec.AFFILIATION_HISTORY_ID, auaf.getHistoryAffid());

					// PARENT AFFILIATION HISTORY_DISPLAY_NAME
					rec.put(AuAfCombinedRec.HISTORY_DISPLAY_NAME, auaf.getHistoryDisplayName());

					// PARENT AFFILIATION HISTORY_CITY
					rec.put(AuAfCombinedRec.HISTORY_CITY, auaf.getHistoryCity());

					// PARENT AFFILIATION HISTORY_COUNTRY
					rec.put(AuAfCombinedRec.HISTORY_COUNTRY, auaf.getHistoryCountry());

					//CURRENT_AND_HISTORY PARENT PREFERRED_NAME
					rec.put(AuAfCombinedRec.AFFILIATION_PREFERRED_NAME, auaf.getParentAffiliationsPreferredName());

					//CURRENT_AND_HOSTORY PARENT NAME_VARIANT
					rec.put(AuAfCombinedRec.AFFILIATION_VARIANT_NAME, auaf.getParentAffiliationsNameVariant());

					//CURRENT_AND_HISTORY PARENT NAMEID
					rec.put(AuAfCombinedRec.NAME_ID, auaf.getAffiliationNameId());

					//CURRENT DEPT AFFILIATION_ID
					rec.put(AuAfCombinedRec.CURRENT_DEPT_AFFILIATION_ID, auaf.getCurrentDeptAffiliation_Id());

					//CURRENT DEPT AFFILIATION DISPLAY_NAME
					rec.put(AuAfCombinedRec.CURRENT_DEPT_AFFILIATION_DISPLAY_NAME, DataLoadDictionary.mapEntity(auaf.getCurrentDeptAffiliation_DisplayName()));

					//CURRENT DEPT AFFILIATION CITY
					rec.put(AuAfCombinedRec.CURRENT_DEPT_AFFILIATIOIN_CITY, auaf.getCurrentDeptAffiliation_City());

					//CURRENT DEPT AFFILIATION COUNTRY
					rec.put(AuAfCombinedRec.CURRENT_DEPT_AFFILIATION_COUNTRY, auaf.getCurrentDeptAffiliation_Country());
				}

				writer.writeAuRec(rec);		
				auaf=null;
				count ++;
				//rec_count++;


			}
			catch (SQLException e) 
			{
				System.out.println("Error Occurred reading from ResultSet for DOCID: " + rs.getString("M_ID") + " ... " + e.getMessage());
				e.printStackTrace();
			}
		}

		System.out.println("Total records count: " +  count);
	}
	
	public void prepareISSN(String journals, AuAfCombinedRec rec)
	{
		String [] author_Journals = null;
		String author_Journal = null;
		String [] singleJournal = null;

		StringBuffer sourceTitles = new StringBuffer();
		StringBuffer issn = new StringBuffer();
		String mapped_issn = "";
		LinkedHashSet<String> issn_list = new LinkedHashSet<String>();


		author_Journals = journals.split(Constants.AUDELIMITER);
		for(int i=0; i<author_Journals.length; i++)
		{
			author_Journal = author_Journals[i].trim();
			if(author_Journal !=null && !(author_Journal.isEmpty()))
			{
				singleJournal = author_Journal.split(Constants.IDDELIMITER);

				if(singleJournal.length>3 && singleJournal[3] !=null)
				{
					mapped_issn = DataLoadDictionary.mapEntity(singleJournal[3]);
					if(!(issn_list.contains(mapped_issn)))
					{
						issn_list.add(mapped_issn);
					}
				}

			}
		}

		// Combine unique list of Issn
		for(String issn_str: issn_list)
		{
			issn.append(issn_str);
			issn.append(Constants.IDDELIMITER);
		}

		rec.put(AuAfCombinedRec.ISSN, issn.toString());

		//clearout stringbuffers
		issn.delete(0, issn.length());

		//clear out the issn List
		issn_list.clear();
	}  
	//SUBJABBR LIST
		public void prepareSubjabbr(String classification_Subjabbr, AuAfCombinedRec rec)
		{
			StringBuffer subjabbrCode = new StringBuffer();
			String[]  single_subjabbr;

			String [] subjabbrs = classification_Subjabbr.split(Constants.AUDELIMITER);
			for(int i=0; i<subjabbrs.length; i++)
			{
				single_subjabbr = subjabbrs[i].split(Constants.IDDELIMITER);

				if(single_subjabbr !=null && single_subjabbr.length>1)
				{
					subjabbrCode.append(single_subjabbr[1]);
					if(i< subjabbrs.length -1)
						subjabbrCode.append(Constants.IDDELIMITER);
				}

			}

			rec.put(AuAfCombinedRec.SUBJECT_CLUSTER, subjabbrCode.toString());
			// clearout subjabbr
			subjabbrCode.delete(0, subjabbrCode.length());

		}
	
	public void prepareNameVariant(String name_variant, AuAfCombinedRec rec)
	{
		String[] name_variants = null;
		String singleName_Variant = "";

		String [] singleVarainat;

		// add to the record
		StringBuffer variantNameInit = new StringBuffer();
		StringBuffer variantNameFirst = new StringBuffer();
		StringBuffer variantNameLast = new StringBuffer();


		name_variants = name_variant.split(Constants.AUDELIMITER);

		for(int i=0;i<name_variants.length;i++)
		{
			singleName_Variant = name_variants[i].trim();
			if(singleName_Variant !=null && !(singleName_Variant.isEmpty()))
			{
				singleVarainat = singleName_Variant.split(Constants.IDDELIMITER);

				if(singleVarainat.length>0 && singleVarainat[0] != null)
				{
					variantNameInit.append(DataLoadDictionary.mapEntity(singleVarainat[0]));
				}
				if(singleVarainat.length>2 && singleVarainat[2] != null)
				{
					variantNameLast.append(DataLoadDictionary.mapEntity(singleVarainat[2]));
				}
				if(singleVarainat.length>3 && singleVarainat[3] !=null)
				{
					variantNameFirst.append(DataLoadDictionary.mapEntity(singleVarainat[3]));
				}
				if(i<name_variants.length -1)
				{
					variantNameInit.append(Constants.IDDELIMITER);
					variantNameLast.append(Constants.IDDELIMITER);
					variantNameFirst.append(Constants.IDDELIMITER);
				}
			}
		}

		rec.put(AuAfCombinedRec.VARIANT_INI, variantNameInit.toString());
		rec.put(AuAfCombinedRec.VARIANT_FIRST, variantNameFirst.toString());
		rec.put(AuAfCombinedRec.VARIANT_LAST, variantNameLast.toString());

		//clearout all stringbuffers
		variantNameInit.delete(0, variantNameInit.length());
		variantNameFirst.delete(0,variantNameFirst.length());
		variantNameLast.delete(0, variantNameLast.length());

	}
	
	private String getStringFromClob(Clob clob)
	{
		String str = null;
		try
		{
			if(clob !=null)
			{
				str = clob.getSubString(1, (int) clob.length());
			}
		}
		catch(SQLException ex)
		{
			ex.printStackTrace();
		}
		return str;
	}

	private void prepareCurrentAffiliation(String parentAffId, String current_deptId,AuAffiliation auaf,Connection con)
	{
		Statement stmt = null;
		ResultSet rs = null;
		ResultSet rsDept = null;
		//Connection con=null;
		try
		{
			//con = getConnection(this.URL,this.driver,this.username,this.password);
			stmt = con.createStatement();
			String query = "select * from db_cafe.AUTHOR_AFF where AFFID='" + parentAffId + "'";
			rs = stmt.executeQuery(query);

			while(rs.next())
			{
				//PREFERRED_NAME
				if(rs.getString("PREFERED_NAME") !=null)
				{
					auaf.setAffiliationPreferredName(DataLoadDictionary.mapEntity(rs.getString("PREFERED_NAME")));
				}

				//NAME_VARIANT
				String affNameVariants = getStringFromClob(rs.getClob("NAME_VARIANT"));

				if(affNameVariants !=null)
				{
					auaf.setParentAffiliationsNameVariant(DataLoadDictionary.mapEntity(affNameVariants));
				}

				// DISPLAY_NAME
				if(rs.getString("AFDISPNAME") !=null)
				{
					auaf.setAffiliationDisplayName(DataLoadDictionary.mapEntity(rs.getString("AFDISPNAME")));
				}

				//DISPLAY_CITY
				if(rs.getString("CITY") !=null)
				{
					auaf.setAffiliationDisplayCity(rs.getString("CITY"));
				}

				//DISPLAY_CITY_GROUP
				if(rs.getString("CITYGROUP") != null)
				{
					auaf.setAffiliationDisplayCity(rs.getString("CITYGROUP"));
				}

				//DISPLAY_COUNTRY
				if(rs.getString("COUNTRY") !=null)
				{
					auaf.setAffiliationDisplayCountry(rs.getString("COUNTRY"));
				}

				//CURRENT NAMEID
				if(rs.getString("AFNAMEID") !=null)
				{
					auaf.setAffiliationNameId(DataLoadDictionary.mapEntity(rs.getString("AFNAMEID")));

				}

				//CURRENT PARENT SORTNAME
				if(rs.getString("SORTED_NAME") !=null)
				{
					auaf.setAffiliationSortName(DataLoadDictionary.mapEntity(rs.getString("SORTED_NAME")));
				}

			}

			//Current DepartmentID, ONLY FOR DISPLAY			

			//dept id
			if(current_deptId !=null)
			{
				query = "select AFDISPNAME,CITY,COUNTRY from db_cafe.AUTHOR_AFF where AFFID='" + current_deptId + "'";
				rsDept = stmt.executeQuery(query);

				auaf.setCurrentDeptAffiliation_Id(current_deptId);
				while(rsDept.next())
				{
					if(rsDept.getString("AFDISPNAME") !=null)
					{
						auaf.setCurrentDeptAffiliation_DisplayName(DataLoadDictionary.mapEntity(rsDept.getString("AFDISPNAME")));
					}
					if(rsDept.getString("CITY") !=null)
					{
						auaf.setCurrentDeptAffiliation_City(rsDept.getString("CITY"));
					}
					if(rsDept.getString("COUNTRY") !=null)
					{
						auaf.setCurrentDeptAffiliation_Country(rsDept.getString("COUNTRY"));
					}
				}
			}

		}
		catch(SQLException ex)
		{
			System.out.println("Error Occurred reading from Author_Aff for Parent affid: " + parentAffId + " ... " + ex.getMessage());
			ex.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(con!=null)
			{
				try
				{
					con.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			if(rs!=null)
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
			if(rsDept !=null)
			{
				try
				{
					rsDept.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}

			if(stmt !=null)
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

	
	private void testBuildElasticSearch() throws Exception
	{
		// Construct a new Jest Client via factory
		//String endpoint = "https://shared-search-es-kibana.dev.scopussearch.net/app/kibana#/discover?_g=()&_a=(columns:!(_source),index:'60f577f0-47f9-11ea-a219-45f91d04cdd7',interval:auto,query:(language:lucene,query:'loadNumber:202010'),sort:!(_score,desc))"; 
				
		//String endpoint = "http://search-evcafeauaf-v6tfjfyfj26rtoneh233lzzqtq.us-east-1.es.amazonaws.com:80";
		String endpoint = "https://shared-search-service-api.cert.scopussearch.net/document/v1/query/result";
		JestClientFactory factory = new JestClientFactory();
		factory.setHttpClientConfig(new HttpClientConfig
				.Builder(endpoint)
				.multiThreaded(true)
				.build()
				);
		
		JestClient client = factory.getObject();
				
				String esDocument = "{\n\"docproperties\":\n"+
						"{\n"+
				"\"doc_type\": \"apr\",\n"+
				"\"status\": \"update\",\n"+
				"\"loaddate\": \"20160601\",\n"+
				"\"itemtransactionid\": \"2015-09-01T04:32:52.537345Z\",\n"+
				"\"indexeddate\": \"1441081972\",\n"+
				"\"esindextime\": \"2016-07-19T17:52:43.404Z\",\n"+
				"\"loadnumber\": \"401600\"\n"+
			"},\n"+
			"\"audoc\":\n"+ 
			"{\n"+
				"\"docid\": \"aut_M22aaa18f155dfa29a2bM7f9b10178163171\",\n"+
				"\"eid\": \"9-s2.0-56798528800\",\n"+
				"\"auid\": \"56798528800\",\n"+
				"\"orcid\": \"null\",\n"+
				"\"author_name\":\n"+ 
					"{\n"+
						"\"variant_name\":\n"+ 
						"{\n"+
							"\"variant_first\": [  ],\n"+
							"\"variant_ini\": [  ],\n"+
							"\"variant_last\": [  ]\n"+
						"},\n"+
						"\"preferred_name\":\n"+ 
						"{\n"+
							"\"preferred_first\": \"Iv&aacute;n J.\",\n"+
							"\"preferred_ini\": \"I.J.\",\n"+
							"\"preferred_last\": \"Bazany-Rodr&iacute;guez\"\n"+
						"}\n"+
					"},\n"+
				"\"subjabbr\":\n"+ 
				"[\n"+
					"{ \"frequency\": \"3\" , \"code\": \"PHYS\" },\n"+
					"{ \"frequency\": \"5\" , \"code\": \"MATE\" },\n"+
					"{ \"frequency\": \"1\" , \"code\": \"CHEM\" },\n"+
					"{ \"frequency\": \"1\" , \"code\": \"ENGI\" }\n"+
				"],\n"+
				"\"subjclus\": [ \"PHYS\" , \"MATE\" , \"CHEM\" , \"ENGI\" ],\n"+
				"\"pubrangefirst\": \"2015\",\n"+
				"\"pubrangelast\": \"2016\",\n"+
				"\"srctitle\": [ \"Acta Crystallographica Section E: Crystallographic Communications\" , \"Sensors and Actuators, B: Chemical\" ],\n"+
				"\"issn\": [ \"20569890\" , \"09254005\" ],\n"+
				"\"email\": \"\",\n"+
				"\"author_affiliations\":\n"+ 
				"{\n"+
					"\"current\":\n"+ 
					"{\n"+
						"\"afid\": \"60032442\",\n"+
						"\"display_name\": \"Universidad Nacional Autonoma de Mexico\",\n"+
						"\"display_city\": \"Mexico City\",\n"+
						"\"display_country\": \"Mexico\",\n"+
						"\"sortname\": \"National Autonomous University of Mexico\"\n"+
					"},\n"+
					"\"history\":\n"+ 
					"{\n"+
						"\"afhistid\": [  ],\n"+
						"\"history_display_name\": [  ],\n"+
						"\"history_city\": [  ],\n"+
						"\"history_country\": [  ]\n"+
					"},\n"+
					"\"parafid\": [ \"60032442\" ],\n"+
					"\"affiliation_name\":\n"+ 
					"{\n"+
						"\"affilprefname\": [ \"Universidad Nacional Autonoma de Mexico\" ],\n"+
						"\"affilnamevar\": [ \"UNAM\" , \"Universidad Nacional Aut&oacute;noma de M&eacute;xico\" ]\n"+
					"},\n"+
					"\"city\": [ \"Mexico City\" ],\n"+
					"\"country\": [ \"Mexico\" ],\n"+
					"\"nameid\": [ \"Universidad Nacional Autonoma de Mexico#60032442\" ],\n"+
					"\"deptid\": \"104652099\",\n"+
					"\"dept_display_name\": \"Universidad Nacional Autonoma de Mexico, Institute of Chemistry\",\n"+
					"\"dept_city\": \"Mexico City\",\n"+
					"\"dept_country\": \"Mexico\"\n"+
				"}\n"+
			"}\n"+
		"}";

				
		Index index = new Index.Builder(esDocument).index("cafe").type("author").id("aut_M22aaa18f155dfa29a2bM7f9b10178163171").build();
		client.execute(index);
			
	}
	
	public String buildESQuery(String value) 
	{	
		JSONObject query = new JSONObject();
		JSONObject queryString = new JSONObject();
		queryString.put("queryString", value);
		queryString.put("defaultOperator", "AND");
		query.put("query",queryString);
		
		JSONArray returnFields = new JSONArray();
		returnFields.add("eidocid");
		query.put("returnFields", returnFields);
		

		JSONObject result = new JSONObject();
		result.put("skip", 0);
		result.put("amount", 0);
		query.put("resultSet", result);
		
		return query.toJSONString();
			
	}
	
	@SuppressWarnings("unchecked")
	public String buildESQueryFacet(String value, String searchField, String prefix) {
		
		JSONObject query = new JSONObject();
		JSONObject queryString = new JSONObject();
		JSONArray facets = new JSONArray();
		JSONObject mainfacet = new JSONObject();
	
		
		queryString.put("queryString", "database:cpx AND " + searchField + ":" + prefix + "*");
		
		JSONObject facet = new JSONObject();
		
		facet.put("fieldName", searchField);
		facet.put("type", "composite");
		facet.put("includeMissing", true);
		
		JSONObject resultSet = new JSONObject();
		if(value.equals("0"))
			resultSet.put("skip", 0);		/*0 has to be numeric otherwise ShApi would return 400 RC*/
		else
			resultSet.put("skip",value);
		resultSet.put("amount", 1000);
		
		mainfacet.put("label","document count by author");
		mainfacet.put("facet",facet);
		mainfacet.put("resultSet",resultSet);

		facets.add(mainfacet);
		
		query.put("query", queryString);
		query.put("facets",facets);
		
		return query.toJSONString();
	
			
		}	
	
	private void testURL(String inputValue) throws Exception
	{
			//String encodedQuery;
			//String esUrl = "https://shared-search-service-api.prod.scopussearch.net/document/v1/query/result";
			//String esUrl = "https://shared-search-service-api.staging.scopussearch.net/document/v1/query/result";
			//String esUrl = "https://shared-search-service-api.cert.scopussearch.net/sharedsearch/document/facets";
			String esUrl = "https://shared-search-service-api.staging.scopussearch.net/sharedsearch/document/result";
			URL urlObject;
			String result = null;
			String prefix ="99";
			String query=buildESQuery(inputValue);
			//String query=buildESQueryFacet("100","authorId",prefix);
			System.out.println("ESQuery: " + query);
			try
			{
								
				long startTime = System.currentTimeMillis();
								
				urlObject = new URL(esUrl);
				HttpURLConnection httpCon = (HttpURLConnection) urlObject.openConnection();
				httpCon.setRequestMethod("POST");
				httpCon.setRequestProperty("x-els-product", "engineering_village");
				httpCon.setRequestProperty("x-els-diagnostics", "false");
				httpCon.setRequestProperty("Content-Type", "application/json");
				httpCon.setDoOutput(true);
				//logger.info("before outputstreamwriter....");
				OutputStreamWriter writer = new OutputStreamWriter(httpCon.getOutputStream());
				//logger.info("after outputstreamwriter....");
				writer.write(query);
				writer.close();
				int responseCode = httpCon.getResponseCode();
				
				//logger.info("responseCode: " + responseCode);
				// Only read response if connection was successful
				if(responseCode == HttpURLConnection.HTTP_OK)
				{
					BufferedReader in = new BufferedReader(new InputStreamReader(httpCon.getInputStream()));
					String line;
					StringBuilder response = new StringBuilder();
					while((line = in.readLine()) != null)
					{
						response.append(line);
						//System.out.println("response="+line);
						// Response Is to big , to speedup the process only read up to hits 
						//if(line.equalsIgnoreCase("hits"))
							//break;
					}
					System.out.println("Response: " + response.toString());   // only for debugging
					// close stream
					in.close();
					
					long finishTime = System.currentTimeMillis();
					System.out.println("Time to runQuery: " + (finishTime - startTime));
					
					// process response and fetch HitCount from result
					
						System.out.println("ProcessResponse start.....");
						//result = processFacetResponse(response.toString(), prefix);
						int totalHit = processResponse(inputValue,response.toString());
						System.out.println("***"+inputValue + "\t" + totalHit +" ***");   // only for debugging	
						System.out.println("ProcessResponse finish.....");
				}
			}
			catch(Exception e)
			{								
				e.printStackTrace();			

			}		
	}
	
	private int processResponse(String value, String response) throws ParseException, IOException 
	{
		int hitCount=0;
		if(!(response.toString().isEmpty()))
		{
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(response);
			hitCount = Integer.parseInt(json.get("totalResultsCount").toString());
						
		}
		return hitCount;
		
	}

	private String processFacetResponse(String response, String prefix) throws ParseException, IOException {
		String after = "";
		String count = null;

		if (response!=null && response.length()>0) {
			JSONParser parser = new JSONParser();
			JSONObject json = (JSONObject) parser.parse(response);
			JSONArray facetResults = (JSONArray) json.get("facetResults");
			if (!facetResults.isEmpty()) {
				@SuppressWarnings("unchecked")
				ListIterator<JSONObject> facetItr = facetResults.listIterator();
				if (facetItr != null) {
					JSONObject att = facetItr.next();
					if(att != null && att.get("after") != null)
					{
						after = att.get("after").toString();
						count = att.get("count").toString();
						System.out.println("after="+after+" count= "+count);
						if (att.get("facetItems") != null) {
							JSONArray facetItems = (JSONArray) att.get("facetItems");
							@SuppressWarnings("unchecked")
							ListIterator<JSONObject> itr = facetItems.listIterator();
							while (itr.hasNext()) {
								JSONObject item = itr.next();
								if (item.containsKey("count") && item.containsKey("value")) {
									/* As per Hawk info, only filter ids start with prefix for having right count*/
									if(item.get("value").toString().startsWith(prefix))
									{
										System.out.println(item.get("value") + "\t" + item.get("count"));
									
									}
									
								}
							}
						}
					}
					
				}

			}
			else
				System.out.println("Fetched all IDS, no more scrolls!");
		}
		return after;

	}
	
	 private void testSearchElasticSearch() throws Exception { 
		 /*
	        String query = "{\n" 
	                + "    \"query\": {\n" 
	                + "        \"filtered\" : {\n" 
	                + "            \"query\" : {\n" 
	                + "                \"query_string\" : {\n" 
	                + "                    \"query\" : \"java\"\n" 
	                + "                }\n" 
	                + "            }" 
	                + "        }\n" 
	                + "    }\n" 
	                + "}"; 
	      */
		 String query = "{\n" 
	                + "    \"query\": {\n" 
	                + "    \"match_all\": {}\n "   
	                + "    }\n" 
	                + "}"; 		
			 //String endpoint = "http://search-author-5qvbvgnktqru5apydu24nu3jmm.us-east-1.es.amazonaws.com";
			 String endpoint = "https://shared-search-service-api.cert.scopussearch.net/document/v1/query/result";
			//String endpoint = "http://search-evcafeauaf-v6tfjfyfj26rtoneh233lzzqtq.us-east-1.es.amazonaws.com:80";
			JestClientFactory factory = new JestClientFactory();
			factory.setHttpClientConfig(new HttpClientConfig
					.Builder(endpoint)
					.multiThreaded(true)
					.build()
					);
			
			JestClient client = factory.getObject();
	        //Search.Builder searchBuilder = new Search.Builder(query).addIndex("cafe").addType("author"); 
			//Search.Builder searchBuilder = new Search.Builder(query).addIndex("author").addType("author");
			Search.Builder searchBuilder = new Search.Builder(query);
	        //io.searchbox.core.SearchResult result = client.execute(searchBuilder.build()); 
	        JestResult jsonResult = client.execute(searchBuilder.build());
	        //System.out.println(jsonResult.getSourceAsString());
	        com.google.gson.JsonObject jsonResultObject = jsonResult.getJsonObject();
	        if(jsonResultObject!=null)
	        {
	        	//String[] jKeys = jsonResult.getKeys();
	        	//List<String> jList = jsonResult.getSourceAsStringList();
	        	//for(int i=0;i<jKeys.length;i++)
	        	String jString =jsonResult.getJsonString();
	        	System.out.println(jString);
	        	/*
	        	for(int i=0;i<jList.size();i++)
	        	{
	        		System.out.println(jList.get(i));
	        	}
	        	*/
		        com.google.gson.JsonArray indexArray = null;
		        if(jsonResultObject.get("audoc")!=null)
		        {
		        	indexArray = jsonResultObject.get("audoc").getAsJsonArray();
		        }
		        else if(indexArray==null && jsonResultObject.get("_index")!=null)
		        {
		        	indexArray = jsonResultObject.get("_index").getAsJsonArray();
		        }
		        else if(indexArray==null && jsonResultObject.get("_source")!=null)
		        {
		        	indexArray = jsonResultObject.get("_source").getAsJsonArray();
		        }
		        else if(indexArray==null && jsonResultObject.get("_type")!=null)
		        {
		        	indexArray = jsonResultObject.get("_type").getAsJsonArray();
		        }
		        else if(indexArray==null && jsonResultObject.get("_id")!=null)
		        {
		        	indexArray = jsonResultObject.get("_id").getAsJsonArray();
		        }
		        else
		        {
		        	System.out.println("nothing found");
		        	return;
		        }
		        
		        for (int i=0;i<indexArray.size();i++) {
		        	JsonElement a = indexArray.get(i);
		        	
		            //String type = a.get(0).getAsString();
		          
			    	System.out.println(a.getAsString());		          
			    	//System.out.println(result.getString("loadnumber", ""));		          
			    }
	        }
	        else
	        {
	        	System.out.println("jsonResultObject is null");
	        }
	        
	        /*
	        List resultList = jsonResult.getSourceAsStringList();
	        for(int i=0;i<resultList.size();i++)
	        {
	        	System.out.println(i+" = "+resultList.get(i));
	        }
	        
	        javax.json.JsonObject jsonResultObject = jsonResult.getJsonObject();
	        
	        JsonObject jsonResultObject = (JsonObject) new JsonParser().parse(jsonResult.getJsonString());
	        JsonReader rdr = jsonResultObject.createReader(); 
			 
		    //JsonObject obj = rdr.readObject();
		    //JsonArray results = obj.getJsonArray("evrecords");
		    JsonArray results = rdr.readArray();
		    for (JsonObject result : results.getValuesAs(JsonObject.class)) {
		    	System.out.print(result.getString("_id")+"\t");		          
		    	System.out.println(result.getString("loadnumber", ""));		          
		    }
	        */
	        //System.out.println("search result is " + jsonResultString);
	       
	    } 
	
	private void getCPCDescription(String filename)
	{
		try{

			BufferedReader in = new BufferedReader(new FileReader(new File(filename)));
			String line=null;
			DiskMap readMap = new DiskMap();
			
			while((line=in.readLine())!=null)
			{
				//System.out.println("LINE1 = "+line);
				readMap.openRead("cpc", false);
				if(line.length()>4)
				{				
					line = line.substring(0,4).trim()+line.substring(4).trim();					
				}
				//System.out.println("LINE2 ="+line);
			    String val = readMap.get(line);
			    if(val==null || val.equals("null") || val.length()<5) 
			    {
			    	System.out.println("CODE= "+line);
			    }
			    else
			    {
			    	System.out.println("CODE= "+line+" description= "+val);
			    }
			    readMap.close();
			}
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void removeInvalidChar(String filename)
	{
		try{

			BufferedReader in = new BufferedReader(new FileReader(new File(filename)));
			FileWriter out = new FileWriter(filename+".out");
			String line=null;
			DiskMap readMap = new DiskMap();
			
			while((line=in.readLine())!=null)
			{
				line= removeLineInvalidCharacters(line);
				out.write(line+"\n");
			}
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private String removeLineInvalidCharacters(String text)
	{
		if (null == text || text.isEmpty()) {
		    return text;
		}
		final int len = text.length();
		char current = 0;
		int codePoint = 0;
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < len; i++) {
		    current = text.charAt(i);
		    boolean surrogate = false;
		    if (Character.isHighSurrogate(current)
		            && i + 1 < len && Character.isLowSurrogate(text.charAt(i + 1))) {
		        surrogate = true;
		        codePoint = text.codePointAt(i++);
		    } else {
		        codePoint = current;
		    }
		    if ((codePoint == 0x9) || (codePoint == 0xA) || (codePoint == 0xD)
		            || ((codePoint >= 0x20) && (codePoint <= 0xD7FF))
		            || ((codePoint >= 0xE000) && (codePoint <= 0xFFFD))
		            || ((codePoint >= 0x10000) && (codePoint <= 0x10FFFF))) {
		        sb.append(current);
		        if (surrogate) {
		            sb.append(text.charAt(i));
		        }
		    }
		}
		return sb.toString();
	}
	
	private void getAllIndexes(String index) throws Exception
	{
		FileWriter out = null;
		String key = null;
		String value = null;
		//private IndexReader reader;
				
		try
		{
			/*
			if (!inMemory) 
			{
			    this.reader = IndexReader.open(dir);
			} else {
			    this.reader = IndexReader.open(new RAMDirectory(dir));
			}
			*/
			System.out.println("INDEX="+index);
			DiskMap readMap = new DiskMap();
			readMap.openRead(index, false);
			HashMap hm = readMap.getAllDocs();
			System.out.println("INDEX SIZE="+hm.size());
			/*
			HashMap hm = new HashMap(); 
		    
	    	TermDocs td = reader.termDocs();
	    	while (td.next()) {
	            int docIndex = td.doc();
	            Document doc = reader.document(docIndex);
	            info = doc.get("VALUE");
	            key = doc.get("KEY");
	            System.out.println(docIndex+"\t"+key+"\t"+info+"\n");
	        }
	        */
			Iterator<String> itr=hm.keySet().iterator();
			while(itr.hasNext())
			{  
				key = itr.next();
				value = (String)hm.get(key);
				
				out = new FileWriter("cpc_codes_with_description.txt");
				out.write(key+"\t"+value+"\n");
				out.flush();
				
			}
			readMap.close();
			if(out!=null)
			out.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(out != null) 
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
	}
	
	private void checkCharacter()
	{
		Statement stmt = null;
		ResultSet rs = null;
		Connection con = null;
		FileWriter out = null;
		FileWriter out1 = null;
		String sqlQuery = "select granttext from bd_master where accessnumber='20192607122436'";
		try
		{

			con = getConnection(this.URL,this.driver,this.username,this.password);
			stmt = con.createStatement();
			LinkedHashSet<String> al=new LinkedHashSet<String>();  
			StringBuffer buffer = new StringBuffer();
			rs = stmt.executeQuery(sqlQuery);
			while (rs.next())
			{
				String grant = rs.getString("granttext");
				char[] arrChars = grant.toCharArray();

		        int i = 0;

		        for (int j = 0; j < arrChars.length; j++) 
		        {
		            i = (int) arrChars[j];

		            if (i >= 1 && i <= 126) {

		                buffer.append(arrChars[j]);

		            } else {
		            	if('�'==arrChars[j] || '�'==arrChars[j])
		            	{
		            		System.out.println("found "+i+" and translated to "+"\"");
		            	}
		            	else if(i==8221 || i==8220)
		            	{
		            		System.out.println("found2 "+i+" and translated to "+"\"");
		            	}
		            	
		            	
		            	buffer.append("***"+arrChars[j]+"***\n");
		            	buffer.append("*** "+i+" ***");
		            }
		        }
		        System.out.println(buffer.toString());
			}
			
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(out != null) {
				try {
					out.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(out1 != null) {
				try {
					out1.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (rs != null) {
				try {
					rs.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (stmt != null) {
				try {
					stmt.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
						
		}
		
	}
	
	private void checkStringCharacter(String input)
	{
		DataLoadDictionary dictionary = new DataLoadDictionary();
		try
		{

			if(input!=null)
			{
				char[] arrChars = input.toCharArray();

		        int i = 0;
		        System.out.println("original String="+input);
		        for (int j = 0; j < arrChars.length; j++) 
		        {
		            System.out.println("Char("+i+") "+ (int) arrChars[j]);

		            i++;
		        }
		        System.out.println("converted html String="+StringEscapeUtils.escapeHtml4(input));
		        System.out.println("converted java String="+StringEscapeUtils.escapeJava(input));
		        System.out.println("converted XML String="+StringEscapeUtils.escapeXml11(input));
		        System.out.println("converted JSON String="+StringEscapeUtils.escapeJson(input));
		        System.out.println("converted  String="+dictionary.mapEntity(input));
			}
			else
			{
				System.out.println("input string is empty");
			}
			
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
				
	}
			
	
	private void getAllCodes()
	{
		Statement stmt = null;
		ResultSet rs = null;
		Connection con = null;
		FileWriter out = null;
		FileWriter out1 = null;
		String sqlQuery = "select CLASSIFICATION_CPC,fec,ecl,ecc,esc from upt_master";
		try
		{

			con = getConnection(this.URL,this.driver,this.username,this.password);
			stmt = con.createStatement();
			LinkedHashSet<String> al=new LinkedHashSet<String>();  
			
			rs = stmt.executeQuery(sqlQuery);
			while (rs.next())
			{
				String fec = rs.getString("fec");
				if(fec!=null)
				{
					String[] fecValues = fec.split(Constants.AUDELIMITER);
					//String[] fecValues = removeSpaces(convert2Array(Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(rs.getString("fec"))))));
					for(int i=0;i<fecValues.length;i++)
					{
						al.add(fecValues[i]);  
					}
				}
				
				String ecl = rs.getString("ecl");
				if(ecl!=null)
				{
					String[] eclValues = ecl.split(Constants.AUDELIMITER);
					//String[] eclValues = removeSpaces(convert2Array(Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(rs.getString("ecl"))))));
					for(int i=0;i<eclValues.length;i++)
					{
						al.add(eclValues[i]);  
					}
				}
				
				String ecc = rs.getString("ecc");
				if(ecc!=null)
				{
					String[] eccValues = ecc.split(Constants.AUDELIMITER);
					//String[] eccValues = removeSpaces(convert2Array(Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(rs.getString("ecc"))))));
					for(int i=0;i<eccValues.length;i++)
					{
						al.add(eccValues[i]);  
					}
				}
				
				String esc = rs.getString("esc");				
				if(esc!=null)
				{
					String[] escValues = esc.split(Constants.AUDELIMITER);
					//String[] escValues = removeSpaces(convert2Array(Entity.replaceUTFString(Entity.prepareString(replaceAmpersand(rs.getString("esc"))))));
					for(int i=0;i<escValues.length;i++)
					{
						al.add(escValues[i]);  
					}
				}
				
				String cpc = rs.getString("CLASSIFICATION_CPC");
				if(cpc!=null)
				{
					String[] cpcValues = cpc.split(Constants.IDDELIMITER);
					for(int i=0;i<cpcValues.length;i++)
					{
						al.add(cpcValues[i]);  
					}
				}
			  
			}
			
			Iterator<String> itr=al.iterator();  
			
			out = new FileWriter("cpc_codes_with_description.txt");
			out1 = new FileWriter("cpc_codes_without_description.txt");
			String term = null;
			DiskMap readMap = new DiskMap();
						
			while(itr.hasNext())
			{  
				term = itr.next();
				readMap.openRead("cpc", false);	
				String val = readMap.get(term);
				readMap.close();
				if(val!=null)
				{
					out.write(itr.next()+"\t"+val+"\n");  
					
				}
				else 
				{
					readMap.openRead("ecla", false);	
					String val1 = readMap.get(term);
					readMap.close();
					if(val!=null)
					{
						out.write(itr.next()+"\t"+val1+"\n");  
					}
					else
					{
						out1.write(itr.next()+"\n");
					}
					
				}
				out.flush();
				out1.flush();
			}  
			
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(out != null) {
				try {
					out.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			if(out1 != null) {
				try {
					out1.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (rs != null) {
				try {
					rs.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (stmt != null) {
				try {
					stmt.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
						
		}
		
	}

	public String replaceNull(String sVal) {

        if (sVal == null)
            sVal = "";

        return sVal;
    }
	
	public String replaceAmpersand(String sVal) 
	{
		Perl5Util perl = new Perl5Util();
        if (sVal == null)
            return "";

        if (perl.match("/&amp;/i", sVal)) {
            sVal = perl.substitute("s/&amp;/&/ig", sVal);
        }

        return sVal;
    }
	 public String[] removeSpaces(String[] arrCodes) 
	 {
		Perl5Util perl = new Perl5Util();
		
        for (int i = 0; i < arrCodes.length; i++) {
            String code = arrCodes[i];
            code = perl.substitute("s/\\s+//", code);
            /*
            if (perl.match("/\\/\\//", code))
                code = perl.substitute("s/\\/\\//\\//", code);
        
            if (perl.match("/\\./", code))
                code = perl.substitute("s/\\./PERIOD/ig", code);

            if (perl.match("/\\//", code))
                code = perl.substitute("s/\\//SLASH/ig", code);
			*/
            arrCodes[i] = code;
        }

	    return arrCodes;
	 }
	 
	 public String[] convert2Array(String sVal) 
	 {
		 	Perl5Util perl = new Perl5Util();
		 	char DELIM = (char) 30;
	    	String[] arrVals = null;
	    	if(sVal!=null)
	    	{
		        List values = new ArrayList();
		
		        perl.split(values, "/" + DELIM + "/", sVal);
		
		        arrVals = (String[]) values.toArray(new String[1]);
		
		        arrVals[0] = replaceNull(arrVals[0]);
	    	}
	    	else
	    	{
	    		arrVals = new String[1];
	    		arrVals[0] = "";
	    	}

	        return arrVals;
	    }
	 
	private void getWeeklyCount(String weekNumber)
	{
		System.out.println("***************** Record Count for Week "+weekNumber+" ******************");
		System.out.println("DATABASE\tDB COUNT\tFAST COUNT");
		String[] databaseArray = {"cpx","chm","geo","ins","cbn","grf","ntis","elt","upa","pch","ept","eup"};
		for (int i=0;i<databaseArray.length;i++)
		{
			String database = databaseArray[i];
			int databaseCount=getDatabaseCount(database,weekNumber);
			String fastCount = getFastCount(database,weekNumber);
			System.out.println(database+"\t\t"+databaseCount+"\t\t"+fastCount);
		}
		System.out.println("******************************************************************");
	}
	
	private void convertInspecNI(String weekNumber)
	{
		Statement stmt = null;
		ResultSet rs = null;
		Connection con = null;
		String sqlQuery = null;
		String m_id = null;
		String accessnumber = null;
		String load_number = null;
		String ndi = null;
		InspecBaseTableWriter inspec = new InspecBaseTableWriter("inspecNumerical_"+weekNumber,"XML");

		try
		{

			con = getConnection(this.URL,this.driver,this.username,this.password);
			stmt = con.createStatement();
			List loadNumberList = new ArrayList();
			if(weekNumber.equals("0"))
			{
				sqlQuery="select distinct load_number from ins_master_orig";
				rs = stmt.executeQuery(sqlQuery);
				while (rs.next())
				{	
					if(rs.getString("load_number")!=null)				
					loadNumberList.add(rs.getString("load_number"));
				}
			}
			else
			{
				loadNumberList.add(weekNumber);
			}
			int k = 0;
			for(int i=0;i<loadNumberList.size();i++)
			{
				sqlQuery="select m_id,anum,load_number,ndi from ins_master_orig where load_number='"+(String)loadNumberList.get(i)+"'";															
				System.out.println("QUERY= "+sqlQuery);
				stmt = con.createStatement();
				rs = stmt.executeQuery(sqlQuery);
				
				while (rs.next())
				{
					m_id = rs.getString("m_id");
					accessnumber = rs.getString("anum");
					load_number = rs.getString("load_number");
					ndi = rs.getString("ndi");
					if(ndi!=null)
					{
						k++;
						inspec.outPutNumericalIndex(m_id,accessnumber,load_number,ndi);
					}
				}
				
				if(stmt!=null)
				{
					stmt.close();
				}
			}
			System.out.println("total count= "+k);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if (rs != null) {
				try {
					rs.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (stmt != null) {
				try {
					stmt.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		
	}

	private int getDatabaseCount(String database,String weekNumber)
	{
		Statement stmt = null;
		ResultSet rs = null;
		Connection con = null;
		String sqlQuery = null;
		int count = 0;


		try
		{

			con = getConnection(this.URL,this.driver,this.username,this.password);
			stmt = con.createStatement();
			if(database!=null)
			{
				if(database.equals("cpx") || database.equals("chm") || database.equals("elt") || database.equals("geo") || database.equals("pch"))
				{
					sqlQuery="select count(*) count from bd_master where database='"+database+"' and loadnumber='"+weekNumber+"'";
				}
				else if(database.equals("ins"))
				{
					sqlQuery="select count(*) count from new_ins_master where load_number='"+weekNumber+"'";
				}
				else if(database.equals("cbn"))
				{
					sqlQuery="select count(*) count from cbn_master where load_number='"+weekNumber+"'";
				}
				else if(database.equals("grf"))
				{
					sqlQuery="select count(*) count from georef_master where load_number='"+weekNumber+"'";
				}
				else if(database.equals("ntis"))
				{
					sqlQuery="select count(*) count from ntis_master where load_number='"+weekNumber+"'";
				}
				else if(database.equals("upa"))
				{
					sqlQuery="select count(*) count from upt_master where ac='US' and load_number='"+weekNumber+"'";
				}
				else if(database.equals("eup"))
				{
					sqlQuery="select count(*) count from upt_master where ac!='US' and load_number='"+weekNumber+"'";
				}
				else if(database.equals("ept"))
				{
					sqlQuery="select count(*) count from ept_master where load_number='"+weekNumber+"'";
				}
				else
				{
					System.out.println("DATABASE "+database+" NOT FOUND");
					System.exit(1);
				}
			}
			else
			{
				System.out.println("DATABASE CAN NOT BE NULL");
				System.exit(1);
			}

			//System.out.println("QUERY= "+sqlQuery);
			rs = stmt.executeQuery(sqlQuery);
			int k = 0;
			while (rs.next())
			{
				count = rs.getInt("count");
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return count;
	}

	private String getFastCount(String database,String loadNumber)
		{
			List outputList = new ArrayList();
			DatabaseConfig databaseConfig = null;

			String[] credentials = {"CPX","CHM","GEO","INS","CBN","GRF","NTIS","ELT","UPA","PCH","EPT","EUP"};

			String[] dbName = database.substring(0,3).split(";");
			//FastSearchControl.BASE_URL = "http://evdr09.cloudapp.net:15100"; //DR
			FastSearchControl.BASE_URL = "http://evazure.trafficmanager.net:15100"; //Production
			//System.out.println("database= "+database);

			//int intDbMask = 1;

			String term1 = loadNumber;
			String searchField="WK";

			try
			{
				databaseConfig = DatabaseConfig.getInstance(DriverConfig.getDriverTable());
				int intDbMask = databaseConfig.getMask(dbName);
				SearchControl sc = new FastSearchControl();
				//int oc = Integer.parseInt((String)inputMap.get(term1));
				org.ei.domain.Query queryObject = new org.ei.domain.Query(databaseConfig, credentials);
				queryObject.setDataBase(intDbMask);

				String searchID = (new GUID()).toString();
				queryObject.setID(searchID);
				queryObject.setSearchType(org.ei.domain.Query.TYPE_QUICK);

				queryObject.setSearchPhrase("{"+term1+"}",searchField,"","","","","","");
				queryObject.setSearchQueryWriter(new FastQueryWriter());
				queryObject.compile();
				//queryObject.setSearchQuery("(wk:"+term1+") and (db:grf)");
				queryObject.setSearchQuery("(wk:"+term1+") and (db:"+database+")");
				System.out.println("DISPLAYQUERY= "+queryObject.getDisplayQuery()+" PhysicalQuery= "+queryObject.getPhysicalQuery()+" SEARCHQUERY= "+queryObject.getSearchQuery());

				String sessionId = null;
				int pagesize = 25;
				org.ei.domain.SearchResult result = sc.openSearch(queryObject,sessionId,pagesize,false);
				int c = result.getHitCount();
				if(c > 0)
					return Integer.toString(c);
				else
					return "0";
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

			return "1";
		}


	private void getInvalidYearData(String database)
	{
		Statement stmt = null;
		ResultSet rs = null;
		Connection con = null;
		String sqlQuery = null;
		List<String>  midList = new ArrayList<String>();
		List<String>  yearList = new ArrayList<String>();

		try
		{

			con = getConnection(this.URL,this.driver,this.username,this.password);
			stmt = con.createStatement();
			if(database.equals("cbn"))
			{
				sqlQuery="select distinct pyr year from cbn_master";
			}
			System.out.println("QUERY= "+sqlQuery);
			rs = stmt.executeQuery(sqlQuery);

			while (rs.next())
			{
				String year = rs.getString("year");
				if(!validYear(year))
				{
					yearList.add(year);
				}
			}
			rs.close();

			for(int i=0;i<yearList.size();i++)
			{
				String year = yearList.get(i);
				year = year.replace("'","''");
				sqlQuery="select m_id  from cbn_master where pyr='"+year+"'";
				System.out.println("QUERY= "+sqlQuery);
				rs = stmt.executeQuery(sqlQuery);

				while (rs.next())
				{
					String mid = rs.getString("m_id");
					System.out.println(mid);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();

		}
		finally {

			if (rs != null) {
				try {
					rs.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (stmt != null) {
				try {
					stmt.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	 private boolean validYear(String year)
	{
		Perl5Util perl = new Perl5Util();
		if (year == null)
		{
			return false;
		}

		if (year.length() != 4)
		{
			return false;
		}

		if(!perl.match("/[1-9][0-9][0-9][0-9]/", year))
		{
			return false;
		}

		if(Integer.parseInt(year)<1980 || Integer.parseInt(year)>2014)
		{
			return false;
		}

		return true;
    }

	private void checkDetailRecord(String database,String updateNumber)
	{
		//Hashtable loadnumberFromDatabase = new Hashtable();
		//loadnumberFromDatabase.put("201402","100");
		
		FileWriter out = null;
		List detailRecordFromDatabase = getDetailRecordFromDatabase(database,updateNumber);
			
		try
		{
			out = new FileWriter("midFromFast_DR.out");
			for(int i=0;i<detailRecordFromDatabase.size();i++)
			{
				String mid = (String)detailRecordFromDatabase.get(i);
				int count = 0;	
				String searchQuery="db:grf all:"+mid;
				//System.out.println("Query= "+searchQuery);
				FastClient client = new FastClient();
				//client.setBaseURL("http://evprod08.cloudapp.net:15100");DEV server
				client.setBaseURL("http://evdr09.cloudapp.net:15100"); //DR				
				client.setResultView("ei");
				client.setOffSet(0);
				client.setPageSize(300000);
				client.setQueryString(searchQuery);
				client.setDoCatCount(true);
				client.setDoNavigators(true);
				client.setPrimarySort("ausort");
				client.setPrimarySortDirection("+");
				client.search();
	
				List l = client.getDocIDs();
				 count =client.getHitCount();
				
				if(count>0)
				{
					out.write(mid+"\n");
				}		
					
				out.flush();
				
	
			}
			out.flush();
			out.close();
		}
		catch(Exception e)
		{
			try{
			if(out!=null)
				out.close();
			}
			catch(Exception e1)
			{
				e1.printStackTrace();
			}
			e.printStackTrace();
		}

	}

	private List getDetailRecordFromDatabase(String database,String loadnumber)
	{
		Statement stmt = null;
		ResultSet rs = null;
		Connection con = null;
		String sqlQuery = null;
		List  midList = new ArrayList();		

		try
		{

			con = getConnection(this.URL,this.driver,this.username,this.password);
			stmt = con.createStatement();
			//sqlQuery="select m_id from "+username+"."+tableName+" where database='"+database+"' and loadnumber='"+loadnumber+"'";
			sqlQuery="select m_id from ap_correction1.georef_master_GI_extra";
			System.out.println("QUERY= "+sqlQuery);
			rs = stmt.executeQuery(sqlQuery);
			int k = 0;
			while (rs.next())
			{
				String mid = rs.getString("m_id");

				midList.add(mid);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return midList;
	}

	private void checkLoadNumber(String database)
	{
		//Hashtable loadnumberFromDatabase = new Hashtable();
		//loadnumberFromDatabase.put("201402","100");

		Hashtable loadnumberFromDatabase = getLoadnumberFromDatabase(database);

		Enumeration e = loadnumberFromDatabase.keys();
		while(e.hasMoreElements())
		{
			String loadNumber = (String)e.nextElement();
			String count = "0";

			if(loadnumberFromDatabase.containsKey(loadNumber))
			{
				count = (String)loadnumberFromDatabase.get(loadNumber);
			}
			int loadNumberCountFromFast=getCountFromFast(loadNumber,database);
			System.out.println(loadNumber+"\t\t"+count+"\t"+loadNumberCountFromFast);
		}

	}
	
	
	
	private void getMIDFromFastQuery(String query)
	{

		Statement stmt = null;
		ResultSet rs = null;
		Connection con = null;
		FileWriter out = null;
		query = "spa7:a* or spa7:b* or spa7:c* or spa7:d* or spa7:e* or spa7:f* or spa7:g* or spa7:h* or spa7:i* or spa7:j* or spa7:k* or spa7:l* or spa7:m* or spa7:n* or spa7:o* or spa7:p* or spa7:q* or spa7:r* or spa7:s* or spa7:t* or spa7:u* or spa7:v* or spa7:w* or spa7:x* or spa7:y* or spa7:z* or spa7:0* or spa7:1* or spa7:2* or spa7:3* or spa7:4* or spa7:5* or spa7:6* or spa7:7* or spa7:8* or spa7:9*";
		String searchQuery = query.replaceAll("__", " and ");
		try
		{
			out = new FileWriter("midFromFast_PROD.out");	
			for(int j=201817;j<201838;j++)
			{
				System.out.println("Query= "+query);
				FastClient client = new FastClient();
				client.setBaseURL("http://evdr09.cloudapp.net:15100");//PROD
				//client.setBaseURL("http://evprod08.cloudapp.net:15100");//DEV server
				//client.setBaseURL("http://evdr09.cloudapp.net:15100"); //DR			
				client.setResultView("ei");
				client.setOffSet(0);
				client.setPageSize(260000);
				client.setQueryString(searchQuery+" and wk:"+j);
				client.setDoCatCount(true);
				client.setDoNavigators(true);
				client.setPrimarySort("ausort");
				client.setPrimarySortDirection("+");
				client.search();
				
				
	
				List l = client.getDocIDs();
				int count =client.getHitCount();
				Thread.sleep(100);
				
				if(count<1)
				{
				  System.out.println("0 records found");
			    }
				else
				{
					System.out.println(count+" records found");
					System.out.println("SIZE= "+l.size());
				}
	
				StringBuffer sb=new StringBuffer();
				
				for(int i=0;i<l.size();i++)
				{
					String[] docID = (String[])l.get(i);
					String m_id = docID[0];
					//System.out.println(m_id);
					
					out.write(m_id+"\n");					
					out.flush();					
				}
					
				out.flush();
			}
			out.close();

		}
		catch(Exception e)
		{
			try{
			if(out!=null)
				out.close();
			}
			catch(Exception e1)
			{
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		
	}
	
	private void getMIDFromFastQuery_DR(String query)
	{

		Statement stmt = null;
		ResultSet rs = null;
		Connection con = null;
		FileWriter out = null;
		String searchQuery = query.replaceAll("__", " and ");
		
		try
		{
			out = new FileWriter("midFromFast_DR.out");	
			System.out.println("Query= "+searchQuery);
			FastClient client = new FastClient();
			//client.setBaseURL("http://evprod08.cloudapp.net:15100");DEV server
			client.setBaseURL("http://evdr09.cloudapp.net:15100"); //DR				
			client.setResultView("ei");
			client.setOffSet(0);
			client.setPageSize(300000);
			client.setQueryString(searchQuery);
			client.setDoCatCount(true);
			client.setDoNavigators(true);
			client.setPrimarySort("ausort");
			client.setPrimarySortDirection("+");
			client.search();

			List l = client.getDocIDs();
			int count =client.getHitCount();
			
			if(count>0)
			{
				System.out.println(count+" records found");
			}

			StringBuffer sb=new StringBuffer();
			
			for(int i=0;i<l.size();i++)
			{
				String[] docID = (String[])l.get(i);
				String m_id = docID[0];
				System.out.println(m_id);
				
				out.write(m_id+"\n");					
				out.flush();					
			}
				
			out.flush();
			out.close();

		}
		catch(Exception e)
		{
			try{
			if(out!=null)
				out.close();
			}
			catch(Exception e1)
			{
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		
	}
	
	private void getMIDFromFastQuery_DEV(String query)
	{

		Statement stmt = null;
		ResultSet rs = null;
		Connection con = null;
		FileWriter out = null;
		String searchQuery = query.replaceAll("_", " and ");
		try
		{
			out = new FileWriter("midFromFast_DEV.out");	
			System.out.println("Query= "+searchQuery);
			FastClient client = new FastClient();
			//client.setBaseURL("http://evprod08.cloudapp.net:15100");DEV server
			client.setBaseURL("http://evprod08.cloudapp.net:15100"); //DEV				
			client.setResultView("ei");
			client.setOffSet(0);
			client.setPageSize(200000);
			client.setQueryString(searchQuery);
			client.setDoCatCount(true);
			client.setDoNavigators(true);
			client.setPrimarySort("ausort");
			client.setPrimarySortDirection("+");
			client.search();

			List l = client.getDocIDs();
			int count =client.getHitCount();
			
			if(count<1)
			{
			  System.out.println("0 records found");
		    }
			else
			{
				System.out.println(count+" records found");
			}

			StringBuffer sb=new StringBuffer();
			
			for(int i=0;i<l.size();i++)
			{
				String[] docID = (String[])l.get(i);
				String m_id = docID[0];
				System.out.println(m_id);
				
				out.write(m_id+"\n");					
				out.flush();					
			}
				
			out.flush();
			out.close();

		}
		catch(Exception e)
		{
			try{
			if(out!=null)
				out.close();
			}
			catch(Exception e1)
			{
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		
	}
	
	private void checkFASTFORMID(String tableName)
	{

		Statement stmt = null;
		ResultSet rs = null;
		Connection con = null;
		FileWriter out = null;
		try
		{
			out = new FileWriter("CHECK_EPT_IN_FAST.out");
			con = getConnection(this.URL,this.driver,this.username,this.password);			
			String sqlQuery="select m_id from "+tableName;
			System.out.println("QUERY= "+sqlQuery);
			stmt = con.createStatement();
			rs = stmt.executeQuery(sqlQuery);
			
			
			int k = 0;			
			while (rs.next())
			{
				String m_id = rs.getString("m_id");
				FastClient client = new FastClient();
				client.setBaseURL("http://evazure.trafficmanager.net:15100");
				client.setResultView("ei");
				client.setOffSet(0);
				client.setPageSize(60000);
				client.setQueryString(m_id);
				client.setDoCatCount(true);
				client.setDoNavigators(true);
				client.setPrimarySort("ausort");
				client.setPrimarySortDirection("+");
				client.search();
	
				List l = client.getDocIDs();
				int count =client.getHitCount();
				
				if(count<1)
				{
				  System.out.println("0 records found for "+m_id);
			    }
				else
				{
					out.write(m_id+"\n");
					System.out.println(count+" records found for"+m_id);
				}
			}		
				
			out.flush();
			out.close();

		}
		catch(Exception e)
		{
			try{
			if(out!=null)
				out.close();
			}
			catch(Exception e1)
			{
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
		finally {

			if (rs != null) {
				try {
					rs.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}	

			if (stmt != null) {
				try {
					stmt.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}

		}
	}
	
	private void getAllMIDFromFastCPXIP_PROD()
	{
	
		FileWriter out = null;
		try
		{
			out = new FileWriter("CPX_IP_PROD.out");
		
			int k = 0;			

			FastClient client = new FastClient();
			client.setBaseURL("http://evazure.trafficmanager.net:15100");
			client.setResultView("ei");
			client.setOffSet(0);
			client.setPageSize(260000);
			client.setQueryString("(DT:\"IP\") AND (((db:cpx)))");
			client.setDoCatCount(true);
			client.setDoNavigators(true);
			client.setPrimarySort("ausort");
			client.setPrimarySortDirection("+");
			client.search();

			List l = client.getDocIDs();
			int count =client.getHitCount();
			
			if(count<1)
			{
			  System.out.println("0 records found");
		    }
			else
			{
				System.out.println(count+" records found");
			}

			StringBuffer sb=new StringBuffer();
			
			for(int i=0;i<l.size();i++)
			{
				String[] docID = (String[])l.get(i);
				String m_id = docID[0];				
				out.write(m_id+"\n");
			}
				
			out.flush();
			out.close();

		}
		catch(Exception e)
		{
			try{
			if(out!=null)
				out.close();
			}
			catch(Exception e1)
			{
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}
	
	private void getAllMIDFromFastCPXIP_DR()
	{
	
		FileWriter out = null;
		try
		{
			out = new FileWriter("CPX_IP_DR.out");
			
			
			int k = 0;			

			FastClient client = new FastClient();
			client.setBaseURL("http://evdr09.cloudapp.net:15100");
			client.setResultView("ei");
			client.setOffSet(0);
			client.setPageSize(260000);
			client.setQueryString("(DT:\"IP\") AND (((db:cpx)))");
			client.setDoCatCount(true);
			client.setDoNavigators(true);
			client.setPrimarySort("ausort");
			client.setPrimarySortDirection("+");
			client.search();

			List l = client.getDocIDs();
			int count =client.getHitCount();
			
			if(count<1)
			{
			  System.out.println("0 records found");
		    }
			else
			{
				System.out.println(count+" records found");
			}

			StringBuffer sb=new StringBuffer();
			
			for(int i=0;i<l.size();i++)
			{
				String[] docID = (String[])l.get(i);
				String m_id = docID[0];				
				out.write(m_id+"\n");
			}
				
			out.flush();
			out.close();

		}
		catch(Exception e)
		{
			try{
			if(out!=null)
				out.close();
			}
			catch(Exception e1)
			{
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}

	private int getCountFromFast(String mid,String database)
	{
		List outputList = new ArrayList();
		DatabaseConfig databaseConfig = null;
		String[] credentials = new String[]{"CPX","INS","EPT","EUP","UPA","GRF","CBN","NTIS","WOP","KNC","KNA"};
		String[] dbName = database.split(";");
		//FastSearchControl.BASE_URL = "http://ei-stage.nda.fastsearch.net:15100";
		FastSearchControl.BASE_URL = "http://evazure.trafficmanager.net:15100";

		//int intDbMask = databaseConfig.getMask(dbName);
		int intDbMask = 1;

		String term1 = mid;
		String searchField="ALL";

		try
		{
			databaseConfig = DatabaseConfig.getInstance(DriverConfig.getDriverTable());
			SearchControl sc = new FastSearchControl();
			//int oc = Integer.parseInt((String)inputMap.get(term1));
			org.ei.domain.Query queryObject = new org.ei.domain.Query(databaseConfig, credentials);
			queryObject.setDataBase(intDbMask);

			String searchID = (new GUID()).toString();
			queryObject.setID(searchID);
			queryObject.setSearchType(org.ei.domain.Query.TYPE_QUICK);

			queryObject.setSearchPhrase("{"+term1+"}",searchField,"","","","","","");
			queryObject.setSearchQueryWriter(new FastQueryWriter());
			queryObject.compile();
			queryObject.setSearchQuery("(wk:"+term1+") and (db:"+database+")");
			//System.out.println("DISPLAYQUERY= "+queryObject.getDisplayQuery()+" PhysicalQuery= "+queryObject.getPhysicalQuery()+" SEARCHQUERY= "+queryObject.getSearchQuery());

			String sessionId = null;
			int pagesize = 25;
			org.ei.domain.SearchResult result = sc.openSearch(queryObject,sessionId,pagesize,false);
			int c = result.getHitCount();
			//System.out.println(term1+"\t"+c);
			if(c > 0)
				return c;
			else
				return 0;
		}
		catch(Exception e)
		{
			System.out.println("term1= "+term1);
			e.printStackTrace();
		}

		return 0;
	}
	
	private void checkColumnSize()
	{
		BufferedReader in=null;
		String line=null;
		try
		{
			in = new BufferedReader(new FileReader("test.txt"));
			while((line=in.readLine())!=null)
			{
				String[] cArray = line.split("\t");
				for(int i=0;i<cArray.length;i++)
				{
					System.out.println(cArray[i].length()+" "+cArray[i].getBytes().length+" "+cArray[i]);
				}
			}
			
		}
		catch(Exception e)
		{
			try{
			if(in!=null)
				in.close();
			}
			catch(Exception e1)
			{
				e1.printStackTrace();
			}
			e.printStackTrace();
		}
	}

	private void getMIDFromFast(String load_number)
	{

		Statement stmt = null;
		ResultSet rs = null;
		Connection con = null;
		FileWriter out = null;
		try
		{			
			con = getConnection(this.URL,this.driver,this.username,this.password);
			//String sqlQuery = "select id_number,m_id from georef_master where load_number='"+load_number+"' and document_type='GI'";
			//String sqlQuery = "select m_id,id_number from georef_master_orig where id_number in (select id_number from georef_master_delete)";
			//String sqlQuery = "select id_number,m_id from georef_master where document_type='GI'";
			String sqlQuery = "select INSTITUTE_NAME,dbase from aflookup_with_bracket order by dbase";
			stmt = con.createStatement();

			System.out.println("QUERY= "+sqlQuery);
			rs = stmt.executeQuery(sqlQuery);
			int k = 0;
			while (rs.next())
			{
				//Thread.currentThread().sleep(250);
				String instituteName = rs.getString("INSTITUTE_NAME");
				String database = rs.getString("dbase");


				//in = new BufferedReader(new FileReader("test.txt"));
				FastClient client = new FastClient();
				client.setBaseURL("http://evazure.trafficmanager.net:15100");
				client.setResultView("ei");
				client.setOffSet(0);
				client.setPageSize(50000);
				client.setQueryString("(af:\""+instituteName+"\") AND (((db:"+database+")))");
				//client.setQueryString("(ALL:\""+m_id+"\") and db:grf and dt:gi and wk:"+load_number);
				
				client.setDoCatCount(true);
				client.setDoNavigators(true);
				client.setPrimarySort("ausort");
				client.setPrimarySortDirection("+");
				client.search();

				List l = client.getDocIDs();
				int count =client.getHitCount();

				if(count<1)
				{
				  System.out.println(instituteName);
			    }
				else
				{
					System.out.println("***KEEP="+instituteName);
				}

				StringBuffer sb=new StringBuffer();
			}
			

		}
		catch(Exception e)
		{
			try{
			if(out!=null)
				out.close();
			}
			catch(Exception e1)
			{
				e1.printStackTrace();
			}
			e.printStackTrace();
		}

	}
	
	private void getALLCPXMIDFromFast(String database)
	{

		FileWriter out = null;
		try
		{
			out = new FileWriter("unitsearch.txt");
							
			FastClient client = new FastClient();
			//client.setBaseURL("http://evprod08.cloudapp.net:15100");
			client.setBaseURL("http://evazure.trafficmanager.net:15100");
			client.setResultView("ei");
			client.setOffSet(0);
			client.setPageSize(50000);
			//client.setQueryString("db:"+database);
			client.setQueryString("fastloadunit:1557416848624_cpx_add_1-0001");
			client.setDoCatCount(true);
			client.setDoNavigators(true);
			client.setPrimarySort("ausort");
			client.setPrimarySortDirection("+");
			client.search();

			List l = client.getDocIDs();
			int count =client.getHitCount();

			if(count<1)
			{
			  System.out.println("No records found in fast");
		    }

			StringBuffer sb=new StringBuffer();
			
			for(int i=0;i<l.size();i++)
			{
				String[] docID = (String[])l.get(i);				
				
				//System.out.println("docID="+docID[0]);
				out.write(docID[0]+"\n");
				
				out.flush();
				
			}		
			out.flush();
			out.close();

		}
		catch(Exception e)
		{
			try{
			if(out!=null)
				out.close();
			}
			catch(Exception e1)
			{
				e1.printStackTrace();
			}
			e.printStackTrace();
		}

	}

	private void getAccessnumber(String inQuery)
	{
		Statement stmt = null;
		ResultSet rs = null;
		Connection con = null;


		try
		{

			con = getConnection(this.URL,this.driver,this.username,this.password);
			String sqlQuery = "select accessnumber from bd_master where m_id in ("+inQuery.substring(0,inQuery.length()-1)+")";
			stmt = con.createStatement();

			System.out.println("QUERY= "+sqlQuery);
			rs = stmt.executeQuery(sqlQuery);
			int k = 0;
			while (rs.next())
			{
				String accessnumber = rs.getString("accessnumber");

				System.out.println(accessnumber);
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

    private void getYearCountFromFast(String year,String database)
	{
		List outputList = new ArrayList();
		DatabaseConfig databaseConfig = null;
		String[] credentials = new String[]{"CPX","INS","EPT","EUP","UPA","C84"};
		String[] dbName = database.split(";");
		FastSearchControl.BASE_URL = "http://ei-main.nda.fastsearch.net:15100";
		String[] yearCount = new String[]{"1000","1003","1018","1039","1042","1043","1047","1051","1052","1059","1065","1153","1494","1495","1590","1592","1593","1597","1643","1659","1798","1800","1802","1804","1885","1806","1807","1808","1809","1811","1813","1820","1830","1831","1838","1855","1856","1857","1858","1860","1863","1864","1865","1867","1868","1869","1870","1871","1872","1873","1874","1875","1876","1877","1878","1879","1880","1881","1882","1883","1884","1885","1886","1887","1888","1889","189","1890","1891","1892","1893","1894","1895","1896","1897","1898","1899","1900","1901","1902","1903","1904","1905","1906","1907","1908","1909","1910","1911","1912","1913","1914","1915","1916","1917","1918","1919","1920","1921","1922","1923","1924","1925","1926","1927","1928","1929","1929s","1930","1931","1931`","1932","1933","1934","1935","1936","1937","1938","1939","1940","1941","1942","1943","1944","1945","1946","1947","1948","1949","194O","1950","1951","1952","1953","1954","1955","1956","1957","1958","1959","1960","1960c","1961","1962","1962A","1963","1964","1965","1966","1967","1967;1968","1967-1968","1967-68","1968","1968;1966","1968;1967","1968;1968","1968-1968","1968-1969","1968-1971","1969","1969;1969","1970","1971","1972","1973","1974","1975","1976","1977","1977-1978","1977-1980","1978","1978-1979","1979","1979-1980","1979-1981","1979-1989","1980","1980-1981","1980-1982","1981","1981-1982","1981-1983","1982","1982-1983","1983","1983-1984","1984","1984-1985","1985","1985-1986","1986","1986-1987","1987","1987-1988","1988","1988-1989","1989","1989-190","1989-1989","1989-1990","1989-1993","199","1990","1990-1991","1991","1991-1991","1991-1992","1992","1992-1993","1992-1994","1993","1993-1994","1993-1995","1993-94","1994","1994-1995","1995","1995-1996","1995-96","1996","1996-1997","1997","1997-1998","1997-2003","1997-98","1998","1998-1999","1998-99","1999","1999-2000","2000","2000-2001","2001","2001-1993","2001-2002","2002","2002-2003","2003","2003-2004","2003-2006","2004","2004-2005","2005","2005-2006","2006","2006-2007","2007","2007-2008","2008","2008-1996","2008-2009","2008-2010","2008;2008","2009","2009-2010","2010","2010-2011","2011","2011-2012","2012","2012-2013","2013","2013-2014","2014","2015","21NE","25En","524T","5Eng","6TOC","820C","7953"};
		//int intDbMask = databaseConfig.getMask(dbName);
		int intDbMask = 1;

		String term1 = year;
		String searchField="WK";

		try
		{
			for( int i=0;i<yearCount.length;i++)
			{
				//term1=i+"";
				term1=yearCount[i];
				databaseConfig = DatabaseConfig.getInstance(DriverConfig.getDriverTable());
				SearchControl sc = new FastSearchControl();
				//int oc = Integer.parseInt((String)inputMap.get(term1));
				org.ei.domain.Query queryObject = new org.ei.domain.Query(databaseConfig, credentials);
				queryObject.setDataBase(intDbMask);

				String searchID = (new GUID()).toString();
				queryObject.setID(searchID);
				queryObject.setSearchType(org.ei.domain.Query.TYPE_QUICK);

				queryObject.setSearchPhrase("{"+term1+"}",searchField,"","","","","","");
				queryObject.setSearchQueryWriter(new FastQueryWriter());
				queryObject.compile();
				//queryObject.setSearchQuery("(yr:"+term1+") and (db:"+database+")");
				queryObject.setSearchQuery("(yr:"+term1+") and ((db:cpx) or (db:c84))");
				System.out.println("DISPLAYQUERY= "+queryObject.getDisplayQuery()+" PhysicalQuery= "+queryObject.getPhysicalQuery()+" SEARCHQUERY= "+queryObject.getSearchQuery());

				String sessionId = null;
				int pagesize = 25;
				org.ei.domain.SearchResult result = sc.openSearch(queryObject,sessionId,pagesize,false);
				int c = result.getHitCount();
				System.out.println(term1+"\t"+c);
				/*
				if(c > 0)
					return Integer.toString(c);
				else
					return "0";
					*/
			}
		}
		catch(Exception e)
		{
			System.out.println("term1= "+term1);
			e.printStackTrace();
		}

		//return "0";
	}

	private String getLNCountFromFast(String loadNumber,String database)
	{
		List outputList = new ArrayList();
		DatabaseConfig databaseConfig = null;
		String[] credentials = new String[]{"CPX","INS","EPT","EUP","UPA","CBN","GRF"};
		String[] dbName = database.split(";");
		FastSearchControl.BASE_URL = "http://ei-stage.nda.fastsearch.net:15100";

		//int intDbMask = databaseConfig.getMask(dbName);
		int intDbMask = 1;

		String term1 = loadNumber;
		String searchField="WK";

		try
		{
			databaseConfig = DatabaseConfig.getInstance(DriverConfig.getDriverTable());
			SearchControl sc = new FastSearchControl();
			//int oc = Integer.parseInt((String)inputMap.get(term1));
			org.ei.domain.Query queryObject = new org.ei.domain.Query(databaseConfig, credentials);
			queryObject.setDataBase(intDbMask);

			String searchID = (new GUID()).toString();
			queryObject.setID(searchID);
			queryObject.setSearchType(org.ei.domain.Query.TYPE_QUICK);

			queryObject.setSearchPhrase("{"+term1+"}",searchField,"","","","","","");
			queryObject.setSearchQueryWriter(new FastQueryWriter());
			queryObject.compile();
			//queryObject.setSearchQuery("(wk:"+term1+") and (db:grf)");
			queryObject.setSearchQuery("(wk:"+term1+") and ((db:upa) or (db:eup))");
			System.out.println("DISPLAYQUERY= "+queryObject.getDisplayQuery()+" PhysicalQuery= "+queryObject.getPhysicalQuery()+" SEARCHQUERY= "+queryObject.getSearchQuery());

			String sessionId = null;
			int pagesize = 25;
			org.ei.domain.SearchResult result = sc.openSearch(queryObject,sessionId,pagesize,false);
			int c = result.getHitCount();
			if(c > 0)
				return Integer.toString(c);
			else
				return "0";
		}
		catch(Exception e)
		{
			System.out.println("term1= "+term1);
			e.printStackTrace();
		}

		return "0";
	}


	private Hashtable getLoadnumberFromDatabase(String database)
	{
		Statement stmt = null;
		ResultSet rs = null;
		Connection con = null;
		String sqlQuery = null;
		Hashtable<String, String> loadNumberCount = new Hashtable<String, String>();

		try
		{

			con = getConnection(this.URL,this.driver,this.username,this.password);
			stmt = con.createStatement();
			//sqlQuery="select count(*) count,loadnumber from "+username+"."+tableName+" where database='"+database+"' group by loadnumber";
			if(database.equals("upt"))
			{
				sqlQuery="select count(*) count,load_number from upt_master  group by load_number";
			}
			else if(database.equals("grf"))
			{
				sqlQuery="select count(*) count,load_number from georef_master  group by load_number";
			}
			else if(database.equals("cpx"))
			{
				sqlQuery="select count(*) count,load_number from bd_master where database='cpx' group by load_number";
			}
			//sqlQuery="select count(*) count,loadnumber from "+username+"."+tableName+" where database='"+database+"' group by loadnumber";
			//sqlQuery="select count(*) count,load_number from ept_master group by load_number";
			//sqlQuery="select count(*) count,loadnumber from bd_master where database='geo' group by loadnumber";
			//sqlQuery="select count(*) count,load_number from db_georef.georef_master group by load_number";
			System.out.println("QUERY= "+sqlQuery);
			rs = stmt.executeQuery(sqlQuery);
			int k = 0;
			while (rs.next())
			{
				String loadnumber = rs.getString("load_number");
				String count = rs.getString("count");
				if(loadnumber!=null && count!=null)
				{
					loadNumberCount.put(loadnumber,count);
				}
				else
				{
					System.out.println("loadnumber= "+loadnumber+" count= "+count);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return loadNumberCount;
	}


	public void checkFast(String file, String file1) throws Exception
	{
		try{

			BufferedReader in = new BufferedReader(new FileReader(new File(file)));
			BufferedReader in1 = new BufferedReader(new FileReader(new File(file1)));
			String line=null;
			String line1=null;
			System.out.println("FILE1= "+file+" FILE2= "+file1);
			while((line=in.readLine())!=null && (line1=in1.readLine())!=null)
			{
				if(!line.equals(line1))
				{
					System.out.println("LINE ::"+line);
					System.out.println("LINE1::"+line1);
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

	}

	public void getData(String database) throws Exception
	{
		Statement stmt = null;
		Statement stmt1 = null;
		ResultSet rs = null;
		ResultSet rs1 = null;
		Connection con = null;
		List columnNameList = getColumnName(database);
		String pui;
		String accessnumber = null;
		String pn;
		String ac;
		String kc;
		String sqlQuery=null;
		String sqlQuery1=null;
		HashMap testData = new HashMap();
		HashMap origData = new HashMap();

		try
		{

			con = getConnection(this.URL,this.driver,this.username,this.password);
			stmt = con.createStatement();
			stmt1 = con.createStatement();
			System.out.println("action= "+action+" and database= "+database);
			if(action.equals("bd_aip")||action.equals("bd_correction"))
			{
				sqlQuery="select * from "+username+"."+tableName+" where database='"+database+"' and updatenumber='"+updateNumber+"'";
			}
			else if(action.equals("bd_loading") && (database.equals("cpx") || database.equals("pch") ||database.equals("geo") ||database.equals("elt") ||database.equals("chm")))
			{
				sqlQuery="select * from "+username+"."+tableName+" where database='"+database+"' and updatenumber='"+updateNumber+"'";
			}
			else if(action.trim().equals("bd_loading") && (database.trim().equals("ins") || database.equals("grf") || database.equals("ntis") || database.equals("ept") || database.equals("cbn")))
			{
				sqlQuery="select * from "+username+"."+tableName+" where updatenumber='"+updateNumber+"'";
			}
			else if(action.equals("bd_loading") && (database.equals("upt")))
			{
				sqlQuery="select * from "+username+"."+tableName1+" where load_number='"+updateNumber+"'";
			}
			else if(action.equals("correction") && (database.equals("grf")))
			{
				sqlQuery="select * from "+username+"."+tableName+" where updatenumber='"+updateNumber+"'";
			}
			System.out.println("QUERY= "+sqlQuery);
			System.out.println("ACCESSNUMBER\tCOLUMN NAME\tNEW DATA\tOLD DATA");
			rs = stmt.executeQuery(sqlQuery);

			int k = 0;
			while (rs.next())
			{

				if(action.equals("bd_loading") && (database.equals("cpx") || database.equals("pch") ||database.equals("geo") ||database.equals("elt") ||database.equals("chm")))
				{
					accessnumber = rs.getString("accessnumber");
					//System.out.println("accessnumber= "+accessnumber);
					sqlQuery1="select * from "+username1+"."+tableName1+ " where database='"+database+"' and accessnumber='"+accessnumber+"'";
				}
				else if(action.equals("bd_loading") && database.equals("ins"))
				{
					accessnumber = rs.getString("anum");
					sqlQuery1="select * from "+username1+"."+tableName1+ " where anum='"+accessnumber+"'";
				}
				else if(action.equals("bd_loading") && (database.equals("ntis")))
				{
					accessnumber = rs.getString("an");
					sqlQuery1="select * from "+username1+"."+tableName1+ " where an='"+accessnumber+"'";
				}
				else if(action.equals("bd_loading") && (database.equals("ept")))
				{
					accessnumber = rs.getString("dn");
					sqlQuery1="select * from "+username1+"."+tableName1+ " where dn='"+accessnumber+"'";
				}
				else if(action.equals("bd_loading") && (database.equals("grf")))
				{
					accessnumber = rs.getString("id_number");
					sqlQuery1="select * from "+username1+"."+tableName1+ " where id_number='"+accessnumber+"'";
				}
				else if(action.equals("bd_loading") && (database.equals("cbn")))
				{
					accessnumber = rs.getString("abn");
					sqlQuery1="select * from "+username1+"."+tableName1+ " where abn='"+accessnumber+"'";
				}
				else if(action.equals("bd_loading") && (database.equals("upt")))
				{
					pn = rs.getString("pn");
					ac = rs.getString("ac");
					kc = rs.getString("kc");
					sqlQuery1="select * from "+username1+"."+tableName+ " where pn='"+pn.trim()+"' and ac='"+ac.trim()+"' and kc='"+kc.trim()+"'" ;
				}
				else if((action.equals("bd_aip")||action.equals("bd_correction") ) && (database.equals("cpx")))
				{
					accessnumber = rs.getString("accessnumber");
					sqlQuery1="select * from "+username1+"."+tableName1+ " where database='cpx' and accessnumber='"+accessnumber+"'";
				}
				else if(action.equals("correction") && (database.equals("grf")))
				{
					accessnumber = rs.getString("id_number");
					sqlQuery1="select * from "+username1+"."+tableName1+ " where id_number='"+accessnumber+"'";
				}
				//System.out.println("QUERY1= "+sqlQuery1);
				rs1 = stmt1.executeQuery(sqlQuery1);


				while (rs1.next())
				{
					testData = setData(rs,database);
					origData = setData(rs1,database);
					compare(testData,origData,database);
				}
				if(rs1!=null)
				{
					try{
						rs1.close();
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally {

			if (rs != null) {
				try {
					rs.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (rs1 != null) {
				try {
					rs1.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}

			if (stmt != null) {
				try {
					stmt.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

	}

	private void compare(HashMap testData,HashMap origData, String database) throws Exception
	{
		List columnNameList = getColumnName(database);
		String columnName = "";
		String value = "";
		String value1= "";
		String accessnumber="";
		for(int i=0;i<columnNameList.size();i++)
		{
			if(database.equals("grf")){
				accessnumber = (String)testData.get("ID_NUMBER");
			}
			else if(database.equals("ins"))
			{
				accessnumber = (String)testData.get("ANUM");
			}
			else
			{
				accessnumber = (String)testData.get("ACCESSNUMBER");
			}
			columnName = (String)columnNameList.get(i);
			value = (String)testData.get(columnName);
			value1= (String)origData.get(columnName);
			if(!columnName.equals("UPDATECODESTAMP") && !columnName.equals("UPDATERESOURCE") && !columnName.equals("UPDATENUMBER")
			   && !columnName.equals("UPDATETIMESTAMP") && !columnName.equals("UPDATEFLAG"))
			{
				//System.out.println("COLUMMN::"+columnName+" test::"+value+" orig::"+value1);
				if(value!=null && value1!=null && !value.trim().equals(value1.trim()))
				{
					//if(!columnName.equals("M_ID"))
					{
						System.out.println(accessnumber+"\t"+columnName+"\t"+value+"\t"+value1);
						//System.out.println("COLUMMN::"+columnName+" test::"+value);
						//System.out.println("COLUMMN::"+columnName+" orig::"+value1);
					}
				}
				else if((value!=null && value1==null) || (value==null && value1!=null))
				{
					System.out.println(accessnumber+"\t"+columnName+"\t"+value+"\t"+value1);
					//System.out.println("NULL::COLUMN:: "+columnName+" test::"+value);
					//System.out.println("NULL::COLUMN:: "+columnName+" orig::"+value1);
				}
			}

		}
	}

	HashMap setData(ResultSet rs, String database) throws Exception
	{
		List columnNameList = getColumnName(database);
		String columnName = "";
		HashMap resultMap = new HashMap();
		for(int i=0;i<columnNameList.size();i++)
		{
			columnName = (String)columnNameList.get(i);
			//System.out.println("columnName= "+columnName);
			if((database.equals("cbn") && !columnName.equals("ABS")) || (!database.equals("cbn") && !columnName.equals("ABSTRACTDATA")))
			{

				resultMap.put(columnName,rs.getString(columnName));
			}
			else
			{
				resultMap.put(columnName,getAbstract(database,rs));
			}
		}

		return resultMap;
	}

	 private String getAbstract(String database, ResultSet rs) throws Exception
	 {
		String abs = null;
		Clob clob = null;
		if(database.equals("cbn"))
		{
			clob = rs.getClob("ABS");
		}
		else
		{
		 	clob = rs.getClob("ABSTRACTDATA");
		}
		if(clob != null)
		{
			abs = StringUtil.getStringFromClob(clob);

		}
		return abs;

    }

    private List getColumnName(String database)
    {
		if(database.equals("cpx") || database.equals("pch") || database.equals("elt") || database.equals("geo") || database.equals("chm"))
		{
			return getBDColumnName();
		}
		else if(database.equals("ins"))
		{
			return getINSColumnName();
		}
		else if(database.equals("ept"))
		{
			return getEPTColumnName();
		}
		else if(database.equals("grf"))
		{
			return getGRFColumnName();
		}
		else if(database.equals("ntis"))
		{
			return getNTISColumnName();
		}
		else if(database.equals("cbn"))
		{
			return getCBNColumnName();
		}
		else if(database.equals("upt"))
		{
			return getUPTColumnName();
		}
		return null;
	}

	private List getGRFColumnName()
	{

	 	List columnNameList = new ArrayList();
	 	columnNameList.add("M_ID");
	 	columnNameList.add("ISSN");
	 	columnNameList.add("EISSN");
	 	columnNameList.add("CODEN");
	 	columnNameList.add("TITLE_OF_SERIAL");
	 	columnNameList.add("VOLUME_ID");
	 	columnNameList.add("ISSUE_ID");
	 	columnNameList.add("OTHER_ID");
	 	columnNameList.add("TITLE_OF_ANALYTIC");
	 	columnNameList.add("TITLE_OF_MONOGRAPH");
	 	columnNameList.add("TITLE_OF_COLLECTION");
	 	columnNameList.add("PERSON_ANALYTIC");
	 	columnNameList.add("PERSON_MONOGRAPH");
	 	columnNameList.add("PERSON_COLLECTION");
	 	columnNameList.add("ALTERNATE_AUTHOR");
	 	columnNameList.add("AUTHOR_AFFILIATION");
	 	columnNameList.add("AUTHOR_AFFILIATION_ADDRESS");
	 	columnNameList.add("AUTHOR_AFFILIATION_COUNTRY");
	 	columnNameList.add("AUTHOR_EMAIL");
	 	columnNameList.add("CORPORATE_BODY_ANALYTIC");
	 	columnNameList.add("CORPORATE_BODY_MONOGRAPH");
	 	columnNameList.add("CORPORATE_BODY_COLLECTION");
	 	columnNameList.add("COLLATION_ANALYTIC");
	 	columnNameList.add("COLLATION_COLLECTION");
	 	columnNameList.add("COLLATION_MONOGRAPH");
	 	columnNameList.add("DATE_OF_PUBLICATION");
	 	columnNameList.add("OTHER_DATE");
	 	columnNameList.add("LANGUAGE_TEXT");
	 	columnNameList.add("LANGUAGE_SUMMARY");
	 	columnNameList.add("PUBLISHER");
	 	columnNameList.add("PUBLISHER_ADDRESS");
	 	columnNameList.add("ISBN");
	 	columnNameList.add("EDITION");
	 	columnNameList.add("NAME_OF_MEETING");
	 	columnNameList.add("LOCATION_OF_MEETING");
	 	columnNameList.add("DATE_OF_MEETING");
	 	columnNameList.add("REPORT_NUMBER");
	 	columnNameList.add("UNIVERSITY");
	 	columnNameList.add("TYPE_OF_DEGREE");
	 	columnNameList.add("AVAILABILITY");
	 	columnNameList.add("NUMBER_OF_REFERENCES");
	 	columnNameList.add("SUMMARY_ONLY_NOTE");
	 	columnNameList.add("LOAD_NUMBER");
	 	columnNameList.add("DOI");
	 	columnNameList.add("COPYRIGHT");
	 	columnNameList.add("ID_NUMBER");
	 	columnNameList.add("CATEGORY_CODE");
	 	columnNameList.add("DOCUMENT_TYPE");
	 	columnNameList.add("BIBLIOGRAPHIC_LEVEL_CODE");
	 	columnNameList.add("ABSTRACT");
	 	columnNameList.add("ANNOTATION");
	 	columnNameList.add("ILLUSTRATION");
	 	columnNameList.add("MAP_SCALE");
	 	columnNameList.add("MAP_TYPE");
	 	columnNameList.add("MEDIUM_OF_SOURCE");
	 	columnNameList.add("COORDINATES");
	 	columnNameList.add("AFFILIATION_SECONDARY");
	 	columnNameList.add("SOURCE_NOTE");
	 	columnNameList.add("COUNTRY_OF_PUBLICATION");
	 	columnNameList.add("UPDATE_CODE");
	 	columnNameList.add("INDEX_TERMS");
	 	columnNameList.add("UNCONTROLLED_TERMS");
	 	columnNameList.add("RESEARCH_PROGRAM");
	 	columnNameList.add("HOLDING_LIBRARY");
	 	columnNameList.add("URL");
	 	columnNameList.add("TARGET_AUDIENCE");

	 	return columnNameList;

	 }



	 private List getUPTColumnName()
	 {

	 	List columnNameList = new ArrayList();

		columnNameList.add("M_ID");
		columnNameList.add("PN");
		columnNameList.add("AC");
		columnNameList.add("KC");
		columnNameList.add("KD");
		columnNameList.add("TI");
		columnNameList.add("FRE_TI");
		columnNameList.add("GER_TI");
		columnNameList.add("LTN_TI");
		columnNameList.add("FD");
		columnNameList.add("FY");
		columnNameList.add("PD");
		columnNameList.add("PY");
		columnNameList.add("XPB_DT");
		columnNameList.add("DAN");
		columnNameList.add("DUN");
		columnNameList.add("AIN");
		columnNameList.add("AID");
		columnNameList.add("AIC");
		columnNameList.add("AIK");
		columnNameList.add("IPC");
		columnNameList.add("FIC");
		columnNameList.add("ICC");
		columnNameList.add("ISC");
		columnNameList.add("ECL");
		columnNameList.add("FEC");
		columnNameList.add("ECC");
		columnNameList.add("ESC");
		columnNameList.add("UCL");
		columnNameList.add("UCC");
		columnNameList.add("USC");
		columnNameList.add("FS");
		columnNameList.add("INV");
		columnNameList.add("INV_ADDR");
		columnNameList.add("INV_CTRY");
		columnNameList.add("ASG");
		columnNameList.add("ASG_ADDR");
		columnNameList.add("ASG_CTRY");
		columnNameList.add("ASG_CST");
		columnNameList.add("ATY");
		columnNameList.add("ATY_ADDR");
		columnNameList.add("ATY_CTRY");
		columnNameList.add("PE");
		columnNameList.add("AE");
		columnNameList.add("PI");
		columnNameList.add("DT");
		columnNameList.add("LA");
		columnNameList.add("PCT_PNM");
		columnNameList.add("PCT_ANM");
		columnNameList.add("DS");
		columnNameList.add("AB");
		columnNameList.add("OAB");
		columnNameList.add("REF_CNT");
		columnNameList.add("CIT_CNT");
		columnNameList.add("MD");
		columnNameList.add("LOAD_NUMBER");
		columnNameList.add("NP_CNT");
		columnNameList.add("UPDATE_NUMBER");
		columnNameList.add("IPC8");
		columnNameList.add("IPC8_2");


	  	return columnNameList;

	 }


	private List getINSColumnName(){

		List columnNameList = new ArrayList();
		 columnNameList.add("M_ID");
		 columnNameList.add("ANUM");
		 columnNameList.add("ASDATE");
		 columnNameList.add("ADATE");
		 columnNameList.add("RTYPE");
		 columnNameList.add("NRTYPE");
		 columnNameList.add("CPR");
		 columnNameList.add("SU");
		 columnNameList.add("TI");
		 columnNameList.add("AB");
		 columnNameList.add("CLS");
		 columnNameList.add("CVS");
		 columnNameList.add("FLS");
		 columnNameList.add("TRMC");
		 columnNameList.add("NDI");
		 columnNameList.add("CHI");
		 columnNameList.add("AOI");
		 columnNameList.add("PFLAG");
		 columnNameList.add("PJID");
		 columnNameList.add("PFJT");
		 columnNameList.add("PAJT");
		 columnNameList.add("PVOL");
		 columnNameList.add("PISS");
		 columnNameList.add("PVOLISS");
		 columnNameList.add("PIPN");
		 columnNameList.add("PSPDATE");
		 columnNameList.add("PEPDATE");
		 columnNameList.add("POPDATE");
		 columnNameList.add("PCPUB");
		 columnNameList.add("PCDN");
		 columnNameList.add("PSN");
		 columnNameList.add("PSICI");
		 columnNameList.add("PPUB");
		 columnNameList.add("PCCCC");
		 columnNameList.add("PUM");
		 columnNameList.add("PDNUM");
		 columnNameList.add("PDOI");
		 columnNameList.add("PURL");
		 columnNameList.add("PDCURL");
		 columnNameList.add("SJID");
		 columnNameList.add("SFJT");
		 columnNameList.add("SAJT");
		 columnNameList.add("SVOL");
		 columnNameList.add("SISS");
		 columnNameList.add("SVOLISS");
		 columnNameList.add("SIPN");
		 columnNameList.add("SSPDATE");
		 columnNameList.add("SEPDATE");
		 columnNameList.add("SOPDATE");
		 columnNameList.add("SCPUB");
		 columnNameList.add("SCDN");
		 columnNameList.add("SSN");
		 columnNameList.add("SSICI");
		 columnNameList.add("LA");
		 columnNameList.add("TC");
		 columnNameList.add("AC");
		 columnNameList.add("AUS");
		 columnNameList.add("AUS2");
		 columnNameList.add("EDS");
		 columnNameList.add("TRS");
		 columnNameList.add("ABNUM");
		 columnNameList.add("MATID");
		 columnNameList.add("SBN");
		 columnNameList.add("RNUM");
		 columnNameList.add("UGCHN");
		 columnNameList.add("CNUM");
		 columnNameList.add("PNUM");
		 columnNameList.add("OPAN");
		 columnNameList.add("PARTNO");
		 columnNameList.add("AMDREF");
		 columnNameList.add("CLOC");
		 columnNameList.add("BPPUB");
		 columnNameList.add("CIORG");
		 columnNameList.add("CCNF");
		 columnNameList.add("CPAT");
		 columnNameList.add("COPA");
		 columnNameList.add("PUBTI");
		 columnNameList.add("NPL1");
		 columnNameList.add("NPL2");
		 columnNameList.add("XREFNO");
		 columnNameList.add("AAFF");
		 columnNameList.add("AFC");
		 columnNameList.add("EAFF");
		 columnNameList.add("EFC");
		 columnNameList.add("PAS");
		 columnNameList.add("IORG");
		 columnNameList.add("SORG");
		 columnNameList.add("AVAIL");
		 columnNameList.add("PRICE");
		 columnNameList.add("CDATE");
		 columnNameList.add("CEDATE");
		 columnNameList.add("CODATE");
		 columnNameList.add("PYR");
		 columnNameList.add("FDATE");
		 columnNameList.add("OFDATE");
		 columnNameList.add("PPDATE");
		 columnNameList.add("OPPDATE");
		 columnNameList.add("LOAD_NUMBER");
		 columnNameList.add("NPSN");
		 columnNameList.add("NSSN");
		 columnNameList.add("SRTDATE");
		 columnNameList.add("VRN");
		 columnNameList.add("AAFFMULTI1");
		 columnNameList.add("AAFFMULTI2");
		 columnNameList.add("EAFFMULTI1");
		 columnNameList.add("EAFFMULTI2");
		 columnNameList.add("SEQ_NUM");
		 columnNameList.add("IPC");
		 columnNameList.add("UPDATECODESTAMP");
		 columnNameList.add("UPDATERESOURCE");
		 columnNameList.add("UPDATENUMBER");
		 columnNameList.add("UPDATETIMESTAMP");
		 columnNameList.add("UPDATEFLAG");
		 columnNameList.add("CITATION");

		return columnNameList;

	}

	private List getEPTColumnName(){

		List columnNameList = new ArrayList();
		 columnNameList.add("ID");
		 columnNameList.add("M_ID");
		 columnNameList.add("DN");
		 columnNameList.add("PAT_IN");
		 columnNameList.add("CS");
		 columnNameList.add("TI");
		 columnNameList.add("TI2");
		 columnNameList.add("PRI");
		 columnNameList.add("AJ");
		 columnNameList.add("AC");
		 columnNameList.add("AD");
		 columnNameList.add("AP");
		 columnNameList.add("PC");
		 columnNameList.add("PD");
		 columnNameList.add("PN");
		 columnNameList.add("PAT");
		 columnNameList.add("LA");
		 columnNameList.add("PY");
		 columnNameList.add("DS");
		 columnNameList.add("IC");
		 columnNameList.add("LL");
		 columnNameList.add("EY");
		 columnNameList.add("CC");
		 columnNameList.add("DT");
		 columnNameList.add("CR");
		 columnNameList.add("LTM");
		 columnNameList.add("ATM");
		 columnNameList.add("ATM1");
		 columnNameList.add("ALC");
		 columnNameList.add("AMS");
		 columnNameList.add("APC");
		 columnNameList.add("ANC");
		 columnNameList.add("AT_API");
		 columnNameList.add("CT");
		 columnNameList.add("CRN");
		 columnNameList.add("LT");
		 columnNameList.add("UT");
		 columnNameList.add("AB");
		 columnNameList.add("LOAD_NUMBER");

		return columnNameList;

	}


	private List getBDColumnName(){

		List columnNameList = new ArrayList();
		columnNameList.add("M_ID");
		columnNameList.add("ACCESSNUMBER");
		columnNameList.add("DATESORT");
		columnNameList.add("AUTHOR");
		columnNameList.add("AUTHOR_1");
		columnNameList.add("AFFILIATION");
		columnNameList.add("AFFILIATION_1");
		columnNameList.add("CODEN");
		columnNameList.add("ISSUE");
		columnNameList.add("CLASSIFICATIONCODE");
		columnNameList.add("CONTROLLEDTERM");
		columnNameList.add("UNCONTROLLEDTERM");
		columnNameList.add("MAINHEADING");
		columnNameList.add("SPECIESTERM");
		columnNameList.add("REGIONALTERM");
		columnNameList.add("TREATMENTCODE");
		columnNameList.add("LOADNUMBER");
		columnNameList.add("SOURCETYPE");
		columnNameList.add("SOURCECOUNTRY");
		columnNameList.add("SOURCEID");
		columnNameList.add("SOURCETITLE");
		columnNameList.add("SOURCETITLEABBREV");
		columnNameList.add("ISSUETITLE");
		columnNameList.add("ISSN");
		columnNameList.add("EISSN");
		columnNameList.add("ISBN");
		columnNameList.add("VOLUME");
		columnNameList.add("PAGE");
		columnNameList.add("PAGECOUNT");
		columnNameList.add("ARTICLENUMBER");
		columnNameList.add("PUBLICATIONYEAR");
		columnNameList.add("PUBLICATIONDATE");
		columnNameList.add("EDITORS");
		columnNameList.add("PUBLISHERNAME");
		columnNameList.add("PUBLISHERADDRESS");
		columnNameList.add("PUBLISHERELECTRONICADDRESS");
		columnNameList.add("REPORTNUMBER");
		columnNameList.add("CONFNAME");
		columnNameList.add("CONFCATNUMBER");
		columnNameList.add("CONFCODE");
		columnNameList.add("CONFLOCATION");
		columnNameList.add("CONFDATE");
		columnNameList.add("CONFSPONSORS");
		columnNameList.add("CONFERENCEPARTNUMBER");
		columnNameList.add("CONFERENCEPAGERANGE");
		columnNameList.add("CONFERENCEPAGECOUNT");
		columnNameList.add("CONFERENCEEDITOR");
		columnNameList.add("CONFERENCEORGANIZATION");
		columnNameList.add("CONFERENCEEDITORADDRESS");
		columnNameList.add("TRANSLATEDSOURCETITLE");
		columnNameList.add("VOLUMETITLE");
		columnNameList.add("CONTRIBUTOR");
		columnNameList.add("CONTRIBUTORAFFILIATION");
		columnNameList.add("COPYRIGHT");
		columnNameList.add("DOI");
		columnNameList.add("PII");
		columnNameList.add("PUI");
		columnNameList.add("ABSTRACTORIGINAL");
		columnNameList.add("ABSTRACTPERSPECTIVE");
		columnNameList.add("ABSTRACTDATA");
		columnNameList.add("CITTYPE");
		columnNameList.add("CORRESPONDENCENAME");
		columnNameList.add("CORRESPONDENCEAFFILIATION");
		columnNameList.add("CORRESPONDENCEEADDRESS");
		columnNameList.add("CITATIONTITLE");
		columnNameList.add("CITATIONLANGUAGE");
		columnNameList.add("AUTHORKEYWORDS");
		columnNameList.add("REFCOUNT");
		columnNameList.add("CHEMICALTERM");
		columnNameList.add("CASREGISTRYNUMBER");
		columnNameList.add("SEQUENCEBANKS");
		columnNameList.add("TRADENAME");
		columnNameList.add("MANUFACTURER");
		columnNameList.add("DATABASE");
		columnNameList.add("MEDIA");
		columnNameList.add("CSESS");
		columnNameList.add("PATNO");
		columnNameList.add("PLING");
		columnNameList.add("APPLN");
		columnNameList.add("PRIOR_NUM");
		columnNameList.add("ASSIG");
		columnNameList.add("PCODE");
		columnNameList.add("CLAIM");

		return columnNameList;

	}

	private List getCBNColumnName(){

	List columnNameList = new ArrayList();
	 columnNameList.add("ID");
	 columnNameList.add("M_ID");
	 columnNameList.add("ABN");
	 columnNameList.add("CDT");
	 columnNameList.add("DOC");
	 columnNameList.add("SCO");
	 columnNameList.add("FJL");
	 columnNameList.add("ISN");
	 columnNameList.add("CDN");
	 columnNameList.add("LAN");
	 columnNameList.add("VOL");
	 columnNameList.add("ISS");
	 columnNameList.add("IBN");
	 columnNameList.add("PBR");
	 columnNameList.add("PAD");
	 columnNameList.add("PAG");
	 columnNameList.add("PBD");
	 columnNameList.add("PBN");
	 columnNameList.add("SRC");
	 columnNameList.add("SCT");
	 columnNameList.add("SCC");
	 columnNameList.add("EBT");
	 columnNameList.add("CIN");
	 columnNameList.add("REG");
	 columnNameList.add("CYM");
	 columnNameList.add("SIC");
	 columnNameList.add("GIC");
	 columnNameList.add("GID");
	 columnNameList.add("ATL");
	 columnNameList.add("OTL");
	 columnNameList.add("EDN");
	 columnNameList.add("AVL");
	 columnNameList.add("CIT");
	 columnNameList.add("ABS");
	 columnNameList.add("PYR");
	 columnNameList.add("LOAD_NUMBER");



	 return columnNameList;

	}
	private void getCITCountFromFast(String loadnumber)
	{
		Statement stmt = null;
		Statement updateStmt = null;
		ResultSet rs = null;
		Connection con = null;		
		List outputList = new ArrayList();
		DatabaseConfig databaseConfig = null;
		String[] credentials = new String[]{"WOP","EUP","UPA"};
		String[] dbName = database.split(";");
		//FastSearchControl.BASE_URL = "http://ei-stage.nda.fastsearch.net:15100";
		FastSearchControl.BASE_URL = "http://evazure.trafficmanager.net:15100";
		String tableName="hmoPatCitCnt";
		//int intDbMask = databaseConfig.getMask(dbName);
		int intDbMask = 1;		
		String searchField="ALL";

		try
		{
			con = getConnection(this.URL,this.driver,"ap_correction1",this.password);
			String sqlQuery = "select CIT_MID,CIT_PN,CIT_AC,CIT_KC from "+tableName+" where MASTER_CIT_CNT!=COUNT";
			stmt = con.createStatement();
			updateStmt = con.createStatement();
			System.out.println("QUERY= "+sqlQuery);
			rs = stmt.executeQuery(sqlQuery);
			int i = 0;
			while (rs.next())
			{
				//Thread.currentThread().sleep(10);
				String mid = rs.getString("CIT_MID");
				String pn  = rs.getString("CIT_PN");
				String ac  = rs.getString("CIT_AC");
				String kc  = rs.getString("CIT_KC");
				FastClient client = new FastClient();
				client.setBaseURL("http://evazure.trafficmanager.net:15100");//PROD
				//client.setBaseURL("http://evprod08.cloudapp.net:15100");//DEV server
				//client.setBaseURL("http://evdr09.cloudapp.net:15100"); //DR			
				client.setResultView("ei");
				client.setOffSet(0);
				client.setPageSize(5);
				client.setQueryString("pci:"+ac+pn);
				client.setDoCatCount(true);
				//client.setDoNavigators(true);
				//client.setPrimarySort("ausort");
				client.setPrimarySortDirection("+");
				client.search();
				
				
	
				List l = client.getDocIDs();
				int count =client.getHitCount();
				
				if(count > 0)
				{
					String updateQuery = "update "+tableName+" set FAST_CIT_CNT="+count+ "where CIT_MID='"+mid+"'";
					updateStmt.addBatch(updateQuery);
					if(i==1000)
					{
						updateStmt.executeBatch();
						i=0;
					} 
					i++;
					System.out.println(mid+"   "+count);
				}
			}
			updateStmt.executeBatch();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		
	}
	
	private void getAuthorCountFromFast(String tableName)
	{
		Statement stmt = null;
		Statement updateStmt = null;
		ResultSet rs = null;
		Connection con = null;		
		List outputList = new ArrayList();
		DatabaseConfig databaseConfig = null;
		String[] credentials = new String[]{"CPX"};
		String[] dbName = database.split(";");
		//FastSearchControl.BASE_URL = "http://ei-stage.nda.fastsearch.net:15100";
		FastSearchControl.BASE_URL = "http://evazure.trafficmanager.net:15100";
		//String tableName="hmo_author_count";
		//int intDbMask = databaseConfig.getMask(dbName);
		int intDbMask = 1;		
		String searchField="ALL";

		try
		{
			con = getConnection(this.URL,this.driver,"DB_CAFE",this.password);
			String sqlQuery = "select authorid from "+tableName;
			stmt = con.createStatement();
			updateStmt = con.createStatement();
			System.out.println("QUERY= "+sqlQuery);
			rs = stmt.executeQuery(sqlQuery);
			int i = 0;
			while (rs.next())
			{
				//Thread.currentThread().sleep(10);
				String authorid = rs.getString("authorid");
				
				FastClient client = new FastClient();
				client.setBaseURL("http://evazure.trafficmanager.net:15100");//PROD
				//client.setBaseURL("http://evprod08.cloudapp.net:15100");//DEV server
				//client.setBaseURL("http://evdr09.cloudapp.net:15100"); //DR			
				client.setResultView("ei");
				client.setOffSet(0);
				client.setPageSize(5);
				client.setQueryString("auid:"+authorid);
				client.setDoCatCount(true);
				//client.setDoNavigators(true);
				//client.setPrimarySort("ausort");
				client.setPrimarySortDirection("+");
				client.search();
				
				
	
				List l = client.getDocIDs();
				int count =client.getHitCount();
				
				if(count > 0)
				{
					String updateQuery = "update "+tableName+" set fast_count="+count+ "where authorid='"+authorid+"'";
					updateStmt.addBatch(updateQuery);
					if(i==100)
					{
						updateStmt.executeBatch();
						i=0;
					} 
					i++;
					System.out.println(authorid+"   "+count);
				}
			}
			updateStmt.executeBatch();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		
	}
	
	private void getAFCountFromFast(String tableName)
	{
		Statement stmt = null;
		Statement updateStmt = null;
		ResultSet rs = null;
		Connection con = null;		
		List outputList = new ArrayList();
		DatabaseConfig databaseConfig = null;
		String[] credentials = new String[]{"CPX"};
		String[] dbName = database.split(";");
		//FastSearchControl.BASE_URL = "http://ei-stage.nda.fastsearch.net:15100";
		FastSearchControl.BASE_URL = "http://evazure.trafficmanager.net:15100";
		//String tableName="hmo_author_count";
		//int intDbMask = databaseConfig.getMask(dbName);
		int intDbMask = 1;		
		String searchField="ALL";

		try
		{
			con = getConnection(this.URL,this.driver,"DB_CAFE",this.password);
			String sqlQuery = "select affid from "+tableName;
			stmt = con.createStatement();
			updateStmt = con.createStatement();
			System.out.println("QUERY= "+sqlQuery);
			rs = stmt.executeQuery(sqlQuery);
			int i = 0;
			while (rs.next())
			{
				//Thread.currentThread().sleep(10);
				String authorid = rs.getString("affid");
				
				FastClient client = new FastClient();
				client.setBaseURL("http://evazure.trafficmanager.net:15100");//PROD
				//client.setBaseURL("http://evprod08.cloudapp.net:15100");//DEV server
				//client.setBaseURL("http://evdr09.cloudapp.net:15100"); //DR			
				client.setResultView("ei");
				client.setOffSet(0);
				client.setPageSize(5);
				client.setQueryString("afid:"+authorid);
				client.setDoCatCount(true);
				//client.setDoNavigators(true);
				//client.setPrimarySort("ausort");
				client.setPrimarySortDirection("+");
				client.search();
				
				
	
				List l = client.getDocIDs();
				int count =client.getHitCount();
				
				if(count > 0)
				{
					String updateQuery = "update "+tableName+" set fast_count="+count+ "where affid='"+authorid+"'";
					updateStmt.addBatch(updateQuery);
					if(i==100)
					{
						updateStmt.executeBatch();
						i=0;
					} 
					i++;
					System.out.println(authorid+"   "+count);
				}
			}
			updateStmt.executeBatch();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		
	}

	private List getNTISColumnName(){

		List columnNameList = new ArrayList();

		 columnNameList.add("M_ID");
		 columnNameList.add("AN");
		 columnNameList.add("CAT");
		 columnNameList.add("IC");
		 columnNameList.add("PR");
		 columnNameList.add("SO");
		 columnNameList.add("TI");
		 columnNameList.add("IDES");
		 columnNameList.add("VI");
		 columnNameList.add("TN");
		 columnNameList.add("PA1");
		 columnNameList.add("PA2");
		 columnNameList.add("PA3");
		 columnNameList.add("PA4");
		 columnNameList.add("PA5");
		 columnNameList.add("RD");
		 columnNameList.add("XP");
		 columnNameList.add("PDES");
		 columnNameList.add("RN");
		 columnNameList.add("CT");
		 columnNameList.add("PN");
		 columnNameList.add("TNUM");
		 columnNameList.add("MAA1");
		 columnNameList.add("MAN1");
		 columnNameList.add("MAA2");
		 columnNameList.add("MAN2");
		 columnNameList.add("CL");
		 columnNameList.add("SU");
		 columnNameList.add("AV");
		 columnNameList.add("DES");
		 columnNameList.add("NU1");
		 columnNameList.add("IDE");
		 columnNameList.add("HN");
		 columnNameList.add("AB");
		 columnNameList.add("NU2");
		 columnNameList.add("IB");
		 columnNameList.add("TA");
		 columnNameList.add("NU3");
		 columnNameList.add("NU4");
		 columnNameList.add("LC");
		 columnNameList.add("SE");
		 columnNameList.add("CAC");
		 columnNameList.add("DLC");
		 columnNameList.add("LOAD_NUMBER");


		 return columnNameList;

	}
	
	 public String getPage(String xp,
             String ar)
	{
		String strPage = null;
		
		if(ar != null)
		{
			strPage = ar;
		}
		else
		{
			strPage = xp;
		}
		
		return strPage;
	}
	
	public String getDedupKey(String issn,
            String coden,
            String volume,
            String issue,
            String page)
	throws Exception
	{
		Perl5Util perl = new Perl5Util();
		String firstVolume = getFirstNumber(volume);
		String firstIssue = getFirstNumber(issue);
		String firstPage = getFirstPage(page);
		
		if ((issn == null && coden == null) ||
			firstVolume == null ||
			firstIssue == null ||
			firstPage == null)
		{
			return null;
		}
		
		StringBuffer buf = new StringBuffer();
		
		if (issn != null)
		{
			buf.append(perl.substitute("s/-//g", issn));
		}
		else
		{
			buf.append(BdCoden.convert(coden));
		}
		
		buf.append("vol" + firstVolume);
		buf.append("is" + firstIssue);
		buf.append("pa" + firstPage);
		
		return buf.toString().toLowerCase();
	
	}
	
	private String getFirstNumber(String v)
    {
		Perl5Util perl = new Perl5Util();
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
	
	private String getFirstPage(String v)
    {
        BdPage pages = new BdPage(v);
        return pages.getStartPage();
    }
	
	private void checkGrfCoordinates()
	{
		Statement stmt = null;
		ResultSet rs = null;
		Connection con = null;
		String sqlQuery = null;
		
		try
		{

			con = getConnection(this.URL,this.driver,this.username,this.password);
			stmt = con.createStatement();
			
			sqlQuery="select m_id,COORDINATES from georef_master where COORDINATES is not null";
			rs = stmt.executeQuery(sqlQuery);
			
			int k = 0;
			
			while (rs.next())
			{
				String strcoordinates = rs.getString("COORDINATES");
				String m_id = rs.getString("m_id");
				if(strcoordinates != null)
				{				  
				  String[] termcoordinate = strcoordinates.split(Constants.AUDELIMITER);
				  List geoterms = new ArrayList();
				  for(int j = 0; j < termcoordinate.length; j++)
				  {
					String[] termcoordinates = termcoordinate[j].split(Constants.IDDELIMITER);
					if(termcoordinates.length == 1)
					{
						String[] termcoordinates_tmp = new String[2];
						termcoordinates_tmp[0] = j + "";
						termcoordinates_tmp[1] = termcoordinates[0];
						termcoordinates = termcoordinates_tmp;
			
					}
					if(termcoordinates.length == 2)
					{
					  if(!termcoordinates[0].matches("\\d+"))
					  {
						  geoterms.add(termcoordinates[0]);
					  }
					  String[] coords = parseCoordinates(termcoordinates[1],m_id);
					  if(coords != null &&  coords.length>4)
					  {
						 
						  
						  if(coords[4].indexOf("-") == -1 && coords[3].indexOf("-") != -1)
						  {
							//secondBoxCoords = parseCoordinates(termcoordinates[1]);
							//System.out.println(secondBoxCoords[1] + "," + secondBoxCoords[2] + "," + secondBoxCoords[3] + "," + secondBoxCoords[4]);
							coords[3] = "180";
							//recSecondBox = new EVCombinedRec();
						  }				
				  		}//if
					}//if
				  }//for
				  
				}//if
			}//while
		}//try
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
	
	private String[] parseCoordinates(String cs,String m_id) throws Exception
	{
		    cs = cs.replaceAll("[^a-zA-Z0-9]", "");
			String coordString = cs.trim().replaceAll("([NEWS])","-$1");

			String[] coords = coordString.split("-");
			
			if(coords.length>1) 
			{ 
				if(coords[1]!=null && coords[1].length()>0 && !coords[1].substring(0,1).equals("N") && !coords[1].substring(0,1).equals("S"))
				{
					 System.out.println("cord1="+coords[1].substring(0,1)+" m_id="+m_id) ;
				}
			}
			else
			{
				System.out.println("coords= "+cs+" m_id="+m_id);
			}
			
			if(coords.length>2) 
			{ 
				if(coords[2]!=null && coords[1].length()>0 && !coords[2].substring(0,1).equalsIgnoreCase("N") && !coords[2].substring(0,1).equalsIgnoreCase("S"))
				{
					 System.out.println("cord2="+coords[2].substring(0,1)+" m_id="+m_id) ;
				}
			}
			else
			{
				System.out.println("coords= "+cs+" m_id="+m_id);
			}
			
			if(coords.length>3) 
			{  
				if(coords[3]!=null && coords[1].length()>0 && !coords[3].substring(0,1).equalsIgnoreCase("W") && !coords[3].substring(0,1).equalsIgnoreCase("E"))
				{
					 System.out.println("cord3="+coords[3].substring(0,1)+" m_id="+m_id) ;
				}
			}
			else
			{
				System.out.println("coords= "+cs+" m_id="+m_id);
			}
			
			if(coords.length>4) 
			{
				if(coords[4]!=null && coords[1].length()>0 && !coords[4].substring(0,1).equalsIgnoreCase("W") && !coords[4].substring(0,1).equalsIgnoreCase("E"))
				{
					 System.out.println("cord4="+coords[4].substring(0,1)+" m_id="+m_id) ;
				}
			}
			else
			{
				System.out.println("coords= "+cs+" m_id="+m_id);
			}
			
			for(int i=1;i< coords.length;i++)
			{
				if(coords[i]!=null && coords[i].length() < 7)
				{
					int padCount = 8 - coords[i].length();
					for(int p=0;p < padCount;p++)
						coords[i] += "0";
				}

				coords[i] = coords[i].replaceAll("[NE]","+").substring(0,coords[i].length()-4).replaceAll("\\+","");
				coords[i] = coords[i].replaceAll("[WS]","-");
				if(coords[i].substring(0,1).indexOf("-") != -1)
					coords[i] = coords[i].replaceAll("^(-)0{1,2}(.*?)","$1$2");
				else
					coords[i] = coords[i].replaceAll("^0{1,2}(.*?)","$1");
			}

			return coords;
	}
	
	private void fullTestRunSQS()
	{
		 AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();

        System.out.println("===============================================");
        System.out.println("Getting Started with Amazon SQS standard Queues");
        System.out.println("===============================================\n");

        try {
            // Create a queue
            System.out.println("Creating a new SQS queue called MyQueue.\n");
            CreateQueueRequest createQueueRequest = new CreateQueueRequest("MyQueue");
            String myQueueUrl = sqs.createQueue(createQueueRequest).getQueueUrl();
            
            // List queues
            System.out.println("Listing all queues in your account.\n");
            System.in.read();
            for (String queueUrl : sqs.listQueues().getQueueUrls()) {
                System.out.println("  QueueUrl: " + queueUrl);
            }
            System.out.println();
            
            // Send a message
            System.out.println("Sending a message to MyQueue.\n");
            System.in.read();
            sqs.sendMessage(new SendMessageRequest(myQueueUrl, "This is my message text."));
           
            // Receive messages
            System.out.println("Receiving messages from MyQueue.\n");
            System.in.read();
            ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(myQueueUrl);
            List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
            for (Message message : messages) {
                System.out.println("  Message");
                System.out.println("    MessageId:     " + message.getMessageId());
                System.out.println("    ReceiptHandle: " + message.getReceiptHandle());
                System.out.println("    MD5OfBody:     " + message.getMD5OfBody());
                System.out.println("    Body:          " + message.getBody());
                for (Entry<String, String> entry : message.getAttributes().entrySet()) {
                    System.out.println("  Attribute");
                    System.out.println("    Name:  " + entry.getKey());
                    System.out.println("    Value: " + entry.getValue());
                }
            }
            System.out.println();

            // Delete a message
            System.out.println("Deleting a message.\n");
            System.in.read();
            String messageReceiptHandle = messages.get(0).getReceiptHandle();
            sqs.deleteMessage(new DeleteMessageRequest(myQueueUrl, messageReceiptHandle));

            // Delete a queue
            System.out.println("Deleting the test queue.\n");
            System.in.read();
            sqs.deleteQueue(new DeleteQueueRequest(myQueueUrl));
        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it " +
                    "to Amazon SQS, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered " +
                    "a serious internal problem while trying to communicate with Amazon SQS, such as not " +
                    "being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }catch (Exception e) {
            System.out.println("Caught an unexpected Exception");
            System.out.println("Error Message: " + e.getMessage());
        }	 	 
	}
	
	private void getSqsMessage(String myQueueUrl)
	{
		AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
		try{
			ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(myQueueUrl);
	        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
	        for (Message message : messages) {
	            System.out.println("  Message");
	            System.out.println("    MessageId:     " + message.getMessageId());
	           // System.out.println("    ReceiptHandle: " + message.getReceiptHandle());
	            //System.out.println("    MD5OfBody:     " + message.getMD5OfBody());
	            System.out.println("    Body:          " + message.getBody());
	            for (Entry<String, String> entry : message.getAttributes().entrySet()) {
	                System.out.println("  Attribute");
	                System.out.println("    Name:  " + entry.getKey());
	                System.out.println("    Value: " + entry.getValue());
	            }
	        }
		}catch(Exception e){
			System.out.println("Caught an unexpected Exception");
            System.out.println("Error Message: " + e.getMessage());
		}
	}
	
	private void testGetSqs(String QUEUE_NAME)
	{
		AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
		String queue_url = sqs.getQueueUrl(QUEUE_NAME).getQueueUrl();
		System.out.println("queue_url= "+queue_url);
	}
	
	private void testListSqs()
	{
		/*
		 * The ProfileCredentialsProvider will return your [default]
		 * credential profile by reading from the credentials file located at
		 * (~/.aws/credentials).
		 */
		AWSCredentialsProvider credentials = null;
		try {
			//credentials = new EnvironmentVariableCredentialsProvider();   // for localhost
			credentials = new InstanceProfileCredentialsProvider();        // for dataloading EC2
		} catch (Exception e) {
			throw new AmazonClientException(
					"Cannot load the credentials from the credential profiles file. " +
							"Please make sure that your credentials file is at the correct " +
							"location (~/.aws/credentials), and is in valid format.",
							e);
		}

		

		AmazonSQS	sqs = new AmazonSQSClient(credentials);
		//Region euWest2 = Region.getRegion(Regions.EU_WEST_1);
		//sqs.setRegion(euWest2);
		//AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
		ListQueuesResult lq_result = sqs.listQueues();
		System.out.println("Your SQS Queue URLs:");
		for (String url : lq_result.getQueueUrls()) {
		    System.out.println(url);
		}
	}
	
	private void testAddSqs()
	{
		String QUEUE_NAME = "myFirstQuery";
		AmazonSQS sqs = AmazonSQSClientBuilder.defaultClient();
		CreateQueueRequest create_request = new CreateQueueRequest(QUEUE_NAME)
		        .addAttributesEntry("DelaySeconds", "60")
		        .addAttributesEntry("MessageRetentionPeriod", "86400");

		try {
		    sqs.createQueue(create_request);
		} catch (AmazonSQSException e) {
		    if (!e.getErrorCode().equals("QueueAlreadyExists")) {
		        throw e;
		    }
		}
	}
	
	/*
	private void testS3(String currDir)
	{
		try
		{
			AmazonS3 s3Client = AmazonS3Service.getInstance().getAmazonS3Service();
			TransferManager tmanager = new TransferManager(s3Client);
			
			File zipsDir = new File(currDir);

			if(!(zipsDir.exists()))
			{
				throw new FileNotFoundException();
			}

			//Check if Dir isNotEmpty (contains file), otherwise skip upload to S3
			if(zipsDir.isDirectory() && zipsDir.list().length>0)
			{
				
				System.out.println("Uploading Dir: " + zipsDir.getAbsolutePath() + " to S3 Bucket");

				// upload vtw zip dir to S3
				MultipleFileUpload uploadFile = tmanager.uploadDirectory(bucketName, key, zipsDir, true);

				// check transfer's status to check its progress
			
				uploadFile.addProgressListener(new ProgressListener()
				{
					@Override
					public void progressChanged(ProgressEvent progressEvent)
					{
						System.out.println("Transfer: " + progressEvent.getBytesTransferred());
					}
				}	);
				
				// block the current thread and wait for the transfer to complete. if transfer fails; this method will throw 
				//AmazonClientException or AmazonServiceException 
				uploadFile.waitForCompletion();
				

			}
			else
			{
				System.out.println("Dir is Empty!");
				System.exit(1);
			}


		}
		catch(AmazonServiceException ase)
		{
			System.out.println("Caught an AmazonServiceException, which " +"means your request made it " +
					"to Amazon S3, but was rejected with an error response" +
					" for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		}
		catch(AmazonClientException ace)
		{
			System.out.println("Caught an AmazonClientException, which " +
					"means the client encountered " +
					"an internal error while trying to " +
					"communicate with S3, " +
					"such as not being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
			System.out.println("HTTP Status Code: " + ace.getCause());
		}
		catch(InterruptedException ex)
		{
			ex.printStackTrace();
		}
		catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		finally
		{
			if(tmanager !=null)
			{
				//after download is complete, call shutdownNow to release resources
				tmanager.shutdownNow(false);  
			}

		}
	}
*/
	private void testPutAWSS3(String key_name)
	{
		//AmazonS3 s3Client = new AmazonS3Client(new ProfileCredentialsProvider());
		AmazonS3 s3Client;
		S3Object object;
		try {
			s3Client = AmazonS3Service.getInstance().getAmazonS3Service();
			//object = s3Client.getObject( new GetObjectRequest("hmoroger", "midFromFast_DR.out"));
			String file_path = "./"+key_name;
			ObjectMetadata md;
			byte[] bytes;
			bytes = file_path.getBytes();
			md = new ObjectMetadata();
			md.setContentType("text/xml");
			//md.setContentLength(bytes.length);
			System.out.println("total length " + bytes.length); 
			PutObjectResult response = s3Client.putObject(new PutObjectRequest("ev-datasets-cert/cpx/", key_name, new File(file_path)));	
			System.out.println("Key: " + key_name + " successfully uploaded to S3, Etag: " + response.getETag());
	        }catch(Exception e){
	        	System.out.println("Other Error Message: " + e.getMessage());
	        }
	}
	
	private void testGetAWSS3(String key)
	{
		//AmazonS3 s3Client = new AmazonS3Client(new ProfileCredentialsProvider());
		AmazonS3 s3Client;
		S3Object object;
		try {
			s3Client = AmazonS3Service.getInstance().getAmazonS3Service();
			//object = s3Client.getObject( new GetObjectRequest("hmoroger", "midFromFast_DR.out"));
			object = s3Client.getObject( new GetObjectRequest("sccontent-ani-xml-prod", key));
			InputStream objectData = object.getObjectContent();
			 System.out.println("Content-Type: "  + 
					 object.getObjectMetadata().getContentType());
	         //displayTextInputStream(object.getObjectContent());
			 saveContentToFile(object.getObjectContent(),key);
	         
			// Process the objectData stream.
			objectData.close();
		 } catch (AmazonServiceException ase) {
	            System.out.println("Caught an AmazonServiceException, which" +
	            		" means your request made it " +
	                    "to Amazon S3, but was rejected with an error response" +
	                    " for some reason.");
	            System.out.println("Error Message:    " + ase.getMessage());
	            System.out.println("HTTP Status Code: " + ase.getStatusCode());
	            System.out.println("AWS Error Code:   " + ase.getErrorCode());
	            System.out.println("Error Type:       " + ase.getErrorType());
	            System.out.println("Request ID:       " + ase.getRequestId());
	        } catch (AmazonClientException ace) {
	            System.out.println("Caught an AmazonClientException, which means"+
	            		" the client encountered " +
	                    "an internal error while trying to " +
	                    "communicate with S3, " +
	                    "such as not being able to access the network.");
	            System.out.println("Error Message: " + ace.getMessage());
	        }catch(Exception ioe)
	        {
	        	System.out.println("Other Error Message: " + ioe.getMessage());
	        }
	}
	
	public static void saveContentToFile (InputStream objectData, String key) throws IOException
	{
		BufferedReader breader = null;
		PrintWriter out = null;
		try
		{
			breader = new BufferedReader(new InputStreamReader(objectData));
			breader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(breader.readLine().replaceAll("><", ">\n<").getBytes())));

			//File file = new File(s3Dir.getName()+"/"+key+".xml");
			File file = new File("./testS3/"+key+".xml");

			if (!file.exists()) 
			{
				System.out.println("Downloaded: "+file.getName());

			}
			else
			{
				System.out.println("file:" +  file.getName() + "already exist");
			}

			String line = null;
			out = new PrintWriter(new BufferedWriter(new FileWriter(file.getAbsolutePath(),true)));
			while ((line = breader.readLine()) !=null)
			{
				out.println(line);

			}

		}
		catch (IOException e) {

			e.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		finally
		{
			try
			{
				if(breader !=null)
				{
					breader.close();
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}


			try
			{
				if(out !=null)
				{
					out.flush();
					out.close();
				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}

	}
	
	private static void displayTextInputStream(InputStream input)
	throws IOException {
		// Read one text line at a time and display.
	    BufferedReader reader = new BufferedReader(new 
	    		InputStreamReader(input));
	    while (true) {
	        String line = reader.readLine();
	        if (line == null) break;
	
	        System.out.println("    " + line);
	    }
	    System.out.println();
	}
	
	@SuppressWarnings("unchecked")
	public String buildESQuery(String value, String searchField, String database) {
		
		JSONObject query = new JSONObject();
		
		JSONObject queryString = new JSONObject();
		queryString.put("queryString", searchField + ":" + value + " AND database:" + database);
		queryString.put("defaultOperator", "AND");
		query.put("query",queryString);
		
		JSONArray returnFields = new JSONArray();
		returnFields.add("eidocid");
		query.put("returnFields", returnFields);
		

		JSONObject result = new JSONObject();
		result.put("skip", 0);
		result.put("amount", 4300);
		query.put("resultSet", result);
		
		return query.toJSONString();
			
	}
	
	private int checkES(String value, String searchfield,String database, String server)
	{
		String esUrl = "https://shared-search-service-api.prod.scopussearch.net/sharedsearch/document/result";
		if(server.equalsIgnoreCase("prod"))
		{
			esUrl = "https://shared-search-service-api.prod.scopussearch.net/sharedsearch/document/result";
			System.out.println("using prod server: "+esUrl);
		}
		else if (server.equalsIgnoreCase("cert"))
		{
			esUrl = "https://shared-search-service-api.cert.scopussearch.net/sharedsearch/document/result";
			System.out.println("using cert server: "+esUrl);
		}
		else if (server.equalsIgnoreCase("dev"))
		{
			esUrl = "https://shared-search-service-api.dev.scopussearch.net/sharedsearch/document/result";
			System.out.println("using dev server: "+esUrl);
		}
		else
			System.out.println("using default prod server: "+esUrl);
		
		URL urlObject;
		String result = null;
		int hitCount=0;
		String query=buildESQuery(value,searchfield,database);		
		//System.out.println("ESQuery: " + query);
		
		try
		{
							
			long startTime = System.currentTimeMillis();
							
			urlObject = new URL(esUrl);
			HttpURLConnection httpCon = (HttpURLConnection) urlObject.openConnection();
			httpCon.setRequestMethod("POST");
			httpCon.setRequestProperty("x-els-product", "engineering_village");
			httpCon.setRequestProperty("x-els-diagnostics", "false");
			httpCon.setRequestProperty("Content-Type", "application/json");
			httpCon.setDoOutput(true);
			//logger.info("before outputstreamwriter....");
			OutputStreamWriter writer = new OutputStreamWriter(httpCon.getOutputStream());
			//logger.info("after outputstreamwriter....");
			writer.write(query);
			writer.close();
			int responseCode = httpCon.getResponseCode();
			
			//logger.info("responseCode: " + responseCode);
			// Only read response if connection was successful
			if(responseCode == HttpURLConnection.HTTP_OK)
			{
				BufferedReader in = new BufferedReader(new InputStreamReader(httpCon.getInputStream()));
				String line;
				StringBuilder response = new StringBuilder();
				while((line = in.readLine()) != null)
				{
					response.append(line);					
					if(line.equalsIgnoreCase("hits"))
						break;
				}
				//System.out.println("Response: " + response.toString());   // only for debugging
				// close stream
				in.close();
				
				long finishTime = System.currentTimeMillis();
				//System.out.println("Time to runQuery: " + (finishTime - startTime));
				
				// process response and fetch HitCount from result
				
				
				if(!(response.toString().isEmpty()))
				{
					JSONParser parser = new JSONParser();
					JSONObject json = (JSONObject) parser.parse(response.toString());
					hitCount = Integer.parseInt(json.get("totalResultsCount").toString());
					if(hitCount>0)
					{
						//System.out.println("***ESQuery:" + query);   // only for debugging
						System.out.println("***" + value + "\t" + hitCount +" ***"); 						
						
					}
					else
					{
						System.out.println("no record found for m_id " + value ); 
					}
								
				}
				
				
				
			}
		}
		catch(Exception e)
		{					
			e.printStackTrace();			

		}	
		return hitCount;
			
	}
	
	private int getESMID(String value, String searchfield,String database, String server)
	{
		String esUrl = "https://shared-search-service-api.prod.scopussearch.net/sharedsearch/document/result";
		if(server.equalsIgnoreCase("prod"))
		{
			esUrl = "https://shared-search-service-api.prod.scopussearch.net/sharedsearch/document/result";
			System.out.println("using prod server: "+esUrl);
		}
		else if (server.equalsIgnoreCase("cert"))
		{
			esUrl = "https://shared-search-service-api.cert.scopussearch.net/sharedsearch/document/result";
			System.out.println("using cert server: "+esUrl);
		}
		else if (server.equalsIgnoreCase("dev"))
		{
			esUrl = "https://shared-search-service-api.dev.scopussearch.net/sharedsearch/document/result";
			System.out.println("using dev server: "+esUrl);
		}
		else
			System.out.println("using default prod server: "+esUrl);
		
		URL urlObject;
		String result = null;
		int hitCount=0;
		String query=buildESQuery(value,searchfield,database);		
		System.out.println("ESQuery: " + query);
		
		try
		{
							
			long startTime = System.currentTimeMillis();
							
			urlObject = new URL(esUrl);
			HttpURLConnection httpCon = (HttpURLConnection) urlObject.openConnection();
			httpCon.setRequestMethod("POST");
			httpCon.setRequestProperty("x-els-product", "engineering_village");
			httpCon.setRequestProperty("x-els-diagnostics", "false");
			httpCon.setRequestProperty("Content-Type", "application/json");
			httpCon.setDoOutput(true);
			//logger.info("before outputstreamwriter....");
			OutputStreamWriter writer = new OutputStreamWriter(httpCon.getOutputStream());
			//logger.info("after outputstreamwriter....");
			writer.write(query);
			writer.close();
			int responseCode = httpCon.getResponseCode();
			
			//logger.info("responseCode: " + responseCode);
			// Only read response if connection was successful
			if(responseCode == HttpURLConnection.HTTP_OK)
			{
				BufferedReader in = new BufferedReader(new InputStreamReader(httpCon.getInputStream()));
				String line;
				StringBuilder response = new StringBuilder();
				while((line = in.readLine()) != null)
				{
					response.append(line);
					
					//if(line.equalsIgnoreCase("hits"))
						//break;
				}
				//System.out.println("Response: " + response.toString());   // only for debugging
				// close stream
				in.close();
				
				long finishTime = System.currentTimeMillis();
				//System.out.println("Time to runQuery: " + (finishTime - startTime));
				
				// process response and fetch HitCount from result
				
				
				if(!(response.toString().isEmpty()))
				{
					JSONParser parser = new JSONParser();
					JSONObject json = (JSONObject) parser.parse(response.toString());
					hitCount = Integer.parseInt(json.get("totalResultsCount").toString());
					if(hitCount>0)
					{
						//System.out.println("***ESQuery:" + query);   // only for debugging
						System.out.println("***" + value + "\t" + hitCount +" ***"); 
						JSONArray jsonArray = (JSONArray)json.get("hits"); 
						Iterator<JSONObject> itr = jsonArray.iterator();
						while(itr.hasNext())
						{
							String[] returnField = itr.next().toString().split(":");
							if(returnField.length >1)
							{
								System.out.println(returnField[1].replaceAll("}", "").replaceAll("\"", ""));
							}
								
						}
						
					}
					else
					{
						System.out.println("no record found for m_id " + value ); 
					}
								
				}
				
				
				
			}
		}
		catch(Exception e)
		{					
			e.printStackTrace();			

		}	
		return hitCount;
			
	}
	

	protected Connection getConnection(String connectionURL,
		                               String driver,
		                               String username,
		                               String password)
		           						   throws Exception
		{
			System.out.println("URL==> "+connectionURL);
			System.out.println("DRIVER==> "+driver);
			System.out.println("USERNAME==> "+username);
			System.out.println("PASSWORD==> "+password);
			Class.forName(driver);
		   	Connection con = DriverManager.getConnection(connectionURL,
		                                              username,
		                                              password);
		    return con;
    }
}
