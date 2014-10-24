<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://jawr.net/tags" prefix="jwr" %>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld"	prefix="stripes"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/tlds/functions.tld" prefix="f" %>

<%-- NOTE: "pageTitle" attribute for layout set in action bean --%>
<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp">

	<stripes:layout-component name="csshead">
	<link href="/static/css/ev_selected.css?v=${releaseversion}" media="all" type="text/css" rel="stylesheet"></link>
	<link href="/static/css/ev_results.css?v=${releaseversion}"" media="all" type="text/css" rel="stylesheet"></link>
	<link href="/static/css/ev_oneclickdl.css?v=${releaseversion}"" media="all" type="text/css" rel="stylesheet"></link>
	<c:if test="${(actionBean.view eq 'detailed') or (actionBean.view eq 'abstract')}">
		<link href="/static/css/ev_abstract.css?v=${releaseversion}" media="all" type="text/css" rel="stylesheet"></link>
	</c:if>
	</stripes:layout-component>

	<stripes:layout-component name="contents">

	<div id="selectedbox">
	<div  style="  margin: 0px 7px 7px 0px ;width:97.5%  "></div>
    <div id="folderview">Folder Name :</div><div id="folderview1">&nbsp;&nbsp;${actionBean.folderName}</div>

	<p style="margin-left:7px;margin-top:7px"><c:choose><c:when test="${empty actionBean.results}">There are no records in this folder.</c:when><c:when test="${actionBean.folderSize eq 1}">There is 1 record in this folder.</c:when><c:otherwise>There are ${actionBean.folderSize} saved records in this folder.</c:otherwise></c:choose></p>

	<c:choose>
	<c:when test="${empty actionBean.results}"><div class="hr" style="color: #D7D7D7; background-color: #D7D7D7; height: 2px; margin: 7px 7px 15px 7px ;width:97.5% "><hr/></div></c:when>
	<c:otherwise><div class="hr" style="color: #D7D7D7; background-color: #D7D7D7; height: 2px; margin: 7px 7px 7px 7px ;width:97.5% "><hr/></div></c:otherwise>
	</c:choose>

	<div class="searchlinks" style="margin-top:0px;">
	           <a  href="/back/service.url?feature=SAVETOFOLDER">Return to previous page</a>
	</div>

	<div class="clear"></div>



	<%-- ******************************************************************************* --%>
	<%-- SELECTED RESULTS                                                                 --%>
	<%-- ******************************************************************************* --%>


<c:if test="${not empty actionBean.results}">
<div id="resultswrapper" style="width:99%;margin-top:7px;">
	<%-- ******************************************************************************* --%>
	<%-- Next start the results section, wrapper contains "refine" and "manager" areas   --%>
	<%-- as well as the list of results themselves                                       --%>
	<%-- ******************************************************************************* --%>
		<div id="selectedarea">
	<FORM name="resultsform" id="resultsform">
<INPUT TYPE="HIDDEN" NAME="database" VALUE="${actionBean.database}">
<INPUT TYPE="HIDDEN" NAME="sessionid" VALUE="${actionBean.sessionid}">
<INPUT TYPE="HIDDEN" NAME="searchid" VALUE="${actionBean.searchid}">
<INPUT TYPE="HIDDEN" NAME="pagesize" VALUE="${actionBean.pagenav.resultsperpage}">
<INPUT TYPE="HIDDEN" NAME="resultscount" VALUE="${actionBean.resultscount}">
<INPUT TYPE="HIDDEN" NAME="searchtype" VALUE="${actionBean.searchtype}">
<INPUT TYPE="HIDDEN" NAME="BASKETCOUNT" VALUE="${actionBean.pagenav.currentpage}">
<INPUT TYPE="HIDDEN" NAME="backIndex" VALUE="${actionBean.backindex}">
<INPUT TYPE="HIDDEN" NAME="folderid" VALUE="${actionBean.folderid}">
<INPUT TYPE="HIDDEN" NAME="searchresults" VALUE="${actionBean.searchresults}">
<INPUT TYPE="HIDDEN" NAME="newsearch" VALUE="${actionBean.newsearch}">
<input type="hidden" name="DOCINDEX" value="${actionBean.docindex}">
<input type="hidden" name="format" value="${actionBean.format}">

		<%-- ******************************************************************************* --%>
		<%-- RESULTS MANAGER include                                                         --%>
		<%-- ******************************************************************************* --%>
		<jsp:include page="../results/parts/resultsmanager.jsp">
			<jsp:param value="viewfolders" name="displaytype"/>
		</jsp:include>

			<div class="clear"></div>

			<div id="resultslist">
			<%--
				Iterate over results
			--%>
		<c:choose>
			<c:when test="${actionBean.view eq 'detailed'}"><jsp:include page="../results/parts/detailedformat.jsp"><jsp:param value="viewfolders" name="displaytype"/></jsp:include></c:when>
			<c:when test="${actionBean.view eq 'abstract'}"><jsp:include page="../results/parts/abstractformat.jsp"><jsp:param value="viewfolders" name="displaytype"/></jsp:include></c:when>
			<c:otherwise><jsp:include page="../results/parts/citationformat.jsp"><jsp:param value="viewfolders" name="displaytype"/></jsp:include></c:otherwise>
		</c:choose>
			</div>
		</form>
		</div><!--  end resultsarea -->
</div> <!--  end resultswrapper -->
<div class="clear"></div>
</c:if>
	</div> <!--  end selectedbox -->

	<div class="clear"></div>

	</stripes:layout-component>

	<stripes:layout-component name="jsbottom_custom">
	<%--
	 --%>
	<SCRIPT type="text/javascript" SRC="/static/js/ViewSavedFolders.js?v=${releaseversion}"></script>
	<SCRIPT type="text/javascript" SRC="/static/js/URLEncode.js?v=${releaseversion}"></script>
	<jwr:script src="/bundles/localholdinglinks.js"></jwr:script>
    <script type="text/javascript">
    GALIBRARY.createWebEventWithLabel('Folders', 'View Selected', 'Folder Name=${actionBean.folderName}; View=${actionBean.view}; ');
    </script>
    <SCRIPT type="text/javascript" SRC="/static/js/oneclick.js?v=${releaseversion}"></script>
	</stripes:layout-component>


</stripes:layout-render>
