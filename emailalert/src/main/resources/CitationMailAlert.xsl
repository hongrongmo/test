<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:cal="java:java.util.Calendar"
    xmlns:DD="java:org.ei.domain.DatabaseDisplayHelper"
    xmlns:bit="java:org.ei.util.BitwiseOperators"
    xmlns:xslcid="java:org.ei.domain.XSLCIDHelper"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    xmlns:java="java:java.net.URLEncoder"
    xmlns:security="java:org.ei.util.security.SecureID"
    xmlns:hlight="java:org.ei.query.base.HitHighlightFinisher"
    exclude-result-prefixes="hlight java html xsl security cal DD bit xslcid"
     >

    <!--
       This xsl file display the data of selected records from the  Search Results in an Citation format.
       All the business rules which govern the display format are also provided.
      -->
      <xsl:output method="html"/>
      <xsl:strip-space elements="html:* xsl:*" />

     <!--  <xsl:include href="CitationResults.xsl" />
 -->
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
      <xsl:variable name="SYSTEM_PASSTHROUGH">SYSTEM_PT=t</xsl:variable>

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
	                    <td valign="top" colspan="5" align="left"><A CLASS="MedBlackText"><b>Engineering Village E-mail Alert</b></A></td>
	                </tr>
	                            <tr>
	                                    <td valign="top" colspan="5" align="left"><A CLASS="MedBlackText">Please do not reply to this email address. Please select this</A><A href="http://help.elsevier.com/app/ask_ev/p/9639"> link </A><A CLASS="MedBlackText">to ask questions.</A></td>
	                            </tr>
	                <xsl:if test="($RESULTS-COUNT &gt; 0)">
	                <tr>
	                    <td valign="top" colspan="5" height="5">&#160;</td>
	                </tr>
	                    <tr>
	                        <td valign="top" colspan="5" align="left"><A CLASS="MedBlackText">Click on the links below to execute search or view a record in Engineering Village.</A></td>
	                    </tr>
	                    <tr>
	                        <td valign="top" colspan="5" align="left"><A CLASS="MedBlackText"><b>NOTE:</b> The links below will only work if your computer's IP address is recognized by Engineering Village.</A></td>
	                    </tr>
	                </xsl:if>
	                <tr>
	                    <td valign="top" colspan="5" height="5">&#160;</td>
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
                  <td valign="top" colspan="5" height="5">&#160;</td>
              </tr>
              <xsl:if test="($RESULTS-COUNT &gt; 25)">
                  <tr>
                      <td valign="top" colspan="5" align="left"><A CLASS="SmBlackText">Up to the first <xsl:value-of select="$ALERTS-PER-PAGE"/> records are displayed below:</A></td>
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
                  <td valign="top" colspan="5" height="5">&#160;</td>
              </tr>
              <tr>
                  <td valign="top" colspan="5" align="left">
                      <xsl:if test="boolean(bit:hasBitSet($RESULTS-DATABASE-MASK,1))">
                          <A CLASS="SmBlackText">Compilation and indexing terms, Copyright 2014, Elsevier Inc.</A><BR/>
                      </xsl:if>
                      <xsl:if test="boolean(bit:hasBitSet($RESULTS-DATABASE-MASK,2))">
                          <A CLASS="SmBlackText">Inspec, Copyright 1969-2014, IET</A><BR/>
                      </xsl:if>
                      <xsl:if test="boolean(bit:hasBitSet($RESULTS-DATABASE-MASK,4))">
                          <A CLASS="SmBlackText">Compiled and Distributed by the NTIS, US Department of Commerce.  It contains copyrighted material.  All rights reserved. 2014</A><BR/>
                      </xsl:if>
                      <xsl:if test="boolean(bit:hasBitSet($RESULTS-DATABASE-MASK,32768)) or boolean(bit:hasBitSet($RESULTS-DATABASE-MASK,16384))">
                          <A CLASS="SmBlackText">Compilation and indexing terms, Copyright 2014 LexisNexis Univentio B.V.</A><BR/>
                      </xsl:if>
                  </td>
              </tr>
              <tr>
                  <td valign="top" colspan="5" height="5">&#160;</td>
              </tr>
              <tr>
                  <td valign="top"  colspan="5" align="left"><HR /></td>
              </tr>
              <tr>
                  <td valign="top" colspan="5" align="left"><A CLASS="SmBlackText">You have received this message because you or someone else has created an E-mail Alert in Engineering Village for this e-mail address. To view this search or cancel this alert, click on "Saved Searches" on the Engineering Village home page</A>(<A href="http://www.engineeringvillage.com/">http://www.engineeringvillage.com</A>)<A CLASS="SmBlackText"> and login to your Personal Account.</A></td>
              </tr>
              <tr>
                  <td valign="top" colspan="5" height="5">&#160;</td>
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

<!--        <tr>
          <td valign="top" colspan="4" height="5">&#160;</td>
        </tr> -->
        <tr>
          <td valign="top" align="left">
            <xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>
          </td>
          <td valign="top">
            <A CLASS="MedBlackText"><xsl:value-of select="$INDEX" />.</A>
          </td>
          <td valign="top" width="3">&#160;</td>
           <td valign="top" align="left">
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

            <A CLASS="LgBlueLink" HREF="{$HREF-PREFIX}/controller/servlet/Controller?{$ABSTRACT-LINK-CID}&amp;{$HREF-QUERY}&amp;{$SYSTEM_PASSTHROUGH}&amp;DOCINDEX={$INDEX}&amp;PAGEINDEX={$CURRENT-PAGE}&amp;format={$ABSTRACT-CID}">Abstract</A>&#160;
            <A CLASS="MedBlackText">|</A>&#160;
            <A CLASS="LgBlueLink" HREF="{$HREF-PREFIX}/controller/servlet/Controller?{$DETAILED-LINK-CID}&amp;{$HREF-QUERY}&amp;{$SYSTEM_PASSTHROUGH}&amp;DOCINDEX={$INDEX}&amp;PAGEINDEX={$CURRENT-PAGE}&amp;format={$DETAILED-CID}">Detailed</A>
          </td>
        </tr>
        <tr>
          <td valign="top" colspan="4" height="5">&#160;</td>
        </tr>
      </xsl:template>
      <xsl:template match="EI-DOCUMENT" >


      <xsl:param name="ascii">false</xsl:param>
      <xsl:apply-templates select="BTI"/>
      <xsl:apply-templates select="BPP"/>
      <xsl:if test="DOC/DB/DBMASK='131072'">
        <xsl:if test="$ascii='true'">
          <xsl:text>&#xD;&#xA;</xsl:text>
        </xsl:if>
        <!-- Book chapter title -->
        <xsl:apply-templates select="BCT"/>
      </xsl:if>

      <xsl:apply-templates select="TI"/>

      <xsl:if test="$ascii='true'">
        <xsl:text>&#xD;&#xA;</xsl:text>
      </xsl:if>
      <xsl:apply-templates select="AUS"/>
      <xsl:apply-templates select="EDS"/>
      <xsl:apply-templates select="IVS"/>
      <span CLASS="SmBlackText">
      <xsl:choose>
          <xsl:when test ="string(NO_SO)">
              <xsl:text></xsl:text>
          </xsl:when>
          <xsl:when test="string(SO)">
              <xsl:apply-templates select="SO"/>
          </xsl:when>
          <xsl:otherwise>
              <b>Source: </b>
          </xsl:otherwise>
      </xsl:choose>

      <xsl:if test="not(DOC/DB/DBMASK='131072')">
        <xsl:apply-templates select="BN"/>
      </xsl:if>
      <xsl:apply-templates select="BN13"/>
      <xsl:apply-templates select="BPN"/>
      <xsl:apply-templates select="BYR"/>

      <xsl:apply-templates select="PF"/>
      <xsl:apply-templates select="RSP"/>
      <xsl:apply-templates select="RN_LABEL"/>

<!--      <xsl:apply-templates select="PN"/> -->
      <xsl:apply-templates select="RN"/>
      <xsl:apply-templates select="VO"/>

	<xsl:apply-templates select="PP"/>
	<xsl:apply-templates select="ARN"/>
	<xsl:apply-templates select="p_PP"/>
	<xsl:apply-templates select="PP_pp"/>

      <xsl:apply-templates select="SD"/>

      <xsl:apply-templates select="MT"/>
      <xsl:apply-templates select="VT"/>

      <xsl:if test="not(string(SD))">
        <xsl:apply-templates select="YR"/>
      </xsl:if>

      <xsl:apply-templates select="PD_YR"/>
<!--  <xsl:apply-templates select="UPD"/> -->
      <xsl:apply-templates select="NV"/>
      <xsl:apply-templates select="PA"/>

      <xsl:apply-templates select="PAS"/>
      <xsl:apply-templates select="PASM"/>
      <xsl:apply-templates select="EASM"/>
      <xsl:apply-templates select="PAN"/>

      <xsl:apply-templates select="PAP"/>
      <xsl:apply-templates select="PIM"/>
      <xsl:apply-templates select="PINFO"/>
      <xsl:apply-templates select="PM"/>
      <xsl:apply-templates select="PM1"/>
      <xsl:apply-templates select="UPD"/>
<!--        <xsl:apply-templates select="KC"/> -->
      <xsl:apply-templates select="KD"/>
      <xsl:if test="$ascii='true'">
          <xsl:text>&#xD;&#xA;</xsl:text>
      </xsl:if>
      <xsl:apply-templates select="PFD"/>
      <xsl:apply-templates select="PIDD"/>
      <xsl:apply-templates select="PPD"/>
      <!-- added for 1884 -->
      <xsl:apply-templates select="PID"/>
      <xsl:apply-templates select="COPA"/>

      <xsl:apply-templates select="LA"/>
      <xsl:apply-templates select="NF"/>
      <!-- CBNB availability -->
      <xsl:apply-templates select="AV"/>
      </span>

      <xsl:apply-templates select="CITEDBY"/>

      <xsl:if test="$ascii='true'">
        <xsl:text>&#xD;&#xA;</xsl:text>
      </xsl:if>
      <xsl:apply-templates select="DT"/>
      <xsl:apply-templates select="DOC/DB/DBNAME"/>
     
      <xsl:apply-templates select="COL"/>

      <xsl:if test="$ascii='true'">
        <xsl:text>&#xD;&#xA;</xsl:text>
      </xsl:if>
      <xsl:apply-templates select="FTTJ"/>
      <xsl:apply-templates select="STT"/>

    </xsl:template>



    <xsl:template match="TI">
        <a class="MedBlackText"><b><xsl:value-of select="." disable-output-escaping="yes"/></b>
        <xsl:if test="../TT">
          <a class="MedBlackText"><xsl:text> </xsl:text><b>(<xsl:value-of select="hlight:addMarkup(../TT)" disable-output-escaping="yes"/>)</b></a>
        </xsl:if>
        <xsl:if test="string(../DOC/TAGDATE)"> (Tag applied on <xsl:value-of select="../DOC/TAGDATE"/>)</xsl:if>
        </a><br/>
    </xsl:template>

    <xsl:template match="CITEDBY">
      <xsl:variable name="SESSION-ID"><xsl:value-of select="/PAGE/SESSION-ID"/></xsl:variable>
      <xsl:variable name="CITEDBY-MD5"><xsl:value-of select="security:getCitedbyMD5($SESSION-ID,@AN)" /></xsl:variable>
      <span>
        <xsl:attribute name="NAME">citedbyspan</xsl:attribute>
        <xsl:attribute name="style">display:none;</xsl:attribute>
        <xsl:if test="(@ISSN)">
          <xsl:attribute name="ISSN"><xsl:value-of select="@ISSN"/></xsl:attribute>
        </xsl:if>
        <xsl:if test="(@firstIssue)">
          <xsl:attribute name="ISSUE"><xsl:value-of select="@firstIssue"/></xsl:attribute>
        </xsl:if>
        <xsl:if test="(@firstVolume)">
          <xsl:attribute name="VOLUME"><xsl:value-of select="@firstVolume"/></xsl:attribute>
        </xsl:if>
        <xsl:if test="(@firstPage)">
          <xsl:attribute name="PAGE"><xsl:value-of select="@firstPage"/></xsl:attribute>
        </xsl:if>
        <xsl:if test="(@DOI)">
          <xsl:attribute name="DOI"><xsl:value-of select="@DOI"/></xsl:attribute>
        </xsl:if>
        <xsl:if test="(@PII)">
          <xsl:attribute name="PII"><xsl:value-of select="@PII"/></xsl:attribute>
        </xsl:if>
        <xsl:if test="(@AN)">
          <xsl:attribute name="AN"><xsl:value-of select="@AN"/></xsl:attribute>
        </xsl:if>
        <xsl:attribute name="SECURITY"><xsl:value-of select="$CITEDBY-MD5"/></xsl:attribute>
        <xsl:attribute name="SESSION-ID"><xsl:value-of select="$SESSION-ID"/></xsl:attribute>
      </span>
    </xsl:template>

    <xsl:template match="BTI">
      <xsl:if test="(../BPP)='0'">
        <img border="0" width="56" height="72" style="float:left; margin-right:5px;">
          <xsl:attribute name="SRC"><xsl:value-of select="//BOOKIMGS"/>/images/<xsl:value-of select="../BN13"/>/<xsl:value-of select="../BN13"/>small.jpg</xsl:attribute>
          <xsl:attribute name="ALT"><xsl:value-of select="."/></xsl:attribute>
        </img>
      </xsl:if><a class="MedBlackText"><b><xsl:value-of select="." disable-output-escaping="yes"/></b></a>
      <xsl:if test="string(../BTST)"><a CLASS="MedBlackText"><b>: <xsl:value-of select="../BTST" disable-output-escaping="yes"/></b></a></xsl:if>
    </xsl:template>

    <!-- Book Section title -->
<!--    <xsl:template match="ST"><a class="MedBlackText"><b>Section:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/></a><br/></xsl:template> -->
    <!-- Book Chapter title -->
    <xsl:template match="BCT"><a class="MedBlackText"><b>Chapter:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/></a><br/></xsl:template>

    <!-- Book Record Page Number -->
    <xsl:template match="BPP">
      <xsl:if test="not(string(.)='0')">
        <a CLASS="MedBlackText">, Page <xsl:value-of select="." disable-output-escaping="yes"/></a>
      </xsl:if>
      <br/>
    </xsl:template>

    <!-- Book Pubyear and Publisher -->
    <xsl:template match="BYR|BPN">
      <xsl:text>, </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>

    <!-- Book ISBN / ISBN13 -->
    <!-- UGLY- since BN may be present in other doctypes and we only want it for Books, check the DBMASK -->
    <xsl:template match="BN|BN13">
      <xsl:if test="../DOC/DB/DBMASK='131072'"><xsl:text> </xsl:text><xsl:value-of select="@label"/>: <xsl:value-of select="." disable-output-escaping="yes"/></xsl:if>
    </xsl:template>

    <xsl:template match="KC">
      <a CLASS="SmBlackText">
      <xsl:text> </xsl:text><b>Kind:</b><xsl:text> </xsl:text> <xsl:value-of select="." disable-output-escaping="yes"/>
      </a>
    </xsl:template>
    <xsl:template match="KD">
      <a CLASS="SmBlackText">
      <xsl:text> </xsl:text><b>Kind:</b><xsl:text> </xsl:text> <xsl:value-of select="." disable-output-escaping="yes"/>
      <xsl:text> </xsl:text>
      </a>
    </xsl:template>

    <xsl:template match="AUS|IVS|EDS">
        <xsl:apply-templates />
    </xsl:template>

    <xsl:template match="RSP">
        <xsl:text> </xsl:text><b>Sponsor:</b><xsl:text> </xsl:text> <xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="RN_LABEL">
        <xsl:text> </xsl:text><b>Report:</b><xsl:text> </xsl:text> <xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>

    <xsl:template match="SO">
        <b>Source:</b><xsl:text> </xsl:text><i><xsl:value-of select="." disable-output-escaping="yes"/></i>
    </xsl:template>
    <xsl:template match="VO">
        <xsl:text>, </xsl:text><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="SD">
        <xsl:text>, </xsl:text><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="MT">
        <xsl:text>, </xsl:text><xsl:text> </xsl:text><i><xsl:value-of select="." disable-output-escaping="yes"/></i>
    </xsl:template>
    <xsl:template match="VT">
        <xsl:text>,</xsl:text><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="YR">
      <xsl:if test="../DOC/DB/DBNAME='NTIS'">
      <xsl:if test="(string(../PF)) or (string(../RSP)) or (string(../RN_LABEL))">
            <xsl:text>, </xsl:text>
      </xsl:if>
      <xsl:value-of select="." disable-output-escaping="yes"/>
      </xsl:if>
      <xsl:if test="not(../DOC/DB/DBNAME='NTIS')">
            <xsl:text>, </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
      </xsl:if>
    </xsl:template>

    <xsl:template match="PASM">
	<b> Assignee:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>

    <xsl:template match="EASM">
    	<a CLASS="SmBlackText"><b> Patent assignee: </b></a><xsl:apply-templates />
    </xsl:template>

    <xsl:template match="PAN">
        <b> Application number:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PAP">
        <b> Patent number:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>


    <xsl:template match="PIM|PINFO">
    <a CLASS="SmBlackText">
        <b> Patent information:</b><xsl:text> </xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
    </a>
    </xsl:template>


    <xsl:template match="PM">
    <a CLASS="SmBlackText">
        <b> Publication Number:</b><xsl:text> </xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
    </a>
    </xsl:template>
    <xsl:template match="PM1">
    <a CLASS="SmBlackText">
        <b>Publication Number:</b><xsl:text> </xsl:text><xsl:value-of select="hlight:addMarkup(.)" disable-output-escaping="yes"/>
    </a>
    </xsl:template>

    <!-- CBNB availability -->
    <xsl:template match="AV">
        <xsl:text> </xsl:text><b>Availability:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>

    <xsl:template match="PFD">
    <a CLASS="SmBlackText">
        <br/>
        <b> Filing date: </b><xsl:value-of select="." disable-output-escaping="yes"/>
    </a>
    </xsl:template>
    <xsl:template match="PIDD">
    <a CLASS="SmBlackText">
        <b> Patent issue date: </b><xsl:value-of select="." disable-output-escaping="yes"/>
    </a>
    </xsl:template>
    <xsl:template match="PPD">
    <a CLASS="SmBlackText">
        <b> Publication date: </b><xsl:value-of select="." disable-output-escaping="yes"/>
    </a>
    </xsl:template>
    <!-- EPT -->
    <xsl:template match="UPD">
       <xsl:choose>
       		<xsl:when test="not(ancestor::EI-DOCUMENT/DOC/DB/DBMASK='2048')">
       			<a CLASS="SmBlackText">
        			<b> Publication date: </b><xsl:value-of select="." disable-output-escaping="yes"/>
    			</a>
       		</xsl:when>
       		<xsl:otherwise>
       			<a CLASS="SmBlackText">
        			<b> Publication year: </b><xsl:value-of select="." disable-output-escaping="yes"/>
    			</a>
         	</xsl:otherwise>
       </xsl:choose>
    </xsl:template>

  <!-- jam added for 1884 -->
    <xsl:template match="PID">
    <a CLASS="SmBlackText">
        <b> Patent issue date:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </a>
    </xsl:template>
  <!-- jam added for 1884  AND INSPEC Patents-->
    <xsl:template match="COPA">
    <a CLASS="SmBlackText">
        <b> Country of application:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </a>
    </xsl:template>

    <xsl:template match="PD_YR">
        <b> Publication date: </b><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="NV">
        <xsl:text>, </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <!-- publisher will ONLY show if there is no other leading information
        so this will follow 'Source' label - no comma needed                -->
    <xsl:template match="PN">
        <xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>

    <xsl:template match="RN">
        <xsl:text>, </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PA">
        <xsl:text>, </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PP">
        <xsl:text>, </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>

    <xsl:template match="ARN">
    	<xsl:text>, art. no. </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>

    <xsl:template match="p_PP">
        <xsl:text>, p </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="PP_pp">
        <xsl:text>, </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text> pp</xsl:text>
    </xsl:template>

    <!-- removed "SmBlkText" anchor class setting - these elements are already surrounded bay <a>...</a> in EI-DOCUMENT template above -->
    <xsl:template match="LA">
        <xsl:text> </xsl:text><b>Language:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    <xsl:template match="NF">
      <xsl:text> </xsl:text><b>Figures:</b><xsl:text> </xsl:text><xsl:value-of select="." disable-output-escaping="yes"/>
    </xsl:template>
    
    <xsl:template match="DT">    
    	<xsl:if test="text()='Article in Press'">
    	      <span CLASS="MedBlackText"><br/><xsl:text> Article in Press</xsl:text></span>
        </xsl:if> 
    </xsl:template>

    <xsl:template match="DBNAME">
        <a CLASS="SmBlackText"><br/><b>Database:</b><xsl:text> </xsl:text><xsl:value-of select="."/></a><xsl:text> </xsl:text>
    </xsl:template>
    <xsl:template match="COL">
        <a CLASS="SmBlackText"><xsl:text> </xsl:text><b>Collection:</b><xsl:text> </xsl:text><xsl:value-of select="text()"/></a><xsl:text> </xsl:text>
    </xsl:template>
    

    <xsl:template match="FTTJ|STT">
        <br/>
        <a CLASS="SmBlackText"><xsl:value-of select="." disable-output-escaping="yes"/></a>
    </xsl:template>

    <xsl:template match="PF">
    <xsl:text> </xsl:text><xsl:text>(</xsl:text><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text>)</xsl:text>
    </xsl:template>

<!-- AFF for Inspec -->

    <xsl:template match="AFF">
    	<xsl:if test="../DOC/DB/DBMASK='2'" >
      		<xsl:apply-templates/>
      	</xsl:if>
    </xsl:template>

    <xsl:template match="AF">
    	<xsl:if test="../DOC/DB/DBMASK='2'" >
            <span CLASS="SmBlackText"><xsl:text> </xsl:text><xsl:text>(</xsl:text><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text>)</xsl:text></span>
        </xsl:if>
    </xsl:template>

<!-- end of AFF for Inspec -->

    <xsl:template match="EF">
            <span CLASS="SmBlackText"><xsl:text> </xsl:text><xsl:text>(</xsl:text><xsl:value-of select="." disable-output-escaping="yes"/><xsl:text>)</xsl:text></span>
    </xsl:template>
    <xsl:template match="AU|ED|IV|EAS">

        <xsl:variable name="NAME"><xsl:value-of select="normalize-space(text())"/></xsl:variable>
        <xsl:variable name="FIELD">
        	<xsl:choose>
        		<xsl:when test="name(.)='EAS'">AF</xsl:when>
        		<xsl:otherwise>AU</xsl:otherwise>
        	</xsl:choose>
        </xsl:variable>

        <xsl:variable name="THIS-DOCUMENT-DB">
            <xsl:choose>
                <xsl:when test="(../../DOC/DB/DBMASK)='32'">1</xsl:when>
                <xsl:when test="(../../DOC/DB/DBMASK)='4096'">2</xsl:when>
                <xsl:otherwise><xsl:value-of select="../../DOC/DB/DBMASK"/></xsl:otherwise>
            </xsl:choose>
        </xsl:variable>


        <xsl:variable name="DATABASE">
            <xsl:choose>
                <!-- View Saved Records -->
                <xsl:when test="string(/PAGE/FOLDER-ID)">
                    <xsl:value-of select="$THIS-DOCUMENT-DB"/>
                </xsl:when>
                <!-- View Selected Set -->
                <xsl:when test="contains(/PAGE/CID,'SelectedSet')">
                    <xsl:choose>
                        <!-- View Selected Set - Multiple Queries -->
                        <xsl:when test="(/PAGE/HAS-MULTIPLE-QUERYS='true')">
                            <xsl:value-of select="$THIS-DOCUMENT-DB"/>
                        </xsl:when>
                        <!-- View Selected Set - Single Query -->
                        <xsl:otherwise>
                            <xsl:value-of select="/PAGE/DBMASK"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:when>
                <!-- Search Results Records -->
                <xsl:otherwise>
                    <xsl:value-of select="/PAGE/DBMASK"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>

        <xsl:variable name="ENCODED-DATABASE">
            <xsl:value-of select="java:encode($DATABASE)" />
        </xsl:variable>

        <!-- the linked field here is ALWAYS 'AU' (ED or IV) -->
<!--
        <xsl:variable name="START-YEAR">
            <xsl:choose>
                <xsl:when test="string(//RESULTS-DATABASES/DATABASE[@ID=$DATABASE]/DATABASE-START)"><xsl:value-of select="//RESULTS-DATABASES/DATABASE[@ID=$DATABASE]/DATABASE-START"/></xsl:when>
                <xsl:when test="string(//SESSION-DATA/START-YEAR)"><xsl:value-of select="//SESSION-DATA/START-YEAR"/></xsl:when>
                <xsl:when test="string(/PAGE/CUSTOMIZED-STARTYEAR)"><xsl:value-of select="/PAGE/CUSTOMIZED-STARTYEAR"/></xsl:when>
                <xsl:otherwise>1990</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="END-YEAR">
            <xsl:choose>
                <xsl:when test="string(//RESULTS-DATABASES/DATABASE[@ID=$DATABASE]/DATABASE-END)"><xsl:value-of select="//RESULTS-DATABASES/DATABASE[@ID=$DATABASE]/DATABASE-END"/></xsl:when>
                <xsl:when test="string(//SESSION-DATA/END-YEAR)"><xsl:value-of select="//SESSION-DATA/END-YEAR"/></xsl:when>
                <xsl:when test="string(/PAGE/CUSTOMIZED-ENDYEAR)"><xsl:value-of select="/PAGE/CUSTOMIZED-ENDYEAR"/></xsl:when>
                <xsl:otherwise>2005</xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
-->
    <!-- the variable $SEARCH-TYPE can be empty so string($SEARCH-TYPE) can be FALSE -->
    <!-- when there is no search in the XML output (when PRINTING) -->
    <!-- -->
    <!-- //SEARCH-TYPE is defined in a SEARCHRESULT as the type of the current search -->
    <!-- //SEARCH-TYPE is defined in a SELECTEDSET by the type of the last search  -->
    <!-- //SEARCH-TYPE is defined in a FOLDER by the type of the last search  -->
    <!--  if there is no search before viewing a folder, the XML <SEARCH-TYPE>NONE</SEARCH-TYPE> -->
    <!--  is output by viewSavedRecordsOfFolder.jsp when recentXMLQuery==null -->
        <xsl:variable name="SEARCH-TYPE">
            <xsl:value-of select="//SEARCH-TYPE"/>
        </xsl:variable>

        <!-- Default Sort Options for these links -->
        <!-- make changes here(like field or adding direction) -->
        <xsl:variable name="DEFAULT-LINK-SORT">sort=yr</xsl:variable>
        <!-- &amp;<xsl:value-of select="$DEFAULT-LINK-SORT"/> is appended to HREF attribute -->

        <!-- If the name does not contain the text Anon -->
        <xsl:if test="boolean(not(contains($NAME,'Anon')))">
            <A class="SpLink">
            	    <xsl:if test="not(/PAGE/LINK ='false') and not(/SECTION-DELIM/LINK ='false')">
                    <xsl:choose>
                         <xsl:when test="($SEARCH-TYPE='Expert') or ($SEARCH-TYPE='Combined') or ($SEARCH-TYPE='Easy')">
                            <xsl:attribute name="href"><xsl:value-of select="$HREF-PREFIX"/>/controller/servlet/Controller?CID=expertSearchCitationFormat&amp;searchWord1={<xsl:value-of select="java:encode($NAME)"/>}<xsl:value-of select="java:encode(' WN ')"/><xsl:value-of select="$FIELD"/>&amp;database=<xsl:value-of select="$ENCODED-DATABASE"/>&amp;yearselect=yearrange&amp;searchtype=<xsl:value-of select="$SEARCH-TYPE"/>&amp;<xsl:value-of select="$DEFAULT-LINK-SORT"/></xsl:attribute>
                        </xsl:when>
                        <xsl:when test="($SEARCH-TYPE='TagSearch')">
                            <xsl:attribute name="href"><xsl:value-of select="$HREF-PREFIX"/>/controller/servlet/Controller?CID=quickSearchCitationFormat&amp;searchWord1={<xsl:value-of select="java:encode($NAME)"/>}&amp;section1=<xsl:value-of select="$FIELD"/>&amp;database=<xsl:value-of select="$THIS-DOCUMENT-DB"/>&amp;yearselect=yearrange&amp;<xsl:value-of select="$DEFAULT-LINK-SORT"/></xsl:attribute>
                        </xsl:when>
                        <xsl:when test="($SEARCH-TYPE='Book')">
                            <xsl:attribute name="href"><xsl:value-of select="$HREF-PREFIX"/>/controller/servlet/Controller?CID=quickSearchCitationFormat&amp;searchWord1={<xsl:value-of select="java:encode($NAME)"/>}&amp;section1=<xsl:value-of select="$FIELD"/>&amp;database=131072&amp;searchtype=<xsl:value-of select="$SEARCH-TYPE"/>&amp;yearselect=yearrange&amp;<xsl:value-of select="$DEFAULT-LINK-SORT"/></xsl:attribute>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:attribute name="href"><xsl:value-of select="$HREF-PREFIX"/>/controller/servlet/Controller?CID=quickSearchCitationFormat&amp;searchWord1={<xsl:value-of select="java:encode($NAME)"/>}&amp;section1=<xsl:value-of select="$FIELD"/>&amp;database=<xsl:value-of select="$ENCODED-DATABASE"/>&amp;yearselect=yearrange&amp;<xsl:value-of select="$DEFAULT-LINK-SORT"/></xsl:attribute>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:if>
            <xsl:value-of select="$NAME" disable-output-escaping="yes"/>
            </A>
            <xsl:if test="position()=1 and (position()=last())">
                <xsl:if test="name(.)='ED'">
                    <a CLASS="SmBlackText"><xsl:text>, ed.</xsl:text> </a>
                </xsl:if>
            </xsl:if>
            <xsl:apply-templates select="AF"/>
            <xsl:apply-templates select="EF"/>
        </xsl:if>
        <!-- If the name contains Anon text -->
        <xsl:if test="boolean(contains($NAME,'Anon'))"><A CLASS="SmBlackText"><xsl:value-of select="$NAME"/></A></xsl:if>
        <xsl:if test="not(position()=last())">
        	<xsl:if test="not((position()=1 and (string(../../AFS/AF)))) ">
        		<A CLASS="SmBlackText">;</A><xsl:text> </xsl:text>
        	</xsl:if>
        </xsl:if>
        <xsl:if test="position()=last()">
            <xsl:if test="name(.)='ED' and not(position()=1)">
                <a CLASS="SmBlackText"><xsl:text> eds.</xsl:text></a>
            </xsl:if>
            <xsl:text> </xsl:text>
        </xsl:if>
        <xsl:if test="not(../DOC/DB/DBMASK='2')  and (position()=1) and (string(../../AFS/AF))" >
		<span CLASS="SmBlackText"><xsl:text> </xsl:text><xsl:text>(</xsl:text>
			<xsl:value-of select="../../AFS/AF" disable-output-escaping="yes"/>
		<xsl:text>)</xsl:text>

		<xsl:if test="not(position()=last())">
			<xsl:text>;</xsl:text>
		</xsl:if>

		<xsl:text> </xsl:text>
		</span>
        </xsl:if>
    </xsl:template>

</xsl:stylesheet>
