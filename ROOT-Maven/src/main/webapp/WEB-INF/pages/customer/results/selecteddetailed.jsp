<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://jawr.net/tags" prefix="jwr" %>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld"	prefix="stripes"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp">

	<stripes:layout-component name="csshead">
	<link href="/static/css/ev_abstract.css?v=${releaseversion}" media="all" type="text/css" rel="stylesheet"></link>
	</stripes:layout-component>

	<stripes:layout-component name="contents">
	
	<c:set var="result" value="${actionBean.results[0]}"/>

	<div id="abstractbox">

<%-- *********************************************************** --%>
<%-- Top navbar - view search history, back to results and --%> 
<%-- page navigation --%> 
<%-- *********************************************************** --%> 
		<div id="abstractnavbar">
			<span><a class="history" href="/controller/servlet/Controller?CID=viewCompleteSearchHistory&database=${actionBean.database}" title="View search history">View search history</a>|</span>
			<span><a class="backtoresults" href="/controller/servlet/Controller?${actionBean.resultsqs}" title="Go back to search results">Back to results</a>|</span>
			<span class="pagenav">
			<c:if test="${not empty actionBean.prevqs}"><a style="margin-right:0" href="/controller/servlet/Controller?${actionBean.prevqs}" title="Go to previous record">&lt; Previous</a></c:if>
				<b>${result.doc.hitindex} of ${actionBean.resultscount}</b>
			<c:if test="${not empty actionBean.nextqs}"><a href="/controller/servlet/Controller?${actionBean.nextqs}" title="Go to next record">Next &gt;</a></c:if>
			</span>
			<div class="clear"></div>
		</div>

		<div style="color: rgb(215, 215, 215); background-color: rgb(215, 215, 215); height: 5px; margin: 0 10px;"></div>
				
<%-- *********************************************************** --%>
<%-- Abstract tools - full text, blog, email/print/download, etc.--%> 
<%-- *********************************************************** --%> 
		<div id="abstracttoolbar">
	<c:if test="${result.fulltext}">
		<span><a class="fulltext" href="javascript:newwindow=window.open('/search/results/fulltext.url?docID=${result.doc.docid}','newwindow','width=500,height=500,toolbar=no,location=no,scrollbars,resizable'); void('');" title="View full text (opens in a new window)"><img id="ftimg" class="fulltext" src="/static/images/full_text.png" title="View full text (opens in a new window)"></a>|</span>
	</c:if>
			<span><a class="blog" id="blogink" title="Create a link to share this record" href="">Blog This</a>|</span>
			<span><a class="email" id="emaillink" title="Email this record" href="">Email</a>|</span>
			<span><a class="print" id="printlink" title="Print this record" href="">Print</a>|</span>
			<span><a class="download" id="downloadlink" title="Download this record" href="">Download</a>|</span>
			<span><a class="save" title="Save this record to a folder" href="/login/customer.url?CID=personalLoginForm&EISESSION=${actionBean.sessionid}&searchid=${actionBean.searchid}count=1&searchtype=${actionBean.searchtype}&displaylogin=true&database=${actionBean.searchtype}&nexturl=CID%3DviewSavedFolders%26database%3D${actionBean.database}%26count%3D1%26searchid%3D${actionBean.searchid}%26source%3Dselectedset&backurl=CID%3D${actionBean.reruncid}%26searchid%3D${actionBean.searchid}%26COUNT%3D1%26database%3D${actionBean.database}">Save to Folder</a></span>
			<div class="clear"></div>
		</div>
		<div style="color: #9b9b9b; background-color: #9b9b9b; height: 1px; margin: 0 10px 20px 10px;"></div>

		
<%-- *********************************************************** --%>
<%-- Abstract display --%> 
<%-- *********************************************************** --%> 
		<div id="abstractwrapper">
		
			<div id="tabs">
			<ul>
