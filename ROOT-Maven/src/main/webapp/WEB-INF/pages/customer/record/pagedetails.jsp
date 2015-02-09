<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>

<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld"	prefix="stripes"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/tlds/datatags.tld" prefix="data" %>

<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp">

	<stripes:layout-component name="csshead">
	<link href="/static/css/ev_abstract.css?v=${releaseversion}" media="all" type="text/css" rel="stylesheet"></link>
	<!--[if IE 6]>
	<link href="/static/css/ev_ie6.css?v=${releaseversion}" media="all" type="text/css" rel="stylesheet"></link>
	<![endif]-->
	</stripes:layout-component>

	<stripes:layout-component name="contents">

	<c:set var="rerunsearchurl" scope="request">
		<c:choose>
            <c:when test="${empty actionBean.searchtype or actionBean.searchtype eq 'Quick' or actionBean.searchtype eq 'Book'}">/search/results/quick.url</c:when>
			<c:when test="${actionBean.searchtype eq 'Thesaurus'}">/search/results/thes.url</c:when>
            <c:when test="${actionBean.searchtype eq 'Book'}">/search/results/quick.url</c:when>
            <c:when test="${actionBean.searchtype eq 'TagSearch'}">/search/results/tags.url</c:when>
			<c:when test="${actionBean.searchtype eq 'Combined'}">/search/results/expert.url</c:when>
			<c:when test="${actionBean.searchtype eq 'Easy'}">/search/results/quick.url</c:when>
			<c:when test="${actionBean.searchtype eq 'Tags'}">/search/results/tags.url</c:when>
			<c:when test="${actionBean.searchtype eq 'Expert'}">/search/results/expert.url</c:when>
			<c:otherwise></c:otherwise>
		</c:choose>
	</c:set>
		<c:set var="searchhistoryurl">
			<c:choose>
				<c:when test="${actionBean.searchtype eq 'Thesaurus'}">/search/thesHome.url</c:when>
				<c:when test="${actionBean.searchtype eq 'Book'}">/search/ebook.url</c:when>
				<c:when test="${actionBean.searchtype eq 'Quick'}">/search/quick.url</c:when>
				<c:otherwise>/search/expert.url</c:otherwise>
			</c:choose>
		</c:set>

		<c:set var="navigationurl">
			<c:choose>
				<c:when test="${actionBean.view eq 'page'}">/search/book/detailed.url?pageType=page</c:when>
				<c:otherwise>/search/book/bookdetailed.url?pageType=book</c:otherwise>
			</c:choose>
		</c:set>

	<c:set var="result" value="${actionBean.results[0]}" scope="request"/>
	<span session-id="${actionBean.sessionid}" security="${result.citedby.md5}" an="${result.citedby.an}" doi="${result.citedby.doi}" page="${result.citedby.firstpage}" volume="${result.citedby.firstvolume}" issue="${result.citedby.firstissue}" issn="${result.citedby.issn}"  isbn="${result.citedby.isbn}" isbn13="${result.citedby.isbn13}"  style="display: none;" name="citedbyspan"></span>

	<div id="abstractbox">

<%-- *********************************************************** --%>
<%-- Top navbar - view search history, back to results and --%>
<%-- page navigation --%>
<%-- *********************************************************** --%>
		<div id="abstractnavbar" style="margin-top:15px;margin-bottom:10px;"  aria-label="Results options" role="navigation">
            <span><a class="newsearch" href="/home.url?database=${actionBean.database}" title="Run new search">New Search</a>|</span>
			<span><a class="history" href="${searchhistoryurl}?${actionBean.newsearchqs}#searchhistorybox" title="View search history">View search history</a>|</span>
			<span><a class="backtoresults" href="${rerunsearchurl}?${actionBean.resultsqs}" title="Go back to search results">Back to results</a>|</span>
			<c:if test="${not empty actionBean.dedupnavqs}">
			<span><a class="backtoresults" href="/search/results/dedup.url?${actionBean.dedupnavqs}" title="Go back to deduplicated search results">Back to deduplicated results</a>|</span>
			</c:if>
			<span class="pagenav">
			<c:if test="${not empty actionBean.prevqs}"><a style="margin-right:0" href="${navigationurl}&amp;${actionBean.prevqs}" title="Go to previous record">&lt; Previous</a></c:if>
				<b>
				<c:choose>
				<c:when test="${actionBean.view eq 'book'}">${actionBean.currentpage}</c:when>
				<c:otherwise>${result.doc.hitindex}</c:otherwise>
				</c:choose>
				 of ${actionBean.resultscount}</b>
			<c:if test="${not empty actionBean.nextqs}"><a href="${navigationurl}&amp;${actionBean.nextqs}" title="Go to next record">Next &gt;</a></c:if>
			</span>
			<div class="clear"></div>
		</div>

		<div class="hr" style="height:5px; margin:0 10px"><hr></div>

		<style type="text/css">

		</style>


		<table id="abstractwrapperpage" border="0" cellpadding="0" cellspacing="0">
