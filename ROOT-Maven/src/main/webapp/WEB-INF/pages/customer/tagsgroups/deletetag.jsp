<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld" prefix="stripes" %>

<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp" pageTitle="Engineering Village - Delete Tags">

	<stripes:layout-component name="csshead">
	<link href="/static/css/ev_tagsgroups.css?v=${releaseversion}" media="all" type="text/css" rel="stylesheet"></link>
	</stripes:layout-component>

	<stripes:layout-component name="contents">

	<div id="tagsgroups_content">
	<c:set var="subaction" scope="request">Delete Tags</c:set>
	<jsp:include page="parts/topnav.jsp"></jsp:include>

	<ul class="errors" id="jserrors">
	<stripes:errors>
	     <stripes:errors-header></stripes:errors-header>
	     <li><stripes:individual-error/></li>
	     <stripes:errors-footer></stripes:errors-footer>
	</stripes:errors>
	</ul>

	<div id="tagsgroups_wrap">
	<form name="deletetag" id="deletetag" method="POST" action="/tagsgroups/deletetag.url">
		<div id="tagsgroups_action_col">
			<div class="backhome"><a href="/tagsgroups/display.url" title="Back to Tags &amp; Groups home">Back to Tags &amp; Groups home</a></div>
		</div>
		<div id="deletetag_action_col">
			<div id="scopeOptiondiv">
				<div style="padding-bottom:5px;"><b>Choose Tag Category:</b></div>
				<select name="scopeOption" id="scopeOption">
	                  <option value="1" href="/tagsgroups/deletetag.url?scopeOption=1" <c:if test="${actionBean.scopeOption eq '1'}"> selected="selected"</c:if>>Public</option>
	                  <option value="2" href="/tagsgroups/deletetag.url?scopeOption=2" <c:if test="${actionBean.scopeOption eq '2'}"> selected="selected"</c:if>>Private</option>
	                  <option value="4" href="/tagsgroups/deletetag.url?scopeOption=4" <c:if test="${actionBean.scopeOption eq '4'}"> selected="selected"</c:if>>My Institution</option>
	                  <c:forEach var="tgroup" items="${actionBean.tgroups}" varStatus="status">
						 <option value="${tgroup.groupID}" href="/tagsgroups/deletetag.url?scopeOption=${tgroup.groupID}" <c:if test="${tgroup.groupID eq actionBean.scopeOption}"> selected="selected"</c:if>>${tgroup.title}</option>
	                  </c:forEach>
	            </select>
        	</div>
			<div style="padding-top:15px;">
			    <div style="padding-bottom:5px;"><b>Choose a Tag to Delete:</b></div>
				<c:choose>
					<c:when test="${not empty actionBean.taglabels}">
					<select name="deletetag" id="deletetag">
		                  <c:forEach var="dtag" items="${actionBean.taglabels}" varStatus="status">
							 <option value="${dtag}">${dtag}</option>
		                  </c:forEach>
		            </select>
		            </c:when>
		            <c:otherwise>
		               <div>No Tag Found.</div>
		            </c:otherwise>
	            </c:choose>
        	</div>
        	<c:if test="${not empty actionBean.taglabels}">
			<div style="padding-top:15px;">
				<input type="submit" value="Delete Tag" title="Delete Tag" name="deletetagsubmit" id="deletetagsubmit" class="button" style="width: 110px; padding:0;"></input>
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
    GALIBRARY.createWebEvent('TagsGroups', 'Delete');
    </script>
	</stripes:layout-component>

</stripes:layout-render>
