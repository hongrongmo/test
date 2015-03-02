<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>

<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld"	prefix="stripes"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib uri="/WEB-INF/tlds/datatags.tld" prefix="data" %>


	<c:forEach items="${actionBean.results}" var="result" varStatus="status">
	<c:set var="resultnum" value="${(actionBean.pagenav.currentpage - 1) * actionBean.pagenav.resultsperpage + status.count}" />
			<div class="result<c:if test="${status.count % 2 eq 0}"> odd</c:if>">
			<c:choose>
			<c:when test="${param.displaytype eq 'viewfolders'}">
				<div style="float: left; line-height:1.9em; padding: 6px 1px 10px 5px ">
					<span>${result.documentbaskethitindex}.&nbsp;</span>
				</div>
				<div style="float: left; line-height:1.6em; padding: 7px 0 10px 0; margin-right: 5px " >
					<span><a href="/selected/deletefolder.url?CID=deleteFromSavedRecords&format=citation&database=${actionBean.database}&docid=${result.doc.docid}&folderid=${actionBean.folderid}" title="Remove record">
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

				<%--
					Results item
				--%>
				<div class="resultcontent" style="width: 87%;">
				<c:if test="${'0' eq result.bpp}"><img border="0" width="56" height="72" style="float:left; margin-right:5px;" title="${result.bti}" src="${result.bimg}/images/${result.isbn13}/${result.isbn13}small.jpg"/></c:if>
				<c:if test="${not empty result.bti}"><p><b>${result.bti}</b><c:if test="${not empty result.btst}"><b>: ${result.btst}</b></c:if><c:if test="${'0' ne result.bpp}">, Page ${result.bpp}</c:if></p></c:if><c:if test="${not empty result.bct}"><p><b>Chapter:</b> ${result.bct}</p></c:if>
				<p class="resulttitle"><b><data:highlighttag value="${result.title}" on="${actionBean.ckhighlighting}"/></b></p>

	<c:if test="${not empty result.authors and result.doc.dbmask == 2048}"><b>Inventor(s): </b></c:if>
<c:forEach items="${result.authors}" var="author" varStatus="austatus">
	<span class="author">
	<c:choose>
	<c:when test="${not empty author.searchlink and not (author.nameupper eq 'ANON')}">
		<a href="${author.searchlink}" title="Search Author">${author.name}</a><c:if test="${austatus.count == 1}"><c:if test="${not empty result.authors[0].affils}"><span class="affiliation"> (${result.authors[0].affils[0].name})</span></c:if><c:if test="${austatus.count < fn:length(result.authors)}">; </c:if></c:if><c:choose><c:when test="${(austatus.count > 1) and (austatus.count < fn:length(result.authors))}">; </c:when><c:when test="${austatus.count >= fn:length(result.authors) and not empty result.editors}">, </c:when></c:choose>
	</c:when>
	<c:otherwise>
		${author.name}<c:if test="${(austatus.count > 1) and (austatus.count < fn:length(result.authors))}">; </c:if>
	</c:otherwise>
	</c:choose>
	</span>
</c:forEach>
<c:if test="${not empty result.editors}">
<b>Editor<c:if test="${fn:length(result.editors) > 1}">s</c:if>: </b>
<c:forEach items="${result.editors}" var="editor" varStatus="edstatus">
	<a href="${author.searchlink}" title="Search Editor">${editor.name}</a><c:if test="${(edstatus.count > 1) and (edstatus.count < fn:length(result.editors))}">; </c:if>
</c:forEach>
</c:if>
				</p>
				<data:resultformat result="${result}" name="selcitationsourceline" on="${actionBean.ckhighlighting}"/>

				<c:if test="${'0' ne result.bpp}"><div class="clear"></div></c:if>
            		<p>
					<b>Database:</b> <span class="dbnameidentifier">${result.doc.dbname}</span> <c:if test="${not empty result.collection}"><b>Collection:</b> ${result.collection}</c:if>
					</p>
                   <!-- missing FTTJ / STT -->
					<%--
						Results links (Abstract, Detailed, etc.)
					--%>
					<p class="links">

					<c:choose>
					<%--
						REFEREX result has different links
					--%>
					<c:when test="${'131072' eq result.doc.dbmask}">
					<div style="line-height: 16px">
					<c:if test="${not empty result.readpagelink}">${result.readpagelink}</c:if>
					<c:if test="${not empty result.readchapterlink}"><c:if test="${not empty result.readpagelink}"><span class="pipe">|</span></c:if>${result.readchapterlink}</c:if>
					<c:if test="${not empty result.readbooklink}"><c:if test="${not empty result.readpagelink or not empty result.readchapterlink}"><span class="pipe">|</span></c:if>${result.readbooklink}</c:if>
					</div>
					</c:when>
					<c:otherwise>

					<c:if test="${result.fulltext}">
					 	<c:if test="${not(fn:substring(result.doc.docid,0,3) eq 'ref')}">
							<a href="javascript:newwindow=window.open('/search/results/fulltext.url?docID=${result.doc.docid}','newwindow','width=500,height=500,toolbar=no,location=no,scrollbars,resizable'); void('');" title="Full-text" style="margin-left: 2px"><img id="ftimg" class="fulltext" src="/static/images/full_text.png" alt="Full-text"></a>
						</c:if>
					</c:if>

					<c:if test="${actionBean.context.userSession.user.getPreference('LOCAL_HOLDINGS_CITATION_FORMAT')}">
						<c:if test="${not empty result.lhlinkObjects}">
							<c:forEach items="${result.lhlinkObjects}" var="lhlink" varStatus="status">
								<c:if test="${not empty lhlink.url}">
									<c:if test="${status.count>1}"><span class="pipe">|</span></c:if>
									<c:if test="${status.count == 1 and result.fulltext}">
					 					<c:if test="${not(fn:substring(result.doc.docid,0,3) eq 'ref')}">
					 						<span class="pipe">|</span>
										</c:if>
									</c:if>
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

					</c:otherwise>
					</c:choose>
					</p>

				</div>


				<div class="clear"></div>
		</div>
		</c:forEach>
