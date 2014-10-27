<!--
 * This page the follwing params as input and generates XML output.
 * @param java.lang.String.database
 */
-->
<%@ page language="java" %>
<%@ page session="false" %>

<!-- import statements of Java packages -->
<%@ page  import=" java.util.*"%>
<%@ page  import=" java.net.*"%>

<!--import statements of ei packages.-->
<%@ page  import="org.engvillage.biz.controller.ControllerClient"%>
<%@ page  import="org.engvillage.biz.controller.UserSession"%>
<%@ page  import="org.ei.query.base.*"%>
<%@ page  import="java.net.*"%>

<%@ page errorPage="/error/errorPage.jsp"%>


<%
   // This variable is used to hold ControllerClient instance
    ControllerClient client = new ControllerClient(request, response);
   //this variable for database name
    String dbName="";
    //this variable name for url
    String urlString=null;
    //this variable for sessionid.
    String sessionId=null;
 	StringBuffer buff=new StringBuffer();
 	 String searchType=null;


/**
 *  Getting the UserSession object from the Controller client .
 *  Getting the session id from the usersession.
 *
 */
  UserSession ussession=(UserSession)client.getUserSession();




 Enumeration anenum=request.getParameterNames();
//till the end of the enumeration.
	while(anenum.hasMoreElements()){
		String key=(String)anenum.nextElement();
    	//if key is not CID.
    	if((!key.equals("CID")) && (!key.equals("searchType"))){
	 		buff.append(key.trim()+"="+URLEncoder.encode(request.getParameter(key).trim())+"&");
    	 }
	}
if(request.getParameter("searchType")!=null) {
	searchType = request.getParameter("searchType").trim();
}


if( (searchType!=null) && (searchType.equals("expert")) )
{
	 urlString = "/controller/servlet/Controller?CID=expertSearch&"+buff.toString();
}
else if ( (searchType!=null) && (searchType.equals("combined")) )
{
	 urlString = "/controller/servlet/Controller?CID=combinedSearch&"+buff.toString();
}
else
{
 	 urlString = "/controller/servlet/Controller?CID=quickSearch&"+buff.toString();
}


  client.setRedirectURL(urlString);
  client.setRemoteControl();
  return;
%>