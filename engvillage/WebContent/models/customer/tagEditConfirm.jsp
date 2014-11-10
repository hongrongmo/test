<%@page import="org.engvillage.biz.controller.ClientCustomizer"%>
<%@ page language="java" %>
<%@ page session="false" %>
<%@ page errorPage="/error/errorPage.jsp"%>

<%@ page import="org.ei.domain.personalization.*"%>
<%@ page import="org.engvillage.biz.controller.ControllerClient"%>
<%@ page import="org.engvillage.biz.controller.UserSession"%>
<%@ page import="org.ei.domain.personalization.*"%>
<%@ page import="org.ei.domain.*"%>
<%@ page import="java.net.URLDecoder"%>
<%@ page import="java.net.URLEncoder"%>

<%
	boolean personalization = true;
	boolean isPersonalizationPresent=true;
	String database = request.getParameter("database");
	String tagsearch = request.getParameter("searchtags");
	String scope = request.getParameter("scope");

    ControllerClient client = new ControllerClient(request,response);
    UserSession ussession=(UserSession)client.getUserSession();
    String sSessionId = ussession.getSessionid();

    StringBuffer sb = new StringBuffer();
    sb = new StringBuffer("<PAGE>");
	sb.append("<DATABASE>").append(database).append("</DATABASE>");
	sb.append("<SEARCH-TAGS><![CDATA[").append(tagsearch).append("]]></SEARCH-TAGS>");
	sb.append("<SCOPE>").append(scope).append("</SCOPE>");
    sb.append("<HEADER/>");
    sb.append(GlobalLinks.toXML(ussession.getCartridge()));
    sb.append("<FOOTER/>");
    String customerId = ussession.getCustomerid();
    String strContractId = ussession.getContractid();
    ClientCustomizer clientCustomizer=new ClientCustomizer(ussession);
    if(clientCustomizer.isCustomized())
    {
        sb.append("<CUSTOMIZED-LOGO>").append(clientCustomizer.getLogo()).append("</CUSTOMIZED-LOGO>");
    }
    sb.append("<SESSION-ID>").append(sSessionId).append("</SESSION-ID>");
  	sb.append("<PERSONALIZATION-PRESENT>").append(isPersonalizationPresent).append("</PERSONALIZATION-PRESENT>");
	sb.append("<PERSONALIZATION>").append(personalization).append("</PERSONALIZATION>");
    sb.append("</PAGE>");
	out.println(sb.toString());
%>