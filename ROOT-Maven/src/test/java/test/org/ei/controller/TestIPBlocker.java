/**
 *
 */
package test.org.ei.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.ei.biz.personalization.cars.Account;
import org.ei.config.ApplicationProperties;
import org.ei.config.EVProperties;
import org.ei.controller.IPBlocker;
import org.ei.controller.IPBlocker.COUNTER;
import org.ei.controller.MemcachedUtil;
import org.ei.session.BlockedIPEvent;
import org.ei.session.BlockedIPStatus;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for the IPBlocker* classes.
 *
 * PLEASE READ!!!!!!!!!!!!!!! This test depends on the System property 'com.elsevier.env' being set. You can enable this by setting it as a parameter to your
 * JVM. In Eclipse, go to Window -> Preferences -> Java -> Installed JREs. Select the JRE you use for this environment and add the following the the
 * "Default JVM arguments":
 *
 * -Dcom.elsevier.env=local
 *
 * @author harovetm
 *
 */
public class TestIPBlocker {

    private static boolean setup = false;
    @Before
    public void setUp() throws Exception {
        if (setup) return;
        init();
        setup = true;
    }

    private static void init() throws IOException {
        String memcacheservers = "localhost:11212,localhost:11213";
        System.setProperty(ApplicationProperties.SYSTEM_ENVIRONMENT_RUNLEVEL, "local");

        EVProperties.getInstance();
        EVProperties.setStartup(System.currentTimeMillis());

        ApplicationProperties rtp = EVProperties.getApplicationProperties();
        rtp.setProperty("CONSUMER_APP", "ENGVIL");
        rtp.setProperty("CONSUMER_CLIENT", "ENGVIL");
        rtp.setProperty("WEBSERVICE_LOG_LEVEL", "Default");
        rtp.setProperty("WEBSERVICE_VERSION", "12");
        rtp.setProperty("X_ELS_AUTHENTICATION", "12");
        rtp.setProperty("X_ELS_AUTHENTICATION_VALUE", "ENGVIL");
        rtp.setProperty("SITE_IDENTIFIER", "engvil");
        rtp.setProperty("PLATFORM_CODE", "EV");
        rtp.setProperty(EVProperties.MEMCACHE_SERVERS, memcacheservers);
        EVProperties.setApplicationProperties(rtp);
        Logger.getRootLogger().setLevel(Level.INFO);
        MemcachedUtil.initialize(memcacheservers);
    }

    /**
     * 1-time run to sync EI* with DynamoDB
     *
     * @param args
     * @throws IOException
     */
    public static void main(String[] args) throws IOException {
        //reload("jdbc:oracle:thin:@localhost:15211:eib", "AP_EV_SESSION", "ei3it", "local");
        reload("jdbc:oracle:thin:@localhost:15211:eib", "AP_EV_SESSION", "ei3it", "cert");
        //reload("jdbc:oracle:thin:@localhost:15210:eia", "AP_EV_SESSION", "ei3it", "prod");
    }

    /**
     * This is a TEMPORARY method to load IP Blocker info from current Oracle RDS into DynamoDB. Once new code is released this can be removed!
     * @throws IOException
     */
    private static void reload(String connstring, String username, String password, String environment) throws IOException {
        init();
        System.out.println("-------- Connecting to " + connstring + " (" + username + "/" + password + ") ------");

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your Oracle JDBC Driver?");
            e.printStackTrace();
            return;
        }

