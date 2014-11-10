<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld"	prefix="stripes"%>
<%@ taglib uri="/WEB-INF/tlds/functions.tld" prefix="f" %>

<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp"
	pageTitle="Engineering Village - Edit Personal Account">

	<stripes:layout-component name="csshead">
	<link href="/static/css/ev_personalaccount.css?v=${releaseversion}" media="all" type="text/css" rel="stylesheet"></link>
	</stripes:layout-component>

	<stripes:layout-component name="contents">
	<div id="registerbox">

	<div class="marginL15">
	<ul id="jserrors" class="errors">
<stripes:errors>
     <stripes:errors-header></stripes:errors-header>
     <li><stripes:individual-error/></li>
     <stripes:errors-footer></stripes:errors-footer>
</stripes:errors>
	</ul>

		<div class="paddingT5">
		<h2 class="txtLargest">Modify personal details and preferences</h2>
		</div>
		<div class="hr" style="display:none;margin:0 10px 0 0; color:#d7d7d7; background-color:#d7d7d7; height: 2px;"><hr></div>

<c:choose>
<c:when test="${not actionBean.context.userLoggedIn}">
		<p class="paddingT5">Please login to see your personal account settings.</p>
		<br/>
		<br/>
		<br/>
		<br/>
</c:when>
<c:otherwise>
		<p class="paddingT10">After updating your information below, please click the Save button to save your changes.</p>
		
		<p class="paddingT5">(<span style="color:red">*</span> = required field)</p>
		
		<form action="/personalaccount/edit/update.url"
			name="contentForm" method="post" id="contentForm" class="user-input-form">
			<input type="hidden" value="updatePersonalAccount" name="CID"/> 
			<input type="hidden" value="${f:encode(actionBean.nexturl)}" name="nexturl"/> 
			<input type="hidden" value="${actionBean.database}" name="database"/>

		<div id="inputbox" class="contentMain shadowbox marginT10">

		<div class="paddingT10 paddingR20 paddingB10 paddingL20 margin0">
			<a title="Read Privacy Policy (Opens in a new window)" target="_blank"
				href="http://www.elsevier.com/wps/find/privacypolicy.cws_home/privacypolicy"
				class="floatR">Privacy policy</a>
			<h3 class="txtLarge paddingB10 margin0 Bold">Your details</h3>
			<div class="clear"></div>
			
			<div class="marginB10">
			<label for="firstName"><span>First name:</span></label>
				<input type="text"
				class="textBox validate-firstName validate-firstNameInvalid"
				id="firstName" value="${actionBean.firstname}" size="50" maxlength="64" name="firstname"/>
				<span title="Required field" class="normal"> *</span>
			</div>
			
			<div class="marginB10">
				<label for="familyName"><span>Family name:</span></label>
				<input type="text"
					class="textBox validate-familyName validate-familyNameInvalid"
					id="familyName" value="${actionBean.lastname}" size="50" maxlength="64" name="lastname"/>
				<span title="Required field" class="normal"> *</span>
			</div>
	
			<div class="marginB10">
				<label for="emailAddress"><span>Email address:</span></label>
				<input type="text"
					class="textBox validate-emailAddress validate-emailInvalidAddress"
					id="email" value="${actionBean.email}" size="50" name="email"/>
				<span title="Required field" class="normal">*</span></label>
			</div>
		</div>
				
		<div class="hr" style="background-color: #9b9b9b"><hr/></div>

		<div class="paddingT10 paddingR20 paddingB10 paddingL20 margin0">

			<div class="marginB10">
				<label for="marketinginfo"></label>
				<input type="checkbox" id="announceflag"
				value="001" name="announceflag"<c:if test="${actionBean.announceflag}"> checked="checked"</c:if>/>
				<span class="normal">&nbsp;</span>
				<strong>Yes.</strong> Please send me information about Engineering Village or related products from time to time.
				<br/><span style="margin-left: 29px">The information I have provided here is confidential and it will not be released to a third party.&nbsp;</span>
			</div>
			
			<div class="marginB10 agreementRead">
		
				<div>
					<label for="useragreement"></label>
					<input type="checkbox" id="useragreement" value="true" name="useragreement"<c:if test="${actionBean.useragreement}"> checked="checked"</c:if>/>
					<span title="Required field" class="normal">*</span>
					I agree to the 
					<a
						onclick="return window.open('http://www.elsevier.com/locate/useragreement','registeredUserAgreement','scrollbars=yes,resizable=yes,directories=no,toolbar=no,menubar=no,status=no,width=760,height=600,left=25,top=25')"
						target="" title="Registered User Agreement" href="#">Registered	User Agreement.</a>
				</div>
		
			</div>

			<input type="submit"
				title="Save" class="button"
				style="margin-right: 7px;" value="Save" name="saveButton"/>
		
		</div>
		</div>

		</form>
		
</c:otherwise>
</c:choose>

		</div>
		</div>
	</stripes:layout-component>
	
	<stripes:layout-component name="jsbottom_custom">
	<jsp:include page="parts/commonjs.jsp"></jsp:include>
	</stripes:layout-component>	
</stripes:layout-render>