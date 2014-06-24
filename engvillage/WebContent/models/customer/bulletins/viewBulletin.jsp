<%@page import="org.ei.exception.SystemErrorCodes"%>
<%@page import="org.ei.exception.SessionException"%>
<%@page import="org.ei.config.RuntimeProperties"%>
<%@page import="org.ei.config.EVProperties"%>
<%@ page language="java"%>
<%@ page session="false"%>
<%@ page import=" java.util.*"%>
<%@ page import=" java.net.*"%>
<%@ page import="java.io.*"%>
<%@ page import="org.ei.bulletins.*"%>
<%@ page import="org.ei.util.*"%>
<%@ page import="org.ei.logging.*"%>
<%@ page import="org.ei.session.*"%>
<%@ page import="org.ei.domain.personalization.*"%>
<%@ page import="org.apache.commons.httpclient.*"%>
<%@ page import="org.apache.commons.httpclient.methods.*"%>
<%@ page import="org.ei.controller.ControllerClient"%>

<%@ page errorPage="/error/errorPage.jsp"%>
<%!private SessionCache sCache = null;
    String bulletinServer;
    String bulletinFileLocation;
    String logURL;
    String appName;
    ControllerClient client;

    public void jspInit() {

        String authURL = EVProperties.getRuntimeProperty(RuntimeProperties.AUTH_URL);
        appName = "EnCompassLit";
        bulletinFileLocation = EVProperties.getRuntimeProperty(RuntimeProperties.BULLETIN_FILE_LOCATION);
        logURL = EVProperties.getRuntimeProperty(RuntimeProperties.LOG_URL);
        try {
            SessionCache.init(authURL, appName);
        } catch (Throwable t) {
            System.out.println("Unable to initialize SessionCache!  authURL = '" + authURL + ", appName='" + appName + "'");
        }
    }%>
<%
    sCache = SessionCache.getInstance();
    if (sCache == null) {
        throw new SessionException(SystemErrorCodes.UNKNOWN, "No SessionCache is available!");
    }
    client = new ControllerClient(request, response);
    javax.servlet.http.Cookie[] cookies = request.getCookies();
    String sessionID = null;
    SessionID sesID = null;
    LogClient logClient = new LogClient(logURL);
    Hashtable logProperties = new Hashtable();

    if (cookies != null) {
        for (int i = 0; i < cookies.length; ++i) {
            javax.servlet.http.Cookie cookie = cookies[i];
            if (cookie.getName().equals("EISESSION")) {
                sessionID = cookie.getValue();
                break;
            }
        }
    }

    if (sessionID == null) {
        sessionID = request.getParameter("EISESSION");
    }
    if (sessionID != null) {
        sesID = new SessionID(sessionID);
    }

    String referrerURL = request.getHeader("Referer");
    String ipaddress = request.getRemoteAddr();
    String username = null;
    String password = null;
    String entryToken = request.getParameter("SYSTEM_ENTRY_TOKEN");

    UserSession ussession = sCache.getUserSession(sesID, ipaddress, referrerURL, username, password, entryToken);

    String status = ussession.getStatus();
    if (status.equals(SessionStatus.NEW_HAD_EXPIRED)) {
        ussession = sCache.getUserSession(null, ipaddress, referrerURL, username, password, entryToken);
    }

    String pUserId = ussession.getUserIDFromSession();

    IEVWebUser user = ussession.getUser();

    String cartridges[] = user.getCartridge();
    StringBuffer buffCartridges = new StringBuffer();

    for (int i = 0; i < cartridges.length; i++) {

        buffCartridges.append(cartridges[i]);

        if (i > 0)
            buffCartridges.append(";");
    }
    String lcCartridges = buffCartridges.toString().toLowerCase();
    if (lcCartridges.indexOf("lit_htm") == -1 && lcCartridges.indexOf("lit_pdf") == -1 && lcCartridges.indexOf("pat_htm") == -1
        && lcCartridges.indexOf("pat_pdf") == -1) {
        throw new SecurityException("<DISPLAY>You do not have access to the bulletins.</DISPLAY>");
    }

    String id = request.getParameter("id");
    String cType = request.getParameter("cType");

    BulletinBuilder builder = new BulletinBuilder();

    BulletinLinkBuilder linkBuilder = new BulletinLinkBuilder();

    Bulletin bulletin = builder.buildBulletinDetail(id);

    logProperties.put("EISESSION", sesID.toString());
    logProperties.put("request", "viewBulletin");
    logProperties.put("dbname", bulletin.getDatabase());
    logProperties.put("category", bulletin.getCategory());
    logProperties.put("filename", bulletin.getFileName());
    logProperties.put("custid", user.getCustomerID());
    logProperties.put("ctype", cType);

    bulletin.setContentType(cType);
    StringBuffer link = new StringBuffer(linkBuilder.buildLink(bulletin));

    if (cType.equals("HTML")) {
        response.setContentType("text/html");
    } else if (cType.equals("PDF")) {
        response.setContentType("application/pdf");
    } else if (cType.equals("ZIP")) {
        response.setContentType("application/zip");
        StringBuffer bufHeader = new StringBuffer("attachments; ");
        bufHeader.append("filename=").append(bulletin.getFileName()).append(".zip");
        response.setHeader("Content-disposition", bufHeader.toString());
    } else if (cType.equals("SAVEHTML")) {
        response.setContentType("text/html");
        StringBuffer bufHeader = new StringBuffer("attachments; ");
        bufHeader.append("filename=").append(bulletin.getFileName()).append(".htm");
        response.setHeader("Content-disposition", bufHeader.toString());
    } else if (cType.equals("SAVEPDF")) {
        response.setContentType("application/pdf");
        StringBuffer bufHeader = new StringBuffer("attachments; ");
        bufHeader.append("filename=").append(bulletin.getFileName()).append(".pdf");
        response.setHeader("Content-disposition", bufHeader.toString());
    }

    BufferedInputStream bis = null;
    BufferedOutputStream bout = null;
    File bulletinFile = null;

    StringBuffer fullUrl = new StringBuffer();
    String fullLink = fullUrl.append(bulletinFileLocation).append("/").append(link).toString();

    bulletinFile = new File(fullLink);

    bis = new BufferedInputStream(new FileInputStream(bulletinFile));
    bout = new BufferedOutputStream(response.getOutputStream());

    byte[] buff = new byte[1024];
    int bytesRead;

    while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
        bout.write(buff, 0, bytesRead);
    }
    bis.close();
    bout.close();

    //Log it

    long start = System.currentTimeMillis();
    logClient.reset();
    logClient.setHost(request.getRemoteAddr());
    logClient.setrfc931("-");
    logClient.setusername("-");
    logClient.setcust_id((String) logProperties.get("custid"));
    logClient.setuser_agent(request.getHeader("User-Agent"));
    logClient.setHTTPmethod(request.getMethod());
    logClient.setreferrer(request.getHeader("referer"));
    logClient.seturi_stem(request.getRequestURI());
    logClient.seturi_query(request.getQueryString());
    logClient.setstatuscode(HttpServletResponse.SC_OK);
    logClient.setrid(new GUID().toString());
    logClient.setappid(appName);
    logClient.setappdata(logProperties);
    if (logProperties.containsKey("EISESSION")) {
        logClient.setsid((String) logProperties.get("EISESSION"));
    } else {
        logClient.setsid("0");
    }
    long end = System.currentTimeMillis();
    logClient.setend_time(end);
    logClient.setresponse_time(end - start);
    logClient.sendit();
%>