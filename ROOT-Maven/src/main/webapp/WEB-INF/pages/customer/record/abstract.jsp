<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://jawr.net/tags" prefix="jwr" %>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld"	prefix="stripes"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/tlds/datatags.tld" prefix="data" %>

<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp">

	<stripes:layout-component name="csshead">
	<%@ include file="/WEB-INF/pages/include/highlight.jsp" %>
	<link href="/static/css/ev_abstract.css?v=${releaseversion}" media="all" type="text/css" rel="stylesheet"></link>
	<!--[if IE 6]>
	<link href="/static/css/ev_ie6.css?v=${releaseversion}" media="all" type="text/css" rel="stylesheet"></link>
	<![endif]-->
	</stripes:layout-component>
	<stripes:layout-component name="SkipToNavigation">
		<a class="skiptonavlink" href="#abstractTabLink" onclick="$('#abstractTabLink').focus();return false;" title="Skip to Abstract Tab">Skip to Abstract Tab</a><br/>
		<a class="skiptonavlink" href="#cbresult_${actionBean.results[0].doc.docid}" onclick="$('#cbresult_${actionBean.results[0].doc.docid}').focus();return false;" title="Skip to record">Skip to record</a><br/>
	</stripes:layout-component>

	<stripes:layout-component name="contents">
	<c:set var="rerunsearchurl" scope="request">
		<c:choose>
			<c:when test="${param.searchtype eq 'Thesaurus'}">/search/results/thes.url</c:when>
			<c:when test="${param.searchtype eq 'Book'}">/search/results/ebook.url</c:when>
			<c:when test="${param.searchtype eq 'Combined'}">/search/results/expert.url</c:when>
			<c:when test="${param.searchtype eq 'Quick'}">/search/results/quick.url</c:when>
			<c:when test="${param.searchtype eq 'Easy'}">/search/results/quick.url</c:when>
			<c:when test="${param.searchtype eq 'Tags'}">/search/results/tags.url</c:when>
			<c:when test="${param.searchtype eq 'Expert'}">/search/results/expert.url</c:when>
			<c:otherwise></c:otherwise>
		</c:choose>
	</c:set>
	<c:set var="searchhistoryurl">
		<c:choose>
			<c:when test="${param.searchtype eq 'Thesaurus'}">/search/thesHome.url</c:when>
			<c:when test="${param.searchtype eq 'Book'}">/search/ebook.url</c:when>
			<c:when test="${param.searchtype eq 'Quick'}">/search/quick.url</c:when>
			<c:otherwise>/search/expert.url</c:otherwise>
		</c:choose>
	</c:set>

	<c:set var="navigationurl">
		<c:choose>
			<c:when test="${actionBean.view eq 'abstract'}">/search/doc/abstract.url</c:when>
			<c:otherwise>/search/doc/detailed.url</c:otherwise>
		</c:choose>
	</c:set>

	<c:set var="result" value="${actionBean.results[0]}" scope="request"/>
	<span session-id="${actionBean.sessionid}" security="${result.citedby.md5}" an="${result.citedby.an}" doi="${result.citedby.doi}" page="${result.citedby.firstpage}" volume="${result.citedby.firstvolume}" issue="${result.citedby.firstissue}" issn="${result.citedby.issn}" isbn="${result.citedby.isbn}" isbn13="${result.citedby.isbn13}" style="display: none;" name="citedbyspan"></span>
	<div id="abstractbox">

