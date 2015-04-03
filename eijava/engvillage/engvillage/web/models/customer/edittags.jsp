<%@ page language="java" %>
<%@ page session="false" %>

<!--
addSelectedRange.jsp
-->

<!-- import statements of Java packages-->
<%@ page import="java.util.*"%>
<%@ page import="java.net.URLEncoder"%>

<!--import statements of ei packages.-->
<%@ page import="org.ei.domain.*"%>
<%@ page import="org.ei.controller.ControllerClient"%>
<%@ page import="org.ei.session.*"%>
<%@ page import="org.ei.config.*"%>
<%@ page import="org.ei.query.base.*"%>
<%@ page import="org.ei.domain.Searches"%>
<%@ page import="org.ei.tags.TagBroker"%>
<%@ page import="org.ei.tags.Tag"%>
<%@ page import="org.ei.tags.TagBroker"%>
<%@ page import="org.ei.tags.Tags"%>
<%@ page import="org.ei.domain.personalization.GlobalLinks"%>
<%@ page import="org.ei.domain.personalization.SavedSearches"%>

<%@ page  errorPage="/error/errorPage.jsp"%>

<%
	FastSearchControl sc=null;
	SearchResult result=null;
	SessionID sessionIdObj = null;

	DocumentBasket basket = null;
	Database databaseObj = null;
	//DocID docId=null;
	String documentId=null;
	String docid=null;
	ControllerClient client = null;
	String sessionId  = null;
	String database = null;
	String databaseId = null;
	String searchtype = null;
	String tagSearchFlag = null;
	//This variable for document type
	String searchId=null;	
	String tagname = null;
	String scope = null;
	int count = 0;
	int startRange = 0;
	int endRange = 0;
	int rescount = 0;
	int scopeint = 0;
	String groupID = null;
	TagBroker tagBroker=null;
	String defaultdbmask = null;	
	String backurl = null;	
	String customizedLogo="";
	String refEmail = "";	
        String dataFormat=null;
	boolean  isPersonalizationPresent=true;
	boolean personalization = false;	
	List docIDList = null;
	PageEntry entry =null;
        EIDoc curDoc =null;
        String format=null;
        String currentRecord=null;
        ArrayList publictags = null;
        ArrayList privatetags = null;
        ArrayList grouptags = null;
	ArrayList groupidslist = null;
	ArrayList grouptitleslist = null;
