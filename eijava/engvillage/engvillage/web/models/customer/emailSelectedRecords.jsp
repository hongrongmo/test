<%--
 * This page takes the below params as input and generates XML output.
 * This JSP is used when some of the records in a page of a particular type of display
 * (citation,detailed or abstract) are to be Emailed
 *
 * @param java.lang.String.searchid
  * @param java.lang.String.database
 * @param java.lang.String.displayformat
 * @param java.lang.String.docidlist
 * @param java.lang.String.handlelist
--%>

<%@ page language="java" %>
<%@ page session="false" %>

<%-- import statements of Java packages--%>
<%@ page import="java.util.*"%>
<%@ page import="java.net.*"%>
<%@ page import="java.io.*"%>
<%@ page import="org.ei.config.*"%>
<%@ page  errorPage="/error/errorPage.jsp"%>

<%--import statements of ei packages.--%>
<%@ page import="org.ei.domain.*"%>
<%@ page import="org.ei.controller.ControllerClient"%>
<%@ page import="org.ei.session.*"%>
<%@ page import="org.ei.components.feedback.*"%>
<%@ page import="org.ei.email.*"%>
<%@ page import="org.ei.domain.Searches"%>

<%@ page import="javax.mail.internet.*"%>
<%@ page import="javax.xml.transform.TransformerFactory"%>
<%@ page import="javax.xml.transform.Transformer"%>
<%@ page import="javax.xml.transform.TransformerException"%>
<%@ page import="javax.xml.transform.TransformerConfigurationException"%>
<%@ page import="javax.xml.transform.stream.StreamSource"%>
<%@ page import="javax.xml.transform.stream.StreamResult"%>
<%@ page import="javax.xml.transform.stream.StreamSource"%>

