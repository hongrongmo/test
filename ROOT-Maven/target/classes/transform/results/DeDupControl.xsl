<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:html="http://www.w3.org/TR/REC-html40"
  exclude-result-prefixes="html xsl "
>

<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="html:* xsl:*" />

<xsl:template name="DEDUP-NEW">
  <xsl:param name="DEDUP"/>
  <xsl:param name="FIELDPREF"/>
  <xsl:param name="DBPREF"/>

  <xsl:variable name="SESSION-ID">
    <xsl:value-of select="SESSION-ID" />
  </xsl:variable>

  <xsl:variable name="SEARCH-ID">
    <xsl:value-of select="SEARCH-ID"/>
  </xsl:variable>

  <xsl:variable name="CURRENT-PAGE">
    <xsl:value-of select="//CURR-PAGE-ID"/>
  </xsl:variable>

  <xsl:variable name="RESULTS-COUNT">
    <xsl:value-of select="//RESULTS-COUNT"/>
  </xsl:variable>

  <xsl:variable name="DATABASE">
    <xsl:value-of select="SESSION-DATA/DATABASE-MASK"/>
  </xsl:variable>

  <xsl:variable name="DUPDB">
    <xsl:value-of select="//DUPDB"/>
  </xsl:variable>

  <xsl:variable name="DEDUPSETSIZE">
    <xsl:value-of select="//DEDUPSETSIZE"/>
  </xsl:variable>


<!-- CS added template for new dedup control -->
  <table border="0" width="100%" cellspacing="0" cellpadding="0">
    <tr>
      <td height="20" bgcolor="#FFFFFF" colspan="2"><img src="/static/images/s.gif" height="20"/></td>
    </tr>
    <tr>
      <td colspan="3" height="1" bgcolor="#3173b5"><img src="/static/images/s.gif" height="1"/></td>
    </tr>
    <tr>
      <td width="1" bgcolor="#3173b5"><img src="/static/images/s.gif" width="1"/></td>
      <td valign="top">
      <a NAME="removedup">
      <form name="removedup" action="/search/results/dedup.url?EISESSION={$SESSION-ID}&amp;CID=dedup&amp;SEARCHID={$SEARCH-ID}&amp;COUNT={$CURRENT-PAGE}&amp;RESULTSCOUNT={$RESULTS-COUNT}&amp;database={$DATABASE}&amp;DupFlag=true" method="POST">
      <table border="0" cellspacing="0" cellpadding="0" width="100%">
        <tr>
          <td width="2" bgcolor="#3173B5"><img src="/static/images/s.gif" width="2"/></td>
          <td height="10" colspan="3" bgcolor="#3173B5"><a CLASS="LgWhiteText"><b>Remove Duplicates</b></a></td>
        </tr>
        <tr>
          <td colspan="4" height="8"><img src="/static/images/s.gif" height="8"/></td>
        </tr>
        <tr>
          <td width="2"><img src="/static/images/s.gif" width="2"/></td>
          <td valign="top" colspan="3"><a CLASS="MedBlackText">Duplicate records will be removed from the first 1000 records in the result set. Use the form below to choose the fields and the database that you prefer to see results from.</a></td>
        </tr>
        <tr>
          <td colspan="4" height="4"><img src="/static/images/s.gif" height="4"/></td>
        </tr>
        <tr>
          <td width="4"><img src="/static/images/s.gif" width="4"/></td>
          <td valign="top" width="40%">
            <table border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td valign="top"><a class="MedBlackText"><b>Field Preferences:</b></a></td>
              </tr>
              <tr>
                <td valign="middle">
                <xsl:choose>
                    <xsl:when test="($FIELDPREF='0')">
                      &#160; &#160; <input type = "radio" name="fieldpref" value="0" checked="checked"/><a CLASS="MedBlackText">No field preference</a><br/>
                      &#160; &#160; <input type = "radio" name="fieldpref" value="4"/><a CLASS="MedBlackText">Has Full Text</a><br/>
                      &#160; &#160; <input type = "radio" name="fieldpref" value="1"/><a CLASS="MedBlackText">Has Abstract</a><br/>
                      &#160; &#160; <input type = "radio" name="fieldpref" value="2"/><a CLASS="MedBlackText">Has Index Terms</a><br/>
                    </xsl:when>
                    <xsl:when test="($FIELDPREF='4')">
                      &#160; &#160; <input type = "radio" name="fieldpref" value="0"/><a CLASS="MedBlackText">No field preference</a><br/>
                      &#160; &#160; <input type = "radio" name="fieldpref" value="4" checked="checked"/><a CLASS="MedBlackText">Has Full Text</a><br/>
                      &#160; &#160; <input type = "radio" name="fieldpref" value="1"/><a CLASS="MedBlackText">Has Abstract</a><br/>
                      &#160; &#160; <input type = "radio" name="fieldpref" value="2"/><a CLASS="MedBlackText">Has Index Terms</a><br/>
                    </xsl:when>
                    <xsl:when test="($FIELDPREF='1')">
                      &#160; &#160; <input type = "radio" name="fieldpref" value="0"/><a CLASS="MedBlackText">No field preference</a><br/>
                      &#160; &#160; <input type = "radio" name="fieldpref" value="4"/><a CLASS="MedBlackText">Has Full Text</a><br/>
                      &#160; &#160; <input type = "radio" name="fieldpref" value="1" checked="checked"/><a CLASS="MedBlackText">Has Abstract</a><br/>
                      &#160; &#160; <input type = "radio" name="fieldpref" value="2"/><a CLASS="MedBlackText">Has Index Terms</a><br/>
                    </xsl:when>
                    <xsl:when test="($FIELDPREF='2')">>
                      &#160; &#160; <input type = "radio" name="fieldpref" value="0"/><a CLASS="MedBlackText">No field preference</a><br/>
                      &#160; &#160; <input type = "radio" name="fieldpref" value="4"/><a CLASS="MedBlackText">Has Full Text</a><br/>
                      &#160; &#160; <input type = "radio" name="fieldpref" value="1"/><a CLASS="MedBlackText">Has Abstract</a><br/>
                      &#160; &#160; <input type = "radio" name="fieldpref" value="2" checked="checked"/><a CLASS="MedBlackText">Has Index Terms</a><br/>
                    </xsl:when>
                    <xsl:otherwise>
                      &#160; &#160; <input type = "radio" name="fieldpref" value="0" checked="checked"/><a CLASS="MedBlackText">No field preference</a><br/>
                      &#160; &#160; <input type = "radio" name="fieldpref" value="4"/><a CLASS="MedBlackText">Has Full Text</a><br/>
                      &#160; &#160; <input type = "radio" name="fieldpref" value="1"/><a CLASS="MedBlackText">Has Abstract</a><br/>
                      &#160; &#160; <input type = "radio" name="fieldpref" value="2"/><a CLASS="MedBlackText">Has Index Terms</a><br/>
                    </xsl:otherwise>
                </xsl:choose>
                </td>
              </tr>
            </table>
          </td>
          <td width="10"><img src="/static/images/s.gif" width="10"/></td>
          <td valign="top" width="60%">
            <table border="0" cellspacing="0" cellpadding="0">
              <tr>
                <td valign="top"><a class="MedBlackText"><b>Database Preferences:</b></a></td>
              </tr>
              <tr>
                <td height="2"><img src="/static/images/s.gif" height="2"/></td>
              </tr>
              <tr>
                <td valign="middle">&#160; &#160; <a class="MedBlackText">
                  <select name="dbpref">
                      <xsl:choose>
                        <xsl:when test="($DBPREF='cpx')">
                            <option value="cpx" selected="selected">Compendex</option>
                            <option value="ins">Inspec</option>
                        </xsl:when>
                        <xsl:when test="($DBPREF='ins')">
                            <option value="cpx">Compendex</option>
                            <option value="ins" selected="selected">Inspec</option>
                        </xsl:when>
                        <xsl:otherwise>
                            <option value="cpx" selected="selected">Compendex</option>
                            <option value="ins">Inspec</option>
                        </xsl:otherwise>
                      </xsl:choose>
                  </select></a>
                </td>
              </tr>
              <tr>
                <td height="10"><img src="/static/images/s.gif" border="0" height="10"/></td>
              </tr>
              <tr>
                <td valign="top">&#160; &#160; <input type="image" src="/static/images/cont.gif" border="0"/></td>
              </tr>
            </table>
          </td>
        </tr>
        <tr>
          <td height="6"><img src="/static/images/s.gif" height="6"/></td>
        </tr>
      </table>
      </form>
      </a>
      </td>
      <td width="1" bgcolor="#3173b5"><img src="/static/images/s.gif" width="1"/></td>
    </tr>
    <tr>
      <td colspan="3" height="1" bgcolor="#3173b5"><img src="/static/images/s.gif" width="1"/></td>
    </tr>
  </table>
