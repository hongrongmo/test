<%@ page language="java" %>
<%@ page session="false" %>
<%@ page import="java.util.*"%>
<%@ page import="java.net.*"%>
<%@ page import="java.io.*"%>
<%@ page import="org.apache.commons.httpclient.*"%>
<%@ page import="org.apache.commons.httpclient.methods.*"%>


<%
	
	URL url = null;
	HttpURLConnection ucon = null;
	HttpClient httpClient = null;
	InputStream in = null;
	PostMethod post = null;
	String citedbyServer = null;

	try 
	{		
		citedbyServer = getServletContext().getInitParameter("citedbyServer");
		String urlString   = "http://"+citedbyServer+"/citedby/servlet/CitedByService";
		System.out.println("citedbyServer= "+citedbyServer);
		String queryString = request.getParameter("citedby");	
		System.out.println("queryString= "+queryString);
		
		httpClient = new HttpClient();
		post = new PostMethod(urlString);
		post.setQueryString(queryString);
		int status = httpClient.executeMethod(post);
		System.out.println("status line = " + status);
		
		if (status==HttpStatus.SC_OK){
			InputStream stream = post.getResponseBodyAsStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			String line = null;
			StringBuffer content = new StringBuffer();
			while((line = reader.readLine()) != null){
				content.append(line);
			}
			System.out.println(content.toString());
		}		
		
	}
	catch(Exception e)
	{
		e.printStackTrace();
	}
	finally 
	{
	   post.releaseConnection();
	}
%>
			