<?xml version="1.0"?>
<!DOCTYPE xsl:stylesheet [
  <!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;nbsp;</xsl:text>'>
]>
<xsl:stylesheet version="1.0"
  xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  exclude-result-prefixes="xsl" >

<xsl:template match="FOOTER">

<br/>

<!-- Footer -->

<div id="footer" class="txtSmaller">
<div id="linkArea" class="padding">
<div class="wrapper">
<ul class="col">
	<li style="float: left; margin-right: 16px"><strong>About Ei</strong>
	<ul>
		<li><a target="_blank" href="http://www.ei.org/" title="Learn more about Ei (opens in a new window)">About Ei</a></li>
		<li><a target="_blank" href="http://www.ei.org/evhistory" title="Learn the history of Ei (opens in a new window)">History of Ei</a></li>
	</ul>
	</li>
	<li style="float: left">
	<img id="eilogo" src="/static/images/ei.gif" title="Engineering Information: Home of Engineering Village"/>
	</li>
	<li style="clear:both;">&nbsp;</li>
	<li><strong>About Engineering Village</strong>
	<ul>
		<li><a target="_blank" href="http://www.ei.org/engineeringvillage" title="Learn more about Engineering Village (opens in a new window)">About Engineering Village</a></li>
		<li><a target="_blank" href="http://www.ei.org/products" title="See what content is available in Engineering Village (opens in a new window)">Content Available</a></li>
		<li><a target="_blank" href="http://www.ei.org/node/4" title="Learn about EV users (opens in a new window)">Who uses EV?</a></li>
	</ul>
	</li>
</ul>

<ul class="col">
	<li><strong>Contact and Support</strong>
	<ul>
		<li><a target="_blank" href="http://www.ei.org/contact-us" title="Contact and support (opens in a new window)">Contact and
		support</a></li>
	</ul>
	</li>
</ul>
<ul class="col">
	<li><strong>About Elsevier</strong>
	<ul>
		<li><a target="_blank" href="http://www.elsevier.com/wps/find/homepage.cws_home" title="Learn about Elsevier (opens in a new window)">About
		Elsevier</a></li>
		<li><a target="_blank" href="http://www.info.sciverse.com/" title="Learn about SciVerse (opens in a new window)">About
		SciVerse</a></li>
		<li><a target="_blank" href="http://www.info.scival.com/" title="Learn about SciVal (opens in a new window)">About
		SciVal </a></li>
		<li><a target="_blank" href="http://www.elsevier.com/locate/termsandconditions" title="See applicable terms and conditions (opens in a new window)">Terms
		and Conditions</a></li>
		<li><a target="_blank" href="http://www.elsevier.com/locate/privacypolicy" title="Read Elsevier’s privacy policy (opens in a new window)">Privacy
		Policy</a></li>
	</ul>
	</li>
</ul>
<div class="floatR">
<ul class="logoElsevier">
	<li><a title="Elsevier homepage (opens in a new window)" target="_blank" href="http://www.elsevier.com"><img width="65" height="71" title="Elsevier homepage (opens in a new window)" alt="Elsevier homepage (opens in a new window)" src="/static/images/logo_Elsevier.gif"/></a></li>
</ul>
</div>
</div>
<div class="clear"></div>
</div>
</div>
<!-- End of footer -->

</xsl:template>

</xsl:stylesheet>

