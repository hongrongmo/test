<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld"
	prefix="stripes"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">

<style>
	#oneClickMid, #oneClickContent, #oneClickBottom{
		width:585px;
	}
	#oneClickRight{
		float:left;
		width:205px;
		height:100%;

	}
	#oneClickLeft{
		float:left;
		width: 175px;
		height:100%;
	}
	#oneClickRight ul{
		list-style:none;
		margin:0px;
		padding:0px;
		padding-left:20px;
		height:175px;
		border-left: #808080 solid 1px;
	}
	#oneClickLeft ul{
		list-style:none;
		margin:0px;
		padding:0px;
	 	height:175px;

	}
	#oneClickRight li, #oneClickLeft li{
		font-weight:bold;
		clear: both;
		height:25px;
	}
   #oneClickRight input, #oneClickRight label,  #oneClickLeft label, #oneClickLeft input {
      float: left;
    }
    #oneClickRight label{
      width: 84%;
    }
	#oneClickLeft label  {
      width: 120px;
      vertical-align:text-bottom;
    }

	#oneClickTitle{
		color:#148C75;
		font-size:14px;
		font-weight:bold;
		padding-top: 11px;
	}
	#oneClickMid{
		pading-top:5px;
		height:195px;
	}
	.outputLocation{
		padding-top: 8px;
		padding-left: 28px;
		font-weight: bold;
		color: #007ee5;
	}
	#oneClickLeft label img{
		width:18px;
		padding-right:3px;
	}

	.outputSelectedBorder{
		border:#3ca690 2px solid;
	}
	#savePrefsButton{

    	color:#FFFFFF;
    	background-color:#3ca690;
    	border-radius:11px;
    	width:100px;
    	height:27px;
    	border:none;

    }
	.smalltxt{
		font-size:10px;
	}
	.btmTextRight{
		width: 215px;
	}
	.saveCancel{
		text-align: right;
		Padding-top: 12px;
		width: 170px;
		float:left;
	}
	.grayText{color:#808080;}
	.btmTextRight{
		float:left;
	}
	.sectionHead{
		text-align:center;
		padding-bottom:7px;
	}
	#dropBoxLinkSpan{
		border: 1px solid #01A9DB;
		padding-bottom: 6px;
		padding-top: 4px;
		padding-left: 2px;
		padding-right: 4px;
		border-radius:11px;
	}
	#oneClickMid input{
		margin-top:1px
	}
	#oneClickContent hr{display:block;}
    .smalltxt{
    	font-size:10px;
    	padding-left:3px;
    	color:#808080;
    }
    
    #fnpfx{
    	padding-left: 2px;
    }
    
</style>
</head>
<body>
<c:set value="${actionBean.context.userSession.user.userPrefs.dlFormat}" var="dlFormat"></c:set>
<c:set value="${actionBean.context.userSession.user.userPrefs.dlOutput}" var="dlOutput"></c:set>
<c:set value="${actionBean.context.userSession.user.userPrefs.dlLocation}" var="dlLocation"></c:set>
<c:set value="${actionBean.context.userSession.user.userPrefs.dlFileNamePrefix}" var="dlFileNamePrefix"></c:set>

