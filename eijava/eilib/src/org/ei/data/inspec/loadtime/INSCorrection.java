package org.ei.data.inspec.loadtime;

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

public class INSCorrection
{
    Perl5Util perl = new Perl5Util();

    private static String tablename;

	private static String currentDb;

    private static HashMap issnARFix = new HashMap();

    private int intDbMask = 1;
    private static Connection con = null;
    static String url="jdbc:oracle:thin:@neptune.elsevier.com:1521:ei";
    static String driver="oracle.jdbc.driver.OracleDriver";
    static String username="ap_correction";
    static String password="ei3it";
    static String database;
    static String action;
    static boolean test = false;
    static String tempTable="ins_correction_temp";
    static String lookupTable="deleted_lookupIndex";
    static String backupTable="ins_temp_backup";
    static String sqlldrFileName="InspecSqlLoaderFile.sh";

    public static void main(String args[])
        throws Exception
    {
		long startTime = System.currentTimeMillis();
		String fileToBeLoaded 	= null;
		int updateNumber   		= 0;
		String input;
		String tableToBeTruncated = "ins_correction_temp,deleted_lookupIndex,ins_temp_backup";
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

		if(args.length>10)
		{
			//System.out.println("args[6]="+args[6]);

			if(args[6]!=null)
			{
				url = args[6];
			}

			//System.out.println("args[7]="+args[7]);
			if(args[7]!=null)
			{
				driver = args[7];
			}

			//System.out.println("args[8]="+args[8]);
			if(args[8]!=null)
			{
				username = args[8];
			}

			//System.out.println("args[9]="+args[9]);
			if(args[9]!=null)
			{
				password = args[9];
			}

			//System.out.println("args[10]="+args[10]);
			if(args[10]!=null)
			{
				sqlldrFileName = args[10];
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
				//System.out.println("args[0] fileToBeLoaded= "+fileToBeLoaded);
			}

			if(args[1]!=null)
			{
				tableToBeTruncated = args[1];
				//System.out.println("args[1] tableToBeTruncated= "+fileToBeLoaded);
			}

			if(args[2]!=null)
			{
				database = args[2];
				//System.out.println("args[2] database= "+database);
			}

			if(args[3]!=null && args[3].length()>0)
			{
				Pattern pattern = Pattern.compile("^\\d*$");
				Matcher matcher = pattern.matcher(args[3]);
				if (matcher.find())
				{
					updateNumber = Integer.parseInt(args[3]);
					//System.out.println("args[3] updateNumber= "+updateNumber);
				}
				else
				{
					System.out.println("did not find updateNumber or updateNumber has wrong format");
					System.out.println("you enter "+args[3]+" as updateNumber");
					System.exit(1);
				}
			}

			if(args[4]!=null)
			{

				action = args[4];
				//System.out.println("action= "+action);
			}
			else
			{
				System.out.println("Are we doing 'update' or 'delete'");
				System.exit(1);
			}

			if(args[5]!=null)
			{
				FastSearchControl.BASE_URL = args[5];
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


		try
		{

			INSCorrection bdc = new INSCorrection();
			con = bdc.getConnection(url,driver,username,password);
			if(action!=null && !(action.equals("extractupdate")||action.equals("extractdelete")))
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

				bdc.cleanUp(tableToBeTruncated);

				/************** load data into temp table ****************/

				if(test)
				{
					System.out.println("about to parse data file "+fileToBeLoaded);
					System.out.println("press enter to continue");
					System.in.read();
					Thread.currentThread().sleep(1000);
				}

				InspecBaseTableDriver c = new InspecBaseTableDriver(updateNumber);
				c.writeBaseTableFile(fileToBeLoaded);
				String dataFile=fileToBeLoaded+".out";
				List dataFileList = new ArrayList();
				int i=0;
				while(true)
				{
					i++;
					String datafile=dataFile+"."+i;
					File f = new File(datafile);
					if(!f.exists())
					{
						if(i<1)
						{
							System.exit(1);
						}
						else
						{
							break;
						}
					}
					System.out.println("DATAFILE FOUND: "+datafile);
					dataFileList.add(datafile);

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

				for(int j=0;j<dataFileList.size();j++)
				{
					String dFile = (String)dataFileList.get(j);
					Process p = r.exec("./"+sqlldrFileName+" "+dFile);
					System.out.println("Loading File "+dFile);
					int t = p.waitFor();
					//break;
				}

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
					bdc.processLookupIndex(bdc.getLookupData("update",updateNumber),bdc.getLookupData("backup",updateNumber));
				}
				else if(action.equalsIgnoreCase("delete"))
				{

					bdc.processLookupIndex(new HashMap(),bdc.getLookupData("backup",updateNumber));
				}
				else if(action.equalsIgnoreCase("ins"))
				{
					bdc.processLookupIndex(bdc.getLookupData("ins",updateNumber),bdc.getLookupData("insBackup",updateNumber));
				}
			}
			bdc.doFastExtract(updateNumber,database,action);
			bdc.getError(updateNumber,action);

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

    private void getError(int updateNumber,String action)
    {
		Statement stmt = null;
		ResultSet rs = null;
		try
		{
			stmt = con.createStatement();
			if(action.equalsIgnoreCase("update") || action.equalsIgnoreCase("ins") )
			{
				rs = stmt.executeQuery("select ex,update_number,action_date,message,source from INS_CORRECTION_ERROR where update_number="+updateNumber);
				boolean errorFlag = false;
				int i=0;
				while (rs.next())
				{
					i++;
					if(!errorFlag)
					{
						System.out.println("*********** Error found while updating data *************");
					}
					String accessNumber = rs.getString("ex");
					String message = rs.getString("message");
					String storedProcedure = rs.getString("source");
					errorFlag=true;
					System.out.println("---- Record "+i+" ----");
					System.out.println("accessNumber: "+accessNumber);
					System.out.println("updateNumber: "+updateNumber);
					System.out.println("message: "+message);
					System.out.println("storedProcedure: "+storedProcedure);
				}

				if(!errorFlag)
				{
					System.out.println("*********** No Error found  *************");
				}
			}

		}
		catch (SQLException e)
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

	}

    private void doFastExtract(int updateNumber,String dbname,String action) throws Exception
    {
		CombinedXMLWriter writer = new CombinedXMLWriter(50000,
		                                              updateNumber,
		                                              dbname,
		                                              "dev");

        Statement stmt = null;
        ResultSet rs = null;
        try
		{
			stmt = con.createStatement();
			if(action.equalsIgnoreCase("update") || action.equalsIgnoreCase("extractupdate") || action.equalsIgnoreCase("ins") )
			{
				System.out.println("Running the query...");
				writer.setOperation("add");
        		INSPECCombiner c = new INSPECCombiner(writer);
				rs = stmt.executeQuery("select m_id, fdate, opan, copa, ppdate,sspdate, aaff, afc, su, pubti, pfjt, pajt, sfjt, sajt, ab, anum, aoi, aus, aus2, pyr, rnum, pnum, cpat, ciorg, iorg, pas, pcdn, scdn, cdate, cedate, pdoi, nrtype, chi, pvoliss, pvol, piss, pipn, cloc, cls, cvs, eaff, eds, fls, la, matid, ndi, pspdate, ppub, rtype, sbn, sorg, psn, ssn, tc, sspdate, ti, trs, trmc,aaffmulti1, aaffmulti2, eaffmulti1, eaffmulti2, nssn, npsn, LOAD_NUMBER, seq_num, ipc from new_ins_master where  length(ipc)>1");
				c.writeRecs(rs);
			}
			else if(action.equalsIgnoreCase("delete") || action.equalsIgnoreCase("extractdelete"))
			{
				writer.setOperation("delete");
				rs = stmt.executeQuery("select m_id from ins_master_orig where updateNumber='"+updateNumber+"' and anum in (select 'D'||anum from "+tempTable+")");
				creatDeleteFile(rs,dbname,updateNumber);
				writer.zipBatch();
			}
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

    private void creatDeleteFile(ResultSet rs,String database,int updateNumber)
    {

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
			String root = batchPath +"/EIDATA/tmp";
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
		boolean blnResult = false;
		try
		{
			if(test)
			{
				System.out.println("begin to execute stored procedure update_ins_backup_table");
				System.out.println("press enter to continue");
				System.in.read();
				Thread.currentThread().sleep(1000);
			}


			pstmt = con.prepareCall("{ call update_ins_backup_table(?)}");
			pstmt.setInt(1,updateNumber);
			pstmt.executeUpdate();


			if(action != null && action.equalsIgnoreCase("update"))
			{
				if(test)
				{
					System.out.println("begin to execute stored procedure update_ins_temp_table");
					System.out.println("press enter to continue");
					System.in.read();
					Thread.currentThread().sleep(1000);
				}
				pstmt = con.prepareCall("{ call update_ins_temp_table(?,?)}");
				pstmt.setInt(1,updateNumber);
				pstmt.setString(2,fileName);
				pstmt.executeUpdate();

				if(test)
				{
					System.out.println("begin to execute stored procedure update_ins_master_table");
					System.out.println("press enter to continue");
					System.in.read();
					Thread.currentThread().sleep(1000);
				}
				pstmt = con.prepareCall("{ call update_ins_master_table(?)}");
				pstmt.setInt(1,updateNumber);
				pstmt.executeUpdate();
			}
			else if(action != null && action.equalsIgnoreCase("ins"))
			{
				if(test)
				{
					System.out.println("begin to execute stored procedure update_ins_temp_table");
					System.out.println("press enter to continue");
					System.in.read();
					Thread.currentThread().sleep(1000);
				}
				pstmt = con.prepareCall("{ call update_ins_temp_table(?,?)}");
				pstmt.setInt(1,updateNumber);
				pstmt.setString(2,fileName);
				pstmt.executeUpdate();

				if(test)
				{
					System.out.println("begin to execute stored procedure update_ins_master_table");
					System.out.println("press enter to continue");
					System.in.read();
					Thread.currentThread().sleep(1000);
				}
				pstmt = con.prepareCall("{ call update_ins_master_table(?)}");
				pstmt.setInt(1,updateNumber);
				pstmt.executeUpdate();
			}
			else if(action != null && action.equalsIgnoreCase("delete"))
			{
				if(test)
				{
					System.out.println("begin to execute stored procedure delete_ins_master_table");
					System.out.println("press enter to continue");
					System.in.read();
					Thread.currentThread().sleep(1000);
				}
				pstmt = con.prepareCall("{ call delete_ins_master_table(?,?)}");
				pstmt.setInt(1,updateNumber);
				pstmt.setString(2,fileName);
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

    private int getTempTableCount()
	{
		Statement stmt = null;
		String[] tableName = null;
		int count = 0;
		ResultSet rs = null;

		try
		{
			stmt = con.createStatement();
			String sqlQuery = "select count(*) count from INS_CORRECTION_TEMP";
			System.out.println("**Query**"+sqlQuery);
			rs = stmt.executeQuery(sqlQuery);
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

				if(i==0)
				{
					this.tempTable=tableName[i];
					System.out.println("truncate temp table "+this.tempTable);
				}

				if(i==1)
				{
					this.lookupTable=tableName[i];
					System.out.println("truncate lookup table "+this.lookupTable);
				}

				if(i==2)
				{
					this.backupTable=tableName[i];
					System.out.println("truncate backup table "+this.backupTable);
				}

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

    private void processLookupIndex(HashMap update,HashMap backup) throws Exception
    {

		database = this.database;

		HashMap outputMap = new HashMap();
		HashMap deletedAuthorLookupIndex 			= getDeleteData(update,backup,"AUTHOR");
		HashMap deletedAffiliationLookupIndex 		= getDeleteData(update,backup,"AFFILIATION");
		//HashMap deletedControlltermLookupIndex 	= getDeleteData(update,backup,"CONTROLLEDTERM");
		//HashMap deletedPublisherNameLookupIndex 	= getDeleteData(update,backup,"PUBLISHERNAME");
		//HashMap deletedSerialtitleLookupIndex 	= getDeleteData(update,backup,"SERIALTITLE");
		saveDeletedData("AU",checkFast(deletedAuthorLookupIndex,"AU",database),database);
		saveDeletedData("AF",checkFast(deletedAffiliationLookupIndex,"AF",database),database);
		//saveDeletedData("CV",checkFast(deletedControlltermLookupIndex,"CV",database),database);
		//saveDeletedData("PN",checkFast(deletedPublisherNameLookupIndex,"PN",database),database);
		//saveDeletedData("ST",checkFast(deletedSerialtitleLookupIndex,"ST",database),database);
	}

	private List checkFast(HashMap inputMap, String searchField, String database) throws Exception
	{

		List outputList = new ArrayList();
		DatabaseConfig databaseConfig = DatabaseConfig.getInstance(DriverConfig.getDriverTable());
		String[] credentials = new String[]{"INS"};
		String[] dbName = {database};

		int intDbMask = databaseConfig.getMask(dbName);

		Iterator searchTerms = inputMap.keySet().iterator();

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
				int pagesize = 25;
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
				System.out.println("term1= "+term1);
				e.printStackTrace();
			}

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


    public HashMap getLookupData(String action,int updateNumber) throws Exception
    {
		Statement stmt = null;
		ResultSet rs = null;
		HashMap results = null;
		try
		{
			stmt = con.createStatement();
			System.out.println("Running the query...");
			if(action.equals("update")||action.equals("ins"))
			{
				rs = stmt.executeQuery("select m_id, fdate, opan, copa, ppdate,sspdate, aaff, afc, su, pubti, pfjt, pajt, sfjt, sajt, ab, anum, aoi, aus, aus2, pyr, rnum, pnum, cpat, ciorg, iorg, pas, pcdn, scdn, cdate, cedate, pdoi, nrtype, chi, pvoliss, pvol, piss, pipn, cloc, cls, cvs, eaff, eds, fls, la, matid, ndi, pspdate, ppub, rtype, sbn, sorg, psn, ssn, tc, sspdate, ti, trs, trmc,aaffmulti1, aaffmulti2, eaffmulti1, eaffmulti2, nssn, npsn, LOAD_NUMBER, seq_num, ipc from ins_master_orig where seq_num is not null and updateNumber='"+updateNumber+"'");
				//rs = stmt.executeQuery("select anum,AUS,AUS2,AFFILIATION,AFFILIATION_1,CONTROLLEDTERM,CHEMICALTERM,SOURCETITLE,PUBLISHERNAME,DATABASE,IPC FROM "+tempTable);
			}
			else
			{
				rs = stmt.executeQuery("select m_id, fdate, opan, copa, ppdate,sspdate, aaff, afc, su, pubti, pfjt, pajt, sfjt, sajt, ab, anum, aoi, aus, aus2, pyr, rnum, pnum, cpat, ciorg, iorg, pas, pcdn, scdn, cdate, cedate, pdoi, nrtype, chi, pvoliss, pvol, piss, pipn, cloc, cls, cvs, eaff, eds, fls, la, matid, ndi, pspdate, ppub, rtype, sbn, sorg, psn, ssn, tc, sspdate, ti, trs, trmc,aaffmulti1, aaffmulti2, eaffmulti1, eaffmulti2, nssn, npsn, LOAD_NUMBER, seq_num, ipc from " + backupTable);
				//rs = stmt.executeQuery("select ACCESSNUMBER,AUTHOR,AUTHOR_1,AFFILIATION,AFFILIATION_1,CONTROLLEDTERM,CHEMICALTERM,SOURCETITLE,PUBLISHERNAME,DATABASE,IPC FROM "+backupTable);
			}

			System.out.println("Got records ...");
			results = setRecs(rs);

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

	public HashMap setRecs(ResultSet rs)
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
					rec.put(EVCombinedRec.ACCESSION_NUMBER, accessNumber);

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
						rec.put(EVCombinedRec.AUTHOR,c.prepareAuthor(aus.toString()));
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

						rec.put(EVCombinedRec.AUTHOR_AFFILIATION, c.prepareAuthor(aaff.toString()));
					}

					if(rs.getString("cvs") != null)
					{
					     rec.put(EVCombinedRec.CONTROLLED_TERMS, c.prepareMulti(rs.getString("cvs")));
                	}

					if(rs.getString("ppub") != null)
					{
					     rec.put(EVCombinedRec.PUBLISHER_NAME, rs.getString("ppub"));
                    }

                    if(rs.getString("chi") != null)
					{
					      rec.put(EVCombinedRec.CHEMICAL_INDEXING,c.prepareIndexterms(rs.getString("chi")));
                	}

					if(rs.getString("pubti") != null)
					{
						rec.put(EVCombinedRec.SERIAL_TITLE, rs.getString("pubti"));
					}

					if(rs.getString("pfjt") != null)
					{
						rec.put(EVCombinedRec.SERIAL_TITLE, rs.getString("pfjt"));
					}


					//if(rs.getString("DATABASE") != null)
					//{
					//	database = rs.getString("DATABASE");
					//}


					if(rs.getString("ipc")!=null)
					{
						String ipcString = rs.getString("ipc");
						ipcString = perl.substitute("s/\\//SLASH/g", ipcString);
						rec.put(EVCombinedRec.INT_PATENT_CLASSIFICATION, ipcString);
					}

				}
			}

			//recs.put("AUTHOR",authorList);
			//recs.put("AFFILIATION",affiliationList);
			//recs.put("CONTROLLEDTERM",controltermList);
			//recs.put("PUBLISHERNAME",publishernameList);
			//recs.put("SERIALTITLE",serialTitleList);
			//recs.put("DATABASE",database);
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
		 		//System.out.println("URL= "+connectionURL+" driver= "+driver+" username= "+username+" password= "+password);
	            Class.forName(driver);
	            Connection con = DriverManager.getConnection(connectionURL,
	                                              username,
	                                              password);
	            return con;
     }

}