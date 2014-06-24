<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jawr.net/tags" prefix="jwr" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld" prefix="stripes" %>

<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp" pageTitle="Engineering Village - Tags &amp; Groups">

	<stripes:layout-component name="csshead">
	<link href="/static/css/ev_tagsgroups.css?v=${releaseversion}" media="all" type="text/css" rel="stylesheet"></link>
	</stripes:layout-component>

	<stripes:layout-component name="contents">

    <ul class="errors" id="jserrors" style="padding-left: 15px">
    <stripes:errors globalErrorsOnly="true">
         <stripes:errors-header></stripes:errors-header>
         <li><stripes:individual-error/></li>
         <stripes:errors-footer></stripes:errors-footer>
    </stripes:errors>
    </ul>

	<div id="tagsgroups_content">

	<jsp:include page="parts/topnav.jsp"></jsp:include>

		<div id="tagsgroups_wrap">

			<div id="tagsgroups_searchbox" class="shadowbox">
			<form id="searchtagsform" name="searchtagsform" action="/search/results/tags.url" method="GET">
				<input type="hidden" name="CID" value="tagSearch"/>
				<input type="hidden" name="searchtype" value="TagSearch"/>
				<input type="hidden" name="sort" value="${actionBean.sort}"/>

				<h2 id="searchtitle"><label for="searchgrouptags">Search Tags</label></h2>

				<select style="width:143px;margin-top:7px" name="scope" id="searchscopeselect">
<c:forEach items="${actionBean.scopes}" var="scope">${scope}</c:forEach>
<c:if test="${not actionBean.context.userLoggedIn}"><option value="6">Private/Groups</option></c:if>
				</select>

				<br/>
				<div style="vertical-align: middle; margin-top:7px">
				<input style="width:140px;" class="SmBlackText" size="20" name="SEARCHID" id="SEARCHID"/>
				<input type="submit" title="Search Tags" value="Search" class="button"/>
				</div>

			</form>
			<div id="autocompletewrap" style="width:142px; position:absolute"></div>

			</div>

			<div id="tagsgroups_tagdisplay">
				<div id="tagdisplay_menu">
				<form name="tagdisplayform" id="tagdisplayform" method="GET">
					<input type="hidden" name="scope" value="${actionBean.scope}"/>
					<input type="hidden" name="sort" value="${actionBean.sort}"/>

					<div style="float:left">View:
					<select id="cloudselect">
<c:forEach items="${actionBean.scopes}" var="scope">${scope}</c:forEach>
<c:if test="${not actionBean.context.userLoggedIn}"><option value="6">Private/Groups</option></c:if>
					</select>
					</div>

					<div style="float:right" id="sortby">
						<ul class="horizlist" style="float:left">
							<li class="sortby">Sort by:</li>
							<li><a href="/tagsgroups/display.url?sort=alpha&scope=${actionBean.scope}"<c:if test="${(empty actionBean.sort) or (actionBean.sort eq 'alpha')}"> style="color:#7a7a7a; text-decoration:none"</c:if>>Alphabetical</a>&nbsp;&nbsp;|</li>
							<li><a href="/tagsgroups/display.url?sort=size&scope=${actionBean.scope}&sort=${actionBean.sort}"<c:if test="${actionBean.sort eq 'size'}"> style="color:#7a7a7a; text-decoration:none"</c:if>>Popularity</a>&nbsp;&nbsp;|</li>
							<li><a href="/tagsgroups/display.url?sort=time&scope=${actionBean.scope}&sort=${actionBean.sort}"<c:if test="${actionBean.sort eq 'time'}"> style="color:#7a7a7a; text-decoration:none"</c:if>>Most Recent</a></li>
						</ul>
					</div>

					<div class="clear"></div>
				</form>
				</div>

				<div id="tagdisplay_cloud" style="text-align: justify">
				<c:choose><c:when test="${empty actionBean.tags}"><span>No tags found</span></c:when>
				<c:otherwise>
				<c:forEach items="${actionBean.tags}" var="cloudtag">
				<c:choose><c:when test="${not empty cloudtag.groupID}"><c:set var="scope">${cloudtag.scope}:${cloudtag.groupID}</c:set></c:when><c:otherwise><c:set var="scope">${cloudtag.scope}</c:set></c:otherwise></c:choose>
				<a class="cloudtag ${cloudtag.size}" style="color:${cloudtag.color}" href="/search/results/tags.url?CID=tagSearch&searchtype=TagSearch&SEARCHID=${cloudtag.tag}&scope=${scope}">${cloudtag.tag}</a>
				</c:forEach>
				</c:otherwise>
				</c:choose>
				</div>

			</div>

			<div class="clear"></div>
		</div>

	</div>


	</stripes:layout-component>

	<stripes:layout-component name="jsbottom_custom">
	<jwr:script src="/bundles/tagsgroups.js"></jwr:script>
    <script type="text/javascript">
    GALIBRARY.createWebEvent('TagsGroups', 'Home');
    </script>
	</stripes:layout-component>

</stripes:layout-render>
