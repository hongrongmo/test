package org.ei.controller;

import java.io.IOException;
import java.net.InetAddress;
import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.config.RuntimeProperties;
import org.ei.email.SESEmail;
import org.ei.email.SESMessage;
import org.ei.exception.ServiceException;
import org.ei.session.BlockedIPEvent;
import org.ei.session.BlockedIPEvent.TimePeriod;
import org.ei.session.BlockedIPStatus;

public class IPBlocker {
    private final static Logger log4j = Logger.getLogger(IPBlocker.class);

    public static final String IPBLOCKER_BUCKET_INTERVAL_MINUTES_PROPERTY = "ipblocker.bucket.interval.minutes";
    public static final String IPBLOCKER_SESSION_LIMIT_PROPERTY = "ipblocker.session.limit";
    public static final String IPBLOCKER_REQUEST_LIMIT_PROPERTY = "ipblocker.request.limit";
    public static final String IPBLOCKER_EMAIL_TO_PROPERTY = "ipblocker.email.to";
    public static final String IPBLOCKER_EMAIL_FROM_PROPERTY = "ipblocker.email.from";
    public static final String IPBLOCKER_DELAY_MILLIS = "ipblocker.delay.millis";
    public static final String IPBLOCKER_DELAY_ENABLED = "ipblocker.delay.enabled";

    // Singleton instance
    private static IPBlocker instance;

    // Available items to count
    public enum COUNTER {
        SESSION, REQUEST
    }

    // Member variables
    private Map<String, String> blockMap = new HashMap<String, String>();
    private Map<String, String> whitelistMap = new HashMap<String, String>();
    private long createTime = System.currentTimeMillis();

    private static long reloadInterval = 600000L; // 10 minutes
    private static int bucketincrementtimemin = 15 * 60;
    private static long sessionlimit = 500;
    private static long requestlimit = 1500;
    private static long delay = 0;
    private static boolean delayenabled = false;
    private static String emailto;
    private static String sender;

    /**
     * Singleton instance
     *
     * @return
     * @throws IOException
     */
    public static synchronized IPBlocker getInstance() {
        long currentTime = System.currentTimeMillis();
        if (instance == null) {
            log4j.warn("Creating new IPBlocker instance (initial)");
            instance = new IPBlocker();
            refreshProperties();
        } else if ((currentTime - instance.getCreateTime()) > reloadInterval) {
            log4j.warn("Creating new IPBlocker instance (reload)");
            instance.blockMap.clear();
            instance.blockMap = null;
            instance = null;
            instance = new IPBlocker();
            refreshProperties();
        }

        return instance;
    }

    /**
     * Get the Runtime properties used with IPBlocker
     *
     * @throws IOException
     */
    private static void refreshProperties() {
        try {
            RuntimeProperties runtimeprops = RuntimeProperties.getInstance();
            bucketincrementtimemin = Integer.parseInt(runtimeprops.getProperty(IPBLOCKER_BUCKET_INTERVAL_MINUTES_PROPERTY, "15"));
            sessionlimit = Long.parseLong(runtimeprops.getProperty(IPBLOCKER_SESSION_LIMIT_PROPERTY, "500"));
            requestlimit = Long.parseLong(runtimeprops.getProperty(IPBLOCKER_REQUEST_LIMIT_PROPERTY, "1500"));
            emailto = runtimeprops.getProperty(IPBLOCKER_EMAIL_TO_PROPERTY);
            sender = runtimeprops.getProperty(IPBLOCKER_EMAIL_FROM_PROPERTY);
            delay = Long.parseLong(runtimeprops.getProperty(IPBLOCKER_DELAY_MILLIS, "0"));
            delayenabled = Boolean.parseBoolean(runtimeprops.getProperty(IPBLOCKER_DELAY_ENABLED, "false"));
        } catch (Throwable t) {
            log4j.error("Unable to refresh properties!", t);
        }
    }

