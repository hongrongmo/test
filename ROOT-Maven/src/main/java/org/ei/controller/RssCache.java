package org.ei.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Writer;
import java.util.Calendar;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.GenericValidator;
import org.apache.log4j.Logger;
import org.ei.domain.RSSFeed;
import org.ei.domain.RSSFeed.Channel;
import org.ei.domain.RSSFeed.Channel.Image;
import org.ei.domain.RSSFeed.Channel.Item;
import org.ei.util.SpinLock;
import org.ei.util.SpinLockException;
import org.ei.util.StringUtil;

public class RssCache {
    private static final Logger log4j = Logger.getLogger(RssCache.class);

    private static Object lock = new Object();

    public static String getFileName(String rssID) throws IOException {
        String fileName = null;
        String rPath = null;
        String aPath = null;

        rPath = getPath();
        File newFile = new File("/tmp/" + rPath);
        aPath = newFile.getAbsolutePath();
        // System.out.println("apath:"+aPath);
        synchronized (lock) {
            newFile.mkdirs();
        }

        fileName = aPath + "/" + rssID + ".xml";

        return fileName;
    }

    private static String getPath() {
        Calendar rightNow = Calendar.getInstance();
        String year = Integer.toString(rightNow.get(Calendar.YEAR));
        String month = getMonthString(rightNow.get(Calendar.MONTH));
        String day = getDayString(rightNow.get(Calendar.DAY_OF_MONTH));
        String path = "cache/" + year + month + day;
        return path;
    }

    private static String getDayString(int day) {
        String d = Integer.toString(day);
        if (d.length() == 1) {
            d = "0" + d;
        }

        return d;
    }

    private static String getMonthString(int month) {
        month++;
        String m = Integer.toString(month);
        if (m.length() == 1) {
            m = "0" + m;
        }

        return m;
    }

    public static boolean isCached(String queryid) {
        if (GenericValidator.isBlankOrNull(queryid)) return false;
        try {
            return exists(getFileName(queryid));
        } catch (Throwable t) {
            log4j.error("Unable to determine cache status for query ID: " + queryid);
            return false;
        }
    }

    public static void cache(RSSFeed rssfeed, Writer responsewriter) throws IOException, SpinLockException {

        Channel channel = rssfeed.getChannel();
        RssFileWriter writer = null;

        try {
            writer = new RssFileWriter(responsewriter, rssfeed.getQueryid());
            writer.write("<rss version=\"2.0\">\r\n");
            writer.write("<channel>\r\n");
            writer.write("<title>" + channel.getTitle() + "</title>\r\n");
            writer.write("<link>" + channel.getLink() + "</link>\r\n");
            writer.write("<description>" + channel.getDescription() + "</description>\r\n");
            writer.write("<language>" + channel.getLanguage() + "</language>\r\n");
            writer.write("<ttl>" + channel.getTtl() + "</ttl>\r\n");
            if (!GenericValidator.isBlankOrNull(channel.getCopyright())) {
                writer.write("<copyright>" + channel.getCopyright() + "</copyright>\r\n");
            }
            if (!GenericValidator.isBlankOrNull(channel.getPubdate())) {
                writer.write("<pubDate>" + channel.getPubdate() + "</pubDate>\r\n");
            }
            if (channel.getImage() != null) {
                Image image = channel.getImage();
                writer.write("<image><url>" + image.getUrl() + "</url><title>" + image.getTitle()+ "</title><link>" + image.getLink() + "</link></image>\r\n");
            }
            for (Item item : channel.getItems()) {
                writer.write("<item>\r\n");
                writer.write("<title>" + StringUtil.escapeHtml(item.getTitle()) + "</title>\r\n");
                writer.write("<link>" + item.getLink() + "</link>\r\n");
                writer.write("<description>" + StringUtil.escapeHtml(item.getDescription()) + "</description>\r\n");
                if (!GenericValidator.isBlankOrNull(item.getGuid())) {
                    writer.write("<guid>" + item.getGuid() + "</guid>\r\n");
                }
                if (!GenericValidator.isBlankOrNull(item.getPubdate())) {
                    writer.write("<pubDate>" + item.getPubdate() + "</pubDate>\r\n");
                }
                writer.write("</item>\r\n");
            }
            writer.write("</channel>\r\n");
            writer.write("</rss>");
        } catch (Throwable t) {
            log4j.error("Unable to write RSS contents", t);
        } finally {
            if (writer != null) {
                writer.flush();
                writer.close();
            }
        }
    }

    public static boolean exists(String fileName) throws IOException {
        boolean checkFile = false;
        File newFile = new File(fileName);
        checkFile = newFile.exists();
        return checkFile;
    }

    public static void printFile(String queryID, Writer out) throws IOException, SpinLockException {
        SpinLock spinLock = SpinLock.getInstance();
        BufferedReader in = null;
        boolean lockPlaced = false;
        try {
            lockPlaced = spinLock.placeLock(queryID, SpinLock.FOR_READ, 7, 1000);
            in = new BufferedReader(new FileReader(getFileName(queryID)));
            String line = null;
            while ((line = in.readLine()) != null) {
                out.write(line);
            }
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            if (lockPlaced) {
                spinLock.releaseLock(queryID, SpinLock.FOR_READ);
            }
        }
    }
}