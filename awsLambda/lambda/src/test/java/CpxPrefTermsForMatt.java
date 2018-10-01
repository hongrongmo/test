import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;


/**
 * 
 * @author TELEBH
 * @Date: Thursday 09/13/2018
 * @Description: as per Matt's description: We need a different file with only CPX preferred terms in column A and the non preferred terms listed next to the preferred term (same row) and listed horizontally in columns B to … Z 
 * I have attached an example file. I can Skype later if you want me to talk through it. We do not want any Inspec terms in it. Operations have the inspec terms in a separate file for matching to CPX. 
 * NOTE: non-prefered are the (used for terms) in CPX file which shows in EV as "For" or Lead-In terms in DB 
 */
public class CpxPrefTermsForMatt 
{
	static String url = "jdbc:oracle:thin:@localhost:1521:eid";
	static String driver = "oracle.jdbc.driver.OracleDriver";
	static String username = "db_cpx";
	static String password = "pwd";

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


		CpxPrefTermsForMatt obj = new CpxPrefTermsForMatt();
		obj.getCpxPrefNonPrefTerms();

	}

	// Matt asked to put prefered terms separate from non-prefered (used for terms) terms and filter out LeadIn terms duplicate with prefered/non-prfered terms
	public void getCpxPrefNonPrefTerms()
	{
		Statement stmt = null;
		Connection con = null;
		ResultSet rs = null;

		Label label1 = null, label2 = null;
		WritableWorkbook workBook = null;

		String[] leadins;
		int j=0;

		try
		{
			File out = new File("cpx_PrefandNonPrefTerms.xls");
			workBook = Workbook.createWorkbook(out);
			WritableSheet sheet = workBook.createSheet("inspec_Thes_terms", 0);
			String query = "select main_term_search,LEADIN_TERMS from cpx_thesaurus_2018 where status='C' order by t_id";
			con = getConnection(url, driver, username, password);
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);

			System.out.println("Got Records....");

			sheet.addCell(new Label(0, 0, "PREFERED_TERMS"));
			sheet.addCell(new Label(1,0, "NON_PREFERED_TERMS"));

			while(rs.next())
			{
				// prefered terms
				label1 = new Label(0,j+1,rs.getString("MAIN_TERM_SEARCH"));
				sheet.addCell(label1);

				// Non-Prefered (Leadin) terms					
				if(rs.getString("LEADIN_TERMS") !=null)
				{
					leadins = rs.getString("LEADIN_TERMS").split(";");
					for(int i=0;i<leadins.length;i++)
					{	

						label2 = new Label(i+1,j+1,leadins[i]);
						sheet.addCell(label2);
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


	protected static Connection getConnection(String connectionURL, String driver, String username, String password) throws Exception
	{
		Class.forName(driver);
		Connection con = DriverManager.getConnection(connectionURL, username, password);
		return con;
	}
}
