package org.ei.util;

/** project specific imports*/
import java.sql.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.ei.connectionpool.ConnectionBroker;
import org.ei.connectionpool.ConnectionPoolException;
import org.ei.connectionpool.NoConnectionAvailableException;

import org.ei.domain.DatabaseConfig;

import org.ei.util.StringUtil;
//import org.ei.util.test.*;

public class TestProcedure {

    protected static Log log = LogFactory.getLog(TestProcedure.class);

    private ConnectionBroker m_broker = null;

    public void cleanup()
    {
        if(m_broker != null)
        {
            try {
                log.info("Calling broker.closeConnections()");
                m_broker.closeConnections();
            }
            catch(ConnectionPoolException cpe)
            {
                log.error(" cleanup " , cpe);
            }
        }
    }
    /**
    * This constructor reads runtimeProperties and gets size of documents to saend as mails.
    * and sends mails.
    */
    public TestProcedure() {

        try {

            String m_strPoolname = StringUtil.EMPTY_STRING;
            String m_strPoolsFilename = StringUtil.EMPTY_STRING;
            m_strPoolname = DatabaseConfig.SESSION_POOL;
            m_strPoolsFilename = "test/logs/pools.xml";

            m_broker = ConnectionBroker.getInstance(m_strPoolsFilename);

        }
        catch(Exception e)
        {
            log.error(" TestProcedure Exception " , e);
        }
    }


    private boolean runTest()
        throws Exception
    {
       // new TestSession().run();
      //  new TestUpdateDb().run();
      //  new TestPA().run();
        return true;
    }


    public static void main(String[] args)
    {

        TestProcedure tstInstance = null;
        try
        {
            tstInstance = new TestProcedure();
            while(true)
            {
                tstInstance.runTest();
            }
        }
        catch(Exception e)
        {
            log.error("Exception ",e);
        }
        finally
        {
            log.info("Cleaning up Connections");
            if(tstInstance != null)
            {
                tstInstance.cleanup();
            }
        }
        System.exit(0);
    }
}
