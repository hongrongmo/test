<%@ page language="java" %>
<%@ page  session="false"%>
<%@ page  import="org.ei.controller.ControllerClient"%>
<%@ page  errorPage="/error/errorPage.jsp"%>

<%@ page  import="org.ei.domain.PublisherBroker"%>
<%@ page  import="org.ei.domain.ChinaLinkBuilder"%>

<%

	String doi = request.getParameter("DOI");

	if((doi != null && (doi.indexOf("10.1016/") == 0 || doi.indexOf("10.1006/") == 0)) &&
	   (request.getParameter("CREDS") != null && request.getParameter("CREDS").indexOf("Elsevier") != -1))
	{
		ControllerClient client = new ControllerClient(request, response);
	        client.setRedirectURL("http://dx.doi.org/"+doi);
  	        client.setRemoteControl();
  	        return;
	}

	PublisherBroker pubBroker = PublisherBroker.getInstance();
	ChinaLinkBuilder chinaLinkBuilder = new ChinaLinkBuilder();

	chinaLinkBuilder.setAulast(request.getParameter("AULAST"));
	chinaLinkBuilder.setAufirst(request.getParameter("AUFIRST"));
	chinaLinkBuilder.setAufull(request.getParameter("AUFULL"));
	chinaLinkBuilder.setIssn(request.getParameter("ISSN"));
	chinaLinkBuilder.setIssn9(request.getParameter("ISSN9"));
	chinaLinkBuilder.setIssue(request.getParameter("ISSUE"));
	chinaLinkBuilder.setCoden(request.getParameter("CODEN"));
	chinaLinkBuilder.setTitle(request.getParameter("TITLE"));
	chinaLinkBuilder.setStitle(request.getParameter("STITLE"));
	chinaLinkBuilder.setAtitle(request.getParameter("ATITLE"));
	chinaLinkBuilder.setVolume(request.getParameter("VOLUME"));
	chinaLinkBuilder.setSpage(request.getParameter("SPAGE"));
	chinaLinkBuilder.setEpage(request.getParameter("EPAGE"));
	chinaLinkBuilder.setCreds(request.getParameter("CREDS"));

	String url	= null;
	String pubID	= null;
	String issn 	= chinaLinkBuilder.getIssn();

  	String errorUrl = "/controller/servlet/Controller?CID=chinaLinkingErrorPage&ISSN="+chinaLinkBuilder.getIssn()+
  						"&AULAST="+chinaLinkBuilder.getAulast()+
						  "&AUFIRST="+chinaLinkBuilder.getAufirst()+
						  "&AUFULL="+chinaLinkBuilder.getAufull()+
						  "&ISSN9="+chinaLinkBuilder.getIssn9()+
						  "&CODEN="+chinaLinkBuilder.getCoden()+
						  "&TITLE="+chinaLinkBuilder.getTitle()+
						  "&STITLE="+chinaLinkBuilder.getStitle()+
						  "&ATITLE="+chinaLinkBuilder.getAtitle()+
						  "&VOLUME="+chinaLinkBuilder.getVolume()+
						  "&ISSUE="+chinaLinkBuilder.getIssue()+
						  "&SPAGE="+chinaLinkBuilder.getSpage()+
						  "&EPAGE="+chinaLinkBuilder.getEpage()+
              					  "&CREDS="+request.getParameter("CREDS");

	chinaLinkBuilder.buildUrls();

	if(issn != null && issn.length() > 0)
	{
 		pubID = pubBroker.fetchPubID(issn);

  		if(pubID != null && pubID.length() > 0)
  		{


  			url = chinaLinkBuilder.getPubURL(pubID);

      			if(url == null)
      			{
        			url = errorUrl.concat("&ERROR=credentials");
      			}
  		}
  		else
  		{
      			url = errorUrl;
  		}
 	}
 	else
 	{
    		url = errorUrl.concat("&ERROR=issn");
	}


	ControllerClient client = new ControllerClient(request, response);
	client.setRedirectURL(url);
  	client.setRemoteControl();

%>


