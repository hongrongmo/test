<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld" prefix="stripes" %>


<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp" pageTitle="Engineering Village - Rename Folder">

	<stripes:layout-component name="csshead">
	<link href="/static/css/ev_folder.css?v=${releaseversion}" media="all" type="text/css" rel="stylesheet"></link>
	</stripes:layout-component>

	<stripes:layout-component name="contents">

	<div id="folders" class="paddingL15">
		<h1>Rename Folder</h1>
		<div class="hr"><hr/></div>

        <stripes:form action="/personal/folders.url" method="post" name="folderform">
            <input type="hidden" name="folderid" value="${actionBean.folderid}">
            <input type="hidden" name="oldfoldername" value="${actionBean.oldfoldername}">
<c:forEach items="${actionBean.folderlist}" var="folder">
            <input type="hidden" name="FOLDER-NAME" value="${folder.folderName}">
</c:forEach>
	        <div id="folderaction">
		        <p>Please enter the new folder name in the appropriate field and click "Save" button to save changes. </p>
		        <p><label for="oldfoldername">Current folder name:</label><span id="oldfoldername" class="currentfoldername"><b>${actionBean.oldfoldername}</b></span></p>
	            <p style="margin-bottom: 7px">
	                <label for="newfoldername" style="margin-top:4px">New folder name:</label><stripes:text name="foldername" size="28" maxlength="32"/>
	                <stripes:submit name="update" title="Rename folder" value="Save" class="button folderbtn" />
	                <stripes:submit name="cancelrename" title="Cancel action" value="Cancel"  class="button"/>
	            </p>
	        </div>
        </stripes:form>
	</div>

	</stripes:layout-component>

	<stripes:layout-component name="jsbottom_custom">
	    <SCRIPT type="text/javascript" SRC="/static/js/Folders.js?v=1"></SCRIPT>
        <script type="text/javascript">
        GALIBRARY.createWebEventWithLabel('Folders', 'Rename Folder', 'Folder name: ${actionBean.oldfoldername}');
        </script>
	</stripes:layout-component>

</stripes:layout-render>
