<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp '&#160;'>
]>
<xsl:stylesheet  version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:html="http://www.w3.org/TR/REC-html40"
	xmlns:java="java:java.net.URLEncoder"
	exclude-result-prefixes="java html xsl"
	>

<xsl:output method="html" encoding="UTF-8"/>
<xsl:include href="./Footer.xsl"/>
<xsl:include href="./HeaderNull.xsl"/>

<!--
   This xsl file takes the userid and password and authenticates the user for linda hall oredr request
-->

<!-- end of include -->

<xsl:template match="PAGE">

  <html>
  <head>
  	<title>Edit Email Alert Carbon Copy List</title>
  	<SCRIPT LANGUAGE="Javascript" SRC="/static/js/StylesheetLinks.js"/>
  		<xsl:text disable-output-escaping="yes">
  		<![CDATA[
  			<SCRIPT LANGUAGE="JavaScript">
  			/* Hide content from old browsers */
  			function validForm(cclist) 
        {
            // validate email list here
            // code taken from EmailForm.xsl - used to send selected set
            var result = false;
            result =  validateToRecipients(cclist.list.value);
            if(!result)
            {
              alert("Please enter a comma separated list or single valid email address.");
              cclist.list.focus();
            }
            return result;
  			}

        function deletecclist(cclist)
        {
          document.cclist.action.value="deletecclist";
          document.cclist.list.value="";
          document.cclist.submit();
        }

        function trim(astring)
        {
          if(astring == "")
          {
            return astring;
          }
          astring = astring.replace(/^\s+/,'');
          astring = astring.replace(/\s+$/,'');
        
          return astring;
        }
        
        // This function basically validates the data entered in TO Field for Multiple receipients
        function validateToRecipients(toEmail)
        {
        
          // replace passed pattern matches with blanks
          toEmail = toEmail.replace(/\r\n/g,'');

          var comma=0;
          var email;
          var init=0;
          var result=false
          var keepLooping=true;
          while(keepLooping)
          {
            if(toEmail.length>0)
            {
              // single instance (no comma in value)
              if((toEmail.indexOf(",")==-1)&&(toEmail.length>0))
              {
                toEmail = trim(toEmail);
                result=validateEmail(toEmail);
                keepLooping=false;
                if(result==false)
                {
                  return false;
                }
              }
              else
              {
                comma=toEmail.indexOf(",");
                email=toEmail.substring(0,comma);
                init=comma+1;
                toEmail=toEmail.substring(init,toEmail.length);

                email = trim(email);
                result=validateEmail(email);
                if(result==false)
                {
                  keepLooping=false;
                  return false;
                }
              }
            }
            else
            {
              keepLooping=false;
              return false;
            }
          }
          return true;
        }
        
        // This function basically does email validation(ie @ . and special characters)
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
              if(splitted[2].match(regexp_ip) == null) 
              {
                return false;
              }
            }// if
            return true;
          }
          return false;
        }

  			</SCRIPT>
  		]]>
  		</xsl:text>
  		<link rel="stylesheet" type="text/css" media="all" href="/engresources/stylesheets/ev_txt_v01.css"/>
		<link rel="stylesheet" type="text/css" media="all" href="/engresources/stylesheets/ev_common_sciverse_v01.css"/>
  </head>
  

  <body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0" >
  	<!-- Start of logo table -->
  	<!-- HEADER -->
	<xsl:call-template name="HEADERNULL"/>

  <!-- End of logo table -->
  
  	<center>
  		<table border="0" width="99%" cellspacing="0" cellpadding="0">
  
  			<tr><td valign="top" height="3"><img src="/static/images/spacer.gif" border="0" height="3"/></td></tr>
  
  			<tr><td valign="top" width="250">
  			<table border="0" width="250" cellspacing="0" cellpadding="0">
  
			    <xsl:choose>
            <xsl:when test="string(/PAGE/CONFIRM)">
      				  <tr><td valign="top" height="4" colspan="2"><a CLASS="MedBlackText">Thank you for sharing your email alert with others. We hope they find the information valuable.</a></td></tr>
                <FORM name="cclist">
        					<input type="hidden" name="EISESSION">
        						<xsl:attribute name="value"><xsl:value-of select="/PAGE/SESSION-ID"/></xsl:attribute>
        					</input>
        					<input type="hidden" name="database">
        						<xsl:attribute name="value"><xsl:value-of select="/PAGE/DBMASK"/></xsl:attribute>
        					</input>
        					<input type="hidden" name="savedsearchid">
        						<xsl:attribute name="value"><xsl:value-of select="/PAGE/SAVEDSEARCH-ID"/></xsl:attribute>
        					</input>
                </FORM>

            		<!-- ADDED BY JM -->
                <xsl:choose>
                  <xsl:when test="/PAGE/CONFIRM='editcclist'"> <!-- editied -->
                    <xsl:text disable-output-escaping="yes">
                	  <![CDATA[
                      <script language="javascript">
                        if(typeof(opener) != 'undefined') 
                        {
                          opener.document.images[document.cclist.savedsearchid.value].src="/static/images/ccg.gif";
              	        }
              				</script>
                		]]>
                    </xsl:text>
                  </xsl:when>
                  <xsl:otherwise> <!-- deleted -->
                    <xsl:text disable-output-escaping="yes">
                	  <![CDATA[
                      <script language="javascript">
                        if(typeof(opener) != 'undefined') 
                        {
                          opener.document.images[document.cclist.savedsearchid.value].src="/static/images/cco.gif";
              	        }
              				</script>
                		]]>
                    </xsl:text>
                  </xsl:otherwise>
                </xsl:choose>

            </xsl:when>
            <xsl:otherwise>
  			      <xsl:choose>
                <xsl:when test="string(/PAGE/SESSION-HISTORY/SESSION-DATA/CC_LIST)">
      			      <tr><td colspan="2"><a CLASS="MedBlackText">Edit or Delete the comma separated list of email addresses.</a></td></tr>
                </xsl:when>
                <xsl:otherwise>
      			      <tr><td colspan="2"><a CLASS="MedBlackText">Enter a comma separated list of email addresses.</a></td></tr>
                </xsl:otherwise>
  			      </xsl:choose>
              <tr><td colspan="2" valign="top" height="3"><img src="/static/images/spacer.gif" border="0" height="3"/></td></tr>
      				<FORM name="cclist" method="post" action="/controller/servlet/Controller?CID=editcclist" onsubmit="return validForm(cclist)">
      					<input type="hidden" name="action" value="editcclist"/>
      					<input type="hidden" name="EISESSION">
      						<xsl:attribute name="value"><xsl:value-of select="/PAGE/SESSION-ID"/></xsl:attribute>
      					</input>
      					<input type="hidden" name="database">
      						<xsl:attribute name="value"><xsl:value-of select="/PAGE/DBMASK"/></xsl:attribute>
      					</input>
      					<input type="hidden" name="savedsearchid">
      						<xsl:attribute name="value"><xsl:value-of select="/PAGE/SESSION-HISTORY/SESSION-DATA/SAVEDSEARCH-ID"/></xsl:attribute>
      					</input>

                <tr><td valign="middle">&#160;</td><td valign="top">
                <a CLASS="MedBlackText">
                <textarea type="text" name="list" rows="5" cols="40" maxlength="4000">
                  <xsl:value-of select="/PAGE/SESSION-HISTORY/SESSION-DATA/CC_LIST"/>
                </textarea>
                </a>
                </td>
                </tr>
      			<tr><td valign="top" height="4" colspan="2"><img src="/static/images/spacer.gif" border="0" height="4"/></td></tr>
      			<tr>
      				<td valign="top">&#160; </td><td valign="top">
		                  <input type="submit" name="notsubmit" value="Submit"/>
		                  &#160;&#160;<input type="button" name="cancel" value="Cancel" onclick="javascript:window.close();"/>
		                  &#160;&#160;<input type="button" name="delete" value="Delete" onclick="javascript:deletecclist(this.form);"/>
                  	</td>
                </tr>
      				</FORM>

            		<!-- ADDED BY JM -->
                <xsl:text disable-output-escaping="yes">
            	  <![CDATA[
                  <script language="javascript">
                    if(typeof(document.cclist.list) != 'undefined') {
                      document.cclist.list.focus();
          	        }
          				</script>
            		]]>
                </xsl:text>
  
            </xsl:otherwise>
          </xsl:choose>
  			</table>
  			</td></tr>
  			<tr><td valign="top" height="5"><img src="/static/images/spacer.gif" border="0" height="5"/></td></tr>
  			<tr><td valign="top">&#160;</td></tr>
  		</table>
  	</center>
  
  
  <!-- Insert Footer -->
  	<!-- jam 12/23/2002
  		while fixing bad character in LHL forms
  		changed to use default footer for file
  	-->
  	<!-- Footer -->
  	<xsl:call-template name="FOOTER"/>
  </body>
  </html>
</xsl:template>

</xsl:stylesheet>
