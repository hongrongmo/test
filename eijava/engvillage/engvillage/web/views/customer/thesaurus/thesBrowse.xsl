<?xml version="1.0"?>
<xsl:stylesheet
  	 version="1.0"
  	 xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  	 xmlns:java="http://www.jclark.com/xt/java/java.net.URLEncoder"
  	 xmlns:sutil="http://www.jclark.com/xt/java/org.ei.util.StringUtil">


<!--- all XSL include files -->

<xsl:include href="../Header.xsl" />
<xsl:include href="ThesaurusHeader.xsl" />
<xsl:include href="../Footer.xsl" />
<xsl:include href="../GlobalLinks.xsl"/>
<xsl:include href="thesPath.xsl"/>
<xsl:include href="ThesaurusHelpText.xsl"/>


<!-- end of include -->

<xsl:variable name="TERM">
	<xsl:value-of select="//DOC/DATA/TERM"/>
</xsl:variable>

<xsl:variable name="RESULT">
	<xsl:value-of select="//DOC/RESULT"/>
</xsl:variable>


<xsl:variable name="ACTION">
	<xsl:value-of select="//DOC/ACTION"/>
</xsl:variable>

<xsl:variable name="DATABASE">
	<xsl:value-of select="//DOC/DATABASE"/>
</xsl:variable>

<xsl:variable name="SESSION-ID">
	<xsl:value-of select="SESSION-ID"/>
</xsl:variable>

<xsl:variable name="ENCODED-TERM">
	<xsl:value-of select="java:encode(//DOC/DATA/TERM)"/>
</xsl:variable>


<xsl:template match="DOC">

<html>
<head>
<title>Ei Thesaurus -- Browse</title>
        <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js"/>

<xsl:text disable-output-escaping="yes">
<![CDATA[
<SCRIPT LANGUAGE="JavaScript">
function validate()
{
	if ((document.search.for1.value.length == 0) || (document.search.for1.value = null))
	{
		alert ('Please enter some search term in the text box');
		search.for1.focus();
	}
}

function checkTerms()
{
	if(typeof(parent.clipFrame) != "undefined" &&
	   typeof(parent.clipFrame.document) != "undefined" &&
	   typeof(parent.clipFrame.document.forms[0]) != "undefined" &&
	   typeof(document.checkform) != "undefined" &&
	   typeof(document.checkform.selectUnselect) != "undefined")

	{

		var cliplength = parent.clipFrame.document.forms[0].clip.options.length;
		var optionsArray = new Array(cliplength);

		for (i=0; i < cliplength; i++)
		{
			optionsArray[parent.clipFrame.document.forms[0].clip.options[i].text] = 'YES';
		}


		var checkboxlength = document.checkform.selectUnselect.length;

		if(checkboxlength == undefined)
		{
			var mainStringaa = document.checkform.selectUnselect.value;
			var mainStringbb = replaceSubstring(mainStringaa,"(", "");
			var mainStringcc = replaceSubstring(mainStringbb,")", "");
			var mainString3 = mainStringcc;

			if(optionsArray[mainString3] == 'YES')
			{
				document.checkform.selectUnselect.checked = true;
			}

		}
		else
		{
			for (i=0;i<checkboxlength; i++)
			{
				var mainStringaa = document.checkform.selectUnselect[i].value;
				var mainStringbb = replaceSubstring(mainStringaa,"(", "");
				var mainStringcc = replaceSubstring(mainStringbb,")", "");
				var mainString3 = mainStringcc;

				if(optionsArray[mainString3] == 'YES')
				{
					document.checkform.selectUnselect[i].checked = true;
				}
			}

		}

	}
}




function clearTerms()
{
	if((typeof(document.checkform) != "undefined") && typeof(document.checkform.selectUnselect) != "undefined")
	{
		var checkboxlength = document.checkform.selectUnselect.length;
		if(checkboxlength == undefined)
		{
			document.checkform.selectUnselect.checked = false;
		}
		else
		{
			for (i=0;i<checkboxlength; i++)
			{
				document.checkform.selectUnselect[i].checked = false;
			}
		}
	}
}




function replaceSubstring(inputString, fromString, toString)
{
	// Goes through the inputString and replaces every occurrence of fromString with toString
	var temp = inputString;
	if (fromString == "")
	{
		return inputString;
	}
	if (toString.indexOf(fromString) == -1)
	{
		// If the string being replaced is not a part of the replacement string (normal situation)
		while (temp.indexOf(fromString) != -1)
		{
			var toTheLeft = temp.substring(0, temp.indexOf(fromString));
			var toTheRight = temp.substring(temp.indexOf(fromString)+fromString.length, temp.length);
			temp = toTheLeft + toString + toTheRight;
		}
	}
	else
	{
		// String being replaced is part of replacement string (like "+" being replaced with "++") - prevent an infinite loop
		var midStrings = new Array("~", "`", "_", "^", "#");
		var midStringLen = 1;
		var midString = "";
		// Find a string that doesn't exist in the inputString to be used
		// as an "inbetween" string
		while (midString == "")
		{
			for (var i=0; i < midStrings.length; i++)
			{
				var tempMidString = "";
				for (var j=0; j < midStringLen; j++)
				{
					tempMidString += midStrings[i];
				}
				if (fromString.indexOf(tempMidString) == -1)
				{
					midString = tempMidString;
					i = midStrings.length + 1;
				}
			}
		}
		// Keep on going until we build an "inbetween" string that doesn't exist
		// Now go through and do two replaces - first, replace the "fromString" with the "inbetween" string
		while (temp.indexOf(fromString) != -1)
		{
			var toTheLeft = temp.substring(0, temp.indexOf(fromString));
			var toTheRight = temp.substring(temp.indexOf(fromString)+fromString.length, temp.length);
			temp = toTheLeft + midString + toTheRight;
		}
		// Next, replace the "inbetween" string with the "toString"
		while (temp.indexOf(midString) != -1)
		{
			var toTheLeft = temp.substring(0, temp.indexOf(midString));
			var toTheRight = temp.substring(temp.indexOf(midString)+midString.length, temp.length);
			temp = toTheLeft + toString + toTheRight;
		}
	}
	// Ends the check to see if the string being replaced is part of the replacement string or not
	return temp;
	// Send the updated string back to the user
}
// Ends the "replaceSubstring" function


</SCRIPT>
]]>
</xsl:text>

