package org.ei.util;


import org.ei.domain.*;
import java.sql.*;


public class RefCheck
{

	public static void main(String args[])
	{
		String driver = args[0];
		String url = args[1];
		String user = args[2];
		String pass = args[3];
		String mid = args[4];

		PreparedStatement pstmt = null;
		Connection con = null;
		ResultSet rs = null;

		try
		{
			Class.forName(driver).newInstance();
			con = DriverManager.getConnection(url,user,pass);
			String sql = "select * from patent_refs where cit_mid = ?";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, mid);
			rs = pstmt.executeQuery();
			int totalLocal = 0;
			int totalFast = 0;
			while(rs.next())
			{
				String patnum = rs.getString("CIT_PN");
				String au = rs.getString("CIT_CY");
				String prt = rs.getString("PRT_MID");
				totalLocal++;
				if(!atFast(au, patnum, prt))
				{
					System.out.println("Record not found:"+prt);
				}
				else
				{
					totalFast++;

				}
			}

			System.out.println("Local total:"+Integer.toString(totalLocal));
			System.out.println("Fast total:"+Integer.toString(totalFast));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if(rs != null)
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

			if(pstmt != null)
			{
				try
				{
					pstmt.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}

			if(con != null)
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

	public static boolean atFast(String au,
								 String patnum,
								 String prt)
		throws Exception
	{
		FastClient client = new FastClient();
		client.setBaseURL("http://rei.bos3.fastsearch.net:15100");
		System.out.println("PCI:"+au+patnum+" AND \""+prt+"\"");
		client.setQueryString("PCI:"+au+patnum+" AND \""+prt+"\"");
		client.setOffSet(0);
		client.setPageSize(1);
		client.setDoClustering(false);
		client.setResultView("ei");
		client.setPrimarySort("ausort");
        client.setPrimarySortDirection("+");
		client.setDoNavigators(false);
		client.search();
		if(client.getHitCount() > 0)
		{
			return true;
		}
		else
		{
			return false;
		}
	}

}