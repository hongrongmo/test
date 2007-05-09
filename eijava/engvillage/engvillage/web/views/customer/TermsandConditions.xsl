<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:html="http://www.w3.org/TR/REC-html40"
	exclude-result-prefixes="xsl html"
>

<xsl:include href="Header.xsl"/>
<xsl:include href="GlobalLinks.xsl"/>
<xsl:include href="Footer.xsl"/>

<xsl:template match="PAGE">

<xsl:variable name="SESSION-ID">
	<xsl:value-of select="SESSION-ID"/>
</xsl:variable>

<xsl:variable name="DBMASK">
	<xsl:value-of select="/PAGE/DBMASK"/>
</xsl:variable>

<html>

<head>
	<SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js"/>
	<title>About Engineering Village</title>

</head>

<body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">

<center>

<table border="0" width="100%" cellspacing="0" cellpadding="0">
	<tr><td valign="top">
        <xsl:apply-templates select="HEADER"/>
	</td></tr>
	<tr><td valign="top">
		<!-- Insert the Global Link table -->
		<xsl:apply-templates select="GLOBAL-LINKS">
			<xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
		 	<xsl:with-param name="SELECTED-DB" select="$DBMASK"/>
		</xsl:apply-templates>

	</td></tr>
</table>
<table border="0" width="99%" cellspacing="0" cellpadding="0">
	<tr><td valign="top" colspan="10" height="20" bgcolor="#C3C8D1"><img src="/engresources/images/s.gif" border="0"/></td></tr>
	<tr><td valign="top" colspan="10" height="20"><img src="/engresources/images/s.gif" border="0"/></td></tr>
</table>

</center>


