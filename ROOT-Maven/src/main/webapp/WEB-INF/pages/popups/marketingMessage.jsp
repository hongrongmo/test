<%@ page language="java" session="false" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld"	prefix="stripes"%>

<style>
	.mm_eilogo{
		background: #FFFFFF;
		padding-right: 4px;
	}
	.title h1{
		color:#FFFFFF;
		margin-top:0px;
		margin-bottom:0px;
	}
</style>
<script>
	function writeDSCookie(box){

		if(box.checked){
			$.cookie("ev_mm_dontshow", release, { expires: 365, path: '/' });
			//console.log("wrote");
		}else{
			$.removeCookie("ev_mm_dontshow");
			//console.log("deleted");
		}
	}


	function closeX(){
		resetMM();
		//add the current content back on the page so TINY doesn't get rid of it.
		//reset tab listener
		$(document).unbind("keydown", setTabListener);
		$("#modalHTMLcontainer").append($(".tcontent").html());

		$.cookie('ev_mm_shown', release,  { path: '/' });
		return false;
	}

</script>
</head>
<body>
<div >
	<div class="title" style="padding-left:0px;" aria-role="alert" ><h1 id="mmPopupTitle"><img id="eilogo" class="mm_eilogo" width="35px" src="/static/images/ei_w.gif" title="Engineering Information: Home of Engineering Village" alt="Engineering Information: Home of Engineering Village"></img>&nbsp;&nbsp;Usage Tips &amp; Tricks</H1></div>
	<div class="msg">
			<jsp:include page="/WEB-INF/pages/popups/usagetips.jsp" />
	</div>
	<div style="float:left;padding-top:25px"><input type="checkbox" value="dontShowAgain" name="dontShowAgainCheckbox" id="dsCheckbox" onclick="writeDSCookie(this);" title="Don't Show Again Checkbox" /><label for="dsCheckbox">Don't Show Again</label></div>
	<div style="float:right;padding-top:25px"><a id="backLink" href="#" style="display:none" onclick="resetMM();return false;" >Back</a><span class="bar" style="display:none;">&nbsp;|&nbsp;</span><a href="#" onclick="TINY.box.hide();" id="closeMessage" title="Close this message">Close</a></div>
	<div class="clear"></div>
</div>
