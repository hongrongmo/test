<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
 <%@ taglib uri="http://jawr.net/tags" prefix="jwr" %>   
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://stripes.sourceforge.net/stripes.tld" prefix="stripes" %>
    
<stripes:layout-render name="/WEB-INF/pages/layout/standard.jsp" pageTitle="Engineering Village - Browse Index - Lookup">

    <stripes:layout-component name="csshead">
    <jwr:style src="/bundles/browseindex.css"></jwr:style>
    </stripes:layout-component>

    <stripes:layout-component name="header"/>

    <stripes:layout-component name="contents">
<div id="li_content">

<c:set var="totalcount" value="${fn:length(actionBean.lookupIndexList)}"/>

<%-- **************************************** --%>  
<%-- Top level - Navigation and form elements --%>
<%-- **************************************** --%>  
    <stripes:form id="lookupform" name="lookupform" action="/search/browseindex.url" method="POST" onsubmit="return validation()">
    <stripes:hidden name="database"/>
    <stripes:hidden name="searchtype"/>
    
    <div id="li_search_top" class="paddingL7">
        <div class="li_search_word">
<c:choose>
<c:when test="${actionBean.dynamic}">
        <label for="searchword">Search for: </label>
        <stripes:text name="searchword" title="Search Word"/>
        <stripes:submit name="find" value="Find Submit"/>
</c:when>
<c:otherwise>&nbsp;</c:otherwise>
</c:choose>
        </div>
        
        <div class="li_search_index">
        <label for="">Selected index:</label>
        <stripes:select name="lookup" title="Selected Index Dropdown">
            <c:forEach var="op" items="${actionBean.selectedIndex}">
              <option value="${op.name}" href="/search/browseindex.url?&lookup=${op.name}&database=${actionBean.database}&searchtype=${actionBean.searchtype}"<c:if test="${actionBean.lookup eq op.name}"> selected="selected"</c:if>>${op.value}</option>
            </c:forEach>
        </stripes:select>
        </div>
        <div class="clear"></div>
    </div>
    </stripes:form>

    <stripes:form name="lookup_box" action="/search/browserindex.url" method="POST">
    <stripes:hidden name="database"/>
    <stripes:hidden name="searchtype"/>
    
