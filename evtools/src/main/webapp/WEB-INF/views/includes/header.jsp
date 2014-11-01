<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<div class="header">
	
	<div id="logoEV">
		<a href="${pageContext.servletContext.contextPath}/app/editruntimeprops.htm" title="Engineering Village Tool" >
			<img alt="Engineering Village Tool" src="${pageContext.servletContext.contextPath}/static/images/EV-logo.gif" style="border:0;"/><img style="border:0;height: 40px;vertical-align: sub;"  alt="Engineering Village Tool" src="${pageContext.servletContext.contextPath}/static/images/tools.jpg"/>
		</a>
		<div style="float:right">
			<div style="margin-top:20px;margin-right:20px">
				<c:if test="${not empty pageContext.request.remoteUser}">
					<span><b>Welcome <c:out value="${pageContext.request.remoteUser}"/> <br/> <a href="#" title="Log out" onclick="document.logoutform.submit();">Log Out</a></b></span>
    			<form name="logoutform" class="form-inline" action="${pageContext.servletContext.contextPath}/logout" method="post">
 					<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
				</form>
				</c:if>
			</div>
			
		</div>
	</div>
	<br/>
	<div class="navigation txtLarger clearfix">
		<ul title="top level navigation" class="nav main">
		</ul>
	</div>
	
</div>
