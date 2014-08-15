var dlOptions;
$(document).ready(function(){
	checkForOneClick();
	if(typeof($("#oneclickDL").attr('href')) == 'undefined'){
		$("#oneclickDL").click(
				function(){
					if(typeof(Basket) != 'undefined' && (Basket.count == 0) ){
						$(this).tooltipster({
						    content: 'Please select records from the search results and try again',
						    autoClose:true,
						    interactive:false,
						    contentAsHTML:true,
						    position:'bottom',
						    fixedLocation:true,
						    positionTracker:false,
						    delay:0,
						    speed:0,
						    functionAfter: function(origin){$(origin).tooltipster('destroy');}
						});
						$(this).tooltipster('show',null);
						return false;
					}else{basketDownloadSubmit();}
				});
	}else{
		$("#oneclickDL").click( function(){return recordPageDownloadSubmit($(this).attr('href'));});
	}

	$("body").append('<form name="oneClickDLForm" id="oneClickDLForm" method="post" action="/delivery/download/submit.url"></form>');
});
function checkForOneClick(){

	if($.cookie("ev_oneclickdl")){
		dlOptions = JSON.parse($.cookie("ev_oneclickdl"));
		changeOneClick(dlOptions.location);
	}else if(typeof(savedDLPrefs) != 'undefined'){
		dlOptions = savedDLPrefs;
		changeOneClick(dlOptions.location);
	}

}
function changeOneClick(dlType){
	var dlClass = "";
	var dlText = "Save to ";
	if(dlType == "refworks"){
		dlText += " Refworks";
		dlClass = "refworksdl";
	}else if(dlType == "googledrive"){
		dlText += " Google Drive";
		dlClass = "googledl";
	}else if(dlType == "dropbox"){
		dlText += " Dropbox";
		dlClass = "dropboxdl";
	}else {
		dlText += " My PC";
		dlClass = "mypcdl";
	}

	$("#downloadlink").html("<span class='transText'>Edit</span>");
	$("#downloadlink").addClass("editDLSettings");
	$("#downloadlink").css("margin-left","0px");
	$("#downloadlink").attr("title",'Click to change one click download preferences.');

	//$("#downloadlink").css("width","18px");

	$("#oneclickDL").html("");
	$("#oneclickDL").html(dlText);
	$("#oneclickDL").show();

	$("#downloadli").removeClass();
	$("#downloadli").addClass(dlClass);

}
function recordPageDownloadSubmit(dlLinkUrl){
	//the params of the url and remove the ?
	dlLinkUrl = dlLinkUrl.match(/\?.+/)[0];
	dlLinkUrl = dlLinkUrl.substr(1,dlLinkUrl.length);

	var params = "";


	var baseaddress = dlOptions.baseaddress;
	var downloadLocation = dlOptions.location;
	var downloadformat = dlOptions.format;
	var displayformat = dlOptions.displaytype;
	var milli = (new Date()).getTime();

	if(displayformat == 'default'){
		//if it's set to default use the format that came in on the url
		params = dlLinkUrl;
	}else{
		//otherwise ignore it and use the setting from the cookie but keep all other parameters
		var vars = dlLinkUrl.split('&');
		for (var i = 0; i < vars.length; i++) {
	        var pair = vars[i].split('=');
	        if (pair[0] != "displayformat") {
	            params += "&" + pair[0] + "=" + pair[1];
	        }
	    }
		params += "&displayformat=" + displayformat;

	}

	GALIBRARY.createWebEventWithLabel('Output', 'Download', downloadformat);
	if (downloadLocation == "refworks") {
		var refworksURL = "http://www.refworks.com/express/ExpressImport.asp?vendor=Engineering%20Village%202&filter=Desktop%20Biblio.%20Mgt.%20Software";
		url = "http://" + baseaddress
				+ "/delivery/download/refworks.url?downloadformat="
				+ downloadformat
				+ "&timestamp=" + milli
				+"&"+ params;


		window.open(
						refworksURL + "&url=" + escape(url),
						"RefWorksMain",
						"width=800,height=500,scrollbars=yes,menubar=yes,resizable=yes,directories=yes,location=yes,status=yes");
        event.preventDefault();

	}else if(downloadLocation == "dropbox"){
		var downloadUrl = '/delivery/download/submit.url?downloadformat='+downloadformat + "&"+ params;
		var dropBoxPageUrl = 'https://'+baseaddress+'/delivery/download/dropbox.url?downloadformat='+downloadformat + '&'+params+'&dropBoxDownloadUrl='+escape(downloadUrl);
		GALIBRARY.createWebEventWithLabel('Dropbox', 'Save Initiated', downloadformat);
		var new_window1 = window.open(dropBoxPageUrl, 'DropBox', "height=350,width=820,resizable=yes,scrollbars=yes");
		new_window1.focus();

	}else if(downloadLocation == "googledrive"){
		var googleDrivePageUrl = 'https://'+baseaddress+'/delivery/download/googledrive.url?downloadformat='+downloadformat + '&' + params;

		GALIBRARY.createWebEventWithLabel('Google Drive', 'Save Initiated', downloadformat);
		var new_window1 = window.open(googleDrivePageUrl, 'Google Drive', "height=350,width=820,resizable=yes,scrollbars=yes");
		new_window1.focus();
		ret = false;
	}else{
		var downloadUrl = 'https://'+baseaddress+'/delivery/download/submit.url?downloadformat='+downloadformat + '&' + params;
		$("#oneClickDLForm").attr("action",downloadUrl);
		$("#oneClickDLForm").submit();
		//window.location = downloadUrl;
	}
	return false;
}