<c:if test="${actionBean.dynamic}">
    <div id="li_sequence" class="paddingL7 paddingT5">
	    <p>Click on letter below to browse index:</p>
	    <div id="alpha_list_main">
	    <c:if test="${actionBean.database eq '1' and actionBean.lookup eq 'AF'}">
	        <a CLASS="LgBlueLink" HREF="/search/browseindex.url?searchword=0&lookup=${actionBean.lookup}&database=${actionBean.database}&searchtype=${actionBean.searchtype}">0-9</a>
	    </c:if>
	        <a CLASS="LgBlueLink SubNav" HREF="/search/browseindex.url?searchword=A&lookup=${actionBean.lookup}&database=${actionBean.database}&searchtype=${actionBean.searchtype}">A</a>
	        <a CLASS="LgBlueLink SubNav" HREF="/search/browseindex.url?searchword=B&lookup=${actionBean.lookup}&database=${actionBean.database}&searchtype=${actionBean.searchtype}">B</a>
	        <a CLASS="LgBlueLink SubNav" HREF="/search/browseindex.url?searchword=C&lookup=${actionBean.lookup}&database=${actionBean.database}&searchtype=${actionBean.searchtype}">C</a>
	        <a CLASS="LgBlueLink SubNav" HREF="/search/browseindex.url?searchword=D&lookup=${actionBean.lookup}&database=${actionBean.database}&searchtype=${actionBean.searchtype}">D</a>
	        <a CLASS="LgBlueLink SubNav" HREF="/search/browseindex.url?searchword=E&lookup=${actionBean.lookup}&database=${actionBean.database}&searchtype=${actionBean.searchtype}">E</a>
	        <a CLASS="LgBlueLink SubNav" HREF="/search/browseindex.url?searchword=F&lookup=${actionBean.lookup}&database=${actionBean.database}&searchtype=${actionBean.searchtype}">F</a>
	        <a CLASS="LgBlueLink SubNav" HREF="/search/browseindex.url?searchword=G&lookup=${actionBean.lookup}&database=${actionBean.database}&searchtype=${actionBean.searchtype}">G</a>
	        <a CLASS="LgBlueLink SubNav" HREF="/search/browseindex.url?searchword=H&lookup=${actionBean.lookup}&database=${actionBean.database}&searchtype=${actionBean.searchtype}">H</a>
	        <a CLASS="LgBlueLink SubNav" HREF="/search/browseindex.url?searchword=I&lookup=${actionBean.lookup}&database=${actionBean.database}&searchtype=${actionBean.searchtype}">I</a>
	        <a CLASS="LgBlueLink SubNav" HREF="/search/browseindex.url?searchword=J&lookup=${actionBean.lookup}&database=${actionBean.database}&searchtype=${actionBean.searchtype}">J</a>
	        <a CLASS="LgBlueLink SubNav" HREF="/search/browseindex.url?searchword=K&lookup=${actionBean.lookup}&database=${actionBean.database}&searchtype=${actionBean.searchtype}">K</a>
	        <a CLASS="LgBlueLink SubNav" HREF="/search/browseindex.url?searchword=L&lookup=${actionBean.lookup}&database=${actionBean.database}&searchtype=${actionBean.searchtype}">L</a>
	        <a CLASS="LgBlueLink SubNav" HREF="/search/browseindex.url?searchword=M&lookup=${actionBean.lookup}&database=${actionBean.database}&searchtype=${actionBean.searchtype}">M</a>
	        <a CLASS="LgBlueLink SubNav" HREF="/search/browseindex.url?searchword=N&lookup=${actionBean.lookup}&database=${actionBean.database}&searchtype=${actionBean.searchtype}">N</a>
	        <a CLASS="LgBlueLink SubNav" HREF="/search/browseindex.url?searchword=O&lookup=${actionBean.lookup}&database=${actionBean.database}&searchtype=${actionBean.searchtype}">O</a>
	        <a CLASS="LgBlueLink SubNav" HREF="/search/browseindex.url?searchword=P&lookup=${actionBean.lookup}&database=${actionBean.database}&searchtype=${actionBean.searchtype}">P</a>
	        <a CLASS="LgBlueLink SubNav" HREF="/search/browseindex.url?searchword=Q&lookup=${actionBean.lookup}&database=${actionBean.database}&searchtype=${actionBean.searchtype}">Q</a>
	        <a CLASS="LgBlueLink SubNav" HREF="/search/browseindex.url?searchword=R&lookup=${actionBean.lookup}&database=${actionBean.database}&searchtype=${actionBean.searchtype}">R</a>
	        <a CLASS="LgBlueLink SubNav" HREF="/search/browseindex.url?searchword=S&lookup=${actionBean.lookup}&database=${actionBean.database}&searchtype=${actionBean.searchtype}">S</a>
	        <a CLASS="LgBlueLink SubNav" HREF="/search/browseindex.url?searchword=T&lookup=${actionBean.lookup}&database=${actionBean.database}&searchtype=${actionBean.searchtype}">T</a>
	        <a CLASS="LgBlueLink SubNav" HREF="/search/browseindex.url?searchword=U&lookup=${actionBean.lookup}&database=${actionBean.database}&searchtype=${actionBean.searchtype}">U</a>
	        <a CLASS="LgBlueLink SubNav" HREF="/search/browseindex.url?searchword=V&lookup=${actionBean.lookup}&database=${actionBean.database}&searchtype=${actionBean.searchtype}">V</a>
	        <a CLASS="LgBlueLink SubNav" HREF="/search/browseindex.url?searchword=W&lookup=${actionBean.lookup}&database=${actionBean.database}&searchtype=${actionBean.searchtype}">W</a>
	        <a CLASS="LgBlueLink SubNav" HREF="/search/browseindex.url?searchword=X&lookup=${actionBean.lookup}&database=${actionBean.database}&searchtype=${actionBean.searchtype}">X</a>
	        <a CLASS="LgBlueLink SubNav" HREF="/search/browseindex.url?searchword=Y&lookup=${actionBean.lookup}&database=${actionBean.database}&searchtype=${actionBean.searchtype}">Y</a>
	        <a CLASS="LgBlueLink SubNav" HREF="/search/browseindex.url?searchword=Z&lookup=${actionBean.lookup}&database=${actionBean.database}&searchtype=${actionBean.searchtype}">Z</a>
	   </div>
	   <div id="alpha_list_sub">
	   <c:forEach var="seq" items="${actionBean.sequence}">
		   <a CLASS="LgBlueLink SubNav" HREF="/search/browseindex.url?searchword=${seq}&lookup=${actionBean.lookup}&database=${actionBean.database}&searchtype=${actionBean.searchtype}">${seq}</a>
	   </c:forEach>
	   </div>
   </div>
