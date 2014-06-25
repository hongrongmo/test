<%--
    This page the follwing params as input and generates XML output.
    @param java.lang.String database
    @param java.lang.String CID
--%>
<%@ page session="false"%>
<%@ page contentType="text/xml"%>
<%--import statements of ei packages.--%>
<%@ page import="org.ei.controller.ControllerClient"%>
<%@ page import="org.ei.session.*"%>
<%@ page import="org.ei.domain.personalization.*"%>
<%@ page import="org.ei.domain.*"%>
<%@ page import="org.ei.config.*"%>
<%@ page import="org.ei.domain.personalization.GlobalLinks"%>
<%@ page import="org.ei.domain.Searches"%>
<%@ page import="org.ei.util.*"%>
<%-- import statements of Java packages--%>
<%@ page import="java.io.*"%>
<%@ page import="java.net.*"%>
<%@ page import="java.util.*"%>
<%@ page errorPage="/error/errorPage.jsp"%>
<%
    /**
    *   This page contains the hardcoded xml.
    */

    String searchid = "";
    Query queryObject = null;

    // This variable is used to hold ControllerClient instance
    ControllerClient client = new ControllerClient(request, response);

    // This variable holds sessionId
    SessionID sessionId = null;
    String sesID = null;

    // Variable to hold the Personalization userid
    String pUserId = null;

    // Variable to hold the Personalizaton status
    boolean personalization = false;

    // Variable to hold the customization status
    boolean customization = false;

    // This variable holds the url
    URL url = null;

    IEVWebUser user=null;

    ClientCustomizer clientCustomizer = null;
    String customizedDB = null;
    String customizedEasyDb = null;
    String customizedStartYear = null;
    int customizedEndYear = -1;
    boolean isPersonalizationPresent = true;
    String customizedLogo = "";
    String customerId = "";
    String CID = request.getParameter("CID");

%>
<%!
    // jspInit() method
    RuntimeProperties eiProps=null;
    public void jspInit()
    {
        try
        {
             eiProps = ConfigService.getRuntimeProperties();
        }
        catch(Exception e)
        {

        }
    }
%>
<%

    /**
    *  Getting the UserSession object from the Controller client .
    *  Getting the session id from the usersession.
    *
    */

    UserSession ussession=client.getUserSession();
    sessionId = ussession.getSessionID();

    sesID = sessionId.toString();
    pUserId = ussession.getUserIDFromSession();
    if((pUserId != null) && (pUserId.trim().length() != 0))
    {
        personalization=true;
    }

    user=ussession.getUser();

    customerId=user.getCustomerID();
    clientCustomizer=new ClientCustomizer(ussession);

    if(clientCustomizer!=null && clientCustomizer.isCustomized())
    {

        customizedEasyDb = clientCustomizer.getEasyDb();
        customizedDB = clientCustomizer.getDefaultDB();
        customizedStartYear = clientCustomizer.getSYear();
        customizedEndYear = clientCustomizer.getEndYear();
        isPersonalizationPresent = clientCustomizer.checkPersonalization();
        customizedLogo = clientCustomizer.getLogo();
    }
