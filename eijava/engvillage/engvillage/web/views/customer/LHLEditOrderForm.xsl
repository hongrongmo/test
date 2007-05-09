<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:java="java:java.net.URLEncoder"
	exclude-result-prefixes="java xsl"
	>

<xsl:output method="html" encoding="UTF-8"/>
<xsl:preserve-space elements="xsl text"/>


<!--This XSL renders the XML Data provided by LHLEditOrderForm.jsp -->
<!--This Page Displays Linda Hall Info where he can Edit Data.
	Based On Shipping Value Attribute the user is given the option to edit Ship to Info -->
<xsl:include href="template.CLEAN-SPECIAL-CHARACTERS.xsl"/>

<xsl:include href="Footer.xsl"/>


<xsl:template match="LINDA-HALL">

		<xsl:variable name="SHIPPING-VALUE">
			<xsl:value-of select="LHL-USER-INFO/SHIPPING-VALUE"/>
		</xsl:variable>

		<xsl:variable name="TITLE">
			<xsl:value-of select="EI-DOCUMENT/ARTICLE-TITLE"/>
		</xsl:variable>

		<xsl:variable name="TITLE-OF-TRANSLATION">
			<xsl:value-of select="EI-DOCUMENT/TITLE-OF-TRANSLATION"/>
		</xsl:variable>

		<xsl:variable name="ISSN">
			<xsl:value-of select="EI-DOCUMENT/ISSN"/>
		</xsl:variable>

		<xsl:variable name="CODEN">
			<xsl:value-of select="EI-DOCUMENT/CODEN"/>
		</xsl:variable>

		<xsl:variable name="ISBN">
			<xsl:value-of select="EI-DOCUMENT/ISBN"/>
		</xsl:variable>

		<xsl:variable name="DOC-ID">
			<xsl:value-of select="LHL-USER-INFO/DOC-ID"/>
		</xsl:variable>

		<xsl:variable name="DATABASE">
			<xsl:value-of select="LHL-USER-INFO/DATABASE"/>
		</xsl:variable>

		<xsl:variable name="DELIVERY-METHOD-NAME">
			<xsl:value-of select="LHL-USER-INFO/DELIVERY-METHOD-NAME"/>
		</xsl:variable>

		<xsl:variable name="DELIVERY-METHOD-VALUE">
			<xsl:value-of select="LHL-USER-INFO/DELIVERY-METHOD-VALUE"/>
		</xsl:variable>

		<xsl:variable name="SERVICE-LEVEL">
			<xsl:value-of select="LHL-USER-INFO/SERVICE-LEVEL"/>
		</xsl:variable>

		<xsl:variable name="ACCOUNT-NUMBER">
			<xsl:value-of select="LHL-USER-INFO/ACCOUNT-NUMBER"/>
		</xsl:variable>

		<xsl:variable name="ATTENTION">
			<xsl:value-of select="LHL-USER-INFO/ATTENTION"/>
		</xsl:variable>

		<xsl:variable name="EMAIL-CONFIRM">
			<xsl:value-of select="LHL-USER-INFO/EMAIL-CONFIRM"/>
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
			<xsl:value-of select="LHL-USER-INFO/CONTACT-INFO/TELEPHONE"/>
		</xsl:variable>

		<xsl:variable name="FAX">
			<xsl:value-of select="LHL-USER-INFO/CONTACT-INFO/FAX"/>
		</xsl:variable>

		<xsl:variable name="EMAIL">
			<xsl:value-of select="LHL-USER-INFO/CONTACT-INFO/EMAIL"/>
		</xsl:variable>

<html>
<head>
	<title>Linda Hall Library Document Request</title>
	<SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js"/>
	<SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/LindaHallOrder.js"/>
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



<FORM name="delivery" method="post" action="/controller/servlet/Controller?CID=lhlOrderFormComplete" onsubmit="return checkdelivery(delivery,'{$SHIPPING-VALUE}')">

<center>

<table border="0" width="99%" cellspacing="0" cellpadding="0">
<tr><td valign="top" colspan="3"><a class="EvHeaderText">Linda Hall Library Document Request</a></td></tr>
<tr><td valign="top" height="10"><img src="/engresources/images/spacer.gif" border="0" height="10"/></td></tr>
<tr><td valign="top" colspan="3"><a CLASS="MedBlackText">You have selected the following document to order from Linda Hall Library.
You must complete the order form below to submit the request.</a></td></tr>

<tr><td valign="top" colspan="2" height="10"><img src="/engresources/images/spacer.gif" border="0" height="10"/></td></tr>


<xsl:if test="boolean(string-length(normalize-space($TITLE))>0)">
	<tr><td valign="top">
			<a CLASS="MedBlackText"><b>Title: </b></a>
			</td><td valign="top">
			<a CLASS="MedBlackText">
			<xsl:value-of select="$TITLE" disable-output-escaping="yes"/>
			<xsl:if test="boolean(string-length(normalize-space($TITLE-OF-TRANSLATION)) > 0)">
			(<xsl:value-of select="$TITLE-OF-TRANSLATION"  disable-output-escaping="yes"/>)
			</xsl:if>
			</a>
	</td></tr>
</xsl:if>

<!--Iterates Thru authors and finally so build authors is assigned to All-Authors variable -->
<xsl:if test="boolean(string-length(normalize-space(EI-DOCUMENT/AUTHORS/AUTHOR))>0)">
    <tr><td valign="top">
	   <a CLASS="MedBlackText"><b>Authors: </b></a></td><td valign="top">
	       <xsl:variable name="ALL-AUTHORS">
				 <xsl:for-each select="EI-DOCUMENT/AUTHORS/AUTHOR">
						<xsl:variable name="AUTHOR">{<xsl:value-of select="." disable-output-escaping="yes"/>}</xsl:variable>
						<xsl:variable name="CLEANED-AUTHOR-NAME">
							<xsl:call-template name="CLEAN-SPECIAL-CHARACTERS">
								<xsl:with-param name="INPUT-STRING">
									<xsl:value-of select="$AUTHOR" />
								</xsl:with-param>
							</xsl:call-template>
						</xsl:variable>
						<xsl:if test="boolean(not(contains($CLEANED-AUTHOR-NAME,'Anon')))">
							<A CLASS="SpLink">
								<xsl:value-of select="." disable-output-escaping="yes"/>
							</A>
						</xsl:if>
						<xsl:if test="boolean(contains($CLEANED-AUTHOR-NAME,'Anon'))">
							<A CLASS="SmBlackText"><xsl:value-of select="." disable-output-escaping="yes"/></A>
						</xsl:if>
						<xsl:if test="not(position()=last())">
							<A CLASS="SmBlackText">;</A>
						</xsl:if>
				</xsl:for-each>
			  </xsl:variable>

			<a CLASS="MedBlackText">
			<xsl:value-of select="$ALL-AUTHORS" disable-output-escaping="yes"/>
			</a>

		  <input type="hidden" name="authors">
		  <xsl:attribute name="value">
			  <xsl:value-of select="$ALL-AUTHORS"/>
		  </xsl:attribute>
		  </input>
		</td></tr>
</xsl:if>

<!--Iterates Thru Editors and finally so build editors is assigned to All-Editors variable -->

<xsl:if test="boolean(string-length(normalize-space(EI-DOCUMENT/EDITORS/EDITOR))>0) and
					   not(boolean(string-length(normalize-space(EI-DOCUMENT/AUTHORS/AUTHOR))>0))">
    <tr><td valign="top">
	   <a CLASS="MedBlackText"><b>Editors: </b></a></td><td valign="top">
	       <xsl:variable name="ALL-EDITORS">
			  <xsl:for-each select="EI-DOCUMENT/EDITORS/EDITOR">
				<xsl:variable name="EDITOR">{<xsl:value-of select="." disable-output-escaping="yes"/>}</xsl:variable>
				<xsl:variable name="CLEANED-EDITOR-NAME">
					<xsl:call-template name="CLEAN-SPECIAL-CHARACTERS">
						<xsl:with-param name="INPUT-STRING">
							<xsl:value-of select="$EDITOR" />
						</xsl:with-param>
					</xsl:call-template>
				</xsl:variable>
					<A CLASS="SpLink">
					<xsl:value-of select="." disable-output-escaping="yes"/></A>
				<xsl:if test="not(position()=last())">
					<A CLASS="SmBlackText">;</A>
				</xsl:if>
			</xsl:for-each>
        </xsl:variable>

		<a CLASS="MedBlackText">
		<xsl:value-of select="$ALL-EDITORS" disable-output-escaping="yes"/>
		</a>

	  <input type="hidden" name="editors">
	  <xsl:attribute name="value">
		  <xsl:value-of select="$ALL-EDITORS"/>
	  </xsl:attribute>
	  </input>
    </td></tr>
</xsl:if>

