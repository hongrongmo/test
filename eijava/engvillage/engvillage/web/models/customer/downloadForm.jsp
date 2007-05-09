<!-- This file creates the xml for result coming from serach result page or from
     selected set page.
-->

<%@ page language="java" %>
<%@ page session="false" %>

<!-- import statements of Java packages-->
<%@ page  import=" java.util.*"%>
<%@ page  import=" java.net.*"%>

<!--import statements of ei packages.-->
<%@ page import="org.ei.controller.ControllerClient"%>
<%@ page import="org.ei.session.*"%>
<%@ page import ="org.ei.domain.*"%>
<%@ page import ="org.ei.domain.personalization.*"%>
<%@ page import ="org.ei.config.*"%>


<%@ page errorPage="/error/errorPage.jsp" %>
<!--setting page buffer size to 20kb-->
<%@ page buffer="20kb"%>


<%
    // This variable for sessionId
    String sessionId="";
    SessionID sessionIdObj = null;
    // This variable for searchId
    String searchId="";

    // This variable for output displayFormat-abstract or detailed
    String displayFormat="";
    // This variable for document ids
    String docids="";
    // This variable for index
    String handles="";
    // This variable for page number of selected set result page
    int basketCount=0;
    // This variable is for if request is coming from selected set results
    String selectedSet="";
    // This variable is for if request is coming for single abstract or full records of page
    String absfullselected="";
    // This variable for page size
    int docBasketPageSize=0;
    // Object reference to BasketPage object
    BasketPage basketPage = null;
    // Object reference to DocumentBasket object
    DocumentBasket basket = null;
    // This variable that is used to calculate the Docuemnt Basket page that is to be shown when the user clicks either the View Selected Records/Record basket link
    int index = 0;
    // This variable to hold the format type
    String displaytype="";
    // This variable to hold the string for output xml
    StringBuffer xmlString=null;
    // This variable is for if request is coming from saved records
    String savedRecords="";
    // This variable to hold the folder id
    String strFolderId="";
    // This variable to hold the folder id

    // This variable to hold the current User id
    String sUserId="";
    // This variable to hold the current User id
    String userId = null;
    //This variable is for holding docis List
    List docidList=null;

    // This variable is for if all databases are Scirus
    String selectedDB="false";
    List docBasketDatabase = null;
    int databaseCount = 0;

    // This variable is used to hold ControllerClient instance
     ControllerClient client = new ControllerClient(request, response);


  /**
   *  Getting the UserSession object from the Controller client .
   *  Getting the session id from the usersession.
   */
    UserSession ussession=(UserSession)client.getUserSession();
    sessionId=ussession.getID();
    sessionIdObj = ussession.getSessionID();

    String baseAddress = ussession.getProperty("ENV_BASEADDRESS");

    sUserId=ussession.getProperty("P_USER_ID");
    //client.updateUserSession(ussession);

    if( sUserId != null)
    {
        userId = sUserId;
    }

    RuntimeProperties runtimeProps = ConfigService.getRuntimeProperties();
    String pageSize = runtimeProps.getProperty("BASKETPAGESIZE");
    docBasketPageSize = Integer.parseInt(pageSize.trim());

    if(request.getParameter("displayformat") != null)
    {
        displaytype = request.getParameter("displayformat");
    }
    if(request.getParameter("selectedset") !=null)
    {
        selectedSet = request.getParameter("selectedset");
    }
    if(request.getParameter("savedrecords") !=null)
    {
        savedRecords = request.getParameter("savedrecords");
    }
    if(request.getParameter("absfullselected") !=null)
    {
        absfullselected = request.getParameter("absfullselected");
    }

    basket = new DocumentBasket(sessionId);
    basketCount = basket.getBasketSize();

    if(selectedSet.equals("true"))
    {

        if(request.getParameter("displayformat") != null)
        {
            displaytype = request.getParameter("displayformat");
        }

        xmlString = new StringBuffer("<PAGE><SESSION-ID>"+sessionIdObj.toString()+"</SESSION-ID>");
        xmlString.append("<SEARCH-ID>"+searchId+"</SEARCH-ID>");
        xmlString.append("<BASE-ADDRESS>");
        xmlString.append(baseAddress);
        xmlString.append("</BASE-ADDRESS>");

    basket = new DocumentBasket(sessionId);
    if(displaytype.equals("citation"))
    {
      displayFormat=Citation.CITATION_FORMAT;
    }
    else if(displaytype.equals("abstract"))
    {
      displayFormat=Abstract.ABSTRACT_FORMAT;
    }
    else if(displaytype.equals("detailed"))
    {
      displayFormat=FullDoc.FULLDOC_FORMAT;
    }

    xmlString.append("<DISPLAY-FORMAT>"+displayFormat+"</DISPLAY-FORMAT>");
    xmlString.append("<DOCID-LIST><DOC>W</DOC>");

    xmlString.append("</DOCID-LIST>");

    xmlString.append("<DB-COUNT>"+databaseCount+"</DB-COUNT>");
    xmlString.append("<SELECTED-SET>"+selectedSet+"</SELECTED-SET>");
    xmlString.append("</PAGE>");
    out.write(xmlString.toString());

    out.flush();

  }
    else if(savedRecords.equals("true"))
    {

    if(request.getParameter("folderid") != null)
    {
        strFolderId = request.getParameter("folderid").trim();
    }
    if(request.getParameter("displayformat") != null)
    {
        displaytype = request.getParameter("displayformat");
    }


    xmlString = new StringBuffer("<PAGE><SESSION-ID>"+sessionIdObj.toString()+"</SESSION-ID>");
    xmlString.append("<BASE-ADDRESS>");
    xmlString.append(baseAddress);
    xmlString.append("</BASE-ADDRESS>");

    //checking for display type and setting it to the required final string
    if(displaytype.equals("citation"))
    {
      displayFormat=Citation.CITATION_FORMAT;
    }
    else if(displaytype.equals("abstract"))
    {
      displayFormat=Abstract.ABSTRACT_FORMAT;
    }
    else if(displaytype.equals("detailed"))
    {
      displayFormat=FullDoc.FULLDOC_FORMAT;
    }

    xmlString.append("<DISPLAY-FORMAT>"+displayFormat+"</DISPLAY-FORMAT>");
    xmlString.append("<DOCID-LIST><DOC>W</DOC>");
    xmlString.append("</DOCID-LIST>");
    xmlString.append("<SELECTED-SETDB>false</SELECTED-SETDB>");
    xmlString.append("<SAVED-RECORDS>"+savedRecords+"</SAVED-RECORDS>");
    xmlString.append("<FOLDER-ID>"+strFolderId+"</FOLDER-ID>");
    xmlString.append("</PAGE>");

        out.write(xmlString.toString());
        out.write("<!--END-->");
        out.flush();

  }
    else if(absfullselected.equals("true"))
    {
        // when looking at a single record in Abstract or Detailed
        // format and dowload is chosen,
        // we need to have the right database name
        // or else the RIS format will not show up!

        if(request.getParameter("searchid") != null)
        {
            searchId = request.getParameter("searchid");
        }

        if(request.getParameter("displayformat") != null)
        {
            displayFormat = request.getParameter("displayformat");
        }

        if(request.getParameter("docidlist") !=null)
        {
            docids = request.getParameter("docidlist");
        }

        if(request.getParameter("handlelist")!=null)
        {
            handles = request.getParameter("handlelist");
        }

        // Writing the XML
        // Since this is a single document and we know this code
        // is going to downloadForm.xsl
        // we can write the actual db for this single record
        // instead of combined
        out.write("<PAGE>");
        out.write("<SESSION-ID>"+sessionIdObj.toString()+"</SESSION-ID>");
        out.write("<SEARCH-ID>"+searchId+"</SEARCH-ID>");
        out.write("<BASE-ADDRESS>");
        out.write(baseAddress);
        out.write("</BASE-ADDRESS>");
        out.write("<DISPLAY-FORMAT>"+displayFormat+"</DISPLAY-FORMAT>");
        out.write("<DOCID-LIST>"+docids+"</DOCID-LIST>");
        out.write("<HANDLES>"+handles+"</HANDLES>");
        out.write("</PAGE>");
        out.write("<!--END-->");
    }
    else if(basketCount!=0)
    {
    xmlString = new StringBuffer("<PAGE><SESSION-ID>"+sessionIdObj.toString()+"</SESSION-ID>");
        xmlString.append("<BASE-ADDRESS>");
        xmlString.append(baseAddress);
        xmlString.append("</BASE-ADDRESS>");

    if( (displaytype.equals("citationSelectedSet")) || (displaytype.equals("citation")) )
    {
      displayFormat=Citation.CITATION_FORMAT;
    }
    else if( (displaytype.equals("abstractSelectedSet")) || (displaytype.equals("abstract")) )
    {
      displayFormat=Abstract.ABSTRACT_FORMAT;
    }
    else if( (displaytype.equals("detailedSelectedSet")) || (displaytype.equals("detailed")) )
    {
      displayFormat=FullDoc.FULLDOC_FORMAT;
    }

    xmlString.append("<DISPLAY-FORMAT>"+displayFormat+"</DISPLAY-FORMAT>");
        xmlString.append("<DOCID-LIST><DOC>W</DOC>");
        xmlString.append("</DOCID-LIST>");
        xmlString.append("<DB-COUNT>"+databaseCount+"</DB-COUNT>");
        xmlString.append("<ALL-SELECTED>true</ALL-SELECTED>");

        xmlString.append("</PAGE>");
        out.write(xmlString.toString());
        out.write("<!--END-->");
        out.flush();

  }
    else
    {
    xmlString = new StringBuffer("<PAGE><SESSION-ID>"+sessionIdObj.toString()+"</SESSION-ID>");
    xmlString.append("<ERROR-PAGE>true</ERROR-PAGE>");
    xmlString.append("</PAGE>");
    out.write(xmlString.toString());
    out.write("<!--END-->");
    out.flush();

  }

%>