%>
<%
 
    /**
    *These variable to hold search words and search options
    *
    */

    String term1 = "";
    String term2 = "";
    String term3 = "";
    String field1 = "";
    String field2 = "";
    String field3 = "";
    //This vriable for batabase name
    String dbName="";


    //This variable for to limit by Document type
    String limitDocument="";
    //This variable for to limit by Treatment type
    String limitTreatment="";
    //This variable for to limit by language.
    String limitLanguage="";
    //This variable for selected radio button for year
    String yearSelect="";
    //this variable for start year.
    String strYear = "";
    //this variable for end year.
    String eYear = "";
    //this varaible for boolean one.
    String bool1=null;
    //this variable for boolean two.
    String bool2=null;


    //This variable for  last four updates only
    String updates="";
    //This variable Discipline type code
    String limitDiscipline="";

    // These variable to hold search words and search options
    String searchWord1="";
    String searchWord2="";
    String searchWord3="";
    String boolean1="";
    String boolean2="";
    String section1="";
    String section2="";
    String section3="";
    String doctype="";
    String treatmentType="";
    String language="";
    String sort="";

    // the default autostem is on - that means
    // the checkbox is clear
    String autostem="on";

    String yearRange="";
    String yearselect="";
    String startYear="";
    String error= null;
    // This will become the default selected end year since it is ouput in //SESSION-DATA/END-YEAR
    String endYear= String.valueOf(SearchForm.ENDYEAR);

    /**
    * This parameter is fetched from the request object to see if the selected set is to be cleared when a new search
    * is executed by the application when the user clicks on the New Search button on the navigation bar
    *
    */
      
    if(request.getParameter("error") != null){
    	error= request.getParameter("error");
    }
    
    if((request.getParameter("searchid")!=null) && (request.getParameter("searchid").trim().length() > 0) && error == null)
    {
    	searchid = request.getParameter("searchid").trim();
    	queryObject = Searches.getSearch(searchid);

    }

    if((request.getParameter("database")!=null) && (request.getParameter("database").trim().length() > 0))
    {
    	dbName = request.getParameter("database").trim();
    }
    else if ("ebookSearch".equals(CID)) {
    	dbName = Integer.toString(DatabaseConfig.PAG_MASK);
    }
    else if(customizedDB != null)
    {
    	dbName = customizedDB;
    }

    // endYear for CBF - 1969
    if (dbName.trim().equals(Integer.toString(DatabaseConfig.CBF_MASK)))
    {
    	endYear=Integer.toString(DatabaseConfig.CBF_ENDYEAR);
    }
    // endYear for IBS - 1969
    if (dbName.trim().equals(Integer.toString(DatabaseConfig.IBS_MASK)))
    {
    	endYear=Integer.toString(DatabaseConfig.IBS_ENDYEAR);
    }

    if((request.getParameter("searchWord1")!=null) && (request.getParameter("searchWord1").trim().length()>0))
    {
        searchWord1 = request.getParameter("searchWord1").trim();
    }

    if((request.getParameter("searchWord2")!=null) && (request.getParameter("searchWord2").trim().length()>0))
    {
        searchWord2 = request.getParameter("searchWord2").trim();
    }

    if((request.getParameter("searchWord3")!=null) && (request.getParameter("searchWord3").trim().length()>0))
    {
        searchWord3 = request.getParameter("searchWord3").trim();
    }

    if((request.getParameter("boolean1")!=null) && (request.getParameter("boolean1").trim().length()>0))
    {
        boolean1 = request.getParameter("boolean1").trim();
    }

    if((request.getParameter("boolean2")!=null) && (request.getParameter("boolean2").trim().length()>0))
    {
        boolean2 = request.getParameter("boolean2").trim();
    }

    if((request.getParameter("section1")!=null) && (request.getParameter("section1").trim().length()>0))
    {
        section1 = request.getParameter("section1").trim();
    }

    if((request.getParameter("section2")!=null) && (request.getParameter("section2").trim().length()>0))
    {
        section2 = request.getParameter("section2").trim();
    }

    if((request.getParameter("section3")!=null) && (request.getParameter("section3").trim().length()>0))
    {
        section3 = request.getParameter("section3").trim();
    }

    if((request.getParameter("doctype")!=null) && (request.getParameter("doctype").trim().length()>0))
    {
        doctype = request.getParameter("doctype").trim();
    }

    if((request.getParameter("treatmentType")!=null) && (request.getParameter("treatmentType").trim().length()>0))
    {
        treatmentType = request.getParameter("treatmentType").trim();
    }

    if((request.getParameter("language")!=null) && (request.getParameter("language").trim().length()>0))
    {
        language = request.getParameter("language").trim();
    }

    if((request.getParameter("disciplinetype")!=null) && (request.getParameter("disciplinetype").trim().length()>0))
    {
        limitDiscipline = request.getParameter("disciplinetype").trim();
    }

	if(request.getParameter("sort")!=null)
	{
		sort = request.getParameter("sort");
	}
	else if (clientCustomizer.getSortBy() != null)
	{
		sort = clientCustomizer.getSortBy();
	}

    // preserve autostem settings from current request
	if(request.getParameter("autostem") != null)
	{
		autostem = request.getParameter("autostem").trim();

		if("on".equalsIgnoreCase(autostem))
		{
			autostem = "off";
		}
		else if("off".equalsIgnoreCase(autostem))
		{
			autostem = "on";
		}
	}
	else
	{
        // if this is an expert search the default will be off
	    if("expertSearch".equalsIgnoreCase(CID))
        {
            // this means the checkbox will checked!
            autostem = "off";
        }
        // else pickup the default from client customizer
        else if (clientCustomizer.getAutostem() != null)
        {
	        autostem = clientCustomizer.getAutostem();
	    }
	}

    if((request.getParameter("yearselect")!=null) && (request.getParameter("yearselect").trim().length()>0))
    {
        yearselect = request.getParameter("yearselect").trim();
    }

    if((request.getParameter("startYear")!=null) && (request.getParameter("startYear").trim().length()>0))
    {
       startYear = request.getParameter("startYear").trim();
    }

	if((customizedStartYear != null) && (customizedStartYear.length() > 0)) {
		strYear = customizedStartYear;
	}

    if((request.getParameter("endYear")!=null) && (request.getParameter("endYear").trim().length()>0))
    {
        endYear = request.getParameter("endYear").trim();
  	}

    if((request.getParameter("yearRange")!=null) && (request.getParameter("yearRange").trim().length()>0))
    {
        yearRange = request.getParameter("yearRange").trim();
    }

    if((request.getParameter("updatesNo")!=null) && (request.getParameter("updatesNo").trim().length()>0))
    {
        updates = request.getParameter("updatesNo").trim();
    }

    //Logging

    client.log("QUERY_STRING", " ");
    client.log("SORT_BY", sort);
    client.log("CONTEXT", "search");
    client.log("ACTION", "prepare");
    if("expertSearch".equalsIgnoreCase(CID))
    {
        client.log("TYPE", "expert_search");
    }
    else {
        client.log("TYPE", "basic_search");
    }

    if((request.getParameter("database")!=null) && (request.getParameter("database").trim().length()>0))
    {
        String logDatabase = request.getParameter("database");

        if(logDatabase.equalsIgnoreCase("COMBINED"))
        {
            client.log("DB_NAME", "3");
        }
        else if(logDatabase.equalsIgnoreCase("inspec"))
        {
            client.log("DB_NAME", "2");
        }
        else if(logDatabase.equalsIgnoreCase("compendex"))
        {
            client.log("DB_NAME", "1");
        }
        else
        {
            client.log("DB_NAME", logDatabase);
        }

    }

    client.log("LIMIT_DOCUMENT", limitDocument);
    client.log("LIMIT_TREATMENT", limitTreatment);
    client.log("LIMIT_LANGUAGE", limitLanguage);
    client.log("LIMIT_YEAR", yearSelect);
    client.log("START_YEAR", startYear);
    client.log("END_YEAR", eYear);
    client.log("NUM_RECS", "0");
    client.log("DOC_INDEX", "0");
    client.log("HIT_COUNT", "0");
    client.setRemoteControl();

    String strGlobalLinksXML = GlobalLinks.toXML(user.getCartridge());

