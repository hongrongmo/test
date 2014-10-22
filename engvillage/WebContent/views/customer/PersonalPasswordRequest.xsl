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

<!-- This file displays
	1. email request form if the password status is false,
	2.email request form if the email entered is wrong.
	3.and confirmation page if the entered email is correct.
-->
<xsl:template match="PAGE">

<xsl:variable name="SESSION-ID">
	<xsl:value-of select="SESSION-ID"/>
</xsl:variable>

<xsl:variable name="SEARCH-ID">
	<xsl:value-of  select="SEARCH-ID"/>
</xsl:variable>

<xsl:variable name="SEARCH-TYPE">
	<xsl:value-of  select="SEARCH-TYPE"/>
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

<xsl:variable name="PASSWORD-STATUS">
	<xsl:value-of select="PASSWORD-REQUEST/PASSWORD-STATUS"/>
</xsl:variable>

<xsl:variable name="BACKURL">
	<xsl:value-of select="BACKURL"/>
</xsl:variable>

<xsl:variable name="NEXTURL">
	<xsl:value-of select="NEXTURL"/>
</xsl:variable>

<!-- conditional check to display the email request form -->
<xsl:choose>
<!-- display form is the password status is false -->
<xsl:when test="($PASSWORD-STATUS='false')">

<xsl:variable name="DISPLAY-FORM">
	<xsl:value-of  select="PASSWORD-REQUEST/DISPLAY-FORM"/>
</xsl:variable>

<xsl:variable name="EMAIL-EXISTS">
	<xsl:value-of select="PASSWORD-REQUEST/EMAIL-EXISTS"/>
</xsl:variable>


<html>
<head>
	<title>Password Request</title>
  	<SCRIPT LANGUAGE="Javascript" SRC="/static/js/StylesheetLinks.js"/>
	<xsl:text disable-output-escaping="yes">
		<![CDATA[
			<xsl:comment>
				<SCRIPT type="TEXT/JAVASCRIPT" language="javascript">
				<!-- /* Hide content from old browsers */
				function validateLogin(personallogin)
				{
				    if (personallogin.email.value =="")
				    {
			        alert ("You must enter Email address");
              personallogin.email.focus();
					    return false;
				    }
					var emailValid = validateEmail(personallogin.email.value);
					if (!emailValid)
					{
							alert("The email field contains an invalid email Format.\nPlease enter your correct email address.");
							personallogin.email.focus();
							return false;
					}

				}
				// function to validate email format
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
				// end hiding content from old browsers  -->
				</SCRIPT>
		</xsl:comment>
		]]>
		</xsl:text>
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

  <!-- Empty Gray Navigation Bar / DO NOT CHANGE -->
  <table border="0" width="99%" cellspacing="0" cellpadding="0">
  	<tr><td valign="top" height="24" bgcolor="#C3C8D1"><img src="/static/images/s.gif" border="0"/></td></tr>
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
                    <a CLASS="LgWhiteText"><b>Password Request</b></a>
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
                    <table border="0" cellspacing="0" cellpadding="0" width="100%" >
                    <FORM name="personallogin" action="/controller/servlet/Controller?CID=sendPersonalPassword" METHOD="POST"  onSubmit="return validateLogin(personallogin)" >
                        <input type="hidden" name="database"><xsl:attribute name="value"><xsl:value-of select="$DATABASE"/></xsl:attribute></input>
                        <input type="hidden" name="searchid"><xsl:attribute name="value"><xsl:value-of select="$SEARCH-ID"/></xsl:attribute></input>
                        <input type="hidden" name="count"><xsl:attribute name="value"><xsl:value-of select="$COUNT"/></xsl:attribute></input>
                        <input type="hidden" name="searchtype"><xsl:attribute name="value"><xsl:value-of select="$SEARCH-TYPE"/></xsl:attribute></input>

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
                    	<!-- display error message if the email does not exists in the system. -->
                    	<xsl:if test="($EMAIL-EXISTS='false')">
                    		<tr><td valign="top" colspan="2"><a CLASS="RedText">The Email you provided is not in system</a></td></tr>
                    	</xsl:if>
                    	<tr>
                    		<td valign="top" height="4" ><img src="/static/images/spacer.gif" border="0" height="3"/></td>
                    		<td valign="top" >
                    			<a CLASS="MedBlackText">Please enter your Email address and your password will be sent to that Email address. </a>
                    		</td>
                    	</tr>
                    	<tr><td valign="top" height="4"  colspan="2"><img src="/static/images/spacer.gif" border="0" height="3"/></td></tr>
                    	<tr>
                    		<td valign="top" width="3" ><img src="/static/images/spacer.gif" border="0" width="3"/></td>
                    		<td valign="top" ><a CLASS="MedBlackText">
                    			Email address: <BR/><input type="text" name="email" size="28"/>
                    		</a></td>
                    	</tr>
                    	<tr><td valign="top" height="4" colspan="2" ><img src="/static/images/spacer.gif" border="0" height="4"/></td></tr>
                    	<tr>
                    		<td valign="top" width="3" ><img src="/static/images/spacer.gif" border="0" width="3"/></td>
                    		<td valign="top" >
                          <input type="image" src="/static/images/sub.gif" name="submit" value="Submit"/> &#160; &#160;
                          <input type="image" src="/static/images/cancel.gif" name="cancel" value="Cancel">
                            <xsl:choose>
                              <xsl:when test="($BACKURL != '')">
                                <xsl:attribute name="onclick">javascript:document.location='/controller/servlet/Controller?CID=personalLoginForm&amp;searchid=<xsl:value-of select="$SEARCH-ID"/>&amp;count=<xsl:value-of select="$COUNT"/>&amp;searchtype=<xsl:value-of select="$SEARCH-TYPE"/>&amp;displaylogin=true&amp;database=<xsl:value-of select="$DATABASE"/>&amp;nexturl=<xsl:value-of select="$NEXTURL"/>&amp;backurl=<xsl:value-of select="$BACKURL"/>'; return false;</xsl:attribute>
                              </xsl:when>
                              <xsl:otherwise>
                                <xsl:attribute name="onclick">javascript:document.location='/controller/servlet/Controller?CID=personalLoginForm&amp;searchid=<xsl:value-of select="$SEARCH-ID"/>&amp;count=<xsl:value-of select="$COUNT"/>&amp;searchtype=<xsl:value-of select="$SEARCH-TYPE"/>&amp;displaylogin=true&amp;database=<xsl:value-of select="$DATABASE"/>&amp;nexturl=<xsl:value-of select="$NEXTURL"/>'; return false;</xsl:attribute>
                              </xsl:otherwise>
                            </xsl:choose>
                          </input>

                    		</td>
                   		</tr>
                    	<tr><td valign="top" height="4" colspan="2" ><img src="/static/images/spacer.gif" border="0" height="4"/></td></tr>
                    	<xsl:if test="($EMAIL-EXISTS='false')">
                    		<tr>
                    			<td valign="top" width="3" ><img src="/static/images/spacer.gif" border="0" width="3"/></td>
                    			<td valign="top" ><a CLASS="MedBlackText">If you are not a registered user </a>
                    			<a CLASS="LgBlueLink" >
                    			  <xsl:attribute name="href">/controller/servlet/Controller?CID=personalAccountForm&amp;newregistration=true&amp;nexturl=<xsl:value-of select="$NEXTURL"/>&amp;database={$DATABASE}</xsl:attribute>
                    			click here</a>
                    			<a CLASS="MedBlackText"> to register.</a>
                    			</td>
                    		</tr>
                    	</xsl:if>
                    	<tr><td valign="top" height="4" colspan="2" ><img src="/static/images/spacer.gif" border="0" height="4"/></td></tr>
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

