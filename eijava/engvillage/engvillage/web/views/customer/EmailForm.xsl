<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:html="http://www.w3.org/TR/REC-html40"
	exclude-result-prefixes="xsl html"
>

<xsl:output method="html"/>
<xsl:include href="Footer.xsl" />

<xsl:template match="PAGE">
	<xsl:variable name="SESSION-ID">
		<xsl:value-of select="SESSION-ID"/>
	</xsl:variable>
	<xsl:variable name="DISPLAY-FORMAT">
		<xsl:value-of select="DISPLAY-FORMAT"/>
	</xsl:variable>
	<xsl:variable name="ERROR-PAGE">
		<xsl:value-of select="ERROR-PAGE"/>
	</xsl:variable>

	<xsl:variable name="SAVED-RECORDS">
		<xsl:choose>
			<xsl:when test="string(SAVED-RECORDS)">
				<xsl:value-of select="SAVED-RECORDS"/>
			</xsl:when>
			<xsl:otherwise>false</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>

	<html>
	<head>

	<title>E-mail Selected Records</title>
  <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js"/>
	<xsl:text disable-output-escaping="yes">
	<![CDATA[
		<xsl:comment>
			<SCRIPT LANGUAGE="JavaScript">

			// This function basically validates the data entered in Email form fields.
			// typically it checks for a valid mail id and not null conditions
			function validateForm(sendemail) {

				var milli = (new Date()).getTime() ;

				sendemail.from.value = sendemail.from.value.replace(/\s+/g,"");
				sendemail.to.value = sendemail.to.value.replace(/\s+/g,"");

				var from = sendemail.from.value;
				var to = sendemail.to.value;

				var subject = sendemail.subject.value;
				var message = sendemail.message.value;
				var sessionid = sendemail.sessionid.value;
				var folder_id = sendemail.folder_id.value;

				if (to.length == 0) {
				   window.alert("You must enter the recipients email address");
				   sendemail.to.focus();
				   return false;
				}
				var booleanTo=validateToRecipients(to);
				if(booleanTo==false){
					alert ("Invalid Email address");
					sendemail.to.focus();
					return false;
        }

				if (from.length == 0) {
					alert("You must enter the Sender email address");
					sendemail.from.focus();
					return false;
				}
				var booleanFrom=validateEmail(from);
				if(booleanFrom==false){
					alert ("Invalid Email address");
					sendemail.from.focus();
					return false;
				}

				if (subject.length == 0) {
					alert("Subject Field cannot be blank");
					sendemail.subject.focus();
					return (false);
				}

				var database = sendemail.database.value;
				var displayformat = sendemail.displayformat.value;
				var selectedset = sendemail.selectedset.value;
				var searchquery = sendemail.searchquery.value;

		   	var hiddensize = sendemail.elements.length;
		   	var docidstring = "&docidlist=";
		   	var handlestring = "&handlelist=";
		   	var dbstring = "&dblist=";
				// logic to construct docidList,HandleList and databaseList
			   	for(var i=0 ; i < hiddensize ; i++)
			   	{
					var nameOfElement = sendemail.elements[i].name;
					var valueOfElement = sendemail.elements[i].value;

					if((nameOfElement.search(/DOC-ID/)!=-1) && (valueOfElement != ""))
					{
						var subdocstring = valueOfElement+",";
						docidstring +=subdocstring;
					}
					if((nameOfElement.search(/HANDLE/)!=-1) && (valueOfElement != ""))
					{
						var subhandlestring = valueOfElement+",";
						handlestring +=subhandlestring;
					}
					if((nameOfElement.search(/DATABASE/)!=-1) && (valueOfElement != ""))
					{
						var subdbstring = valueOfElement+",";
						dbstring +=subdbstring;
					}
				}

				if(typeof(sendemail.docidlist) != 'undefined') {

					url = "/controller/servlet/Controller?EISESSION="+sessionid+
					"&CID=emailSelectedRecords"+
					"&displayformat="+displayformat+
					"&timestamp="+milli+
					"&docidlist="+sendemail.docidlist.value+
					"&handlelist="+sendemail.handlelist.value;
				}
				else {

					if(sendemail.saved_records.value != "true") {
						url = "/controller/servlet/Controller?EISESSION="+sessionid+
						"&CID=emailSelectedSet"+
						"&displayformat="+displayformat+
						"&timestamp="+milli;
					} else {
						url = "/controller/servlet/Controller?EISESSION="+sessionid+
						"&CID=emailSavedRecordsOfFolder"+
						"&displayformat="+displayformat+
						"&folderid="+folder_id+
						"&timestamp="+milli;
					}
				}
				//url = url + "&to="+sendemail.to.value;
				//url = url + "&from="+sendemail.from.value;
				//url = url + "&subject="+sendemail.subject.value;
				//url = url + "&message="+sendemail.message.value;
				sendemail.action = url;

				//window.open(url);
				//window.close();

				return (true);

			} // end function - validateForm

			// This function basically validates the data entered in TO Field for Multiple receipients
			function validateToRecipients(toEmail)
			{
        var result = true;
        receipients = toEmail.split(/,/);
        for(var i=0; i < receipients.length; i++)
        {
          result = result || validateEmail(email);
				}
				return result;
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
		<!-- Start of logo table -->
		<center>
			<table border="0" width="99%" cellspacing="0" cellpadding="0">
				<tr>
					<td valign="top"><img src="/engresources/images/ev2logo5.gif" border="0"/></td>
					<td valign="middle" align="right"><a href="javascript:window.close();"><img src="/engresources/images/close.gif" border="0"/></a></td>
				</tr>
				<tr>
					<td valign="top" height="5" colspan="2"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td>
				</tr>
				<tr>
					<td valign="top" height="2" bgcolor="#3173B5" colspan="2"><img src="/engresources/images/spacer.gif" height="2" border="0"/></td>
				</tr>
				<tr>
					<td valign="top" height="10" colspan="2"><img src="/engresources/images/spacer.gif" border="0" height="10"/></td>
				</tr>
			</table>
		</center>
		<!-- End of logo table -->
	<xsl:choose>
		<xsl:when test="$ERROR-PAGE='true'">
			<table border="0" width="100%" cellspacing="0" cellpadding="0" bgcolor="#FFFFFF">
			<tr><td valign="top" height="15" colspan="3"><img src="/engresources/images/spacer.gif" border="0"/></td></tr>
			<tr>
			<td valign="top" width="20"><img src="/engresources/images/spacer.gif" border="0" width="20"/></td>
			<td valign="top" colspan="2"><a class="EvHeaderText">Email Selected Records</a></td>
			</tr>
			<tr><td valign="top" height="2" colspan="3"><img src="/engresources/images/spacer.gif" border="0"/></td></tr>
			<tr>
			<td valign="top" width="20"><img src="/engresources/images/spacer.gif" border="0" width="20"/></td>
			<td valign="top">
			<A CLASS="MedBlackText">
			You did not select any records to email. Please select records from the search results and try again.
			</A>
			</td>
			<td valign="top" width="10"><img src="/engresources/images/spacer.gif" border="0" width="10"/></td>
			</tr>
			</table>
		</xsl:when>

		<xsl:otherwise>
			<!-- Start of the lower area below the navigation bar -->
			<table border="0" width="100%" cellspacing="0" cellpadding="0" bgcolor="#FFFFFF">


				<tr>
					<td valign="top" height="15" colspan="3"><img src="/engresources/images/spacer.gif" border="0"/></td></tr>
				<tr>
					<td valign="top" width="20"><img src="/engresources/images/spacer.gif" border="0" width="20"/></td>
					<td valign="top" colspan="2"><a class="EvHeaderText">Email Selected Records</a></td>
				</tr>
				<tr>
					<td valign="top" width="20"><img src="/engresources/images/spacer.gif" border="0" width="20"/></td>
					<td valign="top"><A CLASS="MedBlackText">Enter the e-mail address where you would like to have your results sent.</A></td>
					<td valign="top" width="10"><img src="/engresources/images/spacer.gif" border="0" width="10"/></td>
				</tr>

				<tr>
					<td valign="top" height="20" colspan="3"><img src="/engresources/images/spacer.gif" border="0" height="20"/></td>
				</tr>
				<tr>
					<td valign="top" colspan="3">

					<!-- table for outer lines for sendemail form -->
					<table border="0" width="65%" cellspacing="0" cellpadding="0" align="center">
					<tr>
						<td valign="top" width="100%" cellspacing="0" cellpadding="0" height="2" bgcolor="#3173B5" colspan="4">
						<img src="/engresources/images/spacer.gif" border="0" height="2"/></td>
					</tr>
					<tr>
						<td valign="top" width="2" bgcolor="#3173B5"><img src="/engresources/images/spacer.gif" border="0" width="2"/></td>
						<td valign="top" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td>
						<td valign="top">

			<FORM name="sendemail" METHOD="POST" onSubmit="return validateForm(document.sendemail);">


				<xsl:if test="string(HANDLES)">
					<input type="hidden" name="handlelist" value="{HANDLES}"/>
				</xsl:if>
				<xsl:if test="count(DOCID-LIST/DOC) = 0">
					<input type="hidden" name="docidlist" value="{DOCID-LIST}"/>
				</xsl:if>

				<input type="hidden" name="saved_records" value="{SAVED-RECORDS}"/>
				<input type="hidden" name="folder_id" value="{FOLDER-ID}"/>

				<input type="hidden" name="displayformat">
					<xsl:attribute name="value">
					   <xsl:value-of select="DISPLAY-FORMAT"/>
					</xsl:attribute>
				</input>
				<input type="hidden" name="sessionid">
					<xsl:attribute name="value">
					   <xsl:value-of select="SESSION-ID"/>
				  	</xsl:attribute>
				</input>
				<input type="hidden" name="selectedset">
					<xsl:attribute name="value">
					   <xsl:value-of select="SELECTED-SET"/>
				  	</xsl:attribute>
				</input>

			<input type="hidden" name="database">
				<xsl:attribute name="value">
				   <xsl:value-of select="DATABASE"/>
				</xsl:attribute>
			</input>



			<input type="hidden" name="searchquery">
				<xsl:attribute name="value">
				   <xsl:value-of select="SEARCH-QUERY"/>
				</xsl:attribute>
			</input>
			<!-- Looping for Constrcuting Docid's -->
			<xsl:for-each select="DOCIDS/DOC">
				<input type="hidden" name="DOC-ID">
				  <xsl:attribute name="value">
				    <xsl:value-of select="DOCID"/>
				  </xsl:attribute>
				</input>
				<input type="hidden" name="HANDLE">
				  <xsl:attribute name="value">
				    <xsl:value-of select="HITINDEX"/>
				  </xsl:attribute>
				</input>
				<input type="hidden" name="DATABASE">
				  <xsl:attribute name="value">
				    <xsl:value-of select="DATABASE"/>
				  </xsl:attribute>
				</input>
			</xsl:for-each>

				<table border="0" width="300" cellspacing="0" cellpadding="0">
					<tr><td valign="top" height="15" colspan="3"><img src="/engresources/images/spacer.gif" border="0" height="15"/></td></tr>
					<tr><td valign="top" align="right"><A CLASS="MedBlackText">To: </A></td><td valign="top" width="6">
						<img src="/engresources/images/spacer.gif" border="0" width="6"/></td><td valign="top"><A CLASS="MedBlackText">
						<input type="text" name="to" size="25"/>
						</A>
						</td>
					</tr>
					<tr><td valign="top" height="5" colspan="3"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
					<tr><td valign="top" align="right"><A CLASS="MedBlackText">Your Email: </A></td><td valign="top" width="6">
						<img src="/engresources/images/spacer.gif" border="0" width="6"/></td><td valign="top"><A CLASS="MedBlackText">

						<input type="text" name="from" size="25">
							<xsl:attribute name="value">
							<xsl:value-of select="EMAIL-FROM"/>
							</xsl:attribute>
						</input>
						</A>
						</td>
					</tr>
					<tr><td valign="top" height="5" colspan="3"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
					<tr><td valign="top" align="right"><A CLASS="MedBlackText">Subject: </A></td><td valign="top" width="6">
						<img src="/engresources/images/spacer.gif" border="0" width="6"/></td><td valign="top"><A CLASS="MedBlackText">

						<input type="text" name="subject" size="25">
							<xsl:attribute name="value">
								<xsl:value-of select="EMAIL-SUBJECT" disable-output-escaping="yes"/><xsl:value-of select="SEARCH-QUERY" disable-output-escaping="yes"/></xsl:attribute>
						</input>

						</A>
						</td>
					</tr>
					<tr><td valign="top" height="5" colspan="3"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
					<tr><td valign="top" align="right"><A CLASS="MedBlackText">Message: </A></td><td valign="top" width="6">
						<img src="/engresources/images/spacer.gif" border="0" width="6"/></td><td valign="top"><A CLASS="MedBlackText">
						<TEXTAREA ROWS="3" COLS="20" WRAP="PHYSICAL" NAME="message"></TEXTAREA>
						</A>
						</td>
					</tr>
					<tr><td valign="top" height="15"><img src="/engresources/images/spacer.gif" border="0" height="15"/></td></tr>
					<tr><td valign="top" colspan="2">&#160; </td><td valign="top">
						<A CLASS="SmBlackText"><input type="submit" name="submit" value="Send E-mail" /></A>
						</td>
					</tr>
				</table>
			</FORM>
						</td>
						<td valign="top" width="2" bgcolor="#3173B5"><img src="/engresources/images/spacer.gif" border="0" width="2"/></td>
					</tr>
					<tr>
						<td valign="top" width="100%" cellspacing="0" cellpadding="0" height="2" bgcolor="#3173B5" colspan="4">
						<img src="/engresources/images/spacer.gif" border="0" height="2"/></td>
					</tr>
					</table>
				<!-- end of table for outer lines for sendemail form -->
				</td>
				</tr>
				<!-- end of the lower area below the navigation bar -->
			</table>
		</xsl:otherwise>
	</xsl:choose>

	<br/>

<!-- INCLUDE FOOTER  -->
	<xsl:call-template name="FOOTER"/>

	</body>
	</html>

</xsl:template>

</xsl:stylesheet>

