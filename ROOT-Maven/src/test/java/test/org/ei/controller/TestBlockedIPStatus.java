/**
 *
 */
package test.org.ei.controller;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.apache.log4j.Logger;
import org.ei.config.ApplicationProperties;
import org.ei.config.EVProperties;
import org.ei.session.BlockedIPStatus;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test class for the BlockedIPStatus class.
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
public class TestBlockedIPStatus extends TestCase {

    private static final Logger log4j = Logger.getLogger(TestBlockedIPStatus.class);

    private static boolean setup = false;
    @Before
    protected void setUp() throws Exception {
        if (setup) return;
        init();
        setup = true;
    }

    private static void init() throws IOException {
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
        EVProperties.setApplicationProperties(rtp);
    }

    @Test
    public void testFilterStatus() throws IOException {
        System.out.println("Testing status filtering for environment: " + EVProperties.getApplicationProperties().getRunlevel());

        BlockedIPStatus ipstatus = new BlockedIPStatus();
        ipstatus.setIP("testIP1");
        ipstatus.setStatus(BlockedIPStatus.STATUS_UNBLOCKED);
        ipstatus.setTimestamp(new Date());
        ipstatus.save();

        ipstatus = new BlockedIPStatus();
        ipstatus.setIP("testIP2");
        ipstatus.setStatus(BlockedIPStatus.STATUS_BLOCKED);
        ipstatus.setTimestamp(new Date());
        ipstatus.save();

        List<BlockedIPStatus> ipstatuslist = BlockedIPStatus.getByStatus(BlockedIPStatus.STATUS_UNBLOCKED);
        Assert.assertTrue("UNBLOCKED size must be at least 1!", ipstatuslist.size() >= 1);
        for (BlockedIPStatus x : ipstatuslist) {
            String IP = x.getIP();
            if ("testIP1".equals(IP))
                Assert.assertEquals(BlockedIPStatus.STATUS_UNBLOCKED, x.getStatus());
            if ("testIP2".equals(IP))
                fail("'testIP2' should NOT be found with status 'UNBLOCKED'");
        }

        ipstatuslist = BlockedIPStatus.getByStatus(BlockedIPStatus.STATUS_BLOCKED);
        Assert.assertTrue("BLOCKED size must be at least 1!", ipstatuslist.size() >= 1);
        for (BlockedIPStatus x : ipstatuslist) {
            String IP = x.getIP();
            if ("testIP2".equals(IP))
                Assert.assertEquals(BlockedIPStatus.STATUS_BLOCKED, x.getStatus());
            if ("testIP1".equals(IP))
                fail("'testIP1' should NOT be found with status 'UNBLOCKED'");
        }

        ipstatuslist = BlockedIPStatus.getByStatus(BlockedIPStatus.STATUS_ANY);
        Assert.assertTrue("ANY size must be at least 1!", ipstatuslist.size() >= 1);

        ipstatuslist = BlockedIPStatus.getByStatus(null);
        Assert.assertTrue("Empty string size must be at least 1!", ipstatuslist.size() >= 1);

        try {
            BlockedIPStatus.load("testIP1").delete();
        } catch (Throwable t) {
        }

        try {
            BlockedIPStatus.load("testIP2").delete();
        } catch (Throwable t) {
        }
    }

    @Test
    public void testStatusCRUD() throws IOException {
        System.out.println("Testing status CRUD for environment: " + EVProperties.getApplicationProperties().getRunlevel());

        BlockedIPStatus ipstatus = new BlockedIPStatus();
        ipstatus.setIP("testIP1");
        ipstatus.setAccountID("1234");
        ipstatus.setAccountName("TestAccount");
        ipstatus.setAccountNumber("C0000001");
        ipstatus.setDepartmentName("TestDept");
        ipstatus.setDepartmentNumber("D0000001");
        ipstatus.setStatus(BlockedIPStatus.STATUS_UNBLOCKED);
        ipstatus.setTimestamp(new Date());

        ipstatus.save();

        // Ensure retrieved value matches saved value
        BlockedIPStatus getipstatus = BlockedIPStatus.load("testIP1");
        Assert.assertEquals(ipstatus.getStatus(), getipstatus.getStatus());
        Assert.assertEquals(ipstatus.getAccountID(), getipstatus.getAccountID());
        Assert.assertEquals(ipstatus.getAccountName(), getipstatus.getAccountName());
        Assert.assertEquals(ipstatus.getAccountNumber(), getipstatus.getAccountNumber());
        Assert.assertEquals(ipstatus.getDepartmentName(), getipstatus.getDepartmentName());
        Assert.assertEquals(ipstatus.getDepartmentNumber(), getipstatus.getDepartmentNumber());
        Assert.assertEquals(ipstatus.getTimestamp(), getipstatus.getTimestamp());

        // Delete status
        ipstatus.delete();
        getipstatus = BlockedIPStatus.load("testIP1");
        Assert.assertNull(getipstatus);
    }

    @Test
    public void testStatusList() throws IOException {
        System.out.println("Testing status list for environment: " + EVProperties.getApplicationProperties().getRunlevel());

        int total = 0;

        List<BlockedIPStatus> list = BlockedIPStatus.getByStatus(BlockedIPStatus.STATUS_BLOCKED);
        if (list != null) {
            for (BlockedIPStatus ipstatus : list) {
                total++;
                if (!(BlockedIPStatus.STATUS_BLOCKED.equals(ipstatus.getStatus())))
                    fail("Only BLOCKED entries should be returned!");
            }
        }

        list = BlockedIPStatus.getByStatus(BlockedIPStatus.STATUS_UNBLOCKED);
        if (list != null) {
            for (BlockedIPStatus ipstatus : list) {
                if (!(BlockedIPStatus.STATUS_UNBLOCKED.equals(ipstatus.getStatus())))
                    fail("Only UNBLOCKED entries should be returned!");
            }
        }

        int anytotal = 0;
        list = BlockedIPStatus.getByStatus(BlockedIPStatus.STATUS_ANY);
        if (list != null) {
            for (BlockedIPStatus ipstatus : list) {
                anytotal++;
            }
        }

        if (anytotal != total) {
            fail("The count of BLOCKED and UNBLOCKED should match the count for ANY");
        }
    }

}
