<%@ page language="java" %>
<%@ page session="false" %>
<%@ page errorPage="/error/errorPage.jsp"%>

<%@ page import="org.ei.domain.personalization.*"%>
<%@ page import="org.ei.controller.ControllerClient"%>
<%@ page import="org.ei.session.*"%>
<%@ page import="org.ei.domain.*"%>
<%@ page import="java.net.URLDecoder"%>
<%@ page import="java.net.URLEncoder"%>

<%

    ControllerClient client = null;
    StringBuffer sb = null;
    String displayLogin  = null;
    //String displayForm = null;
    String database = null;
    String searchId = null;
    String count = null;
    String searchType = null;
    String CID = "";

    String docids = null;

    String optionValue = null;
    String queryId = null;
    String resultsFormat = null;
    String source = null;

    ClientCustomizer clientCustomizer=null;
    boolean personalization = false;
    boolean isPersonalizationPresent=true;

    // get all the parameters required
    if( request.getParameter("displaylogin") != null)
    {
        displayLogin = request.getParameter("displaylogin");
    }

    if( request.getParameter("database") != null)
    {
        database = request.getParameter("database");
    }

    if( request.getParameter("source") != null)
    {
        source = request.getParameter("source");
    }

    if( request.getParameter("searchid") != null)
    {
        searchId = request.getParameter("searchid");
    }

    if( request.getParameter("count") != null)
    {
        count = request.getParameter("count");
    }

    if( request.getParameter("searchtype") != null)
    {
        searchType = request.getParameter("searchtype");
    }

	Query queryObject = null;
	queryObject=Searches.getSearch(searchId);

	if(queryObject!=null)
	{
		String queryType = queryObject.getSearchType();
		if(queryType!=null)
		{
			if(queryType.equalsIgnoreCase(Query.TYPE_QUICK))
			{
				CID="quickSearchCitationFormat";
			}
			else if(queryType.equalsIgnoreCase(Query.TYPE_EASY))
			{
				CID="expertSearchCitationFormat";
			}
			else if(queryType.equalsIgnoreCase(Query.TYPE_EXPERT))
			{
				CID="expertSearchCitationFormat";
			}
			else if(queryType.equalsIgnoreCase(Query.TYPE_THESAURUS))
			{
				CID="thesSearchCitationFormat";
			}
			else if(queryType.equalsIgnoreCase(Query.TYPE_COMBINED_PAST))
			{
				CID="combineSearchHistory";
			}
		}
	}

    // build xml string
    sb = new StringBuffer("<PAGE>");
    sb.append("<DATABASE>").append(database).append("</DATABASE>");

    String nexturl = request.getParameter("nexturl");
    String backurl = request.getParameter("backurl");
