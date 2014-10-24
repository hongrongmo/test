<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" trimDirectiveWhitespaces="true"  session="false"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
	<div class="pagenav" style="float:left;margin: 0px 12px 5px 0">
		<form name="gotopageform" id="gotopage_${param.position}" action="${rerunactionurl}">
			<input type="hidden" name="SEARCHID" value="${actionBean.searchid}"></input>
				<input type="hidden" name="database" value="${actionBean.database}"></input>
				<input type="hidden" name="CID" value="${actionBean.CID}"></input>
				<input type="hidden" name="offset" value="${actionBean.pagenav.resultsperpage}"></input>
				<input type="hidden" name="format" value="patentReferencesFormat"></input>
				<input type="hidden" name="docid" value="${actionBean.docid}"></input>
				<input type="hidden" name="pagecount" value="${actionBean.pagenav.pagecount}"></input>
				<input type="hidden" value="${actionBean.resultsperpage}" name="resultCountPerPage"/>
				<input type="hidden" value="${rerunactionurl}" name="rerunactionurl"/>

			<c:set var="maxSearchresult" value="${(actionBean.pagenav.currentindex - 1) * actionBean.pagenav.resultsperpage + actionBean.resultsperpage}" />
	<c:if test="${not(actionBean.pageCountOption eq null) && (not empty actionBean.pageCountOption)}">
		<span class="recordperpage"  title="Choose the number of results to display per page" aria-label="Select number of results per page" role="navigation"><span>Display:</span>
					<select name="pageSizeVal" id="pageSizeVal_${param.position}">
					<c:forEach items="${actionBean.pageCountOption}" var="pageCountOption" varStatus="count" >
								<option value="${pageCountOption}" <c:if test="${actionBean.resultsperpage == pageCountOption}"> selected="selected" </c:if>>${pageCountOption}</option>
					</c:forEach>
					</select>
					<span>results per page</span>
					<noscript><input type="submit" class="button" value="Go" title="Go to Selected Order"></input></noscript>
		</span>
	</c:if>
		 <span class="pagenavspan" aria-label="Select page" role="navigation">
		 <c:if test="${actionBean.pagenav.currentindex > 1}">
			<span><a href="/controller/servlet/Controller?CID=${actionBean.CID}&docid=${actionBean.docid}&SEARCHID=${actionBean.searchid}&PAGE=${actionBean.pagenav.currentindex-1}&database=${actionBean.database}&pageSizeVal=${actionBean.resultsperpage}" title="Go to previous page">&lt; Previous</a>&nbsp;&nbsp;|&nbsp;&nbsp;</span>
		 </c:if>
			<span><c:choose><c:when test="${actionBean.pagenav.currentindex == 1}">Go to page: </c:when><c:otherwise>Page: </c:otherwise></c:choose></span>
			<c:set var="inputname">PAGE</c:set>
			<input name="${inputname}" type="text" size="2" value="${actionBean.pagenav.currentindex}" title="Page Number Text Box" style="text-align: right"/>
			<span>of ${actionBean.pagenav.pagecount}</span>

			<input type="submit" class="button" value="Go" title="Go to designated page"></input>
			<c:if test="${(actionBean.pagenav.currentindex < actionBean.pagenav.pagecount)}">
				<span><a href="/controller/servlet/Controller?CID=${actionBean.CID}&docid=${actionBean.docid}&SEARCHID=${actionBean.searchid}&PAGE=${actionBean.pagenav.currentindex+1}&database=${actionBean.database}&pageSizeVal=${actionBean.resultsperpage}" title="Go to next page">Next &gt;</a></span>
		    </c:if>

		</span>
    	</form>
	</div>
