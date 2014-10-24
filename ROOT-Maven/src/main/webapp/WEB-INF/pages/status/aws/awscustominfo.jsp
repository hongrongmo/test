<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld" prefix="stripes" %>
<html>
<head>

</head>
<body>
<c:if test="${not empty actionBean.awsInfo.customVals}">
<h2>Requested Custom Fields</h2>
<div id="customVals">

	<div id="keys">
	<c:forEach items="${actionBean.keys}" var="key">
		${key}:</br>	
	</c:forEach>
	</div>
	<div id="vals">
	<c:forEach items="${actionBean.awsInfo.customVals}" var="customVal">
		${customVal}</br>	
	</c:forEach>
	</div>
</div>	
</c:if>
</body>
</html>