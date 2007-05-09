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
	 This XSL displays all the folders for the user and also provides the option
	 for creating the new folder if the existing folders are less than three.
-->

<xsl:template match="PAGE">

<xsl:variable name="DATABASE">
		<xsl:value-of  select="DATABASE"/>
</xsl:variable>

<xsl:variable name="SESSION-ID">
	<xsl:value-of select="SESSION-ID"/>
</xsl:variable>

<xsl:variable name="NUMBER-OF-FOLDERS">
		<xsl:value-of select="FOLDERS/NUMBER-OF-FOLDERS"/>
</xsl:variable>

<html>
<head>
	<title>My Saved Records Folders</title>
  	<SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js"/>

	<xsl:text disable-output-escaping="yes">
		<![CDATA[
			<xsl:comment>
				<SCRIPT type="TEXT/JAVASCRIPT" language="javascript">
					// This function validates all the fields before submition.
					function validate(folder)
					{
						if (folder.foldername.value =="")
						{
								alert ("Please name the new folder, or choose an existing folder.");
								folder.foldername.focus();
								return false;
						}
						else
						{
								var folderLength= folder.foldername.value.length;
								var tempWord = folder.foldername.value;
								var tempLength=0;

								while (tempWord.substring(0,1) == ' ')
								{
								   tempWord = tempWord.substring(1);
								   tempLength = tempLength + 1;
								}

								if ( folderLength == tempLength)
								{
								   window.alert("Spaces are not allowed as folder name");
								   folder.foldername.focus();
								   return false;
								}

								var foldername= folder.foldername.value;
								var valid = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789_";
								var temp;
								for (var i=0; i<foldername.length; i++)
								{
									temp = foldername.substring(i, i+1);
									if ((valid.indexOf(temp) == "-1") && ( temp != ' '))
									{
										window.alert("Special Characters are not allowed");
								   		folder.foldername.focus();
								   		return false;
									}
								}

						}
						var hiddensize = document.folderslist.elements.length;
						for( var i=0 ; i < hiddensize ; i++)
						{
								var nameOfElement = document.folderslist.elements[i].name;
								var valueOfElement = document.folderslist.elements[i].value;

								if((nameOfElement.search(/FOLDER-NAME/)!=-1) && (valueOfElement != ""))
								{
									if(valueOfElement.toLowerCase() == folder.foldername.value.toLowerCase())
									{
										window.alert(" Choose another Folder Name");
										folder.foldername.focus();
										return false;
									}
								}
						}
						return true;
					}
					// This function displays alert before removing the folder.
					function alertDelete(sessionid,folderid,database)
					{
							var agree=confirm("Are you sure you want to remove the folder and the contents in it?");
							if(agree)
							{
								var url="/controller/servlet/Controller?EISESSION="+sessionid+"&CID=deletePersonalFolder&folderid="+folderid+"&database="+database;
								document.location = url;
							}
					}

				</SCRIPT>
			</xsl:comment>
		]]>
	</xsl:text>
	<!-- -->

</head>

<body bgcolor="#FFFFFF" onLoad="javascript:window.focus();" topmargin="0" marginheight="0" marginwidth="0">

