<%@ page language="java" %>
<%@ page session="false" %>

<!--
	 * This file adds a set of records to the Selected Set.
	 * @param java.lang.String.database
	 * @param java.lang.String.startIndex
	 * @param java.lang.String.endIndex
	 * @param java.lang.String.count
	 * @param java.lang.String.databaseid
	 * @param java.lang.String.pagesize
	 * @param java.lang.String.searchid
-->

<!-- import statements of Java packages-->
<%@ page import="java.util.*"%>
<%@ page import="java.net.URLEncoder"%>

<!--import statements of ei packages.-->
<%@ page import="org.ei.domain.*"%>
<%@ page import="org.ei.controller.ControllerClient"%>
<%@ page import="org.ei.session.*"%>
<%@ page import="org.ei.config.*"%>
<%@ page import="org.ei.tags.*"%>
<%@ page import="org.ei.query.base.*"%>
<%@ page import="org.ei.domain.Searches"%>
<%@ page  errorPage="/error/errorPage.jsp"%>
<%
	FastSearchControl sc=null;
	SearchResult result=null;

	DocumentBasket basket = null;
	DocID docId=null;

	ControllerClient client = null;
	String sessionId  = null;
	String database = null;
	String searchtype = null;
	//This variable for document type
	String searchId=null;
	int startRange = 0;
	int endRange = 0;
	int count = 0;
        Query query=null;
	String dedupFlag = null;
	String dedupOption = null;
	String dedupDB = null;
	String origin = null;
	String dbLink = null;
	String linkSet = null;
	String tagSearchFlag = null;
	List docIDList = null;
%>
<%!
    int pagesize = 0;
    DatabaseConfig databaseConfig = null;

    public void jspInit()
    {
        try
        {
            RuntimeProperties runtimeProps = ConfigService.getRuntimeProperties();
            pagesize = Integer.parseInt(runtimeProps.getProperty("PAGESIZE"));

            databaseConfig = DatabaseConfig.getInstance();

        } catch(Exception e) {
            e.printStackTrace();
        }
    }
