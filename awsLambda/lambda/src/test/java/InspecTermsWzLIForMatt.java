import java.io.File;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashSet;
import java.util.Set;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;


/*
 * @Author: HT
 * @Date: Tuesday 08/13/2018
 * @Description: Matt asked to provide him Inspec Thes preferred & non preferred term file
 * the file shoudl have the "main_term_search" and "UFT/LeadIn-terms" ONLY
 * 
 * NOTE: Today Thursday 09/13/2018 I had a meeting with Matt and the requirements changed
 * to be for CPX instead of Inspec, and have prefered-terms (XML tag: <DESCRIPTOR) in column A & thier
 * corresponding non-prefered terms (XML tag: <UFT>) next to it spanning in columns of same row of 
 * Preferd term (i.e. B1, C1, D1,E1,..), so i will create a nother java class CpxPrefTermsForMatt.java
 * to get this list in excel sheet
 */
public class InspecTermsWzLIForMatt 
{

	static String url = "jdbc:oracle:thin:@localhost:1521:eid";
	static String driver = "oracle.jdbc.driver.OracleDriver";
	static String username = "db_inspec";
	static String password = "";
	
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
		//obj.getInspecPreferedTerms();   // old way where pref&non-pref mixed together & leadin has common terms with them
		obj.getInspecPrefNonPrefTerms();
		
	}
	
	// Matt asked to put prefered terms separate from non-prefered terms and filter out LeadIn terms duplicate with prefered/non-prfered terms
	public void getInspecPrefNonPrefTerms()
	{
		Statement stmt = null;
		Connection con = null;
		ResultSet rs = null;
		
		
		Set<String> prefered_termsList = new LinkedHashSet<String>();
		Set<String> nonprefered_termList = new LinkedHashSet<String>();
		
		Label label1 = null, label2 = null, label3 = null;
		WritableWorkbook workBook = null;
		
		String[] leadins;
		int j=0;
		
		try
		{
			File out = new File("inspec_PrefandNonPrefTerms.xls");
			workBook = Workbook.createWorkbook(out);
			WritableSheet sheet = workBook.createSheet("inspec_Thes_terms", 0);
			String query = "select main_term_search,status,leadin_terms from inspec_thesaurus_2018 order by status,t_id";
			con = getConnection(url, driver, username, password);
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			
			System.out.println("Got Records....");
			
			sheet.addCell(new Label(0, 0, "PREFERED_TERMS"));
			sheet.addCell(new Label(1,0, "NON_PREFERED_TERMS"));
			sheet.addCell(new Label(2,0,"LEADIN_TERMS"));
			
			while(rs.next())
			{
				if(rs.getString("STATUS").equals("C"))
				{
					prefered_termsList.add(rs.getString("MAIN_TERM_SEARCH"));
					label1 = new Label(0,j+1,rs.getString("MAIN_TERM_SEARCH"));
					sheet.addCell(label1);
				}
				
				else if(rs.getString("STATUS").equals("L") || rs.getString("STATUS").contains("D"))
				{
					nonprefered_termList.add(rs.getString("MAIN_TERM_SEARCH"));
					label2 = new Label(1,j+1,rs.getString("MAIN_TERM_SEARCH"));
					sheet.addCell(label2);
				}
				
				// only add leadins if not matching with Pref/Non-pref terms
					
				if(rs.getString("LEADIN_TERMS") !=null)
				{
					leadins = rs.getString("LEADIN_TERMS").split(";");
					for(int i=0;i<leadins.length;i++)
					{	
						if(!(prefered_termsList.contains(leadins[i])) && !(nonprefered_termList.contains((leadins[i]))))
								{
									label3 = new Label(2,i+1,leadins[i]);
									sheet.addCell(label3);
								}
					}
				}
				j++;
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
