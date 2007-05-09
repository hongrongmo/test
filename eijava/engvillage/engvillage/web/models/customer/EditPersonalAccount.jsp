<!--
	This page expects the following parameters based on the request
	* @param java.lang.String.EditDisplay.
	* @param java.lang.String.Email.
	* @param java.lang.String.FirstName.
	* @param java.lang.String.LastName.
	* @param java.lang.String.AnnounceFlag.
	* @param java.lang.String.Title.
	* @param java.lang.String.Password.
	and generates XML
	1. for the registration form with the values if the editdisplay param is not null.
	2. for the registration form with error message if the email already exists.
	3. for the confirmation if the updation of registration is successfull
	and sends email with updation conformation message to the user.
-->
<%@ page session="false" %>

<%@ page  import=" org.ei.domain.personalization.*"%>
<%@ page  import="org.ei.controller.ControllerClient"%>
<%@ page  import="org.ei.session.*"%>
<%@ page  import="org.ei.email.*"%>
<%@ page  import="java.util.Date"%>
<%@ page  import="org.ei.domain.*"%>

<%@ page errorPage="/error/errorPage.jsp"%>

<%
    boolean personalization = false;
    boolean isPersonalizationPresent=true;
	// declare variable to hold cutomerid.
	String strCustomerId = null;
	String strContractId = null;
	// declare variable to hold email
	String sEmail = null;
	// declare variable to hold first name.
	String sFirstName = null;
	// declare variable to hold last name.
	String sLastName = null;
	// declare variable to hold announce flag.
	String sAnnounceFlag = null;
	// declare variable to hold title.
	String sTitle = null;
	// declare variable to hold password.
	String sPassword = null;
	// declare variable to hold session id.
	String sSessionId = null;
	// declare variable to hold email flag.
	boolean emailFlag = false;
	// declare variable to hold xml string.
	StringBuffer sb = new StringBuffer("<PAGE>");

	// declare variable to hold client session.
	ControllerClient client = null;
	// declare variable to hold edit display value.
	String editDisplay = null;
	// declare variable to hold user id which is in the user session.
	String sUserId = null;
	// declare variable to hold user id.
	String nUserId = null;
	// declare variable to hold old email of the user.
	String oldEmail = null;

	// declare variable to hold database param value.
	String database = null;
	//declare variable to hold searchid param value.
	String searchId = null;
	SessionID sessionIdObj = null;
	//declare variable to hold results count param value.

	// declare variable to hold page number.
	String count = null;

	ClientCustomizer clientCustomizer=null;
	// Stores the source attribute of the customized logo image
	String customizedLogo="";

	// get the value of editdisplay param
	if( request.getParameter("editdisplay") != null)
	{
		editDisplay = request.getParameter("editdisplay");
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


	sb.append("<DATABASE>"+database+"</DATABASE>");

	if(count != null)
	{
	    sb.append("<COUNT>"+count+"</COUNT>");
    }
	if(searchId != null)
	{
	    sb.append("<SEARCH-ID>"+searchId+"</SEARCH-ID>");
    }
	// Create a session object using the Controllerclient object and get session id and user id from that object.
	client = new ControllerClient(request,response);
	UserSession ussession=(UserSession)client.getUserSession();
	sSessionId = ussession.getID();
	sessionIdObj = ussession.getSessionID();
	sUserId = ussession.getProperty("P_USER_ID");

	User user=ussession.getUser();
	strContractId = user.getContractID().trim();
	strCustomerId = user.getCustomerID().trim();

    sb.append("<HEADER/>");
    sb.append(GlobalLinks.toXML(user.getCartridge()));
    sb.append("<FOOTER/>");

	// check for the user id
	if( sUserId != null)
	{
		nUserId = sUserId;
		personalization = true;
	}

	sb.append("<SESSION-ID>"+sessionIdObj.toString()+"</SESSION-ID>");


	String customerId=user.getCustomerID().trim();
	clientCustomizer=new ClientCustomizer(ussession);
	if(clientCustomizer.isCustomized())
	{
		customizedLogo=clientCustomizer.getLogo();
		isPersonalizationPresent=clientCustomizer.checkPersonalization();
	}

	PersonalAccount pAccount = new PersonalAccount();
	// display the user information in the registration form.
	if(editDisplay != null)
	{
    	//user profile for the user id.
    	UserProfile userProfile = pAccount.getUserProfile(nUserId);

    	if(userProfile != null)
    	{
    		sb.append("<USER-PROFILE>");
    		sb.append("<USER-STATUS>edit</USER-STATUS>");
    		sb.append("<TITLE>"+userProfile.getTitle()+"</TITLE>");
    		sb.append("<FIRST-NAME>"+userProfile.getFirstName()+"</FIRST-NAME>");
    		sb.append("<LAST-NAME>"+userProfile.getLastName()+"</LAST-NAME>");
    		sb.append("<ANNOUNCE-FLAG>"+userProfile.getAnnounceFlag()+"</ANNOUNCE-FLAG>");
    		sb.append("<PASSWORD>"+userProfile.getPassword()+"</PASSWORD>");
    		sb.append("<EMAIL>"+userProfile.getEmail()+"</EMAIL>");
    		sb.append("</USER-PROFILE>");
    	}
	}
	else
	{
		// otherwise get all the parameters and update the user information.
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

		emailFlag = pAccount.emailExists(sEmail, strContractId);
		// check for email existance ,if the email does not exist in the system update information.
		if(!emailFlag)
		{
    		// get the old email id to send conformation.
    		oldEmail = pAccount.getEmail(nUserId);
    		// build user profile object.
    		UserProfile userProfile = new UserProfile(nUserId);

			userProfile.setEmail(sEmail);
			userProfile.setFirstName(sFirstName);
			userProfile.setLastName(sLastName);
			userProfile.setTitle(sTitle);
			userProfile.setAnnounceFlag(sAnnounceFlag);
			userProfile.setPassword(sPassword);

			//update user information.
			boolean updateFlag = pAccount.updateUserProfile(userProfile);
			// if the updation is successfull send mail to old and new email addresses.
			if(updateFlag)
			{
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
				//build the text required to send email
				emailText.append("Your Engineering Village Personal Account has been updated.\n\n");
				emailText.append("To access your Personal Account features on future visits to Engineering Village");
				emailText.append("(http://www.engineeringvillage.com), simply login using the e-mail address and password that you ");
				emailText.append("provided when you updated this account.\n\n");
				emailText.append("If you have any questions about your account, please contact us at: eicustomersupport@elsevier.com.\n\n");
				emailText.append("Thank you for using Engineering Village.\n\n");
				emailText.append("Ei Customer Support");

				try
				{
					EMail email = EMail.getInstance();
					EIMessage message = new EIMessage();
					message.setSubject("Your Ei Personal Account Has Been Updated");
					message.setSender("\"EI Customer Support\"<eicustomersupport@elsevier.com>");
					message.addTORecepient(sEmail);
					message.addTORecepient(oldEmail);
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
				//build the xml to display confirmation of updation.
				sb.append("<USER-PROFILE>");
				sb.append("<USER-STATUS>updated</USER-STATUS>");
				sb.append("<USER-EXISTS>true</USER-EXISTS>");
				sb.append("</USER-PROFILE>");
            }
        }
	    else
		{
			oldEmail = pAccount.getEmail(nUserId);
			// if the person doesn't change the email update the user information.
			if((oldEmail != null) && (oldEmail.equals(sEmail)))
			{
    			UserProfile userProfile = new UserProfile(nUserId);
    			//build user profile object.
    			userProfile.setCustomerId(strCustomerId);
    			userProfile.setEmail(sEmail);
    			userProfile.setFirstName(sFirstName);
    			userProfile.setLastName(sLastName);
    			userProfile.setTitle(sTitle);
    			userProfile.setAnnounceFlag(sAnnounceFlag);
    			userProfile.setPassword(sPassword);

    			//update the user information.
    			boolean updateFlag = pAccount.updateUserProfile(userProfile);
    			// if updation is successfull send confirmation message as an email.
    			if(updateFlag)
				{
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
						emailText.append("Dear "+sTitle+" "+sFirstName+" "+sLastName+":\n\n");
					}
					else
					{
						emailText.append("Dear  "+sFirstName+" "+sLastName+":\n\n");
					}
					//build the text required to send email.
					emailText.append("Your Engineering Village Personal Account has been updated.\n\n");
					emailText.append("To access your Personal Account features on future visits to Engineering Village");
					emailText.append("(http://www.engineeringvillage.com), simply login using the e-mail address and password that you ");
					emailText.append("provided when you updated this account.\n\n");
					emailText.append("If you have any questions about your account, please contact us at: eicustomersupport@elsevier.com.\n\n");
					emailText.append("Thank you for using Engineering Village.\n\n");
					emailText.append("Ei Customer Support");
					try
					{
						EMail email = EMail.getInstance();
						EIMessage message = new EIMessage();
						message.setSubject("Your Ei Personal Account Has Been Updated");
						message.setSender("\"EI Customer Support\"<eicustomersupport@elsevier.com>");
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
    				//build xml to display confirmation to the user.
    				sb.append("<USER-PROFILE>");
    				sb.append("<USER-STATUS>updated</USER-STATUS>");
    				sb.append("<USER-EXISTS>true</USER-EXISTS>");
    				sb.append("</USER-PROFILE>");
                }
            }
			else
			{
				// build xml if there is error while updating the information.
				sb.append("<USER-PROFILE>");
				sb.append("<USER-STATUS>edit</USER-STATUS>");
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
    }
    sb.append("<CUSTOMIZED-LOGO>" + customizedLogo + "</CUSTOMIZED-LOGO>");
    sb.append("<PERSONALIZATION-PRESENT>").append(isPersonalizationPresent).append("</PERSONALIZATION-PRESENT>");
    sb.append("<PERSONALIZATION>").append(personalization).append("</PERSONALIZATION>");

    sb.append("</PAGE>");

    response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
    response.setHeader("Pragma","no-cache"); //HTTP 1.0
    response.setDateHeader ("Expires", 0);//prevents caching at the proxy server
    out.println(sb.toString());

%>







