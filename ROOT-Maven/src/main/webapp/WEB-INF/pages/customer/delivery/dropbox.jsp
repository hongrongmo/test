<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld"
	prefix="stripes"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp"
	pageTitle="Engineering Village - Save to Dropbox">

	<stripes:layout-component name="csshead">
		<div id="header" style="width:100%; min-width: 100%">
			<div id="logoEV">
		        <img alt="Engineering Village - The information discovery platform of choice for the engineering community" src="/static/images/EV-logo.gif"/>
			</div>
		
			<div id="headerLink">
				<div class="clearfix" id="suites">&#160;</div>
			</div>
		
			<div class="navigation txtLarger clearfix">
				<ul title="top level navigation" class="nav main">
				</ul>
				<ul class="nav misc">
					<li class="nodivider" id='custom-logo-li' style='display:none'>
						<div id="custom-logo-top" align="center">
							<img id="custom-logo-img" border="0"/>
						</div>
					</li>
				</ul>
			</div>
		</div>
	</stripes:layout-component>

	<stripes:layout-component name="header">
		
	</stripes:layout-component>

	<stripes:layout-component name="contents">
		<input type="hidden" value="${actionBean.dropBoxDownloadUrl}" id="dropBoxDownloadUrl" />
		<input type="hidden" value="${actionBean.dropBoxClientid}" id="dropBoxClientid" />
		<input type="hidden" value="${actionBean.downloadformat}" id="downloadformat" />
		<input type="hidden" value="${actionBean.displayformat}" id="displayformat" />
		<input type="hidden" value="${actionBean.filenameprefix}" id="filenameprefix" />
		
		<div style="margin-top:10px;font-size:16px; height:150px" >
			<div id="userInfo" style="display:none">
				<img style="margin-left:30px" width="250px"  src="/static/images/dropbox.jpg"> 
				<div style="float:right;text-align:right">
					<span style="margin-right:5px;color:#585858">Logged in as :</span>
					<br>
					<span style="margin-right:5px;color:#585858"><span id="dbxUserName"></span> (<span id="dbxEmail"></span>)</span>
					<br>
					<span style="margin-right:5px;color:#585858">Go to <a href="http://dropbox.com">www.dropbox.com</a> to logout</span>
				</div>
				
			</div>
			<br>
			<div id="fileInfo" style="display:none">
				<div>
					<span style="margin-left:5px;text-align:center">Engineering Village trying to save the file "<span style="font-weight:bold" id="filename"></span>" into your Dropbox account.</span>
				</div> 
				<br>
				<div style="text-align:center">
					<span style="margin-left:5px"><a href="#" onclick="savefile(event);"><img width="186px" height="38px"  src="/static/images/savetodropbox.jpg"></a></span>
				</div>
			</div>
			<span id="connectioninprogress" style="display:block;color:#585858;text-align:center"><img width="50px"  src="/static/images/waiting.gif">Connecting to Dropbox...</span>
			<div id="saveInfo" style="display:none;text-align:center">
				<span id="saveinprogress" style="color:#585858"><img width="50px"  src="/static/images/waiting.gif">File is being saved into your Dropbox account...</span>
				<span id="savesuccess" style="display:none">File "<span style="font-weight:bold" id="savedfilename"></span>" is saved successfully into your dropbox folder.<br><br><span id="savedfolder"></span></span>
				<span id="savefailed" style="display:none">Saving a file to your dropbox account failed, close this window and retry again from download page.</span>
			</div>
			<div id="errorInfo" style="display:none;text-align:center">Error occured, close this window and retry again from download page.</div>
			
		</div>
		<br>
		<br>
		<stripes:form name="dropboxredirect" id="dropboxredirectform" method="post" action="/delivery/download/dropboxredirect.url">
	        <stripes:hidden name="dropBoxDownloadUrl" value="${actionBean.dropBoxDownloadUrl}"/>
		    <stripes:hidden name="dropBoxClientid" value="${actionBean.dropBoxClientid}" />
		    <stripes:hidden name="downloadformat" value="${actionBean.downloadformat}" />
		    <stripes:hidden name="displayformat" value="${actionBean.displayformat}" />
		    <stripes:hidden name="filenameprefix" value="${actionBean.filenameprefix}" />
		</stripes:form>
	</stripes:layout-component>

	<stripes:layout-component name="footer">
		<div id="dbxusrmsg" style="margin-left:5px;margin-bottom:5px;display:none">
			<span  style="color:#585858">Save to Dropbox may not work in all browsers. We recommend Internet Explorer 10, Safari 6, and recent versions of Firefox and Chrome.  Support for additional environments may be added in the future. <a title="Learn more about Save to Dropbox" alt="Learn more about Save to Dropbox" onclick="helpurlClick(this);return false;"class="helpurl" href="${actionBean.helpUrl}#email_print_downld_ev.htm#downldDropbox">Learn more about Save to Dropbox.</a></span>
		</div>
	</stripes:layout-component>

	<stripes:layout-component name="jsbottom_custom">
		<script LANGUAGE="Javascript" SRC="/static/js/dropbox.js?v=${releaseversion}"></script>
	</stripes:layout-component>
</stripes:layout-render>