<%@ page language="java" %>
<%@ page session="false" %>
<%@ page errorPage="/error/errorPage.jsp"%>
<%@ page buffer="20kb"%>

<%@ page import="java.util.*"%>
<%@ page import="java.net.*"%>
<%@ page import="java.io.*"%>

<%
	
	String citedbyServer = getServletContext().getInitParameter("citedbyServer");


	try 
	{

		String urlString   = "http://"+citedbyServer+"/citedby/servlet/CitedByService?";
		String queryString = request.getQueryString();
		//System.out.println("URL= "+urlString+queryString);
		URL url = new URL(urlString+queryString);
		HttpURLConnection ucon = (HttpURLConnection) url.openConnection();
		ucon.setDoOutput(true);

		InputStream in = ucon.getInputStream();
		InputStreamReader inR = new InputStreamReader(in);
		BufferedReader bin = new BufferedReader(inR);
		String line = null;

		StringBuffer contentBuffer = new StringBuffer();
	
	
	
		while((line = bin.readLine()) != null)
		{
			contentBuffer.append(line);
			
		}
		//System.out.println(contentBuffer.toString());
		out.println(contentBuffer.toString());
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
%>
			