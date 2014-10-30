<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>System Error Occured</title>
		<link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/main.css"/>
	</head>
	<body>
	<%@ include file="includes/header.jsp" %>
		<div class="maincontainer">
			<div class="innercontainer">
<%-- 				<c:if test="${not empty error}"> --%>
				   <div class="paddingL10" style="padding-top:20px">
				   	 <img style="vertical-align:bottom" src="${pageContext.servletContext.contextPath}/static/images/red_warning.gif"><b>&nbsp;&nbsp;Access is denied - You do not have permission to access the requested resource.</b>
				   </div>
<%-- 				</c:if> --%>
<%-- 				<c:if test="${not empty message}"> --%>
				   <span id="messagecontainer">${message}</span>
<%-- 				</c:if> --%>
			</div>
		</div>
	</body>
</html>