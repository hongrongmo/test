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
    static String url="jdbc:oracle:thin:@jupiter:1521:eidb1";
    static String driver="oracle.jdbc.driver.OracleDriver";
    static String username="ap_correction";
    static String password="ei3it";
    static String database;


    public static void main(String args[])
        throws Exception
    {
		String fileToBeLoaded 	= null;
		int updateNumber   		= 0;
		String tableToBeTruncated = "bd_master_temp,deleted_lookupIndex";

		if(args.length>7)
		{
			if(args[4]!=null)
			{
				url = args[4];
			}
			if(args[5]!=null)
			{
				driver = args[5];
			}
			if(args[6]!=null)
			{
				username = args[6];
			}
			if(args[7]!=null)
			{
				password = args[7];
			}
		}

		if(args.length>3)
		{
			if(args[0]!=null)
			{
				fileToBeLoaded = args[0];
			}
			else
			{
				System.out.println("please enter a filename");
				System.exit(1);
			}


			if(args[1]!=null)
			{
				tableToBeTruncated = args[1];
			}
			else
			{
				System.out.println("please enter a temp table name");
				System.exit(1);
			}

			if(args[2]!=null)
			{
				database = args[2];
			}
			else
			{
				System.out.println("please enter a database name");
				System.exit(1);
			}

			if(args[3]!=null && args[3].length()>0)
			{
				Pattern pattern = Pattern.compile("^\\d*$");
				Matcher matcher = pattern.matcher(args[6]);
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
		}
		else
		{
			System.out.println("not enough parameters");
			System.exit(1);
		}


		try
		{
			BdCorrection bdc = new BdCorrection();
			FastSearchControl.BASE_URL = "http://ei-stage.bos3.fastsearch.net:15100";
			con = bdc.getConnection(url,driver,username,password);

			/**********delete all data from temp table *************/

			bdc.cleanUp(tableToBeTruncated);

			/***********load data into temp table ****************/

			BaseTableDriver c = new BaseTableDriver(updateNumber,database);
        	c.writeBaseTableFile(fileToBeLoaded);
			String dataFile=fileToBeLoaded+"."+updateNumber+".out.1";
			File f = new File(dataFile);
			if(!f.exists())
			{
				System.out.println("datafile "+dataFile+" does not exists");
				System.exit(1);
			}
        	Runtime r = Runtime.getRuntime();
			r.exec("correctionFileLoader.sh "+dataFile);
			int tempTableCount = bdc.getTempTableCount(tableToBeTruncated);
			if(tempTableCount>0)
			{
				System.out.println(tempTableCount+" records was loaded into the temp table");
				bdc.runCorrection(dataFile,updateNumber);
			}
			else
			{
				System.out.println("no record was loaded into the temp table");
				System.exit(1);
			}
			bdc.processLookupIndex(bdc.getLookupData("update"),bdc.getLookupData("backup"));
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

    private void runCorrection(String fileName,int updateNumber)
    {
		CallableStatement pstmt = null;
		boolean blnResult = false;
		try
		{
			pstmt = con.prepareCall("{ update_bd_backup_table(?)}");
			pstmt.setInt(1,updateNumber);
			pstmt.executeUpdate();

			pstmt = con.prepareCall("{ update_bd_temp_table(?,?)}");
			pstmt.setInt(1,updateNumber);
			pstmt.setString(2,fileName);
			pstmt.executeUpdate();

			pstmt = con.prepareCall("{ update_bd_master_table(?)}");
			pstmt.setInt(1,updateNumber);
			pstmt.executeUpdate();
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

    private int getTempTableCount(String tableToBeTruncate)
	{
		Statement stmt = null;
		String[] tableName = null;
		int count = 0;
		ResultSet rs = null;
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
				if(tableName[i]!=null && tableName[i].toUpperCase().indexOf("TEMP")>-1)
				{
					rs = stmt.executeQuery("select count(*) count from "+tableName[i]);
					if(rs.next())
					{
						count = rs.getInt("count");
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
		Statement stmt = null;
		try
		{
			stmt = con.createStatement();
			if(data!=null)
			{
				for(int i=0;i<data.size();i++)
				{
					String term = (String)data.get(i);
					if(term != null && field != null && database != null)
					{
						stmt.executeUpdate("insert into deleted_lookupIndex(field,term,database) values('"+field+"','"+term+"','"+database+"')");
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
		database = (String)update.get("DATABASE");
		HashMap outputMap = new HashMap();
		HashMap deletedAuthorLookupIndex 		= getDeleteData(update,backup,"AUTHOR");
		HashMap deletedAffiliationLookupIndex 	= getDeleteData(update,backup,"AFFILIATION");
		HashMap deletedControlltermLookupIndex 	= getDeleteData(update,backup,"CONTROLLEDTERM");
		HashMap deletedPublisherNameLookupIndex = getDeleteData(update,backup,"PUBLISHERNAME");
		HashMap deletedSerialtitleLookupIndex 	= getDeleteData(update,backup,"SERIALTITLE");
		saveDeletedData("AU",checkFast(deletedAuthorLookupIndex,"AU",database),database);
		saveDeletedData("AF",checkFast(deletedAffiliationLookupIndex,"AF",database),database);
		saveDeletedData("CV",checkFast(deletedControlltermLookupIndex,"CV",database),database);
		saveDeletedData("PN",checkFast(deletedPublisherNameLookupIndex,"PN",database),database);
		saveDeletedData("ST",checkFast(deletedSerialtitleLookupIndex,"ST",database),database);
	}

	private List checkFast(HashMap inputMap, String searchField, String database) throws Exception
	{

		List outputList = new ArrayList();
		DatabaseConfig databaseConfig = DatabaseConfig.getInstance(DriverConfig.getDriverTable());
		String[] credentials = new String[]{"CPX","INS","NTI","UPA","EUP"};
		String[] dbName = {database};
		int intDbMask = databaseConfig.getMask(dbName);

		Iterator searchTerms = inputMap.keySet().iterator();

		while (searchTerms.hasNext())
		{
			SearchControl sc = new FastSearchControl();
			String term1 = (String) searchTerms.next();
			int oc = Integer.parseInt((String)inputMap.get(term1));
			Query queryObject = new Query(databaseConfig, credentials);
			queryObject.setDataBase(intDbMask);

			String searchID = (new GUID()).toString();
			queryObject.setID(searchID);
			queryObject.setSearchType(Query.TYPE_QUICK);
        	//queryObject.setAutoStemming("on");
        	queryObject.setSearchPhrase(term1,searchField,"","","","","","");
			System.out.println("Term1= "+term1+" searchField= "+searchField);

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
			System.out.println("Count= "+c);

		}

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
			System.out.println("BACKUP SIZE= "+backupList.size());
			System.out.println("UPSATE SIZE= "+updateList.size());
			String dData = null;
			for(int i=0;i<backupList.size();i++)
			{
				dData = (String)backupList.get(i);
				//System.out.println("term= "+dData);
				//if(updateList.indexOf(dData)<0)
				if(!checkUpdate(updateList,dData))
				{
					System.out.println("***term****= "+dData);
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
		return deleteLookupIndex;
	}

	private boolean checkUpdate(List update,String term)
	{
		for(int i=0;i<update.size();i++)
		{
			String updateDate = (String)update.get(i);
			if(term.equalsIgnoreCase(updateDate))
			{
				return true;
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
				rs = stmt.executeQuery("select ACCESSNUMBER,AUTHOR,AUTHOR_1,AFFILIATION,AFFILIATION_1,CONTROLLEDTERM,CHEMICALTERM,SOURCETITLE,PUBLISHERNAME,DATABASE FROM BD_CORRECTION_BACKUP");

			System.out.println("Got records ...");
			results = setRecs(rs);

			System.out.println("Wrote records.");


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
		while (rs.next())
		{
			++i;
			EVCombinedRec rec = new EVCombinedRec();

			accessNumber = rs.getString("ACCESSNUMBER");
			rec.put(EVCombinedRec.ACCESSION_NUMBER, accessNumber);

			if(rs.getString("AUTHOR") != null)
			{
				String authorString = rs.getString("AUTHOR");
				if(rs.getString("AUTHOR_1") !=null)
				{
					authorString=authorString+rs.getString("AUTHOR_1");
				}
				authorList.addAll(Arrays.asList(xml.prepareBdAuthor(authorString)));
			}

			if (rs.getString("AFFILIATION") != null)
			{
				String affiliation = rs.getString("AFFILIATION");
				if(rs.getString("AFFILIATION_1")!=null)
				{
					affiliation = affiliation+rs.getString("AFFILIATION_1");
				}
				BdAffiliations aff = new BdAffiliations(affiliation);
				affiliationList.addAll(Arrays.asList(aff.getSearchValue()));

			}

			if (rs.getString("CHEMICALTERM") != null)
			{
				controltermList.addAll(Arrays.asList(xml.prepareMulti(rs.getString("CHEMICALTERM"))));
			}

			if (rs.getString("CONTROLLEDTERM") != null)
			{
			    controltermList.addAll(Arrays.asList(xml.prepareMulti(rs.getString("CONTROLLEDTERM"))));
            }

            if (rs.getString("PUBLISHERNAME") != null)
			{
			    publishernameList.add(xml.preparePublisherName(rs.getString("PUBLISHERNAME")));
            }

            if (rs.getString("SOURCETITLE") != null)
			{
				serialTitleList.add(rs.getString("SOURCETITLE"));
            }

            if(rs.getString("DATABASE") != null)
            {
				database = rs.getString("DATABASE");
			}
		}

		recs.put("AUTHOR",authorList);
		recs.put("AFFILIATION",affiliationList);
		recs.put("CONTROLLEDTERM",controltermList);
		recs.put("PUBLISHERNAME",publishernameList);
		recs.put("SERIALTITLE",serialTitleList);
		recs.put("DATABASE",database);

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