//    System.out.println("Next URL:"+nexturl);
//    System.out.println("Back URL:"+backurl);


    //log(" request NEXT ==> " + nexturl);
    //log(" request BACK ==> " + backurl);

    if( nexturl != null )
    {
        sb.append("<NEXTURL>").append(URLEncoder.encode(nexturl)).append("</NEXTURL>");
    }
    if( backurl != null )
    {
        sb.append("<BACKURL>").append(URLEncoder.encode(backurl)).append("</BACKURL>");
    }

	if(count != null)
	{
	    sb.append("<COUNT>").append(count).append("</COUNT>");
    }

	if(CID != null)
	{
        sb.append("<CID>").append(CID).append("</CID>");
    }
	if(searchId != null)
	{
        sb.append("<SEARCH-ID>").append(searchId).append("</SEARCH-ID>");
    }
	if(searchType != null)
	{
        sb.append("<SEARCH-TYPE>").append(searchType).append("</SEARCH-TYPE>");
    }

    //get the client session object from that get the session id.
    client = new ControllerClient(request,response);
    UserSession ussession=(UserSession)client.getUserSession();
    String sSessionId = ussession.getID();
    SessionID sessionIdObj = ussession.getSessionID();
    User user=ussession.getUser();

    // NOV 2004
    // jam added when pop-up login removed
    sb.append("<HEADER/>");
    sb.append(GlobalLinks.toXML(user.getCartridge()));
    sb.append("<FOOTER/>");

    String customerId = user.getCustomerID();
    String strContractId = user.getContractID();

    clientCustomizer=new ClientCustomizer(ussession);
    if(clientCustomizer.isCustomized())
    {
        // Stores the source attribute of the customized logo image
        sb.append("<CUSTOMIZED-LOGO>").append(clientCustomizer.getLogo()).append("</CUSTOMIZED-LOGO>");
		isPersonalizationPresent=clientCustomizer.checkPersonalization();
    }


    sb.append("<SESSION-ID>").append(sessionIdObj.toString()).append("</SESSION-ID>");

    // we need these even though this is the login
    // you may click any link on the page without logging in
  	sb.append("<PERSONALIZATION-PRESENT>").append(isPersonalizationPresent).append("</PERSONALIZATION-PRESENT>");
	sb.append("<PERSONALIZATION>").append(personalization).append("</PERSONALIZATION>");

    boolean loginSuccess = false;

    //display the login form if the displaylogin value is not null.
    if( displayLogin != null)
    {
        //build xml to display login form.
        sb.append("<LOGIN-FORM>");
        sb.append("<LOGIN-STATUS>false</LOGIN-STATUS>");
        sb.append("<EMAIL-EXISTS>true</EMAIL-EXISTS>");
        //sb.append("<DISPLAY-FORM>").append(displayForm).append("</DISPLAY-FORM>");
        sb.append("</LOGIN-FORM>");
    }
    else
    {
        // otherwise get the values of user name and password.
        String sLoginId = request.getParameter("email");
        String sPassword = request.getParameter("password");

        PersonalAccount pAccount = new PersonalAccount();
        //check for existance of email id.
        boolean emailFlag = pAccount.emailExists(sLoginId, customerId);

        // if the email id exists then check for authentication.
        if(emailFlag)
        {
            // changed authetication to Include CustomerID -->
            // changed authetication to Include strContractId -->
            String nUserId = pAccount.authenticateUser(sLoginId,
                                                        sPassword,
                                                        customerId,
                                                        strContractId);

            // if authentication is success build appropriate xml
            // redirect out of the page
            if ( nUserId !=  null)
            {
                loginSuccess = true;
                ussession.setProperty("P_USER_ID",nUserId);
                client.updateUserSession(ussession);

//log(" Success NEXT ==> " + nexturl);
//log(" Success BACK ==> " + backurl);
//System.out.println(" Success NEXT ==> " + nexturl);
//System.out.println(" Success BACK ==> " + backurl);


                if(nexturl != null)
                {
                    if(backurl != null)
                    {
                        client.setRedirectURL("/controller/servlet/Controller?" + nexturl + "&backurl="+ URLEncoder.encode(backurl));
                    }
                    else
                    {
                        client.setRedirectURL("/controller/servlet/Controller?" + nexturl);
                    }

                }
                else
                {
                    client.setRedirectURL("/controller/servlet/Controller?CID=myprofile&database="+database);
                }

                client.setRemoteControl();
                out.println("<!--END-->");

                // response ends here
                // controller cut-off signal
            }

        }
        // If login fails we end up here
        //build xml with error message.
        sb.append("<LOGIN-FORM>");
        sb.append("<LOGIN-STATUS>false</LOGIN-STATUS>");
        sb.append("<EMAIL-EXISTS>false</EMAIL-EXISTS>");
        //sb.append("<DISPLAY-FORM>").append(displayForm).append("</DISPLAY-FORM>");
        sb.append("</LOGIN-FORM>");
    }
    sb.append("</PAGE>");

    if(!loginSuccess)
    {
        out.println(sb.toString());
    }

%>