<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" session="false"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/tlds/functions.tld" prefix="f" %>

		<div id="querydisplaywrap" aria-label="Search query actions" role="navigation">
		<div id="querydisplay">
			<h1 style="margin:5px 0 0 0; font-size: 12px"><b><c:choose><c:when test="${'Combined' eq actionBean.searchtype}">Combined Search</c:when><c:when test="${'Expert' eq actionBean.searchtype}">Expert Search</c:when><c:when test="${'Book' eq actionBean.searchtype}">eBook Search</c:when><c:when test="${'Thesaurus' eq actionBean.searchtype}">Thesaurus Search</c:when><c:otherwise>Quick Search</c:otherwise></c:choose></b></h1>
			<c:set var='gumbo'><c:choose><c:when test="${'Book' eq actionBean.searchtype}">record</c:when><c:when test="${not empty actionBean.citedbyflag}">patent</c:when><c:otherwise>article</c:otherwise></c:choose></c:set>
			<p><b>${actionBean.resultscount}</b> ${gumbo}<c:if test="${actionBean.resultscount > 1 or actionBean.resultscount == 0}">s</c:if> found in ${actionBean.foundin}
<c:choose>
	<c:when test="${not empty actionBean.emailalertweek}">&nbsp;for Week ${actionBean.emailalertweek}</c:when>
	<c:when test="${not empty actionBean.updatesNo}">&nbsp;for Last ${actionBean.updatesNo} update(s)</c:when>
	<c:when test="${'Combined' eq actionBean.searchtype}">&nbsp;for:</c:when>
	<c:otherwise>&nbsp;for ${actionBean.startYear}-${actionBean.endYear}</c:otherwise>
