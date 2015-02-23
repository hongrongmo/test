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
	<div id="ev_survey"></div>
	<script>
	<c:if test="${actionBean.context.userSession.user.userPreferences.featureSurvey && actionBean.context.userSession.user.highlightingEnabled}">

		<c:choose>
			<c:when test="${surveyLocation == 'results'}">
				$(document).ready(function(){showSurvey("highlight", "results");});
			</c:when>
			<c:otherwise>
			$(document).ready(function(){showSurvey("highlight", "record");});
			</c:otherwise>
		</c:choose>
	</c:if>
	</script>