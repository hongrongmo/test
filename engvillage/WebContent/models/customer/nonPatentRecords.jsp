<%@page import="org.engvillage.biz.controller.ClientCustomizer"%>
<%@ page language="java" %>
<%@ page session="false" %>
<!--
 * This page the follwing params as input and generates XML output.
 * @param java.lang.String.database
 * @param java.lang.String.totalDocCount
 * @param java.lang.String.sessionId
 * @param java.lang.String.searchID
 -->
<!-- import statements of Java packages-->
<%@ page import="java.util.*"%>
<%@ page import="java.io.FileWriter"%>
<%@ page import="java.net.URLDecoder"%>
<%@ page import="java.net.URLEncoder"%>
<!--import statements of ei packages.-->
<%@ page import="org.engvillage.biz.controller.ControllerClient"%>
<%@ page import="org.engvillage.biz.controller.UserSession"%>
<%@ page import="org.ei.domain.personalization.*"%>
<%@ page import="org.ei.domain.*" %>
<%@ page import="org.ei.config.*"%>
<%@ page import="org.ei.parser.base.*"%>
<%@ page import="org.ei.query.base.*"%>
<%@ page import="org.ei.domain.personalization.GlobalLinks"%>
<%@ page import="org.ei.domain.personalization.SavedSearches"%>
<%@ page import="org.ei.domain.Searches"%>
<%@ page import="org.ei.data.upt.runtime.*"%>
<%@page import="org.ei.exception.EVBaseException"%>
<%@page import="org.ei.exception.SystemErrorCodes"%>
<%@page import="org.ei.exception.SearchException"%>
<%@ page errorPage="/error/errorPage.jsp"%>
<%@ page buffer="20kb"%>
<%
	SearchControl sc = null;
    PageEntry entry =null;
    EIDoc curDoc =null;
    DatabaseConfig databaseConfig = DatabaseConfig.getInstance();

    String sessionId=null;

    String pUserId = null;
    boolean personalization = false;

    String searchID=null;
    String database=null;
    String format=null;
    String cid=null;

    String currentRecord=null;
    String resultsPage=null;

    ClientCustomizer clientCustomizer=null;
    boolean isPersonalizationPresent=true;
    boolean isLHLPresent=true;
    boolean isFullTextPresent=true;
    boolean isBlogLinkPresent=false;
    boolean isLocalHolidinsPresent=true;
    String customizedLogo="";

    ControllerClient client = new ControllerClient(request, response);
    UserSession ussession=(UserSession)client.getUserSession();
    sessionId=ussession.getSessionid();
    pUserId = ussession.getUserid();
    if((pUserId != null) && (pUserId.trim().length() != 0))
    {
        personalization=true;
    }

    String[] credentials = ussession.getCartridge();
    boolean hasCompendex = false;
    boolean hasInspec = false;
    for(int i=0; i<credentials.length; i++)
    {
      if("ins".equalsIgnoreCase(credentials[i]))
      {
        hasInspec = true;
      }
      else if("cpx".equalsIgnoreCase(credentials[i]))
      {
        hasCompendex = true;
      }
    }