<!--Based on Database ie Compendex or Inspec,Display Rules are applied and Source variable is built   -->
<tr>
<xsl:choose>
	<xsl:when test="(EI-DOCUMENT/DATABASE='Compendex')">

	<!-- Based on Database ie Compendex or Inspec,Display Rules are applied and Source variable is built   -->

	<xsl:variable name="SERIAL-TITLE">
		<xsl:value-of select="EI-DOCUMENT/SERIAL-TITLE"/>
	</xsl:variable>

	<input type="hidden" name="title">
	<xsl:attribute name="value">
		<xsl:value-of select="EI-DOCUMENT/ARTICLE-TITLE"/>
	</xsl:attribute>
	</input>


	<xsl:variable name="ABBREVIATED-SERIAL-TITLE">
		<xsl:value-of select="EI-DOCUMENT/ABBREVIATED-SERIAL-TITLE"/>
	</xsl:variable>
	<xsl:variable name="ABBREVIATED-MONOGRAPH-TITLE">
		<xsl:value-of select="EI-DOCUMENT/ABBREVIATED-MONOGRAPH-TITLE"/>
	</xsl:variable>
	<xsl:variable name="MONOGRAPH-TITLE">
		<xsl:value-of select="EI-DOCUMENT/MONOGRAPH-TITLE"/>
	</xsl:variable>
	<xsl:variable name="VOLUME">
		 <xsl:value-of select="EI-DOCUMENT/VOLUME"/>
	</xsl:variable>
	<xsl:variable name="ISSUE">
		 <xsl:value-of select="EI-DOCUMENT/ISSUE"/>
	</xsl:variable>
	<xsl:variable name="ISSUE-DATE">
		 <xsl:value-of select="EI-DOCUMENT/ISSUE-DATE"/>
	</xsl:variable>
	<xsl:variable name="VOLUME-TITLE">
		 <xsl:value-of select="EI-DOCUMENT/VOLUME-TITLE"/>
	</xsl:variable>
	<xsl:variable name="PUBLISHER-NAME">
		 <xsl:value-of select="EI-DOCUMENT/PUBLISHER/PUBLISHER-NAME"/>
	</xsl:variable>
	<xsl:variable name="PUBLICATION-DATE">
		<xsl:value-of select="EI-DOCUMENT/PUBLICATION-DATE"/>
	</xsl:variable>
	<xsl:variable name="NUMBER-OF-VOLUMES">
		<xsl:value-of select="EI-DOCUMENT/NUMBER-OF-VOLUMES"/>
	</xsl:variable>
	<xsl:variable name="PAPER-NUMBER">
		<xsl:value-of select="EI-DOCUMENT/PAPER-NUMBER"/>
	</xsl:variable>
	<xsl:variable name="PAGE-RANGE">
		<xsl:value-of select="EI-DOCUMENT/PAGE-RANGE"/>
	</xsl:variable>
	<xsl:variable name="PAGES">
		<xsl:value-of select="EI-DOCUMENT/PAGES"/>
	</xsl:variable>

		<!-- Display of Source label -->
		<xsl:if test="boolean( (string-length(normalize-space(EI-DOCUMENT/SERIAL-TITLE))>0) or (string-length(normalize-space(EI-DOCUMENT/ABBREVIATED-SERIAL-TITLE))>0) or  (string-length(normalize-space(EI-DOCUMENT/MONOGRAPH-TITLE))>0) or (string-length(normalize-space(EI-DOCUMENT/ABBREVIATED-MONOGRAPH-TITLE))>0) or (string-length(normalize-space(EI-DOCUMENT/PUBLISHER-NAME))>0) )">
			<td valign="top"><a CLASS="MedBlackText"><b>Source: </b></a></td>
		</xsl:if>

		<xsl:choose>
		<!-- Display of Source label -->
		<xsl:when test="boolean( (string-length(normalize-space(EI-DOCUMENT/SERIAL-TITLE))>0) or (string-length(normalize-space(EI-DOCUMENT/ABBREVIATED-SERIAL-TITLE))>0) or  (string-length(normalize-space(EI-DOCUMENT/MONOGRAPH-TITLE))>0) or (string-length(normalize-space(EI-DOCUMENT/ABBREVIATED-MONOGRAPH-TITLE))>0) or (string-length(normalize-space(EI-DOCUMENT/PUBLISHER-NAME))>0) )">
		<xsl:variable name="SOURCE">
				 <xsl:choose>
							  <xsl:when test="boolean(string-length(normalize-space($SERIAL-TITLE))>0) or
										      boolean(string-length(normalize-space($ABBREVIATED-SERIAL-TITLE))>0)">
									<xsl:choose>
									  <xsl:when test="boolean(string-length(normalize-space($SERIAL-TITLE))>0)">
										<i><xsl:value-of select="$SERIAL-TITLE" disable-output-escaping="yes"/></i>
									  </xsl:when>
									  <xsl:otherwise>
										<xsl:if test="boolean(string-length(normalize-space($ABBREVIATED-SERIAL-TITLE))>0)">
										 <i><xsl:value-of select="$ABBREVIATED-SERIAL-TITLE" disable-output-escaping="yes"/></i>
										</xsl:if>
									  </xsl:otherwise>
									</xsl:choose>
									<xsl:if test="boolean(string-length(normalize-space($VOLUME))>0)">
									   <xsl:value-of select="$VOLUME" disable-output-escaping="yes"/>
									</xsl:if>
									<xsl:if test="boolean(string-length(normalize-space($ISSUE))>0)">
									   <xsl:value-of select="$ISSUE" disable-output-escaping="yes"/>
									</xsl:if>
									<xsl:if test="boolean(string-length(normalize-space($ISSUE-DATE))>0)">
									   <xsl:value-of select="$ISSUE-DATE" disable-output-escaping="yes"/>
									</xsl:if>
									<xsl:choose>
										<xsl:when test="boolean(string-length(normalize-space($MONOGRAPH-TITLE))>0)">
											 <i><xsl:value-of select="$MONOGRAPH-TITLE" disable-output-escaping="yes"/></i>
										</xsl:when>
										<xsl:otherwise>
										   <xsl:if test="boolean(string-length(normalize-space($ABBREVIATED-MONOGRAPH-TITLE))>0)">
											 <i><xsl:value-of select="$ABBREVIATED-MONOGRAPH-TITLE" disable-output-escaping="yes"/></i>
										   </xsl:if>
										</xsl:otherwise>
									</xsl:choose>
									<xsl:if test="boolean(string-length(normalize-space($VOLUME-TITLE))>0)">
										 <xsl:value-of select="$VOLUME-TITLE" disable-output-escaping="yes"/>
									</xsl:if>
									<!-- {if YR is not null} YR-->
									<xsl:if test="boolean(string-length(normalize-space($PUBLICATION-DATE))>0)">
										 <xsl:value-of select="$PUBLICATION-DATE" disable-output-escaping="yes"/>
									</xsl:if>
									<xsl:if test="boolean(string-length(normalize-space($NUMBER-OF-VOLUMES))>0)">
										 <xsl:value-of select="$NUMBER-OF-VOLUMES" disable-output-escaping="yes"/>
									</xsl:if>
									<xsl:if test="boolean(string-length(normalize-space($PAPER-NUMBER))>0)">
										 <xsl:value-of select="$PAPER-NUMBER" disable-output-escaping="yes"/>
									</xsl:if>
									<!-- {if XP (Page Range) is not null} p XP {else if PP(Number of Pages) is not null} PPp -->
									<xsl:choose>
									   <xsl:when test="boolean(string-length(normalize-space($PAGE-RANGE))>0)">
										  <xsl:value-of select="$PAGE-RANGE" disable-output-escaping="yes"/>
									   </xsl:when>
									   <xsl:otherwise>
										 <xsl:if test="boolean(string-length(normalize-space($PAGES))>0)">
											 <xsl:value-of select="$PAGES" disable-output-escaping="yes"/>
										 </xsl:if>
									   </xsl:otherwise>
									</xsl:choose>
							 </xsl:when>

							 <!-- Records without serial titles {if (ST and SE) are null and (MT or ME) is not null} -->

							 <xsl:when test="(not(boolean(string-length(normalize-space($SERIAL-TITLE))>0)) and
											  not(boolean(string-length(normalize-space($ABBREVIATED-SERIAL-TITLE))>0))) and
												 (boolean(string-length(normalize-space($MONOGRAPH-TITLE))>0) or
												  boolean(string-length(normalize-space($ABBREVIATED-MONOGRAPH-TITLE))>0))">
								<xsl:choose>
									<xsl:when test="boolean(string-length(normalize-space($MONOGRAPH-TITLE))>0)">
										<i><xsl:value-of select="$MONOGRAPH-TITLE" disable-output-escaping="yes"/></i>
									</xsl:when>
									<xsl:otherwise>
										<xsl:if test="boolean(string-length(normalize-space($ABBREVIATED-MONOGRAPH-TITLE))>0)">
										  <i><xsl:value-of select="$ABBREVIATED-MONOGRAPH-TITLE" disable-output-escaping="yes"/></i>
										</xsl:if>
									</xsl:otherwise>
								</xsl:choose>
								<xsl:if test="boolean(string-length(normalize-space($VOLUME))>0)">
								 	 <xsl:value-of select="$VOLUME" disable-output-escaping="yes"/>
								</xsl:if>
								<xsl:if test="boolean(string-length(normalize-space($ISSUE))>0)">
								 	 <xsl:value-of select="$ISSUE" disable-output-escaping="yes"/>
								</xsl:if>
								<xsl:if test="boolean(string-length(normalize-space($VOLUME-TITLE))>0)">
								 	 <xsl:value-of select="$VOLUME-TITLE" disable-output-escaping="yes"/>
								</xsl:if>
								<!-- {if YR is not null} YR-->
								<xsl:if test="boolean(string-length(normalize-space($PUBLICATION-DATE))>0)">
									 <xsl:value-of select="$PUBLICATION-DATE" disable-output-escaping="yes"/>
								</xsl:if>
								<xsl:if test="boolean(string-length(normalize-space($NUMBER-OF-VOLUMES))>0)">
									 <xsl:value-of select="$NUMBER-OF-VOLUMES" disable-output-escaping="yes"/>
								</xsl:if>
								<xsl:if test="boolean(string-length(normalize-space($PAPER-NUMBER))>0)">
									 <xsl:value-of select="$PAPER-NUMBER" disable-output-escaping="yes"/>
								</xsl:if>
								<!-- {if XP (Page Range) is not null} p XP {else if PP(Number of Pages) is not null} PPp -->
								<xsl:choose>
								   <xsl:when test="boolean(string-length(normalize-space($PAGE-RANGE))>0)">
										 <xsl:value-of select="$PAGE-RANGE" disable-output-escaping="yes"/>
								   </xsl:when>
								   <xsl:otherwise>
									 <xsl:if test="boolean(string-length(normalize-space($PAGES))>0)">
										 <xsl:value-of select="$PAGES" disable-output-escaping="yes"/>
									 </xsl:if>
								   </xsl:otherwise>
								</xsl:choose>
							</xsl:when>

							<!-- Records without source titles {if (ST and SE and MT and ME) is null} -->

							<xsl:when test="not(boolean(string-length(normalize-space($SERIAL-TITLE))>0)) and
											not(boolean(string-length(normalize-space($ABBREVIATED-SERIAL-TITLE))>0)) and
											not(boolean(string-length(normalize-space($MONOGRAPH-TITLE))>0)) and
											not(boolean(string-length(normalize-space($ABBREVIATED-MONOGRAPH-TITLE))>0))">

								<xsl:if test="boolean(string-length(normalize-space($PUBLISHER-NAME))>0)">
									<xsl:value-of select="$PUBLISHER-NAME" disable-output-escaping="yes"/>
								</xsl:if>
								<xsl:if test="boolean(string-length(normalize-space($VOLUME))>0)">
									 <xsl:value-of select="$VOLUME" disable-output-escaping="yes"/>
								</xsl:if>
								<xsl:if test="boolean(string-length(normalize-space($ISSUE))>0)">
									 <xsl:value-of select="$ISSUE" disable-output-escaping="yes"/>
								</xsl:if>
								<!-- {if YR is not null} YR-->
								<xsl:if test="boolean(string-length(normalize-space($PUBLICATION-DATE))>0)">
									 <xsl:value-of select="$PUBLICATION-DATE" disable-output-escaping="yes"/>
								</xsl:if>
								<xsl:if test="boolean(string-length(normalize-space($NUMBER-OF-VOLUMES))>0)">
									 <xsl:value-of select="$NUMBER-OF-VOLUMES" disable-output-escaping="yes"/>
								</xsl:if>
								<xsl:if test="boolean(string-length(normalize-space($PAPER-NUMBER))>0)">
									 <xsl:value-of select="$PAPER-NUMBER" disable-output-escaping="yes"/>
								</xsl:if>
								<!-- {if XP (Page Range) is not null} p XP {else if PP(Number of Pages) is not null} PPp -->
								<xsl:choose>
								   <xsl:when test="boolean(string-length(normalize-space($PAGE-RANGE))>0)">
									  <xsl:value-of select="$PAGE-RANGE" disable-output-escaping="yes"/>
								   </xsl:when>
								   <xsl:otherwise>
									 <xsl:if test="boolean(string-length(normalize-space($PAGES))>0)">
										 <xsl:value-of select="$PAGES" disable-output-escaping="yes"/>
									 </xsl:if>
								   </xsl:otherwise>
								</xsl:choose>
							 </xsl:when>

						 <!-- end of first if after source -->

						 <!-- Publication date: {only show if (ST and SE and MT and ME and PN) is null} -->

							 <xsl:otherwise>
								 <xsl:if test="not(boolean(string-length(normalize-space($SERIAL-TITLE))>0)) and
											   not(boolean(string-length(normalize-space($ABBREVIATED-SERIAL-TITLE))>0)) and
											   not(boolean(string-length(normalize-space($MONOGRAPH-TITLE))>0)) and
											   not(boolean(string-length(normalize-space($ABBREVIATED-MONOGRAPH-TITLE))>0)) and
											   not(boolean(string-length(normalize-space($PUBLISHER-NAME))>0))">
								   <a CLASS="SmBlackText"><b>Publication date: </b>
									<!-- {if YR is not null} YR-->
									<xsl:if test="boolean(string-length(normalize-space($PUBLICATION-DATE))>0)">
										<xsl:value-of select="$PUBLICATION-DATE" disable-output-escaping="yes"/>
									</xsl:if>
									<xsl:if test="boolean(string-length(normalize-space($NUMBER-OF-VOLUMES))>0)">
										 <xsl:value-of select="$NUMBER-OF-VOLUMES" disable-output-escaping="yes"/>
									</xsl:if>
									<xsl:if test="boolean(string-length(normalize-space($PAPER-NUMBER))>0)">
										 <xsl:value-of select="$PAPER-NUMBER" disable-output-escaping="yes"/>
									</xsl:if>
									<!-- {if XP (Page Range) is not null} p XP {else if PP(Number of Pages) is not null} PPp -->
									<xsl:choose>
									   <xsl:when test="boolean(string-length(normalize-space($PAGE-RANGE))>0)">
										  <xsl:value-of select="$PAGE-RANGE" disable-output-escaping="yes"/>
									   </xsl:when>
									   <xsl:otherwise>
										 <xsl:if test="boolean(string-length(normalize-space($PAGES))>0)">
											 <xsl:value-of select="$PAGES" disable-output-escaping="yes"/>
										 </xsl:if>
									   </xsl:otherwise>
									</xsl:choose>
								   </a>
								 </xsl:if>
							 </xsl:otherwise>
		   </xsl:choose>
            </xsl:variable>


            <td valign="top">
			<a CLASS="MedBlackText">
			<xsl:value-of select="$SOURCE" disable-output-escaping="yes"/>
			</a>
			</td>

			<xsl:if test="boolean(string-length(normalize-space($SOURCE))>0)">
			   <input type="hidden" name="source">
				<xsl:attribute name="value">
					<xsl:value-of select="$SOURCE"/>
				</xsl:attribute>
				</input>
			  <input type="hidden" name="publication_date">
				<xsl:attribute name="value"></xsl:attribute>
			  </input>
			</xsl:if>

