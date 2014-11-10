<%@page import="org.ei.config.ApplicationProperties"%>
<%@page import="org.engvillage.biz.controller.ClientCustomizer"%>
<%@ page language="java" %>
<%@ page session="false" %>
<!-- import statements of Java packages -->
<%@ page import="java.net.URLEncoder"%>
<%@ page import="java.util.*"%>


<!--import statements of ei packages.-->
<%@ page import="org.engvillage.biz.controller.ControllerClient"%>
<%@ page import="org.engvillage.biz.controller.UserSession"%>
<%@ page import="org.ei.domain.personalization.*"%>
<%@ page import="org.ei.query.base.*"%>
<%@ page import="org.ei.domain.*"%>
<%@ page import="org.ei.util.GUID"%>
<%@ page import="org.ei.util.StringUtil"%>
<%@ page import="org.ei.config.*"%>
<%@ page import="org.ei.domain.personalization.GlobalLinks"%>
<%@ page import="org.ei.domain.Searches"%>
<%@ page import="org.ei.domain.personalization.SavedSearches"%>

<%@ page  errorPage="/error/errorPage.jsp"%>

<!-- Setting page buffer size.-->
<%@ page  buffer="20kb"%>

<%
    Query queryObject = null;

    ControllerClient client = new ControllerClient(request, response);
    ClientCustomizer clientCustomizer=null;

    StringBuffer physicalQuery = new StringBuffer();

    String term1 = StringUtil.EMPTY_STRING;
    String term2 = StringUtil.EMPTY_STRING;
    String term3 = StringUtil.EMPTY_STRING;
    String bool1 = StringUtil.EMPTY_STRING;
    String bool2 = StringUtil.EMPTY_STRING;

    String dbName = StringUtil.EMPTY_STRING;
    String remoteURL = StringUtil.EMPTY_STRING;
    String searchID = StringUtil.EMPTY_STRING;
    String databaseType = StringUtil.EMPTY_STRING;
    String pUserId = StringUtil.EMPTY_STRING;
    String sessionId = StringUtil.EMPTY_STRING;
    String customizedLogo = StringUtil.EMPTY_STRING;

    int customizedStartYear = 1970;

    boolean personalization = false;
    boolean newSearch = true;
    boolean remoteDB = true;
    boolean isPersonalizationPresent = true;
    boolean isEmailAlertsPresent = true;

    DatabaseConfig databaseConfig = DatabaseConfig.getInstance();
%>

<%!
    int customizedEndYear=2002;
    //This variable is used to hold size of the results that to be displayed per page
    int pagesize=0;
    String pageSize=null;
    public void jspInit()
    {

        ApplicationProperties eiProps = ApplicationProperties.getInstance();
        pageSize = eiProps.getProperty("PAGESIZE");
        pagesize=Integer.parseInt(pageSize.trim());

        // jam Y2K3
        customizedEndYear = Integer.parseInt(eiProps.getProperty("SYSTEM_ENDYEAR"));
    }
%>

