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

			<p style="font-size: 12px; margin: 3px 0">&nbsp;</p>

			<%-- ARCHIVE SEARCH BOX --%>
			<jsp:include page="parts/archivesearchbox.jsp"></jsp:include>


			<div id="display_box">
				<h2 class="title">Most Recent Bulletins</h2>
				<div style="margin-left: 12px">
<c:if test="${actionBean.lit and not empty actionBean.litpage}">
				<h3 class="subtitle">EnCompassLIT</h3>

				<div class="hr" style="margin-right: 10px"><hr/></div>

				<table border="0" cellspacing="0" cellpadding="0" class="bulletintable">
<c:choose><c:when test="${not empty actionBean.litpage.displayableBulletins}">
					<thead>
						<tr>
							<th class="category" valign="top" align="left"><b>Category</b></th>
							<th valign="top" width="120"><b>Published Date</b></th>
<c:if test="${actionBean.litpage.showpdf}">
							<th valign="top" width="90"><b>PDF</b></th>
</c:if>
						</tr>
					</thead>
					<tbody>
<c:forEach items="${actionBean.litpage.displayableBulletins}" var="bulletin">
						<tr>
							<td valign="top">
<c:choose>
<c:when test="${actionBean.litpage.showhtml}">
							<a
								href="/bulletins/view.url?db=1&fn=${bulletin.fileName}&ct=HTML&id=${bulletin.id}&yr=${bulletin.year}&cy=${bulletin.category}"
								target="_blank" title="View bulletin in HTML">${bulletin.displayCategory}</a>
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
</c:when><c:otherwise>
					<tbody>
						<tr><td>No bulletins to display!</td></tr>
					</tbody>
</c:otherwise></c:choose>
				</table>
				<br/>
</c:if>

<c:if test="${actionBean.pat and not empty actionBean.patpage}">
				<h3 class="subtitle">EnCompassPAT</h3>

				<div class="hr" style="margin-right: 10px"><hr/></div>

				<table border="0" cellspacing="0" cellpadding="0" class="bulletintable">
<c:choose><c:when test="${not empty actionBean.patpage.displayableBulletins}">
				<thead>
				<tr>
					<th class="category" valign="top"><b>Category</b></th>
					<th valign="top" width="120"><b>Published Date</b></th>
<c:if test="${actionBean.patpage.showpdf}">
							<th valign="top" width="90"><b>PDF</b></th>
</c:if>
					<th valign="top" width="90"><b>Images</b></th>
				</tr>
				</thead>
				<tbody>
<c:forEach items="${actionBean.patpage.displayableBulletins}" var="bulletin">
				<tr>
					<td>
<c:choose>
<c:when test="${actionBean.patpage.showhtml}">
							<a
								href="/bulletins/view.url?db=2&fn=${bulletin.fileName}&ct=HTML&id=${bulletin.id}&yr=${bulletin.year}&cy=${bulletin.category}"
								target="_blank" title="View bulletin in HTML">${bulletin.displayCategory}</a>
</c:when>
<c:otherwise>${bulletin.displayCategory}</c:otherwise>
</c:choose>
					</td>

					<td>${bulletin.publishedDt}</a></td>

<c:if test="${actionBean.patpage.showpdf}">
					<td valign="top">
						<a href="/bulletins/view.url?db=2&fn=${bulletin.fileName}&ct=PDF&id=${bulletin.id}&yr=${bulletin.year}&cy=${bulletin.category}"
						target="_blank" title="View bulletin in PDF">View </a> &nbsp;&nbsp;
						<a href="/bulletins/view.url?db=2&fn=${bulletin.fileName}&ct=SAVEPDF&id=${bulletin.id}&yr=${bulletin.year}&cy=${bulletin.category}" title="Save bulletin">
						Save</a>
					</td>
</c:if>

					<td width="90">
					<c:if test="${not empty bulletin.zipFileName}">
						<a
						href="/bulletins/view.url?db=2&fn=${bulletin.fileName}&ct=ZIP&id=${bulletin.id}&yr=${bulletin.year}&cy=${bulletin.category}" title="Save image">Save</a>
					</c:if>
					&nbsp;
					</td>
				</tr>
</c:forEach>
				</tbody>
</c:when><c:otherwise>
				<tbody>
					<tr><td>No bulletins to display!</td></tr>
				</tbody>
</c:otherwise></c:choose>
				</table>
</c:if>
				</div>
		</div>
		</div>

		<div class="clear"></div>
		</div>
	</stripes:layout-component>

	<stripes:layout-component name="jsbottom_custom">
	    <SCRIPT type="text/javascript" SRC="/static/js/Bulletin.js?v=${releaseversion}"></SCRIPT>
	</stripes:layout-component>

</stripes:layout-render>
