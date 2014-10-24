<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:html="http://www.w3.org/TR/REC-html40"
	xmlns:java="java:java.net.URLDecoder"

	exclude-result-prefixes="java html xsl"
>
<xsl:include href="Header.xsl" />
<xsl:include href="GlobalLinks.xsl" />
<xsl:include href="Footer.xsl" />

 <!--
 	This file displays
 	1. registration form for new registration based on the the user staus,
 	2. registration form with the content along with the error message if the registration fails,
 	3. and confirmation message if the registration is successfull.
 -->

<xsl:template match="PAGE">

<xsl:variable name="BACKURL">
	<xsl:value-of select="BACKURL"/>
</xsl:variable>

<xsl:variable name="NEXTURL">
	<xsl:value-of select="NEXTURL"/>
</xsl:variable>

<xsl:variable name="SESSION-ID">
	<xsl:value-of select="SESSION-ID"/>
</xsl:variable>

<xsl:variable name="SEARCH-ID">
		<xsl:value-of  select="SEARCH-ID"/>
</xsl:variable>

<xsl:variable name="COUNT">
		<xsl:value-of  select="COUNT"/>
</xsl:variable>

<xsl:variable name="RESULTS-COUNT">
		<xsl:value-of  select="RESULTS-COUNT"/>
</xsl:variable>

<xsl:variable name="DATABASE">
		<xsl:value-of  select="DATABASE"/>
</xsl:variable>

<xsl:variable name="DATABASE-ID">
		<xsl:value-of  select="DATABASE-ID"/>
</xsl:variable>

<xsl:variable name="DOCIDS">
		<xsl:value-of  select="DOCIDS"/>
</xsl:variable>

<xsl:variable name="QUERY-ID">
		<xsl:value-of  select="QUERY-ID"/>
</xsl:variable>

<xsl:variable name="SOURCE">
	<xsl:value-of  select="SOURCE"/>
</xsl:variable>

<xsl:variable name="OPTION-VALUE">
		<xsl:value-of  select="OPTION-VALUE"/>
</xsl:variable>

<xsl:variable name="RESULTS-FORMAT">
		<xsl:value-of  select="RESULTS-FORMAT"/>
</xsl:variable>

<xsl:variable name="DISPLAY-FORM">
		<xsl:value-of  select="USER-PROFILE/DISPLAY-FORM"/>
</xsl:variable>

<xsl:variable name="USER-STATUS">
		<xsl:value-of  select="USER-PROFILE/USER-STATUS"/>
</xsl:variable>