<center>
<table border="0" width="99%" cellspacing="0" cellpadding="0">
<tr><td valign="top"><a class="EvHeaderText">Terms and Conditions</a></td></tr>
<tr><td valign="top" height="10"><img src="/engresources/images/s.gif" border="0" height="10"/></td></tr>
<tr><td valign="top"> <a CLASS="MedBlackText">This web site ("Site") is owned and operated by Elsevier Inc., 360 Park Avenue South, New York, NY 10010, USA ("Elsevier", "we,"us" or "our").<br/><br/></a>
<a CLASS="MedBlackText">By accessing or using the Site, you agree to be bound by the terms and conditions below ("Terms and Conditions").These Terms and Conditions expressly incorporate by reference and include the Site's privacy policy and any guidelines,rules or disclaimers that may be posted and updated on specific webpages or on notices that are sent to you.If you do not agree with these Terms and Conditions, please do not use this site.</a><br/><br/>
<a class="MedBlackText">Elsevier reserves the right to change, modify, add or remove portions of these Terms and Conditions in its sole discretion at any time and without prior notice. Please check this page periodically for any modifications. Your continued use of this Site following the posting of any changes will mean that you have accepted the changes.</a><br/><br/>
<a class="MedBlackText"><b>Copyrights and Limitations on Use </b><br/><br/></a>
<a class="MedBlackText">All content in this Site, including site layout, design, images, programs, text and other information (collectively, the "Content") is the property of Elsevier and its affiliated companies or licensors and is protected by copyright and other intellectual property laws.</a><br/><br/>
<a class="MedBlackText">You may not copy, display, distribute, modify, publish, reproduce, store, transmit, create derivative works from, or sell or license all or any part of the Content, products or services obtained from this Site in any medium to anyone, except as otherwise expressly permitted under applicable law or as described in these Terms and Conditions or relevant license or subscriber agreement.</a><br/><br/>
<a class="MedBlackText">You may print or download Content from the Site for your own personal, non-commercial use, provided that you keep intact all copyright and other proprietary notices. You may not engage in systematic retrieval of Content from the Site to create or compile, directly or indirectly, a collection, compilation, database or directory without prior written permission from Elsevier.</a><br/><br/>
<a class="MedBlackText">You agree that you will not use any robots, spiders, crawlers or other automated downloading programs or devices to: (i) continuously and automatically search or index any Content, unless authorized by us; (ii) harvest personal information from the Site for purposes of sending unsolicited or unauthorized material; or (iii) cause disruption to the working of the Site. If this Site contains robot exclusion files or robot exclusion headers, you agree to honor them. Any questions about whether a particular use is authorized and any requests for permission to publish, reproduce, distribute, display or make derivative works from any Content should be directed to </a><a class="LgBLueLink" HREF="#" onClick="window.open('http://www.elsevier.com/wps/find/supportfaq.cws_home/permissionusematerial', 'newpg', 'status=yes,resizable,scrollbars=yes,menubar=yes,location=yes,directories=yes,width=700,height=450')">Elsevier Global Rights.</a><br/><br/>
<a class="MedBlackText">You may not use the services on the Site to publish or distribute any information (including software or other content) which is illegal, which violates or infringes upon the rights of any other person, which is abusive, hateful, profane, pornographic, threatening or vulgar, which contains errors, viruses or other harmful components, or which is otherwise actionable at law. Elsevier may at any time exercise editorial control over the content of any information or material that is submitted or distributed through its facilities and/or services.</a><br/><br/>
<a class="MedBlackText">You may not, without the approval of Elsevier, use the Site to publish or distribute any advertising, promotional material, or solicitation to other users of the Site to use any goods or services. For example (but without limitation), you may not use the Site to conduct any business, to solicit the performance of any activity that is prohibited by law, or to solicit other users to become subscribers of other information services. Similarly, you may not use the Site to download and redistribute public information or shareware for personal gain or use the facilities and/or services to distribute multiple copies of public domain information or shareware.</a><br/><br/>
<a class="MedBlackText"><b>Trademarks</b><br/><br/>
All trademarks appearing on this Site are the property of their respective owners.</a><br/><br/>
<a class="MedBlackText"><b>Links to Other Sites</b><br/><br/>
The Site may contain hyperlinks to other sites or resources that are provided solely for your convenience. Elsevier is not responsible for the availability of external sites or resources linked to the Site, and does not endorse and is not responsible or liable for any content, advertising, products or other materials on or available from such sites or resources. Transactions that occur between you and any third party are strictly between you and the third party and are not the responsibility of Elsevier. Because Elsevier is not responsible for the availability or accuracy of these outside resources or their contents, you should review the terms and conditions and privacy policies of these linked sites, as their policies may differ from ours. </a><br/><br/>

<a class="MedBlackText"><b>Disclaimer of Warranties and Liability</b><br/><br/>
Neither Elsevier, its affiliates, nor any third-party content providers or licensors makes any warranty whatsoever, including without limitation, that the operation of the Site will be uninterrupted or error-free; that defects will be corrected; that this Site, including the server that makes it available, is free of viruses or other harmful components; as to the results that may be obtained from use of the Content or other materials on the Site; or as to the accuracy, completeness, reliability, availability, suitability, quality, non-infringement or operation of any Content, product or service provided on or accessible from the Site.</a><br/><br/>
<a class="MedBlackText">THIS SITE AND ALL CONTENT, PRODUCTS AND SERVICES INCLUDED IN OR ACCESSIBLE FROM THIS SITE ARE PROVIDED "AS IS" AND WITHOUT WARRANTIES OR REPRESENTATIONS OF ANY KIND (EXPRESS, IMPLIED AND STATUTORY, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF TITLE AND NONINFRINGEMENT AND THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE), ALL OF WHICH NAME DISCLAIMS TO THE FULLEST EXTENT PERMITTED BY LAW. YOUR USE OF THE SITE IS AT YOUR SOLE RISK.</a><br/><br/>
<a class="MedBlackText">To the extent permissible under applicable laws, no responsibility is assumed for any injury and/or damage to persons, animals or property as a matter of products liability, negligence or otherwise, or from any use or operation of any ideas, instructions, methods, products or procedures contained in the Site. If the Content contained on this site contains medical or health sciences information, it is intended for professional use within the medical field. No suggested test or procedure should be carried out unless, in the reader's judgment, its risk is justified. Because of rapid advances in the medical sciences, we recommend that the independent verification of diagnoses and drug dosages should be made. Discussions, views, and recommendations as to medical procedures, products, choice of drugs, and drug dosages are the responsibility of the authors.</a><br/><br/>
<a class="MedBlackText">NEITHER ELSEVIER NOR ANY OF ITS AFFILIATES OR LICENSORS SHALL BE LIABLE TO YOU OR ANYONE ELSE FOR ANY LOSS OR INJURY, CAUSED IN WHOLE OR PART BY ITS NEGLIGENCE OR CONTINGENCIES BEYOND ITS CONTROL IN PROCURING, COMPILING, INTERPRETING, REPORTING OR DELIVERING INFORMATION THROUGH THE SITE. IN NO EVENT WILL ELSEVIER, ITS AFFILIATES OR LICENSORS BE LIABLE TO YOU OR ANYONE ELSE FOR ANY DECISION MADE OR ACTION TAKEN BY YOU IN RELIANCE ON SUCH INFORMATION. ELSEVIER AND ITS AFFILIATES AND LICENSORS SHALL NOT BE LIABLE TO YOU OR ANYONE ELSE FOR ANY DAMAGES (INCLUDING, WITHOUT LIMITATION, CONSEQUENTIAL, SPECIAL, INCIDENTAL, INDIRECT, OR SIMILAR DAMAGES) EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.</a><br/><br/>
<a class="MedBlackText">Elsevier neither endorses nor takes responsibility for any products, goods or services offered by outside vendors through our services or advertised on our system.</a><br/><br/>


<a class="MedBlackText"><b>Systems Reliability</b><br/><br/>
Elsevier aims to keep the Site available twenty-four (24) hours a day, seven (7) days a week and to maintain saved information. However, due to technical failures, acts of God or routine maintenance, availability may be limited and/or information may be lost. Elsevier shall not be liable for lost information or non-availability of the services. </a><br/><br/>

<a class="MedBlackText"><b>Indemnification</b><br/><br/>
You hereby agree to indemnify, defend and hold Elsevier, its directors, officers, shareholders, parents, subsidiaries, affiliates, agents and licensors harmless from and against any and all liability, losses, damages and costs, including, without limitation, reasonable attorneys' fees, arising from your use of the Site or Content. </a><br/><br/>

<a class="MedBlackText"><b>Export Control</b><br/><br/>
You acknowledge that U.S. export control laws and regulations apply to this site, including the Export Administration Regulations of the U.S. Department of Commerce, which prohibit the export or re-export of products and technology to certain countries and persons. You agree to comply strictly with all U.S. export laws, regulations and orders, and assume sole responsibility for obtaining any required licenses to export or re-export products or technology.</a><br/><br/>


<a class="MedBlackText"><b>Governing Law and Venue</b><br/><br/>
These terms and conditions shall be governed by and construed in accordance with the laws of the State of New York, without regard to its conflicts of law principles. You hereby submit to and agree that the sole jurisdiction and venue for any actions that may arise under or in relation to the subject matter hereof shall be the courts located in the State of New York.</a><br/><br/>
</td></tr> 
</table>
</center>


<br/>

<!-- end of the lower area below the navigation bar -->
<xsl:apply-templates select="FOOTER">
		<xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
		<xsl:with-param name="SELECTED-DB" select="$DBMASK"/>
</xsl:apply-templates>

</body>
</html>
</xsl:template>

</xsl:stylesheet>