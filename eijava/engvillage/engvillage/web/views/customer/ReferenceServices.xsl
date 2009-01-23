<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>'>
  <!ENTITY quot '<xsl:text disable-output-escaping="yes">&amp;quot;</xsl:text>'>
  <!ENTITY ldquo '<xsl:text disable-output-escaping="yes">&amp;ldquo;</xsl:text>'>
  <!ENTITY rdquo '<xsl:text disable-output-escaping="yes">&amp;rdquo;</xsl:text>'>
  <!ENTITY rsquo '<xsl:text disable-output-escaping="yes">&amp;rsquo;</xsl:text>'>
]>
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


<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<style type="text/css">
    <xsl:comment>
		<![CDATA[
#one {
	width:972px;
	height:600px;
	z-index:1;
}
#two {
	width:972px;
	height:2087px;
	z-index:1;
}
#three {
	width:972px;
	height:411px;
	z-index:1;
}
.style1 {
	font-family: Verdana, Arial, Helvetica, sans-serif;
	font-size: 12px;
	}
.styleunderlined {
	font-family: Verdana, Arial, Helvetica, sans-serif;
	font-size: 12px;
	text-decoration: underline;
	padding-bottom: 5px;
}
.style2 {
	font-size: 14px;
	font-weight: bold;
}
.style4 {font-size: 14px; font-weight: bold; font-family: Verdana, Arial, Helvetica, sans-serif; }
.style5 {font-family: Verdana, Arial, Helvetica, sans-serif; font-size: 12px; font-weight: bold; }
		]]>
    // </xsl:comment>
</style>

<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1" />
<title>Engineering Village - Reference Services</title>
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
    function pro_MM_preloadImages() { //v3.0
      var d=document; if(d.images){ if(!d.MM_p) d.MM_p=new Array();
        var i,j=d.MM_p.length,a=pro_MM_preloadImages.arguments; for(i=0; i<a.length; i++)
        if (a[i].indexOf("#")!=0){ d.MM_p[j]=new Image; d.MM_p[j++].src=a[i];}}
    }

    function pro_MM_swapImgRestore() { //v3.0
      var i,x,a=document.MM_sr; for(i=0;a&&i<a.length&&(x=a[i])&&x.oSrc;i++) x.src=x.oSrc;
    }

    function pro_MM_findObj(n, d) { //v4.01
      var p,i,x;  if(!d) d=document; if((p=n.indexOf("?"))>0&&parent.frames.length) {
        d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);}
      if(!(x=d[n])&&d.all) x=d.all[n]; for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][n];
      for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=pro_MM_findObj(n,d.layers[i].document);
      if(!x && d.getElementById) x=d.getElementById(n); return x;
    }

    function pro_MM_swapImage() { //v3.0
      var i,j=0,x,a=pro_MM_swapImage.arguments; document.MM_sr=new Array; for(i=0;i<(a.length-2);i+=3)
       if ((x=pro_MM_findObj(a[i]))!=null){document.MM_sr[j++]=x; if(!x.oSrc) x.oSrc=x.src; x.src=a[i+2];}
    }
        ]]>
    // </xsl:comment>
    </script>

    <script type="text/JavaScript" language="JavaScript">
    <xsl:comment>
    <![CDATA[
    function lib_MM_preloadImages() { //v3.0
      var d=document; if(d.images){ if(!d.MM_p) d.MM_p=new Array();
        var i,j=d.MM_p.length,a=lib_MM_preloadImages.arguments; for(i=0; i<a.length; i++)
        if (a[i].indexOf("#")!=0){ d.MM_p[j]=new Image; d.MM_p[j++].src=a[i];}}
    }

    function lib_MM_swapImgRestore() { //v3.0
      var i,x,a=document.MM_sr; for(i=0;a&&i<a.length&&(x=a[i])&&x.oSrc;i++) x.src=x.oSrc;
    }

    function lib_MM_findObj(n, d) { //v4.01
      var p,i,x;  if(!d) d=document; if((p=n.indexOf("?"))>0&&parent.frames.length) {
        d=parent.frames[n.substring(p+1)].document; n=n.substring(0,p);}
      if(!(x=d[n])&&d.all) x=d.all[n]; for (i=0;!x&&i<d.forms.length;i++) x=d.forms[i][n];
      for(i=0;!x&&d.layers&&i<d.layers.length;i++) x=lib_MM_findObj(n,d.layers[i].document);
      if(!x && d.getElementById) x=d.getElementById(n); return x;
    }

    function lib_MM_swapImage() { //v3.0
      var i,j=0,x,a=lib_MM_swapImage.arguments; document.MM_sr=new Array; for(i=0;i<(a.length-2);i+=3)
       if ((x=lib_MM_findObj(a[i]))!=null){document.MM_sr[j++]=x; if(!x.oSrc) x.oSrc=x.src; x.src=a[i+2];}
    }

        ]]>
    // </xsl:comment>
    </script>

    <script type="text/JavaScript" language="JavaScript">
    <xsl:comment>
		<![CDATA[
    var pageTitles = new Array();
    pageTitles['zero'] = 'Home';
    pageTitles['one'] = 'Ask an Engineer';
    pageTitles['two'] = 'Ask a Product Specialist';
    pageTitles['three'] = 'Ask a Librarian';

    function toggleDiv(divname)
    {
      var allIds = ['zero','one','two','three'];

      for(var i in allIds)
      {
        var thisId = allIds[i];
        var atoggleDiv = document.getElementById(thisId);

        if(thisId == divname) {
          atoggleDiv.style.display = "block";
          pageTracker._trackPageview('/ReferenceServices/' + pageTitles[divname]);
        }
        else {
          atoggleDiv.style.display = "none";
        }
      }
      if(divname != 'zero') {
        document.location = "#top";
      }
    }

    function emailFormat(sessionid,section)
    {
      var url = "/controller/servlet/Controller?EISESSION="+sessionid+"&CID=askanexpert&section="+escape(pageTitles[section])+"&sectionid="+section;
      new_window=window.open(url,'NewWindow','status=yes,resizable,scrollbars,width=600,height=600');
      new_window.focus();
    }

    function emailGuruFormat(sessionid,section,discipline,guru)
    {
      var url = "/controller/servlet/Controller?EISESSION="+sessionid+"&CID=askanexpert&section="+escape(pageTitles[section])+"&sectionid="+section+"&discipline="+escape(layerTitles[discipline])+"&disciplineid="+discipline+"&guru="+escape(guru);
      new_window=window.open(url,'NewWindow','status=yes,resizable,scrollbars,width=600,height=600');
      new_window.focus();
    }

    var layerTitles = new Array();
    layerTitles['zero'] = 'zero';
    layerTitles['Layer12'] = 'Chemical';
    layerTitles['Layer15'] = 'Industrial';
    layerTitles['Layer1'] = 'Mechanical';
    layerTitles['Layer8'] = 'Electrical';
    layerTitles['Layer3'] = 'Signal Processing';
    layerTitles['Layer2'] = 'Maufacturing';
    layerTitles['Layer10'] = 'Materials';
    layerTitles['Layer9'] = 'Engineering Management';
    layerTitles['Layer11'] = 'Computer';

    function toggleDivLayers(divname)
    {
      var allIds = ['Layer12','Layer15','Layer1','Layer8','Layer3','Layer2','Layer10','Layer9','Layer11'];

      for(var i in allIds)
      {
        var thisId = allIds[i];
        var atoggleDiv = document.getElementById(thisId);

        if(thisId == divname) {
          atoggleDiv.style.display = "block";
          pageTracker._trackPageview('/ReferenceServices/AskAnEngineer/' + layerTitles[divname]);
        }
        else {
          atoggleDiv.style.display = "none";
        }
      }
      if(divname != 'zero') {
        document.location = "#auto_scroller";
      }
    }

		]]>
    // </xsl:comment>
    </script>
