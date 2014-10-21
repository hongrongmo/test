<%@ page language="java" session="false" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld"	prefix="stripes"%>

<head>
<link href="/static/css/popups.css?v=${releaseversion}" media="all" type="text/css" rel="stylesheet"></link>
<style>
	.marketing_image{
		text-align:center;
	}
	#marketing_message .message{
		font-weight: normal;
		font-size: 18px;
		text-align: center;
	}
</style>
</head>
<body>
<div id="marketing_message">
	<div class="message">
		Take full advantage of Engineering Village by using the <a href="http://www.elsevier.com/__data/assets/file/0003/218559/ev-tipsntricks.pdf" target="new" title="Click to open Tips & Tricks PDF" onclick="GALIBRARY.createWebEventWithLabel('Education Promo', 'Click Link', '');">Tips & Tricks page</a>
		and experience the leading information discovery tool trusted by engineers across the globe.
	</div>
	<div class="marketing_image">
		<a href="http://www.elsevier.com/__data/assets/file/0003/218559/ev-tipsntricks.pdf" target="new" title="Click to open Tips & Tricks PDF" onclick="GALIBRARY.createWebEventWithLabel('Education Promo', 'Click Image', '');"><img src="/static/images/evtips.jpg" alt="Engineering Village Tips & Tricks" title="Engineering Village Tips & Tricks" /></a>
	</div>
</div>
</body>
<script>
function resetMM(){
	//clearTimeout(myTimer);
}

</script>
