<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/main.css"/>
		<link rel="shortcut icon" href="${pageContext.servletContext.contextPath}/static/images/favicon.ico" type="image/x-icon"/>
		<link rel="icon" href="${pageContext.servletContext.contextPath}/static/images/favicon.ico" type="image/x-icon"/>
		<script type="text/javascript" src="${pageContext.servletContext.contextPath}/static/js/jquery-1.10.2.min.js"></script>
		<title>EV Tools - Simulated IP</title>
	</head>
	<body>
		<%@ include file="includes/header.jsp" %>
		<div class="maincontainer" id="container">
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
				<h2>Engineering Village - Simulated IP</h2>
				<p>
		            This page allows you to send in a simulated IP address in place of your current one. It will do so by writing a cookie (SIMULATEDIP) to be used to retain this value.
		        </p>
		        <p>
		            After submitting a new value you can clear the cookie with the Clear button.
		        </p>
				<div id="ddcontainer" >
					<div>
						<div id="updatediv" style="width:238px;float:left">
							<form action="${pageContext.servletContext.contextPath}/app/simulatedipsubmit" id="simulatedipsubmitform" onsubmit="return isValidForm()"  method="post">
							<input type="text" name="ip" id="ip" value="${simulatedip}"    />
							<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
							<button>Update</button>
							
						</form>
						</div>
						<div id="cleardiv"  style="float:left">
							<form action="${pageContext.servletContext.contextPath}/app/simulatedipclear" id="simulatedipclearform"  method="post">
								<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
								<button  id="clearbutton">Clear</button>
							</form>
						</div>
					</div>
				</div>
	  		</div>
		</div>
		<script type="text/javascript">
			function isValidForm(){
				var simulatedip = document.getElementById('ip').value;
				if(simulatedip == null || simulatedip.trim() == "" ){
					alert("The IP address cannot be empty.");
					return false;
				}
			}
			
		</script>
	</body>
</html>