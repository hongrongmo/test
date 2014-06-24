<%@ page language="java" session="false" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ taglib uri="http://jawr.net/tags" prefix="jwr" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld" prefix="stripes" %>
<%@ page import="org.ei.session.*"%>
<%@ page import="org.ei.domain.personalization.*"%>

<stripes:layout-definition>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"></meta>
    <stripes:layout-component name="metahead"/>
    <title>${pageTitle}</title>
    <link type="image/x-icon" href="/static/images/engineering_village_favicon.gif" rel="SHORTCUT ICON"></link>


<stripes:layout-component name="cssheadstandard">
<jwr:style src="/bundles/standard.css"></jwr:style>
</stripes:layout-component>
<jwr:script src="/bundles/standard.js" useRandomParam="true"></jwr:script>

<c:if test="${actionBean.context.userSession.user.userPreferences.featureHighlight}">
	<jwr:script src="/bundles/tooltipster.js"></jwr:script>
	<jwr:style src="/bundles/tooltipster.css"></jwr:style>
</c:if>
<stripes:layout-component name="csshead"/>



</head>
<body>

<stripes:layout-component name="ssourls">
<c:if test="${actionBean.context.userSession.user.SSOURLInvoked}">
<c:forEach var="urls" items="${actionBean.context.userSession.user.ssoURLs}">
    <iframe class="displayNone" src='<c:out value="${urls}"/>'></iframe>
</c:forEach>
${actionBean.context.userSession.user.setSSOURLInvoked(false)}
</c:if>
</stripes:layout-component>

<stripes:layout-component name="header">
<jsp:include page="/WEB-INF/pages/include/header.jsp" />
</stripes:layout-component>

<div id="container">

<stripes:layout-component name="contents" />
</div>


<stripes:layout-component name="usersessiondump">
<c:set var="usersession" value="${actionBean.context.userSession}"/>
<c:if test="${not empty usersession and not empty usersession.user}">

<!--
    User information: ${usersession.user.firstName}  ${usersession.user.lastName}
    <c:if test="${null != actionBean.context.existingSession}">
        Session ID: ${actionBean.context.existingSession.id}
    </c:if>
    =========================================================================================
    Username: ${usersession.user.username}
    Email: ${usersession.user.email}
    Webuserid: ${usersession.user.webUserId}
    Profile ID: ${usersession.user.profileId}

    Access: ${usersession.user.userAccess}
    Allowed Reg Type: ${usersession.user.allowedRegType}
    Cred type: ${usersession.user.credType}
    Anonymity: ${usersession.user.userAnonymity}

    IP Address: ${usersession.user.ipAddress}
    Start Page: ${usersession.user.startPage}
    Default DB: ${usersession.user.defaultDB}
    Customer ID: ${usersession.user.customerID}
    Cartridge: ${usersession.user.cartridgeString}

    ACCOUNT INFORMATION
    =========================================================================================
    Account Name: ${usersession.user.account.accountName}
    Account Number: ${usersession.user.account.accountNumber}
    Account ID: ${usersession.user.account.accountId}

    Dept name: ${usersession.user.account.departmentName}
    Dept ID: ${usersession.user.account.departmentId}
 -->

 </c:if>
</stripes:layout-component>

<stripes:layout-component name="footer">
<jsp:include page="/WEB-INF/pages/include/footer.jsp" />
<jsp:include page="/WEB-INF/pages/include/copyright.jsp" />
</stripes:layout-component>

<stripes:layout-component name="jsbottom">

<script type="text/javascript" src="/static/js/galibrary.js"></script>

<script type="text/javascript">
    // Initialize GA
    var pageevents = [<c:forEach items="${webAnalyticsEvent}" var="webEvent" varStatus="status">{category:'${webEvent.category}', action: '${webEvent.action}', label: '${webEvent.label}'}<c:if test='${!status.last}'>,</c:if></c:forEach>];
    GALIBRARY.init(
        ["${actionBean.context.googleAnalyticsAccount}", "${usersession.user.account.accountName}", "${usersession.user.individuallyAuthenticated}"],
        pageevents);


    //custom fliplogin to override cars!! ugh
      function flipLogin(button, from) {
      	return false;
      }

    </script>

</stripes:layout-component>


<stripes:layout-component name="jsbottom_custom"/>

</body>
<script>var release = "5.15v2";</script>
	<div style="display:none;" id="modalHTMLcontainer">
	<div id="modalmsg">
		<jsp:include page="/WEB-INF/pages/popups/marketingMessage.jsp" />
	</div>
	<stripes:layout-component name="modal_dialog">
		<c:if test="${actionBean.context.userSession.user.userPreferences.modalDialog && !actionBean.context.userSession.user.userPreferences.modalDialog2}">
			<script>
				if((!$.cookie("ev_mm_dontshow") || release != $.cookie("ev_mm_dontshow")) && (!$.cookie("ev_mm_shown") || release != $.cookie("ev_mm_shown"))){
			
					GALIBRARY.createWebEventWithLabel(['_trackEvent', 'Dialog Open', 'What\'s New', 'Auto Show']);
					TINY.box.show({html:document.getElementById("modalmsg"),clickmaskclose:false,width:900,height:500,close:true,opacity:20,topsplit:3,closejs:function(){closeX();}});
					//tell GA that we showed it
			
				}
			
			</script>
		</c:if>
	</stripes:layout-component>
</div>
<stripes:layout-component name="modal_dialog_2">
	<c:if test="${actionBean.context.userSession.user.userPreferences.modalDialog2}">
			<div style="display:none;" id="modalRMHTMLcontainer">
				<div id="modalrmmsg">
					<jsp:include page="/WEB-INF/pages/popups/referexMessage.jsp" />
				</div>
				<script>
				if((!$.cookie("ev_rm_dontshow") || release != $.cookie("ev_rm_dontshow")) && (!$.cookie("ev_rm_shown") || release != $.cookie("ev_rm_shown"))){
	
					GALIBRARY.createWebEventWithLabel(['_trackEvent', 'Dialog Open', 'Referex', 'Auto Show']);
					TINY.box.show({html:document.getElementById("modalrmmsg"),clickmaskclose:false,width:600,height:250,close:true,opacity:20,topsplit:3,closejs:function(){closeRMX();}});
					//tell GA that we showed it
	
				}
				</script>
			</div>
	</c:if>
</stripes:layout-component>
<stripes:layout-component name="carsoverride">
<jwr:style src="/bundles/carsoverride.css"></jwr:style>
<style></style>

</stripes:layout-component>

</html>
</stripes:layout-definition>