<%

    /**
    *  Getting the UserSession object from the Controller client .
    *  Getting the session id from the usersession.
    */
    UserSession ussession = (UserSession) client.getUserSession();
    //client.updateUserSession(ussession);
    sessionId = ussession.getSessionid();

    pUserId = ussession.getUserid();
    if((pUserId != null) && (pUserId.trim().length() != 0))
    {
        personalization=true;
    }
    String[] credentials = ussession.getCartridge();
    String customerId=ussession.getCustomerid().trim();
    clientCustomizer=new ClientCustomizer(ussession);
    if(clientCustomizer.isCustomized())
    {
        isPersonalizationPresent=clientCustomizer.checkPersonalization();
        isEmailAlertsPresent=clientCustomizer.checkEmailAlert();
        customizedLogo=clientCustomizer.getLogo();
    }

    /**
    * Getting the database parameter from Request object.
    * Assigning that to dbName.
    * Appending this variable to string buffer.
    */
    if(request.getParameter("database")!=null)
    {
        dbName = request.getParameter("database");
        physicalQuery.append("database="+URLEncoder.encode(dbName)+"&");
    }

    /**
    * Getting the databasetype parameter from Request object.
    * Assigning that to databaseType.
    *
    */
    if(request.getParameter("databasetype")!=null)
    {
        databaseType = request.getParameter("databasetype");
    }

    if(request.getParameter("remoteDB")!=null && (request.getParameter("remoteDB").equalsIgnoreCase("true")))
    {
        remoteDB = false;
    }

    /**
    * Validating the databaseType with the remote.
    * If its true do nothing
    * Else build the xml.
    */
    if((databaseType!=null) && (databaseType.equals("remote")))
    {
        //Do nothing
    }
    else
    {
        /**
        *Getting new searchId for each new search
        * Setting the searchId into query object
        */

        searchID = request.getParameter("SEARCHID");
        if((searchID == null) || searchID.equals(StringUtil.EMPTY_STRING))
        {
            searchID=new GUID().toString();
            newSearch = true;
        }
        else
        {
            searchID=request.getParameter("SEARCHID");
            newSearch = false;
        }

        if(newSearch)
        {

            queryObject = new Query(databaseConfig, credentials);
            queryObject.setID(searchID);
            queryObject.setDataBase(Integer.parseInt(dbName));
            queryObject.setSearchType(Query.TYPE_QUICK);

            //getting the search word one
            if(request.getParameter("searchWord1")!=null)
            {
                term1=request.getParameter("searchWord1").trim();
                physicalQuery.append("searchWord1="+ URLEncoder.encode(term1) +"&amp;");
            }
            if(request.getParameter("searchWord2")!=null)
            {
                term2=request.getParameter("searchWord2").trim();
                physicalQuery.append("searchWord2="+ URLEncoder.encode(term2) +"&amp;");
            }
            if(request.getParameter("searchWord3")!=null)
            {
                term3=request.getParameter("searchWord3").trim();
                physicalQuery.append("searchWord3="+ URLEncoder.encode(term3) +"&amp;");
            }
            //getting the boolean one parameter
            if( request.getParameter("boolean1")!=null )
            {
                bool1=request.getParameter("boolean1");
                physicalQuery.append("bool1="+ URLEncoder.encode(bool1) +"&amp;");
            }
            //getting the boolean two parameter
            if( request.getParameter("boolean2")!=null )
            {
                bool2=request.getParameter("boolean2");
                physicalQuery.append("bool2="+ URLEncoder.encode(bool2) +"&amp;");
            }

            // Confusing false positive type condition!
            // When checkbox value is present - box is checked which means
            // autostemming os off
            if(request.getParameter("astem") != null)
            {
                queryObject.setAutoStemming("off");
            }
            else
            {
                queryObject.setAutoStemming("on");
            }
            physicalQuery.append("stemming="+ URLEncoder.encode(queryObject.getAutoStemming()) +"&amp;");

            //setting Search type is quicksearch in hisQuery object
            physicalQuery.append("searchType=Quick&amp;");

            queryObject.setSearchPhrase(term1,"NO-LIMIT",bool1,term2,"NO-LIMIT",bool2,term3,"NO-LIMIT");
        }
        else
        {
            newSearch = false;

            String location = request.getParameter("location");
            if((location != null) && ("SAVEDSEARCH".equalsIgnoreCase(location)))
            {
                queryObject = SavedSearches.getSearch(searchID);

                // reset
                queryObject.setID(new GUID().toString());
                queryObject.setVisible(Query.ON);
                queryObject.setSavedSearch(Query.OFF);
                queryObject.setEmailAlert(Query.OFF);
                newSearch = true;
            }
            else
            {
                queryObject = Searches.getSearch(searchID);
            }
        }

        // do these again - may be a saved search which doesn't save these!
        queryObject.setDatabaseConfig(databaseConfig);
        queryObject.setCredentials(credentials);

        if(personalization == true)
        {
            queryObject.setUserID(pUserId);
        }
        queryObject.setSessionID(sessionId);

        queryObject.setSearchQueryWriter(new ENGnetBASEQueryWriter());
        queryObject.compile();

        if(newSearch)
        {
//            System.out.println(" saveQuery: newSearch == true ");
            Searches.saveSearch(queryObject);
        }

        String refreshOnPersonalLogin = request.getParameter("refreshOnPersonalLogin");
        if(refreshOnPersonalLogin == null)
        {
            //Generating remote search query.
            remoteURL="http://www.engnetbase.com/searchquery.asp?book=All&request="+URLEncoder.encode(queryObject.getSearchQuery().trim())
            +"&Phonic=No&NaturalLanguage=No&chkShowAbstract=&optScope=0&PerPage="+pagesize+"&sort=Hits&Start=1"
            +"&stemming="+ queryObject.getAutoStemming();
        }
        else
        {
            remoteURL = StringUtil.EMPTY_STRING;
        }
    }

    client.log("search_id", searchID);
    if ( physicalQuery != null)
    {
        client.log("query_string", physicalQuery.toString());
    }
    else
    {
        client.log("query_string", "-");
    }

    client.log("context", "search");
    client.log("action", "search");
    client.log("type", "basic");
    client.log("db_name", String.valueOf(queryObject.getDataBase()));
    client.log("page_size", "0");
    client.log("format", "citation");
    client.log("doc_id", " ");
    client.log("num_recs", "0");
    client.log("doc_index", "0");
    client.log("hits", "0");
    client.setRemoteControl();

    String strGlobalLinksXML = GlobalLinks.toXML(ussession.getCartridge());

    out.write("<PAGE>");
    // DBmask is needed in session history page for SESSION-TABLE template
    out.write("<DBMASK>"+queryObject.getDataBase()+"</DBMASK>");
    out.write("<HEADER/>");
    out.write(strGlobalLinksXML);
    out.write("<NAVIGATION-BAR/>");
    out.write("<FOOTER/>");
    out.write("<SESSION-TABLE/>");
    out.write("<SESSION-ID>"+sessionId+"</SESSION-ID>");
    out.write("<SEARCH-ID>"+searchID+"</SEARCH-ID>");
    out.write("<CUSTOMER-ID>"+customerId+"</CUSTOMER-ID>");
    out.write("<CUSTOMIZED-LOGO>"+customizedLogo+"</CUSTOMIZED-LOGO>");
    out.write("<PERSONALIZATION-PRESENT>"+isPersonalizationPresent+"</PERSONALIZATION-PRESENT>");
    out.write("<EMAILALERTS-PRESENT>"+isEmailAlertsPresent+"</EMAILALERTS-PRESENT>");
    out.write("<SELECTED-DATABASE>"+queryObject.getDataBase()+"</SELECTED-DATABASE>");
    out.write("<SEARCH-TYPE>"+queryObject.getSearchType()+"</SEARCH-TYPE>");
    out.write("<PERSONALIZATION>"+personalization+"</PERSONALIZATION>");
    out.write("<PERSON-USER-ID>"+pUserId+"</PERSON-USER-ID>");
    out.write("<RESULTS-PER-PAGE>"+pagesize+"</RESULTS-PER-PAGE>");
    if(remoteURL != null && !StringUtil.EMPTY_STRING.equals(remoteURL))
    {
        out.write("<REMOTE-QUERY><![CDATA["+remoteURL+"]]></REMOTE-QUERY>");
    }
    out.write(queryObject.toXMLString());

%>
<%@ include file="database.jsp"%>
<%@ include file="queryForm.jsp"%>
<%
    out.write("</PAGE>");
    out.println("<!--END-->");
    out.flush();
%>