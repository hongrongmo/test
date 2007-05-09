<%@ page language="java" %>
<%@ page session="false" %>
<%@ page import="java.util.*"%>
<%@ page import="java.net.*"%>
<%@ page import="java.io.*"%>
<%@ page import="org.ei.domain.navigators.*"%>
<%@ page import="org.ei.domain.Searches"%>
<%@ page import="org.ei.controller.ControllerClient"%>
<%@ page import="org.ei.session.*"%>
<%@ page import="org.ei.query.base.*"%>
<%@ page import="org.ei.parser.base.*"%>
<%@ page import="org.ei.domain.*"%>
<%@ page import="org.ei.domain.navigators.*"%>
<%@ page import="org.ei.util.GUID"%>
<%@ page import="org.ei.util.StringUtil"%>
<%@ page import="org.ei.config.*"%>
<%@ page import="org.apache.oro.text.perl.*"%>
<%@ page import="org.apache.oro.text.regex.*"%>
<%@ page import="org.ei.domain.personalization.GlobalLinks"%>
<%@ page import="java.util.Calendar" %>
<%@ page import="org.ei.xmlio.*"%>
<%@ page buffer="20kb"%>
<%

    Perl5Util perl = new Perl5Util();
    String xmlDoc=null;
    String currentPage=null;
    String totalDocCount=null;
    SearchResult result=null;
    org.ei.domain.Query queryObject = null;
    
    Page oPage=null;
    ControllerClient client = new ControllerClient(request, response);
    String sessionId = null;
    SessionID sessionIdObj = null;
    String recentXmlQueryString=null;
    String searchID=null;
    int index=0;
    int nTotalDocs=0;
    String format = StringUtil.EMPTY_STRING;
    String term1 = "";
    String dbName="";
    String sortBy = null;
   	String sortDir = null;
    String autoStem="";
    String bYear = "";
    String eYear = "";
    String location=null;
    String navigator=null;
    String lastFourUpdates=null;
    String nextPage = null;
    String prevPage = null;
    StringBuffer query = null;
    boolean updateCurrentQuery=true;
    int pagesize=0;
%>
<%!
    int customizedEndYear = (Calendar.getInstance()).get(Calendar.YEAR);
    String pageSize=null;
    DatabaseConfig databaseConfig = null;
    public void jspInit()
    {
        try
        {
            RuntimeProperties eiProps = ConfigService.getRuntimeProperties();
            pageSize = eiProps.getProperty("PAGESIZE");
            databaseConfig = DatabaseConfig.getInstance();
            customizedEndYear = Integer.parseInt(eiProps.getProperty("SYSTEM_ENDYEAR"));
        }
        catch(Exception e)
        {
           e.printStackTrace();
        }
    }