</xsl:when>


		<xsl:otherwise>

			<xsl:if test="boolean(not((string-length(normalize-space(EI-DOCUMENT/SERIAL-TITLE))>0) and (string-length(normalize-space(EI-DOCUMENT/ABBREVIATED-SERIAL-TITLE))>0) and (string-length(normalize-space(EI-DOCUMENT/MONOGRAPH-TITLE))>0) and (string-length(normalize-space(EI-DOCUMENT/ABBREVIATED-MONOGRAPH-TITLE))>0)  ))">
			<td valign="top"><a CLASS="MedBlackText"><b>Publication Date: </b></a></td>
			</xsl:if>

			<xsl:variable name="VAR-PUBLICATION-DATE">

			<xsl:if test="boolean(not((string-length(normalize-space(EI-DOCUMENT/SERIAL-TITLE))>0) and (string-length(normalize-space(EI-DOCUMENT/ABBREVIATED-SERIAL-TITLE))>0) and (string-length(normalize-space(EI-DOCUMENT/MONOGRAPH-TITLE))>0) and (string-length(normalize-space(EI-DOCUMENT/ABBREVIATED-MONOGRAPH-TITLE))>0)  ))">

			<xsl:if test="boolean(string-length(normalize-space(EI-DOCUMENT/PUBLICATION-DATE))>0)">
				<a CLASS="SmBlackText"><xsl:value-of select="EI-DOCUMENT/PUBLICATION-DATE" disable-output-escaping="yes"/></a>
			</xsl:if>

			<xsl:if test="boolean(string-length(normalize-space(EI-DOCUMENT/NUMBER-OF-VOLUMES))>0)">
				<a CLASS="SmBlackText">, <xsl:value-of select="EI-DOCUMENT/NUMBER-OF-VOLUMES" disable-output-escaping="yes"/>vols.</a>
			</xsl:if>

			<xsl:if test="boolean(string-length(normalize-space(EI-DOCUMENT/PAPER-NUMBER))>0)">
				<a CLASS="SmBlackText">, <xsl:value-of select="EI-DOCUMENT/PAPER-NUMBER" disable-output-escaping="yes"/></a>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="boolean(string-length(normalize-space(EI-DOCUMENT/PAGE-RANGE))>0)">
					<a CLASS="SmBlackText">, <xsl:value-of select="EI-DOCUMENT/PAGE-RANGE" disable-output-escaping="yes"/></a>
				</xsl:when>
				<xsl:when test="boolean(string-length(normalize-space(EI-DOCUMENT/NUMBER-OF-PAGES))>0)">
						<a CLASS="SmBlackText">, <xsl:value-of select="EI-DOCUMENT/NUMBER-OF-PAGES" disable-output-escaping="yes"/>p</a>
				</xsl:when>
			</xsl:choose>
			</xsl:if>

			</xsl:variable>

			<td valign="top">
			<a CLASS="MedBlackText">
			<xsl:value-of select="$VAR-PUBLICATION-DATE" disable-output-escaping="yes"/>
			</a>
			</td>

			<xsl:if test="boolean(string-length(normalize-space($VAR-PUBLICATION-DATE))>0)">
			   <input type="hidden" name="publication_date">
				<xsl:attribute name="value">
					<xsl:value-of select="$VAR-PUBLICATION-DATE"/>
				</xsl:attribute>
				</input>
			  <input type="hidden" name="source">
				<xsl:attribute name="value"></xsl:attribute>
			  </input>
			</xsl:if>


		</xsl:otherwise>
     </xsl:choose>


</xsl:when>

