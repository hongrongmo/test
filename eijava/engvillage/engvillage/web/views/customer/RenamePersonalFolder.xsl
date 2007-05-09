<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:html="http://www.w3.org/TR/REC-html40"
	xmlns:java="java:java.net.URLEncoder"
	exclude-result-prefixes="java html xsl"
>
<xsl:include href="Header.xsl" />
<xsl:include href="GlobalLinks.xsl" />
<xsl:include href="Footer.xsl" />
<!--
	This file displays the old folder name and text box for new folder name
-->


<xsl:template match="PAGE">

<xsl:variable name="DATABASE">
	<xsl:value-of  select="DATABASE"/>
</xsl:variable>

<xsl:variable name="SESSION-ID">
	<xsl:value-of select="SESSION-ID"/>
</xsl:variable>

<xsl:variable name="FOLDER-ID">
  <xsl:value-of select="FOLDER/FOLDER-ID"/>
</xsl:variable>


<html>
<head>
	<title> Rename Folder </title>
  	<SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js"/>

	<xsl:text disable-output-escaping="yes">
		<![CDATA[
			<xsl:comment>
				<SCRIPT type="TEXT/JAVASCRIPT" language="javascript">
					// this function validates the form fields before submition.
					function validate(savefolder)
					{
						if (savefolder.newfoldername.value =="")
						{
								window.alert ("Please enter a folder name");
								savefolder.newfoldername.focus();
								return false;
						}
						else
						{
								var folderLength= savefolder.newfoldername.value.length;
								var tempWord = savefolder.newfoldername.value;
								var tempLength=0;

								while (tempWord.substring(0,1) == ' ')
								{
								   tempWord = tempWord.substring(1);
								   tempLength = tempLength + 1;
								}

								if ( folderLength == tempLength)
								{
								   window.alert("Spaces are not allowed as folder name");
								   savefolder.newfoldername.focus();
								   return false;
								}
								var foldername= savefolder.newfoldername.value;
								var valid = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_";
								var temp = null;
								for (var i=0; i<foldername.length; i++)
								{
									temp = foldername.substring(i, i+1);
									if ((valid.indexOf(temp) == "-1") && ( temp != ' '))
									{
										window.alert("Special Characters are not allowed");
										savefolder.newfoldername.focus();
										return false;
									}
								}
						 }
						 var hiddensize = document.savefolder.elements.length;
						 var i = 0;
						 for( i=0 ; i < hiddensize ; i++)
						 {
								var nameOfElement = document.savefolder.elements[i].name;
								var valueOfElement = document.savefolder.elements[i].value;

								if((nameOfElement.search(/FOLDER-NAME/)!=-1) && (valueOfElement != ""))
								{
									if(valueOfElement.toLowerCase() == savefolder.newfoldername.value.toLowerCase())
									{
										window.alert(" Folder Name Already Exists");
										savefolder.newfoldername.focus();
										return false;
									}
								}
						 }
						 return true;
					}
				</SCRIPT>
			</xsl:comment>
		]]>
	</xsl:text>

	<!-- -->

</head>

<body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">

