<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8" trimDirectiveWhitespaces="true"  session="false"%>

<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld"	prefix="stripes"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/tlds/datatags.tld" prefix="data" %>
<c:set var="fromResults" value="true"></c:set>
<%@ include file="/WEB-INF/pages/include/highlight.jsp" %>
		<c:choose>
			<c:when test="${not(actionBean.linkResultCount eq '') && not(actionBean.linkResultCount eq 'null')}">
			   <c:set var="dedupResultCount" value="${actionBean.linkResultCount}"/>
			</c:when>
			<c:otherwise>
			 <c:set var="dedupResultCount" value="${actionBean.dedupsummary.dedupsetCount}"/>
			</c:otherwise>
		</c:choose>
		<h2 id="resultslistheader" style="display:none">Records list</h2>


		<div id="resultslist" aria-label="Results" role="main">
			<%--
				Iterate over results
			--%>
			<c:forEach items="${actionBean.results}" var="result" varStatus="status">
        <%-- Mendeley COinS item --%>
        <data:mendeleyformat result="${result}"/>

			<c:set var="resultnum" value="${(actionBean.pagenav.currentpage - 1) * actionBean.pagenav.resultsperpage + status.count}" />
			<span session-id="${actionBean.sessionid}" security="${result.citedby.md5}" an="${result.accnum}" doi="${result.citedby.doi}" page="${result.citedby.firstpage}" volume="${result.citedby.firstvolume}" issue="${result.citedby.firstissue}" issn="${result.citedby.issn}"  isbn="${result.citedby.isbn}" isbn13="${result.citedby.isbn13}"  style="display: none;" name="citedbyspan"></span>
			<div class="result<c:if test="${status.count % 2 eq 0}"> odd</c:if>">

				<div id="divresultnum"><c:choose>
			<c:when test="${param.displaytype eq 'patentref'}"> ${result.doc.hitindex}</c:when><c:otherwise>${resultnum}</c:otherwise></c:choose>.</div>
				<div id="divcbresult"><input type="checkbox" name="cbresult" id="cbresult${resultnum}" title="Select Result ${resultnum} Checkbox" handle="${result.doc.hitindex}" docid="${result.doc.docid}" dbid="${result.doc.dbid}"<c:if test="${result.selected}"> checked="checked" basketid="${result.basketsearchid}"</c:if>/>
				</div>

				<%--
					Results item
				--%>
				<div class="resultcontent">
				<label for="cbresult${resultnum}">
				<c:if test="${'0' eq result.bpp}"><img border="0" width="56" height="72" style="float:left; margin-right:5px;" title="${result.bti}" src="${result.bimg}/images/${result.isbn13}/${result.isbn13}small.jpg"/></c:if>
				<c:if test="${not empty result.bti}"><p class="resulttitle" id="resulttitle_${resultnum}"><b>${result.bti}</b><c:if test="${not empty result.btst}"><b>: ${result.btst}</b></c:if><c:if test="${'0' ne result.bpp}">, Page ${result.bpp}</c:if>
				<c:if test="${not empty result.bct}"><p><b>Chapter:</b> ${result.bct}</c:if></p></c:if>
				<c:if test="${not empty result.title}"><p class="resulttitle" id="resulttitle_${resultnum}"><b>${result.title}</b></p></c:if>
				</label>
				<p class="authorwrap" style="line-height:16px;">
					<c:if test="${not empty result.authors}">
					<span class="author">
					<c:forEach items="${result.authors}" var="author" varStatus="austatus">
						<c:choose>
							<c:when test="${not empty author.searchlink and not (author.nameupper eq 'ANON')}">
								<a href="${author.searchlink}"  aria-labelledby="resulttitle_${resultnum}" title="Search Author">${author.name}</a><c:if test="${austatus.count == 1}"><c:if test="${not empty result.authors[0].affils && !(result.authors[0].affils[0].name eq null || result.authors[0].affils[0].name eq '')}"><span class="affiliation">&nbsp;(${result.authors[0].affils[0].name})</span></c:if><c:if test="${empty result.authors[0].affils}"><c:if test="${not empty result.affils}"><c:forEach items="${result.affils}" var="affil" varStatus="afstatus"><c:if test="${afstatus.count == 1}"><span class="affiliation">&nbsp;(${affil.name})</span></c:if></c:forEach></c:if></c:if><c:if test="${not empty result.authors[0].affils && (result.authors[0].affils[0].name eq null || result.authors[0].affils[0].name eq '')}"><c:if test="${not empty result.affils}"><c:forEach items="${result.affils}" var="affil" varStatus="afstatus"><c:if test="${afstatus.count == 1}"><span class="affiliation">&nbsp;(${affil.name})</span></c:if></c:forEach></c:if></c:if><c:if test="${austatus.count < fn:length(result.authors)}">;&nbsp;</c:if></c:if><c:choose><c:when test="${(austatus.count > 1) and (austatus.count < fn:length(result.authors))}">;&nbsp;</c:when><c:when test="${austatus.count >= fn:length(result.authors) and not empty result.editors}">,&nbsp;</c:when></c:choose>
							</c:when>
							<c:otherwise>${author.name}<c:if test="${(austatus.count > 1) and (austatus.count < fn:length(result.authors))}">;&nbsp;</c:if></c:otherwise>
						</c:choose>
					</c:forEach>
					<c:choose><c:when test="${'131072' eq result.doc.dbmask and (not empty result.isbn13)}">&nbsp;ISBN-13: ${result.isbn13}</c:when><c:when test="${'131072' eq result.doc.dbmask and (not empty result.isbn)}">&nbsp;ISBN-10: ${result.isbn}</c:when></c:choose><c:if test="${'131072' eq result.doc.dbmask and (not empty result.bpn)}">, ${result.bpn}</c:if><c:if test="${'131072' eq result.doc.dbmask and (not empty result.byr)}">, ${result.byr}</c:if>
					</span>
					</c:if>
					<c:if test="${not empty result.editors}">
						<b>Editor<c:if test="${fn:length(result.editors) > 1}">s</c:if>:&nbsp;</b><c:forEach items="${result.editors}" var="editor" varStatus="edstatus"><a href="${editor.searchlink}">${editor.name}</a><c:if test="${not empty editor.affils}"><span class="affiliation"> (${editor.affils[0].name})</span></c:if><c:if test="${edstatus.count == 1}"><c:if test="${edstatus.count < fn:length(result.editors)}">;&nbsp;</c:if></c:if><c:if test="${(edstatus.count > 1) and (edstatus.count < fn:length(result.editors))}">;&nbsp;</c:if></c:forEach>&nbsp;
					</c:if>
					<%-- SPECIAL FORMAT FOR CSV elements --%>
					<data:resultformat result="${result}" name="citationsourceline"/>
					<c:if test="${'0' ne result.bpp}"><div class="clear"></div></c:if>
              </p>
              <c:if test="${not empty result.doctype && result.doctype eq 'Article in Press'}"><p><img src="/static/images/btn_aip.gif" border="0" style="vertical-align:bottom" alt="Articles not published yet, but available online" title="Articles not published yet, but available online"/>&nbsp;${result.doctype}</p></c:if>
              <c:if test="${not empty result.doctype && result.doctype eq 'In Process'}"><p><img src="/static/images/btn_aip.gif" border="0" style="vertical-align:bottom" title="Records still in the process of being indexed, but available online." alt="Records still in the process of being indexed, but available online."/>&nbsp;${result.doctype}</p></c:if>
			  <p>
				<b>Database:</b> ${result.doc.dbname} <c:if test="${not empty result.collection}"><b>&nbsp;Collection:</b> ${result.collection}</c:if>
			  </p>

				<%--
					Results links (Abstract, Detailed, etc.)
				--%>
				<p class="links">

                  <c:if test="${result.dup}">
                  		<a href="#" title="View duplicate" onclick="window.open('/controller/servlet/Controller?CID=viewDups&dups=${result.dupids}', 'newwindow', 'width=800,height=500,toolbar=no,location=no, scrollbars,resizable');return false"><img src="/static/images/d.gif" align="absbottom" border="0"/></a><a CLASS="SmBlackText">&#160; - &#160;</a>
                  </c:if>
				<c:choose>
				<%--
					REFEREX result has different links
				--%>
				<c:when test="${'131072' eq result.doc.dbmask}">
					<div style="line-height: 16px;<c:if test="${'0' eq result.bpp}">margin-left:61px</c:if>">

					<c:if test="${not empty result.pagedetailslink}">${result.pagedetailslink}<span class="pipe">|</span></c:if>
					<a class="externallink" href="/search/book/bookdetailed.url?pageType=book&searchtype=${actionBean.searchtype}&pii=${result.pii}&scope=${param.scope}&SEARCHID=${actionBean.searchid}&DOCINDEX=${result.doc.hitindex}&database=131072&docid=${result.doc.docid}&format=tagSearchDetailedFormat&RESULTCOUNT=${actionBean.resultscount}&INTERNALSEARCH=${result.bpp eq '0' ? 'yes' : 'no'}">Book Details</a>
					<c:if test="${not empty result.readpagelink}"><span class="pipe">|</span>${result.readpagelink}</c:if>
					<c:if test="${not empty result.readchapterlink}"><span class="pipe">|</span>${result.readchapterlink}</c:if>
					</div>
				</c:when>
				<c:otherwise>
					<c:choose>
						<c:when test="${actionBean.dedup}">
						    <c:set var="formatString" value="${result.cidPrefix}AbstractFormat"></c:set>
							<a class="externallink" href="/search/doc/abstract.url?CID=dedupSearchAbstractFormat&pageType=dedupSearch&SEARCHID=${actionBean.searchid}&DOCINDEX=${result.doc.hitindex}&PAGEINDEX=${actionBean.pagenav.currentindex}&database=${actionBean.database}&format=${formatString}&DupFlag=${actionBean.dedup}&dbpref=${actionBean.dedupsummary.dbpref}&fieldpref=${actionBean.dedupsummary.fieldpref}&origin=${actionBean.dedupsummary.origin}&dbLink=${actionBean.dedupsummary.dbLink}&linkSet=${actionBean.dedupsummary.linkSet}&dedupSet=${actionBean.dedupsummary.dedupSubsetCount}&dedupResultCount=${dedupResultCount}" aria-labelledby="resulttitle_${resultnum}" id="abslink_${resultnum}">Abstract</a>
						</c:when>
						<c:otherwise>
	                       <c:choose>
							<c:when test="${param.displaytype eq 'patentref'}">
							<c:if test="${not(fn:substring(result.doc.docid,0,3) eq 'ref')}">
									${result.abstractlink}
							    </c:if>
							</c:when>
							<c:otherwise>
								${result.abstractlink}
							</c:otherwise>
							</c:choose>
						</c:otherwise>
					</c:choose>
					<c:choose>
						<c:when test="${actionBean.dedup}">
						    <c:set var="detailFormatString" value="${result.cidPrefix}DetailedFormat"></c:set>
						    <span class="pipe">|</span>
							<a class="externallink" href="/search/doc/detailed.url?CID=dedupSearchDetailedFormat&pageType=dedupSearch&SEARCHID=${actionBean.searchid}&DOCINDEX=${result.doc.hitindex}&PAGEINDEX=${actionBean.pagenav.currentindex}&database=${actionBean.database}&format=${detailFormatString}&DupFlag=${actionBean.dedup}&dbpref=${actionBean.dedupsummary.dbpref}&fieldpref=${actionBean.dedupsummary.fieldpref}&origin=${actionBean.dedupsummary.origin}&dbLink=${actionBean.dedupsummary.dbLink}&linkSet=${actionBean.dedupsummary.linkSet}&dedupSet=${actionBean.dedupsummary.dedupSubsetCount}&dedupResultCount=${dedupResultCount}" aria-labelledby="resulttitle_${resultnum}" id="detaillink_${resultnum}">Detailed</a>
						</c:when>
						<c:otherwise>
						<c:choose>
							<c:when test="${param.displaytype eq 'patentref'}">
							   <c:if test="${not(fn:substring(result.doc.docid,0,3) eq 'ref')}">
							   		<span class="pipe">|</span>
									${result.detailedlink}
							   </c:if>
							</c:when>
							<c:otherwise>
									<span class="pipe">|</span>
									${result.detailedlink}
							</c:otherwise>
							</c:choose>
						</c:otherwise>
					</c:choose>
					<c:choose>
                        <c:when test="${not empty result.abstractlink}">
                            <span class="pipe">|</span><a id="abstractpreview_${result.doc.hitindex}" num="${result.doc.hitindex}" class="abstractpreview" href="/search/results/preview.url?docId=${result.doc.docid}"><img id="previewimage" src="/static/images/EV_show.png"/>Show preview</a>
                        </c:when>
                        <c:otherwise>
                        </c:otherwise>
                    </c:choose>

						<span id="${result.accnum}" style="display:none"><span class="pipe">|</span><a class="externallink" id="scopuscitedbylink" title="Scopus Cited-by"></a></span>

						<c:if test="${not empty result.abstractrecord.patentrefslink}"><span class="pipe">|</span><a title="Show patents that are referenced by this patent" class="externallink" href="${result.abstractrecord.patentrefslink}">Patent Refs</a> (${result.abstractrecord.patrefcount})</c:if>
						<c:if test="${not empty result.abstractrecord.inspecrefslink}"><span class="pipe">|</span><a title="Show records that are referenced this record" class="externallink" href="${result.abstractrecord.inspecrefslink}">Inspec Refs</a> (${result.abstractrecord.refcount})</c:if>
						<c:if test="${not empty result.abstractrecord.otherrefslink}"><span class="pipe">|</span><a title="Other references" class="externallink" href="${result.abstractrecord.otherrefslink}">Other Refs</a> (${result.abstractrecord.nonpatrefcount})</c:if>
						<c:if test="${not empty result.abstractrecord.citedbyrefslink}"><span class="pipe">|</span><a title="Show patents that reference this patent" class="externallink" href="${result.abstractrecord.citedbyrefslink}">Cited by</a> (${result.abstractrecord.citerefcount})</c:if>
				</c:otherwise>
				</c:choose>
				<c:if test="${result.fulltext}">
					<c:choose>
							<c:when test="${param.displaytype eq 'patentref'}">
							   <c:if test="${not(fn:substring(result.doc.docid,0,3) eq 'ref')}">
									<span class="pipe">|</span><a href="javascript:newwindow=window.open('/search/results/fulltext.url?docID=${result.doc.docid}','newwindow','width=500,height=500,toolbar=no,location=no,scrollbars,resizable'); void('');" title="Full-text" style="margin-left: 2px"><img id="ftimg" class="fulltext" src="/static/images/full_text.png" alt="Full-text"  aria-labelledby="resulttitle_${resultnum}"></a>
							   </c:if>
							</c:when>
							<c:otherwise>
								<span class="pipe">|</span><a href="javascript:newwindow=window.open('/search/results/fulltext.url?docID=${result.doc.docid}','newwindow','width=500,height=500,toolbar=no,location=no,scrollbars,resizable'); void('');" title="Full-text" style="margin-left: 2px"><img id="ftimg" class="fulltext" src="/static/images/full_text.png" alt="Full-text"  aria-labelledby="resulttitle_${resultnum}"></a>
							</c:otherwise>
							</c:choose>
				</c:if>
                <c:if test="${actionBean.context.userSession.user.getPreference('LOCAL_HOLDINGS_CITATION_FORMAT')}">
					<c:if test="${not empty result.lhlinkObjects}">
						<c:forEach items="${result.lhlinkObjects}" var="lhlink" varStatus="status">
							<c:if test="${not empty lhlink.url}">
								<span class="pipe">|</span>
								<c:choose>
	                        		<c:when test="${not empty lhlink.imageUrl}">
	                        			<a CLASS="LgBlueLink" title="Right-click to open in new tab" onclick="openLocalHoldingsLink(event,'Citation Format',this);" href="/search/results/localholdinglinks.url?docId=${result.doc.docid}&url=${lhlink.url}&position=${lhlink.position}" target="new">
	                        				<img src="${lhlink.imageUrl}" alt="${lhlink.label}" border="0" />
	                        			</a>
									</c:when>
	                        		<c:otherwise>
	                        			<a CLASS="LgBlueLink" title="Right-click to open in new tab" onclick="openLocalHoldingsLink(event,'Citation Format',this);" href="/search/results/localholdinglinks.url?docId=${result.doc.docid}&url=${lhlink.url}&position=${lhlink.position}" target="new">${lhlink.label}</a>
	                        		</c:otherwise>
	                    		</c:choose>
							</c:if>
						</c:forEach>
					</c:if>
                </c:if>
<%--
                <c:if test="${actionBean.context.userSession.user.getPreference('LOCAL_HOLDINGS_CITATION_FORMAT')}">
				<data:localHoldings  snvalue="${result.sn}" textzones="${actionBean.context.userSession.userTextZones}" source="results" status="false" docid="${result.doc.docid}" limit="2"/>
				</c:if>
 --%>
				</p>
               <div id="previewtext_${result.doc.hitindex}" class="previewtext"></div>

				</div>

				<div class="clear"></div>
			</div>
			</c:forEach>
		</div>
		<script>
			var show_all = ${actionBean.context.userSession.user.userPrefs.showPreview};
		</script>


