<!--
	This page expects the following params based on the request
	* @param java.lang.String.Email.
	* @param java.lang.String.display.
	* @param java.lang.String.displayform.
	* @param java.lang.String.database.
	and gets the password for the email then sends
	mail to the user.
-->
<%@ page session="false" %>

<%@ page  import=" org.ei.domain.personalization.*"%>
<%@ page  import="org.ei.controller.ControllerClient"%>
<%@ page  import="org.ei.session.*"%>
<%@ page  import="org.ei.email.*"%>
<%@ page  import="java.util.Date"%>
<%@ page  import="org.ei.domain.*"%>
<%@ page import="java.net.URLEncoder"%>

<%@ page errorPage="/error/errorPage.jsp"%>




<%
    boolean personalization = false;
    boolean isPersonalizationPresent=true;
    // declare variable to hold user id from session.
    String sUserId = null;

	// declare variable to hold email
	String sEmail = null;
	// declare variable to hold first name.
	String sFirstName = null;
	// declare variable to hold last name.
	String sLastName = null;
	// declare variable to hold title.
	String sTitle = null;
	// declare variable to hold password.
	String sPassword = null;
	// declare variable to hold session id.
	String sSessionId = null;
	// declare variable to hold email flag.
	boolean emailFlag = false;
	// declare variable to hold display param value.
	String display = null;
	// declare variable to hold display form param value.
	String displayForm = null;
	// declare variable to hold database.
	String database = null;
	//declare variable to hold searchType param value.
	String searchType = null;
	//declare variable to hold searchid param value.
	String searchId = null;
	//declare variable to hold results count param value.

	// declare variable to hold page number.
	String count = null;
	// declare variable to hold docids
	String docids = null;
	// declare variable to hold databaseid.
	String databaseId = null;
	// declare variable to hold option value.
	String optionValue = null;
	// declare variable to hold queryid.
	String queryId = null;
	// declare variable to hold queryid.
	String resultsFormat = null;
	// declare variable to hold value from where it is comming
	String source = null;
	// declare variable to hold user id.
	String nUserId = null;
	// declare variable to hold xml string.
	StringBuffer sb = new StringBuffer("<PAGE>");

	ClientCustomizer clientCustomizer=null;
	// Stores the source attribute of the customized logo image
	String customizedLogo="";

	// declare variable to hold client session.
	ControllerClient client = null;

	// get the value of formdisplay
	if( request.getParameter("displayform") != null)
	{
		displayForm = request.getParameter("displayform");
	}
	if( request.getParameter("display") != null)
	{
		display = request.getParameter("display");
	}
	// get the value of database param
	if( request.getParameter("database") != null)
	{
		database = request.getParameter("database");
	}
	if( request.getParameter("count") != null)
	{
			count = request.getParameter("count");
	}
	if( request.getParameter("searchid") != null)
	{
			searchId = request.getParameter("searchid");
	}
	if( request.getParameter("searchtype") != null)
	{
			searchType = request.getParameter("searchtype");
	}
	if( request.getParameter("docidlist") != null)
	{
			docids = request.getParameter("docidlist");
	}
	if( request.getParameter("databaseid") != null)
	{
			databaseId = request.getParameter("databaseid");
	}
	if( request.getParameter("option") != null)
	{
			optionValue = request.getParameter("option");
	}
	if( request.getParameter("queryid") != null)
	{
			queryId = request.getParameter("queryid");
	}
	if( request.getParameter("resultsformat") != null)
	{
			resultsFormat = request.getParameter("resultsformat");
	}
	if( request.getParameter("source") != null)
	{
			source = request.getParameter("source");
	}

	// build xml string
	if( docids != null )
	{
		sb.append("<DOCIDS>"+docids+"</DOCIDS>");
	}
	if( databaseId != null )
	{
		sb.append("<DATABASE-ID>"+databaseId+"</DATABASE-ID>");
	}
	if( optionValue != null )
	{
		sb.append("<OPTION-VALUE>"+optionValue+"</OPTION-VALUE>");
	}
	if( queryId != null )
	{
		sb.append("<QUERY-ID>"+queryId+"</QUERY-ID>");
	}
	if( source != null )
	{
		sb.append("<SOURCE>"+source+"</SOURCE>");
	}
	if( queryId != null )
	{
		sb.append("<RESULTS-FORMAT>"+resultsFormat+"</RESULTS-FORMAT>");
	}
	if(database != null)
	{
		sb.append("<DATABASE>"+database+"</DATABASE>");
	}
	if(count != null)
	{
		sb.append("<COUNT>"+count+"</COUNT>");
	}
	if(searchId != null)
	{
		sb.append("<SEARCH-ID>"+searchId+"</SEARCH-ID>");
	}
	if(searchType != null)
	{
		sb.append("<SEARCH-TYPE>"+searchType+"</SEARCH-TYPE>");
	}

	// Create a session object using the Controllerclient object and
	// get session id and user id from that object.
	client = new ControllerClient(request,response);
	UserSession ussession=(UserSession)client.getUserSession();
	sSessionId = ussession.getID();
	sUserId = ussession.getProperty("P_USER_ID");
	User user=ussession.getUser();

	if((sUserId != null) && (sUserId.length() != 0))
    {
        personalization = true;
    }
	String strContractId = user.getContractID().trim();

	String customerId=user.getCustomerID().trim();
	clientCustomizer=new ClientCustomizer(ussession);
	if(clientCustomizer.isCustomized())
	{
		customizedLogo=clientCustomizer.getLogo();
		isPersonalizationPresent=clientCustomizer.checkPersonalization();
	}

    String nexturl = request.getParameter("nexturl");
    String backurl = request.getParameter("backurl");

    if( nexturl != null )
    {
        sb.append("<NEXTURL>").append(URLEncoder.encode(nexturl)).append("</NEXTURL>");
    }
    if( backurl != null )
    {
        sb.append("<BACKURL>").append(URLEncoder.encode(backurl)).append("</BACKURL>");
    }

    sb.append("<HEADER/>");
    sb.append(GlobalLinks.toXML(user.getCartridge()));
    sb.append("<FOOTER/>");

	// check to display the form
	if( display != null)
	{
		sb.append("<PASSWORD-REQUEST>");
		sb.append("<DISPLAY-FORM>"+displayForm+"</DISPLAY-FORM>");
		sb.append("<EMAIL-EXISTS>true</EMAIL-EXISTS>");
		sb.append("<PASSWORD-STATUS>false</PASSWORD-STATUS>");
		sb.append("</PASSWORD-REQUEST>");
	}
	else
	{
		if( request.getParameter("email") != null)
		{
			sEmail = request.getParameter("email");
		}
		PersonalAccount pAccount = new PersonalAccount();


		emailFlag = pAccount.emailExists(sEmail,customerId);

		// display the user information in the registration form.
		if(emailFlag)
		{
				// get the password
				sPassword = pAccount.getPassword(sEmail);
				// authenticate the user
// change for adding customer id in personal profile authetication -->
				nUserId = pAccount.authenticateUser(sEmail , sPassword, customerId, strContractId);
				// get the user information for sending mail with conformation.
				UserProfile userProfile = pAccount.getUserProfile(nUserId);
				// create the email format.
				StringBuffer emailText = new StringBuffer();

				sEmail = userProfile.getEmail();
				sTitle = userProfile.getTitle();
				sFirstName = userProfile.getFirstName();
				sLastName = userProfile.getLastName();

				if ( sTitle != null)
				{
					emailText.append("Dear  "+sTitle+" "+sFirstName+" "+sLastName+":\n\n");
				}
				else
				{
					emailText.append("Dear  "+sFirstName+" "+sLastName+":\n\n");
				}
				emailText.append("Here is your Personal Account password that you requested from Engineering Village:\n\n");
				emailText.append("Password : "+sPassword+"\n\n");
				emailText.append("To access your Personal Account features on future visits to Engineering Village");
				emailText.append("(http://www.engineeringvillage.com/), simply login using this e-mail address and password.\n\n");
				emailText.append("If you have any questions about your account, please contact us at: eicustomersupport@elsevier.com.\n\n");
				emailText.append("Thank you for using Engineering Village.\n\n");
				emailText.append("Ei Customer Support");

				try
				{
						// send mail with the required information.
						EMail email = EMail.getInstance();
						EIMessage message = new EIMessage();
						message.setSubject("Your Requested Password");
            //setSender no longer sets from address - use setFrom for From and setSender for Sender
            //message.setSender("\"EI Customer Support\"<eicustomersupport@elsevier.com>");
            message.setSender(EIMessage.DEFAULT_SENDER);
            message.setFrom(EIMessage.DEFAULT_SENDER);

						message.addTORecepient(sEmail);
						long milli = System.currentTimeMillis();
						Date date=new Date(milli);
						message.setSentDate(date);
						message.setMessageBody(emailText.toString());
						email.sendMessage(message);

				}
				catch(Exception e)
				{
					e.printStackTrace();
							}


				sb.append("<PASSWORD-REQUEST>");
				sb.append("<EMAIL-EXISTS>true</EMAIL-EXISTS>");
				sb.append("<PASSWORD-STATUS>true</PASSWORD-STATUS>");
				sb.append("</PASSWORD-REQUEST>");
		}
		else
		{
				sb.append("<PASSWORD-REQUEST>");
				sb.append("<DISPLAY-FORM>"+displayForm+"</DISPLAY-FORM>");
				sb.append("<EMAIL-EXISTS>false</EMAIL-EXISTS>");
				sb.append("<PASSWORD-STATUS>false</PASSWORD-STATUS>");
				sb.append("</PASSWORD-REQUEST>");
		}
	}
	sb.append("<CUSTOMIZED-LOGO>" + customizedLogo + "</CUSTOMIZED-LOGO>");
	sb.append("<PERSONALIZATION-PRESENT>").append(isPersonalizationPresent).append("</PERSONALIZATION-PRESENT>");
	sb.append("<PERSONALIZATION>").append(personalization).append("</PERSONALIZATION>");
	sb.append("</PAGE>");
	out.println(sb.toString());
%>







