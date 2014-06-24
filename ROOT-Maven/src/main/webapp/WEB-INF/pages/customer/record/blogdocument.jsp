<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://jawr.net/tags" prefix="jwr" %>   
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld" prefix="stripes"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="/WEB-INF/tlds/datatags.tld" prefix="data" %>

<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp" pageTitle="Engineering Village - Document">

    <stripes:layout-component name="csshead">
    <link href="/static/css/ev_abstract.css?v=${releaseversion}" media="all" type="text/css" rel="stylesheet"></link>
    <!--[if IE 6]>
    <link href="/static/css/ev_ie6.css?v=${releaseversion}" media="all" type="text/css" rel="stylesheet"></link>
    <![endif]-->
    <style type="text/css">
        table#abstractlayout {border: none; margin: 0; padding: 0}
        table#abstractlayout  {display: hidden}
        #abstractarea {float: none; border: none; position: static;}
        
    </style>
    </stripes:layout-component>

    <stripes:layout-component name="header">
        <jsp:include page="/WEB-INF/pages/include/headernull.jsp" />
    </stripes:layout-component>

    <stripes:layout-component name="contents">

    <c:set var="result" value="${actionBean.results[0]}" scope="request"/>
    <span session-id="${actionBean.sessionid}" security="${result.citedby.md5}" an="${result.citedby.an}" doi="${result.citedby.doi}" page="${result.citedby.firstpage}" volume="${result.citedby.firstvolume}" issue="${result.citedby.firstissue}" issn="${result.citedby.issn}" style="display: none;" name="citedbyspan"></span>

    <div id="abstractbox">

<%-- *********************************************************** --%>
<%-- Abstract display --%> 
<%-- *********************************************************** --%>

		<div id="abstractarea" aria-label="Article" role="main">




<c:choose>
<c:when test="${actionBean.database ne 'pag'}">
<table id="abstractlayout">
<tbody>
    <tr>
        <td style="white-space: nowrap; font-size:14px; padding-top:2px; *padding-top:0px">&nbsp;</td>
        <td style="*padding-top:3px" class="abstractContent">
            <h2 style="font-size:16px"><data:highlighttag value="${result.title}" on="${actionBean.ckhighlighting}"/></h2>
            <div class="clear"></div>
            
            <p class="authors" style="margin-top: 6px;">
            <c:if test="${not empty result.authors}">
                <c:if test="${result.doc.dbmask == 2048}"><h3>Inventor(s): </h3></c:if></c:if>          
                <span class="authors">
<c:forEach items="${result.authors}" var="author" varStatus="austatus">
    <c:choose>
    <c:when test="${not empty author.searchlink and not (author.nameupper eq 'ANON')}"><a href="${author.searchlink}" title="Search Author">${author.name}</a><c:if test="${not empty author.affils}"><sup><c:forEach items="${author.affils}" varStatus="afstatus" var="affil"><c:if test="${afstatus.count > 1}">, </c:if><c:if test="${affil.id ne '0'}">${affil.id}</c:if></c:forEach></sup></c:if><c:if test="${not empty author.email}">&nbsp;<a href="mailto:${author.email}" title="Author email" class="emaillink"><img src="/static/images/emailfolder.gif" alt="Email author ${author.email}"/></a></c:if><c:if test="${(austatus.count < fn:length(result.authors))}">; </c:if></c:when>
    <c:otherwise>${author.name}<c:if test="${(austatus.count > 1) and (austatus.count < fn:length(result.authors))}">; </c:if></c:otherwise>
    </c:choose>
</c:forEach>
<c:if test="${not empty result.pf}">(<data:highlighttag value="${result.pf}" on="${actionBean.ckhighlighting}"/>);</c:if>
                </span> 
            </p>
<c:if test="${not empty result.editors}">
<div class="abstractSection editors">
<h3>Editor<c:if test="${fn:length(result.editors) > 1}">s</c:if>: </h3>
<c:forEach items="${result.editors}" var="editor" varStatus="edstatus">
    <a href="${editor.searchlink}"  title="Search Editors">${editor.name}</a><c:if test="${(edstatus.count < fn:length(result.editors))}">; </c:if>
