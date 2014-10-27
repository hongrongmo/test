<?xml version="1.0" ?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>'>
  <!ENTITY spcr8 '<img width="8" height="1" src="/engresources/images/s.gif" border="0" />'>
]>

<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:html="http://www.w3.org/TR/REC-html40"
	xmlns:java="java:java.net.URLDecoder"
	exclude-result-prefixes="java html xsl"
>
 <!--
 	 This page displays
 	 1. login form if the login status is false,
 	 2. login form with error message if the login fails and
 	 3. navigate to the appropriate page.
 -->

<xsl:include href="Header.xsl" />
<xsl:include href="GlobalLinks.xsl" />
<xsl:include href="PersonalGrayBar.xsl" />
<xsl:include href="Footer.xsl" />

<xsl:template match="PAGE">

<xsl:variable name="CID">
	<xsl:value-of select="CID"/>
</xsl:variable>

<xsl:variable name="SEARCH-TYPE">
	<xsl:value-of select="SEARCH-TYPE"/>
</xsl:variable>

<xsl:variable name="NEWSEARCHTYPE">
  <xsl:choose>
	  <xsl:when test="boolean($SEARCH-TYPE='Thesaurus')">thesSearch</xsl:when>
	  <xsl:when test="boolean($SEARCH-TYPE='Quick')">quickSearch</xsl:when>
	  <xsl:when test="boolean($SEARCH-TYPE='Expert')">expertSearch</xsl:when>
	  <xsl:when test="boolean($SEARCH-TYPE='Easy')">easySearch</xsl:when>
	  <xsl:otherwise>quickSearch</xsl:otherwise>
  </xsl:choose>
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
	<xsl:value-of  select="LOGIN-FORM/DISPLAY-FORM"/>
</xsl:variable>

<xsl:variable name="LOGIN-STATUS">
	<xsl:value-of  select="LOGIN-FORM/LOGIN-STATUS"/>
</xsl:variable>

<xsl:variable name="EMAIL-EXISTS">
<xsl:value-of select="LOGIN-FORM/EMAIL-EXISTS"/>
</xsl:variable>

<xsl:variable name="BACKURL">
<xsl:value-of select="BACKURL"/>
</xsl:variable>

<xsl:variable name="NEXTURL">
<xsl:value-of select="NEXTURL"/>
</xsl:variable>

<html>
<head>
	<title>Personal Account Login</title>
<SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js"/>
	<xsl:text disable-output-escaping="yes">
		<![CDATA[
			<xsl:comment>

			<SCRIPT type="TEXT/JAVASCRIPT" language="javascript">
				/* Hide content from old browsers */
				// this function validates all the form fields before submition.

				function uncheckEmail() {
					if (window.opener.searchhistoryform) {
						if (window.opener.searchhistoryform.emailalert){
							window.opener.searchhistoryform.emailalert.checked = false;
						}
					}
				}

				function validateLogin(personallogin)
				{
					if (personallogin.email.value =="")
					{
						 window.alert ("You must enter an e-mail address(username)");
						 personallogin.email.value = "";
						 personallogin.email.focus();
						 return false;
					}
					var emailValid = validateEmail(personallogin.email.value);
					if (!emailValid)
					{
						alert("The e-mail field contains an invalid e-mail Format.\nPlease enter your correct e-mail address.");
						personallogin.email.focus();
						return false;
					}
					if (personallogin.password.value =="")
					{
						alert ("You must enter a password");
						   personallogin.password.focus();
						   return false;
					}
					else
					{
							var pwdLength= personallogin.password.value.length;
							var tempPwd = personallogin.password.value;
							var tempLength=0;
							if(tempPwd.substring(0,1) == ' ')
							{
								   window.alert("Password should not starts with spaces");
								   personallogin.password.focus();
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
									   personallogin.password.value = "";
									   personallogin.password.focus();
									   return false;
								}
							}
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
	<xsl:with-param name="SEARCH-TYPE" select="$SEARCH-TYPE"/>
</xsl:apply-templates>

<!-- Insert the Global Links -->
<!-- logo, search history, selected records, my folders. end session -->
<xsl:apply-templates select="GLOBAL-LINKS">
	<xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
	<xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
</xsl:apply-templates>

<!-- Gray Navigation Bar -->
<xsl:call-template name="P-GRAY-BAR">
	<xsl:with-param name="SESSIONID" select="$SESSION-ID"/>
	<xsl:with-param name="DATABASE" select="$DATABASE"/>
	<xsl:with-param name="SEARCHID" select="$SEARCH-ID"/>
	<xsl:with-param name="COUNT" select="$COUNT"/>
	<xsl:with-param name="SEARCHTYPE" select="$SEARCH-TYPE"/>
	<xsl:with-param name="CID" select="$CID"/>
</xsl:call-template>

<!-- Table below the logo area -->
<table border="0" width="80%" cellspacing="0" cellpadding="0">
	<FORM name="personallogin" action="/controller/servlet/Controller?CID=personalLoginConfirm" METHOD="POST"  onSubmit="return validateLogin(personallogin)" >

    <input type="hidden" name="displayform" value="{$DISPLAY-FORM}"/>
    <input type="hidden" name="database" value="{$DATABASE}"/>

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
			<a CLASS="LgWhiteText"><b>Personal Account Login</b></a>
		  </td>
		</tr>
		<tr><td valign="top" height="10" colspan="4"><img src="/engresources/images/s.gif" border="0" height="10" /></td></tr>
		<!-- Content Row / Indented with Spacer / Content cell/should can contain table
		  to acheive specific content layout within template -->
		<tr>
		  <td width="2"><img src="/engresources/images/s.gif" width="2" /></td>
		  <td valign="top" width="60%">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr><td colspan="3" height="10"><img src="/engresources/images/s.gif" height="10"/></td></tr>
				<xsl:choose>
				<xsl:when test="($EMAIL-EXISTS='false')">
				<tr><td valign="top" colspan="3">
					<a class="RedText">Invalid Login, please try again. Be sure to enter the email address used when registering for this account.</a>
				</td></tr>
				</xsl:when>
				<xsl:otherwise>
				<tr><td valign="top" colspan="3">
					<a class="MedBlackText">You must login to your personal account to save searches, save records, and create E-mail Alerts.  </a>
				</td></tr>
				</xsl:otherwise>
				</xsl:choose>
				<tr><td colspan="3" height="15"><img src="/engresources/images/s.gif" height="15"/></td></tr>
				<tr><td valign="middle" align="right"><a CLASS="MedBlackText"><label for="txtUsr">E-mail address:</label> </a></td><td width="5"><img src="/engresources/images/s.gif" width="5"/></td><td valign="top"><input id="txtUsr" type="text" name="email" size="20"/></td></tr>
				<tr><td colspan="3" height="5"><img src="/engresources/images/s.gif" height="5"/></td></tr>
				<tr><td valign="middle" align="right"><a CLASS="MedBlackText"><label for="txtPwd">Password:</label> </a></td><td width="5"><img src="/engresources/images/s.gif" width="5"/></td><td valign="top"><input id="txtPwd" type="password" name="password" size="20"/></td></tr>
				<tr><td colspan="3" height="5"><img src="/engresources/images/s.gif" height="5"/></td></tr>
				<tr><td colspan="2"></td><td valign="top"><input type="image" src="/engresources/images/login.gif" alt="Login" border="0"/></td></tr>
				<tr><td colspan="3" height="20"><img src="/engresources/images/s.gif" height="20"/></td></tr>
				<tr><td colspan="3"><a class="MedBlackText">If you have forgotten your password, click </a><a CLASS="LgBlueLink" href="/controller/servlet/Controller?CID=requestPersonalPassword&amp;display=true&amp;displayform={$DISPLAY-FORM}&amp;database={$DATABASE}&amp;count={$COUNT}&amp;searchid={$SEARCH-ID}&amp;searchtype={$SEARCH-TYPE}&amp;docidlist={$DOCIDS}&amp;queryid={$QUERY-ID}&amp;option={$OPTION-VALUE}&amp;resultsformat={$RESULTS-FORMAT}&amp;source={$SOURCE}&amp;backurl={$BACKURL}&amp;nexturl={$NEXTURL}">here</a><a class="MedBlackText"> and we will send you your password. </a></td></tr>
			</table>
		  </td>
		  <td width="100"><img src="/engresources/images/s.gif" width="100"/></td>
		  <td valign="top" width="40%">
		  	<table border="0" cellspacing="0" cellpadding="0">
				<tr><td colspan="3" height="10"><img src="/engresources/images/s.gif" height="10"/></td></tr>
				<tr><td valign="top"><a class="MedBlackText">If not, </a><a CLASS="LgBlueLink" href="/controller/servlet/Controller?CID=personalAccountForm&amp;newregistration=true&amp;displayform={$DISPLAY-FORM}&amp;database={$DATABASE}&amp;count={$COUNT}&amp;searchid={$SEARCH-ID}&amp;docidlist={$DOCIDS}&amp;queryid={$QUERY-ID}&amp;option={$OPTION-VALUE}&amp;resultsformat={$RESULTS-FORMAT}&amp;source={$SOURCE}&amp;backurl={$BACKURL}&amp;nexturl={$NEXTURL}">Register Now</a><a class="MedBlackText">.  It's FREE and allows you to: </a>
				<ul>
				<a class="MedBlackText"><li>Get Weekly Email Alerts</li></a>
				<a class="MedBlackText"><li>Save Records</li></a>
				<a class="MedBlackText"><li>Save Searches</li></a>
				<a class="MedBlackText"><li>Create Folders</li></a>
				</ul>
				</td></tr>
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
			document.personallogin.email.focus();
		</SCRIPT>
	</xsl:comment>
]]>
</xsl:text>

</center>

</body>
</html>

</xsl:template>

</xsl:stylesheet>