</c:if>

    <p class="paddingL7 paddingT5">Select terms below to add to search</p>
    <div class="li_selectterms">
        <span style="float:left">
            <span><b> &nbsp; Connect terms with: </b></span>
            <input type="radio" name="lookup" value="AND" id="andChkbx" onClick="doboolean('${actionBean.searchtype}')"/>
            <label for="andChkbx">AND</label> 
            <input type="radio" name="lookup" value="OR" id="orChkbx" onClick="doboolean('${actionBean.searchtype}')" checked="checked"/>
            <label for="orChkbx">OR</label>
        </span>
        
        <span class="pagenav">
            <c:if test="${actionBean.count gt 1 }">
                <a class="previous" href="/search/browseindex.url?searchword=${actionBean.searchword}&count=${actionBean.count-1}&database=${actionBean.database}&lookup=${actionBean.lookup}&searchtype=${actionBean.searchtype}&lookupSearchID=${actionBean.lookupSearchID}">&lt; Previous page</a>
            </c:if>
            <c:if test="${(totalcount gt 0) and (totalcount eq 100 or totalcount eq 101)}">
                <a class="next" href="/search/browseindex.url?searchword=${actionBean.searchword}&count=&count=${actionBean.count+1}&database=${actionBean.database}&lookup=${actionBean.lookup}&searchtype=${actionBean.searchtype}&lookupSearchID=${actionBean.lookupSearchID}">Next page &gt;</a>
            </c:if>
        </span>
        <div class="clear"></div>
    </div>

    </stripes:form>    
    
<%-- **************************************** --%>  
<%-- LookupIndex display! --%>
<%-- **************************************** --%>  
    <div id="li_display">


<c:set var="searchwordupper" value="${fn:toUpperCase(actionBean.searchword)}"/>
<c:set var="searchwordlength" value="${fn:length(actionBean.searchword)}"/>
	<FORM id="pasteform" NAME="pastelist">
	
	<ul id="li_list">
	
<c:forEach var="lookupindex" items="${actionBean.lookupIndexList}">

<c:set var="piddescription"><c:if test="${actionBean.database eq '2' and actionBean.lookup eq 'PID'}">${lookupindex.pidDescription}</c:if></c:set>
<c:set var="lookupindexvalueupper" value="${fn:toUpperCase(lookupindex.value)}"/>
<c:set var="lookupindexvaluelength" value="${fn:length(lookupindex.value)}"/>

	    <li>	  
            <INPUT TYPE="CHECKBOX"  NAME="selectedchar" value="${lookupindex.value}"<c:if test="${not empty piddescription}"> onmouseover="this.T_WIDTH=450;return escape('${piddescription}');"</c:if>/>                    
               	
            <a class="nolink"<c:if test="${not empty piddescription}"> onmouseover="this.T_WIDTH=450;return escape('${piddescription}');"</c:if>>
               <c:choose>
               <c:when test="${fn:startsWith(lookupindexvalueupper, searchwordupper)}"><span class="substring">${fn:substring(lookupindex.value, 0, searchwordlength)}</span>${fn:substring(lookupindex.value, searchwordlength, lookupindexvaluelength)}</c:when>
               <c:otherwise>${lookupindex.value}</c:otherwise>
               </c:choose>

                <c:if test="${not empty lookupindex.databases}">
                <span class="li_db">(<c:forEach var="lookupindexdb" items="${lookupindex.databases}" varStatus="dbstatus">${lookupindexdb}<c:if test="${not dbstatus.last}">, </c:if></c:forEach>)</span>
                </c:if>
            </a>
	    </li>
</c:forEach>
<c:if test="${empty actionBean.lookupIndexList}"><li style="padding:5px 0 5px 7px">No terms found.</li></c:if>
    </ul>
    
<c:if test="${actionBean.dynamic}">
    <div class="li_selectterms">
        <span class="backtotop"><A HREF="#top">Back to Top</A></span>
        <span class="pagenav">
            <c:if test="${actionBean.count gt 1 }">
                <a class="previous" href="/search/browseindex.url?searchword=${actionBean.searchword}&count=${actionBean.count-1}&database=${actionBean.database}&lookup=${actionBean.lookup}&searchtype=${actionBean.searchtype}&lookupSearchID=${actionBean.lookupSearchID}"> Previous page</a>
            </c:if>
            <c:if test="${(totalcount gt 0) and (totalcount eq 100 or totalcount eq 101)}">
                <a class="next" href="/search/browseindex.url?searchword=${actionBean.searchword}&count=&count=${actionBean.count+1}&database=${actionBean.database}&lookup=${actionBean.lookup}&searchtype=${actionBean.searchtype}&lookupSearchID=${actionBean.lookupSearchID}">Next page &gt;</a>
            </c:if>
        </span>
        <div class="clear"></div>
    </div>
</c:if>

    </FORM>

</div>

</div>

    </stripes:layout-component>

    <stripes:layout-component name="footer"/>

    <stripes:layout-component name="jsbottom_custom">
	<jwr:script src="/bundles/browseindex.js"></jwr:script>
    </stripes:layout-component>
    
</stripes:layout-render>