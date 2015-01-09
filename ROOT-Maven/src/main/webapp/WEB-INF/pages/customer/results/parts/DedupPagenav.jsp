<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
	<div class="pagenav">
		<form name="gotopageform" id="gotopage_${param.position}" action="/search/results/dedup.url">
			<c:choose>
			<c:when test="${not(actionBean.linkResultCount eq '') && not(actionBean.linkResultCount eq 'null')}">
			   <c:set var="dedupResultCount" value="${actionBean.linkResultCount}"/>
			</c:when>
			<c:otherwise>
			 <c:set var="dedupResultCount" value="${actionBean.dedupsummary.dedupsetCount}"/>
			</c:otherwise>
		</c:choose>
			<c:set var="reruncidvalue"><c:choose><c:when test="${actionBean.dedup}">dedup</c:when><c:otherwise>${actionBean.reruncid}</c:otherwise></c:choose></c:set>

			    <input type="hidden" name="CID" value="${reruncidvalue}"></input>
				<input type="hidden" name="SEARCHID" value="${actionBean.searchid}"></input>
				<input type="hidden" name="database" value="${actionBean.database}"></input>
		        <input type="HIDDEN" value="${actionBean.resultscount}" name="RESULTSCOUNT"/>
				<input type="hidden" value="${actionBean.pagenav.resultscount}" name="dedupSet"/>
				<input type="hidden" value="${actionBean.dedupsummary.dbpref}" name="dbpref"/>
				<input type="hidden" value="${actionBean.dedupsummary.fieldpref}" name="fieldpref"/>
				<input type="hidden" value="${actionBean.dedup}" name="DupFlag"/>
				<input type="hidden" value="${actionBean.dedupsummary.origin}" name="origin"/>
				<input type="hidden" value="${actionBean.dedupsummary.dbLink}" name="dbLink"/>
				<input type="hidden" value="${actionBean.dedupsummary.linkSet}" name="linkSet"/>
				<input type="hidden" value="${actionBean.dedupsummary.dedupSubsetCount}" name="dedupSubset"/>
				<input type="hidden" value="${actionBean.dedupsummary.remvoedSubsetCount}" name="removedSubset"/>
				<input type="hidden" value="${actionBean.dedupsummary.removedDupsCount}" name="removedDups"/>
				<input type="hidden" value="NEXT" name="navigator" />
				<input type="hidden" value="${actionBean.resultsperpage}" name="resultCountPerPage"/>
				<input type="hidden" value="${dedupResultCount}" name="linkResultCount"/>
				<input type="hidden" name="sort" value="${actionBean.sort}"></input>
				<input type="hidden" name="sortdir" value="${actionBean.sortdir}"></input>
				<input type="hidden" value="/search/results/dedup.url" name="rerunactionurl"/>
                <input type="hidden" name="pagecount" value="${actionBean.pagenav.pagecount}"></input>


			 <c:set var="maxSearchresult" value="${(actionBean.pagenav.currentpage - 1) * actionBean.pagenav.resultsperpage + actionBean.resultsperpage}" />
			 <c:if test="${not(actionBean.pageCountOption eq null) && (not empty actionBean.pageCountOption)}">
			<span class="recordperpage" style="padding-left:10px;" title="Choose the number of results to display per page"  aria-label="Select number of results per page" role="navigation"><span>Display:</span>
						<select name="pageSizeVal" id="pageSizeVal_${param.position}" class="pageSizeVal">
						<c:forEach items="${actionBean.pageCountOption}" var="pageCountOption" varStatus="count" >
									<option value="${pageCountOption}" <c:if test="${actionBean.resultsperpage == pageCountOption}"> selected="selected" </c:if>>${pageCountOption}</option>
						</c:forEach>
						</select>
						<span>results per page</span>
						<noscript><input type="submit" class="button" value="Go" title="Go to Selected Order"></input></noscript>
			</span>
			</c:if>
			<span class="pagenavspan" aria-label="Select page" role="navigation">
		<c:if test="${actionBean.pagenav.currentpage > 1}">
			<span><a href="/search/results/dedup.url?CID=${reruncidvalue}&amp;DupFlag=${actionBean.dedup}&amp;dbpref=${actionBean.dedupsummary.dbpref}&amp;fieldpref=${actionBean.dedupsummary.fieldpref}&amp;origin=${actionBean.dedupsummary.origin}&amp;dbLink=${actionBean.dedupsummary.dbLink}&amp;linkSet=${actionBean.dedupsummary.linkSet}&amp;navigator=PREV&amp;SEARCHID=${actionBean.searchid}&amp;COUNT=${actionBean.pagenav.previndex}&amp;database=${actionBean.database}&amp;linkResultCount=${dedupResultCount}" title="Go to previous page">&lt; Previous</a>&nbsp;&nbsp;|&nbsp;&nbsp;</span>
		</c:if>
			<span><c:choose><c:when test="${actionBean.pagenav.currentpage == 1}">Go to page: </c:when><c:otherwise>Page: </c:otherwise></c:choose></span>
			<c:set var="inputname"><c:choose><c:when test="${param.displaytype eq 'selectedrecords'}">BASKETCOUNT</c:when><c:otherwise>PAGE</c:otherwise></c:choose></c:set>
			<input name="${inputname}" type="text" size="2" value="${actionBean.pagenav.currentpage}" style="text-align: right"/>
			<span>of ${actionBean.pagenav.pagecount}</span>

			<input type="submit" class="button" value="Go" title="Go to designated page"></input>

		<c:if test="${(actionBean.pagenav.currentpage < actionBean.pagenav.pagecount)}">
			<span>&nbsp;&nbsp;|&nbsp;&nbsp;
			<c:choose>
				<c:when test="${maxSearchresult >= 4000}">
					<a href="javascript:window.alert('Only the first 4000 records can be viewed.')" title="Go to next page">Next &gt;</a>
				</c:when>
				<c:otherwise>
					<a href="/search/results/dedup.url?CID=${reruncidvalue}&amp;DupFlag=${actionBean.dedup}&amp;dbpref=${actionBean.dedupsummary.dbpref}&amp;fieldpref=${actionBean.dedupsummary.fieldpref}&amp;origin=${actionBean.dedupsummary.origin}&amp;dbLink=${actionBean.dedupsummary.dbLink}&amp;linkSet=${actionBean.dedupsummary.linkSet}&amp;navigator=NEXT&amp;SEARCHID=${actionBean.searchid}&amp;COUNT=${actionBean.pagenav.nextindex}&amp;database=${actionBean.database}&amp;linkResultCount=${dedupResultCount}" title="Go to next page">Next &gt;</a>
				</c:otherwise>
			</c:choose>
			</span>
		</c:if>
	</span>
		</form>
	</div>
