import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;


/*
 * @Author: HT
 * @Date: Tuesday 08/13/2018
 * @Description: Matt asked to provide him Inspec Thes preferred & non preferred term file
 * the file shoudl have the "main_term_search" and "UFT/LeadIn-terms" ONLY
 */
public class InspecTermsWzLIForMatt 
{

	static String url = "jdbc:oracle:thin:@localhost:1521:eid";
	static String driver = "oracle.jdbc.driver.OracleDriver";
	static String username = "db_inspec";
	static String password = "Ev360Park";
	
	public static void main(String[] args) 
	{
		if(args.length > 2)
		{
			if(args[0] !=null)
			{
				url = args[0];
			}

			if(args[1] !=null)
			{
				driver = args[1];
			}
			if(args[2] !=null)
			{
				username = args[2];
			}

			if( args[3] != null)
			{
				password = args[3];
			}
		}
		else
		{
			System.out.println("Not enough parameters !");
			System.exit(1);
		}
		

		InspecTermsWzLIForMatt obj = new InspecTermsWzLIForMatt();
		obj.getInspecPreferedTerms();
	}
	
	public void getInspecPreferedTerms()
	{
		Statement stmt = null;
		Connection con = null;
		ResultSet rs = null;
		
		String main_term;
		String[] leadins;
		
		Label lable1=null, lable2=null;
		int j=0;
		
		WritableWorkbook workBook = null;
		
		try
		{
			File out = new File("inspec_terms.xls");
			workBook = Workbook.createWorkbook(out);
			WritableSheet sheet = workBook.createSheet("inspec_Thes_terms", 0);
			String query = "select main_term_search,leadin_terms from inspec_thesaurus_2018 order by t_id";
			con = getConnection(url, driver, username, password);
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			
			System.out.println("Got Records....");
			
			sheet.addCell(new Label(0, 0, "MAIN_TERM_SEARCH"));
			sheet.addCell(new Label(1,0,"LEADIN_TERMS"));
			
			

			while(rs.next())
			{
				main_term = rs.getString("MAIN_TERM_SEARCH");
				if(rs.getString("LEADIN_TERMS") !=null)
				{
					leadins = rs.getString("LEADIN_TERMS").split(";");
					for(int i=0;i<leadins.length;i++)
					{	
						lable1 = new Label(0, j+1, main_term);
						lable2 = new Label(1,j+1,leadins[i]);
						
						sheet.addCell(lable1);
						sheet.addCell(lable2);
						
						j++;
					}
				}
				else
				{
					lable1 = new Label(0, j+1, main_term);
					lable2 = new Label(1,j+1,"");
					
					sheet.addCell(lable1);
					sheet.addCell(lable2);
					j++;
				}
				
				
			}
			workBook.write();
			
		}
		catch(SQLException ex)
		{
			ex.printStackTrace();
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
			if(workBook !=null)
			{
				try
				{
					workBook.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	protected static Connection getConnection(String connectionURL, String driver, String username, String password) throws Exception
	{
		Class.forName(driver);
		Connection con = DriverManager.getConnection(connectionURL, username, password);
		return con;
	}
	
}