<%-- *********************************************************** --%>
<%-- Abstract LH column - abstract!                              --%>
<%-- *********************************************************** --%>
		<tr>
		<td id="lhcolumn">
		<c:choose>
			<c:when test="${actionBean.view eq 'page'}">
				<c:set var="abstractwidth" value="736px" />
			</c:when>
			<c:otherwise>
				<c:set var="abstractwidth" value="640px" />
			</c:otherwise>
		</c:choose>
<%-- *********************************************************** --%>
<%--  tools - full text, blog, email/print/download, etc.--%>
<%-- *********************************************************** --%>
		<div id="abstracttoolbar" style="margin-bottom:7px;width:${abstractwidth}"  aria-label="Record options" role="navigation">
	<c:if test="${result.fulltext}">
		<span><a class="fulltext" href="javascript:newwindow=window.open('/search/results/fulltext.url?docID=${result.doc.docid}','newwindow','width=500,height=500,toolbar=no,location=no,scrollbars,resizable'); void('');" title="View full text (opens in a new window)"><img id="ftimg" class="fulltext" src="/static/images/full_text.png" title="View full text (opens in a new window)"></a>|</span>
	</c:if>
	<c:if test="${'131072' eq result.doc.dbmask}">
			<c:if test="${not empty result.readpagelink}"><span>${result.readpagelink}|</span></c:if>
			<c:if test="${not empty result.readchapterlink}"><span>${result.readchapterlink}|</span></c:if>
			<c:if test="${not empty result.readbooklink}"><span>${result.readbooklink}<c:if test="${actionBean.view eq 'page'}">|</c:if></span></c:if>
	</c:if>
	<c:if test="${actionBean.view eq 'page'}">
		<c:if test="${actionBean.bloglink}">
			<span><a class="blog" id="bloglink" title="Create a link to share this record" href="/blog/open.url?mid=${result.doc.docid}&database=${result.doc.dbid}">Blog This</a>|</span>
		</c:if>
			<span><a class="email" id="emaillink" title="Email this record" href="/delivery/email/display.url?database=${actionBean.database}&displayformat=detailed&docidlist=${result.doc.docid}&handlelist=${result.doc.hitindex}">Email</a>|</span>
			<span><a class="print" id="printlink" title="Print this record" href="/delivery/print/display.url?database=${actionBean.database}&displayformat=detailed&docidlist=${result.doc.docid}&handlelist=${result.doc.hitindex}">Print</a>|</span>
			<span><a class="download" id="downloadlink" title="Download this record" href="/delivery/download/display.url?database=${actionBean.database}&displayformat=detailed&docidlist=${result.doc.docid}&handlelist=${result.doc.hitindex}">Download</a>|</span>
			<span><a class="save" title="Save this record to a folder in Settings" href="/personal/folders/save/view.url?CID=viewSavedFolders&EISESSION=${actionBean.sessionid}&SEARCHID=${actionBean.searchid}&count=1&source=selectedset&database=${actionBean.database}&format=${actionBean.view=='page'?'expertSearchDetailedFormat':'expertSearchAbstractFormat'}&DOCINDEX=${result.doc.hitindex}&backurl=SAVETOFOLDER">Save to Folder</a></span>
   </c:if>
			<div class="clear"></div>
		</div>

		<div class="hr" style="color: #9b9b9b; background-color: #9b9b9b; height: 1px; margin: 0 0 7px 0;"><hr></div>


