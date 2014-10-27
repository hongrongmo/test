<%@ page language="java" %>
<%@ page session="false" %>
<%@ page errorPage="/error/errorPage.jsp"%>
<%@ page buffer="20kb"%>

<%@ page import="java.util.*"%>
<%@ page import="java.net.*"%>
<%@ page import="java.io.*"%>
<%@ page import="org.ei.config.*"%>
<%@ page import="org.ei.controller.ControllerClient"%>
<%@ page import="org.ei.domain.*"%>
<%@ page import="org.ei.domain.navigators.*"%>
<%@ page import="org.ei.domain.personalization.*"%>
<%@ page import="org.ei.domain.Searches"%>
<%@ page import="org.ei.parser.base.*"%>
<%@ page import="org.ei.query.base.*"%>
<%@ page import="org.ei.session.*"%>
<%@ page import="org.ei.util.GUID"%>
<%@ page import="org.ei.util.StringUtil"%>
<%
    String currentPage = null;

    SearchResult result = null;
    Query queryObject = null;

    Page oPage = null;
    ControllerClient client = new ControllerClient(request, response);
    LocalHolding localHolding = null;

    String sessionId = null;
    SessionID sessionIdObj = null;
    String pUserId = "";
    boolean personalization = false;
    String navigator = null;

    String searchID=null;

    int index = 0;
    int nTotalDocs = 0;
    int jumpIndex = -1;
    String format = StringUtil.EMPTY_STRING;


    String dbName = "";

    boolean updateCurrentQuery = true;

    ClientCustomizer clientCustomizer = null;
    boolean isPersonalizationPresent = true;
    boolean isFullTextPresent = true;
    boolean isEmailAlertsPresent = true;
    boolean isCitLocalHoldingsPresent = false;
    boolean isRssLinkPresent = false;

    String customizedStartYear = "1969";
    String customizedLogo = "";

    // jam - added for 'factoring' dbs from combined searches
    // when rerun =
    String rerun = "";

    int pagesize = 0;
    int dedupsetsize = 0;
    
    int numPageToCache = 0;
    
    // The dedup "7" variables
    String DupFlag = "true";
    String dedupOption = null;
    String dedupDB = null;
    String criteria = null;
    String origin = StringUtil.EMPTY_STRING;
    String dbLink = StringUtil.EMPTY_STRING;
    String linkSet = StringUtil.EMPTY_STRING;


