<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/tlds/datatags.tld" prefix="data" %>
<c:forEach items="${actionBean.results}" var="result" varStatus="status">
<c:set var="resultnum" value="${(actionBean.pagenav.currentpage - 1) * actionBean.pagenav.resultsperpage + status.count}" />
<c:set var="displaywhite" value="${status.count}" />

<h2 id="detailedrecord" style="display:none">Detailed record</h2>

			<div class="result<c:if test="${status.count % 2 eq 0}"> odd</c:if>">

			<c:choose>
			<c:when test="${param.displaytype eq 'viewfolders'}">
				<div style="float: left; line-height:1.9em; padding: 6px 1px 10px 5px ">
					<span>${result.documentbaskethitindex}.&nbsp;</span>
				</div>
				<div style="float: left; line-height:1.6em; padding: 7px 0 10px 0; margin-right: 5px " >
					<span><a href="/selected/deletefolder.url?CID=deleteFromSavedRecords&format=detailed&database=${actionBean.database}&docid=${result.doc.docid}&folderid=${actionBean.folderid}" title="Remove record">
					<img border="0" style="padding: 2.5px 0 2.5px 0; margin-top:0.75px" src="/static/images/Remove.png" alt="Remove Record"/>
					</a></span></div>
			 </c:when>
			 <c:otherwise>
					<div style="float: left; line-height:1.9em; padding: 6px 1px 10px 5px ">
					<span>${resultnum}.&nbsp;</span>
					</div>
					<div style="float: left; line-height:1.6em; padding: 7px 0 10px 0; margin-right: 5px " >
					<span>
					<a href="/selected/delete.url?CID=deleteFromSelectedSet&reruncid=${param.CID}&docid=${result.doc.docid}&basketsize=${actionBean.resultsperpage}&handle=${result.doc.hitindex}&database=${actionBean.database}&searchtype=${actionBean.searchtype}&searchid=${actionBean.searchid}&searchresults=${actionBean.searchresultsencoded}&newsearch=${actionBean.newsearchencoded}&backindex=${actionBean.backindex}" title="Remove record">
					<img border="0" style="padding: 2.5px 0 2.5px 0; margin-top:0.75px" src="/static/images/Remove.png" alt="Remove Record"/>
					</a></span></div>
			</c:otherwise>
			</c:choose>

			<table border="0" id="detailed" style="padding-top:6px;">

<c:forEach items="${result.detailedOrder}" var="segment" varStatus="segmentstatus">
<!--  KEY: ${segment.key}  -->
<!--  LABEL: ${segment.label} -->
<c:choose>

<c:when test="${'AN' eq segment.key and not empty result.accnum}"> <!--Accession Number  -->
			<tr>
					<!--<td style="white-space: nowrap"><span style="font-weight: normal;font-size: 14px">${result.doc.hitindex}. <input type="checkbox" name="cbresult" handle="${result.doc.hitindex}" docid="${result.doc.docid}" dbid="${result.doc.dbid}"<c:if test="${result.selected}"> checked="checked"</c:if>/></span></td>-->
					<td class="label"><h3>${result.labels['AN']}: </h3></td>
					<td colspan="2">
						<p>${result.accnum}</p><c:if test="${'Article in Press' eq result.doctype}">
						<p><img src="/static/images/btn_aip.gif" border="0"  title="Articles not published yet, but available online" alt="Articles not published yet, but available online"><span style="vertical-align:text-top"> Article in Press</span>&nbsp;<a href="${actionBean.helpUrl}#Art_in_Press.htm" alt="Information about Article in Press" title="Information about Article in Press" class="helpurl"><img src="/static/images/i.png" border="0" alt="Information about Article in Press"/></a></p></c:if>
		                <c:if test="${'In Process' eq result.doctype}">
                        <p><img src="/static/images/btn_aip.gif" border="0"  title="Records still in the process of being indexed, but available online" alt="Records still in the process of being indexed, but available online"><span style="vertical-align:text-top"> In Process</span>&nbsp;<a href="${actionBean.helpUrl}#GeoRefInProc.htm" alt="Information about In Process" title="Information about In Process" class="helpurl"><img src="/static/images/i.png" border="0" alt=""/></a></p></c:if>
					</td>
				</tr>
</c:when>

<c:when test="${'PM' eq segment.key and not empty result.pm}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Publication number: </h3></td>
					<td colspan="2">${result.pm}</td>
				</tr>
</c:when>
<c:when test="${'PM1' eq segment.key and not empty result.pm1}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Publication number: </h3></td>
					<td colspan="2">${result.pm1}</td>
				</tr>
</c:when>
<c:when test="${('PAPX' eq segment.key or 'PAN' eq segment.key) and empty result.pap}"> <!-- There is no patent number -->
	<c:if test="${'PAPX' eq segment.key and not empty result.papx}">
					<tr>
						<!--<td>&nbsp;</td>-->
						<td class="label"><h3>Application date: </h3></td>
						<td colspan="2">${result.papx}</td>
					</tr>
	</c:if>
	<c:if test="${'PAN' eq segment.key and not empty result.pan}">
						<tr>
						<!--<td>&nbsp;</td>-->
						<td class="label"><h3>Application number: </h3></td>
						<td colspan="2">${result.pan}</td>
					</tr>
	</c:if>
</c:when>
<c:when test="${'PAPD' eq segment.key and not empty result.papd}">

					<tr>
						<!--<td>&nbsp;</td>-->
						<td class="label"><h3>Application date: </h3></td>
						<td colspan="2">${result.papd}</td>
					</tr>
</c:when>
<c:when test="${'PAPCO' eq segment.key and not empty result.papco}">

					<tr>
						<!--<td>&nbsp;</td>-->
						<td class="label"><h3>Application country: </h3></td>
						<td colspan="2">${result.papco}</td>
					</tr>
</c:when>
<c:when test="${('PANS' eq segment.key) and not empty result.pans}">
					<tr>
						<!--<td>&nbsp;</td>-->
						<td class="label"><h3>Application number: </h3></td>
						<td colspan="2">${result.pans}</td>
					</tr>
</c:when>
<c:when test="${'TI' eq segment.key and not empty result.title}">   <!--  Title -->
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${result.labels['TI']}: </h3></td>
					<td colspan="2" id="titletext"><b><data:highlighttag value="${result.title}" on="${actionBean.ckhighlighting}"/></h3></td>
				</tr>
</c:when>

<c:when test="${'TA' eq segment.key and not empty result.ta}"> <!-- Title annotation -->
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${result.labels['TA']}: </h3></td>
					<td colspan="2" id="titletext">${result.ta}</td>
				</tr>
</c:when>

<c:when test="${'TT' eq segment.key and not empty result.tt}"> <!-- Title Translation -->
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${result.labels['TT']}: </h3></td>
					<td colspan="2" id="titletext"><b><data:highlighttag value="${result.tt}" on="${actionBean.ckhighlighting}"/></h3></td>
				</tr>
</c:when>

<c:when test="${'AUS' eq segment.key  and not empty result.authors}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${result.labels['AUS']}: </h3></td>
					<td colspan="2"><span class="authors">
<c:forEach items="${result.authors}" var="author" varStatus="austatus">
	<c:choose>
	<c:when test="${not empty author.searchlink and not (author.nameupper eq 'ANON')}"><a href="${author.searchlink}" title="Search Author">${author.name}</a>${author.co}<c:if test="${not empty author.affils}"><c:forEach items="${author.affils}" varStatus="afstatus" var="affil"><c:if test="${afstatus.count > 1}"><sup>, </sup></c:if><c:if test="${affil.id ne '0'}"><sup>${affil.id}</sup></c:if></c:forEach></c:if><c:if test="${not empty author.email}">&nbsp;<a href="mailto:${author.email}" title="Author email" class="emaillink"><img src="/static/images/emailfolder.gif" alt="Author Email"/></a></c:if><c:if test="${(austatus.count < fn:length(result.authors))}">; </c:if></c:when>
	<c:otherwise>${author.name}<c:if test="${(austatus.count > 0) and (austatus.count < fn:length(result.authors))}">; </c:if></c:otherwise>
	</c:choose>
