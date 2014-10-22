<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
	<head>
		<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
		<link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/main.css"/>
	 	<link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/jquery.ui.thems.smoothness.css"/>
		<link type="text/css" rel="stylesheet" href="${pageContext.servletContext.contextPath}/static/css/jquery.dataTables-1.10.0.min.css"/>
		<script type="text/javascript" src="${pageContext.servletContext.contextPath}/static/js/jquery-1.10.2.min.js"></script>
		<script type="text/javascript" src="${pageContext.servletContext.contextPath}/static/js/jquery-ui.js"></script>
		<script type="text/javascript" src="${pageContext.servletContext.contextPath}/static/js/jquery.ui.datepicker.min.js"></script>
		<script type="text/javascript" src="${pageContext.servletContext.contextPath}/static/js/jquery.dataTables-1.10.0.min.js"></script>
		<title>EV Tools - Google Drive Usage</title>
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
				<h2>Engineering Village - Google Drive Usage<span style="font-size:20px"> [ Total Downloads : ${totalCount} ]</span></h2>
				<p>This page allows you to view usage of save to google drive functionality.</p>
				<div id="ddcontainer" >
					<input type="hidden" value="${usageOption}" id="usageOption" />
	  				<form:form  method="POST" commandName="googledriveusageform" id="mainForm" action="${pageContext.servletContext.contextPath}/app/googledriveusage" onsubmit="return isValidForm()">
			        	<div>
			        		<form:radiobutton id="downloadformat" path="usageOption" name="usageOption" value="downloadformat"  /><label for="downloadformat">Download Format</label>
			        		<form:radiobutton id="ip" path="usageOption" name="usageOption" value="ip"  /><label for="ip">IP</label>
			        		<form:radiobutton id="acctNo" path="usageOption" name="usageOption" value="acctNo"  /><label for="acctNo">Account Number</label>
							<button >Go</button>
			        		<br>
			        		<br>
			        		<input type="checkbox" id="dateSearch" name="dateRange" /><label for="dateSearch">Use Date Filter</label> Start Date: <form:input type="text" disabled="true" path="startDate" name="startDate" id="datepicker1" /> End Date: <form:input  path="endDate"  type="text" disabled="true" name="endDate" id="datepicker2" />
			        		<input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
			        	</div>
			        </form:form>
	  			</div>
	  			<div id="tblcontainer">
	   				<c:choose>
					    <c:when test="${not empty dateQueryinfo}">
					    	<br/>
						   <div style="padding-left:5px;color:red">${dateQueryinfo}</div>
					    </c:when>
					    <c:otherwise>
	
					    </c:otherwise>
				    </c:choose>
			    	<table  id="usage_table">
					    <thead>
					        <tr>
					            <th style="width:100px">Index</th>
					            <th>
						    	<c:choose>
								    <c:when test="${usageOption eq 'downloadformat'}">
						                Download Format
							        </c:when>
								    <c:when test="${usageOption eq 'ip'}">
									    IP
								    </c:when>
								    <c:when test="${usageOption eq 'acctNo'}">
									    Account Number
								    </c:when>
								    <c:otherwise>
					        		</c:otherwise>
				    			</c:choose>
				    			</th>
				    			<th>Download Count</th>
							</tr>
						</thead>
					    <tbody>
						    <c:choose>
							    <c:when test="${not empty usageData}">
								    <c:forEach var="entry" items="${usageData}">
							        <tr>
							            <td style="text-align:center"></td>
							            <td style="text-align:center">${entry.key}</td>
							            <td style="text-align:center">${entry.value}</td>
							        </tr>
							    </c:forEach>
							    </c:when>
							    <c:otherwise>
							    	<tr>
							            <td></td>
							            <td></td>
							            <td></td>
								    </tr>
							    </c:otherwise>
						    </c:choose>
					    </tbody>
					    <tfoot>
				            <tr>
				                <th colspan="2" style="text-align:right">Total:</th>
				                <th ></th>
				            </tr>
				        </tfoot>
					</table>
					<br />
	   			</div>
	   		</div>
		</div>
		<script type="text/javascript">
			$(document).ready(function() {
				var usageOption = $("#usageOption").val();
				if(usageOption == 'ip'){
					$("#ip").prop("checked",true);
				}else if(usageOption == 'acctNo'){
					$("#acctNo").prop("checked",true);
				}else{
					$("#downloadformat").prop("checked",true);
				}
				
	        	$('#dateSearch').change( function() {
	        		var isChecked = this.checked;
	
	        	    if(isChecked) {
	        	        $("#datepicker1").prop("disabled",false);
	        	        $("#datepicker2").prop("disabled",false);
	        	        $( "#datepicker1" ).datepicker({
			        		 onSelect: function(selected) {
			        			 $("#datepicker2").datepicker("option","minDate", selected);
			        		 },
			        		 maxDate: new Date(),
			        		 dateFormat: 'dd-mm-yy'
			        	 });
			        	 $( "#datepicker2" ).datepicker({
			        		 onSelect: function(selected) {
			        			 $("#datepicker1").datepicker("option","maxDate", selected);
			        		 },
			        		 maxDate: new Date(),
			        		 dateFormat: 'dd-mm-yy'
			        	 });
	
	        	    } else {
	        	    	$("#datepicker1").prop("disabled",true);
	        	        $("#datepicker2").prop("disabled",true);
	        	    }
		        });
	
	
		    	 $('#usage_table').dataTable({
		    		 "aaSorting": [[2,"desc"]],
		             "aoColumns": [{"bSortable":false}, {"bSortable":true}, {"bSortable":true}],
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
		     		},
		     		"footerCallback": function ( row, data, start, end, display ) {
		                var api = this.api(), data;
	
		                // Remove the formatting to get integer data for summation
		                var intVal = function ( i ) {
		                    return typeof i === 'string' ?
		                        i.replace(/[\$,]/g, '')*1 :
		                        typeof i === 'number' ?
		                            i : 0;
		                };
		                // Total over this page
		                data = api.column( 2, { page: 'current'} ).data();
		                pageTotal = data.length ?
		                    data.reduce( function (a, b) {
		                            return intVal(a) + intVal(b);
		                    } ) :
		                    0;
	
		                // Update footer
		                $( api.column( 2 ).footer() ).html(
		                    pageTotal
		                );
		            }
	
		         });
	
		    } );
			
			function isValidForm(){
		    	var dateRange = $('input[name="dateRange"]:checked').val();
	    		if(dateRange != undefined){
	    		   var date1 = $( "#datepicker1" ).val();
	    		   var date2 = $( "#datepicker2" ).val();
	    		   if(date1 == "" || date2 == ""){
	    			   alert('Must enter start and end date.');
	    			   return false;
	    		   }
	    	   }else{
	    		   $( "#datepicker1" ).val("");
	    		   $( "#datepicker2" ).val("");
	    	   }
	    	   
		   	}
		
	    </script>
	
	</body>
</html>