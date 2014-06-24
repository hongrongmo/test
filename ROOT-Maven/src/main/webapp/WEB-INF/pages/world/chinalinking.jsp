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
    
    <div style="position:absolute; right:15px; top: 20px"><a href="javascript:window.close();"><img src="/static/images/close.gif" border="0"></a></div>
    
    <br/>
    <p>Full-text linking for this article is unavailable at this time.</p>
    
    <c:if test="${not empty actionBean.message}">
    <br/>
    <p>${actionBean.message}</p>
    <br/>
    </c:if>
    
    <c:if test="${not empty actionBean.linkingurls}">
    <div id="linkingurls">
    <ul>
    <c:forEach var="entry" items="${actionBean.linkingurls}">
    <li><a href="${entry.value}" title="${entry.key}">${entry.key}</a></li>
    </c:forEach>
    </ul>
    </div>
    </c:if>
    
    </div>

    <div class="clear"></div>

<br/>
</stripes:layout-component>

</stripes:layout-render>