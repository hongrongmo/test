<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/tlds/datatags.tld" prefix="data" %>

<table border="0" width="100%" id="detailed" style=" margin-bottom: 10px;min-width:490">
        <tr>
            <td valign="top" align="left">
                <table border="0" width="100%" cellspacing="0" cellpadding="0" style="min-width:100">
                  <tr>
                    <td valign="top">
                      <img border="0" width="100" height="145" style="float:left; margin-top:5px;"
                       src="${result.bimgfullpath}">
                       
                     </td>
                  </tr>  
                  </table>
                </td> 
                <td> 
                <table border="0" width="100%" cellspacing="0" cellpadding="0" style="min-width:490">
                  <tr>
                       <td colspan="10" valign="top" align="left"><b>${result.bti}<c:if test="${not empty result.btst}">: ${result.btst}</c:if></b></td>
                    </tr>
                    
                    <tr>
					<td colspan="10" valign="top" align="left"><span class="authors">
						<c:forEach items="${result.authors}" var="author" varStatus="austatus">
							<c:choose>
							<c:when test="${not empty author.searchlink and not (author.nameupper eq 'ANON')}"><a href="${author.searchlink}" title="Search Author">${author.name}</a><c:if test="${not empty author.affils}"><sup><c:forEach items="${author.affils}" varStatus="afstatus" var="affil"><c:if test="${afstatus.count > 1}">, </c:if>${affil.id}</c:forEach></sup></c:if><c:if test="${not empty author.email}">&nbsp;<a href="mailto:${author.email}" title="Author email" class="emaillink"><img src="/static/images/emailfolder.gif"/></a></c:if><c:if test="${(austatus.count < fn:length(result.authors))}">; </c:if></c:when>
							<c:otherwise>${author.name}<c:if test="${(austatus.count > 1) and (austatus.count < fn:length(result.authors))}">; </c:if></c:otherwise>
							</c:choose>
							</c:forEach>
						</span> ;<c:if test="${not empty result.isbn}"> <b>ISBN-10: </b>${result.isbnlink}</c:if><c:if test="${not empty result.isbn13}">, <b>ISBN-13: </b>${result.isbn13link}</c:if><c:if test="${not empty result.bpc}">, ${result.bpc} p</c:if><c:if test="${not empty result.byr}">, ${result.byr}</c:if>;
						<c:if test="${not empty result.bpn}">  <b>Publisher: </b>${result.bpn}</c:if>
					  </td>
					</tr>
					<tr>
                       <td>&nbsp;</td>
                    </tr>
                    <tr>
                       <td>&nbsp;</td>
                    </tr>
					<tr>
                       <td  colspan="10" valign="top" align="left"><b>Book description:</b></td>
                    </tr>
                   <tr>
                       <td colspan="10" valign="top" align="left"><p><data:highlighttag value="${result.abstractrecord.bookdescription}" on="${actionBean.ckhighlighting}" v2="${result.abstractrecord.v2}"/></p></td>
                    </tr>
                    <tr>
                       <td>&nbsp;</td>
                    </tr>
                    <tr>
                       <td>&nbsp;</td>
                    </tr>
                    <tr>
            <%-- ************************************************************************* --%>
			<%-- Subject terms                                               --%>
			<%-- ************************************************************************* --%>
			<c:if test="${not empty result.abstractrecord.termmap['CVS']}">
				<tr>
					<td  colspan="10" valign="top" align="left"><p><b>${result.labels['CVS']}: </b><c:forEach var="term" items="${result.abstractrecord.termmap['CVS']}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if><c:choose><c:when test="${not empty term.searchlink}"><data:highlighttag value="${term.searchlink}" on="${actionBean.ckhighlighting}"/></c:when><c:otherwise><data:highlighttag value="${term.value}" on="${actionBean.ckhighlighting}"/></c:otherwise></c:choose></c:forEach></p></td>
				</tr>
				<tr>
                    <td>&nbsp;</td>
                </tr>
            </c:if>
			
			<c:if test="${not empty result.collection}">
				<tr>
					<td colspan="10" valign="top" align="left"><b>Collection name:</b>${result.collection}</td>
				</tr>
				<tr>
                    <td>&nbsp;</td>
                </tr>
			</c:if>
			
				<tr>
					<td  colspan="10" valign="top" align="left"><b>Database: </b>${result.doc.dbname}</td>
				</tr>
				<tr>
                    <td>&nbsp;</td>
                </tr>
                <tr>    
                    <td valign="top" align="left">
                      <p/>
                      <b>Table of Contents</b>
                      <p/>
                 </tr> 
                <tr>
                    <td>
                      <div id="toc">
                      ${actionBean.toc}
                      </div>
                      <p/>
                      <a CLASS="SpBoldLink" href="#top">Back to Top</a>
                    </td>

                    
                   </tr>
                </table>
                <!-- END Book Detail Document -->
            </td>
          </tr>
    </table>