%>
<%
  SearchControl sc=null;
  
  try
  {
      if(request.getParameter("DATABASE")!=null)
      {
          dbName = ((String)request.getParameter("DATABASE")).trim();
      }
      else
      {
          throw new Exception("no database found");
      }
      if(request.getParameter("XQUERYX")!=null)
      {
          xmlDoc = request.getParameter("XQUERYX");
          xmlDoc = xmlDoc.trim();
          xmlDoc = perl.substitute("s/>\\s+</></g", xmlDoc);
      }
  
      if((request.getParameter("XQUERYX")==null) && ((request.getParameter("EISESSION")==null) || (request.getParameter("SEARCHID")==null)))
      {
          throw new Exception("not enough data to do a search");
      }
      else
      {
          XqueryxParser xp = new XqueryxParser();
          XqueryxRewriter xr = new XqueryxRewriter();
          if(xmlDoc!=null)
          {
              org.ei.xmlio.Query xquery = (org.ei.xmlio.Query)xp.parse(xmlDoc);
              xquery.accept(xr);
              term1=xr.getQuery();
          }
      }
  
      sc = new FastSearchControl();
      pagesize=Integer.parseInt(pageSize.trim());
      UserSession ussession=(UserSession)client.getUserSession();
      sessionId=ussession.getID();
      sessionIdObj = ussession.getSessionID();
  
      User user=ussession.getUser();
  
      //Getting the COUNT parameter from the request
  
      if(request.getParameter("PAGE")!=null)
      {
          currentPage=(String)request.getParameter("PAGE");
      }
      else
      {
          currentPage="1";
      }
  
      int intDbMask = Integer.parseInt(dbName);
      String[] credentials = user.getCartridge();
  
      if(credentials == null || !UserCredentials.hasCredentials(intDbMask, databaseConfig.getMask(credentials)))
      {
        throw new Exception("You do not have access to the databases requested");
      }
  
      boolean initialSearch = true;
  
      if(request.getParameter("SEARCHID")!=null)
      {
        searchID = (String)request.getParameter("SEARCHID");
      }
  
      if((searchID == null) || searchID.equals(StringUtil.EMPTY_STRING))
      {
      	  /***************initial search*****************************/
          initialSearch = true;
          queryObject = new org.ei.domain.Query(databaseConfig, credentials);
          queryObject.setDataBase(intDbMask);
          searchID = new GUID().toString();
          queryObject.setID(searchID);
          queryObject.setSearchType(org.ei.domain.Query.TYPE_EXPERT);
  
          sortBy = request.getParameter("SORT");
          sortDir = request.getParameter("SORTDIR");
          queryObject.setSortOption(new Sort(sortBy,sortDir));       

          // adding the initial search as a single refinement so it will appear as a breadcrumb
          // at the top of search results
          if(term1 != null)
          {
              Refinements refinements = queryObject.getRefinements();
              EiNavigator easynav = EiNavigator.createNavigator(EiNavigator.ALL);
              easynav.setModifierValue(term1);
              refinements.addRefinement(easynav);
          }

          queryObject.setSearchPhrase(term1,"","","","","","","");
  
          autoStem = request.getParameter("AUTOSTEM");
          if(autoStem == null || "on".equalsIgnoreCase(autoStem))
          {
            queryObject.setAutoStemming("on");
          }
          else
          {
            queryObject.setAutoStemming("off");
          }
  
          bYear = request.getParameter("STARTYEAR");
          eYear = request.getParameter("ENDYEAR");

          // bYear was set to default to 1990 if request parameter was null  
          if ((bYear == null) || (eYear == null))
          {
            Map startEndYears = databaseConfig.getStartEndYears(credentials, intDbMask);
            bYear = (bYear == null) ? (String) startEndYears.get(DatabaseConfig.STARTYEAR) : bYear;
            eYear = (eYear == null) ? (String) startEndYears.get(DatabaseConfig.ENDYEAR) : eYear;
          }
          
          queryObject.setStartYear(bYear);
          queryObject.setEndYear(eYear);
  
          queryObject.setSearchQueryWriter(new FastQueryWriter());
          queryObject.compile();
  
          result = sc.openSearch(queryObject,
                                 sessionId,
                                 pagesize,
                                 false);
  
          //getting the results count for this query
          totalDocCount=Integer.toString(result.getHitCount());
  
          if(totalDocCount!=null)
          {
              queryObject.setRecordCount(totalDocCount.trim());
          }
  		    queryObject.setSessionID(sessionId);
          if(updateCurrentQuery)
          {
              Searches.saveSearch(queryObject);
          }
      }
      else // history
      {
      		updateCurrentQuery = false;
      		queryObject = Searches.getSearch(searchID);
      		queryObject.setSearchQueryWriter(new FastQueryWriter());
      		queryObject.setDatabaseConfig(databaseConfig);
      		queryObject.setCredentials(credentials);
      		result = sc.openSearch(queryObject,
                									sessionId,
                									pagesize,
                									true);
      		initialSearch = false;     
      }
  
      totalDocCount = queryObject.getRecordCount();
      nTotalDocs = Integer.parseInt(totalDocCount);
  
      /**
        * Checking for the total results is greater than zero.
        * then output the xml with results.
        */
  
      if(nTotalDocs>0)
      {
          index = Integer.parseInt(currentPage.trim());
  
          oPage = result.pageAt(index,Citation.XMLCITATION_FORMAT);
          format = "XMLCITATION";
  
          String log_action = "document";
  
          if (initialSearch)
          {
              log_action = "search";
          }
  
          client.log("search_id", queryObject.getID());
          client.log("query_string", queryObject.getPhysicalQuery());
          client.log("sort_by", queryObject.getSortOption().getSortField());
          client.log("suppress_stem", queryObject.getAutoStemming());
          client.log("context", "search");
          client.log("action", log_action);
          client.log("type", "basic");
          client.log("xmlio","y");
          client.log("db_name", dbName);
          client.log("page_size", pageSize);
          client.log("format", format);
          client.log("doc_id", " ");
          client.log("num_recs", Integer.toString(oPage.docCount()));
          client.log("doc_index", Integer.toString(index));
          client.log("hits", totalDocCount);
          client.setRemoteControl();
          String currentTime=((Calendar.getInstance()).getTime()).toString();
  
          //Writing out XML
          //FileWriter out1 = new FileWriter("/temp/test.xml");
          //build next page and prev page;
          String serverName= ussession.getProperty("ENV_BASEADDRESS");
          XMLQueryNavigator bXml=new XMLQueryNavigator();
          bXml.setServerName(serverName);
          bXml.setCID("openXML");
          bXml.setNextPageID(Integer.toString(index+pagesize));
          bXml.setPrevPageID(Integer.toString(index-pagesize));
          bXml.setSearchID(searchID);
          bXml.setDatabase(dbName);
          bXml.setFormat(format);
          String prevPageURL=bXml.buildPrevPageURL();
          String nextPageURL=bXml.buildNextPageURL();
          out.write("<PAGE>");
          out.write("<HEADER/>");
          out.write("<TIME>");
          out.write(currentTime);
          out.write("</TIME>");
          out.write("<DBMASK>");
          out.write(dbName);
          out.write("</DBMASK>");
          out.write("<SEARCH-TYPE>"+queryObject.getSearchType()+"</SEARCH-TYPE>");
          out.write("<FOOTER/>");
          out.write("<SESSION-TABLE/>");
          out.write("<SEARCH/>");
          out.write("<NAVIGATION-BAR/>");
          out.write("<TOP-RESULTS-MANAGER/>");
          out.write("<BOTTOM-RESULTS-MANAGER/>");
          out.write("<RESULTS-PER-PAGE>"+pagesize+"</RESULTS-PER-PAGE>");
          out.write("<SEARCH-ID>"+searchID+"</SEARCH-ID>");
          out.write("<RESULTS-COUNT>"+totalDocCount+"</RESULTS-COUNT>");
          out.write("<SERVER>"+serverName+"</SERVER>");
          if((index-pagesize)>0)
          {
              out.write("<PREV-PAGE><![CDATA["+prevPageURL+"]]></PREV-PAGE>");
          }
          if((index+pagesize)<=nTotalDocs)
          {
              out.write("<NEXT-PAGE><![CDATA["+nextPageURL+"]]></NEXT-PAGE>");
          }
          out.write("<SESSION-ID>"+sessionIdObj.toString()+"</SESSION-ID>");
          out.write("<CURR-PAGE-ID>"+index+"</CURR-PAGE-ID>");
          queryObject.setDisplay(true);
          out.write(queryObject.toXMLString());
          // output databases based on credentials
          databaseConfig.toXML(credentials, out);
          oPage.toXML(out);
          out.write("</PAGE>");
          out.println("<!--END-->");
          out.flush();
  
      }
      else//no record found
      {
          out.write("<PAGE>");
          out.write("<DBMASK>");
          out.write(dbName);
          out.write("</DBMASK>");
          out.write(queryObject.toXMLString());
          out.write("<RESULTS-COUNT>0</RESULTS-COUNT>");
          out.write("</PAGE>");
          return;
      }
  }
  catch(Exception e)
  {
      out.write("<PAGE><EXCEPTION><![CDATA["+e.getMessage()+"]]></EXCEPTION></PAGE>");
      e.printStackTrace();
  }
  finally
  {
      if(sc != null)
      {
          sc.closeSearch();
      }
  }
%>