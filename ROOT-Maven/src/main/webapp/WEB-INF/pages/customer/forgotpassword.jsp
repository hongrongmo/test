<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld"
	prefix="stripes"%>

<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp"
	pageTitle="Engineering Village - Forgot Password">

	<stripes:layout-component name="csshead">
	</stripes:layout-component>

	<stripes:layout-component name="contents">
	
		<div class="marginL15 paddingT5 width690">
		
		<ul id="jserrors" class="errors">
		<stripes:errors>
			<stripes:errors-header></stripes:errors-header>
			<li><stripes:individual-error /></li>
			<stripes:errors-footer></stripes:errors-footer>
		</stripes:errors>
		</ul>

		<h2 class="txtLargest">Password Request</h2>
		<c:choose>
		<c:when test="${actionBean.emailsent}">
			<p class="paddingT20">Your password has been sent to your email address.</p>
			<p class="paddingT5">Return to <a
				href="/login/customer.url?CID=personalLoginForm" title="Login">login</a>
			page.</p>
			<br />
			<br />
			<br />
			<br />
			<br />
		</c:when>
		<c:otherwise>
			<div class="contentMain">
			<div id="contentPadding bgGrey">
			<form id="forgotpasswordForm" class="user-input-form" action="/forgotpassword/retrieve.url"
				method="post" name="forgotpasswordForm">
				
			<input type="hidden" value="passwordretrieve" name="CID"/> 
			<input type="hidden" value="${actionBean.nexturl}" name="nexturl"/> 
			<input type="hidden" value="${actionBean.database}" name="database"/>
			<div class="paddingT7 paddingR7 paddingB7 paddingL7 margin0 zoomFix"
				style="float: left">
			<p><strong>Please enter your email address and your
			password will be sent to the email address.</strong></p>
			<div class="clear"></div>
			<label for="email" style="float: left"><span>Email
			address:</span><input type="text" class="textBox" id="email"
				value="${actionBean.email}" size="50" name="email"></label>
			<p class="submit"><input type="submit" title="Submit to email your password"
				class="button" value="Submit" name="submitButton"/>
			<a href="/login/customer.url?CID=personalLoginForm"
				style="text-decoration: none" class="cancellink" title="Cancel">Cancel</a>
			</p>

			<div class="clear"></div>

			<c:if test="${not empty actionBean.context.validationErrors}">
				<p>If you are not a registered user <a
					title="Register to access personal features"
					href="/register/display.url?CID=newRegistration">click
				here</a> to register</p>
			</c:if></div>

			<div class="clear"></div>
			</form>
			</div>

			</div>
			
		</c:otherwise>
		</c:choose>
		</div>
	</stripes:layout-component>

	<stripes:layout-component name="jsbottom_custom">
	<fmt:setBundle basename="StripesResources" var="bndl" scope="page"/>
	<script type="text/javascript" src="/static/js/jquery/jquery.validate.min.js?v=${releaseversion}"></script>
	
<script type="text/javascript">
$().ready(function() {
	
	// Optionally set some default paramaters for the validator
	$.validator.setDefaults({
		//submitHandler: function() { alert("submitted!"); },
		//debug:true
	});

	// Bind validation to the registration form
	$("#forgotpasswordForm").validate({
		rules: {
			email: {required: true,	email: true}
		},
		messages: {
			email:  "<fmt:message key="org.ei.stripes.action.personalaccount.ForgotPasswordAction.email.errorMessage" bundle="${bndl}"/>"
		},
		errorContainer: "#jserrors",
		errorLabelContainer: "#jserrors",
		wrapper: "li"
	});
});
</script>

	</stripes:layout-component>

</stripes:layout-render>