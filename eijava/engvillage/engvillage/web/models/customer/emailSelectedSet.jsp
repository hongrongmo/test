<%--
 * This page takes the below params as input and generates XML output.
 * This JSP is used records in selected set page of a particular type of display
 * (citation,detailed or abstract) are to be Emailed
 * @param java.lang.String.database
 * @param java.lang.String.SEARCHTYPE
 --%>
<%@ page language="java" %>
<%@ page session="false" %>
<%@ page errorPage="/error/errorPage.jsp" %>
<%@ page buffer="20kb"%>

<%--import statements of ei packages.--%>
<%@ page import="org.ei.domain.*"%>
<%@ page import="org.ei.config.*"%>
<%@ page import="org.ei.controller.ControllerClient"%>
<%@ page import="org.ei.session.*" %>
<%@ page import="org.ei.email.*"%>
<%@ page import="org.ei.util.*"%>

<%-- import statements of Java packages--%>
<%@ page import="java.util.*"%>
<%@ page import="java.net.*"%>
<%@ page import="java.io.*"%>
<%@ page import="javax.mail.internet.*"%>

<%@ page import="javax.xml.transform.TransformerFactory"%>
<%@ page import="javax.xml.transform.Templates"%>
<%@ page import="javax.xml.transform.Transformer"%>
<%@ page import="javax.xml.transform.TransformerException"%>
<%@ page import="javax.xml.transform.TransformerConfigurationException"%>
<%@ page import="javax.xml.transform.stream.StreamSource"%>
<%@ page import="javax.xml.transform.stream.StreamResult"%>
<%@ page import="javax.xml.transform.stream.StreamSource"%>

<%
    //This variable is for holding sessionId
    String sessionId = null;
    SessionID sessionIdObj = null;

    // Variable to hold the reference to displayFormat
    String displayFormat = "";
    String strXSLFilePrefix = "";

    BasketPage basketPage = null;
    DocumentBasket basket = null;

    ControllerClient client = new ControllerClient(request, response);

    UserSession ussession = (UserSession) client.getUserSession();
    sessionIdObj = ussession.getSessionID();
    sessionId = ussession.getID();

    basket = new DocumentBasket(sessionId);
    int numPages = basket.countPages();

    if(request.getParameter("displayformat") != null)
    {
        displayFormat = request.getParameter("displayformat");
        if(displayFormat.equalsIgnoreCase("citation"))
        {
            strXSLFilePrefix = "Citation";
            displayFormat=Citation.CITATION_FORMAT;
        }
        else if(displayFormat.equalsIgnoreCase("abstract"))
        {
            strXSLFilePrefix = "Abstract";
            displayFormat=Abstract.ABSTRACT_FORMAT;
        }
        else // THIS WILL BE THE DEFAULT FORMAT
        {
            strXSLFilePrefix = "Detailed";
            displayFormat=FullDoc.FULLDOC_FORMAT;
        }
    }

    if(numPages==0)
    {
        out.write("<PAGE><SESSION-ID>"+sessionIdObj.toString()+"</SESSION-ID>");
        out.write("<ERROR-PAGE>true</ERROR-PAGE>");
        out.write("</PAGE>");
        out.flush();
    }
    else
    {
        String fileName = "/tmp/"+(new GUID()).toString();
        PrintWriter outWriter = null;
        String to = "";
        String from = "";
        String subject = "";
        String message = "";
        String replyto = "";
        String sender = EIMessage.NOREPLY_SENDER;

        try
        {
            try
            {
                outWriter = new PrintWriter(new FileWriter(fileName));

                // setting from to be no-reply address to match sending domain
                from=EIMessage.NOREPLY_SENDER;
                to = request.getParameter("to");
                subject = request.getParameter("subject");
                message = request.getParameter("message");
                // 'from' form value will be used as the reply-to header and embedded into the message
                replyto = request.getParameter("from");


                message = "This email was sent to you on behalf of ".concat(replyto).concat(" \n \n").concat(message);

                outWriter.println("Subject: "+subject);
                outWriter.println("from: "+from);
                outWriter.println("to: "+to);
                outWriter.println("reply-to: "+replyto);
                outWriter.println("sender: "+sender);
                outWriter.println("");
                if(message != null && message.length() != 0)
                {
                    outWriter.println(message);
                }
                outWriter.println("");

                ServletContext context = getServletContext();
                java.net.URL xslURL = context.getResource("/views/customer/download" + strXSLFilePrefix + "Ascii.xsl");
                String xslURLString = xslURL.toString();

                TransformerFactory tFactory = new com.icl.saxon.TransformerFactoryImpl();
                Templates templates = tFactory.newTemplates(new StreamSource(xslURLString));

                basketPage = (BasketPage) basket.getBasketPage(displayFormat);

                String header = "<SECTION-DELIM><HEADER/></SECTION-DELIM>";
                Transformer transformer = templates.newTransformer();
                transformer.transform(new StreamSource(new StringReader(header)),
                              new StreamResult(outWriter));

                for(int z = 1; z<=numPages; ++z)
                {
                    basketPage = basket.pageAt(z, displayFormat);
                    StringWriter xmlWriter = new StringWriter();
                    xmlWriter.write("<SECTION-DELIM>");
                    basketPage.toXML(xmlWriter);
                    xmlWriter.write("</SECTION-DELIM>");
                    transformer = templates.newTransformer();
                    transformer.transform(new StreamSource(new StringReader(xmlWriter.toString())),
                                  new StreamResult(outWriter));
                }

                String footer = "<SECTION-DELIM><FOOTER/></SECTION-DELIM>";
                transformer = templates.newTransformer();
                transformer.transform(new StreamSource(new StringReader(footer)),
                              new StreamResult(outWriter));
            }
            finally
            {
                if(outWriter != null)
                {
                    outWriter.close();
                }
            }


            InputStream inStream = null;
            try
            {
                inStream = new FileInputStream(fileName);
                EMail email=EMail.getInstance();
                email.sendMessage(inStream);
            }
            finally
            {
                if(inStream != null)
                {
                    inStream.close();
                }
            }
        }
        finally
        {
            File file = new File(fileName);
            file.delete();
        }

        out.write("<PAGE>");
        out.write("<TO-ADDRESS>"+to+"</TO-ADDRESS>");
        out.write("<HEADER/>");
        out.write("<GLOBAL-LINKS/>");
        out.write("<FOOTER/>");
        out.write("</PAGE>");
        out.flush();
    }
%>