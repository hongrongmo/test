<!--
* This page the follwing params as input and generates XML output.
* @param java.lang.String.database
* @param java.lang.String.CID
* @param java.lang.String.SEARCHID
* @param java.lang.String.SEARCHTYPE
* @param java.lang.String.DATABASEID
* @param java.lang.String.BASKETCOUNT
* @param java.lang.String.SEARCHQUERY
-->
<%@ page language="java" %>
<%@ page session="false" %>
<%@ page errorPage="/error/errorPage.jsp" %>
<%@ page buffer="20kb"%>
<!-- import statements of Java packages-->
<%@ page import="java.util.*"%>
<%@ page import="java.net.*"%>
<%@ page import="java.io.FileWriter"%>
<!--import statements of ei packages.-->

<%@ page import="org.ei.domain.*"%>
<%@ page import="org.ei.config.*"%>
<%@ page import="org.ei.controller.ControllerClient"%>
<%@ page import="org.ei.session.*" %>
<%@ page import="org.ei.config.*"%>
<%@ page import="org.ei.domain.personalization.GlobalLinks"%>
<%@ page import="org.ei.domain.Searches"%>

<%
    Query queryObject = new Query();
    DatabaseConfig databaseConfig = DatabaseConfig.getInstance();
    ClientCustomizer clientCustomizer = null;
    LocalHolding localHolding = null;
    BasketPage basketPage = null;
    DocumentBasket basket = null;
    ControllerClient client = null;

    SessionID sessionidObj = null;

    List docBasketDatabase = null;

    String recentXmlQueryString=null;
    String strSessionKey = "";
    String pUserId = null;
    String currentPage = null;
    String qsSearchQuery = null;

    String searchID = null;
    String databaseType=null;
    String cid="citationBasket";
    String databaseID=null;
    String urlString=null;
    String basketCount=null;
    String searchType=null;
    String navigation=null;
    String navigator=null;
    String documentFormat=null;
    String customizedLogo="";
    String customizedStartYear = "";
    String customizedEndYear = "";

    int databaseCount = 0;
    int pagesize = 0;
    int docBasketPageSize = 0 ;
    int index = 0;
    int basketSize = 0;

    boolean personalization = false;
    boolean flag = false ;
    boolean isPersonalizationPresent=true;
    boolean isLHLPresent=true;
    boolean isFullTextPresent=true;
    boolean isLocalHolidinsPresent=true;
    boolean isCitLocalHoldingsPresent=false;
    boolean isEmailAlertsPresent=true;
    boolean hasMultipleSearchIds=false;
