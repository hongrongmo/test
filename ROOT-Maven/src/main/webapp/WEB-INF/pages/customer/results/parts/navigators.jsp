<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" session="false"%>
<%@ taglib uri="http://jawr.net/tags" prefix="jwr" %>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld"	prefix="stripes"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<div id="facetcol"  role="complementary" aria-label="Refine Results box">
	<form name="facet" id="facetform" method="POST"
		action="/search/results/expert.url?CID=expertSearchCitationFormat&database=${actionBean.database}&RERUN=${actionBean.searchid}">
		<input type="hidden" name="sort" value="${actionBean.sort}" />
		<input type="hidden" name="sortdir" value="${actionBean.sortdir}" />
		<input type="hidden" name="resultsorall" value="results" id="rdRes" />
		<input type="hidden" name="lastRefineStep" value="${fn:length(actionBean.refinequery)}" id="lastRefineStep" />
		<input type="hidden" name="sortClick" value="false" id="sortClick" />

		<stripes:errors field="errorcode"><div style="width:240px;"><stripes:individual-error/></div></stripes:errors>
		<div class="shadowbox">
		<div id="facet_refine">
			<h2 class="heading" style="margin: 0; padding-top: 5px">
			<c:choose>
				<c:when test="${actionBean.searchtype == 'Book'}">
					<a	href="${actionBean.helpUrl}#refine_eBook_srch_rslts.htm"  title="Learn more about refining your eBook search" class="helpurl">
				<jwr:img src="/static/images/i.png" border="0" styleClass="infoimg" align="top" style="float:right" alt="Learn more about refining your eBook search"/></a>
				</c:when>
				<c:when test="${actionBean.showpatentshelp}">
					<a	href="${actionBean.helpUrl}#Refine_patent_search.htm"  title="Learn more about refining your search" class="helpurl">
				<jwr:img src="/static/images/i.png" border="0" styleClass="infoimg" align="top" style="float:right" alt="Learn more about refining your search"/></a>
				</c:when>
				<c:otherwise>
				   <a	href="${actionBean.helpUrl}#${param.display eq 'patent'?'Refine_patent_search.htm':'Refine_search.htm'}"  title="Learn more about refining your ${param.display eq 'patent'?'patent':''} search" class="helpurl">
				<jwr:img src="/static/images/i.png" border="0" styleClass="infoimg" align="top" style="float:right" alt="Learn more about refining your ${param.display eq 'patent'?'patent':''} search"/></a>
				</c:otherwise></c:choose><span><b>${param.display eq 'patent'?'Find similar patents':'Refine results'}</b></span>
			</h2>
			<p class="buttons" style="*padding-bottom:3px">
				<span id="limitto"><input type="submit" value="Limit to" title="Limit search results by your selections" id="limittobtn"/></span>
				<c:if test="${param.display ne 'patent'}"><input type="submit" value="Exclude" name="exclude" value="Exclude" title="Exclude your selections from your search results" /></c:if>
			</p>
		</div>

		<div id="facet_append" class="facetbox" aria-label="Add a term" role="search" field="append" isopen="<c:if test="${!actionBean.appendopen}">-</c:if>5" >
			<div class="facetheader" style="border-bottom: none">
				<div class="facetheader_links">
					<a class="facetshowhide" field="append"
						title="${actionBean.appendopen ? 'Close' : 'Open'}">
						<c:choose>
							<c:when test="${!actionBean.appendopen}">
								<jwr:img src="/static/images/facet_down.png"
									styleClass="facetupdown" border="0" alt="facet down arrow"/>
							</c:when>
							<c:otherwise>
								<jwr:img src="/static/images/facet_up.png"
									styleClass="facetupdown" border="0" alt="facet up arrow"/>
							</c:otherwise>
						</c:choose>
					</a>
				</div>
				<div class="facettitle facetshowhide" field="append" title="${actionBean.appendopen ? 'Close' : 'Open'}"><b>Add a term</b>&nbsp;&nbsp;</div>
				<div class="clear"></div>
			</div>
			<div class="facetentries"<c:if test="${!actionBean.appendopen}"> style="display:none"</c:if>>
				<label class="hidden" for="navaddterm">Add Term</label>
				<input type="text" id="navaddterm" title="Add a Term Text Box" name="topappend" style="width: 180px; padding: 0; margin: 0 0 3px 5px" value="<c:if test="${not empty actionBean.appendstr && 'top' eq actionBean.error}">${actionBean.appendstr}</c:if>" />
			</div>
		</div>
