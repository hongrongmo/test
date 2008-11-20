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
    var pageTitles = new Array();
    pageTitles['zero'] = 'Home';
    pageTitles['one'] = 'Ask an Expert';
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
          pageTracker._trackPageview('/folder/' + pageTitles[divname]);
        }
        else {
          atoggleDiv.style.display = "none";
        }
      }
    }

    function emailFormat(sessionid,section)
    {
      var url = "/controller/servlet/Controller?EISESSION="+sessionid+"&CID=askanexpert&database=1&section="+escape(pageTitles[section]);
      new_window=window.open(url,'NewWindow','status=yes,resizable,scrollbars,width=600,height=400');
      new_window.focus();
    }

    var layerTitles = new Array();
    layerTitles['Layer0'] = 'zero';
    layerTitles['Layer1'] = 'Chemical';
    layerTitles['Layer2'] = 'Industrial';
    layerTitles['Layer3'] = 'Mechanical';
    layerTitles['Layer4'] = 'Electrical';
    layerTitles['Layer5'] = 'Signal Processing';
    layerTitles['Layer6'] = 'Maufacturing';
    layerTitles['Layer7'] = 'Materials';
    layerTitles['Layer8'] = 'Engineering Management';
    layerTitles['Layer9'] = 'Computer';
    function toggleDivLayers(divname)
    {
      var allIds = ['Layer1','Layer2','Layer3','Layer4','Layer5','Layer6','Layer7','Layer8','Layer9'];

      for(var i in allIds)
      {
        var thisId = allIds[i];
        var atoggleDiv = document.getElementById(thisId);

        if(thisId == divname) {
          atoggleDiv.style.display = "block";
          pageTracker._trackPageview('/folder/' + layerTitles[divname]);
        }
        else {
          atoggleDiv.style.display = "none";
        }
      }
    }

		]]>
    // </xsl:comment>
    </script>
<style type="text/css">
    <xsl:comment>
		<![CDATA[
.style1 {
	font-family: Verdana, Arial, Helvetica, sans-serif;
	font-size: 12px;
}
.style2 {
	font-size: 14px;
	font-weight: bold;
}

#two {
	width:972px;
	height:2231px;
	z-index:1;
}
.pro_style1 {
	font-family: Verdana, Arial, Helvetica, sans-serif;
	font-size: 12px;

	}
.pro_styleunderlined {
	font-family: Verdana, Arial, Helvetica, sans-serif;
	font-size: 12px;

}
.pro_style2 {
	font-size: 14px;
	font-weight: bold;
}
.pro_style4 {font-size: 14px; font-weight: bold; font-family: Verdana, Arial, Helvetica, sans-serif; }
.pro_style5 {font-family: Verdana, Arial, Helvetica, sans-serif; font-size: 12px; font-weight: bold; }

		]]>
    // </xsl:comment>
</style>
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
    <td width="238"><a href="javascript:;" target="_top" onclick="MM_nbGroup('down','group1','Ask_an_Engineer','/engresources/images/ae/images/ask_engineer.gif',0);toggleDiv('one');" onmouseover="MM_nbGroup('over','Ask_an_Engineer','/engresources/images/ae/images/ask_engineer_over.gif','',0)" onmouseout="toggleDivLayers('zero');MM_nbGroup('out')"><img src="/engresources/images/ae/images/ask_engineer.gif" alt="" name="Ask_an_Engineer" width="238" height="339" border="0" id="Ask_an_Engineer" onload="MM_nbGroup('init','group1','Ask_an_Engineer','/engresources/images/ae/images/ask_engineer_down.gif',0)" /></a></td>
    <td width="25"><img src="/engresources/images/ae/images/spacer.gif" alt="spacer" width="25" height="1" /></td>
    <td width="238"><a href="javascript:;" target="_top" onclick="MM_nbGroup('down','group1','Ask_Product_Specialist','/engresources/images/ae/images/ask_specialist.gif',0);toggleDiv('two');" onmouseover="MM_nbGroup('over','Ask_Product_Specialist','/engresources/images/ae/images/ask_specialist_over.gif','',0)" onmouseout="MM_nbGroup('out')"><img src="/engresources/images/ae/images/ask_specialist.gif" alt="" name="Ask_Product_Specialist" width="238" height="339" border="0" id="Ask_Product_Specialist" onload="MM_nbGroup('init','group1','Ask_Product_Specialist','/engresources/images/ae/images/ask_specialist_down.gif',0)" /></a></td>
    <td width="25"><img src="/engresources/images/ae/images/spacer.gif" alt="spacer" width="25" height="1" /></td>
    <td width="238"><a href="javascript:;" target="_top" onclick="MM_nbGroup('down','group1','Ask_a_Librarian','/engresources/images/ae/images/ask_librarian.gif',0);toggleDiv('three');" onmouseover="MM_nbGroup('over','Ask_a_Librarian','/engresources/images/ae/images/ask_librarian_over.gif','',0)" onmouseout="MM_nbGroup('out')"><img src="/engresources/images/ae/images/ask_librarian.gif" alt="" name="Ask_a_Librarian" border="0" id="Ask_a_Librarian" onload="MM_nbGroup('init','group1','Ask_a_Librarian','/engresources/images/ae/images/ask_librarian_down.gif',0)" /></a></td>
  </tr>
