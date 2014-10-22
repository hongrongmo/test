<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld"
	prefix="stripes"%>

<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp" pageTitle="Engineering Village - Register">

	<stripes:layout-component name="csshead">
	<link href="/static/css/ev_personalaccount.css?v=${releaseversion}" media="all" type="text/css" rel="stylesheet"></link>
	</stripes:layout-component>

	<stripes:layout-component name="contents">

	<div class="marginL15">

<ul class="errors" id="jserrors">
<stripes:errors>
     <stripes:errors-header></stripes:errors-header>
     <li><stripes:individual-error/></li>
     <stripes:errors-footer></stripes:errors-footer>
</stripes:errors>
</ul>

        ${actionBean.pagecontent}

		</div>
	</stripes:layout-component>
	
	<stripes:layout-component name="jsbottom_custom">
	<jsp:include page="parts/commonjs.jsp"></jsp:include>
	</stripes:layout-component>	

</stripes:layout-render>