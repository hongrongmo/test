<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld"
    prefix="stripes"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib prefix="x" uri="http://java.sun.com/jsp/jstl/xml" %>

<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp"
    pageTitle="Engineering Village - Quick Search Results">

    <stripes:layout-component name="csshead">
        <link href="/static/css/ev_results.css?v=${releaseversion}"" media="all"
            type="text/css" rel="stylesheet" />
    </stripes:layout-component>

    <stripes:layout-component name="header">
        <jsp:include page="/WEB-INF/pages/include/headerprint.jsp" />
    </stripes:layout-component>

    <stripes:layout-component name="contents">

<div id="delivery_content">
    <ul class="errors" id="jserrors">
    <stripes:errors>
         <stripes:errors-header></stripes:errors-header>
         <li><stripes:individual-error/></li>
         <stripes:errors-footer></stripes:errors-footer>
    </stripes:errors>
    </ul>

<c:choose>
    <c:when test="${(empty actionBean.basket || actionBean.basket.basketSize eq 0) && (empty actionBean.docidlist) && (empty actionBean.folderid)}">
            <table border="0" width="100%" cellspacing="0" cellpadding="0" bgcolor="#FFFFFF">
            <tr><td valign="top" height="15" colspan="3"><img src="/static/images/spacer.gif" border="0"/></td></tr>
            <tr>
            <td valign="top" width="20"><img src="/static/images/spacer.gif" border="0" width="20"/></td>
            <td valign="top" colspan="2"><h2>Print Records</h2></td>
            </tr>
            <tr><td valign="top" height="2" colspan="3"><img src="/static/images/spacer.gif" border="0"/></td></tr>
            <tr>
            <td valign="top" width="20"><img src="/static/images/spacer.gif" border="0" width="20"/></td>
            <td valign="top">
            <span CLASS="MedBlackText">
            You did not select any records to print. Please select records from the search results and try again.
            </span>
            </td>
            <td valign="top" width="10"><img src="/static/images/spacer.gif" border="0" width="10"/></td>
            </tr>
            </table>    
    </c:when>
    
	<c:otherwise>
<stripes:form name="printdocument" id="printdocument" method="get" action="/delivery/print/display.url">

	<stripes:hidden name="sessionid"/>
	<stripes:hidden name="handlelist" value="${actionBean.handlelist}"/>
	<stripes:hidden name="docidlist" value="${actionBean.docidlist}"/>
	<stripes:hidden name="folderid" value="${actionBean.folderid}"/>
	
	<div style="margin: 5px 7px">
		<div>
			Record output: 
			<stripes:select name="displayformat" onchange="displayFormat();">
			 <stripes:option value="citation">Citation</stripes:option>
			 <stripes:option value="abstract">Abstract</stripes:option>
			 <stripes:option value="detailed">Detailed record</stripes:option>
			</stripes:select>
		</div>
		<div>NOTE: Your selected records (to a maximum of 500) will
			be kept until your session ends. However, to delete them after
			this task:</div>
		<ul style="margin: 0 0 0 30px; padding: 0;">
			<li style="padding-top: 5px;">Return to the Search results
				page and click Delete Selected Records, or</li>
			<li style="padding-top: 5px;">Go to the Selected records
				page and click Remove All, or</li>
			<li style="padding-top: 5px;">Click the End session link at
				the top of the page</li>
		</ul>
	</div>

	<div class="hr"
		style="color: #0156AA; background-color: #0156AA; height: 2px; margin-left: 7px; margin-right: 7px; width: 98%">
		<hr>
	</div>

	<center>
		<table border="0" width="99%" cellspacing="0" cellpadding="0">
			<c:forEach var="doc" items="${actionBean.docs}" varStatus="status">
			${doc.transform}
			</c:forEach>
		</table>
	</center>
</stripes:form>
	</c:otherwise>
</c:choose>

</div>

</stripes:layout-component>

    <stripes:layout-component name="footer">
        <div class="hr"
            style="margin: 20px 0 7px 0; color: #d7d7d7; background-color: #d7d7d7; height: 2px;">
            <hr />
        </div>
        <jsp:include page="/WEB-INF/pages/include/copyright.jsp" />
    </stripes:layout-component>

    <stripes:layout-component name="jsbottom_custom">
        <script language="Javascript" src="/static/js/displayFormat.js?v=${releaseversion}"></script>
	</stripes:layout-component>
    
</stripes:layout-render>