</head>

<body bgcolor="#FFFFFF" topmargin="0" marginheight="0" marginwidth="0">
<a id="top" href="top"/>

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
    <td width="238"><a href="javascript:;" target="_top" onclick="MM_nbGroup('down','group1','Ask_an_Engineer','/engresources/images/ae/ask_engineer.gif',0);toggleDiv('one');" onmouseover="MM_nbGroup('over','Ask_an_Engineer','/engresources/images/ae/ask_engineer_over.gif','',0)" onmouseout="MM_nbGroup('out')"><img src="/engresources/images/ae/ask_engineer.gif" alt="" name="Ask_an_Engineer" width="238" height="339" border="0" id="Ask_an_Engineer" onload="MM_nbGroup('init','group1','Ask_an_Engineer','/engresources/images/ae/ask_engineer_down.gif',0)" /></a></td>
    <td width="25"><img src="/engresources/images/ae/spacer.gif" alt="spacer" width="25" height="1" /></td>
    <td width="238"><a href="javascript:;" target="_top" onclick="MM_nbGroup('down','group1','Ask_Product_Specialist','/engresources/images/ae/ask_specialist.gif',0);toggleDiv('two');" onmouseover="MM_nbGroup('over','Ask_Product_Specialist','/engresources/images/ae/ask_specialist_over.gif','',0)" onmouseout="MM_nbGroup('out')"><img src="/engresources/images/ae/ask_specialist.gif" alt="" name="Ask_Product_Specialist" width="238" height="339" border="0" id="Ask_Product_Specialist" onload="MM_nbGroup('init','group1','Ask_Product_Specialist','/engresources/images/ae/ask_specialist_down.gif',0)" /></a></td>
    <td width="25"><img src="/engresources/images/ae/spacer.gif" alt="spacer" width="25" height="1" /></td>
    <td width="238"><a href="javascript:;" target="_top" onclick="MM_nbGroup('down','group1','Ask_a_Librarian','/engresources/images/ae/ask_librarian.gif',0);toggleDiv('three');" onmouseover="MM_nbGroup('over','Ask_a_Librarian','/engresources/images/ae/ask_librarian_over.gif','',0)" onmouseout="MM_nbGroup('out')"><img src="/engresources/images/ae/ask_librarian.gif" alt="" name="Ask_a_Librarian" border="0" id="Ask_a_Librarian" onload="MM_nbGroup('init','group1','Ask_a_Librarian','/engresources/images/ae/ask_librarian_down.gif',0)" /></a></td>
  </tr>
</table>
<div id="zero" style="display: block;">
  <h1>&#160;</h1>
</div>


<!-- Ask an Engineer -->
<!-- Ask an Engineer -->
<!-- Ask an Engineer -->

<div id="one" style="display: none;">

    <p align="left">&nbsp;</p>

