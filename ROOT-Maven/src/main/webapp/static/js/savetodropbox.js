$(document).ready(function(){
	validationForDropbox();
	$("input[name='downloadformat']").change(function(){
		validationForDropbox();
	});
	$("#displayformat").change(function(){
		validationForDropbox();
	});
});

function validationForDropbox(){
	var downloadformat = $('input[name="downloadformat"]:checked').val();
	if (downloadformat == undefined || downloadformat == "" || downloadformat == "refworks") {
		if (downloadformat == "refworks") {
			$('#savetodrivemsg').html("Refworks not supported");
		}else{
			$('#savetodrivemsg').html("Select a download format");
		}
		return false;
	}else{
		$('#savetodrivemsg').html("");
	}
	return true;
}

function submitDropboxDL(){

	if(!validationForDropbox()){
		return false;;
	}
	var downloadformat = $('input[name="downloadformat"]:checked').val();
	var baseaddress = $("input[name='baseaddress']").val();
	var displaytype = $("input[name='displayformat']:checked").val();
	var downloadUrl = constructDbxDownlodUrl(downloadformat,displaytype);
	var dropBoxPageUrl = 'https://'+baseaddress+'/delivery/download/dropbox.url?downloadformat='+downloadformat+'&displayformat='+displaytype+'&dropBoxDownloadUrl='+escape(downloadUrl);
	GALIBRARY.createWebEventWithLabel('Dropbox', 'Save Initiated', downloadformat);
	var new_window1 = window.open(dropBoxPageUrl, 'DropBox', "height=350,width=820,resizable=yes,scrollbars=yes");
	new_window1.focus();
	return false;
}

function constructDbxDownlodUrl(downloadformat,displaytype){
	var docidlist = $("#docidlist").val();
	var handlelist = $("#handlelist").val();
	var folderid = $("#folderid").val();
	var downloadUrl = '/delivery/download/submit.url?downloadformat='+downloadformat+'&displayformat='+displaytype;
	if (docidlist && docidlist.length > 0)
		downloadUrl += "&docidlist=" + docidlist;
	if (handlelist && handlelist.length > 0)
		downloadUrl += "&handlelist=" + handlelist;
    if (folderid && folderid.length > 0)
    	downloadUrl += "&folderid=" + folderid;
    return downloadUrl;
}

function dbx_tooltip_show(tooltipId,event){
    thistip = document.getElementById("savetodrivemsg");
    thistip.style.visibility = 'visible';

}

function dbx_tooltip_hide(tooltipId){
	thistip = document.getElementById("savetodrivemsg");
    thistip.style.visibility = 'hidden';
}