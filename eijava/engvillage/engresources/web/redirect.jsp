<%@ page language="java" %>
<%@ page session="false" %>
<%@ page import="java.io.*, org.apache.commons.httpclient.*, org.apache.commons.httpclient.methods.*"%>

<%!
	String citedbyServer = null;
	public void jspInit()
	{
		citedbyServer = getServletContext().getInitParameter("citedbyServer");
	}
%>
<%
	HttpClient httpClient = null;
	InputStream in = null;
	PostMethod post = null;
	try 
	{			
		String urlString   = "http://"+citedbyServer+"/citedby/servlet/CitedByService";
		String queryString = request.getParameter("citedby");	
		if(queryString==null)
		{
			queryString = request.getParameter("eid");
		}
		httpClient = new HttpClient();
		post = new PostMethod(urlString);
		post.setQueryString(queryString);
		int status = httpClient.executeMethod(post);		
		if (status==HttpStatus.SC_OK){
			InputStream stream = post.getResponseBodyAsStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			String line = null;
			StringBuffer content = new StringBuffer();
			while((line = reader.readLine()) != null){
				content.append(line);
			}
			out.println(content.toString());
			reader.close();
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