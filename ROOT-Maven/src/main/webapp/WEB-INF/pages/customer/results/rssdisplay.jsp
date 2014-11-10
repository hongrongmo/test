<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://jawr.net/tags" prefix="jwr"%>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld"
	prefix="stripes"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<%-- NOTE: "pageTitle" attribute for layout set in action bean --%>
<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp" pageTitle="Engineering Village RSS Feed">

	<stripes:layout-component name="header">
		<jsp:include page="/WEB-INF/pages/include/headernull.jsp" />
	</stripes:layout-component>

	<stripes:layout-component name="contents">

		<div id="rssdisplay_container" class="padding10">
			<p id="rssdisplay_feedurl" style="word-wrap: break-word; font-weight: bold;">${actionBean.feedurl}</p>
            <br />

			<p id="rssdisplay_info1">
				Copy and paste the link to your RSS reader. Each week when the
				database is updated any new results matching your query will be
				displayed in your RSS reader. Up to 400 titles will be delivered
				with each update. <br /> <br />For more information on RSS see the
				Help section.
				<a class="helpurl" title="Learn more about RSS" alt="Learn more about RSS" href="${actionBean.helpUrl}#RSS_Feature.htm">
				    <img class="infoimg" border="0" align="bottom" alt="" src="/static/images/i.png">
				</a>
			</p>
            <br />

			<p id="rssdisplay_info2">If you use a common RSS reader, you may click your choice
				below to subscribe.</p>
            <br />
            <br />

			<p id="rssdisplay_feedly" class="floatL">
				<a target="blank" id="rssdisplay_feedly_link"
					href="http://cloud.feedly.com/#subscription%2Ffeed%2F${actionBean.encodedfeedurl}"
					title=""><img id="feedlyFollow"
					src="http://s3.feedly.com/img/follows/feedly-follow-rectangle-volume-medium_2x.png"
					alt="follow us in feedly" width="71" height="28" title="" style=""></a>
				&nbsp;&nbsp;
			</p>

			<p id="rssdisplay_yahoo" class="floatL paddingL10">
				<a id="rssdisplay_yahoo_link" target="_blank"
					href="http://add.my.yahoo.com/content?.intl=us&amp;url=${actionBean.encodedfeedurl}"><img
					src="http://us.i1.yimg.com/us.yimg.com/i/us/my/addtomyyahoo4.gif"
					width="91" height="17" border="0" alt="Add to My Yahoo!"></a>&nbsp;&nbsp;
			</p>

			<p id="rssdisplay_bloglines" class="floatL paddingL10">
				<a id="rssdisplay_bloglines_link" target="_blank"
					href="http://www.bloglines.com/sub/${actionBean.encodedfeedurl}"><img
					src="http://www.bloglines.com/images/sub_modern5.gif"
					alt="Subscribe with Bloglines" border="0"></a>
			</p>

			<div class="clear"></div>
		</div>
	</stripes:layout-component>

</stripes:layout-render>