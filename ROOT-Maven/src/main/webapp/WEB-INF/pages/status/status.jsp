<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld" prefix="stripes" %>

<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp" pageTitle="Engineering Village - Main">

    <stripes:layout-component name="csshead">
    <jsp:include page="include/customcss.jsp"></jsp:include>
    </stripes:layout-component>

    <stripes:layout-component name="header">
        <jsp:include page="/WEB-INF/pages/include/headernull.jsp" />
    </stripes:layout-component>

    <stripes:layout-component name="ssourls"/>

<%-- **************************************** --%>
<%-- CONTENTS                                 --%>
<%-- **************************************** --%>
    <stripes:layout-component name="contents">
    <div id="container">

    <jsp:include page="include/tabs.jsp"/>

    <div style="float:right; padding-right: 10px"><a href="/status/clearcookies.url" title="Clear site cookies">Clear Cookies</a>&nbsp;&nbsp;|&nbsp;&nbsp;<a href="/status/expiresession.url" title="Expire Session">Expire Session</a></div>

    <div class="marginL10">

        <c:choose><c:when test="${empty actionBean.viewbean.webappproperties}">No webapp properties found!<br/></c:when>
        <c:otherwise>
        <h2>Web App Properties</h2>
        <table align="left" cellpadding="0" cellspacing="0" border="0" id="userinfo_table">
        <tbody>
        <c:forEach var="entry" items="${actionBean.viewbean.webappproperties}">
            <tr><td><b>${entry.name}</b></td><td>${entry.value}</td></tr>
        </c:forEach>
        </tbody>
        </table>
        </c:otherwise></c:choose>

        <c:choose><c:when test="${empty actionBean.viewbean.webappproperties}">No request properties found!<br/></c:when>
        <c:otherwise>
        <h2>Request Properties</h2>
        <table align="left" cellpadding="0" cellspacing="0" border="0" id="request_table">
        <tbody>
        <c:forEach var="entry" items="${actionBean.viewbean.requestproperties}">
            <tr><td><b>${entry.name}</b></td><td>${entry.value}</td></tr>
        </c:forEach>
        </tbody>
        </table>
        </c:otherwise></c:choose>

        <h2>Test Email mailhost</h2>
        <div style="padding: 4px; border: 1px solid black">
        <stripes:form action="/status/sendmail.url" name="sendmailform">
        <stripes:errors field="emailto"></stripes:errors>
        <stripes:errors field="emailfrom"></stripes:errors>
        <p>To: <stripes:text name="emailto"/></p>
        <p>From: <stripes:text name="emailfrom"/> </p>
        <p><stripes:submit name="sendmail" value="Email"/></p>
        </stripes:form>
        </div>
    </div>

    </div>

</stripes:layout-component>
<stripes:layout-component name="modal_dialog"></stripes:layout-component>
<stripes:layout-component name="sessionexpiryhandler"></stripes:layout-component>
</stripes:layout-render>