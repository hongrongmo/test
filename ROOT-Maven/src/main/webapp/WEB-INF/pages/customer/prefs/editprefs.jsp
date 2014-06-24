<%@ page language="java"  pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld" prefix="stripes"%>
<style type="text/css">
	.settingsContents{
	padding-top:15px;
	height:315px;
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
	.settingSection{
	color:#3ca690;
	font-style:italic;
	font-weight:bold;
	padding-top:7px;

	}
	.prefsRadios input{
		vertical-align:bottom;
	}
	.settingsLeft{
	float:left;
	border-right:2px solid #d7d7d7;
	width:40%;
	padding-right:16px;
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
      float: left;
      display: block;

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
</style>
<div id="settingsPopup">
<div class="title" style="padding-left:0px;"><img id="eilogo" class="mm_eilogo" width="35px" src="/static/images/ei_w.gif" title="Engineering Information: Home of Engineering Village" alt="Engineering Information: Home of Engineering Village"></img>&nbsp;&nbsp;My Preferences</div>
<form  method="post" name="preferencesForm" id="userPreferencesForm"  onsubmit="submitSavePrefsForm();return false;">
<div class="settingsContents" >

	<div class="settingsLeft">
			<input type="hidden" name="save" value="true"/>
			<div class="settingSection">Display Results</div>

			<c:set value="${actionBean.currentuserprefs.resultsPerPage}" var="resultsPerPage"></c:set>
			<fieldset title="Results Per Page Settings">
				<ul class="prefsRadios">
				<c:forEach items="${actionBean.context.userSession.user.userPrefs.resultsPerPageOpt}" var="resultsNum">
					<li><label for="${resultsNum}_radio"><input type="radio" name="resultsPerPage" value="${resultsNum}" id="${resultsNum}_radio" <c:if test="${resultsPerPage eq resultsNum}">checked="checked"</c:if>/>${resultsNum}</label></li>
				</c:forEach>
				</ul>
			</fieldset>
			<hr/>
			<div class="settingSection">Sorting Default</div>
			<c:set value="${actionBean.currentuserprefs.sort}" var="sort"></c:set>
			<fieldset title="Sorting Settings">
			<ul class="prefsRadios">
				<li><label for="relevance_radio"><input type="radio" name="sortOrder" value="relevance" id="relevance_radio" <c:if test="${sort eq 'relevance'}">checked="checked"</c:if>/>Relevance</label></li>
				<li><label for="datenew_radio"><input type="radio" name="sortOrder" value="yr" id="datenew_radio" <c:if test="${sort eq 'yr'}">checked="checked"</c:if>/>Date New</label></li>
			</ul>
			</fieldset>
			<hr/>
			<div class="settingSection">Show Preview</div>
			<c:set value="${actionBean.currentuserprefs.showPreview}" var="showPreview"></c:set>
			<fieldset title="Show Preview">
			<ul class="prefsRadios">
				<li><label for="hideall_radio"><input type="radio" name="showPreview" value="false" id="hideall_radio" <c:if test="${showPreview eq 'false'}">checked="checked"</c:if>/>Hide All</label></li>
				<li><label for="showall_radio"><input type="radio" name="showPreview" value="true" id="showall_radio" <c:if test="${showPreview eq 'true'}">checked="checked"</c:if>/>Show All</label></li>

			</ul>
			</fieldset>
	</div>
	<div class="settingsRight">

		<input type="hidden" name="save" value="true"/>
		<div class="settingSection">Download format</div>
		<c:set value="${actionBean.currentuserprefs.dlFormat}" var="dlFormat"></c:set>
		<fieldset title="Dounload Format Settings">
			<ul class="prefsRadios">
				<li><label for="ris_radio"><input type="radio" name="dlFormat" value="ris" id="ris_radio" <c:if test="${dlFormat eq 'ris'}">checked="checked"</c:if>/>RIS, EndNote, Reference Manager</label></li>
				<li><label for="bib_radio"><input type="radio" name="dlFormat" value="bib" id="bib_radio" <c:if test="${dlFormat eq 'bib'}">checked="checked"</c:if>/>BibTex</label></li>
				<li><label for="refworks_radio"><input type="radio" name="dlFormat" value="refworks" id="refworks_radio" <c:if test="${dlFormat eq 'refworks'}">checked="checked"</c:if>/>RefWorks direct</label></li>
				<li><label for="ascii_radio"><input type="radio" name="dlFormat" value="ascii" id="ascii_radio" <c:if test="${dlFormat eq 'ascii'}">checked="checked"</c:if>/>Plain text (ASCII)</label></li>
				<li><label for="csv_radio"><input type="radio" name="dlFormat" value="csv" id="csv_radio" <c:if test="${dlFormat eq 'csv'}">checked="checked"</c:if>/>Comma Separated Value Format</label></li>
				<li><label for="excel_radio"><input type="radio" name="dlFormat" value="excel" id="excel_radio" <c:if test="${dlFormat eq 'excel'}">checked="checked"</c:if>/>Microsoft Excel&reg;</label></li>
				<li><label for="pdf_radio"><input type="radio" name="dlFormat" value="pdf" id="pdf_radio" <c:if test="${dlFormat eq 'pdf'}">checked="checked"</c:if>/>PDF</label></li>
				<li><label for="rtf_radio"><input type="radio" name="dlFormat" value="rtf" id="rtf_radio" <c:if test="${dlFormat eq 'rtf'}">checked="checked"</c:if>/>RTF</label></li>

			</ul>
			</fieldset>
			<c:set value="${actionBean.currentuserprefs.dlOutput}" var="dlOutput"></c:set>
			<hr/>
			<div class="settingSection">Download output</div>
			<fieldset title="Download Output Settings">
			<ul class="prefsRadios">
				<li><label for="default_radio"><input type="radio" name="dlOutput" value="default" id="default_radio" <c:if test="${dlOutput eq 'default'}">checked="checked"</c:if>/>Default</label></li>
				<li><label for="citation_radio"><input type="radio" name="dlOutput" value="citation" id="citation_radio" <c:if test="${dlOutput eq 'citation'}">checked="checked"</c:if>/>Citation</label></li>
				<li><label for="abstract_radio"><input type="radio" name="dlOutput" value="abstract" id="abstract_radio" <c:if test="${dlOutput eq 'abstract'}">checked="checked"</c:if>/>Abstract</label></li>
				<li><label for="detailed_radio"><input type="radio" name="dlOutput" value="detailed" id="detailed_radio" <c:if test="${dlOutput eq 'detailed'}">checked="checked"</c:if>/>Detailed</label></li>
			</ul>
			</fieldset>


	</div>

</div>
<div class="saveCancel"><input type="submit" value="Save" name="Save" id="savePrefsButton" /><a href="#" style="padding-right:7px;" onclick="TINY.box.hide();">Cancel</a></div>
</form>
</div>