<%@ page language="java" %>
<%@ page session="false" %>
<%@ page errorPage="/error/errorPage.jsp" %>
<%@ page buffer="20kb"%>

<% 
	/*
		Local holdings link format:
		/controller/servlet/Controller?CID=lochform&title=[ATITLE]&author=[AUFULL]&issn=[ISSN]&isbn=[ISBN1]&volume=[VOLUME]&issue=[ISSUE]&serialtitle=[TITLE]&source=[STITLE]&startpage=[SPAGE]&year=[YEAR]&email=j.bernstein@elsevier.com
		conftitle=[CTITLE]
	*/
	
		String title = request.getParameter("title");
		String author = request.getParameter("author");
		String stitle = request.getParameter("serialtitle");
		String conftitle = request.getParameter("conftitle");
		String source = request.getParameter("source");
		String issn = request.getParameter("issn");
		String vol = request.getParameter("volume");
		String issue = request.getParameter("issue");
		String StartPage = request.getParameter("startpage");
		String year = request.getParameter("year");
		String isbn = request.getParameter("isbn");
		String email = request.getParameter("email");
		
		out.write("<LOCH>");

		if ((title != null)&& (!title.trim().equals("")))
		{	
			out.write("<ARTICLETITLE><![CDATA["+title+"]]></ARTICLETITLE>");	
		}
		
		if ((author != null)&& (!author.trim().equals("")))
		{	
			out.write("<AUTHOR><![CDATA["+author+"]]></AUTHOR>");	
		}
		
		if ((stitle != null)&& (!stitle.trim().equals("")))
		{
			out.write("<SERIALTITLE><![CDATA["+stitle+"]]></SERIALTITLE>");		
		}
		else
		{
			if ((source != null)&& (!source.trim().equals("")))
			{	
				out.write("<SOURCE><![CDATA["+source+"]]></SOURCE>");	
			}
		}
			
		if ((conftitle != null)&& (!conftitle.trim().equals("")))
		{
			out.write("<CONFTITLE><![CDATA["+conftitle+"]]></CONFTITLE>");		
		}	
		
		if ((issn != null ) && (!issn.trim().equals("")))
		{	
			out.write("<ISSN><![CDATA["+issn+"]]></ISSN>");
		}
		
		if ((isbn != null ) && (!isbn.trim().equals("")))
		{	
			out.write("<ISBN><![CDATA["+isbn+"]]></ISBN>");	
		}
		
		if ((vol != null ) && (!vol.trim().equals("")))
		{
			out.write("<VOLUME><![CDATA["+vol+"]]></VOLUME>");
		}
		
		if ((issue != null) && (!issue.trim().equals("")))
		{
			out.write("<ISSUE><![CDATA["+issue+"]]></ISSUE>");
		}
		
		if ((StartPage != null ) && (!StartPage.trim().equals("")))
		{
			out.write("<STARTPAGE><![CDATA["+StartPage+"]]></STARTPAGE>");
		}
		
		if ((year != null) && (!year.trim().equals("")))
		{
			out.write("<YEAR><![CDATA["+year+"]]></YEAR>");		
		}
		if ((email != null) && (!email.trim().equals("")))
		{
			out.write("<EMAIL><![CDATA["+email+"]]></EMAIL>");		
		}
		
		out.write("</LOCH>");
		
		out.println("<!--END-->");
		out.flush();

%>