<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld"
	prefix="stripes"%>

<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp">
<stripes:layout-component name="metahead">
	<META http-equiv="refresh" content="10;URL=${actionBean.redirectURL}">
</stripes:layout-component>
<stripes:layout-component name="header">
<script>
$(document).ready(function() {
		if(_gaq){
			GALIBRARY.createWebEventWithLabel("Redirect", "eBook Redirect","${actionBean.redirectURL}");
		}

});
</script>
<jsp:include page="/WEB-INF/pages/include/headernull.jsp" />
</stripes:layout-component>

<stripes:layout-component name="contents">
	<div><img /></div>
	<div id="contentmain"  style="margin-left:15px;padding-top:10px;text-align:left;font-size:14px;">
			<c:if test="${actionBean.redirectType == 'book'}">
				As of April 2014, Referex / eBooks are no longer available in Engineering Village. <br/>You will now be redirected to view this content on ScienceDirect.
				If you are not automatically redirected please click <a href="${ actionBean.redirectURL}">here.</a>
			</c:if>

	</div>

	<div class="clear"></div>

</stripes:layout-component>
<stripes:layout-component name="sessionexpiryhandler"></stripes:layout-component>
</stripes:layout-render>