</head>

<body topmargin="0" marginwidth="0" marginheight="0" onload="checkTerms()">
 <!-- INCLUDE THE HEADER -->

	<xsl:apply-templates select="HEADER">
		  <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
		  <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
		  <xsl:with-param name="SEARCH-TYPE">Thesaurus</xsl:with-param>
		  <xsl:with-param name="TARGET">_parent</xsl:with-param>
	 </xsl:apply-templates>

<!-- INCLUDE THE GLOBAL LINKS BAR -->

	  <xsl:apply-templates select="GLOBAL-LINKS">
		  <xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
		  <xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
		  <xsl:with-param name="LINK">Thesaurus</xsl:with-param>
		  <xsl:with-param name="TARGET">_parent</xsl:with-param>
	</xsl:apply-templates>
<center>
<table border="0" cellspacing="0" cellpadding="0" width="99%">
<tr><td valign="top" width="140">

	<xsl:call-template name="THESAURUS-HELP-TEXT">
		<xsl:with-param name="ACTION">BROWSE</xsl:with-param>
		<xsl:with-param name="RESULT" select="$RESULT"/>
	</xsl:call-template>


</td>

<td valign="top" width="10"><img src="/engresources/images/spacer.gif" border="0" width="10"/></td>
<td valign="top" width="100%">


<table border="0" width="100%" cellspacing="0" cellpadding="0">

<xsl:apply-templates select="//DOC/THESAURUS-HEADER"/>




<xsl:apply-templates select="DATA"/>



</table>

</td></tr>
</table>
</center>


</body>
</html>

</xsl:template>

<xsl:template match="NOSUGGEST">
	<tr><td valign="top" colspan="4">&#160;&#160;<a CLASS="RedText">NO Search Results NO Suggestions:</a></td></tr>
	<tr><td valign="top" height="5" colspan="4"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
	<tr><td valign="top" height="4" colspan="2"><img src="/engresources/images/spacer.gif" border="0" height="4"/></td></tr>
