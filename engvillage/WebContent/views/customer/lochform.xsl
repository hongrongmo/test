<?xml version="1.0"?>

<xsl:stylesheet
    version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:html="http://www.w3.org/TR/REC-html40"
    xmlns:java="java:java.net.URLEncoder"
    exclude-result-prefixes="java xsl html"
>
<xsl:template match="/">
  <xsl:variable name="ARTICLETITLE">
    <xsl:value-of select="//ARTICLETITLE"/>
  </xsl:variable>
  <xsl:variable name="AUTHOR">
    <xsl:value-of select = "//AUTHOR" />
  </xsl:variable>
  <xsl:variable name="SERIALTITLE">
    <xsl:value-of select="//SERIALTITLE"/>
  </xsl:variable>
  <xsl:variable name="SOURCE">
    <xsl:value-of select="//SOURCE"/>
  </xsl:variable>
  <xsl:variable name="CONFTITLE">
    <xsl:value-of select="//CONFTITLE"/>
  </xsl:variable>
  <xsl:variable name="ISSN">
    <xsl:value-of select="//ISSN"/>
  </xsl:variable>
  <xsl:variable name="ISBN">
    <xsl:value-of select="//ISBN"/>
  </xsl:variable>
  <xsl:variable name="VOLUME">
    <xsl:value-of select="//VOLUME"/>
  </xsl:variable>
  <xsl:variable name="ISSUE">
    <xsl:value-of select="//ISSUE"/>
  </xsl:variable>
  <xsl:variable name="STARTPAGE">
    <xsl:value-of select="//STARTPAGE"/>
  </xsl:variable>
  <xsl:variable name="YEAR">
    <xsl:value-of select="//YEAR"/>
  </xsl:variable>
  <xsl:variable name="EMAIL">
    <xsl:value-of select="//EMAIL"/>
  </xsl:variable>
  <HTML>
  <HEAD>
		<SCRIPT TYPE="text/javascript" LANGUAGE="Javascript" SRC="/static/js/StylesheetLinks.js"/>

    <TITLE>LocHoldings</TITLE>
  <xsl:text disable-output-escaping="yes">
  <![CDATA[
  <xsl:comment>
  <SCRIPT LANGUAGE="JavaScript">
  function Validate(){
    var eaddress = emailresults.emailaddress.value;
    if ((eaddress == null)||(eaddress == "")){
      alert("please enter email address");
      return false;
    } else if (eaddress.indexOf("@") < 0 ){
      alert("please enter valid email address");
      return false;
    } else if (eaddress.indexOf(".") < 0 ){
      alert("please enter valid email address");
      return false;
    } else {
      return true;
    }
  }
  </SCRIPT>
  </xsl:comment>
  ]]>
  </xsl:text>
  </HEAD>

  <BODY bgColor="#ffffff" topMargin="0" marginwidth="0" marginheight="0">

  <CENTER>

  <TABLE cellSpacing="0" cellPadding="0" width="99%" border="0">
    <TR>
      <TD vAlign="top"><IMG src="/static/images/ev2logo5.gif" border="0"/></TD>
      <TD align="right"><a href="javascript:window.close();">
      <img src="/static/images/close.gif" border="0"/></a></TD>
    </TR>
    <TR>
      <TD colspan="2" vAlign="top" height="5"><IMG height="5" src="/static/images/spacer.gif" border="0"/></TD></TR>
    <TR>
      <TD colspan="2" vAlign="top" bgColor="#3173b5" height="2">
      <IMG height="2" src="/static/images/spacer.gif" border="0" />
      </TD>
    </TR>
    <TR>
      <TD colspan="2" vAlign="top" height="10">
      <IMG height="10" src="/static/images/spacer.gif"  border="0"/></TD>
    </TR>
    </TABLE>
  <TABLE width="100%" border="0">
    <FORM name="emailresults" onsubmit="return Validate()" action="?CID=lochsentform" method="post">
    <TR>
    <TD width="100%" align="left" colspan="3">
      <TABLE id="table2" cellSpacing="0" cellPadding="0" width="100%" border="0">

        <xsl:if test="string($ARTICLETITLE)">
        <TR>
          <TD align="right" width="25%"><a class="MedBlackText"><b>ARTICLE TITLE:</b></a></TD>
      <td width="5"><img src="/static/images/s.gif" width="5"/></td>
          <TD align="left" width="75%">
          <a class="MedBlackText">
              <xsl:value-of select="$ARTICLETITLE" />
            </a></TD>
          <input type="hidden" name = "articletitle">
          <xsl:attribute name="value">
        <xsl:value-of select="$ARTICLETITLE"/>
      </xsl:attribute>
      </input>
    </TR>
    <TR>
      <TD colspan="3" height="5"><img src="/static/images/s.gif" height="5"/></TD>
    </TR>
    </xsl:if>

    <xsl:if test="string($AUTHOR)">
    <TR>
      <TD align="right" width="25%">
      <a class="MedBlackText"><b>AUTHOR:</b></a>
      </TD>
        <td width="5"><img src="/static/images/s.gif" width="5"/>
        </td>
        <TD align="left" width="75%">
        <a class="MedBlackText">
            <xsl:value-of select="$AUTHOR" />
          </a>
          </TD>
          <input type="hidden" name = "author">
          <xsl:attribute name="value">
        <xsl:value-of select="$AUTHOR"/>
      </xsl:attribute>
      </input>
    </TR>
    <TR>
      <TD colspan="3" height="5"><img src="/static/images/s.gif" height="5"/></TD>
    </TR>
    </xsl:if>


    <xsl:if test="string($SERIALTITLE)">
    <TR>
      <TD align="right" width="25%"><a class="MedBlackText"><b>SOURCE TITLE:</b></a> </TD>
        <td width="5"><img src="/static/images/s.gif" width="5"/></td>
        <TD align="left" width="75%">
        <a class="MedBlackText">
            <xsl:value-of select="$SERIALTITLE" />
          </a></TD>
          <input type="hidden" name = "serialtitle">
          <xsl:attribute name="value">
        <xsl:value-of select="$SERIALTITLE"/>
      </xsl:attribute>
      </input>
    </TR>
    <TR>
      <TD colspan="3" height="5"><img src="/static/images/s.gif" height="5"/></TD>
    </TR>
    </xsl:if>

    <xsl:if test="string($CONFTITLE)">
    <TR>
      <TD align="right" width="25%"><a class="MedBlackText"><b>CONFERENCE NAME:</b></a> </TD>
        <td width="5"><img src="/static/images/s.gif" width="5"/></td>
        <TD align="left" width="75%">
        <a class="MedBlackText">
            <xsl:value-of select="$CONFTITLE" />
          </a></TD>
          <input type="hidden" name = "conftitle">
          <xsl:attribute name="value">
        <xsl:value-of select="$CONFTITLE"/>
      </xsl:attribute>
      </input>
    </TR>
    <TR>
      <TD colspan="3" height="5"><img src="/static/images/s.gif" height="5"/></TD>
    </TR>
    </xsl:if>

        <xsl:if test="string($SOURCE)">
        <TR>
      <TD align="right" width="25%"><a class="MedBlackText"><b>SOURCE:</b></a> </TD>
        <td width="5"><img src="/static/images/s.gif" width="5"/></td>
        <TD align="left" width="75%">
        <a class="MedBlackText">
            <xsl:value-of select="$SOURCE" />
          </a></TD>
          <input type="hidden" name = "source">
          <xsl:attribute name="value">
        <xsl:value-of select="$SOURCE"/>
      </xsl:attribute>
      </input>
    </TR>
    <TR>
      <TD colspan="3" height="5"><img src="/static/images/s.gif" height="5"/></TD>
    </TR>
  </xsl:if>

    <xsl:if test="string($ISSN)">
        <TR>
          <TD align="right" width="25%"><a class="MedBlackText"><b>ISSN:</b></a></TD>
      <td width="5"><img src="/static/images/s.gif" width="5"/></td>
          <TD align="left" width="75%">
          <a class="MedBlackText">
              <xsl:value-of select="$ISSN" />
            </a></TD>
          <input type="hidden" name = "issn">
          <xsl:attribute name="value">
        <xsl:value-of select="$ISSN"/>
      </xsl:attribute>
      </input>
    </TR>
    <TR>
      <TD colspan="3" height="5"><img src="/static/images/s.gif" height="5"/></TD>
    </TR>
    </xsl:if>

    <xsl:if test="string($ISBN)">
        <TR>
          <TD align="right" width="25%"><a class="MedBlackText"><b>ISBN:</b></a></TD>
      <td width="5"><img src="/static/images/s.gif" width="5"/></td>
          <TD align="left" width="75%">
          <a class="MedBlackText">
              <xsl:value-of select="$ISBN" />
            </a></TD>
          <input type="hidden" name = "isbn">
          <xsl:attribute name="value">
        <xsl:value-of select="$ISBN"/>
      </xsl:attribute>
      </input>
    </TR>
    <TR>
      <TD colspan="3" height="5"><img src="/static/images/s.gif" height="5"/></TD>
    </TR>
    </xsl:if>

    <xsl:if test="string($VOLUME)">
        <TR>
          <TD align="right" width="25%"><a class="MedBlackText"><b>VOLUME:</b></a></TD>
      <td width="5"><img src="/static/images/s.gif" width="5"/></td>
          <TD align="left" width="75%"><a class="MedBlackText">
              <xsl:value-of select="$VOLUME" />
            </a></TD>
          <input type="hidden" name = "volume">
          <xsl:attribute name="value">
        <xsl:value-of select="$VOLUME"/>
      </xsl:attribute>
      </input>
    </TR>
    <TR>
      <TD colspan="3" height="5"><img src="/static/images/s.gif" height="5"/></TD>
    </TR>
    </xsl:if>

    <xsl:if test="string($ISSUE)">
        <TR>
          <TD align="right" width="25%"><a class="MedBlackText"><b>ISSUE:</b></a></TD>
      <td width="5"><img src="/static/images/s.gif" width="5"/></td>
          <TD align="left" width="75%"><a class="MedBlackText">
              <xsl:value-of select="$ISSUE" />
            </a></TD>
          <input type="hidden" name = "issue">
          <xsl:attribute name="value">
        <xsl:value-of select="$ISSUE"/>
      </xsl:attribute>
      </input>
    </TR>
    <TR>
      <TD colspan="3" height="5"><img src="/static/images/s.gif" height="5"/></TD>
    </TR>
    </xsl:if>

    <xsl:if test="string($STARTPAGE)">
        <TR>
          <TD align="right" width="25%"><a class="MedBlackText"><b>START PAGE:</b></a></TD>
      <td width="5"><img src="/static/images/s.gif" width="5"/></td>
          <TD align="left" width="75%"><a class="MedBlackText">
              <xsl:value-of select="$STARTPAGE" />
            </a></TD>
          <input type="hidden" name = "startpage">
          <xsl:attribute name="value">
        <xsl:value-of select="$STARTPAGE"/>
      </xsl:attribute>
      </input>
    </TR>
    <TR>
      <TD colspan="3" height="5"><img src="/static/images/s.gif" height="5"/></TD>
    </TR>
    </xsl:if>

    <xsl:if test="string($YEAR)">
        <TR>
          <TD align="right" width="25%"><a class="MedBlackText"><b>YEAR:</b></a></TD>
      <td width="5"><img src="/static/images/s.gif" width="5"/></td>
          <TD align="left" width="75%"><a class="MedBlackText">
            <xsl:value-of select="$YEAR" />
            </a></TD>
          <input type="hidden" name = "year">
          <xsl:attribute name="value">
        <xsl:value-of select="$YEAR"/>
      </xsl:attribute>
      </input>
    </TR>
    <TR>
      <TD colspan="3" height="5"><img src="/static/images/s.gif" height="5"/></TD>
    </TR>
    </xsl:if>
    <input type="hidden" name = "email">
          <xsl:attribute name="value">
        <xsl:value-of select="$EMAIL"/>
      </xsl:attribute>
    </input>

    <TR><TD colspan="3" height="20"><img src="/static/images/s.gif" height="20" border="0"/></TD></TR>
  </TABLE></TD>
    </TR>
    <TR>
      <TD width="10%"></TD>
      <TD width="80%"><a class="MedBlackText">Please fill in the form below to
    send a request for the above document to
    <xsl:value-of select="$EMAIL"/>
    </a></TD>
      <TD width="10%"></TD></TR>
    <TR>
      <TD width="10%"></TD>
      <TD align="middle" width="80%">
        <TABLE id="table3" width="100%" border="0">
        <TR>
          <TD valign="top" align="right">
          <a class="MedBlackText">Return email: </a>
      </TD>
          <TD><INPUT size="26" name="emailaddress"></INPUT></TD></TR>
      <TR><TD height="4"><img src="/static/images/s.gif" height="4"/></TD></TR>
        <TR>
          <TD valign="top" align="right"><a class="MedBlackText">Comments: </a></TD>
          <TD><TEXTAREA name="comments" rows="6" cols="64"></TEXTAREA></TD></TR>
        <TR>
          <TD></TD>
          <TD align="middle"><INPUT type="submit" value="Submit" name="B1"/>
          <INPUT type="reset" value="Reset" name="B2"/></TD></TR></TABLE></TD>
      <TD></TD></TR>
  <TR>
      <TD width="10%"></TD>
      <TD width="80%"></TD>
      <TD></TD>
    </TR>
  </FORM>
  </TABLE></CENTER>
  <br/><br/>
  <table width="100%" cellspacing="0" cellpadding="0" border="0">
  <tr><td>
  <center><A CLASS="SmBlackText">&#169; 2011 Elsevier Inc. All rights reserved.</A></center>
  </td></tr>
  </table>
  </BODY>
  </HTML>

</xsl:template>
</xsl:stylesheet>