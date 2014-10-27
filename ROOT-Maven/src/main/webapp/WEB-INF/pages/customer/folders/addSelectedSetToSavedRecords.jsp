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
								href="/selected/citationfolder.url?CID=viewCitationSavedRecords&EISESSION=${actionBean.sessionid}&database=${actionBean.database}&folderid=${actionBean.folderid}&searchresults=${f:encode(actionBean.searchresults)}&newsearch=${f:encode(actionBean.newsearch)}">
						</c:when>
						<c:otherwise>
							<a
								href="/selected/citationfolder.url?CID=viewCitationSavedRecords&EISESSION=${actionBean.sessionid}&database=${actionBean.database}&folderid=${actionBean.folderid}&DOCINDEX=${actionBean.docindex}&format=${actionBean.format}">
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
					<a href="/back/service.url?feature=SAVETOFOLDER">Return to previous page</a>
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
