<%@ page language="java" %>
<%@ page session="false" %>
<%@ page errorPage="/error/errorPage.jsp" %>
<%@ page buffer="20kb"%>

<%
	/*
		Local holdings link format:
		/controller/servlet/Controller?CID=loch&Title=[ATITLE]&AUTHOR=[AUFULL]&ISSN=[ISSN]&ISBN=[ISBN1]&Volume=[VOLUME]&Issue=[ISSUE]&SerialTitle=[TITLE]&conftitle=[CTITLE]&source=[STITLE]&StartPage=[SPAGE]&Year=[YEAR]
	*/
		String title = request.getParameter("Title");
		String author = request.getParameter("AUTHOR");
		String stitle = request.getParameter("SerialTitle");
		String conftitle = request.getParameter("conftitle");
		String source = request.getParameter("source");
		String issn = request.getParameter("ISSN");
		String vol = request.getParameter("Volume");
		String issue = request.getParameter("Issue");
		String StartPage = request.getParameter("StartPage");
		String year = request.getParameter("Year");
		String isbn = request.getParameter("ISBN");

		out.write("<LOCH>");

		if ((title != null)&& (!title.trim().equals(""))){
			out.write("<ARTICLETITLE><![CDATA["+title+"]]></ARTICLETITLE>");
		}
		if ((author != null)&& (!author.trim().equals(""))){
			out.write("<AUTHOR><![CDATA["+author+"]]></AUTHOR>");
		}
		if ((stitle != null)&& (!stitle.trim().equals(""))){
			out.write("<SERIALTITLE><![CDATA["+stitle+"]]></SERIALTITLE>");
		}
		if ((conftitle != null)&& (!conftitle.trim().equals(""))){
			out.write("<CONFTITLE><![CDATA["+conftitle+"]]></CONFTITLE>");
		}
		if((stitle == null)|| (stitle.trim().equals(""))) {
		 	if ((source != null)&& (!source.trim().equals(""))){
				out.write("<SOURCE><![CDATA["+source+"]]></SOURCE>");
			}
		}
		if ((issn != null ) && (!issn.trim().equals(""))){
			out.write("<ISSN><![CDATA["+issn+"]]></ISSN>");
		}
		if ((isbn != null ) && (!isbn.trim().equals(""))){
			out.write("<ISBN><![CDATA["+isbn+"]]></ISBN>");
		}
		if ((vol != null ) && (!vol.trim().equals(""))){
			out.write("<VOLUME><![CDATA["+vol+"]]></VOLUME>");
		}
		if ((issue != null) && (!issue.trim().equals(""))){
			out.write("<ISSUE><![CDATA["+issue+"]]></ISSUE>");
		}
		if ((StartPage != null ) && (!StartPage.trim().equals(""))){
			out.write("<STARTPAGE><![CDATA["+StartPage+"]]></STARTPAGE>");
		}
		if ((year != null) && (!year.trim().equals(""))){
			out.write("<YEAR><![CDATA["+year+"]]></YEAR>");
		}
		out.write("</LOCH>");

		out.println("<!--END-->");
		out.flush();

%>