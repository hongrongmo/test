<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld" prefix="stripes" %>
<%@page import="org.ei.config.EVProperties"%>
    
<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp" pageTitle="Engineering Village - Edit Environment Properties">

    <stripes:layout-component name="csshead">
    	<jsp:include page="include/customcss.jsp"></jsp:include>
    	<link type="text/css" rel="stylesheet" href="/static/css/custom-theme/jquery.dataTables-1.10.0.min.css"/>
    	<style type="text/css">
    		#container{
    			width:100%;
    		}
    		#maincontainer{
    			width:100%;
    			margin:8px;
    			padding:5px;
    		}
    		#ddcontainer{
    			width:100%;
    		}
    		#tblcontainer{
    			width:98%;
    		}
	        h2 {
	        	font-size: 14px; padding: 0;
	        }
	        #messagecontainer{
	        	font-weight:bold;
	        	color: #148C75;
	        	margin:3px
	        }
	        .keystyle{
	        	font-weight:bold;
	        }
	        .keyaction{
	        	
	        	border-left: 0px;
	        }
	        .keyactionvalue{
	        	border-right: 0px;
	        }
	        table.dataTable td { background-color: #E6E6B8;}
	        table.dataTable tr { background-color: #ADD6C2;  }
	        #envprops_table_filter input {
			  background-color: #f2ecdd;
			}
			#envprops_table_filter label {
			  font-weight:bold;
			}
			select{
   				background:#D8D8D8;
			}
			option:not(:checked) { 
			    background-color: #D8D8D8; 
			}
			#envprops_table{
				table-layout: fixed; 
				word-wrap:break-word;
			}
			.toolbar{
				float:left;
				color:#B22400;
				font-weight: bold;
			}
		</style>
    </stripes:layout-component>
    <stripes:layout-component name="header">
        <jsp:include page="/WEB-INF/pages/include/headernull.jsp" />
    </stripes:layout-component>
    <stripes:layout-component name="ssourls"/>
    <stripes:layout-component name="contents">
    	<div id="container">
    		<jsp:include page="include/tabs.jsp"/>
    		<div id="maincontainer">
    			<c:if test="${not empty actionBean.context.messages}">
					<c:forEach var="message" items="${actionBean.context.messages}"><span id="messagecontainer">${message.message}</span></c:forEach>
				</c:if>
    			<h2>Engineering Village - Edit Environment Properties</h2>
    			<div id="ddcontainer" >
    				<h4><span>Environment level : </span>
    				<select name="envselect" id="envselect">
	                 	<c:forEach var="entry" items="${envrunlevels}">
	                 		<option value="${entry}" ${envrunlevel == entry ? 'selected' : ''}>${entry}</option>
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
					            <th>Environment(${envrunlevel})</th>
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
								            <td><a href="#" onClick="saveKeyValue('${entry.key}','${envrunlevel}','keyvalue${counter.count+1}');">Save</a> | <a href="#" onClick="removeKeyValue('${entry.key}','${envrunlevel}');">Remove</a></td>
								        </tr>
								    </c:forEach>
							    </c:when>
							    <c:otherwise>
							        <tr><td colspan="9">NO ENTRIES!</td></tr>
							    </c:otherwise>
						    </c:choose>
					    </tbody>
				    </table>
    			</div>
    			<div id="formcontainer">
    				<stripes:form id="mainForm" action="/status.url" method="POST">
		        		<stripes:hidden name="runtimepropkey" id="runtimepropkey"/>
		        		<stripes:hidden name="runtimepropkeyvalue" id="runtimepropkeyvalue"/>
		        		<stripes:hidden name="runtimepropenvlevel" id="runtimepropenvlevel"/>
		        	</stripes:form>
    			</div>
    		</div>
    	</div>    
    </stripes:layout-component>
    <stripes:layout-component name="jsbottom">
    	<script type="text/javascript" src="/static/js/jquery/jquery.dataTables-1.10.0.min.js"></script>
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
	            window.location.href = "/status/editenvprops.url?env="+$(this).val();
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
		    		   document.getElementById('mainForm').action = '/statusupdateruntimeproperty.url';
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
		    		document.getElementById('mainForm').action = '/statusremoveruntimepropertyattribute.url';
					document.getElementById('mainForm').submit();
	    	   }
	       }
	    	
    	</script>
    </stripes:layout-component>
    <stripes:layout-component name="sessionexpiryhandler"></stripes:layout-component>
</stripes:layout-render>