</xsl:template>


<xsl:template name="DEDUP-SCRIPT">
<script language="javascript">
<xsl:comment>
<xsl:text disable-output-escaping="yes">
<![CDATA[
]]>
</xsl:text>
// </xsl:comment>
</script>
</xsl:template>

<xsl:template name="DUPRECORD">

<xsl:param name="HIT-INDEX" />

<tr><td valign="top" colspan="3" height="5"><img src="/static/images/s.gif"/></td></tr>
<tr><td valign="top" colspan="3" height="20"><img src="/static/images/s.gif"/></td></tr>
<tr><td valign="top" align="right">&#160; </td><td valign="middle"><a class="MedBlackText"><xsl:value-of select="$HIT-INDEX" />.</a></td><td valign="top" width="4"><img src="/static/images/s.gif" width="4" /></td>
<td valign="middle" width="100%" align="left" bgcolor="#DCDCDC" height="15"><a class="MedBlackText">Removed duplicate record from <b><xsl:value-of select="//DUPDBFULL"/></b></a></td></tr>
<tr><td valign="top" colspan="3" height="20"><img src="/static/images/s.gif"/></td></tr>
</xsl:template>

<xsl:template name="DUPRECORD-FULL">

<xsl:param name="HIT-INDEX" />

<tr><td valign="top" align="right">&#160; </td><td valign="middle"><a class="MedBlackText"><xsl:value-of select="$HIT-INDEX" />.</a></td><td valign="top" width="4"><img src="/static/images/s.gif" width="4" /></td>
<td valign="middle" width="100%" align="left" bgcolor="#DCDCDC" height="15"><a class="MedBlackText">Removed duplicate record from <b><xsl:value-of select="//DUPDBFULL"/></b></a><br /></td></tr>
<tr><td valign="top" colspan="3" height="80"><img src="/static/images/s.gif"/></td></tr>
</xsl:template>

</xsl:stylesheet>