<!-- Table for the logo area -->
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

  <!-- Gray Bar (empty navigation bar) below Global Links -->
  <table border="0" width="99%" cellspacing="0" cellpadding="0" bgcolor="#C3C8D1">
    <tr ><td height="24" bgcolor="#C3C8D1" ><img src="/engresources/images/s.gif" /></td></tr>
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
                <a CLASS="LgWhiteText"><b>My Folders</b></a>
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

                  	<FORM name="folderslist">

                      <!-- iterate through the loop to display folder names -->
                    	<xsl:for-each select="FOLDERS/FOLDER">

                        <xsl:variable name="FOLDER-ID">
                          <xsl:value-of select="FOLDER-ID"/>
                        </xsl:variable>
                        <xsl:variable name="FOLDER-NAME">
                          <xsl:value-of select="FOLDER-NAME"/>
                        </xsl:variable>
                        <xsl:variable name="ENCODED-FOLDER-NAME">
                          <xsl:if test="function-available('java:encode')">
                            <xsl:value-of select="java:encode(FOLDER-NAME)"/>
                          </xsl:if>
                        </xsl:variable>

                    			<tr><td valign="top" height="4" colspan="10"><img src="/engresources/images/spacer.gif" height="4"/></td></tr>
                    			<tr>
                    				<td valign="top" width="3" ><img src="/engresources/images/spacer.gif" border="0" width="3"/></td>
                    				<td valign="top" width="15"><img src="/engresources/images/folderclosed.gif" border="0"/></td>
                    				<td valign="top" width="3" ><img src="/engresources/images/spacer.gif" border="0" width="3"/></td>
                    				<td valign="top" >
                    					<a CLASS="MedBlackText"><b><xsl:value-of select="$FOLDER-NAME"/></b></a>
                    					<input type="hidden" name="FOLDER-NAME">
                    						<xsl:attribute name="value">
                    							<xsl:value-of select="$FOLDER-NAME"/>
                    						</xsl:attribute>
                    					</input>
                    				</td>
                    				<td valign="top" width="15" ><img src="/engresources/images/spacer.gif" border="0" width="15"/></td>
                    				<td valign="top" >
                    					<a CLASS="MedBlueLink" ><xsl:attribute name="href">/controller/servlet/Controller?EISESSION=$SESSIONID&amp;CID=viewCitationSavedRecords&amp;database=<xsl:value-of select="$DATABASE"/>&amp;folderid=<xsl:value-of select="$FOLDER-ID"/></xsl:attribute>View Folder</a>
                    				</td>
                    				<td valign="top" width="15" ><img src="/engresources/images/spacer.gif" border="0" width="15"/></td>
                    				<td valign="top" >
                    					<a CLASS="MedBlueLink" href="/controller/servlet/Controller?CID=renamePersonalFolder&amp;folderedit=true&amp;folderid={$FOLDER-ID}&amp;oldfoldername={$ENCODED-FOLDER-NAME}&amp;database={$DATABASE}">
                    						Rename Folder
                    					</a>
                    				</td>
                    				<td valign="top" width="15" ><img src="/engresources/images/spacer.gif" border="0" width="15"/></td>
                    				<td valign="top" >
                    					<a CLASS="MedBlueLink" href="javascript:alertDelete('$SESSIONID','{$FOLDER-ID}','{$DATABASE}')" >
                    						Delete Folder
                    					</a>
                    				</td>
                    			</tr>
                    		</xsl:for-each>
                    	</FORM>

                    	<!-- if the number of folders are less than three display the form to create new folder -->
                    	<xsl:if test="($NUMBER-OF-FOLDERS &lt; 3)">
                    	<FORM name="folder" action="/controller/servlet/Controller?CID=addPersonalFolder&amp;database={$DATABASE}" METHOD="POST" onSubmit="return validate(folder)">
                    		<tr><td valign="top"  height="14" colspan="10"><img src="/engresources/images/spacer.gif" height="10"/></td></tr>
                    		<tr>
                    			<td valign="top"  colspan="10">
                    				<a CLASS="MedBlackText">To Create a new folder, please enter a folder name:<br/>
                    				&#160;<input type="text" name="foldername" size="28" maxlength="32"/>&#160;</a>
                            &#160;<input type="image" src="/engresources/images/sav.gif" name="submit" value="Submit" alt="Save" /> &#160; &#160;
                            &#160;<input type="image" src="/engresources/images/cancel.gif" name="cancel" value="Cancel" alt="return to My Profile"  onclick="javascript:document.location='/controller/servlet/Controller?EISESSION={$SESSION-ID}&amp;CID=myprofile&amp;database={$DATABASE}';return false;"/> &#160; &#160;
  				                  <input type="image" src="/engresources/images/res.gif" name="reset" value="Reset" alt="Reset" onclick="javascript:reset();return false;"/> &#160; &#160;
                    			</td>
                    		</tr>
                    	</FORM>
                    	</xsl:if>
                    	<!-- end of folder creation form -->
                  		<tr><td valign="top"  height="4" colspan="10"><img src="/engresources/images/spacer.gif" height="4"/></td></tr>
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
