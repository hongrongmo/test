<% response.setContentType("application/opensearchdescription+xml"); %>
<%
		String serverName = request.getServerName();
		int serverPort = request.getServerPort();
		if(serverPort != 80)
		{
			serverName = serverName+":"+Integer.toString(serverPort);
		}
		
		String query = request.getParameter("q");
		java.net.URL url = new java.net.URL("http://" + serverName + "/controller/servlet/Controller?CID=openATOM&SYSTEM_PT=t&database=1&q=" + query);
		java.net.URLConnection conn = url.openConnection();
		java.io.InputStream istream = conn.getInputStream();
		int c = 0;
		while((c = istream.read()) != -1)
		{
			out.write(c);
		}
		istream.close();
%>