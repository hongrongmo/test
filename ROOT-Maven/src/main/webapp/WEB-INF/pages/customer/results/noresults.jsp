<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld"	prefix="stripes"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp" pageTitle="Engineering Village - Quick Search Results">

	<stripes:layout-component name="csshead">
	<link href="/static/css/ev_results.css?v=${releaseversion}"" media="all" type="text/css" rel="stylesheet"></link>
	<style type="text/css">
	</style>
	</stripes:layout-component>

	<stripes:layout-component name="contents">

    <c:set var="rerunactionurl" scope="request">
        <c:choose>
            <c:when test="${actionBean.searchtype eq 'Thesaurus'}">/search/results/thes.url</c:when>
            <c:when test="${actionBean.searchtype eq 'Book'}">/search/results/quick.url</c:when>
            <c:when test="${actionBean.searchtype eq 'Combined'}">/search/results/expert.url</c:when>
            <c:when test="${actionBean.searchtype eq 'Quick'}">/search/results/quick.url</c:when>
            <c:when test="${actionBean.searchtype eq 'Easy'}">/search/results/quick.url</c:when>
            <c:when test="${actionBean.searchtype eq 'Tags'}">/search/results/tags.url</c:when>
            <c:when test="${actionBean.searchtype eq 'Expert'}">/search/results/expert.url</c:when>
            <c:otherwise></c:otherwise>
        </c:choose>
    </c:set>    
	
	<div id="resultsbox"> 
	<%-- ******************************************************************************* --%>
	<%-- Top line is for the query display and search options (save search, alert, etc.) --%>
	<%-- ******************************************************************************* --%>
	<div id="resultsoptions">
	<jsp:include page="parts/querytoolbar.jsp"/>
	</div>

	
	<%-- ******************************************************************************* --%>
	<%-- Next start the results section, wrapper contains "refine" and "manager" areas   --%>
	<%-- as well as the list of results themselves                                       --%>
	<%-- ******************************************************************************* --%>
	<div id="resultswrapper" class="marginL10">
		<p><img title="No results were found" src="/static/images/No_results_found.png">&nbsp;&nbsp;<b>No results were found.</b></p>		
	
		<p style="margin-top:7px">&nbsp;&nbsp;Use Edit to modify your search.</p>		

	</div> <!--  end resultswrapper -->
	</div> <!--  end resultsbox -->	
	

	<div class="clear"></div>

	<br/>
	</stripes:layout-component>

	<stripes:layout-component name="jsbottom_custom">
	</stripes:layout-component>

	
</stripes:layout-render>
