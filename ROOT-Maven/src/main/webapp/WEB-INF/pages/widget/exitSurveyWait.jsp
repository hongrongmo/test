<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld"
	prefix="stripes"%>

<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp">


<stripes:layout-component name="contents">
	<div style="margin-top:5px;text-align:center;">
		<h3 id="waitMsg">Waiting for your EV Session to end... <br/>We will redirect you to the exit survey when you leave EV. <br/>You can start the survey without waiting by clicking <a href="">Here</a></h3>
	</div>

</stripes:layout-component>
<stripes:layout-component name="modal_dialog_2">
</stripes:layout-component>
<stripes:layout-component name="survey">
</stripes:layout-component>
<stripes:layout-component name="exitSurvey">
<script>
	function checkParent(){
		console.log("checking");
		if(window.opener && window.opener.imyourfather){
			setTimeout(checkParent,500);
		}else{
			console.log("closed");
			window.focus();
			window.location.href = "http://www.google.com";
		}
	}
	$(document).ready(function(){
		console.log("loaded");
		setTimeout(checkParent,500);
	});


</script>
</stripes:layout-component>
</stripes:layout-render>