%>
<%

	client = new ControllerClient(request, response);
	UserSession ussession=(UserSession)client.getUserSession();
	sessionId = ussession.getID();
	User user=ussession.getUser();
	String pUserId = ussession.getProperty("P_USER_ID");
	String customerId=user.getCustomerID().trim();

	String[] credentials = user.getCartridge();

	if(request.getParameter("database") != null)
	{
    		database = request.getParameter("database");
	}

	if(request.getParameter("searchtype") != null)
	{
		searchtype = request.getParameter("searchtype");
	}
	if(request.getParameter("searchid") != null)
	{
		searchId = request.getParameter("searchid");
	}
	if(request.getParameter("startrange") != null)
	{
		startRange = Integer.parseInt(request.getParameter("startrange"));
	}
	if(request.getParameter("endrange") != null)
	{
		endRange = Integer.parseInt(request.getParameter("endrange"));
	}
	if(request.getParameter("count") != null)
	{
		count = Integer.parseInt(request.getParameter("count"));
	}

	String groupID = null;
	int scopeint = -1;
	String scope = null;

	dedupFlag = request.getParameter("DupFlag");
	dedupDB = request.getParameter("dbpref");
	dedupOption = request.getParameter("fieldpref");
	origin = request.getParameter("origin");
	dbLink = request.getParameter("dbLink");
	linkSet = request.getParameter("linkSet");

	tagSearchFlag = request.getParameter("tagSearchFlag");

	if(tagSearchFlag != null &&
	   tagSearchFlag.equalsIgnoreCase("true"))
	{
		if(request.getParameter("scope")!= null)
		{
			scope = request.getParameter("scope");
			if (scope.indexOf(":") == -1)
			{
				scopeint = Integer.parseInt(scope);
			}
			else
			{
				String[] parts = scope.split(":");
				scopeint = Integer.parseInt(parts[0]);
				groupID = parts[1];
			}
		}

		/*
			Create a dummy query
		*/

		query = new Query();
		query.setSessionID(sessionId);
		query.setID(searchId);
		query.setDisplayQuery(searchId);
		query.setSearchType("tag");
		query.setDatabaseConfig(databaseConfig);
		query.setCredentials(credentials);
       	TagBroker tagBroker = new TagBroker();
	    docIDList = tagBroker.getDocIDs(searchId,
						                scopeint,
						                pUserId,
						                customerId,
						                groupID,
						                startRange,
						                (endRange-startRange)+1,
						                credentials);
	}
	else
	{
		query = Searches.getSearch(searchId);
		query.setSearchQueryWriter(new FastQueryWriter());
		query.setDatabaseConfig(databaseConfig);
		query.setCredentials(credentials);

		sc = new FastSearchControl();
		result = sc.openSearch(query,
				       sessionId,
				       pagesize,
				       true);

		if(dedupFlag != null && dedupFlag.equalsIgnoreCase("true"))
		{

			FastDeduper deduper = sc.dedupSearch(1000,
							     dedupOption,
							     dedupDB);

			DedupData wanted = deduper.getWanted();
			DedupData unwanted = deduper.getUnwanted();
			List wantedList = null;

			if(origin != null && origin.equalsIgnoreCase("summary"))
			{
				if(linkSet != null && linkSet.equalsIgnoreCase("deduped"))
				{
					if(dbLink != null && dbLink.length() > 0)
					{

						wantedList = wanted.getDocIDs(dbLink);
					}
					else
					{
						wantedList = wanted.getDocIDs();
					}
				}
				else if(linkSet != null && linkSet.equalsIgnoreCase("removed"))
				{
					if(dbLink != null && dbLink.length() > 0)
					{
						wantedList = unwanted.getDocIDs(dbLink);
					}
					else
					{
						wantedList = unwanted.getDocIDs();
					}
				}
			}
			else
			{
				wantedList = wanted.getDocIDs();
			}


			docIDList = wantedList.subList(startRange-1, endRange);
		}
		else
		{
			docIDList = sc.getDocIDRange(startRange, endRange);
		}
	}

	List listOfBasketEntries = new ArrayList();

	basket = new DocumentBasket(sessionId);

	int listSize = docIDList.size();
	for(int i = 0; i < listSize ; i++)
	{
    		docId = (DocID)docIDList.get(i);
		BasketEntry be = new BasketEntry(docId,query);
		listOfBasketEntries.add(be);
	}

	basket.addAll(listOfBasketEntries);

	String redirectURL = null;
	String CID = "";
	if(tagSearchFlag != null && tagSearchFlag.equalsIgnoreCase("true"))
	{
		CID = "tagSearch";


		redirectURL="/controller/servlet/Controller?CID=tagSearch"+"&SEARCHID="+searchId+
		"&COUNT="+count+"&database="+database+"&searchtype=TagSearch"+"&tagSearchFlag="+tagSearchFlag+"&scope="+scope;
	}
	else if(dedupFlag != null && dedupFlag.equalsIgnoreCase("true"))
	{
		CID = "dedup";
		redirectURL="/controller/servlet/Controller?CID="+CID+"&SEARCHID="+searchId+
		"&COUNT="+count+"&database="+database+"&DupFlag="+dedupFlag+"&dbpref="+
		dedupDB+"&fieldpref="+dedupOption+"&origin="+origin+"&dbLink="+dbLink+"&linkSet="+linkSet;
	}
	else
	{
		if(searchtype.equalsIgnoreCase("Thesaurus"))
		{
			CID = "thesSearchCitationFormat";
		}
		else if(searchtype.equalsIgnoreCase("Quick"))
		{
			CID = "quickSearchCitationFormat";
		}
		else if(searchtype.equalsIgnoreCase("Combined"))
		{
			CID = "combineSearchHistory";
		}
		else if(searchtype.equalsIgnoreCase("Expert"))
		{
			CID = "expertSearchCitationFormat";
		}
		else if(searchtype.equalsIgnoreCase("Easy"))
		{
			CID = "expertSearchCitationFormat";
		}
		else if(searchtype.equalsIgnoreCase("Book"))
		{
			CID = "quickSearchCitationFormat";
		}


		redirectURL="/controller/servlet/Controller?CID="+CID+"&SEARCHID="+searchId+"&COUNT="+count+"&database="+database;
	}

	client.setRedirectURL(redirectURL);
	client.setRemoteControl();

	StringBuffer sbuffer=new StringBuffer();
	sbuffer.append("<PAGE>");
	sbuffer.append("</PAGE>");
	out.write(sbuffer.toString());
	out.println("<!--END-->");

%>
