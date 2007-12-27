<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>'>
]>

<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:html="http://www.w3.org/TR/REC-html40"
  exclude-result-prefixes="html xsl"
>

<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="html:* xsl:*" />

<!-- jam 12/23/2002 - added name="FOOTER" so we may
  explicitly use xsl:call-template instead of xsl:apply-tempates
  in order to inlcude the footer in output
  see Linda Hall (LHLxxxx.xsl files).
  -->
<xsl:template match="FOOTER" name="FOOTER">
  <!-- This xsl displays Footer. This file is included, whenever is footer is to be displayed.
  -->
  <xsl:param name="SESSION-ID"/>
  <xsl:param name="SELECTED-DB"/>
  <xsl:param name="TARGET">_self</xsl:param>

  <BR/>

  <TABLE WIDTH="100%" CELLSPACING="0" CELLPADDING="0" BORDER="0">
  <TR><TD>
    <CENTER>
    <!-- jam 12/23/3003
      if params are not supplied,
      do not include links
    -->
    <xsl:if test="boolean(string-length(normalize-space($SELECTED-DB))>0)">
      <A CLASS="MedBlueLink" TARGET="{$TARGET}" HREF="/controller/servlet/Controller?CID=aboutEI&amp;database={$SELECTED-DB}">About Ei</A>
      <A CLASS="SmBlackText">&#160; - &#160;</A>
      <A CLASS="MedBlueLink" TARGET="{$TARGET}" HREF="/controller/servlet/Controller?CID=aboutEV&amp;database={$SELECTED-DB}">About Engineering Village</A>
      <A CLASS="SmBlackText">&#160; - &#160;</A>
      <A CLASS="MedBlueLink" TARGET="{$TARGET}" HREF="/controller/servlet/Controller?CID=feedback&amp;database={$SELECTED-DB}">Feedback</A>
      <A CLASS="SmBlackText">&#160; - &#160;</A>
      <A CLASS="MedBlueLink" TARGET="{$TARGET}" HREF="/controller/servlet/Controller?CID=privacyPolicy&amp;database={$SELECTED-DB}">Privacy Policy</A>
      <A CLASS="SmBlackText">&#160; - &#160;</A>
      <A CLASS="MedBlueLink" TARGET="{$TARGET}" HREF="/controller/servlet/Controller?CID=TermsandConditions&amp;database={$SELECTED-DB}">Terms and Conditions</A>
      <br/>
    </xsl:if>
    <A CLASS="SmBlackText">&#169; 2008 Elsevier Inc. All rights reserved.</A>
    </CENTER>
  </TD></TR>
  </TABLE>

  <!-- End of footer -->

</xsl:template>

</xsl:stylesheet>