</c:forEach>
					</span></td>
				</tr>
</c:when>
<c:when test="${'IVS' eq segment.key and not empty result.inventors}">
<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${result.labels['IVS']}: </h3></td>
					<td colspan="2"><span class="editors">
<c:forEach items="${result.inventors}" var="inventor" varStatus="ivstatus">
	<c:choose>
	<c:when test="${not empty inventor.searchlink and not (inventor.nameupper eq 'ANON')}"><a href="${inventor.searchlink}" title="Search Author">${inventor.name}</a>${inventor.co}<c:if test="${not empty inventor.affils}"><c:forEach items="${inventor.affils}" varStatus="afstatus" var="affil"><c:if test="${afstatus.count > 1}"><sup>, </sup></c:if><c:if test="${affil.id ne '0'}"><sup>${affil.id}</sup></c:if></c:forEach></c:if><c:if test="${not empty inventor.email}">&nbsp;<a href="mailto:${inventor.email}" title="Author email" class="emaillink"><img src="/static/images/emailfolder.gif" alt="Author Email"/></a></c:if><c:if test="${(ivstatus.count < fn:length(result.inventors))}">; </c:if></c:when>
	<c:otherwise>${inventor.name}<c:if test="${(ivstatus.count > 0) and (ivstatus.count < fn:length(result.inventors))}">; </c:if></c:otherwise>
	</c:choose>
</c:forEach>
</span></td>
				</tr>
</c:when>
<c:when test ="${'AFS' eq segment.key and not empty result.affils}">
<c:forEach items="${result.affils}" var="affil" varStatus="afstatus">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><c:if test="${afstatus.count eq 1}">${result.labels['AFS']}: </c:if></td>
					<td class="affil" colspan="2"><c:if test="${affil.id ne '0'}"><sup>${affil.id}</sup></c:if>
						<span style="margin:0 5px 0 0"><data:highlighttag value="${affil.name}" on="${actionBean.ckhighlighting}"/></span>
					</td>
				</tr>
</c:forEach>
</c:when>
<c:when test="${'PF' eq segment.key and not empty result.pf}"> <!-- Author affiliation -->
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${result.labels['PF']}: </h3></td>
					<td colspan="2"><data:highlighttag value="${result.pf}" on="${actionBean.ckhighlighting}"/></td>
				</tr>
</c:when>
<c:when test="${'CAC' eq segment.key and not empty result.cac}"><!-- Author affiliation codes -->
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${result.labels['CAC']}: </h3></td>
					<td colspan="2"><data:highlighttag value="${result.cac}" on="${actionBean.ckhighlighting}"/></td>
				</tr>
</c:when>

<c:when test="${'CAUS' eq segment.key and not empty result.cauthors}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Corresponding author: </h3></td>
					<td colspan="2">
<c:forEach items="${result.cauthors}" var="cauthor" varStatus="caustatus">${cauthor.name}<c:if test="${not empty cauthor.email}"> (<a href="mailto:${cauthor.email}" title="Corresponding author">${cauthor.email}</a>)</c:if></c:forEach>
					</td>
				</tr>
</c:when>
<c:when test="${'CAF' eq segment.key and not empty result.caf}"> <!-- Corr. author affiliation -->
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${result.labels['CAF']}: </h3></td>
					<td colspan="2">${result.caf}</td>
				</tr>
</c:when>
<c:when test="${'EDS' eq segment.key and not empty result.editors}">
<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${result.labels['EDS']}: </h3></td>
					<td colspan="2"><span class="editors">
<c:forEach items="${result.editors}" var="editor" varStatus="edstatus">
<c:choose>
	<c:when test="${not empty editor.searchlink and not (editor.nameupper eq 'ANON')}"><a href="${editor.searchlink}" title="Search Editors">${editor.name}</a>${editor.co}<c:if test="${not empty editor.affils}"><c:forEach items="${editor.affils}" varStatus="afstatus" var="affil"><c:if test="${afstatus.count > 1}"><sup>, </sup></c:if><c:if test="${affil.id ne '0'}"><sup>${affil.id}</sup></c:if></c:forEach></c:if><c:if test="${not empty editor.email}">&nbsp;<a href="mailto:${editor.email}" title="Editor email" class="emaillink"><img src="/static/images/emailfolder.gif" alt="Editor Email"/></a></c:if><c:if test="${(edstatus.count < fn:length(result.editors))}">; </c:if></c:when>
	<c:otherwise>${editor.name}<c:if test="${(edstatus.count > 0) and (ivstatus.count < fn:length(result.editors))}">; </c:if></c:otherwise>
</c:choose>
</c:forEach>
</span></td>
				</tr>
</c:when>

<c:when test ="${'OAF' eq segment.key and not empty result.otherAffils}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label">Other affiliation: </td>
					<td class="affil" colspan="2">
						<span style="margin:0 5px 0 0"><c:forEach items="${result.otherAffils}" var="otherAffils" varStatus="aofstatus"><c:if test="${aofstatus.count > 1}">;</c:if>${otherAffils.name}</c:forEach></span>
					</td>
				</tr>
</c:when>

<c:when test="${'PASM' eq segment.key and not empty result.assigneelinks}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${result.labels['PASM']}: </h3></td>
					<td colspan="2"><c:forEach items="${result.assigneelinks}" var="link" varStatus="status"><c:if test="${status.count>1}">;</c:if>${link}</c:forEach></td>
				</tr>
</c:when>


<c:when test="${'PEXM' eq segment.key and not empty result.primaryexaminer}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Primary examiner: </h3></td>
					<td colspan="2"><c:forEach items="${result.primaryexaminer}" var="link" varStatus="status"><c:if test="${status.count>1}">&nbsp;-&nbsp;</c:if>${link}</c:forEach></td>
				</tr>
</c:when>
<c:when test="${'ATT' eq segment.key and not empty result.att}"> <!-- Attorney, Agent or Firm  -->
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${result.labels['ATT']}: </h3></td>
					<td colspan="2">${result.att}</td>
				</tr>
</c:when>
<c:when test="${'AE' eq segment.key and not empty result.ae}"> <!-- Assistant examiner -->
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${result.labels['AE']}: </h3></td>
					<td colspan="2">${result.ae}</td>
				</tr>
</c:when>
<c:when test="${'EASM' eq segment.key and not empty result.patassigneelinks}">
					<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Assignee: </h3></td>
					<td colspan="2"><c:forEach items="${result.patassigneelinks}" var="link" varStatus="status"><c:if test="${status.count>1}">&nbsp;-&nbsp;</c:if>${link}</c:forEach></td>
				</tr>
</c:when>
<c:when test="${'PAP' eq segment.key and not empty result.pap}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Patent number: </h3></td>
					<td colspan="2">${result.pap}</td>
				</tr>
</c:when>
<c:when test="${'PIDD' eq segment.key and not empty result.pidd}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Patent issue date: </h3></td>
					<td colspan="2">${result.pap}</td>
				</tr>
</c:when>
<c:when test="${'AUTHCD' eq segment.key and not empty result.authcd}"> <!-- Patent authority/ Patent Country-->
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${result.labels['AUTHCD']}: </h3></td>
					<td colspan="2">${result.authcd}</td>
				</tr>
</c:when>

<c:when test="${'KC' eq segment.key and  not empty result.kc}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Kind: </h3></td>
					<td colspan="2"><c:if test="${not empty result.kc}">${result.kc} - </c:if>${result.kd}</td>
				</tr>
</c:when>
<c:when test="${'SO' eq segment.key and not empty result.source}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Source: </h3></td>
					<td colspan="2"><c:choose><c:when test="${empty result.source}">No source available</c:when><c:otherwise><data:highlighttag value="${result.source}" on="${actionBean.ckhighlighting}"/></c:otherwise></c:choose></td>
				</tr>
</c:when>
<c:when test="${'RIL' eq segment.key and not empty result.ril}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Source title: </h3></td>
					<td colspan="2"><c:choose><c:when test="${empty result.ril}">No source available</c:when><c:otherwise><data:highlighttag value="${result.ril}" on="${actionBean.ckhighlighting}"/></c:otherwise></c:choose></td>
				</tr>
