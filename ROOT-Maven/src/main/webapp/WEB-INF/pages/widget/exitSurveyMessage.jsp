<%@ page language="java" session="false" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
 <%@ taglib uri="http://jawr.net/tags" prefix="jwr" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld" prefix="stripes" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<style>
	.survey_rating{
	}

	#survey_message{
		font-size:16px;
		text-align:center;
	}
	#survey_container{
		width:300px;
	}

</style>
</head>
<body>
<div id="survey_container">
	<div id="survey_message">Help us improve Engineering Village by completing a survey when you are finished with Engineering Village.</div>
	<div id="survey_ratings">
	<br/>
		<div style="text-align:center;width:100%;">
		<a class="survey_rating" href="/widget/exitsurvey.url" target="exitsurvey" title="Take Engineering Village Exit Survey">Yes</a>
		&nbsp;|&nbsp;<a class="survey_rating" href="#" title="Decline Engineering Village Exit Survey">No</a>
		</div>
	</div>
</div>
</body>
<script>
	$(".survey_rating").click(function(){
		//we need to submit this survey.
		var ret = true;
		if($(this).text() == 'No'){
			ret = false;
			GALIBRARY.createWebEventWithLabel('_trackEvent', 'Exit Survey No',  document.location.pathname, '');
		}else{
			//set the onunload to watch for the user to leave EV and start the survey
			GALIBRARY.createWebEventWithLabel('_trackEvent', 'Exit Survey Yes', document.location.pathname, '');
		}
		$.cookie("ev_exitsurvey", '{"dontShow":'+true+'}',{expires: 365, path:'/'});
		$(".surveyTheme #survey_ratings").hide();
		$(".surveyTheme #survey_message").html("Thank you!");
		window.setTimeout(function(){$("#ev_survey").tooltipster('hide');}, 1000);

		return ret;

	});
</script>
</html>