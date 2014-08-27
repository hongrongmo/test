<%@ page language="java" session="false" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld" prefix="stripes" %>

<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp" pageTitle="Engineering Village - Search Widget Test Page">

    <stripes:layout-component name="csshead">
    <jsp:include page="include/customcss.jsp"></jsp:include>
    <style>
    	#container{
    		height:600px;
    		text-align:center;
    	}
    	#left_panel{
    		padding-left:25px;
    		width:400px;
    		height:500px;
    		border-right:solid black 2px;
    	}

    	#right_panel{
    		padding-left:25px;
    		width:400px;
    		height:500px;
    	}


    	#left_panel ul{
    		list-style:none;
    		float: right;
			padding-right: 16px;
    	}
    	#left_panel li{
    		float:left;
    	}
    	#left_panel label{
    		vertical-align:top;
    		padding-left:5px;
    	}
    	.heading{
    		font-weight:bold;
    		font-size:14px;
    	}
    </style>
    <script>
    	var sizes  = {
				lg:{width:"298px",height:"270px"},
				sm:{width:"220px",height:"270px"},
				xsm:{width:"186px",height:"270px"}
		};
    </script>
    </stripes:layout-component>

    <stripes:layout-component name="header">
        <jsp:include page="/WEB-INF/pages/include/headernull.jsp" />
    </stripes:layout-component>

    <stripes:layout-component name="ssourls"/>

<%-- **************************************** --%>
<%-- CONTENTS                                 --%>
<%-- **************************************** --%>
    <stripes:layout-component name="contents">
    <div id="container">

	    <jsp:include page="include/tabs.jsp"/>

	    <div>
	    	<div class="floatL" id="left_panel">
	    		<label for="db_options">Select Database(s):</label>
	    		<select id="db_options">
	    			<option value="1" selected>Compendex</option>
	    			<option value="2">Inspec</option>
	    			<option value="3">Compendex & Inspec</option>
	    			<option value="0">All</option>
	    		</select>
	    		<div>

		    		<ul>
		    			<li>
		    				<label>Select Size:</label>
		    			</li>
		    			<li>
		    				<label for="size_xsm"><input type="radio" name="sizes" value="xsm" id="size_xsm" checked="true" />Extra Small</label>
		    			</li>
		    			<li>
		    				<label for="size_sm"><input type="radio" name="sizes" value="sm" id="size_sm" />Small</label>
		    			</li>
		    			<li>
		    				<label for="size_lg"><input type="radio" name="sizes" value="lg" id="size_lg" />Large</label>
		    			</li>
		    		</ul>
	    		</div>
	    	</div>

	    </div>
		<div class="floatL" id="right_panel">
				<div class="heading">Preview:</div>
				<div id="widget_panel">
					<iframe src="" id="ev_searchwidget" ></iframe>
				</div>
				<div>
					<div class="heading">Code to use:</div>
					<div id="widget_code"></div>
				</div>
				<div class="heading" style="padding-top:10px;">Dimensions:</div>
				<div id="widget_dimensions"></div>

		</div>
    </div>

<script>
function renderWidget(){

	$("#widget_code").hide();
	$("#widget_dimensions").hide();

	var size = $("input[name='sizes']:checked").val();
	$("#ev_searchwidget").attr("src","${actionBean.context.request.scheme}://${actionBean.baseaddress}/widget/search.url?database=" + $("#db_options").val() + "&size=" + size);
	$("#ev_searchwidget").css("width",sizes[size].width);
	$("#ev_searchwidget").css("height",sizes[size].height);
	$("#ev_searchwidget").css("border","none");

	$("#widget_code").text($("#ev_searchwidget").parent().html());
	$("#widget_dimensions").html("<span class='heading'>Width: </span><span>"+sizes[size].width+"</span><br/><span class='heading'>Height: </span>"+sizes[size].height+"</span>");

	$("#widget_code").show();

	$("#widget_dimensions").show();
}
$(document).ready(function(){
	renderWidget();
	$("input[name='sizes']").click(function(){renderWidget();});
	$("#db_options").change(function(){renderWidget();});
});



</script>
    </stripes:layout-component>

</stripes:layout-render>