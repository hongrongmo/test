<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>

<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld"	prefix="stripes"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp" pageTitle="Engineering Village - Tag Search Results">

	<stripes:layout-component name="csshead">
	<link href="/static/css/ev_results.css?v=${releaseversion}"" media="all" type="text/css" rel="stylesheet"></link>
	<link href="/static/css/ev_tagsgroups.css?v=${releaseversion}" media="all" type="text/css" rel="stylesheet"></link>
	<link href="/static/css/ev_oneclickdl.css?v=${releaseversion}" media="all" type="text/css" rel="stylesheet"></link>
	</stripes:layout-component>

	<stripes:layout-component name="contents">
	<style type="text/css">.recordperpage {padding:5px 0 0 11px; float: left; text-align:left; }</style>

	<div id="resultsbox">


	<%-- ******************************************************************************* --%>
	<%-- Top line is for the query display and search options (save search, alert, etc.) --%>
	<%-- ******************************************************************************* --%>
	<div id="resultsoptions">

	<%-- ******************************************************************************* --%>
	<%-- QUERY DISPLAY AND TOOLBAR                                                       --%>
	<%-- ******************************************************************************* --%>
	<jsp:include page="parts/tagsquerytoolbar.jsp"/>
	</div>

<c:choose><c:when test="${actionBean.resultscount eq 0}">
	<p style="margin: 0 7px 0 10px;"><img title="No results were found" src="/static/images/No_results_found.png">&nbsp;&nbsp;<b>No results were found.</b></p>
	<br/>
	<p style="margin: 0 7px 0 10px;">Use the search box above to run a new tag search, or go <a title="Tags &amp; Groups - View/Edit your tags and groups" href="/tagsgroups/display.url?CID=tagsLoginForm&amp;searchtype=TagSearch" class="">back to Tags &amp; Groups home</a>.
</c:when><c:otherwise>

	<%-- ******************************************************************************* --%>
	<%-- PAGE NAVIGATION                                                                 --%>
	<%-- ******************************************************************************* --%>
	<stripes:errors><stripes:errors-header>&lt;div style="margin-top:10px; padding-left:10px; width:420px;"&gt;</stripes:errors-header><stripes:individual-error/><stripes:errors-footer>&lt;/div&lt;</stripes:errors-footer></stripes:errors>
	<jsp:include page="parts/tagsearchpagenav.jsp">
		<jsp:param name="position" value="top"/>
	</jsp:include>

		<div class="clear"></div>

	<div id="tagresultsarea">
	<%-- ******************************************************************************* --%>
	<%-- Next start the results section, wrapper contains "refine" and "manager" areas   --%>
	<%-- as well as the list of results themselves                                       --%>
	<%-- ******************************************************************************* --%>
	<FORM name="resultsform" id="resultsform">
		<INPUT TYPE="HIDDEN" NAME="searchquery" VALUE="${actionBean.encodeddisplayquery}">
		<INPUT TYPE="HIDDEN" NAME="database" VALUE="${actionBean.database}">
		<INPUT TYPE="HIDDEN" NAME="sessionid" VALUE="${actionBean.sessionid}">
		<INPUT TYPE="HIDDEN" NAME="searchid" VALUE="${actionBean.searchid}">
		<INPUT TYPE="HIDDEN" NAME="pagesize" VALUE="${actionBean.pagenav.resultsperpage}">
		<INPUT TYPE="HIDDEN" NAME="resultscount" VALUE="${actionBean.resultscount}">
		<INPUT TYPE="HIDDEN" NAME="searchtype" VALUE="${actionBean.searchtype}">
		<INPUT TYPE="HIDDEN" NAME="currentpage" VALUE="${actionBean.pagenav.currentpage}">
		<INPUT TYPE="HIDDEN" NAME="databaseid" VALUE="${actionBean.database}">
		<input type="hidden" name="dbpref" value="">
		<input type="hidden" name="fieldpref" value="">
		<input type="hidden" name="DupFlag" value="false">
		<input type="hidden" name="origin" value="">
		<input type="hidden" name="dbLink" value="">
		<input type="hidden" name="linkSet" value="">
		<input type="hidden" name="tagSearchFlag" value="">
		<input type="hidden" name="scope" value="${actionBean.scope}">
		<INPUT TYPE="HIDDEN" NAME="backIndex" VALUE="${actionBean.pagenav.currentindex}">
		<INPUT TYPE="HIDDEN" NAME="basketCount" VALUE="${actionBean.basketCount}">


		<%-- ******************************************************************************* --%>
		<%-- RESULTS MANAGER include                                                         --%>
		<%-- ******************************************************************************* --%>
		<div class="shadowbox">

		<jsp:include page="parts/patentrefResultsmanager.jsp">
			<jsp:param value="tagresults" name="displaytype"/>
		</jsp:include>

		<jsp:include page="parts/resultslist.jsp"/>
		</div>
	</FORM>
	</div><!--  end resultsarea -->

	<div class="clear"></div>

	<br/>
	<%-- ******************************************************************************* --%>
	<%-- PAGE NAVIGATION                                                                 --%>
	<%-- ******************************************************************************* --%>
	<jsp:include page="parts/tagsearchpagenav.jsp">
		<jsp:param name="position" value="bottom"/>
	</jsp:include>
</c:otherwise></c:choose>

	<div class="clear"></div>

	</div> <!--  end resultsbox -->

	<div class="clear"></div>

	</stripes:layout-component>

	<stripes:layout-component name="jsbottom_custom">
	<SCRIPT LANGUAGE="Javascript" SRC="/static/js/URLEncode.js?v=${releaseversion}"></script>
	<SCRIPT LANGUAGE="Javascript" SRC="/static/js/SelectedRecords.js?v=${releaseversion}"></script>
	<SCRIPT LANGUAGE="Javascript" SRC="/static/js/SearchResults.js?v=${releaseversion}"></script>
	<SCRIPT LANGUAGE="Javascript" SRC="/static/js/QuickResults.js?v=${releaseversion}"></script>
	<SCRIPT LANGUAGE="Javascript" SRC="/static/js/CitedBy.js?v=3"></script>
	<script type="text/javascript" src="/static/js/TagsAjax.js?v=2"></script>
	<SCRIPT LANGUAGE="Javascript" SRC="/static/js/Basket.js?v=2"></script>
	<SCRIPT LANGUAGE="Javascript" SRC="/static/js/oneclick.js?v=2"></script>


    <script type="text/javascript">
      var wadjust = 268;
      $(document).ready(function() {
        function resizeResults() {
              $("#resultsarea").width($("#resultsbox").width() - wadjust);
        }
        resizeResults();
        $(window).bind('resize', resizeResults);
      });
    </script>
	</stripes:layout-component>


</stripes:layout-render>
