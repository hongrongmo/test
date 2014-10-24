<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<div style="border:1px solid #6C6" id="counterpopup">
 <div style="color:white;background:#006699"  onclick="closeStatusPopup();">
 	
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
		            <th>Bucket Name</th>
		            <th>Value</th>
		        </tr>
		    </thead>
		    <tbody>
			    <c:choose>
				    <c:when test="${not empty statusMap}"> 
					    <c:forEach var="entry" items="${statusMap}">
					        <tr>
					            <td>${entry.key}</td>
					            <td>${entry.value}</td>
					        </tr>
					    </c:forEach>
				    </c:when>
				    <c:otherwise>
				        	<tr>
				        		<td colspan="9">NO ENTRIES!</td>
				        	</tr>
				    </c:otherwise>
			    </c:choose>  
		    </tbody>
    	</table>
	</c:otherwise>
 </c:choose>  
</div>	
	
	