</table>

<div id="zero" style="display: block;">
  <h1>&#160;</h1>
</div>


<div id="one" style="display: none;">

  <table border="0" cellpadding="0" cellspacing="0">
    <tr>
      <td><a href="javascript:;" target="_top" onclick="toggleDivLayers('Layer1'); MM_nbGroup('down','group1','Chemical','/engresources/images/ae/images/Chemical.gif',0)" onmouseover="MM_nbGroup('over','Chemical','/engresources/images/ae/images/Chemical_over.gif','',0)" onmouseout="MM_nbGroup('out')"><img src="/engresources/images/ae/images/Chemical.gif" alt="Chemical" name="Chemical" width="108" height="128" border="0" id="Chemical" onload="MM_nbGroup('init','group1','Chemical','/engresources/images/ae/images/Chemical_down.gif',0)" /></a></td>
      <td><a href="javascript:;" target="_top" onclick="toggleDivLayers('Layer2'); MM_nbGroup('down','group1','Industrial','/engresources/images/ae/images/Idustrial.gif',0)" onmouseover="MM_nbGroup('over','Industrial','/engresources/images/ae/images/Idustrial_over.gif','',0)" onmouseout="MM_nbGroup('out')"><img src="/engresources/images/ae/images/Idustrial.gif" alt="industrial" name="Industrial" width="108" height="128" border="0" id="Industrial" onload="MM_nbGroup('init','group1','Industrial','/engresources/images/ae/images/Idustrial_down.gif',0)" /></a></td>
      <td><a href="javascript:;" target="_top" onclick="toggleDivLayers('Layer3'); MM_nbGroup('down','group1','Mechanical','/engresources/images/ae/images/Mechanical.gif',0)" onmouseover="MM_nbGroup('over','Mechanical','/engresources/images/ae/images/Mechanical_over.gif','',0)" onmouseout="MM_nbGroup('out')"><img src="/engresources/images/ae/images/Mechanical.gif" alt="mecanical" name="Mechanical" width="108" height="128" border="0" id="Mechanical" onload="MM_nbGroup('init','group1','Mechanical','/engresources/images/ae/images/Mechanical_down.gif',0)" /></a></td>
      <td><a href="javascript:;" target="_top" onclick="toggleDivLayers('Layer4'); MM_nbGroup('down','group1','Electrical','/engresources/images/ae/images/Electrical.gif',0)" onmouseover="MM_nbGroup('over','Electrical','/engresources/images/ae/images/Electrical_over.gif','',0)" onmouseout="MM_nbGroup('out')"><img src="/engresources/images/ae/images/Electrical.gif" alt="Electrical" name="Electrical" width="108" height="128" border="0" id="Electrical" onload="MM_nbGroup('init','group1','Electrical','/engresources/images/ae/images/Electrical_down.gif',0)" /></a></td>
      <td><a href="javascript:;" target="_top" onclick="toggleDivLayers('Layer5'); MM_nbGroup('down','group1','SignalProcessing','/engresources/images/ae/images/SignalProcessing.gif',0)" onmouseover="MM_nbGroup('over','SignalProcessing','/engresources/images/ae/images/SignalProcessing_over.gif','',0)" onmouseout="MM_nbGroup('out')"><img src="/engresources/images/ae/images/SignalProcessing.gif" alt="signal" name="SignalProcessing" width="108" height="128" border="0" id="SignalProcessing" onload="MM_nbGroup('init','group1','SignalProcessing','/engresources/images/ae/images/SignalProcessing_down.gif',0)" /></a></td>
      <td><a href="javascript:;" target="_top" onclick="toggleDivLayers('Layer6'); MM_nbGroup('down','group1','Manufacturing','/engresources/images/ae/images/Manufacturing.gif',0)" onmouseover="MM_nbGroup('over','Manufacturing','/engresources/images/ae/images/Manufacturing_over.gif','',0)" onmouseout="MM_nbGroup('out')"><img src="/engresources/images/ae/images/Manufacturing.gif" alt="manufacturing" name="Manufacturing" width="108" height="128" border="0" id="Manufacturing" onload="MM_nbGroup('init','group1','Manufacturing','/engresources/images/ae/images/Manufacturing_down.gif',0)" /></a></td>
      <td><a href="javascript:;" target="_top" onclick="toggleDivLayers('Layer7'); MM_nbGroup('down','group1','Materials','/engresources/images/ae/images/Materials.gif',0)" onmouseover="MM_nbGroup('over','Materials','/engresources/images/ae/images/Materials_over.gif','',0)" onmouseout="MM_nbGroup('out')"><img src="/engresources/images/ae/images/Materials.gif" alt="matiterials" name="Materials" width="108" height="128" border="0" id="Materials" onload="MM_nbGroup('init','group1','Materials','/engresources/images/ae/images/Materials_down.gif',0)" /></a></td>
      <td><a href="javascript:;" target="_top" onclick="toggleDivLayers('Layer8'); MM_nbGroup('down','group1','Management','/engresources/images/ae/images/Management-.gif',0)" onmouseover="MM_nbGroup('over','Management','/engresources/images/ae/images/Management_over.gif','',0)" onmouseout="MM_nbGroup('out')"><img src="/engresources/images/ae/images/Management-.gif" alt="managment" name="Management" width="108" height="128" border="0" id="Management" onload="MM_nbGroup('init','group1','Management','/engresources/images/ae/images/Management_down.gif',0)" /></a></td>
      <td><a href="javascript:;" target="_top" onclick="toggleDivLayers('Layer9'); MM_nbGroup('down','group1','Computer','/engresources/images/ae/images/Computer-.gif',0)" onmouseover="MM_nbGroup('over','Computer','/engresources/images/ae/images/Computer_over.gif','',0)" onmouseout="MM_nbGroup('out')"><img src="/engresources/images/ae/images/Computer-.gif" alt="computer" name="Computer" width="108" height="128" border="0" id="Computer" onload="MM_nbGroup('init','group1','Computer','/engresources/images/ae/images/Computer_down.gif',0)" /></a></td>
    </tr>
  </table>

  <div id="Layer1" style="display: none;">
    <p><span class="style1">Our Senior Engineers will draw on their professional knowledge and experience to answer your technical engineering questions.&nbsp; They can also point you to the appropriate companies, consultants, research institutes, web sites, and other resources that can help you solve your problem.</span></p>

    <table width="972" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td width="111" align="left" valign="top"><img src="/engresources/images/ae/images/Mechanical.gif" alt="Mechanical" width="108" height="128" /></td>
        <td width="25"><img src="/engresources/images/ae/images/spacer.gif" alt="spacer" width="25" height="1" /></td>
        <td width="417" align="left" valign="top"><p class="style1"><span class="style2">Ryo Samuel Amano&nbsp; &nbsp;</span> <br />
            Fluid and thermodynamics professor, Department of Mechanical Engineering, University of Wisconsin-Milwaukee<br />
        </p>
        <p class="style1">Areas of expertise: aerodynamics, gas/steam turbines, fluid mechanics/fluid dynamics, rocket/propulsion systems, heat transfer/heat exchangers/engines</p></td>
        <td width="25" align="left" valign="top"><img src="/engresources/images/ae/images/spacer.gif" alt="spacer" width="25" height="1" /></td>
        <td width="394" align="left" valign="top"><span class="style1"><span class="style2">Sample questions:  &nbsp; &nbsp;</span> <br />
            <br /></span>
          <ol>
            <li class="style1">&ldquo;Is it possible to give heat treatment to copper?&rdquo; </li>
            <li class="style1">&ldquo;How can wet carbon dioxide be responsible for a corrosion problem in iron-containing metals?&rdquo;</li>
            <li class="style1">&ldquo;What type of steel rod/axle&nbsp;would you use for one way roller bearings.&rdquo;</li>
          </ol>
          <p align="center"><img src="/engresources/images/ae/images/email.gif" alt="email" width="124" height="14" /></p></td>
      </tr>
    </table>
    <p><br />
    </p>
  </div>

  <div id="Layer2" style="display: none;">
    <p><span class="style1">Our Senior Engineers will draw on their professional knowledge and experience to answer your technical engineering questions.&nbsp; They can also point you to the appropriate companies, consultants, research institutes, web sites, and other resources that can help you solve your problem.</span></p>

    <table width="972" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td width="111" align="left" valign="top"><img src="/engresources/images/ae/images/Manufacturing.gif" alt="chem" width="108" height="128" /></td>
        <td width="25"><img src="/engresources/images/ae/images/spacer.gif" alt="spacer" width="25" height="1" /></td>
        <td width="417" align="left" valign="top"><p class="style1"><span class="style2">Robert D. Borchelt &nbsp; &nbsp;</span> <br />
          Technical advisor for assembly and automation in the Corporate Operations Analysis Group, Cummins Engine Company
  </p>
          <p class="style1">Areas of expertise: computer integrated manufacturing, automation and advanced manufacturing systems, artificial intelligence applications in robotics and manufacturing, automated diagnosis and error recovery, hybrid knowledge-based systems, programmable logic controller applications, physical modeling of manufacturing systems, engineering management issues related to manufacturing, manufacturing processes, systems integration, expert systems </p></td>
        <td width="25" align="left" valign="top"><img src="/engresources/images/ae/images/spacer.gif" alt="spacer" width="25" height="1" /></td>
        <td width="394" align="left" valign="top"><span class="style1"><span class="style2">Sample questions:  &nbsp; &nbsp;</span> <br />
              <br />
          </span>
            <ol>
              <li class="style1">&ldquo;Is it possible to give heat treatment to copper?&rdquo; </li>
              <li class="style1">&ldquo;How can wet carbon dioxide be responsible for a corrosion problem in iron-containing metals?&rdquo;</li>
              <li class="style1">&ldquo;What type of steel rod/axle&nbsp;would you use for one way roller bearings.&rdquo;</li>
            </ol>
          <p align="center"><img src="/engresources/images/ae/images/email.gif" alt="email" width="124" height="14" /></p></td>
      </tr>
    </table>
    <p><br />
    </p>
  </div>

  <div id="Layer3" style="display: none;">
    <p><span class="style1">Our Senior Engineers will draw on their professional knowledge and experience to answer your technical engineering questions.&nbsp; They can also point you to the appropriate companies, consultants, research institutes, web sites, and other resources that can help you solve your problem.</span></p>

    <table width="972" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td width="111" align="left" valign="top"><img src="/engresources/images/ae/images/SignalProcessing.gif" alt="chem" width="108" height="128" /></td>
        <td width="25"><img src="/engresources/images/ae/images/spacer.gif" alt="spacer" width="25" height="1" /></td>
        <td width="417" align="left" valign="top"><p class="style1"><span class="style2">Chi Hau Chen    &nbsp; &nbsp;</span> <br />
          Signal processing professor, Electrical and Computer Engineering, University of Massachusetts</p>
            <p class="style1">Areas of expertise: pattern recognition, signal/image processing and neural networks, applications to ultrasonic/nondestructive evaluation (NDE), sonar, radar, and seismic problems, machine vision, artificial intelligence, time series analysis, wavelet analysis</p></td>
        <td width="25" align="left" valign="top"><img src="/engresources/images/ae/images/spacer.gif" alt="spacer" width="25" height="1" /></td>
        <td width="394" align="left" valign="top"><span class="style1"><span class="style2">Sample questions:  &nbsp; &nbsp;</span> <br />
              <br />
          </span>
            <ol>
              <li class="style1">&ldquo;Is it possible to give heat treatment to copper?&rdquo; </li>
              <li class="style1">&ldquo;How can wet carbon dioxide be responsible for a corrosion problem in iron-containing metals?&rdquo;</li>
              <li class="style1">&ldquo;What type of steel rod/axle&nbsp;would you use for one way roller bearings.&rdquo;</li>
            </ol>
          <p align="center"><img src="/engresources/images/ae/images/email.gif" alt="email" width="124" height="14" /></p></td>
      </tr>
    </table>
    <p><br />
    </p>
  </div>


  <div id="Layer4" style="display: none;">
    <p><span class="style1">Our Senior Engineers will draw on their professional knowledge and experience to answer your technical engineering questions.&nbsp; They can also point you to the appropriate companies, consultants, research institutes, web sites, and other resources that can help you solve your problem.</span></p>

    <table width="972" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td width="111" align="left" valign="top"><img src="/engresources/images/ae/images/Management-.gif" alt="chem" width="108" height="128" /></td>
        <td width="25"><img src="/engresources/images/ae/images/spacer.gif" alt="spacer" width="25" height="1" /></td>
        <td width="417" align="left" valign="top"><p class="style1"><span class="style2">Donald W. Merino, Jr.    &nbsp; &nbsp;</span> <br />
          Areas of expertise: production operations, process flow/optimization, process simulation, capital allocation, cost models, concurrent engineering, SQC/SPC (Statistical Quality Control/Statistical Process Control) and applied statistics, TQM philosophy, benchmarking, DOE (Design of Experiments), QFD (Quality Function Deployment), cost of quality, economic/non-economic decision making, optimization</p></td>
        <td width="25" align="left" valign="top"><img src="/engresources/images/ae/images/spacer.gif" alt="spacer" width="25" height="1" /></td>
        <td width="394" align="left" valign="top"><span class="style1"><span class="style2">Sample questions:  &nbsp; &nbsp;</span> <br />
              <br />
          </span>
            <ol>
              <li class="style1">&ldquo;Is it possible to give heat treatment to copper?&rdquo; </li>
              <li class="style1">&ldquo;How can wet carbon dioxide be responsible for a corrosion problem in iron-containing metals?&rdquo;</li>
              <li class="style1">&ldquo;What type of steel rod/axle&nbsp;would you use for one way roller bearings.&rdquo;</li>
            </ol>
          <p align="center"><img src="/engresources/images/ae/images/email.gif" alt="email" width="124" height="14" /></p></td>
      </tr>
    </table>
    <p><br />
    </p>
  </div>

  <div id="Layer5" style="display: none;">
    <p><span class="style1">Our Senior Engineers will draw on their professional knowledge and experience to answer your technical engineering questions.&nbsp; They can also point you to the appropriate companies, consultants, research institutes, web sites, and other resources that can help you solve your problem.</span></p>

    <table width="972" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td width="111" align="left" valign="top"><img src="/engresources/images/ae/images/Mechanical.gif" alt="Mechanical" width="108" height="128" /></td>
        <td width="25"><img src="/engresources/images/ae/images/spacer.gif" alt="spacer" width="25" height="1" /></td>
        <td width="417" align="left" valign="top"><p class="style1"><span class="style2">Ronald A. Perez</span> <br />
          Areas of expertise: production operations, process flow/optimization, process simulation, capital allocation, cost models, concurrent engineering, SQC/SPC (Statistical Quality Control/Statistical Process Control) and applied statistics, TQM philosophy, benchmarking, DOE (Design of Experiments), QFD (Quality Function Deployment), cost of quality, economic/non-economic decision making, optimization</p>
        </td>
        <td width="25" align="left" valign="top"><img src="/engresources/images/ae/images/spacer.gif" alt="spacer" width="25" height="1" /></td>
        <td width="394" align="left" valign="top"><span class="style1"><span class="style2">Sample questions:  &nbsp; &nbsp;</span> <br />
              <br />
          </span>
            <ol>
              <li class="style1">&ldquo;Is it possible to give heat treatment to copper?&rdquo; </li>
              <li class="style1">&ldquo;How can wet carbon dioxide be responsible for a corrosion problem in iron-containing metals?&rdquo;</li>
              <li class="style1">&ldquo;What type of steel rod/axle&nbsp;would you use for one way roller bearings.&rdquo;</li>
            </ol>
          <p align="center"><img src="/engresources/images/ae/images/email.gif" alt="email" width="124" height="14" /></p></td>
      </tr>
    </table>
    <p><br />
    </p>
  </div>

  <div id="Layer6" style="display: none;">
    <p><span class="style1">Our Senior Engineers will draw on their professional knowledge and experience to answer your technical engineering questions.&nbsp; They can also point you to the appropriate companies, consultants, research institutes, web sites, and other resources that can help you solve your problem.</span></p>

    <table width="972" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td width="111" align="left" valign="top"><img src="/engresources/images/ae/images/Electrical.gif" alt="chem" width="108" height="128" /></td>
        <td width="25"><img src="/engresources/images/ae/images/spacer.gif" alt="spacer" width="25" height="1" /></td>
        <td width="417" align="left" valign="top"><p class="style1"><span class="style2">Kanti Prasad     &nbsp; &nbsp;</span> <br />Professor, electrical engineering, and director, Microelectronics/VLSI Technology, University of Massachusetts</p>
          <p class="style1">Areas of expertise: VLSI chip design incorporating microprocessors, controls, communications, intelligent transportation systems, GPS, computer networks, LANs, including wireless LANs, VLSI fabrication incorporating microelectronics processing such as lithography oxidation, diffusion, implantation, and metallization for silicon as well as GaAs technology including packing and reliability analysis, digital and analog design employing VHDL incorporating test and simulation I entirety, i.e., exhaustive testing and simulation with statistically fast and event driven simulators </p></td>
        <td width="25" align="left" valign="top"><img src="/engresources/images/ae/images/spacer.gif" alt="spacer" width="25" height="1" /></td>
        <td width="394" align="left" valign="top"><span class="style1"><span class="style2">Sample questions:  &nbsp; &nbsp;</span> <br />
              <br />
          </span>
            <ol>
              <li class="style1">&ldquo;Is it possible to give heat treatment to copper?&rdquo; </li>
              <li class="style1">&ldquo;How can wet carbon dioxide be responsible for a corrosion problem in iron-containing metals?&rdquo;</li>
              <li class="style1">&ldquo;What type of steel rod/axle&nbsp;would you use for one way roller bearings.&rdquo;</li>
            </ol>
          <p align="center"><img src="/engresources/images/ae/images/email.gif" alt="email" width="124" height="14" /></p></td>
      </tr>
    </table>
    <p><br />
    </p>
  </div>

  <div id="Layer7" style="display: none;">
    <p><span class="style1">Our Senior Engineers will draw on their professional knowledge and experience to answer your technical engineering questions.&nbsp; They can also point you to the appropriate companies, consultants, research institutes, web sites, and other resources that can help you solve your problem.</span></p>

    <table width="972" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td width="111" align="left" valign="top"><img src="/engresources/images/ae/images/Idustrial.gif" alt="Mechanical" width="108" height="128" /></td>
        <td width="25"><img src="/engresources/images/ae/images/spacer.gif" alt="spacer" width="25" height="1" /></td>
        <td width="417" align="left" valign="top"><p class="style1"><span class="style2">Gregory A. Sedrick  </span> <br />
          UC Foundation assistant professor and director, Engineering Management, Industrial and Manufacturing Engineering Programs, University of Tennessee
  </p>
          <p class="style1">Areas of expertise: TQM (Total Quality Management), ISO 9000, ISO 9000 certification, work measurement, engineering economy, work design and improvement, methods improvement, small manufacturing, multimedia-based corporate training in above areas, Baldridge Award preparation/judging </p></td>
        <td width="25" align="left" valign="top"><img src="/engresources/images/ae/images/spacer.gif" alt="spacer" width="25" height="1" /></td>
        <td width="394" align="left" valign="top"><span class="style1"><span class="style2">Sample questions:  &nbsp; &nbsp;</span> <br />
              <br />
          </span>
            <ol>
              <li class="style1">&ldquo;Is it possible to give heat treatment to copper?&rdquo; </li>
              <li class="style1">&ldquo;How can wet carbon dioxide be responsible for a corrosion problem in iron-containing metals?&rdquo;</li>
              <li class="style1">&ldquo;What type of steel rod/axle&nbsp;would you use for one way roller bearings.&rdquo;</li>
            </ol>
          <p align="center"><img src="/engresources/images/ae/images/email.gif" alt="email" width="124" height="14" /></p></td>
      </tr>
    </table>
    <p><br />
    </p>
  </div>


  <div id="Layer8"  style="display: none;">
    <p><span class="style1">Our Senior Engineers will draw on their professional knowledge and experience to answer your technical engineering questions.&nbsp; They can also point you to the appropriate companies, consultants, research institutes, web sites, and other resources that can help you solve your problem.</span></p>

    <table width="972" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td width="111" align="left" valign="top"><img src="/engresources/images/ae/images/Materials.gif" alt="Mechanical" width="108" height="128" /></td>
        <td width="25"><img src="/engresources/images/ae/images/spacer.gif" alt="spacer" width="25" height="1" /></td>
        <td width="417" align="left" valign="top"><p class="style1"><span class="style2">Keith Sheppard      </span> <br />
          Professor of Materials Science and Engineering, Stevens Institute of Technology
  </p>
          <p class="style1">Areas of expertise: corrosion, electro-deposition, failure of materials, materials characterization, especially scanning and transmission electron microscopy, materials selection, materials processing </p></td>
        <td width="25" align="left" valign="top"><img src="/engresources/images/ae/images/spacer.gif" alt="spacer" width="25" height="1" /></td>
        <td width="394" align="left" valign="top"><span class="style1"><span class="style2">Sample questions:  &nbsp; &nbsp;</span> <br />
              <br />
          </span>
            <ol>
              <li class="style1">&ldquo;Is it possible to give heat treatment to copper?&rdquo; </li>
              <li class="style1">&ldquo;How can wet carbon dioxide be responsible for a corrosion problem in iron-containing metals?&rdquo;</li>
              <li class="style1">&ldquo;What type of steel rod/axle&nbsp;would you use for one way roller bearings.&rdquo;</li>
            </ol>
          <p align="center"><img src="/engresources/images/ae/images/email.gif" alt="email" width="124" height="14" /></p></td>
      </tr>
    </table>
    <p><br />
    </p>
  </div>



  <div id="Layer9"  style="display: none;">
    <p><span class="style1">Our Senior Engineers will draw on their professional knowledge and experience to answer your technical engineering questions.&nbsp; They can also point you to the appropriate companies, consultants, research institutes, web sites, and other resources that can help you solve your problem.</span></p>

    <table width="972" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td width="111" align="left" valign="top"><img src="/engresources/images/ae/images/Computer-.gif" alt="Mechanical" width="108" height="128" /></td>
        <td width="25"><img src="/engresources/images/ae/images/spacer.gif" alt="spacer" width="25" height="1" /></td>
        <td width="417" align="left" valign="top"><p class="style1"><span class="style2">Earl E. Swartzlander, Jr.       </span> <br />
          Professor, Electrical and Computer Engineering, and Schlumberger Centennial Chair in Engineering, University of Texas
  </p>
          <p class="style1">Areas of expertise: computer engineering, computer arithmetic, application specific processing, interaction between computer architecture and VLSI technology, signal processing </p></td>
        <td width="25" align="left" valign="top"><img src="/engresources/images/ae/images/spacer.gif" alt="spacer" width="25" height="1" /></td>
        <td width="394" align="left" valign="top"><span class="style1"><span class="style2">Sample questions:  &nbsp; &nbsp;</span> <br />
              <br />
          </span>
            <ol>
              <li class="style1">&ldquo;Is it possible to give heat treatment to copper?&rdquo; </li>
              <li class="style1">&ldquo;How can wet carbon dioxide be responsible for a corrosion problem in iron-containing metals?&rdquo;</li>
              <li class="style1">&ldquo;What type of steel rod/axle&nbsp;would you use for one way roller bearings.&rdquo;</li>
            </ol>
          <p align="center"><img src="/engresources/images/ae/images/email.gif" alt="email" width="124" height="14" /></p></td>
      </tr>
    </table>
    <p><br />
    </p>
  </div>

  <a href="javascript:emailFormat('$SESSIONID','one');">Ask a question</a>
