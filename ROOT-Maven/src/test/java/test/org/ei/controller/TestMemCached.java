/**
 *
 */
package test.org.ei.controller;

import junit.framework.TestCase;
import net.spy.memcached.CASResponse;
import net.spy.memcached.CASValue;

import org.ei.controller.MemcachedUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * @author harovetm
 *
 */
public class TestMemCached extends TestCase {

    private static boolean setup = false;

    public static void main(String[] args) {
        String memcacheservers = "localhost:11214,localhost:11215";
        MemcachedUtil.initialize(memcacheservers);
        MemcachedUtil memcached = MemcachedUtil.getInstance();

        long count = 0;
        Object obj = memcached.get("200.130.19.174_154500_SESSION");
        if (obj != null && obj.toString() != null)
            count = Long.parseLong(obj.toString().trim().replaceAll(" ", ""));

        System.out.println("Count = " + count);
    }

    /* (non-Javadoc)
     * @see junit.framework.TestCase#setUp()
     */
    @Before
    protected void setUp() throws Exception {
        if (setup) return;
        String memcacheservers = "localhost:11212,localhost:11213";
        MemcachedUtil.initialize(memcacheservers);
        setup = true;
    }

    /**
     * Test method for {@link net.spy.memcached.MemcachedClient#add(java.lang.String, int, java.lang.Object)}.
     * @throws InterruptedException
     */
    @Test
    public void testAdd() throws InterruptedException {
        MemcachedUtil memcached = MemcachedUtil.getInstance();
        String key = "test key";
        memcached.delete(key);

        // Add a test value that expires in 5 seconds
        memcached.add(key, 5, "This is a test value");
        Assert.assertEquals("This is a test value", memcached.get(key));
        Thread.sleep(5200);
        Assert.assertNull(memcached.get(key));

        // Add second value (should NOT override!)
        memcached.add(key, 5, "This is a test value");
        memcached.add(key, 5, "This is a test value 2");
        Assert.assertEquals("This is a test value", memcached.get(key));

        // Cleanup
        memcached.delete(key);
        Assert.assertNull(memcached.get(key));
    }

    @Test
    public void testGet() throws InterruptedException {
        MemcachedUtil memcached = MemcachedUtil.getInstance();
        String key = "test key";
        memcached.delete(key);

        // Add a test value that expires in 30 seconds
        memcached.add(key, 30, "This is a test value");
        Assert.assertEquals("This is a test value", memcached.get(key));
        Thread.sleep(5200);
        Assert.assertEquals("This is a test value", memcached.get(key));
        Thread.sleep(5200);
        Assert.assertEquals("This is a test value", memcached.get(key));

        // Cleanup
        memcached.delete(key);
        Assert.assertNull(memcached.get(key));

    }

    /**
     * Test method for {@link net.spy.memcached.MemcachedClient#replace(java.lang.String, int, java.lang.Object)}.
     */
    @Test
    public void testReplace() {
                MemcachedUtil memcached = MemcachedUtil.getInstance();
        String key = "testkey";
        memcached.delete(key);

        // Add a test value that expires in 15 seconds
        memcached.add(key, 15, "Value1");
        Assert.assertEquals("Value1", memcached.get(key));
        memcached.replace(key, 15, "Value2");
        Assert.assertEquals("Value2", memcached.get(key));

        memcached.delete(key);
        Assert.assertNull(memcached.get(key));
    }

    /**
     * Test method for {@link net.spy.memcached.MemcachedClient#incr(java.lang.String, long)}.
     * @throws InterruptedException
     */
    @Test
    public void testIncr() throws InterruptedException {
                MemcachedUtil memcached = MemcachedUtil.getInstance();

        String key = "incrkey";
        long incrval = -1;
        memcached.delete(key);

        // Test no default value
        incrval = memcached.incr(key);
        Assert.assertEquals(1, incrval);
        incrval = memcached.incr(key);
        Assert.assertEquals(2, incrval);
        memcached.delete(key);
        Assert.assertNull(memcached.get(key));
        incrval = memcached.incr(key, 20L);
        Assert.assertEquals(20L, incrval);
        incrval = memcached.incr(key);
        Assert.assertEquals(21L, incrval);
        incrval = memcached.incr(key, 9);
        Assert.assertEquals(30L, incrval);
        incrval = memcached.incr(key, 0);
        Assert.assertEquals(30L, incrval);
        memcached.delete(key);
        Assert.assertNull(memcached.get(key));

        // Test default value
        incrval = memcached.incr(key, 1, 39L);
        Assert.assertEquals(39L, incrval);
        incrval = memcached.incr(key, 1, 39L);
        Assert.assertEquals(40L, incrval);
        memcached.delete(key);
        Assert.assertNull(memcached.get(key));

        // Test default value and expiration
        incrval = memcached.incr(key, 1, 29L, 5);
        Assert.assertEquals(29L, incrval);
        Thread.sleep(5200);
        incrval = memcached.incr(key, 1, 1L, 5);
        Assert.assertEquals(1L, incrval);
        incrval = memcached.incr(key, 1, 1L, 5);
        Assert.assertEquals(2L, incrval);
        Thread.sleep(5200);
        Assert.assertNull(memcached.get(key));
    }

    /**
     * Test method for {@link net.spy.memcached.MemcachedClient#decr(java.lang.String, int, long, int)}.
     * @throws InterruptedException
     */
    @Test
    public void testDecr() throws InterruptedException {
                MemcachedUtil memcached = MemcachedUtil.getInstance();

        String key = "decrkey";
        long decrval = -1;
        memcached.delete(key);

        // Test no default value
        decrval = memcached.decr(key, 1);
        Assert.assertEquals(0, decrval);
        memcached.delete(key);
        Assert.assertNull(memcached.get(key));

        // Test default value
        decrval = memcached.decr(key, 1, 41L);
        Assert.assertEquals(41L, decrval);
        decrval = memcached.decr(key, 1);
        Assert.assertEquals(40L, decrval);
        memcached.delete(key);
        Assert.assertNull(memcached.get(key));

        // Test default value and expiration
        decrval = memcached.decr(key, 1, 31L, 5);
        Assert.assertEquals(31L, decrval);
        decrval = memcached.decr(key, 1, 5);
        Assert.assertEquals(30L, decrval);
        Thread.sleep(5200);
        Assert.assertNull(memcached.get(key));

        memcached.delete(key);
        Assert.assertNull(memcached.get(key));
    }

    /**
     * Test method for {@link net.spy.memcached.MemcachedClient#decr(java.lang.String, int, long, int)}.
     * @throws InterruptedException
     */
    @Test
    public void testCas() throws InterruptedException {
                MemcachedUtil memcached = MemcachedUtil.getInstance();

        String key = "caskey";
        Integer value = 1;
        memcached.add(key, 600, value);

        // Ensure newly inserted object gives OK on cas()
        CASValue<Object> casvalue = memcached.gets(key);
        CASResponse response = memcached.cas(key, casvalue.getCas(), value + 1);
        Assert.assertEquals(CASResponse.OK.name(), response.name());
        Assert.assertEquals(2, memcached.get(key));

        // Now
        casvalue = memcached.gets(key);
        memcached.replace(key, 600, value+1);
        response = memcached.cas(key, casvalue.getCas(), value + 1);
        Assert.assertEquals(CASResponse.EXISTS.name(), response.name());

        memcached.delete(key);
    }
}
