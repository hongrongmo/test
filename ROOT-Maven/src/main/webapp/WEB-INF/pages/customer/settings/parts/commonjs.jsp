<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
    
	<fmt:setBundle basename="StripesResources" var="bndl" scope="page"/>
	<script type="text/javascript" src="/static/js/jquery/jquery.validate.min.js?v=${releaseversion}"></script>
	<script type="text/javascript">
	$(document).ready(function() {

		// Clear form inputs
		$("#clear").click(function(event) {
			event.preventDefault();
			$("#jserrors").empty();
			$("input[type='password']").val('');
			$("input[type='text']").val('');
		});

		// Optionally set some default paramaters for the validator
		$.validator.setDefaults({
			//submitHandler: function() { alert("submitted!"); },
			//debug:true
		});
	
		// Add a regex pattern to the name field to exclude certain characters.
		$.validator.addMethod('regexp', function(value, element, param) {
		        return this.optional(element) || value.match(param);
		     },
		    'This value doesn\'t match the acceptable pattern.');
	
		// Bind validation to the registration form
		$("#contentForm").validate({
			rules: {
				firstname: {required:true, regexp: /^[a-zA-Z\s]+$/, maxlength:64},
				lastname: {required:true, regexp: /^[a-zA-Z\s]+$/, maxlength:64},
				email: {required: true,	email: true},
				currentpassword: {required: true},
				password: {required: true, minlength: 6, maxlength: 16},
				confirmpassword: { equalTo: "#password"},
				useragreement: "required"
			},
			messages: {
				firstname: {
					required: '<fmt:message key="org.ei.stripes.action.personalaccount.EditPersonalAccountAction.firstname.valueNotPresent" bundle="${bndl}"/>',
					regexp: '<fmt:message key="org.ei.stripes.action.personalaccount.EditPersonalAccountAction.firstname.invalidValue" bundle="${bndl}"/>',
					maxlength: '<fmt:message key="org.ei.stripes.action.personalaccount.EditPersonalAccountAction.firstname.errorMessage" bundle="${bndl}"/>'
				},
				lastname: {
					required: '<fmt:message key="org.ei.stripes.action.personalaccount.EditPersonalAccountAction.lastname.valueNotPresent" bundle="${bndl}"/>',
					regexp: '<fmt:message key="org.ei.stripes.action.personalaccount.EditPersonalAccountAction.lastname.invalidValue" bundle="${bndl}"/>',
					maxlength: '<fmt:message key="org.ei.stripes.action.personalaccount.EditPersonalAccountAction.lastname.errorMessage" bundle="${bndl}"/>'
				},
				email:  '<fmt:message key="org.ei.stripes.action.personalaccount.EditPersonalAccountAction.email.invalidEmail" bundle="${bndl}"/>',
				currentpassword: {
					required: '<fmt:message key="org.ei.stripes.action.personalaccount.ChangePasswordAction.currentpassword.valueNotPresent" bundle="${bndl}"/>'
				},
				password: {
						required: '<fmt:message key="org.ei.stripes.action.personalaccount.ChangePasswordAction.password.valueNotPresent" bundle="${bndl}"/>',
					minlength: '<fmt:message key="org.ei.stripes.action.personalaccount.ChangePasswordAction.password.valueTooLong" bundle="${bndl}"/>',
					maxlength: '<fmt:message key="org.ei.stripes.action.personalaccount.ChangePasswordAction.password.valueBelowMinimum" bundle="${bndl}"/>'
				},
				confirmpassword: {
					equalTo: '<fmt:message key="org.ei.stripes.action.personalaccount.ChangePasswordAction.confirmpassword.equalTo" bundle="${bndl}"/>'
				},
				useragreement: '<fmt:message key="org.ei.stripes.action.personalaccount.EditPersonalAccountAction.useragreement.valueNotPresent" bundle="${bndl}"/>'
			},
			errorContainer: "#jserrors",
			errorLabelContainer: "#jserrors",
			wrapper: "li"
		});
	});
	</script>
