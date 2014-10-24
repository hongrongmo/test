<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld"
	prefix="stripes"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp"
	pageTitle="Engineering Village - Quick Search Results">

	<stripes:layout-component name="csshead">
		<link href="/static/css/ev_results.css?v=${releaseversion}"" media="all"
			type="text/css" rel="stylesheet" />
	</stripes:layout-component>

	<stripes:layout-component name="header">
		<jsp:include page="/WEB-INF/pages/include/headernull.jsp" />
	</stripes:layout-component>

	<stripes:layout-component name="contents">

<div id="delivery_content" class="marginL15">
    <ul class="errors" id="jserrors">
    <stripes:errors>
         <stripes:errors-header></stripes:errors-header>
         <li><stripes:individual-error/></li>
         <stripes:errors-footer></stripes:errors-footer>
    </stripes:errors>
    </ul>
<c:set value="${actionBean.context.userSession.user.userPrefs.dlFormat}" var="dlFormat"></c:set>
<c:set value="${actionBean.context.userSession.user.userPrefs.dlOutput}" var="dlOutput"></c:set>
<c:if test="${dlOutput eq 'default' }"><c:set value="${actionBean.displayformat}" var="dlOutput"></c:set></c:if>

<c:choose>
    <c:when test="${(empty actionBean.basket || actionBean.basket.basketSize eq 0) && (empty actionBean.docidlist) && (empty actionBean.folderid)}">
            <table border="0" width="100%" cellspacing="0" cellpadding="0" bgcolor="#FFFFFF">
            <tr><td valign="top" height="15" colspan="3"><img src="/static/images/spacer.gif" border="0"/></td></tr>
            <tr>
            <td valign="top" width="20"><img src="/static/images/spacer.gif" border="0" width="20"/></td>
            <td valign="top" colspan="2"><span><h2>Download Records <a href="${actionBean.helpUrl}#email_print_downld_ev.htm#downld" class="helpurl" title="Learn more about Download Records" alt="Learn more about Download Records"><img alt="More info icon" width="12px" src="/static/images/i.png"/></a></h2></span></td>
            </tr>
            <tr><td valign="top" height="2" colspan="3"><img src="/static/images/spacer.gif" border="0"/></td></tr>
            <tr>
            <td valign="top" width="20"><img src="/static/images/spacer.gif" border="0" width="20"/></td>
            <td valign="top">
            <span CLASS="MedBlackText">
            You did not select any records to download. Please select records from the search results and try again.
            </span>
            </td>
            <td valign="top" width="10"><img src="/static/images/spacer.gif" border="0" width="10"/></td>
            </tr>
            </table>
    </c:when>

    <c:otherwise>
    <c:set var="usersession" value="${actionBean.context.userSession}"/>
	<stripes:form name="download" id="download" method="post" action="/delivery/download/submit.url">
        <stripes:hidden name="sessionid" id="sessionid"/>
	    <stripes:hidden name="docids" id="docids" />
	    <stripes:hidden name="handles" id="handles" />
	    <stripes:hidden name="docidlist" id="docidlist" />
	    <stripes:hidden name="handlelist" id="handlelist" />
        <stripes:hidden name="folderid" id="folderid" />
	    <stripes:hidden name="database" id="database" />
	    <stripes:hidden name="baseaddress" id="baseaddress" />

			<table border="0" width="100%" cellspacing="0" cellpadding="0" bgcolor="#FFFFFF">
				<tbody>
					<tr>
						<c:choose>
						    <c:when test="${actionBean.referexOnlyRecords}">
						    	<td valign="top" width="20"><img src="/static/images/s.gif" border="0" width="20" alt=""></td>
						    	<td valign="top" height="10" colspan="3" >
						    		<div id="errormessage"><img src="/static/images/red_warning.gif"><b>&nbsp;&nbsp;Referex database results are not supported in the Excel&reg; download format. Please select another format.</b></div>
						    		<br />
						    	</td>
						    </c:when>
						    <c:otherwise>
						    	<td valign="top" height="15" colspan="3"><img src="/static/images/s.gif" border="0" alt=""></td>
							</c:otherwise>
						</c:choose>
					</tr>
					<tr>
						<td valign="top" width="20"><img
							src="/static/images/s.gif" border="0" width="20" alt=""></td>
						<td valign="top" colspan="2"><span><h2>Download Records <a href="${actionBean.helpUrl}#email_print_downld_ev.htm#downld" class="helpurl" title="Learn more about Download Records" alt="Learn more about Download Records"><img alt="More info icon" width="12px" src="/static/images/i.png"/></a></h2></span></td>
					</tr>
					<tr>
						<td valign="top" width="20"><img
							src="/static/images/s.gif" border="0" width="20" alt=""></td>
						<td valign="top"><span class="MedBlackText">To
								download <b>${actionBean.count}</b> record<c:if test="${actionBean.count gt 1}">s</c:if>, please select record output and
								download format below.
						</span></td>
						<td valign="top" width="10"><img
							src="/static/images/s.gif" border="0" width="10" alt=""></td>
					</tr>
					<tr>
						<td valign="top" height="10" colspan="3"><img
							src="/static/images/spacer.gif" border="0" height="10"
							alt=""></td>
					</tr>
					<tr>
						<td valign="top" width="20"><img
							src="/static/images/spacer.gif" border="0" width="20"
							alt=""></td>
						<td valign="top"><span class="MedBlackText">NOTE: Your
								selected records (to a maximum of 500) will be kept until your
								session ends. However, to delete them after this task:</span></td>
						<td valign="top" width="10"><img
							src="/static/images/spacer.gif" border="0" width="10"
							alt=""></td>
					</tr>
					<tr>
						<td valign="top" width="20"><img
							src="/static/images/spacer.gif" border="0" width="20"
							alt=""></td>
						<td valign="top"><span class="MedBlackText"><li
								style="margin-left: 20px;">Return to the Search results
									page and click Delete Selected Records, or</li></span></td>
						<td valign="top" width="10"><img
							src="/static/images/spacer.gif" border="0" width="10"
							alt=""></td>
					</tr>
					<tr>
						<td valign="top" width="20"><img
							src="/static/images/spacer.gif" border="0" width="20"
							alt=""></td>
						<td valign="top"><span class="MedBlackText"><li
								style="margin-left: 20px;">Go to the Selected records page
									and click Remove All, or</li></span></td>
						<td valign="top" width="10"><img
							src="/static/images/spacer.gif" border="0" width="10"
							alt=""></td>
					</tr>
					<tr>
						<td valign="top" width="20"><img
							src="/static/images/spacer.gif" border="0" width="20"
							alt=""></td>
						<td valign="top"><span class="MedBlackText"><li
								style="margin-left: 20px;">Click the End session link at
									the top of the page</li></span></td>
						<td valign="top" width="10"><img
							src="/static/images/spacer.gif" border="0" width="10"
							alt=""></td>
					</tr>
					<tr>
						<td valign="top" height="10" colspan="3"><img
							src="/static/images/spacer.gif" border="0" height="10"
							alt=""></td>
					</tr>
					<tr>
						<td valign="top" colspan="3"><table border="0" width="70%"
								cellspacing="0" cellpadding="0" align="center">
								<tbody>
									<tr>
										<td valign="top" width="100%" height="2" bgcolor="#3173B5"
											colspan="4"></td>
									</tr>
									<tr>
										<td valign="top" width="2" bgcolor="#3173B5"><img
											src="/static/images/s.gif" border="0" width="2" alt=""></td>
										<td valign="top" width="4"><img
											src="/static/images/s.gif" border="0" width="4" alt=""></td>
										<td valign="top">
												<table border="0" width="200" cellspacing="0"
													cellpadding="0">
													<tbody>
														<tr>
															<td valign="top" height="10" colspan="2"></td>
														</tr>
														<tr>
															<td align="left" class="MedBlackText" nowrap="nowrap">&nbsp;Record output:</td>
															<td valign="top" align="left">
			                                                  <select name="displayformat" id="displayformat">
		                                                        <option value="citation" <c:if test="${dlOutput eq 'citation'}">selected="selected"</c:if>>Citation</option>
		                                                        <option value="abstract" <c:if test="${dlOutput eq 'abstract'}">selected="selected"</c:if>>Abstract</option>
		                                                        <option value="detailed" <c:if test="${dlOutput eq 'detailed'}">selected="selected"</c:if>>Detailed record</option>
		                                                    </select></td>
														</tr>
													</tbody>
												</table>
												<table border="0" width="379px" cellspacing="0"
													cellpadding="0">
													<tbody>
														<tr>
															<td valign="top" height="4" colspan="2"></td>
														</tr>
														<tr>
															<td valign="top"><input type="radio" id="rdRis" name="downloadformat" value="ris" style="margin-top:1px" <c:if test="${dlFormat eq 'ris'}">checked="checked"</c:if>/></td>
															<td valign="bottom"><span class="MedBlackText"><label
																	for="rdRis">RIS, EndNote, ProCite, Reference
																		Manager</label></span></td>
														</tr>
														<tr>
															<td valign="top" height="3" colspan="2"></td>
														</tr>
														<tr>
															<td valign="top"><input type="radio" id="rdBib" name="downloadformat" value="bib" style="margin-top:1px" <c:if test="${dlFormat eq 'bib'}">checked="checked"</c:if>/></td>
															<td valign="bottom"><span class="MedBlackText"><label
																	for="rdBib">BibTex format</label></span></td>
														</tr>
														<tr>
															<td valign="top" height="3" colspan="2"></td>
														</tr>
														<tr>
															<td valign="top"><input type="radio" id="rdRef" name="downloadformat" value="refworks" style="margin-top:1px" <c:if test="${dlFormat eq 'refworks'}">checked="checked"</c:if>/></td>
															<td valign="bottom"><span class="MedBlackText"><label
																	for="rdRef">RefWorks direct import</label></span></td>
														</tr>
														<tr>
															<td valign="top" height="3" colspan="2"></td>
														</tr>
														<tr>
															<td valign="top"><input type="radio" id="rdAsc" name="downloadformat" value="ascii" style="margin-top:1px" <c:if test="${dlFormat eq 'ascii'}">checked="checked"</c:if>/></td>
															<td valign="bottom"><span class="MedBlackText"><label
																	for="rdAsc">Plain text format (ASCII)</label></span></td>
														</tr>

														<tr>
														<td colspan="2" >
															<table style="padding-right:5px;background-color:#bbdad2;border: 2px solid #148C75;margin: 5px;margin:5px;" cellpadding="0" cellspacing="0">
															<tr >
																<td valign="top"  colspan="2" style="text-align:center;"><span style="font-size:12px;"> <b>New Download Formats<b></span></td>
															</tr>

															<!--CSV download code starts here-->
															<tr >
																<td valign="top"><input type="radio" id="rdCsv" name="downloadformat" value="csv" style="margin-top:1px" <c:if test="${dlFormat eq 'csv'}">checked="checked"</c:if>/></td>
																<td valign="bottom"><span class="MedBlackText"><label
																		for="rdCsv">CSV (Comma Separated Value Format)</label></span></td>
															</tr>
															<!--CSV download code ends here-->
															<!--Excel download code starts here-->
															<tr>
																<td valign="top" height="3" colspan="2"></td>
															</tr>
															<tr >
																<td valign="top"><input type="radio" id="rdExcel" name="downloadformat" value="excel" style="margin-top:1px" <c:if test="${dlFormat eq 'excel'}">checked="checked"</c:if>/></td>
																<td valign="bottom"><span class="MedBlackText"><label
																		for="rdExcel">Excel (Microsoft Excel&reg;)</label></span></td>
															</tr>
															<!--Excel download code ends here-->
															<!--PDF download code starts here-->
															<tr >
																<td valign="top" height="3" colspan="	2"></td>
															</tr>
															<tr >
																<td valign="top"><input type="radio" id="rdPdf" name="downloadformat" value="pdf" style="margin-top:1px" <c:if test="${dlFormat eq 'pdf'}">checked="checked"</c:if>/></td>
																<td valign="bottom"><span class="MedBlackText"><label
																		for="rdPdf">PDF (Portable Document Format)</label></span></td>
															</tr>
															<!--PDF download code ends here-->
															<!--RTF download code starts here-->
															<tr>
																<td valign="top" height="3" colspan="2"></td>
															</tr>
															<tr >
																<td valign="top"><input type="radio" id="rdRtf" name="downloadformat" value="rtf" style="margin-top:1px" <c:if test="${dlFormat eq 'rtf'}">checked="checked"</c:if>/></td>
																<td valign="bottom"><span class="MedBlackText"><label
																		for="rdRtf">RTF (Rich Text Format, e.g. Word&reg;)</label></span></td>
															</tr>
															<!--RTF download code ends here-->


															<tr >
																<td valign="top" height="10" colspan="2"></td>
															</tr>
															</Table>
															</td>
														</tr>
														<tr>
															<td valign="top" colspan="2">
																<span class="MedBlackText" style="padding-left: 3px;"><input type="submit"	name="submit" style="height:27px" value="Download"></span>
																<c:if test="${actionBean.saveToGoogleEnabled}">
																	<span>&nbsp;</span>
																	<span id="savetodrivebuttoncontainer"  >
																		<a id="savetodrivebutton" onmouseover="tooltip_show();" onmouseout="tooltip_hide();" title="Save to Google Drive" href="#">
																			<img src="/static/images/savetodriveoriginal.jpg">
																		</a>
																	</span>
																</c:if>
																<c:if test="${actionBean.saveToDropboxEnabled}">
																	<span>&nbsp;</span>
																	<span>&nbsp;</span>
																	<span class="MedBlackText"style="border: 1px solid #01A9DB;padding-bottom:6px;padding-top:4px;padding-left:2px;padding-right:4px"><a href id="dropBoxLink" onmouseover="dbx_tooltip_show();" onmouseout="dbx_tooltip_hide();" title="Save to Dropbox"  value="Dropbox"><img width="70px"  src="/static/images/dropbox.jpg"></a></span>
																</c:if>

																<c:if test="${actionBean.saveToGoogleEnabled || actionBean.saveToDropboxEnabled}">
																	<span>  </span>
																	<span id="savetodrivemsg" style="font-size:80%;font-weight:bold;color: #148C75;visibility:hidden"></span>
																</c:if>
																<c:if test="${actionBean.saveToGoogleEnabled}">
																	<div><span style="font-size:90%;font-weight:bold;color: #148C75;" id="userMsg">Save to Google Drive is compatible with most browsers. <br><a title="Learn more about Save to Google Drive" alt="Learn more about Save to Google Drive" onclick="helpurlClick(this);return false;"class="helpurl" href="${actionBean.helpUrl}#email_print_downld_ev.htm#downldGoogle_Drive">Learn More...</a></span></div>
																</c:if>
																<c:if test="${!actionBean.saveToGoogleEnabled}">
																	<span><br><br></span>
																</c:if>

															</td>
														</tr>
													</tbody>
												</table>
											</td>
										<td valign="top" width="2" bgcolor="#3173B5"></td>
									</tr>
									<tr>
										<td valign="top" width="100%" cellspacing="0" cellpadding="0"
											height="2" bgcolor="#3173B5" colspan="4"></td>
									</tr>
								</tbody>
							</table></td>
					</tr>
				</tbody>
			</table>
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
		<c:if test="${actionBean.saveToGoogleEnabled}">
			<script language="javascript"  src="/static/js/savetogoogle.js?v=${releaseversion}"></script>
		</c:if>
		<c:if test="${actionBean.saveToDropboxEnabled}">
			<script language="javascript"  src="/static/js/savetodropbox.js?v=${releaseversion}"></script>
		</c:if>
		<script language="javascript" type="text/javascript">
			$("#download").submit(function(event) {

				var baseaddress = $("input[name='baseaddress']").val();
				var displaytype = $("#displayformat").val();
				var downloadformat = $('input[name="downloadformat"]:checked').val();
				var docidlist = $("#docidlist").val();
				var handlelist = $("#handlelist").val();
				var folderid = $("#folderid").val();
				var milli = (new Date()).getTime();

				if (downloadformat == undefined || downloadformat == "") {
					alert("You must choose a download format.");
					event.preventDefault();
					return (false);
				}

				var url = "";
				GALIBRARY.createWebEventWithLabel('Output', 'Download', downloadformat);
				// Refworks?
				if (downloadformat == "refworks") {
					var refworksURL = "http://www.refworks.com/express/ExpressImport.asp?vendor=Engineering%20Village%202&filter=Desktop%20Biblio.%20Mgt.%20Software";
					url = "http://" + baseaddress
							+ "/delivery/download/refworks.url?downloadformat="
							+ downloadformat + "&timestamp=" + milli
							+ "&sessionid=" + $("#sessionid").val();
					if (docidlist && docidlist.length > 0)
						url += "&docidlist=" + docidlist;
					if (handlelist && handlelist.length > 0)
						url += "&handlelist=" + handlelist;
                    if (folderid && folderid.length > 0)
                        url += "&folderid=" + folderid;

					window
							.open(
									refworksURL + "&url=" + escape(url),
									"RefWorksMain",
									"width=800,height=500,scrollbars=yes,menubar=yes,resizable=yes,directories=yes,location=yes,status=yes");
                    event.preventDefault();
                    return false;
				}
				return true;

			});
		</script>
	</stripes:layout-component>
</stripes:layout-render>