</c:when>
<c:when test="${'SE' eq segment.key and not empty result.sourceabbrev}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Abbreviated source title: </h3></td>
					<td colspan="2">${result.sourceabbrev}</td>
				</tr>
</c:when>

<c:when test="${'MT' eq segment.key and not empty result.mt}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Monograph title: </h3></td>
					<td colspan="2"><data:highlighttag value="${result.mt}" on="${actionBean.ckhighlighting}"/></td>
				</tr>
</c:when>
<c:when test="${'VOM' eq segment.key and not empty result.vo}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Volume: </h3></td>
					<td colspan="2">${result.vo}</td>
				</tr>
</c:when>
<c:when test="${'IS' eq segment.key and not empty result.is}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Issue: </h3></td>
					<td colspan="2">${result.is}</td>
				</tr>
</c:when>

<c:when test="${'SD' eq segment.key and not empty result.sd}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Issue date: </h3></td>
					<td colspan="2">${result.sd}</td>
				</tr>
</c:when>
<c:when test="${'YR' eq segment.key and not empty result.yr}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Publication year: </h3></td>
					<td colspan="2">${result.yr}</td>
				</tr>
</c:when>
<c:when test="${'UPD' eq segment.key and not empty result.upd}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Publication date: </h3></td>
					<td colspan="2">${result.upd}</td>
				</tr>
</c:when>
<c:when test="${('YR' eq segment.key or 'PYR' eq segment.key) and not empty result.yr}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Publication Year: </h3></td>
					<td colspan="2">${result.yr}</td>
				</tr>
</c:when>
<c:when test="${('PD_YR' eq segment.key or 'PPD_YR' eq segment.key) and not empty result.pd}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Publication date: </h3></td>
					<td colspan="2">${result.pd}</td>
				</tr>
</c:when>

<c:when test="${('PAPX' eq segment.key or 'PAN' eq segment.key) and not empty result.pap}"> <!-- There is a patent number -->
	<c:if test="${'PAPX' eq segment.key and not empty result.papx}">
					<tr>
						<!--<td>&nbsp;</td>-->
						<td class="label"><h3>Application date: </h3></td>
						<td colspan="2">${result.papx}</td>
					</tr>
	</c:if>
	<c:if test="${'PAN' eq segment.key and not empty result.pan}">
						<tr>
						<!--<td>&nbsp;</td>-->
						<td class="label"><h3>Application number: </h3></td>
						<td colspan="2">${result.pan}</td>
					</tr>
	</c:if>
</c:when>
<c:when test="${'PANUS' eq segment.key and not empty result.panus}"> <!-- Unstandardized application number -->
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${result.labels['PANUS']}: </h3></td>
					<td colspan="2">${result.panus}</td>
				</tr>
</c:when>
<c:when test="${'PFD' eq segment.key and not empty result.pfd}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Filing date: </h3></td>
					<td colspan="2">${result.pfd}</td>
				</tr>
</c:when>
<c:when test="${'PR' eq segment.key and not empty result.pr}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Part number: </h3></td>
					<td colspan="2">${result.pr}</td>
				</tr>
</c:when>
<c:when test="${'PAPIM' eq segment.key and not empty result.papim}">  <!--Application Information  -->
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Application Information: </h3></td>
					<td colspan="2">${result.papim}</td>
				</tr>
</c:when>
<c:when test="${'PIM' eq segment.key and not empty result.pim}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Priority information: </h3></td>
					<td colspan="2"><c:forEach items="${result.pim}" var="link" varStatus="status"><c:if test="${status.count>1}">&nbsp;-&nbsp;</c:if>${link}</c:forEach></td>
				</tr>
</c:when>
<c:when test="${'VT' eq segment.key and not empty result.vt}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Volume title: </h3></td>
					<td colspan="2">${result.vt}</td>
				</tr>
</c:when>
<c:when test="${'PINFO' eq segment.key and not empty result.pinfo}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Patent information: </h3></td>
					<td colspan="2">${result.pinfo}</td>
				</tr>
</c:when>
<c:when test="${'PA' eq segment.key and not empty result.pa}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Paper number: </h3></td>
					<td colspan="2">${result.pa}</td>
				</tr>
</c:when>
<c:when test="${'DERW' eq segment.key and not empty result.derwent}">
<!-- Int. Patent Classification -->
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>DERWENT accession no.: </h3></td>
					<td colspan="2">${result.derwent}</td>
				</tr>
</c:when>
<c:when test="${'PP' eq segment.key and not empty result.pages}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Pages: </h3></td>
					<td colspan="2">${result.pages}</td>
				</tr>
</c:when>
<c:when test="${'ARTICLE_NUMBER' eq segment.key and not empty result.articlenumber}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${result.labels['ARTICLE_NUMBER']}: </h3></td>
					<td colspan="2">${result.articlenumber}</td>
				</tr>
</c:when>
<c:when test="${'LA' eq segment.key and not empty result.la}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Language: </h3></td>
					<td colspan="2">${result.la}</td>
				</tr>
</c:when>
<c:when test="${'SN' eq segment.key and not empty result.abstractrecord.issnlink}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>ISSN: </h3></td>
					<td colspan="2">${result.abstractrecord.issnlink}</td>
				</tr>
</c:when>
<c:when test="${'E_ISSN' eq segment.key and not empty result.abstractrecord.eissnlink}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>E-ISSN: </h3></td>
					<td colspan="2">${result.abstractrecord.eissnlink}</td>
				</tr>
</c:when>
<c:when test="${'BN' eq segment.key and not empty result.isbn}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>ISBN-10: </h3></td>
					<td colspan="2">${result.isbn}</td>
				</tr>
</c:when>
<c:when test="${('BN13' eq segment.key) and not empty result.isbn13}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>ISBN-13: </h3></td>
					<td colspan="2">${result.isbn13}</td>
				</tr>
</c:when>

<c:when test="${'CN' eq segment.key and not empty result.abstractrecord.codenlink}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>CODEN: </h3></td>
					<td colspan="2">${result.abstractrecord.codenlink}</td>
				</tr>
</c:when>
<c:when test="${'DT' eq segment.key and not empty result.dt}">
				<tr>
					<!-- <td>&nbsp;</td> -->
					<td class="label"><h3>Document type: </h3></td>
					<td colspan="2"><c:forEach items="${result.dt}" var="link" varStatus="status"><c:if test="${status.count>1}">,&nbsp;</c:if>${link}</c:forEach></td>
				</tr>
</c:when>
<c:when test="${'GURL' eq segment.key and not empty result.gurl}"><!-- URL -->
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>URL: </h3></td>
					<td colspan="2">${result.gurl}</td>
				</tr>
</c:when>
<c:when test="${'RPGM' eq segment.key and not empty result.rpgm}"><!-- Research program -->
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${result.labels['RPGM']}: </h3></td>
					<td colspan="2">${result.rpgm}</td>
				</tr>
</c:when>

<c:when test="${'AV' eq segment.key and not empty result.av}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Availability: </h3></td>
					<td colspan="2">${result.av}</td>
				</tr>
</c:when>
<c:when test="${'SU' eq segment.key and not empty result.su}"><!-- Notes -->
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label">${result.labels['SU']}: </td>
					<td colspan="2">${result.su}</td>
				</tr>
</c:when>
<c:when test="${'CF' eq segment.key and not empty result.cf}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Conference name: </h3></td>
					<td colspan="2">${result.cf}</td>
				</tr>
</c:when>
<c:when test="${'MD' eq segment.key and not empty result.md}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Conference date: </h3></td>
					<td colspan="2">${result.md}</td>
				</tr>
</c:when>
<c:when test="${'ML' eq segment.key and not empty result.ml}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Conference location: </h3></td>
					<td colspan="2"><data:highlighttag value="${result.ml}" on="${actionBean.ckhighlighting}"/></td>
				</tr>
</c:when>
<c:when test="${'CC' eq segment.key and not empty result.cc}"> <!-- Conference code -->
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${result.labels['CC']}: </h3></td>
					<td colspan="2">${result.cc}</td>
				</tr>
