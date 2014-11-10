<%@ page language="java" %>
<%@ page session="false" %>
<%@ page import="java.util.*"%>
<%@ page import="java.net.URLEncoder"%>
<%@ page import="org.ei.domain.*"%>
<%@ page import="org.engvillage.biz.controller.ControllerClient"%>
<%@ page import="org.engvillage.biz.controller.UserSession"%>
<%@ page import="org.ei.domain.personalization.*"%>
<%@ page import="org.ei.config.*"%>
<%@ page import="org.ei.query.base.*"%>
<%@ page import="org.ei.domain.Searches"%>
<%@ page import="org.ei.data.upt.runtime.*"%>
<%@ page import="org.ei.data.inspec.runtime.*"%>
<%@ page  errorPage="/error/errorPage.jsp"%><%

	DocumentBasket basket = null;
	DocID docId=null;
	ControllerClient client = null;
	int startRange = 0;
	int endRange = 0;
	List docIDList = null;
	String did = null;
	int resultscount = -1;
	client = new ControllerClient(request, response);
	UserSession ussession=(UserSession)client.getUserSession();
	String sessionId = ussession.getSessionid();
	String[] credentials = ussession.getCartridge();

	if(request.getParameter("docid")!=null)
	{
		did = request.getParameter("docid").trim();
    }

    if(request.getParameter("resultscount") != null)
	{
		resultscount = Integer.parseInt(request.getParameter("resultscount"));
	}

	if(request.getParameter("startrange") != null)
	{
		startRange = Integer.parseInt(request.getParameter("startrange"));
	}

	if(request.getParameter("endrange") != null)
	{
		endRange = Integer.parseInt(request.getParameter("endrange"));
	}

	RefPager pager = null;
	if(did != null && did.indexOf("inspec")>-1)
	{
		InspecDocBuilder docBuilder = new InspecDocBuilder(new InspecDatabase());
		List refList = docBuilder.getRefPager(did);
		if(refList!=null)
		{
			resultscount = refList.size()-1;
			refList.remove(0);
		}
		else
		{
			resultscount = 0;
		}
		pager = new RefPager(refList,
                                      25,
                                      null,
                                      sessionId);

	}
	else
	{
		UPTRefDocBuilder refBuilder = new UPTRefDocBuilder(new UPTRefDatabase());

		pager = refBuilder.getRefPager(did,
						25,
						sessionId);


	}

	docIDList = pager.getPageIDs(startRange,
				     endRange,
				     resultscount);

	List listOfBasketEntries = new ArrayList();
	basket = new DocumentBasket(sessionId);
	int listSize = docIDList.size();

	/*
		Create a dummy query
	*/

	Query query = new Query();
	query.setSessionID(sessionId);
	query.setID("NA");
	query.setDisplayQuery("NA");
	query.setSearchType("tag");
	query.setDatabaseConfig(DatabaseConfig.getInstance());
	query.setCredentials(credentials);
	for(int i = 0; i < listSize ; i++)
	{
    	docId = (DocID)docIDList.get(i);
		BasketEntry be = new BasketEntry(docId,query);
		listOfBasketEntries.add(be);
	}
	basket.addAll(listOfBasketEntries);
	client.setRedirectURL("/controller/servlet/Controller?"+request.getParameter("backurl"));
	client.setRemoteControl();
%>
