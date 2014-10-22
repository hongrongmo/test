<%--
* jsp file that is required to view records in a folder
* to the savetofolder.xsl */
* @param java.lang.String.folderid
* @param java.lang.String.cid
* @param java.lang.String.databse
--%>
<%@page import="org.engvillage.biz.controller.ClientCustomizer"%>
<%@ page language="java" %>
<%@ page session="false" %>
<%-- import statements of Java packages--%>
<%@ page import="java.util.*"%>
<%@ page import="java.net.*"%>
<%--import statements of ei packages.--%>
<%@ page import="org.ei.domain.personalization.*"%>
<%@ page import="org.ei.config.*"%>
<%@ page import="org.ei.domain.*"%>
<%@ page import="org.engvillage.biz.controller.ControllerClient"%>
<%@ page import="org.engvillage.biz.controller.UserSession" %>
<%@ page import="org.ei.domain.personalization.GlobalLinks"%>
<%@ page import="org.ei.domain.Searches "%>

<%@ page errorPage="/error/errorPage.jsp" %>
<%--setting page buffer size to 20kb--%>
<%@ page buffer="20kb"%>
<%
    DatabaseConfig databaseConfig = DatabaseConfig.getInstance();
    SavedRecords savedRecords = null;
    ControllerClient client = null;
    ClientCustomizer clientCustomizer=null;
    LocalHolding localHolding=null;
    FolderPage folderPage = null;
    Folder folder=null;

    String cid=null;
    String database=null;
    String urlString=null;
    String documentFormat=null;
    String sessionid=null;
    String folderId="";
    String redirect=null;
    String customizedLogo="";

    boolean flag = false ;
    boolean personalization = false;
    boolean isPersonalizationPresent=true;
    boolean isLHLPresent=true;
    boolean isFullTextPresent=true;
    boolean isLocalHolidinsPresent=true;
    boolean isCitLocalHoldingsPresent=false;
    String searchresults=null;
    String newsearch=null;
    String docindex="0";
    String format=null;

    int folderSize = 0 ;

    client = new ControllerClient(request,response);
    UserSession ussession = (UserSession)client.getUserSession();

    sessionid = ussession.getSessionid();
    String[] credentials = ussession.getCartridge();

    // should we add Searches.getMostRecentInSession() method?
    // to avoid returning entire list from folder?
    String recentXMLQuery = null;

    // is this needed to link back to search results (?)