<table border="0" cellpadding="0" cellspacing="0">
  <tr>
    <td><a href="javascript:;" target="_top" onclick="toggleDivLayers('Layer12');MM_nbGroup('down','group2','Chemical','/engresources/images/ae/Chemical.gif',0)" onmouseover="MM_nbGroup('over','Chemical','/engresources/images/ae/Chemical_over.gif','',0)" onmouseout="MM_nbGroup('out')"><img src="/engresources/images/ae/Chemical.gif" alt="Chemical" name="Chemical" width="108" height="128" border="0" id="Chemical" onload="MM_nbGroup('init','group2','Chemical','/engresources/images/ae/Chemical_down.gif',0)" /></a></td>
    <td><a href="javascript:;" target="_top" onclick="toggleDivLayers('Layer15');MM_nbGroup('down','group2','Industrial','/engresources/images/ae/Idustrial.gif',0)" onmouseover="MM_nbGroup('over','Industrial','/engresources/images/ae/Idustrial_over.gif','',0)" onmouseout="MM_nbGroup('out')"><img src="/engresources/images/ae/Idustrial.gif" alt="Industrial" name="Industrial" width="108" height="128" border="0" id="Industrial" onload="MM_nbGroup('init','group2','Industrial','/engresources/images/ae/Idustrial_down.gif',0)" /></a></td>
    <td><a href="javascript:;" target="_top" onclick="toggleDivLayers('Layer1');MM_nbGroup('down','group2','Mechanical','/engresources/images/ae/Mechanical.gif',0)" onmouseover="MM_nbGroup('over','Mechanical','/engresources/images/ae/Mechanical_over.gif','',0)" onmouseout="MM_nbGroup('out')"><img src="/engresources/images/ae/Mechanical.gif" alt="Mechanical" name="Mechanical" width="108" height="128" border="0" id="Mechanical" onload="MM_nbGroup('init','group2','Mechanical','/engresources/images/ae/Mechanical_down.gif',0)" /></a></td>
    <td><a href="javascript:;" target="_top" onclick="toggleDivLayers('Layer8');MM_nbGroup('down','group2','Electrical','/engresources/images/ae/Electrical.gif',0)" onmouseover="MM_nbGroup('over','Electrical','/engresources/images/ae/Electrical_over.gif','',0)" onmouseout="MM_nbGroup('out')"><img src="/engresources/images/ae/Electrical.gif" alt="Electrical" name="Electrical" width="108" height="128" border="0" id="Electrical" onload="MM_nbGroup('init','group2','Electrical','/engresources/images/ae/Electrical_down.gif',0)" /></a></td>
    <td><a href="javascript:;" target="_top" onclick="toggleDivLayers('Layer3');MM_nbGroup('down','group2','SignalProcessing','/engresources/images/ae/SignalProcessing.gif',0)" onmouseover="MM_nbGroup('over','SignalProcessing','/engresources/images/ae/SignalProcessing_over.gif','',0)" onmouseout="MM_nbGroup('out')"><img src="/engresources/images/ae/SignalProcessing.gif" alt="Signal Processing" name="SignalProcessing" width="108" height="128" border="0" id="SignalProcessing" onload="MM_nbGroup('init','group2','SignalProcessing','/engresources/images/ae/SignalProcessing_down.gif',0)" /></a></td>
    <td><a href="javascript:;" target="_top" onclick="toggleDivLayers('Layer2');MM_nbGroup('down','group2','Manufacturing','/engresources/images/ae/Manufacturing.gif',0)" onmouseover="MM_nbGroup('over','Manufacturing','/engresources/images/ae/Manufacturing_over.gif','',0)" onmouseout="MM_nbGroup('out')"><img src="/engresources/images/ae/Manufacturing.gif" alt="Manufacturing" name="Manufacturing" width="108" height="128" border="0" id="Manufacturing" onload="MM_nbGroup('init','group2','Manufacturing','/engresources/images/ae/Manufacturing_down.gif',0)" /></a></td>
    <td><a href="javascript:;" target="_top" onclick="toggleDivLayers('Layer10');MM_nbGroup('down','group2','Materials','/engresources/images/ae/Materials.gif',0)" onmouseover="MM_nbGroup('over','Materials','/engresources/images/ae/Materials_over.gif','',0)" onmouseout="MM_nbGroup('out')"><img src="/engresources/images/ae/Materials.gif" alt="Materials" name="Materials" width="108" height="128" border="0" id="Materials" onload="MM_nbGroup('init','group2','Materials','/engresources/images/ae/Materials_down.gif',0)" /></a></td>
    <td><a href="javascript:;" target="_top" onclick="toggleDivLayers('Layer9');MM_nbGroup('down','group2','Management','/engresources/images/ae/Management-.gif',0)" onmouseover="MM_nbGroup('over','Management','/engresources/images/ae/Management_over.gif','',0)" onmouseout="MM_nbGroup('out')"><img src="/engresources/images/ae/Management-.gif" alt="Engineering Management" name="Management" width="108" height="128" border="0" id="Management" onload="MM_nbGroup('init','group2','Management','/engresources/images/ae/Management_down.gif',0)" /></a></td>
    <td><a href="javascript:;" target="_top" onclick="toggleDivLayers('Layer11');MM_nbGroup('down','group2','Computer','/engresources/images/ae/Computer-.gif',0)" onmouseover="MM_nbGroup('over','Computer','/engresources/images/ae/Computer_over.gif','',0)" onmouseout="MM_nbGroup('out')"><img src="/engresources/images/ae/Computer-.gif" alt="Computer" name="Computer" width="108" height="128" border="0" id="Computer" onload="MM_nbGroup('init','group2','Computer','/engresources/images/ae/Computer_down.gif',0)" /></a></td>
  </tr>