<%-- *********************************************************** --%>
<%-- Page/Book display --%>
<%-- *********************************************************** --%>

			<div id="tabs" aria-label="Record views" role="navigation">
				<ul>
				<c:choose>
					<c:when test="${actionBean.view eq 'page'}">
						<c:if test="${actionBean.internalsearch ne 'yes'}">
								<li><a href="#" title="Page details view"  class="active">Page Details</a></li>
						</c:if>
								<li><a href="/search/book/bookdetailed.url?pageType=book&amp;${actionBean.bookdetnavqs}" title="Book details view"<c:if test="${actionBean.view eq 'book'}"> class="active"</c:if>>Book Details</a></li>
					</c:when>
					<c:otherwise>
						<c:if test="${actionBean.internalsearch ne 'yes'}">
								<li><a href="/search/book/detailed.url?pageType=page&amp;${actionBean.pagedetnavqs}" title="Page details view"<c:if test="${actionBean.view eq 'page'}"> class="active"</c:if>>Page Details</a></li>
						</c:if>
								<li><a href="#" title="Book details view" class="active">Book Details</a></li>
					</c:otherwise>
				</c:choose>

				</ul>
				<c:if test="${actionBean.view eq 'page'}">
				<c:when test="${actionBean.context.userSession.user.highlightingEnabled}">
					<div id="highlight" style="float:right;display:none"><input type="checkbox" id="ckbackhighlight" style="margin-bottom:0px;vertical-align:text-bottom;" <c:if test="${actionBean.context.userSession.user.userPrefs.highlightBackground}">checked="checked"</c:if>/><label for="ckbackhighlight"><b>Background Highlighting</b></label></div>
				</c:when>
				<c:otherwise>
					<div id="highlight" style="float:right;display:none"><input type="checkbox" id="ckhighlight" <c:if test="${actionBean.ckhighlighting}">checked="checked"</c:if>/><b>Highlight search terms</b></div>
				</c:otherwise>c
				</c:if>
				<div class="clear"></div>

			</div>


			<div id="abstractarea" class="shadowbox" style="width: 98%;"  aria-label="Article" role="main">
			<form  name="quicksearchresultsform" >
			<c:if test="${actionBean.view eq 'page'}">
				<p class="topline">Record ${result.doc.hitindex} from ${actionBean.displaydb} for: ${actionBean.displayquery},
				<c:choose>
					<c:when test="${not empty actionBean.emailalertweek}">Week ${actionBean.emailalertweek}</c:when>
					<c:when test="${not empty actionBean.updatesNo}">Last ${actionBean.updatesNo} update(s)</c:when>
					<c:when test="${'Combined' eq actionBean.searchtype}"></c:when>
					<c:otherwise>${actionBean.startYear}-${actionBean.endYear}</c:otherwise>
				</c:choose></p>

				<div class="hr" style="color: #9b9b9b; background-color: #9b9b9b; height: 1px; margin: 0 12px 7px 0;width:100%"><hr/></div>
			</c:if>

			<c:choose>
			<c:when test="${actionBean.view eq 'book'}"><jsp:include page="parts/bookformat.jsp"/></c:when>
			<c:otherwise>
			<p style="margin:0 0 5px 0">Check record to add to Selected Records</p>
			<jsp:include page="parts/pageformat.jsp"/></c:otherwise>
			</c:choose>


			<div class="clear"></div>
			</form>
			</div>

			<div class="clear"></div>

		</td>

