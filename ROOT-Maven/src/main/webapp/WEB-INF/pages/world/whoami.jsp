<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld"
	prefix="stripes"%>

<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp">

	<stripes:layout-component name="header">
		<jsp:include page="/WEB-INF/pages/include/headernull.jsp" />
	</stripes:layout-component>

	<stripes:layout-component name="contents">

		<div id="contentmain" style="margin-left: 15px">
			<p>
				<br /> ${actionBean.message}
			</p>


		</div>

		<div class="clear"></div>

		<br />

	</stripes:layout-component>
	<stripes:layout-component name="modal_dialog"></stripes:layout-component>
	<stripes:layout-component name="sessionexpiryhandler"></stripes:layout-component>
</stripes:layout-render>