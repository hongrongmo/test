<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://jawr.net/tags" prefix="jwr" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld" prefix="stripes" %>

<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp" pageTitle="Engineering Village - eBook Search">

	<stripes:layout-component name="csshead">
	<jwr:style src="/bundles/quick.css"></jwr:style>
<!--[if IE]>
	<style type="text/css">
	#searchformwrap { margin-top: 15px; }
	#searchformsidebar { margin-top: 29px; }
	</style>
<![endif]-->
<!--[if ! IE]>
	.specialtop {top: 75px}
<![endif]-->
	</stripes:layout-component>

<%-- **************************************** --%>
<%-- CONTENTS                                 --%>
<%-- **************************************** --%>
	<stripes:layout-component name="contents">

	<div id="container">
	<div id="searchformwrap">
	<div id="searchformbox">

		<stripes:errors field="validationError"><div id="errormessage"><stripes:individual-error/></div></stripes:errors>

		<c:set var="searchtab" value="ebooksearch" scope="request"></c:set>
		<jsp:include page="parts/searchtabs.jsp"></jsp:include>

		<div id="searchcontents" class="shadowbox" role="search" aria-labeledby="eBookSearch">
			<div id="searchtipsbox">
				<ul>
					<li class="databases"><a href="/databases.jsp?dbid=pag" title="Learn more about databases" target="_blank" class="evdialog">Databases</a></li>
					<li><a href="/searchtips.jsp?topic=ebook" title="Search tips to help" target="_blank" class="notfirst evdialog">Search tips</a></li>
				</ul>
			</div>

			<div id="searchform">
			<stripes:form onsubmit="return searchValidation()" method="POST" action="/search/submit.url?CID=searchSubmit" name="quicksearch">
			<input type="hidden" name="database" value="131072" />
			<input type="hidden" name="yearselect" value="yearrange" />
			<input type="hidden" name="searchtype" value="Book"/>

			<%-- ******************************************************** --%>
			<%-- ******************************************************** --%>
			<%-- COLLECTION SELECTION                                       --%>
			<%-- ******************************************************** --%>
			<%-- ******************************************************** --%>
			<div class="searchcomponentfullwrap">
				<h2 class="searchcomponentlabel" style="float:none; margin-bottom: 7px;text-transform:uppercase;">Choose Collection</h2>
				<c:choose>
				<c:when test="${fn:length(actionBean.referexCheckboxes) == 1}">
				<div class="referexcheckall" style="float:none">
					<input type="checkbox" name="chkbx" value="" checked="checked" id="${actionBean.referexCheckboxes[0].abbrev}" style="vertical-align: middle;" disabled="disabled">
					<label class="SmBlackText" for="${actionBean.referexCheckboxes[0].abbrev}"><strong>${actionBean.referexCheckboxes[0].displayname}</strong></label>
				</div>
				</c:when>

				<c:otherwise>
				<fieldset>
				<div class="referexcheckall" style="float:none">
					<input type="checkbox"<c:if test="${empty actionBean.selcols}"> checked="checked"</c:if> onclick="change('all');" value="ALL" id="chkAll" style="vertical-align: middle;" name="allcol">
					<label class="SmBlackText" for="chkAll">All Referex Collections</label>
				</div>

				<ul class="referexcheckgroup">

				<c:forEach items="${actionBean.referexCheckboxes}" var="checkbox">
					<li>
						${checkbox}
					</li>
				</c:forEach>

				</ul>
				</fieldset>
				</c:otherwise>
				</c:choose>

				<div class="clear"></div>

			</div>


			<%-- ******************************************************** --%>
			<%-- ******************************************************** --%>
			<%-- SEARCH TERMS                                             --%>
			<%-- ******************************************************** --%>
			<%-- ******************************************************** --%>

			<div class="searchcomponentfullwrap" style="padding-bottom:0">
				<h2 class="searchcomponentlabel">SEARCH FOR</h2>
				<div class="searchforline">
					<label class="hidden" for="srchWrd1">Search 1</label>
					<stripes:text name="searchWord1" id="srchWrd1" onblur="javascript:updateWinds(); " class="searchword" title="Search Text Box 1"/>
					<span>in</span>
					<label class="hidden" for="sect1">Search Within 1</label>
                    <stripes:select size="1" name="section1" id="sect1" title="Search Within Dropdown 1">
                        <stripes:options-collection collection="${actionBean.section1opts}" label="value" value="name"/>
                    </stripes:select>
					<a	href="${actionBean.helpUrl}#ebook_search_steps.htm" class="helpurl" alt="Learn more about searching for eBook" title="Learn more about searching for eBook"><jwr:img
						src="/static/images/i.png" border="0" styleClass="infoimg" align="bottom" alt=""/></a>
				</div>

				<div class="searchforconnector">
				<label class="hidden" for="cbnt1">Combine Terms 1</label>
					<stripes:select	size="1" name="boolean1" title="Combine Terms With 1" id="cbnt1">
						<stripes:option value="AND">AND</stripes:option>
						<stripes:option value="OR">OR</stripes:option>
						<stripes:option value="NOT">NOT</stripes:option>
					</stripes:select>
				</div>
				<div class="searchforline">
				<label class="hidden" for="srchWrd2">Search 2</label>
                    <stripes:text name="searchWord2" id="srchWrd2" onblur="javascript:updateWinds(); " class="searchword" title="Search Text Box 2"/>
					<span>in</span>
					<label class="hidden" for="sect2">Search Within 2</label>
					<stripes:select	size="1" name="section2" id="sect2" title="Search Within Dropdown 2">
	                    <stripes:options-collection collection="${actionBean.section2opts}" label="value" value="name"/>
					</stripes:select>
				</div>

				<div class="searchforconnector">
				<label class="hidden" for="cbnt2">Combine Terms 2</label>
					<stripes:select	size="1" name="boolean2" title="Combine Terms With 2" id="cbnt2">
						<stripes:option value="AND">AND</stripes:option>
						<stripes:option value="OR">OR</stripes:option>
						<stripes:option value="NOT">NOT</stripes:option>
					</stripes:select>
				</div>
				<div class="searchforline">
					<label class="hidden" for="srchWrd3">Search 3</label>
                    <stripes:text name="searchWord3" id="srchWrd3" onblur="javascript:updateWinds(); " class="searchword" title="Search Text Box 3"/>
					<span> in </span>
					<label class="hidden" for="sect3">Search Within 2</label>
					<stripes:select	size="1" name="section3" title="Search Within Dropdown 3" id="sect3">
                        <stripes:options-collection collection="${actionBean.section3opts}" label="value" value="name"/>
					</stripes:select>
				</div>
				<div class="searchforline" style="padding-right:54px; *padding-right:51px">
         <div class="floatR" style="">
          <input type="submit" style="margin-right:2px" value="Search" title="Submit Search" class="button"></input>
          <input id="ebook-search-reset" type="reset" value="Reset" title="Reset" class="button"></input>
         </div>
					<div class="clear"></div>
				</div>
			</div>

		</stripes:form>

			<div class="searchcomponentseparator" style="padding:0; *margin-bottom:0"><hr/></div>

			<%-- ******************************************************** --%>
			<%-- ******************************************************** --%>
			<%-- BROWSE                                                   --%>
			<%-- ******************************************************** --%>
			<%-- ******************************************************** --%>
			<div class="searchcomponentfullwrap">
				<h2 class="searchcomponentlabel" style="float:none; margin-bottom: 12px;text-transform:uppercase">Browse books by collection or subject&nbsp;&nbsp;<a	href="${actionBean.helpUrl}#Ebook_search_steps.htm#browse_ebook" alt="Learn more about browsing eBooks" title="Learn more about browsing eBooks" class="helpurl"><jwr:img
						src="/static/images/i.png" border="0" styleClass="infoimg" align="bottom" alt=""/></a></h2>

				<div id="ebookbrowsenavwrap">
				<div id="ebookbrowsenavright">
					<c:forEach items="${actionBean.ebooklist}" var="ebookitem" varStatus="status">
					<ul class="ebooklist${status.count} listitem"<c:if test="${status.count gt 1}"> style="display:none"</c:if>>
						<li><b><a href="/search/results/quick.url?CID=quickSearchCitationFormat&database=${actionBean.database}&sortdir=up&sort=stsort&yearselect=yearrange&searchtype=Book&col=${ebookitem.shortname}">All (${ebookitem.subcount})</a></b></li>
						<c:forEach items="${ebookitem.searchlinks}" var="searchlink">
						<li>${searchlink}</li>
						</c:forEach>
					</ul>
					</c:forEach>
				</div>
				<ul id="ebookbrowsenav">
					<c:forEach items="${actionBean.ebooklist}" var="ebookitem" varStatus="status">
					<c:choose>
					<c:when test="${status.count eq 1}">
					<li id="ebooklist${status.count}" class="active">${ebookitem.displayname}</li>
					</c:when>
					<c:otherwise>
					<li id="ebooklist${status.count}">${ebookitem.displayname}</li>
					</c:otherwise>
					</c:choose>
					</c:forEach>
				</ul>

				</div>

			</div>

			</div> <!-- END searchform -->
		</div>	<!-- END searchcontents -->
		<br class="clear"/>
		</div> <!-- END searchformbox -->

		<jsp:include page="parts/history.jsp"></jsp:include>

		</div> <!-- END searchformwrap -->

		<%-- ******************************************************** --%>
		<%-- ******************************************************** --%>
		<%-- SIDEBAR ITEMS                                            --%>
		<%-- ******************************************************** --%>
		<%-- ******************************************************** --%>
		<div style="float:right">
		<jsp:include page="parts/sidebar.jsp"></jsp:include>
		</div>

		</div>  <!-- END container -->

		<div class="clear"></div>


	</stripes:layout-component>

	<stripes:layout-component name="jsbottom_custom">
	<jwr:script src="/bundles/ebook.js"></jwr:script>
	<script type="text/javascript">
	$(function(){
		$("#ebook-search-reset").click(function(e){
			e.preventDefault();
			window.location = "/search/ebook.url?database=131072";
			return false;
		});
		$("#ebookbrowsenav li").click(function () {
			$(this).parent().find("li").removeClass('active');
			this.className="active";
			$("#ebookbrowsenavright .listitem").hide();
			$("#ebookbrowsenavright ul." + this.id).show();
			$("#ebookbrowsenavright ul." + this.id).animate({scrollTop:'0px'});
			$("div#ebookbrowsenavright").scrollTop(0);
		});
	});
	</script>

	<jsp:include page="parts/search_common_js.jsp"></jsp:include>
	</stripes:layout-component>


</stripes:layout-render>
