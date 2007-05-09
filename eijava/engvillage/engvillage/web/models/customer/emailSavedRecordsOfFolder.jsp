<%--
 * jsp file that is required to view records in a folder
 * to the savetofolder.xsl */
 * @param java.lang.String.folderid
 * @param java.lang.String.cid
 * @param java.lang.String.databse
--%>
<%@ page language="java" %>
<%@ page session="false" %>
<%@ page errorPage="/error/errorPage.jsp" %>
<%@ page buffer="20kb"%>

<%-- import statements of Java packages--%>
<%@ page import="java.util.*"%>
<%@ page import="java.net.*"%>
<%@ page import="java.io.*"%>

<%--import statements of ei packages.--%>
<%@ page import="org.ei.domain.personalization.*"%>
<%@ page import="org.ei.config.*"%>
<%@ page import="org.ei.domain.*"%>
<%@ page import="org.ei.controller.ControllerClient"%>
<%@ page import="org.ei.session.*" %>
<%@ page import="org.ei.email.*"%>
<%@ page import="javax.mail.internet.*"%>

<%@ page import="javax.xml.transform.TransformerFactory"%>
<%@ page import="javax.xml.transform.Transformer"%>
<%@ page import="javax.xml.transform.TransformerException"%>
<%@ page import="javax.xml.transform.TransformerConfigurationException"%>
<%@ page import="javax.xml.transform.stream.StreamSource"%>
<%@ page import="javax.xml.transform.stream.StreamResult"%>
<%@ page import="javax.xml.transform.stream.StreamSource"%>
<%
    // Variable to hold the default CID for the Saved Record.
    String cid = null;

    // Variable to hold the database name for which the search results were obtained.
    String database = null;

    //The document format
    String documentFormat = null;

    //the sessionid
    SessionID sessionIdObj = null;
    String sessionid = null;

    // Object reference to FolderPage object
    FolderPage folderPage = null;
    Folder folder = null;
    String folderId = "";
    int folderSize = 0 ;

    // Object reference to SavedRecords object
    SavedRecords savedRecords = null;
    //for redirection
    String redirect = null;

    // Create a session object using the Controllerclient object
    ControllerClient client = new ControllerClient(request,response);
    UserSession ussession = (UserSession)client.getUserSession();

    sessionid = ussession.getID();
    sessionIdObj = ussession.getSessionID();
    User user = ussession.getUser();

    // Varaible to hold the current User id
    String userId = ussession.getProperty("P_USER_ID");

    // Retrieve all the request parameters
    if(request.getParameter("redirect") != null)
    {
        redirect=request.getParameter("redirect");
    }

    if(request.getParameter("CID") != null)
    {
        cid=request.getParameter("CID");
    }

    if(request.getParameter("folderid") != null)
    {
        folderId=request.getParameter("folderid");
    }

    if(request.getParameter("database") != null)
    {
        database=request.getParameter("database");
    }

    savedRecords = new SavedRecords(userId);

    String fName = savedRecords.getFolderName(folderId);
    folder = new Folder(folderId,fName);
    folderSize = savedRecords.getSizeOfFolder(folder);

    /*
     *  Gets the results for the current page
     */
    String strDisplayFormat = "";
    String strFilePrefix = "";

    if(request.getParameter("displayformat") != null)
    {
        strDisplayFormat = request.getParameter("displayformat");
    }

    if(strDisplayFormat.equalsIgnoreCase("citation"))
    {
        documentFormat = Citation.CITATION_FORMAT;
        strFilePrefix = "Citation";
    }
    else if(strDisplayFormat.equalsIgnoreCase("abstract"))
    {
        documentFormat = Abstract.ABSTRACT_FORMAT;
        strFilePrefix = "Abstract";
    }
    else if(strDisplayFormat.equalsIgnoreCase("fulldoc"))
    {
        documentFormat = FullDoc.FULLDOC_FORMAT;
        strFilePrefix = "Detailed";
    }

    if(folderSize > 0)
    {
        // Create the basketPage object with the documents
        folderPage = (FolderPage) savedRecords.viewRecordsInFolder(folder, documentFormat);
    }

    if( (folderSize>0) && (redirect==null))
    {
        StringBuffer xmlString = new StringBuffer();
        xmlString.append("<PAGE><!--BH--><HEADER/><!--EH-->");

        StringWriter xmlWriter = new StringWriter();
        folderPage.toXML(xmlWriter);
        xmlString.append(xmlWriter.toString());

        xmlString.append("<!--*-->");

        //Signal footer section
        xmlString.append("<!--BF--><FOOTER/><!--EF-->");
        xmlString.append("</PAGE>");
        xmlString.append("<!--END-->");

        // -------------------------- Create and Send Email ----------------------
        // -------------------------- Create and Send Email ----------------------
        // -------------------------- Create and Send Email ----------------------

        String to="";
        String from="";
        String subject="";
        String message = "";

        // getting FROM  parameter from request object.
        from = request.getParameter("from");
        // getting TO  parameter from request object.
        to = request.getParameter("to");
        // getting SUBJECT  parameter from request object.
        subject = request.getParameter("subject");
        // getting MESSAGE  parameter from request object.
        message = request.getParameter("message");

        long lo=System.currentTimeMillis();
        java.util.Date d=new java.util.Date(lo);
        // create an instance of EMail Object for sending email
        EMail email=EMail.getInstance();

        // create an instance of eimessage and call the respective set methods
        EIMessage eimessage = new EIMessage();
        eimessage.setSender(from);
        List l=new ArrayList();
        StringTokenizer stoken =new StringTokenizer(to,",");
        while(stoken.hasMoreTokens())
        {
            l.add(stoken.nextToken());
        }

        eimessage.addTORecepients(l);
        eimessage.setSubject(subject);
        eimessage.setSentDate(d);

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
    }
    else
    {
        // This "should" never happen because downloadForm.jsp (DownloadForm.xsl)
        // which is called before this does the same check, preventing
        // the display of the download form when the folder is empty
        //
        // forward when the no of documents in basket is zero
        if((cid != null) && (!cid.equals("printErrorSelectedSet")))
        {
            String urlString = "/controller/servlet/Controller?CID=printErrorSelectedSet";
            client.setRedirectURL(urlString);
            client.setRemoteControl();
        }
    }

%>
