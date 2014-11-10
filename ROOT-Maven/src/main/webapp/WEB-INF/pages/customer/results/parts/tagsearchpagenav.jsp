<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
	<div class="pagenav" style="float: left;">
		<form name="gotopageform" id="gotopage_${param.position}" action="/search/results/tags.url">
			<input type="hidden" name="SEARCHID" value="${actionBean.searchid}"></input>
			<input type="hidden" value="${actionBean.resultsperpage}" name="resultCountPerPage"/>
			<input type="hidden" name="database" value="${actionBean.database}"></input>
			<input type="hidden" name="CID" value="${actionBean.reruncid}"></input>
			<input type="hidden" name="searchtags" value="${actionBean.searchtags}"></input>
			<input type="hidden" name="searchgrouptags" value="${actionBean.searchgrouptags}"></input>
			<input type="hidden" name="searchtype" value="TagSearch"></input>
			<input type="hidden" name="scope" value="${actionBean.scope}"></input>

			<c:set var="maxSearchresult" value="${(actionBean.pagenav.currentpage - 1) * actionBean.pagenav.resultsperpage + actionBean.resultsperpage}" />

		    <c:if test="${not(actionBean.pageCountOption eq null) && (not empty actionBean.pageCountOption)}">
			<span class="recordperpage" title="Choose the number of results to display per page"><span>Display:</span> 
				<select name="pageSizeVal" id="pageSizeVal_${param.position}" class="pageSizeVal" style="margin:0; width:48px;" title="Number of Results Per Page Dropdown">
				<c:forEach items="${actionBean.pageCountOption}" var="pageCountOption" varStatus="count" >
					<c:choose>
						<c:when test="${param.displaytype eq 'selectedrecords'}">
							<option value="${pageCountOption}" <c:if test="${actionBean.resultsperpage == pageCountOption}"> selected="selected" </c:if>>${pageCountOption}</option>
						</c:when>
						<c:otherwise>
							<option value="${pageCountOption}" <c:if test="${actionBean.resultsperpage == pageCountOption}"> selected="selected" </c:if>>${pageCountOption}</option> 
						</c:otherwise>
					</c:choose>
				</c:forEach>
				</select>
				<span>results per page</span>
				<noscript><input type="submit" class="button" value="Go" title="Go to Selected Order"></input></noscript>
			</span>
			</c:if>
		<span class="pagenavspan">	
		<c:if test="${actionBean.pagenav.currentpage > 1}">
			<span><a href="/search/results/tags.url?CID=${actionBean.reruncid}&navigator=PREV&SEARCHID=${actionBean.searchid}&COUNT=${actionBean.pagenav.previndex}&database=${actionBean.database}&scope=${actionBean.scope}" title="Go to previous page">&lt; Previous</a>&nbsp;&nbsp;|&nbsp;&nbsp;</span> 
		</c:if>
			<span><c:choose><c:when test="${actionBean.pagenav.currentpage == 1}">Go to page: </c:when><c:otherwise>Page: </c:otherwise></c:choose></span>
			<input name="PAGE" type="text" size="2" value="${actionBean.pagenav.currentpage}" title="Go to Page Text Box"style="text-align: right"/> 
			<span>of ${actionBean.pagenav.pagecount}</span>
			
			<input type="submit" class="button" value="Go" title="Go to designated page"></input> 
		<c:if test="${(actionBean.pagenav.currentpage < actionBean.pagenav.pagecount)}">
			<span>&nbsp;&nbsp;|&nbsp;&nbsp;
			<c:choose>
				<c:when test="${maxSearchresult >= 4000}">
				    <a href="javascript:window.alert('Only the first 4000 records can be viewed.')" title="Go to next page">Next &gt;</a>
				</c:when>
				<c:otherwise>
					<a href="/search/results/tags.url?CID=${actionBean.reruncid}&navigator=NEXT&SEARCHID=${actionBean.searchid}&COUNT=${actionBean.pagenav.nextindex}&database=${actionBean.database}&SEARCHTYPE=TagSearch&scope=${actionBean.scope}" title="Go to next page">Next &gt;</a>
				</c:otherwise>
			</c:choose>
			</span>			
	    </c:if>
	    </span>
    	</form> 
	</div>
