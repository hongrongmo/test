<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/tlds/datatags.tld" prefix="data" %>
	<c:forEach items="${actionBean.results}" var="result" varStatus="status">
	<c:set var="resultnum" value="${(actionBean.pagenav.currentpage - 1) * actionBean.pagenav.resultsperpage + status.count}" />
	<c:set var="displaywhite" value="${status.count}" />
			<div class="result<c:if test="${status.count % 2 eq 0}"> odd</c:if>">

		   <c:choose>
			<c:when test="${param.displaytype eq 'viewfolders'}">
				<div style="float: left; line-height:1.9em; padding: 6px 1px 10px 5px ">
					<span>${result.documentbaskethitindex}.&nbsp;</span>
				</div>
				<div style="float: left; line-height:1.6em; padding: 7px 0 10px 0; margin-right: 5px " >
					<span><a href="/selected/deletefolder.url?CID=deleteFromSavedRecords&format=abstract&database=${actionBean.database}&docid=${result.doc.docid}&folderid=${actionBean.folderid}" title="Remove record">
					<img border="0" style="padding: 2.5px 0 2.5px 0; margin-top:0.75px" src="/static/images/Remove.png"/>
					</a></span></div>
			 </c:when>
			 <c:otherwise>
					<div style="float: left; line-height:1.9em; padding: 6px 1px 10px 5px ">
					<span>${resultnum}.&nbsp;</span>
					</div>
					<div style="float: left; line-height:1.6em; padding: 7px 0 10px 0; margin-right: 5px " >
					<span>
					<a href="/selected/delete.url?CID=deleteFromSelectedSet&reruncid=${param.CID}&docid=${result.doc.docid}&basketsize=${actionBean.resultsperpage}&handle=${result.doc.hitindex}&database=${actionBean.database}&searchtype=${actionBean.searchtype}&searchid=${actionBean.searchid}&searchresults=${actionBean.searchresultsencoded}&newsearch=${actionBean.newsearchencoded}&backindex=${actionBean.backindex}" title="Remove record">
					<img border="0" style="padding: 2.5px 0 2.5px 0; margin-top:0.75px" src="/static/images/Remove.png"/>
					</a></span></div>
			</c:otherwise>
			</c:choose>

			<table id="abstractlayout" style="padding-top:6px;">
			<tbody>
				<tr>
					<td style="white-space: nowrap; font-size:14px; padding-top:2px; *padding-top:0px"></td>
					<td style="*padding-top:3px">
						<p class="title"><data:highlighttag value="${result.title}" on="${actionBean.ckhighlighting}"/></p>
						<div class="clear"></div>

						<p class="result" style="margin-top: 3px">
						<c:if test="${not empty result.bti}"><b>${result.bti}</b></c:if>
						<c:if test="${not empty result.btst}"><b>: ${result.btst}</b></c:if>
						<c:if test="${not empty result.bpp and result.bpp ne '0'}">, ${result.bpp}</c:if>
						</p>
						<p class="authors" style="margin-top: 3px">
						<c:if test="${not empty result.authors}">
							<c:if test="${result.doc.dbmask == 2048}"><b>Inventor(s): </b></c:if></c:if>
							<span class="authors">
			<c:forEach items="${result.authors}" var="author" varStatus="austatus">
				<c:choose>
				<c:when test="${not empty author.searchlink and not (author.nameupper eq 'ANON')}"><a href="${author.searchlink}" title="Search Author">${author.name}</a><c:if test="${not empty author.affils}"><sup><c:forEach items="${author.affils}" varStatus="afstatus" var="affil"><c:if test="${afstatus.count > 1}">, </c:if><c:if test="${affil.id ne '0'}">${affil.id}</c:if></c:forEach></sup></c:if><c:if test="${not empty author.email}">&nbsp;<a href="mailto:${author.email}" title="Author email" class="emaillink"><img src="/static/images/emailfolder.gif"/></a></c:if><c:if test="${(austatus.count < fn:length(result.authors))}">; </c:if></c:when>
				<c:otherwise>${author.name}<c:if test="${(austatus.count > 1) and (austatus.count < fn:length(result.authors))}">; </c:if></c:otherwise>
				</c:choose>
			</c:forEach>
			<c:if test="${not empty result.pf}">(<data:highlighttag value="${result.pf}" on="${actionBean.ckhighlighting}"/>);</c:if>
							</span>
						</p>
			<c:if test="${not empty result.editors}">
			<p class="editors">
			<b>Editor<c:if test="${fn:length(result.editors) > 1}">s</c:if>: </b>
			<c:forEach items="${result.editors}" var="editor" varStatus="edstatus">
				<a href="${editor.searchlink}" title="Search Editors">${editor.name}</a><c:if test="${(edstatus.count < fn:length(result.editors))}">; </c:if>
			</c:forEach>
			</p>
			</c:if>     <p class="result" >
						<c:if test="${not empty result.assigneelinks}"> <b>Assignee: </b><c:forEach items="${result.assigneelinks}" var="link" varStatus="status"><c:if test="${status.count>1}">&nbsp;-&nbsp;</c:if>${link}</c:forEach></c:if>
						<c:if test="${not empty result.patassigneelinks}"> <b>Patent assignee: </b><c:forEach items="${result.patassigneelinks}" var="link" varStatus="status"><c:if test="${status.count>1}">&nbsp;-&nbsp;</c:if>${link}</c:forEach></c:if>
						</p>
				<p class="result" >
				    <data:resultformat result="${result}" name="abstractsourceline" on="${actionBean.ckhighlighting}"/>
				</p>
				<%-- <p class="result" >
				    <c:if test="${not empty result.fttj}">${result.fttj}</c:if>
				</p> --%>
                <c:if test="${'Article in Press' eq result.doctype}">
                <p><img src="/static/images/btn_aip.gif" border="0"  title="Articles not published yet, but available online"><span style="vertical-align:text-top"> Article in Press</span>&nbsp;<a href="${actionBean.helpUrl}#Art_in_Press.htm" alt="Information about Article in Press" title="Information about Article in Press" class="helpurl"><img src="/static/images/i.png" border="0" alt=""/></a></p></c:if>
                <c:if test="${'In Process' eq result.doctype}">
                <p><img src="/static/images/btn_aip.gif" border="0"  title="Records still in the process of being indexed, but available online" alt="Records still in the process of being indexed, but available online"><span style="vertical-align:text-top"> In Process</span>&nbsp;<a href="${actionBean.helpUrl}#GeoRefInProc.htm" alt="Information about In Process" title="Information about In Process" class="helpurl"><img src="/static/images/i.png" border="0" alt=""/></a></p></c:if>
				<div class="clear"></div>

				<c:if test ="${not empty result.affils}">
				<p class="affils sectionstart"><b>Author affiliation<c:if test="${fn:length(result.affils) > 1}">s</c:if>:</b></p>
				<c:forEach items="${result.affils}" var="affil" varStatus="afstatus">
				<p style="margin:2px 6px 2px 0">
				<c:if test="${affil.id ne '0'}"><sup>${affil.id}</sup></c:if>&nbsp;<data:highlighttag value="${affil.name}" on="${actionBean.ckhighlighting}"/>
				</p>
				</c:forEach>
				</c:if>

	<c:if test="${not empty result.abstractrecord.text}">
				<p class="abstracttext sectionstart"><b>${result.abstractrecord.label}: </b></p>
				<p><data:highlighttag value="${result.abstractrecord.text}" on="${actionBean.ckhighlighting}" v2="${result.abstractrecord.v2}"/><c:if test="${result.abstractrecord.refcount > 0}">(${result.abstractrecord.refcount} refs)</c:if></p>
	</c:if>
	<c:if test="${not empty result.abstractrecord.bookdescription}">
					<!-- ******************************************************** -->
					<!-- BOOK DESCRIPTION! -->
					<!-- ******************************************************** -->
					<p class="abstracttext sectionstart"><b>${result.abstractrecord.label}: </b></p>
							<p><data:highlighttag value="${result.abstractrecord.bookdescription}" on="${actionBean.ckhighlighting}" v2="${result.abstractrecord.v2}"/></p>
	</c:if>
	 <!-- missing IMG -->
	 <c:if test="${not empty result.abstractrecord.figures}">
							<p class="figures"><c:forEach var="figure" items="${result.abstractrecord.figures}"><a href="javascript:window.open('${actionBean.s3FigUrl}${figure}','newwind','width=650,height=600,scrollbars=yes,resizable,statusbar=yes');void('');"><img src="${actionBean.s3FigUrl}${figure}" alt="" border="1" width="100" height="100"/></a></c:forEach></p>
				</c:if>

	 <%-- <c:if test="${not empty result.at}">
				<p><b>Abstract type: </b>${result.at}</p>
	 </c:if> --%>
				<%-- ************************************************************************* --%>
				<%-- KeyWords                            					                   --%>
				<%-- ************************************************************************* --%>
	 <c:if test="${not empty result.abstractrecord.termmap['BKYS']}">
				<p><b>Keywords: </b><c:forEach var="term" items="${result.abstractrecord.termmap['BKYS']}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if><c:choose><c:when test="${not empty term.searchlink}"><data:highlighttag value="${term.searchlink}" on="${actionBean.ckhighlighting}"/></c:when><c:otherwise><data:highlighttag value="${term.value}" on="${actionBean.ckhighlighting}"/></c:otherwise></c:choose></c:forEach></p>
	</c:if>
				<%-- ************************************************************************* --%>
				<%-- Companies                                                                 --%>
				<%-- ************************************************************************* --%>

	<c:if test="${not empty result.abstractrecord.termmap['CPO']}">
					<p class="controlledterms"><b>${result.labels['CPO']}: </b><c:forEach var="term" items="${result.abstractrecord.termmap['CPO']}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if><c:choose><c:when test="${not empty term.searchlink}"><data:highlighttag value="${term.searchlink}" on="${actionBean.ckhighlighting}"/></c:when><c:otherwise><data:highlighttag value="${term.value}" on="${actionBean.ckhighlighting}"/></c:otherwise></c:choose></c:forEach></p>
	</c:if>

				<%-- ************************************************************************* --%>
				<%-- Chemicals                                                                 --%>
				<%-- ************************************************************************* --%>

	<c:if test="${not empty result.abstractrecord.termmap['CMS']}">
					<p class="controlledterms"><b>${result.labels['CMS']}: </b><c:forEach var="term" items="${result.abstractrecord.termmap['CMS']}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if><c:choose><c:when test="${not empty term.searchlink}"><data:highlighttag value="${term.searchlink}" on="${actionBean.ckhighlighting}"/></c:when><c:otherwise><data:highlighttag value="${term.value}" on="${actionBean.ckhighlighting}"/></c:otherwise></c:choose></c:forEach></p>
	</c:if>

				<%-- ************************************************************************* --%>
				<%-- Major terms                                                               --%>
				<%-- ************************************************************************* --%>
	<c:if test="${not empty result.abstractrecord.termmap['MJSM']}">
					<p class="controlledterms"><b>${result.labels['MJSM']}: </b><c:forEach var="term" items="${result.abstractrecord.termmap['MJSM']}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if><c:choose><c:when test="${not empty term.searchlink}"><data:highlighttag value="${term.searchlink}" on="${actionBean.ckhighlighting}"/></c:when><c:otherwise><data:highlighttag value="${term.value}" on="${actionBean.ckhighlighting}"/></c:otherwise></c:choose></c:forEach></p>
	</c:if>

				<%-- ************************************************************************* --%>
				<%-- Main heading terms                                                        --%>
				<%-- ************************************************************************* --%>
	<c:if test="${not empty result.abstractrecord.termmap['MH']}">
					<p class="mainheading"><b>${result.labels['MH']}: </b><c:forEach var="term" items="${result.abstractrecord.termmap['MH']}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if><c:choose><c:when test="${not empty term.searchlink}"><data:highlighttag value="${term.searchlink}" on="${actionBean.ckhighlighting}"/></c:when><c:otherwise><data:highlighttag value="${term.value}" on="${actionBean.ckhighlighting}"/></c:otherwise></c:choose></c:forEach></p>
	</c:if>

				<%-- ************************************************************************* --%>
				<%-- Controlled vocabulary terms                                               --%>
				<%-- ************************************************************************* --%>
	<c:if test="${not empty result.abstractrecord.termmap['CVS']}">
					<p class="controlledterms"><b>${result.labels['CVS']}: </b><c:forEach var="term" items="${result.abstractrecord.termmap['CVS']}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if><c:choose><c:when test="${not empty term.searchlink}"><data:highlighttag value="${term.searchlink}" on="${actionBean.ckhighlighting}"/></c:when><c:otherwise><data:highlighttag value="${term.value}" on="${actionBean.ckhighlighting}"/></c:otherwise></c:choose></c:forEach></p>
	</c:if>

	  <!-- Missing CHS  --no code found -->

				<%-- ************************************************************************* --%>
				<%-- CAS Registry terms                                                        --%>
				<%-- ************************************************************************* --%>
	<c:if test="${not empty result.abstractrecord.termmap['CRM']}">
					<p class="controlledterms"><b>${result.labels['CRM']}: </b><c:forEach var="term" items="${result.abstractrecord.termmap['CRM']}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if><c:choose><c:when test="${not empty term.searchlink}"><data:highlighttag value="${term.searchlink}" on="${actionBean.ckhighlighting}"/></c:when><c:otherwise><data:highlighttag value="${term.value}" on="${actionBean.ckhighlighting}"/></c:otherwise></c:choose></c:forEach></p>
	</c:if>
				<%-- ************************************************************************* --%>
				<%-- Uncontrolled terms                                                        --%>
				<%-- ************************************************************************* --%>
	<c:if test="${not empty result.abstractrecord.termmap['FLS']}">
					<p class="controlledterms"><b>${result.labels['FLS']}: </b><c:forEach var="term" items="${result.abstractrecord.termmap['FLS']}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if><c:choose><c:when test="${not empty term.searchlink}"><data:highlighttag value="${term.searchlink}" on="${actionBean.ckhighlighting}"/></c:when><c:otherwise><data:highlighttag value="${term.value}" on="${actionBean.ckhighlighting}"/></c:otherwise></c:choose></c:forEach></p>
	</c:if>

				<%-- ************************************************************************* --%>
				<%-- Classification codes   CLS                                                   --%>
				<%-- ************************************************************************* --%>
				<c:if test="${not empty result.abstractrecord.classificationcodes}">
				<c:forEach var="clcodelist" items="${result.abstractrecord.classificationcodes}">
				<p class="classificationcode"><b>Classification Code: </b><c:forEach var="clcode" items="${clcodelist.value}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if>${clcode.linkedid}${clcode.title}</c:forEach></p>
				</c:forEach>
				</c:if>

				<%-- ************************************************************************* --%>
				<%-- Regional terms                                                            --%>
				<%-- ************************************************************************* --%>
	<c:if test="${not empty result.abstractrecord.termmap['RGIS']}">
					<p class="controlledterms"><b>${result.labels['RGIS']}: </b><c:forEach var="term" items="${result.abstractrecord.termmap['RGIS']}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if><c:choose><c:when test="${not empty term.searchlink}"><data:highlighttag value="${term.searchlink}" on="${actionBean.ckhighlighting}"/></c:when><c:otherwise><data:highlighttag value="${term.value}" on="${actionBean.ckhighlighting}"/></c:otherwise></c:choose></c:forEach></p>
	</c:if>

				<%-- ************************************************************************* --%>
				<%-- IPC Code terms                                                            --%>
				<%-- ************************************************************************* --%>
	<c:if test="${not empty result.abstractrecord.termmap['PIDM']}">
					<p class="controlledterms"><b>${result.labels['PIDM']}: </b><c:forEach var="term" items="${result.abstractrecord.termmap['PIDM']}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if><c:choose><c:when test="${not empty term.searchlink}">${term.searchlink}</c:when><c:otherwise>${term.value}</c:otherwise></c:choose></c:forEach></p>
	</c:if>

	<c:if test="${not empty result.abstractrecord.termmap['PIDEPM']}">
					<p class="controlledterms"><b>${result.labels['PIDEPM']}: </b><c:forEach var="term" items="${result.abstractrecord.termmap['PIDEPM']}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if><c:choose><c:when test="${not empty term.searchlink}">${term.searchlink}</c:when><c:otherwise>${term.value}</c:otherwise></c:choose></c:forEach></p>
	</c:if>


	<c:if test="${not empty result.abstractrecord.termmap['PIDM8']}">
					<p class="controlledterms"><b>${result.labels['PIDM8']}: </b><c:forEach var="term" items="${result.abstractrecord.termmap['PIDM8']}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if><c:choose><c:when test="${not empty term.searchlink}">${term.searchlink}</c:when><c:otherwise>${term.value}</c:otherwise></c:choose></c:forEach></p>
	</c:if>

	<c:if test="${not empty result.abstractrecord.termmap['PUCM']}">
					<p class="controlledterms"><b>${result.labels['PUCM']}: </b><c:forEach var="term" items="${result.abstractrecord.termmap['PUCM']}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if><c:choose><c:when test="${not empty term.searchlink}">${term.searchlink}</c:when><c:otherwise>${term.value}</c:otherwise></c:choose></c:forEach></p>
	</c:if>
	<c:if test="${not empty result.abstractrecord.termmap['PECM']}">
					<p class="controlledterms"><b>${result.labels['PECM']}: </b><c:forEach var="term" items="${result.abstractrecord.termmap['PECM']}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if><c:choose><c:when test="${not empty term.searchlink}">${term.searchlink}</c:when><c:otherwise>${term.value}</c:otherwise></c:choose></c:forEach></p>
	</c:if>
	 <c:if test="${not empty result.collection}">
					<p><b>Collection name:</b>${result.collection}</p>
	</c:if>
	 			<%-- ************************************************************************* --%>
				<%-- Other info                                                                --%>
				<%-- ************************************************************************* --%>
	<c:if test="${not empty result.abstractrecord.termmap['LOCS']}">	<%-- Coordinates --%>
				<p class="coordinates"><b>${result.labels['LOCS']}: </b><c:forEach var="term" items="${result.abstractrecord.termmap['LOCS']}" varStatus="status"><c:if test="${status.count > 1}">;&nbsp;</c:if>${term.value}</c:forEach></p>
	</c:if>
				<%-- ************************************************************************* --%>
				<%-- Treatments                                                                --%>
				<%-- ************************************************************************* --%>
	<c:if test="${not empty result.abstractrecord.treatments}">
					<p class="treatments"><b>${result.labels['TRS']}: </b><c:forEach var="treatment" items="${result.abstractrecord.treatments}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if>${treatment}</c:forEach></p>
	</c:if>

				<p class="database"><b>Database: </b><span class="dbnameidentifier">${result.doc.dbname}</span></p>
				<c:if test="${result.doc.dbmask == 2097152}">
					<p>${result.cpr}</p>
				</c:if>
			</td>
		</tr>
	</tbody>
	</table>
			<c:choose>
			<c:when test="${(not empty result.lhlinkObjects) or (result.fulltext) or (actionBean.lindahall)}">
				<div id="deliverylinks" style="margin-left:92.5px;width:90.5%">
					<div id="deliveryheader" style="padding: 3px 3px;<c:if test="${displaywhite % 2 eq 0}"> background-color: #FFFFFF;</c:if>">Full-text and Local Holdings Links</div>
					<div id="deliverybody">
						<c:if test="${not empty result.lhlinkObjects}">
							<p>
							<c:forEach items="${result.lhlinkObjects}" var="lhlink" varStatus="status">
								<c:if test="${not empty lhlink.url}">
									<c:if test="${status.count>1}"><span class="pipe">|</span></c:if>
									<c:choose>
		                        		<c:when test="${not empty lhlink.imageUrl}">
		                        			<a CLASS="LgBlueLink" title="Right-click to open in new tab" onclick="openLocalHoldingsLink(event,'Abstract Format',this);" href="/search/results/localholdinglinks.url?docId=${result.doc.docid}&url=${lhlink.url}&position=${lhlink.position}" target="new">
		                        				<img src="${lhlink.imageUrl}" alt="${lhlink.label}" border="0" />
		                        			</a>
										</c:when>
		                        		<c:otherwise>
		                        			<a CLASS="LgBlueLink" title="Right-click to open in new tab" onclick="openLocalHoldingsLink(event,'Abstract Format',this);" href="/search/results/localholdinglinks.url?docId=${result.doc.docid}&url=${lhlink.url}&position=${lhlink.position}" target="new">${lhlink.label}</a>
		                        		</c:otherwise>
		                    		</c:choose>
								</c:if>
							</c:forEach>
							</p>
						</c:if>
					<c:if test="${result.fulltext}">
						<c:if test="${not(fn:substring(result.doc.docid,0,3) eq 'ref')}">
							<p>
								<a href="javascript:newwindow=window.open('/search/results/fulltext.url?docID=${result.doc.docid}','newwindow','width=500,height=500,toolbar=no,location=no,scrollbars,resizable'); void('');" title="Full-text"><img id="ftimg" class="fulltext" src="/static/images/full_text.png" alt="Full-text"></a>
							</p>
						</c:if>
					</c:if>
					<c:if test="${result.doc.dbmask ne '131072' and actionBean.lindahall}">
	    			<p><a title="Linda Hall Library document delivery service" href="javascript:lindaHall('${actionBean.sessionid}','${result.doc.docid}','${result.doc.dbid}')">Linda Hall Library document delivery service</a></p>
	    			</c:if>
					</div>
				</div>
			</c:when>
			<c:otherwise>
			<div style="marign-top:4px;marign-bottom:4px">&nbsp;</div>
			</c:otherwise>
			</c:choose>
			<c:if test="${'131072' eq result.doc.dbmask}">
			<div style="margin-left:92.5px;margin-top:1px;width:90.5%;padding-bottom:10px;">
					<c:if test="${not empty result.readpagelink}">${result.readpagelink}</c:if>
					<c:if test="${not empty result.readchapterlink}"><c:if test="${not empty result.readpagelink}"><span class="pipe">|</span></c:if>${result.readchapterlink}</c:if>
					<c:if test="${not empty result.readbooklink}"><c:if test="${not empty result.readpagelink or not empty result.readchapterlink}"><span class="pipe">|</span></c:if>${result.readbooklink}</c:if>
					</div>
			</c:if>
            </div>
            <div class="clear"></div>
		</c:forEach>
