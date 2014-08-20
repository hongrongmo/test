<%@ page language="java" session="false" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jawr.net/tags" prefix="jwr" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld" prefix="stripes"%>

<link href="/static/css/popups.css?v=${releaseversion}" media="all" type="text/css" rel="stylesheet"></link>
<style>
#slider-code { height: 410px; overflow:hidden; }
#slider-code .viewport { float: left; width: 830px; height: 410px; overflow: hidden; position: relative; }
#slider-code .buttons { display: block; margin: 30px 10px 0 0; float: left; height:80%; padding-top:140px;}
#slider-code .next { margin: 30px 0 0 10px;  }
#slider-code .disable { visibility: hidden; }
#slider-code .overview { list-style: none; padding: 0; margin: 0;  position: absolute; left: 0; top: 0; }
#slider-code .overview li.featureTile{ float: left; margin: 0 20px 0 0; padding: 1px; height: 340px; width: 845px;}

#slider-code .pager { overflow:hidden; list-style: none; clear: both; margin: 0 0 0 45px; }
#slider-code .pager li { float: left; }
#slider-code .pagenum { background-color: #fff; text-decoration: none; text-align: center; padding: 5px; color: #555555; font-size: 14px; font-weight: bold; display: block; }
#slider-code .active { color: #fff; background-color:  #555555; }

#features, #featureDetails {width: 900px; height:410px;overflow:hidden;text-align:center;}
#features .col{float:left; height:300px; width:250px; border:2px green solid; margin:10px;}


.featureBoxSm{
width: 270px;
height: 350px;

}
.featureBox, .fdLeft {
border: 4px solid #148C75;
border-radius:5px;
background: #FFFFFF;
margin:auto;
}
.featureBox:hover{
box-shadow:5px 5px 5px #888888;
-moz-box-shadow:5px 5px 5px #888888;
-webkit-box-shadow:5px 5px 5px #888888;
/* For IE 8 */
	-ms-filter: "progid:DXImageTransform.Microsoft.Shadow(Strength=5, Direction=135, Color='#888888')";
	/* For IE 5.5 - 7 */
	filter: progid:DXImageTransform.Microsoft.Shadow(Strength=5, Direction=135, Color='#888888');
cursor:pointer;
}
.nada{
    width:10px;
}

.fdImage{

}
.fdRight{
text-align:center;
padding-top:15px;
width: 98%;
margin:0 auto;

}
.fdLeft{
width: 90%;
height: 290px;
}
.fdTitle{
text-align:center;
}
#featureList .floatL{float:left;margin:5px;}
.featuretxt, .featureImg{
	text-align:center;
}
.featuretxt{
padding-bottom:35px;
padding-left:4px;
padding-right:4px;

}
.featureImg{
	width:240px;

}
.featureImgDetail{
	height:240px;
}
.featureImgDetail, .featureImg {
	box-shadow:5px 5px 5px #888888;
	-moz-box-shadow:5px 5px 5px #888888;
	-webkit-box-shadow:5px 5px 5px #888888;
	/* For IE 8 */
		-ms-filter: "progid:DXImageTransform.Microsoft.Shadow(Strength=5, Direction=135, Color='#888888')";
		/* For IE 5.5 - 7 */
		filter: progid:DXImageTransform.Microsoft.Shadow(Strength=5, Direction=135, Color='#888888');
}
.fdText li{
	padding:0px;
	float: none;
	margin: 0px;
	padding: 1px;
	height: auto;
	width: auto;
}
.fdText{
	width:90%;
	margin:0 auto;
	font-size:1.3em;
}
.featureBox:hover .featureTitle{
	text-decoration:underline;
}
.featureTitle{
	padding-bottom:0px;
}
#takeATour{padding-top:65px;}
#takeATour a{
	color:148C75;
	font-weight:bold;
	font-size:14px;

}
.featureImageSm{
	height:170px;
}
.featureUL{
padding-top:10px;
list-style-position:inside;
}

</style>

