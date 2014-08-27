<%@page import="org.ei.controller.IPBlocker"%>
<%@page import="org.ei.config.RuntimeProperties"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jstl/sql" prefix="sql" %>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld" prefix="stripes" %>

<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp" pageTitle="Engineering Village - Usage">


    <stripes:layout-component name="csshead">
    <jsp:include page="include/customcss.jsp"></jsp:include>
    <link type="text/css" rel="stylesheet" href="/static/css/custom-theme/jquery.dataTables-1.10.0.min.css"/>
    <style type="text/css">
        h2 {font-size: 14px; margin: 3px; padding: 0}
            #blockedips_table {width:740px;border:1px solid black;}
            #blockedips_table_wrapper {width: 750px}
            #blockedips_table_wrapper input {margin: 5px 0}
    </style>
    </stripes:layout-component>

    <stripes:layout-component name="header">
        <jsp:include page="/WEB-INF/pages/include/headernull.jsp" />
    </stripes:layout-component>

<%-- **************************************** --%>
<%-- CONTENTS                                 --%>
<%-- **************************************** --%>
    <stripes:layout-component name="contents">

    <div id="container">
    	<jsp:include page="include/tabs.jsp"/>
        <div class="marginL10">
	    	<div style="margin-left:5px">
	            <c:if test="${not empty actionBean.context.messages}">
				    <c:forEach var="message" items="${actionBean.context.messages}"><span style="font-weight:bold;color: #148C75;">${message.message}</span></c:forEach>
				</c:if>
		    	<h2>IP Blocker</h2>

		        <p>
		            This page allows you to add/remove/modify the IP's for blocking purpose. <span style="font-weight:bold">You may need to wait for maximum 10 minutes to get the changes applied to the system.</span>
		        </p>

		        <br/>
		        <stripes:form id="mainForm" action="/status.url" method="POST">
		        	<stripes:text name="txtblockedip" id="txtblockedip"/>
		        	<stripes:hidden name="enabled" id="eneblestatus"/>
		        	<stripes:submit name="blockedipsubmit" onclick="return setDefaultAction();" value="Add"></stripes:submit>
		        	<br />
		        </stripes:form>
	    	</div>
	    	<br />
	    	<table  id="blockedips_table">
			    <thead>
			        <tr>
			            <th>IP</th>
                        <th>Status</th>
			            <th>Last Modified</th>
                        <th>Account Name</th>
			            <th>Description</th>
			            <th></th>
			        </tr>
			    </thead>
			    <tbody>
				    <c:choose>
					    <c:when test="${not empty blockedIpsList}">
						    <c:forEach var="entry" items="${blockedIpsList}">
						        <tr>
						            <td><!--  Environment: ${entry.environment} --><a href="#" onClick="getCounter('${entry.IP}',event);">${entry.IP}</a></td>
						            <c:choose>
						            	 <c:when test="${entry.blocked}">
						            	 	<td align="center"><a href="#" onClick="disableIp('${entry.IP}',false);">Blocked</a></td>
						            	 </c:when>
						            	 <c:otherwise>
									        <td align="center"><a href="#" onClick="disableIp('${entry.IP}',true);">Unblocked</a></td>
									    </c:otherwise>
									</c:choose>
                                    <td>${entry.timestamp}</td>
                                    <td>${entry.accountName}</td>
						            <td style="width:150px"><a href="#" onClick="getDescHistory('${entry.IP}',event);">Show History</a></td>
						            <td align="center"><a href="#" onClick="deleteIp('${entry.IP}');">Remove</a></td>

						        </tr>
						    </c:forEach>
					    </c:when>
					    <c:otherwise>
					        <tr><td colspan="9">NO ENTRIES!</td></tr>
					    </c:otherwise>
				    </c:choose>
			    </tbody>
		    </table>
		    <br />
		</div>
   </div>
    </stripes:layout-component>

    <stripes:layout-component name="jsbottom">
    <c:if test="${not empty blockedIpsList}">
    <script type="text/javascript" src="/static/js/jquery/jquery.dataTables-1.10.0.min.js"></script>
    <script type="text/javascript">

    $(document).ready(function() {
    	 $('#blockedips_table').dataTable({
             "aaSorting": [[2,"desc"]],
             "aoColumns": [null, null, null, null,{"bSortable":false}, {"bSortable":false}],
             "bStateSave": false,
             "bPaginate": false,
             "bAutoWidth": false,
             "bFilter": true,
             "bInfo":false
         });

    } );


    function setDefaultAction(){
    	document.getElementById('mainForm').action = '/status.url';
    	return true;
    }

    function deleteIp(blockedIp){
		var confirmDelete = confirm('Are you sure you want to delete this IP ('+blockedIp+')?');
		if(confirmDelete == true){
				document.getElementById('txtblockedip').value = ""+blockedIp;
				document.getElementById('mainForm').action = '/statusdeleteblockedip.url';
				document.getElementById('mainForm').submit();
		  }
    }

    function disableIp(blockedIp, enableflag){
		document.getElementById('txtblockedip').value = ""+blockedIp;
		document.getElementById('eneblestatus').value = enableflag;
		document.getElementById('mainForm').action = '/statusupdateblockedip.url';
		document.getElementById('mainForm').submit();
	}

    function getCounter(blockedIp,e){
    	$('#counterpopup').remove();
    	$('#deschistorypopup').remove();
		document.getElementById('mainForm').action = '/statusipcounterstatus.url?txtblockedip='+blockedIp;
		params = {};
		$.post(document.getElementById('mainForm').action,
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

    function getDescHistory(blockedIp,e){
   	 $('#counterpopup').remove();
   	 $('#deschistorypopup').remove();
		document.getElementById('mainForm').action = '/statusiphistory.url?txtblockedip='+blockedIp;
		params = {};
		$.post(document.getElementById('mainForm').action,
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

    function closeStatusPopup(){
    	 $('#counterpopup').remove();
    }

    function closeDescHistory(){
   	 	$('#deschistorypopup').remove();
   	}



    </script>
    </c:if>

    </stripes:layout-component>
</stripes:layout-render>