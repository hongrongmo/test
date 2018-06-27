package org.ei.domain.lookup;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.CallableStatement;
import org.ei.connectionpool.ConnectionBroker;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.LookupIndexException;

public class LookupSkipper
{
    private String sessionID;

    public LookupSkipper(String sessionID)
    {
        this.sessionID = sessionID;
    }

    public int getSkipCount(String lookupSearchID,
                            int pageNumber)
        throws LookupIndexException
    {
        int skip = -1;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        ConnectionBroker broker = null;

        try
        {
            broker = ConnectionBroker.getInstance();
            con=broker.getConnection(DatabaseConfig.SESSION_POOL);
            pstmt = con.prepareStatement("select skip_count from lookup_skipper where search_id = ? and pnum = ?");
            pstmt.setString(1, lookupSearchID);
            pstmt.setInt(2, pageNumber);
            rs = pstmt.executeQuery();
            while(rs.next())
            {
                skip = rs.getInt("skip_count");
            }
        }
        catch(Exception e)
        {
            throw new LookupIndexException(e);
        }
        finally
        {
            if(rs != null)
            {
                try
                {
                    rs.close();
                }
                catch(Exception e1)
                {
                    e1.printStackTrace();
                }
            }

            if(pstmt != null)
            {
                try
                {
                    pstmt.close();
                }
                catch(Exception e1)
                {
                    e1.printStackTrace();
                }
            }


            if(con != null)
            {
                try
                {
                    broker.replaceConnection(con,
                                           DatabaseConfig.SESSION_POOL);
                }
                catch(Exception e1)
                {
                    e1.printStackTrace();
                }
            }
        }

        return skip;
    }


    public void putSkipCount(String lookupSearchID,
                                    int pageNumber,
                                    int skipCount)
                throws LookupIndexException
    {
        Connection con = null;
        ConnectionBroker broker = null;
        CallableStatement proc = null;

        try
        {
            broker = ConnectionBroker.getInstance();
            con=broker.getConnection(DatabaseConfig.SESSION_POOL);
            proc = con.prepareCall("{ call LookupSkipper_lookup_skipper(?,?,?,?)}");
            proc.setString(1,lookupSearchID);
            proc.setInt(2,pageNumber);
            proc.setString(3,this.sessionID);
            proc.setInt(4,skipCount);
            proc.execute();

        }
        catch(Exception e)
        {
            //throw new LookupIndexException(e);
        }
        finally
        {


            if(proc != null)
            {
                try
                {
                    proc.close();
                }
                catch(Exception e1)
                {
                    e1.printStackTrace();
                }
            }


            if(con != null)
            {
                try
                {
                    broker.replaceConnection(con,
                                           DatabaseConfig.SESSION_POOL);
                }
                catch(Exception e1)
                {
                    e1.printStackTrace();
                }

            }

        }
    }
}