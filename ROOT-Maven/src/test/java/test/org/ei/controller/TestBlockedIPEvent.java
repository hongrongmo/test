/**
 *
 */
package test.org.ei.controller;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.ei.config.RuntimeProperties;
import org.ei.session.BlockedIPEvent;
import org.ei.session.BlockedIPEvent.TimePeriod;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for the BlockedIPEvent class.
 *
 * PLEASE READ!!!!!!!!!!!!!!!
 * This test depends on the System property 'com.elsevier.env' being set.  You can
 * enable this by setting it as a parameter to your JVM.  In Eclipse, go to
 * Window -> Preferences -> Java -> Installed JREs.  Select the JRE you use for
 * this environment and add the following the the "Default JVM arguments":
 *
 * -Dcom.elsevier.env=local
 *
 * @author harovetm
 *
 */
public class TestBlockedIPEvent extends TestCase {


    @Test
    public void testEventCRUD() throws IOException {
        System.out.println("Testing CRUD events for environment: " + RuntimeProperties.getInstance().getRunlevel());
        BlockedIPEvent ipevent = new BlockedIPEvent();
        ipevent.setIP("testIP1");
        ipevent.setEnvironment("local");
        ipevent.setMessage("Message for testIP1");
        ipevent.setTimestamp(new Date());

        ipevent.save();

        // Ensure retrieved value matches saved value
        List<BlockedIPEvent> getipevent = BlockedIPEvent.getByIP("testIP1");
        Assert.assertNotNull(getipevent);
        Assert.assertTrue(getipevent.size() >= 1);

        // Delete all events
        for (BlockedIPEvent deleteme : getipevent) {
            deleteme.delete();
        }
        getipevent = BlockedIPEvent.getByIP("testIP1");
        Assert.assertTrue(getipevent.size() == 0);

    }

    static SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    @Test
    public void testEventList() throws IOException {
        Calendar timestamp = Calendar.getInstance();
        timestamp.add(Calendar.YEAR, -1);
        timestamp.add(Calendar.DAY_OF_YEAR, -1);

        System.out.println("Testing list of events for environment: " + RuntimeProperties.getInstance().getRunlevel());
        BlockedIPEvent ipevent = new BlockedIPEvent("testIP1");

        ipevent.setTimestamp(timestamp.getTime());
        ipevent.save();
        List<BlockedIPEvent> events = BlockedIPEvent.getByTimePeriod("testIP1", TimePeriod.LASTYEAR);
        if (events != null && events.size() > 0) fail("Events should not be found in last year!");

        timestamp.add(Calendar.DAY_OF_YEAR, 1);
        ipevent.setTimestamp(timestamp.getTime());
        ipevent.save();
        events = BlockedIPEvent.getByTimePeriod("testIP1", TimePeriod.LASTYEAR);
        if (events == null || events.size() == 0) fail("New Event should be found from last year!");

        timestamp.add(Calendar.MONTH, 11);
        timestamp.add(Calendar.DAY_OF_YEAR, -1);
        ipevent.setTimestamp(timestamp.getTime());
        ipevent.save();
        events = BlockedIPEvent.getByTimePeriod("testIP1", TimePeriod.LASTMONTH);
        if (events != null && events.size() > 0) fail("Events should not be found for last month!");

        timestamp.add(Calendar.DAY_OF_YEAR, 1);
        ipevent.setTimestamp(timestamp.getTime());
        ipevent.save();
        events = BlockedIPEvent.getByTimePeriod("testIP1", TimePeriod.LASTMONTH);
        if (events == null || events.size() == 0) fail("New Event should be found from last month!");

        // Clear all events
        BlockedIPEvent.deleteByIP("testIP1");

        // Now ensure 2 or more events are returned sorted by date
        // Add current date
        timestamp = Calendar.getInstance();
        ipevent = new BlockedIPEvent("testIP1");
        ipevent.save();
        // Add 1 month ago
        timestamp.add(Calendar.MONTH, -1);
        ipevent = new BlockedIPEvent("testIP1");
        ipevent.setTimestamp(timestamp.getTime());
        ipevent.save();
        // Add 1 month ago
        timestamp.add(Calendar.MONTH, -1);
        ipevent = new BlockedIPEvent("testIP1");
        ipevent.setTimestamp(timestamp.getTime());
        ipevent.save();
        // Add 1 month ago
        timestamp.add(Calendar.MONTH, -1);
        ipevent = new BlockedIPEvent("testIP1");
        ipevent.setTimestamp(timestamp.getTime());
        ipevent.save();
        // Add 1 month ago
        timestamp.add(Calendar.MONTH, -1);
        ipevent = new BlockedIPEvent("testIP1");
        ipevent.setTimestamp(timestamp.getTime());
        ipevent.save();
        // Add 1 month ago
        timestamp.add(Calendar.MONTH, -1);
        ipevent = new BlockedIPEvent("testIP1");
        ipevent.setTimestamp(timestamp.getTime());
        ipevent.save();
        // Add 1 month ago
        timestamp.add(Calendar.MONTH, -1);
        ipevent = new BlockedIPEvent("testIP1");
        ipevent.setTimestamp(timestamp.getTime());
        ipevent.save();
        // Add 1 month ago
        timestamp.add(Calendar.MONTH, -1);
        ipevent = new BlockedIPEvent("testIP1");
        ipevent.setTimestamp(timestamp.getTime());
        ipevent.save();
        // Add 1 month ago
        timestamp.add(Calendar.MONTH, -1);
        ipevent = new BlockedIPEvent("testIP1");
        ipevent.setTimestamp(timestamp.getTime());
        ipevent.save();
        // Add 1 month ago
        timestamp.add(Calendar.MONTH, -1);
        ipevent = new BlockedIPEvent("testIP1");
        ipevent.setTimestamp(timestamp.getTime());
        ipevent.save();
        // Add 1 month ago
        timestamp.add(Calendar.MONTH, -1);
        ipevent = new BlockedIPEvent("testIP1");
        ipevent.setTimestamp(timestamp.getTime());
        ipevent.save();
        // Add 1 month ago
        timestamp.add(Calendar.MONTH, -1);
        ipevent = new BlockedIPEvent("testIP1");
        ipevent.setTimestamp(timestamp.getTime());
        ipevent.save();

        events = BlockedIPEvent.getByTimePeriod("testIP1", TimePeriod.LASTYEAR);
        Assert.assertTrue(events.size() == 12);
        Date lastseen = null;
        for (BlockedIPEvent e1 : events) {
            if (lastseen == null) {
                lastseen = e1.getTimestamp();
                continue;
            } else {
                Assert.assertTrue(e1.getTimestamp().compareTo(lastseen) < 0);
                lastseen = e1.getTimestamp();
            }
        }

        // Clear all events
        BlockedIPEvent.deleteByIP("testIP1");
    }

    @After
    public void tearDown() {
        BlockedIPEvent.deleteByIP("testIP1");
    }
}
