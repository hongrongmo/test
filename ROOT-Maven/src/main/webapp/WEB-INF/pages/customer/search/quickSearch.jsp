
<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8" %>
<%@ taglib uri="http://jawr.net/tags" prefix="jwr" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld" prefix="stripes" %>

<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp" pageTitle="Engineering Village - Quick Search">

	<stripes:layout-component name="csshead">
	<jwr:style src="/bundles/quick.css"></jwr:style>
	<script src="/static/js/autocomplete/typeahead.js"></script>
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
	<a class="skiptonavlink" href="#srchWrd1" onclick="$('#srchWrd1').focus();return false;" title="Skip to Quick Search Form">Skip to Quick Search Form</a><br/>
</stripes:layout-component>

<%-- **************************************** --%>
<%-- CONTENTS                                 --%>
<%-- **************************************** --%>
	<stripes:layout-component name="contents">

	<div id="container">
	<div id="searchformwrap">
	<div id="searchformbox">

        <stripes:errors field="validationError"><div id="errormessage"><stripes:individual-error/></div></stripes:errors>

		<c:set var="searchtab" value="quicksearch" scope="request"></c:set>
		<jsp:include page="parts/searchtabs.jsp"></jsp:include>

		<div id="searchcontents" class="shadowbox" role="search" aria-labeledby="QuickSearch">
			<div id="searchtipsbox">
				<ul>
					<li class="databases"><a href="/databases.jsp?dbid=<c:forEach items="${actionBean.databaseCheckboxes}" var="checkbox" varStatus="status"><c:if test="${status.count>1}">,</c:if>${checkbox.id}</c:forEach>" id="databaseTipsLink" title="Learn more about databases" target="_blank" class="evdialog">Databases</a></li>
					<li><a href="/searchtips.jsp?topic=quick"  title="Search tips to help" id="searchTipsLink" class="notfirst evdialog" >Search tips</a></li>
				</ul>
			</div>

			<div id="searchform">
			<stripes:form onsubmit="return searchValidation();" method="POST" action="/search/submit.url" name="quicksearch">
			<input type="hidden" name="CID" value="searchSubmit"/>
			<input type="hidden" name="searchtype" value="Quick"/>
			
			<input type="hidden" name="resetDataBase" value="${actionBean.database}"/>

			<%-- ******************************************************** --%>
			<%-- ******************************************************** --%>
			<%-- DATABASE SELECTION                                       --%>
			<%-- ******************************************************** --%>
			<%-- ******************************************************** --%>
			<jsp:include page="parts/database.jsp"></jsp:include>

			<div class="clear"></div>

			<%-- ******************************************************** --%>
			<%-- ******************************************************** --%>
			<%-- SEARCH TERMS                                             --%>
			<%-- ******************************************************** --%>
			<%-- ******************************************************** --%>

			<div id="searchcomponentfullwrap" class="searchcomponentfullwrap" style="padding-bottom:0">
				<div id="searchfields">
					<h2 class="searchcomponentlabel">SEARCH FOR</h2>
					<div class="searchforline">
						<label class="hidden" for="srchWrd1">Search 1</label>
						<stripes:text name="searchWord1" id="srchWrd1" onblur="javascript:updateWinds(); " class="searchword" title="Search Text Box 1"/>
						<span>in</span>
						<label class="hidden" for="sect1">Search Within 1</label>
	                    <stripes:select size="1" name="section1" id="sect1" class="section" title="Search Within Dropdown 1">
                        <stripes:options-collection collection="${actionBean.section1opts}" label="value" value="name"/>
	                    </stripes:select>
						<a	href="${actionBean.helpUrl}#search_fields_avail_by_database.htm" class="helpurl" alt="Learn more about available search fields by database">
						<jwr:img src="/static/images/i.png" border="0" styleClass="infoimg" align="bottom" alt="" title="Learn more about available search fields by database"/></a>
					</div>

					<div class="searchforconnector">
						<label class="hidden" for="cbnt1">Combine Terms 1</label>
						<stripes:select	size="1" name="boolean1" class="boolean" id="cbnt1" title="boolean operator for combining textbox 1 and textbox 2">
							<stripes:option value="AND">AND</stripes:option>
							<stripes:option value="OR">OR</stripes:option>
							<stripes:option value="NOT">NOT</stripes:option>
						</stripes:select>
					</div>
					<div class="searchforline">
						<label class="hidden" for="srchWrd2">Search 2</label>
						<stripes:text name="searchWord2" id="srchWrd2" onblur="javascript:updateWinds();" class="searchword" title="Search Text Box 2"/>
						<span>in</span>
						<label class="hidden" for="sect2">Search Within 2</label>
						<stripes:select	size="1" name="section2" class="section" id="sect2" title="Search Within Dropdown 2">
                        <stripes:options-collection collection="${actionBean.section2opts}" label="value" value="name"/>
						</stripes:select>
					</div>

					<div class="searchforconnector">
						<label class="hidden" for="cbnt2">Combine Terms 2</label>
						<stripes:select	size="1" name="boolean2" id="cbnt2" class="boolean" title="boolean operator for combining textbox 2 and textbox 3">
							<stripes:option value="AND">AND</stripes:option>
							<stripes:option value="OR">OR</stripes:option>
							<stripes:option value="NOT">NOT</stripes:option>
						</stripes:select>
					</div>
					<div class="searchforline">
						<label class="hidden" for="srchWrd3">Search 3</label>
						<stripes:text name="searchWord3" id="srchWrd3" onblur="javascript:updateWinds();" class="searchword" title="Search Text Box 3"/>
						<span> in </span>
						<label class="hidden" for="sect3">Search Within 3</label>
						<stripes:select	size="1" name="section3" class="section" id="sect3" title="Search Within Dropdown 3">
                        <stripes:options-collection collection="${actionBean.section3opts}" label="value" value="name"/>
						</stripes:select>
					</div>

				</div>

				<div id="addsearcherror" class="searchforline" style="display:none; padding-left: 400px"><jwr:img src="/static/images/red_warning.gif"/><b> You have reached maximum 12 search fields.</b></div>

				<div id="search" class="searchforline">
					<div style="float:right; margin-right:52px; text-align: middle">
						<c:if test="${actionBean.context.userSession.user.userPreferences.autocomplete}">
							<span id="acSpan">
								<span id='turnOnAutocompleteSpan' style="padding: 0; margin: 0; <c:if test="${actionBean.context.userSession.autoCompleteEnabled}">display:none</c:if>">
									<a href="#" onclick="turnOnAutoComplete();return false;" title="Turn On AutoSuggest">Turn On AutoSuggest</a> <a	href="${actionBean.helpUrl}#autocomplete_ei.htm.htm" class="helpurl" alt="AutoSuggest powered by Ei Thesaurus"><jwr:img src="/static/images/i.png" border="0" styleClass="infoimg" align="bottom" alt="" title="AutoSuggest powered by Ei Thesaurus"/></a><span class="searchoptionpipe">|</span>
								</span>

								<span id='turnOffAutocompleteSpan' style="padding: 0; margin: 0; <c:if test="${!actionBean.context.userSession.autoCompleteEnabled}">display:none</c:if>">
									<a href="#" onclick="turnOffAutoComplete();return false;" title="Turn Off AutoSuggest">Turn Off AutoSuggest</a> <a	href="${actionBean.helpUrl}#autocomplete_ei.htm" class="helpurl" alt="AutoSuggest powered by Ei Thesaurus" targe="_blank"><jwr:img src="/static/images/i.png" border="0" styleClass="infoimg" align="bottom" alt="" title="AutoSuggest powered by Ei Thesaurus"/></a><span class="searchoptionpipe">|</span>
								</span>


							</span>

						</c:if>
						<span id="resetlinkspan" style="display:none; padding: 0; margin: 0">
							<input type ="hidden" id="resetvar" name="resetvar" value="1"/>
							<a class="resetformlink" title="Reset form and clear all" href="/search/quick.url?CID=quickSearch&database=${actionBean.database}">Reset form</a>
							<span class="searchoptionpipe">|</span>
						</span>

						<span id='addsearchspan' style="padding: 0; margin: 0">
							<span id="addsearchllinkgray" title="Add search field" class="greytext" style="display:none"><jwr:img src="/static/images/addsearchfield.gif" styleClass="addsearchimg" alt="Add Search Field Icon"/> Add search field</span><a id="addsearchllink" href="#" title="Add search field"><jwr:img src="/static/images/addsearchfield.gif" styleClass="addsearchimg"/> Add search field</a><span class="searchoptionpipe">|</span>
						</span>



						<input type="submit" value="Search" title="Search" class="button" title="Submit Search"></input>

					</div>

					<div class="clear"></div>
				</div>
			</div>

			<div class="searchcomponentseparator" style="padding:0; *margin-bottom:0"><hr></hr></div>
			<div class="clear"></div>

			<%-- ******************************************************** --%>
			<%-- ******************************************************** --%>
			<%-- LIMIT BY and SORT BY                                     --%>
			<%-- ******************************************************** --%>
			<%-- ******************************************************** --%>
			<jsp:include page="parts/limits.jsp"></jsp:include>

			<div id="buttonsbottom" class="searchcomponentshortwrap" style="position: relative; margin-top:-30px; float:none; z-index: 1">
				<div style="float: right;margin-bottom:7px">
					<input type="submit" class="button" value="Search" title="Submit Search">&nbsp;
					<input id="quick-search-reset" type="reset" class="button" value="Reset" title="Reset search">&nbsp;
					<div class="clear"></div>
				</div>
				<div class="clear"></div>
			</div>
		</stripes:form>
			</div> <!-- END searchform -->
		</div>	<!-- END searchcontents -->
		<br class="clear"/>
		</div><!-- END searchformbox -->

		<jsp:include page="parts/history.jsp"></jsp:include>

		</div> <!-- end searchformwrap -->
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
	<%-- Hidden element only used to add search fields --%>
	<div class="addsearchfieldcopy" style="display:none">
		<div class="searchforconnector" id="addsearchforconnector">
			<select	size="1" name="booleans" id="connector" class="boolean">
				<option value="AND">AND</option>
				<option value="OR">OR</option>
				<option value="NOT">NOT</option>
			</select>
		</div>
		<div class="searchforline" id="addsearchforline">
			<input name="searchWords" onblur="javascript:updateWinds();" value="" type="text" class="searchwordcopy">

			<span>in</span>
			<select	size="1" name="sections" class="section">
                    ${actionBean.section1opts}
			</select>

			<a href="#" class="removesearchlink"><jwr:img src="/static/images/Delete.png" title="Remove this search field."/></a>
		</div>
	</div>
	<jwr:script src="/bundles/quick.js"/>
	<script src="/static/js/autocomplete/hogan.js"></script>
	<script type="text/javascript">
	var acSessionEnabled = false;
	var acPrefsEnabled = false;
	var recommendOnly = false;
	var acFields = [];
	$(document).ready(function() {
		var db = ${actionBean.database};
		// Focus on first search field
		//removed because of skip to navigation
/* 		if(typeof(document.quicksearch.searchWord1) != 'undefined') {
			$('#srchWrd1').focus();
		} */
		flipImage(db);

		// Add extra search fields if present on request
		<c:if test="${not empty actionBean.searchWords}">
		<c:forEach var="searchWord" items="${actionBean.searchWords}" varStatus="status">
			<c:if test="${not empty searchWord}">
			addSearchField('${searchWord}', '${actionBean.booleans[status.count-1]}', '${actionBean.sections[status.count-1]}');

			</c:if>
		</c:forEach>
		</c:if>

		<c:if test="${actionBean.context.userSession.user.userPreferences.autocomplete}">
			<c:if test="${actionBean.context.userSession.autoCompleteEnabled}">
				acSessionEnabled = true;
				checkACFields();
				$(".section").change(function(){checkACField($(this))});

			</c:if>
		</c:if>

	});
	$("#quick-search-reset").click(function(e){
		e.preventDefault();
		window.location = "/search/quick.url?CID=quickSearch&database=${actionBean.database}";
		return false;
	});

	<c:if test="${actionBean.context.userSession.user.userPreferences.autocomplete}">
			var acPrefsEnabled = true;
			acFields = ["NO-LIMIT","KY","AB","TI","CV"];
			var acFieldsMap = {};
			for(var j = 0; j < acFields.length; j++ ){acFieldsMap[acFields[j]] = true;}

			var template = '{{#isUseTerms}}<p><span style="padding-left:15px;" class="term"><i style="font-size:11px;">Recommended Term:</i> {{mainTermDisplay}}</span>{{/isUseTerms}}{{^isUseTerms}}<p><span style="padding:0;" class="term">{{mainTermDisplay}}</span></p>{{/isUseTerms}}';
			function turnOffAutoComplete(){
				$.ajax({
					url:"/ac.url?disable=true&enabled=false"
					}
				).error(function(data){


				}).success(function(data){
					$('.searchword').typeahead("destroy");
					$('#turnOnAutocompleteSpan').toggle();
					$('#turnOffAutocompleteSpan').toggle();
					acSessionEnabled = false;
			        GALIBRARY.createWebEventWithLabel('AutoSuggest', "OnOff" , "Off");

				});
				return false;
			}
			function turnOnAutoComplete(){
				$.ajax({
					url:"/ac.url?disable=true&enabled=true"
					}
				).success(function(data){
					checkACFields();
					$('#turnOnAutocompleteSpan').toggle();
					$('#turnOffAutocompleteSpan').toggle();
					acSessionEnabled = true;
                    GALIBRARY.createWebEventWithLabel('AutoSuggest', "OnOff" , "On");
				});
				return false;
			}
			function toggleUseRecommend(){
				recommendOnly = $("#useRecommendedBox").prop("checked");
				$.ajax({
					url:"/ac.url?userecommend=true&enabled=" + recommendOnly
					}
				).error(function(data){


				});
				return false;
			}


			function createAC(selector){
				if($(selector).data('ttView')){
					//already exists
					return;
				}

				$(selector).typeahead([
		        			{
		        			name: 'suggestions',
		        			minLength:3,
		        			remote: '../autocomplete?term=%QUERY',
		        			valueKey:'mainTermDisplay',
		        			limit:10,
		        			 template: template,
		        			 footer:'<div class="typeaheadFooter"><span style="float:left;"><span style="font-family:sans-serif;font-weight:bold;font-size:12px;padding:0px;">AutoSuggest </span><span style="font-size:11px;padding:0px;"><i>Powered by</i></span> Ei Thesaurus <a title="Learn more about AutoSuggest and Ei Thesaurus" alt="Learn more about AutoSuggest and Ei Thesaurus" onclick="helpurlClick(this);return false;"class="helpurl" href="${actionBean.helpUrl}#autocomplete_ei.htm"><img alt="More info icon" width="12px" src="/static/images/i.png"/></a></span><span class="acToggle"><a href="#" class="acOff" onclick="turnOffAutoComplete()">Turn Off</a></span><div/>',
		        			 engine: Hogan
		        			}
		        		]).on('typeahead:selected', function(obj, value) {
							GALIBRARY.createWebEventWithLabel('AutoSuggest', 'Suggestion Selected', value);
						});

			}

			//create AC for inputs that have the in field set to a valid option
			function checkACFields(){
				for(var i = 0; i < $(".section:visible").length; i++){
					if(acFieldsMap[$($(".section:visible")[i]).val()]){
						//field allowed
						createAC($($(".searchword:visible")[i]));
					}
				}
			}
			//check just one field (for when the user changes the selection)
			function checkACField(selector){
				if(acFieldsMap[$(selector).val()] ){
					//field allowed
					if($(selector).parent().find(".searchword").data('ttView')){
						//already there
						return;
					}else{
						//need to add it
						createAC($(selector).parent().find(".searchword"));
					}
				}else if($(selector).parent().find(".searchword").data('ttView')){
					$(selector).parent().find(".searchword").typeahead("destroy");
				}else{
					return;
				}
			}


		</c:if>
		<c:choose>
		<c:when test="${actionBean.context.userSession.user.userPreferences.autocomplete}">
				function toggleAutoComplete(){

					if($('.searchword').data('ttView')){
					//hide
						$('.searchword').typeahead("destroy");
						$('#acSpan').hide();

					}else{
						//show
						$('#acSpan').show();
						if(acSessionEnabled){
							createAC('.searchword');
						}
					}
				}
		</c:when>
		<c:otherwise>
			function toggleAutoComplete(){}
			function createAC(selector){};
			function checkACField(selector){}
			function checkACFields(){}
		</c:otherwise>
	</c:choose>

	</script>


	<jsp:include page="parts/search_common_js.jsp"></jsp:include>
	</stripes:layout-component>
</stripes:layout-render>