<!-- Based on Database Inspec,Display Rules are applied and Source variable is built   -->
<xsl:when test="(EI-DOCUMENT/DATABASE='INSPEC')">

	<xsl:variable name="SERIAL-TITLE">
		<xsl:value-of select="EI-DOCUMENT/SERIAL-TITLE"/>
	</xsl:variable>

	<input type="hidden" name="title">
	<xsl:attribute name="value">
		<xsl:value-of select="EI-DOCUMENT/ARTICLE-TITLE"/>
	</xsl:attribute>
	</input>

	<xsl:variable name="VOLUME">
		<xsl:value-of select="EI-DOCUMENT/VOLISS/VOLUME"/>
	</xsl:variable>
	<xsl:variable name="ISSUE">
		<xsl:value-of select="EI-DOCUMENT/VOLISS/ISSUE"/>
	</xsl:variable>
	<xsl:variable name="PUBLICATION-DATE">
		<xsl:value-of select="EI-DOCUMENT/PUBLICATION-DATE"/>
	</xsl:variable>
	<xsl:variable name="PART-NUMBER">
		<xsl:value-of select="EI-DOCUMENT/PART-NUMBER"/>
	</xsl:variable>
	<xsl:variable name="PAGES">
		<xsl:value-of select="EI-DOCUMENT/PAGES"/>
	</xsl:variable>
	<xsl:variable name="PAGES1">
		<xsl:value-of select="EI-DOCUMENT/PAGES1"/>
	</xsl:variable>
	<xsl:variable name="PAGES2">
		<xsl:value-of select="EI-DOCUMENT/PAGES2"/>
	</xsl:variable>

	<xsl:variable name="DOCUMENT-TYPE">
		<xsl:value-of select="EI-DOCUMENT/DOCUMENT-TYPE"/>
	</xsl:variable>

	<xsl:variable name="HIGH-LEVEL-PUB-TITLE">
		<xsl:value-of select="EI-DOCUMENT/HIGH-LEVEL-PUB-TITLE"/>
	</xsl:variable>

	<xsl:variable name="PUBLISHER-NAME">
		 <xsl:value-of select="EI-DOCUMENT/PUBLISHER-NAME"/>
	</xsl:variable>

	<xsl:variable name="ISSUING-ORG">
		<xsl:value-of select="EI-DOCUMENT/ISSUING-ORG"/>
	</xsl:variable>
	<xsl:variable name="REPORT-NUMBER">
		<xsl:value-of select="EI-DOCUMENT/REPORT-NUMBER"/>
	</xsl:variable>
	<xsl:variable name="FILING-DATE">
		<xsl:value-of select="EI-DOCUMENT/FILING-DATE"/>
	</xsl:variable>

	<!-- Display Rules for Citation Format when:
		RTYPE=02 (journal article)
		RTYPE=05 and FJT is not null (conference proceeding in journal)
		RTYPE=06 and FJT is not null (conference article in journal)
	 -->
	<xsl:choose>
				<xsl:when test="boolean(($DOCUMENT-TYPE ='02') or ($DOCUMENT-TYPE ='05') and (string-length(normalize-space ($SERIAL-TITLE))>0) or ($DOCUMENT-TYPE ='06') and (string-length(normalize-space($SERIAL-TITLE))>0))">

					<!-- Display Source -->
					<a CLASS="SmBlackText">
						<td valign="top"><a CLASS="MedBlackText"><b>Source: </b></a></td>
					</a>
					<xsl:variable name="SOURCE">

     					<!-- Display Serial Title -->
						<a CLASS="SmBlackText">
							<i><xsl:value-of select="$SERIAL-TITLE" disable-output-escaping="yes"/></i>
						</a>

						<!-- Display Volume: {if VOLISS is not null} v (volume), n (number), -->
						<xsl:if test="boolean(string-length(normalize-space($VOLUME))>0)">
							<a CLASS="SmBlackText">
								, v <xsl:value-of select="$VOLUME" disable-output-escaping="yes"/>
							</a>
						</xsl:if>

						<!-- Display Issue: {if ISSUE is not null} , n (number), -->
						<xsl:if test="boolean(string-length(normalize-space($ISSUE))>0)">
							<a CLASS="SmBlackText">
								, n <xsl:value-of select="$ISSUE" disable-output-escaping="yes"/>
							</a>
						</xsl:if>

						<!-- Display Publication date:  {if PDATE is not null} PDATE, -->
						<xsl:if test="boolean(string-length(normalize-space($PUBLICATION-DATE))>0)">
							<a CLASS="SmBlackText">
								, <xsl:value-of select="$PUBLICATION-DATE" disable-output-escaping="yes"/>
							</a>
						</xsl:if>

						<!-- Display Part number: {if PARTNO is not null} PARTNO, -->
						<xsl:if test="boolean(string-length(normalize-space($PART-NUMBER))>0)">
							<a CLASS="SmBlackText">
								<xsl:text>, pt. </xsl:text><xsl:value-of select="$PART-NUMBER" disable-output-escaping="yes"/>
							</a>
						</xsl:if>

						<!-- Display Pages: {if IPN is not null} p IPN -->
						<xsl:if test="boolean(string-length(normalize-space($PAGES))>0)">
							<a CLASS="SmBlackText">
								, p <xsl:value-of select="$PAGES" disable-output-escaping="yes"/>
							</a>
						</xsl:if>
						</xsl:variable>

			<td valign="top">
			<a CLASS="MedBlackText">
			<xsl:value-of select="$SOURCE" disable-output-escaping="yes"/>
			</a>
			</td>

			<xsl:if test="boolean(string-length(normalize-space($SOURCE))>0)">
			<input type="hidden" name="source">
				<xsl:attribute name="value">
					<xsl:value-of select="$SOURCE"/>
				</xsl:attribute>
			</input>
			<input type="hidden" name="publication_date">
				<xsl:attribute name="value"></xsl:attribute>
			</input>
			</xsl:if>


			</xsl:when>

				<!-- Display Rules for Citaion Format when:
				 RTYPE=03 (monograph review)
				 RTYPE=04 (monograph chapter)
				 RTYPE=05 and FJT is null (conference proceeding not in journal)
				 RTYPE=06 and FJT is null (conference article not in journal)  -->
				<xsl:when test="boolean($DOCUMENT-TYPE ='03') or boolean($DOCUMENT-TYPE='04') or
									(boolean($DOCUMENT-TYPE ='05') and not(boolean(string-length(normalize-space($SERIAL-TITLE))>0))) or
									(boolean($DOCUMENT-TYPE ='06') and not(boolean(string-length(normalize-space($SERIAL-TITLE))>0)))">

					 <!-- Display Source: -->
					 <a CLASS="SmBlackText">
					 <td valign="top"><a CLASS="MedBlackText"><b>Source: </b></a></td>
					 </a>
					 <xsl:variable name="SOURCE">
						 <!-- Display Higher Level Publication Title: {if THLP is not null} THLP, PLEASE CHECK -->
						 <xsl:if test = "boolean(string-length(normalize-space($HIGH-LEVEL-PUB-TITLE))>0)">
							 <xsl:choose>
								<xsl:when test="boolean($DOCUMENT-TYPE = '04') or
												boolean($DOCUMENT-TYPE  = '06')">
									<a CLASS="SmBlackText">
										<i><xsl:value-of select="$HIGH-LEVEL-PUB-TITLE" disable-output-escaping="yes"/></i>
									</a>
								</xsl:when>
								<xsl:otherwise>
									<a CLASS="SmBlackText">
										<xsl:value-of select="$HIGH-LEVEL-PUB-TITLE" disable-output-escaping="yes"/>
									</a>
								</xsl:otherwise>
							 </xsl:choose>
						 </xsl:if>
						<!-- Display Publisher:  {if THLP is null and PUB is not null} PUB, PLEASE CHECK -->
						<xsl:if test="not(boolean(string-length(normalize-space($HIGH-LEVEL-PUB-TITLE))>0)) and
										  boolean(string-length(normalize-space($PUBLISHER-NAME))>0) and
										  (boolean($DOCUMENT-TYPE = '03') or
										  boolean($DOCUMENT-TYPE = '05'))">
							<a CLASS="SmBlackText">
								<xsl:value-of select="$PUBLISHER-NAME" disable-output-escaping="yes"/>
							</a>
						</xsl:if>

						 <!-- Display Publication Date: {if PDATE is not null} PDATE, -->
						<xsl:if test="boolean(string-length(normalize-space($PUBLICATION-DATE))>0)">
							<a CLASS="SmBlackText">
								, <xsl:value-of select="$PUBLICATION-DATE" disable-output-escaping="yes"/>
							</a>
						</xsl:if>

						<!-- Display Part Number: {if PARTNO is not null} PARTNO, -->
						<xsl:if test="boolean(string-length(normalize-space($PART-NUMBER))>0) and
									  (boolean($DOCUMENT-TYPE = '05') or
									  boolean($DOCUMENT-TYPE = '06'))">
							<a CLASS="SmBlackText">
							<xsl:text>, pt. </xsl:text><xsl:value-of select="$PART-NUMBER" disable-output-escaping="yes"/>
							</a>
						</xsl:if>

						<!-- Display Pages : {if IPN is not null} p IPN {else if PAGES1 is not null} PAGES1pp -->
						<xsl:choose>
							<xsl:when test="boolean(string-length(normalize-space($PAGES))>0)">
								<a CLASS="SmBlackText">
									, p <xsl:value-of select="$PAGES" disable-output-escaping="yes"/>
								</a>
							</xsl:when>
							<xsl:when test="boolean(string-length(normalize-space($PAGES1))>0)">
								<a CLASS="SmBlackText">
									, <xsl:value-of select="$PAGES1" disable-output-escaping="yes"/> pp
								</a>
							</xsl:when>
						</xsl:choose>
					</xsl:variable>


					<td valign="top">
					<a CLASS="MedBlackText">
					<xsl:value-of select="$SOURCE" disable-output-escaping="yes"/>
					</a>
					</td>


					<xsl:if test="boolean(string-length(normalize-space($SOURCE))>0)">
					<input type="hidden" name="source">
						<xsl:attribute name="value">
							<xsl:value-of select="$SOURCE"/>
						</xsl:attribute>
					</input>
					<input type="hidden" name="publication_date">
						<xsl:attribute name="value"></xsl:attribute>
					</input>
					</xsl:if>


				</xsl:when>

				<!-- Display Rules for Citaion Format when:
						RTYPE=10 (dissertation)
						RTYPE=11 (report review)
						RTYPE=12 (report chapter) -->
				<xsl:when test="boolean($DOCUMENT-TYPE='10') or boolean($DOCUMENT-TYPE='11') or boolean($DOCUMENT-TYPE='12')">

					 <!-- Display Source: -->
					 <a CLASS="SmBlackText">
					 <td valign="top"><a CLASS="MedBlackText"><b>Source: </b></a></td>
					 </a>

					 <xsl:variable name="SOURCE">
						 <!-- Display Higher Level Publication Title: {if THLP is not null} THLP -->
						 <xsl:if test="boolean(string-length(normalize-space($HIGH-LEVEL-PUB-TITLE))>0)">
							<a CLASS="SmBlackText">
								<i><xsl:value-of select="$HIGH-LEVEL-PUB-TITLE" disable-output-escaping="yes"/></i>0.
							</a>
						 </xsl:if>

						 <!-- Display Issuing Org.: {if IORG is not null} IORG, -->
						 <xsl:if test="boolean(string-length(normalize-space($ISSUING-ORG))>0)">
							<a CLASS="SmBlackText">
								<xsl:value-of select="$ISSUING-ORG" disable-output-escaping="yes"/>
							</a>
						 </xsl:if>

						 <!-- Display Publisher:  {if IORG is null and PUB is not null} PUB, -->
						 <xsl:if test="not(boolean(string-length(normalize-space($ISSUING-ORG))>0)) and
										   boolean(string-length(normalize-space($PUBLISHER-NAME))>0)">
							<a CLASS="SmBlackText">
								, <xsl:value-of select="$PUBLISHER-NAME" disable-output-escaping="yes"/>
							</a>
						 </xsl:if>

						 <!-- Display Report number: {if RNUM is not null} RNUM, -->
						 <xsl:if test="boolean(string-length(normalize-space($REPORT-NUMBER))>0)">
							<a CLASS="SmBlackText">
								, <xsl:value-of select="$REPORT-NUMBER" disable-output-escaping="yes"/>
							</a>
						 </xsl:if>

						 <!--Display  Publication Date / Filing Date: {if PDATE is not null} PDATE, {else if FDATE is not null} FDATE, -->
						 <xsl:choose>
							<xsl:when test="boolean(string-length(normalize-space($PUBLICATION-DATE))>0)">
								<a CLASS="SmBlackText">
									, <xsl:value-of select="$PUBLICATION-DATE" disable-output-escaping="yes"/>
								</a>
							</xsl:when>

							<xsl:when test="boolean(string-length(normalize-space($FILING-DATE))>0)">
								<a CLASS="SmBlackText">
									, <xsl:value-of select="$FILING-DATE" disable-output-escaping="yes"/>
								</a>
							</xsl:when>
						 </xsl:choose>

						<!-- Display Pages:
								{if IPN is not null} p IPN
								{else if PAGES1 is not null} PAGES1pp
								{else if PAGES2 is not null} PAGES2pp -->
						<xsl:choose>
							<xsl:when test="boolean(string-length(normalize-space($PAGES))>0)">
								<a CLASS="SmBlackText">
									, p <xsl:value-of select="$PAGES" disable-output-escaping="yes"/>
								</a>
							</xsl:when>
							<xsl:when test="boolean(string-length(normalize-space($PAGES1))>0)">
								<a CLASS="SmBlackText">
									, <xsl:value-of select="$PAGES1" disable-output-escaping="yes"/> pp
								</a>
								</xsl:when>
							<xsl:when test="boolean(string-length(normalize-space($PAGES2))>0)">
								<a CLASS="SmBlackText">
									, <xsl:value-of select="$PAGES2" disable-output-escaping="yes"/> pp
								</a>
							</xsl:when>
						</xsl:choose>
					</xsl:variable>


					<td valign="top">
					<a CLASS="MedBlackText">
					<xsl:value-of select="$SOURCE" disable-output-escaping="yes"/>
					</a>
					</td>


					<xsl:if test="boolean(string-length(normalize-space($SOURCE))>0)">
					<input type="hidden" name="source">
						<xsl:attribute name="value">
							<xsl:value-of select="$SOURCE"/>
						</xsl:attribute>
					</input>
					<input type="hidden" name="publication_date">
						<xsl:attribute name="value"></xsl:attribute>
					</input>
					</xsl:if>


			  </xsl:when>

				<!-- Display Rules for Citaion Format when RTYPE=08 (patent) -->
				<xsl:when test="boolean($DOCUMENT-TYPE='08')">
					<!-- Display Publication Date: Label - {only show if PDATE is not null}
					{if PDATE is not null} PDATE -->

					<xsl:if test="boolean(string-length(normalize-space($PUBLICATION-DATE))>0)">
						<td valign="top"><a CLASS="MedBlackText"><b>Publication date: </b></a></td>
							<td valign="top"><a CLASS="MedBlackText"><xsl:value-of select="$PUBLICATION-DATE" disable-output-escaping="yes"/></a></td>
					</xsl:if>

					<xsl:if test="boolean(string-length(normalize-space($PUBLICATION-DATE))>0)">
					<input type="hidden" name="publication_date">
						<xsl:attribute name="value">
							<xsl:value-of select="$PUBLICATION-DATE"/>
						</xsl:attribute>
					</input>
					<input type="hidden" name="source">
						<xsl:attribute name="value"></xsl:attribute>
					</input>
					</xsl:if>

				</xsl:when>
			</xsl:choose>
	</xsl:when>
