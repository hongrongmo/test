<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

<%@page import="java.io.File"%>
<c:set var="searchtype">
<c:choose><c:when test="${'expert' eq actionBean.context.eventName}">Expert</c:when><c:otherwise>Quick</c:otherwise></c:choose>
</c:set>
<div id="searchformsidebar" aria-label="Related content" role="complementary">
<c:if test="${'quick' eq actionBean.context.eventName or 'expert' eq actionBean.context.eventName}">
    <div id="browseindexes" class="sidebarbox" title="Browse Indexes to add search terms to your query">
    <div class="moresearchSourcediv">
                <h2 class="moresearchSourceh2">Browse Indexes</h2>
                <div class="paddingT5">
                    <a href="${actionBean.helpUrl}#Browse_Indexes.htm" title="" class="helpurl" style="float:right" alt="Learn more about Browse Index" alt="Learn more about Browse Index"><img class="i" src="/static/images/i.png" alt="" border="0"/></a>
                </div>
            </div>
        <div id="lookups" class="padding5">
                <ul id="browseindexesul">
                    <li id="bi_aus" class="browseindexli"><a class="browseindexlookup" target="LookupWin" lookupindex="AUS" href="/search/browseindex.url?database=${actionBean.database}&lookup=AUS&searchtype=${searchtype}">Author</a></li>
                    <li id="bi_ausinv" class="browseindexli"><a class="browseindexlookup" target="LookupWin" lookupindex="AUS" href="/search/browseindex.url?database=${actionBean.database}&lookup=AUS&searchtype=${searchtype}">Author/Inventor</a></li>
                    <li id="bi_inv" class="browseindexli"><a class="browseindexlookup" target="LookupWin" lookupindex="AUS" href="/search/browseindex.url?database=${actionBean.database}&lookup=AUS&searchtype=${searchtype}">Inventor</a></li>
                    <li id="bi_af" class="browseindexli"><a class="browseindexlookup" target="LookupWin" lookupindex="AF" href="/search/browseindex.url?database=${actionBean.database}&lookup=AF&searchtype=${searchtype}">Author affiliation</a></li>
                    <li id="bi_asg" class="browseindexli"><a class="browseindexlookup" target="LookupWin" lookupindex="AF" href="/search/browseindex.url?database=${actionBean.database}&lookup=AF&searchtype=${searchtype}">Assignee</a></li>
                    <li id="bi_afasg" class="browseindexli"><a class="browseindexlookup" target="LookupWin" lookupindex="AF" href="/search/browseindex.url?database=${actionBean.database}&lookup=AF&searchtype=${searchtype}">Affiliation/Assignee</a></li>
                    <li id="bi_cvs" class="browseindexli"><a class="browseindexlookup" target="LookupWin" lookupindex="CVS" href="/search/browseindex.url?database=${actionBean.database}&lookup=CVS&searchtype=${searchtype}">Controlled term</a></li>
                    <li id="bi_la" class="browseindexli"><a class="browseindexlookup" target="LookupWin" lookupindex="LA" href="/search/browseindex.url?database=${actionBean.database}&lookup=LA&searchtype=${searchtype}">Language</a></li>
                    <li id="bi_st" class="browseindexli"><a class="browseindexlookup" target="LookupWin" lookupindex="ST" href="/search/browseindex.url?database=${actionBean.database}&lookup=ST&searchtype=${searchtype}">Source title</a></li>
                    <li id="bi_dt" class="browseindexli"><a class="browseindexlookup" target="LookupWin" lookupindex="DT" href="/search/browseindex.url?database=${actionBean.database}&lookup=DT&searchtype=${searchtype}">Document type</a></li>
                    <li id="bi_pn" class="browseindexli"><a class="browseindexlookup" target="LookupWin" lookupindex="PN" href="/search/browseindex.url?database=${actionBean.database}&lookup=PN&searchtype=${searchtype}">Publisher</a></li>
                    <li id="bi_tr" class="browseindexli"><a class="browseindexlookup" target="LookupWin" lookupindex="TR" href="/search/browseindex.url?database=${actionBean.database}&lookup=TR&searchtype=${searchtype}">Treatment type</a></li>
                    <li id="bi_pc" class="browseindexli"><a class="browseindexlookup" target="LookupWin" lookupindex="PC" href="/search/browseindex.url?database=${actionBean.database}&lookup=PR&searchtype=${searchtype}">Country</a></li>
                    <li id="bi_di" class="browseindexli"><a class="browseindexlookup" target="LookupWin" lookupindex="DI" href="/search/browseindex.url?database=${actionBean.database}&lookup=DI&searchtype=${searchtype}">Discipline</a></li>
                    <li id="bi_pid" class="browseindexli"><a class="browseindexlookup" target="LookupWin" lookupindex="PID" href="/search/browseindex.url?database=${actionBean.database}&lookup=PID&searchtype=${searchtype}">IPC Code</a></li>
                </ul>
            </div>
    </div>