%>
<%!
    int customizedEndYear = (Calendar.getInstance()).get(Calendar.YEAR);
    String pageSize = null;
    String dedupSetSize = null;
    DatabaseConfig databaseConfig = null;

    public void jspInit()
    {
        try
        {
            RuntimeProperties eiProps = ConfigService.getRuntimeProperties();
            pageSize = eiProps.getProperty("PAGESIZE");
            dedupSetSize = eiProps.getProperty("DEDUPSETSIZE");
            databaseConfig = DatabaseConfig.getInstance();

            // jam Y2K3
            customizedEndYear = Integer.parseInt(eiProps.getProperty("SYSTEM_ENDYEAR"));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
%>
<%
    FastSearchControl sc = new FastSearchControl();

    pagesize=Integer.parseInt(pageSize.trim());
    dedupsetsize=Integer.parseInt(dedupSetSize.trim());
    
    UserSession ussession=(UserSession)client.getUserSession();
    sessionId=ussession.getID();
    sessionIdObj = ussession.getSessionID();
    pUserId = ussession.getProperty("P_USER_ID");
    if((pUserId != null) && (pUserId.trim().length() != 0))
    {
        personalization=true;
    }

    User user = ussession.getUser();
    String customerId=user.getCustomerID().trim();

    clientCustomizer=new ClientCustomizer(ussession);
    // moved localHolding object creation to if statement in outgoing XML section
    isFullTextPresent=clientCustomizer.checkFullText("citationResults");
    isRssLinkPresent=clientCustomizer.checkRssLink();
    isCitLocalHoldingsPresent=clientCustomizer.checkLocalHolding("citationResults");
    isPersonalizationPresent=clientCustomizer.checkPersonalization();
    isEmailAlertsPresent=clientCustomizer.checkEmailAlert();

    customizedLogo=clientCustomizer.getLogo();
    customizedStartYear = clientCustomizer.getSYear();

    dbName = request.getParameter("database");
    navigator = request.getParameter("navigator");
    
    if(request.getParameter("origin") != null)
    {
      origin = request.getParameter("origin");
    }
    if(request.getParameter("dbLink") != null)
    {
      dbLink = request.getParameter("dbLink");
    }
    if(request.getParameter("linkSet") != null)
    {
      linkSet = request.getParameter("linkSet");
    }
    if(request.getParameter("fieldpref") != null)
    {
      dedupOption = request.getParameter("fieldpref");
    }
    else
    {
      dedupOption = "0";
    }
    
    if(request.getParameter("dbpref") != null)
    {
      dedupDB = request.getParameter("dbpref");
    }
    else
    {
      dedupDB = "cpx";
    }


    //Getting the COUNT parameter from the request
    currentPage = request.getParameter("COUNT");
    if(currentPage == null)
    {
      currentPage = "1";
    }

    int intDbMask = Integer.parseInt(dbName);
    String[] credentials = user.getCartridge();

    boolean initialSearch = false;
    boolean clearBasket = false;

    DocumentBasket documentBasket = new DocumentBasket(sessionId);

    try
    {
      searchID = request.getParameter("SEARCHID");
  
      queryObject = Searches.getSearch(searchID);
  
      queryObject.setSearchQueryWriter(new FastQueryWriter());
      queryObject.setDatabaseConfig(databaseConfig);
      queryObject.setCredentials(credentials);

      sc.openSearch(queryObject,
                    sessionId,
                    pagesize,
                    true);
  
      if(personalization == true)
      {
      	queryObject.setUserID(pUserId);
      }
      
      queryObject.setSessionID(sessionId);
  
      nTotalDocs = Integer.parseInt(queryObject.getRecordCount());
  
      
      // Create DB object - use this to get all dedup db related values
      // for consistency - 
      Database dedupDBObj = databaseConfig.getDatabase(dedupDB);
      
      String strGlobalLinksXML = GlobalLinks.toXML(user.getCartridge());
      index = Integer.parseInt(currentPage.trim());
       
  
      FastDeduper deduper = sc.dedupSearch(1000, 
      				   	  dedupOption, 
      				   	  dedupDBObj.getID());
      			
      			
      DedupData wanted = deduper.getWanted();
      List wantedList = wanted.getDocIDs();      
      int dedupSet = wantedList.size();


      DedupData unwanted = deduper.getUnwanted();
  
      int dedupSubset = 0;
      int removedSubset = 0;
  
       
      if(origin.equalsIgnoreCase("summary"))
      {
        if(linkSet.equalsIgnoreCase("deduped"))
        {
          if(dbLink.length() > 0)
          {
            wantedList = (List)wanted.getDocIDs(dbLink);        
            dedupSubset = wantedList.size();
            criteria = "d:" + dbLink.substring(0, 1).toLowerCase();
          }
        }
        else if(linkSet.equalsIgnoreCase("removed"))
        {
          if(dbLink.length() > 0)
          {
            wantedList = (List) unwanted.getDocIDs(dbLink);
            criteria = "r:" + dbLink.substring(0, 1).toLowerCase();
            removedSubset = wantedList.size();
          }
          else
          {
            wantedList = unwanted.getDocIDs();
            criteria = "r";
          }
         }
      }
  
      DedupBroker dedupBroker = new DedupBroker();
      List pageDocIDList = dedupBroker.getPage(wantedList, index, pagesize);
      oPage = dedupBroker.buildPage(pageDocIDList, Citation.CITATION_FORMAT,sessionId);
  
      if(origin.equalsIgnoreCase("history") || origin.equalsIgnoreCase("summary"))
      {
        updateCurrentQuery = false;
      }
      else
      {
        criteria = dedupOption + ":" + dedupDBObj.getMask();
        queryObject.addDupSet(criteria);
      }
  
      // Logging
      client.log("search_id", searchID);
      client.log("query_string", queryObject.getPhysicalQuery());
      client.log("sort_by", queryObject.getSortOption().getSortField());
      client.log("sort_direction", queryObject.getSortOption().getSortDirection());
      client.log("suppress_stem", queryObject.getAutoStemming());
      client.log("context", "search");
      client.log("type", queryObject.getSearchType());
      client.log("db_name", dbName);
      client.log("page_size", pageSize);
      client.log("format", "citation");
      client.log("doc_id", " ");
      client.log("num_recs", Integer.toString(oPage.docCount()));
      client.log("doc_index", Integer.toString(index));
      client.log("hits", Integer.toString(nTotalDocs));

// dedup specific logging data
      client.log("action", "dedup");
      client.log("origin", origin);
      client.log("linkSet", linkSet);
// just to be sure - these have been null before
      if(dedupDB != null)
      {
        client.log("dbpref", dedupDB);
      }
      if(dedupOption != null)
      {
        client.log("fieldpref", dedupOption);
      }
      if(criteria != null)
      {
        client.log("criteria", criteria);
      }

      client.setRemoteControl();

      
      //Writing out XML
  
      out.write("<PAGE>");
  
      StringBuffer backurl = new StringBuffer();
      backurl.append("CID=dedup").append("&");
      backurl.append("SEARCHID=").append(searchID).append("&");
      backurl.append("COUNT=").append(index).append("&");
      backurl.append("database=").append(dbName);
  
      // add the dedup 7 to the back URL 
      backurl.append("&");
      backurl.append("DupFlag=").append(DupFlag).append("&");
      backurl.append("fieldpref=").append(dedupOption).append("&");
      backurl.append("dbpref=").append(dedupDBObj.getID()).append("&");
      backurl.append("origin=").append(origin).append("&");
      backurl.append("linkSet=").append(linkSet).append("&");
      backurl.append("dbLink=").append(dbLink).append("&");
      backurl.append("dedupSet=").append(dedupSet);

      out.write("<BACKURL>");
      out.write(URLEncoder.encode(backurl.toString()));
      out.write("</BACKURL>");
  
      out.write("<HEADER/>");
      out.write("<DBMASK>");
      out.write(dbName);
      out.write("</DBMASK>");
      out.write("<SEARCH-TYPE>"+queryObject.getSearchType()+"</SEARCH-TYPE>");
  
      out.write(strGlobalLinksXML);
  
      out.write("<FOOTER/>");
      out.write("<SESSION-TABLE/>");
      out.write("<SEARCH/>");
      out.write("<DEDUP-NAVIGATION-BAR/>");
      out.write("<RESULTS-MANAGER/>");
  
      out.write("<DEDUP>" + DupFlag + "</DEDUP>");
  
      out.write("<CLEAR-ON-VALUE>"+documentBasket.getClearOnNewSearch()+"</CLEAR-ON-VALUE>");
      out.write("<CUSTOMIZED-LOGO>"+customizedLogo+"</CUSTOMIZED-LOGO>");
      out.write("<CUSTOMIZED-STARTYEAR>"+customizedStartYear+"</CUSTOMIZED-STARTYEAR>");
      out.write("<CUSTOMIZED-ENDYEAR>"+customizedEndYear+"</CUSTOMIZED-ENDYEAR>");
      out.write("<CUSTOMER-ID>"+customerId+"</CUSTOMER-ID>");
      out.write("<RESULTS-PER-PAGE>"+pagesize+"</RESULTS-PER-PAGE>");
      out.write("<SESSION-ID>"+sessionIdObj.toString()+"</SESSION-ID>");
      out.write("<PERSONALIZATION-PRESENT>"+isPersonalizationPresent+"</PERSONALIZATION-PRESENT>");
      out.write("<FULLTEXT>"+isFullTextPresent+"</FULLTEXT>");
      out.write("<RSSLINK>"+isRssLinkPresent+"</RSSLINK>");
      out.write("<LOCALHOLDINGS-CITATION>"+ isCitLocalHoldingsPresent+"</LOCALHOLDINGS-CITATION>");
      out.write("<EMAILALERTS-PRESENT>"+isEmailAlertsPresent+"</EMAILALERTS-PRESENT>");
      out.write("<PERSONALIZATION>"+personalization+"</PERSONALIZATION>");
      out.write("<PERSON-USER-ID>"+pUserId+"</PERSON-USER-ID>");
      out.write("<SEARCH-ID>"+searchID+"</SEARCH-ID>");
      out.write("<RESULTS-COUNT>"+nTotalDocs+"</RESULTS-COUNT>");
      out.write("<PREV-PAGE-ID>"+(index-pagesize)+"</PREV-PAGE-ID>");
      out.write("<CURR-PAGE-ID>"+index+"</CURR-PAGE-ID>");
      out.write("<NEXT-PAGE-ID>"+(index+pagesize)+"</NEXT-PAGE-ID>");
      out.write("<DEDUPSETSIZE>" + dedupsetsize  + "</DEDUPSETSIZE>");
      out.write("<DBPREF>" + dedupDB + "</DBPREF>");
      out.write("<FIELDPREF>" + dedupOption + "</FIELDPREF>");
  
      Map unwantedMap = unwanted.getDatabases();
      
      out.write("<DEDUPSET-REMOVED-DUPS>");
      Iterator itt = unwantedMap.keySet().iterator();

      // create database objects from keys in unwanted
      List unwanteddbs = new ArrayList();
      while(itt.hasNext())
      {
        String key = (String) itt.next();
        unwanteddbs.add(databaseConfig.getDatabase(key));
      }
      // sort to get correct display order
      Collections.sort(unwanteddbs);

      // drive display off of sorted collection
      itt = unwanteddbs.iterator();
      while(itt.hasNext())
      {
        Database db = (Database) itt.next();
        String key = db.getID();
        out.write("<REMOVED-DUPS DBNAME=\"" + db.getShortName() + "\" DB=\""+key+"\" COUNT=\""+((Map)unwantedMap.get(key)).size()+"\" />");
      }
      out.write("</DEDUPSET-REMOVED-DUPS>");
  
      out.write("<DBLINK>" + dbLink + "</DBLINK>");
      out.write("<ORIGIN>" + origin + "</ORIGIN>");
      out.write("<LINKSET>" + linkSet + "</LINKSET>");
      out.write("<DEDUPSET>" + dedupSet + "</DEDUPSET>");
      out.write("<DEDUPSUBSET>" + dedupSubset + "</DEDUPSUBSET>");
      out.write("<REMOVED-SUBSET>" + removedSubset + "</REMOVED-SUBSET>");
      
      Map wantedMap = wanted.getDatabases();
      
      out.write("<DEDUPSET-COUNTER>");
      itt = wantedMap.keySet().iterator();

      // create database objects from keys in wanted
      List wanteddbs = new ArrayList();
      while(itt.hasNext())
      {
        String key = (String) itt.next();
        wanteddbs.add(databaseConfig.getDatabase(key));
      }
      // sort to get correct display order
      Collections.sort(wanteddbs);

      // drive display off of sorted collection
      itt = wanteddbs.iterator();
      while(itt.hasNext())
      {
        Database db = (Database) itt.next();
        String key = db.getID();
        out.write("<DB-COUNT DBNAME=\"" + db.getShortName() + "\" DB=\""+key+"\" COUNT=\""+((Map)wantedMap.get(key)).size()+"\" />");
      }
      out.write("</DEDUPSET-COUNTER>");
  
      queryObject.setDisplay(true);
      out.write(queryObject.toXMLString());
  
      // output databases based on credentials
      databaseConfig.toXML(credentials, out);
      if(isCitLocalHoldingsPresent)
      {
        localHolding = new LocalHolding(ussession,2);
        oPage.setlocalHolding(localHolding);
      }
      oPage.toXML(out);
  
      databaseConfig.sortableToXML(credentials, out);
  
      out.write("</PAGE>");
      out.println("<!--END-->");
      out.flush();
  
      if(updateCurrentQuery)
      {
        Searches.updateSearch(queryObject);
      }
    }
    finally
    {
        if(sc != null)
        {
            sc.closeSearch();
        }
    }
%>