<?xml version="1.0"?>
<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:html="http://www.w3.org/TR/REC-html40"
	xmlns:java="java:java.net.URLEncoder"
	exclude-result-prefixes="java html xsl"
>

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

<html>

<head>
<title>Engineering Village - Ask an Expert</title>
<SCRIPT LANGUAGE="Javascript" SRC="/engresources/js/StylesheetLinks.js"/>
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

  <xsl:choose>
    <xsl:when test="boolean(string-length(normalize-space($REFEMAIL))>0)">

    	<table border="0" cellspacing="0" cellpadding="0" width="100%">
    		<tr><td height="20"><img src="/engresources/images/spacer.gif" height="20"/></td></tr>
    		<tr><td valign="top"><a class="EvHeaderText">Ask an Expert</a></td></tr>
    		<tr><td valign="top"><a CLASS="MedBlackText">If you have searched Engineering Village and cannot find the information you need, Reference Services offers two forms of assistance:</a><br/>
    			<a class="LgBlueLink" href="#librarian">Ask a Librarian</a><br/>
    			<a class="LgBlueLink" href="#engineer">Ask an Engineer</a>
    			
    		<p>
    		<a NAME="librarian" class="MedBlackText"><b>Ask a Librarian</b><br/>
    		Your librarian can help you formulate a search in Engineering Village, identify and locate a source for a book or an article, or find Internet resources on a specific topic.</a>
    
    		</p>
    
    		<p>
    			<a CLASS="MedBlackText">To send a question to your librarian, click </a><a CLASS="LgBlueLink" href="{$REFEMAIL}">here</a>.
    		</p>
    		
    		<p>
    			<a NAME="engineer" class="MedBlackText"><b>Ask an Engineer</b> <br/>
    			Our Engineers will draw on their professional knowledge and experience to answer your technical questions and to point you to the appropriate companies, consultants, research institutes, Web sites, and other resources that can help you solve your problem.</a>
    		</p>
    		
    		<p>
    			<a class="MedBlackText">To send a question to one of the engineers, simply click on the e-mail address next to engineer's name and type in your question in the e-mail message window that opens.</a>
    		</p>
    		
    		<p>
    			<a class="MedBlackText">All <b>Ask and Engineer</b> assistance is provided as a complementary service to subscribers of Engineering Village</a>
    		</p>
    		
    		<p>
    			<a class="MedBlackText"><b>Meet the Engineers</b></a>
    		</p>
    		
    		<p>
    			<a class="MedBlackText"><b>Ryo Samuel Amano</b></a> &#160; &#160; <a class="LgBlueLink" href="mailto:fluidguru@ei.org">fluidguru@ei.org</a><br/>
    			<a class="MedBlackText">Mechanical Engineering<br/>
    			Fluid Mechanics and Thermodynamics professor, Department of Mechanical Engineering, University of Wisconsin-Milwaukee
    			<br/><br/>
    			<i>Areas of expertise:</i> CFD, aerodynamics, gas/steam turbines, fluid mechanics/fluid dynamics, rocket/propulsion systems, heat transfer/heat exchangers/engines</a>
    		</p>
    		
    		<p>
    			<a class="MedBlackText"><b>Robert D. Borchelt</b></a>&#160; &#160; <a class="LgBlueLink" href="mailto:manguru@ei.org">manguru@ei.org</a><br/>
    			<a class="MedBlackText">Manufacturing Engineering<br/>
    			Master Black Belt Design for Six Sigma, Cummins Inc.<br/>
    			Formerly Technical Advisor for Assembly and Automation in the Corporate Manufacturing Engineering Group, Cummins Inc.
    			<br/><br/>
    			<i>Areas of expertise:</i> computer integrated manufacturing, automation and advanced manufacturing systems, artificial intelligence applications in robotics and manufacturing, automated diagnosis and error recovery, hybrid knowledge-based systems, programmable logic controller applications, physical modeling of manufacturing systems, engineering management issues related to manufacturing, manufacturing processes, systems integration, expert systems, electronic identification of components, direct part marking, design for six sigma methods, six sigma methods</a>
    		</p>
    		
    		<p>
    			<a class="MedBlackText"><b>Chi Hau Chen</b></a>&#160; &#160; <a class="LgBlueLink" href="mailto:spquru@ei.org">spquru@ei.org</a><br/>
    			<a class="MedBlackText">Signal and Image Processing<br/>
    			Chancellor Professor, Electrical and Computer Engineering, University of Massachusetts 
    			<br/><br/>
    			<i>Areas of expertise:</i> pattern recognition, signal/image processing and neural networks, applications to ultrasonic/nondestructive evaluation (NDE), and seismic problems, machine vision, artificial intelligence, time series analysis, wavelet analysis</a>
    		</p>
    		
    		<p>
    			<a class="MedBlackText"><b>Donald W. Merino, Jr.</b></a>&#160; &#160; <a class="LgBlueLink" href="mailto:tqmguru@ei.org">tqmguru@ei.org</a><br/>
    			<a class="MedBlackText">Industrial Engineering / Engineering Management
    			<br/><br/> 
    			<i>Areas of expertise:</i> production operations, process flow/optimization, process simulation, capital allocation, cost models, concurrent engineering, SQC/SPC (Statistical Quality Control/Statistical Process Control) and applied statistics, TQM philosophy, benchmarking, DOE (Design of Experiments), QFD (Quality Function Deployment), cost of quality, economic/non-economic decision making, optimization </a>
    		</p>
    		
    		<p>
    			<a class="MedBlackText"><b>Ronald A. Perez</b></a>&#160; &#160; <a class="LgBlueLink" href="mailto:autoguru@ei.org">autoguru@ei.org</a><br/>
    			<a class="MedBlackText">Mechanical Engineering<br/>
    			Mechanics, mechanisms, and automation associate professor, Department of Mechanical Engineering, University of Wisconsin 
    			<br/><br/>
    			Areas of expertise: control theory and applications, robust multivariable control, nonlinear control, robotics, system modeling, interaction analysis, intelligent control</a>
    		</p>
    		
    		<p>
    			<a class="MedBlackText"><b>Kanti Prasad</b></a>&#160; &#160; <a class="LgBLueLink" href="mailto:eeguru@ei.org">eeguru@ei.org</a><br/>
    			<a class="MedBlackText">Electrical Engineering<br/>
    			Professor, electrical engineering, and director, Microelectronics/VLSI Technology, University of Massachusetts
    			<br/><br/> 
    			<i>Areas of expertise:</i> VLSI chip design incorporating microprocessors, controls, communications, intelligent transportation systems, GPS, computer networks, LANs, including wireless LANs, VLSI fabrication incorporating microelectronics processing such as lithography oxidation, diffusion, implantation, and metallization for silicon as well as GaAs technology including packing and reliability analysis, digital and analog design employing VHDL incorporating test and simulation I entirety, i.e., exhaustive testing and simulation with statistically fast and event driven simulators </a>
    		</p>
    		
    		<p>
    			<a class="MedBlackText"><b>Gregory A. Sedrick</b></a>&#160; &#160; <a class="LgBlueLink" href="mailto:induguru@ei.org">induguru@ei.org </a><br/>
    			<a class="MedBlackText">Industrial and Manufacturing Engineering<br/>
    			Director, The CROPIS New Economy Institute (formerly Dean College of Engineering and Computer Science University of Tennessee at Chattanooga, Dean School of Engineering and Engineering Technology, LeTourneau University, and Vice President Academic Affairs, Chattanooga State Technical Community College.) 
    			<br/><br/>
    			<i>Areas of expertise:</i> lean manufacturing, six sigma, advanced technology incubation and commercial spin-off, intellectual properties, value engineering,  plant layout, work measurement, engineering economy, work design and improvement, methods improvement, small manufacturing, multimedia-based corporate training in above areas, Baldridge Award preparation/judging, TQM (Total Quality Management), ISO 9000, ISO 9000 certification</a>
    		</p>
    		
    		<p>
    			<a class="MedBlackText"><b>Keith Sheppard</b></a>&#160; &#160; <a class="LgBLueLink" href="mailto:materials2guru@ei.org">materials2guru@ei.org</a><br/>
    			<a class="MedBlackText">Materials Engineering<br/>
    			Professor of Materials Science and Engineering, Stevens Institute of Technology
    			<br/><br/> 
    			<i>Areas of expertise:</i> corrosion, electro-deposition, failure of materials, materials characterization, especially scanning and transmission electron microscopy, materials selection, materials processing </a>
    		</p>
    		
    		<p>
    			<a class="MedBlackText"><b>Earl E. Swartzlander, Jr.</b></a>&#160; &#160; <a class="LgBlueLink" href="mailto:compengguru@ei.org">compengguru@ei.org</a><br/>
    			<a class="MedBlackText">Computer Engineering<br/>
    			Professor, Electrical and Computer Engineering, and Schlumberger Centennial Chair in Engineering, University of Texas
    			<br/><br/> 
    			<i>Areas of expertise:</i> computer engineering, computer arithmetic, application specific processing, interaction between computer architecture and VLSI technology, signal processing </a>
    		</p>
    
    		</td></tr>
    	</table>
    </xsl:when>
    <xsl:otherwise>
      <table border="0" width="99%" cellspacing="0" cellpadding="0">
      <tr><td valign="top" height="20"><img src="/engresources/images/spacer.gif" height="20" border="0"/></td></tr>
      <tr><td valign="top"><a class="EvHeaderText">Ask an Expert</a></td></tr>
      <tr><td valign="top" height="10"><img src="/engresources/images/spacer.gif" height="10" border="0"/></td></tr>
      <tr>
      <td valign="top">
      <a class="MedBlackText">If you have searched Engineering Village and cannot find the information you need, Reference Services offers two forms of assistance:</a><br/>
      &#32; &#32; &#32; <a class="LgBlueLink" href="#Librarian">Ask a Librarian </a><br/>
      &#32; &#32; &#32; <a class="LgBlueLink" href="#Engineer">Ask an Engineer. </a>
      
      <p>
      <a class="MedBlackText">All assistance is provided as a complementary service to subscribers of Engineering Village</a>
      </p>
      
      <P>
      <a NAME="Librarian" class="MedBlackText"><b>Ask a Librarian</b><br/>
      Elsevier Engineering Information's librarians can help you formulate a search in Engineering Village, identify and locate a source for a book or an article, or find Internet resources on a specific topic. </a>
      </P>
      
      <P>
      <a class="MedBlackText">Questions can also be referred out to the specialists at the Linda Hall Library.  If you would like to send questions to the Linda Hall staff, please indicate this in your request to Ask a Librarian.</a>
      </P>
      
      <p>
      <a class="MedBlackText"><b>Ask a Librarian e-mail: </b> </a> <a class="LgBlueLink" href="mailto:engineeringlibrarian@ei.org">engineeringlibrarian@ei.org</a>
      </p>
      
      <p>
      <a class="MedBlackText"><b>Training</b><br/>
      Elsevier Engineering Information's librarians provide regular training sessions via the Internet to customers and end users of Engineering Village. To sign up for a training session please contact </a><a class="LgBlueLink" href="mailto:eicustomersupport@elsevier.com">eicustomersupport@elsevier.com.</a>
      </p>
      
      <p>
      <a class="MedBlackText">
      These training sessions are given live online and hosted by the Ei librarian. You must have simultaneous access to an Internet connection using either Internet Explorer or Netscape browsers and a voice phone line to carry the audio. Calls from within the United States and Canada are toll free. </a>
      </p>
      
      <p>
      <a class="MedBlackText">
      Internet based training on Engineering Village is provided as a complementary service and is included in the library's subscription to the service.</a>
      </p>
      
      <P>
      <a NAME="Engineer" class="MedBlackText"><b>Ask an Engineer </b><br/>
      Our Senior Engineers will draw on their professional knowledge and experience to answer your technical questions and to point you to the appropriate companies, consultants, research institutes, Web sites, and other resources that can help you solve your problem.</a>
      </P>
      
      <P>
      <a class="MedBlackText">To send a question to one of the engineers, simply click on the e-mail address next to engineer's name and type in your question in the e-mail message window that opens.</a><br/>
      </P>
      
      <P>
      <a class="MedBlackText"><b>Meet the Engineers</b></a>
      </P>
      </td></tr>
      <tr><td valign="top" height="15"><img src="/engresources/images/spacer.gif" height="15" border="0"/></td></tr>
      <tr><td valign="top">
      <a class="MedBlackText"><b>Ryo Samuel Amano</b></a>&#160; &#160; <a class="LgBlueLink" href="mailto:fluidguru@ei.org">fluidguru@ei.org</a><br/>
      <a class="MedBlackText">Mechanical Engineering<br/>
      Fluid and thermodynamics professor, Department of Mechanical Engineering, University of Wisconsin-Milwaukee</a></td></tr>
      <tr><td valign="top" height="5"><img src="/engresources/images/spacer.gif" height="5" border="0"/></td></tr>
      <tr><td valign="top"><a class="MedBlackText"><i>Areas of expertise</i>: aerodynamics, gas/steam turbines, fluid mechanics/fluid dynamics, rocket/propulsion systems, heat transfer/heat exchangers/engines</a>
      </td></tr>
      
      
      <tr><td valign="top" height="25"><img src="/engresources/images/spacer.gif" height="25" border="0"/></td></tr>
      <tr><td valign="top">
      <a class="MedBlackText"><b>Robert D. Borchelt</b></a>&#160; &#160; <a class="LgBlueLink" href="mailto:manguru@ei.org">manguru@ei.org</a><br/>
      <a class="MedBlackText">Manufacturing Engineering<br/>
      Technical advisor for assembly and automation in the Corporate Operations Analysis Group, Cummins Engine Company</a></td></tr>
      <tr><td valign="top" height="5"><img src="/engresources/images/spacer.gif" height="5" border="0"/></td></tr>
      <tr><td valign="top"><a class="MedBlackText"><i>Areas of expertise</i>: computer integrated manufacturing, automation and advanced manufacturing systems, artificial intelligence applications in robotics and manufacturing, automated diagnosis and error recovery, hybrid knowledge-based systems, programmable logic controller applications, physical modeling of manufacturing systems, engineering management issues related to manufacturing, manufacturing processes, systems integration, expert systems </a></td></tr>
      
      
      <tr><td valign="top" height="25"><img src="/engresources/images/spacer.gif" height="25" border="0"/></td></tr>
      <tr><td valign="top">
      <a class="MedBlackText"><b>Chi Hau Chen</b></a>&#160; &#160; <a class="LgBlueLink" href="mailto:spguru@ei.org">spguru@ei.org</a><br/>
      <a class="MedBlackText">Signal Processing<br/>
      Signal processing professor, Electrical and Computer Engineering, University of Massachusetts</a></td></tr>
      <tr><td valign="top" height="5"><img src="/engresources/images/spacer.gif" height="5" border="0"/></td></tr>
      <tr><td valign="top"><a class="MedBlackText"><i>Areas of expertise</i>: pattern recognition, signal/image processing and neural networks, applications to ultrasonic/nondestructive evaluation (NDE), sonar, radar, and seismic problems, machine vision, artificial intelligence, time series analysis, wavelet analysis
      </a></td></tr>
      
      
      <tr><td valign="top" height="25"><img src="/engresources/images/spacer.gif" height="25" border="0"/></td></tr>
      <tr><td valign="top">
      <a class="MedBlackText"><b>Donald W. Merino, Jr.</b></a>&#160; &#160; <a class="LgBlueLink" href="mailto:tqmguru@ei.org">tqmguru@ei.org</a><br/>
      <a class="MedBlackText">Industrial Engineering / Engineering Management</a></td></tr>
      <tr><td valign="top" height="5"><img src="/engresources/images/spacer.gif" height="5" border="0"/></td></tr>
      <tr><td valign="top"><a class="MedBlackText"><i>Areas of expertise</i>: production operations, process flow/optimization, process simulation, capital allocation, cost models, concurrent engineering, SQC/SPC (Statistical Quality Control/Statistical Process Control) and applied statistics, TQM philosophy, benchmarking, DOE (Design of Experiments), QFD (Quality Function Deployment), cost of quality, economic/non-economic decision making, optimization
      </a></td></tr>
      
      
      <tr><td valign="top" height="25"><img src="/engresources/images/spacer.gif" height="25" border="0"/></td></tr>
      <tr><td valign="top">
      <a class="MedBlackText"><b>Ronald A. Perez</b></a>&#160; &#160; <a class="LgBlueLink" href="mailto:autoguru@ei.org">autoguru@ei.org</a><br/>
      <a class="MedBlackText">Mechanical Engineering<br/>
      Mechanics, mechanisms, and automation assistant professor, Department of Mechanical Engineering, University of Wisconsin</a></td></tr>
      <tr><td valign="top" height="5"><img src="/engresources/images/spacer.gif" height="5" border="0"/></td></tr>
      <tr><td valign="top"><a class="MedBlackText"><i>Areas of expertise</i>: control theory and applications, robust multivariable control, nonlinear control, robotics, system modeling, interaction analysis, intelligent contro
      </a></td></tr>
      
      
      <tr><td valign="top" height="25"><img src="/engresources/images/spacer.gif" height="25" border="0"/></td></tr>
      <tr><td valign="top">
      <a class="MedBlackText"><b>Kanti Prasad</b></a>&#160; &#160; <a class="LgBlueLink" href="mailto:eeguru@ei.org">eeguru@ei.org</a><br/>
      <a class="MedBlackText">Electrical Engineering<br/>
      Professor, electrical engineering, and director, Microelectronics/VLSI Technology, University of Massachusetts</a></td></tr>
      <tr><td valign="top" height="5"><img src="/engresources/images/spacer.gif" height="5" border="0"/></td></tr>
      <tr><td valign="top"><a class="MedBlackText"><i>Areas of expertise</i>: VLSI chip design incorporating microprocessors, controls, communications, intelligent transportation systems, GPS, computer networks, LANs, including wireless LANs, VLSI fabrication incorporating microelectronics processing such as lithography oxidation, diffusion, implantation, and metallization for silicon as well as GaAs technology including packing and reliability analysis, digital and analog design employing VHDL incorporating test and simulation I entirety, i.e., exhaustive testing and simulation with statistically fast and event driven simulators
      </a></td></tr>
      
      
      <tr><td valign="top" height="25"><img src="/engresources/images/spacer.gif" height="25" border="0"/></td></tr>
      <tr><td valign="top">
      <a class="MedBlackText"><b>Gregory A. Sedrick</b></a>&#160; &#160; <a class="LgBlueLink" href="mailto:induguru@ei.org">induguru@ei.org</a><br/>
      <a class="MedBlackText">Industrial and Manufacturing Engineering<br/>
      UC Foundation assistant professor and director, Engineering Management, Industrial and Manufacturing Engineering Programs, University of Tennessee</a></td></tr>
      <tr><td valign="top" height="5"><img src="/engresources/images/spacer.gif" height="5" border="0"/></td></tr>
      <tr><td valign="top"><a class="MedBlackText"><i>Areas of expertise</i>: TQM (Total Quality Management), ISO 9000, ISO 9000 certification, work measurement, engineering economy, work design and improvement, methods improvement, small manufacturing, multimedia-based corporate training in above areas, Baldridge Award preparation/judging
      </a></td></tr>
      
      
      <tr><td valign="top" height="25"><img src="/engresources/images/spacer.gif" height="25" border="0"/></td></tr>
      <tr><td valign="top">
      <a class="MedBlackText"><b>Keith Sheppard</b></a>&#160; &#160; <a class="LgBlueLink" href="mailto:materials2guru@ei.org">materials2guru@ei.org</a><br/>
      <a class="MedBlackText">Materials Engineering<br/>
      Professor of Materials Science and Engineering, Stevens Institute of Technology</a></td></tr>
      <tr><td valign="top" height="5"><img src="/engresources/images/spacer.gif" height="5" border="0"/></td></tr>
      <tr><td valign="top"><a class="MedBlackText"><i>Areas of expertise</i>: corrosion, electro-deposition, failure of materials, materials characterization, especially scanning and transmission electron microscopy, materials selection, materials processing
      </a></td></tr>
      
      
      <tr><td valign="top" height="25"><img src="/engresources/images/spacer.gif" height="25" border="0"/></td></tr>
      <tr><td valign="top">
      <a class="MedBlackText"><b>Earl E. Swartzlander, Jr. </b></a>&#160; &#160; <a class="LgBlueLink" href="mailto:compengguru@ei.org">compengguru@ei.org</a><br/>
      <a class="MedBlackText">Computer Engineering<br/>
      Professor, Electrical and Computer Engineering, and Schlumberger Centennial Chair in Engineering, University of Texas</a></td></tr>
      <tr><td valign="top" height="5"><img src="/engresources/images/spacer.gif" height="5" border="0"/></td></tr>
      <tr><td valign="top"><a class="MedBlackText"><i>Areas of expertise</i>: computer engineering, computer arithmetic, application specific processing, interaction between computer architecture and VLSI technology, signal processing
      </a></td></tr>
      </table>
    
    </xsl:otherwise>
  </xsl:choose>

  <br/><br/>

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