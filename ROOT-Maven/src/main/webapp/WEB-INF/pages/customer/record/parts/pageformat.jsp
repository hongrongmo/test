<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/tlds/datatags.tld" prefix="data" %>

<img src="/static/images/s.gif" border="0" name="image_basket">
			<table border="0" width="100%" id="detailed" style=" margin-bottom: 10px; ">


				<tr>
					<td style="white-space: nowrap"><span style="font-weight: normal;font-size: 14px">${result.doc.hitindex}. <input type="checkbox" name="cbresult" handle="${result.doc.hitindex}" docid="${result.doc.docid}" dbid="${result.doc.dbid}"<c:if test="${result.selected}"> checked="checked"</c:if>/></span></td>
					<td class="label"><b>Book title: </b></td>
					<td colspan="2">
						<p>${result.bti}</p>
					</td>
				</tr>

				<tr>
					<td>&nbsp;</td>
					<td class="label"><b>Page number: </b></td>
					<td colspan="2">${result.bpp}</td>
				</tr>
				<c:if test="${not empty result.btst}">
				<tr>
					<td>&nbsp;</td>
					<td class="label"><b>Series title: </b></td>
					<td colspan="2">${result.btst}</td>
				</tr>
				</c:if>
				<tr>
					<td>&nbsp;</td>
					<td class="label"><b>${result.labels['AUS']}: </b></td>
					<td colspan="2"><span class="authors">
						<c:forEach items="${result.authors}" var="author" varStatus="austatus">
							<c:choose>
							<c:when test="${not empty author.searchlink and not (author.nameupper eq 'ANON')}"><a href="${author.searchlink}" title="Search Author">${author.name}</a><c:if test="${not empty author.affils}"><sup><c:forEach items="${author.affils}" varStatus="afstatus" var="affil"><c:if test="${afstatus.count > 1}">, </c:if>${affil.id}</c:forEach></sup></c:if><c:if test="${not empty author.email}">&nbsp;<a href="mailto:${author.email}" title="Author email" class="emaillink"><img src="/static/images/emailfolder.gif"/></a></c:if><c:if test="${(austatus.count < fn:length(result.authors))}">; </c:if></c:when>
							<c:otherwise>${author.name}<c:if test="${(austatus.count > 1) and (austatus.count < fn:length(result.authors))}">; </c:if></c:otherwise>
							</c:choose>
						</c:forEach>
					</span></td>
				</tr>
				
				<c:if test="${not empty result.isbn}">
				<tr>
					<td>&nbsp;</td>
					<td class="label"><b>ISBN-10 : </b></td>
					<td colspan="2"><c:choose><c:when test="${empty result.isbnlink}">${result.isbn}</c:when><c:otherwise>${result.isbnlink}</c:otherwise></c:choose></td>
				</tr>
				</c:if>
				<c:if test="${not empty result.isbn13}">
								<tr>
									<td>&nbsp;</td>
									<td class="label"><b>ISBN-13: </b></td>
									<td colspan="2"><c:choose><c:when test="${empty result.isbn13link}">${result.isbn13}</c:when><c:otherwise>${result.isbn13link}</c:otherwise></c:choose></td>
								</tr>
				</c:if>
				
				<c:if test="${not empty result.bct}">
								<tr>
									<td>&nbsp;</td>
									<td class="label"><b>Chapter title: </b></td>
									<td colspan="2">${result.bct}</td>
								</tr>
				</c:if>
				
				<c:if test="${not empty result.bst}">
								<tr>
									<td>&nbsp;</td>
									<td class="label"><b>Section title: </b></td>
									<td colspan="2">${result.bst}</td>
								</tr>
				</c:if>
				
				<c:if test="${not empty result.bpc}">
								<tr>
									<td>&nbsp;</td>
									<td class="label"><b>Total Pages: </b></td>
									<td colspan="2">${result.bpc}</td>
								</tr>
				</c:if>
				
				<c:if test="${not empty result.byr}">
								<tr>
									<td>&nbsp;</td>
									<td class="label"><b>Year: </b></td>
									<td colspan="2">${result.byr}</td>
								</tr>
				</c:if>
				
				<c:if test="${not empty result.bpn}">
								<tr>
									<td>&nbsp;</td>
									<td class="label"><b>Publisher: </b></td>
									<td colspan="2">${result.bpn}</td>
								</tr>
				</c:if>

				<!-- ******************************************************** -->
				<!-- ABSTRACT TEXT! -->
				<!-- ******************************************************** -->
				<c:if test="${not empty result.abstractrecord.text}">
								<tr>
									<td>&nbsp;</td>
									<td class="label"><b>Page Highlights: </b></td>
									<td colspan="2"><data:highlighttag value="${result.abstractrecord.text}" on="${actionBean.ckhighlighting}" v2="${result.abstractrecord.v2}"/></td>
								</tr>
				</c:if>

			<%-- ************************************************************************* --%>
			<%-- KeyWords                                              --%>
			<%-- ************************************************************************* --%>
				<c:if test="${not empty result.abstractrecord.termmap['BKYS']}">
								<tr>
									<td>&nbsp;</td>
									<td class="label"><b>Keywords: </b></td>
									<td colspan="2"><c:forEach var="term" items="${result.abstractrecord.termmap['BKYS']}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if><c:choose><c:when test="${not empty term.searchlink}"><data:highlighttag value="${term.searchlink}" on="${actionBean.ckhighlighting}"/></c:when><c:otherwise><data:highlighttag value="${term.value}" on="${actionBean.ckhighlighting}"/></c:otherwise></c:choose></c:forEach></td>
								</tr>
				</c:if>


			<%-- ************************************************************************* --%>
			<%-- Subject terms                                               --%>
			<%-- ************************************************************************* --%>
				<c:if test="${not empty result.abstractrecord.termmap['CVS']}">
								<tr>
									<td>&nbsp;</td>
									<td class="label"><b>${result.labels['CVS']}: </b></td>
									<td colspan="2"><c:forEach var="term" items="${result.abstractrecord.termmap['CVS']}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if><c:choose><c:when test="${not empty term.searchlink}"><data:highlighttag value="${term.searchlink}" on="${actionBean.ckhighlighting}"/></c:when><c:otherwise><data:highlighttag value="${term.value}" on="${actionBean.ckhighlighting}"/></c:otherwise></c:choose></c:forEach></td>
								</tr>
				</c:if>

				<c:if test="${not empty result.collection}">
								<tr>
									<td>&nbsp;</td>
									<td class="label"><b>Collection name:</b></td>
									<td colspan="2">${result.collection}</td>
								</tr>
				</c:if>
				<tr>
					<td>&nbsp;</td>
					<td class="label"><b>Database: </b></td>
					<td colspan="2">${result.doc.dbname}</td>
				</tr>
				<tr>
					<td>&nbsp;</td>
					<td class="label">&nbsp;</td>
					<td colspan="2">&nbsp;</td>
				</tr>
				<c:if test="${not empty result.cpr}">
								<tr>
									<td>&nbsp;</td>
									<td class="label"></td>
									<td colspan="2">${result.cpr}</td>
								</tr>
				</c:if>
			</table>
