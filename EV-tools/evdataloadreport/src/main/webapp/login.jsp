<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%-- <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> --%>
    
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
    <title>EV DataLoading Login Page</title>
    <link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/main.css"/>
    <link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/layout.css"/>
    <link rel="shortcut icon" href="${pageContext.servletContext.contextPath}/static/images/favicon.ico" type="image/x-icon"/>
	<link rel="icon" href="${pageContext.servletContext.contextPath}/static/images/favicon.ico" type="image/x-icon"/>
    <script type="text/javascript" src="${pageContext.servletContext.contextPath}/static/js/jquery-1.10.2.min.js"></script>
    <style>
    	table {border: 0px solid #ababab; float:none}
    	table td {border: 0px }
    </style>
</head>
  
  
<body>
	<%@ include file="views/includes/header.jsp" %>
  	<div class="maincontainer">
  		<div tiles:fragment="content">
  			<div class="innercontainer">
  				<c:if test="${not empty param.error}">
  				   <br />
				   <div class="paddingL10">
				   	 <img style="vertical-align:bottom" src="${pageContext.servletContext.contextPath}/static/images/red_warning.gif">
				   	 <b>&nbsp;&nbsp;Invalid username and password.</b>
				   </div>
				</c:if>
				<c:if test="${not empty param.logout}">
				   <br />
				   <span id="messagecontainer">You have been logged out.</span>
				   <br />
				</c:if>
				<br />
				<form name="f" action="${pageContext.servletContext.contextPath}/login" method="post">               
		            <fieldset>
		                <legend><b>Please Login</b></legend>
		                <table>
							<tr>
								<td><b>User:</b></td>
								<td><input type="text" id="username" name="username"/></td>
							</tr>
							<tr>
								<td><b>Password:</b></td>
								<td><input type="password" id="password" name="password"/></td>
							</tr>
							<tr>
								<td></td>
							    <td ><input name="submit" type="submit" value="Login" /></td>
							</tr>
					   </table>
		             </fieldset>
	            	<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
	        	</form>
	        	
	        	</br>
	        	</br>
			</div>
		</div>
	</div>
	
<div>
	<div>
		<div id="footer">
			<%-- <%@ include file="views/includes/footer.jsp" %> --%>
			
			<p id="copyright">Copyright &copy; &nbsp; <%= new java.util.Date().getYear() + 1900 %>&nbsp;<a
	title="Elsevier home page (opens in a new window)" target="_blank"
	href="http://www.elsevier.com">Elsevier B.V.</a> All rights reserved.
	
	<!-- JS works, but replacing it with java instead bc link not work -->
<%-- <script type="text/javascript" src="${pageContext.request.contextPath}/static/js/copyright.js"></script> --%>
	
</p>

		</div>
	</div>		
</div>						
</body>
</html>