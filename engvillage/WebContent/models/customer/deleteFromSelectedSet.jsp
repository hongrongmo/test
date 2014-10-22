<%@page import="org.apache.commons.validator.GenericValidator"%>
<%@ page language="java"%><%@ page session="false"%><%@ page import="java.util.*"%><%@ page import="java.net.*"%><%@ page import="org.ei.domain.*"%><%@ page
	import="org.engvillage.biz.controller.ControllerClient"%><%@ page import="org.engvillage.biz.controller.UserSession"%>
<%@ page import="org.ei.domain.personalization.*"%><%@ page import="org.ei.config.*"%><%@ page
	errorPage="/error/errorPage.jsp"%>
<%
	ControllerClient client = new ControllerClient(request, response);
	UserSession ussession = (UserSession) client.getUserSession();
	String sessionid = ussession.getSessionid();
	// this was failing since basketsize was missing on request - get from DoucmentBasket instead
	//int basketCount=Integer.parseInt(request.getParameter("basketsize"));
	int handle = Integer.parseInt(request.getParameter("handle"));

	String viewType = request.getParameter("viewType");
	String docID = request.getParameter("docid");
	String databaseType = request.getParameter("DATABASETYPE");
	List listOfDocBasHandles = new ArrayList();
	String databaseID = null;

	DocumentBasket documentBasket = new DocumentBasket(sessionid);
	DocID doc = new DocID(handle, docID.trim(),
			(DatabaseConfig.getInstance()).getDatabase(docID.substring(
					0, 3)));
	BasketEntry basEntry = new BasketEntry();
	basEntry.setDocID(doc);
	listOfDocBasHandles.add(basEntry);
	documentBasket.removeSelected(listOfDocBasHandles);

	int pageIndex = -1;

	if ((handle % 50) == 0) {
		pageIndex = (handle / 50);
	} else {
		pageIndex = (handle / 50) + 1;
	}

	int basketCount = documentBasket.getBasketSize();

	if (basketCount != 1) {
		basketCount--;
	}

	int totalPages = -1;

	if ((basketCount % 50) == 0) {
		totalPages = (basketCount / 50);
	} else {
		totalPages = (basketCount / 50) + 1;
	}

	if (pageIndex > totalPages) {
		pageIndex = totalPages;
	}

	if (!GenericValidator.isBlankOrNull(viewType)) {
		StringBuffer urlbuf = new StringBuffer(
				"/controller/servlet/Controller?CID=");
		urlbuf.append(viewType);
		urlbuf.append("&BASKETCOUNT=");
		urlbuf.append(Integer.toString(pageIndex));
		urlbuf.append("&DATABASETYPE=");
		urlbuf.append(databaseType);
		if (request.getParameter("DATABASEID") != null) {
			databaseID = request.getParameter("DATABASEID");
		}
		if (request.getParameter("searchresults") != null) {
			urlbuf.append("&searchresults=");
			urlbuf.append(URLEncoder.encode(request
					.getParameter("searchresults")));
		}

		if (request.getParameter("newsearch") != null) {
			urlbuf.append("&newsearch=");
			urlbuf.append(URLEncoder.encode(request
					.getParameter("newsearch")));
		}

		client.setRedirectURL(urlbuf.toString());
		client.setRemoteControl();
	} else {
		out.write("<PAGE></PAGE>");
		out.println("<!--END-->");
		out.flush();
	}
%>