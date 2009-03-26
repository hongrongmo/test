<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	>
  <xsl:output method="xml" indent="no" doctype-public="-//W3C//DTD XHTML 1.1//EN" doctype-system="http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd"/>

<xsl:template match="/">

	<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en">
	    <head>
	        <meta http-equiv="Content-Type" content="text/html;charset=UTF-8" />
	        <meta name="description" content="Elsevier Engineering Information, the leader in providing online information" />
	        <meta name="keywords" content="Engineering,Chemistry, Paper &amp; Pulp, Online Search Engine, Referex" />

	        <script type="text/javascript" src="http://www.ei.org/js/prototype.lite.js"></script>
	        <script type="text/javascript" src="http://www.ei.org/js/moo.fx.js"></script>
	        <script type="text/javascript" src="http://www.ei.org/js/moo.fx.pack.js"></script>
	        <script type="text/javascript">
	            var webproducts, content_databases, customer_support, news;
	            var toggle_elements = new Array ();

	            window.onload = function() {
	                toggle_elements[0] = new fx.Combo('webproducts', {height: true, opacity: false, duration: 150});
	                toggle_elements[1] = new fx.Combo('content_databases', {height: true, opacity: false, duration: 250});
	                toggle_elements[2] = new fx.Combo('support_center', {height: true, opacity: false, duration: 175});
	                toggle_elements[3] = new fx.Combo('news', {height: true, opacity: false, duration: 175});

	                toggle_elements[3].hide();
	                toggle_elements[1].hide();
	                toggle_elements[0].hide();

	            }

	        </script>
	        <link href="http://www.ei.org/css/layout.css" rel="stylesheet" type="text/css" />
	        <!--[if IE]>
	        <link href="http://www.ei.org/css/ie_fix.css" rel="stylesheet" type="text/css" />
	        <![endif]-->
	        <title>Ei.org - Trial Request Complete</title>
	    </head>
	    <body >
	        <div id="container">
	        <div id="container_inner">

	            <div id="xcontent">
	                <div id="xcontent_inner">
	                    <h2><span>Trial Request</span></h2>

											  <xsl:choose>
												  <xsl:when test="//TRIAL">
							              <p>Thank you for your interest in EI products. You will be contacted by an Elsevier Engineering Information representative shortly to establish a trial of our products.
							              </p>
												  </xsl:when>
												  <xsl:when test="//EXCEPTION">
										        <p>Sorry, there is error on the process, please contact EI at eicustomersupport@elsevier.com.</p>
										        <p>The error message is:</p>
										        <p><xsl:value-of select="//EXCEPTION"/></p>
												  </xsl:when>
												</xsl:choose>


	                </div>
	            </div>


	            <hr />
	        </div>
	        </div>
	        <dl id="footer">
	            <dt>Footer Navigation</dt>
	            <dd>
	                <ul>
	                    <li class="first_child"><a href="mailto:eicustomersupport@elsevier.com">Feedback</a></li>
	                    <li><a href="http://www.ei.org/contact.html">Contact</a></li>
	                    <li><a href="http://www.ei.org/about/privacy.html">Privacy</a></li>
	                </ul>
	                <ul>
	                    <li class="first_child">Copyright 2009 Elsevier Inc. All Rights Reserved</li>
	                </ul>
	            </dd>
	        </dl>
	    </body>
	</html>

</xsl:template>

</xsl:stylesheet>
