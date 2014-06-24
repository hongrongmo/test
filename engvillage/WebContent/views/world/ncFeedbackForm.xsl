<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet
  	 version="1.0"
  	 xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  	 xmlns:java="http://www.jclark.com/xt/java/java.net.URLEncoder">

<xsl:include href="Footer.xsl"/>
<!-- This xsl renders (XML DATA)ncFeedbackForm.jsp.In this xsl we validate the fields before we send the mail-->
<!-- This page is for a non-customer so we don't include GlobalLinks.-->

<xsl:template match="root">

<html>
<head>
	<title>Customer Feedback</title>
		<SCRIPT LANGUAGE="Javascript" SRC="/static/js/StylesheetLinks.js"/>

<xsl:text disable-output-escaping="yes">

<![CDATA[

<xsl:comment>

<SCRIPT LANGUAGE="JavaScript">

  function validForm(feedback) {
	    var fullname= feedback.fullname.value;
		  var eemail = feedback.email.value;
		  var suggestions= feedback.suggestions.value;
		  var comment= feedback.comment.value;

		  var nameLength= fullname.length;
		  var tempNameLength=checkForSpaces(fullname);
		  if (nameLength == tempNameLength) {
			  window.alert ("You must enter a Username");
			  feedback.fullname.focus();
			  return false;
		  }

		  var booleanFrom=validateEmail(eemail);
		  var fromLength= eemail.length;
		  var tempFromLength=checkForSpaces(eemail);
		  if (fromLength == tempFromLength) {
			  window.alert("You must enter the Sender email address");
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

<body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0" rightmargin="0" leftmargin="0">
<!-- Start of logo table -->
<table border="0" width="99%" cellspacing="0" cellpadding="0">
<tr><td valign="top">
<a href="/controller/servlet/Controller?CID=home">
<img src="/static/images/ev2logo5.gif" border="0"/>
</a>
</td></tr>
<tr><td valign="top" height="5"><img src="/static/images/spacer.gif" border="0" height="5"/></td></tr>
<tr><td valign="top" height="2" bgcolor="#3173B5"><img src="/static/images/spacer.gif" border="0" height="2"/></td></tr>
<tr><td valign="top" height="10"><img src="/static/images/spacer.gif" border="0" height="10"/></td></tr>
</table>
<!-- End of logo table -->


<!-- Start of the lower area below the navigation bar -->
<table border="0" width="100%" cellspacing="0" cellpadding="0" bgcolor="#FFFFFF">
	<tr><td valign="top" height="15" colspan="3"><img src="/static/images/spacer.gif" border="0"/></td></tr>
	<tr><td valign="top" width="20"><img src="/static/images/spacer.gif" border="0" width="20"/></td><td valign="top" colspan="2">
	<a class="EvHeaderText">Feedback</a></td></tr>
	<tr><td valign="top" height="2" colspan="3"><img src="/static/images/spacer.gif" border="0"/></td></tr>
	<tr><td valign="top" width="20"><img src="/static/images/spacer.gif" border="0" width="20"/></td>
	<td valign="top"><A CLASS="MedBlackText">Please use our customer feedback form to send us questions or comments on Engineering Village.
	We welcome your feedback.</A></td><td valign="top" width="10"><img src="/static/images/spacer.gif" border="0" width="10"/></td></tr>
	<tr><td valign="top" height="20" colspan="3"><img src="/static/images/spacer.gif" border="0" height="20"/></td></tr>
	<tr><td valign="top" colspan="3">
	<!-- table for outer lines for feedback form -->
	<table border="0" width="65%" cellspacing="0" cellpadding="0" align="center">
		<tr><td valign="top" width="100%" cellspacing="0" cellpadding="0" height="2" bgcolor="#3173B5" colspan="4">
		<img src="/static/images/spacer.gif" border="0" height="2"/></td></tr>
		<tr><td valign="top" width="2" bgcolor="#3173B5"><img src="/static/images/spacer.gif" border="0" width="2"/></td>
		<td valign="top" width="4"><img src="/static/images/spacer.gif" border="0" width="4"/></td>
		<td valign="top">
			<form name="feedback" method="post" action="/controller/servlet/Controller?CID=ncFeedbackConfirmation" onsubmit="return validForm(feedback)">
				<table border="0" width="100%" cellspacing="0" cellpadding="0">
					<tr><td valign="top" height="15"><img src="/static/images/spacer.gif" border="0" height="15"/></td></tr>
					<tr><td valign="top">
					<A CLASS="MedBlackText"><b>1. Please enter your name and email address:</b><br/>
					Name: &#160; <input type="text" name="fullname" size="24"/></A></td></tr>
					<tr><td valign="top" height="5"><img src="/static/images/spacer.gif" border="0" height="5"/></td></tr>
					<tr><td valign="top">
					<A CLASS="MedBlackText">email: &#160; <input type="text" name="email" size="24"/></A></td></tr>
					<tr><td valign="top" height="15"><img src="/static/images/spacer.gif" border="0" height="15"/></td></tr>
					<tr><td valign="top">
					<A CLASS="MedBlackText"><b>2. Please select the area to comment on:</b><br/>
					<select name="comment">
						<option value="Reference Services">Reference Services</option>
						<option value="Search Assistance">Search Assistance</option>
						<option value="Customer Service">Customer Service</option>
						<option value="Ei Product Information">Ei Product Information</option>
					</select></A>
					</td></tr>
					<tr><td valign="top" height="15"><img src="/static/images/spacer.gif" border="0" height="15"/></td></tr>
					<tr><td valign="top">
					<A CLASS="MedBlackText"><b>Please enter your comments or suggestions:</b><br/>
					<TEXTAREA ROWS="6" COLS="28" WRAP="PHYSICAL" NAME="suggestions"></TEXTAREA></A></td></tr>
					<tr><td valign="top" height="15"><img src="/static/images/spacer.gif" border="0" height="15"/></td></tr>
					<tr><td valign="top"><A CLASS="SmBlackText"><input type="submit" name="submit" value="Submit"/></A>&#160;
						<A CLASS="SmBlackText"><input type="reset" name="reset" value="Reset"/></A></td></tr>
				</table>
			</form>
		</td><td valign="top" width="2" bgcolor="#3173B5"><img src="/static/images/spacer.gif" border="0" width="2"/></td>
		</tr>
		<tr><td valign="top" width="100%" cellspacing="0" cellpadding="0" height="2" bgcolor="#3173B5" colspan="4">
		<img src="/static/images/spacer.gif" border="0" height="2"/></td></tr>
	</table>
	<!-- end of table for outer lines for feedback form -->
	</td></tr>
	<tr><td valign="top" height="25"><img src="/static/images/spacer.gif" border="0" height="25"/></td></tr>
	<tr><td valign="top" width="20"><img src="/static/images/spacer.gif" border="0" width="20"/></td><td valign="top" colspan="2">
	    <a class="LgBlueLink" href="/controller/servlet/Controller?CID=home">Back to Login Page</a>
	</td></tr>
	<!-- end of the lower area below the navigation bar -->
</table>

<xsl:apply-templates select="FOOTER"/>

</body>
</html>

</xsl:template>

</xsl:stylesheet>
