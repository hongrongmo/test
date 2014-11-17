<%@page import="org.ei.domain.DatabaseConfig"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://jawr.net/tags" prefix="jwr" %>	
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://elsevier.com/stripesext.tld" prefix="stripesext" %>
<%-- 
NOTE:  Caller is expected to insert the "active" page:
		<c:set var="searchtab" value="[page name]" scope="request"></c:set>
 --%>
<!--[if IE 6]>
<style type="text/css">
#searchformtabs LI {width:146px}
#searchformsidebar .sidebartitle {
	width:100%;
	height: 30px;
	margin-top: 10px;
}
</style>
<![endif]--> 

<c:set var="userprefs" value="${actionBean.context.userSession.user.userPreferences}"/>

<div id="searchformtabsbox">
	<ul id="searchformtabs"  aria-label="Search forms" role="navigation">
		<li<c:if test="${searchtab != 'quicksearch'}"> class="inactive"</c:if>>${searchtab eq 'quicksearch' ? '<h1 class="link">' : ''}<a href="/search/quick.url?CID=quicksearch" 
			  id="searchtablink" class="tablink<c:if test="${searchtab == 'quicksearch'}"> here</c:if>" title="Quick Search">Quick Search</a>${searchtab eq 'quicksearch' ? '</h1>' : ''}</li>
		<li<c:if test="${searchtab != 'expertsearch'}"> class="inactive"</c:if>>${searchtab eq 'expertsearch' ? '<h1 class="link">' : ''}<a href="/search/expert.url?CID=expertsearch" 
			   class="tablink<c:if test="${searchtab == 'expertsearch'}"> here</c:if>" title="Expert Search">Expert Search</a>${searchtab eq 'expertsearch' ? '</h1>' : ''}</li>
<c:if test="${userprefs.thesaurus and actionBean.thesaurusEnabled}">
		<li<c:if test="${searchtab != 'thessearch'}"> class="inactive"</c:if>>${searchtab eq 'thessearch' ? '<h1 class="link">' : ''}<a href="/search/thesHome.url?CID=thessearch#init" 
			   class="tablink<c:if test="${searchtab == 'thessearch'}"> here</c:if>" title="Thesaurus Search">Thesaurus Search</a>${searchtab eq 'thessearch' ? '</h1>' : ''}</li>
</c:if>
<c:if test="${actionBean.ebookEnabled}">
		<li<c:if test="${searchtab != 'ebooksearch'}"> class="inactive"</c:if>>${searchtab eq 'ebooksearch' ? '<h1 class="link">' : ''}<a href="/search/ebook.url?CID=ebooksearch&database=<%= DatabaseConfig.PAG_MASK %>" 
			   class="tablink<c:if test="${searchtab == 'ebooksearch'}"> here</c:if> ebookFeatureHighlight" title="eBook Search">eBook Search </a>${searchtab eq 'ebooksearch' ? '</h1>' : ''}</li>
	</c:if>
	</ul>
</div>
