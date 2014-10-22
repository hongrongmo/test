<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld"
	prefix="stripes"%>

<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp">

<stripes:layout-component name="header">
<jsp:include page="/WEB-INF/pages/include/headernull.jsp" />
</stripes:layout-component>

<stripes:layout-component name="contents">

	<div id="contentmain" style="margin-left:15px">
<c:choose>
<%-- ************************************************************* --%>
<%-- GENERAL ERROR                                                 --%>
<%-- ************************************************************* --%>
<c:when test="${empty actionBean.context.eventName or actionBean.context.eventName eq 'error'}">
    <!--Error Message: <c:if test="${null != actionBean.exceptionMessage}">${actionBean.exceptionMessage}</c:if> -->
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
</c:when>
<%-- ************************************************************* --%>
<%-- INVALID CID                                                   --%>
<%-- ************************************************************* --%>
<c:when test="${actionBean.context.eventName eq 'invalidcid'}">
    <h3 align="left">Unauthorized request/Expired session</h3>
    <p>Either the page you requested does not exist, or you are not authorized to view it. Your session may have also expired; please
    <a href="/home.url?CID=home" class="jsforward">click here</a>
    to return to Engineering Village.</p>
</c:when>
<%-- ************************************************************* --%>
<%-- TOKEN CID                                                     --%>
<%-- ************************************************************* --%>
<c:when test="${actionBean.context.eventName eq 'token'}">
    <h3 align="left">This feature is no longer available.</h3>
    <p>Please <a href="/home.url?CID=home" class="jsforward">click here</a> to return to Engineering Village.</p>
</c:when>
<%-- ************************************************************* --%>
<%-- END SESSION                                                   --%>
<%-- ************************************************************* --%>
<c:when test="${actionBean.context.eventName eq 'endsession'}">
	<h3>Session Ended</h3>
	<p>You have ended your Engineering Village search session. To return to Engineering Village, please click on link below.</p>
	<br/>
	<p><a href="/home.url?CID=home" class="jsforward">Begin a new session</a></p>

	<c:if test="${actionBean.daypass}">
	<br/>
	<br/>
	<p><b>Day Pass Customers</b></p>
	<br/>
	<br/>
	<p>To begin a new session please return to your <a title="Store Account" href="javascript:forwardLink('https://store.engineeringvillage.com/ppd/myaccount.do')">store account</a> and click on an active Day Pass link.</p>
	</c:if>

	<SCRIPT>
    function clearCookies() {
        document.cookie = 'EISESSION=0; expires=Thu, 01-Jan-70 00:00:01 GMT;';
      }

      // Clear cookies on load...
      $(document).ready(clearCookies);
	</SCRIPT>
</c:when>
<%-- ************************************************************* --%>
<%-- WHOAMI                                                         --%>
<%-- ************************************************************* --%>
<c:when test="${actionBean.context.eventName eq 'whoami'}">
<p>
<br/>
${actionBean.message}
</p>
</c:when>
</c:choose>


	</div>

	<div class="clear"></div>

<br/>

</stripes:layout-component>
<stripes:layout-component name="modal_dialog"></stripes:layout-component>
<stripes:layout-component name="sessionexpiryhandler"></stripes:layout-component>
</stripes:layout-render>