<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>


	<div id="querydisplaywrap" aria-label="Search query actions" role="navigation">
		<div id="querydisplay">
			<table id="dedupsummary">
				<tr>
					<td colspan="2"><b>Deduplication Summary</b> (First 1000 search results)</td>
				</tr>
				<tr>
					<td title="Choose field and database preference"><a href="/search/results/dedupForm.url?CID=dedupForm&amp;database=${actionBean.database}&amp;SEARCHID=${actionBean.searchid}&amp;&COUNT=1&amp;SEARCHTYPE=${actionBean.searchtype}&amp;RESULTSCOUNT=${actionBean.resultscount}&amp;dbpref=${actionBean.dedupsummary.dbpref}&amp;fieldpref=${actionBean.dedupsummary.fieldpref}">Deduplication criteria</a></td>
					<td>${actionBean.dedupsummary.fieldpreflabel}&nbsp;Preferred;&nbsp;&nbsp;${actionBean.dedupsummary.dbpreflabel}&nbsp;Preferred&nbsp;</td>
				</tr>
				<tr>
					<td>Original search</td>
					<td title="Display original search"><a href="${rerunactionurl}?CID=${actionBean.reruncid}&SEARCHID=${actionBean.searchid}&navigator=PREV&COUNT=1&database=${actionBean.database}">${actionBean.resultscount} Total records for ${actionBean.displayquery}<c:if test="${actionBean.searchtype != 'Combined'}">, ${actionBean.startYear}-${actionBean.endYear}</c:if></a>&nbsp;</td>
				</tr>
				<tr>
					<td>Duplicates removed</td>
					<td title="Display removed duplicates">
					<c:choose>
			            <c:when test="${not(actionBean.dedupsummary.removedDupsCount eq '0')}">
			                <c:choose>
				                <c:when test="${((actionBean.dedupsummary.linkSet eq 'removed') && ( not empty actionBean.dedupsummary.dbLink)) || not(actionBean.dedupsummary.linkSet eq 'removed')}">
					                <a href ="/search/results/dedup.url?CID=dedup&amp;SEARCHID=${actionBean.searchid}&amp;database=${actionBean.database}&amp;dbpref=${actionBean.dedupsummary.dbpref}&amp;fieldpref=${actionBean.dedupsummary.fieldpref}&amp;origin=summary&amp;linkSet=removed&amp;linkResultCount=${actionBean.dedupsummary.removedDupsCount}">
					                ${actionBean.dedupsummary.removedDupsCount}&#160;Total
					                </a>
				                </c:when>
				                <c:otherwise>
					                ${actionBean.dedupsummary.removedDupsCount}&#160;Total
				                </c:otherwise>
				            </c:choose>
			            </c:when>
			            <c:otherwise>
			                <span>${actionBean.dedupsummary.removedDupsCount}&#160;Total</span>
			            </c:otherwise>
		            </c:choose>

					<c:if test="${not empty actionBean.dedupsummary.removedDupsList}">
						<a>&#160;(</a>
						<c:forEach items="${actionBean.dedupsummary.removedDupsList}" var="removedDups" varStatus="removedDupsCount">
							<c:choose>
								<c:when test="${((actionBean.dedupsummary.linkSet eq 'removed') && (not(actionBean.dedupsummary.dbLink  eq removedDups.name) && not(removedDups.count eq '0'))) or (not(actionBean.dedupsummary.linkSet eq'removed') and not(removedDups.count eq '0'))}">
									<a href = "/search/results/dedup.url?CID=dedup&amp;SEARCHID=${actionBean.searchid}&amp;database=${actionBean.database}&amp;dbpref=${actionBean.dedupsummary.dbpref}&amp;fieldpref=${actionBean.dedupsummary.fieldpref}&amp;dbLink=${removedDups.name}&amp;origin=summary&amp;linkSet=removed&amp;linkResultCount=${removedDups.count}">
								    ${removedDups.count}&#160;${removedDups.dbName}</a>
								</c:when>
								<c:otherwise>
								    ${removedDups.count}&#160;${removedDups.dbName}
							    </c:otherwise>
							</c:choose>
						   <c:if test="${removedDupsCount.count != fn:length(actionBean.dedupsummary.removedDupsList)}">
						   <span>,&#160;</span>
						   </c:if>
						</c:forEach>
						<a>)</a>
					</c:if>
					</td>
				</tr>
				<tr>
					<td>Deduplicated set</td>
					<td title="Display deduplicated set">
					<c:choose>
		                <c:when test="${((actionBean.dedupsummary.linkSet != '') && (actionBean.dedupsummary.linkSet eq 'deduped') && (not empty actionBean.dedupsummary.dbLink)) ||
		                ((not empty actionBean.dedupsummary.linkSet) && (actionBean.dedupsummary.linkSet != 'deduped'))}">
							 <a href= "/search/results/dedup.url?CID=dedup&amp;SEARCHID=${actionBean.searchid}&amp;database=${actionBean.database}&amp;dbpref=${actionBean.dedupsummary.dbpref}&amp;fieldpref=${actionBean.dedupsummary.fieldpref}&amp;origin=summary&amp;linkSet=deduped&amp;linkResultCount=${actionBean.dedupsummary.dedupsetCount}">
				             	${actionBean.dedupsummary.dedupsetCount}&#160;Total
				             </a>
		                </c:when>
		                <c:otherwise>
		                 	${actionBean.dedupsummary.dedupsetCount}&#160;Total
						</c:otherwise>
					</c:choose>
				<c:if test="${not empty actionBean.dedupsummary.dbCountList}">
					<a>&#160;(</a>
					<c:forEach items="${actionBean.dedupsummary.dbCountList}" var="dbCount" varStatus="dbStatus">
						<c:choose>
							<c:when test="${((actionBean.dedupsummary.linkSet eq 'deduped') && ((actionBean.dedupsummary.dbLink != dbCount.name) && (dbCount.count != '0'))) || ((actionBean.dedupsummary.linkSet !='deduped') && (dbCount.count!= '0'))}">
							<a href = "/search/results/dedup.url?CID=dedup&amp;SEARCHID=${actionBean.searchid}&amp;database=${actionBean.database}&amp;dbpref=${actionBean.dedupsummary.dbpref}&amp;fieldpref=${actionBean.dedupsummary.fieldpref}&amp;dbLink=${dbCount.name}&amp;origin=summary&amp;linkSet=deduped&amp;linkResultCount=${dbCount.count}">
						    	${dbCount.count}&#160;${dbCount.dbName}
						    </a>
						    </c:when>
						    <c:otherwise>
						    	${dbCount.count}&#160;${dbCount.dbName}
						    </c:otherwise>
					    </c:choose>
					   <c:if test="${dbStatus.count != fn:length(actionBean.dedupsummary.dbCountList)}">
					   <span>,&#160;</span>
					   </c:if>
					</c:forEach>
					<a>)</a>
				</c:if>
					</td>
				</tr>
			</table>


		</div>
		<div class="clear"></div>
	</div>