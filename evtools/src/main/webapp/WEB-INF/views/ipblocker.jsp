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
		<title>EV Tools - IP Blocker</title>
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
				<h2>Engineering Village - IP Blocker<span style="font-size:18px;color:red"> ${environment}</span><span style="font-size:20px"> [ Total : ${totalCount} ]</span>
				</h2>
				<p>
		            This page allows you to add/remove/modify the IP's for blocking purpose. <span style="font-weight:bold">You may need to wait for maximum 10 minutes to get the changes applied to the system ins selected environment.</span>
		        </p>
				<div id="ddcontainer" >
				
					<form action="${pageContext.servletContext.contextPath}/app/addip" id="mainForm"  method="post">
						<input type="text" name="ip" id="iptext" value=""  />
						<button >Add</button>
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
					</form>
					
				</div>
	  			<div id="tblcontainer">
	   				<table  id="blockedips_table">
					    <thead>
					        <tr>
					            <th>IP</th>
		                        <th>Status</th>
		                        <th>Description</th>
					            <th>Last Modified</th>
		                        <th>Account Name</th>
					            <th></th>
					        </tr>
					    </thead>
					    <tbody>
						    <c:choose>
							    <c:when test="${not empty blockedIpsList}">
								    <c:forEach var="entry" items="${blockedIpsList}">
								        <tr>
								            <td>${entry.IP}</td>
								            <c:choose>
								            	 <c:when test="${entry.blocked}">
								            	 	<td align="center"><a href="#" onClick="updateIp('${entry.IP}',false);">Blocked</a></td>
								            	 </c:when>
								            	 <c:otherwise>
											        <td align="center"><a href="#" onClick="updateIp('${entry.IP}',true);">Unblocked</a></td>
											    </c:otherwise>
											</c:choose>
											<td style="width:150px">
								            	<a href="#" onClick="getIPHistory('${entry.IP}',event);">Show History</a>
								            </td>
		                                    <td>${entry.timestamp}</td>
		                                    <td>${entry.accountName}</td>
								            <td align="center">
								            	<a href="#" onClick="removeIp('${entry.IP}');">Remove</a>
								            </td>
		
								        </tr>
								    </c:forEach>
							    </c:when>
							    <c:otherwise>
							    </c:otherwise>
						    </c:choose>
					    </tbody>
				    </table>
				    <br />
				    <form action="${pageContext.servletContext.contextPath}/app/updateip" id="updateform"  method="post">
						<input type="hidden" name="ip" id="ipupdateform"  />
						<input type="hidden" name="blocked" id="blockedudateform"  />
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
					</form>
					<form action="${pageContext.servletContext.contextPath}/app/removeip" id="removeform"  method="post">
						<input type="hidden" name="ip" id="ipremoveform"  />
						<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
					</form>
				</div>
	   		</div>
		</div>
		<script type="text/javascript">
			$(document).ready(function() {
		    	 $('#blockedips_table').dataTable({
		             "aaSorting": [[3,"desc"]],
		             "aoColumns": [null, null, null, null,{"bSortable":false}, {"bSortable":false}],
		             "bStateSave": false,
		             "bPaginate": false,
		             "bAutoWidth": false,
		             "bFilter": true,
		             "bInfo":false
		         });
	
		    } );
			
			function updateIp(blockedIp, enableflag){
				document.getElementById('ipupdateform').value = blockedIp;
				document.getElementById('blockedudateform').value = enableflag;
				document.getElementById('updateform').submit();
			}
			
			function removeIp(blockedIp){
				document.getElementById('ipremoveform').value = blockedIp;
				document.getElementById('removeform').submit();
			}
		
			function getIPHistory(blockedIp,e){
		   	 	$('#iphistorypopup').remove();
		   	   	params = {};
				$.get("iphistory?ip="+blockedIp,
		               params,
		               function (html) {
							$(html).css({
						        position: "absolute",
						        background:"#CFC",
						        top: e.pageY,
						        left: e.pageX
						      }).appendTo('#container');
		               });
				 e.preventDefault();
			}
		    function closeIPHistory(){
		   	 	$('#iphistorypopup').remove();
		   	}
			
			
	    </script>
	
	</body>
</html>