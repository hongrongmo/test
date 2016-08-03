package org.ei.util;

import java.sql.Connection;

import java.sql.DriverManager;
import java.io.*;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.util.*;
import java.net.*;
import java.util.regex.*;

import org.ei.domain.*;
import org.ei.query.base.*;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.MatchResult;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;
import io.searchbox.core.SearchResult.Hit; 


public class NewDataTesting
{
	//public static String URL="jdbc:oracle:thin:@127.0.0.1:5521:EID";
	//public static String URL="jdbc:oracle:thin:@127.0.0.1:5523:EIA";
	public static String URL="jdbc:oracle:thin:@eid.cmdvszxph9cf.us-east-1.rds.amazonaws.com:1521:eid";
	public static String driver="oracle.jdbc.driver.OracleDriver";
	public static String username="ap_ev_search";
	public static String username1="ba_s300";
	public static String password="ei3it";
	public static String password1="ei7it";
    public static String tableName="bd_master";
    public static String tableName1="bd_master_jupiter";
    public static String database="cpx";
    public static String updateNumber="0";
    public static String action="bd_loading";

	public static void main(String args[]) throws Exception
	{
		NewDataTesting test = new NewDataTesting();
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

		if(action.equals("fast"))
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
		else  if(action.equals("checkmid"))
		{
			test.getMIDFromFast(updateNumber);
		}
		else  if(action.equals("cpc"))
		{
			test.getCPCDescription(updateNumber);
		}
		else  if(action.equals("testElasticSearch"))
		{
			test.testElasticSearch();
		}
		else
		{
			test.getData(database);
		}

	}
	
	private void testElasticSearch() throws Exception
	{
		// Construct a new Jest Client via factory
				JestClientFactory factory = new JestClientFactory();
				factory.setHttpClientConfig(new HttpClientConfig
						.Builder("http://search-evcafeauaf-v6tfjfyfj26rtoneh233lzzqtq.us-east-1.es.amazonaws.com:80")
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
	
	 protected void exampleSearch(JestClient client) throws Exception { 
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
	        Search.Builder searchBuilder = new Search.Builder(query).addIndex("jug").addType("talk"); 
	        io.searchbox.core.SearchResult result = client.execute(searchBuilder.build()); 
	        /*
	        List<Hit<Talk, Void>> hits = result.getHits(Talk.class); 
	        log.info("Retrieved result " + result.getJsonString()); 
	        for (Hit<Talk, Void> hit: hits) { 
	            Talk talk = hit.source; 
	            log.info(talk.getTitle()); 
	        }
	        */ 
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
				readMap.openRead("ecla", false);
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
			    readMap.close();
			}
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
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
			FastSearchControl.BASE_URL = "http://ei-main-p2.nda.fastsearch.net:15100";
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
				//System.out.println("DISPLAYQUERY= "+queryObject.getDisplayQuery()+" PhysicalQuery= "+queryObject.getPhysicalQuery()+" SEARCHQUERY= "+queryObject.getSearchQuery());

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

		List detailRecordFromDatabase = getDetailRecordFromDatabase(database,updateNumber);


		for(int i=0;i<detailRecordFromDatabase.size();i++)
		{
			String mid = (String)detailRecordFromDatabase.get(i);
			String count = "0";
			int midCount=getMIDCountFromFast(mid,database);
			if(midCount<1)
			{
       			System.out.println(mid);
			}
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
			sqlQuery="select m_id from "+username+"."+tableName+" where database='"+database+"' and loadnumber='"+loadnumber+"'";
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
			String loadNumberCountFromFast=getCountFromFast(loadNumber,database);
			System.out.println(loadNumber+"\t\t"+count+"\t"+loadNumberCountFromFast);
		}

	}

	private int getMIDCountFromFast(String mid,String database)
	{
		List outputList = new ArrayList();
		DatabaseConfig databaseConfig = null;
		String[] credentials = new String[]{"CPX","INS","EPT","EUP","UPA"};
		String[] dbName = database.split(";");
		//FastSearchControl.BASE_URL = "http://ei-stage.nda.fastsearch.net:15100";
		FastSearchControl.BASE_URL = "http://ei-main.nda.fastsearch.net:15100";

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
			queryObject.setSearchQuery("(all:"+term1+") and (db:"+database+")");
			//System.out.println("DISPLAYQUERY= "+queryObject.getDisplayQuery()+" PhysicalQuery= "+queryObject.getPhysicalQuery()+" SEARCHQUERY= "+queryObject.getSearchQuery());

			String sessionId = null;
			int pagesize = 25;
			org.ei.domain.SearchResult result = sc.openSearch(queryObject,sessionId,pagesize,false);
			int c = result.getHitCount();
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

	private void getMIDFromFast(String load_number)
	{

		Statement stmt = null;
		ResultSet rs = null;
		Connection con = null;
		try
		{
			con = getConnection(this.URL,this.driver,this.username,this.password);
			String sqlQuery = "select id_number,m_id from georef_master where load_number='"+load_number+"' and document_type='GI'";
			stmt = con.createStatement();

			System.out.println("QUERY= "+sqlQuery);
			rs = stmt.executeQuery(sqlQuery);
			int k = 0;
			while (rs.next())
			{
				//Thread.currentThread().sleep(250);
				String accessnumber = rs.getString("id_number");
				String m_id = rs.getString("m_id");


				//in = new BufferedReader(new FileReader("test.txt"));
				FastClient client = new FastClient();
				client.setBaseURL("http://ei-main.nda.fastsearch.net:15100");
				client.setResultView("ei");
				client.setOffSet(0);
				client.setPageSize(50000);
				client.setQueryString("(ALL:\""+m_id+"\") and (wk:"+load_number+") AND (yr:[1785;2015]) AND (((db:grf)))");
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
				  System.out.println("MID= "+m_id);
			    }

				StringBuffer sb=new StringBuffer();
				/*
				for(int i=0;i<l.size();i++)
				{
					String[] docID = (String[])l.get(i);

					if(!m_id.equals(docID[0]))
					{
						System.out.println("m_id="+m_id+" docID="+docID[0]+" id_number="+accessnumber);
					}
					else
					{
						System.out.println("id_number "+accessnumber+" has the same m_id");
					}
				}
				*/

			}
			//getAccessnumber(sb.toString());
			//System.out.println(sb.toString());


		}
		catch(Exception e)
		{
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

	private String getCountFromFast(String loadNumber,String database)
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
			sqlQuery="select count(*) count,load_number from upt_master  group by load_number";
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