</table>
  <a id="auto_scroller" href="auto_scroller"/>

    <div id="Layer1" style="display: none;">
      <p align="left" class="style1"><span class="style1">Our Senior Engineers will draw on their professional knowledge and experience to answer your technical engineering questions.&nbsp; They can also point you to the appropriate companies, consultants, research institutes, web sites, and other resources that can help you solve your problem.</span></p>

      <table width="972" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td width="111" height="163" align="left" valign="top"><img src="/engresources/images/ae/Mechanical.gif" alt="Mechanical" width="108" height="128" /></td>
          <td width="25"><img src="/engresources/images/ae/spacer.gif" alt="spacer" width="25" height="1" /></td>
          <td width="417" align="left" valign="top"><p align="left" class="style1"><span class="style2"><xsl:apply-templates select="GURUS/GURU[@NAME='Ryo Samuel Amano']"/></span> <br />
              Fluid and thermodynamics professor, Department of Mechanical Engineering, University of Wisconsin-Milwaukee</p>
            <p align="left" class="style1">Areas of expertise: <xsl:apply-templates select="DISCIPLINES/DISCIPLINE[@NAME='mechanical']/GURU[@NAME='Ryo Samuel Amano']/SEARCHES"/></p>
            <p align="left" class="style1">&nbsp;</p>
            <p align="left" class="style1"><span class="style2"><xsl:apply-templates select="GURUS/GURU[@NAME='Ronald A. Perez']"/><br />
            </span>Interim Dean, College of Engineering &amp; Applied Science<br />
            University of Wisconsin-Milwaukee </p>
            <p align="left" class="style1">Areas of expertise: <xsl:apply-templates select="DISCIPLINES/DISCIPLINE[@NAME='mechanical']/GURU[@NAME='Ronald A. Perez']/SEARCHES"/></p>
            <p align="left" class="style1">&nbsp;</p>
          </td>
          <td width="25" align="left" valign="top"><img src="/engresources/images/ae/spacer.gif" alt="spacer" width="25" height="1" /></td>
          <td width="394" align="left" valign="top"><div align="left"><span class="style1"><span class="style2">Sample questions:  &nbsp; &nbsp;</span></span></div>
            <p align="left" class="style1">&ldquo;What type of steel rod/axle&nbsp;would you use for one way roller bearings.&rdquo;</p>
            <p align="center"><a href="javascript:emailGuruFormat('$SESSIONID','one','Layer1','Ryo Samuel Amano');"><img src="/engresources/images/ae/email.gif" alt="email" width="124" height="14" border="0" /></a></p>
            <p class="style1">&nbsp;</p>
            <p class="style1">&nbsp;</p>
            <p align="left" class="style1">&nbsp;&ldquo;How can I calculate or at arrive the capacity of a mechanical press?&rdquo;</p>
            <p align="center"><a href="javascript:emailGuruFormat('$SESSIONID','one','Layer1','Ronald A. Perez');"><img src="/engresources/images/ae/email.gif" alt="email" width="124" height="14" border="0"/></a></p>
            </td>
        </tr>
      </table>
      <p><br />
      </p>
    </div>

    <div id="Layer2" style="display: none;">
      <p align="left" class="style1"><span class="style1">Our Senior Engineers will draw on their professional knowledge and experience to answer your technical engineering questions.&nbsp; They can also point you to the appropriate companies, consultants, research institutes, web sites, and other resources that can help you solve your problem.</span></p>

      <table width="972" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td width="111" align="left" valign="top"><img src="/engresources/images/ae/Manufacturing.gif" alt="Manufacturing" width="108" height="128" /></td>
          <td width="25"><img src="/engresources/images/ae/spacer.gif" alt="spacer" width="25" height="1" /></td>
          <td width="417" align="left" valign="top"><p class="style1"><span class="style2"><xsl:apply-templates select="GURUS/GURU[@NAME='Robert D. Borchelt']"/></span> <br />
            Technical advisor for assembly and automation in the Corporate Operations Analysis Group, Cummins Engine Company
    </p>
            <p class="style1">Areas of expertise: <xsl:apply-templates select="DISCIPLINES/DISCIPLINE[@NAME='manufacturing']/GURU[@NAME='Robert D. Borchelt']/SEARCHES"/></p>
            <p class="style1">&nbsp;</p>
            <p class="style1"><span class="style2"><xsl:apply-templates select="GURUS/GURU[@NAME='Gregory A. Sedrick']"/></span> <br />
    UC Foundation assistant professor and director, Engineering Management, Industrial and Manufacturing Engineering Programs, University of Tennessee </p>
            <p class="style1">Areas of expertise: <xsl:apply-templates select="DISCIPLINES/DISCIPLINE[@NAME='manufacturing']/GURU[@NAME='Gregory A. Sedrick']/SEARCHES"/></p></td>
          <td width="25" align="left" valign="top"><img src="/engresources/images/ae/spacer.gif" alt="spacer" width="25" height="1" /></td>
          <td width="394" align="left" valign="top"><div align="left"><span class="style1"><span class="style2">Sample question:  &nbsp; &nbsp;</span></span></div>
            <p align="left" class="style1">&ldquo;How do you organize the value engineering functions in a tractor manufacturing firm?&rdquo;</p>
            <p align="center"><a href="javascript:emailGuruFormat('$SESSIONID','one','Layer2','Robert D. Borchelt');"><img src="/engresources/images/ae/email.gif" alt="email" width="124" height="14" border="0"/></a></p>
            <p class="style1">&nbsp;</p>
            <p class="style1">&nbsp;</p>
            <p class="style1">&nbsp;</p>
            <p class="style1">&nbsp;</p>
            <p class="style1">&ldquo;What&rsquo;s the best individual fabrication system for Bulk-handling machines?&rdquo;</p>
            <p align="center"><a href="javascript:emailGuruFormat('$SESSIONID','one','Layer2','Gregory A. Sedrick');"><img src="/engresources/images/ae/email.gif" alt="email" width="124" height="14" border="0"/></a></p>
            <p align="center">&nbsp;</p></td>
        </tr>
      </table>
      <p><br />
      </p>
    </div>

    <div id="Layer3" style="display: none;">
      <p align="left" class="style1"><span class="style1">Our Senior Engineers will draw on their professional knowledge and experience to answer your technical engineering questions.&nbsp; They can also point you to the appropriate companies, consultants, research institutes, web sites, and other resources that can help you solve your problem.</span></p>

      <table width="972" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td width="111" align="left" valign="top"><img src="/engresources/images/ae/SignalProcessing.gif" alt="SignalProcessing" width="108" height="128" /></td>
          <td width="25"><img src="/engresources/images/ae/spacer.gif" alt="spacer" width="25" height="1" /></td>
          <td width="417" align="left" valign="top"><p class="style1"><span class="style2"><xsl:apply-templates select="GURUS/GURU[@NAME='Chi Hau Chen']"/></span> <br />
            Signal processing professor, Electrical and Computer Engineering, University of Massachusetts</p>
              <p class="style1">Areas of expertise: <xsl:apply-templates select="DISCIPLINES/DISCIPLINE[@NAME='signal processing']/GURU[@NAME='Chi Hau Chen']/SEARCHES"/></p></td>
          <td width="25" align="left" valign="top"><img src="/engresources/images/ae/spacer.gif" alt="spacer" width="25" height="1" /></td>
          <td width="394" align="left" valign="top"><span class="style1"><span class="style2">Sample question:  &nbsp; &nbsp;</span> <br />
                <br />
            </span>
            <p class="style1">&ldquo;Which images or signals inside the tunnel are best for object detection?&rdquo;</p>
            <p align="center"><a href="javascript:emailGuruFormat('$SESSIONID','one','Layer3','Chi Hau Chen');"><img src="/engresources/images/ae/email.gif" alt="email" width="124" height="14" border="0"/></a></p></td>
        </tr>
      </table>
      <p><br />
      </p>
    </div>


    <div id="Layer8" style="display: none;">
      <p align="left" class="style1"><span class="style1">Our Senior Engineers will draw on their professional knowledge and experience to answer your technical engineering questions.&nbsp; They can also point you to the appropriate companies, consultants, research institutes, web sites, and other resources that can help you solve your problem.</span></p>

      <table width="972" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td width="111" align="left" valign="top"><img src="/engresources/images/ae/Electrical.gif" alt="Electrical" width="108" height="128" /></td>
          <td width="25"><img src="/engresources/images/ae/spacer.gif" alt="spacer" width="25" height="1" /></td>
          <td width="417" align="left" valign="top"><p class="style1"><span class="style2"><xsl:apply-templates select="GURUS/GURU[@NAME='Kanti Prasad']"/></span> <br />Professor, electrical engineering, and director, Microelectronics/VLSI Technology, University of Massachusetts</p>
            <p class="style1">Areas of expertise: <xsl:apply-templates select="DISCIPLINES/DISCIPLINE[@NAME='electrical']/GURU[@NAME='Kanti Prasad']/SEARCHES"/></p></td>
          <td width="25" align="left" valign="top"><img src="/engresources/images/ae/spacer.gif" alt="spacer" width="25" height="1" /></td>
          <td width="394" align="left" valign="top"><span class="style1"><span class="style2">Sample question:  &nbsp; &nbsp;</span> <br />
                <br />
            </span>
              <p class="style1">&ldquo;How can I calibrate a current transformer?&rdquo;</p>
            <p align="center"><a href="javascript:emailGuruFormat('$SESSIONID','one','Layer8','Kanti Prasad');"><img src="/engresources/images/ae/email.gif" alt="email" width="124" height="14" border="0"/></a></p></td>
        </tr>
      </table>
      <p><br />
      </p>
    </div>

    <div id="Layer9" style="display: none;">
      <p align="left" class="style1"><span class="style1">Our Senior Engineers will draw on their professional knowledge and experience to answer your technical engineering questions.&nbsp; They can also point you to the appropriate companies, consultants, research institutes, web sites, and other resources that can help you solve your problem.</span></p>

      <table width="972" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td width="111" align="left" valign="top"><img src="/engresources/images/ae/Management-.gif" alt="Management" width="108" height="128" /></td>
          <td width="25"><img src="/engresources/images/ae/spacer.gif" alt="spacer" width="25" height="1" /></td>
          <td width="417" align="left" valign="top"><p class="style1"><span class="style2"><xsl:apply-templates select="GURUS/GURU[@NAME='Donald W. Merino, Jr.']"/></span> <br />
            Areas of expertise: <xsl:apply-templates select="DISCIPLINES/DISCIPLINE[@NAME='management']/GURU[@NAME='Donald W. Merino, Jr.']/SEARCHES"/></p>
          </td>
          <td width="25" align="left" valign="top"><img src="/engresources/images/ae/spacer.gif" alt="spacer" width="25" height="1" /></td>
          <td width="394" align="left" valign="top"><span class="style1"><span class="style2">Sample question:  &nbsp; &nbsp;</span> <br /></span>
            <p align="left" class="style1">&ldquo;During design, how is optimization achieved and what factors are considered?&rdquo; </p>
            <p align="center"><a href="javascript:emailGuruFormat('$SESSIONID','one','Layer9','Donald W. Merino, Jr.');"><img src="/engresources/images/ae/email.gif" alt="email" width="124" height="14" border="0"/></a></p>
          </td>
        </tr>
      </table>
      <p><br />
      </p>
    </div>

    <div id="Layer15" style="display: none;">
      <p align="left" class="style1"><span class="style1">Our Senior Engineers will draw on their professional knowledge and experience to answer your technical engineering questions.&nbsp; They can also point you to the appropriate companies, consultants, research institutes, web sites, and other resources that can help you solve your problem.</span></p>

      <table width="972" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td width="111" align="left" valign="top"><img src="/engresources/images/ae/Idustrial.gif" alt="Industrial" width="108" height="128" /></td>
          <td width="25"><img src="/engresources/images/ae/spacer.gif" alt="spacer" width="25" height="1" /></td>
          <td width="417" align="left" valign="top"><p class="style1"><span class="style2"><xsl:apply-templates select="GURUS/GURU[@NAME='Donald W. Merino, Jr.']"/></span> <br />
            Areas of expertise: <xsl:apply-templates select="DISCIPLINES/DISCIPLINE[@NAME='industrial']/GURU[@NAME='Donald W. Merino, Jr.']/SEARCHES"/></p>
            <p class="style1">&nbsp;</p>
            <p class="style1"><span class="style2"><xsl:apply-templates select="GURUS/GURU[@NAME='Gregory A. Sedrick']"/></span> <br />
    UC Foundation assistant professor and director, Engineering Management, Industrial and Manufacturing Engineering Programs, University of Tennessee </p>
            <p class="style1">Areas of expertise: <xsl:apply-templates select="DISCIPLINES/DISCIPLINE[@NAME='industrial']/GURU[@NAME='Gregory A. Sedrick']/SEARCHES"/></p></td>
          <td width="25" align="left" valign="top"><img src="/engresources/images/ae/spacer.gif" alt="spacer" width="25" height="1" /></td>
          <td width="394" align="left" valign="top"><span class="style1"><span class="style2">Sample questions:  &nbsp; &nbsp;</span> <br /></span>
            <p align="left" class="style1">&ldquo;To ensure a certain interval in a time study, how many samples should be done?&rdquo;</p>
            <p align="center"><a href="javascript:emailGuruFormat('$SESSIONID','one','Layer15','Donald W. Merino, Jr.');"><img src="/engresources/images/ae/email.gif" alt="email" width="124" height="14" border="0"/></a></p>
            <p class="style1">&nbsp;</p>
            <p class="style1">&nbsp;</p>
            <p align="left" class="style1">&ldquo;How can I measure energy produced from water chilled air conditioning?&rdquo;</p>
            <p align="center"><a href="javascript:emailGuruFormat('$SESSIONID','one','Layer15','Gregory A. Sedrick');"><img src="/engresources/images/ae/email.gif" alt="email" width="124" height="14" border="0"/></a></p></td>
        </tr>
      </table>
      <p><br />
      </p>
    </div>


    <div id="Layer10" style="display: none;">
      <p align="left" class="style1"><span class="style1">Our Senior Engineers will draw on their professional knowledge and experience to answer your technical engineering questions.&nbsp; They can also point you to the appropriate companies, consultants, research institutes, web sites, and other resources that can help you solve your problem.</span></p>

      <table width="972" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td width="111" align="left" valign="top"><img src="/engresources/images/ae/Materials.gif" alt="Materials" width="108" height="128" /></td>
          <td width="25"><img src="/engresources/images/ae/spacer.gif" alt="spacer" width="25" height="1" /></td>
          <td width="417" align="left" valign="top"><p class="style1"><span class="style2"><xsl:apply-templates select="GURUS/GURU[@NAME='Keith Sheppard']"/></span> <br />
            Professor of Materials Science and Engineering, Stevens Institute of Technology
    </p>
            <p class="style1">Areas of expertise: <xsl:apply-templates select="DISCIPLINES/DISCIPLINE[@NAME='materials']/GURU[@NAME='Keith Sheppard']/SEARCHES"/></p></td>
          <td width="25" align="left" valign="top"><img src="/engresources/images/ae/spacer.gif" alt="spacer" width="25" height="1" /></td>
          <td width="394" align="left" valign="top"><span class="style1"><span class="style2">Sample question:  &nbsp; &nbsp;</span> <br />
                <br />
            </span>
              <p class="style1">&ldquo;Is it possible to give heat treatment to copper?&rdquo; </p>
            <p align="center"><a href="javascript:emailGuruFormat('$SESSIONID','one','Layer10','Keith Sheppard ');"><img src="/engresources/images/ae/email.gif" alt="email" width="124" height="14" border="0"/></a></p></td>
        </tr>
      </table>
      <p><br />
      </p>
    </div>



    <div id="Layer11" style="display: none;">
      <p align="left" class="style1"><span class="style1">Our Senior Engineers will draw on their professional knowledge and experience to answer your technical engineering questions.&nbsp; They can also point you to the appropriate companies, consultants, research institutes, web sites, and other resources that can help you solve your problem.</span></p>

      <table width="972" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td width="111" align="left" valign="top"><img src="/engresources/images/ae/Computer-.gif" alt="Computer" width="108" height="128" /></td>
          <td width="25"><img src="/engresources/images/ae/spacer.gif" alt="spacer" width="25" height="1" /></td>
          <td width="417" align="left" valign="top"><p class="style1"><span class="style2"><xsl:apply-templates select="GURUS/GURU[@NAME='Earl E. Swartzlander, Jr.']"/></span> <br />
            Professor, Electrical and Computer Engineering, and Schlumberger Centennial Chair in Engineering, University of Texas
    </p>
            <p class="style1">Areas of expertise: <xsl:apply-templates select="DISCIPLINES/DISCIPLINE[@NAME='computer']/GURU[@NAME='Earl E. Swartzlander, Jr.']/SEARCHES"/></p></td>
          <td width="25" align="left" valign="top"><img src="/engresources/images/ae/spacer.gif" alt="spacer" width="25" height="1" /></td>
          <td width="394" align="left" valign="top"><span class="style1"><span class="style2">Sample question:  &nbsp; &nbsp;</span> <br />
                <br />
            </span>
              <p class="style1">&ldquo;What is a "microprogrammed control unit" and what is the difference between "horizontal" and "vertical" microcode?&rdquo;</p>
            <p align="center"><a href="javascript:emailGuruFormat('$SESSIONID','one','Layer11','Earl E. Swartzlander, Jr.');"><img src="/engresources/images/ae/email.gif" alt="email" width="124" height="14" border="0"/></a></p></td>
        </tr>
      </table>
      <p><br />
      </p>
    </div>


    <div id="Layer12" style="display: none;">
      <p align="left" class="style1"><span class="style1">Our Senior Engineers will draw on their professional knowledge and experience to answer your technical engineering questions.&nbsp; They can also point you to the appropriate companies, consultants, research institutes, web sites, and other resources that can help you solve your problem.</span></p>

      <table width="972" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td width="111" align="left" valign="top"><img src="/engresources/images/ae/Chemical.gif" alt="Chemical" width="108" height="128" /></td>
          <td width="25"><img src="/engresources/images/ae/spacer.gif" alt="spacer" width="25" height="1" /></td>
          <td width="417" align="left" valign="top"><p class="style1"><span class="style2"><xsl:apply-templates select="GURUS/GURU[@NAME='Alan Halecky']"/></span> <br />
     Areas of expertise: <xsl:apply-templates select="DISCIPLINES/DISCIPLINE[@NAME='chemical']/GURU[@NAME='Alan Halecky']/SEARCHES"/>
     </p></td>
          <td width="25" align="left" valign="top"><img src="/engresources/images/ae/spacer.gif" alt="spacer" width="25" height="1" /></td>
          <td width="394" align="left" valign="top"><span class="style1"><span class="style2">Sample question:  &nbsp; &nbsp;</span> <br />
                <br />
            </span>
              <p class="style1">&ldquo;How can wet carbon dioxide be responsible for a corrosion problem in iron-containing metals?&rdquo; </p>
            <p align="center"><a href="javascript:emailGuruFormat('$SESSIONID','one','Layer12','Alan Halecky');"><img src="/engresources/images/ae/email.gif" alt="email" width="124" height="14" border="0"/></a></p></td>
        </tr>
      </table>
      <p><br />
      </p>
    </div>

