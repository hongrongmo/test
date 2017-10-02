package cafe;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class TestDBConnection {

	String connectionURL = "jdbc:oracle:thin:@localhost:1521:eid";
	String driver = "oracle.jdbc.driver.OracleDriver";
	String username = "db_cafe";
	String password = "ny5av";
	
	public static void main(String[] args) {
	

		TestDBConnection obj = new TestDBConnection();
		try
		{
			obj.getCitationTitle();
		}
		
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}

	public void getCitationTitle() throws Exception
	{
		Connection con;
		Statement stmt = null;
		ResultSet rs = null;

		try
		{

			con = getConnection(connectionURL, driver, username, password);
			stmt = con.createStatement();
			rs = stmt.executeQuery("select citationtitle from cafe_master where accessnumber in (select CAFE_AN from REAL_TITLE_DIFF3) and rownum<11");
			
			while(rs.next())
			{
				System.out.println(rs.getString("CITATIONTITLE"));
			}
			rs.close();
			stmt.close();
		}
		
		
		
		catch(SQLException ex)
		{
			ex.printStackTrace();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
		public Connection getConnection(String connectionURL,String driver,String username,String password)	throws Exception
		{
			Class.forName(driver);
			Connection con = DriverManager.getConnection(connectionURL,username,password);
			return con;
		}
}