</c:forEach>
</div>
</c:if>     <div class="abstractSection result" >
            <c:if test="${not empty result.assigneelinks}"> <h3>Assignee: </h3><c:forEach items="${result.assigneelinks}" var="link" varStatus="status"><c:if test="${status.count>1}">&nbsp;-&nbsp;</c:if>${link}</c:forEach></c:if>
            <c:if test="${not empty result.patassigneelinks}"> <h3>Patent assignee: </h3><c:forEach items="${result.patassigneelinks}" var="link" varStatus="status"><c:if test="${status.count>1}">&nbsp;-&nbsp;</c:if>${link}</c:forEach></c:if>
            </div>
            <div class="abstractSection result" >
                <data:resultformat result="${result}" name="abstractsourceline" on="${actionBean.ckhighlighting}"/>
            </div>
            <%-- <p class="result" >
                <c:if test="${not empty result.fttj}">${result.fttj}</c:if>
            </p> --%>
            <c:if test="${'Article in Press' eq result.doctype}">
            <p><img src="/static/images/btn_aip.gif" border="0"  title="Articles not published yet, but available online"><span style="vertical-align:text-top"> Article in Press</span>&nbsp;<a href="${actionBean.helpUrl}#Art_in_Press.htm" title="Information about Article in Press" alt="Information about Article in Press" class="helpurl"><img src="/static/images/i.png" border="0" alt=""/></a></p></c:if>
            <div class="clear"></div>

            <c:if test ="${not empty result.affils}">
            <div class="abstractSection affils sectionstart"><h3>Author affiliation<c:if test="${fn:length(result.affils) > 1}">s</c:if>:</h3></div>
            <c:forEach items="${result.affils}" var="affil" varStatus="afstatus">
            <p style="margin:2px 6px 2px 0">
            <c:if test="${affil.id ne '0'}"><sup>${affil.id}</sup></c:if>&nbsp;<data:highlighttag value="${affil.name}" on="${actionBean.ckhighlighting}"/>
            </p>
            </c:forEach>
            </c:if>
            
<c:if test="${not empty result.abstractrecord.text}">
            <div class="abstractSection abstracttext sectionstart"><h3>${result.abstractrecord.label}: </h3></div>
            <p><data:highlighttag value="${result.abstractrecord.text}" on="${actionBean.ckhighlighting}" v2="${result.abstractrecord.v2}"/><c:if test="${result.abstractrecord.refcount > 0}">(${result.abstractrecord.refcount} refs)</c:if></p>
</c:if>
<c:if test="${not empty result.abstractrecord.bookdescription}">
                <!-- ******************************************************** -->
                <!-- BOOK DESCRIPTION! -->
                <!-- ******************************************************** -->
                <div class="abstractSection abstracttext sectionstart"><h3>${result.abstractrecord.label}: </h3></div>
                        <p><data:highlighttag value="${result.abstractrecord.bookdescription}" on="${actionBean.ckhighlighting}" v2="${result.abstractrecord.v2}"/></p>
</c:if>
 <!-- missing IMG -->
 <c:if test="${not empty result.abstractrecord.figures}">
                        <p class="figures"><c:forEach var="figure" items="${result.abstractrecord.figures}"><a href="javascript:window.open('/fig/${figure}','newwind','width=650,height=600,scrollbars=yes,resizable,statusbar=yes');void('');"><img src="/fig/${figure}" alt="" border="1" width="100" height="100"/></a></c:forEach></p>
            </c:if>

 <%-- <c:if test="${not empty result.at}">
            <p><h3>Abstract type: </h3>${result.at}</p>
 </c:if> --%>  
            <%-- ************************************************************************* --%>
            <%-- KeyWords                                                                  --%>
            <%-- ************************************************************************* --%>
 <c:if test="${not empty result.abstractrecord.termmap['BKYS']}">
            <div><h3>Keywords: </h3><c:forEach var="term" items="${result.abstractrecord.termmap['BKYS']}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if><c:choose><c:when test="${not empty term.searchlink}"><data:highlighttag value="${term.searchlink}" on="${actionBean.ckhighlighting}"/></c:when><c:otherwise><data:highlighttag value="${term.value}" on="${actionBean.ckhighlighting}"/></c:otherwise></c:choose></c:forEach></div>
</c:if>   

            <%-- ************************************************************************* --%>
            <%-- Companies                                                                 --%>
            <%-- ************************************************************************* --%>

<c:if test="${not empty result.abstractrecord.termmap['CPO']}">
                <div class="abstractSection controlledterms"><h3>${result.labels['CPO']}: </h3><c:forEach var="term" items="${result.abstractrecord.termmap['CPO']}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if><c:choose><c:when test="${not empty term.searchlink}"><data:highlighttag value="${term.searchlink}" on="${actionBean.ckhighlighting}"/></c:when><c:otherwise><data:highlighttag value="${term.value}" on="${actionBean.ckhighlighting}"/></c:otherwise></c:choose></c:forEach></div>
</c:if>
            <%-- ************************************************************************* --%>
            <%-- Chemicals                                                                 --%>
            <%-- ************************************************************************* --%>

<c:if test="${not empty result.abstractrecord.termmap['CMS']}">
                <div class="abstractSection controlledterms"><h3>${result.labels['CMS']}: </h3><c:forEach var="term" items="${result.abstractrecord.termmap['CMS']}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if><c:choose><c:when test="${not empty term.searchlink}"><data:highlighttag value="${term.searchlink}" on="${actionBean.ckhighlighting}"/></c:when><c:otherwise><data:highlighttag value="${term.value}" on="${actionBean.ckhighlighting}"/></c:otherwise></c:choose></c:forEach></div>