<%-- *********************************************************** --%>
<%-- Top navbar - view search history, back to results and --%>
<%-- page navigation --%>
<%-- *********************************************************** --%>

        <c:choose>
        	<c:when test="${not empty actionBean.dedupResultCount && not(actionBean.dedupResultCount eq 'null') }">
        	 <c:set var="resultCount" value=" ${actionBean.dedupResultCount}"/>
        	</c:when>
        	<c:otherwise>
        		 <c:set var="resultCount" value="${actionBean.resultscount}"/>
        	</c:otherwise>
        </c:choose>

		<div id="abstractnavbar" aria-label="Results options" role="navigation">
            <span><a class="newsearch" href="/home.url?database=${actionBean.database}" title="Run new search">New Search</a>|</span>
            <span><a class="history" href="${searchhistoryurl}?${actionBean.newsearchqs}#searchhistorybox" title="View search history">View search history</a>|</span>
			<span><a class="backtoresults" href="${actionBean.backtoresultsLink}" title="Go back to search results">Back to results</a><c:if test="${empty actionBean.displayPagination or not(actionBean.displayPagination eq 'no') }">|</c:if></span>
			<c:if test="${not empty actionBean.dedupnavqs}">
			<span><a class="backtoresults" href="/search/results/dedup.url?${actionBean.dedupnavqs}&amp;linkResultCount=${actionBean.dedupResultCount}" title="Go back to deduplicated search results">Back to deduplicated results</a>|</span>
			</c:if>
			<c:if test="${empty actionBean.displayPagination or not(actionBean.displayPagination eq 'no') }">
			<span class="pagenav">
			<c:if test="${not empty actionBean.prevqs}"><a style="margin-right:0" href="${navigationurl}?${actionBean.prevqs}&amp;pageType=${actionBean.pageType}&amp;dedupResultCount=${actionBean.dedupResultCount}" title="Go to previous record">&lt; Previous</a></c:if>
				<b>${result.doc.hitindex} of ${resultCount}</b>
			<c:if test="${not empty actionBean.nextqs}"><a href="${navigationurl}?${actionBean.nextqs}&amp;pageType=${actionBean.pageType}&amp;dedupResultCount=${actionBean.dedupResultCount}" title="Go to next record">Next &gt;</a></c:if>
			</span>
			</c:if>
			<div class="clear"></div>
		</div>

		<div class="hr" style="height:5px; margin:0 10px"><hr/></div>

		<table id="abstractwrapper" border="0" cellpadding="0" cellspacing="0">
<%-- *********************************************************** --%>
<%-- Abstract LH column - abstract!                              --%>
<%-- *********************************************************** --%>
		<tr>
		<td id="lhcolumn">
<%-- *********************************************************** --%>
<%-- Abstract tools - full text, blog, email/print/download, etc.--%>
<%-- *********************************************************** --%>
		<div id="abstracttoolbar" aria-label="Record options" role="navigation">
	<c:if test="${result.fulltext}">
		<span><a class="fulltext" href="javascript:newwindow=window.open('/search/results/fulltext.url?docID=${result.doc.docid}','newwindow','width=500,height=500,toolbar=no,location=no,scrollbars,resizable'); void('');" title="View full text (opens in a new window)"><img id="ftimg" class="fulltext" src="/static/images/full_text.png" title="View full text (opens in a new window)"></a>|</span>
	</c:if>
		<c:if test="${actionBean.bloglink}">
			<span><a class="blog" id="bloglink" title="Create a link to share this record" href="/blog/open.url?mid=${result.doc.docid}&database=${result.doc.dbid}">Blog This</a>|</span>
		</c:if>
			<span><a class="email" id="emaillink" title="Email this record" href="/delivery/email/display.url?database=${actionBean.database}&displayformat=${actionBean.view}&docidlist=${result.doc.docid}&handlelist=${result.doc.hitindex}">Email</a>|</span>
			<span><a class="print" id="printlink" title="Print this record" href="/delivery/print/display.url?database=${actionBean.database}&displayformat=${actionBean.view}&docidlist=${result.doc.docid}&handlelist=${result.doc.hitindex}">Print</a>|</span>
			<span id="downloadli" class="downloadSpan"><a id="oneclickDL" title="Download selections" style="display:none" href="/delivery/download/display.url?database=${actionBean.database}&displayformat=${actionBean.view}&docidlist=${result.doc.docid}&handlelist=${result.doc.hitindex}"></a><a id="downloadlink" title="Download this record" href="/delivery/download/display.url?database=${actionBean.database}&displayformat=${actionBean.view}&docidlist=${result.doc.docid}&handlelist=${result.doc.hitindex}">Download</a>|</span>
			<span><a class="save" title="Save this record to a folder in Settings" href="/personal/folders/save/view.url?CID=viewSavedFolders&searchid=${actionBean.searchid}&EISESSION=${actionBean.sessionid}&count=1&source=selectedset&database=${actionBean.database}&format=${actionBean.view=='detailed'?'quickSearchDetailedFormat':'quickSearchAbstractFormat'}&DOCINDEX=${result.doc.hitindex}&backurl=SAVETOFOLDER">Save to Folder</a></span>
			<div class="clear"></div>
		</div>

		<div class="hr" style="color: #9b9b9b; background-color: #9b9b9b; height: 1px; margin: 0 0 7px 0;"><hr></div>