<!-- conditional check to display the registration form -->
<xsl:choose>
	<!-- display the registration form if the user status is failure -->
	<xsl:when test="($USER-STATUS='failure')">

		<html>
		<head>
			<title>Create Your Personal Account</title>

		<SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js"/>

		<xsl:text disable-output-escaping="yes">

		<![CDATA[

			<xsl:comment>

				<SCRIPT LANGUAGE="JavaScript">

					 // this function validates all the form fields before submition.
					function validateregister(register)
					{
						if (register.firstname.value =="")
						{
							alert ("Please enter your First Name");
							register.firstname.focus();
							return false;
						}
						else
						{
								var nameLength= register.firstname.value.length;
								var tempName = register.firstname.value;
								var tempLength=0;

								while (tempName.substring(0,1) == ' ')
								{
									   tempName = tempName.substring(1);
									   tempLength = tempLength + 1;
								}
								if ( nameLength == tempLength)
								{
									   window.alert("Spaces are not allowed as First Name");
									   register.firstname.value = "";
									   register.firstname.focus();
									   return false;
								}
						}

						var fnameValid = validateName(register.firstname.value);
						if(!fnameValid)
						{
							window.alert("Please type English in first name field");
							register.firstname.value = "";
							register.firstname.focus();
							return false;
						}

						if (register.lastname.value =="")
						{
							alert ("Please enter your Last Name");
							register.lastname.focus();
							return false;
						}
						else
						{
							var nameLength= register.lastname.value.length;
							var tempName = register.lastname.value;
							var tempLength=0;

							while (tempName.substring(0,1) == ' ')
							{
								   tempName = tempName.substring(1);
								   tempLength = tempLength + 1;
							}
							if ( nameLength == tempLength)
							{
								   window.alert("Spaces are not allowed as Last Name");
								   register.lastname.value="";
								   register.lastname.focus();
								   return false;
							}								
						}

						var lnameValid = validateName(register.lastname.value);
						if(!lnameValid)
						{
							window.alert("Please enter English in last name field");
							register.lastname.value = "";
							register.lastname.focus();
							return false;
						}

						 if (register.email.value =="")
						 {
							alert ("Please enter your E-mail address");
							register.email.focus();
							return false;
						}
						var emailValid = validateEmail(register.email.value);
						if (!emailValid)
						{
								alert("The E-Mail field contains an invalid Email Format.\nPlease enter your correct E-Mail address.");
								register.email.focus();
								return false;
						}

						if (register.password.value =="")
						{
							alert ("You must enter a password");
							register.password.focus();
							return false;
						}
						else
						{
								var pwdLength= register.password.value.length;
								var tempPwd = register.password.value;
								var tempLength=0;
								if(tempPwd.substring(0,1) == ' ')
								{
									   window.alert("Password should not starts with spaces");
									   register.password.focus();
									   return false;
								}
								else
								{
									while (tempPwd.substring(0,1) == ' ')
									{
										   tempPwd = tempPwd.substring(1);
										   tempLength = tempLength + 1;
									}
									if ( pwdLength == tempLength)
									{
										   window.alert("Spaces are not allowed as Password");
										   register.password.value = "";
										   register.password.focus();
										   return false;
									}
								}
						}
						if( ( register.password.value.length < 6 ) || ( register.password.value.length  > 16))
						{
							alert ("Password field must be between 6 and 16 characters");
							register.password.focus();
							return false;
						}
						if (register.confpassword.value =="")
						{
							alert ("Confirm password field cannot be empty");
							register.confpassword.focus();
							return false;
						}

						if (register.password.value != register.confpassword.value)
						{
							alert("The password and confirm password  fields did not match");
							register.confpassword.focus();
							register.confpassword.select();
							return false;
						}
						if(register.announceflag.checked)
						{
							register.announceflag.value="001";
						}
						return true;
					}
					// This function validates email format.
					function validateEmail(email)
					{
							   var splitted = email.match("^(.+)@(.+)$");
							   if(splitted == null) return false;
							   if(splitted[1] != null )
							   {
								 	var regexp_user=/^\"?[\w-_\.]*\"?$/;
								 	if(splitted[1].match(regexp_user) == null)
								 	{
								 		return false;
								 	}
							   }
							   if(splitted[2] != null)
							   {
								 	var regexp_domain=/^[\w-\.]*\.[A-Za-z]{2,4}$/;
								 	if(splitted[2].match(regexp_domain) == null)
								 	{
								   		var regexp_ip =/^\[\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}\]$/;
								  		 if(splitted[2].match(regexp_ip) == null) return false;
									 }// if
								 	return true;
							   }
							return false;
					}
					
					//This function validates names
					function validateName(name)
					{
						var reg = /^[a-zA-Z]+$/;
						if(name.match(reg) == null)
						{							
							return false;
						}
						else 
						{
							return true;
						}
					}

				</SCRIPT>
			</xsl:comment>
		]]>
		</xsl:text>
		<!-- -->
		</head>

		<body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">

		<!-- Table for the logo area -->
		<center>
		<!-- APPLY HEADER -->
		<xsl:apply-templates select="HEADER">
			<xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
			<xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
		</xsl:apply-templates>

		<!-- Insert the Global Links -->
		<!-- logo, search history, selected records, my folders. end session -->
		<xsl:apply-templates select="GLOBAL-LINKS">
			<xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
			<xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
		</xsl:apply-templates>

      	<table border="0" width="99%" cellspacing="0" cellpadding="0">
      		<tr><td valign="top" height="24" bgcolor="#C3C8D1"><img src="/engresources/images/s.gif" border="0"/></td></tr>
      	</table>

		<!-- Table for new user registration -->
		<table border="0" width="80%" cellspacing="0" cellpadding="0">

		<FORM name="register" action="/controller/servlet/Controller?CID=createPersonalAccount" METHOD="POST"  onSubmit="return validateregister(register)" >

        <input type="hidden" name="database">
          <xsl:attribute name="value"><xsl:value-of select="$DATABASE"/></xsl:attribute>
        </input>

		<xsl:if test="($NEXTURL != '')">
		  <input type="hidden" name="nexturl">
		  <xsl:attribute name="value"><xsl:value-of select="java:decode($NEXTURL)"/></xsl:attribute>
		  </input>
		</xsl:if>
		<xsl:if test="($BACKURL != '')">
		  <input type="hidden" name="backurl">
		  	<xsl:attribute name="value"><xsl:value-of select="java:decode($BACKURL)"/></xsl:attribute>
		  </input>
		</xsl:if>

          <tr><td colspan="4" height="20"><img src="/engresources/images/s.gif" height="20" /></td></tr>
          <tr><td colspan="3" height="1" bgcolor="#3173b5"><img src="/engresources/images/s.gif" height="1" /></td></tr>

          <tr>
            <td width="1" bgcolor="#3173b5"><img src="/engresources/images/s.gif" width="1" /></td>
            <td valign="top">
              <table border="0" cellspacing="0" cellpadding="0" width="100%">
                <tr>
                  <td width="2" bgcolor="#3173b5"><img src="/engresources/images/s.gif" width="2" /></td>
                  <td height="10" bgcolor="#3173B5" colspan="3">
                    <a CLASS="LgWhiteText"><b>Create Your Personal Account</b></a>
                  </td>
                </tr>
                <tr><td valign="top" height="10" colspan="4"><img src="/engresources/images/s.gif" border="0" height="10" /></td></tr>
                <!-- Content Row / Indented with Spacer / Content cell/should can contain table
                  to acheive specific content layout within template -->
                <tr>
                  <td width="2"><img src="/engresources/images/s.gif" width="2" /></td>
				  <td colspan="3">
					<table border="0" cellspacing="0" cellpadding="0" width="100%">
					<xsl:variable name="EMAIL-EXISTS">
						<xsl:value-of select="USER-PROFILE/EMAIL-EXISTS"/>
					</xsl:variable>
					<!-- display error message if the email already exists in the system. -->
					<xsl:if test="($EMAIL-EXISTS='true')">
						<tr>
							<td width="2"><img src="/engresources/images/s.gif" width="2"/></td><td colspan="3"><a class="RedText">Your e-mail address already exists in the system. Please use login form </a><a class="LgBlueLink" href="/controller/servlet/Controller?CID=personalLoginForm&amp;displaylogin=true&amp;database={$DATABASE}">here</a><a class="RedText">, or enter a different e-mail address.</a>
							</td>
						</tr>
					</xsl:if>
					<!-- end of error message -->
					<tr><td valign="top" height="10" colspan="4"><img src="/engresources/images/s.gif" border="0" height="10"/></td></tr>
					<tr><td valign="top" width="2"><img src="/engresources/images/s.gif" border="0" width="2"/></td><td valign="top" colspan="3"><a CLASS="MedBlackText">To obtain your FREE personal account, please complete the form below. Your account will allow you to save searches, save records, and create E-mail Alerts.</a><br/><font color="red"><b>*</b> <a CLASS="MedBlackText">indicate required fields</a></font></td></tr>
					<tr><td valign="top" height="10" colspan="4"><img src="/engresources/images/s.gif" border="0" height="10"/></td></tr>
					<tr><td valign="top" width="2"><img src="/engresources/images/s.gif" border="0" width="2"/></td><td valign="top"><a CLASS="MedBlackText"><font color="C3C8D1"><B>*</B></font>Title: </a></td><td valign="top" width="1"><img src="/engresources/images/s.gif" border="0"/></td><td valign="top"><a CLASS="MedBlackText">
					<xsl:variable name="TITLE">
							<xsl:value-of select="USER-PROFILE/TITLE"/>
					</xsl:variable>
					<select name="title">
							<option value="">Select Title</option>
							<option value="Dr.">
								<xsl:if test="($TITLE='Dr.')">
									<xsl:attribute name="selected">selected</xsl:attribute>
								</xsl:if>Dr.
							</option>
							<option value="Prof.">
								<xsl:if test="($TITLE='Prof.')">
									<xsl:attribute name="selected">selected</xsl:attribute>
								</xsl:if>Prof.
							</option>
							<option value="Mr.">
								<xsl:if test="($TITLE='Mr.')">
									<xsl:attribute name="selected">selected</xsl:attribute>
								</xsl:if>Mr.
							</option>
							<option value="Mrs.">
								<xsl:if test="($TITLE='Mrs.')">
									<xsl:attribute name="selected">selected</xsl:attribute>
								</xsl:if>Mrs.
							</option>
							<option value="Ms.">
								<xsl:if test="($TITLE='Ms.')">
									<xsl:attribute name="selected">selected</xsl:attribute>
								</xsl:if>Ms.
							</option>
							<option value="Miss">
								<xsl:if test="($TITLE='Miss')">
									<xsl:attribute name="selected">selected</xsl:attribute>
								</xsl:if>Miss
							</option>
					</select></a></td></tr>

					<tr><td valign="top" height="4" colspan="4"><img src="/engresources/images/s.gif" border="0" height="4"/></td></tr>
					<tr><td valign="top" width="2"><img src="/engresources/images/s.gif" border="0" width="2"/></td><td valign="top"><font color="red"><b>*</b></font><a CLASS="MedBlackText">First Name: </a></td><td valign="top" width="1"><img src="/engresources/images/s.gif" border="0"/></td>
						<td valign="top"><a CLASS="MedBlackText">
							<input type="text" name="firstname" size="28" maxlength="64">
								<xsl:attribute name="value">
								<xsl:value-of select="USER-PROFILE/FIRST-NAME"/>
								</xsl:attribute>
							</input></a>
						</td>
					</tr>
					<tr><td valign="top" height="4" colspan="4"><img src="/engresources/images/s.gif" border="0" height="4"/></td></tr>
					<tr><td valign="top" width="2"><img src="/engresources/images/s.gif" border="0" width="2"/></td><td valign="top"><font color="red"><b>*</b></font><a CLASS="MedBlackText">Last Name: </a></td><td valign="top" width="1"><img src="/engresources/images/s.gif" border="0"/></td>
						<td valign="top"><a CLASS="MedBlackText">
						<input type="text" name="lastname" size="28" maxlength="64">
							<xsl:attribute name="value">
								<xsl:value-of select="USER-PROFILE/LAST-NAME"/>
							</xsl:attribute>
						</input></a>
						</td>
					</tr>
					<tr><td valign="top" height="4" colspan="4"><img src="/engresources/images/s.gif" border="0" height="4"/></td></tr>
					<tr><td valign="top" width="2"><img src="/engresources/images/s.gif" border="0" width="2"/></td><td valign="top"><font color="red"><b>*</b></font><a CLASS="MedBlackText">E-mail address: </a></td><td valign="top" width="1"><img src="/engresources/images/s.gif" border="0"/></td>
						<td valign="top"><a CLASS="MedBlackText">
						<input type="text" name="email" size="28">
							<xsl:attribute name="value">
							<xsl:value-of select="USER-PROFILE/EMAIL"/>
							</xsl:attribute>
						</input></a>
						</td>
					</tr>
					<tr><td valign="top" height="4" colspan="4"><img src="/engresources/images/s.gif" border="0" height="4"/></td></tr>
					<tr><td valign="top" width="2"><img src="/engresources/images/s.gif" border="0" width="2"/></td><td valign="top" colspan="3"><a CLASS="MedBlackText">Specify a password between 6 and 16 characters.</a></td></tr>
					<tr><td valign="top" height="4" colspan="4"><img src="/engresources/images/s.gif" border="0" height="4"/></td></tr>
					<tr><td valign="top" width="2"><img src="/engresources/images/s.gif" border="0" width="2"/></td><td valign="top"><font color="red"><b>*</b></font><a CLASS="MedBlackText">Choose a Password: </a></td><td valign="top" width="1"><img src="/engresources/images/s.gif" border="0"/></td>
						<td valign="top"><a CLASS="MedBlackText">
							<input type="password" name="password" size="28">
							<xsl:attribute name="value">
								<xsl:value-of select="USER-PROFILE/PASSWORD"/>
							</xsl:attribute>
							</input></a>
						</td>
					</tr>
					<tr><td valign="top" height="4" colspan="4"><img src="/engresources/images/s.gif" border="0" height="4"/></td></tr>
					<tr><td valign="top" width="2"><img src="/engresources/images/s.gif" border="0" width="2"/></td><td valign="top"><font color="red"><b>*</b></font><a CLASS="MedBlackText">Confirm password: </a></td><td valign="top" width="1"><img src="/engresources/images/s.gif" border="0"/></td>
						<td valign="top"><a CLASS="MedBlackText">
							<input type="password" name="confpassword" size="28" maxlength="16">
							<xsl:attribute name="value">
								<xsl:value-of select="USER-PROFILE/PASSWORD"/>
							</xsl:attribute>
							</input></a>
						</td>
					</tr>
					<tr><td valign="top" height="4" colspan="4"><img src="/engresources/images/s.gif" border="0" height="4"/></td></tr>
					<tr><td valign="top" width="2"><img src="/engresources/images/s.gif" border="0" width="2"/></td>
					<td valign="top" colspan="3">
					<input type="checkbox" name="announceflag" value=" ">
						<xsl:variable name="ANNOUNCE-FLAG">
							<xsl:value-of select="USER-PROFILE/ANNOUNCE-FLAG"/>
						</xsl:variable>
						<xsl:if test="($ANNOUNCE-FLAG='001')">
							<xsl:attribute name="checked">checked</xsl:attribute>
						</xsl:if>
					</input>
					<a CLASS="ExSmBlackText">Yes, Please send me information about Engineering Village or related products from time to time. The information I have provided here is confidential and it will not be released to a third party.</a></td></tr>
					<tr><td valign="top" height="4" colspan="4"><img src="/engresources/images/s.gif" border="0" height="4"/></td></tr>
					<tr><td valign="top" width="2"><img src="/engresources/images/s.gif" border="0" width="2"/></td><td valign="top" colspan="3"><input type="image" src="/engresources/images/sub.gif" border="0" alt="Sends registration"/> &#160; &#160; <a><xsl:attribute name="href">/controller/servlet/Controller?<xsl:value-of select="java:decode(BACKURL)"/></xsl:attribute><img src="/engresources/images/cancel.gif" border="0" alt="Go back to previous screen"/></a> &#160; &#160; <input type="image" src="/engresources/images/res.gif" border="0" alt="Clear entered text" name="reset" value="Reset" onClick="javascript:reset();return false;"/></td></tr>
					<tr><td valign="top" colspan="4" height="4"><img src="/engresources/images/s.gif" border="0" height="4"/></td></tr>
					</table>
				  </td>
		        </tr>
                <tr><td valign="top" colspan="4" height="4"><img src="/engresources/images/s.gif" border="0" height="4" /></td></tr>
              </table>
            </td>
            <td width="1" bgcolor="#3173b5"><img src="/engresources/images/s.gif" width="1" /></td>
          </tr>
          <tr><td colspan="3" height="1" bgcolor="#3173b5"><img src="/engresources/images/s.gif" height="1" /></td></tr>
        </FORM>
        </table>

		<!-- Insert the Footer table -->
		<xsl:apply-templates select="FOOTER">
			<xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
			<xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
		</xsl:apply-templates>

		<xsl:text disable-output-escaping="yes">
		<![CDATA[
			<xsl:comment>
				<SCRIPT LANGUAGE="JavaScript">
					//This Javascript is used to give focus to the first text box in the form when loading the page
					document.register.firstname.focus();
				</SCRIPT>
			</xsl:comment>
		]]>
		</xsl:text>
		</center>
		</body>
		</html>
