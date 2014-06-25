<xsl:stylesheet
  version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  xmlns:java="java:java.net.URLDecoder"
    exclude-result-prefixes="java xsl">
    
<xsl:include href="Header.xsl"/>
<xsl:include href="GlobalLinks.xsl"/>
<xsl:include href="Footer.xsl"/>

<xsl:template match="PAGE">

    <xsl:variable name="SESSION-ID">
        <xsl:value-of select="SESSION-ID"/>
    </xsl:variable>
    <xsl:variable name="NUMBER-OF-FOLDERS">
        <xsl:value-of select="NUMBER-OF-FOLDERS"/>
    </xsl:variable>
    <xsl:variable name="MAX-FOLDER-SIZE">
        <xsl:value-of select="MAX-FOLDER-SIZE"/>
    </xsl:variable>
    <xsl:variable name="DATABASE">
        <xsl:value-of select="DATABASE"/>
    </xsl:variable>
    <xsl:variable name="SELECTED-SET">
    <xsl:value-of select="SELECTED-SET"/>
    </xsl:variable>

    <xsl:variable name="DOCID">
    <xsl:value-of select="DOCID"/>
    </xsl:variable>

    <xsl:variable name="HIDDEN-CID">
        <xsl:choose>
            <xsl:when test="($SELECTED-SET='true')">addSelectedSetRecords</xsl:when>
      <xsl:otherwise>saveRecordsToFolder</xsl:otherwise>
        </xsl:choose>
    </xsl:variable>

    <xsl:variable name="DOCUMENT-COUNT">
        <xsl:choose>
            <xsl:when test="($SELECTED-SET='true')"><xsl:value-of select="BASKET-SIZE"/></xsl:when>
      <xsl:otherwise>1</xsl:otherwise>
        </xsl:choose>
    </xsl:variable>
    
    <xsl:variable name="BACKURL">
        <xsl:value-of select="BACKURL"/>
    </xsl:variable>


<html>
<head>
    <title>Save Records to My Folder</title>
    <SCRIPT LANGUAGE="Javascript" SRC="/static/js/StylesheetLinks.js"/>
    <SCRIPT LANGUAGE="Javascript" SRC="/static/js/SaveToFolder.js"/>
</head>