<%-- *********************************************************** --%>
<%-- Abstract display --%>
<%-- *********************************************************** --%>

			<div id="tabs" aria-label="Record views" role="navigation">
			<ul>
			<c:if test="${result.doc.dbmask != 131072}">
				<c:choose>
				<c:when test="${actionBean.view eq 'abstract'}">
							<li><h1 class="link"><a href="#" title="Abstract view" class="active" id="abstractTabLink">Abstract</a></h1></li>
							<li><h1 class="link" style="font-weight: normal"><a href="/search/doc/detailed.url?pageType=${actionBean.pageType}&amp;${actionBean.detnavqs}&amp;dedupResultCount=${actionBean.dedupResultCount}" title="Detailed view"<c:if test="${actionBean.view eq 'detailed'}"> class="active"</c:if>>Detailed</a></h1></li>
				</c:when>
				<c:otherwise>
							<li><h1 class="link" style="font-weight: normal"><a id="abstractTabLink" href="/search/doc/abstract.url?pageType=${actionBean.pageType}&amp;${actionBean.absnavqs}&amp;dedupResultCount=${actionBean.dedupResultCount}" title="Abstract view"<c:if test="${actionBean.view eq 'abstract'}"> class="active"</c:if>>Abstract</a></h1></li>
							<li><h1 class="link"><a href="#" title="Detailed view" class="active">Detailed</a></h1></li>
				</c:otherwise>
				</c:choose>
			</c:if>

			</ul>
			<c:choose>
				<c:when test="${actionBean.context.userSession.user.highlightingEnabled}">
					<div id="highlight" style="float:right;"><input type="text" name="highlightColorAbs"  id="hlight_color_abs" /><label id="hlight_color_abs_lbl" for="hlight_color_abs" style="padding-left:3px;font-weight:bold;">Color Search Terms</label><input type="checkbox" id="ckbackhighlight" style="margin-bottom:0px;vertical-align:text-bottom;" <c:if test="${actionBean.context.userSession.user.userPrefs.highlightBackground}">checked="checked"</c:if>/><label for="ckbackhighlight"><b>Background Highlighting</b></label></div>
				</c:when>
				<c:otherwise>
					<div id="highlight" style="float:right;display:none"><input type="checkbox" id="ckhighlight" <c:if test="${actionBean.ckhighlighting}">checked="checked"</c:if>/><label for="ckhighlight"><b>Highlight search terms</b></label></div>
				</c:otherwise>
			</c:choose>

			<div class="clear"></div>

			</div>


			<div id="abstractarea" class="shadowbox" aria-label="Article" role="main">
			<form  name="quicksearchresultsform" >
			<p class="topline">Record ${result.doc.hitindex} from ${result.doc.dbname}<c:if test="${not empty actionBean.displayquery}"> for: ${actionBean.displayquery},
<c:choose>
	<c:when test="${not empty actionBean.emailalertweek}">Week ${actionBean.emailalertweek}</c:when>
	<c:when test="${not empty actionBean.updatesNo}">Last ${actionBean.updatesNo} update(s)</c:when>
	<c:when test="${'Combined' eq searchtype}"></c:when>
	<c:otherwise>${actionBean.startYear}-${actionBean.endYear}</c:otherwise>