</div>


<div id="two" style="display: none; text-align:left;">
  <p>&nbsp;</p>
  <p>&nbsp;</p>

  <p class="pro_style1">Our Product Specialist can help you with search assistance in Engineering Village, training needs and questions regarding features and results. </p>

  <table width="972" border="0" cellspacing="0" cellpadding="0">
    <tr>
      <td width="511" align="left" valign="top"><img src="/engresources/images/ae/images/Specialist.gif" alt="AskProductSpecialist " width="493" height="362" id="Image1" onmouseover="MM_swapImage('Image1','','/engresources/images/ae/images/Specialist_over.gif',0)" onmouseout="MM_swapImgRestore()" /></td>
      <td width="25" align="left" valign="top"><img src="/engresources/images/ae/images/spacer.gif" alt="spacer" width="25" height="1" /></td>
      <td width="436" align="left" valign="top"><span class="pro_style1"><span class="pro_style2">Sample questions:  &nbsp; &nbsp;</span> <br /></span>
        <ol>
          <li class="pro_style1">&ldquo;How often do patents to appear on Engineering Village after issuance?&rdquo;</li>
          <li class="pro_style1">&ldquo;How can I use Engineering Village to track competitors?&rdquo;<br />
          </li>
          <li><span class="pro_style1">&ldquo;How would I complete a search for business models and templates?&rdquo;&nbsp;</span>&nbsp; </li>
        </ol>
        <p align="center"><img src="/engresources/images/ae/images/training.gif" alt="email" width="167" height="14" /></p>
        <p align="center"><img src="/engresources/images/ae/images/email_specialist.gif" alt="email_specalist" width="167" height="14" /></p></td>
    </tr>
  </table>
  <p class="pro_style4"><br />
  FAQ</p>
  <ol start="1" type="1">
    <li class="pro_style1">When is full text available?<a name="1" id="1"></a></li>
    <li class="pro_style1">How can I obtain a full text document is it is not available to me?</li>
    <li class="pro_style1">Which groups of results data (&quot;facets&quot;) are available to me?</li>
    <li class="pro_style1">How can I use the grouped result data (&quot;facets&quot;)&nbsp;when viewing my search results?</li>
    <li class="pro_style1">Why would I export the grouped result data (&quot;facets&quot;) into a chart and/or spreadsheet?</li>
    <li class="pro_style1">How often is content updated on EV?</li>
    <li class="pro_style1">How can I manage my results and save my searches?</li>
    <li class="pro_style1">What is the benefit of email alerts and RSS</li>
    <li class="pro_style1">Why would I blog a record?</li>
    <li class="pro_style1">What are tags and how can&nbsp;I use them?</li>
    <li class="pro_style1">What does it mean to &quot;Browse Indexes&quot;?</li>
    <li class="pro_style1">How can the thesaurus help me when searching for content?</li>
  </ol>
  <p class="pro_style5">1. When is full text available?</p>
  <p class="pro_style1">Compendex is an A&amp;I database, or abstracts and index. What this means is that your results will all be in citation, and you can choose to look at either the abstract or detailed record format. You can look at the full text when available. What this means is that your institution subscribes to many journals and conferences, so for those journals and conferences, you would have an additional link in Engineering Village to full text, which would open up that particular article or proceeding for you.&nbsp;</p>
  <p class="pro_style5">2. How can I obtain a full text document is it is not available to me?</p>
  <p class="pro_style1">If you would like to access the full text for a record your institution does not access, you can choose to go through a document delivery service. Linda Hall Library is a good one: <a href="http://www.lindahall.org/services/document_delivery/partners/eicompendex.shtml">http://www.lindahall.org/services/document_delivery/partners/eicompendex.shtml</a>. It is quite inexpensive and they are very good at getting you the documents very quickly. You may also contact your librarian about purchasing the article for you, through Linda Hall or another service.</p>
  <p class="pro_style1">You may be able to get more information on a publisher&rsquo;s site or possibly purchase the article there as well.</p>
  <p class="pro_style5">3. Which groups of results data (&quot;facets&quot;) are available to me?</p>
  <p class="pro_style1">You will see the &ldquo;facets&rdquo; that are available for the database(s) you are searching.&nbsp; Searching multiple databases at a time will change the facts available to you.&nbsp; You will only see the facets that those databases share in common.&nbsp; The Year and Add a Term facets will always appear.</p>
  <p class="pro_style4">4. How can I use the grouped result data (&quot;facets&quot;)&nbsp;when viewing my search results?</p>
  <p class="pro_style1">After performing an initial search, a list of facets will appear on the right side of the search results screen. <br />
  These facets categories include and are not limited to: Databases, Author, Controlled vocabulary, Classification code, Document type, Language, Year, and Publisher. Under each facet, a list of the most recurring terms in that category will appear in descending order.&nbsp; Each facet displays the first 10 items that are relevant for your search. Click on more to find further information within each facet. <br />
  When you check one or more boxes under any of the facets and click 'include', the terms that you selected will be added to your initial search terms, therefore refining the results set. This is similar to doing an AND search with these terms.&nbsp; A number of search terms can be added to the same search, i.e. combining author, year and publication or any other combination which is most appropriate to the search.&nbsp; The &ldquo;Add a Term&rdquo; option allows you to add your own terms for search refinement and can be combined with other terms in the faceted list.</p>
  <p class="pro_style5">5. Why would I export the grouped result data (&quot;facets&quot;) into a chart and/or spreadsheet?</p>
  <p class="pro_style1">The graph and export icons appear next to each facet in the Refine Results section of the search results screen. They are functional elements which allow you to view your faceted results as a bar graph or download a text file that can be exported to other software. <br />
  The Export icon &nbsp;lets you save or open the terms and records counts as a tab file which can be opened with any software. All the terms in a facet are exported at once regardless of the view. &nbsp;When you click on the icon, a download window will appear. Save or open the tab file. You can select in which software you would like the file to open. <br />
  Clicking on the bar graph sign Image &nbsp;will open a new window where an image of the top 10 results in the facet will appear. The bar graph will show the current results in the facet. &nbsp;For example, when you click on 'more', adding 10 more terms are added to the facet, the graph will show the terms that appear on the list that you are viewing. You can save, print or email this image using the windows images utility functions.</p>
  <p class="pro_style5">6. How often is content updated on EV?</p>
  <p class="pro_style1">All of the bibliographic, patent, and news databases are updated with new content weekly.&nbsp; New Referex eBook titles are added annually.</p>
  <p class="pro_style5">7. How can I manage my results and save my searches?</p>
  <p class="pro_style1">Managing your results is easy.&nbsp; Once you have refined your results, you can use the Results Manager to select records, and then view, email, print, download or save the selected records.&nbsp; <br />
  On the Search Results page, there is also a Save Search link, which can be found next to the record count.&nbsp; Simply clicking this link will allow you to save your searched to your Profile.&nbsp; <br />
    In order to save your searches or download specific records to a folder, you must create a personal account.</p>
  <p class="pro_style1">What is the benefit of email alerts and RSS?</p>
  <p class="pro_style1">Email alerts and RSS feeds will keep you up to date with recently added content.&nbsp; These features will allow you to get automatic weekly updates of your search queries' results either emailed directly to your inbox via email alerts or straight to your RSS reader.</p>
  <p class="pro_style1">Why would I blog a record?</p>
  <p class="pro_style1">The Blog This feature on Engineering Village allows you to Link from a blog to a record of interest on Engineering Village.&nbsp; This can create current awareness by highlighting new publications in a popular subject area.&nbsp; You can also create a forum from which to collaborate with colleagues in your area of research.&nbsp; In addition, the Blog This feature allows you the option of sharing information with an audience outside of your organization.&nbsp; <br />
