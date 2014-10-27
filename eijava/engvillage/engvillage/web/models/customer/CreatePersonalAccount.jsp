<!--
	This page expects the following parameters based on the request
	* @param java.lang.String.NewRegistrationForm.
	* @param java.lang.String.DisplayForm.
	* @param java.lang.String.Database.
	* @param java.lang.String.Email.
	* @param java.lang.String.FirstName.
	* @param java.lang.String.LastName.
	* @param java.lang.String.AnnounceFlag.
	* @param java.lang.String.Title.
	* @param java.lang.String.Password.
	and generates XML
	1. for the new registration form if the newregistration param is not null.
	2. for the registration form with error message if the email already exists.
	3. for the confirmation if the registration is successfull
	and sends email with conformation message to the user.
-->
<%@ page session="false" %>

<%@ page  import=" org.ei.domain.personalization.*"%>
<%@ page  import="org.ei.controller.ControllerClient"%>
<%@ page  import="org.ei.session.*"%>
<%@ page  import="org.ei.email.*"%>
<%@ page  import="org.ei.domain.*"%>
<%@ page  import="java.util.Date"%>
<%@ page import="java.net.URLDecoder"%>
<%@ page import="java.net.URLEncoder"%>

<%@ page errorPage="/error/errorPage.jsp"%>

