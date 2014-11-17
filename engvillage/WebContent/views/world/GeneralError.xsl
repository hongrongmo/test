<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:html="http://www.w3.org/TR/REC-html40"
  xmlns:java="java.net.URLEncoder"
  exclude-result-prefixes="java xsl html"
>

<xsl:output method="html" indent="no"/>
<xsl:strip-space elements="html:* xsl:*" />

<xsl:include href="Footer.xsl"/>

<xsl:template match="root">
<html>
  <head>
    <title>Engineering Village System Error</title>
    <SCRIPT LANGUAGE="Javascript" SRC="/static/js/StylesheetLinks.js"/>
     <script language="javascript">
      <xsl:comment>
    	 <![CDATA[
    			// This method is here for when the error page
    			// is loaded into a child window.
    			// we do not want the liks to be loaded in the child
    			// but in the parent
    			function forwardLink(url) {

    				if (window.opener != null) {
    					window.opener.location = url;
    					self.close();
    				} else {
    					parent.document.location = url;
    				}

    			}
        ]]>
    	// </xsl:comment>
  		</script>
		<link rel="stylesheet" type="text/css" media="all" href="/engresources/stylesheets/ev_txt_v01.css"/>
		<link rel="stylesheet" type="text/css" media="all" href="/engresources/stylesheets/ev_common_sciverse_v01.css"/>
  </head>

  <body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0" leftmargin="0" rightmargin="0">
  <!-- Start of toplogo and navigation bar -->
<!-- HEADER -->
	<div id="header" style="min-width:600px">
			<div id="masthead">
				<div id="logoEV">
					<img src="/static/images/SVEV_logo.gif" usemap="#svlogomap"/>
					<map name="svlogomap">
						<area 
							title="SciVerse - Elsevier's product suite for search and discovery" 
							target="_blank"
							href="http://www.sciverse.com/"
							coords="0,0,99,70"/>
						<area 
							title="Engineering Village - The information discovery platform of choice for the engineering community" 
							href="/controller/servlet/Controller?CID=home"
							coords="100,8,180,70"/>
					</map>
				</div>
			</div>
		<div id="headerLink">
		<div id="suites" class="clearfix">
		&#160;
		</div>
		</div>
		<div class="navigation txtLarger clearfix">
		&#160;
		</div>
	</div>
  <!-- end of toplogo and navigation bar -->

  <div id="content-container">
  <h2>System Error</h2>
  
  <div>
  		<xsl:choose>
  			<xsl:when test="string(/root/MESSAGE/DISPLAY)">
  				<A CLASS="MedBlackText"><xsl:value-of select="/root/MESSAGE/DISPLAY"/></A>
  			</xsl:when>
  			<xsl:otherwise>
  				<A CLASS="MedBlackText">Sorry, a system error has occurred, and your request cannot be completed.</A>
  				<P>
  				<A CLASS="MedBlackText">You may </A>
  				<A CLASS="LgBlueLink" href="javascript:forwardLink('/controller/servlet/Controller?CID=feedback');">contact us</A><A CLASS="MedBlackText"> to report this problem, or you may begin a </A>
  				<A CLASS="LgBlueLink" href="javascript:forwardLink('/controller/servlet/Controller?CID=home');">new search</A>.
  				</P>
  			</xsl:otherwise>
  		</xsl:choose>
  </div>
  
  </div>

  <div class="hr" style="color:#D7D7D7; background-color: #D7D7D7; margin:7px 0"><hr/></div>

  <!-- Start of footer-->
  <xsl:apply-templates select="FOOTER"/>

  </body>

</html>
</xsl:template>

</xsl:stylesheet>