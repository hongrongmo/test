package org.ei.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Hashtable;

import org.apache.log4j.Logger;

public class RSSBlocker {
	private final static Logger log4j = Logger.getLogger(RSSBlocker.class);

	public static String BLOCKED_MESSAGE = "<rss version=\"2.0\"><channel><title>RSS feed blocked</title><link>http://www.engineeringvillage.com</link><description></description><language>en-us</language><item><title>This RSS feed has been blocked by Engineering Village</title><description>Your RSS reader has been configured to update this feed too frequently. Please change the settings on this feed to update once a day. To have this feed turned back on please contact eicustomersupport@elsevier.com.</description><link>http://www.engineeringvillage.com</link></item><copyright>Copyright " + Calendar.getInstance().get(Calendar.YEAR) + " Elsevier Inc.</copyright></channel></rss>";
	private static RSSBlocker instance;
	private static long createTime;
	private static long currentTime;
	private static long reloadInterval = 600000L; // 10 minutes
	private Hashtable<String, String> blockedList;

	public static synchronized RSSBlocker getInstance() throws IOException {
		currentTime = System.currentTimeMillis();
		if (instance == null || (currentTime - createTime) > reloadInterval) {
			instance = null;
			instance = new RSSBlocker();
			createTime = currentTime;
		}

		return instance;
	}

	public boolean blocked(String queryID) {
		return blockedList.containsKey(queryID);
	}

	private RSSBlocker() throws IOException {
		this.blockedList = new Hashtable<String, String>();
		InputStream blockedrss = this.getClass().getResourceAsStream("/blocked_rss.txt");
		if (blockedrss == null) {
			throw new IOException("Unable to find 'blocked_rss.txt' as resource!");
		}

		log4j.info("Initialize blocked RSS Map...");
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(blockedrss,"UTF-8"));
            for(String line=reader.readLine(); line!=null; line=reader.readLine()) {
                this.blockedList.put(line, "y");
            }
		} finally {
			if (blockedrss != null) blockedrss.close();
			if (reader != null) reader.close();
		}
		log4j.info(this.blockedList.size() + " entries added to blocked RSS Map!");
	}
}