</xsl:choose>
</tr>

<input type="hidden" name="shipping_value">
<xsl:attribute name="value">
    <xsl:value-of select="$SHIPPING-VALUE"/>
</xsl:attribute>
</input>

<input type="hidden" name="doc-id">
<xsl:attribute name="value">
    <xsl:value-of select="$DOC-ID"/>
</xsl:attribute>
</input>

<input type="hidden" name="database">
<xsl:attribute name="value">
    <xsl:value-of select="$DATABASE"/>
</xsl:attribute>
</input>


<xsl:if test="boolean(string-length(normalize-space($ISSN))>0)">
	<tr><td valign="top">
		<a CLASS="MedBlackText"><b>ISSN: </b></a>
	</td>
	<td valign="top">
	<a CLASS="MedBlackText">
		<xsl:value-of select="$ISSN"/></a>
	</td></tr>
</xsl:if>

<input type="hidden" name="issn">
<xsl:attribute name="value">
	<xsl:value-of select="$ISSN"/>
</xsl:attribute>
</input>

<xsl:if test="boolean(string-length(normalize-space($CODEN))>0)">
	<tr><td valign="top">
		<a CLASS="MedBlackText"><b>CODEN: </b></a>
		</td><td valign="top">
		<a CLASS="MedBlackText">
			<xsl:value-of select="$CODEN"/></a>
		</td></tr>
</xsl:if>

<input type="hidden" name="coden">
<xsl:attribute name="value">
	<xsl:value-of select="$CODEN"/>
</xsl:attribute>
</input>

<xsl:if test="boolean(string-length(normalize-space($ISBN))>0)">
	<tr><td valign="top">
		<a CLASS="MedBlackText"><b>ISBN: </b></a>
		</td><td valign="top">
		<a CLASS="MedBlackText">
			<xsl:value-of select="$ISBN"/></a>
		</td></tr>
</xsl:if>

<input type="hidden" name="isbn">
<xsl:attribute name="value">
	<xsl:value-of select="$ISBN"/>
</xsl:attribute>
</input>

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
<tr><td valign="top">
<table border="0" width="449" cellspacing="1" cellpadding="0" bgcolor="#FFFFFF">
<tr><td valign="top">

<table border="0" width="100%" cellspacing="0" cellpadding="0">
<xsl:choose>
<!--Based On DELIVERY-METHOD-NAME that field is Checked (here Email is checked)-->
<xsl:when test="($DELIVERY-METHOD-NAME='E-mail')">
		<tr><td valign="top" height="15" bgcolor="#3173b5" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" bgcolor="#3173B5" width="168"><a CLASS="LgWhiteText"><b>Delivery method (choose one)</b></a></td><td valign="top" bgcolor="#3173B5"><img src="/engresources/images/spacer.gif" border="0" height="1"/></td><td valign="top" width="68" bgcolor="#3173B5"><a CLASS="LgWhiteText">Cost</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><a CLASS="MedBlackText"><b>Electronic delivery:</b></a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" checked="true" value="E-mail"/><a CLASS="MedBlackText">E-mail - address required   <input type="text" name="deliveryEmail" size="15" onclick="document.delivery.method[0].checked=true">
		<xsl:attribute name="value"><xsl:value-of select="$DELIVERY-METHOD-VALUE"/></xsl:attribute>
		</input>
		</a></td><td valign="top"><a CLASS="MedBlackText">No charge</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="Ariel"/><a CLASS="MedBlackText">Ariel - address required      <input type="text" name="ariel" size="15" onclick="document.delivery.method[1].checked=true"/></a></td><td valign="top"><a CLASS="MedBlackText">No charge</a></td></tr>
		<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>

		<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><a CLASS="MedBlackText"><b>North America delivery:</b></a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="Fax"/><a CLASS="MedBlackText">Fax</a></td><td valign="top"><a CLASS="MedBlackText">No charge</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="First class mail"/><a CLASS="MedBlackText">First class mail</a></td><td valign="top"><a CLASS="MedBlackText">$3.00</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="FedEx 2-Day"/><a CLASS="MedBlackText">FedEx 2-Day (No charge if your FedEx account is used)</a></td><td valign="top"><a CLASS="MedBlackText">$4.00</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="FedEx Overnight"/><a CLASS="MedBlackText">FedEx Overnight (No charge if your FedEx account is used)</a></td><td valign="top"><a CLASS="MedBlackText">$5.00</a></td></tr>
		<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>

		<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><a CLASS="MedBlackText"><b>International delivery:</b></a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="International fax"/><a CLASS="MedBlackText">International fax</a></td><td valign="top"><a CLASS="MedBlackText">$5.00</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="Air mail"/><a CLASS="MedBlackText">Air mail</a></td><td valign="top"><a CLASS="MedBlackText">$3.00</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="FedEx International"/><a CLASS="MedBlackText">FedEx International (No charge if your FedEx account is used)</a></td><td valign="top"><a CLASS="MedBlackText">$20.00</a></td></tr>
		<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
</xsl:when>