%>
<%!

    String pageSize=null;
    int docBasketPageSize=0;
    public void jspInit()
    {
        try
        {
            RuntimeProperties runtimeProps= ConfigService.getRuntimeProperties();
            pageSize = runtimeProps.getProperty("BASKETPAGESIZE");
            docBasketPageSize=Integer.parseInt(pageSize.trim());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

%>
<%
    client = new ControllerClient(request,response);
    UserSession ussession=(UserSession)client.getUserSession();
    String sessionid = ussession.getID();
    localHolding=new LocalHolding(ussession);

    sessionidObj = ussession.getSessionID();
    strSessionKey = ussession.getID();

    pUserId = ussession.getProperty("P_USER_ID");
    if((pUserId != null) && (pUserId.trim().length() != 0))
    {
        personalization=true;
    }
    User user=ussession.getUser();
    String[] credentials = user.getCartridge();
    clientCustomizer=new ClientCustomizer(ussession);
    if(clientCustomizer.isCustomized())
    {
        isPersonalizationPresent=clientCustomizer.checkPersonalization();
        isLHLPresent=clientCustomizer.checkDDS();
        isFullTextPresent=clientCustomizer.checkFullText("citationResults");
       	isCitLocalHoldingsPresent=clientCustomizer.checkLocalHolding("citationResults");
        isLocalHolidinsPresent=clientCustomizer.checkLocalHolding();
        isEmailAlertsPresent=clientCustomizer.checkEmailAlert();
        customizedLogo=clientCustomizer.getLogo();
    }

    DocumentBasket documentBasket = new DocumentBasket(strSessionKey);

    searchID  = request.getParameter("SEARCHID");

    if(request.getParameter("CID") != null)
    {
        cid=request.getParameter("CID").trim();
    }

    if(request.getParameter("SEARCHTYPE") != null)
    {

        searchType=request.getParameter("SEARCHTYPE").trim();
    }

    if(request.getParameter("DATABASETYPE") != null)
    {

        databaseType=request.getParameter("DATABASETYPE").trim();
    }

    if(request.getParameter("DATABASEID") != null)
    {

        databaseID=request.getParameter("DATABASEID").trim();
    }

    if(request.getParameter("BASKETCOUNT") != null)
    {
        basketCount = request.getParameter("BASKETCOUNT").trim();
    }

    navigator =(String)request.getParameter("navigator");



    if(request.getParameter("SEARCHQUERY")!= null)
    {
        qsSearchQuery = request.getParameter("SEARCHQUERY").trim();
    }

    if((databaseType != null ) && (databaseType.equals("Compendex")))
    {
        if(clientCustomizer.getStartYear()!=-1)
        {
            customizedStartYear=Integer.toString(clientCustomizer.getStartYear());
        }
        if(clientCustomizer.getEndYear()!=-1)
        {
            customizedEndYear=Integer.toString(clientCustomizer.getEndYear());
        }
    }

    if(basketCount == null )
    {
        basketCount = "1";
        index = Integer.parseInt(basketCount.trim());
    }
    else
    {
        index = Integer.parseInt(basketCount.trim());
    }

    basket = new DocumentBasket(strSessionKey);
    basketSize = basket.getBasketSize();
    documentFormat=Citation.CITATION_FORMAT;

    if( (cid!=null) && ( (cid.equals("citationSelectedSet")) ||
        (cid.equals("printCitationSelectedSet")) ) )
    {
        documentFormat=Citation.CITATION_FORMAT;
    }
    else if( (cid!=null) && ( (cid.equals("abstractSelectedSet")) ||
             (cid.equals("printAbstractSelectedSet")) ) )
    {
        documentFormat=Abstract.ABSTRACT_FORMAT;
    }
    else if( (cid!=null) && ( (cid.equals("detailedSelectedSet"))  ||
             (cid.equals("printDetailedSelectedSet")) ) )
    {
        documentFormat=FullDoc.FULLDOC_FORMAT;
    }

    if(basketSize > 0)
    {
        basketPage = (BasketPage) basket.pageAt(index, documentFormat);
    }

    if (navigator == null)
    {
        navigator="NEXT";
    }

String strGlobalLinksXML = GlobalLinks.toXML(user.getCartridge());
/*
	client.log("SEARCH_ID", searchID);
	client.log("DOC_ID", " ");
	client.log("ACTION", "document");
	client.log("context", "search");
	client.log("FORMAT", documentFormat);
	client.log("DB_NAME", "Selected Set");
	client.log("PAGE_SIZE", pageSize);
	client.log("DOC_INDEX", (new Integer(index)).toString());
	client.log("NUM_RECS", (new Integer(basketPage.docCount())).toString());
*/
	client.setRemoteControl();

	StringBuffer backurl = new StringBuffer();
	backurl.append("CID=").append(cid).append("&");
	backurl.append("SEARCHID=").append(searchID).append("&");
	backurl.append("COUNT=").append(index).append("&");
	backurl.append("SEARCHTYPE=").append(searchType).append("&");
	backurl.append("DATABASETYPE=").append(databaseType).append("&");
	backurl.append("DATABASEID=").append(databaseType);

	out.write("<PAGE>");
	out.write("<BACKURL>");
	out.write(URLEncoder.encode(backurl.toString()));
	out.write("</BACKURL>");
	out.write("<PAGE-NAV>");
	if(request.getParameter("searchresults") != null)
	{
		out.write("<RESULTS-NAV>");
		out.write(URLEncoder.encode(request.getParameter("searchresults")));
		out.write("</RESULTS-NAV>");
	}
	if(request.getParameter("newsearch") != null)
	{
		out.write("<NEWSEARCH-NAV>");
		out.write(URLEncoder.encode(request.getParameter("newsearch")));
		out.write("</NEWSEARCH-NAV>");
	}
	out.write("</PAGE-NAV>");
	out.write("<CID>");
	out.write(cid);
	out.write("</CID>");
	out.write("<HEADER/>");
	out.write("<DBMASK>");
	out.write(databaseType);
	out.write("</DBMASK>");
	out.write("<PERSONALIZATION>");
	out.write(Boolean.toString(personalization));
	out.write("</PERSONALIZATION>");
	out.write(strGlobalLinksXML);
	out.write("<SELECTED-SET-NAVIGATION-BAR/>");
	out.write("<SELECTED-SET-TOP-RESULTS-MANAGER/>");
	out.write("<CLEAR-ON-VALUE>");
	out.write(Boolean.toString(documentBasket.getClearOnNewSearch()));
	out.write("</CLEAR-ON-VALUE>");
	out.write("<SELECTED-SET-BOTTOM-RESULTS-MANAGER/>");
	out.write("<FOOTER/>");
	out.write("<CUSTOMIZED-LOGO>"+customizedLogo+"</CUSTOMIZED-LOGO>");
	out.write("<CUSTOMIZED-STARTYEAR>");
	out.write(customizedStartYear);
	out.write("</CUSTOMIZED-STARTYEAR>");
	out.write("<CUSTOMIZED-ENDYEAR>");
	out.write(customizedEndYear);
	out.write("</CUSTOMIZED-ENDYEAR>");
	out.write("<PERSONALIZATION-PRESENT>"+isPersonalizationPresent+"</PERSONALIZATION-PRESENT>");
	out.write("<LHL>"+isLHLPresent+"</LHL>");
	out.write("<FULLTEXT>"+isFullTextPresent+"</FULLTEXT>");
	out.write("<LOCALHOLDINGS>"+isLocalHolidinsPresent+"</LOCALHOLDINGS>");
	out.write("<LOCALHOLDINGS-CITATION>"+ isCitLocalHoldingsPresent+"</LOCALHOLDINGS-CITATION>");
	out.write("<EMAILALERTS-PRESENT>"+isEmailAlertsPresent+"</EMAILALERTS-PRESENT>");
	out.write("<SESSION-ID>");
	out.write(sessionidObj.toString());
	out.write("</SESSION-ID>");
	if(basketSize > 0)
    {
		out.write("<BASKET-NAVIGATION>");
		out.write("<PAGE-INDEX>");
		out.write(Integer.toString(index));
		out.write("</PAGE-INDEX>");
		out.write("<RESULTS-PER-PAGE>50</RESULTS-PER-PAGE>");
		out.write("<RESULTS-COUNT>");
		out.write(Integer.toString(basketSize));
		out.write("</RESULTS-COUNT>");
		out.write("<PREV-PAGE-ID>");
		out.write(Integer.toString(index - 1));
		out.write("</PREV-PAGE-ID>");
		out.write("<CURR-PAGE-ID>");
		System.out.println("Current page:"+Integer.toString(index));
		out.write(Integer.toString(index));
		out.write("</CURR-PAGE-ID>");
		out.write("<NEXT-PAGE-ID>");
		out.write(Integer.toString(index + 1));
		out.write("</NEXT-PAGE-ID>");
		out.write("</BASKET-NAVIGATION>");
		basketPage.setlocalHolding(localHolding);
		basketPage.toXML(out);
	}
	else
	{
		out.write("<BASKET-NAVIGATION>");
		out.write("<RESULTS-COUNT>0");
		out.write("</RESULTS-COUNT>");
		out.write("</BASKET-NAVIGATION>");
	}
	databaseConfig.toXML(credentials, out);
	out.write("</PAGE>");
	out.flush();
%>