</c:when>
<c:when test="${'SP' eq segment.key and not empty result.sp}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Sponsor: </h3></td>
					<td colspan="2"><data:highlighttag value="${result.sp}" on="${actionBean.ckhighlighting}"/></td>
				</tr>
</c:when>
<c:when test="${'BPN' eq segment.key and not empty result.bpn}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Publisher: </h3></td>
					<td colspan="2">${result.bpn}</td>
				</tr>
</c:when>
<c:when test="${'PN' eq segment.key and not empty result.pn}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Publisher: </h3></td>
					<td colspan="2"><data:highlighttag value="${result.pn}" on="${actionBean.ckhighlighting}"/></td>
				</tr>
</c:when>
<c:when test="${'I_PN' eq segment.key and not empty result.ipn}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Publisher: </h3></td>
					<td colspan="2"><data:highlighttag value="${result.ipn}" on="${actionBean.ckhighlighting}"/></td>
				</tr>
</c:when>
<c:when test="${'PL' eq segment.key and not empty result.pl}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Country of publication: </h3></td>
					<td colspan="2">${result.pl}</td>
				</tr>
</c:when>
<c:when test="${'CO' eq segment.key and not empty result.co}"> <!-- Country of origin -->
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${result.labels['CO']}: </h3></td>
					<td colspan="2">${result.co}</td>
				</tr>
</c:when>
<c:when test="${'COS' eq segment.key and not empty result.cos}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Country of origin: </h3></td>
					<td colspan="2">${result.cos}</td>
				</tr>
</c:when>
<c:when test="${'VOLT' eq segment.key and not empty result.volt}"> <!-- Translation volume -->
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${result.labels['VOLT']}: </h3></td>
					<td colspan="2">${result.volt}</td>
				</tr>
</c:when>
<c:when test="${'ISST' eq segment.key and not empty result.isst}"> <!-- Translation issue -->
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${result.labels['ISST']}: </h3></td>
					<td colspan="2">${result.isst}</td>
				</tr>
</c:when>
<c:when test="${'IPNT' eq segment.key and not empty result.ipnt}"> <!-- Translation Pages -->
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${result.labels['IPNT']}: </h3></td>
					<td colspan="2">${result.ipnt}</td>
				</tr>
</c:when>
<c:when test="${'TDATE' eq segment.key and not empty result.tdate}"> <!-- Translation pub date -->
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${result.labels['TDATE']}: </h3></td>
					<td colspan="2">${result.tdate}</td>
				</tr>
</c:when>
<c:when test="${'FTTJ' eq segment.key and not empty result.fttj}"> <!-- Translation serial title -->
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${result.labels['FTTJ']}: </h3></td>
					<td colspan="2">${result.fttj}</td>
				</tr>
</c:when>
<c:when test="${'TTJ' eq segment.key and not empty result.ttj}">   <!-- Translation abbreviated serial title -->
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${result.labels['TTJ']}: </h3></td>
					<td colspan="2">${result.ttj}</td>
				</tr>
</c:when>
<c:when test="${'SNT' eq segment.key and not empty result.snt}"> <!-- Translation ISSN -->
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${result.labels['SNT']}: </h3></td>
					<td colspan="2">${result.snt}</td>
				</tr>
</c:when>
<c:when test="${'CNT' eq segment.key and not empty result.cnt}"> <!-- Translation CODEN -->
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${result.labels['CNT']}: </h3></td>
					<td colspan="2">${result.cnt}</td>
				</tr>
</c:when>
<c:when test="${'CPUBT' eq segment.key and not empty result.cpubt}"> <!-- Translation country of publication -->
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${result.labels['CPUBT']}: </h3></td>
					<td colspan="2">${result.cpubt}</td>
				</tr>
</c:when>
<c:when test="${'CPUB' eq segment.key and not empty result.cpub}"> <!--  country of publication -->
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${result.labels['CPUB']}: </h3></td>
					<td colspan="2">${result.cpub}</td>
				</tr>
</c:when>
<c:when test="${'PLA' eq segment.key and not empty result.pla}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Place of publication: </h3></td>
					<td colspan="2">${result.pla}</td>
				</tr>
</c:when>
<c:when test="${'MI' eq segment.key and not empty result.abstractrecord.milink}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Material Identity Number: </h3></td>
					<td colspan="2">${result.abstractrecord.milink}</td>
				</tr>
</c:when>
<c:when test="${'AB' eq segment.key  and not empty result.abstractrecord.text}">
				<!-- ******************************************************** -->
				<!-- ABSTRACT TEXT -->
				<!-- ******************************************************** -->
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${result.abstractrecord.label}: </h3></td>
					<td colspan="2"><data:highlighttag value="${result.abstractrecord.text}" on="${actionBean.ckhighlighting}" v2="${result.abstractrecord.v2}"/></td>
				</tr>
</c:when>
<c:when test="${'AB2' eq segment.key  and not empty result.abstractrecord.text}">
				<!-- ******************************************************** -->
				<!-- ABSTRACT TEXT -->
				<!-- ******************************************************** -->
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${result.abstractrecord.label}: </h3></td>
					<td colspan="2"><data:highlighttag value="${result.abstractrecord.text}" on="${actionBean.ckhighlighting}" v2="${result.abstractrecord.v2}"/></td>
				</tr>
</c:when>
<c:when test="${'BAB' eq segment.key and not empty result.abstractrecord.bookdescription}">
				<!-- ******************************************************** -->
				<!-- BOOK DESCRIPTION! -->
				<!-- ******************************************************** -->
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${result.abstractrecord.label}: </h3></td>
					<td colspan="2"><data:highlighttag value="${result.abstractrecord.bookdescription}" on="${actionBean.ckhighlighting}" v2="${result.abstractrecord.v2}"/></td>
				</tr>
</c:when>
<c:when test="${'DSM' eq segment.key and not empty result.designatedstates}">
			<%-- ************************************************************************* --%>
			<%-- Designated states                                                         --%>
			<%-- ************************************************************************* --%>
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Designated states: </h3></td>
					<td colspan="2"><c:forEach var="term" items="${result.designatedstates}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if><data:highlighttag value="${term}" on="${actionBean.ckhighlighting}"/></c:forEach></td>
				</tr>
</c:when>

<c:when test="${'FSM' eq segment.key and not empty result.fieldofsearch}">
			<%-- ************************************************************************* --%>
			<%-- Field of search                                                         --%>
			<%-- ************************************************************************* --%>
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Field of Search: </h3></td>
					<td colspan="2"><c:forEach var="term" items="${result.fieldofsearch}" varStatus="fosstatus"><c:if test="${fosstatus.count > 1}">&nbsp;-&nbsp;</c:if><data:highlighttag value="${term}" on="${actionBean.ckhighlighting}"/></c:forEach></td>
				</tr>
</c:when>
<c:when test="${'AT' eq segment.key and not empty result.at}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label">Abstract type: </td>
					<td colspan="2">${result.at}</td>
				</tr>
</c:when>
<c:when test="${'SC' eq segment.key and not empty result.sc}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Scope: </h3></td>
					<td colspan="2">${result.sc}</td>
				</tr>
</c:when>
<c:when test="${'PNUM' eq segment.key and not empty result.pnum}"><!-- NTIS project number -->
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label">${result.labels['PNUM']}: </td>
					<td colspan="2"><a href="/search/results/quick.url?CID=quickSearchCitationFormat&searchWord1=${result.pnum}&section1=CT&database=${actionBean.database}&yearselect=yearrange&sort=yr" title="Search by project number">${result.pnum}</a></td>
				</tr>
</c:when>
<c:when test="${'TNUM' eq segment.key and not empty result.tnum}"><!-- NTIS task number -->
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label">${result.labels['TNUM']}: </td>
					<td colspan="2">${result.tnum}</td>
				</tr>
</c:when>
<c:when test="${'NTISP' eq segment.key and not empty result.ntispricecode}"><!-- NTIS price code -->
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label">${result.labels['NTISP']}: </td>
					<td colspan="2">${result.ntispricecode}</td>
				</tr>
</c:when>
<c:when test="${'AGS' eq segment.key and not empty result.ags}"><!-- Monitoring agency -->
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label">${result.labels['AGS']}: </td>
					<td colspan="2">${result.ags}</td>
				</tr>
