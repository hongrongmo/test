<?xml version="1.0"?>

<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:java="java:java.net.URLEncoder"
  xmlns:html="http://www.w3.org/TR/REC-html40"
  exclude-result-prefixes="java html xsl"
>

  <xsl:output method="html" indent="no" doctype-public="-//W3C//DTD HTML 4.01 Transitional//EN"/>
  <xsl:strip-space elements="html:* xsl:*" />

<xsl:include href="Header.xsl"/>
<xsl:include href="GlobalLinks.xsl"/>
<xsl:include href="Footer.xsl"/>

<xsl:template match="PAGE">

<xsl:param name="SELECTED-DB"/>

<xsl:variable name="SESSION-ID">
  <xsl:value-of select="SESSION-ID"/>
</xsl:variable>

<xsl:variable name="SEARCH-TYPE">
  <xsl:value-of select="SEARCH-TYPE"/>
</xsl:variable>

<xsl:variable name="DBMASK">
  <xsl:value-of select="/PAGE/DBMASK"/>
</xsl:variable>

<xsl:variable name="SEARCH-ID">
      <xsl:value-of  select="SEARCH-ID"/>
  </xsl:variable>

<xsl:variable name="DATABASE">
<xsl:choose>
  <xsl:when test="boolean(string-length(normalize-space($SELECTED-DB))>0)">
    <xsl:value-of select="$SELECTED-DB"/>
  </xsl:when>
  <xsl:otherwise>
    <xsl:value-of select="/PAGE/DBMASK"/>
  </xsl:otherwise>
</xsl:choose>
</xsl:variable>

<xsl:variable name="CID">
  <xsl:value-of select="CID"/>
</xsl:variable>

<xsl:variable name="CURRENT-PAGE">
    <xsl:value-of select="//PAGE/CURR-PAGE-ID"/>
  </xsl:variable>

<html>

<head>
  <SCRIPT LANGUAGE="Javascript" SRC="/static/js/StylesheetLinks.js"/>
  <title>Engineering Village Privacy Policy</title>
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
    <tr><td valign="top" colspan="11" height="20" bgcolor="#C3C8D1"><img src="/static/images/spacer.gif" border="0"/></td></tr>
    <tr><td valign="top" colspan="10" height="20"><img src="/static/images/spacer.gif" border="0"/></td></tr>
  </table>

