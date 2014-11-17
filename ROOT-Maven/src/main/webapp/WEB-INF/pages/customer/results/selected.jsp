<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://jawr.net/tags" prefix="jwr" %>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld"	prefix="stripes"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<%-- NOTE: "pageTitle" attribute for layout set in action bean --%>
<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp">

	<stripes:layout-component name="csshead">
	<jwr:style src="/bundles/selected.css"></jwr:style>
	<c:if test="${(actionBean.view eq 'detailed') or (actionBean.view eq 'abstract')}">
		<link href="/static/css/ev_abstract.css?v=${releaseversion}" media="all" type="text/css" rel="stylesheet"></link>
	</c:if>
	</stripes:layout-component>

	<stripes:layout-component name="contents">

	<div id="selectedbox">

    <h1 >Selected records</h1>
	<c:if test="${(not empty actionBean.results)}">
	    <div style="margin-left:7px;">There are ${actionBean.resultscount} selected records.</div>
	</c:if>
	<c:if test="${empty actionBean.results}">
	<div style="margin-top:1px;margin-left:7px;margin-bottom:7px;">Currently there are no records selected. Please select records from the search results and try again.</div>
	</c:if>
	<div class="hr" style="color: #D7D7D7; background-color: #D7D7D7; height: 2px; margin: 0px 7px 7px 7px ;width:97.5%  "><hr/></div>

	<%-- ******************************************************************************* --%>
	<%-- PAGE NAVIGATION                                                                 --%>
	<%-- ******************************************************************************* --%>
	<c:if test="${not empty actionBean.searchresults or not empty actionBean.newsearch}">
	<div class="searchlinks">
	<c:if test="${not empty actionBean.searchresults}">
		<a class="backtoresultslink" href="/search/results/quick.url?${actionBean.searchresults}" title="Back to Search Results">Back to search results</a>
	</c:if>
	<c:if test="${not empty actionBean.newsearch}">
		<c:if test="${not empty actionBean.searchresults}">&nbsp;&nbsp;|&nbsp;&nbsp;</c:if>
		<a class="newsearchlink" href="/home.url?CID=home&database=${actionBean.database}" title="New Search">New search</a>
	</c:if>
	</div>
	</c:if>
	<c:if test="${(not empty actionBean.results)}">
	<jsp:include page="parts/pagenav.jsp">
		<jsp:param name="position" value="top"/>
		<jsp:param value="selectedrecords" name="displaytype"/>
	</jsp:include>
	</c:if>

		<div class="clear"></div>

	<%-- ******************************************************************************* --%>
	<%-- SELECTED RESULTS                                                                 --%>
	<%-- ******************************************************************************* --%>
	<div id="resultswrapper" style="width:99%">

<c:if test="${not empty actionBean.results}">
	<%-- ******************************************************************************* --%>
	<%-- Next start the results section, wrapper contains "refine" and "manager" areas   --%>
	<%-- as well as the list of results themselves                                       --%>
	<%-- ******************************************************************************* --%>
	<FORM name="resultsform" id="resultsform">
		<div id="selectedarea">
<INPUT TYPE="HIDDEN" NAME="database" VALUE="${actionBean.database}">
<INPUT TYPE="HIDDEN" NAME="sessionid" VALUE="${actionBean.sessionid}">
<INPUT TYPE="HIDDEN" NAME="searchid" VALUE="${actionBean.searchid}">
<INPUT TYPE="HIDDEN" NAME="pagesize" VALUE="${actionBean.pagenav.resultsperpage}">
<INPUT TYPE="HIDDEN" NAME="resultscount" VALUE="${actionBean.resultscount}">
<INPUT TYPE="HIDDEN" NAME="searchtype" VALUE="${actionBean.searchtype}">
<INPUT TYPE="HIDDEN" NAME="BASKETCOUNT" VALUE="${actionBean.pagenav.currentpage}">
<INPUT TYPE="HIDDEN" NAME="backIndex" VALUE="${actionBean.backindex}">

		<%-- ******************************************************************************* --%>
		<%-- RESULTS MANAGER include                                                         --%>
		<%-- ******************************************************************************* --%>
		<jsp:include page="parts/resultsmanager.jsp">
			<jsp:param value="selectedrecords" name="displaytype"/>
		</jsp:include>

			<div class="clear"></div>

			<div id="resultslist">
			<%--
				Iterate over results
			--%>
		<c:choose>
			<c:when test="${actionBean.view eq 'detailed'}"><jsp:include page="parts/detailedformat.jsp"/></c:when>
			<c:when test="${actionBean.view eq 'abstract'}"><jsp:include page="parts/abstractformat.jsp"/></c:when>
			<c:otherwise><jsp:include page="parts/citationformat.jsp"/></c:otherwise>
		</c:choose>

			</div>
		</div><!--  end resultsarea -->
		</form>
</c:if>



	</div> <!--  end resultswrapper -->

	<%-- ******************************************************************************* --%>
	<%-- PAGE NAVIGATION                                                                 --%>
	<%-- ******************************************************************************* --%>
	<c:if test="${(not empty actionBean.results)}">
	<jsp:include page="parts/pagenav.jsp">
		<jsp:param name="position" value="bottom"/>
		<jsp:param value="selectedrecords" name="displaytype"/>
	</jsp:include>
	</c:if>
		<div class="clear"></div>

	</div> <!--  end selectedbox -->

	<div class="clear"></div>

	</stripes:layout-component>

	<stripes:layout-component name="jsbottom_custom">
	<jwr:script src="/bundles/selected.js"></jwr:script>
    <script type="text/javascript">
    GALIBRARY.createWebEventWithLabel('Selected Records', 'Home', 'View=${actionBean.view}; ');
    </script>
	</stripes:layout-component>


</stripes:layout-render>