    /**
     * Constructor - attempt to read blocked_ips.txt file from build path.
     *
     * @throws IOException
     */
    private IPBlocker() {
        log4j.info("Initializing blocked IPs Map...");

        try {
            List<BlockedIPStatus> blockedIPs = BlockedIPStatus.getByStatus(BlockedIPStatus.STATUS_ANY);
            if (blockedIPs != null && !blockedIPs.isEmpty()) {
                for (BlockedIPStatus ipstatus : blockedIPs) {
                    if (ipstatus.isBlocked()) {
                        this.blockMap.put(ipstatus.getIP(), "y");
                    } else if (BlockedIPStatus.STATUS_WHITELIST.equals(ipstatus.getStatus())) {
                        this.whitelistMap.put(ipstatus.getIP(), "true");
                    }
                }
            }
        } catch (Throwable t) {
            log4j.error("Unable to read blocked IPs from database...", t);
            this.blockMap.clear();
        }

        log4j.info(this.blockMap.size() + " entries added to blocked IPs Map!");

    }

    /**
     * Determine if user is blocked by comparing IP to IPs from list.
     *
     * @param request
     * @return
     */
    public boolean isBlocked(String ip) {
        log4j.info("Checking for IP blocks...");
        MemcachedUtil memcached = MemcachedUtil.getInstance();

        // If blank, automatically false
        if (GenericValidator.isBlankOrNull(ip)) {
            return false;
        }

        // Check if IP is in blocked IPs list (file system static list)
        boolean blocked = blockMap.containsKey(ip);
        if (blocked) {
            log4j.warn("Returning true due to BLOCKED status for '" + ip + "'");
            return true;
        }

        // Check if IP is in whitelist
        boolean whitelist = whitelistMap.containsKey(ip);
        if (whitelist) {
            log4j.warn("Returning false due to WHITELIST status for '" + ip + "'");
            return false;
        }

        try {
            // Check if bucket has signaled abuse
            if (isThresholdReached(ip, COUNTER.REQUEST)) {
                // Delay request if enabled
                if (delayenabled) {
                    log4j.warn("Delaying request due to abuse detection, IP = " + ip);
                    Thread.sleep(delay);
                }

                // Take one-time action for this abuse
                String bucketname = getBucketName(ip, COUNTER.REQUEST);
                String messagebucket = bucketname + "_MESSAGE";
                if (memcached.get(messagebucket) == null) {
                    memcached.add(messagebucket, bucketincrementtimemin * 60, new Boolean(true));
                    BlockedIPEvent ipevent = new BlockedIPEvent(ip);
                    ipevent.setEvent(COUNTER.REQUEST.name());
                    ipevent.setBucket(bucketname);
                    ipevent.setMessage("Maximum number of requests exceeded (" + requestlimit + ") in " + bucketincrementtimemin + " minutes for IP address "
                        + ipevent.getIP());
                    BlockedIPStatus ipstatus = insertBlockedIPsTable(ipevent);
                    notifyEmail(ipevent, ipstatus);
                    return ipstatus.isBlocked();
                }
            } else if (isThresholdReached(ip, COUNTER.SESSION)) {
                // Delay request if enabled
                if (delayenabled) {
                    log4j.warn("Delaying request due to abuse detection, IP = " + ip);
                    Thread.sleep(delay);
                }

                // Take one-time action for this abuse
                String bucketname = getBucketName(ip, COUNTER.SESSION);
                String messagebucket = bucketname + "_MESSAGE";
                if (memcached.get(messagebucket) == null) {
                    memcached.add(messagebucket, bucketincrementtimemin * 60, new Boolean(true));
                    BlockedIPEvent ipevent = new BlockedIPEvent(ip);
                    ipevent.setEvent(COUNTER.SESSION.name());
                    ipevent.setBucket(bucketname);
                    ipevent.setMessage("Maximum number of new sessions exceeded (" + sessionlimit + ") in " + bucketincrementtimemin
                        + " minutes for IP address " + ipevent.getIP());
                    BlockedIPStatus ipstatus = insertBlockedIPsTable(ipevent);
                    notifyEmail(ipevent, ipstatus);
                    return ipstatus.isBlocked();
                }
            }
        } catch (Throwable t) {
            log4j.error("Unable to check session/request limits!", t);
        }

        // Default return false!
        return false;
    }

