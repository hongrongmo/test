<%@ page language="java" session="false" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib uri="http://jawr.net/tags" prefix="jwr" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld" prefix="stripes" %>
<%@ page import="org.ei.domain.personalization.*"%>

<stripes:layout-definition>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
    <meta name="robots" content="index,nofollow" />
    <stripes:layout-component name="metahead"/>
    <title>${pageTitle}</title>
 	<link rel="shortcut icon" href="/favicon.ico" type="image/x-icon"/>
	<link rel="icon" href="/favicon.ico" type="image/x-icon"/>


	<stripes:layout-component name="cssheadstandard">
		<jwr:style src="/bundles/standard.css"></jwr:style>
		</stripes:layout-component>
		<jwr:script src="/bundles/standard.js" useRandomParam="true"></jwr:script>

		<c:if test="${actionBean.context.userSession.user.userPreferences.featureHighlight}">
			<jwr:script src="/bundles/tooltipster.js"></jwr:script>
			<jwr:style src="/bundles/tooltipster.css"></jwr:style>
		</c:if>
		<script>
		var highlightV1 = false;
		</script>
		<c:if test="${actionBean.context.userSession.user.getPreference('HIGHLIGHT_V1')}">
		<script>
			highlightV1 = true;

		</script>
		<script src='/static/js/jquery/spectrum.js'></script>
		</c:if>
	<stripes:layout-component name="csshead"/>
</head>
<body onload="onloadfunction();">
<div class="skipnav">
<a class="skiptonavlink" href="#searchnavlink" onclick="$('#searchnavlink').focus();return false;" title="Skip to top Navigation 'Search'">Skip to top Navigation "Search"</a><br/>
<stripes:layout-component name="SkipToNavigation">
<!-- Override in jsp to use custom skip to links links -->
</stripes:layout-component>
</div>
<stripes:layout-component name="ssourls">

<c:if test="${actionBean.context.userSession.user.SSOURLInvoked}">
<c:forEach var="urls" items="${actionBean.context.userSession.user.ssoURLs}">
    <iframe class="displayNone" src='<c:out value="${urls}"/>'></iframe>
</c:forEach>
${actionBean.context.userSession.user.setSSOURLInvoked(false)}
</c:if>
</stripes:layout-component>

<stripes:layout-component name="header">
<jsp:include page="/WEB-INF/pages/include/header.jsp" />
</stripes:layout-component>

<div id="container">

<stripes:layout-component name="contents" />
</div>


<stripes:layout-component name="usersessiondump">
<c:set var="usersession" value="${actionBean.context.userSession}"/>
<c:if test="${not empty usersession and not empty usersession.user}">

 </c:if>
</stripes:layout-component>

<stripes:layout-component name="footer">
<jsp:include page="/WEB-INF/pages/include/footer.jsp" />
<jsp:include page="/WEB-INF/pages/include/copyright.jsp" />
</stripes:layout-component>

<stripes:layout-component name="jsbottom">

<script type="text/javascript" src="/static/js/galibrary.js"></script>

<script type="text/javascript">
    // Initialize GA
    var pageevents = [<c:forEach items="${webAnalyticsEvent}" var="webEvent" varStatus="status">{category:'<c:out value="${webEvent.category}"/>', action: '<c:out value="${webEvent.action}"/>', label: '<c:out value="${webEvent.label}"/>'}<c:if test='${!status.last}'>,</c:if></c:forEach>];
    GALIBRARY.init(
        ["${actionBean.context.googleAnalyticsAccount}", "${usersession.user.account.accountName}", "${usersession.user.individuallyAuthenticated}"],
        pageevents);


    //custom fliplogin to override cars!! ugh
      function flipLogin(button, from) {
      	return false;
      }
    
      function onloadfunction(){
    		
    	  if (/MSIE (\d+\.\d+);/.test(navigator.userAgent))
    	  { 
    	     var ieversion=new Number(RegExp.$1);
    	     if (ieversion<8)
    	     {
    	       if(document.getElementById('ie7msg') != 'undefined' &&  document.getElementById('ie7msg') != null ){
    	    	   document.getElementById('ie7msg').style.display = 'block';
    	       }
    	     }
    	  }
    }

    </script>

</stripes:layout-component>


	<stripes:layout-component name="jsbottom_custom"/>
	<stripes:layout-component name="modal_dialog_msg">
		<div style="display:none;" id="modalHTMLcontainer">
			<div id="modalmsg">
				<jsp:include page="/WEB-INF/pages/popups/marketingMessage.jsp" />
			</div>
		</div>
	</stripes:layout-component>
	<stripes:layout-component name="exitSurvey">
		<c:if test="${actionBean.context.userSession.user.userPreferences.exitSurvey}">
			<%@ include file="/WEB-INF/pages/include/exitSurvey.jsp" %>
		</c:if>
	</stripes:layout-component>
</body>
<script>var release = "5.6";</script>

	<stripes:layout-component name="modal_dialog">
		<c:if test="${actionBean.context.userSession.user.userPreferences.modalDialog && !actionBean.context.userSession.user.userPreferences.modalDialog2}">
			<script>
				$(document).ready(function(){
					if((!$.cookie("ev_mm_dontshow") || release != $.cookie("ev_mm_dontshow")) && (!$.cookie("ev_mm_shown") || release != $.cookie("ev_mm_shown"))){

						GALIBRARY.createWebEventWithLabel('Dialog Open', 'What\'s New', 'Auto Show');
						$(document).keydown(function(e){if(e.keyCode == 9 && document.activeElement.id == 'hidePopup'){$($('#marketing_message a, #marketing_message input')[0]).focus()}})
						TINY.box.show({html:document.getElementById("modalmsg"),ariaLabeledBy:"mmPopupTitle",clickmaskclose:false,width:900,height:450,close:true,opacity:20,topsplit:3,closejs:function(){closeX();},openjs:function(){$("#hidePopup").focus();$(document).keydown(setTabListener);}});
	   					//tell GA that we showed it
					}

				});
				function setTabListener(e){

					if(e.keyCode == 9 && document.activeElement.id == 'hidePopup'){
						$($('#marketing_message a, #marketing_message input[type!="hidden"]')[0]).focus();
						e.preventDefault();
					}
				}
			</script>
		</c:if>
	</stripes:layout-component>

<stripes:layout-component name="modal_dialog_2">

</stripes:layout-component>
<stripes:layout-component name="survey">


</stripes:layout-component>

<stripes:layout-component name="carsoverride">
	<jwr:style src="/bundles/carsoverride.css"></jwr:style>
<style></style>

</stripes:layout-component>

<stripes:layout-component name="sessionexpiryhandler">
	<script type="text/javascript" src="/static/js/sessionhandler.js?v=${releaseversion}"></script>
</stripes:layout-component>

</html>
</stripes:layout-definition>
