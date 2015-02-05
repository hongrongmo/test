<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://jawr.net/tags" prefix="jwr" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld" prefix="stripes" %>

<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp" pageTitle="Engineering Village - Thesaurus">

	<stripes:layout-component name="csshead">
	<jwr:style src="/bundles/quick.css"></jwr:style>
<!--[if IE 6]>
	<link href="/static/css/ev_ie6.css?v=${releaseversion}" media="all" type="text/css" rel="stylesheet"></link>
<![endif]-->
<!--[if IE]>
	<style type="text/css">
	#searchformwrap { margin-top: 15px; }
	#searchformsidebar { margin-top: 29px; }
	</style>
<![endif]-->
	</stripes:layout-component>
<stripes:layout-component name="SkipToNavigation">
	<a class="skiptonavlink" href="#searchtablink" onclick="$('#searchtablink').focus();return false;"title="Skip to Quick Search Tab">Skip to Quick Search Tab</a><br/>
	<a class="skiptonavlink" href="#txtTrm" onclick="$('#txtTrm').focus();return false;" title="Skip to Thesaurus Search Form">Skip to Thesaurus Search Form</a><br/>
</stripes:layout-component>
<%-- **************************************** --%>
<%-- CONTENTS                                 --%>
<%-- **************************************** --%>
	<stripes:layout-component name="contents">

	<div id="container">
	<div id="searchformwrap">
	<div id="searchformbox">
        <stripes:errors field="validationError"><div id="errormessage"><stripes:individual-error/></div></stripes:errors>
<c:if test="${not empty actionBean.message}">
<c:choose>
<c:when test="${actionBean.message eq 'zero'}">
		<ul class="errors" style="padding:0; margin:5px 0 0; "><li style="padding-bottom:0">Your search retrieved 0 records. For best results, do one or more of the following: </li></ul>
		<ul class="errorslist" style="padding:0; margin: 5px 0 0 40px; *margin-bottom: 5px">
			<li>Resubmit term to select different or additional terms to run your search</li>
			<li>Broaden your search by using fewer limits</li>
			<li>Delete term(s) from your search box</li>
		</ul>