</div>

<!-- Ask a Product Specialist -->
<!-- Ask a Product Specialist -->
<!-- Ask a Product Specialist -->

<div id="two" style="display: none; text-align:left;">
  <p align="left">&nbsp;</p>
  <p align="left" class="style1">Our Product Specialist can help you with search assistance in Engineering Village, training needs and questions regarding features and results. </p>
  <p align="left" class="style1">In addition, our Product Specialist hosts regular weekly trainings on multiple Engineering Village databases, features and functionality and creates training materials and product tours to support your research.</p>
  <div align="left">
    <table width="972" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td width="429" align="left" valign="top"><a target="_blank" href="http://www.ei.org/support/product_tours.html"><img src="/engresources/images/ae/New_Specialist.gif" alt="Ask a Product Specialist" width="429" height="356" border="0" id="Image1" onmouseover="pro_MM_swapImage('Image1','','/engresources/images/ae/New_Specialist_over.gif',0)" onmouseout="pro_MM_swapImgRestore()" /></a></td>
        <td width="25" align="left" valign="top"><img src="/engresources/images/ae/spacer.gif" alt="spacer" width="25" height="1" /></td>
        <td width="518" align="left" valign="middle"><span class="style2">Sample questions:</span><br />
          <ol>
            <li class="style1">&ldquo;How often do patents to appear on Engineering Village after issuance?&rdquo;</li>
            <li class="style1">&ldquo;How can I use Engineering Village to track competitors?&rdquo;</li>
            <li class="style1">&ldquo;How would I complete a search for business models and templates?&rdquo;</li>
            <li class="style1">&ldquo;Is it possible to get personalized trainings?&rdquo;</li>
          </ol>
          <p align="center"><a target="_blank" href="http://www.ei.org/support/online_seminars.html"><img src="/engresources/images/ae/training.gif" alt="email" width="167" height="14" border="0" /></a></p>
          <p align="center"><a href="javascript:emailFormat('$SESSIONID','two');"><img src="/engresources/images/ae/email_specialist.gif" alt="email_specalist" width="167" height="14" border="0"/></a></p></td>
      </tr>
    </table>
  </div>
  <p align="left" class="style4"><br />
  <a name="faq" id="faq">FAQ</a></p>
  <div align="left">
    <ol class="styleunderlined">
      <li class="styleunderlined"><a href="#1">When is full text available?</a></li>
      <li class="styleunderlined"><a href="#2">How can I obtain a full text document is it is not available to me?</a></li>
      <li class="styleunderlined"><a href="#3">What are &ldquo;facets&rdquo; and how do they help my search?</a></li>
      <li class="styleunderlined"><a href="#4">Which groups of results data (&ldquo;facets&rdquo;) are available to me?</a></li>
      <li class="styleunderlined"><a href="#5">Why would I export the grouped result data (&ldquo;facets&rdquo;) into a chart and/or spreadsheet?</a></li>
      <li class="styleunderlined"><a href="#6">How often is content updated on Engineering Village?</a></li>
      <li class="styleunderlined"><a href="#7">How can I manage my results and save my searches?</a></li>
      <li class="styleunderlined"><a href="#8">What is the benefit of email alerts and RSS</a></li>
      <li class="styleunderlined"><a href="#9">Why would I blog a record?</a></li>
      <li class="styleunderlined"><a href="#10">What are tags and how can I use them?</a></li>
      <li class="styleunderlined"><a href="#11">What does it mean to &ldquo;Browse Indexes&rdquo;?</a></li>
      <li class="styleunderlined"><a href="#12">How can the thesaurus help me when searching for content?</a></li>
    </ol>
  </div>
  <p align="left" class="style5">1. When is full text available?<a name="1" id="1"></a></p>
  <p align="left" class="style1">Availability of full-text varies depending on which databases you search, the content to which your company or institution subscribes, and what is available for free on the web.<br /><br />
  Abstract and Index (A&amp;I) databases such as Compendex, Inspec, GeoBase, and others consist of abstract records, not full-text articles. Full-text links on Engineering Village bring you from bibliographic information in A&amp;I databases to publishers&rsquo; sites where, if the article is available for free or if you have subscription access to a particular journal through your library, you will automatically get the full-text. If you do not have access, the links will take you to the publishers&rsquo; site where you can purchase that article. Records in Engineering Village may not include a full-text link if the article is available only in print, if the publisher has provided inconsistent metadata, or if the publisher does not submit their publications to the <a target="_blank" href="http://www.crossref.org">CrossRef</a> linking system.<br /><br />
  In addition, Engineering Village is compatible with all major link resolvers, including SFX, which help customers manage entitlement and appropriate copies. This ensures that users are directed to the version of the article they have access to through their libraries or institutions.<br /><br />
  Full-text is included for all electronic books in the Referex database, as well as for patent documents in the Ei Patents database.</p>
  <a class="style1" href="#faq">back</a>

  <p align="left" class="style5">2. How can I obtain a full text document is it is not available to me?<a name="2" id="2"></a></p>
  <p align="left" class="style1">Full-text links in Engineering Village will take you to the publishers&rsquo; site where you can purchase that article if you do not have access to it through a library subscription.<br /><br />
  Records in Engineering Village may not include a full-text link if the article is available only in print, if the publisher has provided inconsistent metadata, or if the publisher does not submit their publications to the CrossRef linking system. In this case, you can search for the full-text in your library&rsquo;s print holdings, purchase full-text through a document delivery service such as the Linda Hall Library, or ask your librarian about purchasing the full-text or procuring through an inter-library loan.</p>
  <a class="style1" href="#faq">back</a>

  <p align="left" class="style5">3. What are &ldquo;facets&rdquo; and how do they help my search?<a name="3" id="3"></a></p>
  <p align="left" class="style1">&ldquo;Facets&rdquo; appear in the Refine Results box on the right side of the search results screen.<br /><br />
  Facets represent commonly occurring results data grouped by field. Under each facet is a list of the terms which appear most often in that set of results, along with a record count showing the number of records containing that term. Each facet displays the first 10 items that are relevant for your search, but you can click on &ldquo;more&#0133;&rdquo; to show additional terms.<br /><br />
  To refine or alter your original search: Select one or more search terms and click on &ldquo;include&rdquo; or &ldquo;exclude.&rdquo; Including terms is similar to adding an AND term to your original search, so will limit results to only those containing that term. Excluding terms is similar to adding a NOT to your search terms, so it will exclude any records containing that term. The &ldquo;Add a Term&rdquo; option allows you to enter your own terms for search refinement and can be combined with other terms in the faceted list.</p>
  <a class="style1" href="#faq">back</a>

  <p align="left" class="style5">4. Which groups of results data (&ldquo;facets&rdquo;) are available to me?<a name="4" id="4"></a></p>
  <p align="left" class="style1">You will see the &ldquo;facets&rdquo; (grouped results data from index fields such as author, publication, year, controlled vocabulary) common to all databases you&rsquo;re searching.  Searching multiple databases at the same time results in only the facets that those databases share being displayed.  The Year facet and Add a Term option will always appear.</p>
  <a class="style1" href="#faq">back</a>

  <p align="left" class="style5">5. Why would I export the grouped result data (&ldquo;facets&rdquo;) into a chart and/or spreadsheet?<a name="5" id="5"></a></p>
  <p align="left" class="style1">Bar Graph and Export icons appear next to each facet in the Refine Results section of the search results screen.  The Bar Chart can be helpful as it shows a visual representation of the faceted terms and their distribution.  Exporting the faceted data gives you a record of the data for analysis and provides an overview of results from which you can build on your search. Having access to the raw data also gives you the freedom to graph it, report on it, or reference it in whatever way you need.<br /><br />
  Click on the Export icon to save or open the terms and record counts in that facet as a tab file, which allows you to select the software you would like to use to open the file (e.g. Excel). All the terms in a facet are exported, even when only the top 10 are displayed on Engineering Village.<br /><br />
  Click on the bar graph icon to open a graph of the terms and record counts in a new window. The bar graph will show only the terms being viewed in Engineering Village at that time. For example, by default it will graph the top 10 terms but if you click on &ldquo;more&#0133;&rdquo; to show additional terms, all of those terms displayed will be included on the graph as well. You can then save, print, or email this image.</p>
  <a class="style1" href="#faq">back</a>

  <p align="left" class="style5">6. How often is content updated on Engineering Village?<a name="6" id="6"></a></p>
  <p align="left" class="style1">All of the A&amp;I and patent databases are updated weekly.  New eBook titles are added to the Referex database annually.</p>
  <a class="style1" href="#faq">back</a>

  <p align="left" class="style5">7. How can I manage my results and save my searches?<a name="7" id="7"></a></p>
  <p align="left" class="style1">Managing your results is easy.  From the search results screen, you can select records to view, email, print, download, or save using the &ldquo;Results Manager&rdquo; box at the top of the screen. You can also save searches to rerun later simply by clicking the &ldquo;Save Search&rdquo; link directly above the list of results.<br /><br />
  In order to save your searches or download specific records to a folder, you must create a personal account. You will be prompted to set up an account automatically when you attempt to use one of these features.</p>
  <a class="style1" href="#faq">back</a>

  <p align="left" class="style5">8. What is the benefit of email alerts and RSS?<a name="8" id="8"></a></p>
  <p align="left" class="style1">Weekly alerts of new records that fit your search criteria keep you up to date with recently added content, without having to rerun the same searches over and over again.  Engineering Village gives you automatic weekly updates of your search queries&rsquo; results in whichever format you prefer: emailed directly to your inbox via email alerts or delivered straight to your RSS reader.<br /><br />
  In order to set up email or RSS alerts, you must create a personal account. You will be prompted to set up an account automatically when you attempt to use one of these features.</p>
  <a class="style1" href="#faq">back</a>

  <p align="left" class="style5">9. Why would I blog a record?<a name="9" id="9"></a></p>
  <p align="left" class="style1">The &ldquo;Blog This&rdquo; feature allows you to link from your blog to a record of interest on Engineering Village.   Anyone can view a record following the Blog This link even if they do not subscribe to Engineering Village, so you can share information with an audience outside of your organization. This can help you communicate with your blog readers and allows you to create a forum from which to collaborate with colleagues in your area of research.<br /><br />
  To use this feature simply click the &ldquo;Blog This&rdquo; button in an abstract or detailed record view.  Select and copy the code that appears in the box and paste it in your blog post.  Once you publish your post the title of the record will appear in your blog.</p>
  <a class="style1" href="#faq">back</a>

  <p align="left" class="style5">10. What are tags and how can I use them?<a name="10" id="10"></a></p>
  <p align="left" class="style1">Tags are keywords applied to a record by an Engineering Village user. This allows you to assign meaningful terms either for your own reference and organization or to share with a group, your institution, or the entire Engineering Village community.  You can also search other users&rsquo; tags to gain new perspectives. <br /><br />
  Engineering Village also lets you tag records externally to del.icio.us or 2collab. 2collab is an online community specifically designed for scientific researchers where you can tag and share records across Elsevier products, including Engineering Village, Scopus, and ScienceDirect. </p>
  <a class="style1" href="#faq">back</a>

  <p align="left" class="style5">11. What does it mean to &quot;Browse Indexes&quot;?<a name="11" id="11"></a></p>
  <p align="left" class="style1">Indexing is the process by which bibliographic information and thesaurus terms, or controlled vocabulary, are selected to describe the content of an article. On Engineering Village, indexing determines articles&rsquo; relevance to a search query and links related articles together. The index comprises several fields that vary by database (e.g. author, author affiliation, controlled terms, etc.).<br /><br />
  Browse indexes to find the precise term you need, or to ensure all variations of a term (i.e. spelling or abbreviations) are included in the search so nothing is missed. <br /><br />
  From the Quick and Expert Search screens on Engineering Village you can select the index you wish to search in the &ldquo;Browse Indexes&rdquo; box. The index will appear in a new window, and you can either browse index terms alphabetically or search for a term by typing the first few letters in the SEARCH FOR box and clicking on &ldquo;Find&rdquo;.  Selecting terms from the index will automatically add them to your search.</p>
  <a class="style1" href="#faq">back</a>

  <p align="left" class="style5">12. How can the thesaurus help me when searching for content?<a name="12" id="12"></a></p>
  <p align="left" class="style1">Thesauri are guides to the controlled vocabulary used to index articles for Engineering Village.   Thesauri are hierarchical in nature, organizing terms by broader, narrower, or related concepts.  <br /><br />
  The &ldquo;Thesaurus Search&rdquo; tab on Engineering Village lets you search or browse the thesaurus for additional related terms. From here, you can expand or refine your search by selecting broader, narrower, or related terms to include. This offers insight into additional topics or terms that were not initially included, ensuring that nothing is missed and that search results provide precisely the information you need.</p>
  <a class="style1" href="#faq">back</a>
  <p align="left">&nbsp;</p>
