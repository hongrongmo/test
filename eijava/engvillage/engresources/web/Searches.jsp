<%@ page language="java" %>
<%@ page session="false" %>
<%@ page import="java.io.*"%>
<%@ page import="java.net.*"%>
<%
    String selectValue = null;
    String databaseId = null;
    
    String searchId = null;
    String sessionId = null;
    String option=null;
    String emailOption=null;
    
    String urlString = null;
    String redirectURL = null;
    String controllerServer = getServletContext().getInitParameter("controllerServer");
    
    URL url = null;
    HttpURLConnection con = null;
    File  file = null;

	try 
	{

		if(request.getParameter("sessionid")!=null)
        {
			sessionId = request.getParameter("sessionid").trim();
		}
		if(request.getParameter("searchid")!=null)
		{
			searchId = request.getParameter("searchid").trim();
		}
		if(request.getParameter("select")!=null)
		{
			selectValue = request.getParameter("select").trim();
		}
		if(request.getParameter("databaseid")!=null)
		{
			databaseId = request.getParameter("databaseid").trim();
		}
		if(request.getParameter("option")!=null)
		{
			option=request.getParameter("option").trim();
		}
		if(request.getParameter("emailoption")!=null)
		{
			emailOption=request.getParameter("emailoption").trim();
		}
		if(selectValue.trim().equals("mark"))
		{

			if(option != null && option.trim().equals("EmailAlert") )
			{
                if(emailOption!=null && emailOption.equals("On"))
				{
				    urlString = "http://" + controllerServer + "/controller/servlet/Controller?EISESSION="+sessionId+"&CID=addDeleteSavedSearch&selectvalue=mark&searchid="+searchId+"&option="+option+"&databaseid="+URLEncoder.encode(databaseId);
					redirectURL = "/engresources/images/saved_blue.gif";
                }
				else if(emailOption!=null && emailOption.equals("emailon"))
				{
					urlString = "http://" + controllerServer + "/controller/servlet/Controller?EISESSION="+sessionId+"&CID=updateSavedSearch&selectvalue=mark&searchid="+searchId+"&option="+option+"&databaseid="+URLEncoder.encode(databaseId);
					redirectURL = "/engresources/images/saved_blue.gif";
				}
				else if(emailOption!=null && emailOption.equals("emailoff"))
				{
					urlString = "http://" + controllerServer + "/controller/servlet/Controller?EISESSION="+sessionId+"&CID=updateSavedSearch&selectvalue=unmark&searchid="+searchId+"&option="+option+"&databaseid="+URLEncoder.encode(databaseId);
					redirectURL = "/engresources/images/saved_blue.gif";
				}
			}
			else if(option != null && option.trim().equals("SavedSearch") )
			{

				urlString = "http://" + controllerServer + "/controller/servlet/Controller?EISESSION="+sessionId+"&CID=addDeleteSavedSearch&selectvalue=mark&searchid="+searchId+"&option="+option+"&databaseid="+URLEncoder.encode(databaseId);
				redirectURL = "/engresources/images/saved_blue.gif";
			}
		}
		else if(selectValue.trim().equals("unmark"))
		{

			if(option != null && option.trim().equals("EmailAlert") )
			{
				//to be done
			}
			else
			{
				urlString = "http://" + controllerServer + "/controller/servlet/Controller?EISESSION="+sessionId+"&CID=addDeleteSavedSearch&selectvalue=unmark&searchid="+searchId+"&option="+option+"&databaseid="+URLEncoder.encode(databaseId);
				redirectURL = "/engresources/images/save_blue.gif";
			}
		}
		else if(selectValue.trim().equals("marksearches"))
		{
			if(emailOption!=null && emailOption.equals("emailon"))
			{
				urlString = "http://" + controllerServer + "/controller/servlet/Controller?EISESSION="+sessionId+"&CID=updateSavedSearch&selectvalue=mark&searchid="+searchId+"&option=EmailAlertSearch";
				redirectURL = "/engresources/images/spacer.gif";
			}
			else if(emailOption!=null && emailOption.equals("emailoff"))
			{
				urlString = "http://" + controllerServer + "/controller/servlet/Controller?EISESSION="+sessionId+"&CID=updateSavedSearch&selectvalue=unmark&searchid="+searchId+"&option=EmailAlertSearch";
				redirectURL = "/engresources/images/spacer.gif";
			}
		}


        url = new URL(urlString);
        con = (HttpURLConnection) url.openConnection();
        con.connect();
        con.getResponseMessage();
        
        response.setContentType("image/gif");
        response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
        response.setHeader("Pragma","no-cache"); //HTTP 1.0
        response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
    } catch (Exception e) {
	    //e.printStackTrace();
    }

	if( redirectURL != null)
	{
	    response.sendRedirect(redirectURL);
    }
%>


