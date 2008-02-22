<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:cal="java:java.util.Calendar"
    xmlns:DD="java:org.ei.domain.DatabaseDisplayHelper"
    xmlns:bit="java:org.ei.util.BitwiseOperators"
    xmlns:xslcid="java:org.ei.domain.XSLCIDHelper"
    exclude-result-prefixes="xsl cal DD bit" >

    <!--
       This xsl file display the data of selected records from the  Search Results in an Citation format.
       All the business rules which govern the display format are also provided.
      -->
      <xsl:output method="html"/>

      <xsl:include href="./common/CitationResults.xsl" />

      <xsl:variable name="SESSION-ID">
          <xsl:value-of select="SESSION-ID"/>
      </xsl:variable>

      <xsl:variable name="SEARCH-TYPE"><xsl:value-of select="/PAGE/SESSION-DATA/SEARCH-TYPE"/></xsl:variable>
      <xsl:variable name="RESULTS-DATABASE-MASK"><xsl:value-of select="/PAGE/SESSION-DATA/DATABASE-MASK"/></xsl:variable>
      <xsl:variable name="QUERY-ID"><xsl:value-of select="/PAGE/SESSION-DATA/QUERY-ID"/></xsl:variable>
      <xsl:variable name="RESULTS-YEAR-RANGE"><xsl:value-of select="/PAGE/SESSION-DATA/YEAR-RANGE"/></xsl:variable>
      <xsl:variable name="DISPLAY-QUERY"><xsl:value-of select="/PAGE/SESSION-DATA/DISPLAY-QUERY"/></xsl:variable>
      <xsl:variable name="EMAILALERT"><xsl:value-of select="/PAGE/SESSION-DATA/EMAILALERTWEEK"/></xsl:variable>
      <xsl:variable name="PHYSICAL-QUERY"><xsl:value-of select="/PAGE/SESSION-DATA/PHYSICAL-QUERY"/></xsl:variable>
      <xsl:variable name="RESULTS-COUNT"><xsl:value-of select="/PAGE/SESSION-DATA/RESULTS-COUNT"/></xsl:variable>
      <xsl:variable name="ALERTS-PER-PAGE"><xsl:value-of select="/PAGE/ALERTS-PER-PAGE"/></xsl:variable>
      <xsl:variable name="WEEK-OF-YEAR"><xsl:value-of select="/PAGE/WEEK-OF-YEAR"/></xsl:variable>
      <xsl:variable name="SYSTEM_PASSTHROUGH">SYSTEM_PASSTHROUGH=true</xsl:variable>

      <xsl:variable name="SEARCH-CID">
        <xsl:value-of select="xslcid:searchResultsCid($SEARCH-TYPE)"/>
      </xsl:variable>

      <!-- No longer encode ENTIRE QUERY!  send down saved searchID -->
      <!-- Still NEED CID, Database and emailweek !! Duh -->
      <xsl:variable name="HREF-QUERY">SEARCHID=<xsl:value-of select="$QUERY-ID"/>&amp;location=ALERT&amp;database=<xsl:value-of select="$RESULTS-DATABASE-MASK"/>&amp;emailweek=<xsl:value-of select="$WEEK-OF-YEAR"/></xsl:variable>

      <xsl:template match="PAGE">

          <center>

          <!-- Start of table for search results -->

          <table border="0" width="99%" cellspacing="0" cellpadding="0">
              <tr>
                  <td valign="top" colspan="5" ><img src="cid:spacergif" border="0" width="0"/><A CLASS="MedBlackText"><b>Engineering Village E-mail Alert</b></A></td>
              </tr>
			  <tr>
				  <td valign="top" colspan="5" align="left"><img src="cid:spacergif" border="0" width="0"/><A CLASS="MedBlackText">Please do not reply to this email address.  Please send all responses and questions to eicustomersupport@elsevier.com.</A></td>
			  </tr>
              <xsl:if test="($RESULTS-COUNT &gt; 0)">
              <tr>
                  <td valign="top" colspan="5" ><img src="cid:spacergif" border="0" width="3"/></td>
              </tr>
              <tr>
                  <td valign="top" colspan="5" ><img src="cid:spacergif" border="0" width="3"/></td>
              </tr>
                  <tr>
                      <td valign="top" colspan="5" align="left"><img src="cid:spacergif" border="0" width="0"/><A CLASS="MedBlackText">Click on the links below to execute search or view a record in Engineering Village.</A></td>
                  </tr>
                  <tr>
                      <td valign="top" colspan="5" align="left"><img src="cid:spacergif" border="0" width="0"/><A CLASS="MedBlackText"><b>NOTE:</b> The links below will only work if your computer's IP address is recognized by Engineering Village.</A></td>
                  </tr>
              </xsl:if>
              <tr>
                  <td valign="top" colspan="5"><img src="cid:spacergif" border="0" width="3"/></td>
              </tr>
              <tr>
                  <td valign="top" colspan="5"><img src="cid:spacergif" border="0" width="3"/></td>
              </tr>
              <tr>
                  <td valign="top" colspan="5" align="left">
                      <A CLASS="SmBlackText"><xsl:value-of select="$RESULTS-COUNT"/> new records found in <xsl:value-of select="DD:getDisplayName($RESULTS-DATABASE-MASK)"/> for: </A>
                      <A CLASS="LgBlueLink">
                      <xsl:if test="($RESULTS-COUNT &gt; 0)">
                          <xsl:attribute name="href">http://<xsl:value-of select="/PAGE/SERVER-LOCATION"/>/controller/servlet/Controller?CID=<xsl:value-of select="$SEARCH-CID"/>&amp;<xsl:value-of select="$HREF-QUERY"/>&amp;<xsl:value-of select="$SYSTEM_PASSTHROUGH"/></xsl:attribute>
                      </xsl:if>
                      <xsl:value-of select="$DISPLAY-QUERY"/>, Week <xsl:value-of select="$EMAILALERT"/>
                      </A>
                  </td>
              </tr>
              <tr>
                  <td valign="top" colspan="5"><img src="cid:spacergif" border="0" width="3"/></td>
              </tr>
              <xsl:if test="($RESULTS-COUNT &gt; 25)">
                  <tr>
                      <td valign="top" colspan="5" align="left"><img src="cid:spacergif" border="0" width="3"/><A CLASS="SmBlackText">Up to the first <xsl:value-of select="$ALERTS-PER-PAGE"/> records are displayed below:</A></td>
                  </tr>
              </xsl:if>
              <tr>
                  <td colspan="5">
                      <table border="0" cellspacing="0" cellpadding="0" width="100%">
                          <xsl:apply-templates select="PAGE-RESULTS"/>
                      </table>
                  </td>
              </tr>
              <tr>
                  <td valign="top" colspan="5"><img src="cid:spacergif" border="0" width="3"/></td>
              </tr>
              <tr>
                  <td valign="top" colspan="5" align="left">
                      <xsl:if test="boolean(bit:hasBitSet($RESULTS-DATABASE-MASK,1))">
                          <A CLASS="SmBlackText">Compilation and indexing terms, Copyright 2008, Elsevier Inc.</A><BR/>
                      </xsl:if>
                      <xsl:if test="boolean(bit:hasBitSet($RESULTS-DATABASE-MASK,2))">
                          <A CLASS="SmBlackText">Inspec, Copyright 1969-2008, IET</A><BR/>
                      </xsl:if>
                      <xsl:if test="boolean(bit:hasBitSet($RESULTS-DATABASE-MASK,4))">
                          <A CLASS="SmBlackText">Compiled and Distributed by the NTIS, US Department of Commerce.  It contains copyrighted material.  All rights reserved. 2008</A><BR/>
                      </xsl:if>
                      <xsl:if test="boolean(bit:hasBitSet($RESULTS-DATABASE-MASK,32768)) or boolean(bit:hasBitSet($RESULTS-DATABASE-MASK,16384))">
                          <A CLASS="SmBlackText">Compilation and indexing terms, Copyright 2008 LexisNexis Univentio B.V.</A><BR/>
                      </xsl:if>
                  </td>
              </tr>
              <tr>
                  <td valign="top" colspan="5"><img src="cid:spacergif" border="0" width="3"/></td>
              </tr>
              <tr>
                  <td valign="top"  colspan="5" align="left"><HR /></td>
              </tr>
              <tr>
                  <td valign="top" colspan="5" align="left"><img src="cid:spacergif" border="0" width="3"/><A CLASS="SmBlackText">You have received this message because you or someone else has created an E-mail Alert in Engineering Village for this e-mail address. To view this search or cancel this alert, click on "Saved Searches" on the Engineering Village home page</A>(<A href="http://www.engineeringvillage.com/">http://www.engineeringvillage.com</A>)<A CLASS="SmBlackText"> and login to your Personal Account.</A></td>
              </tr>
              <tr>
                  <td valign="top" colspan="5"><img src="cid:spacergif" border="0" width="3"/></td>
              </tr>
              <tr>
                  <td valign="top" colspan="5" align="left"><img src="cid:spacergif" border="0" width="3"/><A CLASS="SmBlackText">You may also contact Ei Customer Support</A> (<A href="mailto:eicustomersupport@elsevier.com">eicustomersupport@elsevier.com</A>)<A CLASS="SmBlackText"> for further assistance.</A></td>
              </tr>
          </table>

          </center>

      </xsl:template>

      <xsl:variable name="SELECTED-DB"><xsl:value-of select="/PAGE/SESSION-DATA/DATABASE/SHORTNAME"/></xsl:variable>

      <xsl:variable name="CITATION-CID">
        <xsl:choose>
          <xsl:when test="($SEARCH-TYPE='Book')">quickSearchCitationFormat</xsl:when>
          <xsl:when test="($SEARCH-TYPE='Quick')">quickSearchCitationFormat</xsl:when>
          <xsl:when test="($SEARCH-TYPE='Expert')">expertSearchCitationFormat</xsl:when>
          <xsl:when test="($SEARCH-TYPE='Combined')">expertSearchCitationFormat</xsl:when>
          <xsl:when test="($SEARCH-TYPE='Thesaurus')">quickSearchCitationFormat</xsl:when>
          <xsl:when test="($SEARCH-TYPE='Easy')">expertSearchCitationFormat</xsl:when>
          <xsl:otherwise>expertSearchCitationFormat</xsl:otherwise>
        </xsl:choose>
      </xsl:variable>
      <xsl:variable name="ABSTRACT-CID">
        <xsl:choose>
          <xsl:when test="($SEARCH-TYPE='Book')">quickSearchAbstractFormat</xsl:when>
          <xsl:when test="($SEARCH-TYPE='Quick')">quickSearchAbstractFormat</xsl:when>
          <xsl:when test="($SEARCH-TYPE='Expert')">expertSearchAbstractFormat</xsl:when>
          <xsl:when test="($SEARCH-TYPE='Combined')">expertSearchAbstractFormat</xsl:when>
          <xsl:when test="($SEARCH-TYPE='Thesaurus')">quickSearchAbstractFormat</xsl:when>
          <xsl:when test="($SEARCH-TYPE='Easy')">expertSearchAbstractFormat</xsl:when>
          <xsl:otherwise>expertSearchAbstractFormat</xsl:otherwise>
        </xsl:choose>
      </xsl:variable>
      <xsl:variable name="DETAILED-CID">
        <xsl:choose>
          <xsl:when test="($SEARCH-TYPE='Book')">quickSearchDetailedFormat</xsl:when>
          <xsl:when test="($SEARCH-TYPE='Quick')">quickSearchDetailedFormat</xsl:when>
          <xsl:when test="($SEARCH-TYPE='Expert')">expertSearchDetailedFormat</xsl:when>
          <xsl:when test="($SEARCH-TYPE='Combined')">expertSearchDetailedFormat</xsl:when>
          <xsl:when test="($SEARCH-TYPE='Thesaurus')">quickSearchDetailedFormat</xsl:when>
          <xsl:when test="($SEARCH-TYPE='Easy')">expertSearchDetailedFormat</xsl:when>
          <xsl:otherwise>expertSearchDetailedFormat</xsl:otherwise>
        </xsl:choose>
      </xsl:variable>

      <xsl:variable name="HREF-PREFIX">http://<xsl:value-of select="/PAGE/SERVER-LOCATION"/></xsl:variable>
      <xsl:variable name="SPACER-CID">cid:spacergif</xsl:variable >

      <xsl:template match="PAGE-ENTRY" >

        <xsl:variable name="SEARCH-ID"><xsl:value-of select="//SEARCH-ID"/></xsl:variable>
        <xsl:variable name="CURRENT-PAGE">1</xsl:variable>
        <xsl:variable name="RESULTS-PER-PAGE">25</xsl:variable>
        <xsl:variable name="RESULTS-COUNT"><xsl:value-of select="//RESULTS-COUNT"/></xsl:variable>
        <xsl:variable name="DISPLAY-QUERY"><xsl:value-of select="/PAGE/SESSION-DATA/DISPLAY-QUERY"/></xsl:variable>
        <xsl:variable name="SEARCH-TYPE"><xsl:value-of select="/PAGE/SESSION-DATA/SEARCH-TYPE"/></xsl:variable>
        <xsl:variable name="CUSTOMIZED-STARTYEAR"><xsl:value-of select="//CUSTOMIZED-STARTYEAR"/></xsl:variable>
        <xsl:variable name="CUSTOMIZED-ENDYEAR"><xsl:value-of select="//CUSTOMIZED-ENDYEAR"/></xsl:variable>
        <xsl:variable name="DATABASE"><xsl:value-of select="EI-DOCUMENT/DB"/></xsl:variable>
        <xsl:variable name="DATABASE-ID"><xsl:value-of select="EI-DOCUMENT/DBID"/></xsl:variable>
        <xsl:variable name="INDEX"><xsl:value-of select="EI-DOCUMENT/DOC/HITINDEX"/></xsl:variable>
        <xsl:variable name="DOC-ID"><xsl:value-of select="EI-DOCUMENT/DOC-ID"/></xsl:variable>

        <tr>
          <td valign="top" colspan="4" height="5"><img src="{$SPACER-CID}" border="0"/></td>
        </tr>
        <tr>
          <td valign="top" align="left">
            <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
          </td>
          <td valign="top">
            <img src="{$SPACER-CID}" border="0" width="0"/>
            <A CLASS="MedBlackText"><xsl:value-of select="$INDEX" />.</A>
          </td>
          <td valign="top" width="3"><img src="{$SPACER-CID}" border="0" width="3"/></td>
          <td valign="top"><img src="{$SPACER-CID}" border="0" name="image_basket"/>

            <xsl:apply-templates select="EI-DOCUMENT"/>

            <br/>
            <xsl:variable name="ABSTRACT-LINK-CID">
              <xsl:choose>
              <xsl:when test="$SEARCH-CID=''">CID=<xsl:value-of select="$ABSTRACT-CID"/></xsl:when>
              <xsl:otherwise>CID=<xsl:value-of select="$SEARCH-CID"/></xsl:otherwise>
              </xsl:choose>
            </xsl:variable>
            <xsl:variable name="DETAILED-LINK-CID">
              <xsl:choose>
              <xsl:when test="$SEARCH-CID=''">CID=<xsl:value-of select="$DETAILED-CID"/></xsl:when>
              <xsl:otherwise>CID=<xsl:value-of select="$SEARCH-CID"/></xsl:otherwise>
              </xsl:choose>
            </xsl:variable>

            <A CLASS="LgBlueLink" HREF="{$HREF-PREFIX}/controller/servlet/Controller?{$ABSTRACT-LINK-CID}&amp;{$HREF-QUERY}&amp;{$SYSTEM_PASSTHROUGH}&amp;DOCINDEX={$INDEX}&amp;PAGEINDEX={$CURRENT-PAGE}&amp;format={$ABSTRACT-CID}">Abstract</A> &#160;
            <A CLASS="MedBlackText">-</A> &#160;
            <A CLASS="LgBlueLink" HREF="{$HREF-PREFIX}/controller/servlet/Controller?{$DETAILED-LINK-CID}&amp;{$HREF-QUERY}&amp;{$SYSTEM_PASSTHROUGH}&amp;DOCINDEX={$INDEX}&amp;PAGEINDEX={$CURRENT-PAGE}&amp;format={$DETAILED-CID}">Detailed</A>
          </td>
        </tr>
        <tr>
          <td valign="top" colspan="4" height="5"><img src="{$SPACER-CID}" border="0"/></td>
        </tr>
      </xsl:template>

</xsl:stylesheet>