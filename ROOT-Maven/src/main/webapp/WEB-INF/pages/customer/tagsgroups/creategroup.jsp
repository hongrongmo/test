<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld" prefix="stripes" %>

<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp" pageTitle="Engineering Village - Add Group">

	<stripes:layout-component name="csshead">
	<link href="/static/css/ev_tagsgroups.css?v=${releaseversion}" media="all" type="text/css" rel="stylesheet"></link>
	</stripes:layout-component>

	<stripes:layout-component name="contents">

	<div id="tagsgroups_content">

	<c:set var="subaction" scope="request"><c:choose><c:when test="${actionBean.CID eq 'tagEditGroup'}">View/Edit Groups</c:when><c:otherwise>Create a New Group</c:otherwise></c:choose></c:set>
	<jsp:include page="parts/topnav.jsp"></jsp:include>

	<ul class="errors" id="jserrors">
	<stripes:errors>
	     <stripes:errors-header></stripes:errors-header>
	     <li><stripes:individual-error/></li>
	     <stripes:errors-footer></stripes:errors-footer>
	</stripes:errors>
	</ul>


	<div id="tagsgroups_wrap">
	<stripes:form action="/tagsgroups/editgroups.url" method="POST" id="groupsform" name="groupsform" class="user-input-form">
        <input type="hidden" name="creategroup" value="t"/>
		<stripes:hidden name="groupid"/>

		<div id="tagsgroups_action_col">
			<p style="padding:0"><a href="/tagsgroups/display.url" title="Back to Tags &amp; Groups home">Back to Tags &amp; Groups home</a></p>
		</div>

		<div id="tagsgroups_groupsbox">
			<label for="groupname">Group Name:</label>
			<c:choose><c:when test="${actionBean.CID eq 'tagEditGroup'}">
			<p style="font-size: 14px; color:${actionBean.groups[0].colorByID.code}">${actionBean.groupname}</p>
			<stripes:hidden name="groupname" value="${actionBean.groupname}"></stripes:hidden>
			</c:when><c:otherwise>
			<stripes:text name="groupname" class="txtgroupname" value="${actionBean.groupname}"></stripes:text><span title="Required field" class="normal"> *</span>
			</c:otherwise></c:choose>
			<label for="groupdescription">Description:</label>
			<stripes:textarea name="groupdescription">${actionBean.groupdescription}</stripes:textarea>

			<label for="groupcolor">Color:</label>
			<stripes:select size="1" name="groupcolor">
			<c:forEach var="color" items="${actionBean.colors}"><option value="${color.ID}" style="color:${color.code}"<c:if test="${color.ID eq actionBean.groupcolor}"> selected='selected'</c:if>>${color.name}</option></c:forEach>
			</stripes:select>

<c:if test="${actionBean.CID eq 'tagEditGroup' and not empty actionBean.members and fn:length(actionBean.members) > 1}">
			<label for="member">Remove Members:</label>
	<c:forEach var="member" items="${actionBean.members}" varStatus="status"><c:if test="${actionBean.context.userid ne member.memberID}"><p class="removemember"><stripes:checkbox name="member" value="${member.memberID}"/>&nbsp;${member.fullName}</p></c:if></c:forEach>
</c:if>
			<label for="groupinvite">Invite Members (separate email addresses with commas):</label>
			<stripes:textarea name="groupinvite"></stripes:textarea>

			<br/>
			<br/>
			<c:choose><c:when test="${actionBean.context.eventName eq 'edit'}"><stripes:submit name="editsubmit" value="Save changes" title="Save changes" class="button"/></c:when><c:otherwise><stripes:submit name="createsubmit" value="Save changes" title="Save changes" class="button"/></c:otherwise></c:choose>

		</div>

		<div class="clear"></div>

	</stripes:form>
	</div>

	</div>
	</stripes:layout-component>

	<stripes:layout-component name="jsbottom_custom">
	<script type="text/javascript" src="/static/js/TagsGroups.js?v=2"></script>
    <script type="text/javascript">
    GALIBRARY.createWebEvent('TagsGroups', 'Create Group');
    </script>
	</stripes:layout-component>

</stripes:layout-render>
