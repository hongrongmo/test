<%@ page language="java" %>
<%@ page session="false" %>
<%@ page import="java.util.*"%>
<%@ page import="java.net.URLEncoder"%>
<%@ page import="org.ei.domain.*"%>
<%@ page import="org.ei.controller.ControllerClient"%>
<%@ page import="org.ei.session.*"%>
<%@ page import="org.ei.config.*"%>
<%@ page import="org.ei.query.base.*"%>
<%@ page import="org.ei.domain.Searches"%>
<%@ page import="org.ei.tags.TagBroker"%>
<%@ page import="org.ei.tags.Tag"%>
<%@ page import="org.ei.tags.CgroupsBroker"%>
<%@ page import="org.ei.tags.CollaborationGroup"%>
<%@ page import="org.ei.tags.CollaborationGroups"%>

<%@ page import="org.ei.domain.personalization.GlobalLinks"%>
<%@ page import="org.ei.domain.personalization.SavedSearches"%>

<%@ page  errorPage="/error/errorPage.jsp"%>

<%
	SessionID sessionIdObj = null;
	ControllerClient client = null;
	String database = null;
	String searchtype = null;
	String searchId=null;	
	String tagname = null;
	String rangeValfrom = null;
	String rangeValto = null;
	String customizedLogo="";
	String refEmail = "";	
	boolean  isPersonalizationPresent=true;
	boolean personalization = false;	
	String oldtag = null;
	String newtag = null;
	String groupid = null;
	CollaborationGroup cgedit = null;
%>
<%

	client = new ControllerClient(request, response);
	UserSession ussession=(UserSession)client.getUserSession();
	sessionIdObj = ussession.getSessionID();
	User user=ussession.getUser();
	String[] credentials = user.getCartridge();
	String edit = null;
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
	
	if(request.getParameter("database") != null)
	{
    	database = request.getParameter("database");
	}
	
	if(request.getParameter("edit") != null)
	{
    	edit = request.getParameter("edit");
	}
	
	if(request.getParameter("tagname") != null)
	{
    	tagname = request.getParameter("tagname");
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
	

	if(request.getParameter("oldtag") != null)
	{
		oldtag = request.getParameter("oldtag");
	}
	if(request.getParameter("newtag") != null)
	{
		newtag = request.getParameter("newtag");
	}
	
	if(request.getParameter("groupid") != null)
	{
		groupid = request.getParameter("groupid");
	}
	
	if (groupid != null)	
	{
		cgedit = CgroupsBroker.selectCgroup(groupid);
			
	}
	
	String customerId1=user.getCustomerID().trim();
	Integer custid1 = new Integer(customerId1);
	if (newtag != null && oldtag != null)	
	{
		Tag t = new Tag();
    	t.setTagName(oldtag);
    	t.setCustID(custid1.intValue());
    	t.setUserID(pUserId);
    	TagBroker.updateTag(t, newtag);
	}	

  	Query query = Searches.getSearch(searchId);
	String customerId=user.getCustomerID().trim();
	Integer custid = new Integer(customerId);
	Integer cid = new Integer(customerId);
	List taglabels = TagBroker.selectUserTagsList(pUserId,cid.intValue());
	System.out.println("tagarray"+ taglabels.size());
/*	if (taglabels != null && taglabels.size > 0)
	{
		for(int i = 0; i < taglabels.size ; i++)
		{
    		Tag t = new Tag();
    		t.getTagName();
		}		
	}
*/
	
	String redirectURL = null;
	String CID = "quickSearchCitationFormat";
	redirectURL="/controller/servlet/Controller?CID="+CID+"&SEARCHID="+searchId+"&COUNT=1&database="+database;

	if ( tagname != null)
	{
		client.setRedirectURL(redirectURL);
		client.setRemoteControl();	
	}	

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
	out.write("<SESSION-ID>"+sessionIdObj.toString()+"</SESSION-ID>");
	out.write("<CUSTOMIZED-LOGO>"+customizedLogo+"</CUSTOMIZED-LOGO>");
	out.write("<PERSONALIZATION-PRESENT>"+isPersonalizationPresent+"</PERSONALIZATION-PRESENT>");
	out.write("<PERSONALIZATION>"+personalization+"</PERSONALIZATION>");
	out.write("<REFEMAIL>"+refEmail+"</REFEMAIL>");
	out.write("<HEADER/>");
	out.write(strGlobalLinksXML);
	out.write("<FOOTER/>");
	out.write("<DBMASK>"+database+"</DBMASK>");
	out.write("<RANGE-FROM-VAL>"+rangeValfrom+"</RANGE-FROM-VAL>");
	out.write("<RANGE-TO-VAL>"+rangeValto+"</RANGE-TO-VAL>");
	out.write("<SEARCH-ID>"+searchId+"</SEARCH-ID>");
	out.write("<SEARCH-TYPE>"+searchtype+"</SEARCH-TYPE>");
	out.write("<ADDTAGSELECTRANGE-NAVIGATION-BAR/>");


	if(cgedit != null)
	{
		cgedit.toXML(out);
	}	
	else
	{
		out.write("<CGROUP>");
		out.write("<COLORS>");
		for (int i = 0; i < CollaborationGroup.COLORS.size(); i++)
		{
			out.write("<COLOR><![CDATA[");
			out.write((String)CollaborationGroup.COLORS.get(i));
			out.write("]]></COLOR>");	
		}
		out.write("</COLORS>");
		out.write("</CGROUP>");	
	}
	
	if (edit != null)
	{
		out.write("<EDIT>"+edit+"</EDIT>");
	}
	
	out.write("<TAGS>");
	for(int i = 0; i < taglabels.size() ; i++)
	{
    	Tag t = (Tag) taglabels.get(i);
    	out.write("<TAG><![CDATA[");
    	out.write(t.getTagSearchValue());
    	out.write("]]></TAG>");
	}		
	out.write("</TAGS>");
	
	
	out.write("</PAGE>");
	out.write("<!--END-->");
	out.flush();
	

%>
