<%@ page language="java" session="false" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
 <%@ taglib uri="http://jawr.net/tags" prefix="jwr" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld" prefix="stripes" %>


<style>
<c:choose>
	<c:when test="${actionBean.size == 'sm'}">
		body{
			margin:0;
			width:210px;
			height:130px;
		}
		#container{
			border: 4px solid #148C75;
			border-radius:15px;
			background: #FFFFFF;
			margin:0;
			width:210px;
		}
		#srchWrd1{
			height:41px;
			width:155px;
			resize:none;
			overflow:auto;
			padding-right:5px;
			font-size:20px;
			border-radius:5px;
			border: 1px solid #bbdad2;
		}
		#searchcontents img{
			width:195px;
		}
		#buttonBar .button{
			float:left;
			padding:6px;
			cursor:pointer;
			width:55px;

		}

	</c:when>
	<c:when test="${actionBean.size == 'xsm'}">
		body{
			margin:0;
			width:175px;
			height:130px;
		}
		#container{
			border: 4px solid #148C75;
			border-radius:15px;
			background: #FFFFFF;
			margin:0;
			width:175px;
		}
		#srchWrd1{
			height:41px;
			width:123px;
			resize:none;
			overflow:auto;
			padding-right:5px;
			font-size:20px;
			border-radius:5px;
			border: 1px solid #bbdad2;
		}
		#searchcontents img{
			width:165px;
		}
		#buttonBar .button{
			float:left;
			padding:6px;
			cursor:pointer;
			width:55px;

		}
		#buttonBar{padding-left:15px;}
		form{margin-bottom:0px;}
	</c:when>
	<c:otherwise>
		body{
			margin:0;
			width:298px;
			height:130px;
		}
		#container{
			border: 4px solid #148C75;
			border-radius:15px;
			background: #FFFFFF;
			margin:0;
			width:290px;
		}
		#srchWrd1{
			height:41px;
			width:230px;
			resize:none;
			overflow:auto;
			padding-right:5px;
			font-size:20px;
			border-radius:5px;
			border: 1px solid #bbdad2;
		}
		#searchcontents img{
			width:250px;
		}
		#buttonBar .button{
			float:left;
			padding:6px;
			cursor:pointer;

		}
	</c:otherwise>
</c:choose>


	#searchcontents{
		padding-top:15px;
		padding-left:5px;
	}
	.hidden{
		display:none;
	}
	.sbutton{

		background-image:url(/static/images/searchwidget/ev_search.png);
		cursor:pointer;
		width:40px;
		height:41px;
		border: 2px solid #148C75;
		border-radius:5px;
		vertical-align:top;
	}

	.sbutton:hover{
		background-image:url(/static/images/searchwidget/ev_search_b.png);
	}

	#homepage{background-image:url(/static/images/searchwidget/ev_home.png);}
	#tutorials{background-image:url(/static/images/searchwidget/ev_tutorials.png);}
	#learnmore{background-image:url(/static/images/searchwidget/ev_learnmore.png);}
	#contactus{background-image:url(/static/images/searchwidget/ev_contactus.png);}

	.searchcomponentfullwrap{
		padding-top:5px;
		padding-left:2px;
	}
	#buttonBar{
		height:100px;
		background-color:#ececec;
		border-bottom-left-radius:15px;
		border-bottom-right-radius:15px;
	}

	.btnImage{
			width:60px;
		height:61px;
	}
	.btnTxt{
		color:#148C75;
		font-size:11px;
		text-align:center;
		font-family:arial;
	}
	.button:hover  .btnTxt{
	text-decoration:underline;
	}
	.button:hover #homepage{background-image:url(/static/images/searchwidget/ev_home_b.png);}
	.button:hover #tutorials{background-image:url(/static/images/searchwidget/ev_tutorials_b.png);}
	.button:hover #learnmore{background-image:url(/static/images/searchwidget/ev_learnmore_b.png);}
	.button:hover #contactus{background-image:url(/static/images/searchwidget/ev_contactus_b.png);}
	.initText{color:b7b7b8;}

