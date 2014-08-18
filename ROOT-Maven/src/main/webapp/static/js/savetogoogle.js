$(document).ready(function(){
	loadScript('https://apis.google.com/js/platform.js', startLoadingDriveButton);
});


function loadScript(url, callback){
    var script = document.createElement("script");
    script.type = "text/javascript";
    if (script.readyState){  //IE
        script.onreadystatechange = function(){
            if (script.readyState == "loaded" ||
                    script.readyState == "complete"){
                script.onreadystatechange = null;
                callback();
            }
        };
    } else {  //Others
        script.onload = function(){
        	callback();
        };
    }
    script.src = url;
    document.getElementsByTagName("head")[0].appendChild(script);

}

function startLoadingDriveButton(){
	constructDriveButton();
}

function constructDriveButton(){
		var downloadformat = $('#downloadformat').val();
		var displaytype = $("#displayformat").val();
		if (downloadformat == undefined || downloadformat == "" || downloadformat == "refworks") {
			if (downloadformat == "refworks") {
				$('#savetodrivemsg').html("Refworks not supported");
			}else{
				$('#savetodrivemsg').html("Select a download format");
			}
			$('#savetodrive-div').empty();
			$('#savetodrive-div').html('<a id="savetodrivebutton" onmouseover="tooltip_show();" onmouseout="tooltip_hide();" title="Save to Google Drive" href="#"><img src="/static/images/savetodriveoriginal.jpg"></a>');
		}else{
			$('#savetodrivemsg').html("");
			var fileName = creatFileName(downloadformat,displaytype);
			$('#savetodrive-div').empty();
			gapi.savetodrive.render('savetodrive-div', {
		            src: constructDownlodUrl(downloadformat,displaytype),
		            filename: fileName,
		            sitename: 'Engineering Village'
		          });
		}
}

function constructDownlodUrl(downloadformat,displaytype){
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

    downloadUrl += "&downloadMedium=googledrive";
    return downloadUrl;
}

function creatFileName(downloadformat,displaytype){
	var filename = "";
	var dt = new Date();
	filename += dt.getDate();
	filename += '-';
	filename += (dt.getMonth() + 1 );
	filename += '-';
	filename += dt.getFullYear();
	filename += '-';
	filename += dt.getHours();
	filename += '-';
	filename += dt.getMinutes();
	filename += '-';
	filename += dt.getMilliseconds();
	filename += '_';
	filename += displaytype;
	filename += '_';
	filename += downloadformat;
	if(downloadformat == 'ascii'){
		filename += '_.txt';
	}else if(downloadformat == 'excel'){
		filename += '_.xlsx';
	}else{
		filename += '_.'+downloadformat;
	}
	return filename;
}

function tooltip_show(tooltipId,event){
    thistip = document.getElementById("savetodrivemsg");
    thistip.style.visibility = 'visible';

}

function tooltip_hide(tooltipId){
	thistip = document.getElementById("savetodrivemsg");
    thistip.style.visibility = 'hidden';
}

function submitGoogleDriveDL(){
	var downloadformat = $('input[name="downloadformat"]:checked').val();
	var displaytype = $('input[name="displayformat"]:checked').val();
	var docidlist = $("#docidlist").val();
	var handlelist = $("#handlelist").val();
	var folderid = $("#folderid").val();
	var baseaddress = $("input[name='baseaddress']").val();

	var googleDrivePageUrl = 'https://'+baseaddress+'/delivery/download/googledrive.url?downloadformat='+downloadformat+'&displayformat='+displaytype;

	if (docidlist && docidlist.length > 0)
		googleDrivePageUrl += "&docidlist=" + docidlist;
	if (handlelist && handlelist.length > 0)
		googleDrivePageUrl += "&handlelist=" + handlelist;
    if (folderid && folderid.length > 0)
    	googleDrivePageUrl += "&folderid=" + folderid;


	GALIBRARY.createWebEventWithLabel('Google Drive', 'Save Initiated', downloadformat);
	var new_window1 = window.open(googleDrivePageUrl, 'GoogleDrive', "height=350,width=820,resizable=yes,scrollbars=yes");
	new_window1.focus();
}
