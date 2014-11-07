<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/tlds/functions.tld" prefix="f" %>
<%@ taglib uri="/WEB-INF/tlds/custom.tld" prefix="cm" %>


<style type="text/css">
#termresults table {float:left; margin-right: 10px}
#termresults table td.term {width:180px; white-space: normal;}
.RedText  {color:#FF3300;}
</style>

<h3 class="searchcomponentlabel" style="float: none; margin-bottom: 3px; text-transform: uppercase">Exact Term</h3>
<%-- TERM PATH DISPLAY --%>
<c:set var="laststep">${fn:length(actionBean.steps)}</c:set>
<c:set var="scon" value="${actionBean.steps[0].context}"/>
    <div id="termpath">
    <c:choose>
    <c:when test="${empty actionBean.fullrecresults}">
        <span>0 matching terms found for: <span step=0 scon="${scon}" class="Bold">${actionBean.term}</span></span>
    </c:when>
    <c:otherwise>
    <c:forEach var="step" items="${actionBean.steps}" varStatus="status">
        <c:choose>
        <c:when test="${status.last}">
        <span step="${status.count-1}"<c:if test="${status.count == 1}"> scon=${step.context}</c:if> style="font-weight: bold">${step.title}</span>
        </c:when> 
        <c:otherwise>
        <a href="#${step.link}" title="${step.title}" class="termsearchlink rolllink"><span <c:if test="${status.count == 1}"> scon=${step.context}</c:if> step="${status.count-1}">${step.title}</span></a> >> 
        </c:otherwise>
        </c:choose>
    </c:forEach>
    </c:otherwise>
    </c:choose>
    </div>

<div class="clear"></div>

<div id="termresults" class="termelement">
<%-- ********************************************** --%>
<%--
    Possible scenarios for this full record page:
    1) The user clicked on one of the returned terms inside of a search
    or browse request.  The "fullrecresults" object in the ActionBean 
    will be present with terms
    2) The user submitted the form with "Exact Term" option and there were
    no hits.  The "termsearchresults" object will have a list of suggested
    terms.
 --%>
<%-- ********************************************** --%>
<table>
<c:if test="${(empty actionBean.fullrecresults) and (empty actionBean.termsearchsuggests)}">
    <tr><td><span class="RedText">Your search did not find any match for "${actionBean.term}".</span></td></tr> 
</c:if>
<c:choose>
<%-- ******************************* --%>
<%-- Full Rec search with HITS! --%>
<%-- ******************************* --%>
<c:when test="${not empty actionBean.fullrecresults}">

