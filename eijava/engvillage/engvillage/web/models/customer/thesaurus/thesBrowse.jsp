<%@ page session="false" %>

<%@ page import="org.ei.thesaurus.*" %>
<%@ page import="org.ei.domain.*" %>
<%@ page import="org.ei.domain.personalization.*" %>
<%@ page import="org.ei.controller.*" %>
<%@ page import="org.ei.session.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.io.*" %>
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
    User user = ussession.getUser();


    String customerId = user.getCustomerID().trim();

    String sessionID = ussession.getID();
    SessionID sessionIdObj = ussession.getSessionID();

    ClientCustomizer clientCustomizer = new ClientCustomizer(ussession);
    String strGlobalLinksXML = GlobalLinks.toXML(user.getCartridge());


    String term = request.getParameter("term");
    String index = request.getParameter("index");
    String dbName = request.getParameter("database");
    String databaseID = null;

    pUserId = ussession.getProperty("P_USER_ID");
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
    else
    {
        databaseID = "ins";
    }


    ThesaurusPath tpath = new ThesaurusPath(sessionID);

    if(term == null)
    {
        term = "";
    }

    if(index == null)
    {
        index = "";
    }


    long begin = System.currentTimeMillis();
    Database database = (DatabaseConfig.getInstance()).getDatabase(databaseID);
    ThesaurusRecordBroker broker = new ThesaurusRecordBroker(database);

    client.log("thesaurus", "browse");
    client.setRemoteControl();

    out.print("<DOC>");
    out.write("<HEADER/>");
    out.write("<THESAURUS-HEADER/>");
    out.write("<SEARCH-TYPE>Thesaurus</SEARCH-TYPE>");
    out.write("<HAS-INSPEC>"+UserCredentials.hasCredentials(2, databaseConfig.getMask(user.getCartridge()))+"</HAS-INSPEC>");
    out.write("<HAS-CPX>"+UserCredentials.hasCredentials(1, databaseConfig.getMask(user.getCartridge()))+"</HAS-CPX>");
    out.write(strGlobalLinksXML);
    out.write("<FOOTER/>");
    out.write("<SESSION-ID>"+sessionIdObj.toString()+"</SESSION-ID>");
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




    if(term.length() > 0 && index.length() == 0)
    {

        Properties props = new Properties();
        Enumeration anenum = request.getParameterNames();
        while(anenum.hasMoreElements())
        {
            String key = (String) anenum.nextElement();
            String value = request.getParameter(key);
            props.setProperty(key, value);
        }


        tpath.clearPathFrom(0);
        tpath.addStep(ThesaurusAction.BROWSE, term, props);
        ThesaurusStep thesStep = tpath.getFirstStep();

        ThesaurusRecordID recID = new ThesaurusRecordID(term, database);
        ThesaurusRecordID[] recIDs = new ThesaurusRecordID[1];
        recIDs[0] = recID;
        ThesaurusPage tpage = broker.buildPage(recIDs, 0, 0);

        if(tpage.get(0) == null)
        {
            tpage = broker.getAlphaStartingPoint(term.toLowerCase());
        }


        if(tpage.get(0) != null)
        {
            ThesaurusRecord trec = tpage.get(0);
            int recNumber = trec.getRecID().getRecordID();
            int startBrowse = 0;
            if(recNumber > 2)
            {
                startBrowse = recNumber - 3;
            }
            else
            {
                startBrowse = 0;
            }

            ThesaurusRecordID[] ids = new ThesaurusRecordID[10];

            for(int i = 0; i<10; i++)
            {
                ids[i] = new ThesaurusRecordID(startBrowse, database);
                startBrowse++;
            }

            ThesaurusPage bpage = broker.buildPage(ids, 0, 9);

            out.print("<ACTION>");
            out.print(thesStep.getContext());
            out.print("</ACTION>");
            out.print("<RESULT>Y</RESULT>");

            out.print("<DATA>");
            tpath.toXML(out);
            out.print("<TERM><![CDATA[");


            out.print(thesStep.getTitle());
            out.print("]]></TERM>");

            int previousPage = 0;

            if((startBrowse-10) == 0)
            {
                previousPage = -1;
            }
            else
            {
                previousPage = startBrowse-20;
                if(previousPage < 0)
                {
                    previousPage = 0;
                }
            }


            out.print("<PINDEX>"+Integer.toString(previousPage)+"</PINDEX><NINDEX>"+Integer.toString(startBrowse)+"</NINDEX>");
            out.print("<BROWSE>");
            bpage.accept(new MultiRecordXMLVisitor(out));
            out.print("</BROWSE>");
            out.print("</DATA>");
        }
        else
        {
            out.print("<RESULT>N</RESULT>");
            out.print("<ACTION>");
            out.print(thesStep.getContext());
            out.print("</ACTION>");

            out.print("<DATA>");
            tpath.toXML(out);

            out.print("<TERM><![CDATA[");
            out.print(thesStep.getTitle());
            out.print("]]></TERM>");

            out.print("<NOSUGGEST/>");
            out.print("</DATA>");

        }

    }
    else if(index.length() > 0 && term.length() > 0)
    {

        ThesaurusStep thesStep = tpath.getFirstStep();

        ThesaurusRecordID[] ids = new ThesaurusRecordID[10];

        int startBrowse = Integer.parseInt(index);

        for(int i=0; i<10; i++)
        {
            ids[i] = new ThesaurusRecordID(startBrowse, database);
            startBrowse++;
        }

        ThesaurusPage bpage = broker.buildPage(ids, 0, 9);
        out.print("<RESULT>Y</RESULT>");
        out.print("<ACTION>");
        out.print(thesStep.getContext());
        out.print("</ACTION>");

        out.print("<DATA>");
        tpath.toXML(out);

        out.print("<TERM><![CDATA[");
        out.print(thesStep.getTitle());
        out.print("]]></TERM>");

        int previousPage = 0;

        if((startBrowse - 10) == 0)
        {
            previousPage = -1;
        }
        else
        {
            previousPage = startBrowse - 20;
            if(previousPage < 0)
            {
                previousPage = 0;
            }
        }

        out.print("<PINDEX>"+Integer.toString(previousPage)+"</PINDEX><NINDEX>"+Integer.toString(startBrowse)+"</NINDEX>");
        out.print("<BROWSE>");
        bpage.accept(new MultiRecordXMLVisitor(out));
        out.print("</BROWSE>");
        out.print("</DATA>");
    }
    else
    {
        out.print("<ACTION>thesBrowse</ACTION>");
    }

    out.print("</DOC>");


    long end = System.currentTimeMillis();

%>