<%
	// declare variable to hold user id from session.
	String sUserId = null;

	// delcare variable to hold customer id.
	String sCustomerId = null;
	// delcare variable to hold email.
	String sEmail = null;
	// delcare variable to hold first name.
	String sFirstName = null;
	// delcare variable to hold last name.
	String sLastName = null;
	// delcare variable to hold announce flag.
	String sAnnounceFlag = null;
	// delcare variable to hold title.
	String sTitle = null;
	// delcare variable to hold password.
	String sPassword = null;
	// delcare variable to hold session id.
	String sSessionId = null;
	SessionID sessionIdObj = null;
	// delcare variable to hold xml string.
	StringBuffer sb = new StringBuffer("<PAGE>");
	// declare variable to hold the client.
	ControllerClient client = null;
	// declare variable to hold newregistration param value
	String newRegistration=null;
	// declare variable to hold displayform value
	String displayForm = null;
	// declare variable to hold database value.
	String database = null;
	//declare variable to hold searchid param value.
	String searchId = null;
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

	ClientCustomizer clientCustomizer=null;
	// Stores the source attribute of the customized logo image
	String customizedLogo="";
    boolean personalization = false;
    boolean isPersonalizationPresent=true;

	String nexturl = null;
	String backurl = null;





	// get all the parameters required.
	if( request.getParameter("newregistration") != null)
	{
		newRegistration = request.getParameter("newregistration");
	}
	if( request.getParameter("displayform") != null)
	{
		displayForm = request.getParameter("displayform");
	}
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
	if( request.getParameter("nexturl") != null)
	{
			nexturl = request.getParameter("nexturl");
	}
	if( request.getParameter("backurl") != null)
	{
			backurl = request.getParameter("backurl");
	}

    if( nexturl != null )
    {
        sb.append("<NEXTURL>").append(URLEncoder.encode(nexturl)).append("</NEXTURL>");
    }

    if( backurl != null )
    {
        sb.append("<BACKURL>").append(URLEncoder.encode(backurl)).append("</BACKURL>");
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

	sb.append("<DATABASE>"+database+"</DATABASE>");
	sb.append("<COUNT>"+count+"</COUNT>");
	sb.append("<SEARCH-ID>"+searchId+"</SEARCH-ID>");


	// Create a session object using the Controllerclient object
	client = new ControllerClient(request,response);
	UserSession ussession=(UserSession)client.getUserSession();

	// get the customer id for the user from the user session object.
	sCustomerId = ussession.getUser().getCustomerID();
	sSessionId = ussession.getID();
	sessionIdObj = ussession.getSessionID();
	User user=ussession.getUser();

	String strContractId = user.getContractID().trim();
	String customerId=user.getCustomerID().trim();
	clientCustomizer=new ClientCustomizer(ussession);
	if(clientCustomizer.isCustomized())
	{
		customizedLogo=clientCustomizer.getLogo();
        isPersonalizationPresent=clientCustomizer.checkPersonalization();
	}
    sb.append("<HEADER/>");
    sb.append(GlobalLinks.toXML(user.getCartridge()));
    sb.append("<FOOTER/>");

	sb.append("<SESSION-ID>"+sessionIdObj.toString()+"</SESSION-ID>");

	// check for new registration and build the appropriate xml
	if(newRegistration != null)
	{
    	sb.append("<USER-PROFILE>");
    	sb.append("<USER-STATUS>failure</USER-STATUS>");
    	sb.append("<DISPLAY-FORM>"+displayForm+"</DISPLAY-FORM>");
    	sb.append("<TITLE></TITLE>");
    	sb.append("<FIRST-NAME></FIRST-NAME>");
    	sb.append("<LAST-NAME></LAST-NAME>");
    	sb.append("<ANNOUNCE-FLAG></ANNOUNCE-FLAG>");
    	sb.append("<EMAIL-EXISTS></EMAIL-EXISTS>");
    	sb.append("</USER-PROFILE>");
	}
	else
	{
    	// otherwise get all the parameters from the registration form
    	String nUserId = "";

    	if(request.getParameter("email") != null)
    	{
    		sEmail = request.getParameter("email").trim();
    	}
    	if(request.getParameter("firstname") != null)
    	{
    		sFirstName = request.getParameter("firstname").trim();
    	}
    	if(request.getParameter("lastname") != null)
    	{
    		sLastName = request.getParameter("lastname").trim();
    	}
    	if(request.getParameter("announceflag") != null)
    	{
    		sAnnounceFlag = request.getParameter("announceflag");
    	}
    	else
    	{
    		sAnnounceFlag = " ";
    	}
		if(request.getParameter("title") != null)
		{
			sTitle = request.getParameter("title").trim();
		}
		if(request.getParameter("password") != null)
		{
			sPassword = request.getParameter("password").trim();
		}

		PersonalAccount pAccount = new PersonalAccount();
		// first check for email existance.
		boolean emailFlag = pAccount.emailExists(sEmail,customerId);
		// if the email id is not exist create the account.
		if(!emailFlag)
		{

			// build user profile object with the required values.
			UserProfile userProfile = new UserProfile();
			userProfile.setCustomerId(sCustomerId);
			userProfile.setEmail(sEmail);
			userProfile.setFirstName(sFirstName);
			userProfile.setLastName(sLastName);
			userProfile.setTitle(sTitle);
			userProfile.setAnnounceFlag(sAnnounceFlag);
			userProfile.setPassword(sPassword);

			userProfile.setContractId(strContractId);

			// call create user profile method to create the account.
			pAccount.createUserProfile(userProfile);
            //  added customer id in the personal login authetication -->
			// get the user id for that user by authenticating with user name and password.
			nUserId = pAccount.authenticateUser(sEmail, sPassword, sCustomerId, strContractId);
			// after getting the user id place the user id in the user session for future availability.
			if( nUserId != null )
			{

				ussession.setProperty("P_USER_ID",nUserId);
				client.updateUserSession(ussession);
				client.setRemoteControl();
			}

			// get the user information for sending mail with conformation.
			userProfile = pAccount.getUserProfile(nUserId);
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
			emailText.append("An Engineering Village Personal Account has been created with the following information:\n\n");
			if(sTitle != null)
			{
				emailText.append("Title :"+sTitle+"\n");
			}
			//build the text required to send mail.
			emailText.append("First name : "+sFirstName+"\n");
			emailText.append("Last name : "+sLastName+"\n");
			emailText.append("E-mail address : "+sEmail+"\n\n");
			emailText.append("With your Personal Account, you can save searches, save records, and create E-mail Alerts—messages sent ");
			emailText.append("to you that contain up to 25 new records for a saved search executed on the most recent database update.\n\n");
			emailText.append("To access your Personal Account features on future visits to Engineering Village");
			emailText.append("(http://www.engineeringvillage.com), simply login using the e-mail address and password that ");
			emailText.append("you provided when you created this account.\n\n");
			emailText.append("If you have any questions about your account, please contact us at: eicustomersupport@elsevier.com.\n\n");
			emailText.append("Thank you for using Engineering Village.\n\n");
			emailText.append("Ei Customer Support");

			try
			{
    			// send mail with the required information.
    			EIMessage message = new EIMessage();
          //setSender no longer sets from address - use setFrom for From and setSender for Sender
          //message.setSender("\"EI Customer Support\"<eicustomersupport@elsevier.com>");
          message.setSender(EIMessage.DEFAULT_SENDER);
          message.setFrom(EIMessage.DEFAULT_SENDER);

    			message.addTORecepient(sEmail);
    			message.setSubject("Your Ei Personal Account Has Been Created");
    			message.setMessageBody(emailText.toString());
    			long milli = System.currentTimeMillis();
    			Date date=new Date(milli);
    			message.setSentDate(date);
    			EMail email = EMail.getInstance();
    			email.sendMessage(message);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			// build the corresponding xml to display confirmation.
			sb.append("<USER-PROFILE>");
			sb.append("<USER-STATUS>success</USER-STATUS>");
			sb.append("<DISPLAY-FORM>"+displayForm+"</DISPLAY-FORM>");
			sb.append("</USER-PROFILE>");
            personalization = true;
		}
		else
		{
			// display the registration form with error message if the email already exists in the system.
			sb.append("<USER-PROFILE>");
			sb.append("<USER-STATUS>failure</USER-STATUS>");
			sb.append("<DISPLAY-FORM>"+displayForm+"</DISPLAY-FORM>");
			sb.append("<TITLE>"+sTitle+"</TITLE>");
			sb.append("<FIRST-NAME>"+sFirstName+"</FIRST-NAME>");
			sb.append("<LAST-NAME>"+sLastName+"</LAST-NAME>");
			sb.append("<ANNOUNCE-FLAG>"+sAnnounceFlag+"</ANNOUNCE-FLAG>");
			sb.append("<PASSWORD>"+sPassword+"</PASSWORD>");
			sb.append("<EMAIL>"+sEmail+"</EMAIL>");
			sb.append("<EMAIL-EXISTS>"+emailFlag+"</EMAIL-EXISTS>");
			sb.append("</USER-PROFILE>");
        }
	 }

	 sb.append("<CUSTOMIZED-LOGO>" + customizedLogo + "</CUSTOMIZED-LOGO>");
	 sb.append("<PERSONALIZATION-PRESENT>").append(isPersonalizationPresent).append("</PERSONALIZATION-PRESENT>");
	 sb.append("<PERSONALIZATION>").append(personalization).append("</PERSONALIZATION>");

	 sb.append("</PAGE>");
	 out.println(sb.toString());
%>







