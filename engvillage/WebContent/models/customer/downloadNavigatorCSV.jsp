<%@ page language="java"%>
<%@ page session="false"%>
<%@ page import="java.util.*"%>
<%@ page import="java.net.*"%>
<%@ page import="org.ei.data.DataCleaner"%>
<%@ page import="org.ei.domain.*"%>
<%@ page import="org.ei.query.base.*"%>
<%@ page import="org.ei.parser.base.*"%>
<%@ page import="org.ei.domain.navigators.*"%>
<%@ page import="org.engvillage.biz.controller.ControllerClient"%>
<%@ page import="org.engvillage.biz.controller.UserSession"%>
<%@ page import="org.ei.domain.personalization.*"%>
<%@ page errorPage="/error/errorPage.jsp"%>
<%
	// Create a session object using the controllerclient.java
	ControllerClient client = new ControllerClient(request, response);
	UserSession ussession = (UserSession) client.getUserSession();

	String sessionid = ussession.getSessionid();
	String[] credentials = ussession.getCartridge();

	String searchid = null;
	String nav = EiNavigator.DB;

	if (request.getParameter("searchid") != null) {
		searchid = request.getParameter("searchid");
	}
	if (request.getParameter("nav") != null) {
		nav = request.getParameter("nav");
	}

	Query queryObject = Searches.getSearch(searchid);

	String strFilename = "Navigator.tab";
	client.setContentDispositionFilenameTimestamp(strFilename);
	client.setRemoteControl();

	out.write("<PAGE>");

	if (queryObject != null) {
		queryObject.setSessionID(sessionid);
		queryObject.setSearchQueryWriter(new FastQueryWriter());
		queryObject.setDatabaseConfig(DatabaseConfig.getInstance());
		queryObject.setCredentials(credentials);

		SearchControl sc = new FastSearchControl();

		SearchResult result = sc.openSearch(queryObject, sessionid, 25,
				false);

		ResultNavigator navs = sc.getNavigator();

		out.write("<![CDATA[");
		out.write("Engineering Village Search query: ");
		out.write(queryObject.getIntermediateQuery());
		out.write("\n");
		out.write("]]>");

		if (queryObject.getRefinements() != null) {

			if (navs != null) {
				/*
				    JAM TURKEY CODE to parse navigators out of expert search query
				 */
				Refinements parsedrefinements = new Refinements();
				BooleanQuery bq = queryObject.getParseTree();
				RefinementFieldGatherer fg = new RefinementFieldGatherer();
				Map mapFields = fg.gatherExactFields(bq);
				Iterator itr = mapFields.keySet().iterator();

				while (itr.hasNext()) {
					Field field = (Field) itr.next();

					String strfield = field.getValue().toLowerCase()
							.trim();
					if (EiNavigator.ALL.equals(strfield)) {
						continue;
					}

					EiNavigator einavigator = EiNavigator
							.createNavigator(field);

					if (!EiNavigator.DB.equals(einavigator.getName())) {
						List trmMods = new ArrayList();

						// create modifiers from the list of terms and add them
						// to a navigator for this field
						List trmList = (List) mapFields.get(field);
						Iterator itrTerms = trmList.iterator();
						while (itrTerms.hasNext()) {
							String term = (String) itrTerms.next();
							// This is messy.  Could be somewhere else?
							if (EiNavigator.AU.equals(einavigator
									.getName())) {
								// remove 'spacer' inserted into Author names by DataCleaner
								term = DataCleaner.restoreAuthor(term);
							}

							trmMods.add(EiModifier.parseModifier(term));
						}

						einavigator.setModifiers(trmMods);
						parsedrefinements.addRefinement(einavigator);
					}
				}

				navs.removeRefinements(parsedrefinements);
			}
		}

		EiNavigator anav = navs.getNavigatorByName(nav);

		if (anav != null) {
			out.write("<![CDATA[");
			out.write(anav.getDisplayname());
			out.write("\n");
			out.write("]]>");

			out.write("<![CDATA[");
			out.write(anav.toCSV());
			out.write("\n");
			out.write("]]>");
		}
	}

	out.write("<![CDATA[");
	out.write("\n");
	out.write("Copyright " + Calendar.getInstance().get(Calendar.YEAR)
			+ " Elsevier Inc. All rights reserved.");
	out.write("]]>");

	out.write("</PAGE>");
	out.flush();
%>