<c:forEach items="${actionBean.fullrecresults}" var="fullrecresult" varStatus="status" end="4">
<c:if test="${(not empty fullrecresult.recID.mainTerm)}">
  <tr><td>
    <!--  <div style="float:left: 5px"> -->
    <!--  <div style="margin-bottom: 5px"> -->
    <table><tr><td>
    <c:if test="${(not empty fullrecresult.recID.mainTerm) and (fullrecresult.status ne 'L' or (actionBean.database eq '1' and fullrecresult.historyScopeNotes eq 'FEV former Ei Vocabulary term'))}"><input type="checkbox" class="addtoclipboard" style="_top:0; top:-2px;float:left: 1px" value="${fullrecresult.recID.mainTerm}"></input></c:if>
    <span class="mainterm"<c:if test="${fullrecresult.status eq 'L' and fullrecresult.historyScopeNotes ne 'FEV former Ei Vocabulary term'}"> style="font-style:italic"</c:if>>${fullrecresult.recID.mainTerm}</span> 
    <c:if test="${fullrecresult.info}">&nbsp;&nbsp;<a class="scopenotelink" href="/thes/scopenote.url?database=${actionBean.database}&term=${f:encode(fullrecresult.recID.mainTerm)}" title="See notes"><img border="0" align="bottom" class="scopenoteimg" title="" src="/static/images/Full-Text.png"/></a></c:if>
    </td></tr></table>
    <div class="clear"></div>
    <!-- </div> -->

    <c:if test="${not empty fullrecresult.registryNumber}">
    <table cellpadding="0" cellspacing="0" border="0" class="termsearchtable">
        <thead> 
        <tr><th align="center">&nbsp;</th><th align="left">CAS Number</th></tr> 
        </thead>    
        <tbody> 
            <c:forEach items="${fullrecresult.registryNumber}" var="thesrecord" varStatus="status"> 
                <tr><td valign="bottom" align="center">                             
                         <input type="checkbox" class="addtoclipboard" value="${thesrecord}" ></input>
                    </td><td  valign="top" class="termsearchtable">
                         <div class="termsearchtable">${thesrecord}</div>   
                </td></tr>
            </c:forEach>        
        </tbody>
    </table>
    <div class="clear"></div>
    </c:if>

    <c:if test="${not empty fullrecresult.registryNumberBroaderTerm}">
    <table cellpadding="0" cellspacing="0" border="0" class="termsearchtable">
        <thead>
        <tr><th align="center">&nbsp;</th><th align="left">CAS Number Broader Term</th></tr>
        </thead>
        <tbody> 
            <c:forEach items="${fullrecresult.registryNumberBroaderTerm}" var="thesrecord" varStatus="status">
                <tr><td align="center" valign="top">            
                         <input type="checkbox" class="addtoclipboard" value="${thesrecord}"></input>
                    </td><td class="term">
                         <div class="termsearchtable">${thesrecord}</div>
                </td></tr>
            </c:forEach>
        </tbody>
    </table>
    <div class="clear"></div>
    </c:if>
    

    <c:if test="${not empty fullrecresult.leadinTerms.records}">
    <div style="float:left">   
    <table  cellpadding="0" cellspacing="0" border="0" class="termsearchtable">  
    <tr><td align="left" nowrap="nowrap"><b>Used for:</b>&nbsp;</td>  
    <td>
    <c:set var="size" value="${fn:length(fullrecresult.leadinTerms.records)}"/> 
    <c:forEach items="${fullrecresult.leadinTerms.records}" var="thesrecord" varStatus="status">   
    <c:if test="${not empty thesrecord.recID.mainTerm}">
        <a href="#/search/thes/fullrec.url?snum=${laststep}&CID=thesFullRec&tid=${f:encode(thesrecord.recID.recordID)}&term=${f:encode(thesrecord.recID.mainTerm)}&database=${actionBean.database}" ajax="/controller/servlet/Controller?CID=thesFullRec&tid=${f:encode(thesrecord.recID.recordID)}&term=${f:encode(thesrecord.recID.mainTerm)}&database=${actionBean.database}" class="fullreclink rolllink"><i>${thesrecord.recID.mainTerm}</i><c:if test="${status.count > 0 and status.count<size}">; </c:if></a>    
    </c:if>
    </c:forEach>
    </td></tr> 
    </table>
    </div>
    <div class="clear"></div>
    </c:if>


    <div id="termtablewrap">
    <c:if test="${not empty fullrecresult.chemicalAspects.records}">
    <table cellpadding="0" cellspacing="0" border="0" class="termsearchtable">
        <thead>
        <tr><th align="center">&nbsp;</th><th align="left">Chemical Aspects</th></tr>
        </thead>
        <tbody>
            <c:forEach items="${fullrecresult.chemicalAspects.records}" var="thesrecord" varStatus="status">
                <c:if test="${not empty thesrecord.recID.mainTerm}">
                <tr>
                    <td align="center" valign="top"><input type="checkbox" value="${thesrecord.recID.mainTerm}" class="addtoclipboard"/></td>
                    <td class="term">
                    <a href="#/search/thes/fullrec.url?snum=${laststep}&CID=thesFullRec&term=${f:encode(thesrecord.recID.mainTerm)}&database=${actionBean.database}" class="fullreclink rolllink">
                        ${thesrecord.recID.mainTerm}
                    </a>
                    </td>
                </tr>
                </c:if>
            </c:forEach>
        </tbody>
    </table>
    </c:if>

    <c:if test="${not empty fullrecresult.useTerms.records}">
    <table>
    <tr><td align="left"><b>Use:&nbsp;</b></td><td>
    <c:forEach items="${fullrecresult.useTerms.records}" var="thesrecord" varStatus="status">
    <c:if test="${not empty thesrecord.recID.mainTerm}">
    <c:if test="${actionBean.database eq '2048' or actionBean.database eq '1024'}" >
    <input type="checkbox"  style="top:-2px" class="addtoclipboard" value="${cm:replaceLower(thesrecord.recID.mainTerm)}"></input>
    </c:if>
    <c:if test="${actionBean.database ne '2048' and actionBean.database ne '2048'}" >
    <input type="checkbox"  style="top:-2px" class="addtoclipboard" value="${thesrecord.recID.mainTerm}"></input>
    </c:if>
    <c:choose>
    <c:when test="${fn:indexOf(thesrecord.recID.mainTerm,'<STRING>')<0}">
      &nbsp;<a href="#/search/thes/fullrec.url?snum=${laststep}&CID=thesFullRec&tid=${f:encode(thesrecord.recID.recordID)}&term=${f:encode(thesrecord.recID.mainTerm)}&amp;database=${actionBean.database}" class="fullreclink rolllink">${thesrecord.recID.mainTerm}</a>
          <c:choose>
            <c:when test="${'AND' eq thesrecord.userTermAndOrFlag and not status.last}"> AND </c:when>
            <c:when test="${'OR' eq thesrecord.userTermAndOrFlag and not status.last}"> OR </c:when>
             <c:otherwise></td></tr><tr><td>&nbsp;</td><td></c:otherwise>
          </c:choose>         
    </c:when>
    <c:otherwise><i>${thesrecord.recID.mainTerm}</i></c:otherwise>
    </c:choose>   
    </c:if>
    </c:forEach>
    </td></tr>
    </table>
    <div class="clear"></div>   
    </c:if>

    <c:if test="${not empty fullrecresult.priorTerms.records}">
    <table cellpadding="0" cellspacing="0" border="0" class="termsearchtable">
        <thead>
        <tr><th align="center">&nbsp;</th><th align="left">Prior Terms</th></tr>
        </thead>
        <tbody>
            <c:forEach items="${fullrecresult.priorTerms.records}" var="thesrecord" varStatus="status">
                <c:if test="${not empty thesrecord.recID.mainTerm}">
                <tr>
                    <td align="center" valign="top"><input type="checkbox" value="${thesrecord.recID.mainTerm}" class="addtoclipboard"/></td>
                    <td class="term">
                    <a href="#/search/thes/fullrec.url?snum=${laststep}&CID=thesFullRec&term=${f:encode(thesrecord.recID.mainTerm)}&database=${actionBean.database}" class="fullreclink rolllink">
                        ${thesrecord.recID.mainTerm}
                    </a>
                    </td>
                </tr>
                </c:if>
            </c:forEach>
        </tbody>
    </table>
    </c:if>


