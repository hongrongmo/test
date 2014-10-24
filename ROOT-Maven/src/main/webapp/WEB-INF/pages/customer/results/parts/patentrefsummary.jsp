<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true" session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

	<div id="querydisplaywrap" aria-label="Search query actions" role="navigation">
		<div id="querydisplay">
			<table id="patentrefsummary">
				<tr>
					<td colspan="2"><b>${actionBean.patentrefsummary.patrefsummsg}</b></td>
				</tr>
				<tr>
					<td colspan="2">
						<c:forEach items="${actionBean.patentrefsummary.presults}" var="resultSummary" varStatus="status">
						<c:if test="${not empty resultSummary.bti}"><p class="resulttitlecolor"><b>${resultSummary.bti}</b><c:if test="${not empty resultSummary.btst}"><b>: ${resultSummary.btst}</b></c:if><c:if test="${'0' ne resultSummary.bpp}">, Page ${resultSummary.bpp}</c:if>
						<c:if test="${not empty resultSummary.bct}"><p><b>Chapter:</b> ${resultSummary.bct}</c:if></p></c:if>
						<c:if test="${not empty resultSummary.title}"><p class="resulttitlecolor">
						<c:choose>
							<c:when test="${actionBean.searchtype eq 'Expert'}">
							<a href="/search/doc/abstract.url?pageType=expertSearch&searchtype=${actionBean.searchtype}&SEARCHID=${actionBean.searchid}&DOCINDEX=${actionBean.docindex}&database=${actionBean.database}&format=${actionBean.searchtype eq 'Expert'?'expertSearchAbstractFormat':'quickSearchAbstractFormat'}" title="Abstract"><b>${resultSummary.title}</b></a>
							</c:when>
							<c:otherwise>
								<a href="/search/doc/abstract.url?pageType=quicktSearch&searchtype=${actionBean.searchtype}&SEARCHID=${actionBean.searchid}&DOCINDEX=${actionBean.docindex}&database=${actionBean.database}&format=${actionBean.searchtype eq 'Expert'?'expertSearchAbstractFormat':'quickSearchAbstractFormat'}" title="Abstract"><b>${resultSummary.title}</b></a>
							</c:otherwise>
						</c:choose>
						</p></c:if>
						<p class="authorwrap" style="line-height:16px;">
							<c:if test="${not empty resultSummary.authors and resultSummary.doc.dbmask == 2048}"><b>Inventor(s):&nbsp;</b></c:if>
							<span class="author">
							<c:forEach items="${resultSummary.authors}" var="author" varStatus="austatus">
								<c:choose>
									<c:when test="${not empty author.searchlink and not (author.nameupper eq 'ANON')}">
										<a href="${author.searchlink}" title="Search Author">${author.name}</a><c:if test="${austatus.count == 1}"><c:if test="${not empty resultSummary.authors[0].affils}"><span class="affiliation">&nbsp;(${resultSummary.authors[0].affils[0].name})</span></c:if><c:if test="${empty resultSummary.authors[0].affils}"><c:if test="${not empty resultSummary.affils}"><c:forEach items="${resultSummary.affils}" var="affil" varStatus="afstatus"><c:if test="${afstatus.count == 1}"><span class="affiliation">&nbsp;(${affil.name})</span></c:if></c:forEach></c:if></c:if><c:if test="${austatus.count < fn:length(resultSummary.authors)}">;&nbsp;</c:if></c:if>
									 	<c:choose><c:when test="${(austatus.count > 1) and (austatus.count < fn:length(resultSummary.authors))}">;&nbsp;</c:when><c:when test="${austatus.count >= fn:length(resultSummary.authors) and not empty resultSummary.editors}">,&nbsp;</c:when></c:choose>
									</c:when>
									<c:otherwise>${author.name}<c:if test="${(austatus.count > 1) and (austatus.count < fn:length(resultSummary.authors))}">;&nbsp;</c:if></c:otherwise>
								</c:choose>
							</c:forEach>
							<c:choose><c:when test="${'131072' eq resultSummary.doc.dbmask and (not empty resultSummary.isbn13)}">&nbsp;ISBN-13: ${resultSummary.isbn13}</c:when><c:when test="${'131072' eq resultSummary.doc.dbmask and (not empty resultSummary.isbn)}">&nbsp;ISBN-10: ${resultSummary.isbn}</c:when></c:choose><c:if test="${'131072' eq resultSummary.doc.dbmask and (not empty resultSummary.bpn)}">, ${resultSummary.bpn}</c:if><c:if test="${'131072' eq resultSummary.doc.dbmask and (not empty resultSummary.byr)}">, ${resultSummary.byr}</c:if>
							</span>
							<c:if test="${not empty resultSummary.editors}">
								<b>Editor<c:if test="${fn:length(resultSummary.editors) > 1}">s</c:if>:&nbsp;</b><c:forEach items="${resultSummary.editors}" var="editor" varStatus="edstatus"><a href="${editor.searchlink}">${editor.name}</a><c:if test="${not empty editor.affils}"><span class="affiliation"> (${editor.affils[0].name})</span></c:if><c:if test="${edstatus.count == 1}"><c:if test="${empty resultSummary.editors[0].affils}"><c:if test="${not empty resultSummary.affils}"><c:forEach items="${resultSummary.affils}" var="affil" varStatus="afstatus"><c:if test="${afstatus.count == 1}"><span class="affiliation">&nbsp;(${affil.name})</span></c:if></c:forEach></c:if></c:if><c:if test="${edstatus.count < fn:length(resultSummary.editors)}">;&nbsp;</c:if></c:if>	<c:if test="${(edstatus.count > 1) and (edstatus.count < fn:length(resultSummary.editors))}">;&nbsp;</c:if></c:forEach>
							</c:if>
							<c:if test="${not empty resultSummary.assigneelinks}"><span> <b>Assignee: </b><c:forEach items="${resultSummary.assigneelinks}" var="link" varStatus="status"><c:if test="${status.count>1}">&nbsp;-&nbsp;</c:if>${link}</c:forEach></span></c:if>
							<c:if test="${not empty resultSummary.pm}"><span> <b>Publication Number: </b>${resultSummary.pm}</span></c:if>
							<c:choose>
									<c:when  test="${not empty resultSummary.ppd}"><span> <b>Publication date: </b>${resultSummary.ppd}</span></c:when>
									<c:otherwise>
										<c:if test="${not empty resultSummary.upd}"><span> <b>Publication date: </b>${resultSummary.upd}</span></c:if>
									</c:otherwise>
							</c:choose>
							<c:if test="${not empty resultSummary.kd}"><span> <b>Kind: </b>${resultSummary.kd}</span></c:if>
						</c:forEach>
					</td>
				</tr>
				<tr>
					<td colspan="2">
						<b>Database:</b> ${actionBean.dbname} <c:if test="${not empty resultSummary.collection}"><b>&nbsp;Collection:</b> ${resultSummary.collection}</c:if>
					</td>
				</tr>
			</table>


		</div>
		<div class="clear"></div>
	</div>