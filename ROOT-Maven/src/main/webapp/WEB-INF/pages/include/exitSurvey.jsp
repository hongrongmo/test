<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld"	prefix="stripes"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>

	<style>
	#ev_survey{
	    position: fixed;
	    left: 30%;
	    top: 30%;
	    display:none;
	}
	.surveyTheme {
	    position: fixed;

	 }
	</style>
	<div id="exitSurvey" style="display:none">
		<%@ include file="/WEB-INF/pages/widget/exitSurveyMessage.jsp" %>
	</div>
	<div id="ev_survey"></div>
	<script>
		$(document).ready(function(){showExitSurvey();});
		var imyourfather = true;
	</script>