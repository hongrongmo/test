<%@page import="org.engvillage.config.RuntimeProperties"%>
<%@page import="org.engvillage.biz.controller.ClientCustomizer"%>
<%@ page language="java" %>
<%@ page session="false" %>

<!-- import statements of Java packages -->
<%@ page import="java.util.*"%>
<%@ page import="java.net.URLEncoder"%>
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

    // This variable for sessionId
    String sessionId = null;

    /**
    *These variable for Searchterms
    * and search fields.
    */
    String term1 = "";

    //This variable for YearRange
    String yearRange = null;

    //This variable for database name.
    String dbName=null;

    //This variable for remoteUrl
    String remoteURL=null;

    //This variable for type of the database.
    String databaseType=null;

    //This variable for string buffer which contains physical query.
    StringBuffer physicalQuery = new StringBuffer();

    /**
    *This variables is used for storing intermediate query.
    */
    StringBuffer exp1 = null;
    StringBuffer exp1Display = null;
    StringBuffer exp2 = null;
    StringBuffer exp2Display = null;
    StringBuffer exp3 = null;
    StringBuffer exp3Display = null;
    StringBuffer query = null;

    int customizedStartYear=1970;

    //This variable is used for storing display query
    StringBuffer queryDisplay = null;

    //This variable is used for storing searchID
    String searchID=null;

    // Variable to hold the Personalization userid
    String pUserId = "";
    // Variable to hold the Personalizaton status
    boolean personalization = false;

    boolean newSearch=true;
    boolean remoteDB=true;

    //these variables for client customization
    ClientCustomizer clientCustomizer=null;
    boolean isPersonalizationPresent=true;
    boolean isEmailAlertsPresent=true;
    String customizedLogo="";

    DatabaseConfig databaseConfig = DatabaseConfig.getInstance();

%>

<%!
    int customizedEndYear=2002;

    public void jspInit()
    {
        try
        {

            RuntimeProperties eiProps = RuntimeProperties.getInstance();

            // jam Y2K3
            customizedEndYear = Integer.parseInt(eiProps.getProperty("SYSTEM_ENDYEAR"));
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

%>
<%

    /**
    *  Getting the UserSession object from the Controller client .
    *  Getting the session id from the usersession.
    */
    UserSession ussession=(UserSession)client.getUserSession();
    sessionId=ussession.getSessionid();

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
        dbName = request.getParameter("database").trim();
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
        remoteDB=false;
    }

    /**
    *Validating the databaseType with the remote.
    *If its true do nothing
    * Else build the xml.
    */
    if((databaseType!=null) && (databaseType.equalsIgnoreCase("remote")))
    {
        //Do nothing
    }
    else
    {
        /**
        *Getting new searchID for each new search
        * Setting the searchID into query object
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

            /*********************************************
            *
            * Collecting the term
            *
            **********************************************/
            //getting the search word one
            if(request.getParameter("searchWord1")!=null)
            {
                term1=request.getParameter("searchWord1");
            }

            if(request.getParameter("yearRange")!=null)
            {
                yearRange=request.getParameter("yearRange");
            }

            queryObject.setSearchPhrase(term1,"","","","","","","");
            queryObject.setStartYear(yearRange);
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

        queryObject.setSearchQueryWriter(new USPTOQueryWriter());
        queryObject.compile();

        if(newSearch)
        {
//            System.out.println(" saveQuery: newSearch == true ");
            Searches.saveSearch(queryObject);
        }
        String refreshOnPersonalLogin = request.getParameter("refreshOnPersonalLogin");


        String syear = queryObject.getStartYear();

        /*
        *    Needed only for old saved USPTO searches.
        */

        if(syear == null || syear.trim().length()==0)
        {
            syear = "ptxt";
        }

        if(refreshOnPersonalLogin == null)
        {
            remoteURL = "http://patft.uspto.gov/netacgi/nph-Parser?Sect1=PTO2&Sect2=HITOFF&p=1&u=%2Fnetahtml%2Fsearch-bool.html&r=0&f=S&l=50&Query="+queryObject.getSearchQuery().trim()+"&d="+syear;
        }
        else
        {
            remoteURL = StringUtil.EMPTY_STRING;
        }
    }  // if((databaseType!=null) && (databaseType.equals("remote")) )


    // Logging
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
    out.write("<PERSONALIZATION>"+personalization+"</PERSONALIZATION>");
    out.write("<EMAILALERTS-PRESENT>"+isEmailAlertsPresent+"</EMAILALERTS-PRESENT>");
    out.write("<SELECTED-DATABASE>"+queryObject.getDataBase()+"</SELECTED-DATABASE>");
    out.write("<SEARCH-TYPE>"+queryObject.getSearchType()+"</SEARCH-TYPE>");
    out.write("<PERSON-USER-ID>"+pUserId+"</PERSON-USER-ID>");
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