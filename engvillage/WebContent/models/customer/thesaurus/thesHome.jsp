<%@ page session="false" %>
<%@ page import="org.ei.thesaurus.*" %>
<%@ page import="org.ei.domain.*" %>
<%@ page import="org.ei.domain.personalization.*" %>
<%@ page import="org.ei.controller.*" %>
<%@ page import="org.ei.session.*" %>
<%@ page import="org.ei.util.StringUtil"%>

<%@ page import="java.util.*" %>
<%@ page import="java.io.*" %>

<%@ page import="org.ei.parser.base.*"%>
<%@ page import="org.ei.query.base.*"%>
<%@ page import="org.ei.config.*"%>

<%@ page import="org.ei.domain.*"%>

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


    String customerId = user.getCustomerID().trim();

    String sessionID = ussession.getID();
    SessionID sessionIdObj = ussession.getSessionID();

    ClientCustomizer clientCustomizer = new ClientCustomizer(ussession);
    String strGlobalLinksXML = GlobalLinks.toXML(user.getCartridge());


    String term = request.getParameter("term");
    String index = request.getParameter("index");
    String dbName = request.getParameter("database");
    String databaseID = null;

    boolean hasCPX = UserCredentials.hasCredentials(1, databaseConfig.getMask(user.getCartridge()));
    boolean hasINS = UserCredentials.hasCredentials(2, databaseConfig.getMask(user.getCartridge()));
    boolean hasEPT = UserCredentials.hasCredentials(2048, databaseConfig.getMask(user.getCartridge()));
    boolean hasELT = UserCredentials.hasCredentials(1024, databaseConfig.getMask(user.getCartridge()));
    boolean hasGEO = UserCredentials.hasCredentials(8192, databaseConfig.getMask(user.getCartridge()));
    boolean hasGRF = UserCredentials.hasCredentials(2097152, databaseConfig.getMask(user.getCartridge()));
    if (!hasCPX && !hasINS && !hasGEO && !hasGRF && !hasEPT && !hasELT) {
    	// Send to error page - no Thes databases in credentials!
        client.setRedirectURL("/system/error.url");
        client.setRemoteControl();
        return;    	
    }
    
    pUserId = ussession.getUserIDFromSession();
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

    if(dbName == null ||
       dbName.length() == 0)
    {
		if (hasCPX) {
			dbName = "1";
		}
		else if (hasINS) {
			dbName = "2";
		}
		else if (hasEPT) {
			dbName = "2048";
		}
		else if (hasELT) {
			dbName = "1024";
		}
		else if (hasELT && hasEPT) {
			dbName = "3072";
		}
		else if (hasGEO) {
			dbName = "8192";
		}
		else if (hasGRF) {
			dbName = "2097152";
		}

        if(clientCustomizer.getStartYear()!=-1)
        {
            
            customizedStartYear=clientCustomizer.getStartYear();

        }
        if(clientCustomizer.getEndYear()!=-1)
        {
            
            customizedEndYear=clientCustomizer.getEndYear();
        }
    }
    
    if(dbName.equals("1")) 
    { 
    	databaseID = "cpx";
    }
    else if(dbName.equals("2"))
    {
        databaseID = "ins";
    }
    else if(dbName.equals("2048"))
    {
        databaseID = "ept";
    }
    else if(dbName.equals("3072"))
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


    out.write("<DOC>");
    out.write("<HEADER/>");
    out.write("<THESAURUS-HEADER/>");
    out.write("<SEARCH-TYPE>Thesaurus</SEARCH-TYPE>");
    out.write("<HAS-INSPEC>"+hasINS+"</HAS-INSPEC>");
    out.write("<HAS-CPX>"+hasCPX+"</HAS-CPX>");
    out.write("<HAS-GEO>"+hasGEO+"</HAS-GEO>");
    out.write("<HAS-GRF>"+hasGRF+"</HAS-GRF>");
    out.write("<HAS-EPT>"+hasEPT+"</HAS-EPT>");
    out.write("<HAS-ELT>"+hasELT+"</HAS-ELT>");
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
    out.write("<DATABASE>");
    out.write(dbName);
    out.write("</DATABASE>");
%>
<%@ include file="../database.jsp"%>

<%
// TMH
// UI refresh does not use multiple frams so include the Clipboard
// content with the home page!

	String searchid = request.getParameter("searchid");
	String recentXmlQueryString = null;
	StringBuffer strBufThesXML = new StringBuffer();

	if(searchid != null)
	{

		String sessionId=ussession.getID();

//		recentXmlQueryString = Searches.getXMLSearch(searchid);
//		Query tQuery = new Query(new FastQueryWriter(),
//            					 DatabaseConfig.getInstance(),
//            					 user.getCartridge(),
//            					 recentXmlQueryString);

        Query tQuery = Searches.getSearch(searchid);
        tQuery.setSearchQueryWriter(new FastQueryWriter());
        tQuery.setDatabaseConfig(DatabaseConfig.getInstance());
        tQuery.setCredentials(user.getCartridge());
        recentXmlQueryString = tQuery.toXMLString();

		// creating Thesaurus XML for output
		BooleanQuery bq  = tQuery.getParseTree();
		String strThesTerm = "";

		ExactTermGatherer etg = new ThesaurusCVTermGatherer();
		List lstThesTerms = etg.gatherExactTerms(bq);
		Iterator itrThesTerms = lstThesTerms.iterator();
		String firstCon = etg.getFirstConnector();

		strBufThesXML.append("<CVS CONNECTOR='"+firstCon+"'>");
		while(itrThesTerms.hasNext())
		{
			strThesTerm = (String) itrThesTerms.next();
			strBufThesXML.append("<CV><![CDATA[");
			strBufThesXML.append(strThesTerm);
			strBufThesXML.append("]]></CV>");
		}
		strBufThesXML.append("</CVS>");

	}


	if(database == null)
	{
		database ="1";
	}

%>

<CLIPBOARD>
<CONTEXT>T</CONTEXT>
<%
	
	if(recentXmlQueryString != null)
	{
		out.write(strBufThesXML.toString());
		out.write(recentXmlQueryString);
	}
  else
  {
		out.write("<SESSION-DATA>");
		//out.write("<START-YEAR>1990</START-YEAR>");
		
		out.write("<END-YEAR>" + String.valueOf(SearchForm.ENDYEAR) + "</END-YEAR>");
		out.write("</SESSION-DATA>");
	}

%>
<%@ include file="../queryForm.jsp"%>
</CLIPBOARD>
<%
	// TMH 
	// UI refresh project is including search history on search pages.  
    if((sessionIdObj != null) && (sessionIdObj.getID() != null) && (sessionIdObj.getID().trim().length() > 0)) {
    	Searches.getSessionXMLQuery(pUserId,sessionIdObj.getID(),out);
        databaseConfig.sortableToXML(user.getCartridge(), out);
    }

    out.write("</DOC>");

    out.flush();
%>
