<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
	
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld" prefix="stripes" %>
	
<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp" pageTitle="Engineering Village - Accept Invitations">

	<stripes:layout-component name="csshead"> 
	<link href="/static/css/ev_tagsgroups.css?v=${releaseversion}" media="all" type="text/css" rel="stylesheet"></link>
	</stripes:layout-component>

<c:if test="${actionBean.world}">
	<stripes:layout-component name="header">
	<jsp:include page="/WEB-INF/pages/include/headernull.jsp" />
	</stripes:layout-component> 
</c:if>

	<stripes:layout-component name="contents">
	
	<div id="tagsgroups_content">

	<ul class="errors" id="jserrors">
	<stripes:errors>
	     <stripes:errors-header></stripes:errors-header>
	     <li><stripes:individual-error/></li>
	     <stripes:errors-footer></stripes:errors-footer>
	</stripes:errors>
	</ul>
<c:choose>
<c:when test="${actionBean.world}">
	<p><b>Welcome to Engineering Village.</b></p>
	<br/>
	<p>You must <a href="/home.url" title="Login to Engineering Village">login to Engineering Village</a> through your institution to accept this invitation.</p>
	<p>Once you have logged in, you will need to login to you personal account or register for a personal account to join this group.</p>
</c:when>
<c:otherwise>
	<stripes:form action="/tagsgroups/invite.url" method="GET">
	<stripes:hidden name="groupid"/>
	
	<p style="margin: 7px 0">You have been invited to join the group <b style="color:${actionBean.group.colorByID.code}">${actionBean.group.title}</b></p>
	
	<stripes:submit name="accept" value="Accept Invitation" style="margin:0" class="button"/>&nbsp;&nbsp;|&nbsp;&nbsp;<a href="/home.url" title="Cancel">Cancel</a>
	
	</stripes:form>
</c:otherwise>
</c:choose>
	</div>
	
	</stripes:layout-component>

	<stripes:layout-component name="jsbottom_custom">
	<script type="text/javascript" src="/static/js/TagsGroups.js?v=2"></script>
	</stripes:layout-component>

</stripes:layout-render>
