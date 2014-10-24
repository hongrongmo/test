<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>

<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld"	prefix="stripes"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp" pageTitle="Engineering Village - Quick Search Results">

	<stripes:layout-component name="csshead">
	<link href="/static/css/ev_results.css?v=${releaseversion}"" media="all" type="text/css" rel="stylesheet"></link>
	</stripes:layout-component>

	<stripes:layout-component name="contents">

	<div id="dedupbox"  class="paddingL15">

	<h1>Remove Duplicates</h1>
	<hr class="divider"/>
	
	<p>Duplicate records will be removed from the first 1000 records in the result set. Use the form below to choose the fields and the database that you prefer to see results from.</p>

	<form name="removedup" action="/search/results/dedup.url" method="POST">
		<input type="hidden" name="CID" value="dedup"/>
		<input type="hidden" name="EISESSION" value="${actionBean.sessionid}"/>
		<input type="hidden" name="SEARCHID" value="${actionBean.searchid}"/>
		<input type="hidden" name="COUNT" value="${actionBean.count}"/>
		<input type="hidden" name="database" value="${actionBean.database}"/>
		
		<div id="fieldpreferences" style="float:left;margin-right:60px">
		<p><b>Field Preferences:</b>&nbsp;<a href="${actionBean.helpUrl}#Deduplication_feature.htm#Fld_pref" alt="Learn more about field preferences" title="Learn more about field preferences" class="helpurl"><img
						src="/static/images/i.png" border="0" class="infoimg" align="top" alt=""/></a></p>
		<ul class="fieldpreferenceslist">
		  <li>
		    <input type="radio" id="rdo0" name="fieldpref" title="No field preference" value="0"<c:if test="${actionBean.fieldpref eq 0}"> checked="checked"</c:if>/>
		    <label for="rdo0">No field preference</label>
		  </li>
		  <li>
		    <input type="radio" id="rdo4" name="fieldpref" title="Has Full Text" value="4"<c:if test="${actionBean.fieldpref eq 4}"> checked="checked"</c:if>/>
		    <label for="rdo4">Has Full Text</label>
		  </li>
		  <li>
		    <input type="radio" id="rdo1" name="fieldpref" title="Has Abstract" value="1"<c:if test="${actionBean.fieldpref eq 1}"> checked="checked"</c:if>/>
		    <label for="rdo1">Has Abstract</label>
		  </li>
		  <li>
		    <input type="radio" id="rdo2" name="fieldpref" title="Has Index Terms" value="2"<c:if test="${actionBean.fieldpref eq 2}"> checked="checked"</c:if>/>
		    <label for="rdo2">Has Index Terms</label>
		  </li>
		</ul>
		</div>
		
		<div id="dbpreferences" style="float:left">
		<p><b>Database Preferences:</b>&nbsp;<a href="${actionBean.helpUrl}#DB_pref_Remove_dups.htm" alt="Learn more about database preferences" title="Learn more about database preferences" class="helpurl"><img
						src="/static/images/i.png" border="0" class="infoimg" align="top" alt=""/></a></p>
		<p>
			<select id="dbpref" name="dbpref" title="Database Preferences Dropdown">
			  <c:forEach items="${actionBean.databaseprefs}" var="dbpref">
			    <option value="${dbpref.value}"<c:choose><c:when test="${dbpref.selected}">selected="selected"</c:when><c:when test="${dbpref.value eq 'cpx'}">selected="selected"</c:when><c:otherwise></c:otherwise></c:choose>>${dbpref.label}</option>
			  </c:forEach>
			</select>
			
		</p>
		<p>
			<input type="submit" value="Continue" title="Continue Submit Button"/>
		</p>
        </div>
        
        <div class="clear"></div>
	</form>
	</div>
	
	</stripes:layout-component>
	
</stripes:layout-render>