        System.out.println("Oracle JDBC Driver Registered!");
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(connstring, username, password);
        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            return;
        }

        if (connection == null) {
            System.out.println("Failed to make connection!");
        }

        // Delete all entries before starting
        System.out.println("-------- Deleting entries for environment '" + environment + "'");
        for (BlockedIPStatus ipstatus : BlockedIPStatus.getByStatus(BlockedIPStatus.STATUS_ANY, environment)) {
            ipstatus.delete(false);
        }
        BlockedIPEvent.deleteByEnvironment(environment);

        // Reload from database
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        try {
            pstmt = connection.prepareStatement("SELECT * FROM BLOCKED_IPS ORDER BY TS DESC");
            rs = pstmt.executeQuery();
            while (rs.next()) {
                String ip = rs.getString("IP");
                BlockedIPStatus ipstatus = BlockedIPStatus.load(ip, environment);
                if (ipstatus == null) {
                    // New IP seen, save last item!
                    ipstatus = new BlockedIPStatus(ip);
                    ipstatus.setStatus(rs.getLong("ENABLED") == 1 ? BlockedIPStatus.STATUS_BLOCKED : BlockedIPStatus.STATUS_UNBLOCKED);
                    ipstatus.setTimestamp(rs.getTimestamp("TS"));
                    System.out.println("Saving BlockedIPStatus for IP '" + rs.getString("IP") + "' (" + environment + ")");
                    ipstatus.setEnvironment(environment);

                    ipstatus.addAccount(Account.getAccountInfo(ip));

                    ipstatus.save();
                }

                BlockedIPEvent ipevent = new BlockedIPEvent(ip);
                ipevent.setMessage(rs.getString("DESCRIPTION"));
                ipevent.setTimestamp(rs.getTimestamp("TS"));
                System.out.println("Saving BlockedIPEvent for IP '" + rs.getString("IP") + "' (" + environment + ")");
                ipevent.setEnvironment(environment);
                ipevent.save();
            }
        } catch (Throwable t) {
            System.out.println("Exception thrown while updating DynamoDB!");
            t.printStackTrace();
            try {
                rs.close();
                pstmt.close();
                connection.close();
            } catch (Throwable t2) {
            }
        }
    }

    @Test
    public void testRequestLimit() throws IOException {
        setApplicationProperties();

        // Add temporary IPs for test
        String iptoblock = "blockedip";
        IPBlocker ipblocker = IPBlocker.getInstance();
        Assert.assertFalse(ipblocker.isBlocked(iptoblock));

        // Increment 6 times to get limit
        COUNTER counter = COUNTER.REQUEST;
        ipblocker.clearBucket(iptoblock, counter);
        long count = 0;
        count = ipblocker.increment(iptoblock, counter);
        Assert.assertFalse(ipblocker.isThresholdReached(iptoblock, counter));
        Assert.assertEquals(1, count);
        count = ipblocker.increment(iptoblock, counter);
        Assert.assertFalse(ipblocker.isThresholdReached(iptoblock, counter));
        Assert.assertEquals(2, count);
        count = ipblocker.increment(iptoblock, counter);
        Assert.assertFalse(ipblocker.isThresholdReached(iptoblock, counter));
        Assert.assertEquals(3, count);
        count = ipblocker.increment(iptoblock, counter);
        Assert.assertFalse(ipblocker.isThresholdReached(iptoblock, counter));
        Assert.assertEquals(4, count);
        count = ipblocker.increment(iptoblock, counter);
        Assert.assertFalse(ipblocker.isThresholdReached(iptoblock, counter));
        Assert.assertEquals(5, count);
        // Magic happens!
        count = ipblocker.increment(iptoblock, counter);
        Assert.assertTrue(ipblocker.isThresholdReached(iptoblock, counter));
        Assert.assertEquals(6, count);

        ipblocker.clearBucket(iptoblock, counter);
    }

    @Test
    public void testIsBlocked() throws IOException {
        setApplicationProperties();

        // Add temporary IPs for test
        String iptoblock = "blockedip";
        COUNTER counter = COUNTER.REQUEST;
        IPBlocker ipblocker = IPBlocker.getInstance();
        ipblocker.clearBucket(iptoblock, counter);

        Assert.assertFalse(ipblocker.isBlocked(iptoblock));

        // Increment 6 times to get limit
        long count = 0;
        count = ipblocker.increment(iptoblock, counter);
        count = ipblocker.increment(iptoblock, counter);
        count = ipblocker.increment(iptoblock, counter);
        count = ipblocker.increment(iptoblock, counter);
        count = ipblocker.increment(iptoblock, counter);
        // Magic happens!
        count = ipblocker.increment(iptoblock, counter);
        Assert.assertTrue(ipblocker.isThresholdReached(iptoblock, counter));
        Assert.assertEquals(6, count);
        // Should NOT automatically block - just sends message and adds to table
        Assert.assertFalse(ipblocker.isBlocked(iptoblock));
        // Ensure event was written
        List<BlockedIPEvent> events = BlockedIPEvent.getByIP("blockedip");
        Assert.assertFalse((events == null || events.size() ==0));

        ipblocker.clearBucket(iptoblock, counter);
    }

    @After
    public void cleanUp() throws IOException {
        String iptoblock = "blockedip";
        IPBlocker.getInstance().clearBucket(iptoblock, COUNTER.REQUEST);
        BlockedIPStatus ipstatus = BlockedIPStatus.load(iptoblock);
        if (ipstatus != null) ipstatus.delete(true);
    }

    private void setApplicationProperties() throws IOException {
        EVProperties.getApplicationProperties().setProperty(IPBlocker.IPBLOCKER_BUCKET_INTERVAL_MINUTES_PROPERTY, "5");
        EVProperties.getApplicationProperties().setProperty(IPBlocker.IPBLOCKER_SESSION_LIMIT_PROPERTY, "5");
        EVProperties.getApplicationProperties().setProperty(IPBlocker.IPBLOCKER_REQUEST_LIMIT_PROPERTY, "5");
        EVProperties.getApplicationProperties().setProperty(IPBlocker.IPBLOCKER_EMAIL_TO_PROPERTY, "harover@elsevier.com");
        EVProperties.getApplicationProperties().setProperty(IPBlocker.IPBLOCKER_EMAIL_FROM_PROPERTY, "harover@elsevier.com");
    }
}
