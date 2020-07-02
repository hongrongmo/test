package org.ei.dataloading.cafe;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 
 * @author TELEBH
 * @Date: 05/09/2017
 * @Description: update successfully indexed AU/AF profiless' "ES_STATUS" = 'indexed" 
 * for the records that already indexed in ES so it does not re-index unless it got later update
 */
public class UpdateProfileTableESStatus {

	private static Connection con = null;

	String connectionURL = "jdbc:oracle:thin:@localhost:1521:eid";   //for localhost
	String driver = "oracle.jdbc.driver.OracleDriver";
	String username = "db_cafe";
	String password = "";


	String doc_type;
	String action;
	int loadNumber;
	String tableToBeTruncated = null;
	String sqlldrFileName = null;


	PrintWriter out;
	File outDir;
	String outFileName;

	public UpdateProfileTableESStatus()
	{

	}
	public UpdateProfileTableESStatus(String doctype,String actn, String userName, String pswd, int load_number, String tempTableName, String conUrl, String sqlldrFile)
	{
		doc_type = doctype;
		action = actn;
		username = userName;
		password = pswd;
		loadNumber = load_number;
		tableToBeTruncated = tempTableName;
		connectionURL = conUrl;
		sqlldrFileName = sqlldrFile;

		System.out.println("username= " + username);
		System.out.println("table to be truncated: " + tableToBeTruncated);


		init();
	}

	public void init()
	{
		try {
			con  = getConnection(connectionURL, driver, username, password);

			//05/10/2017 create dir to write indexed ES ID's (M_ID) to .out file to load to a temp table
			String currDir = System.getProperty("user.dir");
			outDir = new File(currDir+"/es_indexed");
			if(!(outDir.exists()))
			{
				outDir.mkdir();
			}
		}
		catch (Exception e) {

			e.printStackTrace();
		}

	}
	public void writeIndexedRecs(List<String> esDocIds)
	{
		PrintWriter out = null;
		try
		{
			// get time in epoch format to be able to distinguish which file to send to converting, in case there are multiple files to convert

			DateFormat dateFormat = new SimpleDateFormat("E, MM/dd/yyyy-hh:mm:ss a");
			Date date = dateFormat.parse(dateFormat.format(new Date()));
			Long epoch = date.getTime();

			outFileName =outDir + "/" + doc_type+"_esindexed_"+epoch+"_"+loadNumber + ".txt";

			if (esDocIds.size() >0)
			{
				out = new PrintWriter(new FileWriter(outFileName));
				for(int i=0; i<esDocIds.size();i++)
				{
					out.println(esDocIds.get(i));
				}
			}
		}

		catch (ParseException e) 
		{
			e.printStackTrace();
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		finally
		{
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
		
		updateIndexedRecsStatus();

	}


	public void updateIndexedRecsStatus()
	{
		/**********delete all data from temp table *************/

		System.out.println("about to truncate table "+tableToBeTruncated);
		cleanUp();

		updateAuthorProfileStatus(outFileName);
	}
	
	
	public static void main(String[] args) 
	{


	}

	// added 05/09/2017 to cleanup apr indexed es table  	
	private void cleanUp()
	{
		Statement stmt = null;

		try
		{
			stmt = con.createStatement();
			stmt.executeUpdate("truncate table " + tableToBeTruncated);
			System.out.println("truncate temp table " + tableToBeTruncated);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
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

	private int getTempTableCount(String tempTable)
	{
		Statement stmt = null;
		ResultSet rs = null;
		int count = 0;

		try
		{
			stmt = con.createStatement();

			rs = stmt.executeQuery("select count(*) count from " + tempTable + "");
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
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}

		return count;

	}

	// added 05/08/2017 to update ES_status=indexed for the author profiles that have been indexed in ES successfully so do not index again unless it got later update
	private void updateAuthorProfileStatus(String data_file)
	{
		CallableStatement stmt = null;
		String profileTableName = null;

		try
		{	
			/************** load data into temp table ****************/
			System.out.println("about to load data file "+data_file);

			Runtime r = Runtime.getRuntime();
			Process p = r.exec("./"+ sqlldrFileName + " " + data_file);
			int t = p.waitFor();

			//the value 0 indicates normal termination.
			System.out.println("Sqlldr process complete with exit status: " + t);

			int tempTableCount = getTempTableCount(tableToBeTruncated);
			System.out.println(tempTableCount+" records was loaded into the temp table");

			if(doc_type !=null && doc_type.equalsIgnoreCase("apr"))
				profileTableName = "author_profile";
			else if (doc_type !=null && doc_type.equalsIgnoreCase("ipr"))
				profileTableName = "institute_profile";
			else
			{
				System.out.println("Insvalid doc_type to update profile table's ES_status!");
				System.exit(1);
			}
			System.out.println("Table to updates its ES_status: " + profileTableName);

			// update DB

			if(tempTableCount >0)
			{
				System.out.println("begin to execute stored procedure UPDATE_AUAF_MASTER_ESSTATUS");

				stmt = con.prepareCall("{ call UPDATE_AUAF_MASTER_ESSTATUS(?,?,?)}");
				stmt.setString(1,doc_type);
				stmt.setString(2, profileTableName);
				stmt.setString(3, action);
				stmt.executeUpdate();

			}
			else
			{
				System.out.println("no record was loaded into the temp table");
			}

		} 
		catch (SQLException e) 
		{
			System.out.println("failed to update profile table setting ES_status='indexed'");
			System.out.println(e.getMessage());
			e.printStackTrace();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		} 
		catch (InterruptedException e) 
		{
			e.printStackTrace();
		}

		finally
		{
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
