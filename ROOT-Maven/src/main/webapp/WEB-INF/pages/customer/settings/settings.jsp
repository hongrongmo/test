<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
	
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld" prefix="stripes" %>
<%@ taglib uri="/WEB-INF/tlds/functions.tld" prefix="f" %>
	
<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp" pageTitle="Engineering Village - Settings">

	<stripes:layout-component name="csshead"> 
	<link href="/static/css/ev_settings.css?v=${releaseversion}" media="all" type="text/css" rel="stylesheet"></link>
	</stripes:layout-component>

	<stripes:layout-component name="contents">
	<c:set var="backurl">CID=myprofile&database=${actionBean.database}</c:set>
	<c:set var="deletenexturl">CID=deletePersonalAccount&database=${actionBean.database}</c:set>
	<c:set var="passwordnexturl">CID=changePassword&database=${actionBean.database}</c:set>
	<c:set var="editnexturl">CID=editPersonalAccount&database=${actionBean.database}</c:set>
	
	<div id="mysettings" class="paddingL15">
		<h1>Settings</h1>
	
		<div class="hr" style="color: #E8E8E8; height: 2px; margin: 0px 7px 7px 0"><hr/></div>
	
		<ul>
		    <li>
				<a href="/personalaccount/edit/display.url?CID=editPersonalAccount&database=${actionBean.database}" title="Modify personal details">Modify personal details</a>
		        <p>Manage your account details.</p>
		    </li>
<c:if test="${actionBean.context.userLoggedIn}">
		    <li>
				<a href="/personalaccount/password/display.url?CID=changePassword&database=${actionBean.database}" title="Change your password">Change Password</a>
		        <p>Change your account password.</p>
		    </li>
</c:if>
			<li>
				<a href="/personal/savesearch/display.url" title="Manage your saved searches">View/Update Saved Searches & Alerts</a>
				<p>Manage your saved searches and email alerts.</p>
			</li>
			<li>
				<a href="/personal/folders/view.url?CID=viewPersonalFolders&database=${actionBean.database}" title="View, rename or delete your folders">View/Update Folders </a>
				<p>View, rename or delete your folders.</p>
			</li>
<c:if test="${actionBean.context.userLoggedIn}">
			<li>
				<a href="/personalaccount/edit/delete.url?CID=deletePersonalAccount&database=${actionBean.database}" title="Remove your personal account" id="removelink">Remove Account</a>
				<p>Remove your personal account.</p>
			</li>
</c:if>
		</ul>

	</div>
	
	</stripes:layout-component>
	
	<stripes:layout-component name="jsbottom_custom">
	
	<script type="text/javascript">
	$(document).ready(function() {
		$("#removelink").click(function(event) {
			var go = confirm("Are you sure you want to remove your personal account from the system?");
			if (!go) event.preventDefault();
		});
	});
	</script>
	</stripes:layout-component>	
	
</stripes:layout-render>
