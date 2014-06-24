package org.ei.fulldoc;


import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.ei.util.StringUtil;


public class OHUBService extends HttpServlet
{

    public static String ohuburl = "http://linkinghub.elsevier.com/servlets/OHXmlRequestXml";
    public static String salt = "E78C4EAF-4064-41C5-9FC2-E1C73DE9FBF5";
    public static String saltVersion = "1";
    public static String partnerID = "14";
    private StringUtil sUtil = new StringUtil();
    private String logServiceURL;
    private String appName;


    public void init()
        throws ServletException
    {
        ServletConfig config = getServletConfig();


        logServiceURL = config.getInitParameter("logURL");
        appName       = config.getInitParameter("appName");
    }

    public void destroy()
    {

    }

    private String withDash(String ISSN)
    {
        String newISSN = null;

        if(ISSN.indexOf("-") < 0)
        {
            String begin = ISSN.substring(0,4);
            String end = ISSN.substring(4, ISSN.length());
            newISSN = begin+"-"+end;
        }
        else
        {
            newISSN = ISSN;
        }

        return newISSN;

    }

    private String withoutDash(String ISSN)
    {
        return sUtil.replace(ISSN,
                     "-",
                     "",
					StringUtil.REPLACE_FIRST,
                     StringUtil.MATCH_CASE_INSENSITIVE);
    }


/*    public void service(HttpServletRequest request,
                        HttpServletResponse response)
        throws IOException,
                ServletException
    {

        String reason = request.getParameter("reason");
        String flipScript = "function imageFlip(){imageFlip1(); setTimeout(\"imageFlip1();\",2000);} function imageFlip1() {if (document.images['fullDocImageTop']) {document.images['fullDocImageTop'].src = \"/ohubservice/images/avtop.gif\";} if (document.images['fullDocImageB']) {document.images['fullDocImageB'].src = \"/ohubservice/images/av.gif\";}}";
        String blankImage = "function imageFlip(){}";
        String ohubIDType = request.getParameter("ohubIDType");


        try
        {




            long start = System.currentTimeMillis();
            int status = response.SC_OK;
            String Log_Response = "";
            String Log_FoundDoc = "true";
            String issn = null;
            String issue = null;
            String volume = null;
            String page = null;

            OHUBID id = null;
            OHUBID[] ids = null;
            IssueVolumeID is3 = null;

            if(ohubIDType.equals("ip"))
            {

                issn    = request.getParameter("ISSN");
                issue = request.getParameter("firstIssue");
                volume  = request.getParameter("firstVolume");
                page = request.getParameter("firstPage");


                if(issn == null || issn.length() == 0)
                {
                    issn = "123456789";
                }
                else
                {
                    issn = withoutDash(issn);
                }



                // TODO - handle no issue condition

                ids = new OHUBID[2];


                IssueVolumeID is = new IssueVolumeID();
                is.setISSN(issn);
                is.setFirstPage(page);
                is.setFirstVolume(volume);

                ids[0] = is;


                is3 = new IssueVolumeID();
                is3.setISSN(issn);
                is3.setFirstPage(page);
                is3.setFirstVolume(volume);
                is3.setFirstIssue(issue);
                ids[1] = is3;

            }
            else
            {
                ids = new OHUBID[1];
                AuthorRefID ar = new AuthorRefID();
                ar.setFirstAuthorSurname(request.getParameter("firstAuthorSurname"));
                ar.setYear(request.getParameter("year"));
                ar.setFirstPage(request.getParameter("firstPage"));
                ar.setLastPage(request.getParameter("lastPage"));
                ar.setFirstAuthorInitial(request.getParameter("firtAuthorInitial"));
                ids[0] = ar;

            }


            AlgoLinker algo = new AlgoLinker();



            LinkInfo linkInfo = algo.buildLink(is3);


            if(linkInfo != null )
            {

                if(reason.equals("check"))
                {
                    ServletOutputStream out = response.getOutputStream();
                    out.print(flipScript);
                    out.flush();
                }
                else
                {
                    //System.out.println(linkInfo.url);
                    response.sendRedirect(linkInfo.url);
                    status = response.SC_MOVED_TEMPORARILY;
                }

            }
            else
            {
                OHUBRequest orequest = null;
                OHUBResponses theResponses = null;
                orequest = new OHUBRequest(salt, saltVersion, partnerID, ids);
                OHUBClient client = new OHUBClient(this.ohuburl);
                theResponses = client.getOHUBResponses(orequest);

                if((theResponses.countResponses() == 2) &&
                   (theResponses.responseAt(1)).itemCount() == 1)
                {
                    System.out.println("With Issue");
                    if(reason.equals("check"))
                    {
                        ServletOutputStream out = response.getOutputStream();
                        out.print(flipScript);
                        out.flush();
                    }
                    else
                    {
                        response.sendRedirect(getRedirect(((theResponses.responseAt(1)).itemAt(0)).getURL()));
                        status = response.SC_MOVED_TEMPORARILY;
                    }
                }
                else if((theResponses.responseAt(0)).itemCount() == 1)
                {
                    System.out.println("Without Issue");
                    if(reason.equals("check"))
                    {
                        ServletOutputStream out = response.getOutputStream();
                        out.print(flipScript);
                        out.flush();
                    }
                    else
                    {
                        response.sendRedirect(getRedirect(((theResponses.responseAt(0)).itemAt(0)).getURL()));
                        status = response.SC_MOVED_TEMPORARILY;
                    }
                }
                else
                {
                    if(reason.equals("check"))
                    {
                        ServletOutputStream out = response.getOutputStream();
                        out.print(blankImage);
                        out.flush();
                        Log_FoundDoc = "false";
                    }
                    else
                    {
                        response.sendRedirect("/ohubservice/nodoc.html");
                        status = response.SC_MOVED_TEMPORARILY;
                    }
                }
            }


            if(reason.equals("get"))
            {

                System.out.println("Sending log..");
                LogClient logClient = new LogClient(logServiceURL);
                Enumeration paraNames = request.getParameterNames();
                Hashtable ht = new Hashtable();
                long end = System.currentTimeMillis();

                while (paraNames.hasMoreElements() )
                {
                        String key = (String) paraNames.nextElement();
                        ht.put( key, request.getParameter(key) );
                }

                // populate mandatory fields for CLF construction
                logClient.setdate(start);
                logClient.setbegin_time(start);
                logClient.setend_time(end);
                logClient.setresponse_time(end-start);
                if ( ht.containsKey("EISESSION") ) {
                    logClient.setsid( (String)ht.get("EISESSION") );
                } else {
                    logClient.setsid("0");
                }

                logClient.setHost(request.getRemoteAddr());
                logClient.setrfc931("-");
                logClient.setusername("-");
                logClient.setuser_agent(request.getHeader("User-Agent"));
                logClient.setHTTPmethod(request.getMethod());
                logClient.setreferrer(request.getHeader("referer"));
                logClient.seturi_stem(request.getRequestURI());
                logClient.seturi_query(request.getQueryString());
                logClient.setstatuscode(status);
                logClient.setrid( new GUID().toString());
                logClient.setappid(this.appName);
                logClient.setappdata(ht);

                try
                {
                    logClient.sendit();
                    System.out.println("Sent log ..");
                }
                catch(Exception le)
                {
                    le.printStackTrace();
                }
            }

        }
        catch(Exception e)
        {
            log("OHUBError", e);
            if(reason.equals("check"))
            {
                ServletOutputStream out = response.getOutputStream();
                out.print(blankImage);
                out.flush();
            }
            else
            {
                response.sendRedirect("/ohubservice/nodoc.html");
            }
        }
    } */


    private String getRedirect(String URL)
        throws Exception
    {
        /*
        StringTokenizer tokens1 = new StringTokenizer(URL, "://");
        String discard = tokens1.nextToken();
        String domain = tokens1.nextToken();
        String pubid = tokens1.nextToken();
        String PII = tokens1.nextToken();


        if(pubid.equals(SDGateway.elsevierID))
        {
            String sdLink =  SDGateway.getScienceDirectLink(PII);
            System.out.println("Spot2:"+URL);
            return sdLink;
        }
        */

        return URL;
    }
}