%>
<QUICK-SEARCH>
<%
	if((searchid != null) && (searchid.length() > 0))
	{
    	out.write(queryObject.toXMLString());
    }
	else
	{
%>
		<SESSION-DATA>
			<SEARCH-PHRASE>
			<SEARCH-PHRASE-1><%=searchWord1%></SEARCH-PHRASE-1>
			<SEARCH-PHRASE-2><%=searchWord2%></SEARCH-PHRASE-2>
			<SEARCH-PHRASE-3><%=searchWord3%></SEARCH-PHRASE-3>
			<SEARCH-PHRASES></SEARCH-PHRASES>
			<BOOLEAN-1><%=boolean1%></BOOLEAN-1>
			<BOOLEAN-2><%=boolean2%></BOOLEAN-2>
			<BOOLEANS></BOOLEANS>
			<SEARCH-OPTION-1><%=section1%></SEARCH-OPTION-1>
			<SEARCH-OPTION-2><%=section2%></SEARCH-OPTION-2>
			<SEARCH-OPTION-3><%=section3%></SEARCH-OPTION-3>
			<SEARCH-OPTIONS></SEARCH-OPTIONS>
			</SEARCH-PHRASE>
			<DOCUMENT-TYPE><%=doctype%></DOCUMENT-TYPE>
			<TREATMENT-TYPE><%=treatmentType%></TREATMENT-TYPE>
			<LANGUAGE><%=language%></LANGUAGE>
			<DISCIPLINE-TYPE><%=limitDiscipline%></DISCIPLINE-TYPE>
	<%
		// if sort is not defined - do not output
		// default value will be picked up in XSL
		// as <SORT-DEFAULT> output by queryForm.jsp
			if((sort != null) && !StringUtil.EMPTY_STRING.equals(sort))
			{
	%>          <SORT-OPTION><%=sort%></SORT-OPTION>
	<%
			}
	%>
			<AUTOSTEMMING><%=autostem%></AUTOSTEMMING>
			<LASTFOURUPDATES><%=yearselect%></LASTFOURUPDATES>
			<UPDATES><%=updates%></UPDATES>
			<START-YEAR><%=startYear%></START-YEAR>
			<END-YEAR><%=endYear%></END-YEAR>
			<YEAR-RANGE><%=yearRange%></YEAR-RANGE>
		</SESSION-DATA>
<%
	}
%>
			<YEAR-STRING><%=strYear%></YEAR-STRING>

			<EASYDB><%=customizedEasyDb%></EASYDB>
            <SESSION-ID><%=sesID%></SESSION-ID>
            <SEARCH-ID><%=searchid%></SEARCH-ID>
			<PERSONALIZATION><%=personalization%></PERSONALIZATION>
			<CUSTOMER-ID><%=customerId%></CUSTOMER-ID>
			<HEADER/>
			<%= strGlobalLinksXML %>
			<NAVIGATION-BAR/>
			<RESULTS-MANAGER/>
			<FOOTER/>
			<COMBINED-SEARCH/>
			<PERSONALIZATION-PRESENT><%=isPersonalizationPresent%></PERSONALIZATION-PRESENT>
			<CUSTOMIZED-LOGO><%=customizedLogo%></CUSTOMIZED-LOGO>
			<CREDS><%= user.getCartridgeString() %></CREDS>
		<%@ include file="database.jsp"%>
		<%@ include file="queryForm.jsp"%>
<%
	// TMH 
	// UI refresh project is including search history on search pages.  
    if((sessionId != null) && (sessionId.getID() != null) && (sessionId.getID().trim().length() > 0)) {
    	Searches.getSessionXMLQuery(pUserId,sessionId.getID(),out);
        DatabaseConfig databaseConfig = DatabaseConfig.getInstance();
        databaseConfig.sortableToXML(user.getCartridge(), out);
    }
%>
</QUICK-SEARCH>
<% 	 out.flush(); %>