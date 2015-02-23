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
		font-size:18px;
		text-align:center;
		padding-bottom:25px;
	}
	#survey_container{
	}
	.mm_eilogo{
		background: #FFFFFF;
		padding-right: 4px;
	}
	.title span{
		padding-left:5px;
	}
    #survey_yes{
    	color:#FFFFFF;
    	background-color:#3ca690;
    	border-radius:11px;
    	width:170px;
    	height:27px;
    	border:none;
    }
    .smallgraytxt{
    	color:#808080;
    	font-size:9px;
    	float: left;
		padding-top: 10px;
    }
    .evGreen{
    	color:#3ca690;
    }
    #survey_header{
    	color:#3ca690;
    	font-weight:bold;
		text-align: center;
		padding-bottom: 15px;
		font-size: 23px;
		padding-top: 15px;
    }
</style>
</head>
<body>
<div aria-live="assertive">
	<div class="title" style="padding-left:0px;"><img id="eilogo" class="mm_eilogo" width="35px" src="/static/images/ei_w.gif" title="Engineering Information: Home of Engineering Village" alt="Engineering Information: Home of Engineering Village"></img><span>Engineering Village</span></div>
	<div class="msg">
	<div id="survey_container">
		<div id="survey_header">WE WANT TO HEAR FROM YOU!</div>
		<div id="survey_message">
			<c:choose>
				<c:when test="${not empty textzones and (not empty textzones['EXIT_SURVEY_MSG'])}">
	 				${textzones['EXIT_SURVEY_MSG']}
				</c:when>
				<c:otherwise>
					Help us to improve Engineering Village <br/>by taking a short survey <br/><span class="evGreen">at the end of your session.</span>
				</c:otherwise>
			</c:choose>
		</div>
		<div id="survey_ratings">
			<br/>

			<div style="text-align:right;width:100%;">
			<div class="smallgraytxt">A second window will open.</div>
			<input type="button" id="survey_yes"  title="Take Engineering Village Exit Survey" value="Yes, I will give feedback"/>
			&nbsp;|&nbsp;<a id="survey_no" href="#" onClick="TINY.box.hide();" title="Decline Engineering Village Exit Survey">Cancel</a>
			</div>
		</div>
	</div>
	</div>
	<div class="clear"></div>
</div>

</body>
<script>
	var surveyNo = true;
	var surveyLink = "http://www.engineeringvillage.com";
	<c:if test="${not empty textzones and (not empty textzones['EV_EXIT_SURVEY_LINK'])}">
		surveyLink = "${textzones['EV_EXIT_SURVEY_LINK']}";
	</c:if>
	$("#survey_yes").click(function(){
		//set the onunload to watch for the user to leave EV and start the survey
		GALIBRARY.createWebEventWithLabel('Exit Survey', 'Yes', document.location.pathname);
		window.open(surveyLink, "_new", "location=no,width=720,height=930,scrollbars=yes");
		surveyNo = false;
		TINY.box.hide();

	});

	function closeSurveyPopup(){
		if(surveyNo){
			GALIBRARY.createWebEventWithLabel('Exit Survey', 'No' ,document.location.pathname);
		}
		$.cookie("ev_exitsurvey", '{"dontShow":'+true+'}',{expires: 365, path:'/'});
		return false;
	}
</script>
</html>