</c:if>

            <%-- ************************************************************************* --%>
            <%-- Major terms                                                               --%>
            <%-- ************************************************************************* --%>
<c:if test="${not empty result.abstractrecord.termmap['MJSM']}">
                <div class="abstractSection controlledterms"><h3>${result.labels['MJSM']}: </h3><c:forEach var="term" items="${result.abstractrecord.termmap['MJSM']}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if><c:choose><c:when test="${not empty term.searchlink}"><data:highlighttag value="${term.searchlink}" on="${actionBean.ckhighlighting}"/></c:when><c:otherwise><data:highlighttag value="${term.value}" on="${actionBean.ckhighlighting}"/></c:otherwise></c:choose></c:forEach></div>
</c:if>

            <%-- ************************************************************************* --%>
            <%-- Main heading terms                                                        --%>
            <%-- ************************************************************************* --%>
<c:if test="${not empty result.abstractrecord.termmap['MH']}">
                <div class="abstractSection mainheading"><h3>${result.labels['MH']}: </h3><c:forEach var="term" items="${result.abstractrecord.termmap['MH']}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if><c:choose><c:when test="${not empty term.searchlink}"><data:highlighttag value="${term.searchlink}" on="${actionBean.ckhighlighting}"/></c:when><c:otherwise><data:highlighttag value="${term.value}" on="${actionBean.ckhighlighting}"/></c:otherwise></c:choose></c:forEach></div>
</c:if>
 
            <%-- ************************************************************************* --%>
            <%-- Controlled vocabulary terms                                               --%>
            <%-- ************************************************************************* --%>
<c:if test="${not empty result.abstractrecord.termmap['CVS']}">
                <div class="abstractSection controlledterms"><h3>${result.labels['CVS']}: </h3><c:forEach var="term" items="${result.abstractrecord.termmap['CVS']}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if><c:choose><c:when test="${not empty term.searchlink}"><data:highlighttag value="${term.searchlink}" on="${actionBean.ckhighlighting}"/></c:when><c:otherwise><data:highlighttag value="${term.value}" on="${actionBean.ckhighlighting}"/></c:otherwise></c:choose></c:forEach></div>
</c:if>  
  
  <!-- Missing CHS  --no code found -->
 
            <%-- ************************************************************************* --%>
            <%-- CAS Registry terms                                                        --%>
            <%-- ************************************************************************* --%>
<c:if test="${not empty result.abstractrecord.termmap['CRM']}">
                <div class="abstractSection controlledterms"><h3>${result.labels['CRM']}: </h3><c:forEach var="term" items="${result.abstractrecord.termmap['CRM']}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if><c:choose><c:when test="${not empty term.searchlink}"><data:highlighttag value="${term.searchlink}" on="${actionBean.ckhighlighting}"/></c:when><c:otherwise><data:highlighttag value="${term.value}" on="${actionBean.ckhighlighting}"/></c:otherwise></c:choose></c:forEach></div>
</c:if>
            <%-- ************************************************************************* --%>
            <%-- Uncontrolled terms                                                        --%>
            <%-- ************************************************************************* --%>
<c:if test="${not empty result.abstractrecord.termmap['FLS']}">
                <div class="abstractSection controlledterms"><h3>${result.labels['FLS']}: </h3><c:forEach var="term" items="${result.abstractrecord.termmap['FLS']}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if><c:choose><c:when test="${not empty term.searchlink}"><data:highlighttag value="${term.searchlink}" on="${actionBean.ckhighlighting}"/></c:when><c:otherwise><data:highlighttag value="${term.value}" on="${actionBean.ckhighlighting}"/></c:otherwise></c:choose></c:forEach></div>
</c:if>

            <%-- ************************************************************************* --%>
            <%-- Classification codes   CLS                                                   --%>
            <%-- ************************************************************************* --%>
            <c:if test="${not empty result.abstractrecord.classificationcodes}">
            <c:forEach var="clcodelist" items="${result.abstractrecord.classificationcodes}">
            <div class="abstractSection classificationcode"><h3>Classification Code: </h3><c:forEach var="clcode" items="${clcodelist.value}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if>${clcode.linkedid}${clcode.title}</c:forEach></p>
            </c:forEach>
            </c:if>

            <%-- ************************************************************************* --%>
            <%-- Regional terms                                                            --%>
            <%-- ************************************************************************* --%>
