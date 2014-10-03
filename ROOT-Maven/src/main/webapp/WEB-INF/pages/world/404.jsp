<%@ page language="java" session="false" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld"
	prefix="stripes"%>

<c:set var="pageTitle" value="Engineering Village - Bad Request" scope="request"/>

<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp">

<stripes:layout-component name="header">
<jsp:include page="/WEB-INF/pages/include/headernull.jsp" />
</stripes:layout-component> 

<stripes:layout-component name="ssourls"/>

<stripes:layout-component name="usersessiondump"/>

<stripes:layout-component name="contents">

	<div id="contentmain" style="margin-left:15px">
	<h1>Bad Request</h1>
	<p class="errorText">This request cannot be completed.  The resource you requested may be unavailable.</p> 
	<br/>
	</div>

</stripes:layout-component>

<stripes:layout-component name="jsbottom"/>

<stripes:layout-component name="jsbottom_custom"/>

<stripes:layout-component name="footer">
<jsp:include page="/WEB-INF/pages/include/copyright.jsp" />
</stripes:layout-component> 
<stripes:layout-component name="sessionexpiryhandler"></stripes:layout-component>
</stripes:layout-render>