<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>

<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld"
	prefix="stripes"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp"
	pageTitle="Engineering Village - Local Holdings">

	<stripes:layout-component name="csshead">
	<style>
    #localholding_table {}
    #localholding_table td {padding: 2px; font-size: 14px;}

    h3 {color: #000000;font-size: 1.4em;font-weight: bold;margin: 15px 0 3px;padding-left: 5px;border-bottom: 2px solid #D7D7D7;display: block;}

    #lhinfo_table {padding:0; margin: 0; border-collapse:collapse;}
    #lhinfo_table td {padding:5px; margin: 0; border: 1px solid black}

    div#lhinfo_user_div {padding:0 0 7px 0; width: 730px}
    div#lhinfo_user_div .info {padding-bottom: 5px}
    div#lhinfo_user_div .info label {width: 100px; display: block; float: left}
    div#lhinfo_user_div .buttons {float: right; padding-right: 7px;}
	<style>

	</style>
	</stripes:layout-component>

	<stripes:layout-component name="contents">
	<div class="marginL15">
	    <div class="clearfix"></div>

	    <h3>Local Holdings Link</h3>
        <br/>

	    <form name="emailresults" method="POST" onsubmit="return Validate()" action="/search/results/localholding.url">

		    <table id="lhinfo_table" cellspacing="0">
		        <c:if test="${not empty actionBean.title}">
		        <tr><td class="col1"><label for="ARTICLETITLE">ARTICLE TITLE:</label></td><td class="col2">${actionBean.title}<input type="hidden" id="ARTICLETITLE" name="ARTICLETITLE" value="${actionBean.title}" /></td></tr>
		        </c:if>
		        <c:if test="${not empty actionBean.author}">
		        <tr><td class="col1"><label for="AUTHOR">AUTHOR:</label></td><td class="col2">${actionBean.author}<input type="hidden" id="AUTHOR" name="AUTHOR" value="${actionBean.author}" /></td></tr>
		        </c:if>
		        <c:if test="${not empty actionBean.serialtitle}">
		        <tr><td class="col1"><label for="SERIALTITLE">SOURCE TITLE:</label></td><td class="col2">${actionBean.serialtitle}<input type="hidden" id="SERIALTITLE" name="SERIALTITLE" value="${actionBean.serialtitle}" /></td></tr>
		        </c:if>
		        <c:if test="${not empty actionBean.conftitle}">
		        <tr><td class="col1"><label for="CONFTITLE">CONFERENCE NAME:</label></td><td class="col2">${actionBean.conftitle}<input type="hidden" id="CONFTITLE" name="CONFTITLE" value="${actionBean.conftitle}" /></td></tr>
		        </c:if>
		        <c:if test="${not empty actionBean.source}">
		        <tr><td class="col1"><label for="SOURCE">SOURCE:</label></td><td class="col2">${actionBean.source}<input type="hidden" id="SOURCE" name="SOURCE" value="${actionBean.source}" /></td></tr>
		        </c:if>
		        <c:if test="${not empty actionBean.issn}">
		        <tr><td class="col1"><label for="ISSN">ISSN:</label></td><td class="col2">${actionBean.issn}<input type="hidden" id="ISSN" name="ISSN" value="${actionBean.issn}" /></td></tr>
		        </c:if>
		        <c:if test="${not empty actionBean.isbn}">
		        <tr><td class="col1"><label for="ISBN">ISBN:</label></td><td class="col2">${actionBean.isbn}<input type="hidden" id="ISBN" name="ISBN" value="${actionBean.isbn}" /></td></tr>
		        </c:if>
		        <c:if test="${not empty actionBean.volume}">
		        <tr><td class="col1"><label for="VOLUME">VOLUME:</label></td><td class="col2">${actionBean.volume}<input type="hidden" id="VOLUME" name="VOLUME" value="${actionBean.volume}" /></td></tr>
		        </c:if>
		        <c:if test="${not empty actionBean.issue}">
		        <tr><td class="col1"><label for="ISSUE">ISSUE:</label></td><td class="col2">${actionBean.issue}<input type="hidden" id="ISSUE" name="ISSUE" value="${actionBean.issue}" /></td></tr>
		        </c:if>
		        <c:if test="${not empty actionBean.startpage}">
		        <tr><td class="col1"><label for="STARTPAGE">START PAGE:</label></td><td class="col2">${actionBean.startpage}<input type="hidden" id="STARTPAGE" name="STARTPAGE" value="${actionBean.startpage}" /></td></tr>
		        </c:if>
		        <c:if test="${not empty actionBean.year}">
		        <tr><td class="col1"><label for="YEAR">YEAR:</label></td><td class="col2">${actionBean.year}<input type="hidden" id="YEAR" name="YEAR" value="${actionBean.year}" /></td></tr>
		        </c:if>
		    </table>

            <br/>

			<div id="lhinfo_user_div">
			   <p class="info">Please fill in the form below to send a request for the above document to <c:choose><c:when test="${empty actionBean.email}">library@lamrc.com</c:when><c:otherwise>${actionBean.email}<input type="hidden" id="email" name="email" value="${actionBean.email}"/></c:otherwise></c:choose></p>
			   <p class="info"><label for="emailaddress">Return email</label><span><input type="text" id="emailaddress" name="emailaddress" size="26"></span>
		       <p class="info"><label for="comments">Comments</label><span><textarea rows="6" id="comments" name="comments" cols="74"></textarea></span>
		       <p class="info"><span class="buttons"><input type="submit" value="Submit" name="submit" id="submit"><input type="reset" value="Reset" name="reset" id="reset"></span></p>
			</div>
			<div class="clearfix"></div>


	        <input style="display: none;" id="hiddenlpsubmitdiv"></input>

		</form>

    </div>
	</stripes:layout-component>

	<stripes:layout-component name="jsbottom_custom">
		<script>try{for(var lastpass_iter=0; lastpass_iter < document.forms.length; lastpass_iter++){ var lastpass_f = document.forms[lastpass_iter]; if(typeof(lastpass_f.lpsubmitorig2)=="undefined"){ lastpass_f.lpsubmitorig2 = lastpass_f.submit; lastpass_f.submit = function(){ var form=this; var customEvent = document.createEvent("Event"); customEvent.initEvent("lpCustomEvent", true, true); var d = document.getElementById("hiddenlpsubmitdiv"); for(var i = 0; i < document.forms.length; i++){ if(document.forms[i]==form){ d.innerText=i; } } d.dispatchEvent(customEvent); form.lpsubmitorig2(); } } }}catch(e){}</script>
		<script language="JavaScript">
		    function Validate() {
		        var eaddress = emailresults.emailaddress.value;
		        if ((eaddress == null) || (eaddress == "")) {
		            alert("Please enter email address");
		            return false;
		        } else if (eaddress.indexOf("@") < 0) {
		            alert("Please enter valid email address");
		            return false;
		        } else if (eaddress.indexOf(".") < 0) {
		            alert("Please enter valid email address");
		            return false;
		        } else {
		            return true;
		        }
		    }
		</script>
	</stripes:layout-component>

</stripes:layout-render>