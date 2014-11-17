<%@ page language="java" session="false" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld" prefix="stripes"%>
<%@ taglib uri="http://jawr.net/tags" prefix="jwr" %>
<c:set var="carsresponse" value="${actionBean.context.carsResponse}"/>
<c:set var="user" value="${actionBean.context.userSession.user}"/>
<c:set var="pathchoice" value="${carsresponse.templateName eq 'CARS_PATH_CHOICE'}"/>
<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp"
	pageTitle="${carsresponse.pageTitle}">

    <stripes:layout-component name="csshead">
    <link href="/static/css/ev_personalaccount.css?v=${releaseversion}" media="all" type="text/css" rel="stylesheet"></link>
    <style>
    	#stripes-messages ul{
    		list-style:none;
    		padding:0px;
    	}
    </style>
    </stripes:layout-component>

	<stripes:layout-component name="header">
	<c:choose>
	<c:when test="${user.customer}">
    <jsp:include page="/WEB-INF/pages/include/header.jsp" />
    </c:when>
    <c:otherwise>
    <jsp:include page="/WEB-INF/pages/include/headernull.jsp" />
    </c:otherwise>
    </c:choose>
	</stripes:layout-component>

	<stripes:layout-component name="contents">

	<div class="marginL15">
	<stripes:errors>
	     <stripes:errors-header><ul class="errors"></stripes:errors-header>
	     <li><stripes:individual-error/></li>
	     <stripes:errors-footer></ul></stripes:errors-footer>
	</stripes:errors>

	<c:if test="${not empty actionBean.message}">
	<div class="paddingT5">${actionBean.message}</div>
	</c:if>
	<div class="paddingT5"  id="stripes-messages">
	<stripes:messages/>
	</div>
    <c:choose>
    <%-- ************************************************************************************** --%>
    <%-- If pageContent is empty something is wrong! --%>
    <%-- ************************************************************************************** --%>
    <c:when test="${empty carsresponse.pageContent}">
    <h3>System Error</h3>
    <div class="hr" style="width:1015px;height: 2px; background-color: #D7D7D7; margin: 0 10px 10px 0"><hr></div>
	<p>Sorry, a system error has occurred, and your request cannot be completed.</p>
	<br/>
    </c:when>

    <%-- ************************************************************************************** --%>
    <%-- Otherwise just show the transformed CARS template via the pageContent --%>
    <%-- ************************************************************************************** --%>
    <c:otherwise>
    ${carsresponse.pageContent}
    </c:otherwise>
    </c:choose>

</div>

	</stripes:layout-component>
<stripes:layout-component name="carsoverride">
<jwr:style src="/bundles/carsoverride.css"></jwr:style>
</stripes:layout-component>
<stripes:layout-component name="modal_dialog_msg"/>
<stripes:layout-component name="modal_dialog"></stripes:layout-component>
<stripes:layout-component name="modal_dialog_2"></stripes:layout-component>
<stripes:layout-component name="exitSurvey"></stripes:layout-component>

</stripes:layout-render>
