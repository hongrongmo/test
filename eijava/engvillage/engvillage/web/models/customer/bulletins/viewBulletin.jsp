<%@ page language="java" %>
<%@ page session="false"%>
<%@ page import=" java.util.*"%>
<%@ page import=" java.net.*"%>
<%@ page import="java.io.*"%>
<%@ page import="org.ei.bulletins.*"%>
<%@ page import="org.ei.util.*"%>
<%@ page import="org.ei.logging.*"%>
<%@ page import="org.ei.session.*"%>
<%@ page import="org.apache.commons.httpclient.*"%>
<%@ page import="org.apache.commons.httpclient.methods.*"%>

<%@ page errorPage="/error/errorPage.jsp"%>
<%!


    private SessionCache sCache = null;
    String bulletinServer;
    String logURL;
    String appName;
  	
  	public void jspInit()
  	{
    	try
    	{
      		ServletConfig config = getServletConfig();
            String authURL = getServletContext().getInitParameter("authURL");
            appName = "EnCompassWEB";
            bulletinServer = getServletContext().getInitParameter("bulletinServer");
            logURL = getServletContext().getInitParameter("logURL");
            sCache = SessionCache.getInstance(authURL);
            
     
    	}
    	catch(Exception e)
    	{
       		e.printStackTrace();
    	}
  	}
 %>
<%
		
         
        javax.servlet.http.Cookie [] cookies = request.getCookies();
        String sessionID = null;
        SessionID sesID = null;
        LogClient logClient = new LogClient(logURL);
        Hashtable logProperties = new Hashtable();
        
        if(cookies != null)
        {
            for(int i=0; i<cookies.length;++i)
            {
                javax.servlet.http.Cookie cookie = cookies[i];
                if(cookie.getName().equals("EISESSION"))
                {
                    sessionID = cookie.getValue();
                    break;
                }
            }
        }

        if(sessionID == null)
        {
            sessionID = request.getParameter("EISESSION");
        }
        if(sessionID != null)
        {
            sesID = new SessionID(sessionID);
        }
        
        
        String referrerURL = request.getHeader("Referer");
        String ipaddress = request.getRemoteAddr();
        String username = null;
        String password = null;
        
        UserSession us = sCache.getUserSession(sesID, ipaddress, referrerURL, username, password);
        User user = us.getUser();
        String cartridges[] = user.getCartridge();
        StringBuffer buffCartridges = new StringBuffer();
        
        for (int i = 0; i < cartridges.length; i++) {
        
        	buffCartridges.append(cartridges[i]);
        	
        	if(i > 0)
        		buffCartridges.append(";");
        }
        String lcCartridges = buffCartridges.toString().toLowerCase();
        if (lcCartridges.indexOf("lit_htm") == -1 && lcCartridges.indexOf("lit_pdf") == -1
        && lcCartridges.indexOf("pat_htm") == -1 && lcCartridges.indexOf("pat_pdf") == -1 ) {
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
        logProperties.put("category",bulletin.getCategory());
        logProperties.put("filename", bulletin.getFileName());
        logProperties.put("custid", user.getCustomerID());
        logProperties.put("ctype",cType);
        
        bulletin.setContentType(cType);
        StringBuffer link = new StringBuffer(linkBuilder.buildLink(bulletin));
        
		if(cType.equals("HTML")){
			response.setContentType("text/html");
        }
		else if (cType.equals("PDF")){
			response.setContentType("application/pdf");
        }
		else if (cType.equals("ZIP")){
			response.setContentType("application/zip");
			StringBuffer bufHeader = new StringBuffer("attachments; ");
			bufHeader.append("filename=").append(bulletin.getFileName()).append(".zip");
            response.setHeader("Content-disposition", bufHeader.toString());
		} 
		else if(cType.equals("SAVEHTML")){
			response.setContentType("text/html");
			StringBuffer bufHeader = new StringBuffer("attachments; ");
			bufHeader.append("filename=").append(bulletin.getFileName()).append(".htm");
            response.setHeader("Content-disposition", bufHeader.toString());
        }
		else if (cType.equals("SAVEPDF")){
			response.setContentType("application/pdf");
			StringBuffer bufHeader = new StringBuffer("attachments; ");
			bufHeader.append("filename=").append(bulletin.getFileName()).append(".pdf");
            response.setHeader("Content-disposition", bufHeader.toString());
        }
		
		
		
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        File bulletinFile = null;
        
      
        try {
          
            
			StringBuffer fullUrl = new StringBuffer();
			
			fullUrl.append("http://").append(bulletinServer).append(link);
			
			//Get file from local file system not view httppost
          //  HttpClient client = new HttpClient();
            
          //  HttpMethod method = new GetMethod(fullUrl.toString());
            
         //   int statusCode = client.executeMethod(method);
         
            bulletinFile = new File(link.toString());
            
            bis = new BufferedInputStream(new FileInputStream(bulletinFile));
            bos = new BufferedOutputStream(response.getOutputStream());

            byte[] buff = new byte[1024];
            int bytesRead;

            while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
                bos.write(buff, 0, bytesRead);
            }
       
        } catch (Exception e) {
           e.printStackTrace();
        } finally {
            if (bis != null)
                bis.close();
            if (bos != null)
                bos.close();
        }
        //Log it
        try{
                long start = System.currentTimeMillis();
          		logClient.reset();
         	    logClient.setHost(request.getRemoteAddr());
                logClient.setrfc931("-");
                logClient.setusername("-");
                logClient.setcust_id((Long.valueOf((String)logProperties.get("custid"))).longValue());
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
        } catch(Exception ex){
        ex.printStackTrace();
        }
%>