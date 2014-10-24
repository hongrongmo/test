<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld" prefix="stripes" %>

<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp" pageTitle="Engineering Village - Rename Tags">

	<stripes:layout-component name="csshead">
	<link href="/static/css/ev_tagsgroups.css?v=${releaseversion}" media="all" type="text/css" rel="stylesheet"></link>
	</stripes:layout-component>

	<stripes:layout-component name="contents">

	<div id="tagsgroups_content">
	<c:set var="subaction" scope="request">Rename Tags</c:set>
	<jsp:include page="parts/topnav.jsp"></jsp:include>

	<ul class="errors" id="jserrors" style="color:black">
	<stripes:errors>
	     <stripes:errors-header></stripes:errors-header>
	     <li><stripes:individual-error/></li>
	     <stripes:errors-footer></stripes:errors-footer>
	</stripes:errors>
	</ul>

	<div id="tagsgroups_wrap">

	<form name="renametag" id="renametag" method="POST" action="/tagsgroups/renametag.url">
		<div id="tagsgroups_action_col">
			<div class="backhome"><a href="/controller/servlet/Controller?CID=tagsLoginForm" title="Back to Tags &amp; Groups home">Back to Tags &amp; Groups home</a></div>
		</div>
		<div id="rename_action_col">
			<div id="scopeOptiondiv">
				<div style="padding-bottom:5px;"><b>Choose Tag Category:</b></div>
				<select name="scopeOption" id="scopeOption">
	                  <option value="1" href="/tagsgroups/renametag.url?scopeOption=1" <c:if test="${actionBean.scopeOption eq '1'}"> selected="selected"</c:if>>Public</option>
	                  <option value="2" href="/tagsgroups/renametag.url?scopeOption=2" <c:if test="${actionBean.scopeOption eq '2'}"> selected="selected"</c:if>>Private</option>
	                  <option value="4" href="/tagsgroups/renametag.url?scopeOption=4" <c:if test="${actionBean.scopeOption eq '4'}"> selected="selected"</c:if>>My Institution</option>
	                  <c:forEach var="tgroup" items="${actionBean.tgroups}" varStatus="status">
						 <option value="${tgroup.groupID}" href="/tagsgroups/renametag.url?scopeOption=${tgroup.groupID}" <c:if test="${tgroup.groupID eq actionBean.scopeOption}"> selected="selected"</c:if>>${tgroup.title}</option>
	                  </c:forEach>
	            </select>
        	</div>
			<div style="padding-top:15px; padding-bottom:20px;">
			    <div style="padding-bottom:5px;"><b>Choose a Tag to Rename:</b></div>
				<c:choose>
					<c:when test="${not empty actionBean.taglabels}">
					<select name="oldtag" id="oldtag">
		                  <c:forEach var="rtag" items="${actionBean.taglabels}" varStatus="status">
							 <option value="${rtag}">${rtag}</option>
		                  </c:forEach>
		            </select>
		            </c:when>
		            <c:otherwise>
		               <div>No Tag Found.</div>
		            </c:otherwise>
	            </c:choose>
        	</div>
        	<c:if test="${not empty actionBean.taglabels}">
        	<div class="hr" style="color: #0156AA; background-color: #0156AA; height: 2px; width:50%;"><hr/></div>
			<div style="padding-top:15px;">
			    <div style="padding-bottom:5px;"><b>Rename Tag:</b></div>
				<div style="padding-bottom:15px;"><input size="30" name="newtag" id="newtag"/></div>
				<div><input type="submit" value="Rename Tag" title="Rename Tag" name="renametagsubmit" id="renametagsubmit" class="button"></input>
             	</div>
	        </div>
	       </c:if>
        </div>

		<div class="clear"></div>
	</form>
	</div>

	</div>

	</stripes:layout-component>

	<stripes:layout-component name="jsbottom_custom">
	<script type="text/javascript" src="/static/js/TagsGroups.js?v=2"></script>
    <script type="text/javascript">
    GALIBRARY.createWebEvent('TagsGroups', 'Rename Tag');
    </script>
	</stripes:layout-component>

</stripes:layout-render>
