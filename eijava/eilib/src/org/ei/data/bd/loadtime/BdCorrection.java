package org.ei.data.bd.loadtime;

import java.sql.Clob;
import java.sql.*;
import java.util.*;
import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.MatchResult;
import org.ei.data.*;
import org.ei.data.bd.*;
import org.ei.data.bd.loadtime.*;
import org.ei.domain.*;
import org.ei.query.base.*;
import org.ei.connectionpool.*;
import java.text.*;
import java.io.*;
import java.lang.Process;
import java.util.regex.*;
import org.ei.util.GUID;

public class BdCorrection
{
    Perl5Util perl = new Perl5Util();

    private static String tablename;

	private static String currentDb;

    private static HashMap issnARFix = new HashMap();

    private int intDbMask = 1;
    private static Connection con = null;
    //static String url="jdbc:oracle:thin:@jupiter:1521:eidb1";
    static String url="jdbc:oracle:thin:@neptune:1521:ei";
    static String driver="oracle.jdbc.driver.OracleDriver";
    static String username="ap_correction";
    static String password="ei3it";
    static String database;
    static String action;
    static boolean test = false;


    public static void main(String args[])
        throws Exception
    {
		String fileToBeLoaded 	= null;
		int updateNumber   		= 0;
		String input;
		String tableToBeTruncated = "bd_master_temp,deleted_lookupIndex,bd_temp_backup";
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

		if(args.length>8)
		{
			if(args[5]!=null)
			{
				url = args[5];
			}
			if(args[6]!=null)
			{
				driver = args[6];
			}
			if(args[7]!=null)
			{
				username = args[7];
			}
			if(args[8]!=null)
			{
				password = args[8];
			}
		}

		if(args.length>4)
		{
			if(args[0]!=null)
			{
				fileToBeLoaded = args[0];
			}


			if(args[1]!=null)
			{
				tableToBeTruncated = args[1];
			}


			if(args[2]!=null)
			{
				database = args[2];
			}


			if(args[3]!=null && args[3].length()>0)
			{
				Pattern pattern = Pattern.compile("^\\d*$");
				Matcher matcher = pattern.matcher(args[3]);
				if (matcher.find())
				{
					updateNumber = Integer.parseInt(args[3]);
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
			}

		}
		else
		{
			System.out.println("not enough parameters");
			System.exit(1);
		}


		try
		{

			//FastSearchControl.BASE_URL = "http://ei-main.bos3.fastsearch.net:15100";
			FastSearchControl.BASE_URL = "http://ei-test.bos3.fastsearch.net:15100";


			/**********delete all data from temp table *************/
			if(test)
			{
				System.out.println("about to truncate table "+tableToBeTruncated);
				System.out.println("press enter to continue");
				Thread.currentThread().sleep(500);
				System.in.read();
				int answer = System.in.read();
				Thread.currentThread().sleep(1000);
			}
			BdCorrection bdc = new BdCorrection();
			con = bdc.getConnection(url,driver,username,password);
			bdc.cleanUp(tableToBeTruncated);

			/***********load data into temp table ****************/

			if(test)
			{
				System.out.println("about to parse data file "+fileToBeLoaded);
				System.out.println("press enter to continue");
				System.in.read();
				Thread.currentThread().sleep(1000);
			}

			BaseTableDriver c = new BaseTableDriver(updateNumber,database);
        	c.writeBaseTableFile(fileToBeLoaded);
			String dataFile=fileToBeLoaded+"."+updateNumber+".out";
			File f = new File(dataFile);
			if(!f.exists())
			{
				System.out.println("datafile "+dataFile+" does not exists");
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

			Process p = r.exec("correctionFileLoader.sh "+dataFile);
			int t = p.waitFor();
			//System.out.println(" t "+t);
			int tempTableCount = bdc.getTempTableCount();
			if(tempTableCount>0)
			{
				System.out.println(tempTableCount+" records was loaded into the temp table");
				if(test)
				{
					System.out.println("begin to update tables");
					System.out.println("press enter to continue");
					System.in.read();
					Thread.currentThread().sleep(1000);
				}
				bdc.runCorrection(dataFile,updateNumber,database,action);
			}
			else
			{
				System.out.println("no record was loaded into the temp table");
				System.exit(1);
			}

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
				bdc.processLookupIndex(bdc.getLookupData("update"),bdc.getLookupData("backup"));
			}
			else if(action.equalsIgnoreCase("delete"))
			{
				bdc.processLookupIndex(new HashMap(),bdc.getLookupData("backup"));
			}
			bdc.doFastExtract(updateNumber,database,action);

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
		}

        System.exit(1);
    }

    private void doFastExtract(int updateNumber,String dbname,String action) throws Exception
    {
		CombinedWriter writer = new CombinedXMLWriter(50000,
		                                              updateNumber,
		                                              dbname,
		                                              "dev");

		writer.setOperation("add");

        XmlCombiner c = new XmlCombiner(writer);
        Statement stmt = null;
        ResultSet rs = null;
        try
		{
			stmt = con.createStatement();
			if(action.equalsIgnoreCase("update"))
			{
				System.out.println("Running the query...");
				rs = stmt.executeQuery("select CHEMICALTERM,SPECIESTERM,REGIONALTERM,DATABASE,CITATIONLANGUAGE,CITATIONTITLE,CITTYPE,ABSTRACTDATA,PII,PUI,COPYRIGHT,M_ID,accessnumber,datesort,author,author_1,AFFILIATION,AFFILIATION_1,CORRESPONDENCEAFFILIATION,CODEN,ISSUE,CLASSIFICATIONCODE,CONTROLLEDTERM,UNCONTROLLEDTERM,MAINHEADING,TREATMENTCODE,LOADNUMBER,SOURCETYPE,SOURCECOUNTRY,SOURCEID,SOURCETITLE,SOURCETITLEABBREV,ISSUETITLE,ISSN,EISSN,ISBN,VOLUME,PAGE,PAGECOUNT,ARTICLENUMBER, substr(PUBLICATIONYEAR,1,4) as PUBLICATIONYEAR,PUBLICATIONDATE,EDITORS,PUBLISHERNAME,PUBLISHERADDRESS,PUBLISHERELECTRONICADDRESS,REPORTNUMBER,CONFNAME, CONFCATNUMBER,CONFCODE,CONFLOCATION,CONFDATE,CONFSPONSORS,CONFERENCEPARTNUMBER, CONFERENCEPAGERANGE, CONFERENCEPAGECOUNT, CONFERENCEEDITOR, CONFERENCEORGANIZATION,CONFERENCEEDITORADDRESS,TRANSLATEDSOURCETITLE,VOLUMETITLE,DOI,ASSIG,CASREGISTRYNUMBER,SEQ_NUM from bd_master_orig where updateNumber="+updateNumber);
				//System.out.println("Got records ...");
				c.writeRecs(rs);
				//System.out.println("Wrote records.");

				writer.end();
				writer.flush();
			}
			else if(action.equalsIgnoreCase("delete"))
			{
				rs = stmt.executeQuery("select m_id from bd_master_orig where accessnumber in (select accessnumber from bd_correction_temp)");
				creatDeleteFile(rs,dbname,updateNumber);
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

    private void creatDeleteFile(ResultSet rs,String database,int updateNumber)
    {

		String batchidFormat = "0000";
		String batchID = "0000";
		String numberID = "0000";
		File file=new File("fast");
		FileWriter out= null;
		//System.out.println("updateNumber= "+updateNumber);
		try
		{
			if(!file.exists())
			{
				file.mkdir();
			}
			String upNumber = Integer.toString(updateNumber);
			if(upNumber.length()<5)
			{
				numberID = upNumber;
				batchID = "0000";
			}
			else if(upNumber.length()<6)
			{
				numberID = upNumber.substring(0,4);
				batchID = upNumber.substring(4)+"000";
			}
			else if (upNumber.length()<7)
			{
				numberID = upNumber.substring(0,4);
				batchID = upNumber.substring(4)+"00";
			}
			else if (upNumber.length()<8)
			{
				numberID = upNumber.substring(0,4);
				batchID = upNumber.substring(4)+"0";
			}
			else
			{
				numberID = upNumber.substring(0,4);
				batchID = upNumber.substring(4,8);
			}
			batchidFormat = batchID;
			long starttime = System.currentTimeMillis();
			String batchPath = "fast/batch_" + numberID + "_" + batchidFormat;
			//System.out.println("PATH= "+batchPath);
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
			file = new File(root+"/delete.txt");
			if(!file.exists())
			{
				file.createNewFile();
			}
			out = new FileWriter(file);
			while (rs.next())
			{
				if(rs.getString("M_ID") != null)
				{
					out.write(rs.getString("M_ID")+"\n");
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

	}

    private void runCorrection(String fileName,int updateNumber,String database,String action)
    {
		CallableStatement pstmt = null;
		Statement stmt = null;
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

			pstmt = con.prepareCall("{ call update_bd_backup_table(?,?)}");
			pstmt.setInt(1,updateNumber);
			pstmt.setString(2,database);
			pstmt.executeUpdate();

			if(action != null && action.equalsIgnoreCase("update"))
			{
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
			}
			else
			{
				System.out.println("What do you want me to do? action "+action+" not known");
				System.exit(1);
			}

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

	private void runUpdateBdTempTable(int updateNumber,String fileName) throws Exception
	{

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

			rs = stmt.executeQuery("select count(*) count from BD_CORRECTION_TEMP");
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

		return count;
	}


    private void cleanUp(String tableToBeTruncate)
    {
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

			for(int i=0;i<tableName.length;i++)
			{
				System.out.println("truncate table "+tableName[i]);
				stmt.executeUpdate("truncate table "+tableName[i]);
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

    private void saveDeletedData(String field,List data,String database)
    {
		PreparedStatement stmt = null;
		try
		{
			//stmt = con.createStatement();
			if(data!=null)
			{
				for(int i=0;i<data.size();i++)
				{
					String term = (String)data.get(i);
					if(term != null && field != null && database != null)
					{
						System.out.println("term "+term+" ,field= "+field+" database= "+database);
						//stmt.executeUpdate("insert into deleted_lookupIndex(field,term,database) values('"+field+"','"+term+"','"+database+"')");
						stmt = con.prepareStatement("insert into deleted_lookupIndex(field,term,database) values(?,?,?)");
						stmt.setString(1,field);
						stmt.setString(2,term);
						stmt.setString(3,database);
						stmt.executeUpdate();

						con.commit();
					}
				}
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

    private void processLookupIndex(HashMap update,HashMap backup) throws Exception
    {
		//database = (String)update.get("DATABASE");
		//if(database==null)
		{
			database = this.database;
		}
		HashMap outputMap = new HashMap();
		HashMap deletedAuthorLookupIndex 		= getDeleteData(update,backup,"AUTHOR");
		HashMap deletedAffiliationLookupIndex 	= getDeleteData(update,backup,"AFFILIATION");
		//HashMap deletedControlltermLookupIndex 	= getDeleteData(update,backup,"CONTROLLEDTERM");
		//HashMap deletedPublisherNameLookupIndex = getDeleteData(update,backup,"PUBLISHERNAME");
		//HashMap deletedSerialtitleLookupIndex 	= getDeleteData(update,backup,"SERIALTITLE");
		saveDeletedData("AU",checkFast(deletedAuthorLookupIndex,"AU",database),database);
		saveDeletedData("AF",checkFast(deletedAffiliationLookupIndex,"AF",database),database);
		//saveDeletedData("CV",checkFast(deletedControlltermLookupIndex,"CV",database),database);
		//saveDeletedData("PN",checkFast(deletedPublisherNameLookupIndex,"PN",database),database);
		//saveDeletedData("ST",checkFast(deletedSerialtitleLookupIndex,"ST",database),database);
	}

	private List checkFast(HashMap inputMap, String searchField, String database) throws Exception
	{
		System.out.println(searchField+" SIZE= "+inputMap.size());
		List outputList = new ArrayList();
		DatabaseConfig databaseConfig = DatabaseConfig.getInstance(DriverConfig.getDriverTable());
		String[] credentials = new String[]{"CPX","INS","NTI","UPA","EUP","PCH","CHM"};
		String[] dbName = {database};
		//System.out.println("dbName= "+database);
		int intDbMask = databaseConfig.getMask(dbName);

		Iterator searchTerms = inputMap.keySet().iterator();

		while (searchTerms.hasNext())
		{
			try
			{
				SearchControl sc = new FastSearchControl();
				String term1 = (String) searchTerms.next();
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
				int pagesize = 25;
				SearchResult result = sc.openSearch(queryObject,sessionId,pagesize,false);
				int c = result.getHitCount();
				String indexCount = (String)inputMap.get(term1);
				if(indexCount!=null && indexCount!="" && Integer.parseInt(indexCount) >= c)
				{
					outputList.add(term1);
				}
				System.out.println("IndexCount= "+(String)inputMap.get(term1));
				System.out.println("FastCount= "+c);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}

		}

		System.out.println("SIZE= "+outputList.size());

		return outputList;

	}

	private HashMap getDeleteData(HashMap update,HashMap backup,String field)
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
					//if(!checkUpdate(updateList,dData))
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
			if(action.equals("update"))
				rs = stmt.executeQuery("select ACCESSNUMBER,AUTHOR,AUTHOR_1,AFFILIATION,AFFILIATION_1,CONTROLLEDTERM,CHEMICALTERM,SOURCETITLE,PUBLISHERNAME,DATABASE FROM BD_CORRECTION_TEMP");
			else
				rs = stmt.executeQuery("select ACCESSNUMBER,AUTHOR,AUTHOR_1,AFFILIATION,AFFILIATION_1,CONTROLLEDTERM,CHEMICALTERM,SOURCETITLE,PUBLISHERNAME,DATABASE FROM BD_TEMP_BACKUP");

			System.out.println("Got records ...");
			results = setRecs(rs);

			System.out.println("Wrote records.");


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

	private HashMap setRecs(ResultSet rs)
			throws Exception
	{
		int i = 0;
		CombinedWriter writer = new CombinedXMLWriter(10000,10000,"cpx","dev");
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
				++i;
				EVCombinedRec rec = new EVCombinedRec();

				accessNumber = rs.getString("ACCESSNUMBER");

				if(accessNumber !=null && accessNumber.length()>5 && !(accessNumber.substring(0,6).equals("200138")))
				{
					rec.put(EVCombinedRec.ACCESSION_NUMBER, accessNumber);

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
	            Class.forName(driver);
	            Connection con = DriverManager.getConnection(connectionURL,
	                                              username,
	                                              password);
	            return con;
     }

}