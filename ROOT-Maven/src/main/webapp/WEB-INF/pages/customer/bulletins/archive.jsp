<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld" prefix="stripes" %>

<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp" pageTitle="Engineering Village - Encompass Bulletins">

	<stripes:layout-component name="csshead">
	<link href="/static/css/ev_bulletins.css?v=${releaseversion}" media="all" type="text/css" rel="stylesheet"></link>
	</stripes:layout-component>

	<stripes:layout-component name="contents">

	<div id="bulletins" class="paddingL15">
		<h1>Bulletins</h1>

		<div class="hr" style="margin:0 10px 0 0; color:#d7d7d7; background-color:#d7d7d7; height: 2px;"><hr></div>

		<div id="bulletins_wrapper">
			<p style="font-size: 12px; margin: 3px 0"><a href="/bulletins/display.url?database=${actionBean.database}" title="Back to Bulletins home">Back to Bulletins home</a></p>
			<%-- ARCHIVE SEARCH BOX --%>
			<jsp:include page="parts/archivesearchbox.jsp"></jsp:include>


			<div id="display_box">
				<h2 class="title"><span>${actionBean.displaycategory}</span></h2>

			<div style="margin-left:12px">
<c:if test="${not empty actionBean.litpage && not empty actionBean.litpage.displayableBulletins}">
				<h3 class="subtitle">${actionBean.path}&nbsp;&nbsp;&gt;&nbsp;&nbsp;${actionBean.hitcount} bulletins found.</h3>

				<div class="hr" style="margin-right: 10px"><hr/></div>

				<table border="0" cellspacing="0" cellpadding="0" class="bulletintable">
					<tbody>
						<tr>
							<th class="category" valign="top" align="left"><b>Bulletin</b></th>
							<th valign="top" width="120"><b>Published Date</b></th>
<c:if test="${actionBean.litpage.showpdf}">
							<th valign="top" width="90"><b>PDF</b></th>
</c:if>
						</tr>
<c:forEach items="${actionBean.litpage.displayableBulletins}" var="bulletin">
						<tr>
							<td valign="top">
<c:choose>
<c:when test="${actionBean.litpage.showhtml}">
							<a
								href="/bulletins/view.url?db=1&fn=${bulletin.fileName}&ct=HTML&id=${bulletin.id}&yr=${bulletin.year}&cy=${bulletin.category}&db=1"
								target="_blank" title="View bulletin in HTML">Issue ${bulletin.week}</a>
</c:when>
<c:otherwise>
							${bulletin.displayCategory}
</c:otherwise>
</c:choose>
							</td>
							<td>${bulletin.publishedDt}</a></td>

<c:if test="${actionBean.litpage.showpdf}">
							<td valign="top">
								<a href="/bulletins/view.url?db=1&fn=${bulletin.fileName}&ct=PDF&id=${bulletin.id}&yr=${bulletin.year}&cy=${bulletin.category}"
								target="_blank" title="View bulletin in PDF">View </a> &nbsp;&nbsp;
								<a href="/bulletins/view.url?db=1&fn=${bulletin.fileName}&ct=SAVEPDF&id=${bulletin.id}&yr=${bulletin.year}&cy=${bulletin.category}" title="Save bulletin">
								Save</a>
							</td>
</c:if>


						</tr>
</c:forEach>
					</tbody>
				</table>
<c:if test="${actionBean.hitcount > actionBean.pageSize}">
				<div class="hr" style="margin-right: 10px"><hr/></div>
				<div style="padding: 7px 10px 0 0; text-align: right; width: 425px">
<c:if test="${actionBean.docIndex > 1}">
						<a href="/bulletins/archive.url?docIndex=${actionBean.docIndex-actionBean.pageSize}&queryStr=${actionBean.queryStr}&database=${actionBean.database}" title="Previous">&lt;&nbsp;Previous</a>
</c:if>
<c:if test="${(actionBean.docIndex > 1) && ((actionBean.docIndex + actionBean.pageSize) < actionBean.hitcount)}">&nbsp;&nbsp;|&nbsp;&nbsp;</c:if>
<c:if test="${(actionBean.docIndex + actionBean.pageSize) < actionBean.hitcount}">
						<a href="/bulletins/archive.url?docIndex=${actionBean.docIndex+actionBean.pageSize}&queryStr=${actionBean.queryStr}&database=${actionBean.database}" title="Next">Next&nbsp;&gt;</a>
