<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:html="http://www.w3.org/TR/REC-html40"
	xmlns:java="java:java.net.URLEncoder"
	exclude-result-prefixes="java html xsl"
	>

<xsl:output method="html" encoding="UTF-8"/>
<xsl:preserve-space elements="xsl text"/>

<xsl:include href="./common/LindaHallResults.xsl"/>
<xsl:include href="./Footer.xsl"/>

<xsl:template match="LINDA-HALL">

		<xsl:variable name="SHIPPING-VALUE">
			<xsl:value-of select="SHIPPING-VALUE"/>
		</xsl:variable>

		<xsl:variable name="SESSION-ID">
			<xsl:value-of select="SESSION-ID"/>
		</xsl:variable>

		<xsl:variable name="DOC-ID">
			<xsl:value-of select="DOC-ID"/>
		</xsl:variable>

		<xsl:variable name="DATABASE">
			<xsl:value-of select="DATABASE"/>
		</xsl:variable>

		<xsl:variable name="DELIVERY-METHOD-NAME">
			<xsl:value-of select="DELIVERY-METHOD-NAME"/>
		</xsl:variable>

		<xsl:variable name="DELIVERY-METHOD-VALUE">
			<xsl:value-of select="DELIVERY-METHOD-VALUE"/>
		</xsl:variable>

		<xsl:variable name="SERVICE-LEVEL">
			<xsl:value-of select="SERVICE-LEVEL"/>
		</xsl:variable>

		<xsl:variable name="ACCOUNT-NUMBER">
			<xsl:value-of select="ACCOUNT-NUMBER"/>
		</xsl:variable>

		<xsl:variable name="ATTENTION">
			<xsl:value-of select="ATTENTION"/>
		</xsl:variable>

		<xsl:variable name="EMAIL-CONFIRM">
			<xsl:value-of select="EMAIL-CONFIRM"/>
		</xsl:variable>

		<xsl:variable name="FIRST-NAME">
			<xsl:value-of select="CONTACT-INFO/FIRST-NAME"/>
		</xsl:variable>

		<xsl:variable name="LAST-NAME">
			<xsl:value-of select="CONTACT-INFO/LAST-NAME"/>
		</xsl:variable>

		<xsl:variable name="COMPANY-NAME">
			<xsl:value-of select="CONTACT-INFO/COMPANY-NAME"/>
		</xsl:variable>

		<xsl:variable name="ADDRESS1">
			<xsl:value-of select="CONTACT-INFO/ADDRESS1"/>
		</xsl:variable>

		<xsl:variable name="ADDRESS2">
			<xsl:value-of select="CONTACT-INFO/ADDRESS2"/>
		</xsl:variable>

		<xsl:variable name="CITY">
			<xsl:value-of select="CONTACT-INFO/CITY"/>
		</xsl:variable>

		<xsl:variable name="STATE">
			<xsl:value-of select="CONTACT-INFO/STATE"/>
		</xsl:variable>

		<xsl:variable name="COUNTRY">
			<xsl:value-of select="CONTACT-INFO/COUNTRY"/>
		</xsl:variable>

		<xsl:variable name="ZIP">
			<xsl:value-of select="CONTACT-INFO/ZIP"/>
		</xsl:variable>

		<xsl:variable name="TELEPHONE">
			<xsl:value-of select="CONTACT-INFO/TELEPHONE"/>
		</xsl:variable>

		<xsl:variable name="FAX">
			<xsl:value-of select="CONTACT-INFO/FAX"/>
		</xsl:variable>

		<xsl:variable name="EMAIL">
			<xsl:value-of select="CONTACT-INFO/EMAIL"/>
		</xsl:variable>

