<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
	<div class="pagenav" style="float: left;">
		<form name="gotopageform" id="gotopage_${param.position}" action="${rerunactionurl}">
			<input type="hidden" name="SEARCHID" value="${actionBean.searchid}"></input>
			<input type="hidden" value="${actionBean.resultsperpage}" name="resultCountPerPage"/>
			<input type="hidden" value="${rerunactionurl}" name="rerunactionurl"/>

			<c:choose>
			<c:when test="${param.displaytype eq 'selectedrecords'}">
			<input type="hidden" name="DATABASETYPE" value="${actionBean.database}"></input>
			<input type="hidden" name="DATABASEID" value="${actionBean.database}"></input>
			<input type="hidden" name="CID" value="${actionBean.cid}"></input>
			<input type="hidden" name="SEARCHTYPE" value="${actionBean.searchtype}"></input>
			<input type="hidden" name="searchresults" value="${actionBean.searchresultsencoded}"></input>
			<input type="hidden" name="newsearch" value="${actionBean.newsearchencoded}"></input>
			<INPUT TYPE="HIDDEN" NAME="backIndex" VALUE="${actionBean.backindex}">
			<input type="hidden" name="pagecount" value="${actionBean.pagenav.pagecount}"></input>
			</c:when>
			<c:otherwise>
			<input type="hidden" name="database" value="${actionBean.database}"></input>
			<input type="hidden" name="CID" id="CID" value="${actionBean.reruncid}"></input>
			<input type="hidden" name="sort" value="${actionBean.sort}"></input>
			<input type="hidden" name="sortdir" value="${actionBean.sortdir}"></input>
			<input type="hidden" name="pagecount" value="${actionBean.pagenav.pagecount}"></input>
			</c:otherwise>
			</c:choose>

			<c:set var="maxSearchresult" value="${(actionBean.pagenav.currentpage - 1) * actionBean.pagenav.resultsperpage + actionBean.resultsperpage}" />
			<c:set var="maxSearchresult" value="${(actionBean.pagenav.currentpage - 1) * actionBean.pagenav.resultsperpage + actionBean.resultsperpage}" />
		    <c:if test="${not(actionBean.pageCountOption eq null) && (not empty actionBean.pageCountOption)}">
			<span class="recordperpage" title="Choose the number of results to display per page" aria-label="Select number of results per page" role="navigation"><span>Display:</span>
						<select name="pageSizeVal" id="pageSizeVal_${param.position}" class="pageSizeVal" title="Results Per Page Dropdown">
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
			<c:choose>
			<c:when test="${param.displaytype eq 'selectedrecords'}">
				<c:choose>
					<c:when test="${fn:contains(actionBean.cid,'abstract')}">
						<span><a href="/selected/abstract.url?CID=${actionBean.cid}&navigator=PREV&SEARCHID=${actionBean.searchid}&BASKETCOUNT=${actionBean.pagenav.currentpage-1}&DATABASEID=${actionBean.database}&DATABASETYPE=${actionBean.database}&SEARCHTYPE=${actionBean.searchtype}&searchresults=${actionBean.searchresultsencoded}&newsearch=${actionBean.newsearchencoded}&backIndex=${actionBean.backindex}" title="Go to previous page">&lt; Previous</a>&nbsp;&nbsp;|&nbsp;&nbsp;</span>
					</c:when>
					<c:when test="${fn:contains(actionBean.cid,'citation')}">
						<span><a href="/selected/citation.url?CID=${actionBean.cid}&navigator=PREV&SEARCHID=${actionBean.searchid}&BASKETCOUNT=${actionBean.pagenav.currentpage-1}&DATABASEID=${actionBean.database}&DATABASETYPE=${actionBean.database}&SEARCHTYPE=${actionBean.searchtype}&searchresults=${actionBean.searchresultsencoded}&newsearch=${actionBean.newsearchencoded}&backIndex=${actionBean.backindex}" title="Go to previous page">&lt; Previous</a>&nbsp;&nbsp;|&nbsp;&nbsp;</span>
					</c:when>
					<c:otherwise>
						<span><a href="/selected/detailed.url?CID=${actionBean.cid}&navigator=PREV&SEARCHID=${actionBean.searchid}&BASKETCOUNT=${actionBean.pagenav.currentpage-1}&DATABASEID=${actionBean.database}&DATABASETYPE=${actionBean.database}&SEARCHTYPE=${actionBean.searchtype}&searchresults=${actionBean.searchresultsencoded}&newsearch=${actionBean.newsearchencoded}&backIndex=${actionBean.backindex}" title="Go to previous page">&lt; Previous</a>&nbsp;&nbsp;|&nbsp;&nbsp;</span>
					</c:otherwise>
				 </c:choose>
			</c:when>
			<c:otherwise>
			<span><a href="${rerunactionurl}?CID=${actionBean.reruncid}&navigator=PREV&SEARCHID=${actionBean.searchid}&COUNT=${actionBean.pagenav.previndex}&database=${actionBean.database}" title="Go to previous page">&lt; Previous</a>&nbsp;&nbsp;|&nbsp;&nbsp;</span>
			</c:otherwise>
			</c:choose>
		</c:if>
			<span ><c:choose><c:when test="${actionBean.pagenav.currentpage == 1}">Go to page: </c:when><c:otherwise>Page: </c:otherwise></c:choose></span>
			<c:set var="inputname"><c:choose><c:when test="${param.displaytype eq 'selectedrecords'}">BASKETCOUNT</c:when><c:otherwise>PAGE</c:otherwise></c:choose></c:set>
			<input name="${inputname}" type="text" size="2" value="${actionBean.pagenav.currentpage}" style="text-align: right" title="Page Number Text Box"/>
			<span>of ${actionBean.pagenav.pagecount}</span>

			<input type="submit" class="button" value="Go" title="Go to designated page"></input>
		<c:if test="${(actionBean.pagenav.currentpage < actionBean.pagenav.pagecount)}">
			<c:choose>
			<c:when test="${param.displaytype eq 'selectedrecords'}">
				<span>&nbsp;&nbsp;|&nbsp;&nbsp;
				<c:choose>
					<c:when test="${maxSearchresult >= 4000}">
						<a href="javascript:window.alert('Only the first 4000 records can be viewed.')" title="Go to next page">Next &gt;</a>
				</c:when>
				<c:otherwise>
					<c:choose>
						<c:when test="${fn:contains(actionBean.cid,'abstract')}">
							<a href="/selected/abstract.url?CID=${actionBean.cid}&navigator=NEXT&SEARCHID=${actionBean.searchid}&BASKETCOUNT=${actionBean.pagenav.currentpage+1}&DATABASEID=${actionBean.database}&DATABASETYPE=${actionBean.database}&SEARCHTYPE=${actionBean.searchtype}&searchresults=${actionBean.searchresultsencoded}&newsearch=${actionBean.newsearchencoded}&backIndex=${actionBean.backindex}" title="Go to next page">Next &gt;</a>
						</c:when>
						<c:when test="${fn:contains(actionBean.cid,'citation')}">
							<a href="/selected/citation.url?CID=${actionBean.cid}&navigator=NEXT&SEARCHID=${actionBean.searchid}&BASKETCOUNT=${actionBean.pagenav.currentpage+1}&DATABASEID=${actionBean.database}&DATABASETYPE=${actionBean.database}&SEARCHTYPE=${actionBean.searchtype}&searchresults=${actionBean.searchresultsencoded}&newsearch=${actionBean.newsearchencoded}&backIndex=${actionBean.backindex}" title="Go to next page">Next &gt;</a>
						</c:when>
						<c:otherwise>
							<a href="/selected/detailed.url?CID=${actionBean.cid}&navigator=NEXT&SEARCHID=${actionBean.searchid}&BASKETCOUNT=${actionBean.pagenav.currentpage+1}&DATABASEID=${actionBean.database}&DATABASETYPE=${actionBean.database}&SEARCHTYPE=${actionBean.searchtype}&searchresults=${actionBean.searchresultsencoded}&newsearch=${actionBean.newsearchencoded}&backIndex=${actionBean.backindex}" title="Go to next page">Next &gt;</a>
						</c:otherwise>
					</c:choose>
				</c:otherwise>
					</c:choose>
				</span>
			</c:when>
		<c:otherwise>
			<span>&nbsp;&nbsp;|&nbsp;&nbsp;
			<c:choose>
				<c:when test="${maxSearchresult >= 4000}">
				    <a href="javascript:window.alert('Only the first 4000 records can be viewed.')" title="Go to next page">Next &gt;</a>
				</c:when>
				<c:otherwise>
					<a href="${rerunactionurl}?CID=${actionBean.reruncid}&navigator=NEXT&SEARCHID=${actionBean.searchid}&COUNT=${actionBean.pagenav.nextindex}&database=${actionBean.database}" title="Go to next page">Next &gt;</a>
				</c:otherwise>
			</c:choose>
			</span>
			</c:otherwise>
		  </c:choose>
	    </c:if>
	    </span>
    	</form>
	</div>