</head>
<body>
 <div id="features">
     <div id="featureList" style="height:320px;padding-left:12px;">
         <div class="featureBoxSm floatL featureBox" id="feature1">

         	<div class="featuretxt featureTitle"><H1>Faster Download</H1></div>
         	<div class="featuretxt featureImageSm"><jwr:image styleClass="featureImg" src="/static/images/oneclickdl.png"></jwr:image></div>
         	<div class="featuretxt">You can download records directly to your PC, or choose Google Drive, Dropbox, or RefWorks. Once you choose, we will remember that preference for your session.</div>
         </div>
         <div class="featureBoxSm floatL featureBox" id="feature2">

            <div class="featuretxt featureTitle"><H1>Download to Dropbox</H1></div>
         	<div class="featuretxt featureImageSm"><jwr:image styleClass="featureImg" src="/static/images/dropboxdl.png"></jwr:image></div>
         	<div class="featuretxt">Download records directly to your Dropbox account. Easily access your files on-the-go</div>
         </div>
         <div class="featureBoxSm  floatL featureBox" id="feature3">

            <div class="featuretxt featureTitle"><H1>Want to learn more?</H1></div>
         	<div class="featuretxt featureImageSm"><jwr:image styleClass="featureImg"  src="/static/images/learnmore.png"></jwr:image></div>
         	<div class="featuretxt">Want to learn more about Engineering Village? Check out our Training videos; or use our Getting Started guide. If you would like to provide feedback or participate in a survey, click the give feedback link.</div>
         </div>
     </div>

     <div id="takeATour"><a href="#" onclick="takeATour();">Take a quick tour of new features >></a></div>
	 <div id="slider-code"  style="display:none;">
	  <a class="buttons prev" href="#"><jwr:img src="/static/images/left_arrow.gif" border="0"  alt=""/></a>
		 <div class="viewport">
	        <ul class="overview">
	        <li class="featureTile">
				 <div id="featureDetail1">
				 	<div class="fdLeft">
				 		<div class="fdTitle"><H1>Faster Download</H1></div>
				 		<div class="fdImage">  <jwr:image styleClass="featureImgDetail" src="/static/images/oneclickdl_big.png"></jwr:image></div>
				 	</div>
				 	<div class="fdRight">
				 		<div class="fdText">
								Engineering Village continues to improve the downloading of search results, so we made the downloading process faster so you can spend more time finding relevant articles. .  On the search results page, click the Download icon at the top of the page.  Choose the location, format, and record type for your file, then click Save.
						</div>
				 	</div>
				 </div>
			 </li>
			 <li class="featureTile">
				 <div id="featureDetail2">
				 	<div class="fdLeft">
				 		<div class="fdTitle"><H1>Download to Dropbox</H1></div>
				 		<div class="fdImage">  <jwr:image styleClass="featureImgDetail" src="/static/images/ev-and-dropbox.png"></jwr:image></div>
				 	</div>
				 	<div class="fdRight">
				 		<div class="fdText">
								Engineering Village works with the tools you use!  Now you can download files into your Dropbox account and access the files via your computer, phone, and tablet.  To use Dropbox within Engineering Village, click the Download icon at the top of the search results page, then choose Dropbox.  Your files download automatically to Dropbox and are available for use immediately.
				 		</div>
				 	</div>
				 </div>
			 </li>
			 <li class="featureTile">
				 <div id="featureDetail3">
				 	<div class="fdLeft">
				 		<div class="fdTitle"><H1>Want to learn more?</H1></div>
				 		<div class="fdImage">  <jwr:image styleClass="featureImgDetail" src="/static/images/latestresources.jpg"></jwr:image></div>
				 	</div>
				 	<div class="fdRight">
				 		<div class="fdText">
							Leveraging the wealth of content and powerful features within Engineering Village is now easier than ever. Our <a href="http://trainingdesk.elsevier.com/products/Engineering-Village" target="_blank">TrainingDesk</a> provides short videos, while our <a href="http://www.elsevier.com/__data/assets/pdf_file/0010/176239/engineering_village_user_guide.pdf" target="_blank">Quick Reference Guide</a> provides how-to advice in order to help you find information efficiently on Engineering Village.
				 		</div>

				 	</div>
				 </div>
			 </li>
	 		</ul>
		 </div>
		 <a class="buttons next" href="#"><jwr:img src="/static/images/right_arrow.gif" border="0"  alt=""/></a>
	 </div>

 </div>

<script>
var oSlider = $("#slider-code");

$(document).ready(function(){
	oSlider.tinycarousel({interval : false, intervaltime:6000, callback: function(element, index){
		//if this is the last slide reset after x seconds
		if(index == ($(".featureTile").length - 1) ){
			//console.log("done");
			if(oSlider.tinycarousel_intervalon()){
				oSlider.tinycarousel_stop();
				myTimer = setTimeout(function(){resetMM();oSlider.tinycarousel_move(1);}, 6000);
			}
		}
	}});

 	$(".featureBox").click(function(){
 		var id = $(this).attr("id").substr("feature".length, $(this).attr("id").length - 1);
 		showCarousel();
 		oSlider.tinycarousel_stop();
 		oSlider.tinycarousel_move(id);
 		GALIBRARY.createWebEventWithLabel('New Feature Click', $(this).attr("id") , $("#"+$(this).attr("id")+" .featureTitle h1").text().trim());


 	});

});
var myTimer;
function takeATour(){
		oSlider.tinycarousel_move(1);
		showCarousel();
		oSlider.tinycarousel_start();
		GALIBRARY.createWebEventWithLabel('Take a Tour Click', "Take a Tour" , "Take a Tour");
		return false;
}

function showCarousel(){
		$("#featureList").hide();
 		$("#backLink").toggle();
 		$(".bar").toggle();
 		$("#slider-code").toggle( "fade" );
 		$("#featureHeading").toggle();
 		$("#takeATour").hide();
}


function resetMM(){
	//clearTimeout(myTimer);

	$('#slider-code').hide();
	$('#backLink').hide();
	$('.bar').hide();
	$('#featureList').show();
	$("#featureHeading").show();
	$("#takeATour").show();

}
</script>
