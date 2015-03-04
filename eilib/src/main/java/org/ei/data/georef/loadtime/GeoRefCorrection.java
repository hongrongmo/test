package org.ei.data.georef.loadtime;

import java.sql.Clob;
import java.sql.*;
import java.util.*;
import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.MatchResult;
import org.ei.data.*;
import org.ei.data.encompasspat.loadtime.*;
import org.ei.data.encompasslit.loadtime.*;
import org.ei.data.upt.loadtime.*;
import org.ei.data.ntis.loadtime.*;
import org.ei.data.cbnb.loadtime.*;
import org.ei.data.bd.*;
import org.ei.data.bd.loadtime.*;
import org.ei.data.inspec.loadtime.*;
import org.ei.domain.*;
import org.ei.query.base.*;
import org.ei.util.*;
import org.ei.connectionpool.*;
import java.text.*;
import java.io.*;
import java.lang.Process;
import java.util.regex.*;
import org.ei.util.GUID;
import org.ei.xml.Entity;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.Hashtable;
import java.util.Enumeration;
import org.apache.log4j.Logger;


public class GeoRefCorrection
{
    Perl5Util perl = new Perl5Util();

    private static String tablename;

	private static String currentDb;

    private static HashMap issnARFix = new HashMap();

    private int intDbMask = 1;
    private static Connection con = null;
    static String url="jdbc:oracle:thin:@neptune:1521:ei";
    static String driver="oracle.jdbc.driver.OracleDriver";
    static String username="ap_correction";
    static String password="ei3it";
    static String database;
    static String action;
    static int updateNumber=0;
    static boolean test = false;
    static String tempTable="georef_correction_temp";
    static String lookupTable="georef_deleted_lookupIndex";
    static String backupTable="georef_temp_backup";
    static String sqlldrFileName="georefCorrectionFileLoader.sh";
    static String fileToBeLoaded 	= null;
    public static final String AUDELIMITER = new String(new char[] {30});
    public static final String IDDELIMITER = new String(new char[] {31});
    public static final String GROUPDELIMITER = new String(new char[] {02});
    private static Logger log4j = Logger.getLogger(GeoRefCorrection.class.getName());

    public static void main(String args[])
        throws Exception
    {
		long startTime = System.currentTimeMillis();

		String input;
		String tableToBeTruncated = tempTable+","+backupTable+","+lookupTable;
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
				System.out.println("username= "+username);
			}
			if(args[9]!=null)
			{
				password = args[9];
				System.out.println("password= "+password);
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
				System.out.println("ACTION= "+action);
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

			GeoRefCorrection grfc = new GeoRefCorrection();
			con = grfc.getConnection(url,driver,username,password);
			if(action!=null && !(action.equals("extractupdate")||action.equals("extractdelete") ||action.equals("lookupIndex") ||action.equals("ip_delete") ||action.equals("ip_extract")))
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


				grfc.cleanUp(tableToBeTruncated);


				/************** load data into temp table ****************/

				if(test)
				{
					System.out.println("about to parse data file "+fileToBeLoaded);
					System.out.println("press enter to continue");
					System.in.read();
					Thread.currentThread().sleep(1000);
				}
				String dataFile=fileToBeLoaded+"."+updateNumber+".out";

				int tempTableCount =0;
				if(action.equalsIgnoreCase("ip_add"))
				{
					tempTableCount = grfc.getTempTableCount(updateNumber);
					int maxLoadNumber = grfc.getBiggestLoadNumber();
					if(maxLoadNumber>=updateNumber)
					{
						System.out.println("must enter a bigger load number than "+maxLoadNumber);
						System.exit(1);
					}
					if(tempTableCount==0)
					{
						grfc.loadDataIntoTempTable(fileToBeLoaded,dataFile,Integer.toString(updateNumber));
						tempTableCount = grfc.getTempTableCount(updateNumber);
						log4j.info(tempTableCount+" records for load_number "+updateNumber+" loaded into table georef_master_ip");
					}
					else
					{
						System.out.println("data for load_number "+updateNumber+" already in database");
						log4j.info("data for load_number "+updateNumber+" already in database");
						System.exit(1);
					}

				}
				else
				{
					grfc.loadDataIntoTempTable(fileToBeLoaded,dataFile,Integer.toString(updateNumber));
					tempTableCount = grfc.getTempTableCount();
				}

				if(tempTableCount>0)
				{
					if(action.equalsIgnoreCase("ip_add"))
					{
						System.out.println(tempTableCount+" records was loaded into the georef_master_ip table");
					}
					else
					{
						System.out.println(tempTableCount+" records was loaded into the temp table");
					}
					if(test)
					{
						System.out.println("begin to update tables");
						System.out.println("press enter to continue");
						System.in.read();
						Thread.currentThread().sleep(1000);
					}
					if(action.equalsIgnoreCase("ip_add"))
					{
						grfc.processInProcessData(dataFile,updateNumber,database,action);
					}
					else
					{
						grfc.runCorrection(dataFile,updateNumber,database,action);
					}
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

					grfc.processLookupIndex(grfc.getLookupData("update"),grfc.getLookupData("backup"));
				}
				else if(action.equalsIgnoreCase("delete"))
				{

					grfc.processLookupIndex(new HashMap(),grfc.getLookupData("backup"));
				}


			}