<stripes:form name="download" id="download" method="post" action="/delivery/download/submit.url">
	<stripes:hidden name="sessionid" id="sessionid"/>
	<stripes:hidden name="docidlist" id="docidlist" />
	<stripes:hidden name="handlelist" id="handlelist" />
	<stripes:hidden name="folderid" id="folderid" />
	<stripes:hidden name="database" id="database" />
	<stripes:hidden name="baseaddress" id="baseaddress" />
	<div id="oneClickContent">
		<div id="oneClickTitle">Choose your download settings for this session</div>
		<hr/>
		<div id="oneClickMid">
			<div id="oneClickLeft">
				<div class="grayText sectionHead">Location:</div>
				<ul>
				<li><input type="radio" class="outputLocation" id="outputMyPC"     name="outputLocation" value="mypc" <c:if test="${dlLocation eq 'mypc'}">checked="checked"</c:if>/><label	for="outputMyPC" title="Download the citation section"><img src="/static/images/Download.png" alt="Save to my PC Icon" />My PC</label></li>
				<li><input type="radio" class="outputLocation" id="outputRefWorks" name="outputLocation" value="refworks"  <c:if test="${dlLocation eq 'refworks'}">checked="checked"</c:if>/><label for="outputRefWorks" title="Download the abstract section"><img src="/static/images/refworks_icon.jpg" alt="Reforks Icon" />RefWorks</label></li>
				<li><input type="radio" class="outputLocation" id="outputGoogle"   name="outputLocation" value="googledrive"  <c:if test="${dlLocation eq 'googledrive'}">checked="checked"</c:if>/><label for="outputGoogle" title="Download the detailed record"><img src="/static/images/drive_icon.png" alt="Google Drive Icon" />Google Drive</label></li>
				<li><input type="radio" class="outputLocation" id="rdDropbox"      name="outputLocation" value="dropbox"  <c:if test="${dlLocation eq 'dropbox'}">checked="checked"</c:if>/><label for="outputDropbox" title="Download the detailed record"><img src="/static/images/dropbox_icon.png" alt="Dropbox Icon"/>Dropbox</label></li>
				</ul>
			</div>
			<div id="oneClickRight">
			<div class="grayText sectionHead">Format:</div>
			<ul>
				<li><input type="radio" class="typeEnabled" id="rdRis" name="downloadformat" value="ris"  <c:if test="${dlFormat eq 'ris' or dlFormat eq 'refworks' or dlLocation eq 'refworks'}">checked="checked"</c:if>/><label	for="rdRis" title="RIS Format (EndNote, ProCite, Reference Manager)">RIS<span class="smalltxt">(EndNote, Ref. Manager)</span></label></li>
				<li><input type="radio" class="typeEnabled" id="rdBib" name="downloadformat" value="bib"  <c:if test="${dlFormat eq 'bib'}">checked="checked"</c:if>/><label for="rdBib" title="BibTeX format">BibTeX</label></li>
				<li><input type="radio" class="typeEnabled" id="rdAsc" name="downloadformat" value="ascii"  <c:if test="${dlFormat eq 'ascii'}">checked="checked"</c:if>/><label for="rdAsc" title="Plain text format (ASCII)">Text<span class="smalltxt">(ASCII)</span></label></li>
				<li><input type="radio" class="typeEnabled" id="rdCsv" name="downloadformat" value="csv"  <c:if test="${dlFormat eq 'csv'}">checked="checked"</c:if>/><label for="rdCsv" title="(Comma Separated Value Format)">CSV </label></li>
				<li><input type="radio" class="typeEnabled" id="rdExcel" name="downloadformat" value="excel"  <c:if test="${dlFormat eq 'excel'}">checked="checked"</c:if>/><label for="rdExcel" title="Microsoft Excel">Excel&reg;</label></li>
				<li><input type="radio" class="typeEnabled" id="rdPdf" name="downloadformat" value="pdf"  <c:if test="${dlFormat eq 'pdf'}">checked="checked"</c:if>/><label for="rdPdf" title="PDF">PDF</label></li>
				<li><input type="radio" class="typeEnabled" id="rdRtf" name="downloadformat" value="rtf"  <c:if test="${dlFormat eq 'rtf'}">checked="checked"</c:if>/><label for="rdRtf" title="(Rich Text Format, e.g. Word)">RTF<span class="smalltxt">(Word&reg;)</span></label></li>
			</ul>


			</div>
			<div id="oneClickRight">
				<div class="grayText sectionHead">Output:</div>
				<ul>
				<li><input type="radio" class="typeEnabled" id="rdDefault" name="displayformat" value="default"  <c:if test="${dlOutput eq 'default'}">checked="checked"</c:if>/><label for="rdDefault" title="Download the Format for this Page">Current page view</label></li>
				<li><input type="radio" class="typeEnabled" id="rdCit" name="displayformat" value="citation"  <c:if test="${dlOutput eq 'citation'}">checked="checked"</c:if>/><label	for="rdCit" title="Download the citation section">Citation</label></li>
				<li><input type="radio" class="typeEnabled" id="rdAbs" name="displayformat" value="abstract"  <c:if test="${dlOutput eq 'abstract'}">checked="checked"</c:if>/><label for="rdAbs" title="Download the abstract section">Abstract</label></li>
				<li><input type="radio" class="typeEnabled" id="rdDet" name="displayformat" value="detailed"  <c:if test="${dlOutput eq 'detailed'}">checked="checked"</c:if>/><label for="rdDet" title="Download the detailed record">Detailed record</label></li>
				<li>
					<hr style="width:100%" />
					<div class="grayText" id="fnpfx">File name prefix:</div>
					<div id="fileNamePrefixContainer"><input type="text" value="${dlFileNamePrefix}" name="filenameprefix" id="filenameprefix" maxlength="30" /></div>
				</li>
				
				</ul>
				
				
			</div>
		</div>
		<hr style="width: 100%;"/>
		<div id="oneClickBottom">

				<div class="grayText btmTextRight">Note: Your selected records <br/>(to a maximum of 500) will be  <br/>kept until your session ends.</div>
				<div class="saveCancel">

					<input type="button" value="Save" name="Save" id="savePrefsButton" />
					<a href="#" style="padding-right:7px;" onclick="$('#downloadlink').tooltipster('destroy');">Cancel</a>
				</div>
			</div>
	</div>
</stripes:form>
</body>

<script>

