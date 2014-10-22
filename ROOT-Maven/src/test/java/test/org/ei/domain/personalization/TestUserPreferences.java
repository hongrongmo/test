/**
 *
 */
package test.org.ei.domain.personalization;

import junit.framework.TestCase;

import org.ei.biz.personalization.UserPrefs;
import org.ei.config.ApplicationProperties;
import org.ei.config.EVProperties;
import org.ei.stripes.action.delivery.AbstractDeliveryAction;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author harovetm
 *
 */
public class TestUserPreferences extends TestCase {

    private static boolean setup = false;

    public static void main(String[] args) {
        UserPrefs.deleteByEnvironment("dev");
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Before
    protected void setUp() throws Exception {
        if (setup) return;
        EVProperties.getInstance();
        EVProperties.setStartup(System.currentTimeMillis());

        ApplicationProperties rtp = EVProperties.getApplicationProperties();
        EVProperties.setApplicationProperties(rtp);
        setup = true;
    }

    /**
     * Test method for {@link net.spy.memcached.MemcachedClient#add(java.lang.String, int, java.lang.Object)}.
     * @throws InterruptedException
     */
    @Test
    public void testCRUD() throws InterruptedException {
        String userid = "userid123";
        UserPrefs userprefs = new UserPrefs(userid);
        userprefs.save();

        // Ensure defaults are set
        userprefs = UserPrefs.load(userid);
        Assert.assertNotNull(userprefs);
        Assert.assertEquals(AbstractDeliveryAction.DOWNLOAD_FORMAT_PDF, userprefs.getDlFormat());
        Assert.assertEquals("default", userprefs.getDlOutput());
        Assert.assertEquals(25, userprefs.getResultsPerPage());
        Assert.assertFalse(userprefs.getShowPreview());

        userprefs.delete();
    }

}