function basketDownloadSubmit() {



	var baseaddress = dlOptions.baseaddress;
	var displaytype = dlOptions.displaytype;
	var downloadformat = dlOptions.format;
	var downloadLocation = dlOptions.location;
	var databaseid = $("input[name='databaseid']").val();
	var sessionid = $("input[name='sessionid']").val();
	var folderid = $("input[name='folderid']").val();
	var addParams = '';
	var milli = (new Date()).getTime();

	if (downloadformat == undefined || downloadformat == "") {
		alert("You must choose a download format.");
		event.preventDefault();
		return (false);
	}
	if(typeof(folderid) != 'undefined'){
		addParams += "&folderid=" + folderid;
	}

	if(displaytype == 'default'){
		if(typeof($("input[name='selectoption']:checked").val()) != "undefined" && downloadLocation != "refworks"){
			//we are on selected records get the option that is selected
			displaytype = $("input[name='selectoption']:checked").val();
		}else{
			displaytype = 'citation';
		}
	}

	var url = "";
	GALIBRARY.createWebEventWithLabel('Output', 'Download', downloadformat);
	// Refworks?

	if (downloadLocation == "refworks") {
		var refworksURL = "http://www.refworks.com/express/ExpressImport.asp?vendor=Engineering%20Village%202&filter=Desktop%20Biblio.%20Mgt.%20Software";
		url = "http://" + baseaddress
				+ "/delivery/download/refworks.url?downloadformat="
				+ downloadformat + "&timestamp=" + milli
				+ "&sessionid=" + sessionid
				+ '&database=' + databaseid
				+ addParams;

		window.open(
						refworksURL + "&url=" + escape(url),
						"RefWorksMain",
						"width=800,height=500,scrollbars=yes,menubar=yes,resizable=yes,directories=yes,location=yes,status=yes");
        event.preventDefault();

	}else if(downloadLocation == "dropbox"){
		var downloadUrl = '/delivery/download/submit.url?downloadformat='+downloadformat+'&displayformat='+displaytype + '&database=' + databaseid + "&sessionid=" + sessionid + addParams;
		var dropBoxPageUrl = 'https://'+baseaddress+'/delivery/download/dropbox.url?downloadformat='+downloadformat+'&displayformat='+displaytype+'&dropBoxDownloadUrl='+escape(downloadUrl);
		GALIBRARY.createWebEventWithLabel('Dropbox', 'Save Initiated', downloadformat);
		var new_window1 = window.open(dropBoxPageUrl, 'DropBox', "height=350,width=820,resizable=yes,scrollbars=yes");
		new_window1.focus();

	}else if(downloadLocation == "googledrive"){
		var googleDrivePageUrl = 'https://'+baseaddress+'/delivery/download/googledrive.url?downloadformat='+downloadformat+'&displayformat='+displaytype + "&sessionid=" + sessionid + '&database=' + databaseid + addParams;

		GALIBRARY.createWebEventWithLabel('Google Drive', 'Save Initiated', downloadformat);
		var new_window1 = window.open(googleDrivePageUrl, 'Google Drive', "height=350,width=820,resizable=yes,scrollbars=yes");
		new_window1.focus();
		ret = false;
	}else{
		var downloadUrl = 'https://'+baseaddress+'/delivery/download/submit.url?downloadformat='+downloadformat+'&displayformat='+displaytype + '&database=' + databaseid + addParams;
		$("#oneClickDLForm").attr("action",downloadUrl);
		$("#oneClickDLForm").submit();
		//window.location = downloadUrl;
	}
	return false;

}

function saveDownloadPrefs(dlOptions){
	$(".saved").hide();
	var url = "/customer/userprefs.url?savedlprefs=true&";
	var params = "";
		params += 'dlFormat=' + dlOptions.format + '&dlLocation=' + dlOptions.location + '&dlOutput=' + dlOptions.displaytype;

	url += params;
	GALIBRARY.createWebEventWithLabel('Preferences', 'Preferences Saved', params);

	$.ajax({
		url:url
	}).success(function(data){

	});

	return true;
}