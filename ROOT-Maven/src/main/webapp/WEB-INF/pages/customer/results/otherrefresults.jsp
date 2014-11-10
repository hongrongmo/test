<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8" trimDirectiveWhitespaces="true" %>
<%@ taglib uri="http://jawr.net/tags" prefix="jwr" %>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld"	prefix="stripes"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp" pageTitle="Engineering Village - Quick Search Results">

	<stripes:layout-component name="csshead">
	<link href="/static/css/ev_results.css?v=${releaseversion}"" media="all" type="text/css" rel="stylesheet"></link>
	
	</stripes:layout-component>

	<stripes:layout-component name="contents">
	
	
	<div id="resultsbox"> 

	
	<%-- ******************************************************************************* --%>
	<%-- Top line is for the query display and search options (save search, alert, etc.) --%>
	<%-- ******************************************************************************* --%>
	<div id="resultsoptions">
	
	<%-- ******************************************************************************* --%>
	<%-- QUERY DISPLAY AND TOOLBAR                                                       --%>
	<%-- ******************************************************************************* --%>
	
	<c:if test="${not empty actionBean.limitError}">
	  <div style="margin-top:10px; padding-left:10px; width:620px;"><img src="/static/images/red_warning.gif"/><b>&nbsp;You may only create ${actionBean.savedSeachesAndAlertsLimit} total saved searches and alerts. Please delete a search or alert to create a new one.</b></div>
	</c:if>
			<jsp:include page="parts/patentrefsummary.jsp">
				<jsp:param name="reftype" value="otherref"/>
			</jsp:include>
	
	<%-- ******************************************************************************* --%>
	<%-- PAGE NAVIGATION                                                                 --%>
	<%-- ******************************************************************************* --%>
	<stripes:errors><stripes:errors-header><div style="margin-top:10px; padding-left:10px; width:420px;"></stripes:errors-header><stripes:individual-error/><stripes:errors-footer></div></stripes:errors-footer></stripes:errors>
		<div class="clear"></div>
	</div>

	<%-- ******************************************************************************* --%>
	<%-- Next start the results section, wrapper contains "refine" and "manager" areas   --%>
	<%-- as well as the list of results themselves                                       --%>
	<%-- ******************************************************************************* --%>
 <div id="resultsarea" style="width:98.5%;margin: 0 0 0 7px">
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
		<input type="hidden" name="scope" value="">
		<INPUT TYPE="HIDDEN" NAME="backIndex" VALUE="${actionBean.pagenav.currentindex}">
		<INPUT TYPE="HIDDEN" NAME="basketCount" VALUE="${actionBean.basketCount}">
		
	
		
			<div id="resultslist">
			<%-- 
				Iterate over results 
			--%>
			<c:forEach items="${actionBean.results}" var="result" varStatus="status">
			<c:set var="resultnum" value="${(actionBean.pagenav.currentpage - 1) * actionBean.pagenav.resultsperpage + status.count}" />
			<div class="result<c:if test="${status.count % 2 eq 0}"> odd</c:if>">
				
				<div id="divresultnum">${result.index}.</div>
				<div id="divcbresult">&nbsp;</div>
	
				<%-- 
					Results item 
				--%>
				<div class="resultcontent">
				   <p class="authorwrap" style="line-height:16px;">
					<c:if test="${not result.nosource}"><b>&nbsp;Source:</b> 
						<c:choose>
							<c:when test="${empty result.source}">No source available</c:when>
							<c:otherwise><span><i>${result.source}</i></span></c:otherwise>
						</c:choose>
					</c:if>
				   </p>
				   <p>
				   <c:choose>
							<c:when test="${not empty result.cpxlink or not empty result.inspeclink}">${result.cpxlink}<c:if test="${not empty result.cpxlink and not empty result.inspeclink}">-</c:if>${result.inspeclink}</c:when>
							<c:otherwise>&nbsp;&nbsp;</c:otherwise>
						</c:choose>
				   	
			  		</p>
				</div>			
				
				<div class="clear"></div>
			</div>
			</c:forEach>
				
				
			</div>
		</form>
		</div><!--  end resultsarea -->	

	<div class="clear"></div>

	<br/>
	
	<div class="clear"></div>

	</div> <!--  end resultsbox -->	
	
	<div class="clear"></div>

	</stripes:layout-component>

	<stripes:layout-component name="jsbottom_custom">
<jwr:script src="/bundles/otherrefresults.js"></jwr:script>
<c:if test="${actionBean.showmap}">
    <script type="text/javascript" SRC="https://maps.googleapis.com/maps/api/js?key=AIzaSyCfQ_HSbcET25jn-cuT2Lz0CVycFnoGgZQ&sensor=false"></script>
	<script language="javascript" SRC="/static/js/showmap.js?v=${releaseversion}"></script>
</c:if>

	</script>	
	</stripes:layout-component>

	
</stripes:layout-render>
