<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<div style="border:1px solid #6C6" id="deschistorypopup">
 <div style="color:white;background:#006699"  onclick="closeDescHistory();">

 	<div >
 		<span>${ip}</span><span style="float:right;"><a href="#"><img height="15px" src="/static/images/close.gif"></a></span>
 	</div>
 </div>

 <c:choose>
 	<c:when test="${not empty error}">
 		<span>${error}</span>
 	</c:when>
	<c:otherwise>
		<table >
		    <thead>
		        <tr>
		            <th></th>
		            <th>Description</th>
		            <th>Timestamp</th>
		        </tr>
		    </thead>
		    <tbody>
			    <c:choose>
					    <c:when test="${not empty descHistList}">
						    <c:forEach var="entry" items="${descHistList}" varStatus="loop">
						        <tr>
						            <td>${loop.count}</td>
						            <td style="width:150px">${entry.message}</td>
						            <td>${entry.timestamp}</td>
						        </tr>
						    </c:forEach>
					    </c:when>
					    <c:otherwise>
					        <tr><td colspan="9">NO ENTRIES!</td></tr>
					    </c:otherwise>
				    </c:choose>
		    </tbody>
    	</table>
	</c:otherwise>
 </c:choose>
</div>

