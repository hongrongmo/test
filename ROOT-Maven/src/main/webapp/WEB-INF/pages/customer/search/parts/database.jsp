<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

			<div id="databasechkboxes" class="searchcomponentfullwrap">
				<h2 class="searchcomponentlabel" >DATABASE 
				<c:if test="${actionBean.context.userSession.user.userPreferences.saveDbSelection }">
					<img class="saveDatabases" title="Select your database preference to save as default" id="saveDBIcon" style="cursor:pointer;vertical-align:bottom;" src="/static/images/SaveSearch_off.png"/>
					
				</c:if>
				</h2> 
				<fieldset name="Database Checkboxes" >
				<c:choose>
				<c:when test="${fn:length(actionBean.databaseCheckboxes) == 1}">
				<div class="databasecheckall">
                    <input type="hidden" name="database" value="${actionBean.database}"></input>
                    <input type="checkbox" name="database" value="${actionBean.database}" checked="checked" id="${actionBean.databaseCheckboxes[0].id}" style="vertical-align: middle;" disabled="disabled">
					<label class="SmBlackText" for="${actionBean.databaseCheckboxes[0].id}"><strong>${actionBean.databaseCheckboxes[0].label}</strong></label>
				</div>
				</c:when>

				<c:otherwise>

				<div class="databasecheckall">
					<input type="checkbox"
					 value="${actionBean.alldbvalue}" id="allchkbx" class="databasechkbox"
					 style="vertical-align: middle;" name="alldb"<c:if test="${actionBean.database eq '0'}"> checked="checked"</c:if>/>
					<label class="SmBlackText" for="allchkbx">All</label>
				</div>

				<ul class="databasecheckgroup">
				<c:forEach items="${actionBean.databaseCheckboxes}" var="checkbox">
					<li>
						${checkbox}
					</li>
				</c:forEach>
				</ul>
				</c:otherwise>
				</c:choose>
				</fieldset>
				<div class="clear"></div>

			</div>