</c:choose></c:if></p>

		<div class="hr" style="color: #9b9b9b; background-color: #9b9b9b; height: 1px; margin: 0 12px 7px 0;width:100%"><hr/></div>

			<p style="margin:0 0 5px 0">Check record to add to Selected Records</p>

	        <%-- Mendeley COinS item --%>
	        <data:mendeleyformat result="${result}"/>

			<c:choose>
			<c:when test="${actionBean.view eq 'detailed'}"><jsp:include page="parts/detailedformat.jsp"/></c:when>
			<c:otherwise><jsp:include page="parts/abstractformat.jsp"/></c:otherwise>
			</c:choose>

			<div id="deliverylinks">
				<h2 id="deliveryheader">Full-text and Local Holdings Links</h2>
				<div id="deliverybody">
					<c:if test="${not empty result.lhlinkObjects}">
						<p>
						<c:forEach items="${result.lhlinkObjects}" var="lhlink" varStatus="status">
							<c:if test="${not empty lhlink.url}">
								<c:if test="${status.count>1}"><span class="pipe">|</span></c:if>
								<c:choose>
	                        		<c:when test="${not empty lhlink.imageUrl}">
	                        			<c:choose>
											<c:when test="${actionBean.view eq 'detailed'}">
												<a CLASS="LgBlueLink" title="Right-click to open in new tab" onclick="openLocalHoldingsLink(event,'Detailed Format',this);" href="/search/results/localholdinglinks.url?docId=${result.doc.docid}&url=${lhlink.url}&position=${lhlink.position}" target="new">
			                        				<img src="${lhlink.imageUrl}" alt="${lhlink.label}" border="0" />
			                        			</a>
											</c:when>
											<c:otherwise>
												<a CLASS="LgBlueLink" title="Right-click to open in new tab" onclick="openLocalHoldingsLink(event,'Abstract Format',this);" href="/search/results/localholdinglinks.url?docId=${result.doc.docid}&url=${lhlink.url}&position=${lhlink.position}" target="new">
			                        				<img src="${lhlink.imageUrl}" alt="${lhlink.label}" border="0" />
			                        			</a>
											</c:otherwise>
										</c:choose>
	                        		</c:when>
	                        		<c:otherwise>
	                        			<c:choose>
											<c:when test="${actionBean.view eq 'detailed'}">
												<a CLASS="LgBlueLink" title="Right-click to open in new tab" onclick="openLocalHoldingsLink(event,'Detailed Format',this);" href="/search/results/localholdinglinks.url?docId=${result.doc.docid}&url=${lhlink.url}&position=${lhlink.position}" target="new">${lhlink.label}</a>
											</c:when>
											<c:otherwise>
												<a CLASS="LgBlueLink" title="Right-click to open in new tab" onclick="openLocalHoldingsLink(event,'Abstract Format',this);" href="/search/results/localholdinglinks.url?docId=${result.doc.docid}&url=${lhlink.url}&position=${lhlink.position}" target="new">${lhlink.label}</a>
											</c:otherwise>
										</c:choose>

	                        		</c:otherwise>
	                    		</c:choose>
							</c:if>
						</c:forEach>
						</p>
					</c:if>

                <%--
				<data:localHoldings  snvalue="${result.sn}" textzones="${actionBean.context.userSession.userTextZones}" source="abstract" status="true" docid="${result.doc.docid}"/>
                 --%>

				<c:if test="${result.fulltext}">
				<p>
					<a href="javascript:newwindow=window.open('/search/results/fulltext.url?docID=${result.doc.docid}','newwindow','width=500,height=500,toolbar=no,location=no,scrollbars,resizable'); void('');" title="Full-text"><img id="ftimg" class="fulltext" src="/static/images/full_text.png" alt="Full-text"></a>
				</p>
				</c:if>
    			<c:if test="${result.doc.dbmask ne '131072' and actionBean.lindahall}">
    			<p><a title="Linda Hall Library document delivery service" href="javascript:lindaHall('${actionBean.sessionid}','${result.doc.docid}','${result.doc.dbid}')">Linda Hall Library document delivery service</a></p>
    			</c:if>
				</div>
			</div>

			<div class="clear"></div>
			</form>
			</div>

			<div class="clear"></div>

		</td>

