<%@ page  import="java.io.*,java.net.*"%>
<%@ page session="false" %>

<%

		String selectValue = null;
		String database = null;
		String searchQuery = null;
		String searchId = null;
		String sessionId = null;
		String urlString = null;
		String redirectURL = null;
		String documentType = null;
		String controllerServer = getServletContext().getInitParameter("controllerServer");

		URL url = null;
		HttpURLConnection con = null;
		File  file = null;


		//System.out.println(" The basket.jsp has been called ");


		try 
		{

 			sessionId = request.getParameter("sessionid");
 			//searchId = request.getParameter("searchid");
			selectValue = request.getParameter("select");
			database = request.getParameter("database");
		

			if(request.getParameter("searchquery")!=null)
			{
				searchQuery = URLDecoder.decode(request.getParameter("searchquery").trim());
		    	}
			if(request.getParameter("searchid")!=null)
			{
				searchId = URLDecoder.decode(request.getParameter("searchid").trim());
		    	}		    	
            
            		documentType = request.getParameter("documenttype");
		

			if(selectValue.trim().equals("markall"))
			{
 				String[] docId = request.getParameterValues("docid");
 				String docIdString = "&docidlist=";
				for(int i = 0; i < docId.length; i++)
				{
 					String tempDocIdString = docId[i]+",";
					docIdString += tempDocIdString;
				}
				String[] handle = request.getParameterValues("handle");
 				String handleString = "&handlelist=";
				for(int i = 0; i < handle.length; i++)
				{
 					String tempHandleString = handle[i]+",";
					handleString += tempHandleString;
				}

				if ( documentType == null )
				{
 					urlString = "http://" + controllerServer + "/controller/servlet/Controller?EISESSION="+sessionId+"&CID=addAllToSelectedSet&searchid="+URLEncoder.encode(searchId)+"&database="+URLEncoder.encode(database)+"&searchquery="+URLEncoder.encode(searchQuery)+docIdString+handleString;
				}
				else
				{
 					urlString = "http://" + controllerServer + "/controller/servlet/Controller?EISESSION="+sessionId+"&CID=addAllToSelectedSet&searchid="+URLEncoder.encode(searchId)+"&database="+URLEncoder.encode(database)+"&documenttype="+URLEncoder.encode(documentType)+"&searchquery="+URLEncoder.encode(searchQuery)+docIdString+handleString;
				}
			}
			else if(selectValue.trim().equals("unmarkall"))
			{
				String[] docId = request.getParameterValues("docid");
				String docIdString = "&docidlist=";
				for(int i = 0; i < docId.length; i++)
				{
					String tempDocIdString = docId[i]+",";
					docIdString += tempDocIdString;
				}
				String[] handle = request.getParameterValues("handle");
				String handleString = "&handlelist=";
				for(int i = 0; i < handle.length; i++)
				{
					String tempHandleString =handle[i]+",";
					handleString += tempHandleString;
				}

                		if ( documentType == null )
				{
 					 urlString = "http://" + controllerServer + "/controller/servlet/Controller?EISESSION="+sessionId+"&CID=deleteAllSelectedSet&searchid="+URLEncoder.encode(searchId)+"&database="+URLEncoder.encode(database)+"&searchquery="+URLEncoder.encode(searchQuery)+docIdString+handleString;
				}
				else
				{
 					 urlString = "http://" + controllerServer + "/controller/servlet/Controller?EISESSION="+sessionId+"&CID=deleteAllSelectedSet&searchid="+URLEncoder.encode(searchId)+"&database="+URLEncoder.encode(database)+"&documenttype="+URLEncoder.encode(documentType)+"&searchquery="+URLEncoder.encode(searchQuery)+docIdString+handleString;
				}
			}
			else if(selectValue.trim().equals("mark"))
			{

				String docId = request.getParameter("docid");
				String handle = request.getParameter("handle");
				if ( documentType == null )
				{
					urlString = "http://" + controllerServer + "/controller/servlet/Controller?EISESSION="+sessionId+"&CID=addToSelectedSet&searchid="+URLEncoder.encode(searchId)+"&handle="+handle+"&docid="+docId+"&database="+URLEncoder.encode(database)+"&searchquery="+URLEncoder.encode(searchQuery);
				}
				else
				{
					urlString = "http://" + controllerServer + "/controller/servlet/Controller?EISESSION="+sessionId+"&CID=addToSelectedSet&searchid="+URLEncoder.encode(searchId)+"&handle="+handle+"&docid="+docId+"&documenttype="+URLEncoder.encode(documentType)+"&database="+URLEncoder.encode(database)+"&searchquery="+URLEncoder.encode(searchQuery);
				}
			}
			else if(selectValue.trim().equals("unmark"))
			{

				String docId = request.getParameter("docid");
				String handle = request.getParameter("handle");
				if ( documentType == null )
				{
					 urlString = "http://" + controllerServer + "/controller/servlet/Controller?EISESSION="+sessionId+"&CID=deleteFromSelectedSet&searchid="+URLEncoder.encode(searchId)+"&handle="+handle+"&docid="+docId+"&database="+URLEncoder.encode(database)+"&searchquery="+URLEncoder.encode(searchQuery);
				}
				else
				{
					urlString = "http://" + controllerServer + "/controller/servlet/Controller?EISESSION="+sessionId+"&CID=deleteFromSelectedSet&searchid="+URLEncoder.encode(searchId)+"&handle="+handle+"&docid="+docId+"&documenttype="+URLEncoder.encode(documentType)+"&database="+URLEncoder.encode(database)+"&searchquery="+URLEncoder.encode(searchQuery);
				}
			}
			else if(selectValue.trim().equals("clearonnewsearch"))
			{
				String clearOnValue = request.getParameter("clearonvalue");
				urlString = "http://" + controllerServer + "/controller/servlet/Controller?EISESSION="+sessionId+"&CID=clearOnNewSearch&clearonvalue="+clearOnValue;
			}
			else
			{
					urlString = "http://" + controllerServer + "/controller/servlet/Controller?EISESSION="+sessionId+"&CID=deleteAllFromSelectedSet&selectoption=clearAll";
			}

			url = new URL(urlString);
			con = (HttpURLConnection)url.openConnection();
			con.connect();
			con.getResponseMessage();

			response.setContentType("image/gif");
			response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
			response.setHeader("Pragma","no-cache"); //HTTP 1.0
			response.setDateHeader ("Expires", 0);//prevents caching at the proxy server
		} catch (Exception e) {
			//e.printStackTrace();
		}
		redirectURL = "/engresources/images/spacer.gif";
		if( redirectURL != null)
		{
			response.sendRedirect(redirectURL);
		}

%>


