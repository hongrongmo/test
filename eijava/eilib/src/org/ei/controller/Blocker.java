package org.ei.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

public class Blocker
{


	public boolean blocked(HttpServletRequest request,
			     		 HttpServletResponse response)
		throws IOException
	{
		return checkRSSBlockList(request,
					 			 response);
	}

	private boolean checkRSSBlockList(HttpServletRequest request,
			     		       		  HttpServletResponse response)
		throws IOException
	{
		boolean blocked = false;
		String CID = request.getParameter("CID");
		String queryID = request.getParameter("queryID");
		if(CID != null &&
		   CID.equals("openRSS") &&
		   queryID != null)
		{
			RSSBlocker blocker = RSSBlocker.getInstance();
			if(blocker.blocked(queryID))
			{
				blocked = true;
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
		}

		return blocked;
	}
}