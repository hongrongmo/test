<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld" prefix="stripes" %>

			<div id="archives_box">
				<stripes:form name="archiveform" id="archiveform" action="/bulletins/archive.url" method="GET">
				<input type="hidden" name="database" id="database" value="${actionBean.database}" />

				<h2 class="title">Display Archives</h2>
				<p class="info">In order to view the Bulletins Archives, please select Database, Year of Publication and Category and click the "Display" button.</p>
				<p class="subtitle">Select Database</p>
				<ul class="select">
				    <c:if test="${actionBean.lit}"><li><input title="Select Database to display" type="radio" name="db" id="radLit"  value="1"<c:if test="${(empty actionBean.db) or (actionBean.db eq '1')}"> checked="true"</c:if><c:if test="${not actionBean.pat}"> disabled="disabled"</c:if>/><label for="radLit">EnCompassLIT</label></li></c:if>
					<c:if test="${actionBean.pat}"><li><input title="Select Database to display" type="radio" name="db" id="radPat"  value="2"<c:if test="${actionBean.db eq '2'}"> checked="true"</c:if><c:if test="${not actionBean.lit}"> disabled="disabled"</c:if>><label for="radPat">EnCompassPAT</label></li></c:if>
				</ul>

				<p class="subtitle">Select Category</p>
				<ul class="select">
					<li>
<c:if test="${not empty actionBean.litpage.selectcategoryoptions}">
					${actionBean.litpage.selectcategoryoptions}
</c:if>
<c:if test="${not empty actionBean.patpage.selectcategoryoptions}">
					${actionBean.patpage.selectcategoryoptions}
</c:if>
					</li>
				</ul>

				<p class="subtitle">Select Year of Publication</p>
				<ul class="select">
					<li>
					<stripes:select title="Select Year to display" name="yr" class="allyears">
                        <stripes:option value="2015">2015</stripes:option>
                        <stripes:option value="2014">2014</stripes:option>
                        <stripes:option value="2013">2013</stripes:option>
						<stripes:option value="2012">2012</stripes:option>
						<stripes:option value="2011">2011</stripes:option>
						<stripes:option value="2010">2010</stripes:option>
						<stripes:option value="2009">2009</stripes:option>
						<stripes:option value="2008">2008</stripes:option>
						<stripes:option value="2007">2007</stripes:option>
						<stripes:option value="2006">2006</stripes:option>
						<stripes:option value="2005">2005</stripes:option>
						<stripes:option value="2004">2004</stripes:option>
						<stripes:option value="2003">2003</stripes:option>
						<stripes:option value="2002">2002</stripes:option>
						<stripes:option value="2001">2001</stripes:option>
					</stripes:select>
					<select class="restoreyr" style="display:none">
					</select>
					</li>
				</ul>

				<p><input title="Display archives" type="submit" class="button" value="Display" style="margin-left: 0"/></p>



			</stripes:form>
			</div>

