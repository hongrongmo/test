<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld"
	prefix="stripes"%>

<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp" pageTitle="Engineering Village - Open XML status">

	<stripes:layout-component name="csshead">
		<jsp:include page="include/customcss.jsp"></jsp:include>
	</stripes:layout-component>

	<stripes:layout-component name="header">
		<jsp:include page="/WEB-INF/pages/include/headernull.jsp" />
	</stripes:layout-component>

	<%-- **************************************** --%>
	<%-- CONTENTS                                 --%>
	<%-- **************************************** --%>
	<stripes:layout-component name="contents">
		<div id="container">

			<jsp:include page="include/tabs.jsp" />

			<div class="marginL10">

				<h2>Test OpenXML</h2>
				<hr style="padding-right: 7px" />
				<form name="quicksearch" id="quicksearchform"
					action="/controller/servlet/Controller" method="post">

					<input type=hidden name="CID" value="openXML" /> <input
						name="DATABASE" id="database" type="hidden" value="0" />

					<div id="databasechkboxes" class="searchcomponentfullwrap">
						<div class="searchcomponentlabel">Database:</div>

						<div class="databasecheckall">
							<input type="checkbox" value="2289095" id="allchkbx"
								class="databasechkbox" style="vertical-align: middle;"
								name="alldb" /> <label class="SmBlackText" for="allchkbx">All</label>
						</div>

						<ul class="databasecheckgroup">

							<li><input style="vertical-align: middle" type="checkbox"
								name="database" class="databasechkbox" value="1" id="cpxchkbx"
								checked="checked" /><label class="SmBlackText" for="cpxchkbx">Compendex</label>
							</li>

							<li><input style="vertical-align: middle" type="checkbox"
								name="database" class="databasechkbox" value="2" id="inschkbx"
								checked="checked" /><label class="SmBlackText" for="inschkbx">Inspec</label>
							</li>

							<li><input style="vertical-align: middle" type="checkbox"
								name="database" class="databasechkbox" value="4" id="ntichkbx"
								checked="checked" /><label class="SmBlackText" for="ntichkbx">NTIS</label>
							</li>

							<li><input style="vertical-align: middle" type="checkbox"
								name="database" class="databasechkbox" value="64" id="pchchkbx" /><label
								class="SmBlackText" for="pchchkbx">PaperChem</label></li>

							<li><input style="vertical-align: middle" type="checkbox"
								name="database" class="databasechkbox" value="128" id="chmchkbx" /><label
								class="SmBlackText" for="chmchkbx">Chimica</label></li>

							<li><input style="vertical-align: middle" type="checkbox"
								name="database" class="databasechkbox" value="256" id="cbnchkbx"
								checked="checked" /><label class="SmBlackText" for="cbnchkbx">CBNB</label>
							</li>

							<li><input style="vertical-align: middle" type="checkbox"
								name="database" class="databasechkbox" value="1024"
								id="eltchkbx" /><label class="SmBlackText" for="eltchkbx">EnCompassLIT</label>
							</li>

							<li><input style="vertical-align: middle" type="checkbox"
								name="database" class="databasechkbox" value="2048"
								id="eptchkbx" /><label class="SmBlackText" for="eptchkbx">EnCompassPAT</label>
							</li>

							<li><input style="vertical-align: middle" type="checkbox"
								name="database" class="databasechkbox" value="8192"
								id="geochkbx" /><label class="SmBlackText" for="geochkbx">GEOBASE</label>
							</li>

							<li><input style="vertical-align: middle" type="checkbox"
								name="database" class="databasechkbox" value="2097152"
								id="grfchkbx" /><label class="SmBlackText" for="grfchkbx">GeoRef</label>
							</li>

							<li><input style="vertical-align: middle" type="checkbox"
								name="database" class="databasechkbox" value="32768"
								id="upachkbx" checked="checked" /><label class="SmBlackText"
								for="upachkbx">US Patents</label></li>

							<li><input style="vertical-align: middle" type="checkbox"
								name="database" class="databasechkbox" value="16384"
								id="eupchkbx" checked="checked" /><label class="SmBlackText"
								for="eupchkbx">EP Patents</label></li>

							<li><input style="vertical-align: middle" type="checkbox"
								name="database" class="databasechkbox" value="131072"
								id="pagchkbx" /><label class="SmBlackText" for="pagchkbx">Referex</label>
							</li>
							<li><input style="vertical-align: middle" type="checkbox"
								name="database" class="databasechkbox" value="262144"
								id="cbfchkbx" /> <label for="cbfchkbx">Engineering
									Index (Compendex Backfile Standalone)</label></li>
							<li><input style="vertical-align: middle" type="checkbox"
								name="database" class="databasechkbox" value="1048576"
								id="ibschkbx" /> <label for="ibschkbx">Inspec Backfile
									(Standalone)</label> <!-- This variable wil be set by the JS according to the value of the Checkboxes -->
							</li>

						</ul>


						<div class="clear"></div>


					</div>

					<div class="searchcomponentlabel">Query:</div>
					<div class="searchcomponentfullwrap">
						<textarea rows="10" cols="60" wrap="PHYSICAL" name="XQUERYX"
							id="XQUERYX"></textarea>
					</div>
					<div class="clear"></div>

					<div class="searchcomponentlabel" style="float: none">Examples:</div>
					<div class="clear"></div>
					<div class="searchcomponentfullwrap">
						<ul style="margin: 0; padding: 0 0 0 25px">
							<li class="example">&lt;query&gt;&lt;word
								path="ti"&gt;java&lt;/word&gt;&lt;/query&gt;</li>
							<li class="example">&lt;query&gt;&lt;word
								&gt;diode&lt;/word&gt;&lt;/query&gt;</li>
							<li class="example">&lt;query&gt;&lt;andQuery&gt;&lt;orQuery&gt;&lt;word
								path="ti"&gt;hello*&lt;/word&gt;&lt;word&gt;world
								acid&lt;/word&gt;&lt;/orQuery&gt;&lt;notQuery&gt;&lt;word&gt;goodbye&lt;/word&gt;&lt;word&gt;shift&lt;/word&gt;&lt;/notQuery&gt;&lt;/andQuery&gt;&lt;/query&gt;</li>
						</ul>
					</div>
					<div class="clear"></div>

					<p>
						AutoStemming: <input type="radio" name="AUTOSTEM" value="on"
							checked="checked" />&nbsp;On &nbsp;<input type="radio"
							name="AUTOSTEM" value="off" />&nbsp;Off
					</p>

					<p>
						<label for="STARTYEAR">Start Year</label> <select name="STARTYEAR"
							id="startyear"></select> &nbsp; <label for="ENDYEAR">End
							Year</label> <select NAME="ENDYEAR" id="endyear"></select>
					</p>
					<p>
						Sort By: <input type="radio" name="SORT" value="re"
							checked="checked" />Relevance&nbsp; <input type="radio"
							name="SORT" value="yr" />Publication year
					</p>
					<p>
						<input type="submit" name="xmlsearch" class="button" />&nbsp;&nbsp;<input
							type="reset" class="button" />
					</p>
					<br />
					<p>Results:</p>
					<div id="results"></div>
				</form>


				<script type="text/javascript">
        $(document).ready(function() {
            $("#database").value = calculateMask();

            var d = new Date();
            for (var i = 1885; i <= d.getFullYear(); i++) {
                $("#startyear").append(new Option(i,i));
                $("#endyear").prepend(new Option(i,i));         
            }
            $("#startyear option[value='1990']").attr('selected','selected');
            $("#endyear option[value='"+d.getFullYear()+"']").attr('selected','selected');

            // Add example text to search box
            $(".example").click(function(e) {
                $("#XQUERYX").val($(this).text());
            });
            
            // Re-calc database value on checkbox click
            $(".databasechkbox").click(function(e) {
                $("#database").value = calculateMask();
            });
            
            // Intercept form submit and display results
            $("#quicksearchform").submit(function(e) {
                e.preventDefault();
                var jqhxr = $.post(
                        "/controller/servlet/Controller",
                        $(this).serialize());
                jqhxr.success(function(data) {
                    $("#results").empty().append($(data).text());
                });
            });
        });
        
        function calculateMask()
        {
            var selectedDbMask = 0;
            var alldb = $("input[name='alldb']");
            var databases = $("input[name='database']");
            if (alldb.length > 0 && alldb.is(":checked")) {
                selectedDbMask = eval(alldb.val());
            } else if (databases.length > 0) {
                databases.each(function(idx) {
                    if ($(this).is(":checked")) selectedDbMask += eval($(this).val());
                });
            }
            return selectedDbMask;
        }

        </script>

			</div>

		</div>

	</stripes:layout-component>

</stripes:layout-render>