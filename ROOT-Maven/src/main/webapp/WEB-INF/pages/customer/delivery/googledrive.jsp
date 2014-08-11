<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld"
	prefix="stripes"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp"
	pageTitle="Engineering Village - Save to Google Drive">

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
		<input type="hidden" value="${actionBean.downloadformat}" id="downloadformat" />
		<input type="hidden" value="${actionBean.displayformat}" id="displayformat" />
		<input type="hidden" value="${actionBean.docidlist}" id="docidlist" />
		<input type="hidden" value="${actionBean.handlelist}" id="handlelist" />
		<input type="hidden" value="${actionBean.folderid}" id="folderid" />

		<div style="margin-top:10px;font-size:16px; height:150px" >

			<br>
			<div id="fileInfo" style="text-align:center">
				<div>
					<span style="margin-left:5px;">Please use the Google Drive button below to start your download.</span>
				</div>
				<br>
				<div id="savetodrive-div">
				</div>
				<div id="savetodrivemsg"></div>
			</div>

		</div>
	</stripes:layout-component>

	<stripes:layout-component name="footer">
		<div id="dbxusrmsg" style="margin-left:5px;margin-bottom:5px;text-align:center">
			<span  style="color:#585858">Save to Google Drive may not work in all browsers. We recommend Internet Explorer 10, Safari 6, and recent versions of Firefox and Chrome.  Support for additional environments may be added in the future. </span>
		</div>
	</stripes:layout-component>

	<stripes:layout-component name="jsbottom_custom">
		<script LANGUAGE="Javascript" SRC="/static/js/savetogoogle.js?v=${releaseversion}"></script>
	</stripes:layout-component>
</stripes:layout-render>