<!--Based On DELIVERY-METHOD-NAME that field is Checked (here Fax is checked)-->
<xsl:when test="($DELIVERY-METHOD-NAME='Fax')">
		<tr><td valign="top" height="15" bgcolor="#3173b5" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" bgcolor="#3173B5" width="168"><a CLASS="LgWhiteText"><b>Delivery method (choose one)</b></a></td><td valign="top" bgcolor="#3173B5"><img src="/engresources/images/spacer.gif" border="0" height="1"/></td><td valign="top" width="68" bgcolor="#3173B5"><a CLASS="LgWhiteText">Cost</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><a CLASS="MedBlackText"><b>Electronic delivery:</b></a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="E-mail"/><a CLASS="MedBlackText">E-mail - address required   <input type="text" name="email" size="15" onclick="document.delivery.method[0].checked=true"/></a></td><td valign="top"><a CLASS="MedBlackText">No charge</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="Ariel"/><a CLASS="MedBlackText">Ariel - address required      <input type="text" name="ariel" size="15" onclick="document.delivery.method[1].checked=true"/></a></td><td valign="top"><a CLASS="MedBlackText">No charge</a></td></tr>
		<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>

		<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><a CLASS="MedBlackText"><b>North America delivery:</b></a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="Fax" checked="true"/><a CLASS="MedBlackText">Fax</a></td><td valign="top"><a CLASS="MedBlackText">No charge</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="First class mail"/><a CLASS="MedBlackText">First class mail</a></td><td valign="top"><a CLASS="MedBlackText">$3.00</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="FedEx 2-Day"/><a CLASS="MedBlackText">FedEx 2-Day (No charge if your FedEx account is used)</a></td><td valign="top"><a CLASS="MedBlackText">$4.00</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="FedEx Overnight"/><a CLASS="MedBlackText">FedEx Overnight (No charge if your FedEx account is used)</a></td><td valign="top"><a CLASS="MedBlackText">$5.00</a></td></tr>
		<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>

		<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><a CLASS="MedBlackText"><b>International delivery:</b></a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="International fax"/><a CLASS="MedBlackText">International fax</a></td><td valign="top"><a CLASS="MedBlackText">$5.00</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="Air mail"/><a CLASS="MedBlackText">Air mail</a></td><td valign="top"><a CLASS="MedBlackText">$3.00</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="FedEx International"/><a CLASS="MedBlackText">FedEx International (No charge if your FedEx account is used)</a></td><td valign="top"><a CLASS="MedBlackText">$20.00</a></td></tr>
		<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
</xsl:when>

<!--Based On DELIVERY-METHOD-NAME that field is Checked (here Ariel is checked)-->
<xsl:when test="($DELIVERY-METHOD-NAME='Ariel')">
		<tr><td valign="top" height="15" bgcolor="#3173b5" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" bgcolor="#3173B5" width="168"><a CLASS="LgWhiteText"><b>Delivery method (choose one)</b></a></td><td valign="top" bgcolor="#3173B5"><img src="/engresources/images/spacer.gif" border="0" height="1"/></td><td valign="top" width="68" bgcolor="#3173B5"><a CLASS="LgWhiteText">Cost</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><a CLASS="MedBlackText"><b>Electronic delivery:</b></a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="E-mail"/><a CLASS="MedBlackText">E-mail - address required   <input type="text" name="email" size="15" onclick="document.delivery.method[0].checked=true"/></a></td><td valign="top"><a CLASS="MedBlackText">No charge</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="Ariel" checked="true"/><a CLASS="MedBlackText">Ariel - address required      <input type="text" name="ariel" size="15" onclick="document.delivery.method[1].checked=true">
		<xsl:attribute name="value"><xsl:value-of select="$DELIVERY-METHOD-VALUE"/></xsl:attribute>
		</input>
		</a></td><td valign="top"><a CLASS="MedBlackText">No charge</a></td></tr>
		<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>

		<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><a CLASS="MedBlackText"><b>North America delivery:</b></a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="Fax"/><a CLASS="MedBlackText">Fax</a></td><td valign="top"><a CLASS="MedBlackText">No charge</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="First class mail"/><a CLASS="MedBlackText">First class mail</a></td><td valign="top"><a CLASS="MedBlackText">$3.00</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="FedEx 2-Day"/><a CLASS="MedBlackText">FedEx 2-Day (No charge if your FedEx account is used)</a></td><td valign="top"><a CLASS="MedBlackText">$4.00</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="FedEx Overnight"/><a CLASS="MedBlackText">FedEx Overnight (No charge if your FedEx account is used)</a></td><td valign="top"><a CLASS="MedBlackText">$5.00</a></td></tr>
		<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>

		<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><a CLASS="MedBlackText"><b>International delivery:</b></a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="International fax"/><a CLASS="MedBlackText">International fax</a></td><td valign="top"><a CLASS="MedBlackText">$5.00</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="Air mail"/><a CLASS="MedBlackText">Air mail</a></td><td valign="top"><a CLASS="MedBlackText">$3.00</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="FedEx International"/><a CLASS="MedBlackText">FedEx International (No charge if your FedEx account is used)</a></td><td valign="top"><a CLASS="MedBlackText">$20.00</a></td></tr>
		<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
</xsl:when>


<!--Based On DELIVERY-METHOD-NAME that field is Checked (here First Class mail is checked)-->
<xsl:when test="($DELIVERY-METHOD-NAME='First class mail')">
		<tr><td valign="top" height="15" bgcolor="#3173b5" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" bgcolor="#3173B5" width="168"><a CLASS="LgWhiteText"><b>Delivery method (choose one)</b></a></td><td valign="top" bgcolor="#3173B5"><img src="/engresources/images/spacer.gif" border="0" height="1"/></td><td valign="top" width="68" bgcolor="#3173B5"><a CLASS="LgWhiteText">Cost</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><a CLASS="MedBlackText"><b>Electronic delivery:</b></a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="E-mail"/><a CLASS="MedBlackText">E-mail - address required   <input type="text" name="email" size="15" onclick="document.delivery.method[0].checked=true"/>
		</a></td><td valign="top"><a CLASS="MedBlackText">No charge</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="Ariel"/><a CLASS="MedBlackText">Ariel - address required      <input type="text" name="ariel" size="15" onclick="document.delivery.method[1].checked=true"/></a></td><td valign="top"><a CLASS="MedBlackText">No charge</a></td></tr>
		<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>

		<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><a CLASS="MedBlackText"><b>North America delivery:</b></a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="Fax"/><a CLASS="MedBlackText">Fax</a></td><td valign="top"><a CLASS="MedBlackText">No charge</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="First class mail" checked="true"/><a CLASS="MedBlackText">First class mail</a></td><td valign="top"><a CLASS="MedBlackText">$3.00</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="FedEx 2-Day"/><a CLASS="MedBlackText">FedEx 2-Day (No charge if your FedEx account is used)</a></td><td valign="top"><a CLASS="MedBlackText">$4.00</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="FedEx Overnight"/><a CLASS="MedBlackText">FedEx Overnight (No charge if your FedEx account is used)</a></td><td valign="top"><a CLASS="MedBlackText">$5.00</a></td></tr>
		<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>

		<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><a CLASS="MedBlackText"><b>International delivery:</b></a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="International fax"/><a CLASS="MedBlackText">International fax</a></td><td valign="top"><a CLASS="MedBlackText">$5.00</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="Air mail"/><a CLASS="MedBlackText">Air mail</a></td><td valign="top"><a CLASS="MedBlackText">$3.00</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="FedEx International"/><a CLASS="MedBlackText">FedEx International (No charge if your FedEx account is used)</a></td><td valign="top"><a CLASS="MedBlackText">$20.00</a></td></tr>
		<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
</xsl:when>

<!--Based On DELIVERY-METHOD-NAME that field is Checked (here FedEx 2-day is checked)-->
<xsl:when test="($DELIVERY-METHOD-NAME='FedEx 2-Day')">
		<tr><td valign="top" height="15" bgcolor="#3173b5" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" bgcolor="#3173B5" width="168"><a CLASS="LgWhiteText"><b>Delivery method (choose one)</b></a></td><td valign="top" bgcolor="#3173B5"><img src="/engresources/images/spacer.gif" border="0" height="1"/></td><td valign="top" width="68" bgcolor="#3173B5"><a CLASS="LgWhiteText">Cost</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><a CLASS="MedBlackText"><b>Electronic delivery:</b></a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="E-mail"/><a CLASS="MedBlackText">E-mail - address required   <input type="text" name="email" size="15" onclick="document.delivery.method[0].checked=true"/></a></td><td valign="top"><a CLASS="MedBlackText">No charge</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="Ariel"/><a CLASS="MedBlackText">Ariel - address required      <input type="text" name="ariel" size="15" onclick="document.delivery.method[1].checked=true"/></a></td><td valign="top"><a CLASS="MedBlackText">No charge</a></td></tr>
		<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>

		<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><a CLASS="MedBlackText"><b>North America delivery:</b></a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="Fax"/><a CLASS="MedBlackText">Fax</a></td><td valign="top"><a CLASS="MedBlackText">No charge</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="First class mail"/><a CLASS="MedBlackText">First class mail</a></td><td valign="top"><a CLASS="MedBlackText">$3.00</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="FedEx 2-Day" checked="true"/><a CLASS="MedBlackText">FedEx 2-Day (No charge if your FedEx account is used)</a></td><td valign="top"><a CLASS="MedBlackText">$4.00</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="FedEx Overnight"/><a CLASS="MedBlackText">FedEx Overnight (No charge if your FedEx account is used)</a></td><td valign="top"><a CLASS="MedBlackText">$5.00</a></td></tr>
		<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>

		<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><a CLASS="MedBlackText"><b>International delivery:</b></a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="International fax"/><a CLASS="MedBlackText">International fax</a></td><td valign="top"><a CLASS="MedBlackText">$5.00</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="Air mail"/><a CLASS="MedBlackText">Air mail</a></td><td valign="top"><a CLASS="MedBlackText">$3.00</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="FedEx International"/><a CLASS="MedBlackText">FedEx International (No charge if your FedEx account is used)</a></td><td valign="top"><a CLASS="MedBlackText">$20.00</a></td></tr>
		<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
</xsl:when>


