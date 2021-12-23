<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%-- <%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %> --%>
<div class="header">
	
	<div id="logoEV">
		<%-- <a href="${pageContext.servletContext.contextPath}/login.jsp" title="NYC Tools" >
			<img alt="NYC Tools" src="${pageContext.servletContext.contextPath}/static/images/EV-logo.gif" style="border:0;"/><img style="border:0;height: 35px;vertical-align: sub;"  alt="NYC Tools" src="${pageContext.servletContext.contextPath}/static/images/reports.jpg"/>
		</a> --%>
		<br>
		<br>
		<div style="float:right">
			<div style="margin-top:-20px;margin-right:20px">
				<%-- <c:if test="${not empty pageContext.request.remoteUser}"> --%>
				<%if(session.getAttribute("username") !=null && (session.getAttribute("Valid") !=null && session.getAttribute("Valid").equals("valid"))){ %>
					<span><b>Welcome <c:out value="${username}"/> <br/> <a href="#" title="Log out" onclick="document.logoutform.submit();">Log Out</a></b></span>
    			<form name="logoutform" class="form-inline" action="${pageContext.servletContext.contextPath}/Logout" method="post">
 					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
				</form>
				<%} %>
				<%-- </c:if> --%>
			</div>
			
		</div>
	</div>
	<br/>
	<div class="navigation txtLarger clearfix" aria-label="Engineering Village main navigation" role="navigation" style="z-index:300; text-align: left; padding-left: 4px;">
	<!-- <a title="Home" href="home.jsp" style="text-align:right;color: white">Home</a> -->
		<ul title="top level navigation" class="nav main">
		</ul>
		
	</div>
	
</div>
