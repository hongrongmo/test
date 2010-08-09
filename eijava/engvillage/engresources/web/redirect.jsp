<%@ page language="java" %>
<%@ page session="false" %>
<%@ page errorPage="/error/errorPage.jsp"%>
<%@ page buffer="20kb"%>

<%@ page import="java.util.*"%>
<%@ page import="java.net.*"%>
<%@ page import="java.io.*"%>

<%
	
	String citedbyServer = getServletContext().getInitParameter("citedbyServer");
	URL url = null;
	HttpURLConnection ucon = null;
	InputStream in = null;

	try 
	{

		String urlString   = "http://"+citedbyServer+"/citedby/servlet/CitedByService?";
		String queryString = request.getQueryString();
		
		url = new URL(urlString+queryString);
		ucon = (HttpURLConnection) url.openConnection();
		ucon.setDoOutput(true);
		int status = ucon.getResponseCode();
		
		if(status==HttpURLConnection.HTTP_OK)
		{
			in = ucon.getInputStream();
			InputStreamReader inR = new InputStreamReader(in);
			BufferedReader bin = new BufferedReader(inR);
			String line = null;

			StringBuffer contentBuffer = new StringBuffer();

			while((line = bin.readLine()) != null)
			{
				contentBuffer.append(line);

			}
		
			out.println(contentBuffer.toString());
		}
		else
		{
			out.println(status);
		}
	}
	catch(Exception e)
	{
		e.getMessage();
	}
	finally 
	{
	    try
	    {
	    	   
	    	    if(in != null)
	    	    {
	    	    	in.close();
	    	    }
	    	    
		    if(ucon!=null)
		    {
			ucon.disconnect();
		    }
		    
	    }
	    catch(Exception e)
	    {
	    }
	}
%>
			