</center>
<!-- End of logo table -->

  <!-- table for contents -->
  <center>
    <table border="0" width="99%" cellspacing="0" cellpadding="0">
      <tr><td valign="top"><a class="EvHeaderText">Privacy Policy</a></td></tr>
      <tr><td valign="top" height="10"><img src="/static/images/spacer.gif" border="0" height="10"/></td></tr>
      <tr><td valign="top">
            <a CLASS="MedBlackText">
            This website ("Site") is owned and operated by Elsevier Inc., 360 Park Avenue South, New York, NY 10010, USA
            <br/><br/>
            We are committed to maintaining your confidence and trust with respect to the information we collect from you. This privacy policy sets forth what information we collect about you, how we use your information, and the choices you have about how we use your information.
            <br/><br/>
            <b>Collection of Information Provided By Your Browser And Through Use Of Cookies</b>
            <br/><br/>
            This Site automatically collects information on our server logs from your web browser regarding your use of the Site.  Examples of information we collect automatically from all users include the Internet protocol (IP) address used to connect your computer to the Internet; computer and connection information such as browser type and version, operating system, and platform; and full Uniform Resource Locator ("URL") click stream data, including date and time, and content you viewed or searched for on the Site.
            <br/><br/>
            This Site also collects information through the use of "cookies." Cookies are anonymous, unique alphanumeric identifiers sent to your browser from a web site's computers and stored on your computer's hard drive. The type of information we collect as a result of a cookie being stored on your computer is specific to your computer. We use "session" cookies to collect information about your use of the Site (e.g., whether you are logged in) and so that you may view articles, abstracts or other records and browse from page to page.  These cookies are temporary and are deleted when you close your browser. We use "persistent" cookies to give you a more personalized browsing experience and help you navigate the Site more efficiently.
            <br/><br/>
            We collect and use, and may from time to time supply third parties with, this information only as anonymous, aggregated user data for the purposes of usage analysis, quality control and administering and improving the Site.
            <br/><br/>
            You can prevent your browser from accepting new cookies, have the browser notify you when you receive a new cookie, or disable cookies altogether by accessing your browser's preferences menu.  If you choose to disable cookies altogether, you can use the Site but will not be able to register, login to the Site with a personal User Name and Password, or make use of the advanced personalization features of the Site, although the basic features and functionality offered to unregistered users of the Site will be possible to use.
            <br/><br/>
            <b>Collection and Registration of Personal Information</b>
            <br/><br/>
            We sometimes request users of the Site to provide personal information.  The information collected from a user is strictly voluntary and may include contact information such as the user's name, email address, and postal address.
            <br/><br/>
            In order to access certain content and to make use of the full functionality and advanced personalization features of the Site such as alerts, saved searches, we may ask you to register and obtain a personal User Name and Password.  You may be prompted to register when you:

            <ul style="list-style-position: outside">
            <li>Choose the Register, Free Trial link from the home page or login screens</li>
            <li>Use personalization features</li>
            <li>Create an email alert or save a search</li>
            <li>Provide feedback</li>
            </ul>

            The information collected from the registration form that is presented to users when they choose to register may include contact information such as the user's name, email address. (Registration Information).  The Registration Information is linked directly to a personal User Name and Password chosen by the user and can only be retrieved by supplying the correct User Name and Password that is linked to that profile.
            <br/><br/>
            <b>How Personal Information Is Used</b>
            <br/><br/>
            We use the personal information that you provide for such purposes as:

            <ul style="list-style-position: outside">
            <li>completion and support of the activity for which the information was provided, such as allowing access to or delivery of our products or services, or responding to your requests or inquiries</li>
            <li>website and system administration, such as for the technical support of the Site and its computer system, including processing computer account information, information used in the course of securing and maintaining the Site, and verification of Site activity by the Site or its agents</li>
            <li>research and development to enhance, evaluate and improve the Site and our services</li>
            <li>tailoring or customizing content or design of the Site during a single visit to the Site and individualized personalization of the Site on repeat visits</li>
            <li>pseudonymous analysis to create a profile used to determine the habits, interests or other characteristics of users for purposes of research, analysis and anonymous reporting</li>
            <li>communicating with you about changes or updates to the Site and our services and, with your consent, special offers, promotions or market research surveys</li>
            </ul>

            <b>Disclosure of Personal Information to Third Parties</b>
            <br/><br/>
            Access to your personal information is restricted to our employees, agents, representatives and service providers such as entities for whom we are acting as an agent, licensee or publisher, such as societies and our affiliated group companies for the purposes set forth above.
            <br/><br/>
            With your consent, we also may share your personal information with our affiliated group companies and/or with non-affiliated third parties that wish to send you information about their products and services that may be of interest to you, and users who have consented to receive such information may receive communications from these third parties. If you wish us to pass your personal information to third parties, please tick the appropriate check box when you register for personalized services.
            <br/><br/>
            We will not otherwise disclose any personal information without your consent except under the following circumstances:

            <ul style="list-style-position: outside">
            <li>in response to subpoenas, court orders, or legal process, or to establish or exercise our rights to defend against legal claims;</li>
            <li>if we believe it is necessary to investigate, prevent, or take action regarding illegal activities, suspected fraud, safety of person or property, violation of our policies, or as otherwise required by law; and </li>
            <li>if Elsevier Inc., this Site or related assets or line of business are acquired by, transferred to or merged with another company.</li>
            </ul>
            </a>

            <a class="MedBlackText"><b>Access and Changes to Your Registration Information</b></a>
            <br/><br/>
            <a class="MedBlacktext">
            Registered Users may access their identified contact information and other Registration Information and correct any discrepancies or update information by clicking on the </a><A class="LgBlueLink" target="_top"><xsl:attribute name="HREF">/controller/servlet/Controller?CID=myprofile&amp;searchid=<xsl:value-of select="$SEARCH-ID"/>&amp;searchtype=<xsl:value-of select="$SEARCH-TYPE"/>&amp;count=<xsl:value-of select="$CURRENT-PAGE"/>&amp;database=<xsl:value-of select="$DATABASE"/></xsl:attribute>My Profile</A><a class="MedBlackText">  button and selecting the "Edit Account" The accuracy of such information is solely the responsibility of the user.  No access is given to other data that may have been collected about users.</a>
            <br/><br/>
            <a class="MedBlackText">If you wish to receive alerts, newsletters, new product announcements, market surveys, special offers or promotions, please check the appropriate check box when you register for personalized services or access. If you wish to cease receiving any of this information, amend your preferences either by contacting our customer service center by email at eicustomersupport{at}elsevier{dot}com and/or-telephone at +1-212-633-3795 and/or post at Ei Customer Support, 360 Park Avenue South, New York, NY 10010  We reserve the right to notify you of changes or updates to the Site whenever necessary.
            <br/><br/>
            <b>Retention of Personal Information</b>
            <br/><br/>
            We retain the personal information we collect from you for as long as the information is relevant to our business purposes or until you request that we remove the data by contacting our customer service center by email at eicustomersupport{at}elsevier{dot}com and/or telephone at +1-212-633-3795  and/or post at Ei Customer Support, 360 Park Avenue South, New York, NY 10010 or at the address and telephone number detailed below.
            <br/><br/>
            <b>Our Commitment to Data Security</b>
            <br/><br/>
            We recognize that your privacy is important to you, and therefore we endeavor to keep your personal information confidential.  The personal and demographic information about each user is stored on our servers that are behind a firewall and physically housed within a secured data center. Furthermore, our internal practices help protect your privacy by limiting employee access to and use of such information. However, we do not represent, warrant, or guarantee that your personal information will be protected against unauthorized access, loss, misuse, or alterations, and do not accept any liability for the security of such information submitted to us nor for your or third parties' use or misuse of such information.
            <br/><br/>
            <b>About This Privacy Policy</b>
            <br/><br/>
            The foregoing is the current privacy policy of the Site. We reserve the right to change this policy at any time without notice.  Any changes to this policy will be posted on this webpage. Our policy does not create a contract or any legal rights.
            <br/><br/>
            This privacy policy applies solely to the information you provide while visiting this Site. The terms of this privacy policy do not govern personal information furnished through any means other than this Site (such as via telephone or postal mail).
            <br/><br/>
            This Site may contain links to other websites that are beyond our control.  Other websites to which you link may contain privacy policies that are different from this privacy policy.  We encourage you to check the privacy policy of each website you visit before submitting any information to them.
            <br/><br/>
            <b>Questions, Complaints and Dispute Resolution</b>
            <br/><br/>
            If you have any questions or comments regarding this privacy policy, please contact our Customer Service Center at:
            <br/><br/>
            Elsevier Engineering Information<br/>
            360 Park Avenue South <br/>
            New York, NY 10010<br/>
            Eicustomersupport{at}elsevier{dot}com<br/>
            +1-212-633-3795
             <br/><br/>

            If at any time you believe that we have not adhered to this privacy policy or you have a complaint regarding the way your personal information is being handled, please contact our Customer Service Center.  Disputes under this privacy policy will be resolved by our Customer Service Center, which will use commercially reasonable efforts to promptly investigate, and if necessary, to correct any problem.
            <br/><br/>
            Last revised:  June 21st, 2007

      </a>
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