    /**
     * Increment a count.
     *
     * @param counter
     * @return
     * @throws IOException
     */
    public long increment(String ip, COUNTER counter) {
        MemcachedUtil memcached = MemcachedUtil.getInstance();
        String bucketname = getBucketName(ip, counter);
        try {
            log4j.info("Incrementing bucket: " + bucketname + " by 1, time interval = " + bucketincrementtimemin * 60);
            return memcached.incr(bucketname, 1, 1L, bucketincrementtimemin * 60);
        } catch (Throwable t) {
            log4j.warn("Unable to increment " + counter.name() + " for IP " + ip);
            try {
                memcached.delete(bucketname);
            } catch (Throwable t2) {};
            return 0;
        }
    }

    /**
     * Determine if threshold limit reached for COUNTER
     *
     * @param ip
     * @param counter
     * @return
     * @throws IOException
     */
    public boolean isThresholdReached(String ip, COUNTER counter) {
        MemcachedUtil memcached = MemcachedUtil.getInstance();
        String bucket = "";
        long count = 0;
        try {
            bucket = getBucketName(ip, counter);
            Object obj = memcached.get(bucket);
            if (obj != null && obj.toString() != null)
                count = Long.parseLong(obj.toString().trim());

            if (counter.equals(COUNTER.REQUEST))
                return count > requestlimit;
            else if (counter.equals(COUNTER.SESSION))
                return count > sessionlimit;
            else
                return false;
        } catch (Throwable t) {
            log4j.error("Unable to parse value from bucket " + bucket, t);
            try {
                if (bucket != null) memcached.delete(bucket);
            } catch (Throwable t2) {};
            return false;
        }
    }

    /**
     * Retreive current status.
     *
     * @param ip
     *            the ip
     * @return the map
     * @throws ParseException
     *             the parse exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public Map<String, String> retreiveCurrentStatus(String ip) {
        MemcachedUtil memcached = MemcachedUtil.getInstance();
        Map<String, String> statusMap = new LinkedHashMap<String, String>();

        String bucket = getBucketName(ip, COUNTER.SESSION);
        long count = memcached.incr(bucket, 0, 1, bucketincrementtimemin * 60);
        statusMap.put(bucket, Long.toString(count));

        bucket = getBucketName(ip, COUNTER.REQUEST);
        count = memcached.incr(bucket, 0, 1, bucketincrementtimemin * 60);
        statusMap.put(bucket, Long.toString(count));

        return statusMap;
    }

    public void clearBucket(String ip, COUNTER counter) {
        MemcachedUtil memcached = MemcachedUtil.getInstance();
        String bucket = getBucketName(ip, COUNTER.REQUEST);
        log4j.info("Deleting bucket " + bucket);
        memcached.delete(bucket);
        memcached.delete(bucket + "_MESSAGE");
    }

    /**
     * Gets the bucket name prefix.
     *
     * @param ip
     *            the ip
     * @return the bucket name prefix
     * @throws ParseException
     *             the parse exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    public String getBucketName(String ip, COUNTER counter) {
        return (ip + "_" + getTimeStampByInterval() + "_" + counter.name());
    }

    /**
     * Gets the time stamp by interval.
     *
     * @return the time stamp by interval
     * @throws ParseException
     *             the parse exception
     * @throws IOException
     *             Signals that an I/O exception has occurred.
     */
    private String getTimeStampByInterval() {
        Calendar timestamp = Calendar.getInstance();
        timestamp.setTimeZone(TimeZone.getTimeZone("America/New_York"));

        int divider = timestamp.get(Calendar.MINUTE) / bucketincrementtimemin;
        String minStr = "00";
        if (divider != 0) {
            int minsCalculated = divider * bucketincrementtimemin;
            if (minsCalculated < 10) {
                minStr = "0" + minsCalculated;
            } else {
                minStr = "" + minsCalculated;
            }
        }
        return timestamp.get(Calendar.HOUR_OF_DAY) + minStr + "00";
    }