<!-- Hanan Jan 04,2013 -->
    <c:if test="${not empty fullrecresult.topTerms.records}">
    <table cellpadding="0" cellspacing="0" border="0" class="termsearchtable">
    <thead>
    <tr><th align="center">&nbsp;</th><th align="left">Top Terms</th></tr>
    </thead>
    <tbody>
    <c:forEach items="${fullrecresult.topTerms.records}" var="thesrecord" varStatus="status">
    <c:if test="${not empty thesrecord.recID.mainTerm}">
    <tr>
        <td align="center" valign="top"><input type="checkbox" value="${thesrecord.recID.mainTerm}" class="addtoclipboard"/></td>
        <td class="term">
        <a href="#/search/thes/fullrec.url?snum=${laststep}&CID=thesFullRec&tid=${f:encode(thesrecord.recID.recordID)}&term=${f:encode(thesrecord.recID.mainTerm)}&database=${actionBean.database}" class="fullreclink rolllink">
            ${thesrecord.recID.mainTerm}
        </a>
        </td>
    </tr>
    </c:if>
    </c:forEach>
    </tbody>
    </table>
    </c:if>

    <c:if test="${not empty fullrecresult.broaderTerms.records}">
    <table cellpadding="0" cellspacing="0" border="0" class="termsearchtable">
        <thead>
        <tr><th align="center">&nbsp;</th><th align="left">Broader Terms</th></tr>
        </thead>
        <tbody>
        <c:forEach items="${fullrecresult.broaderTerms.records}" var="thesrecord" varStatus="status">
        <c:if test="${not empty thesrecord.recID.mainTerm}">
        <tr>
            <td align="center" valign="top"><input type="checkbox" value="${thesrecord.recID.mainTerm}" class="addtoclipboard"/></td>
            <td class="term">
            <a href="#/search/thes/fullrec.url?snum=${laststep}&CID=thesFullRec&term=${f:encode(thesrecord.recID.mainTerm)}&database=${actionBean.database}" class="fullreclink rolllink">
                ${thesrecord.recID.mainTerm}
            </a>
            </td>
        </tr>
        </c:if>
        </c:forEach>
        </tbody>
    </table>
    </c:if>

    <c:if test="${not empty fullrecresult.relatedTerms.records}">
    <table cellpadding="0" cellspacing="0" border="0" class="termsearchtable">
        <thead>
        <tr><th align="center">&nbsp;</th><th align="left">Related Terms</th></tr>
        </thead>
        <tbody>
        <c:forEach items="${fullrecresult.relatedTerms.records}" var="thesrecord" varStatus="status">
        <c:if test="${not empty thesrecord.recID.mainTerm}">
        <tr>
            <td align="center" valign="top"><input type="checkbox" value="${thesrecord.recID.mainTerm}" class="addtoclipboard"/></td>
            <td class="term">
            <a href="#/search/thes/fullrec.url?snum=${laststep}&CID=thesFullRec&term=${f:encode(thesrecord.recID.mainTerm)}&database=${actionBean.database}" class="fullreclink rolllink">
                ${thesrecord.recID.mainTerm}
            </a>
            </td>
        </tr>
        </c:if>
        </c:forEach>
        </tbody>
    </table>
    </c:if>

    <c:if test="${not empty fullrecresult.narrowerTerms.records}">
    <table cellpadding="0" cellspacing="0" border="0" class="termsearchtable">
        <thead>
        <tr><th align="center">&nbsp;</th><th align="left">Narrower Term</th></tr>
        </thead>
        <tbody>
        <c:forEach items="${fullrecresult.narrowerTerms.records}" var="thesrecord" varStatus="status">
        <c:if test="${not empty thesrecord.recID.mainTerm}">
        <tr>
            <td align="center" valign="top"><input type="checkbox" value="${thesrecord.recID.mainTerm}" class="addtoclipboard"/></td>
            <td class="term">
            <a href="#/search/thes/fullrec.url?snum=${laststep}&CID=thesFullRec&term=${f:encode(thesrecord.recID.mainTerm)}&database=${actionBean.database}" class="fullreclink rolllink">
                ${thesrecord.recID.mainTerm}
            </a>
            </td>
        </tr> 
        </c:if>
        </c:forEach>
        </tbody>
    </table>
    </c:if>

    <c:if test="${not empty fullrecresult.seeAlso.records}">
    <table cellpadding="0" cellspacing="0" border="0" class="termsearchtable">
        <thead>
        <tr><th align="center">&nbsp;</th><th align="left">See also</th></tr>
        </thead>
        <tbody>
            <c:forEach items="${fullrecresult.seeAlso.records}" var="thesrecord" varStatus="status">
                <c:if test="${not empty thesrecord.recID.mainTerm}">
                <tr>
                    <td><input type="checkbox" value="${thesrecord.recID.mainTerm}" class="addtoclipboard"/></td>
                    <td class="term">
                    <a href="#/search/thes/fullrec.url?snum=${laststep}&CID=thesFullRec&term=${f:encode(thesrecord.recID.mainTerm)}&database=${actionBean.database}" class="fullreclink rolllink">${thesrecord.recID.mainTerm}</a>
                    </td>
                </tr>
                </c:if>
            </c:forEach>
        </tbody>
    </table>
    </c:if>

    <c:if test="${not empty fullrecresult.see.records}">
    <table cellpadding="0" cellspacing="0" border="0" class="termsearchtable">
        <thead>
        <tr><th align="center">&nbsp;</th><th align="left">SEE</th></tr>
        </thead>
        <tbody>
            <c:forEach items="${fullrecresult.see.records}" var="thesrecord" varStatus="status">
               <c:if test="${not empty thesrecord.recID.mainTerm}">
                <tr>
                    <td align="center" valign="top"><input type="checkbox" value="${thesrecord.recID.mainTerm}" class="addtoclipboard"/></td>
                    <td class="term">
                    <a href="#/search/thes/fullrec.url?snum=${laststep}&CID=thesFullRec&term=${f:encode(thesrecord.recID.mainTerm)}&database=${actionBean.database}" class="fullreclink rolllink">
                        ${thesrecord.recID.mainTerm}
                    </a>
                    </td>
                </tr>
                </c:if>
            </c:forEach>
        </tbody>
    </table>
    </c:if>
    </div>
<div class="clear"></div>
</td></tr><tr><td>&nbsp;</td></tr>
</c:if>
</c:forEach>

</c:when>

<%-- ******************************* --%>
<%-- Exact Term search with NO HITS! --%>
<%-- ******************************* --%>
<c:when test="${not empty actionBean.termsearchsuggests}">
</table>
<div id="noresults" class="termelement">Your search did not find any match for "${actionBean.term}".  Did you mean?</div>
<div id="noresultslist" class="termelement">
<c:forEach items="${actionBean.termsearchsuggests}" var="thesrecord" varStatus="status">
    <div class="noresult" style="margin-top: 3px">
        <a href="#/search/thes/fullrec.url?snum=${laststep}&CID=thesFullRec&term=${f:encode(thesrecord.recID.mainTerm)}&database=${actionBean.database}" class="fullreclink rolllink"><c:choose><c:when test="${thesrecord.type eq 'LE'}"><i>${thesrecord.recID.mainTerm}</i></c:when><c:otherwise>${thesrecord.recID.mainTerm}</c:otherwise></c:choose></a>
    </div>
</c:forEach>
</div>
</c:when>
</c:choose>

<div class="clear"></div>
</div>
