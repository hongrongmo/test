<%@ page language="java" %>
<%@ page session="false" %>
<%@ page import=" java.util.*"%>
<%@ page import=" java.net.*"%>
<%@ page import="org.ei.domain.*"%>
<%@ page import="org.ei.controller.ControllerClient"%>
<%@ page import="org.ei.session.*"%>
<%@ page import="org.ei.domain.personalization.*"%>
<%@ page buffer="20kb"%>
<%

	ControllerClient client = null;
	DatabaseConfig databaseConfig = DatabaseConfig.getInstance();
	DocID  docID=null;
	Page page1 = null;
	String sessionId  = null;
	String documentFormat=null;
	String docid = null;
	List DocIDList = null;

%>
<%

	// Create a session object using the controllerclient.java
	client = new ControllerClient(request, response);
	UserSession ussession=(UserSession)client.getUserSession();
	sessionId = ussession.getID();
	 IEVWebUser user = ussession.getUser();

	if(request.getParameter("m_id") !=null)
	{
		docid = request.getParameter("m_id");
	}
	DocIDList = new ArrayList();
	docID = new DocID(docid.trim(),
			  databaseConfig.getDatabase(docid.substring(0,3)));
	DocIDList.add(docID);
	documentFormat = "RIS";

	MultiDatabaseDocBuilder builder = new MultiDatabaseDocBuilder();
	List bList = null;
	bList = builder.buildPage(DocIDList, documentFormat);

	StringBuffer strbFilename = new StringBuffer();
	strbFilename.append(documentFormat);
	strbFilename.append("_.ris");
        // do not set disposition or filename - refworks needs only mime type
        //if(!strDownloadFormat.equalsIgnoreCase("refworks"))
  	//{
        //   client.setContentDispositionFilenameTimestamp(strbFilename.toString());
        //}
	client.setRemoteControl();

	PageEntryBuilder eBuilder = new PageEntryBuilder(sessionId);
	List pList = eBuilder.buildPageEntryList(bList);
	page1 = new Page();
	page1.addAll(pList);

	StringBuffer  basketContentStringBuffer  = new StringBuffer();

	basketContentStringBuffer.append("<PAGE><!--BH-->");
	basketContentStringBuffer.append("<!--EH-->");
	out.write(basketContentStringBuffer.toString());
	
	for(int i=0; i<page1.docCount();i++)
	{
		EIDoc doc = page1.docAt(i);
		out.write("<!--BR--><PAGE-ENTRY>");
		doc.toXML(out);
		out.write("</PAGE-ENTRY><!--ER-->");
	
	}

	out.write("<!--*-->");
	//Signale footer section
	out.write("<!--BF--><FOOTER/><!--EF-->");
	out.write("</PAGE>");
	out.flush();

%>