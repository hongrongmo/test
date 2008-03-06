<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:html="http://www.w3.org/TR/REC-html40"
  xmlns:java="java:java.net.URLEncoder"
  xmlns:cimage="java:org.ei.gui.CustomerImage"
  exclude-result-prefixes="java html xsl cimage"
>

<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="html:* xsl:*" />
<xsl:param name="CUST-ID">0</xsl:param>
<xsl:template match="HEADER">

  <xsl:variable name="CUSTOMIZED-LOGO">
    <xsl:value-of select="//CUSTOMIZED-LOGO"/>
  </xsl:variable>

  <xsl:variable name="GLOBAL-MESSAGE">
      <xsl:value-of select="//GLOBAL-MESSAGE"/>
  </xsl:variable>

  <xsl:variable name="PERSONALIZATION">
    <xsl:value-of select="//PERSONALIZATION"/>
  </xsl:variable>

  <xsl:variable name="PERSONALIZATION-PRESENT">
      <xsl:value-of select="//PERSONALIZATION-PRESENT"/>
  </xsl:variable>

  <!-- This xsl displays Header. This file is included when header is required to rendered.
  -->
  <xsl:param name="SESSION-ID"/>
  <xsl:param name="SELECTED-DB"/>
  <xsl:param name="SEARCH-TYPE"/>
  <xsl:param name="COMBINED-SEARCH"/>

  <xsl:variable name="SESSIONID">
    <xsl:value-of select="//SESSION-ID"/>
  </xsl:variable>

  <xsl:variable name="SEARCH-ID">
    <xsl:value-of select="//SEARCH-ID"/>
  </xsl:variable>

  <xsl:variable name="INDEX">
  <xsl:value-of select="//PAGE-RESULTS/PAGE-ENTRY/EI-DOCUMENT/HANDLE"/>
  </xsl:variable>

  <xsl:variable name="CID">
  <xsl:value-of select="//CID"/>
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

  <xsl:variable name="DATABASE-ID">
    <xsl:value-of select="//SESSION-DATA/DATABASE/ID"/>
  </xsl:variable>

  <xsl:variable name="CURRENT-PAGE">
    <xsl:value-of select="//PAGE/CURR-PAGE-ID"/>
  </xsl:variable>

  <xsl:variable name="DATABASETYPE">
  <xsl:value-of select="//SESSION-DATA/DATABASE-TYPE"/>
  </xsl:variable>

  <xsl:variable name="SEARCH-RESULTS-FORMAT">
    <xsl:choose>
      <xsl:when test="boolean($DATABASETYPE='remote')">remoteRedirect</xsl:when>
      <xsl:otherwise>
       <xsl:choose>
        <xsl:when test="boolean($SEARCH-TYPE='Quick')">quickSearchCitationFormat</xsl:when>
        <xsl:when test="boolean($SEARCH-TYPE='Expert')">expertSearchCitationFormat</xsl:when>
        <xsl:when test="boolean($SEARCH-TYPE='Combined')">combineSearchHistory</xsl:when>
        <xsl:when test="boolean($SEARCH-TYPE='Thesaurus')">thesSearchCitationFormat</xsl:when>
        <xsl:when test="boolean($SEARCH-TYPE='Easy')">expertSearchCitationFormat</xsl:when>
       </xsl:choose>
      </xsl:otherwise>
    </xsl:choose>
  </xsl:variable>


<xsl:variable name="RESULTS-NAV">
	<xsl:value-of select="/PAGE/PAGE-NAV/RESULTS-NAV"/>
</xsl:variable>
<xsl:variable name="NEWSEARCH-NAV">
	<xsl:value-of select="/PAGE/PAGE-NAV/NEWSEARCH-NAV"/>