<c:if test="${fn:length(actionBean.navigators) > 0}">
		<div id="facetsortable">
		<c:forEach items="${actionBean.navigators}" var="navigator" varStatus="status">

		<c:set var="navopen" value="${navigator.open > -1}"/>
		<c:set var="navcount" value="${fn:length(navigator.items)}"/>

		<div id="facet_${navigator.field}" name="facet_item" class="facetbox" order="${status.count}" isopen="${navigator.open}" total="${navigator.totalSize}" more="${navigator.more}" less="${navigator.less}" field="${navigator.field}">
			<div class="facetheader">
				<h3 class="facettitle facetshowhide <c:if test="${navigator.field == 'cv'}">facet_cvHighlightFeature</c:if>" field="${navigator.field}" title="${navopen ? 'Close' : 'Open'}"><b>${navigator.label}</b>&nbsp;&nbsp; </h3>
				<div class="facetheader_links">
				<c:if test="${actionBean.isnavchrt}">
					<a title="View chart" class="viewChart" field="${navigator.label}" href=""
						onclick="window.open('/search/results/analyzenav.url?searchid=${actionBean.searchid}&database=${actionBean.database}&field=${navigator.field}','newwindow','width=600,height=630,toolbar=no,location=no,scrollbars,resizable');return false"><jwr:img
						src="/static/images/Graph.png" styleId="graph" border="0" alt="View Chart"/></a>
					<a title="Download data"
						href="/controller/servlet/Controller?CID=downloadNavigatorCSV&searchid=${actionBean.searchid}&database=${actionBean.database}&nav=${navigator.field}nav"><jwr:img
						src="/static/images/Data.png" styleId="data" border="0" alt="Download Data"/></a>
				</c:if>
					<a class="facetshowhide" field="${navigator.field}"
						title="${navopen ? 'Close' : 'Open'}"> <c:choose>
							<c:when test="${!navopen}">
								<jwr:img src="/static/images/facet_down.png"
									styleClass="facetupdown" border="0" alt="facet down arrow"/>
							</c:when>
							<c:otherwise>
								<jwr:img src="/static/images/facet_up.png"
									styleClass="facetupdown" border="0" alt="facet up arrow"/>
							</c:otherwise>
						</c:choose>
					</a>
				</div>
				<div class="clear"></div>
			</div>

			<div class="facetentries"<c:if test="${!navopen}"> style="display:none"</c:if>>
			<fieldset>
			<table style="border-top: 1px solid #adaaad; padding-top:3px">

				<c:forEach items="${navigator.items}" var="item" varStatus="itemstatus"  end="10">
				<tr class="facetentry<c:if test="${itemstatus.count > 5}"> facetbottom5</c:if>"<c:if test="${(navigator.open <= 5 && navigator.open >= -5) && itemstatus.count > 5}"> style="display:none"</c:if>>
					<td class="facetentry_ck">
						<input type="checkbox" name="${navigator.name}" class="navCheck"
	                         value="${item.count}~${item.value}~${item.label}"
	                         id="${navigator.name}${itemstatus.count}" />
	                 </td>
					<td class="facetentry_label">
					<c:choose>
					<c:when test="${fn:length(item.label) le  80}"><label for="${navigator.name}${itemstatus.count}">${item.label}</label></c:when>
					<c:otherwise><label for="${navigator.name}${itemstatus.count}">${fn:substring(item.label, 0, 79)}
										<span style="cursor: pointer" title="${item.label}">...</span></label>
					</c:otherwise>
					</c:choose>
					</td>
					<td class="facetentry_count">(${item.count})</td>
				</tr>
				</c:forEach>
			</table>
			</fieldset>
			<c:if test="${navcount > 5}">
			<div class="facet_moreless">
				<a class="showmore" aria-labelledby="facet_${navigator.field}" field="${navigator.field}"<c:if test="${((navigator.open > 5 or navigator.open < -5) and navcount > 5 and navigator.more < 10)}"> style="display:none"</c:if>
			          href="#" searchId="${actionBean.searchid}" >
		          View more
		        </a>
		        <%-- Show separator link only when more & less both available --%>
		        <span class="showlessSeprator"<c:if test="${!((navigator.open > 5 or navigator.open < -5) and navigator.more > 10)}"> style="display:none"</c:if>>
		          &nbsp;&nbsp;|&nbsp;&nbsp;
		        </span>
		        <%-- Show "less" link when less count > 0 --%>
		        <a class="showless" more="${navigator.more}" less="${navigator.less}" field="${navigator.field}"<c:if test="${(navigator.open < 10 and navigator.open > -10)}"> style="display:none"</c:if>
		         href="${rerunactionurl}?CID=${actionBean.CID}&navigator=MORE&FIELD=${navigator.field}:${navigator.less}&SEARCHID=${actionBean.searchid}&database=${actionBean.database}&docid=${actionBean.docid}">
		         View fewer
		        </a>
		    </div>
			</c:if>
			</div>
		</div>
		<div id="facet_overlay_${navigator.field}" field="${navigator.field}" searchId="${actionBean.searchid}" class="facet_overlay_box" style="display:none;">
			<div class="facet_overlay_header">
				<h3 class="facettitle facetshowhide" field="${navigator.field}" title="Close"><b>${navigator.label}</b>&nbsp;&nbsp;</h3>
				<div class="facetheader_links">
					<c:if test="${actionBean.isnavchrt}">
						<a title="View chart" href="" onclick="window.open('/search/results/analyzenav.url?searchid=${actionBean.searchid}&database=${actionBean.database}&field=${navigator.field}','newwindow','width=600,height=630,toolbar=no,location=no,scrollbars,resizable');return false"><jwr:img	src="/static/images/Graph.png" styleId="graph" border="0" alt="View Chart"/></a>
						<a title="Download data" href="/controller/servlet/Controller?CID=downloadNavigatorCSV&searchid=${actionBean.searchid}&database=${actionBean.database}&nav=${navigator.field}nav"><jwr:img	src="/static/images/Data.png" styleId="data" border="0" alt="Download Data"/></a>
					</c:if>
					<a class="facet_overlay_showhide" field="${navigator.field}" searchId="${actionBean.searchid}" title="Close">
						<jwr:img src="/static/images/facet_left_arrow.png" styleClass="facetupleft" border="0" alt="facet left arrow"/>
					</a>
				</div>
				<div class="clear"></div>
			</div>
			<div id="facet_overlay_entries_${navigator.field}" class="facet_overlay_entries">

				<!-- where the facets that will come from an ajax call will go-->

			</div>
			<div class="overlay_footer">
				<div class="facet_moreless_overlay">
					<a class="showmore_overlay" id="showmore_overlay_${navigator.field}" aria-labelledby="facet_${navigator.field}" field="${navigator.field}" total="${navigator.totalSize}" searchId="${actionBean.searchid}" href="#" >
			          View more
			        </a>
			        <%-- Show separator link only when more & less both available --%>
			        <span class="showlessSeprator" id="showlessSeprator_${navigator.field}" >&nbsp;&nbsp;|&nbsp;&nbsp;</span>
			        <%-- Show "less" link when less count > 0 --%>
			        <a class="showless_overlay"id="showless_overlay_${navigator.field}" field="${navigator.field}"  searchId="${actionBean.searchid}" href="#" >View fewer</a>
			    </div>
			    <div class="alignTextCenter" style="padding-top:5px;*padding-bottom:3px">
						<input type="submit" value="Limit to" title="Limit search results by your selections" />
						<c:if test="${param.display ne 'patent'}"><input type="submit" value="Exclude" name="exclude" value="Exclude" title="Exclude your selections from your search results" /></c:if>
				</div>
				<div class="clear"></div>
			</div>
		</div>
		</c:forEach>
		</div>
