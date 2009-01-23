<!-- This file creates the xml for result coming from serach result page or from
     selected set page.
-->
<%@ page language="java" %>
<%@ page session="false" %>

<!-- import statements of Java packages-->
<%@ page import="java.util.*"%>
<%@ page import="java.net.*"%>
<%@ page import="java.io.*"%>
<%@ page import="javax.mail.internet.*"%>

<!--import statements of ei packages.-->
<%@ page import="org.ei.controller.ControllerClient"%>
<%@ page import="org.ei.session.*"%>
<%@ page import="org.ei.domain.*"%>
<%@ page import="org.ei.domain.personalization.*"%>
<%@ page import="org.ei.domain.Searches"%>
<%@ page import="org.ei.config.*"%>
<%@ page import="org.ei.email.*"%>

<%@ page errorPage="/error/errorPage.jsp" %>
<!--setting page buffer size to 20kb-->
<%@ page buffer="20kb"%>


<%
    // This variable for sessionId
    String sessionId = "";
    // This variable for searchId
    String searchId = "";
    // This variable for database name
    String database = "";
    // This variable tells us what action is being taken
    String action = "";
    String refEmail = LIBRARIAN_EMAIL;

    String section = "";
    String sectionid = "";
    String discipline = "";
    String disciplineid = "";
    String guru = "";
    String sUserId = "";
    String userId = null;
    SessionID sessionIdObj = null;

    boolean showShareCheckbox = true;

    // This variable is used to hold ControllerClient instance
    ControllerClient client = new ControllerClient(request, response);

    /**
     *  Getting the UserSession object from the Controller client .
     *  Getting the session id from the usersession.
     */
    UserSession ussession = (UserSession)client.getUserSession();
    sessionId = ussession.getID();
    sessionIdObj = ussession.getSessionID();

    sUserId = ussession.getProperty("P_USER_ID");

    ClientCustomizer clientCustomizer = new ClientCustomizer(ussession);

    if( sUserId != null)
    {
        userId = sUserId;
    }

    if(request.getParameter("searchid") != null)
    {
        searchId = request.getParameter("searchid");
    }

    if(request.getParameter("database") != null)
    {
        database = request.getParameter("database");
    }
    if(request.getParameter("section") != null)
    {
        section = request.getParameter("section");
    }
    if(request.getParameter("sectionid") != null)
    {
        sectionid = request.getParameter("sectionid");
    }
    if(request.getParameter("discipline") != null)
    {
        discipline = request.getParameter("discipline");
    }
    if(request.getParameter("disciplineid") != null)
    {
        disciplineid = request.getParameter("disciplineid");
    }
    if(request.getParameter("guru") != null)
    {
        guru = request.getParameter("guru");
    }
    action = request.getParameter("action");

    out.write("<PAGE>");
    out.write("<SESSION-ID>"+sessionIdObj.toString()+"</SESSION-ID>");
    out.write("<SEARCH-ID>"+searchId+"</SEARCH-ID>");
    out.write("<DATABASE>"+database+"</DATABASE>");
    out.write("<SECTION>"+section+"</SECTION>");
    out.write("<SECTIONID>"+sectionid+"</SECTIONID>");

    if(action == null)
    {
      // Writing the XML for the email form
      out.write("<ACTION>compose</ACTION>");
      if(sectionid.equals(ASK_AN_ENGINEER))
      {
        out.write("<DISCIPLINE>"+discipline+"</DISCIPLINE>");
        out.write("<DISCIPLINEID>"+disciplineid+"</DISCIPLINEID>");
        out.write("<GURU>"+guru+"</GURU>");
      }
      if(clientCustomizer.getRefEmail() != null && clientCustomizer.getRefEmail().length()>0)
      {
        showShareCheckbox = false;
      }
      out.write("<SHARE>"+String.valueOf(showShareCheckbox)+"</SHARE>");
    }
    else if(action.equalsIgnoreCase("send"))
    {
      List recipients = new ArrayList();
		  //List ccAddress = new ArrayList();
      //ccAddress.add("j.moschetto@elsevier.com");

      if(sectionid.equals(ASK_AN_ENGINEER))
      {
        // ask an engineer email
        recipients.add(ENGINEER_EMAIL);
      }
      else if(sectionid.equals(ASK_A_PRODUCTSPECIALIST))
      {
        recipients.add(SPECIALIST_EMAIL);
      }
      else if(sectionid.equals(ASK_A_LIBRARIAN))
      {

        if(clientCustomizer.getRefEmail() != null && clientCustomizer.getRefEmail().length()>0)
        {
          refEmail = clientCustomizer.getRefEmail();
          showShareCheckbox = false;
        }
        //log("refEmail: " + refEmail);

        recipients.add(refEmail);
      }

      String from_name="";
      if(request.getParameter("from_name") != null)
      {
          from_name = request.getParameter("from_name");
      }

      String from_email="";
      if(request.getParameter("from_email") != null)
      {
          from_email = request.getParameter("from_email");
      }

      String institution="";
      if(request.getParameter("institution") != null)
      {
          institution = request.getParameter("institution");
      }

      String message="";
      if(request.getParameter("message") != null)
      {
          message = request.getParameter("message");
      }

      String share_question="";
      if(request.getParameter("share_question") != null)
      {
          share_question = request.getParameter("share_question");
      }

      EIMessage eimessage = new EIMessage();
      eimessage.setSender("eicustomersupport@elseiver.com");
      eimessage.addTORecepients(recipients);
      // eimessage.addCCRecepients(ccAddress);
      eimessage.setSubject(section);
      eimessage.setSentDate(new Date());

      Writer messagebody = new StringWriter();
      if(sectionid.equals(ASK_AN_ENGINEER))
      {
        messagebody.write(section);
        messagebody.write("\n");
        messagebody.write(discipline);
        messagebody.write("\n");
        messagebody.write(guru);
        messagebody.write("\n");
      }
      if(showShareCheckbox)
      {
        messagebody.write("Share: ");
        if(!share_question.equalsIgnoreCase("on")) {
          messagebody.write(" User has requested we DO NOT share this question.");
        }
        else {
          messagebody.write(" Anonymously share this question and response with other Engineering Village users");
        }
      }
      messagebody.write("\n");
      messagebody.write("Message contents");
      messagebody.write("\n");
      messagebody.write("From: ");
      messagebody.write(from_name);
      messagebody.write("\n");
      messagebody.write("Institution: ");
      messagebody.write(institution);
      messagebody.write("\n");
      messagebody.write("From email: ");
      messagebody.write(from_email);
      messagebody.write("\n");
      messagebody.write("Message: \n");
      messagebody.write(message);
      messagebody.write("\n");
      messagebody.write("---------------------------------------------------");
      messagebody.write("\n");
      // if email is coming to default ei.org address, add user info
      if(LIBRARIAN_EMAIL.equals(refEmail))
      {
        messagebody.write(ussession.getUser().toString());
      }
      else
      {
        messagebody.write("This email was sent to you via Engineering Village Ask a Librarian feature.");
      }
      messagebody.write("\n");

      eimessage.setMessageBody(messagebody.toString());

      EMail email=EMail.getInstance();
      email.sendMessage(eimessage);

      out.write("<ACTION>confirm</ACTION>");
    }
    else
    {
      out.write("<ACTION>error</ACTION>");
    }

    out.write("</PAGE>");
    out.write("<!--END-->");
    out.flush();

%><%!

    private final String LIBRARIAN_EMAIL = "engineeringlibrarian@ei.org";
    private final String SPECIALIST_EMAIL = "colleen.hunter@elsevier.com";
    private final String ENGINEER_EMAIL = "colleen.hunter@elsevier.com";
    private final String ASK_AN_ENGINEER = "one";
    private final String ASK_A_PRODUCTSPECIALIST = "two";
    private final String ASK_A_LIBRARIAN = "three";

    public void jspInit()
    {
      try
      {
      } catch(Exception e) {
        e.printStackTrace();
      } finally {}
    }
%>

