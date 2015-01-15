<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:html="http://www.w3.org/TR/REC-html40"
	xmlns:java="java:java.net.URLEncoder"
	exclude-result-prefixes="java html xsl"
>

<!--
	This file displays the following image tabs with the appropriate links:
	.Tag
	.Easy Search
	.Quick Search
	.Expert Search
	.Referex (Books)
	.Thesaurus
	.Reference Services (ask an expert)
	.Help


	The XML which triggers which tabs to display is SearchParameters.jsp
-->

<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="html:* xsl:*" />



<xsl:template match="GLOBAL-LINKS">
 <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/Robohelp.js"/>

    <script language="javascript">
    <xsl:comment>
    <![CDATA[
    function help(database)
    {
       var url = null;
       url= "/controller/servlet/Controller?CID=help"+"&database="+database;
       document.location = url;
       javascript:makeUrl('Welcome_to_Engineering_Village_Help.htm');
    }
    ]]>
   // </xsl:comment>
   </script>
	<xsl:param name="SESSION-ID"/>
	<xsl:param name="SELECTED-DB"/>
	<xsl:param name="LINK"/>
	<xsl:param name="TARGET">_self</xsl:param>

	<xsl:variable name="ENCODED-DATABASE">
		<xsl:value-of select="java:encode($SELECTED-DB)"/>
	</xsl:variable>

	<center>
		<!-- Table for Global Links to different areas of EV2 -->
		<table border="0" width="99%" cellspacing="0" cellpadding="0">
			<tr>
				<td valign="top" width="1" bgcolor="#FFFFFF"><img src="/engresources/images/s.gif" border="0" width="1"/></td>
				<td valign="top" width="100%"><img src="/engresources/images/s.gif" border="0"/></td>
					<xsl:apply-templates>
						<xsl:with-param name="LINK"><xsl:value-of select="$LINK"/></xsl:with-param>
						<xsl:with-param name="ENCODED-DATABASE"><xsl:value-of select="$ENCODED-DATABASE"/></xsl:with-param>
					 	<xsl:with-param name="TARGET"><xsl:value-of select="$TARGET"/></xsl:with-param>
					</xsl:apply-templates>
				</tr>
		</table>
	</center>

</xsl:template>

<xsl:template match="TAGGROUPS">
	<xsl:param name="LINK"/>
	<xsl:param name="ENCODED-DATABASE"/>
	<xsl:param name="TARGET">_self</xsl:param>

	<!-- Tag Search -->
	<td valign="bottom" width="2"><img src="/engresources/images/s.gif" width="2" border="0"/></td>
	<td valign="bottom" align="right">
		<a TARGET="{$TARGET}" href="/controller/servlet/Controller?CID=tagsLoginForm&amp;database={$ENCODED-DATABASE}&amp;searchtype=TagSearch">
		<xsl:choose>
			<xsl:when test="($LINK='Tags') or ($LINK='TagSearch') ">
				<img src="/engresources/images/tagsgroups_g.gif" border="0" alt="Tag Search" />
			</xsl:when>
			<xsl:otherwise>
				<img src="/engresources/images/tagsgroups_b.gif" border="0" alt="Tag Search" />
			</xsl:otherwise>
		</xsl:choose>
		</a>
	</td>
</xsl:template>

<xsl:template match="EASY">
	<xsl:param name="LINK"/>
	<xsl:param name="ENCODED-DATABASE"/>
	<xsl:param name="TARGET">_self</xsl:param>

	<!-- EASY Search -->
	<td valign="bottom" width="2"><img src="/engresources/images/s.gif" width="2" border="0"/></td>
	<td valign="bottom" align="right">
		<a TARGET="{$TARGET}" href="/controller/servlet/Controller?CID=easySearch&amp;database={$ENCODED-DATABASE}">
		<xsl:choose>
			<xsl:when test="($LINK='Easy')">
				<img src="/engresources/images/simple_g.gif" border="0" alt="Easy Search" />
			</xsl:when>
			<xsl:otherwise>
				<img src="/engresources/images/simple_b.gif" border="0" alt="Easy Search" />
			</xsl:otherwise>
		</xsl:choose>
		</a>
	</td>
</xsl:template>

<xsl:template match="QUICK">
	<xsl:param name="LINK"/>
	<xsl:param name="ENCODED-DATABASE"/>
	<xsl:param name="TARGET">_self</xsl:param>

	<!-- Quick Search -->
	<td valign="bottom" width="2"><img src="/engresources/images/s.gif" width="2" border="0"/></td>
	<td valign="bottom" align="right">
		<a TARGET="{$TARGET}" href="/controller/servlet/Controller?CID=quickSearch&amp;database={$ENCODED-DATABASE}">
		<xsl:choose>
			<xsl:when test="($LINK='Quick')">
				<img src="/engresources/images/advanced_g.gif" border="0" alt="Quick Search" />
			</xsl:when>
			<xsl:otherwise>
				<img src="/engresources/images/advanced_b.gif" border="0" alt="Quick Search" />
			</xsl:otherwise>
		</xsl:choose>
		</a>
	</td>
</xsl:template>


