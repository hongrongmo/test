<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld"
    prefix="stripes"%>

<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp">

<stripes:layout-component name="header">
<jsp:include page="/WEB-INF/pages/include/headernull.jsp" />
</stripes:layout-component>

<stripes:layout-component name="contents">

    <div id="contentmain" style="margin-left:15px">
    <c:if test="${not empty errorXml}">
    <!-- ************************************************************************************************* -->
    <!-- Error Code:    ${errorXml.errorCode}  -->
    <!-- Error Message: ${errorXml.errorMessage}  -->
    <c:if test="${not empty errorXml.baseExceptionClass}"><!-- Base Exception Class: ${errorXml.baseExceptionClass} --></c:if>
    <c:if test="${not empty errorXml.baseExceptionMessage}"><!-- Base Exception Message: ${errorXml.baseExceptionMessage} --></c:if>
    <c:if test="${not empty errorXml.originExceptionClass}"><!-- Origin Exception Class: ${errorXml.originExceptionClass} --></c:if>
    <c:if test="${not empty errorXml.originExceptionMessage}"><!-- Origin Exception Message: ${errorXml.originExceptionMessage} --></c:if>
    <!-- ************************************************************************************************* -->
    </c:if>
    <h3>System Error</h3>
    <div class="hr" style="height: 2px; background-color: #D7D7D7; margin: 0 10px 10px 0"><hr/></div>
    <c:choose>
    <c:when test="${not empty actionBean.message}">
        <p>${actionBean.message}</p>
    </c:when>
    <c:otherwise>
        <p>Sorry, a system error has occurred, and your request cannot be completed.</p>
    </c:otherwise>
    </c:choose>
    <br/>
    <p>You may <a title="Contact and support (opens in a new window)" href="${contactuslink}" class="evpopup" target="_blank">contact us</a> to report this problem, or you may begin a <a class="newsearch" title="Run a new search" href="/search/quick.url?CID=quickSearch">new search.</a></p>
    </div>
</stripes:layout-component>

</stripes:layout-render>