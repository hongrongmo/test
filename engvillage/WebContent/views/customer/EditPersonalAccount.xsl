<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:html="http://www.w3.org/TR/REC-html40"
	xmlns:java="java:java.net.URLEncoder"
	exclude-result-prefixes="java html xsl"
>
<xsl:include href="Header.xsl" />
<xsl:include href="GlobalLinks.xsl" />
<xsl:include href="Footer.xsl" />

<!--
	This file displays
	1. the user information of the user for editing .
	2. the user information of the user user with error message
	if the updation is not successfull.
	3. and confirmation message if the updation is successfull.
-->
<xsl:template match="PAGE">

<xsl:variable name="SESSION-ID">
	<xsl:value-of select="SESSION-ID"/>
</xsl:variable>

<xsl:variable name="SEARCH-ID">
		<xsl:value-of  select="SEARCH-ID"/>
</xsl:variable>

<xsl:variable name="COUNT">
		<xsl:value-of  select="COUNT"/>
</xsl:variable>

<xsl:variable name="DATABASE">
		<xsl:value-of  select="DATABASE"/>
</xsl:variable>

<xsl:variable name="USER-STATUS">
	<xsl:value-of  select="USER-PROFILE/USER-STATUS"/>
</xsl:variable>

<!-- conditional check for displaying the user status -->
<xsl:choose>
	<!-- display user information in the editable mode if the user status value is edit -->
	<xsl:when test="($USER-STATUS='edit')">

		<html>
		<head>
			<META HTTP-EQUIV="Pragma" CONTENT="no-cache"/>
			<META HTTP-EQUIV="Expires" CONTENT="-1"/>

			<title>Edit Your Personal Account</title>
        <SCRIPT LANGUAGE="Javascript" SRC="/static/js/StylesheetLinks.js"/>

		<xsl:text disable-output-escaping="yes">
		<![CDATA[
			<xsl:comment>
				<SCRIPT type="TEXT/JAVASCRIPT" language="javascript">
					 /* Hide content from old browsers */
					 // this function validates all the form fields before sending the request.
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
							window.alert("Please do not insert non-English accents or characters in first name field");
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
							window.alert("Please do not insert non-English accents or characters in last name field");
							register.lastname.value = "";
							register.lastname.focus();
							return false;
						}

						if (register.email.value =="")
						{
							alert ("Please enter your email address");
							register.email.focus();
							return false;
						}
						var emailValid = validateEmail(register.email.value);
						if (!emailValid)
						{
								alert("The email field contains an invalid email Format.\nPlease enter your correct email address.");
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
					// this function for validating of email format.
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
					// this function inform the user before deletion of user account.
					function deleteAlert(database)
					{
						if(confirm("Are you sure you want to remove your personal account from the system?") == true)
						{
							document.location ="/controller/servlet/Controller?EISESSION=$SESSIONID&CID=deletePersonalAccount&database="+database;;
						}
					}
					// end hiding content from old browsers
				</SCRIPT>
			</xsl:comment>
		]]>
		</xsl:text>
		<!-- -->
		</head>

		<body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">

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

    <!-- Empty Gray Navigation Bar / DO NOT CHANGE -->
    <table border="0" width="99%" cellspacing="0" cellpadding="0">
    	<tr><td valign="middle" height="24"  bgcolor="#C3C8D1"><img src="/static/images/s.gif" border="0"/></td></tr>
    </table>

      <!-- Center Main / DO NOT CHANGE -->
      <center>
        <!-- Outer Template Table / Blue Border / DO NOT CHANGE -->
        <table border="0" width="80%" cellspacing="0" cellpadding="0">
          <!-- Spacer Row / Provides space below Navigation Bar / DO NOT CHANGE -->
          <tr><td colspan="4" height="20"><img src="/static/images/s.gif" height="20" /></td></tr>
          <!-- Spacer Row / Blue Border / DO NOT CHANGE -->
          <tr><td colspan="3" height="1" bgcolor="#3173b5"><img src="/static/images/s.gif" height="1" /></td></tr>
          <tr>
            <!-- Spacer Cel / Blue Border / DO NOT CHANGE -->
            <td width="1" bgcolor="#3173b5"><img src="/static/images/s.gif" width="1" /></td>
            <!-- Content Cel / White (default) Background / DO NOT CHANGE -->
            <td valign="top">
              <!-- Inner Template Table / DO NOT CHANGE -->
              <table border="0" cellspacing="0" cellpadding="0" width="100%">
                <!-- Title Row / Indented with Spacer / Bold White Text on Blue Background -->
                <tr>
                  <!-- Indentation Cel / DO NOT CHANGE -->
                  <td width="2" bgcolor="#3173b5"><img src="/static/images/s.gif" width="2" /></td>
                  <!-- Title Cel / EDIT THIS -->
                  <td height="10" bgcolor="#3173B5" colspan="3">
                    <a CLASS="LgWhiteText"><b>Edit Personal Account</b></a>
                  </td>
                </tr>
                <!-- Spacer Row / White Background / DO NOT CHANGE -->
                <tr><td valign="top" height="10" colspan="4"><img src="/static/images/s.gif" border="0" height="10" /></td></tr>
                <!-- Content Row / Indented with Spacer / Content cell/should can contain table
                  to acheive specific content layout within template -->
                <tr>
                  <!-- Indentation Cel / DO NOT CHANGE -->
                  <td width="2"><img src="/static/images/s.gif" width="2" /></td>
                  <!-- Content Goes Here -->
                  <td colspan="3">
                    <!-- Content Goes Here -->

                  		<!-- Table for new user registration -->
                  		<table border="0" cellspacing="0" cellpadding="0" width="100%" >

                  			<FORM name="register" action="/controller/servlet/Controller?CID=updatePersonalAccount&amp;database={$DATABASE}&amp;count={$COUNT}&amp;searchid={$SEARCH-ID}" METHOD="POST"  onSubmit="return validateregister(register)" >
                  				<tr><td valign="top" height="4" colspan="4"><img src="images/spacer.gif" border="0" height="4"/></td></tr>
                  				<tr><td colspan="4" align="right" ><a href="javascript:deleteAlert({$DATABASE});"><img src="/static/images/removeaccount.gif" border="0"/></a>&#160;</td></tr>

                  				<xsl:variable name="EMAIL-EXISTS">
                  					<xsl:value-of select="USER-PROFILE/EMAIL-EXISTS"/>
                  				</xsl:variable>
                  				<xsl:if test="($EMAIL-EXISTS='true')">
                  					<tr>
                  						<td valign="top" colspan="4"><a CLASS="RedText">
                  						<!-- display the error message if the email already exists in the system. -->
                  											Your email address already exists in the system.
                  								Please use login form, or choose another email address.
                  						</a></td>
                  					</tr>
                  				</xsl:if>
                  				<tr><td valign="top" height="4" colspan="4"><img src="/static/images/s.gif" border="0" height="4"/></td></tr>
                  				<tr>
                  					<td valign="top" width="3" ><img src="/static/images/s.gif" border="0" width="3"/></td>
                  					<td valign="top" colspan="3" ><a CLASS="MedBlackText">To change personal information, overwrite current information and click "Submit".</a><br/><font color="red"><b>*</b>
                  					<a CLASS="MedBlackText">indicate required fields</a></font></td>
                  				</tr>
                  				<tr><td valign="top" height="10"  colspan="4"><img src="/static/images/s.gif" border="0" height="10"/></td></tr>
                  				<tr>
                  					<td valign="top" width="3" ><img src="/static/images/s.gif" border="0" width="3"/></td>
                  					<td valign="top" >&#160; <a CLASS="MedBlackText">Title:</a></td>
                  					<td valign="top" width="1" ><img src="/static/images/s.gif" border="0"/></td>
                  					<td valign="top" ><a CLASS="MedBlackText">

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
                  					</select>
                  					</a></td>
                  				</tr>
                  				<tr><td valign="top" height="4" colspan="4" ><img src="/static/images/s.gif" border="0" height="4"/></td></tr>
                  				<tr>
                  					<td valign="top" width="3" ><img src="/static/images/s.gif" border="0" width="3"/></td>
                  					<td valign="top" ><font color="red"><b>*</b></font><a CLASS="MedBlackText">First Name: </a></td>
                  					<td valign="top" width="1" ><img src="/static/images/s.gif" border="0"/></td>
                  					<td valign="top" ><a CLASS="MedBlackText">
                  					<input type="text" name="firstname" size="28">
                  						<xsl:attribute name="value">
                  							<xsl:value-of select="USER-PROFILE/FIRST-NAME"/>
                  						</xsl:attribute>
                  					</input>
                  					</a></td>
                  				</tr>
                  				<tr><td valign="top" height="4" colspan="4" ><img src="/static/images/s.gif" border="0" height="4"/></td></tr>
                  				<tr>
                  					<td valign="top" width="3" ><img src="/static/images/s.gif" border="0" width="3"/></td>
                  					<td valign="top" ><font color="red"><b>*</b></font><a CLASS="MedBlackText">Last Name: </a></td>
                  					<td valign="top" width="1" ><img src="/static/images/s.gif" border="0"/></td>
                  					<td valign="top" ><a CLASS="MedBlackText">
                  					<input type="text" name="lastname" size="28" maxlength="64">
                  						<xsl:attribute name="value">
                  							<xsl:value-of select="USER-PROFILE/LAST-NAME"/>
                  						</xsl:attribute>
                  					</input>
                  					</a></td>
                  				</tr>
                  				<tr><td valign="top" height="4" colspan="4" ><img src="/static/images/s.gif" border="0" height="4"/></td></tr>
                  				<tr>
                  					<td valign="top" width="3" ><img src="/static/images/s.gif" border="0" width="3"/></td>
                  					<td valign="top" ><font color="red"><b>*</b></font><a CLASS="MedBlackText">Email address: </a></td>
                  					<td valign="top" width="1" ><img src="/static/images/s.gif" border="0"/></td>
                  					<td valign="top" ><a CLASS="MedBlackText">
                  					<input type="text" name="email" size="28" maxlength="64">
                  						<xsl:attribute name="value">
                  							<xsl:value-of select="USER-PROFILE/EMAIL"/>
                  						</xsl:attribute>
                  					</input>
                  					</a></td>
                  				</tr>
                  				<tr>
                  					<td valign="top" width="3" ><img src="/static/images/s.gif" border="0" width="3"/></td>
                  					<td valign="top" colspan="3" >
                  					<a CLASS="MedBlackText">Specify a password between 6 and 16 characters.</a>
                  					</td>
                  				</tr>
                  				<tr><td valign="top" height="4" colspan="4" ><img src="/static/images/s.gif" border="0" height="4"/></td></tr>
                  				<tr>
                  					<td valign="top" width="3" ><img src="/static/images/s.gif" border="0" width="3"/></td>
                  					<td valign="top" ><font color="red"><b>*</b></font><a CLASS="MedBlackText">Password: </a></td>
                  					<td valign="top" width="1" ><img src="/static/images/s.gif" border="0"/></td>
                  					<td valign="top" ><a CLASS="MedBlackText">
                  					<input type="password" name="password" size="28">
                  						<xsl:attribute name="value">
                  							<xsl:value-of select="USER-PROFILE/PASSWORD"/>
                  						</xsl:attribute>
                  					</input>
                  					</a></td>
                  				</tr>
                  				<tr><td valign="top" height="4" colspan="4" ><img src="/static/images/s.gif" border="0" height="4"/></td></tr>
                  				<tr>
                  					<td valign="top" width="3" ><img src="/static/images/s.gif" border="0" width="3"/></td>
                  					<td valign="top" ><font color="red"><b>*</b></font><a CLASS="MedBlackText">Confirm Password: </a></td>
                  					<td valign="top" width="1" ><img src="/static/images/s.gif" border="0"/></td>
                  					<td valign="top" ><a CLASS="MedBlackText">
                  					<input type="password" name="confpassword" size="28">
                  						<xsl:attribute name="value">
                  							<xsl:value-of select="USER-PROFILE/PASSWORD"/>
                  						</xsl:attribute>
                  					</input>
                  					</a></td>
                  				</tr>
                  				<tr><td valign="top" height="4" colspan="4" ><img src="/static/images/s.gif" border="0" height="4"/></td></tr>
                  				<tr>
                  					<td valign="top" width="3" ><img src="/static/images/s.gif" border="0" width="3"/></td>
                  					<td valign="top" colspan="3" >
                  					<input type="checkbox" name="announceflag" value=" ">

                  					<xsl:variable name="ANNOUNCE-FLAG">
                  							<xsl:value-of select="USER-PROFILE/ANNOUNCE-FLAG"/>
                  					</xsl:variable>

                  					<xsl:if test="($ANNOUNCE-FLAG='001')">
                  						<xsl:attribute name="checked">checked</xsl:attribute>
                  					</xsl:if>
                  					</input>
                  					<a CLASS="ExSmBlackText">Please send me information about Engineering Village or related products from time to time.  The information I have provided here is confidential and will not be released to a third party.</a></td>
                  				</tr>
                  				<tr><td valign="top" height="4" colspan="4" ><img src="/static/images/s.gif" border="0" height="4"/></td></tr>
                  				<tr>
                  					<td valign="top" width="3" ><img src="/static/images/s.gif" border="0" width="3"/></td>
                  					<td valign="top" colspan="3" >

                  						<input type="image" src="/static/images/sub.gif" name="submit" value="Submit"/> &#160; &#160;
                  						<input type="image" src="/static/images/cancel.gif" name="cancel" value="Cancel" onclick="javascript:document.location='/controller/servlet/Controller?EISESSION=$SESSIONID&amp;CID=myprofile&amp;database={$DATABASE}'; return false;"/> &#160; &#160;
                  						<input type="image" src="/static/images/res.gif" name="reset" value="Reset" onclick="javascript:reset(); return false;"/> &#160; &#160;
                            </td>
                  				</tr>
                  				<tr><td valign="top" colspan="4" height="4" ><img src="/static/images/s.gif" border="0" height="4"/></td></tr>
                  			</FORM>
                  		</table>
                    <!-- Content Goes Here -->
                  </td>
                </tr>
                <!-- Sopacer Row Below Content / DO NOT CHANGE -->
                <tr><td valign="top" colspan="4" height="4"><img src="/static/images/s.gif" border="0" height="4" /></td></tr>
              </table>
            </td>
            <!-- Spacer Cel / Blue Border / DO NOT CHANGE -->
            <td width="1" bgcolor="#3173b5"><img src="/static/images/s.gif" width="1" /></td>
          </tr>
          <!-- Spacer Row / Blue Border / DO NOT CHANGE -->
          <tr><td colspan="3" height="1" bgcolor="#3173b5"><img src="/static/images/s.gif" height="1" /></td></tr>
        </table>
      </center>

		<!-- Insert the Footer table -->
		<xsl:apply-templates select="FOOTER">
			<xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
			<xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
		</xsl:apply-templates>

		</center>

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
		</body>
		</html>
