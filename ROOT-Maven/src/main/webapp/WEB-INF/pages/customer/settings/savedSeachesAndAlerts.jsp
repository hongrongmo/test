<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld"
	prefix="stripes"%>

<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp"
	pageTitle="Engineering Village - Alerts & Saved Searches">

	<stripes:layout-component name="csshead">
	<link href="/static/css/ev_personalaccount.css?v=${releaseversion}" media="all" type="text/css" rel="stylesheet"></link>
	</stripes:layout-component>

	<stripes:layout-component name="contents">

		<div id="savedsearchcontents">
			<h1>Alerts &amp; Saved Searches</h1>
		
		<hr class="divider">
		<div style="padding-top:15px;"></div>
		
		<form method="POST" action="/personal/savesearch/display.url" name="savedSearches" id="savedSearches">
			<table id="savedsearchtable" class="savedsearchtable" cellpadding="0" cellspacing="0" border="0">
				<thead>
                    <tr>
                      <th colspan="10" style="font-size: 18px;" class="heading first"><h2 style="font-size: 18px">My Saved Searches</h2></th>
                    </tr>				
					<tr class="headingcols">
						<th class="first" style="width: 52px">No.</th>
						<th>Type</th>
						<th style="width: 240px; text-align: left">Search</th>
						<th>Auto-<br />stem</th>
						<th>Sort</th>
						<th>Results</th>
						<th>Year(s)</th>
						<th style="width:72px">Database</th>
						<th>Date<br />Saved</th>
						<th>Add<br/>Email Alert</th>
					</tr>
				</thead>
				<tbody>
					<c:choose>
						<c:when test="${actionBean.savedSeachesCount == 0}">
							<tr>
								<td colspan="10" class="first">You have no saved searches.</td>
							</tr>
						</c:when>
						<c:otherwise>
 						  <input type="hidden" name="database" value="${actionBean.database}"/>
							<c:forEach items="${actionBean.savedSearches}" var="search" varStatus="status">
							  <c:if test="${not(search.emailalert)}">
								<tr>
									<!-- No. -->
									<td class="first">${status.count}.<br /> 
										<a href="/personal/savesearch/display.url?DeleteSearch=t&searchid=${search.queryid}" onclick="return confirm('Are you sure you want to delete this saved search?');">
											<img src="/static/images/Delete.gif" border="0" title="Delete this search" />
									    </a>
									 </td>

									<!-- Type -->
									<td>${search.searchtype}</td>

									<!-- Search -->
									<td style="text-align: left">
											<c:choose>
												<c:when test="${fn:length(search.displayquery) le  200}">
												  <c:set var="displayquery" value="${search.displayquery}"/>
											  </c:when>
											  <c:otherwise>
											  <c:set var="displayquery" value="${fn:substring(search.displayquery, 0, 200)} ..."/>
											  </c:otherwise>
											</c:choose>
											<c:choose>	
												<c:when test="${search.databasemask eq '8'}">
													<a href="/controller/servlet/Controller?CID=uspto${search.searchtype}&database=${search.databasemask}&location=SAVEDSEARCH&SEARCHID=${search.queryid}" title="${search.displayquery}">${displayquery}</a>
												</c:when>
												<c:when test="${search.databasemask eq '16'}">
													<a href="/controller/servlet/Controller?CID=engnetbase${search.searchtype}&database=${search.databasemask}&location=SAVEDSEARCH&SEARCHID=${search.queryid}" title="${search.displayquery}">${displayquery}</a>
												</c:when>
												<c:otherwise>
													<a href="/search/results<c:choose><c:when test="${'Expert' eq search.searchtype}">/expert</c:when><c:otherwise>/quick</c:otherwise></c:choose>.url?CID=${search.searchCID}&location=SAVEDSEARCH&SEARCHID=${search.queryid}&navigator=PREV&COUNT=1&database=${search.databasemask}" title="${search.displayquery}">${displayquery}</A>
												</c:otherwise>
											</c:choose>
											</td>

									<!-- Autostem -->
									<td>
										<c:choose>
											<c:when test="${search.databasemask eq '8'}">
												<!-- USPTO - do nothing / no stemming option-->
											</c:when>
											<c:when test="${search.searchtype eq 'Thesaurus'}">
												<span></span>
											</c:when>
											<c:when test="${search.autostemming eq 'on'}">
												<span>On</span>
											</c:when>
											<c:when test="${search.autostemming eq 'off'}">
												<span>Off</span>
											</c:when>
										</c:choose>
									</td>

									<!-- Sort -->
									<td>${search.sort.prettyfield}<br/><c:if test="${not (search.sort.field  eq 'relevance')}">(${search.sort.prettydir})</c:if></td>
									

									<!-- Results -->
									<td><fmt:formatNumber value="${search.resultscount}" />&nbsp;</td>

									<!-- Year(s) -->
									<td style="white-space: nowrap"><c:choose>
											<c:when
												test="${(search.databasemask eq '16') or (search.databasemask eq '8')}">
												<span>-</span>
											</c:when>
											<c:when test="${not empty search.emailAlertWeek}">
												<span>Week&nbsp;${search.emailAlertWeek}</span>
											</c:when>

											<c:when test="${not empty search.lastFourUpdates}">
												<span>Last&nbsp;${search.lastFourUpdates}&nbsp;update(s)</span>
											</c:when>
											<c:otherwise>
												<span>${search.startYear}-${search.endYear}</span>
											</c:otherwise>
										</c:choose></td>

									<!-- Database(s) -->
									<td>
										<span title="${search.databaseDisplayName}">
											<c:choose>
												<c:when test="${fn:length(search.databaseDisplayName) le  30}">
												  ${search.databaseDisplayName}
											  </c:when>
											  <c:otherwise>${fn:substring(search.databaseDisplayName, 0, 30)} ...
											  </c:otherwise>
											</c:choose>
										</span>
									</td>

									<!-- Save search -->
									 <td>
										${search.savedDate}
									 </td>
									 
									<!-- Email Alert -->
									<td style="text-align: center">
										<c:if test="${actionBean.emailAlertsPresent}">
						                      <c:choose>
														<c:when test="${(search.databasemask eq '16') or (search.databasemask eq '8')}">
															<input type="hidden" name="emailalert" />
														</c:when>
														<c:when test="${not ((search.databasemask eq '16') or (search.databasemask eq '8'))}">
															<input type="checkbox" name="alertcbx" value="${search.queryid}" <c:if test="${search.emailalert}">checked="checked"</c:if> />
														</c:when> 
														<c:otherwise>&#160;<input type="hidden" name="emailalert" />
														</c:otherwise>
												</c:choose>
												<input type="hidden" name="savedsearch-id" value="${search.queryid}"></input>
										  </c:if>
					                </td>
					        		</tr>
					       		</c:if> 
							</c:forEach>
							<tr>
								<td colspan="10" style="height: 30px" class="first">
									<c:if test="${actionBean.savedSeachesCount > 0}">
										<input type="submit" name="DeleteAllSearches" value="Delete All" title="Delete all saved searches" style="float:left;" class="button" onclick="return confirmClear('savedSearches');"></input>
										<input type="submit" name="AddAlerts" id="AddAlerts" value="Save Email Alerts" title="Save Email Alerts" style="float:right; margin-right:0px" class="button"></input>
									</c:if>
								</td>
							</tr>
					</c:otherwise>
					</c:choose>
				</tbody>
			</table>
		</form>
			
			<br/>
			
		<form method="POST" action="/personal/savesearch/display.url" name="savedAlerts" id="savedAlerts">
			<table id="alerttable" class="savedsearchtable" cellpadding="0" cellspacing="0" <c:if test="${not (actionBean.emailAlertsPresent)}">style="width: 720px;"</c:if>>
				<thead>
					<tr>
                      <th style="font-size:18px;" class="heading first"<c:choose><c:when test="${actionBean.emailAlertsPresent}"> colspan="11" </c:when><c:otherwise>colspan="10"</c:otherwise></c:choose>><h2 style="font-size: 18px">My Email Alerts</h2></th>
                    </tr>
					<tr class="headingcols">
						<th class="first" style="width: 52px">No.</th>
						<th>Type</th>
						<th style="width: 240px; text-align: left">Search</th>
						<th>Auto-<br />stem</th>
						<th>Sort</th>
						<th>Results</th>
						<th>Year(s)</th>
						<th style="width:72px">Database</th>
						<th>Date<br />Saved</th>
						<th>Clear<br/>Email Alert</th>
						<c:if test="${actionBean.emailAlertsPresent}">
							<th>Add<br/>Recipients</th>
						</c:if>	
					</tr>
				</thead>
				<tbody>
					<c:choose>
						<c:when test="${actionBean.savedAlertsCount == 0}">
							<tr>
								<td class="first" colspan="11">You have no email alerts.</td>
							</tr>
						</c:when>
						<c:otherwise>
						    <input type="hidden" name="database" value="${actionBean.database}"/>
							<c:forEach items="${actionBean.savedAlerts}" var="search" varStatus="status">
							  <c:if test="${search.emailalert}">
								<tr>
									<!-- No. -->
									<td class="first">${status.count}.<br /> 
										<a href="/personal/savesearch/display.url?DeleteSearch=t&searchid=${search.queryid}" onclick="return confirm('Are you sure you want to delete this alert?')">
											<img src="/static/images/Delete.gif" border="0" title="Delete this alert"/>
									    </a>
									</td>

									<!-- Type -->
									<td>${search.searchtype}</td>

									<!-- Search -->
									<td style="text-align: left;">
										<c:choose>
												<c:when test="${fn:length(search.displayquery) le  200}">
												  <c:set var="displayquery" value="${search.displayquery}"/>
											  </c:when>
											  <c:otherwise>
											  <c:set var="displayquery" value="${fn:substring(search.displayquery, 0, 200)} ..."/>
											  </c:otherwise>
											</c:choose>
										<c:choose>
											<c:when test="${search.databasemask eq '8'}">
												<a href="/controller/servlet/Controller?CID=uspto${search.sSearchType}&database=${search.databasemask}&location=SAVEDSEARCH&SEARCHID=${search.queryid}" title="${search.displayquery}">${displayquery}</a>
											</c:when>
											<c:when test="${search.databasemask eq '16'}">
												<a href="/controller/servlet/Controller?CID=engnetbase${search.sSearchType}&database=${search.databasemask}&location=SAVEDSEARCH&SEARCHID=${search.queryid}" title="${search.displayquery}">${displayquery}</a>
											</c:when>
											<c:otherwise>
												<a href="/controller/servlet/Controller?CID=${search.searchCID}&location=SAVEDSEARCH&SEARCHID=${search.queryid}&navigator=PREV&COUNT=1&database=${search.databasemask}" title="${search.displayquery}">${displayquery}</A>
											</c:otherwise>
										</c:choose></td>

									<!-- Autostem -->
									<td><c:choose>
											<c:when test="${search.databasemask eq '8'}">
												<!-- USPTO - do nothing / no stemming option-->
											</c:when>
											<c:when test="${search.searchtype eq 'Thesaurus'}">
												<span></span>
											</c:when>
											<c:when test="${search.autostemming eq 'on'}">
												<span>On</span>
											</c:when>
											<c:when test="${search.autostemming eq 'off'}">
												<span>Off</span>
											</c:when>
										</c:choose></td>

									<!-- Sort -->
									<td>${search.sort.prettyfield}<br/><c:if test="${not (search.sort.field  eq 'relevance')}">(${search.sort.prettydir})</c:if></td>

									<!-- Results -->
									<td><fmt:formatNumber value="${search.resultscount}" />&nbsp;</td>

									<!-- Year(s) -->
									<td style="white-space: nowrap"><c:choose>
											<c:when
												test="${(search.databasemask eq '16') or (search.databasemask eq '8')}">
												<span>-</span>
											</c:when>
											<c:when test="${not empty search.emailAlertWeek}">
												<span>Week&nbsp;${search.emailAlertWeek}</span>
											</c:when>

											<c:when test="${not empty search.lastFourUpdates}">
												<span>Last&nbsp;${search.lastFourUpdates}&nbsp;update(s)</span>
											</c:when>
											<c:otherwise>
												<span>${search.startYear}-${search.endYear}</span>
											</c:otherwise>
										</c:choose></td>

									<!-- Database(s) -->
									<td>
									<span title="${search.databaseDisplayName}">
										<c:choose>
											<c:when test="${fn:length(search.databaseDisplayName) le 30}">
											  ${search.databaseDisplayName}
										  </c:when>
										  <c:otherwise>${fn:substring(search.databaseDisplayName, 0, 30)} ...
										  </c:otherwise>
										</c:choose>
									</span>
									</td>

									<!-- Save search -->
									 <td>
										${search.savedDate}
									</td>
									
									<!-- Email Alert -->
									<td style="text-align: center">
										<c:if test="${actionBean.emailAlertsPresent}">
						                      <c:choose>
														<c:when
															test="${(search.databasemask eq '16') or (search.databasemask eq '8')}">
															<input type="hidden" name="emailalert" />
														</c:when>
														<c:when
															test="${not ((search.databasemask eq '16') or (search.databasemask eq '8'))}">
															<input type="checkbox" name="alertcbx" value="${search.queryid}" />
														</c:when> 
														<c:otherwise>&#160;<input type="hidden" name="emailalert" />
														</c:otherwise>
												</c:choose>
												<input type="hidden" name="savedsearch-id" value="${search.queryid}"></input>
										  </c:if>
					                </td>
									<c:if test="${actionBean.emailAlertsPresent}">
					                <td style="text-align: center">
						                      <c:choose>
														<c:when
															test="${(search.databasemask eq '16') or (search.databasemask eq '8')}">
															<input type="hidden" name="emailalert" />
														</c:when>
														<c:when
															test="${not ((search.databasemask eq '16') or (search.databasemask eq '8'))}">
															<c:if test="${actionBean.ccEmailAlertsPresent}">
															<c:if test="${search.emailalert}">
								                                <c:if test="${empty search.ccList}"><c:set var="ccimg">cco.gif</c:set></c:if>
																<c:if test="${not empty search.ccList}"><c:set var="ccimg">ccg.gif</c:set></c:if>
															</c:if>
																<a href="javascript:openCCWindow('editcclist','${search.sessionid}','${search.queryid}','${search.databasemask}')">
																	<img src="/static/images/${ccimg}" name="${search.queryid}" width="24" height="14" alt="Alert Cc: List" border="0" style="padding-bottom: 6px;" title="Add recipients to this email alert"></img>
																</a>
															</c:if>
														</c:when> 
														<c:otherwise>&#160;<input type="hidden" name="emailalert" />
														</c:otherwise>
												</c:choose>
												<input type="hidden" name="savedsearch-id" value="${search.queryid}"></input>
					                </td>
									</c:if>
		
					        	</tr>
					       	</c:if> 
						</c:forEach>
						<tr>
							<td class="first" colspan="11" style="height: 30px">
								<c:if test="${actionBean.savedAlertsCount > 0}">
									<input type="submit" name="DeleteAllAlerts" id="DeleteAllAlerts" value="Delete All" title="Delete all alerts" style="float:left;" class="button" onclick="return confirmClear('savedAlerts');"></input>
									<input type="submit" name="ClearAlerts"  id="DeleteAlerts" value="Clear Email Alerts" title="Clear Email Alerts, but retain as a Saved Search" style="float:right; margin-right:0px" class="button"></input>
								</c:if>
							</td>
						</tr>
					</c:otherwise>
					</c:choose>
				</tbody>
			</table>
		</form>
		</div>


	</stripes:layout-component>

	<stripes:layout-component name="jsbottom_custom">
		<SCRIPT type="text/javascript" SRC="/static/js/SavedSearchesAndAlerts.js?v=2"></SCRIPT>
	</stripes:layout-component>

</stripes:layout-render>