</c:when>
<c:when test="${'CTS' eq segment.key and not empty result.cts}"><!-- Contract numbers -->
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label">${result.labels['CTS']}: </td>
					<td colspan="2">${result.cts}</td>
				</tr>
</c:when>
<c:when test="${'VI' eq segment.key and not empty result.vi}"><!-- Journal announcement -->
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label">${result.labels['VI']}: </td>
					<td colspan="2">${result.vi}</td>
				</tr>
</c:when>
<c:when test="${ 'RSP' eq segment.key and not empty result.rsp}"><!-- Sponsor -->
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label">Sponsor: </td>
					<td colspan="2"><data:highlighttag value="${result.rsp}" on="${actionBean.ckhighlighting}"/></td>
				</tr>
</c:when>
<c:when test="${'NR' eq segment.key and result.abstractrecord.refcount > 0}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Number of references: </h3></td>
					<td colspan="2">${result.abstractrecord.refcount}</td>
				</tr>
</c:when>
<c:when test="${'PC' eq segment.key and not empty result.pc}">  <!-- Page count -->
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${result.labels['PC']}: </h3></td>
					<td colspan="2">${result.pc}</td>
				</tr>
</c:when>
<c:when test="${'ANT' eq segment.key and not empty result.ant}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label">Annotation: </td>
					<td colspan="2">${result.ant}</td>
				</tr>
</c:when>
<c:when test="${'ILLUS' eq segment.key and not empty result.illus}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label">Illustrations: </td>
					<td colspan="2">${result.illus}</td>
				</tr>
</c:when>

<c:when test ="${'CAT' eq segment.key and not empty result.category}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label">Category: </td>
					<td class="affil" colspan="2">
						<span style="margin:0 5px 0 0"><c:forEach items="${result.category}" var="category" varStatus="castatus"><c:if test="${castatus.count > 1}"> - </c:if>${category.name}</c:forEach></span>
					</td>
				</tr>
</c:when>
<c:when test="${'DO' eq segment.key and not empty result.doi}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label">DOI: </td>
					<td colspan="2">${result.doi}</td>
				</tr>
</c:when>
<c:when test="${'MH' eq segment.key and not empty result.abstractrecord.termmap['MH']}">
			<%-- ************************************************************************* --%>
			<%-- Main heading terms                                                        --%>
			<%-- ************************************************************************* --%>
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${result.labels['MH']}: </h3></td>
					<td colspan="2"><c:forEach var="term" items="${result.abstractrecord.termmap['MH']}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if><c:choose><c:when test="${not empty term.searchlink}"><data:highlighttag value="${term.searchlink}" on="${actionBean.ckhighlighting}"/></c:when><c:otherwise><data:highlighttag value="${term.value}" on="${actionBean.ckhighlighting}"/></c:otherwise></c:choose></c:forEach></td>
				</tr>
</c:when>

<c:when test="${'MJSM' eq segment.key and not empty result.abstractrecord.termmap['MJSM']}">
			<%-- ************************************************************************* --%>
			<%-- Major terms                                                               --%>
			<%-- ************************************************************************* --%>
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${result.labels['MJSM']}: </h3></td>
					<td colspan="2"><c:forEach var="term" items="${result.abstractrecord.termmap['MJSM']}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if><c:choose><c:when test="${not empty term.searchlink}"><data:highlighttag value="${term.searchlink}" on="${actionBean.ckhighlighting}"/></c:when><c:otherwise><data:highlighttag value="${term.value}" on="${actionBean.ckhighlighting}"/></c:otherwise></c:choose></c:forEach></td>
				</tr>
</c:when>
<c:when test="${'PS' eq segment.key and not empty result.ntispricecode}">
			<%-- ************************************************************************* --%>
			<%-- NTIS price code                                                        --%>
			<%-- ************************************************************************* --%>
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${result.labels['NTISP']}: </h3></td>
					<td colspan="2">${result.ntispricecode}</td>
				</tr>
</c:when>
<c:when test="${'CVS' eq segment.key and not empty result.abstractrecord.termmap['CVS']}">
			<%-- ************************************************************************* --%>
			<%-- Controlled vocabulary terms  / Index Terms                                              --%>
			<%-- ************************************************************************* --%>
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${result.labels['CVS']}: </h3></td>
					<td colspan="2"><c:forEach var="term" items="${result.abstractrecord.termmap['CVS']}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if><c:choose><c:when test="${not empty term.searchlink}"><data:highlighttag value="${term.searchlink}" on="${actionBean.ckhighlighting}"/></c:when><c:otherwise><data:highlighttag value="${term.value}" on="${actionBean.ckhighlighting}"/></c:otherwise></c:choose></c:forEach></td>
				</tr>
</c:when>
<c:when test="${'OCVS' eq segment.key and not empty result.abstractrecord.termmap['OCVS']}">
			<%-- ************************************************************************* --%>
			<%-- Inspec original controlled terms                                          --%>
			<%-- ************************************************************************* --%>
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${result.labels['OCVS']}: </h3></td>
					<td colspan="2"><c:forEach var="term" items="${result.abstractrecord.termmap['OCVS']}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if><c:choose><c:when test="${not empty term.searchlink}"><data:highlighttag value="${term.searchlink}" on="${actionBean.ckhighlighting}"/></c:when><c:otherwise><data:highlighttag value="${term.value}" on="${actionBean.ckhighlighting}"/></c:otherwise></c:choose></c:forEach></td>
				</tr>
</c:when>
			<%-- ************************************************************************* --%>
			<%-- Companies                                                                 --%>
			<%-- ************************************************************************* --%>

<c:when test="${'CPO' eq segment.key and not empty result.abstractrecord.termmap['CPO']}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${result.labels['CPO']}: </h3></td>
					<td colspan="2"><c:forEach var="term" items="${result.abstractrecord.termmap['CPO']}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if><c:choose><c:when test="${not empty term.searchlink}"><data:highlighttag value="${term.searchlink}" on="${actionBean.ckhighlighting}"/></c:when><c:otherwise><data:highlighttag value="${term.value}" on="${actionBean.ckhighlighting}"/></c:otherwise></c:choose></c:forEach></td>
</c:when>
<c:when test="${'CRM' eq segment.key and not empty result.abstractrecord.termmap['CRM']}">
			<%-- ************************************************************************* --%>
			<%-- CAS Registry terms                                                        --%>
			<%-- ************************************************************************* --%>
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${result.labels['CRM']}: </h3></td>
					<td colspan="2"><c:forEach var="term" items="${result.abstractrecord.termmap['CRM']}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if><c:choose><c:when test="${not empty term.searchlink}"><data:highlighttag value="${term.searchlink}" on="${actionBean.ckhighlighting}"/></c:when><c:otherwise><data:highlighttag value="${term.value}" on="${actionBean.ckhighlighting}"/></c:otherwise></c:choose></c:forEach></td>
				</tr>
</c:when>
<c:when test="${'CMS' eq segment.key and not empty result.abstractrecord.termmap['CMS']}">
			<%-- ************************************************************************* --%>
			<%-- Chemicals                                                                 --%>
			<%-- ************************************************************************* --%>

				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${result.labels['CMS']}: </h3></td>
					<td colspan="2"><c:forEach var="term" items="${result.abstractrecord.termmap['CMS']}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if><c:choose><c:when test="${not empty term.searchlink}"><data:highlighttag value="${term.searchlink}" on="${actionBean.ckhighlighting}"/></c:when><c:otherwise><data:highlighttag value="${term.value}" on="${actionBean.ckhighlighting}"/></c:otherwise></c:choose></c:forEach></td>
</c:when>

<c:when test="${'CES' eq segment.key and not empty result.abstractrecord.termmap['CES']}">
			<%-- ************************************************************************* --%>
			<%-- Chemical Acronyms                                                         --%>
			<%-- ************************************************************************* --%>

				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${result.labels['CES']}: </h3></td>
					<td colspan="2"><c:forEach var="term" items="${result.abstractrecord.termmap['CES']}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if><c:choose><c:when test="${not empty term.searchlink}"><data:highlighttag value="${term.searchlink}" on="${actionBean.ckhighlighting}"/></c:when><c:otherwise><data:highlighttag value="${term.value}" on="${actionBean.ckhighlighting}"/></c:otherwise></c:choose></c:forEach></td>