<c:if test="${not empty result.abstractrecord.termmap['RGIS']}">
                <p class="controlledterms"><h3>${result.labels['RGIS']}: </h3><c:forEach var="term" items="${result.abstractrecord.termmap['RGIS']}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if><c:choose><c:when test="${not empty term.searchlink}"><data:highlighttag value="${term.searchlink}" on="${actionBean.ckhighlighting}"/></c:when><c:otherwise><data:highlighttag value="${term.value}" on="${actionBean.ckhighlighting}"/></c:otherwise></c:choose></c:forEach></div>
</c:if>
        
            <%-- ************************************************************************* --%>
            <%-- IPC Code terms                                                            --%>
            <%-- ************************************************************************* --%>
<c:if test="${not empty result.abstractrecord.termmap['PIDM']}">
                <div class="abstractSection controlledterms"><h3>${result.labels['PIDM']}: </h3><c:forEach var="term" items="${result.abstractrecord.termmap['PIDM']}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if><c:choose><c:when test="${not empty term.searchlink}">${term.searchlink}</c:when><c:otherwise>${term.value}</c:otherwise></c:choose></c:forEach></div>
</c:if>

<c:if test="${not empty result.abstractrecord.termmap['PIDEPM']}">
                <div class="abstractSection controlledterms"><h3>${result.labels['PIDEPM']}: </h3><c:forEach var="term" items="${result.abstractrecord.termmap['PIDEPM']}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if><c:choose><c:when test="${not empty term.searchlink}">${term.searchlink}</c:when><c:otherwise>${term.value}</c:otherwise></c:choose></c:forEach></div>
</c:if>

            
<c:if test="${not empty result.abstractrecord.termmap['PIDM8']}">
                <div class="abstractSection controlledterms"><h3>${result.labels['PIDM8']}: </h3><c:forEach var="term" items="${result.abstractrecord.termmap['PIDM8']}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if><c:choose><c:when test="${not empty term.searchlink}">${term.searchlink}</c:when><c:otherwise>${term.value}</c:otherwise></c:choose></c:forEach></div>
</c:if>

<c:if test="${not empty result.abstractrecord.termmap['PUCM']}">
                <div class="abstractSection controlledterms"><h3>${result.labels['PUCM']}: </h3><c:forEach var="term" items="${result.abstractrecord.termmap['PUCM']}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if><c:choose><c:when test="${not empty term.searchlink}">${term.searchlink}</c:when><c:otherwise>${term.value}</c:otherwise></c:choose></c:forEach></div>
</c:if>
<c:if test="${not empty result.abstractrecord.termmap['PECM']}">
                <div class="abstractSection controlledterms"><h3>${result.labels['PECM']}: </h3><c:forEach var="term" items="${result.abstractrecord.termmap['PECM']}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if><c:choose><c:when test="${not empty term.searchlink}">${term.searchlink}</c:when><c:otherwise>${term.value}</c:otherwise></c:choose></c:forEach></div>
</c:if>
 <c:if test="${not empty result.collection}">
                <div><h3>Collection name:</h3>${result.collection}</p>
</c:if>
            <%-- ************************************************************************* --%>
            <%-- Other info                                                                --%>
            <%-- ************************************************************************* --%>
<c:if test="${not empty result.abstractrecord.termmap['LOCS']}">    <%-- Coordinates --%>
            <div class="abstractSection coordinates"><h3>${result.labels['LOCS']}: </h3><c:forEach var="term" items="${result.abstractrecord.termmap['LOCS']}" varStatus="status"><c:if test="${status.count > 1}">;&nbsp;</c:if>${term.value}</c:forEach></div>
</c:if>
            <%-- ************************************************************************* --%>
            <%-- Treatments                                                                --%>
            <%-- ************************************************************************* --%>
<c:if test="${not empty result.abstractrecord.treatments}">
                <div class="abstractSection treatments"><h3>${result.labels['TRS']}: </h3><c:forEach var="treatment" items="${result.abstractrecord.treatments}" varStatus="status"><c:if test="${status.count > 1}">&nbsp;-&nbsp;</c:if>${treatment}</c:forEach></div>
</c:if>
            
            <div class="abstractSection database"><h3>Database: </h3>${result.doc.dbname}</div>
            <c:if test="${result.doc.dbmask == 2097152}">
                <p>${result.cpr}</p>
            </c:if>
        </td>
    </tr>
</tbody>
</table>
</c:when>
<c:otherwise>
<table border="0" width="100%" id="detailed" style=" margin-bottom: 10px;min-width:490">
        <tr>
            <td valign="top" align="left" width="1%">
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

                </table>
                <!-- END Book Detail Document -->
            </td>
          </tr>
    </table>
</c:otherwise>
</c:choose>




			<div class="clear"></div>
		</div>

	</div>

    </stripes:layout-component>

    <stripes:layout-component name="jsbottom_custom"> 
    </stripes:layout-component>

    
</stripes:layout-render>
