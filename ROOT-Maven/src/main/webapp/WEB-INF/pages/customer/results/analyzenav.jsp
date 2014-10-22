<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>

<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld"	prefix="stripes"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp" pageTitle="Engineering Village - Quick Search Results">

	<stripes:layout-component name="header">
	<jsp:include page="/WEB-INF/pages/include/headernull.jsp" />
	</stripes:layout-component> 

	<stripes:layout-component name="csshead">
	<link href="/static/css/ev_results.css?v=${releaseversion}"" media="all" type="text/css" rel="stylesheet"></link>
	</stripes:layout-component>

	<stripes:layout-component name="contents">
	
      <p style="font-family:sans-serif; font-size:14px; text-align:left;">Search Query: <span style="font-weight:bold;">${actionBean.query}</span></p>
      <center>
				<table border="0" width="99%" cellspacing="0" cellpadding="0">
					<tr>
						<td>
							<c:if test="${not empty actionBean.analyzedata}">
								<img alt="Right-click on chart to save a copy of the image." border="0" width="550" height="500" src="/controller/servlet/BarChart?analyzedata=${actionBean.analyzedataencoded}&count=${actionBean.count}"/>
							</c:if>
						</td>
					</tr>
				</table>

      </center>

	</stripes:layout-component>

	<stripes:layout-component name="footer">
	</stripes:layout-component> 

	<stripes:layout-component name="jsbottom_custom">
	</stripes:layout-component>

	
</stripes:layout-render>
