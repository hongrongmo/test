<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld"
	prefix="stripes"%>

<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp"
	pageTitle="Engineering Village - Login">

    <stripes:layout-component name="csshead">
    <link href="/static/css/ev_personalaccount.css?v=${releaseversion}" media="all" type="text/css" rel="stylesheet"></link>
    </stripes:layout-component>

	<stripes:layout-component name="contents">

	<div class="marginL15 width850">
	<stripes:errors>
	     <stripes:errors-header><ul class="errors"></stripes:errors-header>
	     <li><stripes:individual-error/></li>
	     <stripes:errors-footer></ul></stripes:errors-footer>
	</stripes:errors>

	<c:if test="${not empty actionBean.message}">
	<div class="paddingT5">${actionBean.message}</div>
	</c:if>

    <c:choose><c:when test="${empty actionBean.context.carsresponse.pagecontent}">
    
    <h3>System Error</h3>
    <div class="hr" style="height: 2px; background-color: #D7D7D7; margin: 0 10px 10px 0"><hr></div>

	<p>Sorry, a system error has occurred, and your request cannot be completed.</p>
	<br/>
	
    </c:when>
    <c:otherwise>    
    ${actionBean.context.carsresponse.pagecontent}
    </c:otherwise>
    </c:choose>

</div>

	</stripes:layout-component>

	
</stripes:layout-render>