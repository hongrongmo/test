<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>'>
]>
<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:html="http://www.w3.org/TR/REC-html40"
  exclude-result-prefixes="xsl html"
>

<xsl:output method="html"/>
<xsl:include href="Footer.xsl" />
<xsl:include href="./HeaderNull.xsl"/>

<xsl:template match="PAGE">

  <xsl:variable name="SESSION-ID">
    <xsl:value-of select="SESSION-ID"/>
  </xsl:variable>
  <xsl:variable name="ACTION">
    <xsl:value-of select="ACTION"/>
  </xsl:variable>
  <xsl:variable name="DATABASE">
    <xsl:value-of select="DATABASE"/>
  </xsl:variable>
  <xsl:variable name="SHARE">
    <xsl:value-of select="SHARE"/>
  </xsl:variable>

  <html>
  <head>

  <title><xsl:value-of select="SECTION"/> Email</title>
  <link rel="stylesheet" type="text/css" media="all" href="/engresources/stylesheets/ev_common_sciverse_v01.css"/>
  <SCRIPT LANGUAGE="Javascript" SRC="/static/js/StylesheetLinks.js"/>
  <xsl:text disable-output-escaping="yes">
  <![CDATA[
    <xsl:comment>
      <SCRIPT LANGUAGE="JavaScript">

      // This function basically validates the data entered in Email form fields.
      // typically it checks for a valid mail id and not null conditions
      function validateForm(sendemail) {

        sendemail.from_email.value = sendemail.from_email.value.replace(/\s+/g,"");

        var from_name = sendemail.from_name.value;
        var institution = sendemail.institution.value;
        var from_email = sendemail.from_email.value.replace(/\s+/g,"");
        var message = sendemail.message.value;

        var sessionid = sendemail.sessionid.value;

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

        url = "/controller/servlet/Controller?EISESSION="+sessionid+"&CID=askanexpert"
        sendemail.action = url;

        return (true);
      }

      // This function basically does email validation(ie @ . and special characters)
      function validateEmail(email)
      {
        var splitted = email.match("^(.+)@(.+)$");
        if(splitted == null) return false;
        if(splitted[1] != null )
        {
          var regexp_user=/^\"?[\w-_\.]*\"?$/;
          if(splitted[1].match(regexp_user) == null) return false;
        }
        if(splitted[2] != null)
        {
          var regexp_domain=/^[\w-\.]*\.[A-Za-z]{2,4}$/;
          if(splitted[2].match(regexp_domain) == null)
          {
            var regexp_ip =/^\[\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}\]$/;
            if(splitted[2].match(regexp_ip) == null) return false;
          }
          return true;
        }
        return false;
      }
      </SCRIPT>
    </xsl:comment>
  ]]>
  </xsl:text>

