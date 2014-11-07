<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<title>System Error Occured</title>
		<link rel="shortcut icon" href="${pageContext.servletContext.contextPath}/static/images/favicon.ico" type="image/x-icon"/>
		<link rel="icon" href="${pageContext.servletContext.contextPath}/static/images/favicon.ico" type="image/x-icon"/>
		<link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/main.css"/>
	</head>
	<body>
	<%@ include file="includes/header.jsp" %>
		<div class="maincontainer">
			<%@ include file="includes/tabs.jsp" %>
			<div class="innercontainer">
				<c:if test="${not empty error}">
				   <div class="paddingL10">
				   	 <img style="vertical-align:bottom" src="${pageContext.servletContext.contextPath}/static/images/red_warning.gif"><b>&nbsp;&nbsp;${error}</b>
				   </div>
				</c:if>
				<c:if test="${not empty message}">
				   <span id="messagecontainer">${message}</span>
				</c:if>
			</div>
		</div>
	</body>
</html>