<!--Based On DELIVERY-METHOD-NAME that field is Checked (here FedEx Overnight is checked)-->
<xsl:when test="($DELIVERY-METHOD-NAME='FedEx Overnight')">
		<tr><td valign="top" height="15" bgcolor="#3173b5" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" bgcolor="#3173B5" width="168"><a CLASS="LgWhiteText"><b>Delivery method (choose one)</b></a></td><td valign="top" bgcolor="#3173B5"><img src="/engresources/images/spacer.gif" border="0" height="1"/></td><td valign="top" width="68" bgcolor="#3173B5"><a CLASS="LgWhiteText">Cost</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><a CLASS="MedBlackText"><b>Electronic delivery:</b></a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="E-mail"/><a CLASS="MedBlackText">E-mail - address required   <input type="text" name="email" size="15" onclick="document.delivery.method[0].checked=true"/>
		</a></td><td valign="top"><a CLASS="MedBlackText">No charge</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="Ariel"/><a CLASS="MedBlackText">Ariel - address required      <input type="text" name="ariel" size="15" onclick="document.delivery.method[1].checked=true"/></a></td><td valign="top"><a CLASS="MedBlackText">No charge</a></td></tr>
		<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>

		<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><a CLASS="MedBlackText"><b>North America delivery:</b></a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="Fax"/><a CLASS="MedBlackText">Fax</a></td><td valign="top"><a CLASS="MedBlackText">No charge</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="First class mail"/><a CLASS="MedBlackText">First class mail</a></td><td valign="top"><a CLASS="MedBlackText">$3.00</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="FedEx 2-Day"/><a CLASS="MedBlackText">FedEx 2-Day (No charge if your FedEx account is used)</a></td><td valign="top"><a CLASS="MedBlackText">$4.00</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="FedEx Overnight" checked="true"/><a CLASS="MedBlackText">FedEx Overnight (No charge if your FedEx account is used)</a></td><td valign="top"><a CLASS="MedBlackText">$5.00</a></td></tr>
		<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>

		<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><a CLASS="MedBlackText"><b>International delivery:</b></a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="International fax"/><a CLASS="MedBlackText">International fax</a></td><td valign="top"><a CLASS="MedBlackText">$5.00</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="Air mail"/><a CLASS="MedBlackText">Air mail</a></td><td valign="top"><a CLASS="MedBlackText">$3.00</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="FedEx International"/><a CLASS="MedBlackText">FedEx International (No charge if your FedEx account is used)</a></td><td valign="top"><a CLASS="MedBlackText">$20.00</a></td></tr>
		<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
</xsl:when>


<!--Based On DELIVERY-METHOD-NAME that field is Checked (here International Fax is checked)-->
<xsl:when test="($DELIVERY-METHOD-NAME='International fax')">
		<tr><td valign="top" height="15" bgcolor="#3173b5" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" bgcolor="#3173B5" width="168"><a CLASS="LgWhiteText"><b>Delivery method (choose one)</b></a></td><td valign="top" bgcolor="#3173B5"><img src="/engresources/images/spacer.gif" border="0" height="1"/></td><td valign="top" width="68" bgcolor="#3173B5"><a CLASS="LgWhiteText">Cost</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><a CLASS="MedBlackText"><b>Electronic delivery:</b></a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="E-mail"/><a CLASS="MedBlackText">E-mail - address required   <input type="text" name="email" size="15" onclick="document.delivery.method[0].checked=true"/></a></td><td valign="top"><a CLASS="MedBlackText">No charge</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="Ariel"/><a CLASS="MedBlackText">Ariel - address required      <input type="text" name="ariel" size="15" onclick="document.delivery.method[1].checked=true"/></a></td><td valign="top"><a CLASS="MedBlackText">No charge</a></td></tr>
		<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>

		<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><a CLASS="MedBlackText"><b>North America delivery:</b></a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="Fax"/><a CLASS="MedBlackText">Fax</a></td><td valign="top"><a CLASS="MedBlackText">No charge</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="First class mail"/><a CLASS="MedBlackText">First class mail</a></td><td valign="top"><a CLASS="MedBlackText">$3.00</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="FedEx 2-Day"/><a CLASS="MedBlackText">FedEx 2-Day (No charge if your FedEx account is used)</a></td><td valign="top"><a CLASS="MedBlackText">$4.00</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="FedEx Overnight"/><a CLASS="MedBlackText">FedEx Overnight (No charge if your FedEx account is used)</a></td><td valign="top"><a CLASS="MedBlackText">$5.00</a></td></tr>
		<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>

		<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><a CLASS="MedBlackText"><b>International delivery:</b></a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="International fax" checked="true"/><a CLASS="MedBlackText">International fax</a></td><td valign="top"><a CLASS="MedBlackText">$5.00</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="Air mail"/><a CLASS="MedBlackText">Air mail</a></td><td valign="top"><a CLASS="MedBlackText">$3.00</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="FedEx International"/><a CLASS="MedBlackText">FedEx International (No charge if your FedEx account is used)</a></td><td valign="top"><a CLASS="MedBlackText">$20.00</a></td></tr>
		<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
</xsl:when>


<!--Based On DELIVERY-METHOD-NAME that field is Checked (here air Mail is checked)-->
<xsl:when test="($DELIVERY-METHOD-NAME='Air mail')">
		<tr><td valign="top" height="15" bgcolor="#3173b5" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" bgcolor="#3173B5" width="168"><a CLASS="LgWhiteText"><b>Delivery method (choose one)</b></a></td><td valign="top" bgcolor="#3173B5"><img src="/engresources/images/spacer.gif" border="0" height="1"/></td><td valign="top" width="68" bgcolor="#3173B5"><a CLASS="LgWhiteText">Cost</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><a CLASS="MedBlackText"><b>Electronic delivery:</b></a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="E-mail"/><a CLASS="MedBlackText">E-mail - address required   <input type="text" name="email" size="15" onclick="document.delivery.method[0].checked=true"/></a></td><td valign="top"><a CLASS="MedBlackText">No charge</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="Ariel"/><a CLASS="MedBlackText">Ariel - address required      <input type="text" name="ariel" size="15" onclick="document.delivery.method[1].checked=true"/></a></td><td valign="top"><a CLASS="MedBlackText">No charge</a></td></tr>
		<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>

		<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><a CLASS="MedBlackText"><b>North America delivery:</b></a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="Fax"/><a CLASS="MedBlackText">Fax</a></td><td valign="top"><a CLASS="MedBlackText">No charge</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="First class mail"/><a CLASS="MedBlackText">First class mail</a></td><td valign="top"><a CLASS="MedBlackText">$3.00</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="FedEx 2-Day"/><a CLASS="MedBlackText">FedEx 2-Day (No charge if your FedEx account is used)</a></td><td valign="top"><a CLASS="MedBlackText">$4.00</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="FedEx Overnight"/><a CLASS="MedBlackText">FedEx Overnight (No charge if your FedEx account is used)</a></td><td valign="top"><a CLASS="MedBlackText">$5.00</a></td></tr>
		<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>

		<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><a CLASS="MedBlackText"><b>International delivery:</b></a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="International fax"/><a CLASS="MedBlackText">International fax</a></td><td valign="top"><a CLASS="MedBlackText">$5.00</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="Air mail" checked="true"/><a CLASS="MedBlackText">Air mail</a></td><td valign="top"><a CLASS="MedBlackText">$3.00</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="FedEx International"/><a CLASS="MedBlackText">FedEx International (No charge if your FedEx account is used)</a></td><td valign="top"><a CLASS="MedBlackText">$20.00</a></td></tr>
		<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
</xsl:when>


<!--Based On DELIVERY-METHOD-NAME that field is Checked (here FedEx International is checked)-->
<xsl:when test="($DELIVERY-METHOD-NAME='FedEx International')">
		<tr><td valign="top" height="15" bgcolor="#3173b5" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" bgcolor="#3173B5" width="168"><a CLASS="LgWhiteText"><b>Delivery method (choose one)</b></a></td><td valign="top" bgcolor="#3173B5"><img src="/engresources/images/spacer.gif" border="0" height="1"/></td><td valign="top" width="68" bgcolor="#3173B5"><a CLASS="LgWhiteText">Cost</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><a CLASS="MedBlackText"><b>Electronic delivery:</b></a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="E-mail"/><a CLASS="MedBlackText">E-mail - address required   <input type="text" name="email" size="15" onclick="document.delivery.method[0].checked=true"/></a></td><td valign="top"><a CLASS="MedBlackText">No charge</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="Ariel"/><a CLASS="MedBlackText">Ariel - address required      <input type="text" name="ariel" size="15" onclick="document.delivery.method[1].checked=true"/></a></td><td valign="top"><a CLASS="MedBlackText">No charge</a></td></tr>
		<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>

		<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><a CLASS="MedBlackText"><b>North America delivery:</b></a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="Fax"/><a CLASS="MedBlackText">Fax</a></td><td valign="top"><a CLASS="MedBlackText">No charge</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="First class mail"/><a CLASS="MedBlackText">First class mail</a></td><td valign="top"><a CLASS="MedBlackText">$3.00</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="FedEx 2-Day"/><a CLASS="MedBlackText">FedEx 2-Day (No charge if your FedEx account is used)</a></td><td valign="top"><a CLASS="MedBlackText">$4.00</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="FedEx Overnight"/><a CLASS="MedBlackText">FedEx Overnight (No charge if your FedEx account is used)</a></td><td valign="top"><a CLASS="MedBlackText">$5.00</a></td></tr>
		<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>

		<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><a CLASS="MedBlackText"><b>International delivery:</b></a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="International fax"/><a CLASS="MedBlackText">International fax</a></td><td valign="top"><a CLASS="MedBlackText">$5.00</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="Air mail"/><a CLASS="MedBlackText">Air mail</a></td><td valign="top"><a CLASS="MedBlackText">$3.00</a></td></tr>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="method" value="FedEx International" checked="true"/><a CLASS="MedBlackText">FedEx International (No charge if your FedEx account is used)</a></td><td valign="top"><a CLASS="MedBlackText">$20.00</a></td></tr>
		<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
</xsl:when>