</head>

  <body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">
	<!-- HEADER -->
	<xsl:call-template name="HEADERNULL"/>

    <xsl:choose>
      <xsl:when test="$ACTION='confirm'">
        <center>
          <table border="0" width="99%" cellspacing="0" cellpadding="0" bgcolor="#FFFFFF">
            <tr>
                <td valign="top" height="20"><img src="/static/images/s.gif" border="0" height="20"/></td>
            </tr>
            <tr><td valign="top"><a class="EvHeaderText">Thank you</a></td></tr>
            <tr>
              <td valign="top">
                <P><A CLASS="MedBlackText">Somone will contact you shortly.</A></P>
              </td>
            </tr>
          </table>
        </center>
      </xsl:when>
      <xsl:when test="$ACTION='error'">
        <center>
          <table border="0" width="99%" cellspacing="0" cellpadding="0" bgcolor="#FFFFFF">
            <tr>
                <td valign="top" height="20"><img src="/static/images/s.gif" border="0" height="20"/></td>
            </tr>
            <tr><td valign="top"><a class="EvHeaderText">An Error has occured</a></td></tr>
          </table>
        </center>
      </xsl:when>
      <xsl:when test="$ACTION='compose'">
        <!-- Start of the lower area below the navigation bar -->
        <table border="0" width="100%" cellspacing="0" cellpadding="0" bgcolor="#FFFFFF">
          <tr>
            <td valign="top" height="15" colspan="3"><img src="/static/images/s.gif" border="0"/></td></tr>
          <tr>
            <td valign="top" width="20"><img src="/static/images/s.gif" border="0" width="20"/></td>
            <td valign="top" colspan="2"><a class="EvHeaderText"><xsl:value-of select="SECTION"/> <xsl:if test="DISCIPLINE != ''">- <xsl:value-of select="DISCIPLINE"/></xsl:if></a></td>
          </tr>
          <tr>
            <td valign="top" width="20"><img src="/static/images/s.gif" border="0" width="20"/></td>
            <td valign="top"><A CLASS="MedBlackText">Please fill in the information.  All fields are required.</A></td>
            <td valign="top" width="10"><img src="/static/images/s.gif" border="0" width="10"/></td>
          </tr>
          <tr>
            <td valign="top" height="20" colspan="3"><img src="/static/images/s.gif" border="0" height="20"/></td>
          </tr>
          <tr>
            <td valign="top" colspan="3">

              <!-- table for outer lines for sendemail form -->
              <table border="0" width="65%" cellspacing="0" cellpadding="0" align="center">
                <tr>
                  <td valign="top" height="2" bgcolor="#3173B5" colspan="3">
                  <img src="/static/images/s.gif" border="0" height="2"/></td>
                </tr>
                <tr>
                  <td valign="top" width="2" bgcolor="#3173B5"><img src="/static/images/s.gif" border="0" width="2"/></td>
                  <td valign="top">

                    <FORM name="sendemail" METHOD="POST" onSubmit="return validateForm(document.sendemail);">

                      <xsl:if test="GURU">
                        <input type="hidden" name="guru">
                          <xsl:attribute name="value">
                             <xsl:value-of select="GURU"/>
                            </xsl:attribute>
                        </input>
                      </xsl:if>

                      <input type="hidden" name="sessionid">
                        <xsl:attribute name="value">
                           <xsl:value-of select="$SESSION-ID"/>
                          </xsl:attribute>
                      </input>


                      <input type="hidden" name="database">
                        <xsl:attribute name="value">
                           <xsl:value-of select="$DATABASE"/>
                        </xsl:attribute>
                      </input>

                      <!-- these are pulled straight from the DOCUMENT - no variables needed/used -->
                      <input type="hidden" name="discipline">
                        <xsl:attribute name="value">
                           <xsl:value-of select="DISCIPLINE"/>
                        </xsl:attribute>
                      </input>
                      <input type="hidden" name="disciplineid">
                        <xsl:attribute name="value">
                           <xsl:value-of select="DISCIPLINEID"/>
                        </xsl:attribute>
                      </input>
                      <input type="hidden" name="section">
                        <xsl:attribute name="value">
                           <xsl:value-of select="SECTION"/>
                        </xsl:attribute>
                      </input>
                      <input type="hidden" name="sectionid">
                        <xsl:attribute name="value">
                           <xsl:value-of select="SECTIONID"/>
                        </xsl:attribute>
                      </input>

                      <input type="hidden" name="action" value="send"/>

                      <table border="0" width="200" cellspacing="0" cellpadding="0">
                        <tr><td valign="top" height="15" colspan="3"><img src="/static/images/s.gif" border="0" height="15"/></td></tr>
                        <tr><td valign="top" align="right">&nbsp;</td><td valign="top" width="6">
                          <img src="/static/images/s.gif" border="0" width="6"/></td><td valign="top">
                            <A CLASS="MedBlackText">Name:</A>
                          </td>
                        </tr>
                        <tr><td valign="top" align="right">&nbsp;</td><td valign="top" width="6">
                          <img src="/static/images/s.gif" border="0" width="6"/></td><td valign="top">
                            <A CLASS="MedBlackText">
                              <input type="text" name="from_name" size="25">
                              </input>
                            </A>
                          </td>
                        </tr>
                        <tr><td valign="top" height="15" colspan="3"><img src="/static/images/s.gif" border="0" height="15"/></td></tr>
                        <tr><td valign="top" align="right">&nbsp;</td><td valign="top" width="6">
                          <img src="/static/images/s.gif" border="0" width="6"/></td><td valign="top">
                              <A CLASS="MedBlackText">Institution:</A>
                          </td>
                        </tr>
                        <tr><td valign="top" align="right">&nbsp;</td><td valign="top" width="6">
                          <img src="/static/images/s.gif" border="0" width="6"/></td><td valign="top">
                            <A CLASS="MedBlackText">
                              <input type="text" name="institution" size="25">
                              </input>
                            </A>
                          </td>
                        </tr>
                        <tr><td valign="top" height="5" colspan="3"><img src="/static/images/s.gif" border="0" height="5"/></td></tr>
                        <tr><td valign="top" align="right">&nbsp;</td><td valign="top" width="6">
                          <img src="/static/images/s.gif" border="0" width="6"/></td><td valign="top">
                              <A CLASS="MedBlackText">Email:</A>
                          </td>
                        </tr>
                        <tr><td valign="top" align="right">&nbsp;</td><td valign="top" width="6">
                          <img src="/static/images/s.gif" border="0" width="6"/></td><td valign="top"><A CLASS="MedBlackText">
                          <input type="text" name="from_email" size="25">
                            <xsl:attribute name="value">
                            <xsl:value-of select="EMAIL-FROM"/>
                            </xsl:attribute>
                          </input>
                          </A>
                          </td>
                        </tr>
                        <tr><td valign="top" height="5" colspan="3"><img src="/static/images/s.gif" border="0" height="5"/></td></tr>
                        <tr><td valign="top" align="right">&nbsp;</td><td valign="top" width="6">
                          <img src="/static/images/s.gif" border="0" width="6"/></td><td valign="top">
                              <A CLASS="MedBlackText">Question:</A>
                          </td>
                        </tr>
                        <tr><td valign="top" align="right">&nbsp;</td><td valign="top" width="6">
                          <img src="/static/images/s.gif" border="0" width="6"/></td><td valign="top"><A CLASS="MedBlackText">
                          <TEXTAREA ROWS="6" COLS="30" WRAP="PHYSICAL" NAME="message"></TEXTAREA>
                          </A>
                          </td>
                        </tr>
                        <xsl:if test="$SHARE='true'">
                          <tr><td valign="top" align="right">&nbsp;</td><td valign="top" width="6">
                            <img src="/static/images/s.gif" border="0" width="6"/></td><td valign="top">
                            <input type="checkbox" name="share_question" id="share_checkbox" checked="checked" size="25"/><A CLASS="MedBlackText"><label for="share_checkbox">Anonymously share this question and response with others on Ei.org</label>
                            </A>
                            </td>
                          </tr>
                        </xsl:if>
                        <tr><td valign="top" height="15"><img src="/static/images/s.gif" border="0" height="15"/></td></tr>
                        <tr><td valign="top" colspan="2">&#160; </td><td valign="top">
                          <A CLASS="SmBlackText"><input type="submit" name="submit" value="Send Email" /></A>
                          <br/><br/>
                          </td>
                        </tr>
                      </table>
                    </FORM>
                  </td>
                  <td valign="top" width="2" bgcolor="#3173B5"><img src="/static/images/s.gif" border="0" width="2"/></td>
                </tr>
                <tr>
                  <td valign="top" height="2" bgcolor="#3173B5" colspan="3">
                  <img src="/static/images/s.gif" border="0" height="2"/></td>
                </tr>
              </table>
              <!-- end of table for outer lines for sendemail form -->
            </td>
          </tr>
          <!-- end of the lower area below the navigation bar -->
        </table>
      </xsl:when>
      <xsl:otherwise>
        Huh?
      </xsl:otherwise>
  </xsl:choose>

  <br/>

  <!-- INCLUDE FOOTER  -->
  <xsl:call-template name="FOOTER"/>

  </body>
  </html>

</xsl:template>

</xsl:stylesheet>
