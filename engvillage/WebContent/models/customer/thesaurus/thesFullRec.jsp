<%@ page session="false" %>
<%@ page import="org.ei.thesaurus.*" %>
<%@ page import="org.ei.domain.*" %>
<%@ page import="org.ei.domain.personalization.*" %>
<%@ page import="org.ei.controller.*" %>
<%@ page import="org.engvillage.biz.controller.UserSession" %>
<%@ page import="java.util.*" %>
<%@ page import="java.net.*"%>
<%@ page import="java.io.*" %>


<%@ page import ="org.engvillage.biz.controller.ControllerClient"%>
<%@ page errorPage="/error/errorPage.jsp"%>
<%


    boolean isPersonalizationPresent=true;
    boolean isFullTextPresent=true;
    boolean isEmailAlertsPresent=true;
    int customizedStartYear=1970;
    int customizedEndYear=1970;
    String customizedLogo="";
    String pUserId = "";
    boolean personalization = false;
    DatabaseConfig databaseConfig = DatabaseConfig.getInstance();

    ControllerClient client = new ControllerClient(request, response);
    UserSession ussession=(UserSession)client.getUserSession();
    IEVWebUser user = ussession.getUser();


    String customerId = ussession.getCustomerid().trim();

    String sessionID = ussession.getSessionid();
    SessionID sessionIdObj = ussession.getSessionID();

    ClientCustomizer clientCustomizer = new ClientCustomizer(ussession);
    String strGlobalLinksXML = GlobalLinks.toXML(ussession.getCartridge());


    String term = request.getParameter("term");
    String recordID = request.getParameter("tid");
    String index = request.getParameter("index");
    String dbName = request.getParameter("database");
    String databaseID = null;

    pUserId = ussession.getUserid();
    if((pUserId != null) && (pUserId.trim().length() != 0))
    {
        personalization=true;
    }


    if(clientCustomizer.isCustomized())
    {
        isPersonalizationPresent=clientCustomizer.checkPersonalization();
        isFullTextPresent=clientCustomizer.checkFullText();
        isEmailAlertsPresent=clientCustomizer.checkEmailAlert();
        customizedLogo=clientCustomizer.getLogo();
    }

    if(dbName.equals("1"))
    {
        databaseID = "cpx";

        if(clientCustomizer.getStartYear()!=-1)
        {
            customizedStartYear=clientCustomizer.getStartYear();

        }
        if(clientCustomizer.getEndYear()!=-1)
        {
            customizedEndYear=clientCustomizer.getEndYear();
        }
    }
    else if(dbName.equals("2"))
    {
        databaseID = "ins";
    }
    else if(dbName.equals("2048"))
    {
        databaseID = "ept";
    }
    else if(dbName.equals("1024"))
    {
         databaseID = "elt";
    }
    else if(dbName.equals("8192"))
    {
        databaseID = "geo";
    }    
    else if(dbName.equals("2097152"))
    {
        databaseID = "grf";
    }


    ThesaurusPath tpath = new ThesaurusPath(sessionID);
    String snumParam = request.getParameter("snum");


    int snum = -1;

    if(snumParam != null)
    {
        snum = Integer.parseInt(snumParam);
    }



    if(term == null)
    {
        term = "";
    }


    Properties props = new Properties();
    Enumeration anenum = request.getParameterNames();
    while(anenum.hasMoreElements())
    {
        String key = (String) anenum.nextElement();
        String value = request.getParameter(key);
        props.setProperty(key, value);
    }
    props.remove("formSubmit");


    if(request.getParameter("formSubmit") == null)
    {

        if(snum == -1 ||
           !tpath.hasIndex(snum))
        {
            tpath.addStep(ThesaurusAction.FULL, term, props);
        }
        else
        {
            tpath.clearPathFrom((snum+1));
        }
    }
    else
    {
        tpath.clearPathFrom(0);
        tpath.addStep(ThesaurusAction.FULL, term, props);

    }

    ThesaurusStep thesStep = tpath.getFirstStep();

    
    if(term.indexOf("*")>0)
    {
    	term = term.replaceAll("\\*$","");
    }
    
    if(term.length() > 0)
    {
        client.log("thesaurus", "fullrec");
        client.log("term", term);
        client.setRemoteControl();

        Database database = (DatabaseConfig.getInstance()).getDatabase(databaseID);
        ThesaurusRecordBroker broker = new ThesaurusRecordBroker(database);
        ThesaurusRecordID recID=null;
        if(recordID != null && recordID.length()>0)
        {
        	recID = new ThesaurusRecordID(Integer.parseInt(recordID), term, database);
        }
        else
        {
        	recID = new ThesaurusRecordID(term, database);
        }
        ThesaurusRecordID[] recIDs = new ThesaurusRecordID[1];
        recIDs[0] = recID;
        ThesaurusPage tpage = broker.buildPage(recIDs, 0, 0);

        out.print("<DOC>");
        out.write("<HEADER/>");
        out.write("<THESAURUS-HEADER/>");
        out.write("<SEARCH-TYPE>Thesaurus</SEARCH-TYPE>");
        out.write("<HAS-INSPEC>"+UserCredentials.hasCredentials(2, databaseConfig.getMask(ussession.getCartridge()))+"</HAS-INSPEC>");
        out.write("<HAS-CPX>"+UserCredentials.hasCredentials(1, databaseConfig.getMask(ussession.getCartridge()))+"</HAS-CPX>");
        out.write("<HAS-GEO>"+UserCredentials.hasCredentials(8192, databaseConfig.getMask(ussession.getCartridge()))+"</HAS-GEO>");
        out.write("<HAS-GRF>"+UserCredentials.hasCredentials(2097152, databaseConfig.getMask(ussession.getCartridge()))+"</HAS-GRF>");
        out.write("<HAS-EPT>"+UserCredentials.hasCredentials(2048, databaseConfig.getMask(ussession.getCartridge()))+"</HAS-EPT>");
        out.write("<HAS-ELT>"+UserCredentials.hasCredentials(1024, databaseConfig.getMask(ussession.getCartridge()))+"</HAS-ELT>");
        out.write(strGlobalLinksXML);
        out.write("<FOOTER/>");
        out.write("<SESSION-ID>"+sessionId+"</SESSION-ID>");
        out.write("<CUSTOMIZED-LOGO>"+customizedLogo+"</CUSTOMIZED-LOGO>");
        out.write("<CUSTOMIZED-STARTYEAR>"+customizedStartYear+"</CUSTOMIZED-STARTYEAR>");
        out.write("<CUSTOMIZED-ENDYEAR>"+customizedEndYear+"</CUSTOMIZED-ENDYEAR>");
        out.write("<CUSTOMER-ID>"+customerId+"</CUSTOMER-ID>");
        out.write("<PERSONALIZATION-PRESENT>"+isPersonalizationPresent+"</PERSONALIZATION-PRESENT>");
        out.write("<FULLTEXT>"+isFullTextPresent+"</FULLTEXT>");
        out.write("<EMAILALERTS-PRESENT>"+isEmailAlertsPresent+"</EMAILALERTS-PRESENT>");
        out.write("<PERSONALIZATION>"+personalization+"</PERSONALIZATION>");
        out.write("<PERSON-USER-ID>"+pUserId+"</PERSON-USER-ID>");
        out.print("<DATABASE>");
        out.print(dbName);
        out.print("</DATABASE>");


        if(tpage.get(0) == null)
        {


            ThesaurusPage spage = broker.getSuggestions(term.toLowerCase(),10);
            if(spage.size() > 0)
            {
                out.print("<RESULT>N</RESULT>");
                tpath.toXML(out);
                out.print("<ACTION>");
                out.print(thesStep.getContext());
                out.print("</ACTION>");

                out.print("<TERM><![CDATA[");
                out.print(thesStep.getTitle());
                out.print("]]></TERM>");

                out.print("<DATA>");
                out.print("<NOHIT>");
                spage.accept(new MultiRecordXMLVisitor(out));
                out.print("</NOHIT>");
                out.print("</DATA>");

            }
            else
            {
                out.print("<RESULT>N</RESULT>");
                tpath.toXML(out);

                out.print("<ACTION>");
                out.print(thesStep.getContext());
                out.print("</ACTION>");

                out.print("<TERM><![CDATA[");
                out.print(thesStep.getTitle());
                out.print("]]></TERM>");

                out.print("<DATA>");
                out.print("<NOSUGGEST/>");
                out.print("</DATA>");

            }

        }
        else
        {
            
            for(int i=0;i<tpage.size();i++)
            {
		    //ThesaurusRecord trec = tpage.get(0);
		    ThesaurusRecord trec = tpage.get(i);

            tpath.toXML(out);
            out.print("<RESULT>Y</RESULT>");
            out.print("<ACTION>");
            out.print(thesStep.getContext());
            out.print("</ACTION>");

            out.print("<TERM><![CDATA[");
            out.print(thesStep.getTitle());
            out.print("]]></TERM>");

            out.print("<DATA>");
            out.print("<HIT>");
            trec.accept(new SingleRecordXMLVisitor(out));
            out.print("</HIT>");
            out.print("</DATA>");
		}

        }

        out.print("</DOC>");
       
    }

%>