    /**
     * Update BlockedIPStatus and BlockedIPEvent tables. Should ONLY be used when an event has occurred.
     *
     * @param ipevent
     * @return
     * @throws ServiceException
     * @throws Exception
     */
    public static BlockedIPStatus insertBlockedIPsTable(BlockedIPEvent ipevent) {
        log4j.warn("Attempting to insert a new entry for the ip (" + ipevent.getIP() + ") into the table.");
        try {
            ipevent.save();

            // Update the main ipstatus entry
            BlockedIPStatus ipstatus = BlockedIPStatus.load(ipevent.getIP());
            if (ipstatus == null) {
                ipstatus = new BlockedIPStatus(ipevent.getIP());
            }
            ipstatus.addAccount(org.ei.domain.personalization.cars.Account.getAccountInfo(ipevent.getIP()));
            ipstatus.save();

            return ipstatus;
        } catch (Throwable t) {
            log4j.error("Unable to save event!", t);
            return null;
        }
    }

    static String newline = System.getProperty("line.separator") == null ? "\r\f" : System.getProperty("line.separator");

    /**
     * Notify stakeholders via email of abuse
     *
     * @param ipevent
     * @param ipstatus
     * @throws Exception
     */
    public static void notifyEmail(BlockedIPEvent ipevent, BlockedIPStatus ipstatus) {

        try {
            if (ipevent == null || ipstatus == null) {
                throw new IllegalArgumentException("Event or Status object was null!");
            }

            if (GenericValidator.isBlankOrNull(emailto) || GenericValidator.isBlankOrNull(sender)) {
                throw new IllegalArgumentException("Email addresses are not set!");
            }

            log4j.info("Preparing email for event...");
            String subject = "****** EV " + ipevent.getEvent() + " RATE EXCEEDED (" + ipevent.getIP() + ")! ******";

            StringBuffer message = new StringBuffer();
            message.append("##########################################################################################################################"
                + newline);
            message.append(ipevent.getMessage() + newline);
            message.append("    Timestamp: " + ipevent.getTimestamp() + newline);
            message.append("    From host: '" + (InetAddress.getLocalHost()).getHostName() + "'" + newline);
            if (!GenericValidator.isBlankOrNull(ipstatus.getAccountName())) {
                message.append("    Account:  " + ipstatus.getAccountName() + " (" + ipstatus.getAccountNumber() + ") " + newline);
                if (!GenericValidator.isBlankOrNull(ipstatus.getDepartmentName())) {
                    message.append("    Department:  " + ipstatus.getDepartmentName() + " (" + ipstatus.getDepartmentNumber() + ") " + newline);
                }
            }
            message.append("##########################################################################################################################"
                + newline + newline);

            message
                .append("You can take action by submitting the form at http://lngcentral.lexisnexis.com/depts/Edit/AppSupp/Forms/SecurityBreach.aspx (This URL requires Elsevier VPN access)"
                    + newline + newline);

            message.append("You can view all abuse at http://" + RuntimeProperties.getInstance().getAppDomain() + "/status/ipblocker.url (This URL is IP protected!)" + newline
                + newline);

            List<BlockedIPEvent> history = BlockedIPEvent.getByTimePeriod(ipevent.getIP(), TimePeriod.LASTYEAR);
            message.append("Customer Abuse History (" + history.size() + " item" + (history.size() > 1 ? "s" : "") + "): " + newline);
            for (BlockedIPEvent historyitem : history) {
                message.append("    Timestamp:  " + historyitem.getTimestamp() + newline);
                message.append("    " + historyitem.getMessage() + newline + newline);
            }

            log4j.warn("Attempting to notify '" + emailto + "' admin from email '" + sender + "' for the ip (" + ipevent.getIP() + ") with the message: '"
                + ipevent.getMessage() + "'.");
            SESMessage sesmessage = new SESMessage();
            sesmessage.setDestination(emailto);
            sesmessage.setMessage(subject, message.toString(),false);
            sesmessage.setFrom(sender);
            SESEmail.getInstance().send(sesmessage);
        } catch (Throwable t) {
            log4j.error("Error occurred while sending email!", t);
        }

    }

    public Map<String, String> getBlockmap() {
        return this.blockMap;
    }

    public long getCreateTime() {
        return this.createTime;
    }

    public long getReloadInterval() {
        return reloadInterval;
    }
}