<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld"
	prefix="stripes"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp"
	pageTitle="${actionBean.section}">

	<stripes:layout-component name="csshead">
    <link rel="stylesheet" href="/static/css/ev2net.css?v=${releaseversion}" type="text/css"/>
	</stripes:layout-component>

	<stripes:layout-component name="header">
		<jsp:include page="/WEB-INF/pages/include/headernull.jsp" />
	</stripes:layout-component>

	<stripes:layout-component name="contents">

		<div id="delivery_content" class="marginL15">
<c:choose>
<c:when test="${actionBean.success}">
        <center>
          <table border="0" width="99%" cellspacing="0" cellpadding="0" bgcolor="#FFFFFF">
            <tr>
                <td valign="top" height="20"><img src="/static/images/s.gif" border="0" height="20"/></td>
            </tr>
            <tr><td valign="top"><span class="EvHeaderText">Thank you</span></td></tr>
            <tr>
              <td valign="top">
                <P><span CLASS="MedBlackText">Someone will contact you shortly.</span></P>
              </td>
            </tr>
          </table>
        </center>
</c:when>
<c:otherwise>
			<stripes:form name="sendemail" method="POST" action="/askanexpert/email/submit.url" onsubmit="return validateForm(document.sendemail);">
				<stripes:hidden name="database" />
				<stripes:hidden name="discipline"/>
				<stripes:hidden name="disciplineid"/>
				<stripes:hidden name="section"/>
				<stripes:hidden name="sectionid"/>
				<stripes:hidden name="guru"/>

				<table border="0" width="100%" cellspacing="0" cellpadding="0"
					bgcolor="#FFFFFF">
					<tbody>
						<tr>
							<td valign="top" height="15" colspan="3"><img
								src="/static/images/s.gif" border="0" alt=""></td>
						</tr>
						<tr>
							<td valign="top" width="20"><img
								src="/static/images/s.gif" border="0" width="20" alt=""></td>
							<td valign="top" colspan="2"><span class="EvHeaderText">${actionBean.section}<c:if test="${not empty actionBean.discipline}"> - ${actionBean.discipline}</c:if></span></td>
						</tr>
						<tr>
							<td valign="top" width="20"><img
								src="/static/images/s.gif" border="0" width="20" alt=""></td>
							<td valign="top"><span class="MedBlackText">Please fill in
									the information. All fields are required.</span></td>
							<td valign="top" width="10"><img
								src="/static/images/s.gif" border="0" width="10" alt=""></td>
						</tr>
						<tr>
							<td valign="top" height="20" colspan="3"><img
								src="/static/images/s.gif" border="0" height="20" alt=""></td>
						</tr>
						<tr>
							<td valign="top" colspan="3"><table border="0" width="65%"
									cellspacing="0" cellpadding="0" align="center">
									<tbody>
										<tr>
											<td valign="top" height="2" bgcolor="#3173B5" colspan="3"><img
												src="/static/images/s.gif" border="0" height="2"
												alt=""></td>
										</tr>
										<tr>
											<td valign="top" width="2" bgcolor="#3173B5"><img
												src="/static/images/s.gif" border="0" width="2"
												alt=""></td>
											<td valign="top">
												<table border="0" width="200" cellspacing="0"
													cellpadding="0">
													<tbody>
														<tr>
															<td valign="top" height="15" colspan="3"><img
																src="/static/images/s.gif" border="0" height="15"
																alt="" /></td>
														</tr>
														<tr>
															<td valign="top" align="right">&nbsp;</td>
															<td valign="top" width="6"><img
																src="/static/images/s.gif" border="0" width="6"
																alt="" /></td>
															<td valign="top"><span class="MedBlackText">Name:</span></td>
														</tr>
														<tr>
															<td valign="top" align="right">&nbsp;</td>
															<td valign="top" width="6"><img
																src="/static/images/s.gif" border="0" width="6"
																alt="" /></td>
															<td valign="top"><span class="MedBlackText"><stripes:text name="from_name" size="25"/></span></td>
														</tr>
														<tr>
															<td valign="top" height="15" colspan="3"><img
																src="/static/images/s.gif" border="0" height="15"
																alt="" /></td>
														</tr>
														<tr>
															<td valign="top" align="right">&nbsp;</td>
															<td valign="top" width="6"><img
																src="/static/images/s.gif" border="0" width="6"
																alt="" /></td>
															<td valign="top"><span class="MedBlackText">Institution:</span></td>
														</tr>
														<tr>
															<td valign="top" align="right">&nbsp;</td>
															<td valign="top" width="6"><img
																src="/static/images/s.gif" border="0" width="6"
																alt="" /></td>
															<td valign="top"><span class="MedBlackText"><stripes:text name="institution" size="25"/></span></td>
														</tr>
														<tr>
															<td valign="top" height="5" colspan="3"><img
																src="/static/images/s.gif" border="0" height="5"
																alt="" /></td>
														</tr>
														<tr>
															<td valign="top" align="right">&nbsp;</td>
															<td valign="top" width="6"><img
																src="/static/images/s.gif" border="0" width="6"
																alt="" /></td>
															<td valign="top"><span class="MedBlackText">Email:</span></td>
														</tr>
														<tr>
															<td valign="top" align="right">&nbsp;</td>
															<td valign="top" width="6"><img
																src="/static/images/s.gif" border="0" width="6"
																alt="" /></td>
															<td valign="top"><span class="MedBlackText"><stripes:text name="from_email" size="25" value=""/></span></td>
														</tr>
														<tr>
															<td valign="top" height="5" colspan="3"><img
																src="/static/images/s.gif" border="0" height="5"
																alt="" /></td>
														</tr>
														<tr>
															<td valign="top" align="right">&nbsp;</td>
															<td valign="top" width="6"><img
																src="/static/images/s.gif" border="0" width="6"
																alt="" /></td>
															<td valign="top"><span class="MedBlackText">Question:</span></td>
														</tr>
														<tr>
															<td valign="top" align="right">&nbsp;</td>
															<td valign="top" width="6"><img
																src="/static/images/s.gif" border="0" width="6"
																alt="" /></td>
															<td valign="top"><span class="MedBlackText"><stripes:textarea
																		rows="6" cols="30" name="message"></stripes:textarea></span></td>
														</tr>
