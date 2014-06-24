<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld"
	prefix="stripes"%>

<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp">

<stripes:layout-component name="header">
<jsp:include page="/WEB-INF/pages/include/headernull.jsp" />
</stripes:layout-component> 

<stripes:layout-component name="contents">

	<div id="contentmain" style="margin-left:15px">
<%-- ************************************************************* --%>
<%-- LOGOUT --%>
<%-- ************************************************************* --%>
	<h3>Session Ended</h3>
	<p>You have ended your Engineering Village search session. To return to Engineering Village, please click on link below.</p>
	<br/>
	<p><a href="/home.url?CID=home" class="jsforward">Begin a new session</a></p>
	
	<SCRIPT>
    function clearCookies() {
        document.cookie = 'EISESSION=0; expires=Thu, 01-Jan-70 00:00:01 GMT;';
      }

      // Clear cookies on load...
      $(document).ready(clearCookies);
	</SCRIPT>

	</div>

	<div class="clear"></div>

<br/>
</stripes:layout-component>

</stripes:layout-render>