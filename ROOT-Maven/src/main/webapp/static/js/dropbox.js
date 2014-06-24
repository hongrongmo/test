var client;

var dropBoxDownloadUrl = "";
var dropBoxClientid = "";
var filename = "";
var dwnldfrmt = "";

$(document).ready(function(){
	dropBoxClientid = $("#dropBoxClientid").val();
	dwnldfrmt = $("#downloadformat").val();
	if(dropBoxClientid == null || dropBoxClientid == ""){
		 document.getElementById('dropboxredirectform').submit();
	}else{
		dropBoxDownloadUrl = $("#dropBoxDownloadUrl").val();
		if(dropBoxDownloadUrl == null || dropBoxDownloadUrl == ""){
			$("#errorInfo").css('display','block');
		}else{
			loadScript('https://www.dropbox.com/static/api/dropbox-datastores-1.0-latest.js', loadIntoDropbox);
		}

	}
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

function loadIntoDropbox(){
	dropBoxDownloadUrl = $("#dropBoxDownloadUrl").val();
	dropBoxClientid = $("#dropBoxClientid").val();
	client = new Dropbox.Client({ key: dropBoxClientid });
	var authDriver = new Dropbox.AuthDriver.Redirect({rememberUser: false});
	client.authDriver(authDriver);
	client.authenticate(function (error, client) {

		$("#connectioninprogress").css('display','none');
		if (error) {
        	$("#errorInfo").css('display','block');
        	$("#dbxusrmsg").css('display','block');
  			GALIBRARY.createWebEventWithLabel('Dropbox', 'Save Failed', dwnldfrmt);
        } else {
        	var isAuthenticated = false;
        	isAuthenticated = client.isAuthenticated();
        	if(isAuthenticated){
         		client.getAccountInfo(null,parseAccountInfo);
         	}else{
         		$("#errorInfo").css('display','block');
         	}
        	$("#dbxusrmsg").css('display','block');
        }
	});
}

function parseAccountInfo(apiError, acctInfo, jsonobj){
	if(apiError){
		$("#errorInfo").css('display','block');
		GALIBRARY.createWebEventWithLabel('Dropbox', 'Save Failed', dwnldfrmt);
	}else{
		var userName = jsonobj.display_name;
		var userEmail = jsonobj.email;
		 $("#dbxUserName").html(userName);
		 $("#dbxEmail").html(userEmail);
		 filename =  creatFileName();
		 $("#filename").html(filename);
		 $("#savedfilename").html(filename);
		 $("#userInfo").css('display','block');
		 $("#fileInfo").css('display','block');
		 $("#acctMsg").css('display','inline');
	}

}

function savefile(e){
	e.preventDefault();
	var oReq = new XMLHttpRequest();
	oReq.open("GET",  unescape(dropBoxDownloadUrl), true);
 	oReq.responseType = "blob";
 	$("#fileInfo").css('display','none');
 	$("#saveInfo").css('display','block');
 	oReq.onload = function(oEvent) {
 		if(oReq.status == 200) {
 			var blob = oReq.response;
       	  	client.writeFile(filename, blob, function (error) {
      			$("#saveinprogress").css('display','none');
      		  	if (error) {
                	$("#savesuccess").css('display','none');
              		$("#savefailed").css('display','block');
          			GALIBRARY.createWebEventWithLabel('Dropbox', 'Save Failed', dwnldfrmt);
              	} else {
          			GALIBRARY.createWebEventWithLabel('Dropbox', 'Save Success', dwnldfrmt);
              		client.appInfo(dropBoxClientid,parseAppInfo);
              	}
       	  	});
 		}else{
 			$("#saveinprogress").css('display','none');
 			$("#savesuccess").css('display','none');
      		$("#savefailed").css('display','block');
  			GALIBRARY.createWebEventWithLabel('Dropbox', 'Save Failed', dwnldfrmt);
 		}
 	};
 	oReq.send();
 	return false;
}

function parseAppInfo(apiError, appInfo){
	if(apiError){
		$("#savesuccess").css('display','block');
			$("#savefailed").css('display','none');
	}else{
		$("#savedfolder").html('<a href="https://www.dropbox.com/home/Apps/'+appInfo['name']+'" title="/Apps/'+appInfo['name']+'">Go to dropbox folder</a>');
		$("#savesuccess").css('display','block');
			$("#savefailed").css('display','none');
	}

}

function creatFileName(){
	var downloadformat = $("#downloadformat").val();
	var displayformat = $("#displayformat").val();
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
	filename += displayformat;
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