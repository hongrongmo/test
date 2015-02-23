<%@ page language="java" session="false" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/pages/include/taglibs.jsp" %>
<%-- HEADER --%>
<div id="header" style="width:100%; min-width: 100%">
	<div id="logoEV">
        <a href="/home.url" title="Engineering Village - The information discovery platform of choice for the engineering community"><img alt="Engineering Village - The information discovery platform of choice for the engineering community" src="/static/images/EV-logo.gif"/></a>
       	<c:if test="${not empty actionBean.maintenanceMsg}">
    		<div style="position:absolute; left: 320px; top: 28px; font-weight: bold; font-size; 12px; width:44%">${actionBean.maintenanceMsg }</div>
		</c:if>
		<c:if test="${not empty maintenanceMsg}">
    		<div style="position:absolute; left: 320px; top: 28px; font-weight: bold; font-size; 12px; width:44%;">${maintenanceMsg }</div>
		</c:if>
	</div>

	<div id="headerLink">
		<div class="clearfix" id="suites">&#160;</div>
	</div>

	<div class="navigation txtLarger clearfix">
		<ul title="top level navigation" class="nav main">
		</ul>
		<ul class="nav misc">
			<li class="nodivider" id='custom-logo-li' style='display:none'>
				<div id="custom-logo-top" align="center">
					<img id="custom-logo-img" border="0"/>
				</div>
			</li>
		</ul>
	</div>
</div>
<c:if test="${not empty actionBean.IE7Msg}">
  	<div id="ie7msg" style="text-align:left;display:none"><img src="/static/images/red_warning.gif" style="padding-right:5px;width:20px;margin-top:-3px; float:left"/><span id="ie7messageholder">${actionBean.IE7Msg}</span></div>
</c:if>
<c:if test="${not empty IE7Msg}">
  	<div id="ie7msg" style="text-align:left;display:none"><img src="/static/images/red_warning.gif" style="padding-right:5px;width:20px;margin-top:-3px; float:left"/><span id="ie7messageholder">${IE7Msg}</span></div>
</c:if>