%>
<%!
    int customizedEndYear = (Calendar.getInstance()).get(Calendar.YEAR);
    int pagesize = 0;
    int dedupSetSize = 0;
    DatabaseConfig databaseConfig = null;

    public void jspInit()
    {
        try
        {
            RuntimeProperties runtimeProps = ConfigService.getRuntimeProperties();
            pagesize = Integer.parseInt(runtimeProps.getProperty("PAGESIZE"));
            dedupSetSize = Integer.parseInt(runtimeProps.getProperty("DEDUPSETSIZE"));

            databaseConfig = DatabaseConfig.getInstance();

            // jam Y2K3
            customizedEndYear = Integer.parseInt(runtimeProps.getProperty("SYSTEM_ENDYEAR"));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
%>
<%

	client = new ControllerClient(request, response);
	UserSession ussession=(UserSession)client.getUserSession();
	sessionIdObj = ussession.getSessionID();
	sessionId = ussession.getID();
	User user=ussession.getUser();
	String[] credentials = user.getCartridge();
	
	String customerId=user.getCustomerID().trim();
	String pUserId = ussession.getProperty("P_USER_ID");
	
	ClientCustomizer clientCustomizer=new ClientCustomizer(ussession);
	
	if(clientCustomizer.getRefEmail() != null &&
        clientCustomizer.getRefEmail().length()>0)
    	{
        	refEmail = clientCustomizer.getRefEmail();
    	}
    	if(clientCustomizer.isCustomized())
    	{
        	isPersonalizationPresent=clientCustomizer.checkPersonalization();
        	customizedLogo=clientCustomizer.getLogo();
    	}
	if((pUserId != null) && (pUserId.trim().length() != 0))
	{
		personalization=true;
	}	

	if(request.getParameter("tagSearchFlag") != null)
	{
    		tagSearchFlag = request.getParameter("tagSearchFlag");
	}
	if(request.getParameter("database") != null)
	{
    		database = request.getParameter("database");
	}
	if(request.getParameter("defaultdbmask") != null)
	{
    		database = request.getParameter("defaultdbmask");
	}	

        if(request.getParameter("backurl") != null)
	{
    		backurl = request.getParameter("backurl");
	} 
        if(request.getParameter("defaultdbmask")!=null)
        {
          defaultdbmask=request.getParameter("defaultdbmask").trim();
        }	
	if(request.getParameter("searchtype") != null)
	{
		searchtype = request.getParameter("searchtype");
	}	
	else if (request.getParameter("SEARCHTYPE") != null)
	{
		searchtype = request.getParameter("SEARCHTYPE");
	}
	if(request.getParameter("searchid") != null)
	{
		searchId = request.getParameter("searchid");
	}
	else if(request.getParameter("SEARCHID") != null)
	{
		searchId = request.getParameter("SEARCHID");
	}
	if(request.getParameter("format")!=null)
        {
          format=request.getParameter("format").trim();
          //cid=format;
        }    
        if(request.getParameter("docID")!=null)
	{
	  docid=request.getParameter("docID").trim();
	          
        }    
	if(request.getParameter("groupID") != null)
	{
	  groupID = request.getParameter("groupID");
	}    

	if(request.getParameter("SCOPE") != null)
	{
	  scopeint = Integer.parseInt(request.getParameter("SCOPE"));
	}    
        
	currentRecord=request.getParameter("DOCINDEX");
	       
        if(currentRecord == null )
        {
          currentRecord = "1";
        }		       
	int index=Integer.parseInt(currentRecord);
	
	if(format!=null)
	{
	  if(format.endsWith("AbstractFormat"))
	  {
	    dataFormat = Abstract.ABSTRACT_FORMAT;
	  }
	  else if(format.endsWith("TocFormat"))
	  {
	    dataFormat = Abstract.ABSTRACT_FORMAT;
	  }
	  else if(format.endsWith("DetailedFormat"))
	  {
	    dataFormat = FullDoc.FULLDOC_FORMAT;
	  }
	}
	else
	{
	  format = Abstract.ABSTRACT_FORMAT;
	  dataFormat = Abstract.ABSTRACT_FORMAT;
        }
         
	//entry = result.entryAt(index, dataFormat);
	

	String redirectURL = null;
	String CID = "";

	if(searchtype.equalsIgnoreCase("Thesaurus"))
	{
		CID = "thesSearchCitationFormat";
	}
	else if(searchtype.equalsIgnoreCase("Quick"))
	{
		CID = "quickSearchCitationFormat";
	}
	else if(searchtype.equalsIgnoreCase("Combined"))
	{
		CID = "combineSearchHistory";
	}
	else if(searchtype.equalsIgnoreCase("Expert"))
	{
		CID = "expertSearchCitationFormat";
	}
	else if(searchtype.equalsIgnoreCase("Easy"))
	{
		CID = "expertSearchCitationFormat";
	}

	redirectURL="/controller/servlet/Controller?CID="+CID+"&SEARCHID="+searchId+"&COUNT=1&database="+database;
	
        /**
        *   Log Functionality
        */
        
/*
        client.log("SEARCH_ID", searchID);
        client.log("QUERY_STRING", tQuery.getDisplayQuery());
        client.log("DB_NAME", Integer.toString(did.getDatabase().getMask()));
        client.log("PAGE_SIZE", Integer.toString(pagesize));
        client.log("HIT_COUNT", totalDocCount);
        client.log("NUM_RECS", "1");
        client.log("context", "search");
        client.log("DOC_INDEX", (new Integer(index)).toString());
        client.log("DOC_ID", did.getDocID());
        client.log("FORMAT", dataFormat);
        client.log("ACTION", "document");
        client.setRemoteControl();
*/
        String strGlobalLinksXML = GlobalLinks.toXML(user.getCartridge());
 	
	out.write("<PAGE>");
	tagBroker = new TagBroker();

	Tags tags = tagBroker.getTags(docid, pUserId, false);	
	publictags = new ArrayList();
	publictags = tags.getPublictagsList();
	StringBuffer publictagnames = new StringBuffer();
	for(int i = 0; i < publictags.size(); i++)
	{

	     Tag tag = (Tag) publictags.get(i);

	     publictagnames.append(tag.getTag());

	     if( i < publictags.size()-1) {
	     	publictagnames.append(",");
	     }
	}
	
	if (publictagnames != null) {
		out.write("<PUBLIC-TAGS>"+publictagnames.toString()+"</PUBLIC-TAGS>");
	}
	privatetags = new ArrayList();
	privatetags = tags.getPrivatetagsList();
        StringBuffer privatetagnames = new StringBuffer();
	for(int i = 0; i < privatetags.size(); i++)
	{

	     Tag tag = (Tag) privatetags.get(i);

	     privatetagnames.append(tag.getTag());

	     if( i < privatetags.size()-1) {
	     	privatetagnames.append(",");
	     }
	}
	
	if (privatetagnames != null) {
		out.write("<PRIVATE-TAGS>"+privatetagnames.toString()+"</PRIVATE-TAGS>");
	}
	
	grouptags = new ArrayList();
	grouptags = tags.getGrouptagsList();
	groupidslist = tags.getGroupIdsList();
	grouptitleslist = tags.getGroupTitlesList();

	out.write("<GROUP-TAGS>");

	for(int j = 0; j < groupidslist.size(); j++)
	{	
		out.write("<GROUP-TAG");
		out.write(" id=\"");
		out.write(groupidslist.get(j).toString());
		out.write("\"");
		out.write(" title=\"");
		out.write(grouptitleslist.get(j).toString());
		out.write("\">");

        	StringBuffer grouptagnames = new StringBuffer();
		for(int i = 0; i < grouptags.size(); i++)
		{
		     Tag tag = (Tag) grouptags.get(i);
		     if(groupidslist.get(j).equals(tag.getGroupID()))
		     {
			     grouptagnames.append(tag.getTag());
			     if( i < grouptags.size()-1) {
				grouptagnames.append(",");
			     }
		    }
		    
		}
		String tagsgroup=null;
		 if(grouptagnames.lastIndexOf(",") == grouptagnames.length() - 1 ) {
			 tagsgroup = (grouptagnames.delete(grouptagnames.lastIndexOf(","),grouptagnames.length())).toString();
		 }
		 else {
			 tagsgroup= grouptagnames.toString();
		 }

		out.write(tagsgroup);		
		out.write("</GROUP-TAG>");
	}
	
	out.write("</GROUP-TAGS>");
	out.write("<SESSION-ID>"+sessionIdObj.toString()+"</SESSION-ID>");
	out.write("<PERSONALIZATION-PRESENT>"+isPersonalizationPresent+"</PERSONALIZATION-PRESENT>");
	out.write("<PERSONALIZATION>"+personalization+"</PERSONALIZATION>");
        out.write("<SELECTED-DB>"+database+"</SELECTED-DB>");
        out.write("<DOC-ID>"+docid+"</DOC-ID>");
        out.write("<DOCINDEX>"+request.getParameter("DOCINDEX")+"</DOCINDEX>");
        out.write("<PAGEINDEX>"+request.getParameter("PAGEINDEX")+"</PAGEINDEX>");
	out.write("<HEADER/>");
	out.write(strGlobalLinksXML);
	out.write("<FOOTER/>");
	out.write("<SESSION-TABLE/>");
	out.write("<SEARCH/>");
	out.write("<ABSTRACT-DETAILED-NAVIGATION-BAR/>");
	out.write("<DBMASK>"+database+"</DBMASK>");
	out.write("<SEARCH-ID>"+searchId+"</SEARCH-ID>");
	out.write("<SEARCH-TYPE>"+searchtype+"</SEARCH-TYPE>");
	out.write("<FORMAT>"+format+"</FORMAT>");
	out.write("<SEARCH-TYPE-TAG>"+searchtype+"</SEARCH-TYPE-TAG>");
        out.write("<TAG-SEARCH-FLAG>" + tagSearchFlag  + "</TAG-SEARCH-FLAG>");	
        out.write("<DEFAULT-DB-MASK>" + defaultdbmask  + "</DEFAULT-DB-MASK>");
        out.write("<SCOPE-REC>" + scopeint  + "</SCOPE-REC>");
        out.write("<GROUP-ID>" + groupID  + "</GROUP-ID>");

	out.write("</PAGE>");
	out.write("<!--END-->");
	out.flush();
	

%>