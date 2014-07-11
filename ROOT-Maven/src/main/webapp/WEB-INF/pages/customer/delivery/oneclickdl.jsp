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
    <script src="https://apis.google.com/js/platform.js">
      {parsetags: 'explicit'}
    </script>
<style>
	#oneClickMid, #oneClickContent, #oneClickBottom{
		width:450px;
	}
	#oneClickLeft, #oneClickBottomLeft{
		float:left;
		width:220px;
	}
	#oneClickRight,#oneClickBottomRight{
		float:right;
		width:230px;
	}

	#oneClickRight ul{
		list-style:none;
		margin:0px;
		padding:0px;
	}
	#oneClickRight li{
		padding-bottom:10px;
		font-weight:bold;
		clear: both;
		height:25px;
	}
   #oneClickRight input, #oneClickRight label {
      float: left;
    }
    #oneClickRight label {
      width: 200px;
    }
	#oneClickTitle{
		color:#148C75;
		font-size:14px;
		font-weight:bold;
		padding-top: 11px;
	}
	#oneClickMid{
		pading-top:5px;
		height:200px;
	}
	.outputLocation{
		padding: 8px 0px;
		font-weight: bold;
		font-size: 14px;
		color: #007ee5;
	}
	.outputLocation img{
		width:18px;
		padding-right:3px;
	}
	.outputLocation:hover{
		border:#3ca690 2px dashed;
		cursor:pointer;
	}
	.outputLocationSelected{
		background-image:url(/static/images/check_icon_sm.png);
		background-repeat:no-repeat;
		background-position:right;'
		border: solid 1px #3ca690;
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
	}
	.grayText{color:#808080;}
</style>
</head>
<body>
<c:set value="${actionBean.context.userSession.user.userPrefs.dlFormat}" var="dlFormat"></c:set>
<c:set value="${actionBean.context.userSession.user.userPrefs.dlOutput}" var="dlOutput"></c:set>
<c:if test="${dlOutput eq 'default' }"><c:set value="${actionBean.displayformat}" var="dlOutput"></c:set></c:if>
<div id="oneClickContent">
	<div id="oneClickTitle">Choose your default reference manager or file type</div>
	<hr/>
	<div id="oneClickMid">
		<div id="oneClickLeft">
			<div class="outputLocation" id="outputGoogle"><img src="/static/images/drive_icon.png" alt="Google Drive Icon" />Save to Google Drive</div>
			<div class="outputLocation" id="outputDropbox"><img src="/static/images/dropbox_icon.png" alt="Dropbox Icon" />Save to Dropbox</div>
			<div class="outputLocation" id="outputRefWorks"><img src="/static/images/refworks_icon.jpg" alt="Reforks Icon" />Save to RefWorks</div>
			<div class="outputLocation outputSelected" id="outputMyPC"><img src="/static/images/Download.png" alt="Save to my PC Icon" />Save to My PC</div>
		</div>
		<div id="oneClickRight">

		<ul>
			<li><input type="radio" class="typeEnabled" id="rdRis" name="downloadformat" value="ris" style="margin-top:1px" <c:if test="${dlFormat eq 'ris'}">checked="checked"</c:if>/><label	for="rdRis" title="RIS Format (EndNote, ProCite, Reference Manager)">Citation</label></li>
			<li><input type="radio" class="typeEnabled" id="rdBib" name="downloadformat" value="bib" style="margin-top:1px" <c:if test="${dlFormat eq 'bib'}">checked="checked"</c:if>/><label for="rdBib" title="BibTex format">Abstract</label></li>
			<li><input type="radio" class="typeEnabled" id="rdAsc" name="downloadformat" value="ascii" style="margin-top:1px" <c:if test="${dlFormat eq 'ascii'}">checked="checked"</c:if>/><label for="rdAsc" title="Plain text format (ASCII)">Detailed record</label></li>
			<li><input type="radio" class="typeEnabled" id="rdCsv" name="downloadformat" value="csv" style="margin-top:1px" <c:if test="${dlFormat eq 'csv'}">checked="checked"</c:if>/><label for="rdCsv" title="(Comma Separated Value Format)">CSV </label></li>
			<li><input type="radio" class="typeEnabled" id="rdExcel" name="downloadformat" value="excel" style="margin-top:1px" <c:if test="${dlFormat eq 'excel'}">checked="checked"</c:if>/><label for="rdExcel" title="Microsoft Excel">Microsoft Excel&reg;</label></li>
			<li><input type="radio" class="typeEnabled" id="rdPdf" name="downloadformat" value="pdf" style="margin-top:1px" <c:if test="${dlFormat eq 'pdf'}">checked="checked"</c:if>/><label for="rdPdf" title="PDF">PDF</label></li>
			<li><input type="radio" class="typeEnabled" id="rdRtf" name="downloadformat" value="rtf" style="margin-top:1px" <c:if test="${dlFormat eq 'rtf'}">checked="checked"</c:if>/><label for="rdRtf" title="(Rich Text Format, e.g. Word)">RTF</label></li>
		</ul>


		</div>
	</div>
	<hr style="width: 100%;"/>
	<div id="oneClickBottom">
		<div id="oneClickBottomLeft">
			<div class="grayText">Choose the information to save:</div>
			<div>
			<select name="displayformat" id="displayformat">
                 <option value="citation" <c:if test="${dlOutput eq 'citation'}">selected="selected"</c:if>>Citation</option>
                 <option value="abstract" <c:if test="${dlOutput eq 'abstract'}">selected="selected"</c:if>>Abstract</option>
                 <option value="detailed" <c:if test="${dlOutput eq 'detailed'}">selected="selected"</c:if>>Detailed record</option>
             </select>
             </div>
		</div>
		<div id="oneClickBottomRight">
		<div class="grayText btmTextRight">Note: Your selected records <br/>(to a maximum of 500) will be kept until your session ends.</div>
		<div class="saveCancel"><a href="#" style="padding-right:7px;" onclick="$('#downloadlink').tooltipster('destroy');">Cancel</a><input type="submit" value="Save" name="Save" id="savePrefsButton" />

			<div id="savetodrive-div">

			</div>
		</div>
		</div>
	</div>
</div>
</body>

<script>

$(document).ready(function() {
	$(".outputLocation").click(function (){
		var output = $(this).attr("id");
		var defOutput = "";
		if(output == "outputGoogle"){
	        gapi.savetodrive.render('savetodrive-div', {
	            src: '//localhost.engineeringvillage.com/path/to/myfile.pdf',
	            filename: 'My Statement.pdf',
	            sitename: 'Engineering Village'
	          });

		}

		if(output == "outputRefWorks"){
			//disable output type selection and select RIS as default
			$("#rdRis").prop("checked",true);
			$(".typeEnabled").prop("disabled", true);
			$("li label").addClass("grayText");
		}else{
			$(".typeEnabled").prop("disabled",false);
			$("li label").removeClass("grayText");
		}
		$(".outputLocationSelected").removeClass("outputLocationSelected");
		$(this).addClass("outputLocationSelected");

	});
	$("#savetodrive-div").click(function(){console.log("clicked");});
});
</script>
</html>