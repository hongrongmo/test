<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld" prefix="stripes"%>
<style type="text/css">

	.settingsContents{
	padding-top:5px;
	height:463px;
	}


	.prefsRadios{
	list-style: none;
	margin: 0px;
	padding: 0px;
	vertical-align: middle;

	}
	.settingHeading{
		color:#3ca690;
		font-weight:bold;
		font-size:14px;
		padding-top:15px;
	}
	.settingSection h2{
		color:#3ca690;
		font-style:italic;
		font-weight:bold;
		padding-top:7px;
		font-size:12px;
	}
	.prefsRadios input{
		vertical-align:bottom;
	}
	.settingsLeft{
	float:left;
	border-right:2px solid #d7d7d7;
	width:40%;
	padding-right:16px;
	height:420px
	}
	.settingsRight{
		float:right;
		width:48%;
	}
	.mm_eilogo{
		background: #FFFFFF;
		padding-right: 4px;
	}

    .settingsContents fieldset input, .settingsContents label {
     /*  float: left;
      display: block; */

    }
	.settingsContents label {
		width:100%;
	}
    .settingsContents fieldset li {
      clear: both;
    }
    #savePrefsButton{

    	color:#FFFFFF;
    	background-color:#3ca690;
    	border-radius:11px;
    	width:100px;
    	height:27px;
    	border:none;

    }
    .saveCancel{
    float:right;
    margin-bottom:5px
    }
    .saved{
    	color:#3ca690;
		font-weight: bold;
		padding-right: 20px;
		font-size: 14px;
    }
    .conflict{
    color:red;
    font-weight:bold;
    font-size:11px;
    }
    .settingsContents hr{display:block;}
    #highlightColorli div.sp-replacer{margin-right:5px;margin-bottom:5px;}
    .smalltxt{
    	padding-left:3px;
    	font-size:10px;
    	color:#808080;
    }
    .title h1{color:#FFFFFF;margin-top:0px;margin-bottom:0px;}
</style>

<div id="settingsPopup" >
<div class="title" style="padding-left:0px;"><h1 id="myPrefTitle" aria-role="alert"><img id="eilogo" class="mm_eilogo" width="35px" src="/static/images/ei_w.gif" title="Engineering Information: Home of Engineering Village" ></img>&nbsp;&nbsp;My Preferences</h1></div>
<form  method="post" name="preferencesForm" id="userPreferencesForm"  onsubmit="submitSavePrefsForm();return false;">
<div class="settingsContents" id="settingsContents" >
	<div id="valerrormsgcontainer" style="display:none"><img style="position:relative;top:-3px" src="/static/images/red_warning.gif"/><b>&nbsp;&nbsp;<span id="valerrormsg"></span></b></div>

	<div class="settingsLeft">
			<input type="hidden" name="save" value="true"/>

			<div class="settingSection"><h2>Display Results</h2></div>

			<c:set value="${actionBean.currentuserprefs.resultsPerPage}" var="resultsPerPage"></c:set>
			<fieldset title="Results Per Page Settings">
				<ul class="prefsRadios">
				<c:forEach items="${actionBean.context.userSession.user.userPrefs.resultsPerPageOpt}" var="resultsNum">
					<li><input type="radio" name="resultsPerPage" value="${resultsNum}" id="${resultsNum}_radio" <c:if test="${resultsPerPage eq resultsNum}">checked="checked"</c:if>/><label for="${resultsNum}_radio">${resultsNum}</label></li>
				</c:forEach>
				</ul>
			</fieldset>
			<hr/>
			<div class="settingSection"><h2>Sorting Default</h2></div>
			<c:set value="${actionBean.currentuserprefs.sort}" var="sort"></c:set>
			<fieldset title="Sorting Settings">
			<ul class="prefsRadios">
				<li><input type="radio" name="sortOrder" value="relevance" id="relevance_radio" <c:if test="${sort eq 'relevance'}">checked="checked"</c:if>/><label for="relevance_radio">Relevance</label></li>
				<li><input type="radio" name="sortOrder" value="yr" id="datenew_radio" <c:if test="${sort eq 'yr'}">checked="checked"</c:if>/><label for="datenew_radio">Date (Newest)</label></li>
			</ul>
			</fieldset>
			<hr/>
			<div class="settingSection"><h2>Show Preview</h2></div>
			<c:set value="${actionBean.currentuserprefs.showPreview}" var="showPreview"></c:set>
			<fieldset title="Show Preview">
			<ul class="prefsRadios">
				<li><input type="radio" name="showPreview" value="false" id="hideall_radio" <c:if test="${showPreview eq 'false'}">checked="checked"</c:if>/><label for="hideall_radio">Hide All</label></li>
				<li><input type="radio" name="showPreview" value="true" id="showall_radio" <c:if test="${showPreview eq 'true'}">checked="checked"</c:if>/><label for="showall_radio">Show All</label></li>

			</ul>
			</fieldset>
			<c:if test="${actionBean.context.userSession.user.highlightingEnabled}">
				<hr/>
				<div class="settingSection"><h2>Search Terms</h2></div>
				<c:set value="${actionBean.currentuserprefs.highlight}" var="highlightColor"></c:set>
				<c:set value="${actionBean.currentuserprefs.highlightBackground}" var="highlightBackground"></c:set>
				<fieldset title="Highlight Search Terms">
				<ul class="prefsRadios">
					<li id="highlightColorli"><label for="hlight_color" <c:if test="${highlightBackground}">style="color:gray;"</c:if>><input type="text" name="highlightColor"  id="hlight_color" />Text Color</label></li>
					<li><input type="checkbox" name="highlightBackground"  id="hlight_bg_chkbx" <c:if test="${highlightBackground}">checked="checked"</c:if>/><label for="hlight_bg_chkbx" style="width:75%;padding-top:2px;">Background Highlight</label></li>
				</ul>
				</fieldset>
			</c:if>
	</div>
	<div class="settingsRight">

		<input type="hidden" name="save" value="true"/>
			<c:set value="${actionBean.currentuserprefs.dlLocation}" var="dlLocation"></c:set>

			<div class="settingSection"><h2>Download location</h2></div>
			<fieldset title="Download location Settings">
			<ul class="prefsRadios">
				<li><input class="locationRadio" type="radio" name="dlLocation" value="mypc" id="mypc_radio" <c:if test="${dlLocation eq 'mypc'}">checked="checked"</c:if>/><label for="mypc_radio">My PC</label></li>
				<li><input class="locationRadio" type="radio" name="dlLocation" value="refworks" id="refworks_radio" <c:if test="${dlLocation eq 'refworks'}">checked="checked"</c:if>/><label for="refworks_radio">RefWorks</label></li>
				<li><input class="locationRadio" type="radio" name="dlLocation" value="googledrive" id="googledrive_radio" <c:if test="${dlLocation eq 'googledrive'}">checked="checked"</c:if>/><label for="googledrive_radio">Google Drive</label></li>
				<li><input class="locationRadio" type="radio" name="dlLocation" value="dropbox" id="dropbox_radio" <c:if test="${dlLocation eq 'dropbox'}">checked="checked"</c:if>/><label for="dropbox_radio">Dropbox</label></li>
			</ul>
			</fieldset>
				<hr/>
		<div class="settingSection"><h2>Download format</h2></div>
		<c:set value="${actionBean.currentuserprefs.dlFormat}" var="dlFormat"></c:set>
		<fieldset title="Dounload Format Settings">
			<ul class="prefsRadios">
				<li><input class="formatRadio" type="radio" name="dlFormat" value="ris" id="ris_radio" <c:if test="${dlFormat eq 'ris' or dlFormat eq 'refworks'}">checked="checked"</c:if>/><label for="ris_radio">RIS<span class="smalltxt">(EndNote, Ref. Manager)</span></label></li>
				<li><input class="formatRadio" type="radio" name="dlFormat" value="bib" id="bib_radio" <c:if test="${dlFormat eq 'bib'}">checked="checked"</c:if>/><label for="bib_radio">BibTeX</label></li>
				<li><input class="formatRadio" type="radio" name="dlFormat" value="ascii" id="ascii_radio" <c:if test="${dlFormat eq 'ascii'}">checked="checked"</c:if>/><label for="ascii_radio">Text<span class="smalltxt">(ASCII)</span></label></li>
				<li><input class="formatRadio" type="radio" name="dlFormat" value="csv" id="csv_radio" <c:if test="${dlFormat eq 'csv'}">checked="checked"</c:if>/><label for="csv_radio">CSV</label></li>
				<li><input class="formatRadio" type="radio" name="dlFormat" value="excel" id="excel_radio" <c:if test="${dlFormat eq 'excel'}">checked="checked"</c:if>/><label for="excel_radio">Excel&reg;</label></li>
				<li><input class="formatRadio" type="radio" name="dlFormat" value="pdf" id="pdf_radio" <c:if test="${dlFormat eq 'pdf'}">checked="checked"</c:if>/><label for="pdf_radio">PDF</label></li>
				<li><input class="formatRadio" type="radio" name="dlFormat" value="rtf" id="rtf_radio" <c:if test="${dlFormat eq 'rtf'}">checked="checked"</c:if>/><label for="rtf_radio">RTF<span class="smalltxt">(Word&reg;)</span></label></li>
			</ul>
	  		</fieldset>
			<c:set value="${actionBean.currentuserprefs.dlOutput}" var="dlOutput"></c:set>
			<hr/>
			<div class="settingSection"><h2>Download output</h2></div>
			<fieldset title="Download Output Settings">
			<ul class="prefsRadios">
				<li><input  class="outputRadio" type="radio" name="dlOutput" value="default" id="default_radio" <c:if test="${dlOutput eq 'default'}">checked="checked"</c:if>/><label for="default_radio">Current page view</label></li>
				<li><input class="outputRadio" type="radio" name="dlOutput" value="citation" id="citation_radio" <c:if test="${dlOutput eq 'citation'}">checked="checked"</c:if>/><label for="citation_radio">Citation</label></li>
				<li><input class="outputRadio" type="radio" name="dlOutput" value="abstract" id="abstract_radio" <c:if test="${dlOutput eq 'abstract'}">checked="checked"</c:if>/><label for="abstract_radio">Abstract</label></li>
				<li><input class="outputRadio" type="radio" name="dlOutput" value="detailed" id="detailed_radio" <c:if test="${dlOutput eq 'detailed'}">checked="checked"</c:if>/><label for="detailed_radio">Detailed record</label></li>
			</ul>
	    </fieldset>
			<c:set value="${actionBean.currentuserprefs.dlFileNamePrefix}" var="dlFileNamePrefix"></c:set>
			<input type="hidden" id="dlFileNamePrefixOrg" value="${dlFileNamePrefix}"/>
			<hr/>
			<div class="settingSection"><h2>File Name Prefix</h2></div>
			<fieldset title="File Name Prefix Settings">
				<div style="width:150px">
					<div style="width:150px"><input style="width:150px"  type="text" value="${dlFileNamePrefix}" name="dlFileNamePrefix" id="dlFileNamePrefix" onkeypress="return handleKeyPressForFileName(event)"  maxlength="50" /></div>
					<div style="text-align:right;width:150px"><span style="font-size:10px;" id="filenamelabel">&nbsp;&nbsp;_Output_Date/Time.format</span></div>
				</div>

	    	</fieldset>

	</div>

</div>
<div class="saveCancel"><input type="submit" value="Save" name="Save" id="savePrefsButton" /><a href="#" class="closePopup" style="padding-right:7px;" onclick="TINY.box.hide();" id="closePrefsMessage" title="Close Preferences Window">Cancel</a></div>
</form>
</div>

<script>
$(".locationRadio").click(function(){
	checkForRefworks(this);
	updatefilenamelable();
	checkForRisandBib();
});
$(".formatRadio,.outputRadio").click(function(){
	updatefilenamelable();
});

$(".formatRadio,.outputRadio").click(function(){
	updatefilenamelable();
});

$(".formatRadio").click(function(){
	checkForRisandBib();
});

function checkForRisandBib(){
	var formatval = $(".formatRadio:checked").val();
	if(formatval ==  'ris' || formatval ==  'bib'){
		$("#default_radio").prop("checked", true);
		$(".outputRadio").prop("disabled", true);
	}else{
		$(".outputRadio").prop("disabled", false);
	}
}

function checkForRefworks(rad){
	if($(rad).attr('id') == "refworks_radio"){
		$("#ris_radio").prop("checked", true);
		$("#citation_radio").prop("checked", true);
		$(".formatRadio").prop("disabled", true);
		$(".outputRadio").prop("disabled", true);
		handleFileNamePrefix();
		$("#dlFileNamePrefix").prop("disabled", true);
	}else{
		$(".formatRadio").prop("disabled", false);
		$(".outputRadio").prop("disabled", false);
		$("#dlFileNamePrefix").prop("disabled", false);
	}
}

function updatefilenamelable(){
	var formatval = $(".formatRadio:checked").val();
	var outputval = $(".outputRadio:checked").val();
	if(formatval ==  'ris'){
		outputval = "RIS";
	}else if(formatval ==  'bib'){
		outputval = "BIB";
	}
	if(outputval ==  'default'){
		outputval = "current_page_view";
	}
	if(formatval === "ascii"){
		formatval = "txt";
	}else if (formatval === "bib"){
		formatval = "bib";
	}else if(formatval === "csv"){
		formatval = "csv";
	}else if(formatval === "pdf"){
		formatval = "pdf";
	}else if(formatval === "rtf"){
		formatval = "rtf";
	}else if(formatval === "excel"){
		formatval = "xlsx";
	}else if(typeof  formatval === "undefined" || formatval === null || formatval === ""){
		formatval = "format";
	}else {
		formatval = "ris";
	}
	if(typeof  outputval === "undefined" || outputval === null || outputval === "") {
		outputval = "Output";
	}
	$('#filenamelabel').html('_'+outputval+'_Date/Time.'+formatval);
}

function handleKeyPressForFileName(event){
	if (event.keyCode == 13) {
		submitSavePrefsForm();
		event.preventDefault();
		return false;
    }
}

function handleFileNamePrefix(){
	var fileNamePrefix = $.trim($('#dlFileNamePrefix').val());
	if(fileNamePrefix == null || fileNamePrefix.length > 50 || fileNamePrefix.length < 3 || !isValidInput(fileNamePrefix)){
		$('#dlFileNamePrefix').val($('#dlFileNamePrefixOrg').val());
	}
	$("#valerrormsgcontainer").css("display","none");
}

checkForRefworks($("input[name=dlLocation]:checked"));
updatefilenamelable();
checkForRisandBib();

</script>
<c:if test="${actionBean.context.userSession.user.highlightingEnabled}">
<script>
$("#hlight_color").spectrum({
    showPaletteOnly: true,
    <c:if test="${highlightBackground}">disabled:true,</c:if>
    showPalette:true,
    color: '<c:choose><c:when test="${highlightBackground}">#000000</c:when><c:otherwise>${highlightColor}</c:otherwise></c:choose>',
    preferredFormat:'hex',
    palette: [
        ['#ff8200','#2babe2','#158c75', "#000000"]
    ]
});
$("#hlight_bg_chkbx").click(function(e) {
	var oldColor = "${highlightColor}";
	if ($(this).prop('checked')) {
		$("#hlight_color").spectrum("set", "#000000");
		$("#hlight_color").spectrum("disable");
		$("#hlight_color_lbl").css("color", "gray");


	} else {
		$("#hlight_color").spectrum("enable");
		$("#hlight_color").spectrum("set", oldColor);
		$("#hlight_color_lbl").css("color", "black");

	}

});
</script>
</c:if>