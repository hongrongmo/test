<%@ page language="java" %>
<%@ page session="false" %>
<!-- import statements of Java packages -->
<%@ page import="java.net.URLEncoder"%>
<%@ page import="java.util.*"%>

<!--import statements of ei packages.-->
<%@ page import="org.ei.controller.ControllerClient"%>
<%@ page import="org.ei.session.*"%>
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
    SessionID sessionIdObj = null;

    StringBuffer physicalQuery = new StringBuffer();

    String sessionId = StringUtil.EMPTY_STRING;
    String pUserId = StringUtil.EMPTY_STRING;
    String term1 = StringUtil.EMPTY_STRING;
    String dbName = StringUtil.EMPTY_STRING;
    String remoteURL = StringUtil.EMPTY_STRING;
    String databaseType = StringUtil.EMPTY_STRING;
    String searchID = StringUtil.EMPTY_STRING;
    String customizedLogo = StringUtil.EMPTY_STRING;

    int customizedStartYear=1970;

    boolean personalization = false;
    boolean newSearch = true;
    boolean remoteDB = true;
    boolean isPersonalizationPresent=true;
    boolean isEmailAlertsPresent=true;

    DatabaseConfig databaseConfig = DatabaseConfig.getInstance();

%>

<%!
    int customizedEndYear=2002;
    //This variable is used to hold size of the results that to be displayed per page
    int pagesize=0;
    String pageSize=null;
    public void jspInit()
    {

        RuntimeProperties eiProps = ConfigService.getRuntimeProperties();
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
    UserSession ussession=(UserSession)client.getUserSession();
    //client.updateUserSession(ussession);
    sessionId=ussession.getID();
    sessionIdObj = ussession.getSessionID();

    pUserId = ussession.getProperty("P_USER_ID");
    if((pUserId != null) && (pUserId.length() != 0))
    {
        personalization=true;
    }
    User user=ussession.getUser();
    String[] credentials = user.getCartridge();
    String customerId=user.getCustomerID();
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
        physicalQuery.append("database=").append(URLEncoder.encode(dbName)).append("&");
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
    if((databaseType!=null) && (databaseType.equalsIgnoreCase("remote")))
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
            queryObject.setSearchType(Query.TYPE_EXPERT);

            //getting the search word one
            if(request.getParameter("searchWord1")!=null)
            {
                term1=request.getParameter("searchWord1");
                physicalQuery.append("searchWord1="+ URLEncoder.encode(term1) +"&amp;");
            }

            //setting Search type is quicksearch in hisQuery object
            physicalQuery.append("searchType=Expert&amp;");

            queryObject.setSearchPhrase(term1,"","","","","","","");
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
                +"&stemming=no";
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
    client.log("type", "expert");
    client.log("db_name", String.valueOf(queryObject.getDataBase()));
    client.log("page_size", "0");
    client.log("format", "citation");
    client.log("doc_id", " ");
    client.log("num_recs", "0");
    client.log("doc_index", "0");
    client.log("hits", "0");
    client.setRemoteControl();

    String strGlobalLinksXML = GlobalLinks.toXML(user.getCartridge());

    out.write("<PAGE>");
    // DBmask is needed in session history page for SESSION-TABLE template
    out.write("<DBMASK>"+queryObject.getDataBase()+"</DBMASK>");
    out.write("<HEADER/>");
    out.write(strGlobalLinksXML);
    out.write("<NAVIGATION-BAR/>");
    out.write("<FOOTER/>");
    out.write("<SESSION-TABLE/>");
    out.write("<SESSION-ID>"+sessionIdObj+"</SESSION-ID>");
    out.write("<SEARCH-ID>"+searchID+"</SEARCH-ID>");
    out.write("<CUSTOMER-ID>"+customerId+"</CUSTOMER-ID>");
    out.write("<CUSTOMIZED-LOGO>"+customizedLogo+"</CUSTOMIZED-LOGO>");
    out.write("<PERSONALIZATION-PRESENT>"+isPersonalizationPresent+"</PERSONALIZATION-PRESENT>");
    out.write("<EMAILALERTS-PRESENT>"+isEmailAlertsPresent+"</EMAILALERTS-PRESENT>");
    out.write("<SEARCH-TYPE>"+queryObject.getSearchType()+"</SEARCH-TYPE>");
    out.write("<SELECTED-DATABASE>"+queryObject.getDataBase()+"</SELECTED-DATABASE>");
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