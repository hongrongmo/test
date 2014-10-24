<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld" prefix="stripes" %>
    
<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp" pageTitle="Engineering Village - Usage">


    <stripes:layout-component name="csshead">
    <jsp:include page="include/customcss.jsp"></jsp:include>
    <link type="text/css" rel="stylesheet" href="/static/css/custom-theme/jquery.dataTables.css"/>
    <style type="text/css">
        h2 {font-size: 14px; margin: 3px; padding: 0}
            #usagestats_table {width:740px;border:1px solid black;}
            #usagestats_table_wrapper {width: 750px} 
            #usagestats_table_wrapper input {margin: 5px 0}  
    </style>
    </stripes:layout-component>
    
    <stripes:layout-component name="header">
        <jsp:include page="/WEB-INF/pages/include/headernull.jsp" />
    </stripes:layout-component>
    
<%-- **************************************** --%>  
<%-- CONTENTS                                 --%>  
<%-- **************************************** --%>  
    <stripes:layout-component name="contents">
    
    <div id="container">
    
    <jsp:include page="include/tabs.jsp"/>
    
    <div class="marginL10">

    <c:set var="usagestats" value="${usagestats}" scope="request"/>
    
    <c:choose>
    <c:when test="${empty usagestats}">
    <h2>UsageStats object not found in session!</h2>
    </c:when>
    
    <c:otherwise>
    <h2>Total Sessions: ${usagestats.totalsessions}</h2>
    <h2>Total Errors: ${usagestats.totalerrors}</h2>
    <h2>Stats since: ${usagestats.timestamp}</h2>
    
    <table align="left" cellpadding="0" cellspacing="0" border="0" id="usagestats_table">
    <thead>
        <tr>
            <th>IP</th>
            <th>Sessions</th>
            <th>IP Auth</th>
            <th>UNPW Auth</th>
            <th>SHIB Auth</th>
            <th>TICURL Auth</th>
            <th>FAILED IP Auth</th>
            <th>FAILED Auth</th>
            <th>Errors</th>
        </tr>
    </thead>
    <tbody>
    <c:choose>
    <c:when test="${not empty usagestats.ipmap}"> 
    <c:forEach var="entry" items="${usagestats.ipmap}">
        <c:set var="usagebyip" value="${entry.value}"/>
        <tr>
            <td>${entry.key}</td>
            <td>${usagebyip.sessions}</td>
            <td>${usagebyip.authip}</td>
            <td>${usagebyip.authunpw}</td>
            <td>${usagebyip.authshib}</td>
            <td>${usagebyip.authticurl}</td>
            <td>${usagebyip.authfailip}</td>
            <td>${usagebyip.authfail}</td>
            <td>${usagebyip.errors}</td>
        </tr>
    </c:forEach>
    </c:when>
    <c:otherwise>
        <tr><td colspan="9">NO ENTRIES!</td></tr>
    </c:otherwise>
    </c:choose>  
    
    </tbody>
    </table>
        
    </c:otherwise>
    </c:choose>
    
    <div class="clear"></div>
    <br/>
    
    <stripes:form method="POST" action="/status/clearusage.url">
        <stripes:submit name="clearusage" value="Clear"/>
    </stripes:form>
    </div>
    
    </div>    

    </stripes:layout-component>
    
    <stripes:layout-component name="jsbottom">
    <c:if test="${not empty usagestats.ipmap}">
    <script type="text/javascript" src="/static/js/jquery/jquery.dataTables.min.js"></script>
    <script type="text/javascript">
    $(document).ready(function() {
        $('#usagestats_table').dataTable({
            "aaSorting": [[1,"desc"]],
            "aoColumns": [{"bSortable":false}, null, null, null, null, null, null, null, null],
            "bStateSave": false,
            "bPaginate": false,
            "bAutoWidth": false
        });
    } );
    </script>
    </c:if>
    
    </stripes:layout-component>
</stripes:layout-render>