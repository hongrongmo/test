<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld" prefix="stripes" %>
    
<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp" pageTitle="Engineering Village - User Info status">

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

        <c:choose><c:when test="${empty actionBean.viewbean.usersession or empty actionBean.viewbean.usersession.user}">No user info found!</c:when>
        <c:otherwise>
        
        <h2>User Information</h2>

        <c:set var="user" scope="request" value="${actionBean.viewbean.usersession.user}"/>
        <table align="left" cellpadding="0" cellspacing="0" border="0" id="userinfo_table">
        <tbody>
            <tr><td><b>Web User ID: </b></td><td>${user.webUserId}</td></tr>
            <tr><td><b>Username: </b></td><td>${user.username}</td></tr>
            <tr><td><b>Customer ID: </b></td><td>${user.customerID}</td></tr>
            <tr><td><b>Contract ID: </b></td><td>${user.contractID}</td></tr>
            <tr><td><b>Entry Token: </b></td><td>${user.entryToken}</td></tr>
            <tr><td><b>Start Page: </b></td><td>${user.startPage}</td></tr>
            <tr><td><b>Default DB: </b></td><td>${user.defaultDB}</td></tr>
            <tr><td><b>Email: </b></td><td>${user.email}</td></tr>
            <tr><td><b>Cartridge: </b></td><td>${user}</td></tr>
            <tr><td colspan="2"><b>Account information</b></td></tr>
            <tr><td><b>Name: </b></td><td>${user.account.accountName}</td></tr>
            <tr><td><b>Number: </b></td><td>${user.account.accountNumber}</td></tr>
            <tr><td><b>Id: </b></td><td>${user.account.accountId}</td></tr>
        </tbody>
        </table>
        </c:otherwise></c:choose>
        
        <h2>Customer images</h2>
	    <p>
            <select name="custimageselect" id="custimageselect">
                <option value="">Please select an image...</option>
                 <c:forEach var="entry" items="${actionBean.viewbean.customerImages}">
                 	<option value="${actionBean.viewbean.customerImagePath}${entry}">${entry}</option>
                 </c:forEach>
            </select>
        </p>
        <div id="custimagetoggle" style="display:none">
            <div class="custimagetitle marginL10">No styling applied:</div>
            <div id="imagenostyle">
                <img border="0"/>
            </div>
            <br/>
            
        </div>

    </div>
    
    </div>    

    <script type="text/javascript">
    $(document).ready(function() {
        // Handle select change
        $("#custimageselect").change(function(event) {
            var imgname = $(this).val();
            if (!imgname || imgname == '') {
                $("#custimagetoggle").hide();
                $("#custom-logo-li img").hide();
            } else {
                // Show image area and images
                $("#custimagetoggle").show();
                $("#imagenostyle img").attr('src',$(this).val());
                $("#custom-logo-li").show();
                $("#custom-logo-img").attr('src',$(this).val());
            }
        });
    });
    </script>

    </stripes:layout-component>
    <stripes:layout-component name="sessionexpiryhandler"></stripes:layout-component>
</stripes:layout-render>