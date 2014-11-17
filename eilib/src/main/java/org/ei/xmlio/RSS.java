package org.ei.xmlio;


import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Hashtable;

import org.ei.connectionpool.ConnectionBroker;
import org.ei.domain.DatabaseConfig;

public class RSS
{
    private static Hashtable<String, RSS> rssQueries;
    public String QUERY;
    public String DATABASE;
    public String CATEGORY;
	public String UPDATENUMBER="1";
	public String SORTBY="wk";
	public String SORTDIR="dw";


    static
    {
        rssQueries = new Hashtable<String, RSS>();

        RSS honeywell1 = new RSS();
        honeywell1.QUERY = "<query><andQuery><word>Integrated</word><word>Security</word><word>Systems</word></andQuery></query>";
        honeywell1.DATABASE = "1";
        honeywell1.CATEGORY = "Integrated Security";
        rssQueries.put("honeywell1", honeywell1);

        RSS honeywell2 = new RSS();
        honeywell2.QUERY = "<query><andQuery><orQuery><word>airport</word><word>airfield</word></orQuery><orQuery><word>lighting</word><word>gate docking</word><word>landing</word></orQuery></andQuery></query>";
        honeywell2.DATABASE = "1";
        honeywell2.CATEGORY = "Gate Docking";
        rssQueries.put("honeywell2", honeywell2);


        RSS honeywell3 = new RSS();
        honeywell3.QUERY = "<query><word>turbochargers</word></query>";
        honeywell3.DATABASE = "1";
        honeywell3.CATEGORY = "Turbochargers";
        rssQueries.put("honeywell3", honeywell3);


        RSS honeywell4 = new RSS();
        honeywell4.QUERY = "<query><andQuery><word>high pressure</word><word>turbines</word></andQuery></query>";
        honeywell4.DATABASE = "1";
        honeywell4.CATEGORY = "Turbines";
        rssQueries.put("honeywell4", honeywell4);

        RSS honeywell5 = new RSS();
        honeywell5.QUERY = "<query><andQuery><word>removal</word><word>sulfur</word><word>vehicle</word></andQuery></query>";
        honeywell5.DATABASE = "1";
        honeywell5.CATEGORY = "Sulfur Removal";
        rssQueries.put("honeywell5", honeywell5);

    }


    public static RSS getRssQuery(String id)
    {
        return (RSS)rssQueries.get(id);
    }

	public static String[] getQuery(String id)
		throws XMLIOException
	{

		Connection con = null;
		ConnectionBroker broker = null;
		PreparedStatement pstmt = null;
		ResultSet rset = null;
		String[] rssinfo = null;
		CallableStatement proc=null;

		try
		{
			broker = ConnectionBroker.getInstance();
			con = broker.getConnection(DatabaseConfig.SESSION_POOL);

			try
			{
				pstmt = con.prepareStatement("SELECT QUERY, DATABASE FROM RSS_QUERY WHERE RSSID=?");
				pstmt.setString(1, id);
				rset = pstmt.executeQuery();

				while (rset.next())
            	{
					rssinfo = new String[2];
					rssinfo[1] = rset.getString("QUERY");
					rssinfo[0] = rset.getString("DATABASE");
				}
			}
			finally
			{
				if(rset != null)
				{
					try
					{
						rset.close();
					}
					catch(Exception e)
					{
					}
				}

				if (pstmt != null)
				{
					try
					{
						pstmt.close();
					}
					catch (Exception sqle)
					{
					}
				}
			}

			if(rssinfo != null)
			{
				try
				{
					 proc = con.prepareCall("{ call RSS_touch(?)}");
					 proc.setString(1,id);
					 proc.executeUpdate();
				}
				finally
				{
					if(proc != null)
					{
						try
						{
							proc.close();
						}
						catch (Exception sqle)
						{
						}
					}
				}
			}
		}
		catch (Exception sqle)
		{
			throw new XMLIOException(sqle);
		}
		finally
		{
			if (con != null)
			{
				try
				{
					broker.replaceConnection(con, DatabaseConfig.SESSION_POOL);
				}
				catch (Exception cpe)
				{
				}
			}
		}

        return rssinfo;
	}

	public static void setQuery(String rssID,
								String rssQuery,
								String database,
								String customerID)
		throws XMLIOException
	{
		Connection con = null;
		ConnectionBroker broker = null;
		CallableStatement proc=null;
		try
		{
			broker = ConnectionBroker.getInstance();
			con = broker.getConnection(DatabaseConfig.SESSION_POOL);
			proc = con.prepareCall("{ call RSS_setQuery(?,?,?,?)}");
			proc.setString(1,rssID);
			proc.setString(2,rssQuery);
			proc.setString(3,database);
			proc.setString(4,customerID);
			proc.executeUpdate();
		}
		catch (Exception sqle)
		{
			throw new XMLIOException(sqle);
		}
		finally
		{
			if(proc != null)
			{
				try
				{
					proc.close();
				}
				catch (Exception sqle)
				{
				}
			}

			if (con != null)
			{
				try
				{
					broker.replaceConnection(con, DatabaseConfig.SESSION_POOL);
				}
				catch (Exception cpe)
				{
				}
			}
		}
	}

}