</style>
    <div id="container">

        <div id="searchcontents" class="shadowbox" role="search" aria-labeledby="Expert Search"> <!-- gray box -->
        	<c:set var="mask" value="${actionBean.database}"></c:set>
        	<a href="http://www.engineeringvillage.com" target="_blank">
        	<c:choose>
        		<c:when test="${mask == 1}">
        			<img alt="Engineering Village - The information discovery platform of choice for the engineering community" src="/static/images/searchwidget/cpx<c:if test="${actionBean.size == 'xsm' }">_xsm</c:if>.jpg"/>
        		</c:when>
        		<c:when test="${mask == 2}">
        			<img alt="Engineering Village - The information discovery platform of choice for the engineering community" src="/static/images/searchwidget/inspec<c:if test="${actionBean.size == 'xsm' }">_xsm</c:if>.jpg"/>
        		</c:when>
        		<c:when test="${mask == 3}">
					<img alt="Engineering Village - The information discovery platform of choice for the engineering community" src="/static/images/searchwidget/cpx_inspec<c:if test="${actionBean.size == 'xsm' }">_xsm</c:if>.jpg"/>
        		</c:when>
        		<c:otherwise>
        			<img alt="Engineering Village - The information discovery platform of choice for the engineering community" src="/static/images/EV-logo.gif"/>
        		</c:otherwise>
        	</c:choose>
        	</a>

            <div id="searchform">
                <stripes:form  method="GET" action="/search/widgetSubmit.url" name="quicksearch" target="_blank">
	            <input type="hidden" name="CID" value="searchSubmit"/>
	            <%-- <input type="hidden" name="resetDataBase" value="${actionBean.database}"/> --%>
	            <input type="hidden" name="searchtype" value="Quick"/>
	            <input type="hidden" name="useType" value="widgetSearch"/>
	            <input type="hidden" name="database" value="${actionBean.database}"/>
	            <input type="hidden" name="swReferrer" value="${actionBean.referrer}"/>


	            <div class="searchcomponentfullwrap">
	                 <div class="searchforline">
	                	<label class="hidden" for="srchWrd1">Search Query</label>
	                    <input type="text" name="searchWord1" id="srchWrd1" class="initText" title="Search Query Text Box" value="Search for ..."></input>
			         <c:choose>
		        		<c:when test="${mask == 1}">
		        			<stripes:submit name="search" class="sbutton" value="" title="Search Compendex Database (Runs Search in Engineering Village)"/>&nbsp;
		        		</c:when>
		        		<c:when test="${mask == 2}">
		        			<stripes:submit name="search" class="sbutton" value="" title="Search Inspec Database (Runs Search in Engineering Village)"/>&nbsp;
		        		</c:when>
		        		<c:when test="${mask == 3}">
							<stripes:submit name="search" class="sbutton" value="" title="Search Compendex and Inspec Databases (Runs Search in Engineering Village)"/>&nbsp;
		        		</c:when>
		        		<c:otherwise>
		        			<stripes:submit name="search" class="sbutton" value="" title="Search Engineering Village"/>&nbsp;
		        		</c:otherwise>
		        	</c:choose>

	                 </div>

	            </div>

	            </stripes:form>
           </div> <!-- END searchform -->
        </div>  <!-- END searchcontents -->
		<div id="buttonBar">
		<c:if test="${actionBean.size == 'lg' }">
			<div class="button">
				<div class="btnImage" id="homePage" title="Go to Engineering Village home page (Opens in a new window)"><a href="http://www.engineeringvillage.com" target="new" ></a></div>
				<div class="btnTxt" title="Go to Engineering Village home page (Opens in a new window)">Home Page</div>
			</div>
		</c:if>
		<c:if test="${actionBean.size != 'xsm' }">
			<div class="button">
				<div class="btnImage" id="tutorials" title="Watch Engineering Village tutorials (Opens in a new window)"><a href="https://www.youtube.com/user/EngineeringVillage" target="new" ></a></div>
				<div class="btnTxt" title="Watch Engineering Village tutorials (Opens in a new window)">Tutorials</div>
			</div>
		</c:if>
			<div class="button">
				<div class="btnImage" id="learnmore" title="Learn more about Engineering Village (Opens in a new window)"><a href="http://www.elsevier.com/online-tools/engineering-village/using-engineering-village" target="new" ></a></div>
				<div class="btnTxt" title="Learn more about Engineering Village (Opens in a new window)">Learn More</div>
			</div>
			<div class="button">
				<div class="btnImage" id="contactUs" title="Contact us to learn more about Engineering Village (Opens in a new window)"><a href="http://www.elsevier.com/online-tools/engineering-village/contacts" target="new" ></a></div>
				<div class="btnTxt" title="Contact us to learn more about Engineering Village (Opens in a new window)">Contact Us</div>
			</div>
		</div>
        </div>  <!-- END container -->

        <div class="clear"></div>
    	<script type="text/javascript" src="/static/js/jquery/jquery-1.10.2.min.js"></script>
    	<script>
    		$(document).ready(function() {
    			$( "#srchWrd1" ).one( "click", function() {

    				$(this).val("");
    				$(this).css("color", "#000000");
    			});
    			$(".button").click(function(){
    				var url = $(this).find("a").attr("href");
    				window.open(url);
    			});
    		});
    	</script>