&nbsp;In order to use this feature you would simply click the &quot;Blog This&quot; button in an abstract or detailed record.&nbsp; Select the content that is showing in the box and paste it in your blog post.&nbsp; Once you publish your post the title of the record will appear in your blog.</p>
  <p class="pro_style1">What are tags and how can&nbsp;I use them?</p>
  <p class="pro_style1">Tagging is a user-defined taxonomy. Such taxonomy is sometimes called a folksonomy and the bookmarks are referred to as tags. <br />
  With Engineering Village tagging you can add, edit and share tags you assigned to records.&nbsp; You can also search other users&rsquo; tags to gain new perspectives or organize your records of interest for yourself and groups. <br />
    You can create groups by inviting people with the same research interest as you to join a group with which you can share tagged records and view records shared by the group. </p>
  <p class="pro_style1">What does it mean to &quot;Browse Indexes&quot;?</p>
  <p class="pro_style1">From the Browse Indexes box (located on the right side of Quick and Expert Search), select the index you wish to use by clicking on the link.&nbsp; Once the index is loaded, you can navigate by either selecting the first letter(s) of the term you wish to search for or by typing in the first few letters of the term in the SEARCH FOR box and clicking on &quot;Find.&quot;&nbsp; You can browse the indexes to search for precise terms or look for variations of a term (i.e. spelling or abbreviations). &nbsp;</p>
  <p class="pro_style1">How can the thesaurus help me when searching for content?</p>
  <p class="pro_style1">The thesauri are guides to the controlled vocabulary used in indexing articles for Compendex and Inspec. &nbsp;Terms from the controlled vocabulary are assigned to each record to describe the article they are indexing. The controlled vocabulary is used to standardize the way the articles are indexed. The thesauri are hierarchical in nature. Terms are organized by broader, narrower or related concepts.&nbsp; You can use the thesauri to either expend your search or refine your search by finding broader, narrower or related terms to search on your subject area.</p>
  <p>&nbsp;</p>
  <p>&nbsp;</p>
  <p>&nbsp;</p>
  <p>&nbsp;</p>

</div>


<div id="three" style="display: none;">
  <h1>Ask a Librarian</h1>

    <xsl:choose>
      <xsl:when test="$REFEMAIL=''">
        Our Librarian
      </xsl:when>
      <xsl:otherwise>
        Your Librarian
      </xsl:otherwise>
    </xsl:choose>
    <a href="javascript:emailFormat('$SESSIONID','three');">Ask a question</a>
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

</xsl:stylesheet>