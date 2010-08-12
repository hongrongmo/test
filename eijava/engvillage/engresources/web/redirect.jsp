<%@ page language="java" %>
<%@ page session="false" %>
<%@ page import="java.util.*"%>
<%@ page import="java.net.*"%>
<%@ page import="java.io.*"%>


<%
	
	URL url = null;
	HttpURLConnection ucon = null;
	InputStream in = null;
	try 
	{
		String citedbyServer = getServletContext().getInitParameter("citedbyServer");
		String urlString   = "http://"+citedbyServer+"/citedby/servlet/CitedByService?";
		System.out.println("citedbyServer= "+citedbyServer);
		String queryString = request.getParameter("citedby");	
		System.out.println("queryString= "+queryString);
		url = new URL(urlString+queryString);
		ucon = (HttpURLConnection) url.openConnection();
		//ucon.setRequestMethod("POST");
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
			