</xsl:when>
<!-- display the confirmation message if the registration is successfull. -->
<xsl:otherwise>
	<html>
	<head>
		<title>Personal Account Created</title>
		<SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js"/>
	</head>
	<body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0" >
	<center>
	<!-- Table for the logo area -->

	<!-- APPLY HEADER -->
	<xsl:apply-templates select="HEADER">
		<xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
		<xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
	</xsl:apply-templates>

	<!-- Insert the Global Links -->
	<!-- logo, search history, selected records, my folders. end session -->
	<xsl:apply-templates select="GLOBAL-LINKS">
		<xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
		<xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
	</xsl:apply-templates>

	<table border="0" width="99%" cellspacing="0" cellpadding="0" bgcolor="#C3C8D1">
		<tr>
		<td height="24" bgcolor="#C3C8D1" ><img src="/engresources/images/s.gif" /></td>
		</tr>
	</table>

	<!-- Table below the logo area -->
	<!-- Area for confirmation message -->
	<table border="0" width="99%" cellspacing="0" cellpadding="0">
		<tr><td valign="top" height="20"><img src="/engresources/images/spacer.gif" border="0" height="20"/></td></tr>
		<tr><td valign="top"><a class="EvHeaderText">Personal Account Created</a></td></tr>
		<tr><td valign="top" height="10"><img src="/engresources/images/spacer.gif" border="0" height="10"/></td></tr>
		<tr>
			<td valign="top"><a CLASS="MedBlackText">Your personal account has been created successfully,
			and you are now logged in to your personal account. A registration confirmation message has been
			sent to your e-mail address. Please remember your account e-mail address and password for your future visits.
			</a></td>
		</tr>
		<tr><td valign="top" height="8"><img src="/engresources/images/spacer.gif" border="0" height="8"/></td></tr>
		<tr>
			<td valign="top" align="left">
			&#160;<a>
			<xsl:choose>
				<xsl:when test="not($NEXTURL = '')">
					<xsl:choose>
						<xsl:when test="not($BACKURL = '')">
							<xsl:attribute name="href">/controller/servlet/Controller?<xsl:value-of select="java:decode(NEXTURL)"/>&amp;backurl=<xsl:value-of select="BACKURL"/></xsl:attribute>
						</xsl:when>
						<xsl:otherwise>
							<xsl:attribute name="href">/controller/servlet/Controller?<xsl:value-of select="java:decode(NEXTURL)"/></xsl:attribute>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:otherwise>
					<xsl:attribute name="href">/controller/servlet/Controller?CID=myprofile&amp;database=<xsl:value-of select="DATABASE"/></xsl:attribute>
				</xsl:otherwise>
			</xsl:choose>

			<img src="/engresources/images/continue_blue.gif" border="0"/>
			</a>
			</td>
		</tr>
	</table>

	<!-- Insert the Footer table -->
	<xsl:apply-templates select="FOOTER">
		<xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
		<xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
	</xsl:apply-templates>
	</center>
	</body>
	</html>
</xsl:otherwise>
</xsl:choose>
</xsl:template>
</xsl:stylesheet>
