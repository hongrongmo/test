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
	<xsl:value-of select="//DOC/TERM" disable-output-escaping="yes"/>
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

<xsl:variable name="RESULT">
	<xsl:value-of select="//DOC/RESULT"/>
</xsl:variable>


<xsl:template match="DOC">

<html>
<head>
<title>Ei Thesaurus -- Browse</title>
        <SCRIPT LANGUAGE="Javascript" SRC="/static/js/StylesheetLinks.js"/>

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

function scopenotes(term2,database)
{
	if(database == 2097152)
	{
		window.open('/controller/servlet/Controller?EISESSION=$SESSIONID&CID=thesScopeNotes&database='+database+'&term='+term2,'NewWindow','status=no,resizable,scrollbars=1,width=300,height=400');void('');
	}
	else
	{
		window.open('/controller/servlet/Controller?EISESSION=$SESSIONID&CID=thesScopeNotes&database='+database+'&term='+term2,'NewWindow','status=no,resizable,scrollbars=1,width=300,height=250');void('');
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
		<xsl:with-param name="ACTION">FULLREC</xsl:with-param>
		<xsl:with-param name="RESULT" select="$RESULT"/>
	</xsl:call-template>

</td>
<td valign="top" width="10"><img src="/static/images/spacer.gif" border="0" width="10"/></td>
<td valign="top">


<table border="0" width="100%" cellspacing="0" cellpadding="0">

<xsl:apply-templates select="//DOC/THESAURUS-HEADER"/>



<tr><td valign="top" height="2" bgcolor="#3173B5"><img src="/static/images/spacer.gif" border="0" height="2"/></td></tr>
<tr><td valign="top" height="5" bgcolor="#C3C8D1"><img src="/static/images/spacer.gif" border="0" height="5"/></td></tr>

<tr><td valign="top" bgcolor="#C3C8D1">
<center>
<table border="0" width="95%" cellspacing="0" cellpadding="0" bgcolor="#CEEBFF">
<tr><td valign="top" height="1" colspan="3" bgcolor="#000000"><img src="/static/images/spacer.gif" border="0" height="1"/></td></tr>
<tr><td valign="top" width="1" bgcolor="#000000"><img src="/static/images/spacer.gif" border="0" width="1"/></td>
<td valign="top">
<table border="0" cellspacing="0" cellpadding="0" width="100%">
<form name="checkform">

	<tr><td valign="top" height="5" colspan="4"><img src="/static/images/spacer.gif" border="0" height="5"/></td></tr>

	<tr>
	<xsl:apply-templates select="TPATH"/>
	</tr>
	<tr><td valign="top" height="10" colspan="5"><img src="/static/images/spacer.gif" border="0" height="10"/></td></tr>

	<xsl:apply-templates select="DATA"/>

</form>
</table>
</td>
<td valign="top" width="1" bgcolor="#000000"><img src="/static/images/spacer.gif" border="0" width="1"/></td>
</tr>
<tr>
<td valign="top" height="1" bgcolor="#000000" colspan="3">
	<img src="/static/images/spacer.gif" border="0" height="1"/>
</td>
</tr>
</table>
</center>
</td></tr>
<tr><td valign="top" height="20" bgcolor="#C3C8D1"><img src="/static/images/spacer.gif" border="0" height="20"/></td></tr>
</table>

</td></tr>
</table>
</center>

</body>
</html>
</xsl:template>

<xsl:template match="BT">
	<td valign="top">
	<!-- Table for broader terms -->
	<table border="0" cellspacing="0" cellpadding="0" width="230">
		<tr>
			<td valign="top" width="6"><img src="/static/images/spacer.gif" border="0" width="6"/></td><td valign="top" width="100%"><a CLASS="SmBlackText"><u><b>Broader Terms</b></u></a></td><td valign="top" width="10"><img src="/static/images/spacer.gif" border="0" width="10"/></td><td valign="top"><a CLASS="SmBlackText"><b><u>Select</u></b></a></td><td valign="top" width="5"><img src="/static/images/spacer.gif" border="0" width="5"/></td>
			<!-- Explode Commented as of now
			<td valign="top"><a CLASS="SmBlackText"><b><u>Explode</u></b></a></td>
			-->
		</tr>
		<xsl:apply-templates mode="BROADER_TERMS"/>
	</table>
	</td>
</xsl:template>

<xsl:template match="LT">
	<tr>
		<td valign="top"><a CLASS="MedBlackText">&#160;&#160;<b>Used for: </b></a></td>
		 <td valign="top" width="6"><img src="/static/images/spacer.gif" border="0" width="6"/></td>
		    <td valign="top">
			<xsl:apply-templates mode="LEADIN_TERMS"/>
		    </td>
	</tr>
</xsl:template>

<xsl:template match="TT">
	<tr>
		<td valign="top">
			<table border="0" cellspacing="0" cellpadding="0" height="23">
				<tr><td valign="middle">
					<a CLASS="MedBlackText">&#160;&#160;<b>Top Terms: </b></a>
				</td></tr>
			</table>
		</td>
		<td valign="top" width="6"><img src="/static/images/spacer.gif" border="0" width="6"/></td>
		<td valign="top">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr><td valign="top">
					<xsl:apply-templates mode="TOP_TERMS"/>
				</td></tr>
			</table>
		</td>
	</tr>
</xsl:template>




<xsl:template match="UT">
	<tr>
		<td valign="top">
			<table border="0" cellspacing="0" cellpadding="0" height="23">
				<tr><td valign="middle">
					<a CLASS="MedBlackText">&#160;&#160;<b>Use: </b></a>
				</td></tr>
			</table>
		</td>
		<td valign="top" width="6"><img src="/static/images/spacer.gif" border="0" width="6"/></td>
		<td valign="top">
			<table border="0" cellspacing="0" cellpadding="0">
				<tr><td valign="top">
					<xsl:apply-templates mode="USE_TERMS"/>
				</td></tr>
			</table>
		</td>
	</tr>
</xsl:template>

<xsl:template match="PT">
	<tr>
		<td valign="top">
		<table border="0" cellspacing="0" cellpadding="0" height="23">
			<tr><td valign="middle">
			<a CLASS="MedBlackText">&#160;&#160;<b>Prior Terms: </b></a>
			</td></tr>
		</table>
		</td>
		<td valign="top" width="6"><img src="/static/images/spacer.gif" border="0" width="6"/></td>
		<td valign="top">
		<table border="0" cellspacing="0" cellpadding="0">
			<tr><td valign="top">
				<xsl:apply-templates mode="PRIOR_TERMS"/>
			</td></tr>
		</table>
		</td>
	</tr>
</xsl:template>


<xsl:template match="RT">
	<td valign="top">
	<!-- Table for Related Terms -->
	<table border="0" cellspacing="0" cellpadding="0" width="230">
		<tr>
			<td valign="top" width="6"><img src="/static/images/spacer.gif" border="0" width="6"/></td><td valign="top" width="100%"><a CLASS="SmBlackText"><u><b>Related Terms</b></u></a></td><td valign="top" width="10"><img src="/static/images/spacer.gif" border="0" width="10"/></td><td valign="top"><a CLASS="SmBlackText"><b><u>Select</u></b></a></td><td valign="top" width="5"><img src="/static/images/spacer.gif" border="0" width="5"/></td>
			<!-- Explode Commented Of now
			<td valign="top"><a CLASS="SmBlackText"><b><u>Explode</u></b></a></td>
			-->
		</tr>
		<xsl:apply-templates mode="RELATED_TERMS"/>
	</table>
	</td>
</xsl:template>

<xsl:template match="NT">
	<td valign="top">
	<!-- Table for Narrower Terms -->
	<table border="0" cellspacing="0" cellpadding="0" width="230">
		<tr>
			<td valign="top" width="6"><img src="/static/images/spacer.gif" border="0" width="6"/></td><td valign="top" width="100%"><a CLASS="SmBlackText"><u><b>Narrower Terms</b></u></a></td><td valign="top" width="10"><img src="/static/images/spacer.gif" border="0" width="10"/></td><td valign="top"><a CLASS="SmBlackText"><b><u>Select</u></b></a></td><td valign="top" width="5"><img src="/static/images/spacer.gif" border="0" width="5"/></td>
			<!-- Explode commented as of now
			<td valign="top"><a CLASS="SmBlackText"><b><u>Explode</u></b></a></td>
			-->
		</tr>
		<xsl:apply-templates mode="NARROWER_TERMS"/>
	</table>
	</td>
</xsl:template>


<xsl:template match="TREC" mode="LEADIN_TERMS">

	<xsl:variable name="MT">
		<xsl:value-of select="java:encode(ID/MT)" disable-output-escaping="yes"/>
	</xsl:variable>
	<xsl:variable name="RNUM">
		<xsl:value-of select="java:encode(ID/RNUM)" disable-output-escaping="yes"/>
	</xsl:variable>
	<a CLASS="RollLink" href="/controller/servlet/Controller?CID=thesFullRec&amp;tid={$RNUM}&amp;term={$MT}&amp;database={$DATABASE}">
	<i><xsl:value-of select="ID/MT" disable-output-escaping="yes"/></i></a><br/>
</xsl:template>


<xsl:template match="TREC" mode="TOP_TERMS">
	<xsl:variable name="MT">
		<xsl:value-of select="java:encode(ID/MT)" disable-output-escaping="yes"/>
	</xsl:variable>
	<xsl:variable name="RNUM">
		<xsl:value-of select="java:encode(ID/RNUM)" disable-output-escaping="yes"/>
	</xsl:variable>
	<xsl:variable name="MTP">
		<xsl:value-of select="sutil:replaceSingleQuotes(ID/MT)" disable-output-escaping="yes"/>
	</xsl:variable>

	<a CLASS="RollLink" href="/controller/servlet/Controller?CID=thesFullRec&amp;tid={$RNUM}&amp;term={$MT}&amp;database={$DATABASE}">

	<xsl:value-of select="ID/MT" disable-output-escaping="yes"/></a>&#160;
	<xsl:if test="(//DOC/DATA/HIT/TREC/STATUS!='L')">
	<input type="checkbox" name="selectUnselect" onclick="parent.clipFrame.addRemoveFromClipBoard('{$MTP}')" value="{$MTP}"/><a CLASS="ExSmBlackText">(Select)</a>
	</xsl:if>
	<br/>
</xsl:template>

<xsl:template match="TREC" mode="USE_TERMS">
	<xsl:variable name="MT">
		<xsl:value-of select="java:encode(ID/MT)" disable-output-escaping="yes"/>
	</xsl:variable>
	<xsl:variable name="RNUM">
		<xsl:value-of select="java:encode(ID/RNUM)" disable-output-escaping="yes"/>
	</xsl:variable>
	<xsl:variable name="MTP">
		<xsl:value-of select="sutil:replaceSingleQuotes(ID/MT)" disable-output-escaping="yes"/>
	</xsl:variable>

	<a CLASS="RollLink" href="/controller/servlet/Controller?CID=thesFullRec&amp;tid={$RNUM}&amp;term={$MT}&amp;database={$DATABASE}">

	<xsl:value-of select="ID/MT" disable-output-escaping="yes"/></a>&#160;
	<!--<xsl:if test="(//DOC/DATA/HIT/TREC/STATUS!='L')"> -->
	<input type="checkbox" name="selectUnselect" onclick="parent.clipFrame.addRemoveFromClipBoard('{$MTP}')" value="{$MTP}"/><a CLASS="ExSmBlackText">(Select)</a>
	<!--</xsl:if>-->

	<xsl:choose>
		<xsl:when test="(./UTF='AND')" >
		  <xsl:if test="not(position()=last())">
			&#160;AND&#160;
		  </xsl:if>	
		</xsl:when>
		<xsl:when test="(./UTF='OR')" >
			<xsl:if test="not(position()=last())">
			&#160;OR&#160;
			</xsl:if>
		</xsl:when>
		<xsl:otherwise>
	<br/>
		</xsl:otherwise>
	</xsl:choose>
	
</xsl:template>


<xsl:template match="TREC" mode="PRIOR_TERMS">
	<xsl:variable name="MT">
		<xsl:value-of select="java:encode(ID/MT)" disable-output-escaping="yes"/>
	</xsl:variable>
	<xsl:variable name="RNUM">
			<xsl:value-of select="java:encode(ID/RNUM)" disable-output-escaping="yes"/>
	</xsl:variable>
	<xsl:variable name="MTP">
		<xsl:value-of select="sutil:replaceSingleQuotes(ID/MT)" disable-output-escaping="yes"/>
	</xsl:variable>

	<a CLASS="RollLink" href="/controller/servlet/Controller?CID=thesFullRec&amp;tid={$RNUM}&amp;term={$MT}&amp;database={$DATABASE}">

	<xsl:apply-templates select="ID/MT" mode="SIMPLE"/></a>&#160;
	
	<input type="checkbox" name="selectUnselect" onclick="parent.clipFrame.addRemoveFromClipBoard('{$MTP}')" value="{$MTP}"/><a CLASS="ExSmBlackText">(Select)</a>
	
	<br/>
</xsl:template>




<xsl:template match="HIT">
<tr>
	<xsl:apply-templates select="TREC/ID/MT" mode="TERM"/>
</tr>

<tr><td valign="top" height="4" colspan="5"><img src="/static/images/spacer.gif" border="0" height="4"/></td></tr>

<tr><td valign="top" colspan="5">

<table border="0" cellspacing="0" cellpadding="0">
	<xsl:apply-templates select="TREC/UT" />
	<xsl:apply-templates select="TREC/LT" />
	<xsl:apply-templates select="TREC/PT" />
	<xsl:apply-templates select="TREC/TT" />
</table>
</td></tr>

<tr><td valign="top" height="10" colspan="5"><img src="/static/images/spacer.gif" border="0" height="10"/></td></tr>
<tr>
	<!-- Each Broader term -->
	<xsl:apply-templates select="TREC/BT" />

<td valign="top" width="10"><img src="/static/images/spacer.gif" border="0" width="10"/></td>
	<xsl:apply-templates select="TREC/RT" />

<td valign="top" width="10"><img src="/static/images/spacer.gif" border="0" width="10"/></td>
	<xsl:apply-templates select="TREC/NT" />

</tr>
</xsl:template>


<xsl:template match="NOHIT">
	<tr><td valign="top" colspan="4">&#160;&#160;<a CLASS="RedText">Your search did not find a match with the spelling "<xsl:value-of select='//DOC/TERM'/>".  Did you mean?</a></td></tr>
	<tr><td valign="top" height="5" colspan="4"><img src="/static/images/spacer.gif" border="0" height="5"/></td></tr>
	<xsl:apply-templates mode="SUGGESTED_TERMS" select="PAGE/TREC"/>
	<tr><td valign="top" colspan="4" height="4"><img src="/static/images/spacer.gif" border="0" height="4"/></td></tr>
</xsl:template>

<xsl:template match="NOSUGGEST">
	<tr><td valign="top" colspan="4">&#160;&#160;<a CLASS="RedText">Your search for "<xsl:value-of select='//DOC/TERM'/>" did not find any matching term.</a></td></tr>
	<tr><td valign="top" height="5" colspan="4"><img src="/static/images/spacer.gif" border="0" height="5"/></td></tr>
	<tr><td valign="top" height="4" colspan="2"><img src="/static/images/spacer.gif" border="0" height="4"/></td></tr>
</xsl:template>


<xsl:template match="TREC" mode="SUGGESTED_TERMS">
	<xsl:variable name="MT">
		<xsl:value-of select="java:encode(ID/MT)" disable-output-escaping="yes"/>
	</xsl:variable>
	<xsl:variable name="RNUM">
		<xsl:value-of select="java:encode(ID/RNUM)" disable-output-escaping="yes"/>
	</xsl:variable>
	<tr>
		<td valign="top" width="15" height="1">
			<img src="/static/images/spacer.gif" border="0" width="15" height="1"/>
		</td>
		<td valign="top">
			<a CLASS="RollLink" href="/controller/servlet/Controller?CID=thesFullRec&amp;tid={$RNUM}&amp;term={$MT}&amp;database={$DATABASE}"><xsl:apply-templates select="ID/MT" mode="SIMPLE"/></a>
		</td>
	</tr>
</xsl:template>




<xsl:template match="TREC" mode="NARROWER_TERMS">
<tr>
	<td valign="top" width="6"><img src="/static/images/spacer.gif" border="0" width="6"/></td>
	<td valign="top">
	<xsl:variable name="MT">
		<xsl:value-of select="java:encode(ID/MT)" disable-output-escaping="yes"/>
	</xsl:variable>
	<xsl:variable name="RNUM">
		<xsl:value-of select="java:encode(ID/RNUM)" disable-output-escaping="yes"/>
	</xsl:variable>

	<a CLASS="RollLink" href="/controller/servlet/Controller?CID=thesFullRec&amp;tid={$RNUM}&amp;term={$MT}&amp;database={$DATABASE}">
	<xsl:value-of select="ID/MT" disable-output-escaping="yes" /></a></td>
	<td valign="top" width="10"><img src="/static/images/spacer.gif" border="0" width="10"/>
	</td>
	<td valign="top">
	<xsl:variable name="MTP">
		<xsl:value-of select="sutil:replaceSingleQuotes(ID/MT)" disable-output-escaping="yes"/>
	</xsl:variable>

	<input type="checkbox" name="selectUnselect" onclick="parent.clipFrame.addRemoveFromClipBoard('{$MTP}')" value="{$MTP}"/>
	
	</td>
	<td valign="top" width="5">
	<img src="/static/images/spacer.gif" border="0" width="5"/>
	</td>

	<!-- Explode Check Boxes Commented as of now
	<td valign="top">
		<input type="checkbox" name="a"/>
	</td>
	-->
</tr>
</xsl:template>


<xsl:template match="TREC" mode="BROADER_TERMS">
<tr>
	<td valign="top" width="6"><img src="/static/images/spacer.gif" border="0" width="6"/></td>
	<td valign="top">

	<xsl:variable name="MT">
		<xsl:value-of select="java:encode(ID/MT)" disable-output-escaping="yes"/>
	</xsl:variable>
	<xsl:variable name="RNUM">
		<xsl:value-of select="java:encode(ID/RNUM)" disable-output-escaping="yes"/>
	</xsl:variable>

	<a CLASS="RollLink" href="/controller/servlet/Controller?CID=thesFullRec&amp;tid={$RNUM}&amp;term={$MT}&amp;database={$DATABASE}">

	<xsl:value-of select="ID/MT" disable-output-escaping="yes"/>

	</a></td>

	<td valign="top" width="10"><img src="/static/images/spacer.gif" border="0" width="10"/>
	</td>
	<td valign="top">
	<xsl:variable name="MTP" >
		<xsl:value-of select="sutil:replaceSingleQuotes(ID/MT)" disable-output-escaping="yes"/>
	</xsl:variable>

	<input type="checkbox" name="selectUnselect" onclick="parent.clipFrame.addRemoveFromClipBoard('{$MTP}')" value="{$MTP}"/>
	
	</td>
	<td valign="top" width="5">
	<img src="/static/images/spacer.gif" border="0" width="5"/>
	</td>

	<!-- Explode Check Boxes Commented as of now
	<td valign="top">
		<input type="checkbox" name="a"/>
	</td>
	-->
</tr>
</xsl:template>


<xsl:template match="TREC" mode="RELATED_TERMS">
<tr>
	<td valign="top" width="6"><img src="/static/images/spacer.gif" border="0" width="6"/></td>
	<td valign="top">
	<xsl:variable name="MT">
		<xsl:value-of select="java:encode(ID/MT)" disable-output-escaping="yes"/>
	</xsl:variable>
	<xsl:variable name="RNUM">
		<xsl:value-of select="java:encode(ID/RNUM)" disable-output-escaping="yes"/>
	</xsl:variable>

	<a CLASS="RollLink" href="/controller/servlet/Controller?CID=thesFullRec&amp;tid={$RNUM}&amp;term={$MT}&amp;database={$DATABASE}">
	<xsl:value-of select="ID/MT" disable-output-escaping="yes"/></a></td>

	<td valign="top" width="10"><img src="/static/images/spacer.gif" border="0" width="10"/>
	</td>
	<td valign="top">
	<xsl:variable name="MTP">
		<xsl:value-of select="sutil:replaceSingleQuotes(ID/MT)" disable-output-escaping="yes"/>
	</xsl:variable>

	
	<input type="checkbox" name="selectUnselect" onclick="parent.clipFrame.addRemoveFromClipBoard('{$MTP}')" value="{$MTP}"/>
	
	</td>
	<td valign="top" width="5">
	<img src="/static/images/spacer.gif" border="0" width="5"/>
	</td>

	<!-- Explode Check Boxes Commented as of now
	<td valign="top">
	<input type="checkbox" name="a"/>
	</td>
	-->
</tr>
</xsl:template>

<xsl:template match="CU" mode="TERM">
	<td valign="top" colspan="5">&#160;&#160;&#160;&#160;&#160;
	<a CLASS="DBlueText"><b><xsl:value-of select="." disable-output-escaping="yes"/></b></a>
		<xsl:variable name="MT2">
			<xsl:value-of select="java:encode(.)" disable-output-escaping="yes"/>
		</xsl:variable>

		<xsl:variable name="MTP">
			<xsl:value-of select="sutil:replaceSingleQuotes(.)" disable-output-escaping="yes"/>
		</xsl:variable>


		<input type="checkbox" name="selectUnselect" onclick="parent.clipFrame.addRemoveFromClipBoard('{$MTP}')" value="{$MTP}"/><a CLASS="ExSmBlackText">(Select)</a>&#160;

		<!--Explode Commented as fo now
		<input type="checkbox" name="1"/><a CLASS="ExSmBlackText">(Explode)</a>&#160;&#160;
		-->


		<xsl:choose>
			<xsl:when test="(../../../../TREC/@INFO='true')">
				<a href='javascript: scopenotes("{$MT2}","{$DATABASE}");'>
					<img src="/static/images/i1.gif" border="0"/>
				</a>
			</xsl:when>
		</xsl:choose>
	</td>
</xsl:template>

<xsl:template match="LE" mode="TERM">
	<td valign="top" colspan="5">&#160;&#160;
	<a CLASS="DBlueText"><b><i><xsl:value-of select="." disable-output-escaping="yes"/></i></b></a>
			<xsl:variable name="MT2">
				<xsl:value-of select="java:encode(.)" disable-output-escaping="yes"/>
			</xsl:variable>
	
			<xsl:variable name="MTP">
				<xsl:value-of select="sutil:replaceSingleQuotes(.)" disable-output-escaping="yes"/>
			</xsl:variable>
			
			<xsl:choose>
				<xsl:when test="(../../../../TREC/@INFO='true')">
					<a href='javascript: scopenotes("{$MT2}","{$DATABASE}");'>
						<img src="/static/images/i1.gif" border="0"/>
					</a>
				</xsl:when>
			</xsl:choose>
	</td>
	
	
</xsl:template>

<xsl:template match="PR" mode="TERM">
	<td valign="top" colspan="5">&#160;
	<a CLASS="DBlueText">
		<b><i>
		<xsl:choose>
			<xsl:when test="(//DOC/DATA/HIT/TREC/STATUS!='N')">
				<xsl:value-of select="concat(.,' *')" disable-output-escaping="yes"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="concat(.,' ')" disable-output-escaping="yes"/>
			</xsl:otherwise>
		</xsl:choose>
		</i>
		</b></a>
		
		<xsl:variable name="MT2">
			<xsl:value-of select="java:encode(.)" disable-output-escaping="yes"/>
		</xsl:variable>


		<xsl:variable name="MTP">
			<xsl:value-of select="sutil:replaceSingleQuotes(.)" disable-output-escaping="yes"/>
		</xsl:variable>

		<xsl:if test="(//DOC/DATA/HIT/TREC/STATUS!='L')">
			<input type="checkbox" name="selectUnselect" onclick="parent.clipFrame.addRemoveFromClipBoard('{$MTP}')" value="{$MTP}"/><a CLASS="ExSmBlackText">(Select)</a>&#160;
		</xsl:if>

		<!-- Explode Check Boxes Commented as of now
		<input type="checkbox" name="1"/><a CLASS="ExSmBlackText">(Explode)</a>&#160;&#160;
		-->



		<xsl:choose>
			<xsl:when test="(../../../../TREC/@INFO='true')">
				<a href='javascript: scopenotes("{$MT2}","{$DATABASE}");'>
				<img src="/static/images/i1.gif" border="0"/>
				</a>
			</xsl:when>
		</xsl:choose>
	</td>
</xsl:template>


<xsl:template match="CU" mode="SIMPLE">
	<xsl:value-of select="." disable-output-escaping="yes"/>
</xsl:template>

<xsl:template match="LE" mode="SIMPLE">
	<i><xsl:value-of select="." disable-output-escaping="yes"/></i>
</xsl:template>

<xsl:template match="PR" mode="SIMPLE">
	<i><xsl:value-of select="." disable-output-escaping="yes"/>*</i>
</xsl:template>



</xsl:stylesheet>
