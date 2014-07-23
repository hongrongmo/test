var dlOptions;
$(document).ready(function(){
	checkForOneClick();
	$("#oneclickDL").click(basketDownloadSubmit);
});
function checkForOneClick(){
	if($.cookie("ev_oneclickdl")){
		dlOptions = JSON.parse($.cookie("ev_oneclickdl"));
		changeOneClick(dlOptions.location);
	}

}
function changeOneClick(dlType){
	var dlClass = "";
	var dlText = "Save to ";
	if(dlType == "outputRefWorks"){
		dlText += " Refworks";
		dlClass = "refworksdl";
	}else if(dlType == "outputGoogle"){
		dlText += " Google Drive";
		dlClass = "googledl";
	}else if(dlType == "outputDropbox"){
		dlText += " Dropbox";
		dlClass = "dropboxdl";
	}else {
		dlText += " My PC";
		dlClass = "mypcdl";
	}

	$("#downloadlink").html("<span class='transText'>Edit</span>");
	$("#downloadlink").addClass("editDLSettings");
	$("#downloadlink").css("margin-left","0px");
	//$("#downloadlink").css("width","18px");

	$("#oneclickDL").html("");
	$("#oneclickDL").html(dlText);
	$("#oneclickDL").show();

	$("#downloadli").removeClass();
	$("#downloadli").addClass(dlClass);

}
function basketDownloadSubmit() {

	var baseaddress = dlOptions.baseaddress;
	var displaytype = dlOptions.displaytype;
	var downloadformat = dlOptions.format;
	var downloadLocation = dlOptions.location;
	var databaseid = $("input[name='databaseid']").val();
	var sessionid = $("input[name='sessionid']").val();
	var milli = (new Date()).getTime();

	if (downloadformat == undefined || downloadformat == "") {
		alert("You must choose a download format.");
		event.preventDefault();
		return (false);
	}

	var url = "";
	GALIBRARY.createWebEventWithLabel('Output', 'Download', downloadformat);
	// Refworks?

	if (downloadLocation == "outputRefWorks") {
		var refworksURL = "http://www.refworks.com/express/ExpressImport.asp?vendor=Engineering%20Village%202&filter=Desktop%20Biblio.%20Mgt.%20Software";
		url = "http://" + baseaddress
				+ "/delivery/download/refworks.url?downloadformat="
				+ downloadformat + "&timestamp=" + milli
				+ "&sessionid=" + sessionid
				+ '&database=' + databaseid;

		window.open(
						refworksURL + "&url=" + escape(url),
						"RefWorksMain",
						"width=800,height=500,scrollbars=yes,menubar=yes,resizable=yes,directories=yes,location=yes,status=yes");
        event.preventDefault();

	}else if(downloadLocation == "outputDropbox"){
		var downloadUrl = '/delivery/download/submit.url?downloadformat='+downloadformat+'&displayformat='+displaytype + '&database=' + databaseid + "&sessionid=" + sessionid;
		var dropBoxPageUrl = 'https://'+baseaddress+'/delivery/download/dropbox.url?downloadformat='+downloadformat+'&displayformat='+displaytype+'&dropBoxDownloadUrl='+escape(downloadUrl);
		GALIBRARY.createWebEventWithLabel('Dropbox', 'Save Initiated', downloadformat);
		var new_window1 = window.open(dropBoxPageUrl, 'DropBox', "height=350,width=820,resizable=yes,scrollbars=yes");
		new_window1.focus();

	}else if(downloadLocation == "outputGoogle"){
		var googleDrivePageUrl = 'https://'+baseaddress+'/delivery/download/googledrive.url?downloadformat='+downloadformat+'&displayformat='+displaytype + "&sessionid=" + sessionid + '&database=' + databaseid;

		GALIBRARY.createWebEventWithLabel('Google Drive', 'Save Initiated', downloadformat);
		var new_window1 = window.open(googleDrivePageUrl, 'Google Drive', "height=350,width=820,resizable=yes,scrollbars=yes");
		new_window1.focus();
		ret = false;
	}else{
		var downloadUrl = 'https://'+baseaddress+'/delivery/download/submit.url?downloadformat='+downloadformat+'&displayformat='+displaytype + "&sessionid=" + sessionid + '&database=' + databaseid;
		$("#oneClickDLForm").attr("action",downloadUrl);
		$("#oneClickDLForm").submit();
		//window.location = downloadUrl;
	}
	return false;

}