<body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">

  <!-- APPLY HEADER -->
  <xsl:apply-templates select="HEADER">
    <xsl:with-param name="SESSION-ID" select="SESSION-ID"/>
    <xsl:with-param name="SELECTED-DB" select="DATABASE"/>
  </xsl:apply-templates>

    <center>
    
    <!-- Insert the Global Links -->
  <!-- logo, search history, selected records, my folders. end session -->
  <xsl:apply-templates select="GLOBAL-LINKS">
    <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
    <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
  </xsl:apply-templates>

  <!-- Gray Bar (empty navigation bar) below Global Links -->
  <center>
  <table border="0" width="99%" cellspacing="0" cellpadding="0" bgcolor="#C3C8D1">
    <tr height="24"><td bgcolor="#C3C8D1" ><img src="/static/images/s.gif" /></td></tr>
  </table>
  </center>
  
  <xsl:if test="boolean($DOCUMENT-COUNT &gt; 0)">
  
    <FORM name="savetofolder" action="/controller/servlet/Controller" METHOD="POST" onSubmit="return checkfolderforform(savetofolder)" >
      <input type="hidden" name="backurl"><xsl:attribute name="value"><xsl:value-of select="java:decode(BACKURL)"/></xsl:attribute></input>
      <input type="hidden" name="maxfoldersize"><xsl:attribute name="value"><xsl:value-of select="$MAX-FOLDER-SIZE"/></xsl:attribute></input>
      <input type="hidden" name="CID"><xsl:attribute name="value"><xsl:value-of select="$HIDDEN-CID"/></xsl:attribute></input>
      <input type="hidden" name="database"><xsl:attribute name="value"><xsl:value-of select="$DATABASE"/></xsl:attribute></input>
      <input type="hidden" name="documentcount"><xsl:attribute name="value"><xsl:value-of select="$DOCUMENT-COUNT"/></xsl:attribute></input>
      <input type="hidden" name="docid"><xsl:attribute name="value"><xsl:value-of select="$DOCID"/></xsl:attribute></input>

      <!-- Center Main / DO NOT CHANGE -->
      <center>
        <!-- Outer Template Table / Blue Border / DO NOT CHANGE -->
        <table border="0" width="80%" cellspacing="0" cellpadding="0">
          <!-- Spacer Row / Provides space below Navigation Bar / DO NOT CHANGE -->
          <tr><td colspan="4" height="20"><img src="/static/images/s.gif" height="20" /></td></tr>
          <!-- Spacer Row / Blue Border / DO NOT CHANGE -->
          <tr><td colspan="3" height="1" bgcolor="#3173b5"><img src="/static/images/s.gif" height="1" /></td></tr>
          <tr>
            <!-- Spacer Cel / Blue Border / DO NOT CHANGE -->
            <td width="1" bgcolor="#3173b5"><img src="/static/images/s.gif" width="1" /></td>
            <!-- Content Cel / White (default) Background / DO NOT CHANGE -->
            <td valign="top">
              <!-- Inner Template Table / DO NOT CHANGE -->
              <table border="0" cellspacing="0" cellpadding="0" width="100%">
                <!-- Title Row / Indented with Spacer / Bold White Text on Blue Background -->
                <tr>
                  <!-- Indentation Cel / DO NOT CHANGE -->
                  <td width="2" bgcolor="#3173b5"><img src="/static/images/s.gif" width="2" /></td>
                  <!-- Title Cel / EDIT THIS -->
                  <td height="10" bgcolor="#3173B5" colspan="3">
                    <a CLASS="LgWhiteText"><b>My Folders</b></a>
                  </td>
                </tr>
                <!-- Spacer Row / White Background / DO NOT CHANGE -->
                <tr><td valign="top" height="10" colspan="4"><img src="/static/images/s.gif" border="0" height="10" /></td></tr>
                <!-- Content Row / Indented with Spacer / Content cell/should can contain table 
                  to acheive specific content layout within template -->
                <tr>
                  <!-- Indentation Cel / DO NOT CHANGE -->
                  <td width="2"><img src="/static/images/s.gif" width="2" /></td>
                  <!-- Content Goes Here -->
                  <td colspan="3">
                    <!-- Content Goes Here -->

    <table border="0" cellspacing="0" cellpadding="0" width="100%">
      <tr><td valign="top" height="4" colspan="4"><img src="/static/images/s.gif" border="0" height="4"/></td></tr>
      
        <!-- Table below the logo area ,checks if the number of folders for this user == 0 -->
        <xsl:choose>
            <xsl:when test="boolean($NUMBER-OF-FOLDERS = 0)">
                <tr>
                  <td valign="top" width="4"><img src="/static/images/s.gif" border="0" width="4"/></td>
                        <td valign="top" colspan="2">
                        <a CLASS="MedBlackText">With your Personal Account, you can create up to three folders in which to save selected records. Each folder can contain up to 50 records.<br/>To create a new folder, please enter a folder name:</a>
                        </td>
                        <td valign="top" width="4"><img src="/static/images/s.gif" border="0" width="4"/></td>
                </tr>
            <tr><td valign="top" height="4" colspan="4"><img src="/static/images/s.gif" border="0" height="8"/></td></tr>
            <tr>
              <td valign="top" width="4"><img src="/static/images/s.gif" border="0" width="4"/></td>
                        <td valign="top" colspan="2">
                        <a CLASS="SmBlackText">
                        <input type="text" name="foldername" size="28" maxlength="32"/></a>
                        </td>
                        <td valign="top" width="4"><img src="/static/images/s.gif" border="0" width="4"/></td>
                </tr>
            </xsl:when>
        
            <!--checks if the no of folders for this user is less than 3 and greater than zero -->
            <xsl:when test="boolean($NUMBER-OF-FOLDERS &lt; 3) and not(boolean($NUMBER-OF-FOLDERS = 0))">
            <tr>
              <td valign="top" width="4"><img src="/static/images/s.gif" border="0" width="4"/></td>
              <td valign="top" colspan="2">
              <a CLASS="MedBlackText">Please select a folder below to save your selected records in.  Each folder can contain up to 50 records.  You can also save records to a new folder by entering a new folder name.</a>
              </td>
              <td valign="top" width="4"><img src="/static/images/s.gif" border="0" width="4"/></td>
            </tr>
            <tr><td valign="top" height="4" colspan="4"><img src="/static/images/s.gif" border="0" height="8"/></td></tr>
            <tr>
              <td valign="top" width="4"><img src="/static/images/s.gif" border="0" width="4"/></td>
              <td align="right" ><a CLASS="MedBlackText">My existing folders</a></td>
              <td valign="top">
                <input type="radio" name="choose" checked="checked"/>&#160;
                <a CLASS="SmBlackText">
                <select name="folderid" onfocus="selectradiodrop()">
                  <xsl:attribute name="size"><xsl:value-of select="$NUMBER-OF-FOLDERS"/></xsl:attribute>
                  <xsl:for-each select="FOLDERS/FOLDER">
                    <option>
                      <xsl:attribute name="value"><xsl:value-of select="FOLDER-ID"/></xsl:attribute>
                      <xsl:attribute name="size"><xsl:value-of select="FOLDER-SIZE"/></xsl:attribute>
                      <xsl:value-of select="FOLDER-NAME"/>
                    </option>
                  </xsl:for-each>
                </select>
                </a>
              </td>
              <td valign="top" width="4"><img src="/static/images/s.gif" border="0" width="4"/></td>
            </tr>
            <tr><td valign="top" height="4" colspan="4"><img src="/static/images/s.gif" border="0" height="4"/></td></tr>
            <tr>
              <td valign="top" width="4"><img src="/static/images/s.gif" border="0" width="4"/></td>
              <td align="right" >
              <a CLASS="MedBlackText">Create a folder</a>
              </td>
              <td valign="top">
                <input type="radio" name="choose"/>&#160;<a CLASS="SmBlackText">
                <input type="text" name="foldername" size="28"  maxlength="32" onfocus="selectradiotext()"/></a>
              </td>
              <td valign="top" width="4"><img src="/static/images/s.gif" border="0" width="4"/></td>
            </tr>
            </xsl:when>
        
            <!-- Table below the logo area, and checks if the no of folders for this user is equal to 3 -->
            <xsl:when test="boolean($NUMBER-OF-FOLDERS = 3)">
                <tr>
                  <td valign="top" width="4"><img src="/static/images/s.gif" border="0" width="4"/></td>
                  <td valign="top" colspan="2">
                  <a CLASS="MedBlackText">Please select a folder below in which to save your selected records (up to 50 records per folder):</a>
                  </td>
                  <td valign="top" width="4"><img src="/static/images/s.gif" border="0" width="4"/></td>
                </tr>
            <tr>
              <td valign="top" width="4"><img src="/static/images/s.gif" border="0" width="4"/></td>
                  <td valign="top" colspan="2">
                    <a CLASS="SmBlackText">
                  <select name="folderid" >
                    <xsl:attribute name="size"><xsl:value-of select="$NUMBER-OF-FOLDERS"/></xsl:attribute>
                    <xsl:for-each select="FOLDERS/FOLDER">
                      <option>
                        <xsl:attribute name="value"><xsl:value-of select="FOLDER-ID"/></xsl:attribute>
                        <xsl:attribute name="size"><xsl:value-of select="FOLDER-SIZE"/></xsl:attribute>
                        <xsl:value-of select="FOLDER-NAME"/>
                      </option>
                    </xsl:for-each>
                  </select>
                </a>
                    </td>
                    <td valign="top" width="4"><img src="/static/images/s.gif" border="0" width="4"/></td>
            </tr>
            </xsl:when>
        </xsl:choose>
  
            <tr><td valign="top" height="4" colspan="4"><img src="/static/images/s.gif" border="0" height="4"/></td></tr>
            <tr>
              <td valign="top" width="4"><img src="/static/images/s.gif" border="0" width="4"/></td>
        <xsl:if test="($NUMBER-OF-FOLDERS = 1) or ($NUMBER-OF-FOLDERS = 2)">
          <td >&#160;</td>
        </xsl:if>
                <td valign="top">
          <xsl:if test="($NUMBER-OF-FOLDERS = 0) or ($NUMBER-OF-FOLDERS = 3)">
            <xsl:attribute name="colspan">2</xsl:attribute>
          </xsl:if>
                <input type="image" src="/static/images/sav.gif" name="submit" value="Submit"/> &#160; &#160;
                <input type="image" src="/static/images/cancel.gif" name="cancel" value="Cancel"><xsl:attribute name="onclick">javascript:document.location='/controller/servlet/Controller?<xsl:value-of select="java:decode(BACKURL)"/>';return false;</xsl:attribute></input> &#160; &#160;
                </td>
                <td valign="top" width="4"><img src="/static/images/s.gif" border="0" width="4"/></td>
            </tr>
            <tr><td valign="top" height="4" colspan="4"><img src="/static/images/s.gif" border="0" height="4"/></td></tr>

    </table>
                    <!-- Content Goes Here -->
                  </td>
                </tr>
                <!-- Sopacer Row Below Content / DO NOT CHANGE -->
                <tr><td valign="top" colspan="4" height="4"><img src="/static/images/s.gif" border="0" height="4" /></td></tr>
              </table>
            </td>
            <!-- Spacer Cel / Blue Border / DO NOT CHANGE -->
            <td width="1" bgcolor="#3173b5"><img src="/static/images/s.gif" width="1" /></td>
          </tr>
          <!-- Spacer Row / Blue Border / DO NOT CHANGE -->
          <tr><td colspan="3" height="1" bgcolor="#3173b5"><img src="/static/images/s.gif" height="1" /></td></tr>
        </table>
      </center>

    </FORM>
  
  </xsl:if> <!-- $DOCUMENT-COUNT &gt; 0 -->

  </center>

  <xsl:if test="boolean($DOCUMENT-COUNT = 0)">
  
    <table border="0" width="99%" cellspacing="0" cellpadding="0" bgcolor="#FFFFFF">
        <tr><td valign="top" height="15" colspan="3"><img src="/static/images/s.gif" border="0"/></td></tr>
        <tr><td valign="top" width="20"><img src="/static/images/s.gif" border="0" width="20"/></td><td valign="top" colspan="2"><a class="EvHeaderText">Selected Records</a></td></tr>
        <tr><td valign="top" height="2" colspan="3"><img src="/static/images/s.gif" border="0"/></td></tr>
        <tr><td valign="top" width="20"><img src="/static/images/s.gif" border="0" width="20"/></td><td valign="top">
          <a CLASS="MedBlackText">You did not select any records to save to the folder. Please select records from the search results and try again.</a>
        </td><td valign="top" width="10"><img src="/static/images/s.gif" border="0" width="10"/></td></tr>
    </table>
  
    <br/>
  </xsl:if>

    <!-- Start of footer-->
    <xsl:apply-templates select="FOOTER">
        <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
        <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
    </xsl:apply-templates>

</body>
</html>
</xsl:template>
</xsl:stylesheet>