</c:when>

<c:when test="${'ICS' eq segment.key and not empty result.abstractrecord.termmap['ICS']}">
			<%-- ************************************************************************* --%>
			<%-- SIC Codes                                                                 --%>
			<%-- ************************************************************************* --%>

				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${result.labels['ICS']}: </h3></td>
					<td colspan="2"><c:forEach var="term" items="${result.abstractrecord.termmap['ICS']}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if><c:choose><c:when test="${not empty term.searchlink}"><data:highlighttag value="${term.searchlink}" on="${actionBean.ckhighlighting}"/></c:when><c:otherwise><data:highlighttag value="${term.value}" on="${actionBean.ckhighlighting}"/></c:otherwise></c:choose></c:forEach></td>
</c:when>

<c:when test="${'DGS' eq segment.key and not empty result.abstractrecord.termmap['DGS']}">
         	<%-- ************************************************************************* --%>
			<%-- Geographical indexing                                                     --%>
			<%-- ************************************************************************* --%>

				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${result.labels['DGS']}: </h3></td>
					<td colspan="2"><c:forEach var="term" items="${result.abstractrecord.termmap['DGS']}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if><c:choose><c:when test="${not empty term.searchlink}"><data:highlighttag value="${term.searchlink}" on="${actionBean.ckhighlighting}"/></c:when><c:otherwise><data:highlighttag value="${term.value}" on="${actionBean.ckhighlighting}"/></c:otherwise></c:choose></c:forEach></td>
</c:when>

<c:when test="${'GCI' eq segment.key and not empty result.abstractrecord.termmap['GCI']}">
         	<%-- ************************************************************************* --%>
			<%-- Industrial Sector Codes                                                   --%>
			<%-- ************************************************************************* --%>

				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${result.labels['GCI']}: </h3></td>
					<td colspan="2"><c:forEach var="term" items="${result.abstractrecord.termmap['GCI']}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if><c:choose><c:when test="${not empty term.searchlink}"><data:highlighttag value="${term.searchlink}" on="${actionBean.ckhighlighting}"/></c:when><c:otherwise><data:highlighttag value="${term.value}" on="${actionBean.ckhighlighting}"/></c:otherwise></c:choose></c:forEach></td>
</c:when>
<c:when test="${'GDI' eq segment.key and not empty result.abstractrecord.termmap['GDI']}">
         	<%-- ************************************************************************* --%>
			<%-- Industrial Sectors                                                   --%>
			<%-- ************************************************************************* --%>

				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${result.labels['GDI']}: </h3></td>
					<td colspan="2"><c:forEach var="term" items="${result.abstractrecord.termmap['GDI']}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if><c:choose><c:when test="${not empty term.searchlink}"><data:highlighttag value="${term.searchlink}" on="${actionBean.ckhighlighting}"/></c:when><c:otherwise><data:highlighttag value="${term.value}" on="${actionBean.ckhighlighting}"/></c:otherwise></c:choose></c:forEach></td>
</c:when>

<c:when test="${'FLS' eq segment.key and not empty result.abstractrecord.termmap['FLS']}">
			<%-- ************************************************************************* --%>
			<%-- Uncontrolled terms  / Species terms                                                      --%>
			<%-- ************************************************************************* --%>
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${result.labels['FLS']}: </h3></td>
					<td colspan="2"><c:forEach var="term" items="${result.abstractrecord.termmap['FLS']}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if><c:choose><c:when test="${not empty term.searchlink}"><data:highlighttag value="${term.searchlink}" on="${actionBean.ckhighlighting}"/></c:when><c:otherwise><data:highlighttag value="${term.value}" on="${actionBean.ckhighlighting}"/></c:otherwise></c:choose></c:forEach></td>
				</tr>
</c:when>

<c:when test="${'CLS' eq segment.key and not empty result.abstractrecord.classificationcodes}">
			<%-- ************************************************************************* --%>
			<%-- Class codes:                                                     		   --%>
			<%-- ************************************************************************* --%>
<c:forEach var="clcodelist" items="${result.abstractrecord.classificationcodes}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${clcodelist.key}: </h3></td>
					<td colspan="2"><c:forEach var="clcode" items="${clcodelist.value}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if>${clcode.linkedid}${clcode.title}</c:forEach></td>
				</tr>
</c:forEach>
</c:when>
<c:when test="${'CLGM' eq segment.key and not empty result.abstractrecord.termmap['CLGM']}">
			<%-- ************************************************************************* --%>
			<%-- Classification Codes / NTIS                                               --%>
			<%-- ************************************************************************* --%>
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${result.labels['CLGM']}: </h3></td>
					<td colspan="2"><c:forEach var="term" items="${result.abstractrecord.termmap['CLGM']}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if><c:choose><c:when test="${not empty term.searchlink}"><data:highlighttag value="${term.searchlink}" on="${actionBean.ckhighlighting}"/></c:when><c:otherwise><data:highlighttag value="${term.value}" on="${actionBean.ckhighlighting}"/></c:otherwise></c:choose></c:forEach></td>
				</tr>
</c:when>


<c:when test="${'' eq segment.key and not empty result.abstractrecord.inspecclassificationcodes}">
			<%-- ************************************************************************* --%>
			<%-- Inspec classification codes                                               --%>
			<%-- ************************************************************************* --%>
<c:forEach var="clcodelist" items="${result.abstractrecord.inspecclassificationcodes}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${clcodelist.key}: </h3></td>
					<td colspan="2"><c:forEach var="clcode" items="${clcodelist.value}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if>${clcode.linkedid}&nbsp;${clcode.title}</c:forEach></td>
				</tr>
</c:forEach>
</c:when>
<c:when test="${'PUCM' eq segment.key and not empty result.abstractrecord.termmap['PUCM']}">  <!-- US Classification-->
<c:forEach var="term" items="${result.abstractrecord.termmap['PUCM']}" varStatus="status">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><c:choose><c:when test="${status.count eq 1}">${result.labels['PUCM']}: </c:when><c:otherwise>&nbsp;</c:otherwise></c:choose></td>
					<td class="ipccode">
	<c:choose><c:when test="${not empty term.searchlink}"><span class="ipctermlink">${term.searchlink}</span></c:when><c:otherwise><span class="ipcterm"><data:highlighttag value="${term.value}" on="${actionBean.ckhighlighting}"/></span></c:otherwise></c:choose>
					</td>
					<td class="ipcdescription">
	<c:if test="${not empty term.ipcdescription}">
					<data:highlighttag value="${term.ipcdescription}" on="${actionBean.ckhighlighting}"/>
	</c:if>
					</td>
				</tr>
				<tr>
						<!--<td>&nbsp;</td>-->
						<td class="label">&nbsp;</td>
						<td class="ipccode">&nbsp;</td>
						<td class="ipcdescription">&nbsp;</td>
				</tr>
</c:forEach>
</c:when>

<c:when test="${'CHI' eq segment.key and not empty result.chi}">  <!-- Chemical indexing -->
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${result.labels['CHI']}: </h3></td>
					<td colspan="2">${result.chi}</td>
				</tr>
</c:when>

<c:when test="${'PIDM' eq segment.key and not empty result.abstractrecord.termmap['PIDM']}">   <!-- IPC Code -->
			<%-- ************************************************************************* --%>
			<%-- IPC Code terms                                                            --%>
			<%-- ************************************************************************* --%>

<c:forEach var="term" items="${result.abstractrecord.termmap['PIDM']}" varStatus="status">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><c:choose><c:when test="${status.count eq 1}">${result.labels['PIDM']}: </c:when><c:otherwise>&nbsp;</c:otherwise></c:choose></td>
					<td class="ipccode">
	<c:choose><c:when test="${not empty term.searchlink}"><span class="ipctermlink">${term.searchlink}</span></c:when><c:otherwise><span class="ipcterm"><data:highlighttag value="${term.value}" on="${actionBean.ckhighlighting}"/></span></c:otherwise></c:choose>
					</td>
					<td class="ipcdescription">
	<c:if test="${not empty term.ipcdescription}">
					<data:highlighttag value="${term.ipcdescription}" on="${actionBean.ckhighlighting}"/>
	</c:if>
					</td>
				</tr>
				<tr>
						<!--<td>&nbsp;</td>-->
						<td class="label">&nbsp;</td>
						<td class="ipccode">&nbsp;</td>
						<td class="ipcdescription">&nbsp;</td>
				</tr>
