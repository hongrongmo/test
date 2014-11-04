<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<link rel="shortcut icon" href="${pageContext.servletContext.contextPath}/static/images/favicon.ico" type="image/x-icon"/>
		<link rel="icon" href="${pageContext.servletContext.contextPath}/static/images/favicon.ico" type="image/x-icon"/>
		<link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/main.css"/>
		<link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/jquery.dataTables-1.10.0.min.css"/>
		<script type="text/javascript" src="${pageContext.servletContext.contextPath}/static/js/jquery-1.10.2.min.js"></script>
		<script type="text/javascript" src="${pageContext.servletContext.contextPath}/static/js/jquery.dataTables-1.10.0.min.js"></script>
		<title>EV Tools - Edit Runtime Properties</title>
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
				<h2>Engineering Village - Edit Environment Properties</h2>
				<div id="ddcontainer" >
	  				<h4><span>Environment level : </span>
	  				<select name="envselect" id="envselect">
	                	<c:forEach var="entry" items="${envrunlevels}">
	                		<option value="${entry}" ${env == entry ? 'selected' : ''}>${entry}</option>
	               		</c:forEach>
	          		</select>
	          		</h4>
	  			</div>
	  			<div id="tblcontainer">
	   				<table  id="envprops_table">
					    <thead>
					        <tr>
					            <th>Key</th>
		                        <th>Default</th>
					            <th>Environment(${env})</th>
					            <th width="100px"></th>
		                    </tr>
					    </thead>
					    <tbody>
						    <c:choose>
							    <c:when test="${not empty envprops}">
								    <c:forEach var="entry" items="${envprops}" varStatus="counter">
								        <tr>
								            <td>${entry.key}</td>
								            <td><textarea style="width:100%;height:100%;max-width:100%;" disabled>${entry.dfault}</textarea></td>
								            <td><textarea style="width:100%;height:100%;max-width:100%;" id="keyvalue${counter.count+1}">${entry.currentEnvValue}</textarea></td>
								            <td><a href="#" onClick="saveKeyValue('${entry.key}','${env}','keyvalue${counter.count+1}');">Save</a> | <a href="#" onClick="removeKeyValue('${entry.key}','${env}');">Remove</a></td>
								        </tr>
								    </c:forEach>
							    </c:when>
							    <c:otherwise>
							        
							    </c:otherwise>
						    </c:choose>
					    </tbody>
				    </table>
	   			</div>
	   			<div id="formcontainer" >
    				<form:form method="POST" commandName="evform" id="mainForm">
		        		<form:hidden path="runtimepropkey" 		name="runtimepropkey" id="runtimepropkey"/>
		        		<form:hidden path="runtimepropkeyvalue" name="runtimepropkeyvalue" id="runtimepropkeyvalue"/>
		        		<form:hidden path="runtimepropenvlevel" name="runtimepropenvlevel" id="runtimepropenvlevel"/>
		        		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
		        	</form:form>
    			</div>
			</div>
			
			
   			
		</div>
	<script type="text/javascript">
	$(document).ready(function() {
      	 $('#envprops_table').dataTable({
               "aaSorting": [[0,"asc"]],
               "aoColumns": [{"bSortable":true,"sClass": "keystyle", "sWidth":"25%"}, {"bSortable":false,"bSearchable": false,"sWidth":"30%"},{"bSortable":false,"bSearchable": false,"sClass": "keyactionvalue","sWidth":"40%"}, {"bSortable":false,"bSearchable": false,"sClass": "keyaction","sWidth":"10%"}],
               "bStateSave": false,
               "bPaginate": false,
               "bAutoWidth": false,
               "bFilter": true,
               "bInfo":false,
               "oLanguage": { "sSearch": "Search Key:" },
               "dom": '<"toolbar">frtip'
           });
      	 
      	 $("#envselect").change(function(event) {
           window.location.href = "editruntimeprops?env="+$(this).val();
        });
      	 // Adding custom caption to table
      	 $("div.toolbar").html("Please avoid using 'ENTER' key if you dont want a next line character included while updating the data.");

      } );
	
	 function saveKeyValue(key,envvalue,textId){
  	   var keyvalue = $("#"+textId).val();
  	   if(keyvalue != null && keyvalue.trim() != ''){
  		   if (confirm("Are you sure to update the value?") == true) {
  			   document.getElementById('runtimepropkey').value = key;
	    		   document.getElementById('runtimepropkeyvalue').value = keyvalue;
	    		   document.getElementById('runtimepropenvlevel').value = envvalue;
	    		   document.getElementById('mainForm').action = 'updateruntimeproperties';
				   document.getElementById('mainForm').submit();
  		   }
  	   }else{
  		   alert("The value should not be null or empty!");
  	   }
     }
     
     function removeKeyValue(key,envvalue){
  	   if (confirm("Are you sure to delete the value?") == true) {
  		    document.getElementById('runtimepropkey').value = key;
	    		document.getElementById('runtimepropenvlevel').value = envvalue;
	    		document.getElementById('mainForm').action = 'removeruntimeproperties';
				document.getElementById('mainForm').submit();
  	   }
     }
   	
    </script>
	
	</body>
</html>