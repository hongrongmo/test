<%@ page language="java" session="false" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld" prefix="stripes" %>
    
<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp" pageTitle="Engineering Village - Simulated IP status">

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
    
        <h2>Simulate IP</h2>

        <p>
            This page allows you to send in a simulated IP address in place of your current one.  It will do so by writing a cookie
            (SIMULATEDIP) to be used to retain this value.  
        </p>
        
        <p>
            After submitting a new value you can clear the cookie with the Clear button.  
        </p>
        
        <br/>
        
        <stripes:form action="/status.url" method="GET">
        <stripes:text name="txtsimulatedip" id="txtsimulatedid"/>
        <stripes:submit name="simulatedipsubmit" value="Update"></stripes:submit>
        <stripes:submit name="simulatedipclear" value="Clear"></stripes:submit>
        </stripes:form>
    
    </div>
    
    </div>    

    </stripes:layout-component>
    <stripes:layout-component name="modal_dialog"></stripes:layout-component>
    <stripes:layout-component name="sessionexpiryhandler"></stripes:layout-component>
</stripes:layout-render>