</xsl:template>


<xsl:template match="PAGE">
	<tr>
		<td valign="top" width="55" height="1">
			<img src="/engresources/images/spacer.gif" border="0" width="55" height="1"/></td>
		<td valign="top" width="400"><a CLASS="MedBlackText"><u><b>Terms</b></u></a></td><td valign="top" width="8" align="left"><img src="/engresources/images/spacer.gif" border="0" width="8"/></td><td valign="top" align="left"><a CLASS="SmBlackText"><u><b>Select</b></u></a></td><td valign="top" width="8" align="left"><img src="/engresources/images/spacer.gif" border="0" width="8"/></td>
		<!-- Explode commented as fo now -->
		<td valign="top" align="left"><a CLASS="SmBlackText"><img src="/engresources/images/spacer.gif" border="0" width="50" height="1"/></a></td>

	</tr>
	<xsl:apply-templates/>
</xsl:template>

<xsl:template match="TREC">

<xsl:variable name="BG">
	<xsl:variable name="MT">
		<xsl:value-of select="java:encode(ID/MT)" disable-output-escaping="yes"/>
	</xsl:variable>

	<xsl:choose>
		<xsl:when test="(sutil:lowerCase($MT) = sutil:lowerCase($ENCODED-TERM))">#ECCA82</xsl:when>
		<xsl:when test="(sutil:lowerCase($MT) != sutil:lowerCase($ENCODED-TERM))">#CEEBFF</xsl:when>
	</xsl:choose>
</xsl:variable>


<tr bgcolor="{$BG}">
	<td valign="top" width="55" height="1">
		<img src="/engresources/images/spacer.gif" border="0" width="55" height="1"/>
	</td>

	<xsl:apply-templates select="ID/MT"/>
</tr>

</xsl:template>


<xsl:template match="CU">



	<td valign="top">
	<xsl:variable name="MT">
		<xsl:value-of select="java:encode(.)" disable-output-escaping="yes"/>
	</xsl:variable>

		<a CLASS="RollLink" href="/controller/servlet/Controller?CID=thesFullRec&amp;term={$MT}&amp;database={$DATABASE}">

		<xsl:value-of select="." disable-output-escaping="yes"/></a></td>
	<td valign="top" width="8" align="left"><img src="/engresources/images/spacer.gif" border="0" width="8"/>
	</td>
	<td valign="top" align="left">
		<xsl:variable name="MTP">
		<xsl:value-of select="sutil:replaceSingleQuotes(.)"/>
	</xsl:variable>
	&#160;<input type="checkbox" name="selectUnselect" onclick="parent.clipFrame.addRemoveFromClipBoard('{$MTP}')" value="{$MTP}"/></td>
	<td valign="top" width="8"><img src="/engresources/images/spacer.gif" border="0" width="8"/></td>
	<td valign="top">&#160;</td>
</xsl:template>

<xsl:template match="LE">

	<td valign="top">
	<xsl:variable name="MT">
		<xsl:value-of select="java:encode(.)" disable-output-escaping="yes"/>
	</xsl:variable>

		<a CLASS="RollLink" href="/controller/servlet/Controller?CID=thesFullRec&amp;term={$MT}&amp;database={$DATABASE}">
		<i><xsl:value-of select="." disable-output-escaping="yes"/></i></a></td>
	<td valign="top" width="8" align="left"><img src="/engresources/images/spacer.gif" border="0" width="8"/>
	</td>
	<td valign="top" align="left">&#160;</td>
	<td valign="top" width="8"><img src="/engresources/images/spacer.gif" border="0" width="8"/></td>
	<td valign="top">&#160;</td>
</xsl:template>

