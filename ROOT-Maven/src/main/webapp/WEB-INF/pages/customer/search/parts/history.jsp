<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld" prefix="stripes" %>
<%@ taglib uri="/WEB-INF/tlds/custom.tld" prefix="cm" %>

<div id="searchhistoryboxtop">
<stripes:errors field="searchHistoryError"><div id="errormessage"><stripes:individual-error/></div></stripes:errors>

<div id="searchhistorybox" class="shadowbox" style="float: none; clear: both; margin-bottom: 5px;" role=”complementary” aria-label="Search History" >
    <span id="savedcount" style="display:none">${actionBean.savedcount}</span>
    <span id="alertcount" style="display:none">${actionBean.alertcount}</span>
    <div class="title">
        <div style="float: right; font-size: 12px; *padding-top: 5px">
            <a id="historytoggle" class="historyminus" title="Hide search history">Hide</a>
        </div>
        <span>
            <h2 style="float: left">Search history</h2>
            <a href="${actionBean.helpUrl}#Search_History.htm" style="padding-top: 10px;
padding-left: 5px;" class="helpurl" alt="Learn more about search history" title="Learn more about search history"><img src="/static/images/i.png" border="0" class="infoimg" align="bottom" alt=""/></a>
        </span>
    </div>
    <div class="clear"></div>
    <div id="historyshow">
        <c:choose>
            <c:when test="${empty actionBean.searchHistoryList}">
                <div class="toolbar" style="height: 5px"></div>

            </c:when>
            <c:otherwise>
                <div class="toolbar" style="padding: 5px; line-height: 20px">
          <form id="combineSearchForm" action="/search/history/combine.url" method="get" name="combineSearch">
                   <input type="hidden" id="defaultdb" name="defaultdb" value="${actionBean.database}"/>
                   <input type="hidden" id="backurl" name="backurl" value="SEARCHHISTORY"/>
                    <span style="float: right; margin-right: 7px">
                        <h2 style="float: none; display: inline" class="searchcomponentlabel">SORT BY </h2>
                        <input type="radio" value="relevance" id="relChkbx" name="sort" checked="checked" title="Sort your search by Relevance">
                        <label class="SmBlackText" for="relChkbx">Relevance</label>
                        <input type="radio" value="yr" name="sort" id="pubChkbx" title="Sort your search by Year in descending order">
                        <label class="SmBlackText" for="pubChkbx">Date (Newest)</label>
                    </span>
                    Combine Searches: <input type="text" size="28" name="txtcombine" title="Combine Search Textbox">
                    <input type="submit" value="Search" title="Submit Combined Search" />
          </form>
                </div>
                <div class="clear"></div>
            </c:otherwise>
        </c:choose>
    <input type="hidden" id="alertCount" name="alertCount" value="20">
    <input type="hidden" id="savedCount" name="savedCount" value="20">
    <input type="hidden" id="savedSeachesAndAlertsLimit" name="savedSeachesAndAlertsLimit" value="${actionBean.savedSeachesAndAlertsLimit}">

        <table id="historytable" cellpadding="0" cellspacing="0" style="white-space: normal">
            <thead>
                <tr>
                    <th style="width:70px">Combine</th>
                    <!-- <th style="width:64px">Type</th> -->
                    <th style="width:60em">Search</th>
                    <th>Results</th>
                    <th style="width:10em">Database</th>
                    <th style="width: 40px; border-right: none;">Delete</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${empty actionBean.searchHistoryList}">
                    <tr>
                        <td colspan="5" style="border-right: none">You have not
                            performed any searches in this session</td>
                    </tr>
                    <tr style="height: 32px">
                        <td colspan="5" align="right" style="border-right: none">
                        <a href="/personal/savesearch/display.url?database=${actionBean.database}" title="View Saved Searches">View Saved Searches</a>
                        </td>
                    </tr>
                    </c:when>
                    <c:otherwise>
                        <c:set value="${fn:length(actionBean.searchHistoryList)+1}" var="listsize"/>
                        <c:forEach items="${actionBean.searchHistoryList}"
                            var="searchhistory" varStatus="status">
                            <tr>
                                <!-- No. -->
                                <td style="white-space: nowrap">
                                    <div style="float:left">${searchhistory.serialnumber}.&nbsp;&nbsp;</div>
                                    <div class="combineselect_boss" num="${searchhistory.serialnumber}" dbmask="${searchhistory.databasemask}">
										<div class="combineselect">
										  <input type="checkbox" class="combineselect_default" action="start"/>
										  <img id="combineselect_toggle_${searchhistory.serialnumber}" src="/static/images/down.jpg" class="combineselect_toggle closed"/>
										</div>
										<ul class="combineselect_menu horizlist" id="combineselect_menu_${listsize - searchhistory.serialnumber}"><li class="combineselect_action" action="start"><span>Start</span></li></ul>
									</div>
									<span class="clear"></span>
                                </td>

                                <!-- Search -->
                                <td style="padding-top: 5px">

                                    <div style="line-height: 20px; font-weight: bold;" title="${fn:replace(searchhistory.displayquery,'"','&quot;')}">
                                    <c:choose>
                                      <c:when test="${fn:length(searchhistory.displayquery) le  120}">
                                        ${searchhistory.displayquery}
                                      </c:when>
                                      <c:otherwise>${fn:substring(searchhistory.displayquery, 0, 120)} ...
                                      </c:otherwise>
                                    </c:choose>
                                    </div>

                                    <ul class="querytools horizlist">
                                    <li class="details">
		                                <a href="#" class="historydetailslink" title="Query details">Query details</a>
	                                    <div class="historydetails">
	                                       <div class="close"></div>
	                                        <ul>
	                                            <li><b>Type:</b> ${searchhistory.searchtype}</li>
	                                            <li><b>Years:</b> <c:choose><c:when test="${empty searchhistory.lastFourUpdates}">${searchhistory.years}</c:when><c:otherwise>Last ${searchhistory.lastFourUpdates} Updates</c:otherwise></c:choose></li>
	                                            <li><b>Sort:</b> ${searchhistory.sort.prettyfield} <c:if test="${not (searchhistory.sort.field  eq 'relevance')}">(${searchhistory.sort.prettydir})</c:if></li>
	                                            <li><b>Autostemming:</b> ${searchhistory.autostemming eq 'on' ? "On" : "Off"}</li>
	                                        </ul>
	                                        <div class="clear"></div>
	                                      <c:if test="${not empty searchhistory.dedupCriterias}">
	                                       <div class="deduptitle">Deduplicated Sets</div>
	                                       <div class="dedupbox">
	                                            <c:forEach var="dedupSearch" items="${searchhistory.dedupCriterias}" varStatus="dupStatus">
	                                            <div class="dedupitem">
	                                            <div class="dedupdb">${dedupSearch.dbPrefDisplayName}&nbsp;:</div>
	                                                <c:choose>
	                                                    <c:when test="${dedupSearch.fieldPrefDisplayName eq 'No Field'}">
	                                                      <c:set var="fieldPrefDisplayName" value="No Preference"/>
	                                                    </c:when>
	                                                    <c:otherwise>
	                                                      <c:set var="fieldPrefDisplayName" value="${dedupSearch.fieldPrefDisplayName}"/>
	                                                    </c:otherwise>
	                                                </c:choose>
	                                                <a href="/search/results/dedup.url?CID=dedup&amp;SEARCHID=${searchhistory.queryid}&amp;COUNT=1&amp;database=${searchhistory.databasemask}&amp;origin=history&amp;fieldpref=${dedupSearch.fieldPref}&amp;dbpref=${dedupSearch.dbPrefQueryString}" title="Use ${dedupSearch.dbPrefDisplayName} to search ${fieldPrefDisplayName} for this query">${fieldPrefDisplayName}</a>
	                                            </div>
	                                            <span class="clear"></span>
	                                            </c:forEach>
	                                       </div>
	                                      </c:if>
	                                    </div>

                                    </li>
                                    <li class="edit">
									<c:set var="editurl"><c:choose><c:when test="${searchhistory.searchtype eq 'Thesaurus'}">/search/thesHome.url</c:when><c:when test="${searchhistory.searchtype eq 'Book'}">/search/ebook.url</c:when><c:when test="${searchhistory.searchtype eq 'Quick'}">/search/quick.url</c:when><c:otherwise>/search/expert.url</c:otherwise></c:choose></c:set>
                                    <a title="Edit query" aria-labelledby="id of the container of the query string" href="${editurl}?searchid=${searchhistory.queryid}&database=${searchhistory.databasemask}<c:if test="${searchhistory.searchtype eq 'Thesaurus'}">#init</c:if>">Edit</a>
                                    </li>
                                    <%-- [TMH] changed backurl to constant --%>
			                        <li class="${searchhistory.savedsearch ? 'savedsearch' : 'savesearch'}">
			                            <a href="/personal/savedsearch/adddelete.url?history=true&option=SavedSearch&searchid=${searchhistory.queryid}&database=${searchhistory.databasemask}&backurl=SEARCHHISTORY&selectvalue=${searchhistory.savedsearch ? 'unmark' : 'mark'}"
			                                class="savesearch" id="savesearch${searchhistory.serialnumber}" num="${searchhistory.serialnumber}"
			                                loggedin="${actionBean.context.userLoggedIn}" selectvalue="${searchhistory.savedsearch ? 'unmark' : 'mark'}"
			                                title="${searchhistory.savedsearch ? "Remove your saved search query" : "Save this search query"}">${searchhistory.savedsearch ? "Remove Search" : "Save Search"}</a>
			                        </li>
			                        <li class="${searchhistory.emailalert ? 'createdalert' : 'createalert'}">
			                            <a href="/personal/savedsearch/adddelete.url?history=true&selectvalue=${searchhistory.emailalert ? 'unmark' : 'mark'}&option=EmailAlert&searchid=${searchhistory.queryid}&database=${searchhistory.databasemask}&backurl=SEARCHHISTORY"
			                                class="emailalert" id="emailalert${searchhistory.serialnumber}" num="${searchhistory.serialnumber}"
			                                loggedin="${actionBean.context.userLoggedIn}" selectvalue="${searchhistory.emailalert ? 'unmark' : 'mark'}"
			                                title="${searchhistory.emailalert ? "Remove alert for this search query" : "Add Email alert for this search query"}">${searchhistory.emailalert ? "Remove Alert" : "Create Alert"}</a>
			                        </li>
                                    </ul>

                                    <span class="clear"></span>

                                </td>

                                <c:set var="hisrerunactionurl">
									<c:choose>
										<c:when test="${searchhistory.searchtype eq 'Thesaurus'}">/search/results/thes.url</c:when>
										<c:when test="${searchhistory.searchtype eq 'Book'}">/search/results/quick.url</c:when>
										<c:when test="${searchhistory.searchtype eq 'Combined'}">/search/results/expert.url</c:when>
										<c:when test="${searchhistory.searchtype eq 'Quick'}">/search/results/quick.url</c:when>
										<c:when test="${searchhistory.searchtype eq 'Easy'}">/search/results/quick.url</c:when>
										<c:when test="${searchhistory.searchtype eq 'Tags'}">/search/results/tags.url</c:when>
										<c:when test="${searchhistory.searchtype eq 'Expert'}">/search/results/expert.url</c:when>
										<c:otherwise>/search/results/expert.url</c:otherwise>
									</c:choose>
								</c:set>
                                <!-- Results -->
                                <td style="text-align:center">
                                <c:choose><c:when test="${searchhistory.resultscount eq -1}">0</c:when><c:otherwise><a title="${searchhistory.displayquery}" href="${hisrerunactionurl}?CID=${searchhistory.searchCID}&SEARCHID=${searchhistory.queryid}&navigator=PREV&COUNT=1&database=${searchhistory.databasemask}"><fmt:formatNumber value="${searchhistory.resultscount}"/></a></c:otherwise></c:choose><br/>
                                </td>

                                <!-- Database(s) -->
                                <td>
                                <span title="${searchhistory.databasedisplay}">
                                    <c:choose>
                                        <c:when test="${fn:length(searchhistory.databasedisplay) le  30}">
                                          ${searchhistory.databasedisplay}
                                      </c:when>
                                      <c:otherwise>${fn:substring(searchhistory.databasedisplay, 0, 30)} ...
                                      </c:otherwise>
                                    </c:choose>
                                </span>
                                </td>

			                    <td style="border-right: none;text-align:center;">
			                    <a href="/search/history/clear.url?searchid=${searchhistory.queryid}&backurl=SEARCHHISTORY" class="historydelete" title="Delete" aria-labelledby="id of the container of the query string"><img src="/static/images/Delete.png" alt="delete"/></a></li>
			                    </td>
                </tr>
                </c:forEach>
                <tr>
                    <td colspan="2" style="border-right: none; line-height: 20px">
                        <input type="button" name="clearsearchistory" id="clearsearchhistory" value="Delete Search History" title="Delete Search History" sessionid="${actionBean.sessionid}"/>
                  </td>
                  <td colspan="3" style="white-space:nowrap; text-align:right; border-right: none; line-height: 20px">
                        <a href="/personal/savesearch/display.url?database=${actionBean.database}" title="View Saved Searches">View Saved Searches</a>
                  </td>
                </tr>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
    </div>

    <div id="historyhide" style="display: none; padding-top: 5px;border:1px solid #ABABAB">
        <p>The Search history has been closed.</p>
        <p>
            To view Search history, click the <b>Show</b> link.
        </p>
    </div>

</div>
</div>
<div class="message"><b>Note: </b>This Search history will contain the latest 50 searches you perform in this session.</div>
