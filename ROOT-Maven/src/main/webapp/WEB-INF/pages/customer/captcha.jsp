<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld"
	prefix="stripes"%>

<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp"
	pageTitle="Engineering Village - Image Verification">

	<stripes:layout-component name="contents">

	<div class="marginL15">

		<div class="paddingT5">
			<h2 class="txtLargest">Image Verification</h2>
		</div>

		<stripes:form name="captcha_form" action="/captcha/verify.url" method="post">

		<stripes:hidden name="imagetextenc"/>
		<stripes:hidden name="redirectenc"/>

		<div style="margin: 7px 0">
		<p>We would like to	validate the actions you are taking in this Engineering Village	session.</p>
		<p>In order to continue, please enter the text shown in the image below.</p>
		</div>

		<stripes:errors>
		     <stripes:errors-header><ul class="errors" style="padding: 0 0 7px 0"></stripes:errors-header>
		     <li><stripes:individual-error/></li>
		     <stripes:errors-footer></ul></stripes:errors-footer>
		</stripes:errors>

		<div style="border:2px solid #9B9B9B; padding:7px; margin-bottom: 7px; width:300px">
		<p>
			<img height="40" alt="" width="198" border="0"
				src="/captcha/image.url?imagetextenc=${actionBean.imagetextenc}"/>&nbsp;&nbsp;

			<a	href="/captcha/display.url?redirectenc=${actionBean.redirectenc}">
			<img alt="Refresh" src="/static/images/refresh_captcha.gif" height="26" width="26" border="0"/>
			</a>&nbsp;&nbsp;

			<a	onclick="window.open('/captchaFAQ.html','_blank','width=400, height=300, left=' + (screen.width-450) + ', top=100');return false;"
				href="/captchaFAQ.html">
					<img title="Help" height="27" width="27" border="0"
					src="/static/images/help_captcha.gif"/>
			</a>&nbsp;&nbsp;
		</p>
		<br/>
		<p>
		<stripes:text name="userentry" style="padding:0;margin:0;width:195px"/>&nbsp;&nbsp;
		<stripes:submit value="Verify" title="Verify" name="verify"/>
		</p>
		</div>

		<div style="margin: 7px 0">
		<p>Please enter the text from the image and click Verify button</p>
		<p>If you have any questions, please <a title="Contact and support (opens in a new window)" href="${contactuslink}" class="evpopup" target="_blank">contact customer support</a></p>
		</div>

		</stripes:form>
	</div>
	</stripes:layout-component>


</stripes:layout-render>