/*
    List sessionsearches = Searches.getListSessionSearches(sessionid);
    if(sessionsearches != null)
    {

        if(sessionsearches.size() > 0)
        {
            Query query = (Query) sessionsearches.get(0);
            recentXMLQuery = query.toXMLString();
        }
        // free up object(s)
        sessionsearches.clear();
    }
*/
    // jam - when no query object generates output XML,
    // add <SEARCH-TYPE> to output. When <SEARCH-TYPE> is absent
    // links are prevented from showing in HTML (PRINTING)
    if(recentXMLQuery == null)
    {
        recentXMLQuery = "<SESSION-DATA><DATABASE-MASK>1</DATABASE-MASK><SEARCH-TYPE>NONE</SEARCH-TYPE></SESSION-DATA>";
    }

    String customerId=ussession.getCustomerid().trim();
    String contractId=ussession.getContractid().trim();

    localHolding=new LocalHolding(ussession.getProperty(UserSession.LOCAL_HOLDING_KEY));
    clientCustomizer=new ClientCustomizer(ussession);

    if(request.getParameter("CID") != null)
    {
        cid=request.getParameter("CID").trim();
    }

    if(clientCustomizer.isCustomized())
    {
        isPersonalizationPresent=clientCustomizer.checkPersonalization();
        isLHLPresent=clientCustomizer.checkDDS();
        isFullTextPresent=clientCustomizer.checkFullText("citationResults");

        //There is no fence setting for abstract and detailed view
        if( (cid!=null) && ( (cid.equals("viewAbstractSavedRecords")) || (cid.equals("viewDetailedSavedRecords")) ) ){
        	isCitLocalHoldingsPresent = true;
        }else{
           	isCitLocalHoldingsPresent=clientCustomizer.checkLocalHolding("citationResults");
        }
        isLocalHolidinsPresent=clientCustomizer.checkLocalHolding();
        customizedLogo=clientCustomizer.getLogo();
        String startingPage=clientCustomizer.getStartPage();
    }
    // Varaible to hold the current User id
    String userId = null;
    String sUserId=null;

    sUserId=ussession.getUserid();
    if((sUserId != null) && (sUserId.trim().length() != 0))
    {
        personalization=true;
    }

    // Retrieve all the request parameters
    if(request.getParameter("redirect") != null)
    {
        redirect=request.getParameter("redirect").trim();
    }


    if(request.getParameter("folderid") != null)
    {

        folderId=request.getParameter("folderid").trim();
    }

    if(request.getParameter("database") != null)
    {
        database=request.getParameter("database").trim();
    }


    if(request.getParameter("searchresults") != null)
    {
    	searchresults=request.getParameter("searchresults").trim();
    }else{
    	searchresults="searchresults";
    }

    if(request.getParameter("newsearch") != null)
    {
    	newsearch=request.getParameter("newsearch").trim();
    }else{
    	newsearch="newsearch";
    }

    if( request.getParameter("DOCINDEX") != null)
	{
		docindex=request.getParameter("DOCINDEX").trim();

	}

    if( request.getParameter("format") != null)
   	{
    	format=request.getParameter("format").trim();

   	}

  /*
    DO NOT do this anymore - start and End years will be calculated when requests come
    into search pages

    if(("Compendex").equalsIgnoreCase(database))
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
*/

    savedRecords = new SavedRecords();
    folder=savedRecords.getFolder(folderId);
    folderSize = savedRecords.getSizeOfFolder(folder);

    /*
    *  Gets the results for the current page
    */

    if( (cid!=null) && ( (cid.equals("viewCitationSavedRecords")) ||
    (cid.equals("printCitationSavedRecords")) ) )
    {
        documentFormat=Citation.CITATION_FORMAT;
    }
    else if( (cid!=null) && ( (cid.equals("viewAbstractSavedRecords")) ||
    (cid.equals("printAbstractSavedRecords")) ) )
    {
        documentFormat=Abstract.ABSTRACT_FORMAT;
    }
    else if( (cid!=null) && ( (cid.equals("viewDetailedSavedRecords"))  || (cid.equals("printDetailedSavedRecords")) ) )
    {
        documentFormat=FullDoc.FULLDOC_FORMAT;
    }

    if(folderSize > 0)
    {
        folderPage = (FolderPage) savedRecords.viewRecordsInFolder(folderId, documentFormat);
    }


    String strGlobalLinksXML = GlobalLinks.toXML(ussession.getCartridge());

    if( (folderSize>0) && (redirect==null))
    {
        // This xml is generated when there are documents in the Folder
        if(cid.startsWith("print"))
        {

            // and printXXXXXXXXX is the CID
            // THIS CID OPERATES IN BULK MODE
            // THIS CID OPERATES IN BULK MODE
            // This is done so that the print function
            // mimics the print function of PrintSelectedRecords.jsp
            // and uses the XSL, PrintXXXXXXFormat.xsl
            StringBuffer  basketContentStringBuffer  = new StringBuffer();
            basketContentStringBuffer.append("<PAGE>");
            basketContentStringBuffer.append("<CID>"+cid+"</CID>");
            basketContentStringBuffer.append("<CUSTOMER-ID>"+customerId+"</CUSTOMER-ID>");

            basketContentStringBuffer.append("<!--BH--><HEADER>")
            .append("<CUSTOMIZED-LOGO>" + customizedLogo + "</CUSTOMIZED-LOGO>")
            .append("</HEADER><!--EH-->");
            //Writing out XML
            out.write(basketContentStringBuffer.toString());

            folderPage.toXML(out);

            out.write("<!--*-->");
            //Signale footer section
            out.write("<!--BF--><FOOTER/><!--EF-->");
            out.write("</PAGE>");
            out.println("<!--END-->");
            out.flush();
        }
        else
        {

            // and viewXXXXXXXXX is the CID
            // THIS CID DOES NOT OPERATE IN BULK MODE
            // THIS CID DOES NOT OPERATE IN BULK MODE
            // uses the XSL CitationSavedRecords.xsl
            StringBuffer  folderContentStringBuffer  = new StringBuffer();
            folderContentStringBuffer.append("<PAGE>")
            .append("<CUSTOMER-ID>"+customerId+"</CUSTOMER-ID>")
            .append("<ERROR/>")
            .append("<HEADER/>")
            .append("<DBMASK>")
            .append(database)
            .append("</DBMASK>")
            .append(strGlobalLinksXML)
            .append("<SAVED-RECORDS-TOP-RESULTS-MANAGER/>")
            .append("<SAVED-RECORDS-BOTTOM-RESULTS-MANAGER/>")
            .append("<FOOTER/>")

            .append("<SESSION-ID>")
            .append(sessionid)
            .append("</SESSION-ID>")
            .append("<CID>"+cid+"</CID>")
            .append("<CUSTOMIZED-LOGO>"+customizedLogo+"</CUSTOMIZED-LOGO>")
            .append("<PERSONALIZATION-PRESENT>"+isPersonalizationPresent+"</PERSONALIZATION-PRESENT>")
            .append("<PERSONALIZATION>")
            .append(personalization)
            .append("</PERSONALIZATION>")
            .append("<LHL>"+isLHLPresent+"</LHL>")
            .append("<FULLTEXT>"+isFullTextPresent+"</FULLTEXT>")
            .append("<LOCALHOLDINGS>"+isLocalHolidinsPresent+"</LOCALHOLDINGS>")
            .append("<LOCALHOLDINGS-CITATION>"+ isCitLocalHoldingsPresent+"</LOCALHOLDINGS-CITATION>")
//            .append("<CUSTOMIZED-STARTYEAR>"+customizedStartYear+"</CUSTOMIZED-STARTYEAR>")
//            .append("<CUSTOMIZED-ENDYEAR>"+customizedEndYear+"</CUSTOMIZED-ENDYEAR>")
            .append(recentXMLQuery)
            .append("<NEWSEARCHURL>"+ URLEncoder.encode(newsearch)+"</NEWSEARCHURL>")
        	.append("<SEARCHRESULTSURL>"+ URLEncoder.encode(searchresults)+"</SEARCHRESULTSURL>")
        	.append("<DOCINDEX>"+ docindex+"</DOCINDEX>")
        	.append("<FORMAT>"+ format+"</FORMAT>")
            .append("<FOLDER-SIZE>")
            .append(folderSize)
            .append("</FOLDER-SIZE>")
            .append("<FOLDER-ID>")
            .append(folderId)
            .append("</FOLDER-ID>")
            .append("<FOLDER-NAME>")
            .append(folder.getFolderName())
            .append("</FOLDER-NAME>")
			.append("<SEARCH-ID>")
            .append(folderId)
            .append("</SEARCH-ID>")
            .append("<USER-ID>")
            .append(sessionid)
            .append("</USER-ID>");

            out.write(folderContentStringBuffer.toString());
            folderPage.setlocalHolding(localHolding);
            folderPage.toXML(out);

            databaseConfig.toXML(credentials, out);

            out.write("</PAGE>");
            out.println("<!--END-->");
        }

        out.flush();
    }
    else
    {
        // This xml is generated when there are no documents in the Document Basket  -->
        StringBuffer  folderContentStringBuffer  = new StringBuffer();
        folderContentStringBuffer.append("<PAGE>")

        .append("<ERROR>")
        .append("TRUE")
        .append("</ERROR>")
        .append("<HEADER/>")
        .append("<DBMASK>")
        .append(database)
        .append("</DBMASK>")
        .append(strGlobalLinksXML)
        .append("<FOOTER/>")
        .append("<SESSION-ID>")
        .append(sessionid)
        .append("</SESSION-ID>")
        .append("<CID>"+cid+"</CID>")
        .append("<CUSTOMIZED-LOGO>"+customizedLogo+"</CUSTOMIZED-LOGO>")
        .append("<PERSONALIZATION-PRESENT>"+isPersonalizationPresent+"</PERSONALIZATION-PRESENT>")
        .append("<PERSONALIZATION>")
        .append(personalization)
        .append("</PERSONALIZATION>")

        .append("<LHL>"+isLHLPresent+"</LHL>")
        .append("<FULLTEXT>"+isFullTextPresent+"</FULLTEXT>")
        .append("<LOCALHOLDINGS>"+isLocalHolidinsPresent+"</LOCALHOLDINGS>")
        .append("<LOCALHOLDINGS-CITATION>"+ isCitLocalHoldingsPresent+"</LOCALHOLDINGS-CITATION>")
        .append(recentXMLQuery)
        .append("<NEWSEARCHURL>"+ URLEncoder.encode(newsearch)+"</NEWSEARCHURL>")
        .append("<SEARCHRESULTSURL>"+ URLEncoder.encode(searchresults)+"</SEARCHRESULTSURL>")
        .append("<DOCINDEX>"+ docindex+"</DOCINDEX>")
        .append("<FORMAT>"+ format+"</FORMAT>")
        .append("<FOLDER-SIZE>")
        .append(folderSize)
        .append("</FOLDER-SIZE>")
        .append("<FOLDER-ID>")
        .append(folderId)
        .append("</FOLDER-ID>")
        .append("<FOLDER-NAME>")
        .append(folder.getFolderName())
        .append("</FOLDER-NAME>")
        .append("<SEARCH-ID>")
        .append(folderId)
        .append("</SEARCH-ID>")
        .append("<USER-ID>")
        .append(userId)
        .append("</USER-ID>")
        .append("</PAGE>");
        out.write(folderContentStringBuffer.toString());
        out.println("<!--END-->");
        out.flush();

        //urlString = "/controller/servlet/Controller?CID=errorSavedRecords&folderid="+folderId+"&database="+database+"&redirect=true";

        flag=true;
    }
%>
