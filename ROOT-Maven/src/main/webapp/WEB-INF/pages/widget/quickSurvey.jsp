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
		padding-right:10px;
		float:left;
		cursor:pointer;
	}
	.survey_rating img{
	width:50px;
	}
	#survey_container{
		width:178px;
	}

</style>
</head>
<body>
<div id="survey_container">
	<div id="survey_message">Ask a question here?</div>
	<div id="survey_ratings">
		<div class="survey_rating" id="survey_bad"><img src="/static/images/ev_sad.png"/></div>
		<div class="survey_rating" id="survey_neutral"><img src="/static/images/ev_dontcare.png"/></div>
		<div class="survey_rating" style="padding:0px;" id="survey_good"><img src="/static/images/ev_happy.png"/></div>
	</div>
</div>
</body>
<script>
	$(".survey_rating").click(function(){
		//we need to submit this survey.
		var feature = '${actionBean.feature}';
		var rating = $(this).attr("id").split("_")[1];
		var surveyurl = "/widget/qsurvey.url?rate=true&feature=" + feature + "&rating=" + rating;
		$.ajax({
			url:surveyurl
		}).success(function(data){
			$("#ev_survey").tooltipster("content", "Thank you!");
			window.setTimeout(function(){$("#ev_survey").tooltipster('hide');}, 3000);
		});
	});
</script>
</html>