<xsl:template match="PR">


	<td valign="top">
	<xsl:variable name="MT">
		<xsl:value-of select="java:encode(.)" disable-output-escaping="yes"/>
	</xsl:variable>

		<a CLASS="RollLink" href="/controller/servlet/Controller?CID=thesFullRec&amp;term={$MT}&amp;database={$DATABASE}">
		<i><xsl:value-of select="." disable-output-escaping="yes" /></i><xsl:if test="(//DOC/DATA/HIT/TREC/STATUS!='N')">*</xsl:if></a></td>
	<td valign="top" width="8" align="left"><img src="/engresources/images/spacer.gif" border="0" width="8"/>
	</td>
	<td valign="top" align="left">
	<xsl:variable name="MTP">
		<xsl:value-of select="sutil:replaceSingleQuotes(.)"/>
	</xsl:variable>

	&#160;<xsl:if test="(//DOC/DATA/HIT/TREC/STATUS!='N')"><input type="checkbox" name="selectUnselect" onclick="parent.clipFrame.addRemoveFromClipBoard('{$MTP}')" value="{$MTP}"/></xsl:if>
	</td>

	<td valign="top" width="8"><img src="/engresources/images/spacer.gif" border="0" width="8"/></td>
	<td valign="top">&#160;</td>
</xsl:template>



<xsl:template match="DATA">
<tr><td valign="top" height="2" bgcolor="#3173B5"><img src="/engresources/images/spacer.gif" border="0" height="2"/></td></tr>
<tr><td valign="top" height="5" bgcolor="#C3C8D1"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>

<tr><td valign="top" bgcolor="#C3C8D1">
	<center>
	<table border="0" width="80%" cellspacing="0" cellpadding="0" bgcolor="#CEEBFF">
		<tr><td valign="top" height="1" colspan="3" bgcolor="#000000"><img src="/engresources/images/spacer.gif" border="0" height="1"/></td></tr>
		<tr>
			<td valign="top" width="1" bgcolor="#000000"><img src="/engresources/images/spacer.gif" border="0" width="1"/></td>
			<td valign="top">
				<table border="0" cellspacing="0" cellpadding="0" width="100%">
				<form name="checkform">
					<tr><td valign="top" height="5" colspan="4"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>
					<tr>
						<xsl:apply-templates select="TPATH"/>
					</tr>
					<tr><td valign="top" height="5" colspan="4"><img src="/engresources/images/spacer.gif" border="0" height="5"/></td></tr>

					<xsl:apply-templates select="BROWSE|NOSUGGEST"/>

					<tr><td valign="top" colspan="5" height="2"><img src="/engresources/images/spacer.gif" border="0" height="2"/></td></tr>
				</form>
				</table>

			</td>
			<td valign="top" width="1" bgcolor="#000000"><img src="/engresources/images/spacer.gif" border="0" width="1"/></td>
		</tr>

		<tr><td valign="top" height="1" bgcolor="#000000" colspan="3"><img src="/engresources/images/spacer.gif" border="0" height="1"/></td></tr>
	</table>
	</center>
</td></tr>

<tr><td valign="top" bgcolor="#C3C8D1" height="8"><img src="/engresources/images/spacer.gif" border="0" height="8"/></td></tr>
<tr>
	<td valign="top" align="middle">
	<table border="0" cellspacing="0" cellpadding="0">

	<tr>
		<td valign="top">

			<xsl:variable name="PINDEX">
				<xsl:value-of select="PINDEX"/>
			</xsl:variable>

			<xsl:variable name="NINDEX">
				<xsl:value-of select="NINDEX"/>
			</xsl:variable>


			<xsl:choose>
				<xsl:when test="($PINDEX>=0)">
					<a CLASS="LgBlueLink" href="/controller/servlet/Controller?CID=thesBrowse&amp;index={$PINDEX}&amp;term={$ENCODED-TERM}&amp;database={$DATABASE}">Previous Page</a>&#160;<a CLASS="MedBlackText"> |</a>
				</xsl:when>
			</xsl:choose>
			&#160;<a CLASS="LgBlueLink" href="/controller/servlet/Controller?CID=thesBrowse&amp;index={$NINDEX}&amp;term={$ENCODED-TERM}&amp;database={$DATABASE}">Next Page</a>



		</td>
	</tr>

	</table>
</td></tr>
</xsl:template>




</xsl:stylesheet>

