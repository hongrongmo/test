package org.ei.dataloading.knovel.loadtime;

import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ei.dataloading.CombinedXMLWriter;

/**
 * 
 * @author TELEBH
 * @Date: 09/23/2016
 * @Description: Due to KNovel change in Entitlement for kna (i.e. kna changed to knc), 
 * so previous kna record still exist in Fast as "kna", eventhoug in database it is "knc"
 * so kna need to be deleted from fast
 */
public class DeleteKnaFromFast {

	
	static String tempTable = "KNOVEL_A_TEMP";
	static int updateNumber=0;
	static String dbname = "kna";
	static String url = "jdbc:oracle:thin:@localhost:1521:eid";    //for localhost
	static String driver = "oracle.jdbc.driver.OracleDriver";
	static String username = "ap_correction1";
	static String password = "ei3it"; 
	
	
	StringBuffer midList = null;
	
	public DeleteKnaFromFast()
	{
		
	}
	public static void main(String[] args) 
	{
		if(args.length >2)
		{
			if(args[0] !=null)
			{
				tempTable = args[0];
				
				System.out.println("Temp Table: " + tempTable);
			}
			if(args[1] !=null)
			{
				Pattern pattern = Pattern.compile("^\\d*$");
				Matcher matcher = pattern.matcher(args[1]);
				if(matcher.find())
				{
					updateNumber = Integer.parseInt(args[1]);
					System.out.println("updatenumber= " +  updateNumber);
				}
				else
				{
					System.out.println("Invalid updatenumber format!");
					System.exit(1);
				}
			}
			if(args[2] !=null)
			{
				dbname = args[2];
				System.out.println("dbname= " + dbname);
			}
		}
		
		if(args.length >6)
		{
			if(args[3] !=null)
			{
				url = args[3];
				System.out.println("url= " + url);
			}
			if(args[4] !=null)
			{
				driver = args[4];
			}
			if(args[5] !=null)
			{
				username = args[5];
				System.out.println("username= " + username);
			}
			if(args[6] !=null)
			{
				password = args[6];
				System.out.println("password= " + password);
			}
			else
			{
				System.out.println("not enough parameters!");
			}
		}
		DeleteKnaFromFast kc = new DeleteKnaFromFast();
		kc.deleteData();
	}

	@SuppressWarnings("resource")
	public void deleteData()
	{
		Connection con = null;
		Statement stmt = null;
		ResultSet rs = null;
		int deletesize = 0;
		
		
		 CombinedXMLWriter writer = new CombinedXMLWriter(50000,updateNumber,dbname,"dev");
	     writer.setOperation("delete");
	     
	     
		String query = "select count(m_id) count from (select m_id from knovel_master where database='kna' minus select m_id from " + tempTable + " )";
		
		
		System.out.println("Running the query...");
		System.out.println(query);
		
		
		try
		{
			con = getConnection(url, driver, username, password);
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			
			while(rs.next())
			{
				rs.getInt("count");
				deletesize= rs.getInt("count");
			}
			System.out.println("Total kna records to delete from DB & fast: " + deletesize);
			
			
			query = "select m_id from knovel_master where database='kna' minus select m_id from " + tempTable + " ";
			
			
			// ONLY send deletion to fast if there are M_id diff (otherwise no fast extraction files created)
			if(deletesize >0)
			{
				System.out.println("Running the query...");
				System.out.println(query);
				
				rs = stmt.executeQuery(query);
				
				createFastDeleteFile(rs, dbname, updateNumber);
				writer.zipBatch();
			}
			else
			{
				System.out.print("no records to delete from fast");
			}
			
			writer.end();
			writer.flush();
		}
		catch(SQLException ex)
		{
			System.out.println("error message: " + ex.getMessage() + " error code: " + ex.getErrorCode());
			ex.printStackTrace();
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

	private void createFastDeleteFile(ResultSet rs, String db, int updateNumber)
	{
		String batchId = "0001";
		File file = new File("fast");
		FileWriter out = null;
		
		midList = new StringBuffer();
		
		try
		{
			if(!file.exists())
			{
				file.mkdir();
			}
			
			String batchPath = "fast/batch_" + updateNumber+"_"+batchId;
			
			file = new File(batchPath);
			if(!(file.exists()))
			{
				file.mkdir();
			}
			
			String root = batchPath + "/EIDATA/tmp";
			file = new File(root);
			
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
	                	midList.append("'" + rs.getString("M_ID") + "',");
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
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
			if(out !=null)
			{
				try
				{
					out.close();
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
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
