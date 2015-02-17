<%@ page language="java" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<%@ page session="false"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld" prefix="stripes"%>

<c:set var="carsresponse" value="${actionBean.context.carsResponse}"/>
<c:set var="user" value="${actionBean.context.userSession.user}"/>
<c:set var="pathchoice" value="${carsresponse.templateName eq 'CARS_PATH_CHOICE'}"/>
<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp" pageTitle="Engineering Village - Login">

    <stripes:layout-component name="csshead">
    <link href="/static/css/ev_personalaccount.css?v=${releaseversion}" media="all" type="text/css" rel="stylesheet"></link>
    <style>
        #stripes-messages ul{list-style:none;padding:0px;}
        p.cars_register {display:none}
    </style>
	</stripes:layout-component>
    
    <stripes:layout-component name="customjs">
        <c:if test="${actionBean.runlevel eq 'prod' or actionBean.runlevel eq 'release'}">
        <!-- Google Tag Manager -->
        <noscript>
            <iframe src="//www.googletagmanager.com/ns.html?id=GTM-PRWZ3F" height="0" width="0" style="display: none; visibility: hidden"></iframe>
        </noscript>
        <script>
            (function(w, d, s, l, i) {
                w[l] = w[l] || [];
                w[l].push({
                    'gtm.start' : new Date().getTime(),
                    event : 'gtm.js'
                });
                var f = d.getElementsByTagName(s)[0], j = d.createElement(s), dl = l != 'dataLayer' ? '&l='
                        + l
                        : '';
                j.async = true;
                j.src = '//www.googletagmanager.com/gtm.js?id=' + i + dl;
                f.parentNode.insertBefore(j, f);
            })(window, document, 'script', 'dataLayer', 'GTM-PRWZ3F');
        </script>
        <!-- End Google Tag Manager -->
        </c:if>
    </stripes:layout-component>
    
    <stripes:layout-component name="header">
    <jsp:include page="/WEB-INF/pages/include/headernull.jsp" />
    </stripes:layout-component> 

    <stripes:layout-component name="contents">

    <style type="text/css"></style>

    <div class="marginL15">
    <div id="contentmain">

    <c:if test="${not empty actionBean.message}">
    <div class="paddingT5">${actionBean.message}</div>
    </c:if>
    <div class="paddingT5"  id="stripes-messages">
    <stripes:messages/>
    </div>

    <c:choose>
    <%-- ************************************************************************************** --%>
    <%-- If pageContent is empty something is wrong! --%>
    <%-- ************************************************************************************** --%>
    <c:when test="${empty carsresponse.pageContent}">
    <h3>System Error</h3>
    <div class="hr" style="height: 2px; background-color: #D7D7D7; margin: 0 10px 10px 0"><hr></div>
    <p>Sorry, a system error has occurred, and your request cannot be completed.</p>
    <br/>
    </c:when>

    <%-- ************************************************************************************** --%>
    <%-- Otherwise just show the transformed CARS template via the pageContent --%>
    <%-- ************************************************************************************** --%>
    <c:otherwise>    

    <div id="welcomewrap"> 

        <h1><span>Welcome to Engineering Village</span></h1>
		<div id="welcome_top">
            <div id="welcome_header">
                 <p>Engineering Village is the premier web-based discovery   platform meeting the information needs of the engineering   community. By coupling powerful search tools, an intuitive user interface and essential content sources, Engineering Village has    become the globally accepted source of choice for engineers, engineering students, researchers and information professionals.</p>
                 </br>
            </div>
           
        </div>
			
        <div class="clear"></div>
        <div id="welcome_login">
            <stripes:errors>
                 <stripes:errors-header><ul class="errors"></stripes:errors-header>
                 <li><stripes:individual-error/></li>
                 <stripes:errors-footer></ul></stripes:errors-footer>
            </stripes:errors>
            ${actionBean.context.carsResponse.pageContent}
        </div>
        
        <div class="clear"></div>

        <div class="hr" style="background: #d7d7d7; margin: 5px 0; width: 1015px"><hr/></div>

    
		<c:set value="${actionBean.context.userSession.userTextZones}" var="textzones"/>
		<c:if test="${not empty textzones and not empty textzones['WHATS_NEW']}">
		            <div id="whatsnew" title="What's New">
		                <h2>What's New: </h2>
		                ${textzones['WHATS_NEW']}
		            </div>
		</c:if>
        <div id="learnabout">
            <h2>Engineering Village provides:</h2>
            <dl id="leftside">
                <dd>
                    <ul>
                        <li>Combined database searching of all databases including deduplication.</li>
                        <li>Personalized email alerts.</li>
                        <li>The ability to save searches and create personalized folders.</li>
                        <li>Quick &amp; Expert Search options, all of which allow you to save and combine searches.</li>
                        <li>The ability to choose preferred output formats (citation, abstract or detailed) for Selected Record sets, which can then be viewed, printed, saved, downloaded or emailed.</li>
                        <li>OpenURL linking to Endeavor LinkFinder Plus, Ex Libris SFX, Serials Solutions Article Linker, and Innovative Interfaces Web Bridge for local holdings checking and full text option presentation.</li>
                    </ul>
                </dd>
            </dl>
            <dl id="rightside">
                <dd>
                    <ul>
                        <li>Links to full-text using CrossRef.</li>
                        <li>Links to document delivery services.</li>
                        <li>Context sensitive help.</li>
                        <li>Reference Services: Ask a Librarian &amp; Ask an Engineer.</li>
                        <li>RSS feeds and faceted searching.</li>
                        <li>Tags &amp; Groups.</li>
                    </ul>
                </dd>
            </dl>
        </div>
    </div>
    </c:otherwise>
    </c:choose>
  </div>

  <div class="clear"></div>

</div>

</stripes:layout-component>

    <stripes:layout-component name="modal_dialog"></stripes:layout-component>
    <stripes:layout-component name="modal_dialog_2"></stripes:layout-component>
    <stripes:layout-component name="sessionexpiryhandler"></stripes:layout-component>
    
</stripes:layout-render>