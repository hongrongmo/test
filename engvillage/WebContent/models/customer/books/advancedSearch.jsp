<%@ page session="false" %>
<%@ page import="org.ei.domain.*" %>
<%@ page import="org.ei.domain.personalization.*" %>
<%@ page import="org.ei.controller.*" %>
<%@ page import="org.ei.session.*" %>
<%@ page import="org.ei.util.StringUtil"%>

<%@ page import="java.util.*" %>
<%@ page import="java.lang.*" %>
<%@ page import="java.io.*" %>
<%@ page errorPage="/error/errorPage.jsp"%>
<%


    ControllerClient client = new ControllerClient(request, response);
    UserSession ussession=(UserSession)client.getUserSession();
    IEVWebUser user = ussession.getUser();
    SessionID sessionIdObj = ussession.getSessionID();
    ClientCustomizer clientCustomizer = new ClientCustomizer(ussession);
    String strGlobalLinksXML = GlobalLinks.toXML(user.getCartridge());
    String sUserId = "";
    String pageSize = "10";
    String catCode = request.getParameter("catCode");
    boolean personalization = false;
    String customizedLogo = null;
    String db = clientCustomizer.getDefaultDB();

    if(db.equals("-"))
    {
        db  = "1";
    }


    if(catCode == null)
        catCode = "DEFAULT_VIEW";

    sUserId = ussession.getUserIDFromSession();
    customizedLogo = clientCustomizer.getLogo();

    if((sUserId != null) && (sUserId.trim().length() != 0))
    {
        personalization=true;
    }
    log(client);

%>
<%! void log(ControllerClient client)
    {
        client.log("referex", "advancedSearchForm");
        client.setRemoteControl();
    }
%>
<DOC>
<HEADER/>
<FOOTER/>
<BCD><%=catCode%></BCD>
<CUSTOMIZED-LOGO><%=customizedLogo%></CUSTOMIZED-LOGO>
<DBMASK><%=db%></DBMASK>
<PERSONALIZATION><%=personalization%></PERSONALIZATION>
<PCO><%=pageSize%></PCO>
<%=strGlobalLinksXML %>
<DATABASE><%=db%></DATABASE>
<SESSION-ID><%=sessionIdObj.toString()%></SESSION-ID>

<%@ include file="../database.jsp"%>

</DOC>