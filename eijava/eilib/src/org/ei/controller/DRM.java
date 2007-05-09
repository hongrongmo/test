package org.ei.controller;

import java.io.*;
import java.text.*;
import java.util.*;

import javax.servlet.*;
import javax.servlet.http.*;
import java.net.*;

import org.ei.controller.content.*;
import org.ei.controller.logging.*;
import org.ei.session.*;
import org.ei.util.GUID;
import org.ei.logging.LogClient;
import org.apache.oro.text.perl.Perl5Util;
import org.apache.oro.text.regex.MatchResult;

public class DRM extends HttpServlet {
    private SessionCache sCache;
    String logServiceURL = null;
    private LogClient logClient;
    String appName = null;

    public void init() throws ServletException {
        ServletConfig config = getServletConfig();
        String authURL = config.getInitParameter("authURL");
        appName = config.getInitParameter("appName");
        sCache = SessionCache.getInstance(authURL, appName);
        logServiceURL = config.getInitParameter("logURL");

//        System.out.println("Starting DRM...");
    }
    public void service(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        try {

            if ((logServiceURL != null) && (appName != null)) {
                logClient = new LogClient(logServiceURL);
            }

            String requestType = request.getParameter("Request");
            String sessionID = request.getParameter("Session");
//            System.out.println("Request=" + requestType);
//            System.out.println("SessionID=" + sessionID);

            String serviceID = request.getParameter("ServiceID");
            String docUID = request.getParameter("DocumentID");
//            System.out.println("DocID=" + docUID);
            String ident3 = request.getParameter("Ident3ID");
            String ident4 = request.getParameter("Ident4ID");
            String docIsLocal = request.getParameter("DocIsLocal");
            String offlineExpire = request.getParameter("OfflineFail");

            if (offlineExpire == null)
                offlineExpire = "";

//            System.out.println("OfflineExpire=" + offlineExpire);
//            System.out.println("DocIsLocal=" + docIsLocal);

            String username = null;
            String password = null;
            Writer out = response.getWriter();
            String referrerURL = request.getHeader("Referer");
			String ipaddress = request.getHeader("x-forwarded-for");
			if(ipaddress == null)
			{
				ipaddress = request.getRemoteAddr();
			}

            if (requestType != null) {

                if (requestType.equalsIgnoreCase("DocPerm")) {

                    if (docUID.equalsIgnoreCase("install")) {
                        out.write("RetVal=1");
                        out.write("&");
                        out.write("Ident3ID=");
                        out.write(ident3);
                        out.write("&");
                        out.write("Ident4ID=");
                        out.write(ident4);
                        out.write("&");
                        out.write("ServId=");
                        out.write(serviceID);
                        out.write("&");
                        out.write("DocuId=");
                        out.write(docUID);
                        out.write("&Perms=1");
                        out.write("&Code=BC7412231450FB61EAD8F97E6CC5C9FE");
                        return;

                    }
                    SessionID sesID = null;

                    if (sessionID != null && !sessionID.equalsIgnoreCase("none")) {
                        sesID = new SessionID(sessionID);
                    }

                    User user = getUser(sesID, ipaddress, referrerURL, username, password);

                    String cartridges = user.getCartridgeString().toLowerCase();
//                    System.out.println(" DocPerm Cartridges=" + cartridges);

                    if (cartridges.indexOf(serviceID.toLowerCase()) > -1 && docIsLocal.equalsIgnoreCase("no")) {

                        out.write("RetVal=2");
                        out.write("&");
                        out.write("Ident3ID=");
                        out.write(ident3);
                        out.write("&");
                        out.write("Ident4ID=");
                        out.write(ident4);
                        out.write("&");
                        out.write("ServId=");
                        out.write(serviceID);
                        out.write("&");
                        out.write("DocuId=");
                        out.write(docUID);
                        out.write("&Perms=85");
                        out.write("&OfflineExpire=30days");
                        out.write("&Code=BC7412231450FB61EAD8F97E6CC5C9FE");

                        Hashtable logProperties = new Hashtable();

                        String machine = request.getParameter("Machine");
                        String acroVer = request.getParameter("AcroVersion");
                        String acroProd = request.getParameter("AcroProduct");

                        if (sesID != null) {
                            logProperties.put("EISESSION", sesID.toString());
                        }
                        logProperties.put("referex", "docperm");
                        logProperties.put("isbn", docUID);
                        logProperties.put("custid", user.getCustomerID());
                        logProperties.put("usr", user.getUsername());
                        logProperties.put("machine", machine);
                        logProperties.put("acroVer", acroVer);
                        logProperties.put("acroProd", acroProd);

                        log(request, logProperties);

//                        System.out.println("Allowed");
                    }
                    else if (docIsLocal.equalsIgnoreCase("yes") && offlineExpire.equals("1")) {
                        out.write("RetVal=0&Error=Document has expired.  Please re-login to www.engineeringvillage2.org to view and re-download the e-book.");
//                        System.out.println("Document has expired");

                    }
                    else {
                        out.write("RetVal=0&Error=Document Access Denied");
//                        System.out.println("Denied");

                    }
                }
                else if (requestType.equalsIgnoreCase("PrintPerm")) {

                    SessionID sesID = null;

                    if (sessionID != null && !sessionID.equalsIgnoreCase("none")) {
                        sesID = new SessionID(sessionID);
                    }

                    User user = getUser(sesID, ipaddress, referrerURL, username, password);
                    String cartridges = user.getCartridgeString().toLowerCase();
                    String isbn = request.getParameter("DocumentID");
                    String machine = request.getParameter("Machine");
                    String acroVer = request.getParameter("AcroVersion");
                    String acroProd = request.getParameter("AcroProduct");
                    String copies = request.getParameter("Count");
                    String pageRanges = request.getParameter("PageRanges");
                    String printer = request.getParameter("Printer");

                    Hashtable logProperties = new Hashtable();

                    logProperties.put("referex", "printperm");
                    logProperties.put("isbn", isbn);
                    logProperties.put("machine", machine);
                    logProperties.put("acroVer", acroVer);
                    logProperties.put("acroProd", acroProd);
                    logProperties.put("copies", copies);
                    logProperties.put("pageRanges", pageRanges);
                    logProperties.put("totalPages", getTotalPages(pageRanges));
                    logProperties.put("printer", printer);

                    if (sesID != null) {
                        logProperties.put("EISESSION", sesID.toString());
                    }
                    logProperties.put("custid", user.getCustomerID());
                    logProperties.put("usr", user.getUsername());

//                    System.out.println("PrintPerm Cartridges=" + cartridges);
                    if (cartridges.indexOf(serviceID.toLowerCase()) > -1 && docIsLocal.toLowerCase().equals("no")) {
                        out.write("ServId=");
                        out.write(serviceID);
                        out.write("DocuId=");
                        out.write(docUID);
                        out.write("RetVal=1&Perms=1");
                        log(request, logProperties);
                    }
                    else {
                        out.write("RetVal=0&Error=Print Access Denied");
//                        System.out.println("Denied");
                    }

                }

            }

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    public User getUser(SessionID sesID, String ipaddress, String referrerURL, String username, String password) {

        UserSession us = null;
        User user = null;
        String cartridges = "";

        try {
            us = sCache.getUserSession(sesID, ipaddress, referrerURL, username, password, null);
            user = us.getUser();

        }
        catch (SessionException e) {
            e.printStackTrace();
        }

        return user;

    }
    public String getTotalPages(String pageRanges) {

        StringTokenizer tokens = new StringTokenizer(pageRanges, ",", false);

        int i = 1;

        String prevPage = "";
        int totalPages = 0;

        while (tokens.hasMoreTokens()) {

            String page = tokens.nextToken();

            if (i > 1) { //Ignore Number of Page Ranges

                if (i % 2 > 0) { //Reached outer limit

                    int iPage1 = Integer.parseInt(prevPage);
                    int iPage2 = Integer.parseInt(page);

                    int totPageRange = iPage2 - iPage1;

                    if (totPageRange == 0)
                        totPageRange = 1;
                    else
                        totPageRange += 1;

                    totalPages += totPageRange;

                }

                prevPage = new String(page);

            }
            ++i;
        }

        return String.valueOf(totalPages);
    }
    private void log(HttpServletRequest request, Hashtable props) {

        try {
            if (this.logClient != null) {

                long start = System.currentTimeMillis();
                this.logClient.reset();

                Enumeration paraNames = request.getParameterNames();
                Hashtable ht = new Hashtable();

                while (paraNames.hasMoreElements()) {
                    String key = (String) paraNames.nextElement();
                    ht.put(key, request.getParameter(key));
                }

                this.logClient.setdate(start);
                this.logClient.setbegin_time(start);

                if (ht.containsKey("EISESSION")) {
                    this.logClient.setsid((String) ht.get("EISESSION"));
                }
                else {
                    this.logClient.setsid("0");
                }

                Enumeration keys = props.keys();

                while (keys.hasMoreElements()) {
                    String key = (String) keys.nextElement();
                    String value = (String) props.get(key);
//                    System.out.println(key + "=" + value);
                }

				String ipAddress = request.getHeader("x-forwarded-for");
				if(ipAddress == null)
				{
					ipAddress = request.getRemoteAddr();
				}

                this.logClient.setHost(ipAddress);
                this.logClient.setrfc931("-");
                this.logClient.setusername("-");
                this.logClient.setcust_id((Long.valueOf((String) props.get("custid"))).longValue());
                this.logClient.setuser_agent(request.getHeader("User-Agent"));
                this.logClient.setHTTPmethod(request.getMethod());
                this.logClient.setreferrer(request.getHeader("referer"));
                this.logClient.seturi_stem(request.getRequestURI());
                this.logClient.seturi_query(request.getQueryString());
                this.logClient.setstatuscode(HttpServletResponse.SC_OK);
                this.logClient.setrid(new GUID().toString());
                this.logClient.setappid(appName);
                this.logClient.setappdata(ht);
                long end = System.currentTimeMillis();
                this.logClient.setend_time(end);
                this.logClient.setresponse_time(end - start);
                this.logClient.setappdata(props);

                this.logClient.sendit();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