<html>
<head>
	<title>Linda Hall Library Document Request</title>
	<SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js"/>

		<!-- START OF JAVA SCRIPT -->
		<xsl:text disable-output-escaping="yes">
		<![CDATA[
		<xsl:comment>
		<script language="Javascript">
			function validate(sessionId,buttonname){

				// if "edit request" button is pressed submit to CID=lhlEditOrderForm, else sumbit to lhlEmailConfirmation
				if(buttonname=="Edit Request"){
					document.orderform.action='/controller/servlet/Controller?EISESSION='+sessionId+'&CID=lhlEditOrderForm';
					document.orderform.submit();
					return true;
				}
				if(buttonname=="Submit Request"){
					document.orderform.action='/controller/servlet/Controller?EISESSION='+sessionId+'&CID=lhlEmailConfirmation';
					document.orderform.submit();
					return true;
				}
				return false;

			} // validate
		</script>
		</xsl:comment>
		]]>
		</xsl:text>
		<!-- END OF JAVA SCRIPT -->


	</head>
	<body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">
	<!-- Start of logo table -->
	<center>
		<table border="0" width="99%" cellspacing="0" cellpadding="0">
			<tr><td valign="top"><img src="/engresources/images/ev2logo5.gif" border="0"/></td><td valign="middle" align="right"><a href="javascript:window.close();"><img src="/engresources/images/close.gif" border="0"/></a></td></tr>
			<tr><td valign="top" height="5" colspan="2"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
			<tr><td valign="top" height="2" bgcolor="#3173B5" colspan="2"><img src="/engresources/images/spacer.gif" height="2" border="0"/></td></tr>
			<tr><td valign="top" height="10" colspan="2"><img src="/engresources/images/spacer.gif" border="0" height="10"/></td></tr>
		</table>
	</center>
	<!-- End of logo table -->

	<center>
		<form name="orderform" method="post">

			<input type="hidden" name="docid" value="{$DOC-ID}"/><xsl:text>&#xA;</xsl:text>
			<input type="hidden" name="database" value="{$DATABASE}"/><xsl:text>&#xA;</xsl:text>
			<input type="hidden" name="methodname" value="{$DELIVERY-METHOD-NAME}"/><xsl:text>&#xA;</xsl:text>
			<input type="hidden" name="methodvalue" value="{$DELIVERY-METHOD-VALUE}"/><xsl:text>&#xA;</xsl:text>
			<input type="hidden" name="shippingvalue"  value="{$SHIPPING-VALUE}"/><xsl:text>&#xA;</xsl:text>
			<input type="hidden" name="servicelevel" value="{$SERVICE-LEVEL}"/><xsl:text>&#xA;</xsl:text>
			<input type="hidden" name="accountnumber" value="{$ACCOUNT-NUMBER}"/><xsl:text>&#xA;</xsl:text>
			<input type="hidden" name="attention"  value="{$ATTENTION}"/><xsl:text>&#xA;</xsl:text>
			<input type="hidden" name="firstname" value="{$FIRST-NAME}"/><xsl:text>&#xA;</xsl:text>
			<input type="hidden" name="lastname" value="{$LAST-NAME}"/><xsl:text>&#xA;</xsl:text>
			<input type="hidden" name="companyname" value="{$COMPANY-NAME}"/><xsl:text>&#xA;</xsl:text>
			<input type="hidden" name="address1" value="{$ADDRESS1}"/><xsl:text>&#xA;</xsl:text>
			<input type="hidden" name="address2" value="{$ADDRESS2}"/><xsl:text>&#xA;</xsl:text>
			<input type="hidden" name="city" value="{$CITY}"/><xsl:text>&#xA;</xsl:text>
			<input type="hidden" name="state" value="{$STATE}"/><xsl:text>&#xA;</xsl:text>
			<input type="hidden" name="country" value="{$COUNTRY}"/><xsl:text>&#xA;</xsl:text>
			<input type="hidden" name="zip" value="{$ZIP}"/><xsl:text>&#xA;</xsl:text>
			<input type="hidden" name="phone" value="{$TELEPHONE}"/><xsl:text>&#xA;</xsl:text>
			<input type="hidden" name="fax" value="{$FAX}"/><xsl:text>&#xA;</xsl:text>

			<input type="hidden" name="confirmationEmail" value="{$EMAIL-CONFIRM}"/><xsl:text>&#xA;</xsl:text>
			<input type="hidden" name="deliveryEmail"  value="{$DELIVERY-METHOD-VALUE}"/><xsl:text>&#xA;</xsl:text>

			<table border="0" width="99%" cellspacing="0" cellpadding="0">
				<tr><td valign="top" colspan="2"><a class="EvHeaderText">Linda Hall Library Request Confirmation</a></td></tr>
				<tr><td valign="top" height="10"><img src="/engresources/images/spacer.gif" border="0" height="10"/></td></tr>							
				<tr><td valign="top" colspan="2"><a CLASS="MedBlackText">You have selected the following document to order from Linda Hall Library.	You must complete the order form below to submit the request.</a></td></tr>
				<tr><td valign="top" colspan="2" height="10"><img src="/engresources/images/spacer.gif" border="0" height="10"/></td></tr>

				<tr>
					<td valign="top" colspan="3">
							<xsl:apply-templates select="PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT"/>
					</td>
				</tr>
				<tr><td valign="top" colspan="2" height="15"><img src="/engresources/images/spacer.gif" border="0" height="15"/></td></tr>

				<tr><td valign="top">

				<a CLASS="MedBlackText"><b>Delivery method: </b></a></td><td valign="top"><a CLASS="MedBlackText"><xsl:value-of select="$DELIVERY-METHOD-NAME"/>
					<xsl:if test="string($DELIVERY-METHOD-VALUE)">
						(<xsl:value-of select="$DELIVERY-METHOD-VALUE"/>)
					</xsl:if>
				</a>

				</td></tr>
				<tr><td valign="top"><a CLASS="MedBlackText"><b>Service level: </b></a></td><td valign="top"><a CLASS="MedBlackText"><xsl:value-of select="$SERVICE-LEVEL"/></a></td></tr>
				<tr><td valign="top"><a CLASS="MedBlackText"><b>FedEx account: </b></a></td><td valign="top"><a CLASS="MedBlackText"><xsl:value-of select="$ACCOUNT-NUMBER"/></a></td></tr>
				<tr><td valign="top" colspan="2" height="15"><img src="/engresources/images/spacer.gif" border="0" height="15"/></td></tr>
				<tr><td valign="top"><a CLASS="MedBlackText"><b>Attention:</b></a></td><td valign="top"><a CLASS="MedBlackText"><xsl:value-of select="$ATTENTION"/></a></td></tr>
				<tr><td valign="top"><a CLASS="MedBlackText"><b>E-mail confirmation: </b></a></td><td valign="top"><a CLASS="MedBlackText"><xsl:value-of select="$EMAIL-CONFIRM"/></a></td></tr>
				<tr><td valign="top"><a CLASS="MedBlackText"><b>Ship to: </b></a></td><td valign="top"><a CLASS="MedBlackText"><xsl:value-of select="$FIRST-NAME"/><xsl:text> </xsl:text><xsl:value-of select="$LAST-NAME"/><br/>
				<xsl:value-of select="$COMPANY-NAME"/><br/>
				<xsl:value-of select="$ADDRESS1"/><xsl:text> </xsl:text><xsl:value-of select="$ADDRESS2"/><br/>
				<xsl:value-of select="$CITY"/>,<xsl:value-of select="$STATE"/><xsl:text> </xsl:text><xsl:value-of select="$ZIP"/><xsl:text> </xsl:text><xsl:value-of select="$COUNTRY"/><br/>
				<xsl:value-of select="$TELEPHONE"/><br/>
				<xsl:value-of select="$FAX"/><br/>
				</a></td></tr>

				<tr><td valign="top" height="15" colspan="2"><img src="/engresources/images/spacer.gif" border="0" height="15"/></td></tr>
				<tr>
					<td valign="top" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td>
					<td valign="top">
						<a CLASS="MedBlackText">
						<input type="button" name="edit" value="Edit Request" onclick="return validate('$SESSIONID','Edit Request')" />
						<input type="button" name="sendemail" value="Submit Request" onclick="return validate('$SESSIONID','Submit Request')" />
						</a>
					</td>
				</tr>
				<tr><td valign="top" colspan="2" height="20"><img src="/engresources/images/spacer.gif" border="0" height="20"/></td></tr>
			</table>
		</form>
	</center>

	<!-- Footer -->
	<xsl:call-template name="FOOTER"/>

	</body>
</html>

</xsl:template>

</xsl:stylesheet>