</c:forEach>
</c:when>

<c:when test="${'PIDM8' eq segment.key and not empty result.abstractrecord.termmap['PIDM8']}">  <!-- IPC-8 Code -->
<c:forEach var="term" items="${result.abstractrecord.termmap['PIDM8']}" varStatus="status">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><c:choose><c:when test="${status.count eq 1}">${result.labels['PIDM8']}: </c:when><c:otherwise>&nbsp;</c:otherwise></c:choose></td>
					<td class="ipccode">
	<c:choose><c:when test="${not empty term.searchlink}"><span class="ipctermlink">${term.searchlink}</span></c:when><c:otherwise><span class="ipcterm">${term.value}</span></c:otherwise></c:choose>
					</td>
					<td class="ipcdescription">
	<c:if test="${not empty term.ipcdescription}">
					<data:highlighttag value="${term.ipcdescription}" on="${actionBean.ckhighlighting}"/>
	</c:if>
					</td>
				</tr>
				<tr>
						<!--<td>&nbsp;</td>-->
						<td class="label">&nbsp;</td>
						<td class="ipccode">&nbsp;</td>
						<td class="ipcdescription">&nbsp;</td>
				</tr>
</c:forEach>
</c:when>

<c:when test="${'PIDEPM' eq segment.key and not empty result.abstractrecord.termmap['PIDEPM']}"> <!-- IPC Codes -->
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label">${result.labels['PIDEPM']}: </td>
			    	<td colspan="2"><c:forEach var="term" items="${result.abstractrecord.termmap['PIDEPM']}" varStatus="status"><c:if test="${status.count>1}">&nbsp;-&nbsp;</c:if>${term.searchlink}</c:forEach></td>
				</tr>

</c:when>
<c:when test="${'PECM' eq segment.key and not empty result.abstractrecord.termmap['PECM']}">  <!-- ECLA Code -->
<c:forEach var="term" items="${result.abstractrecord.termmap['PECM']}" varStatus="status">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><c:choose><c:when test="${status.count eq 1}">${result.labels['PECM']}: </c:when><c:otherwise>&nbsp;</c:otherwise></c:choose></td>
					<td class="ipccode">
	<c:choose><c:when test="${not empty term.searchlink}"><span class="ipctermlink">${term.searchlink}</span></c:when><c:otherwise><span class="ipcterm"><data:highlighttag value="${term.value}" on="${actionBean.ckhighlighting}"/></span></c:otherwise></c:choose>
					</td>
					<td class="ipcdescription">
	<c:if test="${not empty term.ipcdescription}">
					<data:highlighttag value="${term.ipcdescription}" on="${actionBean.ckhighlighting}"/>
	</c:if>
					</td>
				</tr>
				<tr>
						<!--<td>&nbsp;</td>-->
						<td class="label">&nbsp;</td>
						<td class="ipccode">&nbsp;</td>
						<td class="ipcdescription">&nbsp;</td>
				</tr>
</c:forEach>
</c:when>

<c:when test="${'NDI' eq segment.key and not empty result.ndi}">  <!-- Numerical data indexing -->
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${result.labels['NDI']}: </h3></td>
					<td colspan="2">${result.ndi}</td>
				</tr>
</c:when>
<c:when test="${'TRS' eq segment.key and not empty result.abstractrecord.treatments}">
			<%-- ************************************************************************* --%>
			<%-- Treatments                                                                --%>
			<%-- ************************************************************************* --%>
				<tr>
					<!--<td>&nbsp;</td>-->
				<td class="label">${result.labels['TRS']}: </td>
				<td colspan="2">
				<c:forEach var="treatment" items="${result.abstractrecord.treatments}" varStatus="status"><c:if test="${status.count > 1}">;&nbsp;</c:if>${treatment}</c:forEach>
				</td>
				</tr>
</c:when>
<c:when test="${('DISP' eq segment.key or 'DISPS' eq segment.key) and not empty result.discipline}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Discipline: </h3></td>
					<td colspan="2">${result.discipline}</td>
				</tr>
</c:when>

<c:when test="${'LOCS' eq segment.key and not empty result.abstractrecord.termmap['LOCS']}">
<!-- Coordinates / Locations-->
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${result.labels['LOCS']}: </h3></td>
					<td colspan="2"><c:forEach var="term" items="${result.abstractrecord.termmap['LOCS']}" varStatus="status"><c:if test="${status.count > 1}"><br/></c:if>${term.value}</c:forEach></td>
				</tr>
</c:when>

<c:when test="${'LSTM' eq segment.key and not empty result.lstm}"> <!-- Linked Terms -->
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${result.labels['LSTM']}: </h3></td>
					<td colspan="2">
					<div class="lstdiv" id="lstdiv${result.doc.docid}">
					<input type="hidden" name="lstfield${result.doc.docid}" id="lstfield${result.doc.docid}"/>
   					<span href="" class="encompassOpenClose" isopen='false' termtype='lst' data='${result.lstm}' docid='${result.doc.docid}' title="Display Linked terms">
   					<a class="open" border="0">Open all linked terms view</a><a class="close" border="0" style="display:none">Close all linked terms view</a>&nbsp;&nbsp;<a class="plusminus">+</a><a class="plusminus" style="display:none">-</a>
   					</span>

   					<table style="margin:0px; padding:0px; border:0px black solid; width:100%" id="lst_table${result.doc.docid}">
						<tbody id="lst_table_body${result.doc.docid}">
						</tbody>
					</table>
						</div>
					</td>
				</tr>
</c:when>
<c:when test="${'LTH' eq segment.key and not empty result.lth}"> <!-- Linked Terms Holder -->
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${result.labels['LTH']}: </h3></td>
					<td colspan="2"><div class="longltdiv" id="longltdiv${result.doc.docid}">
					<input type="hidden" id="istag${result.doc.docid}" name="istag${result.doc.docid}"  value="default"/>
                	<input type="hidden" name="longltfield${result.doc.docid}" id="longltfield${result.doc.docid}"/>
                	<input type="hidden" name="schid${result.doc.docid}" id="schid${result.doc.docid}"  value='${actionBean.searchid}'/>
   					<span href="" class="encompassOpenClose" isopen='false' termtype='longlt' data='${result.doc.docid}' docid='${result.doc.docid}' title="Display Linked terms">
   					<a class="open" border="0">Open all linked terms view</a><a class="close" border="0" style="display:none">Close all linked terms view</a>&nbsp;&nbsp;<a class="plusminus">+</a><a class="plusminus" style="display:none">-</a>
   					</span>

   					<table style="margin:0px; padding:0px; border:0px black solid; width:100%" id="longlt_table${result.doc.docid}">
						<tbody id="longlt_table_body${result.doc.docid}">
						</tbody>
					</table>
					</div></td>
				</tr>
</c:when>
<c:when test="${'ATM' eq segment.key and not empty result.atm}"> <!-- Indexing template -->
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${result.labels['ATM']}: </h3></td>
					<td colspan="2"><div class="atmdiv" id="atmdiv${result.doc.docid}">
					<input type="hidden" name="atmfield${result.doc.docid}" id="atmfield${result.doc.docid}" />
   					<span href="" class="encompassOpenClose" isopen='false' id="atmOpenClose" termtype='atm' data='${result.atm}' docid='${result.doc.docid}' title="Display Indexing template">
   					<a class="open" border="0">Open all templates view</a><a class="close" border="0" style="display:none">Close all templates view</a>&nbsp;&nbsp;<a class="plusminus">+</a><a class="plusminus" style="display:none">-</a>
   					</span>

					<table style="margin:0px; padding:0px; border:0px black solid; width:100%" id="atm_table${result.doc.docid}">
						<tbody id="atm_table_body${result.doc.docid}">
						</tbody>
					</table>

					</div></td>
				</tr>

