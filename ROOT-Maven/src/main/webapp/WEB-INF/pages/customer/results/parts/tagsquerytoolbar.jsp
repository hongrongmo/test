<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/tlds/functions.tld" prefix="f" %>

	<div id="querydisplaywrap">
		<div id="querydisplay" style="padding: 7px">
			<p><b>Search Tags</b></p>
			<p style="margin-top:5px">${actionBean.resultscount} total record(s) for:</p>
			<p style="margin-top:5px">
			<form id="searchtagsform" name="searchtagsform" action="/search/results/tags.url" method="GET">
				<input type="hidden" name="CID" value="tagSearch"/>
				<input type="hidden" name="searchtype" value="TagSearch"/>

				<select name="scope" style="vertical-align: middle;margin:0;padding:0;width:105px;" id="searchscopeselect">
<c:forEach items="${actionBean.scopes}" var="scope">${scope}</c:forEach>
				</select> 
				<input style="vertical-align: middle;margin:0;padding:0;" size="20" name="SEARCHID" id="SEARCHID" value="${actionBean.searchid}">
				<input style="vertical-align: middle;margin:0" alt="Search Tags" type="submit" value="Search" class="button">
			</form>
			</p>
		</div>
		<div id="autocompletewrap" style="width:142px; position:absolute"></div>

		<div class="clear"></div>
	</div>