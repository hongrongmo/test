<%@ page language="java" session="false" pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/pages/include/taglibs.jsp" %>
<%-- HEADER --%>
<div id="header" style="width:100%; min-width: 100%">
	<div id="logoEV">
        <a href="/home.url" title="Home"><img src="/static/images/EV-logo.gif"/></a>
        <div style="float:right; padding: 7px">
       		<A href="javascript:window.print()"><IMG src="/static/images/Print.png" border="0"/></A>&#160;&#160;
		</div>
		<div class="clear"></div>
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
