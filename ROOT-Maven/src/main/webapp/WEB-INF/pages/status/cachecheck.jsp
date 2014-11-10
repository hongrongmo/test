<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<div style="border:1px solid #6C6" id="cachecheckpopup">
 <c:choose>
 	<c:when test="${not empty error}"> 
 		<span>${error}</span>
 	</c:when>
	<c:otherwise>
		
		<div>Key :<span>${actionBean.cacheKey}</span></div>
		<div>Retrieved Value :<span>${val}</span></div>
		<div>Expiry Date  :<span>${expiryDateSet}</span></div>
		<div>currentTime :<span>${currentTime}</span></div>
		<div>cacheUpdateStatus :<span>${cacheUpdateStatus}</span></div>
	</c:otherwise>
 </c:choose>  
</div>	
	
	