</c:if>
				</div>
</c:if>
</c:if>

<c:if test="${not empty actionBean.patpage && not empty actionBean.patpage.displayableBulletins}">
				<h3 class="subtitle">${actionBean.path}&nbsp;&nbsp;&gt;${actionBean.hitcount} bulletins found.</h3>

				<div class="hr" style="margin-right: 10px"><hr/></div>

				<table border="0" cellspacing="0" cellpadding="0" class="bulletintable">
				<tbody>
				<tr>
					<th class="category" valign="top"><b>Bulletin</b></th>
					<th valign="top" width="120"><b>Published Date</b></th>
<c:if test="${actionBean.patpage.showpdf}">
					<th valign="top" width="90"><b>PDF</b></th>
</c:if>
					<th valign="top""><b>Images</b></th>
				</tr>

<c:forEach items="${actionBean.patpage.displayableBulletins}" var="bulletin">
				<tr>
					<td>
<c:choose>
<c:when test="${actionBean.patpage.showhtml}">
							<a
								href="/bulletins/view.url?db=2&fn=${bulletin.fileName}&ct=HTML&id=${bulletin.id}&yr=${bulletin.year}&cy=${bulletin.category}"
								target="_blank" title="View bulletin in HTML">Issue ${bulletin.week}</a>
</c:when>
<c:otherwise>${bulletin.displayCategory}</c:otherwise>
</c:choose>
					</td>

					<td>${bulletin.publishedDt}</a></td>

<c:if test="${actionBean.patpage.showpdf}">
					<td>
						<a
						href="/bulletins/view.url?db=2&fn=${bulletin.fileName}&ct=PDF&id=${bulletin.id}&yr=${bulletin.year}&cy=${bulletin.category}"
						target="_blank" title="View bulletin in PDF">View </a> &nbsp;&nbsp;
						<a
						href="/bulletins/view.url?db=2&fn=${bulletin.fileName}&ct=SAVEPDF&id=${bulletin.id}&yr=${bulletin.year}&cy=${bulletin.category}" title="Save bulletin">Save</a>
					</td>
</c:if>

					<td>
					<c:if test="${not empty bulletin.zipFileName}">
						<a
						href="/bulletins/view.url?db=2&fn=${bulletin.fileName}&ct=ZIP&id=${bulletin.id}&yr=${bulletin.year}&cy=${bulletin.category}" title="Save images">Save</a>
					</c:if>
					&nbsp;
					</td>
				</tr>
</c:forEach>
				</tbody>
				</table>
<c:if test="${actionBean.hitcount > actionBean.pageSize}">
				<div class="hr" style="margin-right: 10px"><hr/></div>
				<div style="padding: 7px 10px 0 0; text-align: right; width: 462px">
<c:if test="${actionBean.docIndex > 1}">
						<a href="/bulletins/archive.url?docIndex=${actionBean.docIndex-actionBean.pageSize}&queryStr=${actionBean.queryStr}&database=${actionBean.database}" title="Previous">&lt;&nbsp;Previous</a>
</c:if>
<c:if test="${(actionBean.docIndex > 1) && ((actionBean.docIndex + actionBean.pageSize) < actionBean.hitcount)}">&nbsp;&nbsp;|&nbsp;&nbsp;</c:if>
<c:if test="${(actionBean.docIndex + actionBean.pageSize) < actionBean.hitcount}">
						<a href="/bulletins/archive.url?docIndex=${actionBean.docIndex+actionBean.pageSize}&queryStr=${actionBean.queryStr}&database=${actionBean.database}" title="Next">Next&nbsp;&gt;</a>
</c:if>
				</div>
</c:if>
</c:if>
<c:if test="${(empty actionBean.litpage.displayableBulletins) and (empty actionBean.patpage.displayableBulletins)}">
				<div style="height: 240px">No bulletins could be found!</div>
</c:if>
				</div>
		</div>
		</div>

		<div class="clear"></div>
	</stripes:layout-component>

	<stripes:layout-component name="jsbottom_custom">
	    <SCRIPT type="text/javascript" SRC="/static/js/Bulletin.js?v=${releaseversion}"></SCRIPT>
	</stripes:layout-component>

</stripes:layout-render>
