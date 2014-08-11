<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/tlds/functions.tld" prefix="f" %>
<style type="text/css">
.RedText  {color:#FF3300;}
</style>
<h3 class="searchcomponentlabel" style="float: none; margin-bottom: 3px; text-transform: uppercase">Browse</h3>
<div style="display:none" id="csrfTokenElement">${actionBean.csrfSyncToken}</div>
<%-- TERM PATH DISPLAY --%>
<c:set var="laststep" scope="request">${fn:length(actionBean.steps)}</c:set>
<c:set var="scon" value="${actionBean.steps[0].context}"/>
<div id="termpath">
<c:choose>
<c:when test="${empty actionBean.browseresults}">
    <span>0 matching terms found for: <span step=0 scon="${scon}" class="Bold">${actionBean.term}</span></span>
</c:when>
<c:otherwise>
<c:forEach var="step" items="${actionBean.steps}" varStatus="status">
	<c:choose>
	<c:when test="${status.last}">
<span step="${status.count-1}"<c:if test="${status.count == 1}"> scon=${scon}</c:if> style="font-weight: bold">${step.title}</span>
	</c:when> 
	<c:otherwise>
<a href="#${step.link}" title="${step.title}" class="termsearchlink rolllink"><span<c:if test="${status.count == 1}"> scon=${step.context}</c:if> step="${status.count-1}">${step.title}</span></a> >>  
	</c:otherwise>
	</c:choose>
</c:forEach>
</c:otherwise>
</c:choose>
</div>

<div class="clear"></div>

<div id="termresults" class="termelement">

<c:if test="${empty actionBean.browseresults}">
<span class="RedText">Your search did not find any match for "${actionBean.term}".</span> 
</c:if>

<c:if test="${not empty actionBean.browseresults}">
<%-- SHOW UP TO FIRST 5 RESULTS IN ONE COLUMN --%>
<table cellpadding="0" cellspacing="0" border="0" class="termsearchtable">
<thead>
	<tr><th align="center">&nbsp;</th><th align="left">Term</th></tr>
	</thead> 

	<tbody>
<c:forEach items="${actionBean.browseresults}" var="browserec" varStatus="status" end="4">
	<tr<c:if test="${fn:toUpperCase(browserec.recID.mainTerm) eq fn:toUpperCase(actionBean.term)}"> style="background-color: #BBDAD2"</c:if>>
	<%-- Hanan, fix missing checkbox for star terms, Jan 17,2013 --%>
		<td align="center""><%--<c:if test="${browserec.status eq 'C'}">--%><c:if test="${browserec.status eq 'C' or (actionBean.database eq '1' and browserec.status eq 'L' and browserec.historyScopeNotes eq 'FEV former Ei Vocabulary term')}"><input type="checkbox" value="${browserec.recID.mainTerm}" class="addtoclipboard"/></c:if></td>
		<td class="term">
		<a href="#/search/thes/fullrec.url?snum=${laststep}&term=${f:encode(browserec.recID.mainTerm)}&database=${actionBean.database}" class="fullreclink rolllink">
<c:choose>
<c:when test="${browserec.status ne 'C'}">
			<i>${browserec.recID.mainTerm}</i>
</c:when>
<c:otherwise>
			${browserec.recID.mainTerm}
</c:otherwise>
</c:choose>
		</a>
		</td>
	</tr>
</c:forEach>
	</tbody>
</table>

<%-- SHOW NEXT 5 RESULTS (IF PRESENT) IN NEXT COLUMN --%>
<c:if test="${fn:length(actionBean.browseresults) gt 5}">
<table cellpadding="0" cellspacing="0" border="0" class="termsearchtable">
<thead>
	<tr><th align="center">&nbsp;</th><th align="left">Term</th></tr>
	</thead> 

	<tbody>
<c:forEach items="${actionBean.browseresults}" var="browserec" varStatus="status" begin="5" end="${fn:length(actionBean.browseresults)-1}">
	<tr<c:if test="${fn:toUpperCase(browserec.recID.mainTerm) eq fn:toUpperCase(actionBean.term)}"> style="background-color: #BBDAD2"</c:if>>
		<td align="center""><c:if test="${browserec.status eq 'C' or (actionBean.database eq '1' and browserec.status eq 'L' and browserec.historyScopeNotes eq 'FEV former Ei Vocabulary term')}"><input type="checkbox" value="${browserec.recID.mainTerm}" class="addtoclipboard"/></c:if></td>
		<td class="term">
		<a href="#/search/thes/fullrec.url?snum=${laststep}&term=${f:encode(browserec.recID.mainTerm)}&database=${actionBean.database}" class="fullreclink rolllink">
<c:choose>
<c:when test="${browserec.status ne 'C' and browserec.historyScopeNotes ne 'FEV former Ei Vocabulary term'}">
			<i>${browserec.recID.mainTerm}</i>
</c:when>
<c:otherwise>
			${browserec.recID.mainTerm}
</c:otherwise>
</c:choose>
		</a>
		</td>
	</tr>
</c:forEach>
	</tbody>
</table>
</c:if>
</c:if>

<div class="clear"></div>

</div>

<c:if test="${not empty actionBean.browseresults}">
<div class="pagenav">
<c:if test="${actionBean.previndex >= 0}">
<span><a class="termsearchlink" href="#/search/thes/browse.url?snum=${laststep}&term=${actionBean.term}&index=${actionBean.previndex}&database=${actionBean.database}" title="Previous">&lt; Previous</a><c:if test="${actionBean.nextindex >= 0}"> | </c:if></span>
</c:if>
<c:if test="${actionBean.nextindex >= 0}">
	<span><a class="termsearchlink" href="#/search/thes/browse.url?snum=${laststep}&term=${actionBean.term}&index=${actionBean.nextindex}&database=${actionBean.database}" title="Next">Next &gt;</a></span>
</c:if>
</div>
</c:if>