package org.ei.controller;

import java.io.*;
import java.util.*;

public class RSSBlocker
{
	public static String BLOCKED_MESSAGE ="<rss version=\"2.0\"><channel><title>RSS feed blocked</title><link>http://www.engineeringvillage.com</link><description></description><language>en-us</language><item><title>This RSS feed has been blocked by Engineering Village</title><description>Your RSS reader has been configured to update this feed too frequently. Please change the settings on this feed to update once a day. To have this feed turned back on please contact eicustomersupport@elsevier.com.</description><link>http://www.engineeringvillage.com</link></item><copyright>Copyright 2011 Elsevier Inc.</copyright></channel></rss>";
	private static RSSBlocker instance;
	private static long createTime;
	private static long currentTime;
	private static long reloadInterval = 600000L; // 10 minutes
	private Hashtable blockedList;

	public static synchronized RSSBlocker getInstance()
		throws IOException
	{
		currentTime = System.currentTimeMillis();
		if(instance == null ||
		  (currentTime - createTime) > reloadInterval)
		{
			instance = null;
			instance = new RSSBlocker();
			createTime = currentTime;
		}

		return instance;
	}

	public boolean blocked(String queryID)
	{
		return blockedList.containsKey(queryID);
	}

	private RSSBlocker()
		throws IOException
	{
		BufferedReader in = null;
		this.blockedList = new Hashtable();
		File f = new File("blocked_rss.txt");
		//System.out.println("Init RSS Blocker");

		if(f.exists())
		{
			//System.out.println("Found RSS blocker file");

			try
			{
				in = new BufferedReader(new FileReader(f));
				String line = null;
				while((line = in.readLine()) != null)
				{
					this.blockedList.put(line, "y");
				}
			}
			finally
			{
				if(in != null)
				{
					try
					{
						in.close();
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
	}
}