</xsl:when>

<xsl:otherwise>

	<xsl:variable name="USER-EXISTS">
		<xsl:value-of  select="USER-PROFILE/USER-EXISTS"/>
	</xsl:variable>

	<!-- conditional check for whether the user deleted or updated. -->
	<xsl:choose>
		<!-- if the user updated the information display the confirmation message -->
		<xsl:when test="($USER-EXISTS='true')">
				<html>
				<head>
					<title>Personal Account Updated</title>
          <SCRIPT LANGUAGE="Javascript" SRC="/static/js/StylesheetLinks.js"/>
				</head>
				<body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">

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

        <!-- Empty Gray Navigation Bar / DO NOT CHANGE -->
        <table border="0" width="99%" cellspacing="0" cellpadding="0">
        	<tr><td valign="middle" height="24"  bgcolor="#C3C8D1"><img src="/static/images/s.gif" border="0"/></td></tr>
        </table>

        <!-- Center Main / DO NOT CHANGE -->
        <center>
          <!-- Outer Template Table / Blue Border / DO NOT CHANGE -->
          <table border="0" width="80%" cellspacing="0" cellpadding="0">
            <!-- Spacer Row / Provides space below Navigation Bar / DO NOT CHANGE -->
            <tr><td colspan="4" height="20"><img src="/static/images/s.gif" height="20" /></td></tr>
            <!-- Spacer Row / Blue Border / DO NOT CHANGE -->
            <tr><td colspan="3" height="1" bgcolor="#3173b5"><img src="/static/images/s.gif" height="1" /></td></tr>
            <tr>
              <!-- Spacer Cel / Blue Border / DO NOT CHANGE -->
              <td width="1" bgcolor="#3173b5"><img src="/static/images/s.gif" width="1" /></td>
              <!-- Content Cel / White (default) Background / DO NOT CHANGE -->
              <td valign="top">
                <!-- Inner Template Table / DO NOT CHANGE -->
                <table border="0" cellspacing="0" cellpadding="0" width="100%">
                  <!-- Title Row / Indented with Spacer / Bold White Text on Blue Background -->
                  <tr>
                    <!-- Indentation Cel / DO NOT CHANGE -->
                    <td width="2" bgcolor="#3173b5"><img src="/static/images/s.gif" width="2" /></td>
                    <!-- Title Cel / EDIT THIS -->
                    <td height="10" bgcolor="#3173B5" colspan="3">
                      <a CLASS="LgWhiteText"><b>Personal Account Updated</b></a>
                    </td>
                  </tr>
                  <!-- Spacer Row / White Background / DO NOT CHANGE -->
                  <tr><td valign="top" height="10" colspan="4"><img src="/static/images/s.gif" border="0" height="10" /></td></tr>
                  <!-- Content Row / Indented with Spacer / Content cell/should can contain table
                    to acheive specific content layout within template -->
                  <tr>
                    <!-- Indentation Cel / DO NOT CHANGE -->
                    <td width="2"><img src="/static/images/s.gif" width="2" /></td>
                    <!-- Content Goes Here -->
                    <td colspan="3">
                      <!-- Content Goes Here -->
              				<!-- Area for confirmation message -->
              				<table border="0" cellspacing="0" cellpadding="0" width="100%" >
              					<tr><td valign="top" height="8"><img src="/static/images/s.gif" border="0" height="8"/></td></tr>
              					<tr>
              						<td valign="top"><a CLASS="MedBlackText">
              								Your personal account has been updated successfully.
              								An updated account confirmation message has been sent to your email address.
              						</a></td>
              					</tr>
              					<tr><td valign="top" height="8"><img src="/static/images/s.gif" border="0" height="8"/></td></tr>
              				</table>
                      <!-- Content Goes Here -->
                    </td>
                  </tr>
                  <!-- Sopacer Row Below Content / DO NOT CHANGE -->
                  <tr><td valign="top" colspan="4" height="4"><img src="/static/images/s.gif" border="0" height="4" /></td></tr>
                </table>
              </td>
              <!-- Spacer Cel / Blue Border / DO NOT CHANGE -->
              <td width="1" bgcolor="#3173b5"><img src="/static/images/s.gif" width="1" /></td>
            </tr>
            <!-- Spacer Row / Blue Border / DO NOT CHANGE -->
            <tr><td colspan="3" height="1" bgcolor="#3173b5"><img src="/static/images/s.gif" height="1" /></td></tr>
          </table>
        </center>

				<!-- Insert the Footer table -->
				<xsl:apply-templates select="FOOTER">
					<xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
					<xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
				</xsl:apply-templates>

        </center>

				</body>
				</html>
		</xsl:when>
		<xsl:otherwise>
			<!-- if the user deleted from the system display the confirmation message -->
				<html>
				<head>
					<title>Personal Account Removed</title>
          <SCRIPT LANGUAGE="Javascript" SRC="/static/js/StylesheetLinks.js"/>
				</head>
				<body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">

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

        <!-- Empty Gray Navigation Bar / DO NOT CHANGE -->
        <table border="0" width="99%" cellspacing="0" cellpadding="0">
        	<tr><td valign="middle" height="24"  bgcolor="#C3C8D1"><img src="/static/images/s.gif" border="0"/></td></tr>
        </table>

        <!-- Center Main / DO NOT CHANGE -->
        <center>
          <!-- Outer Template Table / Blue Border / DO NOT CHANGE -->
          <table border="0" width="80%" cellspacing="0" cellpadding="0">
            <!-- Spacer Row / Provides space below Navigation Bar / DO NOT CHANGE -->
            <tr><td colspan="4" height="20"><img src="/static/images/s.gif" height="20" /></td></tr>
            <!-- Spacer Row / Blue Border / DO NOT CHANGE -->
            <tr><td colspan="3" height="1" bgcolor="#3173b5"><img src="/static/images/s.gif" height="1" /></td></tr>
            <tr>
              <!-- Spacer Cel / Blue Border / DO NOT CHANGE -->
              <td width="1" bgcolor="#3173b5"><img src="/static/images/s.gif" width="1" /></td>
              <!-- Content Cel / White (default) Background / DO NOT CHANGE -->
              <td valign="top">
                <!-- Inner Template Table / DO NOT CHANGE -->
                <table border="0" cellspacing="0" cellpadding="0" width="100%">
                  <!-- Title Row / Indented with Spacer / Bold White Text on Blue Background -->
                  <tr>
                    <!-- Indentation Cel / DO NOT CHANGE -->
                    <td width="2" bgcolor="#3173b5"><img src="/static/images/s.gif" width="2" /></td>
                    <!-- Title Cel / EDIT THIS -->
                    <td height="10" bgcolor="#3173B5" colspan="3">
                      <a CLASS="LgWhiteText"><b>Personal Account Updated</b></a>
                    </td>
                  </tr>
                  <!-- Spacer Row / White Background / DO NOT CHANGE -->
                  <tr><td valign="top" height="10" colspan="4"><img src="/static/images/s.gif" border="0" height="10" /></td></tr>
                  <!-- Content Row / Indented with Spacer / Content cell/should can contain table
                    to acheive specific content layout within template -->
                  <tr>
                    <!-- Indentation Cel / DO NOT CHANGE -->
                    <td width="2"><img src="/static/images/s.gif" width="2" /></td>
                    <!-- Content Goes Here -->
                    <td colspan="3">
                      <!-- Content Goes Here -->
              				<!-- Area for confirmation message -->
              				<table border="0" cellspacing="0" cellpadding="0" width="100%" >
              					<tr><td valign="top" height="8"><img src="/static/images/s.gif" border="0" height="8"/></td></tr>
              					<tr>
              						<td valign="top"><a CLASS="MedBlackText">
              								Your personal account has been removed.
              						</a></td>
              					</tr>
              					<tr><td valign="top" height="8"><img src="/static/images/s.gif" border="0" height="8"/></td></tr>
              				</table>
                      <!-- Content Goes Here -->
                    </td>
                  </tr>
                  <!-- Sopacer Row Below Content / DO NOT CHANGE -->
                  <tr><td valign="top" colspan="4" height="4"><img src="/static/images/s.gif" border="0" height="4" /></td></tr>
                </table>
              </td>
              <!-- Spacer Cel / Blue Border / DO NOT CHANGE -->
              <td width="1" bgcolor="#3173b5"><img src="/static/images/s.gif" width="1" /></td>
            </tr>
            <!-- Spacer Row / Blue Border / DO NOT CHANGE -->
            <tr><td colspan="3" height="1" bgcolor="#3173b5"><img src="/static/images/s.gif" height="1" /></td></tr>
          </table>
        </center>

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
</xsl:otherwise>
</xsl:choose>

</xsl:template>
</xsl:stylesheet>