</div>

<!-- Ask a Librarian -->
<!-- Ask a Librarian -->
<!-- Ask a Librarian -->

<div id="three" style="display: none; text-align:left;">
  <p align="left">&nbsp;</p>
  <p align="left" class="style1">A librarian can help you formulate a search, identify and locate a source for a book or an article, or find additional resources on a specific topic.</p>
  <p align="left" class="style1">To learn more about the subject areas that are covered by the databases that are available to you, please visit <a target="_blank" href="http://www.ei.org/products/engineeringvillage.html ">http://www.ei.org/products/engineeringvillage.html </a></p>

  <div align="left">
    <table width="972" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td width="429" align="left" valign="top"><a target="_blank" href="http://www.ei.org/support/product_tours.html"><img src="/engresources/images/ae/New_Specialist.gif" alt="Ask a Librarian" width="429" height="356" border="0" id="lib_Image1" onmouseover="lib_MM_swapImage('lib_Image1','','/engresources/images/ae/New_Specialist_over.gif',0)" onmouseout="lib_MM_swapImgRestore()" /></a></td>
        <td width="25" align="left" valign="top"><img src="/engresources/images/ae/spacer.gif" alt="spacer" width="25" height="1" /></td>
        <td width="518" align="left" valign="middle"><span class="style2">Sample questions:</span><br />
          <ol>
            <li class="style1">&ldquo;Can you help me find the Journal of Constructional Steel Research?&rdquo;</li>
            <li class="style1">&ldquo;I&rsquo;d like to do a detailed search on coercive force and material composition but I need assistance.&rdquo;</li>
          </ol>
          <p align="center">
            <xsl:apply-templates select="ASKALIBRARIAN"/>
          </p>
        </td>
      </tr>
    </table>
  </div>
  <p align="left">&nbsp;</p>
