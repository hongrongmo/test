<%@ page session="false" %>
<%@ page import="org.ei.controller.*" %>
<%@ page import="org.ei.session.*" %>
<%@ page import="org.ei.util.StringUtil"%>
<%@ page import="org.ei.domain.*" %>
<%@ page import="org.ei.domain.personalization.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*" %>

<%@ page errorPage="/error/errorPage.jsp"%>

<%
    ControllerClient client = new ControllerClient(request, response);
    UserSession ussession=(UserSession)client.getUserSession();
    User user = ussession.getUser();
    SessionID sessionIdObj = ussession.getSessionID();
    String strGlobalLinksXML = GlobalLinks.toXML(user.getCartridge());
    String customizedLogo="";
    ClientCustomizer clientCustomizer = new ClientCustomizer(ussession);
    String pageSize = "10";
    String catCode = request.getParameter("catCode");
    String sUserId = "";
    boolean personalization = false;
    // jam - this was a bug call directly to user.getDefaultDB()
    // doesn't set the default DB if the user has none selected in BO - We need to pick one from 
    // the ones they do have available - ClientCustomizer.getDefaultDB() does this
    // later on we assumed CPX - but that was incorrect too since not all users have CPX
    String db = clientCustomizer.getDefaultDB();

    if(db.equals("-"))
    {
        db  = "1";
    }

    if(catCode == null)
    {
        catCode = "DEFAULT_VIEW";
    }
    log(client);

    sUserId = ussession.getProperty("P_USER_ID");

    if((sUserId != null) && (sUserId.trim().length() != 0))
    {
        personalization=true;
    }

    customizedLogo = clientCustomizer.getLogo();

%>
<%! void log(ControllerClient client)
    {
        client.log("referex", "quickSearchForm");
        client.setRemoteControl();
    }
%>
<DOC>
<HEADER/>
<FOOTER/>
<BCD><%=catCode%></BCD>
<CUSTOMIZED-LOGO><%=customizedLogo%></CUSTOMIZED-LOGO>
<PERSONALIZATION><%=personalization%></PERSONALIZATION>
<PCO><%=pageSize%></PCO>
<%=strGlobalLinksXML %>
<DBMASK><%=db%></DBMASK>
<SESSION-ID><%=sessionIdObj.toString()%></SESSION-ID>

<%@ include file="../database.jsp"%>

</DOC>