</xsl:variable>


  <!-- Start of logo table -->
  <table border="0" width="99%" cellspacing="0" cellpadding="0">
    <tr>
      <td valign="top">
        <table border="0" cellspacing="0" cellpadding="0">
          <tr>
            <td valign="top">
              <a target="_top" href="/controller/servlet/Controller?CID=home">
              <xsl:choose>


                <xsl:when test="cimage:containsCustomer($CUST-ID)">
                	<xsl:variable name="CUSTOMER-IMAGE">
				<xsl:value-of select="cimage:getImage($CUST-ID)"/>
			</xsl:variable>
			<xsl:variable name="CUSTOMER-URL">
				<xsl:value-of select="cimage:getURL($CUST-ID)"/>
			</xsl:variable>
	             	<a href="{$CUSTOMER-URL}" border="0">
	             		<img src="/engresources/custimages/{$CUSTOMER-IMAGE}" border="0"/>
                	</a>
                </xsl:when>


                <xsl:when test="not($CUSTOMIZED-LOGO='')">
                  <img src="/engresources/custimages/{$CUSTOMIZED-LOGO}.gif" border="0"/>
                </xsl:when>
                <xsl:otherwise>
                  <img alt="Engineering Village" src="/engresources/images/ev2logo5.gif" border="0"/>
                </xsl:otherwise>
              </xsl:choose>
              </a>
            </td>
          </tr>
        </table>
      </td>

      <xsl:choose>
	      <xsl:when test="not($GLOBAL-MESSAGE='')">
	      <td valign="middle">
          &#160;
	      	<!-- NO MORE FREE DAYPASS INVITES <a class="DecLink" href="https://store.engineeringvillage.com/ppd/invite.do" target="_top">Invite a friend for a free day pass!</a> -->
	      </td>
      	     </xsl:when>
      </xsl:choose>

      <xsl:if test="not($SEARCH-TYPE='EasyForm')">
        <td valign="middle" align="right" target="_top">
          <a class="DecLink" target="_top" href="/controller/servlet/Controller?CID=viewCompleteSearchHistory&amp;database={$DATABASE}&amp;searchresults={$RESULTS-NAV}&amp;newsearch={$NEWSEARCH-NAV}">Search History</a>
          <a class="SmBlackText">&#160; - &#160;</a>
          <a class="DecLink" target="_top" href="/controller/servlet/Controller?CID=citationSelectedSet&amp;DATABASETYPE={$DATABASE}&amp;searchresults={$RESULTS-NAV}&amp;newsearch={$NEWSEARCH-NAV}">Selected Records</a>
          <xsl:if test="($PERSONALIZATION-PRESENT='true')">
            <a class="SmBlackText">&#160; - &#160;</a>
            <a class="DecLink" target="_top" HREF="/controller/servlet/Controller?CID=myprofile&amp;database={$DATABASE}&amp;searchresults={$RESULTS-NAV}&amp;newsearch={$NEWSEARCH-NAV}">My Profile</a>
            <a class="SmBlackText">&#160; - &#160;</a>
            <xsl:variable name="NEXTURL">CID=viewSavedSearches&amp;database=<xsl:value-of select="$DATABASE"/>&amp;show=alerts</xsl:variable>
            <a class="DecLink" target="_top">
            <xsl:attribute name="href">
              <xsl:choose>
                <xsl:when test="($PERSONALIZATION='true')">/controller/servlet/Controller?CID=viewSavedSearches&amp;database=<xsl:value-of select="$DATABASE"/>&amp;show=alerts</xsl:when>
                <xsl:otherwise>/controller/servlet/Controller?CID=personalLoginForm&amp;searchid=<xsl:value-of select="$SEARCH-ID"/>&amp;searchtype=<xsl:value-of select="$SEARCH-TYPE"/>&amp;count=<xsl:value-of select="$CURRENT-PAGE"/>&amp;displaylogin=true&amp;database=<xsl:value-of select="$DATABASE"/>&amp;nexturl=<xsl:value-of select="java:encode($NEXTURL)"/></xsl:otherwise>
              </xsl:choose>
            </xsl:attribute>My Alerts</a>
          </xsl:if>
        </td>
        <td valign="middle" align="right">
          <a target="_top" href="/controller/servlet/Controller?CID=endSession&amp;SYSTEM_LOGOUT=true">
            <img src="/engresources/images/endsession.gif" border="0" alt="End Session" />
          </a>
        </td>
      </xsl:if>
    </tr>
  </table>
  <!-- End of logo table -->

</xsl:template>

</xsl:stylesheet>