</c:if>
		</div> <!-- END SHADOWBOX -->
	</form>
<c:if test="${param.display ne 'patent'}">
	<div id="facetsearch" class="shadowbox">
	<form name="facetsearchform" id="facetsearchform" method="POST" action="/search/results/expert.url" aria-label="Add a term and search with facets" role="search">
		<input type="hidden" name="CID" value="expertSearchCitationFormat"/>
		<input type="hidden" name="RERUN" value="${actionBean.searchid}"/>
		<input type="hidden" name="resultsorall" value="all"/>
		<input type="hidden" name="sort" value="relevance"/>
		<input type="hidden" name="yearselect" value="${actionBean.yearselect}"/>
		<input type="hidden" name="updatesNo" value="${actionBean.updatesNo}"/>
		<input type="hidden" name="statYear" value="${actionBean.startYear}"/>
		<input type="hidden" name="endYear" value="${actionBean.endYear}"/>
		<input type="hidden" name="database" value="${actionBean.database}"/>
		<input type="hidden" name="autostem" value="${actionBean.autostem}"/>
		<p style="margin: 0">
			<b>Run new search with selected <br/>facets <a	href="${actionBean.helpUrl}#Refine_search.htm#run_new" class="helpurl"  title="Learn more about how to run a new search with facets" alt="Learn more about how to run a new search with facets" >
				<jwr:img src="/static/images/i.png" border="0" styleClass="infoimg" alt="Learn more about how to run a new search with facets" align="bottom"/></a></b>
		</p>
		<p class="inputs">
			<label class="hidden" for="facetsearchterm">Search Selected Facets</label>
			<input type="text" name="btmappend" id="facetsearchterm" title="Selected Facets Search Text Box"  value="<c:if test="${not empty actionBean.appendstr && 'btm' eq actionBean.error}">${actionBean.appendstr}</c:if>"></input>
			<input type="submit" value="Search" title="Run new search with selected facets"></input>
		</p>
	</form>
	</div>
</c:if>
<c:if test="${ actionBean.context.userSession.user.userPreferences.knovelSearchButton}">
	<div id="knovelcol" >
		<div class="knovelsearch shadowbox" >
		<div style="float:left;">
		<img src="/static/images/knovel_k_logo.png" style="vertical-align:top;padding-top:2px;" height="20px;"/>
		<input type="button" id="knovelSearchSubmit" value="Knovel Search" title="Run this search in Knovel" style="margin-left:2px;"/>
		</div>
		<a	href="${actionBean.helpUrl}#knovel_search.htm" class="helpurl"  title="Learn more about Knovel" style="float:right;padding-top:4px;" >
			<jwr:img src="/static/images/i.png" border="0" styleClass="infoimg" alt="Learn more about Knovel" align="bottom"/>
		</a>
		</div>
	</div>
</c:if>
</div>


<script>
var checkNavigatorForCV = false;
<c:if test="${actionBean.context.userSession.user.userPreferences.autocomplete && actionBean.context.userSession.autoCompleteEnabled}">
	<c:if test="${actionBean.searchtype == 'Quick'}">
		checkNavigatorForCV = true;
	</c:if>
</c:if>
</script>
