<%@ page language="java" session="false" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld"	prefix="stripes"%> 
<html>
<head>
<link href="/static/css/popups.css?v=${releaseversion}" media="all" type="text/css" rel="stylesheet"></link>
<style>
	.mm_eilogo{
		background: #FFFFFF;
		padding-right: 4px;
	}
</style>
<script>
	function writeDSRMCookie(box){
		
		if(box.checked){
			$.cookie("ev_rm_dontshow", release, { expires: 365, path: '/' });
			//console.log("wrote");
		}else{
			$.removeCookie("ev_rm_dontshow");
			//console.log("deleted");
		}
	}


	function closeRMX(){
		$.cookie('ev_rm_shown', release,  { path: '/' }); 
		return false;		
	}

</script>
</head>
<body>
<div>
	<div class="title" style="padding-left:0px;"><img id="eilogo" class="mm_eilogo" width="35px" src="/static/images/ei_w.gif" title="Engineering Information: Home of Engineering Village" alt="Engineering Information: Home of Engineering Village"></img>Engineering Village Important Information</div>
	<div class="msg">
		<c:set value="${actionBean.context.userSession.userTextZones}" var="referexTextZone"/>
		
		<c:if test="${not empty referexTextZone['EV_MODAL_DIALOG_2_TXT']}">
        	${referexTextZone['EV_MODAL_DIALOG_2_TXT']}
        </c:if>
		
	</div>
	<div style="float:left;padding-top:50px"><input type="checkbox" value="dontShowAgain" name="dontShowAgainCheckbox" id="dsCheckbox" onclick="writeDSRMCookie(this);" title="Don't Show Again Checkbox" /><label for="dsCheckbox">Don't Show Again</label></div>
	<div style="float:right;padding-top:50px"><a href="#" onclick="TINY.box.hide();" >Close</a></div>
	<div class="clear"></div>
</div>

</body>
</html>