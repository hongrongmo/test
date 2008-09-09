package org.ei.controller;

import java.util.*;
import java.io.*;

public class IPBlocker
{
	private Map blockMap;
	private static IPBlocker instance;
	private static long createTime;
	private static long currentTime;
	private static long reloadInterval = 600000L; // 10 minutes

	public static synchronized IPBlocker getInstance()
		throws IOException
	{
		currentTime = System.currentTimeMillis();
		if(instance == null ||
		  (currentTime - createTime) > reloadInterval)
		{
			instance = null;
			instance = new IPBlocker();
			createTime = currentTime;
		}

		return instance;
	}

	private IPBlocker()
		throws IOException
	{
		BufferedReader in = null;
		this.blockMap = new HashMap();
		File f = new File("blocked_ip.txt");
		System.out.println("Init IP Blocker");

		if(f.exists())
		{
			System.out.println("Found IP blocker file");
			try
			{
				in = new BufferedReader(new FileReader(f));
				String line = null;
				while((line = in.readLine()) != null)
				{
					this.blockMap.put(line, "y");
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

	public boolean block(String ip)
	{
		return blockMap.containsKey(ip);
	}
}