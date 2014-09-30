package org.ei.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import org.ei.util.SpinLockException;

public class RssHandler
{
	public static boolean handledRequest(HttpServletRequest request,
                        				 HttpServletResponse response)
    	throws IOException,
    		   SpinLockException
	{
		boolean handled = false;
		String CID = request.getParameter("CID");
		String queryID = request.getParameter("queryID");
		if(CID != null &&
		   CID.indexOf("openRSS") > -1 &&
		   queryID != null)
		{

			/*
				This is an RSS request.
				First check to see if the request has been blocked.

				Next check to see if it has been cached.
			*/

			RSSBlocker blocker = RSSBlocker.getInstance();
			if(blocker.blocked(queryID))
			{
				handled = true;
				Writer outWriter = null;
				try
				{
					response.setContentType("text/xml");
					outWriter = response.getWriter();
					outWriter.write(blocker.BLOCKED_MESSAGE);
				}
				finally
				{
					if(outWriter != null)
					{
						try
						{
							outWriter.close();
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
				}
			}
			else if(RssCache.cached(queryID,response))
			{
				handled = true;
			}

			request.setAttribute("cache", queryID);
		}

		return handled;
	}
}