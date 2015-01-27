<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
 <%@ taglib uri="http://jawr.net/tags" prefix="jwr" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld" prefix="stripes" %>

<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp" pageTitle="Engineering Village - Expert Search">

    <stripes:layout-component name="csshead">
    <jwr:style src="/bundles/expert.css"></jwr:style>
  <!--[if IE 6]>
    <link href="/static/css/ev_ie6.css?v=${releaseversion}" media="all" type="text/css" rel="stylesheet"></link>
<![endif]-->
<!--[if IE]>
    <style type="text/css">
    #searchformwrap { margin-top: 15px; }
    #searchformsidebar { margin-top: 29px; }
    </style>
<![endif]-->
    </stripes:layout-component>

<%-- **************************************** --%>
<%-- CONTENTS                                 --%>
<%-- **************************************** --%>
<stripes:layout-component name="SkipToNavigation">
	<a class="skiptonavlink" href="#searchtablink" onclick="$('#searchtablink').focus();return false;" title="Skip to Quick Search Tab">Skip to Quick Search Tab</a><br/>
	<a class="skiptonavlink" href="#srchWrd1" onclick="$('#srchWrd1').focus();return false;" title="Skip to Expert Search Form">Skip to Expert Search Form</a><br/>
</stripes:layout-component>
    <stripes:layout-component name="contents">

    <div id="container">
    <div id="searchformwrap">
    <div id="searchformbox">

        <stripes:errors field="validationError"><div id="errormessage"><stripes:individual-error/></div></stripes:errors>

        <c:set var="searchtab" value="expertsearch" scope="request"></c:set>
        <jsp:include page="parts/searchtabs.jsp"></jsp:include>

        <div id="searchcontents" class="shadowbox" role="search" aria-labeledby="Expert Search"> <!-- gray box -->
            <div id="searchtipsbox">
                <ul>
                    <li class="databases"><a href="/databases.jsp?dbid=<c:forEach items="${actionBean.databaseCheckboxes}" var="checkbox" varStatus="status"><c:if test="${status.count>1}">,</c:if>${checkbox.id}</c:forEach>" id="databaseTipsLink" title="Learn more about databases" target="_blank" class="evdialog">Databases</a></li>
                    <li><a href="/searchtips.jsp?topic=expert" id="searchTipsLink" title="Search tips to help" target="_blank" class="notfirst evdialog">Search tips</a></li>
                </ul>
            </div>

            <div id="searchform">

            <stripes:form onsubmit="return searchValidation();" method="POST" action="/search/submit.url" name="quicksearch" onreset="javascript:resetSearchForm('expertSearch')">
            <input type="hidden" name="CID" value="searchSubmit"/>
            <input type="hidden" name="resetDataBase" value="${actionBean.database}"/>
            <input type="hidden" name="searchtype" value="Expert"/>

            <%-- ******************************************************** --%>
            <%-- ******************************************************** --%>
            <%-- DATABASE SELECTION                                       --%>
            <%-- ******************************************************** --%>
            <%-- ******************************************************** --%>
            <jsp:include page="parts/database.jsp"></jsp:include>

            <div class="clear"></div>

            <%-- ******************************************************** --%>
            <%-- ******************************************************** --%>
            <%-- SEARCH TERMS                                             --%>
            <%-- ******************************************************** --%>
            <%-- ******************************************************** --%>

            <div class="searchcomponentfullwrap" style="padding-bottom:0;">
                <h2 class="searchcomponentlabel">SEARCH FOR</h2>
                <div class="searchcomponenthelpicon"><a href="${actionBean.helpUrl}#Expert_search_steps.htm"   title="Learn how to do an expert search" class="helpurl" ><jwr:img
                        src="/static/images/i.png" border="0" styleClass="infoimg" align="top" alt="Learn how to do an expert search"/></a></div>
                <div class="searchforline">
                	<label class="hidden" for="srchWrd1">Search Query</label>
                    <textarea onblur="updateWinds()" name="searchWord1" wrap="soft" id="srchWrd1" style="height:4em;width:560px; resize:none;overflow:auto" title="Search Query Text Box">${actionBean.searchWord1}</textarea>
                 </div>

                <div class="searchforline" style="height: 27px; *width:100%; margin: 8px 52px 5px 0">
                    <stripes:submit name="search" value="Search" style="float:right;" class="button" title="Submit Search"/>&nbsp;
                </div>
            </div>

            <div class="searchcomponentseparator" style="padding:0; *margin-bottom:0"><hr /></div>

            <%-- ******************************************************** --%>
            <%-- ******************************************************** --%>
            <%-- LIMIT BY and SORT BY                                     --%>
            <%-- ******************************************************** --%>
            <%-- ******************************************************** --%>
            <div class="clear"></div>
            <div id="limitby" style="min-width:679px;">
                <jsp:include page="parts/limits.jsp"></jsp:include>
                <div class="clear"></div>
            </div>

            <div class="searchcomponentshortwrap" style="position: relative;">
                <div style="float: right;margin-bottom:7px">
                    <stripes:submit name="search" class="button" value="Search" title="Submit Search"/>&nbsp;
                    <stripes:reset name="reset" class="button" value="Reset" title="Reset search"/>&nbsp;
                </div>
                <div class="clear"></div>
            </div>


            <%-- ******************************************************** --%>
            <%-- ******************************************************** --%>
            <%-- SEARCH CODES                                             --%>
            <%-- ******************************************************** --%>
            <%-- ******************************************************** --%>
            <div class="searchcomponentfullwrap">
            <div><h2 class="searchcomponentlabel" style="float:left; margin-bottom: 2px">SEARCH CODES
            </h2>
            <a href="${actionBean.helpUrl}#Expert_Search_Fields_and_Fields_Codes.htm"  title="Learn more about search fields and field codes" class="helpurl"><jwr:img
                        src="/static/images/i.png" border="0" styleClass="infoimg" align="bottom" alt="Learn more about search fields and field codes"/></a>
            </div>
            <ul class="searchcodetitle" >
                <li><label>c</label>&nbsp;=&nbsp;Compendex,</li>
                <li><label>i</label>&nbsp;=&nbsp; Inspec,</li>
                <li><label>n</label>&nbsp;=&nbsp;NTIS,</li>
                <li><label>pc</label>&nbsp;=&nbsp;PaperChem,</li>
                <li><label>cm</label>&nbsp;=&nbsp;Chimica,</li>
                <li><label>cb</label>&nbsp;=&nbsp;CBNB,</li>
                <li><label>el</label>&nbsp;=&nbsp;EnCompassLIT,</li>
                <li><label>ep</label>&nbsp;=&nbsp;EnCompassPAT,</li>
                <li><label>g</label>&nbsp;=&nbsp;GEOBASE,</li>
                <li><label>f</label>&nbsp;=&nbsp;GeoRef,</li>
                <li><label>u</label>&nbsp;=&nbsp;US Patents,</li>
                <li><label>e</label>&nbsp;=&nbsp;EP Patents,</li>
            </ul>
            <div class="clear"></div>

            <div id="searchcodes">
            <table cellspacing="0" cellpadding="0" border="0">
                <tbody>
                    <tr>
                        <th scope="col" style="float: left;"><span class="MedBlackText"><b><u>Field</u></b></span></th>
                        <th `scope="col">&nbsp;</th>
                        <th scope="col" style="float: left;"><span class="MedBlackText"><b><u>Code</u></b></span></th>
                    </tr>

                    <tr>
                        <td valign="top" height="1"><span class="SmBlackText">Abstract</span><span class="SmGreenText">&nbsp;(c, i, n, pc, cm, cb, el, ep, g, f, u, e)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">AB</span></td>
                    </tr>
                    <tr class="even">
                        <td valign="top" height="1"><span class="SmBlackText">Accession number</span><span class="SmGreenText">&nbsp;(c, i, n, pc, el, ep, g, f)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">AN</span></td>
                    </tr>
                    <tr>
                        <td valign="top" height="1"><span class="SmBlackText">Affiliation/Assignee</span><span class="SmGreenText">&nbsp;(c, i, n, pc, cm, el, ep, g, f, u, e)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">AF</span></td>
                    </tr>
                    <tr class="even">
                        <td valign="top" height="1"><span class="SmBlackText">All fields</span><span class="SmGreenText">&nbsp;(c, i, n, pc, cm, cb, el, g, f, u, e)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">ALL</span></td>
                    </tr>
                    <tr>
                        <td valign="top" height="1"><span class="SmBlackText">Astronomical indexing</span><span class="SmGreenText">&nbsp;(i)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">AI</span></td>
                    </tr>
                    <tr class="even">
                        <td valign="top" height="1"><span class="SmBlackText">Author/Inventor</span><span class="SmGreenText">&nbsp;(c, i, n, pc, el, ep, g, f, u, e)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">AU</span></td>
                    </tr>
                    <tr>
                        <td valign="top" height="1"><span class="SmBlackText">Availability</span><span class="SmGreenText">&nbsp;(n, cb, f)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">AV</span></td>
                    </tr>
                    <tr class="even">
                        <td valign="top" height="1"><span class="SmBlackText">CAS registry number</span><span class="SmGreenText">&nbsp;(cm, cb, el, ep)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">CR</span></td>
                    </tr>
                    <tr>
                        <td valign="top" height="1"><span class="SmBlackText">Chemical Acronyms</span><span class="SmGreenText">&nbsp;(cb)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">CE</span></td>
                    </tr>
                    <tr class="even">
                        <td valign="top" height="1"><span class="SmBlackText">Chemical indexing</span><span class="SmGreenText">&nbsp;(i)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">CI</span></td>
                    </tr>
                    <tr>
                        <td valign="top" height="1"><span class="SmBlackText">Chemicals</span><span class="SmGreenText">&nbsp;(cb)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">CM</span></td>
                    </tr>
                    <tr class="even">
                        <td valign="top" height="1"><span class="SmBlackText">Classification code</span><span class="SmGreenText">&nbsp;(c, i, cm, el, ep, g, f)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">CL</span></td>
                    </tr>
                    <tr>
                        <td valign="top" height="1"><span class="SmBlackText">CODEN</span><span class="SmGreenText">&nbsp;(c, i, pc, cm, cb, f)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">CN</span></td>
                    </tr>
                    <tr class="even">
                        <td valign="top" height="1"><span class="SmBlackText">Companies</span><span class="SmGreenText">&nbsp;(pc, cb)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">CP</span></td>
                    </tr>
                    <tr>
                        <td valign="top" height="1"><span class="SmBlackText">Conference code</span><span class="SmGreenText">&nbsp;(c, pc, el, f)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">CC</span></td>
                    </tr>
                    <tr class="even">
                        <td valign="top" height="1"><span class="SmBlackText">Conference information</span><span class="SmGreenText">&nbsp;(c, i, el, f)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">CF</span></td>
                    </tr>
                    <tr>
                        <td valign="top" height="1"><span class="SmBlackText">Contract number</span><span class="SmGreenText">&nbsp;(n)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">CT</span></td>
                    </tr>
                    <tr class="even">
                        <td valign="top" height="1"><span class="SmBlackText">Controlled term</span><span class="SmGreenText">&nbsp;(c, i, n, pc, cm, cb, el, ep, g, f)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">CV</span></td>
                    </tr>
                    <tr>
                        <td valign="top" height="1"><span class="SmBlackText">Controlled term as a product</span><span class="SmGreenText">&nbsp;(el, ep)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">CVP</span></td>
                    </tr>
                    <tr class="even">
                        <td valign="top" height="1"><span class="SmBlackText">Controlled term as a reagent</span><span class="SmGreenText">&nbsp;(el, ep)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">CVA</span></td>
                    </tr>
                    <tr>
                        <td valign="top" height="1"><span class="SmBlackText">Controlled term with no role</span><span class="SmGreenText">&nbsp;(el, ep)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">CVN</span></td>
                    </tr>
                    <tr class="even">
                        <td valign="top" height="1"><span class="SmBlackText">Country of application</span><span class="SmGreenText">&nbsp;(c, pc)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">PU</span></td>
                    </tr>
                    <tr>
                        <td valign="top" height="1"><span class="SmBlackText">Country of origin </span><span class="SmGreenText">&nbsp;(c, i, n, cm, cb, el, g, f, u, e)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">CO</span></td>
                    </tr>
                    <tr class="even">
                        <td valign="top" height="1"><span class="SmBlackText">Derwent accession number</span><span class="SmGreenText">&nbsp;(ep)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">AJ</span></td>
                    </tr>
                    <tr>
                        <td valign="top" height="1"><span class="SmBlackText">Derwent
                        IPC</span><span class="SmGreenText">&nbsp;(ep)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">DPID</span></td>
                    </tr>
                    <tr class="even">
                        <td valign="top" height="1"><span class="SmBlackText">Discipline</span><span class="SmGreenText">&nbsp;(i)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">DI</span></td>
                    </tr>
                    <tr>
                        <td valign="top" height="1"><span class="SmBlackText">DOI</span><span class="SmGreenText">&nbsp;(c, i,  pc, cm, el, g)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">DOI</span></td>
                    </tr>
                    <tr class="even">
                        <td valign="top" height="1"><span class="SmBlackText">Document type</span><span class="SmGreenText">&nbsp;(c, i, n, pc, cm, cb, el, g, f)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">DT</span></td>
                    </tr>
                    <tr>
                        <td valign="top" height="1"><span class="SmBlackText">ECLA code</span><span class="SmGreenText">&nbsp;(e)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">PEC</span></td>
                    </tr>
                    <tr class="even">
                        <td valign="top" height="1"><span class="SmBlackText">Ei main heading</span><span class="SmGreenText">&nbsp;(c)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">MH</span></td>
                    </tr>
                    <tr>
                        <td valign="top" height="1"><span class="SmBlackText">Industrial Sectors</span><span class="SmGreenText">&nbsp;(cb)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">GD</span></td>
                    </tr>
                    <tr class="even">
                        <td valign="top" height="1"><span class="SmBlackText">Int.patent classification</span><span class="SmGreenText">&nbsp;(ep, u, e)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">PID</span></td>
                    </tr>
                    <tr>
                        <td valign="top" height="1"><span class="SmBlackText">ISBN</span><span class="SmGreenText">&nbsp;(c, i, pc, cb, el, g, f)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">BN</span></td>
                    </tr>
                    <tr class="even">
                        <td valign="top" height="1"><span class="SmBlackText">ISSN</span><span class="SmGreenText">&nbsp;(c, i, pc, cm, cb, el, g, f)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">SN</span></td>
                    </tr>
                    <tr>
                        <td valign="top" height="1"><span class="SmBlackText">Issue</span><span class="SmGreenText">&nbsp;(c, i, pc, cm, el, g, f)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">SU</span></td>
                    </tr>
                    <tr class="even">
                        <td valign="top" height="1"><span class="SmBlackText">Language</span><span class="SmGreenText">&nbsp;(c, i, n, pc, cm, cb, el, ep, g, f)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">LA</span></td>
                    </tr>
                    <tr>
                        <td valign="top" height="1"><span class="SmBlackText">Linked term</span><span class="SmGreenText">&nbsp;(el, ep)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">LT</span></td>
                    </tr>
                    <tr class="even">
                        <td valign="top" height="1"><span class="SmBlackText">Major term</span><span class="SmGreenText">&nbsp;(el, ep)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">CVM</span></td>
                    </tr>
                </tbody>
            </table>

            <table cellspacing="0" cellpadding="0"  border="0" style="margin-left:35px">
                <tbody>
                    <tr>
                        <th scope="col" style="float: left;"><span class="MedBlackText"><b><u>Field</u></b></span></th>
                        <th scope="col">&nbsp;&nbsp;&nbsp;</th>
                        <th scope="col" style="float: left;"><span class="MedBlackText"><b><u>Code</u></b></span></th>
                    </tr>
                    <tr>
                        <td valign="top" height="1"><span class="SmBlackText">Major term as a product</span><span class="SmGreenText">&nbsp;(el, ep)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">CVMP</span></td>
                    </tr>
                    <tr class="even">
                        <td valign="top" height="1"><span class="SmBlackText">Major term as a reagent</span><span class="SmGreenText">&nbsp;(el, ep)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">CVMA</span></td>
                    </tr>
                    <tr>
                        <td valign="top" height="1"><span class="SmBlackText">Major term with no role</span><span class="SmGreenText">&nbsp;(el, ep)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">CVMN</span></td>
                    </tr>
                    <tr class="even">
                        <td valign="top" height="1"><span class="SmBlackText">Material identity number</span><span class="SmGreenText">&nbsp;(i)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">MI</span></td>
                    </tr>
                    <tr>
                        <td valign="top" height="1"><span class="SmBlackText">Monitoring agency</span><span class="SmGreenText">&nbsp;(n)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">AG</span></td>
                    </tr>
                    <tr class="even">
                        <td valign="top" height="1"><span class="SmBlackText">Notes</span><span class="SmGreenText">&nbsp;(n)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">NT</span></td>
                    </tr>
                    <tr>
                        <td valign="top" height="1"><span class="SmBlackText">Numerical indexing</span><span class="SmGreenText">&nbsp;(i)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">NI</span></td>
                    </tr>
                    <tr class="even">
                        <td valign="top" height="1"><span class="SmBlackText">Original classification code</span><span class="SmGreenText">&nbsp;(i)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">OC</span></td>
                    </tr>
                    <tr>
                        <td valign="top" height="1"><span class="SmBlackText">Patent application country</span><span class="SmGreenText">&nbsp;(ep, u, e)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">PCO</span></td>
                    </tr>
                    <tr class="even">
                        <td valign="top" height="1"><span class="SmBlackText">Patent application date</span><span class="SmGreenText">&nbsp;(c, n, pc, ep, u, e)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">PA</span></td>
                    </tr>
                    <tr>
                        <td valign="top" height="1"><span class="SmBlackText">Patent application number</span><span class="SmGreenText">&nbsp;(ep, u, e)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">PAM</span></td>
                    </tr>
                    <tr class="even">
                        <td valign="top" height="1"><span class="SmBlackText">Patent attorney name</span><span class="SmGreenText">&nbsp;(u, e)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">PAN</span></td>
                    </tr>
                    <tr>
                        <td valign="top" height="1"><span class="SmBlackText">Patent authority code</span><span class="SmGreenText">&nbsp;(ep, u, e)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">PAC</span></td>
                    </tr>
                    <tr class="even">
                        <td valign="top" height="1"><span class="SmBlackText">Patent citation</span><span class="SmGreenText">&nbsp;(u,  e)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">PCI</span></td>
                    </tr>
                    <tr>
                        <td valign="top" height="1"><span class="SmBlackText">Patent Country</span><span class="SmGreenText">&nbsp;(ep)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">PC</span></td>
                    </tr>
                    <tr class="even">
                        <td valign="top" height="1"><span class="SmBlackText">Patent examiner</span><span class="SmGreenText">&nbsp;(u, e)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">PE</span></td>
                    </tr>
                    <tr>
                        <td valign="top" height="1"><span class="SmBlackText">Patent filing date</span><span class="SmGreenText">&nbsp;(u, e)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">PFD</span></td>
                    </tr>
                    <tr class="even">
                        <td valign="top" height="1"><span class="SmBlackText">Patent issue date</span><span class="SmGreenText">&nbsp;(c, n, pc, u, e)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">PI</span></td>
                    </tr>
                    <tr>
                        <td valign="top" height="1"><span class="SmBlackText">Patent number</span><span class="SmGreenText">&nbsp;(c, pc, ep, u, e)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">PM</span></td>
                    </tr>
                    <tr class="even">
                        <td valign="top" height="1"><span class="SmBlackText">Patent priority information</span><span class="SmGreenText">&nbsp;(ep, u, e)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">PRN</span></td>
                    </tr>
                    <tr>
                        <td valign="top" height="1"><span class="SmBlackText">Priority country</span><span class="SmGreenText">&nbsp;(ep)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">PRC</span></td>
                    </tr>
                    <tr class="even">
                        <td valign="top" height="1"><span class="SmBlackText">Priority date</span><span class="SmGreenText">&nbsp;(ep)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">PRD</span></td>
                    </tr>
                    <tr>
                        <td valign="top" height="1"><span class="SmBlackText">Publication date</span><span class="SmGreenText">&nbsp;(u, e)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">PD</span></td>
                    </tr>
                    <tr class="even">
                        <td valign="top" height="1"><span class="SmBlackText">Publication year</span><span class="SmGreenText">&nbsp;(el, f)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">YR</span></td>
                    </tr>
                    <tr>
                        <td valign="top" height="1"><span class="SmBlackText">Publisher</span><span class="SmGreenText">&nbsp;(c, i, pc, cm, el, g, f)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">PN</span></td>
                    </tr>
                    <tr class="even">
                        <td valign="top" height="1"><span class="SmBlackText">Regional terms</span><span class="SmGreenText">&nbsp;(g)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">RGI</span></td>
                    </tr>
                    <tr>
                        <td valign="top" height="1"><span class="SmBlackText">Report number</span><span class="SmGreenText">&nbsp;(n,  f)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">RN</span></td>
                    </tr>
                    <tr class="even">
                        <td valign="top" height="1"><span class="SmBlackText">Role</span><span class="SmGreenText">&nbsp;(el, ep)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">RO</span></td>
                    </tr>
                    <tr>
                        <td valign="top" height="1"><span class="SmBlackText">SIC Codes</span><span class="SmGreenText">&nbsp;(cb)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">IC</span></td>
                    </tr>
                    <tr class="even">
                        <td valign="top" height="1"><span class="SmBlackText">Source</span><span class="SmGreenText">&nbsp;(el)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">SO</span></td>
                    </tr>
                    <tr>
                        <td valign="top" height="1"><span class="SmBlackText">Source title</span><span class="SmGreenText">&nbsp;(c, i, pc, cm, cb, el, g, f)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">ST</span></td>
                    </tr>
                    <tr class="even">
                        <td valign="top" height="1"><span class="SmBlackText">Subject/Title/Abstract</span><span class="SmGreenText">&nbsp;(c, i, n, pc, cm, cb, el, ep, g, f,<br>u, e)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">KY</span></td>
                    </tr>
                    <tr>
                        <td valign="top" height="1"><span class="SmBlackText">Title</span><span class="SmGreenText">&nbsp;(c, i, n, pc, cm, cb, el, ep, g, f, u, e)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">TI</span></td>
                    </tr>
                    <tr class="even">
                        <td valign="top" height="1"><span class="SmBlackText">Treatment type</span><span class="SmGreenText">&nbsp;(c, i, pc)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">TR</span></td>
                    </tr>
                    <tr>
                        <td valign="top" height="1"><span class="SmBlackText">Uncontrolled term</span><span class="SmGreenText">&nbsp;(c, i, n, pc, g, f)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">FL</span></td>
                    </tr>
                    <tr class="even">
                        <td valign="top" height="1"><span class="SmBlackText">US classification</span><span class="SmGreenText">&nbsp;(u)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">PUC</span></td>
                    </tr>
                    <tr>
                        <td valign="top" height="1"><span class="SmBlackText">Volume</span><span class="SmGreenText">&nbsp;(c, i, pc, cm, el, g, f)</span></td>
                        <td valign="top">&nbsp;&nbsp;&nbsp;</td>
                        <td valign="top"><span class="SmBlackText">VO</span></td>
                    </tr>
                </tbody>
            </table>
            </div>

            </div>

            </stripes:form>

        </div> <!-- END searchform -->

        </div>  <!-- END searchcontents -->
        <br class="clear"/>
        </div> <!-- END searchformbox -->

        <jsp:include page="parts/history.jsp"></jsp:include>

        </div> <!-- END searchformwrap -->

        <%-- ******************************************************** --%>
        <%-- ******************************************************** --%>
        <%-- SIDEBAR ITEMS                                            --%>
        <%-- ******************************************************** --%>
        <%-- ******************************************************** --%>
        <div style="float:right">
        <jsp:include page="parts/sidebar.jsp"></jsp:include>
        </div>

        </div>  <!-- END container -->

        <div class="clear"></div>


    </stripes:layout-component>

    <stripes:layout-component name="jsbottom_custom">
    <jwr:script src="/bundles/expert.js"></jwr:script>
    <script type="text/javascript">
    $(document).ready(function() {
    flipImage(${actionBean.database});
    });
    </script>

    <jsp:include page="parts/search_common_js.jsp"></jsp:include>


    </stripes:layout-component>


</stripes:layout-render>