$(document).ready(function() {

	$(".outputLocation").click(function (){
		checkForRefworks(this);

	});

	//read the cookie
	if($.cookie("ev_oneclickdl")){
		var dlOptions = JSON.parse($.cookie("ev_oneclickdl"));
		$('#oneClickContent input[value="' + dlOptions.location + '"]').prop("checked", true);
		$('#oneClickContent input[value="'+dlOptions.displaytype+'"]').prop("checked", true);
		$('#oneClickContent input[value="'+dlOptions.format+'"]').prop("checked", true);
		$('#filenameprefix').val(dlOptions.filenameprefix);
	}
	checkForRefworks($('input[name="outputLocation"]:checked'));

});
function checkForRefworks(radio){
	var output = $(radio).attr("id");
	var defOutput = "";
		//console.log(output);
       //gapi.savetodrive.render('savetodrive-div', {
          //  src: '//localhost.engineeringvillage.com/path/to/myfile.pdf',
           // filename: 'My Statement.pdf',
           // sitename: 'Engineering Village'
          //});
	if(output == "outputRefWorks"){
		//disable output type selection and select RIS as default
		$("#rdRis").prop("checked",true);
		$("#rdCit").prop("checked", true);
		$(".typeEnabled").prop("disabled", true);
		$(".typeEnabled").parent().find("label").addClass("grayText");
		$("#savePrefsButton").show();
        $("#dropBoxLinkSpan").hide();
	}else{
		$(".typeEnabled").prop("disabled",false);
		$(".typeEnabled").parent().find("label").removeClass("grayText");
		$("#savePrefsButton").show();
        $("#dropBoxLinkSpan").hide();
	}
}
</script>
		<c:if test="${actionBean.saveToGoogleEnabled}">
			<script language="javascript"  src="/static/js/savetogoogle.js?v=${releaseversion}"></script>
		</c:if>
		<c:if test="${actionBean.saveToDropboxEnabled}">
			<script language="javascript"  src="/static/js/savetodropbox.js?v=${releaseversion}"></script>
		</c:if>
		<script language="javascript" type="text/javascript">
			$("#savePrefsButton").click(function(event) {

				var baseaddress = $("input[name='baseaddress']").val();
				var displaytype = $('input[name="displayformat"]:checked').val();
				var actionDisplayType = '${actionBean.displayformat}';
				var downloadformat = $('input[name="downloadformat"]:checked').val();
				var downloadLocation = $('input[name="outputLocation"]:checked').val();
				var docidlist = $("#docidlist").val();
				var handlelist = $("#handlelist").val();
				var folderid = $("#folderid").val();
				var milli = (new Date()).getTime();
				
				
				
				if (downloadformat == undefined || downloadformat == "") {
					alert("You must choose a download format.");
					event.preventDefault();
					return (false);
				}
				
				var filenameprefix = $.trim($('#filenameprefix').val());
				if(filenameprefix.length < 3){
					alert("File name prefix cannot be empty and should have minimum of 3 characters");
					event.preventDefault();
					return (false);
				}
				if(filenameprefix.length > 50){
					alert("File name prefix cannot have more than 50 characters");
					event.preventDefault();
					return (false);
				}

				var url = "";
				GALIBRARY.createWebEventWithLabel('Output', 'Download', downloadformat);
				$.cookie('ev_oneclickdl', '{"location":"'+downloadLocation+'","format":"'+downloadformat+'","displaytype":"'+displaytype+'","baseaddress":"'+baseaddress+'","filenameprefix":"'+filenameprefix+'"}',{path:'/'});
				dlOptions = {
						location:downloadLocation,
						format:downloadformat,
						displaytype:displaytype,
						baseaddress:baseaddress,
						filenameprefix:filenameprefix
				};
				$("#dlprefsSaved").fadeIn("slow");

				if(displaytype == 'default'){
					//need to figure out what page they are on to get this right.
					if(typeof($("input[name='selectoption']:checked").val()) != "undefined" && downloadLocation != "refworks"){
						//we are on selected records get the option that is selected
						actionDisplayType = $("input[name='selectoption']:checked").val();
					}if(downloadLocation == "refworks"){
						actionDisplayType = "citation";
					}
					displaytype = actionDisplayType;
					$('#oneClickContent input[value="' + actionDisplayType + '"]').prop("checked", true);
				}
				// Refworks?
				var ret = true;
				changeOneClick(downloadLocation);
				if (downloadLocation == "refworks") {
					var refworksURL = "http://www.refworks.com/express/ExpressImport.asp?vendor=Engineering%20Village%202&filter=Desktop%20Biblio.%20Mgt.%20Software";
					url = "http://" + baseaddress
							+ "/delivery/download/refworks.url?downloadformat="
							+ downloadformat + "&timestamp=" + milli
							+ "&sessionid=" + $("#sessionid").val();
					if (docidlist && docidlist.length > 0)
						url += "&docidlist=" + docidlist;
					if (handlelist && handlelist.length > 0)
						url += "&handlelist=" + handlelist;
                    if (folderid && folderid.length > 0)
                        url += "&folderid=" + folderid;
					window.open(refworksURL + "&url=" + escape(url),
									"RefWorksMain",
									"width=800,height=500,scrollbars=yes,menubar=yes,resizable=yes,directories=yes,location=yes,status=yes");
                    event.preventDefault();
                    ret = false;
				}else if(downloadLocation == "dropbox"){
					submitDropboxDL();
					ret = false;
				}else if(downloadLocation == "googledrive"){
					submitGoogleDriveDL();
					ret = false;
				}else{
					$("#download").submit();
					ret = false;
				}

				$('#downloadlink').attr("title", "Click to change one click download preferences.");
				$('#downloadlink').tooltipster('hide');
				return ret;

			});


			</script>
</html>