try{
    String customerId=ussession.getCustomerid().trim();

    clientCustomizer=new ClientCustomizer(ussession);
    isFullTextPresent=clientCustomizer.checkFullText();
    isBlogLinkPresent=clientCustomizer.checkBlogLink();

    if(clientCustomizer.isCustomized())
    {
        isPersonalizationPresent=clientCustomizer.checkPersonalization();
        isLHLPresent=clientCustomizer.checkDDS();
        isLocalHolidinsPresent=clientCustomizer.checkLocalHolding();
        customizedLogo=clientCustomizer.getLogo();
    }

    currentRecord=request.getParameter("DOCINDEX");
    resultsPage=request.getParameter("PAGEINDEX");
    searchID=request.getParameter("SEARCHID");

    if(request.getParameter("format")!=null)
    {
        format=request.getParameter("format").trim();
        cid = format.trim();
    }

    if(request.getParameter("CID")!=null)
    {
        cid=request.getParameter("CID").trim();
    }

    if(request.getParameter("database")!=null)
    {
        database=request.getParameter("database").trim();
    }

    String docid = null;

    if(request.getParameter("docid")!=null)
    {
        docid =request.getParameter("docid").trim();
    }

    if(currentRecord == null )
    {
        currentRecord = "1";
    }

    int index=Integer.parseInt(currentRecord);

    StringBuffer backurl = new StringBuffer();
    backurl.append("CID=").append(request.getParameter("CID")).append("&");
    backurl.append("SEARCHID=").append(request.getParameter("SEARCHID")).append("&");
    backurl.append("DOCINDEX=").append(request.getParameter("DOCINDEX")).append("&");
    backurl.append("PAGEINDEX=").append(request.getParameter("PAGEINDEX")).append("&");
    backurl.append("database=").append(request.getParameter("database")).append("&");
    backurl.append("format=").append(request.getParameter("format"));

    Query tQuery = Searches.getSearch(searchID);
    if(tQuery == null)
    {
        tQuery = SavedSearches.getSearch(searchID);
    }

    if(tQuery != null)
    {
      tQuery.setSearchQueryWriter(new FastQueryWriter());
      tQuery.setDatabaseConfig(databaseConfig);
      tQuery.setCredentials(ussession.getCartridge());
    }


    StringBuffer srurl = new StringBuffer();

    if(request.getAttribute("SEARCH_RESULTS_URL") == null)
    {
      srurl = getsrURL(index,
               searchID,
               database,
               tQuery);
    }
    else
    {
      srurl = (StringBuffer)request.getAttribute("SEARCH_RESULTS_URL");
    }

	StringBuffer dedupurl = null;
	if(request.getAttribute("DEDUP_RESULTS_URL") != null)
	{
		dedupurl = (StringBuffer)request.getAttribute("DEDUP_RESULTS_URL");
	}

    StringBuffer nsurl = new StringBuffer();
    if(request.getAttribute("NEW_SEARCH_URL") == null)
    {
      nsurl = getnsURL(database,
               tQuery);
    }
    else
    {
      nsurl = (StringBuffer)request.getAttribute("NEW_SEARCH_URL");
    }

    StringBuffer absurl = new StringBuffer();
    if(request.getAttribute("ABS_URL") == null)
    {
      absurl = getabsURL(tQuery,
                 index,
                 searchID,
                 database);
    }
    else
    {
      absurl = (StringBuffer)request.getAttribute("ABS_URL");
    }

    StringBuffer deturl = new StringBuffer();
    if(request.getAttribute("DET_URL") == null)
    {
      deturl = getdetURL(tQuery,
                 index,
                 searchID,
                 database);
    }
    else
    {
      deturl = (StringBuffer)request.getAttribute("DET_URL");
    }


    StringBuffer patrefurl = new StringBuffer();
    if(request.getAttribute("PATREF_URL") == null)
    {
      patrefurl = getpatrefURL(tQuery,
                       index,
                       searchID,
                       database,
                       docid);
    }
    else
    {
      patrefurl = (StringBuffer)request.getAttribute("PATREF_URL");
    }

    UPTDocBuilder docBuilder = new UPTDocBuilder();

    List list = new ArrayList();
    list.add(new DocID(docid,new UPTDatabase()));
    List eiDocs = docBuilder.buildPage(list,Citation.CITATION_FORMAT);
    curDoc = (EIDoc)eiDocs.get(0);

    UPTRefDocBuilder refBuilder = new UPTRefDocBuilder(new UPTRefDatabase());

    List refDocs = refBuilder.buildNonPatentRefs(docid);
    int refCount = refDocs.size();
    String strGlobalLinksXML = GlobalLinks.toXML(ussession.getCartridge());

    out.write("<PAGE>");
    out.write("<HIDE-NAV/>");
    out.write("<PAGE-NAV>");
    out.write("<ABS-NAV>");
    out.write(absurl.toString());
    out.write("</ABS-NAV>");
    out.write("<DET-NAV>");
    out.write(deturl.toString());
    out.write("</DET-NAV>");
    out.write("<PATREF-NAV>");
    out.write(patrefurl.toString());
    out.write("</PATREF-NAV>");
    out.write("<RESULTS-NAV><![CDATA[");
    out.write(srurl.toString());
    out.write("]]></RESULTS-NAV>");
	if(dedupurl != null)
	{
		out.write("<DEDUP-RESULTS-NAV>");
		out.write(dedupurl.toString());
		out.write("</DEDUP-RESULTS-NAV>");
	}
    out.write("<NEWSEARCH-NAV>");
    out.write(nsurl.toString());
    out.write("</NEWSEARCH-NAV>");
    out.write("</PAGE-NAV>");
    out.write("<BACKURL>");
    out.write(URLEncoder.encode(backurl.toString()));
    out.write("</BACKURL>");
    out.write("<HEADER/>");
    out.write("<ABSTRACT-DETAILED-NAVIGATION-BAR/>");
    out.write("<DBMASK>");
    out.write(database);
    out.write("</DBMASK>");
    out.write("<FOOTER/>");
    out.write(strGlobalLinksXML);
    out.write("<ABSTRACT-DETAILED-RESULTS-MANAGER/>");
    out.write("<SEARCH/>");
    out.write("<SESSION-TABLE/>");
    out.write("<CID>"+cid+"</CID>");
    out.write("<CUSTOMER-ID>"+customerId+"</CUSTOMER-ID>");
    out.write("<PERSONALIZATION-PRESENT>"+isPersonalizationPresent+"</PERSONALIZATION-PRESENT>");
    out.write("<LHL>"+isLHLPresent+"</LHL>");
    out.write("<BLOGLINK>"+isBlogLinkPresent+"</BLOGLINK>");
    out.write("<CUSTOMIZED-LOGO>"+customizedLogo+"</CUSTOMIZED-LOGO>");
    out.write("<FULLTEXT>"+isFullTextPresent+"</FULLTEXT>");
    out.write("<LOCALHOLDINGS>"+isLocalHolidinsPresent+"</LOCALHOLDINGS>");
    out.write("<DATABASE>"+database+"</DATABASE>");
    out.write("<RESULTS-COUNT>1</RESULTS-COUNT>");
    out.write("<NP-COUNT>"+refCount+"</NP-COUNT>");
    out.write("<SESSION-ID>"+sessionId+"</SESSION-ID>");
    out.write("<PERSONALIZATION>"+personalization+"</PERSONALIZATION>");
    out.write("<SEARCH-ID>"+searchID+"</SEARCH-ID>");
    out.write("<PAGE-INDEX>"+resultsPage+"</PAGE-INDEX>");
    out.write("<DOCINDEX>"+currentRecord+"</DOCINDEX>");
    out.write("<PREV-PAGE-ID>"+(index-1)+"</PREV-PAGE-ID>");
    out.write("<CURR-PAGE-ID>"+index+"</CURR-PAGE-ID>");
    out.write("<NEXT-PAGE-ID>"+(index+1)+"</NEXT-PAGE-ID>");
    out.write("<PAGE-RESULTS>");
    databaseConfig.toXML(credentials, out);
    out.write("<UPT-DOC>");
    curDoc.toXML(out);

    out.write("<REF-DOCS>");
    for(int i=0; i<refDocs.size(); i++)
    {
      out.write("<PAGE-ENTRY>");
      NonPatentRef nonPatRef = (NonPatentRef)refDocs.get(i);
      if(!hasCompendex)
      {
        nonPatRef.setCpxDocId(null);
      }

      if(!hasInspec)
      {
        nonPatRef.setInsDocId(null);
      }
      nonPatRef.toXML(out);
      out.write("</PAGE-ENTRY>");
    }

    out.write("</REF-DOCS>");
    out.write("</UPT-DOC>");
    out.write("</PAGE-RESULTS>");
    if(tQuery != null)
    {
      out.write(tQuery.toXMLString());
    }
    else
    {
      out.write("<SESSION-DATA><SEARCH-TYPE>Tags</SEARCH-TYPE></SESSION-DATA>");
    }

    out.write("</PAGE>");
    out.write("<!--END-->");
    out.flush();
    } catch (Exception e) {
        EVBaseException be = null;
        if (e instanceof EVBaseException) {
            be = (EVBaseException) e;
        } else {
            be = new SearchException(SystemErrorCodes.UNKNOWN_SEARCH_ERROR, e);
        }
        client.setException("true");
        client.setRemoteControl();
        out.write(be.toXML());
        out.flush();

        return;
    }
