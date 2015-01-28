<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8" trimDirectiveWhitespaces="true" session="false"%>
<%@ taglib uri="http://jawr.net/tags" prefix="jwr" %>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld"	prefix="stripes"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp" pageTitle="Engineering Village - ${actionBean.searchtype} Search Results">

	<stripes:layout-component name="csshead">
	<jwr:style src="/bundles/results.css"></jwr:style>

	</stripes:layout-component>
<stripes:layout-component name="SkipToNavigation">
	<a class="skiptonavlink" href="#cbresult1" onclick="$('#cbresult1').focus();return false;" title="Skip to First Result">Skip to First Result</a><br/>
	<a class="skiptonavlink" href="#limittobtn" onclick="$('#limittobtn').focus();return false;" title="Skip to Refine Results">Skip to Refine Results</a><br/>
</stripes:layout-component>
	<stripes:layout-component name="contents">

	<c:set var="rerunactionurl" scope="request">
		<c:choose>
			<c:when test="${actionBean.searchtype eq 'Thesaurus'}">/search/results/thes.url</c:when>
			<c:when test="${actionBean.searchtype eq 'Book'}">/search/results/quick.url</c:when>
			<c:when test="${actionBean.searchtype eq 'Combined'}">/search/results/expert.url</c:when>
			<c:when test="${actionBean.searchtype eq 'Quick'}">/search/results/quick.url</c:when>
			<c:when test="${actionBean.searchtype eq 'Easy'}">/search/results/quick.url</c:when>
			<c:when test="${actionBean.searchtype eq 'TagSearch'}">/search/results/tags.url</c:when>
			<c:when test="${actionBean.searchtype eq 'Expert'}">/search/results/expert.url</c:when>
			<c:otherwise></c:otherwise>
		</c:choose>
	</c:set>

	<div id="resultsbox">


	<%-- ******************************************************************************* --%>
	<%-- Top line is for the query display and search options (save search, alert, etc.) --%>
	<%-- ******************************************************************************* --%>
	<div id="resultsoptions">

	<%-- ******************************************************************************* --%>
	<%-- QUERY DISPLAY AND TOOLBAR                                                       --%>
	<%-- ******************************************************************************* --%>

	<stripes:errors field="limitError"><stripes:individual-error/></stripes:errors>

	<c:choose>
		<c:when test="${actionBean.dedup}">
			<jsp:include page="parts/dedupsummary.jsp"/>
		</c:when>
		<c:when test="${actionBean.patentref}">
			<jsp:include page="parts/patentrefsummary.jsp">
				<jsp:param name="reftype" value="patentref"/>
			</jsp:include>
		</c:when>
		<c:otherwise>
			<jsp:include page="parts/querytoolbar.jsp"/>
		</c:otherwise>
	</c:choose>


	<div style="display: none; padding: 0; margin:0" id="map">
		<p style="background-color: #148C75; border-bottom: 2px solid #ADAAAD;margin: 0; color: white; font-size: 14px" id="maptitle">
	    	<img style="float:right;cursor:pointer;margin-right:5px" src="/static/images/remove.jpg" onclick="javascript:togglemap();"/>
			Geographic Map
		</p>
	    <p style="margin-bottom: 7px">
	    	<img style="width: 16px; height: 16px; margin-right: 5px" src="/static/images/red-pin.png" />
	    	<span>Click on a pin to refine search results</span>
	    </p>

		<div class="clear"></div>

	    <div id="map_canvas" style="clear:right; width: 600px; height: 300px;">&#160;</div>

	    <p style="margin-top: 7px"><a class="SpLink" id="resetcenter" href="javascript:resetCenterAndZoom()">Reset Map Center and Zoom</a></p>
	</div>


	<%-- ******************************************************************************* --%>
	<%-- PAGE NAVIGATION                                                                 --%>
	<%-- ******************************************************************************* --%>

	<c:choose>
		<c:when test="${actionBean.dedup}">
			<jsp:include page="parts/DedupPagenav.jsp">
				<jsp:param name="position" value="top"/>
			</jsp:include>
		</c:when>
		<c:when test="${actionBean.patentref}">
			<jsp:include page="parts/patentrefPagenav.jsp">
			<jsp:param name="position" value="patent"/>
			</jsp:include>
		</c:when>
		<c:otherwise>
			<jsp:include page="parts/pagenav.jsp">
				<jsp:param name="position" value="top"/>
			</jsp:include>
		</c:otherwise>
   </c:choose>

		<div class="clear"></div>

  <%-- ******************************************************************************* --%>
  <%-- NAVIGATORS include                                                              --%>
  <%-- ******************************************************************************* --%>
  <c:choose>
	  <c:when test="${actionBean.patentref}">
		<jsp:include page="parts/navigators.jsp">
			<jsp:param name="display" value="patent"/>
		</jsp:include>
	  </c:when>
	  <c:otherwise>
		  <c:if test="${not(actionBean.dedup)}">
		  <jsp:include page="parts/navigators.jsp">
		  	<jsp:param name="display" value="other"/>
		  </jsp:include>
		  </c:if>
	  </c:otherwise>
   </c:choose>

   <div <c:choose><c:when test="${actionBean.dedup}">id = "dudupresultsarea"</c:when><c:otherwise>id="resultsarea"</c:otherwise></c:choose> >
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
		<input type="hidden" name="scope" value="">
		<INPUT TYPE="HIDDEN" NAME="backIndex" VALUE="${actionBean.pagenav.currentindex}">
		<INPUT TYPE="HIDDEN" NAME="basketCount" VALUE="${actionBean.basketCount}">
        <INPUT TYPE="HIDDEN" NAME="shownewsrchalert" VALUE="${actionBean.shownewsrchalert}">
        <INPUT TYPE="HIDDEN" NAME="showmaxalert" VALUE="${actionBean.showmaxalert}">
        <INPUT TYPE="HIDDEN" NAME="showmaxalertclear" VALUE="${actionBean.showmaxalertclear}">
		<%-- <INPUT TYPE="HIDDEN" NAME="tracksearchid" VALUE="${actionBean.tracksearchid}"> --%>
		<%-- <INPUT TYPE="HIDDEN" NAME="otherbasketcount" VALUE="${actionBean.prevSrchBasketCount}"> --%>


		<%-- ******************************************************************************* --%>
		<%-- RESULTS MANAGER include                                                         --%>
		<%-- ******************************************************************************* --%>

		<div class="shadowbox">
		<c:choose>
		<c:when test="${actionBean.patentref}">
		<jsp:include page="parts/patentrefResultsmanager.jsp">
			<jsp:param value="patentrefresults" name="displaytype"/>
			</jsp:include>
		</c:when>
		<c:otherwise>
		<jsp:include page="parts/resultsmanager.jsp">
			<jsp:param value="quickresults" name="displaytype"/>
			</jsp:include>
		</c:otherwise>
		</c:choose>

		<c:choose>
			<c:when test="${actionBean.patentref}">
			<jsp:include page="parts/resultslist.jsp">
				<jsp:param name="displaytype" value="patentref"/>
			</jsp:include>
			</c:when>
			<c:otherwise>
			 <jsp:include page="parts/resultslist.jsp">
			 	<jsp:param name="displaytype" value="selectedrecords"/>
			 </jsp:include>
			</c:otherwise>
		</c:choose>
		</div>

		</FORM>
		</div><!--  end resultsarea -->

	<br/>
	<div class="clear"></div>

	<%-- ******************************************************************************* --%>
	<%-- PAGE NAVIGATION                                                                 --%>
	<%-- ******************************************************************************* --%>
	<c:choose>
		<c:when test="${actionBean.dedup}">
			<jsp:include page="parts/DedupPagenav.jsp">
				<jsp:param name="position" value="bottom"/>
			</jsp:include>
		</c:when>
		<c:when test="${actionBean.patentref}">
			<jsp:include page="parts/patentrefPagenav.jsp">
			<jsp:param name="position" value="patent"/>
			</jsp:include>
		</c:when>
		<c:otherwise>
			<jsp:include page="parts/pagenav.jsp">
				<jsp:param name="position" value="bottom"/>
			</jsp:include>
		</c:otherwise>
   </c:choose>

	<div class="clear"></div>

	</div> <!--  end resultsbox -->

	<div class="clear"></div>

	</stripes:layout-component>

	<stripes:layout-component name="jsbottom_custom">
	<jwr:script src="/bundles/results.js"></jwr:script>


 <c:if test="${actionBean.showmap}">
	<script language="javascript" SRC="/static/js/showmap.js?v=${releaseversion}"></script>
</c:if>

<c:forEach var="comment" items="${actionBean.comments}"><!-- ${comment} -->
</c:forEach>
	</stripes:layout-component>

<stripes:layout-component name="survey">
	<c:set var="surveyLocation" value="results" scope="request"/>
	<%@ include file="/WEB-INF/pages/include/survey.jsp" %>
</stripes:layout-component>
</stripes:layout-render>
