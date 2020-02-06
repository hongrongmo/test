package org.ei.dataloading.upt.loadtime;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class OracleMap {

    PreparedStatement stmt = null;
    static Connection con= null;
    String URL = null ;
    String UserName=null;
    String Password=null;


    public OracleMap(String setURL, String setUserName,String setPassword)
    {
        this.URL=setURL;
        this.UserName=setUserName;
        this.Password=setPassword;
    }

    public static void main(String args[]) throws Exception {

    }

    public void open()  {
        try{

            Class.forName("oracle.jdbc.driver.OracleDriver").newInstance();
            System.out.println("URL="+URL+" UserName="+" Password="+Password);
            con = DriverManager.getConnection(URL, UserName, Password);
            //String sql = "select m_id,pn,kc,load_number from upt_master where patentid(ac,pn) = ? order by kc";
            String sql = "select m_id,pn,kc,load_number from upt_master where ac||ltrim(pn,'0') = ? order by kc";
            
            stmt = con.prepareStatement(sql);
            System.out.println("Got Connection>>>>");
        }
        catch (Exception sqle) {
           sqle.printStackTrace();
        }

    }

    public void close()
    	throws Exception
    {

         close(stmt);
         close(con);
    }

    public boolean contains(String key)
    	throws Exception
    {
        ResultSet rs = null;
        boolean hit = false;
        try
        {
           stmt.setString(1, key);
           stmt.execute();
           rs = stmt.getResultSet();
           hit = rs.next();
        }
        catch (Exception sqle)
        {
           sqle.printStackTrace();
        }
        finally
        {
			try
			{
				if(rs!=null)
					rs.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return hit;
    }

	public String[] getMID_KC(String key,
							  String kindPref)
		throws Exception
	{
		ResultSet rs = null;
		String[] mid_kc = null;
		try
		{
		   stmt.setString(1, key);
		   //stmt run this query "select m_id,pn,kc,load_number from upt_master where patentid(ac,pn) = ?";
		   stmt.execute();
		   rs = stmt.getResultSet();
		   while(rs.next())
		   {
				mid_kc = new String[2];
				mid_kc[0] = rs.getString("m_id");
				mid_kc[1] = rs.getString("kc");
				if(kindPref.equals(mid_kc))
				{
					System.out.println("got mid"+mid_kc+" kindPref="+kindPref+" mid_kc[0]="+mid_kc[0]+" mid_kc[1]="+mid_kc[1]);
					break;
				}
		   }
		}
		catch(Exception sqle)
		{
		   sqle.printStackTrace();
		}
		finally
		{
			try
			{
				if(rs!=null)
					rs.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}

		return mid_kc;
	}

	public boolean contains(String key,
							String kind)
			throws Exception
		{
			ResultSet rs = null;
			String kc = null;
			boolean result = false;
			try
			{
			   stmt.setString(1, key);
			   stmt.execute();
			   rs = stmt.getResultSet();
			   while(rs.next())
			   {
					kc = rs.getString("kc");
					if(kc != null && kind != null && kc.length()>0 && kind.length()>0 &&
						kind.substring(0,1).equalsIgnoreCase(kc.substring(0,1)))
					{
						result = true;
						break;
					}
			   }
			}
			catch(Exception sqle)
			{
			   sqle.printStackTrace();
			}
			finally
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

			return result;
	}

    private void close(Statement stmt) {

        if (stmt != null) {
            try {
                stmt.close();
            }
            catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    private void close(ResultSet rs) {

        if (rs != null) {
            try {
                rs.close();
            }
            catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }
    private void close(Connection conn) {

        if (conn != null) {
            try {
                conn.close();
            }
            catch (SQLException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }



}
