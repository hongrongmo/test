<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
	
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld" prefix="stripes" %>
<%@ taglib uri="/WEB-INF/tlds/functions.tld" prefix="f" %>
	
<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp" pageTitle="Engineering Village - Accept Invitations">

	<stripes:layout-component name="csshead"> 
	<link href="/static/css/ev_tagsgroups.css?v=${releaseversion}" media="all" type="text/css" rel="stylesheet"></link>
	</stripes:layout-component>

	<stripes:layout-component name="contents">
	
	<div id="tagsgroups_content">

	<ul class="errors" id="jserrors">
	<stripes:errors>
	     <stripes:errors-header></stripes:errors-header>
	     <li><stripes:individual-error/></li>
	     <stripes:errors-footer></stripes:errors-footer>
	</stripes:errors>
	</ul>

	<p class="Bold">Your edit(s) have been saved.</p>

	<p>Note that your search results list may have changed based on your edits.</p>

	<p class="paddingT5">Return to <a href="/search/results/tags.url?CID=tagSearch&searchtype=TagSearch&SEARCHID=${actionBean.searchgrouptags}&scope=${actionBean.scope}" title="Return to earch results">search results</a> or go to <a href="/controller/servlet/Controller?CID=tagsLoginForm" title="Tags &amp; Groups home">Tags &amp; Groups home</a>.</p>

	</div>
	
	</stripes:layout-component>

	<stripes:layout-component name="jsbottom_custom">
	<script type="text/javascript" src="/static/js/TagsGroups.js?v=2"></script>
	</stripes:layout-component>

</stripes:layout-render>
