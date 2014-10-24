package org.ei.controller;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import net.spy.memcached.AddrUtil;
import net.spy.memcached.BinaryConnectionFactory;
import net.spy.memcached.CASResponse;
import net.spy.memcached.CASValue;
import net.spy.memcached.MemcachedClient;
import net.spy.memcached.internal.OperationFuture;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;

public class MemcachedUtil {

    private static final Logger log4j = Logger.getLogger(MemcachedUtil.class); // log4j

    public static final int DEFAULT_EXPIRATION = 30;  // Default 30 second expiration
    public static final long ASYNC_TIMEOUT = 5L;      // Timeout for asynchronous get operation

    private static MemcachedUtil instance = null;
    private MemcachedClient client = null;

    // Private constructor
    private MemcachedUtil() {
    }

    /**
     * Instance retrieval
     *
     * @return
     */
    public static MemcachedUtil getInstance() {
        if (instance == null) {
            throw new RuntimeException("Instance has not been initialized!");
        }
        return instance;
    }

    /**
     * Initialize this instance. Requires memcache server names!
     *
     * @param servers
     *            Memcache server names
     */
    public static synchronized void initialize(String servers) {

        log4j.info("MemcachedUtil: initialize()");

        // Already initialized?
        if (instance != null) {
            throw new RuntimeException("Instance already initialized!");
        }

        if (GenericValidator.isBlankOrNull(servers)) {
            throw new IllegalArgumentException("Unable to initialialize memcache - invalid servers");
        }
        List<String> serverlist = Arrays.asList(servers.split(","));
        if (serverlist.isEmpty()) {
            throw new IllegalArgumentException("No server addresses");
        }

        //
        // Build Memcached!
        //
        try {
            instance = new MemcachedUtil();
            instance.client = new MemcachedClient(new BinaryConnectionFactory(), AddrUtil.getAddresses(serverlist));

        } catch (Throwable t) {
            log4j.error("Unable to build memcache object!");
        }
    }

    public void shutdown() {
        if (this.client != null) {
            this.client.shutdown(5L, TimeUnit.SECONDS);
        }
    }

    /**
     * Wrapper for add()
     */
    public boolean add(String key, Object o) {
        return add(key, DEFAULT_EXPIRATION, o);
    }

    public boolean add(String key, int expiration, Object o) {
        OperationFuture<Boolean> added = client.add(normalizeKey(key), expiration, o);
        return added.getStatus().isSuccess();
    }

    /**
     * Wrapper for replace()
     */
    public void replace(String key, Object o) {
        replace(key, DEFAULT_EXPIRATION, o);
    }

    public void replace(String key, int expiration, Object o) {
        client.replace(normalizeKey(key), expiration, o);
    }

    /**
     * Wrappers for get() and gets()
     */
    public Object get(String key) {
        Object o = null;
        Future<Object> f = client.asyncGet(normalizeKey(key));
        try {
            o = f.get(ASYNC_TIMEOUT, TimeUnit.SECONDS);
            return o;
        } catch (Exception e) {
            log4j.warn("Unable to retrieve key '" + key + "'");
            f.cancel(true);
            return null;
        }
    }

    public CASValue<Object> gets(String key) {
        CASValue<Object> o = null;
        Future<CASValue<Object>> f = client.asyncGets(normalizeKey(key));
        try {
            o = f.get(ASYNC_TIMEOUT, TimeUnit.SECONDS);
            return o;
        } catch (Exception e) {
            log4j.warn("Unable to retrieve key '" + key + "'");
            f.cancel(true);
            return null;
        }
    }

    /**
     * Wrapper for cas()
     */
    public CASResponse cas(String key, long casId, Object value) {
        CASResponse o = null;
        Future<CASResponse> f = client.asyncCAS(normalizeKey(key), casId, value);
        try {
            o = f.get(ASYNC_TIMEOUT, TimeUnit.SECONDS);
            return o;
        } catch (Exception e) {
            log4j.warn("Unable to retrieve key '" + key + "'");
            f.cancel(true);
            return null;
        }
    }

    /**
     * Wrapper for delete()
     */
    public Future<Boolean> delete(String key) {
        return client.delete(normalizeKey(key));
    }

    /**
     * Wrapper for incr() method
     */
    public long incr(String key) {
        return incr(key, 1);
    }
    public long incr(String key, long def) {
        return incr(key, 1, def);
    }
    public long incr(String key, int by) {
        return incr(key, by, 1l);
    }
    public long incr(String key, int by, long def) {
        return incr(key, by, def, DEFAULT_EXPIRATION);
    }
    public long incr(String key, int by, long def, int exp) {
        log4j.info("Incrementing '" + key +"' by " + by + ", def = " + def + ", exp =" + exp + ".");
        long val = def;
        try {
            CASValue<Object> o = gets(key);
            if (o == null || o.getValue() == null) {
                log4j.info("'" + key + "' not found - adding default");
                add(key, exp, new Long(def).toString());
            } else {
                log4j.info("'" + key + "' found: " + o.getValue());
                val = client.incr(key, by);
            }
            return val;
        } catch (Throwable t) {
            log4j.error("Error trying to increment key '" + key + "'", t);
            delete(key);
            add(key, exp, new Long(val));
            return def;
        }
    }

    /**
     * Wrapper for decr()
     */
    public long decr(String key) {
        return decr(key, 1);
    }
    public long decr(String key, long def) {
        return incr(key, 1, def);
    }
    public long decr(String key, int by) {
        return decr(key, by, 0l);
    }
    public long decr(String key, int by, long def) {
        return decr(key, by, def, DEFAULT_EXPIRATION);
    }
    public long decr(String key, int by, long def, int exp) {
        log4j.info("Decrementing '" + key +"' by " + by + ", def = " + def + ", exp =" + exp + ".");
        long val = def;
        try {
            CASValue<Object> o = gets(key);
            if (o == null || o.getValue() == null) {
                log4j.info("'" + key + "' not found - adding default");
                add(key, exp, new Long(def).toString());
            } else {
                log4j.info("'" + key + "' found: " + o.getValue());
                val = client.decr(key, by);
            }
            return val;
        } catch (Throwable t) {
            log4j.error("Error trying to decrement key '" + key + "'", t);
            delete(key);
            add(key, exp, new Long(val));
            return def;
        }
    }

    private String normalizeKey(String key) {
        return key.replaceAll(" ", "%2d");
    }

    /**
     * Return the memcached client object
     *
     * @return
     */
    private MemcachedClient getClient() {
        return this.client;
    }
}
