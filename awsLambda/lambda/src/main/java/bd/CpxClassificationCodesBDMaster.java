package bd;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

import org.ei.common.Constants;

/**
 * 
 * @author TELEBH
 * @ Date: 05/20/2019
 * @ Description: I have reported a classification code "21" for CPX that shows no text description/term associated to it. after reporting to Matt, he confirmed this is 
 * invalid CPX CL, and he asked to provide the full list of all CL for CPX, actually we have full CPX CL list from Thesaurus file, though the invalid ones
 * come from CPX abstract contents (ANI) so in order to get the full list of CPX CL, check them from master table
 */
public class CpxClassificationCodesBDMaster 
{
	static String url = "jdbc:oracle:thin:@localhost:1521:eid";    //for localhost
	static String driver = "oracle.jdbc.driver.OracleDriver";
	static String username = "db_xml";
	static String passwd = "xyz";
	
	public static void main(String[] args) 
	{
		if(args.length >3)
		{
			if(args[0] != null)
				url = args[0];
			System.out.println("URL:" + url);
			if(args[1] !=null)
				driver = args[1];
			if(args[2] !=null)
				username = args[2];
			System.out.println("username: " + username);
			if(args[3] !=null)
				passwd = args[3];
			System.out.println("passwd: " + passwd);
			
		}
		else
		{
			System.out.println("Not Enough Parameters!");
			System.exit(1);
		}
		CpxClassificationCodesBDMaster obj = new CpxClassificationCodesBDMaster();
		obj.getClassificationCodes();
	}
	
	
	public void getClassificationCodes()
	{
		Statement stmt;
		Connection con;
		ResultSet rs;
		Set<String> class_codes = new TreeSet<String>();
		
		String [] classificationCodes = null;
		FileWriter out = null;
		
		String query = "select CLASSIFICATIONCODE from bd_master where database='cpx'";
		try
		{
			out = new FileWriter("cpx_master_class_codes.txt");
			System.out.println("runnig query...." + query);
			con = getconnection(url, driver, username, passwd);
			stmt = con.createStatement();
			stmt.setFetchSize(1000);
			rs = stmt.executeQuery(query);
			
			while (rs.next())
			{
				if(rs.getString(1) !=null)
				{
					classificationCodes = rs.getString(1).split(Constants.AUDELIMITER);
					for(int i=0; i< classificationCodes.length; i++)
					{
						if(!(class_codes.contains(classificationCodes[i])))
						{
							class_codes.add(classificationCodes[i].trim());
							//System.out.println(classificationCodes[i]);
							
						}
					}
					
				}
			}
			System.out.println("Total # of cpx class codes: " + class_codes.size());
			
			
			for(String classCode: class_codes)
			{
				out.write(classCode + "\n");
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
				if(out !=null)
				{
					out.flush();
					out.close();
				}
				
			}
			catch(IOException ex)
			{
				ex.printStackTrace();
			}
		}
		
		
	}
	
	protected static Connection getconnection(String connectionUrl, String driver, String username, String passwd) throws Exception
	{
		Class.forName(driver);
		Connection con = DriverManager.getConnection(connectionUrl, username, passwd);
		return con;
	}

}
