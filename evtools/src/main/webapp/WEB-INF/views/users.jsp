<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/main.css"/>
		<link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/jquery.dataTables-1.10.0.min.css"/>
		<script type="text/javascript" src="${pageContext.servletContext.contextPath}/static/js/jquery-1.10.2.min.js"></script>
		<script type="text/javascript" src="${pageContext.servletContext.contextPath}/static/js/jquery.dataTables-1.10.0.min.js"></script>
		<title>EV Tools - User List</title>
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
				<h2>Engineering Village - Users Details</h2>
				<div id="tblcontainer">
	   				<table  id="users_table">
					    <thead>
					        <tr>
					            <th>Index</th>
		                        <th>User Name</th>
		                        <th>Status</th>
					            <th>Roles</th>
		                    </tr>
					    </thead>
					    <tbody>
						    <c:choose>
							    <c:when test="${not empty userslist}">
								    <c:forEach var="entry" items="${userslist}" varStatus="counter">
								        <tr>
								            <td>${counter.count+1}</td>
								            <td>${entry.username}</td>
								            <td>${entry.enabled}</td>
								            <td>${entry.userRolesText}</td>
								        </tr>
								    </c:forEach>
							    </c:when>
							    <c:otherwise>
							    </c:otherwise>
						    </c:choose>
					    </tbody>
				    </table>
					<br />
	   			</div>
	   		</div>
		</div>
		<script type="text/javascript">
		$(document).ready(function() {

		    	 $('#users_table').dataTable({
		    		 "aaSorting": [[1,"asc"]],
		             "aoColumns": [{"bSortable":false,"bSearchable": false},null, {"bSortable":false,"bSearchable": false}, {"bSortable":false,"bSearchable": false}],
		             "bStateSave": false,
		             "bPaginate": false,
		             "bAutoWidth": false,
		             "bFilter": true,
		             "bInfo":false,
		             "fnDrawCallback": function ( oSettings ) {
		     			/* Need to redo the counters if filtered or sorted */
		     			if ( oSettings.bSorted || oSettings.bFiltered )
		     			{
		     				for ( var i=0, iLen=oSettings.aiDisplay.length ; i<iLen ; i++ )
		     				{
		     					$('td:eq(0)', oSettings.aoData[ oSettings.aiDisplay[i] ].nTr ).html( i+1 );
		     				}
		     			}
		     		}
		     		
	
		         });
	
		   

	      } );
		
	    </script>
	
	</body>
</html>