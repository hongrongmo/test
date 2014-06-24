<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="/WEB-INF/tlds/functions.tld" prefix="f" %>
<style type="text/css">
.RedText  {color:#FF3300;}
</style>
<h2 class="searchcomponentlabel" style="float: none; margin-bottom: 3px; text-transform: uppercase">Search</h2>

<c:set var="laststep">${fn:length(actionBean.steps)}</c:set>
<c:set var="scon" value="${actionBean.steps[0].context}"/>
<div id="termpath">
  <c:choose>
    <c:when test="${actionBean.termsearchcount == 0}">
      <span>0 matching terms found for: <span step=0 scon="${scon}" class="Bold">${f:decode(actionBean.term)}</span></span>
    </c:when>
    <c:when test="${fn:length(actionBean.steps) == 1}">
      ${actionBean.termsearchcount} matching terms found for: <span step=0 scon=${actionBean.steps[0].context} class="Bold">${f:decode(actionBean.steps[0].title)}</span>
    </c:when>
    <c:otherwise>
	<c:forEach var="step" items="${actionBean.steps}" varStatus="status">
		<c:choose>
			<c:when test="${status.last}"> 
				<span step="${status.count-1}"<c:if test="${status.count == 1}"> scon=${step.context}</c:if> style="font-weight: bold" href="#${step.link}&snum=${step.stepNum}">${step.title}</span>
			</c:when>
			<c:otherwise>
				<a href="#${step.link}&snum=${step.stepNum}"
					title="${step.title}" class="termsearchlink rolllink"><span step="${status.count-1}"<c:if test="${status.count == 1}"> scon=${step.context}</c:if>>${step.title}</a> >> </span>
			</c:otherwise>
		</c:choose>
	</c:forEach>
    </c:otherwise>
  </c:choose>
</div>

<div class="clear"></div>
<div id="termresults" class="termelement">
	<c:choose>
		<c:when test="${not empty actionBean.termsearchresults}">
			<table cellpadding="0" cellspacing="0" border="0"
				class="termsearchtable">
				<thead>
					<tr>
						<th align="center">&nbsp;</th>
						<th align="left">Term</th>
					</tr>
				</thead>
				<tbody>
<c:forEach items="${actionBean.termsearchresults}" var="thesrecord" varStatus="status" end="4">
						<tr>
							<td align="center""><c:if test="${thesrecord.status eq 'C' or (actionBean.database eq '1' and thesrecord.status eq 'L' and thesrecord.historyScopeNotes eq 'FEV former Ei Vocabulary term')}"><input type="checkbox" value="${thesrecord.recID.mainTerm}" class="addtoclipboard" /></c:if></td>
							<td class="term"><a
								href="#/search/thes/fullrec.url?snum=${laststep}&term=${f:encode(thesrecord.recID.mainTerm)}&database=${actionBean.database}"
								class="fullreclink rolllink"> <c:choose>
							<c:when test="${thesrecord.status ne 'C' and thesrecord.historyScopeNotes ne 'FEV former Ei Vocabulary term'}">
								<span class="Italic">${thesrecord.recID.mainTerm}</span>
							</c:when>
							<c:otherwise>
								${thesrecord.recID.mainTerm}
						    </c:otherwise></c:choose>
							</a></td>
						</tr>
</c:forEach>
				</tbody>
			</table>
			<c:if test="${fn:length(actionBean.termsearchresults) gt 5}">
				<table cellpadding="0" cellspacing="0" border="0"
					class="termsearchtable">
					<thead>
						<tr>
							<th align="center">&nbsp;</th>
							<th align="left">Term</th>
						</tr>
					</thead>
					<tbody>
<c:forEach items="${actionBean.termsearchresults}"
	var="thesrecord" varStatus="status" begin="5"
	end="${fn:length(actionBean.termsearchresults)-1}">
						<tr>
							<td align="center""><c:if test="${thesrecord.status eq 'C' or (actionBean.database eq '1' and thesrecord.status eq 'L' and thesrecord.historyScopeNotes eq 'FEV former Ei Vocabulary term')}"><input type="checkbox" value="${thesrecord.recID.mainTerm}" class="addtoclipboard" /></c:if></td>
							<td class="term"><a
								href="#/search/thes/fullrec.url?snum=${laststep}&term=${f:encode(thesrecord.recID.mainTerm)}&database=${actionBean.database}"
								class="fullreclink rolllink"> <c:choose>
							<c:when test="${thesrecord.status ne 'C' and thesrecord.historyScopeNotes ne 'FEV former Ei Vocabulary term'}">
								<span class="Italic">${thesrecord.recID.mainTerm}</span>
							</c:when>
							<c:otherwise>
								${thesrecord.recID.mainTerm}
						    </c:otherwise></c:choose>
							</a></td>
						</tr>
</c:forEach>
					</tbody>
				</table>
				<div class="clear"></div>
			</c:if>
		</c:when>

		<c:when test="${not empty actionBean.termsearchsuggests}">
			<div id="noresults" class="termelement">
				<p>Your search did not find any match for "${f:decode(actionBean.term)}".</p>
				<p>To go to the thesaurus record, click on that term.</p>
				<p>To rerun your search, select the radio button of the best
					match and click Submit.</p>
			</div>
			<div id="noresultslist" class="termelement">
				<c:forEach items="${actionBean.termsearchsuggests}" var="thesrecord" varStatus="status">
					<div class="noresult" style="margin-top: 3px">
						<input type="radio" name="termsuggest" value="${thesrecord.recID.mainTerm}"
							class="termsuggest" /> <a class="fullreclink rolllink"
							href="#/search/thes/fullrec.url?snum=${laststep}&amp;term=${f:encode(thesrecord.recID.mainTerm)}&amp;database=${actionBean.database}"><c:choose><c:when test="${thesrecord.status eq 'L'}"><i>${thesrecord.recID.mainTerm}</i></c:when><c:otherwise>${thesrecord.recID.mainTerm}</c:otherwise></c:choose></a>
					</div>
				</c:forEach>
			</div>
		</c:when>
		<c:otherwise>
		    <span class="RedText">Your search did not find any match for "${f:decode(actionBean.term)}".</span>   
	    </c:otherwise>
	</c:choose>
</div>

<c:if test="${not empty actionBean.termsearchresults}">
<form name="gotopageform" id="gotopageform"
	action="/search/thes/termsearch.url">
	<input type="hidden" name="database" value="${actionBean.database}"></input> 
	<input type="hidden" name="term" value="${actionBean.term}"></input> 
	<input type="hidden" name="EISESSION" value="${actionBean.sessionid}"></input> 
	<input type="hidden" name="pagecount" value="${actionBean.pagenav.pagecount}"></input> 
	<input type="hidden" name="step" value="${step}"></input> 
	<div class="pagenav">
		<c:if test="${actionBean.pagenav.currentpage > 1}">
			<span><a class="termsearchlink"
				href="#/search/thes/termsearch.url?snum=${laststep}&term=${actionBean.term}&pageNumber=${actionBean.pagenav.currentpage-1}&database=${actionBean.database}"
				title="Previous">&lt; Previous</a> | </span>
		</c:if>
			<span><c:choose><c:when test="${actionBean.pagenav.currentpage == 1}">Go to page: </c:when><c:otherwise>Page:</c:otherwise></c:choose></span> <input type="hidden" name="CID"
				value="thesTermSearch"></input> <input
				name="pageNumber" type="text" size="2"
				value="${actionBean.pagenav.currentpage}"> <span>of
				${actionBean.pagenav.pagecount}</span> <input id="go" type="submit"	class="button" value="Go" title="Go to designated page"></input>
		<c:if
			test="${actionBean.pagenav.currentpage < actionBean.pagenav.pagecount}">
			<span> | <a class="termsearchlink"
				href="#/search/thes/termsearch.url?snum=${laststep}&term=${actionBean.term}&pageNumber=${actionBean.pagenav.currentpage+1}&database=${actionBean.database}"
				title="Next">Next &gt;</a></span>
		</c:if>
		<div class="clear"></div>
	</div>
</form>
</c:if>

    
