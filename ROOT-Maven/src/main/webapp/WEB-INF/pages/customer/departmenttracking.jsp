<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld"
    prefix="stripes"%>

<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp"
    pageTitle="Engineering Village - Department Level Tracking">

    <stripes:layout-component name="header">
    <jsp:include page="/WEB-INF/pages/include/headernull.jsp" />
    </stripes:layout-component> 

    <stripes:layout-component name="contents">

<form method="POST" action='/search/quick.url?CID=quickSearch'>
<input type="hidden" name="CID" value="quickSearch"/>
<br/>
<div class="marginL15">
Your organization has requested department level usage tracking.<br/> To enable this feature please select from one of the departments listed below:<br/><blockquote>
<c:forEach items="${requestScope.department}" var="department">
    <input type='radio'  name='SYSTEM_DEPARTMENT' value='${department.ID}'/>${department.name}<br/>
</c:forEach>
<br/><input type='submit' value='Submit'/></blockquote><p/>
(If you have questions about which department to select or do not see your department please contact ${requestScope.cust.contactName} at ${requestScope.cust.contactEmail})
<input type='hidden' name='EISESSION' value='${requestScope.sessionid}'/>
<input type='hidden' name='SYSTEM_USE_SESSION_PARAM' value='true'/>
</div>
</form>

</stripes:layout-component>
</stripes:layout-render>