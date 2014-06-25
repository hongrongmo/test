<!-- This file creates the xml for result coming from serach result page or from
     selected set page.
-->
<%@ page language="java" %>
<%@ page session="false" %>

<!-- import statements of Java packages-->
<%@ page import="java.util.*"%>
<%@ page import="java.net.*"%>

<!--import statements of ei packages.-->
<%@ page import="org.ei.controller.ControllerClient"%>
<%@ page import="org.ei.session.*"%>
<%@ page import="org.ei.domain.personalization.*"%>
<%@ page import="org.ei.domain.*"%>
<%@ page import="org.ei.domain.personalization.*"%>
<%@ page import="org.ei.domain.Searches"%>
<%@ page import="org.ei.config.*"%>

<%@ page errorPage="/error/errorPage.jsp" %>
<!--setting page buffer size to 20kb-->
<%@ page buffer="20kb"%>


<%
    // This variable for sessionId
    String sessionId="";
    SessionID sessionIdObj = null;
    // This variable for searchid
    String searchid="";
    // This variable for database name
    String database="";
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
    // This variable to hold the string for output xml
    StringBuffer xmlString=null;
    // This variable is for if request is coming from saved records
    String savedRecords="";
    // This variable to hold the folder id
    String folderId="";
    // This variable to hold the current User id
    String sUserId="";
    // This variable to hold the current User id
    String userId = null;
    //This variable is for holding docis List
    List docidList=null;
    // Hashtable for database name
    Hashtable ht = new Hashtable();
    // This variable is for if all databases are Scirus
    String selectedDB="false";
    List docBasketDatabase = null;
    int databaseCount = 0;
    // Variable to hold the reference to searchQuery
    String searchQuery=null;

    // This variable is used to hold ControllerClient instance
     ControllerClient client = new ControllerClient(request, response);


   /**
     *  Getting the UserSession object from the Controller client .
     *  Getting the session id from the usersession.
     */
    UserSession ussession=(UserSession)client.getUserSession();
    sessionId=ussession.getID();
    sessionIdObj = ussession.getSessionID();

    sUserId=ussession.getUserIDFromSession();
    //client.updateUserSession(ussession);


    if( sUserId != null)
    {
        userId = sUserId;
    }

    String pageSize=null;
    RuntimeProperties runtimeProps= ConfigService.getRuntimeProperties();
    pageSize = runtimeProps.getProperty("BASKETPAGESIZE");
    docBasketPageSize=Integer.parseInt(pageSize.trim());

    if(request.getParameter("database") != null)
    {
        database = request.getParameter("database");
    }

    if(request.getParameter("displayformat") != null)
    {
        displayFormat = request.getParameter("displayformat");
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

        long beginTime = System.currentTimeMillis();

        if(request.getParameter("database") != null)
        {
            database = request.getParameter("database");
        }

        xmlString = new StringBuffer("<PAGE><SESSION-ID>"+sessionIdObj.toString()+"</SESSION-ID>");
        xmlString.append("<SEARCH-ID>"+searchid+"</SEARCH-ID>");
        xmlString.append("<DATABASE>"+database+"</DATABASE>");
        xmlString.append("<SEARCH-QUERY>");

        Query query = Searches.getSearch(searchid);
        if(query != null)
        {
            String temp=query.getDisplayQuery();
            int k=temp.indexOf('&',0);
            String temp1="";
            if(k>0){
                temp1=temp.substring(0,k)+"&#38;"+temp.substring(k+1,temp.length());
            }else{
                temp1=temp;
            }
            xmlString.append("Engineering Village Search: ");
            xmlString.append("["+temp1+"]");
        }


        xmlString.append("</SEARCH-QUERY>");


         //checking for display type and setting it to the required final string
         if(displayFormat.equals("citation"))
         {
             displayFormat=Citation.CITATION_FORMAT;
         }
         else if(displayFormat.equals("abstract"))
         {
             displayFormat=Abstract.ABSTRACT_FORMAT;
         }
         else if(displayFormat.equals("detailed"))
         {
             displayFormat=FullDoc.FULLDOC_FORMAT;
         }

        xmlString.append("<DISPLAY-FORMAT>"+displayFormat+"</DISPLAY-FORMAT>");
        xmlString.append("<DOCID-LIST><DOC>W</DOC>");
        xmlString.append("</DOCID-LIST>");

        xmlString.append("<DB-LIST>");
        docBasketDatabase = basket.getListOfDatabases();
        databaseCount=docBasketDatabase.size();


        String dbflag="false";
        Iterator iter = docBasketDatabase.iterator();
        while(iter.hasNext())
        {
            String databaseName=(String)iter.next();

            if(databaseName.equals("COM"))
            {
                if(docBasketDatabase.contains("cpx"))
                {
                    //do nothing
                }
                else
                {
                    databaseName="cpx";
                    dbflag="true";
                }
            }
            else
            {
                dbflag="true";
            }
            if(dbflag.equals("true"))
            {
                xmlString.append("<DB-NAME>");
                xmlString.append(databaseName);
                xmlString.append("</DB-NAME>");
            }
        }
        xmlString.append("</DB-LIST>");
        xmlString.append("<DB-COUNT>"+databaseCount+"</DB-COUNT>");
        xmlString.append("<SELECTED-SET>"+selectedSet+"</SELECTED-SET>");
        xmlString.append("</PAGE>");
        out.write(xmlString.toString());
        out.write("<!--END-->");
        out.flush();

    }
    else if(savedRecords.equals("true"))
    {

        if(request.getParameter("folderid") != null)
        {
            folderId = request.getParameter("folderid").trim();
        }


        xmlString = new StringBuffer("<PAGE><SESSION-ID>"+sessionIdObj.toString()+"</SESSION-ID>");

         if(displayFormat.equals("citation"))
         {
             displayFormat=Citation.CITATION_FORMAT;
         }
         else if(displayFormat.equals("abstract"))
         {
             displayFormat=Abstract.ABSTRACT_FORMAT;
         }
         else if(displayFormat.equals("detailed"))
         {
             displayFormat=FullDoc.FULLDOC_FORMAT;
         }

        xmlString.append("<DISPLAY-FORMAT>"+displayFormat+"</DISPLAY-FORMAT>");
        xmlString.append("<DOCID-LIST><DOC></DOC>");
        xmlString.append("</DOCID-LIST>");
        xmlString.append("<SELECTED-SETDB>false</SELECTED-SETDB>");
        xmlString.append("<SAVED-RECORDS>"+savedRecords+"</SAVED-RECORDS>");
        xmlString.append("<FOLDER-ID>"+folderId+"</FOLDER-ID>");
        xmlString.append("</PAGE>");

        out.write(xmlString.toString());
        out.write("<!--END-->");
        out.flush();

    }
    else if(absfullselected.equals("true"))
    {

        if(request.getParameter("searchid") != null)
        {
            searchid = request.getParameter("searchid");
        }

        if(request.getParameter("database") != null)
        {
            database = request.getParameter("database");
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


        //Writing the XML
        out.write("<PAGE>");
        out.write("<SESSION-ID>"+sessionIdObj.toString()+"</SESSION-ID>");
        out.write("<SEARCH-ID>"+searchid+"</SEARCH-ID>");
        out.write("<DATABASE>"+database+"</DATABASE>");
        out.write("<DISPLAY-FORMAT>"+displayFormat+"</DISPLAY-FORMAT>");
        out.write("<DOCID-LIST>"+docids+"</DOCID-LIST>");
        out.write("<HANDLES>"+handles+"</HANDLES>");
        out.write("</PAGE>");
        out.write("<!--END-->");
    }
    else if(basketCount!=0)
    {

        xmlString = new StringBuffer("<PAGE><SESSION-ID>"+sessionIdObj.toString()+"</SESSION-ID>");
        xmlString.append("<DATABASE>"+database+"</DATABASE>");
         if( (displayFormat.equals("citationSelectedSet")) || (displayFormat.equals("citation")) )
         {
             displayFormat=Citation.CITATION_FORMAT;
         }
         else if( (displayFormat.equals("abstractSelectedSet")) || (displayFormat.equals("abstract")) )
         {
             displayFormat=Abstract.ABSTRACT_FORMAT;
         }
         else if( (displayFormat.equals("detailedSelectedSet")) || (displayFormat.equals("detailed")) )
         {
             displayFormat=FullDoc.FULLDOC_FORMAT;
         }

        xmlString.append("<DISPLAY-FORMAT>"+displayFormat+"</DISPLAY-FORMAT>");
        xmlString.append("<DOCID-LIST><DOC>W</DOC>");
        xmlString.append("</DOCID-LIST>");

        xmlString.append("<DB-LIST>");
        docBasketDatabase = basket.getListOfDatabases();
        databaseCount=docBasketDatabase.size();


        String dbflag="false";
        Iterator iter = docBasketDatabase.iterator();
        while(iter.hasNext())
        {
            String databaseName=(String)iter.next();
            // if database list contains combined then we have to show the same
            // ris compendex radio button,for this we are using the name cpx for both
            // compendex and combined databases
            if(databaseName.equals("COM"))
            {
                if(docBasketDatabase.contains("cpx"))
                {
                    //do nothing
                }
                else
                {
                    databaseName="cpx";
                    dbflag="true";
                }
            }
            else
            {
                dbflag="true";
            }
            if(dbflag.equals("true"))
            {
                xmlString.append("<DB-NAME>");
                xmlString.append(databaseName);
                xmlString.append("</DB-NAME>");
            }
        }
        xmlString.append("</DB-LIST>");
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
        xmlString.append("<DATABASE>"+database+"</DATABASE>");
        xmlString.append("<ERROR-PAGE>true</ERROR-PAGE>");
        xmlString.append("</PAGE>");
        out.write(xmlString.toString());
        out.write("<!--END-->");
        out.flush();

    //to do
    }



%>

