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
<div id="survey_container" aria-live="assertive">
	<div id="survey_message">Help us improve Engineering Village by completing a short survey on a new feature.</div>
	<div id="survey_ratings">
	<br/>
		<div style="text-align:center;width:100%;">
		<a class="survey_rating" href="https://docs.google.com/forms/d/1iYQqn-AoGh5MTkjmPY4dsl7e2Oi4bkcSxJZ2ECnHDKY/viewform?usp=send_form" target="new" title="Search Term Highlighting feedback survey">Yes</a>
		&nbsp;|&nbsp;<a class="survey_rating" href="#" title="Search Term Highlighting feedback survey">No</a>
		</div>
	</div>
</div>
</body>
<script>
	$(".survey_rating").click(function(){
		//we need to submit this survey.
		var ret = true;
		var surveyCookie;

		if($.cookie("ev_survey")){
			surveyCookie = JSON.parse($.cookie("ev_survey"));
		}else{
			surveyCookie = {
					pageTrack:2,
					dontShow:false
			};
		}


		if($(this).text() == 'No'){
			ret = false;
			GALIBRARY.createWebEventWithLabel('Survey No', '${actionBean.feature}', document.location.pathname);
		}else{
			GALIBRARY.createWebEventWithLabel('Survey Yes', '${actionBean.feature}', document.location.pathname);
		}
		$.cookie("ev_survey", '{"pageTrack":'+surveyCookie.pageTrack+',"dontShow":'+true+'}',{expires: 365, path:'/'});
		$("#survey_ratings").hide();
		$("#survey_message").html("Thank you!");
		window.setTimeout(function(){$("#ev_survey").tooltipster('hide');}, 2000);

		return ret;

	});
</script>
</html>