			if(action.equalsIgnoreCase("lookupIndex"))
			{
				grfc.outputLookupIndex(grfc.getLookupData("lookupIndex"),updateNumber);
				System.out.println(database+" "+updateNumber+" lookup index is done.");
			}
			else if(action.equalsIgnoreCase("extractupdate")||action.equalsIgnoreCase("extractdelete"))
			{
				grfc.doFastExtract(updateNumber,database,action);
				System.out.println(database+" "+updateNumber+" fast extract is done.");
			}
			else if(action.equalsIgnoreCase("ip_delete"))
			{
				grfc.deleteInProcessData();
				System.out.println(updateNumber+" "+database+" IN PROCESS deletion is done.");
			}
			else if(action.equalsIgnoreCase("ip_extract") )
			{
					grfc.runExtract(Integer.toString(updateNumber));
					System.out.println(updateNumber+" "+database+" IN PROCESS extract is done.");
			}
			else if(!action.equalsIgnoreCase("ip_add") && !action.equalsIgnoreCase("ip_delete"))
			{
				System.out.println(database+" "+updateNumber+" correction is done.");
				System.out.println("Please run this program again with parameter \"extractupdate\" or \"extractdelete\" to get fast extract file");
			}


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

    private int getBiggestLoadNumber()
	{
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Connection con1 = null;
		int maxLoadNumber =0;
		try
		{
			if(con1==null)
			{
				con1 = getConnection(url,driver,username,password);
			}
			stmt = con1.prepareStatement("select max(load_number) load_number from georef_master_ip");
			rs = stmt.executeQuery();

			while (rs.next())
			{
				maxLoadNumber=Integer.parseInt(rs.getString("load_number"));
			}


		}
		catch(Exception e)
		{
			log4j.info("Exception on GeoRefCorrection.getBiggestLoadNumber "+e.getMessage());
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
				catch (Exception en)
				{
					en.printStackTrace();
				}
			}

			if (rs != null)
			{
				try
				{
					rs.close();
				}
				catch (Exception en)
				{
					en.printStackTrace();
				}
			}

			if (con1 != null)
			{
				try
				{
					con1.close();
				}
				catch (Exception en)
				{
					en.printStackTrace();
				}
			}
		}
		return maxLoadNumber;
	}


    private void deleteInProcessData()
	{
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Connection con1 = null;
		try
		{
			if(con1==null)
			{
				con1 = getConnection(url,driver,username,password);
			}
			stmt = con1.prepareStatement("select updatenumber,count(*) count from georef_master_delete group by updatenumber");
			rs = stmt.executeQuery();
			int count = 0;
			int updatenumber = 0;
			while (rs.next())
			{
				updatenumber = rs.getInt("updatenumber");
				count = rs.getInt("count");
				log4j.info("**** Total "+count+" records will be deleted from georef_master table for updatenumber "+updatenumber+ "****");
			}

			deleteData();

			if(count==0)
			{
				System.out.println("**** There is no record in georef_master_delete table ****");
				log4j.info("**** There is no record in georef_master_delete table ****");
			}
			else
			{
				processLookupIndex(new HashMap(),getLookupData("ip_delete"));
			}
		}
		catch(Exception e)
		{
			log4j.info("Exception on GeoRefCorrection.deleteInProcessData "+e.getMessage());
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
				catch (Exception en)
				{
					en.printStackTrace();
				}
			}

			if (rs != null)
			{
				try
				{
					rs.close();
				}
				catch (Exception en)
				{
					en.printStackTrace();
				}
			}

			if (con1 != null)
			{
				try
				{
					con1.close();
				}
				catch (Exception en)
				{
					en.printStackTrace();
				}
			}
		}
	}


    private void processInProcessData(String fileName,int updateNumber,String database,String action)
	{
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String selectQuery = "select updatenumber from (select distinct updatenumber from georef_master_ip order by updatenumber desc) where rownum<3";
		String updateQuery = "update georef_master_ip set updatenumber='"+updateNumber+"' where load_number='"+updateNumber+"'";
		try
		{
			if(con==null)
			{
				con = getConnection(url,driver,username,password);
			}

			stmt = con.prepareStatement(updateQuery);
			log4j.info("run "+updateQuery);
		    stmt.executeUpdate();
			stmt = con.prepareStatement(selectQuery);
			log4j.info("run "+selectQuery);
			rs = stmt.executeQuery();
			String[] topTwoLoadnumber = new String[2];
			int i=0;
			while (rs.next())
			{
				topTwoLoadnumber[i]= rs.getString("updatenumber");
				log4j.info("Processing loadNumber "+rs.getString("updatenumber"));
				i++;

			}
			runUpdateFlag(updateNumber);
			runAddDelete(topTwoLoadnumber);
			addToMasterTable();


		}
		catch(Exception e)
		{
			log4j.info("Exception on GeoRefCorrection.processInProcessData "+e.getMessage());
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
				catch (Exception en)
				{
					en.printStackTrace();
				}
			}

			if (rs != null)
			{
				try
				{
					rs.close();
				}
				catch (Exception en)
				{
					en.printStackTrace();
				}
			}
		}
	}

	private void runUpdateFlag(int updateNumber)
	{
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Connection con1 = null;
		try
		{
			if(con1==null)
			{
				con1 = getConnection(url,driver,username,password);
			}
			stmt = con1.prepareStatement("update georef_master_ip set updateflag=document_type where updatenumber='"+updateNumber+"'");
			log4j.info("update georef_master_ip set updateflag=document_type where updatenumber='"+updateNumber+"'");
			stmt.executeUpdate();
			stmt = con1.prepareStatement("update georef_master_ip set document_type='GI' where updatenumber='"+updateNumber+"'");
			log4j.info("update georef_master_ip set document_type='GI' where updatenumber='"+updateNumber+"'");
			stmt.executeUpdate();



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
				catch (Exception en)
				{
					en.printStackTrace();
				}
			}

			if (con1 != null)
			{
				try
				{
					con1.close();
				}
				catch (Exception en)
				{
					en.printStackTrace();
				}
			}
		}

	}


	private void addRecords(String idNumber)
	{
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Connection con1 = null;
		try
		{
			if(con1==null)
			{
				con1 = getConnection(url,driver,username,password);
			}
			stmt = con1.prepareStatement("insert into georef_master_orig select * from georef_master_add where id_number='"+idNumber+"'");
			stmt.executeUpdate();

		}
		catch(Exception e)
		{
			try{
			log4j.info("Exception on GeoRefCorrection.addRecords  "+e.getMessage());
			//stmt = con1.prepareStatement("insert into georef_master_error values('"+idNumber+"',"+updateNumber+",Sysdate,'"+e.getMessage()+"','"+fileToBeLoaded+"'");
			stmt = con1.prepareStatement("insert into georef_correction_error values(?,?,Sysdate,?,?)");
			stmt.setString(1,idNumber);
			stmt.setInt(2,updateNumber);
			stmt.setString(3,e.getMessage());
			stmt.setString(4,fileToBeLoaded);

			stmt.executeUpdate();
			}
			catch(Exception em)
			{
				em.printStackTrace();
			}
			finally
			{
				if (stmt != null)
				{
					try
					{
						stmt.close();
					}
					catch (Exception ef)
					{
						ef.printStackTrace();
					}
				}

				if (con1 != null)
				{
					try
					{
						con1.close();
					}
					catch (Exception en)
					{
						en.printStackTrace();
					}
				}
		}
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
				catch (Exception en)
				{
					en.printStackTrace();
				}
			}

			if (con1 != null)
			{
				try
				{
					con1.close();
				}
				catch (Exception en)
				{
					en.printStackTrace();
				}
			}
		}

	}

	private void addToMasterTable()
	{
		PreparedStatement stmt = null;
		ResultSet rs = null;
		Connection con1 = null;
		try
		{
			if(con1==null)
			{
				con1 = getConnection(url,driver,username,password);
			}
			stmt = con1.prepareStatement("select id_number from georef_master_add");
			rs = stmt.executeQuery();
			List<String> idNumberList = new ArrayList();

			while (rs.next())
			{
				idNumberList.add(rs.getString("id_number"));
			}
			if(idNumberList.size()>0)
			{
				for(int i=0;i<idNumberList.size();i++)
				{
					addRecords(idNumberList.get(i));
				}
			}


		}
		catch(Exception e)
		{
			log4j.info("Exception on GeoRefCorrection.addToMasterTable "+e.getMessage());
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
				catch (Exception en)
				{
					en.printStackTrace();
				}
			}

			if (rs != null)
			{
				try
				{
					rs.close();
				}
				catch (Exception en)
				{
					en.printStackTrace();
				}
			}

			if (con1 != null)
			{
				try
				{
					con1.close();
				}
				catch (Exception en)
				{
					en.printStackTrace();
				}
			}
		}
	}

	private void runAddDelete(String[] updateNumber)
	{
		PreparedStatement stmt = null;
		String addQuery=null;
		String deleteQuery=null;
		String updateQuery=null;
		String updateQuery1=null;
		Connection con1 = null;
		try
		{
			if(con1==null)
			{
				con1 = getConnection(url,driver,username,password);
			}
			if(updateNumber.length==2 && updateNumber[0]!=null && updateNumber[1]!=null)
			{
			  addQuery = "insert into georef_master_add select * from georef_master_ip where updatenumber='"+updateNumber[0]+"' and id_number in(select id_number from georef_master_ip where updatenumber='"+updateNumber[0]+"' minus select id_number from georef_master_ip where updatenumber='"+updateNumber[1]+"')";
			  log4j.info("Run Query \"insert into georef_master_add select * from georef_master_ip where updatenumber='"+updateNumber[0]+"' and id_number in(select id_number from georef_master_ip where updatenumber='"+updateNumber[0]+"' minus select id_number from georef_master_ip where updatenumber='"+updateNumber[1]+"'\" to create georef_master_add table" );
			  deleteQuery = "insert into georef_master_delete select * from georef_master_ip where updatenumber='"+updateNumber[1]+"' and id_number in(select id_number from georef_master_ip where updatenumber='"+updateNumber[1]+"' minus select id_number from georef_master_ip where updatenumber='"+updateNumber[0]+"' )";
			  log4j.info("Run Query \"insert into georef_master_delete select * from georef_master_ip where updatenumber='"+updateNumber[1]+"' and id_number in(select id_number from georef_master_ip where updatenumber='"+updateNumber[1]+"' minus select id_number from georef_master_ip where updatenumber='"+updateNumber[0]+"'\" to create georef_master_delete table" );
			  updateQuery = "update georef_master_add set PERSON_MONOGRAPH=replace(PERSON_MONOGRAPH,'?',' '),PERSON_ANALYTIC=replace(PERSON_ANALYTIC,'?',' '),PERSON_COLLECTION=replace(PERSON_COLLECTION,'?',' ')  where PERSON_MONOGRAPH like'%?%' or PERSON_ANALYTIC like'%?%' or PERSON_COLLECTION  like'%?%;'";
			  updateQuery1 ="update georef_master_delete set m_id=(select m_id from georef_master_orig where id_number=georef_master_delete.id_number and document_type='GI') where exists(select m_id from georef_master_orig where id_number=georef_master_delete.id_number and document_type='GI')";
			  log4j.info("Run Query update georef_master_delete set m_id=(select m_id from georef_master_orig where id_number=georef_master_delete.id_number) where exists(select m_id from georef_master_orig where id_number=georef_master_delete.id_number)" );
		      stmt = con1.prepareStatement(addQuery);
		      stmt.executeUpdate();
		      stmt = con1.prepareStatement(deleteQuery);
		      stmt.executeUpdate();
		      stmt = con1.prepareStatement(updateQuery);
		      stmt.executeUpdate();
			  stmt = con1.prepareStatement(updateQuery1);
		      stmt.executeUpdate();
			  runExtract(updateNumber[0]);

		    }
		    else
		    {
				log4j.info("No Load Number");
			}




		}
		catch(Exception e)
		{
			log4j.info("Exception on GeoRefCorrection.runAddDelete "+e.getMessage());
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
				catch (Exception en)
				{
					en.printStackTrace();
				}
			}

			if (con1 != null)
			{
				try
				{
					con1.close();
				}
				catch (Exception en)
				{
					en.printStackTrace();
				}
			}


		}



	}

	private void deleteRecord(String m_id)
	{
		PreparedStatement stmt = null;
		String sqlQuery1 = "delete from georef_master_orig where m_id='"+m_id+"'";
		String sqlQuery2 = "delete from saved_records where guid='"+m_id+"'";
		String sqlQuery3 = "insert into  georef_master_backup select * from georef_master_orig where m_id='"+m_id+"'";
		String sqlQuery4 = "insert into  saved_records_backup select * from saved_records where guid='"+m_id+"'";
		Connection con1 = null;
		try
		{
			if(con1==null)
			{
				con1 = getConnection(url,driver,username,password);
			}
			con1.setAutoCommit(false);
			stmt = con1.prepareStatement(sqlQuery4);
			stmt.executeUpdate();
			stmt = con1.prepareStatement(sqlQuery3);
		    stmt.executeUpdate();
			stmt = con1.prepareStatement(sqlQuery2);
		    stmt.executeUpdate();
		    stmt = con1.prepareStatement(sqlQuery1);
		    stmt.executeUpdate();
		    con1.commit();

		}
		catch(Exception e)
		{
			log4j.info("Exception on GeoRefCorrection.deleteRecord "+e.getMessage());
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
				catch (Exception en)
				{
					en.printStackTrace();
				}
			}

			if (con1 != null)
			{
				try
				{
					con1.close();
				}
				catch (Exception en)
				{
					en.printStackTrace();
				}
			}
		}

	}

	private void deleteData()
	{
		Statement stmt = null;
		String sqlQuery = "select * from georef_master_delete";
		ResultSet rs = null;
		Connection con1=null;
		try
		{
			if(con1==null)
			{
				con1 = getConnection(url,driver,username,password);
			}
			stmt = con1.createStatement();
			rs = stmt.executeQuery(sqlQuery);
			String m_id = "";
			while (rs.next())
			{
				m_id= rs.getString("m_id");

				if(checkRecord(m_id))
				{
					deleteRecord(m_id);
				}
			}

		}
		catch(Exception e)
		{
			log4j.info("Exception on GeoRefCorrection.deleteData "+e.getMessage());
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
				catch (Exception en)
				{
					en.printStackTrace();
				}
			}

			if (rs != null)
			{
				try
				{
					rs.close();
				}
				catch (Exception en)
				{
					en.printStackTrace();
				}
			}

			if (con1 != null)
			{
				try
				{
					con1.close();
				}
				catch (Exception en)
				{
					en.printStackTrace();
				}
			}
		}


	}

	private boolean checkRecord(String m_id)
	{
		Statement stmt = null;
		String sqlQuery = "select id_number from georef_master_orig where m_id='"+m_id+"'";
		ResultSet rs = null;
		Connection con1 = null;
		try
		{
			if(con1==null)
			{
				con1 = getConnection(url,driver,username,password);
			}
			stmt = con.createStatement();
			rs = stmt.executeQuery(sqlQuery);
			String accessnumber = null;
			int inFast=0;
			while (rs.next())
			{
				accessnumber=rs.getString("id_number");
			}
			if(accessnumber != null)
			{
				Thread.currentThread().sleep(250);
				inFast = checkFast(accessnumber,"an","grf");
				if(inFast<1)
				{
					return true;
				}
				else
				{
					System.out.println("****record "+m_id+" is still in Fast****");

				}
			}
			else
			{
				System.out.println("****record "+m_id+" is not in georef_master_orig****");
			}

		}
		catch(Exception e)
		{
			log4j.info("Exception on GeoRefCorrection.checkRecord "+e.getMessage());
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
				catch (Exception en)
				{
					en.printStackTrace();
				}
			}

			if (rs != null)
			{
				try
				{
					rs.close();
				}
				catch (Exception en)
				{
					en.printStackTrace();
				}
			}

			if (con1 != null)
			{
				try
				{
					con1.close();
				}
				catch (Exception en)
				{
					en.printStackTrace();
				}
			}
		}
		return false;

	}

	private void addData()
	{
		PreparedStatement stmt = null;
		String sqlQuery = "insert into georef_master_orig select * from georef_master_add";
		try
		{
			if(con==null)
			{
				con = getConnection(url,driver,username,password);
			}
			stmt = con.prepareStatement(sqlQuery);
			stmt.executeUpdate();
		}
		catch(Exception e)
		{
			log4j.info("Exception on GeoRefCorrection.addData "+e.getMessage());
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
				catch (Exception en)
				{
					en.printStackTrace();
				}
			}

			if (con != null)
			{
				try
				{
					con.close();
				}
				catch (Exception en)
				{
					en.printStackTrace();
				}
			}
		}

	}

	private void runExtract(String updateNumber)
	{
		CombinedXMLWriter writer = new CombinedXMLWriter(50000,Integer.parseInt(updateNumber),"grf", "dev");

		Statement stmt = null;
		ResultSet rs = null;
		Connection con1 = null;
		try
		{
			if(con1==null)
			{
				con1 = getConnection(url,driver,username,password);
			}
			stmt = con1.createStatement();
			writer.setOperation("add");
			GeoRefCombiner c = new GeoRefCombiner(writer);
			rs = stmt.executeQuery("select * from georef_master_add");
			c.writeRecs(rs);
			writer.end();
			writer.flush();

			writer = new CombinedXMLWriter(50000,Integer.parseInt(updateNumber),"grf", "dev");
			writer.setOperation("delete");
			rs = stmt.executeQuery("select m_id from georef_master_delete");
			creatDeleteFile(rs,"grf",Integer.parseInt(updateNumber));
			writer.zipBatch();
			writer.end();
			writer.flush();

		}
		catch(Exception e)
		{
			log4j.info("Exception on GeoRefCorrection.runExtract "+e.getMessage());
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

			if (con1 != null)
			{
				try
				{
					con1.close();
				}
				catch (Exception en)
				{
					en.printStackTrace();
				}
			}
		}
	}


    private void loadDataIntoTempTable(String infile, String outputFile,String loadNumber) throws Exception
    {
		if(infile.toLowerCase().endsWith(".zip"))
		{
			System.out.println("IS ZIP FILE");
			ZipFile zipFile = new ZipFile(infile);
			Enumeration entries = zipFile.entries();
			while (entries.hasMoreElements())
			{
				ZipEntry entry = (ZipEntry)entries.nextElement();
				String fileName = entry.getName();
				outputFile = fileName+".out";
				BufferedReader in = new BufferedReader(new InputStreamReader(zipFile.getInputStream(entry), "UTF-8"));
				GeorefReader c = new GeorefReader(in,outputFile);
				c.loadNumber = loadNumber;
				Hashtable rec;
				while ((rec = c.getRecord()) != null) {

				}

				c.close();

				File f = new File(outputFile);
				if(!f.exists())
				{
					System.out.println("datafile: "+outputFile+" does not exists");
					System.exit(1);
				}

				if(test)
				{
					System.out.println("sql loader file "+outputFile+" created;");
					System.out.println("about to load data file "+outputFile);
					System.out.println("press enter to continue");
					System.in.read();
					Thread.currentThread().sleep(1000);
				}
				Runtime r = Runtime.getRuntime();

				Process p = r.exec("./"+sqlldrFileName+" "+outputFile);
				int t = p.waitFor();
			}
		}
		else
		{
			//System.out.println("It is not ZIP file");
			GeorefReader c = new GeorefReader(infile,outputFile,action);

			c.loadNumber = loadNumber;
			Hashtable rec;
			while ((rec = c.getRecord()) != null) {

			}

			c.close();

			File f = new File(outputFile);
			if(!f.exists())
			{
				System.out.println("datafile: "+outputFile+" does not exists");
				System.exit(1);
			}

			if(test)
			{
				System.out.println("sql loader file "+outputFile+" created;");
				System.out.println("about to load data file "+outputFile);
				System.out.println("press enter to continue");
				System.in.read();
				Thread.currentThread().sleep(1000);
			}
			Runtime r = Runtime.getRuntime();

			Process p = r.exec("./"+sqlldrFileName+" "+outputFile);
			int t = p.waitFor();
		}
	}

    private void outputLookupIndex(HashMap lookupData, int updateNumber)
    {

		if(lookupData.get("AUTHOR")!=null)
		{
			writeToFile((ArrayList)lookupData.get("AUTHOR"),"AUTHOR",updateNumber);
		}

		if(lookupData.get("AFFILIATION")!=null)
		{
			writeToFile((ArrayList)lookupData.get("AFFILIATION"),"AFFILIATION",updateNumber);
		}

		if(lookupData.get("CONTROLLEDTERM")!=null)
		{
			writeToFile((ArrayList)lookupData.get("CONTROLLEDTERM"),"CONTROLLEDTERM",updateNumber);
		}

		if(lookupData.get("PUBLISHERNAME")!=null)
		{
			writeToFile((ArrayList)lookupData.get("PUBLISHERNAME"),"PUBLISHERNAME",updateNumber);
		}

		if(lookupData.get("SERIALTITLE")!=null)
		{
			writeToFile((ArrayList)lookupData.get("SERIALTITLE"),"SERIALTITLE",updateNumber);
		}

	}



	private void writeToFile(List data, String field, int updateNumber)
	{
		String fileName = "./lookupindex/"+database+"/"+database+"-"+field+"-"+updateNumber+".txt";
		FileWriter out;

		File file=new File("lookupindex/"+database);


		try
		{
			if(!file.exists())
			{
				file.mkdir();
			}



			out = new FileWriter(fileName);
			System.out.println("field==> "+field);
			if(data != null)
			{
				for(int i=0;i<data.size();i++)
				{
					out.write(data.get(i)+"\n");
				}
			}
			out.close();
		}
		catch(Exception e)
		{
			log4j.info("Exception on GeoRefCorrection.writeToFile "+e.getMessage());
			e.printStackTrace();
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
			if(action.equalsIgnoreCase("update") || action.equalsIgnoreCase("extractupdate"))
			{
				System.out.println("Running the query...");
				writer.setOperation("add");
        		GeoRefCombiner c = new GeoRefCombiner(writer);
				rs = stmt.executeQuery("select * from georef_master_orig where updateNumber='"+updateNumber+"'");
				c.writeRecs(rs);
			}
			else if(action.equalsIgnoreCase("delete") || action.equalsIgnoreCase("extractdelete"))
			{
				writer.setOperation("delete");
				rs = stmt.executeQuery("select m_id from georef_master_orig where updateNumber='"+updateNumber+"' and ID_NUMBER in (select 'D'||ID_NUMBER from "+tempTable+")");
				creatDeleteFile(rs,dbname,updateNumber);
				writer.zipBatch();
			}
			writer.end();
			writer.flush();
		}
		catch(Exception e)
		{
			log4j.info("Exception on GeoRefCorrection.doFastExtract "+e.getMessage());
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
			log4j.info("Exception on GeoRefCorrection.creatDeleteFile "+e.getMessage());
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
				System.out.println("begin to execute stored procedure update_aip_backup_table");
				System.out.println("press enter to continue");
				System.in.read();
				Thread.currentThread().sleep(1000);
			}



			pstmt = con.prepareCall("{ call update_grf_backup_table(?,?)}");
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
				pstmt = con.prepareCall("{ call update_grf_temp_table(?,?)}");
				pstmt.setInt(1,updateNumber);
				pstmt.setString(2,fileName);
				//pstmt.setString(3,database);
				pstmt.executeUpdate();

				if(test)
				{
					System.out.println("begin to execute stored procedure update_bd_master_table");
					System.out.println("press enter to continue");
					System.in.read();
					Thread.currentThread().sleep(1000);
				}
				pstmt = con.prepareCall("{ call update_grf_master_table(?)}");
				pstmt.setInt(1,updateNumber);
				System.out.println("UPDATENUMBER:"+updateNumber);
				//pstmt.setString(2,database);
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
				pstmt = con.prepareCall("{ call delete_grf_master_table(?,?,?)}");
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
			log4j.info("Exception on GeoRefCorrection.runCorrection "+e.getMessage());
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
			log4j.info("Exception on GeoRefCorrection.getTempTableCount "+e.getMessage());
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



	private int getTempTableCount(int updatenumber)
	{
			Statement stmt = null;
			String[] tableName = null;
			int count = 0;
			ResultSet rs = null;

			try
			{
				stmt = con.createStatement();

				rs = stmt.executeQuery("select count(*) count from georef_master_ip where load_number='"+updatenumber+"'");
				if(rs.next())
				{
					count = rs.getInt("count");
				}

			}
			catch(Exception e)
			{
				log4j.info("Exception on IN PROGRESS GeoRefCorrection.getTempTableCount "+e.getMessage());
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
					System.out.println("truncate table "+this.tempTable);
				}

				if(i==1)
				{
					this.lookupTable=tableName[i];
					System.out.println("truncate table "+this.lookupTable);
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
			log4j.info("Exception on GeoRefCorrection.cleanUp "+e.getMessage());
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
						System.out.println("**LOOKUPINDEX   insert into "+lookupTable+" (field,term,database) values('"+field+"','"+term+"','"+database+"')");
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
			log4j.info("Exception on GeoRefCorrection.saveDeletedData "+e.getMessage());
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
		//System.out.println("****Doing Amazon testing, do not process lookup index*****");
		//command out for amazon cloud testing

		HashMap deletedAuthorLookupIndex 			= getDeleteData(update,backup,"AUTHOR");
		HashMap deletedAffiliationLookupIndex 		= getDeleteData(update,backup,"AFFILIATION");
		HashMap deletedControlltermLookupIndex 	= getDeleteData(update,backup,"CONTROLLEDTERM");
		HashMap deletedPublisherNameLookupIndex 	= getDeleteData(update,backup,"PUBLISHERNAME");
		HashMap deletedSerialtitleLookupIndex 	= getDeleteData(update,backup,"SERIALTITLE");
		saveDeletedData("AU",checkFast(deletedAuthorLookupIndex,"AU",database),database);
		saveDeletedData("AF",checkFast(deletedAffiliationLookupIndex,"AF",database),database);
		saveDeletedData("CV",checkFast(deletedControlltermLookupIndex,"CV",database),database);
		saveDeletedData("PN",checkFast(deletedPublisherNameLookupIndex,"PN",database),database);
		saveDeletedData("ST",checkFast(deletedSerialtitleLookupIndex,"ST",database),database);

	}

	private int checkFast(String term1, String searchField, String database)
	{

		List outputList = new ArrayList();

		String[] credentials = new String[]{"CPX","PCH","CHM","GEO","GRF","ELT","INS"};
		String[] dbName = {database};
		int c = 0;



		try
		{
			DatabaseConfig databaseConfig = DatabaseConfig.getInstance(DriverConfig.getDriverTable());
			int intDbMask = databaseConfig.getMask(dbName);
			if(term1 != null)
			{
				Thread.currentThread().sleep(250);
				SearchControl sc = new FastSearchControl();

				//int oc = Integer.parseInt((String)inputMap.get(term1));
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
				c = result.getHitCount();
			}

		}
		catch(Exception e)
		{
			log4j.info("Exception on GeoRefCorrection.checkFast "+e.getMessage());
			e.printStackTrace();
		}



		return c;

	}

	private List checkFast(HashMap inputMap, String searchField, String database) throws Exception
	{
		List outputList = new ArrayList();
		DatabaseConfig databaseConfig = DatabaseConfig.getInstance(DriverConfig.getDriverTable());
		String[] credentials = new String[]{"CPX","PCH","CHM","GEO","GRF","ELT","INS"};
		String[] dbName = {database};

		int intDbMask = databaseConfig.getMask(dbName);

		Iterator searchTerms = inputMap.keySet().iterator();

		while (searchTerms.hasNext())
		{
			String term1=null;
			try
			{
				Thread.currentThread().sleep(250);
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
				log4j.info("term= "+term1+" Exception on GeoRefCorrection.checkFast "+e.getMessage());
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
			if(database.equals("grf") && !action.equals("ip_add") && !action.equals("ip_delete"))
			{
				sqlString = "select * from georef_master_orig where updatenumber='"+updateNumber+"'";
				System.out.println("Processing "+sqlString);
				rs = stmt.executeQuery(sqlString);
				results = setGRFRecs(rs);

			}
			else if(database.equals("grf") && action.equals("ip_delete"))
			{
				sqlString = "select * from georef_master_delete where updatenumber='"+updateNumber+"'";
				log4j.info("run select * from georef_master_delete where updatenumber='"+updateNumber+"'");
				System.out.println("Processing "+sqlString);
				rs = stmt.executeQuery(sqlString);
				results = setGRFRecs(rs);

			}
			else
			{
				if(action.equals("lookupIndex") && updateNumber != 0 && database != null)
				{
					sqlString = "select ACCESSNUMBER,AUTHOR,AUTHOR_1,AFFILIATION,AFFILIATION_1,CONTROLLEDTERM,CHEMICALTERM,SOURCETITLE,PUBLISHERNAME,DATABASE FROM BD_MASTER where updateNumber="+updateNumber+" and database='"+database+"'";
				}
				else
				{
					sqlString = "select ACCESSNUMBER,AUTHOR,AUTHOR_1,AFFILIATION,AFFILIATION_1,CONTROLLEDTERM,CHEMICALTERM,SOURCETITLE,PUBLISHERNAME,DATABASE FROM "+backupTable;
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
			log4j.info("Exception on GeoRefCorrection.getLookupData "+e.getMessage());
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
				log4j.info("Exception on GeoRefCorrection.setInspecRecs "+e.getMessage());
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

					if(rs.getString("PERSON_MONOGRAPH") != null)
					{
						String eString = rs.getString("PERSON_MONOGRAPH");

						String otherEditors = rs.getString("PERSON_COLLECTION");
						if(otherEditors != null)
						{
							eString = eString.concat(AUDELIMITER).concat(otherEditors);
						}
						authorList.addAll(Arrays.asList(eString.split(AUDELIMITER)));
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
			log4j.info("Exception on GeoRefCorrection.setGRFRecs "+e.getMessage());
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
			log4j.info("Exception on GeoRefCorrection.setEPTRecs "+e.getMessage());
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
			log4j.info("Exception on GeoRefCorrection.setUPARecs "+e.getMessage());
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
			log4j.info("Exception on GeoRefCorrection.setNTISRecs "+e.getMessage());
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
				log4j.info("Exception on GeoRefCorrection.setCBNRecs "+e.getMessage());
				e.printStackTrace();
			}

			return recs;
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
			log4j.info("Exception on GeoRefCorrection.setRecs "+e.getMessage());
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
		 		//System.out.println("connectionURL= "+connectionURL);
		 		//System.out.println("driver= "+driver);
		 		//System.out.println("username= "+username);
		 		//System.out.println("password= "+password);

	            Class.forName(driver);
	            Connection con = DriverManager.getConnection(connectionURL,
	                                              username,
	                                              password);
	            return con;
     }

}