<!-- table for logo area -->
<center>

  <!-- APPLY HEADER -->
  <xsl:apply-templates select="HEADER">
  	<xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
  	<xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
  </xsl:apply-templates>

  <!-- Insert the Global Links -->
  <!-- logo, search history, selected records, my folders. end session -->
  <xsl:apply-templates select="GLOBAL-LINKS">
  	<xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
  	<xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
  </xsl:apply-templates>

	<!-- Start of topnavigation bar -->
	<table border="0" width="99%" cellspacing="0" cellpadding="0">
		<tr>
			<td valign="bottom" height="25" bgcolor="#C3C8D1">&#160;
			<a href="/controller/servlet/Controller?CID=viewPersonalFolders&amp;database={$DATABASE}">
			<img src="/engresources/images/myfolders.gif" border="0"/></a></td>
		</tr>
	</table>

      <!-- Center Main / DO NOT CHANGE -->
      <center>
        <!-- Outer Template Table / Blue Border / DO NOT CHANGE -->
        <table border="0" width="80%" cellspacing="0" cellpadding="0">
          <!-- Spacer Row / Provides space below Navigation Bar / DO NOT CHANGE -->
          <tr><td colspan="4" height="20"><img src="/engresources/images/s.gif" height="20" /></td></tr>
          <!-- Spacer Row / Blue Border / DO NOT CHANGE -->
          <tr><td colspan="3" height="1" bgcolor="#3173b5"><img src="/engresources/images/s.gif" height="1" /></td></tr>
          <tr>
            <!-- Spacer Cel / Blue Border / DO NOT CHANGE -->
            <td width="1" bgcolor="#3173b5"><img src="/engresources/images/s.gif" width="1" /></td>
            <!-- Content Cel / White (default) Background / DO NOT CHANGE -->
            <td valign="top">
              <!-- Inner Template Table / DO NOT CHANGE -->
              <table border="0" cellspacing="0" cellpadding="0" width="100%">
                <!-- Title Row / Indented with Spacer / Bold White Text on Blue Background -->
                <tr>
                  <!-- Indentation Cel / DO NOT CHANGE -->
                  <td width="2" bgcolor="#3173b5"><img src="/engresources/images/s.gif" width="2" /></td>
                  <!-- Title Cel / EDIT THIS -->
                  <td height="10" bgcolor="#3173B5" colspan="3">
                    <a CLASS="LgWhiteText"><b>Rename Folder</b></a>
                  </td>
                </tr>
                <!-- Spacer Row / White Background / DO NOT CHANGE -->
                <tr><td valign="top" height="10" colspan="4"><img src="/engresources/images/s.gif" border="0" height="10" /></td></tr>
                <!-- Content Row / Indented with Spacer / Content cell/should can contain table
                  to acheive specific content layout within template -->
                <tr>
                  <!-- Indentation Cel / DO NOT CHANGE -->
                  <td width="2"><img src="/engresources/images/s.gif" width="2" /></td>
                  <!-- Content Goes Here -->
                  <td colspan="3">
                    <!-- Content Goes Here -->
                      <table border="0" cellspacing="0" cellpadding="0" width="100%" >

                      		<FORM name="savefolder" action="/controller/servlet/Controller?CID=updatePersonalFolder&amp;database={$DATABASE}" METHOD="POST" onSubmit="return validate(savefolder)">
                          <tr><td valign="top" height="4" colspan="5"><img src="/engresources/images/s.gif" height="4" border="0"/></td></tr>
                          <tr><td valign="top" width="4"><img src="/engresources/images/s.gif" border="0"/></td><td valign="top" colspan="4"><a CLASS="MedBlackText">Please enter the new folder name in the appropriate field and click "Save" button to save changes. </a></td></tr>
                          <tr><td valign="top" height="4" colspan="5"><img src="/engresources/images/s.gif" height="4" border="0"/></td></tr>
                          <tr>
                            <td valign="top" width="4"><img src="/engresources/images/spacer.gif" border="0"/></td><td valign="top" align="right"><a CLASS="MedBlackText">Current folder name is:</a></td><td width="5"><img src="/engresources/images/s.gif" width="5"/></td><td valign="top">

                          		<xsl:for-each select="FOLDERS/FOLDER">
                          				<input type="hidden" name="FOLDER-NAME">
                          					<xsl:attribute name="value">
                          						<xsl:value-of select="FOLDER-NAME"/>
                          					</xsl:attribute>
                          				</input>
                          		</xsl:for-each>

                      				<a CLASS="MedBlackText"><b><xsl:value-of select="FOLDER/FOLDER-NAME"/></b></a>
                      				<input type="hidden" name="folderid">
                      				 	<xsl:attribute name="value"><xsl:value-of select="$FOLDER-ID"/></xsl:attribute>
                      				</input>
                            </td>
                          </tr>
                          <tr><td valign="top" height="4" colspan="5"><img src="/engresources/images/s.gif" height="4" border="0"/></td></tr>
                          <tr><td valign="top" width="4"><img src="/engresources/images/spacer.gif" border="0"/></td><td valign="top" align="right"><a CLASS="MedBlackText">New folder name is:</a></td><td width="5"><img src="/engresources/images/s.gif" width="5"/></td><td valign="top"><a CLASS="MedBlackText"><input type="text" name="newfoldername" size="28" maxlength="32"/></a></td></tr>
                          <tr><td valign="top" height="4" colspan="5"><img src="/engresources/images/s.gif" height="4" border="0"/></td></tr>
                          <tr>
                            <td valign="top" width="4"><img src="/engresources/images/spacer.gif" border="0"/></td>
                            <td valign="top" colspan="2">&#160;</td>
                            <td valign="top">
                              <input alt="Rename" type="image" src="/engresources/images/sav.gif" name="submit" value="Submit"/> &#160; &#160;
    				                  <input alt="Cancel" type="image" src="/engresources/images/cancel.gif" name="cancel" value="Cancel" onclick="javascript:document.location='/controller/servlet/Controller?EISESSION={$SESSION-ID}&amp;CID=viewPersonalFolders&amp;database={$DATABASE}';return false;"/> &#160; &#160;
                            </td>
                          </tr>
                          <tr><td valign="top" height="4" colspan="5"><img src="/engresources/images/s.gif" height="4" border="0"/></td></tr>
                      	</FORM>

                      </table>
                    <!-- Content Goes Here -->
                  </td>
                </tr>
                <!-- Sopacer Row Below Content / DO NOT CHANGE -->
                <tr><td valign="top" colspan="4" height="4"><img src="/engresources/images/s.gif" border="0" height="4" /></td></tr>
              </table>
            </td>
            <!-- Spacer Cel / Blue Border / DO NOT CHANGE -->
            <td width="1" bgcolor="#3173b5"><img src="/engresources/images/s.gif" width="1" /></td>
          </tr>
          <!-- Spacer Row / Blue Border / DO NOT CHANGE -->
          <tr><td colspan="3" height="1" bgcolor="#3173b5"><img src="/engresources/images/s.gif" height="1" /></td></tr>
        </table>
      </center>

  <!-- Insert the Footer table -->
  <xsl:apply-templates select="FOOTER">
  	<xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
  	<xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
  </xsl:apply-templates>

</center>
</body>
</html>

</xsl:template>
</xsl:stylesheet>