<c:if test="${empty actionBean.context.userSession.userTextZones['REFERENCE_SERVICES_LINK']}">
														<tr>
															<td valign="top" align="right">&nbsp;</td>
															<td valign="top" width="6"><img
																src="/static/images/s.gif" border="0" width="6"
																alt="" /></td>
															<td valign="top"><input type="checkbox" checked="checked" name="share_question" id="share_checkbox" size="25"/>
															<span class="MedBlackText"><label for="share_checkbox">Anonymously share this
																		question and response with others on Ei.org</label></span></td>
														</tr>
</c:if>
														<tr>
															<td valign="top" height="15"><img
																src="/static/images/s.gif" border="0" height="15"
																alt=""></td>
														</tr>
														<tr>
															<td valign="top" colspan="2">&nbsp;</td>
															<td valign="top"><span class="SmBlackText"><input
																	type="submit" name="submit" value="Send Email"></span><br>
																<br></td>
														</tr>
													</tbody>
												</table>
												<form action=""></form>
											</td>
											<td valign="top" width="2" bgcolor="#3173B5"><img
												src="/static/images/s.gif" border="0" width="2"
												alt=""></td>
										</tr>
										<tr>
											<td valign="top" height="2" bgcolor="#3173B5" colspan="3"><img
												src="/static/images/s.gif" border="0" height="2"
												alt=""></td>
										</tr>
									</tbody>
								</table></td>
						</tr>
					</tbody>
				</table>
			</stripes:form>
</c:otherwise>
</c:choose>
		</div>

	</stripes:layout-component>

    <stripes:layout-component name="footer">
        <div class="hr" style="margin:20px 0 7px 0; color:#d7d7d7; background-color:#d7d7d7; height: 2px;"><hr/></div>
        <jsp:include page="/WEB-INF/pages/include/copyright.jsp" />
    </stripes:layout-component>

	<stripes:layout-component name="jsbottom_custom">
		<SCRIPT LANGUAGE="JavaScript" type="text/javascript">
			// This function basically validates the data entered in Email form fields.
			// typically it checks for a valid mail id and not null conditions
			function validateForm(sendemail) {
				sendemail.from_email.value = sendemail.from_email.value
						.replace(/\s+/g, "");
				var from_name = sendemail.from_name.value;
				var institution = sendemail.institution.value;
				var from_email = sendemail.from_email.value.replace(/\s+/g, "");
				var message = sendemail.message.value;
				if (from_name.length == 0) {
					window.alert("Please enter your name.");
					sendemail.from_name.focus();
					return false;
				}
				if (institution.length == 0) {
					alert("Please enter your institution.");
					sendemail.institution.focus();
					return false;
				}
				if (validateEmail(from_email) == false) {
					alert("Invalid Email address.");
					sendemail.from_email.focus();
					return false;
				}
				if (message.length == 0) {
					alert("Question cannot be blank.");
					sendemail.message.focus();
					return false;
				}
		        GALIBRARY.createWebEventWithLabel('AskAnExpert', 'Email Sent', 'From: ' + from_name + '; Institution: ' + institution + '; Email: ' + from_email);
				return (true);
			}
			// This function basically does email validation(ie @ . and special characters)
			function validateEmail(email) {
				var splitted = email.match("^(.+)@(.+)$");
				if (splitted == null)
					return false;
				if (splitted[1] != null) {
					var regexp_user = /^\"?[\w-_\.]*\"?$/;
					if (splitted[1].match(regexp_user) == null)
						return false;
				}
				if (splitted[2] != null) {
					var regexp_domain = /^[\w-\.]*\.[A-Za-z]{2,4}$/;
					if (splitted[2].match(regexp_domain) == null) {
						var regexp_ip = /^\[\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}\]$/;
						if (splitted[2].match(regexp_ip) == null)
							return false;
					}
					return true;
				}
				return false;
			}
		</SCRIPT>

	    <script type="text/javascript">
	    GALIBRARY.createWebEventWithLabel('AskAnExpert', 'Email', '${actionBean.section}');
	    </script>

	</stripes:layout-component>
</stripes:layout-render>