<xsl:template match="EXPERT">
	<xsl:param name="LINK"/>
	<xsl:param name="ENCODED-DATABASE"/>
	<xsl:param name="TARGET">_self</xsl:param>

	<!-- Expert Search -->
	<td valign="bottom" width="2"><img src="/engresources/images/s.gif" width="2" border="0"/></td>
	<td valign="bottom" align="right">
		<a TARGET="{$TARGET}" href="/controller/servlet/Controller?CID=expertSearch&amp;database={$ENCODED-DATABASE}">
		<xsl:choose>
			<xsl:when test="($LINK='Expert')">
				<img src="/engresources/images/expert_g.gif" border="0" alt="Expert Search" />
			</xsl:when>
			<xsl:otherwise>
				<img src="/engresources/images/expert_b.gif" border="0" alt="Expert Search" />
			</xsl:otherwise>
		</xsl:choose>
		</a>
	</td>
</xsl:template>

<xsl:template match="BULLETINS">
	<xsl:param name="LINK"/>
	<xsl:param name="ENCODED-DATABASE"/>
	<xsl:param name="TARGET">_self</xsl:param>
	<xsl:param name="RESOURCE-PATH"/>

	<!-- Bulletin Search -->
	<td valign="bottom" width="2"><img src="/engresources/images/s.gif" width="2"/></td>
	<td valign="bottom" align="right">
		<a TARGET="{$TARGET}" href="/controller/servlet/Controller?CID=bulletinSearch&amp;database=1024">
		<xsl:choose>
			<xsl:when test="($LINK='Bulletins')">
				<img src="/engresources/images/bu1.gif" border="0" />
			</xsl:when>
			<xsl:otherwise>
				<img src="/engresources/images/bu2.gif" border="0"/>
			</xsl:otherwise>
		</xsl:choose>
		</a>
	</td>
</xsl:template>

<xsl:template match="THESAURUS">
	<xsl:param name="LINK"/>
	<xsl:param name="ENCODED-DATABASE"/>
	<xsl:param name="TARGET">_self</xsl:param>

	<!-- Thesaurus Search -->
	<td valign="bottom" width="2"><img src="/engresources/images/s.gif" width="2" border="0"/></td>
	<td valign="bottom" align="right">
		<a TARGET="{$TARGET}" href="/controller/servlet/Controller?CID=thesHome&amp;database={$ENCODED-DATABASE}">
		<xsl:choose>
			<xsl:when test="($LINK='Thesaurus')">
				<img src="/engresources/images/thes_g.gif" border="0" alt="Thesaurus" />
			</xsl:when>
			<xsl:otherwise>
				<img src="/engresources/images/thes_b.gif" border="0" alt="Thesaurus" />
			</xsl:otherwise>
		</xsl:choose>
		</a>
	</td>
</xsl:template>

<xsl:template match="BOOK">
	<xsl:param name="LINK"/>
	<xsl:param name="ENCODED-DATABASE"/>
	<xsl:param name="TARGET">_self</xsl:param>

	<!-- Book Search -->
	<td valign="bottom" width="2"><img src="/engresources/images/s.gif" width="2" border="0"/></td>
	<td valign="bottom" align="right">
		<a TARGET="{$TARGET}" href="/controller/servlet/Controller?CID=ebookSearch&amp;database=131072">
		<xsl:choose>
			<xsl:when test="($LINK='Book')">
				<img src="/engresources/images/ebook_g.gif" border="0" alt="eBook Search" />
			</xsl:when>
			<xsl:otherwise>
				<img src="/engresources/images/ebook_b.gif" border="0" alt="eBook Search" />
			</xsl:otherwise>
		</xsl:choose>
		</a>
	</td>
</xsl:template>

<xsl:template match="REFERENCE">
	<xsl:param name="LINK"/>
	<xsl:param name="ENCODED-DATABASE"/>
	<xsl:param name="TARGET">_self</xsl:param>

	<!-- Reference Services -->
	<td valign="bottom" width="2"><img src="/engresources/images/s.gif" width="2" border="0"/></td>
	<td valign="bottom" align="right">
		<a TARGET="{$TARGET}" href="/controller/servlet/Controller?CID=referenceServices&amp;database={$ENCODED-DATABASE}">
		<xsl:choose>
			<xsl:when test="($LINK='referenceServices')">
				<img src="/engresources/images/ask_g.gif" border="0" alt="Ask an Expert" />
			</xsl:when>
			<xsl:otherwise>
				<img src="/engresources/images/ask_b.gif" border="0" alt="Ask an Expert" />
			</xsl:otherwise>
		</xsl:choose>
		</a>
	</td>
</xsl:template>

<xsl:template match="HELP">
	<xsl:param name="LINK"/>
	<xsl:param name="ENCODED-DATABASE"/>
	<xsl:param name="TARGET">_self</xsl:param>

	<!-- HELP! -->
	<td valign="bottom" width="2"><img src="/engresources/images/s.gif" width="2" border="0"/></td>
	<td valign="bottom" align="right">

	<a TARGET="{$TARGET}" href="javascript:help({$ENCODED-DATABASE})">

	<!-- <a TARGET="{$TARGET}" href="/controller/servlet/Controller?CID=help&amp;database={$ENCODED-DATABASE}"> -->
	<xsl:choose>
		<xsl:when test="($LINK='help')">
			<img src="/engresources/images/help_g.gif" border="0" alt="Help" />
		</xsl:when>
		<xsl:otherwise>
			<img src="/engresources/images/help_b.gif" border="0" alt="Help" />
		</xsl:otherwise>
	</xsl:choose>
	</a>
	</td>
</xsl:template>

</xsl:stylesheet>