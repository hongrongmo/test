<?xml version="1.0"?>
<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:html="http://www.w3.org/TR/REC-html40"
	xmlns:java="java:java.net.URLEncoder"
	exclude-result-prefixes="java html xsl"
>


<xsl:output method="html" indent="no" doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"/>

<xsl:include href="Header.xsl"/>
<xsl:include href="GlobalLinks.xsl"/>
<xsl:include href="Footer.xsl"/>

<xsl:template match="PAGE">

<xsl:variable name="SESSION-ID">
	<xsl:value-of select="SESSION-ID"/>
</xsl:variable>

<xsl:variable name="DATABASE">
	<xsl:value-of select="/PAGE/DBMASK"/>
</xsl:variable>

<xsl:variable name="REFEMAIL">
	<xsl:value-of select="REFEMAIL"/>
</xsl:variable>

<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>Engineering Village - Ask an Expert</title>
    <SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js"/>
    <script type="text/JavaScript" language="JavaScript">
    <xsl:comment>
		<![CDATA[
    function MM_preloadImages() { //v3.0
      var d=document; if(d.images){ if(!d.MM_p) d.MM_p=new Array();
        var i,j=d.MM_p.length,a=MM_preloadImages.arguments; for(i=0; i<a.length; i++)
        if (a[i].indexOf("#")!=0){ d.MM_p[j]=new Image; d.MM_p[j++].src=a[i];}}
    }

    function MM_findObj(n, d) { //v4.01
      var p,i,x;  if(!d) d=document; if((p=n.indexOf("?"))>0&&parent.frames.length) {
        d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);}
      if(!(x=d[n])&&d.all) x=d.all[n]; for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][n];
      for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=MM_findObj(n,d.layers[i].document);
      if(!x && d.getElementById) x=d.getElementById(n); return x;
    }

    function MM_nbGroup(event, grpName) { //v6.0
      var i,img,nbArr,args=MM_nbGroup.arguments;
      if (event == "init" && args.length > 2) {
        if ((img = MM_findObj(args[2])) != null && !img.MM_init) {
          img.MM_init = true; img.MM_up = args[3]; img.MM_dn = img.src;
          if ((nbArr = document[grpName]) == null) nbArr = document[grpName] = new Array();
          nbArr[nbArr.length] = img;
          for (i=4; i < args.length-1; i+=2) if ((img = MM_findObj(args[i])) != null) {
            if (!img.MM_up) img.MM_up = img.src;
            img.src = img.MM_dn = args[i+1];
            nbArr[nbArr.length] = img;
        } }
      } else if (event == "over") {
        document.MM_nbOver = nbArr = new Array();
        for (i=1; i < args.length-1; i+=3) if ((img = MM_findObj(args[i])) != null) {
          if (!img.MM_up) img.MM_up = img.src;
          img.src = (img.MM_dn && args[i+2]) ? args[i+2] : ((args[i+1])? args[i+1] : img.MM_up);
          nbArr[nbArr.length] = img;
        }
      } else if (event == "out" ) {
        for (i=0; i < document.MM_nbOver.length; i++) {
          img = document.MM_nbOver[i]; img.src = (img.MM_dn) ? img.MM_dn : img.MM_up; }
      } else if (event == "down") {
        nbArr = document[grpName];
        if (nbArr)
          for (i=0; i < nbArr.length; i++) { img=nbArr[i]; img.src = img.MM_up; img.MM_dn = 0; }
        document[grpName] = nbArr = new Array();
        for (i=2; i < args.length-1; i+=2) if ((img = MM_findObj(args[i])) != null) {
          if (!img.MM_up) img.MM_up = img.src;
          img.src = img.MM_dn = (args[i+1])? args[i+1] : img.MM_up;
          nbArr[nbArr.length] = img;
      } }
    }
		]]>
    // </xsl:comment>
    </script>

    <script type="text/JavaScript" language="JavaScript">
    <xsl:comment>
		<![CDATA[
    function toggleDiv(divname)
    {
      var allIds = ['zero','one','two','three'];
      for(var i in allIds)
      {
        var thisId = allIds[i];
        var atoggleDiv = document.getElementById(thisId);

        if(thisId == divname) {
          atoggleDiv.style.display = "block";
        }
        else {
          atoggleDiv.style.display = "none";
        }
      }
    }
		]]>
    // </xsl:comment>
    </script>

</head>

<body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">

<center>

    <xsl:apply-templates select="HEADER"/>

		<!-- Insert the Global Link table -->
		<xsl:apply-templates select="GLOBAL-LINKS">
			<xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
		 	<xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
		 	<xsl:with-param name="LINK">referenceServices</xsl:with-param>
		</xsl:apply-templates>

    <!-- Empty Gray Navigation Bar -->
    <table border="0" width="99%" cellspacing="0" cellpadding="0">
    	<tr><td valign="middle" height="24" bgcolor="#C3C8D1"><img src="/engresources/images/s.gif" border="0"/></td></tr>
    </table>

<p></p>
<table border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td><a href="javascript:;" target="_top" onclick="MM_nbGroup('down','group1','_1','/engresources/images/halftone_menu/1.jpg',0);toggleDiv('one');" onmouseover="MM_nbGroup('over','_1','/engresources/images/halftone_menu/1_over.jpg','',0)" onmouseout="MM_nbGroup('out')"><img src="/engresources/images/halftone_menu/1.jpg" alt="" name="_1" width="306" height="451" border="0" id="_1" onload="MM_nbGroup('init','group1','_1','/engresources/images/halftone_menu/1_gost.jpg',0)" /></a></td>
    <td><a href="javascript:;" target="_top" onclick="MM_nbGroup('down','group1','_2','/engresources/images/halftone_menu/2.jpg',0);toggleDiv('two');" onmouseover="MM_nbGroup('over','_2','/engresources/images/halftone_menu/2_over.jpg','',0)" onmouseout="MM_nbGroup('out')"><img src="/engresources/images/halftone_menu/2.jpg" alt="" name="_2" width="306" height="451" border="0" id="_2" onload="MM_nbGroup('init','group1','_2','/engresources/images/halftone_menu/2_gost.jpg',0)" /></a></td>
    <td><a href="javascript:;" target="_top" onclick="MM_nbGroup('down','group1','_3','/engresources/images/halftone_menu/3.jpg',0);toggleDiv('three');" onmouseover="MM_nbGroup('over','_3','/engresources/images/halftone_menu/3_over.jpg','',0)" onmouseout="MM_nbGroup('out')"><img src="/engresources/images/halftone_menu/3.jpg" alt="" name="_3" width="306" height="451" border="0" id="_3" onload="MM_nbGroup('init','group1','_3','/engresources/images/halftone_menu/3_gost.jpg',0)" /></a></td>
  </tr>
</table>

<div id="zero" style="display: block;">
  <h1>&#160;</h1>
</div>
<div id="one" style="display: none;">
  <h1>Ask an Engineer</h1>
</div>
<div id="two" style="display: none;">
  <h1>Ask a Product Specialist</h1>
</div>
<div id="three" style="display: none;">
  <h1>Ask a Librarian</h1>
</div>


  <!-- end of the lower area below the navigation bar -->
  <xsl:apply-templates select="FOOTER">
  		<xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
  		<xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
  </xsl:apply-templates>

</center>

</body>
</html>

</xsl:template>

</xsl:stylesheet>