<%-- *********************************************************** --%>
<%-- Abstract RH column - scopus tools, add a tag, etc.!         --%>
<%-- *********************************************************** --%>
		<td id="rhcolumnpage"  aria-label="Related content" role="complementary">
		<c:if test="${actionBean.view eq 'book'}">
	 		<span> <form name="quicksearch" style="display:inline;" action="/search/results/quick.url" method="POST" onSubmit="return searchValidation();">
                          <input type="hidden" name="EI-SESSION" value="$SESSIONID"/>
                          <input type="hidden" name="database" value="131072"/>
                          <input type="hidden" name="yearselect" value="yearrange"/>
                          <input type="hidden" name="CID" value="quickSearchCitationFormat"/>
                          <input type="hidden" name="searchtype" value="Book"/>
                          <input type="hidden" name="section1" value="KY"/>

                          <input type="hidden" name="boolean1" value="AND"/>
                          <input type="hidden" name="searchWord2" value="${result.isbn13}"/>
                          <input type="hidden" name="section2" value="BN"/>

                          <label for="txtsearchWord1">Search this book</label>
                         &#160;<input style="font-size:11px; vertical-align:left;" type="text" size="20" maxlength="64" name="searchWord1" id="txtsearchWord1"/>
                          &#160;<input style="vertical-align:middle;" type="submit"  name="search" value="Search" alt="Search this book" border="0"/>
                        </form>&#160;</span>
                      <div id="keycloud" style="width:310px;margin-top:10px">
						<div  class="sidebarbox" title="Keyword Cloud">
							<div class="sidebartitle">Keyword Cloud <a href="${actionBean.helpUrl}#Ebook_keyword_cloud.htm#About" alt="Learn more about this keyword cloud" title="Learn more about this keyword cloud" class="helpurl"><img
						src="/static/images/i.png" border="0" class="infoimg" align="top" alt=""/></a></div>

							<div style="padding: 7px;">Keywords that appear most frequently in this book</div>

							<div class="sidebardata">${actionBean.cloud}</div>

						</div>
					</div>
	 </c:if>

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
    <SCRIPT LANGUAGE="Javascript" SRC="/static/js/Basket.js?v=3"></script>
    <SCRIPT LANGUAGE="Javascript" SRC="/static/js/SelectedRecords.js?v=${releaseversion}"></script>
	<SCRIPT LANGUAGE="Javascript" SRC="/static/js/ReferexSearch.js?v=${releaseversion}"></script>
	<script language="JavaScript" type="text/javascript" src="/static/js/wz_tooltip.js?v=${releaseversion}"></script>
	<c:if test="${actionBean.lindahall}">
	<script src="/static/js/lindaHall.js?v=${releaseversion}" language="Javascript"></script>
	</c:if>
	<SCRIPT LANGUAGE="Javascript">

	var searchquery="${actionBean.encodeddisplayquery}";
	var sessionid="${actionBean.sessionid}";
	var searchid="${actionBean.searchid}";
	var resultscount="${actionBean.resultscount}";
	var searchtype = "${actionBean.searchtype}";
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
			if ($(this).is(':checked')) {
				$(".bghit").removeClass("bghit").addClass("hit");
				$.cookie('ev_highlight', '{bg_highlight":'+false+'',{path:'/'});
			} else {
				$(".hit").removeClass("hit").addClass("bghit");
				$.cookie('ev_highlight', '{bg_highlight":'+true+'',{path:'/'});
			}

		});
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
		$("#downloadlink").click(function(e) {
			e.preventDefault();

			new_window = window.open($(this).attr('href'), 'NewWindow',
					'status=yes,resizable,scrollbars,width=640,height=600');
			new_window.focus();

			return false;
		});

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
			url = "/personal/folders/save/view.url?CID=viewSavedFolders&databaseid=" + databaseid
					+ docidstring + "&database=" + database + "EISESSION=" + sessionid;
		} else {
			url = "/login/customer.url?CID=personalLoginForm&displaylogin=true&displayform=addrecords&database="
					+ database + "&databaseid=" + databaseid + "&source="
					+ source + docidstring + "EISESSION="
                    + sessionid;
		}
		NewWindow = window.open(url, 'NewWindow',
				'status=yes,resizable,scrollbars,width=600,height=400');
		NewWindow.focus();
	}

	<c:if test="${actionBean.view eq 'book'}">
//  Book specific code for loading pages from TOC links
            function loadFromToc(isbn,page)
            {
              isbn = "${result.isbn13}";
	          var common =  "/controller/servlet/Controller?CID=bookFrameset&docid=pag_"+isbn.toLowerCase()+"_"+page+"&searchid=${actionBean.searchid}&DOCINDEX=${result.doc.hitindex}&docid=${result.doc.docid}&database=${actionBean.compmask}";
              var newwwin = window.open(common,'NewWindow','height=800,width=700,status=yes,resizable,scrollbars=1,menubar=no');
              if(newwwin)
              {
                newwwin.focus();
              }

              return;
            }

            function searchFromCloud(isbn,tag)
            {
              document.location = "/search/results/quick.url?CID=quickSearchCitationFormat&yearselect=yearrange&database=131072&searchWord1={" + escape(tag) + "}&searchWord2=" + isbn + "&boolean1=AND&section1=KY&section2=BN";
              return;
            }
       </c:if>

</SCRIPT>
	</stripes:layout-component>


</stripes:layout-render>
