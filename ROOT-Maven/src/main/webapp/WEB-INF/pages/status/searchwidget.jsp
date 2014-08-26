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
    </style>
    <script>
    	var sizes  = {
				large:{width:"298px",height:"230px"},
				format:downloadformat,
				displaytype:displaytype,
				baseaddress:baseaddress
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
	    			<option value="1">Compendex</option>
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
		    				<label for="size_xsm"><input type="radio" name="sizes" value="xsm" id="size_xsm" />Extra Small</label>
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
			<div>Preview:</div>
			<div id="widget_panel">
				<iframe src="" id="widget_preview" ></iframe>
			</div>
		</div>
    </div>
<div class="floatL padding10 "><iframe src="/widget/search.url?database=1" width="298px" height="230px" style="border:none;"></iframe><div>http://www.engineeringvillage.com//widget/search.url?database=1</div></div>
	    <div class="floatL padding10"><iframe src="/widget/search.url?database=2"width="298px" height="240px" style="border:none;"></iframe><div>http://www.engineeringvillage.com//widget/search.url?database=2</div></div>
		<div class="floatL padding10"><iframe src="/widget/search.url?database=3" width="298px" height="230px" style="border:none;"></iframe><div>http://www.engineeringvillage.com//widget/search.url?database=3</div></div>
		<div class="floatL padding10"><iframe src="/widget/search.url" width="298px" height="230px" style="border:none;"></iframe><div>http://www.engineeringvillage.com//widget/search.url</div></div>


    </stripes:layout-component>

</stripes:layout-render>