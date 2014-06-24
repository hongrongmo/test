<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>

<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld"
	prefix="stripes"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp" pageTitle="Engineering Village - Local Holdings Sent">

	<stripes:layout-component name="csshead" />

	<stripes:layout-component name="contents">

    <div class="clear"></div>
    
    <div class="paddingL10">
    <p>Your document request has been sent to 
    <c:choose>
        <c:when test="${empty actionBean.email}">library@lamrc.com</c:when>
        <c:otherwise>${actionBean.email}<input type="hidden" name="email" value="${actionBean.email}"/></c:otherwise>
    </c:choose>
    </p>
    
    <br/>
    
    <a href="javascript:window.close();"><img src="/static/images/close.gif" border="0" /></a>
    
    </div>

	</stripes:layout-component>

	<stripes:layout-component name="jsbottom_custom" />

</stripes:layout-render>