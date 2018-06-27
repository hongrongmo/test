package org.ei.util.test;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ei.connectionpool.ConnectionBroker;
import org.ei.connectionpool.ConnectionPoolException;
import org.ei.connectionpool.NoConnectionAvailableException;
import org.ei.domain.DatabaseConfig;

public abstract class BaseStoredProcedure {

    protected static Log log = LogFactory.getLog(BaseStoredProcedure.class);

    public abstract CallableStatement getStatement(Connection con);
    
    public void run()
    {
        Connection con = null;
        CallableStatement pstmt = null;
        int idx = 1;
        
        try
        {
            con = getConnection();
            pstmt = getStatement(con);
            pstmt.executeUpdate();
        }
        catch(ConnectionPoolException cpe)
        {
            log.error("run - ConnectionPoolException ", cpe);
        }
        catch(NoConnectionAvailableException ncae)
        {
            log.error("run - NoConnectionAvailableException ", ncae);
        }
        catch(SQLException sqle)
        {
            log.error("run - SQLException ", sqle);
        }
        finally
        {
            try
            {
                if(pstmt != null)
                {
                    pstmt.close();
                    pstmt = null;
                }
                if(con != null)
                {
                    replaceConnection(con);
                    con = null;
                }
            }
            catch(ConnectionPoolException cpe)
            {
                log.error("run - ConnectionPoolException ", cpe);
            }
            catch(SQLException sqle)
            {
                log.error("run - SQLException ", sqle);
            }

        }
    }

    public static void replaceConnection(Connection con)
        throws ConnectionPoolException
    {
        ConnectionBroker broker = ConnectionBroker.getInstance();
        broker.replaceConnection(con, DatabaseConfig.SESSION_POOL);
    }
    public static Connection getConnection() 
        throws ConnectionPoolException, NoConnectionAvailableException
    {
        ConnectionBroker broker = ConnectionBroker.getInstance();
		return broker.getConnection(DatabaseConfig.SESSION_POOL);
    }
    
}

