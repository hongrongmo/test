<%@ page language="java" contentType="text/html; charset=UTF-8"	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<c:if test="${result.abstractrecord.citerefcount > 0 || result.abstractrecord.patrefcount > 0 || result.abstractrecord.nonpatrefcount > 0}">
		<div id="references_box" class="toolbox">
<c:if test="${result.abstractrecord.citerefcount > 0 && not empty result.abstractrecord.citedbyrefslink}">
			<p><b>Cited by: </b>This patent has been cited <b><a href="${result.abstractrecord.citedbyrefslink}" title="Display citations">${result.abstractrecord.citerefcount} time${result.abstractrecord.citerefcount>1?'s':''}</a></b> in ${result.doc.dbname}.</p>
</c:if>
<c:if test="${result.abstractrecord.patrefcount > 0 && not empty result.abstractrecord.patentrefslink}">
			<p><b>Patent Refs: </b>This patent has <b><a href="${result.abstractrecord.patentrefslink}" title="Display patent references">${result.abstractrecord.patrefcount} reference${result.abstractrecord.patrefcount>1?'s':''}</a></b> in ${result.doc.dbname}.</p>
</c:if>
<c:if test="${result.abstractrecord.nonpatrefcount > 0 && not empty result.abstractrecord.otherrefslink}">
			<p><b>Other Refs: </b>This patent has <b><a href="${result.abstractrecord.otherrefslink}" title="Display additional references">${result.abstractrecord.nonpatrefcount} additional reference${result.abstractrecord.nonpatrefcount>1?'s':''}.</a></b></p>
</c:if>
		</div>
</c:if>


