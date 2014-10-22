<%@ page language="java" %>
<%@ page session="false" %>
<!--
 * This page the follwsing params as input and generates XML output.
 * @param java.lang.String.database
 * @param java.lang.String.totalDocCount
 * @param java.lang.String.sessionId
 * @param java.lang.String.searchID
 -->

<!-- import statements of Java packages-->
<%@ page import="java.util.*"%>
<%@ page import="org.ei.tags.*"%>
<%@ page import="org.engvillage.biz.controller.UserSession"%>
<%@ page import="org.ei.domain.personalization.*"%>
<%@ page import="org.engvillage.biz.controller.ControllerClient"%>
<%@ page import="org.ei.email.*"%>
<%@ page import="javax.mail.internet.*"%>

String initialpublictags=null;
String initialprivatetags=null;
String grouptags=null;
String initialgrouptags=null;
String publictags=null;
String privatetags=null
String[] initgrouptagsarray = null;
String[] grouptagsarray = null;
String[] groupidsarray = null;
String docId=null;

ControllerClient client = new ControllerClient(request, response);
UserSession ussession=(UserSession)client.getUserSession();
String sessionId=ussession.getSessionid();
SessionID sessionIdObj = ussession.getSessionID();
String pUserId = ussession.getUserid();

if(request.getParameter("publictags")!=null)
{
  publictags=request.getParameter("publictags").trim();
}
if(request.getParameter("privatetags")!=null)
{
  privatetags=request.getParameter("privatetags").trim();
}
if(request.getParameter("docid")!=null)
{
  docId=request.getParameter("docid").trim();
}
if(request.getParameter("initialpublictags")!=null)
{
  initialpublictags=request.getParameter("initialpublictags").trim();
}
if(request.getParameter("initialprivatetags")!=null)
{
  initialprivatetags=request.getParameter("initialprivatetags").trim();
}
if (request.getParameterValues("initialgrouptags") != null)
{
	initgrouptagsarray = request.getParameterValues("initialgrouptags");
}
if (request.getParameterValues("grouptags") != null)
{
	grouptagsarray = request.getParameterValues("grouptags");
}
if (request.getParameterValues("groupid") != null)
{
	groupidsarray = request.getParameterValues("groupid");
}

tagBrokerEdit = new TagBroker();
Integer mask = new Integer(did.getDatabase().getMask());
tagBrokerEdit.editTags(docId,
					   pUserId,
					   custid.intValue(),
					   mask.intValue(),
					   publictags,
					   privatetags,
					   initialpublictags,
					   initialprivatetags,
					   initgrouptagsarray,
					   grouptagsarray,
					   groupidsarray);

RequestDispatcher dispatcher = request.getRequestDispatcher("/models/customer/abstractDetailedRecord.jsp");
request.setAttribute("CONTROLLER_CLIENT", client);
dispatcher.forward(request, response);