</c:when>
<c:when test="${'MLT' eq segment.key and not empty result.mlt}"> <!-- Manually linked terms -->
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${result.labels['MLT']}: </h3></td>
					<td colspan="2"><div class="mltdiv" id="mltdiv${result.doc.docid}">
					<input type="hidden" name="mltfield${result.doc.docid}" id="mltfield${result.doc.docid}"/>
   					<span href="" class="encompassOpenClose" isopen='false' id="mltOpenClose" termtype='mlt' data='${result.mlt}' docid='${result.doc.docid}' title="Display Manually linked terms">
   					<a class="open" border="0">Open manually linked terms</a><a class="close" border="0" style="display:none">Close manually linked terms</a>&nbsp;&nbsp;<a class="plusminus">+</a><a class="plusminus" style="display:none">-</a>
   					</span>

   					<table style="margin:0px; padding:0px; border:0px black solid; width:100%" id="mlt_table${result.doc.docid}">
						<tbody id="mlt_table_body${result.doc.docid}">
						</tbody>
					</table>
					</div></td>
				</tr>
</c:when>
<c:when test="${'BTI' eq segment.key and not empty result.bti}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Book title: </h3></td>
					<td colspan="2">${result.bti}</td>
				</tr>
</c:when>
<c:when test="${'BPP' eq segment.key and not empty result.bpp and result.bpp ne '0' }">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Page number: </h3></td>
					<td colspan="2">${result.bpp}</td>
				</tr>
</c:when>
<c:when test="${'BTST' eq segment.key and not empty result.btst}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Series title: </h3></td>
					<td colspan="2">${result.btst}</td>
				</tr>
</c:when>

<c:when test="${'RN_LABEL' eq segment.key and not empty result.rnlabel}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Report number: </h3></td>
					<td colspan="2">${result.rnlabel}</td>
				</tr>
</c:when>
<c:when test="${'RN' eq segment.key and not empty result.rn}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Report number: </h3></td>
					<td colspan="2">${result.rn}</td>
				</tr>
</c:when>
<c:when test="${'BCT' eq segment.key and not empty result.bct}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Chapter title: </h3></td>
					<td colspan="2">${result.bct}</td>
				</tr>
</c:when>
<c:when test="${'BST' eq segment.key and not empty result.bst}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Section title: </h3></td>
					<td colspan="2">${result.bst}</td>
				</tr>
</c:when>
<c:when test="${'BPC' eq segment.key and not empty result.bpc}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Total Pages: </h3></td>
					<td colspan="2">${result.bpc}</td>
				</tr>
</c:when>
<c:when test="${'BYR' eq segment.key and not empty result.byr}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Year: </h3></td>
					<td colspan="2">${result.byr}</td>
				</tr>
</c:when>


<c:when test="${'MED' eq segment.key and not empty result.med}"><!-- Source medium -->
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label">${result.labels['MED']}: </td>
					<td colspan="2">${result.med}</td>
				</tr>
</c:when>

<c:when test="${'IMG' eq segment.key and not empty result.abstractrecord.figures}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Figures: </h3></td>
					<td colspan="2"><c:forEach var="figure" items="${result.abstractrecord.figures}"><a href="javascript:window.open('${actionBean.s3FigUrl}${figure}','newwind','width=650,height=600,scrollbars=yes,resizable,statusbar=yes');void('');"><img src="${actionBean.s3FigUrl}${figure}" alt="" border="1" width="100" height="100"/></a></c:forEach></td>
				</tr>
</c:when>


<c:when test="${'BKYS' eq segment.key and not empty result.abstractrecord.termmap['BKYS']}">
			<%-- ************************************************************************* --%>
			<%-- KeyWords                            					                   --%>
			<%-- ************************************************************************* --%>
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Keywords: </h3></td>
					<td colspan="2"><c:forEach var="term" items="${result.abstractrecord.termmap['BKYS']}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if><c:choose><c:when test="${not empty term.searchlink}"><data:highlighttag value="${term.searchlink}" on="${actionBean.ckhighlighting}"/></c:when><c:otherwise><data:highlighttag value="${term.value}" on="${actionBean.ckhighlighting}"/></c:otherwise></c:choose></c:forEach></td>
				</tr>
</c:when>


<c:when test="${'COL' eq segment.key and not empty result.collection}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Collection name:</h3></td>
					<td colspan="2">${result.collection}</td>
				</tr>
</c:when>


<c:when test="${'RGIS' eq segment.key and not empty result.abstractrecord.termmap['RGIS']}">
			<%-- ************************************************************************* --%>
			<%-- Regional terms                                                            --%>
			<%-- ************************************************************************* --%>
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>${result.labels['RGIS']}: </h3></td>
					<td colspan="2"><c:forEach var="term" items="${result.abstractrecord.termmap['RGIS']}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if><c:choose><c:when test="${not empty term.searchlink}"><data:highlighttag value="${term.searchlink}" on="${actionBean.ckhighlighting}"/></c:when><c:otherwise><data:highlighttag value="${term.value}" on="${actionBean.ckhighlighting}"/></c:otherwise></c:choose></c:forEach></td>
				</tr>
</c:when>
</c:choose>
</c:forEach>
			<%-- ************************************************************************* --%>
			<%-- Other info                                                                --%>
			<%-- ************************************************************************* --%>
<c:if test="${not empty result.doc.dbname}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"><h3>Database: </h3></td>
					<td colspan="2"><span class="dbnameidentifier">${result.doc.dbname}</span></td>
				</tr>
</c:if>

<c:if test="${not empty result.cpr}">
				<tr>
					<!--<td>&nbsp;</td>-->
					<td class="label"></td>
					<td colspan="2">${result.cpr}</td>
				</tr>
</c:if>
			</table>

			<c:if test="${(not empty result.lhlinkObjects) or (result.fulltext) or (actionBean.lindahall)}">
				<div id="deliverylinks" style="margin-left:92.5px;width:90.5%">

					<h3 id="deliveryheader" style="padding: 3px 3px;<c:if test="${displaywhite % 2 eq 0}"> background-color: #FFFFFF;</c:if>">Full-text and Local Holdings Links</h3>
					<div id="deliverybody">
						<c:if test="${not empty result.lhlinkObjects}">
							<p>
							<c:forEach items="${result.lhlinkObjects}" var="lhlink" varStatus="status">
								<c:if test="${not empty lhlink.url}">
									<c:if test="${status.count>1}"><span class="pipe">|</span></c:if>
									<c:choose>
		                        		<c:when test="${not empty lhlink.imageUrl}">
		                        			<a CLASS="LgBlueLink" title="Right-click to open in new tab" onclick="openLocalHoldingsLink(event,'Detailed Format',this);" href="/search/results/localholdinglinks.url?docId=${result.doc.docid}&url=${lhlink.url}&position=${lhlink.position}" target="new">
		                        				<img src="${lhlink.imageUrl}" alt="${lhlink.label}" border="0" />
		                        			</a>
										</c:when>
		                        		<c:otherwise>
		                        			<a CLASS="LgBlueLink" title="Right-click to open in new tab" onclick="openLocalHoldingsLink(event,'Detailed Format',this);" href="/search/results/localholdinglinks.url?docId=${result.doc.docid}&url=${lhlink.url}&position=${lhlink.position}" target="new">${lhlink.label}</a>
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
			</c:if>

			<c:if test="${'131072' eq result.doc.dbmask}">
			<div style="margin-left:92.5px;width:90.5%;padding-bottom:10px;">
					<c:if test="${not empty result.readpagelink}">${result.readpagelink}</c:if>
					<c:if test="${not empty result.readchapterlink}"><c:if test="${not empty result.readpagelink}"><span class="pipe">|</span></c:if>${result.readchapterlink}</c:if>
					<c:if test="${not empty result.readbooklink}"><c:if test="${not empty result.readpagelink or not empty result.readchapterlink}"><span class="pipe">|</span></c:if>${result.readbooklink}</c:if>
					</div>
			</c:if>
	     </div>

		</c:forEach>