<c:choose><c:when test="${result.doc.dbmask != 131072}">
			<li><a href="/controller/servlet/Controller?${actionBean.absnavqs}" title="Abstract view"<c:if test="${actionBean.activetab eq 'Abstract'}"> class="active"</c:if>>Abstract</a></li><c:if test="${actionBean.activetab eq 'Abstract'}"></c:if>
			<li><a href="/controller/servlet/Controller?${actionBean.detnavqs}" title="Detailed view"<c:if test="${actionBean.activetab eq 'Detailed'}"> class="active"</c:if>>Detailed</a></li>
</c:when><c:otherwise>
</c:otherwise>
</c:choose>
<c:choose><c:when test="${result.doc.dbmask == 2}">
<c:if test="${result.abstractrecord.patrefcount > 0 && not empty actionBean.patrefnavqs}">
			<li><a href="/controller/servlet/Controller?${actionBean.patrefnavqs}" title="Patent references view"<c:if test="${actionBean.activetab eq 'Patent references'}"> class="active"</c:if>>Patent References</a></li>
</c:if>
</c:when><c:otherwise>
<c:if test="${result.abstractrecord.patrefcount > 0 && not empty actionBean.patrefnavqs}">
			<li><a href="/controller/servlet/Controller?${actionBean.patrefnavqs}" title="Patent references view"<c:if test="${actionBean.activetab eq 'Patent references'}"> class="active"</c:if>>Patent References</a></li>
</c:if>
<c:if test="${result.abstractrecord.nonpatrefcount > 0 && not empty actionBean.nonpatrefnavqs}">
			<li><a href="/controller/servlet/Controller?${actionBean.nonpatrefnavqs}" title="Patent references view"<c:if test="${actionBean.activetab eq 'Non-Patent references'}"> class="active"</c:if>>Non-Patent References</a></li>
</c:if>
<c:if test="${result.abstractrecord.citerefcount > 0}">
			<li><a href="/controller/servlet/Controller?TODO" title="Show patents that reference this patent"<c:if test="${actionBean.activetab eq 'Cited-by'}"> class="active"</c:if>>Cited-by</a></li>
</c:if>
</c:otherwise></c:choose>		
			</ul>
			
			<div class="clear"></div>
			
			</div>
			
			
			<div id="abstractarea">
			<p class="topline">Record ${result.doc.hitindex} from ${actionBean.displaydb} for: ${actionBean.displayquery}, 
<c:choose>
	<c:when test="${not empty actionBean.emailalertweek}">Week ${actionBean.emailalertweek}</c:when>
	<c:when test="${not empty actionBean.updatesNo}">Last ${actionBean.updatesNo} update(s)</c:when>
	<c:when test="${'Combined' eq actionBean.searchtype}"></c:when>
	<c:otherwise>${actionBean.startYear}-${actionBean.endYear}</c:otherwise>
</c:choose></p>

		<div style="color: #9b9b9b; background-color: #9b9b9b; height: 1px; margin: 0 10px 12px 0;"></div>

			<p style="margin:0">Check record to add to Selected Records</p>
			
			<p class="title">
				<span style="font-weight: normal;font-size: 14px">${result.doc.hitindex}. <input type="checkbox" name="cbresult" title="Select Record" id="cbresult_${result.doc.dbid}" handle="${result.doc.hitindex}" docid="${result.doc.docid}" dbid="${result.doc.dbid}"<c:if test="${result.selected}"> checked="checked"</c:if>/></span>
				<lable for="cbresult_${result.doc.dbid}">${result.title}</lable>
			</p>
			
			<p class="authors" style="margin-top: 3px">
				<span class="authors">
