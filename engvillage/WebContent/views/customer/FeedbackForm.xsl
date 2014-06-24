<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:html="http://www.w3.org/TR/REC-html40"
	exclude-result-prefixes="html xsl"
  >
<!-- This xsl renders FeedbackForm.jsp.In this xsl we validate the fields before we send the mail-->
<!-- This page is for a customer so we include Header ,GlobalLinks,Footer.So that user can navigate thru.There is seperate xsl for non customer-->
<xsl:include href="Header.xsl"/>
<xsl:include href="GlobalLinks.xsl"/>
<xsl:include href="Footer.xsl"/>


<xsl:template match="PAGE">

<xsl:variable name="SESSION-ID">
	<xsl:value-of select="SESSION-ID"/>
</xsl:variable>

<xsl:variable name="DBMASK">
	<xsl:value-of select="DBMASK"/>
</xsl:variable>

<html>
<head>
	<title>Customer Feedback</title>
  <SCRIPT LANGUAGE="Javascript" SRC="/static/js/StylesheetLinks.js"/>

<xsl:text disable-output-escaping="yes">

<![CDATA[

<xsl:comment>

  <SCRIPT LANGUAGE="JavaScript">
  // This function basically validates the data entered in feedback form fields.
  // typically it checks for a valid mail id and not null conditions
	function validForm(feedback) 
	{
	  var fullname= feedback.fullname.value;
		var eemail = feedback.email.value;
		var suggestions= feedback.suggestions.value;
		var comment= feedback.comment.value;

	  var nameLength= fullname.length;
	  var tempNameLength=checkForSpaces(fullname);
	  if (nameLength == tempNameLength) {
		  window.alert ("Please enter a name");
		  feedback.fullname.focus();
		  return false;
	  }

	  var booleanFrom=validateEmail(eemail);
	  var fromLength= eemail.length;
	  var tempFromLength=checkForSpaces(eemail);
	  if (fromLength == tempFromLength) {
		  window.alert("You must enter an email address");
		  feedback.email.focus();
		  return false;
		}
	  if(booleanFrom==false){
			alert ("Invalid Email address");
			feedback.email.focus();
			return false;
	  }


	  var commentLength= comment.length;
	  var tempCommentLength=checkForSpaces(comment);
	  if (commentLength == tempCommentLength) {
		  window.alert ("You must select a area of comment");
		  feedback.comment.focus();
		  return false;
	  }

	  var suggestLength= suggestions.length;
	  var tempSuggestLength=checkForSpaces(suggestions);
	  if (suggestLength == tempSuggestLength) {
		  window.alert("You must enter some suggestions");;
		  feedback.suggestions.focus();
		  return false;
	  }
  }

	function checkForSpaces(str){
		var tempword = str;
		var tempLength=0;
		while (tempword.substring(0,1) == ' ') {
		   tempword = tempword.substring(1);
		   tempLength = tempLength + 1;
		}

		return tempLength;
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
		     }// if
		     return true;
		   }
  		return false;
		}
</SCRIPT>
</xsl:comment>

]]>

</xsl:text>

<!-- -->

</head>

<body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">

<!-- Start of logo table -->
<center>

	<!-- Insert the Header -->
  <xsl:apply-templates select="HEADER"/>
	
	<!-- Insert the Global Link table -->
	<xsl:apply-templates select="GLOBAL-LINKS">
		<xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
	 	<xsl:with-param name="SELECTED-DB" select="$DBMASK"/>
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
                <a CLASS="LgWhiteText"><b>Customer Feedback</b></a>
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
  
                  <A CLASS="MedBlackText">Please use our customer feedback form to send us questions or comments on Engineering Village. We welcome your feedback.</A>
	
            			<Form name="feedback" method="post" action="/controller/servlet/Controller?EISESSION=$SESSIONID&amp;CID=FeedbackConfirmation&amp;database={$DBMASK}" onsubmit="return validForm(feedback)">
            				<table border="0" width="100%" cellspacing="0" cellpadding="0">
            					<tr><td valign="top" height="15"><img src="/static/images/s.gif" border="0" height="15"/></td></tr>
            					<tr><td valign="top">
            					<A CLASS="MedBlackText"><b>1. Please enter your name and email address:</b><br/>
            					Name: &#160; <input type="text" name="fullname" size="24"/></A></td></tr>
            					<tr><td valign="top" height="5"><img src="/static/images/s.gif" border="0" height="5"/></td></tr>
            					<tr><td valign="top">
            					<A CLASS="MedBlackText">Email: &#160; <input type="text" name="email" size="24"/></A></td></tr>
            					<tr><td valign="top" height="15"><img src="/static/images/s.gif" border="0" height="15"/></td></tr>
            					<tr><td valign="top">
            					<A CLASS="MedBlackText"><b>2. Please select the area to comment on:</b><br/>
            					<select name="comment">
            						<option value="Reference Services">Reference Services</option>
            						<option value="Search Assistance">Search Assistance</option>
            						<option value="Customer Service">Customer Service</option>
            						<option value="Ei Product Information">Ei Product Information</option>
            					</select></A>
            					</td></tr>
            					<tr><td valign="top" height="15"><img src="/static/images/s.gif" border="0" height="15"/></td></tr>
            					<tr><td valign="top">
            					<A CLASS="MedBlackText"><b>Please enter your comments or suggestions:</b><br/>
            					<TEXTAREA ROWS="6" COLS="80" WRAP="PHYSICAL" NAME="suggestions"></TEXTAREA></A></td></tr>
            					<tr><td valign="top" height="15"><img src="/static/images/s.gif" border="0" height="15"/></td></tr>
            					<tr><td valign="top">
            						<input type="image" src="/static/images/sub.gif" name="submit" value="Submit"/> &#160; &#160;
            						<input type="image" src="/static/images/cancel.gif" name="cancel" value="Cancel" onclick="javascript:document.location='/controller/servlet/Controller?EISESSION=$SESSIONID'; return false;"/> &#160; &#160;
            						<input type="image" src="/static/images/res.gif" name="reset" value="Reset" onclick="javascript:reset(); return false;"/> &#160; &#160;
            					</td></tr>
            				</table>
            			</Form>
              </td>
              <!-- End Content Cel -->
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

	<!-- Insert the Footer -->
  <xsl:apply-templates select="FOOTER">
  	<xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
  	<xsl:with-param name="SELECTED-DB" select="$DBMASK"/>
  </xsl:apply-templates>

  </center>

</body>
</html>
</xsl:template>
</xsl:stylesheet>