<%-- *********************************************************** --%>
<%-- Abstract RH column - scopus tools, add a tag, etc.!         --%>
<%-- *********************************************************** --%>
		<td id="rhcolumn" aria-label="Related content" role="complementary">

<%-- References box                                                 --%>
<jsp:include page="parts/references_box.jsp"></jsp:include>

<%-- Scopus cited-by box                                                 --%>
<jsp:include page="parts/citedby_box.jsp"></jsp:include>

<%-- Tags & groups box                                                   --%>
<jsp:include page="parts/tagbubble.jsp"></jsp:include>

		</td>
		</tr>

		</table>

		<div class="clear"></div>

	</div>

	</stripes:layout-component>

	<stripes:layout-component name="jsbottom_custom">
	<jwr:script src="/bundles/abstract.js"></jwr:script>
	<c:if test="${actionBean.lindahall}">
		<script src="/static/js/lindaHall.js?v=${releaseversion}" language="Javascript"></script>
	</c:if>
	<SCRIPT LANGUAGE="Javascript">

	var searchquery="${actionBean.encodeddisplayquery}";
	var sessionid="${actionBean.sessionid}";
	var searchid="${actionBean.searchid}";
	var resultscount="${actionBean.resultscount}";
	var searchtype = "${param.searchtype}";
	var database = "${actionBean.database}";
	var databaseid = "${actionBean.compmask}";
	var handle="${result.doc.hitindex}";
	var docid="${result.doc.docid}";
	var view="${actionBean.view}";
	$(document).ready(function() {
		$("#highlight").show();
		$("#ckhighlight").click(function(e) {
			if ($(this).is(':checked')) {
				$("span.nohit").removeClass("nohit").addClass("hit");
				$.get("/session/highlight.url?CID=highlight&value=true");
			} else {
				$("span.hit").removeClass("hit").addClass("nohit");
				$.get("/session/highlight.url?CID=highlight&value=false");
			}

		});
		$("#ckbackhighlight").click(function(e) {
			var oldColor;
			if($.cookie('ev_highlight')){
				oldColor = JSON.parse($.cookie('ev_highlight')).color;
			}else{
				oldColor = $("#hlight_color_abs").spectrum("get").toString();
			}

			if ($(this).prop('checked')) {
				$(".hit").removeClass("hit").addClass("bghit");
				$(".bghit").css("color", "black");
				$("#hlight_color_abs").spectrum("set", "#000000");
				$("#hlight_color_abs").spectrum("disable");
				$("#hlight_color_abs_lbl").css("color", "gray");
				$.cookie('ev_highlight', '{"bg_highlight":'+true+', "color":"'+oldColor +'"}',{path:'/'});

			} else {
				$(".bghit").removeClass("bghit").addClass("hit");
				$(".hit").css("color", oldColor);
				$("a span.hit").css("color", "inherit");
				$("#hlight_color_abs").spectrum("enable");
				$("#hlight_color_abs").spectrum("set", oldColor);
				$("#hlight_color_abs_lbl").css("color", "black");
				$.cookie('ev_highlight', '{"bg_highlight":'+false+', "color":"'+oldColor +'"}',{path:'/'});
			}

		});
		if(highlightV1){
			var storedColor = '${actionBean.context.userSession.user.userPrefs.highlight}';
			var sessionColor = storedColor;

			if($.cookie("ev_highlight")){
				sessionColor = JSON.parse($.cookie("ev_highlight")).color;
			}

			$("#hlight_color_abs").spectrum({
			    showPaletteOnly: true,
			    showPalette:true,
			    color: sessionColor,
			    preferredFormat:'hex',
			    palette: [
			        ['#ff8200','#2babe2','#158c75', "#000000"]
			    ]
			});
			$("#hlight_color_abs").change(function(){
				var newColor = $("#hlight_color_abs").spectrum("get").toString();
				var bgColor = $("#ckbackhighlight").prop("checked");
				$.cookie('ev_highlight', '{"bg_highlight":'+bgColor+', "color":"'+newColor +'"}',{path:'/'});
				if(!bgColor){
					$(".hit").css("color",newColor);
					$("a span.hit").css("color", "inherit");
				}

			});
			if ( $("#ckbackhighlight").prop("checked")) {
				$("#hlight_color_abs").spectrum("set", "#000000");
				$("#hlight_color_abs").spectrum("disable");
				$("#hlight_color_abs_lbl").css("color", "gray");
			}
		}

		// Adjust title element when <sup> present
		var suptext = $("#detailed td");
		suptext.each(function() {
			if ($(this).find('sup').length > 0) $(this).css('position','relative').css('top','-5px');
		});

		//
		// Handle the print link
		//
		$("#printlink").click(function(e) {
			e.preventDefault();
			new_window = window.open($(this).attr('href'), 'NewWindow',
					'status=yes,resizable,scrollbars,width=640,height=480');
			new_window.focus();

			return false;
		});

		//
		// Handle the email link
		//
		$("#emaillink").click(function(e) {
			e.preventDefault();

			new_window = window.open($(this).attr('href'), 'NewWindow',
					'status=yes,resizable,scrollbars,width=640,height=480');
			new_window.focus();

			return false;
		});

		//
		// Handle the download link
		//
		//$("#downloadlink").click(function(e) {
		//	e.preventDefault();
		//	new_window = window.open($(this).attr('href'), 'NewWindow',
		//			'status=yes,resizable,scrollbars,width=640,height=600');
		//	new_window.focus();
		//	return false;
		//});

		//
		// Handle the blog link
		//
		$("#bloglink").click(function(e) {
			e.preventDefault();

			new_window = window.open($(this).attr('href'), 'NewWindow',
					'status=yes,resizable,scrollbars,width=640,height=480');
			new_window.focus();

			return false;
		});



});
	//Script for saved records format
	function savedrecordsFormat(sessionid, searchtype, searchid, database,
			databaseid, displayformat, source) {
		var i = 0;
		var url = null;
		var docidstring = "&docidlist=";
		var handlestring = "&handlelist=";
		var hiddensize = document.quicksearchresultsform.elements.length;
		var cbcheckvalue = false;
		if (document.quicksearchresultsform.cbresult.checked) {
			cbcheckvalue = true;
		}
		// jam 10/4/2002
		// bug - single view of document can be 'Saved to Folder'
		// regardless of whether or not the document is selected (in basket)
		//if(cbcheckvalue)
		//{
		for ( var i = 0; i < hiddensize; i++) {
			var nameOfElement = document.quicksearchresultsform.elements[i].name;
			var valueOfElement = document.quicksearchresultsform.elements[i].value;
			if ((nameOfElement.search(/HANDLE/) != -1)
					&& (valueOfElement != "")) {
				handlestring += valueOfElement;
			}
			if ((nameOfElement.search(/DOC-ID/) != -1)
					&& (valueOfElement != "")) {
				docidstring += valueOfElement;
			}
		}
		//}
		if (displayformat == 'addrecord') {
			url = "//personal/folders/save/view.url?CID=viewSavedFolders&databaseid=" + databaseid
					+ docidstring + "&database=" + database + "&EISESSION=" + sessionid;
		} else {
			url = "/login/customer.url?CID=personalLoginForm&displaylogin=true&displayform=addrecords&database="
					+ database + "&databaseid=" + databaseid + "&source="
					+ source + docidstring + "&EISESSION=" + sessionid;
		}
		NewWindow = window.open(url, 'NewWindow',
				'status=yes,resizable,scrollbars,width=600,height=400');
		NewWindow.focus();
	}

</SCRIPT>
	</stripes:layout-component>
<stripes:layout-component name="survey">
	<c:set var="surveyLocation" value="record" scope="request"/>
	<%@ include file="/WEB-INF/pages/include/survey.jsp" %>
</stripes:layout-component>

</stripes:layout-render>