</c:choose><c:if test="${not empty actionBean.citedbyflag}"> that cite</c:if>:

			<span id="querytext">
			<span id="physicalquery" style="display:none">${actionBean.physicalquery }</span>
			<span id="displayquery" style="display:none">${actionBean.displayquery }</span>
			<c:choose>
			<c:when test="${empty actionBean.refinequery}"><a title="" href="${rerunactionurl}?CID=${actionBean.reruncid}&RERUN=${actionBean.searchid}&database=${actionBean.database}&STEP=1">${actionBean.displayquery}</a></c:when>
			<c:otherwise>
			<c:forEach var="refine" items="${actionBean.refinequery}" varStatus="status">
				<c:choose>
				<c:when test="${status.count == 1}"><a href="${rerunactionurl}?CID=${actionBean.reruncid}&RERUN=${actionBean.searchid}&database=${actionBean.database}&REMOVE=${status.count}" title="Remove Term"><img src="/static/images/remove.jpg" title=""/></a>&nbsp;<a title="" href="${rerunactionurl}?CID=${actionBean.reruncid}&RERUN=${actionBean.searchid}&database=${actionBean.database}&STEP=${status.count}">${refine.value}</a></c:when>
				<c:otherwise>&nbsp;&nbsp;<a href="${rerunactionurl}?CID=${actionBean.reruncid}&RERUN=${actionBean.searchid}&database=${actionBean.database}&REMOVE=${status.count}" title="Remove Term"><img src="/static/images/remove.jpg" title=""/></a>&nbsp;<span class="plusminus"><c:choose><c:when test="${'plus' eq refine.includeall}">+</c:when><c:otherwise>-</c:otherwise></c:choose></span><a title="" href="${rerunactionurl}?CID=${actionBean.reruncid}&RERUN=${actionBean.searchid}&database=${actionBean.database}&STEP=${status.count}">${refine.value}</a></c:otherwise>
				</c:choose>
			</c:forEach>
			</c:otherwise>
			</c:choose>
			</span>
			</p>

			<ul id="optionnav" class="horizlist">
                <li class="new"><a id="newsearchlink" class="webeventlink" category="Query Toolbar" action="New Search" href="/home.url?CID=home&database=${actionBean.database}" title="Run a new search">New Search</a></li>
				<c:set var="editcid"><c:choose><c:when test="${actionBean.searchtype eq 'Thesaurus'}">thesHome</c:when><c:when test="${actionBean.searchtype eq 'Book'}">ebookSearch</c:when><c:when test="${actionBean.searchtype eq 'Combined'}">expertSearch</c:when><c:otherwise><c:out value="${fn:toLowerCase(actionBean.searchtype)}"/>Search</c:otherwise></c:choose></c:set>
                <c:set var="editurl"><c:choose><c:when test="${actionBean.searchtype eq 'Thesaurus'}">/search/thesHome.url</c:when><c:when test="${actionBean.searchtype eq 'Book'}">/search/ebook.url</c:when><c:when test="${actionBean.searchtype eq 'Quick'}">/search/quick.url</c:when><c:otherwise>/search/expert.url</c:otherwise></c:choose></c:set>
				<li class="edit"><a id="editsearchlink" class="webeventlink" category="Query Toolbar" action="Edit Search" href="${editurl}?searchid=${actionBean.searchid}&database=${actionBean.database}<c:if test="${actionBean.searchtype eq 'Thesaurus'}">#init</c:if>" title="Edit your search query">Edit</a></li>

				<li <c:if test="${not(actionBean.savedsearchflag eq 'On')}">class="savesearch"</c:if>>
					<c:set var="nexturl">CID=addDeleteSavedSearch&selectvalue=mark&option=SavedSearch&searchid=${actionBean.searchid}&database=${actionBean.database}</c:set>
					<c:set var="backurl">/search/results/quick.url?CID=quickSearchCitationFormat&SEARCHID=${actionBean.searchid}&database=${actionBean.database}</c:set>
					<c:set var="title"><c:choose><c:when test="${actionBean.savedsearchflag eq 'On'}">Remove your saved search query</c:when><c:otherwise>Save your search query to Settings</c:otherwise></c:choose></c:set>
					<c:set var="namevalue"><c:choose><c:when test="${actionBean.savedsearchflag eq 'On'}">Remove Search</c:when><c:otherwise>Save Search</c:otherwise></c:choose></c:set>
					<c:choose>
					    <c:when test="${actionBean.savedsearchflag eq 'On'}">
					    	<a id="savesearchlink" class="webeventlink"  category="Query Toolbar" action="Remove Search" style="margin:0;" href="/personal/savedsearch/adddelete.url?selectvalue=unmark&option=SavedSearch&searchid=${actionBean.searchid}&database=${actionBean.database}&backurl=SAVEDSEARCH" title="${title}"><img class="savedsearch" src="/static/images/remove.jpg" title=""/> ${namevalue}</a>
					    </c:when>
						<c:otherwise>
                            <a id="removeearchlink" class="webeventlink"  category="Query Toolbar" action="Save Search" href="/personal/savedsearch/adddelete.url?selectvalue=mark&option=SavedSearch&searchid=${actionBean.searchid}&database=${actionBean.database}&backurl=SAVEDSEARCH" title="${title}">${namevalue}</a>
						</c:otherwise>
					</c:choose>
				</li>

				<li <c:if test="${not(actionBean.createalertflag eq 'On')}">class="createalert"</c:if>>
					<c:set var="nexturl">CID=addDeleteSavedSearch&selectvalue=mark&option=EmailAlert&searchid=${actionBean.searchid}&database=${actionBean.database}</c:set>
					<c:set var="backurl">/search/results/quick.url?CID=quickSearchCitationFormat&SEARCHID=${actionBean.searchid}&database=${actionBean.database}</c:set>
					<c:set var="title"><c:choose><c:when test="${actionBean.createalertflag eq 'On'}">Remove alert for this search query</c:when><c:otherwise>Create an alert for this search query in Settings</c:otherwise></c:choose></c:set>
					<c:set var="namevalue"><c:choose><c:when test="${actionBean.createalertflag eq 'On'}">Remove Alert</c:when><c:otherwise>Create Alert</c:otherwise></c:choose></c:set>
					<c:choose>
					<c:when test="${actionBean.createalertflag eq 'On'}">
				    	<a id="createalertlink" class="webeventlink"  category="Query Toolbar" action="Remove Alert" style="margin:0;" href="/personal/savedsearch/adddelete.url?selectvalue=unmark&option=EmailAlert&searchid=${actionBean.searchid}&database=${actionBean.database}&backurl=SAVEDSEARCH" title="${title}"><img class="createdalert" src="/static/images/remove.jpg " title=""/>${namevalue}</a>
				    </c:when>
					<c:otherwise>
                        <a id="removealertlink" class="webeventlink"  category="Query Toolbar" action="Create Alert" href="/personal/savedsearch/adddelete.url?selectvalue=mark&option=EmailAlert&searchid=${actionBean.searchid}&database=${actionBean.database}&backurl=SAVEDSEARCH" title="${title}">${namevalue}</a>
					</c:otherwise>
				</c:choose>
				</li>

				<c:if test="${not empty actionBean.rsslink}">
				<li id="rssfeedlink" class="webeventlink rssfeed" category="Query Toolbar" action="RSS Feed" href="#">${actionBean.rsslink}</li>
				</c:if>
				<li class="history"><a id="searchhistorylink" class="webeventlink" category="Query Toolbar" action="Show History" href="${editurl}?CID=${editcid}&database=${actionBean.database}&history=t" title="View your search history for this session">Search history</a></li>
				<c:if test="${actionBean.showmap}">
				<li class="showmap"><a id="mapToggleAnchor" href="" title="Display result set on a map">Show Map</a></li>
				</c:if>
			</ul>
			<div class="clear"></div>
		</div>

		<div class="clear"></div>
	</div>