</div>


  <!-- end of the lower area below the navigation bar -->
  <xsl:apply-templates select="FOOTER">
  		<xsl:with-param name="SESSION-ID" select="$SESSION-ID"/>
  		<xsl:with-param name="SELECTED-DB" select="$DATABASE"/>
  </xsl:apply-templates>

</center>

<script type="text/javascript">
var gaJsHost = (("https:" == document.location.protocol) ? "https://ssl." : "http://www.");
document.write(unescape("%3Cscript src='" + gaJsHost + "google-analytics.com/ga.js' type='text/javascript'%3E%3C/script%3E"));
</script>
<script type="text/javascript">
try {
var pageTracker = _gat._getTracker("UA-6113832-3");
pageTracker._trackPageview();
} catch(err) {}</script>
      </body>
</html>

</xsl:template>

<xsl:template match="REFLINK">
    <a target="_blank">
      <xsl:attribute name="href"><xsl:value-of select="."/></xsl:attribute>
      <img src="/engresources/images/ae/Contact_your_librarian.gif" alt="Contact your Librarian" width="156" height="14" border="0"/></a>
</xsl:template>
<xsl:template match="REFEMAIL">
    <a href="javascript:emailFormat('$SESSIONID','three');"><img src="/engresources/images/ae/email_Librarian.gif" alt="Email a Librarian" width="124" height="14" border="0"/></a>
</xsl:template>


<!-- template for author name links -->
<xsl:template match="GURU">
  <a>
    <xsl:if test="GURU_SEARCHLINK != ''">
      <xsl:attribute name="href"><xsl:value-of select="GURU_SEARCHLINK"/></xsl:attribute>
    </xsl:if>
    <xsl:value-of select="@NAME"/>
  </a>
  <xsl:if test="not(position()=last())">, </xsl:if>
</xsl:template>

<!-- template for author areas of expertise links -->
<xsl:template match="SEARCH">
  <a>
    <xsl:attribute name="href"><xsl:value-of select="."/></xsl:attribute>
    <xsl:value-of select="@NAME"/>
  </a>
  <xsl:if test="not(position()=last())">, </xsl:if>
</xsl:template>

</xsl:stylesheet>