</c:if>

<c:set value="${actionBean.context.userSession.userTextZones}" var="textzones"/>
<c:if test="${not empty textzones and ((not empty textzones['OPTION1']) or (not empty textzones['OPTION2']) or (not empty textzones['OPTION3']) or (not empty textzones['OPTION4']) or (not empty textzones['OPTION5']))}">
    <div id="latestresources" class="sidebarbox" title="Latest Resources">
        <h2 class="sidebartitle">Latest Resources</h2>
        <!-- <div class="padding5">As of April 1, eBook Search (Referex database) is only available on ScienceDirect. <a href="http://info.sciencedirect.com/referex" title="Learn more about Referex">Learn more.</a></div> -->
        <div class="padding5">
        <ul id="latestresourceslist">
        <c:if test="${not empty textzones['OPTION1']}">
            ${textzones['OPTION1']}
        </c:if>
        <c:if test="${not empty textzones['OPTION2']}">
            ${textzones['OPTION2']}
        </c:if>
        <c:if test="${not empty textzones['OPTION3']}">
            ${textzones['OPTION3']}
        </c:if>
        <c:if test="${not empty textzones['OPTION4']}">
            ${textzones['OPTION4']}
        </c:if>
        <c:if test="${not empty textzones['OPTION5']}">
            ${textzones['OPTION5']}
        </c:if>
        </ul>
        </div>
    </div>
</c:if>

<c:if test="${not empty actionBean.moresourceslinks}">
    <div  class="sidebarbox">
        <h2 class="sidebartitle">
            <a id="searchsourcestoggle" class="historyplus" title="Show more search sources">Show</a>
        More Sources<a href="${actionBean.helpUrl}#More_search_sources.htm" style="padding-right:11px;" alt="Learn more about additional search sources" title="Learn more about additional search sources" class="helpurl"><img class="i" src="/static/images/i.png" alt="" border="0"/></a></span> </h2>

        <div class="padding5" id="searchSourcesLinks" style="display:none;">
        <ul id="mosrchsourcesidebarul">
            <c:forEach items="${actionBean.moresourceslinks}" var="moresources" varStatus="msstatus">
                <li class="mosrchsourcesidebarli">${moresources}</li>
            </c:forEach>
        </ul>
        </div>
    </div>
 </c:if>
	<div  class="sidebarbox" id="knovelbox">
        <div class="knoveltitle" title="Interactive Tools">
        <div>Interactive Equations <a title="Learn more about" class="helpurl" style="padding:5px;padding-left:15px;" onclick="helpurlClick(this);return false;"class="helpurl" href="${actionBean.helpUrl}#knovel.htm"><img class="i" src="/static/images/i.png" alt="" border="0"/></a><br/>and Tools&nbsp;<span class="smallTxt">Powered by <a href="http://www.knovel.com" title="Knovel" target="new">Knovel &reg;</a></span></div>
        </div>

        <div class="padding5" >
        <ul id="knovelsidebar">
			<li class="knovelChem"><a href="http://tools.knovel.com/ie/?kpromoter=engineeringvillage-chemistry#browse/213" class="knovelLink" title="Chemistry and Chemical Equations">Chemistry &amp; Chemical</a></li>
			<li class="knovelElect"><a href="http://tools.knovel.com/ie/?kpromoter=engineeringvillage-electronics#browse/206" class="knovelLink" title="Electronics and Semiconductors Equations">Electronics</a></li>
			<li class="knovelEng"><a href="http://tools.knovel.com/ie/?kpromoter=engineeringvillage-engineering#browse/211" class="knovelLink" title="General Engineering Equations">General Engineering</a></li>
			<li class="knovelMech"><a href="http://tools.knovel.com/ie/?kpromoter=engineeringvillage-mechanics#browse/219" class="knovelLink" title="Mechanics and Mechanical Equations">Mechanics &amp; Mechanical</a></li>
			<li class="knovelMetal"><a href="http://tools.knovel.com/ie/?kpromoter=engineeringvillage-metals#browse/207" class="knovelLink" title="Metals and Metalurgy Equations">Metals &amp; Metallurgy</a></li>
			<li class="knovelUnit"><a href="http://tools.knovel.com/uc?kpromoter=engineeringvillage-unitconverter" class="knovelLink" title="Unit Converter">Unit Converter</a></li>
        </ul>
        </div>
    </div>
<div class="clear"></div>