%><%!StringBuffer getsrURL(int recnum,
						  String searchID,
						  String database,
						  Query tQuery)
	{
		StringBuffer srurlbuf = new StringBuffer();
		srurlbuf.append("CID=").append(XSLCIDHelper.searchResultsCid(tQuery.getSearchType())).append("&");
		srurlbuf.append("SEARCHID=").append(searchID).append("&");
		srurlbuf.append("COUNT=").append(Integer.toString((recnum))).append("&");
		srurlbuf.append("database=").append(database);
		return srurlbuf;
	}

	private StringBuffer getnsURL(String database,Query tQuery)
	{
		StringBuffer nsurlbuf = new StringBuffer();
		nsurlbuf.append("CID=").append(XSLCIDHelper.newSearchCid(tQuery.getSearchType())).append("&amp;");
		nsurlbuf.append("database=").append(database);
		return nsurlbuf;
	}



	private StringBuffer getpatrefURL(Query tQuery, int recnum,String searchID,String database, String docID)
	{
		StringBuffer patrefbuf = new StringBuffer();
		patrefbuf.append("CID=").append(XSLCIDHelper.formatBase(tQuery.getSearchType())+"ReferencesFormat").append("&amp;");
		patrefbuf.append("SEARCHID=").append(searchID).append("&amp;");
		patrefbuf.append("DOCINDEX=").append(Integer.toString((recnum))).append("&amp;");
		patrefbuf.append("database=").append(database).append("&amp;");
		patrefbuf.append("docid=").append(docID).append("&amp;");
		patrefbuf.append("format=").append("ReferencesFormat");
		return patrefbuf;
	}


	private StringBuffer getabsURL(Query tQuery,
								   int recnum,
								   String searchID,
								   String database)
	{
		StringBuffer absurlbuf = new StringBuffer();
		absurlbuf.append("CID=").append(XSLCIDHelper.formatBase(tQuery.getSearchType())+"AbstractFormat").append("&amp;");
		absurlbuf.append("SEARCHID=").append(searchID).append("&amp;");
		absurlbuf.append("DOCINDEX=").append(Integer.toString((recnum))).append("&amp;");
		absurlbuf.append("database=").append(database).append("&amp;");
		absurlbuf.append("format=").append(XSLCIDHelper.formatBase(tQuery.getSearchType())+"AbstractFormat");
		return absurlbuf;
	}

	private StringBuffer getdetURL(Query tQuery,
								   int recnum,
								   String searchID,
								   String database)
	{
		StringBuffer deturlbuf = new StringBuffer();
		deturlbuf.append("CID=").append(XSLCIDHelper.formatBase(tQuery.getSearchType())+"DetailedFormat").append("&amp;");
		deturlbuf.append("SEARCHID=").append(searchID).append("&amp;");
		deturlbuf.append("DOCINDEX=").append(Integer.toString(recnum)).append("&amp;");
		deturlbuf.append("database=").append(database).append("&amp;");
		deturlbuf.append("format=").append(XSLCIDHelper.formatBase(tQuery.getSearchType())+"DetailedFormat");
		return deturlbuf;
	}%>