<!-- This Javascript is used to give focus to the first text box in the form when loading the page -->
<xsl:text disable-output-escaping="yes">
		<![CDATA[
			<xsl:comment>
				<SCRIPT LANGUAGE="JavaScript">
						document.personallogin.email.focus();
				</SCRIPT>
			</xsl:comment>
		]]>
</xsl:text>
<!-- -->
</body>
</html>
</xsl:when>
<xsl:otherwise>
<!-- display confirmation message to the user-->
<html>
<head>
	<title>Password Request</title>
  	<SCRIPT LANGUAGE="Javascript" SRC="/static/js/StylesheetLinks.js"/>
</head>

<body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">
<!-- Table for the logo area -->
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

<table border="0" width="99%" cellspacing="0" cellpadding="0" bgcolor="#C3C8D1">
	<tr >
	<td height="24" bgcolor="#C3C8D1" ><img src="/static/images/s.gif" /></td>
	</tr>
</table>
</center>

<!-- Table below the logo area -->
<center>
  <table border="0" width="99%" cellspacing="0" cellpadding="0">
	<tr><td valign="top" height="20"><img src="/static/images/spacer.gif" border="0" height="20"/></td></tr>
  	<tr><td valign="top" colspan="2"><a class="EvHeaderText">Password Request</a></td></tr>
	<tr><td valign="top" height="10"><img src="/static/images/spacer.gif" border="0" height="10"/></td></tr>
  	<tr><td valign="top"><a CLASS="MedBlackText">Your password has been sent to your email address.</a></td></tr>

    <xsl:choose>
      <xsl:when test="($BACKURL != '')">
        <tr><td valign="top"><a class="MedBlackText">Return to </a><a CLASS="LgBlueLink"><xsl:attribute name="href">/controller/servlet/Controller?<xsl:value-of select="java:decode(BACKURL)"/></xsl:attribute>previous</a><a class="MedBlackText"> page.</a></td></tr>
        <tr><td valign="top"><a class="MedBlackText">Return to </a><a CLASS="LgBlueLink"><xsl:attribute name="href">/controller/servlet/Controller?CID=personalLoginForm&amp;searchid=<xsl:value-of select="$SEARCH-ID"/>&amp;count=<xsl:value-of select="$COUNT"/>&amp;searchtype=<xsl:value-of select="$SEARCH-TYPE"/>&amp;displaylogin=true&amp;database=<xsl:value-of select="$DATABASE"/>&amp;backurl=<xsl:value-of select="$BACKURL"/>&amp;nexturl=<xsl:value-of select="$NEXTURL"/></xsl:attribute>login</a><a class="MedBlackText"> page.</a></td></tr>
      </xsl:when>
      <xsl:otherwise>
      	<tr><td valign="top"><a class="MedBlackText">Return to </a><a CLASS="LgBlueLink"><xsl:attribute name="href">/controller/servlet/Controller?CID=personalLoginForm&amp;searchid=<xsl:value-of select="$SEARCH-ID"/>&amp;count=<xsl:value-of select="$COUNT"/>&amp;searchtype=<xsl:value-of select="$SEARCH-TYPE"/>&amp;displaylogin=true&amp;database=<xsl:value-of select="$DATABASE"/>&amp;nexturl=<xsl:value-of select="$NEXTURL"/></xsl:attribute>login</a><a class="MedBlackText"> page.</a></td></tr>
      </xsl:otherwise>
    </xsl:choose>

  </table>
</center>

<!-- Insert the Footer table -->
<xsl:apply-templates select="FOOTER">
	<xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
	<xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
</xsl:apply-templates>

</body>
</html>
</xsl:otherwise>
</xsl:choose>

</xsl:template>
</xsl:stylesheet>
