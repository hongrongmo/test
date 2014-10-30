 <ul id="tabs" class="tabnav">
     <li class='tab<c:if test="${pagetype eq 'editruntimeprops'}"> active</c:if>'><a href="${pageContext.servletContext.contextPath}/app/editruntimeprops">Edit (ENV)Properties</a></li>
     <li class='tab<c:if test="${pagetype eq 'googledriveusage'}"> active</c:if>'><a href="${pageContext.servletContext.contextPath}/app/googledriveusage">Google Drive usage</a></li>
     <li class='tab<c:if test="${pagetype eq 'ipblocker'}"> active</c:if>'><a href="${pageContext.servletContext.contextPath}/app/ipblocker">IP Blocker</a></li>
     <sec:authorize access="hasRole('ROLE_ADMIN')">
     	<li class='tab<c:if test="${pagetype eq 'users'}"> active</c:if>'><a href="${pageContext.servletContext.contextPath}/admin/users">Users</a></li>
     </sec:authorize>
 </ul>