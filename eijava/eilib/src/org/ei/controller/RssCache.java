package org.ei.controller;

import java.util.*;
import java.io.*;
import javax.servlet.http.HttpServletResponse;
import org.ei.util.*;


public class RssCache
{
	private static Object lock = new Object();

	public static String getFileName(String rssID) throws IOException
	{
		String fileName=null;
		String rPath = null;
		String aPath = null;

		rPath= getPath();
		File newFile = new File(rPath);
		aPath = newFile.getAbsolutePath();
		//System.out.println("apath:"+aPath);
		synchronized(lock)
		{
			newFile.mkdirs();
		}

		fileName = aPath+"/"+rssID+".xml";

		return fileName;
	}

	private static String getPath()
	{
		Calendar rightNow = Calendar.getInstance();
		String year = Integer.toString(rightNow.get(Calendar.YEAR));
		String month = getMonthString(rightNow.get(Calendar.MONTH));
		String day = getDayString(rightNow.get(Calendar.DAY_OF_MONTH));
		String path = "cache/"+year+month+day;
		return path;
	}

	private static String getDayString(int day)
	{
		String d = Integer.toString(day);
		if(d.length() == 1)
		{
			d = "0"+d;
		}

		return d;
	}

	private static String getMonthString(int month)
	{
		month++;
		String m = Integer.toString(month);
		if(m.length() == 1)
		{
			m="0"+m;
		}

		return m;
	}


	public static boolean cached(String queryID,
						  HttpServletResponse response)
		throws IOException,
			   SpinLockException
	{
		SpinLock spinLock = SpinLock.getInstance();
		boolean lockPlaced = false;
		boolean cached = false;

		try
		{
			lockPlaced = spinLock.placeLock(queryID,
											SpinLock.FOR_READ,
											7,
											1000);
			if(lockPlaced)
			{
				String fileName = getFileName(queryID);
				if(exists(fileName))
				{
					printFile(fileName, response);
					cached = true;
				}
			}
			else
			{
				throw new IOException("could not obtain read lock");
			}
		}
		finally
		{
			if(lockPlaced)
			{
				spinLock.releaseLock(queryID,
									 SpinLock.FOR_READ);
			}
		}

		return cached;
	}

	public static boolean exists(String fileName) throws IOException
	{
		boolean checkFile = false;
		File newFile = new File(fileName);
		checkFile = newFile.exists();
		return checkFile;
	}

	public static void printFile(String fileName,
						  HttpServletResponse response)
		throws IOException
	{
		//System.out.println("get from cache");
		Writer out = null;
		BufferedReader in = null;
		try
		{
			response.setContentType("text/xml");
			out = response.getWriter();
			in = new BufferedReader(new FileReader(fileName));
			String line = null;
			while((line = in.readLine()) != null)
			{
				out.write(line);
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

			if(out != null)
			{
				out.close();
			}
		}
	}
}