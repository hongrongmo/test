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

		<xsl:variable name="DELIVERY-METHOD-NAME">
			<xsl:choose>
				<xsl:when test='string(LHL-USER-INFO/DELIVERY-METHOD-NAME)'><xsl:value-of select="LHL-USER-INFO/DELIVERY-METHOD-NAME"/></xsl:when>
				<xsl:otherwise>E-mail</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="SERVICE-LEVEL">
			<xsl:choose>
				<xsl:when test='string(LHL-USER-INFO/SERVICE-LEVEL)'><xsl:value-of select="LHL-USER-INFO/SERVICE-LEVEL"/></xsl:when>
				<xsl:otherwise>Regular service</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="ARIEL">
			<xsl:choose>
				<xsl:when test="$DELIVERY-METHOD-NAME='Ariel'"><xsl:value-of select="LHL-USER-INFO/DELIVERY-METHOD-NAME/DELIVERY-METHOD-VALUE"/></xsl:when>
			</xsl:choose>
		</xsl:variable>

		<xsl:variable name="CONTRACT-ID">
			<xsl:value-of select="LHL-USER-INFO/CONTRACT-ID"/>
		</xsl:variable>

		<xsl:variable name="CUSTOMER-ID">
			<xsl:value-of select="LHL-USER-INFO/CUSTOMER-ID"/>
		</xsl:variable>

		<xsl:variable name="SHIPPING-VALUE">
			<xsl:value-of select="LHL-USER-INFO/SHIPPING"/>
		</xsl:variable>

		<xsl:variable name="ATTENTION">
			<xsl:value-of select="LHL-USER-INFO/ATTENTION"/>
		</xsl:variable>

		<xsl:variable name="CREATED-DATE">
			<xsl:value-of select="LHL-USER-INFO/CREATED-DATE"/>
		</xsl:variable>

		<xsl:variable name="FIRST-NAME">
			<xsl:value-of select="LHL-USER-INFO/CONTACT-INFO/FIRST-NAME"/>
		</xsl:variable>

		<xsl:variable name="LAST-NAME">
			<xsl:value-of select="LHL-USER-INFO/CONTACT-INFO/LAST-NAME"/>
		</xsl:variable>

		<xsl:variable name="COMPANY-NAME">
			<xsl:value-of select="LHL-USER-INFO/CONTACT-INFO/COMPANY-NAME"/>
		</xsl:variable>

		<xsl:variable name="ADDRESS1">
			<xsl:value-of select="LHL-USER-INFO/CONTACT-INFO/ADDRESS1"/>
		</xsl:variable>

		<xsl:variable name="ADDRESS2">
			<xsl:value-of select="LHL-USER-INFO/CONTACT-INFO/ADDRESS2"/>
		</xsl:variable>

		<xsl:variable name="CITY">
			<xsl:value-of select="LHL-USER-INFO/CONTACT-INFO/CITY"/>
		</xsl:variable>

		<xsl:variable name="STATE">
			<xsl:value-of select="LHL-USER-INFO/CONTACT-INFO/STATE"/>
		</xsl:variable>

		<xsl:variable name="COUNTRY">
			<xsl:value-of select="LHL-USER-INFO/CONTACT-INFO/COUNTRY"/>
		</xsl:variable>

		<xsl:variable name="ZIP">
			<xsl:value-of select="LHL-USER-INFO/CONTACT-INFO/ZIP"/>
		</xsl:variable>

		<xsl:variable name="TELEPHONE">
			<xsl:value-of select="LHL-USER-INFO/CONTACT-INFO/PHONE"/>
		</xsl:variable>

		<xsl:variable name="FAX">
			<xsl:value-of select="LHL-USER-INFO/CONTACT-INFO/FAX"/>
		</xsl:variable>

		<xsl:variable name="EMAIL">
			<xsl:value-of select="LHL-USER-INFO/CONTACT-INFO/EMAIL"/>
		</xsl:variable>

		<xsl:variable name="ACCOUNT-NUMBER">
			<xsl:choose>
				<xsl:when test='string(LHL-USER-INFO/ACCOUNT-NUMBER)'><xsl:value-of select="LHL-USER-INFO/ACCOUNT-NUMBER"/></xsl:when>
				<xsl:otherwise><xsl:value-of select="LHL-USER-INFO/CONTACT-INFO/ACCOUNT-NUMBER"/></xsl:otherwise>
			</xsl:choose>

		</xsl:variable>

		<xsl:variable name="ISSN">
			<xsl:value-of select="PAGE-RESUTLS/PAGE-ENTRY/EI-DOCUMENT/ISSN"/>
		</xsl:variable>

		<xsl:variable name="CODEN">
			<xsl:value-of select="PAGE-RESUTLS/PAGE-ENTRY/EI-DOCUMENT/CODEN"/>
		</xsl:variable>

		<xsl:variable name="ISBN">
			<xsl:value-of select="PAGE-RESUTLS/PAGE-ENTRY/EI-DOCUMENT/ISBN"/>
		</xsl:variable>

		<!-- variables needed for login -->
		<xsl:variable name="MATCH">
			<xsl:value-of select="MATCH"/>
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

		<html>
		<head>
			<title>Linda Hall Library Document Request</title>
			<SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js"/>
			<SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/LindaHallOrder_V2_1.js"/>
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

		<!-- condition when he is authenticated -->
		<xsl:if test="boolean($MATCH='ORDER-FORM')">

		<FORM name="delivery" method="post" action="/controller/servlet/Controller?CID=lhlOrderFormComplete" onsubmit="return checkdelivery(delivery,'{$SHIPPING-VALUE}')">
		<center>
			<table border="0" width="99%" cellspacing="0" cellpadding="0">
				<tr><td valign="top" colspan="3"><a class="EvHeaderText">Linda Hall Library Document Request</a></td></tr>
				<tr><td valign="top" colspan="3"><a CLASS="MedBlackText">You have selected the following document to order from Linda Hall Library. You must complete the order form below to submit the request.</a></td></tr>
				<tr><td valign="top" colspan="2" height="10"><img src="/engresources/images/spacer.gif" border="0" height="10"/></td></tr>

					<input type="hidden" name="shipping_value">
						<xsl:attribute name="value"><xsl:value-of select="$SHIPPING-VALUE"/></xsl:attribute>
					</input>

					<input type="hidden" name="docid">
						<xsl:attribute name="value"><xsl:value-of select="$DOC-ID"/></xsl:attribute>
					</input>

					<input type="hidden" name="database">
						<xsl:attribute name="value"><xsl:value-of select="$DATABASE"/></xsl:attribute>
					</input>

				<tr>
					<td valign="top" colspan="3">
						<xsl:apply-templates select="PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT"/>
					</td>
				</tr>
				<tr><td valign="top" colspan="3" height="20"><img src="/engresources/images/spacer.gif" border="0" height="20"/></td></tr>
				<tr><td valign="top" colspan="3"><a CLASS="MedBlackText"><b>Document charges</b>
				<br/>Document delivery: </a><br/>
				<li><a CLASS="MedBlackText">First 50 pages:  $12.00 for academic institutions; $16.00 for nonacademic institutions</a></li>
				<li><a CLASS="MedBlackText">Add $0.25 per page for each page over 50.</a></li><br/>
				<a CLASS="MedBlackText">Book loan:</a><br/>
				<li><a CLASS="MedBlackText">$12.00 for academic institutions; $16.00 for nonacademic institutions</a></li>
				<!-- <li><a CLASS="MedBlackText">You must select FedEx delivery.</a></li> -->
				</td></tr>
				<tr><td valign="top" colspan="3" height="15"><img src="/engresources/images/spacer.gif" border="0" height="15"/></td></tr>
				<tr><td valign="top" colspan="3"><a CLASS="MedBlackText"><b>Copyright charges</b></a><br/>
				<a CLASS="MedBlackText">A copyright payment must be made for each request:</a>
				<li><a CLASS="MedBlackText">$7.00 for documents with an actual copyright royalty fee is less than $15.00</a></li>
				<li><a CLASS="MedBlackText">If the actual royalty fee is $15.00 or more, you will be charged the actual royalty fee.</a></li>
				</td></tr>
				<tr><td valign="top" height="20" colspan="2"><img src="/engresources/images/spacer.gif" border="0" height="20"/></td></tr>
			</table>

		</center>

		<table border="0" width="450" cellspacing="1" cellpadding="0" bgcolor="#3173B5">
		<tr>
			<td valign="top">

			<table border="0" width="449" cellspacing="1" cellpadding="0" bgcolor="#FFFFFF">
			<tr><td valign="top">

			<table border="0" width="100%" cellspacing="0" cellpadding="0">
				<tr><td valign="top" height="15" bgcolor="#3173b5" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" bgcolor="#3173B5" width="168"><a CLASS="LgWhiteText"><b>Delivery method (choose one)</b></a></td><td valign="top" bgcolor="#3173B5"><img src="/engresources/images/spacer.gif" border="0" height="1"/></td><td valign="top" width="68" bgcolor="#3173B5"><a CLASS="LgWhiteText">Cost</a></td></tr>
				<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><a CLASS="MedBlackText"><b>Electronic delivery:</b></a></td></tr>
				<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2">


				<input type="radio" name="method" value="E-mail">
				<xsl:if test="boolean($DELIVERY-METHOD-NAME='E-mail')">
					<xsl:attribute name="checked">checked</xsl:attribute>
				</xsl:if>
				</input>

				<a CLASS="MedBlackText">E-mail - address required
				<input type="text" name="deliveryEmail" size="15" onclick="document.delivery.method[0].checked=true" >
				<xsl:attribute name="value"><xsl:value-of select="$EMAIL"/></xsl:attribute>
				</input>
				</a>

				</td><td valign="top"><a CLASS="MedBlackText">No charge</a></td></tr>


				<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2">

				<input type="radio" name="method" value="Ariel">
				<xsl:if test="boolean($DELIVERY-METHOD-NAME='Ariel')">
					<xsl:attribute name="checked">checked</xsl:attribute>
				</xsl:if>
				</input>

				<a CLASS="MedBlackText">Ariel - address required
				<input type="text" name="ariel" size="15" onclick="document.delivery.method[1].checked=true">
				<xsl:attribute name="value"><xsl:value-of select="$ARIEL"/></xsl:attribute>
				</input>
				</a>
				</td><td valign="top"><a CLASS="MedBlackText">No charge</a></td></tr>

				<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>

				<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
				<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><a CLASS="MedBlackText"><b>North America delivery:</b></a></td></tr>
				<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2">

				<input type="radio" name="method" value="Fax">
				<xsl:if test="boolean($DELIVERY-METHOD-NAME='Fax')">
					<xsl:attribute name="checked">checked</xsl:attribute>
				</xsl:if>
				</input>
				<a CLASS="MedBlackText">Fax</a></td><td valign="top"><a CLASS="MedBlackText">No charge</a></td></tr>
				<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2">

				<input type="radio" name="method" value="First class mail">
				<xsl:if test="boolean($DELIVERY-METHOD-NAME='First class mail')">
					<xsl:attribute name="checked">checked</xsl:attribute>
				</xsl:if>
				</input>
				<a CLASS="MedBlackText">First class mail</a></td><td valign="top"><a CLASS="MedBlackText">$3.00</a></td></tr>
				<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2">

				<input type="radio" name="method" value="FedEx 2-Day">
				<xsl:if test="boolean($DELIVERY-METHOD-NAME='FedEx 2-Day')">
					<xsl:attribute name="checked">checked</xsl:attribute>
				</xsl:if>
				</input>
				<a CLASS="MedBlackText">FedEx 2-Day (No charge if your FedEx account is used)</a></td><td valign="top"><a CLASS="MedBlackText">$4.00</a></td></tr>
				<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2">

				<input type="radio" name="method" value="FedEx Overnight">
				<xsl:if test="boolean($DELIVERY-METHOD-NAME='FedEx Overnight')">
					<xsl:attribute name="checked">checked</xsl:attribute>
				</xsl:if>
				</input>
				<a CLASS="MedBlackText">FedEx Overnight (No charge if your FedEx account is used)</a></td><td valign="top"><a CLASS="MedBlackText">$5.00</a></td></tr>
				<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>

				<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
				<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><a CLASS="MedBlackText"><b>International delivery:</b></a></td></tr>
				<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2">

				<input type="radio" name="method" value="International fax">
				<xsl:if test="boolean($DELIVERY-METHOD-NAME='International fax')">
					<xsl:attribute name="checked">checked</xsl:attribute>
				</xsl:if>
				</input>
				<a CLASS="MedBlackText">International fax</a></td><td valign="top"><a CLASS="MedBlackText">$5.00</a></td></tr>
				<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2">

				<input type="radio" name="method" value="Air mail">
				<xsl:if test="boolean($DELIVERY-METHOD-NAME='Air mail')">
					<xsl:attribute name="checked">checked</xsl:attribute>
				</xsl:if>
				</input>
				<a CLASS="MedBlackText">Air mail</a></td><td valign="top"><a CLASS="MedBlackText">$3.00</a></td></tr>
				<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2">

				<input type="radio" name="method" value="FedEx International">
				<xsl:if test="boolean($DELIVERY-METHOD-NAME='FedEx International')">
					<xsl:attribute name="checked">checked</xsl:attribute>
				</xsl:if>
				</input>
				<a CLASS="MedBlackText">FedEx International (No charge if your FedEx account is used)</a></td><td valign="top"><a CLASS="MedBlackText">$20.00</a></td></tr>
				<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>

				<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
				<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><a CLASS="MedBlackText"><b>Charge to my FedEx Account:</b></a></td></tr>
				<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><a CLASS="MedBlackText">FedEx Account #

				<input type="text" name="fedex" size="15">
				<xsl:attribute name="value"><xsl:value-of select="$ACCOUNT-NUMBER"/></xsl:attribute>
				</input>

				</a></td><td valign="top"><a CLASS="MedBlackText">No charge</a></td></tr>
				<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
				<tr><td valign="top" height="15" bgcolor="#3173b5" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" bgcolor="#3173B5" width="168"><a CLASS="LgWhiteText"><b>Service level (choose one)</b></a></td><td valign="top" bgcolor="#3173B5"><img src="/engresources/images/spacer.gif" border="0" height="1"/></td><td valign="top" width="68" bgcolor="#3173B5"><a CLASS="LgWhiteText">Cost</a></td></tr>
				<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
				<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><a CLASS="MedBlackText"><b>Choose service level</b></a></td></tr>
				<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2">

				<input type="radio" name="service" value="Regular service">
				<xsl:if test="boolean($SERVICE-LEVEL='Regular service')">
					<xsl:attribute name="checked">checked</xsl:attribute>
				</xsl:if>
				</input>
				<a CLASS="MedBlackText">Regular service: within 24-48 hours</a></td><td valign="top"><a CLASS="MedBlackText">No charge</a></td></tr>
				<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2">

				<input type="radio" name="service" value="Rush service">
				<xsl:if test="boolean($SERVICE-LEVEL='Rush service')">
					<xsl:attribute name="checked">checked</xsl:attribute>
				</xsl:if>
				</input>
				<a CLASS="MedBlackText">Rush service: within 6 working hours</a></td><td valign="top"><a CLASS="MedBlackText">$6.00</a></td></tr>
				<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2">

				<input type="radio" name="service" value="Super rush service">
				<xsl:if test="boolean($SERVICE-LEVEL='Super rush service')">
					<xsl:attribute name="checked">checked</xsl:attribute>
				</xsl:if>
				</input>
				<a CLASS="MedBlackText">Super rush service: within 3 working hours</a></td><td valign="top"><a CLASS="MedBlackText">$12.00</a></td></tr>
				<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2">

				<input type="radio" name="service" value="Drop everything service">
				<xsl:if test="boolean($SERVICE-LEVEL='Drop everything service')">
					<xsl:attribute name="checked">checked</xsl:attribute>
				</xsl:if>
				</input>
				<a CLASS="MedBlackText">Drop everything service: within 1 working hour</a></td><td valign="top"><a CLASS="MedBlackText">$100.00</a></td></tr>
				<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>

				<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
				<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><a CLASS="MedBlackText"><b>Shipping information</b></a></td></tr>
				<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><a CLASS="MedBlackText">Send to the attention of:  <input type="text" name="attention" size="15" value="{$ATTENTION}"/></a></td><td valign="top">  </td></tr>

				<xsl:choose>
					<xsl:when test="($SHIPPING-VALUE='d')">
						<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><a CLASS="MedBlackText">E-mail address for order confirmation:  <input type="text" name="confirmationEmail" size="15"/></a></td><td valign="top">  </td></tr>
					</xsl:when>
					<xsl:otherwise>
						<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
						<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><a CLASS="MedBlackText">E-mail address for order confirmation:  <xsl:value-of select="$EMAIL"/></a></td><td valign="top">  </td></tr>
					</xsl:otherwise>
				</xsl:choose>

				<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
				<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
				<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><a CLASS="MedBlackText"><b>Ship to:</b></a></td></tr>
				<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="3">

		<!--Based On shipping ie if Decentralised Editable fields are displayed else Non editable Data is Displayed-->
		<xsl:choose>
			<xsl:when test="($SHIPPING-VALUE='d')">

				<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<tr>
					<td valign="middle"><a CLASS="MedBlackText">First name: </a></td>
					<td valign="top">
						<input type="text" name="firstname" size="15">
							<xsl:attribute name="value"><xsl:value-of select="$FIRST-NAME"/></xsl:attribute>
						</input>
					</td>
					<td valign="middle"><a CLASS="MedBlackText">Last name: </a></td>
					<td valign="top">
						<input type="text" name="lastname" size="15">
							<xsl:attribute name="value"><xsl:value-of select="$LAST-NAME"/></xsl:attribute>
						</input>
					</td>
				</tr>

				<tr>
					<td valign="middle"><a CLASS="MedBlackText">Company name: </a></td>
					<td valign="top">
						<input type="text" name="companyname" size="15">
							<xsl:attribute name="value"><xsl:value-of select="$COMPANY-NAME"/></xsl:attribute>
						</input>
					</td>
				</tr>

				<tr>
					<td valign="middle"><a CLASS="MedBlackText">Address1: </a></td>
					<td valign="top">
						<input type="text" name="address1" size="15">
							<xsl:attribute name="value"><xsl:value-of select="$ADDRESS1"/></xsl:attribute>
						</input>
					</td>
					<td valign="middle"><a CLASS="MedBlackText">Address2: </a></td>
					<td valign="top">
						<input type="text" name="address2" size="15">
							<xsl:attribute name="value"><xsl:value-of select="$ADDRESS2"/></xsl:attribute>
						</input>
					</td>
				</tr>

				<tr>
					<td valign="middle"><a CLASS="MedBlackText">City: </a></td>
					<td valign="top">
						<input type="text" name="city" size="10">
							<xsl:attribute name="value"><xsl:value-of select="$CITY"/></xsl:attribute>
						</input>
					</td>
					<td valign="middle"><a CLASS="MedBlackText">State: </a></td><td valign="top">
						<input type="text" name="state" size="15">
							<xsl:attribute name="value"><xsl:value-of select="$STATE"/></xsl:attribute>
						</input>
					</td>
				</tr>

				<tr>
					<td valign="middle"><a CLASS="MedBlackText">Country: </a></td>
					<td valign="top">
						<input type="text" name="country" size="10">
							<xsl:attribute name="value"><xsl:value-of select="$COUNTRY"/></xsl:attribute>
						</input>
					</td>

					<td valign="middle"><a CLASS="MedBlackText">Zip: </a></td><td valign="top">
						<input type="text" name="zip" size="15">
							<xsl:attribute name="value"><xsl:value-of select="$ZIP"/></xsl:attribute>
						</input>
					</td>
				</tr>


				<tr>
					<td valign="middle"><a CLASS="MedBlackText">Telephone: </a></td>
					<td valign="top">
						<input type="text" name="phone" size="10">
						<xsl:attribute name="value"><xsl:value-of select="$TELEPHONE"/></xsl:attribute>
						</input>
					</td>
					<td valign="middle"><a CLASS="MedBlackText">Fax: </a></td><td valign="top">
						<input type="text" name="fax" size="15">
						<xsl:attribute name="value"><xsl:value-of select="$FAX"/></xsl:attribute>
						</input>
					</td>
				</tr>

				<tr>
					<td valign="top" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td>
					<td valign="middle" colspan="2">
						<a CLASS="MedBlackText">
							<input type="submit" name="submit" value="Submit" />
							<input type="button" name="close" value="Close" onclick="javascript:window.close();"/>
						</a>
					</td>
				</tr>
				</table>

			</xsl:when>

			<xsl:otherwise>

				<input type="hidden" name="firstname">
					<xsl:attribute name="value"><xsl:value-of select="$FIRST-NAME"/></xsl:attribute>
				</input>

				<input type="hidden" name="lastname">
					<xsl:attribute name="value"><xsl:value-of select="$LAST-NAME"/></xsl:attribute>
				</input>

				<input type="hidden" name="companyname">
					<xsl:attribute name="value"><xsl:value-of select="$COMPANY-NAME"/></xsl:attribute>
				</input>

				<input type="hidden" name="address1">
					<xsl:attribute name="value"><xsl:value-of select="$ADDRESS1"/></xsl:attribute>
				</input>

				<input type="hidden" name="address2">
					<xsl:attribute name="value"><xsl:value-of select="$ADDRESS2"/></xsl:attribute>
				</input>

				<input type="hidden" name="city">
					<xsl:attribute name="value"><xsl:value-of select="$CITY"/></xsl:attribute>
				</input>

				<input type="hidden" name="state">
					<xsl:attribute name="value"><xsl:value-of select="$STATE"/></xsl:attribute>
				</input>

				<input type="hidden" name="country">
					<xsl:attribute name="value"><xsl:value-of select="$COUNTRY"/></xsl:attribute>
				</input>

				<input type="hidden" name="zip">
					<xsl:attribute name="value"><xsl:value-of select="$ZIP"/></xsl:attribute>
				</input>

				<input type="hidden" name="fax">
					<xsl:attribute name="value"><xsl:value-of select="$FAX"/></xsl:attribute>
				</input>

				<input type="hidden" name="confirmationEmail">
					<xsl:attribute name="value"><xsl:value-of select="$EMAIL"/></xsl:attribute>
				</input>

				<input type="hidden" name="phone">
					<xsl:attribute name="value"><xsl:value-of select="$TELEPHONE"/></xsl:attribute>
				</input>

				<table border="0" cellspacing="0" cellpadding="0" width="100%">
					<tr>
						<td valign="top" colspan="3">
							<a CLASS="MedBlackText"><xsl:value-of select="$FIRST-NAME"/><xsl:text> </xsl:text><xsl:value-of select="$LAST-NAME"/><br/>
							<xsl:value-of select="$COMPANY-NAME"/><br/>
							<xsl:value-of select="$ADDRESS1"/><xsl:text> </xsl:text><xsl:value-of select="$ADDRESS2"/><br/>
							<xsl:value-of select="$CITY"/>,<xsl:text> </xsl:text><xsl:value-of select="$STATE"/><xsl:text> </xsl:text><xsl:value-of select="$ZIP"/><xsl:text> </xsl:text><xsl:value-of select="$COUNTRY"/><br/>
							<xsl:value-of select="$TELEPHONE"/><br/>
							<xsl:value-of select="$FAX"/><br/>
							</a>
						</td>
					</tr>
					<tr>
						<td valign="top" colspan="3" height="4"><img src="/engresources/images/spacer.gif" border="0" height="4"/></td></tr>
					<tr>
						<td valign="top" colspan="3">
							<a CLASS="MedBlackText"><input type="submit" name="submit" value="Submit"/>  <input type="button" name="close" value="Close" onclick="javascript:window.close();"/></a>
						</td>
					</tr>
				</table>

			</xsl:otherwise>

		</xsl:choose>

		</td>
		</tr>
		</table>

	</td>
	</tr>
	</table>

	</td>
	</tr>
	</table>

	</FORM>

	</xsl:if>


	<!-- LOGIN WITH WRONG PASSWORD -->
	<xsl:if test="boolean($MATCH='true')">
	<center>
			<table border="0" width="99%" cellspacing="0" cellpadding="0">

				<tr><td valign="top"><a class="EvHeaderText">Linda Hall Library Document Request</a></td></tr>
				<tr><td valign="top" height="10"><img src="/engresources/images/spacer.gif" border="0" height="10"/></td></tr>							
				<tr><td valign="top"><a CLASS="MedBlackText">A password is required to access the Linda Hall Library document request form:</a></td></tr>
				<tr><td valign="top" height="3"><img src="/engresources/images/spacer.gif" border="0" height="3"/></td></tr>

				<tr><td valign="top"><a CLASS="RedText">Your Password is not recognized.</a></td></tr>
				<tr><td valign="top" height="15"><img src="/engresources/images/spacer.gif" border="0" height="15"/></td></tr>


				<tr><td valign="top" width="250">
				<table border="0" width="250" cellspacing="0" cellpadding="0">

					<FORM name="lhlrequest" method="post" action="/controller/servlet/Controller?CID=lhlViewOrderForm" onsubmit="return validForm(lhlrequest)">
						<input type="hidden" name="docid">
							<xsl:attribute name="value"><xsl:value-of select="$DOC-ID"/></xsl:attribute>
						</input>
						<input type="hidden" name="database">
							<xsl:attribute name="value"><xsl:value-of select="$DATABASE"/></xsl:attribute>
						</input>
						<tr><td valign="middle"><a CLASS="MedBlackText">Password: </a></td><td valign="top"><a CLASS="MedBlackText"><input type="password" name="pword" size="24"/></a></td></tr>
						<tr><td valign="top" height="4" colspan="2"><img src="/engresources/images/spacer.gif" border="0" height="4"/></td></tr>
						<tr><td valign="top">  </td><td valign="top"><a CLASS="MedBlackText"><input type="submit" name="submit" value="Submit"/></a></td></tr>
					</FORM>
				</table>
				</td></tr>
				<tr><td valign="top" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
				<tr><td valign="top"><a CLASS="MedBlackText">If you don't have a password, you can place an order directly with </a><a CLASS="LgBlueLink" href="http://www.lindahall.org/services/document_delivery/partners/eicompendex.shtml">Linda Hall Library.</a></td></tr>
			</table>
		</center>
	</xsl:if>

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