<c:forEach items="${result.authors}" var="author" varStatus="austatus">
	<c:choose>
	<c:when test="${not empty author.searchlink and not (author.nameupper eq 'ANON')}"><a href="${author.searchlink}">${author.name}</a><c:if test="${not empty author.affils}"><sup><c:forEach items="${author.affils}" varStatus="afstatus" var="affil"><c:if test="${afstatus.count > 1}">, </c:if>${affil.id}</c:forEach></sup></c:if><c:if test="${(austatus.count > 1) and (austatus.count < fn:length(result.authors))}">; </c:if></c:when>
	<c:otherwise>${author.name}<c:if test="${(austatus.count > 1) and (austatus.count < fn:length(result.authors))}">; </c:if></c:otherwise>
	</c:choose>
</c:forEach>
				</span> 
			</p>
			
			<p class="result">
				<c:if test="${result.source ne null}"><span><b>Source:</b> <i>${result.source}</i></span></c:if>
				<c:choose>
				<c:when test="${'131072' eq result.doc.dbmask and (not empty result.isbn13)}">
				<span class="isbn">&nbsp;ISBN-13: ${result.isbn13}</span>
				</c:when>
				<c:when test="${'131072' eq result.doc.dbmask and (not empty result.isbn)}">
				<span class="isbn">&nbsp;ISBN-10: ${result.isbn}</span>
				</c:when>
				</c:choose>
				<c:if test="${not empty result.pf}"><span> (${result.pf})</span></c:if>
				<c:if test="${not empty result.rsp}"><span> <b>Sponsor:</b> ${result.rsp}</span></c:if>
				<c:if test="${not empty result.rnlabel}"><span> <b>Report:</b> ${result.rnlabel}</span></c:if>
				<c:if test="${not empty result.rn}"><span>, ${result.rn}</span></c:if>
				<c:if test="${not empty result.vo}"><span>, ${result.vo}</span></c:if>
				<c:if test="${not empty result.pages}"><span>, ${result.pages}</span></c:if>
				<c:if test="${not empty result.arn}"><span>, art. no. ${result.arn}</span></c:if>
				<c:if test="${not empty result.page}"><span>, ${result.page} p</span></c:if>
				<c:if test="${not empty result.pagespp}"><span>, ${result.pagespp} pp</span></c:if>
				<c:if test="${not empty result.mt}"><span>, <i>${result.mt}</i></span></c:if>
				<c:if test="${not empty result.vt}"><span>, ${result.vt}</span></c:if>
				<c:if test="${not empty result.sd}"><span>, ${result.sd};&nbsp;</span></c:if>
				<c:if test="${not empty result.yr and empty result.sd}"><span>, ${result.yr};&nbsp;</span></c:if>
				<c:if test="${not empty result.pd}"><span> <b>Publication Date: </b>${result.pd}</span></c:if>
				<c:if test="${not empty result.pn}"><span>, ${result.pn}</span></c:if>
				<c:if test="${not empty result.nv}"><span>, ${result.nv}</span></c:if>
				<c:if test="${not empty result.pa}"><span>, ${result.pa}</span></c:if>
				<c:if test="${not empty result.pasm}"><span> <b>Assignee: </b>${result.pasm}</span></c:if>
				<c:if test="${not empty result.easm}"><span> <b>Patent assignee: </b>${result.easm}</span></c:if>
				<c:if test="${not empty result.pan}"><span> <b>Application number: </b>${result.pan}</span></c:if>
				<c:if test="${not empty result.pap}"><span> <b>Patent number: </b>${result.pap}</span></c:if>
				<c:if test="${not empty result.pinfo}"><span> <b>Patent information: </b>${result.pinfo}</span></c:if>
				<c:if test="${not empty result.pm}"><span> <b>Publication Number: </b>${result.pm}</span></c:if>
				<c:if test="${not empty result.upd}"><span> <b>Publication <c:choose><c:when test="${'2048' eq result.doc.dbmask}">year</c:when><c:otherwise>date</c:otherwise></c:choose>: </b>${result.upd}</span></c:if>
				<c:if test="${not empty result.kd}"><span> <b>Kind: </b>${result.kd}</span></c:if>
				<c:if test="${not empty result.pfd}"><span> <b>Filing date: </b>${result.pfd}</span></c:if>
				<c:if test="${not empty result.pidd}"><span> <b>Patent issue date: </b>${result.pidd}</span></c:if>
				<c:if test="${not empty result.ppd}"><span> <b>Publication date: </b>${result.ppd}</span></c:if>
				<c:if test="${not empty result.copa}"><span> <b>Country of application: </b>${result.copa}</span></c:if>
				<c:if test="${not empty result.la}"><span> <b>Language: </b>${result.la}</span></c:if>
				<c:if test="${not empty result.nf}"><span> <b>Figures: </b>${result.nf}</span></c:if>
				<c:if test="${not empty result.av}"><span> <b>Availability: </b>${result.av}</span></c:if>
				<%-- ******** ABSTRACT FIELDS ************ --%>
				<c:if test="${not empty result.abstractrecord.issn}"><span>&nbsp;<b>ISSN: </b>${result.abstractrecord.issn};</span></c:if>
				<c:if test="${not empty result.abstractrecord.eissn}"><span>&nbsp;<b>E-ISSN: </b>${result.abstractrecord.issn};</span></c:if>
				<c:if test="${not empty result.abstractrecord.doi}"><span>&nbsp;<b>DOI: </b>${result.abstractrecord.doi};</span></c:if>
				<c:if test="${not empty result.abstractrecord.publisher}"><span>&nbsp;<b>Publisher: </b>${result.abstractrecord.publisher};</span></c:if>
			</p>
			<div class="clear"></div>

			<c:if test ="${not empty result.affils}">
			<p class="affils sectionstart"><b>Author affiliation<c:if test="${fn:length(result.affils) > 1}">s</c:if>:</b></p>
			<c:forEach items="${result.affils}" var="affil" varStatus="afstatus">
			<p style="margin:2px 6px 2px 40px">
			<sup>${afstatus.count}</sup>&nbsp;${affil.name}
			</p>
			</c:forEach>
			</c:if>
			
			<p class="abstracttext sectionstart"><b>${result.abstractrecord.label}: </b></p>
			<p>${result.abstractrecord.text}<c:if test="${result.abstractrecord.refcount > 0}"><br/>(${result.abstractrecord.refcount} refs)</c:if></p>
			
			<c:set var="mainheading" value="${result.abstractrecord.labelvalues['Main Heading']}"/>
			<c:if test="${not empty mainheading}">
			<p class="mainheading"><b>Main Heading: </b><c:forEach var="value" items="${mainheading}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if>${value}</c:forEach></p>
			</c:if>
			
			<%-- ************************************************************************* --%>
			<%-- Controlled terms: Companies, Chemicals and terms                          --%>
			<%-- ************************************************************************* --%>
			<c:forEach var="entry" items="${result.abstractrecord.controlledterms}" varStatus="status">
			<c:set var="label" value="${entry.key.label}"/>
			<p class="controlledterms"><b>${label}: </b><c:forEach var="term" items="${entry.value}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if>${term}</c:forEach></p>
			</c:forEach>
						
			<%--
			<c:if test="${not empty result.abstractrecord.controlledterms}">
			<c:set var="companies" value="${result.abstractrecord.controlledterms['Companies']}"/>
			<c:if test="${not empty companies}">
			<p class="controlledterms"><b>Companies: </b><c:forEach var="term" items="${companies}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if>${term}</c:forEach></p>
			</c:if>
			
			<c:set var="chemicals" value="${result.abstractrecord.controlledterms['Chemicals']}"/>
			<c:if test="${not empty companies}">
			<p class="controlledterms"><b>Chemicals: </b><c:forEach var="term" items="${chemicals}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if>${term}</c:forEach></p>
			</c:if>
			
			<c:set var="controlledterms" value="${result.abstractrecord.controlledterms['Controlled terms']}"/>
			<c:if test="${not empty controlledterms}">
			<p class="controlledterms"><b>Controlled terms: </b><c:forEach var="term" items="${controlledterms}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if>${term}</c:forEach></p>
			</c:if>

			<c:set var="regionalterms" value="${result.abstractrecord.controlledterms['Regional terms']}"/>
			<c:if test="${not empty regionalterms}">
			<p class="regionalterms"><b>Regional terms: </b><c:forEach var="term" items="${regionalterms}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if>${term}</c:forEach></p>
			</c:if>
			</c:if>
			 --%>
			 
			<%-- ************************************************************************* --%>
			<%-- Uncontrolled terms                                                        --%>
			<%-- ************************************************************************* --%>
			<c:if test="${not empty result.abstractrecord.uncontrolledterms}">
			<c:forEach var="entry" items="${result.abstractrecord.uncontrolledterms}" varStatus="status">
			<c:set var="label" value="${entry.key.label}"/>
			<p class="controlledterms"><b>${label}: </b><c:forEach var="term" items="${entry.value}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if>${term}</c:forEach></p>
			</c:forEach>
			</c:if>

			<%-- ************************************************************************* --%>
			<%-- Classification codes                                                      --%>
			<%-- ************************************************************************* --%>
			<c:if test="${not empty result.abstractrecord.classificationcodes}">
			<c:forEach var="clcodelist" items="${result.abstractrecord.classificationcodes}">
			<p class="classificationcode"><b>${clcodelist.key}: </b><c:forEach var="clcode" items="${clcodelist.value}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if>${clcode.linkedid}&nbsp;${clcode.title}</c:forEach></p>
			</c:forEach>
			</c:if>

			<%-- ************************************************************************* --%>
			<%-- Other info                                                                --%>
			<%-- ************************************************************************* --%>
			<c:set var="coordinates" value="${result.abstractrecord.labelvalues['Coordinates']}"/>
			<c:if test="${not empty coordinates}">
			<p class="coordinates"><b>Coordinates: </b><c:forEach var="value" items="${coordinates}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if>${value}</c:forEach></p>
			</c:if>
			
			<p class="database"><b>Database: </b>${result.doc.dbname}</p>

			<div id="deliverylinks">
				<div id="deliveryheader">Full-text and Local Holdings Links</div>
				<div id="deliverybody">
				<p><a target="new" href="http://www.google.com/search?hl=en&ie=UTF8&oe=UTF8&q=${actionBean.encodedquery}" class="SpLink">Search Article Title at Google</a><a class="MedBlackText"><c:if test="${not empty actionBean.encodedfirstauthor}">&nbsp; - &nbsp;</a><a target="new" href="http://www.google.com/search?hl=en&ie=UTF8&oe=UTF8&q=${actionBean.encodedfirstauthor}" class="SpLink">Search First Author at Google</a></c:if></p>
				<c:if test="${result.fulltext}">
				<p><a href="javascript:newwindow=window.open('/search/results/fulltext.url?docID=${result.doc.docid}','newwindow','width=500,height=500,toolbar=no,location=no,scrollbars,resizable'); void('');" title="Full-text"><img id="ftimg" class="fulltext" src="/static/images/full_text.png" alt="Full-text"></a></p>
				</c:if>
    			<c:if test="${actionBean.lindahall}">
    			<p><a title="Linda Hall Library document delivery service" href="javascript:lindaHall('${actionBean.sessionid}','${result.doc.docid}','${result.doc.dbid}')">Linda Hall Library document delivery service</a></p>
    			</c:if>
				</div>
			</div>

			<div class="clear"></div>

			</div>

			<div class="clear"></div>
			
		</div>

		<div class="clear"></div>

	</div>

	<br/>
	
	</stripes:layout-component>

	<stripes:layout-component name="jsbottom_custom">
	<jwr:script src="/bundles/selectedabs.js"></jwr:script>
	<c:if test="${actionBean.lindahall}">
	<script src="/static/js/lindaHall.js?v=${releaseversion}" language="Javascript"></script>
	</c:if>
	</stripes:layout-component>

	
</stripes:layout-render>