</xsl:choose>
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
<!--Based On SERVICE-LEVEL that field is Checked -->
<xsl:choose>
    <!--Based On SERVICE-LEVEL that field is Checked (Here regular service is checked)-->
	<xsl:when test="($SERVICE-LEVEL='Regular service')">
	<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="service" value="Regular service" checked="true"/><a CLASS="MedBlackText">Regular service: within 24-48 hours</a></td><td valign="top"><a CLASS="MedBlackText">No charge</a></td></tr>
	<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="service" value="Rush service"/><a CLASS="MedBlackText">Rush service: within 6 working hours</a></td><td valign="top"><a CLASS="MedBlackText">$6.00</a></td></tr>
	<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="service" value="Super rush service"/><a CLASS="MedBlackText">Super rush service: within 3 working hours</a></td><td valign="top"><a CLASS="MedBlackText">$12.00</a></td></tr>
	<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="service" value="Drop everything service"/><a CLASS="MedBlackText">Drop everything service: within 1 working hour</a></td><td valign="top"><a CLASS="MedBlackText">$100.00</a></td></tr>
	</xsl:when>
	<!--Based On SERVICE-LEVEL that field is Checked (Here rush service is checked)-->
	<xsl:when test="($SERVICE-LEVEL='Rush service')">
	<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="service" value="Regular service" /><a CLASS="MedBlackText">Regular service: within 24-48 hours</a></td><td valign="top"><a CLASS="MedBlackText">No charge</a></td></tr>
	<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="service" value="Rush service" checked="true"/><a CLASS="MedBlackText">Rush service: within 6 working hours</a></td><td valign="top"><a CLASS="MedBlackText">$6.00</a></td></tr>
	<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="service" value="Super rush service"/><a CLASS="MedBlackText">Super rush service: within 3 working hours</a></td><td valign="top"><a CLASS="MedBlackText">$12.00</a></td></tr>
	<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="service" value="Drop everything service"/><a CLASS="MedBlackText">Drop everything service: within 1 working hour</a></td><td valign="top"><a CLASS="MedBlackText">$100.00</a></td></tr>
	</xsl:when>
	<!--Based On SERVICE-LEVEL that field is Checked (Here Super rush service is checked)-->
	<xsl:when test="($SERVICE-LEVEL='Super rush service')">
	<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="service" value="Regular service"/><a CLASS="MedBlackText">Regular service: within 24-48 hours</a></td><td valign="top"><a CLASS="MedBlackText">No charge</a></td></tr>
	<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="service" value="Rush service"/><a CLASS="MedBlackText">Rush service: within 6 working hours</a></td><td valign="top"><a CLASS="MedBlackText">$6.00</a></td></tr>
	<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="service" value="Super rush service" checked="true"/><a CLASS="MedBlackText">Super rush service: within 3 working hours</a></td><td valign="top"><a CLASS="MedBlackText">$12.00</a></td></tr>
	<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="service" value="Drop everything service"/><a CLASS="MedBlackText">Drop everything service: within 1 working hour</a></td><td valign="top"><a CLASS="MedBlackText">$100.00</a></td></tr>
	</xsl:when>
	<!--Based On SERVICE-LEVEL that field is Checked (Here Drop everything service is checked)-->
	<xsl:when test="($SERVICE-LEVEL='Drop everything service')">
	<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="service" value="Regular service"/><a CLASS="MedBlackText">Regular service: within 24-48 hours</a></td><td valign="top"><a CLASS="MedBlackText">No charge</a></td></tr>
	<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="service" value="Rush service"/><a CLASS="MedBlackText">Rush service: within 6 working hours</a></td><td valign="top"><a CLASS="MedBlackText">$6.00</a></td></tr>
	<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="service" value="Super rush service"/><a CLASS="MedBlackText">Super rush service: within 3 working hours</a></td><td valign="top"><a CLASS="MedBlackText">$12.00</a></td></tr>
	<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><input type="radio" name="service" value="Drop everything service" checked="true"/><a CLASS="MedBlackText">Drop everything service: within 1 working hour</a></td><td valign="top"><a CLASS="MedBlackText">$100.00</a></td></tr>
	</xsl:when>
</xsl:choose>


<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><a CLASS="MedBlackText"><b>Shipping information</b></a></td></tr>
<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><a CLASS="MedBlackText">Send to the attention of:  <input type="text" name="shipping" size="15">
<xsl:attribute name="value"><xsl:value-of select="$ATTENTION"/></xsl:attribute>
</input>
</a></td><td valign="top">  </td></tr>

<xsl:choose>
	<xsl:when test="(LHL-USER-INFO/SHIPPING-VALUE='d')">
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><a CLASS="MedBlackText">E-mail address for order confirmation:  <input type="text" name="confirmationEmail" size="15"><xsl:attribute name="value"><xsl:value-of select="$EMAIL-CONFIRM"/></xsl:attribute></input></a></td><td valign="top"> </td></tr>
	</xsl:when>
	<xsl:otherwise>
		<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><a CLASS="MedBlackText">E-mail address for order confirmation:  <xsl:value-of select="$EMAIL-CONFIRM"/></a></td><td valign="top">  </td></tr>
	</xsl:otherwise>
</xsl:choose>

<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
<tr><td valign="top" colspan="4" height="5"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="2"><a CLASS="MedBlackText"><b>Ship to:</b></a></td></tr>
<tr><td valign="top" height="15" bgcolor="#FFFFFF" width="4"><img src="/engresources/images/spacer.gif" border="0" width="4"/></td><td valign="top" colspan="3">
<!--Based On shipping ie if Decentralised Editable fields are displayed else Non editable Data is Displayed-->

<xsl:choose>
   <xsl:when test="(LHL-USER-INFO/SHIPPING-VALUE='d')">
	<!--Shipping address equals d DISPLAY THIS -->
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
	<td valign="top"><input type="text" name="address1" size="15">
	<xsl:attribute name="value"><xsl:value-of select="$ADDRESS1"/></xsl:attribute>
	</input></td>
	<td valign="middle"><a CLASS="MedBlackText">Address2: </a></td>
	<td valign="top"><input type="text" name="address2" size="15">
	<xsl:attribute name="value"><xsl:value-of select="$ADDRESS2"/></xsl:attribute>
	</input>
	</td>
	</tr>


	<tr>
	<td valign="middle"><a CLASS="MedBlackText">City: </a></td>
	<td valign="top"><input type="text" name="city" size="10">
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
	<xsl:attribute name="value"><xsl:value-of select="$ZIP"/>
	</xsl:attribute>
	</input>
	</td>
	</tr>


	<tr>
	<td valign="middle"><a CLASS="MedBlackText">Telephone: </a></td>
	<td valign="top"><input type="text" name="phone" size="10">
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
	<td valign="middle">

	</td>
	<td valign="top">

	</td>
	<td valign="middle" colspan="2"><a CLASS="MedBlackText">
	<input type="submit" name="submit" value="Submit" />  <input type="button" name="close" value="Close" onclick="javascript:window.close();"/></a>

	</td>
	</tr>
	</table>
</xsl:when>
<!--Shipping address equals c DISPLAY THIS -->
<xsl:when test="(LHL-USER-INFO/SHIPPING-VALUE='c')">
		<input type="hidden" name="firstname">
		<xsl:attribute name="value">
			<xsl:value-of select="$FIRST-NAME"/>
		</xsl:attribute>
		</input>

		<input type="hidden" name="lastname">
		<xsl:attribute name="value">
			<xsl:value-of select="$LAST-NAME"/>
		</xsl:attribute>
		</input>

		<input type="hidden" name="companyname">
		<xsl:attribute name="value">
			<xsl:value-of select="$COMPANY-NAME"/>
		</xsl:attribute>
		</input>

		<input type="hidden" name="address1">
		<xsl:attribute name="value">
			<xsl:value-of select="$ADDRESS1"/>
		</xsl:attribute>
		</input>

		<input type="hidden" name="address2">
		<xsl:attribute name="value">
			<xsl:value-of select="$ADDRESS2"/>
		</xsl:attribute>
		</input>

		<input type="hidden" name="city">
		<xsl:attribute name="value">
			<xsl:value-of select="$CITY"/>
		</xsl:attribute>
		</input>

		<input type="hidden" name="state">
		<xsl:attribute name="value">
			<xsl:value-of select="$STATE"/>
		</xsl:attribute>
		</input>

		<input type="hidden" name="country">
		<xsl:attribute name="value">
			<xsl:value-of select="$COUNTRY"/>
		</xsl:attribute>
		</input>

		<input type="hidden" name="zip">
		<xsl:attribute name="value">
			<xsl:value-of select="$ZIP"/>
		</xsl:attribute>
		</input>

		<input type="hidden" name="fax">
		<xsl:attribute name="value">
			<xsl:value-of select="$FAX"/>
		</xsl:attribute>
		</input>

		<input type="hidden" name="confirmationEmail">
		<xsl:attribute name="value">
			<xsl:value-of select="$EMAIL-CONFIRM"/>
		</xsl:attribute>
		</input>

		<input type="hidden" name="phone">
		<xsl:attribute name="value">
			<xsl:value-of select="$TELEPHONE"/>
		</xsl:attribute>
		</input>

		<table border="0" cellspacing="0" cellpadding="0" width="100%">
		<a CLASS="MedBlackText"><xsl:value-of select="$FIRST-NAME"/> <xsl:value-of select="$LAST-NAME"/><br/>
		<xsl:value-of select="$COMPANY-NAME"/><br/>
		<xsl:value-of select="$ADDRESS1"/> <xsl:value-of select="$ADDRESS2"/><br/>
		<xsl:value-of select="$CITY"/>, <xsl:value-of select="$STATE"/> <xsl:value-of select="$ZIP"/> <xsl:value-of select="$COUNTRY"/><br/>
		<xsl:value-of select="$TELEPHONE"/><br/>
		<xsl:value-of select="$FAX"/><br/>
		</a>

		<tr><td valign="top" colspan="3" height="4"><img src="/engresources/images/spacer.gif" border="0" height="4"/></td></tr>
		<tr><td valign="top" colspan="3">
		<a CLASS="MedBlackText"><input type="submit" name="submit" value="Submit"/>  <input type="button" name="close" value="Close" onclick="javascript:window.close();"/></a>
		</td></tr>
		</table>
</xsl:when>
</xsl:choose>

</td>
</tr>
</table>

</td></tr>
</table>
</td></tr>
</table>

</FORM>

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
