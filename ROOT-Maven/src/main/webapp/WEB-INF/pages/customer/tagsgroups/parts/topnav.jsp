<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
	
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
		<div id="tagsgroups_top"> 
			<ul id="tagsgroups_tool_links" class="horizlist" style="float:right">
				<li style="display:none" class="home<c:if test="${actionBean['class'].name ne 'org.ei.stripes.action.tagsgroups.TagsGroupsAction'}"> active</c:if>""><a title="Tags &amp; Groups">Tags &amp; Groups</a></li>
				<c:if test="${actionBean['class'].name ne 'org.ei.stripes.action.tagsgroups.ViewEditGroupsAction'}"><li class="vieweditgroups"><a title="View/Edit Groups" href="/tagsgroups/editgroups.url">View/Edit Groups</a></li></c:if>
				<c:if test="${actionBean['class'].name ne 'org.ei.stripes.action.tagsgroups.RenameTagsAction'}"><li class="renametag"><a title="Rename Tags" href="/tagsgroups/renametag.url">Rename Tags</a></li></c:if>
				<c:if test="${actionBean['class'].name ne 'org.ei.stripes.action.tagsgroups.DeleteTagsAction'}"><li class="deletetag" style="margin-right:0"><a title="Delete Tags" href="/tagsgroups/deletetag.url">Delete Tags</a></li></c:if>
			</ul>

			<h1>Tags &amp; Groups<c:if test="${actionBean['class'].name ne 'org.ei.stripes.action.tagsgroups.TagsGroupsAction'}">:</c:if><c:if test="${not empty subaction}">&nbsp;<span class="action_title">${subaction}</span></c:if></h1>
			
			<div class="clear"></div>
		</div>
		
		
		<div class="hr" style="height: 2px;margin: 0 0 7px"><hr/></div>
