<%@ page language="java" session="false" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld" prefix="stripes" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
	<title>Engineering Village - System Error</title>
	<link type="image/x-icon" href="/static/images/engineering_village_favicon.gif" rel="SHORTCUT ICON"></link>
	<link href="/static/css/ev_txt.css?v=${releaseversion}" media="all" type="text/css" rel="stylesheet"></link>
	<link href="/static/css/ev_common_sciverse.css?v=${releaseversion}" media="all" type="text/css" rel="stylesheet"></link>

	<script type="text/javascript" src="/static/js/jquery/jquery-1.10.2.min.js?v=${releaseversion}"></script>
    <script type="text/javascript" src="/static/js/galibrary.js"></script>
	<script type="text/javascript">
    // Initialize GA
    var pageevents = [<c:forEach items="${webAnalyticsEvent}" var="webEvent" varStatus="status">{category:'${webEvent.category}', action: '${webEvent.action}', label: '${webEvent.label}'}<c:if test='!status.last'>,</c:if></c:forEach>];
    GALIBRARY.init(
        ["${actionBean.context.googleAnalyticsAccount}", "${usersession.user.account.accountName}", "${usersession.user.individuallyAuthenticated}"],
        pageevents);
    </script>
	</head>
<body>

<jsp:include page="/WEB-INF/pages/include/headernull.jsp" />
<div id="container" class="marginL15">
	<h3>System Error</h3>
	<p><c:choose><c:when test="${not empty message}">${message}</c:when><c:otherwise>Sorry, a system error has occurred, and your request cannot be completed.</c:otherwise></c:choose></p>
    <!--Error Message: ${exception} -->
</div>

<jsp:include page="/WEB-INF/pages/include/footer.jsp" />
<jsp:include page="/WEB-INF/pages/include/copyright.jsp" />

</body>
</html>

