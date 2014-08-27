<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld" prefix="stripes" %>

<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp" pageTitle="Engineering Village - Google Drive Usage">
    <stripes:layout-component name="csshead">
	    <jsp:include page="include/customcss.jsp"></jsp:include>
	    <link type="text/css" rel="stylesheet" href="/static/css/custom-theme/jquery.dataTables-1.10.0.min.css"/>
	    <link type="text/css" rel="stylesheet" href="/static/css/custom-theme/jquery.ui.thems.smoothness.css"/>
	    <style type="text/css">
	        h2 {font-size: 14px; margin: 3px; padding: 0}
	            #usage_table {border:1px solid black;}
	            #usage_table_wrapper {width: 750px}
	            #usage_table_wrapper input {margin: 5px 0}
	    </style>
    </stripes:layout-component>
    <stripes:layout-component name="header">
        <jsp:include page="/WEB-INF/pages/include/headernull.jsp" />
    </stripes:layout-component>
    <stripes:layout-component name="contents">
	    <div id="container">
	    	<jsp:include page="include/tabs.jsp"/>
	        <div class="marginL10">
		    	<div style="margin-left:5px">
			    	<h2>Save to Google Drive Usage<span style="font-size:20px"> [ Total Downloads : ${totalCount} ]</span></h2>
			        <p>
			            This page allows you to view usage of save to google drive functionality.</span>
			        </p>
			        <br/>
			        <form name="usageForm" id="usageForm" method="GET" action="/status.url">

			        	<div>
			        		<input type="radio" id="downloadformat" name="usageOption" value="downloadformat"  <c:if test="${actionBean.usageOption eq 'downloadformat'}">checked="checked"</c:if>/><label for="downloadformat">Download Format</label>
			        		<input type="radio" id="ip" name="usageOption" value="ip"  <c:if test="${actionBean.usageOption eq 'ip'}">checked="checked"</c:if>/><label for="ip">IP</label>
			        		<input type="radio" id="acctNo" name="usageOption" value="acctNo"  <c:if test="${actionBean.usageOption eq 'acctNo'}">checked="checked"</c:if>/><label for="acctNo">Account Number</label>
			        		<input type="button" onclick="doSubmitAction();" value="Go" />
			        		<br>
			        		<br>
			        		<input type="checkbox" id="dateSearch" name="dateRange" /><label for="dateSearch">Use Date Filter</label> Start Date: <input type="text" disabled name="startDate" id="datepicker1" /> End Date: <input type="text" disabled name="endDate" id="datepicker2" />
			        	</div>
			        </form>
		    	</div>
		    	<br />
		    	<c:choose>
				    <c:when test="${not empty dateQueryinfo}">
					   <div style="padding-left:5px;color:red">${dateQueryinfo}</div>
				    </c:when>
				    <c:otherwise>

				    </c:otherwise>
			    </c:choose>
		    	<table  id="usage_table">
				    <thead>
				        <tr>
				            <th>Index</th>
				            <th>
					    	<c:choose>
							    <c:when test="${actionBean.usageOption eq 'downloadformat'}">
					                Download Format
						        </c:when>
							    <c:when test="${actionBean.usageOption eq 'ip'}">
								    IP
							    </c:when>
							    <c:when test="${actionBean.usageOption eq 'acctNo'}">
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
							            <td></td>
							            <td>${entry.key}</td>
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
    </stripes:layout-component>

    <stripes:layout-component name="jsbottom">
   	    <script type="text/javascript" src="/static/js/jquery/jquery.dataTables-1.10.0.min.js"></script>

	    <script type="text/javascript">
	        $(document).ready(function() {

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
		             "aoColumns": [null, null, null],
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

// 		                // Total over all pages
// 		                data = api.column( 2 ).data();
// 		                total = data.length ?
// 		                    data.reduce( function (a, b) {
// 		                            return intVal(a) + intVal(b);
// 		                    } ) :
// 		                    0;

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



	       function doSubmitAction(){
	    	   var usageOption = $('input[name="usageOption"]:checked').val();
	    	   var dateRange = $('input[name="dateRange"]:checked').val();
	    	   var actionUrl = '/status/driveusage.url?usageOption='+usageOption;
	    	   if(dateRange != undefined){
	    		   var date1 = $( "#datepicker1" ).val();
	    		   var date2 = $( "#datepicker2" ).val();
	    		   if(date1 == "" || date2 == ""){
	    			   alert('Must enter start and end date.');
	    			   return false;
	    		   }else{
	    			   actionUrl += '&startDate='+date1;
	    			   actionUrl += '&endDate='+date2;
	    		   }
	    	   }
	    	   document.getElementById('usageForm').action = actionUrl;
	   		   document.getElementById('usageForm').submit();
	   	   }

	    </script>
	</stripes:layout-component>
</stripes:layout-render>
