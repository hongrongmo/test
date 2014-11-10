<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld" prefix="stripes" %>

<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp" pageTitle="Engineering Village - Tags &amp; Groups">

	<stripes:layout-component name="csshead">
	<link href="/static/css/ev_tagsgroups.css?v=${releaseversion}" media="all" type="text/css" rel="stylesheet"></link>
	</stripes:layout-component>

	<stripes:layout-component name="contents">

	<div id="tagsgroups_content">
	<c:set var="subaction" scope="request">View/Edit Groups</c:set>
	<jsp:include page="parts/topnav.jsp"></jsp:include>

	<div id="tagsgroups_wrap">
	<form action="/tagsgroups/editgroups.url" method="GET" name="creategroupsform" id="creategroupsform">
		<input type="hidden" name="create" value="t"/>

		<div id="tagsgroups_action_col">
			<div class="backhome"><a href="/tagsgroups/display.url" title="Back to Tags &amp; Groups home">Back to Tags &amp; Groups home</a></div>
			<input type="submit" name="creategroup" title="Create a New Group" value="Create a New Group" class="button"></input>
		</div>

		<div id="tagsgroups_groupsbox">

		<ul class="errors" id="jserrors" style="color:black; margin-bottom: 5px">
		<stripes:errors>
		     <stripes:errors-header></stripes:errors-header>
		     <li><stripes:individual-error/></li>
		     <stripes:errors-footer></stripes:errors-footer>
		</stripes:errors>
		</ul>

		<h2 class="title">
			<span>Groups</span>
			<span style="padding: 0 5px">
				<a href="${actionBean.helpUrl}#view_edit_delete_groups.htm" class="helpurl" alt="Learn more about viewing, editing, and deleting groups" title="Learn more about viewing, editing, and deleting groups"><img src="/static/images/i.png" border="0" alt=""/></a>
			</span>
		</h2>
		<div class="toolbar" style="height: 5px"></div>
		<table id="groupstable" cellpadding="0" cellspacing="0" border="0">
		<thead>
		<tr>
			<th class="groupname">Name</th>
			<th class="groupcreate">Date</th>
			<th class="groupdescription">Description</th>
			<th class="groupmembers">Members</th>
			<th class="grouptags">Tags</th>
			<th class="groupedit">Edit</th>
			<th class="groupdelete" style="border-right:none">Delete</th>
		</tr>
		</thead>
		<tbody>
		<c:choose><c:when test="${empty actionBean.groups}">
		<tr class="groupsdisplay"><td colspan="7" style="border-right:none; line-height: 26px; vertical-align: middle">You have no Groups created.</td></tr>
		</c:when><c:otherwise>
		<c:forEach var="group" items="${actionBean.groups}" varStatus="status">
		<tr class="groupsdisplay">
			<td class="groupname"><span style="color:${group.colorByID.code}">${group.title}</span></td>
			<td class="groupcreate">${group.date}</td>
			<td class="groupdescription">${group.description}<c:if test="${empty group.description}">&nbsp;</c:if></td>
			<td class="groupmembers"><c:forEach var="member" items="${group.members}" varStatus="status"><c:if test="${not status.first}">, </c:if>${member.fullName}</c:forEach><c:if test="${empty group.members}">&nbsp;</c:if></td>
			<td class="grouptags"><c:forEach var="tag" items="${group.tags}" varStatus="status"><a href="/search/results/tags.url?CID=tagSearch&searchtype=TagSearch&SEARCHID=${tag.tag}&scope=${tag.scope}:${group.groupID}" title="" class="taglink" style="color:${group.colorByID.code}">${tag.tag}</a>&nbsp;</c:forEach><c:if test="${empty group.tags}">&nbsp;</c:if></td>
			<c:choose><c:when test="${group.ownerid eq actionBean.context.userid}">
			<td class="groupedit"><a href="/tagsgroups/editgroups.url?groupid=${group.groupID}&edit=t" title="Edit Group">Edit Group</a></td>
			<td class="groupdelete" style="border-right:none"><a href="/tagsgroups/editgroups.url?groupid=${group.groupID}&delete=t" class="groupdeletelink" title="Delete Group"><img src="/static/images/Delete.png"/></a></td>
			</c:when><c:otherwise>
			<td class="groupdelete" colspan="2" style="border-right:none; white-space: nowrap"><a href="/tagsgroups/editgroups.url?groupid=${group.groupID}&cancel=t" class="groupcancellink" title="Cancel Membership">Cancel Membership</a></td>
			</c:otherwise></c:choose>
		</tr>
		</c:forEach>
		</c:otherwise></c:choose>
		</tbody>
		</table>

		</div>

		</div>

		<div class="clear"></div>
		</div>

		<div class="clear"></div>
	</form>
	</div>

	</div>

	</stripes:layout-component>

	<stripes:layout-component name="jsbottom_custom">
	<script type="text/javascript" src="/static/js/TagsGroups.js?v=2"></script>
    <script type="text/javascript">
    GALIBRARY.createWebEvent('TagsGroups', 'View/Edit Groups');
    </script>
	</stripes:layout-component>

</stripes:layout-render>
