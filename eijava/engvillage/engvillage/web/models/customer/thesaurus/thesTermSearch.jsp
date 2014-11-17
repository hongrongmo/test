<%@ page session="false" %>
<%@ page import="org.ei.thesaurus.*" %>
<%@ page import="org.ei.domain.*" %>
<%@ page import="org.ei.domain.personalization.*" %>
<%@ page import="org.ei.controller.*" %>
<%@ page import="org.ei.session.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.lang.*" %>
<%@ page import="java.io.*" %>

<%@ page errorPage="/error/errorPage.jsp"%>

<%


    DatabaseConfig databaseConfig = DatabaseConfig.getInstance();
    boolean isPersonalizationPresent=true;
    boolean isFullTextPresent=true;
    boolean isEmailAlertsPresent=true;
    int customizedStartYear=1970;
    int customizedEndYear=1970;
    String customizedLogo="";
    String pUserId = "";
    boolean personalization = false;


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
    else if(dbName.equals("2"))
    {
        databaseID = "ins";
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

    String pageNumber = request.getParameter("pageNumber");
    if(pageNumber == null && term != null)
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
        tpath.addStep(ThesaurusAction.SEARCH, term, props);
        pageNumber = "1";
    }

    client.log("thesaurus", "search");
    client.setRemoteControl();

    out.print("<DOC>");
    out.write("<HEADER/>");
    out.write("<THESAURUS-HEADER/>");
    out.write("<SEARCH-TYPE>Thesaurus</SEARCH-TYPE>");
    out.write("<HAS-INSPEC>"+UserCredentials.hasCredentials(2, databaseConfig.getMask(user.getCartridge()))+"</HAS-INSPEC>");
    out.write("<HAS-CPX>"+UserCredentials.hasCredentials(1, databaseConfig.getMask(user.getCartridge()))+"</HAS-CPX>");
    out.write("<HAS-GEO>"+UserCredentials.hasCredentials(8192, databaseConfig.getMask(user.getCartridge()))+"</HAS-GEO>");
    out.write("<HAS-GRF>"+UserCredentials.hasCredentials(2097152, databaseConfig.getMask(user.getCartridge()))+"</HAS-GRF>");
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
    int pageSize = 10;

    if(term != null && term.length() > 0)
    {

        int offSet = ((Integer.parseInt(pageNumber)-1) * 10) +1;
        ThesaurusStep thesStep = tpath.getFirstStep();


        DatabaseConfig dConfig = DatabaseConfig.getInstance();
        Database database = dConfig.getDatabase(databaseID);
        ThesaurusQuery query = new ThesaurusQuery(database, term);
        query.compile();
        ThesaurusSearchControl sc = new ThesaurusSearchControl();
        ThesaurusSearchResult sr = sc.search(query, pageSize);
        ThesaurusPage tpage = sr.pageAt(offSet);
        int count = sr.getHitCount();

        ThesSearchNavigator searchNavigator =
            new ThesSearchNavigator(pageSize,
                        count,
                        Integer.parseInt(pageNumber));

        if(count > 0)
        {
            out.print("<RESULT>Y</RESULT>");
            out.print("<ACTION>");
            out.print(thesStep.getContext());
            out.print("</ACTION>");

            out.print("<TERM><![CDATA[");
            out.print(thesStep.getTitle());
            out.print("]]></TERM>");

            out.print("<COUNT>"+String.valueOf(sr.getHitCount())+"</COUNT>");
            tpath.toXML(out);
            out.print("<SEARCH>");
            tpage.accept(new MultiRecordXMLVisitor(out));
            out.print("</SEARCH>");
            searchNavigator.toXML(out);
        }
        else
        {
            ThesaurusRecordBroker broker = new ThesaurusRecordBroker(database);
            ThesaurusPage spage = broker.getSuggestions(term.toLowerCase(),pageSize);
            if(spage.size() > 0)
            {

                out.print("<RESULT>N</RESULT>");
                out.print("<ACTION>");
                out.print(thesStep.getContext());
                out.print("</ACTION>");

                out.print("<TERM><![CDATA[");
                out.print(thesStep.getTitle());
                out.print("]]></TERM>");

                tpath.toXML(out);
                out.print("<SUGGEST>");
                spage.accept(new MultiRecordXMLVisitor(out));
                out.print("</SUGGEST>");
                searchNavigator.toXML(out);


            }
            else
            {

                out.print("<RESULT>N</RESULT>");
                out.print("<ACTION>");
                out.print(thesStep.getContext());
                out.print("</ACTION>");

                out.print("<TERM><![CDATA[");
                out.print(thesStep.getTitle());
                out.print("]]></TERM>");

                tpath.toXML(out);
                out.print("<NOSUGGEST>");
                //spage.accept(new MultiRecordXMLVisitor(out));
                //searchNavigator.toXML(out);
                out.print("</NOSUGGEST>");

            }
        }

    }
    else
    {
        out.print("<RESULT>Y</RESULT>");
        out.print("<ACTION>thesTermSearch</ACTION>");
    }

    out.print("</DOC>");
%>
