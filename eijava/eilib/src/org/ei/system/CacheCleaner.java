package org.ei.system;

/** project specific imports*/
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Hashtable;
import java.util.Map;

import org.ei.connectionpool.ConnectionBroker;
import org.ei.connectionpool.ConnectionPoolException;
import org.ei.domain.DatabaseConfig;
import org.ei.domain.Query;
import org.ei.util.StringUtil;

/**
 *
 * Handles EmailAlert related tasks such as
 * <p> a)This object gets distinct userIds and his email address where emailalerts is not null.</p>
 * <p> b)For each userId gets query object where emailAlert is not null. </p>
 * <p> c)For query object it call corresponding SearchControl depending on database of query.
 *      From that compenent gets first 25 documents DocIDs list.</p>
 * <p> d)This DocIds list is send to the EmailAlertResultsManager and then call Email.java to send mail.</p>
 *
 */

public class CacheCleaner
{

//    private RuntimeProperties eiProps;

    private ConnectionBroker m_broker = null;
    private String m_strPoolname = StringUtil.EMPTY_STRING;
    private String m_strPoolsFilename = "./etc/cachecleaner_pool.xml";
//    private String m_strPropertiesFile = "./etc/cachecleaner.properties";

    private static Map mapTimePeriodsInMilliSecs = new Hashtable();

    private static final String TWENTY_MINUTES = "TWENTY_MINUTES";
    private static final String ONE_HOUR = "ONE_HOUR";
    private static final String ONE_DAY = "ONE_DAY";
    private static final String ONE_WEEK = "ONE_WEEK";
    private static final String TWO_WEEKS = "TWO_WEEKS";

    public void cleanup()
    {
        if (m_broker != null)
        {
            try
            {
                m_broker.closeConnections();
            }
            catch (ConnectionPoolException cpe)
            {
            }
        }
    }
    /**
    * This constructor reads runtimeProperties and gets size of documents to saend as mails.
    * and sends mails.
    */
    private CacheCleaner()
    {

        try
        {

            m_strPoolname = DatabaseConfig.SESSION_POOL;

//            eiProps = new RuntimeProperties(m_strPropertiesFile);
//            m_strPoolsFilename = eiProps.getProperty("POOLSFILENAME");
            m_broker = ConnectionBroker.getInstance(m_strPoolsFilename);

            mapTimePeriodsInMilliSecs.put(CacheCleaner.TWENTY_MINUTES, new Long(1200000));
            mapTimePeriodsInMilliSecs.put(CacheCleaner.ONE_HOUR, new Long(3600000));
            mapTimePeriodsInMilliSecs.put(CacheCleaner.ONE_DAY, new Long(86400000));
            mapTimePeriodsInMilliSecs.put(CacheCleaner.ONE_WEEK, new Long(604800000));
            mapTimePeriodsInMilliSecs.put(CacheCleaner.TWO_WEEKS, new Long(1209600000));

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    private void cleanSessionTables() throws Exception
    {
        cleanSessionTables(((Long) mapTimePeriodsInMilliSecs.get(CacheCleaner.ONE_DAY)).longValue());
    }

    private void cleanSessionTables(long lngTimePeriod) throws Exception
    {
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rset = null;

        String strTableArray[] = { "BASKET", "BASKET_STATE", "PAGE_CACHE", "THESAURUS_PATH", "SESSION_PROPS","LOOKUP_SKIPPER","NAVIGATOR_CACHE", "SEARCHES", "USER_SESSION"};

        Query queryObject = null;

        try
        {
            long lngNow = System.currentTimeMillis();

            con = m_broker.getConnection(m_strPoolname);

            pstmt = con.prepareStatement("SELECT SESSION_ID, LAST_TOUCHED FROM USER_SESSION WHERE ((? - LAST_TOUCHED ) > ?)");
            pstmt.setLong(1, lngNow);
            pstmt.setLong(2, lngTimePeriod);
            rset = pstmt.executeQuery();

            System.out.println(" Now = " + (new Timestamp(lngNow)).toString());
            System.out.println(" Last Touched Since = " + (new Timestamp(lngNow - lngTimePeriod)).toString());

            while (rset.next())
            {
                String strSessionId = rset.getString(1);
                System.out.println(" Session ID " + strSessionId + " Last Touched Since = " + (new Timestamp(rset.getLong(2))).toString());
                for (int intTableIndex = 0; intTableIndex < strTableArray.length; intTableIndex++)
                {
                    System.out.println("DELETE FROM " + strTableArray[intTableIndex] + " WHERE SESSION_ID=? " + strSessionId);
                    PreparedStatement pstmt1 = null;
                    try
                    {
                        pstmt1 = con.prepareStatement("DELETE FROM " + strTableArray[intTableIndex] + " WHERE SESSION_ID=?");
                        pstmt1.setString(1, strSessionId);
                        pstmt1.executeQuery();
                    }
                    finally
                    {
                        if (pstmt1 != null)
                        {
                            pstmt1.close();
                        }
                    }
                }
            }
        }
        catch (Exception se)
        {
            throw new Exception(se.getMessage());
        }
        finally
        {
            if (rset != null)
            {
                try
                {
                    rset.close();
                }
                catch (Exception e1)
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
            if (con != null)
            {
                try
                {
					m_broker.replaceConnection(con, m_strPoolname);
                }
                catch (Exception cpe)
                {
                }
            }
        }
    }

    public static void main(String[] args) throws Exception
    {

        //java -cp ;./lib/eilib.jar;./lib/xerces.jar;./lib/oracle-jdbc.jar; org.ei.system.CacheCleaner

        CacheCleaner cchInstance = new CacheCleaner();

        long lng_TimePeriod = ((Long) mapTimePeriodsInMilliSecs.get(CacheCleaner.ONE_DAY)).longValue();

        try
        {
            if ((args.length == 1) && (args[0] != null))
            {
                String strTimePeriodName = args[0];
                if (mapTimePeriodsInMilliSecs.containsKey(strTimePeriodName))
                {
                    lng_TimePeriod = ((Long) mapTimePeriodsInMilliSecs.get(strTimePeriodName)).longValue();
                }
                else
                {
                    System.out.println("Usage: CacheCleaner [TIME_PERIOD]");
                    System.out.println("Error in specified time period");
                    System.out.println("Legal Values are:");
                    System.out.println("\t");
                    System.out.println("\t" + CacheCleaner.TWENTY_MINUTES);
                    System.out.println("\t" + CacheCleaner.ONE_HOUR);
                    System.out.println("\t" + CacheCleaner.ONE_DAY);
                    System.out.println("\t" + CacheCleaner.ONE_WEEK);
                    System.out.println("\t" + CacheCleaner.TWO_WEEKS);
                    System.out.println("Or specify none for default of ONE_DAY.");

                    throw new Exception("Illegal time period for cache cleaner.");
                }

            }

            cchInstance.cleanSessionTables(lng_TimePeriod);
        }
        finally
        {
            if (cchInstance != null)
            {
                System.out.println("Cleaning up Connections");
                cchInstance.cleanup();
            }
        }

        System.exit(0);

    }

}