</c:when>
</c:choose>
</c:if>

		<c:set var="searchtab" value="thessearch" scope="request"></c:set>
		<jsp:include page="parts/searchtabs.jsp"></jsp:include>

		<div id="searchcontents" class="shadowbox" role="search" aria-labeledby="ThesaurusSearch">
			<div id="searchtipsbox">
				<ul>
					<li class="databases"><a href="/databases.jsp?dbid=${actionBean.helpdbids}" title="Learn more about databases" id="databaseTipsLink" target="_blank" class="evdialog">Databases</a></li>
					<li><a href="/searchtips.jsp?topic=thes" id="searchTipsLink" title="Search tips to help" target="_blank" class="notfirst evdialog">Search tips</a></li>
				</ul>
			</div>

		 <div id="searchform">
         <form name="termsearch" id="thessearchform" method="get" action="#step0">
		 <input type="hidden" name="database" value="${actionBean.database}"/>
		 <input type="hidden" name="path" value="/search/thes/termsearch.url?"/>
	    <input type="hidden" name="searchid" value="${actionBean.searchid}"/>
			<%-- ******************************************************** --%>
			<%-- ******************************************************** --%>
			<%-- DATABASE SELECTION                                       --%>
			<%-- ******************************************************** --%>
			<%-- ******************************************************** --%>
			<div class="searchcomponentfullwrap">
				<h2 class="searchcomponentlabel">DATABASE</h2>
				<fieldset>
				<ul class="databasecheckgroup" style="margin:0; width: 600px">
					<c:if test="${actionBean.compendex}">
					<li><input id="rdCpx" name="database" value="1"<c:if test="${actionBean.database == 1}"> checked="checked"</c:if> type="radio"><label for="rdCpx">Compendex</label></li>
					</c:if>
					<c:if test="${actionBean.inspec}">
					<li><input id="rdIns" name="database" value="2"<c:if test="${actionBean.database == 2}"> checked="checked"</c:if> type="radio"><label for="rdIns">Inspec</label></li>
					</c:if>
					<c:if test="${actionBean.georef}">
					<li><input id="rdGrf" name="database" value="2097152"<c:if test="${actionBean.database == 2097152}"> checked="checked"</c:if> type="radio"><label for="rdGrf">GeoRef</label></li>
					</c:if>
                    <c:if test="${actionBean.geobase}">
                    <li><input id="rdGeo" name="database" value="8192"<c:if test="${actionBean.database == 8192}"> checked="checked"</c:if> type="radio"><label for="rdGeo">GEOBASE</label></li>
                    </c:if>
                    <c:if test="${actionBean.encompasspat and actionBean.encompasslit}">
                    <li><input id="rdEpt" name="database" value="3072" <c:if test="${actionBean.database == 3072}"> checked="checked"</c:if> type="radio"><label for="rdEpt">EnCompass</label></li>
                    </c:if>
                    <c:if test="${actionBean.encompasspat and !actionBean.encompasslit}">
                    <li><input id="rdEpt" name="database" value="2048" <c:if test="${actionBean.database == 2048}"> checked="checked"</c:if> type="radio"><label for="rdEpt">EnCompass</label></li>
                    </c:if>
                    <c:if test="${!actionBean.encompasspat and actionBean.encompasslit}">
                    <li><input id="rdEpt" name="database" value="1024" <c:if test="${actionBean.database == 1024}"> checked="checked"</c:if> type="radio"><label for="rdEpt">EnCompass</label></li>
                    </c:if>

				</ul>
			</fieldset>
			<div class="clear"></div>

			</div>

			<%-- ******************************************************** --%>
			<%-- ******************************************************** --%>
			<%-- SEARCH TERMS                                             --%>
			<%-- ******************************************************** --%>
			<%-- ******************************************************** --%>

			<div class="searchcomponentfullwrap">
				<h2 class="searchcomponentlabel">SEARCH FOR</h2>
				<div class="searchforline">
					<label class="hidden" for="sterm">Search Term</label>
					<input id="txtTrm" type="text" name="term" value="${actionBean.term}" title="Search Term Text Box" id="sterm"/>
					<a href="${actionBean.helpUrl}#Thesaurus_srch_steps.htm" class="helpurl"  title="Learn how to do a Thesaurus search"><img
						src="/static/images/i.png" border="0" class="infoimg" align="top" alt="Learn how to do a Thesaurus search"/></a>
				</div>

				<div class="searchforline">
					<fieldset>
							<span style="margin-left:100px; float:left">
							<input class="searchtype" id="rdSch" type="radio" name="thesAction" value="thesTermSearch" action="/search/thes/termsearch.url?" checked="true"/>
			                <label for="rdSch">Search</label>
			                <input class="searchtype" id="rdExt" type="radio" name="thesAction" value="thesFullRec" action="/search/thes/fullrec.url?"/>
			                <label for="rdExt">Exact Term</label>
			                <input class="searchtype" id="rdBrw" type="radio" name="thesAction" value="thesBrowse" action="/search/thes/browse.url?"/>
			                <label for="rdBrw">Browse</label>
			                </span>
		             </fieldset>
					 <input id="thessubmitbtn" type="submit" value="Submit" title="Submit search" style="float:right; margin-right:52px" class="button"></input>&nbsp;

				</div>
			</div>
			</form>

			<div id="firstslide" style="display:none">
			<%-- ******************************************************** --%>
			<%-- ******************************************************** --%>
			<%-- RESULTS (AJAX)                                           --%>
			<%-- ******************************************************** --%>
			<%-- ******************************************************** --%>
			<div class="searchcomponentfullwrap" style="padding-bottom:0">

			<div id="thesresultswrap" class="termsearchfromform">
				<h2 class="searchcomponentlabel">SEARCH</h2>
				<div id="termpath"></div>
				<div class="clear"></div>
				<div id="termresults" class="termelement"></div>
			</div>

			</div>


			<div class="searchcomponentseparator termsearchfromform" style="padding:0;margin:0;*margin-top:5px;height:1px;display:none"><hr></hr></div>

            <stripes:form id="pagesearchform" name="search" onsubmit="return searchValidation();" method="POST" action="/search/thesHome.url">
		    <input type="hidden" name="database" value="${actionBean.database}"/>
		    <input type="hidden" name="CID" value="thesSearchCitationFormat"/>
		    <input type="hidden" name="searchWord1" value=""/>
		    <input type="hidden" name="thesterm" value=""/>
			<input type="hidden" name="searchtype" value="Thesaurus"/>

			<div class="searchcomponentshortwrap termsearchfromform" style="display:none;margin-top:7px;width:100%">

				<%-- ******************************************************** --%>
				<%-- ******************************************************** --%>
				<%-- LIMIT BY and SORT BY                                     --%>
				<%-- ******************************************************** --%>
				<%-- ******************************************************** --%>
				<div id="limittowrap" style="width:250px; float:left;">

					<div class="searchcomponentlabel" style="float:none;margin-bottom:10px"><h2 style="font-size:14px;">LIMIT TO <a href="${actionBean.helpUrl}#Limit_to.htm" alt="Learn more about search limits" title="Learn more about search limits" class="helpurl"><img
						src="/static/images/i.png" border="0" class="infoimg" align="bottom" alt=""/></a></h2></div>

					<div class="searchlimitbybox">
					<ul>
						<c:if test="${not empty actionBean.doctypeopts}">
						<li>
                        <stripes:select name="doctype" onchange="checkPatent(this.form);" class="limittype" title="Choose a Document Type">
                        <stripes:options-collection collection="${actionBean.doctypeopts}" label="value" value="name"/>
                        </stripes:select>
					    </li>
						</c:if>

						<c:if test="${not empty actionBean.treatmenttypeopts}">
						<li>
                        <stripes:select name="treatmentType" class="limittype" title="Choose a Treatment Type">
                        <stripes:options-collection collection="${actionBean.treatmenttypeopts}" label="value" value="name"/>
                        </stripes:select>
					    </li>
						</c:if>

						<c:if test="${not empty actionBean.disciplinetypeopts}">
						<li>
                        <stripes:select name="disciplinetype" class="limittype" title="Choose a Discipline Type">
                        <stripes:options-collection collection="${actionBean.disciplinetypeopts}" label="value" value="name"/>
                        </stripes:select>
					    </li>
						</c:if>

						<%-- Always output language type options --%>
						<li>
						<stripes:select name="language" class="limittype" title="Choose a Language">
                        <stripes:options-collection collection="${actionBean.languageopts}" label="value" value="name"/>
						</stripes:select>
					    </li>

						<li>
						  <stripes:radio name="yearselect" value="yearrange"/>

						  <stripes:select name="startYear" style="width:5em" title="Choose a date range">
                            <stripes:options-collection collection="${actionBean.startyearopts}" label="value" value="name"/>
						  </stripes:select>

						<h2 class="searchcomponentlabel" style="float:none; margin: 0 15px; display:inline;"> TO </h2>

						  <stripes:select name="endYear" style="width:5em" title="Choose a date range">
                            <stripes:options-collection collection="${actionBean.endyearopts}" label="value" value="name"/>
						  </stripes:select>
						  <input type="hidden" name="stringYear" value="${actionBean.stringYear}"></input>
						</li>

						<li>
	                        <input type="radio" name="yearselect" value="lastupdate" id="rdupdt"<c:if test="${not empty actionBean.updatesNo}"> checked="checked"</c:if>
	                        	title="Use this option to limit your search to the database updates from the last week, last 2 weeks, last 3 weeks, or last 4 weeks."/>
	                          <stripes:select name="updatesNo" title="Use this option to limit your search to the database updates from the last week, last 2 weeks, last 3 weeks, or last 4 weeks.">
								<stripes:option value="1">1</stripes:option>
								<stripes:option value="2">2</stripes:option>
								<stripes:option value="3">3</stripes:option>
								<stripes:option value="4">4</stripes:option>
	                          </stripes:select>

                            <label for="rdupdt">Updates</label>
                        </li>
                        </ul>

       				</div>

				</div>

				<%-- ******************************************************** --%>
				<%-- ******************************************************** --%>
				<%-- CLIPBOARD                                                --%>
				<%-- ******************************************************** --%>
				<%-- ******************************************************** --%>
				<div id="clipboardwrap" style="width: 210px; float:left;">
					<div class="searchcomponentlabel" style="float:none;margin-bottom:10px"><h2 style="font-size:14px;">SEARCH BOX</h2></div>
					<stripes:select id="clip" name="clip" multiple="true" size="8" style="width:200px">
                        <stripes:options-collection collection="${actionBean.clipoptions}" label="value" value="name"/>
					</stripes:select>
					<br/>
					<input class="button" style="margin:5px 0" type="button" value="Remove&nbsp;selected&nbsp;terms" title="Remove selected terms from your query" id="removeterm">
					<%-- Couldn't find any other way to line these buttons up with the button above!!! --%>
					<div class="searchforline" style="position: relative; margin-top:-31px; *margin-top:-32px; float:right; left:222px">
						<stripes:submit name="submit" value="Search" class="button" title="Search"/>&nbsp;
						<input type="reset" title="Reset search" value="Reset" class="button"></input>&nbsp;
					</div>

				</div>

				<%-- ******************************************************** --%>
				<%-- ******************************************************** --%>
				<%-- COMBINE AND SORT                                         --%>
				<%-- ******************************************************** --%>
				<%-- ******************************************************** --%>
				<div id="sortbywrap" style="float:left;">
					<div class="searchcomponentlabel" style="float:none;margin-bottom:10px; width:100%"><h2 style="font-size:14px;">COMBINE SEARCH WITH&nbsp;<a href="${actionBean.helpUrl}#Operators_Thesaurus.htm" alt="Learn about selecting how your search terms can be combined" title="Learn about selecting how your search terms can be combined" class="helpurl"><img
						src="/static/images/i.png" border="0" class="infoimg" align="top" alt=""/></h2></a></div>
					<div class="searchcombinebox">
					<fieldset>
						<input name="andor" id="andorand" value="AND"<c:if test="${actionBean.combine eq 'and'}"> checked="true"</c:if> type="radio">&nbsp;<label for="andorand" >AND</label>
						<input name="andor" id="andoror" value="OR"<c:if test="${actionBean.combine eq 'or'}"> checked="true"</c:if> type="radio">&nbsp;<label for="andoror">OR</label>
					</fieldset>
					</div>

					<br/>

					<div class="searchcomponentlabel" style="float:none;margin-bottom:10px"><h2 style="font-size:14px;">SORT BY <a href="${actionBean.helpUrl}#Specify_sort_Thesau_srch.htm" alt="Learn more about choosing sort order" title="Learn more about choosing sort order" class="helpurl"><img
						src="/static/images/i.png" border="0" class="infoimg" align="bottom" alt=""/></a></h2></div>
					<div class="searchlimitbybox">
					<ul>
					<li>
					<fieldset>
					  <span style="margin-right:15px;">
	                    <input type="radio" name="sort" id="chkrel" value="relevance" title="Sort your search by Relevance" <c:if test="${actionBean.sort == 'relevance'}"> checked="checked"</c:if>/>
						<label for="chkrel">Relevance</label>
					  </span>
					  <span>
                        <input type="radio" name="sort" id="chkyr" value="yr" title="Sort your search by Year in descending order" <c:if test="${actionBean.sort == 'yr'}"> checked="checked"</c:if>/>
                        <label for="chkyr">Date (Newest)</label>
                      </span>
                      </fieldset>
					</li>

                    </ul>
					</div>
				</div>

				<div class="clear"></div>

			</div>

				<div class="clear"></div>

		</stripes:form>
		</div> <!-- END firstslide -->
		</div> <!-- END searchform -->

		<div class="clear"></div>

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

		</div> <!-- END container -->

		<div class="clear"></div>

	<div id="loading" style="display:none"><img src="/static/images/loading.gif"></img></div>

	</stripes:layout-component>

	<stripes:layout-component name="jsbottom_custom">
	<jwr:script src="/bundles/thes.js"></jwr:script>
	<script>var flipImage;</script>
	<jsp:include page="parts/search_common_js.jsp"></jsp:include>

	</stripes:layout-component>


</stripes:layout-render>

