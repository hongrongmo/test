<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld" prefix="stripes"%>

<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp"
	pageTitle="Engineering Village - Reference Services">

	<stripes:layout-component name="csshead">
	<link href="/static/css/ev_askanexpert.css?v=${releaseversion}" media="all" type="text/css" rel="stylesheet"></link>
	</stripes:layout-component>

	<stripes:layout-component name="contents">

	<div id="contents">
<center>
		<a id="top" href="top"></a>
		<div id="askwrapper">

		<div id="askanengineer" class="expertbox shadowbox" title="Ask an Engineer">
			<p class="title">Ask an Engineer</p>
			<img class="expertimg"
				src="/static/images/ae/1.png" title="" border="0" id="img_engineer"/>
			<div class="body">
				<p style="font-weight: bold">Our Senior Engineers can help you:</p>
				<ul>
					<li>Answer technical engineering questions</li>
					<li>Identify appropriate related resources</li>
				</ul>
			</div>
		</div>

		<div id="askaproductspecialist" class="expertbox shadowbox" title="Ask a Product Specialist">
			<p class="title">Ask a Product Specialist</p>
			<img class="expertimg"
				src="/static/images/ae/2.png" title="" border="0" id="img_specialist"/>
			<div class="body">
				<p style="font-weight: bold">Our Product Specialist can help you:</p>
				<ul>
					<li>Learn to use EV features effectively</li>
					<li>Analyze results</li>
					<li>Register for online seminars or trainings</li>
				</ul>
			</div>
		</div>

		<div id="askalibrarian" class="expertbox shadowbox" style="margin-right:0px" title="Ask a Librarian">
			<p class="title">Ask a Librarian</p>
			<img class="expertimg"
				src="/static/images/ae/3.png" title="" border="0" id="img_librarian"/>
			<div class="body">
				<p style="font-weight: bold">A librarian can help you:</p>
				<ul>
					<li>Formulate searches</li>
					<li>Locate a content source (book, journal, conference paper, etc.)</li>
					<li>Find additional resources</li>
				</ul>
			</div>
		</div>

		<div class="clear"></div>
		</div>

		<jsp:include page="parts/askanengineer.jsp"></jsp:include>

		<jsp:include page="parts/askaproductspecialist.jsp"></jsp:include>

		<jsp:include page="parts/askalibrarian.jsp"></jsp:include>
</center>
	</div>
	</stripes:layout-component>

	<stripes:layout-component name="jsbottom_custom">
	<SCRIPT SRC="/static/js/ReferenceServices.js?v=${releaseversion}"></script>
    <script type="text/javascript">
    GALIBRARY.createWebEventWithLabel('AskAnExpert', 'Home', '');
    </script>
    </stripes:layout-component>


</stripes:layout-render>