var dbSave = {
		mask:1,
		tooltipShown:false
};
$(document).ready(function(){
	$("#saveDBIcon").click(function(){
		//write new db mask to cookie
		dbSave.mask = calculateMask();
		if($(this).attr("src").indexOf('SaveSearch_off') >= 0){
			return false;
		}
		$.cookie("ev_saveddbs", dbSave.mask,{expires:365, path:'/'});
		$("#saveDBIcon").attr("src","/static/images/SaveSearch_off.png");
		$("#saveDBIcon").attr("title","");
		showDBTooltip("Database preference saved!");
	});
	
	if($.cookie("ev_saveddbs") && $.cookie("ev_saveddbs") != 'null'){
		mask = $.cookie("ev_saveddbs");
		if(calculateMask() != mask){
			enableSaveDB();
		}
		dbSave.mask = mask;
	}
	
});
function enableSaveDB(){
	$("#saveDBIcon").attr("src","/static/images/SaveSearch.png");
	$("#saveDBIcon").attr("title","Save current database selection as default");
	$("#saveDBIcon").css("cursor","pointer");
	showDBTooltip();
	
}
function disableSaveDB(){
	$("#saveDBIcon").attr("src","/static/images/SaveSearch_off.png");
	$("#saveDBIcon").attr("title","");
	$("#saveDBIcon").css("cursor","default");
}

function showDBTooltip(otherMsg){
	
	if((!$.cookie("ev_saveddbs") && !dbSave.tooltipShown) || (otherMsg != null && otherMsg.length > 0)){
		//if the cookie has never been made then this is the first time. 
		//show tooltip to let them know how to save db selections
		var msg = "Select to save database<br/> preference as default.";
		var selector = ".saveDatabases";
		var duration = 4000;
		var direction = "bottom";
		//showTooltip(".saveDatabases", msg, "bottom", 4000,true,false);
		
		if ( $.browser.msie && $.browser.version == 7) {
			$(selector).tooltipster({interactive:true, arrow:false,contentAsHTML:true});
			$(selector).tooltipster('reposition');
		}else{
			$(selector).tooltipster({interactive:true, position:direction,contentAsHTML:true});
		}
		if(otherMsg != null && otherMsg.length > 0)
		{
			msg = otherMsg;
		}else{dbSave.tooltipShown = true;}
		
		$(selector).tooltipster('update',msg);
		$(selector).tooltipster("show");
		setTimeout("$('" + selector + "').tooltipster('destroy');$('" + selector + "').attr('title','Select your database preference to save as default')",duration);
		
	}
}