<%
    // Variable used to hold the reference to ControllerClient object
    ControllerClient client = null;
    // Variable to hold the reference to docID object
    DocID  docID=null;

    Page page1 = null;
    // Variable to hold the reference to sessionId
    String sessionId  = null;
    SessionID sessionIdObj = null;
    // Variable to hold the reference to searchId
    String searchId  = null;
    // Variable to hold the reference to displayFormat
    String displayFormat = null;
    //This variable for holding Document ids.
    String docids= null;
    //This variable for holding handles
    String  handles = null;
    //This variable for database name
    String database = null;
    // Variable to hold the reference of final DocIDList(List that is built)
    List DocIDList = null;
    // Variable to hold the reference to docIdList
    List docIdList = null;
    // Variable to hold the reference to handleList
    List handleList = null;
    // Variable for holding xmlString
    StringBuffer xmlString = null;

    DatabaseConfig databaseConfig = DatabaseConfig.getInstance();
    // Create a session object using the controllerclient.java
    client = new ControllerClient(request, response);
    UserSession ussession=(UserSession)client.getUserSession();

    sessionId = ussession.getID();
    sessionIdObj = ussession.getSessionID();
    //Getting the searchid parameter from the request.
    if(request.getParameter("searchid") != null)
    {
        searchId = request.getParameter("searchid");
    }

    //Getting the displayformat parameter from the request.(citation,abstract,detailed)
    if(request.getParameter("displayformat") != null)
    {
        displayFormat = request.getParameter("displayformat");
    }
    //Getting the docidlist parameter from the request.
    if(request.getParameter("docidlist") !=null)
    {
        docids = request.getParameter("docidlist");
    }
    //Getting the handlelist parameter from the request.
    if(request.getParameter("handlelist")!=null)
    {
        handles = request.getParameter("handlelist");
    }

    StringTokenizer st = new StringTokenizer(docids,",");
    docIdList = new ArrayList();

    while(st.hasMoreTokens())
    {
        String docid = st.nextToken();
        if(!docid.trim().equals(""))
        {
            docIdList.add(docid);
        }
    }

    StringTokenizer st1 = new StringTokenizer(handles,",");
    handleList = new ArrayList();

    while(st1.hasMoreTokens())
    {
        String handle  = st1.nextToken();
        if(!handle.trim().equals(""))
        {
            handleList.add(handle);
        }
    }

    //  Check if the no. of docid and handles are same.
    //  If they are same, then construct DocId Object
    DocIDList = new ArrayList();

    int docIdSize = docIdList.size();
    int handleSize = handleList.size();

    if( docIdSize  == handleSize )
    {
        int handle = 0;
        String docid = null;
        for(int i = 0; i < docIdSize ; i++)
        {
            handle =  Integer.parseInt((String) handleList.get(i));
            docid =  (String) docIdList.get(i);
            docID = new DocID(handle,
                            docid.trim(),
                            databaseConfig.getDatabase(docid.substring(0,3)));
            DocIDList.add(docID);
        }
    }

    MultiDatabaseDocBuilder builder = new MultiDatabaseDocBuilder();
    List bList = null;
    String strFilePrefix = "";

    if(displayFormat.equals("citation"))
    {
        strFilePrefix = "Citation";
        bList = builder.buildPage(DocIDList, Citation.CITATION_FORMAT);
    }
    else if(displayFormat.equals("abstract"))
    {
        strFilePrefix = "Abstract";
        bList = builder.buildPage(DocIDList, Abstract.ABSTRACT_FORMAT);
    }
    else if(displayFormat.equals("detailed"))
    {
        strFilePrefix = "Detailed";
        bList = builder.buildPage(DocIDList, FullDoc.FULLDOC_FORMAT);
    }

    PageEntryBuilder eBuilder = new PageEntryBuilder(sessionId);
    List pList = eBuilder.buildPageEntryList(bList);
    page1 = new Page();
    page1.addAll(pList);

    xmlString = new StringBuffer();
    xmlString.append("<PAGE><!--BH--><HEADER/><!--EH-->");

    StringWriter xmlWriter = new StringWriter();
    page1.toXML(xmlWriter);
    xmlString.append(xmlWriter.toString());

    xmlString.append("<!--*-->");
    //Signale footer section
    xmlString.append("<!--BF--><FOOTER/><!--EF-->");
    xmlString.append("</PAGE>");
    xmlString.append("<!--END-->");

    // -------------------------- Create and Send Email ----------------------
    // -------------------------- Create and Send Email ----------------------
    // -------------------------- Create and Send Email ----------------------

    String to="";
    String from="";
    String replyto="";
    String subject="";
    String message = "";
    String sender=EIMessage.NOREPLY_SENDER;

    // setting from to be no-reply address to match sending domain
    from = EIMessage.NOREPLY_SENDER;
    // 'from' form value will be used as the reply-to header and embedded into the message
    replyto = request.getParameter("from");
    // getting TO  parameter from request object.
    to = request.getParameter("to");
    // getting SUBJECT  parameter from request object.
    subject = request.getParameter("subject");
    // getting MESSAGE  parameter from request object.
    message = request.getParameter("message");

    message = "This email was sent to you on behalf of ".concat(replyto).concat(" \n \n").concat(message);

    long lo=System.currentTimeMillis();
    java.util.Date d=new java.util.Date(lo);
    // create an instance of EMail Object for sending email
    EMail email=EMail.getInstance();

    // create an instance of eimessage and call the respective set methods
    EIMessage eimessage = new EIMessage();

    eimessage.setFrom(from);
    eimessage.setSender(sender);

    List l=new ArrayList();
    StringTokenizer stoken =new StringTokenizer(to,",");

    while(stoken.hasMoreTokens())
    {
        l.add(stoken.nextToken());
    }

    eimessage.addTORecepients(l);
    eimessage.setSubject(subject);
    eimessage.setSentDate(d);

    List lreplyto=new ArrayList();
    StringTokenizer token =new StringTokenizer(replyto,",");
    while(token.hasMoreTokens())
    {
    	lreplyto.add(token.nextToken());
    }
    eimessage.addReplyToRecepients(lreplyto);





    TransformerFactory tfactory = new com.icl.saxon.TransformerFactoryImpl();
    ServletContext context = getServletContext();
    java.net.URL xslURL = context.getResource("/views/customer/download" + strFilePrefix + "Ascii.xsl");
    Transformer transformer = tfactory.newTransformer(new StreamSource(xslURL.toString()));

    StringReader sr=new StringReader(xmlString.toString());
    StringWriter sw=new StringWriter();
    transformer.transform(new StreamSource(sr),new StreamResult(sw));

    eimessage.setMessageBody(message+"\n \n"+sw.toString());

    email.sendMessage(eimessage);

    // output XML to EmailConfirmation.xsl for success page
    out.write("<PAGE>");
    out.write("<TO-ADDRESS>"+to+"</TO-ADDRESS>");
    out.write("<HEADER/>");
    out.write("<GLOBAL-LINKS/>");
    out.write("<FOOTER/>");
    out.write("</PAGE>");
    out.flush();


    // -------------------------- Create and Send Email ----------------------
    // -------------------------- Create and Send Email ----------------------
    // -------------------------- Create and Send Email ----------------------
 %>
