<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld"	prefix="stripes"%>
<%@ taglib uri="/WEB-INF/tlds/functions.tld" prefix="f" %>

<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp"
	pageTitle="Engineering Village - Change Password">

	<stripes:layout-component name="csshead">
	<link href="/static/css/ev_personalaccount.css?v=${releaseversion}" media="all" type="text/css" rel="stylesheet"></link>
	</stripes:layout-component>

	<stripes:layout-component name="contents">
	<div style="width:100%"> 

	<div class="marginL15">
	<ul id="jserrors" class="errors">
<stripes:errors>
     <stripes:errors-header></stripes:errors-header>
     <li><stripes:individual-error/></li>
     <stripes:errors-footer></stripes:errors-footer>
</stripes:errors>
	</ul>

		<div class="paddingT5">
		<h2 class="txtLargest">Change password</h2>
		</div>
		<div class="hr" style="display:none;margin:0 10px 0 0; color:#d7d7d7; background-color:#d7d7d7; height: 2px;"><hr></div>

<c:choose>
<c:when test="${not actionBean.context.userLoggedIn}">
		<p class="paddingT5">Please login to change your password.</p>
		<br/>
		<br/>
		<br/>
		<br/>
</c:when>
<c:otherwise>
		
		<form action="/personalaccount/password/update.url"
			name="contentForm" method="post" id="contentForm" class="user-input-form">
			<input type="hidden" value="updatePassword" name="CID"/> 
			<input type="hidden" value="${f:encode(actionBean.nexturl)}" name="nexturl"/> 
			<input type="hidden" value="${actionBean.database}" name="database"/>

		<div id="inputbox" class="contentMain shadowbox marginT10">

		<div class="paddingT10 paddingR20 paddingB5 paddingL20 margin0">
			<div class="clear"></div>
	
			<div class="marginB10">
				<label for="currentpassword"><span style="font-weight: bold">Current Password:</span></label>
				<input type="password" style="width:180px"
					class="textBox  validate-password validate-passwordInvalid"
					id="currentpassword" value="${actionBean.currentpassword}" size="50" maxlength="16" name="currentpassword"/>
			</div>
			
			<div class="marginB10">
				<label for="password"><span style="font-weight: bold">New Password:</span></label>
				<input type="password" style="width:180px"
					class="textBox  validate-password validate-passwordInvalid"
					id="password" value="${actionBean.password}" size="50" maxlength="16" name="password"/>
				<span style="width:100%">&nbsp;(must be at least 6 characters and no more than 16)</span>
			</div>
			
			<div class="marginB10">
				<label for="confirmpassword"><span style="font-weight: bold">Confirm Password:</span></label>
				<input type="password" style="width:180px"
						class="textBox validate-confirmPassword validate-confirmPasswordInvalid validate-passwordsMatch"
						id=confirmpassword value="${actionBean.confirmpassword}" size="50" maxlength="16" name="confirmpassword"/>
			</div>
			
		</div>

		<p style="margin-left: 130px"><input type="submit"
			title="Submit" class="button"
			style="margin-right: 7px;" value="Submit" name="submitButton"/>
			&nbsp;&nbsp;|&nbsp;&nbsp;<a id="clear" href="/personalaccount/password/display.url?CID=changePassword&database=${actionBean.database}" title="Clear">Clear</a>
		</p>
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