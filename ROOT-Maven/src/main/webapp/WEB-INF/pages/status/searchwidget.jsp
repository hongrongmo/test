<%@ page language="java" session="false" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld" prefix="stripes" %>
    
<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp" pageTitle="Engineering Village - Search Widget Test Page">

    <stripes:layout-component name="csshead">
    <jsp:include page="include/customcss.jsp"></jsp:include>
    <style> 
    	#container{
    		height:350px;
    		text-align:center;
    	}
    </style>
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
	    <div class="floatL padding10 "><iframe src="/widget/search.url?database=1" width="298px" height="230px" style="border:none;"></iframe><div>http://www.engineeringvillage.com//widget/search.url?database=1</div></div>
	    <div class="floatL padding10"><iframe src="/widget/search.url?database=2"width="298px" height="240px" style="border:none;"></iframe><div>http://www.engineeringvillage.com//widget/search.url?database=2</div></div>
		<div class="floatL padding10"><iframe src="/widget/search.url?database=3" width="298px" height="230px" style="border:none;"></iframe><div>http://www.engineeringvillage.com//widget/search.url?database=3</div></div>
		<div class="floatL padding10"><iframe src="/widget/search.url" width="298px" height="230px" style="border:none;"></iframe><div>http://www.engineeringvillage.com//widget/search.url</div></div>
    
    </div>
   

    </stripes:layout-component>
    
</stripes:layout-render>