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
	<script>
	function showExitSurvey(){
		var surveyCookie;
		if($.cookie("ev_exitsurvey")){
			surveyCookie = JSON.parse($.cookie("ev_exitsurvey"));

			$.cookie("ev_exitsurvey", '{"dontShow":'+surveyCookie.dontShow+'}',{expires: 365, path:'/'});
		}else{
			$.cookie("ev_exitsurvey", '{"dontShow":'+false+'}',{expires: 365, path:'/'});
			surveyCookie = {
				dontShow:false,
			};
		}

		if(!surveyCookie.dontShow){
			TINY.box.show({html:document.getElementById("exitSurvey"),clickmaskclose:false,width:420,height:227,close:true,opacity:20,topsplit:3,closejs:function(){closeSurveyPopup();}});
			$("#exitSurvey").show();
		}

	}

	</script>
	<c:set value="${actionBean.context.userSession.userTextZones}" var="textzones"/>
	<div id="exitSurvey" style="display:none">
		<%@ include file="/WEB-INF/pages/popups/exitSurveyMessage.jsp" %>
	</div>
	<div id="ev_survey"></div>
	<script>
		var surveyFreq = 30;
		<c:if test="${not empty textzones and (not empty textzones['EXIT_SURVEY_LINK'])}">
		surveyFreq = ${textzones['EXIT_SURVEY_FREQ']};
		</c:if>
		$(document).ready(function(){
			if(Math.floor(Math.random() * 100) < surveyFreq){
				showExitSurvey();
			}
		});
		var imyourfather = true;
	</script>