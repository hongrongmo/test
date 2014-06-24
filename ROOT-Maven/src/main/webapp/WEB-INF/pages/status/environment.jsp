<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld" prefix="stripes" %>
<%@page import="org.ei.config.EVProperties"%>
    
<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp" pageTitle="Engineering Village - Environment">

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
    
    <div class="marginL10">
<%-- 	<stripes:form action="/status/updateprop.url"> --%>
<!-- 		<div> -->
<!-- 			<label>Property Key: </label><input name="propKey" type="text"/> -->
<!-- 			<label>Property Value: </label><input name="propValue" type="text"/> -->
<!-- 			<input type="submit" value="submit"/><br/> -->
<!-- 			<span style="font-weight:bold;color:red;">This will only change the values for the current instance you're on.</span> -->
<!-- 		</div> -->
<%-- 	</stripes:form> --%>
    <c:choose><c:when test="${empty actionBean.viewbean.runtimeproperties}">No runtime properties found!</c:when>
    <c:otherwise>
    <h2>Runtime Properties for '<%= EVProperties.getRuntimeProperties().getRunlevel() %>'</h2>
    <table align="left" cellpadding="0" cellspacing="0" border="0" id="userinfo_table">
    <tbody>
    <c:forEach var="key" items="${sortedkeys}">
        <tr><td><b>${key}</b></td><td>${actionBean.viewbean.runtimeproperties[key]}</td></tr>
    </c:forEach>
    </tbody>
    </table> 
    </c:otherwise></c:choose>
    
    </div>
    
    </div>    

    </stripes:layout-component>
    
</stripes:layout-render>