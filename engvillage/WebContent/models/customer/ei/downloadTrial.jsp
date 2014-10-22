<%@ page language="java" %>
<%@ page session="false" %>
<%@ page import="java.io.*"%>
<%@ page import="java.util.*"%>
<%@ page import="org.ei.trial.*"%>
<%@ page import ="org.engvillage.biz.controller.ControllerClient"%>
<%@ page errorPage="/error/errorPage.jsp"%>
<%
	String startMonth=request.getParameter("startMonth");
	String startDay=request.getParameter("startDay");
	String startYear=request.getParameter("startYear");
	String endMonth=request.getParameter("endMonth");
	String endDay=request.getParameter("endDay");
	String endYear=request.getParameter("endYear");
	java.sql.Date startDate=null;
	java.sql.Date endDate=null;
	// Variable used to hold the reference to ControllerClient object
		ControllerClient client = null;
		client = new ControllerClient(request,response);
	StringBuffer strbFilename = new StringBuffer();
	strbFilename.append("TRIAL");
	strbFilename.append("_.txt");
	client.setContentDispositionFilenameTimestamp(strbFilename.toString());
	client.setRemoteControl();

	if(startMonth!=null&&startDay!=null&&startYear!=null)
	{
		startDate=new java.sql.Date(Integer.parseInt(startYear)-1900,Integer.parseInt(startMonth),Integer.parseInt(startDay));
	}
	if(endMonth!=null&&endDay!=null&&endYear!=null)
	{
		endDate=new java.sql.Date(Integer.parseInt(endYear)-1900,Integer.parseInt(endMonth),Integer.parseInt(endDay));
	}
	TrialUserBroker tBroker=new TrialUserBroker();
	Collection allTrialCollection = tBroker.getTrialAccount(startDate,endDate);
	Iterator allTrial=allTrialCollection.iterator();
	out.write("<PAGE>");

	while(allTrial.hasNext())
	{
		TrialUser aUser=(TrialUser)allTrial.next();
		out.write("<TRIAL-USER>");
		out.write("<INDEX-ID><![CDATA["+aUser.getIndexID()+"]]></INDEX-ID>");
		if(aUser.getFirstName()!=null)
		{
			out.write("<FIRST-NAME><![CDATA["+checkCharacter(aUser.getFirstName())+"]]></FIRST-NAME>");
		}
		if(aUser.getLastName()!=null)
		{
			out.write("<LAST-NAME><![CDATA["+checkCharacter(aUser.getLastName())+"]]></LAST-NAME>");
		}
		if(aUser.getJobTitle()!=null)
		{
			out.write("<JOB-TITLE><![CDATA["+checkCharacter(aUser.getJobTitle())+"]]></JOB-TITLE>");
		}
		if(aUser.getWebSite()!=null)
		{
			out.write("<WEB-SITE><![CDATA["+checkCharacter(aUser.getWebSite())+"]]></WEB-SITE>");
		}
		if(aUser.getCompany()!=null)
		{
			out.write("<COMPANY><![CDATA["+checkCharacter(aUser.getCompany())+"]]></COMPANY>");
		}
		if(aUser.getAddress1()!=null)
		{
			out.write("<ADDRESS1><![CDATA["+checkCharacter(aUser.getAddress1())+"]]></ADDRESS1>");
		}
		if(aUser.getAddress2()!=null)
		{
			out.write("<ADDRESS2><![CDATA["+checkCharacter(aUser.getAddress2())+"]]></ADDRESS2>");
		}
		if(aUser.getCity()!=null)
		{
			out.write("<CITY><![CDATA["+checkCharacter(aUser.getCity())+"]]></CITY>");
		}
		if(aUser.getState()!=null)
		{
			out.write("<STATE><![CDATA["+checkCharacter(aUser.getState())+"]]></STATE>");
		}
		if(aUser.getCountry()!=null)
		{
			out.write("<COUNTRY><![CDATA["+checkCharacter(aUser.getCountry())+"]]></COUNTRY>");
		}
		if(aUser.getPhone()!=null)
		{
			out.write("<PHONE-NUMBER><![CDATA["+checkCharacter(aUser.getPhone())+"]]></PHONE-NUMBER>");
		}
		if(aUser.getEmail()!=null)
		{
			out.write("<EMAIL-ADDRESS><![CDATA["+checkCharacter(aUser.getEmail())+"]]></EMAIL-ADDRESS>");
		}
		if(aUser.getHowHear()!=null)
		{
			out.write("<HOW-HEAR><![CDATA["+checkCharacter(aUser.getHowHear())+"]]></HOW-HEAR>");
		}
		if(aUser.getHowHearExplained()!=null)
		{
			out.write("<HOW-HEAR-EXPLAIN><![CDATA["+checkCharacter(aUser.getHowHearExplained())+"]]></HOW-HEAR-EXPLAIN>");
		}
		if(aUser.getProduct()!=null)
		{
			out.write("<PRODUCT><![CDATA["+aUser.getProduct()+"]]></PRODUCT>");
		}
		if(aUser.getByMail()!=null)
		{
			out.write("<BY-MAIL><![CDATA["+aUser.getByMail()+"]]></BY-MAIL>");
		}
		if(aUser.getByEmail()!=null)
		{
			out.write("<BY-EMAIL><![CDATA["+aUser.getByEmail()+"]]></BY-EMAIL>");
		}
		if(aUser.getReferringURL()!=null)
		{
			out.write("<REFERRING-URL><![CDATA["+checkCharacter(aUser.getReferringURL())+"]]></REFERRING-URL>");
		}
		if(aUser.getTrialDate()!=null)
		{
			out.write("<TIME-STAMP><![CDATA["+aUser.getTrialDate()+"]]></TIME-STAMP>");
		}
		out.write("</TRIAL-USER>");

	}
	
	
	out.write("<START-YEAR>"+startYear+"</START-YEAR>");
	out.write("<START-MONTH>"+startMonth+"</START-MONTH>");
	out.write("<START-DAY>"+startDay+"</START-DAY>");
	out.write("<END-YEAR>"+endYear+"</END-YEAR>");
	out.write("<END-MONTH>"+endMonth+"</END-MONTH>");
	out.write("<END-DAY>"+endDay+"</END-DAY>");
	out.write("</PAGE>");
%>
<%!
	String checkCharacter(String inString) throws Exception
	{
		StringBuffer st = null;
		
		if((inString != null)&&(inString.length()>0))
		{
			st=new StringBuffer(inString);
			for(int i=0;i<st.length();i++)
			{
				char c = st.charAt(i);
				//System.out.println("char "+c);
				if((c<32)||(c>127))
				{
					st.replace(i,i+1," ");
					//System.out.println("char "+c);
				}
			}
		}
		
		return st.toString();
	}
%>