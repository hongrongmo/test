<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld" prefix="stripes" %>
<%@ taglib uri="/WEB-INF/tlds/functions.tld" prefix="f" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

	
<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp" pageTitle="Engineering Village - My Saved Records Folders">

	<stripes:layout-component name="csshead"> 
	<link href="/static/css/ev_folder.css?v=${releaseversion}" media="all" type="text/css" rel="stylesheet"></link>
	</stripes:layout-component>

	<stripes:layout-component name="contents">
	
    <c:set var="backeventurl" scope="request">
        <c:choose>
            <c:when test="${fn:contains(actionBean.backurl,'citationSelectedSet')}">/selected/citation.url?</c:when>
            <c:when test="${fn:contains(actionBean.backurl,'quickSearchAbstractFormat')}">/search/doc/abstract.url?pageType=quickSearch&searchtype=Quick&</c:when>
            <c:when test="${fn:contains(actionBean.backurl,'quickSearchDetailedFormat')}">/search/doc/detailed.url?pageType=quickSearch&searchtype=Quick&</c:when>
            <c:when test="${fn:contains(actionBean.backurl,'expertSearchAbstractFormat')}">/search/doc/abstract.url?pageType=expertSearch&searchtype=Expert&</c:when>
            <c:when test="${fn:contains(actionBean.backurl,'expertSearchDetailedFormat')}">/search/doc/detailed.url?pageType=expertSearch&searchtype=Expert&</c:when>
            <c:when test="${fn:contains(actionBean.backurl,'pageDetailedFormat')}">/search/book/detailed.url?pageType=page&</c:when>
            <c:when test="${fn:contains(actionBean.backurl,'tagSearchAbstractFormat')}">/search/doc/abstract.url?pageType=tagSearch&searchtype=TagSearch&</c:when>
            <c:when test="${fn:contains(actionBean.backurl,'tagSearchDetailedFormat')}">/search/doc/detailed.url?pageType=tagSearch&searchtype=TagSearch&</c:when>
            <c:otherwise>/search/results/quick.url?</c:otherwise>
        </c:choose>
    </c:set>
    
	<div id="folders" class="paddingL15">

		<!-- Area for confirmation message -->
		<table border="0" width="90%" cellspacing="0" cellpadding="0"
			id="addfolder">
			<tr>
				<td valign="top" height="5"><img src="/static/images/s.gif"	border="0" height="5" /></td>
			</tr>
			<tr>
				<td valign="top"><b>Record(s) Saved</b></td>
			</tr>
			<tr>
				<td>Your selected record(s) have been saved to <c:choose>
						<c:when
							test="${not empty actionBean.searchresults and not empty actionBean.newsearch}">
							<a
								href="/selected/citationfolder.url?CID=viewCitationSavedRecords&EISESSION=${actionBean.sessionid}&database=${actionBean.database}&folderid=${actionBean.folderid}&backurl=${f:encode(actionBean.backurl)}&searchresults=${f:encode(actionBean.searchresults)}&newsearch=${f:encode(actionBean.newsearch)}">
						</c:when>
						<c:otherwise>
							<a
								href="/selected/citationfolder.url?CID=viewCitationSavedRecords&EISESSION=${actionBean.sessionid}&database=${actionBean.database}&folderid=${actionBean.folderid}&backurl=${f:encode(actionBean.backurl)}&DOCINDEX=${actionBean.docindex}&format=${actionBean.format}">
						</c:otherwise>
					</c:choose> <b>${actionBean.foldername}</b> </a>folder.
				</td>
			</tr>
			<tr>
				<td valign="top" height="8"><img src="/static/images/s.gif"
					border="0" height="8" /></td>
			</tr>
			<tr>
				<td valign="top">
				<c:choose>
				<c:when test="${not empty actionBean.searchresults and not empty actionBean.newsearch}">
							<a
								href="${backeventurl}${actionBean.backurl}&searchresults=${f:encode(actionBean.searchresults)}&newsearch=${f:encode(actionBean.newsearch)}">Return
								to previous page</a>
				</c:when>
				<c:otherwise>
							<a
								href="${backeventurl}${actionBean.backurl}&DOCINDEX=${actionBean.docindex}&format=${actionBean.format}">Return
								to previous page</a>
					</c:otherwise>
				</c:choose>
				</td>
			</tr>
			<tr>
				<td valign="top" height="8"><img src="/static/images/s.gif"
					border="0" height="8" /></td>
			</tr>
		</table>
			
	</stripes:layout-component>
	
	<stripes:layout-component name="jsbottom_custom">
	    <SCRIPT type="text/javascript" SRC="/static/js/Folders.js?v=${releaseversion}"></SCRIPT>
	</stripes:layout-component>
	
</stripes:layout-render>
