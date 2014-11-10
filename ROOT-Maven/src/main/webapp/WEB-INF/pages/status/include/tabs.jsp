<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld" prefix="stripes" %>

	   	<stripes:errors>
             <div class="paddingL10"><img style="margin-bottom:5px" src="/static/images/red_warning.gif"><b>&nbsp;&nbsp;<stripes:individual-error /></b></div>
        </stripes:errors>
        <stripes:messages key="statusmessages"/>

        <ul id="tabs" class="tabnav">
            <li class='tab<c:if test="${actionBean.context.eventName eq '/sendmail' or actionBean.context.eventName eq 'home'}"> active</c:if>'><a href="/status/home.url"/>Status</a></li>
            <li class='tab<c:if test="${actionBean.context.eventName eq '/environment'}"> active</c:if>'><a href="/status/environment.url"/>Environment</a></li>
            <li class='tab<c:if test="${actionBean.context.eventName eq '/userinfo'}"> active</c:if>'><a href="/status/userinfo.url"/>User Info</a></li>
            <li class='tab<c:if test="${actionBean.context.eventName eq '/ticurl'}"> active</c:if>'><a href="/status/ticurl.url"/>TICURL</a></li>
            <li class='tab<c:if test="${actionBean.context.eventName eq '/searchwidget'}"> active</c:if>'><a href="/status/searchwidget.url"/>Search Widget</a></li>
            <!--
            <li class='tab<c:if test="${actionBean.context.eventName eq '/openxml'}"> active</c:if>'><a href="/status/openxml.url"/>Test OpenXML</a></li>
            <li class='tab<c:if test="${actionBean.context.eventName eq '/openrss'}"> active</c:if>'><a href="/status/openrss.url"/>Test OpenRSS</a></li>
            <li class='tab<c:if test="${actionBean.context.eventName eq '/openurl'}"> active</c:if>'><a href="/status/openurl.url"/>Test OpenURL</a></li>
             -->
        </ul>